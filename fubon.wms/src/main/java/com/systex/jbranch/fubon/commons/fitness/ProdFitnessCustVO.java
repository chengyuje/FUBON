package com.systex.jbranch.fubon.commons.fitness;

import com.systex.jbranch.app.server.fps.sot701.*;
import com.systex.jbranch.app.server.fps.sot714.SOT714;
import com.systex.jbranch.app.server.fps.sot714.SOT714InputVO;
import com.systex.jbranch.fubon.commons.fitness.ProdFitness.CustType;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.server.bizLogic.BizLogic;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ProdFitnessCustVO extends BizLogic  {
	private String custID;
	private String riskAtr;
	private Date kycDueDate;
	private BigDecimal age;
	private Date birthday;
	private String qValue;
	private Boolean isStakeholder;
	private CustType custType;
	private W8BenDataVO w8BenDataVO;
	private FatcaDataVO fatcaDataVO;
//	private FC032675DataVO fc032675DataVO; // CBS 已改用 fp032675DataVO
	private FP032675DataVO fp032675DataVO;
	private String BranchNbr;	//歸屬行
	private CustHighNetWorthDataVO HNWCDataVO; //客戶高資產註記
	
	public ProdFitnessCustVO(DataAccessManager dam, String custId) throws DAOException, JBranchException {
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT MAST.CUST_ID, KYC.CUST_RISK_AFR as CUST_RISK_ATR, MAST.BIRTH_DATE, ");
		sb.append(" KYC.EXPIRY_DATE as KYC_DUE_DATE, MAST.BRA_NBR, ROUND(MONTHS_BETWEEN(SYSDATE, MAST.BIRTH_DATE)/12, 2) AS AGE ");
		sb.append(" FROM TBCRM_CUST_MAST MAST ");
		sb.append(" LEFT OUTER JOIN TBKYC_INVESTOREXAM_M_HIS KYC ON KYC.CUST_ID = MAST.CUST_ID AND STATUS = '03' ");
		sb.append(" WHERE MAST.CUST_ID = :custID ");
		sb.append(" ORDER BY CREATE_DATE DESC ");
		sb.append(" FETCH FIRST 1 ROWS ONLY ");
		queryCondition.setQueryString(sb.toString());
		queryCondition.setObject("custID", custId.toUpperCase());
		List<Map<String, Object>> cList = dam.exeQuery(queryCondition);
		
		if(CollectionUtils.isNotEmpty(cList)) {
			this.setCustID(custId);
			this.setRiskAtr((String) cList.get(0).get("CUST_RISK_ATR"));
			this.setKycDueDate((Date) cList.get(0).get("KYC_DUE_DATE"));
			this.setAge((BigDecimal) cList.get(0).get("AGE"));
			this.setBirthday((Date) cList.get(0).get("BIRTH_DATE"));
			this.setBranchNbr((String) cList.get(0).get("BRA_NBR"));
		}
		
		//#0476 下單模組KYC相關來源改從電文取得
		CustKYCDataVO kycData = getKycData();
		if(null != kycData){
			this.setRiskAtr(kycData.getKycLevel());
			this.setKycDueDate(kycData.getKycDueDate());
		} else {
			this.setRiskAtr(null);
			this.setKycDueDate(null);
		}
		
		//客戶類型
		int custIdLen = StringUtils.length(custId);
		if(custIdLen >= 10)
			this.setCustType(CustType.PERSON);
		else if(custIdLen >= 8 && custIdLen < 10)
			this.setCustType(CustType.CORPORATION);
	}
	
	public String getCustID() {
		return custID;
	}
	
	public void setCustID(String custID) {
		this.custID = custID;
	}
	
	public String getRiskAtr() {
		return riskAtr;
	}
	
	public void setRiskAtr(String riskAtr) {
		this.riskAtr = riskAtr;
	}
	
	public Date getKycDueDate() {
		return kycDueDate;
	}

	public void setKycDueDate(Date kycDueDate) {
		this.kycDueDate = kycDueDate;
	}

	public BigDecimal getAge() {
		return age;
	}
	
	public void setAge(BigDecimal age) {
		this.age = age;
	}
	
	public Date getBirthday() {
		return birthday;
	}
	
	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}
	
	public String getqValue() throws Exception {
		if(StringUtils.isBlank(this.qValue) && !StringUtils.isWhitespace(this.qValue)) {
			SOT701InputVO inputVO = new SOT701InputVO();
			inputVO.setCustID(this.custID);
			SOT701 sot701 = (SOT701) PlatformContext.getBean("sot701");
					
			//查詢客戶Q值
			String qVal = sot701.getCustQValue(inputVO);
	        this.qValue = StringUtils.isBlank(qVal) ? "" : qVal;
		}
		
		return qValue;
	}

	public void setqValue(String qValue) {
		this.qValue = qValue;
	}

	public Boolean getIsStakeholder() throws Exception {
		if(this.isStakeholder == null) {
			SOT701InputVO inputVO = new SOT701InputVO();
			inputVO.setCustID(this.custID);
			SOT701 sot701 = (SOT701) PlatformContext.getBean("sot701");
					
			//查詢客戶客戶利害關係人註記
	        this.isStakeholder = sot701.isCustStakeholder(inputVO);
		}
		
		return isStakeholder;
	}

	public void setIsStakeholder(Boolean isStakeholder) {
		this.isStakeholder = isStakeholder;
	}

	public CustType getCustType() {
		return custType;
	}

	public void setCustType(CustType custType) {
		this.custType = custType;
	}
	public FatcaDataVO getFatcaDataVO() throws Exception {
		if(this.fatcaDataVO ==  null) {
			SOT701InputVO inputVO = new SOT701InputVO();
			inputVO.setCustID(this.custID);
			SOT701 sot701 = (SOT701) PlatformContext.getBean("sot701");
					
			//查詢FATCA客戶註記檢核
	        this.fatcaDataVO = sot701.getFatcaData(inputVO);
		}
		
		return this.fatcaDataVO;
	}
	
	public void setFatcaDataVO(FatcaDataVO fatcaDataVO) {
		this.fatcaDataVO = fatcaDataVO;
	}

	public W8BenDataVO getW8BenDataVO() throws Exception {
		if(this.w8BenDataVO ==  null) {
			SOT701InputVO inputVO = new SOT701InputVO();
			inputVO.setCustID(this.custID);
			SOT701 sot701 = (SOT701) PlatformContext.getBean("sot701");
					
			//查詢客戶W-8BEN和FATCA客戶註記檢核
	        this.w8BenDataVO = sot701.getCustW8BenFATCA(inputVO);
		}
		
		return this.w8BenDataVO;
	}
	
	public void setW8BenDataVO(W8BenDataVO w8BenDataVO) {
		this.w8BenDataVO = w8BenDataVO;
	}


//	CBS 刪除此方法
//	public FC032675DataVO getFc032675DataVO() throws Exception {
//		if(this.fc032675DataVO ==  null) {
//			SOT701InputVO inputVO = new SOT701InputVO();
//			inputVO.setCustID(this.custID);
//			SOT701 sot701 = (SOT701) PlatformContext.getBean("sot701");
//
//			//查詢客戶註記資料
//	        this.fc032675DataVO = sot701.getFC032675Data(inputVO);
//		}
//
//		return fc032675DataVO;
//	}

	//	CBS 刪除此方法
//	public void setFc032675DataVO(FC032675DataVO fc032675DataVO) {
//		this.fc032675DataVO = fc032675DataVO;
//	}

	public FP032675DataVO getFp032675DataVO() throws Exception {
		if(this.fp032675DataVO ==  null) {
			SOT701InputVO inputVO = new SOT701InputVO();
			inputVO.setCustID(this.custID);
			SOT701 sot701 = (SOT701) PlatformContext.getBean("sot701");
					
			//查詢客戶註記資料
	        this.fp032675DataVO = sot701.getFP032675Data(inputVO);
		}
		
		return fp032675DataVO;
	}

	public void setFp032675DataVO(FP032675DataVO fp032675DataVO) {
		this.fp032675DataVO = fp032675DataVO;
	}

	public String getBranchNbr() {
		return BranchNbr;
	}

	public void setBranchNbr(String branchNbr) {
		BranchNbr = branchNbr;
	}
	
	private CustKYCDataVO getKycData(){
		SOT701InputVO inputVO = new SOT701InputVO();
		inputVO.setCustID(this.custID);
		SOT701 sot701 = null;
		try {
			sot701 = (SOT701) PlatformContext.getBean("sot701");
		} catch (JBranchException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			return sot701.getCustKycData(inputVO);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
	}

	public CustHighNetWorthDataVO getHNWCDataVO() throws Exception {
		if(this.HNWCDataVO ==  null) {
			SOT714 sot714 = (SOT714) PlatformContext.getBean("sot714");
			//查詢客戶高資產註記資料
	        this.HNWCDataVO = sot714.getHNWCData(this.custID);
		}
		
		return HNWCDataVO;
	}

	public void setHNWCDataVO(CustHighNetWorthDataVO hNWCDataVO) {
		this.HNWCDataVO = hNWCDataVO;
	}
	
	
}