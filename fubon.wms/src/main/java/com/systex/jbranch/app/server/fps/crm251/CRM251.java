package com.systex.jbranch.app.server.fps.crm251;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBCRM_CUST_AO_DEF_GROUPVO;
import com.systex.jbranch.app.common.fps.table.TBCRM_CUST_GROUPPK;
import com.systex.jbranch.app.common.fps.table.TBCRM_CUST_GROUPVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.dataaccess.serialnumber.SerialNumberUtil;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * MENU
 * 
 * @author moron
 * @date 2016/04/20
 * @spec null
 */
@Component("crm251")
@Scope("request")
public class CRM251 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(CRM251.class);

	public void getGroups(Object body, IPrimitiveMap header) throws JBranchException {
		CRM251OutputVO return_VO = new CRM251OutputVO();
		dam = this.getDataAccessManager();
		// 依系統角色決定下拉選單可視範圍
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> headmgrMap = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2); //總行
		Map<String, String> armgrMap = xmlInfo.doGetVariable("FUBONSYS.ARMGR_ROLE", FormatHelper.FORMAT_2); 	//業務處
		Map<String, String> mbrmgrMap = xmlInfo.doGetVariable("FUBONSYS.MBRMGR_ROLE", FormatHelper.FORMAT_2); 	//營運區
		Map<String, String> uhrmmgrMap = xmlInfo.doGetVariable("FUBONSYS.UHRMMGR_ROLE", FormatHelper.FORMAT_2);	//UHRM科/處主管
		Map<String, String> uhrmMap = xmlInfo.doGetVariable("FUBONSYS.UHRM_ROLE", FormatHelper.FORMAT_2);		//UHRM

		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT GROUP_ID, GROUP_NAME ");
		sql.append("FROM TBCRM_CUST_AO_DEF_GROUP A ");
		sql.append("WHERE 1 = 1 ");
		if (uhrmmgrMap.containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			sql.append("AND EXISTS (SELECT T.AO_CODE ");
			sql.append("            FROM TBORG_SALES_AOCODE T ");
			sql.append("            WHERE 1 = 1");
			sql.append("            AND T.TYPE = '5' ");
			sql.append("            ) ");
		} else if (uhrmMap.containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			sql.append("AND EXISTS (SELECT T.AO_CODE FROM TBORG_SALES_AOCODE T WHERE T.EMP_ID = :loginID AND T.TYPE = '5' AND A.AO_CODE = T.AO_CODE) ");
			queryCondition.setObject("loginID", getUserVariable(FubonSystemVariableConsts.LOGINID));
		} else {
			if (headmgrMap.containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
				sql.append("and AO_CODE IN (SELECT AO_CODE FROM VWORG_AO_INFO WHERE 1 = 1 ");
				sql.append("AND (CENTER_ID IN (:rcIdList) OR CENTER_ID IS NULL) ");
				sql.append("AND (AREA_ID IN (:opIdList) OR AREA_ID IS NULL) ");
				sql.append("AND (BRA_NBR IN (:brNbrList) OR BRA_NBR IS NULL)) ");
				queryCondition.setObject("rcIdList", getUserVariable(FubonSystemVariableConsts.AVAILREGIONLIST));
				queryCondition.setObject("opIdList", getUserVariable(FubonSystemVariableConsts.AVAILAREALIST));
				queryCondition.setObject("brNbrList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
			} else if (armgrMap.containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
				sql.append("and AO_CODE IN (SELECT AO_CODE FROM VWORG_AO_INFO WHERE 1=1 ");
				sql.append("AND (CENTER_ID IN (:rcIdList)) ");
				sql.append("AND (AREA_ID IN (:opIdList) OR AREA_ID IS NULL) ");
				sql.append("AND (BRA_NBR IN (:brNbrList) OR BRA_NBR IS NULL)) ");
				queryCondition.setObject("rcIdList", getUserVariable(FubonSystemVariableConsts.AVAILREGIONLIST));
				queryCondition.setObject("opIdList", getUserVariable(FubonSystemVariableConsts.AVAILAREALIST));
				queryCondition.setObject("brNbrList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
			} else if (mbrmgrMap.containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
				sql.append("and AO_CODE IN (SELECT AO_CODE FROM VWORG_AO_INFO WHERE 1=1 ");
				sql.append("AND (CENTER_ID IN (:rcIdList)) ");
				sql.append("AND (AREA_ID IN (:opIdList)) ");
				sql.append("AND (BRA_NBR IN (:brNbrList) OR BRA_NBR IS NULL)) ");
				queryCondition.setObject("rcIdList", getUserVariable(FubonSystemVariableConsts.AVAILREGIONLIST));
				queryCondition.setObject("opIdList", getUserVariable(FubonSystemVariableConsts.AVAILAREALIST));
				queryCondition.setObject("brNbrList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
			} else {
				List curr_ao_list = (List) getUserVariable(FubonSystemVariableConsts.LOGIN_AOCODE_LIST);
				if (curr_ao_list.size() > 0) {
					sql.append("and AO_CODE IN (:login_ao) ");
					sql.append("AND EXISTS (SELECT T.AO_CODE FROM TBORG_SALES_AOCODE T WHERE T.TYPE <= 4 AND T.AO_CODE = A.AO_CODE)");
					queryCondition.setObject("login_ao", curr_ao_list);
				} else {
					sql.append("and AO_CODE IN (SELECT AO_CODE FROM VWORG_AO_INFO WHERE 1=1 ");
					sql.append("AND (CENTER_ID IN (:rcIdList)) ");
					sql.append("AND (AREA_ID IN (:opIdList)) ");
					sql.append("AND (BRA_NBR IN (:brNbrList))) ");
					queryCondition.setObject("rcIdList", getUserVariable(FubonSystemVariableConsts.AVAILREGIONLIST));
					queryCondition.setObject("opIdList", getUserVariable(FubonSystemVariableConsts.AVAILAREALIST));
					queryCondition.setObject("brNbrList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
				}
			}
		}
		sql.append("ORDER BY GROUP_ID ");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		return_VO.setResultList(list);
		this.sendRtnObject(return_VO);
	}

	//此方法無法抓到"理專副CODE"，故改使用CRM211.getAOCode
	public void getAOCodes(Object body, IPrimitiveMap header) throws JBranchException {
		//		CRM251OutputVO return_VO = new CRM251OutputVO();
		//		dam = this.getDataAccessManager();
		//		
		//		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		//		StringBuffer sql = new StringBuffer();
		//		sql.append("SELECT AO_CODE, EMP_NAME FROM VWORG_BRANCH_EMP_DETAIL_INFO WHERE AO_CODE IS NOT NULL AND BRANCH_NBR IN (:brNbrList) ORDER BY AO_CODE ");
		//		queryCondition.setObject("brNbrList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
		//		queryCondition.setQueryString(sql.toString());
		//		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		//		return_VO.setResultList(list);
		//		this.sendRtnObject(return_VO);
	}

	public void inquire(Object body, IPrimitiveMap header) throws JBranchException {
		CRM251InputVO inputVO = (CRM251InputVO) body;
		CRM251OutputVO return_VO = new CRM251OutputVO();
		dam = this.getDataAccessManager();

		// 依系統角色決定下拉選單可視範圍
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> headmgrMap = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2); //總行
		Map<String, String> armgrMap = xmlInfo.doGetVariable("FUBONSYS.ARMGR_ROLE", FormatHelper.FORMAT_2); 	//業務處
		Map<String, String> mbrmgrMap = xmlInfo.doGetVariable("FUBONSYS.MBRMGR_ROLE", FormatHelper.FORMAT_2); 	//營運區
		Map<String, String> uhrmmgrMap = xmlInfo.doGetVariable("FUBONSYS.UHRMMGR_ROLE", FormatHelper.FORMAT_2);	//UHRM科/處主管
		Map<String, String> uhrmMap = xmlInfo.doGetVariable("FUBONSYS.UHRM_ROLE", FormatHelper.FORMAT_2);		//UHRM

		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT a.AO_CODE, a.GROUP_ID, a.GROUP_NAME, a.LASTUPDATE, b.EMP_NAME, ao.EMP_ID AS EMP_ID ");
		sql.append("FROM TBCRM_CUST_AO_DEF_GROUP a ");
		sql.append("LEFT JOIN TBORG_MEMBER b ON a.MODIFIER = b.EMP_ID ");
		sql.append("LEFT JOIN TBORG_SALES_AOCODE ao ON ao.AO_CODE = a.AO_CODE ");
//		sql.append("LEFT JOIN TBORG_MEMBER ao_b ON ao.EMP_ID = ao_b.EMP_ID ");
		sql.append("WHERE 1 = 1 ");
		// where
		if (StringUtils.isNotBlank(inputVO.getGroup_id())) {
			sql.append("and a.GROUP_ID = :id ");
			queryCondition.setObject("id", inputVO.getGroup_id());
		}
		
		if (uhrmmgrMap.containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			sql.append("AND EXISTS (SELECT T.AO_CODE ");
			sql.append("            FROM TBORG_SALES_AOCODE T ");
			sql.append("            WHERE 1 = 1");
			sql.append("            AND T.TYPE = '5' ");
			sql.append("            AND A.AO_CODE = T.AO_CODE ");
			if (StringUtils.isNotBlank(inputVO.getuEmpID())) {
				sql.append("        AND T.EMP_ID = :uEmpID ");
				queryCondition.setObject("uEmpID", inputVO.getuEmpID());
			}
			sql.append("            ) ");
		} else if (uhrmMap.containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			sql.append("AND EXISTS (SELECT T.AO_CODE FROM TBORG_SALES_AOCODE T WHERE T.EMP_ID = :loginID AND T.TYPE = '5' AND A.AO_CODE = T.AO_CODE) ");
			queryCondition.setObject("loginID", getUserVariable(FubonSystemVariableConsts.LOGINID));
		} else {
			if (StringUtils.isNotBlank(inputVO.getAo_code())) {
				sql.append("and a.AO_CODE = :code ");
				queryCondition.setObject("code", inputVO.getAo_code());
			} else {
				if (headmgrMap.containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
					sql.append("and a.AO_CODE IN (SELECT AO_CODE FROM VWORG_AO_INFO WHERE 1=1 ");
					sql.append("AND (CENTER_ID IN (:rcIdList) OR CENTER_ID IS NULL) ");
					sql.append("AND (AREA_ID IN (:opIdList) OR AREA_ID IS NULL) ");
					sql.append("AND (BRA_NBR IN (:brNbrList) OR BRA_NBR IS NULL)) ");
					queryCondition.setObject("rcIdList", getUserVariable(FubonSystemVariableConsts.AVAILREGIONLIST));
					queryCondition.setObject("opIdList", getUserVariable(FubonSystemVariableConsts.AVAILAREALIST));
					queryCondition.setObject("brNbrList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
				} else if (armgrMap.containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
					sql.append("and a.AO_CODE IN (SELECT AO_CODE FROM VWORG_AO_INFO WHERE 1=1 ");
					sql.append("AND (CENTER_ID IN (:rcIdList)) ");
					sql.append("AND (AREA_ID IN (:opIdList) OR AREA_ID IS NULL) ");
					sql.append("AND (BRA_NBR IN (:brNbrList) OR BRA_NBR IS NULL)) ");
					queryCondition.setObject("rcIdList", getUserVariable(FubonSystemVariableConsts.AVAILREGIONLIST));
					queryCondition.setObject("opIdList", getUserVariable(FubonSystemVariableConsts.AVAILAREALIST));
					queryCondition.setObject("brNbrList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
				} else if (mbrmgrMap.containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
					sql.append("and a.AO_CODE IN (SELECT AO_CODE FROM VWORG_AO_INFO WHERE 1=1 ");
					sql.append("AND (CENTER_ID IN (:rcIdList)) ");
					sql.append("AND (AREA_ID IN (:opIdList)) ");
					sql.append("AND (BRA_NBR IN (:brNbrList) OR BRA_NBR IS NULL)) ");
					queryCondition.setObject("rcIdList", getUserVariable(FubonSystemVariableConsts.AVAILREGIONLIST));
					queryCondition.setObject("opIdList", getUserVariable(FubonSystemVariableConsts.AVAILAREALIST));
					queryCondition.setObject("brNbrList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
				} else {
					List curr_ao_list = (List) getUserVariable(FubonSystemVariableConsts.LOGIN_AOCODE_LIST);
					if (curr_ao_list.size() > 0) {
						sql.append("and a.AO_CODE IN (:login_ao) ");
						queryCondition.setObject("login_ao", curr_ao_list);
					} else {
						sql.append("and a.AO_CODE IN (SELECT AO_CODE FROM VWORG_AO_INFO WHERE 1=1 ");
						sql.append("AND (CENTER_ID IN (:rcIdList)) ");
						sql.append("AND (AREA_ID IN (:opIdList)) ");
						sql.append("AND (BRA_NBR IN (:brNbrList))) ");
						queryCondition.setObject("rcIdList", getUserVariable(FubonSystemVariableConsts.AVAILREGIONLIST));
						queryCondition.setObject("opIdList", getUserVariable(FubonSystemVariableConsts.AVAILAREALIST));
						queryCondition.setObject("brNbrList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
					}
				}
			}
		}
		
		queryCondition.setQueryString(sql.toString());
		ResultIF list = dam.executePaging(queryCondition, inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
		int totalPage_i = list.getTotalPage();
		int totalRecord_i = list.getTotalRecord();
		return_VO.setResultList(list);
		return_VO.setCurrentPageIndex(inputVO.getCurrentPageIndex());// 當前頁次
		return_VO.setTotalPage(totalPage_i);// 總頁次
		return_VO.setTotalRecord(totalRecord_i);// 總筆數
		this.sendRtnObject(return_VO);
	}

	public void addGroup(Object body, IPrimitiveMap header) throws JBranchException {
		
		CRM251InputVO inputVO = (CRM251InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		String uEmpCode = "";
		
		if (StringUtils.isNotBlank(inputVO.getuEmpID())) {
			sql = new StringBuffer();
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			
			sql.append("SELECT AO_CODE ");
			sql.append("FROM TBORG_SALES_AOCODE ");
			sql.append("WHERE TYPE = '5' ");
			sql.append("AND EMP_ID = :uEmpID ");
			queryCondition.setObject("uEmpID", inputVO.getuEmpID());
			
			queryCondition.setQueryString(sql.toString());
			
			List<Map<String, Object>> uEmpCodeList = dam.exeQuery(queryCondition);
			
			if (uEmpCodeList.size() > 0) {
				uEmpCode = (String) uEmpCodeList.get(0).get("AO_CODE");
			}
		}
		
		sql = new StringBuffer();
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		// check 2017/4/11
		sql.append("SELECT GROUP_ID ");
		sql.append("FROM TBCRM_CUST_AO_DEF_GROUP ");
		sql.append("WHERE GROUP_NAME = :gro_name ");
		if (StringUtils.isNotBlank(inputVO.getuEmpID()) && StringUtils.isNotBlank(uEmpCode)) {
			sql.append("AND AO_CODE = :uEmpCode ");
			queryCondition.setObject("uEmpCode", uEmpCode);
		} else if (StringUtils.isNotBlank(inputVO.getAo_code())) {
			sql.append("AND AO_CODE = :ao_code ");
			queryCondition.setObject("ao_code", inputVO.getAo_code());
		}
		
		queryCondition.setObject("gro_name", inputVO.getGroup_name());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		if (list.size() > 0)
			throw new APException("ehl_01_CRM251_001");

		// get id
		String pk = getSN();
		while (checkID(pk)) {
			pk = getSN();
		}

		TBCRM_CUST_AO_DEF_GROUPVO vo = new TBCRM_CUST_AO_DEF_GROUPVO();
		vo.setGROUP_ID(pk);
		vo.setGROUP_NAME(inputVO.getGroup_name());
		if (StringUtils.isNotBlank(inputVO.getuEmpID()) && StringUtils.isNotBlank(uEmpCode)) {
			vo.setAO_CODE(uEmpCode);
		} else if (StringUtils.isNotBlank(inputVO.getAo_code())) {
			vo.setAO_CODE(inputVO.getAo_code());
		}
		
		dam.create(vo);
		this.sendRtnObject(null);
	}

	public void updateGroup(Object body, IPrimitiveMap header) throws JBranchException {
		CRM251InputVO inputVO = (CRM251InputVO) body;
		CRM251OutputVO return_VO = new CRM251OutputVO();
		dam = this.getDataAccessManager();

		TBCRM_CUST_AO_DEF_GROUPVO vo = new TBCRM_CUST_AO_DEF_GROUPVO();
		vo = (TBCRM_CUST_AO_DEF_GROUPVO) dam.findByPKey(TBCRM_CUST_AO_DEF_GROUPVO.TABLE_UID, inputVO.getGroup_id());
		if (vo != null) {
			vo.setGROUP_NAME(inputVO.getGroup_name());
			vo.setAO_CODE(inputVO.getAo_code());
			dam.update(vo);
		} else {
			// 顯示資料不存在
			throw new APException("ehl_01_common_001");
		}
		this.sendRtnObject(null);
	}

	public void deleteGroup(Object body, IPrimitiveMap header) throws JBranchException {
		CRM251InputVO inputVO = (CRM251InputVO) body;
		CRM251OutputVO return_VO = new CRM251OutputVO();
		dam = this.getDataAccessManager();

		// TBCRM_CUST_AO_DEF_GROUP
		TBCRM_CUST_AO_DEF_GROUPVO vo = new TBCRM_CUST_AO_DEF_GROUPVO();
		vo = (TBCRM_CUST_AO_DEF_GROUPVO) dam.findByPKey(TBCRM_CUST_AO_DEF_GROUPVO.TABLE_UID, inputVO.getGroup_id());
		if (vo != null) {
			dam.delete(vo);
		} else {
			// 顯示資料不存在
			throw new APException("ehl_01_common_001");
		}

		// TBCRM_CUST_GROUP
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		condition.setQueryString("delete from TBCRM_CUST_GROUP where GROUP_ID = :group");
		condition.setObject("group", inputVO.getGroup_id());
		dam.exeUpdate(condition);

		this.sendRtnObject(null);
	}

	public void getDetail(Object body, IPrimitiveMap header) throws JBranchException {
		CRM251InputVO inputVO = (CRM251InputVO) body;
		CRM251OutputVO return_VO = new CRM251OutputVO();
		dam = this.getDataAccessManager();

		// 依系統角色決定下拉選單可視範圍
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> headmgrMap = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2); //總行
		Map<String, String> armgrMap = xmlInfo.doGetVariable("FUBONSYS.ARMGR_ROLE", FormatHelper.FORMAT_2); 	//業務處
		Map<String, String> mbrmgrMap = xmlInfo.doGetVariable("FUBONSYS.MBRMGR_ROLE", FormatHelper.FORMAT_2); 	//營運區
		Map<String, String> uhrmmgrMap = xmlInfo.doGetVariable("FUBONSYS.UHRMMGR_ROLE", FormatHelper.FORMAT_2);	//UHRM科/處主管
		Map<String, String> uhrmMap = xmlInfo.doGetVariable("FUBONSYS.UHRM_ROLE", FormatHelper.FORMAT_2);		//UHRM

		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT C.GROUP_ID, A.CUST_ID, A.CUST_NAME, CASE WHEN UP.CODE_TYPE = '1' THEN '計績' WHEN UP.CODE_TYPE = '3' THEN '維護' ELSE NULL END AS UEMP_AO_TYPE, ");
		sql.append("       A.VIP_DEGREE, A.CON_DEGREE, A.GENDER, A.BIRTH_DATE, B.MOBILE_NO, B.EMAIL, A.AUM_AMT ");
		sql.append("from TBCRM_CUST_MAST A ");
		sql.append("left join TBCRM_CUST_CONTACT B on A.CUST_ID = B.CUST_ID ");
		sql.append("left join TBCRM_CUST_GROUP C on A.CUST_ID = C.CUST_ID ");
		sql.append("LEFT JOIN VWORG_EMP_UHRM_INFO UP ON A.AO_CODE = UP.UHRM_CODE ");
		sql.append("where C.GROUP_ID = :id ");
		
		if (uhrmmgrMap.containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			//TODO
//			sql.append("AND EXISTS (SELECT T.AO_CODE ");
//			sql.append("            FROM TBORG_SALES_AOCODE T ");
//			sql.append("            WHERE 1 = 1");
//			sql.append("            AND T.TYPE = '5' ");
//			sql.append("            AND A.AO_CODE = T.AO_CODE ");
//			if (StringUtils.isNotBlank(inputVO.getuEmpID())) {
//				sql.append("        AND T.EMP_ID = :uEmpID ");
//				queryCondition.setObject("uEmpID", inputVO.getuEmpID());
//			}
//			sql.append("            ) ");
		} else if (uhrmMap.containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			sql.append("AND A.UEMP_ID = :loginID ");
			queryCondition.setObject("loginID", getUserVariable(FubonSystemVariableConsts.LOGINID));
		} else {
			if (headmgrMap.containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
				sql.append("and A.AO_CODE IN (SELECT AO_CODE FROM VWORG_AO_INFO WHERE 1=1 ");
				sql.append("AND (CENTER_ID IN (:rcIdList) OR CENTER_ID IS NULL) ");
				sql.append("AND (AREA_ID IN (:opIdList) OR AREA_ID IS NULL) ");
				sql.append("AND (BRA_NBR IN (:brNbrList) OR BRA_NBR IS NULL)) ");
				queryCondition.setObject("rcIdList", getUserVariable(FubonSystemVariableConsts.AVAILREGIONLIST));
				queryCondition.setObject("opIdList", getUserVariable(FubonSystemVariableConsts.AVAILAREALIST));
				queryCondition.setObject("brNbrList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
			} else if (armgrMap.containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
				sql.append("and A.AO_CODE IN (SELECT AO_CODE FROM VWORG_AO_INFO WHERE 1=1 ");
				sql.append("AND (CENTER_ID IN (:rcIdList)) ");
				sql.append("AND (AREA_ID IN (:opIdList) OR AREA_ID IS NULL) ");
				sql.append("AND (BRA_NBR IN (:brNbrList) OR BRA_NBR IS NULL)) ");
				queryCondition.setObject("rcIdList", getUserVariable(FubonSystemVariableConsts.AVAILREGIONLIST));
				queryCondition.setObject("opIdList", getUserVariable(FubonSystemVariableConsts.AVAILAREALIST));
				queryCondition.setObject("brNbrList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
			} else if (mbrmgrMap.containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
				sql.append("and A.AO_CODE IN (SELECT AO_CODE FROM VWORG_AO_INFO WHERE 1=1 ");
				sql.append("AND (CENTER_ID IN (:rcIdList)) ");
				sql.append("AND (AREA_ID IN (:opIdList)) ");
				sql.append("AND (BRA_NBR IN (:brNbrList) OR BRA_NBR IS NULL)) ");
				queryCondition.setObject("rcIdList", getUserVariable(FubonSystemVariableConsts.AVAILREGIONLIST));
				queryCondition.setObject("opIdList", getUserVariable(FubonSystemVariableConsts.AVAILAREALIST));
				queryCondition.setObject("brNbrList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
			} else {
				List curr_ao_list = (List) getUserVariable(FubonSystemVariableConsts.LOGIN_AOCODE_LIST);
				if (curr_ao_list.size() > 0) {
					sql.append("and a.AO_CODE IN (:login_ao) ");
					queryCondition.setObject("login_ao", curr_ao_list);
				} else {
					sql.append("and a.AO_CODE IN (SELECT AO_CODE FROM VWORG_AO_INFO WHERE 1=1 ");
					sql.append("AND (CENTER_ID IN (:rcIdList)) ");
					sql.append("AND (AREA_ID IN (:opIdList)) ");
					sql.append("AND (BRA_NBR IN (:brNbrList))) ");
					queryCondition.setObject("rcIdList", getUserVariable(FubonSystemVariableConsts.AVAILREGIONLIST));
					queryCondition.setObject("opIdList", getUserVariable(FubonSystemVariableConsts.AVAILAREALIST));
					queryCondition.setObject("brNbrList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
				}
			}
		}
		System.out.println(sql.toString());

		System.out.println(sql.toString());
		queryCondition.setObject("id", inputVO.getGroup_id());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		return_VO.setResultList(list);
		this.sendRtnObject(return_VO);
	}

	public void deleteCustGroup(Object body, IPrimitiveMap header) throws JBranchException {
		CRM251InputVO inputVO = (CRM251InputVO) body;
		CRM251OutputVO return_VO = new CRM251OutputVO();
		dam = this.getDataAccessManager();

		for (Map<String, Object> map : inputVO.getChkId()) {
			TBCRM_CUST_GROUPVO vo = new TBCRM_CUST_GROUPVO();
			TBCRM_CUST_GROUPPK pk = new TBCRM_CUST_GROUPPK();
			pk.setCUST_ID(map.get("CUST_ID").toString());
			pk.setGROUP_ID(map.get("GROUP_ID").toString());
			vo = (TBCRM_CUST_GROUPVO) dam.findByPKey(TBCRM_CUST_GROUPVO.TABLE_UID, pk);
			if (vo != null) {
				dam.delete(vo);
			} else {
				// 顯示資料不存在
				throw new APException("ehl_01_common_001");
			}
		}
		this.sendRtnObject(null);
	}

	private String getSN() throws JBranchException {
		SerialNumberUtil sn = new SerialNumberUtil();
		String seqNum = "";
		try {
			seqNum = sn.getNextSerialNumber("CRM251");
		} catch (Exception e) {
			sn.createNewSerial("CRM251", "0000000000", null, null, null, 1, new Long("9999999999"), "y", new Long("0"), null);
			seqNum = sn.getNextSerialNumber("CRM251");
		}
		return seqNum;
	}

	private Boolean checkID(String pk) throws JBranchException {
		Boolean ans = false;

		TBCRM_CUST_AO_DEF_GROUPVO vo = new TBCRM_CUST_AO_DEF_GROUPVO();
		vo = (TBCRM_CUST_AO_DEF_GROUPVO) dam.findByPKey(TBCRM_CUST_AO_DEF_GROUPVO.TABLE_UID, pk);
		if (vo != null)
			ans = true;
		else
			ans = false;
		return ans;
	}

	public void checkMail(Object body, IPrimitiveMap header) throws JBranchException {
		CRM251InputVO inputVO = (CRM251InputVO) body;
		CRM251OutputVO return_VO = new CRM251OutputVO();
		dam = this.getDataAccessManager();

		List<String> ans = new ArrayList<String>();
		for (Map<String, Object> map : inputVO.getChkId()) {
			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sql = new StringBuffer();
			sql.append("SELECT EMAIL FROM TBCRM_CUST_CONTACT WHERE CUST_ID = :cust_id");
			queryCondition.setObject("cust_id", map.get("CUST_ID"));
			queryCondition.setQueryString(sql.toString());
			List<Map<String, Object>> list = dam.exeQuery(queryCondition);
			if (list.size() > 0) {
				if (StringUtils.isBlank(ObjectUtils.toString(list.get(0).get("EMAIL"))))
					throw new APException("客戶:" + map.get("CUST_ID") + "，信箱為空");
				else if (StringUtils.isBlank(ObjectUtils.toString(list.get(0).get("EMAIL")).trim()))
					throw new APException("客戶:" + map.get("CUST_ID") + "，信箱為空白字串");
				else
					ans.add(ObjectUtils.toString(map.get("CUST_ID")));
			}
		}

		return_VO.setResultList(ans);
		this.sendRtnObject(return_VO);
	}

}