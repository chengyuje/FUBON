package com.systex.jbranch.app.server.fps.cam110;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class CAM110InputVO extends PagingInputVO{
	private List<Map<String, Object>> list;

	public List<Map<String, Object>> getList() {
		return list;
	}
	public void setList(List<Map<String, Object>> list) {
		this.list = list;
	}
}
