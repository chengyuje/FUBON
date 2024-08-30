package com.systex.jbranch.app.server.fps.crm671;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class CRM671InputVO extends PagingInputVO {
	
	private String seq;
	private String visitor_role;
	private String cust_id;
	private String pri_id;
	private String cmu_type;
	private String visit_memo;
	private String rec_text_format;
	private List<Map<String, Object>> statusList;
	private List<String> seqList;

	private Date visit_date;
	private Date visit_time;
	private String visit_creply;
	
	public Date getVisit_date() {
		return visit_date;
	}

	public void setVisit_date(Date visit_date) {
		this.visit_date = visit_date;
	}

	public Date getVisit_time() {
		return visit_time;
	}

	public void setVisit_time(Date visit_time) {
		this.visit_time = visit_time;
	}

	public String getVisit_creply() {
		return visit_creply;
	}

	public void setVisit_creply(String visit_creply) {
		this.visit_creply = visit_creply;
	}

	public String getCust_id() {
		return cust_id;
	}

	public void setCust_id(String cust_id) {
		this.cust_id = cust_id;
	}

	public String getPri_id() {
		return pri_id;
	}

	public void setPri_id(String pri_id) {
		this.pri_id = pri_id;
	}

	public List<Map<String, Object>> getStatusList() {
		return statusList;
	}

	public void setStatusList(List<Map<String, Object>> statusList) {
		this.statusList = statusList;
	}

	public String getCmu_type() {
		return cmu_type;
	}

	public void setCmu_type(String cmu_type) {
		this.cmu_type = cmu_type;
	}

	public String getVisit_memo() {
		return visit_memo;
	}

	public void setVisit_memo(String visit_memo) {
		this.visit_memo = visit_memo;
	}

	public String getRec_text_format() {
		return rec_text_format;
	}

	public void setRec_text_format(String rec_text_format) {
		this.rec_text_format = rec_text_format;
	}

	public String getSeq() {
		return seq;
	}

	public void setSeq(String seq) {
		this.seq = seq;
	}

	public String getVisitor_role() {
		return visitor_role;
	}

	public void setVisitor_role(String visitor_role) {
		this.visitor_role = visitor_role;
	}

	public List<String> getSeqList() {
		return seqList;
	}

	public void setSeqList(List<String> seqList) {
		this.seqList = seqList;
	}

}
