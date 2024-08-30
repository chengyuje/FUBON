package com.systex.jbranch.app.server.fps.pms703;
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

import com.systex.jbranch.app.server.fps.pms703.PMS703;
import com.systex.jbranch.app.server.fps.pms703.PMS703InputVO;
import com.systex.jbranch.app.server.fps.pms703.PMS703OutputVO;
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
 * Description : 保險新承作明細報表Controller<br>
 * Comments Name : PMS703.java<br>
 * Author :zhouyiqiong<br>
 * Date :2016年11月15日 <br>
 * Version : 1.01 <br>
 * Editor : zhouyiqiong<br>
 * Editor Date : 2016年11月15日<br>
 */
@Component("pms703")
@Scope("request")
public class PMS703 extends FubonWmsBizLogic
{
	private DataAccessManager dam = null;
	private PMS703InputVO inputVO = null;
	private Logger logger = LoggerFactory.getLogger(PMS703.class);
	
	public void inquire (Object body, IPrimitiveMap header) throws JBranchException, ParseException {
		
		PMS703InputVO inputVO = (PMS703InputVO) body;
		PMS703OutputVO outputVO = new PMS703OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		int total=0;
		this.inquire(condition, inputVO);
		
		ResultIF list = dam.executePaging(condition,inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
	
		outputVO.setTotalPage(list.getTotalPage());
		outputVO.setResultList(list);
		outputVO.setTotalRecord(list.getTotalRecord());
		outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());
		outputVO.setErrorMessage("");
		total=list.getTotalRecord();
		if(outputVO.getTotalRecord()>200000)
		{
			outputVO.setResultList( new ArrayList());
			outputVO.setErrorMessage("查詢筆數過多, 請增加查詢條件");
			sendRtnObject(outputVO);
		}else
		{
			sendRtnObject(outputVO);
		}
	}

	public void inquire (QueryConditionIF condition, PMS703InputVO inputVO) throws JBranchException, ParseException {
		
	
		StringBuffer sql = new StringBuffer();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		sql.append("  SELECT ROWNUM AS NUM,                                          ");
		sql.append("		T.*										         ");			
		sql.append("  FROM (SELECT YEARMON,											 ");
		sql.append("  	     	REGION_CENTER_ID,                                    ");
		sql.append("  	     	REGION_CENTER_NAME,                                  ");
		sql.append("  	     	BRANCH_AREA_ID,                                      ");
		sql.append("  	     	BRANCH_AREA_NAME,                                    ");
		sql.append("  	     	BRANCH_NBR,                                          ");
		sql.append("  	     	BRANCH_NAME,                                         ");
		sql.append("  	     	AO_CODE,                                             ");
		sql.append("  	     	EMP_ID,                                              ");
		sql.append("  	     	EMP_NAME,                                            ");
		sql.append("  	     	ID_DUP,                                              ");
		sql.append("  	     	POLICY_NO,                                           ");
		sql.append("  	     	POLICY_SEQ,                                          ");
		sql.append("  	     	APPL_ID,                                             ");
		sql.append("  	     	APPL_NAME,                                           ");
		sql.append("  	     	EFF_YY,                                              ");
		sql.append("  	     	EFF_MM,                                              ");
		sql.append("  	     	EFF_DD,                                              ");
		sql.append("  	     	INS_TYPE_CODE,                                       ");
		sql.append("  	     	INS_TYPE_NOTE,                                       ");
		sql.append("  	     	PREM,                                                ");
		sql.append("  	     	COMMISSION,                                          ");
		sql.append("  	     	RAISE_FINAL,                                         ");
		sql.append("  	     	ACH_PRFT,                                            ");
		sql.append("  	     	RESOURCE1,                                           ");
		sql.append("  	     	VERSION,                                             ");
		sql.append("  	     	CREATETIME,                                          ");
		sql.append("  	     	CREATOR,                                             ");
		sql.append("  	     	MODIFIER,                                            ");
		sql.append("  	     	LASTUPDATE                                           ");
		sql.append("  FROM TBPMS_INS_NEW_RPT                                         ");
		sql.append("  WHERE 1=1                                                     ");
		if (!StringUtils.isBlank(inputVO.getsTime())) {
			sql.append(" AND YEARMON = :sTime                                 ");
			condition.setObject("sTime", inputVO.getsTime().trim());
		}
		if (!StringUtils.isBlank(inputVO.getRegion_center_id())) {
			sql.append(" AND REGION_CENTER_ID = :regionCenter     		         ");
			condition.setObject("regionCenter", inputVO.getRegion_center_id());
		}
		if (!StringUtils.isBlank(inputVO.getBranch_area_id())) {
			sql.append(" AND BRANCH_AREA_ID = :branchArea                          ");
			condition.setObject("branchArea", inputVO.getBranch_area_id());
		}
		if (!StringUtils.isBlank(inputVO.getBranch_nbr())) {
			sql.append(" AND BRANCH_NBR = :branchNbr                               ");
			condition.setObject("branchNbr", inputVO.getBranch_nbr());
		}
		if (!StringUtils.isBlank(inputVO.getAo_code())) {
			sql.append(" AND AO_CODE = :aoCode                                     ");
			condition.setObject("aoCode", inputVO.getAo_code());
		}
		if (!StringUtils.isBlank(inputVO.getPOLICY_NO())) {
			sql.append(" AND POLICY_NO = :POLICY_NO                                 ");
			condition.setObject("POLICY_NO", inputVO.getPOLICY_NO().trim().toUpperCase());
		}
		if (!StringUtils.isBlank(inputVO.getAPPL_ID())) {
			sql.append(" AND APPL_ID = :APPL_ID                                 ");
			condition.setObject("APPL_ID", inputVO.getAPPL_ID().trim().toUpperCase());
		}
		sql.append("  ORDER BY  REGION_CENTER_ID,                                    ");
		sql.append(" 			BRANCH_AREA_ID,                                      ");
		sql.append(" 			BRANCH_NBR,                                          ");
		sql.append(" 			AO_CODE) T                                           ");
		condition.setQueryString(sql.toString());
	}
//	/**
//	 * 查詢檔案
//	 * 
//	 * @param body
//	 * @param header
//	 * @throws JBranchException
//	 */
//	public void inquire(Object body, IPrimitiveMap header) throws JBranchException
//	{	
//		PMS703InputVO inputVO = (PMS703InputVO) body;
//		PMS703OutputVO outputVO = new PMS703OutputVO();
//		dam = this.getDataAccessManager();
//		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
//		StringBuffer sql = new StringBuffer();
//		try
//		{
//			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
//			sql.append("  SELECT ROWNUM AS NUM,                                          ");
//			sql.append("		T.*										         ");			
//			sql.append("  FROM (SELECT YEARMON,											 ");
//			sql.append("  	     	REGION_CENTER_ID,                                    ");
//			sql.append("  	     	REGION_CENTER_NAME,                                  ");
//			sql.append("  	     	BRANCH_AREA_ID,                                      ");
//			sql.append("  	     	BRANCH_AREA_NAME,                                    ");
//			sql.append("  	     	BRANCH_NBR,                                          ");
//			sql.append("  	     	BRANCH_NAME,                                         ");
//			sql.append("  	     	AO_CODE,                                             ");
//			sql.append("  	     	EMP_ID,                                              ");
//			sql.append("  	     	EMP_NAME,                                            ");
//			sql.append("  	     	ID_DUP,                                              ");
//			sql.append("  	     	POLICY_NO,                                           ");
//			sql.append("  	     	POLICY_SEQ,                                          ");
//			sql.append("  	     	APPL_ID,                                             ");
//			sql.append("  	     	APPL_NAME,                                           ");
//			sql.append("  	     	EFF_YY,                                              ");
//			sql.append("  	     	EFF_MM,                                              ");
//			sql.append("  	     	EFF_DD,                                              ");
//			sql.append("  	     	INS_TYPE_CODE,                                       ");
//			sql.append("  	     	INS_TYPE_NOTE,                                       ");
//			sql.append("  	     	PREM,                                                ");
//			sql.append("  	     	COMMISSION,                                          ");
//			sql.append("  	     	RAISE_FINAL,                                         ");
//			sql.append("  	     	ACH_PRFT,                                            ");
//			sql.append("  	     	RESOURCE1,                                           ");
//			sql.append("  	     	VERSION,                                             ");
//			sql.append("  	     	CREATETIME,                                          ");
//			sql.append("  	     	CREATOR,                                             ");
//			sql.append("  	     	MODIFIER,                                            ");
//			sql.append("  	     	LASTUPDATE                                           ");
//			sql.append("  FROM TBPMS_INS_NEW_RPT                                         ");
//			sql.append("  WHERE 1=1                                                     ");
//			if (!StringUtils.isBlank(inputVO.getsTime())) {
//				sql.append(" AND YEARMON = :sTime                                 ");
//				condition.setObject("sTime", inputVO.getsTime().trim());
//			}
//			if (!StringUtils.isBlank(inputVO.getRegion_center_id())) {
//				sql.append(" AND REGION_CENTER_ID = :regionCenter     		         ");
//				condition.setObject("regionCenter", inputVO.getRegion_center_id());
//			}
//			if (!StringUtils.isBlank(inputVO.getBranch_area_id())) {
//				sql.append(" AND BRANCH_AREA_ID = :branchArea                          ");
//				condition.setObject("branchArea", inputVO.getBranch_area_id());
//			}
//			if (!StringUtils.isBlank(inputVO.getBranch_nbr())) {
//				sql.append(" AND BRANCH_NBR = :branchNbr                               ");
//				condition.setObject("branchNbr", inputVO.getBranch_nbr());
//			}
//			if (!StringUtils.isBlank(inputVO.getAo_code())) {
//				sql.append(" AND AO_CODE = :aoCode                                     ");
//				condition.setObject("aoCode", inputVO.getAo_code());
//			}
//			if (!StringUtils.isBlank(inputVO.getPOLICY_NO())) {
//				sql.append(" AND POLICY_NO = :POLICY_NO                                 ");
//				condition.setObject("POLICY_NO", inputVO.getPOLICY_NO().trim().toUpperCase());
//			}
//			if (!StringUtils.isBlank(inputVO.getAPPL_ID())) {
//				sql.append(" AND APPL_ID = :APPL_ID                                 ");
//				condition.setObject("APPL_ID", inputVO.getAPPL_ID().trim().toUpperCase());
//			}
//			sql.append("  ORDER BY  REGION_CENTER_ID,                                    ");
//			sql.append(" 			BRANCH_AREA_ID,                                      ");
//			sql.append(" 			BRANCH_NBR,                                          ");
//			sql.append(" 			AO_CODE) T                                           ");
//			
//			condition.setQueryString(sql.toString());
//			ResultIF list = dam.executePaging(condition, inputVO
//					.getCurrentPageIndex() + 1, inputVO.getPageCount());
//			//List<Map<String, Object>> list1 = dam.exeQuery(condition);
//			int totalPage_i = list.getTotalPage();
//			int totalRecord_i = list.getTotalRecord();
//			outputVO.setResultList(list);
//			//outputVO.setCsvList(list1);
//			outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());// 當前頁次
//			outputVO.setTotalPage(totalPage_i);// 總頁次
//			outputVO.setTotalRecord(totalRecord_i);// 總筆數
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
	public void export(Object body, IPrimitiveMap header) throws JBranchException, ParseException 
	{
		PMS703InputVO inputVO = (PMS703InputVO) body;
		PMS703OutputVO outputVO = new PMS703OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		this.inquire(condition, inputVO);

		List<Map<String, Object>> list = dam.exeQuery(condition);
		if(list.size()>200000)
		{	
			outputVO.setResultList( new ArrayList());
			outputVO.setErrorMessage("匯出筆數過多, 請增加查詢條件");
			sendRtnObject(outputVO);
//		
		}
		else
		{	
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH:mm:ss");
			String fileName = "保險佣金明細報表_" + sdf.format(new Date()) + ".csv"; 
			List listCSV =  new ArrayList();
			for(Map<String, Object> map : list){
				String[] records = new String[26];
				int i = 0;
				records[i] = checkIsNull(map, "NUM");                           //項次
				records[++i] = checkIsNullAndTrans(map, "REGION_CENTER_ID");            //區域中心代碼
				records[++i] = checkIsNull(map, "REGION_CENTER_NAME");          //區域中心
				records[++i] = checkIsNullAndTrans(map, "BRANCH_AREA_ID");              //營運區代碼
				records[++i] = checkIsNull(map, "BRANCH_AREA_NAME");            //營運區
				records[++i] = checkIsNullAndTrans(map, "BRANCH_NBR");                  //分行代碼
				records[++i] = checkIsNull(map, "BRANCH_NAME");                 //分行別
				records[++i] = checkIsNullAndTrans(map, "AO_CODE");                      //專員AO_CODE
				records[++i] = checkIsNullAndTrans(map, "EMP_ID");                      //專員員工代碼
				records[++i] = checkIsNull(map, "EMP_NAME");                    //專員姓名
				records[++i] = checkIsNull(map, "YEARMON");                     //成績年月
				records[++i] = checkIsNullAndTrans(map, "POLICY_NO");                   //保單號碼
				records[++i] = checkIsNullAndTrans(map, "POLICY_SEQ");                  //保單序號
				records[++i] = checkIsNull(map, "APPL_ID");                     //要保人ID
				records[++i] = checkIsNull(map, "ID_DUP");                      //身分證重覆別
				records[++i] = checkIsNull(map, "APPL_NAME");                   //要保人姓名
				records[++i] = checkIsNull(map, "EFF_YY");                      //生效日-年
				records[++i] = checkIsNull(map, "EFF_MM");                      //生效日-月
				records[++i] = checkIsNull(map, "EFF_DD");                      //生效日-日
				records[++i] = checkIsNullAndTrans(map, "INS_TYPE_CODE");               //險種代碼
				records[++i] = checkIsNull(map, "INS_TYPE_NOTE");               //險種說明
				records[++i] = checkIsNull(map, "PREM");                        //保費
				records[++i] = checkIsNull(map, "COMMISSION");                  //佣金
				records[++i] = checkIsNull(map, "RAISE_FINAL");                 //加碼FINAL
				records[++i] = checkIsNull(map, "ACH_PRFT");                    //計績收益
				records[++i] = checkIsNull(map, "RESOURCE1");                    //新件/續期
				listCSV.add(records);
			}
			//header
			String [] csvHeader = new String[26];
			int j = 0;
			csvHeader[j] = "項次";
			csvHeader[++j] = "區域中心代碼";
			csvHeader[++j] = "區域中心";
			csvHeader[++j] = "營運區代碼";
			csvHeader[++j] = "營運區";
			csvHeader[++j] = "分行代碼";
			csvHeader[++j] = "分行別";
			csvHeader[++j] = "AO_CODE";
			csvHeader[++j] = "專員員工代碼";
			csvHeader[++j] = "專員姓名";
			csvHeader[++j] = "計績年月";
			csvHeader[++j] = "保單號碼";
			csvHeader[++j] = "保單序號";
			csvHeader[++j] = "要保人ID";
			csvHeader[++j] = "身分證重覆別";
			csvHeader[++j] = "要保人姓名";
			csvHeader[++j] = "生效日-年";
			csvHeader[++j] = "生效日-月";
			csvHeader[++j] = "生效日-日";
			csvHeader[++j] = "險種代碼";
			csvHeader[++j] = "險種說明";
			csvHeader[++j] = "保費";
			csvHeader[++j] = "佣金";
			csvHeader[++j] = "加碼FINAL";
			csvHeader[++j] = "計績收益";
			csvHeader[++j] = "新件/續期";

			CSVUtil csv = new CSVUtil();
			csv.setHeader(csvHeader);
			csv.addRecordList(listCSV);
			String url = csv.generateCSV();
			notifyClientToDownloadFile(url, fileName); //download
		} 
//		else 
//		{
//			return_VO2.setResultList(list);
//			this.sendRtnObject(outputVO);
//	    }
			
	}
	/**
	* 檢查Map取出欄位是否為Null
	* 
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
	private String currencyFormat(Map map, String key)
	{		
		if(StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key) != null)
		{
			NumberFormat nf = NumberFormat.getCurrencyInstance();
			return nf.format(map.get(key));										
		}else
			return "$0.00";		
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
		
	/**
	 * 角色獲取
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void getRole(Object body, IPrimitiveMap header)
			throws JBranchException {
		PMS703OutputVO return_VO = new PMS703OutputVO();
		PMS703InputVO inputVO = (PMS703InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
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
	
}
