package com.systex.jbranch.ws.external.service.domain.insurance_status;

public class ProcessResult {
    private String status;          // 回傳狀態
    private String returnCode;      // 系統回傳代碼
    private String returnMessage;   // 系統回傳結果

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(String returnCode) {
        this.returnCode = returnCode;
    }

    public String getReturnMessage() {
        return returnMessage;
    }

    public void setReturnMessage(String returnMessage) {
        this.returnMessage = returnMessage;
    }
}
