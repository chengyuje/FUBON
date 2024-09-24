package com.systex.jbranch.app.server.fps.eln100;

import java.text.SimpleDateFormat;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Component("eln100")
@Scope("request")
public class ELN100 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;

	public void inquire (Object body, IPrimitiveMap header) throws JBranchException {
		ELN100InputVO inputVO = (ELN100InputVO) body;
		ELN100OutputVO outputVO = new ELN100OutputVO();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT S03.SEQ_NO, S01.UPDATE_STATUS, S01.ACCEPTID, S01.POLICY_SIMP_NAME, S01.POLICY_FULL_NAME, ");
		sb.append(" CASE WHEN S03.SEQ_NO IS NULL THEN S01.POLICY_NBR ELSE S03.POLICY_NBR END AS POLICY_NBR, ");
		sb.append(" CASE WHEN S03.SEQ_NO IS NULL THEN S01.CUST_ID ELSE S03.CUST_ID END AS CUST_ID, ");
		sb.append(" CASE WHEN S03.SEQ_NO IS NULL THEN S01.APPL_NAME ELSE S03.APPL_NAME END AS APPL_NAME, ");
		sb.append(" CASE WHEN S03.SEQ_NO IS NULL THEN S01.INS_ID ELSE S03.INS_ID END AS INS_ID, ");
		sb.append(" CASE WHEN S03.SEQ_NO IS NULL THEN S01.INS_NAME ELSE S03.INS_NAME END AS INS_NAME, ");
		sb.append(" CASE WHEN S03.SEQ_NO IS NULL THEN S01.PROPOSER_BIRTH ELSE S03.PROPOSER_BIRTH END AS PROPOSER_BIRTH, ");
		sb.append(" CASE WHEN S03.SEQ_NO IS NULL THEN S01.INSURED_BIRTH ELSE S03.INSURED_BIRTH END AS INSURED_BIRTH, ");
		sb.append(" CASE WHEN S03.SEQ_NO IS NULL THEN S01.PROJECT_CODE ELSE S03.PROJECT_CODE END AS PROJECT_CODE, ");
		sb.append(" CASE WHEN S03.SEQ_NO IS NULL THEN S01.PAY_TYPE ELSE S03.PAY_TYPE END AS PAY_TYPE, ");
		sb.append(" CASE WHEN S03.SEQ_NO IS NULL THEN S01.POLICY_ASSURE_AMT ELSE S03.POLICY_ASSURE_AMT END AS POLICY_ASSURE_AMT, ");
		sb.append(" CASE WHEN S03.SEQ_NO IS NULL THEN S01.UNIT_NBR ELSE S03.UNIT_NBR END AS UNIT_NBR, ");
		sb.append(" CASE WHEN S03.SEQ_NO IS NULL THEN S01.CONTRACT_STATUS ELSE S03.CONTRACT_STATUS END AS CONTRACT_STATUS, ");
		sb.append(" CASE WHEN S03.SEQ_NO IS NULL THEN S01.SERVICE_EMP_ID ELSE S03.SERVICE_EMP_ID END AS SERVICE_EMP_ID, ");
		sb.append(" CASE WHEN S03.SEQ_NO IS NULL THEN S01.DATA_DATE ELSE S03.DATA_DATE END AS DATA_DATE, ");
		sb.append(" CASE WHEN S03.SEQ_NO IS NULL THEN S01.USER_UPDATE_DATE ELSE S03.LASTUPDATE END AS USER_UPDATE_DATE, ");
		sb.append(" S01.INS_COMP_NAME, S01.SEQ, S03.STATUS, S03.UPDATE_REASON ");
		sb.append(" FROM TBJSB_AST_INS_MAST_SG S01 ");
		sb.append(" LEFT JOIN (SELECT * FROM TBJSB_INS_EDIT WHERE STATUS IN ('E', 'P', 'R')) S03 ");
		sb.append(" ON S01.SEQ = S03.SEQ ");
		sb.append(" WHERE S01.ACCEPTID IS NOT NULL ");
		
		if (inputVO.getReq_time_s() != null) {
			String sDate = sdf.format(inputVO.getReq_time_s());
			sb.append(" AND TO_CHAR(NVL(S03.LASTUPDATE, S01.USER_UPDATE_DATE), 'YYYYMMDD') >= :sDate ");
			qc.setObject("sDate", sDate);
		}
		
		if (inputVO.getReq_time_e() != null) {
			String eDate = sdf.format(inputVO.getReq_time_e());
			sb.append(" AND TO_CHAR(NVL(S03.LASTUPDATE, S01.USER_UPDATE_DATE), 'YYYYMMDD') <= :eDate ");
			qc.setObject("eDate", eDate);
		}
		
		sb.append(" ORDER BY S01.ACCEPTID, S01.POLICY_NBR, S01.POLICY_FULL_NAME ");
		
		qc.setQueryString(sb.toString());
		outputVO.setResultList(dam.exeQuery(qc));
		this.sendRtnObject(outputVO);
	}
	
}