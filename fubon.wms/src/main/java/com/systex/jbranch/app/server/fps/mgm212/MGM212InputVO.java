package com.systex.jbranch.app.server.fps.mgm212;

import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class MGM212InputVO extends PagingInputVO {
	private String act_seq;				//活動代碼
	private String act_name;			//活動名稱
	private String mgm_cust_id;			//推薦人ID
	private String seq;					//案件序號
	private String points_type;			//點數類型
	private String release_status;		//狀態
	private String branch_nbr;			//分行
	
	private String appr_points;   		//修改後點數
	private String modify_reason;   	//修改原因
	private String evidence_name;   	//修改憑證
	private String real_evidence_name;	//修改憑證
	
	private String[] seq_list;			//給點案件序號(多筆)
	
	private List<Map<String, Object>> resultList;	//查詢結果下載
	
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

	public String getMgm_cust_id() {
		return mgm_cust_id;
	}
	
	public void setMgm_cust_id(String mgm_cust_id) {
		this.mgm_cust_id = mgm_cust_id;
	}
	
	public String getSeq() {
		return seq;
	}
	
	public void setSeq(String seq) {
		this.seq = seq;
	}
	
	public String getPoints_type() {
		return points_type;
	}
	
	public void setPoints_type(String points_type) {
		this.points_type = points_type;
	}
	
	public String getRelease_status() {
		return release_status;
	}
	
	public void setRelease_status(String release_status) {
		this.release_status = release_status;
	}
	
	public String getBranch_nbr() {
		return branch_nbr;
	}
	
	public void setBranch_nbr(String branch_nbr) {
		this.branch_nbr = branch_nbr;
	}

	public String getAppr_points() {
		return appr_points;
	}

	public void setAppr_points(String appr_points) {
		this.appr_points = appr_points;
	}

	public String getModify_reason() {
		return modify_reason;
	}

	public void setModify_reason(String modify_reason) {
		this.modify_reason = modify_reason;
	}

	public String getEvidence_name() {
		return evidence_name;
	}

	public void setEvidence_name(String evidence_name) {
		this.evidence_name = evidence_name;
	}

	public String getReal_evidence_name() {
		return real_evidence_name;
	}

	public void setReal_evidence_name(String real_evidence_name) {
		this.real_evidence_name = real_evidence_name;
	}

	public String[] getSeq_list() {
		return seq_list;
	}

	public void setSeq_list(String[] seq_list) {
		this.seq_list = seq_list;
	}

	public List<Map<String, Object>> getResultList() {
		return resultList;
	}

	public void setResultList(List<Map<String, Object>> resultList) {
		this.resultList = resultList;
	}
	
}
