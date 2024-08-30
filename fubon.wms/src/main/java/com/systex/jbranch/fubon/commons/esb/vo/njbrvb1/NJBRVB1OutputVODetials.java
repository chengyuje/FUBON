package com.systex.jbranch.fubon.commons.esb.vo.njbrvb1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by SebastianWu on 2016/9/29.
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType
public class NJBRVB1OutputVODetials {
    @XmlElement
	private String TrustNo; 		//憑証單號
    @XmlElement
	private String BondNo; 			//債券代號
    @XmlElement
	private String BondName; 		//債券名稱
    @XmlElement
	private String CurCode; 		//幣別
    @XmlElement
	private String Unit; 			//庫存張數
    @XmlElement
	private String TrustVal; 		//庫存面額
    @XmlElement
	private String TrustType; 		//信託業務別
    @XmlElement
	private String TrustAcct; 		//信託帳號
    @XmlElement
	private String BondType; 		//債券種類
    @XmlElement
	private String BondVal; 		//票面價值
    @XmlElement
	private String RefPrice; 		//參考報價(市價)
    @XmlElement
	private String RefPriceDate; 	//參考報價日期
    @XmlElement
	private String ApplyDate; 		//申購日期
    @XmlElement
	private String TrustAmt; 		//信託本金
    @XmlElement
	private String TrustFeeRate; 	//信託管理費率
    @XmlElement
	private String PayableFee; 		//應收前手息率
    @XmlElement
	private String AccuInterest; 	//累積配息
    @XmlElement
	private String StorageStatus; 	//狀態
    @XmlElement
    private String NewInterestDate;	//最近一期配息日期
    @XmlElement
    private String NewAccuInterest;	//最近一期配息金額
    
    //2018.01.04 add by Carley(已付前手息,應收前手息,含息報酬率三欄的來源調整為電文來源 #4106)
    @XmlElement
    private String Frontfee1;		//已付前手息
    @XmlElement
    private String Frontfee2;		//應收前手息
    @XmlElement
    private String Sign;			//含累積現金配息報酬率正負號(空白為正)
    @XmlElement
    private String IntRate;			//含累積現金配息報酬率
    @XmlElement
    private String CmkType;			//質借圈存註記 	MK01 法院圈存; MK02 警示戶圈存; MK03 質權設定圈存  => 區分 他行,個金,法金; MK99 其他
    
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

    public String getUnit() {
        return Unit;
    }

    public void setUnit(String unit) {
        Unit = unit;
    }

    public String getTrustVal() {
        return TrustVal;
    }

    public void setTrustVal(String trustVal) {
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

    public String getBondVal() {
        return BondVal;
    }

    public void setBondVal(String bondVal) {
        BondVal = bondVal;
    }

    public String getRefPrice() {
        return RefPrice;
    }

    public void setRefPrice(String refPrice) {
        RefPrice = refPrice;
    }

    public String getRefPriceDate() {
        return RefPriceDate;
    }

    public void setRefPriceDate(String refPriceDate) {
        RefPriceDate = refPriceDate;
    }

    public String getApplyDate() {
        return ApplyDate;
    }



    public void setApplyDate(String applyDate) {
        ApplyDate = applyDate;
    }

    public String getTrustAmt() {
        return TrustAmt;
    }

    public void setTrustAmt(String trustAmt) {
        TrustAmt = trustAmt;
    }

    public String getTrustFeeRate() {
        return TrustFeeRate;
    }

    public void setTrustFeeRate(String trustFeeRate) {
        TrustFeeRate = trustFeeRate;
    }

    public String getPayableFee() {
        return PayableFee;
    }

    public void setPayableFee(String payableFee) {
        PayableFee = payableFee;
    }

    public String getAccuInterest() {
        return AccuInterest;
    }

    public void setAccuInterest(String accuInterest) {
        AccuInterest = accuInterest;
    }

    public String getStorageStatus() {
        return StorageStatus;
    }

    public void setStorageStatus(String storageStatus) {
        StorageStatus = storageStatus;
    }

    public String getNewInterestDate() {
		return NewInterestDate;
	}

	public void setNewInterestDate(String newInterestDate) {
		NewInterestDate = newInterestDate;
	}

	public String getNewAccuInterest() {
		return NewAccuInterest;
	}

	public void setNewAccuInterest(String newAccuInterest) {
		NewAccuInterest = newAccuInterest;
	}

	public String getFrontfee1() {
		return Frontfee1;
	}

	public void setFrontfee1(String frontfee1) {
		Frontfee1 = frontfee1;
	}

	public String getFrontfee2() {
		return Frontfee2;
	}

	public void setFrontfee2(String frontfee2) {
		Frontfee2 = frontfee2;
	}

	public String getSign() {
		return Sign;
	}

	public void setSign(String sign) {
		Sign = sign;
	}

	public String getIntRate() {
		return IntRate;
	}

	public void setIntRate(String intRate) {
		IntRate = intRate;
	}

	public String getCmkType() {
		return CmkType;
	}

	public void setCmkType(String cmkType) {
		CmkType = cmkType;
	}

	@Override
	public String toString() {
		return "NJBRVB1OutputVODetials [TrustNo=" + TrustNo + ", BondNo="
				+ BondNo + ", BondName=" + BondName + ", CurCode=" + CurCode
				+ ", Unit=" + Unit + ", TrustVal=" + TrustVal + ", TrustType="
				+ TrustType + ", TrustAcct=" + TrustAcct + ", BondType="
				+ BondType + ", BondVal=" + BondVal + ", RefPrice=" + RefPrice
				+ ", RefPriceDate=" + RefPriceDate + ", ApplyDate=" + ApplyDate
				+ ", TrustAmt=" + TrustAmt + ", TrustFeeRate=" + TrustFeeRate
				+ ", PayableFee=" + PayableFee + ", AccuInterest="
				+ AccuInterest + ", StorageStatus=" + StorageStatus
				+ ", NewInterestDate=" + NewInterestDate + ", NewAccuInterest="
				+ NewAccuInterest + ", Frontfee1=" + Frontfee1 + ", Frontfee2="
				+ Frontfee2 + ", Sign=" + Sign + ", IntRate=" + IntRate + ", CmkType=" + CmkType
				+ ", getTrustNo()=" + getTrustNo() + ", getBondNo()="
				+ getBondNo() + ", getBondName()=" + getBondName()
				+ ", getCurCode()=" + getCurCode() + ", getUnit()=" + getUnit()
				+ ", getTrustVal()=" + getTrustVal() + ", getTrustType()="
				+ getTrustType() + ", getTrustAcct()=" + getTrustAcct()
				+ ", getBondType()=" + getBondType() + ", getBondVal()="
				+ getBondVal() + ", getRefPrice()=" + getRefPrice()
				+ ", getRefPriceDate()=" + getRefPriceDate()
				+ ", getApplyDate()=" + getApplyDate() + ", getTrustAmt()="
				+ getTrustAmt() + ", getTrustFeeRate()=" + getTrustFeeRate()
				+ ", getPayableFee()=" + getPayableFee()
				+ ", getAccuInterest()=" + getAccuInterest()
				+ ", getStorageStatus()=" + getStorageStatus()
				+ ", getNewInterestDate()=" + getNewInterestDate()
				+ ", getNewAccuInterest()=" + getNewAccuInterest()
				+ ", getFrontfee1()=" + getFrontfee1() + ", getFrontfee2()="
				+ getFrontfee2() + ", getSign()=" + getSign()
				+ ", getIntRate()=" + getIntRate() + ", getCmkType()=" + getCmkType() + ", getClass()="
				+ getClass() + ", hashCode()=" + hashCode() + ", toString()="
				+ super.toString() + "]";
	}
}
