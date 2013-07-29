package netgest.bo.xwc.components.classic.charts;

import netgest.bo.ql.QLParser;
import netgest.bo.runtime.EboContext;
import netgest.bo.system.Logger;
import netgest.bo.system.boApplication;
import netgest.bo.xwc.components.HTMLAttr;
import netgest.bo.xwc.components.HTMLTag;
import netgest.bo.xwc.components.annotations.Localize;
import netgest.bo.xwc.components.classic.charts.configurations.IPieChartConfiguration;
import netgest.bo.xwc.components.classic.charts.datasets.PieDataSet;
import netgest.bo.xwc.components.util.ComponentRenderUtils;
import netgest.bo.xwc.framework.XUIBindProperty;
import netgest.bo.xwc.framework.XUIRenderer;
import netgest.bo.xwc.framework.XUIRendererServlet;
import netgest.bo.xwc.framework.XUIRequestContext;
import netgest.bo.xwc.framework.XUIResponseWriter;
import netgest.bo.xwc.framework.XUIScriptContext;
import netgest.bo.xwc.framework.XUIStateBindProperty;
import netgest.bo.xwc.framework.components.XUIComponentBase;

import netgest.utils.StringUtils;

import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import jofc2.model.Chart;
import jofc2.model.Text;

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
 *          To use this component in a XWC Viewer use:
 * 
 *          <xvw:pieChart dataSet="#{viewBean.METHOD_DATASET}" width="pixels"
 *          height="pixels" label="#{viewBean.LABEL}" type="FLASH/IMG"
 *          configOptions="#{viewBean.configOptions}" sql = "SQL_EXPRESSION"
 *          sqlAttCategory = "SQL_COLUMN_NAME_FOR_CATEGORIES" sqlAttValues =
 *          "SQL_COLUMN_NAME_FOR_VALUES" />
 * 
 * 
 * 
 * 
 */
public class PieChart extends XUIComponentBase implements
		netgest.bo.xwc.components.classic.charts.Chart {

	/**
	 * The Logger
	 */
	private static Logger logger = Logger
			.getLogger("netgest.bo.xcw.components.classic.charts.PieChart");

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
	private XUIStateBindProperty<PieDataSet> dataSet = new XUIStateBindProperty<PieDataSet>(
			"dataSet", this, PieDataSet.class);

	/**
	 * Optional configurations for the Chart
	 */
	private XUIBindProperty<IPieChartConfiguration> configOptions = new XUIBindProperty<IPieChartConfiguration>(
			"configOptions", this, IPieChartConfiguration.class);

	/**
	 * The width of the chart (rendered on the client)
	 */
	private XUIBindProperty<Integer> width = new XUIBindProperty<Integer>(
			"width", this, Integer.class, "500");

	/**
	 * The maximum number of results to show from a given query
	 */
	private XUIBindProperty<Integer> resultLimit = new XUIBindProperty<Integer>(
			"resultLimit", this, Integer.class);

	/**
	 * A BOQL Expression to use as a data source
	 */
	private XUIStateBindProperty<String> boql = new XUIStateBindProperty<String>("boql",
			this, String.class);

	/**
	 * Label mapping 
	 */
	private XUIBindProperty<Map<String,String>> labelsMap = new XUIBindProperty<Map<String,String>>(
			"labels", this, Map.class);
	
	/**
	 * The height of the chart (rendered on the client)
	 */
	private XUIBindProperty<Integer> height = new XUIBindProperty<Integer>(
			"height", this, Integer.class, "300");

	/**
	 * A label for the chart
	 */
	@Localize
	private XUIStateBindProperty<String> label = new XUIStateBindProperty<String>(
			"label", this, String.class);

	/**
	 * The type of the chart Flash/ Static Image
	 */
	private XUIBindProperty<String> type = new XUIBindProperty<String>("type",
			this, String.class);

	/**
	 * The sql query to get the data from
	 */
	private XUIStateBindProperty<String> sql = new XUIStateBindProperty<String>("sql",
			this, String.class);

	/**
	 * The sql attribute where the category names are stored
	 */
	private XUIBindProperty<String> sqlAttCategory = new XUIBindProperty<String>(
			"sqlAttCategory", this, String.class);
	
	/**
	 * The sql parameters for the query
	 */
	private XUIBindProperty<Object[]> sqlParameters = new XUIBindProperty<Object[]>(
			"sqlParameters", this, Object[].class);

	/**
	 * Mappings for columns and colors
	 */
	private XUIBindProperty<Map<String,Color>> colorMapping = 
		new XUIBindProperty<Map<String,Color>>("colorMapping", this, Map.class);
	
	/**
	 * The sql attribute where the values are stored
	 */
	private XUIBindProperty<String> sqlAttValues = new XUIBindProperty<String>(
			"sqlAttValues", this, String.class);
	
	private boolean usesDefaultColors = false;
	
	public void setUsesDefaultColors(boolean useDefault){
		this.usesDefaultColors = useDefault;
	}

	public boolean getUsesDefaultColors(){
		return this.usesDefaultColors;
	}
	

	@Override
	public void initComponent() {
		super.initComponent();

		if (this.getType() == null)
			throw new RuntimeException("Must declare the type of chart");
		String sql = this.getSql(); 
		
		if (!StringUtils.isEmpty(sql)){
			String category = getSqlAttCategory();
			String values = getSqlAttValues();
			if (StringUtils.isEmpty(category))
				throw new RuntimeException("Must declare SQL Column for category");
			if (StringUtils.isEmpty(values))
				throw new RuntimeException("Must declare SQL Column for values");
		}
		
		if (this.getType().equalsIgnoreCase(TYPE_CHART_FLASH)) {
			XUIRequestContext
					.getCurrentContext()
					.getScriptContext()
					.addInclude(XUIScriptContext.POSITION_HEADER, "openFlash",
							"js/swfobject.js");
		}
		// super.setId("chart");

	}

	/**
	 * @param sqlResultLimit
	 *            the sqlResultLimit to set
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
	 * @param resultExpr
	 *            the sqlResultLimit to set
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
	 * @param dataSetExpr
	 *            The expression that will be used to retrieve the data set
	 */
	public void setDataSet(String dataSetExpr) {
		this.dataSet.setExpressionText(dataSetExpr);
	}

	/**
	 * 
	 * Retrieves the expression that represents the source of the data set
	 * 
	 * @return The expression that will be used to retrieve the data set
	 */
	public PieDataSet getDataSet() {
		return this.dataSet.getEvaluatedValue();
	}

	/**
	 * 
	 * Retrieves the expression that represents the optional configuration
	 * options of this pie chart
	 * 
	 * @return The expression used to retrieve the configuration options
	 */
	public IPieChartConfiguration getConfigOptions() {
		return this.configOptions.getEvaluatedValue();
	}

	/**
	 * 
	 * Sets the chart configuration expression
	 * 
	 * @param chartConfExpr
	 */
	public void setConfigOptions(String chartConfExpr) {
		this.configOptions.setExpressionText(chartConfExpr);
	}

	/**
	 * 
	 * Sets the expression for the label of the chart
	 * 
	 * @param newLabelExpr
	 *            The label expression
	 */
	public void setLabel(String newLabelExpr) {
		this.label.setExpressionText(newLabelExpr);
	}

	/**
	 * 
	 * Return the label for the chart
	 * 
	 * @return The label to show with the chart
	 */
	public String getLabel() {
		return this.label.getEvaluatedValue();
	}

	/**
	 * 
	 * Sets the expression for inout data SQL
	 * 
	 * @param newSqlExpr
	 *            The SQL expression
	 */
	public void setSql(String newSqlExpr) {
		this.sql.setExpressionText(newSqlExpr);
	}

	/**
	 * 
	 * Return the SQL expression to retrieve input data
	 * 
	 * @return The SQL expression to retrieve to the data base
	 */
	public String getSql() {
		return this.sql.getEvaluatedValue();
	}

	/**
	 * 
	 * Sets the expression for the SQL Category Attribute
	 * 
	 * @param newSqlExpr
	 *            The SQL expression
	 */
	public void setSqlAttCategory(String newSqlExpr) {
		this.sqlAttCategory.setExpressionText(newSqlExpr);
	}

	/**
	 * 
	 * Return the SQL attribute which contains the category values
	 * 
	 * @return The SQL attribute which contains the category values
	 */
	public String getSqlAttCategory() {
		return this.sqlAttCategory.getEvaluatedValue();
	}

	/**
	 * 
	 * Sets the name of the values attribute in the SQL expression
	 * 
	 * @param newSqlAttValuesExpr
	 *            The name of the values attribute
	 */
	public void setSqlAttValues(String newSqlAttValuesExpr) {
		this.sqlAttValues.setExpressionText(newSqlAttValuesExpr);
	}

	/**
	 * 
	 * Get the name of the sql attribute for the values
	 * 
	 * @return
	 */
	public String getSqlAttValues() {
		return this.sqlAttValues.getEvaluatedValue();
	}

	/**
	 * 
	 * Sets the type of the chart (Image Based or Flash based)
	 * 
	 * @param newTypeExpr
	 *            The type expression
	 */
	public void setType(String newTypeExpr) {
		this.type.setExpressionText(newTypeExpr);
	}

	/**
	 * 
	 * Returns the type of the chart
	 * 
	 * @return A string with the value <code>IMG</code> or <code>FLASH</code>
	 * 
	 */
	public String getType() {
		return this.type.getEvaluatedValue();
	}

	/**
	 * 
	 * Sets the with (in pixels) of the PieChart
	 * 
	 * @param width
	 *            The value for the width of the chart (in pixels)
	 */
	public void setWidth(String newWidthExpr) {
		this.width.setExpressionText(newWidthExpr);
	}

	/**
	 * 
	 * Retrieves the width (in pixels) of the chart
	 * 
	 * @return The number of pixels for the width of the chart
	 */
	public Integer getWidth() {
		return this.width.getEvaluatedValue();
	}

	/**
	 * 
	 * Sets the height of the chart (in pixels)
	 * 
	 * @param newHeight
	 *            The number of pixels in height of the chart
	 */
	public void setHeight(String newHeightExpr) {
		this.height.setExpressionText(newHeightExpr);
	}

	/**
	 * 
	 * Get the height of the chart (in pixels)
	 * 
	 * @return The number of pixels for the height of the chart
	 */
	public Integer getHeight() {
		return this.height.getEvaluatedValue();
	}
	
	/**
	 * 
	 * Retrieve the labels map
	 * 
	 * @return a map between original labels and their formating
	 */
	public Map<String,String> getLabelsMap(){
		return labelsMap.getEvaluatedValue();
	}

	/**
	 * 
	 * Sets the labels map
	 * 
	 * @param labelsExpr
	 * 
	 */
	public  void setLabelsMap(String labelsExpr){
		this.labelsMap.setExpressionText(labelsExpr);
	}
	
	/**
	 * 
	 * Retrieves the array of parameters for the sql query
	 * 
	 * @return
	 */
	public Object[] getSqlParameters(){
		Object[] params = sqlParameters.getEvaluatedValue();
		if (params != null)
			return params;
		return new Object[0];
	}
	
	/**
	 * 
	 * Sets the parameters for the sql query
	 * 
	 * @param sqlParamExpr
	 */
	public void setSqlParameters(String sqlParamExpr){
		this.sqlParameters.setExpressionText(sqlParamExpr);
	}
	
	/**
	 * 
	 * Returns a mapping of labels and their respective columns
	 * 
	 * @return
	 */
	public Map<String,Color> getColorMapping(){
		Map<String,Color> colorMapping = this.colorMapping.getEvaluatedValue();
		if (colorMapping != null)
			return colorMapping;
		return new HashMap<String, Color>();
	}
	
	/**
	 * 
	 * Sets
	 * 
	 * @param colorMappingExpr
	 */
	public void setColorMapping(String colorMappingExpr){
		this.colorMapping.setExpressionText(colorMappingExpr);
	}
	
	@Override
	public void preRender() {
	}
	
	

	@Override
	public Object saveState() {
		return super.saveState();
	}
	
	public DefaultPieDataset fillDataSetForImageChart(){
		DefaultPieDataset data = new DefaultPieDataset();

		PieDataSet setMine = null;
		
		Map<String,String> labels = getLabelsMap();
		
		// Check where the input data is coming from
		java.sql.Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet srs = null;
		if (getSql() != null || getBoql() != null) { 
			try {
				EboContext ctx = boApplication.currentContext().getEboContext();

				String sqlToExecute = "";
				if (getBoql() != null) {
					QLParser boqlParser = new QLParser();
					sqlToExecute = boqlParser.toSql(getBoql(), ctx);
					;
				}
				if (getSql() != null)
					sqlToExecute = getSql();

				Object[] parameters = getSqlParameters();
				conn = ctx.getConnectionData();
				pstmt = conn.prepareStatement(sqlToExecute,ResultSet.TYPE_SCROLL_SENSITIVE,
						ResultSet.CONCUR_READ_ONLY);
				for (int paramIndex = 0; paramIndex < parameters.length ; paramIndex++){
					pstmt.setObject(paramIndex + 1, parameters[paramIndex]);
				}
				srs = pstmt.executeQuery();
				int count = 0;
				while (srs.next()) {
					if (getResultLimit() > 0) {
						if (count >= getResultLimit())
							break;
					}
					String name = ChartUtils.getLabelOrReplacement(srs.getString(getSqlAttCategory()),labels);
					double value = srs.getDouble(getSqlAttValues());
					data.setValue(name, value);
					count++;
				}
			} catch (Exception e) {
				logger.warn(e);
			} finally {
				closeDatabaseResources( conn, pstmt, srs );
			}
		} else { // Fill the values from a
			setMine = (PieDataSet) getDataSet();
			Iterator<String> it = setMine.getCategories().iterator();
			while (it.hasNext()) {
				String curr = (String) it.next();
				curr = ChartUtils.getLabelOrReplacement(curr, labels);
				data.setValue(curr, setMine.getValue(curr));
			}
		}
		return data;
	}

	private void closeDatabaseResources( java.sql.Connection conn, PreparedStatement pstmt, ResultSet srs )
		{
		
		if (srs != null)
			try {
				srs.close();
			} catch ( SQLException e ) {
				e.printStackTrace();
			}
		if (pstmt != null)
			try {
				pstmt.close();
			} catch ( SQLException e ) {
				e.printStackTrace();
			}
		if (conn != null)
			try {
				conn.close();
			} catch ( SQLException e ) {
				e.printStackTrace();
			}
	}
	
	

	/**
	 * 
	 * Outputs the rendered chart (as image) to a stream
	 * 
	 * @param out
	 *            The {@link OutputStream} where to write the chart
	 * @param force
	 *            If the chart is of type flash, using the force parameter as
	 *            true will render the flash chart as an image
	 * 
	 */
	public void outputChartAsImageToStream(OutputStream out, boolean force) {

		try {

			// XUIRequestContext oRequestContext = getRequestContext();
			if (getType().equalsIgnoreCase(TYPE_CHART_IMG) || force) {
				DefaultPieDataset data = fillDataSetForImageChart();

				JFreeChart chart = ChartFactory.createPieChart(getLabel(),
						data, true, // legend
						true, // tooltips
						false); // URLs?

				applyChartDefaults(chart);
				applyChartConfigurations(chart);
				
				// Calculate Chart dimensions
				Integer width = getWidth();
				if (width == 0)
					width = DEFAULT_WIDTH;
				Integer height = getHeight();
				if (height == 0)
					height = DEFAULT_HEIGHT;

				// Render the chart
				ChartUtilities.writeChartAsPNG(out, chart, width, height);
			}

		} catch (IOException e) {
			logger.warn(e);
		}

	}
	
private void applyChartConfigurations(JFreeChart chart){
		
		if (getConfigOptions() != null) {
			PiePlot plot = (PiePlot) chart.getPlot();
			IPieChartConfiguration chartConfigurations = (IPieChartConfiguration) getConfigOptions();
			if (chartConfigurations != null) {
				if (chartConfigurations.getBackgroundColour() != null) {
					Color color = chartConfigurations
							.getBackgroundColour();
					plot.setBackgroundPaint(color);
				}

				if (!chartConfigurations.showLabels())
					plot.setLabelGenerator(null);

				if (chartConfigurations.getColours() != null) {
					Color[] colorArray = chartConfigurations
							.getColours();
					int index = 0;
					for (Color p : colorArray) {
						String val = (String) plot.getDataset().getKey(
								index);
						plot.setSectionPaint(val, p);
						index++;
					}
				} else {
					Color[] colorArray = ChartUtils.DEFAULT_COLORS;
					int index = 0;
					for (Color p : colorArray) {
						String val = (String) plot.getDataset().getKey(
								index);
						plot.setSectionPaint(val, p);
						index++;
					}
				}

				if (!chartConfigurations.showChartTitle())
					chart.setTitle("");

				if (chartConfigurations.getTooltipString() != null) {
					String expression = chartConfigurations
							.getTooltipString();
					expression = expression.replace("$key", "{0}");
					expression = expression.replace("$val", "{1}");
					expression = expression.replace("$percent", "{2}");
					PieSectionLabelGenerator generatorAlternative = new StandardPieSectionLabelGenerator(
							expression);
					plot.setLabelGenerator(generatorAlternative);
				}

			}

		}
	}
	
	private void applyChartDefaults(JFreeChart chart){
		chart.setBackgroundPaint(Color.white);
		chart.setBorderVisible(false);
		PiePlot plot = (PiePlot) chart.getPlot();

		// CHanges background of labels to white
		plot.setLabelBackgroundPaint(new Color(255, 255, 255));

		// Changes background color to white
		plot.setBackgroundPaint(new Color(255, 255, 255));

		// Mete o border a false
		plot.setOutlineVisible(false);

		// Label expression
		PieSectionLabelGenerator generator = new StandardPieSectionLabelGenerator(
				" {0} has {1} ({2})");
		plot.setLabelGenerator(generator);
	}

	/**
	 * 
	 * The class that renders the Graph in the Viewer (depending on the type,
	 * uses HTML+Servlet to render the image or HTML+Flash)
	 * 
	 * @author Pedro Pereira
	 * 
	 */
	public static class XEOHTMLRenderer extends XUIRenderer implements
			XUIRendererServlet {

		/*
		 * (non-Javadoc)
		 * 
		 * Used for the Image Based Graph, implements a servlet service so that
		 * the HTML tag <img src="URL"/> can be used
		 * 
		 * @see
		 * netgest.bo.xwc.framework.XUIRendererServlet#service(javax.servlet
		 * .ServletRequest, javax.servlet.ServletResponse,
		 * netgest.bo.xwc.framework.components.XUIComponentBase)
		 */
		@Override
		public void service(ServletRequest oRequest, ServletResponse oResponse,
				XUIComponentBase oComp) throws IOException {
			PieChart component = (PieChart) oComp;
			OutputStream out = oResponse.getOutputStream();

			if (component.getType().equalsIgnoreCase(TYPE_CHART_IMG)) {
				component.outputChartAsImageToStream(out, false);
			} else if (component.getType().equalsIgnoreCase(TYPE_CHART_FLASH)) {
				// Create the chart with the default settings
				Chart c = new Chart();
				jofc2.model.elements.PieChart chartElements = new jofc2.model.elements.PieChart();

				applyFlashChartDefaults(c, chartElements, component);
				applyFlashChartConfigurations(c, chartElements, component);
				
				// Fill the data from the SQL query or the DataSet
				List<String> categoriesList = new Vector<String>();
				generateDataSetForFlash(component, chartElements, categoriesList);

				// Add the colors:
				List<String> colors = new Vector<String>();
				int index = 0;
				// Add the colors
				Color[] arrayColors;
				if (component.getUsesDefaultColors())
					arrayColors = component.getConfigOptions().getColours();
				else
					arrayColors = ChartUtils.DEFAULT_COLORS;

				Map<String,Color> colorMap = component.getColorMapping();
				boolean hasColorMapping = colorMap.size() > 0;
				
				Iterator<String> it = categoriesList.iterator();
				while (it.hasNext()) {
					String category = it.next();
					Color currColor;
					if (index <= arrayColors.length - 1)
						currColor = arrayColors[index];
					else
						currColor = ChartUtils.getRandomDarkColor();
					
					if (hasColorMapping)
						currColor = colorMap.get(category);
					
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
		
		private void applyFlashChartDefaults(Chart c, jofc2.model.elements.PieChart chartElements, PieChart component){
			c.setBackgroundColour("#FFFFFF");
			c.setTitle(new Text(component.getLabel()));
			c.setXAxis(null);
			chartElements.setAlpha((float) 0.7);
			chartElements.setAnimate(true);
			chartElements.setGradientFill(false);
			chartElements
					.setTooltip("#val# of #total#<br>#percent# of 100%");

		}
		
		private void applyFlashChartConfigurations(Chart c, jofc2.model.elements.PieChart chartElements,
				PieChart component ){
			IPieChartConfiguration chartConfigurations = component.getConfigOptions();
			if (chartConfigurations != null) {
				if (chartConfigurations.getBackgroundColour() != null) {
					Color color = chartConfigurations.getBackgroundColour();
					String rgb = ChartUtils.ColorToRGB(color);
					c.setBackgroundColour(rgb);
				}

				if (!chartConfigurations.showLabels())
					chartElements.setNoLabels(true);

				if (chartConfigurations.getColours() != null) {
					component.setUsesDefaultColors(true);
				}

				if (!chartConfigurations.showChartTitle())
					c.setTitle(null);

				if (chartConfigurations.getTooltipString() != null) {
					String expression = chartConfigurations
							.getTooltipString();
					expression = expression.replace("$key", "#label#");
					expression = expression.replace("$val", "#val#");
					expression = expression
							.replace("$percent", "#percent#");
					chartElements.setTooltip(expression);
				}
			}
		}

		
		private void generateDataSetForFlash(PieChart component, jofc2.model.elements.PieChart chartElements, List<String> categoriesList){
			Map<String,String> labels = component.getLabelsMap();

			if (component.getSql() != null) { // Fill the values from a SQL
												// Query
				java.sql.Connection conn = null;
				ResultSet srs = null;
				PreparedStatement pstmt = null;
				try {

					EboContext ctx = boApplication.currentContext()
					.getEboContext();
					
					Object[] parameters = component.getSqlParameters();
					conn = ctx.getConnectionData();
					pstmt = conn.prepareStatement(component.getSql(),ResultSet.TYPE_SCROLL_SENSITIVE,
							ResultSet.CONCUR_READ_ONLY);
					for (int paramIndex = 0; paramIndex < parameters.length ; paramIndex++){
						pstmt.setObject(paramIndex + 1, parameters[paramIndex]);
					}
					srs = pstmt.executeQuery();
					
					int count = 0;
					while (srs.next()) {
						if (component.getResultLimit() > 0) {
							if (count >= component.getResultLimit())
								break;
						}
						String name = srs.getString(component
								.getSqlAttCategory());
						name = ChartUtils.getLabelOrReplacement(name, labels);
						double value = srs.getDouble(component
								.getSqlAttValues());
						chartElements.addSlice(value, name);
						categoriesList.add(name);
						count++;
					}
				} catch (Exception e) {
					logger.severe(e.getMessage(), e);
				} finally {
					try {
						if (srs != null)
							srs.close();
					} catch (Exception e) {
						logger.warn("Could not close resultset" , e);
					}
					try {
						if (pstmt != null)
							pstmt.close();
					} catch (Exception e) {
						logger.warn("Could not close statement" , e);
					}
					try {
						if (conn != null)
							conn.close();
					} catch (Exception e) {
						logger.warn("Could not close connection" , e);
					}

				}

			} else {
				PieDataSet setMine = (PieDataSet) component.getDataSet();
				Iterator<String> it = setMine.getCategories().iterator();

				while (it.hasNext()) {
					String curr = (String) it.next();
					curr = ChartUtils.getLabelOrReplacement(curr, labels);
					categoriesList.add(curr);
					chartElements.addSlice(setMine.getValue(curr), curr);
				}
			}
		}
		
		
		@Override
		public void encodeBegin(XUIComponentBase component) throws IOException {
			XUIResponseWriter w = getResponseWriter();
			PieChart oComp = (PieChart) component;

			Integer width = oComp.getWidth();
			if (width == 0)
				width = DEFAULT_WIDTH;
			Integer height = oComp.getHeight();
			if (height == 0)
				height = DEFAULT_HEIGHT;

			// If our chart is a flash based one
			if (oComp.getType().equalsIgnoreCase(TYPE_CHART_FLASH)) {
				StringBuilder b = new StringBuilder();
				String clientId = oComp.getClientId();

				String url = ComponentRenderUtils.getCompleteServletURL(
						getRequestContext(), component.getClientId());
				url = URLEncoder.encode(url, "UTF-8");

				b.append("  var flashvars = {};"
						+ "  var params = { wmode: \"transparent\" };"
						+ "var attributes = {}; ");
				b.append("swfobject.embedSWF(\"open-flash-chart.swf\", " + "\""
						+ component.getClientId() + "\", " + "\"" + width
						+ "\", " + "\"" + height + "\", "
						+ "\"9.0.0\",\"expressInstall.swf\", "
						+ "{\"data-file\": \"" + url
						+ "\"},flashvars,params,attributes);");

				w.getScriptContext().add(XUIScriptContext.POSITION_FOOTER,
						component.getClientId(), b);

				String reloadChart = ChartUtils.getReloadChartJSFunction(
						clientId, url, width, height, getRequestContext());

				w.getScriptContext().add(XUIScriptContext.POSITION_HEADER,
						"reloadChart", reloadChart);

				w.startElement(HTMLTag.DIV, oComp);
				w.writeAttribute(HTMLAttr.ID, component.getClientId(), null);
				w.endElement(HTMLTag.DIV);
				
			} else if (oComp.getType().equalsIgnoreCase(TYPE_CHART_IMG))// JFreeChart
																		// based
																		// graph
			{
				String url = ComponentRenderUtils.getServletURL(getRequestContext(),
						component.getClientId());
				url += "&ts="+System.currentTimeMillis();
				w.startElement(HTMLTag.DIV, oComp);
				w.writeAttribute(HTMLAttr.ID, component.getClientId(), null);
					w.startElement(HTMLTag.IMG, oComp);
					w.writeAttribute(HTMLAttr.SRC, url, null);
					w.writeAttribute(HTMLAttr.WIDTH, width, null);
					w.writeAttribute(HTMLAttr.HEIGHT, height, null);
					w.endElement(HTMLTag.IMG);
				w.endElement(HTMLTag.DIV);	
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
