package com.systex.jbranch.app.common.fps.table;

import java.sql.Timestamp;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.systex.jbranch.platform.common.dataaccess.vo.VOBase;

;

/** @Author : SystexDBTool CodeGenerator By LeoLin */
public class TBSOT_TRADE_MAINVO extends VOBase {

	/** identifier field */
	private String TRADE_SEQ;

	/** persistent field */
	private String PROD_TYPE;

	/** persistent field */
	private String TRADE_TYPE;

	/** persistent field */
	private String CUST_ID;

	/** nullable persistent field */
	private String CUST_NAME;

	/** nullable persistent field */
	private String AGENT_ID;

	/** nullable persistent field */
	private String AGENT_NAME;

	/** nullable persistent field */
	private String KYC_LV;

	/** nullable persistent field */
	private Timestamp KYC_DUE_DATE;

	/** persistent field */
	private String PROF_INVESTOR_YN;

	/** nullable persistent field */
	private Timestamp PI_DUE_DATE;

	/** nullable persistent field */
	private String CUST_REMARKS;

	/** nullable persistent field */
	private String IS_OBU;

	/** nullable persistent field */
	private String IS_AGREE_PROD_ADV;

	/** nullable persistent field */
	private Timestamp BARGAIN_DUE_DATE;

	/** nullable persistent field */
	private String TRADE_STATUS;

	/** nullable persistent field */
	private String IS_BARGAIN_NEEDED;

	/** nullable persistent field */
	private String BARGAIN_FEE_FLAG;

	/** persistent field */
	private String IS_REC_NEEDED;

	/** nullable persistent field */
	private String REC_SEQ;

	/** nullable persistent field */
	private Timestamp SEND_DATE;

	/** nullable persistent field */
	private String PROF_INVESTOR_TYPE;

	/** nullable persistent field */
	private String PI_REMARK;

	/** persistent field */
	private String BRANCH_NBR;
	
	private String TRUST_TRADE_TYPE;
	
	/** nullable persistent field */
	private String GUARDIANSHIP_FLAG;
	
	/** nullable persistent field */
	private String IS_WEB;
	
	/** nullable persistent field */
	private String HNWC_YN;
	
	/** nullable persistent field */
	private String HNWC_SERVICE_YN;
	
	/** nullable persistent field */
	private String FLAG_NUMBER;

	public static final String TABLE_UID = "com.systex.jbranch.app.common.fps.table.TBSOT_TRADE_MAIN";

	public String getTableuid() {
		return TABLE_UID;
	}

	/** full constructor */
	public TBSOT_TRADE_MAINVO(String TRADE_SEQ, String PROD_TYPE, String TRADE_TYPE, String CUST_ID, String CUST_NAME, String AGENT_ID, String AGENT_NAME, String KYC_LV, Timestamp KYC_DUE_DATE, String PROF_INVESTOR_YN, Timestamp PI_DUE_DATE, String CUST_REMARKS, String IS_OBU, String IS_AGREE_PROD_ADV, Timestamp BARGAIN_DUE_DATE, String TRADE_STATUS, String IS_BARGAIN_NEEDED, String BARGAIN_FEE_FLAG, String IS_REC_NEEDED, String REC_SEQ, Timestamp SEND_DATE, Timestamp createtime, String creator, String modifier, Timestamp lastupdate, String PROF_INVESTOR_TYPE, String PI_REMARK, String BRANCH_NBR, Long version, String TRUST_TRADE_TYPE, String GUARDIANSHIP_FLAG, String IS_WEB, String HNWC_YN) {
		this.TRADE_SEQ = TRADE_SEQ;
		this.PROD_TYPE = PROD_TYPE;
		this.TRADE_TYPE = TRADE_TYPE;
		this.CUST_ID = CUST_ID;
		this.CUST_NAME = CUST_NAME;
		this.AGENT_ID = AGENT_ID;
		this.AGENT_NAME = AGENT_NAME;
		this.KYC_LV = KYC_LV;
		this.KYC_DUE_DATE = KYC_DUE_DATE;
		this.PROF_INVESTOR_YN = PROF_INVESTOR_YN;
		this.PI_DUE_DATE = PI_DUE_DATE;
		this.CUST_REMARKS = CUST_REMARKS;
		this.IS_OBU = IS_OBU;
		this.IS_AGREE_PROD_ADV = IS_AGREE_PROD_ADV;
		this.BARGAIN_DUE_DATE = BARGAIN_DUE_DATE;
		this.TRADE_STATUS = TRADE_STATUS;
		this.IS_BARGAIN_NEEDED = IS_BARGAIN_NEEDED;
		this.BARGAIN_FEE_FLAG = BARGAIN_FEE_FLAG;
		this.IS_REC_NEEDED = IS_REC_NEEDED;
		this.REC_SEQ = REC_SEQ;
		this.SEND_DATE = SEND_DATE;
		this.createtime = createtime;
		this.creator = creator;
		this.modifier = modifier;
		this.lastupdate = lastupdate;
		this.PROF_INVESTOR_TYPE = PROF_INVESTOR_TYPE;
		this.PI_REMARK = PI_REMARK;
		this.BRANCH_NBR = BRANCH_NBR;
		this.version = version;
		this.TRUST_TRADE_TYPE = TRUST_TRADE_TYPE;
		this.GUARDIANSHIP_FLAG = GUARDIANSHIP_FLAG;
		this.IS_WEB = IS_WEB;
		this.HNWC_YN = HNWC_YN;
	}

	/** default constructor */
	public TBSOT_TRADE_MAINVO() {
	}

	public String getTRUST_TRADE_TYPE() {
		return TRUST_TRADE_TYPE;
	}

	public void setTRUST_TRADE_TYPE(String tRUST_TRADE_TYPE) {
		TRUST_TRADE_TYPE = tRUST_TRADE_TYPE;
	}
	
	

	public String getGUARDIANSHIP_FLAG() {
		return GUARDIANSHIP_FLAG;
	}

	public void setGUARDIANSHIP_FLAG(String gUARDIANSHIP_FLAG) {
		GUARDIANSHIP_FLAG = gUARDIANSHIP_FLAG;
	}

	public static String getTableUid() {
		return TABLE_UID;
	}

	/** minimal constructor */
	public TBSOT_TRADE_MAINVO(String TRADE_SEQ, String PROD_TYPE, String TRADE_TYPE, String CUST_ID, String PROF_INVESTOR_YN, String IS_REC_NEEDED, String BRANCH_NBR, String TRUST_TRADE_TYPE) {
		this.TRADE_SEQ = TRADE_SEQ;
		this.PROD_TYPE = PROD_TYPE;
		this.TRADE_TYPE = TRADE_TYPE;
		this.CUST_ID = CUST_ID;
		this.PROF_INVESTOR_YN = PROF_INVESTOR_YN;
		this.IS_REC_NEEDED = IS_REC_NEEDED;
		this.BRANCH_NBR = BRANCH_NBR;
		this.TRUST_TRADE_TYPE = TRUST_TRADE_TYPE;
	}

	public String getTRADE_SEQ() {
		return this.TRADE_SEQ;
	}

	public void setTRADE_SEQ(String TRADE_SEQ) {
		this.TRADE_SEQ = TRADE_SEQ;
	}

	public String getPROD_TYPE() {
		return this.PROD_TYPE;
	}

	public void setPROD_TYPE(String PROD_TYPE) {
		this.PROD_TYPE = PROD_TYPE;
	}

	public String getTRADE_TYPE() {
		return this.TRADE_TYPE;
	}

	public void setTRADE_TYPE(String TRADE_TYPE) {
		this.TRADE_TYPE = TRADE_TYPE;
	}

	public String getCUST_ID() {
		return this.CUST_ID;
	}

	public void setCUST_ID(String CUST_ID) {
		this.CUST_ID = CUST_ID;
	}

	public String getCUST_NAME() {
		return this.CUST_NAME;
	}

	public void setCUST_NAME(String CUST_NAME) {
		this.CUST_NAME = CUST_NAME;
	}

	public String getAGENT_ID() {
		return this.AGENT_ID;
	}

	public void setAGENT_ID(String AGENT_ID) {
		this.AGENT_ID = AGENT_ID;
	}

	public String getAGENT_NAME() {
		return this.AGENT_NAME;
	}

	public void setAGENT_NAME(String AGENT_NAME) {
		this.AGENT_NAME = AGENT_NAME;
	}

	public String getKYC_LV() {
		return this.KYC_LV;
	}

	public void setKYC_LV(String KYC_LV) {
		this.KYC_LV = KYC_LV;
	}

	public Timestamp getKYC_DUE_DATE() {
		return this.KYC_DUE_DATE;
	}

	public void setKYC_DUE_DATE(Timestamp KYC_DUE_DATE) {
		this.KYC_DUE_DATE = KYC_DUE_DATE;
	}

	public String getPROF_INVESTOR_YN() {
		return this.PROF_INVESTOR_YN;
	}

	public void setPROF_INVESTOR_YN(String PROF_INVESTOR_YN) {
		this.PROF_INVESTOR_YN = PROF_INVESTOR_YN;
	}

	public Timestamp getPI_DUE_DATE() {
		return this.PI_DUE_DATE;
	}

	public void setPI_DUE_DATE(Timestamp PI_DUE_DATE) {
		this.PI_DUE_DATE = PI_DUE_DATE;
	}

	public String getCUST_REMARKS() {
		return this.CUST_REMARKS;
	}

	public void setCUST_REMARKS(String CUST_REMARKS) {
		this.CUST_REMARKS = CUST_REMARKS;
	}

	public String getIS_OBU() {
		return this.IS_OBU;
	}

	public void setIS_OBU(String IS_OBU) {
		this.IS_OBU = IS_OBU;
	}

	public String getIS_AGREE_PROD_ADV() {
		return this.IS_AGREE_PROD_ADV;
	}

	public void setIS_AGREE_PROD_ADV(String IS_AGREE_PROD_ADV) {
		this.IS_AGREE_PROD_ADV = IS_AGREE_PROD_ADV;
	}

	public Timestamp getBARGAIN_DUE_DATE() {
		return this.BARGAIN_DUE_DATE;
	}

	public void setBARGAIN_DUE_DATE(Timestamp BARGAIN_DUE_DATE) {
		this.BARGAIN_DUE_DATE = BARGAIN_DUE_DATE;
	}

	public String getTRADE_STATUS() {
		return this.TRADE_STATUS;
	}

	public void setTRADE_STATUS(String TRADE_STATUS) {
		this.TRADE_STATUS = TRADE_STATUS;
	}

	public String getIS_BARGAIN_NEEDED() {
		return this.IS_BARGAIN_NEEDED;
	}

	public void setIS_BARGAIN_NEEDED(String IS_BARGAIN_NEEDED) {
		this.IS_BARGAIN_NEEDED = IS_BARGAIN_NEEDED;
	}

	public String getBARGAIN_FEE_FLAG() {
		return this.BARGAIN_FEE_FLAG;
	}

	public void setBARGAIN_FEE_FLAG(String BARGAIN_FEE_FLAG) {
		this.BARGAIN_FEE_FLAG = BARGAIN_FEE_FLAG;
	}

	public String getIS_REC_NEEDED() {
		return this.IS_REC_NEEDED;
	}

	public void setIS_REC_NEEDED(String IS_REC_NEEDED) {
		this.IS_REC_NEEDED = IS_REC_NEEDED;
	}

	public String getREC_SEQ() {
		return this.REC_SEQ;
	}

	public void setREC_SEQ(String REC_SEQ) {
		this.REC_SEQ = REC_SEQ;
	}

	public Timestamp getSEND_DATE() {
		return this.SEND_DATE;
	}

	public void setSEND_DATE(Timestamp SEND_DATE) {
		this.SEND_DATE = SEND_DATE;
	}

	public String getPROF_INVESTOR_TYPE() {
		return this.PROF_INVESTOR_TYPE;
	}

	public void setPROF_INVESTOR_TYPE(String PROF_INVESTOR_TYPE) {
		this.PROF_INVESTOR_TYPE = PROF_INVESTOR_TYPE;
	}

	public String getPI_REMARK() {
		return this.PI_REMARK;
	}

	public void setPI_REMARK(String PI_REMARK) {
		this.PI_REMARK = PI_REMARK;
	}

	public String getBRANCH_NBR() {
		return this.BRANCH_NBR;
	}

	public void setBRANCH_NBR(String BRANCH_NBR) {
		this.BRANCH_NBR = BRANCH_NBR;
	}

	public String getIS_WEB() {
		return IS_WEB;
	}

	public void setIS_WEB(String iS_WEB) {
		IS_WEB = iS_WEB;
	}

	public String getHNWC_YN() {
		return HNWC_YN;
	}

	public void setHNWC_YN(String hNWC_YN) {
		HNWC_YN = hNWC_YN;
	}

	public String getHNWC_SERVICE_YN() {
		return HNWC_SERVICE_YN;
	}

	public void setHNWC_SERVICE_YN(String hNWC_SERVICE_YN) {
		HNWC_SERVICE_YN = hNWC_SERVICE_YN;
	}

	public String getFLAG_NUMBER() {
		return FLAG_NUMBER;
	}

	public void setFLAG_NUMBER(String fLAG_NUMBER) {
		FLAG_NUMBER = fLAG_NUMBER;
	}

	public void checkDefaultValue() {
	}

	public String toString() {
		return new ToStringBuilder(this).append("TRADE_SEQ", getTRADE_SEQ()).toString();
	}

}
