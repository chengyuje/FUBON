package com.systex.jbranch.app.server.fps.cmmgr017;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Scope("request")
public class CMMGR017OutputVO extends PagingOutputVO {
    private List msg; // 執行訊息
    private List pckInfo; // package資訊

    public List getMsg() {
        return msg;
    }

    public void setMsg(List msg) {
        this.msg = msg;
    }

    public List getPckInfo() {
        return pckInfo;
    }

    public void setPckInfo(List pckInfo) {
        this.pckInfo = pckInfo;
    }
}
