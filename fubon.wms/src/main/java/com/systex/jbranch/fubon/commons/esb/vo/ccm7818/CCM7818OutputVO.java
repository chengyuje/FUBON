package com.systex.jbranch.fubon.commons.esb.vo.ccm7818;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;

import com.systex.jbranch.fubon.commons.esb.vo.fc032675.FC032675OutputDetailsVO;

/**
 * Created by Walalala on 2016/12/07.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class CCM7818OutputVO {
	@XmlElement
	private String FUNCTION;
	@XmlElement
	private String RANGE;
	@XmlElement
	private String OCCUR;
	@XmlElement(name="TxRepeat")
	private List<CCM7818OutputDetailsVO> details;
	
	
	public String getFUNCTION() {
		return FUNCTION;
	}
	public void setFUNCTION(String fUNCTION) {
		FUNCTION = fUNCTION;
	}
	public String getRANGE() {
		return RANGE;
	}
	public void setRANGE(String rANGE) {
		RANGE = rANGE;
	}
	public String getOCCUR() {
		return OCCUR;
	}
	public void setOCCUR(String oCCUR) {
		OCCUR = oCCUR;
	}
	public List<CCM7818OutputDetailsVO> getDetails() {
		return details;
	}
	public void setDetails(List<CCM7818OutputDetailsVO> details) {
		this.details = details;
	}
	
	
}