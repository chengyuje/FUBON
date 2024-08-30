package com.systex.jbranch.fubon.commons.esb.vo.wms032275;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;

/**
 * Created by SebastianWu on 2016/09/09.
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType
public class WMS032275OutputDetailsVO {
	@XmlElement
	private String SIG_DATE;	//功能
	@XmlElement
	private String DOC_NO;	//FKey
	@XmlElement
	private String EFF_DATE;	//統一編號

	public String getSIG_DATE() {
		return SIG_DATE;
	}

	public String setSIG_DATE( String SIG_DATE) {
		 return this.SIG_DATE = SIG_DATE;
	}

	public String getDOC_NO() {
		return DOC_NO;
	}

	public String setDOC_NO( String DOC_NO) {
		 return this.DOC_NO = DOC_NO;
	}

	public String getEFF_DATE() {
		return EFF_DATE;
	}

	public String setEFF_DATE( String EFF_DATE) {
		 return this.EFF_DATE = EFF_DATE;
	}
}