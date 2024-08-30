package com.systex.jbranch.app.server.fps.fps817;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Component("fps817")
@Scope("request")
public class FPS817 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(FPS817.class);
	
	public void inquire(Object body, IPrimitiveMap header) throws JBranchException {
		FPS817InputVO inputVO = (FPS817InputVO) body;
		FPS817OutputVO return_VO = new FPS817OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		// 主管
		if(inputVO.getIsManger()) {
			sql.append("select REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME, BRANCH_NBR, BRANCH_NAME, ");
			sql.append("EMP_ID, EMP_NAME, AO_JOB_RANK, CUST_ID, CUST_NAME, SPP_TYPE, INV_AMOUNT, PLAN_MARKET_VALUE, ");
			sql.append("PLAN_INCOME, C_RETURN, TARGET_AMOUNT, ACH_RATE, RDM_INCOME_TTL, PROGRESS_DESC, MEMO ");
			sql.append("from TBFPS_DASHBOARD_DETAIL_SPP where 1=1 ");
			// where
			if (StringUtils.isNotBlank(inputVO.getRegion_center_id())) {
				sql.append("AND REGION_CENTER_ID = :region ");
				queryCondition.setObject("region", inputVO.getRegion_center_id());
			} else {
				sql.append("AND (REGION_CENTER_ID IN (:rcIdList) OR REGION_CENTER_ID IS NULL) ");
				sql.append("AND (BRANCH_AREA_ID IN (:opIdList) OR BRANCH_AREA_ID IS NULL) ");
				sql.append("AND (BRANCH_NBR IN (:brNbrList) OR BRANCH_NBR IS NULL) ");
				queryCondition.setObject("rcIdList", getUserVariable(FubonSystemVariableConsts.AVAILREGIONLIST));
				queryCondition.setObject("opIdList", getUserVariable(FubonSystemVariableConsts.AVAILAREALIST));
				queryCondition.setObject("brNbrList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
			}
			if (StringUtils.isNotBlank(inputVO.getBranch_area_id())) {
				sql.append("AND BRANCH_AREA_ID = :branch_area ");
				queryCondition.setObject("branch_area", inputVO.getBranch_area_id());
			}
			if (StringUtils.isNotBlank(inputVO.getBranch_nbr())) {
				sql.append("AND BRANCH_NBR = :branch ");
				queryCondition.setObject("branch", inputVO.getBranch_nbr());
			}
			if (StringUtils.isNotBlank(inputVO.getAo_job_rank())) {
				sql.append("AND AO_JOB_RANK = :ao_job_rank ");
				queryCondition.setObject("ao_job_rank", inputVO.getAo_job_rank());
			}
			if(inputVO.getsDate() != null) {
				sql.append("and PLAN_CREATETIME >= TRUNC(:start) ");
				queryCondition.setObject("start", inputVO.getsDate());
			}
			if(inputVO.geteDate() != null) {
				sql.append("and PLAN_CREATETIME < TRUNC(:end)+1 ");
				queryCondition.setObject("end", inputVO.geteDate());
			}
		}
		// 理專
		else {
			sql.append("select CUST_ID, CUST_NAME, VIP_DEGREE, CUST_RISK_ATR, SPP_TYPE, ");
			sql.append("INV_AMOUNT, PLAN_MARKET_VALUE, PLAN_INCOME, C_RETURN, ");
			sql.append("TARGET_AMOUNT, ACH_RATE, RDM_INCOME_TTL, PROGRESS_DESC, MEMO ");
			sql.append("from TBFPS_DASHBOARD_DETAIL_SPP where 1=1 ");
			// where
			if (StringUtils.isNotBlank(inputVO.getRegion_center_id())) {
				sql.append("AND REGION_CENTER_ID = :region ");
				queryCondition.setObject("region", inputVO.getRegion_center_id());
			} else {
				sql.append("AND (REGION_CENTER_ID IN (:rcIdList) OR REGION_CENTER_ID IS NULL) ");
				sql.append("AND (BRANCH_AREA_ID IN (:opIdList) OR BRANCH_AREA_ID IS NULL) ");
				sql.append("AND (BRANCH_NBR IN (:brNbrList) OR BRANCH_NBR IS NULL) ");
				queryCondition.setObject("rcIdList", getUserVariable(FubonSystemVariableConsts.AVAILREGIONLIST));
				queryCondition.setObject("opIdList", getUserVariable(FubonSystemVariableConsts.AVAILAREALIST));
				queryCondition.setObject("brNbrList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
			}
			if (StringUtils.isNotBlank(inputVO.getBranch_area_id())) {
				sql.append("AND BRANCH_AREA_ID = :branch_area ");
				queryCondition.setObject("branch_area", inputVO.getBranch_area_id());
			}
			if (StringUtils.isNotBlank(inputVO.getBranch_nbr())) {
				sql.append("AND BRANCH_NBR = :branch ");
				queryCondition.setObject("branch", inputVO.getBranch_nbr());
			}
			sql.append("AND EMP_ID = :emp_id ");
			queryCondition.setObject("emp_id", getUserVariable(FubonSystemVariableConsts.LOGINID));
			if (StringUtils.isNotBlank(inputVO.getPlanning())) {
				sql.append("AND SPP_TYPE = :planning ");
				queryCondition.setObject("planning", inputVO.getPlanning());
			}
			if (StringUtils.isNotBlank(inputVO.getPlan_category())) {
				sql.append("AND PLAN_CATEGORY = :plan_category ");
				queryCondition.setObject("plan_category", inputVO.getPlan_category());
			}
			if (StringUtils.isNotBlank(inputVO.getVip_degree())) {
				sql.append("AND VIP_DEGREE = :vip_degree ");
				queryCondition.setObject("vip_degree", inputVO.getVip_degree());
			}
			if (StringUtils.isNotBlank(inputVO.getMemo())) {
				sql.append("AND MEMO = :memo ");
				queryCondition.setObject("memo", inputVO.getMemo());
			}
			if(inputVO.getsDate() != null) {
				sql.append("and PLAN_CREATETIME >= TRUNC(:start) ");
				queryCondition.setObject("start", inputVO.getsDate());
			}
			if(inputVO.geteDate() != null) {
				sql.append("and PLAN_CREATETIME < TRUNC(:end)+1 ");
				queryCondition.setObject("end", inputVO.geteDate());
			}
		}
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		return_VO.setResultList(list);
		
		this.sendRtnObject(return_VO);
	}
	
	
	
	
	
}