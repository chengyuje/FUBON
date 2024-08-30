package com.systex.jbranch.app.server.fps.appvo.pii;

import java.io.Serializable;

public class AFP940MkReportVO implements Serializable{
	public AFP940MkReportVO(){
		super();
	}
	private String updateStatus;	//異動狀態
	private String layer;			//階層
	private String layerType;		//該層分類
	private String rptFileID;		//41類市場報告檔案ID
	private String rptFileName;		//41類市場報告檔案名稱
	private String rptFileURL;		//41類市場報告檔案URL
	private String introduction;	//簡介
	
	public String getUpdateStatus() {
		return updateStatus;
	}
	public void setUpdateStatus(String updateStatus) {
		this.updateStatus = updateStatus;
	}
	public String getLayer() {
		return layer;
	}
	public void setLayer(String layer) {
		this.layer = layer;
	}
	public String getLayerType() {
		return layerType;
	}
	public void setLayerType(String layerType) {
		this.layerType = layerType;
	}
	public String getRptFileID() {
		return rptFileID;
	}
	public void setRptFileID(String rptFileID) {
		this.rptFileID = rptFileID;
	}
	public String getRptFileName() {
		return rptFileName;
	}
	public void setRptFileName(String rptFileName) {
		this.rptFileName = rptFileName;
	}
	public String getRptFileURL() {
		return rptFileURL;
	}
	public void setRptFileURL(String rptFileURL) {
		this.rptFileURL = rptFileURL;
	}
	public String getIntroduction() {
		return introduction;
	}
	public void setIntroduction(String introduction) {
		this.introduction = introduction;
	}
}
