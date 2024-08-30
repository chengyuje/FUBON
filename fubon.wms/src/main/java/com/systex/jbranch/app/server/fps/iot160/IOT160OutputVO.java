package com.systex.jbranch.app.server.fps.iot160;

import java.math.BigDecimal;
import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class IOT160OutputVO extends PagingOutputVO {
	
	private List list;
	private StringBuffer message;
	
	public List getList() {
		return list;
	}
	public void setList(List list) {
		this.list = list;
	}
	public StringBuffer getMessage() {
		return message;
	}
	public void setMessage(StringBuffer message) {
		this.message = message;
	}
}