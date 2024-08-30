package com.systex.jbranch.fubon.commons.esb.vo.nfee001;

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
public class NFEE001OutputVO {
    @XmlElement
    private String SPRefId;     //傳送序號
    @XmlElement
    private String AcctId16;    //帳號
    @XmlElement
    private String Name;        //姓名
    @XmlElement
    private String SEX;         //性別 0:女 1:男 2:公司
    @XmlElement
    private String Occur;		//資料筆數
    @XmlElement(name="TxRepeat")
    private List<NFEE001OutputVODetailsVO> details;
    
    
    public List<NFEE001OutputVODetailsVO> getDetails() {
		return details;
	}

	public void setDetails(List<NFEE001OutputVODetailsVO> details) {
		this.details = details;
	}

	public String getOccur() {
		return Occur;
	}

	public void setOccur(String occur) {
		Occur = occur;
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
