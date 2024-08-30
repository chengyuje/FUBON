package com.systex.jbranch.fubon.commons.esb.vo.wmshacr;

import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;

/**
 * Created 2023/04/26
 * 高風險商品承作系統發查微服務，取得集中度資訊
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class WMSHACROutputVO {
	@XmlElement
	private String CUST_ID; //客戶統編
	@XmlElement
	private String PROD_TYPE; //1: SI 2: SN 3: DCI 4: 特金海外債 5: 金市海外債 6: 境外私募基金
	@XmlElement
	private String DENO_AMT; //分母總金額(折台)
	@XmlElement
	private String BASE_PERC_1; //（有不保本註記之SI、SN、DCI）限額基準百分比
	@XmlElement
	private String PERCENTAGE_1; //（有不保本註記之SI、SN、DCI）計算出的百分比
	@XmlElement
	private String AMT_1; //（有不保本註記之SI、SN、DCI）不保本註記之SI、SN、DCI分子金額，不包含當下申購金額(折台)
	@XmlElement
	private String BASE_PERC_2; //（有僅限高資產客戶申購註記之海外債券）限額基準百分比
	@XmlElement
	private String PERCENTAGE_2; //（有僅限高資產客戶申購註記之海外債券）計算出的百分比
	@XmlElement
	private String AMT_2; //（有僅限高資產客戶申購註記之海外債券）不保本註記之SI、SN、DCI分子金額，不包含當下申購金額(折台)
	@XmlElement
	private String BASE_PERC_3; //（未具證投信基金性質境外基金）限額基準百分比
	@XmlElement
	private String PERCENTAGE_3; //（未具證投信基金性質境外基金）計算出的百分比
	@XmlElement
	private String AMT_3; //（未具證投信基金性質境外基金）不保本註記之SI、SN、DCI分子金額，不包含當下申購金額(折台)
	@XmlElement
	private String BASE_PERC_4; //（加總）限額基準百分比
	@XmlElement
	private String PERCENTAGE_4; //（加總）計算出的百分比
	@XmlElement
	private String AMT_4; //（加總）不保本註記之SI、SN、DCI分子金額，不包含當下申購金額(折台)
	@XmlElement
	private String VALIDATE_YN; //Y:可交易 W:超過通知門檻 N:不可交易
	@XmlElement
	private String LIMIT_PERC_2; //（有僅限高資產客戶申購註記之海外債券）上限比例基準百分比
	@XmlElement
	private String LIMIT_PERC_3; //（未具證投信基金性質境外基金）上限比例基準百分比
	@XmlElement
	private String EMSGID; //錯誤代碼
	@XmlElement
	private String EMSGTXT; //錯誤訊息
	
	
	public String getCUST_ID() {
		return CUST_ID;
	}
	public void setCUST_ID(String cUST_ID) {
		CUST_ID = cUST_ID;
	}
	public String getPROD_TYPE() {
		return PROD_TYPE;
	}
	public void setPROD_TYPE(String pROD_TYPE) {
		PROD_TYPE = pROD_TYPE;
	}
	public String getDENO_AMT() {
		return DENO_AMT;
	}
	public void setDENO_AMT(String dENO_AMT) {
		DENO_AMT = dENO_AMT;
	}
	public String getBASE_PERC_1() {
		return BASE_PERC_1;
	}
	public void setBASE_PERC_1(String bASE_PERC_1) {
		BASE_PERC_1 = bASE_PERC_1;
	}
	public String getPERCENTAGE_1() {
		return PERCENTAGE_1;
	}
	public void setPERCENTAGE_1(String pERCENTAGE_1) {
		PERCENTAGE_1 = pERCENTAGE_1;
	}
	public String getAMT_1() {
		return AMT_1;
	}
	public void setAMT_1(String aMT_1) {
		AMT_1 = aMT_1;
	}
	public String getBASE_PERC_2() {
		return BASE_PERC_2;
	}
	public void setBASE_PERC_2(String bASE_PERC_2) {
		BASE_PERC_2 = bASE_PERC_2;
	}
	public String getPERCENTAGE_2() {
		return PERCENTAGE_2;
	}
	public void setPERCENTAGE_2(String pERCENTAGE_2) {
		PERCENTAGE_2 = pERCENTAGE_2;
	}
	public String getAMT_2() {
		return AMT_2;
	}
	public void setAMT_2(String aMT_2) {
		AMT_2 = aMT_2;
	}
	public String getBASE_PERC_3() {
		return BASE_PERC_3;
	}
	public void setBASE_PERC_3(String bASE_PERC_3) {
		BASE_PERC_3 = bASE_PERC_3;
	}
	public String getPERCENTAGE_3() {
		return PERCENTAGE_3;
	}
	public void setPERCENTAGE_3(String pERCENTAGE_3) {
		PERCENTAGE_3 = pERCENTAGE_3;
	}
	public String getAMT_3() {
		return AMT_3;
	}
	public void setAMT_3(String aMT_3) {
		AMT_3 = aMT_3;
	}
	public String getBASE_PERC_4() {
		return BASE_PERC_4;
	}
	public void setBASE_PERC_4(String bASE_PERC_4) {
		BASE_PERC_4 = bASE_PERC_4;
	}
	public String getPERCENTAGE_4() {
		return PERCENTAGE_4;
	}
	public void setPERCENTAGE_4(String pERCENTAGE_4) {
		PERCENTAGE_4 = pERCENTAGE_4;
	}
	public String getAMT_4() {
		return AMT_4;
	}
	public void setAMT_4(String aMT_4) {
		AMT_4 = aMT_4;
	}
	public String getVALIDATE_YN() {
		return VALIDATE_YN;
	}
	public void setVALIDATE_YN(String vALIDATE_YN) {
		VALIDATE_YN = vALIDATE_YN;
	}
	public String getEMSGID() {
		return EMSGID;
	}
	public void setEMSGID(String eMSGID) {
		EMSGID = eMSGID;
	}
	public String getEMSGTXT() {
		return EMSGTXT;
	}
	public void setEMSGTXT(String eMSGTXT) {
		EMSGTXT = eMSGTXT;
	}
	public String getLIMIT_PERC_2() {
		return LIMIT_PERC_2;
	}
	public void setLIMIT_PERC_2(String lIMIT_PERC_2) {
		LIMIT_PERC_2 = lIMIT_PERC_2;
	}
	public String getLIMIT_PERC_3() {
		return LIMIT_PERC_3;
	}
	public void setLIMIT_PERC_3(String lIMIT_PERC_3) {
		LIMIT_PERC_3 = lIMIT_PERC_3;
	}
	
}