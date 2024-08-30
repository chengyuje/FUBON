package com.systex.jbranch.app.server.fps.pms325;
import java.math.BigDecimal;
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

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;
/**
 * 
 * Copy Right Information : Forms Syntron <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description : 理專降級報表Controller<br>
 * Comments Name : PMS325.java<br>
 * Author :zhouyiqiong<br>
 * Date :2016年11月12日 <br>
 * Version : 1.01 <br>
 * Editor : zhouyiqiong<br>
 * Editor Date : 2016年11月12日<br>
 */
@Component("pms325")
@Scope("request")
public class PMS325 extends FubonWmsBizLogic
{
	private DataAccessManager dam = null;
	private PMS325InputVO inputVO = null;
	private Logger logger = LoggerFactory.getLogger(PMS325.class);
	/**
	 * 區間獲取
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void getDurExam(Object body, IPrimitiveMap header)
			throws JBranchException {
		PMS325OutputVO return_VO = new PMS325OutputVO();
		PMS325InputVO inputVO = (PMS325InputVO) body;
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
			sql.append("    LEFT JOIN TBPMS_EMP_DEMOTION T                                                                      ");
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
			sql.append("    LEFT JOIN TBPMS_EMP_DEMOTION T                                                                      ");
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
		PMS325OutputVO return_VO = new PMS325OutputVO();
		PMS325InputVO inputVO = (PMS325InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam
				.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		try {
			sql.append("    SELECT DISTINCT T.START_MONTH,T.EXEC_MONTH,T.END_MONTH   ");
			sql.append("    FROM TBPMS_EMP_DEMOTION T                                ");
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
		PMS325OutputVO return_VO = new PMS325OutputVO();
		PMS325InputVO inputVO = (PMS325InputVO) body;
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
		}catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
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
		PMS325InputVO inputVO = (PMS325InputVO) body;
		PMS325OutputVO outputVO = new PMS325OutputVO();
		List roleList = new ArrayList();
		roleList.add(0, inputVO.getRole());
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		try
		{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			sql.append("  SELECT START_MONTH, 								   ");
			sql.append("  	     END_MONTH,                                    ");
			sql.append("  	     EXEC_MONTH,                                   ");
			sql.append("  	     REGION_CENTER_ID,                             ");
			sql.append("  	     REGION_CENTER_NAME,                           ");
			sql.append("  	     BRANCH_AREA_ID,                               ");
			sql.append("  	     BRANCH_AREA_NAME,                             ");
			sql.append("  	     BRANCH_NBR,                                   ");
			sql.append("  	     BRANCH_NAME,                                  ");
			sql.append("  	     EMP_ID,                                       ");
			sql.append("  	     EMP_NAME,                                     ");
			sql.append("  	     AO_CODE,                                      ");
			sql.append("  	     JOB_TITLE_ID,                                 ");
			sql.append("  	     JOB_LEVEL_NAME,                               ");
			sql.append("  	     JOB_POSITION_NAME,                            ");
			sql.append("  	     GOAL_DAY,                                     ");
			sql.append("  	     GOAL_SENIORITY,                               ");
			sql.append("  	     WORK_AGREEMENT,                               ");
			sql.append("  	     STAT_AVG1,                                    ");
			sql.append("  	     STAT_PROD_GOAL1,                              ");
			sql.append("  	     STAT_GOAL1,                                   ");
			sql.append("  	     STAT_AVG2,                                    ");
			sql.append("  	     STAT_PROD_GOAL2,                              ");
			sql.append("  	     STAT_GOAL2,                                   ");
			sql.append("  	     AVG_REAL_INCOME,                              ");
			sql.append("  	     decode(WHET_DEG,'0','否','1','是') AS WHET_DEG,");
			sql.append("  	     DEG_AFT_RANK,                                 ");
			sql.append("  	     DEG_AFT_GRA,                                  ");
			sql.append("  	     DEG_AFT_NAME,                                 ");
			sql.append("  	     ADJUST_SUM,                                   ");
			sql.append("  	     VERSION,                                      ");
			sql.append("  	     CREATETIME,                                   ");
			sql.append("  	     CREATOR,                                      ");
			sql.append("  	     MODIFIER,                                     ");
			sql.append("  	     LASTUPDATE                                    ");
			sql.append("  FROM TBPMS_EMP_DEMOTION                              ");
			sql.append("  WHERE 1 = 1                                          ");
			if (!StringUtils.isBlank(inputVO.getRegion_center_id())) {
				sql.append(" AND REGION_CENTER_ID = :regionCenter              ");
				condition.setObject("regionCenter", inputVO.getRegion_center_id());
			}
			if (!StringUtils.isBlank(inputVO.getBranch_area_id())) {
				sql.append(" AND BRANCH_AREA_ID = :branchArea                  ");
				condition.setObject("branchArea", inputVO.getBranch_area_id());
			}
			if (!StringUtils.isBlank(inputVO.getBranch_nbr())) {
				sql.append(" AND BRANCH_NBR = :branchNbr                       ");
				condition.setObject("branchNbr", inputVO.getBranch_nbr());
			}
			if (!StringUtils.isBlank(inputVO.getAo_code())) {
				sql.append(" AND AO_CODE = :aoCode                              ");
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
		PMS325OutputVO return_VO2 = (PMS325OutputVO) body;
		List<Map<String, Object>> list = return_VO2.getCsvList();
		List<String> roleList = return_VO2.getRoleList();
		if(list.size() > 0){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH:mm:ss");
			String fileName = "理專降級報表_" + sdf.format(new Date()) + ".csv"; 
			List listCSV =  new ArrayList();
			String[] csvHeader = null;
			if(roleList.get(0).equals("1"))
			{
				for(Map<String, Object> map : list){
					String[] records = new String[29];
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
					records[++i] = checkIsNull(map, "GOAL_SENIORITY");  
					records[++i] = checkIsNull(map, "WORK_AGREEMENT"); 
					records[++i] = checkIsNull(map, "STAT_AVG1");  
					records[++i] = checkIsNull(map, "STAT_PROD_GOAL1");  
					records[++i] = pcntFormat(map, "STAT_GOAL1");  
					records[++i] = checkIsNull(map, "STAT_AVG2"); 
					records[++i] = checkIsNull(map, "STAT_PROD_GOAL2");  
					records[++i] = pcntFormat(map, "STAT_GOAL2");  
					records[++i] = checkIsNull(map, "AVG_REAL_INCOME");  
					records[++i] = checkIsNull(map, "WHET_DEG");  
					records[++i] = checkIsNull(map, "DEG_AFT_RANK");  
					records[++i] = checkIsNull(map, "DEG_AFT_GRA");  
					records[++i] = checkIsNull(map, "DEG_AFT_NAME");   
					records[++i] = checkIsNull(map, "ADJUST_SUM");                         //信用卡計績卡數達成率
					listCSV.add(records);
				}
				//header
				csvHeader = new String[29];
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
				csvHeader[++j] = "任該職務掛GOAL日";
				csvHeader[++j] = "掛GOAL年資";
				csvHeader[++j] = "勞動契約";
				csvHeader[++j] = "應達生產力目標第一次手收平均值";
				csvHeader[++j] = "應達生產力目標第一次生產力目標";
				csvHeader[++j] = "應達生產力目標第一次達成率";
				csvHeader[++j] = "應達生產力目標第二次手收平均值";
				csvHeader[++j] = "應達生產力目標第二次生產力目標";
				csvHeader[++j] = "應達生產力目標第二次達成率";
				csvHeader[++j] = "兩次平均實際收益";
				csvHeader[++j] = "是否降級";
				csvHeader[++j] = "降級後職級";
				csvHeader[++j] = "降級後職等";
				csvHeader[++j] = "降級後職稱";
				csvHeader[++j] = "調整金額";
			}else
			{
				for(Map<String, Object> map : list){
					String[] records = new String[17];
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
					records[++i] = checkIsNullAndTrans(map, "EMP_ID");                   //員工編號
					records[++i] = checkIsNull(map, "EMP_NAME");                         //姓名
					records[++i] = checkIsNull(map, "JOB_TITLE_ID");                     //職級
					records[++i] = checkIsNull(map, "JOB_LEVEL_NAME");                   //職等
					records[++i] = checkIsNull(map, "JOB_POSITION_NAME");                //職稱
					records[++i] = checkIsNull(map, "AVG_REAL_INCOME");                  //平均實際收益
					records[++i] = pcntFormat(map, "STAT_GOAL1");                        //應達生產力目標第一次達成率
					records[++i] = pcntFormat(map, "STAT_GOAL2");               		 //應達生產力目標第二次達成率
					listCSV.add(records);
				}
				//header
				csvHeader = new String[17];
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
				csvHeader[++j] = "員工編號";
				csvHeader[++j] = "姓名";
				csvHeader[++j] = "職級";
				csvHeader[++j] = "職等";
				csvHeader[++j] = "職稱";
				csvHeader[++j] = "平均實際收益";
				csvHeader[++j] = "應達生產力目標第一次達成率";
				csvHeader[++j] = "應達生產力目標第二次達成率";
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
}
