package com.systex.jbranch.app.server.fps.pms405;

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

import com.systex.jbranch.app.common.fps.table.TBPMS_MON_TERM_INT_CDPK;
import com.systex.jbranch.app.common.fps.table.TBPMS_MON_TERM_INT_CDVO;
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
 * Description :定存中解不打折特殊客戶投保明細月報<br>
 * Comments Name : PMS405.java<br>
 * Author : Kevin<br>
 * Date :2016/05/24 <br>
 * Version : 1.0 <br>
 * Editor : Kevin<br>
 * Editor Date : 2017年01月30日<br>
 */

@Component("pms405")
@Scope("request")
public class PMS405 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;

	Logger logger = LoggerFactory.getLogger(this.getClass());

	public void queryData(Object body, IPrimitiveMap header)
			throws JBranchException {
		// 輸入vo
		PMS405InputVO inputVO = (PMS405InputVO) body;
		// 輸出vo
		PMS405OutputVO outputVO = new PMS405OutputVO();
		dam = this.getDataAccessManager();

		QueryConditionIF condition = dam
				.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		try {
			StringBuffer sql = new StringBuffer("select ROWNUM, t.* from ( ");
			// ==主查詢==
			sql.append("select * from TBPMS_MON_TERM_INT_CD) T where 1=1 ");
			// ==以下為查詢條件==
			if (inputVO.getsCreDate() != null) {
				sql.append(" and T.TERM_DATE >= TO_DATE(:TERM_DATES, 'YYYY-MM-DD') ");
			}
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
			if (inputVO.getsCreDate() != null) {
				condition.setObject("TERM_DATES",
						new java.text.SimpleDateFormat("yyyy/MM/dd")
								.format(inputVO.getsCreDate()));
			}
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
		PMS405InputVO inputVO = (PMS405InputVO) body;
		DataAccessManager dam = this.getDataAccessManager();
		try {
			for (Map<String, Object> map : inputVO.getList()) {
				Timestamp ts = Timestamp.valueOf(map.get("TERM_DATE")
						.toString());

				TBPMS_MON_TERM_INT_CDPK PK = new TBPMS_MON_TERM_INT_CDPK();
				PK.setCERT_NBR(map.get("CERT_NBR").toString());
				PK.setDATA_YEARMON(map.get("DATA_YEARMON").toString());
				TBPMS_MON_TERM_INT_CDVO paramVO = (TBPMS_MON_TERM_INT_CDVO) dam
						.findByPKey(TBPMS_MON_TERM_INT_CDVO.TABLE_UID, PK);
				paramVO.setNOTE(map.get("NOTE").toString());
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
		PMS405OutputVO return_VO = (PMS405OutputVO) body;

		List<Map<String, Object>> list = return_VO.getList();
		try {
			if (list.size() > 0) {
				// gen csv
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
				String fileName = "定存中解利息不打折特殊客戶明月報" + sdf.format(new Date())
						+ "_員工編號.csv";
				List listCSV = new ArrayList();
				for (Map<String, Object> map : list) {
					// 21 column
					String[] records = new String[21];
					int i = 0;
					records[i] = checkIsNull(map, "DATA_YEARMON"); // 資料統計年月
					records[++i] = checkIsNull(map, "TERM_DATE"); // 解約日
					records[++i] = checkIsNull(map, "REGION_CENTER_ID"); // 區域中心ID
					records[++i] = checkIsNull(map, "REGION_CENTER_NAME"); // 區域中心
					records[++i] = checkIsNull(map, "BRANCH_AREA_ID"); // 營運區中心ID
					records[++i] = checkIsNull(map, "BRANCH_AREA_NAME"); // 營運區中心
					records[++i] = checkIsNull(map, "BRANCH_NBR"); // 分行代碼
					records[++i] = checkIsNull(map, "BRANCH_NAME"); // 分行
					records[++i] = checkIsNull(map, "AO_CODE"); // 理專
					records[++i] = checkIsNull(map, "CUST_ID"); // 客戶ID
					records[++i] = checkIsNull(map, "CUST_NAME"); // 客戶姓名
					records[++i] = checkIsNull(map, "EMP_ID");
					records[++i] = checkIsNull(map, "PRD_TYPE"); // 承作商品類型
					records[++i] = checkIsNull(map, "MAKE_DATE"); // 承作日
					records[++i] = checkIsNull(map, "CRCY_TYPE"); // 幣別
					records[++i] = checkIsNull(map, "AMT"); // 投資金額
					records[++i] = checkIsNull(map, "CERT_NBR"); // 憑證編號/送件編號
					records[++i] = checkIsNull(map, "PRD_NBR"); // 商品代碼
					records[++i] = checkIsNull(map, "PRD_NAME"); // 商品名稱
					records[++i] = checkIsNull(map, "RISK_ATTR"); // 風險屬性
					records[++i] = checkIsNull(map, "NOTE"); // 備註

					listCSV.add(records);
				}
				// header
				String[] csvHeader = new String[21];
				int j = 0;
				csvHeader[j] = "資料統計年月";
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
				csvHeader[++j] = "承作商品類型";
				csvHeader[++j] = "承作日";
				csvHeader[++j] = "幣別";
				csvHeader[++j] = "投資金額";
				csvHeader[++j] = "憑證編號/送件編號";
				csvHeader[++j] = "商品代碼";
				csvHeader[++j] = "商品名稱";
				csvHeader[++j] = "風險屬性";
				csvHeader[++j] = "備註";

				CSVUtil csv = new CSVUtil(); // new新csv
				csv.setHeader(csvHeader); // 設定標頭
				csv.addRecordList(listCSV); // 設定內容
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

	private String checkIsNull(Map map, String key) {
		if (StringUtils.isNotBlank(String.valueOf(map.get(key)))) {
			return String.valueOf(map.get(key));
		} else {
			return "";
		}
	}

}