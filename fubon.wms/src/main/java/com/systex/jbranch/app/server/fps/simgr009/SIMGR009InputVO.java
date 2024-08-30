package com.systex.jbranch.app.server.fps.simgr009;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class SIMGR009InputVO {
	
	public SIMGR009InputVO()
	{
	}
	private String cmbPtypeBuss;
	private String cmbOrderType;
	private String selectedParameter;
	private String varWorksType;
	private String varPtypeRange; 
	private String varCsvType;
	private Date tipWorksDate;	
	private List<Map<String,String>> adgParameter;
	private List csvData;
	private String csvFileName;
	private String selectedParameterlabel;
	
	public String getSelectedParameterlabel() {
		return selectedParameterlabel;
	}

	public void setSelectedParameterlabel(String selectedParameterlabel) {
		this.selectedParameterlabel = selectedParameterlabel;
	}

	public String getCsvFileName() {
		return csvFileName;
	}

	public void setCsvFileName(String csvFileName) {
		this.csvFileName = csvFileName;
	}

	public void setCmbPtypeBuss(String cmbPtypeBuss) {
		this.cmbPtypeBuss = cmbPtypeBuss;
	}

	public String getCmbPtypeBuss() {
		return cmbPtypeBuss;
	}

	public void setSelectedParameter(String selectedParameter) {
		this.selectedParameter = selectedParameter;
	}

	public String getSelectedParameter() {
		return selectedParameter;
	}

	public void setVarWorksType(String varWorksType) {
		this.varWorksType = varWorksType;
	}

	public String getVarWorksType() {
		return varWorksType;
	}

	public void setTipWorksDate(Date tipWorksDate) {
		this.tipWorksDate = tipWorksDate;
	}

	public Date getTipWorksDate() {
		return tipWorksDate;
	}

	public void setAdgParameter(List<Map<String,String>> adgParameter) {
		this.adgParameter = adgParameter;
	}

	public List<Map<String,String>> getAdgParameter() {
		return adgParameter;
	}

	public void setVarPtypeRange(String varPtypeRange) {
		this.varPtypeRange = varPtypeRange;
	}

	public String getVarPtypeRange() {
		return varPtypeRange;
	}

	public void setVarCsvType(String varCsvType) {
		this.varCsvType = varCsvType;
	}

	public String getVarCsvType() {
		return varCsvType;
	}

	public void setCsvData(List csvData) {
		this.csvData = csvData;
	}

	public List getCsvData() {
		return csvData;
	}

	public void setCmbOrderType(String cmbOrderType) {
		this.cmbOrderType = cmbOrderType;
	}

	public String getCmbOrderType() {
		return cmbOrderType;
	}

	

	


}
