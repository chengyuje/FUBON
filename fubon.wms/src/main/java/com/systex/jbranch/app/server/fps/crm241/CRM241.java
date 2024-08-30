package com.systex.jbranch.app.server.fps.crm241;


import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * @author walalala
 * @date 2016/06/07
 * 
 */
@Component("crm241")
@Scope("request")
public class CRM241 extends FubonWmsBizLogic {
	
	private DataAccessManager dam = null;
	
	public void inquire(Object body, IPrimitiveMap header) throws JBranchException {
		CRM241InputVO inputVO = (CRM241InputVO) body;
		CRM241OutputVO outputVO = new CRM241OutputVO();
		dam = this.getDataAccessManager();
		
		// 依系統角色決定下拉選單可視範圍
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> fcMap = xmlInfo.doGetVariable("FUBONSYS.FC_ROLE", FormatHelper.FORMAT_2);		//理專
		Map<String, String> fchMap = xmlInfo.doGetVariable("FUBONSYS.FCH_ROLE", FormatHelper.FORMAT_2);		//FCH理專
		
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT LOG.CUST_ID, CU.CUST_NAME, CU.BRA_NBR, ORG.BRANCH_NAME AS BRA_NAME, AVG_AUM_AMT, AO_ME.AO_CODE AS LM_AO_CODE, NOTE.OBU_YN,");
		sql.append("       LOG.ORG_AO_CODE, LOG.NEW_AO_CODE, CU.AO_CODE AS CUR_AO_CODE, AO.EMP_ID, LOG.ORG_AO_NAME, LOG.LETGO_DATETIME ");
		sql.append("FROM TBCRM_CUST_AOCODE_CHGLOG LOG ");
		sql.append("INNER JOIN TBCRM_CUST_MAST CU ON LOG.CUST_ID = CU.CUST_ID ");
		sql.append("LEFT JOIN TBCRM_CUST_NOTE NOTE ON CU.CUST_ID = NOTE.CUST_ID ");
		sql.append("LEFT JOIN VWORG_DEFN_INFO ORG ON CU.BRA_NBR = ORG.BRANCH_NBR ");
		sql.append("LEFT JOIN TBCRM_CUST_AUM_MONTHLY AUM ON LOG.CUST_ID = AUM.CUST_ID ");
		sql.append("LEFT JOIN VWORG_AO_INFO AO ON CU.AO_CODE = AO.AO_CODE ");
		sql.append("LEFT JOIN TBPMS_CUST_AO_ME AO_ME ON LOG.CUST_ID = AO_ME.CUST_ID AND AO_ME.YEARMONTH = TO_CHAR(ADD_MONTHS(TO_DATE(:query_month,'YYYYMM'),-1),'YYYYMM') ");
		sql.append("WHERE 1=1 ");
		
		// 查詢年月 必填
		sql.append("AND TO_CHAR(LOG.LETGO_DATETIME,'YYYYMM') = :query_month ");
		condition.setObject("query_month", inputVO.getQuery_month());
		
		// 查詢類別 必填
		// 登錄
		if("1".equals(inputVO.getQuery_type())) {
			
			if (StringUtils.lowerCase((String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)).indexOf("uhrm") >= 0 &&
				!StringUtils.equals(StringUtils.lowerCase((String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)), "uhrm")) {
				
				if (StringUtils.isNotBlank(inputVO.getInq_ao_code())) {
					sql.append("AND LOG.NEW_AO_CODE = :ao_code ");
					condition.setObject("ao_code", inputVO.getInq_ao_code());
				} else {
					sql.append("AND EXISTS (SELECT 1 FROM VWORG_EMP_UHRM_INFO U WHERE LOG.NEW_AO_CODE = U.UHRM_CODE) ");
				}
			} else {
				// 分行 必填
				sql.append("AND LOG.NEW_AO_BRH = :bra_nbr ");
				condition.setObject("bra_nbr", inputVO.getBra_nbr());
				
				// 理專
				if(StringUtils.isNotBlank(inputVO.getInq_ao_code())) {
					sql.append("AND LOG.NEW_AO_CODE = :ao_code ");
					condition.setObject("ao_code", inputVO.getInq_ao_code());
				} else {
					if (fcMap.containsKey((String)getUserVariable(FubonSystemVariableConsts.LOGINROLE)) || 
						fchMap.containsKey((String)getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
						sql.append("AND LOG.NEW_AO_CODE IN (:ao_list) ");
						condition.setObject("ao_list", getUserVariable(FubonSystemVariableConsts.LOGIN_AOCODE_LIST));
					}
				}
			}
		}
		// 移出
		else {
			if (StringUtils.lowerCase((String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)).indexOf("uhrm") >= 0 &&
				!StringUtils.equals(StringUtils.lowerCase((String) getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG)), "uhrm")) {

				if (StringUtils.isNotBlank(inputVO.getInq_ao_code())) {
					sql.append("AND LOG.ORG_AO_CODE = :ao_code ");
					condition.setObject("ao_code", inputVO.getInq_ao_code());
				} else {
					sql.append("AND EXISTS (SELECT 1 FROM VWORG_EMP_UHRM_INFO U WHERE LOG.ORG_AO_CODE = U.UHRM_CODE) ");
				}
			} else {
				// 分行 必填
				sql.append("AND LOG.ORG_AO_BRH = :bra_nbr ");
				condition.setObject("bra_nbr", inputVO.getBra_nbr());
				
				// 理專
				if(StringUtils.isNotBlank(inputVO.getInq_ao_code())) {
					sql.append("AND LOG.ORG_AO_CODE = :ao_code ");
					condition.setObject("ao_code", inputVO.getInq_ao_code());
				} else {
					if (fcMap.containsKey((String)getUserVariable(FubonSystemVariableConsts.LOGINROLE)) || 
						fchMap.containsKey((String)getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
						sql.append("AND LOG.ORG_AO_CODE IN (:ao_list) ");
						condition.setObject("ao_list", getUserVariable(FubonSystemVariableConsts.LOGIN_AOCODE_LIST));
					}
				}
			}
		}
				
		sql.append("ORDER BY LOG.LETGO_DATETIME DESC ");
		
		condition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(condition);
		outputVO.setResultList(list);
		this.sendRtnObject(outputVO);
	}

}