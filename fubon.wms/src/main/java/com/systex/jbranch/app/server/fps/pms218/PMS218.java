package com.systex.jbranch.app.server.fps.pms218;
import java.text.NumberFormat;
import java.text.ParseException;
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

import com.systex.jbranch.app.server.fps.pms214.PMS214OutputVO;
import com.systex.jbranch.app.server.fps.pms214.PMS214QueryInputVO;
import com.systex.jbranch.app.server.fps.pms707.PMS707OutputVO;
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
 * Description : 理專計績商品報表Controller<br>
 * Comments Name : PMS218.java<br>
 * Author :zhouyiqiong<br>
 * Date :2016年11月9日 <br>
 * Version : 1.01 <br>
 * Editor : zhouyiqiong<br>
 * Editor Date : 2016年11月9日<br>
 */
@Component("pms218")
@Scope("request")
public class PMS218 extends FubonWmsBizLogic
{
	private DataAccessManager dam = null;
	private PMS218InputVO inputVO = null;
	private Logger logger = LoggerFactory.getLogger(PMS218.class);
	/**
	 * 角色獲取
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void getRole(Object body, IPrimitiveMap header)
			throws JBranchException {
		PMS218OutputVO return_VO = new PMS218OutputVO();
		PMS218InputVO inputVO = (PMS218InputVO) body;
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
			List roleList = dam.exeQuery(queryCondition);
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
		PMS218OutputVO return_VO = new PMS218OutputVO();
		PMS218InputVO inputVO = (PMS218InputVO) body;
		dam = this.getDataAccessManager();
		String role = inputVO.getRole();
		QueryConditionIF queryCondition = dam
				.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		try {
			sql.append(" select * from table(FN_EXAM_REPORT('"+inputVO.getsTime()+"','"+role+"','PMS218'))");
			queryCondition.setQueryString(sql.toString());
			// result
			List<Map<String, Object>> list = dam.exeQuery(queryCondition);
			return_VO.setVerList(list);
			this.sendRtnObject(return_VO);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}
	public void inquire (Object body, IPrimitiveMap header) throws JBranchException, ParseException {
		
		PMS218InputVO inputVO = (PMS218InputVO) body;
		PMS218OutputVO outputVO = new PMS218OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		int total=0;
		this.inquire(condition, inputVO);
		List roleList = new ArrayList();
		roleList.add(0, inputVO.getRole());
		ResultIF list = dam.executePaging(condition,inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
	
	
		if (list.size() > 0) {
			outputVO.setTotalPage(list.getTotalPage());
			outputVO.setResultList(list);
			outputVO.setTotalRecord(list.getTotalRecord());
			outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());
			outputVO.setErrorMessage("");
			outputVO.setRoleList(roleList);
			total=list.getTotalRecord();
			if(outputVO.getTotalRecord()>200000)
			{
//				logger.error(String.format("查詢筆數過多, 請增加查詢條件"));
//			 	throw new APException(e.getMessage());
				outputVO.setResultList( new ArrayList());
				outputVO.setErrorMessage("查詢筆數過多, 請增加查詢條件");
				sendRtnObject(outputVO);
			}
			else
			{
				sendRtnObject(outputVO);
			}
		}
		else 
		{
				throw new APException("ehl_01_common_009");
		}
		
	}

	public void inquire (QueryConditionIF condition, PMS218InputVO inputVO) throws JBranchException, ParseException {
			//建立字串buffer
			StringBuffer sql = new StringBuffer();
			//時間格式化字串格式
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			//以下主查詢
			sql.append("  SELECT                                                 ");
			sql.append("         ROW_NUMBER() OVER(ORDER BY REGION_CENTER_ID,BRANCH_AREA_ID, BRANCH_NBR,AO_CODE) AS NUM,");
			sql.append("         T.YEARMON,                                      ");
			sql.append("         T.OPEN_FLAG,                                    ");
			sql.append("         T.EXEC_DATE,                                    ");
			sql.append("         T.REPORT_TYPE,                                  ");
			sql.append("         T.REGION_CENTER_ID,                             ");
			sql.append("         T.REGION_CENTER_NAME,                           ");
			sql.append("         T.BRANCH_AREA_ID,                               ");
			sql.append("         T.BRANCH_AREA_NAME,                             ");
			sql.append("         T.BRANCH_NBR,                                   ");
			sql.append("         T.BRANCH_NAME,                                  ");
			sql.append("         T.AO_CODE,                                      ");
			sql.append("         T.EMP_ID,                                       ");
			sql.append("         T.EMP_NAME,                                     ");
			sql.append("         T.PROD_TYPE,                                    ");
			sql.append("         S.PARAM_NAME AS PROD_TYPE_NAME,                 ");
			sql.append("         T.PROD_ID,                                      ");
			sql.append("         T.PROD_NAME,                                    ");
			sql.append("         T.SELL_NUM,                                     ");
			sql.append("         T.ACT_PRFT,                                     ");
			sql.append("         T.CNR_PRFT                                      ");
			sql.append("  FROM (SELECT YEARMON,                                  ");
			sql.append("             EXEC_DATE,                                  ");
			sql.append("             OPEN_FLAG,                                  ");
			sql.append("             REPORT_TYPE,                                ");
			sql.append("             REGION_CENTER_ID,                           ");
			sql.append("             REGION_CENTER_NAME,                         ");
			sql.append("             BRANCH_AREA_ID,                             ");
			sql.append("             BRANCH_AREA_NAME,                           ");
			sql.append("             BRANCH_NBR,                                 ");
			sql.append("             BRANCH_NAME,                                ");
			sql.append("             AO_CODE,                                    ");
			sql.append("             EMP_ID,                                     ");
			sql.append("             EMP_NAME,                                   ");
			sql.append("             PROD_TYPE,                                  ");
			sql.append("             PROD_ID,                                    ");
			sql.append("             PROD_NAME,                                  ");
			sql.append("             SELL_NUM,                                   ");
			sql.append("             ACT_PRFT,                                   ");
			sql.append("             CNR_PRFT                                    ");
			sql.append("        FROM TBPMS_BUY_PROD_RPT) T                       ");
			sql.append("  LEFT JOIN TBSYSPARAMETER S                             ");
			sql.append("       ON S.PARAM_TYPE = 'PMS.CNR_PROD_SOURCE'           ");
			sql.append("       AND S.PARAM_CODE = T.PROD_TYPE                 	 ");
			sql.append("  WHERE 1 = 1                                            ");
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
			if (!StringUtils.isBlank(inputVO.getAo_code())){
				sql.append(" AND AO_CODE = :ao_code 					    	  ");
				condition.setObject("ao_code", inputVO.getAo_code());
			}
			if (!StringUtils.isBlank(inputVO.getPROD_SOURCE())){
				sql.append(" AND PROD_TYPE = :PROD_SOURCE 					    	  ");
				condition.setObject("PROD_SOURCE", inputVO.getPROD_SOURCE());
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
				sql.append(" AND TRIM(T.YEARMON) = :sTime                        ");
				condition.setObject("sTime", inputVO.getsTime().trim());
			}
			sql.append(" ORDER BY REGION_CENTER_ID,BRANCH_AREA_ID, BRANCH_NBR,AO_CODE 	            ");
			condition.setQueryString(sql.toString());
	}
	
//	
//	/**
//	 * 查詢檔案
//	 * 
//	 * @param body
//	 * @param header
//	 * @throws JBranchException
//	 */
//	public void inquire(Object body, IPrimitiveMap header) throws JBranchException
//	{	
//		PMS218InputVO inputVO = (PMS218InputVO) body;
//		PMS218OutputVO outputVO = new PMS218OutputVO();
//		dam = this.getDataAccessManager();
//		List roleList = new ArrayList();
//		roleList.add(0, inputVO.getRole());
//		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
//		StringBuffer sql = new StringBuffer();
//		try
//		{
//			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
//			sql.append("  SELECT                                                 ");
//			sql.append("         ROW_NUMBER() OVER(ORDER BY REGION_CENTER_ID,BRANCH_AREA_ID, BRANCH_NBR,AO_CODE) AS NUM,");
//			sql.append("         T.YEARMON,                                      ");
//			sql.append("         T.OPEN_FLAG,                                    ");
//			sql.append("         T.EXEC_DATE,                                    ");
//			sql.append("         T.REPORT_TYPE,                                  ");
//			sql.append("         T.REGION_CENTER_ID,                             ");
//			sql.append("         T.REGION_CENTER_NAME,                           ");
//			sql.append("         T.BRANCH_AREA_ID,                               ");
//			sql.append("         T.BRANCH_AREA_NAME,                             ");
//			sql.append("         T.BRANCH_NBR,                                   ");
//			sql.append("         T.BRANCH_NAME,                                  ");
//			sql.append("         T.AO_CODE,                                      ");
//			sql.append("         T.EMP_ID,                                       ");
//			sql.append("         T.EMP_NAME,                                     ");
//			sql.append("         T.PROD_TYPE,                                    ");
//			sql.append("         S.PARAM_NAME AS PROD_TYPE_NAME,                 ");
//			sql.append("         T.PROD_ID,                                      ");
//			sql.append("         T.PROD_NAME,                                    ");
//			sql.append("         T.SELL_NUM,                                     ");
//			sql.append("         T.ACT_PRFT,                                     ");
//			sql.append("         T.CNR_PRFT                                      ");
//			sql.append("  FROM (SELECT YEARMON,                                  ");
//			sql.append("             EXEC_DATE,                                  ");
//			sql.append("             OPEN_FLAG,                                  ");
//			sql.append("             REPORT_TYPE,                                ");
//			sql.append("             REGION_CENTER_ID,                           ");
//			sql.append("             REGION_CENTER_NAME,                         ");
//			sql.append("             BRANCH_AREA_ID,                             ");
//			sql.append("             BRANCH_AREA_NAME,                           ");
//			sql.append("             BRANCH_NBR,                                 ");
//			sql.append("             BRANCH_NAME,                                ");
//			sql.append("             AO_CODE,                                    ");
//			sql.append("             EMP_ID,                                     ");
//			sql.append("             EMP_NAME,                                   ");
//			sql.append("             PROD_TYPE,                                  ");
//			sql.append("             PROD_ID,                                    ");
//			sql.append("             PROD_NAME,                                  ");
//			sql.append("             SELL_NUM,                                   ");
//			sql.append("             ACT_PRFT,                                   ");
//			sql.append("             CNR_PRFT                                    ");
//			sql.append("        FROM TBPMS_BUY_PROD_RPT) T                       ");
//			sql.append("  LEFT JOIN TBSYSPARAMETER S                             ");
//			sql.append("       ON S.PARAM_TYPE = 'PMS.CNR_PROD_SOURCE'           ");
//			sql.append("       AND S.PARAM_CODE = T.PROD_TYPE                 	 ");
//			sql.append("  WHERE 1 = 1                                            ");
//			if (!StringUtils.isBlank(inputVO.getRegion_center_id())) {
//				sql.append(" AND REGION_CENTER_ID = :region_center_id         ");
//				condition.setObject("region_center_id", inputVO.getRegion_center_id());
//			}
//			if (!StringUtils.isBlank(inputVO.getBranch_area_id())) {
//				sql.append(" AND BRANCH_AREA_ID = :branch_area_id             ");
//				condition.setObject("branch_area_id", inputVO.getBranch_area_id());
//			}
//			if (!StringUtils.isBlank(inputVO.getBranch_nbr())) {
//				sql.append(" AND BRANCH_NBR = :branch_nbr                     ");
//				condition.setObject("branch_nbr", inputVO.getBranch_nbr());
//			}
//			if (!StringUtils.isBlank(inputVO.getAo_code())){
//				sql.append(" AND AO_CODE = :ao_code 					    	  ");
//				condition.setObject("ao_code", inputVO.getAo_code());
//			}
//			if (!StringUtils.isBlank(inputVO.getPROD_SOURCE())){
//				sql.append(" AND PROD_TYPE = :PROD_SOURCE 					    	  ");
//				condition.setObject("PROD_SOURCE", inputVO.getPROD_SOURCE());
//			}
//			if(StringUtils.isNotBlank(inputVO.getRptVersion())){
//				if(inputVO.getRptVersion().equals("上簽版")){
//					sql.append(" AND REPORT_TYPE = '1' 					    ");
//				}else{
//					sql.append(" AND REPORT_TYPE = '0' 				        ");
//					sql.append(" AND EXEC_DATE = :rptVersion 	            ");
//					condition.setObject("rptVersion", inputVO.getRptVersion().substring(3,11));
//				}
//			}
//			if (!StringUtils.isBlank(inputVO.getsTime())) {
//				sql.append(" AND TRIM(T.YEARMON) = :sTime                        ");
//				condition.setObject("sTime", inputVO.getsTime().trim());
//			}
//			sql.append(" ORDER BY REGION_CENTER_ID,BRANCH_AREA_ID, BRANCH_NBR,AO_CODE 	            ");
//			condition.setQueryString(sql.toString());
//			ResultIF list = dam.executePaging(condition, inputVO
//					.getCurrentPageIndex() + 1, inputVO.getPageCount());
//			List<Map<String, Object>> list1 = dam.exeQuery(condition);
//			int totalPage_i = list.getTotalPage();
//			int totalRecord_i = list.getTotalRecord();
//			outputVO.setResultList(list);
//			outputVO.setCsvList(list1);
//			outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());// 當前頁次
//			outputVO.setTotalPage(totalPage_i);// 總頁次
//			outputVO.setTotalRecord(totalRecord_i);// 總筆數
//			outputVO.setRoleList(roleList);
//			this.sendRtnObject(outputVO);
//		}
//		catch (Exception e)
//		{
//			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
//			throw new APException("系統發生錯誤請洽系統管理員");
//		}
//	}
	/**
	 * 匯出EXCLE
	 * @param body
	 * @param header
	 * @throws JBranchException
	 * @throws ParseException 
	 */
	public void export(Object body, IPrimitiveMap header) throws JBranchException, ParseException {
		PMS218InputVO inputVO = (PMS218InputVO) body;
		PMS218OutputVO outputVO = new PMS218OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		//套用共用查詢條件
		this.inquire(condition, inputVO);
		List<Map<String, Object>> list = dam.exeQuery(condition);
		if(list.size()>200000)
		{	outputVO.setResultList( new ArrayList());
			outputVO.setErrorMessage("匯出筆數過多, 請增加查詢條件");
			sendRtnObject(outputVO);
//		
		}
		else
		{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH:mm:ss");
			String fileName = "理專計績商品報表_" + sdf.format(new Date()) + ".csv"; 
			List listCSV =  new ArrayList();
			for(Map<String, Object> map : list){
				String[] records = new String[17];
				int i = 0;
				records[i] = checkIsNull(map, "NUM");                        //項次
				records[++i] = checkIsNull(map, "YEARMON");  				 //成績年月
				records[++i] = checkIsNullAndTrans(map, "REGION_CENTER_ID");	     //區域中心 id
				records[++i] = checkIsNull(map, "REGION_CENTER_NAME");	     //區域中心 name
				records[++i] = checkIsNullAndTrans(map, "BRANCH_AREA_ID");	     //營運區	id
				records[++i] = checkIsNull(map, "BRANCH_AREA_NAME");	     //營運區	name
				records[++i] = checkIsNullAndTrans(map, "BRANCH_NBR");			     //分行別	
				records[++i] = checkIsNull(map, "BRANCH_NAME");			     //分行別	
				records[++i] = checkIsNullAndTrans(map, "AO_CODE");				     //AO_CODE
				records[++i] = checkIsNullAndTrans(map, "EMP_ID");  			     //員工編號
				records[++i] = checkIsNull(map, "EMP_NAME");			     //專員姓名
				records[++i] = checkIsNull(map, "PROD_TYPE_NAME");		      //商品類別					
				records[++i] = checkIsNullAndTrans(map, "PROD_ID");		             //商品代碼
				records[++i] = checkIsNull(map, "PROD_NAME");		         //商品名稱
				records[++i] = checkIsNull(map, "SELL_NUM");		         //銷量
				records[++i] = checkIsNull(map, "ACT_PRFT");		         //實際收益
				records[++i] = checkIsNull(map, "CNR_PRFT");		         //CNR收益	
				listCSV.add(records);
			}
			//header
			String [] csvHeader = new String[17];
			int j = 0;
			csvHeader[j] = "項次";
			csvHeader[++j] = "成績年月";
			csvHeader[++j] = "業務處代碼";
			csvHeader[++j] = "業務處名稱";
			csvHeader[++j] = "營運區代碼";
			csvHeader[++j] = "營運區名稱";
			csvHeader[++j] = "分行別代碼";
			csvHeader[++j] = "分行別名稱";
			csvHeader[++j] = "AO_CODE";
			csvHeader[++j] = "員工編號";
			csvHeader[++j] = "專員姓名";
			csvHeader[++j] = "商品類別";
			csvHeader[++j] = "商品代碼";
			csvHeader[++j] = "商品名稱";
			csvHeader[++j] = "銷量";
			csvHeader[++j] = "實際收益";
			csvHeader[++j] = "CNR收益";
			CSVUtil csv = new CSVUtil();
			csv.setHeader(csvHeader);
			csv.addRecordList(listCSV);
			String url = csv.generateCSV();
			notifyClientToDownloadFile(url, fileName); //download
			this.sendRtnObject(null);
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
}
