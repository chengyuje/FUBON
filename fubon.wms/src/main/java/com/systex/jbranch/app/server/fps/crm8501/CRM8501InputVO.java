package com.systex.jbranch.app.server.fps.crm8501;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class CRM8501InputVO extends PagingInputVO {
	private String custID;				//客戶ID
	private String assetPrintFlag;		//約定書註記
	private String applyStatus;			//申請狀態	
	private String printAll;			//全選
	private String printSav;			//存款
	private String printInv;			//投資
	private String printIns;			//保險
	private String printLoan;			//融資	
	private String printAllChart;		//圖示全選
	private String printAUM;			//AUM圖
	private String printIIL;			//存投保圖
	private String printCUR;			//幣別圖
	private String printType;			//基金市場圖
	private String printFundMkt;		//基金市場圖
	private String printPortfolio;		//投資組合圖
	private String branchNbr;			//指定分行
	private String aoCode;				//理專
	private boolean isPrint;            //是否套印申請書(不套印只寫入申請記錄) 20200320 by Jacky WMS-CR-20200214-01_新增理專輪調暨客戶換手經營資料產出及後續控管
	private String fromTxn;             //從那個交易進入,目前CRM341、CRM8501 都會呼叫
	private String fileName;            //下載檔案名稱
	private String fundSortType;     //基金排序方式
	
	
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFromTxn() {
		return fromTxn;
	}
	public void setFromTxn(String fromTxn) {
		this.fromTxn = fromTxn;
	}
	public boolean isPrint() {
		return isPrint;
	}
	public void setPrint(boolean isPrint) {
		this.isPrint = isPrint;
	}
	public String getCustID() {
		return custID;
	}
	public void setCustID(String custID) {
		this.custID = custID;
	}
	
	public String getAssetPrintFlag() {
		return assetPrintFlag;
	}
	public void setAssetPrintFlag(String assetPrintFlag) {
		this.assetPrintFlag = assetPrintFlag;
	}
	
	public String getApplyStatus() {
		return applyStatus;
	}
	public void setApplyStatus(String applyStatus) {
		this.applyStatus = applyStatus;
	}
	
	public String getPrintAll() {
		return printAll;
	}
	public void setPrintAll(String printAll) {
		this.printAll = printAll;
	}
	
	public String getPrintSav() {
		return printSav;
	}
	public void setPrintSav(String printSav) {
		this.printSav = printSav;
	}
	
	public String getPrintInv() {
		return printInv;
	}
	public void setPrintInv(String printInv) {
		this.printInv = printInv;
	}
	
	public String getPrintIns() {
		return printIns;
	}
	public void setPrintIns(String printIns) {
		this.printIns = printIns;
	}
	
	public String getPrintLoan() {
		return printLoan;
	}
	public void setPrintLoan(String printLoan) {
		this.printLoan = printLoan;
	}
	
	public String getPrintAUM() {
		return printAUM;
	}
	public void setPrintAUM(String printAUM) {
		this.printAUM = printAUM;
	}
	
	public String getPrintIIL() {
		return printIIL;
	}
	public void setPrintIIL(String printIIL) {
		this.printIIL = printIIL;
	}
	
	public String getPrintCUR() {
		return printCUR;
	}
	public void setPrintCUR(String printCUR) {
		this.printCUR = printCUR;
	}
	
	public String getPrintType() {
		return printType;
	}
	public void setPrintType(String printType) {
		this.printType = printType;
	}
	
	public String getPrintFundMkt() {
		return printFundMkt;
	}
	public void setPrintFundMkt(String printFundMkt) {
		this.printFundMkt = printFundMkt;
	}
	
	public String getPrintPortfolio() {
		return printPortfolio;
	}
	public void setPrintPortfolio(String printPortfolio) {
		this.printPortfolio = printPortfolio;
	}
	
	public String getBranchNbr() {
		return branchNbr;
	}
	public void setBranchNbr(String branchNbr) {
		this.branchNbr = branchNbr;
	}
		
	public String getAoCode() {
		return aoCode;
	}
	public void setAoCode(String aoCode) {
		this.aoCode = aoCode;
	}

	public String getPrintAllChart() {
		return printAllChart;
	}
	public void setPrintAllChart(String printAllChart) {
		this.printAllChart = printAllChart;
	}
	

	public String getFundSortType() {
		return fundSortType;
	}
	public void setFundSortType(String fundSortType) {
		this.fundSortType = fundSortType;
	}
	@Override
	public String toString() {
		return "CRM8501InputVO [custID=" + custID + ", assetPrintFlag="
				+ assetPrintFlag + ", applyStatus=" + applyStatus
				+ ", printAll=" + printAll + ", printSav=" + printSav
				+ ", printInv=" + printInv + ", printIns=" + printIns
				+ ", printLoan=" + printLoan + ", printAUM=" + printAUM
				+ ", printIIL=" + printIIL + ", printCUR=" + printCUR
				+ ", printType=" + printType + ", printFundMkt=" + printFundMkt
				+ ", printPortfolio=" + printPortfolio + ", branchNbr="
				+ branchNbr + ", aoCode=" + aoCode + " , printAllChart="+printAllChart+"]";
	}
		
}
