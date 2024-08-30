package com.systex.jbranch.app.server.fps.pms404;

import java.sql.Timestamp;
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

import com.systex.jbranch.app.common.fps.table.TBPMS_DAILY_SALES_DEPPK;
import com.systex.jbranch.app.common.fps.table.TBPMS_DAY_TERM_INT_CDVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * Copy Right Information :<br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description :定存中解利息不打折特殊客戶明細日報<br>
 * Comments Name : PMS404.java<br>
 * Author : Kevin<br>
 * Date :2016/05/24 <br>
 * Version : 1.0 <br>
 * Editor : Kevin<br>
 * Editor Date : 2017年01月30日<br>
 */

@Component("pms404")
@Scope("request")
public class PMS404 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(PMS404.class);

	/** ==主資料查詢== **/
	public void queryData(Object body, IPrimitiveMap header)
			throws JBranchException {
		// 輸入vo
		PMS404InputVO inputVO = (PMS404InputVO) body;
		// 輸出vo
		PMS404OutputVO outputVO = new PMS404OutputVO();
		DataAccessManager dam = this.getDataAccessManager();
		try {
			QueryConditionIF condition = dam
					.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			ArrayList<String> sql_list = new ArrayList<String>();
			// ==主查詢==
			StringBuffer sql = new StringBuffer("select ROWNUM, t.* from ( ");
			sql.append("select * from TBPMS_DAY_TERM_INT_CD) T where 1=1  ");

			// ==以下為查詢條件==
			// 解約日起
			if (inputVO.getsCreDate() != null) {
				sql.append(" and T.TERM_DATE >= TO_DATE(:TERM_DATES, 'YYYY-MM-DD') ");
			}
			// 解約日訖
			if (inputVO.geteCreDate() != null) {
				sql.append(" and T.TERM_DATE <= TO_DATE(:TERM_DATEE, 'YYYY-MM-DD') ");
			}
			// 分行
			if (!StringUtils.isBlank(inputVO.getBr_id())) {
				sql.append(" and T.BRANCH_NBR LIKE :BRANCH_NBRR ");
			}
			// 營運區
			if (!StringUtils.isBlank(inputVO.getOp_id())) {
				sql.append(" and T.BRANCH_AREA_ID LIKE :BRANCH_AREA_IDD  ");
			}
			// 區域中心
			if (!StringUtils.isBlank(inputVO.getRc_id())) {
				sql.append(" and T.REGION_CENTER_ID LIKE :REGION_CENTER_IDD ");
			}
			// 員編
			if (!StringUtils.isBlank(inputVO.getEmp_id())) {
				sql.append(" and T.EMP_ID LIKE :EMP_IDD  ");
			}

			condition.setQueryString(sql.toString());

			// ==以下為設定參數==
			// 分行
			if (!StringUtils.isBlank(inputVO.getBr_id())) {
				condition.setObject("BRANCH_NBRR", inputVO.getBr_id());
			}
			// 營運區
			if (!StringUtils.isBlank(inputVO.getOp_id())) {
				condition.setObject("BRANCH_AREA_IDD", inputVO.getOp_id());
			}
			// 區域中心
			if (!StringUtils.isBlank(inputVO.getRc_id())) {
				condition.setObject("REGION_CENTER_IDD", inputVO.getRc_id());
			}
			// 員編
			if (!StringUtils.isBlank(inputVO.getEmp_id())) {
				condition.setObject("EMP_IDD", inputVO.getEmp_id());
			}
			// 解約日起
			if (inputVO.getsCreDate() != null) {
				condition.setObject("TERM_DATES",
						new java.text.SimpleDateFormat("yyyy/MM/dd")
								.format(inputVO.getsCreDate()));
			}
			// 解約日訖
			if (inputVO.geteCreDate() != null) {
				condition.setObject("TERM_DATEE",
						new java.text.SimpleDateFormat("yyyy/MM/dd")
								.format(inputVO.geteCreDate()));
			}
			// 分頁資訊
			ResultIF list = dam.executePaging(condition,
					inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());

			int totalPage = list.getTotalPage();
			outputVO.setTotalPage(totalPage);
			outputVO.setResultList(list); // 主查詢資訊
			outputVO.setTotalRecord(list.getTotalRecord());
			outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());
			sendRtnObject(outputVO);

		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}

	/* ==== 【儲存】更新資料 ======== */
	public void save(Object body, IPrimitiveMap header) throws JBranchException {
		Timestamp stamp = new Timestamp(System.currentTimeMillis());
		PMS404InputVO inputVO = (PMS404InputVO) body;
		DataAccessManager dam = this.getDataAccessManager();
		try {
			for (Map<String, Object> map : inputVO.getList()) {
				Timestamp ts = Timestamp.valueOf(map.get("TERM_DATE")
						.toString());
				TBPMS_DAILY_SALES_DEPPK pk = new TBPMS_DAILY_SALES_DEPPK();

				TBPMS_DAY_TERM_INT_CDVO paramVO = (TBPMS_DAY_TERM_INT_CDVO) dam
						.findByPKey(TBPMS_DAY_TERM_INT_CDVO.TABLE_UID,
								map.get("DEP_NO").toString());

				paramVO.setNOTE(map.get("NOTE").toString());
				paramVO.setREASON(map.get("REASON").toString());
				paramVO.setModifier("test");
				paramVO.setLastupdate(stamp);

				dam.update(paramVO);
			}
			sendRtnObject(null);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}

	/* === 產出Excel==== */
	public void export(Object body, IPrimitiveMap header)
			throws JBranchException {
		// 取得畫面資料
		PMS404OutputVO return_VO = (PMS404OutputVO) body;

		List<Map<String, Object>> list = return_VO.getList();
		try {
			if (list.size() > 0) {
				// gen csv
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
				String fileName = "定存中解利息不打折特殊客戶明日報" + sdf.format(new Date())
						+ "_員工編號.csv";
				List listCSV = new ArrayList();
				for (Map<String, Object> map : list) {
					// 21 column
					String[] records = new String[19];
					int i = 0;
//					records[i] = checkIsNull(map, "ROWNUM").substring(0, 1);
					records[i] =((int)Double.parseDouble(checkIsNull(map, "ROWNUM").toString()))+""; // 序號 - 去小數點
					records[++i] = checkIsNull(map, "TERM_DATE");
					records[++i] = checkIsNull(map, "REGION_CENTER_ID");
					records[++i] = checkIsNull(map, "REGION_CENTER_NAME");
					records[++i] = checkIsNull(map, "BRANCH_AREA_ID");
					records[++i] = checkIsNull(map, "BRANCH_AREA_NAME");
					records[++i] = checkIsNull(map, "BRANCH_NBR");
					records[++i] = checkIsNull(map, "BRANCH_NAME");
					records[++i] = checkIsNull(map, "AO_CODE");
					records[++i] = checkIsNull(map, "CUST_ID");
					records[++i] = checkIsNull(map, "CUST_NAME");
					records[++i] = checkIsNull(map, "EMP_ID");
					records[++i] = checkIsNull(map, "EMP_NAME");
					records[++i] = checkIsNull(map, "ACCOUNT");
					records[++i] = checkIsNull(map, "DEP_NO");
					records[++i] = checkIsNull(map, "CRCY_TYPE");
					records[++i] = checkIsNull(map, "AMT");
					records[++i] = checkIsNull(map, "REASON");
					records[++i] = checkIsNull(map, "NOTE");

					listCSV.add(records);
				}
				// header
				String[] csvHeader = new String[19];
				int j = 0;
				csvHeader[j] = "序號";
				csvHeader[++j] = "解約日";
				csvHeader[++j] = "業務處ID";
				csvHeader[++j] = "業務處名稱";
				csvHeader[++j] = "營運區ID";
				csvHeader[++j] = "營運區名稱";
				csvHeader[++j] = "分行代碼";
				csvHeader[++j] = "分行名稱";
				csvHeader[++j] = "AO Code";
				csvHeader[++j] = "客戶ID";
				csvHeader[++j] = "客戶姓名";
				csvHeader[++j] = "理專員編";
				csvHeader[++j] = "員工姓名";
				csvHeader[++j] = "帳號";
				csvHeader[++j] = "存單號碼";
				csvHeader[++j] = "幣別";
				csvHeader[++j] = "金額";
				csvHeader[++j] = "解約原因";
				csvHeader[++j] = "備註";

				CSVUtil csv = new CSVUtil();
				csv.setHeader(csvHeader);
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

	/** ==判斷NULL回傳字串== **/
	private String checkIsNull(Map map, String key) {
		if (StringUtils.isNotBlank(String.valueOf(map.get(key)))) {
			return String.valueOf(map.get(key));
		} else {
			return "";
		}
	}

}