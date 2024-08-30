package com.systex.jbranch.app.server.fps.cmmgr004;

import java.util.List;

public class CMMGR004QueryOutputVO {

	public CMMGR004QueryOutputVO() {
	}

	private List resultList;
	private List lstTotalJob;
	private List lstChoiceJob;
	private List jobConList;
	
	public List getJobConList() {
		return jobConList;
	}
	public void setJobConList(List jobConList) {
		this.jobConList = jobConList;
	}
	public List getResultList() {
		return resultList;
	}
	public void setResultList(List resultList) {
		this.resultList = resultList;
	}
	public List getLstTotalJob() {
		return lstTotalJob;
	}
	public void setLstTotalJob(List lstTotalJob) {
		this.lstTotalJob = lstTotalJob;
	}
	public List getLstChoiceJob() {
		return lstChoiceJob;
	}
	public void setLstChoiceJob(List lstChoiceJob) {
		this.lstChoiceJob = lstChoiceJob;
	}

	
}
