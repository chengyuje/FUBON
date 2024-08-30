package com.systex.jbranch.app.server.fps.prd150;

import java.util.List;

import com.systex.jbranch.app.server.fps.sot701.FC032675DataVO;
import com.systex.jbranch.app.server.fps.sot701.FP032675DataVO;
import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class PRD150OutputVO extends PagingOutputVO {
	private String si_name;
	private List resultList;
	private FC032675DataVO fc032675DataVO;
	private FP032675DataVO fp032675DataVO;
	
	
	public String getSi_name() {
		return si_name;
	}
	public void setSi_name(String si_name) {
		this.si_name = si_name;
	}	
	public List getResultList() {
		return resultList;
	}
	public void setResultList(List resultList) {
		this.resultList = resultList;
	}
	public FC032675DataVO getFc032675DataVO() {
		return fc032675DataVO;
	}
	public void setFc032675DataVO(FC032675DataVO fc032675DataVO) {
		this.fc032675DataVO = fc032675DataVO;
	}
	public FP032675DataVO getFp032675DataVO() {
		return fp032675DataVO;
	}
	public void setFp032675DataVO(FP032675DataVO fp032675DataVO) {
		this.fp032675DataVO = fp032675DataVO;
	}
	
}
