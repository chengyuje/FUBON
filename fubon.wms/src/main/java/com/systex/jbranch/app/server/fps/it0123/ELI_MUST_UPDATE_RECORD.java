package com.systex.jbranch.app.server.fps.it0123;

import java.math.BigDecimal;

/**
 * 上線必要更新功能 VO
 */
public class ELI_MUST_UPDATE_RECORD {
    private BigDecimal SEQ;   // 序號
    private String SNAP_DATE; // 資料日期
    private String REDMINE;   // Redmine 單號
    private String PRINCIPAL; // 負責人
    private String CLIENT;    // 提出問題人
    private String TYPE;      // 類型
    private String FILE_NAME; // 上傳檔案的 UUID 名稱
    private String REAL_NAME; // 配合事項與附件檔名
    private String MEMO;      // 簡述
    private String CREATOR;   // 建立者
    private String MODIFIER;  // 最後更新者
    private String PREV_FILE_NAME; // 原始附件檔名（用來在更新動作時，刪除原始附件檔案）

    public String getSNAP_DATE() {
        return SNAP_DATE;
    }

    public void setSNAP_DATE(String SNAP_DATE) {
        this.SNAP_DATE = SNAP_DATE;
    }

    public String getREDMINE() {
        return REDMINE;
    }

    public void setREDMINE(String REDMINE) {
        this.REDMINE = REDMINE;
    }

    public String getPRINCIPAL() {
        return PRINCIPAL;
    }

    public void setPRINCIPAL(String PRINCIPAL) {
        this.PRINCIPAL = PRINCIPAL;
    }

    public String getCLIENT() {
        return CLIENT;
    }

    public void setCLIENT(String CLIENT) {
        this.CLIENT = CLIENT;
    }

    public String getTYPE() {
        return TYPE;
    }

    public void setTYPE(String TYPE) {
        this.TYPE = TYPE;
    }

    public String getFILE_NAME() {
        return FILE_NAME;
    }

    public void setFILE_NAME(String FILE_NAME) {
        this.FILE_NAME = FILE_NAME;
    }

    public String getMEMO() {
        return MEMO;
    }

    public void setMEMO(String MEMO) {
        this.MEMO = MEMO;
    }

    public String getREAL_NAME() {
        return REAL_NAME;
    }

    public void setREAL_NAME(String REAL_NAME) {
        this.REAL_NAME = REAL_NAME;
    }
    public String getCREATOR() {
        return CREATOR;
    }

    public void setCREATOR(String CREATOR) {
        this.CREATOR = CREATOR;
    }

    public String getMODIFIER() {
        return MODIFIER;
    }

    public void setMODIFIER(String MODIFIER) {
        this.MODIFIER = MODIFIER;
    }

    public BigDecimal getSEQ() {
        return SEQ;
    }

    public void setSEQ(BigDecimal SEQ) {
        this.SEQ = SEQ;
    }

    public String getPREV_FILE_NAME() {
        return PREV_FILE_NAME;
    }

    public void setPREV_FILE_NAME(String PREV_FILE_NAME) {
        this.PREV_FILE_NAME = PREV_FILE_NAME;
    }
}
