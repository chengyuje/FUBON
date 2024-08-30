package com.systex.jbranch.fubon.commons.esb.vo.vn085n;

import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by SebastianWu on 2016/9/9.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class VN085NOutputVO {
    @XmlElement
    private String SPRefId;     //傳送序號
    @XmlElement
    private String AcctId16;    //帳號
    @XmlElement
    private String Name;        //姓名
    @XmlElement
    private String SEX;         //性別 0:女 1:男 2:公司
    @XmlElement(name="TxRepeat")
    private List<VN085NOutputVODetailsVO> details;
    
    
    public List<VN085NOutputVODetailsVO> getDetails() {
		return details;
	}

	public void setDetails(List<VN085NOutputVODetailsVO> details) {
		this.details = details;
	}

	public String getSPRefId() {
        return SPRefId;
    }

    public void setSPRefId(String SPRefId) {
        this.SPRefId = SPRefId;
    }

    public String getAcctId16() {
        return AcctId16;
    }

    public void setAcctId16(String acctId16) {
        AcctId16 = acctId16;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getSEX() {
        return SEX;
    }

    public void setSEX(String SEX) {
        this.SEX = SEX;
    }


}
