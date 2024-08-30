package com.systex.jbranch.app.server.fps.pms216;
import java.sql.Timestamp;
import java.text.DateFormat;
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

import com.systex.jbranch.app.server.fps.pms216.PMS216InputVO;
import com.systex.jbranch.app.server.fps.pms216.PMS216OutputVO;
import com.systex.jbranch.app.server.fps.pms401.PMS401InputVO;
import com.systex.jbranch.app.server.fps.pms401.PMS401OutputVO;
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
 * Copy Right Information : Forms Syntron <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description : FC獎勵金查詢Controller<br>
 * Comments Name : PMS216.java<br>
 * Author : zzh<br>
 * Date :2016年11月10日 <br>
 * Version : 1.01 <br>
 * Editor : zzh<br>
 * Editor Date : 2016年11月10日<br>
 */
@Component("pms216")
@Scope("request")
public class PMS216 extends FubonWmsBizLogic
{
	public DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(PMS216.class);
	/**
	 * 角色獲取
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void getRole(Object body, IPrimitiveMap header)
			throws JBranchException {
		PMS216OutputVO return_VO = new PMS216OutputVO();
		PMS216InputVO inputVO = (PMS216InputVO) body;
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
	
	/**取得可視範圍**/
	public void getOrgInfo(Object body, IPrimitiveMap header) throws JBranchException, ParseException {	
		PMS216InputVO inputVO = (PMS216InputVO) body;
		PMS216OutputVO outputVO = new PMS216OutputVO();
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
//		condition.setObject("purview_type", "OTHER");   //非業績報表
//		condition.setObject("purview_type", "P_PERF");  //個人業績報表
		condition.setObject("purview_type", "ORG_PERF"); //轄下人員業績
		
		if(inputVO.getYearMon() != null){				
			condition.setObject("e_dt", getMonthLastDate(inputVO.getYearMon()));
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
	 * 版本下拉選單
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void version(Object body, IPrimitiveMap header)
			throws JBranchException 
	{
		PMS216OutputVO return_VO = new PMS216OutputVO();
		PMS216InputVO inputVO = (PMS216InputVO) body;
		dam = this.getDataAccessManager();
		String role = inputVO.getRole();
		QueryConditionIF queryCondition = dam
				.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		try {
			sql.append(" select * from table(FN_EXAM_REPORT('"+inputVO.getYearMon()+"','"+role+"','PMS216'))");
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
			PMS216InputVO inputVO = (PMS216InputVO) body;
			PMS216OutputVO outputVO = new PMS216OutputVO();
			dam = this.getDataAccessManager();
			List roleList = new ArrayList();
			roleList.add(0, inputVO.getRole());
			QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sql = new StringBuffer();
			StringBuffer temp = new StringBuffer();
			if(roleList.get(0).equals("1"))
			{
				sql.append("  SELECT YEARMON,                                     ");
				sql.append("         EXEC_DATE,						       	      ");
				sql.append("         OPEN_FLAG,						       	      ");
				sql.append("         REPORT_TYPE,							      ");
				sql.append("         REGION_CENTER_ID,                            ");
				sql.append("         REGION_CENTER_NAME,                          ");
				sql.append("         BRANCH_AREA_ID,                              ");
				sql.append("         BRANCH_AREA_NAME ,                           ");
				sql.append("         BRANCH_NBR,                                  ");
				sql.append("         BRANCH_NAME,                                 ");
				sql.append("         AO_CODE,                                     ");
				sql.append("         EMP_ID,                                      ");
				sql.append("         EMP_NAME,                                    ");
				sql.append("         JOB_TITLE_ID,                                ");
				sql.append("         FC_DEPOSIT ,                                 ");
				sql.append("         FC_LFUND_SIN,                                ");
				sql.append("         FC_LFUND_SMA,                                ");
				sql.append("         FC_LFUND_STR,                                ");
				sql.append("         FC_OLUND_SIN,                                ");
				sql.append("         FC_OLUND_SMA,                                ");
				sql.append("         FC_OLUND_STR ,                               ");
				sql.append("         FC_FUND_MNG_FEE,                             ");
				sql.append("         FC_DCI,                                      ");
				sql.append("         FC_SI,                                       ");
				sql.append("         FC_ETF,                                      ");
				sql.append("         FC_TPCC,                                     ");
				sql.append("         FC_OSEA,                                     ");
				sql.append("         FC_SN,                                       ");
				sql.append("         FC_INS_SG,                                   ");
				sql.append("         FC_INS_SQ_FIR ,                              ");
				sql.append("         FC_INS_SQ_CON,                               ");
				sql.append("         FC_TRUST_FEE,                                ");
				sql.append("         FC_GOLD,                                     ");
				sql.append("         COMBINED,                                    ");
				sql.append("         REAL_INCOME,                                 ");
				sql.append("         STAND_GOAL,                                  ");
				sql.append("         PRFT_GOAL,                                   ");
				sql.append("         FC_FC_TATOL ,                                ");
				sql.append("         FC_INDCT,                                    ");
				sql.append("         FIN_DEDUC_AMT,                               ");
				sql.append("         UN_FIN_DEDUCAMT,                             ");
				sql.append("         LACK_IND_AMT,                                ");
				sql.append("         NEW_REF_DEDUCT_AMT ,                         ");
				sql.append("         PREVIOUS_DEDUC_AMT,                          ");
				sql.append("         REAL_DEDUC_AMT,                              ");
				sql.append("         FC_BONUS_CNR100,                             ");
				sql.append("         FC_BONUS_CNR80,                              ");
				sql.append("         FC_BONUS_CNR20,                              ");
				sql.append("         FC_BONUS_CNR ,                               ");
				sql.append("         FC_BONUS_CNRADJ,                             ");
				sql.append("         FC_BONUS_CNR_REAL,                           ");
				sql.append("         RANKING,                                     ");
				sql.append("         THRESHOLD_NUM_100,                           ");
				sql.append("         FC_BONUS_CNRADJ_NOTE,                        ");
				sql.append("         TO_CHAR(LEAVE,'YYYYMMDD') AS LEAVE         ");
				sql.append("    FROM TBPMS_FC_BONUS_RPT                           ");
			}else
			{
				sql.append("  SELECT YEARMON,                                     ");
				sql.append("         EXEC_DATE,                                   ");
				sql.append("         OPEN_FLAG,                                   ");
				sql.append("         REPORT_TYPE,                                 ");
				sql.append("         REGION_CENTER_ID,                            ");
				sql.append("         REGION_CENTER_NAME,                          ");
				sql.append("         BRANCH_AREA_ID,                              ");
				sql.append("         BRANCH_AREA_NAME ,                           ");
				sql.append("         BRANCH_NBR,                                  ");
				sql.append("         BRANCH_NAME,                                 ");
				sql.append("         EMP_ID,                                      ");
				sql.append("         EMP_NAME,                                    ");
				sql.append("         AO_CODE,                                     ");
				sql.append("         JOB_TITLE_ID,                                ");
				sql.append("         FC_DEPOSIT ,                                 ");
				sql.append("         FC_LFUND_SIN,                                ");
				sql.append("         FC_LFUND_SMA,                                ");
				sql.append("         FC_LFUND_STR,                                ");
				sql.append("         FC_OLUND_SIN,                                ");
				sql.append("         FC_OLUND_SMA,                                ");
				sql.append("         FC_OLUND_STR ,                               ");
				sql.append("         FC_FUND_MNG_FEE,                             ");
				sql.append("         FC_DCI,                                      ");
				sql.append("         FC_SI,                                       ");
				sql.append("         FC_ETF,                                      ");
				sql.append("         FC_TPCC ,                                    ");
				sql.append("         FC_OSEA,                                     ");
				sql.append("         FC_SN,                                       ");
				sql.append("         FC_INS_SG ,                                  ");
				sql.append("         FC_INS_SQ_FIR,                               ");
				sql.append("         FC_INS_SQ_CON,                               ");
				sql.append("         FC_TRUST_FEE,                                ");
				sql.append("         FC_GOLD,                                     ");
				sql.append("         COMBINED,                                    ");
				sql.append("         FC_INDCT,                                    ");
				sql.append("         FIN_DEDUC_AMT ,                              ");
				sql.append("         UN_FIN_DEDUCAMT,                             ");
				sql.append("         LACK_IND_AMT,                                ");
				sql.append("         NEW_REF_DEDUCT_AMT,                          ");
				sql.append("         PREVIOUS_DEDUC_AMT,                          ");
				sql.append("         REAL_DEDUC_AMT ,                             ");
				sql.append("         FC_BONUS_CNR100,                             ");
				sql.append("         FC_BONUS_CNR80,                              ");
				sql.append("         FC_BONUS_CNRADJ,                             ");
				sql.append("         FC_BONUS_CNR_REAL ,                          ");
				sql.append("         FC_BONUS_CNRADJ_NOTE                         ");
				sql.append("    FROM TBPMS_FC_BONUS_RPT                           ");
			}
			if(!StringUtils.isBlank(inputVO.getYearMon()))
			{
				temp.append("  AND YEARMON = :yearMon                             ");
				condition.setObject("yearMon",inputVO.getYearMon());
			}
			if(StringUtils.isNotBlank(inputVO.getRptVersion())){
				if(inputVO.getRptVersion().equals("上簽版")){
					temp.append(" AND REPORT_TYPE = '1' 					      ");
				}else{
					temp.append(" AND REPORT_TYPE = '0' 					      ");
					temp.append(" AND EXEC_DATE = :rptVersion 	                  ");
					condition.setObject("rptVersion", inputVO.getRptVersion().substring(3,11));
				}
			}
			if(!StringUtils.isBlank(inputVO.getRegionCenterId()))
			{
				temp.append("   AND REGION_CENTER_ID = :regionCenterID            ");
				condition.setObject("regionCenterID",inputVO.getRegionCenterId());
			}/*else{
				StringBuffer regionListStr = new StringBuffer();
				regionListStr.append("('");
				for(int i=0; i<inputVO.getRegionList().size(); i++){
					if(i != inputVO.getRegionList().size()-1)
						regionListStr.append(inputVO.getRegionList().get(i)+"','");
					else
						regionListStr.append(inputVO.getRegionList().get(i)+"')");
				}
				temp.append("   AND REGION_CENTER_ID  in  " + regionListStr.toString());
			}*/
			if(!StringUtils.isBlank(inputVO.getBranchAreaId()))
			{
				temp.append("   AND BRANCH_AREA_ID = :branchAreaId                ");
				condition.setObject("branchAreaId",inputVO.getBranchAreaId());
			}
			if(!StringUtils.isBlank(inputVO.getBranchNbr()))
			{
				temp.append("   AND BRANCH_NBR = :branchNbr                       ");
				condition.setObject("branchNbr",inputVO.getBranchNbr());
			}
			if(!StringUtils.isBlank(inputVO.getAoCode()))
			{
				temp.append("   AND AO_CODE = :AO_CODE                               ");
				condition.setObject("AO_CODE",inputVO.getAoCode());
			}
			if(temp != null &&  !"".equals(temp.toString()))
			{
				sql.append(temp.toString().replaceFirst(" AND ", " WHERE "));
			}
			sql.append("  order by BRANCH_AREA_ID, BRANCH_NBR,AO_CODE ");
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
		PMS216OutputVO return_VO = (PMS216OutputVO) body;
		List<Map<String, Object>> list = return_VO.getCsvList();
		List<String> roleList = return_VO.getRoleList();
		try{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH:mm:ss");
			String fileName = "FC獎勵金查詢_" + sdf.format(new Date()) + ".csv"; 
			List listCSV = new ArrayList();
			String[] csvHeader = null;
			if(roleList.get(0).equals("1"))
			{
				for (Map<String, Object> map : list)
				{
					String[] records = new String[52];
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
					records[++i] = checkIsNull(map, "FC_DEPOSIT");
					records[++i] = checkIsNull(map, "FC_LFUND_SIN");
					records[++i] = checkIsNull(map, "FC_LFUND_SMA");
					records[++i] = checkIsNull(map, "FC_LFUND_STR");
					records[++i] = checkIsNull(map, "FC_OLUND_SIN");
					records[++i] = checkIsNull(map, "FC_OLUND_SMA");
					records[++i] = checkIsNull(map, "FC_OLUND_STR");
					records[++i] = checkIsNull(map, "FC_FUND_MNG_FEE");
					records[++i] = checkIsNull(map, "FC_DCI");
					records[++i] = checkIsNull(map, "FC_SI");
					records[++i] = checkIsNull(map, "FC_ETF");
					records[++i] = checkIsNull(map, "FC_TPCC");
					records[++i] = checkIsNull(map, "FC_OSEA");
					records[++i] = checkIsNull(map, "FC_SN");
					records[++i] = checkIsNull(map, "FC_INS_SG");
					records[++i] = checkIsNull(map, "FC_INS_SQ_FIR");
					records[++i] = checkIsNull(map, "FC_INS_SQ_CON");
					records[++i] = checkIsNull(map, "FC_TRUST_FEE");
					records[++i] = checkIsNull(map, "FC_GOLD");
					records[++i] = checkIsNull(map, "COMBINED");
					records[++i] = checkIsNull(map, "REAL_INCOME");
					records[++i] = checkIsNull(map, "STAND_GOAL");
					records[++i] = checkIsNull(map, "PRFT_GOAL");
					records[++i] = checkIsNull(map, "FC_FC_TATOL");
					records[++i] = checkIsNull(map, "FC_INDCT");
					records[++i] = checkIsNull(map, "FIN_DEDUC_AMT");
					records[++i] = checkIsNull(map, "UN_FIN_DEDUCAMT");
					records[++i] = checkIsNull(map, "LACK_IND_AMT");
					records[++i] = checkIsNull(map, "NEW_REF_DEDUCT_AMT");
					records[++i] = checkIsNull(map, "PREVIOUS_DEDUC_AMT");
					records[++i] = checkIsNull(map, "REAL_DEDUC_AMT");
					records[++i] = checkIsNull(map, "FC_BONUS_CNR100");
					records[++i] = checkIsNull(map, "FC_BONUS_CNR80");
					records[++i] = checkIsNull(map, "FC_BONUS_CNR20");
					records[++i] = checkIsNull(map, "FC_BONUS_CNR");
					records[++i] = checkIsNull(map, "FC_BONUS_CNRADJ");
					records[++i] = checkIsNull(map, "FC_BONUS_CNR_REAL");
					records[++i] = checkIsNull(map, "RANKING");
					records[++i] = checkIsNull(map, "THRESHOLD_NUM_100");
					records[++i] = checkIsNull(map, "FC_BONUS_CNRADJ_NOTE");
					records[++i] = checkIsNull(map, "LEAVE");
					listCSV.add(records);
				}
				csvHeader = new String[52];
				int j = 0;
				csvHeader[j] = "資料統計月份";
				csvHeader[++j] = "業務處代碼";
				csvHeader[++j] = "業務處名稱";
				csvHeader[++j] = "營運區代碼";
				csvHeader[++j] = "營運區名稱";
				csvHeader[++j] = "分行ID";
				csvHeader[++j] = "分行名稱";
				csvHeader[++j] = "員工編號";
				csvHeader[++j] = "理專姓名";
				csvHeader[++j] = "AOCODE";
				csvHeader[++j] = "專員職級";
				csvHeader[++j] = "計績收益(加碼後)-存匯類商品";
				csvHeader[++j] = "計績收益(加碼後)-國內基金-單筆";
				csvHeader[++j] = "計績收益(加碼後)-國內基金-小額";
				csvHeader[++j] = "計績收益(加碼後)-國內基金短Trade(減項)";
				csvHeader[++j] = "計績收益(加碼後)-國外基金-單筆(含轉換)";
				csvHeader[++j] = "計績收益(加碼後)-國外基金-小額";
				csvHeader[++j] = "計績收益(加碼後)-國外基金短Trade(減項)";
				csvHeader[++j] = "計績收益(加碼後)-基金管理費";
				csvHeader[++j] = "計績收益(加碼後)-DCI";
				csvHeader[++j] = "計績收益(加碼後)-SI";
				csvHeader[++j] = "計績收益(加碼後)-ETF";
				csvHeader[++j] = "計績收益(加碼後)-海外股票";
				csvHeader[++j] = "計績收益(加碼後)-海外債券";
				csvHeader[++j] = "計績收益(加碼後)-SN";
				csvHeader[++j] = "計績收益(加碼後)-保險-躉繳";
				csvHeader[++j] = "計績收益(加碼後)-保險-分期繳首年";
				csvHeader[++j] = "計績收益(加碼後)-保險-分期繳續年";
				csvHeader[++j] = "計績收益(加碼後)-信託商品";
				csvHeader[++j] = "計績收益(加碼後)-黃金存摺";
				csvHeader[++j] = "計績收益(加碼後)-合計";
				csvHeader[++j] = "實際收益";
				csvHeader[++j] = "標準生產力目標";
				csvHeader[++j] = "收益目標";
				csvHeader[++j] = "加碼後計績收益達成率";
				csvHeader[++j] = "個人淨貢獻";
				csvHeader[++j] = "財務指標扣減金額";
				csvHeader[++j] = "非財務指標扣減金額";
				csvHeader[++j] = "獨立列示重大缺失扣減金額";
				csvHeader[++j] = "新戶轉介扣減金額";
				csvHeader[++j] = "上期遞延扣減金額";
				csvHeader[++j] = "實際扣減金額(本月+上期遞延)";
				csvHeader[++j] = "業務獎勵金100%";
				csvHeader[++j] = "業務獎勵金80%";
				csvHeader[++j] = "業務獎勵金20%";
				csvHeader[++j] = "獎金率";
				csvHeader[++j] = "業務獎勵金應調整數";
				csvHeader[++j] = "業務獎勵金實發";
				csvHeader[++j] = "名次(依據業務獎勵金實發)";
				csvHeader[++j] = "達100%門檻人數";
				csvHeader[++j] = "業務獎勵金調整說明";
				csvHeader[++j] = "離職日";
			}else
			{
				for (Map<String, Object> map : list)
				{
					String[] records = new String[42];
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
					records[++i] = checkIsNull(map, "FC_DEPOSIT");
					records[++i] = checkIsNull(map, "FC_LFUND_SIN");
					records[++i] = checkIsNull(map, "FC_LFUND_SMA");
					records[++i] = checkIsNull(map, "FC_LFUND_STR");
					records[++i] = checkIsNull(map, "FC_OLUND_SIN");
					records[++i] = checkIsNull(map, "FC_OLUND_SMA");
					records[++i] = checkIsNull(map, "FC_OLUND_STR");
					records[++i] = checkIsNull(map, "FC_FUND_MNG_FEE");
					records[++i] = checkIsNull(map, "FC_DCI");
					records[++i] = checkIsNull(map, "FC_SI");
					records[++i] = checkIsNull(map, "FC_ETF");
					records[++i] = checkIsNull(map, "FC_TPCC");
					records[++i] = checkIsNull(map, "FC_OSEA");
					records[++i] = checkIsNull(map, "FC_SN");
					records[++i] = checkIsNull(map, "FC_INS_SG");
					records[++i] = checkIsNull(map, "FC_INS_SQ_FIR");
					records[++i] = checkIsNull(map, "FC_INS_SQ_CON");
					records[++i] = checkIsNull(map, "FC_TRUST_FEE");
					records[++i] = checkIsNull(map, "FC_GOLD");
					records[++i] = checkIsNull(map, "COMBINED");
					records[++i] = checkIsNull(map, "FC_INDCT");
					records[++i] = checkIsNull(map, "FIN_DEDUC_AMT");
					records[++i] = checkIsNull(map, "UN_FIN_DEDUCAMT");
					records[++i] = checkIsNull(map, "LACK_IND_AMT");
					records[++i] = checkIsNull(map, "NEW_REF_DEDUCT_AMT");
					records[++i] = checkIsNull(map, "PREVIOUS_DEDUC_AMT");
					records[++i] = checkIsNull(map, "REAL_DEDUC_AMT");
					records[++i] = checkIsNull(map, "FC_BONUS_CNR100");
					records[++i] = checkIsNull(map, "FC_BONUS_CNR80");
					records[++i] = checkIsNull(map, "FC_BONUS_CNRADJ");
					records[++i] = checkIsNull(map, "FC_BONUS_CNR_REAL");
					records[++i] = checkIsNull(map, "FC_BONUS_CNRADJ_NOTE");
					listCSV.add(records);
				}
				csvHeader = new String[42];
				int j = 0;
				csvHeader[j] = "資料統計月份";
				csvHeader[++j] = "業務處代碼";
				csvHeader[++j] = "業務處名稱";
				csvHeader[++j] = "營運區代碼";
				csvHeader[++j] = "營運區名稱";
				csvHeader[++j] = "分行ID";
				csvHeader[++j] = "分行名稱";
				csvHeader[++j] = "員工編號";
				csvHeader[++j] = "理專姓名";
				csvHeader[++j] = "AOCODE";
				csvHeader[++j] = "專員職級";
				csvHeader[++j] = "計績收益(加碼後)-存匯類商品";
				csvHeader[++j] = "計績收益(加碼後)-國內基金-單筆";
				csvHeader[++j] = "計績收益(加碼後)-國內基金-小額";
				csvHeader[++j] = "計績收益(加碼後)-國內基金短Trade(減項)";
				csvHeader[++j] = "計績收益(加碼後)-國外基金-單筆(含轉換)";
				csvHeader[++j] = "計績收益(加碼後)-國外基金-小額";
				csvHeader[++j] = "計績收益(加碼後)-國外基金短Trade(減項)";
				csvHeader[++j] = "計績收益(加碼後)-基金管理費";
				csvHeader[++j] = "計績收益(加碼後)-DCI";
				csvHeader[++j] = "計績收益(加碼後)-SI";
				csvHeader[++j] = "計績收益(加碼後)-ETF";
				csvHeader[++j] = "計績收益(加碼後)-海外股票";
				csvHeader[++j] = "計績收益(加碼後)-海外債券";
				csvHeader[++j] = "計績收益(加碼後)-SN";
				csvHeader[++j] = "計績收益(加碼後)-保險-躉繳";
				csvHeader[++j] = "計績收益(加碼後)-保險-分期繳首年";
				csvHeader[++j] = "計績收益(加碼後)-保險-分期繳續年";
				csvHeader[++j] = "計績收益(加碼後)-信託商品";
				csvHeader[++j] = "計績收益(加碼後)-黃金存摺";
				csvHeader[++j] = "計績收益(加碼後)-合計";
				csvHeader[++j] = "個人淨貢獻";
				csvHeader[++j] = "財務指標扣減金額";
				csvHeader[++j] = "非財務指標扣減金額";
				csvHeader[++j] = "獨立列示重大缺失扣減金額";
				csvHeader[++j] = "新戶轉介扣減金額";
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
			notifyClientToDownloadFile(url, fileName);
		} catch (Exception e) {
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
	@SuppressWarnings("rawtypes")
	private String checkIsNull(Map map, String key) 
	{
		if (StringUtils.isNotBlank(String.valueOf(map.get(key)))&& map.get(key) != null) 
		{
			return String.valueOf(map.get(key));
		} else 
		{
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
