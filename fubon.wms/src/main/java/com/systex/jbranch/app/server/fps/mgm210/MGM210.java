package com.systex.jbranch.app.server.fps.mgm210;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

// import bsh.ParseException;

import com.systex.jbranch.app.common.fps.table.TBMGM_ACTIVITY_MAINVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * MGM210
 * 
 * @author Grace
 * @date 2018/03/01
 */
@Component("mgm210")
@Scope("request")
public class MGM210 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	
	/**
	 * MGM210  取得活動代碼
	 * @param body
	 * @param header
	 * @throws JBranchException
	 * @throws ParseException
	 */
	@SuppressWarnings("unchecked")
	public void getAct(Object body, IPrimitiveMap header) throws JBranchException {
		MGM210OutputVO outputVO = new MGM210OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		qc.setQueryString("SELECT ACT_SEQ, ACT_NAME FROM TBMGM_ACTIVITY_MAIN WHERE DELETE_YN IS NULL ");
		
		outputVO.setOutputList(dam.exeQuery(qc));
		sendRtnObject(outputVO);
	}
	
	/**
	 * MGM210  查詢
	 * @param body
	 * @param header
	 * @throws JBranchException
	 * @throws ParseException
	 */
	@SuppressWarnings("unchecked")
	public void query(Object body, IPrimitiveMap<Object> header) throws JBranchException {
		MGM210InputVO inputVO = (MGM210InputVO) body;
		MGM210OutputVO outputVO = new MGM210OutputVO();
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();

		// 查詢
		sb.append("SELECT A.*, (CASE WHEN A.TEMP_YN = 'Y' THEN '1' ELSE ( CASE ");
		sb.append("WHEN TO_CHAR(A.EFF_DATE, 'YYYY/MM/DD') > TO_CHAR(SYSDATE, 'YYYY/MM/DD') THEN '1' ");
		sb.append("WHEN TO_CHAR(A.EFF_DATE, 'YYYY/MM/DD') <= TO_CHAR(SYSDATE, 'YYYY/MM/DD') AND ");
		sb.append("TO_CHAR(A.DEADLINE, 'YYYY/MM/DD') >= TO_CHAR(SYSDATE, 'YYYY/MM/DD') THEN '2' ");
		sb.append("WHEN TO_CHAR(A.DEADLINE, 'YYYY/MM/DD') < TO_CHAR(SYSDATE, 'YYYY/MM/DD') ");
		sb.append("THEN '3' END ) END ) AS ACT_STATUS FROM ( ");
		sb.append("SELECT ACT.ACT_SEQ, ACT.ACT_NAME, ACT.EFF_DATE, ACT.DEADLINE, ACT.EXC_DEADLINE, ");
		sb.append("ACT.ACT_CONTENT, ACT.ACT_APPROACH, ACT.PRECAUTIONS, ACT.TEMP_YN, ");
		sb.append("ACT.EMP_NAME, NVL(SUM(MGM.APPR_POINTS), 0) AS POINT FROM ( ");
		sb.append("SELECT MAI.*, EMP_NAME FROM TBMGM_ACTIVITY_MAIN MAI ");
		sb.append("LEFT JOIN TBORG_MEMBER ORG ON MAI.CREATOR = ORG.EMP_ID ");
		sb.append("WHERE MAI.DELETE_YN IS NULL ) ACT ");
		sb.append("LEFT JOIN TBMGM_MGM MGM ON ACT.ACT_SEQ = MGM.ACT_SEQ ");
		sb.append("WHERE 1 = 1 ");
		
		// 查詢條件
		if (StringUtils.isNotBlank(inputVO.getActSEQ())) {
			sb.append("AND ACT.ACT_SEQ = :act_seq ");
			qc.setObject("act_seq", inputVO.getActSEQ());
		}
		if (StringUtils.isNotBlank(inputVO.getActStatus())) {
			if(("1").equals(inputVO.getActStatus())){
				sb.append("AND TO_CHAR(ACT.EFF_DATE, 'YYYY/MM/DD') > TO_CHAR(SYSDATE, 'YYYY/MM/DD') ");
				
			}else if(("2").equals(inputVO.getActStatus())){
				sb.append("AND ( TO_CHAR(ACT.EFF_DATE, 'YYYY/MM/DD') <= TO_CHAR(SYSDATE, 'YYYY/MM/DD') ");
				sb.append("AND TO_CHAR(ACT.DEADLINE, 'YYYY/MM/DD') >= TO_CHAR(SYSDATE, 'YYYY/MM/DD') ) ");
				
			}else if(("3").equals(inputVO.getActStatus())){
				sb.append("AND TO_CHAR(ACT.DEADLINE, 'YYYY/MM/DD') < TO_CHAR(SYSDATE, 'YYYY/MM/DD') ");
			}
		}
		
		sb.append("GROUP BY ACT.ACT_SEQ, ACT.ACT_NAME, ACT.EFF_DATE, ACT.DEADLINE, ");
		sb.append("ACT.EXC_DEADLINE, ACT.ACT_CONTENT, ACT.ACT_APPROACH, ACT.PRECAUTIONS, ");
		sb.append("ACT.TEMP_YN, ACT.EMP_NAME ) A ORDER BY A.ACT_SEQ DESC ");
		qc.setQueryString(sb.toString());

		outputVO.setResultList(dam.exeQuery(qc));
		sendRtnObject(outputVO);
	}

	/**
	 * MGM210 刪除
	 * @param body
	 * @param header
	 * @throws JBranchException
	 * @throws ParseException
	 */
	@SuppressWarnings("unchecked")
	public void deleteRow(Object body, IPrimitiveMap<Object> header) throws JBranchException {
		MGM210InputVO inputVO = (MGM210InputVO) body;
		dam = this.getDataAccessManager();
		
		if(inputVO.getActSeq().length > 0){
			for(String act_seq : inputVO.getActSeq()){
				TBMGM_ACTIVITY_MAINVO actVO = new TBMGM_ACTIVITY_MAINVO();
				actVO = (TBMGM_ACTIVITY_MAINVO) dam.findByPKey(TBMGM_ACTIVITY_MAINVO.TABLE_UID, act_seq);
				if(actVO != null){
					// 刪除：將TBMGM_ACTIVITY_MAIN.DELETE_YN壓 "Y"，不要真的delete
					actVO.setDELETE_YN("Y");					
				}
				
				dam.update(actVO);
			}
		}
		sendRtnObject(null);
	}
	
}