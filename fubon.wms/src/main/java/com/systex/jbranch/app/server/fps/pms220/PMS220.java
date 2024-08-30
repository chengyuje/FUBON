package com.systex.jbranch.app.server.fps.pms220;
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

import com.systex.jbranch.app.server.fps.pms220.PMS220InputVO;
import com.systex.jbranch.app.server.fps.pms220.PMS220OutputVO;
import com.systex.jbranch.app.server.fps.pms220.PMS220;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
/**
 * 
 * Copy Right Information : Forms Syntron <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description : 主管財務非財務報表Controller<br>
 * Comments Name : PMS220.java<br>
 * Author :zhouyiqiong<br>
 * Date :2016年11月10日 <br>
 * Version : 1.01 <br>
 * Editor : zhouyiqiong<br>
 * Editor Date : 2016年11月10日<br>
 */
@Component("pms220")
@Scope("request")
public class PMS220 extends FubonWmsBizLogic
{
	private DataAccessManager dam = null;
	private PMS220InputVO inputVO = null;
	private Logger logger = LoggerFactory.getLogger(PMS220.class);
	private static Map<String, String> descMap ;
	
	/**取得可視範圍**/
	public void getOrgInfo(Object body, IPrimitiveMap header) throws JBranchException, ParseException {	
		PMS220InputVO inputVO = (PMS220InputVO) body;
		PMS220OutputVO outputVO = new PMS220OutputVO();
		Timestamp stamp = new Timestamp(System.currentTimeMillis());		
		String loginID = (String)getUserVariable(FubonSystemVariableConsts.LOGINID);				
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT V_REGION_CENTER_ID, V_REGION_CENTER_NAME, ");
		sql.append("V_BRANCH_AREA_ID, V_BRANCH_AREA_NAME, ");
		sql.append("V_BRANCH_NBR, V_BRANCH_NAME, ");
		sql.append("V_AO_CODE, V_EMP_ID, V_EMP_NAME ");
		sql.append("FROM table( ");
		sql.append("FC_GET_VRR( ");
		sql.append(":purview_type, null, ");		
		sql.append(":e_dt, :emp_id, ");
		sql.append("null, null, null, null) ");		
		sql.append(") ");
		
		condition.setQueryString(sql.toString());
		condition.setObject("purview_type", "OTHER");   //非業績報表
//		condition.setObject("purview_type", "P_PERF");  //個人業績報表
//		condition.setObject("purview_type", "ORG_PERF"); //轄下人員業績
		
		if(inputVO.getsTime() != null && !"".equals(inputVO.getsTime())){				
			condition.setObject("e_dt", getMonthLastDate(inputVO.getsTime()));
		}else
			condition.setObject("e_dt", stamp);
		condition.setObject("emp_id", loginID);
		
		outputVO.setOrgList(dam.exeQuery(condition));
		sendRtnObject(outputVO);		
	}	
	/** ==可視範圍取月份最後一天==
	 * @throws ParseException **/
	private Date getMonthLastDate(String date) throws ParseException{
		DateFormat df = new SimpleDateFormat("yyyyMM");
		Calendar cal = Calendar.getInstance();
		Date rptDate = df.parse(date);
		cal.setTime(rptDate);
		cal.set(cal.DATE, cal.getActualMaximum(cal.DATE));
		return cal.getTime();
	}
	
	/**
	 * 員工編號
	 */
	public void empID(Object body, IPrimitiveMap header)
			throws JBranchException {
		PMS220OutputVO return_VO = new PMS220OutputVO();
		PMS220InputVO inputVO = (PMS220InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam
				.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		try {
			sql.append("  SELECT 														");
			sql.append("         S.EMP_ID                                               ");
			sql.append("        ,S.EMP_NAME                                             ");
			sql.append("  FROM TBORG_MEMBER S                                           ");
			sql.append("  LEFT JOIN TBORG_MEMBER_ROLE R                                 ");
			sql.append("       ON S.EMP_ID = R.EMP_ID                                   ");
			sql.append("  WHERE R.ROLE_ID IN('ABRF')                                    ");
			queryCondition.setQueryString(sql.toString());
			// result
			List<Map<String, Object>> list = dam.executeQuery(queryCondition);
			return_VO.setEmpIDList(list);
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
		PMS220OutputVO return_VO = new PMS220OutputVO();
		PMS220InputVO inputVO = (PMS220InputVO) body;
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
	 * 版本下拉選單
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void version(Object body, IPrimitiveMap header)
			throws JBranchException {
		PMS220OutputVO return_VO = new PMS220OutputVO();
		PMS220InputVO inputVO = (PMS220InputVO) body;
		dam = this.getDataAccessManager();
		String role = inputVO.getRole();
		QueryConditionIF queryCondition = dam
				.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		try {
			sql.append(" select * from table(FN_EXAM_REPORT('"+inputVO.getsTime()+"','"+role+"','PMS220'))");
			queryCondition.setQueryString(sql.toString());
			// result
			List<Map<String, Object>> list = dam.executeQuery(queryCondition);
			return_VO.setVerList(list);
			this.sendRtnObject(return_VO);
		} catch (Exception e) {
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
		PMS220InputVO inputVO = (PMS220InputVO) body;
		PMS220OutputVO outputVO = new PMS220OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		List roleList = new ArrayList();
		roleList.add(0, inputVO.getRole());
		StringBuffer sql = new StringBuffer();
		try
		{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			sql.append("  SELECT EXEC_DATE,														");
			sql.append("  	     REPORT_TYPE,                                                   ");
			sql.append("  	     OPEN_FLAG,                                                     ");
			sql.append("  	     YEARMON,                                                       ");
			sql.append("  	     REGION_CENTER_ID,                                              ");
			sql.append("  	     REGION_CENTER_NAME,                                            ");
			sql.append("  	     BRANCH_AREA_ID,                                                ");
			sql.append("  	     BRANCH_AREA_NAME,                                              ");
			sql.append("  	     BRANCH_NBR,                                                    ");
			sql.append("  	     BRANCH_NAME,                                                   ");
			sql.append("  	     EMP_ID,                                                        ");
			sql.append("  	     EMP_NAME,                                                      ");
			sql.append("  	     GROUP_ID,                                                      ");
			sql.append("  	     MANAGER_GROUP,                                                 ");
			sql.append("  	     PERSON_TYPE,                                                   ");
			sql.append("  	     PERSON_TYPE_NOTE,                                              ");			
			sql.append("  	     INS_INCOME,                                                    ");
			sql.append("  	     INS_GOAL,                                                      ");
			sql.append("  	     DEP_DIFF,                                                      ");
			sql.append("  	     DEP_DIFF_GOAL,                                                 ");
			sql.append("  	     ROUND(INS_YIELD_RATE*100,2)||'%' AS INS_YIELD_RATE,            ");
			sql.append("  	     INS_SCORE,                                                     ");
			sql.append("  	     DEP_AUM_IN,                                                    ");
			sql.append("  	     DEP_AUM_IN_GOAL,                                               ");
			sql.append("  	     DEP_AUM_IN_YTD,                                                ");
			sql.append("  	     DEP_AUM_IN_YTD_GOAL,                                           ");
			sql.append("  	     ROUND(DEP_AUM_IN_YTD_RATE*100,2)||'%' AS DEP_AUM_IN_YTD_RATE,  ");
			sql.append("  	     DEP_AUM_SC,                                                    ");
			sql.append("  	     INC_INSU_AUM,                                                  ");
			sql.append("  	     INC_INSU_AUM_GOAL,                                             ");
			sql.append("  	     INC_INSU_AUM_YTD,                                              ");
			sql.append("  	     INC_INSU_AUM_YTD_GOAL,                                         ");
			sql.append("  	     ROUND(INC_INSU_AUM_YTD_RATE*100,2)||'%' AS INC_INSU_AUM_YTD_RATE, ");
			sql.append("  	     INC_AUM_SC,                                                    ");
			sql.append("  	     AUM_SC,                                                        ");
			sql.append("  	     E_CL,                                                          ");
			sql.append("  	     E_CL_YTD,                                                      ");
			sql.append("  	     I_CL,                                                          ");
			sql.append("  	     I_CL_YTD,                                                      ");
			sql.append("  	     P_CL,                                                          ");
			sql.append("  	     P_CL_YTD,                                                      ");
			sql.append("  	     EIP_ALL_CL,                                                    ");
			sql.append("  	     EIP_ALL_CL_GOAL,                                               ");
			sql.append("  	     EIP_ALL_CL_YTD,                                                ");
			sql.append("  	     EIP_ALL_CL_YTD_GOAL,                                           ");
			sql.append("  	     NEW_CUST_CL,                                                   ");
			sql.append("  	     NEW_CUST_CL_GOAL,                                              ");
			sql.append("  	     NEW_CUST_CL_YTD,                                               ");
			sql.append("  	     NEW_CUST_CL_YTD_GOAL,                                          ");
			sql.append("  	     ROUND(CUST_ACH*100,2)||'%' AS CUST_ACH,                        ");
			sql.append("  	     CUST_SCORE,                                                    ");
			sql.append("  	     FIN_IND_SC,                                                    ");
			sql.append("  	     AUM_ICMT_ACH_SCORE,                                            ");
			sql.append("  	     ROUND(UN_FIN_IND_BONUS_RATE*100,2)||'%' AS UN_FIN_IND_BONUS_RATE,");
			sql.append("  	     ROUND(LACK_IND_RATE*100,2)||'%' AS LACK_IND_RATE,              ");
			sql.append("  	     LOST_CONTENT,                                                  ");
			sql.append("  	     VERSION,                                                       ");
			sql.append("  	     CREATETIME,                                                    ");
			sql.append("  	     CREATOR,                                                       ");
			sql.append("  	     MODIFIER,                                                      ");
			sql.append("  	     LASTUPDATE                                                     ");
			sql.append("  FROM TBPMS_MNGR_FIN_RPT                                               ");
			sql.append("  WHERE 1=1                                                             ");
			
			if (!StringUtils.isBlank(inputVO.getsTime())) {
				sql.append(" AND YEARMON = :YEARMON                                          ");
				condition.setObject("YEARMON", inputVO.getsTime());
			}
			if(StringUtils.isNotBlank(inputVO.getRptVersion())){
				if(inputVO.getRptVersion().equals("上簽版")){
					sql.append(" AND REPORT_TYPE = '1' 			              ");
					
				}else{
					sql.append(" AND REPORT_TYPE = '0' 			              ");
					sql.append(" AND EXEC_DATE = :rptVersion 	              ");
					condition.setObject("rptVersion", inputVO.getRptVersion().substring(3,11));
				}
			}
			if (!StringUtils.isBlank(inputVO.getRegion())) {
				sql.append(" AND REGION_CENTER_ID = :region                                 ");
				condition.setObject("region",inputVO.getRegion());
			}
			if (!StringUtils.isBlank(inputVO.getOp())) {
				sql.append(" AND BRANCH_AREA_ID = :op                                   ");
				condition.setObject("op",inputVO.getOp());
			}
			if (!StringUtils.isBlank(inputVO.getBranch())) {
				sql.append(" AND BRANCH_NBR = :branch                                       ");
				condition.setObject("branch",inputVO.getBranch());
			}
			if (!StringUtils.isBlank(inputVO.getEmpId())) {
				sql.append(" AND EMP_ID = :empId                                           ");
				condition.setObject("empId",inputVO.getEmpId());
			}
			if (!StringUtils.isBlank(inputVO.getPersonType())) {
				sql.append(" AND PERSON_TYPE = :perType                                      ");
				condition.setObject("perType",inputVO.getPersonType());
			}
			sql.append("  order by REGION_CENTER_ID,BRANCH_AREA_ID, DECODE(BRANCH_NBR,'-','999',BRANCH_NBR), ");
			sql.append("           DECODE(PERSON_TYPE,'SH','1','BM','2','3'),MANAGER_GROUP ");
			condition.setQueryString(sql.toString());
			ResultIF list = dam.executePaging(condition, inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
			List<Map<String, Object>> list1 = dam.exeQuery(condition);
			int totalPage_i = list.getTotalPage(); // 分頁用
			int totalRecord_i = list.getTotalRecord(); // 分頁用
			outputVO.setResultList(list); // data
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
		PMS220OutputVO return_VO2 = (PMS220OutputVO) body;
		List<Map<String, Object>> list = return_VO2.getCsvList();
		List<String> roleList = return_VO2.getRoleList();
		if(list.size() > 0){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH:mm:ss");
			String fileName = "主管財務非財務報表_" + sdf.format(new Date()) + ".csv"; 
			List listCSV =  new ArrayList();
			String[] csvHeader = null;
			if(roleList.get(0).equals("1"))
			{
				for(Map<String, Object> map : list){
					String[] records = new String[57];
					int i = 0;
					records[i] = checkIsNull(map, "YEARMON");                         //資料年月
					records[++i] = checkIsNull(map, "REGION_CENTER_ID");            //區域中心
					records[++i] = checkIsNull(map, "REGION_CENTER_NAME");            //區域中心
					records[++i] = checkIsNull(map, "BRANCH_AREA_ID");              //營運區
					records[++i] = checkIsNull(map, "BRANCH_AREA_NAME");              //營運區
					records[++i] = checkIsNull(map, "BRANCH_NBR");                    //分行代碼/母行分行代碼
					records[++i] = checkIsNull(map, "BRANCH_NAME");                   //分別行/母行別
					records[++i] = checkIsNull(map, "GROUP_ID");                      //分行組別/區組別
					records[++i] = checkIsNull(map, "MANAGER_GROUP");                 //業務主管組別
					records[++i] = checkIsNull(map, "PERSON_TYPE");                   //人員類別
					records[++i] = descFormat(map, "PERSON_TYPE_NOTE");              //人員類別註記
					records[++i] = checkIsNullAndTrans(map, "EMP_ID");                //員工編號
					records[++i] = checkIsNull(map, "EMP_NAME");                      //員工姓名
					records[++i] = checkIsNull(map, "INS_INCOME");                    //投保收益(a)
					records[++i] = checkIsNull(map, "INS_GOAL");                      //投保收益目標(b)
					records[++i] = checkIsNull(map, "DEP_DIFF");                      //存款利差(c)
					records[++i] = checkIsNull(map, "DEP_DIFF_GOAL");                 //存款利差目標(d)
					records[++i] = checkIsNull(map, "INS_YIELD_RATE");                //存投保達成率=(a+c)/(b+d)
					records[++i] = checkIsNull(map, "INS_SCORE");                     //存投保得分
					records[++i] = checkIsNull(map, "DEP_AUM_IN");                    //當月增量(含台定減量加回)
					records[++i] = checkIsNull(map, "DEP_AUM_IN_GOAL");               //當月增量目標
					records[++i] = checkIsNull(map, "DEP_AUM_IN_YTD");                //增量YTD(含台定減量加回)
					records[++i] = checkIsNull(map, "DEP_AUM_IN_YTD_GOAL");           //增量目標YTD
					records[++i] = checkIsNull(map, "DEP_AUM_IN_YTD_RATE");           //達成率YTD
					records[++i] = checkIsNull(map, "DEP_AUM_SC");                    //得分
					records[++i] = checkIsNull(map, "INC_INSU_AUM");                  //當月增量
					records[++i] = checkIsNull(map, "INC_INSU_AUM_GOAL");             //當月增量目標
					records[++i] = checkIsNull(map, "INC_INSU_AUM_YTD");              //增量YTD
					records[++i] = checkIsNull(map, "INC_INSU_AUM_YTD_GOAL");         //增量目標YTD
					records[++i] = checkIsNull(map, "INC_INSU_AUM_YTD_RATE");         //達成率YTD
					records[++i] = checkIsNull(map, "INC_AUM_SC");                    //得分
					records[++i] = checkIsNull(map, "AUM_SC");                        //AUM得分
					records[++i] = checkIsNull(map, "E_CL");                          //E級客戶當月增量
					records[++i] = checkIsNull(map, "E_CL_YTD");                      //E級客戶增量YTD
					records[++i] = checkIsNull(map, "I_CL");                          //I級客戶當月增量
					records[++i] = checkIsNull(map, "I_CL_YTD");                      //I級客戶增量YTD
					records[++i] = checkIsNull(map, "P_CL");                          //P級客戶當月增量
					records[++i] = checkIsNull(map, "P_CL_YTD");                      //P級客戶增量YTD
					records[++i] = checkIsNull(map, "EIP_ALL_CL");                    //EIP合計當月增量
					records[++i] = checkIsNull(map, "EIP_ALL_CL_GOAL");               //EIP合計當月目標
					records[++i] = checkIsNull(map, "EIP_ALL_CL_YTD");                //EIP合計增量YTD
					records[++i] = checkIsNull(map, "EIP_ALL_CL_YTD_GOAL");           //EIP合計增量目標YTD
					records[++i] = checkIsNull(map, "NEW_CUST_CL");                   //當月增量
					records[++i] = checkIsNull(map, "NEW_CUST_CL_GOAL");              //當月增量目標
					records[++i] = checkIsNull(map, "NEW_CUST_CL_YTD");               //增量YTD
					records[++i] = checkIsNull(map, "NEW_CUST_CL_YTD_GOAL");         //增量目標YTD
					records[++i] = checkIsNull(map, "CUST_ACH");                      //客戶數達成率
					records[++i] = checkIsNull(map, "CUST_SCORE");                    //客戶數得分
					records[++i] = checkIsNull(map, "AUM_ICMT_ACH_SCORE");            //AUM+客戶數得分
					records[++i] = checkIsNull(map, "FIN_IND_SC");                    //財務指標得分
					records[++i] = checkIsNull(map, "UN_FIN_IND_BONUS_RATE");         //非財務指標扣減百分比%
					records[++i] = checkIsNull(map, "LACK_IND_RATE");                 //獨立列示重大缺失扣減百分比
					records[++i] = checkIsNull(map, "LOST_CONTENT");                  //缺失內容
					listCSV.add(records);
				}
				//header
				csvHeader = new String[57];
				int j = 0;
				csvHeader[j] = "資料年月";
				csvHeader[++j] = "業務處代碼";
				csvHeader[++j] = "業務處名稱";
				csvHeader[++j] = "營運區代碼";
				csvHeader[++j] = "營運區名稱";
				csvHeader[++j] = "分行代碼/母行分行代碼";
				csvHeader[++j] = "分行別/母行別";
				csvHeader[++j] = "分行組別/區組別";
				csvHeader[++j] = "業務主管組別";
				csvHeader[++j] = "人員類別";
				csvHeader[++j] = "類別註記";
				csvHeader[++j] = "員工編號";
				csvHeader[++j] = "員工姓名";
				
				csvHeader[++j] = "存投保-投保收益(a)";
				csvHeader[++j] = "存投保-投保收益目標(b)";
				csvHeader[++j] = "存投保-存款利差(c)";
				csvHeader[++j] = "存投保-存款利差目標(d)";
				csvHeader[++j] = "存投保-存投保達成率=(a+c)/(b+d)";
				csvHeader[++j] = "存投保-存投保得分";
				csvHeader[++j] = "存款AUM-當月增量(含台定減量加回)";
				csvHeader[++j] = "存款AUM-當月增量目標";
				csvHeader[++j] = "存款AUM-增量YTD(含台定減量加回)";
				csvHeader[++j] = "存款AUM-增量目標YTD";
				csvHeader[++j] = "存款AUM-達成率YTD";
				csvHeader[++j] = "存款AUM-得分";
				csvHeader[++j] = "投保AUM-當月增量";
				csvHeader[++j] = "投保AUM-當月增量目標";
				csvHeader[++j] = "投保AUM-增量YTD";
				csvHeader[++j] = "投保AUM-增量目標YTD";
				csvHeader[++j] = "投保AUM-達成率YTD";
				csvHeader[++j] = "投保AUM-得分";
				csvHeader[++j] = "AUM得分";
				csvHeader[++j] = "EIP客戶數-E級客戶當月增量";
				csvHeader[++j] = "EIP客戶數-E級客戶增量YTD";
				csvHeader[++j] = "EIP客戶數-I級客戶當月增量";
				csvHeader[++j] = "EIP客戶數-I級客戶增量YTD";
				csvHeader[++j] = "EIP客戶數-P級客戶當月增量";
				csvHeader[++j] = "EIP客戶數-P級客戶增量YTD";
				csvHeader[++j] = "EIP客戶數-EIP合計當月增量";
				csvHeader[++j] = "EIP客戶數-EIP合計當月目標";
				csvHeader[++j] = "EIP客戶數-EIP合計增量YTD";
				csvHeader[++j] = "EIP客戶數-EIP合計增量目標YTD";
				csvHeader[++j] = "新客戶數-當月增量";
				csvHeader[++j] = "新客戶數-當月增量目標";
				csvHeader[++j] = "新客戶數-增量YTD";
				csvHeader[++j] = "新客戶數-增量目標YTD";
				csvHeader[++j] = "客戶數達成率";
				csvHeader[++j] = "客戶數得分";
				csvHeader[++j] = "AUM+客戶數得分";
				csvHeader[++j] = "財務指標得分";
				csvHeader[++j] = "非財務指標扣減百分比%";
				csvHeader[++j] = "獨立列示重大缺失扣減百分比";
				csvHeader[++j] = "缺失內容";
			}else
			{
				for(Map<String, Object> map : list){
					String[] records = new String[42];
					int i = 0;
					records[i] = checkIsNull(map, "YEARMON");                      //資料年月
					records[++i] = checkIsNull(map, "REGION_CENTER_ID");         //區域中心
					records[++i] = checkIsNull(map, "REGION_CENTER_NAME");         //區域中心
					records[++i] = checkIsNull(map, "BRANCH_AREA_ID");           //營運區
					records[++i] = checkIsNull(map, "BRANCH_AREA_NAME");           //營運區
					records[++i] = checkIsNull(map, "BRANCH_NBR");   			   //分行代碼/母行分行代碼
					records[++i] = checkIsNull(map, "BRANCH_NAME");  			   //分別行/母行別
					records[++i] = checkIsNull(map, "GROUP_ID");   				   //分行組別/區組別
					records[++i] = checkIsNull(map, "MANAGER_GROUP");  			   //業務主管組別
					records[++i] = checkIsNull(map, "PERSON_TYPE");  			   //人員類別
					records[++i] = checkIsNullAndTrans(map, "EMP_ID");  		   //員工編號
					records[++i] = checkIsNull(map, "EMP_NAME");  			       //員工姓名
					
					records[++i] = checkIsNull(map, "INS_INCOME");                 //投保收益(a)
					records[++i] = checkIsNull(map, "INS_GOAL");                   //投保收益目標(b)
					records[++i] = checkIsNull(map, "DEP_DIFF");                   //存款利差(c)
					records[++i] = checkIsNull(map, "DEP_DIFF_GOAL");              //存款利差目標(d)
					records[++i] = checkIsNull(map, "INS_YIELD_RATE");             //存投保達成率=(a+c)/(b+d)
					records[++i] = checkIsNull(map, "INS_SCORE");                  //存投保得分
					
					records[++i] = checkIsNull(map, "DEP_AUM_IN_YTD");             //增量YTD(含台定減量加回)
					records[++i] = checkIsNull(map, "DEP_AUM_IN_YTD_GOAL");        //增量目標YTD
					records[++i] = checkIsNull(map, "DEP_AUM_IN_YTD_RATE");        //達成率YTD
					records[++i] = checkIsNull(map, "DEP_AUM_SC");                 //得分
					
					records[++i] = checkIsNull(map, "INC_INSU_AUM_YTD");           //增量YTD
					records[++i] = checkIsNull(map, "INC_INSU_AUM_YTD_GOAL");      //增量目標YTD
					records[++i] = checkIsNull(map, "INC_INSU_AUM_YTD_RATE");      //達成率YTD
					records[++i] = checkIsNull(map, "INC_AUM_SC");                 //得分
					
					records[++i] = checkIsNull(map, "AUM_SC");                     //AUM得分
					
					records[++i] = checkIsNull(map, "E_CL_YTD");                   //E級客戶增量YTD
					records[++i] = checkIsNull(map, "I_CL_YTD");                   //I級客戶增量YTD
					records[++i] = checkIsNull(map, "P_CL_YTD");                   //P級客戶增量YTD
					records[++i] = checkIsNull(map, "EIP_ALL_CL_YTD");             //EIP合計增量YTD
					records[++i] = checkIsNull(map, "EIP_ALL_CL_YTD_GOAL");        //EIP合計增量目標YTD
					
					records[++i] = checkIsNull(map, "NEW_CUST_CL_YTD");            //增量YTD
					records[++i] = checkIsNull(map, "NEW_CUST_CL_YTD_GOAL");      //增量目標YTD
					
					records[++i] = checkIsNull(map, "CUST_ACH");                   //客戶數達成率
					records[++i] = checkIsNull(map, "CUST_SCORE");                 //客戶數得分
					records[++i] = checkIsNull(map, "AUM_ICMT_ACH_SCORE");         //AUM+客戶數得分
					records[++i] = checkIsNull(map, "FIN_IND_SC");                 //財務指標得分
					records[++i] = checkIsNull(map, "UN_FIN_IND_BONUS_RATE");      //非財務指標扣減百分比%
					records[++i] = checkIsNull(map, "LACK_IND_RATE");              //獨立列示重大缺失扣減百分比
					records[++i] = checkIsNull(map, "LOST_CONTENT");               //缺失內容
					listCSV.add(records);
				}
				//header
				csvHeader = new String[42];
				int j = 0;
				csvHeader[j] = "資料年月";
				csvHeader[++j] = "業務處代碼";
				csvHeader[++j] = "業務處名稱";
				csvHeader[++j] = "營運區代碼";
				csvHeader[++j] = "營運區名稱";
				csvHeader[++j] = "分行代碼";
				csvHeader[++j] = "分行別";
				csvHeader[++j] = "分行組別";
				csvHeader[++j] = "業務主管組別";
				csvHeader[++j] = "人員類別";
				csvHeader[++j] = "員工編號";
				csvHeader[++j] = "員工姓名";
				
				csvHeader[++j] = "存投保-投保收益(a)";
				csvHeader[++j] = "存投保-投保收益目標(b)";
				csvHeader[++j] = "存投保-存款利差(c)";
				csvHeader[++j] = "存投保-存款利差目標(d)";
				csvHeader[++j] = "存投保-存投保達成率=(a+c)/(b+d)";
				csvHeader[++j] = "存投保-存投保得分";
				
				csvHeader[++j] = "存款AUM-增量YTD(含台定減量加回)";
				csvHeader[++j] = "存款AUM-增量目標YTD";
				csvHeader[++j] = "存款AUM-達成率YTD";
				csvHeader[++j] = "存款AUM-得分";
				
				csvHeader[++j] = "投保AUM-增量YTD";
				csvHeader[++j] = "投保AUM-增量目標YTD";
				csvHeader[++j] = "投保AUM-達成率YTD";
				csvHeader[++j] = "投保AUM-得分";
				
				csvHeader[++j] = "AUM得分";
				
				csvHeader[++j] = "EIP客戶數-E級客戶增量YTD";
				csvHeader[++j] = "EIP客戶數-I級客戶增量YTD";
				csvHeader[++j] = "EIP客戶數-P級客戶增量YTD";
				csvHeader[++j] = "EIP客戶數-EIP合計增量YTD";
				csvHeader[++j] = "EIP客戶數-EIP合計增量目標YTD";
				
				csvHeader[++j] = "新客戶數-增量YTD";
				csvHeader[++j] = "新客戶數-增量目標YTD";
				
				csvHeader[++j] = "客戶數達成率";
				csvHeader[++j] = "客戶數得分";
				csvHeader[++j] = "AUM+客戶數得分";
				csvHeader[++j] = "財務指標得分";
				csvHeader[++j] = "非財務指標扣減百分比%";
				csvHeader[++j] = "獨立列示重大缺失扣減百分比";
				csvHeader[++j] = "缺失內容";
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
	* 檢查Map取出欄位是否為 用於AO_CODE EMP_ID 
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
	
	/**
	* 身份類別註記格式轉換
	* PERSON_TYPE_NOTE  
	* @param map
	* @return String
	 * @throws JBranchException 
	*/
	private String descFormat(Map map, String key) throws JBranchException {		
		if(descMap==null){
			XmlInfo xmlInfo = new XmlInfo();
			descMap = xmlInfo.doGetVariable("PMS.PERSON_TYPE_NOTE", FormatHelper.FORMAT_3);     //軌跡類型	
		}	
		if (StringUtils.isNotBlank(String.valueOf(map.get(key)))
				&& map.get(key) != null) {
			String type = ""; 
			if(map.get(key)!=null)
				type=descMap.get(map.get(key)+"");		
			return type;
		} else
			return "";
	}	
}

