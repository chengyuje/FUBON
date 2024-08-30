package com.systex.jbranch.app.server.fps.sot701;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by SebastianWu on 2016/9/9.
 */
public class AcctVO { 
    String      acctNo;         //帳號
    Date        openDate;       //開戶日期 (西元年月日)
    String      currency;       //幣別
    BigDecimal  avbBalance;     //可用餘額 (帳戶餘額 - 圈存金額)
    BigDecimal  acctBalance;    //帳戶餘額
    BigDecimal  bckBalance;     //圈存金額
    String		acctCatagory;	//帳號類別
    String      branch;         //分行
    String      digitType;      //實體戶數位戶  0,1,2,3,4,5是數位戶

    public String getAcctNo() {
        return acctNo;
    }

    public void setAcctNo(String acctNo) {
        this.acctNo = acctNo;
    }

    public Date getOpenDate() {
        return openDate;
    }

    public void setOpenDate(Date openDate) {
        this.openDate = openDate;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public BigDecimal getAvbBalance() {
        return avbBalance;
    }

    public void setAvbBalance(BigDecimal avbBalance) {
        this.avbBalance = avbBalance;
    }

    public BigDecimal getAcctBalance() {
        return acctBalance;
    }

    public void setAcctBalance(BigDecimal acctBalance) {
        this.acctBalance = acctBalance;
    }

    public BigDecimal getBckBalance() {
        return bckBalance;
    }

    public void setBckBalance(BigDecimal bckBalance) {
        this.bckBalance = bckBalance;
    }

	public String getAcctCatagory() {
		return acctCatagory;
	}

	public void setAcctCatagory(String acctCatagory) {
		this.acctCatagory = acctCatagory;
	}

	public String getBranch() {
		return branch;
	}

	public void setBranch(String branch) {
		this.branch = branch;
	}

	public String getDigitType() {
		return digitType;
	}

	public void setDigitType(String digitType) {
		this.digitType = digitType;
	}
	

	
	
    
}
