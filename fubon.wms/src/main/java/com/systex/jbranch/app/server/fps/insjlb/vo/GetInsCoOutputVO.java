package com.systex.jbranch.app.server.fps.insjlb.vo;

import java.util.List;
import java.util.Map;

public class GetInsCoOutputVO {
	private List<Map<String , Object>> getInsCoList;
	/**	COM_ID		string	保險公司編號
	 *	COM_NAME	string	保險公司名	
	 */

	public List<Map<String , Object>> getGetInsCoList() {
		return getInsCoList;
	}

	public void setGetInsCoList(List<Map<String , Object>> getInsCoList) {
		this.getInsCoList = getInsCoList;
	}
}
