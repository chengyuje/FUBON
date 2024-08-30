package com.systex.jbranch.fubon.commons.esb.vo.wms032675;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;

/**
 * Created by SamTu on 2018/02/26.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class WMS032675OutputVO {
	@XmlElement
	private String CUST_VIP_NAME;	//功能碼
	@XmlElement
	private String EXT;	//範圍
	@XmlElement
	private String TEL_NO;	//FKey
	@XmlElement
	private String RES_COD;	//身分證字號
	@XmlElement
	private String RC_COD;	//FKey
	@XmlElement
	private String BDAY;	//列印編號
	@XmlElement
	private String VIP_EXP;	//FKey
	@XmlElement
	private String CUST_ADDR_1;	//FKey
	@XmlElement
	private String CNT;	//客戶統一編號
	@XmlElement
	private String EMAIL_ADDR;	//理財戶身份別
	@XmlElement
	private String ADV_COD;	//身分別說明
	@XmlElement
	private String CUST_NO;	//電話
	@XmlElement
	private String FILLER;	//分機
	@XmlElement
	private String FUNCTION;	//中文戶名
	@XmlElement
	private String CUST_TYPE;	//債清註記
	@XmlElement
	private String ORG_TYPE;	//地址
	@XmlElement
	private String OCCUR;	//帳號/事故別
	@XmlElement
	private String ACTION;	//生效日
	@XmlElement
	private String PROD_TYP;	//幣別
	@XmlElement
	private String IDU_COD;	//金額
	@XmlElement
	private String CUST_ADDR_2;	//法扣暨圈存金額
	@XmlElement
	private String CUST_NAME;	//戶況/通報內容
	@XmlElement
	private String CUST_ADDR;	//功能碼
	@XmlElement
	private String RANGE;	//範圍
	@XmlElement(name="TxRepeat")
	private List<WMS032675OutputDetailsVO> details;

	public String getCUST_VIP_NAME() {
		return CUST_VIP_NAME;
	}

	public String setCUST_VIP_NAME( String CUST_VIP_NAME) {
		 return this.CUST_VIP_NAME = CUST_VIP_NAME;
	}

	public String getEXT() {
		return EXT;
	}

	public String setEXT( String EXT) {
		 return this.EXT = EXT;
	}

	public String getTEL_NO() {
		return TEL_NO;
	}

	public String setTEL_NO( String TEL_NO) {
		 return this.TEL_NO = TEL_NO;
	}

	public String getRES_COD() {
		return RES_COD;
	}

	public String setRES_COD( String RES_COD) {
		 return this.RES_COD = RES_COD;
	}

	public String getRC_COD() {
		return RC_COD;
	}

	public String setRC_COD( String RC_COD) {
		 return this.RC_COD = RC_COD;
	}

	public String getBDAY() {
		return BDAY;
	}

	public String setBDAY( String BDAY) {
		 return this.BDAY = BDAY;
	}

	public String getVIP_EXP() {
		return VIP_EXP;
	}

	public String setVIP_EXP( String VIP_EXP) {
		 return this.VIP_EXP = VIP_EXP;
	}

	public String getCUST_ADDR_1() {
		return CUST_ADDR_1;
	}

	public String setCUST_ADDR_1( String CUST_ADDR_1) {
		 return this.CUST_ADDR_1 = CUST_ADDR_1;
	}

	public String getCNT() {
		return CNT;
	}

	public String setCNT( String CNT) {
		 return this.CNT = CNT;
	}

	public String getEMAIL_ADDR() {
		return EMAIL_ADDR;
	}

	public String setEMAIL_ADDR( String EMAIL_ADDR) {
		 return this.EMAIL_ADDR = EMAIL_ADDR;
	}

	public String getADV_COD() {
		return ADV_COD;
	}

	public String setADV_COD( String ADV_COD) {
		 return this.ADV_COD = ADV_COD;
	}

	public String getCUST_NO() {
		return CUST_NO;
	}

	public String setCUST_NO( String CUST_NO) {
		 return this.CUST_NO = CUST_NO;
	}

	public String getFILLER() {
		return FILLER;
	}

	public String setFILLER( String FILLER) {
		 return this.FILLER = FILLER;
	}

	public String getFUNCTION() {
		return FUNCTION;
	}

	public String setFUNCTION( String FUNCTION) {
		 return this.FUNCTION = FUNCTION;
	}

	public String getCUST_TYPE() {
		return CUST_TYPE;
	}

	public String setCUST_TYPE( String CUST_TYPE) {
		 return this.CUST_TYPE = CUST_TYPE;
	}

	public String getORG_TYPE() {
		return ORG_TYPE;
	}

	public String setORG_TYPE( String ORG_TYPE) {
		 return this.ORG_TYPE = ORG_TYPE;
	}

	public String getOCCUR() {
		return OCCUR;
	}

	public String setOCCUR( String OCCUR) {
		 return this.OCCUR = OCCUR;
	}

	public String getACTION() {
		return ACTION;
	}

	public String setACTION( String ACTION) {
		 return this.ACTION = ACTION;
	}

	public String getPROD_TYP() {
		return PROD_TYP;
	}

	public String setPROD_TYP( String PROD_TYP) {
		 return this.PROD_TYP = PROD_TYP;
	}

	public String getIDU_COD() {
		return IDU_COD;
	}

	public String setIDU_COD( String IDU_COD) {
		 return this.IDU_COD = IDU_COD;
	}

	public String getCUST_ADDR_2() {
		return CUST_ADDR_2;
	}

	public String setCUST_ADDR_2( String CUST_ADDR_2) {
		 return this.CUST_ADDR_2 = CUST_ADDR_2;
	}

	public String getCUST_NAME() {
		return CUST_NAME;
	}

	public String setCUST_NAME( String CUST_NAME) {
		 return this.CUST_NAME = CUST_NAME;
	}

	public String getCUST_ADDR() {
		return CUST_ADDR;
	}

	public String setCUST_ADDR( String CUST_ADDR) {
		 return this.CUST_ADDR = CUST_ADDR;
	}

	public String getRANGE() {
		return RANGE;
	}

	public String setRANGE( String RANGE) {
		 return this.RANGE = RANGE;
	}

	public List<WMS032675OutputDetailsVO> getDetails() {
		return details;
	}

	public List<WMS032675OutputDetailsVO> setDetails( List<WMS032675OutputDetailsVO> details) {
		 return this.details = details;
	}
}