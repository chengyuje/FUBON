package com.systex.jbranch.app.server.fps.pms715;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.server.fps.pms215.PMS215OutputVO;
import com.systex.jbranch.app.server.fps.pms715.PMS715InputVO;
import com.systex.jbranch.app.server.fps.pms715.PMS715OutputVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
/**
 * Copy Right Information : Forms Syntron <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description : 年度KPI成績查詢Controller<br>
 * Comments Name : PMS715.java<br>
 * Author :zhouyiqiong<br>
 * Date :2017年2月6日 <br>
 * Version : 1.01 <br>
 * Editor : zhouyiqiong<br>
 * Editor Date : 2017年2月6日<br>
 */
@Component("pms715")
@Scope("request")
public class PMS715 extends FubonWmsBizLogic
{
	private DataAccessManager dam = null;
	private PMS715InputVO inputVO = null;
	private Logger logger = LoggerFactory.getLogger(PMS715.class);
	
	/**
	 * 角色獲取
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void getRole(Object body, IPrimitiveMap header)
			throws JBranchException {
		PMS715OutputVO return_VO = new PMS715OutputVO();
		PMS715InputVO inputVO = (PMS715InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam
				.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		try {
			sql.append("  SELECT COUNT(1) AS CNT														");
			sql.append("  FROM TBORG_MEMBER_ROLE Q                                                      ");
			if(StringUtils.isNotBlank((String) getUserVariable(FubonSystemVariableConsts.LOGINID))){
				sql.append(" WHERE Q.EMP_ID = :user 				                                    ");
				queryCondition.setObject("user", (String) getUserVariable(FubonSystemVariableConsts.LOGINID));
			}
			sql.append("        AND EXISTS(SELECT 1                                                     ");
			sql.append("                   FROM TBSYSSECUROLPRIASS S                                    ");
			sql.append("                   INNER JOIN TBSYSSECUPRIFUNMAP T               				");
			sql.append("                         ON T.ITEMID = 'PMS715'          						");
			sql.append("                         AND T.FUNCTIONID = 'maintenance'          				");
			sql.append("                         AND S.PRIVILEGEID = T.PRIVILEGEID						");
			sql.append("                    WHERE S.ROLEID = Q.ROLE_ID)									");
			queryCondition.setQueryString(sql.toString());
			// result
			List roleList = dam.executeQuery(queryCondition);
			return_VO.setRoleList(roleList);
			this.sendRtnObject(return_VO);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}
	
	/**
	 * 角色獲取
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void getOpenFlag(Object body, IPrimitiveMap header)
			throws JBranchException {
		PMS715OutputVO return_VO = new PMS715OutputVO();
		PMS715InputVO inputVO = (PMS715InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam
				.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		try {
			sql.append("  SELECT DISTINCT NVL(T.OPEN_FLAG,0)  AS OPEN_FLAG  ");
			sql.append("  FROM DUAL                                         ");
			sql.append("  LEFT JOIN TBPMS_KPI_YEAR_GRADE T                  ");
			sql.append("       ON 1 = 1                                     ");
			sql.append("       AND T.YEARMON = :yearmon                     ");
			if(!StringUtils.isBlank(inputVO.getsTime())){
				sql.append("       AND T.YEARMON = :yearmon                 ");
				queryCondition.setObject("yearmon", inputVO.getsTime());
			}
			queryCondition.setQueryString(sql.toString());
			// result
			List openFlagList = dam.executeQuery(queryCondition);
			return_VO.setOpenFlagList(openFlagList);
			this.sendRtnObject(return_VO);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}
	
	/**
	 * 查詢角色獲取
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void inquireRole(Object body, IPrimitiveMap header)
			throws JBranchException {
		PMS715InputVO inputVO = (PMS715InputVO) body;
		PMS715OutputVO outputVO = new PMS715OutputVO();
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		ArrayList<String> sql_list = new ArrayList<String>();
		StringBuffer sql = new StringBuffer();		
		try
		{			
			sql.append("  SELECT * FROM (SELECT DISTINCT T.SUB_PROJECT_SEQ_ID,T.SUB_PROJECT_SEQ_NAME          ");
			sql.append("  FROM TBPMS_CNR_KPI_MAST_DETAIL T                                            		  ");
			sql.append("  INNER JOIN TBPMS_CNR_KPI_MAST M                                              		  ");
			sql.append("       ON M.DATA_YEAR = T.DATA_YEAR                                                   ");
			sql.append("  	   AND M.PERSON_TYPE = T.PERSON_TYPE                                              ");
			if (!StringUtils.isBlank(inputVO.getsTime())){
				sql.append(" and to_char(last_day(to_date(:yearMon,'yyyymm')),'yyyymmdd') between m.start_day and m.end_day					    	                          ");
				qc.setObject("yearMon", inputVO.getsTime());
			}			
			sql.append("  UNION ALL                                                                           ");
			sql.append("  SELECT DISTINCT N.SUB_PROJECT_SEQ_ID,N.SUB_PROJECT_SEQ_NAME                         ");
			sql.append("  FROM TBPMS_CNR_KPI_MAST_ADD_DETAIL N                                                ");
			sql.append("  INNER JOIN TBPMS_CNR_KPI_MAST M                                                     ");
			sql.append("        ON M.DATA_YEAR = N.DATA_YEAR                                                  ");
			sql.append("        AND M.PERSON_TYPE = N.PERSON_TYPE                                             ");
			if (!StringUtils.isBlank(inputVO.getsTime())){
				sql.append(" and to_char(last_day(to_date(:yearMon,'yyyymm')),'yyyymmdd') between m.start_day and m.end_day					    	                          ");
				qc.setObject("yearMon", inputVO.getsTime());
			}
			sql.append("  ) order by SUB_PROJECT_SEQ_ID                                                       ");
			qc.setQueryString(sql.toString());
			List<Map<String, Object>> result = dam.exeQuery(qc);
			outputVO.setInquireRoleList(result);
			outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());
			sendRtnObject(outputVO);			
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}
	
	/*
	 * 內部調度使用
	 */
	public List<Map<String, Object>> inquireRole(PMS715InputVO inputVO)
			throws JBranchException {
		PMS715OutputVO outputVO = new PMS715OutputVO();
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		ArrayList<String> sql_list = new ArrayList<String>();
		List<Map<String, Object>> result = null;
		StringBuffer sql = new StringBuffer();		
		try
		{			
			sql.append("  SELECT * FROM (SELECT DISTINCT T.SUB_PROJECT_SEQ_ID,T.SUB_PROJECT_SEQ_NAME          ");
			sql.append("  FROM TBPMS_CNR_KPI_MAST_DETAIL T                                            		  ");
			sql.append("  INNER JOIN TBPMS_CNR_KPI_MAST M                                              		  ");
			sql.append("       ON M.DATA_YEAR = T.DATA_YEAR                                                   ");
			sql.append("  	   AND M.PERSON_TYPE = T.PERSON_TYPE                                              ");
			if (!StringUtils.isBlank(inputVO.getsTime())){
				sql.append(" and to_char(last_day(to_date(:yearMon,'yyyymm')),'yyyymmdd') between m.start_day and m.end_day					    	                          ");
				qc.setObject("yearMon", inputVO.getsTime());
			}			
			sql.append("  UNION ALL                                                                           ");
			sql.append("  SELECT DISTINCT N.SUB_PROJECT_SEQ_ID,N.SUB_PROJECT_SEQ_NAME                         ");
			sql.append("  FROM TBPMS_CNR_KPI_MAST_ADD_DETAIL N                                                ");
			sql.append("  INNER JOIN TBPMS_CNR_KPI_MAST M                                                     ");
			sql.append("        ON M.DATA_YEAR = N.DATA_YEAR                                                  ");
			sql.append("        AND M.PERSON_TYPE = N.PERSON_TYPE                                             ");
			if (!StringUtils.isBlank(inputVO.getsTime())){
				sql.append(" and to_char(last_day(to_date(:yearMon,'yyyymm')),'yyyymmdd') between m.start_day and m.end_day					    	                          ");
				qc.setObject("yearMon", inputVO.getsTime());
			}
			sql.append("  ) order by SUB_PROJECT_SEQ_ID                                                       ");
			qc.setQueryString(sql.toString());
			result = dam.exeQuery(qc);			
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
		return result;
	}
	
	/**
	 * 查詢檔案
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void inquire(Object body, IPrimitiveMap header) throws JBranchException
	{	
		PMS715InputVO inputVO = (PMS715InputVO) body;
		PMS715OutputVO outputVO = new PMS715OutputVO();
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition();
		StringBuffer sql = new StringBuffer();
		String inputSeqId = inputVO.getSubProjectSeqId();
		String[] seqArray = inputSeqId.split(",");		
		try
		{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			sql.append("  SELECT OPEN_FLAG,                                              ");
			sql.append("         ROW_NUMBER() OVER(ORDER BY REGION_CENTER_ID, BRANCH_AREA_ID,BRANCH_NBR,PERSON_TYPE,T.EMP_ID) AS NUM,");
			sql.append("  	     T.YEARMON,                                              ");
			sql.append("  	     REGION_CENTER_ID,                                       ");
			sql.append("  	     REGION_CENTER_NAME,                                     ");
			sql.append("  	     BRANCH_AREA_ID,                                         ");
			sql.append("  	     BRANCH_AREA_NAME,                                       ");
			sql.append("  	     BRANCH_NBR,                                             ");
			sql.append("  	     BRANCH_NAME,                                            ");
			sql.append("  	     T.EMP_ID,                                               ");
			sql.append("  	     EMP_NAME,                                               ");
			sql.append("  	     AO_CODE,                                                ");
			sql.append("  	     PERSON_GROUP,                                           ");
			sql.append("  	     PERSON_TYPE,                                            ");
			sql.append("  	     PA.PARAM_DESC  AS PERSON_TYPE_NAME,                     ");
			sql.append("  	     CNT_MON,                                                ");
			sql.append("  	     DUTY_CHANGE                                             ");
			for(int i=0 ; i<seqArray.length ; i++){
				sql.append( " ,REAL_" + seqArray[i] 									  );
				sql.append( " ,TARGET_" + seqArray[i]								      );
				sql.append( " ,RATE_" + seqArray[i] 									  );
				sql.append( " ,SCORE_" + seqArray[i]                                      );
			}
			sql.append("  	     ,ROUND(ORI_SCORE,2) AS ORI_SCORE,                       ");
			sql.append("  	     ROUND(ALL_SCORE,2) AS ALL_SCORE,                      	 ");
			sql.append("  	     ROUND(ADDAFT_SCORE,2) AS ADDAFT_SCORE,                  ");
			sql.append("  	     ROUND(NEW_SOCRE,2) AS NEW_SOCRE,                        ");
			sql.append("  	     ROUND(LAST_YEAR_SCORE,2) AS LAST_YEAR_SCORE,            ");
			sql.append("  	     ROUND(TOTAL_SCORE,2) AS TOTAL_SCORE,                    ");
			sql.append("  	     RANKING,                                                ");
			sql.append("  	     ROUND(LEV_AVG_SCORE,2) AS LEV_AVG_SCORE,                ");
			sql.append("  	     ROUND(FULL_AVG_SCORE,2) AS FULL_AVG_SCORE,              ");
			sql.append("  	     T.VERSION,                                              ");
			sql.append("  	     T.CREATETIME,                                           ");
			sql.append("  	     T.CREATOR,                                              ");
			sql.append("  	     T.MODIFIER,                                             ");
			sql.append("  	     T.LASTUPDATE                                            ");
			sql.append("  FROM TBPMS_KPI_YEAR_GRADE T                                    ");
			sql.append("  LEFT JOIN TBSYSPARAMETER PA                                    ");
			sql.append("       ON PA.PARAM_TYPE = 'PMS.PERSON_TYPE'                      ");
			sql.append("       AND PA.PARAM_CODE = T.PERSON_TYPE                         ");			
			sql.append("  LEFT JOIN (SELECT EMP_ID										 ");
			sql.append("                   ,YEARMON										 ");
			for(int i=0 ; i<seqArray.length ; i++){
				sql.append( " ,NVL(SUM(CASE WHEN SUB_PROJECT_SEQ_ID = " + seqArray[i] 
						   + " THEN ROUND(REAL_VALUE,2) ELSE 0 END ),0) AS REAL_" + seqArray[i]   );
				sql.append( " ,NVL(SUM(CASE WHEN SUB_PROJECT_SEQ_ID = " + seqArray[i] 
				           + " THEN ROUND(TARGET_VALUE,2) ELSE 0 END ),0) AS TARGET_" + seqArray[i]);
				sql.append( " ,NVL(SUM(CASE WHEN SUB_PROJECT_SEQ_ID = " + seqArray[i] 
				           + " THEN ROUND(RATE_VALUE*100,2) ELSE 0 END ),0)||'%' AS RATE_" + seqArray[i]   );
				sql.append( " ,NVL(SUM(CASE WHEN SUB_PROJECT_SEQ_ID = " + seqArray[i] 
				           + " THEN ROUND(SCORE_VALUE,2) ELSE 0 END ),0) AS SCORE_" + seqArray[i] );
			}
			sql.append("  FROM TBPMS_KPI_YEAR_GRADE_DETAIL S                             ");
			sql.append("  GROUP BY S.EMP_ID,S.YEARMON)T1                                 ");
			sql.append("       ON T1.YEARMON = T.YEARMON                                 ");
			sql.append("       AND T1.EMP_ID = T.EMP_ID                                  ");
			sql.append("      WHERE 1 = 1                                                ");
			if(!"1".equals(inputVO.getRole())){
				sql.append("  AND OPEN_FLAG = 1                                          ");
			}
			if (!StringUtils.isBlank(inputVO.getsTime())) {
				sql.append(" AND TRIM(T.YEARMON) = '" + inputVO.getsTime() + "'");
			}
			if (!StringUtils.isBlank(inputVO.getRegion_center_id())) {
				sql.append(" AND REGION_CENTER_ID = '" + inputVO.getRegion_center_id() + "'");
			}
			if (!StringUtils.isBlank(inputVO.getBranch_area_id())) {
				sql.append(" AND BRANCH_AREA_ID = '" + inputVO.getBranch_area_id() + "'");
			}
			if (!StringUtils.isBlank(inputVO.getBranch_nbr())) {
				sql.append(" AND BRANCH_NBR = '" + inputVO.getBranch_nbr() + "'");
			}
			if (!StringUtils.isBlank(inputVO.getEmp_id())){
				sql.append(" AND T.EMP_ID = '" + inputVO.getEmp_id() + "'");
			}			
			sql.append("  order by REGION_CENTER_ID,BRANCH_AREA_ID,BRANCH_NBR,PERSON_TYPE,T.EMP_ID    ");
			queryCondition.setQueryString(sql.toString());			
			ResultIF list = dam.executePaging(queryCondition, inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
			int totalPage_i = list.getTotalPage(); // 分頁用
			int totalRecord_i = list.getTotalRecord(); // 分頁用
			outputVO.setResultList(list); // data
			outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());// 當前頁次
			outputVO.setTotalPage(totalPage_i);// 總頁次
			outputVO.setTotalRecord(totalRecord_i);// 總筆數
			this.sendRtnObject(outputVO);
		}
		catch (Exception e)
		{
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}
	
	/**
	 * 儲存
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void saveChange(Object body, IPrimitiveMap header) throws JBranchException
	{
		final PMS715InputVO inputVO = (PMS715InputVO) body;
		final PMS715OutputVO outputVO = new PMS715OutputVO();
		dam = this.getDataAccessManager();
		final QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		new Thread() {
			@Override
			public void run() {
				StringBuffer sb = new StringBuffer();
				sb.append(" CALL PABTH_BTPMS319.SP_EXEC_KPI_SELECT(?,?,?,?,?) ");
				qc.setString(1, inputVO.getsTime());  //計算年月
				qc.setString(2, inputVO.getOpenFlag());
				qc.setString(3, inputVO.getYearMonOp());    //查詢年月
				qc.setString(4, inputVO.getExecFlag());    //查詢年月
				qc.registerOutParameter(5, Types.VARCHAR);
				qc.setQueryString(sb.toString());
				try {
					Map<Integer, Object> resultMap = dam.executeCallable(qc);
					String resultStr = (String) resultMap.get(5);
					outputVO.setErrorMessage(resultStr);
					sendRtnObject(outputVO);
				} catch (DAOException e) {
					e.printStackTrace();
				} catch (JBranchException e) {
					e.printStackTrace();
				}
			}					
		}.start();
	}
	
	/**
	 * 匯出EXCLE
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void export(Object body, IPrimitiveMap header) throws JBranchException {
		PMS715InputVO inputVO = (PMS715InputVO) body;
		PMS715OutputVO outputVO = new PMS715OutputVO();
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition();
		StringBuffer sql = new StringBuffer();
		String inputSeqId = inputVO.getSubProjectSeqId();
		String[] seqArray = inputSeqId.split(",");		
		try
		{
			sql.append("  SELECT OPEN_FLAG,                                              ");
			sql.append("         ROW_NUMBER() OVER(ORDER BY REGION_CENTER_ID, BRANCH_AREA_ID,BRANCH_NBR,PERSON_TYPE,T.EMP_ID) AS NUM,");
			sql.append("  	     T.YEARMON,                                              ");
			sql.append("  	     REGION_CENTER_ID,                                       ");
			sql.append("  	     REGION_CENTER_NAME,                                     ");
			sql.append("  	     BRANCH_AREA_ID,                                         ");
			sql.append("  	     BRANCH_AREA_NAME,                                       ");
			sql.append("  	     BRANCH_NBR,                                             ");
			sql.append("  	     BRANCH_NAME,                                            ");
			sql.append("  	     T.EMP_ID,                                               ");
			sql.append("  	     EMP_NAME,                                               ");
			sql.append("  	     AO_CODE,                                                ");
			sql.append("  	     PERSON_GROUP,                                           ");
			sql.append("  	     PERSON_TYPE,                                            ");
			sql.append("  	     PA.PARAM_DESC  AS PERSON_TYPE_NAME,                     ");
			sql.append("  	     CNT_MON,                                                ");
			sql.append("  	     DUTY_CHANGE                                             ");
			for(int i=0 ; i<seqArray.length ; i++){
				sql.append( " ,REAL_" + seqArray[i] 									  );
				sql.append( " ,TARGET_" + seqArray[i]								      );
				sql.append( " ,RATE_" + seqArray[i] 									  );
				sql.append( " ,SCORE_" + seqArray[i]                                      );
			}
			sql.append("  	     ,ROUND(ORI_SCORE,2) AS ORI_SCORE,                       ");
			sql.append("  	     ROUND(ALL_SCORE,2) AS ALL_SCORE,                      	 ");
			sql.append("  	     ROUND(ADDAFT_SCORE,2) AS ADDAFT_SCORE,                  ");
			sql.append("  	     ROUND(NEW_SOCRE,2) AS NEW_SOCRE,                        ");
			sql.append("  	     ROUND(LAST_YEAR_SCORE,2) AS LAST_YEAR_SCORE,            ");
			sql.append("  	     ROUND(TOTAL_SCORE,2) AS TOTAL_SCORE,                    ");
			sql.append("  	     RANKING,                                                ");
			sql.append("  	     ROUND(LEV_AVG_SCORE,2) AS LEV_AVG_SCORE,                ");
			sql.append("  	     ROUND(FULL_AVG_SCORE,2) AS FULL_AVG_SCORE,              ");
			sql.append("  	     T.VERSION,                                              ");
			sql.append("  	     T.CREATETIME,                                           ");
			sql.append("  	     T.CREATOR,                                              ");
			sql.append("  	     T.MODIFIER,                                             ");
			sql.append("  	     T.LASTUPDATE                                            ");
			sql.append("  FROM TBPMS_KPI_YEAR_GRADE T                                    ");
			sql.append("  LEFT JOIN TBSYSPARAMETER PA                                    ");
			sql.append("       ON PA.PARAM_TYPE = 'PMS.PERSON_TYPE'                      ");
			sql.append("       AND PA.PARAM_CODE = T.PERSON_TYPE                         ");			
			sql.append("  LEFT JOIN (SELECT EMP_ID										 ");
			sql.append("                   ,YEARMON										 ");
			for(int i=0 ; i<seqArray.length ; i++){
				sql.append( " ,NVL(SUM(CASE WHEN SUB_PROJECT_SEQ_ID = " + seqArray[i] 
						   + " THEN ROUND(REAL_VALUE,2) ELSE 0 END ),0) AS REAL_" + seqArray[i]   );
				sql.append( " ,NVL(SUM(CASE WHEN SUB_PROJECT_SEQ_ID = " + seqArray[i] 
				           + " THEN ROUND(TARGET_VALUE,2) ELSE 0 END ),0) AS TARGET_" + seqArray[i]);
				sql.append( " ,NVL(SUM(CASE WHEN SUB_PROJECT_SEQ_ID = " + seqArray[i] 
				           + " THEN ROUND(RATE_VALUE*100,2) ELSE 0 END ),0)||'%' AS RATE_" + seqArray[i]   );
				sql.append( " ,NVL(SUM(CASE WHEN SUB_PROJECT_SEQ_ID = " + seqArray[i] 
				           + " THEN ROUND(SCORE_VALUE,2) ELSE 0 END ),0) AS SCORE_" + seqArray[i] );
			}
			sql.append("  FROM TBPMS_KPI_YEAR_GRADE_DETAIL S                             ");
			sql.append("  GROUP BY S.EMP_ID,S.YEARMON)T1                                 ");
			sql.append("       ON T1.YEARMON = T.YEARMON                                 ");
			sql.append("       AND T1.EMP_ID = T.EMP_ID                                  ");
			sql.append("      WHERE 1 = 1                                                ");
			if(!"1".equals(inputVO.getRole())){
				sql.append("  AND OPEN_FLAG = 1                                          ");
			}
			if (!StringUtils.isBlank(inputVO.getsTime())) {
				sql.append(" AND TRIM(T.YEARMON) = '" + inputVO.getsTime() + "'");
			}
			if (!StringUtils.isBlank(inputVO.getRegion_center_id())) {
				sql.append(" AND REGION_CENTER_ID = '" + inputVO.getRegion_center_id() + "'");
			}
			if (!StringUtils.isBlank(inputVO.getBranch_area_id())) {
				sql.append(" AND BRANCH_AREA_ID = '" + inputVO.getBranch_area_id() + "'");
			}
			if (!StringUtils.isBlank(inputVO.getBranch_nbr())) {
				sql.append(" AND BRANCH_NBR = '" + inputVO.getBranch_nbr() + "'");
			}
			if (!StringUtils.isBlank(inputVO.getEmp_id())){
				sql.append(" AND T.EMP_ID = '" + inputVO.getEmp_id() + "'");
			}			
			sql.append("  order by REGION_CENTER_ID,BRANCH_AREA_ID,BRANCH_NBR,PERSON_TYPE,T.EMP_ID    ");
			queryCondition.setQueryString(sql.toString());			
			List<Map<String, Object>> list = dam.exeQuery(queryCondition);
			List<Map<String, Object>> listHead = this.inquireRole(inputVO);
			if(list.size() > 0){
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH:mm:ss");
				String fileName = "年度KPI成績查詢_" + sdf.format(new Date()) + ".csv"; 
				List listCSV =  new ArrayList();
				int len = 28 + 4*listHead.size();
				for(Map<String, Object> map : list){
					String[] records = new String[len];
					int i = 0;
					records[i] = checkIsNull(map, "NUM");  						    //序號
					records[++i] = checkIsNull(map, "YEARMON");  					//資料年月
					records[++i] = checkIsNull(map, "REGION_CENTER_ID");  		    //區域中心
					records[++i] = checkIsNull(map, "REGION_CENTER_NAME");  		//區域中心name
					records[++i] = checkIsNull(map, "BRANCH_AREA_ID");			    //營運區
					records[++i] = checkIsNull(map, "BRANCH_AREA_NAME");			//營運區	name
					records[++i] = checkIsNull(map, "BRANCH_NBR");					//分行代碼
					records[++i] = checkIsNull(map, "BRANCH_NAME");					//分行別		
					records[++i] = checkIsNullAndTrans(map, "EMP_ID");				//員工編號
					records[++i] = checkIsNull(map, "EMP_NAME");					//專員姓名
					records[++i] = checkIsNull(map, "PERSON_GROUP");				//人員類別
					records[++i] = checkIsNull(map, "PERSON_TYPE_NAME");			//人員類別
					records[++i] = checkIsNull(map, "CNT_MON");						//計績月數
					records[++i] = checkIsNull(map, "DUTY_CHANGE");					//職務異動註記
					//動態
					for(int k = 0;k<listHead.size();k++){
						String index = listHead.get(k).get("SUB_PROJECT_SEQ_ID").toString();
						records[++i] = checkIsNull(map, "REAL_"+index);
						records[++i] = checkIsNull(map, "TARGET_"+index);
						records[++i] = checkIsNull(map, "RATE_"+index);
						records[++i] = checkIsNull(map, "SCORE_"+index);
					}
					//records[++i] = checkIsNull(map, "ALL_SCORE");					//考核總分
					records[++i] = checkIsNull(map, "ORI_SCORE");					//原始分數	
					records[++i] = checkIsNull(map, "OTHER_SCORE");					//其他加扣分
					records[++i] = checkIsNull(map, "ADDAFT_SCORE");				//加權后分數
					records[++i] = checkIsNull(map, "NEW_SOCRE");					//最後分數	(100%還原)
					records[++i] = checkIsNull(map, "LAST_YEAR_SCORE");				//去年12月成績
					records[++i] = checkIsNull(map, "TOTAL_SCORE");					//結算分數
					records[++i] = checkIsNullAndTrans(map, "RANKING");				//全行排名		
					records[++i] = checkIsNull(map, "LEV_AVG_SCORE");				//各級平均分數
					records[++i] = checkIsNull(map, "FULL_AVG_SCORE");				//全行平均分數
					listCSV.add(records);
				}
				//header
				String[] csvHeader = new String[len];
				int j = 0;
				csvHeader[j] = "項次";
				csvHeader[++j] = "資料年月";
				csvHeader[++j] = "業務處代碼";
				csvHeader[++j] = "業務處";
				csvHeader[++j] = "營運區代碼";
				csvHeader[++j] = "營運區";
				csvHeader[++j] = "分行代碼";
				csvHeader[++j] = "分行別";
				csvHeader[++j] = "員工編號";
				csvHeader[++j] = "員工姓名";
				csvHeader[++j] = "組別";
				csvHeader[++j] = "人員類別";
				csvHeader[++j] = "計績月數";
				csvHeader[++j] = "職務異動註記";
				//動態表頭
				for(int k = 0;k<listHead.size();k++){
					csvHeader[++j] = listHead.get(k).get("SUB_PROJECT_SEQ_NAME")+"實際數";
					csvHeader[++j] = listHead.get(k).get("SUB_PROJECT_SEQ_NAME")+"目標數";
					csvHeader[++j] = listHead.get(k).get("SUB_PROJECT_SEQ_NAME")+"達成率";
					csvHeader[++j] = listHead.get(k).get("SUB_PROJECT_SEQ_NAME")+"得分";
				}
				//公共
				//csvHeader[++j] = "考核總分";
				csvHeader[++j] = "原始分數";
				csvHeader[++j] = "其他加扣分";
				csvHeader[++j] = "加權後分數";
				csvHeader[++j] = "最後分數(100%還原)";
				csvHeader[++j] = "去年12月成績";
				csvHeader[++j] = "結算分數";
				csvHeader[++j] = "全行排名";
				csvHeader[++j] = "各級平均分數";
				csvHeader[++j] = "全行平均分數";
				CSVUtil csv = new CSVUtil();
				csv.setHeader(csvHeader);
				csv.addRecordList(listCSV);
				String url = csv.generateCSV();
				notifyClientToDownloadFile(url, fileName); //download
			} else {
				this.sendRtnObject(null);
		    }
		}
		catch (Exception e)
		{
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}
	/**
	* 檢查Map取出欄位是否為Null
	* 
	* @param map
	* @return String
	*/
	private String checkIsNull(Map map, String key) {
		if(StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key) != null){
			return String.valueOf(map.get(key));
		}else{
			return "";
		}
	}
	/**
	 * 處理貨幣格式
	 * @param map
	 * @param key
	 * @return
	 */
	private String currencyFormat(Map map, String key){		
		if(StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key) != null){
			NumberFormat nf = NumberFormat.getCurrencyInstance();
			return nf.format(map.get(key));										
		}else
			return "$0.00";		
	}
	
	/**
	* 檢查Map取出欄位是否為Null  用於理專以及員編
	* @param map
	* @return String
	*/
	private String checkIsNullAndTrans(Map map, String key) 
	{
		if(StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key) != null){
			return String.valueOf("=\""+map.get(key)+"\"");
		}else{
			return "";
		}
	}
}