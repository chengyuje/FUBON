package com.systex.jbranch.app.server.fps.sot701;

import com.systex.jbranch.fubon.commons.cbs.vo.basic.CBSUtilOutputVO;
import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

import java.util.Date;
import java.util.List;

public class SOT701InputVO extends PagingInputVO{
    private String  custID;
    private String  trustAcct;
    private String  prodType;
    private String  tradeType;
    private String  isOBU;
    private String debitAcct; //扣款帳號
    /* 畫面使用欄位 */
    private String  sno;
    private Date    esbDate;
    private String  status;
    /* 上傳客戶經驗值 */
    private String comExp;

    /** DATA_067050_Type**/
    private String data067050Type;
    private List<CBSUtilOutputVO> data067050_067101_2;// 電文資料 自然人 _067050_067101、法人: _067050_067102
    private List<CBSUtilOutputVO> data067050_067000;  // 電文資料_067050_067000
    private List<CBSUtilOutputVO> data067050_067112;  // 電文資料_067050_067112
    private List<CBSUtilOutputVO> data067164_067165;  // 電文資料_067164_067165
    private List<CBSUtilOutputVO> data067050_067115;  // 電文資料_067050_067115
    private List<CBSUtilOutputVO> data060425_060433;  // 電文資料_060425_060433

    /** 客戶帳戶明細 **/
    private List<CBSUtilOutputVO> acctData;

    /** 不需要 DESC 相關欄位可將此設為 false，可避免發送 NFEI002 電文 **/
    private boolean needDesc = true;
    
    private String trustTS;		 //S=特金/M=金錢信託
    
    private boolean needFlagNumber = false; //#1695 貸轉投
    
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

    public String getTradeType() {
        return tradeType;
    }

    public void setTradeType(String tradeType) {
        this.tradeType = tradeType;
    }

    public String getIsOBU() {
        return isOBU;
    }

    public void setIsOBU(String isOBU) {
        this.isOBU = isOBU;
    }

    public String getSno() {
        return sno;
    }

    public void setSno(String sno) {
        this.sno = sno;
    }

    public Date getEsbDate() {
        return esbDate;
    }

    public void setEsbDate(Date esbDate) {
        this.esbDate = esbDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getComExp() {
		return comExp;
	}

	public void setComExp(String comExp) {
		this.comExp = comExp;
	}

	public String getTrustAcct() {
		return trustAcct;
	}

	public void setTrustAcct(String trustAcct) {
		this.trustAcct = trustAcct;
	}

	public String getDebitAcct() {
		return debitAcct;
	}

	public void setDebitAcct(String debitAcct) {
		this.debitAcct = debitAcct;
	}

	@Override
    public String toString() {
        return "SOT701InputVO{" +
                "custID='" + custID + '\'' +
                ", prodType='" + prodType + '\'' +
                ", tradeType='" + tradeType + '\'' +
                ", isOBU='" + isOBU + '\'' +
                ", sno='" + sno + '\'' +
                ", esbDate=" + esbDate +
                ", status='" + status + '\'' +
                ", comExp='" + comExp + '\'' +
                ", trustAcct='" + trustAcct + '\'' +
                ", debitAcct='" + debitAcct + '\'' +
                '}';
    }

    public List<CBSUtilOutputVO> getAcctData() {
        return acctData;
    }

    public void setAcctData(List<CBSUtilOutputVO> acctData) {
        this.acctData = acctData;
    }

    public String getData067050Type() {
        return data067050Type;
    }

    public void setData067050Type(String data067050Type) {
        this.data067050Type = data067050Type;
    }

    public List<CBSUtilOutputVO> getData067050_067101_2() {
        return data067050_067101_2;
    }

    public void setData067050_067101_2(List<CBSUtilOutputVO> data067050_067101_2) {
        this.data067050_067101_2 = data067050_067101_2;
    }

    public List<CBSUtilOutputVO> getData067050_067000() {
        return data067050_067000;
    }

    public void setData067050_067000(List<CBSUtilOutputVO> data067050_067000) {
        this.data067050_067000 = data067050_067000;
    }

    public List<CBSUtilOutputVO> getData067050_067112() {
        return data067050_067112;
    }

    public void setData067050_067112(List<CBSUtilOutputVO> data067050_067112) {
        this.data067050_067112 = data067050_067112;
    }

    public List<CBSUtilOutputVO> getData067164_067165() {
        return data067164_067165;
    }

    public void setData067164_067165(List<CBSUtilOutputVO> data067164_067165) {
        this.data067164_067165 = data067164_067165;
    }

    public List<CBSUtilOutputVO> getData067050_067115() {
        return data067050_067115;
    }

    public void setData067050_067115(List<CBSUtilOutputVO> data067050_067115) {
        this.data067050_067115 = data067050_067115;
    }

    public List<CBSUtilOutputVO> getData060425_060433() {
        return data060425_060433;
    }

    public void setData060425_060433(List<CBSUtilOutputVO> data060425_060433) {
        this.data060425_060433 = data060425_060433;
    }

    public boolean isNeedDesc() {
        return needDesc;
    }

    public void setNeedDesc(boolean needDesc) {
        this.needDesc = needDesc;
    }

	public String getTrustTS() {
		return trustTS;
	}

	public void setTrustTS(String trustTS) {
		this.trustTS = trustTS;
	}

	public boolean isNeedFlagNumber() {
		return needFlagNumber;
	}

	public void setNeedFlagNumber(boolean needFlagNumber) {
		this.needFlagNumber = needFlagNumber;
	}
    
}
