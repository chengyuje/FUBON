package com.systex.jbranch.app.server.fps.crm160;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.server.fps.crm160.CRM160InputVO;
import com.systex.jbranch.app.server.fps.crm160.CRM160OutputVO;
import com.systex.jbranch.app.server.fps.crm160.CRM160;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;



/**
 * @author Stella
 * @date 2016/08/22
 * 
 */
@Component("crm160")
@Scope("request")
public class CRM160 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(CRM160.class);
	public void login(Object body, IPrimitiveMap header) throws JBranchException {
		CRM160OutputVO return_VO = new CRM160OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		//1-共同無、 0-無 
		sql.append("SELECT CASE WHEN PRIVILEGEID IN ('038', '039', '040', '053', '054') THEN 0 ");
		sql.append("            WHEN PRIVILEGEID IN ('004', '012', '013', '041', '042', '043', '044', '045', '046', '071', '072') THEN 1  ");
		sql.append("            WHEN PRIVILEGEID IN ('035', '036') THEN 2  ");
		sql.append("            WHEN PRIVILEGEID IN ('033', '034') THEN 3 ");
		sql.append("            WHEN PRIVILEGEID = '037' THEN 4 ");
		sql.append("            WHEN PRIVILEGEID IN ('047', '048', '049', '050', '051', '052') THEN 5 ");
		sql.append("            WHEN PRIVILEGEID IN ('002', '003') THEN 7 "); // 2017/2/8 add 理專
		sql.append("       ELSE 6 END AS COUNTS ");
		sql.append("FROM TBSYSSECUROLPRIASS ");
		sql.append("WHERE ROLEID = :roleID ");
		queryCondition.setQueryString(sql.toString());
		queryCondition.setObject("roleID", getUserVariable(FubonSystemVariableConsts.LOGINROLE));
			
		return_VO.setPrivilege(dam.exeQuery(queryCondition));
		sendRtnObject(return_VO);
	}
	
	public void queryData(Object body, IPrimitiveMap header) throws JBranchException {
		CRM160InputVO inputVO = new CRM160InputVO();
		CRM160OutputVO outputVO = new CRM160OutputVO();
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		
		sql.append("WITH BASE AS ( "
				+ " SELECT CASE WHEN TR.JOB_TITLE_NAME IS NULL THEN '999' ELSE (SELECT TS.PRIVILEGEID FROM TBSYSSECUROLPRIASS TS WHERE TS.ROLEID =  :roleID )  END AS ROLE_ID  "
				+ " FROM TBORG_ROLE TR WHERE  TR.ROLE_ID = :roleID ) ");
		
		sql.append("SELECT  SEQ, BTYPE, SUBJECT, ROLE, PTYPE, S_DATE, E_DATE, ORGN, CONTACT, ATTACHMENT, PICTURE, CONTENT, CREATETIME, CREATOR, MODIFIER, LASTUPDATE, TYPE "
				+ " FROM ( ");
    	//行銷活動 
		sql.append("SELECT  SEQ, BTYPE, SUBJECT, ROLE, PTYPE, S_DATE, E_DATE, ORGN, CONTACT, ATTACHMENT, PICTURE, CONTENT, CREATETIME, CREATOR, MODIFIER, LASTUPDATE, NULL AS TYPE "
				+ " FROM TBMKT_BULLETIN_BOARD "
				+ " WHERE (ROLE <> 'ALL' AND BTYPE = '01' "
				+ "       AND TO_CHAR(SYSDATE,'YYYYMMDD') >=  TO_CHAR (S_DATE, 'YYYYMMDD')  "
				+ "       AND TO_CHAR (E_DATE, 'YYYYMMDD')>= TO_CHAR(SYSDATE,'YYYYMMDD')  "
				+ "       AND INSTR(ROLE, (SELECT ROLE_ID FROM BASE) )>0 ) "
				+ " OR (ROLE = 'ALL' AND BTYPE = '01' "
				+ "      AND TO_CHAR(SYSDATE,'YYYYMMDD') >=  TO_CHAR (S_DATE, 'YYYYMMDD')  "
				+ "      AND TO_CHAR (E_DATE, 'YYYYMMDD')>= TO_CHAR(SYSDATE,'YYYYMMDD') "
				+ "      AND INSTR(ROLE,'ALL')>0 )  "
				+ " AND ((REVIEW_STATUS = 'Y' AND ACT_TYPE in ('A', 'M')) OR (ACT_TYPE = 'D' AND REVIEW_STATUS <> 'Y')) "
				+ " AND ROWNUM <='10' "
				+ " UNION ALL ");
		//產品訊息 
		sql.append(" SELECT  SEQ, BTYPE, SUBJECT, ROLE, PTYPE, S_DATE, E_DATE, ORGN, CONTACT, ATTACHMENT, PICTURE, CONTENT, CREATETIME, CREATOR, MODIFIER, LASTUPDATE, NULL AS TYPE "
				+ " FROM TBMKT_BULLETIN_BOARD  "
				+ " WHERE (ROLE <> 'ALL' "
				+ "        AND BTYPE = '02' "
				+ "        AND TO_CHAR(SYSDATE,'YYYYMMDD') >=  TO_CHAR (S_DATE, 'YYYYMMDD') "
				+ "        AND TO_CHAR (E_DATE, 'YYYYMMDD')>= TO_CHAR(SYSDATE,'YYYYMMDD') "
				+ "        AND INSTR(ROLE, (SELECT ROLE_ID FROM BASE) )>0 ) "
				+ " OR (ROLE = 'ALL' "
				+ "       AND BTYPE = '02' "
				+ "       AND TO_CHAR(SYSDATE,'YYYYMMDD') >=  TO_CHAR (S_DATE, 'YYYYMMDD') "
				+ "       AND TO_CHAR (E_DATE, 'YYYYMMDD')>= TO_CHAR(SYSDATE,'YYYYMMDD') "
				+ "       AND INSTR(ROLE,'ALL')>0 )  "
				+ " AND ((REVIEW_STATUS = 'Y' AND ACT_TYPE in ('A', 'M')) OR (ACT_TYPE = 'D' AND REVIEW_STATUS <> 'Y')) "
				+ " AND ROWNUM <='10' ");
	    //研究報告
		sql.append(" UNION ALL "
				+ " SELECT  SEQ, BTYPE, SUBJECT, ROLE, PTYPE, S_DATE, E_DATE, ORGN, CONTACT, ATTACHMENT, PICTURE, CONTENT, CREATETIME, CREATOR, MODIFIER, LASTUPDATE, 1 AS TYPE "
				+ " FROM TBMKT_BULLETIN_BOARD  "
				+ " WHERE (ROLE <> 'ALL' "
				+ "        AND BTYPE = '03'  "
				+ "        AND TO_CHAR (S_DATE, 'YYYYMMDD') = TO_CHAR(SYSDATE,'YYYYMMDD') "
				+ "        AND INSTR(ROLE, (SELECT ROLE_ID FROM BASE) )>0 ) "
				+ "OR (ROLE = 'ALL' "
				+ "    AND BTYPE = '03'  "
				+ "    AND TO_CHAR (S_DATE, 'YYYYMMDD') = TO_CHAR(SYSDATE,'YYYYMMDD') "
				+ "    AND INSTR(ROLE,'ALL')>0 ) "
				+ " AND ((REVIEW_STATUS = 'Y' AND ACT_TYPE in ('A', 'M')) OR (ACT_TYPE = 'D' AND REVIEW_STATUS <> 'Y')) "
				+ " AND ROWNUM <='5' "
	            +" UNION ALL "
				+ " SELECT  SEQ, BTYPE, SUBJECT, ROLE, PTYPE, S_DATE, E_DATE, ORGN, CONTACT, ATTACHMENT, PICTURE, CONTENT, CREATETIME, CREATOR, MODIFIER, LASTUPDATE, 2 AS TYPE "
				+ " FROM TBMKT_BULLETIN_BOARD  "
				+ " WHERE  (ROLE <> 'ALL' "
				+ "         AND  BTYPE = '03' "
				+ "         AND  TO_CHAR(SYSDATE,'YYYY/MM/DD') BETWEEN TO_CHAR(S_DATE,'YYYY/MM/DD') AND TO_CHAR(S_DATE+7,'YYYY/MM/DD')   "
				+ "         AND INSTR(ROLE, (SELECT ROLE_ID FROM BASE) )>0 ) "
				+ " OR (ROLE = 'ALL' "
				+ "     AND  BTYPE = '03' "
				+ "     AND TO_CHAR(SYSDATE,'YYYY/MM/DD') BETWEEN  TO_CHAR(S_DATE,'YYYY/MM/DD') AND TO_CHAR(S_DATE+7,'YYYY/MM/DD')   "
				+ "     AND INSTR(ROLE,'ALL')>0 )   "
				+ " AND ((REVIEW_STATUS = 'Y' AND ACT_TYPE in ('A', 'M')) OR (ACT_TYPE = 'D' AND REVIEW_STATUS <> 'Y')) "
				+ " AND ROWNUM <='5' "
				+ " UNION ALL "
				+ " SELECT  SEQ, BTYPE, SUBJECT, ROLE, PTYPE, S_DATE, E_DATE, ORGN, CONTACT, ATTACHMENT, PICTURE, CONTENT, CREATETIME, CREATOR, MODIFIER, LASTUPDATE, 3 AS TYPE "
				+ " FROM TBMKT_BULLETIN_BOARD  "
				+ " WHERE  (ROLE <> 'ALL' "
				+ "         AND BTYPE = '03'  "
				+ "         AND TO_CHAR(S_DATE,'YYYY/MM') = TO_CHAR(SYSDATE,'YYYY/MM') "
				+ "         AND INSTR(ROLE, (SELECT ROLE_ID FROM BASE) )>0 ) "
				+ " OR (ROLE = 'ALL' "
				+ "     AND BTYPE = '03' "
				+ "     AND TO_CHAR(S_DATE,'YYYY/MM') = TO_CHAR(SYSDATE,'YYYY/MM') "
				+ "     AND INSTR(ROLE,'ALL')>0 )  "
				+ " AND ((REVIEW_STATUS = 'Y' AND ACT_TYPE in ('A', 'M')) OR (ACT_TYPE = 'D' AND REVIEW_STATUS <> 'Y')) "
				+ " AND ROWNUM <='5' ");
		//內部公告-4
		sql.append(" UNION ALL  "
		 		+ " SELECT  SEQ, BTYPE, SUBJECT, ROLE, PTYPE, S_DATE, E_DATE, ORGN, CONTACT, ATTACHMENT, PICTURE, CONTENT, CREATETIME, CREATOR, MODIFIER, LASTUPDATE, NULL AS TYPE "
				+ " FROM TBMKT_BULLETIN_BOARD  "
				+ " WHERE (ROLE <> 'ALL' "
				+ "        AND BTYPE = '04' "
				+ "        AND TO_CHAR(SYSDATE,'YYYYMMDD') >=  TO_CHAR (S_DATE, 'YYYYMMDD') "
				+ "        AND TO_CHAR (E_DATE, 'YYYYMMDD')>= TO_CHAR(SYSDATE,'YYYYMMDD') "
				+ "        AND INSTR(ROLE, (SELECT ROLE_ID FROM BASE) )>0 ) "
				+ " OR (ROLE = 'ALL' "
				+ "     AND BTYPE = '04' "
				+ "     AND TO_CHAR(SYSDATE,'YYYYMMDD') >=  TO_CHAR (S_DATE, 'YYYYMMDD') "
				+ "     AND TO_CHAR (E_DATE, 'YYYYMMDD')>= TO_CHAR(SYSDATE,'YYYYMMDD') "
				+ "     AND INSTR(ROLE,'ALL')>0 )  "
				+ " AND ((REVIEW_STATUS = 'Y' AND ACT_TYPE in ('A', 'M')) OR (ACT_TYPE = 'D' AND REVIEW_STATUS <> 'Y')) "
				+ " AND ROWNUM <='10' ");
		//本月主推 -基金(FUND)
		sql.append(" UNION ALL "
				+ " SELECT  SEQ, BTYPE, SUBJECT, ROLE, PTYPE, S_DATE, E_DATE, ORGN, CONTACT, ATTACHMENT, PICTURE, CONTENT, CREATETIME, CREATOR, MODIFIER, LASTUPDATE, NULL AS TYPE "
				+ " FROM TBMKT_BULLETIN_BOARD  "
				+ " WHERE (ROLE <> 'ALL' "
				+ "        AND BTYPE = '05' "
				+ "        AND PTYPE = 'FUND' "
				+ "        AND TO_CHAR(SYSDATE,'YYYYMMDD') >=  TO_CHAR (S_DATE, 'YYYYMMDD') "
				+ "        AND TO_CHAR (E_DATE, 'YYYYMMDD')>= TO_CHAR(SYSDATE,'YYYYMMDD') "
				+ "        AND INSTR(ROLE, (SELECT ROLE_ID FROM BASE) )>0 ) "
				+ " OR (ROLE = 'ALL' "
				+ "     AND BTYPE = '05'  "
				+ "     AND PTYPE = 'FUND' "
				+ "     AND TO_CHAR(SYSDATE,'YYYYMMDD') >=  TO_CHAR (S_DATE, 'YYYYMMDD') "
				+ "     AND TO_CHAR (E_DATE, 'YYYYMMDD')>= TO_CHAR(SYSDATE,'YYYYMMDD') "
				+ "     AND INSTR(ROLE,'ALL')>0 ) "
				+ " AND ((REVIEW_STATUS = 'Y' AND ACT_TYPE in ('A', 'M')) OR (ACT_TYPE = 'D' AND REVIEW_STATUS <> 'Y')) "
				+ " AND ROWNUM <='10' ");
		//本月主推-ETF
		sql.append(" UNION ALL "
				+ "  SELECT  SEQ, BTYPE, SUBJECT, ROLE, PTYPE, S_DATE, E_DATE, ORGN, CONTACT, ATTACHMENT, PICTURE, CONTENT, CREATETIME, CREATOR, MODIFIER, LASTUPDATE, NULL AS TYPE "
				+ "  FROM TBMKT_BULLETIN_BOARD  "
				+ "  WHERE (ROLE <> 'ALL' "
				+ "        AND BTYPE = '05'  "
				+ "        AND PTYPE = 'ETF' "
				+ "        AND TO_CHAR(SYSDATE,'YYYYMMDD') >=  TO_CHAR (S_DATE, 'YYYYMMDD') "
				+ "        AND TO_CHAR (E_DATE, 'YYYYMMDD')>= TO_CHAR(SYSDATE,'YYYYMMDD')  "
				+ "        AND INSTR(ROLE, (SELECT ROLE_ID FROM BASE) )>0 ) "
				+ " OR (ROLE = 'ALL' "
				+ "     AND BTYPE = '05' "
				+ "     AND PTYPE = 'ETF' "
				+ "     AND TO_CHAR(SYSDATE,'YYYYMMDD') >=  TO_CHAR (S_DATE, 'YYYYMMDD') "
				+ "     AND TO_CHAR (E_DATE, 'YYYYMMDD')>= TO_CHAR(SYSDATE,'YYYYMMDD')  "
				+ "     AND INSTR(ROLE,'ALL')>0 )  "
				+ " AND ((REVIEW_STATUS = 'Y' AND ACT_TYPE in ('A', 'M')) OR (ACT_TYPE = 'D' AND REVIEW_STATUS <> 'Y')) "
				+ " AND ROWNUM <='10'  ");
		//本月主推-股票(STOCK)
		sql.append(" UNION ALL "
				+ " SELECT  SEQ, BTYPE, SUBJECT, ROLE, PTYPE, S_DATE, E_DATE, ORGN, CONTACT, ATTACHMENT, PICTURE, CONTENT, CREATETIME, CREATOR, MODIFIER, LASTUPDATE, NULL AS TYPE "
				+ " FROM TBMKT_BULLETIN_BOARD "
				+ " WHERE (ROLE <> 'ALL' "
				+ "      AND BTYPE = '05' "
				+ "      AND PTYPE = 'STOCK' "
				+ "      AND TO_CHAR(SYSDATE,'YYYYMMDD') >=  TO_CHAR (S_DATE, 'YYYYMMDD') "
				+ "      AND TO_CHAR (E_DATE, 'YYYYMMDD')>= TO_CHAR(SYSDATE,'YYYYMMDD')  "
				+ "      AND INSTR(ROLE, (SELECT ROLE_ID FROM BASE) )>0 ) "
				+ " OR (ROLE = 'ALL' "
				+ "     AND BTYPE = '05' "
				+ "     AND PTYPE = 'STOCK' "
				+ "     AND TO_CHAR(SYSDATE,'YYYYMMDD') >=  TO_CHAR (S_DATE, 'YYYYMMDD') "
				+ "     AND TO_CHAR (E_DATE, 'YYYYMMDD')>= TO_CHAR(SYSDATE,'YYYYMMDD')  "
				+ "     AND INSTR(ROLE,'ALL')>0 )  "
				+ " AND ((REVIEW_STATUS = 'Y' AND ACT_TYPE in ('A', 'M')) OR (ACT_TYPE = 'D' AND REVIEW_STATUS <> 'Y')) "
				+ " AND ROWNUM <='10' ");
		//本月主推-海外債(BOND)
		sql.append("UNION ALL "
				+ " SELECT  SEQ, BTYPE, SUBJECT, ROLE, PTYPE, S_DATE, E_DATE, ORGN, CONTACT, ATTACHMENT, PICTURE, CONTENT, CREATETIME, CREATOR, MODIFIER, LASTUPDATE, NULL AS TYPE  "
				+ " FROM TBMKT_BULLETIN_BOARD  "
				+ " WHERE (ROLE <> 'ALL' "
				+ "        AND BTYPE = '05' "
				+ "        AND PTYPE = 'BOND' "
				+ "        AND TO_CHAR(SYSDATE,'YYYYMMDD') >=  TO_CHAR (S_DATE, 'YYYYMMDD') "
				+ "        AND TO_CHAR (E_DATE, 'YYYYMMDD')>= TO_CHAR(SYSDATE,'YYYYMMDD') "
				+ "        AND INSTR(ROLE, (SELECT ROLE_ID FROM BASE) )>0 ) "
				+ " OR (ROLE = 'ALL' "
				+ "   AND PTYPE = 'BOND' "
				+ "   AND BTYPE = '05' "
				+ "   AND TO_CHAR(SYSDATE,'YYYYMMDD') >=  TO_CHAR (S_DATE, 'YYYYMMDD') "
				+ "   AND TO_CHAR (E_DATE, 'YYYYMMDD')>= TO_CHAR(SYSDATE,'YYYYMMDD') "
				+ "   AND INSTR(ROLE,'ALL')>0 )   "
				+ " AND ((REVIEW_STATUS = 'Y' AND ACT_TYPE in ('A', 'M')) OR (ACT_TYPE = 'D' AND REVIEW_STATUS <> 'Y')) "
				+ " AND ROWNUM <='10' ");
		//本月主推-SI
		sql.append("UNION ALL "
				+ " SELECT  SEQ, BTYPE, SUBJECT, ROLE, PTYPE, S_DATE, E_DATE, ORGN, CONTACT, ATTACHMENT, PICTURE, CONTENT, CREATETIME, CREATOR, MODIFIER, LASTUPDATE, NULL AS TYPE "
				+ " FROM TBMKT_BULLETIN_BOARD "
				+ " WHERE (ROLE <> 'ALL' "
				+ "      AND BTYPE = '05'  "
				+ "      AND PTYPE = 'SI' "
				+ "      AND TO_CHAR(SYSDATE,'YYYYMMDD') >=  TO_CHAR (S_DATE, 'YYYYMMDD') "
				+ "      AND TO_CHAR (E_DATE, 'YYYYMMDD')>= TO_CHAR(SYSDATE,'YYYYMMDD') "
				+ "      AND INSTR(ROLE, (SELECT ROLE_ID FROM BASE) )>0 ) "
				+ " OR (ROLE = 'ALL'  "
				+ "     AND PTYPE = 'SI' "
				+ "     AND BTYPE = '05' "
				+ "     AND TO_CHAR(SYSDATE,'YYYYMMDD') >=  TO_CHAR (S_DATE, 'YYYYMMDD') "
				+ "     AND TO_CHAR (E_DATE, 'YYYYMMDD')>= TO_CHAR(SYSDATE,'YYYYMMDD') "
				+ "     AND INSTR(ROLE,'ALL')>0 )  "
				+ " AND ((REVIEW_STATUS = 'Y' AND ACT_TYPE in ('A', 'M')) OR (ACT_TYPE = 'D' AND REVIEW_STATUS <> 'Y')) "
				+ " AND ROWNUM <='10' ");
		//本月主推-SN
		sql.append(" UNION ALL "
				+ " SELECT  SEQ, BTYPE, SUBJECT, ROLE, PTYPE, S_DATE, E_DATE, ORGN, CONTACT, ATTACHMENT, PICTURE, CONTENT, CREATETIME, CREATOR, MODIFIER, LASTUPDATE, NULL AS TYPE "
				+ " FROM TBMKT_BULLETIN_BOARD "
				+ " WHERE (ROLE <> 'ALL' "
				+ "       AND BTYPE = '05' "
				+ "       AND PTYPE = 'SN' "
				+ "       AND TO_CHAR(SYSDATE,'YYYYMMDD') >=  TO_CHAR (S_DATE, 'YYYYMMDD') "
				+ "       AND TO_CHAR (E_DATE, 'YYYYMMDD')>= TO_CHAR(SYSDATE,'YYYYMMDD')  "
				+ "       AND INSTR(ROLE, (SELECT ROLE_ID FROM BASE) )>0 ) "
				+ " OR (ROLE = 'ALL' "
				+ "   AND PTYPE = 'SN' "
				+ "   AND BTYPE = '05' "
				+ "   AND TO_CHAR(SYSDATE,'YYYYMMDD') >=  TO_CHAR (S_DATE, 'YYYYMMDD') "
				+ "   AND TO_CHAR (E_DATE, 'YYYYMMDD')>= TO_CHAR(SYSDATE,'YYYYMMDD')  "
				+ "   AND INSTR(ROLE,'ALL')>0 ) "
				+ " AND ((REVIEW_STATUS = 'Y' AND ACT_TYPE in ('A', 'M')) OR (ACT_TYPE = 'D' AND REVIEW_STATUS <> 'Y')) "
				+ " AND ROWNUM <='10' ");
		//本月主推-INS
		sql.append(" UNION ALL "
				+ "  SELECT  SEQ, BTYPE, SUBJECT, ROLE, PTYPE, S_DATE, E_DATE, ORGN, CONTACT, ATTACHMENT, PICTURE, CONTENT, CREATETIME, CREATOR, MODIFIER, LASTUPDATE, NULL AS TYPE "
				+ "  FROM TBMKT_BULLETIN_BOARD "
				+ "  WHERE (ROLE <> 'ALL' "
				+ "        AND BTYPE = '05' "
				+ "        AND PTYPE = 'INS' "
				+ "        AND TO_CHAR(SYSDATE,'YYYYMMDD') >=  TO_CHAR (S_DATE, 'YYYYMMDD') "
				+ "        AND TO_CHAR (E_DATE, 'YYYYMMDD')>= TO_CHAR(SYSDATE,'YYYYMMDD')   "
				+ "        AND INSTR(ROLE, (SELECT ROLE_ID FROM BASE) )>0 ) "
				+ "  OR (ROLE = 'ALL' "
				+ "     AND BTYPE = '05' "
				+ "     AND PTYPE = 'INS' "
				+ "     AND TO_CHAR(SYSDATE,'YYYYMMDD') >=  TO_CHAR (S_DATE, 'YYYYMMDD') "
				+ "     AND TO_CHAR (E_DATE, 'YYYYMMDD')>= TO_CHAR(SYSDATE,'YYYYMMDD')   "
				+ "     AND INSTR(ROLE,'ALL')>0 ) "
				+ " AND ((REVIEW_STATUS = 'Y' AND ACT_TYPE in ('A', 'M')) OR (ACT_TYPE = 'D' AND REVIEW_STATUS <> 'Y')) "
				+ " AND ROWNUM <='10' ");
		//消金業務市場資訊
		sql.append(" UNION ALL "
				+ "  SELECT  SEQ, BTYPE, SUBJECT, ROLE, PTYPE, S_DATE, E_DATE, ORGN, CONTACT, ATTACHMENT, PICTURE, CONTENT, CREATETIME, CREATOR, MODIFIER, LASTUPDATE, NULL AS TYPE "
				+ "  FROM TBMKT_BULLETIN_BOARD "
				+ "  WHERE (ROLE <> 'ALL' "
				+ "         AND BTYPE = '06' "
				+ "         AND TO_CHAR(SYSDATE,'YYYYMMDD') >=  TO_CHAR (S_DATE, 'YYYYMMDD') "
				+ "         AND TO_CHAR (E_DATE, 'YYYYMMDD')>= TO_CHAR(SYSDATE,'YYYYMMDD')  "
				+ "         AND INSTR(ROLE, (SELECT ROLE_ID FROM BASE) )>0 ) "
				+ "  OR (ROLE = 'ALL' "
				+ "      AND BTYPE = '06' "
				+ "      AND TO_CHAR(SYSDATE,'YYYYMMDD') >=  TO_CHAR (S_DATE, 'YYYYMMDD') "
				+ "      AND TO_CHAR (E_DATE, 'YYYYMMDD')>= TO_CHAR(SYSDATE,'YYYYMMDD')  "
				+ "      AND INSTR(ROLE,'ALL')>0 ) "
				+ " AND ((REVIEW_STATUS = 'Y' AND ACT_TYPE in ('A', 'M')) OR (ACT_TYPE = 'D' AND REVIEW_STATUS <> 'Y')) "
				+ "  AND ROWNUM <='10' ) "
				+ " ORDER BY S_DATE DESC ");
		
		condition.setQueryString(sql.toString());
		condition.setObject("roleID", getUserVariable(FubonSystemVariableConsts.LOGINROLE));
		List resultList = dam.exeQuery(condition);
		outputVO.setResultList(resultList);
		sendRtnObject(outputVO);
	}
}
