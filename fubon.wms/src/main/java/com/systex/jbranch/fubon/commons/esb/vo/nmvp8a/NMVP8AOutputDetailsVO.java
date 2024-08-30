package com.systex.jbranch.fubon.commons.esb.vo.nmvp8a;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;

import org.apache.commons.lang.StringUtils;

@XmlAccessorType(XmlAccessType.NONE)
@XmlType
public class NMVP8AOutputDetailsVO implements Comparable<NMVP8AOutputDetailsVO> {

	@XmlElement
	private String REL_TYPE;	//信託關係

	@XmlElement
	private String REL_ID;		//關係人ID

	@XmlElement
	private String REL_NAME;	//關係人姓名

	@XmlElement
	private String INS_TYPE2;	//指示方式 A:單一人指示 B:任一人指示 C:任二人指示 D:全體共同指示


	public String getREL_TYPE() {
		return REL_TYPE;
	}

	public void setREL_TYPE(String rEL_TYPE) {
		REL_TYPE = rEL_TYPE;
	}

	public String getREL_ID() {
		return REL_ID;
	}

	public void setREL_ID(String rEL_ID) {
		REL_ID = rEL_ID;
	}

	public String getREL_NAME() {
		return REL_NAME;
	}

	public void setREL_NAME(String rEL_NAME) {
		REL_NAME = rEL_NAME;
	}

	public String getINS_TYPE2() {
		return INS_TYPE2;
	}

	public void setINS_TYPE2(String iNS_TYPE2) {
		INS_TYPE2 = iNS_TYPE2;
	}

	/***
	 * 先比較信託關係再比較指示方式 
	 */
	@Override
	public int compareTo(NMVP8AOutputDetailsVO o) {
		if(StringUtils.isNotBlank(this.getREL_TYPE()) && StringUtils.isNotBlank(o.getREL_TYPE())) {
			if((this.getREL_TYPE().charAt(0) - o.getREL_TYPE().charAt(0)) == 0) {
				if(StringUtils.isNotBlank(this.getINS_TYPE2()) && StringUtils.isNotBlank(o.getINS_TYPE2())) {
					return (this.getINS_TYPE2().charAt(0) - o.getINS_TYPE2().charAt(0));
				} else {
					return 0;
				}
			} else {
				return (this.getREL_TYPE().charAt(0) - o.getREL_TYPE().charAt(0));
			}
		}
		return 0;
	}
	
	
}