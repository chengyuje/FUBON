package com.systex.jbranch.app.server.fps.pms223;

import java.text.NumberFormat;
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

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * 
 * Copy Right Information : <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description :Controller <br>
 * Comments Name : PMS223.java<br>
 * Author :frank<br>
 * Date :2016年06月30日 <br>
 * Version : 1.01 <br>
 * Editor : Kevin<br>
 * Editor Date : 2017年02月01日<br>
 */

@Component("pms223")
@Scope("request")
public class PMS223 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(PMS223.class);

	public void queryData(Object body, IPrimitiveMap header)
			throws JBranchException {
		PMS223InputVO inputVO = (PMS223InputVO) body;
		PMS223OutputVO outputVO = new PMS223OutputVO();
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition();
		ArrayList<String> sql_list = new ArrayList<String>();
		StringBuffer sql = new StringBuffer("SELECT ROWNUM, T.* FROM ( ");
		sql.append("SELECT a.BRANCH_AREA_NAME, a.BRANCH_NAME, ");
		sql.append("a.EMP_ID, a.EMP_NAME, a.ACH_YR_MN, ");
		sql.append("a.POLICY_NO, a.POLICY_SEQ, a.APPL_NAME, ");
		sql.append("a.INS_TYPE_CODE, a.INS_TYPE_NOTE, ");
		sql.append("b.PARAM_NAME as CNCT_STATE, a.PREM, a.RAISE_FINAL, ");
		sql.append("a.ACH_PRFT, a.PLUS_ACH_YR_MN ");
		sql.append("FROM TBPMS_INS_MINUS_ITEM_RPT a ");
		sql.append("left join TBSYSPARAMETER b ");
		sql.append("on b.PARAM_CODE = a.CNCT_STATE ");
		sql.append("WHERE 1=1 AND b.PARAM_TYPE = 'PMS.CONTRACT_STATE' ");
		// 資料統計日期
		if (inputVO.getDataMonth() != null
				&& !inputVO.getDataMonth().equals("")) {
			sql.append("and a.ACH_YR_MN = ? ");
			sql_list.add(inputVO.getDataMonth());
		}
		// 營運區
		if (inputVO.getOp_id() != null && !inputVO.getOp_id().equals("")) {
			sql.append("and a.BRANCH_AREA_ID = ? ");
			sql_list.add(inputVO.getOp_id());
		}
		// 分行
		if (inputVO.getBr_id() != null && !inputVO.getBr_id().equals("")) {
			sql.append("and a.BRANCH_NBR = ? ");
			sql_list.add(inputVO.getBr_id());
		}
		// 理專員編
		if (inputVO.getEmp_id() != null && !inputVO.getEmp_id().equals("")) {
			sql.append("and a.EMP_ID = ? ");
			sql_list.add(inputVO.getEmp_id());
		}
		sql.append("order by a.BRANCH_AREA_ID, a.BRANCH_NBR ) T ");

		condition.setQueryString(sql.toString());
		for (int sql_i = 0; sql_i < sql_list.size(); sql_i++) {
			condition.setString(sql_i + 1, sql_list.get(sql_i));
		}
		outputVO.setTotalList(dam.executeQuery(condition));
		ResultIF list = dam.executePaging(condition,
				inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
		if (list.size() > 0) {
			int totalPage = list.getTotalPage();
			outputVO.setTotalPage(totalPage);
			outputVO.setResultList(list);
			outputVO.setTotalRecord(list.getTotalRecord());

			outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());
			sendRtnObject(outputVO);
		} else {
			throw new APException("ehl_01_common_009");
		}
	}

	/* === 產出CSV==== */
	public void export(Object body, IPrimitiveMap header)
			throws JBranchException {
		PMS223OutputVO outputVO = (PMS223OutputVO) body;

		List<Map<String, Object>> list = outputVO.getTotalList();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String fileName = "保險負項明細報表_" + sdf.format(new Date()) + ".csv";
		List listCSV = new ArrayList();
		for (Map<String, Object> map : list) {
			String[] records = new String[16];
			int i = 0;
//			records[i] = checkIsNull(map, "ROWNUM");
			records[i] =((int)Double.parseDouble(checkIsNull(map, "ROWNUM").toString()))+""; // 序號 - 去小數點
			records[++i] = checkIsNull(map, "BRANCH_AREA_NAME");
			records[++i] = checkIsNull(map, "BRANCH_NAME");
			records[++i] = checkIsNull(map, "EMP_ID");
			records[++i] = checkIsNull(map, "EMP_NAME");
			records[++i] = checkIsNull(map, "ACH_YR_MN");
			records[++i] = checkIsNull(map, "POLICY_NO");
			records[++i] = checkIsNull(map, "POLICY_SEQ");
			records[++i] = checkIsNull(map, "APPL_NAME");
			records[++i] = checkIsNull(map, "INS_TYPE_CODE");
			records[++i] = checkIsNull(map, "INS_TYPE_NOTE");
			records[++i] = checkIsNull(map, "CNCT_STATE");
			records[++i] = currencyFormat(map, "PREM");
			records[++i] = currencyFormat(map, "RAISE_FINAL");
			records[++i] = currencyFormat(map, "ACH_PRFT");
			records[++i] = checkIsNull(map, "PLUS_ACH_YR_MN");

			listCSV.add(records);
		}
		// header
		String[] csvHeader = new String[16];
		int j = 0;
		csvHeader[j] = "項次";
		csvHeader[++j] = "營運區";
		csvHeader[++j] = "分行別";
		csvHeader[++j] = "專員員工代碼";
		csvHeader[++j] = "專員姓名";
		csvHeader[++j] = "成績年月";
		csvHeader[++j] = "保單號碼";
		csvHeader[++j] = "保單序號";
		csvHeader[++j] = "要保人姓名";
		csvHeader[++j] = "險種代號";
		csvHeader[++j] = "險種說明";
		csvHeader[++j] = "契約狀態";
		csvHeader[++j] = "保費";
		csvHeader[++j] = "加碼FINAL";
		csvHeader[++j] = "計績收益";
		csvHeader[++j] = "正項計績月份";

		CSVUtil csv = new CSVUtil();
		csv.setHeader(csvHeader);
		csv.addRecordList(listCSV);
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

	// 處理貨幣格式
	private String currencyFormat(Map map, String key) {
		if (StringUtils.isNotBlank(String.valueOf(map.get(key)))
				&& map.get(key) != null) {
			NumberFormat nf = NumberFormat.getCurrencyInstance();
			return nf.format(map.get(key));
		} else
			return "0.00";
	}
}