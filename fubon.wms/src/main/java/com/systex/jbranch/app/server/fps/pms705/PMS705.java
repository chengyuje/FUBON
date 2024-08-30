package com.systex.jbranch.app.server.fps.pms705;
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

import com.systex.jbranch.app.server.fps.pms705.PMS705InputVO;
import com.systex.jbranch.app.server.fps.pms705.PMS705OutputVO;
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
 * Description : FCH理專財務非財務報表Controller<br>
 * Comments Name : PMS705.java<br>
 * Author : zzh<br>
 * Date :2016年11月10日 <br>
 * Version : 1.01 <br>
 * Editor : zzh<br>
 * Editor Date : 2016年11月10日<br>
 */
@Component("pms705")
@Scope("request")
public class PMS705 extends FubonWmsBizLogic
{
	public DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(PMS705.class);
	/**
	 * 角色獲取
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void getRole(Object body, IPrimitiveMap header)
			throws JBranchException {
		PMS705OutputVO return_VO = new PMS705OutputVO();
		PMS705InputVO inputVO = (PMS705InputVO) body;
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
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void version(Object body, IPrimitiveMap header)
			throws JBranchException {
		PMS705OutputVO return_VO = new PMS705OutputVO();
		PMS705InputVO inputVO = (PMS705InputVO) body;
		dam = this.getDataAccessManager();
		String role = inputVO.getRole();
		QueryConditionIF queryCondition = dam
				.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		try {
			sql.append(" select * from table(FN_EXAM_REPORT('"+inputVO.getsTime()+"','"+role+"','PMS705'))");
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
	 * 查詢
	 * @param body
	 * @param header
	 * @throws APException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void queryData(Object body, IPrimitiveMap header) throws APException
	{
		try
		{
			PMS705InputVO inputVO = (PMS705InputVO) body;
			PMS705OutputVO outputVO = new PMS705OutputVO();
			dam = this.getDataAccessManager();
			List roleList = new ArrayList();
			roleList.add(0, inputVO.getRole());
			QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sql = new StringBuffer();
			StringBuffer temp = new StringBuffer();
			if(roleList.get(0).equals("1"))
			{
				sql.append("  SELECT YEARMON,                            ");
				sql.append("         REPORT_TYPE,                        ");
				sql.append("         EXEC_DATE,                          ");
				sql.append("         OPEN_FLAG,                          ");
				sql.append("         REGION_CENTER_ID,                   ");
				sql.append("         REGION_CENTER_NAME,                 ");
				sql.append("         BRANCH_AREA_ID ,                    ");
				sql.append("         BRANCH_AREA_NAME,                   ");
				sql.append("         BRANCH_NBR,                         ");
				sql.append("         BRANCH_NAME,                        ");
				sql.append("         EMP_ID,                             ");
				sql.append("         EMP_NAME,                           ");
				sql.append("         AO_CODE,                            ");
				sql.append("         JOB_TITLE_ID ,                      ");
				sql.append("         AOVER_GAINS,                        ");
				sql.append("         AOVER_GAINS_GOAL_MUL,               ");
				sql.append("         AOVER_GAINS_GOAL,                   ");
				sql.append("         ROUND(FEE_RATE_REACHED_GOAL*100,2)||'%' AS FEE_RATE_REACHED_GOAL, ");
				sql.append("         ROUND(FEE_RATE_REACHED*100,2)||'%' AS FEE_RATE_REACHED,   ");
				sql.append("         SCORE_FEE_INCOME,                   ");
				sql.append("         DEP_AUM_IN,                         ");
				sql.append("         DEP_AUM_IN_GOAL,                    ");
				sql.append("         DEP_AUM_IN_YTD ,                    ");
				sql.append("         DEP_AUM_IN_YTD_GOAL,                ");
				sql.append("         ROUND(DEP_AUM_IN_YTD_RATE*100,2)||'%' AS DEP_AUM_IN_YTD_RATE,  ");
				sql.append("         DEP_AUM_SC,                         ");
				sql.append("         INC_INSU_AUM,                       ");
				sql.append("         INC_INSU_AUM_GOAL ,                 ");
				sql.append("         INC_INSU_AUM_YTD,                   ");
				sql.append("         INC_INSU_AUM_YTD_GOAL,              ");
				sql.append("         ROUND(INC_AUM_IN_YTD_RATE*100,2)||'%' AS INC_AUM_IN_YTD_RATE,  ");
				sql.append("         INC_AUM_SC ,                        ");
				sql.append("         AUM_SC,                             ");
				sql.append("         NEW_CUST_CL,                        ");
				sql.append("         NEW_CUST_CL_GOAL,                   ");
				sql.append("         NEW_CUST_CL_YTD,                    ");
				sql.append("         NEW_CUST_CL_YTD_GOAL ,              ");
				sql.append("         ROUND(NEW_CUST_CL_RATE_YTD*100,2)||'%' AS NEW_CUST_CL_RATE_YTD,  ");
				sql.append("         NEW_CUST_CL_SC,                     ");
				sql.append("         AUM_ICMT_ACH_SCORE,                 ");
				sql.append("         FIN_IND_SC ,                        ");
				sql.append("         ROUND(UN_FIN_IND_BONUS_RATE*100,2)||'%' AS UN_FIN_IND_BONUS_RATE,  ");
				sql.append("         ROUND(LACK_IND_RATE*100,2)||'%' AS LACK_IND_RATE,   ");
				sql.append("         LOST_CONTENT                        ");
				sql.append("    FROM TBPMS_FCH_FIN_RPT                   ");
			}else
			{
				sql.append("  SELECT YEARMON,                            ");
				sql.append("         REPORT_TYPE,                        ");
				sql.append("         EXEC_DATE,                          ");
				sql.append("         OPEN_FLAG,                          ");
				sql.append("         REGION_CENTER_ID,                   ");
				sql.append("         REGION_CENTER_NAME,                 ");
				sql.append("         BRANCH_AREA_ID,                     ");
				sql.append("         BRANCH_AREA_NAME ,                  ");
				sql.append("         BRANCH_NBR,                         ");
				sql.append("         BRANCH_NAME,                        ");
				sql.append("         EMP_ID,                             ");
				sql.append("         EMP_NAME,                           ");
				sql.append("         AO_CODE,                            ");
				sql.append("         JOB_TITLE_ID,                       ");
				sql.append("         AOVER_GAINS,                        ");
				sql.append("         AOVER_GAINS_GOAL,                   ");
				sql.append("         ROUND(FEE_RATE_REACHED*100,2)||'%' AS FEE_RATE_REACHED,    ");
				sql.append("         SCORE_FEE_INCOME,                   ");
				sql.append("         DEP_AUM_IN_YTD ,                    ");
				sql.append("         DEP_AUM_IN_YTD_GOAL,                ");
				sql.append("         ROUND(DEP_AUM_IN_YTD_RATE*100,2)||'%' AS DEP_AUM_IN_YTD_RATE,  ");
				sql.append("         DEP_AUM_SC,                         ");
				sql.append("         INC_INSU_AUM_YTD ,                  ");
				sql.append("         INC_INSU_AUM_YTD_GOAL,              ");
				sql.append("         ROUND(INC_AUM_IN_YTD_RATE*100,2)||'%' AS INC_AUM_IN_YTD_RATE, ");
				sql.append("         INC_AUM_SC,                         ");
				sql.append("         AUM_SC,                             ");
				sql.append("         NEW_CUST_CL_YTD,                    ");
				sql.append("         NEW_CUST_CL_YTD_GOAL,               ");
				sql.append("         ROUND(NEW_CUST_CL_RATE_YTD*100,2)||'%' AS NEW_CUST_CL_RATE_YTD, ");
				sql.append("         NEW_CUST_CL_SC ,                    ");
				sql.append("         AUM_ICMT_ACH_SCORE,                 ");
				sql.append("         FIN_IND_SC,                         ");
				sql.append("         ROUND(UN_FIN_IND_BONUS_RATE*100,2)||'%' AS UN_FIN_IND_BONUS_RATE, ");
				sql.append("         ROUND(LACK_IND_RATE*100,2)||'%' AS LACK_IND_RATE,   ");
				sql.append("         LOST_CONTENT                        ");
				sql.append("    FROM TBPMS_FCH_FIN_RPT                   ");
			}
			if(!StringUtils.isBlank(inputVO.getsTime()))
			{
				temp.append(" AND YEARMON = :yearMon                    ");
				condition.setObject("yearMon",inputVO.getsTime());
			}
			if(StringUtils.isNotBlank(inputVO.getRptVersion())){
				if(inputVO.getRptVersion().equals("上簽版")){
					temp.append(" AND REPORT_TYPE = '1' 			    ");
					
				}else{
					temp.append(" AND REPORT_TYPE = '0' 			    ");
					temp.append(" AND EXEC_DATE = :rptVersion 	        ");
					condition.setObject("rptVersion", inputVO.getRptVersion().substring(3,11));
				}
			}
			if(!StringUtils.isBlank(inputVO.getRegion_center_id()))
			{
				temp.append(" AND REGION_CENTER_ID = :regionCenterID    ");
				condition.setObject("regionCenterID",inputVO.getRegion_center_id());
			}
			if(!StringUtils.isBlank(inputVO.getBranch_area_id()))
			{
				temp.append(" AND BRANCH_AREA_ID = :branchAreaId        ");
				condition.setObject("branchAreaId",inputVO.getBranch_area_id());
			}
			if(!StringUtils.isBlank(inputVO.getBranch_nbr()))
			{
				temp.append(" AND BRANCH_NBR = :branchNbr               ");
				condition.setObject("branchNbr",inputVO.getBranch_nbr());
			}
			if(!StringUtils.isBlank(inputVO.getAo_code()))
			{
				temp.append(" AND AO_CODE = :empId                       ");
				condition.setObject("empId",inputVO.getAo_code());
			}
			if(temp != null &&  !"".equals(temp.toString()))
			{
				sql.append(temp.toString().replaceFirst(" AND ", " WHERE "));
			}
			sql.append("  order by REGION_CENTER_ID, BRANCH_AREA_ID, BRANCH_NBR, EMP_ID ");
			condition.setQueryString(sql.toString());
			ResultIF largeAgrList = dam.executePaging(condition, inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
			List<Map<String, Object>> list1 = dam.exeQuery(condition);
			int totalPage_i = largeAgrList.getTotalPage(); // 分頁用
			int totalRecord_i = largeAgrList.getTotalRecord(); // 分頁用
			outputVO.setLargeAgrList(largeAgrList); // data
			outputVO.setCsvList(list1);
			outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());// 當前頁次
			outputVO.setTotalPage(totalPage_i);// 總頁次
			outputVO.setTotalRecord(totalRecord_i);// 總筆數
			outputVO.setRoleList(roleList);
			this.sendRtnObject(outputVO);
		}
		catch (Exception e)
		{
			logger.error(String.format("發生錯誤:%s",StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}
	/**
	 * 匯出CSV
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void export(Object body, IPrimitiveMap header)
			throws JBranchException 
	{
		// 取得畫面資料
		PMS705OutputVO return_VO = (PMS705OutputVO) body;
		List<Map<String, Object>> list = return_VO.getCsvList();
		List<String> roleList = return_VO.getRoleList();
		try
		{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH:mm:ss");
			String fileName = "FCH理專財務非財務報表_" + sdf.format(new Date()) + ".csv"; 
			List listCSV = new ArrayList();
			String[] csvHeader = null;
			if(roleList.get(0).equals("1"))
			{
				for (Map<String, Object> map : list) 
				{
					String[] records = new String[41];
					int i = 0;
					records[i] = checkIsNull(map, "YEARMON");
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
					records[++i] = checkIsNull(map, "AOVER_GAINS");
					records[++i] = checkIsNull(map, "AOVER_GAINS_GOAL_MUL");
					records[++i] = checkIsNull(map, "AOVER_GAINS_GOAL");
					records[++i] = checkIsNull(map, "FEE_RATE_REACHED_GOAL");
					records[++i] = checkIsNull(map, "FEE_RATE_REACHED");
					records[++i] = checkIsNull(map, "SCORE_FEE_INCOME");
					records[++i] = checkIsNull(map, "DEP_AUM_IN");
					records[++i] = checkIsNull(map, "DEP_AUM_IN_GOAL");
					records[++i] = checkIsNull(map, "DEP_AUM_IN_YTD");
					records[++i] = checkIsNull(map, "DEP_AUM_IN_YTD_GOAL");
					records[++i] = checkIsNull(map, "DEP_AUM_IN_YTD_RATE");
					records[++i] = checkIsNull(map, "DEP_AUM_SC");
					records[++i] = checkIsNull(map, "INC_INSU_AUM");
					records[++i] = checkIsNull(map, "INC_INSU_AUM_GOAL");
					records[++i] = checkIsNull(map, "INC_INSU_AUM_YTD");
					records[++i] = checkIsNull(map, "INC_INSU_AUM_YTD_GOAL");
					records[++i] = checkIsNull(map, "INC_AUM_IN_YTD_RATE");
					records[++i] = checkIsNull(map, "INC_AUM_SC");
					records[++i] = checkIsNull(map, "AUM_SC");
					records[++i] = checkIsNull(map, "NEW_CUST_CL");
					records[++i] = checkIsNull(map, "NEW_CUST_CL_GOAL");
					records[++i] = checkIsNull(map, "NEW_CUST_CL_YTD");
					records[++i] = checkIsNull(map, "NEW_CUST_CL_YTD_GOAL");
					records[++i] = checkIsNull(map, "NEW_CUST_CL_RATE_YTD");
					records[++i] = checkIsNull(map, "NEW_CUST_CL_SC");
					records[++i] = checkIsNull(map, "AUM_ICMT_ACH_SCORE");
					records[++i] = checkIsNull(map, "FIN_IND_SC");
					records[++i] = checkIsNull(map, "UN_FIN_IND_BONUS_RATE");
					records[++i] = checkIsNull(map, "LACK_IND_RATE");
					records[++i] = checkIsNull(map, "LOST_CONTENT");
					listCSV.add(records);
				}
				csvHeader = new String[41];
				int j = 0;
				csvHeader[j] = "資料年月";
				csvHeader[++j] = "業務處代碼";
				csvHeader[++j] = "業務處名稱";
				csvHeader[++j] = "營運區代碼";
				csvHeader[++j] = "營運區名稱";
				csvHeader[++j] = "分行代碼";
				csvHeader[++j] = "分行別";
				csvHeader[++j] = "員工編號";
				csvHeader[++j] = "專員姓名";
				csvHeader[++j] = "AO_CODE";
				csvHeader[++j] = "專員職級";
				csvHeader[++j] = "財務指標-手續費收入-加碼後計績收益";
				csvHeader[++j] = "財務指標-手續費收入-目標(本薪倍數)";
				csvHeader[++j] = "財務指標-手續費收入-目標";
				csvHeader[++j] = "財務指標-手續費收入-達成率(本薪倍數)";
				csvHeader[++j] = "財務指標-手續費收入-達成率";
				csvHeader[++j] = "財務指標-手續費收入-得分";
				csvHeader[++j] = "財務指標-存款AUM（不含台定)-當月增量(含台定減量加回)";
				csvHeader[++j] = "財務指標-存款AUM（不含台定)-當月增量目標";
				csvHeader[++j] = "財務指標-存款AUM（不含台定)-增量YTD(含台定減量加回)";
				csvHeader[++j] = "財務指標-存款AUM（不含台定)-增量目標YTD";
				csvHeader[++j] = "財務指標-存款AUM（不含台定)-達成率YTD";
				csvHeader[++j] = "財務指標-存款AUM（不含台定)-得分";
				csvHeader[++j] = "財務指標-投保AUM-當月增量";
				csvHeader[++j] = "財務指標-投保AUM-當月增量目標";
				csvHeader[++j] = "財務指標-投保AUM-增量YTD";
				csvHeader[++j] = "財務指標-投保AUM-增量目標YTD";
				csvHeader[++j] = "財務指標-投保AUM-達成率YTD";
				csvHeader[++j] = "財務指標-投保AUM-得分";
				csvHeader[++j] = "AUM得分";
				csvHeader[++j] = "新客戶數-當月增量";
				csvHeader[++j] = "新客戶數-當月目標";
				csvHeader[++j] = "新客戶數-增量YTD";
				csvHeader[++j] = "新客戶數-增量目標YTD";
				csvHeader[++j] = "新客戶數-達成率YTD";
				csvHeader[++j] = "新客戶數-得分";
				csvHeader[++j] = "AUM+客戶數得分";
				csvHeader[++j] = "財務指標得分";
				csvHeader[++j] = "非財務指標扣減百分比%";
				csvHeader[++j] = "獨立列示重大缺失扣減百分比%";
				csvHeader[++j] = "缺失內容";
			}else
			{
				for (Map<String, Object> map : list) 
				{
					String[] records = new String[34];
					int i = 0;
					records[i] = checkIsNull(map, "YEARMON");
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
					records[++i] = checkIsNull(map, "AOVER_GAINS");
					records[++i] = checkIsNull(map, "AOVER_GAINS_GOAL");
					records[++i] = checkIsNull(map, "FEE_RATE_REACHED");
					records[++i] = checkIsNull(map, "SCORE_FEE_INCOME");
					records[++i] = checkIsNull(map, "DEP_AUM_IN_YTD");
					records[++i] = checkIsNull(map, "DEP_AUM_IN_YTD_GOAL");
					records[++i] = checkIsNull(map, "DEP_AUM_IN_YTD_RATE");
					records[++i] = checkIsNull(map, "DEP_AUM_SC");
					records[++i] = checkIsNull(map, "INC_INSU_AUM_YTD");
					records[++i] = checkIsNull(map, "INC_INSU_AUM_YTD_GOAL");
					records[++i] = checkIsNull(map, "INC_AUM_IN_YTD_RATE");
					records[++i] = checkIsNull(map, "INC_AUM_SC");
					records[++i] = checkIsNull(map, "AUM_SC");
					records[++i] = checkIsNull(map, "NEW_CUST_CL_YTD");
					records[++i] = checkIsNull(map, "NEW_CUST_CL_YTD_GOAL");
					records[++i] = checkIsNull(map, "NEW_CUST_CL_RATE_YTD");
					records[++i] = checkIsNull(map, "NEW_CUST_CL_SC");
					records[++i] = checkIsNull(map, "AUM_ICMT_ACH_SCORE");
					records[++i] = checkIsNull(map, "FIN_IND_SC");
					records[++i] = checkIsNull(map, "UN_FIN_IND_BONUS_RATE");
					records[++i] = checkIsNull(map, "LACK_IND_RATE");
					records[++i] = checkIsNull(map, "LOST_CONTENT");
					listCSV.add(records);
				}
				csvHeader = new String[34];
				int j = 0;
				csvHeader[j] = "資料年月";
				csvHeader[++j] = "業務處代碼";
				csvHeader[++j] = "業務處名稱";
				csvHeader[++j] = "營運區代碼";
				csvHeader[++j] = "營運區名稱";
				csvHeader[++j] = "分行代碼";
				csvHeader[++j] = "分行別";
				csvHeader[++j] = "員工編號";
				csvHeader[++j] = "專員姓名";
				csvHeader[++j] = "AO_CODE";
				csvHeader[++j] = "專員職級";
				csvHeader[++j] = "財務指標-手續費收入-加碼後計績收益";
				csvHeader[++j] = "財務指標-手續費收入-目標";
				csvHeader[++j] = "財務指標-手續費收入-達成率";
				csvHeader[++j] = "財務指標-手續費收入-得分";
				csvHeader[++j] = "財務指標-存款AUM（不含台定)-增量YTD(含台定減量加回)";
				csvHeader[++j] = "財務指標-存款AUM（不含台定)-增量目標YTD";
				csvHeader[++j] = "財務指標-存款AUM（不含台定)-達成率YTD";
				csvHeader[++j] = "財務指標-存款AUM（不含台定)-得分";
				csvHeader[++j] = "財務指標-投保AUM-增量YTD";
				csvHeader[++j] = "財務指標-投保AUM-增量目標YTD";
				csvHeader[++j] = "財務指標-投保AUM-達成率YTD";
				csvHeader[++j] = "財務指標-投保AUM-得分";
				csvHeader[++j] = "AUM得分";
				csvHeader[++j] = "新客戶數-增量YTD";
				csvHeader[++j] = "新客戶數-增量目標YTD";
				csvHeader[++j] = "新客戶數-達成率YTD";
				csvHeader[++j] = "新客戶數-得分";
				csvHeader[++j] = "AUM+客戶數得分";
				csvHeader[++j] = "財務指標得分";
				csvHeader[++j] = "非財務指標扣減百分比%";
				csvHeader[++j] = "獨立列示重大缺失扣減百分比%";
				csvHeader[++j] = "缺失內容";
			}
			CSVUtil csv = new CSVUtil();
			csv.setHeader(csvHeader);
			csv.addRecordList(listCSV);
			String url = csv.generateCSV();
			notifyClientToDownloadFile(url, fileName);
		} catch (Exception e) 
		{
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}
	/**
	 * 返回map中value为null则返回空字符串
	 * @param map
	 * @param key
	 * @return
	 */
	private String checkIsNull(Map map, String key) {
		if(StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key) != null){
			return String.valueOf(map.get(key));
		}else{
			return "";
		}
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