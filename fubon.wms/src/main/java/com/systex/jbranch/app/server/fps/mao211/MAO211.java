package com.systex.jbranch.app.server.fps.mao211;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.birt.report.model.api.util.StringUtil;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBMAO_DEV_APL_PLISTVO;
import com.systex.jbranch.app.server.fps.sot701.CustKYCDataVO;
import com.systex.jbranch.app.server.fps.sot701.FP032675DataVO;
import com.systex.jbranch.app.server.fps.sot701.SOT701;
import com.systex.jbranch.app.server.fps.sot701.SOT701InputVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.serialnumber.SerialNumberUtil;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Component("mao211")
@Scope("request")
public class MAO211 extends FubonWmsBizLogic {

	private DataAccessManager dam = null;

	public void inquire(Object body, IPrimitiveMap header) throws JBranchException {

		MAO211InputVO inputVO = (MAO211InputVO) body;
		MAO211OutputVO outputVO = new MAO211OutputVO();
		dam = this.getDataAccessManager();

		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();

		sql.append("WITH APL_PLIST AS ( ");
		sql.append("  SELECT DEV_NBR,  ");
		sql.append("         TO_CHAR(USE_DATE, 'yyyyMMdd')  || USE_PERIOD_S_TIME || '00' AS START_TIME,  ");
		sql.append("         CASE WHEN USE_PERIOD_E_TIME < USE_PERIOD_S_TIME THEN TO_CHAR((USE_DATE) + 1, 'yyyyMMdd')  || USE_PERIOD_E_TIME || '00' ");
		sql.append("         ELSE TO_CHAR(USE_DATE, 'yyyyMMdd')  || USE_PERIOD_E_TIME || '00' END AS END_TIME,  ");
		sql.append("         DEV_STATUS ");
		sql.append("  FROM TBMAO_DEV_APL_PLIST ");
		sql.append(") ");
		sql.append(", USE_STATUS AS ( ");
		sql.append("  SELECT BASE.SEQ, ");
		sql.append("         BASE.DEV_NBR, ");
		sql.append("         BASE.DC_NBR, ");
		sql.append("         BASE.OP_NBR, ");
		sql.append("         BASE.BRA_NBR, ");
		sql.append("         BASE.DEV_TAKE_EMP, ");
		sql.append("         BASE.DEV_TAKE_EMP_NAME, ");
		sql.append("         BASE.DEV_SITE_TYPE, ");
		sql.append("         BASE.O_TYPE, ");
		sql.append("         CASE WHEN (SELECT COUNT(1)  ");
		sql.append("                    FROM APL_PLIST  ");
		sql.append("                    WHERE BASE.DEV_NBR = APL_PLIST.DEV_NBR  ");
		sql.append("                    AND ( ");
		sql.append("                         (APL_PLIST.START_TIME >= BASE.START_TIME AND APL_PLIST.END_TIME <= BASE.END_TIME) ");
		sql.append("                      OR (APL_PLIST.START_TIME <= BASE.START_TIME AND APL_PLIST.END_TIME >= BASE.END_TIME) ");
		sql.append("                    ) ");
		sql.append("                    AND APL_PLIST.DEV_STATUS IN ('B04', 'C05', 'D06', 'E07') ");
		sql.append("                   ) >= 1 THEN 'Y' ");
		sql.append("              WHEN TO_CHAR(SYSDATE, 'yyyyMMddHH24mmSS') > BASE.END_TIME THEN 'X'  ");
		sql.append("         ELSE 'N' END AS USE_TYPE ");
		sql.append("  FROM ( ");
		sql.append("    SELECT M.FLAG, ");
		sql.append("           M.SEQ, ");
		sql.append("           M.DEV_NBR, ");
		sql.append("           M.DC_NBR, ");
		sql.append("           M.OP_NBR, ");
		sql.append("           M.BRA_NBR, ");
		sql.append("           M.DEV_TAKE_EMP, ");
		sql.append("           MEM.EMP_NAME AS DEV_TAKE_EMP_NAME, ");
		sql.append("           M.DEV_SITE_TYPE, ");
		sql.append("           T_LIST.O_TYPE, ");
		sql.append("           TO_CHAR(:use_date, 'yyyyMMdd') || T_LIST.START_TIME || '00' AS START_TIME,  ");
		sql.append("           CASE WHEN T_LIST.END_TIME < T_LIST.START_TIME THEN TO_CHAR(:use_date + 1, 'yyyyMMdd') || T_LIST.END_TIME || '00' ");
		sql.append("           ELSE TO_CHAR(:use_date, 'yyyyMMdd') || T_LIST.END_TIME || '00' END AS END_TIME ");
		sql.append("    FROM TBMAO_DEV_MAST M ");
		sql.append("    LEFT JOIN TBORG_MEMBER MEM ON M.DEV_TAKE_EMP = MEM.EMP_ID ");
		sql.append("    LEFT JOIN ( ");
		sql.append("        SELECT PARAM_ORDER AS O_TYPE, ");
		sql.append("               PARAM_CODE AS START_TIME, ");
		sql.append("               PARAM_DESC AS END_TIME ");
		sql.append("        FROM TBSYSPARAMETER ");
		sql.append("        WHERE PARAM_TYPE = 'MAO.USE_PERIOD_HOUR' ");
		sql.append("    ) T_LIST ON 1 = 1 ");
		sql.append("    WHERE M.FLAG = 'U' ");
		sql.append("    AND M.DEV_STATUS = 'N' ");
		sql.append("    AND EXISTS (SELECT 1 FROM VWORG_EMP_UHRM_INFO UHRM WHERE UHRM.EMP_ID = :loginID AND UHRM.DEPT_ID = M.OP_NBR) ");

		queryCondition.setObject("loginID", (String) SysInfo.getInfoValue(SystemVariableConsts.LOGINID));

		if (StringUtils.isNotBlank(inputVO.getDev_site_type())) {
			sql.append("AND M.DEV_SITE_TYPE = :dev_site_type ");
			queryCondition.setObject("dev_site_type", inputVO.getDev_site_type());
		}

		sql.append("  ) BASE ");
		sql.append(") ");

		sql.append("SELECT * ");
		sql.append("FROM USE_STATUS ");
		sql.append("PIVOT (MAX(USE_TYPE) FOR O_TYPE IN ('1' AS TITLE_1, '2' AS TITLE_2, '3' AS TITLE_3, '4' AS TITLE_4, '5' AS TITLE_5, '6' AS TITLE_6, '7' AS TITLE_7, '8' AS TITLE_8, '9' AS TITLE_9, '10' AS TITLE_10, '11' AS TITLE_11)) ");

		queryCondition.setQueryString(sql.toString());

		queryCondition.setObject("use_date", new Timestamp(inputVO.getUse_date().getTime()));

		outputVO.setResultList(dam.exeQuery(queryCondition));

		this.sendRtnObject(outputVO);
	}

	public void add_query(Object body, IPrimitiveMap header) throws JBranchException {

		MAO211InputVO inputVO = (MAO211InputVO) body;
		MAO211OutputVO outputVO = new MAO211OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT C.CUST_NAME, ");
		sql.append("       C.CUST_ID, ");
		sql.append("       REL_TYPE, ");
		sql.append("       '' AS REL_TYPE_OTH, ");
		sql.append("       C.BRA_NBR, ");
		sql.append("       AO.AO_CODE, ");
		sql.append("       AO.EMP_NAME, ");
		sql.append("       UEMP.EMP_ID AS UEMP_ID, ");
		sql.append("       UEMP.EMP_NAME AS UEMP_NAME, ");
		sql.append("       R.CUST_ID_M, ");
		sql.append("       RC.CUST_NAME AS JOIN_SRV_CUST_NAME ");
		sql.append("FROM TBCRM_CUST_MAST C ");
		sql.append("LEFT JOIN VWORG_EMP_INFO AO ON C.AO_CODE = AO.AO_CODE ");
		sql.append("LEFT JOIN VWORG_EMP_UHRM_INFO UEMP ON C.UEMP_ID = UEMP.EMP_ID ");
		sql.append("LEFT JOIN TBCRM_CUST_REL R ON C.CUST_ID = R.CUST_ID_M AND R.CUST_ID_S = C.CUST_ID ");
		sql.append("LEFT JOIN TBCRM_CUST_MAST RC ON R.CUST_ID_M = RC.CUST_ID ");
		sql.append("WHERE AO.EMP_ID = :uEmpID ");
		sql.append("AND C.CUST_ID IS NOT NULL ");

		if (!StringUtil.isBlank(inputVO.getCust_id()))
			sql.append("AND C.CUST_ID = :cust_id ");

		if (!StringUtil.isBlank(inputVO.getCust_name()))
			sql.append("AND C.CUST_NAME like :cust_name ");
		
		//找客戶為主戶的所有從戶
		sql.append("UNION ");
		sql.append("SELECT C.CUST_NAME, ");
		sql.append("       R.CUST_ID_S AS CUST_ID, ");
		sql.append("       R.REL_TYPE, ");
		sql.append("       R.REL_TYPE_OTH, ");
		sql.append("       RC.BRA_NBR, ");
		sql.append("       RC.AO_CODE, ");
		sql.append("       AO.EMP_NAME, ");
		sql.append("       UEMP.EMP_ID AS UEMP_ID, ");
		sql.append("       UEMP.EMP_NAME AS UEMP_NAME, ");
		sql.append("       R.CUST_ID_M, ");
		sql.append("       RC.CUST_NAME AS JOIN_SRV_CUST_NAME ");
		sql.append("FROM TBCRM_CUST_MAST C ");
		sql.append("LEFT JOIN VWORG_EMP_INFO AO ON C.AO_CODE = AO.AO_CODE ");
		sql.append("LEFT JOIN VWORG_EMP_UHRM_INFO UEMP ON C.UEMP_ID = UEMP.EMP_ID ");
		sql.append("LEFT JOIN TBCRM_CUST_REL R ON C.CUST_ID = R.CUST_ID_S ");
		sql.append("LEFT JOIN TBCRM_CUST_MAST RC ON R.CUST_ID_M = RC.CUST_ID ");
		sql.append("WHERE AO.EMP_ID = :uEmpID ");
		sql.append("AND R.CUST_ID_S IS NOT NULL ");
		sql.append("AND R.REL_MBR_YN = 'Y' "); 

		if (!StringUtil.isBlank(inputVO.getCust_id()))
			sql.append("AND R.CUST_ID_M = :cust_id ");

		if (!StringUtil.isBlank(inputVO.getCust_name()))
			sql.append("AND C.CUST_NAME like :cust_name ");
		
		//找客戶為從戶的所有主戶
		sql.append("UNION ");
		sql.append("SELECT C.CUST_NAME, ");
		sql.append("       R.CUST_ID_M AS CUST_ID, ");
		sql.append("       R.REL_TYPE, ");
		sql.append("       R.REL_TYPE_OTH, ");
		sql.append("       RC.BRA_NBR, ");
		sql.append("       RC.AO_CODE, ");
		sql.append("       AO.EMP_NAME, ");
		sql.append("       UEMP.EMP_ID AS UEMP_ID, ");
		sql.append("       UEMP.EMP_NAME AS UEMP_NAME, ");
		sql.append("       R.CUST_ID_S, ");
		sql.append("       RC.CUST_NAME AS JOIN_SRV_CUST_NAME ");
		sql.append("FROM TBCRM_CUST_MAST C ");
		sql.append("LEFT JOIN VWORG_EMP_INFO AO ON C.AO_CODE = AO.AO_CODE ");
		sql.append("LEFT JOIN VWORG_EMP_UHRM_INFO UEMP ON C.UEMP_ID = UEMP.EMP_ID ");
		sql.append("LEFT JOIN TBCRM_CUST_REL R ON C.CUST_ID = R.CUST_ID_M ");
		sql.append("LEFT JOIN TBCRM_CUST_MAST RC ON R.CUST_ID_S = RC.CUST_ID ");
		sql.append("WHERE AO.EMP_ID = :uEmpID ");
		sql.append("AND R.CUST_ID_M IS NOT NULL ");
		sql.append("AND R.REL_MBR_YN = 'Y' "); 

		if (!StringUtil.isBlank(inputVO.getCust_id()))
			sql.append("AND R.CUST_ID_S = :cust_id ");

		if (!StringUtil.isBlank(inputVO.getCust_name()))
			sql.append("AND C.CUST_NAME like :cust_name ");
		
		sql.append("ORDER BY REL_TYPE ");

		queryCondition.setQueryString(sql.toString());

		queryCondition.setObject("uEmpID", inputVO.getEmp_id());//getUserVariable(FubonSystemVariableConsts.LOGINID)

		if (!StringUtil.isBlank(inputVO.getCust_id()))
			queryCondition.setObject("cust_id", inputVO.getCust_id().toUpperCase());

		if (!StringUtil.isBlank(inputVO.getCust_name()))
			queryCondition.setObject("cust_name", "%" + inputVO.getCust_name() + "%");

		List<Map<String, Object>> list = dam.exeQuery(queryCondition);

		// 2.查詢客戶的結果要控管筆數上限，若超過請顯示"查詢結果超過50筆，為維持系統效能，請給予更多的查詢條件並重新查詢！"
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql = new StringBuffer();
		sql.append("SELECT PARAM_CODE ");
		sql.append("FROM TBSYSPARAMETER ");
		sql.append("WHERE PARAM_TYPE = 'MAO.AOUSER_QRY_MAX_LIMIT' ");

		queryCondition.setQueryString(sql.toString());

		List<Map<String, Object>> limitList = dam.exeQuery(queryCondition);

		if (list.size() > Integer.valueOf(ObjectUtils.toString(limitList.get(0).get("PARAM_CODE"))))
			throw new APException("查詢結果超過50筆，為維持系統效能，請給予更多的查詢條件並重新查詢！");

		outputVO.setAddList(list);

		//2017/4/7
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql = new StringBuffer();
		sql.append("SELECT CUST_LIST.CUST_ID, ");
		sql.append("       CASE WHEN S.SEQ IS NOT NULL THEN 'S' ELSE 'M' END AS CUST_TYPE ");
		sql.append("FROM ( ");
		sql.append("  SELECT DISTINCT SUBSTR(REGEXP_SUBSTR(VISIT_CUST_LIST,'[^,]+', 1, LEVEL), 0, INSTR(REGEXP_SUBSTR(VISIT_CUST_LIST,'[^,]+', 1, LEVEL), ':') - 1) AS CUST_ID ");
		sql.append("  FROM ( ");
		sql.append("    SELECT VISIT_CUST_LIST ");
		sql.append("    FROM TBMAO_DEV_APL_PLIST ");
		sql.append("    WHERE TRUNC(USE_DATE) = TRUNC(:ssdate) ");
		sql.append("    AND APL_EMP_ID = :emp_id ");
		sql.append("  ) BASE ");
		sql.append("  CONNECT BY REGEXP_SUBSTR(VISIT_CUST_LIST, '[^,]+', 1, LEVEL) IS NOT NULL ");
		sql.append(") CUST_LIST ");
		sql.append("LEFT JOIN TBCRM_CUST_REL R ON CUST_LIST.CUST_ID = R.CUST_ID_M AND CUST_LIST.CUST_ID = R.CUST_ID_S AND R.REL_STATUS = 'RSN' ");
		sql.append("LEFT JOIN TBCRM_CUST_REL S ON CUST_LIST.CUST_ID <> S.CUST_ID_M AND CUST_LIST.CUST_ID = S.CUST_ID_S AND S.REL_STATUS = 'RSN' ");

		queryCondition.setObject("ssdate", inputVO.getUse_date());
		queryCondition.setObject("emp_id", getUserVariable(FubonSystemVariableConsts.LOGINID));

		queryCondition.setQueryString(sql.toString());

		outputVO.setResultList(dam.exeQuery(queryCondition));

		this.sendRtnObject(outputVO);
	}

	//取得AO_CODE對應的員工編號
	public void get_emp_id(Object body, IPrimitiveMap header) throws JBranchException {

		MAO211InputVO inputVO = (MAO211InputVO) body;

		List<Map<String, Object>> empIdList = exeQueryForQcf(genDefaultQueryConditionIF().setQueryString(new StringBuilder().append(" SELECT EMP_ID FROM VWORG_AO_INFO ").append(" WHERE AO_CODE = :ao_code ").toString()).setObject("ao_code", inputVO.getAo_code()));

		String emp_id = ObjectUtils.toString(empIdList.get(0).get("EMP_ID"));

		this.sendRtnObject(emp_id);
	}

	public void application(Object body, IPrimitiveMap header) throws JBranchException {

		MAO211InputVO inputVO = (MAO211InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();

		if (inputVO.getRelationshipList().size() != 0) {
			// check 關係戶 2017/4/10
			sql.append("SELECT RC.CUST_NAME, ");
			sql.append("       RC.CUST_ID, ");
			sql.append("       R.REL_TYPE, ");
			sql.append("       R.REL_TYPE_OTH, ");
			sql.append("       RC.BRA_NBR, ");
			sql.append("       RC.AO_CODE, ");
			sql.append("       BASE.EMP_NAME, ");
			sql.append("       R.JOIN_SRV_CUST_ID, ");
			sql.append("       RC.CUST_NAME AS JOIN_SRV_CUST_NAME ");
			sql.append("FROM TBCRM_CUST_MAST C ");
			sql.append("LEFT JOIN ( ");
			sql.append("  SELECT MEM.EMP_ID, MEM.EMP_NAME, AO.AO_CODE ");
			sql.append("  FROM TBORG_SALES_AOCODE AO ");
			sql.append("  LEFT JOIN TBORG_MEMBER MEM ON AO.EMP_ID = MEM.EMP_ID ");
			sql.append(") BASE ON BASE.AO_CODE = C.AO_CODE ");
			sql.append("LEFT JOIN TBCRM_CUST_REL R ON C.CUST_ID = R.CUST_ID_M ");
			sql.append("LEFT JOIN TBCRM_CUST_MAST RC ON R.CUST_ID_S = RC.CUST_ID ");
			sql.append("WHERE C.AO_CODE = :ao_code ");
			sql.append("AND R.REL_STATUS = 'RSN' ");
			sql.append("AND R.CUST_ID_M IN (:list) ");
			sql.append("AND R.CUST_ID_S NOT IN (:list) ");

			queryCondition.setObject("ao_code", inputVO.getAo_code());
			queryCondition.setObject("list", inputVO.getCustList());

			queryCondition.setQueryString(sql.toString());

			List<Map<String, Object>> list = dam.exeQuery(queryCondition);

			for (String str : inputVO.getCustList2()) {
				Boolean check = true;
				for (Map<String, Object> map : list) {
					if (map.containsValue(str)) {
						check = false;
						break;
					}
				}

				if (check)
					throw new APException("該關係戶無所屬主戶");
			}
		}

		sql = new StringBuffer();
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		sql.append("SELECT MIN(PARAM_CODE) AS PARAM_CODE, ");
		sql.append("       CASE WHEN MAX(PARAM_ORDER) > 10 THEN MIN(PARAM_DESC) ELSE MAX(PARAM_DESC) END AS PARAM_DESC ");
		sql.append("FROM TBSYSPARAMETER ");
		sql.append("WHERE PARAM_TYPE = 'MAO.USE_PERIOD_HOUR' ");
		sql.append("AND PARAM_ORDER IN (:use_time_list) ");
		sql.append("GROUP BY PARAM_TYPE ");

		queryCondition.setQueryString(sql.toString());

		queryCondition.setObject("use_time_list", inputVO.getUseTimeList());

		List<Map<String, Object>> timeList = dam.exeQuery(queryCondition);

		TBMAO_DEV_APL_PLISTVO vo = new TBMAO_DEV_APL_PLISTVO();
		
		vo.setSEQ(getSEQ());
		vo.setDEV_NBR(inputVO.getDev_nbr());
		vo.setAPL_EMP_ID(inputVO.getEmp_id());
		vo.setVISIT_CUST_LIST(inputVO.getCustString());
		vo.setDEV_STATUS(inputVO.getRelationshipList().size() != 0 ? "B04" : "C05");
		vo.setUSE_DATE(new Timestamp(inputVO.getUse_date().getTime()));
		vo.setUSE_PERIOD_S_TIME(ObjectUtils.toString(timeList.get(0).get("PARAM_CODE")));
		vo.setUSE_PERIOD_E_TIME(ObjectUtils.toString(timeList.get(0).get("PARAM_DESC")));
		
		dam.create(vo);

		this.sendRtnObject(checkFpsValid(inputVO.getCustList(), inputVO.getUse_date()));
	}

	private List<Map<String, Object>> checkFpsValid(List<String> custList, Date useDate) throws DAOException, JBranchException {

		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();

		if (custList.size() <= 0)
			return new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> validationList = new ArrayList<Map<String, Object>>();

		for (String custID : custList) {
			if (StringUtils.isBlank(custID))
				continue;

			Map<String, Object> validation = new HashMap<String, Object>();
			Map<String, Object> tmp = new HashMap<String, Object>();

			validation.put("custID", custID);

			qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb = new StringBuilder();
			sb.append("SELECT MAST.KYC_DUE_DATE, "); // KYC有效期
			sb.append("       NVL(NOTE.SIGN_AGMT_YN, 'N') SIGN_AGMT_YN, "); // 有無簽屬推介同意書
			sb.append("       NVL(NOTE.REC_YN, 'N') REC_YN, "); // 可否推介
			sb.append("       NVL(NOTE.SP_CUST_YN, 'N') SP_CUST_YN, "); // 特定客戶(特殊客戶)
			sb.append("       NVL(NOTE.COMM_RS_YN, 'N') COMM_RS_YN, "); // 拒銷註記
			sb.append("       NVL(NOTE.COMM_NS_YN, 'N') COMM_NS_YN, "); // 撤銷註記
			sb.append("       NVL(NOTE.COMPLAIN_YN, 'N') COMPLAIN_YN, "); // 客訴註記
			sb.append("	      CONTACT.EMAIL ");
			sb.append("FROM TBCRM_CUST_MAST MAST ");
			sb.append("LEFT JOIN TBCRM_CUST_NOTE NOTE ON MAST.CUST_ID = NOTE.CUST_ID ");
			sb.append("LEFT JOIN TBCRM_CUST_CONTACT CONTACT ON MAST.CUST_ID = CONTACT.CUST_ID ");
			sb.append("WHERE MAST.CUST_ID = :custId ");

			qc.setObject("custId", custID);

			qc.setQueryString(sb.toString());

			List<Map<String, Object>> list = dam.exeQuery(qc);

			if (list.size() <= 0) {
				tmp.put("KYC_DUE_DATE", null);
				tmp.put("REC_YN", "N"); // 能否簽署
				tmp.put("SIGN_AGMT_YN", "N"); // 已簽署推介同意書
				tmp.put("SP_CUST_YN", "N"); // 特殊客戶註記
				tmp.put("COMM_RS_YN", "N"); // 拒銷註記
				tmp.put("COMM_NS_YN", "N"); // 撤銷註記
				tmp.put("COMPLAIN_YN", "N"); // 客訴註記
				tmp.put("EMAIL", ""); //
			} else {
				tmp.put("KYC_DUE_DATE", list.get(0).get("KYC_DUE_DATE"));
				tmp.put("REC_YN", list.get(0).get("REC_YN")); // 能否簽署
				tmp.put("SIGN_AGMT_YN", list.get(0).get("SIGN_AGMT_YN")); // 已簽署推介同意書
				tmp.put("SP_CUST_YN", list.get(0).get("SP_CUST_YN")); // 特殊客戶註記
				tmp.put("COMM_RS_YN", list.get(0).get("COMM_RS_YN")); // 拒銷註記
				tmp.put("COMM_NS_YN", list.get(0).get("COMM_NS_YN")); // 撤銷註記
				tmp.put("COMPLAIN_YN", list.get(0).get("COMPLAIN_YN")); // 客訴註記
				tmp.put("EMAIL", list.get(0).get("EMAIL")); //
			}

			// 暫時 Juan
			try {
				SOT701InputVO inputVO_701 = new SOT701InputVO();
				inputVO_701.setCustID(custID);

				SOT701 sot701 = (SOT701) PlatformContext.getBean("sot701");
				FP032675DataVO FP032675Data = sot701.getFP032675Data(inputVO_701);
				CustKYCDataVO CustKYCData = sot701.getCustKycData(inputVO_701);

				// FC032675DataVO.java及SOT710.java 增加BDAY_D 在1.5階新增 到時要注意
				tmp.put("KYC_DUE_DATE", CustKYCData.getKycDueDate());// KYC(效期)
				tmp.put("SIGN_AGMT_YN", StringUtils.isBlank(FP032675Data.getCustTxFlag()) ? "N" : FP032675Data.getCustTxFlag());// 有無簽屬推介同意書
			} catch (Exception e) {
				e.printStackTrace();
			}

			// 有效KYC：日期須檢核至外訪當日(useDate) + 3"工作天"，客戶KYC是否有效，如無有效KYC，訊息提醒理專。
			// 提醒訊息：客戶KYC於2018/5/11(日期為T+3)日失效，提醒客戶於網銀或行銀執行KYC。
			qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb = new StringBuilder();
			sb.append("SELECT PABTH_UTIL.FC_getBusiDay( :useDate , 'TWD', 3 ) AS DATE_MARK FROM DUAL ");

			qc.setObject("useDate", useDate);
			qc.setQueryString(sb.toString());
			List<Map<String, Object>> dateList = dam.exeQuery(qc);

			Date dateMark = new Date();

			if (dateList.size() > 0) {
				dateMark = (Date) dateList.get(0).get("DATE_MARK");
			}

			if (tmp.get("KYC_DUE_DATE") != null) {
				Date kycValidDate = (Date) tmp.get("KYC_DUE_DATE");
				Date kycDueDate = (Date) tmp.get("KYC_DUE_DATE");
				Date today = new Date();

				// 外訪當日(useDate) + 3工作天去時分秒
				Calendar c = Calendar.getInstance();
				c.setTime(dateMark);
				c.set(Calendar.HOUR_OF_DAY, 0);
				c.set(Calendar.MINUTE, 0);
				c.set(Calendar.SECOND, 0);
				c.set(Calendar.MILLISECOND, 0);
				dateMark = c.getTime();

				// KYC有效日期去時分秒
				c = Calendar.getInstance();
				c.setTime(kycDueDate);
				c.set(Calendar.HOUR_OF_DAY, 0);
				c.set(Calendar.MINUTE, 0);
				c.set(Calendar.SECOND, 0);
				c.set(Calendar.MILLISECOND, 0);
				kycDueDate = c.getTime();

				// 系統日期去時分秒
				c = Calendar.getInstance();
				c.setTime(today);
				c.set(Calendar.HOUR_OF_DAY, 0);
				c.set(Calendar.MINUTE, 0);
				c.set(Calendar.SECOND, 0);
				c.set(Calendar.MILLISECOND, 0);
				today = c.getTime();

				validation.put("kycInvalid", kycDueDate.compareTo(today) < 0); // KYC已失效
				validation.put("kycNearlyInvalid", kycDueDate.compareTo(dateMark) < 0); // KYC即將失效
				validation.put("kycDueDate", tmp.get("KYC_DUE_DATE")); // KYC有效期
			} else {
				validation.put("kycInvalid", true);
				validation.put("kycDueDate", null);
			}

			// 推介同意書
			validation.put("signAgmtInvalid", "Y".equals(tmp.get("REC_YN")) && !"Y".equals(tmp.get("SIGN_AGMT_YN")));
			validation.put("recInvalid", !"Y".equals(tmp.get("REC_YN")) && !"Y".equals(tmp.get("SIGN_AGMT_YN")));

			// email
			validation.put("emailInvalid", StringUtils.isBlank(ObjectUtils.toString(tmp.get("EMAIL"))));

			// 特殊客戶註記
			validation.put("specialInvalid", "Y".equals(tmp.get("SP_CUST_YN"))); // 特殊客戶
			validation.put("rsInvalid", "Y".equals(tmp.get("COMM_RS_YN"))); // 拒銷
			validation.put("nsInvalid", "Y".equals(tmp.get("COMM_NS_YN"))); // 撤銷
			validation.put("complainInvalid", "Y".equals(tmp.get("COMPLAIN_YN"))); // 撤銷	

			validationList.add(validation);
		}

		return validationList;
	}

	private String getSEQ() throws JBranchException {
		SerialNumberUtil sn = new SerialNumberUtil();
		String seqNum = "";
		try {
			seqNum = sn.getNextSerialNumber("MAO111");
		} catch (Exception e) {
			sn.createNewSerial("MAO111", "00000", null, null, null, 1, new Long("99999"), "y", new Long("0"), null);
			seqNum = sn.getNextSerialNumber("MAO111");
		}
		return seqNum;
	}
}