package com.systex.jbranch.fubon.commons.esb.vo.fp032671;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;

import com.systex.jbranch.fubon.commons.esb.vo.eb172656.EB172656OutputDetailsVO;

/**
 * Created by SamTu on 2018/02/13.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class FP032671OutputVO {
	@XmlElement
	private String BRH_COD;
	@XmlElement
	private String RM;
	@XmlElement
	private String CUST_NO;
	@XmlElement
	private String CUST_NAME_S;
	@XmlElement
	private String ADV_COD;
	@XmlElement
	private String REF_IDNO;
	
	@XmlElement(name="TxRepeat")
	private List<FP032671OutputDetailsVO> details;
	
	
	
	public List<FP032671OutputDetailsVO> getDetails() {
		return details;
	}
	public void setDetails(List<FP032671OutputDetailsVO> details) {
		this.details = details;
	}
	public String getBRH_COD() {
		return BRH_COD;
	}
	public void setBRH_COD(String bRH_COD) {
		BRH_COD = bRH_COD;
	}
	public String getRM() {
		return RM;
	}
	public void setRM(String rM) {
		RM = rM;
	}
	public String getCUST_NO() {
		return CUST_NO;
	}
	public void setCUST_NO(String cUST_NO) {
		CUST_NO = cUST_NO;
	}
	public String getCUST_NAME_S() {
		return CUST_NAME_S;
	}
	public void setCUST_NAME_S(String cUST_NAME_S) {
		CUST_NAME_S = cUST_NAME_S;
	}
	public String getADV_COD() {
		return ADV_COD;
	}
	public void setADV_COD(String aDV_COD) {
		ADV_COD = aDV_COD;
	}
	public String getREF_IDNO() {
		return REF_IDNO;
	}
	public void setREF_IDNO(String rEF_IDNO) {
		REF_IDNO = rEF_IDNO;
	}

	
}