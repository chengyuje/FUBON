package com.systex.jbranch.app.server.fps.pms362;

import java.util.Date;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

public class PMS362InputVO extends PagingInputVO{	
	private String srchType;   //分行各項商品收益核實版、分行各項商品銷量核實版、專員業績戰報資料
	private Date sTimes;       //戰報統計月份起月
	private Date eTimes;       //戰報統計月份迄月
	private Date time;         //戰報上傳日期
	private String fileName;   
	private String realfileName;
	
	public String getSrchType() {
		return srchType;
	}
	public void setSrchType(String srchType) {
		this.srchType = srchType;
	}
	public Date getsTimes() {
		return sTimes;
	}
	public void setsTimes(Date sTimes) {
		this.sTimes = sTimes;
	}
	public Date geteTimes() {
		return eTimes;
	}
	public void seteTimes(Date eTimes) {
		this.eTimes = eTimes;
	}
	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time = time;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getRealfileName() {
		return realfileName;
	}
	public void setRealfileName(String realfileName) {
		this.realfileName = realfileName;
	}
	
}
