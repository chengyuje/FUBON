package com.systex.jbranch.fubon.commons.esb.vo.njweea60;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.math.BigDecimal;

/**
 * 
 * @author 1800036
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class NJWEEA60OutputVODetails {
    @XmlElement
	private String DefaultFeeRate;
    @XmlElement
	private String BestFeeRate;
    @XmlElement
	private String PayableFeeRate;
    @XmlElement
	private String FeeAmt;
    @XmlElement
	private String PayableFeeAmt;
    @XmlElement
	private String TxAmt;
    @XmlElement
	private String FeeType1;
    @XmlElement
	private String FeeType2;
    @XmlElement
	private String FeeType3;
    @XmlElement
	private String TxFeeRate1;
    @XmlElement
	private String TxFeeRate2;

    public String getDefaultFeeRate() {
        return DefaultFeeRate;
    }

    public void setDefaultFeeRate(String defaultFeeRate) {
        DefaultFeeRate = defaultFeeRate;
    }

    public String getBestFeeRate() {
        return BestFeeRate;
    }

    public void setBestFeeRate(String bestFeeRate) {
        BestFeeRate = bestFeeRate;
    }

    public String getPayableFeeRate() {
        return PayableFeeRate;
    }

    public void setPayableFeeRate(String payableFeeRate) {
        PayableFeeRate = payableFeeRate;
    }

    public String getFeeAmt() {
		return FeeAmt;
	}

	public void setFeeAmt(String feeAmt) {
		FeeAmt = feeAmt;
	}

	public String getPayableFeeAmt() {
		return PayableFeeAmt;
	}

	public void setPayableFeeAmt(String payableFeeAmt) {
		PayableFeeAmt = payableFeeAmt;
	}

	public String getTxAmt() {
		return TxAmt;
	}

	public void setTxAmt(String txAmt) {
		TxAmt = txAmt;
	}

	public String getFeeType1() {
		return FeeType1;
	}

	public void setFeeType1(String feeType1) {
		FeeType1 = feeType1;
	}

	public String getFeeType2() {
		return FeeType2;
	}

	public void setFeeType2(String feeType2) {
		FeeType2 = feeType2;
	}

	public String getFeeType3() {
		return FeeType3;
	}

	public void setFeeType3(String feeType3) {
		FeeType3 = feeType3;
	}

	public String getTxFeeRate1() {
		return TxFeeRate1;
	}

	public void setTxFeeRate1(String txFeeRate1) {
		TxFeeRate1 = txFeeRate1;
	}

	public String getTxFeeRate2() {
		return TxFeeRate2;
	}

	public void setTxFeeRate2(String txFeeRate2) {
		TxFeeRate2 = txFeeRate2;
	}

	@Override
    public String toString() {
        return "NJWEEA60OutputVODetails{" +
                "DefaultFeeRate=" + DefaultFeeRate +
                ", BestFeeRate=" + BestFeeRate +
                ", PayableFeeRate=" + PayableFeeRate +
                ", FeeAmt=" + FeeAmt +
                ", TxAmt=" + TxAmt +
                ", FeeType1=" + FeeType1 +
                ", FeeType2=" + FeeType2 +
                ", FeeType3=" + FeeType3 +
                ", TxFeeRate1=" + TxFeeRate1 +
                ", TxFeeRate2=" + TxFeeRate2 +
                '}';
    }
}
