package com.systex.jbranch.app.server.fps.sot701;

import java.util.Date;
import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class SOT701OutputVO extends PagingOutputVO {
	private List resultList;
	private FC032675DataVO fc032675DataVO;
	private CustPLDataVO custPLDataVO;
	private CustKYCDataVO custKYCDataVO;
	private W8BenDataVO w8BenDataVO;
	private FatcaDataVO fatcaDataVO;
	private CustNoteDataVO custNoteDataVO;
	private CustAcctDataVO custAcctDataVO;
	private Date dueDate;
	private Boolean isFirstTrade;
	private String getCustQValue;
	private Boolean isCustStakeholder;
	private List<AcctVO> acct12List;
	private FP032675DataVO fp032675DataVO;
	
	private CustSIPromotionDataVO siPromDataVO;
	private CustHighNetWorthDataVO hnwcDataVO;
	
	private String flagNumber; 	//90天內是否有貸款紀錄 Y/N
	
	
	public FP032675DataVO getFp032675DataVO() {
		return fp032675DataVO;
	}

	public void setFp032675DataVO(FP032675DataVO fp032675DataVO) {
		this.fp032675DataVO = fp032675DataVO;
	}


	public List getResultList() {
		return resultList;
	}

	public void setResultList(List resultList) {
		this.resultList = resultList;
	}

	public FC032675DataVO getFc032675DataVO() {
		return fc032675DataVO;
	}

	public void setFc032675DataVO(FC032675DataVO fc032675DataVO) {
		this.fc032675DataVO = fc032675DataVO;
	}

	public CustPLDataVO getCustPLDataVO() {
		return custPLDataVO;
	}

	public void setCustPLDataVO(CustPLDataVO custPLDataVO) {
		this.custPLDataVO = custPLDataVO;
	}

	public CustKYCDataVO getCustKYCDataVO() {
		return custKYCDataVO;
	}

	public void setCustKYCDataVO(CustKYCDataVO custKYCDataVO) {
		this.custKYCDataVO = custKYCDataVO;
	}

	public W8BenDataVO getW8BenDataVO() {
		return w8BenDataVO;
	}

	public void setW8BenDataVO(W8BenDataVO w8BenDataVO) {
		this.w8BenDataVO = w8BenDataVO;
	}
	
	public FatcaDataVO getFatcaDataVO() {
		return fatcaDataVO;
	}

	public void setFatcaDataVO(FatcaDataVO fatcaDataVO) {
		this.fatcaDataVO = fatcaDataVO;
	}

	public CustNoteDataVO getCustNoteDataVO() {
		return custNoteDataVO;
	}

	public void setCustNoteDataVO(CustNoteDataVO custNoteDataVO) {
		this.custNoteDataVO = custNoteDataVO;
	}

	public CustAcctDataVO getCustAcctDataVO() {
		return custAcctDataVO;
	}

	public void setCustAcctDataVO(CustAcctDataVO custAcctDataVO) {
		this.custAcctDataVO = custAcctDataVO;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public void setDueDate(Date dueDate) {
		this.dueDate = dueDate;
	}

	public Boolean getIsFirstTrade() {
		return isFirstTrade;
	}

	public void setIsFirstTrade(Boolean firstTrade) {
		isFirstTrade = firstTrade;
	}

	public Boolean getFirstTrade() {
		return isFirstTrade;
	}

	public void setFirstTrade(Boolean firstTrade) {
		isFirstTrade = firstTrade;
	}

	public String getGetCustQValue() {
		return getCustQValue;
	}

	public void setGetCustQValue(String getCustQValue) {
		this.getCustQValue = getCustQValue;
	}

	public Boolean getCustStakeholder() {
		return isCustStakeholder;
	}

	public void setCustStakeholder(Boolean custStakeholder) {
		isCustStakeholder = custStakeholder;
	}

	public List<AcctVO> getAcct12List() {
		return acct12List;
	}

	public void setAcct12List(List<AcctVO> acct12List) {
		this.acct12List = acct12List;
	}

	public Boolean getIsCustStakeholder() {
		return isCustStakeholder;
	}

	public void setIsCustStakeholder(Boolean isCustStakeholder) {
		this.isCustStakeholder = isCustStakeholder;
	}

	public CustSIPromotionDataVO getSiPromDataVO() {
		return siPromDataVO;
	}

	public void setSiPromDataVO(CustSIPromotionDataVO siPromDataVO) {
		this.siPromDataVO = siPromDataVO;
	}

	public CustHighNetWorthDataVO getHnwcDataVO() {
		return hnwcDataVO;
	}

	public void setHnwcDataVO(CustHighNetWorthDataVO hnwcDataVO) {
		this.hnwcDataVO = hnwcDataVO;
	}

	public String getFlagNumber() {
		return flagNumber;
	}

	public void setFlagNumber(String flagNumber) {
		this.flagNumber = flagNumber;
	}
	
	
}
