package com.systex.jbranch.fubon.commons.esb.vo.gd320140;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * 
 * @author sam
 * @date 2018/01/19
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
@Component
@Scope("request")
public class GD320140OutputVO {
	
	@XmlElement	// 端末格式
	private String STYLE;
	
	
	public String getSTYLE() {
		return STYLE;
	}
	public void setSTYLE(String sTYLE) {
		STYLE = sTYLE;
	}
	@XmlElement(name="TxRepeat")
	private List<GD320140OutputDetailsVO> details;
	
	public List<GD320140OutputDetailsVO> getDetails() {
		return details;
	}
	public void setDetails(List<GD320140OutputDetailsVO> details) {
		this.details = details;
	}
	
	
}