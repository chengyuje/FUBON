package com.systex.jbranch.app.server.fps.sot707;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by SebastianWu on 2016/9/26.
 * for sql query columns
 */
public class MainInfoBean {
    private String CUST_ID;
    private String CUST_NAME;
    private BigDecimal SEQ_NO;
    private Date TRADE_DATE;
    private String BATCH_SEQ;
    private String MARKET_TYPE;
    private String TRUST_CURR_TYPE;
    private String TRUST_ACCT;
    private BigDecimal TRUST_AMT;
    private String PROD_ID;
    private String DEBIT_ACCT;
    private String CREDIT_ACCT;
//  private String DEPT_ID;
    private String MODIFIER;
    private String PROD_CURR;
    private String CERTIFICATE_ID;
    private BigDecimal PURCHASE_AMT;
    private BigDecimal REDEEM_AMT;
    private BigDecimal TRUST_UNIT;
    private String REC_SEQ;
    private String NARRATOR_ID;
    private BigDecimal ENTRUST_AMT;
    private String ENTRUST_TYPE;
    private BigDecimal REF_VAL;
    private Date REF_VAL_DATE;
    private String FEE_TYPE;
    private BigDecimal FEE_RATE;
    private BigDecimal DEFAULT_FEE_RATE;
    private BigDecimal MGM_FEE;
    private String BRANCH_NBR;
    private String BOSS_ID;
    private String AUTH_ID;
    private BigDecimal BOND_VALUE;
    private BigDecimal GTC_FEE_RATE;
    private String GTC_YN;
    private Date GTC_START_DATE;
    private Date GTC_END_DATE;
    private String TRUST_TRADE_TYPE;
    private String IS_WEB;
    private String HNWC_BUY;
    private String FLAG_NUMBER;
    
    
    public String getIS_WEB() {
		return IS_WEB;
	}

	public void setIS_WEB(String iS_WEB) {
		IS_WEB = iS_WEB;
	}

	public String getTRUST_TRADE_TYPE() {
		return TRUST_TRADE_TYPE;
	}

	public void setTRUST_TRADE_TYPE(String tRUST_TRADE_TYPE) {
		TRUST_TRADE_TYPE = tRUST_TRADE_TYPE;
	}

	public BigDecimal getBOND_VALUE() {
		return BOND_VALUE;
	}

	public void setBOND_VALUE(BigDecimal bOND_VALUE) {
		BOND_VALUE = bOND_VALUE;
	}

	public String getCUST_ID() {
        return CUST_ID;
    }

    public void setCUST_ID(String CUST_ID) {
        this.CUST_ID = CUST_ID;
    }

    public String getCUST_NAME() {
        return CUST_NAME;
    }

    public void setCUST_NAME(String CUST_NAME) {
        this.CUST_NAME = CUST_NAME;
    }

    public BigDecimal getSEQ_NO() {
		return SEQ_NO;
	}

	public void setSEQ_NO(BigDecimal sEQ_NO) {
		SEQ_NO = sEQ_NO;
	}

	public Date getTRADE_DATE() {
        return TRADE_DATE;
    }

    public void setTRADE_DATE(Date TRADE_DATE) {
        this.TRADE_DATE = TRADE_DATE;
    }

    public String getBATCH_SEQ() {
        return BATCH_SEQ;
    }

    public void setBATCH_SEQ(String BATCH_SEQ) {
        this.BATCH_SEQ = BATCH_SEQ;
    }

    public String getMARKET_TYPE() {
        return MARKET_TYPE;
    }

    public void setMARKET_TYPE(String MARKET_TYPE) {
        this.MARKET_TYPE = MARKET_TYPE;
    }

    public String getTRUST_CURR_TYPE() {
        return TRUST_CURR_TYPE;
    }

    public void setTRUST_CURR_TYPE(String TRUST_CURR_TYPE) {
        this.TRUST_CURR_TYPE = TRUST_CURR_TYPE;
    }

    public String getTRUST_ACCT() {
        return TRUST_ACCT;
    }

    public void setTRUST_ACCT(String TRUST_ACCT) {
        this.TRUST_ACCT = TRUST_ACCT;
    }

    public BigDecimal getTRUST_AMT() {
        return TRUST_AMT;
    }

    public void setTRUST_AMT(BigDecimal TRUST_AMT) {
        this.TRUST_AMT = TRUST_AMT;
    }

    public String getPROD_ID() {
        return PROD_ID;
    }

    public void setPROD_ID(String PROD_ID) {
        this.PROD_ID = PROD_ID;
    }

    public String getDEBIT_ACCT() {
        return DEBIT_ACCT;
    }

    public void setDEBIT_ACCT(String DEBIT_ACCT) {
        this.DEBIT_ACCT = DEBIT_ACCT;
    }

    public String getCREDIT_ACCT() {
        return CREDIT_ACCT;
    }

    public void setCREDIT_ACCT(String CREDIT_ACCT) {
        this.CREDIT_ACCT = CREDIT_ACCT;
    }

    public String getMODIFIER() {
        return MODIFIER;
    }

    public void setMODIFIER(String MODIFIER) {
        this.MODIFIER = MODIFIER;
    }

    public String getPROD_CURR() {
        return PROD_CURR;
    }

    public void setPROD_CURR(String PROD_CURR) {
        this.PROD_CURR = PROD_CURR;
    }

    public String getCERTIFICATE_ID() {
        return CERTIFICATE_ID;
    }

    public void setCERTIFICATE_ID(String CERTIFICATE_ID) {
        this.CERTIFICATE_ID = CERTIFICATE_ID;
    }

    public BigDecimal getPURCHASE_AMT() {
        return PURCHASE_AMT;
    }

    public void setPURCHASE_AMT(BigDecimal PURCHASE_AMT) {
        this.PURCHASE_AMT = PURCHASE_AMT;
    }

    public BigDecimal getREDEEM_AMT() {
		return REDEEM_AMT;
	}

	public void setREDEEM_AMT(BigDecimal rEDEEM_AMT) {
		REDEEM_AMT = rEDEEM_AMT;
	}

	public BigDecimal getTRUST_UNIT() {
        return TRUST_UNIT;
    }

    public void setTRUST_UNIT(BigDecimal TRUST_UNIT) {
        this.TRUST_UNIT = TRUST_UNIT;
    }

    public String getREC_SEQ() {
        return REC_SEQ;
    }

    public void setREC_SEQ(String REC_SEQ) {
        this.REC_SEQ = REC_SEQ;
    }

    public String getNARRATOR_ID() {
        return NARRATOR_ID;
    }

    public void setNARRATOR_ID(String NARRATOR_ID) {
        this.NARRATOR_ID = NARRATOR_ID;
    }

    public BigDecimal getENTRUST_AMT() {
        return ENTRUST_AMT;
    }

    public void setENTRUST_AMT(BigDecimal ENTRUST_AMT) {
        this.ENTRUST_AMT = ENTRUST_AMT;
    }

    public String getENTRUST_TYPE() {
        return ENTRUST_TYPE;
    }

    public void setENTRUST_TYPE(String ENTRUST_TYPE) {
        this.ENTRUST_TYPE = ENTRUST_TYPE;
    }

    public BigDecimal getREF_VAL() {
        return REF_VAL;
    }

    public void setREF_VAL(BigDecimal REF_VAL) {
        this.REF_VAL = REF_VAL;
    }

    public Date getREF_VAL_DATE() {
		return REF_VAL_DATE;
	}

	public void setREF_VAL_DATE(Date rEF_VAL_DATE) {
		REF_VAL_DATE = rEF_VAL_DATE;
	}

	public String getFEE_TYPE() {
        return FEE_TYPE;
    }

    public void setFEE_TYPE(String FEE_TYPE) {
        this.FEE_TYPE = FEE_TYPE;
    }

    public BigDecimal getFEE_RATE() {
        return FEE_RATE;
    }

    public void setFEE_RATE(BigDecimal FEE_RATE) {
        this.FEE_RATE = FEE_RATE;
    }

    public BigDecimal getDEFAULT_FEE_RATE() {
        return DEFAULT_FEE_RATE;
    }

    public void setDEFAULT_FEE_RATE(BigDecimal DEFAULT_FEE_RATE) {
        this.DEFAULT_FEE_RATE = DEFAULT_FEE_RATE;
    }

    public BigDecimal getMGM_FEE() {
        return MGM_FEE;
    }

    public void setMGM_FEE(BigDecimal MGM_FEE) {
        this.MGM_FEE = MGM_FEE;
    }

    public String getBRANCH_NBR() {
		return BRANCH_NBR;
	}

	public void setBRANCH_NBR(String bRANCH_NBR) {
		BRANCH_NBR = bRANCH_NBR;
	}

    public String getBOSS_ID() {
		return BOSS_ID;
	}

	public void setBOSS_ID(String bOSS_ID) {
		BOSS_ID = bOSS_ID;
	}
	
	public String getAUTH_ID() {
		return AUTH_ID;
	}

	public void setAUTH_ID(String aUTH_ID) {
		AUTH_ID = aUTH_ID;
	}

	public BigDecimal getGTC_FEE_RATE() {
		return GTC_FEE_RATE;
	}

	public void setGTC_FEE_RATE(BigDecimal gTC_FEE_RATE) {
		GTC_FEE_RATE = gTC_FEE_RATE;
	}

	public String getGTC_YN() {
		return GTC_YN;
	}

	public void setGTC_YN(String gTC_YN) {
		GTC_YN = gTC_YN;
	}
	
	public Date getGTC_START_DATE() {
		return GTC_START_DATE;
	}

	public void setGTC_START_DATE(Date gTC_START_DATE) {
		GTC_START_DATE = gTC_START_DATE;
	}

	public Date getGTC_END_DATE() {
		return GTC_END_DATE;
	}

	public void setGTC_END_DATE(Date gTC_END_DATE) {
		GTC_END_DATE = gTC_END_DATE;
	}

	public String getHNWC_BUY() {
		return HNWC_BUY;
	}

	public void setHNWC_BUY(String hNWC_BUY) {
		HNWC_BUY = hNWC_BUY;
	}
	

	public String getFLAG_NUMBER() {
		return FLAG_NUMBER;
	}

	public void setFLAG_NUMBER(String fLAG_NUMBER) {
		FLAG_NUMBER = fLAG_NUMBER;
	}

	@Override
    public String toString() {
        return "MainInfoBean{" +
                "CUST_ID='" + CUST_ID + '\'' +
                ", CUST_NAME='" + CUST_NAME + '\'' +
                ", TRADE_DATE=" + TRADE_DATE +
                ", BATCH_SEQ='" + BATCH_SEQ + '\'' +
                ", MARKET_TYPE=" + MARKET_TYPE +
                ", TRUST_CURR_TYPE=" + TRUST_CURR_TYPE +
                ", TRUST_ACCT='" + TRUST_ACCT + '\'' +
                ", TRUST_AMT=" + TRUST_AMT +
                ", PROD_ID='" + PROD_ID + '\'' +
                ", DEBIT_ACCT='" + DEBIT_ACCT + '\'' +
                ", CREDIT_ACCT='" + CREDIT_ACCT + '\'' +
                ", MODIFIER='" + MODIFIER + '\'' +
                ", PROD_CURR=" + PROD_CURR +
                ", CERTIFICATE_ID='" + CERTIFICATE_ID + '\'' +
                ", PURCHASE_AMT=" + PURCHASE_AMT +
                ", TRUST_UNIT=" + TRUST_UNIT +
                ", REC_SEQ='" + REC_SEQ + '\'' +
                ", NARRATOR_ID='" + NARRATOR_ID + '\'' +
                ", ENTRUST_AMT=" + ENTRUST_AMT +
                ", ENTRUST_TYPE=" + ENTRUST_TYPE +
                ", REF_VAL=" + REF_VAL +
                ", REF_VAL_DATE=" + REF_VAL_DATE +
                ", FEE_TYPE='" + FEE_TYPE + '\'' +
                ", FEE_RATE=" + FEE_RATE +
                ", DEFAULT_FEE_RATE=" + DEFAULT_FEE_RATE +
                ", MGM_FEE=" + MGM_FEE +
                ", BRANCH_NBR=" + BRANCH_NBR +
                ", BOSS_ID=" + BOSS_ID + 
                ", GTC_FEE_RATE=" + GTC_FEE_RATE + 
                ", GTC_YN=" + GTC_YN + 
                ", GTC_END_DATE=" + GTC_END_DATE + 
                '}';
    }
}
