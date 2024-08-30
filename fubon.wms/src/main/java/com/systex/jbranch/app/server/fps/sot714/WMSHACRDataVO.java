package com.systex.jbranch.app.server.fps.sot714;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlElement;

/**
 * Created 2023/04/26
 * 高風險商品承作系統發查微服務，取得集中度資訊
 */

public class WMSHACRDataVO {
	private BigDecimal DENO_AMT = BigDecimal.ZERO; //分母總金額(折台)
	
	private BigDecimal BASE_PERC_1 = BigDecimal.ZERO; //（有不保本註記之SI、SN、DCI）限額基準百分比
	
	private BigDecimal PERCENTAGE_1 = BigDecimal.ZERO; //（有不保本註記之SI、SN、DCI）計算出的百分比
	
	private BigDecimal AMT_1 = BigDecimal.ZERO; //（有不保本註記之SI、SN、DCI）不保本註記之SI、SN、DCI分子金額，不包含當下申購金額(折台)
	
	private BigDecimal BASE_PERC_2 = BigDecimal.ZERO; //（有僅限高資產客戶申購註記之海外債券）限額基準百分比
	
	private BigDecimal PERCENTAGE_2 = BigDecimal.ZERO; //（有僅限高資產客戶申購註記之海外債券）計算出的百分比
	
	private BigDecimal AMT_2 = BigDecimal.ZERO; //（有僅限高資產客戶申購註記之海外債券）不保本註記之SI、SN、DCI分子金額，不包含當下申購金額(折台)
	
	private BigDecimal BASE_PERC_3 = BigDecimal.ZERO; //（未具證投信基金性質境外基金）限額基準百分比
	
	private BigDecimal PERCENTAGE_3 = BigDecimal.ZERO; //（未具證投信基金性質境外基金）計算出的百分比
	
	private BigDecimal AMT_3 = BigDecimal.ZERO; //（未具證投信基金性質境外基金）不保本註記之SI、SN、DCI分子金額，不包含當下申購金額(折台)
	
	private BigDecimal BASE_PERC_4 = BigDecimal.ZERO; //（加總）限額基準百分比
	
	private BigDecimal PERCENTAGE_4 = BigDecimal.ZERO; //（加總）計算出的百分比
	
	private BigDecimal AMT_4 = BigDecimal.ZERO; //（加總）不保本註記之SI、SN、DCI分子金額，不包含當下申購金額(折台)
	
	private BigDecimal LIMIT_PERC_2 = BigDecimal.ZERO; //（有僅限高資產客戶申購註記之海外債券）上限比例基準百分比
	
	private BigDecimal LIMIT_PERC_3 = BigDecimal.ZERO; //（未具證投信基金性質境外基金）上限比例基準百分比
		
	private String VALIDATE_YN; //Y:可交易 W:超過通知門檻 N:不可交易

	
	public BigDecimal getDENO_AMT() {
		return DENO_AMT;
	}

	public void setDENO_AMT(BigDecimal dENO_AMT) {
		DENO_AMT = dENO_AMT;
	}

	public BigDecimal getBASE_PERC_1() {
		return BASE_PERC_1;
	}

	public void setBASE_PERC_1(BigDecimal bASE_PERC_1) {
		BASE_PERC_1 = bASE_PERC_1;
	}

	public BigDecimal getPERCENTAGE_1() {
		return PERCENTAGE_1;
	}

	public void setPERCENTAGE_1(BigDecimal pERCENTAGE_1) {
		PERCENTAGE_1 = pERCENTAGE_1;
	}

	public BigDecimal getAMT_1() {
		return AMT_1;
	}

	public void setAMT_1(BigDecimal aMT_1) {
		AMT_1 = aMT_1;
	}

	public BigDecimal getBASE_PERC_2() {
		return BASE_PERC_2;
	}

	public void setBASE_PERC_2(BigDecimal bASE_PERC_2) {
		BASE_PERC_2 = bASE_PERC_2;
	}

	public BigDecimal getPERCENTAGE_2() {
		return PERCENTAGE_2;
	}

	public void setPERCENTAGE_2(BigDecimal pERCENTAGE_2) {
		PERCENTAGE_2 = pERCENTAGE_2;
	}

	public BigDecimal getAMT_2() {
		return AMT_2;
	}

	public void setAMT_2(BigDecimal aMT_2) {
		AMT_2 = aMT_2;
	}

	public BigDecimal getBASE_PERC_3() {
		return BASE_PERC_3;
	}

	public void setBASE_PERC_3(BigDecimal bASE_PERC_3) {
		BASE_PERC_3 = bASE_PERC_3;
	}

	public BigDecimal getPERCENTAGE_3() {
		return PERCENTAGE_3;
	}

	public void setPERCENTAGE_3(BigDecimal pERCENTAGE_3) {
		PERCENTAGE_3 = pERCENTAGE_3;
	}

	public BigDecimal getAMT_3() {
		return AMT_3;
	}

	public void setAMT_3(BigDecimal aMT_3) {
		AMT_3 = aMT_3;
	}

	public BigDecimal getBASE_PERC_4() {
		return BASE_PERC_4;
	}

	public void setBASE_PERC_4(BigDecimal bASE_PERC_4) {
		BASE_PERC_4 = bASE_PERC_4;
	}

	public BigDecimal getPERCENTAGE_4() {
		return PERCENTAGE_4;
	}

	public void setPERCENTAGE_4(BigDecimal pERCENTAGE_4) {
		PERCENTAGE_4 = pERCENTAGE_4;
	}

	public BigDecimal getAMT_4() {
		return AMT_4;
	}

	public void setAMT_4(BigDecimal aMT_4) {
		AMT_4 = aMT_4;
	}

	public BigDecimal getLIMIT_PERC_2() {
		return LIMIT_PERC_2;
	}

	public void setLIMIT_PERC_2(BigDecimal lIMIT_PERC_2) {
		LIMIT_PERC_2 = lIMIT_PERC_2;
	}

	public BigDecimal getLIMIT_PERC_3() {
		return LIMIT_PERC_3;
	}

	public void setLIMIT_PERC_3(BigDecimal lIMIT_PERC_3) {
		LIMIT_PERC_3 = lIMIT_PERC_3;
	}

	public String getVALIDATE_YN() {
		return VALIDATE_YN;
	}

	public void setVALIDATE_YN(String vALIDATE_YN) {
		VALIDATE_YN = vALIDATE_YN;
	}
	
	
}