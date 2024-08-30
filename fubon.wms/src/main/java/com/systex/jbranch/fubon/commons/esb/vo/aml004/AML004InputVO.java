package com.systex.jbranch.fubon.commons.esb.vo.aml004;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/***
 * 洗錢防制電文
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class AML004InputVO {
    private String BankNo = "TW";
    private String CustomerNo;

    public AML004InputVO() {}

    public AML004InputVO(String custId) {
        this.CustomerNo = custId;
    }

    public String getBankNo() {
        return BankNo;
    }

    public void setBankNo(String bankNo) {
        BankNo = bankNo;
    }

    public String getCustomerNo() {
        return CustomerNo;
    }

    public void setCustomerNo(String customerNo) {
        CustomerNo = customerNo;
    }
}
