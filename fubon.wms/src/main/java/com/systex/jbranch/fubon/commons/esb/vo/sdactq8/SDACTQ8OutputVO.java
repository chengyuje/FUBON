package com.systex.jbranch.fubon.commons.esb.vo.sdactq8;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Created by CathyTang on 2018/09/05.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class SDACTQ8OutputVO {
    @XmlElement
	private String SDIVID;   	//身分證號
    @XmlElement
	private String SDRECO;  	//是否簽署推介同意書
    @XmlElement
	private String SDSDAY;  	//登錄日期
    @XmlElement
	private String SDEDAY;  	//有效日期
    @XmlElement
	private String SDCDAY;  	//終止日期
    @XmlElement
	private String SDSTATUS;  	//狀態註記

    public String getSDIVID() {
		return SDIVID;
	}

	public void setSDIVID(String sDIVID) {
		SDIVID = sDIVID;
	}

	public String getSDRECO() {
		return SDRECO;
	}

	public void setSDRECO(String sDRECO) {
		SDRECO = sDRECO;
	}

	public String getSDSDAY() {
		return SDSDAY;
	}

	public void setSDSDAY(String sDSDAY) {
		SDSDAY = sDSDAY;
	}

	public String getSDEDAY() {
		return SDEDAY;
	}

	public void setSDEDAY(String sDEDAY) {
		SDEDAY = sDEDAY;
	}

	public String getSDCDAY() {
		return SDCDAY;
	}

	public void setSDCDAY(String sDCDAY) {
		SDCDAY = sDCDAY;
	}

	public String getSDSTATUS() {
		return SDSTATUS;
	}

	public void setSDSTATUS(String sDSTATUS) {
		SDSTATUS = sDSTATUS;
	}

	@Override
    public String toString() {
        return "SDACT8OutputVO{" +
                "SDIVID='" + SDIVID + '\'' +
                ", SDRECO='" + SDRECO + '\'' +
                ", SDSDAY='" + SDSDAY + '\'' +
                ", SDEDAY='" + SDEDAY + '\'' +
                ", SDCDAY='" + SDCDAY + '\'' +
                ", SDSTATUS='" + SDSTATUS + '\'' +
                '}';
    }
}
