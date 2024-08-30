package com.systex.jbranch.app.server.fps.sot701;

import java.math.BigDecimal;

/**
 * Created by SebastianWu on 2016/9/9.
 *
 * 客戶停損停利資料VO
 */
public class CustPLDataVO {

    private BigDecimal takeProfitPerc;  //停利點
    private BigDecimal stopLossPerc;    //停損點
    private String plMsg;               //停損停利通知方式

    public CustPLDataVO() {
    }

    public CustPLDataVO(BigDecimal takeProfitPerc, BigDecimal stopLossPerc, String plMsg) {
        this.takeProfitPerc = takeProfitPerc;
        this.stopLossPerc = stopLossPerc;
        this.plMsg = plMsg;
    }

    public BigDecimal getTakeProfitPerc() {
        return takeProfitPerc;
    }

    public void setTakeProfitPerc(BigDecimal takeProfitPerc) {
        this.takeProfitPerc = takeProfitPerc;
    }

    public BigDecimal getStopLossPerc() {
        return stopLossPerc;
    }

    public void setStopLossPerc(BigDecimal stopLossPerc) {
        this.stopLossPerc = stopLossPerc;
    }

    public String getPlMsg() {
        return plMsg;
    }

    public void setPlMsg(String plMsg) {
        this.plMsg = plMsg;
    }

    @Override
    public String toString() {
        return "CustPLDataVO{" +
                "takeProfitPerc=" + takeProfitPerc +
                ", stopLossPerc=" + stopLossPerc +
                ", plMsg='" + plMsg + '\'' +
                '}';
    }
}
