package com.systex.jbranch.app.server.fps.mao121;

import java.sql.Timestamp;
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
 * @date 2016/07/26
 * 
 */
@Component("mao121")
@Scope("request")
public class MAO121 extends FubonWmsBizLogic {
	
	private DataAccessManager dam = null;
	
	public void inquire(Object body, IPrimitiveMap header) throws JBranchException {
		
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> uhrmmgrMap = xmlInfo.doGetVariable("FUBONSYS.UHRMMGR_ROLE", FormatHelper.FORMAT_2);
		Map<String, String> uhrmMap = xmlInfo.doGetVariable("FUBONSYS.UHRM_ROLE", FormatHelper.FORMAT_2);
		
		MAO121InputVO inputVO = (MAO121InputVO) body;
		MAO121OutputVO outputVO = new MAO121OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		sql.append("SELECT DISTINCT DM.DEV_NBR AS BRANCH_NBR, ");
		sql.append("       E.AO_CODE, ");
		sql.append("       E.EMP_NAME, ");
		sql.append("       P.USE_DATE, ");
		sql.append("       P.VISIT_CUST_LIST, ");
		sql.append("       P.USE_PERIOD, ");
		sql.append("       P.DEV_NBR, ");
		sql.append("       P.DEV_STATUS, ");
		sql.append("       P.APL_EMP_ID, ");
		sql.append("       P.DEV_TAKE_EMP_ID, ");
		sql.append("       P.DEV_TAKE_DATETIME, ");
		sql.append("       P.DEV_RETURN_EMP_ID, ");
		sql.append("       P.DEV_RETURN_DATETIME, ");
		sql.append("       SUBSTR(P.USE_PERIOD_S_TIME, 1, 2) || ':00' AS START_TIME, ");
		sql.append("       CASE WHEN TO_NUMBER(SUBSTR(P.USE_PERIOD_E_TIME, 1, 2)) < 9 THEN '次日' ELSE '' END || SUBSTR(P.USE_PERIOD_E_TIME, 1, 2) || ':00' AS END_TIME ");
		sql.append("FROM TBMAO_DEV_APL_PLIST P ");
		sql.append("LEFT JOIN VWORG_BRANCH_EMP_DETAIL_INFO E ON P.APL_EMP_ID = E.EMP_ID ");
		sql.append("LEFT JOIN TBMAO_DEV_MAST DM ON P.DEV_NBR = DM.DEV_NBR ");
		sql.append("WHERE 1 = 1 ");

		if (uhrmmgrMap.containsKey((String)getUserVariable(FubonSystemVariableConsts.LOGINROLE)) || 
			uhrmMap.containsKey((String)getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			if (StringUtils.isNotBlank(inputVO.getBranch_area_id()) && !"null".equals(inputVO.getBranch_area_id())) {
				sql.append("AND DM.OP_NBR = :branchAreaID "); //營運區代碼
				queryCondition.setObject("branchAreaID", inputVO.getBranch_area_id());
			} else if (StringUtils.isNotBlank(inputVO.getRegion_center_id()) && !"null".equals(inputVO.getBranch_area_id())) {
				sql.append("AND DM.DC_NBR = :regionCenterID "); //區域代碼
				queryCondition.setObject("regionCenterID", inputVO.getRegion_center_id());
			}
		} else {
			if (StringUtils.isNotBlank(inputVO.getBranch_nbr()) && !"null".equals(inputVO.getBranch_nbr())) {
				sql.append("AND DM.BRA_NBR = :branchID "); //分行代碼
				queryCondition.setObject("branchID", inputVO.getBranch_nbr());
			} else if (StringUtils.isNotBlank(inputVO.getBranch_area_id()) && !"null".equals(inputVO.getBranch_area_id())) {
				sql.append("AND DM.OP_NBR = :branchAreaID "); //營運區代碼
				queryCondition.setObject("branchAreaID", inputVO.getBranch_area_id());
			} else if (StringUtils.isNotBlank(inputVO.getRegion_center_id()) && !"null".equals(inputVO.getBranch_area_id())) {
				sql.append("AND DM.DC_NBR = :regionCenterID "); //區域代碼
				queryCondition.setObject("regionCenterID", inputVO.getRegion_center_id());
			} else {
				sql.append("AND DM.BRA_NBR IN (:branchIDList) ");
				queryCondition.setObject("branchIDList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
			}
		}
		
		if (!StringUtils.isBlank(inputVO.getEmp_id())) {
			sql.append("AND P.APL_EMP_ID like :emp_id ");
			queryCondition.setObject("emp_id", inputVO.getEmp_id());
		}
			
		if (inputVO.getUse_date_bgn() != null) {
			sql.append("AND TRUNC(P.USE_DATE) >= TRUNC(:start) ");
			queryCondition.setObject("start", new Timestamp(inputVO.getUse_date_bgn().getTime()));
		}
			
		if (inputVO.getUse_date_end() != null) {
			sql.append("AND TRUNC(P.USE_DATE) <= TRUNC(:end) ");
			queryCondition.setObject("end", new Timestamp(inputVO.getUse_date_end().getTime()));
		}
		
		sql.append("ORDER BY P.DEV_TAKE_DATETIME, P.DEV_RETURN_DATETIME, DM.DEV_NBR ");
		
		queryCondition.setQueryString(sql.toString());
		
		outputVO.setResultList(dam.exeQuery(queryCondition));
		
		this.sendRtnObject(outputVO);
	}
}