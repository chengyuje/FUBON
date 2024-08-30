package com.systex.jbranch.app.server.fps.pms702;
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

import com.systex.jbranch.app.server.fps.pms702.PMS702;
import com.systex.jbranch.app.server.fps.pms702.PMS702InputVO;
import com.systex.jbranch.app.server.fps.pms702.PMS702OutputVO;
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
 * Description : 保險負項明細報表Controller<br>
 * Comments Name : PMS702.java<br>
 * Author :zhouyiqiong<br>
 * Date :2016年11月14日 <br>
 * Version : 1.01 <br>
 * Editor : zhouyiqiong<br>
 * Editor Date : 2016年11月14日<br>
 */
@Component("pms702")
@Scope("request")
public class PMS702 extends FubonWmsBizLogic
{
	private DataAccessManager dam = null;
	private PMS702InputVO inputVO = null;
	private Logger logger = LoggerFactory.getLogger(PMS702.class);
	
public void inquire (Object body, IPrimitiveMap header) throws JBranchException, ParseException {
		
		PMS702InputVO inputVO = (PMS702InputVO) body;
		PMS702OutputVO outputVO = new PMS702OutputVO();
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
/**
 * 角色獲取
 * @param body
 * @param header
 * @throws JBranchException
 */
	public void getRole(Object body, IPrimitiveMap header)
			throws JBranchException {
		PMS702OutputVO return_VO = new PMS702OutputVO();
		PMS702InputVO inputVO = (PMS702InputVO) body;
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
	public void inquire (QueryConditionIF condition, PMS702InputVO inputVO) throws JBranchException, ParseException {
		
	
		StringBuffer sql = new StringBuffer();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		sql.append("  SELECT ROWNUM AS NUM,                                ");
		sql.append("  	     T.REGION_CENTER_ID,                           ");
		sql.append("  	     T.REGION_CENTER_NAME,                         ");
		sql.append("  	     T.BRANCH_AREA_ID,                             ");
		sql.append("  	     T.BRANCH_AREA_NAME,                           ");
		sql.append("  	     T.BRANCH_NBR,                                 ");
		sql.append("  	     T.BRANCH_NAME,                                ");
		sql.append("  	     T.AO_CODE,                                    ");
		sql.append("  	     T.EMP_ID,                                     ");
		sql.append("  	     T.EMP_NAME,                                   ");
		sql.append("  	     T.YEARMON,                                    ");
		sql.append("  	     T.ID_DUP,                                     ");
		sql.append("  	     T.POLICY_NO,                                  ");
		sql.append("  	     T.POLICY_SEQ,                                 ");
		sql.append("  	     T.APPL_ID,                                    ");
		sql.append("  	     T.APPL_NAME,                                  ");
		sql.append("  	     T.INS_TYPE_CODE,                              ");
		sql.append("  	     T.INS_TYPE_NOTE,                              ");
		sql.append("  	     T.CNCT_STATE,                                 ");
		sql.append("  	     T.PREM,                                       ");
		sql.append("  	     T.COMMISSION,                                 ");
		sql.append("  	     T.RAISE_FINAL,                                ");
		sql.append("  	     T.ACH_PRFT,                                   ");
//		RESOURCE1
		sql.append("  	     T.RESOURCE1,                                  ");
		sql.append("  	     T.PLUS_ACH_YR_MN,                             ");
		sql.append("  	     T.VERSION,                                    ");
		sql.append("  	     T.CREATETIME,                                 ");
		sql.append("  	     T.CREATOR,                                    ");
		sql.append("  	     T.MODIFIER,                                   ");
		sql.append("  	     T.LASTUPDATE                                  ");
		sql.append("  FROM (SELECT YEARMON,                                ");
		sql.append("  	           REGION_CENTER_ID,                       ");
		sql.append("  	           REGION_CENTER_NAME,                     ");
		sql.append("  	           BRANCH_AREA_ID,                         ");
		sql.append("  	           BRANCH_AREA_NAME,                       ");
		sql.append("  	           BRANCH_NBR,                             ");
		sql.append("  	           BRANCH_NAME,                            ");
		sql.append("  	           AO_CODE,                                ");
		sql.append("  	           EMP_ID,                                 ");
		sql.append("  	           EMP_NAME,                               ");
		sql.append("  	           ID_DUP,                                 ");
		sql.append("  	     	   POLICY_NO,                              ");
		sql.append("  	     	   POLICY_SEQ,                             ");
		sql.append("  	     	   APPL_ID,                                ");
		sql.append("  	    	   APPL_NAME,                              ");
		sql.append("  	     	   INS_TYPE_CODE,                          ");
		sql.append("  	     	   INS_TYPE_NOTE,                          ");
		sql.append("  	     	   CNCT_STATE,                             ");
		sql.append("  	     	   PREM,                                   ");
		sql.append("  	     	   COMMISSION,                             ");
		sql.append("  	     	   RAISE_FINAL,                            ");
		sql.append("  	     	   ACH_PRFT,                               ");
		//RESOURCE1  新件/續期
		sql.append("  	     	   RESOURCE1,                              ");
		sql.append("  	     	   PLUS_ACH_YR_MN,                         ");
		sql.append("  	     	   VERSION,                                ");
		sql.append("  	     	   CREATETIME,                             ");
		sql.append("  	     	   CREATOR,                                ");
		sql.append("  	     	   MODIFIER,                               ");
		sql.append("  	     	   LASTUPDATE                              ");
		sql.append("  	 FROM TBPMS_INS_MINUS_ITEM_RPT                     ");
		sql.append(" 	 ORDER BY  REGION_CENTER_ID,                       ");
		sql.append(" 			   BRANCH_AREA_ID,                         ");
		sql.append(" 			   BRANCH_NBR,                             ");
		sql.append(" 			   AO_CODE) T                              ");
		sql.append("  WHERE 1 = 1                                          ");
		if (!StringUtils.isBlank(inputVO.getRegion_center_id())) {
			sql.append(" AND T.REGION_CENTER_ID = :regionCenter            ");
			condition.setObject("regionCenter", inputVO.getRegion_center_id());
		}
		if (!StringUtils.isBlank(inputVO.getBranch_area_id())) {
			sql.append(" AND T.BRANCH_AREA_ID = :branchArea                ");
			condition.setObject("branchArea", inputVO.getBranch_area_id());
		}
		if (!StringUtils.isBlank(inputVO.getBranch_nbr())) {
			sql.append(" AND T.BRANCH_NBR = :branchNbr                     ");
			condition.setObject("branchNbr", inputVO.getBranch_nbr());
		}
		if (!StringUtils.isBlank(inputVO.getAo_code())) {
			sql.append(" AND T.AO_CODE = :aoCode                            ");
			condition.setObject("aoCode", inputVO.getAo_code());
		}
		if (!StringUtils.isBlank(inputVO.getsTime())) {
			sql.append(" AND TRIM(T.YEARMON) = :sTime                       ");
			condition.setObject("sTime", inputVO.getsTime().trim());
		}
		if (!StringUtils.isBlank(inputVO.getPOLICY_NO())) {
			sql.append(" AND POLICY_NO = :POLICY_NO                                 ");
			condition.setObject("POLICY_NO", inputVO.getPOLICY_NO().trim().toUpperCase());
		}
		if (!StringUtils.isBlank(inputVO.getAPPL_ID())) {
			sql.append(" AND APPL_ID = :APPL_ID                                 ");
			condition.setObject("APPL_ID", inputVO.getAPPL_ID().trim().toUpperCase());
		}
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
//		PMS702InputVO inputVO = (PMS702InputVO) body;
//		PMS702OutputVO outputVO = new PMS702OutputVO();
//		dam = this.getDataAccessManager();
//		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
//		StringBuffer sql = new StringBuffer();
//		try
//		{
//			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
//			sql.append("  SELECT ROWNUM AS NUM,                                ");
//			sql.append("  	     T.REGION_CENTER_ID,                           ");
//			sql.append("  	     T.REGION_CENTER_NAME,                         ");
//			sql.append("  	     T.BRANCH_AREA_ID,                             ");
//			sql.append("  	     T.BRANCH_AREA_NAME,                           ");
//			sql.append("  	     T.BRANCH_NBR,                                 ");
//			sql.append("  	     T.BRANCH_NAME,                                ");
//			sql.append("  	     T.AO_CODE,                                    ");
//			sql.append("  	     T.EMP_ID,                                     ");
//			sql.append("  	     T.EMP_NAME,                                   ");
//			sql.append("  	     T.YEARMON,                                    ");
//			sql.append("  	     T.ID_DUP,                                     ");
//			sql.append("  	     T.POLICY_NO,                                  ");
//			sql.append("  	     T.POLICY_SEQ,                                 ");
//			sql.append("  	     T.APPL_ID,                                    ");
//			sql.append("  	     T.APPL_NAME,                                  ");
//			sql.append("  	     T.INS_TYPE_CODE,                              ");
//			sql.append("  	     T.INS_TYPE_NOTE,                              ");
//			sql.append("  	     T.CNCT_STATE,                                 ");
//			sql.append("  	     T.PREM,                                       ");
//			sql.append("  	     T.COMMISSION,                                 ");
//			sql.append("  	     T.RAISE_FINAL,                                ");
//			sql.append("  	     T.ACH_PRFT,                                   ");
////			RESOURCE1
//			sql.append("  	     T.RESOURCE1,                                  ");
//			sql.append("  	     T.PLUS_ACH_YR_MN,                             ");
//			sql.append("  	     T.VERSION,                                    ");
//			sql.append("  	     T.CREATETIME,                                 ");
//			sql.append("  	     T.CREATOR,                                    ");
//			sql.append("  	     T.MODIFIER,                                   ");
//			sql.append("  	     T.LASTUPDATE                                  ");
//			sql.append("  FROM (SELECT YEARMON,                                ");
//			sql.append("  	           REGION_CENTER_ID,                       ");
//			sql.append("  	           REGION_CENTER_NAME,                     ");
//			sql.append("  	           BRANCH_AREA_ID,                         ");
//			sql.append("  	           BRANCH_AREA_NAME,                       ");
//			sql.append("  	           BRANCH_NBR,                             ");
//			sql.append("  	           BRANCH_NAME,                            ");
//			sql.append("  	           AO_CODE,                                ");
//			sql.append("  	           EMP_ID,                                 ");
//			sql.append("  	           EMP_NAME,                               ");
//			sql.append("  	           ID_DUP,                                 ");
//			sql.append("  	     	   POLICY_NO,                              ");
//			sql.append("  	     	   POLICY_SEQ,                             ");
//			sql.append("  	     	   APPL_ID,                                ");
//			sql.append("  	    	   APPL_NAME,                              ");
//			sql.append("  	     	   INS_TYPE_CODE,                          ");
//			sql.append("  	     	   INS_TYPE_NOTE,                          ");
//			sql.append("  	     	   CNCT_STATE,                             ");
//			sql.append("  	     	   PREM,                                   ");
//			sql.append("  	     	   COMMISSION,                             ");
//			sql.append("  	     	   RAISE_FINAL,                            ");
//			sql.append("  	     	   ACH_PRFT,                               ");
//			//RESOURCE1  新件/續期
//			sql.append("  	     	   RESOURCE1,                              ");
//			sql.append("  	     	   PLUS_ACH_YR_MN,                         ");
//			sql.append("  	     	   VERSION,                                ");
//			sql.append("  	     	   CREATETIME,                             ");
//			sql.append("  	     	   CREATOR,                                ");
//			sql.append("  	     	   MODIFIER,                               ");
//			sql.append("  	     	   LASTUPDATE                              ");
//			sql.append("  	 FROM TBPMS_INS_MINUS_ITEM_RPT                     ");
//			sql.append(" 	 ORDER BY  REGION_CENTER_ID,                       ");
//			sql.append(" 			   BRANCH_AREA_ID,                         ");
//			sql.append(" 			   BRANCH_NBR,                             ");
//			sql.append(" 			   AO_CODE) T                              ");
//			sql.append("  WHERE 1 = 1                                          ");
//			if (!StringUtils.isBlank(inputVO.getRegion_center_id())) {
//				sql.append(" AND T.REGION_CENTER_ID = :regionCenter            ");
//				condition.setObject("regionCenter", inputVO.getRegion_center_id());
//			}
//			if (!StringUtils.isBlank(inputVO.getBranch_area_id())) {
//				sql.append(" AND T.BRANCH_AREA_ID = :branchArea                ");
//				condition.setObject("branchArea", inputVO.getBranch_area_id());
//			}
//			if (!StringUtils.isBlank(inputVO.getBranch_nbr())) {
//				sql.append(" AND T.BRANCH_NBR = :branchNbr                     ");
//				condition.setObject("branchNbr", inputVO.getBranch_nbr());
//			}
//			if (!StringUtils.isBlank(inputVO.getAo_code())) {
//				sql.append(" AND T.AO_CODE = :aoCode                            ");
//				condition.setObject("aoCode", inputVO.getAo_code());
//			}
//			if (!StringUtils.isBlank(inputVO.getsTime())) {
//				sql.append(" AND TRIM(T.YEARMON) = :sTime                       ");
//				condition.setObject("sTime", inputVO.getsTime().trim());
//			}
//			if (!StringUtils.isBlank(inputVO.getPOLICY_NO())) {
//				sql.append(" AND POLICY_NO = :POLICY_NO                                 ");
//				condition.setObject("POLICY_NO", inputVO.getPOLICY_NO().trim().toUpperCase());
//			}
//			if (!StringUtils.isBlank(inputVO.getAPPL_ID())) {
//				sql.append(" AND APPL_ID = :APPL_ID                                 ");
//				condition.setObject("APPL_ID", inputVO.getAPPL_ID().trim().toUpperCase());
//			}
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
		PMS702InputVO inputVO = (PMS702InputVO) body;
		PMS702OutputVO outputVO = new PMS702OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		this.inquire(condition, inputVO);

		List<Map<String, Object>> list = dam.exeQuery(condition);
		if(list.size()>200000)
		{	
			outputVO.setResultList( new ArrayList());
			outputVO.setErrorMessage("匯出筆數過多, 請增加查詢條件");
			sendRtnObject(outputVO);
		}
		else
		{
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHH:mm:ss");
			String fileName = "保險負項明細報表_" + sdf.format(new Date()) + ".csv"; 
			List listCSV =  new ArrayList();
			for(Map<String, Object> map : list){
				String[] records = new String[25];
				int i = 0;
				records[i] = checkIsNull(map, "NUM");                          //項次
				records[++i] = checkIsNullAndTrans(map, "REGION_CENTER_ID");           //區域中心代碼
				records[++i] = checkIsNull(map, "REGION_CENTER_NAME");         //區域中心
				records[++i] = checkIsNullAndTrans(map, "BRANCH_AREA_ID");             //營運區代碼
				records[++i] = checkIsNull(map, "BRANCH_AREA_NAME");           //營運區
				records[++i] = checkIsNullAndTrans(map, "BRANCH_NBR");                 //分行代碼
				records[++i] = checkIsNull(map, "BRANCH_NAME");                //分行別
				records[++i] = checkIsNullAndTrans(map, "AO_CODE");                      //專員AO_CODE
				records[++i] = checkIsNullAndTrans(map, "EMP_ID");                     //專員員工代碼
				records[++i] = checkIsNull(map, "EMP_NAME");                   //專員姓名
				records[++i] = checkIsNull(map, "YEARMON");                    //成績年月
				records[++i] = checkIsNullAndTrans(map, "POLICY_NO");                  //保單號碼
				records[++i] = checkIsNullAndTrans(map, "POLICY_SEQ");                 //保單序號
				records[++i] = checkIsNullAndTrans(map, "APPL_ID");                    //要保人ID
				records[++i] = checkIsNull(map, "ID_DUP");                     //身分證重覆別
				records[++i] = checkIsNull(map, "APPL_NAME");                  //要保人姓名
				records[++i] = checkIsNullAndTrans(map, "INS_TYPE_CODE");              //險種代碼
				records[++i] = checkIsNull(map, "INS_TYPE_NOTE");              //險種說明
				records[++i] = checkIsNull(map, "CNCT_STATE");                 //契約狀態
				records[++i] = checkIsNull(map, "PREM");                       //保費
				records[++i] = checkIsNull(map, "COMMISSION");                 //佣金
				records[++i] = checkIsNull(map, "RAISE_FINAL");                //加碼FINAL
				records[++i] = checkIsNull(map, "ACH_PRFT");                   //計績收益
				records[++i] = checkIsNull(map, "PLUS_ACH_YR_MN");             //正項計績月份
				records[++i] = checkIsNull(map, "RESOURCE1");                    //新件/續期
				listCSV.add(records);
			}
			//header
			String [] csvHeader = new String[25];
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
			csvHeader[++j] = "險種代碼";
			csvHeader[++j] = "險種說明";
			csvHeader[++j] = "契約狀態";
			csvHeader[++j] = "保費";
			csvHeader[++j] = "佣金";
			csvHeader[++j] = "加碼FINAL";
			csvHeader[++j] = "計績收益";
			csvHeader[++j] = "正項計績月份";
			csvHeader[++j] = "新件/續期";
			CSVUtil csv = new CSVUtil();
			csv.setHeader(csvHeader);
			csv.addRecordList(listCSV);
			String url = csv.generateCSV();
			notifyClientToDownloadFile(url, fileName); //download
		}
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
