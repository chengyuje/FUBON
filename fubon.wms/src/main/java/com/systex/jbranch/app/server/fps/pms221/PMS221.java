package com.systex.jbranch.app.server.fps.pms221;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.server.fps.pms221.PMS221InputVO;
import com.systex.jbranch.app.server.fps.pms221.PMS221OutputVO;
import com.systex.jbranch.app.server.fps.pms221.PMS221;
import com.systex.jbranch.app.server.fps.pms221.PMS221InputVO;
import com.systex.jbranch.app.server.fps.pms221.PMS221OutputVO;
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
 * Description : 主管獎勵金報表Controller<br>
 * Comments Name : PMS221.java<br>
 * Author :zhouyiqiong<br>
 * Date :2016年11月10日 <br>
 * Version : 1.01 <br>
 * Editor : zhouyiqiong<br>
 * Editor Date : 2016年11月10日<br>
 */
@Component("pms221")
@Scope("request")
public class PMS221 extends FubonWmsBizLogic
{
	private DataAccessManager dam = null;
	private PMS221InputVO inputVO = null;
	private Logger logger = LoggerFactory.getLogger(PMS221.class);
	
	/**
	 * 員工編號
	 */
	/*public void empID(Object body, IPrimitiveMap header)
			throws JBranchException 
	{
		PMS221OutputVO return_VO = new PMS221OutputVO();
		PMS221InputVO inputVO = (PMS221InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam
				.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		try 
		{
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
	}*/
	/**
	 * 角色獲取
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void getRole(Object body, IPrimitiveMap header)
			throws JBranchException {
		PMS221OutputVO return_VO = new PMS221OutputVO();
		PMS221InputVO inputVO = (PMS221InputVO) body;
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
			throws JBranchException 
	{
		PMS221OutputVO return_VO = new PMS221OutputVO();
		PMS221InputVO inputVO = (PMS221InputVO) body;
		dam = this.getDataAccessManager();
		String role = inputVO.getRole();
		QueryConditionIF queryCondition = dam
				.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		try 
		{
			sql.append(" select * from table(FN_EXAM_REPORT('"+inputVO.getsTime()+"','"+role+"','PMS221'))");
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
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void inquire(Object body, IPrimitiveMap header) throws JBranchException
	{	
		PMS221InputVO inputVO = (PMS221InputVO) body;
		PMS221OutputVO outputVO = new PMS221OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		List roleList = new ArrayList();
		roleList.add(0, inputVO.getRole());
		//QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		//ArrayList<String> sql_list = new ArrayList<String>();
		StringBuffer sql = new StringBuffer();
		try
		{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			sql.append("  SELECT YEARMON,	                                          ");
			sql.append("  	     REPORT_TYPE,                                         ");
			sql.append("  	     OPEN_FLAG,                                           ");
			sql.append("  	     REGION_CENTER_ID,                                    ");
			sql.append("  	     EXEC_DATE,                                           ");
			sql.append("  	     REGION_CENTER_NAME,                                  ");
			sql.append("  	     BRANCH_AREA_ID,                                      ");
			sql.append("  	     BRANCH_AREA_NAME,                                    ");
			sql.append("  	     BRANCH_NBR,                                          ");
			sql.append("  	     BRANCH_NAME,                                         ");
			sql.append("  	     EMP_ID,                                              ");
			sql.append("  	     EMP_NAME,                                            ");
			sql.append("  	     GROUP_ID,                                            ");
			sql.append("  	     PERSON_TYPE,                                         ");
			sql.append("  	     SCORE_FIN_INDEX,                                     ");
			sql.append("  	     BONUS_FIN_INDEX,                                     ");
			sql.append("  	     FC_ALL_NUM,                                          ");
			sql.append("  	     FC_ACH_PROD_NUM,                                     ");
			sql.append("  	     FC_ACH_PROD_RATE,                                    ");
			sql.append("  	     FC_ACH_GOAL_BONUS_RATE,                              ");
			sql.append("  	     NEW_OR_TRANS_YR_MN,                                  ");
			sql.append("  	     NEW_OR_TRANS_MN_NUM,                                 ");
			sql.append("  	     ORIG_BRANCH_NBR,                                     ");
			sql.append("  	     NEW_BRANCH_RATE,                                     ");
			sql.append("  	     ORIG_BRANCH_RATE,                                    ");
			sql.append("  	     BUS_BONUS,                                           ");
			sql.append("  	     UN_FIN_DEDUCAMT,                                     ");
			sql.append("  	     LACK_IND_AMT,                                        ");
			sql.append("  	     PREVIOUS_DEDUC_AMT,                                  ");
			sql.append("  	     REAL_DEDUC_AMT,                                      ");
			sql.append("  	     BUSI_BONUS_100_PERTG,                                ");
			sql.append("  	     BUSI_BONUS_80_PERTG,                                 ");
			sql.append("  	     BUSI_BONUS_CNRADJ,                                   ");
			sql.append("  	     BUSI_BONUS_CNR_REAL,                                 ");
			sql.append("  	     BONUS_CNRADJ_NOTE,                                   ");
			sql.append("  	     MANAGER_GROUP                                        ");
			sql.append("  FROM  TBPMS_MNGR_BONUS_RPT                                  ");
			//sql.append("    and dep.YEARMON between org.START_TIME and org.END_TIME");
			sql.append("  WHERE 1=1                                                       ");
			if (!StringUtils.isBlank(inputVO.getsTime())) {
				sql.append(" AND TRIM(YEARMON) = :YEARMON                     ");
				condition.setObject("YEARMON", inputVO.getsTime());
			}
			if(StringUtils.isNotBlank(inputVO.getRptVersion())){
				if(inputVO.getRptVersion().equals("上簽版")){
					sql.append(" AND REPORT_TYPE = '1' 					              ");
				}else{
					sql.append(" AND REPORT_TYPE = '0' 					              ");
					sql.append(" AND EXEC_DATE = :rptVersion 	              ");
					condition.setObject("rptVersion", inputVO.getRptVersion().substring(3,11));
				}
			}
			if (!StringUtils.isBlank(inputVO.getRegion_center_id())) {
				sql.append(" AND REGION_CENTER_ID = :region_center_id         ");
				condition.setObject("region_center_id", inputVO.getRegion_center_id());
			}
			if (!StringUtils.isBlank(inputVO.getBranch_area_id())) {
				sql.append(" AND BRANCH_AREA_ID = :branch_area_id             ");
				condition.setObject("branch_area_id", inputVO.getBranch_area_id());
			}
			if (!StringUtils.isBlank(inputVO.getBranch_nbr())) {
				sql.append(" AND BRANCH_NBR = :branch_nbr                     ");
				condition.setObject("branch_nbr", inputVO.getBranch_nbr());
			}
			if (!StringUtils.isBlank(inputVO.getEmp_id())){
				sql.append(" AND EMP_ID = :emp_id 					    	  ");
				condition.setObject("emp_id", inputVO.getEmp_id());
			}
			if (!StringUtils.isBlank(inputVO.getPersonType())) {
				sql.append(" AND PERSON_TYPE = :personType                                      ");
				condition.setObject("personType", inputVO.getPersonType());
			}
			sql.append("  order by REGION_CENTER_ID, BRANCH_AREA_ID, DECODE(BRANCH_NBR,'-','999',BRANCH_NBR),   ");
			sql.append("           DECODE(PERSON_TYPE,'SH','1','BM','2','3'), MANAGER_GROUP  ");
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
			/*outputVO.setTotalList(dam.exeQuery(condition));
			ResultIF list = dam.executePaging(condition, inputVO
					.getCurrentPageIndex() + 1, inputVO.getPageCount());
			List<Map<String, Object>> list1 = dam.exeQuery(condition);
			int totalPage_i = list.getTotalPage();
			int totalRecord_i = list.getTotalRecord();
			outputVO.setResultList(list);
			outputVO.setCsvList(list1);
			outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());  // 當前頁次
			outputVO.setTotalPage(totalPage_i);                           // 總頁次
			outputVO.setTotalRecord(totalRecord_i);                       // 總筆數
			outputVO.setRoleList(roleList);
			this.sendRtnObject(outputVO);*/
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
	public void export(Object body, IPrimitiveMap header) throws JBranchException {
		PMS221OutputVO return_VO2 = (PMS221OutputVO) body;
		List<Map<String, Object>> list = return_VO2.getCsvList();
		List<String> roleList = return_VO2.getRoleList();
		if(list.size() > 0){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH:mm:ss");
			String fileName = "主管獎勵金報表_" + sdf.format(new Date()) + ".csv"; 
			List listCSV =  new ArrayList();
			String[] csvHeader = null;
			if(roleList.get(0).equals("1")) 
			{
				for(Map<String, Object> map : list){
					String[] records = new String[37];
					int i = 0;
					records[i] = checkIsNull(map, "YEARMON");                           //資料年月
					records[++i] = checkIsNull(map, "REGION_CENTER_ID");                //區域中心代碼
					records[++i] = checkIsNull(map, "REGION_CENTER_NAME");              //區域中心
					records[++i] = checkIsNull(map, "BRANCH_AREA_ID");                  //營運區代碼
					records[++i] = checkIsNull(map, "BRANCH_AREA_NAME");                //營運區
					records[++i] = checkIsNull(map, "BRANCH_NBR");                      //分行代碼母行分行代碼
					records[++i] = checkIsNull(map, "BRANCH_NAME");             	    //分別行母行別
					records[++i] = checkIsNull(map, "PERSON_TYPE");             		//人員類別
					records[++i] = checkIsNullAndTrans(map, "EMP_ID");             			    //員工編號
					records[++i] = checkIsNull(map, "EMP_NAME");            		    //員工姓名
					records[++i] = checkIsNull(map, "SCORE_FIN_INDEX");                 //財務指標得分
					records[++i] = checkIsNull(map, "BONUS_FIN_INDEX");                 //財務指標績效獎金
					records[++i] = checkIsNull(map, "FC_ALL_NUM");             		    //應有轄下專員總人數
					records[++i] = checkIsNull(map, "FC_ACH_PROD_NUM");                 //達標準生產力專員人數
					records[++i] = checkIsNull(map, "FC_ACH_PROD_RATE");                //專員達標準生產力占比
					records[++i] = checkIsNull(map, "FC_ACH_GOAL_BONUS_RATE");          //專員達GOAL率對應獎勵金成數
					records[++i] = checkIsNull(map, "NEW_OR_TRANS_YR_MN");              //新、調任年月
					records[++i] = checkIsNull(map, "NEW_OR_TRANS_MN_NUM");             //新、調任分行任職月份數
					records[++i] = checkIsNull(map, "ORIG_BRANCH_NBR");                 //原分行代碼
					records[++i] = checkIsNull(map, "NEW_BRANCH_RATE");                 //新分行成數
					records[++i] = checkIsNull(map, "ORIG_BRANCH_RATE");                //原分行成數
					records[++i] = checkIsNull(map, "BUS_BONUS");             		    //業務獎勵金(過新、調任成數、達goal率獎金折數)
					records[++i] = checkIsNull(map, "UN_FIN_DEDUCAMT");                 //非財務指標扣減金額
					records[++i] = checkIsNull(map, "LACK_IND_AMT");              		//獨立列示重大缺失扣減金額
					records[++i] = checkIsNull(map, "PREVIOUS_DEDUC_AMT");              //上期遞延扣減金額
					records[++i] = checkIsNull(map, "REAL_DEDUC_AMT");                  //實際扣減金額(本月+上期遞延)
					records[++i] = checkIsNull(map, "BUSI_BONUS_100_PERTG");            //業務獎勵金100%
					records[++i] = checkIsNull(map, "BUSI_BONUS_80_PERTG");             //業務獎勵金80%
					records[++i] = checkIsNull(map, "BUSI_BONUS_CNRADJ");               //業務獎勵金應調整數
					records[++i] = checkIsNull(map, "BUSI_BONUS_CNR_REAL");             //業務獎勵金實發
					records[++i] = checkIsNull(map, "BONUS_CNRADJ_NOTE");               //業務獎勵金調整說明
					listCSV.add(records);
				}
				//header
				csvHeader = new String[37];
				int j = 0;
				csvHeader[j] = "資料年月";
				csvHeader[++j] = "業務處代碼";
				csvHeader[++j] = "業務處名稱";
				csvHeader[++j] = "營運區代碼";
				csvHeader[++j] = "營運區名稱";
				csvHeader[++j] = "分行代碼/母行分行代碼";
				csvHeader[++j] = "分行別/母行別";
				csvHeader[++j] = "人員類別";
				csvHeader[++j] = "員工編號";
				csvHeader[++j] = "員工姓名";
				
				csvHeader[++j] = "財務指標得分";
				csvHeader[++j] = "財務指標績效獎金";
				csvHeader[++j] = "應有轄下專員總人數";
				csvHeader[++j] = "達標準生產力專員人數";
				csvHeader[++j] = "專員達標準生產力占比";
				csvHeader[++j] = "專員達GOAL率對應獎勵金成數";
				
				csvHeader[++j] = "新、調任年月";			
				csvHeader[++j] = "新、調任分行任職月份數";
				csvHeader[++j] = "原分行代碼";
				csvHeader[++j] = "新分行成數";
				
				csvHeader[++j] = "原分行成數";			
				csvHeader[++j] = "業務獎勵金(過新、調任成數、達goal率獎金折數)";
				csvHeader[++j] = "非財務指標扣減金額";
				csvHeader[++j] = "獨立列示重大缺失扣減金額";
				
				csvHeader[++j] = "上期遞延扣減金額";			
				csvHeader[++j] = "實際扣減金額(本月+上期遞延)";
				csvHeader[++j] = "業務獎勵金100%";
				csvHeader[++j] = "業務獎勵金80%";
				csvHeader[++j] = "業務獎勵金應調整數";			
				csvHeader[++j] = "業務獎勵金實發";
				csvHeader[++j] = "業務獎勵金調整說明";
			}else 
			{
				for(Map<String, Object> map : list){
					String[] records = new String[37];
					int i = 0;
					records[i] = checkIsNull(map, "YEARMON");                            //資料年月
					records[++i] = checkIsNull(map, "REGION_CENTER_ID");                 //區域中心代碼
					records[++i] = checkIsNull(map, "REGION_CENTER_NAME");               //區域中心
					records[++i] = checkIsNull(map, "BRANCH_AREA_ID");                 //營運區
					records[++i] = checkIsNull(map, "BRANCH_AREA_NAME");                 //營運區
					records[++i] = checkIsNull(map, "BRANCH_NBR");             		     //分行代碼母行分行代碼
					records[++i] = checkIsNull(map, "BRANCH_NAME");                      //分別行母行別
					records[++i] = checkIsNull(map, "GROUP_ID");            		    //分行組別區組別
					records[++i] = checkIsNull(map, "MANAGER_GROUP");            	    //業務主管組別
					records[++i] = checkIsNull(map, "PERSON_TYPE");                      //人員類別
					records[++i] = checkIsNullAndTrans(map, "EMP_ID");             			     //員工編號
					records[++i] = checkIsNull(map, "EMP_NAME");              		     //員工姓名
					records[++i] = checkIsNull(map, "SCORE_FIN_INDEX");                  //財務指標得分
					records[++i] = checkIsNull(map, "BONUS_FIN_INDEX");                  //財務指標績效獎金
					records[++i] = checkIsNull(map, "FC_ALL_NUM");                       //應有轄下專員總人數
					records[++i] = checkIsNull(map, "FC_ACH_PROD_NUM");                  //達標準生產力專員人數
					records[++i] = checkIsNull(map, "FC_ACH_PROD_RATE");                 //專員達標準生產力占比
					records[++i] = checkIsNull(map, "FC_ACH_GOAL_BONUS_RATE");           //專員達GOAL率對應獎勵金成數
					records[++i] = checkIsNull(map, "NEW_OR_TRANS_YR_MN");               //新、調任年月
					records[++i] = checkIsNull(map, "NEW_OR_TRANS_MN_NUM");              //新、調任分行任職月份數
					records[++i] = checkIsNull(map, "ORIG_BRANCH_NBR");                  //原分行代碼
					records[++i] = checkIsNull(map, "NEW_BRANCH_RATE");                  //新分行成數
					records[++i] = checkIsNull(map, "ORIG_BRANCH_RATE");                 //原分行成數
					records[++i] = checkIsNull(map, "BUS_BONUS");                        //業務獎勵金(過新、調任成數、達goal率獎金折數)
					records[++i] = checkIsNull(map, "UN_FIN_DEDUCAMT");                  //非財務指標扣減金額
					records[++i] = checkIsNull(map, "LACK_IND_AMT");                     //獨立列示重大缺失扣減金額
					records[++i] = checkIsNull(map, "PREVIOUS_DEDUC_AMT");               //上期遞延扣減金額
					records[++i] = checkIsNull(map, "REAL_DEDUC_AMT");                   //實際扣減金額(本月+上期遞延)
					records[++i] = checkIsNull(map, "BUSI_BONUS_100_PERTG");             //業務獎勵金100%
					records[++i] = checkIsNull(map, "BUSI_BONUS_80_PERTG");              //業務獎勵金80%
					records[++i] = checkIsNull(map, "BUSI_BONUS_CNRADJ");                //業務獎勵金應調整數
					records[++i] = checkIsNull(map, "BUSI_BONUS_CNR_REAL");              //業務獎勵金實發
					records[++i] = checkIsNull(map, "BONUS_CNRADJ_NOTE");                //業務獎勵金調整說明
					listCSV.add(records);
				}
				//header
				csvHeader = new String[37];
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
				csvHeader[++j] = "員工編號";
				csvHeader[++j] = "員工姓名";
				
				csvHeader[++j] = "財務指標得分";
				csvHeader[++j] = "財務指標績效獎金";
				csvHeader[++j] = "應有轄下專員總人數";
				csvHeader[++j] = "達標準生產力專員人數";
				csvHeader[++j] = "專員達標準生產力占比";
				csvHeader[++j] = "專員達GOAL率對應獎勵金成數";
				
				csvHeader[++j] = "新、調任年月";			
				csvHeader[++j] = "新、調任分行任職月份數";
				csvHeader[++j] = "原分行代碼";
				csvHeader[++j] = "新分行成數";
				
				csvHeader[++j] = "原分行成數";			
				csvHeader[++j] = "業務獎勵金(過新、調任成數、達goal率獎金折數)";
				csvHeader[++j] = "非財務指標扣減金額";
				csvHeader[++j] = "獨立列示重大缺失扣減金額";
				
				csvHeader[++j] = "上期遞延扣減金額";			
				csvHeader[++j] = "實際扣減金額(本月+上期遞延)";
				csvHeader[++j] = "業務獎勵金100%";
				csvHeader[++j] = "業務獎勵金80%";
				csvHeader[++j] = "業務獎勵金應調整數";			
				csvHeader[++j] = "業務獎勵金實發";
				csvHeader[++j] = "業務獎勵金調整說明";
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
	* @param map
	* @return String
	*/
	private String checkIsNull(Map map, String key) 
	{
		if(StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key) != null)
		{
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
}

