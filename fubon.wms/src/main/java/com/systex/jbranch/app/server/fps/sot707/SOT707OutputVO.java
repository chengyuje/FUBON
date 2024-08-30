package com.systex.jbranch.app.server.fps.sot707;

import java.util.List;

/**
 * Created by SebastianWu on 2016/9/26.
 */
public class SOT707OutputVO {
    private List<CustAssetBondVO>  custAssetBondList;   //客戶海外債/SN資產資料
    private List<ProdRefValVO> prodRefVal;    //產品參考報價資料
    private String errorCode;   //錯誤代碼
    private String errorMsg;    //錯誤訊息
    private List<String> warningCode;   //申贖電文判斷專投警語
    private List<BondGTCDataVO> bondGTCDataList;	//長效單資料
    private List<BondGTCDataDetailVO> bondGTCDataDetailList;	//長效單明細資料
    
    public List<CustAssetBondVO> getCustAssetBondList() {
        return custAssetBondList;
    }

    public void setCustAssetBondList(List<CustAssetBondVO> custAssetBondList) {
        this.custAssetBondList = custAssetBondList;
    }

    public List<ProdRefValVO> getProdRefVal() {
        return prodRefVal;
    }

    public void setProdRefVal(List<ProdRefValVO> prodRefVal) {
        this.prodRefVal = prodRefVal;
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

    public List<String> getWarningCode() {
        return warningCode;
    }

    public void setWarningCode(List<String> warningCode) {
        this.warningCode = warningCode;
    }

    public List<BondGTCDataVO> getBondGTCDataList() {
		return bondGTCDataList;
	}

	public void setBondGTCDataList(List<BondGTCDataVO> bondGTCDataList) {
		this.bondGTCDataList = bondGTCDataList;
	}

	public List<BondGTCDataDetailVO> getBondGTCDataDetailList() {
		return bondGTCDataDetailList;
	}

	public void setBondGTCDataDetailList(
			List<BondGTCDataDetailVO> bondGTCDataDetailList) {
		this.bondGTCDataDetailList = bondGTCDataDetailList;
	}

	@Override
    public String toString() {
        return "SOT707OutputVO{" +
                "custAssetBondList=" + custAssetBondList +
                ", prodRefVal=" + prodRefVal +
                ", errorCode='" + errorCode + '\'' +
                ", errorMsg='" + errorMsg + '\'' +
                ", warningCode=" + warningCode +
                ", bondGTCDataList=" + bondGTCDataList +
                ", bondGTCDataDetailList=" + bondGTCDataDetailList +
                '}';
    }
}
