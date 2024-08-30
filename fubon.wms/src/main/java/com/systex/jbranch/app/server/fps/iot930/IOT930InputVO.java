package com.systex.jbranch.app.server.fps.iot930;

import java.sql.Date;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class IOT930InputVO extends PagingInputVO{

	private String CASE_ID;

	public String getCASE_ID() {
		return CASE_ID;
	}

	public void setCASE_ID(String cASE_ID) {
		CASE_ID = cASE_ID;
	}
	
	
}
