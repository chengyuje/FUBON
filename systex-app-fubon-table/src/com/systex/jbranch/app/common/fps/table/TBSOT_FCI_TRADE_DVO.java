package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Timestamp;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBSOT_FCI_TRADE_DVO extends VOBase {

    /** identifier field */
    private com.systex.jbranch.app.common.fps.table.TBSOT_FCI_TRADE_DPK comp_id;

    /** nullable persistent field */
    private String BATCH_SEQ;

    /** nullable persistent field */
    private String TRADE_SUB_TYPE;

    /** nullable persistent field */
    private String PROD_ID;

    /** nullable persistent field */
    private String PROD_NAME;

    /** nullable persistent field */
    private String PROD_CURR;

    /** nullable persistent field */
    private String PROD_RISK_LV;

    /** nullable persistent field */
    private BigDecimal PROD_MIN_BUY_AMT;

    /** nullable persistent field */
    private BigDecimal PROD_MIN_GRD_AMT;

    /** nullable persistent field */
    private BigDecimal PURCHASE_AMT;
    
    /** nullable persistent field */
    private String DEBIT_ACCT;

    /** nullable persistent field */
    private String PROD_ACCT;
    
    /** nullable persistent field */
    private String MON_PERIOD;

    /** nullable persistent field */
    private BigDecimal RM_PROFEE;
    
    /** nullable persistent field */
    private String TARGET_CURR_ID;
    
    /** nullable persistent field */
    private BigDecimal STRIKE_PRICE;
    
    /** nullable persistent field */
    private BigDecimal FTP_RATE;
    
    /** nullable persistent field */
    private String TARGET_NAME;
    
    /** nullable persistent field */
    private Timestamp TRADE_DATE;
    
    /** nullable persistent field */
    private Timestamp VALUE_DATE;
    
    /** nullable persistent field */
    private Timestamp SPOT_DATE;
    
    /** nullable persistent field */
    private Timestamp EXPIRE_DATE;

    /** nullable persistent field */
    private BigDecimal INT_DATES;
    
    /** nullable persistent field */
    private BigDecimal TRADER_CHARGE;
    
    /** nullable persistent field */
    private BigDecimal PRD_PROFEE_RATE;
    
    /** nullable persistent field */
    private BigDecimal LESS_PROFEE_RATE;
    
    /** nullable persistent field */
    private BigDecimal PRD_PROFEE_AMT;
    
    /** nullable persistent field */
    private BigDecimal LESS_PROFEE_AMT;

    /** nullable persistent field */
    private String NARRATOR_ID;

    /** nullable persistent field */
    private String NARRATOR_NAME;

    /** nullable persistent field */
    private String BOSS_ID;
    
    /** nullable persistent field */
    private String AUTH_ID;
    
    /** nullable persistent field */
    private String REC_CODE;

    
public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBSOT_FCI_TRADE_D";


public String getTableuid () {
    return TABLE_UID;
}

    /** default constructor */
    public TBSOT_FCI_TRADE_DVO() {
    }

    /** minimal constructor */
    public TBSOT_FCI_TRADE_DVO(com.systex.jbranch.app.common.fps.table.TBSOT_FCI_TRADE_DPK comp_id) {
        this.comp_id = comp_id;
    }

    public com.systex.jbranch.app.common.fps.table.TBSOT_FCI_TRADE_DPK getcomp_id() {
        return this.comp_id;
    }

    public void setcomp_id(com.systex.jbranch.app.common.fps.table.TBSOT_FCI_TRADE_DPK comp_id) {
        this.comp_id = comp_id;
    }

    public String getBATCH_SEQ() {
        return this.BATCH_SEQ;
    }

    public void setBATCH_SEQ(String BATCH_SEQ) {
        this.BATCH_SEQ = BATCH_SEQ;
    }

    public String getTRADE_SUB_TYPE() {
        return this.TRADE_SUB_TYPE;
    }

    public void setTRADE_SUB_TYPE(String TRADE_SUB_TYPE) {
        this.TRADE_SUB_TYPE = TRADE_SUB_TYPE;
    }

    public String getPROD_ID() {
        return this.PROD_ID;
    }

    public void setPROD_ID(String PROD_ID) {
        this.PROD_ID = PROD_ID;
    }

    public String getPROD_NAME() {
        return this.PROD_NAME;
    }

    public void setPROD_NAME(String PROD_NAME) {
        this.PROD_NAME = PROD_NAME;
    }

    public String getPROD_CURR() {
        return this.PROD_CURR;
    }

    public void setPROD_CURR(String PROD_CURR) {
        this.PROD_CURR = PROD_CURR;
    }

    public String getPROD_RISK_LV() {
        return this.PROD_RISK_LV;
    }

    public void setPROD_RISK_LV(String PROD_RISK_LV) {
        this.PROD_RISK_LV = PROD_RISK_LV;
    }

    public BigDecimal getPROD_MIN_BUY_AMT() {
        return this.PROD_MIN_BUY_AMT;
    }

    public void setPROD_MIN_BUY_AMT(BigDecimal PROD_MIN_BUY_AMT) {
        this.PROD_MIN_BUY_AMT = PROD_MIN_BUY_AMT;
    }

    public BigDecimal getPROD_MIN_GRD_AMT() {
        return this.PROD_MIN_GRD_AMT;
    }

    public void setPROD_MIN_GRD_AMT(BigDecimal PROD_MIN_GRD_AMT) {
        this.PROD_MIN_GRD_AMT = PROD_MIN_GRD_AMT;
    }

    public BigDecimal getPURCHASE_AMT() {
        return this.PURCHASE_AMT;
    }

    public void setPURCHASE_AMT(BigDecimal PURCHASE_AMT) {
        this.PURCHASE_AMT = PURCHASE_AMT;
    }

    public String getDEBIT_ACCT() {
		return DEBIT_ACCT;
	}

	public void setDEBIT_ACCT(String dEBIT_ACCT) {
		DEBIT_ACCT = dEBIT_ACCT;
	}

	public String getPROD_ACCT() {
		return PROD_ACCT;
	}

	public void setPROD_ACCT(String pROD_ACCT) {
		PROD_ACCT = pROD_ACCT;
	}

	public String getMON_PERIOD() {
		return MON_PERIOD;
	}

	public void setMON_PERIOD(String mON_PERIOD) {
		MON_PERIOD = mON_PERIOD;
	}

	public BigDecimal getRM_PROFEE() {
		return RM_PROFEE;
	}

	public void setRM_PROFEE(BigDecimal rM_PROFEE) {
		RM_PROFEE = rM_PROFEE;
	}

	public String getTARGET_CURR_ID() {
		return TARGET_CURR_ID;
	}

	public void setTARGET_CURR_ID(String tARGET_CURR_ID) {
		TARGET_CURR_ID = tARGET_CURR_ID;
	}

	public BigDecimal getSTRIKE_PRICE() {
		return STRIKE_PRICE;
	}

	public void setSTRIKE_PRICE(BigDecimal sTRIKE_PRICE) {
		STRIKE_PRICE = sTRIKE_PRICE;
	}

	public BigDecimal getFTP_RATE() {
		return FTP_RATE;
	}

	public void setFTP_RATE(BigDecimal fTP_RATE) {
		FTP_RATE = fTP_RATE;
	}

	public String getTARGET_NAME() {
		return TARGET_NAME;
	}

	public void setTARGET_NAME(String tARGET_NAME) {
		TARGET_NAME = tARGET_NAME;
	}

	public Timestamp getTRADE_DATE() {
		return TRADE_DATE;
	}

	public void setTRADE_DATE(Timestamp tRADE_DATE) {
		TRADE_DATE = tRADE_DATE;
	}

	public Timestamp getVALUE_DATE() {
		return VALUE_DATE;
	}

	public void setVALUE_DATE(Timestamp vALUE_DATE) {
		VALUE_DATE = vALUE_DATE;
	}

	public Timestamp getSPOT_DATE() {
		return SPOT_DATE;
	}

	public void setSPOT_DATE(Timestamp sPOT_DATE) {
		SPOT_DATE = sPOT_DATE;
	}

	public Timestamp getEXPIRE_DATE() {
		return EXPIRE_DATE;
	}

	public void setEXPIRE_DATE(Timestamp eXPIRE_DATE) {
		EXPIRE_DATE = eXPIRE_DATE;
	}

	public BigDecimal getINT_DATES() {
		return INT_DATES;
	}

	public void setINT_DATES(BigDecimal iNT_DATES) {
		INT_DATES = iNT_DATES;
	}

	public BigDecimal getTRADER_CHARGE() {
		return TRADER_CHARGE;
	}

	public void setTRADER_CHARGE(BigDecimal tRADER_CHARGE) {
		TRADER_CHARGE = tRADER_CHARGE;
	}

	public BigDecimal getPRD_PROFEE_RATE() {
		return PRD_PROFEE_RATE;
	}

	public void setPRD_PROFEE_RATE(BigDecimal pRD_PROFEE_RATE) {
		PRD_PROFEE_RATE = pRD_PROFEE_RATE;
	}

	public BigDecimal getLESS_PROFEE_RATE() {
		return LESS_PROFEE_RATE;
	}

	public void setLESS_PROFEE_RATE(BigDecimal lESS_PROFEE_RATE) {
		LESS_PROFEE_RATE = lESS_PROFEE_RATE;
	}

	public BigDecimal getPRD_PROFEE_AMT() {
		return PRD_PROFEE_AMT;
	}

	public void setPRD_PROFEE_AMT(BigDecimal pRD_PROFEE_AMT) {
		PRD_PROFEE_AMT = pRD_PROFEE_AMT;
	}

	public BigDecimal getLESS_PROFEE_AMT() {
		return LESS_PROFEE_AMT;
	}

	public void setLESS_PROFEE_AMT(BigDecimal lESS_PROFEE_AMT) {
		LESS_PROFEE_AMT = lESS_PROFEE_AMT;
	}

	public String getNARRATOR_ID() {
        return this.NARRATOR_ID;
    }

    public void setNARRATOR_ID(String NARRATOR_ID) {
        this.NARRATOR_ID = NARRATOR_ID;
    }

    public String getNARRATOR_NAME() {
        return this.NARRATOR_NAME;
    }

    public void setNARRATOR_NAME(String NARRATOR_NAME) {
        this.NARRATOR_NAME = NARRATOR_NAME;
    }

    public String getBOSS_ID() {
        return this.BOSS_ID;
    }

    public void setBOSS_ID(String BOSS_ID) {
        this.BOSS_ID = BOSS_ID;
    }

    public String getAUTH_ID() {
		return AUTH_ID;
	}

	public void setAUTH_ID(String aUTH_ID) {
		AUTH_ID = aUTH_ID;
	}

	public String getREC_CODE() {
		return REC_CODE;
	}

	public void setREC_CODE(String rEC_CODE) {
		REC_CODE = rEC_CODE;
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
        if ( !(other instanceof TBSOT_FCI_TRADE_DVO) ) return false;
        TBSOT_FCI_TRADE_DVO castOther = (TBSOT_FCI_TRADE_DVO) other;
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
