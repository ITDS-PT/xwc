package netgest.bo.xwc.components.classic.charts;

import java.awt.BasicStroke;
import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.el.MethodExpression;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import jofc2.model.Chart;
import jofc2.model.Text;
import jofc2.model.axis.XAxis;
import jofc2.model.elements.LineChart.Dot;
import netgest.bo.runtime.EboContext;
import netgest.bo.system.boApplication;
import netgest.bo.xwc.components.HTMLAttr;
import netgest.bo.xwc.components.HTMLTag;
import netgest.bo.xwc.components.classic.charts.configurations.ILineChartConfiguration;
import netgest.bo.xwc.components.classic.charts.datasets.SeriesDataSet;
import netgest.bo.xwc.components.classic.charts.datasets.SeriesDataSetSQL;
import netgest.bo.xwc.framework.XUIBindProperty;
import netgest.bo.xwc.framework.XUIMethodBindProperty;
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
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;


/**
 * 
 * A LineChart component for XVW Viewers
 * 
 * @author Pedro Rio
 * @version 1
 * 
 *   
 * 
 */
public class LineChart extends XUIComponentBase {

	
	/**
	 * The default stroke size for the lines in the line chart
	 */
	private static final int DEFAULT_STROKE_SIZE  = 2;
	
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
	private XUIMethodBindProperty dataSet = 
		new XUIMethodBindProperty("dataSet", this, "#{viewBean.dataSet}" );
	
	/**
	 * Optional configurations for the 
	 */
	private XUIMethodBindProperty configOptions = 
		new XUIMethodBindProperty("configOption", this, "#{viewBean.configOptions}");
	
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
	public MethodExpression getDataSet()
	{
		return this.dataSet.getValue();
	}
	
	/**
	 * 
	 * Retrieves the expression that represents the optional configuration options
	 * of this pie chart
	 * 
	 * @return The expression used to retrieve the configuration options
	 */
	public MethodExpression getConfigOptions()
	{
		return this.configOptions.getValue();
	}
	
	/**
	 * 
	 * Sets the config options expression
	 * 
	 * @param configOptionsExpr The string with the config options expression
	 */
	public void setConfigOptions(String configOptionsExpr)
	{
		this.configOptions.setExpressionText(configOptionsExpr);
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
	 * The class that renders the Graph in the Viewer (depending on the type, uses HTML+Servlet to render
	 * the image or HTML+Flash)
	 * 
	 * @author Pedro Rio
	 * @version 1
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
			LineChart component = (LineChart)oComp;
			OutputStream out = oResponse.getOutputStream();
			
			List<String> colors = new LinkedList<String>();
			
			ILineChartConfiguration chartConfigurations = null;
			if (component.getConfigOptions() != null)
				chartConfigurations = (ILineChartConfiguration) component.getConfigOptions().invoke(getRequestContext().getELContext(), null);
			
			//If the Chart is image based
			if (component.getType().equalsIgnoreCase(TYPE_CHART_IMG))
			{
				
				DefaultCategoryDataset data = new DefaultCategoryDataset();
				
				if (component.getSql() != null)
				{
					try
					{
						EboContext ctx = boApplication.currentContext().getEboContext();
						java.sql.Connection conn = ctx.getConnectionData();
						Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,
	                            ResultSet.CONCUR_READ_ONLY);
						ResultSet srs = stmt.executeQuery(component.getSql());
						HashSet<String> seriesMaps = new HashSet<String>();
						
						while (srs.next()) 
						{
						        String column = srs.getString(component.getSqlAttColumn());
						        String series = srs.getString(component.getSqlAttSeries());
						        float value = srs.getFloat(component.getSqlAttValues());
						        data.setValue(value, series, column);
						        
						        //Add the name of the series to the set, so that we can reuse it later
						        seriesMaps.add(series);
						}
						colors.addAll(seriesMaps);
					}
					catch (Exception e)
					{
						e.printStackTrace();
					}
				}
				else
				{
					SeriesDataSet setMine = (SeriesDataSet) component.getDataSet().invoke(getRequestContext().getELContext(), null);
					
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
					
					colors = setMine.getSeriesKeys();
				}
				
				// create the chart... 
				JFreeChart chart = ChartFactory.createLineChart(
				component.getLabel(), // chart title 
				"", // domain axis label 
				"", // range axis label 
				data, // data
				PlotOrientation.VERTICAL, // orientation 
				true, // include legend 
				true, // tooltips 
				false // urls
				);
				
				chart.setBackgroundPaint(Color.WHITE);
				chart.setBorderVisible(false);
				
				CategoryPlot plot = (CategoryPlot) chart.getCategoryPlot();
				
				plot.setRangeGridlinePaint(Color.BLACK);
				plot.setBackgroundPaint(Color.WHITE);
				
				LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer(); 
				renderer.setDrawOutlines(true); 
				renderer.setUseFillPaint(true);
				
				//Draw colors
				Color[] mapOfColors = ChartUtils.DEFAULT_COLORS;
				if (chartConfigurations.getColours() != null)
					mapOfColors = chartConfigurations.getColours();
				
				int colorCounter = 0;
				
				Iterator<String> itSeries = colors.iterator();
				while(itSeries.hasNext())
				{
					itSeries.next();
					if (colorCounter <= mapOfColors.length-1)
						renderer.setSeriesPaint(colorCounter, mapOfColors[colorCounter]);
					else
						renderer.setSeriesPaint(colorCounter, ChartUtils.getRandomDarkColor());
						
					if (chartConfigurations.getStrokeSize() > 0)
						renderer.setSeriesStroke(colorCounter, new BasicStroke(chartConfigurations.getStrokeSize()));
					else
						renderer.setSeriesStroke(colorCounter, new BasicStroke(DEFAULT_STROKE_SIZE));
					colorCounter++;
				}
				
				
				
				//Label Renderer
				CategoryItemRenderer renderer2 = plot.getRenderer(); 
				CategoryItemLabelGenerator generator = new StandardCategoryItemLabelGenerator(
						"{2}", new DecimalFormat("0.00")); 
				renderer2.setBaseItemLabelGenerator(generator);
				renderer2.setBaseItemLabelsVisible(true);
				
				//Chart Size (width/height)
				oResponse.setContentType("image/png");
				Integer width = component.getWidth();
				if (width == null)
					width = DEFAULT_WIDTH;
				Integer height = component.getHeight();
				if (height == null)
					height = DEFAULT_HEIGHT;
				
				plot.getRangeAxis().setUpperMargin(0.1);
				
				if (chartConfigurations != null)
				{
					if (chartConfigurations.getBackgroundColour() != null)
						chart.setBackgroundPaint(chartConfigurations.getBackgroundColour());
					
					if (!chartConfigurations.showChartTitle())
						chart.setTitle("");
					
				}
				
				//Render that chart
				ChartUtilities.writeChartAsPNG(out, chart, width, height);
			}
			else if (component.getType().equalsIgnoreCase(TYPE_CHART_FLASH)) //If the Chart is flash based
			{
				Chart c = new Chart();
				
				jofc2.model.elements.LineChart chartElements = new jofc2.model.elements.LineChart();
				
				//Default color
				c.setBackgroundColour("#FFFFFF");
				//Label from the component
				c.setTitle(new Text(component.getLabel()));
				
				//Default alpha opacity
				chartElements.setAlpha((float)0.6);
				
				XAxis xAx = new XAxis();
				HashSet<String> labelsUsed = new HashSet<String>();
				
				HashSet<jofc2.model.elements.LineChart> elements;
				elements = new HashSet<jofc2.model.elements.LineChart>();
				
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
					//Fill the values
					setMine = (SeriesDataSet) component.getDataSet().invoke(getRequestContext().getELContext(), null);
					
					//Fill the data set
					Iterator<String> it = setMine.getSeriesKeys().iterator();
					
					int colorCounter = 0;
					
					Color[] mapOfColors = ChartUtils.DEFAULT_COLORS;
					if (chartConfigurations.getColours() != null)
						mapOfColors = chartConfigurations.getColours();
					while (it.hasNext())
					{
						jofc2.model.elements.LineChart currChart =  new jofc2.model.elements.LineChart();
						String currRow = (String) it.next();
						currChart.setText(currRow);
						Iterator<String> itRows = setMine.getColumnKeys().iterator();
						while (itRows.hasNext())
						{
							String currColumn = (String) itRows.next();
							if (!labelsUsed.contains(currColumn))
							{
								labelsUsed.add(currColumn);
								xAx.addLabels(currColumn);
							}
							Number val = setMine.getValue(currRow,currColumn );
							currChart.addDots(new Dot(val));
						}
						
						//Color of each series
						Color setColor = null;
						if (colorCounter <= mapOfColors.length - 1)
							setColor = mapOfColors[colorCounter];
						else
							setColor = ChartUtils.getRandomDarkColor();
						String rgb = ChartUtils.ColorToRGB(setColor);
						currChart.setColour(rgb);
						
						//Set the width of the chart
						if (chartConfigurations.getStrokeSize() > 0)
							currChart.setWidth(chartConfigurations.getStrokeSize());
						else
							currChart.setWidth(DEFAULT_STROKE_SIZE);
						
						//Set the tooltip if needed
						elements.add(currChart);
						colorCounter++;
						
					}
				}
				
				
				//Add the various lines to the chart
				Iterator<jofc2.model.elements.LineChart> itElements = elements.iterator();
				while (itElements.hasNext())
				{
					jofc2.model.elements.LineChart lines = itElements.next();
					c.addElements(lines);
				}
				
				//Set the configuration options
				if (chartConfigurations != null)
				{
					if (chartConfigurations.getBackgroundColour() != null)
					{
						Color color = chartConfigurations.getBackgroundColour();
						String rgb = Integer.toHexString(color.getRGB());
						rgb = rgb.substring(2, rgb.length());
						c.setBackgroundColour(rgb);
					}
					if (!chartConfigurations.showChartTitle())
					{
						c.setTitle(null);
					}
					
					if (chartConfigurations.getTooltipString() != null)
					{
						String tooltip = chartConfigurations.getTooltipString();
						tooltip = tooltip.replace("$val", "#val#");
						chartElements.setTooltip(tooltip);
					}
					
				}
				
				c.computeYAxisRange(10);
				c.setXAxis(xAx);
				out.write(c.toString().getBytes());
				out.close();
			}
			
		}
		
		
		@Override
		public void encodeBegin(XUIComponentBase component) throws IOException {
			XUIResponseWriter w = getResponseWriter();
			LineChart oComp = (LineChart)component;
			
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
				
				int width = oComp.getWidth();
				int height = oComp.getHeight();
				
				b.append("swfobject.embedSWF(\"open-flash-chart.swf\", \""+component.getClientId()+"\", " +
						"\""+width+"\", \""+height+"\", \"9.0.0\",\"expressInstall.swf\", " +
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
