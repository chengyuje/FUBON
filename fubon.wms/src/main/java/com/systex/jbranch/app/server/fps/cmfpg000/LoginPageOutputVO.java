package com.systex.jbranch.app.server.fps.cmfpg000;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.platform.common.security.privilege.vo.ModuleDTO;

//import com.systex.jbranch.platform.common.security.privilege.vo.ItemDTO;


public class LoginPageOutputVO {

	private Map<String, Object> tempUserInfo; // 臨時員工（E.g. For 平測）
	private Map<?, ?> parameter;
	private String moduleId;

	//** append by William **//
	private Map<String, Object> userInfo;

	private String apServerName;
	private String loginID;
	private String loginName;
	private String loginBrh;
	private String loginBrhName;
	private String loginArea;
	private String loginAreaName;
	private String loginRegion;
	private String loginRegionName;
	private String loginRole;
	private String loginRoleName;
	private List<String> priID;
	private String aoCode;
	private String loginMsg;
	private boolean isHoliday;
	//** append by William **//

	private String url;
	private List<?> orderExpress;
	private List<?> speedFunction;
	private List<?> userList;	// 用作: 代理人清單
	private String name;
	private Date date;
	private List<?> functionList;
	private List<Map<String, String>> availRegionList;
	private List<Map<String, String>> availAreaList;
	private List<Map<String, String>> availBranchList;
	private String roleID;
	private String roleLevel;
	private Map<?, ?> configuration;
	private Map<String, ModuleDTO> moduleMap;
	private String menu;						//2011/4/18 add
	private Boolean blnCheckPermission=true;	//2011/5/27 add
	private String tlrSupv;
	private boolean tlrMoneyFlg; //櫃員備付現金Flag
	private String sysTime;
	private String loginSourceToken;
	private String currentUserId;

	// 20200525 add by ocean : WMS-CR-20200226-01_高端業管功能改採兼任分行角色調整相關功能
	private String memLoginFlag;

	public String getMemLoginFlag() {
		return memLoginFlag;
	}
	public void setMemLoginFlag(String memLoginFlag) {
		this.memLoginFlag = memLoginFlag;
	}
	public String getApServerName() {
		return apServerName;
	}
	public void setApServerName(String apServerName) {
		this.apServerName = apServerName;
	}
	public final Map<?, ?> getParameter() {
		return parameter;
	}
	public final void setParameter(Map<?, ?> parameter) {
		this.parameter = parameter;
	}
	public final String getModuleId() {
		return moduleId;
	}
	public final void setModuleId(String moduleId) {
		this.moduleId = moduleId;
	}
	public final Map<String, Object> getUserInfo() {
		return userInfo;
	}
	public final void setUserInfo(Map<String, Object> userInfo) {
		this.userInfo = userInfo;
	}
	public final String getLoginID() {
		return loginID;
	}
	public final void setLoginID(String loginID) {
		this.loginID = loginID;
	}
	public final String getLoginName() {
		return loginName;
	}
	public final void setLoginName(String loginName) {
		this.loginName = loginName;
	}
	public final String getLoginBrh() {
		return loginBrh;
	}
	public final void setLoginBrh(String loginBrh) {
		this.loginBrh = loginBrh;
	}
	public final String getLoginBrhName() {
		return loginBrhName;
	}
	public final void setLoginBrhName(String loginBrhName) {
		this.loginBrhName = loginBrhName;
	}
	public String getLoginArea() {
		return loginArea;
	}
	public void setLoginArea(String loginArea) {
		this.loginArea = loginArea;
	}
	public String getLoginAreaName() {
		return loginAreaName;
	}
	public void setLoginAreaName(String loginAreaName) {
		this.loginAreaName = loginAreaName;
	}
	public String getLoginRegion() {
		return loginRegion;
	}
	public void setLoginRegion(String loginRegion) {
		this.loginRegion = loginRegion;
	}
	public String getLoginRegionName() {
		return loginRegionName;
	}
	public void setLoginRegionName(String loginRegionName) {
		this.loginRegionName = loginRegionName;
	}
	public final String getLoginRole() {
		return loginRole;
	}
	public final void setLoginRole(String loginRole) {
		this.loginRole = loginRole;
	}
	public final String getLoginRoleName() {
		return loginRoleName;
	}
	public final void setLoginRoleName(String loginRoleName) {
		this.loginRoleName = loginRoleName;
	}
	public final String getAoCode() {
		return aoCode;
	}
	public final void setAoCode(String aoCode) {
		this.aoCode = aoCode;
	}
	public final String getLoginMsg() {
		return loginMsg;
	}
	public final void setLoginMsg(String loginMsg) {
		this.loginMsg = loginMsg;
	}
	public final boolean isHoliday() {
		return isHoliday;
	}
	public final void setHoliday(boolean isHoliday) {
		this.isHoliday = isHoliday;
	}
	public List<String> getPriID() {
		return priID;
	}
	public void setPriID(List<String> priID) {
		this.priID = priID;
	}
	public final String getUrl() {
		return url;
	}
	public final void setUrl(String url) {
		this.url = url;
	}
	public final List<?> getOrderExpress() {
		return orderExpress;
	}
	public final void setOrderExpress(List<?> orderExpress) {
		this.orderExpress = orderExpress;
	}
	public final List<?> getSpeedFunction() {
		return speedFunction;
	}
	public final void setSpeedFunction(List<?> speedFunction) {
		this.speedFunction = speedFunction;
	}
	public final List<?> getUserList() {
		return userList;
	}
	public final void setUserList(List<?> userList) {
		this.userList = userList;
	}
	public final String getName() {
		return name;
	}
	public final void setName(String name) {
		this.name = name;
	}
	public final Date getDate() {
		return date;
	}
	public final void setDate(Date date) {
		this.date = date;
	}
	public final List<?> getFunctionList() {
		return functionList;
	}
	public final void setFunctionList(List<?> functionList) {
		this.functionList = functionList;
	}
	public List<Map<String, String>> getAvailRegionList() {
		return availRegionList;
	}
	public void setAvailRegionList(List<Map<String, String>> availRegionList) {
		this.availRegionList = availRegionList;
	}
	public final List<Map<String, String>> getAvailAreaList() {
		return availAreaList;
	}
	public final void setAvailAreaList(List<Map<String, String>> availAreaList) {
		this.availAreaList = availAreaList;
	}
	public final List<Map<String, String>> getAvailBranchList() {
		return availBranchList;
	}
	public final void setAvailBranchList(List<Map<String, String>> availBranchList) {
		this.availBranchList = availBranchList;
	}
	public final String getRoleID() {
		return roleID;
	}
	public final void setRoleID(String roleID) {
		this.roleID = roleID;
	}
	public final String getRoleLevel() {
		return roleLevel;
	}
	public final void setRoleLevel(String roleLevel) {
		this.roleLevel = roleLevel;
	}
	public final Map<?, ?> getConfiguration() {
		return configuration;
	}
	public final void setConfiguration(Map<?, ?> configuration) {
		this.configuration = configuration;
	}
	public final Map<String, ModuleDTO> getModuleMap() {
		return moduleMap;
	}
	public final void setModuleMap(Map<String, ModuleDTO> moduleMap) {
		this.moduleMap = moduleMap;
	}
	public final String getMenu() {
		return menu;
	}
	public final void setMenu(String menu) {
		this.menu = menu;
	}
	public final Boolean getBlnCheckPermission() {
		return blnCheckPermission;
	}
	public final void setBlnCheckPermission(Boolean blnCheckPermission) {
		this.blnCheckPermission = blnCheckPermission;
	}
	public final String getTlrSupv() {
		return tlrSupv;
	}
	public final void setTlrSupv(String tlrSupv) {
		this.tlrSupv = tlrSupv;
	}
	public final boolean isTlrMoneyFlg() {
		return tlrMoneyFlg;
	}
	public final void setTlrMoneyFlg(boolean tlrMoneyFlg) {
		this.tlrMoneyFlg = tlrMoneyFlg;
	}
	public final String getSysTime() {
		return sysTime;
	}
	public final void setSysTime(String sysTime) {
		this.sysTime = sysTime;
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

	public Map<String, Object> getTempUserInfo() {
		return tempUserInfo;
	}

	public void setTempUserInfo(Map<String, Object> tempUserInfo) {
		this.tempUserInfo = tempUserInfo;
	}
}

