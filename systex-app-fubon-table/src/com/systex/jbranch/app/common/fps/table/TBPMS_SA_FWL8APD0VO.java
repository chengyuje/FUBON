package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBPMS_SA_FWL8APD0VO extends VOBase {

    /** identifier field */
    private com.systex.jbranch.app.common.fps.table.TBPMS_SA_FWL8APD0PK comp_id;

    /** nullable persistent field */
    private Timestamp DATA_DATE;

    /** nullable persistent field */
    private String REGION_CENTER_ID;

    /** nullable persistent field */
    private String REGION_CENTER_NAME;

    /** nullable persistent field */
    private String BRANCH_AREA_ID;

    /** nullable persistent field */
    private String BRANCH_AREA_NAME;

    /** nullable persistent field */
    private String BRANCH_NBR;

    /** nullable persistent field */
    private String BRANCH_NAME;

    /** nullable persistent field */
    private String AO_CODE;

    /** nullable persistent field */
    private String CUST_ID;

    /** nullable persistent field */
    private String CUST_NAME;

    /** nullable persistent field */
    private String INS_NAME;

    /** nullable persistent field */
    private String POLICY_NO;

    /** nullable persistent field */
    private String ID_DUP;

    /** nullable persistent field */
    private String POLICY_SEQ;

    /** nullable persistent field */
    private String ITEM4;

    /** nullable persistent field */
    private String PAY_KIND_T;

    /** nullable persistent field */
    private Timestamp PAID_DATE;

    /** nullable persistent field */
    private Timestamp FST_ACT_DT;

    /** nullable persistent field */
    private String MOP_T;

    /** nullable persistent field */
    private String CURRENCY;

    /** nullable persistent field */
    private BigDecimal PAID_AMOUNT;

    /** nullable persistent field */
    private String POLI_YEAR;

    /** nullable persistent field */
    private String POLI_PERD;

    /** nullable persistent field */
    private String SECRT_YN;

    /** nullable persistent field */
    private String ATR_CODE;

    /** nullable persistent field */
    private String PAID_STAT;

    /** nullable persistent field */
    private Timestamp APAID_DATE;

    /** nullable persistent field */
    private String NEXT_DATE;

    /** nullable persistent field */
    private String REMARK;

    /** nullable persistent field */
    private String SA_LEAD_ID;

    /** nullable persistent field */
    private String INTERACT_ID;

    /** nullable persistent field */
    private String DESCR254;

    /** nullable persistent field */
    private BigDecimal EXCHANGE_RATE;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBPMS_SA_FWL8APD0";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBPMS_SA_FWL8APD0VO(com.systex.jbranch.app.common.fps.table.TBPMS_SA_FWL8APD0PK comp_id, Timestamp DATA_DATE, String REGION_CENTER_ID, String REGION_CENTER_NAME, String BRANCH_AREA_ID, String BRANCH_AREA_NAME, String BRANCH_NBR, String BRANCH_NAME, String AO_CODE, String CUST_ID, String CUST_NAME, String INS_NAME, String POLICY_NO, String ID_DUP, String POLICY_SEQ, String ITEM4, String PAY_KIND_T, Timestamp PAID_DATE, Timestamp FST_ACT_DT, String MOP_T, String CURRENCY, BigDecimal PAID_AMOUNT, String POLI_YEAR, String POLI_PERD, String SECRT_YN, String ATR_CODE, String PAID_STAT, Timestamp APAID_DATE, String NEXT_DATE, String REMARK, String SA_LEAD_ID, String INTERACT_ID, String DESCR254, BigDecimal EXCHANGE_RATE, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.comp_id = comp_id;
        this.DATA_DATE = DATA_DATE;
        this.REGION_CENTER_ID = REGION_CENTER_ID;
        this.REGION_CENTER_NAME = REGION_CENTER_NAME;
        this.BRANCH_AREA_ID = BRANCH_AREA_ID;
        this.BRANCH_AREA_NAME = BRANCH_AREA_NAME;
        this.BRANCH_NBR = BRANCH_NBR;
        this.BRANCH_NAME = BRANCH_NAME;
        this.AO_CODE = AO_CODE;
        this.CUST_ID = CUST_ID;
        this.CUST_NAME = CUST_NAME;
        this.INS_NAME = INS_NAME;
        this.POLICY_NO = POLICY_NO;
        this.ID_DUP = ID_DUP;
        this.POLICY_SEQ = POLICY_SEQ;
        this.ITEM4 = ITEM4;
        this.PAY_KIND_T = PAY_KIND_T;
        this.PAID_DATE = PAID_DATE;
        this.FST_ACT_DT = FST_ACT_DT;
        this.MOP_T = MOP_T;
        this.CURRENCY = CURRENCY;
        this.PAID_AMOUNT = PAID_AMOUNT;
        this.POLI_YEAR = POLI_YEAR;
        this.POLI_PERD = POLI_PERD;
        this.SECRT_YN = SECRT_YN;
        this.ATR_CODE = ATR_CODE;
        this.PAID_STAT = PAID_STAT;
        this.APAID_DATE = APAID_DATE;
        this.NEXT_DATE = NEXT_DATE;
        this.REMARK = REMARK;
        this.SA_LEAD_ID = SA_LEAD_ID;
        this.INTERACT_ID = INTERACT_ID;
        this.DESCR254 = DESCR254;
        this.EXCHANGE_RATE = EXCHANGE_RATE;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBPMS_SA_FWL8APD0VO() {
    }

    /** minimal constructor */
    public TBPMS_SA_FWL8APD0VO(com.systex.jbranch.app.common.fps.table.TBPMS_SA_FWL8APD0PK comp_id) {
        this.comp_id = comp_id;
    }

    public com.systex.jbranch.app.common.fps.table.TBPMS_SA_FWL8APD0PK getcomp_id() {
        return this.comp_id;
    }

    public void setcomp_id(com.systex.jbranch.app.common.fps.table.TBPMS_SA_FWL8APD0PK comp_id) {
        this.comp_id = comp_id;
    }

    public Timestamp getDATA_DATE() {
        return this.DATA_DATE;
    }

    public void setDATA_DATE(Timestamp DATA_DATE) {
        this.DATA_DATE = DATA_DATE;
    }

    public String getREGION_CENTER_ID() {
        return this.REGION_CENTER_ID;
    }

    public void setREGION_CENTER_ID(String REGION_CENTER_ID) {
        this.REGION_CENTER_ID = REGION_CENTER_ID;
    }

    public String getREGION_CENTER_NAME() {
        return this.REGION_CENTER_NAME;
    }

    public void setREGION_CENTER_NAME(String REGION_CENTER_NAME) {
        this.REGION_CENTER_NAME = REGION_CENTER_NAME;
    }

    public String getBRANCH_AREA_ID() {
        return this.BRANCH_AREA_ID;
    }

    public void setBRANCH_AREA_ID(String BRANCH_AREA_ID) {
        this.BRANCH_AREA_ID = BRANCH_AREA_ID;
    }

    public String getBRANCH_AREA_NAME() {
        return this.BRANCH_AREA_NAME;
    }

    public void setBRANCH_AREA_NAME(String BRANCH_AREA_NAME) {
        this.BRANCH_AREA_NAME = BRANCH_AREA_NAME;
    }

    public String getBRANCH_NBR() {
        return this.BRANCH_NBR;
    }

    public void setBRANCH_NBR(String BRANCH_NBR) {
        this.BRANCH_NBR = BRANCH_NBR;
    }

    public String getBRANCH_NAME() {
        return this.BRANCH_NAME;
    }

    public void setBRANCH_NAME(String BRANCH_NAME) {
        this.BRANCH_NAME = BRANCH_NAME;
    }

    public String getAO_CODE() {
        return this.AO_CODE;
    }

    public void setAO_CODE(String AO_CODE) {
        this.AO_CODE = AO_CODE;
    }

    public String getCUST_ID() {
        return this.CUST_ID;
    }

    public void setCUST_ID(String CUST_ID) {
        this.CUST_ID = CUST_ID;
    }

    public String getCUST_NAME() {
        return this.CUST_NAME;
    }

    public void setCUST_NAME(String CUST_NAME) {
        this.CUST_NAME = CUST_NAME;
    }

    public String getINS_NAME() {
        return this.INS_NAME;
    }

    public void setINS_NAME(String INS_NAME) {
        this.INS_NAME = INS_NAME;
    }

    public String getPOLICY_NO() {
        return this.POLICY_NO;
    }

    public void setPOLICY_NO(String POLICY_NO) {
        this.POLICY_NO = POLICY_NO;
    }

    public String getID_DUP() {
        return this.ID_DUP;
    }

    public void setID_DUP(String ID_DUP) {
        this.ID_DUP = ID_DUP;
    }

    public String getPOLICY_SEQ() {
        return this.POLICY_SEQ;
    }

    public void setPOLICY_SEQ(String POLICY_SEQ) {
        this.POLICY_SEQ = POLICY_SEQ;
    }

    public String getITEM4() {
        return this.ITEM4;
    }

    public void setITEM4(String ITEM4) {
        this.ITEM4 = ITEM4;
    }

    public String getPAY_KIND_T() {
        return this.PAY_KIND_T;
    }

    public void setPAY_KIND_T(String PAY_KIND_T) {
        this.PAY_KIND_T = PAY_KIND_T;
    }

    public Timestamp getPAID_DATE() {
        return this.PAID_DATE;
    }

    public void setPAID_DATE(Timestamp PAID_DATE) {
        this.PAID_DATE = PAID_DATE;
    }

    public Timestamp getFST_ACT_DT() {
        return this.FST_ACT_DT;
    }

    public void setFST_ACT_DT(Timestamp FST_ACT_DT) {
        this.FST_ACT_DT = FST_ACT_DT;
    }

    public String getMOP_T() {
        return this.MOP_T;
    }

    public void setMOP_T(String MOP_T) {
        this.MOP_T = MOP_T;
    }

    public String getCURRENCY() {
        return this.CURRENCY;
    }

    public void setCURRENCY(String CURRENCY) {
        this.CURRENCY = CURRENCY;
    }

    public BigDecimal getPAID_AMOUNT() {
        return this.PAID_AMOUNT;
    }

    public void setPAID_AMOUNT(BigDecimal PAID_AMOUNT) {
        this.PAID_AMOUNT = PAID_AMOUNT;
    }

    public String getPOLI_YEAR() {
        return this.POLI_YEAR;
    }

    public void setPOLI_YEAR(String POLI_YEAR) {
        this.POLI_YEAR = POLI_YEAR;
    }

    public String getPOLI_PERD() {
        return this.POLI_PERD;
    }

    public void setPOLI_PERD(String POLI_PERD) {
        this.POLI_PERD = POLI_PERD;
    }

    public String getSECRT_YN() {
        return this.SECRT_YN;
    }

    public void setSECRT_YN(String SECRT_YN) {
        this.SECRT_YN = SECRT_YN;
    }

    public String getATR_CODE() {
        return this.ATR_CODE;
    }

    public void setATR_CODE(String ATR_CODE) {
        this.ATR_CODE = ATR_CODE;
    }

    public String getPAID_STAT() {
        return this.PAID_STAT;
    }

    public void setPAID_STAT(String PAID_STAT) {
        this.PAID_STAT = PAID_STAT;
    }

    public Timestamp getAPAID_DATE() {
        return this.APAID_DATE;
    }

    public void setAPAID_DATE(Timestamp APAID_DATE) {
        this.APAID_DATE = APAID_DATE;
    }

    public String getNEXT_DATE() {
        return this.NEXT_DATE;
    }

    public void setNEXT_DATE(String NEXT_DATE) {
        this.NEXT_DATE = NEXT_DATE;
    }

    public String getREMARK() {
        return this.REMARK;
    }

    public void setREMARK(String REMARK) {
        this.REMARK = REMARK;
    }

    public String getSA_LEAD_ID() {
        return this.SA_LEAD_ID;
    }

    public void setSA_LEAD_ID(String SA_LEAD_ID) {
        this.SA_LEAD_ID = SA_LEAD_ID;
    }

    public String getINTERACT_ID() {
        return this.INTERACT_ID;
    }

    public void setINTERACT_ID(String INTERACT_ID) {
        this.INTERACT_ID = INTERACT_ID;
    }

    public String getDESCR254() {
        return this.DESCR254;
    }

    public void setDESCR254(String DESCR254) {
        this.DESCR254 = DESCR254;
    }

    public BigDecimal getEXCHANGE_RATE() {
        return this.EXCHANGE_RATE;
    }

    public void setEXCHANGE_RATE(BigDecimal EXCHANGE_RATE) {
        this.EXCHANGE_RATE = EXCHANGE_RATE;
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
        if ( !(other instanceof TBPMS_SA_FWL8APD0VO) ) return false;
        TBPMS_SA_FWL8APD0VO castOther = (TBPMS_SA_FWL8APD0VO) other;
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
