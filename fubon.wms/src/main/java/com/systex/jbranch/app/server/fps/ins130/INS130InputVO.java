package com.systex.jbranch.app.server.fps.ins130;

import java.math.BigDecimal;
import java.util.Date;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class INS130InputVO extends PagingInputVO {
	private String custId;					//被保人
	private String partnerName;				//配偶(家庭成員)姓名            
	private Date partnerBirthDate;		//配偶(家庭成員)姓名            
	private String partnerGender;			//配偶(家庭成員)姓名            
	private BigDecimal livingExp;			//每月家庭基本生活支出(萬元)
	private BigDecimal notlivingExp;		//每月家庭非必要生活支出(萬元)
	private BigDecimal custLivingFee;		//本人生活費用比
	private BigDecimal childLivingFee;	//子女經濟獨立生活比
	private BigDecimal houDebtAmt;			//每月房貸攤還金額(萬元)
	private BigDecimal houDebtY;			//需幾年還清房貸
	private BigDecimal eduAmt;				//子女教育費用(萬元)
	private BigDecimal carDebtAmt;			//車貸(萬元)
	private BigDecimal carDebtY;			//車貸還需幾年還清
	private BigDecimal cardDebtAmt;			//信貸(萬元)
	private BigDecimal cardDebtY;			//信貸還需幾年還清
//	private BigDecimal stockDebtAmt;		//股票融資(萬元)
	private BigDecimal otherDebtAmt;		//其它投資性融資(萬元)
	private BigDecimal taxIn;				//遺產稅(萬元)
	private BigDecimal income;				//本人年薪(萬元)
	private BigDecimal couIncome;			//配偶年薪(萬元)
	private BigDecimal rentIncome;			//房租收入(萬元)
	private BigDecimal cashAmt;				//現金及活期存款(萬元)
	private BigDecimal stockAmt;			//現有資產-股票(萬元)
	private BigDecimal ctAmt;				//現有資產-定存(萬元)
	private BigDecimal fundAmt;				//現有資產-基金(萬元)
	private BigDecimal snAmt;				//現有資產-連動債(萬元)
	private BigDecimal investInsAmt;		//現有資產-投資型保單(萬元)
	private BigDecimal selfImmoveAmt;		//自用不動產(萬元)
	private BigDecimal investImmoveAmt;		//房地產投資(萬元)
	private BigDecimal trustAmt;			//信託資產(萬元)
	
	//add by Brian
	private String paraType;                //家庭財務安全類型判斷 : A.被保人生活費用, B.子女生活費用, C.子女教育基金
	
	public String getCustId() {
		return custId;
	}
	public void setCustId(String custId) {
		this.custId = custId;
	}
	
	public String getPartnerName() {
		return partnerName;
	}
	public void setPartnerName(String partnerName) {
		this.partnerName = partnerName;
	}
	public Date getPartnerBirthDate() {
		return partnerBirthDate;
	}
	public void setPartnerBirthDate(Date partnerBirthDate) {
		this.partnerBirthDate = partnerBirthDate;
	}
	public String getPartnerGender() {
		return partnerGender;
	}
	public void setPartnerGender(String partnerGender) {
		this.partnerGender = partnerGender;
	}
	public BigDecimal getLivingExp() {
		return livingExp;
	}
	public void setLivingExp(BigDecimal livingExp) {
		this.livingExp = livingExp;
	}
	
	public BigDecimal getNotlivingExp() {
		return notlivingExp;
	}
	public void setNotlivingExp(BigDecimal notlivingExp) {
		this.notlivingExp = notlivingExp;
	}
	
	public BigDecimal getCustLivingFee() {
		return custLivingFee;
	}
	public void setCustLivingFee(BigDecimal custLivingFee) {
		this.custLivingFee = custLivingFee;
	}
	
	public BigDecimal getChildLivingFee() {
		return childLivingFee;
	}
	public void setChildLivingFee(BigDecimal childLivingFee) {
		this.childLivingFee = childLivingFee;
	}
	
	public BigDecimal getHouDebtAmt() {
		return houDebtAmt;
	}
	public void setHouDebtAmt(BigDecimal houDebtAmt) {
		this.houDebtAmt = houDebtAmt;
	}
	
	public BigDecimal getHouDebtY() {
		return houDebtY;
	}
	public void setHouDebtY(BigDecimal houDebtY) {
		this.houDebtY = houDebtY;
	}
	
	public BigDecimal getEduAmt() {
		return eduAmt;
	}
	public void setEduAmt(BigDecimal eduAmt) {
		this.eduAmt = eduAmt;
	}
	
	public BigDecimal getCarDebtAmt() {
		return carDebtAmt;
	}
	public void setCarDebtAmt(BigDecimal carDebtAmt) {
		this.carDebtAmt = carDebtAmt;
	}
	
	public BigDecimal getCarDebtY() {
		return carDebtY;
	}
	public void setCarDebtY(BigDecimal carDebtY) {
		this.carDebtY = carDebtY;
	}
	
	public BigDecimal getCardDebtAmt() {
		return cardDebtAmt;
	}
	public void setCardDebtAmt(BigDecimal cardDebtAmt) {
		this.cardDebtAmt = cardDebtAmt;
	}
	
	public BigDecimal getCardDebtY() {
		return cardDebtY;
	}
	public void setCardDebtY(BigDecimal cardDebtY) {
		this.cardDebtY = cardDebtY;
	}
	
	public BigDecimal getOtherDebtAmt() {
		return otherDebtAmt;
	}
	public void setOtherDebtAmt(BigDecimal otherDebtAmt) {
		this.otherDebtAmt = otherDebtAmt;
	}
	
	public BigDecimal getTaxIn() {
		return taxIn;
	}
	public void setTaxIn(BigDecimal taxIn) {
		this.taxIn = taxIn;
	}
	
	public BigDecimal getIncome() {
		return income;
	}
	public void setIncome(BigDecimal income) {
		this.income = income;
	}
	
	public BigDecimal getCouIncome() {
		return couIncome;
	}
	public void setCouIncome(BigDecimal couIncome) {
		this.couIncome = couIncome;
	}
	
	public BigDecimal getRentIncome() {
		return rentIncome;
	}
	public void setRentIncome(BigDecimal rentIncome) {
		this.rentIncome = rentIncome;
	}
	
	public BigDecimal getCashAmt() {
		return cashAmt;
	}
	public void setCashAmt(BigDecimal cashAmt) {
		this.cashAmt = cashAmt;
	}
	
	public BigDecimal getStockAmt() {
		return stockAmt;
	}
	public void setStockAmt(BigDecimal stockAmt) {
		this.stockAmt = stockAmt;
	}
	
	public BigDecimal getCtAmt() {
		return ctAmt;
	}
	public void setCtAmt(BigDecimal ctAmt) {
		this.ctAmt = ctAmt;
	}
	
	public BigDecimal getFundAmt() {
		return fundAmt;
	}
	public void setFundAmt(BigDecimal fundAmt) {
		this.fundAmt = fundAmt;
	}
	
	public BigDecimal getSnAmt() {
		return snAmt;
	}
	public void setSnAmt(BigDecimal snAmt) {
		this.snAmt = snAmt;
	}
	
	public BigDecimal getInvestInsAmt() {
		return investInsAmt;
	}
	public void setInvestInsAmt(BigDecimal investInsAmt) {
		this.investInsAmt = investInsAmt;
	}
	
	public BigDecimal getSelfImmoveAmt() {
		return selfImmoveAmt;
	}
	public void setSelfImmoveAmt(BigDecimal selfImmoveAmt) {
		this.selfImmoveAmt = selfImmoveAmt;
	}
	
	public BigDecimal getInvestImmoveAmt() {
		return investImmoveAmt;
	}
	public void setInvestImmoveAmt(BigDecimal investImmoveAmt) {
		this.investImmoveAmt = investImmoveAmt;
	}
	
	public BigDecimal getTrustAmt() {
		return trustAmt;
	}
	public void setTrustAmt(BigDecimal trustAmt) {
		this.trustAmt = trustAmt;
	}
	
	public String getParaType() {
		return paraType;
	}
	public void setParaType(String paraType) {
		this.paraType = paraType;
	}
		
	@Override
	public String toString() {
		return "INS130InputVO [custId=" + custId + ", livingExp=" + livingExp
				+ ", notlivingExp=" + notlivingExp + ", custLivingFee="
				+ custLivingFee + ", childLivingFee="
				+ childLivingFee + ", houDebtAmt=" + houDebtAmt
				+ ", houDebtY=" + houDebtY + ", eduAmt=" + eduAmt
				+ ", carDebtAmt=" + carDebtAmt + ", carDebtY=" + carDebtY
				+ ", cardDebtAmt=" + cardDebtAmt + ", cardDebtY=" + cardDebtY
				+ ", otherDebtAmt="
				+ otherDebtAmt + ", taxIn=" + taxIn + ", income=" + income
				+ ", couIncome=" + couIncome + ", rentIncome=" + rentIncome
				+ ", cashAmt=" + cashAmt + ", stockAmt=" + stockAmt
				+ ", ctAmt=" + ctAmt + ", fundAmt=" + fundAmt + ", snAmt="
				+ snAmt + ", investInsAmt=" + investInsAmt + ", selfImmoveAmt="
				+ selfImmoveAmt + ", investImmoveAmt=" + investImmoveAmt
				+ ", trustAmt=" + trustAmt + "]";
	}	
		
}
