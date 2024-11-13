package com.systex.jbranch.app.server.fps.cam130;

import java.math.BigDecimal;
import java.sql.Date;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class CAM130InputVO extends PagingInputVO {
	
	private BigDecimal seq;
	private String cam_id;
	private String cam_name;
	private Date sCreDate;
	private Date eCreDate;
	private String source_id;
	private String channel;
	private Date sStaDate;
	private Date eStaDate;
	private String type;
	private String camp_purpose;
	private String status;
	private Date sEndDate;
	private Date eEndDate;
	
	private String expType;

	public String getExpType() {
		return expType;
	}

	public void setExpType(String expType) {
		this.expType = expType;
	}

	public BigDecimal getSeq() {
		return seq;
	}

	public void setSeq(BigDecimal seq) {
		this.seq = seq;
	}

	public String getCam_id() {
		return cam_id;
	}

	public void setCam_id(String cam_id) {
		this.cam_id = cam_id;
	}

	public String getCam_name() {
		return cam_name;
	}

	public void setCam_name(String cam_name) {
		this.cam_name = cam_name;
	}

	public Date getsCreDate() {
		return sCreDate;
	}

	public void setsCreDate(Date sCreDate) {
		this.sCreDate = sCreDate;
	}

	public Date geteCreDate() {
		return eCreDate;
	}

	public void seteCreDate(Date eCreDate) {
		this.eCreDate = eCreDate;
	}

	public String getSource_id() {
		return source_id;
	}

	public void setSource_id(String source_id) {
		this.source_id = source_id;
	}

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public Date getsStaDate() {
		return sStaDate;
	}

	public void setsStaDate(Date sStaDate) {
		this.sStaDate = sStaDate;
	}

	public Date geteStaDate() {
		return eStaDate;
	}

	public void seteStaDate(Date eStaDate) {
		this.eStaDate = eStaDate;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCamp_purpose() {
		return camp_purpose;
	}

	public void setCamp_purpose(String camp_purpose) {
		this.camp_purpose = camp_purpose;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getsEndDate() {
		return sEndDate;
	}

	public void setsEndDate(Date sEndDate) {
		this.sEndDate = sEndDate;
	}

	public Date geteEndDate() {
		return eEndDate;
	}

	public void seteEndDate(Date eEndDate) {
		this.eEndDate = eEndDate;
	}
}
