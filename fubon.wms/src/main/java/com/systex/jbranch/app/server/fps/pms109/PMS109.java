package com.systex.jbranch.app.server.fps.pms109;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

//import com.systex.jbranch.app.common.fps.table.TBPMS_SALES_PLANSPK;
//import com.systex.jbranch.app.common.fps.table.TBPMS_SALES_PLANSVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.serialnumber.SerialNumberUtil;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * @Description : 新增銷售計劃
 * @Author      : 20160613 KevinHsu
 * @Editor      : 20170112 KevinHsu
 * @Editor      : 20220825 Ocean.Lin WMS-CR-20220307-01_新增量收本利預估報表
 */

@Component("pms109")
@Scope("request")
public class PMS109 extends FubonWmsBizLogic {
	
	private DataAccessManager dam = null;
		
	/** 查詢 **/
	public void queryData (Object body, IPrimitiveMap header) throws JBranchException {

		PMS109InputVO inputVO = (PMS109InputVO) body;
		PMS109OutputVO outputVO = new PMS109OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();

		if (!StringUtils.isBlank(inputVO.getSRC_TYPE())) {
			if (!StringUtils.isBlank(inputVO.getCUST_ID())) {
				switch (inputVO.getSRC_TYPE()) {
					case "1":	// 類別1: 金流名單_客戶主檔
						sb = new StringBuffer();
						sb.append("SELECT SUBSTR(A.DATA_DATE, 1, 6) AS YEAR_MON, ");
						sb.append("       A.CUST_ID, ");
						sb.append("       A.CUST_NAME, ");
						sb.append("       CASE WHEN (B.SEQ IS NOT NULL) THEN B.SEQ + 1 ELSE 1 END AS NEWSEQ ");
						sb.append("FROM TBPMS_CUS_CF_MAST A ");
						sb.append("LEFT JOIN TBPMS_SALES_PLANS B ON A.CUST_ID = B.CUST_ID AND SUBSTR(A.DATA_DATE, 1, 6) = B.PLAN_YEARMON ");
						sb.append("WHERE A.CUST_ID LIKE :CUST_IDD ");
						
						if (!StringUtils.isBlank(inputVO.getSEQ())) {
							sb.append("AND B.SEQ = :SEQQ");
							queryCondition.setObject("SEQQ", inputVO.getSEQ());
						}
						
						queryCondition.setObject("CUST_IDD", "%" + inputVO.getCUST_ID() + "%");
						
						sb.append(" ORDER BY A.DATA_DATE DESC ");
						
						queryCondition.setQueryString(sb.toString());
						
						break;
					case "2":	// 類別2: 潛力金流名單_客戶主檔
						sb = new StringBuffer();
						sb.append("SELECT A.CUST_ID, ");
						sb.append("       A.CUST_NAME, ");	
						sb.append("       NVL((SELECT MAX(SEQ) + 1 FROM TBPMS_SALES_PLANS WHERE CUST_ID = :CUST_IDD ").append(!StringUtils.isBlank(inputVO.getSEQ()) ? " AND SEQ = :SEQQ ": "").append("), 1) AS NEWSEQ ");
						
						if (!StringUtils.isBlank(inputVO.getSEQ())) {	// 約訪覆核序號
							sb.append("       , NVL((SELECT C.SEQ FROM TBPMS_SALES_PLANS B LEFT JOIN TBCRM_WKPG_AS_MAST C ON B.SEQ = C.SALES_PLAN_SEQ WHERE B.CUST_ID = :CUST_IDD AND B.SEQ = :SEQQ), 1) AS SEQ2 ");
						}
						
						sb.append("FROM TBPMS_POT_CF_MAST A ");
						sb.append("LEFT JOIN TBPMS_SALES_PLANS C ON A.CUST_ID = C.CUST_ID ");
						sb.append("WHERE A.CUST_ID = :CUST_IDD  ");
						
						if (!StringUtils.isBlank(inputVO.getSEQ())) {
							sb.append("AND C.SEQ = :SEQQ  ");
							queryCondition.setObject("SEQQ", inputVO.getSEQ());
						}
						
						queryCondition.setObject("CUST_IDD", inputVO.getCUST_ID() );
						
						sb.append("ORDER BY a.DATA_DATE DESC ");
						
						queryCondition.setQueryString(sb.toString());
						
						break;
					case "3":	// 類別3: 客管專用  CRM系列
						sb = new StringBuffer();
						sb.append("SELECT A.CUST_ID, ");
						sb.append("       A.CUST_NAME, ");
						sb.append("       CASE WHEN  (B.SEQ IS NOT NULL) THEN B.SEQ + 1 ELSE 0 END AS NEWSEQ ");
						sb.append("FROM MVCRM_AST_AMT A ");
						sb.append("LEFT JOIN TBPMS_SALES_PLANS B ON A.CUST_ID = B.CUST_ID ");
						sb.append("WHERE A.CUST_ID = :CUST_IDD ");						

						if (!StringUtils.isBlank(inputVO.getSEQ())) {
							sb.append(" and B.SEQ = :SEQQ");
							queryCondition.setObject("SEQQ", inputVO.getSEQ());
						}
						
						queryCondition.setObject("CUST_IDD", inputVO.getCUST_ID());

						queryCondition.setQueryString(sb.toString());
						
						break;
					case "4":	// 類別4: 客管專用 CRM系列 單純檢視
						sb = new StringBuffer();
						sb.append("SELECT C.* ");
						sb.append("FROM TBPMS_SALES_PLANS C ");
						sb.append("WHERE 1 = 1 ");

						if (!StringUtils.isBlank(inputVO.getSEQ())) {
							sb.append("AND C.SEQ = :SEQQ");
							queryCondition.setObject("SEQQ", inputVO.getSEQ());
						}
						
						if (!StringUtils.isBlank(inputVO.getCUST_ID())) {
							sb.append("AND C.CUST_ID = :CUST_IDD");
							queryCondition.setObject("CUST_IDD",inputVO.getCUST_ID());
						}
						
						queryCondition.setQueryString(sb.toString());
						
						break;
				}
				
				outputVO.setResultList(dam.exeQuery(queryCondition));
				
				this.sendRtnObject(outputVO);
			} else {				
				throw new APException("沒有接到客戶ID資料");
			}
		} else {
			throw new APException("請由交易傳進來");
		}
	}
	
	/** 以下是新增資料到銷售計劃 ADD BY 20160613 KevinHsu **/
	public void queryMod (Object body, IPrimitiveMap header) throws JBranchException, ParseException {
		
		PMS109InputVO inputVO = (PMS109InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
				
		switch (inputVO.getQueryModType()) {
			case "A":
				// *** 取得理專資料 START *** //
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sb = new StringBuffer();
				sb.append("SELECT EMP_ID, EMP_NAME ");
				sb.append("FROM TBORG_MEMBER ");
				sb.append("WHERE EMP_ID = :empID ");
				
				queryCondition.setObject("empID", inputVO.getEMP_ID());
				
				queryCondition.setQueryString(sb.toString());
				
				List<Map<String, Object>> aoDTL = dam.exeQuery(queryCondition);
				// *** 取得理專資料 END *** //
				
				// *** 寫入銷售計畫 START *** //
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sb = new StringBuffer();
				sb.append("INSERT INTO TBPMS_SALES_PLANS ( ");
				sb.append("  SEQ, ");
				sb.append("  PLAN_YEARMON, BRANCH_NBR, AO_CODE, ");   
				sb.append("  EMP_ID, EMP_NAME, CUST_ID, CUST_NAME, ");
				sb.append("  SRC_TYPE, ");
				sb.append("  EST_PRD, EST_AMT, EST_EARNINGS_RATE, EST_EARNINGS, ");
				
				if (StringUtils.isNotEmpty(inputVO.getACTION_DATE())) {
					sb.append("  ACTION_DATE, ");
				}
				
				if (StringUtils.isNotEmpty(inputVO.getMEETING_DATE())) {
					sb.append("  MEETING_DATE, ");
				}
				
				sb.append("  CLOSE_DATE, ");
				sb.append("  VERSION, CREATETIME, CREATOR, MODIFIER, LASTUPDATE ");
				sb.append(") ");
				
				sb.append("VALUES ( ");
				sb.append("  :SEQ, ");
				sb.append("  :PLAN_YEARMON, :BRANCH_NBR, :AO_CODE, ");   
				sb.append("  :EMP_ID, :EMP_NAME, :CUST_ID, :CUST_NAME, ");
				sb.append("  :SRC_TYPE, ");
				sb.append("  :EST_PRD, :EST_AMT, :EST_EARNINGS_RATE, :EST_EARNINGS, ");
				
				if (StringUtils.isNotEmpty(inputVO.getACTION_DATE())) {
					sb.append("  :ACTION_DATE, ");
					queryCondition.setObject("ACTION_DATE", new Timestamp(Long.parseLong(inputVO.getACTION_DATE().toString())));
				}
				
				if (StringUtils.isNotEmpty(inputVO.getMEETING_DATE())) {
					sb.append("  :MEETING_DATE, ");
					queryCondition.setObject("MEETING_DATE", new Timestamp(Long.parseLong(inputVO.getMEETING_DATE().toString())));
				}
				
				sb.append("  :CLOSE_DATE, ");
				sb.append("  0, SYSDATE, :LOGIN_ID, :LOGIN_ID, SYSDATE ");
				sb.append(") ");
				
				queryCondition.setObject("SEQ", new BigDecimal(getSN()));
				queryCondition.setObject("PLAN_YEARMON", inputVO.getPLAN_YEARMON());
				queryCondition.setObject("BRANCH_NBR", SysInfo.getInfoValue(FubonSystemVariableConsts.LOGINBRH));
				queryCondition.setObject("AO_CODE", inputVO.getAO_CODE());
				queryCondition.setObject("EMP_ID", inputVO.getEMP_ID());
				queryCondition.setObject("EMP_NAME", (aoDTL.size() > 0 ? aoDTL.get(0).get("EMP_NAME") : ""));
				queryCondition.setObject("CUST_ID", inputVO.getCUST_ID());
				queryCondition.setObject("CUST_NAME", inputVO.getCUST_NAME());
				queryCondition.setObject("SRC_TYPE", inputVO.getSRC_TYPE());
				queryCondition.setObject("EST_PRD", inputVO.getEST_PRD());
				queryCondition.setObject("EST_AMT", inputVO.getEST_AMT());
				queryCondition.setObject("EST_EARNINGS_RATE", inputVO.getEST_EARNINGS_RATE());
				queryCondition.setObject("EST_EARNINGS", inputVO.getEST_EARNINGS());
				queryCondition.setObject("CLOSE_DATE", (StringUtils.isNotEmpty(inputVO.getCLOSE_DATE()) ? new Timestamp(Long.parseLong(inputVO.getCLOSE_DATE().toString())) : null));;
				queryCondition.setObject("LOGIN_ID", getUserVariable(FubonSystemVariableConsts.LOGINID));
				
				queryCondition.setQueryString(sb.toString());
				
				dam.exeUpdate(queryCondition);
				// *** 寫入銷售計畫 END *** //
				
				break;
			case "M":
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sb = new StringBuffer();
				
				sb.append("UPDATE TBPMS_SALES_PLANS ");
				sb.append("SET EST_AMT = :EST_AMT, ");
				sb.append("    EST_EARNINGS_RATE = :EST_EARNINGS_RATE, ");
				sb.append("    EST_EARNINGS = :EST_EARNINGS, ");
				
				if (StringUtils.isNotEmpty(inputVO.getACTION_DATE())) {
					sb.append("    ACTION_DATE = :ACTION_DATE, ");
					queryCondition.setObject("ACTION_DATE", new Timestamp(Long.parseLong(inputVO.getACTION_DATE().toString())));
				}
				
				if (StringUtils.isNotEmpty(inputVO.getMEETING_DATE())) {
					sb.append("    MEETING_DATE = :MEETING_DATE, ");
					queryCondition.setObject("MEETING_DATE", new Timestamp(Long.parseLong(inputVO.getMEETING_DATE().toString())));
				}
				
				sb.append("    CLOSE_DATE = :CLOSE_DATE, ");
				sb.append("    MODIFIER = :LOGIN_ID, ");
				sb.append("    LASTUPDATE = SYSDATE ");
				sb.append("WHERE SEQ = :SEQ ");
				
				queryCondition.setObject("SEQ", inputVO.getSEQ());
				queryCondition.setObject("EST_AMT", inputVO.getEST_AMT());
				queryCondition.setObject("EST_EARNINGS_RATE", inputVO.getEST_EARNINGS_RATE());
				queryCondition.setObject("EST_EARNINGS", inputVO.getEST_EARNINGS());
				queryCondition.setObject("CLOSE_DATE", (StringUtils.isNotEmpty(inputVO.getCLOSE_DATE()) ? new Timestamp(Long.parseLong(inputVO.getCLOSE_DATE().toString())) : null));;
				queryCondition.setObject("LOGIN_ID", getUserVariable(FubonSystemVariableConsts.LOGINID));
				
				queryCondition.setQueryString(sb.toString());
				
				dam.exeUpdate(queryCondition);
				
				break;
		}
		
		this.sendRtnObject(null);
	}
	
	private String getSN() throws JBranchException {
		
		SerialNumberUtil sn = new SerialNumberUtil();
		String seqNum = "";
		try {
			seqNum = sn.getNextSerialNumber("TBPMS_SALES_PLAN");
		} catch(Exception e) {
			sn.createNewSerial("TBPMS_SALES_PLAN", "00000", null, null, null, 1, new Long("99999"), "y", new Long("0"), null);
			seqNum = sn.getNextSerialNumber("TBPMS_SALES_PLAN");
		}
		
		return seqNum;
	}
	
	public void getAoCode(Object body, IPrimitiveMap header) throws JBranchException {
		
		PMS109InputVO inputVO = (PMS109InputVO) body;
		PMS109OutputVO OutputVO = new PMS109OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		String cust_id = "";
		if (inputVO.getPMSROW() instanceof String) {
			cust_id = ObjectUtils.toString(inputVO.getPMSROW());
		} else {
			Map<String, Object> map = (Map<String, Object>) inputVO.getPMSROW();
			cust_id = ObjectUtils.toString(map.get("CUST_ID"));
		}
		
		sb.append("SELECT A.AO_CODE, C.EMP_ID, C.EMP_NAME ");
		sb.append("FROM TBCRM_CUST_MAST A ");
		sb.append("LEFT JOIN TBORG_SALES_AOCODE B ON A.AO_CODE = B.AO_CODE ");
		sb.append("LEFT JOIN TBORG_MEMBER C ON B.EMP_ID = C.EMP_ID ");
		sb.append("WHERE A.CUST_ID = :cust_id ");
		
		queryCondition.setObject("cust_id", cust_id);

		queryCondition.setQueryString(sb.toString());

		OutputVO.setResultList(dam.exeQuery(queryCondition));
		
		System.out.println("getAoCode:" + sb.toString());
		this.sendRtnObject(OutputVO);
	}
	
	/** 權限設置 **/
	public void competence(Object body, IPrimitiveMap header) throws JBranchException {

		PMS109InputVO inputVO = (PMS109InputVO) body;
		PMS109OutputVO outputVO = new PMS109OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();

		switch (inputVO.getPRD()) {
			case "manager":
				sb.append("select a.AO_CODE || '-' || a.EMP_NAME as NAME, ");
				sb.append("       a.AO_CODE, ");
				sb.append("       a.BRANCH_NBR, ");
				sb.append("       a.EMP_NAME as EMP_NAME, ");
				sb.append("       a.EMP_ID as EMP_ID, ");
				sb.append("       a.BRANCH_NBR as BRANCH_NBRS, ");
				sb.append("       a.AO_JOB_RANK as AO_JOB_RANK  ");
				sb.append("from VWORG_BRANCH_EMP_DETAIL_INFO a ");
				sb.append("WHERE 1 = 1 ");
				
				if (!StringUtils.isBlank(inputVO.getAO_CODE())) {
					sb.append("AND a.AO_CODE = :ao_code ");
					queryCondition.setObject("ao_code", inputVO.getAO_CODE());
				} else {
					sb.append("AND a.EMP_ID = :EMP_IDD ");
					queryCondition.setObject("EMP_IDD", (String) SysInfo.getInfoValue(FubonSystemVariableConsts.LOGINID));					
				}
				
				queryCondition.setQueryString(sb.toString());

				outputVO.setEmpList(dam.exeQuery(queryCondition));
				
				break;
			case "aocode":
				sb.append("select a.AO_CODE||'-'||a.EMP_NAME as NAME, ");
				sb.append("       a.AO_CODE, ");
				sb.append("       a.BRANCH_NBR, ");
				sb.append("       a.EMP_NAME as EMP_NAME, ");
				sb.append("       a.EMP_ID as EMP_ID, ");
				sb.append("       a.BRANCH_NBR as BRANCH_NBRS, ");
				sb.append("       a.AO_JOB_RANK as AO_JOB_RANK ");
				sb.append("from VWORG_BRANCH_EMP_DETAIL_INFO a ");
				sb.append("WHERE AO_CODE IS NOT NULL ");
				sb.append("AND a.BRANCH_NBR = :BRANCH_NBRRR ");
				
				queryCondition.setObject("BRANCH_NBRRR", (String) SysInfo.getInfoValue(FubonSystemVariableConsts.LOGINBRH));

				queryCondition.setQueryString(sb.toString());

				outputVO.setEmpList(dam.exeQuery(queryCondition));
				
				break;
			default :
				outputVO.setEmpList(new ArrayList<Map<String, Object>>());
				
				break;
		}

		this.sendRtnObject(outputVO);
	}
	
}
