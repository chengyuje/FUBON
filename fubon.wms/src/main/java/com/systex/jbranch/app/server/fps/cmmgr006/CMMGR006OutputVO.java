package com.systex.jbranch.app.server.fps.cmmgr006;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Scope("request")
public class CMMGR006OutputVO {
    /**
     * 參數種類資料
     **/
    private List paramData;

    public List getParamData() {
        return paramData;
    }

    public void setParamData(List paramData) {
        this.paramData = paramData;
    }
}
