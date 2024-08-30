package com.systex.jbranch.app.server.fps.cmmgr018;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Scope("request")
public class CMMGR018OutputVO extends PagingOutputVO {
    private List resultList;

    public List getResultList() {
        return resultList;
    }

    public void setResultList(List resultList) {
        this.resultList = resultList;
    }

}
