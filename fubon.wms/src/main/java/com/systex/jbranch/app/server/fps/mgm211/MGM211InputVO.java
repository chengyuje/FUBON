package com.systex.jbranch.app.server.fps.mgm211;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class MGM211InputVO extends PagingInputVO {
	
	private String act_type;		//活動類型模組
	private String act_seq;			//活動代碼
	private String act_name;		//活動名稱
	private Date eff_date;			//生效日期
	private Date deadline;			//截止日期
	private Date exc_deadline;		//兌換截止日期
	private String act_content;		//活動內容
	private String act_approach;	//活動辦法
	private String precautions;		//注意事項
	
	private String file_name;
	private String actual_file_name;
	private String receipt_name;
	private String actual_receipt_name;
	private String mgm_form_name;
	private String real_mgm_form_name;
	private String be_mgm_form_name;
	private String real_be_mgm_form_name;
	private String formType;
	
	private List<Map<String,Object>> fileList;		//上傳活動附件
	private List<Map<String,String>> delFileSeq;	//刪除活動附件
	
	private List<Map<String,Object>> giftList;		//適用贈品清單
	private List<Map<String,Object>> listGiftNbr;	//VIP名單上傳之贈品代碼
	
	public String getAct_type() {
		return act_type;
	}
	
	public void setAct_type(String act_type) {
		this.act_type = act_type;
	}
	
	public String getAct_seq() {
		return act_seq;
	}
	
	public void setAct_seq(String act_seq) {
		this.act_seq = act_seq;
	}
	
	public String getAct_name() {
		return act_name;
	}
	
	public void setAct_name(String act_name) {
		this.act_name = act_name;
	}
	
	public Date getEff_date() {
		return eff_date;
	}
	
	public void setEff_date(Date eff_date) {
		this.eff_date = eff_date;
	}
	
	public Date getDeadline() {
		return deadline;
	}
	
	public void setDeadline(Date deadline) {
		this.deadline = deadline;
	}
	
	public Date getExc_deadline() {
		return exc_deadline;
	}
	
	public void setExc_deadline(Date exc_deadline) {
		this.exc_deadline = exc_deadline;
	}
	
	public String getAct_content() {
		return act_content;
	}
	
	public void setAct_content(String act_content) {
		this.act_content = act_content;
	}
	
	public String getAct_approach() {
		return act_approach;
	}
	
	public void setAct_approach(String act_approach) {
		this.act_approach = act_approach;
	}
	
	public String getPrecautions() {
		return precautions;
	}
	
	public void setPrecautions(String precautions) {
		this.precautions = precautions;
	}
	
	public String getFile_name() {
		return file_name;
	}
	
	public void setFile_name(String file_name) {
		this.file_name = file_name;
	}
	
	public String getActual_file_name() {
		return actual_file_name;
	}
	
	public void setActual_file_name(String actual_file_name) {
		this.actual_file_name = actual_file_name;
	}
	
	public String getReceipt_name() {
		return receipt_name;
	}
	
	public void setReceipt_name(String receipt_name) {
		this.receipt_name = receipt_name;
	}
	
	public String getActual_receipt_name() {
		return actual_receipt_name;
	}
	
	public void setActual_receipt_name(String actual_receipt_name) {
		this.actual_receipt_name = actual_receipt_name;
	}
	
	public String getMgm_form_name() {
		return mgm_form_name;
	}

	public void setMgm_form_name(String mgm_form_name) {
		this.mgm_form_name = mgm_form_name;
	}

	public String getReal_mgm_form_name() {
		return real_mgm_form_name;
	}

	public void setReal_mgm_form_name(String real_mgm_form_name) {
		this.real_mgm_form_name = real_mgm_form_name;
	}

	public String getBe_mgm_form_name() {
		return be_mgm_form_name;
	}

	public void setBe_mgm_form_name(String be_mgm_form_name) {
		this.be_mgm_form_name = be_mgm_form_name;
	}

	public String getReal_be_mgm_form_name() {
		return real_be_mgm_form_name;
	}

	public void setReal_be_mgm_form_name(String real_be_mgm_form_name) {
		this.real_be_mgm_form_name = real_be_mgm_form_name;
	}
	
	public String getFormType() {
		return formType;
	}

	public void setFormType(String formType) {
		this.formType = formType;
	}

	public List<Map<String, Object>> getFileList() {
		return fileList;
	}
	
	public void setFileList(List<Map<String, Object>> fileList) {
		this.fileList = fileList;
	}
	
	public List<Map<String, String>> getDelFileSeq() {
		return delFileSeq;
	}

	public void setDelFileSeq(List<Map<String, String>> delFileSeq) {
		this.delFileSeq = delFileSeq;
	}

	public List<Map<String, Object>> getGiftList() {
		return giftList;
	}
	
	public void setGiftList(List<Map<String, Object>> giftList) {
		this.giftList = giftList;
	}
	
	public List<Map<String, Object>> getListGiftNbr() {
		return listGiftNbr;
	}
	
	public void setListGiftNbr(List<Map<String, Object>> listGiftNbr) {
		this.listGiftNbr = listGiftNbr;
	}
	
}
