package com.systex.jbranch.fubon.commons.esb.vo.njweea60;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.math.BigDecimal;
import java.util.List;

/**
 * 
 * @author 1800036
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class NJWEEA60OutputVO {
    @XmlElement
	private String SPRefId; //傳送序號
    @XmlElement
	private String AcctId16;  //帳號
    @XmlElement
	private String Name;  //姓名
    @XmlElement
    private String SEX;    //性別
    @XmlElement
	private String Occur;   //筆數

    @XmlElement(name = "TxRepeat")
	private List<NJWEEA60OutputVODetails> details;

    public String getSPRefId() {
		return SPRefId;
	}

	public void setSPRefId(String sPRefId) {
		SPRefId = sPRefId;
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

	public void setSEX(String sEX) {
		SEX = sEX;
	}

	public String getOccur() {
		return Occur;
	}

	public void setOccur(String occur) {
		Occur = occur;
	}

	public List<NJWEEA60OutputVODetails> getDetails() {
        return details;
    }

    public void setDetails(List<NJWEEA60OutputVODetails> details) {
        this.details = details;
    }

    @Override
    public String toString() {
        return "NJWEEA60OutputVO{" +
                "SPRefId='" + SPRefId + '\'' +
                ", AcctId16='" + AcctId16 + '\'' +
                ", Name='" + Name + '\'' +
                ", SEX=" + SEX + '\'' +
                ", Occur='" + Occur + '\'' +
                ", details=" + details +
                '}';
    }
}
