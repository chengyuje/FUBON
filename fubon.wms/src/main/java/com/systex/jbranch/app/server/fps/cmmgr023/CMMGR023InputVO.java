package com.systex.jbranch.app.server.fps.cmmgr023;

/**
 * 排程監控_確認未來排程預計要Get的檔案是否存在
 *
 * @author SamTu
 * @date 2019/09/04
 */
import java.util.Date;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


@Component
@Scope("request")
public class CMMGR023InputVO {
	
	private Date searchDate;
	private String SCHDID;
	private String JOBID;
	
	
	public Date getSearchDate() {
		return searchDate;
	}
	public void setSearchDate(Date searchDate) {
		this.searchDate = searchDate;
	}
	
	public String getSCHDID() {
		return SCHDID;
	}
	public void setSCHDID(String sCHDID) {
		SCHDID = sCHDID;
	}
	public String getJOBID() {
		return JOBID;
	}
	public void setJOBID(String jOBID) {
		JOBID = jOBID;
	}
	
	

}
