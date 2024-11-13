package com.systex.jbranch.app.server.fps.cam140;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class CAM140InputVO extends PagingInputVO{
	
	private String sfaParaID; // 名單參數維護-名單參數代碼
	private BigDecimal seqNo;

	private String camp_id;
	private String camp_name;
	private Date sDate;
	private Date eDate;
	private String channel;
	private String[] chkCode;
	private String source_id;
	private String type;
	private String exam_id;
	
	private String camp_desc;
	private String lead_para1;
	private String lead_para2;
	private String sales_pitch;
	private String gift_camp_id;
	
	private String action;
	private String camp_purpose;
	
	// 檔案
	private List<Map<String,String>> fileName;
	private List<Map<String,String>> realfileName;
	private String tempName;
	private String realTempName;
	private List<String> delId;
	
	// 回應選項
	private String enable;
	private String lead;
	private String mean;
	private String name;
	private String responseCode;

	// 問卷
	private String moduleCategory;
	private String questionVersion;
	private String examVersion;
	private String examName;
	private String[] questionVersionList;
	
	//
	private String interType;
		
	public String getInterType() {
		return interType;
	}

	public void setInterType(String interType) {
		this.interType = interType;
	}

	public String getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}

	public String[] getQuestionVersionList() {
		return questionVersionList;
	}

	public void setQuestionVersionList(String[] questionVersionList) {
		this.questionVersionList = questionVersionList;
	}

	public String getExamName() {
		return examName;
	}

	public void setExamName(String examName) {
		this.examName = examName;
	}

	public String getExamVersion() {
		return examVersion;
	}

	public void setExamVersion(String examVersion) {
		this.examVersion = examVersion;
	}

	public String getQuestionVersion() {
		return questionVersion;
	}

	public void setQuestionVersion(String questionVersion) {
		this.questionVersion = questionVersion;
	}

	public String getModuleCategory() {
		return moduleCategory;
	}

	public void setModuleCategory(String moduleCategory) {
		this.moduleCategory = moduleCategory;
	}

	public BigDecimal getSeqNo() {
		return seqNo;
	}

	public void setSeqNo(BigDecimal seqNo) {
		this.seqNo = seqNo;
	}

	public List<String> getDelId() {
		return delId;
	}

	public void setDelId(List<String> delId) {
		this.delId = delId;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getCamp_purpose() {
		return camp_purpose;
	}

	public void setCamp_purpose(String camp_purpose) {
		this.camp_purpose = camp_purpose;
	}

	public String getExam_id() {
		return exam_id;
	}

	public void setExam_id(String exam_id) {
		this.exam_id = exam_id;
	}

	public String getSfaParaID() {
		return sfaParaID;
	}

	public void setSfaParaID(String sfaParaID) {
		this.sfaParaID = sfaParaID;
	}

	public String getTempName() {
		return tempName;
	}

	public void setTempName(String tempName) {
		this.tempName = tempName;
	}

	public String getRealTempName() {
		return realTempName;
	}

	public void setRealTempName(String realTempName) {
		this.realTempName = realTempName;
	}

	public List<Map<String, String>> getFileName() {
		return fileName;
	}

	public void setFileName(List<Map<String, String>> fileName) {
		this.fileName = fileName;
	}

	public List<Map<String, String>> getRealfileName() {
		return realfileName;
	}

	public void setRealfileName(List<Map<String, String>> realfileName) {
		this.realfileName = realfileName;
	}

	public String getCamp_desc() {
		return camp_desc;
	}

	public void setCamp_desc(String camp_desc) {
		this.camp_desc = camp_desc;
	}

	public String getLead_para1() {
		return lead_para1;
	}

	public void setLead_para1(String lead_para1) {
		this.lead_para1 = lead_para1;
	}

	public String getLead_para2() {
		return lead_para2;
	}

	public void setLead_para2(String lead_para2) {
		this.lead_para2 = lead_para2;
	}

	public String getSales_pitch() {
		return sales_pitch;
	}

	public void setSales_pitch(String sales_pitch) {
		this.sales_pitch = sales_pitch;
	}

	public String getGift_camp_id() {
		return gift_camp_id;
	}

	public void setGift_camp_id(String gift_camp_id) {
		this.gift_camp_id = gift_camp_id;
	}

	public String getCamp_id() {
		return camp_id;
	}
	
	public void setCamp_id(String camp_id) {
		this.camp_id = camp_id;
	}
	
	public String getCamp_name() {
		return camp_name;
	}
	
	public void setCamp_name(String camp_name) {
		this.camp_name = camp_name;
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

	public String getChannel() {
		return channel;
	}
	
	public void setChannel(String channel) {
		this.channel = channel;
	}
	
	public String[] getChkCode() {
		return chkCode;
	}
	
	public void setChkCode(String[] chkCode) {
		this.chkCode = chkCode;
	}
	
	public String getSource_id() {
		return source_id;
	}
	
	public void setSource_id(String source_id) {
		this.source_id = source_id;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}

	public String getEnable() {
		return enable;
	}

	public void setEnable(String enable) {
		this.enable = enable;
	}

	public String getLead() {
		return lead;
	}

	public void setLead(String lead) {
		this.lead = lead;
	}

	public String getMean() {
		return mean;
	}

	public void setMean(String mean) {
		this.mean = mean;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
