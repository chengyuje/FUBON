package com.systex.jbranch.app.server.fps.sot701;

import java.util.List;

/**
 * Created by SebastianWu on 2016/9/9.
 */
public class CustAcctDataVO {
    List<AcctVO> debitAcctList;     //扣款帳號
    List<AcctVO> trustAcctList;     //信託帳號
    List<AcctVO> creditAcctList;    //收益入帳帳號 贖回入帳帳號(基金贖回/贖回再申購); 新收益入帳帳號(基金約定條件變更)
    List<AcctVO> prodAcctList;      //組合式商品帳號
    List<AcctVO> includingDigitDebitAcctList;  //因應#0695數存戶改動 海外ETF/股票需用到包含數存戶的扣款帳號

    public List<AcctVO> getDebitAcctList() {
        return debitAcctList;
    }

    public void setDebitAcctList(List<AcctVO> debitAcctList) {
        this.debitAcctList = debitAcctList;
    }

    public List<AcctVO> getTrustAcctList() {
        return trustAcctList;
    }

    public void setTrustAcctList(List<AcctVO> trustAcctList) {
        this.trustAcctList = trustAcctList;
    }

    public List<AcctVO> getCreditAcctList() {
        return creditAcctList;
    }

    public void setCreditAcctList(List<AcctVO> creditAcctList) {
        this.creditAcctList = creditAcctList;
    }

    public List<AcctVO> getProdAcctList() {
        return prodAcctList;
    }

    public void setProdAcctList(List<AcctVO> prodAcctList) {
        this.prodAcctList = prodAcctList;
    }

	public List<AcctVO> getIncludingDigitDebitAcctList() {
		return includingDigitDebitAcctList;
	}

	public void setIncludingDigitDebitAcctList(List<AcctVO> includingDigitDebitAcctList) {
		this.includingDigitDebitAcctList = includingDigitDebitAcctList;
	}
    
	
    


    
}
