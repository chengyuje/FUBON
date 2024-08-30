package com.systex.jbranch.app.server.fps.exportfile;


import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class EXPORTFILEInputVO extends PagingInputVO{
	private String sql;
	private String type;

	public String getSql() {
		return sql;
	}
	public void setSql(String sql) {
		this.sql = sql;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	
}
