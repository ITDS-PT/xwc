package netgest.bo.xwc.components.classic.charts;

import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Iterator;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import jofc2.model.Chart;
import jofc2.model.Text;
import jofc2.model.axis.XAxis;
import jofc2.model.elements.BarChart.Bar;
import netgest.bo.runtime.EboContext;
import netgest.bo.system.boApplication;
import netgest.bo.xwc.components.HTMLAttr;
import netgest.bo.xwc.components.HTMLTag;
import netgest.bo.xwc.components.classic.charts.configurations.IBarChartConfiguration;
import netgest.bo.xwc.components.classic.charts.datasets.SeriesDataSet;
import netgest.bo.xwc.components.classic.charts.datasets.SeriesDataSetSQL;
import netgest.bo.xwc.framework.XUIBindProperty;
import netgest.bo.xwc.framework.XUIRenderer;
import netgest.bo.xwc.framework.XUIRendererServlet;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.XUIScriptContext;
import netgest.bo.xwc.framework.components.XUIComponentBase;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.data.category.DefaultCategoryDataset;


/**
 * 
 * A BarChart component
 * 
 * @author Pedro Pereira
 * 
 * 
 */
public class BarChart extends XUIComponentBase  implements netgest.bo.xwc.components.classic.charts.Chart{

	/**
	 * The default height for a pie chart
	 */
	private static final int DEFAULT_HEIGHT = 300;
	
	/**
	 * The default width for a pie chart
	 */
	private static final int DEFAULT_WIDTH = 500;
	
	/**
	 * Orientation of the chart as horizontal
	 */
	private static final String CHART_ORIENTATION_HORIZONTAL = "horizontal";
	
	/**
	 * Orientation of the chart as vertical
	 */
	private static final String CHART_ORIENTATION_VERTICAL = "vertical";
	
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
	private XUIBindProperty<SeriesDataSet> dataSet = 
		new XUIBindProperty<SeriesDataSet>("dataSet", this, SeriesDataSet.class );
	
	/**
	 * The orientation of the bar char (horizontal / vertical
	 */
	private XUIBindProperty<String> orientation =
		new XUIBindProperty<String>("orientation", this, String.class);
	
	/**
	 * Optional configurations for the 
	 */
	private XUIBindProperty<IBarChartConfiguration> configOptions = 
		new XUIBindProperty<IBarChartConfiguration>("configOptions", this, IBarChartConfiguration.class);
	
	/**
	 * The width of the chart (rendered on the client)
	 */
	private XUIBindProperty<Integer> width = 
		new XUIBindProperty<Integer>("width", this, Integer.class);
	
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
	 * The sql attribute where the column names are stored
	 */
	private XUIBindProperty<String> sqlAttColumn = 
		new XUIBindProperty<String>("sqlAttColumn", this, String.class);
	
	/**
	 * The sql attribute where the series names are stored
	 */
	private XUIBindProperty<String> sqlAttSeries = 
		new XUIBindProperty<String>("sqlAttSeries", this, String.class);
	
	/**
	 * The sql attribute where the values
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
	public SeriesDataSet getDataSet()
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
	public IBarChartConfiguration getConfigOptions()
	{
		return this.configOptions.getEvaluatedValue();
	}
	
	/**
	 * 
	 * Sets the Configuration Options expression
	 * 
	 * @param configExpr The configuration options expression string
	 */
	public void setConfigOptions(String configExpr)
	{
		this.configOptions.setExpressionText(configExpr);
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
	 * Sets the expression for the SQL Column Attribute
	 * 
	 * @param newSqlColumnExpr The SQL attribute name for the column of the chart
	 */
	public void setSqlAttColumn(String newSqlColumnExpr)
	{
		this.sqlAttColumn.setExpressionText(newSqlColumnExpr);
	}
	
	/**
	 * 
	 * Return the SQL attribute which contains the category values
	 * 
	 * @return The SQL attribute which contains the category values
	 */
	public String getSqlAttColumn()
	{
		return this.sqlAttColumn.getEvaluatedValue();
	}
	

	/**
	 * 
	 * Sets the expression for the SQL Series Attribute
	 * 
	 * @param newSqlColumnExpr The SQL attribute name for the Series of the chart
	 */
	public void setSqlAttSeries(String newSqlSeriesExpr)
	{
		this.sqlAttSeries.setExpressionText(newSqlSeriesExpr);
	}
	
	/**
	 * 
	 * Return the SQL attribute which contains the Series values
	 * 
	 * @return The SQL attribute which contains the Series values
	 */
	public String getSqlAttSeries()
	{
		return this.sqlAttSeries.getEvaluatedValue();
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
	 * Returns the orientation of this 
	 * 
	 * @return
	 */
	public String getOrientation()
	{
		return this.orientation.getEvaluatedValue();
	}
	
	/**
	 * 
	 * Sets the orientation expression 
	 * 
	 * @return The orientation expression
	 */
	public void setOrientation(String orientationExpr)
	{
		this.orientation.setExpressionText(orientationExpr);
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
	 * Renders the LineChart as an image to the given output stream
	 * 
	 * @param out The output stream to write the chart to
	 * @param force If the charts type is not image, forces the chart to be rendered as image
	 */
	public void outputChartAsImageToStream(OutputStream out, boolean force){
		
		if (getType().equalsIgnoreCase(TYPE_CHART_IMG) || force)
		{
			
			String xAxisLabel = "";
			String yAxisLabel = "";
			
			//Retrieve the data
			DefaultCategoryDataset data = new DefaultCategoryDataset();
			
			if (getSql() != null)
			{
				try
				{
					EboContext ctx = boApplication.currentContext().getEboContext();
					java.sql.Connection conn = ctx.getConnectionData();
					Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
                            ResultSet.CONCUR_READ_ONLY);
					ResultSet srs = stmt.executeQuery(getSql());
					while (srs.next()) 
					{
					        String column = srs.getString(getSqlAttColumn());
					        String series = srs.getString(getSqlAttSeries());
					        float value = srs.getFloat(getSqlAttValues());
					        data.setValue(value, series, column);
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
			else
			{
				SeriesDataSet setMine = (SeriesDataSet) getDataSet();
				//Add the values to the data set
				Iterator<String> it = setMine.getColumnKeys().iterator();
				while (it.hasNext())
				{
					String currColum = (String) it.next();
					Iterator<String> itRows = setMine.getSeriesKeys().iterator();
					while (itRows.hasNext())
					{
						String currSeries = (String) itRows.next();
						Number val = setMine.getValue(currSeries, currColum);
						data.addValue(val, currSeries, currColum);
					}
				}
				xAxisLabel = setMine.getXAxisLabel(); 
				yAxisLabel = setMine.getYAxisLabel(); 
			}
			
			
			
			//Set the chart orientation
			PlotOrientation orientation = PlotOrientation.VERTICAL;
			if (getOrientation() != null)
			{
				if (getOrientation().equalsIgnoreCase(CHART_ORIENTATION_HORIZONTAL))
					orientation = PlotOrientation.HORIZONTAL;
				if (getOrientation().equalsIgnoreCase(CHART_ORIENTATION_VERTICAL))
					orientation = PlotOrientation.VERTICAL;
			}
			
			//Create the chart
			JFreeChart chart = ChartFactory.createBarChart
			(
				getLabel(), //Chart Title
				xAxisLabel,
				yAxisLabel,
				data, // data 
				orientation, // orientation
				true, // include legend
				true, // tooltips?
				false // URLs?
			);
			
			//Set Border visibility and background color
			chart.setBackgroundPaint(Color.WHITE);
			chart.setBorderVisible(false);
			CategoryPlot plot = (CategoryPlot) chart.getCategoryPlot();
			//Set the upper margin so that labels show inside the chart
			plot.getRangeAxis().setUpperMargin(0.1);
			//Set space between Items
			BarRenderer renderer = (BarRenderer) plot.getRenderer();
			renderer.setItemMargin(0.0);
			
			
			
			//Set colors
			plot.setRangeGridlinePaint(Color.BLACK);
			plot.setBackgroundPaint(Color.white);
			
			//Tooltip Generator
			//CategoryItemRenderer renderer = plot.getRenderer(); 
			CategoryItemLabelGenerator generator = new StandardCategoryItemLabelGenerator(
					"{2}", new DecimalFormat("0.00")); 
			renderer.setBaseItemLabelGenerator(generator);
			renderer.setBaseItemLabelsVisible(true);
			
			//Chart size
			Integer width = getWidth();
			if (width == null)
				width = DEFAULT_WIDTH;
			Integer height = getHeight();
			if (height == null)
				height = DEFAULT_HEIGHT;
			
			//Apply the Configuration options for the Bar Chart
			if (getConfigOptions() != null)
			{
				IBarChartConfiguration chartConfigurations = (IBarChartConfiguration) getConfigOptions();
				if (chartConfigurations != null)
				{
					if (!chartConfigurations.showChartTitle())
						chart.setTitle("");
					
					if (chartConfigurations.getTooltipString() != null)
					{
						String expression = chartConfigurations.getTooltipString();
						expression = expression.replace("$val", "{2}");
						CategoryItemRenderer rendererLabel = plot.getRenderer(); 
						CategoryItemLabelGenerator generatorCustom = new StandardCategoryItemLabelGenerator(
								expression, new DecimalFormat("0.00"));
						rendererLabel.setBaseItemLabelGenerator(generatorCustom);
					}
					
					if (chartConfigurations.getColours() != null)
					{
						Color[] colors = chartConfigurations.getColours();
						BarRenderer rendererColors = (BarRenderer) plot.getRenderer();
						int pos = 0;
						for (Color p : colors)
						{
							rendererColors.setSeriesPaint(pos,p);
							pos++;
						}
					}
					
					if (chartConfigurations.getBackgroundColour() != null)
					{
						chart.setBackgroundPaint(chartConfigurations.getBackgroundColour());
					}
				}
			}
			
			//Render the chart
			try {
				ChartUtilities.writeChartAsPNG(out, chart, width, height);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static class XEOHTMLRenderer extends XUIRenderer implements XUIRendererServlet {
			
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
			BarChart component = (BarChart)oComp;
			OutputStream out = oResponse.getOutputStream();
			
			if (component.getType().equalsIgnoreCase(TYPE_CHART_IMG))
			{
				//Chart size
				oResponse.setContentType("image/png");
				component.outputChartAsImageToStream(out, false);
			}
			else if (component.getType().equalsIgnoreCase(TYPE_CHART_FLASH))
			{
				Chart c = new Chart();
				IBarChartConfiguration chartConfigurations = null;
				if (component.getConfigOptions() != null)
					chartConfigurations = (IBarChartConfiguration) component.getConfigOptions();
				
				//Default background colour
				c.setBackgroundColour("#FFFFFF");
				c.setTitle(new Text(component.getLabel()));
				
				//Labels to be placed in the XAxis
				HashSet<String> labelsUsed = new HashSet<String>();
				XAxis xAx = new XAxis();
				//YAxis yAy = new YAxis();
				//Fill the data and the labels
				SeriesDataSet setMine;
				if (component.getSql() != null)
				{
					String sqlExpression = component.getSql();
					String attColumn = component.getSqlAttColumn();
					String attSeries = component.getSqlAttSeries();
					String attValues = component.getSqlAttValues();
					EboContext context = boApplication.currentContext().getEboContext();
					setMine = new SeriesDataSetSQL(context, sqlExpression, attColumn, attSeries, attValues);
				}
				else
				{
					setMine = (SeriesDataSet) component.getDataSet();
				}
				
				Iterator<String> it = setMine.getSeriesKeys().iterator();
				
				Color[] mapOfColors = ChartUtils.DEFAULT_COLORS;
				if (chartConfigurations != null)
					if (chartConfigurations.getColours() != null)
					mapOfColors = chartConfigurations.getColours();
				
				int colorCounter = 0;
				
				if (component.getOrientation().equalsIgnoreCase(CHART_ORIENTATION_VERTICAL))
				{
					while (it.hasNext())
					{
						jofc2.model.elements.BarChart currChart = new jofc2.model.elements.BarChart(jofc2.model.elements.BarChart.Style.GLASS);
						
						Color currentColor = null;
						if (colorCounter <= mapOfColors.length - 1)
							currentColor = mapOfColors[colorCounter];
						else
							currentColor = ChartUtils.getRandomDarkColor();
						
						String rgb = ChartUtils.ColorToRGB(currentColor);
						
						String currentSeries = (String) it.next();
						currChart.setText(currentSeries);
						currChart.setColour(rgb);
						
						
						Iterator<String> itColum = setMine.getColumnKeys().iterator();
						
						while (itColum.hasNext())
						{
							String currentColumn = (String) itColum.next();
							Number val = setMine.getValue(currentSeries,currentColumn);
							Bar barra = new Bar(val,rgb);
							
							if (!labelsUsed.contains(currentColumn))
							{
								labelsUsed.add(currentColumn);
								xAx.addLabels(currentColumn);
							}
							currChart.addBars(barra);
						}
						colorCounter++;
						
						//Set the tooltip per Chart (equal for every one)
						if (chartConfigurations.getTooltipString() != null)
						{
							String expression = chartConfigurations.getTooltipString();
							expression = expression.replace("$val", "#val#");
							currChart.setTooltip(expression);
						}
						
						c.addElements(currChart);
					}
					
					//Set the configuration options for the chart
					if (chartConfigurations != null)
					{
						//Set the background color
						if (chartConfigurations.getBackgroundColour() != null)
						{
							Color color = chartConfigurations.getBackgroundColour();
							String rgb = Integer.toHexString(color.getRGB());
							rgb = rgb.substring(2, rgb.length());
							c.setBackgroundColour(rgb);
						}
	
						//Show the title or not
						if (!chartConfigurations.showChartTitle())
						{
							c.setTitle(null);
						}
					}
					
					c.setXAxis(xAx);
					c.computeYAxisRange(10);
				}
				else //We have a HORIZONTAL Graph
				{
					//FIXME: Colors is not possible :/ and still not working
					/*jofc2.model.elements.HorizontalBarChart currChart = new jofc2.model.elements.HorizontalBarChart();
					while (it.hasNext())
					{
						String currentSeries = (String) it.next();
						Iterator<String> itColum = setMine.getColumnKeys().iterator();
						while (itColum.hasNext())
						{
							
							//Color of the chart
							colorPos = colorCounter % 3;
							String rgb = Integer.toHexString(colours[colorPos].getRGB());
							rgb = rgb.substring(2, rgb.length());
							
							currChart.setText(currentSeries);
							if (mapOfColors != null)
							{
								Color color = mapOfColors.get(currentSeries);
								rgb = Integer.toHexString(color.getRGB());
								rgb = rgb.substring(2, rgb.length());
								currChart.setColour(rgb);
							}
							else
								currChart.setColour(rgb);
							
														
							String currentColumn = (String) itColum.next();
							Number val = setMine.getValue(currentSeries,currentColumn);
							HorizontalBarChart.Bar barra = new HorizontalBarChart.Bar(0,val);
							if (!labelsUsed.contains(currentColumn))
							{
								labelsUsed.add(currentColumn);
								yAy.addLabels(currentColumn);
							}
							currChart.addBars(barra);
							
							//Set the tooltip per Chart (equal for every one)
							if (chartConfigurations.getTooltipString() != null)
							{
								String expression = chartConfigurations.getTooltipString();
								expression = expression.replace("$key", "#label#");
								expression = expression.replace("$val", "#val#");
								expression = expression.replace("$percent", "#percent#");
								currChart.setTooltip(expression);
							}
							
							colorCounter++;
						}
						
					}
					c.addElements(currChart);
					
					//Set the configuration options for the chart
					
					if (chartConfigurations != null)
					{
						//Set the background color
						if (chartConfigurations.getBackgroundColour() != null)
						{
							Color color = chartConfigurations.getBackgroundColour();
							String rgb = Integer.toHexString(color.getRGB());
							rgb = rgb.substring(2, rgb.length());
							c.setBackgroundColour(rgb);
						}
	
						//Show the title or not
						if (!chartConfigurations.showChartTitle())
						{
							c.setTitle(null);
						}
					}
					//XAxis horizontalAxis = new XAxis();
					//c.setXAxis(horizontalAxis);
					
					//c.setXAxis(xAx);
					//c.setYAxis(yAy);
					//c.computeYAxisRange(10);*/
				}	
				System.out.println(c.toDebugString());
				
				
				String val = "{" +
   "\"bg_colour\": \"ffffff\"," +
   "\"elements\": [{ "+
   "   \"colour\": \"00ff00\"," +
   "   \"text\": \"Aug 07\"," +
   "   \"tip\": \"#val#\"," +
   "   \"type\": \"hbar\"," +
   "   \"values\": [" +
   "      { "+
   "         \"left\": 0," +
   "         \"right\": 2400" +
   "      }, "+
   "      { "+
   "         \"left\": 0," +
   "         \"right\": 900" +
   "      }," +
   "      {" +
   "         \"left\": 0," +
   "         \"right\": 1200" +
   "      }," +
   "      {" +
   "         \"left\": 0," +
   "         \"right\": 3000" +
   "      }," +
   "      {" +
   "         \"left\": 0," +
   "         \"right\": 500" +
   "      }," +
   "      {" +
   "         \"left\": 0," +
   "         \"right\": 2100" +
   "      }" +
   "   ]" +
   "}]," +
   "\"is_decimal_separator_comma\": 0," +
   "\"is_fixed_num_decimals_forced\": 0," +
   "\"is_thousand_separator_disabled\": 0," +
   "\"num_decimals\": 2," +
   "\"y_axis\": {}" +
   "}";
				
				out.write(c.toString().getBytes());
				out.write(val.getBytes());
				out.close();
			}
			
		}
		
		
		@Override
		public void encodeBegin(XUIComponentBase component) throws IOException {
			XUIResponseWriter w = getResponseWriter();
			BarChart oComp = (BarChart)component;
			
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
				//URL Encode is required for the Flash Component
				url = URLEncoder.encode(url,"UTF-8");
				
				int width = oComp.getWidth();
				int height = oComp.getHeight();
				
				b.append("swfobject.embedSWF(\"open-flash-chart.swf\", \""+component.getClientId()+"\"," +
						" \""+width+"\", \""+height+"\", \"9.0.0\",\"expressInstall.swf\", " +
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
					w.writeAttribute(HTMLAttr.WIDTH, oComp.getWidth(), null);
					w.writeAttribute(HTMLAttr.HEIGHT, oComp.getHeight(), null);
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
