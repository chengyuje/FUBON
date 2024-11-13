package com.systex.jbranch.app.server.fps.pms400;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBPMS_DAILY_SALES_DEPPK;
import com.systex.jbranch.app.common.fps.table.TBPMS_DAILY_SALES_DEPVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Component("pms400")
@Scope("request")
public class PMS400 extends FubonWmsBizLogic {

	private DataAccessManager dam = null;

	/* 取得可視範圍 */
	public void getOrgInfo(Object body, IPrimitiveMap header) throws JBranchException, ParseException {
		
		PMS400InputVO inputVO = (PMS400InputVO) body;
		PMS400OutputVO outputVO = new PMS400OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		sql.append("SELECT V_REGION_CENTER_ID, V_REGION_CENTER_NAME, ");
		sql.append("       V_BRANCH_AREA_ID, V_BRANCH_AREA_NAME, ");
		sql.append("       V_BRANCH_NBR, V_BRANCH_NAME, ");
		sql.append("       V_AO_CODE, V_EMP_ID, V_EMP_NAME ");
		sql.append("FROM table(FC_GET_VRR(:purview_type, null, :e_dt, :emp_id, null, null, null, null)) ");

		condition.setQueryString(sql.toString());
		condition.setObject("purview_type", "OTHER"); // 非業績報表

		if (inputVO.geteCreDate() != null) {
			condition.setObject("e_dt", inputVO.geteCreDate());
		} else {
			condition.setObject("e_dt", new Timestamp(System.currentTimeMillis()));
		}
		
		condition.setObject("emp_id", (String) getUserVariable(FubonSystemVariableConsts.LOGINID));

		outputVO.setOrgList(dam.exeQuery(condition));
		
		sendRtnObject(outputVO);
	}

	/* 查詢資料 */
	public void queryData(Object body, IPrimitiveMap header) throws JBranchException, ParseException {
		
		PMS400InputVO inputVO = (PMS400InputVO) body;
		PMS400OutputVO outputVO = new PMS400OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		// 主查詢 sql 修正 20170120
		sql.append("select ROWNUM, t.* ");
		sql.append("from ( ");
		sql.append("  select dep.TRADE_DATE, ");
		sql.append("         to_char(TRADE_DATE, 'YYYY/MM/DD') as TRADE_DATE_F, ");
		sql.append("         dep.BRANCH_NBR, ");
		sql.append("         org.BRANCH_NAME, ");
		sql.append("         dep.ACC_NBR, ");
		sql.append("         dep.ID, ");
		sql.append("         dep.NAME, ");
		sql.append("         dep.EMP_ID, ");
		sql.append("         dep.AO_CODE, ");
		sql.append("         dep.CRCY_TYPE, ");
		sql.append("         dep.AMT_ORGD, ");
		sql.append("         dep.AMT_NTD, ");
		sql.append("         dep.SUPERVISOR_FLAG, ");
		sql.append("         dep.NOTE, ");
		sql.append("         dep.MODIFIER, ");
		sql.append("         case when role.PARAM_NAME IN('001', '004') then 'OP' ");
		sql.append("              when role.PARAM_NAME IN('002', '003', '005', '006', '007', '008') then 'BR' ");
		sql.append("         end as ROLE_FLAG, ");
		sql.append("         to_char(dep.LASTUPDATE, 'YYYY/MM/DD HH:MI:SS AM') as LASTUPDATE ");
		sql.append("  from TBPMS_DAILY_SALES_DEP dep ");
		sql.append("  left join TBPMS_ORG_REC_N org on dep.BRANCH_NBR = org.DEPT_ID and dep.TRADE_DATE between org.START_TIME and org.END_TIME ");
		sql.append("  left join TBPMS_EMPLOYEE_REC_N emp ON dep.ID = emp.CUST_ID and dep.BRANCH_NBR = emp.DEPT_ID and dep.TRADE_DATE between emp.START_TIME and emp.END_TIME and (emp.PS_FLAG = 'Y' OR emp.AO_JOB_RANK LIKE ('%F%') OR emp.ROLE_ID IN (select PARAM_CODE from TBSYSPARAMETER where PARAM_TYPE = 'BTPMS401.PERSON_ROLE')) ");
		sql.append("  left join (select * from TBSYSPARAMETER where PARAM_TYPE = 'BTPMS401.PERSON_ROLE') role ON emp.role_id =role.PARAM_CODE ");
		sql.append("  where 1 = 1 ");

		// 資料統計日期
		if (inputVO.getsCreDate() != null) {
			sql.append("  and TRUNC(dep.TRADE_DATE) >= TRUNC(TO_DATE(:times, 'YYYY-MM-DD')) ");
			condition.setObject("times", new java.text.SimpleDateFormat("yyyyMMdd").format(inputVO.getsCreDate()));
		}
		
		if (inputVO.geteCreDate() != null) {
			sql.append("  and TRUNC(dep.TRADE_DATE) <= TRUNC(TO_DATE(:timee, 'YYYY-MM-DD')) ");
			condition.setObject("timee", new java.text.SimpleDateFormat("yyyyMMdd").format(inputVO.geteCreDate()));
		}

		// by Willis 20171019 此條件因為發現組織換區有異動(例如:東寧分行在正式環境10/1從西台南區換至東台南區)，跟之前組織對應會有問題，改為對應目前最新組織分行別
		
		if (StringUtils.isNotBlank(inputVO.getBranch_nbr())) {				// 分行
			sql.append("  and dep.BRANCH_NBR = :BRNCH_NBRR ");
			
			condition.setObject("BRNCH_NBRR", inputVO.getBranch_nbr());
		} else if (StringUtils.isNotBlank(inputVO.getBranch_area_id())) {	// 營運區
			sql.append("  and dep.BRANCH_NBR in ( select BRANCH_NBR from VWORG_DEFN_BRH where DEPT_ID = :BRANCH_AREA_IDD ) ");
			
			condition.setObject("BRANCH_AREA_IDD", inputVO.getBranch_area_id());
		} else if (StringUtils.isNotBlank(inputVO.getRegion_center_id())) {	// 區域中心	
			sql.append("  and dep.BRANCH_NBR in ( select BRANCH_NBR from VWORG_DEFN_BRH where DEPT_ID = :REGION_CENTER_IDD ) ");
			
			condition.setObject("REGION_CENTER_IDD", inputVO.getRegion_center_id());
		}

		// 需求變更-新稱職務欄位 by 2017/11/27-Willis
		if (StringUtils.isNotBlank(inputVO.getPerson_role())) {
			sql.append("  and role.PARAM_NAME = :PERSON_ROLE ");
			
			condition.setObject("PERSON_ROLE", inputVO.getPerson_role());
		}

		sql.append("  order by dep.TRADE_DATE, dep.BRANCH_NBR, dep.ID");
		sql.append(") t ");
		
		condition.setQueryString(sql.toString());
		
		ResultIF list = dam.executePaging(condition, inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());

		outputVO.setTotalList(dam.exeQuery(condition));
		outputVO.setTotalPage(list.getTotalPage());
		outputVO.setResultList(list);
		outputVO.setTotalRecord(list.getTotalRecord());
		outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());
		
		sendRtnObject(outputVO);
	}

	/* 儲存 */
	public void save(Object body, IPrimitiveMap header) throws JBranchException {

		PMS400InputVO inputVO = (PMS400InputVO) body;
		dam = this.getDataAccessManager();
		
		for (Map<String, Object> map : inputVO.getList()) { // 資料修改後
			if (map.get("NOTE") == null)
				map.put("NOTE", "");
			
			if (map.get("SUPERVISOR_FLAG") == null) // 2/14外加
				map.put("SUPERVISOR_FLAG", "");
			
			for (Map<String, Object> map2 : inputVO.getList2()) { // 資料修改前
				if (map2.get("NOTE") == null)
					map2.put("NOTE", "");
				
				if (map2.get("SUPERVISOR_FLAG") == null)
					map2.put("SUPERVISOR_FLAG", "");
				
				if (map.get("TRADE_DATE").equals(map2.get("TRADE_DATE")) && map.get("ID").equals(map2.get("ID")) && map.get("CRCY_TYPE").equals(map2.get("CRCY_TYPE")) && map.get("BRANCH_NBR").equals(map2.get("BRANCH_NBR")) && map.get("ACC_NBR").equals(map2.get("ACC_NBR")) && (!map.get("SUPERVISOR_FLAG").equals(map2.get("SUPERVISOR_FLAG")) || !map.get("NOTE").equals(map2.get("NOTE")))) {
					TBPMS_DAILY_SALES_DEPPK pk = new TBPMS_DAILY_SALES_DEPPK();
					pk.setACC_NBR(map.get("ACC_NBR") + "");
					pk.setBRANCH_NBR(map.get("BRANCH_NBR") + "");
					pk.setCRCY_TYPE(map.get("CRCY_TYPE") + "");
					pk.setID(map.get("ID") + "");
					pk.setTRADE_DATE(Timestamp.valueOf(map.get("TRADE_DATE") + ""));
					
					TBPMS_DAILY_SALES_DEPVO paramVO = (TBPMS_DAILY_SALES_DEPVO) dam.findByPKey(TBPMS_DAILY_SALES_DEPVO.TABLE_UID, pk);
					
					if (map.get("SUPERVISOR_FLAG") != null)
						paramVO.setSUPERVISOR_FLAG(map.get("SUPERVISOR_FLAG") + "");
					
					paramVO.setNOTE(map.get("NOTE") + "");
					paramVO.setModifier("test");
					paramVO.setLastupdate(new Timestamp(System.currentTimeMillis()));
					
					dam.update(paramVO);
				}
			}
		}
		
		sendRtnObject(null);
	}

	/* 產出CSV */
	public void export(Object body, IPrimitiveMap header) throws JBranchException {
		
		PMS400OutputVO outputVO = (PMS400OutputVO) body;

		String[] csvHeader = { "序號", "交易日期", "所屬分行代碼", "所屬分行", "帳號", "行員身分證字號", "行員姓名", "理專員編", "AO Code", "幣別", "原幣金額", "交易金額(台幣)", "主管確認", "主管備註欄", "最新異動人員", "最新異動日期"};
		String[] csvMain = { "ROWNUM", "TRADE_DATE_F", "BRANCH_NBR", "BRANCH_NAME", "ACC_NBR", "ID", "NAME", "EMP_ID", "AO_CODE", "CRCY_TYPE", "AMT_ORGD", "AMT_NTD", "SUPERVISOR_FLAG", "NOTE", "MODIFIER", "LASTUPDATE" };
		
		List<Object[]> csvData = new ArrayList<Object[]>();
		
		if ((outputVO.getTotalList()).size() == 0) {
			String[] records = new String[csvHeader.length];
			records[0] = "查無資料";
			csvData.add(records);
		} else {
			for (Map<String, Object> map : outputVO.getTotalList()) {
				String[] records = new String[csvHeader.length];
				for (int i = 0; i < csvHeader.length; i++) {
					switch (csvMain[i]) {
						case "AMT_ORGD":
						case "AMT_NTD":
							records[i] = currencyFormat(map, csvMain[i]);
							break;
						case "ROWNUM":
							records[i] = ((int) Double.parseDouble(checkIsNull(map, csvMain[i]).toString())) + ""; // 序號 - 去小數點
							break;
						case "ACC_NBR":
							records[i] = "=\"" + checkIsNull(map, csvMain[i]) + "\"";
							break;
						default:
							records[i] = checkIsNull(map, csvMain[i]);
							break;
					}
				}
				csvData.add(records);
			}
		}

		CSVUtil csv = new CSVUtil();
		csv.setHeader(csvHeader);
		csv.addRecordList(csvData);
		
		notifyClientToDownloadFile(csv.generateCSV(), "分行人員存款異動日報_" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + "-" + getUserVariable(FubonSystemVariableConsts.LOGINID) + ".csv");

		this.sendRtnObject(null);
	}

	/* 檢查Map取出欄位是否為Null */
	private String checkIsNull(Map map, String key) {
		if (StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key) != null) {
			return String.valueOf(map.get(key));
		} else {
			return "";
		}
	}

	/* 處理貨幣格式 */
	private String currencyFormat(Map map, String key) {
		if (StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key) != null) {
			DecimalFormat df = new DecimalFormat("#,##0.00");
			return df.format(map.get(key));
		} else
			return "0.00";
	}
}