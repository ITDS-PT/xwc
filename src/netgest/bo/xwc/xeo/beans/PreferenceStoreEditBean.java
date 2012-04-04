package netgest.bo.xwc.xeo.beans;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import netgest.bo.runtime.boRuntimeException;
import netgest.bo.xwc.components.classic.Attribute;
import netgest.bo.xwc.components.classic.Cell;
import netgest.bo.xwc.components.classic.Row;
import netgest.bo.xwc.components.classic.Rows;
import netgest.bo.xwc.components.classic.Window;
import netgest.bo.xwc.components.classic.scripts.XVWScripts;
import netgest.bo.xwc.components.classic.scripts.XVWServerActionWaitMode;
import netgest.bo.xwc.components.model.Menu;
import netgest.bo.xwc.framework.XUIComponentPlugIn;
import netgest.bo.xwc.framework.XUIRequestContext;
import netgest.bo.xwc.framework.XUIViewBindProperty;
import netgest.bo.xwc.framework.XUIViewProperty;
import netgest.bo.xwc.framework.XUIViewStateBindProperty;
import netgest.bo.xwc.framework.XUIViewStateProperty;
import netgest.bo.xwc.framework.components.XUIViewRoot;
import netgest.bo.xwc.framework.def.XUIComponentDefinition;
import netgest.bo.xwc.framework.def.XUIComponentStore;

public class PreferenceStoreEditBean extends XEOBaseBean {
		
	private boolean			  	reloadTree = true;
	private boolean			  	reloadRows = true;
	
	private String				selectedComponent = null;
	
	private XUIComponentPlugIn	rowsPlugIn = new PropertyRowsPlugIn();
	
	public XUIComponentPlugIn getRowsPlugIn() {
		return this.rowsPlugIn;
	}
	
	private Map<String,Map<String,ComponentProperty>> componentProperties = 
		new LinkedHashMap<String, Map<String,ComponentProperty>>();			
	
	public boolean getReloadTree() {
		boolean ret =this.reloadTree;
		this.reloadTree = false;
		return ret;
	}
	
	public void setReloadTree( boolean reloadTree ) {
		this.reloadTree = reloadTree;
	}
	
	public void selectComponent() {
		XUIRequestContext oRequestContext;
		oRequestContext = XUIRequestContext.getCurrentContext();
		
		Menu m = (Menu)oRequestContext.getEvent().getComponent();
		String newComponent = m.getValue().toString();
		
		if( !newComponent.equals( selectedComponent ) ) {
			reloadRows = true;
			selectedComponent = newComponent;
		}
		
	}
	
	public String getSegurancasTitle() {
		return "Propriedades";
	}
	
	public Menu getTree() {
		Menu root;
		
		root = new Menu();
		root.setText( "Componentes" );
		try {
			buildTree( root );
		} catch (boRuntimeException e) {
			e.printStackTrace();
		}
		return root;
	}
	
	private void buildTree( Menu root ) throws boRuntimeException {
		buildNode( this.componentProperties, root );
	}
	
	private void buildNode( Map<String,Map<String,ComponentProperty>> components, Menu parentMenu ) {
		Map<String,Object> menuMap = new HashMap<String, Object>();
		for( Entry<String,Map<String,ComponentProperty>> entry : this.componentProperties.entrySet() ) {
			String key = entry.getKey();
			String ns = entry.getKey().substring( 0, key.indexOf(':') );
			if( !menuMap.containsKey( ns ) ) {
				menuMap.put( ns , Boolean.TRUE );
				Menu menu = new Menu();
				menu.setText( ns );
				menu.setServerActionWaitMode( XVWServerActionWaitMode.NONE.toString() );
				parentMenu.getChildren().add( menu );
				buildNodeNS(ns, components, menu);
			}
		}
	}

	private void buildNodeNS( String ns, Map<String,Map<String,ComponentProperty>> components, Menu parentMenu ) {
		for( Entry<String,Map<String,ComponentProperty>> entry : this.componentProperties.entrySet() ) {
			String key = entry.getKey();
			if( key.startsWith( ns ) ) {
				Menu m = new Menu();
				m.setText( key );
				m.setValue( key );
				m.setServerAction("#{" + getId() + ".selectComponent}");
				parentMenu.getChildren().add( m );
			}
		}
	}
	
	public boolean getShowComponentTree() {
		return true;
	}
	
	public void canCloseTab() {
		XUIRequestContext oRequestContext = XUIRequestContext.getCurrentContext();
		XUIViewRoot viewRoot = oRequestContext.getViewRoot();
		Window xWnd = (Window)viewRoot.findComponent(Window.class);
		if( xWnd != null ) {
			if( xWnd.getOnClose() != null ) {
				xWnd.getOnClose().invoke( oRequestContext.getELContext(), null);
            }
		}
		XVWScripts.closeView( viewRoot );
		oRequestContext.getViewRoot().setRendered( false );
		oRequestContext.renderResponse();
	}
	
	public void loadComponentsMap() {
		try {
			
			XUIRequestContext oRequestContext = XUIRequestContext.getCurrentContext();
			XUIComponentStore componentStore = oRequestContext.getApplicationContext().getComponentStore();
			
			String[] componentNames = componentStore.getComponentNames();
			Arrays.sort( componentNames );
			for( String componentName : componentNames ) {

				XUIComponentDefinition comp = componentStore.getComponent( componentName );
				String className = comp.getClassName();
				
				try {
					Class<?>  componentClass = Class.forName( className );
					List<Field> fields = new ArrayList<Field>();
					
					Class<?>  superClass = componentClass.getSuperclass();
					while( superClass != null ) {
						fields.addAll( Arrays.asList( superClass.getDeclaredFields() ) );
						superClass = superClass.getSuperclass();
					}
					
					Field[] fieldsSorted = fields.toArray( new Field[0] );
					Arrays.sort( fieldsSorted, 
							new Comparator<Field>() {
								public int compare(Field o1, Field o2) {
									return o1.getName().toUpperCase().compareTo( o2.getName().toUpperCase() );
								};
							}
					);
					
					for( Field field : fieldsSorted ) {
						Class<?> type = field.getType();
						if( 
							type == XUIViewProperty.class ||
							type == XUIViewStateProperty.class ||
							type == XUIViewBindProperty.class || 
							type == XUIViewStateBindProperty.class 
						) {
							
							Map<String, ComponentProperty> compPropMap = 
								this.componentProperties.get( componentName );
							
							if( compPropMap == null ) {
								compPropMap = new LinkedHashMap<String, ComponentProperty>();
								this.componentProperties.put( componentName, compPropMap );
							}
							
							ParameterizedType genericType 	= (ParameterizedType)field.getGenericType();
							
							Type valueType 		= genericType.getActualTypeArguments()[0];
							
							compPropMap.put( field.getName(), 
								new ComponentProperty(
										field.getName(), 
										true, 
										type,
										valueType,
										null
								) 
							);
							System.out.println( comp.getName() + " : " + field.getName() + " : " + type );
						}
					}
				}
				catch ( ClassNotFoundException e ) {
					
				}
				
			}
			setReloadTree( true );
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static class ComponentProperty {
		String		propertyName;
		boolean		active;
		Type		type;
		Type		valueType;
		Object		value;
		
		public ComponentProperty( String propertyName, boolean active, Type type, Type valueType, Object value ) {
			this.propertyName 	= propertyName;
			this.active			= active;
			this.valueType		= valueType;
			this.type			= type;
			this.value			= value;
		}
		
		public void setValue( Object newValue ) {
			this.value = newValue;
		}
		
		public Object getValue() {
			return this.value;
		}
		
		public void setActive( boolean active ) {
			this.active = active;
		}
		
		public boolean getActive() {
			return this.active;
		}
	}
	
	public class PropertyRowsPlugIn extends XUIComponentPlugIn {
		@Override
		public void beforePreRender() {
			Rows rows = (Rows)getComponent();
			rows.setLabelWidth(200);
			
			if( reloadRows && selectedComponent != null ) {
				
				
				rows.getChildren().clear();
				rows.forceRenderOnClient();
				for ( Entry<String,ComponentProperty> prop : componentProperties.get( selectedComponent ).entrySet() ) {
					Row row = new Row();
					Cell cell = new Cell();
					Attribute att = new Attribute();
					att.setLabel( prop.getValue().propertyName );
					
					ComponentProperty compProp = prop.getValue();
					
					String inputType = "attributeText";
					
					if( compProp.valueType == Boolean.class ) {
						inputType = "attributeBoolean";
					}
					else if( compProp.valueType == Boolean.class ) {
						inputType = "attributeBoolean";
					}
					att.setInputType( inputType );
					att.setMaxLength( 100 );
					att.setId( prop.getValue().propertyName );
					cell.getChildren().add( att );
					row.getChildren().add( cell );
					row.getChildren().add( new Cell() );
					rows.getChildren().add( row );
					att.initComponent();
				}
			}
			
		}
		@Override
		public void afterPreRender() {
			reloadRows = false;
		}
	}
	
}
