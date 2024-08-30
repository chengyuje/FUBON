package com.systex.jbranch.app.server.fps.fps816;

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

@Component("fps816")
@Scope("request")
public class FPS816 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(FPS816.class);
	
	public void inquire(Object body, IPrimitiveMap header) throws JBranchException {
		FPS816InputVO inputVO = (FPS816InputVO) body;
		FPS816OutputVO return_VO = new FPS816OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		// 主管
		if(inputVO.getIsManger()) {
			// SPP
			sql.append("WITH BASE AS ( ");
			sql.append("select REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME, BRANCH_NBR, BRANCH_NAME, ");
			sql.append("EMP_ID, EMP_NAME, AO_JOB_RANK, PCH_TTL, PCH_FUND, PCH_ETF, PCH_BOND, PCH_SN, PCH_SI, PCH_INS_1, PCH_INS_2, ");
			sql.append("N_PCH_TTL, N_PCH_FUND, N_PCH_ETF, N_PCH_BOND, N_PCH_SN, N_PCH_SI, N_PCH_INS_1, N_PCH_INS_2, FEE_TTL, ");
			sql.append("FEE_FUND, FEE_ETF, FEE_BOND, FEE_SN, FEE_SI, FEE_INS_1, FEE_INS_2, PLAN_TYPE ");
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
			// INV
			sql.append("UNION ALL ");
			sql.append("select REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME, BRANCH_NBR, BRANCH_NAME, ");
			sql.append("EMP_ID, EMP_NAME, AO_JOB_RANK, PCH_TTL, PCH_FUND, PCH_ETF, PCH_BOND, PCH_SN, PCH_SI, PCH_INS_1, PCH_INS_2, ");
			sql.append("N_PCH_TTL, N_PCH_FUND, N_PCH_ETF, N_PCH_BOND, N_PCH_SN, N_PCH_SI, N_PCH_INS_1, N_PCH_INS_2, FEE_TTL, ");
			sql.append("FEE_FUND, FEE_ETF, FEE_BOND, FEE_SN, FEE_SI, FEE_INS_1, FEE_INS_2, PLAN_TYPE ");
			sql.append("from TBFPS_DASHBOARD_DETAIL_INV where 1=1 ");
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
			sql.append(") ");
			//
			sql.append("select REGION_CENTER_NAME, BRANCH_AREA_NAME, BRANCH_NBR, BRANCH_NAME, AO_JOB_RANK, EMP_ID, EMP_NAME, sum(PCH_TTL) PCH_TTL, sum(PCH_FUND) PCH_FUND, ");
			sql.append("sum(PCH_ETF) PCH_ETF, sum(PCH_BOND) PCH_BOND, sum(PCH_SN) PCH_SN, sum(PCH_SI) PCH_SI, sum(PCH_INS_1) PCH_INS_1, sum(PCH_INS_2) PCH_INS_2, sum(N_PCH_TTL) N_PCH_TTL, sum(N_PCH_FUND) N_PCH_FUND, ");
			sql.append("sum(N_PCH_ETF) N_PCH_ETF, sum(N_PCH_BOND) N_PCH_BOND, sum(N_PCH_SN) N_PCH_SN, sum(N_PCH_SI) N_PCH_SI, sum(N_PCH_INS_1) N_PCH_INS_1, sum(N_PCH_INS_2) N_PCH_INS_2, sum(FEE_TTL) FEE_TTL, ");
			sql.append("sum(FEE_FUND) FEE_FUND, sum(FEE_ETF) FEE_ETF, sum(FEE_BOND) FEE_BOND, sum(FEE_SN) FEE_SN, sum(FEE_SI) FEE_SI, sum(FEE_INS_1) FEE_INS_1, sum(FEE_INS_2) FEE_INS_2 ");
			sql.append("from BASE ");
			sql.append("group by REGION_CENTER_NAME, BRANCH_AREA_NAME, BRANCH_NBR, BRANCH_NAME, AO_JOB_RANK, EMP_ID, EMP_NAME ");
		}
		// 理專
		else {
			// SPP
			sql.append("WITH BASE AS ( ");
			sql.append("select REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME, BRANCH_NBR, BRANCH_NAME, ");
			sql.append("VIP_DEGREE, CUST_ID, CUST_NAME, PCH_TTL, PCH_FUND, PCH_ETF, PCH_BOND, PCH_SN, PCH_SI, PCH_INS_1, PCH_INS_2, ");
			sql.append("N_PCH_TTL, N_PCH_FUND, N_PCH_ETF, N_PCH_BOND, N_PCH_SN, N_PCH_SI, N_PCH_INS_1, N_PCH_INS_2, FEE_TTL, ");
			sql.append("FEE_FUND, FEE_ETF, FEE_BOND, FEE_SN, FEE_SI, FEE_INS_1, FEE_INS_2, PLAN_TYPE, PLAN_CATEGORY ");
			sql.append("from TBFPS_DASHBOARD_DETAIL_SPP where 1=1 ");
			// where
			if (StringUtils.isNotBlank(inputVO.getRegion_center_id())) {
				sql.append("AND REGION_CENTER_ID = :region ");
				queryCondition.setObject("region", inputVO.getRegion_center_id());
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
			if (StringUtils.isNotBlank(inputVO.getVip_degree())) {
				sql.append("AND VIP_DEGREE = :vip_degree ");
				queryCondition.setObject("vip_degree", inputVO.getVip_degree());
			}
			if (StringUtils.isNotBlank(inputVO.getPlan_category())) {
				sql.append("AND PLAN_CATEGORY = :plan_category ");
				queryCondition.setObject("plan_category", inputVO.getPlan_category());
			}
			if(inputVO.getsDate() != null) {
				sql.append("and PLAN_CREATETIME >= TRUNC(:start) ");
				queryCondition.setObject("start", inputVO.getsDate());
			}
			if(inputVO.geteDate() != null) {
				sql.append("and PLAN_CREATETIME < TRUNC(:end)+1 ");
				queryCondition.setObject("end", inputVO.geteDate());
			}
			// INV
			sql.append("UNION ALL ");
			sql.append("select REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME, BRANCH_NBR, BRANCH_NAME, ");
			sql.append("VIP_DEGREE, CUST_ID, CUST_NAME, PCH_TTL, PCH_FUND, PCH_ETF, PCH_BOND, PCH_SN, PCH_SI, PCH_INS_1, PCH_INS_2, ");
			sql.append("N_PCH_TTL, N_PCH_FUND, N_PCH_ETF, N_PCH_BOND, N_PCH_SN, N_PCH_SI, N_PCH_INS_1, N_PCH_INS_2, FEE_TTL, ");
			sql.append("FEE_FUND, FEE_ETF, FEE_BOND, FEE_SN, FEE_SI, FEE_INS_1, FEE_INS_2, PLAN_TYPE, PLAN_CATEGORY ");
			sql.append("from TBFPS_DASHBOARD_DETAIL_INV where 1=1 ");
			// where
			if (StringUtils.isNotBlank(inputVO.getRegion_center_id())) {
				sql.append("AND REGION_CENTER_ID = :region ");
				queryCondition.setObject("region", inputVO.getRegion_center_id());
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
			if (StringUtils.isNotBlank(inputVO.getVip_degree())) {
				sql.append("AND VIP_DEGREE = :vip_degree ");
				queryCondition.setObject("vip_degree", inputVO.getVip_degree());
			}
			if (StringUtils.isNotBlank(inputVO.getPlan_category())) {
				sql.append("AND PLAN_CATEGORY = :plan_category ");
				queryCondition.setObject("plan_category", inputVO.getPlan_category());
			}
			if(inputVO.getsDate() != null) {
				sql.append("and PLAN_CREATETIME >= TRUNC(:start) ");
				queryCondition.setObject("start", inputVO.getsDate());
			}
			if(inputVO.geteDate() != null) {
				sql.append("and PLAN_CREATETIME < TRUNC(:end)+1 ");
				queryCondition.setObject("end", inputVO.geteDate());
			}
			sql.append(") ");
			//
			sql.append("select REGION_CENTER_NAME, BRANCH_AREA_NAME, BRANCH_NBR, BRANCH_NAME, VIP_DEGREE, CUST_ID, CUST_NAME, PLAN_TYPE, PLAN_CATEGORY, sum(PCH_TTL) PCH_TTL, sum(PCH_FUND) PCH_FUND, ");
			sql.append("sum(PCH_ETF) PCH_ETF, sum(PCH_BOND) PCH_BOND, sum(PCH_SN) PCH_SN, sum(PCH_SI) PCH_SI, sum(PCH_INS_1) PCH_INS_1, sum(PCH_INS_2) PCH_INS_2, sum(N_PCH_TTL) N_PCH_TTL, sum(N_PCH_FUND) N_PCH_FUND, ");
			sql.append("sum(N_PCH_ETF) N_PCH_ETF, sum(N_PCH_BOND) N_PCH_BOND, sum(N_PCH_SN) N_PCH_SN, sum(N_PCH_SI) N_PCH_SI, sum(N_PCH_INS_1) N_PCH_INS_1, sum(N_PCH_INS_2) N_PCH_INS_2, sum(FEE_TTL) FEE_TTL, ");
			sql.append("sum(FEE_FUND) FEE_FUND, sum(FEE_ETF) FEE_ETF, sum(FEE_BOND) FEE_BOND, sum(FEE_SN) FEE_SN, sum(FEE_SI) FEE_SI, sum(FEE_INS_1) FEE_INS_1, sum(FEE_INS_2) FEE_INS_2 ");
			sql.append("from BASE where 1=1 ");
			if (StringUtils.isNotBlank(inputVO.getPlan_type())) {
				sql.append("AND PLAN_TYPE = :plan_type ");
				queryCondition.setObject("plan_type", inputVO.getPlan_type());
			}
			sql.append("group by REGION_CENTER_NAME, BRANCH_AREA_NAME, BRANCH_NBR, BRANCH_NAME, VIP_DEGREE, CUST_ID, CUST_NAME, PLAN_TYPE, PLAN_CATEGORY ");
		}
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		return_VO.setResultList(list);
		
		this.sendRtnObject(return_VO);
	}
	
	
	
	
	
}