package com.systex.jbranch.app.server.fps.pms409;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.fubon.jlb.DataFormat;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * Create Author : 2016/05/17 _ Frank 
 */

@Component("pms409")
@Scope("prototype")
public class PMS409 extends FubonWmsBizLogic {
	
	private DataAccessManager dam = null;

	public void queryData(Object body, IPrimitiveMap header) throws JBranchException {
		
		PMS409OutputVO outputVO = new PMS409OutputVO();
		outputVO = this.queryData(body);

		sendRtnObject(outputVO);
	}

	public PMS409OutputVO queryData(Object body) throws JBranchException {
		
		initUUID();
		
		PMS409InputVO inputVO = (PMS409InputVO) body;
		PMS409OutputVO outputVO = new PMS409OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		StringBuffer sql = new StringBuffer();
		sql.append("SELECT ROWNUM, T.* ");
		sql.append("FROM ( ");
		sql.append("  SELECT ORG.REGION_CENTER_NAME, ");
		sql.append("         ORG.BRANCH_AREA_NAME, ");
		sql.append("         RPT.BRANCH_NBR, ");
		sql.append("         ORG.BRANCH_NAME, ");
		sql.append("         NVL(HIS.AO_CODE, '') AS AO_CODE, ");
		sql.append("         NVL(HIS.CREATE_DATE, '') AS CREATETIME, ");
		sql.append("         RPT.LASTUPDATE, ");
		sql.append("         NVL(HIS.CUST_ID, '') AS CUST_ID, ");
		sql.append("         NVL(HIS.CUST_NAME, '') AS CUST_NAME, ");
		sql.append("         NVL(HIS.CUST_RISK_AFR, '') AS CUST_RISK_AFR, ");
		sql.append("         NVL(HIS.OUT_ACCESS, '') AS OUT_ACCESS, ");
		sql.append("         DECODE(STATUS, '04', 'Y', 'N') AS DEL_FLAG, ");
		sql.append("         NVL(HIS.DEL_TYPE, '') AS DEL_TYPE, ");
		sql.append("         (CASE WHEN HIS.DEL_TYPE = '1' THEN '資料鍵檔錯誤' ");
		sql.append("               WHEN HIS.DEL_TYPE = '3' THEN '行外承作KYC問卷，電話照會有疑慮者' ");
		sql.append("               WHEN HIS.DEL_TYPE = '4' THEN '客戶所填資料與實際狀況不符' ");
		sql.append("               WHEN HIS.DEL_TYPE = '5' THEN '其他' ");
		sql.append("          END) AS DEL_REASON, ");
		sql.append("         RPT.YEARMON, ");
		sql.append("         RPT.SUPERVISOR_FLAG, ");
		sql.append("         RPT.MODIFIER, ");
		sql.append("         RPT.SEQ, ");
		sql.append("         RPT.CREATETIME AS CDATE, ");
		sql.append("         HIS.SIGNOFF_DATE, ");
		sql.append("         PA.PARAM_NAME AS OUT_ACCESS_VALUE ");
		sql.append("  FROM TBPMS_EXCEPT_KYC_RPT RPT ");
		sql.append("  LEFT JOIN TBKYC_INVESTOREXAM_M_HIS HIS ON RPT.SEQ = HIS.SEQ ");
		sql.append("  LEFT JOIN TBPMS_ORG_REC_N ORG ON ORG.DEPT_ID = RPT.BRANCH_NBR AND LAST_DAY(TO_DATE(RPT.YEARMON, 'yyyyMM')) BETWEEN ORG.START_TIME AND ORG.END_TIME ");
		sql.append("  LEFT JOIN TBSYSPARAMETER PA ON PA.PARAM_CODE = HIS.OUT_ACCESS AND PA.PARAM_TYPE='KYC.OUT_ACCESS' ");
		sql.append("  WHERE 1 = 1 ");
		
		sql.append("  AND RPT.YEARMON = :ym ");
		condition.setObject("ym", inputVO.getsCreDate());

		//AO_CODE
		if (StringUtils.isNotBlank(inputVO.getAo_code())) {
			sql.append("  AND HIS.AO_CODE = :aocode ");
			condition.setObject("aocode", inputVO.getAo_code());
		}
		
		if (StringUtils.lowerCase(inputVO.getMemLoginFlag()).indexOf("uhrm") < 0 || 
			StringUtils.lowerCase(inputVO.getMemLoginFlag()).equals("uhrm")) {
		
			// by Willis 20171024 此條件因為發現組織換區有異動(例如:東寧分行在正式環境10/1從西台南區換至東台南區)，跟之前組織對應會有問題，改為對應目前最新組織分行別
			if (StringUtils.isNotBlank(inputVO.getBranch_nbr())) {	// 分行
				sql.append("  AND RPT.BRANCH_NBR = :BRNCH_NBRR ");
				condition.setObject("BRNCH_NBRR", inputVO.getBranch_nbr());
			} else if (StringUtils.isNotBlank(inputVO.getBranch_area_id())) {	// 營運區	
				sql.append("  AND RPT.BRANCH_NBR IN ( SELECT BRANCH_NBR FROM VWORG_DEFN_BRH WHERE DEPT_ID = :BRANCH_AREA_IDD ) ");
				condition.setObject("BRANCH_AREA_IDD", inputVO.getBranch_area_id());
			} else if (StringUtils.isNotBlank(inputVO.getRegion_center_id())) {	// 區域中心	
				sql.append("  AND RPT.BRANCH_NBR IN ( SELECT BRANCH_NBR FROM VWORG_DEFN_BRH WHERE DEPT_ID = :REGION_CENTER_IDD ) ");
				condition.setObject("REGION_CENTER_IDD", inputVO.getRegion_center_id());
			}
		} else {
			sql.append("  AND RPT.RM_FLAG = 'U' ");
		}

		//由工作首頁CRM181過來，只須查詢主管尚未確認資料
		if (StringUtils.equals("Y", inputVO.getNeedConfirmYN())) {
			sql.append("  AND NVL(RPT.SUPERVISOR_FLAG, 'N') <> 'Y' ");
		}

		sql.append("  ORDER BY ORG.REGION_CENTER_ID, ORG.BRANCH_AREA_ID, RPT.BRANCH_NBR");
		sql.append(") T ");

		condition.setQueryString(sql.toString());

		List<Map<String, Object>> list = dam.exeQuery(condition);

		outputVO.setTotalList(list);
		outputVO.setResultList(list);

		return outputVO;
	}

	/* 更新資料 */
	public void save(Object body, IPrimitiveMap header) throws JBranchException {
		
		PMS409InputVO inputVO = (PMS409InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		for (Map<String, Object> map : inputVO.getList()) {
			for (Map<String, Object> map2 : inputVO.getList2()) {
				String s_flag1 = map.get("SUPERVISOR_FLAG") == null ? "" : map.get("SUPERVISOR_FLAG").toString(); //NEW
				String s_flag2 = map2.get("SUPERVISOR_FLAG") == null ? "" : map2.get("SUPERVISOR_FLAG").toString(); //OLD

				if (map.get("SEQ").equals(map2.get("SEQ")) && map.get("BRANCH_NBR").equals(map2.get("BRANCH_NBR")) && !s_flag1.equals(s_flag2)) {
					sql = new StringBuffer();
					condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					
					sql.append("UPDATE TBPMS_EXCEPT_KYC_RPT ");
					sql.append("SET SUPERVISOR_FLAG = :superflag, ");
					sql.append("    MODIFIER = :mod, ");
					sql.append("    LASTUPDATE = SYSDATE  ");
					sql.append("WHERE SEQ = :seq ");
					sql.append("AND BRANCH_NBR = :br_id ");
					sql.append("AND YEARMON=:YEARMON");
					
					condition.setQueryString(sql.toString());
					
					condition.setObject("seq", map.get("SEQ").toString());
					condition.setObject("br_id", map.get("BRANCH_NBR").toString());
					condition.setObject("superflag", map.get("SUPERVISOR_FLAG").toString());
					condition.setObject("YEARMON", map.get("YEARMON").toString());
					condition.setObject("mod", (String) getUserVariable(FubonSystemVariableConsts.LOGINID));
					
					dam.exeUpdate(condition);
				}
			}
		}
		
		sendRtnObject(null);
	}

	/* 產出CSV */
	public void export(Object body, IPrimitiveMap header) throws JBranchException {
		
		PMS409OutputVO outputVO = (PMS409OutputVO) body;
		List<Map<String, Object>> list = outputVO.getTotalList();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String fileName = "KYC例外管理報表_" + sdf.format(new Date()) + ".csv";
		List listCSV = new ArrayList();
		
		for (Map<String, Object> map : list) {
			String[] records = new String[17];
			int i = 0;

			records[i] = ((int) Double.parseDouble(checkIsNull(map, "ROWNUM").toString())) + ""; // 序號 - 去小數點
			records[++i] = checkIsNull(map, "REGION_CENTER_NAME"); //區部名稱
			records[++i] = checkIsNull(map, "BRANCH_AREA_NAME"); //區名
			records[++i] = checkIsNull(map, "BRANCH_NBR"); //分行代碼
			records[++i] = checkIsNull(map, "BRANCH_NAME"); //分行名稱
			records[++i] = checkIsNull(map, "AO_CODE"); //AO Code
			records[++i] = checkIsNull(map, "CREATETIME"); //測試日期
			
			if ("N".equals(map.get("DEL_FLAG")))
				records[++i] = checkIsNull(map, "SIGNOFF_DATE"); //覆核日期
			else
				records[++i] = checkIsNull(map, ""); //覆核日期
			
			if (map.get("CUST_ID") == null || "".equals(map.get("CUST_ID")))
				records[++i] = "本月無資料";
			else
				records[++i] = checkIsNull(map, "CUST_ID"); //客戶ID
			
			records[++i] = checkIsNull(map, "CUST_NAME"); //客戶姓名
			records[++i] = checkIsNull(map, "CUST_RISK_AFR"); //風險承受度
			records[++i] = checkIsNull(map, "OUT_ACCESS_VALUE"); //是否為行外KYC				
			records[++i] = checkIsNullYn(map, "DEL_FLAG"); //KYC是否已刪除
			records[++i] = checkIsNull(map, "DEL_REASON"); //刪除原因
			records[++i] = checkIsNull(map, "SUPERVISOR_FLAG"); //主管確認
			records[++i] = checkIsNull(map, "LASTUPDATE"); //覆核時間
			records[++i] = checkIsNull(map, "MODIFIER"); //覆核人員
			
			listCSV.add(records);
		}

		String[] csvHeader = new String[17];
		int j = 0;
		csvHeader[j] = "序號";
		csvHeader[++j] = "業務處名稱";
		csvHeader[++j] = "區名";
		csvHeader[++j] = "分行代碼";
		csvHeader[++j] = "分行名稱";
		csvHeader[++j] = "AO Code";
		csvHeader[++j] = "測試日期";
		csvHeader[++j] = "覆核日期";
		csvHeader[++j] = "客戶ID";
		csvHeader[++j] = "客戶姓名";
		csvHeader[++j] = "風險承受度";
		csvHeader[++j] = "是否為行外KYC";
		csvHeader[++j] = "KYC是否已刪除";
		csvHeader[++j] = "刪除原因";
		csvHeader[++j] = "主管確認";
		csvHeader[++j] = "覆核時間";
		csvHeader[++j] = "覆核人員";

		CSVUtil csv = new CSVUtil();
		csv.setHeader(csvHeader);
		csv.addRecordList(listCSV);
		
		String url = csv.generateCSV();
		
		notifyClientToDownloadFile(url, fileName);
		
		this.sendRtnObject(null);
	}

	/* 檢查Map取出欄位是否為Null */
	private String checkIsNull(Map map, String key) {
		if (StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key) != null) {
			if ("CUST_ID".equals(key)) {
				return DataFormat.getCustIdMaskForHighRisk(String.valueOf(map.get(key)));
			} else {
				return String.valueOf(map.get(key));
			}
		} else {
			return "";
		}
	}

	private String checkIsNullYn(Map map, String key) {
		
		if (StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key) != null) {
			if ("Y".equals(String.valueOf(map.get(key)))) {
				return "是";
			} else {
				return "否";
			}
		} else {
			return "";
		}
	}
}