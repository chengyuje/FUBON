package com.systex.jbranch.fubon.commons.esb.vo.ajbrva2;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by Carley on 2024/07/26
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class AJBRVA2OutputVODetails {
    @XmlElement
	private String DefaultFeeRate;	// 表定手續費率
    @XmlElement
	private String BestFeeRate;		// 最優手續率
    @XmlElement
	private String PayableFeeRate;	// 應付前手息率

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
        return "AJBRVA2OutputVODetails{" +
                "DefaultFeeRate=" + DefaultFeeRate +
                ", BestFeeRate=" + BestFeeRate +
                ", PayableFeeRate=" + PayableFeeRate +
                '}';
    }
}
