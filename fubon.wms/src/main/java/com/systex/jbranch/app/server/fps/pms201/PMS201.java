package com.systex.jbranch.app.server.fps.pms201;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBPMS_MNGR_EVAL_SCHEDULEPK;
import com.systex.jbranch.app.common.fps.table.TBPMS_MNGR_EVAL_SCHEDULEVO;
import com.systex.jbranch.app.common.fps.table.TBPMS_PRDCTVT_MASTPK;
import com.systex.jbranch.app.common.fps.table.TBPMS_PRDCTVT_MASTVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Component("pms201")
@Scope("request")
public class PMS201 extends FubonWmsBizLogic {
	
	

	/**
	 * 取得理專職級
	 * 問題單:0001575
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void aorank (Object body, IPrimitiveMap header) throws JBranchException {
		
		PMS201InputVO inputVO = (PMS201InputVO) body;
		PMS201OutputVO outputVO = new PMS201OutputVO();
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT ROWNUM AS NUM,T.*  ");
		sb.append(" FROM ( ");
		sb.append(" SELECT A.AO_JOB_RANK, ");
		sb.append(" A.JOB_RANK, ");
		sb.append(" PRO.PRODUCT_GOALS ");
		sb.append(" FROM TBPMS_EMPLOYEE_REC_N A  ");
		sb.append(" left join TBPMS_SALES_AOCODE_REC B  ");
		sb.append(" on A.EMP_ID=B.EMP_ID ");
		sb.append(" LEFT JOIN TBPMS_PRO_FUN_TR_PROD_SET PRO ");
		sb.append(" ON A.AO_JOB_RANK = PRO.JOB_TITLE_ID ");
		sb.append(" AND A.JOB_RANK = SUBSTR(PRO.OL_TITLE,2,1) ");
		sb.append(" AND PRO.DATA_YEARMON = :dataDate ");
		sb.append(" WHERE B.AO_CODE LIKE :AO_CODEID)t ");
		queryCondition.setObject("AO_CODEID","%" +inputVO.getAo_code()+"%" );
		queryCondition.setObject("dataDate", inputVO.geteTime().substring(0, 6));
		queryCondition.setQueryString(sb.toString());
		List<Map<String,Object>> list=dam.exeQuery(queryCondition);
		outputVO.setResultList(list);
		this.sendRtnObject(outputVO);
	
	}
	
	/**
	 * 取得月週列表
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void monweek (Object body, IPrimitiveMap header) throws JBranchException {
		
		PMS201OutputVO outputVO = new PMS201OutputVO();
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition();
		
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT MON_YEAR || '_' || '第' || CASE REPLACE(MTH_WEEK, 'MMW') WHEN '1' THEN '一' WHEN '2' THEN '二' WHEN '3' THEN '三' WHEN '4' THEN '四' WHEN '5' THEN '五' WHEN '6' THEN '六' END || '週_' || WEEK_START_DATE AS LABEL, WEEK_START_DATE ");
		sb.append("FROM TBPMS_DATE_REC ");
		sb.append("WHERE DATA_DATE = WEEK_START_DATE ");
		sb.append("AND DATA_DATE < (SELECT WEEK_START_DATE AS DATA_DATE FROM TBPMS_DATE_REC WHERE DATA_DATE = ").append(new SimpleDateFormat("yyyyMMdd").format(new Date())).append(") ");
		sb.append("ORDER BY DATA_DATE DESC ");

		queryCondition.setQueryString(sb.toString());
		
		outputVO.setWeekDate(dam.exeQuery(queryCondition));	
		
		this.sendRtnObject(outputVO);	
	}	
	
	/**
	 * A.收益達成目標-業績表現達成率
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void getResultBehaveRate (Object body, IPrimitiveMap header) throws JBranchException {
		
		PMS201InputVO inputVO = (PMS201InputVO) body;
		PMS201OutputVO outputVO = new PMS201OutputVO();
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF queryCondition;
		
		// 取得日期區間，用於HTML TR 顯示
		StringBuffer yyyyMMSb = new StringBuffer();
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		yyyyMMSb.append("  SELECT ROWNUM AS YM_NUM, YYYYMM ");
		yyyyMMSb.append("  FROM ( ");
		yyyyMMSb.append("    SELECT YYYYMM ");
		yyyyMMSb.append("    FROM ( ");
		yyyyMMSb.append("      SELECT TO_CHAR(TO_DATE(:dataDate, 'yyyyMM') - (level - 1), 'yyyyMM') AS YYYYMM ");
		yyyyMMSb.append("      FROM DUAL ");
		yyyyMMSb.append("      CONNECT BY TRUNC(TO_DATE(TO_CHAR(ADD_MONTHS(TO_DATE(:dataDate, 'yyyyMM'), -2), 'yyyyMM'), 'yyyyMM')) + level - 1 <= TRUNC(TO_DATE(:dataDate, 'yyyyMM')) ");
		yyyyMMSb.append("    ) ");
		yyyyMMSb.append("    GROUP BY YYYYMM ");
		yyyyMMSb.append("    ORDER BY YYYYMM DESC ");
		yyyyMMSb.append("  ) ");
		queryCondition.setObject("dataDate", inputVO.geteTime().substring(0, 6));
		
		queryCondition.setQueryString(yyyyMMSb.toString());
		
		outputVO.setaGroupTitleList(dam.exeQuery(queryCondition));
		
		// 取得業績表現達成率
		StringBuffer sb = new StringBuffer();
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb.append("WITH YM_LIST AS ( ");
		sb.append(yyyyMMSb);
		sb.append("), RATE_LIST AS ( ");
		sb.append("  SELECT SUBSTR(DATA_DATE, 0 ,6) AS YYYYMM, AO_CODE, EMP_ID, JOB_TITLE_ID AS AO_JOB_RANK, SUM_TYPE, NVL(TOT_A_RATE_1, 0) AS RATE ");
		sb.append("  FROM TBPMS_AO_ACHD_RATE AAR ");
		sb.append("  WHERE AAR.DATA_DATE IN ( ");
		sb.append("    SELECT MONTH_MAX_DAY ");
		sb.append("    FROM ( ");
		sb.append("      SELECT SUBSTR(DATA_DATE, 0 ,6) AS YYYYMM, MAX(DATA_DATE) AS MONTH_MAX_DAY ");
		sb.append("      FROM TBPMS_BR_ACHD_RATE ");
		sb.append("      WHERE SUBSTR(DATA_DATE, 0, 6) IN (SELECT YYYYMM FROM YM_LIST) ");
		sb.append("      GROUP BY SUBSTR(DATA_DATE, 0 ,6) ");
		sb.append("      ORDER BY MONTH_MAX_DAY DESC ");
		sb.append("	    ) ");
		sb.append("  ) ");
		sb.append("  AND AAR.AO_CODE = :aoCode ");
		sb.append("), AVG_LIST AS ( ");
		sb.append("  SELECT SUBSTR(DATA_DATE, 0 ,6) AS YYYYMM, SUM_TYPE, AO_JOB_RANK, RATE AS AVG_R ");
		sb.append("  FROM ( ");
		sb.append("    SELECT DATA_DATE, SUM_TYPE, REPLACE(COLUMN_NAME, '_TOT_A_RATE_1') AS AO_JOB_RANK, RATE ");
		sb.append("    FROM TBPMS_AO_ACHD_RATE_AVG ");
		sb.append("    UNPIVOT (RATE FOR COLUMN_NAME IN (FC1_TOT_A_RATE_1, FC2_TOT_A_RATE_1, FC3_TOT_A_RATE_1, FC4_TOT_A_RATE_1, FC5_TOT_A_RATE_1, FCH_TOT_A_RATE_1)) ");
		sb.append("  ) AARA ");
		sb.append("  WHERE AARA.DATA_DATE IN ( ");
		sb.append("    SELECT MONTH_MAX_DAY ");
		sb.append("    FROM ( ");
		sb.append("      SELECT SUBSTR(DATA_DATE, 0 ,6) AS YYYYMM, MAX(DATA_DATE) AS MONTH_MAX_DAY ");
		sb.append("      FROM TBPMS_AO_ACHD_RATE_AVG ");
		sb.append("      WHERE SUBSTR(DATA_DATE, 0, 6) IN (SELECT YYYYMM FROM YM_LIST) ");
		sb.append("      GROUP BY SUBSTR(DATA_DATE, 0 ,6) ");
		sb.append("      ORDER BY MONTH_MAX_DAY DESC ");
		sb.append("    ) ");
		sb.append("  ) ");
		sb.append("), RANK_LIST AS ( ");
		sb.append("  SELECT SUBSTR(DATA_DATE, 0 ,6) AS YYYYMM, AO_CODE, EMP_ID, SUM_TYPE, BK_RANK_LVL ");
		sb.append("  FROM TBPMS_AO_ACHD_RANK AAR ");
		sb.append("  WHERE AAR.DATA_DATE IN ( ");
		sb.append("    SELECT MONTH_MAX_DAY ");
		sb.append("      FROM ( ");
		sb.append("        SELECT SUBSTR(DATA_DATE, 0 ,6) AS YYYYMM, MAX(DATA_DATE) AS MONTH_MAX_DAY ");
		sb.append("        FROM TBPMS_AO_ACHD_RANK ");
		sb.append("        WHERE SUBSTR(DATA_DATE, 0, 6) IN (SELECT YYYYMM FROM YM_LIST) ");
		sb.append("        GROUP BY SUBSTR(DATA_DATE, 0 ,6) ");
		sb.append("        ORDER BY MONTH_MAX_DAY DESC ");
		sb.append("      ) ");
		sb.append("  ) ");
		sb.append("  AND AAR.AO_CODE = :aoCode ");
		sb.append("), MAIN_T AS ( ");
		sb.append("  SELECT YM_LIST.YM_NUM, YM_LIST.YYYYMM, AO.EMP_ID, AO.AO_CODE, AO.AO_JOB_RANK ");
		sb.append("  FROM YM_LIST ");
		sb.append("  LEFT JOIN TBPMS_PRDCTVT_MAST AO ON 1 = 1 ");
		sb.append("  WHERE AO.AO_CODE = :aoCode ");
		sb.append(") ");
		
		sb.append(", TOT_LVL AS ( ");
		sb.append("  SELECT DISTINCT MAIN_T.YM_NUM, LT.AO_JOB_RANK, LT.LVL ");
		sb.append("  FROM MAIN_T ");
		sb.append("  LEFT JOIN ( ");
		sb.append("    SELECT SUBSTR(DATA_DATE, 0 ,6) AS YYYYMM, COLUMN_NAME AS AO_JOB_RANK, LVL ");
		sb.append("    FROM ( ");
		sb.append("      SELECT DATA_DATE, COLUMN_NAME, LVL ");
		sb.append("      FROM ( ");
		sb.append("        SELECT DATA_DATE, FC1, FC2, FC3, FC4, FC5, FCH ");
		sb.append("        FROM TBPMS_AO_ACHD_RANK_BASE ");
		sb.append("        WHERE RANK_ORG_TYPE = 'BK' ");
		sb.append("      ) ");
		sb.append("      UNPIVOT (LVL FOR COLUMN_NAME IN (FC1, FC2, FC3, FC4, FC5, FCH)) ");
		sb.append("    ) ");
		sb.append("    WHERE DATA_DATE IN ( ");
		sb.append("      SELECT MONTH_MAX_DAY ");
		sb.append("      FROM ( ");
		sb.append("        SELECT SUBSTR(DATA_DATE, 0 ,6) AS YYYYMM, MAX(DATA_DATE) AS MONTH_MAX_DAY ");
		sb.append("        FROM TBPMS_AO_ACHD_RANK ");
		sb.append("        WHERE SUBSTR(DATA_DATE, 0, 6) IN (SELECT YYYYMM FROM YM_LIST) ");
		sb.append("        GROUP BY SUBSTR(DATA_DATE, 0 ,6) ");
		sb.append("        ORDER BY MONTH_MAX_DAY DESC ");
		sb.append("      ) ");
		sb.append("    ) ");
		sb.append("    AND (SUBSTR(DATA_DATE, 0 ,6), COLUMN_NAME) IN (SELECT YYYYMM, AO_JOB_RANK FROM MAIN_T) ");
		sb.append("  ) LT ON MAIN_T.YYYYMM = LT.YYYYMM AND MAIN_T.AO_JOB_RANK = LT.AO_JOB_RANK ");
		sb.append(") ");

		sb.append("SELECT PARAM_CODE AS COLUMN_NAME, MONTH_1, MONTH_2, MONTH_3 ");
		sb.append("FROM (SELECT PARAM_CODE, PARAM_NAME, PARAM_ORDER FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'PMS.PERFORMANCE_TITLE_NAME') SYS_P ");
		sb.append("LEFT JOIN ( ");
		sb.append("  SELECT COLUMN_NAME, ");
		sb.append("         NVL(MONTH_1, 0) || CASE WHEN COLUMN_NAME IN ('M_RANKING', 'Y_RANKING') THEN ('/' || (SELECT LVL FROM TOT_LVL WHERE YM_NUM = '1')) ELSE '' END AS MONTH_1, "); 
		sb.append("         NVL(MONTH_2, 0) || CASE WHEN COLUMN_NAME IN ('M_RANKING', 'Y_RANKING') THEN ('/' || (SELECT LVL FROM TOT_LVL WHERE YM_NUM = '2')) ELSE '' END AS MONTH_2, ");
		sb.append("         NVL(MONTH_3, 0) || CASE WHEN COLUMN_NAME IN ('M_RANKING', 'Y_RANKING') THEN ('/' || (SELECT LVL FROM TOT_LVL WHERE YM_NUM = '3')) ELSE '' END AS MONTH_3 ");
		sb.append("  FROM ( ");
		sb.append("    SELECT COLUMN_NAME, MONTH_1, MONTH_2, MONTH_3 ");
		sb.append("    FROM ( ");
		sb.append("      SELECT YM_NUM, COLUMN_NAME, COLUMN_T ");
		sb.append("      FROM ( ");
		sb.append("        SELECT MAIN_T.YM_NUM, 'M_FEE' COLUMN_NAME, RL_M.RATE AS COLUMN_T ");
		sb.append("        FROM MAIN_T ");
		sb.append("        LEFT JOIN RATE_LIST RL_M ON MAIN_T.AO_CODE = RL_M.AO_CODE AND MAIN_T.YYYYMM = RL_M.YYYYMM AND RL_M.SUM_TYPE = 'M' ");
		sb.append("       UNION ");
		sb.append("        SELECT MAIN_T.YM_NUM, 'M_AVG' COLUMN_NAME, AL_M.AVG_R AS COLUMN_T ");
		sb.append("        FROM MAIN_T ");
		sb.append("        LEFT JOIN AVG_LIST AL_M ON MAIN_T.AO_JOB_RANK = AL_M.AO_JOB_RANK AND MAIN_T.YYYYMM = AL_M.YYYYMM AND AL_M.SUM_TYPE = 'M' ");
		sb.append("        UNION ");
		sb.append("        SELECT MAIN_T.YM_NUM, 'M_RANKING' COLUMN_NAME, KL_M.BK_RANK_LVL AS COLUMN_T ");
		sb.append("        FROM MAIN_T ");
		sb.append("        LEFT JOIN RANK_LIST KL_M ON MAIN_T.AO_CODE = KL_M.AO_CODE AND MAIN_T.YYYYMM = KL_M.YYYYMM AND KL_M.SUM_TYPE = 'M' ");
		sb.append("        UNION ");
		sb.append("        SELECT MAIN_T.YM_NUM, 'Y_FEE' COLUMN_NAME, RL_Y.RATE AS COLUMN_T ");
		sb.append("        FROM MAIN_T ");
		sb.append("        LEFT JOIN RATE_LIST RL_Y ON MAIN_T.AO_CODE = RL_Y.AO_CODE AND MAIN_T.YYYYMM = RL_Y.YYYYMM AND RL_Y.SUM_TYPE = 'Y' ");
		sb.append("        UNION ");
		sb.append("        SELECT MAIN_T.YM_NUM, 'Y_AVG' COLUMN_NAME, AL_Y.AVG_R AS COLUMN_T ");
		sb.append("        FROM MAIN_T ");
		sb.append("        LEFT JOIN AVG_LIST AL_Y ON MAIN_T.AO_JOB_RANK = AL_Y.AO_JOB_RANK AND MAIN_T.YYYYMM = AL_Y.YYYYMM AND AL_Y.SUM_TYPE = 'Y' ");
		sb.append("        UNION ");
		sb.append("        SELECT MAIN_T.YM_NUM, 'Y_RANKING' COLUMN_NAME, KL_Y.BK_RANK_LVL AS COLUMN_T ");
		sb.append("        FROM MAIN_T ");
		sb.append("        LEFT JOIN RANK_LIST KL_Y ON MAIN_T.AO_CODE = KL_Y.AO_CODE AND MAIN_T.YYYYMM = KL_Y.YYYYMM AND KL_Y.SUM_TYPE = 'Y' ");
		sb.append("        ) ");
		sb.append("    ) PIVOT (SUM(COLUMN_T) FOR (YM_NUM) IN ('1' AS MONTH_1, '2' AS MONTH_2, '3' AS MONTH_3)) ");
		sb.append("  ) ");
		sb.append(") RES ON SYS_P.PARAM_CODE = RES.COLUMN_NAME ");
		sb.append("ORDER BY SYS_P.PARAM_ORDER ");
		
		queryCondition.setObject("dataDate", inputVO.geteTime().substring(0, 6));
		queryCondition.setObject("aoCode", inputVO.getAo_code());
		
		queryCondition.setQueryString(sb.toString());
		
		outputVO.setResultBehaveRate(dam.exeQuery(queryCondition));
		
		this.sendRtnObject(outputVO);
	}
	
	/**
	 * A.收益達成目標-戰力&Coaching活動/理專行動計劃
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void getEmpData (Object body, IPrimitiveMap header) throws JBranchException {
		
		PMS201InputVO inputVO = (PMS201InputVO) body;
		PMS201OutputVO outputVO = new PMS201OutputVO();
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		StringBuffer sb = new StringBuffer();
//		sb.append("SELECT PM.EMP_ID, PM.EMP_NAME, PM.AO_JOB_RANK, PM.SALES_SUP_EMP_NAME, PM.ACT_RATE, MES.COACHING_FREQ, MES.COACHING_MAIN, PM.SELF_NOTE, BG.TARGET_AMT, BG.EST_RATE, TO_DATE(BG.WEEK_START_DATE, 'yyyy/MM/dd') AS WEEK_START_DATE, TO_DATE(BG.WEEK_END_DATE, 'yyyy/MM/dd') AS WEEK_END_DATE ");
//		sb.append("FROM TBPMS_PRDCTVT_MAST PM ");
//		sb.append("LEFT JOIN ( ");
//		sb.append("  SELECT AO_CODE, COACHING_FREQ, LISTAGG(COACHING_MAIN, '\n') WITHIN GROUP (ORDER BY COACHING_MAIN) AS COACHING_MAIN ");
//		sb.append("  FROM ( ");
//		sb.append("    SELECT MES.AO_CODE, MES.COACHING_FREQ, MES.POINT_TYPE || '.' || SP.PARAM_NAME AS COACHING_MAIN ");
//		sb.append("    FROM ( ");
//		sb.append("      SELECT AO_CODE, COACHING_FREQ, REPLACE(COLUMN_NAME, 'COACHING_POINT_') AS POINT_TYPE, COACHING_POINT ");
//		sb.append("      FROM TBPMS_MNGR_EVAL_SCHEDULE ");
//		sb.append("      UNPIVOT (COACHING_POINT FOR COLUMN_NAME IN (COACHING_POINT_A, COACHING_POINT_B, COACHING_POINT_C, COACHING_POINT_D)) ");
//		sb.append("    ) MES ");
//		sb.append("    LEFT JOIN TBSYSPARAMETER SP ON SP.PARAM_TYPE = 'PMS.COACHING_POINT' AND SP.PARAM_CODE = MES.COACHING_POINT ");
//		sb.append("  ) ");
//		sb.append("  GROUP BY AO_CODE, COACHING_FREQ ");
//		sb.append(") MES ON PM.AO_CODE = MES.AO_CODE ");
//		sb.append("LEFT JOIN ( ");
//		sb.append("  SELECT DM.AO_CODE, DM.TARGET_AMT, DM.EST_RATE, DR.WEEK_START_DATE, DR.WEEK_END_DATE ");
//		sb.append("  FROM TBPMS_DATE_REC DR ");
//		sb.append("  LEFT JOIN TBPMS_AO_DIFF_MAST DM ON DM.YEARMON = SUBSTR(:weekStartDate, 0, 6) ");
//		sb.append("  WHERE DR.DATA_DATE = :weekStartDate ");
//		sb.append(") BG ON PM.AO_CODE = BG.AO_CODE ");
//		sb.append("WHERE PM.AO_CODE = :aoCode ");
//		sb.append("AND BG.WEEK_START_DATE = :weekStartDate ");
		
		sb.append("SELECT REC.EMP_ID, B.EMP_NAME, B.AO_JOB_RANK, PM.SALES_SUP_EMP_NAME, PM.ACT_RATE, ");
		sb.append("	   MES.COACHING_FREQ, MES.COACHING_MAIN, PM.SELF_NOTE,  BG.TARGET_AMT, BG.EST_RATE, ");
		sb.append("	   TO_DATE(BG.WEEK_START_DATE, 'YYYY/MM/DD') AS WEEK_START_DATE, ");
		sb.append("	   TO_DATE(BG.WEEK_END_DATE, 'YYYY/MM/DD') AS WEEK_END_DATE ");
		sb.append("FROM TBPMS_SALES_AOCODE_REC REC ");
		sb.append("LEFT JOIN TBPMS_EMPLOYEE_REC_N B ON REC.EMP_ID = B.EMP_ID ");
		sb.append("LEFT JOIN TBPMS_PRDCTVT_MAST PM ON PM.EMP_ID = REC.EMP_ID AND PM.WEEK_START_DATE = :weekStartDate ");
		sb.append("LEFT JOIN ( ");
		sb.append("  SELECT AO_CODE, COACHING_FREQ, LISTAGG(COACHING_MAIN, ' ') WITHIN GROUP (ORDER BY COACHING_MAIN) AS COACHING_MAIN ");
		sb.append("  FROM ( ");
		sb.append("    SELECT MES.AO_CODE, MES.COACHING_FREQ, MES.POINT_TYPE || '.' || SP.PARAM_NAME AS COACHING_MAIN ");
		sb.append("    FROM ( ");
		sb.append("      SELECT DATE_S, AO_CODE, COACHING_FREQ, REPLACE(COLUMN_NAME, 'COACHING_POINT_') AS POINT_TYPE, COACHING_POINT ");
		sb.append("  	 FROM TBPMS_MNGR_EVAL_SCHEDULE UNPIVOT (COACHING_POINT FOR COLUMN_NAME IN (COACHING_POINT_A, COACHING_POINT_B, COACHING_POINT_C, COACHING_POINT_D)) ");
		sb.append(" 	 WHERE SUBSTR(DATE_S, 0, 6) = SUBSTR(:weekStartDate, 0, 6) ");
		sb.append("	   ) MES ");
		sb.append("    LEFT JOIN TBSYSPARAMETER SP ON SP.PARAM_TYPE = 'PMS.COACHING_POINT' AND SP.PARAM_CODE = MES.COACHING_POINT ");
		sb.append("  ) ");
		sb.append("  GROUP BY AO_CODE, COACHING_FREQ ");
		sb.append(") MES ON PM.AO_CODE = MES.AO_CODE ");
		sb.append("LEFT JOIN ( ");
		sb.append("  SELECT DM.AO_CODE, DM.TARGET_AMT, DM.EST_RATE, DR.WEEK_START_DATE, DR.WEEK_END_DATE ");
		sb.append("  FROM TBPMS_DATE_REC DR ");
		sb.append("  LEFT JOIN TBPMS_AO_DIFF_MAST DM ON DM.YEARMON = SUBSTR(:weekStartDate, 0, 6) ");
		sb.append("  WHERE DR.DATA_DATE = :weekStartDate ");
		sb.append(") BG ON PM.AO_CODE = BG.AO_CODE ");
		sb.append("WHERE REC.AO_CODE = :aoCode ");
		sb.append("AND TO_DATE(:weekStartDate, 'yyyyMMdd') BETWEEN REC.START_TIME AND REC.END_TIME ");
		//2017/05/23  SQL調整   增加EMP_ID  由歷史主檔產生
		
		queryCondition.setObject("weekStartDate", inputVO.geteTime());
		queryCondition.setObject("aoCode", inputVO.getAo_code());
		
		queryCondition.setQueryString(sb.toString());
		
		outputVO.setEmpDtl(dam.exeQuery(queryCondition));
		
		this.sendRtnObject(outputVO);
	}
	
	/**
	 * A.收益達成目標-理專行動計劃
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void save (Object body, IPrimitiveMap header) throws JBranchException {
		
		PMS201InputVO inputVO = (PMS201InputVO) body;
		Date today = new Date();
		try{
			TBPMS_PRDCTVT_MASTPK pk = new TBPMS_PRDCTVT_MASTPK();
			TBPMS_PRDCTVT_MASTVO vo = new TBPMS_PRDCTVT_MASTVO();
			TBPMS_MNGR_EVAL_SCHEDULEPK SCHEDULEPK = new TBPMS_MNGR_EVAL_SCHEDULEPK();
			TBPMS_MNGR_EVAL_SCHEDULEVO SCHEDULEVO = new TBPMS_MNGR_EVAL_SCHEDULEVO();
			DataAccessManager dam = this.getDataAccessManager();
			pk.setWEEK_START_DATE(inputVO.geteTime());
			pk.setAO_CODE(inputVO.getAo_code());
			pk.setEMP_ID(inputVO.getEmpID());
			
			vo = (TBPMS_PRDCTVT_MASTVO) dam.findByPKey(
					TBPMS_PRDCTVT_MASTVO.TABLE_UID, pk);
			if ( vo != null ) {
				vo.setSELF_NOTE(inputVO.getSelfNote());
				vo.setC_DATE(new Timestamp(today.getTime()));
				vo.setModifier((String) getUserVariable(FubonSystemVariableConsts.LOGINID));
				dam.update(vo);
			}else {
				// 顯示資料不存在
				throw new APException("ehl_01_common_005");
			}
			SCHEDULEPK.setDATE_S(inputVO.geteTime());
			SCHEDULEPK.setAO_CODE(inputVO.getAo_code());
			
			SCHEDULEVO = (TBPMS_MNGR_EVAL_SCHEDULEVO) dam.findByPKey(
					TBPMS_MNGR_EVAL_SCHEDULEVO.TABLE_UID, SCHEDULEPK);
			
			if( SCHEDULEVO != null ){
				SCHEDULEVO.setDIR_REV(inputVO.getSelfNote());
				SCHEDULEVO.setModifier((String) getUserVariable(FubonSystemVariableConsts.LOGINID));
				SCHEDULEVO.setFIN_DATE(new Timestamp(today.getTime()));
				SCHEDULEVO.setFIN_TYPE("1");
				
				dam.update(SCHEDULEVO);
			}else {
				// 顯示資料不存在
				throw new APException("ehl_01_common_005");
			}
			
			this.sendRtnObject(null);
		}catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}
	
	/**
	 * B.產品組合指標-產品組合指標/本月追蹤商品明細
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void getProdGroupIndex (Object body, IPrimitiveMap header) throws JBranchException {
		
		PMS201InputVO inputVO = (PMS201InputVO) body;
		PMS201OutputVO outputVO = new PMS201OutputVO();
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF queryCondition;
		
		// 取得日期區間，用於HTML TR 顯示
		StringBuffer weekSb = new StringBuffer();
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		weekSb.append("  SELECT ROWNUM AS WEEK_NUM, WEEK_START_DATE, WEEK_END_DATE ");
		weekSb.append("  FROM ( ");
		weekSb.append("    SELECT WEEK_START_DATE, WEEK_END_DATE ");
		weekSb.append("    FROM ( ");
		weekSb.append("      SELECT WEEK_START_DATE, WEEK_END_DATE ");
		weekSb.append("      FROM (SELECT MIN(DATA_DATE) AS DATA_DATE, WEEK_START_DATE, WEEK_END_DATE FROM TBPMS_DATE_REC GROUP BY WEEK_START_DATE, WEEK_END_DATE) ");
		weekSb.append("      WHERE DATA_DATE <= :dataDate ");
		weekSb.append("      ORDER BY DATA_DATE DESC ");
		weekSb.append("    ) ");
		weekSb.append("    WHERE ROWNUM <= 5 ");
		weekSb.append("    ORDER BY WEEK_START_DATE DESC");
		weekSb.append("  ) ");
		weekSb.append("  ORDER BY ROWNUM ");
		queryCondition.setQueryString(weekSb.toString());
		queryCondition.setObject("dataDate", inputVO.geteTime());
		
		outputVO.setbGroupTitleList(dam.exeQuery(queryCondition));
		// 取得產品組合指標
		StringBuffer sb = new StringBuffer();
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb.append("WITH WEEK_DATE AS ( ");
		sb.append(weekSb);
		sb.append("), WEEK_ROI AS ( ");
		sb.append("  SELECT WEEK_START_DATE, AO_JOB_RANK, ITEM, ROI ");
		sb.append("  FROM ( ");
		sb.append("    SELECT WEEK_START_DATE, AO_JOB_RANK, NVL((SELECT PARAM_CODE FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'PMS.ROI_ITEM_MAPPING' AND PARAM_NAME = REPLACE(ROLE_TYPE, '_ROI')), ' ') AS ITEM, ROI "); 
		sb.append("    FROM TBPMS_PRDCTVT_PRTFL_AVG ");
		sb.append("    UNPIVOT (ROI FOR ROLE_TYPE IN (FUND_ROI, ETF_ROI, STOCK_ROI, BOND_ROI, SI_ROI, SN_ROI, DCI_ROI, INV_ROI, OT_ROI, SY_ROI, LY_ROI, IV_ROI, INS_ROI, OTH_ROI_1, OTH_ROI_2, TOTAL_ROI)) ");
		sb.append("    WHERE WEEK_START_DATE = :dataDate2 ");
		sb.append("  ) ");
		sb.append("  WHERE TRIM(ITEM) IS NOT NULL ");
		sb.append("), WEEK_DATA AS ( ");
		sb.append("  SELECT WD.ITEM, WD.AO_CODE, WD.WEEK_1, WD.WEEK_2, WD.WEEK_3, WD.WEEK_4, WD.WEEK_5, AO.AO_JOB_RANK, NVL(WR.ROI, 0) AS ROI ");
		sb.append("  FROM ( ");
		sb.append("    SELECT ITEM, AO_CODE, NVL(WEEK_1, 0) AS WEEK_1, NVL(WEEK_2, 0) AS WEEK_2, NVL(WEEK_3, 0) AS WEEK_3, NVL(WEEK_4, 0) AS WEEK_4, NVL(WEEK_5, 0) AS WEEK_5 ");
		sb.append("    FROM ( ");
		sb.append("      SELECT WEEK_NUM, ITEM, AO_CODE, NVL(SUM(FEE), 0) AS FEE ");
		sb.append("      FROM ( ");
		sb.append("        SELECT DR.WEEK_NUM, ITEM_LIST.ITEM, ");
		sb.append("               CASE WHEN AO.AO_CODE IS NULL THEN :aoCode ELSE AO.AO_CODE END AS AO_CODE, "); 
		sb.append("               ADP.FEE ");
		sb.append("        FROM WEEK_DATE DR ");
		sb.append("        LEFT JOIN (SELECT PARAM_CODE AS ITEM FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'PMS.ROI_ITEM_MAPPING') ITEM_LIST ON 1 = 1 ");
		sb.append("        LEFT JOIN ( ");
		sb.append("         SELECT DATA_DATE, EMP_ID, AO_CODE, AO_JOB_RANK, FEE, ");
		sb.append("                 CASE WHEN ITEM = '07' THEN '02' ");
		sb.append("                      WHEN ITEM = '08' THEN '03' ");
		sb.append("                      WHEN ITEM = '13' THEN '05' ");
		sb.append("                 ELSE ITEM END AS ITEM ");
		sb.append("          FROM TBPMS_AO_DAY_PROFIT ");
		sb.append("        ) ADP ON ADP.DATA_DATE >= DR.WEEK_START_DATE AND ADP.DATA_DATE <= DR.WEEK_END_DATE AND ADP.ITEM = ITEM_LIST.ITEM AND ADP.AO_CODE = :aoCode ");
		sb.append("        LEFT JOIN TBPMS_PRDCTVT_MAST AO ON AO.AO_CODE = :aoCode AND AO.WEEK_START_DATE = DR.WEEK_START_DATE ");
		sb.append("     ) ");
		sb.append("     GROUP BY WEEK_NUM, ITEM, AO_CODE ");
		sb.append("   ) PIVOT (SUM(FEE) FOR (WEEK_NUM) IN ('1' AS WEEK_1, '2' AS WEEK_2, '3' AS WEEK_3, '4' AS WEEK_4, '5' AS WEEK_5)) ");
		sb.append("   ORDER BY DECODE(ITEM, '01', 1, '06' , 2, '17', 3, '05', 4, '02', 5, '03', 6, '04', 7, '10', 8, '11', 9, '12', 10, '16', 11, '09', 12, '15', 13, 99) ");
		sb.append("  ) WD ");
		sb.append("  LEFT JOIN TBPMS_PRDCTVT_MAST AO ON WD.AO_CODE = AO.AO_CODE AND AO.WEEK_START_DATE = :dataDate ");
		sb.append("  LEFT JOIN WEEK_ROI WR ON TO_CHAR(WD.ITEM) = TO_CHAR(WR.ITEM) AND AO.AO_JOB_RANK = WR.AO_JOB_RANK ");
		sb.append(") ");
				
		sb.append("SELECT PROD_TYPE, ITEM, AO_CODE, WEEK_1, WEEK_2, WEEK_3, WEEK_4, WEEK_5, NVL(ROI, 0) AS ROI ");
		sb.append("FROM ( ");
		sb.append("  SELECT '投資型產品收益' AS PROD_TYPE, 1 AS PROD_TYPE_ORDER, WD.ITEM, WD.AO_CODE, WD.WEEK_1, WD.WEEK_2, WD.WEEK_3, WD.WEEK_4, WD.WEEK_5, WD.ROI ");
		sb.append("  FROM WEEK_DATA WD ");
		sb.append("  WHERE WD.ITEM IN ('01', '06', '17', '05', '02', '03', '04') ");
		sb.append("  UNION ");
		sb.append("  SELECT '投資型產品收益' AS PROD_TYPE, 1 AS PROD_TYPE_ORDER, '小計' AS ITEM, WD.AO_CODE, SUM(WD.WEEK_1), SUM(WD.WEEK_2), SUM(WD.WEEK_3), SUM(WD.WEEK_4), SUM(WD.WEEK_5), SUM(WR.ROI) ");
		sb.append("  FROM WEEK_DATA WD ");
		sb.append("  LEFT JOIN WEEK_ROI WR ON TO_CHAR(WD.ITEM) = TO_CHAR(WR.ITEM) AND WD.AO_JOB_RANK = WR.AO_JOB_RANK ");
		sb.append("  WHERE WD.ITEM IN ('01', '06', '17', '05', '02', '03', '04') ");
		sb.append("  GROUP BY WD.AO_CODE ");
		sb.append("  UNION ");
		sb.append("  SELECT '保險產品收益' AS PROD_TYPE, 2 AS PROD_TYPE_ORDER, WD.ITEM, WD.AO_CODE, WD.WEEK_1, WD.WEEK_2, WD.WEEK_3, WD.WEEK_4, WD.WEEK_5, WR.ROI ");
		sb.append("  FROM WEEK_DATA WD ");
		sb.append("  LEFT JOIN WEEK_ROI WR ON TO_CHAR(WD.ITEM) = TO_CHAR(WR.ITEM) AND WD.AO_JOB_RANK = WR.AO_JOB_RANK ");
		sb.append("  WHERE WD.ITEM IN ('10', '11', '12', '16') ");
		sb.append("  UNION ");
		sb.append("  SELECT '保險產品收益' AS PROD_TYPE, 2 AS PROD_TYPE_ORDER, '小計' AS ITEM, WD.AO_CODE, SUM(WD.WEEK_1), SUM(WD.WEEK_2), SUM(WD.WEEK_3), SUM(WD.WEEK_4), SUM(WD.WEEK_5), SUM(WR.ROI) ");
		sb.append("  FROM WEEK_DATA WD ");
		sb.append("  LEFT JOIN WEEK_ROI WR ON TO_CHAR(WD.ITEM) = TO_CHAR(WR.ITEM) AND WD.AO_JOB_RANK = WR.AO_JOB_RANK ");
		sb.append("  WHERE WD.ITEM IN ('10', '11', '12', '16') ");
		sb.append("  GROUP BY WD.AO_CODE ");
		sb.append("  UNION ");
		sb.append("  SELECT '其他收益' AS PROD_TYPE, 3 AS PROD_TYPE_ORDER, WD.ITEM, WD.AO_CODE, WD.WEEK_1, WD.WEEK_2, WD.WEEK_3, WD.WEEK_4, WD.WEEK_5, WR.ROI ");
		sb.append("  FROM WEEK_DATA WD ");
		sb.append("  LEFT JOIN WEEK_ROI WR ON TO_CHAR(WD.ITEM) = TO_CHAR(WR.ITEM) AND WD.AO_JOB_RANK = WR.AO_JOB_RANK ");
		sb.append("  WHERE WD.ITEM IN ('09', '15') ");
		sb.append("  UNION ");
		sb.append("  SELECT '合計總收益' AS PROD_TYPE, 4 AS PROD_TYPE_ORDER, '' AS ITEM, WD.AO_CODE, SUM(NVL(WD.WEEK_1, 0)), SUM(NVL(WD.WEEK_2, 0)), SUM(NVL(WD.WEEK_3, 0)), SUM(NVL(WD.WEEK_4, 0)), SUM(NVL(WD.WEEK_5, 0)), SUM(NVL(WR.ROI, 0)) ");
		sb.append("  FROM WEEK_DATA WD ");
		sb.append("  LEFT JOIN WEEK_ROI WR ON TO_CHAR(WD.ITEM) = TO_CHAR(WR.ITEM) AND WD.AO_JOB_RANK = WR.AO_JOB_RANK ");
		sb.append("  GROUP BY WD.AO_CODE ");
		sb.append(") ");
		sb.append("ORDER BY PROD_TYPE_ORDER, DECODE(ITEM, '01', 1, '06' , 2, '17', 3, '05', 4, '02', 5, '03', 6, '04', 7, '10', 8, '11', 9, '12', 10, '16', 11, '09', 12, '15', 13, 99) ");
		
		
		//dataDate2預計作為上周FCX均值的日期使用，目前暫不使用，維持原先本周的日期
//		System.err.println("================ " + inputVO.geteTime2() + " ===================" + inputVO.geteTime());
		queryCondition.setObject("dataDate", inputVO.geteTime());
		queryCondition.setObject("dataDate2", inputVO.geteTime());
		queryCondition.setObject("aoCode", inputVO.getAo_code());
		
		queryCondition.setQueryString(sb.toString());
		
		outputVO.setProdGroupIndex(dam.exeQuery(queryCondition));	
		
		//取得本月追蹤商品明細
		sb = new StringBuffer();
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb.append("SELECT AO.EMP_ID, AO.AO_CODE, AO.AO_JOB_RANK, AO.PRD_ID, TEMP_F.MAIN_COM_NBR_NAME, SUM(NVL(AO_PRD.BAL, 0)) AS T_BAL, SUM(NVL(AO_PRD.FEE, 0)) AS T_FEE ");
		sb.append("FROM TBPMS_AO_PRD_PROFIT AO, ");
		sb.append("(SELECT DISTINCT MAIN_COM_NBR, MAIN_COM_NBR_NAME, TRIM(REGEXP_SUBSTR(STR, '[^,]+', 1, LEVEL)) STR ");
		sb.append(" FROM (SELECT DATA_YEARMON, MAIN_COM_NBR, MAIN_COM_NBR_NAME, REPLACE(MAIN_COM_NBR || ',' || REL_COM_NBR, ' ') AS STR ");
		sb.append("       FROM TBPMS_TRACK_PRO_SET ");
		sb.append("       WHERE DATA_YEARMON = :dataYearMon) T ");
		sb.append(" CONNECT BY INSTR(STR, ',', 1, LEVEL - 1) > 0) TEMP_F ");
		sb.append("LEFT JOIN TBPMS_AO_PRD_PROFIT AO_PRD ON AO_PRD.PRD_ID = TEMP_F.STR ");
		sb.append("WHERE AO.PRD_ID = TEMP_F.MAIN_COM_NBR ");
		sb.append("AND SUBSTR(AO.DATA_DATE, 0 ,6) = :dataYearMon ");
		sb.append("AND AO.AO_CODE = :aoCode ");
		sb.append("AND TEMP_F.STR IS NOT NULL ");
		sb.append("GROUP BY AO.EMP_ID, AO.AO_CODE, AO.AO_JOB_RANK, AO.PRD_ID, TEMP_F.MAIN_COM_NBR_NAME ");
		sb.append("ORDER BY T_BAL DESC, T_FEE DESC ");
		sb.append("FETCH FIRST 3 ROWS ONLY ");
		
		queryCondition.setObject("dataYearMon", inputVO.geteTime().substring(0, 6));
		queryCondition.setObject("aoCode", inputVO.getAo_code());
		
		queryCondition.setQueryString(sb.toString());
		
		outputVO.setProdTrackDtl(dam.exeQuery(queryCondition));
		
		this.sendRtnObject(outputVO);
	}	
	

	
	
	
	/**
	 * C.客戶經營指標-客戶經營指標1
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void getPrdctvt1 (Object body, IPrimitiveMap header) throws JBranchException {
	
		PMS201InputVO inputVO = (PMS201InputVO) body;
		PMS201OutputVO outputVO = new PMS201OutputVO();
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF queryCondition;
		
//		//2017/05/22  修正會重複筆資料    SQL問題 增加AO_JOB_RANK
//		String UserRole=getEmpRoName(inputVO.getAo_code(),inputVO.geteTime().toString());
		
		
		//取得日期區間，用於HTML TR 顯示
		StringBuffer yyyyMMSb = new StringBuffer();
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		yyyyMMSb.append("  SELECT ROWNUM AS YM_NUM, YYMM.YYYYMM, DD.DATA_DATE ");
		yyyyMMSb.append("  FROM ( ");
		yyyyMMSb.append("    SELECT YYYYMM ");
		yyyyMMSb.append("    FROM ( ");
		yyyyMMSb.append("      SELECT TO_CHAR(TO_DATE(SUBSTR(:weekStartDate, 0, 6), 'yyyyMM') - (level - 1), 'yyyyMM') AS YYYYMM ");
		yyyyMMSb.append("      FROM DUAL ");
		yyyyMMSb.append("      CONNECT BY TRUNC(TO_DATE(TO_CHAR(ADD_MONTHS(TO_DATE(SUBSTR(:weekStartDate, 0, 6), 'yyyyMM'), -2), 'yyyyMM'), 'yyyyMM')) + level - 1 <= TRUNC(TO_DATE(SUBSTR(:weekStartDate, 0, 6), 'yyyyMM')) ");
		yyyyMMSb.append("    ) ");
		yyyyMMSb.append("    GROUP BY YYYYMM ");
		yyyyMMSb.append("    ORDER BY YYYYMM DESC ");
		yyyyMMSb.append("  ) YYMM ");
		yyyyMMSb.append("  LEFT JOIN ( ");
		yyyyMMSb.append("    SELECT MAX(WEEK_START_DATE) AS DATA_DATE, MON_YEAR ");
		yyyyMMSb.append("    FROM TBPMS_DATE_REC ");
		yyyyMMSb.append("    WHERE DATA_DATE <= :weekStartDate ");
		yyyyMMSb.append("    GROUP BY MON_YEAR ");
		yyyyMMSb.append("    ORDER BY DATA_DATE DESC ");
		yyyyMMSb.append("  ) DD ON YYMM.YYYYMM = DD.MON_YEAR ");
		
		queryCondition.setObject("weekStartDate", inputVO.geteTime());

		queryCondition.setQueryString(yyyyMMSb.toString());
		
		outputVO.setcGroupTitle1List(dam.exeQuery(queryCondition));
		
		//取得客戶經營指標1
		StringBuffer sb = new StringBuffer();
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb.append("WITH DATE_B AS ( ");
		sb.append(yyyyMMSb);
		sb.append("), CNTCT_CNT AS ( ");
		sb.append("  SELECT DATE_B.YM_NUM, 'CNTCT' AS COLUMN_NAME, '(' || NVL(PMA.CNTCT_CNT, 0) || '/' || NVL(PMA.SRPPS_CNT, 0) || ')' AS CNTCT ");
		sb.append("  FROM DATE_B ");
		sb.append("  LEFT JOIN TBPMS_PRDCTVT_MAST AO ON AO.AO_CODE = :aoCode AND AO.WEEK_START_DATE = DATE_B.DATA_DATE ");
		sb.append("  LEFT JOIN ( ");
		sb.append("    SELECT WEEK_START_DATE, AO_CODE, EMP_ID, AO_JOB_RANK, CNTCT_CNT, SRPPS_CNT ");
		sb.append("    FROM TBPMS_PRDCTVT_MNGM_A ");
		sb.append("  ) PMA ON DATE_B.DATA_DATE = PMA.WEEK_START_DATE AND PMA.AO_CODE = AO.AO_CODE ");
		sb.append("), AVG_CNTCT_CNT AS ( ");
		sb.append("  SELECT DATE_B.YM_NUM, PMA.AO_JOB_RANK,  'CNTCT' AS COLUMN_NAME, '(' || NVL(PMA.CNTCT_CNT, 0) || '/' || NVL(PMA.SRPPS_CNT, 0) || ')' AS CNTCT ");
		sb.append("  FROM DATE_B ");
		sb.append("  LEFT JOIN TBPMS_PRDCTVT_MNGM_A_AVG PMA ON DATE_B.DATA_DATE = PMA.WEEK_START_DATE ");
		sb.append("  WHERE DATE_B.YM_NUM = 1 ");
		sb.append(") ");

		sb.append("SELECT BASE.COLUMN_NAME, ");
//		sb.append("       BASE.MONTH_1 || '%' || CASE WHEN BASE.COLUMN_NAME = 'CNTCT' THEN '\n' || (SELECT C.CNTCT FROM CNTCT_CNT C WHERE C.COLUMN_NAME = BASE.COLUMN_NAME AND YM_NUM = 1) ELSE '' END AS MONTH_1, ");
//		sb.append("       BASE.MONTH_2 || '%' || CASE WHEN BASE.COLUMN_NAME = 'CNTCT' THEN '\n' || (SELECT C.CNTCT FROM CNTCT_CNT C WHERE C.COLUMN_NAME = BASE.COLUMN_NAME AND YM_NUM = 2) ELSE '' END AS MONTH_2, ");
//		sb.append("       BASE.MONTH_3 || '%' || CASE WHEN BASE.COLUMN_NAME = 'CNTCT' THEN '\n' || (SELECT C.CNTCT FROM CNTCT_CNT C WHERE C.COLUMN_NAME = BASE.COLUMN_NAME AND YM_NUM = 3 ) ELSE '' END AS MONTH_3, ");
//		sb.append("       NVL(PMAA.RATE, 0) || '%' || CASE WHEN BASE.COLUMN_NAME = 'CNTCT' THEN '\n' || (SELECT C.CNTCT FROM AVG_CNTCT_CNT C WHERE C.COLUMN_NAME = BASE.COLUMN_NAME AND YM_NUM = 1) ELSE '' END AS AO_AVG ");
		//2017/05/22  修正會重複筆資料    SQL問題
//		sb.append("       NVL(PMAA.RATE, 0) || '%' || CASE WHEN BASE.COLUMN_NAME = 'CNTCT' THEN '\n' || (SELECT C.CNTCT FROM AVG_CNTCT_CNT C WHERE C.COLUMN_NAME = BASE.COLUMN_NAME AND YM_NUM = 1 AND c.AO_JOB_RANK=  ) ELSE '' END AS AO_AVG ");
		 
		sb.append("       REPLACE(LTRIM(TO_CHAR(NVL(BASE.MONTH_1, 0), '990.99')), '.00', '') || '%' || CASE WHEN BASE.COLUMN_NAME = 'CNTCT' THEN '\n' || (SELECT C.CNTCT FROM CNTCT_CNT C WHERE C.COLUMN_NAME = BASE.COLUMN_NAME AND YM_NUM = 1) ELSE '' END AS MONTH_1, ");
		sb.append("       REPLACE(LTRIM(TO_CHAR(NVL(BASE.MONTH_2, 0), '990.99')), '.00', '') || '%' || CASE WHEN BASE.COLUMN_NAME = 'CNTCT' THEN '\n' || (SELECT C.CNTCT FROM CNTCT_CNT C WHERE C.COLUMN_NAME = BASE.COLUMN_NAME AND YM_NUM = 2) ELSE '' END AS MONTH_2, ");
		sb.append("       REPLACE(LTRIM(TO_CHAR(NVL(BASE.MONTH_3, 0), '990.99')), '.00', '') || '%' || CASE WHEN BASE.COLUMN_NAME = 'CNTCT' THEN '\n' || (SELECT C.CNTCT FROM CNTCT_CNT C WHERE C.COLUMN_NAME = BASE.COLUMN_NAME AND YM_NUM = 3 ) ELSE '' END AS MONTH_3, ");

		sb.append("	      NVL(PMAA.RATE, 0) || '%' || CASE WHEN BASE.COLUMN_NAME = 'CNTCT' THEN '\n' || (SELECT C.CNTCT FROM AVG_CNTCT_CNT C WHERE C.COLUMN_NAME = BASE.COLUMN_NAME AND YM_NUM = 1 AND c.AO_JOB_RANK = PMAA.AO_JOB_RANK) ELSE '' END AS AO_AVG ");
	    			 
		sb.append("FROM ( ");
		sb.append("  SELECT COLUMN_NAME, MONTH_1, MONTH_2, MONTH_3 ");
		sb.append("  FROM ( ");
		sb.append("    SELECT YM_NUM, REPLACE(COLUMN_NAME, '_RATE') AS COLUMN_NAME, RATE AS RATE ");
		sb.append("    FROM ( ");
		sb.append("      SELECT DATE_B.YM_NUM, DATE_B.DATA_DATE, NVL(PMA.INTRVW_RATE, 0) AS INTRVW_RATE, NVL(PMA.CNTCT_RATE, 0) AS CNTCT_RATE, NVL(PMA.PRD_RATE, 0) AS PRD_RATE, NVL(PMA.TXN_RATE, 0) AS TXN_RATE, NVL(PMA.ROA_INT_RATE, 0) AS ROA_INT_RATE, NVL(PMA.ROA_NON_RATE, 0) AS ROA_NON_RATE ");
		sb.append("      FROM DATE_B ");
		sb.append("      LEFT JOIN TBPMS_PRDCTVT_MAST AO ON AO.AO_CODE = :aoCode AND AO.WEEK_START_DATE = DATE_B.DATA_DATE ");
		sb.append("      LEFT JOIN TBPMS_PRDCTVT_MNGM_A PMA ON DATE_B.DATA_DATE = PMA.WEEK_START_DATE AND PMA.AO_CODE = AO.AO_CODE ");
		sb.append("    ) UNPIVOT (RATE FOR COLUMN_NAME IN (INTRVW_RATE, CNTCT_RATE, PRD_RATE, TXN_RATE, ROA_INT_RATE, ROA_NON_RATE)) ");
		sb.append("  ) PIVOT (SUM(RATE) FOR (YM_NUM) IN ('1' AS MONTH_1, '2' AS MONTH_2, '3' AS MONTH_3)) ");
		sb.append(") BASE ");
		sb.append("LEFT JOIN ( ");
		sb.append("  SELECT YM_NUM, AO_JOB_RANK, REPLACE(COLUMN_NAME, '_RATE') AS COLUMN_NAME, NVL(RATE, 0) AS RATE ");
		sb.append("  FROM ( ");
		sb.append("    SELECT DATE_B.YM_NUM, PMA.AO_JOB_RANK, PMA.INTRVW_RATE, PMA.CNTCT_RATE, PMA.PRD_RATE, PMA.TXN_RATE, PMA.ROA_INT_RATE, PMA.ROA_NON_RATE ");
		sb.append("    FROM DATE_B ");
		sb.append("    LEFT JOIN TBPMS_PRDCTVT_MNGM_A_AVG PMA ON DATE_B.DATA_DATE = PMA.WEEK_START_DATE ");
		sb.append("    WHERE DATE_B.YM_NUM = 1 ");
		sb.append("    AND PMA.AO_JOB_RANK = (SELECT AO_JOB_RANK FROM TBPMS_PRDCTVT_MAST WHERE AO_CODE = :aoCode AND WEEK_START_DATE = :weekStartDate) ");
		sb.append("  ) A ");
		sb.append("  UNPIVOT (RATE FOR COLUMN_NAME IN (INTRVW_RATE, CNTCT_RATE, PRD_RATE, TXN_RATE, ROA_INT_RATE, ROA_NON_RATE)) ");
		sb.append(") PMAA ON BASE.COLUMN_NAME = PMAA.COLUMN_NAME ");
		sb.append("ORDER BY DECODE(COLUMN_NAME, 'INTRVW', 1, 'CNTCT' , 2, 'PRD', 3, 'TXN', 4, 'ROA_INT', 5, 'ROA_NON', 6, 99) ");

		queryCondition.setObject("weekStartDate", inputVO.geteTime());
		queryCondition.setObject("aoCode", inputVO.getAo_code());

		queryCondition.setQueryString(sb.toString());
		
		outputVO.setPrdctvt1List(dam.exeQuery(queryCondition));
		
		this.sendRtnObject(outputVO);
	}
	
	/**
	 * C.客戶經營指標-客戶經營指標2
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void getPrdctvt2 (Object body, IPrimitiveMap header) throws JBranchException {
	
		PMS201InputVO inputVO = (PMS201InputVO) body;
		PMS201OutputVO outputVO = new PMS201OutputVO();
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF queryCondition;
		
		StringBuffer sb = new StringBuffer();
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb.append("SELECT PARAM_CODE, PARAM_NAME ");
		sb.append("FROM TBSYSPARAMETER ");
		sb.append("WHERE PARAM_TYPE = 'PMS.PRDCIVT2_PROD_T1_TITLE_NAME' ");
		sb.append("ORDER BY PARAM_ORDER ");
		queryCondition.setQueryString(sb.toString());
		
		outputVO.setcGroupTitle2List(dam.exeQuery(queryCondition));
		
		sb = new StringBuffer();
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb.append("SELECT PARAM_CODE, PARAM_NAME ");
		sb.append("FROM TBSYSPARAMETER ");
		sb.append("WHERE PARAM_TYPE = 'PMS.PRDCIVT2_PROD_T2_TITLE_NAME' ");
		sb.append("ORDER BY PARAM_ORDER ");
		queryCondition.setQueryString(sb.toString());
		
		outputVO.setcGroupTitle3List(dam.exeQuery(queryCondition));
		
		sb = new StringBuffer();
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb.append("WITH BASE AS ( ");
		sb.append("  SELECT WEEK_START_DATE, AO_CODE, COLUMN_TYPE, FUND, ETF, BOND, SI, SN, DCI, INS, SAV, CD, F, OTH, TTL, LUM ");
		sb.append("  FROM ( ");
		sb.append("    SELECT WEEK_START_DATE, AO_CODE, ");
		sb.append("           CASE WHEN INSTR(COLUMN_NAME, '_AUM', -1, 1) > 0 THEN 'AUM' ");
		sb.append("                WHEN INSTR(COLUMN_NAME, '_RATE', -1, 1) > 0 THEN 'RATE' ");
		sb.append("                WHEN INSTR(COLUMN_NAME, '_CUST', -1, 1) > 0 THEN 'CUST' ");
		sb.append("           END AS COLUMN_TYPE, ");
		sb.append("           CASE WHEN INSTR(COLUMN_NAME, '_AUM', -1, 1) > 0 THEN REPLACE(COLUMN_NAME, '_AUM') ");
		sb.append("                WHEN INSTR(COLUMN_NAME, '_RATE', -1, 1) > 0 THEN REPLACE(COLUMN_NAME, '_RATE') ");
		sb.append("                WHEN INSTR(COLUMN_NAME, '_CUST', -1, 1) > 0 THEN REPLACE(COLUMN_NAME, '_CUST') ");
		sb.append("           END AS COLUMN_NAME, TYPE_NUM ");
		sb.append("    FROM TBPMS_PRDCTVT_MNGM_B ");
		sb.append("    UNPIVOT (TYPE_NUM FOR COLUMN_NAME IN (FUND_AUM, ETF_AUM, BOND_AUM, SI_AUM, SN_AUM, DCI_AUM, INS_AUM, SAV_AUM, CD_AUM, F_AUM, OTH_AUM, TTL_AUM, LUM_AUM, FUND_RATE, ETF_RATE, BOND_RATE, SI_RATE, SN_RATE, DCI_RATE, INS_RATE, SAV_RATE, CD_RATE, F_RATE, OTH_RATE, TTL_RATE, LUM_RATE, FUND_CUST, ETF_CUST, BOND_CUST, SI_CUST, SN_CUST, DCI_CUST, INS_CUST, SAV_CUST, CD_CUST, F_CUST, OTH_CUST, TTL_CUST, LUM_CUST)) ");
		sb.append("  ) PIVOT (SUM(TYPE_NUM) FOR (COLUMN_NAME) IN ('FUND' AS FUND, 'ETF' AS ETF, 'BOND' AS BOND, 'SI' AS SI, 'SN' AS SN, 'DCI' AS DCI, 'INS' AS INS, 'SAV' AS SAV, 'CD' AS CD, 'F' AS F, 'OTH' AS OTH, 'TTL' AS TTL, 'LUM' AS LUM)) ");
		sb.append("  WHERE WEEK_START_DATE = :dataDate ");
		sb.append("  AND AO_CODE = :aoCode ");
		sb.append("  ORDER BY DECODE(COLUMN_TYPE, 'AUM', 1, 'RATE', 2, 'CUST', 3, 99) ");
		sb.append(") ");
	
		sb.append("SELECT PARAM_CODE, PARAM_NAME, ");
		sb.append("       FUND || CASE WHEN COLUMN_TYPE = 'RATE' THEN '%\n(' || (SELECT TO_CHAR(FUND) FROM BASE WHERE COLUMN_TYPE = 'CUST') || ')' ELSE '' END AS FUND, ");
		sb.append("       ETF || CASE WHEN COLUMN_TYPE = 'RATE' THEN '%\n(' || (SELECT TO_CHAR(ETF) FROM BASE WHERE COLUMN_TYPE = 'CUST') || ')' ELSE '' END AS ETF, ");
		sb.append("       BOND || CASE WHEN COLUMN_TYPE = 'RATE' THEN '%\n(' || (SELECT TO_CHAR(BOND) FROM BASE WHERE COLUMN_TYPE = 'CUST') || ')' ELSE '' END AS BOND, ");
		sb.append("       SI || CASE WHEN COLUMN_TYPE = 'RATE' THEN '%\n(' || (SELECT TO_CHAR(SI) FROM BASE WHERE COLUMN_TYPE = 'CUST') || ')' ELSE '' END AS SI, ");
		sb.append("       SN || CASE WHEN COLUMN_TYPE = 'RATE' THEN '%\n(' || (SELECT TO_CHAR(SN) FROM BASE WHERE COLUMN_TYPE = 'CUST') || ')' ELSE '' END AS SN, ");
		sb.append("       DCI || CASE WHEN COLUMN_TYPE = 'RATE' THEN '%\n(' || (SELECT TO_CHAR(DCI) FROM BASE WHERE COLUMN_TYPE = 'CUST') || ')' ELSE '' END AS DCI, ");
		sb.append("       INS || CASE WHEN COLUMN_TYPE = 'RATE' THEN '%\n(' || (SELECT TO_CHAR(INS) FROM BASE WHERE COLUMN_TYPE = 'CUST') || ')' ELSE '' END AS INS, ");
		sb.append("       SAV || CASE WHEN COLUMN_TYPE = 'RATE' THEN '%\n(' || (SELECT TO_CHAR(SAV) FROM BASE WHERE COLUMN_TYPE = 'CUST') || ')' ELSE '' END AS SAV, ");
		sb.append("       CD || CASE WHEN COLUMN_TYPE = 'RATE' THEN '%\n(' || (SELECT TO_CHAR(CD) FROM BASE WHERE COLUMN_TYPE = 'CUST') || ')' ELSE '' END AS CD, ");
		sb.append("       F || CASE WHEN COLUMN_TYPE = 'RATE' THEN '%\n(' || (SELECT TO_CHAR(F) FROM BASE WHERE COLUMN_TYPE = 'CUST') || ')' ELSE '' END AS F, ");
		sb.append("       OTH || CASE WHEN COLUMN_TYPE = 'RATE' THEN '%\n(' || (SELECT TO_CHAR(OTH) FROM BASE WHERE COLUMN_TYPE = 'CUST') || ')' ELSE '' END AS OTH, ");
		sb.append("       TTL || CASE WHEN COLUMN_TYPE = 'RATE' THEN '%\n(' || (SELECT TO_CHAR(TTL) FROM BASE WHERE COLUMN_TYPE = 'CUST') || ')' ELSE '' END AS TTL, ");
		sb.append("       LUM || CASE WHEN COLUMN_TYPE = 'RATE' THEN '%\n(' || (SELECT TO_CHAR(LUM) FROM BASE WHERE COLUMN_TYPE = 'CUST') || ')' ELSE '' END AS LUM ");
		sb.append("FROM TBSYSPARAMETER SYS_P ");
		sb.append("LEFT JOIN BASE ON BASE.COLUMN_TYPE = REPLACE(PARAM_CODE, '_CUST') ");
		sb.append("WHERE PARAM_TYPE = 'PMS.PRDCIVT2_TITLE_NAME' ");
		
		queryCondition.setObject("dataDate", inputVO.geteTime());
		queryCondition.setObject("aoCode", inputVO.getAo_code());

		queryCondition.setQueryString(sb.toString());
		
		outputVO.setPrdctvt2List(dam.exeQuery(queryCondition));
		
		this.sendRtnObject(outputVO);
	} 
	
	/**
	 * C.客戶經營指標-客戶經營指標2
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void getPrdctvt3 (Object body, IPrimitiveMap header) throws JBranchException {
	
		PMS201InputVO inputVO = (PMS201InputVO) body;
		PMS201OutputVO outputVO = new PMS201OutputVO();
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF queryCondition;
		
		StringBuffer sb = new StringBuffer();
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb.append("SELECT PARAM_CODE AS CON_TYPE, SUBSTR(PARAM_NAME, 0, 2) AS CON_NAME ");
		sb.append("FROM TBSYSPARAMETER ");
		sb.append(" WHERE PARAM_TYPE = 'CRM.CON_DEGREE' ");
	    sb.append("ORDER BY PARAM_ORDER ");
	    queryCondition.setQueryString(sb.toString());
		
		outputVO.setcGroupTitle4List(dam.exeQuery(queryCondition));
		
		sb = new StringBuffer();
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb.append("SELECT BASE.COLUMN_NAME, BASE.E, BASE.I, BASE.P, BASE.O, BASE.S, BASE.OTH, (BASE.E + BASE.I + BASE.P + BASE.O + BASE.S + BASE.OTH) AS TOT ");
		sb.append("FROM ( ");
		sb.append("  SELECT COLUMN_NAME, E, I, P, O, S, OTH ");
		sb.append("  FROM ( ");
		sb.append("    SELECT SYS_P.CON_TYPE, C.COLUMN_NAME, C.TYPE_NUM ");
		sb.append("    FROM ( ");
		sb.append("      SELECT :aoCode AS AO_CODE, PARAM_CODE AS CON_TYPE, SUBSTR(PARAM_NAME, 0, 2) AS CON_NAME, PARAM_ORDER AS CON_ORDER ");
		sb.append("      FROM TBSYSPARAMETER ");
		sb.append("      WHERE PARAM_TYPE = 'CRM.CON_DEGREE' ");
		sb.append("    ) SYS_P ");
		sb.append("    LEFT JOIN ( ");
		sb.append("      SELECT CASE WHEN UPC.WEEK_START_DATE IS NULL THEN :dataDate ELSE UPC.WEEK_START_DATE END AS WEEK_START_DATE, ");
		sb.append("             CASE WHEN UPC.AO_CODE IS NULL THEN :aoCode ELSE UPC.AO_CODE END AS AO_CODE, ");
		sb.append("             SYS_P.CON_TYPE AS CON_DEGREE, LT.PARAM_CODE AS COLUMN_NAME, NVL(UPC.TYPE_NUM, 0) AS TYPE_NUM ");
		sb.append("      FROM (SELECT PARAM_CODE FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'PMS.PRDCIVT3_TITLE_NAME' ORDER BY PARAM_ORDER) LT ");
		sb.append("      LEFT JOIN ( ");
		sb.append("        SELECT :aoCode AS AO_CODE, PARAM_CODE AS CON_TYPE, SUBSTR(PARAM_NAME, 0, 2) AS CON_NAME, PARAM_ORDER AS CON_ORDER ");
		sb.append("        FROM TBSYSPARAMETER ");
		sb.append("        WHERE PARAM_TYPE = 'CRM.CON_DEGREE' ");
		sb.append("      ) SYS_P ON 1 = 1 ");
		sb.append("      LEFT JOIN ( ");
		sb.append("        SELECT WEEK_START_DATE, AO_CODE, CON_DEGREE, COLUMN_NAME, TYPE_NUM ");
		sb.append("        FROM TBPMS_PRDCTVT_MNGM_C ");
		sb.append("        UNPIVOT (TYPE_NUM FOR (COLUMN_NAME) IN (CUST_CNT, NON_CNTCT_CNT, UNSERVE_CNT, CIF_DIFF, AUM_DIFF)) ");
		sb.append("      ) UPC ON LT.PARAM_CODE = UPC.COLUMN_NAME AND SYS_P.CON_TYPE = UPC.CON_DEGREE AND UPC.WEEK_START_DATE = :dataDate AND UPC.AO_CODE = :aoCode ");
		sb.append("    ) C ON TRIM(C.CON_DEGREE) = TRIM(SYS_P.CON_TYPE) ");
		sb.append("  ) PIVOT (SUM(TYPE_NUM) FOR (CON_TYPE) IN ('E' AS E, 'I' AS I, 'P' AS P, 'O' AS O, 'S' AS S, 'OTH' AS OTH)) ");
		sb.append(") BASE ");
		sb.append("LEFT JOIN TBSYSPARAMETER P ON BASE.COLUMN_NAME = P.PARAM_CODE AND P.PARAM_TYPE = 'PMS.PRDCIVT3_TITLE_NAME' ");
		sb.append("ORDER BY P.PARAM_ORDER ");
		
		queryCondition.setObject("dataDate", inputVO.geteTime());
		queryCondition.setObject("aoCode", inputVO.getAo_code());

		queryCondition.setQueryString(sb.toString());
		
		outputVO.setPrdctvt3List(dam.exeQuery(queryCondition));
		
		this.sendRtnObject(outputVO);
	}
	
	/**
	 * D.活動量指標-成交客戶
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void getTransCust (Object body, IPrimitiveMap header) throws JBranchException {
		
		PMS201InputVO inputVO = (PMS201InputVO) body;
		PMS201OutputVO outputVO = new PMS201OutputVO();
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sb = new StringBuffer();
		sb.append("WITH FIRST_MON AS ( ");
		sb.append("  SELECT T.TOP, CASE WHEN TOP.YYYYMM IS NULL THEN SUBSTR(:dataDate, 0, 6) ELSE TOP.YYYYMM END AS YYYYMM, CUST_ID, CUST_NAME, TBAL_1, TBAL_2 ");
		sb.append("  FROM (SELECT ROWNUM AS TOP FROM DUAL CONNECT BY ROWNUM <= 10) T ");
		sb.append("  LEFT JOIN ( ");
		sb.append("    SELECT ROWNUM AS TOP, YYYYMM, CUST_ID, CUST_NAME, TBAL_1, TBAL_2 ");
		sb.append("    FROM ( ");
		sb.append("      SELECT SUBSTR(PRO.DATA_DATE, 0, 6) AS YYYYMM, PRO.CUST_ID, MA.CUST_NAME, SUM(PRO.BAL) AS TBAL_1, SUM(PRO.FEE) AS TBAL_2 ");
		sb.append("      FROM TBPMS_CUST_DAY_PROFIT PRO ");
		sb.append("      LEFT JOIN TBCRM_CUST_MAST MA ON MA.CUST_ID = PRO.CUST_ID ");
		sb.append("      WHERE PRO.AO_CODE = :aoCode ");
		sb.append("      AND PRO.ITEM = '92' ");
		sb.append("      AND SUBSTR(PRO.DATA_DATE, 0, 6) = SUBSTR(:dataDate, 0, 6) ");
		sb.append("      GROUP BY SUBSTR(PRO.DATA_DATE, 0, 6), PRO.CUST_ID, MA.CUST_NAME ");
		sb.append("      ORDER BY TBAL_1 DESC, TBAL_2 DESC, PRO.CUST_ID DESC ");
		sb.append("    ) ");
		sb.append("    WHERE ROWNUM <= 10 ");
		sb.append("  ) TOP ON T.TOP = TOP.TOP ");
		sb.append("), BACK_MON AS ( ");
		sb.append("  SELECT T.TOP AS TOP, CASE WHEN TOP.YYYYMM IS NULL THEN SUBSTR(TO_CHAR(ADD_MONTHS(TO_DATE(:dataDate, 'yyyyMMdd'), -1), 'yyyyMMdd'), 0, 6) ELSE TOP.YYYYMM END AS YYYYMM, CUST_ID, CUST_NAME, TBAL_1, TBAL_2 ");
		sb.append("  FROM (SELECT ROWNUM AS TOP FROM DUAL CONNECT BY ROWNUM <= 10) T ");
		sb.append("  LEFT JOIN ( ");
		sb.append("    SELECT ROWNUM AS TOP, YYYYMM, CUST_ID, CUST_NAME, TBAL_1, TBAL_2 ");
		sb.append("    FROM ( ");
		sb.append("      SELECT SUBSTR(PRO.DATA_DATE, 0, 6) AS YYYYMM, PRO.CUST_ID, MA.CUST_NAME, SUM(PRO.BAL) AS TBAL_1, SUM(PRO.FEE) AS TBAL_2 ");
		sb.append("      FROM TBPMS_CUST_DAY_PROFIT PRO ");
		sb.append("      LEFT JOIN TBCRM_CUST_MAST MA ON MA.CUST_ID = PRO.CUST_ID ");
		sb.append("      WHERE PRO.AO_CODE = :aoCode ");
		sb.append("      AND PRO.ITEM = '92' ");
		sb.append("      AND SUBSTR(PRO.DATA_DATE, 0, 6) = SUBSTR(TO_CHAR(ADD_MONTHS(TO_DATE(:dataDate, 'yyyyMMdd'), -1), 'yyyyMMdd'), 0, 6) ");
		sb.append("      GROUP BY SUBSTR(PRO.DATA_DATE, 0, 6), PRO.CUST_ID, MA.CUST_NAME ");
		sb.append("      ORDER BY TBAL_1 DESC, TBAL_2 DESC, PRO.CUST_ID DESC ");
		sb.append("    ) ");
		sb.append("    WHERE ROWNUM <= 10 ");
		sb.append("  ) TOP ON T.TOP = TOP.TOP ");
		sb.append(") ");

		sb.append("SELECT 'TOP' || F.TOP AS TOP, F.YYYYMM AS YYYYMM_F, F.CUST_ID AS CUST_ID_F, F.CUST_NAME AS CUST_NAME_F,  (SELECT TOP FROM BACK_MON WHERE CUST_ID = F.CUST_ID) AS BACK_TOP, ");
		sb.append("       B.YYYYMM AS YYYYMM_B, B.CUST_ID AS CUST_ID_B, B.CUST_NAME AS CUST_NAME_B, (SELECT TOP FROM FIRST_MON WHERE CUST_ID = B.CUST_ID) AS FIRST_TOP ");
		sb.append("FROM FIRST_MON F ");
		sb.append("LEFT JOIN BACK_MON B ON F.TOP = B.TOP ");
		sb.append("ORDER BY F.TOP ");
		
		queryCondition.setObject("dataDate", inputVO.geteTime());
		queryCondition.setObject("aoCode", inputVO.getAo_code());
		
		queryCondition.setQueryString(sb.toString());
		
		outputVO.setTransCustList(dam.exeQuery(queryCondition));
		
		this.sendRtnObject(outputVO);
	}
	
	/**
	 * D.活動量指標-活動量指標
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void getActIndex (Object body, IPrimitiveMap header) throws JBranchException {
		
		PMS201InputVO inputVO = (PMS201InputVO) body;
		PMS201OutputVO outputVO = new PMS201OutputVO();
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF queryCondition;
		
		//取得日期區間，用於HTML TR 顯示
		StringBuffer yyyyMMSb = new StringBuffer();
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		yyyyMMSb.append("  SELECT ROWNUM AS WEEK_NUM, WEEK_START_DATE, WEEK_END_DATE ");
		yyyyMMSb.append("  FROM ( ");
		yyyyMMSb.append("    SELECT WEEK_START_DATE, WEEK_END_DATE ");
		yyyyMMSb.append("    FROM ( ");
		yyyyMMSb.append("      SELECT WEEK_START_DATE, WEEK_END_DATE ");
		yyyyMMSb.append("      FROM (SELECT MIN(DATA_DATE) AS DATA_DATE, WEEK_START_DATE, WEEK_END_DATE FROM TBPMS_DATE_REC GROUP BY WEEK_START_DATE, WEEK_END_DATE) ");
		yyyyMMSb.append("      WHERE DATA_DATE <= :dataDate ");
		yyyyMMSb.append("      ORDER BY DATA_DATE DESC ");
		yyyyMMSb.append("    ) ");
		yyyyMMSb.append("    WHERE ROWNUM <= 2 ");
		yyyyMMSb.append("    ORDER BY WEEK_START_DATE DESC ");
		yyyyMMSb.append("  ) ");
		yyyyMMSb.append("  ORDER BY ROWNUM ");
		
		queryCondition.setObject("dataDate", inputVO.geteTime());
		
		queryCondition.setQueryString(yyyyMMSb.toString());
		
		outputVO.setdGroupTitleList(dam.exeQuery(queryCondition));
		
		//取得活動量指標
		StringBuffer sb = new StringBuffer();
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb.append("WITH WEEK_DATE AS ( ");
		sb.append(yyyyMMSb);
		sb.append("), FIRST_WEEK AS ( ");
		sb.append("  SELECT WAA.WEEK_START_DATE, WAA.AO_CODE, WAA.AO_JOB_RANK, WAA.VIP_DEGREE, WAA.CUST_CNT, WAA.A_RATE, WAA.M_RATE, WAA.C_RATE ");
		sb.append("  FROM TBPMS_WEEK_AMC_ACT WAA ");
		sb.append("  WHERE WAA.WEEK_START_DATE = (SELECT WEEK_START_DATE FROM WEEK_DATE WHERE WEEK_NUM = 1) ");
		sb.append("), BACK_WEEK AS ( ");
		sb.append("  SELECT WAA.WEEK_START_DATE, WAA.AO_CODE, WAA.AO_JOB_RANK, WAA.VIP_DEGREE, WAA.CUST_CNT, WAA.A_RATE, WAA.M_RATE, WAA.C_RATE ");
		sb.append("  FROM TBPMS_WEEK_AMC_ACT WAA ");
		sb.append("  WHERE WAA.WEEK_START_DATE = (SELECT WEEK_START_DATE FROM WEEK_DATE WHERE WEEK_NUM = 2) ");
		sb.append("), AVG_WEEK AS ( ");
		sb.append("  SELECT WAA.WEEK_START_DATE, WAA.AO_JOB_RANK, WAA.VIP_DEGREE, WAA.A_RATE, WAA.C_RATE ");
		sb.append("  FROM TBPMS_WEEK_AMC_ACT_AVG WAA ");
		sb.append("  WHERE WEEK_START_DATE = (SELECT WEEK_START_DATE FROM WEEK_DATE WHERE WEEK_NUM = 1) ");
		sb.append("), DATA_WEEK AS ( ");
		sb.append("  SELECT PM.AO_CODE, AO.AO_JOB_RANK, PM.VIP_NAME, PM.VIP_ORDER, NVL(FW.CUST_CNT, 0) AS CUST_CNT, NVL(FW.A_RATE, 0) AS A_RATE_F, NVL(FW.M_RATE, 0) AS M_RATE_F, NVL(FW.C_RATE, 0) AS C_RATE_F, NVL(BW.A_RATE, 0) AS A_RATE_B, NVL(BW.C_RATE, 0) AS C_RATE_B, NVL(AW.A_RATE, 0) AS A_RATE_AVG, NVL(AW.C_RATE, 0) AS C_RATE_AVG ");
		sb.append("  FROM ( ");
		sb.append("    SELECT :aoCode AS AO_CODE, PARAM_CODE AS VIP_TYPE, SUBSTR(PARAM_NAME, 0, 2) AS VIP_NAME, PARAM_ORDER AS VIP_ORDER ");
		sb.append("    FROM TBSYSPARAMETER ");
		sb.append("    WHERE PARAM_TYPE = 'CRM.VIP_DEGREE' ");
		sb.append("    ORDER BY VIP_ORDER ");
		sb.append("  ) PM ");
		sb.append("  LEFT JOIN FIRST_WEEK FW ON FW.AO_CODE = :aoCode AND FW.VIP_DEGREE = PM.VIP_TYPE ");
		sb.append("  LEFT JOIN BACK_WEEK BW ON BW.AO_CODE = :aoCode AND BW.VIP_DEGREE = PM.VIP_TYPE ");
		sb.append("  LEFT JOIN AVG_WEEK AW ON AW.AO_JOB_RANK = FW.AO_JOB_RANK AND AW.VIP_DEGREE = PM.VIP_TYPE ");
		sb.append("  LEFT JOIN TBPMS_PRDCTVT_MAST AO ON AO.AO_CODE = :aoCode AND AO.WEEK_START_DATE = (SELECT WEEK_START_DATE FROM WEEK_DATE WHERE WEEK_NUM = 1) ");
		sb.append(") ");
		
		sb.append("SELECT VIP_NAME, VIP_ORDER, AO_JOB_RANK, CUST_CNT, A_RATE_F, M_RATE_F, C_RATE_F, A_RATE_B, C_RATE_B, A_RATE_AVG, C_RATE_AVG ");
		sb.append("FROM DATA_WEEK ");
		sb.append("UNION ALL ");
		sb.append("SELECT '合計' AS VIP_NAME, 99 AS VIP_ORDER, AO_JOB_RANK, SUM(CUST_CNT), ROUND(AVG(A_RATE_F), 2), ROUND(AVG(M_RATE_F)), ROUND(AVG(C_RATE_F)), ROUND(AVG(A_RATE_B)), ROUND(AVG(C_RATE_B)), ROUND(AVG(A_RATE_AVG)), ROUND(AVG(C_RATE_AVG)) ");
		sb.append("FROM DATA_WEEK ");
		sb.append("GROUP BY AO_CODE, AO_JOB_RANK ");
		sb.append("ORDER BY VIP_ORDER ");
		
		queryCondition.setObject("dataDate", inputVO.geteTime());
		queryCondition.setObject("aoCode", inputVO.getAo_code());
		
		queryCondition.setQueryString(sb.toString());
		
		outputVO.setActIndexList(dam.exeQuery(queryCondition));
		
		this.sendRtnObject(outputVO);
	}	
	
	public void getPrdCtvtMngmDtl (Object body, IPrimitiveMap header) throws JBranchException {
		
		PMS201InputVO inputVO = (PMS201InputVO) body;
		PMS201OutputVO outputVO = new PMS201OutputVO();
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT CUST_ID, CUST_NAME, (SRPPS_CNT - CNTCT_CNT) AS UNSERVE_CNT ");
		sb.append("FROM TBPMS_PRDCTVT_MNGM_C_DTL ");
		sb.append("WHERE WEEK_START_DATE = :weekStartDate ");
		sb.append("AND AO_CODE = :aoCode ");

		if (!StringUtils.equals("TOT", inputVO.getConDegree())) {
			sb.append("AND CON_DEGREE = :conDegree ");
			sb.append("AND TARGET_TYPE = :targetType ");
			
			queryCondition.setObject("conDegree", inputVO.getConDegree());
			queryCondition.setObject("targetType", inputVO.getTargetType());
		}
		
		if (StringUtils.equals("2", inputVO.getTargetType())) {
			sb.append("AND CNTCT_CNT <> SRPPS_CNT ");
		}
		
		queryCondition.setObject("weekStartDate", inputVO.geteTime());
		queryCondition.setObject("aoCode", inputVO.getAo_code());

		queryCondition.setQueryString(sb.toString());
		
		outputVO.setPrdCtvtMngmDtl(dam.exeQuery(queryCondition));
		
		this.sendRtnObject(outputVO);
	}
	
	
//	/**
//	 * 求出ao_job_rank
//	 * 
//	 * @param body
//	 * @param header
//	 * @return 
//	 * @throws JBranchException
//	 */
//	public String getEmpRoName (String ao_code,String geteTime) throws JBranchException {
//		DataAccessManager dam = this.getDataAccessManager();
//		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
//		
//		StringBuffer sb = new StringBuffer();
//		sb.append(" SELECT A.AO_JOB_RANK FROM TBPMS_EMPLOYEE_REC_N A left join TBPMS_SALES_AOCODE_REC B on A.EMP_ID=B.EMP_ID WHERE B.AO_CODE LIKE :AO_CODE ");
////		sb.append("SELECT AO_JOB_RANK FROM TBPMS_SALES_AOCODE_MEM_M WHERE  AO_CODE=:AO_CODE AND YEARMON=:YEARMON");
//		queryCondition.setObject("AO_CODE",ao_code );
////		queryCondition.setObject("YEARMON",geteTime.substring(0,6) );
//		queryCondition.setQueryString(sb.toString());
//		
//		List<Map<String,Object>> list=dam.exeQuery(queryCondition);
//		if(list.size()>0)
//			return list.get(0).get("AO_JOB_RANK").toString() ;
//		else
//			return "FC1";
//		
//	}
}
