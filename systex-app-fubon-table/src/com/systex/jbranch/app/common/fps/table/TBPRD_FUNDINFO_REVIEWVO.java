package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Timestamp;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBPRD_FUNDINFO_REVIEWVO extends VOBase {

    /** identifier field */
    private BigDecimal SEQ;

    /** nullable persistent field */
    private String PRD_ID;

    /** nullable persistent field */
    private String LIPPER_ID;

    /** nullable persistent field */
    private BigDecimal ALLOTMENT_RATIO;

    /** nullable persistent field */
    private String MAIN_PRD;

    /** nullable persistent field */
    private Timestamp MAIN_PRD_SDATE;

    /** nullable persistent field */
    private Timestamp MAIN_PRD_EDATE;

    /** nullable persistent field */
    private String RAISE_FUND;

    /** nullable persistent field */
    private Timestamp RAISE_FUND_SDATE;

    /** nullable persistent field */
    private Timestamp RAISE_FUND_EDATE;

    /** nullable persistent field */
    private String IPO;

    /** nullable persistent field */
    private Timestamp IPO_SDATE;

    /** nullable persistent field */
    private Timestamp IPO_EDATE;

    /** nullable persistent field */
    private BigDecimal CNR_YIELD;

    /** nullable persistent field */
    private BigDecimal CNR_MULTIPLE;

    /** nullable persistent field */
    private BigDecimal CNR_FEE;

    /** nullable persistent field */
    private String FUS40;

    /** nullable persistent field */
    private String NO_E_PURCHASE;

    /** nullable persistent field */
    private String NO_E_OUT;

    /** nullable persistent field */
    private String NO_E_IN;

    /** nullable persistent field */
    private String NO_E_BUYBACK;

    /** nullable persistent field */
    private String QUOTA_CONTROL;

    /** nullable persistent field */
    private BigDecimal Y_RETURN;

    /** nullable persistent field */
    private BigDecimal Y_STD;

    /** nullable persistent field */
    private Timestamp SDATE;

    /** nullable persistent field */
    private Timestamp EDATE;

    /** nullable persistent field */
    private BigDecimal LIPPER_RANK;

    /** nullable persistent field */
    private BigDecimal LIPPER_BENCHMARK_ID;

    /** nullable persistent field */
    private String FUS20;

    /** nullable persistent field */
    private Timestamp MULTIPLE_SDATE;

    /** nullable persistent field */
    private Timestamp MULTIPLE_EDATE;

    /** nullable persistent field */
    private BigDecimal CNR_DISCOUNT;

    /** nullable persistent field */
    private BigDecimal RATE_DISCOUNT;

    /** nullable persistent field */
    private String IS_CNR_TARGET;

    /** nullable persistent field */
    private Timestamp CNR_TARGET_SDATE;

    /** nullable persistent field */
    private Timestamp CNR_TARGET_EDATE;

    /** nullable persistent field */
    private String ACT_TYPE;

    /** nullable persistent field */
    private String REVIEW_STATUS;

    /** nullable persistent field */
    private String SELLING;
    
    /** nullable persistent field */
    private String VIGILANT;
    
    /** nullable persistent field */
    private String PROJECT1;
    
    /** nullable persistent field */
    private String PROJECT2;
    
    /** nullable persistent field */
    private String CUSTOMER_LEVEL;


public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBPRD_FUNDINFO_REVIEW";


public String getTableuid () {
    return TABLE_UID;
}

    /** full constructor */
    public TBPRD_FUNDINFO_REVIEWVO(BigDecimal SEQ, String PRD_ID, String LIPPER_ID, BigDecimal ALLOTMENT_RATIO, String MAIN_PRD, Timestamp MAIN_PRD_SDATE, Timestamp MAIN_PRD_EDATE, String RAISE_FUND, Timestamp RAISE_FUND_SDATE, Timestamp RAISE_FUND_EDATE, String IPO, Timestamp IPO_SDATE, Timestamp IPO_EDATE, BigDecimal CNR_YIELD, BigDecimal CNR_MULTIPLE, BigDecimal CNR_FEE, String FUS40, String NO_E_PURCHASE, String NO_E_OUT, String NO_E_IN, String NO_E_BUYBACK, String QUOTA_CONTROL, BigDecimal Y_RETURN, BigDecimal Y_STD, Timestamp SDATE, Timestamp EDATE, BigDecimal LIPPER_RANK, BigDecimal LIPPER_BENCHMARK_ID, String FUS20, Timestamp MULTIPLE_SDATE, Timestamp MULTIPLE_EDATE, BigDecimal CNR_DISCOUNT, BigDecimal RATE_DISCOUNT, String IS_CNR_TARGET, Timestamp CNR_TARGET_SDATE, Timestamp CNR_TARGET_EDATE, String ACT_TYPE, String REVIEW_STATUS, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, String SELLING, Long version, String VIGILANT, String PROJECT1, String PROJECT2, String CUSTOMER_LEVEL) {
        this.SEQ = SEQ;
        this.PRD_ID = PRD_ID;
        this.LIPPER_ID = LIPPER_ID;
        this.ALLOTMENT_RATIO = ALLOTMENT_RATIO;
        this.MAIN_PRD = MAIN_PRD;
        this.MAIN_PRD_SDATE = MAIN_PRD_SDATE;
        this.MAIN_PRD_EDATE = MAIN_PRD_EDATE;
        this.RAISE_FUND = RAISE_FUND;
        this.RAISE_FUND_SDATE = RAISE_FUND_SDATE;
        this.RAISE_FUND_EDATE = RAISE_FUND_EDATE;
        this.IPO = IPO;
        this.IPO_SDATE = IPO_SDATE;
        this.IPO_EDATE = IPO_EDATE;
        this.CNR_YIELD = CNR_YIELD;
        this.CNR_MULTIPLE = CNR_MULTIPLE;
        this.CNR_FEE = CNR_FEE;
        this.FUS40 = FUS40;
        this.NO_E_PURCHASE = NO_E_PURCHASE;
        this.NO_E_OUT = NO_E_OUT;
        this.NO_E_IN = NO_E_IN;
        this.NO_E_BUYBACK = NO_E_BUYBACK;
        this.QUOTA_CONTROL = QUOTA_CONTROL;
        this.Y_RETURN = Y_RETURN;
        this.Y_STD = Y_STD;
        this.SDATE = SDATE;
        this.EDATE = EDATE;
        this.LIPPER_RANK = LIPPER_RANK;
        this.LIPPER_BENCHMARK_ID = LIPPER_BENCHMARK_ID;
        this.FUS20 = FUS20;
        this.MULTIPLE_SDATE = MULTIPLE_SDATE;
        this.MULTIPLE_EDATE = MULTIPLE_EDATE;
        this.CNR_DISCOUNT = CNR_DISCOUNT;
        this.RATE_DISCOUNT = RATE_DISCOUNT;
        this.IS_CNR_TARGET = IS_CNR_TARGET;
        this.CNR_TARGET_SDATE = CNR_TARGET_SDATE;
        this.CNR_TARGET_EDATE = CNR_TARGET_EDATE;
        this.ACT_TYPE = ACT_TYPE;
        this.REVIEW_STATUS = REVIEW_STATUS;
        this.createtime = createtime;
        this.creator = creator;
        this.modifier = modifier;
        this.lastupdate = lastupdate;
        this.SELLING = SELLING;
        this.version = version;
        this.VIGILANT = VIGILANT;
        this.PROJECT1 = PROJECT1;
        this.PROJECT2 = PROJECT2;
        this.CUSTOMER_LEVEL = CUSTOMER_LEVEL;
    }

    /** default constructor */
    public TBPRD_FUNDINFO_REVIEWVO() {
    }

    /** minimal constructor */
    public TBPRD_FUNDINFO_REVIEWVO(BigDecimal SEQ) {
        this.SEQ = SEQ;
    }

    public BigDecimal getSEQ() {
        return this.SEQ;
    }

    public void setSEQ(BigDecimal SEQ) {
        this.SEQ = SEQ;
    }

    public String getPRD_ID() {
        return this.PRD_ID;
    }

    public void setPRD_ID(String PRD_ID) {
        this.PRD_ID = PRD_ID;
    }

    public String getLIPPER_ID() {
        return this.LIPPER_ID;
    }

    public void setLIPPER_ID(String LIPPER_ID) {
        this.LIPPER_ID = LIPPER_ID;
    }

    public BigDecimal getALLOTMENT_RATIO() {
        return this.ALLOTMENT_RATIO;
    }

    public void setALLOTMENT_RATIO(BigDecimal ALLOTMENT_RATIO) {
        this.ALLOTMENT_RATIO = ALLOTMENT_RATIO;
    }

    public String getMAIN_PRD() {
        return this.MAIN_PRD;
    }

    public void setMAIN_PRD(String MAIN_PRD) {
        this.MAIN_PRD = MAIN_PRD;
    }

    public Timestamp getMAIN_PRD_SDATE() {
        return this.MAIN_PRD_SDATE;
    }

    public void setMAIN_PRD_SDATE(Timestamp MAIN_PRD_SDATE) {
        this.MAIN_PRD_SDATE = MAIN_PRD_SDATE;
    }

    public Timestamp getMAIN_PRD_EDATE() {
        return this.MAIN_PRD_EDATE;
    }

    public void setMAIN_PRD_EDATE(Timestamp MAIN_PRD_EDATE) {
        this.MAIN_PRD_EDATE = MAIN_PRD_EDATE;
    }

    public String getRAISE_FUND() {
        return this.RAISE_FUND;
    }

    public void setRAISE_FUND(String RAISE_FUND) {
        this.RAISE_FUND = RAISE_FUND;
    }

    public Timestamp getRAISE_FUND_SDATE() {
        return this.RAISE_FUND_SDATE;
    }

    public void setRAISE_FUND_SDATE(Timestamp RAISE_FUND_SDATE) {
        this.RAISE_FUND_SDATE = RAISE_FUND_SDATE;
    }

    public Timestamp getRAISE_FUND_EDATE() {
        return this.RAISE_FUND_EDATE;
    }

    public void setRAISE_FUND_EDATE(Timestamp RAISE_FUND_EDATE) {
        this.RAISE_FUND_EDATE = RAISE_FUND_EDATE;
    }

    public String getIPO() {
        return this.IPO;
    }

    public void setIPO(String IPO) {
        this.IPO = IPO;
    }

    public Timestamp getIPO_SDATE() {
        return this.IPO_SDATE;
    }

    public void setIPO_SDATE(Timestamp IPO_SDATE) {
        this.IPO_SDATE = IPO_SDATE;
    }

    public Timestamp getIPO_EDATE() {
        return this.IPO_EDATE;
    }

    public void setIPO_EDATE(Timestamp IPO_EDATE) {
        this.IPO_EDATE = IPO_EDATE;
    }

    public BigDecimal getCNR_YIELD() {
        return this.CNR_YIELD;
    }

    public void setCNR_YIELD(BigDecimal CNR_YIELD) {
        this.CNR_YIELD = CNR_YIELD;
    }

    public BigDecimal getCNR_MULTIPLE() {
        return this.CNR_MULTIPLE;
    }

    public void setCNR_MULTIPLE(BigDecimal CNR_MULTIPLE) {
        this.CNR_MULTIPLE = CNR_MULTIPLE;
    }

    public BigDecimal getCNR_FEE() {
        return this.CNR_FEE;
    }

    public void setCNR_FEE(BigDecimal CNR_FEE) {
        this.CNR_FEE = CNR_FEE;
    }

    public String getFUS40() {
        return this.FUS40;
    }

    public void setFUS40(String FUS40) {
        this.FUS40 = FUS40;
    }

    public String getNO_E_PURCHASE() {
        return this.NO_E_PURCHASE;
    }

    public void setNO_E_PURCHASE(String NO_E_PURCHASE) {
        this.NO_E_PURCHASE = NO_E_PURCHASE;
    }

    public String getNO_E_OUT() {
        return this.NO_E_OUT;
    }

    public void setNO_E_OUT(String NO_E_OUT) {
        this.NO_E_OUT = NO_E_OUT;
    }

    public String getNO_E_IN() {
        return this.NO_E_IN;
    }

    public void setNO_E_IN(String NO_E_IN) {
        this.NO_E_IN = NO_E_IN;
    }

    public String getNO_E_BUYBACK() {
        return this.NO_E_BUYBACK;
    }

    public void setNO_E_BUYBACK(String NO_E_BUYBACK) {
        this.NO_E_BUYBACK = NO_E_BUYBACK;
    }

    public String getQUOTA_CONTROL() {
        return this.QUOTA_CONTROL;
    }

    public void setQUOTA_CONTROL(String QUOTA_CONTROL) {
        this.QUOTA_CONTROL = QUOTA_CONTROL;
    }

    public BigDecimal getY_RETURN() {
        return this.Y_RETURN;
    }

    public void setY_RETURN(BigDecimal Y_RETURN) {
        this.Y_RETURN = Y_RETURN;
    }

    public BigDecimal getY_STD() {
        return this.Y_STD;
    }

    public void setY_STD(BigDecimal Y_STD) {
        this.Y_STD = Y_STD;
    }

    public Timestamp getSDATE() {
        return this.SDATE;
    }

    public void setSDATE(Timestamp SDATE) {
        this.SDATE = SDATE;
    }

    public Timestamp getEDATE() {
        return this.EDATE;
    }

    public void setEDATE(Timestamp EDATE) {
        this.EDATE = EDATE;
    }

    public BigDecimal getLIPPER_RANK() {
        return this.LIPPER_RANK;
    }

    public void setLIPPER_RANK(BigDecimal LIPPER_RANK) {
        this.LIPPER_RANK = LIPPER_RANK;
    }

    public BigDecimal getLIPPER_BENCHMARK_ID() {
        return this.LIPPER_BENCHMARK_ID;
    }

    public void setLIPPER_BENCHMARK_ID(BigDecimal LIPPER_BENCHMARK_ID) {
        this.LIPPER_BENCHMARK_ID = LIPPER_BENCHMARK_ID;
    }

    public String getFUS20() {
        return this.FUS20;
    }

    public void setFUS20(String FUS20) {
        this.FUS20 = FUS20;
    }

    public Timestamp getMULTIPLE_SDATE() {
        return this.MULTIPLE_SDATE;
    }

    public void setMULTIPLE_SDATE(Timestamp MULTIPLE_SDATE) {
        this.MULTIPLE_SDATE = MULTIPLE_SDATE;
    }

    public Timestamp getMULTIPLE_EDATE() {
        return this.MULTIPLE_EDATE;
    }

    public void setMULTIPLE_EDATE(Timestamp MULTIPLE_EDATE) {
        this.MULTIPLE_EDATE = MULTIPLE_EDATE;
    }

    public BigDecimal getCNR_DISCOUNT() {
        return this.CNR_DISCOUNT;
    }

    public void setCNR_DISCOUNT(BigDecimal CNR_DISCOUNT) {
        this.CNR_DISCOUNT = CNR_DISCOUNT;
    }

    public BigDecimal getRATE_DISCOUNT() {
        return this.RATE_DISCOUNT;
    }

    public void setRATE_DISCOUNT(BigDecimal RATE_DISCOUNT) {
        this.RATE_DISCOUNT = RATE_DISCOUNT;
    }

    public String getIS_CNR_TARGET() {
        return this.IS_CNR_TARGET;
    }

    public void setIS_CNR_TARGET(String IS_CNR_TARGET) {
        this.IS_CNR_TARGET = IS_CNR_TARGET;
    }

    public Timestamp getCNR_TARGET_SDATE() {
        return this.CNR_TARGET_SDATE;
    }

    public void setCNR_TARGET_SDATE(Timestamp CNR_TARGET_SDATE) {
        this.CNR_TARGET_SDATE = CNR_TARGET_SDATE;
    }

    public Timestamp getCNR_TARGET_EDATE() {
        return this.CNR_TARGET_EDATE;
    }

    public void setCNR_TARGET_EDATE(Timestamp CNR_TARGET_EDATE) {
        this.CNR_TARGET_EDATE = CNR_TARGET_EDATE;
    }

    public String getACT_TYPE() {
        return this.ACT_TYPE;
    }

    public void setACT_TYPE(String ACT_TYPE) {
        this.ACT_TYPE = ACT_TYPE;
    }

    public String getREVIEW_STATUS() {
        return this.REVIEW_STATUS;
    }

    public void setREVIEW_STATUS(String REVIEW_STATUS) {
        this.REVIEW_STATUS = REVIEW_STATUS;
    }

    public String getSELLING() {
        return this.SELLING;
    }

    public void setSELLING(String SELLING) {
        this.SELLING = SELLING;
    }

    public void checkDefaultValue() {
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("SEQ", getSEQ())
            .toString();
    }
    
    public String getVIGILANT() {
		return this.VIGILANT;
	}

	public void setVIGILANT(String VIGILANT) {
		this.VIGILANT = VIGILANT;
	}

	public String getPROJECT1() {
		return PROJECT1;
	}

	public void setPROJECT1(String pROJECT1) {
		PROJECT1 = pROJECT1;
	}

	public String getPROJECT2() {
		return PROJECT2;
	}

	public void setPROJECT2(String pROJECT2) {
		PROJECT2 = pROJECT2;
	}

	public String getCUSTOMER_LEVEL() {
		return CUSTOMER_LEVEL;
	}

	public void setCUSTOMER_LEVEL(String cUSTOMER_LEVEL) {
		CUSTOMER_LEVEL = cUSTOMER_LEVEL;
	}

	
}
