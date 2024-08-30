package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBMTC_CONTRACT_MAINVO extends VOBase {

    /** identifier field */
    private String CON_NO;

    /** nullable persistent field */
    private String CON_TYPE;

    /** nullable persistent field */
    private String CUST_ID;

    /** nullable persistent field */
    private String CON_CURR;

    /** nullable persistent field */
    private String CURR1;

    /** nullable persistent field */
    private BigDecimal AMT1;

    /** nullable persistent field */
    private String CURR2;

    /** nullable persistent field */
    private BigDecimal AMT2;

    /** nullable persistent field */
    private String CURR3;

    /** nullable persistent field */
    private BigDecimal AMT3;

    /** nullable persistent field */
    private BigDecimal SIGNING_FEE;

    /** nullable persistent field */
    private BigDecimal MODIFY_FEE;

    /** nullable persistent field */
    private BigDecimal MNG_FEE_RATE1;

    /** nullable persistent field */
    private BigDecimal MNG_FEE_RATE2;

    /** nullable persistent field */
    private BigDecimal MNG_FEE_RATE3;

    /** nullable persistent field */
    private BigDecimal MNG_FEE_MIN;

    /** nullable persistent field */
    private BigDecimal END_YEARS;

    /** nullable persistent field */
    private BigDecimal END_AMT_LIMIT;

    /** nullable persistent field */
    private String TERM_CON;

    /** nullable persistent field */
    private String MODI_CON;

    /** nullable persistent field */
    private String DISC_CON;

    /** nullable persistent field */
    private String APP_SUP;

    /** nullable persistent field */
    private String DISC_ID;

    /** nullable persistent field */
    private String PAY_TYPE;

    /** nullable persistent field */
    private String SIP_TYPE;

    /** nullable persistent field */
    private String APL_CON;

    /** nullable persistent field */
    private String APL_DOC_YN;

    /** nullable persistent field */
    private String BEN_AGE_YN1;

    /** nullable persistent field */
    private BigDecimal BEN_AGE1;

    /** nullable persistent field */
    private String BEN_CURR1;

    /** nullable persistent field */
    private BigDecimal BEN_AMT1;

    /** nullable persistent field */
    private String BEN_AGE_YN2;

    /** nullable persistent field */
    private BigDecimal BEN_AGE2;

    /** nullable persistent field */
    private BigDecimal BEN_AGE3;

    /** nullable persistent field */
    private String BEN_CURR2;

    /** nullable persistent field */
    private BigDecimal BEN_AMT2;

    /** nullable persistent field */
    private String BEN_AGE_YN3;

    /** nullable persistent field */
    private BigDecimal BEN_AGE4;

    /** nullable persistent field */
    private String BEN_CURR3;

    /** nullable persistent field */
    private BigDecimal BEN_AMT3;

    /** nullable persistent field */
    private String AGR_MON_TYPE;

    /** nullable persistent field */
    private String M_CURR;

    /** nullable persistent field */
    private BigDecimal M_AMT;

    /** nullable persistent field */
    private String A_MONTHS;

    /** nullable persistent field */
    private String A_CURR;

    /** nullable persistent field */
    private BigDecimal A_AMT;

    /** nullable persistent field */
    private String UNLIMIT_DOC_YN;

    /** nullable persistent field */
    private String UNLIMIT_DOC_TYPE;

    /** nullable persistent field */
    private String LIMIT_DOC_YN;

    /** nullable persistent field */
    private String LIMIT_DOC_TYPE;

    /** nullable persistent field */
    private String EDU_YN;

    /** nullable persistent field */
    private String MED_YN;

    /** nullable persistent field */
    private String MED_PAY_FOR;

    /** nullable persistent field */
    private String NUR_YN;

    /** nullable persistent field */
    private String NUR_PAY_FOR;

    /** nullable persistent field */
    private String OTR_YN;

    /** nullable persistent field */
    private String MAR_CURR;

    /** nullable persistent field */
    private BigDecimal MAR_AMT;

    /** nullable persistent field */
    private String BIR_CURR;

    /** nullable persistent field */
    private BigDecimal BIR_AMT;

    /** nullable persistent field */
    private String HOS_CURR;

    /** nullable persistent field */
    private BigDecimal HOS_AMT;

    /** nullable persistent field */
    private String OTR_CURR;

    /** nullable persistent field */
    private BigDecimal OTR_AMT;
    
    /** nullable persistent field */
    private String OTR_ITEM;
    
    /** nullable persistent field */
    private String CON_STATUS;
    
    /** nullable persistent field */
    private String NMP2YFWK_STATUS;
    
    /** nullable persistent field */
    private String NMP2YFWK_NO;
    
    /** nullable persistent field */
    private String NMP2YFWK_ACC;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBMTC_CONTRACT_MAIN";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBMTC_CONTRACT_MAINVO(String CON_NO, String CON_TYPE, String CUST_ID, String CON_CURR, String CURR1, BigDecimal AMT1, String CURR2, BigDecimal AMT2, String CURR3, BigDecimal AMT3, BigDecimal SIGNING_FEE, BigDecimal MODIFY_FEE, BigDecimal MNG_FEE_RATE1, BigDecimal MNG_FEE_RATE2, BigDecimal MNG_FEE_RATE3, BigDecimal MNG_FEE_MIN, BigDecimal END_YEARS, BigDecimal END_AMT_LIMIT, String TERM_CON, String MODI_CON, String DISC_CON, String APP_SUP, String DISC_ID, String PAY_TYPE, String SIP_TYPE, String APL_CON, String APL_DOC_YN, String BEN_AGE_YN1, BigDecimal BEN_AGE1, String BEN_CURR1, BigDecimal BEN_AMT1, String BEN_AGE_YN2, BigDecimal BEN_AGE2, BigDecimal BEN_AGE3, String BEN_CURR2, BigDecimal BEN_AMT2, String BEN_AGE_YN3, BigDecimal BEN_AGE4, String BEN_CURR3, BigDecimal BEN_AMT3, String AGR_MON_TYPE, String M_CURR, BigDecimal M_AMT, String A_MONTHS, String A_CURR, BigDecimal A_AMT, String UNLIMIT_DOC_YN, String UNLIMIT_DOC_TYPE, String LIMIT_DOC_YN, String LIMIT_DOC_TYPE, String EDU_YN, String MED_YN, String MED_PAY_FOR, String NUR_YN, String NUR_PAY_FOR, String OTR_YN, String MAR_CURR, BigDecimal MAR_AMT, String BIR_CURR, BigDecimal BIR_AMT, String HOS_CURR, BigDecimal HOS_AMT, String OTR_CURR, BigDecimal OTR_AMT, String OTR_ITEM, String CON_STATUS, String NMP2YFWK_STATUS, String NMP2YFWK_NO, String NMP2YFWK_ACC, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, Long version) {
        this.CON_NO = CON_NO;
        this.CON_TYPE = CON_TYPE;
        this.CUST_ID = CUST_ID;
        this.CON_CURR = CON_CURR;
        this.CURR1 = CURR1;
        this.AMT1 = AMT1;
        this.CURR2 = CURR2;
        this.AMT2 = AMT2;
        this.CURR3 = CURR3;
        this.AMT3 = AMT3;
        this.SIGNING_FEE = SIGNING_FEE;
        this.MODIFY_FEE = MODIFY_FEE;
        this.MNG_FEE_RATE1 = MNG_FEE_RATE1;
        this.MNG_FEE_RATE2 = MNG_FEE_RATE2;
        this.MNG_FEE_RATE3 = MNG_FEE_RATE3;
        this.MNG_FEE_MIN = MNG_FEE_MIN;
        this.END_YEARS = END_YEARS;
        this.END_AMT_LIMIT = END_AMT_LIMIT;
        this.TERM_CON = TERM_CON;
        this.MODI_CON = MODI_CON;
        this.DISC_CON = DISC_CON;
        this.APP_SUP = APP_SUP;
        this.DISC_ID = DISC_ID;
        this.PAY_TYPE = PAY_TYPE;
        this.SIP_TYPE = SIP_TYPE;
        this.APL_CON = APL_CON;
        this.APL_DOC_YN = APL_DOC_YN;
        this.BEN_AGE_YN1 = BEN_AGE_YN1;
        this.BEN_AGE1 = BEN_AGE1;
        this.BEN_CURR1 = BEN_CURR1;
        this.BEN_AMT1 = BEN_AMT1;
        this.BEN_AGE_YN2 = BEN_AGE_YN2;
        this.BEN_AGE2 = BEN_AGE2;
        this.BEN_AGE3 = BEN_AGE3;
        this.BEN_CURR2 = BEN_CURR2;
        this.BEN_AMT2 = BEN_AMT2;
        this.BEN_AGE_YN3 = BEN_AGE_YN3;
        this.BEN_AGE4 = BEN_AGE4;
        this.BEN_CURR3 = BEN_CURR3;
        this.BEN_AMT3 = BEN_AMT3;
        this.AGR_MON_TYPE = AGR_MON_TYPE;
        this.M_CURR = M_CURR;
        this.M_AMT = M_AMT;
        this.A_MONTHS = A_MONTHS;
        this.A_CURR = A_CURR;
        this.A_AMT = A_AMT;
        this.UNLIMIT_DOC_YN = UNLIMIT_DOC_YN;
        this.UNLIMIT_DOC_TYPE = UNLIMIT_DOC_TYPE;
        this.LIMIT_DOC_YN = LIMIT_DOC_YN;
        this.LIMIT_DOC_TYPE = LIMIT_DOC_TYPE;
        this.EDU_YN = EDU_YN;
        this.MED_YN = MED_YN;
        this.MED_PAY_FOR = MED_PAY_FOR;
        this.NUR_YN = NUR_YN;
        this.NUR_PAY_FOR = NUR_PAY_FOR;
        this.OTR_YN = OTR_YN;
        this.MAR_CURR = MAR_CURR;
        this.MAR_AMT = MAR_AMT;
        this.BIR_CURR = BIR_CURR;
        this.BIR_AMT = BIR_AMT;
        this.HOS_CURR = HOS_CURR;
        this.HOS_AMT = HOS_AMT;
        this.OTR_CURR = OTR_CURR;
        this.OTR_AMT = OTR_AMT;
        this.OTR_ITEM = OTR_ITEM;
        this.CON_STATUS = CON_STATUS;
        this.NMP2YFWK_STATUS = NMP2YFWK_STATUS;
        this.NMP2YFWK_NO = NMP2YFWK_NO;
        this.NMP2YFWK_ACC = NMP2YFWK_ACC;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.version = version;
    }

    /** default constructor */
    public TBMTC_CONTRACT_MAINVO() {
    }

    /** minimal constructor */
    public TBMTC_CONTRACT_MAINVO(String CON_NO) {
        this.CON_NO = CON_NO;
    }

    public String getCON_NO() {
        return this.CON_NO;
    }

    public void setCON_NO(String CON_NO) {
        this.CON_NO = CON_NO;
    }

    public String getCON_TYPE() {
        return this.CON_TYPE;
    }

    public void setCON_TYPE(String CON_TYPE) {
        this.CON_TYPE = CON_TYPE;
    }

    public String getCUST_ID() {
        return this.CUST_ID;
    }

    public void setCUST_ID(String CUST_ID) {
        this.CUST_ID = CUST_ID;
    }

    public String getCON_CURR() {
        return this.CON_CURR;
    }

    public void setCON_CURR(String CON_CURR) {
        this.CON_CURR = CON_CURR;
    }

    public String getCURR1() {
        return this.CURR1;
    }

    public void setCURR1(String CURR1) {
        this.CURR1 = CURR1;
    }

    public BigDecimal getAMT1() {
        return this.AMT1;
    }

    public void setAMT1(BigDecimal AMT1) {
        this.AMT1 = AMT1;
    }

    public String getCURR2() {
        return this.CURR2;
    }

    public void setCURR2(String CURR2) {
        this.CURR2 = CURR2;
    }

    public BigDecimal getAMT2() {
        return this.AMT2;
    }

    public void setAMT2(BigDecimal AMT2) {
        this.AMT2 = AMT2;
    }

    public String getCURR3() {
        return this.CURR3;
    }

    public void setCURR3(String CURR3) {
        this.CURR3 = CURR3;
    }

    public BigDecimal getAMT3() {
        return this.AMT3;
    }

    public void setAMT3(BigDecimal AMT3) {
        this.AMT3 = AMT3;
    }

    public BigDecimal getSIGNING_FEE() {
        return this.SIGNING_FEE;
    }

    public void setSIGNING_FEE(BigDecimal SIGNING_FEE) {
        this.SIGNING_FEE = SIGNING_FEE;
    }

    public BigDecimal getMODIFY_FEE() {
        return this.MODIFY_FEE;
    }

    public void setMODIFY_FEE(BigDecimal MODIFY_FEE) {
        this.MODIFY_FEE = MODIFY_FEE;
    }

    public BigDecimal getMNG_FEE_RATE1() {
        return this.MNG_FEE_RATE1;
    }

    public void setMNG_FEE_RATE1(BigDecimal MNG_FEE_RATE1) {
        this.MNG_FEE_RATE1 = MNG_FEE_RATE1;
    }

    public BigDecimal getMNG_FEE_RATE2() {
        return this.MNG_FEE_RATE2;
    }

    public void setMNG_FEE_RATE2(BigDecimal MNG_FEE_RATE2) {
        this.MNG_FEE_RATE2 = MNG_FEE_RATE2;
    }

    public BigDecimal getMNG_FEE_RATE3() {
        return this.MNG_FEE_RATE3;
    }

    public void setMNG_FEE_RATE3(BigDecimal MNG_FEE_RATE3) {
        this.MNG_FEE_RATE3 = MNG_FEE_RATE3;
    }

    public BigDecimal getMNG_FEE_MIN() {
        return this.MNG_FEE_MIN;
    }

    public void setMNG_FEE_MIN(BigDecimal MNG_FEE_MIN) {
        this.MNG_FEE_MIN = MNG_FEE_MIN;
    }

    public BigDecimal getEND_YEARS() {
        return this.END_YEARS;
    }

    public void setEND_YEARS(BigDecimal END_YEARS) {
        this.END_YEARS = END_YEARS;
    }

    public BigDecimal getEND_AMT_LIMIT() {
        return this.END_AMT_LIMIT;
    }

    public void setEND_AMT_LIMIT(BigDecimal END_AMT_LIMIT) {
        this.END_AMT_LIMIT = END_AMT_LIMIT;
    }

    public String getTERM_CON() {
        return this.TERM_CON;
    }

    public void setTERM_CON(String TERM_CON) {
        this.TERM_CON = TERM_CON;
    }

    public String getMODI_CON() {
        return this.MODI_CON;
    }

    public void setMODI_CON(String MODI_CON) {
        this.MODI_CON = MODI_CON;
    }

    public String getDISC_CON() {
        return this.DISC_CON;
    }

    public void setDISC_CON(String DISC_CON) {
        this.DISC_CON = DISC_CON;
    }

    public String getAPP_SUP() {
        return this.APP_SUP;
    }

    public void setAPP_SUP(String APP_SUP) {
        this.APP_SUP = APP_SUP;
    }

    public String getDISC_ID() {
        return this.DISC_ID;
    }

    public void setDISC_ID(String DISC_ID) {
        this.DISC_ID = DISC_ID;
    }

    public String getPAY_TYPE() {
        return this.PAY_TYPE;
    }

    public void setPAY_TYPE(String PAY_TYPE) {
        this.PAY_TYPE = PAY_TYPE;
    }

    public String getSIP_TYPE() {
        return this.SIP_TYPE;
    }

    public void setSIP_TYPE(String SIP_TYPE) {
        this.SIP_TYPE = SIP_TYPE;
    }

    public String getAPL_CON() {
        return this.APL_CON;
    }

    public void setAPL_CON(String APL_CON) {
        this.APL_CON = APL_CON;
    }

    public String getAPL_DOC_YN() {
        return this.APL_DOC_YN;
    }

    public void setAPL_DOC_YN(String APL_DOC_YN) {
        this.APL_DOC_YN = APL_DOC_YN;
    }

    public String getBEN_AGE_YN1() {
        return this.BEN_AGE_YN1;
    }

    public void setBEN_AGE_YN1(String BEN_AGE_YN1) {
        this.BEN_AGE_YN1 = BEN_AGE_YN1;
    }

    public BigDecimal getBEN_AGE1() {
        return this.BEN_AGE1;
    }

    public void setBEN_AGE1(BigDecimal BEN_AGE1) {
        this.BEN_AGE1 = BEN_AGE1;
    }

    public String getBEN_CURR1() {
        return this.BEN_CURR1;
    }

    public void setBEN_CURR1(String BEN_CURR1) {
        this.BEN_CURR1 = BEN_CURR1;
    }

    public BigDecimal getBEN_AMT1() {
        return this.BEN_AMT1;
    }

    public void setBEN_AMT1(BigDecimal BEN_AMT1) {
        this.BEN_AMT1 = BEN_AMT1;
    }

    public String getBEN_AGE_YN2() {
        return this.BEN_AGE_YN2;
    }

    public void setBEN_AGE_YN2(String BEN_AGE_YN2) {
        this.BEN_AGE_YN2 = BEN_AGE_YN2;
    }

    public BigDecimal getBEN_AGE2() {
        return this.BEN_AGE2;
    }

    public void setBEN_AGE2(BigDecimal BEN_AGE2) {
        this.BEN_AGE2 = BEN_AGE2;
    }

    public BigDecimal getBEN_AGE3() {
        return this.BEN_AGE3;
    }

    public void setBEN_AGE3(BigDecimal BEN_AGE3) {
        this.BEN_AGE3 = BEN_AGE3;
    }

    public String getBEN_CURR2() {
        return this.BEN_CURR2;
    }

    public void setBEN_CURR2(String BEN_CURR2) {
        this.BEN_CURR2 = BEN_CURR2;
    }

    public BigDecimal getBEN_AMT2() {
        return this.BEN_AMT2;
    }

    public void setBEN_AMT2(BigDecimal BEN_AMT2) {
        this.BEN_AMT2 = BEN_AMT2;
    }

    public String getBEN_AGE_YN3() {
        return this.BEN_AGE_YN3;
    }

    public void setBEN_AGE_YN3(String BEN_AGE_YN3) {
        this.BEN_AGE_YN3 = BEN_AGE_YN3;
    }

    public BigDecimal getBEN_AGE4() {
        return this.BEN_AGE4;
    }

    public void setBEN_AGE4(BigDecimal BEN_AGE4) {
        this.BEN_AGE4 = BEN_AGE4;
    }

    public String getBEN_CURR3() {
        return this.BEN_CURR3;
    }

    public void setBEN_CURR3(String BEN_CURR3) {
        this.BEN_CURR3 = BEN_CURR3;
    }

    public BigDecimal getBEN_AMT3() {
        return this.BEN_AMT3;
    }

    public void setBEN_AMT3(BigDecimal BEN_AMT3) {
        this.BEN_AMT3 = BEN_AMT3;
    }

    public String getAGR_MON_TYPE() {
        return this.AGR_MON_TYPE;
    }

    public void setAGR_MON_TYPE(String AGR_MON_TYPE) {
        this.AGR_MON_TYPE = AGR_MON_TYPE;
    }

    public String getM_CURR() {
        return this.M_CURR;
    }

    public void setM_CURR(String M_CURR) {
        this.M_CURR = M_CURR;
    }

    public BigDecimal getM_AMT() {
        return this.M_AMT;
    }

    public void setM_AMT(BigDecimal M_AMT) {
        this.M_AMT = M_AMT;
    }

    public String getA_MONTHS() {
        return this.A_MONTHS;
    }

    public void setA_MONTHS(String A_MONTHS) {
        this.A_MONTHS = A_MONTHS;
    }

    public String getA_CURR() {
        return this.A_CURR;
    }

    public void setA_CURR(String A_CURR) {
        this.A_CURR = A_CURR;
    }

    public BigDecimal getA_AMT() {
        return this.A_AMT;
    }

    public void setA_AMT(BigDecimal A_AMT) {
        this.A_AMT = A_AMT;
    }

    public String getUNLIMIT_DOC_YN() {
        return this.UNLIMIT_DOC_YN;
    }

    public void setUNLIMIT_DOC_YN(String UNLIMIT_DOC_YN) {
        this.UNLIMIT_DOC_YN = UNLIMIT_DOC_YN;
    }

    public String getUNLIMIT_DOC_TYPE() {
        return this.UNLIMIT_DOC_TYPE;
    }

    public void setUNLIMIT_DOC_TYPE(String UNLIMIT_DOC_TYPE) {
        this.UNLIMIT_DOC_TYPE = UNLIMIT_DOC_TYPE;
    }

    public String getLIMIT_DOC_YN() {
        return this.LIMIT_DOC_YN;
    }

    public void setLIMIT_DOC_YN(String LIMIT_DOC_YN) {
        this.LIMIT_DOC_YN = LIMIT_DOC_YN;
    }

    public String getLIMIT_DOC_TYPE() {
        return this.LIMIT_DOC_TYPE;
    }

    public void setLIMIT_DOC_TYPE(String LIMIT_DOC_TYPE) {
        this.LIMIT_DOC_TYPE = LIMIT_DOC_TYPE;
    }

    public String getEDU_YN() {
        return this.EDU_YN;
    }

    public void setEDU_YN(String EDU_YN) {
        this.EDU_YN = EDU_YN;
    }

    public String getMED_YN() {
        return this.MED_YN;
    }

    public void setMED_YN(String MED_YN) {
        this.MED_YN = MED_YN;
    }

    public String getMED_PAY_FOR() {
        return this.MED_PAY_FOR;
    }

    public void setMED_PAY_FOR(String MED_PAY_FOR) {
        this.MED_PAY_FOR = MED_PAY_FOR;
    }

    public String getNUR_YN() {
        return this.NUR_YN;
    }

    public void setNUR_YN(String NUR_YN) {
        this.NUR_YN = NUR_YN;
    }

    public String getNUR_PAY_FOR() {
        return this.NUR_PAY_FOR;
    }

    public void setNUR_PAY_FOR(String NUR_PAY_FOR) {
        this.NUR_PAY_FOR = NUR_PAY_FOR;
    }

    public String getOTR_YN() {
        return this.OTR_YN;
    }

    public void setOTR_YN(String OTR_YN) {
        this.OTR_YN = OTR_YN;
    }

    public String getMAR_CURR() {
        return this.MAR_CURR;
    }

    public void setMAR_CURR(String MAR_CURR) {
        this.MAR_CURR = MAR_CURR;
    }

    public BigDecimal getMAR_AMT() {
        return this.MAR_AMT;
    }

    public void setMAR_AMT(BigDecimal MAR_AMT) {
        this.MAR_AMT = MAR_AMT;
    }

    public String getBIR_CURR() {
        return this.BIR_CURR;
    }

    public void setBIR_CURR(String BIR_CURR) {
        this.BIR_CURR = BIR_CURR;
    }

    public BigDecimal getBIR_AMT() {
        return this.BIR_AMT;
    }

    public void setBIR_AMT(BigDecimal BIR_AMT) {
        this.BIR_AMT = BIR_AMT;
    }

    public String getHOS_CURR() {
        return this.HOS_CURR;
    }

    public void setHOS_CURR(String HOS_CURR) {
        this.HOS_CURR = HOS_CURR;
    }

    public BigDecimal getHOS_AMT() {
        return this.HOS_AMT;
    }

    public void setHOS_AMT(BigDecimal HOS_AMT) {
        this.HOS_AMT = HOS_AMT;
    }

    public String getOTR_CURR() {
        return this.OTR_CURR;
    }

    public void setOTR_CURR(String OTR_CURR) {
        this.OTR_CURR = OTR_CURR;
    }

    public BigDecimal getOTR_AMT() {
        return this.OTR_AMT;
    }

    public void setOTR_AMT(BigDecimal OTR_AMT) {
        this.OTR_AMT = OTR_AMT;
    }

    public String getOTR_ITEM() {
		return OTR_ITEM;
	}

	public void setOTR_ITEM(String oTR_ITEM) {
		OTR_ITEM = oTR_ITEM;
	}

	public String getCON_STATUS() {
        return this.CON_STATUS;
    }

    public void setCON_STATUS(String CON_STATUS) {
        this.CON_STATUS = CON_STATUS;
    }

    public String getNMP2YFWK_STATUS() {
		return NMP2YFWK_STATUS;
	}

	public void setNMP2YFWK_STATUS(String nMP2YFWK_STATUS) {
		NMP2YFWK_STATUS = nMP2YFWK_STATUS;
	}

	public String getNMP2YFWK_NO() {
		return NMP2YFWK_NO;
	}

	public void setNMP2YFWK_NO(String nMP2YFWK_NO) {
		NMP2YFWK_NO = nMP2YFWK_NO;
	}

	public String getNMP2YFWK_ACC() {
		return NMP2YFWK_ACC;
	}

	public void setNMP2YFWK_ACC(String nMP2YFWK_ACC) {
		NMP2YFWK_ACC = nMP2YFWK_ACC;
	}

	public void checkDefaultValue() {
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("CON_NO", getCON_NO())
            .toString();
    }

}
