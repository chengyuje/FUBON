package com.systex.jbranch.platform.common.report.generator.jfreechart;

import java.awt.Color;
import java.awt.Font;
import java.awt.TexturePaint;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.systex.jbranch.platform.common.errHandle.JBranchException;

public class Line extends Chart {

	private XYSeriesCollection dataset = new XYSeriesCollection();
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
		// create the chart...
        final JFreeChart chart = ChartFactory.createXYLineChart(
    		title,      // chart title
    		xTitle,                      // x axis label
    		yTitle,                      // y axis label
            dataset,                  // data
            orientation,				//方向
            isLegend(),                     // include legend
            true,                     // tooltips
            false                     // urls
        );

        chart.setBackgroundPaint(Color.white);

        TextTitle txtTitle = chart.getTitle();
		txtTitle.setFont(getDefaultTitleFont());
        final XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint(Color.lightGray);
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);
        plot.setNoDataMessage(getNoData());
        
        final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesLinesVisible(0, false);
        renderer.setSeriesShapesVisible(1, false);
        plot.setRenderer(renderer);
        plot.getDomainAxis().setLabelFont(getDefaultPlotFont());
		plot.getRangeAxis().setLabelFont(getDefaultPlotFont());
		plot.setNoDataMessage(getNoData());

        final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        if(chart.getLegend() != null){
			chart.getLegend().setItemFont(getDefaultPlotFont());
		}
        
		return save(chart, path, width, height);
	}
	
	/**
	 * @param list 資料來源，內容需為Map<String, Object>
	 * @param cateName 折線圖種類
	 * @param xKey 圖表x軸值
	 * @param yKey 圖表y軸值
	 * @throws JBranchException
	 */
	public void addLineDataset(List list, String cateName, String xKey, String yKey) throws JBranchException{
		XYSeries series = new XYSeries(cateName);
		Map<String, Object> row = null;
		Number xNum = null;
		Number yNum = null;
		for (int i = 0; i < list.size(); i++) {
			row = (Map) list.get(i);
			try{
				xNum = (Number) row.get(xKey);
			}catch(Exception e){
				throw new JBranchException("第" + (i + 1) + "列，欄位[" + xKey + "]需為數字");
			}
			try{
				yNum = (Number) row.get(yKey);
			}catch(Exception e){
				throw new JBranchException("第" + (i + 1) + "列，欄位[" + yKey + "]需為數字");
			}
			series.add(xNum, yNum);
		}
		dataset.addSeries(series);
	}
	
	public static void main(String[] args) throws Exception{
		List<Map<String, Object>> list = new ArrayList();
		Map row1 = new HashMap();
		row1.put("col1", 4);
		row1.put("col2", 2);
		row1.put("col3", 3);
		Map row2 = new HashMap();
		row2.put("col1", 2);
		row2.put("col2", 4);
		row2.put("col3", 1);
		list.add(row1);
		list.add(row2);
		
		
		
		Line line = new Line();
		line.addLineDataset(list, "種類A", "col1", "col3");
		line.addLineDataset(list, "種類B", "col2", "col3");

		System.out.println(line.create("標題", 370, 300, "D:\\"));
	}
}
