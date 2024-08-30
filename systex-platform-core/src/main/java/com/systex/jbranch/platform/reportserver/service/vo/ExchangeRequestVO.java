package com.systex.jbranch.platform.reportserver.service.vo;

import java.util.ArrayList;
import java.util.HashMap;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.systex.jbranch.platform.common.report.factory.ReportEngineType;
import com.systex.jbranch.platform.common.report.factory.ReportFormat;
import com.systex.jbranch.platform.reportserver.adapter.MapAdapter;
import com.systex.jbranch.platform.reportserver.adapter.ObjectAdapter;

public class ExchangeRequestVO {
	
	private String txnCode;
	private String reportId;
	private boolean async;
	private ReportEngineType reportEngineType;
	private ReportFormat reportFormat;
	private HashMap parameters = null;
	private Boolean merge;
	private String fileName = null;
	private String path = null;
	private HashMap<String, ArrayList> records = null;
	private HashMap<String, String> sqlMap = null;
	private Object otherParam = null;

	public String getTxnCode() {
		return txnCode;
	}
	public void setTxnCode(String txnCode) {
		this.txnCode = txnCode;
	}
	public String getReportId() {
		return reportId;
	}
	public void setReportId(String reportId) {
		this.reportId = reportId;
	}
	public boolean isAsync() {
		return async;
	}
	public void setAsync(boolean async) {
		this.async = async;
	}
	public Boolean getMerge() {
		return merge;
	}
	public void setMerge(Boolean merge) {
		this.merge = merge;
	}
	public ReportEngineType getReportEngineType() {
		return reportEngineType;
	}
	public void setReportEngineType(ReportEngineType reportEngineType) {
		this.reportEngineType = reportEngineType;
	}
	public ReportFormat getReportFormat() {
		return reportFormat;
	}
	public void setReportFormat(ReportFormat reportFormat) {
		this.reportFormat = reportFormat;
	}
	public HashMap getParameters() {
		return parameters;
	}
	public void setParameters(HashMap parameters) {
		this.parameters = parameters;
	}
	public boolean isMerge() {
		return merge;
	}
	public void setMerge(boolean merge) {
		this.merge = merge;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}

//	public void setRecords(Map<String, List> records) {
//		Iterator<String> it = records.keySet().iterator();
//		List recordKeyList = new ArrayList();
//		List recordValueList = new ArrayList();
//		while(it.hasNext()){
//			String key = it.next();
//			List value = records.get(key);
//			recordKeyList.add(key);
//			recordValueList.add(value);
//		}
//	}
	
	@WebMethod
	@XmlJavaTypeAdapter(MapAdapter.class) 
	public HashMap<String, ArrayList> getRecords() {
		return records;
	}
	
	@WebMethod
	public void setRecords(@WebParam @XmlJavaTypeAdapter(MapAdapter.class) HashMap<String, ArrayList> records) {
		this.records = records;
	}
	public HashMap<String, String> getSqlMap() {
		return sqlMap;
	}
	public void setSqlMap(HashMap<String, String> sqlMap) {
		this.sqlMap = sqlMap;
	}
	
	@WebMethod
	@XmlJavaTypeAdapter(ObjectAdapter.class)
	public Object getOtherParam() {
		return otherParam;
	}
	public void setOtherParam(@WebParam @XmlJavaTypeAdapter(ObjectAdapter.class) Object otherParam) {
		this.otherParam = otherParam;
	}
}
