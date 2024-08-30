package com.systex.jbranch.platform.common.report.generator.jfreechart;

import java.awt.Color;
import java.awt.Font;
import java.awt.TexturePaint;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.Dataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;

import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.report.generator.DataSetCalc;

public class Pie extends Chart{

	private Series series;
	
	@Deprecated
	public String create(DataSetCalc dataSet, String title, int width, int height, String path) throws IOException{
		DefaultPieDataset data = getDataSet(dataSet);
		JFreeChart chart = ChartFactory.createPieChart(title,  // 圖表標題
		data, 
		false, // 是否顯示圖例
		true,
		false
		);
		Font titleFont = new Font("新細明體", Font.PLAIN, 18);
		Font plotFont = new Font("新細明體", Font.PLAIN, 14);

		TextTitle txtTitle = chart.getTitle();
		txtTitle.setFont(titleFont);

		PiePlot pieplot = (PiePlot)chart.getPlot();

		pieplot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0}:{2}",
                NumberFormat.getNumberInstance(),
                new DecimalFormat("0.00%")));
		pieplot.setAutoPopulateSectionOutlinePaint(true);
        pieplot.setLabelFont(plotFont);
        pieplot.setOutlineVisible(false);
        pieplot.setLabelBackgroundPaint(Color.WHITE);
        pieplot.setLabelShadowPaint(Color.WHITE);
        pieplot.setShadowPaint(Color.WHITE);
        pieplot.setLabelOutlinePaint(Color.WHITE);
        pieplot.setSectionOutlinesVisible(true);
        pieplot.setBackgroundPaint(Color.WHITE);
        pieplot.setNoDataMessage(getNoData());

        List<TexturePaint> texturePaints = getTexturePaints();
		try {
			for (int j = 0; j < texturePaints.size(); j++) {
				String key = (String) data.getKey(j);
				pieplot.setSectionPaint(key, texturePaints.get(j));
				pieplot.setSectionOutlinePaint(key, Color.BLACK);
			}
		} catch (Exception e) {
			//ignore
		}
        pieplot.setLabelFont(plotFont);
        LegendTitle legend = chart.getLegend();
        if(legend != null){
        	legend.setItemFont(plotFont);
        }
        
        File f = new File(path + "tempPie_" + getNexSEQ() + ".jpg");
		ChartUtilities.saveChartAsPNG(f, chart, width, height);

		return f.getAbsolutePath();
	}

	@Deprecated
	public DefaultPieDataset getDataSet(DataSetCalc dataSet) {
		DefaultPieDataset dataset = new DefaultPieDataset();
		Iterator<Object> it = dataSet.getKeySet().iterator();
		while(it.hasNext()){
			Object key = it.next();
			Object value = dataSet.get(key);
			if(key instanceof String && value instanceof BigDecimal){
				dataset.setValue((String) key, (BigDecimal) value);
			}
		}
		return dataset;
	}
	
	/* (non-Javadoc)
	 * @see com.systex.jbranch.platform.common.report.generator.jfreechart.Chart#create(java.lang.String, int, int, java.lang.String)
	 */
	@Override
	public String create(String title, int width, int height, String path) throws IOException{
		setLegend(false);
		DefaultPieDataset dataset = getDataSet();
		JFreeChart chart = ChartFactory.createPieChart(title,  // 圖表標題
		(PieDataset) dataset, 
		isLegend(), // 是否顯示圖例
		true,
		false
		);
		Font titleFont = getDefaultTitleFont();
		Font plotFont = getDefaultPlotFont();

		if(chart.getLegend() != null){
			chart.getLegend().setItemFont(getDefaultPlotFont());
		}
		
		TextTitle txtTitle = chart.getTitle();
		txtTitle.setFont(titleFont);

		PiePlot pieplot = (PiePlot)chart.getPlot();

		pieplot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0}:{2}",
                NumberFormat.getNumberInstance(),
                new DecimalFormat("0.00%")));
		pieplot.setAutoPopulateSectionOutlinePaint(true);
        pieplot.setLabelFont(plotFont);
        pieplot.setOutlineVisible(false);
        pieplot.setLabelBackgroundPaint(Color.WHITE);
        pieplot.setLabelShadowPaint(Color.WHITE);
        pieplot.setShadowPaint(Color.WHITE);
        pieplot.setLabelOutlinePaint(Color.WHITE);
        pieplot.setSectionOutlinesVisible(true);
        pieplot.setBackgroundPaint(Color.WHITE);
        pieplot.setNoDataMessage(getNoData());

        List<TexturePaint> texturePaints = getTexturePaints();
		try {
			for (int j = 0; j < texturePaints.size(); j++) {
				pieplot.setSectionPaint(j, texturePaints.get(j));
				pieplot.setSectionOutlinePaint(j, Color.BLACK);
			}
		} catch (Exception e) {
			//ignore
		}
        pieplot.setLabelFont(plotFont);

		return save(chart, path, width, height);
	}
	
	/**
	 * @param series 指定資料內容
	 */
	public void addPieDataset(Series series){
		this.series = series;
	}
	
	/**
	 * 將來源資料依指定的colNames與colKeys統計。
	 * @param list 資料來源，內容需為Map<String, Object>
	 * @param colNames 欄位名稱陣列
	 * @param colKeys 取欄位值key陣列
	 * @throws JBranchException
	 */
	public void addPieDataset(List list, String[] colNames, String[] colKeys) throws JBranchException{
		
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

		this.addPieDataset(dataSet.getSeries());
	}

	private DefaultPieDataset getDataSet() {
		
		DefaultPieDataset dataset = new DefaultPieDataset();
		String[] cates = series.getCates();
		Number[] values = series.getValues();
		for (int j = 0; j < values.length; j++) {
			dataset.setValue(cates[j], values[j]);
		}
		return dataset;
	}
	
	public static void main(String[] args) throws IOException, JBranchException{
		List<Map<String, Number>> list = new ArrayList();
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
		
		Pie pie = new Pie();
		pie.addPieDataset(list, new String[]{"欄1", "欄2", "欄3"}, new String[]{"col1", "col2", "col3"});
		System.out.println(pie.create("標題", 370, 300, "D:\\"));
	}
}
