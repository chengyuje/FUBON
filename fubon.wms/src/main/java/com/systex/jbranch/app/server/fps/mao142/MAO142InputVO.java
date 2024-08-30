package com.systex.jbranch.app.server.fps.mao142;


import java.util.Date;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class MAO142InputVO extends PagingInputVO{
	private String regionCenterId;
	private String bra_areaID;
	private String branchNbr;
	private String emp_id;
	private String reply_type;
	private Date use_date_bgn;
	private Date use_date_end;
	private String seq;
	private String dev_status;
	
	public String getBranchNbr() {
		return branchNbr;
	}
	public void setBranchNbr(String branchNbr) {
		this.branchNbr = branchNbr;
	}
	public String getRegionCenterId() {
		return regionCenterId;
	}
	public void setRegionCenterId(String regionCenterId) {
		this.regionCenterId = regionCenterId;
	}
	public String getBra_areaID() {
		return bra_areaID;
	}
	public void setBra_areaID(String bra_areaID) {
		this.bra_areaID = bra_areaID;
	}
	public String getEmp_id() {
		return emp_id;
	}
	public void setEmp_id(String emp_id) {
		this.emp_id = emp_id;
	}
	public Date getUse_date_bgn() {
		return use_date_bgn;
	}
	public void setUse_date_bgn(Date use_date_bgn) {
		this.use_date_bgn = use_date_bgn;
	}
	public Date getUse_date_end() {
		return use_date_end;
	}
	public void setUse_date_end(Date use_date_end) {
		this.use_date_end = use_date_end;
	}
	public String getReply_type() {
		return reply_type;
	}
	public void setReply_type(String reply_type) {
		this.reply_type = reply_type;
	}
	public String getSeq() {
		return seq;
	}
	public void setSeq(String seq) {
		this.seq = seq;
	}
	public String getDev_status() {
		return dev_status;
	}
	public void setDev_status(String dev_status) {
		this.dev_status = dev_status;
	}

}
