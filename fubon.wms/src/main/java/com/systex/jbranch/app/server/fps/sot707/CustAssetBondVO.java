package com.systex.jbranch.app.server.fps.sot707;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by SebastianWu on 2016/9/26.
 */
public class CustAssetBondVO {
	private String  TrustNo;    		//憑証單號
	private String  BondNo; 			//債券代號
	private String  BondName;   		//債券名稱
	private String  CurCode;    		//幣別
	private BigDecimal Unit;   			//庫存張數
    private BigDecimal  TrustVal;   	//庫存面額
    private String  TrustType;  		//信託業務別
    private String  TrustAcct;  		//信託帳號
    private String  BondType;   		//債券種類
    private BigDecimal  BondVal;    	//票面價值
    private BigDecimal  RefPrice;   	//參考報價(市價)
    private Date    RefPriceDate;   	//參考報價日期
    private Date    ApplyDate;  		//申購日期
    private BigDecimal  TrustAmt;   	//信託本金
    private BigDecimal  TrustFeeRate;   //信託管理費率
    private BigDecimal  PayableFeeRate; //應收前手息率
    private BigDecimal  PayableFee; 	//應收前手息
    private BigDecimal  AccuInterest;   //累積配息
    private String  StorageStatus;  	//狀態
    private Date NewInterestDate;		//最近一期配息日期
    private BigDecimal NewAccuInterest;	//最近一期配息金額
    
    //2018.01.04 add by Carley(已付前手息,應收前手息,含息報酬率三欄的來源調整為電文來源 #4106)
    private BigDecimal Frontfee1;		//已付前手息
    private BigDecimal Frontfee2;		//應收前手息
    private String Sign;				//含累積現金配息報酬率正負號(空白為正)
    private BigDecimal IntRate;			//含累積現金配息報酬率
    private String IsPledged;			//質借圈存註記(cmkType=MK03 ==> Y else N) 	MK01 法院圈存; MK02 警示戶圈存; MK03 質權設定圈存  => 區分 他行,個金,法金; MK99 其他 

    private String trustTS;
    
    public String getTrustTS() {
		return trustTS;
	}

	public void setTrustTS(String trustTS) {
		this.trustTS = trustTS;
	}
    public String getTrustNo() {
        return TrustNo;
    }

    public void setTrustNo(String trustNo) {
        TrustNo = trustNo;
    }

    public String getBondNo() {
        return BondNo;
    }

    public void setBondNo(String bondNo) {
        BondNo = bondNo;
    }

    public String getBondName() {
        return BondName;
    }

    public void setBondName(String bondName) {
        BondName = bondName;
    }

    public String getCurCode() {
        return CurCode;
    }

    public void setCurCode(String curCode) {
        CurCode = curCode;
    }

    public BigDecimal getUnit() {
        return Unit;
    }

    public void setUnit(BigDecimal unit) {
        Unit = unit;
    }

    public BigDecimal getTrustVal() {
        return TrustVal;
    }

    public void setTrustVal(BigDecimal trustVal) {
        TrustVal = trustVal;
    }

    public String getTrustType() {
        return TrustType;
    }

    public void setTrustType(String trustType) {
        TrustType = trustType;
    }

    public String getTrustAcct() {
        return TrustAcct;
    }

    public void setTrustAcct(String trustAcct) {
        TrustAcct = trustAcct;
    }

    public String getBondType() {
        return BondType;
    }

    public void setBondType(String bondType) {
        BondType = bondType;
    }

    public BigDecimal getBondVal() {
        return BondVal;
    }

    public void setBondVal(BigDecimal bondVal) {
        BondVal = bondVal;
    }

    public BigDecimal getRefPrice() {
        return RefPrice;
    }

    public void setRefPrice(BigDecimal refPrice) {
        RefPrice = refPrice;
    }

    public Date getRefPriceDate() {
        return RefPriceDate;
    }

    public void setRefPriceDate(Date refPriceDate) {
        RefPriceDate = refPriceDate;
    }

    public Date getApplyDate() {
        return ApplyDate;
    }

    public void setApplyDate(Date applyDate) {
        ApplyDate = applyDate;
    }

    public BigDecimal getTrustAmt() {
        return TrustAmt;
    }

    public void setTrustAmt(BigDecimal trustAmt) {
        TrustAmt = trustAmt;
    }

    public BigDecimal getTrustFeeRate() {
        return TrustFeeRate;
    }

    public void setTrustFeeRate(BigDecimal trustFeeRate) {
        TrustFeeRate = trustFeeRate;
    }
    
    public BigDecimal getPayableFeeRate() {
		return PayableFeeRate;
	}

	public void setPayableFeeRate(BigDecimal payableFeeRate) {
		PayableFeeRate = payableFeeRate;
	}

	public BigDecimal getPayableFee() {
        return PayableFee;
    }

    public void setPayableFee(BigDecimal payableFee) {
        PayableFee = payableFee;
    }

    public BigDecimal getAccuInterest() {
        return AccuInterest;
    }

    public void setAccuInterest(BigDecimal accuInterest) {
        AccuInterest = accuInterest;
    }

    public String getStorageStatus() {
        return StorageStatus;
    }

    public void setStorageStatus(String storageStatus) {
        StorageStatus = storageStatus;
    }

    public Date getNewInterestDate() {
		return NewInterestDate;
	}

	public void setNewInterestDate(Date newInterestDate) {
		NewInterestDate = newInterestDate;
	}

	public BigDecimal getNewAccuInterest() {
		return NewAccuInterest;
	}

	public void setNewAccuInterest(BigDecimal newAccuInterest) {
		NewAccuInterest = newAccuInterest;
	}

	public BigDecimal getFrontfee1() {
		return Frontfee1;
	}

	public void setFrontfee1(BigDecimal frontfee1) {
		Frontfee1 = frontfee1;
	}

	public BigDecimal getFrontfee2() {
		return Frontfee2;
	}

	public void setFrontfee2(BigDecimal frontfee2) {
		Frontfee2 = frontfee2;
	}

	public String getSign() {
		return Sign;
	}

	public void setSign(String sign) {
		Sign = sign;
	}

	public BigDecimal getIntRate() {
		return IntRate;
	}

	public void setIntRate(BigDecimal intRate) {
		IntRate = intRate;
	}

	public String getIsPledged() {
		return IsPledged;
	}

	public void setIsPledged(String isPledged) {
		IsPledged = isPledged;
	}

	@Override
	public String toString() {
		return "CustAssetBondVO [TrustNo=" + TrustNo + ", BondNo=" + BondNo
				+ ", BondName=" + BondName + ", CurCode=" + CurCode + ", Unit="
				+ Unit + ", TrustVal=" + TrustVal + ", TrustType=" + TrustType
				+ ", TrustAcct=" + TrustAcct + ", BondType=" + BondType
				+ ", BondVal=" + BondVal + ", RefPrice=" + RefPrice
				+ ", RefPriceDate=" + RefPriceDate + ", ApplyDate=" + ApplyDate
				+ ", TrustAmt=" + TrustAmt + ", TrustFeeRate=" + TrustFeeRate
				+ ", PayableFeeRate=" + PayableFeeRate + ", PayableFee="
				+ PayableFee + ", AccuInterest=" + AccuInterest
				+ ", StorageStatus=" + StorageStatus + ", NewInterestDate="
				+ NewInterestDate + ", NewAccuInterest=" + NewAccuInterest
				+ ", Frontfee1=" + Frontfee1 + ", Frontfee2=" + Frontfee2
				+ ", Sign=" + Sign + ", IntRate=" + IntRate + ", IsPledged=" + IsPledged + "]";
	}
}
