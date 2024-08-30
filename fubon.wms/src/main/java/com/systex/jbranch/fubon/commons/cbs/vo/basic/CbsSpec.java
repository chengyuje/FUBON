package com.systex.jbranch.fubon.commons.cbs.vo.basic;

import com.systex.jbranch.fubon.commons.cbs.service_platform.axis.SoapService;

/**
 * CBS 電文規格
 * 因應每隻電文邏輯差異性大，將電文的相關邏輯（上送、下傳，多筆，錯誤處理）抽離出來。
 **/
public abstract class CbsSpec {
    /** 上行電文 VO，處理對象為 Cbs.java request **/
    protected CBSUtilInputVO request;
    /** 下行電文 VO，處理對象為 Cbs.java response **/
    protected CBSUtilOutputVO txData;
    /** 上行 Context 物件，處理對象為 Cbs.java SoapService before sending **/
    protected SoapService txUpObject;
    /** 下行 Context 物件，處理對象為 Cbs.java SoapService after sending **/
    protected SoapService txDownObject;
    protected String errorCode;

    protected boolean isMultiple = false; // 預設電文為單次發送
    protected boolean hasCustomErrorProcess = false; // 預設沒有自訂錯誤處理
    protected CBSUtilOutputVO customTxData;

    public CBSUtilInputVO getRequest() {
        return request;
    }

    public void setRequest(CBSUtilInputVO request) {
        this.request = request;
    }

    public CBSUtilOutputVO getTxData() {
        return txData;
    }

    public void setTxData(CBSUtilOutputVO txData) {
        this.txData = txData;
    }

    public SoapService getTxUpObject() {
        return txUpObject;
    }

    public void setTxUpObject(SoapService txUpObject) {
        this.txUpObject = txUpObject;
    }

    public SoapService getTxDownObject() {
        return txDownObject;
    }

    public void setTxDownObject(SoapService txDownObject) {
        this.txDownObject = txDownObject;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public boolean isMultiple() {
        return isMultiple;
    }

    public void setMultiple(boolean multiple) {
        isMultiple = multiple;
    }

    public boolean isHasCustomErrorProcess() {
        return hasCustomErrorProcess;
    }

    public void setHasCustomErrorProcess(boolean hasCustomErrorProcess) {
        this.hasCustomErrorProcess = hasCustomErrorProcess;
    }

    public CBSUtilOutputVO getCustomTxData() {
        return customTxData;
    }

    public void setCustomTxData(CBSUtilOutputVO customTxData) {
        this.customTxData = customTxData;
    }

    public abstract void process() throws Exception;

}
