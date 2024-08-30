package com.systex.jbranch.app.server.fps.sot711;

import java.math.BigDecimal;

/**
 * Created by SebastianWu on 2016/9/29.
 */
public class SOT711OutputVO {
    private BigDecimal defaultFeeRate;  //表定手續費率
    private BigDecimal bestFeeRate;     //最優手續率
    private BigDecimal payableFeeRate;  //應付前手息率

    public BigDecimal getDefaultFeeRate() {
        return defaultFeeRate;
    }

    public void setDefaultFeeRate(BigDecimal defaultFeeRate) {
        this.defaultFeeRate = defaultFeeRate;
    }

    public BigDecimal getBestFeeRate() {
        return bestFeeRate;
    }

    public void setBestFeeRate(BigDecimal bestFeeRate) {
        this.bestFeeRate = bestFeeRate;
    }

    public BigDecimal getPayableFeeRate() {
        return payableFeeRate;
    }

    public void setPayableFeeRate(BigDecimal payableFeeRate) {
        this.payableFeeRate = payableFeeRate;
    }

    @Override
    public String toString() {
        return "SOT711OutputVO{" +
                "defaultFeeRate=" + defaultFeeRate +
                ", bestFeeRate=" + bestFeeRate +
                ", payableFeeRate=" + payableFeeRate +
                '}';
    }
}
