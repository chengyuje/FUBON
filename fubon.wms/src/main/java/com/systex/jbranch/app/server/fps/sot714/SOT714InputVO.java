package com.systex.jbranch.app.server.fps.sot714;

import java.math.BigDecimal;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class SOT714InputVO extends PagingInputVO{
    private String custID;
    private String prodType;
    private BigDecimal buyAmt;
    //高資產客戶投組適配電文
    private String CUST_KYC;		//客戶當下風險屬性
    private String SP_YN;           //是否為特定客戶
    private String PROD_RISK;       //本次申購商品風險屬性
    private BigDecimal AMT_BUY_1;   //P1申購金額(折台)
    private BigDecimal AMT_SELL_1;  //P1贖回金額(折台)
    private BigDecimal AMT_BUY_2;   //P2申購金額(折台)
    private BigDecimal AMT_SELL_2;  //P2贖回金額(折台)
    private BigDecimal AMT_BUY_3;   //P3申購金額(折台)
    private BigDecimal AMT_SELL_3;  //P3贖回金額(折台)
    private BigDecimal AMT_BUY_4;   //P4申購金額(折台)
    private BigDecimal AMT_SELL_4;  //P4贖回金額(折台)

	public String getCustID() {
        return custID;
    }

    public void setCustID(String custID) {
        this.custID = custID;
    }

	public String getProdType() {
		return prodType;
	}

	public void setProdType(String prodType) {
		this.prodType = prodType;
	}

	public BigDecimal getBuyAmt() {
		return buyAmt;
	}

	public void setBuyAmt(BigDecimal buyAmt) {
		this.buyAmt = buyAmt;
	}

	public String getCUST_KYC() {
		return CUST_KYC;
	}

	public void setCUST_KYC(String cUST_KYC) {
		CUST_KYC = cUST_KYC;
	}

	public String getSP_YN() {
		return SP_YN;
	}

	public void setSP_YN(String sP_YN) {
		SP_YN = sP_YN;
	}

	public String getPROD_RISK() {
		return PROD_RISK;
	}

	public void setPROD_RISK(String pROD_RISK) {
		PROD_RISK = pROD_RISK;
	}

	public BigDecimal getAMT_BUY_1() {
		return AMT_BUY_1;
	}

	public void setAMT_BUY_1(BigDecimal aMT_BUY_1) {
		AMT_BUY_1 = aMT_BUY_1;
	}

	public BigDecimal getAMT_SELL_1() {
		return AMT_SELL_1;
	}

	public void setAMT_SELL_1(BigDecimal aMT_SELL_1) {
		AMT_SELL_1 = aMT_SELL_1;
	}

	public BigDecimal getAMT_BUY_2() {
		return AMT_BUY_2;
	}

	public void setAMT_BUY_2(BigDecimal aMT_BUY_2) {
		AMT_BUY_2 = aMT_BUY_2;
	}

	public BigDecimal getAMT_SELL_2() {
		return AMT_SELL_2;
	}

	public void setAMT_SELL_2(BigDecimal aMT_SELL_2) {
		AMT_SELL_2 = aMT_SELL_2;
	}

	public BigDecimal getAMT_BUY_3() {
		return AMT_BUY_3;
	}

	public void setAMT_BUY_3(BigDecimal aMT_BUY_3) {
		AMT_BUY_3 = aMT_BUY_3;
	}

	public BigDecimal getAMT_SELL_3() {
		return AMT_SELL_3;
	}

	public void setAMT_SELL_3(BigDecimal aMT_SELL_3) {
		AMT_SELL_3 = aMT_SELL_3;
	}

	public BigDecimal getAMT_BUY_4() {
		return AMT_BUY_4;
	}

	public void setAMT_BUY_4(BigDecimal aMT_BUY_4) {
		AMT_BUY_4 = aMT_BUY_4;
	}

	public BigDecimal getAMT_SELL_4() {
		return AMT_SELL_4;
	}

	public void setAMT_SELL_4(BigDecimal aMT_SELL_4) {
		AMT_SELL_4 = aMT_SELL_4;
	}
	
}
