package com.systex.jbranch.app.server.fps.iot150;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class IOT150NotFbReviewFormVO {
    private List<BigDecimal> insKeyNo;    // 保險公司進件主檔主鍵 TBIOT_MAIN.INS_KEYNO
    private Date notFbSignDate;       // 保險公司回饋簽收日期
    private String notFbOpName;       // 保險公司簽收窗口

    public List<BigDecimal> getInsKeyNo() {
        return insKeyNo;
    }

    public void setInsKeyNo(List<BigDecimal> insKeyNo) {
        this.insKeyNo = insKeyNo;
    }

    public Date getNotFbSignDate() {
        return notFbSignDate;
    }

    public void setNotFbSignDate(Date notFbSignDate) {
        this.notFbSignDate = notFbSignDate;
    }

    public String getNotFbOpName() {
        return notFbOpName;
    }

    public void setNotFbOpName(String notFbOpName) {
        this.notFbOpName = notFbOpName;
    }
}
