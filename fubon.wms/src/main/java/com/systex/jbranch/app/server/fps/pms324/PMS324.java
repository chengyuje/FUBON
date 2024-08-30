package com.systex.jbranch.app.server.fps.pms324;
import java.io.File;
import java.math.BigDecimal;
import java.sql.Types;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import jxl.Sheet;
import jxl.Workbook;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.server.fps.pms324.PMS324InputVO;
import com.systex.jbranch.app.server.fps.pms324.PMS324OutputVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;
/**
 * 
 * Copy Right Information : Forms Syntron <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description :理專晉級報表Controller <br>
 * Comments Name : PMS324.java<br>
 * Author :zhouyiqiong<br>
 * Date :2016年11月12日 <br>
 * Version : 1.01 <br>
 * Editor : zhouyiqiong<br>
 * Editor Date : 2016年11月12日<br>
 */
@Component("pms324")
@Scope("request")
public class PMS324 extends FubonWmsBizLogic
{
	private DataAccessManager dam = null;
	private PMS324InputVO inputVO = null;
	private Logger logger = LoggerFactory.getLogger(PMS324.class);
	
	/**
	 * 區間獲取
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void getDurExam(Object body, IPrimitiveMap header)
			throws JBranchException {
		PMS324OutputVO return_VO = new PMS324OutputVO();
		PMS324InputVO inputVO = (PMS324InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam
				.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		try {
			sql.append("    SELECT                                                                                              ");
			sql.append("         CASE WHEN TO_CHAR(ADD_MONTHS(SYSDATE,-2),'MM') > 6                                             ");
			sql.append("              THEN TO_CHAR(ADD_MONTHS(SYSDATE,-2),'YYYY')||'01'                                         ");
			sql.append("              ELSE TO_CHAR(ADD_MONTHS(SYSDATE,-2-12),'YYYY')||'07'                                      ");
			sql.append("         END                                          AS START_MONTH                                    ");
			sql.append("        ,MAX(T.EXEC_MONTH)                            AS EXEC_MONTH                                     ");
			sql.append("        ,CASE WHEN TO_CHAR(ADD_MONTHS(SYSDATE,-2),'MM') > 6                                             ");
			sql.append("              THEN TO_CHAR(ADD_MONTHS(SYSDATE,-2),'YYYY')||'12'                                         ");
			sql.append("              ELSE TO_CHAR(ADD_MONTHS(SYSDATE,-2),'YYYY')||'06'                                         ");
			sql.append("         END                                          AS END_MONTH                                      ");
			sql.append("    FROM DUAL                                                                                           ");
			sql.append("    LEFT JOIN TBPMS_EMP_PROMOTION T                                                                     ");
			sql.append("         ON T.START_MONTH = CASE WHEN TO_CHAR(ADD_MONTHS(SYSDATE,-2),'MM') > 6                          ");
			sql.append("                                 THEN TO_CHAR(ADD_MONTHS(SYSDATE,-2),'YYYY')||'01'                      ");
			sql.append("                                 ELSE TO_CHAR(ADD_MONTHS(SYSDATE,-2-12),'YYYY')||'07'                   ");
			sql.append("                            END                                                                         ");
			sql.append("         AND T.END_MONTH = CASE WHEN TO_CHAR(ADD_MONTHS(SYSDATE,-2),'MM') > 6                           ");
			sql.append("                                THEN TO_CHAR(ADD_MONTHS(SYSDATE,-2),'YYYY')||'12'                       ");
			sql.append("                                ELSE TO_CHAR(ADD_MONTHS(SYSDATE,-2),'YYYY')||'06'                       ");
			sql.append("                           END                                                                          ");
			sql.append("    UNION ALL                                                                                           ");
			sql.append("    SELECT                                                                                              ");
			sql.append("         TO_CHAR(ADD_MONTHS(TO_DATE(                                                                    ");
			sql.append("         CASE WHEN TO_CHAR(ADD_MONTHS(SYSDATE,-2),'MM') > 6                                             ");
			sql.append("              THEN TO_CHAR(ADD_MONTHS(SYSDATE,-2),'YYYY')||'01'                                         ");
			sql.append("              ELSE TO_CHAR(ADD_MONTHS(SYSDATE,-2-12),'YYYY')||'07'                                      ");
			sql.append("         END,'YYYYMM'),-6),'YYYYMM')                AS START_MONTH                                      ");
			sql.append("        ,MAX(T.EXEC_MONTH)                          AS EXEC_MONTH                                       ");
			sql.append("        ,TO_CHAR(ADD_MONTHS(TO_DATE(                                                                    ");
			sql.append("         CASE WHEN TO_CHAR(ADD_MONTHS(SYSDATE,-2),'MM') > 6                                             ");
			sql.append("              THEN TO_CHAR(ADD_MONTHS(SYSDATE,-2),'YYYY')||'12'                                         ");
			sql.append("              ELSE TO_CHAR(ADD_MONTHS(SYSDATE,-2),'YYYY')||'06'                                         ");
			sql.append("         END,'YYYYMM'),-6),'YYYYMM')                AS END_MONTH                                        ");
			sql.append("    FROM DUAL                                                                                           ");
			sql.append("    LEFT JOIN TBPMS_EMP_PROMOTION T                                                                     ");
			sql.append("         ON T.START_MONTH = TO_CHAR(ADD_MONTHS(TO_DATE(                                                 ");
			sql.append("                                     CASE WHEN TO_CHAR(ADD_MONTHS(SYSDATE,-2),'MM') > 6                 ");
			sql.append("                                          THEN TO_CHAR(ADD_MONTHS(SYSDATE,-2),'YYYY')||'01'             ");
			sql.append("                                          ELSE TO_CHAR(ADD_MONTHS(SYSDATE,-2-12),'YYYY')||'07'          ");
			sql.append("                                     END,'YYYYMM'),-6),'YYYYMM')                                        ");
			sql.append("         AND T.END_MONTH = TO_CHAR(ADD_MONTHS(TO_DATE(                                                  ");
			sql.append("                                     CASE WHEN TO_CHAR(ADD_MONTHS(SYSDATE,-2),'MM') > 6                 ");
			sql.append("                                          THEN TO_CHAR(ADD_MONTHS(SYSDATE,-2),'YYYY')||'12'             ");
			sql.append("                                          ELSE TO_CHAR(ADD_MONTHS(SYSDATE,-2),'YYYY')||'06'             ");
			sql.append("                                     END,'YYYYMM'),-6),'YYYYMM')                                        ");
			queryCondition.setQueryString(sql.toString());
			// result
			List durExamList = dam.executeQuery(queryCondition);
			return_VO.setDurExamList(durExamList);
			this.sendRtnObject(return_VO);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}
	
	/**
	 * 計值區間獲取
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void getexeDurExam(Object body, IPrimitiveMap header)
			throws JBranchException {
		PMS324OutputVO return_VO = new PMS324OutputVO();
		PMS324InputVO inputVO = (PMS324InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam
				.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		try {
			sql.append("    SELECT DISTINCT T.START_MONTH,T.EXEC_MONTH,T.END_MONTH   ");
			sql.append("    FROM TBPMS_EMP_PROMOTION T                               ");
			sql.append("    WHERE 1 = 1                               				 ");
			if (!StringUtils.isBlank(inputVO.getStartTime())) {
				sql.append(" AND START_MONTH = :startMonth                           ");
				queryCondition.setObject("startMonth", inputVO.getStartTime());
			}
			if (!StringUtils.isBlank(inputVO.getEndTime())) {
				sql.append(" AND END_MONTH = :endMonth                               ");
				queryCondition.setObject("endMonth", inputVO.getEndTime());
			}
			queryCondition.setQueryString(sql.toString());
			// result
			List durExeExamList = dam.executeQuery(queryCondition);
			return_VO.setDurExeExamList(durExeExamList);;
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
	public void getRole(Object body, IPrimitiveMap header)
			throws JBranchException {
		PMS324OutputVO return_VO = new PMS324OutputVO();
		PMS324InputVO inputVO = (PMS324InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam
				.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		try {
			sql.append("  SELECT COUNT(1) AS CNT														");
			sql.append("  FROM TBORG_MEMBER_ROLE                                                        ");
			if(StringUtils.isNotBlank((String) getUserVariable(FubonSystemVariableConsts.LOGINID))){
				sql.append(" WHERE EMP_ID = :user 				                                        ");
				queryCondition.setObject("user", (String) getUserVariable(FubonSystemVariableConsts.LOGINID));
			}
			sql.append("        AND ROLE_ID IN(SELECT PARAM_CODE                                        ");
			sql.append("  				     FROM TBSYSPARAMETER                                        ");
			sql.append("  				     WHERE PARAM_TYPE = 'FUBONSYS.HEADMGR_ROLE')                ");
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
	 * 查詢檔案
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void inquire(Object body, IPrimitiveMap header) throws JBranchException
	{	
		PMS324InputVO inputVO = (PMS324InputVO) body;
		PMS324OutputVO outputVO = new PMS324OutputVO();
		List roleList = new ArrayList();
		roleList.add(0, inputVO.getRole());
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		try
		{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			sql.append("  SELECT START_MONTH, 														");
			sql.append("  	     END_MONTH,                                                  		");
			sql.append("  	     EXEC_MONTH,                                                  		");
			sql.append("  	     REGION_CENTER_ID,                                                  ");
			sql.append("  	     REGION_CENTER_NAME,                                                ");
			sql.append("  	     BRANCH_AREA_ID,                                                    ");
			sql.append("  	     BRANCH_AREA_NAME,                                                  ");
			sql.append("  	     BRANCH_NBR,                                                        ");
			sql.append("  	     BRANCH_NAME,                                                       ");
			sql.append("  	     LPAD(EMP_ID,6,'0') AS EMP_ID,                                      ");
			sql.append("  	     EMP_NAME,                                                          ");
			sql.append("  	     AO_CODE,                                                           ");
			sql.append("  	     JOB_TITLE_ID,                                                      ");
			sql.append("  	     JOB_LEVEL_NAME,                                                    ");
			sql.append("  	     JOB_POSITION_NAME,                                                 ");
			sql.append("  	     GOAL_DAY,                                                          ");
			sql.append("  	     WORK_AGREEMENT,                                                    ");
			sql.append("  	     AVG_REAL_INCOME,                                                   ");
			sql.append("  	     JOB_SICK,                                                          ");
			sql.append("  	     FUNERAL_LEAVE,                                                     ");
			sql.append("  	     MARRIAGE_LEAVE,                                                    ");
			sql.append("  	     MATERNITY_LEAVE,                                                   ");
			sql.append("  	     LEAVE_DAYS,                                                        ");
			sql.append("  	     STAT_GOAL,                                                         ");
			sql.append("  	     LAST_TITLE_STR,                                                    ");
			sql.append("  	     LAST_TITLE_STR_RATE,                                               ");
			sql.append("  	     LAST_TITLE_TRA,                                                    ");
			sql.append("  	     LAST_TITLE_TRA_RATE,                                               ");
			sql.append("  	     LAST_TITLE_45,                                                     ");
			sql.append("  	     LAST_TITLE_45_RATE,                                                ");
			sql.append("  	     decode(PROM_PROD_GOAL,'0','否','1','是') AS  PROM_PROD_GOAL,        ");
			sql.append("  	     LOAN_NUM,                                                          ");
			sql.append("  	     LOAN_GOAL,                                                         ");
			sql.append("  	     LOAN_RATE,                                                         ");
			sql.append("  	     CARD_NUM,                                                          ");
			sql.append("  	     CARD_GOAL,                                                         ");
			sql.append("  	     CARD_RATE,                                                         ");
			sql.append("  	     RESULT_FIRST_OVER,                                                 ");
			sql.append("  	     VIOL_ORDER,                                                        ");
			sql.append("  	     decode(IS_PROMO,'0','否','1','是') AS IS_PROMO,                     ");
			sql.append("  	     PROM_AFT_RANK,                                                     ");
			sql.append("  	     PROM_AFT_GRA,                                                      ");
			sql.append("  	     PROM_AFT_NAME,                                                     ");
			sql.append("  	     ADJUST_SUM,                                                        ");
			sql.append("  	     VERSION,                                                           ");
			sql.append("  	     CREATETIME,                                                        ");
			sql.append("  	     CREATOR,                                                           ");
			sql.append("  	     MODIFIER,                                                          ");
			sql.append("  	     LASTUPDATE                                                         ");
			sql.append("  FROM TBPMS_EMP_PROMOTION                                                  ");
			sql.append("  WHERE 1 = 1                                                               ");
			if (!StringUtils.isBlank(inputVO.getRegion_center_id())) {
				sql.append(" AND REGION_CENTER_ID = :regionCenter                                   ");
				condition.setObject("regionCenter", inputVO.getRegion_center_id());
			}
			if (!StringUtils.isBlank(inputVO.getBranch_area_id())) {
				sql.append(" AND BRANCH_AREA_ID = :branchArea                                       ");
				condition.setObject("branchArea", inputVO.getBranch_area_id());
			}
			if (!StringUtils.isBlank(inputVO.getBranch_nbr())) {
				sql.append(" AND BRANCH_NBR = :branchNbr                                            ");
				condition.setObject("branchNbr", inputVO.getBranch_nbr());
			}
			if (!StringUtils.isBlank(inputVO.getAo_code())) {
				sql.append(" AND AO_CODE = :aoCode                                                  ");
				condition.setObject("aoCode", inputVO.getAo_code());
			}
			if (!StringUtils.isBlank(inputVO.getEmp_id())) {
				sql.append(" AND EMP_ID = :empId                                                    ");
				condition.setObject("empId", inputVO.getEmp_id());
			}
			sql.append(" AND START_MONTH = :startMonth  ");
			condition.setObject("startMonth", inputVO.getStartTime());
			sql.append(" AND END_MONTH = :endMonth  ");		
			condition.setObject("endMonth", inputVO.getEndTime());	
			sql.append(" AND EXEC_MONTH = :execMonth  ");
			condition.setObject("execMonth", inputVO.getExecTime());
			sql.append("  order by REGION_CENTER_ID,BRANCH_AREA_NAME,BRANCH_NBR,EMP_ID ");
			condition.setQueryString(sql.toString());
			ResultIF list = dam.executePaging(condition, inputVO
					.getCurrentPageIndex() + 1, inputVO.getPageCount());
			List<Map<String, Object>> list1 = dam.exeQuery(condition);
			int totalPage_i = list.getTotalPage();
			int totalRecord_i = list.getTotalRecord();
			outputVO.setResultList(list);
			outputVO.setCsvList(list1);
			outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());// 當前頁次
			outputVO.setTotalPage(totalPage_i);// 總頁次
			outputVO.setTotalRecord(totalRecord_i);// 總筆數
			outputVO.setRoleList(roleList);
			this.sendRtnObject(outputVO);
		}
		catch (Exception e)
		{
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}
	/**
	 * 匯出EXCLE
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void export(Object body, IPrimitiveMap header) throws JBranchException 
	{
		PMS324OutputVO return_VO2 = (PMS324OutputVO) body;
		List<Map<String, Object>> list = return_VO2.getCsvList();
		List<String> roleList = return_VO2.getRoleList();
		if(list.size() > 0){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH:mm:ss");
			String fileName = "理專晉級報表_" + sdf.format(new Date()) + ".csv"; 
			List listCSV =  new ArrayList();
			String[] csvHeader = null;
			if(roleList.get(0).equals("1"))
			{
				for(Map<String, Object> map : list){
					String[] records = new String[43];
					int i = 0;
					records[i] = checkIsNull(map, "START_MONTH");  
					records[++i] = checkIsNull(map, "END_MONTH");  
					records[++i] = checkIsNull(map, "REGION_CENTER_ID");  
					records[++i] = checkIsNull(map, "REGION_CENTER_NAME");  
					records[++i] = checkIsNull(map, "BRANCH_AREA_ID");  
					records[++i] = checkIsNull(map, "BRANCH_AREA_NAME");  
					records[++i] = checkIsNull(map, "BRANCH_NBR");  
					records[++i] = checkIsNull(map, "BRANCH_NAME"); 
					records[++i] = checkIsNullAndTrans(map, "EMP_ID");   
					records[++i] = checkIsNull(map, "EMP_NAME"); 
					records[++i] = checkIsNullAndTrans(map, "AO_CODE"); 
					records[++i] = checkIsNull(map, "JOB_TITLE_ID");  
					records[++i] = checkIsNull(map, "JOB_LEVEL_NAME");  
					records[++i] = checkIsNull(map, "JOB_POSITION_NAME");  
					records[++i] = checkIsNull(map, "GOAL_DAY");  
					records[++i] = checkIsNull(map, "WORK_AGREEMENT");  
					records[++i] = checkIsNull(map, "AVG_REAL_INCOME"); 
					records[++i] = checkIsNull(map, "JOB_SICK");  
					records[++i] = checkIsNull(map, "FUNERAL_LEAVE");  
					records[++i] = checkIsNull(map, "MARRIAGE_LEAVE");  
					records[++i] = checkIsNull(map, "MATERNITY_LEAVE"); 
					records[++i] = checkIsNull(map, "LEAVE_DAYS");  
					records[++i] = pcntFormat(map, "STAT_GOAL");  
					records[++i] = checkIsNull(map, "LAST_TITLE_STR");  
					records[++i] = pcntFormat(map, "LAST_TITLE_STR_RATE");  
					records[++i] = checkIsNull(map, "LAST_TITLE_TRA");  
					records[++i] = pcntFormat(map, "LAST_TITLE_TRA_RATE");  
					records[++i] = checkIsNull(map, "LAST_TITLE_45"); 
					records[++i] = pcntFormat(map, "LAST_TITLE_45_RATE");  
					records[++i] = checkIsNull(map, "PROM_PROD_GOAL"); 
					records[++i] = checkIsNull(map, "LOAN_NUM"); 
					records[++i] = checkIsNull(map, "LOAN_GOAL"); 
					records[++i] = pcntFormat(map, "LOAN_RATE"); 
					records[++i] = checkIsNull(map, "CARD_NUM"); 
					records[++i] = checkIsNull(map, "CARD_GOAL"); 
					records[++i] = pcntFormat(map, "CARD_RATE"); 
					records[++i] = checkIsNull(map, "RESULT_FIRST_OVER");  
					records[++i] = checkIsNull(map, "VIOL_ORDER");    
					records[++i] = checkIsNull(map, "IS_PROMO");  
					records[++i] = checkIsNull(map, "PROM_AFT_RANK"); 
					records[++i] = checkIsNull(map, "PROM_AFT_GRA");  
					records[++i] = checkIsNull(map, "PROM_AFT_NAME");  
					records[++i] = checkIsNull(map, "ADJUST_SUM");                         //信用卡計績卡數達成率
					listCSV.add(records);
				}
				//header
				csvHeader = new String[43];
				int j = 0;
				csvHeader[j] = "考核開始時間";
				csvHeader[++j] = "考核結束時間";
				csvHeader[++j] = "業務處代碼";
				csvHeader[++j] = "業務處名稱";
				csvHeader[++j] = "營運區代碼";
				csvHeader[++j] = "營運區名稱";
				csvHeader[++j] = "分行代號";
				csvHeader[++j] = "分行名稱";
				csvHeader[++j] = "員工編號";
				csvHeader[++j] = "姓名";
				csvHeader[++j] = "AO CODE";
				csvHeader[++j] = "職級";
				csvHeader[++j] = "職等";
				csvHeader[++j] = "職稱";
				csvHeader[++j] = "任業務職掛GOAL日";
				csvHeader[++j] = "勞動契約";
				csvHeader[++j] = "平均實際收益";
				csvHeader[++j] = "公傷病假";
				csvHeader[++j] = "喪假";
				csvHeader[++j] = "婚假";
				csvHeader[++j] = "產假";
				csvHeader[++j] = "休假日數";
				csvHeader[++j] = "應達生產力目標達成率";
				csvHeader[++j] = "上一職級直線標準";
				csvHeader[++j] = "上一職級直線標準達成率";
				csvHeader[++j] = "上一職級橫向標準";
				csvHeader[++j] = "上一職級橫向標準達成率";
				csvHeader[++j] = "上一職級45度標準";
				csvHeader[++j] = "上一職級45度標準達成率";
				csvHeader[++j] = "手收是否達晉級生產力目標";
				csvHeader[++j] = "房貸件數";
				csvHeader[++j] = "房貸轉介目標";
				csvHeader[++j] = "房貸達成率";
				csvHeader[++j] = "信用卡計績卡數";
				csvHeader[++j] = "信用卡目標";
				csvHeader[++j] = "信用卡達成率";
				csvHeader[++j] = "去年考績甲以上(含)";
				csvHeader[++j] = "違反法令遵循";
				csvHeader[++j] = "是否晉級";
				csvHeader[++j] = "晉升後職級";
				csvHeader[++j] = "晉升後職等";
				csvHeader[++j] = "晉升後職稱";
				csvHeader[++j] = "建議調整金額";
			}else
			{
				for(Map<String, Object> map : list){
					String[] records = new String[21];
					int i = 0;
					records[i] = checkIsNull(map, "START_MONTH");  
					records[++i] = checkIsNull(map, "END_MONTH"); 
					records[++i] = checkIsNull(map, "REGION_CENTER_ID");                 //區域中心
					records[++i] = checkIsNull(map, "REGION_CENTER_NAME");               //區域中心
					records[++i] = checkIsNull(map, "BRANCH_AREA_ID");                   //營運區
					records[++i] = checkIsNull(map, "BRANCH_AREA_NAME");                 //營運區
					records[++i] = checkIsNull(map, "BRANCH_NBR");                       //分行代號
					records[++i] = checkIsNull(map, "BRANCH_NAME");                      //分行名稱
					records[++i] = checkIsNull(map, "AO_CODE");                          //AO CODE
					records[++i] = checkIsNull(map, "EMP_NAME");                         //姓名
					records[++i] = checkIsNullAndTrans(map, "EMP_ID");                   //員工編號
					records[++i] = checkIsNull(map, "JOB_TITLE_ID");                     //職級
					records[++i] = checkIsNull(map, "JOB_LEVEL_NAME");                   //職等
					records[++i] = checkIsNull(map, "JOB_POSITION_NAME");                //職稱
					records[++i] = checkIsNull(map, "AVG_REAL_INCOME");                  //平均實際收益
					records[++i] = checkIsNull(map, "STAT_GOAL");                        //應達生產力目標達成率(註)
					records[++i] = pcntFormat(map, "LAST_TITLE_STR_RATE");               //上一職級直線標準達成率
					records[++i] = pcntFormat(map, "LAST_TITLE_TRA_RATE");               //上一職級橫向標準達成率
					records[++i] = pcntFormat(map, "LAST_TITLE_45_RATE");                //上一職級45度標準達成率
					records[++i] = pcntFormat(map, "LOAN_RATE");                         //房貸件數達成率
					records[++i] = pcntFormat(map, "CARD_RATE");                         //信用卡計績卡數達成率
					listCSV.add(records);
				}
				//header
				csvHeader = new String[21];
				int j = 0;
				csvHeader[j] = "考核開始時間";
				csvHeader[++j] = "考核結束時間";
				csvHeader[++j] = "業務處編碼";
				csvHeader[++j] = "業務處";
				csvHeader[++j] = "營運區編碼";
				csvHeader[++j] = "營運區";
				csvHeader[++j] = "分行代號";
				csvHeader[++j] = "分行名稱";
				csvHeader[++j] = "AO CODE";
				csvHeader[++j] = "姓名";
				csvHeader[++j] = "員工編號";
				csvHeader[++j] = "職級";
				csvHeader[++j] = "職等";
				csvHeader[++j] = "職稱";
				csvHeader[++j] = "平均實際收益";
				csvHeader[++j] = "應達生產力目標達成率(註)";
				csvHeader[++j] = "上一職級直線標準達成率";
				csvHeader[++j] = "上一職級橫向標準達成率";
				csvHeader[++j] = "上一職級45度標準達成率";
				csvHeader[++j] = "房貸件數達成率";
				csvHeader[++j] = "信用卡計績卡數達成率";
			}
			CSVUtil csv = new CSVUtil();
			csv.setHeader(csvHeader);
			csv.addRecordList(listCSV);
			String url = csv.generateCSV();
			notifyClientToDownloadFile(url, fileName); //download
		} else {
			return_VO2.setResultList(list);
			this.sendRtnObject(return_VO2);
	    }
	}
	//從excel表格中新增數據
	@SuppressWarnings({ "rawtypes", "unused" })
	public void addData(Object body, IPrimitiveMap header) throws APException
	{
		int flag = 0;
		try {
			PMS324InputVO inputVO = (PMS324InputVO) body;
			PMS324OutputVO outputVO = new PMS324OutputVO();
			List<String> import_file = new ArrayList<String>();
			List<String> list =  new ArrayList<String>();
			String joinedPath = new File((String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH), inputVO.getFileName()).toString();
			Workbook workbook = Workbook.getWorkbook(new File(joinedPath));
			Sheet sheet[] = workbook.getSheets();
			//有表頭.xls文檔
			//清空臨時表
			dam = this.getDataAccessManager();
			QueryConditionIF dcon = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);	
			String dsql = " TRUNCATE TABLE TBPMS_EMP_PROMO_INFO_U ";
			dcon.setQueryString(dsql.toString());
			dam.exeUpdate(dcon);
			String lab = null;
			//SQL指令
			StringBuffer sb = new StringBuffer();
			dam = this.getDataAccessManager();
			QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb.append("   INSERT INTO TBPMS_EMP_PROMO_INFO_U (START_MONTH,	 ");
			sb.append("  		END_MONTH,            				         ");
			sb.append("  		EMP_ID,           					     	 ");
			sb.append("  		LOAN_REF_CONT,           					 ");
			sb.append("  		EISS_CNT,           					     ");					
			sb.append("  		RESULT_FIRST_OVER,            			     ");
			sb.append("  		WORK_AGREEMENT,            					 ");
			sb.append("  		RNUM,            					         ");
			sb.append("  		VERSION,            						 ");
			sb.append("  		CREATETIME,             					 ");
			sb.append("  		CREATOR,             						 ");
			sb.append("  		MODIFIER,         						     ");
			sb.append("  		LASTUPDATE )             					 ");
			sb.append("  	VALUES(:START_MONTH,            				 ");
			sb.append("  		:END_MONTH,             				     ");
			sb.append("  		LPAD(:EMP_ID,6,'0'),   					     ");
			sb.append("  		:LOAN_REF_CONT,           					 ");
			sb.append("  		:EISS_CNT,           					     ");
			sb.append("  		:RESULT_FIRST_OVER,             		     ");
			sb.append("  		:WORK_AGREEMENT,             			     ");
			sb.append("  		:RNUM,             					         ");
			sb.append("  		:VERSION,           					     ");
			sb.append("  		SYSDATE,           				             ");
			sb.append("  		:CREATOR,            					     ");
			sb.append("  		:MODIFIER,         					         ");
			sb.append("  		SYSDATE)          				             ");
			for(int a=0;a<sheet.length;a++){
				for(int i=1;i<sheet[a].getRows();i++){
					for(int j=0;j<sheet[a].getColumns();j++){
						lab = sheet[a].getCell(j, i).getContents();
						list.add(lab);
					}					
					//excel表格記行數
					flag++;					
					//判斷當前上傳數據欄位個數是否一致
					if(list.size()!=7){
						throw new APException("上傳數據欄位個數不一致");
					}					
					qc.setObject("START_MONTH",list.get(0).trim()                     );
					qc.setObject("END_MONTH",list.get(1).trim()                       );
					qc.setObject("EMP_ID",list.get(2).trim()                       	  );
					qc.setObject("LOAN_REF_CONT",list.get(3).trim()               	  );
					qc.setObject("EISS_CNT",list.get(4).trim()                        );
					qc.setObject("RESULT_FIRST_OVER",list.get(5).trim()               );
					qc.setObject("WORK_AGREEMENT",list.get(6).trim()                  );
					qc.setObject("RNUM",flag                                          );
					qc.setObject("VERSION","0"                                        );
					qc.setObject("CREATOR", (String) getUserVariable(FubonSystemVariableConsts.LOGINID)       );
					qc.setObject("MODIFIER", (String) getUserVariable(FubonSystemVariableConsts.LOGINID)      );
					qc.setQueryString(sb.toString());
					dam.exeUpdate(qc);
					list.clear();
				}
			}		
			//文檔上傳成功
			outputVO.setFlag(flag);
			this.sendRtnObject(outputVO);
		} catch (Exception e) {
			logger.error("文檔上傳失敗");
			throw new APException("資料上傳失敗,錯誤發生在第"+flag+"筆,"+e.getMessage());
		}
	}
	
	/**
	 * 調用存儲過程
	 * @param body
	 * @param header
	 * @throws APException
	 */
	@SuppressWarnings({ "unused", "rawtypes" })
	public void callStored(Object body, IPrimitiveMap header) throws APException
	{
		try
		{
			PMS324InputVO inputVO = (PMS324InputVO) body;
			PMS324OutputVO outputVO = new PMS324OutputVO();
			//執行存儲過程
			dam = this.getDataAccessManager();
			QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sb = new StringBuffer();
			sb.append(" CALL PABTH_BTPMS718.SP_TBPMS_EMP_PROMO_INFO(?,?,?) ");
			qc.setString(1, inputVO.getStartTime());
			qc.setString(2, inputVO.getEndTime());
			qc.registerOutParameter(3, Types.VARCHAR);
			qc.setQueryString(sb.toString());
			Map<Integer, Object> resultMap = dam.executeCallable(qc);
			String str = (String) resultMap.get(3);
			String[] strs = null;
			if(str!=null){
				strs = str.split("；");
				if(strs!=null&&strs.length>5){
					str = strs[0]+"；"+strs[1]+"；"+strs[2]+"；"+strs[3]+"；"+strs[4]+"...等";
				}
			}
			outputVO.setErrorMessage(str);
			this.sendRtnObject(outputVO);
		}
		catch (Exception e)
		{
			logger.error(String.format("發生錯誤:%s",StringUtil.getStackTraceAsString(e)));
			throw new APException(e.getMessage());
		}
	
	}
	
	public void showData(Object body, IPrimitiveMap header) throws JBranchException
	{	
		PMS324InputVO inputVO = (PMS324InputVO) body;
		PMS324OutputVO outputVO = new PMS324OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		try
		{
			sql.append("  SELECT START_MONTH,									    ");
			sql.append("         END_MONTH,											");
			sql.append("  	     EMP_ID,                                            ");
			sql.append("  	     LOAN_REF_CONT,                                     ");
			sql.append("  	     EISS_CNT,                                          ");
			sql.append("  	     RESULT_FIRST_OVER,                                 ");
			sql.append("  	     WORK_AGREEMENT                                     ");
			sql.append("  FROM TBPMS_EMP_PROMO_INFO                                 ");
			sql.append("  WHERE 1 = 1												");
			sql.append(" AND START_MONTH = :startMonth  ");
			condition.setObject("startMonth", inputVO.getStartTime());
			sql.append(" AND END_MONTH = :endMonth  ");		
			condition.setObject("endMonth", inputVO.getEndTime());	
			condition.setQueryString(sql.toString());
			ResultIF list = dam.executePaging(condition, inputVO
					.getCurrentPageIndex() + 1, inputVO.getPageCount());
			List<Map<String, Object>> list1 = dam.exeQuery(condition);
			int totalPage_i = list.getTotalPage();
			int totalRecord_i = list.getTotalRecord();
			outputVO.setDataList(list);
			outputVO.setCsvList(list1);
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
	 * 查詢晉級門檻參數
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes", "unused" })
	public void queryParm(Object body, IPrimitiveMap header) throws JBranchException
	{
		PMS324InputVO inputVO = (PMS324InputVO) body;
		PMS324OutputVO outputVO = new PMS324OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		StringBuffer sb = new StringBuffer();
		sb.append("  SELECT LOAN_REF_CONT                ");
		sb.append("         ,EISS_CNT                    ");
		sb.append("  FROM TBPMS_EMP_PROMO_LINE           ");
		qc.setQueryString(sb.toString()                   );
		List<Map<String, Object>> result = dam.exeQuery(qc);
		outputVO.setParmList(result);
		this.sendRtnObject(outputVO);
	}
	
	/**晉級門檻參數設置**/
	@SuppressWarnings("rawtypes")
	public void addParameter(Object body, IPrimitiveMap header) throws APException
	{
		try
		{
			PMS324InputVO inputVO = (PMS324InputVO) body;
			PMS324OutputVO outputVO = new PMS324OutputVO();
			//刪除已存在數據
			dam = this.getDataAccessManager();
			QueryConditionIF dqc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);	
			StringBuffer dsb = new StringBuffer();
			dsb.append("  DELETE TBPMS_EMP_PROMO_LINE");
			dqc.setQueryString(dsb.toString());
			dam.exeUpdate(dqc);
			StringBuffer sb = new StringBuffer();
			dam = this.getDataAccessManager();
			QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb.append("   INSERT INTO TBPMS_EMP_PROMO_LINE (LOAN_REF_CONT,	 ");
			sb.append("  		EISS_CNT,            				         ");
			sb.append("  		VERSION,            						 ");
			sb.append("  		CREATETIME,             					 ");
			sb.append("  		CREATOR,             						 ");
			sb.append("  		MODIFIER,         						     ");
			sb.append("  		LASTUPDATE )             					 ");
			sb.append("  	VALUES(:LOAN_REF_CONT,            				 ");
			sb.append("  		:EISS_CNT,             				         ");
			sb.append("  		:VERSION,           					     ");
			sb.append("  		SYSDATE,           				             ");
			sb.append("  		:CREATOR,            					     ");
			sb.append("  		:MODIFIER,         					         ");
			sb.append("  		SYSDATE)          				             ");
			qc.setObject("LOAN_REF_CONT",inputVO.getLoan_cont()               );
			qc.setObject("EISS_CNT",inputVO.getEiss_cnt()                     );
			qc.setObject("VERSION","0"                                        );
			qc.setObject("CREATOR", (String) getUserVariable(FubonSystemVariableConsts.LOGINID)       );
			qc.setObject("MODIFIER", (String) getUserVariable(FubonSystemVariableConsts.LOGINID)      );
			qc.setQueryString(sb.toString());
			qc.setQueryString(sb.toString());
			dam.exeUpdate(qc);
			this.sendRtnObject(outputVO);
		} catch (Exception e) {
			logger.error("參數調整失敗");
			throw new APException(e.getMessage());
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
	 * 達成率格式
	 * @param map
	 * @param key
	 * @return
	 */
	private String pcntFormat(Map map, String key){		
		if(StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key) != null){
			BigDecimal bd = new BigDecimal(Float.parseFloat(String.valueOf(map.get(key))) * 100); 
			return bd.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue() + "%";										
		}else
			return "";		
	}
	
	/**
	* 檢查Map取出欄位是否為Null
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
	
	
	/** 下載範例檔 **/
	public void downloadSample(Object body, IPrimitiveMap header) throws Exception {
		PMS324InputVO inputVO = (PMS324InputVO) body;
		notifyClientToDownloadFile("doc"+File.separator+"PMS"+File.separator+"PMS324_EXAMPLE.xls", "晉級參數上傳範例.xls");
		
	    this.sendRtnObject(null);
	}
}
