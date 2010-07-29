package netgest.bo.xwc.components.classic.charts;

import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import jofc2.model.Chart;
import jofc2.model.Text;
import netgest.bo.ql.V2.QLParser;
import netgest.bo.runtime.EboContext;
import netgest.bo.system.boApplication;
import netgest.bo.xwc.components.HTMLAttr;
import netgest.bo.xwc.components.HTMLTag;
import netgest.bo.xwc.components.classic.charts.configurations.IPieChartConfiguration;
import netgest.bo.xwc.components.classic.charts.datasets.PieDataSet;
import netgest.bo.xwc.framework.XUIBindProperty;
import netgest.bo.xwc.framework.XUIRenderer;
import netgest.bo.xwc.framework.XUIRendererServlet;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.XUIScriptContext;
import netgest.bo.xwc.framework.components.XUIComponentBase;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.PieSectionLabelGenerator;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.data.general.DefaultPieDataset;



/**
 * 
 * 
 * A XVW component to render a 2D PieChart
 * 
 * 
 * @author Pedro Rio
 * @version 1
 * 
 * 
 * To use this component in a XWC Viewer use:
 * 
 * <xvw:pieChart 
 * 		dataSet="#{viewBean.METHOD_DATASET}"
 * 		width="pixels"
 * 		height="pixels"	
 * 		label="#{viewBean.LABEL}"
 * 		type="FLASH/IMG"
 * 		configOptions="#{viewBean.configOptions}"
 * 		sql = "SQL_EXPRESSION"
 * 		sqlAttCategory = "SQL_COLUMN_NAME_FOR_CATEGORIES"
 * 		sqlAttValues = "SQL_COLUMN_NAME_FOR_VALUES"
 * />
 * 
 * 
 *   
 * 
 */
public class PieChart extends XUIComponentBase implements netgest.bo.xwc.components.classic.charts.Chart 
{

	/**
	 * The default height for a pie chart
	 */
	private static final int DEFAULT_HEIGHT = 300;
	
	/**
	 * The default width for a pie chart
	 */
	private static final int DEFAULT_WIDTH = 500;
	
	
	/**
	 * The type of chart that renders JFreeChart based charts
	 */
	private static final String TYPE_CHART_IMG = "IMG";
	
	/**
	 * The type of chart that renders Flash based charts
	 */
	private static final String TYPE_CHART_FLASH = "FLASH";
	
	/**
	 * The source of data for the chart
	 */
	private XUIBindProperty<PieDataSet> dataSet = 
		new XUIBindProperty<PieDataSet>("dataSet", this, PieDataSet.class );
	
	
	/**
	 * Optional configurations for the 
	 */
	private XUIBindProperty<IPieChartConfiguration> configOptions = 
		new XUIBindProperty<IPieChartConfiguration>("configOptions", this, IPieChartConfiguration.class);
	
	/**
	 * The width of the chart (rendered on the client)
	 */
	private XUIBindProperty<Integer> width = 
		new XUIBindProperty<Integer>("width", this, Integer.class);
	
	/**
	 * The maximum number of results to show from a given query 
	 */
	private XUIBindProperty<Integer> resultLimit = 
		new XUIBindProperty<Integer>("resultLimit", this, Integer.class);
	
	/**
	 * A BOQL Expression to use as a data source
	 */
	private XUIBindProperty<String> boql = 
		new XUIBindProperty<String>("boql", this, String.class);
	
	/**
	 * The height of the chart (rendered on the client)
	 */
	private XUIBindProperty<Integer> height = 
		new XUIBindProperty<Integer>("height", this, Integer.class);
	
	/**
	 * A label for the chart
	 */
	private XUIBindProperty<String> label = 
		new XUIBindProperty<String>("label", this, String.class);
	
	/**
	 * The type of the chart Flash/ Static Image
	 */
	private XUIBindProperty<String> type = 
		new XUIBindProperty<String>("type", this, String.class);
	
	/**
	 * The sql query to get the data from
	 */
	private XUIBindProperty<String> sql = 
		new XUIBindProperty<String>("sql", this, String.class);
	
	/**
	 * The sql attribute where the category names are stored
	 */
	private XUIBindProperty<String> sqlAttCategory = 
		new XUIBindProperty<String>("sqlAttCategory", this, String.class);
	
	/**
	 * The sql attribute where the values are stored
	 */
	private XUIBindProperty<String> sqlAttValues = 
		new XUIBindProperty<String>("sqlAttValues", this, String.class);
	
	public boolean wasStateChanged() 
	{
		return super.wasStateChanged();
	};
		
	@Override
	public void initComponent() {
		super.initComponent();
	}
	
	/**
	 * @param sqlResultLimit the sqlResultLimit to set
	 */
	public void setResultLimit(String resultExprt) {
		this.resultLimit.setExpressionText(resultExprt);
	}

	/**
	 * @return the sqlResultLimit
	 */
	public Integer getResultLimit() {
		return resultLimit.getEvaluatedValue();
	}
	
	/**
	 * @param resultExpr the sqlResultLimit to set
	 */
	public void setBoql(String resultExpr) {
		this.boql.setExpressionText(resultExpr);
	}

	/**
	 * @return the sqlResultLimit
	 */
	public String getBoql() {
		return boql.getEvaluatedValue();
	}

	/**
	 * 
	 * Sets the expression from which the DataSet will be retrieved
	 * 
	 * @param dataSetExpr The expression that will be used to retrieve the data set
	 */
	public void setDataSet(String dataSetExpr)
	{
		this.dataSet.setExpressionText(dataSetExpr);
	}
	
	/**
	 * 
	 * Retrieves the expression that represents the source of the data set
	 * 
	 * @return The expression that will be used to retrieve the data set
	 */
	public PieDataSet getDataSet()
	{
		return this.dataSet.getEvaluatedValue();
	}
	
	/**
	 * 
	 * Retrieves the expression that represents the optional configuration options
	 * of this pie chart
	 * 
	 * @return The expression used to retrieve the configuration options
	 */
	public IPieChartConfiguration getConfigOptions()
	{
		return this.configOptions.getEvaluatedValue();
	}
	
	/**
	 * 
	 * Sets the chart configuration expression
	 * 
	 * @param chartConfExpr
	 */
	public void setConfigOptions(String chartConfExpr)
	{
		this.configOptions.setExpressionText(chartConfExpr);
	}
	
	/**
	 * 
	 * Sets the expression for the label of the chart
	 * 
	 * @param newLabelExpr The label expression
	 */
	public void setLabel(String newLabelExpr)
	{
		this.label.setExpressionText(newLabelExpr);
	}
	
	/**
	 * 
	 * Return the label for the chart
	 * 
	 * @return The label to show with the chart
	 */
	public String getLabel()
	{
		return this.label.getEvaluatedValue();
	}
	
	
	/**
	 * 
	 * Sets the expression for inout data SQL
	 * 
	 * @param newSqlExpr The SQL expression
	 */
	public void setSql(String newSqlExpr)
	{
		this.sql.setExpressionText(newSqlExpr);
	}
	
	/**
	 * 
	 * Return the SQL expression to retrieve input data
	 * 
	 * @return The SQL expression to retrieve to the data base
	 */
	public String getSql()
	{
		return this.sql.getEvaluatedValue();
	}
	
	/**
	 * 
	 * Sets the expression for the SQL Category Attribute
	 * 
	 * @param newSqlExpr The SQL expression
	 */
	public void setSqlAttCategory(String newSqlExpr)
	{
		this.sqlAttCategory.setExpressionText(newSqlExpr);
	}
	
	/**
	 * 
	 * Return the SQL attribute which contains the category values
	 * 
	 * @return The SQL attribute which contains the category values
	 */
	public String getSqlAttCategory()
	{
		return this.sqlAttCategory.getEvaluatedValue();
	}
	
	/**
	 * 
	 * Sets the name of the values attribute in the SQL expression
	 * 
	 * @param newSqlAttValuesExpr The name of the values attribute
	 */
	public void setSqlAttValues(String newSqlAttValuesExpr)
	{
		this.sqlAttValues.setExpressionText(newSqlAttValuesExpr);
	}
	
	
	/**
	 * 
	 * Get the name of the sql attribute for the values
	 * 
	 * @return 
	 */
	public String getSqlAttValues()
	{
		return this.sqlAttValues.getEvaluatedValue();
	}
	
	/**
	 * 
	 * Sets the type of the chart (Image Based or Flash based)
	 * 
	 * @param newTypeExpr The type expression
	 */
	public void setType(String newTypeExpr)
	{
		this.type.setExpressionText(newTypeExpr);
	}
	
	/**
	 * 
	 * Returns the type of the chart
	 * 
	 * @return A string with the value <code>IMG</code> or <code>FLASH</code>
	 * 
	 */
	public String getType()
	{
		return this.type.getEvaluatedValue();
	}
	
	/**
	 * 
	 * Sets the with (in pixels) of the PieChart
	 * 
	 * @param width The value for the width of the chart (in pixels) 
	 */
	public void setWidth(String newWidthExpr)
	{
		this.width.setExpressionText(newWidthExpr);
	}
	
	/**
	 * 
	 * Retrieves the width (in pixels) of the chart
	 * 
	 * @return The number of pixels for the width of the chart
	 */
	public Integer getWidth()
	{
		return this.width.getEvaluatedValue();
	}
	
	/**
	 * 
	 * Sets the height of the chart (in pixels)
	 * 
	 * @param newHeight The number of pixels in height of the chart
	 */
	public void setHeight(String newHeightExpr)
	{
		this.height.setExpressionText(newHeightExpr);
	}
	
	/**
	 * 
	 * Get the height of the chart (in pixels)
	 * 
	 * @return The number of pixels for the height of the chart
	 */
	public Integer getHeight()
	{
		return this.height.getEvaluatedValue();
	}
	
	@Override
	public void preRender() {
	}
	
	@Override
	public Object saveState() {				
		return super.saveState();
	}
	
	
	/**
	 * 
	 * Outputs the rendered chart (as image) to a stream
	 * 
	 * @param out The {@link OutputStream} where to write the chart
	 * @param force If the chart is of type flash, using the force parameter as true
	 * will render the flash chart as an image
	 * 
	 */
	public void outputChartAsImageToStream(OutputStream out, boolean force){
		
		try {
			
			//XUIRequestContext oRequestContext = getRequestContext();
			if (getType().equalsIgnoreCase(TYPE_CHART_IMG) || force)
			{
				DefaultPieDataset data = new DefaultPieDataset(); 
				
				PieDataSet setMine = null;
				
				//Check where the input data is coming from
				if (getSql() != null || getBoql() != null)
				{ //Fill the values from a SQL Query
					
					try
					{
						EboContext ctx = boApplication.currentContext().getEboContext();
						
						String sqlToExecute = "";
						if (getBoql() != null){
							QLParser boqlParser = new QLParser();
							sqlToExecute = boqlParser.toSql(getBoql(), ctx);;	
						}
						if (getSql() != null)
							sqlToExecute = getSql();
						
						java.sql.Connection conn = ctx.getConnectionData();
						Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
	                            ResultSet.CONCUR_READ_ONLY);
						ResultSet srs = stmt.executeQuery(sqlToExecute);
						int count = 0;
						while (srs.next()) 
						{
								if (getResultLimit() > 0){
									if (count >= getResultLimit())
										break;
								}
						        String name = srs.getString(getSqlAttCategory());
						        double value = srs.getDouble(getSqlAttValues());
						        data.setValue(name,value);
						        count++;
						}
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
				else
				{ //Fill the values from a 
					setMine = (PieDataSet) getDataSet();
					Iterator<String> it = setMine.getCategories().iterator();
					while (it.hasNext())
					{
						String curr = (String) it.next();
						data.setValue(curr, setMine.getValue(curr));
					}
				}
					
				JFreeChart chart = ChartFactory.createPieChart(
						getLabel(), 
						data,
						true, //legend 
						true, //tooltips
						false); //URLs?
				
				chart.setBackgroundPaint(Color.white); 
				chart.setBorderVisible(false);
				PiePlot plot = (PiePlot) chart.getPlot();
				
				//CHanges background of labels to white
				plot.setLabelBackgroundPaint(new Color(255,255,255));
				
				//Changes background color to white
				plot.setBackgroundPaint(new Color(255,255,255));
				
				//Mete o border a false
				plot.setOutlineVisible(false);
				
				//Label expression 
				PieSectionLabelGenerator generator = new StandardPieSectionLabelGenerator(" {0} has {1} ({2})");
				plot.setLabelGenerator(generator);
				
				//Chart size
				Integer width = getWidth();
				if (width == 0)
					width = DEFAULT_WIDTH;
				Integer height = getHeight();
				if (height == 0)
					height = DEFAULT_HEIGHT;
				
				if (getConfigOptions() != null){
					IPieChartConfiguration chartConfigurations = (IPieChartConfiguration) getConfigOptions();
					if (chartConfigurations != null)
					{
						if (chartConfigurations.getBackgroundColour() != null)
						{
							Color color = chartConfigurations.getBackgroundColour();
							plot.setBackgroundPaint(color);
						}
						
						if (!chartConfigurations.showLabels())
							plot.setLabelGenerator(null);
						
						if (chartConfigurations.getColours() != null)
						{
							Color[] colorArray = chartConfigurations.getColours();
							int index = 0;
							for (Color p : colorArray)
							{
								String val = (String) plot.getDataset().getKey(index);
								plot.setSectionPaint(val, p);
								index++;
							}
						}
						else
						{
							Color[] colorArray = ChartUtils.DEFAULT_COLORS;
							int index = 0;
							for (Color p : colorArray)
							{
								String val = (String) plot.getDataset().getKey(index);
								plot.setSectionPaint(val, p);
								index++;
							}
						}
						
						if (!chartConfigurations.showChartTitle())
							chart.setTitle("");
						
						if (chartConfigurations.getTooltipString() != null)
						{
							String expression = chartConfigurations.getTooltipString();
							expression = expression.replace("$key", "{0}");
							expression = expression.replace("$val", "{1}");
							expression = expression.replace("$percent", "{2}");
							PieSectionLabelGenerator generatorAlternative = new StandardPieSectionLabelGenerator(expression);
							plot.setLabelGenerator(generatorAlternative);
						}
						
					}
					
				}
				
				//Render the chart as a PNG image
				ChartUtilities.writeChartAsPNG(out, chart, width, height);
			}
			
		} catch 
		(IOException e) {
			e.printStackTrace();
		}
		
		
		
	}
	


	/**
	 * 
	 * The class that renders the Graph in the Viewer (depending on the type, uses HTML+Servlet to render
	 * the image or HTML+Flash)
	 * 
	 * @author Pedro Pereira
	 *
	 */
	public static class XEOHTMLRenderer extends XUIRenderer implements XUIRendererServlet 
	{
		
		/* (non-Javadoc)
		 * 
		 * Used for the Image Based Graph, implements a servlet service so that
		 * the HTML tag <img src="URL"/> can be used
		 * 
		 * @see netgest.bo.xwc.framework.XUIRendererServlet#service(javax.servlet.ServletRequest, javax.servlet.ServletResponse, netgest.bo.xwc.framework.components.XUIComponentBase)
		 */
		@Override
		public void service(ServletRequest oRequest, ServletResponse oResponse,
				XUIComponentBase oComp) throws IOException 
		{
			PieChart component = (PieChart)oComp;
			OutputStream out = oResponse.getOutputStream();
			
			if (component.getType().equalsIgnoreCase(TYPE_CHART_IMG))
			{
				component.outputChartAsImageToStream(out,false);
			}
			else if (component.getType().equalsIgnoreCase(TYPE_CHART_FLASH))
			{
				//Create the chart with the default settings
				Chart c = new Chart();
				jofc2.model.elements.PieChart chartElements = new jofc2.model.elements.PieChart();
				
				c.setBackgroundColour("#FFFFFF");
				c.setTitle(new Text(component.getLabel()));
				c.setXAxis(null);
				chartElements.setAlpha((float)0.7);
				chartElements.setAnimate(true);
				chartElements.setGradientFill(false);
				
				chartElements.setTooltip("#val# of #total#<br>#percent# of 100%");
				
				IPieChartConfiguration chartConfigurations = (IPieChartConfiguration) component.getConfigOptions();
				boolean addColors = false;
				
				//Set configurations if we have them
				if (chartConfigurations != null)
				{
					if (chartConfigurations.getBackgroundColour() != null)
					{
						Color color = chartConfigurations.getBackgroundColour();
						String rgb = ChartUtils.ColorToRGB(color);
						c.setBackgroundColour(rgb);
					}
					
					if (!chartConfigurations.showLabels())
						chartElements.setNoLabels(true);
					
					if (chartConfigurations.getColours() != null)
					{
						addColors = true;
					}
					
					if (!chartConfigurations.showChartTitle())
						c.setTitle(null);
					
					if (chartConfigurations.getTooltipString() != null)
					{
						String expression = chartConfigurations.getTooltipString();
						expression = expression.replace("$key", "#label#");
						expression = expression.replace("$val", "#val#");
						expression = expression.replace("$percent", "#percent#");
						chartElements.setTooltip(expression);
					}
				}
				
				//Fill the data from the SQL query or the DataSet
				List<String> categoriesList = new Vector<String>();
				if (component.getSql() != null)
				{ //Fill the values from a SQL Query
					try
					{
						
						EboContext ctx = boApplication.currentContext().getEboContext();
						java.sql.Connection conn = ctx.getConnectionData();
						Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
	                            ResultSet.CONCUR_READ_ONLY);
						ResultSet srs = stmt.executeQuery(component.getSql());
						int count = 0;
						while (srs.next()) 
						{
							if (component.getResultLimit() > 0){
								if (count >= component.getResultLimit())
									break;
							}
					        String name = srs.getString(component.getSqlAttCategory());
					        double value = srs.getDouble(component.getSqlAttValues());
					        chartElements.addSlice(value,name);
					        categoriesList.add(name);
					        count++;
						}
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
					
				}
				else
				{
					PieDataSet setMine = (PieDataSet) component.getDataSet();
					Iterator<String> it = setMine.getCategories().iterator();
					
					while (it.hasNext())
					{
						String curr = (String) it.next();
						categoriesList.add(curr);
						chartElements.addSlice(setMine.getValue(curr), curr);
					}
				}
				
				//Add the colors:
				List<String> colors = new Vector<String>();
				int index = 0;
				//Add the colors
				Color[] arrayColors;
				if (addColors)
					arrayColors = chartConfigurations.getColours();
				else
					arrayColors = ChartUtils.DEFAULT_COLORS;
				
				Iterator<String> it = categoriesList.iterator();
				while (it.hasNext())
				{
					it.next();
					Color currColor; 
					if (index <= arrayColors.length -1)
						currColor = arrayColors[index];
					else
						currColor = ChartUtils.getRandomDarkColor();
					String rgb = ChartUtils.ColorToRGB(currColor);
					colors.add(rgb);
					index++;
				}
				chartElements.setColours(colors);	
				c.addElements(chartElements);
				out.write(c.toString().getBytes());
				out.close();
			}
			
		}
		
		
		@Override
		public void encodeBegin(XUIComponentBase component) throws IOException {
			XUIResponseWriter w = getResponseWriter();
			PieChart oComp = (PieChart)component;
			
			Integer width = oComp.getWidth();
			if (width == 0)
				width = DEFAULT_WIDTH;
			Integer height = oComp.getHeight();
			if (height == 0)
				height = DEFAULT_HEIGHT;
			
			//If our chart is a flash based one
			if (oComp.getType().equalsIgnoreCase(TYPE_CHART_FLASH))
			{
				StringBuilder b = new StringBuilder();
				
				w.getScriptContext().addInclude(
	            		XUIScriptContext.POSITION_HEADER, 
	            		"openflash", 
	            		"js/swfobject.js" 
	            );
				
				String url = ChartUtils.getCompleteServletURL(getRequestContext(), component.getClientId());
				url = URLEncoder.encode(url,"UTF-8");
				
				b.append("swfobject.embedSWF(\"open-flash-chart.swf\", " +
						"\""+component.getClientId()+"\", " +
						"\""+width+"\", " +
						"\""+height+"\", " +
						"\"9.0.0\",\"expressInstall.swf\", " +
						"{\"data-file\": \""+url+"\"});");
				
				w.getScriptContext().add( 
						XUIScriptContext.POSITION_HEADER, 
						component.getClientId(),
						b);
				
				w.startElement(HTMLTag.DIV, oComp);
					w.writeAttribute(HTMLAttr.ID, component.getClientId(), null);
				w.endElement(HTMLTag.DIV);
			}
			else if (oComp.getType().equalsIgnoreCase(TYPE_CHART_IMG))//JFreeChart based graph
			{
				String url = ChartUtils.getServletURL(getRequestContext(), component.getClientId());
				w.startElement(HTMLTag.IMG, oComp);
					w.writeAttribute(HTMLAttr.SRC, url, null);
					w.writeAttribute(HTMLAttr.WIDTH, width, null);
					w.writeAttribute(HTMLAttr.HEIGHT, height, null);
				w.endElement(HTMLTag.IMG);
			}
						
				
		}
		
		@Override
		public void encodeEnd(XUIComponentBase component) throws IOException {
		}

		@Override
		public void decode(XUIComponentBase component) {
			super.decode(component);
		}

								
	}
	
}
