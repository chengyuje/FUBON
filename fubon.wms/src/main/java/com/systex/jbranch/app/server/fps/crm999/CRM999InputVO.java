package com.systex.jbranch.app.server.fps.crm999;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class CRM999InputVO extends PagingInputVO {
	//客訴單編號
	private String complainListId;
	//分行別
	private String branchId;
	//等級
	private String grade;
	//客訴來源
	private String complainSource;
	//客訴類型
	private String complainTypeSel;
	//其他原因
	private String otherSource;
	//客訴摘要
	private String complainSummary;
	//客戶統編
	private String custId;
	//客戶種類
	private String custKind;
	//姓名
	private String custName;
	//出生年月日(西元)
	private Date birthday;
	//職業別
	private String occup;
	//聯絡電話
	private String phone;
	//學歷
	private String education;
	//開戶日期
	private Date openAccountDate;
	//總往來資產AUM
	private String totalAsset;
	//是否寄送對帳單
	private String checkSheet;
	//專員員編
	private String personId;
	//專員姓名
	private String personName;
	//AO Code
	private String AOcode;
	//是否在職
	private String serviceStatus;
	//申購產品類型
	private String buyProductType;
	//產品代碼
	private String productId;
	//申購日期
	private Date buyDate;
	//客訴商品
	private String complainProduct;
	//客訴商品幣別
	private String complainProductCurrency;
	//客訴商品金額
	private String complainProductAmount;
	//問題實況
	private String problemDescribe;
	//客戶訴求
	private String custDescribe;
	//處理情形- 金服主管
	private String handleCondition1;
	//處理情形- 處理情形- 區督導
	private String handleCondition2;
	//處理情形- 總行
	private String handleCondition3;
	//客訴類型
	private String complainType;
	//客訴型態
	private String complainMode;
	//摘要／訴求
	private String complainRequest;
	//處理情形
	private String handleStatus;
	//處理進度
	private String handleStep;
    //加附投資明細損益表 *.xls
//	private FormFile uploadFile;
    //加附投資明細損益表 *.xls
	private String uploadFileName;
    //處理人員等級
	private String jobLevel;
	//起始日
    private Date startDate;
    //迄止日
    private Date endDate;
    //所歸屬單位
    private String territoryId;
    //職稱ID
    private String jobTitleId;
    //狀態
    private String status;
    //查詢客訴表建檔結果
    private List custComplainList;
    //是否可執行退件
    private String rejectFlag;;
    //客訴發生日期
    private Date fmt_create_date;
    //客訴結案日期
    private Date fmt_end_date;
    //下一流程所屬單位
    private Collection allOrg=null;
    //下一流程負責人
    private Collection allUsr=null;
    //是否為總行
    private String isA01;
    //是否可新增客訴件
    private String canAdd;
    //客訴簽核類型
    private String flowType;
    //上一流程單位
    private String lastOrg;
    //上一流程負責人
    private String lastName;
    //目前流程單位
    private String org;
    //目前流程負責人
    private String emp;
    //目前流程負責人
    private String name;
    //下一流程單位
    private String nextOrg;
    //下一流程負責人
    private String nextEmp;
    //下一流程負責人
    private String nextName;
    //是否為目前處理人員
    private String myCase;
	//客訴類型
	private List complainTypeList;
	//客訴型態
	private List complainModeList;
	//摘要／訴求
	private List complainRequestList;
	//處理情形
	private List handleStatusList;
	//處理情形
	private String backReason;
	//總行新增客訴
	private String addByA01;
	//轄下分行
	private Collection branchById;
	//是否為客服
	private String is806;
	//上傳檔名
	private String uploadFile;
	//ADD新增流程時，jobLV固定設為1
	private String jobLv;
	//上一流程職稱
	private String lastOrgTitle;
	
	public String getAOcode() {
		return this.AOcode;
	}
	public void setAOcode(String ocode) {
		this.AOcode = ocode;
	}
	public Date getBirthday() {
		return birthday;
	}
	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}
	public String getBranchId() {
		return branchId;
	}
	public void setBranchId(String branchId) {
		this.branchId = branchId;
	}
	public Date getBuyDate() {
		return buyDate;
	}
	public void setBuyDate(Date buyDate) {
		this.buyDate = buyDate;
	}
	public String getBuyProductType() {
		return buyProductType;
	}
	public void setBuyProductType(String buyProductType) {
		this.buyProductType = buyProductType;
	}
	public String getCheckSheet() {
		return checkSheet;
	}
	public void setCheckSheet(String checkSheet) {
		this.checkSheet = checkSheet;
	}
	public String getComplainMode() {
		return complainMode;
	}
	public void setComplainMode(String complainMode) {
		this.complainMode = complainMode;
	}
	public String getComplainProduct() {
		return complainProduct;
	}
	public void setComplainProduct(String complainProduct) {
		this.complainProduct = complainProduct;
	}
	public String getComplainProductAmount() {
		return complainProductAmount;
	}
	public void setComplainProductAmount(String complainProductAmount) {
		this.complainProductAmount = complainProductAmount;
	}
	public String getComplainProductCurrency() {
		return complainProductCurrency;
	}
	public void setComplainProductCurrency(String complainProductCurrency) {
		this.complainProductCurrency = complainProductCurrency;
	}
	public String getComplainRequest() {
		return complainRequest;
	}
	public void setComplainRequest(String complainRequest) {
		this.complainRequest = complainRequest;
	}
	public String getComplainSource() {
		return complainSource;
	}
	public void setComplainSource(String complainSource) {
		this.complainSource = complainSource;
	}
	public String getComplainSummary() {
		return complainSummary;
	}
	public void setComplainSummary(String complainSummary) {
		this.complainSummary = complainSummary;
	}
	public String getComplainType() {
		return complainType;
	}
	public void setComplainType(String complainType) {
		this.complainType = complainType;
	}
	public String getCustId() {
		return custId;
	}
	public void setCustId(String custId) {
		this.custId = custId;
	}
	public String getCustName() {
		return custName;
	}
	public void setCustName(String custName) {
		this.custName = custName;
	}
	public String getCustDescribe() {
		return custDescribe;
	}
	public void setCustDescribe(String custDescribe) {
		this.custDescribe = custDescribe;
	}
	public String getEducation() {
		return education;
	}
	public void setEducation(String education) {
		this.education = education;
	}
	public String getHandleCondition1() {
		return handleCondition1;
	}
	public void setHandleCondition1(String handleCondition1) {
		this.handleCondition1 = handleCondition1;
	}
	public String getHandleCondition2() {
		return handleCondition2;
	}
	public void setHandleCondition2(String handleCondition2) {
		this.handleCondition2 = handleCondition2;
	}
	public String getHandleCondition3() {
		return handleCondition3;
	}
	public void setHandleCondition3(String handleCondition3) {
		this.handleCondition3 = handleCondition3;
	}
	public String getHandleStatus() {
		return handleStatus;
	}
	public void setHandleStatus(String handleStatus) {
		this.handleStatus = handleStatus;
	}
	public String getGrade() {
		return grade;
	}
	public void setGrade(String grade) {
		this.grade = grade;
	}
	public Date getOpenAccountDate() {
		return openAccountDate;
	}
	public void setOpenAccountDate(Date openAccountDate) {
		this.openAccountDate = openAccountDate;
	}
	public String getOtherSource() {
		return otherSource;
	}
	public void setOtherSource(String other) {
		this.otherSource = other;
	}
	public String getPersonId() {
		return personId;
	}
	public void setPersonId(String personId) {
		this.personId = personId;
	}
	public String getPersonName() {
		return personName;
	}
	public void setPersonName(String personName) {
		this.personName = personName;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getProblemDescribe() {
		return problemDescribe;
	}
	public void setProblemDescribe(String problemDescribe) {
		this.problemDescribe = problemDescribe;
	}
	public String getProductId() {
		return productId;
	}
	public void setProductId(String productId) {
		this.productId = productId;
	}
	public String getServiceStatus() {
		return serviceStatus;
	}
	public void setServiceStatus(String serviceStatus) {
		this.serviceStatus = serviceStatus;
	}
	public String getTotalAsset() {
		return totalAsset;
	}
	public void setTotalAsset(String totalAsset) {
		this.totalAsset = totalAsset;
	}
	public String getOccup() {
		return occup;
	}
	public void setOccup(String occup) {
		this.occup = occup;
	}

	public String getHandleStep() {
		return handleStep;
	}
	public void setHandleStep(String handleStep) {
		this.handleStep = handleStep;
	}
	public String getComplainListId() {
		return complainListId;
	}
	public void setComplainListId(String complainListId) {
		this.complainListId = complainListId;
	}
	public String getCustKind() {
		return custKind;
	}
	public void setCustKind(String custKind) {
		this.custKind = custKind;
	}
//	public FormFile getUploadFile() {
//		return uploadFile;
//	}
//	public void setUploadFile(FormFile uploadFile) {
//		this.uploadFile = uploadFile;
//	}
	public String getUploadFileName() {
		return uploadFileName;
	}
	public void setUploadFileName(String uploadFileName) {
		this.uploadFileName = uploadFileName;
	}
	public String getComplainTypeSel() {
		return complainTypeSel;
	}
	public void setComplainTypeSel(String complainTypeSel) {
		this.complainTypeSel = complainTypeSel;
	}
	public String getJobLevel() {
		return jobLevel;
	}
	public void setJobLevel(String jobLevel) {
		this.jobLevel = jobLevel;
	}
	public List getCustComplainList() {
		return custComplainList;
	}
	public void setCustComplainList(List custComplainList) {
		this.custComplainList = custComplainList;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public String getTerritoryId() {
		return territoryId;
	}
	public void setTerritoryId(String territoryId) {
		this.territoryId = territoryId;
	}
	public String getJobTitleId() {
		return jobTitleId;
	}
	public void setJobTitleId(String jobTitleId) {
		this.jobTitleId = jobTitleId;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getRejectFlag() {
		return rejectFlag;
	}
	public void setRejectFlag(String rejectFlag) {
		this.rejectFlag = rejectFlag;
	}
	public Date getFmt_create_date() {
		return fmt_create_date;
	}
	public void setFmt_create_date(Date fmt_create_date) {
		this.fmt_create_date = fmt_create_date;
	}
	public Date getFmt_end_date() {
		return fmt_end_date;
	}
	public void setFmt_end_date(Date fmt_end_date) {
		this.fmt_end_date = fmt_end_date;
	}

	public Collection getAllOrg() {
		return allOrg;
	}
	public void setAllOrg(Collection allOrg) {
		this.allOrg = allOrg;
	}

	public Collection getAllUsr() {
		return allUsr;
	}
	public void setAllUsr(Collection allUsr) {
		this.allUsr = allUsr;
	}
    
	public String getIsA01() {
		return this.isA01;
	}
	
	public void setIsA01(String isA01) {
		this.isA01 = isA01;
	}

	public void setCanAdd(String canAdd) {
		this.canAdd = canAdd;
	}
	
	public String getCanAdd() {
		return canAdd;
	}

	public void setFlowType(String flowType) {
		this.flowType = flowType;
	}
	
	public String getFlowType() {
		return flowType;
	}
    
	public String getLastOrg() {
		return this.lastOrg;
	}
	
	public void setLastOrg(String lastOrg) {
		this.lastOrg = lastOrg;
	}
	
	public String getLastName() {
		return this.lastName;
	}
	
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
    
	public String getOrg() {
		return this.org;
	}
	public void setOrg(String org) {
		this.org = org;
	}
    
	public String getEmp() {
		return this.emp;
	}
	
	public void setEmp(String emp) {
		this.emp = emp;
	}
    
	public String getName() {
		return this.name;
	}
	public void setName(String name) {
		this.name = name;
	}

    
	public String getNextOrg() {
		return nextOrg;
	}
	public void setNextOrg(String nextOrg) {
		this.nextOrg = nextOrg;
	}
    
	public String getNextEmp() {
		return nextEmp;
	}
	
	public void setNextEmp(String nextEmp) {
		this.nextEmp = nextEmp;
	}
    
	public String getNextName() {
		return nextName;
	}
	
	public void setNextName(String nextName) {
		this.nextName = nextName;
	}
    
	public String getMyCase() {
		return myCase;
	}
	
	public void setMyCase(String myCase) {
		this.myCase = myCase;
	}

	public List getComplainTypeList() {
		return complainTypeList;
	}
	
	public void setComplainTypeList(List complainTypeList) {
		this.complainTypeList = complainTypeList;
	}

	public List getComplainModeList() {
		return complainModeList;
	}
	
	public void setComplainModeList(List complainModeList) {
		this.complainModeList = complainModeList;
	}

	public List getComplainRequestList() {
		return complainRequestList;
	}
	
	public void setComplainRequestList(List complainRequestList) {
		this.complainRequestList = complainRequestList;
	}

	public List getHandleStatusList() {
		return handleStatusList;
	}
	
	public void setHandleStatusList(List handleStatusList) {
		this.handleStatusList = handleStatusList;
	}
	
	public String getBackReason() {
		return backReason;
	}
	
	public void setBackReason(String backReason) {
		this.backReason = backReason;
	}
	
	public String getAddByA01() {
		return addByA01;
	}
	
	public void setAddByA01(String addByA01) {
		this.addByA01 = addByA01;
	}

	public Collection getBranchById() {
		return branchById;
	}
	
	public void setBranchById(Collection branchById) {
		this.branchById = branchById;
	}
	public String getIs806() {
		return is806;
	}
	public void setIs806(String is806) {
		this.is806 = is806;
	}
	public String getUploadFile() {
		return uploadFile;
	}
	public void setUploadFile(String uploadFile) {
		this.uploadFile = uploadFile;
	}
	public String getJobLv() {
		return jobLv;
	}
	public void setJobLv(String jobLv) {
		this.jobLv = jobLv;
	}
	public String getLastOrgTitle() {
		return lastOrgTitle;
	}
	public void setLastOrgTitle(String lastOrgTitle) {
		this.lastOrgTitle = lastOrgTitle;
	}
}
