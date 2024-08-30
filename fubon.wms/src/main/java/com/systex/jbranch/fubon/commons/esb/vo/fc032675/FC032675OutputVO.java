package com.systex.jbranch.fubon.commons.esb.vo.fc032675;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlElement;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType
public class FC032675OutputVO {
	@XmlElement
	private String ACTION;
	@XmlElement
	private String CNT;
	@XmlElement
	private String RC_COD;
	@XmlElement
	private String SRC_TYP;
	@XmlElement
	private String MTN_DATE;
	@XmlElement
	private String TX_FLG;
	@XmlElement
	private String DESC;
	@XmlElement
	private String PROD_TYP;
	@XmlElement
	private String DEAD_TYP;
	@XmlElement
	private String REJ_TYP;
	@XmlElement
	private String DM_FLG;
	@XmlElement
	private String EDM_FLG;
	@XmlElement
	private String SMS_FLG;
	@XmlElement
	private String TM_FLG;
	@XmlElement
	private String INFO_FLG;
	@XmlElement
	private String ACC1_FLG;
	@XmlElement
	private String ACC2_FLG;
	@XmlElement
	private String ACC3_FLG;
	@XmlElement
	private String ACC4_FLG;
	@XmlElement
	private String ACC5_FLG;
	@XmlElement
	private String ACC6_FLG;
	@XmlElement
	private String VUL_FLAG;      
	@XmlElement
	private String TRUST_FLAG;    
	@XmlElement
	private String AGE_UN70_FLAG;  
	@XmlElement
	private String EDU_OV_JR_FLAG; 
	@XmlElement
	private String HEALTH_FLAG;    
	@XmlElement
	private String BILLS_CHECK;   
	@XmlElement
	private String OBU_FLG;
	@XmlElement
	private String SICK_TYP;   
	@XmlElement
	private String ACC7_FLG;
	@XmlElement
	private String ACC8_FLG;
	@XmlElement
	private String ACC1_OTHER;
	@XmlElement
	private String ACC2_OTHER;
	@XmlElement
	private String ACC3_OTHER;
	@XmlElement
	private String ACC4_OTHER;
	@XmlElement
	private String ACC6_OTHER;
	@XmlElement
	private String ACC7_OTHER;
	@XmlElement
	private String ACC8_OTHER;
	@XmlElement
	private String INVEST_FLG;
	@XmlElement
	private String INVEST_TYP;
	@XmlElement
	private String INVEST_EXP;
	@XmlElement
	private String INVEST_DUE;
	@XmlElement
	private String BOND_FLAG;
	@XmlElement
	private String END_DATE; 
	@XmlElement
	private String DUE_END_DATE; 
	@XmlElement
	private String IDN_F; 
	@XmlElement
	private String BDAY;
	@XmlElement
	private String SICK_TYPE;
	@XmlElement
	private String CUST_NAME;
	@XmlElement
	private String DEGRADE;
	
	@XmlElement(name="TxRepeat")
	private List<FC032675OutputDetailsVO> details;
	public String getACTION() {
		return ACTION;
	}
	public void setACTION(String aCTION) {
		ACTION = aCTION;
	}
	public String getCNT() {
		return CNT;
	}
	public void setCNT(String cNT) {
		CNT = cNT;
	}
	public String getRC_COD() {
		return RC_COD;
	}
	public void setRC_COD(String rC_COD) {
		RC_COD = rC_COD;
	}
	public String getSRC_TYP() {
		return SRC_TYP;
	}
	public void setSRC_TYP(String sRC_TYP) {
		SRC_TYP = sRC_TYP;
	}
	public String getMTN_DATE() {
		return MTN_DATE;
	}
	public void setMTN_DATE(String mTN_DATE) {
		MTN_DATE = mTN_DATE;
	}
	public String getTX_FLG() {
		return TX_FLG;
	}
	public void setTX_FLG(String tX_FLG) {
		TX_FLG = tX_FLG;
	}
	public String getDESC() {
		return DESC;
	}
	public void setDESC(String dESC) {
		DESC = dESC;
	}
	public String getPROD_TYP() {
		return PROD_TYP;
	}
	public void setPROD_TYP(String pROD_TYP) {
		PROD_TYP = pROD_TYP;
	}
	public String getDEAD_TYP() {
		return DEAD_TYP;
	}
	public void setDEAD_TYP(String dEAD_TYP) {
		DEAD_TYP = dEAD_TYP;
	}
	public String getREJ_TYP() {
		return REJ_TYP;
	}
	public void setREJ_TYP(String rEJ_TYP) {
		REJ_TYP = rEJ_TYP;
	}
	public String getDM_FLG() {
		return DM_FLG;
	}
	public void setDM_FLG(String dM_FLG) {
		DM_FLG = dM_FLG;
	}
	public String getEDM_FLG() {
		return EDM_FLG;
	}
	public void setEDM_FLG(String eDM_FLG) {
		EDM_FLG = eDM_FLG;
	}
	public String getSMS_FLG() {
		return SMS_FLG;
	}
	public void setSMS_FLG(String sMS_FLG) {
		SMS_FLG = sMS_FLG;
	}
	public String getTM_FLG() {
		return TM_FLG;
	}
	public void setTM_FLG(String tM_FLG) {
		TM_FLG = tM_FLG;
	}
	public String getINFO_FLG() {
		return INFO_FLG;
	}
	public void setINFO_FLG(String iNFO_FLG) {
		INFO_FLG = iNFO_FLG;
	}
	public String getACC1_FLG() {
		return ACC1_FLG;
	}
	public void setACC1_FLG(String aCC1_FLG) {
		ACC1_FLG = aCC1_FLG;
	}
	public String getACC2_FLG() {
		return ACC2_FLG;
	}
	public void setACC2_FLG(String aCC2_FLG) {
		ACC2_FLG = aCC2_FLG;
	}
	public String getACC3_FLG() {
		return ACC3_FLG;
	}
	public void setACC3_FLG(String aCC3_FLG) {
		ACC3_FLG = aCC3_FLG;
	}
	public String getACC4_FLG() {
		return ACC4_FLG;
	}
	public void setACC4_FLG(String aCC4_FLG) {
		ACC4_FLG = aCC4_FLG;
	}
	public String getACC5_FLG() {
		return ACC5_FLG;
	}
	public void setACC5_FLG(String aCC5_FLG) {
		ACC5_FLG = aCC5_FLG;
	}
	public String getACC6_FLG() {
		return ACC6_FLG;
	}
	public void setACC6_FLG(String aCC6_FLG) {
		ACC6_FLG = aCC6_FLG;
	}
	public String getVUL_FLAG() {
		return VUL_FLAG;
	}
	public void setVUL_FLAG(String vUL_FLAG) {
		VUL_FLAG = vUL_FLAG;
	}
	public String getTRUST_FLAG() {
		return TRUST_FLAG;
	}
	public void setTRUST_FLAG(String tRUST_FLAG) {
		TRUST_FLAG = tRUST_FLAG;
	}
	public String getAGE_UN70_FLAG() {
		return AGE_UN70_FLAG;
	}
	public void setAGE_UN70_FLAG(String aGE_UN70_FLAG) {
		AGE_UN70_FLAG = aGE_UN70_FLAG;
	}
	public String getEDU_OV_JR_FLAG() {
		return EDU_OV_JR_FLAG;
	}
	public void setEDU_OV_JR_FLAG(String eDU_OV_JR_FLAG) {
		EDU_OV_JR_FLAG = eDU_OV_JR_FLAG;
	}
	public String getHEALTH_FLAG() {
		return HEALTH_FLAG;
	}
	public void setHEALTH_FLAG(String hEALTH_FLAG) {
		HEALTH_FLAG = hEALTH_FLAG;
	}
	public String getBILLS_CHECK() {
		return BILLS_CHECK;
	}
	public void setBILLS_CHECK(String bILLS_CHECK) {
		BILLS_CHECK = bILLS_CHECK;
	}
	public String getOBU_FLG() {
		return OBU_FLG;
	}
	public void setOBU_FLG(String oBU_FLG) {
		OBU_FLG = oBU_FLG;
	}
	public String getSICK_TYP() {
		return SICK_TYP;
	}
	public void setSICK_TYP(String sICK_TYP) {
		SICK_TYP = sICK_TYP;
	}
	public String getACC7_FLG() {
		return ACC7_FLG;
	}
	public void setACC7_FLG(String aCC7_FLG) {
		ACC7_FLG = aCC7_FLG;
	}
	public String getACC8_FLG() {
		return ACC8_FLG;
	}
	public void setACC8_FLG(String aCC8_FLG) {
		ACC8_FLG = aCC8_FLG;
	}
	public String getACC1_OTHER() {
		return ACC1_OTHER;
	}
	public void setACC1_OTHER(String aCC1_OTHER) {
		ACC1_OTHER = aCC1_OTHER;
	}
	public String getACC2_OTHER() {
		return ACC2_OTHER;
	}
	public void setACC2_OTHER(String aCC2_OTHER) {
		ACC2_OTHER = aCC2_OTHER;
	}
	public String getACC3_OTHER() {
		return ACC3_OTHER;
	}
	public void setACC3_OTHER(String aCC3_OTHER) {
		ACC3_OTHER = aCC3_OTHER;
	}
	public String getACC4_OTHER() {
		return ACC4_OTHER;
	}
	public void setACC4_OTHER(String aCC4_OTHER) {
		ACC4_OTHER = aCC4_OTHER;
	}
	public String getACC6_OTHER() {
		return ACC6_OTHER;
	}
	public void setACC6_OTHER(String aCC6_OTHER) {
		ACC6_OTHER = aCC6_OTHER;
	}
	public String getACC7_OTHER() {
		return ACC7_OTHER;
	}
	public void setACC7_OTHER(String aCC7_OTHER) {
		ACC7_OTHER = aCC7_OTHER;
	}
	public String getACC8_OTHER() {
		return ACC8_OTHER;
	}
	public void setACC8_OTHER(String aCC8_OTHER) {
		ACC8_OTHER = aCC8_OTHER;
	}
	public String getINVEST_FLG() {
		return INVEST_FLG;
	}
	public void setINVEST_FLG(String iNVEST_FLG) {
		INVEST_FLG = iNVEST_FLG;
	}
	public String getINVEST_TYP() {
		return INVEST_TYP;
	}
	public void setINVEST_TYP(String iNVEST_TYP) {
		INVEST_TYP = iNVEST_TYP;
	}
	public String getINVEST_EXP() {
		return INVEST_EXP;
	}
	public void setINVEST_EXP(String iNVEST_EXP) {
		INVEST_EXP = iNVEST_EXP;
	}
	public String getINVEST_DUE() {
		return INVEST_DUE;
	}
	public void setINVEST_DUE(String iNVEST_DUE) {
		INVEST_DUE = iNVEST_DUE;
	}
	public String getBOND_FLAG() {
		return BOND_FLAG;
	}
	public void setBOND_FLAG(String bOND_FLAG) {
		BOND_FLAG = bOND_FLAG;
	}
	public String getEND_DATE() {
		return END_DATE;
	}
	public void setEND_DATE(String eND_DATE) {
		END_DATE = eND_DATE;
	}
	public String getDUE_END_DATE() {
		return DUE_END_DATE;
	}
	public void setDUE_END_DATE(String dUE_END_DATE) {
		DUE_END_DATE = dUE_END_DATE;
	}
	public String getIDN_F() {
		return IDN_F;
	}
	public void setIDN_F(String iDN_F) {
		IDN_F = iDN_F;
	}
	public String getBDAY() {
		return BDAY;
	}
	public void setBDAY(String bDAY) {
		BDAY = bDAY;
	}
	public String getSICK_TYPE() {
		return SICK_TYPE;
	}
	public void setSICK_TYPE(String sICK_TYPE) {
		SICK_TYPE = sICK_TYPE;
	}
	public String getCUST_NAME() {
		return CUST_NAME;
	}
	public void setCUST_NAME(String cUST_NAME) {
		CUST_NAME = cUST_NAME;
	}
	public List<FC032675OutputDetailsVO> getDetails() {
		return details;
	}
	public void setDetails(List<FC032675OutputDetailsVO> details) {
		this.details = details;
	}
	public String getDEGRADE() {
		return DEGRADE;
	}
	public void setDEGRADE(String dEGRADE) {
		DEGRADE = dEGRADE;
	}	
}