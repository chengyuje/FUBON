package com.systex.jbranch.fubon.commons.esb.vo.wms032675;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;

/**
 * Created by SamTu on 2018/02/26.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class WMS032675InputVO {
	@XmlElement
	private String PRINT_NO;	//功能碼
	@XmlElement
	private String CUSID;	//範圍
	@XmlElement
	private String FKey3;	//FKey
	@XmlElement
	private String FKey4;	//身分證字號
	@XmlElement
	private String FKey1;	//FKey
	@XmlElement
	private String FKey2;	//列印編號
	@XmlElement
	private String FUNCTION;	//FKey
	@XmlElement
	private String RANGE;	//FKey
	
	// Mark: 2016/12/12 新增FUNC_COD  by Stella
	@XmlElement
	private String FUNC_COD;	//FUNC_COD
	// FUNC_COD end 
	
	@XmlElement
	private String IDTYPE; //證件類型
	@XmlElement
	private String ACTNO; //帳號
	@XmlElement
	private String BRANCH; //分行代號
	
	
	
	public String getACTNO() {
		return ACTNO;
	}

	public void setACTNO(String aCTNO) {
		ACTNO = aCTNO;
	}

	public String getBRANCH() {
		return BRANCH;
	}

	public void setBRANCH(String bRANCH) {
		BRANCH = bRANCH;
	}

	public String getIDTYPE() {
		return IDTYPE;
	}

	public void setIDTYPE(String iDTYPE) {
		IDTYPE = iDTYPE;
	}

	public String getPRINT_NO() {
		return PRINT_NO;
	}

	public String setPRINT_NO( String PRINT_NO) {
		 return this.PRINT_NO = PRINT_NO;
	}

	public String getCUSID() {
		return CUSID;
	}

	public String setCUSID( String CUSID) {
		 return this.CUSID = CUSID;
	}

	public String getFKey3() {
		return FKey3;
	}

	public String setFKey3( String FKey3) {
		 return this.FKey3 = FKey3;
	}

	public String getFKey4() {
		return FKey4;
	}

	public String setFKey4( String FKey4) {
		 return this.FKey4 = FKey4;
	}

	public String getFKey1() {
		return FKey1;
	}

	public String setFKey1( String FKey1) {
		 return this.FKey1 = FKey1;
	}

	public String getFKey2() {
		return FKey2;
	}

	public String setFKey2( String FKey2) {
		 return this.FKey2 = FKey2;
	}

	public String getFUNCTION() {
		return FUNCTION;
	}

	public String setFUNCTION( String FUNCTION) {
		 return this.FUNCTION = FUNCTION;
	}

	public String getRANGE() {
		return RANGE;
	}

	public String setRANGE( String RANGE) {
		 return this.RANGE = RANGE;
	}

	public String getFUNC_COD() {
		return FUNC_COD;
	}

	public void setFUNC_COD(String fUNC_COD) {
		FUNC_COD = fUNC_COD;
	}
	
	
}