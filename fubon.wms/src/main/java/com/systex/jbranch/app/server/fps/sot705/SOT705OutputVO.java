package com.systex.jbranch.app.server.fps.sot705;

import java.util.List;

/**
 * Created by SebastianWu on 2016/9/21.
 */
public class SOT705OutputVO {
    private List<CustAssetETFVO> custAssetETFList;      //客戶ETF庫存資料 (getCustAssetETFData回傳)
    private List<CustAssetETFVO> eList;					//客戶ETF庫存資料(<RANGE>0001)
    private List<CustAssetETFVO> custAssetStockList;    //客戶股票庫存資料 (getCustAssetETFData回傳)
    private List<CustAssetETFVO> sList;					//客戶股票庫存資料(<RANGE>0001)
    private List<CustAssetETFVO> onTradeList;			//賣出委託交易資訊
    private List tradeDueDate;                          //交易指示到期日 (getTradeDueDate回傳,回傳欄位名稱對應請參照下行電文格式)
    private String warningMsg;                          //申贖電文NRBRVA9回傳訊息
    private String errorCode;                           //錯誤代碼
    private String errorMsg;                            //錯誤訊息
    private String earnAcct;                            //NRBRVA9的ETF首購收益帳號
    private List<CustAssetETFMonVO> custAssetETFMonList;//金錢信託_客戶ETF庫存資料 
    
    public List<CustAssetETFVO> getCustAssetETFList() {
        return custAssetETFList;
    }

    public void setCustAssetETFList(List<CustAssetETFVO> custAssetETFList) {
        this.custAssetETFList = custAssetETFList;
    }

    public List<CustAssetETFVO> getCustAssetStockList() {
        return custAssetStockList;
    }

    public void setCustAssetStockList(List<CustAssetETFVO> custAssetStockList) {
        this.custAssetStockList = custAssetStockList;
    }

    public List<CustAssetETFVO> getsList() {
		return sList;
	}

	public void setsList(List<CustAssetETFVO> sList) {
		this.sList = sList;
	}

	public List getTradeDueDate() {
        return tradeDueDate;
    }

    public void setTradeDueDate(List tradeDueDate) {
        this.tradeDueDate = tradeDueDate;
    }

    public String getWarningMsg() {
        return warningMsg;
    }

    public void setWarningMsg(String warningMsg) {
        this.warningMsg = warningMsg;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public List<CustAssetETFVO> getOnTradeList() {
		return onTradeList;
	}

	public void setOnTradeList(List<CustAssetETFVO> onTradeList) {
		this.onTradeList = onTradeList;
	}
	
	public List<CustAssetETFVO> geteList() {
		return eList;
	}

	public void seteList(List<CustAssetETFVO> eList) {
		this.eList = eList;
	}

	public String getEarnAcct() {
		return earnAcct;
	}

	public void setEarnAcct(String earnAcct) {
		this.earnAcct = earnAcct;
	}

	public List<CustAssetETFMonVO> getCustAssetETFMonList() {
		return custAssetETFMonList;
	}

	public void setCustAssetETFMonList(List<CustAssetETFMonVO> custAssetETFMonList) {
		this.custAssetETFMonList = custAssetETFMonList;
	}

	@Override
	public String toString() {
		return "SOT705OutputVO [custAssetETFList=" + custAssetETFList
				+ ", eList=" + eList + ", custAssetStockList="
				+ custAssetStockList + ", onTradeList=" + onTradeList
				+ ", tradeDueDate=" + tradeDueDate + ", warningMsg="
				+ warningMsg + ", errorCode=" + errorCode + ", errorMsg="
				+ errorMsg + "]";
	}
}
