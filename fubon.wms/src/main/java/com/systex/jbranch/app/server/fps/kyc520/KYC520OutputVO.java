package com.systex.jbranch.app.server.fps.kyc520;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

import java.util.List;

public class KYC520OutputVO extends PagingOutputVO {
    private List resultList;
    private List rlVersionList;
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
}