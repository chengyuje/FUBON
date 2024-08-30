package com.systex.jbranch.app.server.fps.cmmgr016;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 報表內容設定 Output
 *
 * @author Eli
 * @date 20180727
 */
@Component
@Scope("request")
public class CMMGR016OutputVO {
    private List resultList;
    private List codeData;

    public List getResultList() {
        return resultList;
    }

    public void setResultList(List resultList) {
        this.resultList = resultList;
    }

    public List getCodeData() {
        return codeData;
    }

    public void setCodeData(List codeData) {
        this.codeData = codeData;
    }
}
