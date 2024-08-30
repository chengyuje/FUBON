package com.systex.jbranch.app.server.fps.ins300;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class INS300OutputVO extends PagingOutputVO{
	public INS300OutputVO(){}
	private List<Map<String, Object>> Ins_outputList;

	public void setIns_outputList(List Ins_outputList){
		this.Ins_outputList = Ins_outputList;
	}

	public List<Map<String, Object>> getIns_outputList(){
		return Ins_outputList;
	}
}
