package com.systex.jbranch.platform.common.report.generator.linemode.context;

import java.util.List;
import java.util.Map;

public class Page {
	
	private String deviceId;
	private String printPrompt="";
	private String nextPageMsg = "";
	private String formfeed=PageContent.FORMFEE;
	private String lpi=PageContent.LPI;
	private String cpi=PageContent.CPI;
	private String paperSize="";
	private String writeMsr;
	private List<Field> fields;
	private double leftMargin;
	private double topMargin;
	
	public String getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	public String getPrintPrompt() {
		return printPrompt;
	}
	public void setPrintPrompt(String printPrompt) {
		this.printPrompt = printPrompt;
	}
	/**
	 * @return the nextPageMsg
	 */
	public String getNextPageMsg() {
		return nextPageMsg;
	}
	/**
	 * @param nextPageMsg the nextPageMsg to set
	 */
	public void setNextPageMsg(String nextPageMsg) {
		this.nextPageMsg = nextPageMsg;
	}
	public String getFormfeed() {
		return formfeed;
	}
	public void setFormfeed(String formfeed) {
		this.formfeed = formfeed;
	}
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
	public String getPaperSize() {
		return paperSize;
	}
	public void setPaperSize(String paperSize) {
		this.paperSize = paperSize;
	}
	
	/**
	 * @return the writeMsr
	 */
	public String getWriteMsr() {
		return writeMsr;
	}
	/**
	 * @param writeMsr the writeMsr to set
	 */
	public void setWriteMsr(String writeMsr) {
		this.writeMsr = writeMsr;
	}
	/**
	 * @return the fields
	 */
	public List<Field> getFields() {
		return fields;
	}
	/**
	 * @param fields the fields to set
	 */
	public void setFields(List<Field> fields) {
		this.fields = fields;
	}
	/**
	 * @return the leftMargin
	 */
	public double getLeftMargin() {
		return leftMargin;
	}
	/**
	 * @param leftMargin the leftMargin to set
	 */
	public void setLeftMargin(double leftMargin) {
		this.leftMargin = leftMargin;
	}
	/**
	 * @return the topMargin
	 */
	public double getTopMargin() {
		return topMargin;
	}
	/**
	 * @param topMargin the topMargin to set
	 */
	public void setTopMargin(double topMargin) {
		this.topMargin = topMargin;
	}
}
