package com.systex.jbranch.app.server.fps.kyc620;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

/**
 * @author USER
 *
 */
public class KYC620InputVO extends PagingInputVO {
 
	private String CUST_ID;     //客戶ID
	private String CUST_NAME;     //客戶姓名
	private String SEQ;    //序號主鍵
	private Date CREATE_DATE;//問卷列印日期      
	private String branch_nbr;
	public String getCUST_ID() {
		return CUST_ID;
	}
	public void setCUST_ID(String cUST_ID) {
		CUST_ID = cUST_ID;
	}
	public String getSEQ() {
		return SEQ;
	}


	public void setSEQ(String sEQ) {
		SEQ = sEQ;
	}


	public String getCUST_NAME() {
		return CUST_NAME;
	}


	public void setCUST_NAME(String cUST_NAME) {
		CUST_NAME = cUST_NAME;
	}
	public Date getCREATE_DATE() {
		return CREATE_DATE;
	}
	public void setCREATE_DATE(Date cREATE_DATE) {
		CREATE_DATE = cREATE_DATE;
	}
	public String getBranchNbr() {
		return branch_nbr;
	}
	public void setBranchNbr(String branchNbr) {
		this.branch_nbr = branchNbr;
	}

}
