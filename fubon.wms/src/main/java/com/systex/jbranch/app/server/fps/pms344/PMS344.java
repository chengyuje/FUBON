package com.systex.jbranch.app.server.fps.pms344;

import java.sql.Timestamp;
import java.text.DecimalFormat;
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
 * Description :投資商品TOP10排名<br>
 * Comments Name : PMS344.java<br>
 * Author : Kevin<br>
 * Date :2016/06/23 <br>
 * Version : 1.0 <br>
 * Editor : Kevin<br>
 * Editor Date : 2017年01月30日<br>
 */

@Component("pms344")
@Scope("request")
public class PMS344 extends FubonWmsBizLogic {
	public DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(PMS344.class);

	/**
	 * 主查詢 
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void queryData(Object body, IPrimitiveMap header)
			throws JBranchException, ParseException  {
		PMS344InputVO inputVO = (PMS344InputVO) body;
		PMS344OutputVO outputVO = new PMS344OutputVO();
		
		
		String roleType = "";
		String roleID = (String) getUserVariable(FubonSystemVariableConsts.LOGINROLE);
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> fcMap = xmlInfo.doGetVariable("FUBONSYS.FC_ROLE", FormatHelper.FORMAT_2);         //理專
		Map<String, String> psopMap = xmlInfo.doGetVariable("FUBONSYS.PSOP_ROLE", FormatHelper.FORMAT_2);     //OP
		Map<String, String> bmmgrMap = xmlInfo.doGetVariable("FUBONSYS.BMMGR_ROLE", FormatHelper.FORMAT_2);    //個金主管
		Map<String, String> mbrmgrMap = xmlInfo.doGetVariable("FUBONSYS.MBRMGR_ROLE", FormatHelper.FORMAT_2);   //營運督導
		Map<String, String> armgrMap = xmlInfo.doGetVariable("FUBONSYS.ARMGR_ROLE", FormatHelper.FORMAT_2);   //業務處主管
		Map<String, String> headmgrMap = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2); //總行人員
			
		//取得查詢資料可視範圍
		PMS000 pms000 = (PMS000) PlatformContext.getBean("pms000");
		PMS000InputVO pms000InputVO = new PMS000InputVO();
		pms000InputVO.setReportDate(inputVO.getReportDate());
		PMS000OutputVO pms000outputVO = pms000.getOrg(pms000InputVO);

		dam = this.getDataAccessManager();
		QueryConditionIF condition1 = dam
				.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		String selectOrg ="";
		
		if(StringUtils.equals(inputVO.getElement(), "TBPMS_TOP10_STOCK_AO") || StringUtils.equals(inputVO.getElement(), "TBPMS_TOP10_SALE_AO") || StringUtils.equals(inputVO.getElement(), "TBPMS_TOP10_RDMP_AO")){
			selectOrg="AO";
		}else if(StringUtils.equals(inputVO.getElement(), "TBPMS_TOP10_STOCK_BR") || StringUtils.equals(inputVO.getElement(), "TBPMS_TOP10_SALE_BR") || StringUtils.equals(inputVO.getElement(), "TBPMS_TOP10_RDMP_BR")){
			selectOrg="BR";
		}

		StringBuffer sql1 = new StringBuffer();
		try {
			// ==主查詢==
			// 依照各張表查詢結果 Element
			sql1.append("SELECT ROWNUM as num, A.* " + "FROM ( " + " SELECT T.* "
					+ " FROM " + inputVO.getElement() + " T ");
			
			// TBPMS_TOP10_STOCK_OP與TBPMS_TOP10_STOCK_RC 因為以滾算到營運區跟業務處，無法再與VWORG_DEFN_INFO做串聯。BY 20180122-willis 
			if("AO".equals(selectOrg)||"BR".equals(selectOrg)){	
			// 應抓最新的組織去對應 問題單:4064 by 2017/12/15 willis	
			sql1.append(" LEFT JOIN VWORG_DEFN_INFO ORG ");
			sql1.append(" ON ORG.BRANCH_NBR=T.BRANCH_NBR ");
			}
			sql1.append(" WHERE 1=1 ");
			// ==主查詢條件==
			if (inputVO.getsCreDate() != null) {
				sql1.append(" and T.DATA_DATE like :DATA_DATEE  "); // 年月
			}
			if (!StringUtils.isBlank(inputVO.getBranch_nbr())) {
				sql1.append(" and ORG.BRANCH_NBR like :BRANCH_IDEE  "); // 分行
			}else{
				//登入非總行人員強制加分行
				if(!headmgrMap.containsKey(roleID) ) {	
					if(!(roleID.equals("A164") || roleID.equals("A146"))){
						sql1.append("  and ORG.BRANCH_NBR IN (:branch_nbr) ");
						condition1.setObject("branch_nbr", pms000outputVO.getV_branchList());	
					}
				}
			}
			// 業務處
			if (!StringUtils.isBlank(inputVO.getRegion_center_id())) {
				if("AO".equals(selectOrg)||"BR".equals(selectOrg))
				sql1.append(" and ORG.REGION_CENTER_ID LIKE :REGION_CENTER_IDDD ");
				else{
				sql1.append(" and REGION_CENTER_ID LIKE :REGION_CENTER_IDDD ");	
				};
			}else{
				//登入非總行人員強制加業務處
				if(!headmgrMap.containsKey(roleID)) {
					if("TBPMS_TOP10_STOCK_AO".equals(inputVO.getElement()) || "TBPMS_TOP10_STOCK_BR".equals(inputVO.getElement())){
						sql1.append(" and ORG.REGION_CENTER_ID in (:region_center_id ) ");
					}else{
						sql1.append(" and REGION_CENTER_ID LIKE in (:region_center_id ) ");	
						};
					condition1.setObject("region_center_id", pms000outputVO.getV_regionList());
				}
			}

			if (!StringUtils.isBlank(inputVO.getBranch_area_id())) {
				if("AO".equals(selectOrg)||"BR".equals(selectOrg)){
				sql1.append(" and ORG.BRANCH_AREA_ID  like :OP_AREA_IDEEE "); // 營運區
				}else{
				sql1.append(" and BRANCH_AREA_ID  like :OP_AREA_IDEEE "); // 營運區	
				}
			}else{
				//登入非總行人員強制加營運區
				if(!headmgrMap.containsKey(roleID) ) {
					if(!roleID.equals("A164")){
						if("AO".equals(selectOrg)||"BR".equals(selectOrg)){
							sql1.append(" and ORG.BRANCH_AREA_ID  in (:branch_area_id ) "); // 營運區
							}else{
							sql1.append(" and BRANCH_AREA_ID  in (:branch_area_id ) "); // 營運區	
							}
						condition1.setObject("branch_area_id", pms000outputVO.getV_areaList());
					}
				}
			}

			if (!StringUtils.isBlank(inputVO.getAo_code())) {
				sql1.append(" and T.AO_CODE  like :AO_CODEEE "); // 理專
			}else{
				//登入為銷售人員強制加AO_CODE
				if(fcMap.containsKey(roleID) || psopMap.containsKey(roleID)) {
					if(!(roleID.equals("A164") || roleID.equals("A146"))){
						sql1.append(" and T.AO_CODE IN (:ao_code) ");
						condition1.setObject("ao_code", pms000outputVO.getV_aoList());
					}
				}
			}
			
			String cost_type = "";
			if("STOCK".equals(inputVO.getElement().substring(12,17)))
				cost_type = "INV_COST_NTD";
			if("SALE_".equals(inputVO.getElement().substring(12,17)))
				cost_type = "INV_AMT_NTD";
			if("RDMP_".equals(inputVO.getElement().substring(12,17)))
				cost_type = "RDMP_COST_NTD";
			
			if(StringUtils.equals(inputVO.getElement(), "TBPMS_TOP10_STOCK_AO") || StringUtils.equals(inputVO.getElement(), "TBPMS_TOP10_SALE_AO") || StringUtils.equals(inputVO.getElement(), "TBPMS_TOP10_RDMP_AO"))
				sql1.append("  order by " + cost_type + " desc , T.REGION_CENTER_ID, T.BRANCH_AREA_ID, T.BRANCH_NBR, AO_CODE, SRANK   ");
			else if(StringUtils.equals(inputVO.getElement(), "TBPMS_TOP10_STOCK_BR") || StringUtils.equals(inputVO.getElement(), "TBPMS_TOP10_SALE_BR") || StringUtils.equals(inputVO.getElement(), "TBPMS_TOP10_RDMP_BR"))
				sql1.append("  order by " + cost_type + " desc , T.REGION_CENTER_ID, T.BRANCH_AREA_ID, T.BRANCH_NBR, SRANK  ");
			else if(StringUtils.equals(inputVO.getElement(), "TBPMS_TOP10_STOCK_OP") || StringUtils.equals(inputVO.getElement(), "TBPMS_TOP10_SALE_OP") || StringUtils.equals(inputVO.getElement(), "TBPMS_TOP10_RDMP_OP"))
				sql1.append("  order by " + cost_type + " desc , T.REGION_CENTER_ID, T.BRANCH_AREA_ID, SRANK  ");
			else if(StringUtils.equals(inputVO.getElement(), "TBPMS_TOP10_STOCK_RC") || StringUtils.equals(inputVO.getElement(), "TBPMS_TOP10_SALE_RC") || StringUtils.equals(inputVO.getElement(), "TBPMS_TOP10_RDMP_RC"))
				sql1.append("  order by " + cost_type + " desc , REGION_CENTER_ID,SRANK  ");
			
			// 設定查詢字串   增加TOP10 增加條件小於10
			sql1.append(" ) A WHERE 1=1 AND ROWNUM<11 ");
			
			
			condition1.setQueryString(sql1.toString());
			// ==主查詢條件設定==
			// 分行
			if (!StringUtils.isBlank(inputVO.getBranch_nbr())) {
				condition1.setObject("BRANCH_IDEE", "%" + inputVO.getBranch_nbr()
						+ "%");
			}
			// 營運區
			if (!StringUtils.isBlank(inputVO.getBranch_area_id())) {
				condition1.setObject("OP_AREA_IDEEE", "%" + inputVO.getBranch_area_id()
						+ "%");
			}
			// 業務處
			if (!StringUtils.isBlank(inputVO.getRegion_center_id())) {
				condition1.setObject("REGION_CENTER_IDDD",
						"%" + inputVO.getRegion_center_id() + "%");
			}
			// 理專ao_code
			if (!StringUtils.isBlank(inputVO.getAo_code())) {
				condition1.setObject("AO_CODEEE", "%" + inputVO.getAo_code()
						+ "%");
			}
			// 日期
			if (inputVO.getsCreDate() != null) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
				condition1.setObject("DATA_DATEE",sdf.format(inputVO.getsCreDate()));
			}
			//TOP 永遠前10筆
			inputVO.setCurrentPageIndex(0);
			// 分頁查詢結果
			ResultIF list = dam.executePaging(condition1,
					inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
			// csv全部查詢結果
			List<Map<String, Object>> csvList = dam.exeQuery(condition1);

			if (list != null) {
				int totalPage_i = list.getTotalPage(); // 分頁用
				int totalRecord_i = list.getTotalRecord(); // 分頁用
				outputVO.setResultList(list); // 分頁data
				outputVO.setResultList2(csvList); // csv 匯出資訊
				outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());// 當前頁次
				outputVO.setTotalPage(totalPage_i);// 總頁次
				outputVO.setTotalRecord(totalRecord_i);// 總筆數
				this.sendRtnObject(outputVO);
			}
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
	 */
	public void queryDataDate(Object body, IPrimitiveMap header)
			throws JBranchException, ParseException  
	{
		PMS344InputVO inputVO = (PMS344InputVO) body;
		PMS344OutputVO outputVO = new PMS344OutputVO();
		dam = this.getDataAccessManager();
		
		try {
			QueryConditionIF condition1 = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);					
			StringBuffer sql = new StringBuffer();
			sql.append("		SELECT DISTINCT                                                                                                  ");
			sql.append("			   TO_CHAR(PABTH_UTIL.FC_getBusiDay(TO_date(WEEK_END_DATE,'yyyymmdd')+1, 'TWD', -1),'YYYY/MM/DD') AS LABEL,  ");      
			sql.append("			   PABTH_UTIL.FC_getBusiDay(TO_date(WEEK_END_DATE,'yyyymmdd')+1, 'TWD', -1) AS DATA                          ");      
			sql.append("	    FROM TBPMS_DATE_REC                                                                                              ");
			sql.append("        WHERE TO_date(WEEK_END_DATE,'yyyymmdd') < TO_DATE(TO_CHAR(SYSDATE,'YYYYMMDD'),'YYYYMMDD')                        ");
			sql.append("	    ORDER BY LABEL DESC                                                                                              ");

			condition1.setQueryString(sql.toString());
			// csv全部查詢結果
			List<Map<String, Object>> list = dam.exeQuery(condition1);

			if (list != null) {
				outputVO.setDateList(list); // date   data
				this.sendRtnObject(outputVO);
			}
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}
	

	public void export(Object body, IPrimitiveMap header)
			throws JBranchException {
		// 取得畫面資料
		PMS344OutputVO return_VO = (PMS344OutputVO) body;

		List<Map<String, Object>> list = return_VO.getList();
		try {
			if (list.size() > 0) {
				// gen csv
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
				List listCSV = new ArrayList();
				if ("TBPMS_TOP10_SALE_AO".equals(return_VO.getElement()
						.toString())) {
					String fileName = "理專銷售TOP10排名" + sdf.format(new Date())
							+"_"+ (String) getUserVariable(FubonSystemVariableConsts.LOGINID)+".csv";
					for (Map<String, Object> map : list) {
						// 21 column
						String[] records = new String[15];
						int i = 0;
						records[i] = checkIsNull(map, "DATA_DATE"); // 資料日期
						records[++i] = checkIsNullAndTrans(map, "REGION_CENTER_ID"); // 業務處ID
						records[++i] = checkIsNull(map, "REGION_CENTER_NAME"); // 業務處名稱
						records[++i] = checkIsNullAndTrans(map, "BRANCH_AREA_ID"); // 營運區ID
						records[++i] = checkIsNull(map, "BRANCH_AREA_NAME"); // 營運區名稱
						records[++i] = checkIsNullAndTrans(map, "BRANCH_NBR"); // 分行代碼
						records[++i] = checkIsNull(map, "BRANCH_NAME"); // 分行名稱
						records[++i] = checkIsNullAndTrans(map, "BRANCH_CLS"); // 組別
						records[++i] = checkIsNullAndTrans(map, "AO_JOB_RANK"); // 理專職級代碼
//						records[++i] = checkIsNull(map, "AO_JOB_NAME"); // 理專職級名稱
						records[++i] = checkIsNullAndTrans(map, "AO_CODE"); // AO Code
						records[++i] = checkIsNullAndTrans(map, "EMP_ID"); // 理專員編
						records[++i] = checkIsNull(map, "EMP_NAME"); // 理專姓名
						records[++i] = checkIsNullAndTrans(map, "PRD_ID"); // 產品代號
						records[++i] = checkIsNull(map, "PRD_NAME"); // 產品名稱
						records[++i] = checkIsNull(map, "INV_AMT_NTD"); // 投資金額(台幣)
						listCSV.add(records);
					}
					String[] csvHeader = new String[15];
					int j = 0;
					csvHeader[j] = "資料日期";
					csvHeader[++j] = "業務處ID";
					csvHeader[++j] = "業務處名稱";
					csvHeader[++j] = "營運區ID";
					csvHeader[++j] = "營運區名稱";
					csvHeader[++j] = "分行代碼";
					csvHeader[++j] = "分行名稱";
					csvHeader[++j] = "組別";
					csvHeader[++j] = "理專職級代碼";
//					csvHeader[++j] = "理專職級名稱";
					csvHeader[++j] = "AO Code";
					csvHeader[++j] = "理專員編";
					csvHeader[++j] = "理專姓名";
					csvHeader[++j] = "產品代號";
					csvHeader[++j] = "產品名稱";
					csvHeader[++j] = "投資金額(台幣)";
					CSVUtil csv = new CSVUtil();
					csv.setHeader(csvHeader); // 設定標頭
					csv.addRecordList(listCSV); // 設定內容
					String url = csv.generateCSV();
					// download
					notifyClientToDownloadFile(url, fileName);
				}

				if ("TBPMS_TOP10_SALE_BR".equals(return_VO.getElement()
						.toString())) {
					String fileName = "分行銷售TOP10排名" + sdf.format(new Date())
							+"_"+  (String) getUserVariable(FubonSystemVariableConsts.LOGINID)+".csv";
					for (Map<String, Object> map : list) {

						String[] records = new String[10];
						int i = 0;
						records[i] = checkIsNull(map, "DATA_DATE"); // 資料日期
						records[++i] = checkIsNullAndTrans(map, "REGION_CENTER_ID"); // 業務處ID
						records[++i] = checkIsNull(map, "REGION_CENTER_NAME"); // 業務處名稱
						records[++i] = checkIsNullAndTrans(map, "BRANCH_AREA_ID"); // 營運區ID
						records[++i] = checkIsNull(map, "BRANCH_AREA_NAME"); // 營運區名稱
						records[++i] = checkIsNullAndTrans(map, "BRANCH_NBR"); // 分行代碼
						records[++i] = checkIsNull(map, "BRANCH_NAME"); // 分行名稱
						records[++i] = checkIsNullAndTrans(map, "PRD_ID"); // 產品代號
						records[++i] = checkIsNull(map, "PRD_NAME"); // 產品名稱
						records[++i] = checkIsNull(map, "INV_AMT_NTD"); // 投資金額(台幣)

						// 組別名稱
						listCSV.add(records);
					}

					String[] csvHeader = new String[10];
					int j = 0;
					csvHeader[j] = "資料日期";
					csvHeader[++j] = "業務處ID";
					csvHeader[++j] = "業務處名稱";
					csvHeader[++j] = "營運區ID";
					csvHeader[++j] = "營運區名稱";
					csvHeader[++j] = "分行代碼";
					csvHeader[++j] = "分行名稱";
					csvHeader[++j] = "產品代號";
					csvHeader[++j] = "產品名稱";
					csvHeader[++j] = "投資金額(台幣)";

					CSVUtil csv = new CSVUtil();
					csv.setHeader(csvHeader); // 設定標頭
					csv.addRecordList(listCSV); // 設定內容
					String url = csv.generateCSV();
					// download
					notifyClientToDownloadFile(url, fileName);
				}

				if ("TBPMS_TOP10_SALE_OP".equals(return_VO.getElement()
						.toString())) {
					String fileName = "營運區銷售TOP10排名" + sdf.format(new Date())
							+"_"+  (String) getUserVariable(FubonSystemVariableConsts.LOGINID)+".csv";
					for (Map<String, Object> map : list) {

						String[] records = new String[8];
						int i = 0;
						records[i] = checkIsNull(map, "DATA_DATE"); // 資料日期
						records[++i] = checkIsNullAndTrans(map, "REGION_CENTER_ID"); // 業務處ID
						records[++i] = checkIsNull(map, "REGION_CENTER_NAME"); // 業務處名稱
						records[++i] = checkIsNullAndTrans(map, "BRANCH_AREA_ID"); // 營運區ID
						records[++i] = checkIsNull(map, "BRANCH_AREA_NAME"); // 營運區名稱
						records[++i] = checkIsNullAndTrans(map, "PRD_ID"); // 產品代號
						records[++i] = checkIsNull(map, "PRD_NAME"); // 產品名稱
						records[++i] = checkIsNull(map, "INV_AMT_NTD"); // 投資金額(台幣)
						// 組別名稱
						listCSV.add(records);
					}

					String[] csvHeader = new String[8];
					int j = 0;
					csvHeader[j] = "資料日期";
					csvHeader[++j] = "業務處ID";
					csvHeader[++j] = "業務處名稱";
					csvHeader[++j] = "營運區ID";
					csvHeader[++j] = "營運區名稱";
					csvHeader[++j] = "產品代號";
					csvHeader[++j] = "產品名稱";
					csvHeader[++j] = "投資金額(台幣)";

					CSVUtil csv = new CSVUtil();
					csv.setHeader(csvHeader); // 設定標頭
					csv.addRecordList(listCSV); // 設定內容
					String url = csv.generateCSV();
					// download
					notifyClientToDownloadFile(url, fileName);
				}

				if ("TBPMS_TOP10_SALE_RC".equals(return_VO.getElement()
						.toString())) {
					String fileName = "業務處銷售TOP10排名" + sdf.format(new Date())
							+"_"+  (String) getUserVariable(FubonSystemVariableConsts.LOGINID)+".csv";
					for (Map<String, Object> map : list) {

						String[] records = new String[6];
						int i = 0;
						records[i] = checkIsNull(map, "DATA_DATE"); // 資料日期
						records[++i] = checkIsNullAndTrans(map, "REGION_CENTER_ID"); // 業務處ID
						records[++i] = checkIsNull(map, "REGION_CENTER_NAME"); // 業務處名稱
						records[++i] = checkIsNullAndTrans(map, "PRD_ID"); // 產品代號
						records[++i] = checkIsNull(map, "PRD_NAME"); // 產品名稱
						records[++i] = currencyFormat(map, "INV_AMT_NTD"); // 投資金額(台幣)

						// 組別名稱
						listCSV.add(records);
					}

					String[] csvHeader = new String[6];
					int j = 0;
					csvHeader[j] = "資料日期";
					csvHeader[++j] = "業務處ID";
					csvHeader[++j] = "業務處名稱";
					csvHeader[++j] = "產品代號";
					csvHeader[++j] = "產品名稱";
					csvHeader[++j] = "投資金額(台幣)";

					CSVUtil csv = new CSVUtil();
					csv.setHeader(csvHeader); // 設定標頭
					csv.addRecordList(listCSV); // 設定內容
					String url = csv.generateCSV();
					// download
					notifyClientToDownloadFile(url, fileName);
				}

				if ("TBPMS_TOP10_STOCK_AO".equals(return_VO.getElement()
						.toString())) {
					String fileName = "理專庫存TOP10排名" + sdf.format(new Date())
							+"_"+  (String) getUserVariable(FubonSystemVariableConsts.LOGINID)+".csv";
					for (Map<String, Object> map : list) {
						// 21 column
						String[] records = new String[20];
						int i = 0;
						records[i] = checkIsNull(map, "DATA_DATE"); // 資料日期
						records[++i] = checkIsNullAndTrans(map, "REGION_CENTER_ID"); // 業務處ID
						records[++i] = checkIsNull(map, "REGION_CENTER_NAME"); // 業務處名稱
						records[++i] = checkIsNullAndTrans(map, "BRANCH_AREA_ID"); // 營運區ID
						records[++i] = checkIsNull(map, "BRANCH_AREA_NAME"); // 營運區名稱
						records[++i] = checkIsNullAndTrans(map, "BRANCH_NBR"); // 分行代碼
						records[++i] = checkIsNull(map, "BRANCH_NAME"); // 分行名稱
						records[++i] = checkIsNull(map, "BRANCH_CLS"); // 組別

						records[++i] = checkIsNull(map, "AO_JOB_RANK"); // 理專職級代碼
//						records[++i] = checkIsNull(map, "AO_JOB_NAME"); // 理專職級名稱
						records[++i] = checkIsNullAndTrans(map, "AO_CODE"); // AO Code
						records[++i] = checkIsNullAndTrans(map, "EMP_ID"); // 理專員編
						records[++i] = checkIsNull(map, "EMP_NAME"); // 理專姓名
						records[++i] = checkIsNullAndTrans(map, "PRD_ID"); // 產品代號
						records[++i] = checkIsNull(map, "PRD_NAME"); // 產品名稱
						records[++i] = currencyFormat(map, "INV_COST_NTD"); // 投資成本(台幣)
						records[++i] = currencyFormat(map, "INV_VALU_NTD"); // 投資現值(台幣)
						records[++i] = currencyFormat(map, "DIFF_COST_NTD"); // 成本與上週差異(台幣)
						records[++i] = currencyFormat(map, "DIFF_VALU_NTD"); // 現值與上週差異(台幣)
						records[++i] = checkIsNull(map, "SRANK"); // 排名
						System.out.println(currencyFormat2(map, "ROR"));
						records[++i] = currencyFormat2(map, "ROR"); // 報酬率

						listCSV.add(records);
					}
					String[] csvHeader = new String[20];
					int j = 0;
					csvHeader[j] = "資料日期";
					csvHeader[++j] = "業務處ID";
					csvHeader[++j] = "業務處名稱";
					csvHeader[++j] = "營運區ID";
					csvHeader[++j] = "營運區名稱";
					csvHeader[++j] = "分行代碼";
					csvHeader[++j] = "分行名稱";
					csvHeader[++j] = "組別";

					csvHeader[++j] = "理專職級代碼";
//					csvHeader[++j] = "理專職級名稱";
					csvHeader[++j] = "AO Code";
					csvHeader[++j] = "理專員編";
					csvHeader[++j] = "理專姓名";
					csvHeader[++j] = "產品代號";
					csvHeader[++j] = "產品名稱";
					csvHeader[++j] = "投資成本(台幣)";
					csvHeader[++j] = "投資現值(台幣)";
					csvHeader[++j] = "成本與上週差異(台幣)";
					csvHeader[++j] = "現值與上週差異(台幣)";
					csvHeader[++j] = "排名";
					csvHeader[++j] = "報酬率(%)";

					CSVUtil csv = new CSVUtil();
					csv.setHeader(csvHeader); // 設定標頭
					csv.addRecordList(listCSV); // 設定內容
					String url = csv.generateCSV();
					// download
					notifyClientToDownloadFile(url, fileName);
				}

				if ("TBPMS_TOP10_STOCK_BR".equals(return_VO.getElement()
						.toString())) {
					String fileName = "分行庫存TOP10排名" + sdf.format(new Date())
							+"_"+ (String) getUserVariable(FubonSystemVariableConsts.LOGINID)+ ".csv";
					for (Map<String, Object> map : list) {
						// 21 column
						String[] records = new String[15];
						int i = 0;
						records[i] = checkIsNull(map, "DATA_DATE"); // 資料日期
						records[++i] = checkIsNullAndTrans(map, "REGION_CENTER_ID"); // 業務處ID
						records[++i] = checkIsNull(map, "REGION_CENTER_NAME"); // 業務處名稱
						records[++i] = checkIsNullAndTrans(map, "BRANCH_AREA_ID"); // 營運區ID
						records[++i] = checkIsNull(map, "BRANCH_AREA_NAME"); // 營運區名稱
						records[++i] = checkIsNullAndTrans(map, "BRANCH_NBR"); // 分行代碼
						records[++i] = checkIsNull(map, "BRANCH_NAME"); // 分行名稱
						records[++i] = checkIsNullAndTrans(map, "PRD_ID"); // 產品代號
						records[++i] = checkIsNull(map, "PRD_NAME"); // 產品名稱
						records[++i] = currencyFormat(map, "INV_COST_NTD"); // 投資成本(台幣)
						records[++i] = currencyFormat(map, "INV_VALU_NTD"); // 投資現值(台幣)
						records[++i] = currencyFormat(map, "DIFF_COST_NTD"); // 成本與上週差異(台幣)
						records[++i] = currencyFormat(map, "DIFF_VALU_NTD"); // 現值與上週差異(台幣)
						records[++i] = checkIsNull(map, "SRANK"); // 排名
						records[++i] = checkIsNull(map, "ROR"); // 報酬率

						// 組別名稱 //組別名稱
						listCSV.add(records);
					}
					String[] csvHeader = new String[15];
					int j = 0;
					csvHeader[j] = "資料日期";
					csvHeader[++j] = "業務處ID";
					csvHeader[++j] = "業務處名稱";
					csvHeader[++j] = "營運區ID";
					csvHeader[++j] = "營運區名稱";
					csvHeader[++j] = "分行代碼";
					csvHeader[++j] = "分行名稱";
					csvHeader[++j] = "產品代號";
					csvHeader[++j] = "產品名稱";
					csvHeader[++j] = "投資成本(台幣)";
					csvHeader[++j] = "投資現值(台幣)";
					csvHeader[++j] = "成本與上週差異(台幣)";
					csvHeader[++j] = "現值與上週差異(台幣)";
					csvHeader[++j] = "排名";
					csvHeader[++j] = "報酬率(%)";

					CSVUtil csv = new CSVUtil();
					csv.setHeader(csvHeader); // 設定標頭
					csv.addRecordList(listCSV); // 設定內容
					String url = csv.generateCSV();
					// download
					notifyClientToDownloadFile(url, fileName);
				}

				if ("TBPMS_TOP10_STOCK_OP".equals(return_VO.getElement()
						.toString())) {
					String fileName = "營運區庫存TOP10排名" + sdf.format(new Date())
							+"_"+  (String) getUserVariable(FubonSystemVariableConsts.LOGINID)+".csv";
					for (Map<String, Object> map : list) {
						// 21 column
						String[] records = new String[13];
						int i = 0;
						records[i] = checkIsNull(map, "DATA_DATE"); // 資料日期
						records[++i] = checkIsNullAndTrans(map, "REGION_CENTER_ID"); // 業務處ID
						records[++i] = checkIsNull(map, "REGION_CENTER_NAME"); // 業務處名稱
						records[++i] = checkIsNullAndTrans(map, "BRANCH_AREA_ID"); // 營運區ID
						records[++i] = checkIsNull(map, "BRANCH_AREA_NAME"); // 營運區名稱
						records[++i] = checkIsNullAndTrans(map, "PRD_ID"); // 產品代號
						records[++i] = checkIsNull(map, "PRD_NAME"); // 產品名稱
						records[++i] = currencyFormat(map, "INV_COST_NTD"); // 投資成本(台幣)
						records[++i] = currencyFormat(map, "INV_VALU_NTD"); // 投資現值(台幣)
						records[++i] = currencyFormat(map, "DIFF_COST_NTD"); // 成本與上週差異(台幣)
						records[++i] = currencyFormat(map, "DIFF_VALU_NTD"); // 現值與上週差異(台幣)
						records[++i] = checkIsNull(map, "SRANK"); // 排名
						records[++i] = checkIsNull(map, "ROR"); // 報酬率

						// 組別名稱 //組別名稱
						listCSV.add(records);
					}
					String[] csvHeader = new String[13];
					int j = 0;
					csvHeader[j] = "資料日期";
					csvHeader[++j] = "業務處ID";
					csvHeader[++j] = "業務處名稱";
					csvHeader[++j] = "營運區ID";
					csvHeader[++j] = "營運區名稱";
					csvHeader[++j] = "產品代號";
					csvHeader[++j] = "產品名稱";
					csvHeader[++j] = "投資成本(台幣)";
					csvHeader[++j] = "投資現值(台幣)";
					csvHeader[++j] = "成本與上週差異(台幣)";
					csvHeader[++j] = "現值與上週差異(台幣)";
					csvHeader[++j] = "排名";
					csvHeader[++j] = "報酬率(%)";

					CSVUtil csv = new CSVUtil();
					csv.setHeader(csvHeader); // 設定標頭
					csv.addRecordList(listCSV); // 設定內容
					String url = csv.generateCSV();
					// download
					notifyClientToDownloadFile(url, fileName);
				}

				if ("TBPMS_TOP10_STOCK_RC".equals(return_VO.getElement()
						.toString())) {
					String fileName = "業務處庫存TOP10排名" + sdf.format(new Date())
							+"_"+  (String) getUserVariable(FubonSystemVariableConsts.LOGINID)+".csv";
					for (Map<String, Object> map : list) {
						// 21 column
						String[] records = new String[11];
						int i = 0;
						records[i] = checkIsNull(map, "DATA_DATE"); // 資料日期
						records[++i] = checkIsNullAndTrans(map, "REGION_CENTER_ID"); // 業務處ID
						records[++i] = checkIsNull(map, "REGION_CENTER_NAME"); // 業務處名稱
						records[++i] = checkIsNullAndTrans(map, "PRD_ID"); // 產品代號
						records[++i] = checkIsNull(map, "PRD_NAME"); // 產品名稱
						records[++i] = currencyFormat(map, "INV_COST_NTD"); // 投資成本(台幣)
						records[++i] = currencyFormat(map, "INV_VALU_NTD"); // 投資現值(台幣)
						records[++i] = currencyFormat(map, "DIFF_COST_NTD"); // 成本與上週差異(台幣)
						records[++i] = currencyFormat(map, "DIFF_VALU_NTD"); // 現值與上週差異(台幣)
						records[++i] = checkIsNull(map, "SRANK"); // 排名
						records[++i] = checkIsNull(map, "ROR"); // 報酬率

						// 組別名稱 //組別名稱
						listCSV.add(records);
					}
					String[] csvHeader = new String[11];
					int j = 0;
					csvHeader[j] = "資料日期";
					csvHeader[++j] = "業務處ID";
					csvHeader[++j] = "業務處名稱";
					csvHeader[++j] = "產品代號";
					csvHeader[++j] = "產品名稱";
					csvHeader[++j] = "投資成本(台幣)";
					csvHeader[++j] = "投資現值(台幣)";
					csvHeader[++j] = "成本與上週差異(台幣)";
					csvHeader[++j] = "現值與上週差異(台幣)";
					csvHeader[++j] = "排名";
					csvHeader[++j] = "報酬率(%)";

					CSVUtil csv = new CSVUtil();
					csv.setHeader(csvHeader); // 設定標頭
					csv.addRecordList(listCSV); // 設定內容
					String url = csv.generateCSV();
					// download
					notifyClientToDownloadFile(url, fileName);
				}

				if ("TBPMS_TOP10_RDMP_AO".equals(return_VO.getElement()
						.toString())) {
					String fileName = "理專贖回量TOP10排名" + sdf.format(new Date())
							+"_"+  (String) getUserVariable(FubonSystemVariableConsts.LOGINID)+".csv";
					for (Map<String, Object> map : list) {
						// 21 column AO贖回量TOP10排名

						String[] records = new String[16];
						int i = 0;
						records[i] = checkIsNull(map, "DATA_DATE"); // 資料日期
						records[++i] = checkIsNullAndTrans(map, "REGION_CENTER_ID"); // 業務處ID
						records[++i] = checkIsNull(map, "REGION_CENTER_NAME"); // 業務處名稱
						records[++i] = checkIsNullAndTrans(map, "BRANCH_AREA_ID"); // 營運區ID
						records[++i] = checkIsNull(map, "BRANCH_AREA_NAME"); // 營運區名稱
						records[++i] = checkIsNullAndTrans(map, "BRANCH_NBR"); // 分行代碼
						records[++i] = checkIsNull(map, "BRANCH_NAME"); // 分行名稱
						records[++i] = checkIsNullAndTrans(map, "BRANCH_CLS"); // 組別
						records[++i] = checkIsNullAndTrans(map, "AO_JOB_RANK"); // 理專職級代碼
//						records[++i] = checkIsNull(map, "AO_JOB_NAME"); // 理專職級名稱
						records[++i] = checkIsNull(map, "AO_CODE"); // AO Code
						records[++i] = checkIsNullAndTrans(map, "EMP_ID"); // 理專員編
						records[++i] = checkIsNull(map, "EMP_NAME"); // 理專姓名
						records[++i] = checkIsNullAndTrans(map, "PRD_ID"); // 產品代號
						records[++i] = checkIsNull(map, "PRD_NAME"); // 產品名稱
						records[++i] = currencyFormat(map, "RDMP_COST_NTD"); // 贖回成本(台幣)
						records[++i] = currencyFormat(map, "RDMP_VALU_NTD"); // 贖回現值(台幣)

						listCSV.add(records);
					}
					String[] csvHeader = new String[16];
					int j = 0;
					csvHeader[j] = "資料日期";
					csvHeader[++j] = "業務處ID";
					csvHeader[++j] = "業務處名稱";
					csvHeader[++j] = "營運區ID";
					csvHeader[++j] = "營運區名稱";
					csvHeader[++j] = "分行代碼";
					csvHeader[++j] = "分行名稱";
					csvHeader[++j] = "組別";
					csvHeader[++j] = "理專職級代碼";
//					csvHeader[++j] = "理專職級名稱";
					csvHeader[++j] = "AO Code";
					csvHeader[++j] = "理專員編";
					csvHeader[++j] = "理專姓名";
					csvHeader[++j] = "產品代號";
					csvHeader[++j] = "產品名稱";
					csvHeader[++j] = "贖回成本(台幣)";
					csvHeader[++j] = "贖回現值(台幣)";
					

					CSVUtil csv = new CSVUtil();
					csv.setHeader(csvHeader); // 設定標頭
					csv.addRecordList(listCSV); // 設定內容
					String url = csv.generateCSV();
					// download
					notifyClientToDownloadFile(url, fileName);
				}

				if ("TBPMS_TOP10_RDMP_BR".equals(return_VO.getElement()
						.toString())) {
					String fileName = "分行贖回量TOP10排名" + sdf.format(new Date())
							+"_"+  (String) getUserVariable(FubonSystemVariableConsts.LOGINID)+".csv";
					for (Map<String, Object> map : list) {
						// 21 column AO贖回量TOP10排名

						String[] records = new String[11];
						int i = 0;
						records[i] = checkIsNull(map, "DATA_DATE"); // 資料日期
						records[++i] = checkIsNullAndTrans(map, "REGION_CENTER_ID"); // 業務處ID
						records[++i] = checkIsNull(map, "REGION_CENTER_NAME"); // 業務處名稱
						records[++i] = checkIsNullAndTrans(map, "BRANCH_AREA_ID"); // 營運區ID
						records[++i] = checkIsNull(map, "BRANCH_AREA_NAME"); // 營運區名稱
						records[++i] = checkIsNullAndTrans(map, "BRANCH_NBR"); // 分行代碼
						records[++i] = checkIsNull(map, "BRANCH_NAME"); // 分行名稱
						records[++i] = checkIsNullAndTrans(map, "PRD_ID"); // 產品代號
						records[++i] = checkIsNull(map, "PRD_NAME"); // 產品名稱
						records[++i] = currencyFormat(map, "RDMP_COST_NTD"); // 贖回成本(台幣)
						records[++i] = currencyFormat(map, "RDMP_VALU_NTD"); // 贖回現值(台幣)

						listCSV.add(records);
					}
					String[] csvHeader = new String[11];
					int j = 0;
					csvHeader[j] = "資料日期";
					csvHeader[++j] = "業務處ID";
					csvHeader[++j] = "業務處名稱";
					csvHeader[++j] = "營運區ID";
					csvHeader[++j] = "營運區名稱";
					csvHeader[++j] = "分行代碼";
					csvHeader[++j] = "分行名稱";
					csvHeader[++j] = "產品代號";
					csvHeader[++j] = "產品名稱";
					csvHeader[++j] = "贖回成本(台幣)";
					csvHeader[++j] = "贖回現值(台幣)";

					CSVUtil csv = new CSVUtil();
					csv.setHeader(csvHeader); // 設定標頭
					csv.addRecordList(listCSV); // 設定內容
					String url = csv.generateCSV();
					// download
					notifyClientToDownloadFile(url, fileName);
				}

				if ("TBPMS_TOP10_RDMP_OP".equals(return_VO.getElement()
						.toString())) {
					String fileName = "營運區贖回量TOP10排名" + sdf.format(new Date())
							+"_"+  (String) getUserVariable(FubonSystemVariableConsts.LOGINID)+".csv";
					for (Map<String, Object> map : list) {
						// 21 column AO贖回量TOP10排名

						String[] records = new String[10];
						int i = 0;
						records[i] = checkIsNull(map, "DATA_DATE"); // 資料日期
						records[++i] = checkIsNullAndTrans(map, "REGION_CENTER_ID"); // 業務處ID
						records[++i] = checkIsNull(map, "REGION_CENTER_NAME"); // 業務處名稱
						records[++i] = checkIsNullAndTrans(map, "BRANCH_AREA_ID"); // 營運區ID
						records[++i] = checkIsNull(map, "BRANCH_AREA_NAME"); // 營運區名稱
						records[++i] = checkIsNullAndTrans(map, "PRD_ID"); // 產品代號
						records[++i] = checkIsNull(map, "PRD_NAME"); // 產品名稱
						records[++i] = currencyFormat(map, "RDMP_COST_NTD"); // 贖回成本(台幣)
						records[++i] = currencyFormat(map, "RDMP_VALU_NTD"); // 贖回現值(台幣)
//						records[++i] = checkIsNull(map, "BRANCH_CLS"); // 組別

						listCSV.add(records);
					}
					String[] csvHeader = new String[10];
					int j = 0;
					csvHeader[j] = "資料日期";
					csvHeader[++j] = "業務處ID";
					csvHeader[++j] = "業務處名稱";
					csvHeader[++j] = "營運區ID";
					csvHeader[++j] = "營運區名稱";
					csvHeader[++j] = "產品代號";
					csvHeader[++j] = "產品名稱";
					csvHeader[++j] = "贖回成本(台幣)";
					csvHeader[++j] = "贖回現值(台幣)";
//					csvHeader[++j] = "組別";

					CSVUtil csv = new CSVUtil();
					csv.setHeader(csvHeader); // 設定標頭
					csv.addRecordList(listCSV); // 設定內容
					String url = csv.generateCSV();
					// download
					notifyClientToDownloadFile(url, fileName);
				}

				if ("TBPMS_TOP10_RDMP_RC".equals(return_VO.getElement()
						.toString())) {
					String fileName = "業務處贖回量TOP10排名" + sdf.format(new Date())
							+"_"+  (String) getUserVariable(FubonSystemVariableConsts.LOGINID)+".csv";
					for (Map<String, Object> map : list) {
						// 21 column AO贖回量TOP10排名

						String[] records = new String[8];
						int i = 0;
						records[i] = checkIsNull(map, "DATA_DATE"); // 資料日期
						records[++i] = checkIsNullAndTrans(map, "REGION_CENTER_ID"); // 業務處ID
						records[++i] = checkIsNull(map, "REGION_CENTER_NAME"); // 業務處名稱
						records[++i] = checkIsNullAndTrans(map, "PRD_ID"); // 產品代號
						records[++i] = checkIsNull(map, "PRD_NAME"); // 產品名稱
						records[++i] = currencyFormat(map, "RDMP_COST_NTD"); // 贖回成本(台幣)
						records[++i] = currencyFormat(map, "RDMP_VALU_NTD"); // 贖回現值(台幣)
//						records[++i] = checkIsNull(map, "BRANCH_CLS"); // 組別

						listCSV.add(records);
					}
					String[] csvHeader = new String[8];
					int j = 0;
					csvHeader[j] = "資料日期";
					csvHeader[++j] = "業務處ID";
					csvHeader[++j] = "業務處名稱";
					csvHeader[++j] = "產品代號";
					csvHeader[++j] = "產品名稱";
					csvHeader[++j] = "贖回成本(台幣)";
					csvHeader[++j] = "贖回現值(台幣)";
//					csvHeader[++j] = "組別";

					CSVUtil csv = new CSVUtil();
					csv.setHeader(csvHeader); // 設定標頭
					csv.addRecordList(listCSV); // 設定內容
					String url = csv.generateCSV();
					// download
					notifyClientToDownloadFile(url, fileName);
				}

			} else
				return_VO.setResultList(list);
			this.sendRtnObject(null);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}
	//處理貨幣格式
	private String currencyFormat(Map map, String key){		
		if(StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key) != null){
			NumberFormat nf =  new DecimalFormat("###,###.00");
			return nf.format(map.get(key));	
		}else
			return "0.00";		
	}
		
	private String checkIsNull(Map map, String key) {
		if (StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key) != null) {
				return String.valueOf(map.get(key));
		} else {
			/* 理專職級名稱 */
//			if (key == "AO_JOB_NAME")
//				return "經辦";
//			else if (key == "GROUP_NAME")
//				return "理組";
//			else
			return "";
		}
	}
	private String checkIsNull2(Map map, String key) {
		if (StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key) != null) {
				return String.valueOf(map.get(key))+"%";
		} else {
			/* 理專職級名稱 */
//			if (key == "AO_JOB_NAME")
//				return "經辦";
//			else if (key == "GROUP_NAME")
//				return "理組";
//			else
			return "";
		}
	}
	
	private String currencyFormat2(Map map, String key){		
		if(StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key) != null){
			NumberFormat nf =  new DecimalFormat("###.##");
			System.out.println("ya "+nf.format(map.get(key)));
//			return String.format("%.2s%s",map.get(key),".00%") ;
			return nf.format(map.get(key));	
//			return String.format("%.2f",map.get(key)) ;
		}else
			return "0.00";		
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
