package com.systex.jbranch.app.server.fps.importfile;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class IMPORTFILEInputVO extends PagingInputVO{
	private String set_name;
	private String set_code;
	private String fileName;
	private List<Map<String, Object>> list;

	public String getSet_name() {
		return set_name;
	}
	public void setSet_name(String set_name) {
		this.set_name = set_name;
	}
	public String getSet_code() {
		return set_code;
	}
	public void setSet_code(String set_code) {
		this.set_code = set_code;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public List<Map<String, Object>> getList() {
		return list;
	}
	public void setList(List<Map<String, Object>> list) {
		this.list = list;
	}
	
}
