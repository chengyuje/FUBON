package com.systex.jbranch.app.server.fps.pms357;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.server.fps.pms000.PMS000;
import com.systex.jbranch.app.server.fps.pms000.PMS000InputVO;
import com.systex.jbranch.app.server.fps.pms000.PMS000OutputVO;
import com.systex.jbranch.app.server.fps.pms359.PMS359OutputVO;
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
 * Description :各分行損益表<br>
 * Comments Name : PMS357java<br>
 * Author : Kevin<br>
 * Date :2016/08/11 <br>
 * Version : 1.0 <br>
 * Editor : Kevin<br>
 * Editor Date : 2017年01月30日<br>
 */

@Component("pms357")
@Scope("request")
public class PMS357 extends FubonWmsBizLogic {
	public DataAccessManager dam = null;
	
	/** 查詢資料 **/
	public void inquire(Object body, IPrimitiveMap header)
			throws JBranchException, ParseException {
		PMS357InputVO inputVO = (PMS357InputVO) body;
		PMS357OutputVO outputVO = new PMS357OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam
				.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		String roleType = "";
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
		pms000InputVO.setReportDate(inputVO.getsTime());
		PMS000OutputVO pms000outputVO = pms000.getOrg(pms000InputVO);
		
		try {
			// ==預先兜資料，主查詢(前段)==
			sql.append("WITH ");
			sql.append("ORIGINAL_VIEW AS ( ");
			sql.append("  select  ");
			sql.append("    loss.YEARMON, vw.REGION_CENTER_ID, vw.REGION_CENTER_NAME, vw.BRANCH_AREA_ID, vw.BRANCH_AREA_NAME, loss.BRANCH_NBR, vw.BRANCH_NAME, loss.Q_TYPE, ");
			sql.append("    loss.NIT_DEPST, loss.NIT_HLOAN, loss.NIT_CREDIT, loss.NIT_OTHER, ");
			sql.append("    loss.UNNIT_DEPST, loss.UNNIT_TUST, loss.UNNIT_CON, loss.UNNIT_LOAN, loss.UNNIT_EXP, loss.UNNIT_LOSS, loss.UNNIT_OTHER, ");
			sql.append("    loss.OP_EXPEN, loss.OP_OTHER, loss.OP_INTER, loss.OP_SHARE, loss.BR_PROFIT_LOSS, loss.TARGET, loss.RATE ");
			sql.append("  from TBPMS_BR_PROFIT_LOSS loss  ");
			sql.append("  left join VWORG_DEFN_INFO vw on vw.BRANCH_NBR = loss.BRANCH_NBR ");
			sql.append("  where 1=1   ");
			
			// ==主查詢條件==
			sql.append(" and loss.YEARMON = :YEARMON ");
			queryCondition.setObject("YEARMON", inputVO.getsTime());
			sql.append(" and loss.Q_TYPE = :Q_TYPE ");
			queryCondition.setObject("Q_TYPE", inputVO.getChecked());
			
			// 分行
			if (StringUtils.isNotBlank(inputVO.getBranch_nbr())) {
				sql.append("and vw.BRANCH_NBR = :branch_nbr ");
				queryCondition.setObject("branch_nbr", inputVO.getBranch_nbr());
			}else{
				//登入非總行人員強制加分行
				if(!headmgrMap.containsKey(roleID)) {
					sql.append("and vw.BRANCH_NBR IN (:branch_nbr) ");
					queryCondition.setObject("branch_nbr", pms000outputVO.getV_branchList());
				}
			}
			// 營運區
			if (StringUtils.isNotBlank(inputVO.getBranch_area_id())) {
				sql.append("and vw.BRANCH_AREA_ID = :branch_area_id ");
				queryCondition.setObject("branch_area_id", inputVO.getBranch_area_id());
			}else{
				//登入非總行人員強制加營運區
				if(!headmgrMap.containsKey(roleID)) {
					sql.append("and vw.BRANCH_AREA_ID IN (:branch_area_id) ");
					queryCondition.setObject("branch_area_id", pms000outputVO.getV_areaList());
				}
			}
			// 區域中心
			if (StringUtils.isNotBlank(inputVO.getRegion_center_id())) {
				sql.append("and vw.REGION_CENTER_ID = :region_center_id ");
				queryCondition.setObject("region_center_id", inputVO.getRegion_center_id());
			}else{
				//登入非總行人員強制加區域中心
				if(!headmgrMap.containsKey(roleID)) {
					sql.append("and vw.REGION_CENTER_ID IN (:region_center_id) ");
					queryCondition.setObject("region_center_id", pms000outputVO.getV_regionList());
				}
			}
			
			// ==預先兜資料(後段)==
			sql.append("), ");
			sql.append("AREA AS ( ");
			sql.append("  SELECT  ");
			sql.append("    'AREA' AS YEARMON,REGION_CENTER_ID,REGION_CENTER_NAME,BRANCH_AREA_ID,BRANCH_AREA_NAME,'AREA' AS BRANCH_NBR,'AREA' AS BRANCH_NAME, ");
			sql.append("    'AREA' AS Q_TYPE, ");
			sql.append("    SUM(NIT_DEPST) AS NIT_DEPST,SUM(NIT_HLOAN) AS NIT_HLOAN,SUM(NIT_CREDIT) AS NIT_CREDIT,SUM(NIT_OTHER) AS NIT_OTHER, ");
			sql.append("    SUM(UNNIT_DEPST) AS UNNIT_DEPST,SUM(UNNIT_TUST) AS UNNIT_TUST,SUM(UNNIT_CON) AS UNNIT_CON,SUM(UNNIT_LOAN) AS UNNIT_LOAN,SUM(UNNIT_EXP) AS UNNIT_EXP,SUM(UNNIT_LOSS) AS UNNIT_LOSS,SUM(UNNIT_OTHER) AS UNNIT_OTHER, ");
			sql.append("    SUM(OP_EXPEN) AS OP_EXPEN,SUM(OP_OTHER) AS OP_OTHER,SUM(OP_INTER) AS OP_INTER,SUM(OP_SHARE) AS OP_SHARE,SUM(BR_PROFIT_LOSS) AS BR_PROFIT_LOSS,SUM(TARGET) AS TARGET,CASE WHEN SUM(TARGET)<>0 THEN ROUND((1+((SUM(BR_PROFIT_LOSS)-SUM(TARGET))/ABS(SUM(TARGET)))) * 100, 2) ELSE 0 END AS RATE ");
			sql.append("  FROM ORIGINAL_VIEW ");
			sql.append("  group by  ");
			sql.append("    REGION_CENTER_ID,REGION_CENTER_NAME,BRANCH_AREA_ID,BRANCH_AREA_NAME ");
			sql.append("), ");
			sql.append("REGION AS ( ");
			sql.append("  SELECT  ");
			sql.append("    'REGION' AS YEARMON,REGION_CENTER_ID,REGION_CENTER_NAME,'REGION' AS BRANCH_AREA_ID,'REGION' AS BRANCH_AREA_NAME,'REGION' AS BRANCH_NBR,'REGION' AS BRANCH_NAME, ");
			sql.append("    'REGION' AS Q_TYPE, ");
			sql.append("    SUM(NIT_DEPST) AS NIT_DEPST,SUM(NIT_HLOAN) AS NIT_HLOAN,SUM(NIT_CREDIT) AS NIT_CREDIT,SUM(NIT_OTHER) AS NIT_OTHER, ");
			sql.append("    SUM(UNNIT_DEPST) AS UNNIT_DEPST,SUM(UNNIT_TUST) AS UNNIT_TUST,SUM(UNNIT_CON) AS UNNIT_CON,SUM(UNNIT_LOAN) AS UNNIT_LOAN,SUM(UNNIT_EXP) AS UNNIT_EXP,SUM(UNNIT_LOSS) AS UNNIT_LOSS,SUM(UNNIT_OTHER) AS UNNIT_OTHER, ");
			sql.append("    SUM(OP_EXPEN) AS OP_EXPEN,SUM(OP_OTHER) AS OP_OTHER,SUM(OP_INTER) AS OP_INTER,SUM(OP_SHARE) AS OP_SHARE,SUM(BR_PROFIT_LOSS) AS BR_PROFIT_LOSS,SUM(TARGET) AS TARGET,CASE WHEN SUM(TARGET)<>0 THEN ROUND(( SUM(BR_PROFIT_LOSS)/SUM(TARGET) ) * 100, 2) ELSE 0 END AS RATE ");
			sql.append("  FROM AREA ");
			sql.append("  group by  ");
			sql.append("    REGION_CENTER_ID,REGION_CENTER_NAME ");
			sql.append("), ");
			sql.append("SUM_AREA AS (  ");
			sql.append("  SELECT *  ");
			sql.append("  FROM (  ");
			sql.append("    SELECT * ");  
			sql.append("    FROM ORIGINAL_VIEW  ");
			sql.append("    UNION ALL  ");
			sql.append("    SELECT *   ");
			sql.append("    FROM AREA    ");
			sql.append("  )  ");
			sql.append("),   ");
			sql.append("SUM_AREA_REGION AS (  ");
			sql.append("  SELECT *  ");
			sql.append("  FROM SUM_AREA  ");
			sql.append("  UNION ALL  ");
			sql.append("  SELECT *  ");
			sql.append("  FROM REGION  ");
			sql.append(") ");
	
			
			// 依據條件顯示適當的合計
			if (StringUtils.isNotBlank(inputVO.getBranch_nbr()) && 
				StringUtils.isNotBlank(inputVO.getBranch_area_id()) &&
				StringUtils.isNotBlank(inputVO.getRegion_center_id())) {
				sql.append("SELECT * from ORIGINAL_VIEW ");
			} else if (!StringUtils.isNotBlank(inputVO.getBranch_nbr()) && 
					StringUtils.isNotBlank(inputVO.getBranch_area_id()) &&
					StringUtils.isNotBlank(inputVO.getRegion_center_id())) {
				sql.append("SELECT * from SUM_AREA ");
			} else if (!StringUtils.isNotBlank(inputVO.getBranch_nbr()) && 
					!StringUtils.isNotBlank(inputVO.getBranch_area_id())) {
				sql.append("SELECT * from SUM_AREA_REGION ");
			}
			
			sql.append("order by REGION_CENTER_ID,BRANCH_AREA_ID,BRANCH_NBR,YEARMON ");			
			queryCondition.setQueryString(sql.toString());
			
			//	查詢分頁
//			ResultIF list = dam.executePaging(queryCondition,
//					inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
//			int totalPage_i = list.getTotalPage();
//			int totalRecord_i = list.getTotalRecord();
			
			//	查詢全部
			List<Map<String, Object>> list1 = dam.exeQuery(queryCondition); // 匯出全部用
			//	設定CSV
			outputVO.setCsvList(list1);
			//	設定分頁
			outputVO.setResultList(list1);
//			outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());// 當前頁次
//			outputVO.setTotalPage(totalPage_i);// 總頁次
//			outputVO.setTotalRecord(totalRecord_i);// 總筆數
			this.sendRtnObject(outputVO);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}

	}
	
	/*有資料日期下拉選單查詢 */
	public void date_query (Object body, IPrimitiveMap header)
			throws JBranchException{
		PMS357OutputVO outputVO = new PMS357OutputVO();
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF condition = dam
				.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		try {
			StringBuilder sql=new StringBuilder();
			sql.append("SELECT distinct  YEARMON from TBPMS_BR_PROFIT_LOSS order by YEARMON desc");
			condition.setQueryString(sql.toString());
			List<Map<String,Object>> list=dam.exeQuery(condition);
			outputVO.setResultList(list);;
			sendRtnObject(outputVO);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		} 
	
	
	}

	/***** 查詢實績/目標 ******/
	public void getDetail(Object body, IPrimitiveMap header)
			throws JBranchException {
		PMS357InputVO inputVO = (PMS357InputVO) body;
		PMS357OutputVO return_VO = new PMS357OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam
				.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		try {
			//==主查詢  依照前端查詢==
			sql.append(" SELECT ACT.YYMM, ");
			sql.append(" 		(SELECT COUNT(*) FROM TBPMS_ACTUAL_TARGET TAR WHERE  ACT.YYMM = TAR.YYMM) AS n_count, ");
			sql.append(" 		N_CREATETIME ");
			sql.append(" FROM TBPMS_ACTUAL_TARGET ACT ");
			sql.append(" left join ( ");
			sql.append(" 	select YYMM,to_char(ACT2.CREATETIME,'yyyyMMdd') AS N_CREATETIME ");
			sql.append(" 	from TBPMS_ACTUAL_TARGET ACT2 ");
			sql.append(" 	group by YYMM,to_char(ACT2.CREATETIME,'yyyyMMdd'))B ");
			sql.append(" on ACT.YYMM = B.YYMM ");
			sql.append(" where 1=1 ");
			
			// ==主查詢條件==
//			年月
			if (!StringUtils.isBlank(inputVO.getsTime())) {
				sql.append(" and ACT.YYMM LIKE :YEARMONN");
			}
			//	設定年月
			if (!StringUtils.isBlank(inputVO.getsTime())) {
				queryCondition.setObject("YEARMONN", "%" + inputVO.getsTime()
						+ "%");
			}
			sql.append(" group by ACT.YYMM,N_CREATETIME ");
			queryCondition.setQueryString(sql.toString());
			// result查詢結果
//			List<Map<String, Object>> list1 = dam.exeQuery(queryCondition); // 全部用
			ResultIF list = dam.executePaging(queryCondition,
					inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
			int totalPage_i = list.getTotalPage();
			int totalRecord_i = list.getTotalRecord();
			return_VO.setResultList(list);
			return_VO.setCurrentPageIndex(inputVO.getCurrentPageIndex());// 當前頁次
			return_VO.setTotalPage(totalPage_i);// 總頁次
			return_VO.setTotalRecord(totalRecord_i);// 總筆數
			this.sendRtnObject(return_VO);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}

	/******* 匯出功能 *******/
	public void export(Object body, IPrimitiveMap header)
			throws JBranchException, FileNotFoundException, IOException {
		PMS357OutputVO return_VO = (PMS357OutputVO) body;

		List<Map<String, Object>> list = return_VO.getList();
		try {
			if (list.size() > 0) {
				// gen csv
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
				String fileName = "各分行損益表報表" + sdf.format(new Date())+"-"
						+getUserVariable(FubonSystemVariableConsts.LOGINID)+ ".csv";
				List listCSV = new ArrayList();
				for (Map<String, Object> map : list) {
					// 41 column
					String[] records = new String[25];
					int i = 0;
					if (!(checkIsNull(map, "YEARMON").equals("AREA") || checkIsNull(map, "YEARMON").equals("REGION"))) {

						records[i] = checkIsNull(map, "YEARMON"); // 資料統計年月
						records[++i] = checkIsNull(map, "REGION_CENTER_ID"); // 區域中心ID
						records[++i] = checkIsNull(map, "REGION_CENTER_NAME"); // 區域中心名稱
						records[++i] = checkIsNull(map, "BRANCH_AREA_ID"); // 營運區ID
						records[++i] = checkIsNull(map, "BRANCH_AREA_NAME"); // 營運區名稱
						records[++i] = checkIsNull(map, "BRANCH_NBR"); // 分行ID
						records[++i] = checkIsNull(map, "BRANCH_NAME"); // 分行名稱
						records[++i] = checkIsNull(map, "NIT_DEPST"); // 利息淨收益-存款利差
						records[++i] = checkIsNull(map, "NIT_HLOAN"); // 利息淨收益-房貸利差
						records[++i] = checkIsNull(map, "NIT_CREDIT"); // 利息淨收益-信貸利差
						records[++i] = checkIsNull(map, "NIT_OTHER"); // 利息淨收益-其他
						/*
						 * records[++i] = checkIsNull(map, "UNNIT_FORM"); //
						 * 非利息淨收益-手續費淨收益 records[++i] = checkIsNull(map,
						 * "UNNIT_INCOM"); // 非利息淨收益-手續費收入
						 */
						records[++i] = checkIsNull(map, "UNNIT_DEPST"); // 非利息淨收益-存匯手續費收入
						records[++i] = checkIsNull(map, "UNNIT_TUST"); // 非利息淨收益-信託及保管業務收入
						records[++i] = checkIsNull(map, "UNNIT_CON"); // 非利息淨收益-理財商品收入
						records[++i] = checkIsNull(map, "UNNIT_LOAN"); // 非利息淨收益-放款手續費收入
						records[++i] = checkIsNull(map, "UNNIT_EXP"); // 非利息淨收益-手續費費用
						records[++i] = checkIsNull(map, "UNNIT_LOSS"); // 非利息淨收益-評價及已實現損益
						records[++i] = checkIsNull(map, "UNNIT_OTHER"); // 非利息淨收益-評價及其他淨收益
						records[++i] = checkIsNull(map, "OP_EXPEN"); // 營業支出-營業費用
						records[++i] = checkIsNull(map, "OP_OTHER"); // 營業支出-其他成本
						records[++i] = checkIsNull(map, "OP_INTER"); // 營業支出-內部計價成本
						records[++i] = checkIsNull(map, "OP_SHARE"); // 營業支出-分攤費用
						records[++i] = checkIsNull(map, "BR_PROFIT_LOSS"); // 營業支出-實際數(A+B+C)
						records[++i] = checkIsNull(map, "TARGET"); // YTM目標
						records[++i] = checkIsNull(map, "RATE")+"%"; // YTM達成率
	
						listCSV.add(records);
					
					}
				}
				// header
				String[] csvHeader = new String[25];
				int j = 0;
				csvHeader[j] = "資料統計年月";
				csvHeader[++j] = "業務處ID";
				csvHeader[++j] = "業務處名稱";
				csvHeader[++j] = "營運區ID";
				csvHeader[++j] = "營運區名稱";
				csvHeader[++j] = "分行ID";
				csvHeader[++j] = "分行名稱";
				csvHeader[++j] = "利息淨收益-存款利差";
				csvHeader[++j] = "利息淨收益-房貸利差";
				csvHeader[++j] = "利息淨收益-信貸利差";
				csvHeader[++j] = "利息淨收益-其他";
				csvHeader[++j] = "非利息淨收益-存匯手續費收入";
				csvHeader[++j] = "非利息淨收益-信託及保管業務收入";
				csvHeader[++j] = "非利息淨收益-理財商品收入";
				csvHeader[++j] = "非利息淨收益-放款手續費收入";
				csvHeader[++j] = "非利息淨收益-手續費費用";
				csvHeader[++j] = "非利息淨收益-評價及已實現損益";
				csvHeader[++j] = "非利息淨收益-其他淨收益";
				csvHeader[++j] = "營業支出-營業費用";
				csvHeader[++j] = "營業支出-其他成本";
				csvHeader[++j] = "營業支出-內部計價成本";
				csvHeader[++j] = "營業支出-分攤費用";
				csvHeader[++j] = "提存前分攤後淨利(A+B+C)";

				if (StringUtils.equals(return_VO.getChecked(), "2")) {
					csvHeader[++j] = "YTD目標";
					csvHeader[++j] = "YTD達成率";
				} else {
					csvHeader[++j] = "當月目標";
					csvHeader[++j] = "當月達成率";
				}
				CSVUtil csv = new CSVUtil();
				csv.setHeader(csvHeader); // 設定標頭
				csv.addRecordList(listCSV); // 設定內容
				String url = csv.generateCSV();
				// download
				notifyClientToDownloadFile(url, fileName);
			} else

				this.sendRtnObject(null);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}

	}
	/***** tuncate報表 ******/
	public void tuncateTable(Object body, IPrimitiveMap header)
			throws JBranchException {
		PMS357InputVO inputVO = (PMS357InputVO) body;
		PMS357OutputVO return_VO = new PMS357OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		QueryConditionIF queryCondition2 = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql_view1 = new StringBuffer();
		StringBuffer sql_view2 = new StringBuffer();
		try {
			sql_view1.append("TRUNCATE  TABLE TBPMS_ACTUAL_TARGET ");
			queryCondition.setQueryString(sql_view1.toString());
			dam.exeUpdate(queryCondition);
			this.sendRtnObject(null);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}
	
	
	/***** 計算報表 ******/
	public void runJob(Object body, IPrimitiveMap header)
			throws JBranchException {
		PMS357InputVO inputVO = (PMS357InputVO) body;
		PMS357OutputVO return_VO = new PMS357OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam
				.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		try {
			sql.append("CALL PABTH_BTPMS349.MAIN(?) ");
			queryCondition.setString(1, inputVO.getsTime());
			queryCondition.setQueryString(sql.toString());
			dam.exeUpdate(queryCondition);
			this.sendRtnObject(return_VO);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}
	
	
	/**
	 * 檢查Map取出欄位是否為Null
	 */
	private String checkIsNull(Map map, String key) {
		if (StringUtils.isNotBlank(String.valueOf(map.get(key)))) {
			return String.valueOf(map.get(key));
		} else {
			return "";
		}
	}

	private String checkIsNu(Map map, String key) {

		if (StringUtils.isNotBlank(String.valueOf(map.get(key)))) {
			return String.valueOf(map.get(key));
		} else {
			return "0";
		}
	}
}
