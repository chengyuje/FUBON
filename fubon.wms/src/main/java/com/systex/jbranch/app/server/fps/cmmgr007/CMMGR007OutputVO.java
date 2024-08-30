package com.systex.jbranch.app.server.fps.cmmgr007;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CMMGR007OutputVO extends PagingOutputVO {

    public CMMGR007OutputVO() {
    }

    private String resultType;
    private List resultList;

    public String getResultType() {
        return resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }

    public List getResultList() {
        return resultList;
    }

    public void setResultList(List resultList) {
        this.resultList = resultList;
    }

}
