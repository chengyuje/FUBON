package com.systex.jbranch.app.server.fps.pms901;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * @author Carley
 * @date 2021/01/15
 * 
 */
@Component("pms901")
@Scope("request")
public class PMS901 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	
	//查詢
	public void query (Object body, IPrimitiveMap header) throws Exception {
		PMS901InputVO inputVO = (PMS901InputVO) body;
		PMS901OutputVO outputVO = new PMS901OutputVO();
		List<Map<String,Object>> resultList = new ArrayList<Map<String,Object>>();
		
		if(StringUtils.equals("3", inputVO.getSetupCategory())) {
			//以商品查詢
			resultList = queryByProd(inputVO);
		} else if(StringUtils.equals("2", inputVO.getSetupCategory())) {
			//以組織查詢
			resultList = queryByOrg(inputVO);
		} else {
			//以日期區間查詢
			resultList = queryByDate(inputVO);
		}
		
		outputVO.setResultList(resultList);
		
		this.sendRtnObject(outputVO);
	}
	
	/**
	 * 以日期區間為X軸做統計
	 * @param inputVO
	 * @return
	 * @throws DAOException
	 * @throws JBranchException
	 */
	private List<Map<String,Object>> queryByDate(PMS901InputVO inputVO) throws DAOException, JBranchException {
		List<Map<String,Object>> resultList = new ArrayList<Map<String,Object>>();	
		
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT YEARMON, ");
		switch (inputVO.getSetupProd()) {
			case "1":	//全商品(投資、存款、保險)總計
				sb.append(" ROUND(SUM(INV_BAL)/10000,2) AS 投資_總計, ");
				sb.append(" ROUND(SUM(INS_BAL)/10000,2) AS 保險_總計, ");
				sb.append(" ROUND(SUM(DEP_BAL)/10000,2) AS 存款_總計 ");
				break;
			case "2":	//投資總計
				sb.append(" ROUND(SUM(INV_FUND)/10000,2) AS 投資_基金, ");
				sb.append(" ROUND(SUM(INV_STOCK)/10000,2) AS 投資_股票, ");
				sb.append(" ROUND(SUM(INV_ETF)/10000,2) AS 投資_ETF, ");
				sb.append(" ROUND(SUM(INV_SI)/10000,2) AS 投資_SI, ");
				sb.append(" ROUND(SUM(INV_SN)/10000,2) AS 投資_SN, ");
				sb.append(" ROUND(SUM(INV_BOND)/10000,2) AS 投資_海外債, ");
				sb.append(" ROUND(SUM(INV_RP)/10000,2) AS 投資_RP複委託, ");
				sb.append(" ROUND(SUM(INV_STRST)/10000,2) AS 投資_指定單獨信託, ");
				sb.append(" ROUND(SUM(INV_GOLD)/10000,2) AS 投資_黃金存摺, ");
				sb.append(" ROUND(SUM(INV_DCI)/10000,2) AS 投資_DCI, ");
				sb.append(" ROUND(SUM(INV_NAMI)/10000,2) AS 投資_奈米投 ");
				break;
			case "3":	//存款總計
				sb.append(" ROUND(SUM(DEP_SAV)/10000,2) AS 存款_台幣活存, ");
				sb.append(" ROUND(SUM(DEP_CD)/10000,2) AS 存款_台幣定存, ");
				sb.append(" ROUND(SUM(DEP_FSAV)/10000,2) AS 存款_外幣活存, ");
				sb.append(" ROUND(SUM(DEP_FCD)/10000,2) AS 存款_外幣定存, ");
				sb.append(" ROUND(SUM(DEP_CSAV)/10000,2) AS 存款_台幣支存 ");
				break;
			case "4":	//保險總計
				sb.append(" ROUND(SUM(INS_IV)/10000,2) AS 保險_投資型, ");
				sb.append(" ROUND(SUM(INS_OT)/10000,2) AS 保險_躉繳, ");
				sb.append(" ROUND(SUM(INS_SY)/10000,2) AS 保險_短年期繳, ");
				sb.append(" ROUND(SUM(INS_LY)/10000,2) AS 保險_長年期繳 ");
				break;
			case "5":
				sb.append(" ROUND(SUM(INV_FUND)/10000,2) AS 投資_基金 ");
				break;
			case "6":
				sb.append(" ROUND(SUM(INV_STOCK)/10000,2) AS 投資_股票 ");
				break;
			case "7":
				sb.append(" ROUND(SUM(INV_ETF)/10000,2) AS 投資_ETF ");
				break;
			case "8":
				sb.append(" ROUND(SUM(INV_SI)/10000,2) AS 投資_SI ");
				break;
			case "9":
				sb.append(" ROUND(SUM(INV_SN)/10000,2) AS 投資_SN ");
				break;
			case "10":
				sb.append(" ROUND(SUM(INV_BOND)/10000,2) AS 投資_海外債 ");
				break;
			case "11":
				sb.append(" ROUND(SUM(INV_RP)/10000,2) AS 投資_RP複委託 ");
				break;
			case "12":
				sb.append(" ROUND(SUM(INV_STRST)/10000,2) AS 投資_指定單獨信託 ");
				break;
			case "13":
				sb.append(" ROUND(SUM(INV_GOLD)/10000,2) AS 投資_黃金存摺 ");
				break;
			case "14":
				sb.append(" ROUND(SUM(INV_DCI)/10000,2) AS 投資_DCI ");
				break;
			case "15":
				sb.append(" ROUND(SUM(INV_NAMI)/10000,2) AS 投資_奈米投 ");
				break;
			case "16":
				sb.append(" ROUND(SUM(DEP_SAV)/10000,2) AS 存款_台幣活存 ");
				break;
			case "17":
				sb.append(" ROUND(SUM(DEP_CD)/10000,2) AS 存款_台幣定存 ");
				break;
			case "18":
				sb.append(" ROUND(SUM(DEP_FSAV)/10000,2) AS 存款_外幣活存 ");
				break;
			case "19":
				sb.append(" ROUND(SUM(DEP_FCD)/10000,2) AS 存款_外幣定存 ");
				break;
			case "20":
				sb.append(" ROUND(SUM(DEP_CSAV)/10000,2) AS 存款_台幣支存 ");
				break;
			case "21":
				sb.append(" ROUND(SUM(INS_IV)/10000,2) AS 保險_投資型 ");
				break;
			case "22":
				sb.append(" ROUND(SUM(INS_OT)/10000,2) AS 保險_躉繳 ");
				break;
			case "23":
				sb.append(" ROUND(SUM(INS_SY)/10000,2) AS 保險_短年期繳 ");
				break;
			case "24":
				sb.append(" ROUND(SUM(INS_LY)/10000,2) AS 保險_長年期繳 ");
				break;
			case "25":
				sb.append(" ROUND(SUM(A_AVG_AUM)/10000,2) AS A板塊, ");
				sb.append(" ROUND(SUM(C_AVG_AUM)/10000,2) AS C板塊 ");
				break;
			case "26":
				sb.append(" ROUND(SUM(CUST_TYPE_PERSON)/10000,2) AS 個人, ");
				sb.append(" ROUND(SUM(CUST_TYPE_CORPOR)/10000,2) AS 法人 ");
				break;
		}		
		sb.append("FROM TBPMS_MONTHLY_AUM ");
		sb.append("WHERE 1 = 1 ");
		
		if (inputVO.getsDate() != null) {
			sb.append("AND YEARMON >= TO_CHAR( :sDate , 'YYYYMM') ");
			queryCondition.setObject("sDate", inputVO.getsDate());
		}
		
		if (inputVO.geteDate() != null) {
			sb.append("AND YEARMON <= TO_CHAR( :eDate , 'YYYYMM') ");
			queryCondition.setObject("eDate", inputVO.geteDate());
		}
		
		if (StringUtils.isNotBlank(inputVO.getEmp_id())) {
			sb.append("AND EMP_ID = :emp_id ");
			queryCondition.setObject("emp_id", inputVO.getEmp_id());
		} else if (StringUtils.isNotBlank(inputVO.getBranch_nbr())) {
			sb.append("AND BRANCH_NBR = :branch_nbr ");
			queryCondition.setObject("branch_nbr", inputVO.getBranch_nbr());
		} else if (StringUtils.isNotBlank(inputVO.getBranch_area_id())) { 
			sb.append("AND BRANCH_AREA_ID = :branch_area_id ");
			queryCondition.setObject("branch_area_id", inputVO.getBranch_area_id());
		} else if (StringUtils.isNotBlank(inputVO.getRegion_center_id())) {
			sb.append("AND REGION_CENTER_ID = :region_center_id ");
			queryCondition.setObject("region_center_id", inputVO.getRegion_center_id());
		}
		
		sb.append("GROUP BY YEARMON ");
		
		queryCondition.setQueryString(sb.toString());
		resultList = dam.exeQuery(queryCondition);
		
		return resultList;
	}
	 
	/** 
	 * 以商品做X軸統計
	 * @param inputVO
	 * @return
	 * @throws DAOException
	 * @throws JBranchException
	 */
	private List<Map<String,Object>> queryByProd(PMS901InputVO inputVO) throws DAOException, JBranchException {
		List<Map<String,Object>> resultList = new ArrayList<Map<String,Object>>();	
		
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT YEARMON, ");
		switch (inputVO.getSetupProd()) {
			case "1":	//全商品(投資、存款、保險)總計
				sb.append(" ROUND(SUM(INV_BAL)/10000,2) AS 投資_總計, ");
				sb.append(" ROUND(SUM(INS_BAL)/10000,2) AS 保險_總計, ");
				sb.append(" ROUND(SUM(DEP_BAL)/10000,2) AS 存款_總計 ");
				break;
			case "2":	//投資總計
				sb.append(" ROUND(SUM(INV_FUND)/10000,2) AS 投資_基金, ");
				sb.append(" ROUND(SUM(INV_STOCK)/10000,2) AS 投資_股票, ");
				sb.append(" ROUND(SUM(INV_ETF)/10000,2) AS 投資_ETF, ");
				sb.append(" ROUND(SUM(INV_SI)/10000,2) AS 投資_SI, ");
				sb.append(" ROUND(SUM(INV_SN)/10000,2) AS 投資_SN, ");
				sb.append(" ROUND(SUM(INV_BOND)/10000,2) AS 投資_海外債, ");
				sb.append(" ROUND(SUM(INV_RP)/10000,2) AS 投資_RP複委託, ");
				sb.append(" ROUND(SUM(INV_STRST)/10000,2) AS 投資_指定單獨信託, ");
				sb.append(" ROUND(SUM(INV_GOLD)/10000,2) AS 投資_黃金存摺, ");
				sb.append(" ROUND(SUM(INV_DCI)/10000,2) AS 投資_DCI, ");
				sb.append(" ROUND(SUM(INV_NAMI)/10000,2) AS 投資_奈米投 ");
				break;
			case "3":	//存款總計
				sb.append(" ROUND(SUM(DEP_SAV)/10000,2) AS 存款_台幣活存, ");
				sb.append(" ROUND(SUM(DEP_CD)/10000,2) AS 存款_台幣定存, ");
				sb.append(" ROUND(SUM(DEP_FSAV)/10000,2) AS 存款_外幣活存, ");
				sb.append(" ROUND(SUM(DEP_FCD)/10000,2) AS 存款_外幣定存, ");
				sb.append(" ROUND(SUM(DEP_CSAV)/10000,2) AS 存款_台幣支存 ");
				break;
			case "4":	//保險總計
				sb.append(" ROUND(SUM(INS_IV)/10000,2) AS 保險_投資型, ");
				sb.append(" ROUND(SUM(INS_OT)/10000,2) AS 保險_躉繳, ");
				sb.append(" ROUND(SUM(INS_SY)/10000,2) AS 保險_短年期繳, ");
				sb.append(" ROUND(SUM(INS_LY)/10000,2) AS 保險_長年期繳 ");
				break;
			case "5":
				sb.append(" ROUND(SUM(INV_FUND)/10000,2) AS 投資_基金 ");
				break;
			case "6":
				sb.append(" ROUND(SUM(INV_STOCK)/10000,2) AS 投資_股票 ");
				break;
			case "7":
				sb.append(" ROUND(SUM(INV_ETF)/10000,2) AS 投資_ETF ");
				break;
			case "8":
				sb.append(" ROUND(SUM(INV_SI)/10000,2) AS 投資_SI ");
				break;
			case "9":
				sb.append(" ROUND(SUM(INV_SN)/10000,2) AS 投資_SN ");
				break;
			case "10":
				sb.append(" ROUND(SUM(INV_BOND)/10000,2) AS 投資_海外債 ");
				break;
			case "11":
				sb.append(" ROUND(SUM(INV_RP)/10000,2) AS 投資_RP複委託 ");
				break;
			case "12":
				sb.append(" ROUND(SUM(INV_STRST)/10000,2) AS 投資_指定單獨信託 ");
				break;
			case "13":
				sb.append(" ROUND(SUM(INV_GOLD)/10000,2) AS 投資_黃金存摺 ");
				break;
			case "14":
				sb.append(" ROUND(SUM(INV_DCI)/10000,2) AS 投資_DCI ");
				break;
			case "15":
				sb.append(" ROUND(SUM(INV_NAMI)/10000,2) AS 投資_奈米投 ");
				break;
			case "16":
				sb.append(" ROUND(SUM(DEP_SAV)/10000,2) AS 存款_台幣活存 ");
				break;
			case "17":
				sb.append(" ROUND(SUM(DEP_CD)/10000,2) AS 存款_台幣定存 ");
				break;
			case "18":
				sb.append(" ROUND(SUM(DEP_FSAV)/10000,2) AS 存款_外幣活存 ");
				break;
			case "19":
				sb.append(" ROUND(SUM(DEP_FCD)/10000,2) AS 存款_外幣定存 ");
				break;
			case "20":
				sb.append(" ROUND(SUM(DEP_CSAV)/10000,2) AS 存款_台幣支存 ");
				break;
			case "21":
				sb.append(" ROUND(SUM(INS_IV)/10000,2) AS 保險_投資型 ");
				break;
			case "22":
				sb.append(" ROUND(SUM(INS_OT)/10000,2) AS 保險_躉繳 ");
				break;
			case "23":
				sb.append(" ROUND(SUM(INS_SY)/10000,2) AS 保險_短年期繳 ");
				break;
			case "24":
				sb.append(" ROUND(SUM(INS_LY)/10000,2) AS 保險_長年期繳 ");
				break;
		}		
		sb.append("FROM TBPMS_MONTHLY_AUM ");
		sb.append("WHERE 1 = 1 ");
		
		if (inputVO.getsDate() != null) {
			sb.append("AND YEARMON >= TO_CHAR( :sDate , 'YYYYMM') ");
			queryCondition.setObject("sDate", inputVO.getsDate());
		}
		
		if (inputVO.geteDate() != null) {
			sb.append("AND YEARMON <= TO_CHAR( :eDate , 'YYYYMM') ");
			queryCondition.setObject("eDate", inputVO.geteDate());
		}
		
		if (StringUtils.isNotBlank(inputVO.getEmp_id())) {
			sb.append("AND EMP_ID = :emp_id ");
			queryCondition.setObject("emp_id", inputVO.getEmp_id());
		} else if (StringUtils.isNotBlank(inputVO.getBranch_nbr())) {
			sb.append("AND BRANCH_NBR = :branch_nbr ");
			queryCondition.setObject("branch_nbr", inputVO.getBranch_nbr());
		} else if (StringUtils.isNotBlank(inputVO.getBranch_area_id())) { 
			sb.append("AND BRANCH_AREA_ID = :branch_area_id ");
			queryCondition.setObject("branch_area_id", inputVO.getBranch_area_id());
		} else if (StringUtils.isNotBlank(inputVO.getRegion_center_id())) {
			sb.append("AND REGION_CENTER_ID = :region_center_id ");
			queryCondition.setObject("region_center_id", inputVO.getRegion_center_id());
		}
		
		sb.append("GROUP BY YEARMON ");
		
		queryCondition.setQueryString(sb.toString());
		resultList = dam.exeQuery(queryCondition);
		
		return resultList;
	}
	
	/**
	 * 以組織為X軸統計
	 * @param inputVO
	 * @return
	 * @throws DAOException
	 * @throws JBranchException
	 */
	private List<Map<String,Object>> queryByOrg(PMS901InputVO inputVO) throws DAOException, JBranchException {
		List<Map<String,Object>> resultList = new ArrayList<Map<String,Object>>();	
		
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sb = new StringBuffer();
		if (StringUtils.isBlank(inputVO.getRegion_center_id())) {
			sb.append("SELECT REGION_CENTER_NAME AS ORG_NAME, ");
		} else if (StringUtils.isBlank(inputVO.getBranch_area_id())) { 
			sb.append("SELECT BRANCH_AREA_NAME AS ORG_NAME, ");
		} else if (StringUtils.isBlank(inputVO.getBranch_nbr())) {
			sb.append("SELECT BRANCH_NAME AS ORG_NAME, ");
		} else {
			sb.append("SELECT EMP_NAME AS ORG_NAME, ");
		}
		
		switch (inputVO.getSetupProd()) {
			case "1":	//全商品(投資、存款、保險)總計
				sb.append(" ROUND(SUM(INV_BAL)/10000,2) AS 投資_總計, ");
				sb.append(" ROUND(SUM(INS_BAL)/10000,2) AS 保險_總計, ");
				sb.append(" ROUND(SUM(DEP_BAL)/10000,2) AS 存款_總計 ");
				break;
			case "2":	//投資總計
				sb.append(" ROUND(SUM(INV_FUND)/10000,2) AS 投資_基金, ");
				sb.append(" ROUND(SUM(INV_STOCK)/10000,2) AS 投資_股票, ");
				sb.append(" ROUND(SUM(INV_ETF)/10000,2) AS 投資_ETF, ");
				sb.append(" ROUND(SUM(INV_SI)/10000,2) AS 投資_SI, ");
				sb.append(" ROUND(SUM(INV_SN)/10000,2) AS 投資_SN, ");
				sb.append(" ROUND(SUM(INV_BOND)/10000,2) AS 投資_海外債, ");
				sb.append(" ROUND(SUM(INV_RP)/10000,2) AS 投資_RP複委託, ");
				sb.append(" ROUND(SUM(INV_STRST)/10000,2) AS 投資_指定單獨信託, ");
				sb.append(" ROUND(SUM(INV_GOLD)/10000,2) AS 投資_黃金存摺, ");
				sb.append(" ROUND(SUM(INV_DCI)/10000,2) AS 投資_DCI, ");
				sb.append(" ROUND(SUM(INV_NAMI)/10000,2) AS 投資_奈米投 ");
				break;
			case "3":	//存款總計
				sb.append(" ROUND(SUM(DEP_SAV)/10000,2) AS 存款_台幣活存, ");
				sb.append(" ROUND(SUM(DEP_CD)/10000,2) AS 存款_台幣定存, ");
				sb.append(" ROUND(SUM(DEP_FSAV)/10000,2) AS 存款_外幣活存, ");
				sb.append(" ROUND(SUM(DEP_FCD)/10000,2) AS 存款_外幣定存, ");
				sb.append(" ROUND(SUM(DEP_CSAV)/10000,2) AS 存款_台幣支存 ");
				break;
			case "4":	//保險總計
				sb.append(" ROUND(SUM(INS_IV)/10000,2) AS 保險_投資型, ");
				sb.append(" ROUND(SUM(INS_OT)/10000,2) AS 保險_躉繳, ");
				sb.append(" ROUND(SUM(INS_SY)/10000,2) AS 保險_短年期繳, ");
				sb.append(" ROUND(SUM(INS_LY)/10000,2) AS 保險_長年期繳 ");
				break;
			case "5":
				sb.append(" ROUND(SUM(INV_FUND)/10000,2) AS 投資_基金 ");
				break;
			case "6":
				sb.append(" ROUND(SUM(INV_STOCK)/10000,2) AS 投資_股票 ");
				break;
			case "7":
				sb.append(" ROUND(SUM(INV_ETF)/10000,2) AS 投資_ETF ");
				break;
			case "8":
				sb.append(" ROUND(SUM(INV_SI)/10000,2) AS 投資_SI ");
				break;
			case "9":
				sb.append(" ROUND(SUM(INV_SN)/10000,2) AS 投資_SN ");
				break;
			case "10":
				sb.append(" ROUND(SUM(INV_BOND)/10000,2) AS 投資_海外債 ");
				break;
			case "11":
				sb.append(" ROUND(SUM(INV_RP)/10000,2) AS 投資_RP複委託 ");
				break;
			case "12":
				sb.append(" ROUND(SUM(INV_STRST)/10000,2) AS 投資_指定單獨信託 ");
				break;
			case "13":
				sb.append(" ROUND(SUM(INV_GOLD)/10000,2) AS 投資_黃金存摺 ");
				break;
			case "14":
				sb.append(" ROUND(SUM(INV_DCI)/10000,2) AS 投資_DCI ");
				break;
			case "15":
				sb.append(" ROUND(SUM(INV_NAMI)/10000,2) AS 投資_奈米投 ");
				break;
			case "16":
				sb.append(" ROUND(SUM(DEP_SAV)/10000,2) AS 存款_台幣活存 ");
				break;
			case "17":
				sb.append(" ROUND(SUM(DEP_CD)/10000,2) AS 存款_台幣定存 ");
				break;
			case "18":
				sb.append(" ROUND(SUM(DEP_FSAV)/10000,2) AS 存款_外幣活存 ");
				break;
			case "19":
				sb.append(" ROUND(SUM(DEP_FCD)/10000,2) AS 存款_外幣定存 ");
				break;
			case "20":
				sb.append(" ROUND(SUM(DEP_CSAV)/10000,2) AS 存款_台幣支存 ");
				break;
			case "21":
				sb.append(" ROUND(SUM(INS_IV)/10000,2) AS 保險_投資型 ");
				break;
			case "22":
				sb.append(" ROUND(SUM(INS_OT)/10000,2) AS 保險_躉繳 ");
				break;
			case "23":
				sb.append(" ROUND(SUM(INS_SY)/10000,2) AS 保險_短年期繳 ");
				break;
			case "24":
				sb.append(" ROUND(SUM(INS_LY)/10000,2) AS 保險_長年期繳 ");
				break;
			case "25":
				sb.append(" ROUND(SUM(A_AVG_AUM)/10000,2) AS A板塊, ");
				sb.append(" ROUND(SUM(C_AVG_AUM)/10000,2) AS C板塊 ");
				break;
			case "26":
				sb.append(" ROUND(SUM(CUST_TYPE_PERSON)/10000,2) AS 個人, ");
				sb.append(" ROUND(SUM(CUST_TYPE_CORPOR)/10000,2) AS 法人 ");
				break;
		}		
		sb.append("FROM TBPMS_MONTHLY_AUM ");
		sb.append("WHERE 1 = 1 ");
		
		if (inputVO.getsDate() != null) {
			sb.append("AND YEARMON >= TO_CHAR( :sDate , 'YYYYMM') ");
			queryCondition.setObject("sDate", inputVO.getsDate());
		}
		
		if (inputVO.geteDate() != null) {
			sb.append("AND YEARMON <= TO_CHAR( :eDate , 'YYYYMM') ");
			queryCondition.setObject("eDate", inputVO.geteDate());
		}
		
		if (StringUtils.isNotBlank(inputVO.getEmp_id())) {
			sb.append("AND EMP_ID = :emp_id ");
			queryCondition.setObject("emp_id", inputVO.getEmp_id());
		} else if (StringUtils.isNotBlank(inputVO.getBranch_nbr())) {
			sb.append("AND BRANCH_NBR = :branch_nbr ");
			queryCondition.setObject("branch_nbr", inputVO.getBranch_nbr());
		} else if (StringUtils.isNotBlank(inputVO.getBranch_area_id())) { 
			sb.append("AND BRANCH_AREA_ID = :branch_area_id ");
			queryCondition.setObject("branch_area_id", inputVO.getBranch_area_id());
		} else if (StringUtils.isNotBlank(inputVO.getRegion_center_id())) {
			sb.append("AND REGION_CENTER_ID = :region_center_id ");
			queryCondition.setObject("region_center_id", inputVO.getRegion_center_id());
		}
		
		if (StringUtils.isBlank(inputVO.getRegion_center_id())) {
			sb.append(" GROUP BY REGION_CENTER_NAME ");
		} else if (StringUtils.isBlank(inputVO.getBranch_area_id())) { 
			sb.append(" GROUP BY BRANCH_AREA_NAME ");
		} else if (StringUtils.isBlank(inputVO.getBranch_nbr())) {
			sb.append(" GROUP BY BRANCH_NAME ");
		} else {
			sb.append(" GROUP BY EMP_NAME ");
		}
		
		queryCondition.setQueryString(sb.toString());
		resultList = dam.exeQuery(queryCondition);
		
		return resultList;
	}
}