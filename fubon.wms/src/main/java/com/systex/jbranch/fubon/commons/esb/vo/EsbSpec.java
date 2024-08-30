package com.systex.jbranch.fubon.commons.esb.vo;

/**
 * ESB 電文規格
 * 因應有一些電文有獨特的規則，這些特例將在這裡做處理。
 **/
public abstract class EsbSpec {
    /** 上行電文 VO，處理對象為 Esb.java request **/
    protected ESBUtilInputVO request;
    /** 下行電文 VO，處理對象為 Esb.java response **/
    protected ESBUtilOutputVO txData;
    /** 下行電文的 TxHead.HRETRN，ESB 使用這個欄位來判斷是否要繼續上送 C:繼續 E:結束 **/
    protected String HRETRN;
    /** 下行如果有錯誤，為下行電文的 TxBody **/
    protected ESBErrorVO errorVO;
    /** 下行如果有錯誤，為下行電文的 TxHead.HERRID **/
    protected String HERRID;

    protected boolean isMultiple = false; // 預設電文為單次發送
    protected boolean hasCustomErrorProcess = false; // 預設沒有自訂錯誤處理

    public ESBUtilInputVO getRequest() {
        return request;
    }

    public void setRequest(ESBUtilInputVO request) {
        this.request = request;
    }

    public ESBUtilOutputVO getTxData() {
        return txData;
    }

    public void setTxData(ESBUtilOutputVO txData) {
        this.txData = txData;
    }

    public ESBErrorVO getErrorVO() {
        return errorVO;
    }

    public void setErrorVO(ESBErrorVO errorVO) {
        this.errorVO = errorVO;
    }

    public String getHERRID() {
        return HERRID;
    }

    public void setHERRID(String HERRID) {
        this.HERRID = HERRID;
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

    public String getHRETRN() {
        return HRETRN;
    }

    public void setHRETRN(String HRETRN) {
        this.HRETRN = HRETRN;
    }

    public abstract void process() throws Exception;
    
    public  void clear() throws Exception {
    	this.txData = null;
    };

}
