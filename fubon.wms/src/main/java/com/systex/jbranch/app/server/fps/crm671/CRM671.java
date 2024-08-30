package com.systex.jbranch.app.server.fps.crm671;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBCAM_CAL_SALES_TASKVO;
import com.systex.jbranch.app.common.fps.table.TBCRM_CUST_VISIT_RECORDVO;
import com.systex.jbranch.app.common.fps.table.TBCRM_CUST_VISIT_RECORD_DLOGVO;
import com.systex.jbranch.app.server.fps.crm131.CRM131;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataManager.WorkStation;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * @author walalala
 * @date 2016/06/08
 * 
 *       2016/12/29 Stella 改寫
 */
@Component("crm671")
@Scope("request")
public class CRM671 extends FubonWmsBizLogic {
	
	private DataAccessManager dam = null;

	// 查詢
	public void inquire(Object body, IPrimitiveMap header) throws JBranchException {
		WorkStation ws = DataManager.getWorkStation(uuid);
		CRM671InputVO inputVO = (CRM671InputVO) body;
		CRM671OutputVO return_VO = new CRM671OutputVO();
		dam = this.getDataAccessManager();

		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();

//		sql.append("SELECT '0' AS CHECK_AO, '0' AS CHECK_PS, '0' AS CHECK_FI, RECORD.VISIT_SEQ, RECORD.CMU_TYPE, ");
//		sql.append("       RECORD.VISIT_MEMO, RECORD.VISITOR_ROLE, RECORD.MODIFIER, RECORD.LASTUPDATE, INFO.EMP_NAME, ");
//		sql.append("       (SELECT PARAMETER.PARAM_CODE ");
//		sql.append("        FROM TBSYSPARAMETER PARAMETER ");
//		sql.append("        WHERE PARAMETER.PARAM_TYPE = 'CRM.VISIT_REC_ROLE' ");
//		sql.append("        AND INSTR(PARAMETER.PARAM_NAME , (SELECT PRIASS.PRIVILEGEID ");
//		sql.append("                                          FROM TBSYSSECUROLPRIASS PRIASS ");
//		sql.append("                                          WHERE 1 = 1  AND  PRIASS.ROLEID =  RECORD.VISITOR_ROLE )) > 0) AS PRI ");
//		sql.append("       FROM TBCRM_CUST_VISIT_RECORD RECORD ");
//		sql.append("LEFT OUTER JOIN TBORG_MEMBER INFO ON RECORD.MODIFIER = INFO.EMP_ID ");
//		sql.append("where 1 = 1 ");
		sql.append("SELECT '0' AS CHECK_AO, '0' AS CHECK_PS, '0' AS CHECK_FI, RECORD.VISIT_SEQ, RECORD.CMU_TYPE, ");
		sql.append("       RECORD.VISIT_MEMO, RECORD.VISIT_DT, RECORD.VISIT_CREPLY, ");
		sql.append("       RECORD.VISITOR_ROLE, RECORD.MODIFIER, RECORD.LASTUPDATE, INFO.EMP_NAME, ");
		sql.append("       (SELECT PARAM_CODE ");
		sql.append("        FROM ( ");
		sql.append("          SELECT REGEXP_SUBSTR(PAR.PARAM_NAME,'[^,]+', 1, TEMP_T.LEV) PARAM_NAME, PARAM_CODE ");
		sql.append("           FROM ( ");
		sql.append("             SELECT PARAMETER.PARAM_CODE, PARAMETER.PARAM_NAME ");
		sql.append("             FROM TBSYSPARAMETER PARAMETER ");
		sql.append("             WHERE PARAMETER.PARAM_TYPE = 'CRM.VISIT_REC_ROLE' ");
		sql.append("           ) PAR ");
		sql.append("           OUTER APPLY ( ");
		sql.append("             SELECT LEVEL AS LEV ");
		sql.append("             FROM DUAL ");
		sql.append("             CONNECT BY LEVEL <= REGEXP_COUNT(PAR.PARAM_NAME,',') + 1 ");
		sql.append("           ) TEMP_T ");
		sql.append("         ) A ");
		sql.append("         WHERE EXISTS ( ");
		sql.append("           SELECT PRIASS.PRIVILEGEID ");
		sql.append("           FROM TBSYSSECUROLPRIASS PRIASS ");
		sql.append("           WHERE 1 = 1 ");
		sql.append("           AND PRIASS.PRIVILEGEID = A.PARAM_NAME ");
		sql.append("	       AND PRIASS.ROLEID = RECORD.VISITOR_ROLE ");
		sql.append("	     )");
		sql.append("       ) AS PRI ");
		sql.append("FROM TBCRM_CUST_VISIT_RECORD RECORD ");
		sql.append("LEFT OUTER JOIN TBORG_MEMBER INFO ON RECORD.MODIFIER = INFO.EMP_ID ");
		sql.append("WHERE 1 = 1 ");
		sql.append("AND RECORD.CUST_ID = :cust_id ");

		//輔銷FA人員
		if ("014".equals(inputVO.getPri_id())) {
			sql.append(" AND VISITOR_ROLE = 'FA' ");
			sql.append(" AND RECORD.CREATOR = :emp_id ");
			queryCondition.setObject("emp_id", ws.getUser().getUserID());
		}
		//輔銷FA科長
		if ("015".equals(inputVO.getPri_id())) {
			sql.append(" AND VISITOR_ROLE = 'FA' ");
		}

		//輔銷IA人員
		if ("023".equals(inputVO.getPri_id())) {
			sql.append(" AND VISITOR_ROLE = 'IA' ");
			sql.append(" AND RECORD.CREATOR = :emp_id ");
			queryCondition.setObject("emp_id", ws.getUser().getUserID());
		}
		
		//輔銷IA科長
		if ("024".equals(inputVO.getPri_id())) {
			sql.append(" AND VISITOR_ROLE = 'IA' ");
		}

		//消金PS人員 & 個金AO
		if ("004".equals(inputVO.getPri_id()) || "004AO".equals(inputVO.getPri_id())) {
			sql.append(" AND RECORD.CREATOR = :emp_id ");
			queryCondition.setObject("emp_id", ws.getUser().getUserID());
		}
		
		sql.append("ORDER BY RECORD.LASTUPDATE DESC ");

		queryCondition.setQueryString(sql.toString());
		queryCondition.setObject("cust_id", inputVO.getCust_id());
		
		return_VO.setResultList2(dam.exeQuery(queryCondition));
		
		sendRtnObject(return_VO);
	}

	// 交辦事項
	public void inquire2(Object body, IPrimitiveMap header) throws JBranchException {
		WorkStation ws = DataManager.getWorkStation(uuid);
		CRM671InputVO inputVO = (CRM671InputVO) body;
		CRM671OutputVO return_VO = new CRM671OutputVO();
		dam = this.getDataAccessManager();

		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT SEQ_NO, TASK_DATE, TASK_STATUS, ");
		sql.append("        NVL(TRIM(TASK_TITLE),TASK_MEMO)AS TASK_MEMO ");
		sql.append(" FROM TBCAM_CAL_SALES_TASK ");
		sql.append(" where 1=1 ");
		sql.append(" AND CUST_ID = :cust_id AND EMP_ID = :emp ");
		sql.append(" ORDER BY TASK_DATE  DESC, TASK_ETIME ");

		queryCondition.setQueryString(sql.toString());
		queryCondition.setObject("cust_id", inputVO.getCust_id());
		queryCondition.setObject("emp", ws.getUser().getUserID());
		ResultIF list = dam.executePaging(queryCondition, inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
		int totalPage_i = list.getTotalPage();
		int totalRecord_i = list.getTotalRecord();
		return_VO.setResultList3(list);
		return_VO.setCurrentPageIndex(inputVO.getCurrentPageIndex());// 當前頁次
		return_VO.setTotalPage(totalPage_i);// 總頁次
		return_VO.setTotalRecord(totalRecord_i);// 總筆數
		this.sendRtnObject(return_VO);
	}

	// 交辦事項更改狀態儲存
	public void saveStatus(Object body, IPrimitiveMap header) throws JBranchException {
		WorkStation ws = DataManager.getWorkStation(uuid);
		CRM671InputVO inputVO = (CRM671InputVO) body;
		dam = this.getDataAccessManager();
		Timestamp currentTM = new Timestamp(System.currentTimeMillis());
		for (int i = 0; i < inputVO.getStatusList().size(); i++) {
			TBCAM_CAL_SALES_TASKVO vo = new TBCAM_CAL_SALES_TASKVO();
			vo = (TBCAM_CAL_SALES_TASKVO) dam.findByPKey(TBCAM_CAL_SALES_TASKVO.TABLE_UID, new BigDecimal(inputVO.getStatusList().get(i).get("SEQ").toString()));
			if (vo != null) {
				vo.setTASK_STATUS(inputVO.getStatusList().get(i).get("STATUS").toString());
				vo.setModifier(ws.getUser().getUserID());
				vo.setLastupdate(currentTM);
				dam.update(vo);
			} else {
				throw new APException("ehl_01_common_007");
			}
		}
		this.sendRtnObject(null);
	}

	//新增
	public void saveRecord(Object body, IPrimitiveMap header) throws JBranchException, ParseException {
		
		CRM671InputVO inputVO = (CRM671InputVO) body;
		dam = this.getDataAccessManager();

		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT TO_CHAR(SQ_TBCRM_CUST_VISIT_RECORD.nextval) AS SEQNO FROM DUAL");
		queryCondition.setQueryString(sb.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);

        SimpleDateFormat sdfYmd = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdfHms = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat sdfDms = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		TBCRM_CUST_VISIT_RECORDVO vo = new TBCRM_CUST_VISIT_RECORDVO();
		vo.setVISIT_SEQ(addZeroForNum((String) list.get(0).get("SEQNO"), 10));
		vo.setVISITOR_ROLE((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE));
		vo.setCUST_ID(inputVO.getCust_id());
		vo.setCMU_TYPE(inputVO.getCmu_type());
		vo.setVISIT_MEMO(inputVO.getVisit_memo());
		vo.setVISIT_DT(new Timestamp(sdfDms.parse((sdfYmd.format(inputVO.getVisit_date()) + " " + sdfHms.format(inputVO.getVisit_time()))).getTime()));
		vo.setVISIT_CREPLY(inputVO.getVisit_creply());
		
		dam.create(vo);

		// #0001931_WMS-CR-20240226-03_為提升系統效能擬優化業管系統相關功能_名單訪談記錄
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb = new StringBuffer();
		
		sb.append("INSERT INTO TBCRM_CUST_VISIT_RECORD_NEW ( ");
		sb.append("  VISIT_SEQ, ");
		sb.append("  VISITOR_ROLE, ");
		sb.append("  CUST_ID, ");
		sb.append("  CMU_TYPE, ");
		sb.append("  VISIT_MEMO, ");
		sb.append("  VERSION, ");
		sb.append("  CREATOR, ");
		sb.append("  CREATETIME, ");
		sb.append("  MODIFIER, ");
		sb.append("  LASTUPDATE, ");
		sb.append("  VISIT_DT, ");
		sb.append("  VISIT_CREPLY ");
		sb.append(") ");
		sb.append("VALUES ( ");
		sb.append("  :VISIT_SEQ, ");
		sb.append("  :VISITOR_ROLE, ");
		sb.append("  :CUST_ID, ");
		sb.append("  :CMU_TYPE, ");
		sb.append("  :VISIT_MEMO, ");
		sb.append("  :VERSION, ");
		sb.append("  :CREATOR, ");
		sb.append("  :CREATETIME, ");
		sb.append("  :MODIFIER, ");
		sb.append("  :LASTUPDATE, ");
		sb.append("  :VISIT_DT, ");
		sb.append("  :VISIT_CREPLY ");
		sb.append(") ");
		
		queryCondition.setQueryString(sb.toString());
		queryCondition.setObject("VISIT_SEQ", vo.getVISIT_SEQ());
		queryCondition.setObject("VISITOR_ROLE", vo.getVISITOR_ROLE());
		queryCondition.setObject("CUST_ID", vo.getCUST_ID());
		queryCondition.setObject("CMU_TYPE", vo.getCMU_TYPE());
		queryCondition.setObject("VISIT_MEMO", vo.getVISIT_MEMO());
		queryCondition.setObject("VERSION", vo.getVersion());
		queryCondition.setObject("CREATOR", vo.getCreator());
		queryCondition.setObject("CREATETIME", vo.getCreatetime());
		queryCondition.setObject("MODIFIER", vo.getModifier());
		queryCondition.setObject("LASTUPDATE", vo.getLastupdate());
		queryCondition.setObject("VISIT_DT", vo.getVISIT_DT());
		queryCondition.setObject("VISIT_CREPLY", vo.getVISIT_CREPLY());
		
		dam.exeUpdate(queryCondition);
		
		//2018-12-14 by Jacky WMS-CR-20181025-02_新增客戶聯繫頻率管理報表
		//排除當日連繫客戶
		CRM131 crm131 = (CRM131) PlatformContext.getBean("crm131");
		
		crm131.updateUnderservCust(inputVO.getCust_id());

		this.sendRtnObject(null);
	}

	private String addZeroForNum(String str, int strLength) {
		int strLen = str.length();
		if (strLen < strLength) {
			while (strLen < strLength) {
				StringBuffer sb = new StringBuffer();
				sb.append("0").append(str);// 左補0
				// sb.append(str).append("0");//右補0
				str = sb.toString();
				strLen = str.length();
			}
		}
		return str;
	}

	//刪除
	@SuppressWarnings("unused")
	public void deleteData(Object body, IPrimitiveMap header) throws JBranchException, ParseException {
		CRM671InputVO inputVO = (CRM671InputVO) body;
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		List<String> list = inputVO.getSeqList();

		for (int i = 0; i < list.size(); i++) {
			/** 訪談移除3步驟:查詢、新增、刪除 **/

			//step1: 先查詢TBCRM_CUST_VISIT_RECORD準備刪除的資料====================================	
			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT * FROM TBCRM_CUST_VISIT_RECORD ");
			sql.append(" WHERE VISIT_SEQ =:seq ");
			queryCondition.setObject("seq", list.get(i));
			queryCondition.setQueryString(sql.toString());
			List<Map<String, Object>> querylist = dam.exeQuery(queryCondition);
			//===============================================================================

			//step2: 將查詢的資料新增到TBCRM_CUST_VISIT_RECORD_DLOG================================
			TBCRM_CUST_VISIT_RECORD_DLOGVO vo = new TBCRM_CUST_VISIT_RECORD_DLOGVO();
			vo.setVISIT_SEQ(ObjectUtils.toString(querylist.get(0).get("VISIT_SEQ")));
			vo.setVISITOR_ROLE(ObjectUtils.toString(querylist.get(0).get("VISITOR_ROLE")));
			vo.setCUST_ID(ObjectUtils.toString(querylist.get(0).get("CUST_ID")));
			vo.setCMU_TYPE(ObjectUtils.toString(querylist.get(0).get("CMU_TYPE")));
			vo.setVISIT_MEMO(ObjectUtils.toString(querylist.get(0).get("VISIT_MEMO")));
			vo.setVISIT_DT(new Timestamp(new SimpleDateFormat("yyyy-mm-dd HH:mm:ss").parse(ObjectUtils.toString(querylist.get(0).get("VISIT_DT"))).getTime()));
			vo.setVISIT_CREPLY(ObjectUtils.toString(querylist.get(0).get("VISIT_CREPLY")));
			dam.create(vo);
			//===============================================================================ObjectUtils.toString(querylist.get(0).get("VISIT_DT"))

			//step3: 刪除TBCRM_CUST_VISIT_RECORD的資料==========================================		
			TBCRM_CUST_VISIT_RECORDVO vo2 = (TBCRM_CUST_VISIT_RECORDVO) dam.findByPKey(TBCRM_CUST_VISIT_RECORDVO.TABLE_UID, list.get(i));
			if (vo2 != null) {
				dam.delete(vo2);
			} else {
				throw new APException("ehl_01_common_009");
			}
			//===============================================================================			

		}
		this.sendRtnObject(null);

	}
}