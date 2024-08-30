package com.systex.jbranch.app.server.fps.crm651;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.fubon.commons.esb.vo.fc032151.FC032151OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.fp032151.FP032151OutputVO;
import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class CRM651OutputVO extends PagingOutputVO {
	
	private List resultList;
	private List resultList2;
	private FC032151OutputVO fc032151OutputVO;
	private FP032151OutputVO fp032151OutputVO;
	
	private List<Map<String, Object>> questionnaireList;
	
	public List<Map<String, Object>> getQuestionnaireList() {
		return questionnaireList;
	}

	public void setQuestionnaireList(List<Map<String, Object>> questionnaireList) {
		this.questionnaireList = questionnaireList;
	}

	public FP032151OutputVO getFp032151OutputVO() {
		return fp032151OutputVO;
	}

	public void setFp032151OutputVO(FP032151OutputVO fp032151OutputVO) {
		this.fp032151OutputVO = fp032151OutputVO;
	}

	public List getResultList() {
		return resultList;
	}

	public void setResultList(List resultList) {
		this.resultList = resultList;
	}

	public List getResultList2() {
		return resultList2;
	}

	public void setResultList2(List resultList2) {
		this.resultList2 = resultList2;
	}

	public FC032151OutputVO getFc032151OutputVO() {
		return fc032151OutputVO;
	}

	public void setFc032151OutputVO(FC032151OutputVO fc032151OutputVO) {
		this.fc032151OutputVO = fc032151OutputVO;
	}
}