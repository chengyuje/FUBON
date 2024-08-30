package com.systex.jbranch.app.server.fps.kyc220;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

import java.util.List;

public class KYC220OutputVO extends PagingOutputVO {
    private List resultList;
    private List rlVersionList;
    private List rlrVersionList;
    private List cValList;
    private List wValList;
    private List cwRLList;
    private List flwList;

    public List getResultList() {
        return resultList;
    }
    public void setResultList(List resultList) {
        this.resultList = resultList;
    }

    public List getRlVersionList() { return rlVersionList; }
    public void setRlVersionList(List rlVersionList) { this.rlVersionList = rlVersionList; }

    public List getFlwList() { return flwList; }
    public void setFlwList(List flwList) { this.flwList = flwList; }
	public List getRlrVersionList() {
		return rlrVersionList;
	}
	public void setRlrVersionList(List rlrVersionList) {
		this.rlrVersionList = rlrVersionList;
	}
	public List getcValList() {
		return cValList;
	}
	public void setcValList(List cValList) {
		this.cValList = cValList;
	}
	public List getwValList() {
		return wValList;
	}
	public void setwValList(List wValList) {
		this.wValList = wValList;
	}
	public List getCwRLList() {
		return cwRLList;
	}
	public void setCwRLList(List cwRLList) {
		this.cwRLList = cwRLList;
	}
    
}