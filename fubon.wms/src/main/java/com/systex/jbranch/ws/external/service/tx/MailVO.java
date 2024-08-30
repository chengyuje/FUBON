package com.systex.jbranch.ws.external.service.tx;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class MailVO {
    private String YYY;         // 民國年
    private String MM;          // 月
    private String DD;          // 日
    private String ACNO;        // 帳戶
    private String RECEIVER;    // 收件者 Email

    @Override
    public String toString() {
        return "MailVO{" +
                "YYY='" + YYY + '\'' +
                ", MM='" + MM + '\'' +
                ", DD='" + DD + '\'' +
                ", ACNO='" + ACNO + '\'' +
                ", RECEIVER='" + RECEIVER + '\'' +
                '}';
    }

    public String getYYY() {
        return YYY;
    }

    public void setYYY(String YYY) {
        this.YYY = YYY;
    }

    public String getMM() {
        return MM;
    }

    public void setMM(String MM) {
        this.MM = MM;
    }

    public String getDD() {
        return DD;
    }

    public void setDD(String DD) {
        this.DD = DD;
    }

    public String getACNO() {
        return ACNO;
    }

    public void setACNO(String ACNO) {
        this.ACNO = ACNO;
    }

    public String getRECEIVER() {
        return RECEIVER;
    }

    public void setRECEIVER(String RECEIVER) {
        this.RECEIVER = RECEIVER;
    }
}
