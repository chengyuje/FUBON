package com.systex.jbranch.app.server.fps.pms222;
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
 * Description : 分行收益報表Controller<br>
 * Comments Name : PMS222.java<br>
 * Author :zhouyiqiong<br>
 * Date :2016年11月12日 <br>
 * Version : 1.01 <br>
 * Editor : zhouyiqiong<br>
 * Editor Date : 2016年11月12日<br>
 */
@Component("pms222")
@Scope("request")
public class PMS222 extends FubonWmsBizLogic
{
	private DataAccessManager dam = null;
	private PMS222InputVO inputVO = null;
	private Logger logger = LoggerFactory.getLogger(PMS222.class);
	/**
	 * 角色獲取
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void getRole(Object body, IPrimitiveMap header)
			throws JBranchException {
		PMS222OutputVO return_VO = new PMS222OutputVO();
		PMS222InputVO inputVO = (PMS222InputVO) body;
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
		}  catch (Exception e) {
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
		PMS222OutputVO return_VO = new PMS222OutputVO();
		PMS222InputVO inputVO = (PMS222InputVO) body;
		dam = this.getDataAccessManager();
		String role = inputVO.getRole();
		QueryConditionIF queryCondition = dam
				.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		try {
			sql.append(" select * from table(FN_EXAM_REPORT('"+inputVO.getsTime()+"','"+role+"','PMS222'))");
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
		PMS222InputVO inputVO = (PMS222InputVO) body;
		PMS222OutputVO outputVO = new PMS222OutputVO();
		dam = this.getDataAccessManager();
		List roleList = new ArrayList();
		roleList.add(0, inputVO.getRole());
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		try
		{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			sql.append("  SELECT YEARMON,							           ");
			sql.append("  	   EXEC_DATE,                                      ");
			sql.append("  	   OPEN_FLAG,                                      ");
			sql.append("  	   REPORT_TYPE,                                    ");
			sql.append("  	   REGION_CENTER_ID,                               ");
			sql.append("  	   REGION_CENTER_NAME,                             ");
			sql.append("  	   BRANCH_AREA_ID,                                 ");
			sql.append("  	   BRANCH_AREA_NAME,                               ");
			sql.append("  	   BRANCH_NBR,                                     ");
			sql.append("  	   BRANCH_NAME,                                    ");
			sql.append("  	   PRFT_AMT,                                       ");
			sql.append("  	   PRFT_GOAL,                                      ");
			sql.append("  	   PRFT_GOAL_ACH,                                  ");
			sql.append("  	   INV_DAY_RCEV,                                   ");
			sql.append("  	   RCEV_FUND,                                      ");
			sql.append("  	   NUM_MNGR_FEE,                                   ");
			sql.append("  	   EXCHG_PL,                                       ");
			sql.append("  	   INV_PL,                                         ");
			sql.append("  	   INS_NEW,                                        ");
			sql.append("  	   RCEV_INS_RENEW,                                 ");
			sql.append("  	   INS_PL                                          ");
			sql.append("  FROM TBPMS_BARNCH_PROFIT_RPT                         ");
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
			if(StringUtils.isNotBlank(inputVO.getRptVersion())){
				if(inputVO.getRptVersion().equals("上簽版")){
					sql.append(" AND REPORT_TYPE = '1' 					    ");
				}else{
					sql.append(" AND REPORT_TYPE = '0' 				        ");
					sql.append(" AND EXEC_DATE = :rptVersion 	            ");
					condition.setObject("rptVersion", inputVO.getRptVersion().substring(3,11));
				}
			}
			if (!StringUtils.isBlank(inputVO.getsTime())) {
				sql.append(" AND TRIM(YEARMON) = :sTime                        ");
				condition.setObject("sTime", inputVO.getsTime().trim());
			}
			sql.append("  ORDER BY REGION_CENTER_ID,decode(BRANCH_AREA_ID,'-','z',BRANCH_AREA_ID) ,decode(BRANCH_NBR,'-','z',BRANCH_NBR)     ");
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
		PMS222OutputVO return_VO2 = (PMS222OutputVO) body;
		List<Map<String, Object>> list = return_VO2.getCsvList();
		if(list.size() > 0)
		{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH:mm:ss");
			String fileName = "分行收益報表_" + sdf.format(new Date()) + ".csv"; 
			List listCSV =  new ArrayList();
			for(Map<String, Object> map : list){
				String[] records = new String[18];
				int i = 0;
				records[i] = checkIsNull(map, "YEARMON");                      //資料年月
				records[++i] = checkIsNull(map, "REGION_CENTER_ID");         //區域中心
				records[++i] = checkIsNull(map, "REGION_CENTER_NAME");         //區域中心
				records[++i] = checkIsNull(map, "BRANCH_AREA_ID");           //營運區
				records[++i] = checkIsNull(map, "BRANCH_AREA_NAME");           //營運區
				records[++i] = checkIsNull(map, "BRANCH_NBR");                 //分別代碼
				records[++i] = checkIsNull(map, "BRANCH_NAME");                //分行別
				records[++i] = checkIsNull(map, "PRFT_AMT");                   //分行收益
				records[++i] = checkIsNull(map, "PRFT_GOAL");                  //分行收益目標
				records[++i] = checkIsNull(map, "PRFT_GOAL_ACH");              //收益達成率
				records[++i] = checkIsNull(map, "INV_DAY_RCEV");               //投資日收
				records[++i] = checkIsNull(map, "RCEV_FUND");                  //持收_基金小額
				records[++i] = checkIsNull(map, "NUM_MNGR_FEE");               //持收_管理費
				records[++i] = checkIsNull(map, "EXCHG_PL");                   //兌換損益
				records[++i] = checkIsNull(map, "INV_PL");                     //投資收益
				records[++i] = checkIsNull(map, "INS_NEW");                    //保險首期
				records[++i] = checkIsNull(map, "RCEV_INS_RENEW");             //持收_保險績期
				records[++i] = checkIsNull(map, "INS_PL");                     //保險收益
				listCSV.add(records);
			}
			//header
			String [] csvHeader = new String[18];
			int j = 0;
			csvHeader[j] = "資料年月";
			csvHeader[++j] = "業務處代碼";
			csvHeader[++j] = "業務處名稱";
			csvHeader[++j] = "營運區代碼";
			csvHeader[++j] = "營運區名稱";
			csvHeader[++j] = "分別代碼";
			csvHeader[++j] = "分行別";
			csvHeader[++j] = "分行收益";
			csvHeader[++j] = "分行收益目標";
			csvHeader[++j] = "收益達成率";
			csvHeader[++j] = "投資日收";
			csvHeader[++j] = "持收_基金小額";
			csvHeader[++j] = "持收_管理費";
			csvHeader[++j] = "兌換損益";
			csvHeader[++j] = "投資收益";
			csvHeader[++j] = "保險首期";
			csvHeader[++j] = "持收_保險績期";
			csvHeader[++j] = "保險收益";
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
		}else
		{
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
	
}
