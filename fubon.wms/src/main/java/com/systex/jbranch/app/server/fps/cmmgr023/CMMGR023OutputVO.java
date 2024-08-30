package com.systex.jbranch.app.server.fps.cmmgr023;


import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
/**
 * 排程監控_確認未來排程預計要Get的檔案是否存在
 *
 * @author SamTu
 * @date 2019/09/04
 */
@Component
@Scope("request")
public class CMMGR023OutputVO {
	
	private List recentSCHDMonitorList;
	private List hisSCHDMonitorList;
	private Date monitorTime;
	
	
	
	

	public Date getMonitorTime() {
		return monitorTime;
	}

	public void setMonitorTime(Date monitorTime) {
		this.monitorTime = monitorTime;
	}

	public List getHisSCHDMonitorList() {
		return hisSCHDMonitorList;
	}

	public void setHisSCHDMonitorList(List hisSCHDMonitorList) {
		this.hisSCHDMonitorList = hisSCHDMonitorList;
	}

	public List getRecentSCHDMonitorList() {
		return recentSCHDMonitorList;
	}

	public void setRecentSCHDMonitorList(List recentSCHDMonitorList) {
		this.recentSCHDMonitorList = recentSCHDMonitorList;
	}
	
	

}
