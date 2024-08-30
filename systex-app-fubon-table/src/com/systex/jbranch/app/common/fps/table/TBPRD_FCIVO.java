package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Timestamp;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;;




/** @Author : SystexDBTool CodeGenerator By LeoLin*/
public class TBPRD_FCIVO extends VOBase {

	/** identifier field */
    private BigDecimal SEQ_NO;
    
    /** persistent field */
    private String CURR_ID;

    /** nullable persistent field */
    private String RISKCATE_ID;

    /** nullable persistent field */
    private BigDecimal REF_PRICE_Y;
    
    /** nullable persistent field */
    private BigDecimal MIN_UF;
    
    /** nullable persistent field */
    private BigDecimal BASE_AMT;
    
    /** nullable persistent field */
    private BigDecimal UNIT_AMT;
    
    /** nullable persistent field */
    private BigDecimal TRADER_CHARGE;
    
    /** nullable persistent field */
    private BigDecimal STRIKE_PRICE;
    
    /** nullable persistent field */
    private String TARGET_CURR_ID;
    
    /** nullable persistent field */
    private Timestamp EFFECTIVE_DATE;
    
    /** nullable persistent field */
    private String EFFECTIVE_YN;
    
public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBPRD_FCI";


public String getTableuid () {
    return TABLE_UID;
}

    /** default constructor */
    public TBPRD_FCIVO() {
    }

    /** minimal constructor */
    public TBPRD_FCIVO(BigDecimal SEQ_NO) {
        this.SEQ_NO = SEQ_NO;
    }

	public BigDecimal getSEQ_NO() {
		return SEQ_NO;
	}

	public void setSEQ_NO(BigDecimal sEQ_NO) {
		SEQ_NO = sEQ_NO;
	}

	public String getCURR_ID() {
		return CURR_ID;
	}

	public void setCURR_ID(String cURR_ID) {
		CURR_ID = cURR_ID;
	}

	public String getRISKCATE_ID() {
		return RISKCATE_ID;
	}

	public void setRISKCATE_ID(String rISKCATE_ID) {
		RISKCATE_ID = rISKCATE_ID;
	}

	public BigDecimal getREF_PRICE_Y() {
		return REF_PRICE_Y;
	}

	public void setREF_PRICE_Y(BigDecimal rEF_PRICE_Y) {
		REF_PRICE_Y = rEF_PRICE_Y;
	}

	public BigDecimal getMIN_UF() {
		return MIN_UF;
	}

	public void setMIN_UF(BigDecimal mIN_UF) {
		MIN_UF = mIN_UF;
	}

	public BigDecimal getBASE_AMT() {
		return BASE_AMT;
	}

	public void setBASE_AMT(BigDecimal bASE_AMT) {
		BASE_AMT = bASE_AMT;
	}

	public BigDecimal getUNIT_AMT() {
		return UNIT_AMT;
	}

	public void setUNIT_AMT(BigDecimal uNIT_AMT) {
		UNIT_AMT = uNIT_AMT;
	}

	public BigDecimal getTRADER_CHARGE() {
		return TRADER_CHARGE;
	}

	public void setTRADER_CHARGE(BigDecimal tRADER_CHARGE) {
		TRADER_CHARGE = tRADER_CHARGE;
	}

	public BigDecimal getSTRIKE_PRICE() {
		return STRIKE_PRICE;
	}

	public void setSTRIKE_PRICE(BigDecimal sTRIKE_PRICE) {
		STRIKE_PRICE = sTRIKE_PRICE;
	}

	public String getTARGET_CURR_ID() {
		return TARGET_CURR_ID;
	}

	public void setTARGET_CURR_ID(String tARGET_CURR_ID) {
		TARGET_CURR_ID = tARGET_CURR_ID;
	}

	public Timestamp getEFFECTIVE_DATE() {
		return EFFECTIVE_DATE;
	}

	public void setEFFECTIVE_DATE(Timestamp eFFECTIVE_DATE) {
		EFFECTIVE_DATE = eFFECTIVE_DATE;
	}

	public String getEFFECTIVE_YN() {
		return EFFECTIVE_YN;
	}

	public void setEFFECTIVE_YN(String eFFECTIVE_YN) {
		EFFECTIVE_YN = eFFECTIVE_YN;
	}

	public void checkDefaultValue() {
    }


}
