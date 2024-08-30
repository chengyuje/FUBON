package com.systex.jbranch.ws.external.service.tx;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "Tx")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class TxVO {
    @XmlElement(name = "mail")
    private MailVO mailVO;
    private String errorMsg;

    public MailVO getMailVO() {
        return mailVO;
    }

    public void setMailVO(MailVO mailVO) {
        this.mailVO = mailVO;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
