package com.systex.jbranch.app.server.fps.crm611;

import java.util.List;

import com.systex.jbranch.fubon.commons.esb.vo.clm032151.CLM032151OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.fc032151.FC032151OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.fp032151.FP032151OutputVO;
import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class CRM611OutputVO extends PagingOutputVO{
	private List resultList;
	private FC032151OutputVO fc032151OutputVO;
	private CLM032151OutputVO clm032151OutputVO;
	private FP032151OutputVO fp032151OutputVO;
	
	private String AO_ID;
	private String AO_CODE;
	private String VIP_DEGREE;
	private String RISK_ATTR;
	private String CUST_BRANCH;
	private String CUST_AREA;
	private String CUST_REGION;
	
	private boolean lostFlag;
	
	
	
	public FP032151OutputVO getFp032151OutputVO() {
		return fp032151OutputVO;
	}
	public void setFp032151OutputVO(FP032151OutputVO fp032151OutputVO) {
		this.fp032151OutputVO = fp032151OutputVO;
	}
	public CLM032151OutputVO getClm032151OutputVO() {
		return clm032151OutputVO;
	}
	public void setClm032151OutputVO(CLM032151OutputVO clm032151OutputVO) {
		this.clm032151OutputVO = clm032151OutputVO;
	}
	public List getResultList() {
		return resultList;
	}
	public void setResultList(List resultList) {
		this.resultList = resultList;
	}
	public FC032151OutputVO getFc032151OutputVO() {
		return fc032151OutputVO;
	}
	public void setFc032151OutputVO(FC032151OutputVO fc032151OutputVO) {
		this.fc032151OutputVO = fc032151OutputVO;
	}
	public String getAO_ID() {
		return AO_ID;
	}
	public void setAO_ID(String aO_ID) {
		AO_ID = aO_ID;
	}
	public String getAO_CODE() {
		return AO_CODE;
	}
	public void setAO_CODE(String aO_CODE) {
		AO_CODE = aO_CODE;
	}
	public String getVIP_DEGREE() {
		return VIP_DEGREE;
	}
	public void setVIP_DEGREE(String vIP_DEGREE) {
		VIP_DEGREE = vIP_DEGREE;
	}
	public String getRISK_ATTR() {
		return RISK_ATTR;
	}
	public void setRISK_ATTR(String rISK_ATTR) {
		RISK_ATTR = rISK_ATTR;
	}
	public String getCUST_BRANCH() {
		return CUST_BRANCH;
	}
	public void setCUST_BRANCH(String cUST_BRANCH) {
		CUST_BRANCH = cUST_BRANCH;
	}
	public String getCUST_AREA() {
		return CUST_AREA;
	}
	public void setCUST_AREA(String cUST_AREA) {
		CUST_AREA = cUST_AREA;
	}
	public String getCUST_REGION() {
		return CUST_REGION;
	}
	public void setCUST_REGION(String cUST_REGION) {
		CUST_REGION = cUST_REGION;
	}
	public boolean isLostFlag() {
		return lostFlag;
	}
	public void setLostFlag(boolean lostFlag) {
		this.lostFlag = lostFlag;
	}
    
	
}
