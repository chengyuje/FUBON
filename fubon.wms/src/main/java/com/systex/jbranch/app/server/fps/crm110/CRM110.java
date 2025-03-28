package com.systex.jbranch.app.server.fps.crm110;

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

import org.apache.commons.lang.StringUtils;
import org.eclipse.birt.report.model.api.util.StringUtil;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;



/**
 * @author walalala、Carley
 * @date 2016/08/18
 * 
 *       Mark: 2017/01/03 增加消金PS查詢上限 Stella
 */
@Component("crm110")
@Scope("request")
public class CRM110 extends FubonWmsBizLogic {
	
	private DataAccessManager dam;

	//可視範圍 : 轄下掛Code客戶、歸屬行或帳戶行空Code客戶
	public void inquire(Object body, IPrimitiveMap header) throws JBranchException {
		
		CRM110InputVO inputVO = (CRM110InputVO) body;
		CRM110OutputVO return_VO = new CRM110OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		XmlInfo xmlInfo = new XmlInfo();
		boolean isHANDMGR = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2).containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE).toString());
		boolean isFAIA = xmlInfo.doGetVariable("FUBONSYS.FAIA_ROLE", FormatHelper.FORMAT_2).containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE));
		boolean isFC = xmlInfo.doGetVariable("FUBONSYS.FC_ROLE", FormatHelper.FORMAT_2).containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE).toString());
		boolean isPAO = xmlInfo.doGetVariable("FUBONSYS.PAO_ROLE", FormatHelper.FORMAT_2).containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE));
		boolean isNEWPS = xmlInfo.doGetVariable("FUBONSYS.NEWPS_ROLE", FormatHelper.FORMAT_2).containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE));
		
		String brNbr = getUserVariable(FubonSystemVariableConsts.LOGINBRH).toString();

		sql.append("SELECT CUST_ID, CUST_NAME, BRA_NBR, AO_CODE ");
		sql.append("FROM TBCRM_CUST_MAST CM ");
		sql.append("WHERE 1 = 1 ");

		// UHRM
		if (StringUtils.lowerCase((String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)).indexOf("uhrm") >= 0) {
			sql.append("AND EXISTS (SELECT T.CUST_ID FROM VWORG_EMP_UHRM_INFO T WHERE T.UHRM_CODE = CM.AO_CODE) ");
		// 非總行人員
		} else if (!isHANDMGR) {
			if (StringUtils.equals("JRM", getCommonVariable(FubonSystemVariableConsts.LOGINROLE).toString())) {
				// 開放全行查詢
			} else {
				sql.append("AND BRA_NBR in (:branchlist) ");
				
				//理專不可查私銀客戶
				if (isFC) {
					sql.append("and NOT EXISTS (SELECT 1 FROM VWORG_EMP_UHRM_INFO T WHERE T.UHRM_CODE = CM.AO_CODE) ");
				}

				// 輔銷人員 FAIA、無歸屬行（分行000）、PAO/新興PS 使用轄下可視分行作條件查詢
				if (isFAIA || isPAO || isNEWPS || StringUtil.isEqual(brNbr, "000")) {
					queryCondition.setObject("branchlist", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
				} else {
					queryCondition.setObject("branchlist", brNbr);
				}
			}
		}

		if (StringUtils.isNotBlank(inputVO.getCust_id())) {
			sql.append("AND CUST_ID = :cust_id ");
			queryCondition.setObject("cust_id", inputVO.getCust_id());
		}

		if (StringUtils.isNotBlank(inputVO.getCust_name())) {
			sql.append("AND CUST_NAME LIKE :cust_name ");
			queryCondition.setObject("cust_name", "%" + inputVO.getCust_name() + "%");
		}

		//判斷行動載具登入，查詢條件需檢核申請許可時間段的客戶清單
		if (getUserVariable(FubonSystemVariableConsts.LOGIN_SOURCE_TOKEN) != null) {
			String loginToken = getUserVariable(FubonSystemVariableConsts.LOGIN_SOURCE_TOKEN).toString();
			if ("mobile".equals(loginToken)) {
				sql.append("AND CUST_ID IN (SELECT CUST_ID FROM VWCRM_MAO_QRY_CUST WHERE AO_CODE IN (:loginAoCode)) ");
				queryCondition.setObject("loginAoCode", getUserVariable(FubonSystemVariableConsts.LOGIN_AOCODE_LIST));
			}
		}

		// PS查詢上限
		if (StringUtil.isEqual(inputVO.getRole(), "ps")) {
			Map<String, String> qry_max_limit_xml = xmlInfo.doGetVariable("CRM.CFUSER_QRY_MAX_LIMIT", FormatHelper.FORMAT_2);

			String qry_max_limit = "";
			for (Object key : qry_max_limit_xml.keySet()) {
				qry_max_limit = key.toString();
			}
			
			sql.append("AND ROWNUM <= :qry_max_limit ");
			
			queryCondition.setObject("qry_max_limit", qry_max_limit);
		} else {
			queryCondition.setMaxResults((Integer) SysInfo.getInfoValue(FubonSystemVariableConsts.QRY_MAX_RESULTS));
		}

		queryCondition.setQueryString(sql.toString());

		return_VO.setResultList(dam.exeQuery(queryCondition));
		
		sendRtnObject(return_VO);
	}
	
	//查詢
	public void inquireCust(Object body, IPrimitiveMap header) throws JBranchException {

		CRM110InputVO inputVO = (CRM110InputVO) body;
		CRM110OutputVO return_VO = new CRM110OutputVO();
		dam = this.getDataAccessManager();

		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();

		//判斷行動載具登入，查詢條件需檢核申請許可時間段的客戶清單
		if (getUserVariable(FubonSystemVariableConsts.LOGIN_SOURCE_TOKEN) != null) {
			String loginToken = getUserVariable(FubonSystemVariableConsts.LOGIN_SOURCE_TOKEN).toString();
			if ("mobile".equals(loginToken)) {
				sql.append("SELECT CUST_ID FROM VWCRM_MAO_QRY_CUST WHERE AO_CODE in (:loginAoCode) ");
				sql.append("AND CUST_ID = :cust_id ");
				queryCondition.setObject("loginAoCode", getUserVariable(FubonSystemVariableConsts.LOGIN_AOCODE_LIST));
				queryCondition.setObject("cust_id", inputVO.getCust_id());
				queryCondition.setQueryString(sql.toString());
				List<Map<String, Object>> list = dam.exeQuery(queryCondition);
				if (list == null) {
					throw new JBranchException("ehl_01_crm110_001"); //該客戶未事先申請，故無法查詢。
				}
			}
		} else {
			sql.append("SELECT CUST.BRA_NBR, DEFN.BRANCH_NAME, AO.AO_CODE, AO.EMP_NAME, CASE WHEN NOTE.CO_ACCT_YN = 'Y' THEN NOTE.RM_NAME ELSE NULL END AS RM_NAME ");
			sql.append("FROM TBCRM_CUST_MAST CUST ");
			sql.append("LEFT JOIN TBCRM_CUST_NOTE NOTE ON CUST.CUST_ID = NOTE.CUST_ID ");
			sql.append("LEFT JOIN VWORG_AO_INFO AO ON CUST.AO_CODE = AO.AO_CODE ");
			sql.append("LEFT JOIN VWORG_DEFN_INFO DEFN ON CUST.BRA_NBR = DEFN.BRANCH_NBR ");
			sql.append("WHERE CUST.CUST_ID = :cust_id ");
			queryCondition.setObject("cust_id", inputVO.getCust_id());
			queryCondition.setQueryString(sql.toString());
			List<Map<String, Object>> list = dam.exeQuery(queryCondition);

			return_VO.setResultList(list);
			sendRtnObject(return_VO);
		}
	}

	public void checkCust(Object body, IPrimitiveMap header) throws JBranchException {

		CRM110InputVO inputVO = (CRM110InputVO) body;
		CRM110OutputVO return_VO = new CRM110OutputVO();
		dam = this.getDataAccessManager();

		// 依系統角色決定下拉選單可視範圍
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> headmgrMap = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2); //總行
		Map<String, String> armgrMap = xmlInfo.doGetVariable("FUBONSYS.ARMGR_ROLE", FormatHelper.FORMAT_2); //業務處
		Map<String, String> mbrmgrMap = xmlInfo.doGetVariable("FUBONSYS.MBRMGR_ROLE", FormatHelper.FORMAT_2); //營運區
		Map<String, String> bmmgrMap = xmlInfo.doGetVariable("FUBONSYS.BMMGR_ROLE", FormatHelper.FORMAT_2); // 分行人員

		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT CUST_ID FROM TBCRM_CUST_MAST CUST WHERE CUST.CUST_ID = :cust_id ");
		queryCondition.setObject("cust_id", inputVO.getCust_id());
		if (headmgrMap.containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE)) 
				|| armgrMap.containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE)) 
				|| mbrmgrMap.containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE))
				|| bmmgrMap.containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			sql.append("AND CUST.BRA_NBR IN (:brNbrList)");
			queryCondition.setObject("brNbrList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
		}
		// 理專
		else {
			sql.append("AND CUST.BRA_NBR IN (:brNbrList) AND CUST.AO_CODE IN (:AO_CODE) ");
			queryCondition.setObject("brNbrList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
			queryCondition.setObject("AO_CODE", getUserVariable(FubonSystemVariableConsts.LOGIN_AOCODE_LIST));
		}
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		return_VO.setResultList(list);
		this.sendRtnObject(return_VO);
	}
	
	public void getForbiddenList(Object body, IPrimitiveMap header) throws JBranchException {
		CRM110InputVO inputVO = (CRM110InputVO) body;
		CRM110OutputVO return_VO = new CRM110OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT DISTINCT OU_ID_NO AS CUST_ID ");
		sql.append("FROM TBCRM_PIMDATA");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> resultList = dam.exeQuery(queryCondition);
		return_VO.setResultList(resultList);
		this.sendRtnObject(return_VO);
	}
}