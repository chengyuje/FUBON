package com.systex.jbranch.app.server.fps.prd177;

import java.math.BigDecimal;

public class ProdBoundDataVO {
    private BigDecimal productSerialNum;    // 保險產品序號
    private String boundItem;               // 佣金項目
    private String boundName;               // 佣金名稱
    private String add1StartMonth;      // 單追分段計佣第一段起始月份
    private String add1EndMonth;        // 單追分段計佣第一段結束月份
    private BigDecimal add1CommissionRate;  // 單追分段計佣第一段目標佣金率
    private BigDecimal add1CommRateA;       // 單追分段計佣第一段超額佣金率
    private String channel;                 // 案件來源(小花/一般)

    public BigDecimal getProductSerialNum() {
        return productSerialNum;
    }

    public void setProductSerialNum(BigDecimal productSerialNum) {
        this.productSerialNum = productSerialNum;
    }

    public String getBoundItem() {
        return boundItem;
    }

    public void setBoundItem(String boundItem) {
        this.boundItem = boundItem;
    }

    public String getBoundName() {
        return boundName;
    }

    public void setBoundName(String boundName) {
        this.boundName = boundName;
    }

    public String getAdd1StartMonth() {
        return add1StartMonth;
    }

    public void setAdd1StartMonth(String add1StartMonth) {
        this.add1StartMonth = add1StartMonth;
    }

    public String getAdd1EndMonth() {
        return add1EndMonth;
    }

    public void setAdd1EndMonth(String add1EndMonth) {
        this.add1EndMonth = add1EndMonth;
    }

    public BigDecimal getAdd1CommissionRate() {
        return add1CommissionRate;
    }

    public void setAdd1CommissionRate(BigDecimal add1CommissionRate) {
        this.add1CommissionRate = add1CommissionRate;
    }

    public BigDecimal getAdd1CommRateA() {
        return add1CommRateA;
    }

    public void setAdd1CommRateA(BigDecimal add1CommRateA) {
        this.add1CommRateA = add1CommRateA;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }
}
