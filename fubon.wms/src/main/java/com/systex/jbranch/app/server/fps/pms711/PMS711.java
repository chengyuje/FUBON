package com.systex.jbranch.app.server.fps.pms711;
import java.io.File;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jxl.Sheet;
import jxl.Workbook;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.server.fps.pms711.PMS711InputVO;
import com.systex.jbranch.app.server.fps.pms711.PMS711OutputVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;
/**
 * 
 * Copy Right Information : Forms Syntron <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.6.0 <br>
 * Description : <br>
 * Comments Name : PMS711.java<br>
 * Author :zyq<br>
 * Date :2016年12月12日 <br>
 * Version : 1.01 <br>
 * Editor : zyq<br>
 * Editor Date : 2016年12月12日<br>
 */
@Component("pms711")
@Scope("request")
public class PMS711 extends FubonWmsBizLogic
{
	public DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(PMS711.class);
	/**
	 * 查詢（主表）
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void queryData(Object body, IPrimitiveMap header) throws JBranchException
	{
		PMS711InputVO inputVO = (PMS711InputVO) body;
		PMS711OutputVO outputVO = new PMS711OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("  SELECT T.DATA_YEAR,                                ");
		sql.append("  	     T.PERSON_TYPE,                              ");
		sql.append("  	     S.PARAM_NAME  AS PERSON_TYPE_NAME,          ");
		sql.append("  	     T.START_DAY,							   	 ");
		sql.append("  	     T.END_DAY, 								 ");
		sql.append("  	     T.FULL_SCORE,                               ");
		sql.append("  	     T.VERSION,                                  ");
		sql.append("  	     T.CREATETIME,                               ");
		sql.append("  	     T.CREATOR,                                  ");
		sql.append("  	     T.MODIFIER,                                 ");
		sql.append("  	     T.LASTUPDATE                                ");
		sql.append("  FROM TBPMS_CNR_KPI_MAST T                          ");
		sql.append("  LEFT JOIN TBSYSPARAMETER S                         ");
		sql.append("       ON S.PARAM_TYPE = 'PMS.PERSON_TYPE'           ");
		sql.append("       AND S.PARAM_CODE = T.PERSON_TYPE              ");
		
		sql.append("  WHERE 1=1                                          ");
		if(StringUtils.isNotBlank(inputVO.getDate_year())){
			sql.append("  AND T.DATA_YEAR = :YEAR                        ");
			qc.setObject("YEAR", inputVO.getDate_year());
		}
		if(StringUtils.isNotBlank(inputVO.getDate_year())){
			sql.append("  AND T.PERSON_TYPE = :personType                ");
			qc.setObject("personType", inputVO.getPersonType());
		}
		qc.setQueryString(sql.toString());
		List<Map<String, Object>> result = dam.exeQuery(qc);
		outputVO.setShowList(result);
		sendRtnObject(outputVO);
	}
	
	/**
	 * 儲存主頁面的修改（主表）
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void saveChange(Object body, IPrimitiveMap header) throws JBranchException, ParseException
	{
		PMS711InputVO inputVO = (PMS711InputVO) body;
		PMS711OutputVO outputVO = new PMS711OutputVO();
		dam = this.getDataAccessManager();
		//修改主界面信息
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		sb.append("  UPDATE TBPMS_CNR_KPI_MAST SET                        ");
		sb.append("         START_DAY  = :START_DAY,                      ");
		sb.append("         END_DAY    = :END_DAY                        ");
		sb.append("    WHERE DATA_YEAR = :dataYear                        ");
		qc.setObject("START_DAY", inputVO.getSDate1());
		qc.setObject("END_DAY", inputVO.getEDate1());
		qc.setObject("dataYear", inputVO.getDate_year());
		qc.setQueryString(sb.toString());
		dam.exeUpdate(qc);
		
		//修改FULL_SCORE
		sb.setLength(0);
		QueryConditionIF qcScore = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb.append("  UPDATE TBPMS_CNR_KPI_MAST SET                        ");
		sb.append("         FULL_SCORE = :FULL_SCORE,                     ");
		sb.append("         SCORE_VALUE = :SCORE_VALUE                    ");
		sb.append("    WHERE DATA_YEAR = :dataYear                        ");
		sb.append("    AND PERSON_TYPE = :PERSON_TYPE                     ");
		if(inputVO.getFullScore()==null || "".equals(inputVO.getFullScore())){
			qcScore.setObject("FULL_SCORE", "");
		}else{
			qcScore.setObject("FULL_SCORE",inputVO.getFullScore());
		}
		qcScore.setObject("dataYear", inputVO.getDate_year());
		qcScore.setObject("PERSON_TYPE", inputVO.getPersonType());
		qcScore.setObject("SCORE_VALUE", inputVO.getScoreValue());
		qcScore.setQueryString(sb.toString());
		dam.exeUpdate(qcScore);
		
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		try {
			sql.append("  UPDATE TBPMS_CNR_KPI_MAST_DETAIL SET                 ");
			sql.append("   		BRANCHS = :BRANCHS,                            ");
			sql.append("   		SELECT_FLAG = :SELECT_FLAG,                    ");
			sql.append("  		UP_RATE     = :UP_RATE,                        ");
			sql.append("  		KPI_SCORE   = :KPI_SCORE,                      ");
			sql.append("  		STAT_RATE   = :STAT_RATE,                      ");
			sql.append("  	    VERSION = 1,                                   ");
			sql.append("  	    MODIFIER = :MODIFIER,                          ");
			sql.append("  	    LASTUPDATE = SYSDATE                           ");
			sql.append("    WHERE DATA_YEAR = :dataYear                        ");
			sql.append("    AND PERSON_TYPE = :PERSON_TYPE                     ");
			sql.append("    AND IS_SPECIAL  = :IS_SPECIAL                      ");
			
			if(inputVO.getBranchs()!=null)
			{
				condition.setObject("BRANCHS", inputVO.getBranchs().trim());
			}else
			{
				condition.setObject("BRANCHS", inputVO.getBranchs());
			}
			condition.setObject("dataYear", inputVO.getDate_year());
			condition.setObject("PERSON_TYPE", inputVO.getPersonType());
			condition.setObject("IS_SPECIAL", inputVO.getIsSpecial());		
			condition.setObject("MODIFIER", (String) getUserVariable(FubonSystemVariableConsts.LOGINID));
			for (int i = 0; i < inputVO.getStatsList().size(); i++)
			{
				condition.setObject("SELECT_FLAG", inputVO.getStatsList().get(i).get("SELECT_FLAG"));
				if(inputVO.getStatsList().get(i).get("UP_RATE")==null || "".equals(inputVO.getStatsList().get(i).get("UP_RATE"))){
					condition.setObject("UP_RATE", "");
				}else{
					condition.setObject("UP_RATE",  Float.parseFloat(String.valueOf(inputVO.getStatsList().get(i).get("UP_RATE")))/100);
				}
				if(inputVO.getStatsList().get(i).get("KPI_SCORE")==null){
					condition.setObject("KPI_SCORE", "");
				}else{
					condition.setObject("KPI_SCORE", inputVO.getStatsList().get(i).get("KPI_SCORE"));
				}
				if(inputVO.getStatsList().get(i).get("STAT_RATE")==null || "".equals(inputVO.getStatsList().get(i).get("STAT_RATE"))){
					condition.setObject("STAT_RATE", "");
				}else{
					condition.setObject("STAT_RATE", Float.parseFloat(String.valueOf(inputVO.getStatsList().get(i).get("STAT_RATE")))/100);
				}
				sql.append("    AND SUB_PROJECT_SEQ_ID = :SUB_PROJECT_SEQ_ID   ");
				condition.setObject("SUB_PROJECT_SEQ_ID", inputVO.getStatsList().get(i).get("SUB_PROJECT_SEQ_ID"));
				condition.setQueryString(sql.toString());
				dam.exeUpdate(condition);
			}
			sendRtnObject(outputVO);
		}
		catch (Exception e)
		{
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員"+e.getMessage());
		}
	}
	
	/**
	 * 查詢KPI項目
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void queryExamStats(Object body, IPrimitiveMap header) throws JBranchException
	{
		PMS711InputVO inputVO = (PMS711InputVO) body;
		PMS711OutputVO outputVO = new PMS711OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("  SELECT DATA_YEAR,                                                    ");
		sql.append("  	     PERSON_TYPE,                                                  ");
		sql.append("  	     BRANCHS,                                                      ");
		sql.append("  	     SELECT_FLAG,                                                  ");
		sql.append("  	     EXAM_STATS,                                                   ");
		sql.append("  	     KPI_PROJECT,                                                  ");
		sql.append("  	     LPAD(SUB_PROJECT_SEQ_ID,2,'0') AS SUB_PROJECT_SEQ_ID,         ");
		sql.append("  	     SUB_PROJECT_SEQ_NAME,                                         ");
		sql.append("  	     STAT_FLAG,                                                    ");
		sql.append("  	     UP_RATE*100 AS UP_RATE,                                       ");
		sql.append("  	     KPI_SCORE,                                                    ");
		sql.append("  	     STAT_RATE*100 AS STAT_RATE,                                   ");
		sql.append("  	     URL,                                    					   ");
		sql.append("  	     CLASS_NAME                                    				   ");
		sql.append("  FROM TBPMS_CNR_KPI_MAST_DETAIL                                       ");
		sql.append("  WHERE 1=1                                                            ");
		if(StringUtils.isNotBlank(inputVO.getDate_year())){
			sql.append("  AND DATA_YEAR = :YEAR                                            ");
			qc.setObject("YEAR", inputVO.getDate_year());
		}
		if(StringUtils.isNotBlank(inputVO.getPersonType())){
			sql.append("  AND PERSON_TYPE = :personType                                    ");
			qc.setObject("personType", inputVO.getPersonType());
		}
		if(StringUtils.isNotBlank(inputVO.getIsSpecial())){
			sql.append("  AND IS_SPECIAL = :isSpecial                                      ");
			qc.setObject("isSpecial", inputVO.getIsSpecial());
		sql.append(" ORDER BY SUB_PROJECT_SEQ_ID");
		qc.setQueryString(sql.toString());
		List<Map<String, Object>> result = dam.exeQuery(qc);
		outputVO.setStatsList(result);
		sendRtnObject(outputVO);
		}
	}
	/**
	 * 查詢非系統計算人工上傳項目
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void queryAdd(Object body, IPrimitiveMap header) throws JBranchException
	{
		PMS711InputVO inputVO = (PMS711InputVO) body;
		PMS711OutputVO outputVO = new PMS711OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("  SELECT DATA_YEAR,                                                    ");
		sql.append("  	     PERSON_TYPE,                                                  ");
		sql.append("  	     SELECT_FLAG,                                                  ");
		sql.append("  	     EXAM_STATS,                                                   ");
		sql.append("  	     KPI_PROJECT,                                                  ");
		sql.append("  	     LPAD(SUB_PROJECT_SEQ_ID,2,'0') AS SUB_PROJECT_SEQ_ID,         ");
		sql.append("  	     SUB_PROJECT_SEQ_NAME,                                         ");
		sql.append("  	     STAT_FLAG,                                                    ");
		sql.append("  	     UP_RATE*100 AS UP_RATE,                                       ");
		sql.append("  	     KPI_SCORE,                                                    ");
		sql.append("  	     STAT_RATE*100 AS STAT_RATE                                    ");
		sql.append("  FROM TBPMS_CNR_KPI_MAST_ADD_DETAIL                                   ");
		sql.append("  WHERE 1=1                                                            ");
		if(StringUtils.isNotBlank(inputVO.getDate_year())){
			sql.append("  AND DATA_YEAR = :YEAR                                            ");
			qc.setObject("YEAR", inputVO.getDate_year());
		}
		if(StringUtils.isNotBlank(inputVO.getPersonType())){
			sql.append("  AND PERSON_TYPE = :personType                                    ");
			qc.setObject("personType", inputVO.getPersonType());
		}
		sql.append(" ORDER BY SUB_PROJECT_SEQ_ID");
		qc.setQueryString(sql.toString());
		List<Map<String, Object>> result = dam.exeQuery(qc);
		outputVO.setAddToList(result);
		sendRtnObject(outputVO);
	}
	/**
	 * 查詢KPI期間設定計績期間初始值
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void queryKpiDay(Object body, IPrimitiveMap header) throws JBranchException
	{
		PMS711InputVO inputVO = (PMS711InputVO) body;
		PMS711OutputVO outputVO = new PMS711OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("  SELECT DATA_YEAR,                                ");
		sql.append("  	     PERSON_TYPE,                              ");
		sql.append("  	     START_DAY,                                ");
		sql.append("  	     END_DAY,                                  ");
		sql.append("  	     FULL_SCORE                                ");
		sql.append("  FROM TBPMS_CNR_KPI_MAST                          ");
		sql.append("  WHERE 1=1                                        ");
		if(StringUtils.isNotBlank(inputVO.getDate_year())){
			sql.append("  AND DATA_YEAR = :YEAR                        ");
			qc.setObject("YEAR", inputVO.getDate_year());
		}
		if(StringUtils.isNotBlank(inputVO.getDate_year())){
			sql.append("  AND  PERSON_TYPE = :personType               ");
			qc.setObject("personType", inputVO.getPersonType());
		}
		qc.setQueryString(sql.toString());
		List<Map<String, Object>> result = dam.exeQuery(qc);
		outputVO.setKpiDayList(result);
		sendRtnObject(outputVO);
	}
	//新增KPI項目
	public void addKpi(Object body, IPrimitiveMap header) throws APException
	{
		try{
			PMS711InputVO inputVO = (PMS711InputVO) body;
			PMS711OutputVO outputVO = new PMS711OutputVO();
			dam = this.getDataAccessManager();
			QueryConditionIF qcDel = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sbDel = new StringBuffer();					
			sbDel.append("  DELETE FROM TBPMS_CNR_KPI_MAST_ADD_DETAIL  ");
			sbDel.append("  WHERE DATA_YEAR =:DATA_YEAR 		       ");
			sbDel.append("   AND PERSON_TYPE =:PERSON_TYPE 		       ");
			qcDel.setObject("DATA_YEAR", inputVO.getDate_year());
			qcDel.setObject("PERSON_TYPE", inputVO.getPersonType());
			qcDel.setQueryString(sbDel.toString());
			dam.exeUpdate(qcDel);
			QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sb = new StringBuffer();						//執行插入語句
			sb.append("  	INSERT INTO TBPMS_CNR_KPI_MAST_ADD_DETAIL(       ");
			sb.append("		    DATA_YEAR,	                                 ");
			sb.append("  		PERSON_TYPE,           					     ");
			sb.append("  		SELECT_FLAG,            					 ");
			sb.append("  		EXAM_STATS,            						 ");
			sb.append("  		KPI_PROJECT,            				     ");
			sb.append("  		SUB_PROJECT_SEQ_ID,            		         ");
			sb.append("  		SUB_PROJECT_SEQ_NAME,            			 ");
			sb.append("  		STAT_FLAG,            						 ");
			sb.append("  		UP_RATE,            						 ");
			sb.append("  		KPI_SCORE,            						 ");
			sb.append("  		STAT_RATE,            						 ");
			sb.append("  		VERSION,           						     ");
			sb.append("  		CREATETIME,             					 ");
			sb.append("  		CREATOR,             						 ");
			sb.append("  		MODIFIER,         						     ");
			sb.append("  		LASTUPDATE )             					 ");
			sb.append("  	VALUES(:DATA_YEAR,            				     ");
			sb.append("  		:PERSON_TYPE,             				     ");
			sb.append("  		:SELECT_FLAG,             				 	 ");
			sb.append("  		:EXAM_STATS,            					 ");
			sb.append("  		:KPI_PROJECT,            					 ");
			sb.append("  		:SUB_PROJECT_SEQ_ID,            		     ");
			sb.append("  		:SUB_PROJECT_SEQ_NAME,            		     ");
			sb.append("  		:STAT_FLAG,            					 	 ");
			sb.append("  		:UP_RATE,            					     ");
			sb.append("  		:KPI_SCORE,            					 	 ");
			sb.append("  		:STAT_RATE,            					 	 ");
			sb.append("  		0,           					    		 ");
			sb.append("  		sysdate,           				    		 ");
			sb.append("  		:userId,            					     ");
			sb.append("  		:userId,         					   	     ");
			sb.append("  		sysdate)          				    		 ");
			/*將bsGoalTarList的內容插入數據庫*/
			for(int i=0; i < inputVO.getAddToList().size(); i++){
				qc.setObject("DATA_YEAR", inputVO.getDate_year());
				qc.setObject("PERSON_TYPE", inputVO.getPersonType());
				qc.setObject("SELECT_FLAG", inputVO.getAddToList().get(i).get("SELECT_FLAG"));
				qc.setObject("EXAM_STATS", inputVO.getAddToList().get(i).get("EXAM_STATS"));
				qc.setObject("KPI_PROJECT", inputVO.getAddToList().get(i).get("KPI_PROJECT"));
				qc.setObject("SUB_PROJECT_SEQ_ID", inputVO.getAddToList().get(i).get("SUB_PROJECT_SEQ_ID"));
				qc.setObject("SUB_PROJECT_SEQ_NAME", inputVO.getAddToList().get(i).get("SUB_PROJECT_SEQ_NAME"));
				qc.setObject("STAT_FLAG", inputVO.getAddToList().get(i).get("STAT_FLAG"));
				if(inputVO.getAddToList().get(i).get("UP_RATE")==null || "".equals(inputVO.getAddToList().get(i).get("UP_RATE"))){
					qc.setObject("UP_RATE", "");
				}else{
					qc.setObject("UP_RATE",  Float.parseFloat(String.valueOf(inputVO.getAddToList().get(i).get("UP_RATE")))/100);
				}
				if(inputVO.getAddToList().get(i).get("KPI_SCORE")==null){
					qc.setObject("KPI_SCORE", "");
				}else{
					qc.setObject("KPI_SCORE", inputVO.getAddToList().get(i).get("KPI_SCORE"));
				}
				if(inputVO.getAddToList().get(i).get("STAT_RATE")==null || "".equals(inputVO.getAddToList().get(i).get("STAT_RATE"))){
					qc.setObject("STAT_RATE", "");
				}else{
					qc.setObject("STAT_RATE", Float.parseFloat(String.valueOf(inputVO.getAddToList().get(i).get("STAT_RATE")))/100);
				}
				qc.setObject("userId", (String) getUserVariable(FubonSystemVariableConsts.LOGINID));
				qc.setQueryString(sb.toString());
				dam.exeUpdate(qc);
			}
			QueryConditionIF qcAdd = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sbAdd = new StringBuffer();					
			sbAdd.append("  MERGE INTO TBPMS_CNR_KPI_MAST_ADD_DETAIL MU                                                                      ");
			sbAdd.append("  USING(SELECT T.*,S.PARAM_CODE                                                                                    ");
			sbAdd.append("        FROM TBPMS_CNR_KPI_MAST_ADD_DETAIL T                                                                       ");
			sbAdd.append("        LEFT JOIN TBSYSPARAMETER S                                                                                 ");
			sbAdd.append("              ON S.PARAM_TYPE = 'PMS.PERSON_TYPE'                                                                  ");
			sbAdd.append("        WHERE DATA_YEAR =:DATA_YEAR                                                                                ");
			sbAdd.append("              AND PERSON_TYPE =:PERSON_TYPE)SO                                                                     ");
			sbAdd.append("        ON (MU.DATA_YEAR = SO.DATA_YEAR AND MU.PERSON_TYPE = SO.PARAM_CODE AND MU.SUB_PROJECT_SEQ_ID = SO.SUB_PROJECT_SEQ_ID )  ");
			sbAdd.append("  WHEN NOT MATCHED THEN                                                                                            ");
			sbAdd.append("       INSERT (DATA_YEAR,PERSON_TYPE,SELECT_FLAG,EXAM_STATS,KPI_PROJECT,SUB_PROJECT_SEQ_ID,SUB_PROJECT_SEQ_NAME    ");
			sbAdd.append("              ,STAT_FLAG,UP_RATE,KPI_SCORE,STAT_RATE,VERSION,CREATETIME,CREATOR,MODIFIER,LASTUPDATE)               ");
			sbAdd.append("       VALUES(SO.DATA_YEAR,SO.PARAM_CODE,'0',SO.EXAM_STATS                                                         ");
			sbAdd.append("             ,SO.KPI_PROJECT,SO.SUB_PROJECT_SEQ_ID,SO.SUB_PROJECT_SEQ_NAME                                         ");
			sbAdd.append("             ,SO.STAT_FLAG,1,NULL,NULL,SO.VERSION                                                               ");
			sbAdd.append("             ,SO.CREATETIME,SO.CREATOR,SO.MODIFIER,SO.LASTUPDATE)                                                  ");
			qcAdd.setObject("DATA_YEAR", inputVO.getDate_year());
			qcAdd.setObject("PERSON_TYPE", inputVO.getPersonType());
			qcAdd.setQueryString(sbAdd.toString());
			dam.exeUpdate(qcAdd);
			outputVO.setAddToList(null);
			sendRtnObject(outputVO);
			}catch (Exception e) {
				logger.error("更新失敗");
				throw new APException("更新失敗,系統發生錯誤請洽系統管理員"+e.getMessage());
			}
	}
	
	// 修改主表中为已设定
		public void changeMastSet(String column,String dataYear,String userId,String personType,String isSpecial,String subId) throws JBranchException
		{
				dam = this.getDataAccessManager();
				QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				StringBuffer sb = new StringBuffer();
				sb.append("		UPDATE TBPMS_CNR_KPI_MAST_DETAIL T 					");
				sb.append("		SET T."+column+" = '1',                             ");
				sb.append("			T.VERSION = 1,                                  ");
				sb.append("         MODIFIER = :MODIFIER,                           ");
				sb.append("         LASTUPDATE = SYSDATE                            ");
				sb.append("		WHERE T.DATA_YEAR = :DATA_YEAR 					    ");
				sb.append("		  AND T.PERSON_TYPE = :PERSON_TYPE 					");
				sb.append("		  AND T.SUB_PROJECT_SEQ_ID = :SUB_ID 			    ");
				qc.setObject("DATA_YEAR", dataYear);
				qc.setObject("MODIFIER", userId);
				qc.setObject("PERSON_TYPE", personType);
				qc.setObject("SUB_ID",Integer.parseInt(subId));
				qc.setQueryString(sb.toString());
				dam.exeUpdate(qc);
		}
		
		/**
		 * 查詢存投保收益目標達成率(FC-FCH-PS子項目2)
		  * @param body
		  * @param header
		  * @throws JBranchException
		  */
			@SuppressWarnings({ "rawtypes", "unchecked" })
		public void queryPd02(Object body, IPrimitiveMap header) throws JBranchException
		{
			PMS711InputVO inputVO = (PMS711InputVO) body;
			PMS711OutputVO outputVO = new PMS711OutputVO();
			StringBuffer sb = new StringBuffer();
			dam = this.getDataAccessManager();
			try
			{
				QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sb.append(" SELECT                                        ");
			    sb.append("        DATE_YEAR,                             ");
				sb.append("        PERSON_TYPE,                           ");
				sb.append("        AO_JOB_RANK,                           ");
				sb.append("        M_GOAL                                ");
				sb.append(" FROM TBPMS_KPI_TB_INCOME_REACH_RATE           ");
				sb.append(" WHERE DATE_YEAR = :date_year                  ");
				sb.append(" AND PERSON_TYPE = :personType                 ");
				sb.append("ORDER BY AO_JOB_RANK desc                      ");
				condition.setObject("date_year", inputVO.getDate_year());
				condition.setObject("personType", inputVO.getPersonType());
				condition.setQueryString(sb.toString());
				List<Map<String,Object>> resultList = dam.exeQuery(condition);
				outputVO.setShowList(resultList);
				this.sendRtnObject(outputVO);
				
			}
			catch (Exception e)
			{
				logger.error(String.format("發生錯誤:%s",StringUtil.getStackTraceAsString(e)));
				throw new APException("系統發生錯誤請洽系統管理員");
			}
		}
			
	/**
	 * 存儲存投保收益達成率(FC-FCH-ps子項目2)
	 * @param body
	 * @param header
	 *  @throws JBranchException
	 */
	public void savePd02(Object body, IPrimitiveMap header) throws JBranchException
	{
		PMS711InputVO inputVO = (PMS711InputVO) body;
		PMS711OutputVO outputVO = new PMS711OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		try
		{
			sb.append("	  	UPDATE TBPMS_KPI_TB_INCOME_REACH_RATE SET       "); 
			//動態添加MONTH1，MONTH2...
			/*Set<String> valumnSet = inputVO.getColumnMap().keySet();
			for(String key : valumnSet){
				String valumn = inputVO.getColumnMap().get(key);
				sb.append( valumn + " =  :" + valumn + "  ,"); 
			}*/
			
			sb.append("		   M_GOAL  = :M_GOAL,                           ");
			sb.append("		   VERSION = 1,                                 ");
			sb.append("	  	   MODIFIER = :userId,         	                "); 
			sb.append("	  	   LASTUPDATE = sysdate        	                "); 
			sb.append("	  	WHERE DATE_YEAR = :DATE_YEAR      	            "); 
			sb.append("	  	AND PERSON_TYPE = :personType                   "); 
			sb.append("	  	AND AO_JOB_RANK = :AO_JOB_RANK                  "); 
			qc.setObject("personType", inputVO.getPersonType());
			qc.setObject("DATE_YEAR", inputVO.getDate_year());
			for(int i=0; i<inputVO.getShowList().size(); i++){
				/*for(String key : valumnSet){
					String valumn = inputVO.getColumnMap().get(key);
					qc.setObject(valumn, inputVO.getShowList().get(i).get(valumn).toString().replace(".0",""));
				}*/
				qc.setObject("M_GOAL", inputVO.getShowList().get(i).get("M_GOAL"));
				qc.setObject("userId", (String) getUserVariable(FubonSystemVariableConsts.LOGINID));
				qc.setObject("AO_JOB_RANK", inputVO.getShowList().get(i).get("AO_JOB_RANK"));
				qc.setQueryString(sb.toString());
				dam.exeUpdate(qc);
			}
			//更新成功
			changeMastSet("EXAM_STATS",inputVO.getDate_year(),inputVO.getUserId(),inputVO.getPersonType(),inputVO.getIsSpecial(),inputVO.getSubProjectSeqId());
			this.sendRtnObject(outputVO);
		}
		catch (Exception e)
		{
		logger.error(String.format("發生錯誤:%s",StringUtil.getStackTraceAsString(e)));
		throw new APException("系統發生錯誤請洽系統管理員");
		}
	}
	
	/**
	 * 查詢存投保收益達成率(FC-FCH-PS子項目1)
	  * @param body
	  * @param header
	  * @throws JBranchException
	  */
		@SuppressWarnings({ "rawtypes", "unchecked" })
	public void queryPdGoal(Object body, IPrimitiveMap header) throws JBranchException
	{
		PMS711InputVO inputVO = (PMS711InputVO) body;
		PMS711OutputVO outputVO = new PMS711OutputVO();
		StringBuffer sb = new StringBuffer();
		dam = this.getDataAccessManager();
		try
		{
			QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb.append(" SELECT                                ");
			sb.append(" 	DATE_YEAR,                        ");
			sb.append(" 	AO_JOB_RANK,                      ");
			sb.append(" 	M_GOAL,                      	  ");
			sb.append(" 	NT_LIVE1,                         ");
			sb.append(" 	FOR_DEPOSIT1,                     ");
			sb.append(" 	FOR_LIVE2,                        ");
			sb.append(" 	FOR_DEPOSIT2,                     ");
			sb.append(" 	VERSION                           ");
			sb.append(" FROM TBPMS_KPI_PD_CTS_GOAL            ");
			sb.append(" WHERE DATE_YEAR = :date_year          ");
			sb.append(" AND PERSON_TYPE = :personType         ");
			sb.append("ORDER BY AO_JOB_RANK desc              ");
			condition.setObject("date_year", inputVO.getDate_year());
			condition.setObject("personType", inputVO.getPersonType());
			condition.setQueryString(sb.toString());
			List<Map<String,Object>> resultList = dam.exeQuery(condition);
			outputVO.setShowList(resultList);
			this.sendRtnObject(outputVO);
		}
		catch (Exception e)
		{
			logger.error(String.format("發生錯誤:%s",StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}
	/**
	 * 存儲存投保收益達成率(FC-FCH-ps子項目1)
	 * @param body
	 * @param header
	 *  @throws JBranchException
	 */
	public void savePdGoal(Object body, IPrimitiveMap header) throws JBranchException
	{
		PMS711InputVO inputVO = (PMS711InputVO) body;
		PMS711OutputVO outputVO = new PMS711OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		try
		{
			sb.append("	  	UPDATE TBPMS_KPI_PD_CTS_GOAL SET       "); 
		    sb.append("           M_GOAL       = :M_GOAL,          "); 
		    sb.append("           NT_LIVE1     = :NT_LIVE1,        "); 
		    sb.append("           FOR_DEPOSIT1 = :FOR_DEPOSIT1,    "); 
		    sb.append("           FOR_LIVE2    = :FOR_LIVE2,       "); 
		    sb.append("           FOR_DEPOSIT2 = :FOR_DEPOSIT2,    "); 
			sb.append("			  VERSION = 1,                     ");
			sb.append("	  		  MODIFIER = :userId,         	   "); 
			sb.append("	  		  LASTUPDATE = sysdate        	   "); 
			sb.append("	  	WHERE DATE_YEAR = :DATE_YEAR      	   "); 
			sb.append("	  	AND PERSON_TYPE = :personType          "); 
			sb.append("	  	AND AO_JOB_RANK = :AO_JOB_RANK         "); 
			qc.setObject("personType", inputVO.getPersonType());
			qc.setObject("DATE_YEAR", inputVO.getDate_year());
			for(int i=0; i<inputVO.getShowList().size(); i++){
				qc.setObject("M_GOAL", inputVO.getShowList().get(i).get("M_GOAL").toString().replace(".0",""));
				qc.setObject("NT_LIVE1", inputVO.getShowList().get(i).get("NT_LIVE1").toString().replace(".0",""));
				qc.setObject("FOR_DEPOSIT1", inputVO.getShowList().get(i).get("FOR_DEPOSIT1").toString().replace(".0",""));
				qc.setObject("FOR_LIVE2", inputVO.getShowList().get(i).get("FOR_LIVE2").toString().replace(".0",""));
				qc.setObject("FOR_DEPOSIT2", inputVO.getShowList().get(i).get("FOR_DEPOSIT2").toString().replace(".0",""));
				qc.setObject("userId", (String) getUserVariable(FubonSystemVariableConsts.LOGINID));
				qc.setObject("AO_JOB_RANK", inputVO.getShowList().get(i).get("AO_JOB_RANK"));
				qc.setQueryString(sb.toString());
				dam.exeUpdate(qc);
			}
			//更新成功
			changeMastSet("EXAM_STATS",inputVO.getDate_year(),inputVO.getUserId(),inputVO.getPersonType(),inputVO.getIsSpecial(),inputVO.getSubProjectSeqId());
			this.sendRtnObject(outputVO);
		}
		catch (Exception e)
		{
		logger.error(String.format("發生錯誤:%s",StringUtil.getStackTraceAsString(e)));
		throw new APException("系統發生錯誤請洽系統管理員");
		}
	}
	//KPI指標設定06
	/**
	  * 查詢子項目FC-FCH06
	  * @param body
	  * @param header
	  * @throws JBranchException
	  */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void queryPdAum(Object body, IPrimitiveMap header) throws JBranchException {
			try
			{
				PMS711InputVO inputVO = (PMS711InputVO) body;
				PMS711OutputVO outputVO = new PMS711OutputVO();
				dam = this.getDataAccessManager();
				QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);			
				StringBuffer sb = new StringBuffer();
				sb.append("  SELECT PD_TYPE,                	 ");
				sb.append("         D_GOAL,                	     ");
				sb.append("         M_GOAL                	     ");
				sb.append("  FROM TBPMS_KPI_PD_AUM		   	     ");
				sb.append("  WHERE DATE_YEAR = :DATE_YEAR		 ");
				sb.append("  AND PERSON_TYPE = :PERSON_TYPE      ");
				sb.append("  ORDER BY PD_TYPE DESC               ");
				qc.setObject("DATE_YEAR", inputVO.getDate_year()  );
				qc.setObject("PERSON_TYPE", inputVO.getPersonType());
				qc.setQueryString(sb.toString()                   );
				List<Map<String, Object>> result = dam.exeQuery(qc);
				outputVO.setShowList(result);
				sendRtnObject(outputVO);
			}
			catch (Exception e)
			{
				logger.error(String.format("發生錯誤:%s",StringUtil.getStackTraceAsString(e)));
				throw new APException(e.getMessage());
			}
	}
	 /**
	  * 更新子項目FC-FCH06
	  * @param body
	  * @param header
	  * @throws JBranchException
	  */
	public void savePdAum(Object body, IPrimitiveMap header) throws JBranchException {
		try{
		PMS711InputVO inputVO = (PMS711InputVO) body;
		PMS711OutputVO outputVO = new PMS711OutputVO();
		for(int i=0; i < inputVO.getShowList().size(); i++){
			dam = this.getDataAccessManager();
			QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sb = new StringBuffer();					
			if(inputVO.getShowList().get(i).get("PD_TYPE").toString().equals("FCH"))
			{
				sb.append("  UPDATE  TBPMS_KPI_PD_AUM	                             ");
				sb.append("   SET    M_GOAL = :M_GOAL,                               ");
				sb.append("  	    VERSION = 1,                                     ");
				sb.append("  	    MODIFIER = :MODIFIER,                            ");
				sb.append("  	    LASTUPDATE = SYSDATE                             ");
				sb.append("  WHERE   DATE_YEAR = :DATE_YEAR          				 ");
				sb.append("  AND   PERSON_TYPE = :PERSON_TYPE          				 ");
				sb.append("  AND    PD_TYPE = :PD_TYPE          				     ");
				qc.setObject("DATE_YEAR", inputVO.getDate_year()                      );
				qc.setObject("PERSON_TYPE", inputVO.getPersonType()                   );
				qc.setObject("M_GOAL", inputVO.getShowList().get(i).get("M_GOAL")     );
				qc.setObject("MODIFIER", (String) getUserVariable(FubonSystemVariableConsts.LOGINID)                        );
				qc.setObject("PD_TYPE", inputVO.getShowList().get(i).get("PD_TYPE") );
				qc.setQueryString(sb.toString());
			}else
			{
				sb.append(" UPDATE  TBPMS_KPI_PD_AUM	                            ");
				sb.append("  SET    D_GOAL = :D_GOAL,                               ");
				sb.append("  	    VERSION = 1,                                    ");
				sb.append("  	    MODIFIER = :MODIFIER,                           ");
				sb.append("  	    LASTUPDATE = SYSDATE                            ");
				sb.append("  WHERE  DATE_YEAR = :DATE_YEAR          				");
				sb.append("  AND    PD_TYPE = :PD_TYPE          				    ");
				sb.append("  AND  PERSON_TYPE = :PERSON_TYPE          			    ");
				qc.setObject("DATE_YEAR", inputVO.getDate_year()                     );
				qc.setObject("PERSON_TYPE", inputVO.getPersonType()                  );
				qc.setObject("D_GOAL", inputVO.getShowList().get(i).get("D_GOAL")    );
				qc.setObject("MODIFIER", inputVO.getUserId()                         );
				qc.setObject("PD_TYPE", inputVO.getShowList().get(i).get("PD_TYPE")  );
				qc.setQueryString(sb.toString());
			}
			dam.exeUpdate(qc);
		}
		//更新成功
		changeMastSet("EXAM_STATS",inputVO.getDate_year(),inputVO.getUserId(),inputVO.getPersonType(),inputVO.getIsSpecial(),inputVO.getSubProjectSeqId());
		sendRtnObject(outputVO);
		}catch (Exception e) {
			logger.error("更新失敗");
			throw new APException(e.getMessage());
		}
	}
	/**
	 * 理財業務專員-一般分行設定-子項目FC-FCH07
	 * 進入台幣定存減量子頁面
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void queryTwdDec(Object body, IPrimitiveMap header) throws JBranchException
	{	
		PMS711InputVO inputVO = (PMS711InputVO) body;
		PMS711OutputVO outputVO = new PMS711OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		try
		{		
			StringBuffer sb = new StringBuffer();
			sb.append("  SELECT START_RATE,                	 ");
			sb.append("         END_RATE,                	 ");
			sb.append("         M_GOAL                	     ");
			sb.append("  FROM TBPMS_KPI_TWD_DEC	             ");
			sb.append("  WHERE DATE_YEAR = :dateYear		 ");
			sb.append(" AND    PERSON_TYPE = :PERSON_TYPE    ");
			qc.setObject("dateYear", inputVO.getDate_year()   );
			qc.setObject("PERSON_TYPE", inputVO.getPersonType());
			qc.setQueryString(sb.toString()                   );
			List<Map<String, Object>> result = dam.exeQuery(qc);
			outputVO.setShowList(result);
			sendRtnObject(outputVO);
		}
		catch (Exception e)
		{
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}
	/**
	 * 理財業務專員-一般分行設定-子項目FC-FCH07
	 * 儲存台幣定存減量子頁面修改
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void saveTwdDec(Object body, IPrimitiveMap header) throws JBranchException
	{
		try{
			PMS711InputVO inputVO = (PMS711InputVO) body;
			PMS711OutputVO outputVO = new PMS711OutputVO();
			//刪除已存在數據
			dam = this.getDataAccessManager();
			QueryConditionIF dqc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);	
			StringBuffer dsb = new StringBuffer();
			dsb.append("  DELETE TBPMS_KPI_TWD_DEC               ");
			dsb.append("  WHERE DATE_YEAR = :DATE_YEAR           ");
			dqc.setObject("DATE_YEAR", inputVO.getDate_year()     );
			dqc.setQueryString(dsb.toString());
			dam.exeUpdate(dqc);
			
			for(int i=0; i < inputVO.getShowList().size(); i++){
			dam = this.getDataAccessManager();
			QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sb = new StringBuffer();
			sb.append("   INSERT INTO TBPMS_KPI_TWD_DEC (DATE_YEAR,	         ");
			sb.append("  		PERSON_TYPE,           					     ");
			sb.append("  		START_RATE,            				         ");
			sb.append("  		END_RATE,            				         ");
			sb.append("  		M_GOAL,            					         ");
			sb.append("  		VERSION,            						 ");
			sb.append("  		CREATETIME,             					 ");
			sb.append("  		CREATOR,             						 ");
			sb.append("  		MODIFIER,         						     ");
			sb.append("  		LASTUPDATE )             					 ");
			sb.append("  	VALUES(:DATE_YEAR,            				     ");
			sb.append("  		:PERSON_TYPE,             					 ");
			sb.append("  		:START_RATE,             				     ");
			sb.append("  		:END_RATE,             				         ");
			sb.append("  		:M_GOAL,             					     ");
			sb.append("  		:VERSION,           					     ");
			sb.append("  		SYSDATE,           				             ");
			sb.append("  		:CREATOR,            					     ");
			sb.append("  		:MODIFIER,         					         ");
			sb.append("  		SYSDATE)          				             ");
			qc.setObject("DATE_YEAR", inputVO.getDate_year());
			qc.setObject("PERSON_TYPE", "1");
			qc.setObject("START_RATE", inputVO.getShowList().get(i).get("START_RATE"));
			qc.setObject("END_RATE", inputVO.getShowList().get(i).get("END_RATE"));
			qc.setObject("M_GOAL", inputVO.getShowList().get(i).get("M_GOAL"));
			qc.setObject("VERSION", "0");
			qc.setObject("CREATOR", (String) getUserVariable(FubonSystemVariableConsts.LOGINID));
			qc.setObject("MODIFIER", (String) getUserVariable(FubonSystemVariableConsts.LOGINID));
			qc.setQueryString(sb.toString());
			dam.exeUpdate(qc);
			
			QueryConditionIF qc1 = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sb1 = new StringBuffer();
			sb1.append("   INSERT INTO TBPMS_KPI_TWD_DEC (DATE_YEAR,	         ");
			sb1.append("  		PERSON_TYPE,           					     ");
			sb1.append("  		START_RATE,            				         ");
			sb1.append("  		END_RATE,            				         ");
			sb1.append("  		M_GOAL,            					         ");
			sb1.append("  		VERSION,            						 ");
			sb1.append("  		CREATETIME,             					 ");
			sb1.append("  		CREATOR,             						 ");
			sb1.append("  		MODIFIER,         						     ");
			sb1.append("  		LASTUPDATE )             					 ");
			sb1.append("  	VALUES(:DATE_YEAR,            				     ");
			sb1.append("  		:PERSON_TYPE,             					 ");
			sb1.append("  		:START_RATE,             				     ");
			sb1.append("  		:END_RATE,             				         ");
			sb1.append("  		:M_GOAL,             					     ");
			sb1.append("  		:VERSION,           					     ");
			sb1.append("  		SYSDATE,           				             ");
			sb1.append("  		:CREATOR,            					     ");
			sb1.append("  		:MODIFIER,         					         ");
			sb1.append("  		SYSDATE)          				             ");
			qc1.setObject("DATE_YEAR", inputVO.getDate_year());
			qc1.setObject("PERSON_TYPE", "2");
			qc1.setObject("START_RATE", inputVO.getShowList().get(i).get("START_RATE"));
			qc1.setObject("END_RATE", inputVO.getShowList().get(i).get("END_RATE"));
			qc1.setObject("M_GOAL", inputVO.getShowList().get(i).get("M_GOAL"));
			qc1.setObject("VERSION", "0");
			qc1.setObject("CREATOR", (String) getUserVariable(FubonSystemVariableConsts.LOGINID));
			qc1.setObject("MODIFIER", (String) getUserVariable(FubonSystemVariableConsts.LOGINID));
			qc1.setQueryString(sb.toString());
			dam.exeUpdate(qc1);
			}
			changeMastSet("EXAM_STATS",inputVO.getDate_year(),inputVO.getUserId(),"1",inputVO.getIsSpecial(),inputVO.getSubProjectSeqId());
			changeMastSet("EXAM_STATS",inputVO.getDate_year(),inputVO.getUserId(),"2",inputVO.getIsSpecial(),inputVO.getSubProjectSeqId());
			sendRtnObject(outputVO);
			}catch (Exception e) {
				logger.error("更新失敗");
				throw new APException(e.getMessage());
			}
	}
	/**
	 * 理財業務專員-一般分行設定-子項目FC-FCH08
	 * 進入基金銷量目標子頁面
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void queryFundSalesGoal(Object body, IPrimitiveMap header) throws JBranchException
	{
		PMS711InputVO inputVO = (PMS711InputVO) body;
		PMS711OutputVO outputVO = new PMS711OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		StringBuffer sql = new StringBuffer();
		try
		{
			sql.append("  SELECT DATE_YEAR,                          ");
			sql.append("         AO_JOB_RANK,                        ");
			sql.append("         DAY_GOAL                            ");
			sql.append("  FROM  TBPMS_KPI_FUND_SALES_GOAL            ");
			sql.append("    WHERE DATE_YEAR = :DATE_YEAR             ");
			sql.append("    AND PERSON_TYPE = :PERSON_TYPE           ");
			sql.append("    ORDER BY AO_JOB_RANK desc                ");
			
			qc.setObject("DATE_YEAR", inputVO.getDate_year()  );
			qc.setObject("PERSON_TYPE", inputVO.getPersonType()  );
			qc.setQueryString(sql.toString());
			List<Map<String, Object>> result = dam.exeQuery(qc);
			outputVO.setShowList(result);
		    sendRtnObject(outputVO);
		}
		catch (Exception e)
		{
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}
	/**
	 * 理財業務專員-一般分行設定-子項目FC-FCH08
	 * 保存基金銷量目標修改
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void saveFundSalesGoal(Object body, IPrimitiveMap header) throws JBranchException
	{
		PMS711InputVO inputVO = (PMS711InputVO) body;
		PMS711OutputVO outputVO = new PMS711OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		try {
			sql.append("  UPDATE TBPMS_KPI_FUND_SALES_GOAL SET               ");
			sql.append("         DAY_GOAL = :DAY_GOAL,                       ");
			sql.append("  	     VERSION = 1,                                ");
			sql.append("  	     MODIFIER = :MODIFIER,                       ");
			sql.append("  	     LASTUPDATE = SYSDATE                        ");
			sql.append("   WHERE DATE_YEAR    = :dateYear                    ");
			sql.append("  	AND	AO_JOB_RANK   = :AO_JOB_RANK                 ");
			sql.append("  	AND	PERSON_TYPE   = :PERSON_TYPE                 ");
			qc.setObject("PERSON_TYPE", inputVO.getPersonType());
			qc.setObject("dateYear", inputVO.getDate_year());
			for(int i=0; i < inputVO.getShowList().size(); i++){
				qc.setObject("AO_JOB_RANK", inputVO.getShowList().get(i).get("AO_JOB_RANK"));
				qc.setObject("DAY_GOAL", inputVO.getShowList().get(i).get("DAY_GOAL"));
				qc.setObject("MODIFIER", (String) getUserVariable(FubonSystemVariableConsts.LOGINID));
				qc.setQueryString(sql.toString());
				dam.exeUpdate(qc);
			}
			changeMastSet("EXAM_STATS",inputVO.getDate_year(),inputVO.getUserId(),inputVO.getPersonType(),inputVO.getIsSpecial(),inputVO.getSubProjectSeqId());
			this.sendRtnObject(null);
		}
		catch (Exception e)
		{
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}
	//KPI指標設定09
	/**
	  * 查詢子項目FC-FCH09
	  * @param body
	  * @param header
	  * @throws JBranchException
	  */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void queryInsGoal(Object body, IPrimitiveMap header) throws JBranchException {
		try
		{
			PMS711InputVO inputVO = (PMS711InputVO) body;
			PMS711OutputVO outputVO = new PMS711OutputVO();
			dam = this.getDataAccessManager();
			QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);			
			StringBuffer sb = new StringBuffer();
			sb.append("  SELECT PD_TYPE,                	           ");
			sb.append("         D_GOAL                	               ");
			sb.append("  FROM TBPMS_KPI_PD_INS_GOAL		               ");
			sb.append("    WHERE DATE_YEAR = :dateYear                 ");
			sb.append("  	AND	PERSON_TYPE   = :PERSON_TYPE           ");
			sb.append("     ORDER BY PD_TYPE desc                      ");
			qc.setObject("dateYear", inputVO.getDate_year());
			qc.setObject("PERSON_TYPE", inputVO.getPersonType());
			qc.setQueryString(sb.toString());
			List<Map<String, Object>> result = dam.exeQuery(qc);
			outputVO.setShowList(result);
		    sendRtnObject(outputVO);
		}
		catch (Exception e)
		{
			logger.error(String.format("發生錯誤:%s",StringUtil.getStackTraceAsString(e)));
			throw new APException(e.getMessage());
		}
	}
	 /**
	  * 更新子項目FC-FCH09
	  * @param body
	  * @param header
	  * @throws JBranchException
	  */
	public void saveInsGoal(Object body, IPrimitiveMap header) throws JBranchException {
		PMS711InputVO inputVO = (PMS711InputVO) body;
		PMS711OutputVO outputVO = new PMS711OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();	
		try{
			sb.append(" UPDATE  TBPMS_KPI_PD_INS_GOAL	                           ");
			sb.append("  SET    D_GOAL = :D_GOAL,                                  ");
			sb.append("   	    VERSION = 1,                                       ");
			sb.append("   	    MODIFIER = :MODIFIER,                              ");
			sb.append("  	    LASTUPDATE = SYSDATE                               ");
			sb.append("  WHERE  DATE_YEAR = :dateYear          				       ");
			sb.append("  AND  PD_TYPE = :PD_TYPE          				           ");
			sb.append("  AND    PERSON_TYPE = :PERSON_TYPE          		       ");
			qc.setObject("dateYear", inputVO.getDate_year()                         );
			qc.setObject("PERSON_TYPE", inputVO.getPersonType()                     );
			for(int i=0; i < inputVO.getShowList().size(); i++){
				qc.setObject("D_GOAL", inputVO.getShowList().get(i).get("D_GOAL")   );
				qc.setObject("MODIFIER", (String) getUserVariable(FubonSystemVariableConsts.LOGINID));
				qc.setObject("PD_TYPE", inputVO.getShowList().get(i).get("PD_TYPE") );
				qc.setQueryString(sb.toString());
				dam.exeUpdate(qc);
				
			}
			changeMastSet("EXAM_STATS",inputVO.getDate_year(),inputVO.getUserId(),inputVO.getPersonType(),inputVO.getIsSpecial(),inputVO.getSubProjectSeqId());
			sendRtnObject(outputVO);
		}catch (Exception e) {
			logger.error("更新失敗");
			throw new APException(e.getMessage());
		}
	}
	//KPI指標設定10
	/**
	  * 查詢子項目10
	  * @param body
	  * @param header
	  * @throws JBranchException
	  */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void queryInvGoal(Object body, IPrimitiveMap header) throws JBranchException {
		try
		{
			PMS711InputVO inputVO = (PMS711InputVO) body;
			PMS711OutputVO outputVO = new PMS711OutputVO();
			dam = this.getDataAccessManager();
			QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);			
			StringBuffer sb = new StringBuffer();
			sb.append("  SELECT PD_TYPE,                	     ");
			sb.append("         D_GOAL                	         ");
			sb.append("  FROM TBPMS_KPI_PD_INV_GOAL		   	     ");
			sb.append("  WHERE DATE_YEAR = :dateYear      	     ");
			sb.append("  	AND	PERSON_TYPE   = :PERSON_TYPE     ");
			sb.append("     ORDER BY PD_TYPE desc                ");
			qc.setObject("dateYear", inputVO.getDate_year()       );
			qc.setObject("PERSON_TYPE", inputVO.getPersonType());
			qc.setQueryString(sb.toString()                       );
			List<Map<String, Object>> result = dam.exeQuery(qc);
			outputVO.setShowList(result);
			sendRtnObject(outputVO);
		}
		catch (Exception e)
		{
			logger.error(String.format("發生錯誤:%s",StringUtil.getStackTraceAsString(e)));
			throw new APException(e.getMessage());
		}
	}
	 /**
	  * 更新子項目10
	  * @param body
	  * @param header
	  * @throws JBranchException
	  */
	public void saveInvGoal(Object body, IPrimitiveMap header) throws JBranchException {
		try{
		PMS711InputVO inputVO = (PMS711InputVO) body;
		PMS711OutputVO outputVO = new PMS711OutputVO();
		for(int i=0; i < inputVO.getShowList().size(); i++){
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();					
		sb.append("UPDATE  TBPMS_KPI_PD_INV_GOAL	                           ");
		sb.append(" SET    D_GOAL = :D_GOAL,                                   ");
		sb.append("  	   VERSION = 1,                                        ");
		sb.append("  	   MODIFIER = :MODIFIER,                               ");
		sb.append("  	   LASTUPDATE = SYSDATE                                ");
		sb.append(" WHERE  DATE_YEAR = :dateYear          				       ");
		sb.append(" AND    PD_TYPE = :PD_TYPE          				           ");
		sb.append(" AND    PERSON_TYPE = :PERSON_TYPE          				   ");
		qc.setObject("PERSON_TYPE", inputVO.getPersonType()                     );
		qc.setObject("dateYear", inputVO.getDate_year()                         );
		qc.setObject("D_GOAL", inputVO.getShowList().get(i).get("D_GOAL")       );
		qc.setObject("MODIFIER", (String) getUserVariable(FubonSystemVariableConsts.LOGINID));
		qc.setObject("PD_TYPE", inputVO.getShowList().get(i).get("PD_TYPE")     );
		qc.setQueryString(sb.toString());
		dam.exeUpdate(qc);
		}
		changeMastSet("EXAM_STATS",inputVO.getDate_year(),inputVO.getUserId(),inputVO.getPersonType(),inputVO.getIsSpecial(),inputVO.getSubProjectSeqId());
		sendRtnObject(outputVO);
		}catch (Exception e) {
			logger.error("更新失敗");
			throw new APException(e.getMessage());
		}
	}
	//KPI指標設定11
	/**
	  * 查詢子項目11
	  * @param body
	  * @param header7
	  * @throws JBranchException
	  */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void queryLinsGoal(Object body, IPrimitiveMap header) throws JBranchException {
		try
		{
		PMS711InputVO inputVO = (PMS711InputVO) body;
		PMS711OutputVO outputVO = new PMS711OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);			
		StringBuffer sb = new StringBuffer();
		sb.append("  SELECT PD_TYPE,                	             ");
		sb.append("         D_GOAL                	                 ");
		sb.append("  FROM TBPMS_KPI_PD_LINS_GOAL		             ");
		sb.append("  WHERE DATE_YEAR = :dateYear                     ");
		sb.append("  	AND	PERSON_TYPE   = :PERSON_TYPE             ");
		sb.append("  ORDER BY PD_TYPE DESC                           ");
		qc.setObject("dateYear", inputVO.getDate_year()   );
		qc.setObject("PERSON_TYPE", inputVO.getPersonType());
		qc.setQueryString(sb.toString()                   );
		List<Map<String, Object>> result = dam.exeQuery(qc);
		outputVO.setShowList(result);
		sendRtnObject(outputVO);
		}
		catch (Exception e)
		{
		logger.error(String.format("發生錯誤:%s",StringUtil.getStackTraceAsString(e)));
		throw new APException(e.getMessage());
		}
	}
	 /**
	  * 更新子項目11
	  * @param body
	  * @param header
	  * @throws JBranchException
	  */
	public void saveLinsGoal(Object body, IPrimitiveMap header) throws JBranchException {
		try{
		PMS711InputVO inputVO = (PMS711InputVO) body;
		PMS711OutputVO outputVO = new PMS711OutputVO();
		for(int i=0; i < inputVO.getShowList().size(); i++){
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();					
		sb.append("UPDATE  TBPMS_KPI_PD_LINS_GOAL	                           ");
		sb.append(" SET    D_GOAL = :D_GOAL,                                   ");
		sb.append("  	   VERSION = 1,                                        ");
		sb.append("  	   MODIFIER = :MODIFIER,                               ");
		sb.append("  	   LASTUPDATE = SYSDATE                                ");
		sb.append(" WHERE  DATE_YEAR = :dateYear          				       ");
		sb.append(" AND    PD_TYPE = :PD_TYPE          				           ");
		sb.append(" AND    PERSON_TYPE = :PERSON_TYPE          				   ");
		qc.setObject("PERSON_TYPE", inputVO.getPersonType()                     );
		qc.setObject("dateYear", inputVO.getDate_year()                         );
		qc.setObject("D_GOAL", inputVO.getShowList().get(i).get("D_GOAL")       );
		qc.setObject("MODIFIER", (String) getUserVariable(FubonSystemVariableConsts.LOGINID));
		qc.setObject("PD_TYPE", inputVO.getShowList().get(i).get("PD_TYPE")     );
		qc.setQueryString(sb.toString());
		dam.exeUpdate(qc);
		}
		changeMastSet("EXAM_STATS",inputVO.getDate_year(),inputVO.getUserId(),inputVO.getPersonType(),inputVO.getIsSpecial(),inputVO.getSubProjectSeqId());
		sendRtnObject(outputVO);
		}catch (Exception e) {
			logger.error("更新失敗");
			throw new APException(e.getMessage());
		}
	}
	/**
	 * 理財業務專員-一般分行設定-子項目12
	 * 進入房貸(中長期購屋、非購屋+額度式訂約額度)新承作目標子頁面
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void queryHouNew(Object body, IPrimitiveMap header) throws JBranchException
	{
		PMS711InputVO inputVO = (PMS711InputVO) body;
		PMS711OutputVO outputVO = new PMS711OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		try
		{
			sql.append("  SELECT DATE_YEAR,                                        ");
			sql.append("         AO_JOB_RANK,                                      ");
			sql.append("         CEN_LONG_BUY,                                     ");
			sql.append("         MIS_BUY,                                          ");
			sql.append("         LIMIT_STYLE                                       ");
			sql.append("  FROM  TBPMS_KPI_PD_HOU_NEW                               ");
			sql.append("    WHERE DATE_YEAR = :DATE_YEAR                           ");
			sql.append("  	AND	PERSON_TYPE   = :PERSON_TYPE                       ");
			sql.append("  	AND	SUB_PROJECT_SEQ_ID   = :SUB_PROJECT_SEQ_ID         ");
			sql.append("  ORDER BY AO_JOB_RANK DESC                                ");
			qc.setObject("PERSON_TYPE", inputVO.getPersonType());
			qc.setObject("DATE_YEAR", inputVO.getDate_year());
			qc.setObject("SUB_PROJECT_SEQ_ID", inputVO.getSubProjectSeqId());
			qc.setQueryString(sql.toString());
			List<Map<String, Object>> result = dam.exeQuery(qc);
			outputVO.setShowList(result);
		    sendRtnObject(outputVO);
		}
		catch (Exception e)
		{
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}
	/**
	 * 理財業務專員-一般分行設定-子項目12
	 * 儲存房貸(中長期購屋、非購屋+額度式訂約額度)新承作目標修改
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void saveHouNew(Object body, IPrimitiveMap header) throws JBranchException
	{
		PMS711InputVO inputVO = (PMS711InputVO) body;
		PMS711OutputVO outputVO = new PMS711OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		try {
			if("12".equals(inputVO.getSubProjectSeqId())){
				sql.append("  UPDATE TBPMS_KPI_PD_HOU_NEW SET                    ");
				sql.append("         CEN_LONG_BUY = :CEN_LONG_BUY,               ");
				sql.append("  	     VERSION = 1,                                ");
				sql.append("  	     MODIFIER = :MODIFIER,                       ");
				sql.append("  	     LASTUPDATE = SYSDATE                        ");
				sql.append("   WHERE DATE_YEAR    = :DATE_YEAR                   ");
				sql.append("  	AND	AO_JOB_RANK   = :AO_JOB_RANK                 ");
				sql.append(" AND    PERSON_TYPE = :PERSON_TYPE          	     ");
				sql.append(" AND    SUB_PROJECT_SEQ_ID = :SUB_PROJECT_SEQ_ID     ");
				qc.setObject("DATE_YEAR", inputVO.getDate_year()                  );
				qc.setObject("PERSON_TYPE", inputVO.getPersonType()               );
				qc.setObject("SUB_PROJECT_SEQ_ID", inputVO.getSubProjectSeqId()   );
				for(int i=0; i < inputVO.getShowList().size(); i++){
					qc.setObject("AO_JOB_RANK", inputVO.getShowList().get(i).get("AO_JOB_RANK"));
					qc.setObject("CEN_LONG_BUY", inputVO.getShowList().get(i).get("CEN_LONG_BUY"));
					qc.setObject("MODIFIER", (String) getUserVariable(FubonSystemVariableConsts.LOGINID));
					qc.setQueryString(sql.toString());
					dam.exeUpdate(qc);
				}
			}else if("25".equals(inputVO.getSubProjectSeqId())) {
				sql.append("  UPDATE TBPMS_KPI_PD_HOU_NEW SET                    ");
				sql.append("         MIS_BUY      = :MIS_BUY,                    ");
				sql.append("         LIMIT_STYLE  = :LIMIT_STYLE,                ");
				sql.append("  	     VERSION = 1,                                ");
				sql.append("  	     MODIFIER = :MODIFIER,                       ");
				sql.append("  	     LASTUPDATE = SYSDATE                        ");
				sql.append("   WHERE DATE_YEAR    = :DATE_YEAR                   ");
				sql.append("  	AND	AO_JOB_RANK   = :AO_JOB_RANK                 ");
				sql.append(" AND    PERSON_TYPE = :PERSON_TYPE          	     ");
				sql.append(" AND    SUB_PROJECT_SEQ_ID = :SUB_PROJECT_SEQ_ID     ");
				qc.setObject("DATE_YEAR", inputVO.getDate_year()                  );
				qc.setObject("PERSON_TYPE", inputVO.getPersonType()               );
				qc.setObject("SUB_PROJECT_SEQ_ID", inputVO.getSubProjectSeqId()   );
				for(int i=0; i < inputVO.getShowList().size(); i++){
					qc.setObject("AO_JOB_RANK", inputVO.getShowList().get(i).get("AO_JOB_RANK"));
					qc.setObject("MIS_BUY", inputVO.getShowList().get(i).get("MIS_BUY"));
					qc.setObject("LIMIT_STYLE", inputVO.getShowList().get(i).get("LIMIT_STYLE"));
					qc.setObject("MODIFIER", (String) getUserVariable(FubonSystemVariableConsts.LOGINID));
					qc.setQueryString(sql.toString());
					dam.exeUpdate(qc);
				}
			}
			changeMastSet("EXAM_STATS",inputVO.getDate_year(),inputVO.getUserId(),inputVO.getPersonType(),inputVO.getIsSpecial(),inputVO.getSubProjectSeqId());
			sendRtnObject(outputVO);
		}
		catch (Exception e)
		{
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員"+e.getMessage());
		}
	}
	/**
	 * 理財業務專員-一般分行設定-子項目12
	 * 進入房貸(中長期購屋、非購屋+額度式訂約額度)新承作目標-T/S設定頁面
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void queryHouNewTS(Object body, IPrimitiveMap header) throws JBranchException
	{
		PMS711InputVO inputVO = (PMS711InputVO) body;
		PMS711OutputVO outputVO = new PMS711OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		try
		{
			sql.append("  SELECT DATE_YEAR,                          ");
			sql.append("         ORG_ID,                             ");
			sql.append("         ORG_NAME,                           ");
			sql.append("         BUY_HON_TS                          ");
			sql.append("  FROM  TBPMS_KPI_PD_HOU_NEW_TS              ");
			sql.append("    WHERE DATE_YEAR = :YEAR                  ");
			sql.append("    AND PERSON_TYPE = :PERSON_TYPE           ");
			sql.append("    AND TS_TYPE = :TS_TYPE");
			qc.setObject("PERSON_TYPE", inputVO.getPersonType()       );
			qc.setObject("YEAR", inputVO.getDate_year());
			qc.setObject("TS_TYPE", inputVO.getTsType());
			qc.setQueryString(sql.toString());
			
			ResultIF largeAgrList = dam.executePaging(qc, inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
			int totalPage_i = largeAgrList.getTotalPage(); // 分頁用
			int totalRecord_i = largeAgrList.getTotalRecord(); // 分頁用
			outputVO.setShowList(largeAgrList); // data
			outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());// 當前頁次
			outputVO.setTotalPage(totalPage_i);// 總頁次
			outputVO.setTotalRecord(totalRecord_i);// 總筆數
						
		    sendRtnObject(outputVO);
		}
		catch (Exception e)
		{
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}
	/**
	 * 理財業務專員-子項目12
	 * 保存信貸新承作目標修改
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void saveHonNewTs(Object body, IPrimitiveMap header) throws JBranchException
	{
		PMS711InputVO inputVO = (PMS711InputVO) body;
		PMS711OutputVO outputVO = new PMS711OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		try {
			sql.append("  UPDATE TBPMS_KPI_PD_HOU_NEW_TS SET             ");
			sql.append("         BUY_HON_TS = :BUY_HON_TS,               ");
			sql.append("  	     VERSION = 1,                            ");
			sql.append("  	     MODIFIER = :MODIFIER,                   ");
			sql.append("  	     LASTUPDATE = SYSDATE                    ");
			sql.append("   WHERE DATE_YEAR    = :dateYear                ");
			sql.append("  	AND	PERSON_TYPE   = :PERSON_TYPE             ");
			sql.append("  	AND	ORG_ID   = :ORG_ID                       ");
			sql.append("  	AND	TS_TYPE   = :TS_TYPE                     ");
			qc.setObject("PERSON_TYPE", inputVO.getPersonType());
			qc.setObject("dateYear", inputVO.getDate_year());
			qc.setObject("TS_TYPE", inputVO.getTsType());
			for(int i=0; i < inputVO.getShowList().size(); i++){
				qc.setObject("BUY_HON_TS", inputVO.getShowList().get(i).get("BUY_HON_TS"));
				qc.setObject("ORG_ID", inputVO.getShowList().get(i).get("ORG_ID"));
				qc.setObject("MODIFIER", (String) getUserVariable(FubonSystemVariableConsts.LOGINID));
				qc.setQueryString(sql.toString());
				dam.exeUpdate(qc);
			}
			sendRtnObject(outputVO);
		}
		catch (Exception e)
		{
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}
	//從excel表格中新增數據
	@SuppressWarnings({ "rawtypes", "unused" })
	public void setHonNewTS(Object body, IPrimitiveMap header) throws JBranchException
	{
		PMS711InputVO inputVO = (PMS711InputVO) body;
		PMS711OutputVO outputVO = new PMS711OutputVO();
		int flag = 0;
		try{
			List<String> list =  new ArrayList<String>();
			String joinedPath = new File((String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH), inputVO.getFileName()).toString();
			Workbook workbook = Workbook.getWorkbook(new File(joinedPath));
			Sheet sheet[] = workbook.getSheets();
			//有表頭.xls文檔
			//清空臨時表
			dam = this.getDataAccessManager();
			QueryConditionIF dcon = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);	
			String dsql = " TRUNCATE TABLE TBPMS_KPI_PD_HOU_NEW_TS_U ";
			dcon.setQueryString(dsql.toString());
			dam.exeUpdate(dcon);
			String lab = null;
//			for(int a=0;a<sheet.length;a++){   //目前註解只需要一sheet
			for(int a=0;a<1;a++){
				for(int i=1;i<sheet[a].getRows();i++){
					for(int j=0;j<sheet[a].getColumns();j++){
						lab = sheet[a].getCell(j, i).getContents();
						list.add(lab);
					}
					//excel表格記行數
					flag++;
					//判斷當前上傳數據欄位個數是否一致
					if(list.size()!=3){
						throw new APException("上傳數據欄位個數不一致");
					}
					//SQL指令
					StringBuffer sb = new StringBuffer();
					dam = this.getDataAccessManager();
					QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					sb.append("   INSERT INTO TBPMS_KPI_PD_HOU_NEW_TS_U (DATE_YEAR,	         ");
					sb.append("          TS_TYPE,                                            ");
					sb.append("          ORG_ID,                                             ");
					sb.append("          ORG_NAME,                                           ");
					sb.append("          BUY_HON_TS,                                         ");
					sb.append("          PERSON_TYPE,                                        ");
					sb.append("  		 RNUM,            					                 ");		
					sb.append("  		 VERSION,            						         ");
					sb.append("  		 CREATETIME,             					         ");
					sb.append("  		 CREATOR,             						         ");
					sb.append("  		 MODIFIER,         						             ");
					sb.append("  		 LASTUPDATE )             					         ");
					sb.append("  	VALUES(:DATE_YEAR,            				             ");
					sb.append("  		:TS_TYPE,             				                 ");
					sb.append("  		:ORG_ID,             				                 ");
					sb.append("  		:ORG_NAME,             				                 ");
					sb.append("  		:BUY_HON_TS,             					         ");
					sb.append("  		:PERSON_TYPE,             					         ");
					sb.append("  		:RNUM,             					                 ");
					sb.append("  		:VERSION,           					             ");
					sb.append("  		SYSDATE,           				                     ");
					sb.append("  		:CREATOR,            					             ");
					sb.append("  		:MODIFIER,         					                 ");
					sb.append("  		SYSDATE)          				                     ");
					qc.setObject("DATE_YEAR",inputVO.getDate_year()                           );
					qc.setObject("TS_TYPE",inputVO.getTsType()                                );
					qc.setObject("ORG_ID",list.get(0).trim()                                  );
					qc.setObject("ORG_NAME",list.get(1).trim()                                );
					qc.setObject("BUY_HON_TS",list.get(2).trim()                              );
					qc.setObject("PERSON_TYPE",inputVO.getPersonType()                        );
					qc.setObject("RNUM",flag                                                  );
					qc.setObject("VERSION","0"                                                );
					qc.setObject("CREATOR", (String) getUserVariable(FubonSystemVariableConsts.LOGINID));
					qc.setObject("MODIFIER",(String) getUserVariable(FubonSystemVariableConsts.LOGINID));
					qc.setQueryString(sb.toString());
					dam.exeUpdate(qc);
					list.clear();
				}
			}	
			sendRtnObject(null);
		}catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",StringUtil.getStackTraceAsString(e)));
			throw new APException("資料上傳失敗,錯誤發生在第"+flag+"筆,"+e.getMessage());
		}
	}
	//KPI指標設定13
	/**
	  * 查詢子項目13
	  * @param body
	  * @param header
	  * @throws JBranchException
	  */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void queryInhGoal(Object body, IPrimitiveMap header) throws JBranchException {
		try
		{
		PMS711InputVO inputVO = (PMS711InputVO) body;
		PMS711OutputVO outputVO = new PMS711OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);			
		StringBuffer sb = new StringBuffer();
		sb.append("  SELECT PD_TYPE,                	               ");
		sb.append("         D_GOAL                	                   ");
		sb.append("  FROM TBPMS_KPI_PD_INH_GOAL		   	               ");
		sb.append("  WHERE DATE_YEAR = :dateYear                       ");
		sb.append(" AND    PERSON_TYPE = :PERSON_TYPE          	       ");
		qc.setObject("PERSON_TYPE", inputVO.getPersonType()             );
		sb.append("  ORDER BY PD_TYPE DESC                             ");
		qc.setObject("dateYear", inputVO.getDate_year()                 );
		qc.setQueryString(sb.toString()                                 );
		List<Map<String, Object>> result = dam.exeQuery(qc);
		outputVO.setShowList(result);
		sendRtnObject(outputVO);
		}
		catch (Exception e)
		{
		logger.error(String.format("發生錯誤:%s",StringUtil.getStackTraceAsString(e)));
		throw new APException(e.getMessage());
		}
	}
	 /**
	  * 更新子項目13
	  * @param body
	  * @param header
	  * @throws JBranchException
	  */
	public void saveInhGoal(Object body, IPrimitiveMap header) throws JBranchException {
		try{
		PMS711InputVO inputVO = (PMS711InputVO) body;
		PMS711OutputVO outputVO = new PMS711OutputVO();
		for(int i=0; i < inputVO.getShowList().size(); i++){
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();					
		sb.append("UPDATE  TBPMS_KPI_PD_INH_GOAL	                           ");
		sb.append(" SET    D_GOAL = :D_GOAL,                                   ");
		sb.append("  	   VERSION = 1,                                        ");
		sb.append("  	   MODIFIER = :MODIFIER,                               ");
		sb.append("  	   LASTUPDATE = SYSDATE                                ");
		sb.append(" WHERE  DATE_YEAR = :dateYear          				       ");
		sb.append(" AND    PD_TYPE = :PD_TYPE          				           ");
		sb.append("  	AND	PERSON_TYPE   = :PERSON_TYPE                       ");
		qc.setObject("PERSON_TYPE", inputVO.getPersonType());
		qc.setObject("dateYear", inputVO.getDate_year()                         );
		qc.setObject("D_GOAL", inputVO.getShowList().get(i).get("D_GOAL")       );
		qc.setObject("MODIFIER",(String) getUserVariable(FubonSystemVariableConsts.LOGINID));
		qc.setObject("PD_TYPE", inputVO.getShowList().get(i).get("PD_TYPE")     );
		qc.setQueryString(sb.toString());
		dam.exeUpdate(qc);
		}
		changeMastSet("EXAM_STATS",inputVO.getDate_year(),inputVO.getUserId(),inputVO.getPersonType(),inputVO.getIsSpecial(),inputVO.getSubProjectSeqId());
		sendRtnObject(outputVO);
		}catch (Exception e) {
			logger.error("更新失敗");
			throw new APException(e.getMessage());
		}
	}
	/**
	 * 理財業務專員-一般分行設定-子項目14
	 * 進入信貸新承作目標子頁面
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void queryCreditNewGoal(Object body, IPrimitiveMap header) throws JBranchException
	{
		PMS711InputVO inputVO = (PMS711InputVO) body;
		PMS711OutputVO outputVO = new PMS711OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		try
		{
			sql.append("  SELECT DATE_YEAR,                            ");
			sql.append("         AO_JOB_RANK,                          ");
			sql.append("         YEAR_GOAL                             ");
			sql.append("  FROM  TBPMS_KPI_CREDIT_NEW_GOAL              ");
			sql.append("    WHERE DATE_YEAR = :dateYear                ");
			sql.append(" AND    PERSON_TYPE = :PERSON_TYPE             ");
			sql.append(" ORDER BY AO_JOB_RANK DESC                     ");
			
			qc.setObject("dateYear", inputVO.getDate_year());
			qc.setObject("PERSON_TYPE", inputVO.getPersonType()         );
			qc.setQueryString(sql.toString());
			List<Map<String, Object>> result = dam.exeQuery(qc);
			outputVO.setShowList(result);
		    sendRtnObject(outputVO);
		}
		catch (Exception e)
		{
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}
	/**
	 * 理財業務專員-一般分行設定-子項目14
	 * 保存信貸新承作目標修改
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void saveCreditNewGoal(Object body, IPrimitiveMap header) throws JBranchException
	{
		PMS711InputVO inputVO = (PMS711InputVO) body;
		PMS711OutputVO outputVO = new PMS711OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		try {
			sql.append("  UPDATE TBPMS_KPI_CREDIT_NEW_GOAL SET           ");
			sql.append("         YEAR_GOAL = :YEAR_GOAL,                 ");
			sql.append("  	     VERSION = 1,                            ");
			sql.append("  	     MODIFIER = :MODIFIER,                   ");
			sql.append("  	     LASTUPDATE = SYSDATE                    ");
			sql.append("   WHERE DATE_YEAR    = :dateYear                ");
			sql.append("  	AND	AO_JOB_RANK   = :AO_JOB_RANK             ");
			sql.append("  	AND	PERSON_TYPE   = :PERSON_TYPE             ");
			qc.setObject("PERSON_TYPE", inputVO.getPersonType());
			qc.setObject("dateYear", inputVO.getDate_year());
			for(int i=0; i < inputVO.getShowList().size(); i++){
				qc.setObject("AO_JOB_RANK", inputVO.getShowList().get(i).get("AO_JOB_RANK"));
				qc.setObject("YEAR_GOAL", inputVO.getShowList().get(i).get("YEAR_GOAL"));
				qc.setObject("MODIFIER", (String) getUserVariable(FubonSystemVariableConsts.LOGINID));
				qc.setQueryString(sql.toString());
				dam.exeUpdate(qc);
			}
			changeMastSet("EXAM_STATS",inputVO.getDate_year(),inputVO.getUserId(),inputVO.getPersonType(),inputVO.getIsSpecial(),inputVO.getSubProjectSeqId());
			sendRtnObject(outputVO);
		}
		catch (Exception e)
		{
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}
	/**
	 * 理財業務專員-一般分行設定-子項目14
	 * T/S設定
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	//從excel表格中新增數據
	@SuppressWarnings({ "rawtypes", "unused" })
	public void setCreditNewGoalTS(Object body, IPrimitiveMap header) throws JBranchException
	{
		PMS711InputVO inputVO = (PMS711InputVO) body;
		PMS711OutputVO outputVO = new PMS711OutputVO();
		int flag = 0;
		try{
			List<String> list =  new ArrayList<String>();
			String joinedPath = new File((String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH), inputVO.getFileName()).toString();
			Workbook workbook = Workbook.getWorkbook(new File(joinedPath));
			Sheet sheet[] = workbook.getSheets();
			//有表頭.xls文檔
			//清空臨時表
			dam = this.getDataAccessManager();
			QueryConditionIF dcon = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);	
			String dsql = " TRUNCATE TABLE TBPMS_KPI_CREDIT_NEW_GOAL_TS_U ";
			dcon.setQueryString(dsql.toString());
			dam.exeUpdate(dcon);
			String lab = null;
//			for(int a=0;a<sheet.length;a++){  //目前只需要一  sheet
			for(int a=0;a<1;a++){
				for(int i=1;i<sheet[a].getRows();i++){
					for(int j=0;j<sheet[a].getColumns();j++){
						lab = sheet[a].getCell(j, i).getContents();
						list.add(lab);
					}
					//excel表格記行數
					flag++;
					//判斷當前上傳數據欄位個數是否一致
					if(list.size()!=3){
						throw new APException("上傳數據欄位個數不一致");
					}
					//SQL指令
					StringBuffer sb = new StringBuffer();
					dam = this.getDataAccessManager();
					QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					sb.append("   INSERT INTO TBPMS_KPI_CREDIT_NEW_GOAL_TS_U (DATE_YEAR,	 ");
					sb.append("          BRANCH_AREA_ID,                                     ");
					sb.append("          BRANCH_AREA_NAME,                                   ");
					sb.append("          TS,                                                 ");
					sb.append("          PERSON_TYPE,                                        ");
					sb.append("  		 RNUM,            					                 ");		
					sb.append("  		 VERSION,            						         ");
					sb.append("  		 CREATETIME,             					         ");
					sb.append("  		 CREATOR,             						         ");
					sb.append("  		 MODIFIER,         						             ");
					sb.append("  		 LASTUPDATE )             					         ");
					sb.append("  	VALUES(:DATE_YEAR,            				             ");
					sb.append("  		:BRANCH_AREA_ID,             				         ");
					sb.append("  		:BRANCH_AREA_NAME,             				         ");
					sb.append("  		:TS,             					                 ");
					sb.append("  		:PERSON_TYPE,             					         ");
					sb.append("  		:RNUM,             					                 ");
					sb.append("  		:VERSION,           					             ");
					sb.append("  		SYSDATE,           				                     ");
					sb.append("  		:CREATOR,            					             ");
					sb.append("  		:MODIFIER,         					                 ");
					sb.append("  		SYSDATE)          				                     ");
					qc.setObject("DATE_YEAR",inputVO.getDate_year()                           );
					qc.setObject("BRANCH_AREA_ID",list.get(0).trim()                          );
					qc.setObject("BRANCH_AREA_NAME",list.get(1).trim()                        );
					qc.setObject("TS",list.get(2).trim()                                      );
					qc.setObject("PERSON_TYPE",inputVO.getPersonType()                        );
					qc.setObject("RNUM",flag                                                  );
					qc.setObject("VERSION","0"                                                );
					qc.setObject("CREATOR", (String) getUserVariable(FubonSystemVariableConsts.LOGINID));
					qc.setObject("MODIFIER",(String) getUserVariable(FubonSystemVariableConsts.LOGINID));
					qc.setQueryString(sb.toString());
					dam.exeUpdate(qc);
					list.clear();
				}
			}	
			sendRtnObject(null);
		}catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",StringUtil.getStackTraceAsString(e)));
			throw new APException("資料上傳失敗,錯誤發生在第"+flag+"筆,"+e.getMessage());
		}
	}
	//從excel表格中新增數據
	public void queryCreditNewGoalTS(Object body, IPrimitiveMap header) throws JBranchException
	{	
		PMS711InputVO inputVO = (PMS711InputVO) body;
		PMS711OutputVO outputVO = new PMS711OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		try
		{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			sql.append("  SELECT DATE_YEAR,											");
			sql.append("         BRANCH_AREA_ID,									");
			sql.append("  	     BRANCH_AREA_NAME,                                  ");
			sql.append("  	     TS                                                 ");
			sql.append("  FROM TBPMS_KPI_CREDIT_NEW_GOAL_TS                         ");
			//if(StringUtils.isNotBlank(inputVO.getYearMon())){
				sql.append("    WHERE DATE_YEAR = :DATE_YEAR                        ");
				condition.setObject("DATE_YEAR", inputVO.getDate_year());
			//}
			//if(StringUtils.isNotBlank(inputVO.getPersonType())){
				sql.append("    AND PERSON_TYPE = :PERSON_TYPE                    ");
				condition.setObject("PERSON_TYPE", inputVO.getPersonType());
			//}
			condition.setQueryString(sql.toString());
			ResultIF list = dam.executePaging(condition, inputVO
					.getCurrentPageIndex() + 1, inputVO.getPageCount());
			List<Map<String, Object>> list1 = dam.exeQuery(condition);
			int totalPage_i = list.getTotalPage();
			int totalRecord_i = list.getTotalRecord();
			outputVO.setShowList(list);
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
	//KPI指標設定15
	/**
	  * 查詢子項目15
	  * @param body
	  * @param header
	  * @throws JBranchException
	  */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void queryConGoal(Object body, IPrimitiveMap header) throws JBranchException {
		try
		{
		PMS711InputVO inputVO = (PMS711InputVO) body;
		PMS711OutputVO outputVO = new PMS711OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);			
		StringBuffer sb = new StringBuffer();
		sb.append("  SELECT PD_TYPE,                	 ");
		sb.append("         E_GOAL,                	     ");
		sb.append("         I_GOAL,                	     ");
		sb.append("         P_GOAL                	     ");
		sb.append("  FROM TBPMS_KPI_PD_CON_GOAL		   	 ");
		sb.append("  WHERE DATE_YEAR = :dateYear	   	 ");
		sb.append(" AND    PERSON_TYPE = :PERSON_TYPE    ");
		sb.append("  ORDER BY PD_TYPE DESC               ");
		qc.setObject("dateYear", inputVO.getDate_year()   );
		qc.setObject("PERSON_TYPE", inputVO.getPersonType());
		qc.setQueryString(sb.toString()                   );
		List<Map<String, Object>> result = dam.exeQuery(qc);
		outputVO.setShowList(result);
		sendRtnObject(outputVO);
		}
		catch (Exception e)
		{
		logger.error(String.format("發生錯誤:%s",StringUtil.getStackTraceAsString(e)));
		throw new APException(e.getMessage());
		}
	}
	 /**
	  * 更新子項目15
	  * @param body
	  * @param header
	  * @throws JBranchException
	  */
	public void saveConGoal(Object body, IPrimitiveMap header) throws JBranchException {
		try{
		PMS711InputVO inputVO = (PMS711InputVO) body;
		PMS711OutputVO outputVO = new PMS711OutputVO();
		for(int i=0; i < inputVO.getShowList().size(); i++){
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();					
		sb.append("UPDATE  TBPMS_KPI_PD_CON_GOAL	                           ");
		sb.append(" SET    E_GOAL = :E_GOAL,                                   ");
		sb.append("        I_GOAL = :I_GOAL,                                   ");
		sb.append("        P_GOAL = :P_GOAL,                                   ");
		sb.append("  	   VERSION = 1,                                        ");
		sb.append("  	   MODIFIER = :MODIFIER,                               ");
		sb.append("  	   LASTUPDATE = SYSDATE                                ");
		sb.append(" WHERE  DATE_YEAR = :dateYear          			           ");
		sb.append(" AND    PD_TYPE = :PD_TYPE          				           ");
		sb.append("  	AND	PERSON_TYPE   = :PERSON_TYPE                       ");
		qc.setObject("PERSON_TYPE", inputVO.getPersonType());
		qc.setObject("dateYear", inputVO.getDate_year()                         );
		qc.setObject("E_GOAL", inputVO.getShowList().get(i).get("E_GOAL")       );
		qc.setObject("I_GOAL", inputVO.getShowList().get(i).get("I_GOAL")       );
		qc.setObject("P_GOAL", inputVO.getShowList().get(i).get("P_GOAL")       );
		qc.setObject("MODIFIER", (String) getUserVariable(FubonSystemVariableConsts.LOGINID));
		qc.setObject("PD_TYPE", inputVO.getShowList().get(i).get("PD_TYPE")     );
		qc.setQueryString(sb.toString());
		dam.exeUpdate(qc);
		}
		changeMastSet("EXAM_STATS",inputVO.getDate_year(),inputVO.getUserId(),inputVO.getPersonType(),inputVO.getIsSpecial(),inputVO.getSubProjectSeqId());
		sendRtnObject(outputVO);
		}catch (Exception e) {
			logger.error("更新失敗");
			throw new APException(e.getMessage());
		}
	}
	//KPI指標設定16
	/**
	  * 查詢子項目16
	  * @param body
	  * @param header
	  * @throws JBranchException
	  */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void queryNewGoal(Object body, IPrimitiveMap header) throws JBranchException {
			try
			{
				PMS711InputVO inputVO = (PMS711InputVO) body;
				PMS711OutputVO outputVO = new PMS711OutputVO();
				dam = this.getDataAccessManager();
				QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);			
				StringBuffer sb = new StringBuffer();
				sb.append("  SELECT PD_TYPE,                	 ");
				sb.append("         CARD_GOAL                	 ");
				sb.append("  FROM TBPMS_KPI_PD_NEW_GOAL		   	 ");
				sb.append("  WHERE DATE_YEAR = :dateYear     	 ");
				sb.append(" AND    PERSON_TYPE = :PERSON_TYPE    ");
				sb.append("  ORDER BY PD_TYPE DESC             	 ");
				qc.setObject("dateYear", inputVO.getDate_year()   );
				qc.setObject("PERSON_TYPE", inputVO.getPersonType());
				qc.setQueryString(sb.toString()                   );
				List<Map<String, Object>> result = dam.exeQuery(qc);
				outputVO.setShowList(result);
				sendRtnObject(outputVO);
			}
			catch (Exception e)
			{
				logger.error(String.format("發生錯誤:%s",StringUtil.getStackTraceAsString(e)));
				throw new APException(e.getMessage());
			}
	}
	 /**
	  * 更新子項目16
	  * @param body
	  * @param header
	  * @throws JBranchException
	  */
	public void saveNewGoal(Object body, IPrimitiveMap header) throws JBranchException {
		try{
		PMS711InputVO inputVO = (PMS711InputVO) body;
		PMS711OutputVO outputVO = new PMS711OutputVO();
		for(int i=0; i < inputVO.getShowList().size(); i++){
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();					
		sb.append("UPDATE  TBPMS_KPI_PD_NEW_GOAL	                           ");
		sb.append(" SET    CARD_GOAL = :CARD_GOAL,                             ");
		sb.append("  	   VERSION = 1,                                        ");
		sb.append("  	   MODIFIER = :MODIFIER,                               ");
		sb.append("  	   LASTUPDATE = SYSDATE                                ");
		sb.append(" WHERE  DATE_YEAR = :dateYear     		                   ");
		sb.append(" AND    PD_TYPE = :PD_TYPE          				           ");
		sb.append("  	AND	PERSON_TYPE   = :PERSON_TYPE                       ");
		qc.setObject("PERSON_TYPE", inputVO.getPersonType());
		qc.setObject("dateYear", inputVO.getDate_year()                         );
		qc.setObject("CARD_GOAL", inputVO.getShowList().get(i).get("CARD_GOAL") );
		qc.setObject("MODIFIER", (String) getUserVariable(FubonSystemVariableConsts.LOGINID));
		qc.setObject("PD_TYPE", inputVO.getShowList().get(i).get("PD_TYPE")     );
		qc.setQueryString(sb.toString());
		dam.exeUpdate(qc);
		}
		changeMastSet("EXAM_STATS",inputVO.getDate_year(),inputVO.getUserId(),inputVO.getPersonType(),inputVO.getIsSpecial(),inputVO.getSubProjectSeqId());
		sendRtnObject(outputVO);
		}catch (Exception e) {
			logger.error("更新失敗");
			throw new APException(e.getMessage());
		}
	}
	//KPI指標設定17
	/**
	  * 查詢子項目17
	  * @param body
	  * @param header
	  * @throws JBranchException
	  */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void queryNewCustGoal(Object body, IPrimitiveMap header) throws JBranchException {
			try
			{
				PMS711InputVO inputVO = (PMS711InputVO) body;
				PMS711OutputVO outputVO = new PMS711OutputVO();
				dam = this.getDataAccessManager();
				QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);			
				StringBuffer sb = new StringBuffer();
				sb.append("  SELECT PD_TYPE,                	 ");
				sb.append("         M_GOAL                	     ");
				sb.append("  FROM TBPMS_KPI_PD_NEW_CUST_GOAL	 ");
				sb.append("  WHERE DATE_YEAR = :dateYear		 ");
				sb.append(" AND    PERSON_TYPE = :PERSON_TYPE    ");
				sb.append("  ORDER BY PD_TYPE DESC             	 ");
				qc.setObject("dateYear", inputVO.getDate_year()   );
				qc.setObject("PERSON_TYPE", inputVO.getPersonType());
				qc.setQueryString(sb.toString()                   );
				List<Map<String, Object>> result = dam.exeQuery(qc);
				outputVO.setShowList(result);
				sendRtnObject(outputVO);
			}
			catch (Exception e)
			{
				logger.error(String.format("發生錯誤:%s",StringUtil.getStackTraceAsString(e)));
				throw new APException(e.getMessage());
			}
	}
	 /**
	  * 更新子項目17
	  * @param body
	  * @param header
	  * @throws JBranchException
	  */
	public void saveNewCustGoal(Object body, IPrimitiveMap header) throws JBranchException {
		try{
		PMS711InputVO inputVO = (PMS711InputVO) body;
		PMS711OutputVO outputVO = new PMS711OutputVO();
		for(int i=0; i < inputVO.getShowList().size(); i++){
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();					
		sb.append("UPDATE  TBPMS_KPI_PD_NEW_CUST_GOAL	                       ");
		sb.append(" SET    M_GOAL = :M_GOAL,                                   ");
		sb.append("  	   VERSION = 1,                                        ");
		sb.append("  	   MODIFIER = :MODIFIER,                               ");
		sb.append("  	   LASTUPDATE = SYSDATE                                ");
		sb.append(" WHERE  DATE_YEAR = :dateYear          				       ");
		sb.append(" AND    PD_TYPE = :PD_TYPE          				           ");
		sb.append("  	AND	PERSON_TYPE   = :PERSON_TYPE                       ");
		qc.setObject("PERSON_TYPE", inputVO.getPersonType());
		qc.setObject("dateYear", inputVO.getDate_year()                         );
		qc.setObject("M_GOAL", inputVO.getShowList().get(i).get("M_GOAL")       );
		qc.setObject("MODIFIER", (String) getUserVariable(FubonSystemVariableConsts.LOGINID));
		qc.setObject("PD_TYPE", inputVO.getShowList().get(i).get("PD_TYPE")     );
		qc.setQueryString(sb.toString());
		dam.exeUpdate(qc);
		}
		changeMastSet("EXAM_STATS",inputVO.getDate_year(),inputVO.getUserId(),inputVO.getPersonType(),inputVO.getIsSpecial(),inputVO.getSubProjectSeqId());
		sendRtnObject(outputVO);
		}catch (Exception e) {
			logger.error("更新失敗");
			throw new APException(e.getMessage());
		}
	}
	/**
	 * 查詢成功轉介法、商金目標(子項目21,26)
	 * @param body
	 * @param header
	 * @throws APException
	 */
	public void queryTgGoal(Object body, IPrimitiveMap header) throws APException
	{
		PMS711InputVO inputVO = (PMS711InputVO) body;
		PMS711OutputVO outputVO = new PMS711OutputVO();
		StringBuffer sb = new StringBuffer();
		dam = this.getDataAccessManager();
		try
		{
			QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb.append(" SELECT  DATE_YEAR,                             ");
			sb.append("	        AO_JOB_RANK,          	               ");
			sb.append("	        TAKING_COUNT,         	               ");
			sb.append("	        NEW_COUNT,             	               ");
			sb.append("	        TAKING_PROP,           	               ");
			sb.append("	        NEW_PROP,              	               ");
			sb.append("	        VERSION               	               ");
			sb.append("  FROM TBPMS_KPI_PD_TG_GOAL                     ");
			sb.append(" WHERE DATE_YEAR = :YEAR                        ");
			sb.append("  AND   PERSON_TYPE = :PERSON_TYPE              ");
			sb.append("  AND   SUB_PROJECT_SEQ_ID = :SUB_PROJECT_SEQ_ID");
			sb.append("  ORDER BY AO_JOB_RANK DESC                     ");
			condition.setQueryString(sb.toString());
			condition.setObject("YEAR", inputVO.getDate_year());
			condition.setObject("PERSON_TYPE", inputVO.getPersonType());
			condition.setObject("SUB_PROJECT_SEQ_ID", inputVO.getSubProjectSeqId());
			List<Map<String,Object>> resultList = dam.exeQuery(condition);
			outputVO.setShowList(resultList);
			this.sendRtnObject(outputVO);
		}
		catch (Exception e)
		{
			logger.error(String.format("發生錯誤:%s",StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}
	/**
	 * 修改成功轉介法、商金目標(子項目21,26)
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void saveTgGoal(Object body, IPrimitiveMap header) throws JBranchException
	{
		PMS711InputVO inputVO = (PMS711InputVO) body;
		PMS711OutputVO outputVO = new PMS711OutputVO();
		StringBuffer sb = new StringBuffer();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		try
		{
			sb.append("	  	UPDATE TBPMS_KPI_PD_TG_GOAL SET       "); 
			sb.append("     DATE_YEAR     = :DATE_YEAR,       	  ");
			sb.append(" 	AO_JOB_RANK   = :AO_JOB_RANK ,   	  ");
			sb.append(" 	TAKING_COUNT  = :TAKING_COUNT,   	  ");
			sb.append(" 	NEW_COUNT     = :NEW_COUNT   ,   	  ");
			if("21".equals(inputVO.getSubProjectSeqId())){
				sb.append(" 	TAKING_PROP   = :TAKING_PROP ,    ");
			}else if("26".equals(inputVO.getSubProjectSeqId())){
				sb.append(" 	NEW_PROP      = :NEW_PROP    ,    ");
			}
			sb.append(" 	VERSION       = :VERSION   ,   	      ");
			sb.append(" 	MODIFIER      = :userId    ,    	  ");
			sb.append(" 	LASTUPDATE    = sysdate     	 	  ");
			sb.append("	  	WHERE DATE_YEAR = :DATE_YEAR          "); 
			sb.append("     AND    PERSON_TYPE = :PERSON_TYPE     ");
			sb.append("     AND    SUB_PROJECT_SEQ_ID = :SUB_PROJECT_SEQ_ID");
			sb.append("	  	AND AO_JOB_RANK = :AO_JOB_RANK        "); 
			condition.setObject("DATE_YEAR", inputVO.getDate_year());
			condition.setObject("PERSON_TYPE", inputVO.getPersonType());
			condition.setObject("SUB_PROJECT_SEQ_ID", inputVO.getSubProjectSeqId());
			Object taking = (Object) inputVO.getShowList().get(0).get("TAKING_COUNT");
			Object new0 = (Object) inputVO.getShowList().get(0).get("NEW_COUNT");
			for(int i=0; i<inputVO.getShowList().size(); i++){
				condition.setObject("AO_JOB_RANK", inputVO.getShowList().get(i).get("AO_JOB_RANK"));
				condition.setObject("TAKING_COUNT", taking);
				condition.setObject("NEW_COUNT", new0);
				if("21".equals(inputVO.getSubProjectSeqId())){
					condition.setObject("TAKING_PROP", inputVO.getShowList().get(i).get("TAKING_PROP"));
				}else if("26".equals(inputVO.getSubProjectSeqId())){
					condition.setObject("NEW_PROP", inputVO.getShowList().get(i).get("NEW_PROP"));
				}
				condition.setObject("VERSION", inputVO.getShowList().get(i).get("VERSION"));
				condition.setObject("userId", (String) getUserVariable(FubonSystemVariableConsts.LOGINID));
				condition.setQueryString(sb.toString());
				dam.exeUpdate(condition);
			}
			changeMastSet("EXAM_STATS",inputVO.getDate_year(),inputVO.getUserId(),inputVO.getPersonType(),inputVO.getIsSpecial(),inputVO.getSubProjectSeqId());
			this.sendRtnObject(null);
		}
		catch (Exception e)
		{
		logger.error(String.format("發生錯誤:%s",StringUtil.getStackTraceAsString(e)));
		throw new APException("系統發生錯誤請洽系統管理員");
		}
	}
	//KPI指標設定22
	/**
	  * 查詢子項目22
	  * @param body
	  * @param header
	  * @throws JBranchException
	  */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void queryFinGoal(Object body, IPrimitiveMap header) throws JBranchException {
		try
		{
			PMS711InputVO inputVO = (PMS711InputVO) body;
			PMS711OutputVO outputVO = new PMS711OutputVO();
			dam = this.getDataAccessManager();
			QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);			
			StringBuffer sb = new StringBuffer();
			sb.append("  SELECT START_RATE,                	 ");
			sb.append("         END_RATE,                	 ");
			sb.append("         M_GOAL                	     ");
			sb.append("  FROM TBPMS_KPI_PD_FIN_GOAL	         ");
			sb.append("  WHERE DATE_YEAR = :dateYear		 ");
			sb.append(" AND    PERSON_TYPE = :PERSON_TYPE    ");
			qc.setObject("dateYear", inputVO.getDate_year()   );
			qc.setObject("PERSON_TYPE", inputVO.getPersonType());
			qc.setQueryString(sb.toString()                   );
			List<Map<String, Object>> result = dam.exeQuery(qc);
			outputVO.setShowList(result);
			sendRtnObject(outputVO);
		}
		catch (Exception e)
		{
			logger.error(String.format("發生錯誤:%s",StringUtil.getStackTraceAsString(e)));
			throw new APException(e.getMessage());
		}
}
 /**
  * 更新子項目22
  * @param body
  * @param header
  * @throws JBranchException
  */
public void saveFinGoal(Object body, IPrimitiveMap header) throws JBranchException {
	try{
	PMS711InputVO inputVO = (PMS711InputVO) body;
	PMS711OutputVO outputVO = new PMS711OutputVO();
	//刪除已存在數據
	dam = this.getDataAccessManager();
	QueryConditionIF dqc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);	
	StringBuffer dsb = new StringBuffer();
	dsb.append("  DELETE TBPMS_KPI_PD_FIN_GOAL           ");
	dsb.append("  WHERE DATE_YEAR = :DATE_YEAR           ");
	dqc.setObject("DATE_YEAR", inputVO.getDate_year()     );
	dqc.setQueryString(dsb.toString());
	dam.exeUpdate(dqc);
	
	for(int i=0; i < inputVO.getShowList().size(); i++){
	dam = this.getDataAccessManager();
	QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
	StringBuffer sb = new StringBuffer();
	sb.append("   INSERT INTO TBPMS_KPI_PD_FIN_GOAL (DATE_YEAR,	     ");
	sb.append("  		PERSON_TYPE,           					     ");
	sb.append("  		START_RATE,            				         ");
	sb.append("  		END_RATE,            				         ");
	sb.append("  		M_GOAL,            					         ");
	sb.append("  		VERSION,            						 ");
	sb.append("  		CREATETIME,             					 ");
	sb.append("  		CREATOR,             						 ");
	sb.append("  		MODIFIER,         						     ");
	sb.append("  		LASTUPDATE )             					 ");
	sb.append("  	VALUES(:DATE_YEAR,            				     ");
	sb.append("  		:PERSON_TYPE,             					 ");
	sb.append("  		:START_RATE,             				     ");
	sb.append("  		:END_RATE,             				         ");
	sb.append("  		:M_GOAL,             					     ");
	sb.append("  		:VERSION,           					     ");
	sb.append("  		SYSDATE,           				             ");
	sb.append("  		:CREATOR,            					     ");
	sb.append("  		:MODIFIER,         					         ");
	sb.append("  		SYSDATE)          				             ");
	qc.setObject("DATE_YEAR", inputVO.getDate_year());
	qc.setObject("PERSON_TYPE", "1");
	qc.setObject("START_RATE", inputVO.getShowList().get(i).get("START_RATE"));
	qc.setObject("END_RATE", inputVO.getShowList().get(i).get("END_RATE"));
	qc.setObject("M_GOAL", inputVO.getShowList().get(i).get("M_GOAL"));
	qc.setObject("VERSION", "0");
	qc.setObject("CREATOR", (String) getUserVariable(FubonSystemVariableConsts.LOGINID));
	qc.setObject("MODIFIER", (String) getUserVariable(FubonSystemVariableConsts.LOGINID));
	qc.setQueryString(sb.toString());
	dam.exeUpdate(qc);
	
	QueryConditionIF qc1 = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
	StringBuffer sb1 = new StringBuffer();
	sb1.append("   INSERT INTO TBPMS_KPI_PD_FIN_GOAL (DATE_YEAR,	     ");
	sb1.append("  		PERSON_TYPE,           					     ");
	sb1.append("  		START_RATE,            				         ");
	sb1.append("  		END_RATE,            				         ");
	sb1.append("  		M_GOAL,            					         ");
	sb1.append("  		VERSION,            						 ");
	sb1.append("  		CREATETIME,             					 ");
	sb1.append("  		CREATOR,             						 ");
	sb1.append("  		MODIFIER,         						     ");
	sb1.append("  		LASTUPDATE )             					 ");
	sb1.append("  	VALUES(:DATE_YEAR,            				     ");
	sb1.append("  		:PERSON_TYPE,             					 ");
	sb1.append("  		:START_RATE,             				     ");
	sb1.append("  		:END_RATE,             				         ");
	sb1.append("  		:M_GOAL,             					     ");
	sb1.append("  		:VERSION,           					     ");
	sb1.append("  		SYSDATE,           				             ");
	sb1.append("  		:CREATOR,            					     ");
	sb1.append("  		:MODIFIER,         					         ");
	sb1.append("  		SYSDATE)          				             ");
	qc1.setObject("DATE_YEAR", inputVO.getDate_year());
	qc1.setObject("PERSON_TYPE", "2");
	qc1.setObject("START_RATE", inputVO.getShowList().get(i).get("START_RATE"));
	qc1.setObject("END_RATE", inputVO.getShowList().get(i).get("END_RATE"));
	qc1.setObject("M_GOAL", inputVO.getShowList().get(i).get("M_GOAL"));
	qc1.setObject("VERSION", "0");
	qc1.setObject("CREATOR", (String) getUserVariable(FubonSystemVariableConsts.LOGINID));
	qc1.setObject("MODIFIER", (String) getUserVariable(FubonSystemVariableConsts.LOGINID));
	qc1.setQueryString(sb.toString());
	dam.exeUpdate(qc1);
	}
	changeMastSet("EXAM_STATS",inputVO.getDate_year(),inputVO.getUserId(),"1",inputVO.getIsSpecial(),inputVO.getSubProjectSeqId());
	changeMastSet("EXAM_STATS",inputVO.getDate_year(),inputVO.getUserId(),"2",inputVO.getIsSpecial(),inputVO.getSubProjectSeqId());
	sendRtnObject(outputVO);
	}catch (Exception e) {
		logger.error("更新失敗");
		throw new APException(e.getMessage());
	}
}
//KPI指標設定PS1
/**
  * 查詢子項目PS
  * @param body
  * @param header
  * @throws JBranchException
  */
@SuppressWarnings({ "rawtypes", "unchecked" })
public void queryPs01(Object body, IPrimitiveMap header) throws JBranchException {
	PMS711InputVO inputVO = (PMS711InputVO) body;
	PMS711OutputVO outputVO = new PMS711OutputVO();
	StringBuffer sb = new StringBuffer();
	dam = this.getDataAccessManager();
	try
	{
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb.append("  SELECT T.DATE_YEAR,										      ");		
		sb.append("  	    T.PERSON_TYPE,                                            ");
		sb.append("  	    T.AO_ORGANIZATION AS AO_JOB_RANK,                         ");
		sb.append("  	    LPAD(T.SUB_PROJECT_SEQ_ID,2,'0') AS SUB_PROJECT_SEQ_ID,   ");
		sb.append("  	    T.MONTH1,                                                 ");
		sb.append("  	    T.MONTH2,                                                 ");
		sb.append("  	    T.MONTH3,                                                 ");
		sb.append("  	    T.MONTH4,                                                 ");
		sb.append("  	    T.MONTH5,                                                 ");
		sb.append("  	    T.MONTH6,                                                 ");
		sb.append("  	    T.MONTH7,                                                 ");
		sb.append("  	    T.MONTH8,                                                 ");
		sb.append("  	    T.MONTH9,                                                 ");
		sb.append("  	    T.MONTH10,                                                ");
		sb.append("  	    T.MONTH11,                                                ");
		sb.append("  	    T.MONTH12,                                                ");
		sb.append("  	    T.MONTH13,                                                ");
		sb.append("  	    T.MONTH14,                                                ");
		sb.append("  	    T.MONTH15,                                                ");
		sb.append("  	    T.MONTH16,                                                ");
		sb.append("  	    T.MONTH17,                                                ");
		sb.append("  	    T.MONTH18,                                                ");
		sb.append("  	    T.MONTH19,                                                ");
		sb.append("  	    T.MONTH20,                                                ");
		sb.append("  	    T.MONTH21,                                                ");
		sb.append("  	    T.MONTH22,                                                ");
		sb.append("  	    T.MONTH23,                                                ");
		sb.append("  	    T.MONTH24,                                                ");
		sb.append("  	    U.MONTH1 AS FOR_LIVE1,                                    ");
		sb.append("  	    U.MONTH2 AS FOR_DEPOSIT1,                                 ");
		sb.append("  	    U.MONTH3 AS FOR_LIVE2,                                    ");
		sb.append("  	    U.MONTH4 AS FOR_DEPOSIT2,                                 ");
		sb.append("  	    NVL(T.MONTH1,0)+NVL(T.MONTH2,0)+NVL(T.MONTH3,0)+NVL(T.MONTH4,0)+");
		sb.append("  	    NVL(T.MONTH5,0)+NVL(T.MONTH6,0)+NVL(T.MONTH7,0)+NVL(T.MONTH8,0)+");
		sb.append("  	    NVL(T.MONTH9,0)+NVL(T.MONTH10,0)+NVL(T.MONTH11,0)+NVL(T.MONTH12,0)+");
		sb.append("  	    NVL(T.MONTH13,0)+NVL(T.MONTH14,0)+NVL(T.MONTH15,0)+NVL(T.MONTH16,0)+");
		sb.append("         NVL(T.MONTH17,0)+NVL(T.MONTH18,0)+NVL(T.MONTH19,0)+NVL(T.MONTH20,0)+");
		sb.append("         NVL(T.MONTH21,0)+NVL(T.MONTH22,0)+NVL(T.MONTH23,0)+NVL(T.MONTH24,0)AS TOTAL");
		sb.append("   FROM TBPMS_KPI_FC_SUB_PROJECT T,TBPMS_KPI_FC_SUB_PROJECT U");
		sb.append("   WHERE T.DATE_YEAR = :dateYear   	                              ");
		sb.append("   AND T.AO_ORGANIZATION = U.AO_ORGANIZATION    	                  ");
		sb.append("  AND    T.PERSON_TYPE = :personType      	                      ");
		sb.append("  AND    T.SUB_PROJECT_SEQ_ID = :subProjectSeqId    	              ");
		sb.append("  AND    U.MEMO = '2'  	                                          ");
		sb.append("  AND    T.MEMO = '1'    	                                      ");
		sb.append("  ORDER BY AO_JOB_RANK desc   	                                      ");
		
		
		/*sb.append("  FROM (SELECT * FROM TBPMS_KPI_FC_SUB_PROJECT                   ");
		sb.append("  WHERE DATE_YEAR = :dateYear		                            ");
		sb.append("  AND    PERSON_TYPE = :personType      	                        ");
		sb.append("  AND    SUB_PROJECT_SEQ_ID = :subProjectSeqId    	            ");
		sb.append("  AND    MEMO = 0 )T ,    	                                    ");
		sb.append("   (SELECT * FROM TBPMS_KPI_FC_SUB_PROJECT                       ");
		sb.append("  WHERE DATE_YEAR = :dateYear		                            ");
		sb.append("  AND    PERSON_TYPE = :personType      	                        ");
		sb.append("  AND    SUB_PROJECT_SEQ_ID = :subProjectSeqId    	            ");
		sb.append("  AND    MEMO = 1 )U    	                                        ");
		sb.append("  WHERE T.AO_JOB_RANK = U.AO_JOB_RANK    	                    ");*/
		qc.setObject("dateYear", inputVO.getDate_year());
		qc.setObject("personType", inputVO.getPersonType());
		qc.setObject("subProjectSeqId", inputVO.getSubProjectSeqId());
		
		
		qc.setQueryString(sb.toString());
		List<Map<String, Object>> resultList = dam.exeQuery(qc);
		outputVO.setShowList(resultList);
		this.sendRtnObject(outputVO);
	}
	catch (Exception e)
	{
		logger.error(String.format("發生錯誤:%s",StringUtil.getStackTraceAsString(e)));
		throw new APException(e.getMessage());
	}
}
//KPI指標設定PS2
/**
* 查詢子項目PS
* @param body
* @param header
* @throws JBranchException
*/
@SuppressWarnings({ "rawtypes", "unchecked" })
public void queryPs02(Object body, IPrimitiveMap header) throws JBranchException {
	PMS711InputVO inputVO = (PMS711InputVO) body;
	PMS711OutputVO outputVO = new PMS711OutputVO();
	StringBuffer sb = new StringBuffer();
	dam = this.getDataAccessManager();
	try
	{
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb.append("  SELECT DATE_YEAR,												");		
		sb.append("  	    PERSON_TYPE,                                            ");
		sb.append("  	    AO_ORGANIZATION AS BRANCH_NBR,                          ");
		sb.append("  	    LPAD(SUB_PROJECT_SEQ_ID,2,'0') AS SUB_PROJECT_SEQ_ID,   ");
		sb.append("  	    MONTH1,                                                 ");
		sb.append("  	    MONTH2,                                                 ");
		sb.append("  	    MONTH3,                                                 ");
		sb.append("  	    MONTH4,                                                 ");
		sb.append("  	    MONTH5,                                                 ");
		sb.append("  	    MONTH6,                                                 ");
		sb.append("  	    MONTH7,                                                 ");
		sb.append("  	    MONTH8,                                                 ");
		sb.append("  	    MONTH9,                                                 ");
		sb.append("  	    MONTH10,                                                ");
		sb.append("  	    MONTH11,                                                ");
		sb.append("  	    MONTH12,                                                ");
		sb.append("  	    MONTH13,                                                ");
		sb.append("  	    MONTH14,                                                ");
		sb.append("  	    MONTH15,                                                ");
		sb.append("  	    MONTH16,                                                ");
		sb.append("  	    MONTH17,                                                ");
		sb.append("  	    MONTH18,                                                ");
		sb.append("  	    MONTH19,                                                ");
		sb.append("  	    MONTH20,                                                ");
		sb.append("  	    MONTH21,                                                ");
		sb.append("  	    MONTH22,                                                ");
		sb.append("  	    MONTH23,                                                ");
		sb.append("  	    MONTH24,                                                ");
		sb.append("  	    NVL(MONTH1,0)+NVL(MONTH2,0)+NVL(MONTH3,0)+NVL(MONTH4,0)+");
		sb.append("  	    NVL(MONTH5,0)+NVL(MONTH6,0)+NVL(MONTH7,0)+NVL(MONTH8,0)+");
		sb.append("  	    NVL(MONTH9,0)+NVL(MONTH10,0)+NVL(MONTH11,0)+NVL(MONTH12,0)+");
		sb.append("  	    NVL(MONTH13,0)+NVL(MONTH14,0)+NVL(MONTH15,0)+NVL(MONTH16,0)+");
		sb.append("         NVL(MONTH17,0)+NVL(MONTH18,0)+NVL(MONTH19,0)+NVL(MONTH20,0)+");
		sb.append("         NVL(MONTH21,0)+NVL(MONTH22,0)+NVL(MONTH23,0)+NVL(MONTH24,0)AS TOTAL");
		sb.append("  FROM (SELECT * FROM TBPMS_KPI_FC_SUB_PROJECT                   ");
		sb.append("  WHERE DATE_YEAR = :dateYear		                            ");
		sb.append("  AND    PERSON_TYPE = :personType      	                        ");
		sb.append("  AND    SUB_PROJECT_SEQ_ID = :subProjectSeqId )T     	        ");
		sb.append("  ORDER BY BRANCH_NBR desc   	                                      ");
		qc.setObject("dateYear", inputVO.getDate_year());
		qc.setObject("personType", inputVO.getPersonType());
		qc.setObject("subProjectSeqId", inputVO.getSubProjectSeqId());
		
		
		qc.setQueryString(sb.toString());
		List<Map<String, Object>> resultList = dam.exeQuery(qc);
		outputVO.setShowList(resultList);
		this.sendRtnObject(outputVO);
	}
	catch (Exception e)
	{
		logger.error(String.format("發生錯誤:%s",StringUtil.getStackTraceAsString(e)));
		throw new APException(e.getMessage());
	}
}
	//KPI指標設定PS11
	/**
	  * 查詢子項目PS
	  * @param body
	  * @param header
	  * @throws JBranchException
	  */
@SuppressWarnings({ "rawtypes", "unchecked" })
public void queryPs11(Object body, IPrimitiveMap header) throws JBranchException {
	PMS711InputVO inputVO = (PMS711InputVO) body;
	PMS711OutputVO outputVO = new PMS711OutputVO();
	StringBuffer sb = new StringBuffer();
	dam = this.getDataAccessManager();
	try
	{
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb.append("  SELECT DATE_YEAR,												");		
		sb.append("  	    PERSON_TYPE,                                            ");
		sb.append("  	    AO_ORGANIZATION AS BRANCH_NBR,                          ");
		sb.append("  	    U.BRANCH_NAME AS BRANCH_NAME,                           ");
		sb.append("  	    U.BRANCH_CLS AS GROUP_TYPE,                             ");
		sb.append("  	    LPAD(SUB_PROJECT_SEQ_ID,2,'0') AS SUB_PROJECT_SEQ_ID,   ");
		sb.append("  	    MONTH1,                                                 ");
		sb.append("  	    MONTH2,                                                 ");
		sb.append("  	    MONTH3,                                                 ");
		sb.append("  	    MONTH4,                                                 ");
		sb.append("  	    MONTH5,                                                 ");
		sb.append("  	    MONTH6,                                                 ");
		sb.append("  	    MONTH7,                                                 ");
		sb.append("  	    MONTH8,                                                 ");
		sb.append("  	    MONTH9,                                                 ");
		sb.append("  	    MONTH10,                                                ");
		sb.append("  	    MONTH11,                                                ");
		sb.append("  	    MONTH12,                                                ");
		sb.append("  	    MONTH13,                                                ");
		sb.append("  	    MONTH14,                                                ");
		sb.append("  	    MONTH15,                                                ");
		sb.append("  	    MONTH16,                                                ");
		sb.append("  	    MONTH17,                                                ");
		sb.append("  	    MONTH18,                                                ");
		sb.append("  	    MONTH19,                                                ");
		sb.append("  	    MONTH20,                                                ");
		sb.append("  	    MONTH21,                                                ");
		sb.append("  	    MONTH22,                                                ");
		sb.append("  	    MONTH23,                                                ");
		sb.append("  	    MONTH24,                                                ");
		sb.append("  	    NVL(MONTH1,0)+NVL(MONTH2,0)+NVL(MONTH3,0)+NVL(MONTH4,0)+");
		sb.append("  	    NVL(MONTH5,0)+NVL(MONTH6,0)+NVL(MONTH7,0)+NVL(MONTH8,0)+");
		sb.append("  	    NVL(MONTH9,0)+NVL(MONTH10,0)+NVL(MONTH11,0)+NVL(MONTH12,0)+");
		sb.append("  	    NVL(MONTH13,0)+NVL(MONTH14,0)+NVL(MONTH15,0)+NVL(MONTH16,0)+");
		sb.append("         NVL(MONTH17,0)+NVL(MONTH18,0)+NVL(MONTH19,0)+NVL(MONTH20,0)+");
		sb.append("         NVL(MONTH21,0)+NVL(MONTH22,0)+NVL(MONTH23,0)+NVL(MONTH24,0)AS TOTAL");
		sb.append("  FROM (SELECT * FROM TBPMS_KPI_FC_SUB_PROJECT                   ");
		sb.append("  WHERE DATE_YEAR = :dateYear		                            ");
		sb.append("  AND    PERSON_TYPE = :personType      	                        ");
		sb.append("  AND    SUB_PROJECT_SEQ_ID = :subProjectSeqId )T     	        ");
		sb.append("  LEFT JOIN TBPMS_ORG_REC_N U ON T.AO_ORGANIZATION = U.BRANCH_NBR");
		qc.setObject("dateYear", inputVO.getDate_year());
		qc.setObject("personType", inputVO.getPersonType());
		qc.setObject("subProjectSeqId", inputVO.getSubProjectSeqId());
		qc.setQueryString(sb.toString());
		List<Map<String, Object>> resultList = dam.exeQuery(qc);
		outputVO.setShowList(resultList);
		this.sendRtnObject(outputVO);
	}
	catch (Exception e)
	{
		logger.error(String.format("發生錯誤:%s",StringUtil.getStackTraceAsString(e)));
		throw new APException(e.getMessage());
	}
}
	/*@SuppressWarnings({ "rawtypes", "unchecked" })
	public void queryPs11(Object body, IPrimitiveMap header) throws JBranchException {
		PMS711InputVO inputVO = (PMS711InputVO) body;
		PMS711OutputVO outputVO = new PMS711OutputVO();
		StringBuffer sb = new StringBuffer();
		dam = this.getDataAccessManager();
		try
		{
			QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb.append(" SELECT                                ");
			sb.append(" 	DATE_YEAR,                        ");
			sb.append(" 	BRANCH_NBR,                       ");
			sb.append(" 	BRANCH_NAME,                      ");
			sb.append(" 	GROUP_TYPE,                       ");
			sb.append(" 	MONTH1_GOAL,                      ");
			sb.append(" 	MONTH2_GOAL,                      ");
			sb.append(" 	MONTH3_GOAL,                      ");
			sb.append(" 	MONTH4_GOAL,                      ");
			sb.append(" 	MONTH5_GOAL,                      ");
			sb.append(" 	MONTH6_GOAL,                      ");
			sb.append(" 	MONTH7_GOAL,                      ");
			sb.append(" 	MONTH8_GOAL,                      ");
			sb.append(" 	MONTH9_GOAL,                      ");
			sb.append(" 	MONTH10_GOAL,                     ");
			sb.append(" 	MONTH11_GOAL,                     ");
			sb.append(" 	MONTH12_GOAL,                     ");
			sb.append(" 	TOTAL,                            ");
			sb.append(" 	VERSION                           ");
			sb.append(" FROM TBPMS_KPI_PS_LINS_GOAL           ");
			sb.append(" WHERE DATE_YEAR = :date_year          ");
			sb.append(" AND    PERSON_TYPE = :PERSON_TYPE     ");
			sb.append("ORDER BY BRANCH_NBR                    ");
			condition.setObject("date_year", inputVO.getDate_year());
			condition.setObject("PERSON_TYPE", inputVO.getPersonType());
			condition.setQueryString(sb.toString());
			List<Map<String,Object>> resultList = dam.exeQuery(condition);
			outputVO.setShowList(resultList);
			this.sendRtnObject(outputVO);
		}
		catch (Exception e)
		{
			logger.error(String.format("發生錯誤:%s",StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}*/
	 /**
	  * 更新子項目Ps11
	  * @param body
	  * @param header
	  * @throws JBranchException
	  */
	public void savePs11(Object body, IPrimitiveMap header) throws JBranchException {
			PMS711InputVO inputVO = (PMS711InputVO) body;
			PMS711OutputVO outputVO = new PMS711OutputVO();
			StringBuffer sb = new StringBuffer();
			dam = this.getDataAccessManager();
			try
			{
				sb.append("	  	UPDATE TBPMS_KPI_PS_LINS_GOAL SET      "); 
				sb.append("			  MONTH1_GOAL =  :MONTH1_GOAL,     "); 
				sb.append("	  		  MONTH2_GOAL =  :MONTH2_GOAL,     "); 	 
				sb.append("	  		  MONTH3_GOAL =  :MONTH3_GOAL,     ");    
				sb.append("	  		  MONTH4_GOAL =  :MONTH4_GOAL,     ");     
				sb.append("	  		  MONTH5_GOAL =  :MONTH5_GOAL,     ");     
				sb.append("	  		  MONTH6_GOAL =  :MONTH6_GOAL,     "); 
			    sb.append("           MONTH7_GOAL  = :MONTH7_GOAL,     "); 
			    sb.append("           MONTH8_GOAL  = :MONTH8_GOAL,     "); 
			    sb.append("           MONTH9_GOAL  = :MONTH9_GOAL,     "); 
			    sb.append("           MONTH10_GOAL = :MONTH10_GOAL,    "); 
			    sb.append("           MONTH11_GOAL = :MONTH11_GOAL,    "); 
			    sb.append("           MONTH12_GOAL = :MONTH12_GOAL,    "); 
			    sb.append("           TOTAL        = :TOTAL,           "); 
				sb.append("			  VERSION = 1,                     ");
				sb.append("	  		  MODIFIER = :userId,         	   "); 
				sb.append("	  		  LASTUPDATE = sysdate        	   "); 
				sb.append("	  	WHERE DATE_YEAR = :DATE_YEAR      	   "); 
				sb.append("	  	AND BRANCH_NBR = :BRANCH_NBR           "); 
				sb.append("     AND GROUP_TYPE = :GROUP_TYPE           ");
				for(int i=0; i<inputVO.getShowList().size(); i++){
					QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					condition.setObject("MONTH1_GOAL", inputVO.getShowList().get(i).get("MONTH1_GOAL").toString().replace(".0",""));
					condition.setObject("MONTH2_GOAL", inputVO.getShowList().get(i).get("MONTH2_GOAL").toString().replace(".0",""));
					condition.setObject("MONTH3_GOAL", inputVO.getShowList().get(i).get("MONTH3_GOAL").toString().replace(".0",""));
					condition.setObject("MONTH4_GOAL", inputVO.getShowList().get(i).get("MONTH4_GOAL").toString().replace(".0",""));
					condition.setObject("MONTH5_GOAL", inputVO.getShowList().get(i).get("MONTH5_GOAL").toString().replace(".0",""));
					condition.setObject("MONTH6_GOAL", inputVO.getShowList().get(i).get("MONTH6_GOAL").toString().replace(".0",""));
					condition.setObject("MONTH7_GOAL", inputVO.getShowList().get(i).get("MONTH7_GOAL").toString().replace(".0",""));
					condition.setObject("MONTH8_GOAL", inputVO.getShowList().get(i).get("MONTH8_GOAL").toString().replace(".0",""));
					condition.setObject("MONTH9_GOAL", inputVO.getShowList().get(i).get("MONTH9_GOAL").toString().replace(".0",""));
					condition.setObject("MONTH10_GOAL", inputVO.getShowList().get(i).get("MONTH10_GOAL").toString().replace(".0",""));
					condition.setObject("MONTH11_GOAL", inputVO.getShowList().get(i).get("MONTH11_GOAL").toString().replace(".0",""));
					condition.setObject("MONTH12_GOAL", inputVO.getShowList().get(i).get("MONTH12_GOAL").toString().replace(".0",""));
					condition.setObject("TOTAL", inputVO.getShowList().get(i).get("TOTAL").toString().replace(".0",""));
					condition.setObject("userId", (String) getUserVariable(FubonSystemVariableConsts.LOGINID));
					condition.setObject("DATE_YEAR", inputVO.getShowList().get(i).get("DATE_YEAR"));
					condition.setObject("GROUP_TYPE", inputVO.getShowList().get(i).get("GROUP_TYPE"));
					condition.setObject("BRANCH_NBR", inputVO.getShowList().get(i).get("BRANCH_NBR"));
					condition.setQueryString(sb.toString());
					dam.exeUpdate(condition);
				}
				changeMastSet("EXAM_STATS",inputVO.getDate_year(),inputVO.getUserId(),inputVO.getPersonType(),inputVO.getIsSpecial(),inputVO.getSubProjectSeqId());
				this.sendRtnObject(null);
		}catch (Exception e) {
			logger.error("更新失敗");
			throw new APException(e.getMessage());
		}
	}
	//KPI指標設定PS12
	/**
	  * 查詢子項目PS
	  * @param body
	  * @param header
	  * @throws JBranchException
	  */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void queryPs12(Object body, IPrimitiveMap header) throws JBranchException {
		PMS711InputVO inputVO = (PMS711InputVO) body;
		PMS711OutputVO outputVO = new PMS711OutputVO();
		StringBuffer sb = new StringBuffer();
		dam = this.getDataAccessManager();
		try
		{
			QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb.append("  SELECT DATE_YEAR,												");		
			sb.append("  	    PERSON_TYPE,                                            ");
			sb.append("  	    AO_ORGANIZATION AS BRANCH_NBR,                          ");
			sb.append("  	    LPAD(SUB_PROJECT_SEQ_ID,2,'0') AS SUB_PROJECT_SEQ_ID,   ");
			sb.append("  	    substr(MEMO,3,LENGTH(MEMO)) AS PS_EMP_ID,               ");
			sb.append("  	    MONTH1,                                                 ");
			sb.append("  	    MONTH2,                                                 ");
			sb.append("  	    MONTH3,                                                 ");
			sb.append("  	    MONTH4,                                                 ");
			sb.append("  	    MONTH5,                                                 ");
			sb.append("  	    MONTH6,                                                 ");
			sb.append("  	    MONTH7,                                                 ");
			sb.append("  	    MONTH8,                                                 ");
			sb.append("  	    MONTH9,                                                 ");
			sb.append("  	    MONTH10,                                                ");
			sb.append("  	    MONTH11,                                                ");
			sb.append("  	    MONTH12,                                                ");
			sb.append("  	    MONTH13,                                                ");
			sb.append("  	    MONTH14,                                                ");
			sb.append("  	    MONTH15,                                                ");
			sb.append("  	    MONTH16,                                                ");
			sb.append("  	    MONTH17,                                                ");
			sb.append("  	    MONTH18,                                                ");
			sb.append("  	    MONTH19,                                                ");
			sb.append("  	    MONTH20,                                                ");
			sb.append("  	    MONTH21,                                                ");
			sb.append("  	    MONTH22,                                                ");
			sb.append("  	    MONTH23,                                                ");
			sb.append("  	    MONTH24,                                                ");
			sb.append("  	    NVL(MONTH1,0)+NVL(MONTH2,0)+NVL(MONTH3,0)+NVL(MONTH4,0)+NVL(MONTH5,0)+NVL(MONTH6,0)+NVL(MONTH7,0)+NVL(MONTH8,0)+NVL(MONTH9,0)+NVL(MONTH10,0)+NVL(MONTH11,0)+NVL(MONTH12,0)+   ");
			sb.append("         NVL(MONTH13,0)+NVL(MONTH14,0)+NVL(MONTH15,0)+NVL(MONTH16,0)+NVL(MONTH17,0)+NVL(MONTH18,0)+NVL(MONTH19,0)+NVL(MONTH20,0)+NVL(MONTH21,0)+NVL(MONTH22,0)+NVL(MONTH23,0)+NVL(MONTH24,0)AS TOTAL");
			sb.append("  FROM TBPMS_KPI_FC_SUB_PROJECT                                  ");
			sb.append("  WHERE DATE_YEAR = :dateYear		                            ");
			sb.append("  AND    PERSON_TYPE = :personType      	                        ");
			sb.append("  AND    SUB_PROJECT_SEQ_ID = :subProjectSeqId      	            ");
			sb.append("  AND    substr(MEMO,1,1) = :TS_TYPE      	                    ");
			qc.setObject("dateYear", inputVO.getDate_year());
			qc.setObject("personType", inputVO.getPersonType());
			qc.setObject("subProjectSeqId", inputVO.getSubProjectSeqId());
			qc.setObject("TS_TYPE", inputVO.getTsType());
			qc.setQueryString(sb.toString());
			List<Map<String, Object>> resultList = dam.exeQuery(qc);
			outputVO.setShowList(resultList);
			this.sendRtnObject(outputVO);
		}
		catch (Exception e)
		{
			logger.error(String.format("發生錯誤:%s",StringUtil.getStackTraceAsString(e)));
			throw new APException(e.getMessage());
		}
	}
	 /**
	  * 更新子項目Ps12
	  * @param body
	  * @param header
	  * @throws JBranchException
	  */
	public void savePs12(Object body, IPrimitiveMap header) throws JBranchException {
			PMS711InputVO inputVO = (PMS711InputVO) body;
			PMS711OutputVO outputVO = new PMS711OutputVO();
			StringBuffer sb = new StringBuffer();
			dam = this.getDataAccessManager();
			try
			{
				sb.append("	  	UPDATE TBPMS_KPI_PS_HOU_NEW SET        "); 
				sb.append("			  MONTH1_GOAL =  :MONTH1_GOAL,     "); 
				sb.append("	  		  MONTH2_GOAL =  :MONTH2_GOAL,     "); 	 
				sb.append("	  		  MONTH3_GOAL =  :MONTH3_GOAL,     ");    
				sb.append("	  		  MONTH4_GOAL =  :MONTH4_GOAL,     ");     
				sb.append("	  		  MONTH5_GOAL =  :MONTH5_GOAL,     ");     
				sb.append("	  		  MONTH6_GOAL =  :MONTH6_GOAL,     "); 
			    sb.append("           MONTH7_GOAL  = :MONTH7_GOAL,     "); 
			    sb.append("           MONTH8_GOAL  = :MONTH8_GOAL,     "); 
			    sb.append("           MONTH9_GOAL  = :MONTH9_GOAL,     "); 
			    sb.append("           MONTH10_GOAL = :MONTH10_GOAL,    "); 
			    sb.append("           MONTH11_GOAL = :MONTH11_GOAL,    "); 
			    sb.append("           MONTH12_GOAL = :MONTH12_GOAL,    "); 
			    sb.append("           TOTAL        = :TOTAL,           "); 
				sb.append("			  VERSION = 1,                     ");
				sb.append("	  		  MODIFIER = :userId,         	   "); 
				sb.append("	  		  LASTUPDATE = sysdate        	   "); 
				sb.append("	  	WHERE DATE_YEAR = :DATE_YEAR      	   "); 
				sb.append("	  	AND BRANCH_NBR = :BRANCH_NBR           "); 
				sb.append("     AND PS_EMP_ID = :PS_EMP_ID             ");
				sb.append("     AND TS_TYPE = :TS_TYPE                 ");
				for(int i=0; i<inputVO.getShowList().size(); i++){
					QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					condition.setObject("MONTH1_GOAL", inputVO.getShowList().get(i).get("MONTH1_GOAL").toString().replace(".0",""));
					condition.setObject("MONTH2_GOAL", inputVO.getShowList().get(i).get("MONTH2_GOAL").toString().replace(".0",""));
					condition.setObject("MONTH3_GOAL", inputVO.getShowList().get(i).get("MONTH3_GOAL").toString().replace(".0",""));
					condition.setObject("MONTH4_GOAL", inputVO.getShowList().get(i).get("MONTH4_GOAL").toString().replace(".0",""));
					condition.setObject("MONTH5_GOAL", inputVO.getShowList().get(i).get("MONTH5_GOAL").toString().replace(".0",""));
					condition.setObject("MONTH6_GOAL", inputVO.getShowList().get(i).get("MONTH6_GOAL").toString().replace(".0",""));
					condition.setObject("MONTH7_GOAL", inputVO.getShowList().get(i).get("MONTH7_GOAL").toString().replace(".0",""));
					condition.setObject("MONTH8_GOAL", inputVO.getShowList().get(i).get("MONTH8_GOAL").toString().replace(".0",""));
					condition.setObject("MONTH9_GOAL", inputVO.getShowList().get(i).get("MONTH9_GOAL").toString().replace(".0",""));
					condition.setObject("MONTH10_GOAL", inputVO.getShowList().get(i).get("MONTH10_GOAL").toString().replace(".0",""));
					condition.setObject("MONTH11_GOAL", inputVO.getShowList().get(i).get("MONTH11_GOAL").toString().replace(".0",""));
					condition.setObject("MONTH12_GOAL", inputVO.getShowList().get(i).get("MONTH12_GOAL").toString().replace(".0",""));
					condition.setObject("TOTAL", inputVO.getShowList().get(i).get("TOTAL").toString().replace(".0",""));
					condition.setObject("userId", (String) getUserVariable(FubonSystemVariableConsts.LOGINID));
					condition.setObject("DATE_YEAR", inputVO.getShowList().get(i).get("DATE_YEAR"));
					condition.setObject("PS_EMP_ID", inputVO.getShowList().get(i).get("PS_EMP_ID"));
					condition.setObject("BRANCH_NBR", inputVO.getShowList().get(i).get("BRANCH_NBR"));
					condition.setObject("TS_TYPE", inputVO.getTsType());
					condition.setQueryString(sb.toString());
					dam.exeUpdate(condition);
				}
				changeMastSet("EXAM_STATS",inputVO.getDate_year(),inputVO.getUserId(),inputVO.getPersonType(),inputVO.getIsSpecial(),inputVO.getSubProjectSeqId());
				this.sendRtnObject(null);
		}catch (Exception e) {
			logger.error("更新失敗");
			throw new APException(e.getMessage());
		}
	}
	
	/**
	 * 查詢成功轉介法、商金目標(PRO21)
	 * @param body
	 * @param header
	 * @throws APException
	 */
	public void queryPro21(Object body, IPrimitiveMap header) throws APException
	{
		PMS711InputVO inputVO = (PMS711InputVO) body;
		PMS711OutputVO outputVO = new PMS711OutputVO();
		StringBuffer sb = new StringBuffer();
		dam = this.getDataAccessManager();
		try
		{
			 
			QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb.append(" SELECT  DATE_YEAR,                             ");
			sb.append("	        CHAR_TYPE,          	               ");
			sb.append("	        TAKING_COUNT,         	               ");
			sb.append("	        NEW_COUNT,             	               ");
			sb.append("	        TAKING_PROP,           	               ");
			sb.append("	        NEW_PROP,              	               ");
			sb.append("	        VERSION               	               ");
			sb.append("  FROM TBPMS_KPI_PRO_TG_GOAL                    ");
			sb.append(" WHERE DATE_YEAR = :YEAR                        ");
			sb.append("  AND   SUB_PROJECT_SEQ_ID = :SUB_PROJECT_SEQ_ID");
			sb.append("  ORDER BY CHAR_TYPE                            ");
			condition.setQueryString(sb.toString());
			condition.setObject("YEAR", inputVO.getDate_year());
			condition.setObject("SUB_PROJECT_SEQ_ID", inputVO.getSubProjectSeqId());
			List<Map<String,Object>> resultList = dam.exeQuery(condition);
			outputVO.setShowList(resultList);
			this.sendRtnObject(outputVO);
		}
		catch (Exception e)
		{
			logger.error(String.format("發生錯誤:%s",StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}
	
	/**
	 * 修改成功轉介法、商金目標(子項目21)
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void savePro21(Object body, IPrimitiveMap header) throws JBranchException
	{
		PMS711InputVO inputVO = (PMS711InputVO) body;
		PMS711OutputVO outputVO = new PMS711OutputVO();
		StringBuffer sb = new StringBuffer();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		try
		{
			sb.append("	  	UPDATE TBPMS_KPI_PRO_TG_GOAL SET      "); 
			sb.append("     DATE_YEAR     = :DATE_YEAR,   	      ");
			sb.append(" 	CHAR_TYPE   = :CHAR_TYPE,   	      ");
			sb.append(" 	TAKING_COUNT  = :TAKING_COUNT,   	  ");
			sb.append(" 	NEW_COUNT     = :NEW_COUNT,   	      ");
			if("21".equals(inputVO.getSubProjectSeqId())){
				sb.append(" 	TAKING_PROP   = :TAKING_PROP ,    ");
			}else if("26".equals(inputVO.getSubProjectSeqId())){
				sb.append(" 	NEW_PROP      = :NEW_PROP    ,    ");
			}
			sb.append(" 	VERSION       = :VERSION,   	      ");
			sb.append(" 	MODIFIER      = :userId,    	      ");
			sb.append(" 	LASTUPDATE    = sysdate     	 	  ");
			sb.append("	  	WHERE DATE_YEAR = :DATE_YEAR          "); 
			sb.append("	  	AND CHAR_TYPE = :CHAR_TYPE            "); 
			sb.append("	  	AND SUB_PROJECT_SEQ_ID = :subProjectSeqId");
			
			condition.setObject("DATE_YEAR", inputVO.getDate_year());
			condition.setObject("subProjectSeqId", inputVO.getSubProjectSeqId());
			Object taking = (Object) inputVO.getShowList().get(0).get("TAKING_COUNT");
			Object new0 = (Object) inputVO.getShowList().get(0).get("NEW_COUNT");
			for(int i=0; i<inputVO.getShowList().size(); i++){
				/*inputVO.getShowList().get(i).put("TAKING_PROP", taking); 
				inputVO.getShowList().get(i).put("NEW_PROP", new0); */
				condition.setObject("CHAR_TYPE", inputVO.getShowList().get(i).get("CHAR_TYPE"));
				condition.setObject("TAKING_COUNT", taking);
				condition.setObject("NEW_COUNT", new0);
				if("21".equals(inputVO.getSubProjectSeqId())){
					condition.setObject("TAKING_PROP", inputVO.getShowList().get(i).get("TAKING_PROP"));
				}else if("26".equals(inputVO.getSubProjectSeqId())){
					condition.setObject("NEW_PROP", inputVO.getShowList().get(i).get("NEW_PROP"));
				}
				condition.setObject("VERSION", "0");
				condition.setObject("userId", (String) getUserVariable(FubonSystemVariableConsts.LOGINID));
				condition.setQueryString(sb.toString());
				dam.exeUpdate(condition);
			}
			//changeMastSet("EXAM_STATS",inputVO.getDate_year(),inputVO.getUserId(),inputVO.getPersonType(),inputVO.getBranchs(),inputVO.getSubProjectSeqId());
			this.sendRtnObject(null);
		}
		catch (Exception e)
		{
		logger.error(String.format("發生錯誤:%s",StringUtil.getStackTraceAsString(e)));
		throw new APException("系統發生錯誤請洽系統管理員");
		}
	}
	
	/**
	  * 查詢子項目PS1
	  * @param body
	  * @param header
	  * @throws JBranchException
	  */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void queryMet01(Object body, IPrimitiveMap header) throws JBranchException {
		PMS711InputVO inputVO = (PMS711InputVO) body;
		PMS711OutputVO outputVO = new PMS711OutputVO();
		StringBuffer sb = new StringBuffer();
		dam = this.getDataAccessManager();
		try
		{
			QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb.append("  SELECT DATE_YEAR,												");		
			sb.append("  	    PERSON_TYPE,                                            ");
			sb.append("  	    AO_ORGANIZATION AS BRANCH_NBR,                          ");
			sb.append("  	    U.BRANCH_NAME AS BRANCH_NAME,                           ");
			sb.append("  	    U.BRANCH_CLS AS GROUP_TYPE,                             ");
			sb.append("  	    LPAD(SUB_PROJECT_SEQ_ID,2,'0') AS SUB_PROJECT_SEQ_ID,   ");
			sb.append("  	    MONTH1,                                                 ");
			sb.append("  	    MONTH2,                                                 ");
			sb.append("  	    MONTH3,                                                 ");
			sb.append("  	    MONTH4,                                                 ");
			sb.append("  	    MONTH5,                                                 ");
			sb.append("  	    MONTH6,                                                 ");
			sb.append("  	    MONTH7,                                                 ");
			sb.append("  	    MONTH8,                                                 ");
			sb.append("  	    MONTH9,                                                 ");
			sb.append("  	    MONTH10,                                                ");
			sb.append("  	    MONTH11,                                                ");
			sb.append("  	    MONTH12,                                                ");
			sb.append("  	    MONTH13,                                                ");
			sb.append("  	    MONTH14,                                                ");
			sb.append("  	    MONTH15,                                                ");
			sb.append("  	    MONTH16,                                                ");
			sb.append("  	    MONTH17,                                                ");
			sb.append("  	    MONTH18,                                                ");
			sb.append("  	    MONTH19,                                                ");
			sb.append("  	    MONTH20,                                                ");
			sb.append("  	    MONTH21,                                                ");
			sb.append("  	    MONTH22,                                                ");
			sb.append("  	    MONTH23,                                                ");
			sb.append("  	    MONTH24                                                 ");
			
			sb.append("  FROM (SELECT * FROM TBPMS_KPI_FC_SUB_PROJECT                   ");
			sb.append("  WHERE DATE_YEAR = :dateYear		                            ");
			sb.append("  AND    PERSON_TYPE = :personType      	                        ");
			sb.append("  AND    SUB_PROJECT_SEQ_ID = :subProjectSeqId      	            ");
			sb.append("  AND    MEMO = :FLAG)T      	                                ");
			sb.append("  LEFT JOIN TBPMS_ORG_REC_N U ON T.AO_ORGANIZATION = U.BRANCH_NBR");
			sb.append("   AND CASE WHEN TO_DATE(:JJJ,'YYYYMMDD') = LAST_DAY(TO_DATE(:JJJ,'YYYYMMDD')) THEN TO_DATE(:JJJ,'YYYYMMDD') ELSE TRUNC(TO_DATE(:JJJ,'YYYYMMDD'),'MM') - 1 END BETWEEN U.START_TIME AND U.END_TIME");
			sb.append("  ORDER BY BRANCH_NBR                          ");
			qc.setObject("dateYear", inputVO.getDate_year());
			qc.setObject("personType", inputVO.getPersonType());
			qc.setObject("subProjectSeqId", inputVO.getSubProjectSeqId());
			qc.setObject("FLAG", inputVO.getFlag());
			qc.setObject("JJJ", inputVO.getEDate1());
			qc.setQueryString(sb.toString());
			
			ResultIF largeAgrList = dam.executePaging(qc, inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
			int totalPage_i = largeAgrList.getTotalPage(); // 分頁用
			int totalRecord_i = largeAgrList.getTotalRecord(); // 分頁用
			outputVO.setShowList(largeAgrList); // data
			outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());// 當前頁次
			outputVO.setTotalPage(totalPage_i);// 總頁次
			outputVO.setTotalRecord(totalRecord_i);// 總筆數
						
			this.sendRtnObject(outputVO);
		}
		catch (Exception e)
		{
			logger.error(String.format("發生錯誤:%s",StringUtil.getStackTraceAsString(e)));
			throw new APException(e.getMessage());
		}
	}
	 /**
	  * 更新子項目met01
	  * @param body
	  * @param header
	  * @throws JBranchException
	  */
	public void saveMet01(Object body, IPrimitiveMap header) throws JBranchException {
			PMS711InputVO inputVO = (PMS711InputVO) body;
			PMS711OutputVO outputVO = new PMS711OutputVO();
			StringBuffer sb = new StringBuffer();
			dam = this.getDataAccessManager();
			try
			{
				sb.append("	UPDATE  TBPMS_KPI_CK_OR_TB SET          ");
				sb.append("			MG1 = :MG1 ,                    ");
				sb.append("			MG2 = :MG2 ,                    ");
				sb.append("			MG3 = :MG3 ,                    ");
				sb.append("			MG4 = :MG4 ,                    ");
				sb.append("			MG5 = :MG5 ,                    ");
				sb.append("			MG6 = :MG6 ,                    ");
				sb.append("			MG7 = :MG7 ,                    ");
				sb.append("			MG8 = :MG8 ,                    ");
				sb.append("			MG9 = :MG9 ,                    ");
				sb.append("			MG10 = :MG10 ,                  ");
				sb.append("			MG11 = :MG11 ,                  ");
				sb.append("			MG12 = :MG12 ,       	        ");
				sb.append("			MODIFIER = :userId,             ");
				sb.append("			LASTUPDATE = SYSDATE            ");
				sb.append(" WHERE DATE_YEAR = :DATE_YEAR            ");
				sb.append(" AND PERSON_TYPE = :PERSON_TYPE          ");
				sb.append(" AND BRANCH_NBR = :BRANCH_NBR            ");
				sb.append(" AND GROUP_TYPE = :GROUP_TYPE            ");
				sb.append(" AND FLAG = :FLAG          		        ");
				for(int i=0; i<inputVO.getShowList().size(); i++){
					QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					condition.setObject("MG1", inputVO.getShowList().get(i).get("MG1").toString().replace(".0",""));
					condition.setObject("MG2", inputVO.getShowList().get(i).get("MG2").toString().replace(".0",""));
					condition.setObject("MG3", inputVO.getShowList().get(i).get("MG3").toString().replace(".0",""));
					condition.setObject("MG4", inputVO.getShowList().get(i).get("MG4").toString().replace(".0",""));
					condition.setObject("MG5", inputVO.getShowList().get(i).get("MG5").toString().replace(".0",""));
					condition.setObject("MG6", inputVO.getShowList().get(i).get("MG6").toString().replace(".0",""));
					condition.setObject("MG7", inputVO.getShowList().get(i).get("MG7").toString().replace(".0",""));
					condition.setObject("MG8", inputVO.getShowList().get(i).get("MG8").toString().replace(".0",""));
					condition.setObject("MG9", inputVO.getShowList().get(i).get("MG9").toString().replace(".0",""));
					condition.setObject("MG10", inputVO.getShowList().get(i).get("MG10").toString().replace(".0",""));
					condition.setObject("MG11", inputVO.getShowList().get(i).get("MG11").toString().replace(".0",""));
					condition.setObject("MG12", inputVO.getShowList().get(i).get("MG12").toString().replace(".0",""));
					condition.setObject("userId", (String) getUserVariable(FubonSystemVariableConsts.LOGINID));
					condition.setObject("DATE_YEAR", inputVO.getShowList().get(i).get("DATE_YEAR"));
					condition.setObject("PERSON_TYPE", inputVO.getShowList().get(i).get("PERSON_TYPE"));
					condition.setObject("FLAG", inputVO.getShowList().get(i).get("FLAG"));
					condition.setObject("GROUP_TYPE", inputVO.getShowList().get(i).get("GROUP_TYPE"));
					condition.setObject("BRANCH_NBR", inputVO.getShowList().get(i).get("BRANCH_NBR"));
					condition.setQueryString(sb.toString());
					dam.exeUpdate(condition);
				}
				changeMastSet("EXAM_STATS",inputVO.getDate_year(),inputVO.getUserId(),inputVO.getPersonType(),inputVO.getIsSpecial(),inputVO.getSubProjectSeqId());
				this.sendRtnObject(null);
		}catch (Exception e) {
			logger.error("更新失敗");
			throw new APException(e.getMessage());
		}
	}
	//KPI指標設定met02
	/**
	  * 查詢子項目分行2
	  * @param body
	  * @param header
	  * @throws JBranchException
	  */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void queryMet02(Object body, IPrimitiveMap header) throws JBranchException {
		PMS711InputVO inputVO = (PMS711InputVO) body;
		PMS711OutputVO outputVO = new PMS711OutputVO();
		StringBuffer sb = new StringBuffer();
		dam = this.getDataAccessManager();
		try
		{
			QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb.append("  SELECT DATE_YEAR,												");		
			sb.append("  	    PERSON_TYPE,                                            ");
			sb.append("  	    AO_ORGANIZATION AS BRANCH_NBR,                          ");
			sb.append("  	    U.BRANCH_NAME AS BRANCH_NAME,                           ");
			sb.append("  	    U.BRANCH_CLS AS GROUP_TYPE,                             ");
			sb.append("  	    LPAD(SUB_PROJECT_SEQ_ID,2,'0') AS SUB_PROJECT_SEQ_ID,   ");
			sb.append("  	    MONTH1,                                                 ");
			sb.append("  	    MONTH2,                                                 ");
			sb.append("  	    MONTH3,                                                 ");
			sb.append("  	    MONTH4,                                                 ");
			sb.append("  	    MONTH5,                                                 ");
			sb.append("  	    MONTH6,                                                 ");
			sb.append("  	    MONTH7,                                                 ");
			sb.append("  	    MONTH8,                                                 ");
			sb.append("  	    MONTH9,                                                 ");
			sb.append("  	    MONTH10,                                                ");
			sb.append("  	    MONTH11,                                                ");
			sb.append("  	    MONTH12,                                                ");
			sb.append("  	    MONTH13,                                                ");
			sb.append("  	    MONTH14,                                                ");
			sb.append("  	    MONTH15,                                                ");
			sb.append("  	    MONTH16,                                                ");
			sb.append("  	    MONTH17,                                                ");
			sb.append("  	    MONTH18,                                                ");
			sb.append("  	    MONTH19,                                                ");
			sb.append("  	    MONTH20,                                                ");
			sb.append("  	    MONTH21,                                                ");
			sb.append("  	    MONTH22,                                                ");
			sb.append("  	    MONTH23,                                                ");
			sb.append("  	    MONTH24                                                 ");
			
			sb.append("  FROM (SELECT * FROM TBPMS_KPI_FC_SUB_PROJECT                   ");
			sb.append("  WHERE DATE_YEAR = :dateYear		                            ");
			sb.append("  AND    PERSON_TYPE = :personType      	                        ");
			sb.append("  AND    SUB_PROJECT_SEQ_ID = :subProjectSeqId )T     	        ");
			sb.append("  LEFT JOIN TBPMS_ORG_REC_N U ON T.AO_ORGANIZATION = U.BRANCH_NBR");
			sb.append("   AND CASE WHEN TO_DATE(:JJJ,'YYYYMMDD') = LAST_DAY(TO_DATE(:JJJ,'YYYYMMDD')) THEN TO_DATE(:JJJ,'YYYYMMDD') ELSE TRUNC(TO_DATE(:JJJ,'YYYYMMDD'),'MM') - 1 END BETWEEN U.START_TIME AND U.END_TIME");
			sb.append("  ORDER BY BRANCH_NBR                          ");
			qc.setObject("dateYear", inputVO.getDate_year());
			qc.setObject("personType", inputVO.getPersonType());
			qc.setObject("subProjectSeqId", inputVO.getSubProjectSeqId());
			qc.setObject("JJJ", inputVO.getEDate1());
			qc.setQueryString(sb.toString());
			
			ResultIF largeAgrList = dam.executePaging(qc, inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
			int totalPage_i = largeAgrList.getTotalPage(); // 分頁用
			int totalRecord_i = largeAgrList.getTotalRecord(); // 分頁用
			outputVO.setShowList(largeAgrList); // data
			outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());// 當前頁次
			outputVO.setTotalPage(totalPage_i);// 總頁次
			outputVO.setTotalRecord(totalRecord_i);// 總筆數
			
			this.sendRtnObject(outputVO);
		}
		catch (Exception e)
		{
			logger.error(String.format("發生錯誤:%s",StringUtil.getStackTraceAsString(e)));
			throw new APException(e.getMessage());
		}
	}
	/*@SuppressWarnings({ "rawtypes", "unchecked" })
	public void queryMet02(Object body, IPrimitiveMap header) throws JBranchException {
		PMS711InputVO inputVO = (PMS711InputVO) body;
		PMS711OutputVO outputVO = new PMS711OutputVO();
		StringBuffer sb = new StringBuffer();
		dam = this.getDataAccessManager();
		try
		{
			QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb.append(" SELECT                                ");
			sb.append(" 	DATE_YEAR,                        ");
			sb.append(" 	BRANCH_NBR,                       ");
			sb.append(" 	BRANCH_NAME,                      ");
			sb.append(" 	GROUP_TYPE,                       ");
			sb.append(" 	MONTH1_GOAL,                      ");
			sb.append(" 	MONTH2_GOAL,                      ");
			sb.append(" 	MONTH3_GOAL,                      ");
			sb.append(" 	MONTH4_GOAL,                      ");
			sb.append(" 	MONTH5_GOAL,                      ");
			sb.append(" 	MONTH6_GOAL,                      ");
			sb.append(" 	MONTH7_GOAL,                      ");
			sb.append(" 	MONTH8_GOAL,                      ");
			sb.append(" 	MONTH9_GOAL,                      ");
			sb.append(" 	MONTH10_GOAL,                     ");
			sb.append(" 	MONTH11_GOAL,                     ");
			sb.append(" 	MONTH12_GOAL,                     ");
			sb.append(" 	TOTAL1,                           ");
			sb.append(" 	MON1_GOAL,                        ");
			sb.append(" 	MON2_GOAL,                        ");
			sb.append(" 	MON3_GOAL,                        ");
			sb.append(" 	MON4_GOAL,                        ");
			sb.append(" 	MON5_GOAL,                        ");
			sb.append(" 	MON6_GOAL,                        ");
			sb.append(" 	MON7_GOAL,                        ");
			sb.append(" 	MON8_GOAL,                        ");
			sb.append(" 	MON9_GOAL,                        ");
			sb.append(" 	MON10_GOAL,                       ");
			sb.append(" 	MON11_GOAL,                       ");
			sb.append(" 	MON12_GOAL,                       ");
			sb.append(" 	TOTAL2,                           ");
			sb.append(" 	VERSION                           ");
			sb.append(" FROM TBPMS_KPI_MET_CTS_GOAL           ");
			sb.append(" WHERE DATE_YEAR = :date_year          ");
			sb.append("ORDER BY BRANCH_NBR                    ");
			condition.setObject("date_year", inputVO.getDate_year());
			condition.setQueryString(sb.toString());
			List<Map<String,Object>> resultList = dam.exeQuery(condition);
			outputVO.setShowList(resultList);
			this.sendRtnObject(outputVO);
		}
		catch (Exception e)
		{
			logger.error(String.format("發生錯誤:%s",StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}*/
	 /**
	  * 更新子項目met02
	  * @param body
	  * @param header
	  * @throws JBranchException
	  */
	public void saveMet02(Object body, IPrimitiveMap header) throws JBranchException {
			PMS711InputVO inputVO = (PMS711InputVO) body;
			PMS711OutputVO outputVO = new PMS711OutputVO();
			StringBuffer sb = new StringBuffer();
			dam = this.getDataAccessManager();
			try
			{
				sb.append("	  	UPDATE TBPMS_KPI_MET_CTS_GOAL SET      "); 
				sb.append("			  MONTH1_GOAL =  :MONTH1_GOAL,     "); 
				sb.append("	  		  MONTH2_GOAL =  :MONTH2_GOAL,     "); 	 
				sb.append("	  		  MONTH3_GOAL =  :MONTH3_GOAL,     ");    
				sb.append("	  		  MONTH4_GOAL =  :MONTH4_GOAL,     ");     
				sb.append("	  		  MONTH5_GOAL =  :MONTH5_GOAL,     ");     
				sb.append("	  		  MONTH6_GOAL =  :MONTH6_GOAL,     "); 
			    sb.append("           MONTH7_GOAL  = :MONTH7_GOAL,     "); 
			    sb.append("           MONTH8_GOAL  = :MONTH8_GOAL,     "); 
			    sb.append("           MONTH9_GOAL  = :MONTH9_GOAL,     "); 
			    sb.append("           MONTH10_GOAL = :MONTH10_GOAL,    "); 
			    sb.append("           MONTH11_GOAL = :MONTH11_GOAL,    "); 
			    sb.append("           MONTH12_GOAL = :MONTH12_GOAL,    "); 
			    sb.append("           TOTAL1        = :TOTAL1,         "); 
			    /*sb.append("			  MON1_GOAL =  :MON1_GOAL,         "); 
				sb.append("	  		  MON2_GOAL =  :MON2_GOAL,         "); 	 
				sb.append("	  		  MON3_GOAL =  :MON3_GOAL,         ");    
				sb.append("	  		  MON4_GOAL =  :MON4_GOAL,         ");     
				sb.append("	  		  MON5_GOAL =  :MON5_GOAL,         ");     
				sb.append("	  		  MON6_GOAL =  :MON6_GOAL,         "); 
			    sb.append("           MON7_GOAL  = :MON7_GOAL,         "); 
			    sb.append("           MON8_GOAL  = :MON8_GOAL,         "); 
			    sb.append("           MON9_GOAL  = :MON9_GOAL,         "); 
			    sb.append("           MON10_GOAL = :MON10_GOAL,        "); 
			    sb.append("           MON11_GOAL = :MON11_GOAL,        "); 
			    sb.append("           MON12_GOAL = :MON12_GOAL,        "); 
			    sb.append("           TOTAL2     = :TOTAL2,            "); */
				sb.append("			  VERSION = 1,                     ");
				sb.append("	  		  MODIFIER = :userId,         	   "); 
				sb.append("	  		  LASTUPDATE = sysdate        	   "); 
				sb.append("	  	WHERE DATE_YEAR = :DATE_YEAR      	   "); 
				sb.append("	  	AND BRANCH_NBR = :BRANCH_NBR           "); 
				sb.append("     AND GROUP_TYPE = :GROUP_TYPE           ");
				for(int i=0; i<inputVO.getShowList().size(); i++){
					QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					condition.setObject("MONTH1_GOAL", inputVO.getShowList().get(i).get("MONTH1_GOAL").toString().replace(".0",""));
					condition.setObject("MONTH2_GOAL", inputVO.getShowList().get(i).get("MONTH2_GOAL").toString().replace(".0",""));
					condition.setObject("MONTH3_GOAL", inputVO.getShowList().get(i).get("MONTH3_GOAL").toString().replace(".0",""));
					condition.setObject("MONTH4_GOAL", inputVO.getShowList().get(i).get("MONTH4_GOAL").toString().replace(".0",""));
					condition.setObject("MONTH5_GOAL", inputVO.getShowList().get(i).get("MONTH5_GOAL").toString().replace(".0",""));
					condition.setObject("MONTH6_GOAL", inputVO.getShowList().get(i).get("MONTH6_GOAL").toString().replace(".0",""));
					condition.setObject("MONTH7_GOAL", inputVO.getShowList().get(i).get("MONTH7_GOAL").toString().replace(".0",""));
					condition.setObject("MONTH8_GOAL", inputVO.getShowList().get(i).get("MONTH8_GOAL").toString().replace(".0",""));
					condition.setObject("MONTH9_GOAL", inputVO.getShowList().get(i).get("MONTH9_GOAL").toString().replace(".0",""));
					condition.setObject("MONTH10_GOAL", inputVO.getShowList().get(i).get("MONTH10_GOAL").toString().replace(".0",""));
					condition.setObject("MONTH11_GOAL", inputVO.getShowList().get(i).get("MONTH11_GOAL").toString().replace(".0",""));
					condition.setObject("MONTH12_GOAL", inputVO.getShowList().get(i).get("MONTH12_GOAL").toString().replace(".0",""));
					condition.setObject("TOTAL1", inputVO.getShowList().get(i).get("TOTAL1").toString().replace(".0",""));
					/*condition.setObject("MON1_GOAL", inputVO.getShowList().get(i).get("MON1_GOAL").toString().replace(".0",""));
					condition.setObject("MON2_GOAL", inputVO.getShowList().get(i).get("MON2_GOAL").toString().replace(".0",""));
					condition.setObject("MON3_GOAL", inputVO.getShowList().get(i).get("MON3_GOAL").toString().replace(".0",""));
					condition.setObject("MON4_GOAL", inputVO.getShowList().get(i).get("MON4_GOAL").toString().replace(".0",""));
					condition.setObject("MON5_GOAL", inputVO.getShowList().get(i).get("MON5_GOAL").toString().replace(".0",""));
					condition.setObject("MON6_GOAL", inputVO.getShowList().get(i).get("MON6_GOAL").toString().replace(".0",""));
					condition.setObject("MON7_GOAL", inputVO.getShowList().get(i).get("MON7_GOAL").toString().replace(".0",""));
					condition.setObject("MON8_GOAL", inputVO.getShowList().get(i).get("MON8_GOAL").toString().replace(".0",""));
					condition.setObject("MON9_GOAL", inputVO.getShowList().get(i).get("MON9_GOAL").toString().replace(".0",""));
					condition.setObject("MON10_GOAL", inputVO.getShowList().get(i).get("MON10_GOAL").toString().replace(".0",""));
					condition.setObject("MON11_GOAL", inputVO.getShowList().get(i).get("MON11_GOAL").toString().replace(".0",""));
					condition.setObject("MON12_GOAL", inputVO.getShowList().get(i).get("MON12_GOAL").toString().replace(".0",""));
					condition.setObject("TOTAL2", inputVO.getShowList().get(i).get("TOTAL2").toString().replace(".0",""));*/
					condition.setObject("userId", (String) getUserVariable(FubonSystemVariableConsts.LOGINID));
					condition.setObject("DATE_YEAR", inputVO.getShowList().get(i).get("DATE_YEAR"));
					condition.setObject("GROUP_TYPE", inputVO.getShowList().get(i).get("GROUP_TYPE"));
					condition.setObject("BRANCH_NBR", inputVO.getShowList().get(i).get("BRANCH_NBR"));
					condition.setQueryString(sb.toString());
					dam.exeUpdate(condition);
				}
				changeMastSet("EXAM_STATS",inputVO.getDate_year(),inputVO.getUserId(),inputVO.getPersonType(),inputVO.getIsSpecial(),inputVO.getSubProjectSeqId());
				this.sendRtnObject(null);
		}catch (Exception e) {
			logger.error("更新失敗");
			throw new APException(e.getMessage());
		}
	}
	/**
	 * 整批上傳1
	 */
	public void batchUpload(Object body, IPrimitiveMap header) throws APException
	{
		int flag = 0;
		try {
			PMS711InputVO inputVO = (PMS711InputVO) body;
			PMS711OutputVO outputVO = new PMS711OutputVO();
			List<String> list =  new ArrayList<String>();
			String joinedPath = new File((String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH), inputVO.getFileName()).toString();
			Workbook workbook = Workbook.getWorkbook(new File(joinedPath));
			Sheet sheet[] = workbook.getSheets();
			//有表頭.xls文檔
			//清空臨時表
			dam = this.getDataAccessManager();
			QueryConditionIF dcon = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			
//			if(!inputVO.getCheckflag().equals("理財戶網行銀實動戶目標")){
					String dsql = " TRUNCATE TABLE TBPMS_KPI_FC_SUB_PROJECT_U ";
					dcon.setQueryString(dsql.toString());
					dam.exeUpdate(dcon);
					String lab = null;
//					for(int a=0;a<sheet.length;a++){  //目前只要一sheet
					for(int a=0;a<1;a++){
						for(int i=1;i<sheet[a].getRows();i++){
							for(int j=0;j<sheet[a].getColumns();j++){
									lab = sheet[a].getCell(j, i).getContents();
									list.add(lab);
							}
							if(list.get(1).trim().equals("22")){
								list.clear();
								continue;
							}
							//excel表格記行數
							flag++;
							//SQL指令
							StringBuffer sb = new StringBuffer();
							dam = this.getDataAccessManager();
							QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
							sb.append("   INSERT INTO TBPMS_KPI_FC_SUB_PROJECT_U (     	      ");
							sb.append("  		DATE_YEAR,            				          ");
							sb.append("  		RNUM,            				          	  ");
							sb.append("  		PERSON_TYPE,           					      ");
							sb.append("  		AO_ORGANIZATION,            			      ");
							sb.append("  		SUB_PROJECT_SEQ_ID,            				  ");
							sb.append("  		MEMO,            					 		  ");
							sb.append("  		MONTH1,            					          ");
							sb.append("  		MONTH2,            					          ");
							sb.append("  		MONTH3,            					          ");
							sb.append("  		MONTH4,            					          ");
							sb.append("  		MONTH5,            					          ");
							sb.append("  		MONTH6,            					          ");
							sb.append("  		MONTH7,            					          ");
							sb.append("  		MONTH8,            					          ");
							sb.append("  		MONTH9,            					          ");
							sb.append("  		MONTH10,            					      ");
							sb.append("  		MONTH11,            					      ");
							sb.append("  		MONTH12,            					      ");
							sb.append("  		MONTH13,            					      ");
							sb.append("  		MONTH14,            					      ");
							sb.append("  		MONTH15,            					      ");
							sb.append("  		MONTH16,            					      ");
							sb.append("  		MONTH17,            					      ");
							sb.append("  		MONTH18,            					      ");
							sb.append("  		MONTH19,            					      ");
							sb.append("  		MONTH20,            					      ");
							sb.append("  		MONTH21,            					      ");
							sb.append("  		MONTH22,            					      ");
							sb.append("  		MONTH23,            					      ");
							sb.append("  		MONTH24,            					      ");
							sb.append("  		VERSION,            						  ");
							sb.append("  		CREATETIME,             					  ");
							sb.append("  		CREATOR,             						  ");
							sb.append("  		MODIFIER,         						      ");
							sb.append("  		LASTUPDATE )             					  ");
							sb.append("  	VALUES(:DATA_YEAR,            				      ");
							sb.append("  		:RNUM,		            				      ");
							sb.append("  		:PERSON_TYPE,             				      ");
							sb.append("  		:AO_ORGANIZATION,             			      ");
							sb.append("  		:SUB_PROJECT_SEQ_ID,             			  ");
							sb.append("  		:MEMO,             			        		  ");
							sb.append("  		:MONTH1,             			              ");
							sb.append("  		:MONTH2,             			              ");
							sb.append("  		:MONTH3,             			              ");
							sb.append("  		:MONTH4,             			              ");
							sb.append("  		:MONTH5,             			              ");
							sb.append("  		:MONTH6,             			              ");
							sb.append("  		:MONTH7,             			              ");
							sb.append("  		:MONTH8,             			              ");
							sb.append("  		:MONTH9,             			              ");
							sb.append("  		:MONTH10,             			              ");
							sb.append("  		:MONTH11,             			              ");
							sb.append("  		:MONTH12,             			              ");
							sb.append("  		:MONTH13,             			              ");
							sb.append("  		:MONTH14,             			              ");
							sb.append("  		:MONTH15,             			              ");
							sb.append("  		:MONTH16,             			              ");
							sb.append("  		:MONTH17,             			              ");
							sb.append("  		:MONTH18,             			              ");
							sb.append("  		:MONTH19,             			              ");
							sb.append("  		:MONTH20,             			              ");
							sb.append("  		:MONTH21,             			              ");
							sb.append("  		:MONTH22,             			              ");
							sb.append("  		:MONTH23,             			              ");
							sb.append("  		:MONTH24,             			              ");
							sb.append("  		:VERSION,           					      ");
							sb.append("  		SYSDATE,           				              ");
							sb.append("  		:CREATOR,            					      ");
							sb.append("  		:MODIFIER,         					          ");
							sb.append("  		SYSDATE)          				              ");
							qc.setObject("DATA_YEAR",inputVO.getDate_year()                    );
							qc.setObject("RNUM",i					                           );
							qc.setObject("PERSON_TYPE",inputVO.getPersonType()                 );
							qc.setObject("AO_ORGANIZATION",list.get(0).trim()                  );
							qc.setObject("SUB_PROJECT_SEQ_ID",list.get(1).trim()               );
							qc.setObject("MEMO",list.get(2).trim()                             );
							for(int index=1 ; index<list.size()-2 ; index++){
								qc.setObject("MONTH"+index,list.get(index+2).trim()            );
							}
							for(int index=list.size()-2 ; index<25; index++){
								qc.setObject("MONTH"+index,0            					   );
							}
							qc.setObject("VERSION","0"                                         );
							qc.setObject("CREATOR", (String) getUserVariable(FubonSystemVariableConsts.LOGINID)      );
							qc.setObject("MODIFIER", (String) getUserVariable(FubonSystemVariableConsts.LOGINID)      );
							qc.setQueryString(sb.toString());
							dam.exeUpdate(qc);
							list.clear();
							
						}
					}					
			this.sendRtnObject(outputVO);
		} catch (Exception e) {
			logger.error("文檔上傳失敗");
			throw new APException("資料上傳失敗,錯誤發生在第"+flag+"筆,"+e.getMessage());
		}
	}
	
	/**
	 * 整批上傳(22)
	 */
	public void batchUploade(Object body, IPrimitiveMap header) throws APException
	{
		int flag = 0;
		try {
			PMS711InputVO inputVO = (PMS711InputVO) body;
			PMS711OutputVO outputVO = new PMS711OutputVO();
			List<String> list =  new ArrayList<String>();
			String joinedPath = new File((String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH), inputVO.getFileName()).toString();
			Workbook workbook = Workbook.getWorkbook(new File(joinedPath));
			Sheet sheet[] = workbook.getSheets();
			//有表頭.xls文檔
			//清空臨時表
			dam = this.getDataAccessManager();
			QueryConditionIF dcon = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);	
//			String dsql = " TRUNCATE TABLE TBPMS_KPI_PD_FIN_GOAL_BRAN ";
//			dcon.setQueryString(dsql.toString());
//			dam.exeUpdate(dcon);
			String lab = null;
//			for(int a=0;a<sheet.length;a++){   //目前只需要一個sheet  loop跑設定只跑一次
			for(int a=0;a<1;a++){
				for(int i=1;i<sheet[a].getRows();i++){
					for(int j=0;j<sheet[a].getColumns();j++){
						lab = sheet[a].getCell(j, i).getContents();
						list.add(lab);
					}
					//excel表格記行數
					flag++;
					//SQL指令
					StringBuffer sb = new StringBuffer();
					dam = this.getDataAccessManager();
					QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
//					sb.append("   INSERT INTO TBPMS_KPI_PD_FIN_GOAL_BRAN (     	      ");
//					sb.append("  		DATE_YEARMON,BRANCH_NBR,            				          ");
//					sb.append("  		BRANCH_RATE,            				          	  ");
//					sb.append("  		BRANCH_M_GOAL,PERSON_TYPE,BRANCH_NAME,GROUP_TYPE           					      ");
//					sb.append("  		)                       					  ");
//					sb.append("  	VALUES(:DATE_YEARMON,:BRANCH_NBR,            				      ");
//					sb.append("  		:BRANCH_RATE,		            				      ");
//					sb.append("  		:BRANCH_M_GOAL,:PERSON_TYPE,             				      ");
//					sb.append("  		(SELECT AA.branch_name FROM tbpms_org_rec_n AA WHERE AA.branch_nbr = :BRANCH_NBR),         ");
//					sb.append("  		(SELECT AA.branch_cls FROM tbpms_org_rec_n AA WHERE AA.branch_nbr = :BRANCH_NBR)        ");
//					sb.append("  		)          				              ");
					if(list.get(1).trim().equals("滲透率")){
						sb.append("   INSERT INTO TBPMS_KPI_PD_FIN_GOAL_BRAN (     	      ");
						sb.append("   DATE_YEAR,PERSON_TYPE,BRANCH_NBR,BRANCH_NAME,GROUP_TYPE,     	      ");
						sb.append("   MONTH1_RATE,MONTH2_RATE,MONTH3_RATE,MONTH4_RATE,MONTH5_RATE,     	      ");
						sb.append("   MONTH6_RATE,MONTH7_RATE,MONTH8_RATE,MONTH9_RATE,MONTH10_RATE,MONTH11_RATE,MONTH12_RATE     	      ");
						sb.append("   ,CREATOR,MODIFIER,VERSION )     	      ");
						sb.append("   VALUES(:DATE_YEAR,:PERSON_TYPE,:BRANCH_NBR,   ");
						sb.append("   (SELECT AA.branch_name FROM tbpms_org_rec_n AA WHERE AA.branch_nbr = :BRANCH_NBR),    ");
						sb.append("   (SELECT AA.branch_cls FROM tbpms_org_rec_n AA WHERE AA.branch_nbr = :BRANCH_NBR),     	      ");
						sb.append("   :MONTH1_RATE,:MONTH2_RATE,:MONTH3_RATE,:MONTH4_RATE,:MONTH5_RATE,:MONTH6_RATE,:MONTH7_RATE,     	      ");
						sb.append("   :MONTH8_RATE,:MONTH9_RATE,:MONTH10_RATE,:MONTH11_RATE,:MONTH12_RATE     	      ");
						sb.append("   ,:CREATOR,:MODIFIER,:VERSION )     	      ");
						
//						qc.setObject("DATE_YEARMON",list.get(0).trim()                 );
						qc.setObject("BRANCH_NBR",list.get(0).trim());
						qc.setObject("MONTH1_RATE",list.get(2).trim());
						qc.setObject("MONTH2_RATE",list.get(3).trim());
						qc.setObject("MONTH3_RATE",list.get(4).trim());
						qc.setObject("MONTH4_RATE",list.get(5).trim());
						qc.setObject("MONTH5_RATE",list.get(6).trim());
						qc.setObject("MONTH6_RATE",list.get(7).trim());
						qc.setObject("MONTH7_RATE",list.get(8).trim());
						qc.setObject("MONTH8_RATE",list.get(9).trim());
						qc.setObject("MONTH9_RATE",list.get(10).trim());
						qc.setObject("MONTH10_RATE",list.get(11).trim());
						qc.setObject("MONTH11_RATE",list.get(12).trim());
						qc.setObject("MONTH12_RATE",list.get(13).trim());
						
						qc.setObject("VERSION","0" );
						qc.setObject("CREATOR", (String) getUserVariable(FubonSystemVariableConsts.LOGINID));
						qc.setObject("MODIFIER", (String) getUserVariable(FubonSystemVariableConsts.LOGINID));
						qc.setObject("PERSON_TYPE",inputVO.getPersonType());
						qc.setObject("DATE_YEAR",inputVO.getDate_year());


					} else {
//						sb.append("   INSERT INTO TBPMS_KPI_PD_FIN_GOAL_BRAN (   ");
//						sb.append("   PERSON_TYPE,BRANCH_NBR,BRANCH_NAME,GROUP_TYPE,   ");
//						sb.append("   MONTH1_M_GOAL,MONTH2_M_GOAL,MONTH3_M_GOAL,MONTH4_M_GOAL,MONTH5_M_GOAL,     	      ");
//						sb.append("   MONTH6_M_GOAL,MONTH7_M_GOAL,MONTH8_M_GOAL,MONTH9_M_GOAL,MONTH10_M_GOAL,MONTH11_M_GOAL,MONTH12_M_GOAL   ");
//						sb.append("   ,CREATOR,MODIFIER,VERSION )  ");
//						sb.append("   VALUES(:PERSON_TYPE,:BRANCH_NBR,   ");
//						sb.append("   (SELECT AA.branch_name FROM tbpms_org_rec_n AA WHERE AA.branch_nbr = :BRANCH_NBR),     	      ");
//						sb.append("   (SELECT AA.branch_cls FROM tbpms_org_rec_n AA WHERE AA.branch_nbr = :BRANCH_NBR),     	      ");
//						sb.append("   :MONTH1_M_GOAL,:MONTH2_M_GOAL,:MONTH3_M_GOAL,:MONTH4_M_GOAL,:MONTH5_M_GOAL,:MONTH6_M_GOAL,:MONTH7_M_GOAL, ");
//						sb.append("   :MONTH8_M_GOAL,:MONTH9_M_GOAL,:MONTH10_M_GOAL,:MONTH11_M_GOAL,:MONTH12_M_GOAL  ");
//						sb.append("   ,:CREATOR,:MODIFIER,:VERSION )     	      ");
						
						sb.append(" update tbpms_kpi_pd_fin_goal_bran  set MONTH1_M_GOAL=:MONTH1_M_GOAL,MONTH2_M_GOAL=:MONTH2_M_GOAL, ");
						sb.append(" MONTH3_M_GOAL=:MONTH3_M_GOAL,MONTH4_M_GOAL=:MONTH4_M_GOAL,MONTH5_M_GOAL=:MONTH5_M_GOAL,  ");
						sb.append(" MONTH6_M_GOAL=:MONTH6_M_GOAL,MONTH7_M_GOAL=:MONTH7_M_GOAL  ");
						sb.append(" ,MONTH8_M_GOAL=:MONTH8_M_GOAL,MONTH9_M_GOAL=:MONTH9_M_GOAL,MONTH10_M_GOAL=:MONTH10_M_GOAL,MONTH11_M_GOAL=:MONTH11_M_GOAL  ");
						sb.append(" ,MONTH12_M_GOAL=:MONTH12_M_GOAL where branch_nbr = :BRANCH_NBR  ");
						
						qc.setObject("BRANCH_NBR",list.get(0).trim());
						qc.setObject("MONTH1_M_GOAL",list.get(2).trim());
						qc.setObject("MONTH2_M_GOAL",list.get(3).trim());
						qc.setObject("MONTH3_M_GOAL",list.get(4).trim());
						qc.setObject("MONTH4_M_GOAL",list.get(5).trim());
						qc.setObject("MONTH5_M_GOAL",list.get(6).trim());
						qc.setObject("MONTH6_M_GOAL",list.get(7).trim());
						qc.setObject("MONTH7_M_GOAL",list.get(8).trim());
						qc.setObject("MONTH8_M_GOAL",list.get(9).trim());
						qc.setObject("MONTH9_M_GOAL",list.get(10).trim());
						qc.setObject("MONTH10_M_GOAL",list.get(11).trim());
						qc.setObject("MONTH11_M_GOAL",list.get(12).trim());
						qc.setObject("MONTH12_M_GOAL",list.get(13).trim());
					}



					qc.setQueryString(sb.toString());
					dam.exeUpdate(qc);
					list.clear();
				}
			}
			this.sendRtnObject(outputVO);
		} catch (Exception e) {
			logger.error("文檔上傳失敗");
			throw new APException("資料上傳失敗,錯誤發生在第"+flag+"筆,"+e.getMessage());
		}
	}
	
	/**
	 * 非系統項目整批上傳
	 */
	public void batchUpload2(Object body, IPrimitiveMap header) throws APException
	{
		int flag = 0;
		try {
			PMS711InputVO inputVO = (PMS711InputVO) body;
			PMS711OutputVO outputVO = new PMS711OutputVO();
			List<String> list =  new ArrayList<String>();
			String joinedPath = new File((String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH), inputVO.getFileName()).toString();
			Workbook workbook = Workbook.getWorkbook(new File(joinedPath));
			Sheet sheet[] = workbook.getSheets();
			//有表頭.xls文檔
			//清空臨時表
			dam = this.getDataAccessManager();
			QueryConditionIF dcon = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);	
			String dsql = " TRUNCATE TABLE TBPMS_KPI_ADD_PROJECT_U ";
			dcon.setQueryString(dsql.toString());
			dam.exeUpdate(dcon);
			String lab = null;
//			for(int a=0;a<sheet.length;a++){ //目前只需要一個sheet  loop跑設定只跑一次
			for(int a=0;a<1;a++){
				for(int i=1;i<sheet[a].getRows();i++){
					for(int j=0;j<sheet[a].getColumns();j++){
						lab = sheet[a].getCell(j, i).getContents();
						list.add(lab);
					}
					//excel表格記行數
					flag++;
					//SQL指令
					StringBuffer sb = new StringBuffer();
					dam = this.getDataAccessManager();
					QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					sb.append("   INSERT INTO TBPMS_KPI_ADD_PROJECT_U (     	      ");
					sb.append("  		DATE_YEAR,            				          ");
					sb.append("  		RNUM,            				          	  ");
					sb.append("  		PERSON_TYPE,           					      ");
					sb.append("  		SUB_PROJECT_SEQ_ID,            				  ");
					sb.append("  		BRANCH_EMP,            					 	  ");
					sb.append("  		BRANCH_EMP_TYPE,            				  ");
					sb.append("  		TOTAL_GOAL,            					      ");
					sb.append("  		TOTAL_GRADE,            					  ");
					sb.append("  		RATE,           					          ");
					sb.append("  		VERSION,            						  ");
					sb.append("  		CREATETIME,             					  ");
					sb.append("  		CREATOR,             						  ");
					sb.append("  		MODIFIER,         						      ");
					sb.append("  		LASTUPDATE )             					  ");
					sb.append("  	VALUES(:DATA_YEAR,            				      ");
					sb.append("  		:RNUM,		            				      ");
					sb.append("  		:PERSON_TYPE,             				      ");
					sb.append("  		:SUB_PROJECT_SEQ_ID,             			  ");
					sb.append("  		:BRANCH_EMP,             			          ");
					sb.append("  		:BRANCH_EMP_TYPE,             			      ");
					sb.append("  		:TOTAL_GOAL,             			          ");
					sb.append("  		:TOTAL_GRADE,             			          ");
					sb.append("  		:RATE,             			                  ");
					sb.append("  		:VERSION,           					      ");
					sb.append("  		SYSDATE,           				              ");
					sb.append("  		:CREATOR,            					      ");
					sb.append("  		:MODIFIER,         					          ");
					sb.append("  		SYSDATE)          				              ");
					qc.setObject("DATA_YEAR",list.get(0).trim()                       );
					qc.setObject("RNUM",i					                          );
					qc.setObject("PERSON_TYPE",inputVO.getPersonType()                );
					qc.setObject("SUB_PROJECT_SEQ_ID",list.get(1).trim()              );
					qc.setObject("BRANCH_EMP",list.get(2).trim()                      );
					qc.setObject("BRANCH_EMP_TYPE",list.get(3).trim()                 );
					qc.setObject("TOTAL_GOAL",list.get(4).trim()                      );
					qc.setObject("TOTAL_GRADE",list.get(5).trim()                     );
					qc.setObject("RATE",list.get(6).trim()                            );
					qc.setObject("VERSION","0"                                        );
					qc.setObject("CREATOR",(String) getUserVariable(FubonSystemVariableConsts.LOGINID)    );
					qc.setObject("MODIFIER", (String) getUserVariable(FubonSystemVariableConsts.LOGINID)    );
					qc.setQueryString(sb.toString());
					dam.exeUpdate(qc);
					list.clear();
				}
			}
			this.sendRtnObject(outputVO);
		} catch (Exception e) {
			logger.error("文檔上傳失敗");
			throw new APException("資料上傳失敗,錯誤發生在第"+flag+"筆,"+e.getMessage());
		}
	}
	/**
	 * 堆疊關係設定查詢
	 */
	/**
	 * 查詢KPI項目
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void queryBatchUpload3(Object body, IPrimitiveMap header) throws JBranchException
	{
		PMS711InputVO inputVO = (PMS711InputVO) body;
		PMS711OutputVO outputVO = new PMS711OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("  SELECT DATE_YEAR,                                                ");
		sql.append("  	     PERSON_TYPE,                                              ");
		sql.append("  	     SET_FC,                                                   ");
		sql.append("  	     SET_FCH,                                                  ");
		sql.append("  	     SET_PS,                                                   ");
		sql.append("  	     SET_PRO,                                                  ");
		sql.append("  	     SET_MET,                                                  ");
		sql.append("  	     SET_SER,                                                  ");
		sql.append("  	     SET_AREA                                                  ");
		sql.append("  FROM TBPMS_KPI_PILE_PROJECT                                      ");
		sql.append("  WHERE 1=1                                                        ");
		if(StringUtils.isNotBlank(inputVO.getDate_year())){
			sql.append("  AND DATE_YEAR = :YEAR                                        ");
			qc.setObject("YEAR", inputVO.getDate_year());
		}
		if(StringUtils.isNotBlank(inputVO.getPersonType())){
			sql.append("  AND PERSON_TYPE = :personType                                ");
			qc.setObject("personType", inputVO.getPersonType());
		}
		qc.setQueryString(sql.toString());
		List<Map<String, Object>> result = dam.exeQuery(qc);
		outputVO.setShowList(result);
		sendRtnObject(outputVO);
	}
	/**
	 * 查詢KPI項目
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void saveBatchUpload3(Object body, IPrimitiveMap header) throws JBranchException, ParseException
	{
		PMS711InputVO inputVO = (PMS711InputVO) body;
		PMS711OutputVO outputVO = new PMS711OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		SimpleDateFormat simpleDate = new SimpleDateFormat("yyyyMMdd");
		sb.append("  UPDATE TBPMS_KPI_PILE_PROJECT SET               ");
		sb.append("         SET_FC  = :SET_FC,                       ");
		sb.append("         SET_FCH    = :SET_FCH,                   ");
		sb.append("         SET_PS = :SET_PS,                        ");
		sb.append("         SET_PRO = :SET_PRO,                      ");
		sb.append("         SET_MET = :SET_MET,                      ");
		sb.append("         SET_SER = :SET_SER,                      ");
		sb.append("         SET_AREA = :SET_AREA,                    ");
		sb.append("			VERSION = 1,                             ");
		sb.append("	  		MODIFIER = :userId,         	         "); 
		sb.append("	  		LASTUPDATE = sysdate        	         "); 
		sb.append("    WHERE DATE_YEAR = :dataYear                   ");
		sb.append("    AND PERSON_TYPE = :PERSON_TYPE                ");
		qc.setObject("dataYear", inputVO.getDate_year());
		qc.setObject("PERSON_TYPE", inputVO.getPersonType());
		for (int i = 0; i < inputVO.getShowList().size(); i++)
		{
			qc.setObject("SET_FC",inputVO.getShowList().get(i).get("SET_FC"));
			qc.setObject("SET_FCH",inputVO.getShowList().get(i).get("SET_FCH"));
			qc.setObject("SET_PS",inputVO.getShowList().get(i).get("SET_PS")); 
			qc.setObject("SET_PRO",inputVO.getShowList().get(i).get("SET_PRO")); 
			qc.setObject("SET_MET",inputVO.getShowList().get(i).get("SET_MET")); 
			qc.setObject("SET_SER",inputVO.getShowList().get(i).get("SET_SER")); 
			qc.setObject("SET_AREA",inputVO.getShowList().get(i).get("SET_AREA")); 
			qc.setObject("userId", (String) getUserVariable(FubonSystemVariableConsts.LOGINID));
			qc.setQueryString(sb.toString());
			dam.exeUpdate(qc);
		}
		sendRtnObject(outputVO);
	}
	
	/**
	 * 刪除子項目
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void deleteAddKpi(Object body, IPrimitiveMap header) throws JBranchException, ParseException
	{
		PMS711InputVO inputVO = (PMS711InputVO) body;
		PMS711OutputVO outputVO = new PMS711OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF qc1 = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb1 = new StringBuffer();
		sb1.append("  DELETE TBPMS_CNR_KPI_MAST_ADD_DETAIL T           ");
		sb1.append("  WHERE T.SUB_PROJECT_SEQ_ID = :SUB_PROJECT_SEQ_ID ");
		qc1.setObject("SUB_PROJECT_SEQ_ID", inputVO.getSubProjectSeqId());
		qc1.setQueryString(sb1.toString());
		dam.exeUpdate(qc1);

		QueryConditionIF qc2 = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb2 = new StringBuffer();
		sb2.append("  DELETE TBPMS_KPI_ADD_PROJECT T                   ");
		sb2.append("  WHERE T.SUB_PROJECT_SEQ_ID = :SUB_PROJECT_SEQ_ID ");
		qc2.setObject("SUB_PROJECT_SEQ_ID", inputVO.getSubProjectSeqId());
		qc2.setQueryString(sb2.toString());
		dam.exeUpdate(qc2);
		sendRtnObject(outputVO);
	}
	
	/**
	 * 子項目 整批上傳調用存儲過程
	 * 
	 * @param body
	 * @param header
	 * @throws APException
	 */
	@SuppressWarnings(
	{ "unused", "rawtypes" })
	public void callStored(Object body, IPrimitiveMap header) throws APException
	{
		try
		{
			PMS711InputVO inputVO = (PMS711InputVO) body;
			PMS711OutputVO outputVO = new PMS711OutputVO();
			// 執行存儲過程
			dam = this.getDataAccessManager();
			QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sb = new StringBuffer();
			sb.append(" CALL PABTH_BTPMS724.SP_TBPMS_KPI_FC_SUB_PROJECT(? ,? ,? ) ");
			qc.setString(1, inputVO.getDate_year());
			qc.setString(2, inputVO.getPersonType());
			qc.registerOutParameter(3, Types.VARCHAR);
			qc.setQueryString(sb.toString());
			Map<Integer, Object> resultMap = dam.executeCallable(qc);
			String str = (String) resultMap.get(3);
			String[] strs = null;
			if(str != null)
			{
				strs = str.split("；");
				if(strs != null && strs.length > 5)
				{
					str = strs[0] + "；" + strs[1] + "；" + strs[2] + "；" + strs[3] + "；" + strs[4] + "...等";
				}
			}
			outputVO.setErrorMessage(str);
			this.sendRtnObject(outputVO);
		}
		catch (Exception e)
		{
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new APException(e.getMessage());
		}
	}
	
	
	
	/**
	 * 非系統計算人工上傳 整批上傳調用存儲過程
	 * @param body
	 * @param header
	 * @throws APException
	 */
	@SuppressWarnings(
	{ "unused", "rawtypes" })
	public void callStored2(Object body, IPrimitiveMap header) throws APException
	{
		try
		{
			PMS711InputVO inputVO = (PMS711InputVO) body;
			PMS711OutputVO outputVO = new PMS711OutputVO();
			// 執行存儲過程
			dam = this.getDataAccessManager();
			QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sb = new StringBuffer();
			sb.append(" CALL PABTH_BTPMS724.SP_TBPMS_KPI_ADD_PROJECT(? ,? ,? ) ");
			qc.setString(1, inputVO.getDate_year());
			qc.setString(2, inputVO.getPersonType());
			qc.registerOutParameter(3, Types.VARCHAR);
			qc.setQueryString(sb.toString());
			Map<Integer, Object> resultMap = dam.executeCallable(qc);
			String str = (String) resultMap.get(3);
			String[] strs = null;
			if(str != null)
			{
				strs = str.split("；");
				if(strs != null && strs.length > 5)
				{
					str = strs[0] + "；" + strs[1] + "；" + strs[2] + "；" + strs[3] + "；" + strs[4] + "...等";
				}
			}
			outputVO.setErrorMessage(str);
			this.sendRtnObject(outputVO);
		}
		catch (Exception e)
		{
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new APException(e.getMessage());
		}
	}
	/**
	 * 子項目12上傳
	 * @param body
	 * @param header
	 * @throws APException
	 */
	@SuppressWarnings(
	{ "unused", "rawtypes" })
	public void callStoredHonTs(Object body, IPrimitiveMap header) throws APException
	{
		try
		{
			PMS711InputVO inputVO = (PMS711InputVO) body;
			PMS711OutputVO outputVO = new PMS711OutputVO();
			// 執行存儲過程
			dam = this.getDataAccessManager();
			QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sb = new StringBuffer();
			sb.append(" CALL PABTH_BTPMS724.SP_TBPMS_KPI_PD_HOU_NEW_TS(? ,? ,? ) ");
			qc.setString(1, inputVO.getDate_year());
			qc.setString(2, inputVO.getPersonType());
			qc.registerOutParameter(3, Types.VARCHAR);
			qc.setQueryString(sb.toString());
			Map<Integer, Object> resultMap = dam.executeCallable(qc);
			String str = (String) resultMap.get(3);
			String[] strs = null;
			if(str != null)
			{
				strs = str.split("；");
				if(strs != null && strs.length > 5)
				{
					str = strs[0] + "；" + strs[1] + "；" + strs[2] + "；" + strs[3] + "；" + strs[4] + "...等";
				}
			}
			outputVO.setErrorMessage(str);
			this.sendRtnObject(outputVO);
		}
		catch (Exception e)
		{
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new APException(e.getMessage());
		}
	}
	/**
	 * 子項目14
	 * @param body
	 * @param header
	 * @throws APException
	 */
	@SuppressWarnings(
	{ "unused", "rawtypes" })
	public void callStoredCreditTs(Object body, IPrimitiveMap header) throws APException
	{
		try
		{
			PMS711InputVO inputVO = (PMS711InputVO) body;
			PMS711OutputVO outputVO = new PMS711OutputVO();
			// 執行存儲過程
			dam = this.getDataAccessManager();
			QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sb = new StringBuffer();
			sb.append(" CALL PABTH_BTPMS724.SP_TBPMS_KPI_CREDIT_GOAL_TS(? ,? ,? ) ");
			qc.setString(1, inputVO.getDate_year());
			qc.setString(2, inputVO.getPersonType());
			qc.registerOutParameter(3, Types.VARCHAR);
			qc.setQueryString(sb.toString());
			Map<Integer, Object> resultMap = dam.executeCallable(qc);
			String str = (String) resultMap.get(2);
			String[] strs = null;
			if(str != null)
			{
				strs = str.split("；");
				if(strs != null && strs.length > 5)
				{
					str = strs[0] + "；" + strs[1] + "；" + strs[2] + "；" + strs[3] + "；" + strs[4] + "...等";
				}
			}
			outputVO.setErrorMessage(str);
			this.sendRtnObject(outputVO);
		}
		catch (Exception e)
		{
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new APException(e.getMessage());
		}
	}
	//KPI指標設定1 FC,FCH,PS,分行個金主管整批上傳子頁面查詢
	/**
	  * 查詢子項目
	  * @param body
	  * @param header
	  * @throws JBranchException
	  */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void queryFcSub(Object body, IPrimitiveMap header) throws JBranchException {
			try
			{
				PMS711InputVO inputVO = (PMS711InputVO) body;
				PMS711OutputVO outputVO = new PMS711OutputVO();
				dam = this.getDataAccessManager();
				QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);			
				StringBuffer sb = new StringBuffer();
				sb.append("  SELECT T.DATE_YEAR,												");		
				sb.append("  	    T.PERSON_TYPE,U.BRANCH_CLS AS GROUP_TYPE,U.BRANCH_NAME,                      ");
				sb.append("  	    T.AO_ORGANIZATION,                                        ");
				sb.append("  	    LPAD(SUB_PROJECT_SEQ_ID,2,'0') AS SUB_PROJECT_SEQ_ID,   ");
				sb.append("  	    T.MEMO,                                                   ");
				sb.append("  	    T.MONTH1,                                                 ");
				sb.append("  	    T.MONTH2,                                                 ");
				sb.append("  	    T.MONTH3,                                                 ");
				sb.append("  	    T.MONTH4,                                                 ");
				sb.append("  	    T.MONTH5,                                                 ");
				sb.append("  	    T.MONTH6,                                                 ");
				sb.append("  	    T.MONTH7,                                                 ");
				sb.append("  	    T.MONTH8,                                                 ");
				sb.append("  	    T.MONTH9,                                                 ");
				sb.append("  	    T.MONTH10,                                                ");
				sb.append("  	    T.MONTH11,                                                ");
				sb.append("  	    T.MONTH12,                                                ");
				sb.append("  	    T.MONTH13,                                                ");
				sb.append("  	    T.MONTH14,                                                ");
				sb.append("  	    T.MONTH15,                                                ");
				sb.append("  	    T.MONTH16,                                                ");
				sb.append("  	    T.MONTH17,                                                ");
				sb.append("  	    T.MONTH18,                                                ");
				sb.append("  	    T.MONTH19,                                                ");
				sb.append("  	    T.MONTH20,                                                ");
				sb.append("  	    T.MONTH21,                                                ");
				sb.append("  	    T.MONTH22,                                                ");
				sb.append("  	    T.MONTH23,                                                ");
				sb.append("  	    T.MONTH24,                                                ");
				sb.append("  	    T.VERSION,                                                ");
				sb.append("  	    T.CREATETIME,                                             ");
				sb.append("  	    T.CREATOR,                                                ");
				sb.append("  	    T.MODIFIER,                                               ");
				sb.append("  	    T.LASTUPDATE                                              ");
				

				
				sb.append("  FROM (SELECT * FROM TBPMS_KPI_FC_SUB_PROJECT                                  ");
				sb.append("  WHERE DATE_YEAR = :dateYear		                            ");
				sb.append("  AND    PERSON_TYPE = :personType      	                        ");
				sb.append("  AND    SUB_PROJECT_SEQ_ID = :subProjectSeqId      	            ");
				/*if(inputVO.getCheckflag()!= null && inputVO.getCheckflag().equals("房貸(非購屋+額度式)新承作目標")){
					sb.append("  AND     MEMO = :FLAG     	            ");
				}*/
				if(null != inputVO.getCflag() && !"".equals(inputVO.getCflag())){
					sb.append("  AND     MEMO = :FLAG     	            ");
					qc.setObject("FLAG", inputVO.getCflag());					
				}
				sb.append("  ) T      	        ");

				sb.append(" LEFT JOIN TBPMS_ORG_REC_N U ON T.AO_ORGANIZATION = U.BRANCH_NBR   	            ");
				sb.append("   AND CASE WHEN TO_DATE(:JJJ,'YYYYMMDD') = LAST_DAY(TO_DATE(:JJJ,'YYYYMMDD')) THEN TO_DATE(:JJJ,'YYYYMMDD') ELSE TRUNC(TO_DATE(:JJJ,'YYYYMMDD'),'MM') - 1 END BETWEEN U.START_TIME AND U.END_TIME");
				sb.append("      ORDER BY AO_ORGANIZATION desc  	                        ");
				qc.setObject("dateYear", inputVO.getDate_year()   );
				qc.setObject("personType", inputVO.getPersonType()   );
				qc.setObject("subProjectSeqId", inputVO.getSubProjectSeqId()   );
				qc.setObject("JJJ", inputVO.getEDate1()   );
				qc.setQueryString(sb.toString()                   );
				ResultIF largeAgrList = dam.executePaging(qc, inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
				int totalPage_i = largeAgrList.getTotalPage(); // 分頁用
				int totalRecord_i = largeAgrList.getTotalRecord(); // 分頁用
				outputVO.setShowList(largeAgrList); // data
				outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());// 當前頁次
				outputVO.setTotalPage(totalPage_i);// 總頁次
				outputVO.setTotalRecord(totalRecord_i);// 總筆數

				
				sendRtnObject(outputVO);
			}
			catch (Exception e)
			{
				logger.error(String.format("發生錯誤:%s",StringUtil.getStackTraceAsString(e)));
				throw new APException(e.getMessage());
			}
	}
	/**
	  * 查詢個金主管子項目
	  * @param body
	  * @param header
	  * @throws JBranchException
	  */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void queryMetSub(Object body, IPrimitiveMap header) throws JBranchException {
		PMS711InputVO inputVO = (PMS711InputVO) body;
		PMS711OutputVO outputVO = new PMS711OutputVO();
		StringBuffer sb = new StringBuffer();
		dam = this.getDataAccessManager();
		try
		{
			QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb.append("  SELECT DATE_YEAR,												");		
			sb.append("  	    PERSON_TYPE,                                            ");
			sb.append("  	    AO_ORGANIZATION,                                        ");
			sb.append("  	    U.BRANCH_NAME AS BRANCH_NAME,                           ");
			sb.append("  	    U.BRANCH_CLS AS GROUP_TYPE,                             ");
			sb.append("  	    LPAD(SUB_PROJECT_SEQ_ID,2,'0') AS SUB_PROJECT_SEQ_ID,   ");
			sb.append("  	    MONTH1,                                                 ");
			sb.append("  	    MONTH2,                                                 ");
			sb.append("  	    MONTH3,                                                 ");
			sb.append("  	    MONTH4,                                                 ");
			sb.append("  	    MONTH5,                                                 ");
			sb.append("  	    MONTH6,                                                 ");
			sb.append("  	    MONTH7,                                                 ");
			sb.append("  	    MONTH8,                                                 ");
			sb.append("  	    MONTH9,                                                 ");
			sb.append("  	    MONTH10,                                                ");
			sb.append("  	    MONTH11,                                                ");
			sb.append("  	    MONTH12,                                                ");
			sb.append("  	    MONTH13,                                                ");
			sb.append("  	    MONTH14,                                                ");
			sb.append("  	    MONTH15,                                                ");
			sb.append("  	    MONTH16,                                                ");
			sb.append("  	    MONTH17,                                                ");
			sb.append("  	    MONTH18,                                                ");
			sb.append("  	    MONTH19,                                                ");
			sb.append("  	    MONTH20,                                                ");
			sb.append("  	    MONTH21,                                                ");
			sb.append("  	    MONTH22,                                                ");
			sb.append("  	    MONTH23,                                                ");
			sb.append("  	    MONTH24                                                 ");

			sb.append("  FROM (SELECT * FROM TBPMS_KPI_FC_SUB_PROJECT                   ");
			sb.append("  WHERE DATE_YEAR = :dateYear		                            ");
			sb.append("  AND    PERSON_TYPE = :personType      	                        ");
			sb.append("  AND    SUB_PROJECT_SEQ_ID = :subProjectSeqId       	        ");
			if(inputVO.getCheckflag()!= null && inputVO.getCheckflag().equals("房貸(非購屋+額度式)新承作目標")){
				sb.append("  AND    MEMO = :FLAG       	        ");
			}
			sb.append("  ) T      	        ");
			sb.append("  LEFT JOIN TBPMS_ORG_REC_N U ON T.AO_ORGANIZATION = U.BRANCH_NBR");
			sb.append("   AND CASE WHEN TO_DATE(:JJJ,'YYYYMMDD') = LAST_DAY(TO_DATE(:JJJ,'YYYYMMDD')) THEN TO_DATE(:JJJ,'YYYYMMDD') ELSE TRUNC(TO_DATE(:JJJ,'YYYYMMDD'),'MM') - 1 END BETWEEN U.START_TIME AND U.END_TIME");
			sb.append("  ORDER BY AO_ORGANIZATION                          ");
			qc.setObject("dateYear", inputVO.getDate_year());
			qc.setObject("personType", inputVO.getPersonType());
			qc.setObject("subProjectSeqId", inputVO.getSubProjectSeqId());
			qc.setObject("JJJ", inputVO.getEDate1());
			if(inputVO.getCheckflag()!= null && inputVO.getCheckflag().equals("房貸(非購屋+額度式)新承作目標")){
			qc.setObject("FLAG", inputVO.getCflag());
			}
			qc.setQueryString(sb.toString());

			ResultIF largeAgrList = dam.executePaging(qc, inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
			int totalPage_i = largeAgrList.getTotalPage(); // 分頁用
			int totalRecord_i = largeAgrList.getTotalRecord(); // 分頁用
			outputVO.setShowList(largeAgrList); // data
			outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());// 當前頁次
			outputVO.setTotalPage(totalPage_i);// 總頁次
			outputVO.setTotalRecord(totalRecord_i);// 總筆數
			this.sendRtnObject(outputVO);
		}
		catch (Exception e)
		{
			logger.error(String.format("發生錯誤:%s",StringUtil.getStackTraceAsString(e)));
			throw new APException(e.getMessage());
		}
	}
	
	/**
	  * 查詢個金主管子項目(分行跟PS的項目22)
	  * @param body
	  * @param header
	  * @throws JBranchException
	  */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void queryMetSubEX(Object body, IPrimitiveMap header) throws JBranchException {
		PMS711InputVO inputVO = (PMS711InputVO) body;
		PMS711OutputVO outputVO = new PMS711OutputVO();
		StringBuffer sb = new StringBuffer();
		dam = this.getDataAccessManager();
		try
		{
//			if(inputVO.getCheckflag()!= null && !inputVO.getCheckflag().equals("理財戶網行銀實動戶目標")){
				QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sb.append("  SELECT DATE_YEAR,												");		
				sb.append("  	    PERSON_TYPE,                                            ");
				sb.append("  	    AO_ORGANIZATION,                                        ");
				sb.append("  	    U.BRANCH_NAME AS BRANCH_NAME,                           ");
				sb.append("  	    U.BRANCH_CLS AS GROUP_TYPE,                             ");
				sb.append("  	    LPAD(SUB_PROJECT_SEQ_ID,2,'0') AS SUB_PROJECT_SEQ_ID,   ");
				sb.append("  	    MONTH1,                                                 ");
				sb.append("  	    MONTH2,                                                 ");
				sb.append("  	    MONTH3,                                                 ");
				sb.append("  	    MONTH4,                                                 ");
				sb.append("  	    MONTH5,                                                 ");
				sb.append("  	    MONTH6,                                                 ");
				sb.append("  	    MONTH7,                                                 ");
				sb.append("  	    MONTH8,                                                 ");
				sb.append("  	    MONTH9,                                                 ");
				sb.append("  	    MONTH10,                                                ");
				sb.append("  	    MONTH11,                                                ");
				sb.append("  	    MONTH12,                                                ");
				sb.append("  	    MONTH13,                                                ");
				sb.append("  	    MONTH14,                                                ");
				sb.append("  	    MONTH15,                                                ");
				sb.append("  	    MONTH16,                                                ");
				sb.append("  	    MONTH17,                                                ");
				sb.append("  	    MONTH18,                                                ");
				sb.append("  	    MONTH19,                                                ");
				sb.append("  	    MONTH20,                                                ");
				sb.append("  	    MONTH21,                                                ");
				sb.append("  	    MONTH22,                                                ");
				sb.append("  	    MONTH23,                                                ");
				sb.append("  	    MONTH24                                                  ");

				

				sb.append("  FROM (SELECT * FROM TBPMS_KPI_FC_SUB_PROJECT                   ");
				sb.append("  WHERE DATE_YEAR = :dateYear		                            ");
				sb.append("  AND    PERSON_TYPE = :personType      	                        ");
				sb.append("  AND    SUB_PROJECT_SEQ_ID = :subProjectSeqId       	        ");
				if(inputVO.getCheckflag()!= null && inputVO.getCheckflag().equals("年度貢獻度客戶數目標")){
					sb.append("  AND    MEMO = :FLAG       	        ");
				}
				sb.append("  ) T      	        ");
				sb.append("  LEFT JOIN TBPMS_ORG_REC_N U ON T.AO_ORGANIZATION = U.BRANCH_NBR");
				sb.append("   AND CASE WHEN TO_DATE(:JJJ,'YYYYMMDD') = LAST_DAY(TO_DATE(:JJJ,'YYYYMMDD')) THEN TO_DATE(:JJJ,'YYYYMMDD') ELSE TRUNC(TO_DATE(:JJJ,'YYYYMMDD'),'MM') - 1 END BETWEEN U.START_TIME AND U.END_TIME");
				sb.append("  ORDER BY AO_ORGANIZATION                          ");
				qc.setObject("dateYear", inputVO.getDate_year());
				qc.setObject("personType", inputVO.getPersonType());
				qc.setObject("subProjectSeqId", inputVO.getSubProjectSeqId());
				qc.setObject("JJJ", inputVO.getEDate1());
				if(inputVO.getCheckflag()!= null && inputVO.getCheckflag().equals("年度貢獻度客戶數目標")){
				qc.setObject("FLAG", inputVO.getCflag());
				}
				qc.setQueryString(sb.toString());

				ResultIF largeAgrList = dam.executePaging(qc, inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
				int totalPage_i = largeAgrList.getTotalPage(); // 分頁用
				int totalRecord_i = largeAgrList.getTotalRecord(); // 分頁用
				outputVO.setShowList(largeAgrList); // data
				outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());// 當前頁次
				outputVO.setTotalPage(totalPage_i);// 總頁次
				outputVO.setTotalRecord(totalRecord_i);// 總筆數
						
			this.sendRtnObject(outputVO);
		}
		catch (Exception e)
		{
			logger.error(String.format("發生錯誤:%s",StringUtil.getStackTraceAsString(e)));
			throw new APException(e.getMessage());
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void queryMetSub1(Object body, IPrimitiveMap header) throws JBranchException {
		PMS711InputVO inputVO = (PMS711InputVO) body;
		PMS711OutputVO outputVO = new PMS711OutputVO();
		StringBuffer sb = new StringBuffer();
		dam = this.getDataAccessManager();
		try
		{
			
				QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);			
				StringBuffer sb1 = new StringBuffer();
//				sb1.append("  SELECT BRANCH_RATE AS BR,                	 ");
//				sb1.append("         BRANCH_M_GOAL AS BM,                	 ");
//				sb1.append("         DATE_YEARMON,BRANCH_NAME,BRANCH_NBR,GROUP_TYPE                 	     ");
//				sb1.append("  FROM TBPMS_KPI_PD_FIN_GOAL_BRAN	         ");
//				sb1.append("  WHERE SUBSTR(DATE_YEARMON,1,4) = :dateYear		 ");
//				sb1.append(" AND    PERSON_TYPE = :PERSON_TYPE    ");
				sb1.append("  SELECT DATE_YEAR,PERSON_TYPE,BRANCH_NBR,BRANCH_NAME,GROUP_TYPE,             ");
				sb1.append("  MONTH1_RATE,MONTH2_RATE,MONTH3_RATE,MONTH4_RATE,MONTH5_RATE,MONTH6_RATE,     ");
				sb1.append("  MONTH7_RATE,MONTH8_RATE,MONTH9_RATE,MONTH10_RATE,MONTH11_RATE,MONTH12_RATE,     ");
				sb1.append("  MONTH1_M_GOAL,MONTH2_M_GOAL,MONTH3_M_GOAL,MONTH4_M_GOAL,MONTH5_M_GOAL,MONTH6_M_GOAL,  ");
				sb1.append("  MONTH7_M_GOAL,MONTH8_M_GOAL,MONTH9_M_GOAL,MONTH10_M_GOAL,MONTH11_M_GOAL,MONTH12_M_GOAL ");
				sb1.append("  FROM TBPMS_KPI_PD_FIN_GOAL_BRAN ");
				sb1.append("  WHERE  DATE_YEAR = :dateYear ");
				sb1.append("  AND PERSON_TYPE = :PERSON_TYPE ");
				sb1.append("  ORDER BY DATE_YEAR                         ");
				qc.setObject("dateYear", inputVO.getDate_year()   );
				qc.setObject("PERSON_TYPE", inputVO.getPersonType());
				qc.setQueryString(sb1.toString());

				ResultIF largeAgrList = dam.executePaging(qc, inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
				int totalPage_i = largeAgrList.getTotalPage(); // 分頁用
				int totalRecord_i = largeAgrList.getTotalRecord(); // 分頁用
				outputVO.setShowList1(largeAgrList); // data
				outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());// 當前頁次
				outputVO.setTotalPage(totalPage_i);// 總頁次
				outputVO.setTotalRecord(totalRecord_i);// 總筆數
					
			this.sendRtnObject(outputVO);
		}
		catch (Exception e)
		{
			logger.error(String.format("發生錯誤:%s",StringUtil.getStackTraceAsString(e)));
			throw new APException(e.getMessage());
		}
	}
	
	//KPI指標設定2 非系統計算人工上傳子項目查詢
	/**
	  * 查詢子項目
	  * @param body
	  * @param header
	  * @throws JBranchException
	  */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void queryAddSub(Object body, IPrimitiveMap header) throws JBranchException {
			try
			{
				PMS711InputVO inputVO = (PMS711InputVO) body;
				PMS711OutputVO outputVO = new PMS711OutputVO();
				dam = this.getDataAccessManager();
				QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);			
				StringBuffer sb = new StringBuffer();
				sb.append("  SELECT DATE_YEAR,  										        ");		
				sb.append("  	    PERSON_TYPE,                                                ");
				sb.append("  	    BRANCH_EMP,                                                 ");
				sb.append("  	    LPAD(SUB_PROJECT_SEQ_ID,2,'0') AS SUB_PROJECT_SEQ_ID,       ");
				sb.append("  	    BRANCH_EMP_TYPE,                                            ");
				sb.append("  	    TOTAL_GOAL,                                                 ");
				sb.append("  	    TOTAL_GRADE,                                                ");
				sb.append("  	    RATE,                                                       ");
				sb.append("  	    VERSION,                                                    ");
				sb.append("  	    CREATETIME,                                                 ");
				sb.append("  	    CREATOR,                                                    ");
				sb.append("  	    MODIFIER,                                                   ");
				sb.append("  	    LASTUPDATE                                                  ");
				sb.append("  FROM TBPMS_KPI_ADD_PROJECT                                         ");
				sb.append("  WHERE SUBSTR(DATE_YEAR,1,4) = :dateYear		                    ");
				sb.append("  AND    PERSON_TYPE = :personType      	                            ");
				sb.append("  AND    SUB_PROJECT_SEQ_ID = :subProjectSeqId      	                ");
				qc.setObject("dateYear", inputVO.getDate_year());
				qc.setObject("personType", inputVO.getPersonType());
				qc.setObject("subProjectSeqId", inputVO.getSubProjectSeqId());
				qc.setQueryString(sb.toString());
				ResultIF largeAgrList = dam.executePaging(qc, inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
				int totalPage_i = largeAgrList.getTotalPage(); // 分頁用
				int totalRecord_i = largeAgrList.getTotalRecord(); // 分頁用
				outputVO.setShowList(largeAgrList); // data
				outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());// 當前頁次
				outputVO.setTotalPage(totalPage_i);// 總頁次
				outputVO.setTotalRecord(totalRecord_i);// 總筆數
									
				sendRtnObject(outputVO);
			}
			catch (Exception e)
			{
				logger.error(String.format("發生錯誤:%s",StringUtil.getStackTraceAsString(e)));
				throw new APException(e.getMessage());
			}
	}
	//查詢人員類別
	public void queryPersonType(Object body, IPrimitiveMap header) throws JBranchException {
		PMS711InputVO inputVO = (PMS711InputVO) body;
		PMS711OutputVO outputVO = new PMS711OutputVO();
		StringBuffer sb = new StringBuffer();
		dam = this.getDataAccessManager();
		try
		{
			QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);			
			sb.append("		SELECT PARAM_TYPE,PARAM_CODE,PARAM_NAME,PARAM_DESC   ");
			sb.append("		,MAX(PARAM_CODE) OVER(ORDER BY 1) AS MAX_CODE	     ");
			sb.append("		FROM tbsysparameter                                  ");
			sb.append("		WHERE PARAM_TYPE='PMS.PERSON_TYPE'                   ");
			sb.append("		ORDER BY TO_NUMBER(PARAM_CODE)                       ");
			qc.setQueryString(sb.toString());
			List list = dam.exeQuery(qc);
			outputVO.setShowList(list); 
			/*ResultIF largeAgrList = dam.executePaging(qc, inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
			int totalPage_i = largeAgrList.getTotalPage(); // 分頁用
			int totalRecord_i = largeAgrList.getTotalRecord(); // 分頁用
			outputVO.setShowList(largeAgrList); // data
			outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());// 當前頁次
			outputVO.setTotalPage(totalPage_i);// 總頁次
			outputVO.setTotalRecord(totalRecord_i);// 總筆數
*/					
			this.sendRtnObject(outputVO);
		}
		catch (Exception e)
		{
			logger.error(String.format("發生錯誤:%s",StringUtil.getStackTraceAsString(e)));
			throw new APException(e.getMessage());
		}
	}
	
	public void updateMaintainPT(Object body, IPrimitiveMap header) throws JBranchException
	{
		PMS711InputVO inputVO = (PMS711InputVO) body;
		PMS711OutputVO outputVO = new PMS711OutputVO();
		StringBuffer sb = new StringBuffer();
		dam = this.getDataAccessManager();
		QueryConditionIF conditionDel = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb.append("		DELETE FROM TBSYSPARAMETER WHERE PARAM_TYPE='PMS.PERSON_TYPE' ");
		conditionDel.setQueryString(sb.toString());
		dam.exeUpdate(conditionDel);
		
		List<Map<String, String>> list = inputVO.getNewCustRateList();
		sb.setLength(0);
		sb.append("		INSERT INTO WMSUSER.TBSYSPARAMETER    ");
		sb.append("			(                                 ");
		sb.append("			PARAM_TYPE,                       ");
		sb.append("			PARAM_CODE,                       ");
		sb.append("			VERSION,                          ");
		sb.append("			PARAM_ORDER,                      ");
		sb.append("			PARAM_NAME,                       ");
		sb.append("			PARAM_NAME_EDIT,                  ");
		sb.append("			PARAM_DESC,                       ");
		sb.append("			PARAM_STATUS,                     ");
		sb.append("			CREATETIME,                       ");
		sb.append("			CREATOR,                          ");
		sb.append("			MODIFIER,                         ");
		sb.append("			LASTUPDATE                        ");
		sb.append("			)                                 ");
		sb.append("		VALUES                                ");
		sb.append("			(                                 ");
		sb.append("			'PMS.PERSON_TYPE',                ");
		sb.append("			:PARAM_CODE,                      ");
		sb.append("			0,                                ");
		sb.append("			:PARAM_ORDER,                     ");
		sb.append("			:PARAM_NAME,                      ");
		sb.append("			:PARAM_NAME_EDIT,                 ");
		sb.append("			:PARAM_DESC,                      ");
		sb.append("			0,                                ");
		sb.append("			sysdate,                          ");
		sb.append("			:CREATOR,                         ");
		sb.append("			:MODIFIER,                        ");
		sb.append("			sysdate                           ");
		sb.append("			)                                 ");
		try
		{
			for(int i=0; i<list.size(); i++){
				QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				condition.setObject("PARAM_CODE", list.get(i).get("PARAM_CODE"));
				condition.setObject("PARAM_ORDER", Integer.parseInt(list.get(i).get("PARAM_CODE"))-1);
				condition.setObject("PARAM_NAME", (String)list.get(i).get("PARAM_NAME"));
				condition.setObject("PARAM_NAME_EDIT", (String)list.get(i).get("PARAM_NAME"));
				condition.setObject("PARAM_DESC", (String)list.get(i).get("PARAM_DESC"));
				condition.setObject("CREATOR", (String) getUserVariable(FubonSystemVariableConsts.LOGINID));
				condition.setObject("MODIFIER", (String) getUserVariable(FubonSystemVariableConsts.LOGINID));
				condition.setQueryString(sb.toString());
				dam.exeUpdate(condition);
			}
			this.sendRtnObject(outputVO);
		}
		catch (Exception e)
		{
		logger.error(String.format("發生錯誤:%s",StringUtil.getStackTraceAsString(e)));
		throw new APException("系統發生錯誤請洽系統管理員");
		}
	}
	
	public void queryPerson(Object body, IPrimitiveMap header) throws JBranchException
	{
		PMS711InputVO inputVO = (PMS711InputVO) body;
		PMS711OutputVO outputVO = new PMS711OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("  SELECT T.PARAM_NAME AS PERSON_TYPE_NAME            ");
		sql.append("  	    ,T.PARAM_CODE AS PERSON_TYPE                 ");
		sql.append("  FROM TBSYSPARAMETER T                              ");
		sql.append("  WHERE PARAM_TYPE = 'PMS.PERSON_TYPE'               ");
		qc.setQueryString(sql.toString());
		List<Map<String, Object>> result = dam.exeQuery(qc);
		outputVO.setPersonTypeList(result);
		sendRtnObject(outputVO);
	}	

	/**
	 * 下載
	 * @param body
	 * @param header
	 * @throws Exception
	 */
	public void downLoad(Object body, IPrimitiveMap header) throws Exception {
		PMS711InputVO inputVO = (PMS711InputVO) body;
		
		if ("1".equals(inputVO.getPersonType()) || "2".equals(inputVO.getPersonType())){
			notifyClientToDownloadFile("doc//PMS//PMS711_FC_EXAMPLE.xls", "理專_KPI指標目標上傳_上傳範例.xls"); //download
		}
		else {
			notifyClientToDownloadFile("doc//PMS//PMS711_BM_EXAMPLE.xls", "分行_KPI指標目標上傳_上傳範例.xls"); //download
		}
		this.sendRtnObject(null);
	}
	
	/**
	 * 下載
	 * @param body
	 * @param header
	 * @throws Exception
	 */
	public void downLoadOther(Object body, IPrimitiveMap header) throws Exception {
		notifyClientToDownloadFile("doc//PMS//PMS711_OTHER_EXAMPLE.xls", "KPI其他非系統計算上傳範例.xls"); //download
		this.sendRtnObject(null);
	}
}