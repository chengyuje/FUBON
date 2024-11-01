package com.systex.jbranch.app.server.fps.pms424;

import static org.apache.commons.collections.CollectionUtils.isEmpty;

import java.text.DecimalFormat;
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
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Component("pms424")
@Scope("prototype")
public class PMS424 extends FubonWmsBizLogic {
	
	private DataAccessManager dam = null;

	public void query (Object body, IPrimitiveMap header) throws Exception {
		
		PMS424OutputVO outputVO = new PMS424OutputVO();
		outputVO = this.query(body);

		sendRtnObject(outputVO);
	}
	
	public PMS424OutputVO query (Object body) throws JBranchException {
		
		initUUID();
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> headmgrMap = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2); // 總行人員
		Map<String, String> armgrMap   = xmlInfo.doGetVariable("FUBONSYS.ARMGR_ROLE", FormatHelper.FORMAT_2);	//處長

		PMS424InputVO inputVO = (PMS424InputVO) body;
		PMS424OutputVO outputVO = new PMS424OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();

		String loginRoleID = null != inputVO.getSelectRoleID() ? inputVO.getSelectRoleID() : (String) getUserVariable(FubonSystemVariableConsts.LOGINROLE);

		sb.append("SELECT ROWNUM, A.* ");
		sb.append("FROM ( ");
		sb.append("  SELECT CASE WHEN RPT.RM_FLAG = 'U' THEN 'Y' ELSE 'N' END AS RM_FLAG, ");
		sb.append("         RPT.TXN_DATE, ");
		sb.append("         CASE WHEN TRUNC(RPT.CREATETIME) <= TRUNC(TO_DATE('20230630', 'YYYYMMDD')) THEN 'N' ELSE 'Y' END AS RECORD_YN, ");
		sb.append("         RPT.EMP_ID, ");
		sb.append("         MBR.EMP_NAME, ");
		sb.append("         ORG.REGION_CENTER_ID, ");
		sb.append("         ORG.REGION_CENTER_NAME, ");
		sb.append("         ORG.BRANCH_AREA_ID, ");
		sb.append("         ORG.BRANCH_AREA_NAME, ");
		sb.append("         RPT.BRANCH_NBR || '-' || ORG.BRANCH_NAME AS BRANCH_NBR, ");
		sb.append("         RPT.ACCT_OUT_BANK, ");
		sb.append("         RPT.ACCT_OUT, ");
		sb.append("         RPT.ACCT_OUT_ID, ");
		sb.append("         O.CUST_NAME AS ACCT_OUT_NAME, ");
		sb.append("         RPT.ACCT_IN_BANK, ");
		sb.append("         RPT.ACCT_IN, ");
		sb.append("         RPT.ACCT_IN_ID, ");
		sb.append("         RPT.TXNSEQ, ");
		sb.append("         I.CUST_NAME AS ACCT_IN_NAME, ");
		sb.append("         RPT.TXN_AMT, ");
		sb.append("         RPT.NOTE_TYPE, ");
		sb.append("         RPT.NOTE1, ");
		sb.append("         RPT.NOTE2, ");
		sb.append("         RPT.NOTE3, ");
		sb.append("         RPT.RECORD_SEQ, ");
		sb.append("         RPT.WARNING_YN, ");
		sb.append("         RPT.FIRST_CREATIME, ");
		sb.append("         RPT.MODIFIER, ");
		sb.append("         RPT.LASTUPDATE, ");
		sb.append("         TO_CHAR(RPT.TXN_DATE, 'YYYY/MM/DD') AS TXN_DATE_S, ");
		sb.append("         TO_CHAR(RPT.SNAP_DATE, 'YYYY/MM/DD') AS SNAP_DATE_S, ");
		sb.append("         TO_CHAR(RPT.CREATETIME, 'YYYY/MM/DD') AS CREATETIME_S, ");
		sb.append("         PAR.PARAM_NAME AS SOURCE_OF_DEMAND_N ");
		sb.append("  FROM TBPMS_INDIRECT_ACCT_MONITOR RPT ");
		sb.append("  LEFT JOIN VWORG_DEFN_INFO ORG ON RPT.BRANCH_NBR = ORG.BRANCH_NBR ");
		sb.append("  LEFT JOIN TBCRM_CUST_MAST O ON RPT.ACCT_OUT_ID = O.CUST_ID ");
		sb.append("  LEFT JOIN TBCRM_CUST_MAST I ON RPT.ACCT_IN_ID = I.CUST_ID ");
		sb.append("  LEFT JOIN TBORG_MEMBER MBR ON RPT.EMP_ID = MBR.EMP_ID ");
		sb.append("  LEFT JOIN TBSYSPARAMETER PAR ON PAR.PARAM_TYPE = 'PMS.PMS424_SOURCE_OF_DEMAND' AND RPT.SOURCE_OF_DEMAND = PAR.PARAM_CODE ");
		sb.append("  LEFT JOIN TBPMS_EMPLOYEE_REC_N MEM ON RPT.EMP_ID = MEM.EMP_ID AND RPT.BRANCH_NBR = MEM.DEPT_ID AND RPT.TXN_DATE BETWEEN MEM.START_TIME AND MEM.END_TIME ");

		sb.append("  WHERE 1 = 1 ");
					
		if (null != inputVO.getSourceOfDemand()) {
			sb.append("  AND RPT.SOURCE_OF_DEMAND = :sourceOfDemand ");
			queryCondition.setObject("sourceOfDemand", inputVO.getSourceOfDemand());
		}
		
		if (StringUtils.lowerCase(inputVO.getMemLoginFlag()).indexOf("uhrm") < 0) {
			if (StringUtils.isNotBlank(inputVO.getBranch_nbr())) {
				sb.append("  AND RPT.BRANCH_NBR = :branchNbr ");
				queryCondition.setObject("branchNbr", inputVO.getBranch_nbr());
			} else if (StringUtils.isNotBlank(inputVO.getBranch_area_id())) {
				sb.append("  AND EXISTS (SELECT 1 FROM VWORG_DEFN_BRH BRHT WHERE DEPT_ID = :branchAreaID AND RPT.BRANCH_NBR = BRHT.BRANCH_NBR ) ");
				queryCondition.setObject("branchAreaID", inputVO.getBranch_area_id());				
			} else if (StringUtils.isNotBlank(inputVO.getRegion_center_id())) {
				sb.append("  AND EXISTS (SELECT 1 FROM VWORG_DEFN_BRH BRHT WHERE BRHT.DEPT_ID = :regionCenterID AND RPT.BRANCH_NBR = BRHT.BRANCH_NBR) ");
				queryCondition.setObject("regionCenterID", inputVO.getRegion_center_id());
			}
			
			if (!headmgrMap.containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE)) || 
				!armgrMap.containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
				sb.append("  AND RPT.RM_FLAG = 'B' ");
			}
		} else {
			if (StringUtils.isNotBlank(inputVO.getUhrmOP())) {
				sb.append("  AND (");
				sb.append("       EXISTS ( SELECT 1 FROM TBORG_MEMBER MT WHERE RPT.EMP_ID = MT.EMP_ID AND MT.DEPT_ID = :uhrmOP ) ");
				sb.append("    OR MEM.E_DEPT_ID = :uhrmOP ");
				sb.append("  )");
				queryCondition.setObject("uhrmOP", inputVO.getUhrmOP());
			}
			
			sb.append("  AND RPT.RM_FLAG = 'U' ");
		}
		
		if (StringUtils.isNotBlank(inputVO.getEmp_id())) {
			sb.append("  AND RPT.EMP_ID = :emp_id ");
			queryCondition.setObject("emp_id", inputVO.getEmp_id());
		}
		
		if (inputVO.getsDate() != null) {
			sb.append("  AND TRUNC(RPT.CREATETIME) >= TRUNC(:start) ");
			queryCondition.setObject("start", inputVO.getsDate());
		}
		
		if (inputVO.geteDate() != null) {
			sb.append("  AND TRUNC(RPT.CREATETIME) <= TRUNC(:end) ");
			queryCondition.setObject("end", inputVO.geteDate());
		}
		
		//由工作首頁 CRM181 過來，只須查詢主管尚未確認資料
        if(StringUtils.equals("Y", inputVO.getFrom181())){
        	sb.append("  AND NVL(RPT.SUPERVISOR_FLAG, 'N') <> 'Y' ");
        }
        
		if (StringUtils.isNotEmpty(inputVO.getNoteStatus())) {
			switch (inputVO.getNoteStatus()) {
				case "01":
					sb.append("  AND RPT.FIRST_CREATIME IS NOT NULL ");
					break;
				case "02":
					sb.append("  AND RPT.FIRST_CREATIME IS NULL ");
					break;
			}
		}
        
        sb.append("  ORDER BY RPT.BRANCH_NBR, RPT.ACCT_IN_ID ");
        sb.append(") A ");
        
		queryCondition.setQueryString(sb.toString());

		outputVO.setResultList(dam.exeQuery(queryCondition));
		
		return outputVO;
	}
	
	public void save (Object body, IPrimitiveMap header) throws JBranchException {
		
		PMS424InputVO inputVO = (PMS424InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		for (Map<String, Object> map : inputVO.getList()) { // 資料修改後
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb = new StringBuffer();
			
			sb.append("UPDATE TBPMS_INDIRECT_ACCT_MONITOR ");
			sb.append("SET ");
			sb.append("    NOTE_TYPE = :noteType, ");
			sb.append("    NOTE1 = :note1, ");
			sb.append("    NOTE2 = :note2, ");
			sb.append("    NOTE3 = :note3, ");
			sb.append("    RECORD_SEQ = :recordSEQ, ");
			sb.append("    WARNING_YN = :warning_yn, ");
			sb.append("    SUPERVISOR_FLAG = 'Y', ");
			sb.append("    MODIFIER = :modifier, ");
			sb.append("    LASTUPDATE = SYSDATE ");
			
			if (map.get("FIRST_CREATIME") == null) {
				sb.append(", FIRST_CREATIME = sysdate ");
			}
			
			sb.append("WHERE TO_CHAR(TXN_DATE, 'YYYY/MM/DD') = :txn_date_s ");
			sb.append("AND TXNSEQ = :txnseq ");

			// KEY
			queryCondition.setObject("txn_date_s", map.get("TXN_DATE_S") == null ? "" : map.get("TXN_DATE_S").toString());
			queryCondition.setObject("txnseq", map.get("TXNSEQ") == null ? "" : map.get("TXNSEQ").toString());
			
			// CONTECT
			queryCondition.setObject("noteType", map.get("NOTE_TYPE"));
			queryCondition.setObject("note1", map.get("NOTE1") == null ? "" : map.get("NOTE1").toString());
			queryCondition.setObject("note2", map.get("NOTE2") == null ? "" : map.get("NOTE2").toString());
			queryCondition.setObject("note3", map.get("NOTE3") == null ? "" : map.get("NOTE3").toString());
			queryCondition.setObject("recordSEQ", map.get("RECORD_SEQ"));
			queryCondition.setObject("warning_yn", map.get("WARNING_YN") == null ? "" : map.get("WARNING_YN").toString());
			queryCondition.setObject("modifier", DataManager.getWorkStation(uuid).getUser().getCurrentUserId());
			
			queryCondition.setQueryString(sb.toString());
			
			dam.exeUpdate(queryCondition);
		}
		
		sendRtnObject(null);
	}
	
	public void export (Object body, IPrimitiveMap header) throws JBranchException {
		
		XmlInfo xmlInfo = new XmlInfo();

		PMS424InputVO inputVO = (PMS424InputVO) body;
		List<Map<String, Object>> list = inputVO.getList();

		String fileName = "關聯戶交易報表_" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".csv";
		List listCSV = new ArrayList();
		
		String[] csvHeader = {  "序號", "私銀註記", "資料日期", "產出來源",
								"交易日期", "理專歸屬行", "理專姓名",
								"轉出姓名", "轉出身分證字號", "轉出帳號",
								"轉入姓名", "轉入身分證字號", "轉入帳號",
								"交易金額",
								"查證方式", "電訪錄音編號", "資金來源或帳戶關係", "具體原因或用途", "初判異常轉法遵部調查", "首次建立時間", 
								"最後異動人員", "最後異動日期"};
		String[] csvMain   = {	"ROWNUM", "RM_FLAG", "CREATETIME_S", "SOURCE_OF_DEMAND_N",
								"TXN_DATE_S", "BRANCH_NBR", "EMP_NAME",
								"ACCT_OUT_NAME", "ACCT_OUT_ID", "ACCT_OUT",
								"ACCT_IN_NAME", "ACCT_IN_ID", "ACCT_IN",
								"TXN_AMT",
								"NOTE1", "RECORD_SEQ", "NOTE2", "NOTE3", "WARNING_YN", "FIRST_CREATIME",
								"MODIFIER", "LASTUPDATE"};
		
		if (isEmpty(list)) {
			listCSV.add(new String[] { "查無資料" });
		} else {
			for (Map<String, Object> map : list) {
				String[] records = new String[csvHeader.length];
				
				for (int i = 0; i < csvHeader.length; i++) {
					switch (csvMain[i]) {
					case "ROWNUM" :
						records[i] = ((int) Double.parseDouble(checkIsNull(map, csvMain[i]).toString())) + ""; 	// 序號 - 去小數點;
						break;
					case "ACCT_OUT":
					case "ACCT_IN":
						records[i] = "=\"" + checkIsNull(map, csvMain[i]) + "\"";
						break;
					case "TXN_AMT":
						records[i] = currencyFormat(map, csvMain[i]);
						break;
					case "NOTE1":
						String note = (String) xmlInfo.getVariable("PMS.CHECK_TYPE", (String) map.get("NOTE_TYPE"), "F3");
						
						if (null != map.get("NOTE_TYPE") && StringUtils.equals("O", (String) map.get("NOTE_TYPE"))) {
							note = note + "：" + StringUtils.defaultString((String) map.get(csvMain[i]));
						}
						
						records[i] = (StringUtils.isNotEmpty(note) ? note : "");
						break;
					case "RECORD_SEQ":
						records[i] = "=\"" + checkIsNull(map, csvMain[i]) + "\"";
						break;
					default:
						records[i] = checkIsNull(map, csvMain[i]); 
						break;
					}
				}
				
				listCSV.add(records);
			}
		}
		
		CSVUtil csv = new CSVUtil();
		csv.setHeader(csvHeader);
		csv.addRecordList(listCSV);
		
		notifyClientToDownloadFile(csv.generateCSV(), fileName);

		this.sendRtnObject(null);
	}

	public void getExample (Object body, IPrimitiveMap header) throws Exception {
		
		notifyClientToDownloadFile("doc//PMS//PMS424_getExample.xlsx", "2020.1_11關聯戶報表.xlsx");
	    this.sendRtnObject(null);
	}
	
	private String checkIsNull(Map map, String key) {
		if (StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key) != null) {
			if ("ACCT_OUT_ID".equals(key) || "ACCT_IN_ID".equals(key)) {
				return DataFormat.getCustIdMaskForHighRisk(String.valueOf(map.get(key)));
			} else {
				return String.valueOf(map.get(key));
			}
		} else {
			return "";
		}
	}
	
	// 處理貨幣格式
	private String currencyFormat (Map map, String key) {
		
		if (StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key) != null) {
			DecimalFormat df = new DecimalFormat("#,##0.00");
			return df.format(map.get(key));
		} else
			return "0.00";
	}
}