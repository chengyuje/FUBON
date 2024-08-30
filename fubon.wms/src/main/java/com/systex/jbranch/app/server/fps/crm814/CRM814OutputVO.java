package com.systex.jbranch.app.server.fps.crm814;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.systex.jbranch.app.server.fps.sot701.FC032675DataVO;
import com.systex.jbranch.fubon.commons.esb.vo.fc032671.FC032671OutputVO;
import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class CRM814OutputVO extends PagingOutputVO {
	private HashMap<String, String> results;
	private List resultList;

	public HashMap<String, String> getResults() {
		return results;
	}

	public void setResults(HashMap<String, String> results) {
		this.results = results;
	}

	public List getResultList() {
		return resultList;
	}

	public void setResultList(List resultList) {
		this.resultList = resultList;
	}

	public void setResultList(ArrayList resultList) {
		this.resultList = resultList;
	}

}
