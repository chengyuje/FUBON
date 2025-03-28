package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBSQM_CSM_MAINVO extends VOBase {

    /** identifier field */
    private com.systex.jbranch.app.common.fps.table.TBSQM_CSM_MAINPK comp_id;

    /** nullable persistent field */
    private String REGION_CENTER_ID;

    /** nullable persistent field */
    private String BRANCH_AREA_ID;

    /** nullable persistent field */
    private String BRANCH_NBR;

    /** nullable persistent field */
    private String EMP_ID;

    /** nullable persistent field */
    private String AO_CODE;

    /** nullable persistent field */
    private String QUESTION_DESC;

    /** nullable persistent field */
    private String CUST_NAME;

    /** nullable persistent field */
    private String MOBILE_NO;

    /** nullable persistent field */
    private String ANSWER;

    /** nullable persistent field */
    private String RESP_NOTE;

    /** nullable persistent field */
    private String TRADE_DATE;

    /** nullable persistent field */
    private String RESP_DATE;

    /** nullable persistent field */
    private String SEND_DATE;

    /** nullable persistent field */
    private String CAMPAIGN_ID;

    /** nullable persistent field */
    private String STEP_ID;

    /** nullable persistent field */
    private BigDecimal SEND_QUANTITY;

    /** nullable persistent field */
    private String SATISFACTION_O;

    /** nullable persistent field */
    private String SATISFACTION_W;

    /** nullable persistent field */
    private String IMPORTED;

    /** nullable persistent field */
    private BigDecimal QST_VERSION;

    /** nullable persistent field */
    private String PAPER_UUID;
    
    /** nullable persistent field */
    private String UHRM_YN;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBSQM_CSM_MAIN";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBSQM_CSM_MAINVO(com.systex.jbranch.app.common.fps.table.TBSQM_CSM_MAINPK comp_id, String REGION_CENTER_ID, String BRANCH_AREA_ID, String BRANCH_NBR, String EMP_ID, String AO_CODE, String QUESTION_DESC, String CUST_NAME, String MOBILE_NO, String ANSWER, String RESP_NOTE, String TRADE_DATE, String RESP_DATE, String SEND_DATE, String CAMPAIGN_ID, String STEP_ID, BigDecimal SEND_QUANTITY, String SATISFACTION_O, String SATISFACTION_W, String IMPORTED, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, BigDecimal QST_VERSION, String PAPER_UUID, Long version) {
        this.comp_id = comp_id;
        this.REGION_CENTER_ID = REGION_CENTER_ID;
        this.BRANCH_AREA_ID = BRANCH_AREA_ID;
        this.BRANCH_NBR = BRANCH_NBR;
        this.EMP_ID = EMP_ID;
        this.AO_CODE = AO_CODE;
        this.QUESTION_DESC = QUESTION_DESC;
        this.CUST_NAME = CUST_NAME;
        this.MOBILE_NO = MOBILE_NO;
        this.ANSWER = ANSWER;
        this.RESP_NOTE = RESP_NOTE;
        this.TRADE_DATE = TRADE_DATE;
        this.RESP_DATE = RESP_DATE;
        this.SEND_DATE = SEND_DATE;
        this.CAMPAIGN_ID = CAMPAIGN_ID;
        this.STEP_ID = STEP_ID;
        this.SEND_QUANTITY = SEND_QUANTITY;
        this.SATISFACTION_O = SATISFACTION_O;
        this.SATISFACTION_W = SATISFACTION_W;
        this.IMPORTED = IMPORTED;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.QST_VERSION = QST_VERSION;
        this.PAPER_UUID = PAPER_UUID;
        this.version = version;
    }

    /** default constructor */
    public TBSQM_CSM_MAINVO() {
    }

    /** minimal constructor */
    public TBSQM_CSM_MAINVO(com.systex.jbranch.app.common.fps.table.TBSQM_CSM_MAINPK comp_id) {
        this.comp_id = comp_id;
    }

    public com.systex.jbranch.app.common.fps.table.TBSQM_CSM_MAINPK getcomp_id() {
        return this.comp_id;
    }

    public void setcomp_id(com.systex.jbranch.app.common.fps.table.TBSQM_CSM_MAINPK comp_id) {
        this.comp_id = comp_id;
    }

    public String getREGION_CENTER_ID() {
        return this.REGION_CENTER_ID;
    }

    public void setREGION_CENTER_ID(String REGION_CENTER_ID) {
        this.REGION_CENTER_ID = REGION_CENTER_ID;
    }

    public String getBRANCH_AREA_ID() {
        return this.BRANCH_AREA_ID;
    }

    public void setBRANCH_AREA_ID(String BRANCH_AREA_ID) {
        this.BRANCH_AREA_ID = BRANCH_AREA_ID;
    }

    public String getBRANCH_NBR() {
        return this.BRANCH_NBR;
    }

    public void setBRANCH_NBR(String BRANCH_NBR) {
        this.BRANCH_NBR = BRANCH_NBR;
    }

    public String getEMP_ID() {
        return this.EMP_ID;
    }

    public void setEMP_ID(String EMP_ID) {
        this.EMP_ID = EMP_ID;
    }

    public String getAO_CODE() {
        return this.AO_CODE;
    }

    public void setAO_CODE(String AO_CODE) {
        this.AO_CODE = AO_CODE;
    }

    public String getQUESTION_DESC() {
        return this.QUESTION_DESC;
    }

    public void setQUESTION_DESC(String QUESTION_DESC) {
        this.QUESTION_DESC = QUESTION_DESC;
    }

    public String getCUST_NAME() {
        return this.CUST_NAME;
    }

    public void setCUST_NAME(String CUST_NAME) {
        this.CUST_NAME = CUST_NAME;
    }

    public String getMOBILE_NO() {
        return this.MOBILE_NO;
    }

    public void setMOBILE_NO(String MOBILE_NO) {
        this.MOBILE_NO = MOBILE_NO;
    }

    public String getANSWER() {
        return this.ANSWER;
    }

    public void setANSWER(String ANSWER) {
        this.ANSWER = ANSWER;
    }

    public String getRESP_NOTE() {
        return this.RESP_NOTE;
    }

    public void setRESP_NOTE(String RESP_NOTE) {
        this.RESP_NOTE = RESP_NOTE;
    }

    public String getTRADE_DATE() {
        return this.TRADE_DATE;
    }

    public void setTRADE_DATE(String TRADE_DATE) {
        this.TRADE_DATE = TRADE_DATE;
    }

    public String getRESP_DATE() {
        return this.RESP_DATE;
    }

    public void setRESP_DATE(String RESP_DATE) {
        this.RESP_DATE = RESP_DATE;
    }

    public String getSEND_DATE() {
        return this.SEND_DATE;
    }

    public void setSEND_DATE(String SEND_DATE) {
        this.SEND_DATE = SEND_DATE;
    }

    public String getCAMPAIGN_ID() {
        return this.CAMPAIGN_ID;
    }

    public void setCAMPAIGN_ID(String CAMPAIGN_ID) {
        this.CAMPAIGN_ID = CAMPAIGN_ID;
    }

    public String getSTEP_ID() {
        return this.STEP_ID;
    }

    public void setSTEP_ID(String STEP_ID) {
        this.STEP_ID = STEP_ID;
    }

    public BigDecimal getSEND_QUANTITY() {
        return this.SEND_QUANTITY;
    }

    public void setSEND_QUANTITY(BigDecimal SEND_QUANTITY) {
        this.SEND_QUANTITY = SEND_QUANTITY;
    }

    public String getSATISFACTION_O() {
        return this.SATISFACTION_O;
    }

    public void setSATISFACTION_O(String SATISFACTION_O) {
        this.SATISFACTION_O = SATISFACTION_O;
    }

    public String getSATISFACTION_W() {
        return this.SATISFACTION_W;
    }

    public void setSATISFACTION_W(String SATISFACTION_W) {
        this.SATISFACTION_W = SATISFACTION_W;
    }

    public String getIMPORTED() {
        return this.IMPORTED;
    }

    public void setIMPORTED(String IMPORTED) {
        this.IMPORTED = IMPORTED;
    }

    public BigDecimal getQST_VERSION() {
        return this.QST_VERSION;
    }

    public void setQST_VERSION(BigDecimal QST_VERSION) {
        this.QST_VERSION = QST_VERSION;
    }

    public String getPAPER_UUID() {
        return this.PAPER_UUID;
    }

    public void setPAPER_UUID(String PAPER_UUID) {
        this.PAPER_UUID = PAPER_UUID;
    }

    public String getUHRM_YN() {
		return UHRM_YN;
	}

	public void setUHRM_YN(String uHRM_YN) {
		UHRM_YN = uHRM_YN;
	}

	public void checkDefaultValue() {
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("comp_id", getcomp_id())
            .toString();
    }

    public boolean equals(Object other) {
        if ( (this == other ) ) return true;
        if ( !(other instanceof TBSQM_CSM_MAINVO) ) return false;
        TBSQM_CSM_MAINVO castOther = (TBSQM_CSM_MAINVO) other;
        return new EqualsBuilder()
            .append(this.getcomp_id(), castOther.getcomp_id())
            .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
            .append(getcomp_id())
            .toHashCode();
    }

}
