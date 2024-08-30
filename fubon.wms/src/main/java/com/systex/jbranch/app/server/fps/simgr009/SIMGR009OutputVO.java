package com.systex.jbranch.app.server.fps.simgr009;

import java.util.List;
import java.util.Map;

public class SIMGR009OutputVO {
	
	public SIMGR009OutputVO()
	{
	}
	private List<Map<String,Object>> lstParameter;
	private List<Map<String,Object>> adgParameter;
	

	public void setAdgParameter(List<Map<String,Object>> adgParameter) {
		this.adgParameter = adgParameter;
	}

	public List<Map<String,Object>> getAdgParameter() {
		return adgParameter;
	}

	public void setLstParameter(List<Map<String,Object>> lstParameter) {
		this.lstParameter = lstParameter;
	}

	public List<Map<String,Object>> getLstParameter() {
		return lstParameter;
	}




}
