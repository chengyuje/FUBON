package com.systex.jbranch.fubon.commons.esb.vo.nb052650;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by SebastianWu on 2016/10/18.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
@Component
@Scope("request")
public class NB052650OutputVO {
	@XmlElement
	private String MEDIUM_ID;
	@XmlElement
	private String I_CUST_NO;
	@XmlElement
	private String IS_R44;
	@XmlElement
	private String IS_R45;
	@XmlElement
	private String IS_R33;
	@XmlElement
	private String CUST_NAME;
	@XmlElement(name = "TxRepeat")
	private List<NB052650OutputVODetails> details;
	public String getMEDIUM_ID() {
		return MEDIUM_ID;
	}
	public void setMEDIUM_ID(String mEDIUM_ID) {
		MEDIUM_ID = mEDIUM_ID;
	}
	public String getI_CUST_NO() {
		return I_CUST_NO;
	}
	public void setI_CUST_NO(String i_CUST_NO) {
		I_CUST_NO = i_CUST_NO;
	}
	public String getIS_R44() {
		return IS_R44;
	}
	public void setIS_R44(String iS_R44) {
		IS_R44 = iS_R44;
	}
	public String getIS_R45() {
		return IS_R45;
	}
	public void setIS_R45(String iS_R45) {
		IS_R45 = iS_R45;
	}
	public String getIS_R33() {
		return IS_R33;
	}
	public void setIS_R33(String iS_R33) {
		IS_R33 = iS_R33;
	}
	public String getCUST_NAME() {
		return CUST_NAME;
	}
	public void setCUST_NAME(String cUST_NAME) {
		CUST_NAME = cUST_NAME;
	}
	public List<NB052650OutputVODetails> getDetails() {
		return details;
	}
	public void setDetails(List<NB052650OutputVODetails> details) {
		this.details = details;
	}

}
