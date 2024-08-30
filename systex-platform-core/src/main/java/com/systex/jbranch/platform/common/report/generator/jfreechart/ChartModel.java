package com.systex.jbranch.platform.common.report.generator.jfreechart;

public class ChartModel {

	private int[] chartsId;
	private int[] piesId;
	private String[] sliceSizes;
	private String[] cates;
	private String[] titles;
	private int width = 0;
	private int height = 0;
	private Chart[] charts;

	/**
	 * @return 圖表Id陣列
	 */
	public int[] getChartsId() {
		return chartsId;
	}
	/**
	 * @param chartsId 指定圖表Id陣列
	 */
	public void setChartsId(int[] chartsId) {
		this.chartsId = chartsId;
	}
	/**
	 * @return 圖表陣列
	 */
	public Chart[] getCharts() {
		return charts;
	}
	/**
	 * @param charts 指定圖表陣列
	 */
	public void setCharts(Chart[] charts) {
		this.charts = charts;
	}
	
	/**
	 * @return 圖片標題
	 */
	public String[] getTitles() {
		return titles;
	}
	
	/**
	 * @param titles 指定圖示標題
	 */
	public void setTitles(String[] titles) {
		this.titles = titles;
	}
	
	@Deprecated
	public int[] getPiesId() {
		return piesId;
	}
	@Deprecated
	public void setPiesId(int[] piesId) {
		this.piesId = piesId;
	}
	
	@Deprecated
	public String[] getSliceSizes() {
		return sliceSizes;
	}
	
	@Deprecated
	public void setSliceSizes(String[] sliceSizes) {
		this.sliceSizes = sliceSizes;
	}
	
	@Deprecated
	public String[] getCates() {
		return cates;
	}
	
	@Deprecated
	public void setCates(String[] cates) {
		this.cates = cates;
	}
	
	@Deprecated
	public int getWidth() {
		return width;
	}
	
	@Deprecated
	public void setWidth(int width) {
		this.width = width;
	}
	
	@Deprecated
	public int getHeight() {
		return height;
	}
	
	@Deprecated
	public void setHeight(int height) {
		this.height = height;
	}
}
