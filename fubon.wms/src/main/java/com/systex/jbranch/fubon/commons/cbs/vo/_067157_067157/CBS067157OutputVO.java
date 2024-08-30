package com.systex.jbranch.fubon.commons.cbs.vo._067157_067157;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class CBS067157OutputVO {
    private String FILLER1; // FILLER1
    private String Option1; // 功能 C新增A修改D刪除E查詢
    private String FilDate1; // 問卷填寫日期 ddMMyyyy
    private String ExpiryDate1; // 有效截止日期 = 問卷填寫日期加一年 ddMMyyyy
    private String Source1; // 問卷填寫來源 網銀:09999 臨櫃: 00+分行別
    private String Result1; // 問卷測試結果 C1~C5
    private String ErrFlag1; // 問卷異常註記
    private String ErrFlagDate1; // 異常註記日期
    private String FILLER2; // FILLER2
    private String idno; // 證件類型/號碼
    private String idtype; // 證件類型/號碼

	public String getFILLER1() {
		return FILLER1;
	}

	public void setFILLER1(String FILLER1) {
		this.FILLER1 = FILLER1;
	}

	public String getOption1() {
		return Option1;
	}

	public void setOption1(String option1) {
		Option1 = option1;
	}

	public String getFilDate1() {
		return FilDate1;
	}

	public void setFilDate1(String filDate1) {
		FilDate1 = filDate1;
	}

	public String getExpiryDate1() {
		return ExpiryDate1;
	}

	public void setExpiryDate1(String expiryDate1) {
		ExpiryDate1 = expiryDate1;
	}

	public String getSource1() {
		return Source1;
	}

	public void setSource1(String source1) {
		Source1 = source1;
	}

	public String getResult1() {
		return Result1;
	}

	public void setResult1(String result1) {
		Result1 = result1;
	}

	public String getErrFlag1() {
		return ErrFlag1;
	}

	public void setErrFlag1(String errFlag1) {
		ErrFlag1 = errFlag1;
	}

	public String getErrFlagDate1() {
		return ErrFlagDate1;
	}

	public void setErrFlagDate1(String errFlagDate1) {
		ErrFlagDate1 = errFlagDate1;
	}

	public String getFILLER2() {
		return FILLER2;
	}

	public void setFILLER2(String FILLER2) {
		this.FILLER2 = FILLER2;
	}

	public String getIdno() {
		return idno;
	}

	public void setIdno(String idno) {
		this.idno = idno;
	}

	public String getIdtype() {
		return idtype;
	}

	public void setIdtype(String idtype) {
		this.idtype = idtype;
	}
}
