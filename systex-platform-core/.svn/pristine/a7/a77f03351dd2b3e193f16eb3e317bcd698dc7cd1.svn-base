package com.systex.jbranch.platform.common.report.generator.jfreechart;

import java.awt.Font;
import java.awt.TexturePaint;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.DefaultXYZDataset;

import com.systex.jbranch.platform.common.errHandle.JBranchException;

public class Bubble extends Chart {
	
	private DefaultXYZDataset dataset = new DefaultXYZDataset();
	private String xTitle = "X";
	private String yTitle = "Y";
	private PlotOrientation orientation = PlotOrientation.VERTICAL;

	/**
	 * @return 取得x軸標題
	 */
	public String getxTitle() {
		return xTitle;
	}

	/**
	 * @param xTitle 指定x軸標題
	 */
	public void setxTitle(String xTitle) {
		this.xTitle = xTitle;
	}

	/**
	 * @return 取得y軸標題
	 */
	public String getyTitle() {
		return yTitle;
	}

	/**
	 * @param yTitle 指定y軸標題
	 */
	public void setyTitle(String yTitle) {
		this.yTitle = yTitle;
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
	
	/* (non-Javadoc)
	 * @see com.systex.jbranch.platform.common.report.generator.jfreechart.Chart#create(java.lang.String, int, int, java.lang.String)
	 */
	@Override
	public String create(String title, int width, int height, String path)
			throws Exception {

		JFreeChart chart = ChartFactory.createBubbleChart(
				  title, 
				  xTitle, 
				  yTitle, 
				  dataset, 
				  orientation,
				  isLegend(), true, false);
		TextTitle txtTitle = chart.getTitle();
		txtTitle.setFont(getDefaultTitleFont());
		
        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setForegroundAlpha(0.65f);
        plot.setNoDataMessage(getNoData());

        XYItemRenderer renderer = plot.getRenderer();

        List<TexturePaint> texturePaints = getTexturePaints();
		try {
			for (int j = 0; j < texturePaints.size(); j++) {
				renderer.setSeriesPaint(j, texturePaints.get(j));
			}
		} catch (Exception e) {
			//ignore
		}
         // increase the margins to account for the fact that the auto-range
         // doesn't take into account the bubble size...
        NumberAxis domainAxis = (NumberAxis) plot.getDomainAxis();
        domainAxis.setLowerMargin(0.5);
        domainAxis.setUpperMargin(0.5);
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setLowerMargin(0.5);
        rangeAxis.setUpperMargin(0.5);
		plot.getDomainAxis().setLabelFont(getDefaultPlotFont());
		plot.getRangeAxis().setLabelFont(getDefaultPlotFont());
		if(chart.getLegend() != null){
			chart.getLegend().setItemFont(getDefaultPlotFont());
		}
        
		return save(chart, path, width, height);
	}
	
	/**
	 * @param list 資料來源，內容需為Map<String, Object>
	 * @param cateName 氣泡圖種類
	 * @param xKey 圖表x軸值
	 * @param yKey 圖表y軸值
	 * @param sizeKey 氣泡尺寸
	 * @throws JBranchException
	 */
	public void addBubbleDataset(List list, String cateName, String xKey, String yKey, String sizeKey) throws JBranchException{

		Map<String, Object> row = null;
		int size = list.size();
		double[] xValue = new double[size];
		double[] yValue = new double[size];
		double[] sizeValue = new double[size];
		
		for (int i = 0; i < list.size(); i++) {
			row = (Map) list.get(i);
			try{
				xValue[i] = Double.parseDouble(row.get(xKey).toString());
			}catch(Exception e){
				throw new JBranchException("第" + (i + 1) + "列，欄位[" + xKey + "]需為數字");
			}
			try{
				yValue[i] = Double.parseDouble(row.get(yKey).toString());
			}catch(Exception e){
				throw new JBranchException("第" + (i + 1) + "列，欄位[" + yKey + "]需為數字");
			}
			try{
				sizeValue[i] = Double.parseDouble(row.get(sizeKey).toString());
			}catch(Exception e){
				throw new JBranchException("第" + (i + 1) + "列，欄位[" + sizeKey + "]需為數字");
			}
		}
		double[][] series = new double[][] {xValue, yValue, sizeValue};
		dataset.addSeries(cateName, series);
	}

	public static void main(String[] args) throws Exception{
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
		
		Bubble bubble = new Bubble();
		bubble.addBubbleDataset(list, "種類A", "col1", "col2", "col3");
		bubble.addBubbleDataset(list2, "種類B", "col1", "col2", "col3");
		System.out.println(bubble.create("標題", 370, 300, "D:\\"));
	}
}
