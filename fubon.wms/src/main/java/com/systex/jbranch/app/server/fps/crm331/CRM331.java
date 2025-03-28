package com.systex.jbranch.app.server.fps.crm331;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBCAM_SFA_LEADS_IMPVO;
import com.systex.jbranch.app.common.fps.table.TBCAM_SFA_LE_IMP_TEMPVO;
import com.systex.jbranch.app.common.fps.table.TBCRM_CUST_AOCODE_CHGLOGVO;
import com.systex.jbranch.app.common.fps.table.TBCRM_CUST_MASTVO;
import com.systex.jbranch.app.common.fps.table.TBCRM_TRS_AOCHG_PLISTVO;
import com.systex.jbranch.app.server.fps.crm210.CRM210;
import com.systex.jbranch.app.server.fps.crm210.CRM210OutputVO;
import com.systex.jbranch.app.server.fps.crm210.CRM210_ALLInputVO;
import com.systex.jbranch.app.server.fps.crm341.CRM341;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataManager.WorkStation;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * MENU
 * 
 * @author walalala
 * @date 2016/08/23
 * @spec null
 */
@Component("crm331")
@Scope("request")
public class CRM331 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(CRM331.class);

	public void inquire(Object body, IPrimitiveMap header) throws JBranchException {
		WorkStation ws = DataManager.getWorkStation(uuid);
		CRM331InputVO inputVO = (CRM331InputVO) body;
		CRM210_ALLInputVO inputVO_all = new CRM210_ALLInputVO();
		inputVO_all.setCrm331inputVO(inputVO);
		inputVO_all.setCrm210inputVO(inputVO);
		inputVO_all.setAvailRegionList(getUserVariable(FubonSystemVariableConsts.AVAILREGIONLIST));
		inputVO_all.setAvailAreaList(getUserVariable(FubonSystemVariableConsts.AVAILAREALIST));
		inputVO_all.setAvailBranchList(getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
		inputVO_all.setLoginEmpID(ws.getUser().getUserID());
		inputVO_all.setLoginRole(getUserVariable(FubonSystemVariableConsts.LOGINROLE));
		
		String loginToken = "";
		Object loginAoCode = null;
		
		CRM210 crm210 = (CRM210) PlatformContext.getBean("crm210");
		CRM210OutputVO outputVO_crm210 = new CRM210OutputVO();
		outputVO_crm210 = crm210.inquire_common(inputVO_all, "CRM331", loginToken, loginAoCode);

		this.sendRtnObject(outputVO_crm210);
	}

	// 2017/6/23
	public void checkadd(Object body, IPrimitiveMap header) throws JBranchException {
		CRM331InputVO inputVO = (CRM331InputVO) body;
		CRM331OutputVO outputVO = new CRM331OutputVO();
		dam = this.getDataAccessManager();
		
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> fcMap = new HashMap(xmlInfo.doGetVariable("FUBONSYS.FC_ROLE", FormatHelper.FORMAT_2));	//理專
		Map<String, String> fchMap = new HashMap(xmlInfo.doGetVariable("FUBONSYS.FCH_ROLE", FormatHelper.FORMAT_2));	//FCH理專
		
		// get new role
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT RO.ROLE_ID as NEW_ROLE_ID, RO.ROLE_NAME as NEW_ROLE_NAME FROM VWORG_AO_INFO ");
		sql.append("LEFT JOIN (SELECT MR.EMP_ID, RO.ROLE_ID, RO.ROLE_NAME, RO.JOB_TITLE_NAME FROM TBORG_MEMBER_ROLE MR, TBORG_ROLE RO ");
		sql.append("WHERE MR.ROLE_ID = RO.ROLE_ID AND MR.IS_PRIMARY_ROLE = 'Y' AND RO.REVIEW_STATUS = 'Y' AND RO.IS_AO = 'Y') RO ON VWORG_AO_INFO.EMP_ID = RO.EMP_ID ");
		sql.append("WHERE AO_CODE = :ao_code ");
		queryCondition.setObject("ao_code", inputVO.getNew_ao_code());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> ao_list = dam.exeQuery(queryCondition);
		if(ao_list.size() == 0)
			throw new APException("該理專無角色，請先設定角色");
		String NEW_ROLE_ID = ObjectUtils.toString(ao_list.get(0).get("NEW_ROLE_ID"));
		String NEW_ROLE_NAME = ObjectUtils.toString(ao_list.get(0).get("NEW_ROLE_NAME"));
		//
		Boolean Check1 = false;
		Boolean Check2 = false;
		Boolean Check3 = false;
		List<String> res3 = new ArrayList<String>();
		Boolean Check4 = false;
		List<String> res4 = new ArrayList<String>();
		Boolean Check5 = false;
		List<String> res5 = new ArrayList<String>();
		Boolean Check6 = false;
		List<String> res6 = new ArrayList<String>();
		Boolean Check7 = false;  //十保專區檢查, 半年內不可移回原理專
		List<String> res7 = new ArrayList<String>();
		Boolean Check8 = false;  //主CODE客戶不可移轉至維護CODE
		List<String> res8 = new ArrayList<String>();
		Boolean Check9 = false;  //禁銷戶檢核
		List<String> res9 = new ArrayList<String>();
		Boolean Check10 = false;  //2022換手名單：已換手經營客戶未滿6個月不可移轉回原個金RM
		List<String> res10 = new ArrayList<String>();
		Boolean Check11 = false;  //客戶在ON CODE排除名單中，不可做移轉
		List<String> res11 = new ArrayList<String>();
		Boolean Check12 = false;  //2023必輪調：區域分行非核心客戶一年內移回原理專
		List<String> res12 = new ArrayList<String>();
		Boolean Check13 = false;  //2023必輪調名單：必輪調RM名單上傳後，名單中客戶不可做移入申請
		List<String> res13 = new ArrayList<String>();
		Boolean Check14 = false;  //2023必輪調名單：必輪調RM名單上傳後，名單中RM不可做移入申請
		List<String> res14 = new ArrayList<String>();
		Boolean Check15 = false;  //必輪調名單：RM輪調後，帶走30%核心客戶，一年內不得再帶走該RM轄下原分行70%客戶
		List<String> res15 = new ArrayList<String>();
		Boolean Check16 = false; //分行不可於客戶移轉篩選及主管覆核客戶移轉中，將分行客戶移入私銀
		List<String> res16 = new ArrayList<String>();
		
		// follow crm341 check
		// 檢核是否超過客戶數上限  NEW_ROLE_ID in (FC1, FC2, FC3, FC4, FC5)
		if (fcMap.containsKey(NEW_ROLE_ID)) {
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql = new StringBuffer();
			sql.append("SELECT COUNT(1) CUR_CNT FROM TBCRM_CUST_MAST WHERE AO_CODE = :ao_code ");
			queryCondition.setObject("ao_code", inputVO.getNew_ao_code());
			queryCondition.setQueryString(sql.toString());
			List<Map<String, Object>> list = dam.exeQuery(queryCondition);
			BigDecimal CUR_CNT = new BigDecimal(ObjectUtils.toString(list.get(0).get("CUR_CNT")));
			
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql = new StringBuffer();
			sql.append("SELECT S.TTL_CUST_NO_LIMIT_UP FROM TBCRM_TRS_CUST_MGMT_SET S WHERE S.AO_JOB_RANK = :role_id ");
			queryCondition.setObject("role_id", NEW_ROLE_ID);
			queryCondition.setQueryString(sql.toString());
			List<Map<String, Object>> list2 = dam.exeQuery(queryCondition);
			BigDecimal TTL_CUST_NO_LIMIT_UP = null;
			if(list2.size() > 0)
				TTL_CUST_NO_LIMIT_UP = new BigDecimal(ObjectUtils.toString(list2.get(0).get("TTL_CUST_NO_LIMIT_UP")));
			
			if(list2.size() == 0) {
				Check1 = true;
			}
			else if (CUR_CNT.add(new BigDecimal(1)).compareTo(TTL_CUST_NO_LIMIT_UP) > 0) {
				Check2 = true;
			}
		}
		
		for (Map<String,Object> map : inputVO.getConfirm_list()) {
			// 檢核該客戶是否在待覆核流程
			// 2017/12/18 jacky 2個檢查合為一個 SQL
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql = new StringBuffer();
			sql.append("SELECT COUNT(1) FROM TBCRM_TRS_AOCHG_PLIST WHERE 1=1 ");
			sql.append("AND CUST_ID = :cust_id ");
			sql.append("AND PROCESS_STATUS in ('L0', 'L1', 'L2', 'L3', 'L4', 'L5') ");
			sql.append("AND TRS_TYPE NOT IN ('7', '8', '9') ");
			queryCondition.setObject("cust_id", map.get("CUST_ID"));
			queryCondition.setQueryString(sql.toString());
			List<Map<String, Object>> list = dam.exeQuery(queryCondition);
			if(list.size() > 0) {
				BigDecimal COUNT = new BigDecimal(ObjectUtils.toString(list.get(0).get("COUNT(1)")));
				if(COUNT.compareTo(new BigDecimal(0)) > 0) {
					Check3 = true;
					res3.add(ObjectUtils.toString(map.get("CUST_ID")));
				}
			}
			// 重複申請
			
			if(!Check3) {
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sql = new StringBuffer();
				sql.append("SELECT COUNT(1) FROM TBCRM_TRS_AOCHG_PLIST WHERE 1=1 ");
				sql.append("AND NEW_AO_CODE = :ao_code ");
				sql.append("AND CUST_ID = :cust_id ");
				sql.append("AND PROCESS_STATUS in ('L0', 'L1', 'L2', 'L3', 'L4', 'L5') ");
				// 2018/02/09 Mantis 0004222: 客戶移轉篩選 
				// 換手名單允許被添加入待移轉清單，加入時作廢原客戶帶移轉清單 sen
				sql.append("AND TRS_TYPE NOT IN ('7', '8', '9') ");
				queryCondition.setObject("ao_code", inputVO.getNew_ao_code());
				queryCondition.setObject("cust_id", map.get("CUST_ID"));
				queryCondition.setQueryString(sql.toString());
				List<Map<String, Object>> list3 = dam.exeQuery(queryCondition);
				if(list3.size() > 0) {
					BigDecimal COUNT = new BigDecimal(ObjectUtils.toString(list3.get(0).get("COUNT(1)")));
					if(COUNT.compareTo(new BigDecimal(0)) > 0) {
						Check3 = true;
						res3.add(ObjectUtils.toString(map.get("CUST_ID")));
					}
				}
			}
			// 檢核禁移條件
			// FC移到FCH
			if (fcMap.containsKey(map.get("ROLE_ID")) && fchMap.containsKey(NEW_ROLE_ID)) {
				Check4 = true;
				res4.add(ObjectUtils.toString(map.get("CUST_ID")));
			}
			// 計績FCH移到維護FCH
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql = new StringBuffer();
			sql.append("SELECT A1.TYPE as OLD_TYPE, A2.TYPE as NEW_TYPE FROM TBORG_SALES_AOCODE A1, TBORG_SALES_AOCODE A2 ");
			sql.append("WHERE A1.AO_CODE = :ao_code ");
			sql.append("AND A2.AO_CODE = :ao_code2 ");
			queryCondition.setObject("ao_code", map.get("AO_CODE"));
			queryCondition.setObject("ao_code2", inputVO.getNew_ao_code());
			queryCondition.setQueryString(sql.toString());
			List<Map<String, Object>> list2 = dam.exeQuery(queryCondition);
			if(list2.size() > 0) {
				String OLD_TYPE = ObjectUtils.toString(list2.get(0).get("OLD_TYPE"));
				String NEW_TYPE = ObjectUtils.toString(list2.get(0).get("NEW_TYPE"));
				if("1".equals(OLD_TYPE) && fchMap.containsKey(map.get("ROLE_ID"))
						&& "3".equals(NEW_TYPE) && fchMap.containsKey(NEW_ROLE_ID)) {
					Check5 = true;
					res5.add(ObjectUtils.toString(map.get("CUST_ID")));
				}
			}
			// 公司戶與公司負責人Code不同
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql = new StringBuffer();
			sql.append("SELECT M1.CUST_ID, TRIM(M1.RPRS_ID) as RPRS_ID, M2.AO_CODE FROM TBCRM_CUST_MAST M1 ");
			sql.append("LEFT JOIN TBCRM_CUST_MAST M2 ON TRIM(M1.RPRS_ID) = M2.CUST_ID WHERE 1=1 ");
			sql.append("AND LENGTH(M1.CUST_ID) < 10 AND M1.CUST_ID = :cust_id ");
			queryCondition.setObject("cust_id", map.get("CUST_ID"));
			queryCondition.setQueryString(sql.toString());
			List<Map<String, Object>> list3 = dam.exeQuery(queryCondition);
			if(list3.size() != 0) {
				// 有公司
				if(list3.get(0).get("RPRS_ID") != null && list3.get(0).get("AO_CODE") != null) {
					if(!inputVO.getNew_ao_code().equals(ObjectUtils.toString(list3.get(0).get("AO_CODE")))) {
						Check6 = true;
						res6.add(ObjectUtils.toString(map.get("CUST_ID")));
					}	
				}
			}
			
			//十保專區檢查-不可移回原理專
			//20200325 by Jacky 增加理專十保檢查
			CRM341 crm341 = (CRM341) PlatformContext.getBean("crm341");
			if(crm341.check10CMDTCust((String)map.get("CUST_ID"), inputVO.getNew_ao_code())){
				Check7 = true;
				res7.add(ObjectUtils.toString(map.get("CUST_ID")));
			}
			//必輪調名單：RM輪調後，帶走30%核心客戶，一年內不得再帶走該RM轄下原分行70%客戶
			String check2023CMDTCust3 = crm341.check2023CMDTCust3((String)map.get("CUST_ID"), inputVO.getNew_ao_code());
			if(StringUtils.equals("Y", check2023CMDTCust3)) {
				Check15 = true;
				res15.add(ObjectUtils.toString(map.get("CUST_ID")));
			}
			
			//2023必輪調名單：必輪調RM名單上傳後，名單中客戶不可做移入申請
			if(StringUtils.equals("Y", crm341.check2023CMDTCust2((String)map.get("CUST_ID")))){
				Check13 = true;
				res13.add(ObjectUtils.toString(map.get("CUST_ID")));
			}
			
			//2023必輪調名單：必輪調RM名單上傳後，名單中RM不可做移入申請
			if(StringUtils.equals("Y", check2023CMDTCust1(inputVO.getNew_ao_code()))){
				Check14 = true;
				res14.add(ObjectUtils.toString(map.get("CUST_ID")));
			}
			
			//2022換手名單：已換手經營客戶未滿6個月不可移轉回原個金RM(需簽署「客戶資產現況表申請書」及「客戶指定個金客戶經理自主聲明書」)
			if(StringUtils.equals("Y", crm341.check2022CMDTCust3((String)map.get("CUST_ID"), inputVO.getNew_ao_code()))){
				Check10 = true;
				res10.add(ObjectUtils.toString(map.get("CUST_ID")));
			}
			
			//2023必輪調：區域分行非核心客戶一年內移回原理專(需簽署「客戶資產現況表申請書」及「客戶指定個金客戶經理自主聲明書」)
			if(StringUtils.equals("Y", crm341.check2023CMDTCust4((String)map.get("CUST_ID"), inputVO.getNew_ao_code()))){
				Check12 = true;
				res12.add(ObjectUtils.toString(map.get("CUST_ID")));
			}
			
			//#2225: 主CODE移轉規則調整，主CODE客戶不可移轉至維護CODE
			if(StringUtils.equals("1", ObjectUtils.toString(map.get("AO_CODE_TYPE"))) && StringUtils.equals("3", inputVO.getNew_ao_type())) {
				Check8 = true;
				res8.add(ObjectUtils.toString(map.get("CUST_ID")));
			}
			
			//禁銷戶
			if(StringUtils.equals(ObjectUtils.toString(map.get("COMM_NS_YN")), "Y")) {
				Check9 = true;
				res9.add(ObjectUtils.toString(map.get("CUST_ID")));
			}
			
			//是否在ONCODE客戶排除列表中
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql = new StringBuffer();
			sql.append("SELECT 1 FROM TBCRM_TRS_CUST_EXCLUDE WHERE CUST_ID = :cust_id AND NVL(DEL_YN, 'N') = 'N' ");
			queryCondition.setObject("cust_id", map.get("CUST_ID"));
			queryCondition.setQueryString(sql.toString());
			List<Map<String, Object>> list4 = dam.exeQuery(queryCondition);
			if(CollectionUtils.isNotEmpty(list4)) {
				Check11 = true;
				res11.add(ObjectUtils.toString(map.get("CUST_ID")));
			}
			
			//分行不可於客戶移轉篩選及主管覆核客戶移轉中，將分行客戶移入私銀
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql = new StringBuffer();
			sql.append("SELECT 1 FROM VWORG_EMP_UHRM_INFO WHERE UHRM_CODE = :newAoCode ");
			queryCondition.setObject("newAoCode", inputVO.getNew_ao_code());
			queryCondition.setQueryString(sql.toString());
			List<Map<String, Object>> list5 = dam.exeQuery(queryCondition);
			if(CollectionUtils.isNotEmpty(list5)) {
				Check16 = true;
				res16.add(ObjectUtils.toString(map.get("CUST_ID")));
			}
		}
		
		outputVO.setRole_name(NEW_ROLE_NAME);
		if(Check1) {
			outputVO.setResultList2("ERR1");
		} else if(Check3) {
			outputVO.setResultList(res3);
			outputVO.setResultList2("ERR3");
		} else if(Check4) {
			outputVO.setResultList(res4);
			outputVO.setResultList2("ERR4");
		} else if(Check5) {
			outputVO.setResultList(res5);
			outputVO.setResultList2("ERR5");
		} else if(Check6) {
			outputVO.setResultList(res6);
			outputVO.setResultList2("ERR6");
		} else if(Check7) {
			outputVO.setResultList(res7); //十保檢查
			outputVO.setResultList2("ERR7");
		} else if(Check8) {
			outputVO.setResultList(res8); //主CODE客戶不可移轉至維護CODE
			outputVO.setResultList2("ERR8");
		} else if(Check9) {
			outputVO.setResultList(res9); //禁銷戶
			outputVO.setResultList2("ERR9");
		} else if(Check2) {
			outputVO.setResultList2("ERR2");
		} else if(Check10) {
			outputVO.setResultList(res10); //2022換手檢核
			outputVO.setResultList2("ERR10"); 
		} else if(Check11) {
			outputVO.setResultList(res11); //在ONCODE客戶排除列表中
			outputVO.setResultList2("ERR11"); 
		} else if(Check12) {
			outputVO.setResultList(res12); //2022必輪調區域分行
			outputVO.setResultList2("ERR12"); 
		} else if(Check13) {
			outputVO.setResultList(res13); //2023必輪調名單：必輪調RM名單上傳後，名單中客戶不可做移入申請
			outputVO.setResultList2("ERR13"); 
		} else if(Check14) {
			outputVO.setResultList(res14); //2023必輪調名單：必輪調RM名單上傳後，名單中RM不可做移入申請
			outputVO.setResultList2("ERR14"); 
		} else if(Check15) {
			outputVO.setResultList(res15); //必輪調名單：RM輪調後，帶走30%核心客戶，一年內不得再帶走該RM轄下原分行70%客戶
			outputVO.setResultList2("ERR15"); 
		} else if(Check16) {
			outputVO.setResultList(res16); //分行不可於客戶移轉篩選及主管覆核客戶移轉中，將分行客戶移入私銀
			outputVO.setResultList2("ERR16"); 
		} else {
			outputVO.setResultList2("GOOD");
		}
		
		this.sendRtnObject(outputVO);
	}
	
	
	// mantis 4240 : 同分行客戶移轉跑到待總行覆核
	// old code
	public void confirm(Object body, IPrimitiveMap header) throws Exception {
		CRM331InputVO inputVO = (CRM331InputVO) body;
		SimpleDateFormat HHMMSS = new SimpleDateFormat("HHmmss");
		dam = this.getDataAccessManager();
		
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> headmgrMap = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2);	//總行人員
		Map<String, String> armgrMap = xmlInfo.doGetVariable("FUBONSYS.ARMGR_ROLE", FormatHelper.FORMAT_2);		//業務處主管
		Map<String, String> mbrmgrMap = xmlInfo.doGetVariable("FUBONSYS.MBRMGR_ROLE", FormatHelper.FORMAT_2);	//營運督導
		Map<String, String> bmmgrMap = xmlInfo.doGetVariable("FUBONSYS.BMMGR_ROLE", FormatHelper.FORMAT_2);		//個金主管
		
		// mantis 0004267: 主管覆核客戶移轉 先埋log
		logger.info("CRM331登入角色為:" + (String)getUserVariable(FubonSystemVariableConsts.LOGINROLE));
		//加入待移轉清單
		if ("1".equals(inputVO.getProcess_mode())) {
			for (Map<String,Object> list : inputVO.getConfirm_list()) {
				// 2017/12/18 jacky
				this.cancelOther(ObjectUtils.toString(list.get("CUST_ID")));
				
				TBCRM_TRS_AOCHG_PLISTVO vo = new TBCRM_TRS_AOCHG_PLISTVO();
				vo.setSEQ(new BigDecimal(getPLIST_SEQ()));
				vo.setCUST_ID(ObjectUtils.toString(list.get("CUST_ID")));
				vo.setORG_AO_CODE(ObjectUtils.toString(list.get("AO_CODE")));
				vo.setORG_AO_BRH(ObjectUtils.toString(list.get("BRA_NBR")));
				vo.setNEW_AO_CODE(inputVO.getNew_ao_code());
				vo.setNEW_AO_BRH(inputVO.getNew_branch_nbr());
				vo.setAPL_EMP_ID(getUserVariable(FubonSystemVariableConsts.LOGINID).toString());
				vo.setAPL_EMP_ROLE(getUserVariable(FubonSystemVariableConsts.LOGINROLE).toString());
				vo.setAPL_DATETIME(new Timestamp(Calendar.getInstance().getTime().getTime()));
				vo.setAPL_REASON(inputVO.getApl_reason());
				if (StringUtils.isNotBlank(inputVO.getApl_reason_oth()))
					vo.setAPL_OTH_REASON(inputVO.getApl_reason_oth());
				
				// 判斷流程類型
				Map<String, String> fcmap = new HashMap<String, String>();
				fcmap.put("FC1", "");fcmap.put("FC2", "");fcmap.put("FC3", "");fcmap.put("FC4", "");fcmap.put("FC5", "");
				Map<String, String> fcmap2 = new HashMap<String, String>();
				fcmap2.put("FCH", "");fcmap2.put("FCH2", "");fcmap2.put("FCH3", "");fcmap2.put("FCH4", "");fcmap2.put("FCH5", "");
				String TRS_FLOW_TYPE = "";
				if (StringUtils.isBlank(ObjectUtils.toString(list.get("AO_CODE"))))
					TRS_FLOW_TYPE = "1";
				else if(StringUtils.equals(ObjectUtils.toString(list.get("BRA_NBR")), inputVO.getNew_branch_nbr()))
					TRS_FLOW_TYPE = "2";
				else if(fcmap2.containsKey(list.get("AO_06")) && !StringUtils.equals(ObjectUtils.toString(list.get("BRA_NBR")), inputVO.getNew_branch_nbr())) {
					TRS_FLOW_TYPE = "3";
				}
				else if(fcmap.containsKey(list.get("AO_06")) && !StringUtils.equals(ObjectUtils.toString(list.get("BRA_NBR")), inputVO.getNew_branch_nbr())) {
					TRS_FLOW_TYPE = "4";
				}
				// 2017/9/22 jacky:CUST_02正常一定有值, 空的當離職AO_CODE
				else if(StringUtils.isBlank(ObjectUtils.toString(list.get("BRA_NBR")))) {
					TRS_FLOW_TYPE = "1";
				}
				//
				vo.setTRS_FLOW_TYPE(TRS_FLOW_TYPE);
				// 2017/6/9
				if(armgrMap.containsKey((String)getUserVariable(FubonSystemVariableConsts.LOGINROLE)))
					vo.setPROCESS_STATUS("L3");
				else if(mbrmgrMap.containsKey((String)getUserVariable(FubonSystemVariableConsts.LOGINROLE)))
					vo.setPROCESS_STATUS("L2");
				else
					vo.setPROCESS_STATUS("L1");
				
				// mantis 0004267: 主管覆核客戶移轉 先埋log
//				logger.info("處理客戶為:" + ObjectUtils.toString(list.get("CUST_ID")));
				logger.info("處理流程為:" + vo.getPROCESS_STATUS());
				vo.setTRS_TYPE("6");
				vo.setTRS_TXN_SOURCE("1");
				dam.create(vo);
			}
		}
		//強制移轉
		else if ("2".equals(inputVO.getProcess_mode())) {
			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sql = new StringBuffer();
			sql.append("SELECT EMP_ID, EMP_NAME FROM VWORG_AO_INFO WHERE AO_CODE = :ao_code");
			queryCondition.setObject("ao_code", inputVO.getNew_ao_code());
			queryCondition.setQueryString(sql.toString());
			List<Map<String, String>> query = dam.exeQuery(queryCondition);
			
			Set<String> ao_List = new HashSet<String>();
			ao_List.add(inputVO.getNew_ao_code());
			for (Map<String,Object> list : inputVO.getConfirm_list()) {
				ao_List.add(ObjectUtils.toString(list.get("AO_CODE")));
				// 判斷流程類型
				Map<String, String> fcmap = new HashMap<String, String>();
				fcmap.put("FC1", "");fcmap.put("FC2", "");fcmap.put("FC3", "");fcmap.put("FC4", "");fcmap.put("FC5", "");
				Map<String, String> fcmap2 = new HashMap<String, String>();
				fcmap2.put("FCH", "");fcmap2.put("FCH2", "");fcmap2.put("FCH3", "");fcmap2.put("FCH4", "");fcmap2.put("FCH5", "");
				String TRS_FLOW_TYPE = "";
				if (StringUtils.isBlank(ObjectUtils.toString(list.get("AO_CODE"))))
					TRS_FLOW_TYPE = "1";
				else if(StringUtils.equals(ObjectUtils.toString(list.get("BRA_NBR")), inputVO.getNew_branch_nbr()))
					TRS_FLOW_TYPE = "2";
				else if(fcmap2.containsKey(list.get("AO_06")) && !StringUtils.equals(ObjectUtils.toString(list.get("BRA_NBR")), inputVO.getNew_branch_nbr())) {
					TRS_FLOW_TYPE = "3";
				}
				else if(fcmap.containsKey(list.get("AO_06")) && !StringUtils.equals(ObjectUtils.toString(list.get("BRA_NBR")), inputVO.getNew_branch_nbr())) {
					TRS_FLOW_TYPE = "4";
				}
				// 2017/9/22 jacky:CUST_02正常一定有值, 空的當離職AO_CODE
				else if(StringUtils.isBlank(ObjectUtils.toString(list.get("BRA_NBR")))) {
					TRS_FLOW_TYPE = "1";
				}
				//
				
				// 2017/12/18 jacky
				this.cancelOther(ObjectUtils.toString(list.get("CUST_ID")));
				//
				
				TBCRM_TRS_AOCHG_PLISTVO vo = new TBCRM_TRS_AOCHG_PLISTVO();
				vo.setSEQ(new BigDecimal(getPLIST_SEQ()));
				vo.setCUST_ID(ObjectUtils.toString(list.get("CUST_ID")));
				vo.setORG_AO_CODE(ObjectUtils.toString(list.get("AO_CODE")));
				vo.setORG_AO_BRH(ObjectUtils.toString(list.get("BRA_NBR")));
				vo.setNEW_AO_CODE(inputVO.getNew_ao_code());
				vo.setNEW_AO_BRH(inputVO.getNew_branch_nbr());
				vo.setAPL_EMP_ID(getUserVariable(FubonSystemVariableConsts.LOGINID).toString());
				vo.setAPL_EMP_ROLE(getUserVariable(FubonSystemVariableConsts.LOGINROLE).toString());
				vo.setAPL_DATETIME(new Timestamp(Calendar.getInstance().getTime().getTime()));
				vo.setAPL_REASON(inputVO.getApl_reason());
				if (StringUtils.isNotBlank(inputVO.getApl_reason_oth()))
					vo.setAPL_OTH_REASON(inputVO.getApl_reason_oth());
				vo.setHQ_MGR_RPL_DATETIME(new Timestamp(Calendar.getInstance().getTime().getTime()));
				vo.setHQ_MGR_RPL_STATUS("Y");
				vo.setTRS_FLOW_TYPE(TRS_FLOW_TYPE);
				vo.setPROCESS_STATUS("S");
				vo.setTRS_TYPE("6");
				vo.setTRS_TXN_SOURCE("1");
				vo.setACT_DATE(new Timestamp(Calendar.getInstance().getTime().getTime()));
				dam.create(vo);
				
				//======執行生效後動作======
				// Step1.更新客戶主檔，並回傳名單檔案更新主機資料
				TBCRM_CUST_MASTVO vo_mast = new TBCRM_CUST_MASTVO();
				vo_mast = (TBCRM_CUST_MASTVO) dam.findByPKey(TBCRM_CUST_MASTVO.TABLE_UID, ObjectUtils.toString(list.get("CUST_ID")));
				vo_mast.setAO_CODE(inputVO.getNew_ao_code());
				vo_mast.setBRA_NBR(inputVO.getNew_branch_nbr());
				vo_mast.setAO_LASTUPDATE(new Timestamp(Calendar.getInstance().getTime().getTime()));
		        dam.update(vo_mast);
		        
		        // Step2.新增一筆到移轉紀錄TBCRM_CUST_AOCHG_LOG
		        TBCRM_CUST_AOCODE_CHGLOGVO vo_log = new TBCRM_CUST_AOCODE_CHGLOGVO();
		        vo_log.setSEQ(getCHGLOG_SEQ());
		        vo_log.setCUST_ID(ObjectUtils.toString(list.get("CUST_ID")));
		        vo_log.setORG_AO_CODE(ObjectUtils.toString(list.get("AO_CODE")));
		        vo_log.setORG_AO_BRH(ObjectUtils.toString(list.get("BRA_NBR")));
		        vo_log.setORG_AO_NAME(ObjectUtils.toString(list.get("EMP_NAME")));
		        vo_log.setNEW_AO_CODE(inputVO.getNew_ao_code());
		        vo_log.setNEW_AO_BRH(inputVO.getNew_branch_nbr());
		        vo_log.setNEW_AO_NAME(query.get(0).get("EMP_NAME"));
		        vo_log.setREG_AOCODE_EMP_ID(getUserVariable(FubonSystemVariableConsts.LOGINID).toString());
		        vo_log.setREG_AOCODE_SUB_DATETIME(new Timestamp(Calendar.getInstance().getTime().getTime()));
				vo_log.setLETGO_EMP_ID(getUserVariable(FubonSystemVariableConsts.LOGINID).toString());
				vo_log.setLETGO_DATETIME(new Timestamp(Calendar.getInstance().getTime().getTime()));
				vo.setAPL_REASON(inputVO.getApl_reason());
				if (StringUtils.isNotBlank(inputVO.getApl_reason_oth()))
					vo.setAPL_OTH_REASON(inputVO.getApl_reason_oth());
				vo_log.setTRS_TXN_SOURCE("1");
				vo_log.setRETURN_400_YN("N");
				dam.create(vo_log);
				
				// Step3-1.寫資料到名單匯入主檔TBCAM_SFA_LEADS_IMP
				QueryConditionIF queryCondition_imp = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				StringBuffer sql_imp = new StringBuffer();
				sql_imp.append("SELECT SEQNO FROM TBCAM_SFA_LEADS_IMP WHERE CAMPAIGN_ID = ");
				sql_imp.append("'TRS'||TO_CHAR(current_date, 'YYYYMMDD') || 'FLW' || :trs_flow_type ");
				queryCondition_imp.setObject("trs_flow_type", TRS_FLOW_TYPE);
				queryCondition_imp.setQueryString(sql_imp.toString());
				List<Map<String, Object>> imp_list = dam.exeQuery(queryCondition_imp);
				if (imp_list.size() > 0) {
					//To Step3-2
		        	//Step3-2.寫資料到名單匯入暫存檔(客戶明細) 
		        	QueryConditionIF queryCondition_Imptemp_SEQ = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		    		StringBuffer sql_ImptempSEQ = new StringBuffer();
		    		sql_ImptempSEQ.append("SELECT SQ_TBCAM_SFA_LE_IMP_TEMP.nextval AS SEQNO FROM DUAL");
		    		queryCondition_Imptemp_SEQ.setQueryString(sql_ImptempSEQ.toString());
		    		List<Map<String, Object>> leadsImptempSEQ = dam.exeQuery(queryCondition_Imptemp_SEQ);
		    		BigDecimal seqNo_imptemp = (BigDecimal) leadsImptempSEQ.get(0).get("SEQNO");
		    		
		    		TBCAM_SFA_LE_IMP_TEMPVO vo_imp_temp = new TBCAM_SFA_LE_IMP_TEMPVO();
				    vo_imp_temp.setSEQNO(seqNo_imptemp);
			        vo_imp_temp.setIMP_SEQNO((BigDecimal)imp_list.get(0).get("SEQNO"));
			        vo_imp_temp.setLEAD_ID("SYS" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + addZeroForNum(seqNo_imptemp.toString(), 8));
			        vo_imp_temp.setCUST_ID(ObjectUtils.toString(list.get("CUST_ID")));
			        vo_imp_temp.setCUST_NAME(ObjectUtils.toString(list.get("CUST_NAME")));
			        vo_imp_temp.setBRANCH_ID(inputVO.getNew_branch_nbr());
			        vo_imp_temp.setEMP_ID(query.get(0).get("EMP_ID"));
			        vo_imp_temp.setAO_CODE(inputVO.getNew_ao_code());
			        vo_imp_temp.setSTART_DATE(new Timestamp(Calendar.getInstance().getTime().getTime()));
			        QueryConditionIF queryCondition_FRQ = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					StringBuffer sql_frq = new StringBuffer();
					sql_frq.append("SELECT FRQ_DAY FROM VWCRM_CUST_REVIEWDATE_MAP WHERE CON_DEGREE = ");
					sql_frq.append("(SELECT NVL(CON_DEGREE, 'S') FROM TBCRM_CUST_MAST WHERE CUST_ID = :cust_id) ");
					sql_frq.append("AND VIP_DEGREE = (SELECT NVL(VIP_DEGREE, 'M') FROM TBCRM_CUST_MAST WHERE CUST_ID = :cust_id) ");
					queryCondition_FRQ.setObject("cust_id", list.get("CUST_ID"));
					queryCondition_FRQ.setQueryString(sql_frq.toString());
					List<Map<String, Object>> list_srq = dam.exeQuery(queryCondition_FRQ);
					BigDecimal seq_srq = new BigDecimal(ObjectUtils.toString(list_srq.get(0).get("FRQ_DAY")));
					
					Calendar cal = Calendar.getInstance(); 
		    		cal.add(Calendar.DAY_OF_WEEK, seq_srq.intValue());
					vo_imp_temp.setEND_DATE(new Timestamp(cal.getTime().getTime()));
			        vo_imp_temp.setLEAD_TYPE("03");
			        dam.create(vo_imp_temp);
				} else {
					//Step3-1.寫資料到名單匯入主檔TBCAM_SFA_LEADS_IMP
					QueryConditionIF queryCondition_ImpSEQ = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		    		StringBuffer sql_ImpSEQ = new StringBuffer();
		    		sql_ImpSEQ.append("SELECT SQ_TBCAM_SFA_LEADS_IMP.nextval AS SEQNO FROM DUAL");
		    		queryCondition_ImpSEQ.setQueryString(sql_ImpSEQ.toString());
		    		List<Map<String, Object>> leadsImpSEQ = dam.exeQuery(queryCondition_ImpSEQ);
		    		BigDecimal seqNo_imp = (BigDecimal) leadsImpSEQ.get(0).get("SEQNO");
		        	
		    		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		        	TBCAM_SFA_LEADS_IMPVO vo_imp = new TBCAM_SFA_LEADS_IMPVO();
		        	vo_imp.setSEQNO(seqNo_imp);
		        	vo_imp.setCAMPAIGN_ID("TRS" + sdf.format(new Date()) + "FLW" + TRS_FLOW_TYPE);
		        	if ("1".equals(TRS_FLOW_TYPE)) {
		        		vo_imp.setCAMPAIGN_NAME("新Pool客戶聯繫名單 - 空Code");
		        	}else if ("2".equals(TRS_FLOW_TYPE)) {
		        		vo_imp.setCAMPAIGN_NAME("新Pool客戶聯繫名單 - 有Code 同分行");
		        	}else if ("3".equals(TRS_FLOW_TYPE)) {
		        		vo_imp.setCAMPAIGN_NAME("新Pool客戶聯繫名單 - 有Code FCH跨分行");
		        	}else if ("4".equals(TRS_FLOW_TYPE)) {
		        		vo_imp.setCAMPAIGN_NAME("新Pool客戶聯繫名單 - 有Code跨分行");
		        	}
		        	vo_imp.setCAMPAIGN_DESC("提醒理專聯繫並告知客戶已更換新理專");
		        	vo_imp.setSTEP_ID(HHMMSS.format(new Date()));
		        	vo_imp.setLEAD_SOURCE_ID("04");
		        	Calendar c = Calendar.getInstance();
		        	vo_imp.setSTART_DATE(new Timestamp(c.getTime().getTime()));
		        	c.add(Calendar.DAY_OF_WEEK, 35);
		        	vo_imp.setEND_DATE(new Timestamp(c.getTime().getTime()));
		        	vo_imp.setLEAD_TYPE("03");
		        	vo_imp.setLEAD_PARA1("N");
		        	vo_imp.setLEAD_PARA2("N");
		        	if("002".equals(list.get("PRIVILEGEID")))
		        		vo_imp.setFIRST_CHANNEL("FCALL");
		        	else if("003".equals(list.get("PRIVILEGEID")))
		        		vo_imp.setFIRST_CHANNEL("FCH");
		        	vo_imp.setIMP_STATUS("IN");
		        	dam.create(vo_imp);
		        	//Step3-2.寫資料到名單匯入暫存檔(客戶明細) 
		        	QueryConditionIF queryCondition_Imptemp_SEQ = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		    		StringBuffer sql_ImptempSEQ = new StringBuffer();
		    		sql_ImptempSEQ.append("SELECT SQ_TBCAM_SFA_LE_IMP_TEMP.nextval AS SEQNO FROM DUAL");
		    		queryCondition_Imptemp_SEQ.setQueryString(sql_ImptempSEQ.toString());
		    		List<Map<String, Object>> leadsImptempSEQ = dam.exeQuery(queryCondition_Imptemp_SEQ);
		    		BigDecimal seqNo_imptemp = (BigDecimal) leadsImptempSEQ.get(0).get("SEQNO");

		    		TBCAM_SFA_LE_IMP_TEMPVO vo_imp_temp = new TBCAM_SFA_LE_IMP_TEMPVO();
		    		vo_imp_temp.setSEQNO(seqNo_imptemp);
			        vo_imp_temp.setIMP_SEQNO(seqNo_imp);
			        vo_imp_temp.setLEAD_ID("SYS" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + addZeroForNum(seqNo_imptemp.toString(), 8));
			        vo_imp_temp.setCUST_ID(ObjectUtils.toString(list.get("CUST_ID")));
			        vo_imp_temp.setCUST_NAME(ObjectUtils.toString(list.get("CUST_NAME")));
			        vo_imp_temp.setBRANCH_ID(inputVO.getNew_branch_nbr());
			        vo_imp_temp.setEMP_ID(query.get(0).get("EMP_ID"));
			        vo_imp_temp.setAO_CODE(inputVO.getNew_ao_code());
			        vo_imp_temp.setSTART_DATE(new Timestamp(Calendar.getInstance().getTime().getTime()));
					
			        QueryConditionIF queryCondition_FRQ = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					StringBuffer sql_frq = new StringBuffer();
					sql_frq.append("SELECT FRQ_DAY FROM VWCRM_CUST_REVIEWDATE_MAP WHERE CON_DEGREE = ");
					sql_frq.append("(SELECT NVL(CON_DEGREE, 'S') FROM TBCRM_CUST_MAST WHERE CUST_ID = :cust_id) ");
					sql_frq.append("AND VIP_DEGREE = (SELECT NVL(VIP_DEGREE, 'M') FROM TBCRM_CUST_MAST WHERE CUST_ID = :cust_id) ");
					queryCondition_FRQ.setObject("cust_id", list.get("CUST_ID"));
					queryCondition_FRQ.setQueryString(sql_frq.toString());
					List<Map<String, Object>> list_srq = dam.exeQuery(queryCondition_FRQ);
					BigDecimal seq_srq = new BigDecimal(ObjectUtils.toString(list_srq.get(0).get("FRQ_DAY")));
					Calendar cal = Calendar.getInstance(); 
		    		cal.add(Calendar.DAY_OF_WEEK, seq_srq.intValue());
					vo_imp_temp.setEND_DATE(new Timestamp(cal.getTime().getTime()));
			        vo_imp_temp.setLEAD_TYPE("03");
			        dam.create(vo_imp_temp);
				}
				
				// Step3-3當客戶改AO Code或歸屬行時，若理專沒執行該名單，則要將所有名單換AO Code
		        QueryConditionIF queryCondition_update = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
	    		StringBuffer sql_update = new StringBuffer();
	    		sql_update.append("UPDATE TBCAM_SFA_LEADS SET AO_CODE = :new_ao_code, ");
	    		sql_update.append("EMP_ID = :new_ao_emp_id, ");
	    		sql_update.append("BRANCH_ID = :new_ao_brh ");
	    		sql_update.append("WHERE CUST_ID = :cust_id ");
	    		sql_update.append("AND LEAD_STATUS < '03' ");
	    		sql_update.append("AND fn_is_fc(EMP_ID) = 1 "); //#1357
	    		queryCondition_update.setQueryString(sql_update.toString());
	    		queryCondition_update.setObject("new_ao_code", inputVO.getNew_ao_code());
	    		queryCondition_update.setObject("new_ao_emp_id", query.get(0).get("EMP_ID"));
	    		queryCondition_update.setObject("new_ao_brh", inputVO.getNew_branch_nbr());
	    		queryCondition_update.setObject("cust_id", list.get("CUST_ID"));
	    		dam.exeUpdate(queryCondition_update);
	    		
	    		// 2017/6/30 結束同一客戶在移轉流程的移轉單(專案移轉類別:6重要性:3)
	    		QueryConditionIF queryCondition_close = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
	    		StringBuffer sql_close = new StringBuffer();
	    		sql_close.append("UPDATE TBCRM_TRS_AOCHG_PLIST ");
	    		sql_close.append("SET HQ_MGR = :emp_id, HQ_MGR_RPL_DATETIME = sysdate, HQ_MGR_RPL_STATUS = 'N', PROCESS_STATUS = 'F' ");
	    		sql_close.append("WHERE 1=1 AND CUST_ID = :cust_id ");
	    		sql_close.append("AND PROCESS_STATUS in ( 'L1', 'L2', 'L3', 'L4', 'L5', 'BS') ");
	    		sql_close.append("AND TRS_TYPE in ( SELECT PARAM_CODE FROM TBSYSPARAMETER ");
	    		sql_close.append("WHERE PARAM_TYPE = 'CRM.TRS_TYPE_PRIORITY' ");
	    		sql_close.append("AND PARAM_ORDER >= (SELECT PARAM_ORDER FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'CRM.TRS_TYPE_PRIORITY' AND PARAM_CODE = '6')) ");
	    		queryCondition_close.setQueryString(sql_close.toString());
	    		queryCondition_close.setObject("emp_id", getUserVariable(FubonSystemVariableConsts.LOGINID).toString());
	    		queryCondition_close.setObject("cust_id", list.get("CUST_ID"));
	    		dam.exeUpdate(queryCondition_close);
			}
			
			// 2017/8/30 jacky test
//			count_by_ao_code_change(ao_List);
		}else{
			throw new APException("ehl_01_common_008");
		}
		
		this.sendRtnObject(null);
		
	}
	
	private void cancelOther(String cust_id) throws JBranchException {
		// 2017/12/18 jacky 作廢
		QueryConditionIF queryCondition_close = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql_close = new StringBuffer();
		sql_close.append("UPDATE TBCRM_TRS_AOCHG_PLIST SET MODIFIER = :emp_id, LASTUPDATE = sysdate, PROCESS_STATUS = 'F' ");
		sql_close.append("WHERE CUST_ID = :cust_id ");
		sql_close.append("AND PROCESS_STATUS in ('L1', 'L2', 'L3', 'L4', 'L5') ");
		sql_close.append("AND TRS_TYPE in ('7', '8', '9') ");
		queryCondition_close.setQueryString(sql_close.toString());
		queryCondition_close.setObject("emp_id", getUserVariable(FubonSystemVariableConsts.LOGINID));
		queryCondition_close.setObject("cust_id", cust_id);
		dam.exeUpdate(queryCondition_close);
	}
	
	/**產生seq No */
	private String getPLIST_SEQ() throws JBranchException {
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT TBCRM_TRS_AOCHG_PLIST_SEQ.nextval AS SEQ FROM DUAL ");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> SEQLIST = dam.exeQuery(queryCondition);
		
		return ObjectUtils.toString(SEQLIST.get(0).get("SEQ"));
	}
	
	/**產生seq No */
	private String getCHGLOG_SEQ() throws JBranchException {
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT TBCRM_CUST_AOCODE_CHGLOG_SEQ.nextval AS SEQ FROM DUAL ");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> SEQLIST = dam.exeQuery(queryCondition);
		
		return ObjectUtils.toString(SEQLIST.get(0).get("SEQ"));
	}
	
	public BigDecimal getBigDecimal( Object value ) {
        BigDecimal ret = null;
        if(value != null) {
            if(value instanceof BigDecimal) {
                ret = (BigDecimal) value;
            } else if(value instanceof String) {
                ret = new BigDecimal((String) value);
            } else if(value instanceof BigInteger) {
                ret = new BigDecimal((BigInteger) value);
            } else if(value instanceof Number) {
                ret = new BigDecimal(((Number)value).doubleValue());
            } else {
                throw new ClassCastException("Not possible to coerce ["+value+"] from class "+value.getClass()+" into a BigDecimal.");
            }
        }
        return ret;
    }
	
	public String addZeroForNum(String str, int strLength) {
	    int strLen = str.length();
	    if (strLen < strLength) {
	        while (strLen < strLength) {
	            StringBuffer sb = new StringBuffer();
	            sb.append("0").append(str);// 左補0
	            // sb.append(str).append("0");//右補0
	            str = sb.toString();
	            strLen = str.length();
	        }
	    }
	    return str;
	}
	
	// 2017/8/30 jacky want
	public void count_by_ao_code_change(Set<String> ao_list) throws Exception {
		dam = this.getDataAccessManager();
		
		if(ao_list.size() > 0) {
			// 更新調整統計表主用TABLE
			// VWCRM_AO_CUST_AUM_BY_DEGREE
			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sql = new StringBuffer();
			sql.append("UPDATE (SELECT O.AO_CODE, O.AUM_CUST_V, O.AUM_CUST_A, O.AUM_CUST_B, O.AUM_CUST_M, ");
			sql.append("N.NEW_AUM_CUST_V, N.NEW_AUM_CUST_A, N.NEW_AUM_CUST_B, N.NEW_AUM_CUST_M ");
			sql.append("FROM MVCRM_AO_CUST_AUM_BY_DEGREE O, ");
			sql.append("(SELECT \"AO_CODE\" as \"NEW_AO_CODE\", ");
			sql.append("\"AUM_CUST_V\" as \"NEW_AUM_CUST_V\", \"AUM_CUST_A\" as \"NEW_AUM_CUST_A\", ");
			sql.append("\"AUM_CUST_B\" as \"NEW_AUM_CUST_B\", \"AUM_CUST_M\" as \"NEW_AUM_CUST_M\" ");
			sql.append("FROM ( ");
			sql.append("SELECT C.AO_CODE, NVL(C.VIP_DEGREE,'M') as VIP_DEGREE, SUM(NVL(V.AVG_AUM_AMT,0)) as AUM_CUST ");
			sql.append("FROM MVCRM_AST_AMT C, TBCRM_CUST_LDAY_AUM_MONTHLY V ");
			sql.append("WHERE 1=1 ");
			sql.append("AND C.CUST_ID = V.CUST_ID ");
			sql.append("AND C.AO_CODE in (:ao_list) ");
			sql.append("GROUP BY C.AO_CODE, NVL(C.VIP_DEGREE,'M') ");
			sql.append(") ");
			sql.append("PIVOT ( SUM(AUM_CUST) FOR VIP_DEGREE in ( 'V' as AUM_CUST_V, 'A' as AUM_CUST_A, 'B' as AUM_CUST_B, 'M' as AUM_CUST_M) ) ");
			sql.append(") N ");
			sql.append("WHERE O.AO_CODE = N.NEW_AO_CODE ");
			sql.append(") MV ");
			sql.append("SET MV.AUM_CUST_V = MV.NEW_AUM_CUST_V, ");
			sql.append("MV.AUM_CUST_A = MV.NEW_AUM_CUST_A, ");
			sql.append("MV.AUM_CUST_B = MV.NEW_AUM_CUST_B, ");
			sql.append("MV.AUM_CUST_M = MV.NEW_AUM_CUST_M ");
			sql.append("WHERE AO_CODE in (:ao_list) ");
			queryCondition.setObject("ao_list", ao_list);
			queryCondition.setQueryString(sql.toString());
			dam.exeUpdate(queryCondition);
			
			// VWCRM_AO_CUST_NC_NO_BY_DEGREE
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql = new StringBuffer();
			sql.append("UPDATE (SELECT O.AO_CODE, O.CNT_NC_CUST_V, O.CNT_NC_CUST_A, O.CNT_NC_CUST_B, O.CNT_NC_CUST_M, ");
			sql.append("N.NEW_CNT_NC_CUST_V, N.NEW_CNT_NC_CUST_A, N.NEW_CNT_NC_CUST_B, N.NEW_CNT_NC_CUST_M ");
			sql.append("FROM MVCRM_AO_CUST_NC_NO_BY_DEGREE O, ");
			sql.append("(SELECT \"AO_CODE\" as \"NEW_AO_CODE\", ");
			sql.append("\"CNT_NC_CUST_V\" as \"NEW_CNT_NC_CUST_V\", \"CNT_NC_CUST_A\" as \"NEW_CNT_NC_CUST_A\", ");
			sql.append("\"CNT_NC_CUST_B\" as \"NEW_CNT_NC_CUST_B\", \"CNT_NC_CUST_M\" as \"NEW_CNT_NC_CUST_M\" ");
			sql.append("FROM ( ");
			sql.append("SELECT C.AO_CODE, NVL(C.VIP_DEGREE,'M') as VIP_DEGREE, COUNT(NVL(C.VIP_DEGREE,'M')) as CNT_NC_CUST ");
			sql.append("FROM MVCRM_AST_AMT C ");
			sql.append("WHERE 1=1 AND (C.C0_YN = 'Y' OR  ");
			sql.append("(extract(year from sysdate) - extract(year from NVL(C.BIRTH_DATE,SYSDATE)))>=80 ");
			sql.append(") ");
			sql.append("AND C.AO_CODE in (:ao_list) ");
			sql.append("GROUP BY C.AO_CODE, NVL(C.VIP_DEGREE,'M') ");
			sql.append(") ");
			sql.append("PIVOT ( SUM(CNT_NC_CUST) FOR VIP_DEGREE in ( 'V' as CNT_NC_CUST_V, 'A' as CNT_NC_CUST_A, 'B' as CNT_NC_CUST_B, 'M' as CNT_NC_CUST_M) ) ");
			sql.append(") N ");
			sql.append("WHERE O.AO_CODE = N.NEW_AO_CODE ");
			sql.append(") MV ");
			sql.append("SET MV.CNT_NC_CUST_V = MV.NEW_CNT_NC_CUST_V, ");
			sql.append("MV.CNT_NC_CUST_A = MV.NEW_CNT_NC_CUST_A, ");
			sql.append("MV.CNT_NC_CUST_B = MV.NEW_CNT_NC_CUST_B, ");
			sql.append("MV.CNT_NC_CUST_M = MV.NEW_CNT_NC_CUST_M ");
			sql.append("WHERE AO_CODE in (:ao_list) ");
			queryCondition.setObject("ao_list", ao_list);
			queryCondition.setQueryString(sql.toString());
			dam.exeUpdate(queryCondition);
			
			// VWCRM_AO_CUST_NO_BY_DEGREE
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql = new StringBuffer();
			sql.append("UPDATE (SELECT O.AO_CODE, O.CNT_CUST_V, O.CNT_CUST_A, O.CNT_CUST_B, O.CNT_CUST_M, ");
			sql.append("N.NEW_CNT_CUST_V, N.NEW_CNT_CUST_A, N.NEW_CNT_CUST_B, N.NEW_CNT_CUST_M ");
			sql.append("FROM MVCRM_AO_CUST_NO_BY_DEGREE O, ");
			sql.append("(SELECT \"AO_CODE\" as \"NEW_AO_CODE\", ");
			sql.append("\"CNT_CUST_V\" as \"NEW_CNT_CUST_V\", \"CNT_CUST_A\" as \"NEW_CNT_CUST_A\", ");
			sql.append("\"CNT_CUST_B\" as \"NEW_CNT_CUST_B\", \"CNT_CUST_M\" as \"NEW_CNT_CUST_M\"  ");
			sql.append("FROM ( ");
			sql.append("SELECT C.AO_CODE, NVL(C.VIP_DEGREE,'M') as VIP_DEGREE, COUNT(NVL(C.VIP_DEGREE,'M')) as CNT_CUST ");
			sql.append("FROM MVCRM_AST_AMT C ");
			sql.append("WHERE 1=1 ");
			sql.append("AND C.AO_CODE in (:ao_list) ");
			sql.append("GROUP BY C.AO_CODE, NVL(C.VIP_DEGREE,'M') ");
			sql.append(") ");
			sql.append("PIVOT ( SUM(CNT_CUST) FOR VIP_DEGREE in ( 'V' as CNT_CUST_V, 'A' as CNT_CUST_A, 'B' as CNT_CUST_B, 'M' as CNT_CUST_M) ) ");
			sql.append(") N ");
			sql.append("WHERE O.AO_CODE = N.NEW_AO_CODE ");
			sql.append(") MV ");
			sql.append("SET MV.CNT_CUST_V = MV.NEW_CNT_CUST_V, ");
			sql.append("MV.CNT_CUST_A = MV.NEW_CNT_CUST_A, ");
			sql.append("MV.CNT_CUST_B = MV.NEW_CNT_CUST_B, ");
			sql.append("MV.CNT_CUST_M = MV.NEW_CNT_CUST_M ");
			sql.append("WHERE AO_CODE in (:ao_list) ");
			queryCondition.setObject("ao_list", ao_list);
			queryCondition.setQueryString(sql.toString());
			dam.exeUpdate(queryCondition);
		}
	}
	
	//2023必輪調名單：必輪調RM名單上傳後，名單中RM不可做移入申請
	private String check2023CMDTCust1(String newAoCode) throws DAOException, JBranchException {
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		sql.append("SELECT 1 ");
		sql.append(" FROM TBCRM_TRS_PRJ_ROTATION_M A ");
		sql.append(" INNER JOIN TBCRM_TRS_PRJ_MAST B ON B.PRJ_ID = A.PRJ_ID "); 
		sql.append(" WHERE A.EMP_ID = (SELECT EMP_ID FROM TBORG_SALES_AOCODE WHERE AO_CODE = :aoCode) ");
		sql.append(" AND A.IMP_SUCCESS_YN = 'Y' AND B.PRJ_TYPE = '1' "); //必輪調有匯入成功的理專
		sql.append("   AND TRUNC(SYSDATE) < TRUNC(B.PRJ_EXE_DATE) "); //名單尚未執行
		
		queryCondition.setObject("aoCode", newAoCode);
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);

		return CollectionUtils.isNotEmpty(list) ? "Y" : "N"; //Y:必輪調名單中RM，不可做移入申請
	}
}