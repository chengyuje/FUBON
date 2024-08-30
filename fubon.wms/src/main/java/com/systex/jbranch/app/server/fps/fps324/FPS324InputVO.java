package com.systex.jbranch.app.server.fps.fps324;

import java.math.BigDecimal;
import java.util.List;
import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class FPS324InputVO extends PagingInputVO{

	private String prdId;
	private String mfmktcat;

	// table
	private String custId;
	private String sppType;
	private String riskType;
	private String planName;
	private String planID;

	//問題單0004749
	private String isChange;

	private String RISK_ATTR;
	private String VOL_RISK_ATTR;
	private String PLAN_STATUS;
	private int INV_PERIOD;
	private int INV_AMT_ONETIME;
	private int INV_AMT_PER_MONTH;
	private int INV_AMT_TARGET;
	private String VALID_FLAG;
	private String TRACE_FLAG;
	private String STEP_STAUS;
	private String INV_AMT_TYPE;
	
	//判斷是否刪除 子女教育 或 退休規劃 暫存資料
	private String sppTypeDelete;
	//子女教育
	private String UNIVERSITY;
	private BigDecimal UNIVERSITY_FEE_EDU;
	private BigDecimal UNIVERSITY_FEE_LIFE;
	private BigDecimal UNIVERSITY_YEAR;
	private String MASTER;
	private BigDecimal MASTER_FEE_EDU;
	private BigDecimal MASTER_FEE_LIFE;
	private BigDecimal MASTER_YEAR;
	private String PHD;
	private BigDecimal PHD_FEE_EDU;
	private BigDecimal PHD_FEE_LIFE;
	private BigDecimal PHD_YEAR;	
	
	//退休規劃
	private BigDecimal RETIREMENT_AGE;
	private BigDecimal RETIRE_FEE;
	private BigDecimal PREPARE_FEE;
	private BigDecimal SOCIAL_INS_FEE_1;
	private BigDecimal SOCIAL_INS_FEE_2;
	private BigDecimal SOCIAL_WELFARE_FEE_1;
	private BigDecimal SOCIAL_WELFARE_FEE_2;
	private BigDecimal COMM_INS_FEE_1;
	private BigDecimal COMM_INS_FEE_2;
	private BigDecimal OTHER_FEE_1;
	private BigDecimal OTHER_FEE_2;
	private BigDecimal HERITAGE;
	

	private List<FPS324PrdInputVO> prdList;

	public FPS324InputVO() {
	}

	public String getCustId() {
		return custId;
	}

	public void setCustId(String custId) {
		this.custId = custId;
	}

	public String getSppType() {
		return sppType;
	}

	public void setSppType(String sppType) {
		this.sppType = sppType;
	}

	public String getRiskType() {
		return riskType;
	}

	public void setRiskType(String riskType) {
		this.riskType = riskType;
	}

	public String getPrdId() {
		return prdId;
	}

	public void setPrdId(String prdId) {
		this.prdId = prdId;
	}

	public String getMfmktcat() {
		return mfmktcat;
	}

	public void setMfmktcat(String mfmktcat) {
		this.mfmktcat = mfmktcat;
	}

	public String getPlanName() {
		return planName;
	}

	public void setPlanName(String planName) {
		this.planName = planName;
	}

	public String getPlanID() {
		return planID;
	}

	public void setPlanID(String planID) {
		this.planID = planID;
	}

	public String getIsChange() {
		return isChange;
	}

	public void setIsChange(String isChange) {
		this.isChange = isChange;
	}
	
	public String getRISK_ATTR() {
		return RISK_ATTR;
	}

	public void setRISK_ATTR(String rISK_ATTR) {
		RISK_ATTR = rISK_ATTR;
	}

	public String getVOL_RISK_ATTR() {
		return VOL_RISK_ATTR;
	}

	public void setVOL_RISK_ATTR(String vOL_RISK_ATTR) {
		VOL_RISK_ATTR = vOL_RISK_ATTR;
	}

	public String getPLAN_STATUS() {
		return PLAN_STATUS;
	}

	public void setPLAN_STATUS(String pLAN_STATUS) {
		PLAN_STATUS = pLAN_STATUS;
	}

	public int getINV_PERIOD() {
		return INV_PERIOD;
	}

	public void setINV_PERIOD(int iNV_PERIOD) {
		INV_PERIOD = iNV_PERIOD;
	}

	public int getINV_AMT_ONETIME() {
		return INV_AMT_ONETIME;
	}

	public void setINV_AMT_ONETIME(int iNV_AMT_ONETIME) {
		INV_AMT_ONETIME = iNV_AMT_ONETIME;
	}

	public int getINV_AMT_PER_MONTH() {
		return INV_AMT_PER_MONTH;
	}

	public void setINV_AMT_PER_MONTH(int iNV_AMT_PER_MONTH) {
		INV_AMT_PER_MONTH = iNV_AMT_PER_MONTH;
	}

	public int getINV_AMT_TARGET() {
		return INV_AMT_TARGET;
	}

	public void setINV_AMT_TARGET(int iNV_AMT_TARGET) {
		INV_AMT_TARGET = iNV_AMT_TARGET;
	}

	public String getVALID_FLAG() {
		return VALID_FLAG;
	}

	public void setVALID_FLAG(String vALID_FLAG) {
		VALID_FLAG = vALID_FLAG;
	}

	public String getTRACE_FLAG() {
		return TRACE_FLAG;
	}

	public void setTRACE_FLAG(String tRACE_FLAG) {
		TRACE_FLAG = tRACE_FLAG;
	}	
	
	public String getSTEP_STAUS() {
		return STEP_STAUS;
	}

	public void setSTEP_STAUS(String sTEP_STAUS) {
		STEP_STAUS = sTEP_STAUS;
	}

	public String getINV_AMT_TYPE() {
		return INV_AMT_TYPE;
	}

	public void setINV_AMT_TYPE(String iNV_AMT_TYPE) {
		INV_AMT_TYPE = iNV_AMT_TYPE;
	}	

	public String getSppTypeDelete() {
		return sppTypeDelete;
	}

	public void setSppTypeDelete(String sppTypeDelete) {
		this.sppTypeDelete = sppTypeDelete;
	}

	public String getUNIVERSITY() {
		return UNIVERSITY;
	}
	public void setUNIVERSITY(String uNIVERSITY) {
		UNIVERSITY = uNIVERSITY;
	}

	public BigDecimal getUNIVERSITY_FEE_EDU() {
		return UNIVERSITY_FEE_EDU;
	}
	public void setUNIVERSITY_FEE_EDU(BigDecimal uNIVERSITY_FEE_EDU) {
		UNIVERSITY_FEE_EDU = uNIVERSITY_FEE_EDU;
	}

	public BigDecimal getUNIVERSITY_FEE_LIFE() {
		return UNIVERSITY_FEE_LIFE;
	}
	public void setUNIVERSITY_FEE_LIFE(BigDecimal uNIVERSITY_FEE_LIFE) {
		UNIVERSITY_FEE_LIFE = uNIVERSITY_FEE_LIFE;
	}

	public BigDecimal getUNIVERSITY_YEAR() {
		return UNIVERSITY_YEAR;
	}
	public void setUNIVERSITY_YEAR(BigDecimal uNIVERSITY_YEAR) {
		UNIVERSITY_YEAR = uNIVERSITY_YEAR;
	}

	public String getMASTER() {
		return MASTER;
	}
	public void setMASTER(String mASTER) {
		MASTER = mASTER;
	}

	public BigDecimal getMASTER_FEE_EDU() {
		return MASTER_FEE_EDU;
	}
	public void setMASTER_FEE_EDU(BigDecimal mASTER_FEE_EDU) {
		MASTER_FEE_EDU = mASTER_FEE_EDU;
	}

	public BigDecimal getMASTER_FEE_LIFE() {
		return MASTER_FEE_LIFE;
	}
	public void setMASTER_FEE_LIFE(BigDecimal mASTER_FEE_LIFE) {
		MASTER_FEE_LIFE = mASTER_FEE_LIFE;
	}

	public BigDecimal getMASTER_YEAR() {
		return MASTER_YEAR;
	}
	public void setMASTER_YEAR(BigDecimal mASTER_YEAR) {
		MASTER_YEAR = mASTER_YEAR;
	}

	public String getPHD() {
		return PHD;
	}
	public void setPHD(String pHD) {
		PHD = pHD;
	}

	public BigDecimal getPHD_FEE_EDU() {
		return PHD_FEE_EDU;
	}
	public void setPHD_FEE_EDU(BigDecimal pHD_FEE_EDU) {
		PHD_FEE_EDU = pHD_FEE_EDU;
	}

	public BigDecimal getPHD_FEE_LIFE() {
		return PHD_FEE_LIFE;
	}
	public void setPHD_FEE_LIFE(BigDecimal pHD_FEE_LIFE) {
		PHD_FEE_LIFE = pHD_FEE_LIFE;
	}

	public BigDecimal getPHD_YEAR() {
		return PHD_YEAR;
	}
	public void setPHD_YEAR(BigDecimal pHD_YEAR) {
		PHD_YEAR = pHD_YEAR;
	}

	public List<FPS324PrdInputVO> getPrdList() {
		return prdList;
	}

	public void setPrdList(List<FPS324PrdInputVO> prdList) {
		this.prdList = prdList;
	}

	public BigDecimal getRETIREMENT_AGE() {
		return RETIREMENT_AGE;
	}

	public void setRETIREMENT_AGE(BigDecimal rETIREMENT_AGE) {
		RETIREMENT_AGE = rETIREMENT_AGE;
	}

	public BigDecimal getRETIRE_FEE() {
		return RETIRE_FEE;
	}

	public void setRETIRE_FEE(BigDecimal rETIRE_FEE) {
		RETIRE_FEE = rETIRE_FEE;
	}

	public BigDecimal getPREPARE_FEE() {
		return PREPARE_FEE;
	}

	public void setPREPARE_FEE(BigDecimal pREPARE_FEE) {
		PREPARE_FEE = pREPARE_FEE;
	}

	public BigDecimal getSOCIAL_INS_FEE_1() {
		return SOCIAL_INS_FEE_1;
	}

	public void setSOCIAL_INS_FEE_1(BigDecimal sOCIAL_INS_FEE_1) {
		SOCIAL_INS_FEE_1 = sOCIAL_INS_FEE_1;
	}

	public BigDecimal getSOCIAL_INS_FEE_2() {
		return SOCIAL_INS_FEE_2;
	}

	public void setSOCIAL_INS_FEE_2(BigDecimal sOCIAL_INS_FEE_2) {
		SOCIAL_INS_FEE_2 = sOCIAL_INS_FEE_2;
	}

	public BigDecimal getSOCIAL_WELFARE_FEE_1() {
		return SOCIAL_WELFARE_FEE_1;
	}

	public void setSOCIAL_WELFARE_FEE_1(BigDecimal sOCIAL_WELFARE_FEE_1) {
		SOCIAL_WELFARE_FEE_1 = sOCIAL_WELFARE_FEE_1;
	}

	public BigDecimal getSOCIAL_WELFARE_FEE_2() {
		return SOCIAL_WELFARE_FEE_2;
	}

	public void setSOCIAL_WELFARE_FEE_2(BigDecimal sOCIAL_WELFARE_FEE_2) {
		SOCIAL_WELFARE_FEE_2 = sOCIAL_WELFARE_FEE_2;
	}

	public BigDecimal getCOMM_INS_FEE_1() {
		return COMM_INS_FEE_1;
	}

	public void setCOMM_INS_FEE_1(BigDecimal cOMM_INS_FEE_1) {
		COMM_INS_FEE_1 = cOMM_INS_FEE_1;
	}

	public BigDecimal getCOMM_INS_FEE_2() {
		return COMM_INS_FEE_2;
	}

	public void setCOMM_INS_FEE_2(BigDecimal cOMM_INS_FEE_2) {
		COMM_INS_FEE_2 = cOMM_INS_FEE_2;
	}

	public BigDecimal getOTHER_FEE_1() {
		return OTHER_FEE_1;
	}

	public void setOTHER_FEE_1(BigDecimal oTHER_FEE_1) {
		OTHER_FEE_1 = oTHER_FEE_1;
	}

	public BigDecimal getOTHER_FEE_2() {
		return OTHER_FEE_2;
	}

	public void setOTHER_FEE_2(BigDecimal oTHER_FEE_2) {
		OTHER_FEE_2 = oTHER_FEE_2;
	}

	public BigDecimal getHERITAGE() {
		return HERITAGE;
	}

	public void setHERITAGE(BigDecimal hERITAGE) {
		HERITAGE = hERITAGE;
	}
	
}
