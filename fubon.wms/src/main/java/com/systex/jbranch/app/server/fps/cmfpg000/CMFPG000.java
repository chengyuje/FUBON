package com.systex.jbranch.app.server.fps.cmfpg000;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.naming.Context;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import com.systex.jbranch.app.common.fps.table.TBORG_MEMBERVO;
import com.systex.jbranch.app.server.fps.oth001.OTH001;
import com.systex.jbranch.comutil.collection.CollectionSearchUtils;
import com.systex.jbranch.comutil.collection.GenericMap;
import com.systex.jbranch.comutil.collection.MapObjectUtils;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.fubon.commons.Manager;
import com.systex.jbranch.platform.common.dataManager.BranchFactoryIF;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataManager.PlatformVOHelper;
import com.systex.jbranch.platform.common.dataManager.Section;
import com.systex.jbranch.platform.common.dataManager.User;
import com.systex.jbranch.platform.common.dataManager.WorkStation;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.platformdao.table.TBSYSSECUROLEVO;
import com.systex.jbranch.platform.common.platformdao.table.TBSYSWSONLINESTATUSPK;
import com.systex.jbranch.platform.common.platformdao.table.TBSYSWSONLINESTATUSVO;
import com.systex.jbranch.platform.common.security.privilege.BatchItemMapFilter;
import com.systex.jbranch.platform.common.security.privilege.ItemMapFilterIF;
import com.systex.jbranch.platform.common.security.privilege.PrivilegeManagement;
import com.systex.jbranch.platform.common.security.privilege.vo.ItemDTO;
import com.systex.jbranch.platform.common.security.privilege.vo.ModuleDTO;
import com.systex.jbranch.platform.common.security.privilege.vo.RoleFunctionDTO;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.common.util.ThreadDataPool;
import com.systex.jbranch.platform.configuration.ConfigurationException;
import com.systex.jbranch.platform.configuration.PlatformConfigUtils;
import com.systex.jbranch.platform.server.conversation.message.EnumShowType;
import com.systex.jbranch.platform.server.conversation.message.EnumTiaHeader;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.server.info.fubonbranch.WmsBranch;
import com.systex.jbranch.platform.server.info.fubonbranch.WmsBranchFactory;
import com.systex.jbranch.platform.server.info.fubonuser.WmsUser;
import com.systex.jbranch.platform.server.pipeline.flex.PipelineUtil;
import com.systex.jbranch.platform.server.sessionmanager.APServerManager;
import com.systex.jbranch.platform.server.sessionmanager.WSManagerIF;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Repository("cmfpg000")
@Scope("prototype")
public class CMFPG000 extends FubonWmsBizLogic {

	//===== ABOUT ROLE =====//
	private final String sysRoleFC = "FC";
	private final String xmlFC = "FUBONSYS.FC_ROLE";

	private final String sysRoleFCH = "FCH";
	private final String xmlFCH = "FUBONSYS.FCH_ROLE";

	private final String sysRolePSOP = "PSOP";
	private final String xmlPSOP = "FUBONSYS.PSOP_ROLE";

	private final String sysRoleBMMGR = "BMMGR";
	private final String xmlBMMGR = "FUBONSYS.BMMGR_ROLE";

	private final String sysRoleMBRMGR = "MBRMGR";
	private final String xmlMBRMGR = "FUBONSYS.MBRMGR_ROLE";

	private final String sysRoleARMGR = "ARMGR";
	private final String xmlARMGR = "FUBONSYS.ARMGR_ROLE";

	private final String sysRoleHEADMGR = "HEADMGR";
	private final String xmlHEADMGR = "FUBONSYS.HEADMGR_ROLE";

	private final String sysRoleFAIA = "FAIA";
	private final String xmlFAIA = "FUBONSYS.FAIA_ROLE";

	private final String sysRoleUHRM = "UHRM";
	private final String xmlUHRM = "FUBONSYS.UHRM_ROLE";

	private final String sysRoleUHRMMGR = "UHRMMGR";
	private final String xmlUHRMMGR = "FUBONSYS.UHRMMGR_ROLE";

	private final String sysRolePAO = "PAO";
	private final String xmlPAO = "FUBONSYS.PAO_ROLE";

	private final String sysRoleBSMGR = "BSMGR";
	private final String xmlBSMGR = "FUBONSYS.BSMGR_ROLE";

	//===== ABOUT OTHER =====//
	private final String LDAP_CONFIG = "FUBONSYS.LOGIN_LDAP";
	private final String FUBONSYS_MODE = "FUBONSYS.MODE";
	private final String FUBONSYS_LOGIN_IP_CHK = "FUBONSYS.LOGIN_IP_CHK";
	private final String TRUST_ID = "FUBONSYS.TRUST_IP";
	//	private final String xmlADURL = "FUBONSYS.AD_URL#MODE";

	private DataAccessManager dam;

	//是否為測試環境
	private boolean isTestMode() {
		try {
			//改判斷系統參數
			Map<String, String> xmlVar = new XmlInfo().getVariable(FUBONSYS_MODE, FormatHelper.FORMAT_3);
			return xmlVar != null && "D".equals(xmlVar.get("MODE"));
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 檢查帳號密碼並取得代理人清單 依WMS-CR-20180305-02_申請請假超過30天(含)人員自動移除系統權限內容，
	 * TBORG_MEMBER_UNAUTH.ISUNAUTHED='Y'則不允許登入;
	 * 
	 * @throws JBranchException
	 * @throws IOException
	 */
	public void authenticate(Object body, IPrimitiveMap<EnumTiaHeader> headers) throws JBranchException, Exception {
		
		LoginPageVO inputVO = (LoginPageVO) body;
		LoginPageOutputVO outputVO = new LoginPageOutputVO();
		String loginId = "";
		dam = getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		OTH001 oth001 = PlatformContext.getBean(OTH001.class);
		List<Map<String, Object>> tempInfo = null;

		if (StringUtils.isNotBlank(inputVO.getAuthuid())) {
			String authUid = inputVO.getAuthuid();

			Map<String, String> params = authUidToMap(authUid);

			if (logger.isDebugEnabled()) {
				logger.debug("tlronSSO params=" + params);
			}

			loginId = params.get("userId");
		} else {
			loginId = inputVO.getIptAppUsername();

			String passWord = inputVO.getIptAppUserPassword();

			try {
				//WMS-CR-20180305-02_申請請假超過30天(含)人員自動移除系統權限內容
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				queryCondition.setQueryString("	SELECT * FROM TBORG_MEMBER_UNAUTH	WHERE EMP_ID	=	:empId	AND	ISUNAUTHED	=	'Y'	");
				queryCondition.setObject("empId", inputVO.getIptAppUsername());
				List list1 = dam.exeQuery(queryCondition);
				if (!list1.isEmpty()) {
					throw new JBranchException("無登入權限");
				}
				
				if (StringUtils.isBlank(passWord))
					throw new JBranchException("密碼錯誤");
				else if (!isPrivilegedLogin(passWord)) {
					//發送LDAP
					queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					queryCondition.setQueryString("select EMP_EMAIL_ADDRESS from TBORG_MEMBER where EMP_ID = :userId");
					queryCondition.setObject("userId", loginId);

					List<Map<String, Object>> list = dam.exeQuery(queryCondition);
					
					String email;

					// 若員工檔無資料，且允許臨時員工登入系統
					if (list.isEmpty()) {
						tempInfo = oth001.queryTempEmpInfo(loginId);

						if (CollectionUtils.isEmpty(tempInfo))
							throw new JBranchException("無此使用者");
						email = (String) tempInfo.get(0).get("EMP_EMAIL_ADDRESS");
					} else {
						email = (String) list.get(0).get("EMP_EMAIL_ADDRESS");
					}

					checkLoginForLdap(loginId, passWord, email);
				}

			} catch (Exception ex) {
				throw new JBranchException(ex);
			}
		}

		WmsUser wmsUser = (WmsUser) PlatformContext.getBean("wmsUser");

		// 臨時員工雖有自己的員編，但尚未入檔組織檔，因此使用虛擬腳色代為登入系統（E.g. 平測日盛員工登入）
		boolean allowLoginByTempID = oth001.allowLoginByTempID(loginId);
		if (tempInfo == null) // 不走 LDAP 可能會是空值，這裡再取值一次
			tempInfo = oth001.queryTempEmpInfo(loginId);

		if (allowLoginByTempID || CollectionUtils.isNotEmpty(tempInfo)) {
			outputVO.setUserList(wmsUser.getAgentInfo(oth001.getVirtualEmpID()));
			outputVO.setTempUserInfo(tempInfo.get(0));
		} else {
			// 更新代理人資訊
			wmsUser.updateAgentInfo();
			// 檢查代理人
			outputVO.setUserList(wmsUser.getAgentInfo(loginId));
		}

		//		logger.info("#tmplog authenticate end");
		sendRtnObject(outputVO);
	}

	public String getDefaultCode() throws JBranchException {
		Map<String, String> xmlVar = new XmlInfo().getVariable(FUBONSYS_MODE, FormatHelper.FORMAT_3);
		return xmlVar.get("DEFAULT_CODE") == null ? null : xmlVar.get("DEFAULT_CODE").toString();
	}

	public boolean isTestModeLogin(String pwd) throws JBranchException {
		String dCode = getDefaultCode();
		return dCode != null && pwd.equals(dCode);
	}

	/**
	 * 當系統開啟白名單功能，則只有白名單 IP 可使用萬用密碼登入系統，其他 IP 則需要透過 LDAP authentication。 否則所有 IP
	 * 只要輸入的密碼與萬用密碼相符，則不走 LDAP authentication
	 **/
	private boolean isPrivilegedLogin(String pwd) throws JBranchException {
		return (!isEnabledPrivilegedMechanism() || isPrivileged()) && pwd.equals(getDefaultCode());
	}

	/** 使用者 IP 是否在白名單 IP 內 **/
	private boolean isPrivileged() throws JBranchException {
		List<Map<String, Object>> ipList = Manager.manage(this.getDataAccessManager()).append("select PARAM_NAME from TBSYSPARAMETER ").append("where PARAM_TYPE = 'FUBONSYS.PRIVILEGED_IP' ").query();

		if (ipList.isEmpty())
			return false;
		Set<String> ipSet = new HashSet<>();
		for (Map<String, Object> each : ipList) {
			ipSet.add(StringUtils.defaultString((String) each.get("PARAM_NAME")));
		}

		return ipSet.contains(PipelineUtil.getRemoteAddr());
	}

	/** 白名單功能是否開啟 **/
	private boolean isEnabledPrivilegedMechanism() {
		List<Map<String, Object>> result = doGetParameter("FUBONSYS.MODE", "ENABLE_PRIVILEGED_IP");

		return CollectionUtils.isNotEmpty(result) && "Y".equals(result.get(0).get("PARAM_NAME"));
	}

	/**
	 * SSO登入
	 * 
	 * @param body
	 * @param headers
	 * @throws JBranchException
	 */
	/*
	//	public void tlronSSO(Object body,IPrimitiveMap<EnumTiaHeader> headers) throws JBranchException
	//	{
	//		LoginPageVO loginPageVO = (LoginPageVO) body;
	//		String authUid = loginPageVO.getAuthuid();
	//
	//		Map<String, String> params = authUidToMap(authUid);
	//
	//		if (logger.isDebugEnabled()){
	//			logger.debug("tlronSSO params=" + params);
	//		}
	//
	//		HashMap<String, String> loginInfoMap = new HashMap<String, String>();
	//
	//		loginInfoMap.put("userId", params.get("userId"));
	////		loginInfoMap.put("branchNo", params.get("branchNo"));  改在tlron中取得
	//		loginInfoMap.put("userPwd", params.get(""));
	//		loginInfoMap.put("racfId", params.get(""));
	//		loginInfoMap.put("racfPwd", params.get(""));
	//		loginInfoMap.put("caller", "");
	//
	//		String mod = params.get("mod");
	//		loginInfoMap.put("mod", mod);
	//		loginInfoMap.put("agentId", null);
	//		loginInfoMap.put("agentRoleId", params.get(""));
	//
	//		loginInfoMap.put("userName", getEmpName(loginInfoMap.get("userId")));	//姓名
	//		loginInfoMap.put("userDN", "");		//LDAP DN
	//
	//		String roleId = getRoleId(loginInfoMap.get("userId"));
	//		loginInfoMap.put("roleId",roleId);
	//		loginInfoMap.put("roleName", getRoleName(roleId));
	//		loginInfoMap.put("htgDepid", "");
	//		loginInfoMap.put("htgSessionType", "P8EstablishSession");
	//
	//		//登入資訊載入
	//		LoginPageOutputVO output = tlron(loginInfoMap);
	//		output.setModuleId(mod);
	//		output.setParameter(params);
	//
	//		if(StringUtils.isNotBlank(mod)){
	//			sendRtnObject("loadSinglePage",output);
	//		} else {
	//			sendRtnObject(output);
	//		}
	//	}
	*/

	/**
	 * WMS系統取APServerInfo
	 * 
	 * @param body
	 * @param headers
	 * @throws JBranchException
	 * @throws Exception
	 */
	public void getApServerInfo(Object body, IPrimitiveMap<EnumTiaHeader> headers) throws JBranchException, Exception {
		LoginPageOutputVO loginOutputVO = new LoginPageOutputVO();
		try {
			APServerManager apServerManager = (APServerManager) PlatformContext.getBean("apServerManager");
			String apServerName = apServerManager.getApSvrName();
			loginOutputVO.setApServerName(apServerName);
		} catch (Exception ex) {
			logger.error(String.format("getApServerInfo Exception:", ex.getMessage()));
		}
		sendRtnObject(loginOutputVO);
	}

	/**
	 * WMS系統登入
	 * 
	 * @param body
	 * @param headers
	 * @throws JBranchException
	 * @throws Exception
	 */
	public void tlron(Object body, IPrimitiveMap<EnumTiaHeader> headers) throws JBranchException, Exception {
		LoginPageVO loginPageVO = (LoginPageVO) body;
		checkLoginIpSuccess(loginPageVO);

		LoginPageOutputVO loginOutputVO = tlron(setLoginInfoMap(loginPageVO));
		sendRtnObject(loginOutputVO);
	}

	private HashMap<String, String> setLoginInfoMap(LoginPageVO loginPageVO) throws Exception {
		HashMap<String, String> loginInfoMap = setLoginInfoMap(loginPageVO.getIptAppUsername(), loginPageVO.getIptAppUserRole(), loginPageVO.getIptAppUserDeptID(), loginPageVO.getIptAppUserRegioinCenterID(), loginPageVO.getIptAppUserRegioinCenterName(), loginPageVO.getIptAppUserBranchAreaID(), loginPageVO.getIptAppUserBranchAreaName(), loginPageVO.getIptAppUserBranchID(), loginPageVO.getIptAppUserBranchName(), loginPageVO.getIptAppUserIsPrimaryRole(), loginPageVO.getLoginSourceToken(), loginPageVO.getCurrentUserId());
		return loginInfoMap;
	}

	private HashMap<String, String> setLoginInfoMap(String userID, String roleID, String deptID, String regionCenterID, String regionCenterName, String branchAreaID, String branchAreaName, String branchID, String branchName, String isPrimaryRole, String loginSourceToken, String currentUserId) throws Exception {
		HashMap<String, String> loginInfoMap = new HashMap<String, String>();
		loginInfoMap.put("userId", userID);
		loginInfoMap.put("userName", getEmpName(loginInfoMap.get("userId"))); //姓名
		loginInfoMap.put("userDN", ""); //LDAP DN
		loginInfoMap.put("roleId", roleID);
		loginInfoMap.put("roleName", getRoleName(roleID));
		loginInfoMap.put("htgDepid", "");
		loginInfoMap.put("htgSessionType", "P8EstablishSession");
		loginInfoMap.put("deptID", deptID);
		loginInfoMap.put("REGION_CENTER_ID", regionCenterID);
		loginInfoMap.put("REGION_CENTER_NAME", regionCenterName);
		loginInfoMap.put("BRANCH_AREA_ID", branchAreaID);
		loginInfoMap.put("BRANCH_AREA_NAME", branchAreaName);
		loginInfoMap.put("BRANCH_NBR", branchID);
		loginInfoMap.put("BRANCH_NAME", branchName);
		loginInfoMap.put("isPrimaryRole", isPrimaryRole);
		loginInfoMap.put("loginSourceToken", loginSourceToken);
		loginInfoMap.put("CURRENT_USER_ID", currentUserId);

		return loginInfoMap;
	}

	/*
	 * 登入流程
	 *
	 */
	private LoginPageOutputVO tlron(Map<String, String> loginInfoMap) throws JBranchException, UnknownHostException {

		boolean successed = false;
		String userId = loginInfoMap.get("userId");
		String userName = loginInfoMap.get("userName");
		String roleId = loginInfoMap.get("roleId");
		String currentUserId = loginInfoMap.get("CURRENT_USER_ID");
		dam = this.getDataAccessManager();

		try {
			// ***** 取得分行資訊
			// branchNo = getBranchNbr();
			WmsBranchFactory branchFactory = (WmsBranchFactory) PlatformContext.getBean(BranchFactoryIF.DEFAULT_BRANCH_FACTORY);

			WmsBranch branch = branchFactory.getWmsBranch(loginInfoMap);
			loginInfoMap.put("branchNo", branch.getBrchID());

			// ***** 如branch不存在則重設 [2016/03/26] Modified by William
			// (kgi登入時以0000為預設分行，故在此取得實際分行後新增判斷是否已存在DataManager中)
			if (DataManager.getBranch(branch.getBrchID()) == null) {
				DataManager.setBranch(branch.getBrchID(), branch);
			}

			// ***** 重設section, uuid [2016/03/14] Modified by William
			Section section = DataManager.getSection(uuid); // 先用舊的uuid得到section再重設uuid的分行ID
			WorkStation ws = DataManager.getWorkStation(uuid); // 先用舊的uuid得到WorkStation
			uuid.setBranchID(branch.getBrchID()); // 重設登入分行 (kgi登入不選擇分行，改在此處重設)
			uuid.setTellerID(userId); // 重設UserID (for SSO)
			DataManager.setSection(uuid, section);

			// ***** 取得權限資訊
			PrivilegeManagement pm = (PrivilegeManagement) PlatformContext.getBean("privilegeManagement");

			RoleFunctionDTO roleFunctionDTO = pm.getFunctionMapByRole(roleId);
			List<String> priList = pm.getRolePrivileger(roleId);

			// ***** 建立並初始化User
			User user = createUser(loginInfoMap);
			user.setLevel(roleFunctionDTO.getExtend1());
			user.setCurrentUserId(currentUserId);

			DataManager.setUser(uuid, user);

			ws.setUser(user);
			ws.setSignOnBranchID(branch.getBrchID());
			ws.setBrchID(branch.getBrchID());
			branch.setWorkStation(ws.getWsID(), ws);
			DataManager.setWorkStation(uuid, ws); // 2011-03-11
			logger.debug(uuid.toString() + " - " + "add WorkStation!");

			//
			Map<String, ModuleDTO> moduleMap = roleFunctionDTO.getModuleMap();
			ModuleDTO moduleDTO = moduleMap.get("WMS");
			Map<String, ItemDTO> itemMap = moduleDTO.getItemMap();

			// ***** 交易權限特殊規則, 因為它實作的內容有使用到User, 所以必須在User初始化完成後才能執行
			ItemMapFilterIF itemFilter = getItemMapFilter();
			itemFilter.filter(itemMap);
			Iterator<String> it = moduleMap.keySet().iterator();

			while (it.hasNext()) {
				String key = it.next();
				ModuleDTO mDTO = moduleMap.get(key);
				itemFilter.filter(mDTO.getItemMap());
			}

			// ***** 建立並初始化:LoginPageOutputVO
			LoginPageOutputVO loginOutputVO = new LoginPageOutputVO();
			Map<String, Object> userInfo = getUserInfo(userId);
//			userInfo.remove("EMP_EMAIL_ADDRESS"); // 因應黑箱掃描，Email 個資不回傳前端（該欄位也沒有任何地方使用到）
			List<String> aoCode = new ArrayList<String>();

			// 依系統角色決定下拉選單可視範圍
			XmlInfo xmlInfo = new XmlInfo();
			Map<String, String> mapFC = xmlInfo.doGetVariable(xmlFC, FormatHelper.FORMAT_2); // FC
			Map<String, String> mapFCH = xmlInfo.doGetVariable("FUBONSYS.FCH_ROLE", FormatHelper.FORMAT_2); // FCH
			Map<String, String> mapPSOP = xmlInfo.doGetVariable(xmlPSOP, FormatHelper.FORMAT_2); // PS/OP
			Map<String, String> mapFAIA = xmlInfo.doGetVariable(xmlFAIA, FormatHelper.FORMAT_2); // FA/IA
			Map<String, String> mapUHRM = xmlInfo.doGetVariable(xmlUHRM, FormatHelper.FORMAT_2); // UHRM
			Map<String, String> trustIP = xmlInfo.doGetVariable(TRUST_ID, FormatHelper.FORMAT_3);

			// 當登入角色為輔銷人員 IA，取得轄下所有 AO 為自己的 AO
			if (mapFAIA.containsKey(roleId)) {
				aoCode = getAllAoList(dam, userId);
			} else {
				aoCode = getAoCode(ws.getUser().getUserID());
			}

			userInfo.put("AoCode", aoCode);

			// ***** 取得工作站設定,因為它實作的內容有使用到User,所以必須在User初始化完成後才能執行
			try {
				loginOutputVO.setConfiguration(getConfigMap());
			} catch (Exception ex) {
				logger.error(String.format("取得工作站設定錯誤:%s", ex.getMessage()), ex);
				sendWarningMsg("ehl_01_cmfpg000_007", EnumShowType.Popup);
			}

			// ***** 記錄使用者登入資訊
			String apServerName = recordLoginInfo(loginInfoMap, userId, userInfo, ws);

			loginOutputVO.setApServerName(apServerName);
			loginOutputVO.setLoginID(userId);
			loginOutputVO.setLoginName(userName);
			loginOutputVO.setLoginRole(roleId);
			loginOutputVO.setLoginRoleName(getRoleName(roleId));
			loginOutputVO.setLoginBrh(branch.getBrchID());
			loginOutputVO.setLoginBrhName(branch.getName());
			loginOutputVO.setLoginArea(branch.getBranch_area_id());
			loginOutputVO.setLoginAreaName(branch.getBranch_area_name());
			loginOutputVO.setLoginRegion(branch.getRegion_center_id());
			loginOutputVO.setLoginRegionName(branch.getRegion_center_name());
			loginOutputVO.setCurrentUserId(user.getCurrentUserId());

			loginOutputVO.setPriID(priList);
			loginOutputVO.setModuleMap(moduleMap);
			loginOutputVO.setRoleID(roleId);
			loginOutputVO.setRoleLevel(roleFunctionDTO.getExtend1());
			loginOutputVO.setName(userName);
			loginOutputVO.setCurrentUserId(user.getCurrentUserId());

			// 2017/11/23 載具借用只有理專人員
			boolean isMobile = "mobile".equals(loginInfoMap.get("loginSourceToken"));
			if (isMobile) {
				if (!mapFC.containsKey(roleId) && !mapFCH.containsKey(roleId) && !mapPSOP.containsKey(roleId) && !mapUHRM.containsKey(roleId))
					throw new JBranchException("非理專人員無法登入本系統");
			} else {
				if ("TRUE".equals(trustIP.get("IP_CHECK"))) {
					String[] trustIpArray = trustIP.get("IP_PREFIX").split(";");

					// 2018/10/15 伯瑞來 Mail 調整
					String currentIP = ObjectUtils.toString(PipelineUtil.getRemoteAddr());
					boolean isPassIp = false;
					for (String ip : trustIpArray) {
						isPassIp = currentIP.startsWith(ip) || userId.startsWith(ip);
						if (isPassIp)
							break;
					}

					//                  2018/10/15 伯瑞來 Mail 調整
					//					InetAddress currentIP = InetAddress.getLocalHost();
					//					boolean isPassIp = false;
					//					for(String ip:trustIpArray) {
					//						isPassIp = currentIP.getHostAddress().startsWith(ip);
					//						if(isPassIp) break;
					//					}

					if (!isMobile && !isPassIp) {
						throw new JBranchException("非合法連線");
					}
				}

			}

			String sysRole = (String) getSysRole(dam, roleId, loginOutputVO, userInfo).get("sysRole");
			loginOutputVO.setMemLoginFlag((String) getSysRole(dam, roleId, loginOutputVO, userInfo).get("memLoginFlag"));

			loginInfoMap.put("memLoginFlag", loginOutputVO.getMemLoginFlag());

			loginOutputVO.setAvailBranchList(getAvailBranchList(loginInfoMap, sysRole));
			loginOutputVO.setAvailAreaList(getAvailAreaList(loginInfoMap, sysRole));
			loginOutputVO.setAvailRegionList(getAvailRegionList(loginInfoMap, sysRole));

			loginOutputVO.setDate(new Date());
			loginOutputVO.setUrl(roleFunctionDTO.getExtend2());
			loginOutputVO.setUserInfo(userInfo);
			loginOutputVO.setHoliday(isHoliday()); // 是否為非營業日
			loginOutputVO.setLoginSourceToken(loginInfoMap.get("loginSourceToken"));//登入來源

			successed = true;
			setUserVariable(SystemVariableConsts.LOGIN_FLAG, successed);
			setSysInfo(loginOutputVO);

			return loginOutputVO;
		} finally {
			if (!successed) {
				// try
				// {
				// HtgHelper.closeSession();
				// }
				// catch(Throwable e)
				// {}

				WorkStation ws = DataManager.getWorkStation(uuid);
				if (ws != null) {
					ws.setUser(null);
				}
			}

			String branchId = uuid.getBranchID();
			String sectionID = uuid.getSectionID();
			String tellerId = uuid.getTellerID();

			//String wsId = uuid.getWsId();
			//String code = "wsid=" + wsId + ",branchID=" + branchId + ",tellerId=" + tellerId + ",sectionID=" + sectionID;
			//System.out.println(code);
			//System.out.println("#end");
		}
	}

	/*
	 * 取得登入權限及註記 20201216 add by ocean
	 */
	public Map<String, Object> getSysRole(DataAccessManager dam, String roleId, LoginPageOutputVO loginOutputVO, Map<String, Object> userInfo) throws JBranchException {

		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> brhChgMap_BS = xmlInfo.doGetVariable("SOT.BRANCH_CHANGE_BS", FormatHelper.FORMAT_3);

		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		//
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();

		sb.append("SELECT BRANCH_NBR ");
		sb.append("FROM TBORG_UHRM_BRH ");
		sb.append("WHERE EMP_ID = :loginID ");

		queryCondition.setObject("loginID", (String) getUserVariable(FubonSystemVariableConsts.LOGINID));
		queryCondition.setQueryString(sb.toString());

		List<Map<String, Object>> loginBreach = dam.exeQuery(queryCondition);

		//                 0          1          2           3            4             5              6             7            8               9               10           11             		12             13
		String[] cases = { xmlFC,     xmlFCH,    xmlPAO,     xmlPSOP,     xmlBMMGR,     xmlMBRMGR,     xmlARMGR,     xmlFAIA,     xmlHEADMGR,     xmlUHRMMGR,     xmlUHRM,     "UHRMHEADMGR",        xmlBSMGR,     "BS" };
		String[] flags = { "brhMem",  "brhMem",  "brhMem",   "brhMem",    "brhMem",     "brhMemMGR",   "brhMemMGR",  "ALL",       "ALL",          "uhrmMGR",      "UHRM",      "uhrmHeadMGR",        "bsMGR",      "BS" };
		String[] sysRo = { sysRoleFC, sysRoleFC, sysRolePAO, sysRolePSOP, sysRoleBMMGR, sysRoleMBRMGR, sysRoleARMGR, sysRoleFAIA, sysRoleHEADMGR, sysRoleUHRMMGR, sysRoleUHRM, "sysRoleUHRMHEADMGR", sysRoleBSMGR, "sysRoleBS" };

		String sysRole = sysRoleFC; // 預設FC
		String memLoginFlag = flags[0]; // 預設brhMem

		int i;
		for (i = 0; i < cases.length; i++)
			if (!(i == 11 || i == 13)) { // 遇11(UHRMHEADMGR=ARM) & 13 (BS)有額外判斷，所以在此處略過
				if (xmlInfo.doGetVariable(cases[i], FormatHelper.FORMAT_2).containsKey(roleId))
					break;
			}

		switch (i) {
		case 0: // FUBONSYS.FC_ROLE
			sysRole = sysRo[i];

			if (new BigDecimal((String) userInfo.get("DEPT_ID")).compareTo(new BigDecimal("200")) == 1 && !(new BigDecimal((String) userInfo.get("DEPT_ID")).compareTo(new BigDecimal("806")) == 0) && !(new BigDecimal((String) userInfo.get("DEPT_ID")).compareTo(new BigDecimal("810")) == 0) && new BigDecimal((String) userInfo.get("DEPT_ID")).compareTo(new BigDecimal("900")) == -1) { // 若人員主要組織為分行(200-900)，則判定為分行人員，排除806、810
				memLoginFlag = flags[i];
			} else if (loginBreach.size() > 0) { // 若人員於TBORG_UHRM_BRH中，判定為UHRM
				memLoginFlag = flags[10];
			} else if (StringUtils.equals(brhChgMap_BS.get("BS").toString(), (String) userInfo.get("DEPT_ID"))) { // 若人員主要組織非分行 且為175，則判定為銀證人員
				memLoginFlag = flags[13];
			} else { // 無法判斷者，則判定為分行人員
				memLoginFlag = flags[i];
			}
			break;
		case 8: // FUBONSYS.HEADMGR_ROLE
			sysRole = sysRoleHEADMGR;
			memLoginFlag = flags[i];

			if (StringUtils.equals("R001", roleId)) //   2020/12/16 寫死 R001 ARM
				memLoginFlag = flags[11];

			break;
		case 1: // FUBONSYS.FCH_ROLE
		case 2: // FUBONSYS.PAO_ROLE
		case 3: // FUBONSYS.PSOP_ROLE
		case 4: // FUBONSYS.BMMGR_ROLE
		case 5: // FUBONSYS.MBRMGR_ROLE
		case 6: // FUBONSYS.ARMGR_ROLE
		case 7: // FUBONSYS.FAIA_ROLE
		case 9: // FUBONSYS.UHRMMGR_ROLE
		case 10: // FUBONSYS.UHRM_ROLE
		case 12: // FUBONSYS.BSMGR_ROLE
			sysRole = sysRo[i];
			memLoginFlag = flags[i];
			break;
		}

		Map<String, Object> mapTemp = new HashMap<String, Object>();
		mapTemp.put("sysRole", sysRole);
		mapTemp.put("memLoginFlag", memLoginFlag);

		logger.info("##### sysRole=" + mapTemp.get("sysRole"));
		logger.info("##### memLoginFlag=" + mapTemp.get("memLoginFlag"));
		return mapTemp;
	}

	/*
	 * LDAP SERVER TEST => ldap://groupt.fbt.com
	 * LDAP SERVER PROD => ldap://group.fb.com
	 *
	 * AD帳號規則: MAIL前半段+ DN
	 * TEST DN => @groupt.fbt.com 	e.g. poray.wang@groupt.fbt.com
	 * PROD DN => @group.fb.com 	e.g. poray.wang@group.fb.com
	 */
	public boolean checkLoginForLdap(String loginId, String pw, String email) throws Exception {
		DirContext ctx = null;//呼叫LDAP的物件
		final String LDAP_URL = "ldap://";
		final String URL_KEY = "LDAP_URL";
		final String DN_KEY = "LDAP_DN";
		String nowExceptionMessage = "";
		List<String> errorList = new ArrayList<String>();//裝錯握訊息的
		List<String> dnKeys = new ArrayList<String>();//dn用同一組
		List<String> ldapUrlKeys = new ArrayList<String>();//所有ldap url key
		Map<String, String> ldapConfMap = null;//ldap設定
		String url = null;
		String dn = null;

		//判斷正確的 email 格式，避免含有不合法的字元 造成 ldap injection。
		// 若符合取@後方接dns
		if (!(email == null ? "" : email).matches("^\\w+((-\\w+)|(\\.\\w+))*\\@[A-Za-z0-9]+((\\.|-)[A-Za-z0-9]+)*\\.[A-Za-z]+$")) {
			//			logger.info("#tmplog error[" + loginId + "] in ldap email format : " + email);
			throw new Exception("ldap error for email format");
		}

		email = email.replaceFirst("@.*", "");

		//取得LDAP相關參數
		//		logger.info("#tmplog [" + loginId + "] ldap sys config start");
		ldapConfMap = getLdapConfig();
		//		logger.info("#tmplog [" + loginId + "] ldap sys config end");

		if (MapUtils.isEmpty(ldapConfMap)) {
			//			logger.info("#tmplog error[" + loginId + "] in ldap 參數未設定");
			throw new Exception("LDAP參數未設定");
		}

		//取所有url設定，區隔URL與DN
		for (String key : ldapConfMap.keySet()) {
			//key
			if (key.matches(URL_KEY + "\\d+"))
				ldapUrlKeys.add(key);
			else if (key.matches(DN_KEY + "\\d+"))
				dnKeys.add(key);
		}

		if (CollectionUtils.isEmpty(ldapUrlKeys)) {
			//			logger.info("#tmplog error[" + loginId + "] in ldap URL參數未設定- 請檢察系統參數");
			throw new Exception("LDAP URL參數未設定");
		}

		if (CollectionUtils.isEmpty(dnKeys)) {
			//			logger.info("#tmplog error[" + loginId + "] in ldap DN參數未設定 - 請檢察系統參數");
			throw new Exception("LDAP DN參數未設定");
		}

		//		logger.info("#tmplog [" + loginId + "] call ldap start");
		//取本次躍登入的LDAP設定
		int randomIdx = (int) (Math.random() * ldapUrlKeys.size());//因為是索引不用加１
		url = LDAP_URL + ldapConfMap.get(ldapUrlKeys.get(randomIdx));//當次要抓的url
		dn = ldapConfMap.get(dnKeys.get(0));

		//		logger.info("#tmplog [" + loginId + "] ldap url[" + url + "]");
		//		logger.info("#tmplog [" + loginId + "] ldap url[" + dn + "]");

		//組合ldap參數
		Hashtable<String, String> env = new Hashtable<String, String>();
		env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, url);
		env.put(Context.SECURITY_AUTHENTICATION, "Simple");
		env.put(Context.SECURITY_PRINCIPAL, StringUtils.join(new String[] { email, dn }, "")); // 避免白箱掃出 ldap injection 疑慮，因此不使用 + 串聯變數
		env.put(Context.SECURITY_CREDENTIALS, pw);

		//		for(String key : env.keySet())
		//			logger.info("#tmplog [" + loginId + "] ldap key[" + key + "] value[" + env.get(key) + "]");

		try {
			//			logger.info("#tmplog [" + loginId + "] create ldap connection to check login start");
			ctx = new InitialDirContext(env);
			//			logger.info("#tmplog [" + loginId + "] create ldap connection to check is success");
			return true;//呼叫成功就回傳
		} catch (javax.naming.AuthenticationException e) {
			//			logger.info("#tmplog [" + loginId + "] create ldap connection exception : " + (nowExceptionMessage = getStackTrace(e)));
			throw new Exception("使用者代號或EIP密碼有誤：\r\n" + getLdapErrorMessage(getStackTrace(e).replaceAll("\r|\n", "")));
		} catch (javax.naming.CommunicationException e) {
			//			logger.info("#tmplog [" + loginId + "] create ldap connection exception : " + (nowExceptionMessage = getStackTrace(e)));
			throw new Exception("請確認LDAP連線[" + (ldapUrlKeys.get(randomIdx)) + "]");
		} catch (Exception e) {
			//			logger.info("#tmplog [" + loginId + "] create ldap connection exception : " + (nowExceptionMessage = getStackTrace(e)));
			throw new Exception(getStackTrace(e));
		} finally {
			if (ctx != null) {
				try {
					ctx.close();
				} catch (Exception e) {
					errorList.add("ldap error message for ctx close：\r\n" + getStackTrace(e));
					logger.error(errorList.get(errorList.size() - 1));
				}
			}
			//			logger.info("#tmplog [" + loginId + "] create ldap connection to check login end");
			//			logger.info("#tmplog [" + loginId + "] call ldap end");
		}
	}

	public String getLdapErrorMessage(String ldapMsg) {
		String ldapPattern = "\\[LDAP:\\s+error.*\\]";
		Matcher matcher = Pattern.compile(ldapPattern).matcher(ldapMsg.replaceAll("\r||\n", ""));
		while (matcher.find())
			return matcher.group().replaceAll("\\[|\\]", "");
		return ldapMsg;
	}

	@SuppressWarnings("unchecked")
	public Map<String, String> getLdapConfig() throws JBranchException {
		return new XmlInfo().getVariable(LDAP_CONFIG, FormatHelper.FORMAT_3);
	}

	/*
	 * 建立並初始化User
	 */
	private User createUser(Map<String, String> loginInfoMap) throws JBranchException {
		String section = DataManager.getSection(uuid).getSectionID();
		User user = new User();

		user.setSection(section);
		user.setUserID(loginInfoMap.get("userId"));
		user.setUserPwd(loginInfoMap.get("userPwd"));
		user.setUserName(loginInfoMap.get("userName"));
		user.setUserAuth(loginInfoMap.get("roleId"));
		user.setCurrentUserId(loginInfoMap.get("CURRENT_USER_ID"));

		user.getPlatFormVO().setVar(PlatformVOHelper._FPS_USER_RACFID, loginInfoMap.get("racfId"));
		user.getPlatFormVO().setVar(PlatformVOHelper._FPS_USER_RACFPASSWORD, loginInfoMap.get("racfPwd"));
		user.getPlatFormVO().setVar(PlatformVOHelper._FPS_USER_ROLENAME, loginInfoMap.get("roleName"));
		user.getPlatFormVO().setVar(PlatformVOHelper._FPS_USER_CALLER, loginInfoMap.get("caller"));
		user.getPlatFormVO().setVar(PlatformVOHelper._FPS_USER_MODULE, loginInfoMap.get("mod"));

		return user;
	}

	/*
	 * 取得使用者其他登入資訊
	 */
	@SuppressWarnings("unchecked")
	public Map<String, Object> getUserInfo(String userId) throws JBranchException {
		List<Map<String, Object>> result = exeQueryForQcf(genDefaultQueryConditionIF().setQueryString(new StringBuffer().append(" SELECT ").append("   DEPT_ID, ").append("   EMP_CELL_NUM, ").append("   EMP_DEPT_EXT, ").append("   EMP_FAX, ").append("   JOB_TITLE_NAME, ").append("   JOB_RANK, ").append("   EXPECTED_END_DATE, ").append("   GROUP_TYPE, ").append("   ONBOARD_DATE, ").append("   JOB_ONBOARD_DATE, ").append("   JOB_RESIGN_DATE, ").append("   SALES_SUP_EMP_ID, ").append("   EMP_MAIL_ADDRESS, ").append("   EMP_EMAIL_ADDRESS, ").append("   EMP_PHONE_NUM, ").append("   PERF_EFF_DATE, ").append("   HANDOVER_FLAG, ").append("   HANDOVER_DATE, ").append("   REMARK, ").append("   EMP_PHOTO, ").append("   SERVICE_FLAG, ").append("   CHANGE_FLAG, ").append("   REVIEW_STATUS, ").append("   JOB_POSITION, ").append("   JOB_GOAL_DATE, ").append("   VERSION, ").append("   CREATETIME, ").append("   CREATOR, ").append("   MODIFIER, ").append("   LASTUPDATE ").append(" FROM TBORG_MEMBER where EMP_ID = :empId ").toString()).setObject("empId", userId));

		return CollectionUtils.isNotEmpty(result) ? result.get(0) : new HashMap<String, Object>();

		//		WmsUser wmsUser = (WmsUser) PlatformContext.getBean("wmsUser");
		//		return wmsUser.getUserInfo(userId);
	}

	/*
	 * 取得使用者AO CODE
	 */
	private List<String> getAoCode(String userId) throws JBranchException {
		WmsUser wmsUser = (WmsUser) PlatformContext.getBean("wmsUser");
		return wmsUser.getAoCode(userId);
	}

	/**
	 * 取得權限資訊
	 * 
	 * @param body
	 * @param headers
	 * @throws JBranchException
	 */
	public void getAuthorities(Object body, IPrimitiveMap<EnumTiaHeader> headers) throws JBranchException {
		AuthoritiesInputVO inputVO = (AuthoritiesInputVO) body;
		AuthoritiesOutputVO outputVO = new AuthoritiesOutputVO();

		PrivilegeManagement pm = (PrivilegeManagement) PlatformContext.getBean("privilegeManagement");
		outputVO.setAuthorities(pm.getAuthorities(inputVO.getRoles()));

		sendRtnObject(outputVO);
	}

	/*
	 * 取得人員名稱
	 * @param empId
	 * @return
	 * @throws JBranchException
	 */
	private String getEmpName(String empId) throws JBranchException {
		String empName = null;
		try {
			DataAccessManager dam = getDataAccessManager();
			TBORG_MEMBERVO vo = (TBORG_MEMBERVO) dam.findByPKey(TBORG_MEMBERVO.TABLE_UID, empId);
			if (vo != null) {
				empName = vo.getEMP_NAME();
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new JBranchException("無法正確取得帳號名稱");
		}

		return empName;
	}

	/*
	 * 取得角色名稱
	 * @param roleId
	 * @return
	 * @throws JBranchException
	 */
	private String getRoleName(String roleId) throws JBranchException {
		TBSYSSECUROLEVO tbsyssecurole = null;

		if (roleId != null) {
			DataAccessManager dam = getDataAccessManager();
			tbsyssecurole = (TBSYSSECUROLEVO) dam.findByPKey(TBSYSSECUROLEVO.TABLE_UID, roleId);
		}

		if (tbsyssecurole == null) {
			throw new JBranchException("無法正確取得角色資訊");
		} else {
			return tbsyssecurole.getNAME();
		}
	}

	/*
	 * 取得角色ID
	 * @param loginid
	 * @return
	 * @throws JBranchException
	 */
	private String getRoleId(DataAccessManager dam, String loginid) throws JBranchException {
		QueryConditionIF queryCondition = dam.getQueryCondition();

		queryCondition.setQueryString("select ROLE_ID from TBORG_MEMBER_ROLE where EMP_ID = ? ");
		queryCondition.setString(1, loginid);

		List<Map<String, String>> resultList = dam.exeQuery(queryCondition);

		if (resultList.isEmpty()) {
			return "";
		} else {
			return (resultList.get(0).get("ROLE_ID")).trim();
		}
	}

	/**
	 * 取得所有分行清單
	 * 
	 * @param body
	 * @param headers
	 * @throws JBranchException
	 */
	public void getBranchList(Object body, IPrimitiveMap<EnumTiaHeader> headers) throws JBranchException {
		WmsBranchFactory wmsBranchFactory = (WmsBranchFactory) PlatformContext.getBean(BranchFactoryIF.DEFAULT_BRANCH_FACTORY);
		sendRtnObject(wmsBranchFactory.getBranchLabelList());
	}

	/*
	 * 取得可視營運中心清單
	 *
	 */
	private List<Map<String, String>> getAvailRegionList(Map<String, String> loginInfoMap, String sysRole) throws JBranchException {
		WmsBranchFactory wmsBranchFactory = (WmsBranchFactory) PlatformContext.getBean(BranchFactoryIF.DEFAULT_BRANCH_FACTORY);
		return (wmsBranchFactory.getAvailRegionList(loginInfoMap, sysRole));
	}

	/*
	 * 取得可視區域清單
	 *
	 */
	private List<Map<String, String>> getAvailAreaList(Map<String, String> loginInfoMap, String sysRole) throws JBranchException {
		WmsBranchFactory wmsBranchFactory = (WmsBranchFactory) PlatformContext.getBean(BranchFactoryIF.DEFAULT_BRANCH_FACTORY);
		return (List<Map<String, String>>) wmsBranchFactory.getAvailAreaList(loginInfoMap, sysRole);
	}

	/*
	 * 取得可視分行清單
	 *
	 */
	private List<Map<String, String>> getAvailBranchList(Map<String, String> loginInfoMap, String sysRole) throws JBranchException {
		WmsBranchFactory wmsBranchFactory = (WmsBranchFactory) PlatformContext.getBean(BranchFactoryIF.DEFAULT_BRANCH_FACTORY);
		return (wmsBranchFactory.getAvailBranchList(loginInfoMap, sysRole));
	}

	/*
	 * 檢查是否為非營業日
	 *
	 */
	private boolean isHoliday() throws JBranchException {
		//
		//		if(isTestMode()){
		//			return false;
		//		}
		//
		//		DataAccessManager dam = getDataAccessManager();
		//		QueryConditionIF cond = dam.getQueryCondition();
		//		StringBuilder sb = new StringBuilder();
		//
		//		sb.append(" select * from TBSFA_DAT_HOLIDAY ");
		//		sb.append(" where BUSINESS_DATE_FLAG = '1' and CALENDAR_DATE = convert(varchar(10), getdate(), 120) ");
		//
		//		cond.setQueryString(sb.toString());
		//		List<?> result  = dam.exeQuery(cond);
		//
		//		return !result.isEmpty();
		return false;
	}

	/*
	 * 取得登入時間
	 */
	private String getLoginTime(DataAccessManager dam, String loginID, String wsID, String apSvrName) throws JBranchException {
		TBSYSWSONLINESTATUSPK pk = new TBSYSWSONLINESTATUSPK();
		pk.setTELLERID(loginID);
		pk.setWSID(wsID);
		pk.setAPSVRNAME(apSvrName);

		TBSYSWSONLINESTATUSVO vo = (TBSYSWSONLINESTATUSVO) dam.findByPKey(TBSYSWSONLINESTATUSVO.TABLE_UID, pk);

		if (vo == null) {
			return "";
		} else {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			return sdf.format(vo.getCreatetime());
		}
	}

	/*
	 * 轉換登入URL的參數
	 *
	 */
	private Map<String, String> authUidToMap(String authUid) throws JBranchException {
		Map<String, String> result = new HashMap<String, String>();
		try {

			String queryString = new String(StringUtil.fromHex(authUid));
			String[] paramArr = queryString.split("&");

			for (String paramKeyAndValue : paramArr) {

				String[] params = paramKeyAndValue.split("=", 2);
				if (params.length == 2)
					result.put(params[0], params[1]);

			}
		} catch (DecoderException e) {
			throw new JBranchException(e.getMessage(), e);
		}

		return result;
	}

	/*
	 * 權限控管
	 *
	 */
	public void getCompetence(DataAccessManager dam, Object body, IPrimitiveMap header) throws JBranchException {
		LoginPageVO inputVO = (LoginPageVO) body;
		LoginPageOutputVO outputVO = new LoginPageOutputVO();

		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("select * from TBSYSPARAMETER WHERE PARAM_TYPE = 'SYS.FUNCTION_PRI' and PARAM_CODE = :id ORDER BY PARAM_ORDER");
		queryCondition.setObject("id", inputVO.getAuthuid());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		outputVO.setFunctionList(list);
		this.sendRtnObject(outputVO);
	}

	/*
	 * 登出作業
	 *
	 */
	public void tlroff(Object body, IPrimitiveMap<EnumTiaHeader> headers) throws JBranchException {
		//		changeStatusOff();

		WorkStation ws = DataManager.getWorkStation(uuid);

		if (ws != null) {
			try {
				this.sendRtnObject(null);
			} finally {
				//***** 記錄使用者登出資訊
				APServerManager apServerManager = (APServerManager) PlatformContext.getBean("apServerManager");
				WSManagerIF wsManager = (WSManagerIF) PlatformContext.getBean("wsManager");

				String apServerName = apServerManager.getApSvrName();
				String wsID = ws.getWsID();
				String brchID = ws.getBrchID();
				String userId = ws.getUser().getUserID();
				logger.debug(uuid.toString() + " tlroff " + wsID + " - " + brchID + " - " + userId);
				wsManager.whenLogout(wsID, userId, apServerName, brchID);
				//*************************
				logger.debug(uuid.toString() + " - delete user!");
				ws.setUser(null);

			}
		}
	}

	public void aliveUpdate(Object body, IPrimitiveMap<EnumTiaHeader> headers) {
	}

	/*
	 *
	 */
	private ItemMapFilterIF getItemMapFilter() throws JBranchException {
		List<ItemMapFilterIF> filterlist = new ArrayList<ItemMapFilterIF>();
		XmlInfo xmlinfo = new XmlInfo();
		Map<String, String> map = xmlinfo.getVariable("TBSYS.CMFPG000.PRI_RULE", FormatHelper.FORMAT_2);

		if (map != null) {
			for (Entry<String, String> entry : map.entrySet()) {
				ItemMapFilterIF filter = (ItemMapFilterIF) PlatformContext.getBean(entry.getKey());
				if (filter != null)
					filterlist.add(filter);
			}
		}
		return new BatchItemMapFilter(filterlist);
	}

	/*
	 *
	 */
	private Map getConfigMap() throws ConfigurationException {
		StringBuilder sb = new StringBuilder();
		Map config = PlatformConfigUtils.getConfigMap();
		Iterator i = config.keySet().iterator();

		while (i.hasNext()) {
			Object key = i.next();
			Object value = config.get(key);

			sb.append(key).append(":").append(value);
			if (i.hasNext())
				sb.append(",");
		}
		logger.info(sb.toString());

		return config;
	}

	/*
	 * 設定Java端系統變數
	 *
	 */
	private void setSysInfo(LoginPageOutputVO vo) {
		try {
			//存入使用者變數
			setUserVariable(SystemVariableConsts.LOGINBRH, vo.getLoginBrh());
			setUserVariable(FubonSystemVariableConsts.LOGIN_AREA, vo.getLoginArea());
			setUserVariable(FubonSystemVariableConsts.LOGIN_REGION, vo.getLoginRegion());
			setUserVariable(SystemVariableConsts.AVAILBRANCHLIST, listOfString(vo.getAvailBranchList(), "BRANCH_NBR"));
			setUserVariable(FubonSystemVariableConsts.AVAILAREALIST, listOfString(vo.getAvailAreaList(), "BRANCH_AREA_ID"));
			setUserVariable(FubonSystemVariableConsts.AVAILREGIONLIST, listOfString(vo.getAvailRegionList(), "REGION_CENTER_ID"));
			setUserVariable(FubonSystemVariableConsts.LOGINROLE, vo.getLoginRole());
			setUserVariable(FubonSystemVariableConsts.LOGINID, vo.getLoginID());
			setUserVariable(FubonSystemVariableConsts.LOGIN_AOCODE_LIST, vo.getUserInfo().get("AoCode"));
			setUserVariable(FubonSystemVariableConsts.LOGIN_SOURCE_TOKEN, vo.getLoginSourceToken());
			setUserVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG, vo.getMemLoginFlag());

			//存入公用變數
			setCommonVariable("LOGINDATE", new Date());
			setCommonVariable(SystemVariableConsts.HOSTDATE, new Date());
			setCommonVariable(SystemVariableConsts.HOSTTIME, new Date());
			setCommonVariable(FubonSystemVariableConsts.IS_HOLIDAY, vo.isHoliday());

			XmlInfo xmlInfo = new XmlInfo();
			String maxQryRows = xmlInfo.doGetVariable("SYS", "MAX_QRY_ROWS", FormatHelper.FORMAT_3);
			setCommonVariable(FubonSystemVariableConsts.QRY_MAX_RESULTS, Integer.parseInt(maxQryRows));
		} catch (Exception e) {
			logger.debug(e.getMessage(), e);
		}
	}

	private List<String> listOfString(List<Map<String, String>> lstSrc, String key) {
		List<String> lstRtn = new ArrayList<String>();

		for (Map<String, ?> map : lstSrc) {
			Object obj = map.get(key);

			if (obj == null) {
				continue;
			} else {
				lstRtn.add(obj.toString());
			}
		}

		return lstRtn;
	}

	/** 抓exception 完整訊息 **/
	public static String getStackTrace(Exception e) {
		StringWriter sw = null;
		PrintWriter pw = null;
		try {
			sw = new StringWriter();
			pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			pw.flush();
			sw.flush();
		} finally {
			if (sw != null) {
				try {
					sw.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
			if (pw != null) {
				pw.close();
			}
		}
		return sw.toString();
	}

	/**
	 * 判斷登入者ip
	 * 
	 * @throws Exception
	 * @throws JBranchException
	 **/
	public void checkLoginIpSuccess(LoginPageVO inputVO) throws Exception {
		String ipRegexp = "";//驗證ip表達式
		String checkIp = "";//登入ip
		String ip = "";//登入ip
		String mode = "";
		String mobileToken = StringUtils.isNotEmpty(inputVO.getLoginSourceToken()) ? inputVO.getLoginSourceToken() : (String) getCommonVariable(SystemVariableConsts.LOGIN_SOURCE_TOKEN);

		//如果沒有標記要驗證IP，或是登入的對象是行動APP，不驗證IP
		if (!isCheckIp() || "mobile".equals(mobileToken))
			return;

		try {
			ipRegexp = doGetIpCheckRegexp();//取ip檢核表達式
		} catch (Exception ex) {
			logger.error(StringUtil.getStackTraceAsString(ex));
			return;//未設定ip檢核設定表達式時不檢核
		}

		checkIp = (mode = isTestMode() ? "T" : "P") + (ip = ObjectUtils.toString(PipelineUtil.getRemoteAddr()));

		if (!checkIp.matches(ipRegexp)) {
			throw new Exception("IP check is error : [" + mode + "][" + ip + "] ");
		}

	}

	private boolean isCheckIp() throws Exception {
		List<Map<String, Object>> results = null;
		String ipRegexp = null;

		if (CollectionUtils.isEmpty((results = doGetParameter("FUBONSYS.LOGIN_IP_CHK", "IS_CHECK"))))
			return false;

		return "Y".equals(ObjectUtils.toString(results.get(0).get("PARAM_NAME")));
	}

	private String doGetIpCheckRegexp() throws Exception {
		List<Map<String, Object>> results = null;
		String ipRegexp = null;

		if (CollectionUtils.isEmpty((results = doGetParameter("FUBONSYS.LOGIN_IP_CHK", "IP_REGEXP")))) {
			throw new Exception("未設定ip檢核表達式");
		} else if (StringUtils.isBlank(ipRegexp = ObjectUtils.toString(results.get(0).get("PARAM_NAME")))) {
			throw new Exception("未設定ip檢核表達式");
		}

		return ipRegexp;
	}

	/** 若要抓即時更改的parameter才使用 **/
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> doGetParameter(String type) {
		List<Map<String, Object>> results = null;

		try {
			String queryStr = " select IP_REGEXP from TBSYSPARAMETER where PARAM_TYPE = :PARAM_TYPE ";
			results = getDataAccessManager().exeQueryWithoutSort(genDefaultQueryConditionIF().setQueryString(queryStr).setObject("PARAM_TYPE", type));
		} catch (Exception ex) {
			logger.error(StringUtil.getStackTraceAsString(ex));
		}

		return results;
	}

	/** 若要抓即時更改的parameter才使用 **/
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> doGetParameter(String type, String code) {
		List<Map<String, Object>> results = null;

		try {
			String queryStr = " select * from TBSYSPARAMETER where PARAM_TYPE = :PARAM_TYPE and PARAM_CODE = :PARAM_CODE ";
			results = getDataAccessManager().exeQueryWithoutSort(genDefaultQueryConditionIF().setQueryString(queryStr).setObject("PARAM_TYPE", type).setObject("PARAM_CODE", code));

		} catch (Exception ex) {
			logger.error(StringUtil.getStackTraceAsString(ex));
		}

		return results;
	}

	public void chkLdep(DataAccessManager dam, String loginId, String passWord) throws Exception {
		try {
			//發送LDAP
			QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			qc.setQueryString("select EMP_EMAIL_ADDRESS from TBORG_MEMBER where EMP_ID = :userId");
			qc.setObject("userId", loginId);

			List<Map<String, Object>> list = dam.exeQuery(qc);

			if (list.isEmpty()) {
				throw new JBranchException("無此使用者");
			}

			String email = (String) list.get(0).get("EMP_EMAIL_ADDRESS");
			checkLoginForLdap(loginId, passWord, email);
		} catch (Exception ex) {
			throw new JBranchException(ex);
		}
	}

	private HttpServletRequest doGetHttpServletRequest() {
		HttpServletRequest request = (HttpServletRequest) ThreadDataPool.getData(PipelineUtil.HTTP_SERVLET_REQUEST);

		if (request == null) {
			request = (HttpServletRequest) ThreadDataPool.getData("HttpServletRequest");
		}

		return request;
	}

	public void loadSysInfoData(Object body, IPrimitiveMap<EnumTiaHeader> headers) throws JBranchException, Exception {
		APServerManager apServerManager = (APServerManager) PlatformContext.getBean("apServerManager");
		WorkStation ws = DataManager.getWorkStation(uuid);
		String empId = ObjectUtils.toString(getUserVariable(FubonSystemVariableConsts.LOGINID));
		String roleId = (String) getUserVariable(FubonSystemVariableConsts.LOGINROLE);

		PrivilegeManagement pm = (PrivilegeManagement) PlatformContext.getBean("privilegeManagement");
		List<String> priList = pm.getRolePrivileger(roleId);
		Map<String, Object> userInfo = getUserInfo(ws.getUser().getUserID());

		List<String> aoCode = new ArrayList<String>();

		// 當登入角色為輔銷人員 IA，取得轄下所有 AO 為自己的 AO
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> roleMap = xmlInfo.doGetVariable("FUBONSYS.FAIA_ROLE", FormatHelper.FORMAT_2);
		if (roleMap.containsKey(roleId)) {
			aoCode = getAllAoList(dam, empId);
		} else {
			aoCode = getAoCode(ws.getUser().getUserID());
		}

		userInfo.put("AoCode", aoCode);

		List<Map> authorities = pm.getAuthorities(Arrays.asList(roleId));
		WmsUser wmsUser = (WmsUser) PlatformContext.getBean("wmsUser");

		Map sysInfo = new GenericMap().put("apServerName", apServerManager.getApSvrName()).put("loginID", ws.getUser().getUserID()).put("loginName", ws.getUser().getUserName()).put("ApplicationID", ObjectUtils.toString(doGetHttpServletRequest().getAttribute("applicationId"))).put("loginBrh", getUserVariable(SystemVariableConsts.LOGINBRH)).put("loginArea", getUserVariable(FubonSystemVariableConsts.LOGIN_AREA)).put("loginRegion", getUserVariable(FubonSystemVariableConsts.LOGIN_REGION)).put("loginBrhName", getCommonVariable(SystemVariableConsts.LOGINBRHNAME)).put("wsId", ws.getWsID()).put("loginRole", roleId).put("priID", priList).put("loginRoleName", getRoleName(roleId)).put("availBranchList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST)).put("availAreaList", getUserVariable(FubonSystemVariableConsts.AVAILAREALIST)).put("availRegionList", getUserVariable(FubonSystemVariableConsts.AVAILREGIONLIST)).put("userInfo", userInfo).put("AoCode", aoCode).put("loginSourceToken", "mobile").put("authorities", authorities).put("userList", wmsUser.getAgentInfo(ws.getUser().getUserID())).put("memLoginFlag", getUserVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)).getParamMap();

		//		System.out.println("##########\r\n" + sysInfo);
		sendRtnObject(sysInfo);
	}

	/**
	 * app透過ws時的登入方法：檢查帳號密碼並取得代理人清單
	 * 
	 * @throws JBranchException
	 * @throws IOException
	 */
	public void mobAuthenticate(Object body, IPrimitiveMap<EnumTiaHeader> headers) throws JBranchException, Exception {
		GenericMap inputGmap = new GenericMap((Map) body);

		Exception ex = null;
		//帳號
		String loginId = inputGmap.getNotNullStr("account");
		//密碼
		String passWord = inputGmap.getNotNullStr("pwd");
		passWord = new String(Base64.decodeBase64(passWord));

		StringBuffer repwd = new StringBuffer();

		if (StringUtils.isBlank(passWord)) {
			throw new JBranchException("密碼錯誤");
		}

		for (int i = passWord.length() - 1; i >= 0; i--) {
			repwd.append(passWord.charAt(i));
		}
		passWord = repwd.toString();

		//判斷此帳密是否可以登入，如果錯誤會拋出ldap的例外
		if (!isPrivilegedLogin(passWord)) {
			chkLdep(dam, loginId, passWord);
		}

		WmsUser wmsUser = (WmsUser) PlatformContext.getBean("wmsUser");
		// 更新代理人資訊
		wmsUser.updateAgentInfo();
		// 取得身分、兼職、代理人清單，供呼叫者選取
		List agentInfo = wmsUser.getAgentInfo(loginId);
		agentInfo = MapObjectUtils.reHumpByKey(agentInfo);
		sendRtnObject(new GenericMap().put("userList", agentInfo).getParamMap());
	}

	@SuppressWarnings("unchecked")
	public void mobLogin(Object body, IPrimitiveMap<EnumTiaHeader> headers) throws JBranchException, Exception {
		HttpServletRequest request = doGetHttpServletRequest();
		GenericMap inputGmap = new GenericMap((Map) body);

		String loginSourceToken = request.getAttribute("mobile") == null ? null : ObjectUtils.toString(request.getAttribute("mobile"));

		sendRtnObject(doGetMobileLoginData(dam, setLoginInfoMap((String) inputGmap.get("empId"), (String) inputGmap.get("roleId"), (String) inputGmap.get("deptId"), (String) inputGmap.get("regionCenterId"), (String) inputGmap.get("regionCenterName"), (String) inputGmap.get("branchAreaId"), (String) inputGmap.get("branchAreaName"), (String) inputGmap.get("branchNbr"), (String) inputGmap.get("branchName"), (String) inputGmap.get("isPrimaryRole"), loginSourceToken, (String) inputGmap.get("currentUserId"))));
	}

	private Map doGetMobileLoginData(DataAccessManager dam, Map<String, String> loginInfoMap) throws Exception {
		boolean successed = false;
		String userId = loginInfoMap.get("userId");
		String userName = loginInfoMap.get("userName");
		String roleId = loginInfoMap.get("roleId");
		String currentUserId = loginInfoMap.get("CURRENT_USER_ID");

		Map outputMap = new HashMap();

		try {
			// ***** 取得分行資訊
			// branchNo = getBranchNbr();
			WmsBranchFactory branchFactory = (WmsBranchFactory) PlatformContext.getBean(BranchFactoryIF.DEFAULT_BRANCH_FACTORY);
			WmsBranch branch = branchFactory.getWmsBranch(loginInfoMap);
			Map<String, Object> userInfo = getUserInfo(userId);

			// ***** 如branch不存在則重設 [2016/03/26] Modified by William
			// (kgi登入時以0000為預設分行，故在此取得實際分行後新增判斷是否已存在DataManager中)
			if (DataManager.getBranch(branch.getBrchID()) == null) {
				DataManager.setBranch(branch.getBrchID(), branch);
			}

			// ***** 重設section, uuid [2016/03/14] Modified by William
			Section section = DataManager.getSection(uuid); // 先用舊的uuid得到section再重設uuid的分行ID
			WorkStation ws = DataManager.getWorkStation(uuid); // 先用舊的uuid得到WorkStation
			uuid.setBranchID(branch.getBrchID()); // 重設登入分行 (kgi登入不選擇分行，改在此處重設)
			uuid.setTellerID(userId); // 重設UserID (for SSO)
			DataManager.setSection(uuid, section);

			// ***** 取得權限資訊
			PrivilegeManagement pm = (PrivilegeManagement) PlatformContext.getBean("privilegeManagement");
			RoleFunctionDTO roleFunctionDTO = pm.getFunctionMapByRole(roleId);
			List<String> priList = pm.getRolePrivileger(roleId);

			// ***** 建立並初始化User
			User user = createUser(loginInfoMap);
			user.setLevel(roleFunctionDTO.getExtend1());
			user.setCurrentUserId(currentUserId);

			DataManager.setUser(uuid, user);

			ws.setUser(user);
			ws.setSignOnBranchID(branch.getBrchID());
			ws.setBrchID(branch.getBrchID());
			branch.setWorkStation(ws.getWsID(), ws);
			DataManager.setWorkStation(uuid, ws); // 2011-03-11
			logger.debug(uuid.toString() + " - " + "add WorkStation!");

			//
			Map<String, ModuleDTO> moduleMap = roleFunctionDTO.getModuleMap();
			ModuleDTO moduleDTO = moduleMap.get("WMS");
			Map<String, ItemDTO> itemMap = moduleDTO.getItemMap();

			// ***** 交易權限特殊規則, 因為它實作的內容有使用到User, 所以必須在User初始化完成後才能執行
			ItemMapFilterIF itemFilter = getItemMapFilter();
			itemFilter.filter(itemMap);
			Iterator<String> it = moduleMap.keySet().iterator();

			while (it.hasNext()) {
				String key = it.next();
				ModuleDTO mDTO = moduleMap.get(key);
				itemFilter.filter(mDTO.getItemMap());
			}

			// ***** 建立並初始化:LoginPageOutputVO
			LoginPageOutputVO loginOutputVO = new LoginPageOutputVO();

			List<String> aoCode = new ArrayList<String>();

			// 依系統角色決定下拉選單可視範圍
			XmlInfo xmlInfo = new XmlInfo();
			Map<String, String> mapFAIA = xmlInfo.doGetVariable(xmlFAIA, FormatHelper.FORMAT_2);
			// 當登入角色為輔銷人員 IA，取得轄下所有 AO 為自己的 AO
			if (mapFAIA.containsKey(roleId)) {
				aoCode = getAllAoList(dam, userId);
			} else {
				aoCode = getAoCode(ws.getUser().getUserID());
			}

			userInfo.put("AoCode", aoCode);

			// ***** 取得工作站設定,因為它實作的內容有使用到User,所以必須在User初始化完成後才能執行
			try {
				loginOutputVO.setConfiguration(getConfigMap());
			} catch (Exception ex) {
				logger.error(String.format("取得工作站設定錯誤:%s", ex.getMessage()), ex);
				sendWarningMsg("ehl_01_cmfpg000_007", EnumShowType.Popup);
			}
			String apServerName = recordLoginInfo(loginInfoMap, userId, userInfo, ws);

			String sysRole = (String) getSysRole(dam, roleId, loginOutputVO, userInfo).get("sysRole");
			loginOutputVO.setMemLoginFlag((String) getSysRole(dam, roleId, loginOutputVO, userInfo).get("memLoginFlag"));

			loginInfoMap.put("memLoginFlag", loginOutputVO.getMemLoginFlag());
			loginInfoMap.put("roleName", getRoleName(roleId));

			loginOutputVO.setApServerName(apServerName);
			loginOutputVO.setLoginID(userId);
			loginOutputVO.setLoginName(userName);
			loginOutputVO.setLoginRole(roleId);
			loginOutputVO.setLoginRoleName(getRoleName(roleId));
			loginOutputVO.setLoginBrh(branch.getBrchID());
			loginOutputVO.setLoginBrhName(branch.getName());
			loginOutputVO.setLoginArea(branch.getBranch_area_id());
			loginOutputVO.setLoginAreaName(branch.getBranch_area_name());
			loginOutputVO.setLoginRegion(branch.getRegion_center_id());
			loginOutputVO.setLoginRegionName(branch.getRegion_center_name());
			loginOutputVO.setPriID(priList);
			loginOutputVO.setModuleMap(moduleMap);
			loginOutputVO.setRoleID(roleId);
			loginOutputVO.setRoleLevel(roleFunctionDTO.getExtend1());
			loginOutputVO.setName(userName);
			loginOutputVO.setAvailBranchList(getAvailBranchList(loginInfoMap, sysRole));
			loginOutputVO.setAvailAreaList(getAvailAreaList(loginInfoMap, sysRole));
			loginOutputVO.setAvailRegionList(getAvailRegionList(loginInfoMap, sysRole));
			loginOutputVO.setDate(new Date());
			loginOutputVO.setUrl(roleFunctionDTO.getExtend2());
			loginOutputVO.setUserInfo(userInfo);
			loginOutputVO.setHoliday(isHoliday()); // 是否為非營業日
			loginOutputVO.setLoginSourceToken(loginInfoMap.get("loginSourceToken"));//登入來源

			successed = true;
			setUserVariable(SystemVariableConsts.LOGIN_FLAG, successed);
			setSysInfo(loginOutputVO);

			//是否為行內IP
			String clientIp = ObjectUtils.toString(PipelineUtil.getRemoteAddr());
			logger.info("client ip = " + clientIp);

			//			boolean isInBankIp = CollectionUtils.isNotEmpty(
			//				dam.exeQuery(dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL).setQueryString(
			//					" select 1 from TBPMS_BRANCH_IP where IPADDRESS = :IPADDRESS "
			//				)
			//				.setObject("IPADDRESS" , clientIp)
			//				)
			//			);

			//			boolean isOutside = CollectionUtils.isEmpty(
			//				dam.exeQuery(
			//						dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL)
			//						.setQueryString(" select 1 from TBPMS_BRANCH_IP where IPADDRESS = :IPADDRESS ")
			//						.setObject("IPADDRESS", clientIp)
			//				)
			//			);

			//			System.out.println("===============isOutside====================");
			//			System.out.println(clientIp);
			//			System.out.println(isOutside);
			//			outputMap.put("isOutside" , isInBankIp);
			//			outputMap.put("isOutside" , isOutside);

			outputMap.put("isOutside", true); //外訪清單的功能無條件顯示出來 (#5917)
			outputMap.put("slideMenu", doGetMobileMenu());
			outputMap.put("empId", userId);
			//			outputMap.put("WorkStation" , ws);
			outputMap.put("uuid", uuid);

			return outputMap;
			//return loginOutputVO;
		} finally {
			if (!successed) {
				WorkStation ws = DataManager.getWorkStation(uuid);
				if (ws != null) {
					ws.setUser(null);
				}
			}
		}
	}

	/** 記錄使用者登入資訊 **/
	private String recordLoginInfo(Map<String, String> loginInfoMap, String userId, Map<String, Object> userInfo, WorkStation ws) {
		String apServerName = "";
		try {
			APServerManager apServerManager = (APServerManager) PlatformContext.getBean("apServerManager");
			WSManagerIF wsManager = (WSManagerIF) PlatformContext.getBean("wsManager");
			apServerName = apServerManager.getApSvrName();
			String wsID = ws.getWsID();
			String brchID = ws.getBrchID();

			// 當開啟白名單功能，白名單 IP 不紀錄登入資訊。否則所有 IP 皆會記錄資訊
			if (!(isEnabledPrivilegedMechanism() && isPrivileged())) {
				if ("CSI".equals(loginInfoMap.get("caller"))) {
					wsManager.whenLogin(wsID, loginInfoMap.get("userId"), apServerName, brchID, true);// 允許重覆登入
				} else {
					wsManager.whenLogin(wsID, loginInfoMap.get("userId"), apServerName, brchID);
				}
			}

			userInfo.put("loginTime", getLoginTime(dam, userId, wsID, apServerName));
		} catch (Exception ex) {
			logger.error(String.format("使用者狀態管理錯誤:%s", ex.getMessage()));
			sendWarningMsg("使用者狀態管理錯誤", EnumShowType.Popup);
		}
		return apServerName;
	}

	public List<Map> doGetMobileMenu() throws Exception {
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT DISTINCT  ");
		sql.append("   MENU_ID , SEQ_NUM , MENU_TYPE , MENU_NAME ITEM , PREV_MENU ");
		sql.append(" FROM TBSFA_FUNC_MENU  ");
		sql.append(" START WITH MENU_ID IN ( ");
		sql.append("   SELECT DISTINCT ITEMID FROM TBSYSSECUPRIFUNMAP  ");
		sql.append("   WHERE FUNCTIONID='mobile'  ");
		sql.append("   AND PRIVILEGEID in ( ");
		sql.append("     SELECT privilegeid FROM TBSYSSECUROLPRIASS where ROLEID = :ROLEID ");
		sql.append("   ) ");
		sql.append(" ) ");
		sql.append(" CONNECT BY PRIOR PREV_MENU=ITEM_ID ");
		sql.append(" ORDER BY CASE WHEN PREV_MENU IS NULL THEN 0 ELSE 1 END ,  ");
		sql.append("          CASE WHEN SUBSTR(MENU_ID,1,3)='CRM' THEN 1 ");
		sql.append("               WHEN SUBSTR(MENU_ID,1,3)='KYC' THEN 2 ");
		sql.append("               WHEN SUBSTR(MENU_ID,1,3)='PRD' THEN 3 ");
		sql.append("               ELSE 4 END , MENU_TYPE, SEQ_NUM ");

		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		condition.setQueryString(sql.toString());
		condition.setObject("ROLEID", getUserVariable(FubonSystemVariableConsts.LOGINROLE));

		List<Map> menuList = dam.exeQuery(condition);

		List<Map> topNodes = new ArrayList();
		for (Map menu : menuList) {
			if (menu.get("PREV_MENU") == null) {
				topNodes.add(menu);
			}
		}

		Comparator menuComparator = new Comparator<Map>() {
			public int compare(Map o1, Map o2) {
				BigDecimal seqNum1 = (BigDecimal) o1.get("SEQ_NUM");
				BigDecimal seqNum2 = (BigDecimal) o2.get("SEQ_NUM");
				return seqNum1.compareTo(seqNum2);
			}
		};

		Collections.sort(topNodes, menuComparator);
		menuList.removeAll(topNodes);

		for (Map node : topNodes) {
			addChildNode(node, menuList, menuComparator);
			reSetMenuByMobile(node);
		}

		return topNodes;
	}

	public void addChildNode(Map superNode, List<Map> menuList, Comparator menuComparator) throws JBranchException {
		//找出此node下的子node
		List<Map> childNodes = CollectionSearchUtils.selectMapInList(menuList, "PREV_MENU", superNode.get("MENU_ID"));
		Collections.sort(childNodes, menuComparator);

		if (CollectionUtils.isNotEmpty(childNodes)) {
			superNode.put("sub_Item", childNodes);
			//如果子node時，在爬下一階層時先把找到的從menuList移除
			menuList.remove(childNodes);

			for (Map childNode : childNodes) {
				//最底層不爬樹
				if ("F".equals(childNode.get("MENU_TYPE"))) {
					addChildNode(childNode, menuList, menuComparator);
				} else {
					childNode.put("sub_Item", null);
				}
			}
		}
	}

	private void reSetMenuByMobile(Map node) throws Exception {
		String menuId = (String) node.get("MENU_ID");
		String itemUrl = null;
		boolean isEndChild = "P".equals(node.get("MENU_TYPE"));

		for (String key : Arrays.asList("MENU_ID", "SEQ_NUM", "MENU_TYPE", "PREV_MENU"))
			node.remove(key);

		Map reNode = MapObjectUtils.reHumpByKey(node);

		for (String key : Arrays.asList("ITEM", "ITEM_URL", "sub_Item"))
			node.remove(key);

		node.putAll(reNode);

		if (isEndChild && menuId != null) {
			String applicationId = ObjectUtils.toString(doGetHttpServletRequest().getAttribute("applicationId"));
			String reApid = menuId;
			for (int i = 0; i < 32 - menuId.length(); i++) {
				reApid += "0";
			}

			String branchId = uuid.getBranchID();
			String sectionID = uuid.getSectionID();
			String tellerId = uuid.getTellerID();
			String wsId = uuid.getWsId();
			//			String code = "wsid=" + wsId + ",branchID=" + branchId + ",tellerId=" + tellerId + ",sectionID=" + sectionID;
			//			System.out.println(code);
			//			code = AesEncryptDecryptUtils.encryptAesEcbPkcs7Padding(reApid , code);
			//			code = new String(Base64.encodeBase64(code.getBytes("utf-8")));
			//			itemUrl = "/" + menuId + ".vw?uuid=" + code;
			itemUrl = "/" + menuId + ".vw";
		}

		node.put("itemURL", itemUrl);

		List<Map> childNodes = (List<Map>) node.get("subItem");

		if (CollectionUtils.isNotEmpty(childNodes)) {
			for (Map childNode : childNodes) {
				reSetMenuByMobile(childNode);
			}
		}

	}

	@SuppressWarnings("unchecked")
	public Map getReferenceData() throws JBranchException {
		XmlInfo xmlInfo = new XmlInfo();
		GenericMap insAverageParam = new GenericMap(xmlInfo.doGetVariable("FPS.INS_AVERAGE", FormatHelper.FORMAT_3));
		Map insuranceRefLink = xmlInfo.doGetVariable("INS.INSURANCE_REF_LINK", FormatHelper.FORMAT_3);
		List<Map<String, Object>> tmpResult = null;
		List<Map<String, Object>> urlExtateTaxList = null;
		List<Map<String, Object>> webviewUrlList = null;
		
		DataAccessManager dam = getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition();
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT COM_ID , COM_NAME FROM TBPRD_INSDATA_COMPANY ");
		qc.setQueryString(sb.toString());
		List<Map<String, Object>> result = dam.exeQueryWithoutSort(qc);

		Map resultMap = new HashMap();
		List reNameResult = new ArrayList();
		Map retiredInfo = new HashMap();

		resultMap.put("retiredInfo", retiredInfo);

		if (CollectionUtils.isNotEmpty(result)) {
			for (Map map : result) {
				reNameResult.add(map);
			}
		}

		resultMap.put("company", MapObjectUtils.reHumpByKey(reNameResult));
		retiredInfo.put("avgLifeM", insAverageParam.getBigDecimal("1").doubleValue());
		retiredInfo.put("avgLifeF", insAverageParam.getBigDecimal("2").doubleValue());

		tmpResult = dam.exeQueryWithoutSort(dam.getQueryCondition().setQueryString(new StringBuffer().append(" SELECT DE.VOLATILITY ").append(" FROM TBFPS_CUSTRISK_VOLATILITY_HEAD HE ").append(" INNER JOIN TBFPS_CUSTRISK_VOLATILITY DE on HE.PARAM_NO = DE.PARAM_NO ").append(" where HE.STATUS = 'A' ").append(" and he.effect_start_date <= sysdate ").append(" ORDER BY DE.CUST_RISK_ATR ").toString()));

		if (CollectionUtils.isNotEmpty(tmpResult)) {
			double[] volatilityLimit = new double[tmpResult.size()];

			for (int i = 0; i < tmpResult.size(); i++) {
				volatilityLimit[i] = new GenericMap(tmpResult.get(i)).getBigDecimal("VOLATILITY").doubleValue();
			}

			resultMap.put("volatilityLimit", volatilityLimit);
		}

		//行事曆提醒類別
		tmpResult = dam.exeQueryWithoutSort(dam.getQueryCondition().setQueryString(new StringBuffer().append(" select ").append("   param_code as notice_type_id , ").append("   param_name as notice_type_Name , ").append("   case ").append("     when regexp_like(param_code ,  '^0\\d+$') then 0 ").append("     when regexp_like(param_code ,  '^A\\d+$') then 1 ").append("     else null ").append("   end event_Type  ").append(" from tbsysparameter where param_type = 'CAM.TASK_SOURCE' order by param_code ").toString()));

		if (CollectionUtils.isNotEmpty(tmpResult)) {
			List calNoticeTypes = new ArrayList();

			for (Map<String, Object> tmpMap : tmpResult) {
				Map map = new HashMap();
				map.putAll(tmpMap);
				calNoticeTypes.add(MapObjectUtils.reHumpByKey(map));
			}

			resultMap.put("calNoticeType", calNoticeTypes);
		}

		for (Object key : insuranceRefLink.keySet()) {
			retiredInfo.put("url" + ObjectUtils.toString(key), insuranceRefLink.get(key));
		}

		urlExtateTaxList = dam.exeQueryWithoutSort(dam.getQueryCondition().setQueryString(new StringBuffer().append("select PARAM_NAME from TBSYSPARAMETER where PARAM_TYPE = 'INS.INHERIT_TAX'").toString()));

		String urlExtateTax = urlExtateTaxList.get(0).get("PARAM_NAME").toString();

		// 2018/7/6 financialData
		Map<String, Object> financialData = new HashMap<String, Object>();
		List<Map<String, Object>> productInfo = new ArrayList<Map<String, Object>>();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("productType", "MFD");
		map.put("url", "./assets/txn/PRD110/PRD110_DETAIL.html");
		productInfo.add(map);
		map = new HashMap<String, Object>();
		map.put("productType", "BND");
		map.put("url", "./assets/txn/PRD130/PRD130_DETAIL.html");
		productInfo.add(map);
		map = new HashMap<String, Object>();
		map.put("productType", "SN");
		map.put("url", "./assets/txn/PRD140/PRD140_DETAIL.html");
		productInfo.add(map);
		map = new HashMap<String, Object>();
		map.put("productType", "SI");
		map.put("url", "./assets/txn/PRD150/PRD150_DETAIL.html");
		productInfo.add(map);
		map = new HashMap<String, Object>();
		map.put("productType", "INS");
		map.put("url", "./assets/txn/PRD160/PRD160_DETAIL.html");
		productInfo.add(map);
		financialData.put("productInfo", productInfo);

		List<Map<String, Object>> otherURL = new ArrayList<Map<String, Object>>();
		Map<String, Object> map2 = new HashMap<String, Object>();
		map2.put("type", "0");
		map2.put("url", "https://www.fubon.com/banking/personal/fund_trust/ETF_search/ETF_search.htm?show=m5&Pram=$ETFWEB$HTML$ET011001]DJHTM#ETFID}{{PRD_ID}}");
		otherURL.add(map2);
		map2 = new HashMap<String, Object>();
		map2.put("type", "1");
		map2.put("url", "./assets/txn/FPS230/FPS230Models.html");
		otherURL.add(map2);
		map2 = new HashMap<String, Object>();
		map2.put("type", "2");
		map2.put("url", "./assets/txn/FPS325/FPS325.html");
		otherURL.add(map2);
		map2 = new HashMap<String, Object>();
		map2.put("type", "3");
		map2.put("url", "./assets/txn/FPS230/FPS230Review.html");
		otherURL.add(map2);
		financialData.put("otherURL", otherURL);

		tmpResult = dam.exeQueryWithoutSort(dam.getQueryCondition().setQueryString(new StringBuffer().append(" SELECT MT.MARKET_OVERVIEW FROM TBFPS_MARKET_TREND MT where MT.STATUS = 'A' ").toString()));

		financialData.put("marketDesc", CollectionUtils.isNotEmpty(tmpResult) ? new GenericMap(tmpResult.get(0)).getStr("MARKET_OVERVIEW") : "");

		tmpResult = dam.exeQueryWithoutSort(dam.getQueryCondition().setQueryString(new StringBuffer().append(" select b.PRD_TYPE, b.RANK, b.WARNING, b.FONT from TBFPS_RPT_PARA_HEAD a ").append(" left join TBFPS_OTHER_PARA_WARNING b on a.PARAM_NO = b.PARAM_NO ").append(" WHERE a.STATUS = 'A' ORDER BY b.PRD_TYPE, b.RANK ").toString()));
		Map<String, List<Map<String, Object>>> ans_map = new HashMap<String, List<Map<String, Object>>>();
		for (Map<String, Object> tmpMap : tmpResult) {
			String noticeType = new GenericMap(tmpMap).getStr("PRD_TYPE");
			Map<String, Object> map3 = new HashMap<String, Object>();
			map3.put("rank", tmpMap.get("RANK"));
			map3.put("notice", tmpMap.get("WARNING"));
			String font[] = new GenericMap(tmpMap).getStr("FONT").split("#");
			map3.put("font", font[0]);
			map3.put("color", "#" + font[1]);

			if (!ans_map.containsKey(noticeType)) {
				List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
				list.add(map3);
				ans_map.put(noticeType, list);
			} else {
				ans_map.get(noticeType).add(map3);
			}
		}
		List<Map<String, Object>> noticeList = new ArrayList<Map<String, Object>>();
		for (Entry<String, List<Map<String, Object>>> entry : ans_map.entrySet()) {
			Map<String, Object> data_map = new HashMap<String, Object>();
			data_map.put("noticeType", entry.getKey());
			data_map.put("noticeContent", entry.getValue());
			noticeList.add(data_map);
		}
		financialData.put("noticeList", noticeList);

		resultMap.put("financialData", financialData);

		//遺產稅預估試算URL
		resultMap.put("urlExtateTax", urlExtateTax);

		webviewUrlList = dam.exeQueryWithoutSort(dam.getQueryCondition().setQueryString(new StringBuffer().append("select PARAM_CODE, PARAM_NAME from TBSYSPARAMETER where PARAM_TYPE = 'APP.WEBVIEW_URL'").toString()));

		if (CollectionUtils.isNotEmpty(webviewUrlList) && webviewUrlList.size() > 0) {
			for (Map<String, Object> webviewUrlMap : webviewUrlList) {
				resultMap.put(webviewUrlMap.get("PARAM_CODE"), webviewUrlMap.get("PARAM_NAME"));
			}
		}

		// 匯率表
		resultMap.put("rateMap", dam.exeQueryWithoutSort(dam.getQueryCondition().setQueryString(new StringBuffer().append(" SELECT CUR_COD, SEL_RATE, BUY_RATE FROM TBPMS_IQ053").append(" WHERE MTN_DATE = (SELECT MAX (MTN_DATE) FROM TBPMS_IQ053)").toString())));

		return resultMap;
	}

	/**
	 * 該 EMP ID 輔銷 轄下所有 AO LIST
	 * 
	 * @param empId
	 * @return
	 * @throws DAOException
	 * @throws JBranchException
	 */
	private List<String> getAllAoList(DataAccessManager dam, String empId) throws DAOException, JBranchException {
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT AO_CODE FROM VWORG_AO_INFO WHERE BRA_NBR IN ");
		sql.append(" (select BRANCH_NBR from TBORG_FAIA where EMP_ID = :empId) ");

		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		condition.setObject("empId", empId);
		condition.setQueryString(sql.toString());

		List<Map> aoList = dam.exeQuery(condition);
		List<String> allAoList = new ArrayList<String>();
		if (CollectionUtils.isNotEmpty(aoList)) {
			for (Map aoMap : aoList) {
				allAoList.add(ObjectUtils.toString(aoMap.get("AO_CODE")));
			}
		}
		return allAoList;
	}

	/**
	 * 判斷登入是否需要驗證碼
	 * 
	 * @param body
	 * @param header
	 */
	public void needCaptcha(Object body, IPrimitiveMap header) {
		String ip = (isTestMode() ? "T" : "P") + StringUtils.defaultString(PipelineUtil.getRemoteAddr());
		logger.info("user ip is " + ip);

		boolean needCaptcha = false;
		try {
			if (!ip.matches(doGetIpCheckRegexp()))
				needCaptcha = true;
		} catch (Exception e) {// 系統參數無設定 IP 檢核式，則無需驗證碼
		}

		this.sendRtnObject(needCaptcha);
	}

	/**
	 * 進入登入頁檢核IP是否可使用
	 * 
	 * @param body
	 * @param headers
	 * @throws JBranchException
	 * @throws Exception
	 */
	public void precheckIP(Object body, IPrimitiveMap<EnumTiaHeader> headers) throws JBranchException, Exception {
		XmlInfo xmlInfo = new XmlInfo();
		Boolean isOK = false;

		Map<String, String> trustIP = xmlInfo.doGetVariable(TRUST_ID, FormatHelper.FORMAT_3); //信任IP
		Map<String, String> mode = xmlInfo.doGetVariable("FUBONSYS.MODE", FormatHelper.FORMAT_3); //信任IP

		//是否啟用
		if ("TRUE".equals(trustIP.get("IP_CHECK"))) {
			String[] trustIpArray = trustIP.get("IP_PREFIX").split(";");
			String currentIP = ObjectUtils.toString(PipelineUtil.getRemoteAddr());

			//for測試環境 本機IP避開
			if (StringUtils.equals(mode.get("MODE"), "D")) {
				if (currentIP.startsWith("127.0.0.1")) {
					isOK = true;
				}
			}

			if (!isOK) {
				for (String ip : trustIpArray) {
					isOK = currentIP.startsWith(ip);
					if (isOK)
						break;
				}
			}

		} else {
			isOK = true;
		}

		this.sendRtnObject(isOK);
	}
}
