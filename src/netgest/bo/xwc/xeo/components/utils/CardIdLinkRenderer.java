package netgest.bo.xwc.xeo.components.utils;

import netgest.bo.def.boDefAttribute;
import netgest.bo.runtime.AttributeHandler;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.runtime.bridgeHandler;
import netgest.bo.xwc.components.classic.GridColumnRenderer;
import netgest.bo.xwc.components.classic.GridPanel;
import netgest.bo.xwc.components.connectors.DataFieldConnector;
import netgest.bo.xwc.components.connectors.DataRecordConnector;
import netgest.bo.xwc.components.connectors.XEOObjectAttributeConnector;
import netgest.bo.xwc.xeo.components.ColumnAttribute;

/**
 * Column Renderer to create links with the CardId of the object(s) to display in the 
 * GridPanel
 *
 */
public class CardIdLinkRenderer implements GridColumnRenderer {


	private ColumnAttribute column;
	
	public  CardIdLinkRenderer(ColumnAttribute col){
		this.column = col;
	}
	
	@Override
	public String render(GridPanel grid, DataRecordConnector record,
			DataFieldConnector field) {
		
		String sRetValue = "";
		try{
			
			if (field instanceof XEOObjectAttributeConnector){
				XEOObjectAttributeConnector connector = (XEOObjectAttributeConnector) field;
				AttributeHandler oAttHandler = connector.getAttributeHandler();
				if ( oAttHandler.getDefAttribute().getAtributeType() 
						== boDefAttribute.TYPE_OBJECTATTRIBUTE ) {
					
					if (oAttHandler.getDefAttribute().getReferencedObjectDef().getBoCanBeOrphan()){
					
						if (boDefAttribute.ATTRIBUTE_OBJECTCOLLECTION.
								equals(oAttHandler.getDefAttribute().getAtributeDeclaredType())){
							bridgeHandler currentBridge = oAttHandler.getParent().
								getBridge(oAttHandler.getDefAttribute().getName());
							sRetValue = createCardIdLinkForBridge(currentBridge);
						}
						else{
							boObject obj = oAttHandler.getObject();
							if( obj != null ) {
								sRetValue = createCardIdLinkForObject(obj);
							}
						}
					} else {
						return field.getDisplayValue();
					}
					
				} 
			}
		}catch (boRuntimeException e ){
			
		}
		return sRetValue;
	}

	
	/**
	 * 
	 * Generates a cardIdLink for a single object
	 * 
	 * @param objectToDisplayCardId The object to cardIdlink-'ify'
	 * 
	 * @return A String with the inovcation 
	 * 
	 * @throws boRuntimeException
	 */
	private String createCardIdLinkForObject(boObject objectToDisplayCardId) throws boRuntimeException {
		String objectCardId = objectToDisplayCardId.getTextCARDID().toString();
		String objectName = objectToDisplayCardId.getName();
		String formId = column.getNamingContainerId();
		
		StringBuilder result = new StringBuilder(50);
		//Creating an invocation to openCardIdLink(boui, formId, gridId);
		
		result.append("<a href='javascript:void(0)' onclick=\"XVW.AjaxCommand('").append(formId).append("','");
		result.append(column.getClientId()).append("_cardIdLink',").append(objectToDisplayCardId.getBoui());
		result.append(",1)\">").append("<img src='resources/").append(objectName).append("/ico16.gif' />").append(objectCardId).append("</a>");
		return result.toString();
	}
	
	/**
	 * 
	 * 
	 * Generates a string with all objects in a bridge with respective 
	 * javascript to open the edit viewer
	 * 
	 * @param bridgeToDisplay The bridge to retrieve the objects from 
	 * 
	 * @return
	 */
	private String createCardIdLinkForBridge(bridgeHandler bridgeToDisplay){
		StringBuilder sb = new StringBuilder(200);
		bridgeToDisplay.beforeFirst();
		String append = "";
		while (bridgeToDisplay.next()) {
            try {
            	 	sb.append(append);
            	 	boObject currentElementInBridge = bridgeToDisplay.getObject();
            	 	String objectName = currentElementInBridge.getName();
            		String formId = column.getNamingContainerId();
             		
             		sb.append("<a href='javascript:void(0)' onclick=\"XVW.AjaxCommand('").append(formId).append("','");
             		sb.append(column.getClientId()).append("_cardIdLink',").append(currentElementInBridge.getBoui());
             		sb.append(",1)\">").append("<img src='resources/").append(objectName).append("/ico16.gif' />").append(currentElementInBridge.getTextCARDID()).append("</a>");
            		append = ", ";
            } catch (boRuntimeException e) {
            	e.printStackTrace();
            }
        }
        return sb.toString();
	}

	@Override
	public Object clone(){
		throw new RuntimeException("Cannot clone CardIdLinkRenderer");
	}
	
}
