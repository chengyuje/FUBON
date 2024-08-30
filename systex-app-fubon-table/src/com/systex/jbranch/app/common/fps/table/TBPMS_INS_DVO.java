package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Timestamp;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;

/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBPMS_INS_DVO extends VOBase {

    /** identifier field */
    private String INSFNO;

    /** nullable persistent field */
    private String RPT;

    /** nullable persistent field */
    private String P1;

    /** nullable persistent field */
    private String C_FLAG;

    /** nullable persistent field */
    private BigDecimal NUM;

    /** nullable persistent field */
    private BigDecimal INS_FEE;

    /** nullable persistent field */
    private BigDecimal ACT_INC;

    /** nullable persistent field */
    private String REGION_CENTER_ID;

    /** nullable persistent field */
    private String REGION_CENTER_NAME;

    /** nullable persistent field */
    private String BRANCH_AREA_ID;

    /** nullable persistent field */
    private String BRANCH_AREA_NAME;

    /** nullable persistent field */
    private String BRANCH_CLS;

    /** nullable persistent field */
    private String BN_DAY;

    /** nullable persistent field */
    private String KM_DAY;

    /** nullable persistent field */
    private String DBNO;

    /** nullable persistent field */
    private String NAME;

    /** nullable persistent field */
    private String BRANCH_NBR;

    /** nullable persistent field */
    private String BRANCH_NAME;

    /** nullable persistent field */
    private String INS_NBR;

    /** nullable persistent field */
    private String PAY_YEAR;

    /** nullable persistent field */
    private String OTP_PAY;

    /** nullable persistent field */
    private String PP_TYPE;

    /** nullable persistent field */
    private String SP_CON;

    /** nullable persistent field */
    private String CRCY_TYPE;

    /** nullable persistent field */
    private BigDecimal AIF_ORGD;

    /** nullable persistent field */
    private String C_STATE;

    /** nullable persistent field */
    private String R_STATE;

    /** nullable persistent field */
    private String BN_STATE;

    /** nullable persistent field */
    private String N_TIME;

    /** nullable persistent field */
    private String N_EMP_ID;

    /** nullable persistent field */
    private String REQ_DAY;

    /** nullable persistent field */
    private String INSURED_ID;

    /** nullable persistent field */
    private String PROPOSER_ID;

    /** nullable persistent field */
    private String PROPOSER_NAME;

    /** nullable persistent field */
    private String AO_CODE;

    /** nullable persistent field */
    private String AP_EMP_ID;

    /** nullable persistent field */
    private String AP_ID;

    /** nullable persistent field */
    private String AP_NAME;

    /** nullable persistent field */
    private String INS_NAME;

    /** nullable persistent field */
    private String INS_TYPE;

    /** nullable persistent field */
    private BigDecimal BF_ORGD;

    /** nullable persistent field */
    private BigDecimal AF_NTD;

    /** nullable persistent field */
    private BigDecimal CNRF_NTD;

    /** nullable persistent field */
    private BigDecimal REF_E_RATE;

    /** nullable persistent field */
    private String WQ_CHECK;

    /** nullable persistent field */
    private String SIGN_TIME;

    /** nullable persistent field */
    private String SIGN_PERSON;

    /** nullable persistent field */
    private String TL_TIME;

    /** nullable persistent field */
    private String TL_PERSON;

    /** nullable persistent field */
    private String DEL_TIME;

    /** nullable persistent field */
    private String DEL_PERSON;

    /** nullable persistent field */
    private String NOTE;

    /** nullable persistent field */
    private String MNGR_FLAG;

    /** nullable persistent field */
    private String CMSN_TYPE;

    /** nullable persistent field */
    private String CMSN_NAME;

    /** nullable persistent field */
    private String FAGIN_PS;

    /** nullable persistent field */
    private String FAGIN_CODE;

    /** nullable persistent field */
    private String STS;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBPMS_INS_D";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBPMS_INS_DVO(String INSFNO, String RPT, String P1, String C_FLAG, BigDecimal NUM, BigDecimal INS_FEE, BigDecimal ACT_INC, String REGION_CENTER_ID, String REGION_CENTER_NAME, String BRANCH_AREA_ID, String BRANCH_AREA_NAME, String BRANCH_CLS, String BN_DAY, String KM_DAY, String DBNO, String NAME, String BRANCH_NBR, String BRANCH_NAME, String INS_NBR, String PAY_YEAR, String OTP_PAY, String PP_TYPE, String SP_CON, String CRCY_TYPE, BigDecimal AIF_ORGD, String C_STATE, String R_STATE, String BN_STATE, String N_TIME, String N_EMP_ID, String REQ_DAY, String INSURED_ID, String PROPOSER_ID, String PROPOSER_NAME, String AO_CODE, String AP_EMP_ID, String AP_ID, String AP_NAME, String INS_NAME, String INS_TYPE, BigDecimal BF_ORGD, BigDecimal AF_NTD, BigDecimal CNRF_NTD, BigDecimal REF_E_RATE, String WQ_CHECK, String SIGN_TIME, String SIGN_PERSON, String TL_TIME, String TL_PERSON, String DEL_TIME, String DEL_PERSON, String NOTE, String MNGR_FLAG, String CMSN_TYPE, String CMSN_NAME, String FAGIN_PS, String FAGIN_CODE, String STS, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.INSFNO = INSFNO;
        this.RPT = RPT;
        this.P1 = P1;
        this.C_FLAG = C_FLAG;
        this.NUM = NUM;
        this.INS_FEE = INS_FEE;
        this.ACT_INC = ACT_INC;
        this.REGION_CENTER_ID = REGION_CENTER_ID;
        this.REGION_CENTER_NAME = REGION_CENTER_NAME;
        this.BRANCH_AREA_ID = BRANCH_AREA_ID;
        this.BRANCH_AREA_NAME = BRANCH_AREA_NAME;
        this.BRANCH_CLS = BRANCH_CLS;
        this.BN_DAY = BN_DAY;
        this.KM_DAY = KM_DAY;
        this.DBNO = DBNO;
        this.NAME = NAME;
        this.BRANCH_NBR = BRANCH_NBR;
        this.BRANCH_NAME = BRANCH_NAME;
        this.INS_NBR = INS_NBR;
        this.PAY_YEAR = PAY_YEAR;
        this.OTP_PAY = OTP_PAY;
        this.PP_TYPE = PP_TYPE;
        this.SP_CON = SP_CON;
        this.CRCY_TYPE = CRCY_TYPE;
        this.AIF_ORGD = AIF_ORGD;
        this.C_STATE = C_STATE;
        this.R_STATE = R_STATE;
        this.BN_STATE = BN_STATE;
        this.N_TIME = N_TIME;
        this.N_EMP_ID = N_EMP_ID;
        this.REQ_DAY = REQ_DAY;
        this.INSURED_ID = INSURED_ID;
        this.PROPOSER_ID = PROPOSER_ID;
        this.PROPOSER_NAME = PROPOSER_NAME;
        this.AO_CODE = AO_CODE;
        this.AP_EMP_ID = AP_EMP_ID;
        this.AP_ID = AP_ID;
        this.AP_NAME = AP_NAME;
        this.INS_NAME = INS_NAME;
        this.INS_TYPE = INS_TYPE;
        this.BF_ORGD = BF_ORGD;
        this.AF_NTD = AF_NTD;
        this.CNRF_NTD = CNRF_NTD;
        this.REF_E_RATE = REF_E_RATE;
        this.WQ_CHECK = WQ_CHECK;
        this.SIGN_TIME = SIGN_TIME;
        this.SIGN_PERSON = SIGN_PERSON;
        this.TL_TIME = TL_TIME;
        this.TL_PERSON = TL_PERSON;
        this.DEL_TIME = DEL_TIME;
        this.DEL_PERSON = DEL_PERSON;
        this.NOTE = NOTE;
        this.MNGR_FLAG = MNGR_FLAG;
        this.CMSN_TYPE = CMSN_TYPE;
        this.CMSN_NAME = CMSN_NAME;
        this.FAGIN_PS = FAGIN_PS;
        this.FAGIN_CODE = FAGIN_CODE;
        this.STS = STS;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBPMS_INS_DVO() {
    }

    /** minimal constructor */
    public TBPMS_INS_DVO(String INSFNO) {
        this.INSFNO = INSFNO;
    }

    public String getINSFNO() {
        return this.INSFNO;
    }

    public void setINSFNO(String INSFNO) {
        this.INSFNO = INSFNO;
    }

    public String getRPT() {
        return this.RPT;
    }

    public void setRPT(String RPT) {
        this.RPT = RPT;
    }

    public String getP1() {
        return this.P1;
    }

    public void setP1(String P1) {
        this.P1 = P1;
    }

    public String getC_FLAG() {
        return this.C_FLAG;
    }

    public void setC_FLAG(String C_FLAG) {
        this.C_FLAG = C_FLAG;
    }

    public BigDecimal getNUM() {
        return this.NUM;
    }

    public void setNUM(BigDecimal NUM) {
        this.NUM = NUM;
    }

    public BigDecimal getINS_FEE() {
        return this.INS_FEE;
    }

    public void setINS_FEE(BigDecimal INS_FEE) {
        this.INS_FEE = INS_FEE;
    }

    public BigDecimal getACT_INC() {
        return this.ACT_INC;
    }

    public void setACT_INC(BigDecimal ACT_INC) {
        this.ACT_INC = ACT_INC;
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

    public String getBRANCH_CLS() {
        return this.BRANCH_CLS;
    }

    public void setBRANCH_CLS(String BRANCH_CLS) {
        this.BRANCH_CLS = BRANCH_CLS;
    }

    public String getBN_DAY() {
        return this.BN_DAY;
    }

    public void setBN_DAY(String BN_DAY) {
        this.BN_DAY = BN_DAY;
    }

    public String getKM_DAY() {
        return this.KM_DAY;
    }

    public void setKM_DAY(String KM_DAY) {
        this.KM_DAY = KM_DAY;
    }

    public String getDBNO() {
        return this.DBNO;
    }

    public void setDBNO(String DBNO) {
        this.DBNO = DBNO;
    }

    public String getNAME() {
        return this.NAME;
    }

    public void setNAME(String NAME) {
        this.NAME = NAME;
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

    public String getINS_NBR() {
        return this.INS_NBR;
    }

    public void setINS_NBR(String INS_NBR) {
        this.INS_NBR = INS_NBR;
    }

    public String getPAY_YEAR() {
        return this.PAY_YEAR;
    }

    public void setPAY_YEAR(String PAY_YEAR) {
        this.PAY_YEAR = PAY_YEAR;
    }

    public String getOTP_PAY() {
        return this.OTP_PAY;
    }

    public void setOTP_PAY(String OTP_PAY) {
        this.OTP_PAY = OTP_PAY;
    }

    public String getPP_TYPE() {
        return this.PP_TYPE;
    }

    public void setPP_TYPE(String PP_TYPE) {
        this.PP_TYPE = PP_TYPE;
    }

    public String getSP_CON() {
        return this.SP_CON;
    }

    public void setSP_CON(String SP_CON) {
        this.SP_CON = SP_CON;
    }

    public String getCRCY_TYPE() {
        return this.CRCY_TYPE;
    }

    public void setCRCY_TYPE(String CRCY_TYPE) {
        this.CRCY_TYPE = CRCY_TYPE;
    }

    public BigDecimal getAIF_ORGD() {
        return this.AIF_ORGD;
    }

    public void setAIF_ORGD(BigDecimal AIF_ORGD) {
        this.AIF_ORGD = AIF_ORGD;
    }

    public String getC_STATE() {
        return this.C_STATE;
    }

    public void setC_STATE(String C_STATE) {
        this.C_STATE = C_STATE;
    }

    public String getR_STATE() {
        return this.R_STATE;
    }

    public void setR_STATE(String R_STATE) {
        this.R_STATE = R_STATE;
    }

    public String getBN_STATE() {
        return this.BN_STATE;
    }

    public void setBN_STATE(String BN_STATE) {
        this.BN_STATE = BN_STATE;
    }

    public String getN_TIME() {
        return this.N_TIME;
    }

    public void setN_TIME(String N_TIME) {
        this.N_TIME = N_TIME;
    }

    public String getN_EMP_ID() {
        return this.N_EMP_ID;
    }

    public void setN_EMP_ID(String N_EMP_ID) {
        this.N_EMP_ID = N_EMP_ID;
    }

    public String getREQ_DAY() {
        return this.REQ_DAY;
    }

    public void setREQ_DAY(String REQ_DAY) {
        this.REQ_DAY = REQ_DAY;
    }

    public String getINSURED_ID() {
        return this.INSURED_ID;
    }

    public void setINSURED_ID(String INSURED_ID) {
        this.INSURED_ID = INSURED_ID;
    }

    public String getPROPOSER_ID() {
        return this.PROPOSER_ID;
    }

    public void setPROPOSER_ID(String PROPOSER_ID) {
        this.PROPOSER_ID = PROPOSER_ID;
    }

    public String getPROPOSER_NAME() {
        return this.PROPOSER_NAME;
    }

    public void setPROPOSER_NAME(String PROPOSER_NAME) {
        this.PROPOSER_NAME = PROPOSER_NAME;
    }

    public String getAO_CODE() {
        return this.AO_CODE;
    }

    public void setAO_CODE(String AO_CODE) {
        this.AO_CODE = AO_CODE;
    }

    public String getAP_EMP_ID() {
        return this.AP_EMP_ID;
    }

    public void setAP_EMP_ID(String AP_EMP_ID) {
        this.AP_EMP_ID = AP_EMP_ID;
    }

    public String getAP_ID() {
        return this.AP_ID;
    }

    public void setAP_ID(String AP_ID) {
        this.AP_ID = AP_ID;
    }

    public String getAP_NAME() {
        return this.AP_NAME;
    }

    public void setAP_NAME(String AP_NAME) {
        this.AP_NAME = AP_NAME;
    }

    public String getINS_NAME() {
        return this.INS_NAME;
    }

    public void setINS_NAME(String INS_NAME) {
        this.INS_NAME = INS_NAME;
    }

    public String getINS_TYPE() {
        return this.INS_TYPE;
    }

    public void setINS_TYPE(String INS_TYPE) {
        this.INS_TYPE = INS_TYPE;
    }

    public BigDecimal getBF_ORGD() {
        return this.BF_ORGD;
    }

    public void setBF_ORGD(BigDecimal BF_ORGD) {
        this.BF_ORGD = BF_ORGD;
    }

    public BigDecimal getAF_NTD() {
        return this.AF_NTD;
    }

    public void setAF_NTD(BigDecimal AF_NTD) {
        this.AF_NTD = AF_NTD;
    }

    public BigDecimal getCNRF_NTD() {
        return this.CNRF_NTD;
    }

    public void setCNRF_NTD(BigDecimal CNRF_NTD) {
        this.CNRF_NTD = CNRF_NTD;
    }

    public BigDecimal getREF_E_RATE() {
        return this.REF_E_RATE;
    }

    public void setREF_E_RATE(BigDecimal REF_E_RATE) {
        this.REF_E_RATE = REF_E_RATE;
    }

    public String getWQ_CHECK() {
        return this.WQ_CHECK;
    }

    public void setWQ_CHECK(String WQ_CHECK) {
        this.WQ_CHECK = WQ_CHECK;
    }

    public String getSIGN_TIME() {
        return this.SIGN_TIME;
    }

    public void setSIGN_TIME(String SIGN_TIME) {
        this.SIGN_TIME = SIGN_TIME;
    }

    public String getSIGN_PERSON() {
        return this.SIGN_PERSON;
    }

    public void setSIGN_PERSON(String SIGN_PERSON) {
        this.SIGN_PERSON = SIGN_PERSON;
    }

    public String getTL_TIME() {
        return this.TL_TIME;
    }

    public void setTL_TIME(String TL_TIME) {
        this.TL_TIME = TL_TIME;
    }

    public String getTL_PERSON() {
        return this.TL_PERSON;
    }

    public void setTL_PERSON(String TL_PERSON) {
        this.TL_PERSON = TL_PERSON;
    }

    public String getDEL_TIME() {
        return this.DEL_TIME;
    }

    public void setDEL_TIME(String DEL_TIME) {
        this.DEL_TIME = DEL_TIME;
    }

    public String getDEL_PERSON() {
        return this.DEL_PERSON;
    }

    public void setDEL_PERSON(String DEL_PERSON) {
        this.DEL_PERSON = DEL_PERSON;
    }

    public String getNOTE() {
        return this.NOTE;
    }

    public void setNOTE(String NOTE) {
        this.NOTE = NOTE;
    }

    public String getMNGR_FLAG() {
        return this.MNGR_FLAG;
    }

    public void setMNGR_FLAG(String MNGR_FLAG) {
        this.MNGR_FLAG = MNGR_FLAG;
    }

    public String getCMSN_TYPE() {
        return this.CMSN_TYPE;
    }

    public void setCMSN_TYPE(String CMSN_TYPE) {
        this.CMSN_TYPE = CMSN_TYPE;
    }

    public String getCMSN_NAME() {
        return this.CMSN_NAME;
    }

    public void setCMSN_NAME(String CMSN_NAME) {
        this.CMSN_NAME = CMSN_NAME;
    }

    public String getFAGIN_PS() {
        return this.FAGIN_PS;
    }

    public void setFAGIN_PS(String FAGIN_PS) {
        this.FAGIN_PS = FAGIN_PS;
    }

    public String getFAGIN_CODE() {
        return this.FAGIN_CODE;
    }

    public void setFAGIN_CODE(String FAGIN_CODE) {
        this.FAGIN_CODE = FAGIN_CODE;
    }

    public String getSTS() {
        return this.STS;
    }

    public void setSTS(String STS) {
        this.STS = STS;
    }

    public void checkDefaultValue() {
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("INSFNO", getINSFNO())
            .toString();
    }

}
