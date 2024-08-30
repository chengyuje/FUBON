package com.systex.jbranch.app.server.fps.prd131;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.server.fps.sot701.CustKYCDataVO;
import com.systex.jbranch.app.server.fps.sot701.SOT701;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.fubon.commons.fitness.ProdFitness;
import com.systex.jbranch.fubon.commons.fitness.ProdFitnessOutputVO;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * prd131 金市海外債
 * 
 * @author 
 * @date 
 * @spec null
 */
@Component("prd131")
@Scope("request")
public class PRD131 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(PRD131.class);
	
	//基本資料
	public void getBondInfo(Object body, IPrimitiveMap header) throws JBranchException {
		PRD131InputVO inputVO = (PRD131InputVO) body;
		PRD131OutputVO return_VO = new PRD131OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT A.PRD_ID, A.BOND_CNAME, A.BOND_CATE_ID, A.CURRENCY_STD_ID, A.FACE_VALUE, A.DATE_OF_MATURITY, ROUND((A.DATE_OF_MATURITY - SYSDATE) / 365, 2) as SURPLUS, ");
		sql.append(" A.YTM, A.RISKCATE_ID, A.PI_BUY, CASE WHEN A.OBU_BUY = 'O' THEN 'Y' ELSE 'N' END AS OBU_BUY, A.IS_SALE, A.W8BEN_MARK, ");
		sql.append(" B.ISIN_CODE, B.BOND_PRIORITY, B.CREDIT_RATING_SP, B.CREDIT_RATING_MODDY, B.CREDIT_RATING_FITCH, B.BOND_CREDIT_RATING_SP, B.BOND_CREDIT_RATING_MODDY, ");
		sql.append(" B.BOND_CREDIT_RATING_FITCH, B.ISSUER_BUYBACK, B.RISK_CHECKLIST, B.INSTITION_OF_FLOTATION, B.INSTITION_OF_AVOUCH, B.DATE_OF_FLOTATION, B.BASE_AMT_OF_PURCHASE, ");
		sql.append(" B.BASE_AMT_OF_BUYBACK, B.UNIT_OF_VALUE, B.COUPON_TYPE, B.FREQUENCY_OF_INTEST_PAY, B.LAST_INTEREST_PAY_DATE, B.NEXT_INTEREST_PAY_DATE ");
		sql.append(" FROM TBPRD_VPBND_1004 A ");
		sql.append(" LEFT OUTER JOIN TBPRD_VPBND_1005 B on B.PRD_ID = A.PRD_ID ");
		sql.append(" WHERE A.PRD_ID = :prd_id ");
		
		queryCondition.setObject("prd_id", inputVO.getPrd_id());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		return_VO.setResultList(list);
		
		this.sendRtnObject(return_VO);
	}
	
	//參考報價
	public void getBondPrice(Object body, IPrimitiveMap header) throws JBranchException {
		PRD131InputVO inputVO = (PRD131InputVO) body;
		PRD131OutputVO return_VO = new PRD131OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("select A.BARGAIN_DATE, A.SELL_PRICE, A.BUY_PRICE ");
		sql.append(" from TBPRD_BONDPRICE A ");
		sql.append(" inner join TBPRD_BONDINFO B on A.PRD_ID = B.PRD_ID ");
		sql.append(" where B.ISIN_CODE = :prd_id ");
		queryCondition.setObject("prd_id", inputVO.getPrd_id());
		// date
		sql.append("and BARGAIN_DATE BETWEEN :start AND :end order by BARGAIN_DATE ");
		queryCondition.setObject("start", inputVO.getsDate());
		queryCondition.setObject("end", inputVO.geteDate());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		return_VO.setResultList(list);
		this.sendRtnObject(return_VO);
	}
	
	//配息情況
	public void getBondDividend(Object body, IPrimitiveMap header) throws JBranchException {
		PRD131InputVO inputVO = (PRD131InputVO) body;
		PRD131OutputVO return_VO = new PRD131OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append(" select a.BDPE2, b.UNIT_OF_VALUE AS BDF08, ");
		sql.append(" CASE b.FREQUENCY_OF_INTEST_PAY WHEN 1 THEN a.BDPE9/2 WHEN 3 THEN a.BDPE9/4 WHEN 4 THEN a.BDPE9/12 ELSE a.BDPE9 END AS BDPE9 ");
		sql.append(" from TBPRD_BDS650 a left join TBPRD_BONDINFO b on a.BDPE1 = b.PRD_ID ");
		sql.append(" where b.ISIN_CODE = :prd_id ");
		sql.append(" and a.snap_date = (select max(snap_date) from TBPRD_BDS650 a left join TBPRD_BONDINFO b on a.BDPE1 = b.PRD_ID where b.ISIN_CODE = :prd_id) ");
		sql.append(" order by a.BDPE2 desc ");
		queryCondition.setObject("prd_id", inputVO.getPrd_id());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		return_VO.setResultList(list);
		this.sendRtnObject(return_VO);
	}
	
	//交易明細
	public void getBondTrans(Object body, IPrimitiveMap header) throws JBranchException {
		PRD131InputVO inputVO = (PRD131InputVO) body;
		PRD131OutputVO return_VO = new PRD131OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT A.M_TP_DTETRN, A.M_TP_DTEPMT, A.M_TP_NOMCUR, A.M_TP_NOMINAL, A.M_TP_PRICED, A.TRANS_AMT, ");
		sql.append(" CASE A.M_TP_BUY WHEN 'B' THEN 'Buy' WHEN 'S' THEN 'Sell' ELSE ' ' END AS BUY_SELL ");
		sql.append(" FROM TBCRM_AST_INV_VPBND_1001 A ");
		sql.append(" WHERE A.M_SE_CODE = :prd_id AND A.M_TP_CNTRP = :cust_id ");
		sql.append(" AND A.M_TRN_GRP = :type ");
		sql.append(" ORDER BY A.M_TP_DTETRN DESC ");
		queryCondition.setObject("prd_id", inputVO.getPrd_id());
		queryCondition.setObject("cust_id", inputVO.getCust_id());
		queryCondition.setObject("type", inputVO.getType());
		
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		return_VO.setResultList(list);
		
		this.sendRtnObject(return_VO);
	}
}