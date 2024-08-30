package com.systex.jbranch.app.server.fps.pms399;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Component("pms399")
@Scope("prototype")
public class PMS399 extends FubonWmsBizLogic {

	private DataAccessManager dam = null;

	/* 取得可視範圍 */
	public void getOrgInfo(Object body, IPrimitiveMap header) throws JBranchException, ParseException {

		PMS399InputVO inputVO = (PMS399InputVO) body;
		PMS399OutputVO outputVO = new PMS399OutputVO();
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

	/* 查詢資料-前端 */
	public void queryData(Object body, IPrimitiveMap header) throws Exception {
		
		PMS399OutputVO outputVO = new PMS399OutputVO();
		outputVO = this.queryData(body);

		sendRtnObject(outputVO);
	}

	/* 查詢資料-後端 */
	public PMS399OutputVO queryData(Object body) throws Exception {

		initUUID();
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> headmgrMap = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2); //總行人員

		PMS399InputVO inputVO = (PMS399InputVO) body;
		PMS399OutputVO outputVO = new PMS399OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();

		String loginRoleID = null != inputVO.getSelectRoleID() ? inputVO.getSelectRoleID() : (String) getUserVariable(FubonSystemVariableConsts.LOGINROLE);

		sql.append("select ROWNUM, t.* ");
		sql.append("from ( ");
		sql.append("  select dep.TRADE_DATE, to_char(TRADE_DATE, 'YYYY/MM/DD') as TRADE_DATE_F, ");
		sql.append("         dep.BRANCH_NBR, org.BRANCH_NAME, dep.ID, dep.NAME, dep.EMP_ID, ");
		sql.append("         dep.AMT_NTD, ");
		sql.append("         dep.SUPERVISOR_FLAG, dep.WARNING_YN, dep.FIRST_CREATIME, to_char(dep.CREATETIME, 'YYYY/MM/DD') as CREATETIME, dep.NOTE, dep.NOTE2, dep.NOTE3, dep.MODIFIER, ");
		sql.append("         case when role.PARAM_NAME IN('001', '004') then 'OP' ");
		sql.append("              when role.PARAM_NAME IN('002', '003', '005', '006', '007', '008') then 'BR' ");
		sql.append("         end as ROLE_FLAG, ");
		sql.append("         to_char(dep.LASTUPDATE, 'YYYY/MM/DD HH:MI:SS AM') as LASTUPDATE ");
		sql.append("  from TBPMS_DAILY_SALES_DEP_N dep ");
		sql.append("  left join TBPMS_ORG_REC_N org on dep.BRANCH_NBR = org.DEPT_ID and dep.TRADE_DATE between org.START_TIME and org.END_TIME ");
		sql.append("  left join TBPMS_EMPLOYEE_REC_N emp ");
		sql.append("         ON dep.ID = emp.CUST_ID ");
		sql.append("        and dep.BRANCH_NBR = emp.DEPT_ID ");
		sql.append("        and dep.TRADE_DATE between emp.START_TIME and emp.END_TIME ");
		sql.append("        and (emp.PS_FLAG = 'Y' OR emp.AO_JOB_RANK LIKE ('%F%') OR emp.ROLE_ID IN (select PARAM_CODE from TBSYSPARAMETER where PARAM_TYPE ='BTPMS401.PERSON_ROLE')) ");
		sql.append("  left join (select * from TBSYSPARAMETER where PARAM_TYPE = 'BTPMS401.PERSON_ROLE') role ON emp.role_id = role.PARAM_CODE ");
		sql.append("  where  1=1 ");

		// 資料統計日期
		if (inputVO.getsCreDate() != null) {
			sql.append("  and TRUNC(dep.CREATETIME) >= TRUNC(TO_DATE(:times, 'YYYY-MM-DD')) ");
			condition.setObject("times", new java.text.SimpleDateFormat("yyyyMMdd").format(inputVO.getsCreDate()));
		}

		if (inputVO.geteCreDate() != null) {
			sql.append("  and TRUNC(dep.CREATETIME) <= TRUNC(TO_DATE(:timee, 'YYYY-MM-DD')) ");
			condition.setObject("timee", new java.text.SimpleDateFormat("yyyyMMdd").format(inputVO.geteCreDate()));
		}

		if (StringUtils.lowerCase(inputVO.getMemLoginFlag()).indexOf("uhrm") < 0 || StringUtils.lowerCase(inputVO.getMemLoginFlag()).equals("uhrm")) {
			// 非任何uhrm相關人員 或 為031之兼任FC，需查詢畫面中組織條件

			// by Willis 20171019 此條件因為發現組織換區有異動(例如:東寧分行在正式環境10/1從西台南區換至東台南區)，跟之前組織對應會有問題，改為對應目前最新組織分行別
			// 分行
			if (StringUtils.isNotBlank(inputVO.getBranch_nbr())) {
				sql.append("  and dep.BRANCH_NBR = :BRNCH_NBRR ");
				condition.setObject("BRNCH_NBRR", inputVO.getBranch_nbr());
				// 營運區	
			} else if (StringUtils.isNotBlank(inputVO.getBranch_area_id())) {
				sql.append("  and dep.BRANCH_NBR in ( ");
				sql.append("    select BRANCH_NBR ");
				sql.append("    from VWORG_DEFN_BRH ");
				sql.append("    where DEPT_ID = :BRANCH_AREA_IDD ");
				sql.append("  ) ");
				condition.setObject("BRANCH_AREA_IDD", inputVO.getBranch_area_id());
				// 區域中心	
			} else if (StringUtils.isNotBlank(inputVO.getRegion_center_id())) {
				sql.append("  and dep.BRANCH_NBR in ( ");
				sql.append("    select BRANCH_NBR ");
				sql.append("    from VWORG_DEFN_BRH ");
				sql.append("    where DEPT_ID = :REGION_CENTER_IDD ");
				sql.append("  ) ");
				condition.setObject("REGION_CENTER_IDD", inputVO.getRegion_center_id());
			}

			// 需求變更-新稱職務欄位 by 2017/11/27-Willis
			if (StringUtils.isNotBlank(inputVO.getPerson_role())) {
				sql.append("  and role.PARAM_NAME = :PERSON_ROLE ");
				condition.setObject("PERSON_ROLE", inputVO.getPerson_role());
			}

			if (!headmgrMap.containsKey(loginRoleID) && !StringUtils.lowerCase(inputVO.getMemLoginFlag()).equals("uhrm")) {
				// 非總人行員 或 非 為031之兼任FC，僅可視轄下
				sql.append("  AND dep.RM_FLAG = 'B' ");
			}
		} else {
			sql.append("  AND TRIM(dep.ID) IS NOT NULL ");
			sql.append("  AND dep.RM_FLAG = 'U' ");
		}

		//由工作首頁CRM181過來，只須查詢主管尚未確認資料
		if (StringUtils.equals("Y", inputVO.getNeedConfirmYN())) {
			//			sql.append("  and NVL(dep.SUPERVISOR_FLAG, 'N') <> 'Y' ");
			// modify by ocean : 20240524 偲偲信件
			sql.append("AND dep.FIRST_CREATIME IS NULL ");
		}

		sql.append("  order by dep.TRADE_DATE, dep.BRANCH_NBR, dep.ID ");
		sql.append(") t ");

		condition.setQueryString(sql.toString());

		ResultIF list = dam.executePaging(condition, inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());

		outputVO.setTotalList(dam.exeQuery(condition));
		outputVO.setTotalPage(list.getTotalPage());
		outputVO.setResultList(list);
		outputVO.setTotalRecord(list.getTotalRecord());
		outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());

		return outputVO;
	}

	/* 查詢資料 */
	public void queryDetail(Object body, IPrimitiveMap header) throws JBranchException, ParseException {

		PMS399InputdetailVO inputVO = (PMS399InputdetailVO) body;
		PMS399OutputVO outputVO = new PMS399OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();

		// 主查詢 sql 修正 20170120
		sql.append("select ROWNUM, t.* ");
		sql.append("from ( ");
		sql.append(" select dep.TRADE_DATE, ");
		sql.append("        to_char(TRADE_DATE, 'YYYY/MM/DD') as TRADE_DATE_F, ");
		sql.append("        dep.BRANCH_NBR, ");
		sql.append("        dep.ACC_NBR, ");
		sql.append("        dep.ID, ");
		sql.append("        dep.NAME, ");
		sql.append("        dep.EMP_ID,");
		sql.append("        dep.AMT_NTD, ");
		sql.append("        dep.CRCY_TYPE, ");
		sql.append("        dep.AMT_ORGD ");
		sql.append(" from TBPMS_DAILY_SALES_DEP_DTL dep ");
		sql.append(" where 1 = 1 ");

		// 資料統計日期
		if (inputVO.getsCreDate() != null) {
			sql.append("  and TRUNC(dep.TRADE_DATE) >= TRUNC(TO_DATE(:times, 'YYYY-MM-DD')) ");
			condition.setObject("times", new java.text.SimpleDateFormat("yyyyMMdd").format(inputVO.getsCreDate()));
		}

		if (inputVO.geteCreDate() != null) {
			sql.append("  and TRUNC(dep.TRADE_DATE) <= TRUNC(TO_DATE(:timee, 'YYYY-MM-DD')) ");
			condition.setObject("timee", new java.text.SimpleDateFormat("yyyyMMdd").format(inputVO.geteCreDate()));
		}

		if (StringUtils.isNotBlank(inputVO.getCust_id())) {
			sql.append("  and dep.ID = :PERSON_ID ");
			condition.setObject("PERSON_ID", inputVO.getCust_id());
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

	/* ==== 【儲存】更新資料 ========修正by KevinHsu */
	public void save(Object body, IPrimitiveMap header) throws JBranchException {

		PMS399InputVO inputVO = (PMS399InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();

		for (Map<String, Object> map : inputVO.getList()) { // 資料修改後
			condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb = new StringBuffer();

			sb.append("UPDATE TBPMS_DAILY_SALES_DEP_N ");
			sb.append("SET SUPERVISOR_FLAG = 'Y', ");
			sb.append("    WARNING_YN = :warning_yn, ");
			sb.append("    NOTE = :note, ");
			sb.append("    NOTE2 = :note2, ");
			sb.append("    NOTE3 = :note3, ");
			sb.append("    Modifier = :modifier, ");
			sb.append("    Lastupdate = sysdate ");

			if (map.get("FIRST_CREATIME") == null) {
				sb.append(",FIRST_CREATIME = sysdate ");
			}

			sb.append("WHERE BRANCH_NBR = :branchNBr ");
			sb.append("AND ID = :id ");
			sb.append("AND TRADE_DATE = :tradeDate");

			condition.setObject("warning_yn", ObjectUtils.toString(map.get("WARNING_YN")));
			condition.setObject("note", ObjectUtils.toString(map.get("NOTE")));
			condition.setObject("note2", ObjectUtils.toString(map.get("NOTE2")));
			condition.setObject("note3", ObjectUtils.toString(map.get("NOTE3")));
			condition.setObject("modifier", (String) getUserVariable(FubonSystemVariableConsts.LOGINID));
			condition.setObject("branchNBr", ObjectUtils.toString(map.get("BRANCH_NBR")));
			condition.setObject("id", ObjectUtils.toString(map.get("ID")));
			condition.setObject("tradeDate", Timestamp.valueOf(ObjectUtils.toString(map.get("TRADE_DATE"))));

			condition.setQueryString(sb.toString());

			dam.exeUpdate(condition);
		}
		sendRtnObject(null);
	}

	/* 產出CSV */
	public void export(Object body, IPrimitiveMap header) throws JBranchException {

		PMS399OutputVO outputVO = (PMS399OutputVO) body;

		String[] csvHeader = { "序號", "資料日期", "交易日期", "所屬分行代碼", "所屬分行", "行員身分證字號", "行員姓名", "員編", "交易金額(台幣)", "查證方式", "資金來源/帳戶關係", "具體原因/用途", "初判異常轉法遵部調查", "首次建立時間", "最新異動人員", "最新異動日期" };

		String[] csvMain = { "ROWNUM", "CREATETIME", "TRADE_DATE_F", "BRANCH_NBR", "BRANCH_NAME", "ID", "NAME", "EMP_ID", "AMT_NTD", "NOTE2", "NOTE3", "NOTE", "WARNING_YN", "FIRST_CREATIME", "MODIFIER", "LASTUPDATE" };

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
					case "AMT_NTD":
						records[i] = currencyFormat(map, csvMain[i]);
						break;
					case "ROWNUM":
						records[i] = ((int) Double.parseDouble(checkIsNull(map, csvMain[i]).toString())) + ""; // 序號 - 去小數點
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

		notifyClientToDownloadFile(csv.generateCSV(), "分行人員當日存款異動明細表_" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + "-" + getUserVariable(FubonSystemVariableConsts.LOGINID) + ".csv");
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