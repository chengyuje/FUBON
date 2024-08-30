package com.systex.jbranch.app.server.fps.prd151;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBPRD_FCIVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Component("prd151")
@Scope("request")
public class PRD151  extends FubonWmsBizLogic {
	private DataAccessManager dam = null;	
	private Logger logger = LoggerFactory.getLogger(PRD151.class);
	
	/**查詢 **/
	public void inquire(Object body, IPrimitiveMap header) throws JBranchException {
		PRD151InputVO inputVO = (PRD151InputVO) body;
		PRD151OutputVO outputVO = new PRD151OutputVO();
		
		dam = this.getDataAccessManager();		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		sql.append("SELECT A.*, TO_CHAR(B.LASTUPDATE, 'YYYY/MM/DD') AS DR_LASTUPDATE, C.PARAM_NAME AS PRICE_REMARK, ");
		sql.append(" CEIL(B.MON_RATE_1 * (1 + A.REF_PRICE_Y) * 100)/100 AS PRD_PROFEE_RATE_1, ");
		sql.append(" CEIL(B.MON_RATE_2 * (1 + A.REF_PRICE_Y) * 100)/100 AS PRD_PROFEE_RATE_2, ");
		sql.append(" CEIL(B.MON_RATE_3 * (1 + A.REF_PRICE_Y) * 100)/100 AS PRD_PROFEE_RATE_3, ");
		sql.append(" CEIL(B.MON_RATE_4 * (1 + A.REF_PRICE_Y) * 100)/100 AS PRD_PROFEE_RATE_4, ");
		sql.append(" CEIL(B.MON_RATE_5 * (1 + A.REF_PRICE_Y) * 100)/100 AS PRD_PROFEE_RATE_5, ");
		sql.append(" CEIL(B.MON_RATE_6 * (1 + A.REF_PRICE_Y) * 100)/100 AS PRD_PROFEE_RATE_6, ");
		sql.append(" CEIL(B.MON_RATE_7 * (1 + A.REF_PRICE_Y) * 100)/100 AS PRD_PROFEE_RATE_7, ");
		sql.append(" CEIL(B.MON_RATE_8 * (1 + A.REF_PRICE_Y) * 100)/100 AS PRD_PROFEE_RATE_8, ");
		sql.append(" CEIL(B.MON_RATE_9 * (1 + A.REF_PRICE_Y) * 100)/100 AS PRD_PROFEE_RATE_9, ");
		sql.append(" CEIL(B.MON_RATE_10 * (1 + A.REF_PRICE_Y) * 100)/100 AS PRD_PROFEE_RATE_10, ");
		sql.append(" CEIL(B.MON_RATE_11 * (1 + A.REF_PRICE_Y) * 100)/100 AS PRD_PROFEE_RATE_11, ");
		sql.append(" CEIL(B.MON_RATE_12 * (1 + A.REF_PRICE_Y) * 100)/100 AS PRD_PROFEE_RATE_12, ");
		sql.append(" CEIL(B.MON_RATE_1 * TO_NUMBER(NVL(F.PARAM_NAME, '1')) * 100)/100 AS MIN_PROFEE_RATE_1, ");
		sql.append(" CEIL(B.MON_RATE_2 * TO_NUMBER(NVL(F.PARAM_NAME, '1')) * 100)/100 AS MIN_PROFEE_RATE_2, ");
		sql.append(" CEIL(B.MON_RATE_3 * TO_NUMBER(NVL(F.PARAM_NAME, '1')) * 100)/100 AS MIN_PROFEE_RATE_3, ");
		sql.append(" CEIL(B.MON_RATE_4 * TO_NUMBER(NVL(F.PARAM_NAME, '1')) * 100)/100 AS MIN_PROFEE_RATE_4, ");
		sql.append(" CEIL(B.MON_RATE_5 * TO_NUMBER(NVL(F.PARAM_NAME, '1')) * 100)/100 AS MIN_PROFEE_RATE_5, ");
		sql.append(" CEIL(B.MON_RATE_6 * TO_NUMBER(NVL(F.PARAM_NAME, '1')) * 100)/100 AS MIN_PROFEE_RATE_6, ");
		sql.append(" CEIL(B.MON_RATE_7 * TO_NUMBER(NVL(F.PARAM_NAME, '1')) * 100)/100 AS MIN_PROFEE_RATE_7, ");
		sql.append(" CEIL(B.MON_RATE_8 * TO_NUMBER(NVL(F.PARAM_NAME, '1')) * 100)/100 AS MIN_PROFEE_RATE_8, ");
		sql.append(" CEIL(B.MON_RATE_9 * TO_NUMBER(NVL(F.PARAM_NAME, '1')) * 100)/100 AS MIN_PROFEE_RATE_9, ");
		sql.append(" CEIL(B.MON_RATE_10 * TO_NUMBER(NVL(F.PARAM_NAME, '1')) * 100)/100 AS MIN_PROFEE_RATE_10, ");
		sql.append(" CEIL(B.MON_RATE_11 * TO_NUMBER(NVL(F.PARAM_NAME, '1')) * 100)/100 AS MIN_PROFEE_RATE_11, ");
		sql.append(" CEIL(B.MON_RATE_12 * TO_NUMBER(NVL(F.PARAM_NAME, '1')) * 100)/100 AS MIN_PROFEE_RATE_12, ");
		sql.append(" E.PARAM_DESC AS TARGET_CNAME ");
		sql.append(" FROM TBPRD_FCI A ");
		sql.append(" INNER JOIN TBPRD_FCI_DEPOSIT_RATE B ON B.CURR_ID = A.CURR_ID ");
		sql.append(" LEFT JOIN TBSYSPARAMETER C ON C.PARAM_TYPE = 'PRD.FCI_PRICE_REMARK' AND C.PARAM_CODE = '1' ");
		sql.append(" LEFT JOIN TBSYSPARAMETER D ON D.PARAM_TYPE = 'PRD.FCI_CURRENCY' AND D.PARAM_CODE = A.CURR_ID ");
		sql.append(" LEFT JOIN TBSYSPARAMETER E ON E.PARAM_TYPE = 'PRD.FCI_CURRENCY_PAIR' AND E.PARAM_CODE = A.CURR_ID AND E.PARAM_NAME = A.TARGET_CURR_ID ");
		sql.append(" LEFT JOIN TBSYSPARAMETER F ON F.PARAM_TYPE = 'PRD.FCI_MIN_PROFEE_RATE' AND F.PARAM_CODE = '1' ");
		sql.append(" WHERE A.EFFECTIVE_YN = 'Y' ");
		sql.append("   AND EXISTS (SELECT 1 FROM TBPRD_FCI_DEPOSIT_RATE WHERE CURR_ID = A.CURR_ID AND TRUNC(CREATETIME) = TRUNC(SYSDATE)) ");
		sql.append("   AND EXISTS (SELECT 1 FROM TBPRD_FCI_FTP_RATE WHERE CURR_ID = A.CURR_ID AND TRUNC(CREATETIME) = TRUNC(SYSDATE)) ");
		sql.append("   AND EXISTS (SELECT 1 FROM TBPRD_FCI_EXPIRE_DATE WHERE CURR_ID = A.CURR_ID AND TRUNC(CREATETIME) = TRUNC(SYSDATE)) ");
		sql.append("   AND EXISTS (SELECT 1 FROM TBPRD_FCI_SPOT_DATE WHERE CURR_ID = A.CURR_ID AND TRUNC(CREATETIME) = TRUNC(SYSDATE)) ");
		
		if(StringUtils.isNotBlank(inputVO.getKycLV())) {
			sql.append(" AND TO_NUMBER(SUBSTR(A.RISKCATE_ID, 2, 1)) <= TO_NUMBER(SUBSTR(:kycLV, 2, 1)) ");
			queryCondition.setObject("kycLV", inputVO.getKycLV());
		}
		sql.append(" ORDER BY A.CURR_ID ");		
		queryCondition.setQueryString(sql.toString());
		outputVO.setResultList(dam.exeQuery(queryCondition));
		
		this.sendRtnObject(outputVO);
	}
	
	/**查詢牌告利率 **/
	public void inquireDepRate(Object body, IPrimitiveMap header) throws JBranchException {
		PRD151InputVO inputVO = (PRD151InputVO) body;
		PRD151OutputVO outputVO = new PRD151OutputVO();
		
		dam = this.getDataAccessManager();		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		sql.append("SELECT CURR_ID, ");
		sql.append(" MON_RATE_1, ");
		sql.append(" MON_RATE_2, ");
		sql.append(" MON_RATE_3, ");
		sql.append(" MON_RATE_4, ");
		sql.append(" MON_RATE_5, ");
		sql.append(" MON_RATE_6, ");
		sql.append(" MON_RATE_7, ");
		sql.append(" MON_RATE_8, ");
		sql.append(" MON_RATE_9, ");
		sql.append(" MON_RATE_10, ");
		sql.append(" MON_RATE_11, ");
		sql.append(" MON_RATE_12 ");
		sql.append(" FROM TBPRD_FCI_DEPOSIT_RATE ");
		sql.append(" ORDER BY CURR_ID ");		
		queryCondition.setQueryString(sql.toString());
		outputVO.setResultList(dam.exeQuery(queryCondition));
		
		this.sendRtnObject(outputVO);
	}
	
	/***
	 * 取得理專收益率
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void getRMProfee(Object body, IPrimitiveMap header) throws JBranchException {
		PRD151InputVO inputVO = (PRD151InputVO) body;
		PRD151OutputVO outputVO = new PRD151OutputVO();
		outputVO.setC_RM_PROFEE(BigDecimal.ZERO);
		outputVO.setMINUF_MON(BigDecimal.ZERO);
		
		dam = this.getDataAccessManager();		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		sql.append("SELECT TRUNC(((A.MON_RATE_"+inputVO.getMonType()+" - :prdProfee - 0.01) * ");
		sql.append(" ((TRUNC(B.MON_DATE_"+inputVO.getMonType()+") - TRUNC(B.VALUE_DATE))/360) - C.TRADER_CHARGE), 2) AS RM_PROFEE, ");
		sql.append(" (C.MIN_UF * :monType) AS MINUF_MON ");
		sql.append(" FROM TBPRD_FCI_FTP_RATE A ");
		sql.append(" INNER JOIN TBPRD_FCI_EXPIRE_DATE B ON B.CURR_ID = A.CURR_ID ");
		sql.append(" INNER JOIN TBPRD_FCI C ON C.CURR_ID = A.CURR_ID AND C.EFFECTIVE_YN = 'Y' ");
		sql.append(" WHERE A.CURR_ID = :currId ");
		queryCondition.setObject("currId", inputVO.getC_CURR_ID());
		queryCondition.setObject("prdProfee", inputVO.getC_PRD_PROFEE());
		queryCondition.setObject("monType", inputVO.getMonType());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		
		if(CollectionUtils.isNotEmpty(list)) {
			outputVO.setC_RM_PROFEE((BigDecimal)(list.get(0).get("RM_PROFEE")));
			outputVO.setMINUF_MON((BigDecimal)(list.get(0).get("MINUF_MON")));
		}		
		this.sendRtnObject(outputVO);
	}
	
}
