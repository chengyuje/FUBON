package com.systex.jbranch.fubon.commons.esb.vo.nfbrne;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * 國外私募基金贖回修正單位數交易電文
 * NFBRNE
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class NFBRNEOutputVO {
	@XmlElement
    private String ErrorCode;  //錯誤碼
    @XmlElement
    private String ErrorMsg;   //錯誤訊息

    public String getErrorCode() {
        return ErrorCode;
    }

    public void setErrorCode(String errorCode) {
        ErrorCode = errorCode;
    }

    public String getErrorMsg() {
        return ErrorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        ErrorMsg = errorMsg;
    }
    
}
