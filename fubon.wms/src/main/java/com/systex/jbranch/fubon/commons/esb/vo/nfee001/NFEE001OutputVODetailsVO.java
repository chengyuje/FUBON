package com.systex.jbranch.fubon.commons.esb.vo.nfee001;

import java.math.BigDecimal;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by SebastianWu on 2016/9/9.
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlType
public class NFEE001OutputVODetailsVO {
 
    @XmlElement
    private String N0101;		//有效期間起
    @XmlElement
    private String N0102;		//有效期間迄
    @XmlElement
    private String N0103;		//臨櫃單筆
    @XmlElement
    private String N0104;		//臨櫃小額
    @XmlElement
    private String N0105;		//網銀單筆
    @XmlElement
    private String N0106;		//網銀小額
    @XmlElement
    private String N0107;		//優惠次數
    @XmlElement
    private String N0108;		//已使用次數
    @XmlElement
    private String N0109;		//團體名稱
	public String getN0101() {
		return N0101;
	}
	public void setN0101(String n0101) {
		N0101 = n0101;
	}
	public String getN0102() {
		return N0102;
	}
	public void setN0102(String n0102) {
		N0102 = n0102;
	}
	public String getN0103() {
		return N0103;
	}
	public void setN0103(String n0103) {
		N0103 = n0103;
	}
	public String getN0104() {
		return N0104;
	}
	public void setN0104(String n0104) {
		N0104 = n0104;
	}
	public String getN0105() {
		return N0105;
	}
	public void setN0105(String n0105) {
		N0105 = n0105;
	}
	public String getN0106() {
		return N0106;
	}
	public void setN0106(String n0106) {
		N0106 = n0106;
	}
	public String getN0107() {
		return N0107;
	}
	public void setN0107(String n0107) {
		N0107 = n0107;
	}
	public String getN0108() {
		return N0108;
	}
	public void setN0108(String n0108) {
		N0108 = n0108;
	}
	public String getN0109() {
		return N0109;
	}
	public void setN0109(String n0109) {
		N0109 = n0109;
	}
   
	
	
}
