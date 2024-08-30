package com.systex.jbranch.app.server.fps.pms311;

import java.text.DateFormat;
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

import com.systex.jbranch.app.server.fps.pms358.PMS358OutputVO;
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
 * Copy Right Information : <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description :房信貸PS生產力周報 Controller <br>
 * Comments Name : PMS311.java<br>
 * Author :Frank<br>
 * Date :2016年08月11日 <br>
 * Version : 1.01 <br>
 * Editor : Kevin<br>
 * Editor Date : 2017年01月31日<br>
 */

@Component("pms311")
@Scope("request")
public class PMS311 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(PMS311.class);
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

	public void date_query (Object body, IPrimitiveMap header)
			throws JBranchException{
		PMS311InputVO inputVO = (PMS311InputVO) body;
		PMS311OutputVO outputVO = new PMS311OutputVO();
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF condition = dam
				.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		try {
			StringBuilder sql=new StringBuilder();
			if ("mortRPT".equals(inputVO.getRptType())) {
				sql.append("SELECT distinct DATA_DATE from TBPMS_MRTG_PS_WEEK order by DATA_DATE DESC");
			}
			if ("credRPT".equals(inputVO.getRptType())) {
				sql.append("SELECT distinct DATA_DATE from TBPMS_CREDIT_PS_WEEK order by DATA_DATE DESC");
			}
			condition.setQueryString(sql.toString());
			if(inputVO.getRptType() != null && !"".equals(inputVO.getRptType()) ){
				List<Map<String,Object>> list = dam.exeQuery(condition);
				outputVO.setResultList(list);
			}
			sendRtnObject(outputVO);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		} 
	
	
	}
	
	
	public void queryData(Object body, IPrimitiveMap header)
			throws JBranchException {
		PMS311InputVO inputVO = (PMS311InputVO) body;
		PMS311OutputVO outputVO = new PMS311OutputVO();
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF condition = dam
				.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		StringBuffer sql = new StringBuffer();
		/* 查詢房貸週報 */
		if ("mortRPT".equals(inputVO.getRptType())) {
			sql.append("SELECT W.REGION_CENTER_ID, W.REGION_CENTER_NAME, ");
			sql.append("W.BRANCH_AREA_ID, W.BRANCH_AREA_NAME, ");
			sql.append("W.BRANCH_NBR, W.BRANCH_NAME, W.BRANCH_CLS, ");
			sql.append("W.EMP_NAME, W.EMP_ID, W.S_HB_TAR, ");
			sql.append("W.S_HB_IN_AMT, W.S_HB_OUT_AMT, W.S_HB_RATE, ");
			sql.append("M.S_HB_MTD_IN_AMT, M.S_HB_MTD_OUT_AMT, ");
			sql.append("M.S_HB_MTD_RATE, Y.S_HB_YTD_OUT_AMT, ");
			sql.append("Y.S_HB_YTD_TAR, Y.S_HB_YTD_RATE, W.S_NHB_TAR, ");
			sql.append("W.S_NHB_IN_AMT, W.S_NHB_OUT_AMT, W.S_NHB_RATE, ");
			sql.append("M.S_NHB_MTD_INT_AMT, M.S_NHB_MTD_OUT_AMT, ");
			sql.append("M.S_NHB_MTD_RATE, Y.S_NHB_YTD_OUT_AMT, ");
			sql.append("Y.S_NHB_YTD_TAR, Y.S_NHB_YTD_RATE, W.C_NHB_TAR, ");
			sql.append("W.C_NHB_IN_AMT, W.C_NHB_OUT_AMT, W.C_NHB_RATE, ");
			sql.append("M.C_NHB_MTD_IN_AMT, M.C_NHB_MTD_OUT_AMT, ");
			sql.append("M.C_NHB_MTD_RATE, Y.C_NHB_YTD_OUT_AMT, ");
			sql.append("Y.C_NHB_YTD_TAR, Y.C_NHB_YTD_RATE, ");
			sql.append("W.MRTG_TAR, W.MRTG_IN_AMT, W.MRTG_OUT_AMT, ");
			sql.append("W.MRTG_RATE, M.MRTG_MTD_IN_AMT, M.MRTG_MTD_OUT_AMT, ");
			sql.append("M.MRTG_MTD_RATE, Y.MRTG_YTD_OUT_AMT, Y.MRTG_YTD_TAR, ");
			sql.append("Y.MRTG_YTD_RATE, Y.MRTG_YTD_RANK, W.CREATETIME ");
			sql.append("FROM TBPMS_MRTG_PS_WEEK W ");
			sql.append("LEFT JOIN TBPMS_MRTG_PS_MTD M ");
			sql.append("ON W.DATA_DATE=M.DATA_DATE ");
			sql.append("AND W.EMP_ID=M.EMP_ID ");
			sql.append("LEFT JOIN TBPMS_MRTG_PS_YTD Y ");
			sql.append("ON W.DATA_DATE=Y.DATA_DATE ");
			sql.append("AND W.EMP_ID=Y.EMP_ID ");
			sql.append("WHERE 1=1 ");
			if (StringUtils.isNotBlank(inputVO.getReportDate()))
				sql.append("AND W.DATA_DATE = :dt ");
			if (StringUtils.isNotBlank(inputVO.getRegion_center_id()))
				sql.append("AND W.REGION_CENTER_ID = :rcid ");
			if (StringUtils.isNotBlank(inputVO.getBranch_area_id()))
				sql.append("AND W.BRANCH_AREA_ID = :opid ");
			if (StringUtils.isNotBlank(inputVO.getBranch_nbr()))
				sql.append("AND W.BRANCH_NBR = :brid ");
			if (StringUtils.isNotBlank(inputVO.getEmp_id()))
				sql.append("AND W.EMP_ID = :empid  ");
		}

		/* 查詢信貸週報 */
		if ("credRPT".equals(inputVO.getRptType())) {
			sql.append("SELECT W.REGION_CENTER_ID, W.REGION_CENTER_NAME, ");
			sql.append("W.BRANCH_AREA_ID, W.BRANCH_AREA_NAME, ");
			sql.append("W.BRANCH_NBR, W.BRANCH_NAME, W.BRANCH_CLS, ");
			sql.append("W.EMP_NAME, W.EMP_ID, W.NC_TAR, ");
			sql.append("W.NC_W_IA, W.NC_W_OA, W.NC_W_RATE, ");
			sql.append("M.NC_MTD_IA, M.NC_MTD_OA, M.NC_MTD_RATE, ");
			sql.append("Y.NC_YTD_OA, Y.NC_YTD_TAR, Y.NC_YTD_RATE, ");
			sql.append("W.GC_TAR, W.GC_W_IA, W.GC_W_OA, W.GC_W_RATE, ");
			sql.append("M.GC_MTD_IA, M.GC_MTD_OA, M.GC_MTD_RATE, ");
			sql.append("Y.GC_YTD_OA, Y.GC_YTD_TAR, Y.GC_YTD_RATE, ");
			sql.append("W.CC_TAR, W.CC_W_IA, W.CC_W_OA, W.CC_W_RATE, ");
			sql.append("M.CC_MTD_IA, M.CC_MTD_OA, M.CC_MTD_RATE, ");
			sql.append("Y.CC_YTD_OA, Y.CC_YTD_TAR, Y.CC_YTD_RATE, ");
			sql.append("W.C_W_IA, W.C_W_OA, M.C_MTD_IA, M.C_MTD_OA, ");
			sql.append("Y.C_YTD_OA, Y.C_YTD_TAR, W.CT_TAR, ");
			sql.append("W.CT_W_IA, W.CT_W_OA, W.CT_W_RATE, ");
			sql.append("M.CT_MTD_IA, M.CT_MTD_OA, M.CT_MTD_RATE, ");
			sql.append("Y.CT_YTD_OA, Y.CT_YTD_TAR, Y.CT_YTD_RATE, ");
			sql.append("Y.CT_YTD_RANK, W.E_TAR, W.E_W_AF, ");
			sql.append("W.E_W_RATE, M.E_MTD_AF, M.E_MTD_RATE, ");
			sql.append("Y.E_YTD_AF, Y.E_YTD_TAR, Y.E_YTD_RATE, ");
			sql.append("Y.E_YTD_RANK, W.CREATETIME   ");
			sql.append("FROM TBPMS_CREDIT_PS_WEEK W ");
			sql.append("LEFT JOIN TBPMS_CREDIT_PS_MTD M ");
			sql.append("ON W.DATA_DATE=M.DATA_DATE ");
			sql.append("AND W.EMP_ID=M.EMP_ID ");
			sql.append("LEFT JOIN TBPMS_CREDIT_PS_YTD Y ");
			sql.append("ON W.DATA_DATE=Y.DATA_DATE ");
			sql.append("AND W.EMP_ID=Y.EMP_ID ");
			sql.append("WHERE 1=1 ");
			if (StringUtils.isNotBlank(inputVO.getReportDate()))
				sql.append("AND W.DATA_DATE = :dt ");
			if (StringUtils.isNotBlank(inputVO.getRegion_center_id()))
				sql.append("AND W.REGION_CENTER_ID = :rcid ");
			if (StringUtils.isNotBlank(inputVO.getBranch_area_id()))
				sql.append("AND W.BRANCH_AREA_ID = :opid ");
			if (StringUtils.isNotBlank(inputVO.getBranch_nbr()))
				sql.append("AND W.BRANCH_NBR = :brid ");
			if (StringUtils.isNotBlank(inputVO.getEmp_id()))
				sql.append("AND W.EMP_ID =  :empid ");
		}
		sql.append(" order by W.REGION_CENTER_ID,W.BRANCH_AREA_ID,W.BRANCH_NBR,W.EMP_ID ");
		condition.setQueryString(sql.toString());
		if (StringUtils.isNotBlank(inputVO.getReportDate()))
			condition.setObject("dt", inputVO.getReportDate());
		if (StringUtils.isNotBlank(inputVO.getRegion_center_id()))
			condition.setObject("rcid", inputVO.getRegion_center_id());
		if (StringUtils.isNotBlank(inputVO.getBranch_area_id()))
			condition.setObject("opid", inputVO.getBranch_area_id());
		if (StringUtils.isNotBlank(inputVO.getBranch_nbr()))
			condition.setObject("brid", inputVO.getBranch_nbr());
		if (StringUtils.isNotBlank(inputVO.getEmp_id()))
			condition.setObject("empid", inputVO.getEmp_id());

		ResultIF list = dam.executePaging(condition,
				inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
		outputVO.setTotalList(dam.exeQuery(condition));

		outputVO.setTotalPage(list.getTotalPage());
		outputVO.setResultList(list);
		outputVO.setTotalRecord(list.getTotalRecord());
		outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());
		sendRtnObject(outputVO);
	}

	/** ==== 產出Excel ==== **/
	public void export(Object body, IPrimitiveMap header)
			throws JBranchException {
		PMS311OutputVO outputVO = (PMS311OutputVO) body;
		String strTitle = "";

		List<Map<String, Object>> list = outputVO.getTotalList();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

		String tempName = "";
		int rec = 0;
		if ("mortRPT".equals(outputVO.getType())) {
			rec = 45;
			tempName = "房貸PS生產力周報";
		} else {
			rec = 62;
			tempName = "信貸PS生產力周報";

		}
		String fileName = tempName + strTitle + "_" + sdf.format(new Date())
				+"_"+ getUserVariable(FubonSystemVariableConsts.LOGINID)
				+ ".csv";

		CSVUtil csv = new CSVUtil();
		csv.setHeader(setHeader(rec));
		csv.addRecordList(setBody(list, rec));
		String url = csv.generateCSV();
		notifyClientToDownloadFile(url, fileName);
		this.sendRtnObject(null);
	}

	/**
	 * 檢查Map取出欄位是否為Null
	 * 
	 * @param map
	 * @return String
	 */
	private String checkIsNull(Map map, String key) {
		if (StringUtils.isNotBlank(String.valueOf(map.get(key)))
				&& map.get(key) != null) {
			return String.valueOf(map.get(key));
		} else {
			return "";
		}

	}

	private String convertDate(Date date) {
		// Create an instance of SimpleDateFormat used for formatting
		// the string representation of date (month/day/year)
		DateFormat df = new SimpleDateFormat("yyyyMMdd");

		// Get the date today using Calendar object.
		// Using DateFormat format method we can create a string
		// representation of a date with the defined format.
		String reportDate = df.format(date.getTime());
		return reportDate;
	}

	private String[] setHeader(int rec) {
		String[] csvHeader = new String[rec];
		int j = 0;
		if (rec == 62) {
			csvHeader[j++] = "業務處";
			csvHeader[j++] = "營運區";
			csvHeader[j++] = "分行代號";
			csvHeader[j++] = "分行名稱";
			csvHeader[j++] = "消金專員姓名";
			csvHeader[j++] = "員編";
			csvHeader[j++] = "一般信貸_月目標";
			csvHeader[j++] = "一般信貸_當週_進件金額";
			csvHeader[j++] = "一般信貸_當週_撥款金額";
			csvHeader[j++] = "一般信貸_當週_達成率(%)";
			csvHeader[j++] = "一般信貸_MTD_進件金額";
			csvHeader[j++] = "一般信貸_MTD_撥款金額";
			csvHeader[j++] = "一般信貸_MTD_達成率(%)";
			csvHeader[j++] = "一般信貸_YTD_撥款金額";
			csvHeader[j++] = "一般信貸_YTD_目標";
			csvHeader[j++] = "一般信貸_YTD_達成率(%)";
			csvHeader[j++] = "職團信貸_月目標";
			csvHeader[j++] = "職團信貸_當週_進件金額";
			csvHeader[j++] = "職團信貸_當週_撥款金額";
			csvHeader[j++] = "職團信貸_當週_達成率(%)";
			csvHeader[j++] = "職團信貸_MTD_進件金額";
			csvHeader[j++] = "職團信貸_MTD_撥款金額";
			csvHeader[j++] = "職團信貸_MTD_達成率(%)";
			csvHeader[j++] = "職團信貸_YTD_撥款金額";
			csvHeader[j++] = "職團信貸_YTD_目標";
			csvHeader[j++] = "職團信貸_YTD_達成率(%)";
			csvHeader[j++] = "卡友信貸_月目標";
			csvHeader[j++] = "卡友信貸_當週_進件金額";
			csvHeader[j++] = "卡友信貸_當週_撥款金額";
			csvHeader[j++] = "卡友信貸_當週_達成率(%)";
			csvHeader[j++] = "卡友信貸_MTD_進件金額";
			csvHeader[j++] = "卡友信貸_MTD_撥款金額";
			csvHeader[j++] = "卡友信貸_MTD_達成率(%)";
			csvHeader[j++] = "卡友信貸_YTD_撥款金額";
			csvHeader[j++] = "卡友信貸_YTD_目標";
			csvHeader[j++] = "卡友信貸_YTD_達成率(%)";
			csvHeader[j++] = "認股信貸_當週_進件金額";
			csvHeader[j++] = "認股信貸_當週_撥款金額";
			csvHeader[j++] = "認股信貸_MTD_進件金額";
			csvHeader[j++] = "認股信貸_MTD_撥款金額";
			csvHeader[j++] = "認股信貸_YTD_撥款金額";
			csvHeader[j++] = "認股信貸_YTD_目標";
			csvHeader[j++] = "信貸合計(不含認股信貸)_月目標";
			csvHeader[j++] = "信貸合計(不含認股信貸)_當週_進件金額";
			csvHeader[j++] = "信貸合計(不含認股信貸)_當週_撥款金額";
			csvHeader[j++] = "信貸合計(不含認股信貸)_當週_達成率(%)";
			csvHeader[j++] = "信貸合計(不含認股信貸)_MTD_進件金額";
			csvHeader[j++] = "信貸合計(不含認股信貸)_MTD_撥款金額";
			csvHeader[j++] = "信貸合計(不含認股信貸)_MTD_達成率(%)";
			csvHeader[j++] = "信貸合計(不含認股信貸)_YTD_撥款金額";
			csvHeader[j++] = "信貸合計(不含認股信貸)_YTD_目標";
			csvHeader[j++] = "信貸合計(不含認股信貸)_YTD_達成率(%)";
			csvHeader[j++] = "信貸合計(不含認股信貸)_YTD_排名";
			csvHeader[j++] = "好運貸_月目標";
			csvHeader[j++] = "好運貸_當週_核實保費";
			csvHeader[j++] = "好運貸_當週_達成率(%)";
			csvHeader[j++] = "好運貸_MTD_核實保費";
			csvHeader[j++] = "好運貸_MTD_達成率(%)";
			csvHeader[j++] = "好運貸_YTD_核實保費";
			csvHeader[j++] = "好運貸_YTD_目標";
			csvHeader[j++] = "好運貸_YTD_達成率(%)";
			csvHeader[j++] = "好運貸_YTD_排名";
		} else {
			csvHeader[j++] = "業務處";
			csvHeader[j++] = "營運區";
			csvHeader[j++] = "分行代號";
			csvHeader[j++] = "分行名稱";
			csvHeader[j++] = "消金專員姓名";
			csvHeader[j++] = "分期型_購屋_月目標";
			csvHeader[j++] = "分期型_購屋_當週_進件金額";
			csvHeader[j++] = "分期型_購屋_當週_撥款金額";
			csvHeader[j++] = "分期型_購屋_當週_達成率(%)";
			csvHeader[j++] = "分期型_購屋_MTD_進件金額";
			csvHeader[j++] = "分期型_購屋_MTD_撥款金額";
			csvHeader[j++] = "分期型_購屋_MTD_達成率(%)";
			csvHeader[j++] = "分期型_購屋_YTD_撥款金額";
			csvHeader[j++] = "分期型_購屋_YTD_目標";
			csvHeader[j++] = "分期型_購屋_YTD_達成率(%)";
			csvHeader[j++] = "分期型_非購屋_月目標";
			csvHeader[j++] = "分期型_非購屋_當週_進件金額";
			csvHeader[j++] = "分期型_非購屋_當週_撥款金額";
			csvHeader[j++] = "分期型_非購屋_當週_達成率(%)";
			csvHeader[j++] = "分期型_非購屋_MTD_進件金額";
			csvHeader[j++] = "分期型_非購屋_MTD_撥款金額";
			csvHeader[j++] = "分期型_非購屋_MTD_達成率(%)";
			csvHeader[j++] = "分期型_非購屋_YTD_撥款金額";
			csvHeader[j++] = "分期型_非購屋_YTD_目標";
			csvHeader[j++] = "分期型_非購屋_YTD_達成率(%)";
			csvHeader[j++] = "循環型房貸_月目標";
			csvHeader[j++] = "循環型房貸_當週_進件金額";
			csvHeader[j++] = "循環型房貸_當週_撥款金額";
			csvHeader[j++] = "循環型房貸_當週_達成率(%)";
			csvHeader[j++] = "循環型房貸_MTD_進件金額";
			csvHeader[j++] = "循環型房貸_MTD_撥款金額";
			csvHeader[j++] = "循環型房貸_MTD_達成率(%)";
			csvHeader[j++] = "循環型房貸_YTD_撥款金額";
			csvHeader[j++] = "循環型房貸_YTD_目標";
			csvHeader[j++] = "循環型房貸_YTD_達成率(%)";
			csvHeader[j++] = "房貸合計_月目標";
			csvHeader[j++] = "房貸合計_當週_進件金額";
			csvHeader[j++] = "房貸合計_當週_撥款金額";
			csvHeader[j++] = "房貸合計_當週_達成率(%)";
			csvHeader[j++] = "房貸合計_MTD_進件金額";
			csvHeader[j++] = "房貸合計_MTD_撥款金額";
			csvHeader[j++] = "房貸合計_MTD_達成率(%)";
			csvHeader[j++] = "房貸合計_YTD_撥款金額";
			csvHeader[j++] = "房貸合計_YTD_目標";
			csvHeader[j++] = "房貸合計_YTD_達成率(%)";
		}

		return csvHeader;
	}

	/**
	 * 匯出檔案body
	 * 
	 * */
	private List setBody(List<Map<String, Object>> list, int rec) {
		List listCSV = new ArrayList();
		for (Map<String, Object> map : list) {
			String[] records = new String[rec];
			int i = 0;
			// records
			if (rec == 62) {
				records[i++] = checkIsNull(map, "REGION_CENTER_NAME");
				records[i++] = checkIsNull(map, "BRANCH_AREA_NAME");
				records[i++] = checkIsNull(map, "BRANCH_NBR");
				records[i++] = checkIsNull(map, "BRANCH_NAME");
				records[i++] = checkIsNull(map, "EMP_NAME");
				records[i++] = checkIsNull(map, "EMP_ID");
				records[i++] = checkIsNull(map, "NC_TAR");
				records[i++] = checkIsNull(map, "NC_W_IA");
				records[i++] = checkIsNull(map, "NC_W_OA");
				records[i++] = checkIsNull(map, "NC_W_RATE");
				records[i++] = checkIsNull(map, "NC_MTD_IA");
				records[i++] = checkIsNull(map, "NC_MTD_OA");
				records[i++] = checkIsNull(map, "NC_MTD_RATE");
				records[i++] = checkIsNull(map, "NC_YTD_OA");
				records[i++] = checkIsNull(map, "NC_YTD_TAR");
				records[i++] = checkIsNull(map, "NC_YTD_RATE");
				records[i++] = checkIsNull(map, "GC_TAR");
				records[i++] = checkIsNull(map, "GC_W_IA");
				records[i++] = checkIsNull(map, "GC_W_OA");
				records[i++] = checkIsNull(map, "GC_W_RATE");
				records[i++] = checkIsNull(map, "GC_MTD_IA");
				records[i++] = checkIsNull(map, "GC_MTD_OA");
				records[i++] = checkIsNull(map, "GC_MTD_RATE");
				records[i++] = checkIsNull(map, "GC_YTD_OA");
				records[i++] = checkIsNull(map, "GC_YTD_TAR");
				records[i++] = checkIsNull(map, "GC_YTD_RATE");
				records[i++] = checkIsNull(map, "CC_TAR");
				records[i++] = checkIsNull(map, "CC_W_IA");
				records[i++] = checkIsNull(map, "CC_W_OA");
				records[i++] = checkIsNull(map, "CC_W_RATE");
				records[i++] = checkIsNull(map, "CC_MTD_IA");
				records[i++] = checkIsNull(map, "CC_MTD_OA");
				records[i++] = checkIsNull(map, "CC_MTD_RATE");
				records[i++] = checkIsNull(map, "CC_YTD_OA");
				records[i++] = checkIsNull(map, "CC_YTD_TAR");
				records[i++] = checkIsNull(map, "CC_YTD_RATE");
				records[i++] = checkIsNull(map, "C_W_IA");
				records[i++] = checkIsNull(map, "C_W_OA");
				records[i++] = checkIsNull(map, "C_MTD_IA");
				records[i++] = checkIsNull(map, "C_MTD_OA");
				records[i++] = checkIsNull(map, "C_YTD_OA");
				records[i++] = checkIsNull(map, "C_YTD_TAR");
				records[i++] = checkIsNull(map, "CT_TAR");
				records[i++] = checkIsNull(map, "CT_W_IA");
				records[i++] = checkIsNull(map, "CT_W_OA");
				records[i++] = checkIsNull(map, "CT_W_RATE");
				records[i++] = checkIsNull(map, "CT_MTD_IA");
				records[i++] = checkIsNull(map, "CT_MTD_OA");
				records[i++] = checkIsNull(map, "CT_MTD_RATE");
				records[i++] = checkIsNull(map, "CT_YTD_OA");
				records[i++] = checkIsNull(map, "CT_YTD_TAR");
				records[i++] = checkIsNull(map, "CT_YTD_RATE");
				records[i++] = checkIsNull(map, "CT_YTD_RANK");
				records[i++] = checkIsNull(map, "E_TAR");
				records[i++] = checkIsNull(map, "E_W_AF");
				records[i++] = checkIsNull(map, "E_W_RATE");
				records[i++] = checkIsNull(map, "E_MTD_AF");
				records[i++] = checkIsNull(map, "E_MTD_RATE");
				records[i++] = checkIsNull(map, "E_YTD_AF");
				records[i++] = checkIsNull(map, "E_YTD_TAR");
				records[i++] = checkIsNull(map, "E_YTD_RATE");
				records[i++] = checkIsNull(map, "E_YTD_RANK");
			} else {
				records[i++] = checkIsNull(map, "REGION_CENTER_NAME");
				records[i++] = checkIsNull(map, "BRANCH_AREA_NAME");
				records[i++] = checkIsNull(map, "BRANCH_NBR");
				records[i++] = checkIsNull(map, "BRANCH_NAME");
				records[i++] = checkIsNull(map, "EMP_NAME");
				records[i++] = checkIsNull(map, "S_HB_TAR");
				records[i++] = checkIsNull(map, "S_HB_IN_AMT");
				records[i++] = checkIsNull(map, "S_HB_OUT_AMT");
				records[i++] = checkIsNull(map, "S_HB_RATE");
				records[i++] = checkIsNull(map, "S_HB_MTD_IN_AMT");
				records[i++] = checkIsNull(map, "S_HB_MTD_OUT_AMT");
				records[i++] = checkIsNull(map, "S_HB_MTD_RATE");
				records[i++] = checkIsNull(map, "S_HB_YTD_OUT_AMT");
				records[i++] = checkIsNull(map, "S_HB_YTD_TAR");
				records[i++] = checkIsNull(map, "S_HB_YTD_RATE");
				records[i++] = checkIsNull(map, "S_NHB_TAR");
				records[i++] = checkIsNull(map, "S_NHB_IN_AMT");
				records[i++] = checkIsNull(map, "S_NHB_OUT_AMT");
				records[i++] = checkIsNull(map, "S_NHB_RATE");
				records[i++] = checkIsNull(map, "S_NHB_MTD_INT_AMT");
				records[i++] = checkIsNull(map, "S_NHB_MTD_OUT_AMT");
				records[i++] = checkIsNull(map, "S_NHB_MTD_RATE");
				records[i++] = checkIsNull(map, "S_NHB_YTD_OUT_AMT");
				records[i++] = checkIsNull(map, "S_NHB_YTD_TAR");
				records[i++] = checkIsNull(map, "S_NHB_YTD_RATE");
				records[i++] = checkIsNull(map, "C_NHB_TAR");
				records[i++] = checkIsNull(map, "C_NHB_IN_AMT");
				records[i++] = checkIsNull(map, "C_NHB_OUT_AMT");
				records[i++] = checkIsNull(map, "C_NHB_RATE");
				records[i++] = checkIsNull(map, "C_NHB_MTD_IN_AMT");
				records[i++] = checkIsNull(map, "C_NHB_MTD_OUT_AMT");
				records[i++] = checkIsNull(map, "C_NHB_MTD_RATE");
				records[i++] = checkIsNull(map, "C_NHB_YTD_OUT_AMT");
				records[i++] = checkIsNull(map, "C_NHB_YTD_TAR");
				records[i++] = checkIsNull(map, "C_NHB_YTD_RATE");
				records[i++] = checkIsNull(map, "MRTG_TAR");
				records[i++] = checkIsNull(map, "MRTG_IN_AMT");
				records[i++] = checkIsNull(map, "MRTG_OUT_AMT");
				records[i++] = checkIsNull(map, "MRTG_RATE");
				records[i++] = checkIsNull(map, "MRTG_MTD_IN_AMT");
				records[i++] = checkIsNull(map, "MRTG_MTD_OUT_AMT");
				records[i++] = checkIsNull(map, "MRTG_MTD_RATE");
				records[i++] = checkIsNull(map, "MRTG_YTD_OUT_AMT");
				records[i++] = checkIsNull(map, "MRTG_YTD_TAR");
				records[i++] = checkIsNull(map, "MRTG_YTD_RATE");
			}
			listCSV.add(records);
		}
		return listCSV;
	}
}