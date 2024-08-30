package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Timestamp;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBINS_OUTBUY_MASTVO extends VOBase {

    /** identifier field */
    private BigDecimal KEYNO;

    /** nullable persistent field */
    private String INSSEQ;

    /** nullable persistent field */
    private String CUST_ID;

    /** nullable persistent field */
    private String POLICY_NBR;

    /** nullable persistent field */
    private String PRD_KEYNO;

    /** nullable persistent field */
    private String AO_CODE;

    /** nullable persistent field */
    private String IS_MAIN;

    /** nullable persistent field */
    private String CURR_CD;

    /** nullable persistent field */
    private BigDecimal CURR_RATE;

    /** nullable persistent field */
    private String PAYTYPE;

    /** nullable persistent field */
    private String INSURED_NAME;

    /** nullable persistent field */
    private String INSURED_ID;

    /** nullable persistent field */
    private String INSURED_GENDER;

    /** nullable persistent field */
    private Timestamp INSURED_BIRTHDAY;

    /** nullable persistent field */
    private Timestamp EFFECTED_DATE;

    /** nullable persistent field */
    private BigDecimal INSURED_AGE;

    /** nullable persistent field */
    private String PAYMENTYEAR_SEL;

    /** nullable persistent field */
    private String COVER_TYPE;

    /** nullable persistent field */
    private String COVERYEAR_SEL;

    /** nullable persistent field */
    private String UPTYPE;

    /** nullable persistent field */
    private String UPQTY_SEL;

    /** nullable persistent field */
    private BigDecimal INSUREDAMT;

    /** nullable persistent field */
    private BigDecimal LOCAL_INSUREDAMT;

    /** nullable persistent field */
    private BigDecimal INSYEARFEE;

    /** nullable persistent field */
    private BigDecimal LOCAL_INSYEARFEE;

    /** nullable persistent field */
    private String INSURED_OBJECT;

    /** nullable persistent field */
    private String KIND_SEL;

    /** nullable persistent field */
    private String STATUS;

    /** nullable persistent field */
    private BigDecimal LOCAL_TOTINSYEARFEE;
    
    /** nullable persistent field */
    private String BENEFICIARY_YN;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBINS_OUTBUY_MAST";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBINS_OUTBUY_MASTVO(BigDecimal KEYNO, String INSSEQ, String CUST_ID, String POLICY_NBR, String PRD_KEYNO, String AO_CODE, String IS_MAIN, String CURR_CD, BigDecimal CURR_RATE, String PAYTYPE, String INSURED_NAME, String INSURED_ID, String INSURED_GENDER, Timestamp INSURED_BIRTHDAY, Timestamp EFFECTED_DATE, BigDecimal INSURED_AGE, String PAYMENTYEAR_SEL, String COVER_TYPE, String COVERYEAR_SEL, String UPTYPE, String UPQTY_SEL, BigDecimal INSUREDAMT, BigDecimal LOCAL_INSUREDAMT, BigDecimal INSYEARFEE, BigDecimal LOCAL_INSYEARFEE, String INSURED_OBJECT, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, String KIND_SEL, String STATUS, BigDecimal LOCAL_TOTINSYEARFEE, String BENEFICIARY_YN, Long version) {
        this.KEYNO = KEYNO;
        this.INSSEQ = INSSEQ;
        this.CUST_ID = CUST_ID;
        this.POLICY_NBR = POLICY_NBR;
        this.PRD_KEYNO = PRD_KEYNO;
        this.AO_CODE = AO_CODE;
        this.IS_MAIN = IS_MAIN;
        this.CURR_CD = CURR_CD;
        this.CURR_RATE = CURR_RATE;
        this.PAYTYPE = PAYTYPE;
        this.INSURED_NAME = INSURED_NAME;
        this.INSURED_ID = INSURED_ID;
        this.INSURED_GENDER = INSURED_GENDER;
        this.INSURED_BIRTHDAY = INSURED_BIRTHDAY;
        this.EFFECTED_DATE = EFFECTED_DATE;
        this.INSURED_AGE = INSURED_AGE;
        this.PAYMENTYEAR_SEL = PAYMENTYEAR_SEL;
        this.COVER_TYPE = COVER_TYPE;
        this.COVERYEAR_SEL = COVERYEAR_SEL;
        this.UPTYPE = UPTYPE;
        this.UPQTY_SEL = UPQTY_SEL;
        this.INSUREDAMT = INSUREDAMT;
        this.LOCAL_INSUREDAMT = LOCAL_INSUREDAMT;
        this.INSYEARFEE = INSYEARFEE;
        this.LOCAL_INSYEARFEE = LOCAL_INSYEARFEE;
        this.INSURED_OBJECT = INSURED_OBJECT;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.KIND_SEL = KIND_SEL;
        this.STATUS = STATUS;
        this.LOCAL_TOTINSYEARFEE = LOCAL_TOTINSYEARFEE;
        this.BENEFICIARY_YN = BENEFICIARY_YN;
        this.version = version;
    }

    /** default constructor */
    public TBINS_OUTBUY_MASTVO() {
    }

    /** minimal constructor */
    public TBINS_OUTBUY_MASTVO(BigDecimal KEYNO) {
        this.KEYNO = KEYNO;
    }

    public BigDecimal getKEYNO() {
        return this.KEYNO;
    }

    public void setKEYNO(BigDecimal KEYNO) {
        this.KEYNO = KEYNO;
    }

    public String getINSSEQ() {
        return this.INSSEQ;
    }

    public void setINSSEQ(String INSSEQ) {
        this.INSSEQ = INSSEQ;
    }

    public String getCUST_ID() {
        return this.CUST_ID;
    }

    public void setCUST_ID(String CUST_ID) {
        this.CUST_ID = CUST_ID;
    }

    public String getPOLICY_NBR() {
        return this.POLICY_NBR;
    }

    public void setPOLICY_NBR(String POLICY_NBR) {
        this.POLICY_NBR = POLICY_NBR;
    }

    public String getPRD_KEYNO() {
        return this.PRD_KEYNO;
    }

    public void setPRD_KEYNO(String PRD_KEYNO) {
        this.PRD_KEYNO = PRD_KEYNO;
    }

    public String getAO_CODE() {
        return this.AO_CODE;
    }

    public void setAO_CODE(String AO_CODE) {
        this.AO_CODE = AO_CODE;
    }

    public String getIS_MAIN() {
        return this.IS_MAIN;
    }

    public void setIS_MAIN(String IS_MAIN) {
        this.IS_MAIN = IS_MAIN;
    }

    public String getCURR_CD() {
        return this.CURR_CD;
    }

    public void setCURR_CD(String CURR_CD) {
        this.CURR_CD = CURR_CD;
    }

    public BigDecimal getCURR_RATE() {
        return this.CURR_RATE;
    }

    public void setCURR_RATE(BigDecimal CURR_RATE) {
        this.CURR_RATE = CURR_RATE;
    }

    public String getPAYTYPE() {
        return this.PAYTYPE;
    }

    public void setPAYTYPE(String PAYTYPE) {
        this.PAYTYPE = PAYTYPE;
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

    public String getINSURED_GENDER() {
        return this.INSURED_GENDER;
    }

    public void setINSURED_GENDER(String INSURED_GENDER) {
        this.INSURED_GENDER = INSURED_GENDER;
    }

    public Timestamp getINSURED_BIRTHDAY() {
        return this.INSURED_BIRTHDAY;
    }

    public void setINSURED_BIRTHDAY(Timestamp INSURED_BIRTHDAY) {
        this.INSURED_BIRTHDAY = INSURED_BIRTHDAY;
    }

    public Timestamp getEFFECTED_DATE() {
        return this.EFFECTED_DATE;
    }

    public void setEFFECTED_DATE(Timestamp EFFECTED_DATE) {
        this.EFFECTED_DATE = EFFECTED_DATE;
    }

    public BigDecimal getINSURED_AGE() {
        return this.INSURED_AGE;
    }

    public void setINSURED_AGE(BigDecimal INSURED_AGE) {
        this.INSURED_AGE = INSURED_AGE;
    }

    public String getPAYMENTYEAR_SEL() {
        return this.PAYMENTYEAR_SEL;
    }

    public void setPAYMENTYEAR_SEL(String PAYMENTYEAR_SEL) {
        this.PAYMENTYEAR_SEL = PAYMENTYEAR_SEL;
    }

    public String getCOVER_TYPE() {
        return this.COVER_TYPE;
    }

    public void setCOVER_TYPE(String COVER_TYPE) {
        this.COVER_TYPE = COVER_TYPE;
    }

    public String getCOVERYEAR_SEL() {
        return this.COVERYEAR_SEL;
    }

    public void setCOVERYEAR_SEL(String COVERYEAR_SEL) {
        this.COVERYEAR_SEL = COVERYEAR_SEL;
    }

    public String getUPTYPE() {
        return this.UPTYPE;
    }

    public void setUPTYPE(String UPTYPE) {
        this.UPTYPE = UPTYPE;
    }

    public String getUPQTY_SEL() {
        return this.UPQTY_SEL;
    }

    public void setUPQTY_SEL(String UPQTY_SEL) {
        this.UPQTY_SEL = UPQTY_SEL;
    }

    public BigDecimal getINSUREDAMT() {
        return this.INSUREDAMT;
    }

    public void setINSUREDAMT(BigDecimal INSUREDAMT) {
        this.INSUREDAMT = INSUREDAMT;
    }

    public BigDecimal getLOCAL_INSUREDAMT() {
        return this.LOCAL_INSUREDAMT;
    }

    public void setLOCAL_INSUREDAMT(BigDecimal LOCAL_INSUREDAMT) {
        this.LOCAL_INSUREDAMT = LOCAL_INSUREDAMT;
    }

    public BigDecimal getINSYEARFEE() {
        return this.INSYEARFEE;
    }

    public void setINSYEARFEE(BigDecimal INSYEARFEE) {
        this.INSYEARFEE = INSYEARFEE;
    }

    public BigDecimal getLOCAL_INSYEARFEE() {
        return this.LOCAL_INSYEARFEE;
    }

    public void setLOCAL_INSYEARFEE(BigDecimal LOCAL_INSYEARFEE) {
        this.LOCAL_INSYEARFEE = LOCAL_INSYEARFEE;
    }

    public String getINSURED_OBJECT() {
        return this.INSURED_OBJECT;
    }

    public void setINSURED_OBJECT(String INSURED_OBJECT) {
        this.INSURED_OBJECT = INSURED_OBJECT;
    }

    public String getKIND_SEL() {
        return this.KIND_SEL;
    }

    public void setKIND_SEL(String KIND_SEL) {
        this.KIND_SEL = KIND_SEL;
    }

    public String getSTATUS() {
        return this.STATUS;
    }

    public void setSTATUS(String STATUS) {
        this.STATUS = STATUS;
    }

    public BigDecimal getLOCAL_TOTINSYEARFEE() {
        return this.LOCAL_TOTINSYEARFEE;
    }

    public void setLOCAL_TOTINSYEARFEE(BigDecimal LOCAL_TOTINSYEARFEE) {
        this.LOCAL_TOTINSYEARFEE = LOCAL_TOTINSYEARFEE;
    }

    public String getBENEFICIARY_YN() {
		return BENEFICIARY_YN;
	}

	public void setBENEFICIARY_YN(String bENEFICIARY_YN) {
		BENEFICIARY_YN = bENEFICIARY_YN;
	}

	public void checkDefaultValue() {
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("KEYNO", getKEYNO())
            .toString();
    }

}
