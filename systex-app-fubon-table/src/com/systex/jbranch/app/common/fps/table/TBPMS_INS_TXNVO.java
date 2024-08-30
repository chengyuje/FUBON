package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Timestamp;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;

/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBPMS_INS_TXNVO extends VOBase {

    /** identifier field */
    private com.systex.jbranch.app.common.fps.table.TBPMS_INS_TXNPK comp_id;

    /** nullable persistent field */
    private BigDecimal TX_CNT;

    /** nullable persistent field */
    private String TX_DATE;

    /** nullable persistent field */
    private BigDecimal ANNU_PREMIUM;

    /** nullable persistent field */
    private BigDecimal ANNU_ACT_FEE;

    /** nullable persistent field */
    private BigDecimal ACT_FEE;

    /** nullable persistent field */
    private BigDecimal CNR_FEE;

    /** nullable persistent field */
    private Timestamp KEYIN_DATE;

    /** nullable persistent field */
    private Timestamp APPLY_DATE;

    /** nullable persistent field */
    private String OP_BATCH_NO;

    /** nullable persistent field */
    private String BRANCH_NBR;

    /** nullable persistent field */
    private String BRANCH_NAME;

    /** nullable persistent field */
    private String RECRUIT_ID;

    /** nullable persistent field */
    private String RECRUIT_IDNBR;

    /** nullable persistent field */
    private String RECRUIT_NAME;

    /** nullable persistent field */
    private String AO_CODE;

    /** nullable persistent field */
    private String INSURED_NAME;

    /** nullable persistent field */
    private String INSURED_ID;

    /** nullable persistent field */
    private String CUST_ID;

    /** nullable persistent field */
    private String PROPOSER_NAME;

    /** nullable persistent field */
    private String PRD_ID;

    /** nullable persistent field */
    private String PRD_NAME;

    /** nullable persistent field */
    private BigDecimal STATUS;

    /** nullable persistent field */
    private String PRD_TYPE;

    /** nullable persistent field */
    private String PRD_ANNUAL;

    /** nullable persistent field */
    private String PAY_TYPE;

    /** nullable persistent field */
    private String MOP2;

    /** nullable persistent field */
    private String SPECIAL_CONDITION;

    /** nullable persistent field */
    private String CURR_CD;

    /** nullable persistent field */
    private BigDecimal EXCH_RATE;

    /** nullable persistent field */
    private BigDecimal REAL_PREMIUM;

    /** nullable persistent field */
    private BigDecimal BASE_PREMIUM;

    /** nullable persistent field */
    private Timestamp MATCH_DATE;

    /** nullable persistent field */
    private String REF_CON_ID;

    /** nullable persistent field */
    private String REF_EMP_ID;

    /** nullable persistent field */
    private String REF_EMP_NAME;

    /** nullable persistent field */
    private String REG_TYPE;

    /** nullable persistent field */
    private String WRITE_REASON;

    /** nullable persistent field */
    private String WRITE_REASON_OTH;

    /** nullable persistent field */
    private String QC_ANC_DOC;

    /** nullable persistent field */
    private Timestamp SIGN_DATE;

    /** nullable persistent field */
    private Timestamp AFT_SIGN_DATE;

    /** nullable persistent field */
    private Timestamp INS_RCV_DATE;

    /** nullable persistent field */
    private String INS_RCV_OPRID;

    /** nullable persistent field */
    private String REMARK_BANK;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBPMS_INS_TXN";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBPMS_INS_TXNVO(com.systex.jbranch.app.common.fps.table.TBPMS_INS_TXNPK comp_id, BigDecimal TX_CNT, String TX_DATE, BigDecimal ANNU_PREMIUM, BigDecimal ANNU_ACT_FEE, BigDecimal ACT_FEE, BigDecimal CNR_FEE, Timestamp KEYIN_DATE, Timestamp APPLY_DATE, String OP_BATCH_NO, String BRANCH_NBR, String BRANCH_NAME, String RECRUIT_ID, String RECRUIT_IDNBR, String RECRUIT_NAME, String AO_CODE, String INSURED_NAME, String INSURED_ID, String CUST_ID, String PROPOSER_NAME, String PRD_ID, String PRD_NAME, BigDecimal STATUS, String PRD_TYPE, String PRD_ANNUAL, String PAY_TYPE, String MOP2, String SPECIAL_CONDITION, String CURR_CD, BigDecimal EXCH_RATE, BigDecimal REAL_PREMIUM, BigDecimal BASE_PREMIUM, Timestamp MATCH_DATE, String REF_CON_ID, String REF_EMP_ID, String REF_EMP_NAME, String REG_TYPE, String WRITE_REASON, String WRITE_REASON_OTH, String QC_ANC_DOC, Timestamp SIGN_DATE, Timestamp AFT_SIGN_DATE, Timestamp INS_RCV_DATE, String INS_RCV_OPRID, String REMARK_BANK, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.comp_id = comp_id;
        this.TX_CNT = TX_CNT;
        this.TX_DATE = TX_DATE;
        this.ANNU_PREMIUM = ANNU_PREMIUM;
        this.ANNU_ACT_FEE = ANNU_ACT_FEE;
        this.ACT_FEE = ACT_FEE;
        this.CNR_FEE = CNR_FEE;
        this.KEYIN_DATE = KEYIN_DATE;
        this.APPLY_DATE = APPLY_DATE;
        this.OP_BATCH_NO = OP_BATCH_NO;
        this.BRANCH_NBR = BRANCH_NBR;
        this.BRANCH_NAME = BRANCH_NAME;
        this.RECRUIT_ID = RECRUIT_ID;
        this.RECRUIT_IDNBR = RECRUIT_IDNBR;
        this.RECRUIT_NAME = RECRUIT_NAME;
        this.AO_CODE = AO_CODE;
        this.INSURED_NAME = INSURED_NAME;
        this.INSURED_ID = INSURED_ID;
        this.CUST_ID = CUST_ID;
        this.PROPOSER_NAME = PROPOSER_NAME;
        this.PRD_ID = PRD_ID;
        this.PRD_NAME = PRD_NAME;
        this.STATUS = STATUS;
        this.PRD_TYPE = PRD_TYPE;
        this.PRD_ANNUAL = PRD_ANNUAL;
        this.PAY_TYPE = PAY_TYPE;
        this.MOP2 = MOP2;
        this.SPECIAL_CONDITION = SPECIAL_CONDITION;
        this.CURR_CD = CURR_CD;
        this.EXCH_RATE = EXCH_RATE;
        this.REAL_PREMIUM = REAL_PREMIUM;
        this.BASE_PREMIUM = BASE_PREMIUM;
        this.MATCH_DATE = MATCH_DATE;
        this.REF_CON_ID = REF_CON_ID;
        this.REF_EMP_ID = REF_EMP_ID;
        this.REF_EMP_NAME = REF_EMP_NAME;
        this.REG_TYPE = REG_TYPE;
        this.WRITE_REASON = WRITE_REASON;
        this.WRITE_REASON_OTH = WRITE_REASON_OTH;
        this.QC_ANC_DOC = QC_ANC_DOC;
        this.SIGN_DATE = SIGN_DATE;
        this.AFT_SIGN_DATE = AFT_SIGN_DATE;
        this.INS_RCV_DATE = INS_RCV_DATE;
        this.INS_RCV_OPRID = INS_RCV_OPRID;
        this.REMARK_BANK = REMARK_BANK;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBPMS_INS_TXNVO() {
    }

    /** minimal constructor */
    public TBPMS_INS_TXNVO(com.systex.jbranch.app.common.fps.table.TBPMS_INS_TXNPK comp_id) {
        this.comp_id = comp_id;
    }

    public com.systex.jbranch.app.common.fps.table.TBPMS_INS_TXNPK getcomp_id() {
        return this.comp_id;
    }

    public void setcomp_id(com.systex.jbranch.app.common.fps.table.TBPMS_INS_TXNPK comp_id) {
        this.comp_id = comp_id;
    }

    public BigDecimal getTX_CNT() {
        return this.TX_CNT;
    }

    public void setTX_CNT(BigDecimal TX_CNT) {
        this.TX_CNT = TX_CNT;
    }

    public String getTX_DATE() {
        return this.TX_DATE;
    }

    public void setTX_DATE(String TX_DATE) {
        this.TX_DATE = TX_DATE;
    }

    public BigDecimal getANNU_PREMIUM() {
        return this.ANNU_PREMIUM;
    }

    public void setANNU_PREMIUM(BigDecimal ANNU_PREMIUM) {
        this.ANNU_PREMIUM = ANNU_PREMIUM;
    }

    public BigDecimal getANNU_ACT_FEE() {
        return this.ANNU_ACT_FEE;
    }

    public void setANNU_ACT_FEE(BigDecimal ANNU_ACT_FEE) {
        this.ANNU_ACT_FEE = ANNU_ACT_FEE;
    }

    public BigDecimal getACT_FEE() {
        return this.ACT_FEE;
    }

    public void setACT_FEE(BigDecimal ACT_FEE) {
        this.ACT_FEE = ACT_FEE;
    }

    public BigDecimal getCNR_FEE() {
        return this.CNR_FEE;
    }

    public void setCNR_FEE(BigDecimal CNR_FEE) {
        this.CNR_FEE = CNR_FEE;
    }

    public Timestamp getKEYIN_DATE() {
        return this.KEYIN_DATE;
    }

    public void setKEYIN_DATE(Timestamp KEYIN_DATE) {
        this.KEYIN_DATE = KEYIN_DATE;
    }

    public Timestamp getAPPLY_DATE() {
        return this.APPLY_DATE;
    }

    public void setAPPLY_DATE(Timestamp APPLY_DATE) {
        this.APPLY_DATE = APPLY_DATE;
    }

    public String getOP_BATCH_NO() {
        return this.OP_BATCH_NO;
    }

    public void setOP_BATCH_NO(String OP_BATCH_NO) {
        this.OP_BATCH_NO = OP_BATCH_NO;
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

    public String getRECRUIT_ID() {
        return this.RECRUIT_ID;
    }

    public void setRECRUIT_ID(String RECRUIT_ID) {
        this.RECRUIT_ID = RECRUIT_ID;
    }

    public String getRECRUIT_IDNBR() {
        return this.RECRUIT_IDNBR;
    }

    public void setRECRUIT_IDNBR(String RECRUIT_IDNBR) {
        this.RECRUIT_IDNBR = RECRUIT_IDNBR;
    }

    public String getRECRUIT_NAME() {
        return this.RECRUIT_NAME;
    }

    public void setRECRUIT_NAME(String RECRUIT_NAME) {
        this.RECRUIT_NAME = RECRUIT_NAME;
    }

    public String getAO_CODE() {
        return this.AO_CODE;
    }

    public void setAO_CODE(String AO_CODE) {
        this.AO_CODE = AO_CODE;
    }

    public String getINSURED_NAME() {
        return this.INSURED_NAME;
    }

    public void setINSURED_NAME(String INSURED_NAME) {
        this.INSURED_NAME = INSURED_NAME;
    }

    public String getINSURED_ID() {
        return this.INSURED_ID;
    }

    public void setINSURED_ID(String INSURED_ID) {
        this.INSURED_ID = INSURED_ID;
    }

    public String getCUST_ID() {
        return this.CUST_ID;
    }

    public void setCUST_ID(String CUST_ID) {
        this.CUST_ID = CUST_ID;
    }

    public String getPROPOSER_NAME() {
        return this.PROPOSER_NAME;
    }

    public void setPROPOSER_NAME(String PROPOSER_NAME) {
        this.PROPOSER_NAME = PROPOSER_NAME;
    }

    public String getPRD_ID() {
        return this.PRD_ID;
    }

    public void setPRD_ID(String PRD_ID) {
        this.PRD_ID = PRD_ID;
    }

    public String getPRD_NAME() {
        return this.PRD_NAME;
    }

    public void setPRD_NAME(String PRD_NAME) {
        this.PRD_NAME = PRD_NAME;
    }

    public BigDecimal getSTATUS() {
        return this.STATUS;
    }

    public void setSTATUS(BigDecimal STATUS) {
        this.STATUS = STATUS;
    }

    public String getPRD_TYPE() {
        return this.PRD_TYPE;
    }

    public void setPRD_TYPE(String PRD_TYPE) {
        this.PRD_TYPE = PRD_TYPE;
    }

    public String getPRD_ANNUAL() {
        return this.PRD_ANNUAL;
    }

    public void setPRD_ANNUAL(String PRD_ANNUAL) {
        this.PRD_ANNUAL = PRD_ANNUAL;
    }

    public String getPAY_TYPE() {
        return this.PAY_TYPE;
    }

    public void setPAY_TYPE(String PAY_TYPE) {
        this.PAY_TYPE = PAY_TYPE;
    }

    public String getMOP2() {
        return this.MOP2;
    }

    public void setMOP2(String MOP2) {
        this.MOP2 = MOP2;
    }

    public String getSPECIAL_CONDITION() {
        return this.SPECIAL_CONDITION;
    }

    public void setSPECIAL_CONDITION(String SPECIAL_CONDITION) {
        this.SPECIAL_CONDITION = SPECIAL_CONDITION;
    }

    public String getCURR_CD() {
        return this.CURR_CD;
    }

    public void setCURR_CD(String CURR_CD) {
        this.CURR_CD = CURR_CD;
    }

    public BigDecimal getEXCH_RATE() {
        return this.EXCH_RATE;
    }

    public void setEXCH_RATE(BigDecimal EXCH_RATE) {
        this.EXCH_RATE = EXCH_RATE;
    }

    public BigDecimal getREAL_PREMIUM() {
        return this.REAL_PREMIUM;
    }

    public void setREAL_PREMIUM(BigDecimal REAL_PREMIUM) {
        this.REAL_PREMIUM = REAL_PREMIUM;
    }

    public BigDecimal getBASE_PREMIUM() {
        return this.BASE_PREMIUM;
    }

    public void setBASE_PREMIUM(BigDecimal BASE_PREMIUM) {
        this.BASE_PREMIUM = BASE_PREMIUM;
    }

    public Timestamp getMATCH_DATE() {
        return this.MATCH_DATE;
    }

    public void setMATCH_DATE(Timestamp MATCH_DATE) {
        this.MATCH_DATE = MATCH_DATE;
    }

    public String getREF_CON_ID() {
        return this.REF_CON_ID;
    }

    public void setREF_CON_ID(String REF_CON_ID) {
        this.REF_CON_ID = REF_CON_ID;
    }

    public String getREF_EMP_ID() {
        return this.REF_EMP_ID;
    }

    public void setREF_EMP_ID(String REF_EMP_ID) {
        this.REF_EMP_ID = REF_EMP_ID;
    }

    public String getREF_EMP_NAME() {
        return this.REF_EMP_NAME;
    }

    public void setREF_EMP_NAME(String REF_EMP_NAME) {
        this.REF_EMP_NAME = REF_EMP_NAME;
    }

    public String getREG_TYPE() {
        return this.REG_TYPE;
    }

    public void setREG_TYPE(String REG_TYPE) {
        this.REG_TYPE = REG_TYPE;
    }

    public String getWRITE_REASON() {
        return this.WRITE_REASON;
    }

    public void setWRITE_REASON(String WRITE_REASON) {
        this.WRITE_REASON = WRITE_REASON;
    }

    public String getWRITE_REASON_OTH() {
        return this.WRITE_REASON_OTH;
    }

    public void setWRITE_REASON_OTH(String WRITE_REASON_OTH) {
        this.WRITE_REASON_OTH = WRITE_REASON_OTH;
    }

    public String getQC_ANC_DOC() {
        return this.QC_ANC_DOC;
    }

    public void setQC_ANC_DOC(String QC_ANC_DOC) {
        this.QC_ANC_DOC = QC_ANC_DOC;
    }

    public Timestamp getSIGN_DATE() {
        return this.SIGN_DATE;
    }

    public void setSIGN_DATE(Timestamp SIGN_DATE) {
        this.SIGN_DATE = SIGN_DATE;
    }

    public Timestamp getAFT_SIGN_DATE() {
        return this.AFT_SIGN_DATE;
    }

    public void setAFT_SIGN_DATE(Timestamp AFT_SIGN_DATE) {
        this.AFT_SIGN_DATE = AFT_SIGN_DATE;
    }

    public Timestamp getINS_RCV_DATE() {
        return this.INS_RCV_DATE;
    }

    public void setINS_RCV_DATE(Timestamp INS_RCV_DATE) {
        this.INS_RCV_DATE = INS_RCV_DATE;
    }

    public String getINS_RCV_OPRID() {
        return this.INS_RCV_OPRID;
    }

    public void setINS_RCV_OPRID(String INS_RCV_OPRID) {
        this.INS_RCV_OPRID = INS_RCV_OPRID;
    }

    public String getREMARK_BANK() {
        return this.REMARK_BANK;
    }

    public void setREMARK_BANK(String REMARK_BANK) {
        this.REMARK_BANK = REMARK_BANK;
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
        if ( !(other instanceof TBPMS_INS_TXNVO) ) return false;
        TBPMS_INS_TXNVO castOther = (TBPMS_INS_TXNVO) other;
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
