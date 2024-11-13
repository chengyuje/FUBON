package com.systex.jbranch.app.server.fps.pms433;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.fubon.jlb.DataFormat;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * 網行銀高齡商品交易報表
 * PMS432
 * 
 * @author Sam Tu
 * @date 2024/07/10
 * @spec null
 */
@Component("pms433")
@Scope("request")
@SuppressWarnings({ "rawtypes", "unchecked" })
public class PMS433 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(PMS433.class);
		
	/** 查詢資料 
	 * @throws ParseException **/
	public void query(Object body, IPrimitiveMap header) throws JBranchException, ParseException {
		PMS433InputVO inputVO = (PMS433InputVO) body;
		PMS433OutputVO outputVO = new PMS433OutputVO();
		DataAccessManager dam = this.getDataAccessManager();
		
		String roleID = (String) getUserVariable(FubonSystemVariableConsts.LOGINROLE);
		if(StringUtils.isBlank(roleID)) {
			roleID = "999";
		}
		
		
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		// 主查詢
		sql.append("SELECT * FROM ( ");
		sql.append(" SELECT ");
		sql.append(" row_number() over (partition by H.CUST_ID, H.SELECTED_DATE ORDER BY H.YYYYMM) RN,  ");
		sql.append(" H.SEQ,  ");
		sql.append(" H.CUST_ID,  ");
		sql.append(" C.CUST_NAME,  ");
		sql.append(" H.AGE, ");
		sql.append(" H.BRA_NBR,  ");
		sql.append(" D.DEPT_NAME,  ");
		sql.append(" H.CALL_RESULT,  ");
		sql.append(" H.REC_SEQ,  ");
		sql.append(" H.MEMO,  ");
		sql.append(" H.CREATETIME,  ");
		sql.append(" H.LASTUPDATE,  ");
		sql.append(" H.MODIFIER,  ");
		sql.append(" M.EMP_NAME, ");
		sql.append(" 'N' AS MODIFY_FLAG, ");
		sql.append(" CASE WHEN VW.UHRM_CODE IS NULL THEN 'N' ELSE 'Y' END AS ISUHRM, ");
		sql.append(" H.SELECTED_DATE, ");
		sql.append(" H.CREATOR ");
		sql.append(" FROM TBPMS_HIGH_SOT_M H  ");
		sql.append(" LEFT JOIN TBCRM_CUST_MAST C ON H.CUST_ID = C.CUST_ID ");
		sql.append(" LEFT JOIN TBORG_DEFN D ON H.BRA_NBR = D.DEPT_ID ");
		sql.append(" LEFT JOIN TBORG_MEMBER M ON H.MODIFIER = M.EMP_ID ");
		sql.append(" LEFT JOIN VWORG_EMP_UHRM_INFO VW ON C.AO_CODE = VW.UHRM_CODE ");
		sql.append(" LEFT JOIN ( SELECT AO.EMP_ID, AO.AO_CODE, M.DEPT_ID FROM TBORG_SALES_AOCODE AO  ");
		sql.append(" INNER JOIN TBORG_MEMBER M ON M.EMP_ID = AO.EMP_ID) AO_INFO ON C.AO_CODE = AO_INFO.AO_CODE ");
		sql.append(" LEFT JOIN TBPMS_EMPLOYEE_REC_N MEM ON VW.EMP_ID = MEM.EMP_ID AND TO_DATE(H.YYYYMM,'yyyyMM') BETWEEN MEM.START_TIME AND MEM.END_TIME ");
		sql.append(" WHERE 1 = 1  ");
		//限制條件
		if(roleID.matches("A150|ABRF|A157")) { //作業主管or有權人員
			sql.append(" AND VW.UHRM_CODE IS NULL ");
		} else if (roleID.equals("R012")) { //UHRM科主管
			sql.append(" AND VW.UHRM_CODE IS NOT NULL ");
		} /*其他身分都可檢視'B024','B026','A164','B030'
		    財管內控管理科經辦、財管績效科管理科經辦、業務處主管、個金行銷部經辦
		  */
		
		
		if(StringUtils.isNotBlank(inputVO.getsCreDate())) {
			sql.append(" AND H.SELECTED_DATE = :YYYYMM ");
			condition.setObject("YYYYMM", inputVO.getsCreDate());
		}
		if(StringUtils.isNotBlank(inputVO.getCall_result())) {
			sql.append(" AND H.CALL_RESULT = :CALL_RESULT ");
			condition.setObject("CALL_RESULT", inputVO.getCall_result());
		}
		if(inputVO.isUhrmFlag()) {
			if (StringUtils.isNotBlank(inputVO.getUhrm_branch_area_id())) {
				sql.append(" AND (AO_INFO.DEPT_ID = :BRA_AREA OR MEM.E_DEPT_ID = :BRA_AREA)  ");
				condition.setObject("BRA_AREA", inputVO.getUhrm_branch_area_id());
			}	
		} else {
			if(StringUtils.isNotBlank(inputVO.getBranch_nbr())) {
				sql.append(" AND H.BRA_NBR = :BRA_NBR ");
				condition.setObject("BRA_NBR", inputVO.getBranch_nbr());
			} else if (StringUtils.isNotBlank(inputVO.getBranch_area_id())) {
				sql.append(" AND H.BRA_NBR in (:BRA_NBR) ");
				condition.setObject("BRA_NBR", getFilterBranchLise(inputVO.getBranch_list()));
			} else if (StringUtils.isNotBlank(inputVO.getRegion_center_id())) {
				sql.append(" AND H.BRA_NBR in (:BRA_NBR) ");
				condition.setObject("BRA_NBR", getFilterBranchLise(inputVO.getBranch_list()));
			} else {
				sql.append(" AND H.BRA_NBR in (:BRA_NBR) ");
				condition.setObject("BRA_NBR", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
			}
		}

		sql.append(" ) T");
		sql.append(" WHERE T.RN = 1 ");
		//排序
		sql.append(" ORDER BY T.SEQ ");
		
		condition.setQueryString(sql.toString());
		List<Map<String, Object>> resultList = dam.exeQuery(condition);
		
		if (resultList.size() > 0) {
			outputVO.setResultList(resultList); // 主查詢資訊
			sendRtnObject(outputVO);
		} else {
			throw new APException("ehl_01_common_009");
		}
	}
	
	private Object getFilterBranchLise(List<Map<String, Object>>  branch_list) {
		if (branch_list.size() == 0) {
			return null;
		}
		
		String filterBranchList[] = new String[branch_list.size()];
		int i = 0;
		for (Map map : branch_list) {
			filterBranchList[i] = (String) map.get("DATA");
			i++;
		}
		return filterBranchList;
	}

	/* === 產出Excel==== */
	public void export(Object body, IPrimitiveMap header)
			throws JBranchException {
		PMS433InputVO inputVO = (PMS433InputVO) body;
		List<Map<String, Object>> list = inputVO.getResultList();
		String fileName = "網行銀高齡交易季報" + ".csv";
		List listCSV = new ArrayList();
		
		

		String roleID = (String) getUserVariable(FubonSystemVariableConsts.LOGINROLE);
		if(StringUtils.isBlank(roleID)) {
			roleID = "999";
		}
		
		//資料
		dealrecords(listCSV,list,roleID);	
		

		// header
		String[] csvHeader = getCsvHeader(roleID);
		

		CSVUtil csv = new CSVUtil();
		csv.setHeader(csvHeader);
		csv.addRecordList(listCSV);
		String url = csv.generateCSV();
		notifyClientToDownloadFile(url, fileName);
		this.sendRtnObject(null);
	}
	
	private void dealrecords(List listCSV, List<Map<String, Object>> list, String roleID) throws JBranchException {
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> CALL_RESULT = xmlInfo.doGetVariable("PMS.HIGH_SOT_CALL_RESULT", FormatHelper.FORMAT_3); // 外撥結果
		
		if (list.size() == 0) {
			String[] records = new String[2];
			records[0] = "查無資料";
			listCSV.add(records);
		} else {
			for (Map<String, Object> map : list) {
				String[] records = null;
				if(roleID.matches("A150|ABRF|A157|R012")) {
					records = new String[12];
					int i = 0;
					records[i] = checkIsNull(map, "SEQ"); 
					records[++i] = DataFormat.getCustIdMaskForHighRisk(checkIsNull(map, "CUST_ID"));
					records[++i] = DataFormat.getNameForHighRisk(checkIsNull(map, "CUST_NAME"));
					records[++i] = "=\"" + checkIsNull(map, "AGE") +"\"";
					records[++i] = "=\"" + checkIsNull(map, "BRA_NBR") +"\""; 
					records[++i] = checkIsNull(map, "DEPT_NAME");
					records[++i] = CALL_RESULT.get(checkIsNull(map, "CALL_RESULT")); 
					records[++i] = "=\"" + checkIsNull(map, "REC_SEQ") +"\"";
					records[++i] = checkIsNull(map, "MEMO"); 
					records[++i] = checkIsNull(map, "CREATETIME"); 
					records[++i] = checkIsNull(map, "LASTUPDATE");
					records[++i] = checkIsNull(map, "MODIFIER") + "-" + checkIsNull(map, "EMP_NAME"); 
					listCSV.add(records);					
				} else {
					records = new String[13];
					int i = 0;
					records[i] = checkIsNull(map, "SEQ"); 
					records[++i] = DataFormat.getCustIdMaskForHighRisk(checkIsNull(map, "CUST_ID"));
					records[++i] = DataFormat.getNameForHighRisk(checkIsNull(map, "CUST_NAME"));
					records[++i] = "=\"" + checkIsNull(map, "AGE") +"\"";
					records[++i] = "=\"" + checkIsNull(map, "BRA_NBR") +"\""; 
					records[++i] = checkIsNull(map, "DEPT_NAME");
					records[++i] = checkIsNull(map, "ISUHRM");
					records[++i] = CALL_RESULT.get(checkIsNull(map, "CALL_RESULT")); 
					records[++i] = "=\"" + checkIsNull(map, "REC_SEQ") +"\"";
					records[++i] = checkIsNull(map, "MEMO"); 
					records[++i] = checkIsNull(map, "CREATETIME"); 
					records[++i] = checkIsNull(map, "LASTUPDATE");
					records[++i] = checkIsNull(map, "MODIFIER") + "-" + checkIsNull(map, "EMP_NAME"); 
					listCSV.add(records);
				}
			}
		}
		
	}

	private String[] getCsvHeader(String roleID) {
		int j = 0;
		String[] csvHeader = null;
		if(roleID.matches("A150|ABRF|A157|R012")) {
			csvHeader = new String[12];
			csvHeader[j] = "序號";
			csvHeader[++j] = "客戶ID";
			csvHeader[++j] = "姓名";
			csvHeader[++j] = "年齡";
			csvHeader[++j] = "歸屬行";
			csvHeader[++j] = "分行名稱";
			csvHeader[++j] = "外撥結果";
			csvHeader[++j] = "電訪錄音編號";
			csvHeader[++j] = "備註說明";
			csvHeader[++j] = "首次建立時間";
			csvHeader[++j] = "最新異動時間";
			csvHeader[++j] = "最新異動人員";
		} else {
			csvHeader = new String[13];
			csvHeader[j] = "序號";
			csvHeader[++j] = "客戶ID";
			csvHeader[++j] = "姓名";
			csvHeader[++j] = "年齡";
			csvHeader[++j] = "歸屬行";
			csvHeader[++j] = "分行名稱";
			csvHeader[++j] = "高端客戶";
			csvHeader[++j] = "外撥結果";
			csvHeader[++j] = "電訪錄音編號";
			csvHeader[++j] = "備註說明";
			csvHeader[++j] = "首次建立時間";
			csvHeader[++j] = "最新異動時間";
			csvHeader[++j] = "最新異動人員";
		}

		
		return csvHeader;
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
	
	/*
	 * 例
	 * 202403：12~2月
	 * 202406：3~5月
	 * 202409：6~8月
	 * 202412： 9~11月
	 */
	public void init(Object body, IPrimitiveMap header) throws Exception {
		PMS433OutputVO return_VO = new PMS433OutputVO();
		
		List<Map<String, Object>> dateList = new ArrayList();
		
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();

		sql.append(" select distinct YYYYMM FROM TBPMS_HIGH_SOT_M order by YYYYMM desc ");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		if(list.size() < 1) {
			throw new Exception("尚未有任一資料統計月份可供查詢。");
		}
		
		for(Map map : list) {
			String date = (String) map.get("YYYYMM");
			String yyyy = date.substring(0,4);
			String mm = date.substring(4,6);
			Map<String, Object> temp = new HashMap();
			switch(mm){
			case "02":
				temp.put("LABEL", yyyy + "03");
				temp.put("DATA", yyyy + "03");
				dateList.add(temp);
				break;
			case "05":
				temp.put("LABEL", yyyy + "06");
				temp.put("DATA", yyyy + "06");
				dateList.add(temp);
				break;
			case "07":
				temp.put("LABEL", yyyy + "09");
				temp.put("DATA", yyyy + "09");
				dateList.add(temp);
				break;
			case "11":
				temp.put("LABEL", yyyy + "12");
				temp.put("DATA", yyyy + "12");
				dateList.add(temp);
				break;			
			}
		}
		return_VO.setDateList(dateList);
		this.sendRtnObject(return_VO);
	}


	/** 取得明細資料
	 * @throws ParseException **/
	public void getDtl(Object body, IPrimitiveMap header) throws JBranchException, ParseException {
		PMS433InputVO inputVO = (PMS433InputVO) body;
		PMS433OutputVO outputVO = new PMS433OutputVO();
		DataAccessManager dam = this.getDataAccessManager();
		
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		// 主查詢
		sql = getDtlSql();
		condition.setObject("CUST_ID", inputVO.getCust_id());
		condition.setObject("SELECTED_DATE", inputVO.getSelected_date());
		
		
		condition.setQueryString(sql.toString());
		List<Map<String, Object>> resultList = dam.exeQuery(condition);
		
		if (resultList.size() > 0) {
			outputVO.setDetailList(resultList); // 主查詢資訊
			sendRtnObject(outputVO);
		} else {
			throw new APException("ehl_01_common_009");
		}
	}

	private StringBuffer getDtlSql() {
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT ROWNUM AS SEQ, D.* FROM ( ");
		sb.append(" SELECT ");
		sb.append(" D.TRADE_DATE,  ");
		sb.append(" D.TRADE_SOURCE,  ");
		sb.append(" D.PRD_TYPE, ");
		sb.append(" D.PRD_ID,  ");
		sb.append(" CASE WHEN D.PRD_TYPE = '01' THEN F.FUND_CNAME  ");
		sb.append(" WHEN D.PRD_TYPE = '02' THEN NVL(E.ETF_CNAME, S.STOCK_CNAME)  ");
		sb.append(" WHEN D.PRD_TYPE = '03' THEN N.PRD_NAME  ");
		sb.append(" ELSE '' END AS PROD_NAME,  ");
		sb.append(" D.CUR_COD,  ");
		sb.append(" D.TRADE_TYPE,  ");
		sb.append(" D.AMOUNT  ");
				
		sb.append(" FROM TBPMS_HIGH_SOT_M M  ");
		sb.append(" INNER JOIN TBPMS_HIGH_SOT_D D ON M.SEQ = D.SEQ  ");
		sb.append(" LEFT JOIN TBPRD_FUND F ON D.PRD_ID = F.PRD_ID ");
		sb.append(" LEFT JOIN TBPRD_ETF E ON D.PRD_ID = E.PRD_ID ");
		sb.append(" LEFT JOIN TBPRD_STOCK S ON D.PRD_ID = S.PRD_ID ");
		sb.append(" LEFT JOIN TBPRD_NANO N ON D.PRD_ID = N.PRD_ID ");
		sb.append(" WHERE 1 = 1  ");
		sb.append(" AND M.CUST_ID = :CUST_ID ");
		sb.append(" AND M.SELECTED_DATE = :SELECTED_DATE ");
		
		sb.append(" ORDER BY D.TRADE_DATE ");
		sb.append(" ) D ");
		return sb;
	}
	
	public void save(Object body, IPrimitiveMap header) throws JBranchException, ParseException {
		PMS433InputVO inputVO = (PMS433InputVO) body;
		PMS433OutputVO outputVO = new PMS433OutputVO();
		DataAccessManager dam = this.getDataAccessManager();
		
		List<Map> modifyList = inputVO.getModifyList();
		
		for(Map data : modifyList) {
			updateTBPMS_HIGH_SOT_M(data);
		}
		
		sendRtnObject(outputVO);
	}
	
	/** 更新客戶主檔 TBCRM_CUST_MAST中的客戶等級 VIP_DEGREE */
	private void updateTBPMS_HIGH_SOT_M(Map map) throws JBranchException {
		
		exeUpdateForMap(getUpdateTBPMS_HIGH_SOT_MSql((String)map.get("CREATOR")), getUpdateTBPMS_HIGH_SOT_MParamMap(map));
	}

	/**取得更新客戶主檔等級的Param Map 
	 * @throws JBranchException */
	private Map getUpdateTBPMS_HIGH_SOT_MParamMap(Map map) throws JBranchException {
		HashMap param = new HashMap();
		if(StringUtils.isBlank((String)map.get("CREATOR"))) {
			param.put("CREATOR", (String) getUserVariable(FubonSystemVariableConsts.LOGINID));
		}
		param.put("CALL_RESULT", (String)map.get("CALL_RESULT"));
		param.put("REC_SEQ", (String)map.get("REC_SEQ"));
		param.put("MEMO", (String)map.get("MEMO"));
		param.put("MODIFIER", (String) getUserVariable(FubonSystemVariableConsts.LOGINID));
		param.put("CUST_ID", map.get("CUST_ID"));
		param.put("SELECTED_DATE", map.get("SELECTED_DATE"));

		return param;
	}

	/**取得更新客戶主檔等級Sql*/
	private String getUpdateTBPMS_HIGH_SOT_MSql(String creator) {
		StringBuffer sb = new StringBuffer();
		sb.append(" UPDATE ");
		sb.append(" TBPMS_HIGH_SOT_M ");
		sb.append(" SET ");
		if(StringUtils.isBlank(creator)) {
			sb.append(" CREATOR = :CREATOR, ");
			sb.append(" CREATETIME = SYSDATE, ");
		}
		sb.append(" CALL_RESULT = :CALL_RESULT, ");
		sb.append(" REC_SEQ = :REC_SEQ, ");
		sb.append(" MEMO = :MEMO, ");
		sb.append(" MODIFIER = :MODIFIER, ");
		sb.append(" LASTUPDATE = SYSDATE ");
		sb.append(" WHERE ");
		sb.append(" 1 = 1 ");
		sb.append(" AND CUST_ID = :CUST_ID ");
		sb.append(" AND SELECTED_DATE = :SELECTED_DATE ");

		return sb.toString();
	}
		

}
