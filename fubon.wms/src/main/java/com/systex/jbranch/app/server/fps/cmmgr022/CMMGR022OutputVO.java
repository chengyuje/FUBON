package com.systex.jbranch.app.server.fps.cmmgr022;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Scope("request")
public class CMMGR022OutputVO {
    private List txResult; // 基本資訊
    private String onMsg;   // 上行電文
    private String offMsg;  // 下行電文

    public String getOnMsg() {
        return onMsg;
    }

    public void setOnMsg(String onMsg) {
        this.onMsg = onMsg;
    }

    public String getOffMsg() {
        return offMsg;
    }

    public void setOffMsg(String offMsg) {
        this.offMsg = offMsg;
    }

    public List getTxResult() {
        return txResult;
    }

    public void setTxResult(List txResult) {
        this.txResult = txResult;
    }
}
