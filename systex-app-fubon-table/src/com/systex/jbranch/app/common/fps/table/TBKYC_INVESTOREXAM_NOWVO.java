package com.systex.jbranch.app.common.fps.table;

import java.math.BigDecimal;
import java.sql.Timestamp;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;

public class TBKYC_INVESTOREXAM_NOWVO extends VOBase {

    private String CUST_ID;
    private String CUST_RISK_ATR;
    private Timestamp KYC_DUE_DATE;
    private String ANNUAL_INCOME_AMT;
    private BigDecimal STOP_LOSS_PT;
    private BigDecimal TAKE_PRFT_PT;

    public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBKYC_INVESTOREXAM_NOW";

	
	public String getTableuid () {
	    return TABLE_UID;
	}

    public TBKYC_INVESTOREXAM_NOWVO(String cUST_ID, String cUST_RISK_ATR,
		Timestamp kYC_DUE_DATE, String aNNUAL_INCOME_AMT,
		BigDecimal sTOP_LOSS_PT, BigDecimal tAKE_PRFT_PT) {
		super();
		CUST_ID = cUST_ID;
		CUST_RISK_ATR = cUST_RISK_ATR;
		KYC_DUE_DATE = kYC_DUE_DATE;
		ANNUAL_INCOME_AMT = aNNUAL_INCOME_AMT;
		STOP_LOSS_PT = sTOP_LOSS_PT;
		TAKE_PRFT_PT = tAKE_PRFT_PT;
	}

    public TBKYC_INVESTOREXAM_NOWVO(){}

    public TBKYC_INVESTOREXAM_NOWVO(String CUST_ID) {
        this.CUST_ID = CUST_ID;
    }

    public String getCUST_ID() {
        return this.CUST_ID;
    }

    public void setCUST_ID(String CUST_ID) {
        this.CUST_ID = CUST_ID;
    }

    public String getANNUAL_INCOME_AMT() {
        return this.ANNUAL_INCOME_AMT;
    }

    public void setANNUAL_INCOME_AMT(String ANNUAL_INCOME_AMT) {
        this.ANNUAL_INCOME_AMT = ANNUAL_INCOME_AMT;
    }

    public String getCUST_RISK_ATR() {
        return this.CUST_RISK_ATR;
    }

    public void setCUST_RISK_ATR(String CUST_RISK_ATR) {
        this.CUST_RISK_ATR = CUST_RISK_ATR;
    }

    public BigDecimal getSTOP_LOSS_PT() {
        return this.STOP_LOSS_PT;
    }

    public void setSTOP_LOSS_PT(BigDecimal STOP_LOSS_PT) {
        this.STOP_LOSS_PT = STOP_LOSS_PT;
    }

    public BigDecimal getTAKE_PRFT_PT() {
        return this.TAKE_PRFT_PT;
    }

    public void setTAKE_PRFT_PT(BigDecimal TAKE_PRFT_PT) {
        this.TAKE_PRFT_PT = TAKE_PRFT_PT;
    }

    public Timestamp getKYC_DUE_DATE() {
        return this.KYC_DUE_DATE;
    }

    public void setKYC_DUE_DATE(Timestamp KYC_DUE_DATE) {
        this.KYC_DUE_DATE = KYC_DUE_DATE;
    }

    public void checkDefaultValue() {
    }

    public String toString() {
        return new ToStringBuilder(this)
            .append("CUST_ID", getCUST_ID())
            .toString();
    }

}
