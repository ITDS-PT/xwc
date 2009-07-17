package netgest.bo.xwc.components.security;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import javax.faces.component.UIComponent;

import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boBridgeIterator;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boObjectList;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.xwc.framework.XUISessionContext;
import netgest.bo.xwc.framework.components.XUIViewRoot;

import org.apache.log4j.Logger;

public class ViewerAccessPolicyBuilder {

	private static Logger logger = Logger.getLogger( ViewerAccessPolicyBuilder.class.getName() );

	/** Controls the use of viewer-defined securities */
	public static boolean applyViewerSecurity = true;
	
	/** Component path => Node id */
	private Map<String, String> accessPolicyByPath; 
	/** InstanceId => Id */
	private Map<String, String> idByComponent;
	
	/** Components processed */
	private int nrComponents;
	/** Components that allow security policies */
	private int nrComponentsStored;
	
	/** iXeoUser bridges that may define security policies */
	private static Map<String,String> bridges;

	/** Instance counter for the viewer */
	private AtomicLong instanceCounter;
	
	/** 
	 * Processes the registered viewers and build persistent XVWAccessPolicy instances
	 * @param context
	 * @param sessionContext
	 * @throws boRuntimeException
	 */
	public void buildAccessPolicy( EboContext context, XUISessionContext sessionContext ) throws boRuntimeException {
		boolean commit = false;

		List<String> viewerNames = getRegisteredViewers();

		try {
			context.setModeBatch();
			
			try {
				context.beginContainerTransaction();

				cleanAccessPolicy(context);
				
				// Mark all items as unused
				markAllAsUnreferenced(context);

				for (String viewer : viewerNames) {
					processViewer( viewer, context, sessionContext, true );
				}
				
				commit = true;
			} catch ( Exception e ) {
				logger.error( "buildAccessPolicy:"+e );
				commit = false;
				throw new boRuntimeException( "", "", e );
			} finally {
				if ( commit ) {
					context.commitContainerTransaction();
				} else {
					context.rollbackContainerTransaction();
				}
			}
			
		} finally {
			context.setModeBatchOff();
			context.releaseObjects();
		}
		
		logger.info("Done");
	}

	/**
	 * Process the given viewer
	 * @param viewer
	 * @param context
	 * @param sessionContext
	 * @param buildObjects
	 * @throws boRuntimeException
	 */
	private void processViewer( String viewer, EboContext context, XUISessionContext sessionContext, boolean buildObjects ) throws boRuntimeException {
		XUIViewRoot viewRoot = sessionContext.createView( viewer );
		processViewer( viewRoot, context, buildObjects );
	}
	
	/**
	 * Process the given viewer
	 * @param viewRoot
	 * @param context
	 * @param buildObjects
	 * @throws boRuntimeException
	 */
	public void processViewer( XUIViewRoot viewRoot, EboContext context, boolean buildObjects ) throws boRuntimeException {
		String viewerName = viewRoot.getViewId();

		accessPolicyByPath = new HashMap<String, String>();
		idByComponent = new HashMap<String, String>();
		instanceCounter = new AtomicLong(0);
		nrComponents = 0;
		nrComponentsStored = 0;
		
		for ( UIComponent component : viewRoot.getChildren() ) {
			processComponent( viewerName, context, component, null, buildObjects );
		}
	}

	/**
	 * Returns a list of application-specific viewers that support security policies 
	 * @return
	 * @throws boRuntimeException
	 */
	public static List<String> getRegisteredViewers() throws boRuntimeException {
		List<String> registedViewers = new ArrayList<String>();
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		InputStream inputStream = classLoader.getResourceAsStream("netgest/bo/xwc/components/security/RegisteredViewers.txt");
		BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream) );
		String line;
		
		try {
			while( (line=bufferedReader.readLine())!=null ) {
				registedViewers.add(line.trim());
			}
		} catch (IOException e) {
			throw new boRuntimeException("","",e);
		} finally {
			try {
				if (bufferedReader!=null) bufferedReader.close();
				if (inputStream!=null) inputStream.close();
			} catch (IOException e) {
				throw new boRuntimeException("","",e);
			}
		}

		return registedViewers;
	}
	
	/**
	 * Processes a given component
	 * @param context
	 * @param id
	 * @param viewerName
	 * @param componentType
	 * @param label
	 * @param containerBoui
	 * @return
	 * @throws boRuntimeException
	 */
	private boObject buildAccessPolicyForComponent( EboContext context, String id, String viewerName, String componentType, String label,
			Long containerBoui, String childViewers ) throws boRuntimeException {
		boObject accessPolicy = 
			boObject.getBoManager().loadObject( context, "XVWAccessPolicy", "id=?", new Object[]{ id } );
		
		accessPolicy.getAttribute("id").setValueString( id );
		accessPolicy.getAttribute("viewer").setValueString( viewerName );
		accessPolicy.getAttribute("componentType").setValueString( componentType );
		accessPolicy.getAttribute("label").setValueString( label );
		accessPolicy.getAttribute("referenced").setValueString( "1" );
		if ( containerBoui!=null ) {
			accessPolicy.getAttribute("container").setValueLong( containerBoui );
		}
		accessPolicy.getAttribute("childViewers").setValueString( childViewers );
		
		return accessPolicy;
	}
	
	/**
	 * Processes a given component
	 * @param viewerName
	 * @param context
	 * @param component
	 * @param containerBoui
	 * @param buildObjects
	 * @throws boRuntimeException
	 */
	private void processComponent( String viewerName, EboContext context, UIComponent component, Long containerBoui, boolean buildObjects ) throws boRuntimeException {
		boolean isContainer = false;
		Long parentBoui = containerBoui;
		nrComponents++;
		
		// Process the component
		String id = null, componentType = null, label = null;
		StringBuffer stringBufferId = new StringBuffer();
		String componentPath = null, componentPathWithIds = null, childViewers = null;
		
	 	if (component instanceof SecurableComponent) {
	 		SecurableComponent securableComponent = (SecurableComponent)component;
	 		
	 		isContainer = securableComponent.isContainer();
	 		childViewers = securableComponent.getChildViewers();
	 		
	 		if ( securableComponent.getViewerSecurityId()!=null ) {
	 			stringBufferId.append(securableComponent.getViewerSecurityId());
	 		}
	 		componentType = securableComponent.getViewerSecurityComponentType().toString();
	 		label = securableComponent.getViewerSecurityLabel();

	 		componentPath = getComponentPath( component );
			componentPathWithIds = addIdsToPath(componentPath);
			if (stringBufferId.length()>0) {
				id = stringBufferId.toString();
				accessPolicyByPath.put( componentPath, id );
				id = componentPathWithIds.replaceAll( "\\[\\d+\\]$", "[@id="+id+"]" );
			} else {
				accessPolicyByPath.put( componentPath, null );
				id = componentPathWithIds;
			}
			// Add the viewer name to the component
			id = viewerName+id;
			
			String instanceId = "instanceId"+instanceCounter.addAndGet(1);
			securableComponent.setInstanceId(instanceId);
			idByComponent.put( instanceId, id );
			
			if ( buildObjects ) {
				boObject accessPolicy = buildAccessPolicyForComponent( context, id, viewerName, componentType, label, containerBoui, childViewers );
				accessPolicy.update();				
				if ( isContainer ) {
					parentBoui = accessPolicy.getBoui();
				} 
			}
			
			nrComponentsStored++;			
		}
		
		// Process the children
		for ( UIComponent childComponent : component.getChildren() ) {
			processComponent( viewerName, context, childComponent, parentBoui, buildObjects );
		}

	}
	
	/**
	 * Returns an map matching the instance id for the component and the component id
	 * @return
	 */
	public Map<String, String> getIdsByComponent() {
		if ( idByComponent!=null ) return idByComponent;
		throw new RuntimeException( "Id by Component not initialized!" );
	}
	
	/**
	 * Removes "#{","}" and "."s from el expressions
	 * @param elExpression
	 * @return
	 */
	public static String cleanElExpression( String elExpression ) {
		String resultingExpression = elExpression.replaceAll( "#\\{", "" );
		resultingExpression = resultingExpression.replaceAll( "\\}", "" );
		resultingExpression = resultingExpression.replaceAll("\\.", "");
		return resultingExpression;
	}

	/**
	 * Adds componentIds to a component path
	 * form[1]/panel[1] and form[1] => 'mainForm' becomes form[@id=mainForm]/panel[1]
	 * @param componentPath
	 * @return
	 */
	private String addIdsToPath( String componentPath ) {
		String componentId, path = "", pathWithId = "";

		// Get all existing components Id by xpath
		String[] individualComponents = componentPath.split("/");
		for( int compIndex=0; compIndex<individualComponents.length; compIndex++ ) {
			if ( individualComponents[compIndex]!=null && individualComponents[compIndex].length()>0 ) {
				path += "/"+individualComponents[compIndex];
				pathWithId += "/"+individualComponents[compIndex];
				componentId = accessPolicyByPath.get( path );
				if ( componentId!=null && componentId.length()>0 ) {
					pathWithId = pathWithId.replaceAll( "\\[\\d+\\]$", "[@id="+componentId+"]" );
				} 
			}
		}
		
		return pathWithId;
	}

	/**
	 * Returns an xpath for the component
	 * @param component
	 * @return
	 */
	private String getComponentPath( UIComponent component ) {
		int index;
		StringBuffer sb = new StringBuffer();
		UIComponent parentComponent = component.getParent();
		UIComponent currentComponent = component;
		
		while( parentComponent!=null ) {
			index = getChildrenByType(parentComponent, currentComponent.getClass() ).indexOf(currentComponent)+1;
			sb.insert( 0, "["+index+"]" );
			sb.insert( 0, currentComponent.getRendererType() );
			sb.insert( 0, "/" );
			currentComponent = parentComponent;
			parentComponent = parentComponent.getParent();
		}

		return sb.toString();
	}
	
	/**
	 * Returns a list of descending components filtered by class
	 * @param component
	 * @param uiClass
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<UIComponent> getChildrenByType( UIComponent component, Class uiClass ) {
		List<UIComponent> childrenByType = new ArrayList<UIComponent>();
		
		Iterator<UIComponent> iterator = component.getChildren().iterator();
		while( iterator.hasNext() ) {
			UIComponent iterComponent = iterator.next();
			if ( uiClass.isInstance(iterComponent) ) {
				childrenByType.add(iterComponent);
			}
		}
		
		return childrenByType;
	}

	/**
	 * Returns the list of persistent policies for the given viewer
	 * @param context
	 * @param viewerName
	 * @param xeoUserObj
	 * @return
	 * @throws boRuntimeException
	 */
	public Map<String,Byte> getPoliciesByViewer( EboContext context, String viewerName, boObject xeoUserObj ) throws boRuntimeException {
		Map<String,Byte> policiesByViewer = new HashMap<String, Byte>();
		// SELECT XVWAccessPolicy.policyDetails WHERE policyDetails.boui in () and referenced='1' AND viewer=? order by policyDetails.securityLevel
		//StringBuffer sb = new StringBuffer( "SELECT XVWAccessPolicy.policyDetails WHERE policyDetails.object.BOUI in (" );
		StringBuffer sb = new StringBuffer( "SELECT XVWAccessPolicy.policyDetails WHERE policyDetails.object in (" );
		boObjectList accessPolicyList = null;
		boObject policyDetail = null;
		String componentId = null;
		
		if ( bridges==null ) {
			bridges = new HashMap<String, String>();
			bridges.put( "workQueue", "queues" );
			bridges.put( "Ebo_Group", "groups" );
			bridges.put( "Ebo_Role", "roles" );
			bridges.put( "uiProfile", "iProfile" );
		}
		
		sb.append( xeoUserObj.getBoui() );

		for (String bridge : bridges.keySet()) {
			boBridgeIterator iterator = xeoUserObj.getBridge( bridges.get(bridge) ).iterator();
			iterator.beforeFirst();
			while( iterator.next() ){
				sb.append( "," );
				sb.append( iterator.currentRow().getObject().getBoui() );
			}
		}
		sb.append( ") and referenced='1' AND viewer=? order by policyDetails.PARENT, policyDetails.accessLevel" );
		
		accessPolicyList = boObjectList.list( context, sb.toString(),
				new Object[]{ viewerName },
				1, 999999, "", false );
		accessPolicyList.beforeFirst();
		while( accessPolicyList.next() ) {
			policyDetail = accessPolicyList.getObject();
			componentId = policyDetail.getParent().getAttribute("id").getValueString();
			// Get the most restrictive policy
			if ( !policiesByViewer.containsKey(componentId) ) {
				policiesByViewer.put( componentId, 
						Byte.valueOf(policyDetail.getAttribute("accessLevel").getValueString()) );				
			}
		}
		
		return policiesByViewer;
	}

	/**
	 * Marks all persistent policies as not referenced
	 * @param context
	 * @throws boRuntimeException
	 */
	private void markAllAsUnreferenced( EboContext context ) throws boRuntimeException {
		boObject accessPolicy;
		boObjectList accessPolicyList = boObjectList.list( context, "SELECT XVWAccessPolicy", 1, 999999 );
		accessPolicyList.beforeFirst();
		while( accessPolicyList.next() ) {
			accessPolicy = accessPolicyList.getObject();
			accessPolicy.getAttribute("referenced").setValueString("0");
			accessPolicy.update();
		}
	}
	
	/**
	 * Removes all persistent policies
	 * @param context
	 * @throws boRuntimeException
	 */
	private void cleanAccessPolicy( EboContext context ) throws boRuntimeException {
		// Get the children
		boObjectList accessPolicyList = boObjectList.list( context, "SELECT XVWAccessPolicy WHERE container is not null", 1, 999999 );
		accessPolicyList.beforeFirst();
		while( accessPolicyList.next() ) {
			accessPolicyList.getObject().destroyForce();
		}
		// Then the parent
		accessPolicyList = boObjectList.list( context, "SELECT XVWAccessPolicy WHERE container is null", 1, 999999 );
		accessPolicyList.beforeFirst();
		while( accessPolicyList.next() ) {
			accessPolicyList.getObject().destroyForce();
		}		
	}
	
}
