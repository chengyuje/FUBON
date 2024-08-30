package com.systex.jbranch.app.server.fps.cmfpg000;


public class LoginPageVO {

	private String authuid;
	private String iptAppUsername;
	private String iptAppUserPassword;
	private String iptAppUserRole;
	private String iptRACFUser_ID;
	private String iptRACF_Password;
	private String ddlBranchNo;		
	private Boolean kickOut;
	
	private String iptAppUserDeptID;
	private String iptAppUserRegioinCenterID;
	private String iptAppUserRegioinCenterName;
	private String iptAppUserBranchAreaID;
	private String iptAppUserBranchAreaName;
	private String iptAppUserBranchID;
	private String iptAppUserBranchName;
	private String iptAppUserIsPrimaryRole;
	private String loginSourceToken;
	private String currentUserId;
	
	public String getIptAppUserDeptID() {
		return iptAppUserDeptID;
	}
	public void setIptAppUserDeptID(String iptAppUserDeptID) {
		this.iptAppUserDeptID = iptAppUserDeptID;
	}
	public String getIptAppUserIsPrimaryRole() {
		return iptAppUserIsPrimaryRole;
	}
	public void setIptAppUserIsPrimaryRole(String iptAppUserIsPrimaryRole) {
		this.iptAppUserIsPrimaryRole = iptAppUserIsPrimaryRole;
	}
	public String getIptAppUserRegioinCenterName() {
		return iptAppUserRegioinCenterName;
	}
	public void setIptAppUserRegioinCenterName(String iptAppUserRegioinCenterName) {
		this.iptAppUserRegioinCenterName = iptAppUserRegioinCenterName;
	}
	public String getIptAppUserBranchAreaName() {
		return iptAppUserBranchAreaName;
	}
	public void setIptAppUserBranchAreaName(String iptAppUserBranchAreaName) {
		this.iptAppUserBranchAreaName = iptAppUserBranchAreaName;
	}
	public String getIptAppUserBranchName() {
		return iptAppUserBranchName;
	}
	public void setIptAppUserBranchName(String iptAppUserBranchName) {
		this.iptAppUserBranchName = iptAppUserBranchName;
	}
	public String getIptAppUserRegioinCenterID() {
		return iptAppUserRegioinCenterID;
	}
	public void setIptAppUserRegioinCenterID(String iptAppUserRegioinCenterID) {
		this.iptAppUserRegioinCenterID = iptAppUserRegioinCenterID;
	}
	public String getIptAppUserBranchAreaID() {
		return iptAppUserBranchAreaID;
	}
	public void setIptAppUserBranchAreaID(String iptAppUserBranchAreaID) {
		this.iptAppUserBranchAreaID = iptAppUserBranchAreaID;
	}
	public String getIptAppUserBranchID() {
		return iptAppUserBranchID;
	}
	public void setIptAppUserBranchID(String iptAppUserBranchID) {
		this.iptAppUserBranchID = iptAppUserBranchID;
	}
	public final String getAuthuid() {
		return authuid;
	}
	public final void setAuthuid(String authuid) {
		this.authuid = authuid;
	}
	public String getIptAppUsername() {
		return iptAppUsername;
	}
	public void setIptAppUsername(String AppUsername) {
		this.iptAppUsername = AppUsername;
	}
	public String getIptAppUserPassword() {
		return iptAppUserPassword;
	}
	public void setIptAppUserPassword(String AppUserPassword) {
		this.iptAppUserPassword = AppUserPassword;
	}
	public String getIptAppUserRole() {
		return iptAppUserRole;
	}
	public void setIptAppUserRole(String iptAppUserRole) {
		this.iptAppUserRole = iptAppUserRole;
	}
	public String getIptRACFUser_ID() {
		return iptRACFUser_ID;
	}
	public void setIptRACFUser_ID(String RACFUser_ID) {
		this.iptRACFUser_ID = RACFUser_ID;
	}
	public String getIptRACF_Password() {
		return iptRACF_Password;
	}
	public void setIptRACF_Password(String RACF_Password) {
		this.iptRACF_Password = RACF_Password;
	}
	public String getDdlBranchNo() {
		return ddlBranchNo;
	}
	public void setDdlBranchNo(String BranchNo) {
		this.ddlBranchNo = BranchNo;
	}
	public Boolean getKickOut() {
		return kickOut;
	}
	public void setKickOut(Boolean kickOut) {
		this.kickOut = kickOut;
	}
	public String getLoginSourceToken() {
		return loginSourceToken;
	}
	public void setLoginSourceToken(String loginSourceToken) {
		this.loginSourceToken = loginSourceToken;
	}
	public String getCurrentUserId() {
		return currentUserId;
	}
	public void setCurrentUserId(String currentUserId) {
		this.currentUserId = currentUserId;
	}
	
}
