package com.systex.jbranch.app.server.fps.prd230;

import java.sql.Date;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class PRD230InputVO extends PagingInputVO {
	private String prd_id;
	private String lipper_id;
	private String allot;
	private Date main_sDate;
	private Date main_eDate;
	private Date raise_sDate;
	private Date raise_eDate;
	private String ipo;
	private Date ipo_sDate;
	private Date ipo_eDate;
	private String yield;
	private String plus;
	private Date multi_sDate;
	private Date multi_eDate;
	private String cnr_discount;
	private String rate_discount;
	private String cnr_target;
	private Date cnrtar_sDate;
	private Date cnrtar_eDate;
	private String fee;
	private String fus40;
	private String purchase;
	private String out;
	private String ein;
	private String buyback;
	private String control;
	private String yreturn;
	private String std;
	private Date sDate;
	private Date eDate;
	private String lipper_rank;
	private String lipper_ben_id;
	private String status;
	private String selling;
	private String vigilant;
	
	private String passParams;   //首頁接參數
	
	private String ptype;
	
	private String fileName;
	private String fileRealName;
	
	private List<Map<String, Object>> review_list;
	private String stock_bond_type;
	
	private String warning; //警語
	private String fund_subject1; //主題代碼1
	private String fund_subject2; //主題代碼2
	private String fund_subject3; //主題代碼3
	private String fund_project1; //專案代碼1
	private String fund_project2; //專案代碼2
	private String customer_level; //客群代碼
	private String downloadParamType; //下載標籤對應的paramType
	
	public String getPrd_id() {
		return prd_id;
	}
	public void setPrd_id(String prd_id) {
		this.prd_id = prd_id;
	}
	public String getLipper_id() {
		return lipper_id;
	}
	public void setLipper_id(String lipper_id) {
		this.lipper_id = lipper_id;
	}
	public String getAllot() {
		return allot;
	}
	public void setAllot(String allot) {
		this.allot = allot;
	}
	public Date getMain_sDate() {
		return main_sDate;
	}
	public void setMain_sDate(Date main_sDate) {
		this.main_sDate = main_sDate;
	}
	public Date getMain_eDate() {
		return main_eDate;
	}
	public void setMain_eDate(Date main_eDate) {
		this.main_eDate = main_eDate;
	}
	public Date getRaise_sDate() {
		return raise_sDate;
	}
	public void setRaise_sDate(Date raise_sDate) {
		this.raise_sDate = raise_sDate;
	}
	public Date getRaise_eDate() {
		return raise_eDate;
	}
	public void setRaise_eDate(Date raise_eDate) {
		this.raise_eDate = raise_eDate;
	}
	public String getIpo() {
		return ipo;
	}
	public void setIpo(String ipo) {
		this.ipo = ipo;
	}
	public Date getIpo_sDate() {
		return ipo_sDate;
	}
	public void setIpo_sDate(Date ipo_sDate) {
		this.ipo_sDate = ipo_sDate;
	}
	public Date getIpo_eDate() {
		return ipo_eDate;
	}
	public void setIpo_eDate(Date ipo_eDate) {
		this.ipo_eDate = ipo_eDate;
	}
	public String getYield() {
		return yield;
	}
	public void setYield(String yield) {
		this.yield = yield;
	}
	public String getPlus() {
		return plus;
	}
	public void setPlus(String plus) {
		this.plus = plus;
	}
	public Date getMulti_sDate() {
		return multi_sDate;
	}
	public void setMulti_sDate(Date multi_sDate) {
		this.multi_sDate = multi_sDate;
	}
	public Date getMulti_eDate() {
		return multi_eDate;
	}
	public void setMulti_eDate(Date multi_eDate) {
		this.multi_eDate = multi_eDate;
	}
	public String getCnr_discount() {
		return cnr_discount;
	}
	public void setCnr_discount(String cnr_discount) {
		this.cnr_discount = cnr_discount;
	}
	public String getRate_discount() {
		return rate_discount;
	}
	public void setRate_discount(String rate_discount) {
		this.rate_discount = rate_discount;
	}
	public String getCnr_target() {
		return cnr_target;
	}
	public void setCnr_target(String cnr_target) {
		this.cnr_target = cnr_target;
	}
	public Date getCnrtar_sDate() {
		return cnrtar_sDate;
	}
	public void setCnrtar_sDate(Date cnrtar_sDate) {
		this.cnrtar_sDate = cnrtar_sDate;
	}
	public Date getCnrtar_eDate() {
		return cnrtar_eDate;
	}
	public void setCnrtar_eDate(Date cnrtar_eDate) {
		this.cnrtar_eDate = cnrtar_eDate;
	}
	public String getFee() {
		return fee;
	}
	public void setFee(String fee) {
		this.fee = fee;
	}
	public String getFus40() {
		return fus40;
	}
	public void setFus40(String fus40) {
		this.fus40 = fus40;
	}
	public String getPurchase() {
		return purchase;
	}
	public void setPurchase(String purchase) {
		this.purchase = purchase;
	}
	public String getOut() {
		return out;
	}
	public void setOut(String out) {
		this.out = out;
	}
	public String getEin() {
		return ein;
	}
	public void setEin(String ein) {
		this.ein = ein;
	}
	public String getBuyback() {
		return buyback;
	}
	public void setBuyback(String buyback) {
		this.buyback = buyback;
	}
	public String getControl() {
		return control;
	}
	public void setControl(String control) {
		this.control = control;
	}
	public String getYreturn() {
		return yreturn;
	}
	public void setYreturn(String yreturn) {
		this.yreturn = yreturn;
	}
	public String getStd() {
		return std;
	}
	public void setStd(String std) {
		this.std = std;
	}
	public Date getsDate() {
		return sDate;
	}
	public void setsDate(Date sDate) {
		this.sDate = sDate;
	}
	public Date geteDate() {
		return eDate;
	}
	public void seteDate(Date eDate) {
		this.eDate = eDate;
	}
	public String getLipper_rank() {
		return lipper_rank;
	}
	public void setLipper_rank(String lipper_rank) {
		this.lipper_rank = lipper_rank;
	}
	public String getLipper_ben_id() {
		return lipper_ben_id;
	}
	public void setLipper_ben_id(String lipper_ben_id) {
		this.lipper_ben_id = lipper_ben_id;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getSelling() {
		return selling;
	}
	public void setSelling(String selling) {
		this.selling = selling;
	}
	public String getPassParams() {
		return passParams;
	}
	public void setPassParams(String passParams) {
		this.passParams = passParams;
	}
	public String getPtype() {
		return ptype;
	}
	public void setPtype(String ptype) {
		this.ptype = ptype;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFileRealName() {
		return fileRealName;
	}
	public void setFileRealName(String fileRealName) {
		this.fileRealName = fileRealName;
	}
	public List<Map<String, Object>> getReview_list() {
		return review_list;
	}
	public void setReview_list(List<Map<String, Object>> review_list) {
		this.review_list = review_list;
	}
	public String getStock_bond_type() {
		return stock_bond_type;
	}
	public void setStock_bond_type(String stock_bond_type) {
		this.stock_bond_type = stock_bond_type;
	}
	public String getVigilant() {
		return vigilant;
	}
	public void setVigilant(String vigilant) {
		this.vigilant = vigilant;
	}
	public String getWarning() {
		return warning;
	}
	public void setWarning(String warning) {
		this.warning = warning;
	}
	public String getFund_subject1() {
		return fund_subject1;
	}
	public void setFund_subject1(String fund_subject1) {
		this.fund_subject1 = fund_subject1;
	}
	public String getFund_subject2() {
		return fund_subject2;
	}
	public void setFund_subject2(String fund_subject2) {
		this.fund_subject2 = fund_subject2;
	}
	public String getFund_subject3() {
		return fund_subject3;
	}
	public void setFund_subject3(String fund_subject3) {
		this.fund_subject3 = fund_subject3;
	}
	public String getFund_project1() {
		return fund_project1;
	}
	public void setFund_project1(String fund_project1) {
		this.fund_project1 = fund_project1;
	}
	public String getFund_project2() {
		return fund_project2;
	}
	public void setFund_project2(String fund_project2) {
		this.fund_project2 = fund_project2;
	}
	public String getCustomer_level() {
		return customer_level;
	}
	public void setCustomer_level(String customer_level) {
		this.customer_level = customer_level;
	}
	public String getDownloadParamType() {
		return downloadParamType;
	}
	public void setDownloadParamType(String downloadParamType) {
		this.downloadParamType = downloadParamType;
	}
	
	
}