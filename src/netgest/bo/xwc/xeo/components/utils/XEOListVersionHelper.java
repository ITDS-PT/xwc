package netgest.bo.xwc.xeo.components.utils;

import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boFlashBackHandler;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boObjectList;
import netgest.bo.system.Logger;
import netgest.bo.utils.GridFlashBack;
import netgest.bo.utils.ObjectAttributeValuePair;
import netgest.bo.utils.ObjectDifference;
import netgest.bo.utils.RowGridFlashBack;
import netgest.bo.utils.boVersioning;
import netgest.bo.xwc.components.HTMLTag;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.xeo.components.FormEdit;
import netgest.bo.xwc.xeo.localization.BeansMessages;
import netgest.bo.xwc.xeo.localization.XEOViewersMessages;
import netgest.utils.ngtXMLUtils;
import oracle.xml.parser.v2.XMLDocument;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * 
 * A utility class to handle operations regarding the Listing of versions
 * in a form edit component
 * 
 * @author Pedro Pereira
 *
 */
public class XEOListVersionHelper 
{
	
	public static final Logger log = Logger.getLogger( XEOListVersionHelper.class.getName() );
	
	public XEOListVersionHelper()
	{}
	
	/**
	 * 
	 * 
	 * Updates the XML tree of a component to include the information about non-saved changes so that
	 * when the XSLT to display the list of differences is applied all the required information is there.
	 * It adds new rows bellow fields that were changed and also adds them to the beginning of the form
	 * for the difference summary. For the grid panel it adds new lines for the values that were added
	 * also for the deleted, repeats the table with the added/deleted lines in the beginning of the form
	 * 
	 * @param doc The XML document to modify
	 * @param values The list of attribute differences
	 * @param diffBridges The list of bridge differences
	 * @param differences The XML element where to append the tables for added/removed columns
	 * 
	 */
	public static void updateXMLTreeWithAttributeDifferences(
			XMLDocument doc, 
			HashMap<String, ObjectAttributeValuePair> values, 
			HashMap<String,GridFlashBack> diffBridges, 
			Element differences)
	{
		
		XEOListVersionHelper versionHelper = new XEOListVersionHelper();
		
		Element rootElem  = doc.getDocumentElement();
		//Fetch all "row" elements
		NodeList rows = rootElem.getElementsByTagName("row");
		//Iterate through all rows and fetch, to see if the have
		int rowLength = rows.getLength();
		for (int i = 0; i < rowLength; i++)
		{
			Node currentRow = rows.item(i);
			//Get all Cells from the row and iterate through them
			NodeList cells = currentRow.getChildNodes();
			int numCells = cells.getLength();
			boolean added = false;
			CellPositionValue[] listOfCells = new CellPositionValue[numCells];
			for (int k = 0; k < numCells; k++)
			{
				Node currentRowCell = cells.item(k); //Check if there are children
				if (currentRowCell.getNodeName().equalsIgnoreCase("cell"))
				{
					String colSpan = ((Element) currentRowCell).getAttribute("colSpan");
					Integer colSpanNumber = 0;
					if (colSpan != null)
						colSpanNumber = Integer.valueOf(colSpan);
					
					NodeList cellNodes = currentRowCell.getChildNodes();
					int numCellNodes = cellNodes.getLength();
					for (int m = 0; m < numCellNodes; m++)
					{
						Node currentCellNode = cellNodes.item(m);
						//Check if we have an attributeLabel and if the name matches one of the attributes
						//changed, mark it
						if (currentCellNode.getNodeName().equalsIgnoreCase("attributeLabel"))
						{
							Element attributeLabel = (Element) currentCellNode;
							String attName = attributeLabel.getAttribute("text");
							if (values.containsKey(attName))
							{
								added = true;
								listOfCells[k] = 
									versionHelper.new CellPositionValue(values.get(attName).getOldVal(),colSpanNumber);
							}
						} //If we don't have an attribute
						else if (currentCellNode.getNodeName().equalsIgnoreCase("attribute")){
							Element attribute = (Element)  currentCellNode;
							NodeList attributeChildren = attribute.getChildNodes();
							int numChildAttribute = attributeChildren.getLength();
							for (int l = 0; l < numChildAttribute ; l++){
								Node currentAttributeChild = attributeChildren.item(l);
								if (currentAttributeChild.getNodeName().equalsIgnoreCase("attributeLabel")){
									Element attributeLabel = (Element) currentAttributeChild;
									String attName = attributeLabel.getAttribute("text");
									if (values.containsKey(attName))
									{
										added = true;
										listOfCells[k] = 
											versionHelper.new CellPositionValue(values.get(attName).getOldVal(),colSpanNumber);
									}
								}
							}
						}
					}
				}
			}
			//Produce an alternative row with the values
			if (added)
			{
				Node newRow = doc.createElement("row");
				Element newRowElem = (Element) newRow;
				newRowElem.setAttribute("type", "old");
				
				for (int n = 0; n < numCells; n++)
				{
					Node cellOfRow = doc.createElement("cell");
					Node attribute = doc.createElement("attribute");
					if (listOfCells[n] != null)
					{
						CellPositionValue cellCurrent = listOfCells[n];
						Element label = doc.createElement("attributeLabel");
						label.setAttribute("text","");
						Element value = doc.createElement("attributeText");
						value.setTextContent(cellCurrent.getValue());
						attribute.appendChild(label);
						attribute.appendChild(value);
						
						if (cellCurrent.getColSpan() > 0)
							((Element) cellOfRow).setAttribute("colSpan", String.valueOf(cellCurrent.getColSpan()));
					}
					else
					{
						Node label = doc.createElement("attributeLabel");
						((Element) label).setAttribute("text","");
						Node value = doc.createElement("attributeLabel");
						((Element) value).setAttribute("text","");
						attribute.appendChild(label);
						attribute.appendChild(value);
					}
					cellOfRow.appendChild(attribute);
					newRow.appendChild(cellOfRow);
				}
				
				if (currentRow.getNextSibling() != null)
				{
					Element node = (Element) currentRow;
					node.getParentNode().insertBefore(newRow, currentRow.getNextSibling());
				}
				else
				{
					currentRow.getParentNode().appendChild(newRow);
				}
			}
		}
			
			//Deal with all the grid panels
			NodeList gridPanels = doc.getElementsByTagName("gridPanel");
			
			//Iterate through each grid panel
			for (int k = 0; k < gridPanels.getLength(); k++)
			{
				Element currentPanel = (Element)gridPanels.item(k);
				String bridgeName = currentPanel.getAttribute("bridgeName");
				
				//The new panel to add at the top of the form (to see bridge differences)
				Element newGridPanel = doc.createElement("gridPanelTop");
				Element newGridHeaderRow = doc.createElement("gridTopheaderrow");
				
				if (diffBridges.containsKey(bridgeName))
				{
					//Process all header columns so that we know which ones are being displayed
					List<String> columnNames = new LinkedList<String>();
					NodeList headerColList = currentPanel.getElementsByTagName("gridheadercolumn");
					
					if (diffBridges.get(bridgeName).getAddedRows().size() > 0
					 || diffBridges.get(bridgeName).getDeletedRows().size() > 0		
					)
					{
						for (int l = 0; l < headerColList.getLength(); l++)
						{
							Element colum = (Element) headerColList.item(l);
							String colName = colum.getAttribute("datafield"); 
							columnNames.add(colName);
							
							//Deal with the top table
							Element gridHeaderCollumn = doc.createElement("gridTopheadercolumn");
							gridHeaderCollumn.setTextContent(colum.getTextContent());
							newGridHeaderRow.appendChild(gridHeaderCollumn);
						}
					}
					
					//Top table column headers
					newGridPanel.appendChild(newGridHeaderRow);
					
					//Deal with the added rows
					GridFlashBack gridPreviousValues = diffBridges.get(bridgeName);
					List<RowGridFlashBack> listAdded = gridPreviousValues.getAddedRows();
					Iterator<RowGridFlashBack> itAdded = listAdded.iterator();
					while (itAdded.hasNext()) 
					{
						RowGridFlashBack rowGridFlashBack = itAdded.next();
						Element newGridRow = doc.createElement("gridrow");
						newGridRow.setAttribute("type", "listrowNew");
							
						//Top table new row
						Element newGridTopTableRow = doc.createElement("gridToprow");
						newGridTopTableRow.setAttribute("type", "listrowNew");
						
						Iterator<String> itColumns = columnNames.iterator();
						while (itColumns.hasNext()) 
						{
							String colName = (String) itColumns.next();
							Element newGridColumn = doc.createElement("gridcolumn");
							newGridColumn.setTextContent(rowGridFlashBack.getRowValue(colName));
							newGridRow.appendChild(newGridColumn);
							
							//Deal with the top table 
							Element newTopTableGridColumn = doc.createElement("gridTopcolumn");
							newTopTableGridColumn.setTextContent(rowGridFlashBack.getRowValue(colName));
							newGridTopTableRow.appendChild(newTopTableGridColumn);
						}
						
						//Add the line to the top table
						newGridPanel.appendChild(newGridTopTableRow);
						
						currentPanel.appendChild(newGridRow);
					}
					
					//Deal with the deleted rows
					List<RowGridFlashBack> listDeleted = gridPreviousValues.getDeletedRows();
					Iterator<RowGridFlashBack> itDeleted = listDeleted.iterator();
					while (itDeleted.hasNext()) 
					{
						RowGridFlashBack rowGridFlashBack = itDeleted.next();
						Element newGridRow = doc.createElement("gridToprow");
						newGridRow.setAttribute("type", "listrowOld");
							
						//Top table new row
						Element newGridTopTableRow = doc.createElement("gridToprow");
						newGridTopTableRow.setAttribute("type", "listrowOld");
						
						Iterator<String> itColumns = columnNames.iterator();
						while (itColumns.hasNext()) 
						{
							String colName = (String) itColumns.next();
							Element newGridColumn = doc.createElement("gridTopcolumn");
							newGridColumn.setTextContent(rowGridFlashBack.getRowValue(colName));
							newGridRow.appendChild(newGridColumn);
							
							//Keep the value for later use
							Element newTopTableGridColumn = doc.createElement("gridTopcolumn");
							newTopTableGridColumn.setTextContent(rowGridFlashBack.getRowValue(colName));
							newGridTopTableRow.appendChild(newTopTableGridColumn);
						}
						currentPanel.appendChild(newGridRow);
						newGridPanel.appendChild(newGridTopTableRow);
					}
					
					//Create a XML Table definition
					differences.appendChild(newGridPanel);
				}
				
			
			}
		
	}
	
	/**
     * 
     * Produces a list of Logs of a given Object as a HTML Table
     * 
     * @param bouiOfObjectLogs The BOUI of the Object
     * @param component The formEdit component
     * 
     * @return A HTML table with the list of Logs
     */
    public static String getListOfLogsObject(long bouiOfObjectLogs, FormEdit component)
    {
    	String			boql = "SELECT Ebo_Log where parent = " + bouiOfObjectLogs;
    	EboContext		ctx = component.getTargetObject().getEboContext();
    	
    	boObjectList 	listOfLogs = boObjectList.list(ctx, boql);
    	listOfLogs.beforeFirst();
    	
    	StringWriter writer = new StringWriter();
		XUIResponseWriter w = new XUIResponseWriter(writer, "text/html", "UTF-8");
    	
		try
		{
			w.startDocument();
    		w.startElement(HTMLTag.STYLE, null);
    			w.write(getCSS());
    		w.endElement(HTMLTag.STYLE);
    		w.startElement(HTMLTag.TABLE,null);
    			w.writeAttribute("class", "relations", null);
		
			w.startElement(HTMLTag.TR, null);	
    			w.startElement(HTMLTag.TH, null);
            	w.write("Atributo");
            w.endElement(HTMLTag.TH);
            w.startElement(HTMLTag.TH, null);
        		w.write("Valor");
        	w.endElement(HTMLTag.TH);
        	w.startElement(HTMLTag.TH, null);
        		w.write("Tipo");
        	w.endElement(HTMLTag.TH);	
        	w.endElement(HTMLTag.TR);	
			
			
			while (listOfLogs.next())
        	{
				w.startElement(HTMLTag.TR, null);
				
        		boObject currentLog = listOfLogs.getObject();
        		String type = currentLog.getAttribute("type").getValueString();
                Object value = null;

                if (type.equalsIgnoreCase("BOOLEAN") || type.equalsIgnoreCase("CHAR"))
                {
                    value = currentLog.getAttribute("value_String").getValueObject();
                }
                else if (type.equalsIgnoreCase("CURRENCY") ||
                        type.equalsIgnoreCase("NUMBER"))
                {
                    value = currentLog.getAttribute("value_Long").getValueObject();
                }
                else if (type.equalsIgnoreCase("DATE") ||
                        type.equalsIgnoreCase("DATETIME") ||
                        type.equalsIgnoreCase("DURATION"))
                {
                    value = currentLog.getAttribute("value_Date").getValueObject();
                }
                else if (type.equalsIgnoreCase("CLOB"))
                {
                	value = currentLog.getAttribute("value_CLOB").getValueObject();
                }
                String attributeName = currentLog.getAttribute("attribute").getValueString();
                String attributeType = currentLog.getAttribute("type").getValueString();
                String valueAsString = "";
                valueAsString = String.valueOf(value);
                
                w.startElement(HTMLTag.TD, null);
                	w.write(attributeName);
                w.endElement(HTMLTag.TD);
                w.startElement(HTMLTag.TD, null);
            		w.write(valueAsString);
            	w.endElement(HTMLTag.TD);
            	w.startElement(HTMLTag.TD, null);
            		w.write(attributeType);
            	w.endElement(HTMLTag.TD);
                
                w.endElement(HTMLTag.TR);
                
        	}
			
			w.endElement(HTMLTag.TABLE);
			w.endDocument();
			
			return writer.toString();
		}
		catch (Exception e)
		{
			log.severe("Error while listing the logs of object: " + bouiOfObjectLogs ,e);
			return BeansMessages.MSG_ERROR_IN_LOGS.toString() + ": " +e.getLocalizedMessage();
		} 
	}
    
    /**
     * 
     * The CSS for the List of Logs
     * 
     * @return A valid CSS with the styles
     */
    public static String getCSS()
    {
    	return "table.relations "+
		  "{ " +
			"  margin: 1em 1em 1em 2em;" +
			"  border-collapse: collapse;" +
			"  width:90%;" +
		  "}" +
		"table.relations td" + 
		"{" +
		"    border-left: 1px solid #C1DAD7;" +
		"	border-right: 1px solid #C1DAD7;" +
		"	border-bottom: 1px solid #C1DAD7;" +
		"	background: #fff;" +
		"	padding: 6px 6px 6px 12px;" +
		"	color: #6D929B;" +
		"}" +
		"table.relations th" + 
		"{" +
		"	font: bold 10px \"Trebuchet MS\", Verdana, Arial, Helvetica," +
		"	sans-serif;" +
		"	color: #003399;" +
		"	border-right: 1px solid #C1DAD7;" +
		"	border-left: 1px solid #C1DAD7;" +
		"	border-bottom: 1px solid #C1DAD7;" +
		"	border-top: 1px solid #C1DAD7;" +
		"	letter-spacing: 2px;" +
		"	text-transform: uppercase;" +
		"	text-align: left;" +
		"	padding: 3px 3px 3px 6px;" +
		"	background: #B0C4DE;" +
		"}";
    }
	
    
    /**
     * 
     * 
     * Renders the differences of a given boObject with a previous version
     * as an HTML form
     * 
     * @param frmComponent The formEdit component to have access to the current object
     * @param bouiVersion The boui of the Ebo_Versioning object
     * @param doc The XML representation of the form (with the values from the object)
     * @param newContext A new EboContext to hold the previous version of the object
     * 
     * @return A HTML string with the render of the differences
     */
    public static String renderDifferencesWithPreviousVersion(FormEdit frmComponent, long bouiVersion, XMLDocument doc, EboContext newContext)
    {
    	try
		{
			EboContext		eboCtx = frmComponent.getTargetObject().getEboContext();
			//Boui of the Ebo_Versioning
			
			boObject		objectVersion = boObject.getBoManager().loadObject(eboCtx, bouiVersion);
			
			boVersioning	versioningManager = new boVersioning();
			long			bouiOfObject = objectVersion.getAttribute("changedObject").getValueLong();
			long			versionOfObject = objectVersion.getAttribute("version").getValueLong();
			
			//Create a new context to hold the rollback version
			//which means the original object can be left alone
			versioningManager.rollbackVersion(newContext,bouiOfObject,versionOfObject, true);
			
			boObject		rollBackVersion =  boObject.getBoManager().loadObject(newContext, bouiOfObject);
			
			//Get the difference between objects
			ObjectDifference diffObj = new ObjectDifference(frmComponent.getTargetObject(), rollBackVersion);
			
			//Get the Map of differences of attributes
			HashMap<String,ObjectAttributeValuePair> diffAttributes = diffObj.getAttributeDifferencesOfObjects();
			
			//Get the Map of differences of bridges
			HashMap<String,GridFlashBack> diffBridges = diffObj.getBridgeDifferencesOfObjects();
			
			Iterator<String> itDiffAtts = diffAttributes.keySet().iterator();
			Element differences = doc.createElement("differences");
			differences.setAttribute("label", BeansMessages.LBL_DIFFERENCES_RESUME.toString());
			while (itDiffAtts.hasNext())
			{
				Element attribute = doc.createElement("attribute");
				String attName = itDiffAtts.next();
				ObjectAttributeValuePair pair = diffAttributes.get(attName);
				attribute.setAttribute("name", pair.getAttName());
				attribute.setAttribute("oldValue", pair.getOldVal());
				attribute.setAttribute("newValue", pair.getNewVal());
				differences.appendChild(attribute);
			}
			doc.getDocumentElement().appendChild(differences);
			
			//Update the XML tree with the differences in attributes and bridges
			updateXMLTreeWithAttributeDifferences(doc, diffAttributes, diffBridges,differences);
			
			String xmlSourceContent = ngtXMLUtils.getXML(doc);
			
			//System.out.println(xmlSourceContent);
			
			//Apply the XSLT
			final String XSLT = "showDifferencesVersion.xsl";
			InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream( XSLT );
			String finalTransformer = ngtXMLUtils.getXML(ngtXMLUtils.loadXML(in));
			
			Source xmlSource = new StreamSource(new StringReader(xmlSourceContent));
	        Source xsltSource = new StreamSource(new StringReader(finalTransformer));

	        StringWriter pw = new StringWriter();
        	// the factory pattern supports different XSLT processors
	        TransformerFactory transFact =
	                TransformerFactory.newInstance();
	        Transformer trans = transFact.newTransformer(xsltSource);
	        trans.transform(xmlSource, new StreamResult(pw));
			
			newContext.close();
			
			return pw.toString();
			
			
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
			if (newContext != null)
				newContext.close();
		} 
		
		//Show the Logs in case of error 
		String logs = getListOfLogsObject(bouiVersion, frmComponent);
		
		return BeansMessages.MSG_ERROR_IN_DIFFERENCES_RENDER.toString()
				+ "<br />" + logs;
    }
    
    
    public static String renderDifferencesWithFlashBack(FormEdit frmComponent, XMLDocument doc)
    {
    	/*
		 * This method is responsible for the generation of the HTML form with the differences
		 * between the object being edited (and not saved) in this form and the last
		 * one saved in the database.
		 * 
		 * The steps are to do this are
		 * 
		 * 1) Retrieving the XML serialization of the form
		 * 2) Checking the FlashBack Object from the edited object and comparing
		 * the values
		 * 3) Edit the XML serialization so that the deleted/edited/added values
		 * are appended in the right positions
		 * 4) Apply the XSLT
		 * 
		 * 
		 * */
		
		boObject 		current = frmComponent.getTargetObject();
		
		//Retrieve the XML Serialization
		try 
		{
			boFlashBackHandler handlerFlashback = current.getFlashBackHandler();
			
			//Get the Map of differences of attributes
			HashMap<String,ObjectAttributeValuePair> diffAttributes = handlerFlashback.getAttributeDiference();
			
			//Get the Map of differences of bridges
			HashMap<String,GridFlashBack> diffBridges = handlerFlashback.getBridgeDifference();
			
			//Apply to the XML
			/*
			 * 
			 * <differences>
			 * 		<attribute name="NAME" oldValue="OLD_VAL" newValue="NEW_VAL" />
			 * 		<gridTopPanel>
			 * 			<!-- The panels to show -->
			 * 		</gridTopPanel>
			 * </differences>
			 * 
			 * 
			 **/
			
			//Update the XML tree with a sumary of differences
			Iterator<String> itDiffAtts = diffAttributes.keySet().iterator();
			Element differences = doc.createElement("differences");
			String val = "Difference Summary";
			differences.setAttribute("label", val);
			while (itDiffAtts.hasNext())
			{
				Element attribute = doc.createElement("attribute");
				String attName = itDiffAtts.next();
				ObjectAttributeValuePair pair = diffAttributes.get(attName);
				attribute.setAttribute("name", pair.getAttName());
				attribute.setAttribute("oldValue", pair.getOldVal());
				attribute.setAttribute("newValue", pair.getNewVal());
				differences.appendChild(attribute);
			}
			doc.getDocumentElement().appendChild(differences);
			
			//Update the XML tree with the differences in attributes and bridges
			updateXMLTreeWithAttributeDifferences(doc, diffAttributes, diffBridges,differences);
			
			String xmlSourceContent = ngtXMLUtils.getXML(doc);
			
			//Apply the XSLT
			final String XSLT = "showDifferences.xsl";
			InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream( XSLT );
			String finalTransformer = ngtXMLUtils.getXML(ngtXMLUtils.loadXML(in));
			
			Source xmlSource = new StreamSource(new StringReader(xmlSourceContent));
	        Source xsltSource = new StreamSource(new StringReader(finalTransformer));

	        StringWriter pw = new StringWriter();
        	// the factory pattern supports different XSLT processors
	        TransformerFactory transFact =
	                TransformerFactory.newInstance();
	        Transformer trans = transFact.newTransformer(xsltSource);
	        trans.transform(xmlSource, new StreamResult(pw));
	        
	        //Output the result to the response
	        String result = pw.toString();
	        //System.out.println(result);
	        return result;
        } 
		catch (Exception e) 
		{
			throw new RuntimeException(e);
		}
    }
    
    
	/**
	 * 
	 * Represents a pair label/value of a cell, it's used to 
	 * mark the value and position of a given cell when creating the
	 * list of differences in attributes
	 * 
	 * @author Pedro Rio
	 *
	 */
	public class CellPositionValue
	{
		private String value;
		private int colspan;
		public CellPositionValue(String value, int colSpan)
		{
			this.value = value;
			this.colspan = colSpan;
		}

		public String getValue() {
			return value;
		}
		
		public int getColSpan(){
			return this.colspan;
		}
	}
	
	
	
	
}
