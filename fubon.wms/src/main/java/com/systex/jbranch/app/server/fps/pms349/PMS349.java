package com.systex.jbranch.app.server.fps.pms349;

import java.math.BigDecimal;
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

import com.systex.jbranch.app.server.fps.pms000.PMS000;
import com.systex.jbranch.app.server.fps.pms000.PMS000InputVO;
import com.systex.jbranch.app.server.fps.pms000.PMS000OutputVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * Copy Right Information :<br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description :客戶整體損益分析報表<br>
 * Comments Name : PMS349java<br>
 * Author : Kevin<br>
 * Date :2016/07/01 <br>
 * Version : 1.0 <br>
 * Editor : Kevin<br>
 * Editor Date : 2017年01月30日<br>
 */

@Component("pms349")
@Scope("request")
public class PMS349 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(PMS349.class);

	/**
	 * 彈跳視窗查詢
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void getDetail(Object body, IPrimitiveMap header)
			throws JBranchException {
		PMS349InputVO inputVO = (PMS349InputVO) body;
		PMS349OutputVO return_VO = new PMS349OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam
				.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		try {
			// ==彈跳視窗查詢==
			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT ROWNUM, T.* ");
			sql.append(" FROM ( SELECT a.DATA_DATE, a.EMP_ID, a.AO_CODE, a.CUST_ID, a.FUND_AMT, ");
			sql.append(" a.LOSS_CAT, a.CUST_NAME, a.CON_DEGREE, a.AUM,a.DEP, a.FUND_LOSS, a.ETF_LOSS, ");
			sql.append(" a.ETF_AMT, a.BOND_LOSS, a.BOND_AMT, a.SI_LOSS, a.SI_AMT, a.SN_LOSS, ");
			sql.append(" a.CREATETIME, a.SN_AMT, a.DCI_LOSS, a.DCI_AMT, a.OT_LOSS, a.OT_AMT, ");
			sql.append(" a.SY_LOSS, a.SY_AMT, a.LY_LOSS,a.LY_AMT,a.STK_LOSS,a.STK_AMT ");
			sql.append(" ,NVL(FUND_AMT,0)-NVL(FUND_COST,0)+NVL(ETF_AMT,0)-NVL(ETF_COST,0)+NVL(BOND_AMT,0)-NVL(BOND_COST,0) ");
			sql.append(" +NVL(STK_AMT,0)-NVL(STK_COST,0)+NVL(SI_AMT,0)-NVL(SI_COST,0)+NVL(SN_AMT,0)-NVL(SN_COST,0) ");
			sql.append(" +NVL(DCI_AMT,0)-NVL(DCI_COST,0)+NVL(IV_AMT,0)-NVL(IV_COST,0)+NVL(OT_AMT,0)-NVL(OT_COST,0) ");
			sql.append(" +NVL(SY_AMT,0)-NVL(SY_COST,0)+NVL(LY_AMT,0)-NVL(LY_COST,0) TTL_RTN_AMT, ROR ");
			sql.append(" FROM TBPMS_CUST_LOSS_DTL a ) T ");
			sql.append(" WHERE 1=1 ");
			sql.append(" AND T.EMP_ID = :id ");
			sql.append(" AND T.AO_CODE= :AO_CDEO  ");
			sql.append(" AND T.LOSS_CAT= :LOSS_CATTT ");
			sql.append(" AND T.DATA_DATE=:DATA_DATEE");
			
			if(StringUtils.isNotBlank(inputVO.getCust_id())){
				sql.append(" AND T.CUST_ID=:CUST_IDD");
			}
			sql.append(" ORDER BY CON_DEGREE");
			queryCondition.setQueryString(sql.toString());
			//==必要查詢條件設定==
			queryCondition.setObject("id", inputVO.getEMP_ID());
			queryCondition.setObject("AO_CDEO", inputVO.getAO_CODE());
			queryCondition.setObject("LOSS_CATTT", inputVO.getLOSS_CAT());
			queryCondition.setObject("DATA_DATEE", inputVO.getDATA_DATE());
			
			if(StringUtils.isNotBlank(inputVO.getCust_id())){
				queryCondition.setObject("CUST_IDD", inputVO.getCust_id());
			}
			// 分頁結果result
			ResultIF list = dam.executePaging(queryCondition, inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
			List<Map<String,Object>> list1=dam.exeQuery(queryCondition);                
			
			
			int totalPage_i = list.getTotalPage();
			int totalRecord_i = list.getTotalRecord();
			return_VO.setResultList(list); // 查詢資訊
			return_VO.setCsvList(list1);
			return_VO.setCurrentPageIndex(inputVO.getCurrentPageIndex());// 當前頁次
			return_VO.setTotalPage(totalPage_i);// 總頁次
			return_VO.setTotalRecord(totalRecord_i);// 總筆數
			this.sendRtnObject(return_VO);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}

	private String checkIsNull(Map map, String key) {
		if (StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key) != null) {
			return String.valueOf(map.get(key));
		} else {
			if(key=="CUST_NAME")
			 return "";
			return "0";
		}
	}

	/**
	 * 匯出檔案
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void export(Object body, IPrimitiveMap header)
			throws JBranchException {
		// 取得畫面資料
		PMS349OutputVO return_VO = (PMS349OutputVO) body;

		List<Map<String, Object>> list = return_VO.getList();
		try {
			if (list.size() > 0) {
				// gen csv
				String.format("%1$,09d", -3123);
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
				String fileName = "客戶整體損益分析" + sdf.format(new Date())
						+"_"+(String) getUserVariable(FubonSystemVariableConsts.LOGINID)+".csv";
				List listCSV = new ArrayList();
				for (Map<String, Object> map : list) {
					// 17 column
					String[] records = new String[14];
					int i = 0;
					records[i] = checkIsNull(map, "DATA_DATE"); // 統計日期
					records[++i] = checkIsNull(map, "REGION_CENTER_ID"); // 區域中心ID
					records[++i] = checkIsNull(map, "BRANCH_AREA_ID"); // 營運區ID
					records[++i] = checkIsNull(map, "BRANCH_NBR"); // 分行代號
					records[++i] = checkIsNull(map, "BRANCH_NAME"); // 分行名稱
					records[++i] = checkIsNull(map, "AO_CODE"); // AO Code
					records[++i] = checkIsNull(map, "EMP_ID"); // 理專員編
					records[++i] = checkIsNull(map, "EMP_NAME"); // 理專姓名
					records[++i] = checkIsNull(map, "AO_JOB_RANK"); // 職級
					records[++i] = checkIsNull(map, "LV1"); // -10%(含)~-5%(不含)
					records[++i] = checkIsNull(map, "LV2"); // -5%(含)~0%(不含)
					records[++i] = checkIsNull(map, "LV3"); // 0%(含)~5%(不含)
					records[++i] = checkIsNull(map, "LV4"); // 5%(含)~10%(不含)
					records[++i] = checkIsNull(map, "LV5"); // 10%(含)~

					listCSV.add(records);
				}
				// header
				String[] csvHeader = new String[14];
				int j = 0;
				csvHeader[j] = "統計日期";
				csvHeader[++j] = "業務處ID";
				csvHeader[++j] = "營運區ID";
				csvHeader[++j] = "分行代號";
				csvHeader[++j] = "分行名稱";
				csvHeader[++j] = "AO Code";
				csvHeader[++j] = "理專員編";
				csvHeader[++j] = "理專姓名";
				csvHeader[++j] = "職級";
				csvHeader[++j] = "-5%以下";
				csvHeader[++j] = "-5%(含)~0%(不含)";
				csvHeader[++j] = "0%(含)~5%(不含)";
				csvHeader[++j] = "5%(含)~10%(不含)";
				csvHeader[++j] = "10%(含)~";

				CSVUtil csv = new CSVUtil();
				// 表頭欄位
				csv.setHeader(csvHeader);
				// 內容
				csv.addRecordList(listCSV);
				String url = csv.generateCSV();
				// download
				notifyClientToDownloadFile(url, fileName);
			} else
				return_VO.setResultList(list);
			this.sendRtnObject(null);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}

	/**
	 * 匯出明細
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void  detailexport(Object body, IPrimitiveMap header) throws JBranchException {
		PMS349OutputVO return_VO = (PMS349OutputVO) body;
		List<Map<String,Object>> list=return_VO.getList();
	
	     try {
			if(list.size()>0)
			 {
			  String.format("%1$,09d",-3123 );	 
			  SimpleDateFormat sdf=new SimpleDateFormat("yyyymmdd");
			  String fileName = "客戶整體損益分析明細" + sdf.format(new Date())
						+"_"+(String) getUserVariable(FubonSystemVariableConsts.LOGINID)+".csv";	            
			  List listCSV = new ArrayList();
			  for(Map<String,Object> map:list)
			  {
				String[] records=new String[29];
				int i = 0;
				
				records[i] = checkIsNull(map, "IDS"); // 
				records[++i] = checkIsNull(map, "CUST_NAME"); // 
				records[++i] = checkIsNull(map, "CON_DEGREE"); //  
				records[++i] = String.valueOf(new BigDecimal(checkIsNull(map, "AUM")).intValue()); // 
				records[++i] = String.valueOf(new BigDecimal(checkIsNull(map, "TTL_RTN_AMT")).intValue()); // 
				records[++i] = String.valueOf(new BigDecimal(checkIsNull(map, "DEP")).intValue()); // 
				records[++i] = checkIsNull(map, "FUND_LOSS")+"%"; //
				records[++i] = String.valueOf(new BigDecimal(checkIsNull(map, "FUND_AMT")).intValue()); // 
				records[++i] = checkIsNull(map, "STK_LOSS")+"%"; //
				records[++i] = String.valueOf(new BigDecimal(checkIsNull(map, "STK_AMT")).intValue()); // 
				records[++i] = checkIsNull(map, "ETF_LOSS")+"%"; //
				records[++i] = String.valueOf(new BigDecimal(checkIsNull(map, "ETF_AMT")).intValue()); // 
				records[++i] = checkIsNull(map, "BOND_LOSS")+"%"; //
				records[++i] = String.valueOf(new BigDecimal(checkIsNull(map, "BOND_AMT")).intValue()); // 
				records[++i] = checkIsNull(map, "SI_LOSS")+"%"; //
				records[++i] = String.valueOf(new BigDecimal(checkIsNull(map, "SI_AMT")).intValue()); // 
				records[++i] = checkIsNull(map, "SN_LOSS")+"%"; //
				records[++i] = String.valueOf(new BigDecimal(checkIsNull(map, "SN_AMT")).intValue()); // 
				records[++i] = checkIsNull(map, "DCI_LOSS")+"%"; //
				records[++i] = String.valueOf(new BigDecimal(checkIsNull(map, "DCI_AMT")).intValue()); // 

				// Mantis:0006403 拿掉保險相關數據
//				records[++i] = checkIsNull(map, "OT_LOSS")+"%"; //
//				records[++i] = String.valueOf(new BigDecimal(checkIsNull(map, "OT_AMT")).intValue()); //
//				records[++i] = checkIsNull(map, "SY_LOSS")+"%"; //
//				records[++i] = String.valueOf(new BigDecimal(checkIsNull(map, "SY_AMT")).intValue()); //
//				records[++i] = checkIsNull(map, "LY_LOSS")+"%"; //
//				records[++i] = String.valueOf(new BigDecimal(checkIsNull(map, "LY_AMT")).intValue()); //
//				records[++i] = checkIsNull(map, "IV_LOSS")+"%"; //
//				records[++i] = String.valueOf(new BigDecimal(checkIsNull(map, "IV_AMT")).intValue()); //
				records[++i] = checkIsNull(map, "ROR")+"%"; //
				listCSV.add(records);
			  }
			  String[] csvHeader=new String[29];
			  int j=0;
			  csvHeader[j]="客戶ID";
			  csvHeader[++j]="客戶姓名";
			  csvHeader[++j]="客戶分級";
			  csvHeader[++j]="上一日AUM餘額";
			  csvHeader[++j]="未實現損益";
			  csvHeader[++j]="存款";
			  csvHeader[++j]="基金投資報酬率";
			  csvHeader[++j]="基金投資現值";
			  csvHeader[++j]="海外股票投資報酬率";
			  csvHeader[++j]="海外股票投資現值";
			  csvHeader[++j]="海外ETF投資報酬率";
			  csvHeader[++j]="海外ETF投資現值";
			  csvHeader[++j]="海外債投資報酬率";
			  csvHeader[++j]="海外債投資現值";
			  csvHeader[++j]="SI投資報酬率";
			  csvHeader[++j]="SI投資現值";
			  csvHeader[++j]="SN投資報酬率";
			  csvHeader[++j]="SN投資現值";
			  csvHeader[++j]="DCI投資報酬率";
			  csvHeader[++j]="DCI投資現值";
			  // Mantis:0006403 拿掉保險相關數據
//			  csvHeader[++j]="躉繳保險報酬率";
//			  csvHeader[++j]="躉繳保險現值";
//			  csvHeader[++j]="短年期保險報酬率";
//			  csvHeader[++j]="短年期保險現值";
//			  csvHeader[++j]="長年期保險報酬率";
//			  csvHeader[++j]="長年期保險現值";
//			  csvHeader[++j]="投資型保險報酬率";
//			  csvHeader[++j]="投資型保險現值";
			  csvHeader[++j]="總報酬";
			  CSVUtil csv = new CSVUtil();
			  csv.setHeader(csvHeader);
			  csv.addRecordList(listCSV);
			  String url = csv.generateCSV();
			  notifyClientToDownloadFile(url, fileName);
			 
			 }else
			return_VO.setResultList(list);
			 this.sendRtnObject(null);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	     	 
	    	 
}
	/**
	 * 主查詢
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 * @throws ParseException 
	 */
	public void inquire(Object body, IPrimitiveMap header)
			throws JBranchException, ParseException {
		PMS349InputVO inputVO = (PMS349InputVO) body;
		PMS349OutputVO outputVO = new PMS349OutputVO();
		dam = this.getDataAccessManager();
		
		
		String roleID = (String) getUserVariable(FubonSystemVariableConsts.LOGINROLE);
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> fcMap = xmlInfo.doGetVariable("FUBONSYS.FC_ROLE", FormatHelper.FORMAT_2);         //理專
		Map<String, String> psopMap = xmlInfo.doGetVariable("FUBONSYS.PSOP_ROLE", FormatHelper.FORMAT_2);     //OP
		Map<String, String> bmmgrMap = xmlInfo.doGetVariable("FUBONSYS.BMMGR_ROLE", FormatHelper.FORMAT_2);    //個金主管
		Map<String, String> mbrmgrMap = xmlInfo.doGetVariable("FUBONSYS.MBRMGR_ROLE", FormatHelper.FORMAT_2);   //營運督導
		Map<String, String> armgrMap = xmlInfo.doGetVariable("FUBONSYS.ARMGR_ROLE", FormatHelper.FORMAT_2);   //區域中心主管
		Map<String, String> headmgrMap = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2); //總行人員
		
		//取得查詢資料可視範圍
				PMS000 pms000 = (PMS000) PlatformContext.getBean("pms000");
				PMS000InputVO pms000InputVO = new PMS000InputVO();
				pms000InputVO.setReportDate(inputVO.getReportDate());
				PMS000OutputVO pms000outputVO = pms000.getOrg(pms000InputVO);
		
		QueryConditionIF condition = dam
				.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		try {
			// BY WILLIS 2017/10/25 增加客戶id查詢CR需求
			if (StringUtils.isBlank(inputVO.getCust_id())) {
			    sql.append("SELECT mast.* FROM TBPMS_CUST_LOSS_MAST mast ");
			    sql.append("LEFT JOIN VWORG_DEFN_INFO ORG ");
				sql.append("ON ORG.BRANCH_NBR=mast.BRANCH_NBR ");
				sql.append("WHERE 1=1  and ao_code<>'000' ");
			}
			else{
				sql.append("SELECT mast.DATA_DATE,mast.REGION_CENTER_ID,mast.BRANCH_AREA_ID,mast.BRANCH_NBR,mast.BRANCH_NAME,mast.AO_CODE,mast.EMP_ID,mast.EMP_NAME,mast.AO_JOB_RANK ");
				sql.append(",CASE WHEN dtl.LOSS_CAT ='1' THEN 1 ELSE 0  END LV1 ");
				sql.append(",CASE WHEN dtl.LOSS_CAT ='2' THEN 1 ELSE 0  END LV2 ");
				sql.append(",CASE WHEN dtl.LOSS_CAT ='3' THEN 1 ELSE 0  END LV3 ");
				sql.append(",CASE WHEN dtl.LOSS_CAT ='4' THEN 1 ELSE 0  END LV4 ");
				sql.append(",CASE WHEN dtl.LOSS_CAT ='5' THEN 1 ELSE 0  END LV5 ");
				sql.append(",dtl.CUST_ID ");
				sql.append("FROM TBPMS_CUST_LOSS_MAST mast ");
				// 應抓最新的組織去對應 問題單:4064 by 2017/12/15 willis
				sql.append("LEFT JOIN VWORG_DEFN_INFO ORG ");
				sql.append("ON ORG.BRANCH_NBR=mast.BRANCH_NBR ");
				sql.append("INNER JOIN TBPMS_CUST_LOSS_DTL dtl ON dtl.DATA_DATE = mast.DATA_DATE and dtl.AO_CODE=mast.AO_CODE ");
				sql.append("WHERE 1=1  and mast.ao_code<>'000' ");
				sql.append("and dtl.CUST_ID=:CUST_IDD ");
				sql.append("and dtl.LOSS_CAT <> '0' ");
				condition.setObject("CUST_IDD",inputVO.getCust_id());
			}
			// ==查詢條件設定==
			// 區域中心
			if (!StringUtils.isBlank(inputVO.getRegion_center_id())) {
				sql.append(" and ORG.REGION_CENTER_ID = :REGION_CENTER_IDD");
				condition.setObject("REGION_CENTER_IDD", inputVO.getRegion_center_id());
			}else{
				//登入非總行人員強制加區域中心
				if(!headmgrMap.containsKey(roleID)) {
					sql.append("and ORG.REGION_CENTER_ID IN (:region_center_id) ");
					condition.setObject("region_center_id", pms000outputVO.getV_regionList());
				}
			}
			
			// 營運區
			if (!StringUtils.isBlank(inputVO.getBranch_area_id())) {
				sql.append(" and ORG.BRANCH_AREA_ID = :BRANCH_AREA_IDD ");
				condition.setObject("BRANCH_AREA_IDD", inputVO.getBranch_area_id());
			}else{
				//登入非總行人員強制加營運區
				if(!headmgrMap.containsKey(roleID)) {
					sql.append("  and ORG.BRANCH_AREA_ID IN (:branch_area_id) ");
					condition.setObject("branch_area_id", pms000outputVO.getV_areaList());
				}
			}
			
			// 分行
			if (!StringUtils.isBlank(inputVO.getBranch_nbr())) {
				sql.append(" and ORG.BRANCH_NBR = :BRNCH_NBRR ");
				condition.setObject("BRNCH_NBRR", inputVO.getBranch_nbr());
			}else{
				//登入非總行人員強制加分行
				if(!headmgrMap.containsKey(roleID)) {		
					sql.append("  and ORG.BRANCH_NBR IN (:branch_nbr) ");
					condition.setObject("branch_nbr", pms000outputVO.getV_branchList());
				}
			}
							
			// 理專
			if (!StringUtils.isBlank(inputVO.getAo_code())) {
				sql.append(" and mast.AO_CODE LIKE :AO_CODEE ");
			}
			// 日期
			if (inputVO.geteCreDate() != null) {
				sql.append(" and mast.DATA_DATE LIKE :DATA_DATEe ");
			}
			
			
			// 理專
			if (!StringUtils.isBlank(inputVO.getAo_code())) {
				condition.setObject("AO_CODEE", "%" + inputVO.getAo_code()
						+ "%");
			}else{
				//登入為銷售人員強制加AO_CODE
				if(fcMap.containsKey(roleID) || psopMap.containsKey(roleID)) {
					sql.append(" and mast.AO_CODE IN (:ao_code) ");
					condition.setObject("ao_code", pms000outputVO.getV_aoList());
				}
			}
			
			// 起日
			if (inputVO.geteCreDate() != null) {
				SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd");
				String aa = sdf1.format(inputVO.geteCreDate());
				condition.setObject("DATA_DATEe", aa);
			}
			
			sql.append(" order by mast.BRANCH_NBR,mast.AO_JOB_RANK,mast.AO_CODE");
			condition.setQueryString(sql.toString());
			// 分頁資訊
			ResultIF list = dam.executePaging(condition,
					inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
			List<Map<String, Object>> list1 = dam.exeQuery(condition);
			int totalPage_i = list.getTotalPage(); // 分頁用
			int totalRecord_i = list.getTotalRecord(); // 分頁用
			outputVO.setResultList(list); // data
			outputVO.setCsvList(list1);
			outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());// 當前頁次
			outputVO.setTotalPage(totalPage_i);// 總頁次
			outputVO.setTotalRecord(totalRecord_i);// 總筆數
			this.sendRtnObject(outputVO);

		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}

}