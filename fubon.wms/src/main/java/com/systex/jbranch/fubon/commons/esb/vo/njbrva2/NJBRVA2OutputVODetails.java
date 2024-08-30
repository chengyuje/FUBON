package com.systex.jbranch.fubon.commons.esb.vo.njbrva2;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.math.BigDecimal;

/**
 * Created by SebastianWu on 2016/9/29.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class NJBRVA2OutputVODetails {
    @XmlElement
	private String DefaultFeeRate;
    @XmlElement
	private String BestFeeRate;
    @XmlElement
	private String PayableFeeRate;

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

    @Override
    public String toString() {
        return "NJBRVA2OutputVODetails{" +
                "DefaultFeeRate=" + DefaultFeeRate +
                ", BestFeeRate=" + BestFeeRate +
                ", PayableFeeRate=" + PayableFeeRate +
                '}';
    }
}
