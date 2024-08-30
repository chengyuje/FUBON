package com.systex.jbranch.platform.common.report.generator.jfreechart;

import java.awt.TexturePaint;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.Dataset;

import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.report.generator.DataSetCalc;

public class Bar extends Chart{

	private String categoryString = "種類";
	private String valueString = "值";
	private PlotOrientation orientation = PlotOrientation.VERTICAL;
	private Map<String, Series> dataMap = new HashMap<String, Series>();
	private BarRenderer renderer = new BarRenderer();
	/**
	 * @return 取得種類標題
	 */
	public String getCategoryString() {
		return categoryString;
	}

	/**
	 * @param categoryString 指定種類標題
	 */
	public void setCategoryString(String categoryString) {
		this.categoryString = categoryString;
	}

	/**
	 * @return 取得值標題
	 */
	public String getValueString() {
		return valueString;
	}

	/**
	 * @param valueString 指定值標題
	 */
	public void setValueString(String valueString) {
		this.valueString = valueString;
	}

	/**
	 * @return 取得圖表方向
	 */
	public PlotOrientation getOrientation() {
		return orientation;
	}

	/**
	 * @param orientation 指定圖表方向
	 */
	public void setOrientation(PlotOrientation orientation) {
		this.orientation = orientation;
	}

	/**
	 * @return 取得圖表表示方式
	 */
	public BarRenderer getRenderer() {
		return renderer;
	}

	/**
	 * @param 指定圖表表示方式(StackedBarRenderer、BarRenderer)
	 */
	public void setRenderer(BarRenderer renderer) {
		this.renderer = renderer;
	}

	/* (non-Javadoc)
	 * @see com.systex.jbranch.platform.common.report.generator.jfreechart.Chart#create(java.lang.String, int, int, java.lang.String)
	 */
	@Override
	public String create(String title, int width, int height, String path) throws IOException{
		Dataset dataset = this.getDataSet();
		JFreeChart chart = ChartFactory.createBarChart(title, // chart
				// title
				categoryString, // domain axis label
				valueString, // range axis label
				(CategoryDataset) dataset, // data
				orientation, // orientation
				isLegend(), // include legend
				true, // tooltips?
				false // URLs?
				);
		TextTitle txtTitle = chart.getTitle();
		txtTitle.setFont(getDefaultTitleFont());
		CategoryPlot plot = chart.getCategoryPlot();

		plot.setRenderer(renderer);
		renderer.setDrawBarOutline(false);


		plot.getDomainAxis().setLabelFont(getDefaultPlotFont());
		plot.getRangeAxis().setLabelFont(getDefaultPlotFont());
		plot.setNoDataMessage(getNoData());
		if(chart.getLegend() != null){
			chart.getLegend().setItemFont(getDefaultPlotFont());
		}
	    
	    CategoryAxis categoryaxis = plot.getDomainAxis();
    	categoryaxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
    	categoryaxis.setTickLabelFont(getDefaultPlotFont());
		
		List<TexturePaint> texturePaints = getTexturePaints();

		try {
			for (int j = 0; j < texturePaints.size(); j++) {
				renderer.setSeriesPaint(j, texturePaints.get(j));
			}
		} catch (Exception e) {
			//ignore
		}
		renderer.setShadowVisible(false);
		renderer.setBarPainter(new StandardBarPainter());
		return save(chart, path, width, height);
	}
	
	/**
	 * @param category 圖表種類
	 * @param series 圖表資料
	 */
	public void addBarDataset(String category, Series series){
		dataMap.put(category, series);
	}
	
	/**
	 * 將來源資料依指定的colNames與colKeys統計。
	 * @param list 資料來源，內容需為Map<String, Object>
	 * @param category 種類
	 * @param colNames 欄位名稱陣列
	 * @param colKeys 取欄位值key陣列
	 * @throws JBranchException
	 */
	public void addBarDataset(List list, String category, String[] colNames, String[] colKeys) throws JBranchException{
		
		DataSetCalc dataSet = new DataSetCalc();
		
		for (int i = 0; i < list.size(); i++) {
			Map<String, Object> row = (Map<String, Object>) list.get(i);
			for (int j = 0; j < colNames.length; j++) {
				try{
					Object key = colNames[j];
					Object value = row.get(colKeys[j]);
					dataSet.put(key, value);
				}catch(Exception e){
					throw new JBranchException("第" + (i + 1) + "列，欄位[" + colNames[j] + "]需為數字");
				}
			}
		}

		this.addBarDataset(category, dataSet.getSeries());
	}
	
	/**
	 * @return 取得Dataset
	 */
	public CategoryDataset getDataSet() {
		
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		Iterator<String> it = dataMap.keySet().iterator();
		while(it.hasNext()){
			String key = it.next();
			Series seri = dataMap.get(key);

			String[] cates = seri.getCates();
			Number[] values = seri.getValues();
			for (int j = 0; j < values.length; j++) {
				dataset.addValue(values[j], cates[j], key);
			}
		}
		return dataset;
	}
	
	public static void main(String[] args) throws IOException, JBranchException{
		List<Map<String, Object>> list = new ArrayList();
		Map row1 = new HashMap();
		row1.put("col1", 8);
		row1.put("col2", 7);
		row1.put("col3", 1);
		Map row2 = new HashMap();
		row2.put("col1", 4);
		row2.put("col2", 2);
		row2.put("col3", 2);
		list.add(row1);
		list.add(row2);
		
		List<Map<String, Object>> list2 = new ArrayList();
		Map row3 = new HashMap();
		row3.put("col1", 2);
		row3.put("col2", 2);
		row3.put("col3", 2);
		Map row4 = new HashMap();
		row4.put("col1", 3);
		row4.put("col2", 6);
		row4.put("col3", 1);
		list2.add(row3);
		list2.add(row4);
		
		Bar bar = new Bar();

		bar.addBarDataset(list, "種類A", new String[]{"欄1", "欄2", "欄3"}, new String[]{"col1", "col2", "col3"});
		bar.addBarDataset(list2, "種類B", new String[]{"欄1", "欄2", "欄3"}, new String[]{"col1", "col2", "col3"});
		
		System.out.println(bar.create("標題", 370, 300, "D:\\"));
	}
}
