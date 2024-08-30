package com.systex.jbranch.app.server.fps.crm613;

import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * @author Stella
 * @date 2016/05/30
 * 
 */
@Component("crm613")
@Scope("request")
public class CRM613 extends FubonWmsBizLogic {

	private DataAccessManager dam = null;

	/*
	 * 20221208_#1373_麗文_AO Code 變更紀錄排序   By SamTu
	 */
	public void inquire(Object body, IPrimitiveMap header) throws JBranchException {
		CRM613InputVO inputVO = (CRM613InputVO) body;
		CRM613OutputVO outputVO = new CRM613OutputVO();
		dam = this.getDataAccessManager();
		StringBuffer sql = new StringBuffer();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql.append("SELECT ORG_AO_CODE, ORG_AO_NAME, NEW_AO_CODE, NEW_AO_NAME, ");
		sql.append("       REG_AOCODE_EMP_ID, (SELECT EMP_NAME FROM VWORG_BRANCH_EMP_DETAIL_INFO WHERE EMP_ID = REG_AOCODE_EMP_ID ) AS REG_AOCODE_EMP_NAME, ");
		sql.append("       REG_AOCODE_SUB_DATETIME, LETGO_EMP_ID, (SELECT EMP_NAME FROM VWORG_BRANCH_EMP_DETAIL_INFO WHERE EMP_ID = LETGO_EMP_ID ) AS LETGO_EMP_NAME, ");
		sql.append("       LETGO_DATETIME, ");
		sql.append(" CASE WHEN SUBSTR(SEQ,1,2) = 'JS' THEN '1' ELSE '2' END AS RANK ");
		sql.append("FROM TBCRM_CUST_AOCODE_CHGLOG ");
		sql.append("WHERE CUST_ID = :cust_id ");
		sql.append("Order by RANK DESC, SEQ DESC ");
		condition.setQueryString(sql.toString());
		condition.setObject("cust_id", inputVO.getCust_id());

		List list = dam.exeQuery(condition); 
		outputVO.setResultList(list);
		this.sendRtnObject(outputVO);

	}

	public void inquire2(Object body, IPrimitiveMap header) throws JBranchException {
		CRM613InputVO inputVO = (CRM613InputVO) body;
		CRM613OutputVO outputVO = new CRM613OutputVO();
		dam = this.getDataAccessManager();
		StringBuffer sql = new StringBuffer();
		QueryConditionIF condition1 = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql.append("SELECT CHG_TYPE, ");
		sql.append("       CASE WHEN ORG_DEGREE IS NULL THEN 'M' ELSE ORG_DEGREE END AS ORG_DEGREE, ");
		sql.append("       ORG_DEGREE_NAME, NEW_NOTE, ");
		sql.append("       CASE WHEN NEW_DEGREE IS NULL THEN 'M' ELSE NEW_DEGREE END AS NEW_DEGREE, ");
		sql.append("       NEW_DEGREE_NAME, UPGRADE_EXP_NOTE, DUE_DATE, ");
		sql.append("       APPL_EMP_ID, (SELECT DISTINCT EMP_NAME FROM VWORG_BRANCH_EMP_DETAIL_INFO WHERE EMP_ID = APPL_EMP_ID ) AS APPL_EMP_NAME, ");
		sql.append("       APPL_DATE, REVIEW_EMP_ID, (SELECT DISTINCT EMP_NAME FROM VWORG_BRANCH_EMP_DETAIL_INFO WHERE EMP_ID = REVIEW_EMP_ID ) AS REVIEW_EMP_NAME, ");
		sql.append("       REVIEW_DATE ");
		sql.append("FROM TBCRM_CUST_VIP_DEGREE_CHGLOG ");
		sql.append("WHERE CUST_ID  = :cust_id ");
		sql.append("Order by LASTUPDATE DESC ");
		sql.append("FETCH FIRST 24 ROWS ONLY ");
		condition1.setQueryString(sql.toString());
		condition1.setObject("cust_id", inputVO.getCust_id());
		ResultIF list3 = dam.executePaging(condition1, inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
		List<Map<String, Object>> list4 = dam.exeQuery(condition1);
		outputVO.setResultList2(list4);
		int totalPage_i2 = list3.getTotalPage();
		int totalRecord_i2 = list3.getTotalRecord();
		outputVO.setResultList2(list3);
		outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());// 當前頁次
		outputVO.setTotalPage(totalPage_i2);// 總頁次
		outputVO.setTotalRecord(totalRecord_i2);// 總筆數
		this.sendRtnObject(outputVO);

	}

	/**
	 * UHRM異動紀錄
	 * 
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void getUHRMLog(Object body, IPrimitiveMap header) throws JBranchException {

		CRM613InputVO inputVO = (CRM613InputVO) body;
		CRM613OutputVO outputVO = new CRM613OutputVO();
		dam = this.getDataAccessManager();
		StringBuffer sb = new StringBuffer();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		sb.append("SELECT OLD_UEMP_ID, OLD_UEMP_NAME || CASE WHEN OLD_UEMP_AOTYPE = '5' THEN '(計績)' WHEN OLD_UEMP_AOTYPE = '6' THEN '(維護)' ELSE '' END AS OLD_UEMP_NAME, ");
		sb.append("       NEW_UEMP_ID, NEW_UEMP_NAME || CASE WHEN NEW_UEMP_AOTYPE = '5' THEN '(計績)' WHEN NEW_UEMP_AOTYPE = '6' THEN '(維護)' ELSE '' END AS NEW_UEMP_NAME, ");
		sb.append("       CREATOR AS REG_EMP_ID, (SELECT EMP_NAME FROM VWORG_BRANCH_EMP_DETAIL_INFO WHERE EMP_ID = CREATOR) AS REG_EMP_NAME, ");
		sb.append("       CREATETIME AS REG_SUB_DATETIME, NULL AS LETGO_EMP_ID, NULL AS LETGO_EMP_NAME, NULL AS LETGO_DATETIME ");
		sb.append("FROM TBCRM_CUST_UHRM_CHGLOG ");
		sb.append("WHERE CUST_ID = :cust_id ");
		sb.append("Order by SEQNO DESC ");

		queryCondition.setQueryString(sb.toString());
		queryCondition.setObject("cust_id", inputVO.getCust_id());

		outputVO.setUhrmChgLogList(dam.exeQuery(queryCondition));

		this.sendRtnObject(outputVO);
	}

}
