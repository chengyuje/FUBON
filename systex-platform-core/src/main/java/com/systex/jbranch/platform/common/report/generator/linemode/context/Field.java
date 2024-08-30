package com.systex.jbranch.platform.common.report.generator.linemode.context;



public class Field {

	private String lpi;
	private String cpi;
	private String row;
	private String col; 
	private StringBuffer data = new StringBuffer();
	private String dataType; //H(二倍高),W(二倍寬),Z(二倍高寬)
	private String maskType;	//遮罩種類(待定義)
	
	public String getLpi() {
		return lpi;
	}
	public void setLpi(String lpi) {
		this.lpi = lpi;
	}
	public String getCpi() {
		return cpi;
	}
	public void setCpi(String cpi) {
		this.cpi = cpi;
	}
	public String getRow() {
		return row;
	}
	public void setRow(String row) {
		this.row = row;
	}
	public String getCol() {
		return col;
	}
	public void setCol(String col) {
		this.col = col;
	}
	public String getData() {
		return data.toString();
	}
	public StringBuffer getDataStringBuffer(){
		return data;
	}
	public void setData(String data) {
		this.data.append(data);
	}
	public String getDataType() {
		return dataType;
	}
	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
	/**
	 * @return the maskType
	 */
	public String getMaskType() {
		return maskType;
	}
	/**
	 * @param maskType the maskType to set
	 */
	public void setMaskType(String maskType) {
		this.maskType = maskType;
	}
}
