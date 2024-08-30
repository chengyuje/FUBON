package com.systex.jbranch.fubon.commons.cbs.vo._000454_032081;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class CBS000454InputVO {
    private String SavingsAcct; // 存款帳號
    private String FromDate;    // 起日
    private String EndDate;     // 迄日
    private String EndTime = "235959"; // 當天最後一刻
    private String DateFunc = "2"; //查詢日期功能 1.計息日 2.交易日 3.帳務日
    private String SortMethd = "A"; //A: Ascending D: Descending
    private String TXType = "01"; // 01:帳務交易 30:凍結查詢 31:圈存/設質查詢  39:止付查詢 40:定存明細查詢
    private String LayoutNo = "2";
    private String NextKey; //Last Index Key

    public String getSavingsAcct() {
        return SavingsAcct;
    }

    public void setSavingsAcct(String savingsAcct) {
        SavingsAcct = savingsAcct;
    }

    public String getFromDate() {
        return FromDate;
    }

    public void setFromDate(String fromDate) {
        FromDate = fromDate;
    }

    public String getEndDate() {
        return EndDate;
    }

    public void setEndDate(String endDate) {
        EndDate = endDate;
    }

    public String getDateFunc() {
        return DateFunc;
    }

    public void setDateFunc(String dateFunc) {
        DateFunc = dateFunc;
    }

    public String getSortMethd() {
        return SortMethd;
    }

    public void setSortMethd(String sortMethd) {
        SortMethd = sortMethd;
    }

    public String getTXType() {
        return TXType;
    }

    public void setTXType(String TXType) {
        this.TXType = TXType;
    }

    public String getLayoutNo() {
        return LayoutNo;
    }

    public void setLayoutNo(String layoutNo) {
        LayoutNo = layoutNo;
    }

    public String getEndTime() {
        return EndTime;
    }

    public void setEndTime(String endTime) {
        EndTime = endTime;
    }

	public String getNextKey() {
		return NextKey;
	}

	public void setNextKey(String nextKey) {
		NextKey = nextKey;
	}
    
    
}
