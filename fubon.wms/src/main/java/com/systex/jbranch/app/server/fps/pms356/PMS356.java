package com.systex.jbranch.app.server.fps.pms356;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.server.fps.pms357.PMS357InputVO;
import com.systex.jbranch.app.server.fps.pms357.PMS357OutputVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * Copy Right Information :<br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description :存投保收益報表<br>
 * Comments Name : PMS356java<br>
 * Author : Kevin<br>
 * Date :2016/08/10 <br>
 * Version : 1.0 <br>
 * Editor : Kevin<br>
 * Editor Date : 2017年01月30日<br>
 */

@Component("pms356")
@Scope("request")
public class PMS356 extends FubonWmsBizLogic {
	public DataAccessManager dam = null;
	
	/******* 主查詢 ********/
	public void inquire(Object body, IPrimitiveMap header)
			throws JBranchException {

		PMS356InputVO inputVO = (PMS356InputVO) body;
		PMS356OutputVO outputVO = new PMS356OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam
				.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		try {
			// ==主查詢==
			sb.append(" select rownum as num,t.* from (select * from TBPMS_FTP_PROFIT) t  ");
			sb.append(" where 1=1  ");
			
			// ==主查詢條件==
			//	區域中心
			if (!StringUtils.isBlank(inputVO.getRegion_center_id())) {
				sb.append(" and REGION_CENTER_ID LIKE :REGION_CENTER_IDDD");
			}
			//	營運區	
			if (!StringUtils.isBlank(inputVO.getBranch_area_id())) {
				sb.append(" and BRANCH_AREA_ID LIKE :OP_AREA_IDDD");
			}
			//	分行
			if (!StringUtils.isBlank(inputVO.getBranch_nbr())) {
				sb.append(" and BRANCH_NBR LIKE :BRANCH_NBRR");
			}
			//	年月
			if (!StringUtils.isBlank(inputVO.getReportDate())) {
				sb.append(" and TRIM( YEARMON ) = :YEARMONN");
			}

			queryCondition.setQueryString(sb.toString());
			// ==主查詢條件設定==
			//	區域中心
			if (!StringUtils.isBlank(inputVO.getRegion_center_id())) {
				queryCondition.setObject("REGION_CENTER_IDDD",
						"%" + inputVO.getRegion_center_id() + "%");
			}
			//	營運區
			if (!StringUtils.isBlank(inputVO.getBranch_area_id())) {
				queryCondition.setObject("OP_AREA_IDDD",
						"%" + inputVO.getBranch_area_id() + "%");
			}
			//	分行
			if (!StringUtils.isBlank(inputVO.getBranch_nbr())) {
				queryCondition.setObject("BRANCH_NBRR",
						"%" + inputVO.getBranch_nbr() + "%");
			}
			//	年月
			if (!StringUtils.isBlank(inputVO.getReportDate())) {
				queryCondition.setObject("YEARMONN", inputVO.getReportDate());
			}
			sb.append(" order by REGION_CENTER_ID,REGION_CENTER_NAME,BRANCH_AREA_ID,BRANCH_AREA_NAME,BRANCH_NBR,BRANCH_NAME  ");

			//	分頁資訊
			ResultIF list = dam.executePaging(queryCondition,
					inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
			int totalPage_i = list.getTotalPage();
			int totalRecord_i = list.getTotalRecord();
			List<Map<String, Object>> list1 = dam.exeQuery(queryCondition); // 匯出全部用
			//	csv查詢資訊 全部
			outputVO.setCsvList(list1);
			//	主查詢資訊	10頁
			outputVO.setResultList(list1);
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

	/****** 匯出 ******/
	public void export(Object body, IPrimitiveMap header)
			throws JBranchException, FileNotFoundException, IOException {
		PMS356OutputVO return_VO = (PMS356OutputVO) body;
		List<Map<String, Object>> list = return_VO.getList();
		try {
			if (list.size() > 0) {
				// gen csv
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
				String fileName = "存投保收益報表" + sdf.format(new Date())
						+ "_"+(String) getUserVariable(FubonSystemVariableConsts.LOGINID)+".csv";
				List listCSV = new ArrayList();
				for (Map<String, Object> map : list) {
					// 34 column
					String[] records = new String[34];
					int i = 0;
					records[i] = checkIsNull(map, "YEARMON"); // 資料日期
					records[++i] = checkIsNull(map, "REGION_CENTER_ID"); // 區域中心ID
					records[++i] = checkIsNull(map, "REGION_CENTER_NAME"); // 區域中心名稱
					records[++i] = checkIsNull(map, "BRANCH_AREA_ID"); // 營運區ID
					records[++i] = checkIsNull(map, "BRANCH_AREA_NAME"); // 營運區名稱
					records[++i] = checkIsNull(map, "BRANCH_NBR"); // 分行代碼
					records[++i] = checkIsNull(map, "BRANCH_NAME"); // 分行名稱
					records[++i] = checkIsNull(map, "BRANCH_CLS"); // 分行組別
					records[++i] = checkIsNull(map, "ORG_ID"); // 組織代碼
					records[++i] = checkIsNull(map, "MTD_INV_AMT"); // MTD投資實際數
					records[++i] = checkIsNull(map, "MTD_INV_TAR"); // MTD投資目標數
					records[++i] = checkIsNull(map, "MTD_INV_RATE"); // MTD投資達成率
					records[++i] = checkIsNull(map, "MTD_INS_AMT"); // MTD保險實際數
					records[++i] = checkIsNull(map, "MTD_INS_TAR"); // MTD保險目標數
					records[++i] = checkIsNull(map, "MTD_INS_RATE"); // MTD保險達成率
					records[++i] = checkIsNull(map, "MTD_FTP_AMT"); // MTD存款利差實際數
					records[++i] = checkIsNull(map, "MTD_FTP_TAR"); // MTD存款利差目標數
					records[++i] = checkIsNull(map, "MTD_FTP_RATE"); // MTD存款利差達成率
					records[++i] = checkIsNull(map, "MTD_TOTAL_AMT"); // MTD存投保實際數
					records[++i] = checkIsNull(map, "MTD_TOTAL_TAR"); // MTD存投保目標數
					records[++i] = checkIsNull(map, "MTD_TOTAL_RATE"); // MTD存投保達成率
					records[++i] = checkIsNull(map, "YTD_INV_AMT"); // YTD投資實際數
					records[++i] = checkIsNull(map, "YTD_INV_TAR"); // YTD投資目標數
					records[++i] = checkIsNull(map, "YTD_INV_RATE"); // YTD投資達成率
					records[++i] = checkIsNull(map, "YTD_INS_AMT"); // YTD保險實際數
					records[++i] = checkIsNull(map, "YTD_INS_TAR"); // YTD保險目標數
					records[++i] = checkIsNull(map, "YTD_INS_RATE"); // YTD保險達成率
					records[++i] = checkIsNull(map, "YTD_FTP_AMT"); // YTD存款利差實際數
					records[++i] = checkIsNull(map, "YTD_FTP_TAR"); // YTD存款利差目標數
					records[++i] = checkIsNull(map, "YTD_FTP_RATE"); // YTD存款利差達成率
					records[++i] = checkIsNull(map, "YTD_TOTAL_AMT"); // YTD存投保實際數
					records[++i] = checkIsNull(map, "YTD_TOTAL_TAR"); // YTD存投保目標數
					records[++i] = checkIsNull(map, "YTD_TOTAL_RATE"); // YTD存投保達成率
				
					listCSV.add(records);
				}
				// header
				String[] csvHeader = new String[34];
				int j = 0;
				csvHeader[j] = "資料日期";
				csvHeader[++j] = "業務處ID";
				csvHeader[++j] = "業務處名稱";
				csvHeader[++j] = "營運區ID";
				csvHeader[++j] = "營運區名稱";
				csvHeader[++j] = "分行代碼";
				csvHeader[++j] = "分行名稱";
				csvHeader[++j] = "分行組別";
				csvHeader[++j] = "組織代碼";
				csvHeader[++j] = "MTD投資實際數";
				csvHeader[++j] = "MTD投資目標數";
				csvHeader[++j] = "MTD投資達成率(%)";
				csvHeader[++j] = "MTD保險實際數";
				csvHeader[++j] = "MTD保險目標數";
				csvHeader[++j] = "MTD保險達成率(%)";
				csvHeader[++j] = "MTD存款利差實際數";
				csvHeader[++j] = "MTD存款利差目標數";
				csvHeader[++j] = "MTD存款利差達成率(%)";
				csvHeader[++j] = "MTD存投保實際數";
				csvHeader[++j] = "MTD存投保目標數";
				csvHeader[++j] = "MTD存投保達成率(%)";
				csvHeader[++j] = "YTD投資實際數";
				csvHeader[++j] = "YTD投資目標數";
				csvHeader[++j] = "YTD投資達成率(%)";
				csvHeader[++j] = "YTD保險實際數";
				csvHeader[++j] = "YTD保險目標數";
				csvHeader[++j] = "YTD保險達成率(%)";
				csvHeader[++j] = "YTD存款利差實際數";
				csvHeader[++j] = "YTD存款利差目標數";
				csvHeader[++j] = "YTD存款利差達成率(%)";
				csvHeader[++j] = "YTD存投保實際數";
				csvHeader[++j] = "YTD存投保目標數";
				csvHeader[++j] = "YTD存投保達成率(%)";
				
				CSVUtil csv = new CSVUtil();
				csv.setHeader(csvHeader); // 設定標頭
				csv.addRecordList(listCSV); // 設定內容
				String url = csv.generateCSV();
				// download csv
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
		PMS356InputVO inputVO = (PMS356InputVO) body;
		PMS356OutputVO return_VO = new PMS356OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		QueryConditionIF queryCondition2 = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql_view1 = new StringBuffer();
		StringBuffer sql_view2 = new StringBuffer();
		try {
//			sql_view1.append("TRUNCATE  TABLE TBPMS_FTP_PROFIT ");
//			queryCondition.setQueryString(sql_view1.toString());
//			dam.exeUpdate(queryCondition);
			this.sendRtnObject(null);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}

	
	/**
	 * 下載
	 * @param body
	 * @param header
	 * @throws Exception
	 */
	public void downLoad1(Object body, IPrimitiveMap header) throws Exception {
//		PMS713OutputVO return_VO2 = (PMS713OutputVO) body;
//		File filePath = new File(DataManager.getRealPath(), DataManager.getSystem().getPath().get("temp").toString());

		notifyClientToDownloadFile("doc//PMS//pms356_ex.xlsx", "存投保定義_對應損益表項目_範例.xlsx"); //download
//		System.out.println(DataManager.getRealPath()+"doc\\PMS\\業務主管員額清單_上傳範例.xls");
		
		this.sendRtnObject(null);
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

	/**
	 * 取得temp資料夾絕對路徑
	 * 
	 * @return
	 * @throws JBranchException
	 */
	private String getTempPath() throws JBranchException {
		String serverPath = (String) getCommonVariable(SystemVariableConsts.SERVER_PATH);
		String seperator = System.getProperties().getProperty("file.separator");
		if (!serverPath.endsWith(seperator)) {
			serverPath += seperator;
		}
		return serverPath + "temp";
	}
}
