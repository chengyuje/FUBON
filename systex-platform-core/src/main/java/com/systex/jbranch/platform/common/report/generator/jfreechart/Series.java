package com.systex.jbranch.platform.common.report.generator.jfreechart;

public class Series {
	
	private String[] cates;
	private Number[] values;
	
	/**
	 * @return 取得種類陣列
	 */
	public String[] getCates() {
		return cates;
	}
	/**
	 * @param cates 指定種類陣列
	 */
	public void setCates(String[] cates) {
		this.cates = cates;
	}
	/**
	 * @return 取得值陣列
	 */
	public Number[] getValues() {
		return values;
	}
	/**
	 * @param values 指定值陣列
	 */
	public void setValues(Number[] values) {
		this.values = values;
	}
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("cate{");
		if(cates != null){
			for (int i = 0; i < cates.length; i++) {
				sb.append(cates[i]);
				if(i < cates.length - 1){
					sb.append(",");
				}
			}
		}
		sb.append("}values{");
		if(values != null){
			for (int i = 0; i < values.length; i++) {
				sb.append(values[i]);
				if(i < values.length - 1){
					sb.append(",");
				}
			}
		}
		sb.append("}");
		return sb.toString();
	}
	
}
