package com.systex.jbranch.fubon.commons.esb.vo.fc032154;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;

/**
 * Created by Walalala on 2016/12/21.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class FC032154OutputVO {
	@XmlElement
    private String CUST_NAME_S;
	@XmlElement(name="TxRepeat")
	private List<FC032154OutputDetailsVO> details;
	
	
	public String getCUST_NAME_S() {
		return CUST_NAME_S;
	}
	public void setCUST_NAME_S(String cUST_NAME_S) {
		CUST_NAME_S = cUST_NAME_S;
	}
	public List<FC032154OutputDetailsVO> getDetails() {
		return details;
	}
	public void setDetails(List<FC032154OutputDetailsVO> details) {
		this.details = details;
	}
	
	
	
}