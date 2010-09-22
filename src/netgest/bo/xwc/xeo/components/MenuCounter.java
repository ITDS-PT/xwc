package netgest.bo.xwc.xeo.components;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import netgest.bo.ql.QLParser;
import netgest.bo.ql.boqlParserException;
import netgest.bo.runtime.EboContext;
import netgest.bo.system.boApplication;
import netgest.bo.utils.XEOQLModifier;
import netgest.bo.xwc.components.classic.ToolBar;
import netgest.bo.xwc.components.classic.TreePanel;
import netgest.bo.xwc.components.model.Menu;
import netgest.bo.xwc.framework.XUIBindProperty;
import netgest.bo.xwc.framework.XUIRenderer;
import netgest.bo.xwc.framework.XUIRendererServlet;
import netgest.bo.xwc.framework.XUIRequestContext;
import netgest.bo.xwc.framework.XUIScriptContext;
import netgest.bo.xwc.framework.XUIViewProperty;
import netgest.bo.xwc.framework.components.XUIComponentBase;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 
 * A {@link Menu} component that renders an "Outlook style" counter
 * along side the label of the {@link MenuCounter}
 * 
 * @author jcarreira
 *
 */
public class MenuCounter extends Menu {
	
	/**
	 * A BOQL expression that supplies the counter value,
	 * Eg. 'Select Ebo_Perf where active="1"'
	 * 
	 */
	XUIBindProperty<String> boql 			= new XUIBindProperty<String>("boql", this, String.class );
	/**
	 * An SQL expression that supplies the counter value,
	 * Eg: 'Select count(*) from OEbo_Perf'
	 */
	XUIBindProperty<String> sql 			= new XUIBindProperty<String>("sql", this, String.class );
	/**
	 * Value to show in the counter, if the value is to a be fixed value, or binded
	 * to a bean property, otherwise use the <code>boql</code> or <code>sql</code>
	 * properties
	 */
	XUIBindProperty<String> counterValue	= new XUIBindProperty<String>("counterValue", this, String.class );
	/**
	 * 
	 * A mask to apply when rendering the counter value
	 * Eg: <b style='color:red'>%s</b> (where '%s' is replaced by the value of the counter)
	 * 
	 */
	XUIViewProperty<String> counterMask		= new XUIViewProperty<String>("counterMask", this, "(<span style='font-weight:normal;color:blue;'>%s</span>)" );
	/**
	 * The update interval (in seconds) for the counter
	 */
	XUIViewProperty<Integer> updateInterval	= new XUIViewProperty<Integer>("updateInterval", this, 60 );
	
	@Override
	public void initComponent() {
		XUIRequestContext reqCtx = XUIRequestContext.getCurrentContext();
        String sActionUrl = getRequestContext().getAjaxURL();
        String sPar = "javax.faces.ViewState=" + XUIRequestContext.getCurrentContext().getViewRoot().getViewState();
        sPar += "&xvw.servlet=" + getClientId();
        if( sActionUrl.indexOf("?") == -1 ) {
        	sActionUrl += "?" + sPar;
        }
        else {
        	sActionUrl += "&" + sPar;
        }
		
        
        UIComponent comp = getParent();
        while( !(comp instanceof TreePanel || comp instanceof ToolBar ) && comp != null ) {
        	comp = comp.getParent();
        }
        if( comp != null ) {
			reqCtx.getScriptContext().add( XUIScriptContext.POSITION_FOOTER , getClientId() +"_r", 
					"XVW.MenuCounter.registerCounter('" + sActionUrl + "','" + comp.getClientId(getFacesContext()) + "','" + getClientId() +  "'," + getUpdateInterval() + ");" 
				);
			
			reqCtx.getScriptContext().add( XUIScriptContext.POSITION_FOOTER , getClientId(), 
				"window.setTimeout( \"XVW.MenuCounter.updateCounter('" + getClientId() + "')\",500);" 
			);
			
        }
	}
	
	public void setBoql( String boql ) {
		this.boql.setExpressionText( boql );
	}
	
	public String getBoql() {
		return this.boql.getEvaluatedValue();
	}
	
	public void setSql( String sql ) {
		this.sql.setExpressionText( sql );
	}
	
	public String getSql() {
		return this.sql.getEvaluatedValue();
	}
	
	public void setCounterValue( String counterExpr ) {
		this.counterValue.setExpressionText( counterExpr );
	}
	
	public String getCounterValue() {
		return this.counterValue.getEvaluatedValue();
	}

	public void setCounterMask( String counterExpr ) {
		this.counterMask.setValue( counterExpr );
	}
	
	public String getCounterMask() {
		return (String)this.counterMask.getValue();
	}
	
	public void setUpdateInterval( int updateInterval ) {
		this.updateInterval.setValue( updateInterval );
	}

	
	public int getUpdateInterval() {
		return this.updateInterval.getValue();
	}
	
	
	@Override
	public String getRendererType() {
		return super.getRendererType();
	}
	
	public static void updateClientCounter( String componentId ) {
		XUIRequestContext.getCurrentContext()
			.getScriptContext()
				.add( XUIScriptContext.POSITION_FOOTER , componentId + "updCtnr", "XVW.MenuCounter.updateCounter('formMain:testeCntr')" );
	}

	public static void updateClientCounters() {
		XUIRequestContext.getCurrentContext()
			.getScriptContext()
				.add( XUIScriptContext.POSITION_FOOTER , "updAllCtnrs", "XVW.MenuCounter.updateCounters()" );
	}
	
	public static class XEOHTMLRenderer extends XUIRenderer implements XUIRendererServlet {
		
		public static final List<Object> EMPTY_ARGS = new ArrayList<Object>(0);
		
		@Override
		public void service(ServletRequest oRequest, ServletResponse oResponse, XUIComponentBase oComp) throws IOException {
			
			try {
				EboContext ctx = boApplication.currentContext().getEboContext();
				MenuCounter m = (MenuCounter)oComp;

				String counter = m.getCounterValue();
				if( counter == null ) {
					String boql = m.getBoql();
					String sql  = m.getSql();
					
					if( boql != null ) {
						QLParser qp = new QLParser();
						XEOQLModifier qm = new XEOQLModifier(boql, EMPTY_ARGS);
						qm.setFieldsPart("[count(*)]");
						sql = qp.toSql( boql , ctx);
						sql = "select count(*) from ( " + sql + " ) t";
					}
					
					if( sql == null ) {
						Object value = m.getValue();
						if( value != null && value instanceof String ) {
							JSONObject j = new JSONObject( (String)value );
							boql = j.optString("boql");
							if( boql != null ) {
								QLParser qp = new QLParser();
								sql = qp.toSql( boql , ctx);
								sql = "select count(*) from ( " + sql + " ) t";
							}
						}
					}
					
					if( sql != null ) {
						PreparedStatement 	pstm = null;
						ResultSet 			rslt = null;
						try {
							pstm = ctx.getConnectionData().prepareStatement( sql );
							rslt = pstm.executeQuery();
							if( rslt.next() ) {
								counter = rslt.getString(1);
							}
						}
						finally {
							if( rslt != null )
								rslt.close();
							if( pstm != null ) 
								pstm.close();
						}
					}
				}
				oResponse.setContentType("text/plain");
				JSONObject j = new JSONObject();
				CharArrayWriter cw = new CharArrayWriter();
				new PrintWriter( cw ).printf( m.getCounterMask() , counter );
				
				j.put( "counter" , counter );
				j.put( "counterHtml" , m.getText() + "&nbsp;" + cw.toString() );
				oResponse.getWriter().write( j.toString() );
			} catch (boqlParserException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
	}
	
}
