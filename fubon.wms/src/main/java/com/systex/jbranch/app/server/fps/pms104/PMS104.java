package com.systex.jbranch.app.server.fps.pms104;

import java.text.SimpleDateFormat;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * Copy Right Information : <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description :<br>
 * Comments Name : PMS104.java<br>
 * Author : Chia-Li<br>
 * Date :2016年09月29日 <br>
 * Version : 1.0 <br>
 * Editor : KevinHsu<br>
 * Editor Date : 2017年01月12日<br>
 */
@Component("pms104")
@Scope("request")
public class PMS104 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(PMS104.class);

	public void getDetailInv(Object body, IPrimitiveMap header) throws JBranchException {
		PMS104InputVO inputVO = (PMS104InputVO) body;
		PMS104OutputVO return_VO = new PMS104OutputVO();
		dam = this.getDataAccessManager();

		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		try {
			StringBuffer sql = new StringBuffer();
			sql.append("SELECT PRD_TYPE,PRD_DTL,CRCY_TYPE,AMT_ORGD,AMT_NTD FROM TBPMS_CF_USE_ANLS_DTL_INV WHERE YEARMON = :cyYearmon AND CUST_ID= :custID");
			queryCondition.setObject("cyYearmon", inputVO.getReportDate());
			queryCondition.setObject("custID", inputVO.getCustID());
			queryCondition.setQueryString(sql.toString());
			// result
			return_VO.setResultList(dam.exeQuery(queryCondition));
		
			this.sendRtnObject(return_VO);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}

	
	public void getDetailIns(Object body, IPrimitiveMap header) throws JBranchException {
		PMS104InputVO inputVO = (PMS104InputVO) body;
		PMS104OutputVO return_VO = new PMS104OutputVO();
		dam = this.getDataAccessManager();

		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		try {
			StringBuffer sql = new StringBuffer();
			sql.append("SELECT INS_NBR,INS_NAME,TER_FEE_YEAR,POLICY_NO,SEQ,ACUM_PAID_ORGD,EFFECT_DATE FROM TBPMS_CF_USE_ANLS_DTL_INS WHERE YEARMON = :cyYearmon AND CUST_ID= :custID");
			queryCondition.setObject("cyYearmon", inputVO.getReportDate());
			queryCondition.setObject("custID", inputVO.getCustID());
			queryCondition.setQueryString(sql.toString());
			
			// result
			return_VO.setResultList(dam.exeQuery(queryCondition));
			this.sendRtnObject(return_VO);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}
	
	
	private String checkIsNull(Map map, String key) {
		if (StringUtils.isNotBlank(String.valueOf(map.get(key)))) {
			return String.valueOf(map.get(key));
		} else {
			return "";
		}
	}

	/**
	 * 查詢檔案
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */

	public void inquire(Object body, IPrimitiveMap header) throws JBranchException {
		PMS104InputVO inputVO = (PMS104InputVO) body;
		PMS104OutputVO outputVO = new PMS104OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			sql.append("SELECT ROWNUM AS NUM,T.REGION_CENTER_ID,T.REGION_CENTER_NAME,T.BRANCH_AREA_ID,T.BRANCH_AREA_NAME,T.BRANCH_NBR,T.BRANCH_NAME,T.AO_CODE,T.EMP_ID,T.EMP_NAME,");
			sql.append("T.CUST_ID,T.CUST_NAME,T.DUE_AMT,PRD_CNT,T.TOTAL_AMT,T.CF_USAGE_RATE,T.YEARMON,T.CREATETIME  ");
			sql.append("FROM (");
			sql.append("SELECT REGION_CENTER_ID,REGION_CENTER_NAME,BRANCH_AREA_ID,BRANCH_AREA_NAME,BRANCH_NBR,BRANCH_NAME,AO_CODE,EMP_ID,EMP_NAME,CUST_ID,CUST_NAME,DUE_AMT,PRD_CNT,TOTAL_AMT,CF_USAGE_RATE,YEARMON,CREATETIME ");
			sql.append("FROM TBPMS_CF_USE_ANLS_MAST) T ");
			// 應抓最新的組織去對應 問題單:4064 by 2017/12/15 willis
			sql.append("LEFT JOIN VWORG_DEFN_INFO ORG ");
			sql.append("ON ORG.BRANCH_NBR=T.BRANCH_NBR ");
			
			sql.append("WHERE ROWNUM<=2000 ");
			if (!StringUtils.isBlank(inputVO.getRegion_center_id())) {
				sql.append(" AND ORG.REGION_CENTER_ID = :regionCenter");
				condition.setObject("regionCenter", inputVO.getRegion_center_id());
			}
			if (!StringUtils.isBlank(inputVO.getBranch_area_id())) {
				sql.append(" AND ORG.BRANCH_AREA_ID = :branchArea");
				condition.setObject("branchArea", inputVO.getBranch_area_id());
			}
			if (!StringUtils.isBlank(inputVO.getBranch_nbr())) {
				sql.append(" AND ORG.BRANCH_NBR = :branchNbr");
				condition.setObject("branchNbr", inputVO.getBranch_nbr());
			}
			if (!StringUtils.isBlank(inputVO.getAo_code())) {
				sql.append(" AND T.AO_CODE = :ao_code");
				condition.setObject("ao_code", inputVO.getAo_code());
			}
			if (!StringUtils.isBlank(inputVO.getReportDate())) {
				sql.append(" AND T.YEARMON = :yearMon");
				condition.setObject("yearMon", inputVO.getReportDate());
			}
			condition.setQueryString(sql.toString());
			ResultIF list = dam.executePaging(condition,inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
			int totalPage_i = list.getTotalPage(); // 分頁用
			int totalRecord_i = list.getTotalRecord(); // 分頁用
			outputVO.setResultList(list); // data
			outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());// 當前頁次
			outputVO.setTotalPage(totalPage_i);// 總頁次
			outputVO.setTotalRecord(totalRecord_i);// 總筆數
			this.sendRtnObject(outputVO);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}
	
	
	/**
	 * 拿總計
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */

	public void inquire2(Object body, IPrimitiveMap header) throws JBranchException {
		PMS104InputVO inputVO = (PMS104InputVO) body;
		PMS104OutputVO outputVO = new PMS104OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			sql.append("SELECT SUM(DUE_AMT) AS DDUE_AMT,SUM(PRD_CNT) AS DPRD_CNT,SUM(TOTAL_AMT) AS DTOTAL_AMT ");
			sql.append("  ");
			sql.append(" ");
			sql.append(" ");
			sql.append("FROM TBPMS_CF_USE_ANLS_MAST MAST ");
			// 應抓最新的組織去對應 問題單:4064 by 2017/12/15 willis
			sql.append("LEFT JOIN VWORG_DEFN_INFO ORG ");
			sql.append("ON ORG.BRANCH_NBR=MAST.BRANCH_NBR ");
			sql.append(" WHERE ROWNUM<=2000 ");
			if (!StringUtils.isBlank(inputVO.getRegion_center_id())) {
				sql.append(" AND ORG.REGION_CENTER_ID = :regionCenter");
				condition.setObject("regionCenter", inputVO.getRegion_center_id());
			}
			if (!StringUtils.isBlank(inputVO.getBranch_area_id())) {
				sql.append(" AND ORG.BRANCH_AREA_ID = :branchArea");
				condition.setObject("branchArea", inputVO.getBranch_area_id());
			}
			if (!StringUtils.isBlank(inputVO.getBranch_nbr())) {
				sql.append(" AND ORG.BRANCH_NBR = :branchNbr");
				condition.setObject("branchNbr", inputVO.getBranch_nbr());
			}
			if (!StringUtils.isBlank(inputVO.getAo_code())) {
				sql.append(" AND MAST.AO_CODE = :ao_code");
				condition.setObject("ao_code", inputVO.getAo_code());
			}
			if (!StringUtils.isBlank(inputVO.getReportDate())) {
				sql.append(" AND MAST.YEARMON = :yearMon");
				condition.setObject("yearMon", inputVO.getReportDate());
			}
			condition.setQueryString(sql.toString());
			ResultIF list = dam.executePaging(condition,inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
			int totalPage_i = list.getTotalPage(); // 分頁用
			int totalRecord_i = list.getTotalRecord(); // 分頁用
			outputVO.setResultList2(list); // data
			outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());// 當前頁次
			outputVO.setTotalPage(totalPage_i);// 總頁次
			outputVO.setTotalRecord(totalRecord_i);// 總筆數
			this.sendRtnObject(outputVO);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}


}