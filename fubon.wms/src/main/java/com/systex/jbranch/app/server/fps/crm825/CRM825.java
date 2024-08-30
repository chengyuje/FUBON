package com.systex.jbranch.app.server.fps.crm825;

import static com.systex.jbranch.fubon.commons.esb.cons.EsbSotCons.ETF_ASSETS;

import java.sql.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.fubon.commons.esb.EsbFmpJRunConfiguer;
import com.systex.jbranch.fubon.commons.esb.EsbUtil;
import com.systex.jbranch.fubon.commons.esb.vo.ESBUtilInputVO;
import com.systex.jbranch.fubon.commons.esb.vo.ESBUtilOutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.TxHeadVO;
import com.systex.jbranch.fubon.commons.esb.vo.ajw084.AJW084InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.njw084.NJW084InputVO;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * crm825
 * 
 * @author Walalala
 * @date 2016/12/24
 * @spec null
 */
@Component("crm825")
@Scope("request")
public class CRM825 extends EsbUtil {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(CRM825.class);
	
	private String ESB_TYPE = EsbFmpJRunConfiguer.ESB_TYPE;
    private String thisClaz = this.getClass().getSimpleName() + ".";
	
	public void inquire(Object body, IPrimitiveMap header) throws Exception {
		CRM825InputVO inputVO = (CRM825InputVO) body;
		CRM825OutputVO return_VO = new CRM825OutputVO();
		
		dam = this.getDataAccessManager();
		StringBuffer sql = new StringBuffer();
//		
//		QueryConditionIF querycondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
//		StringBuffer sql = new StringBuffer();
//		sql.append("SELECT A.DATE_OF_MATURITY, A.RISKCATE_ID, B.ISIN_CODE, B.INSTITION_OF_FLOTATION, ");
//		sql.append("B.TRANS_DATE, B.DATE_OF_FLOTATION, B.FREQUENCY_OF_INTEST_PAY, B.FIXED_RATE_DURATION, B.FIXED_DIVIDEND_RATE, B.FLOATING_DIVIDEND_RATE, ");
//		sql.append("B.BASE_AMT_OF_PURCHASE, B.UNIT_AMT_OF_BUYBACK, B.START_DATE_OF_BUYBACK, B.INVESTMENT_TARGETS, A.RATE_GUARANTEEPAY ");
//		sql.append("FROM TBPRD_SN A LEFT JOIN TBPRD_SNINFO B ON A.PRD_ID = B.PRD_ID ");
//		sql.append("WHERE A.PRD_ID = :prod_id ");
//
//		querycondition.setObject("prod_id", inputVO.getProd_id().trim());		
//
//		querycondition.setQueryString(sql.toString());		
//		List<Map<String,Object>> list = dam.exeQuery(querycondition);
//		return_VO.setResultList(list);
//		
		sql = new StringBuffer();
		QueryConditionIF querycondition2 = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		//債券交易明細
		sql.append("SELECT * FROM ( ");
		sql.append("SELECT ASGN_DT, TXN_TYPE, IVT_CUR, TXN_AMT, null AS DIV_RATE ");
		sql.append("FROM TBPMS_WMG_BOND_TXN ");
		sql.append("WHERE CUST_ID = :custID AND ENTR_NO = :entrNO ");
		sql.append("UNION ");
		sql.append("SELECT A.BDG05 AS ASGN_DT, '配息' AS TXN_TYPE, ");
		sql.append("A.BDG0N AS IVT_CUR, A.BDG08 AS TXN_AMT, B.BDF07 AS DIV_RATE ");
		sql.append("FROM TBPMS_BDS070 A LEFT JOIN TBPMS_BDS060 B ON ");
		sql.append("A.BDG01 = B.BDF01 ");
		sql.append("AND TRUNC(A.BDG05) = TRUNC(B.BDF05) ");
		sql.append("AND TRUNC(A.BDG04) = TRUNC(B.BDF04) ");
		sql.append("WHERE TRIM(A.BDG06) = :custID ");
		sql.append("AND TRIM(A.BDG0K) = :entrNO ");
		sql.append(")ORDER BY ASGN_DT DESC ");

		querycondition2.setObject("custID", inputVO.getCust_id().trim());
		querycondition2.setObject("entrNO", inputVO.getCert_id().trim());
		
		querycondition2.setQueryString(sql.toString());		
		List<Map<String,Object>> txnList = dam.exeQuery(querycondition2);
		return_VO.setTxnList(txnList);
		
		//報價
//		QueryConditionIF querycondition3 = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
//		sql = new StringBuffer();
//		sql.append("SELECT BARGAIN_DATE, SELL_PRICE, BUY_PRICE FROM TBPRD_BONDPRICE WHERE PRD_ID = :prod_id ");
//
//		querycondition3.setObject("prod_id", inputVO.getProd_id());	
//		querycondition3.setQueryString(sql.toString());
//		List<Map<String,Object>> priceList = dam.exeQuery(querycondition3);
//		
//		return_VO.setPriceList(priceList);
		
		//配息明細
//		QueryConditionIF querycondition4 = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
//		sql = new StringBuffer();
//		sql.append("SELECT (A.BDG08/A.BDG0L)*100 AS CURR_INT, ");
//		sql.append("A.BDG08, A.BDG0L, B.BDF05, C.BDPE9, B.BDF09, B.BDF0A, (C.BDPE6 - C.BDPE5) AS DAY ");
//		sql.append("FROM ( ");
//		sql.append("SELECT A.*, ");
//		sql.append("rank() over( partition by A.BDG01, A.BDG05 order by A.SNAP_DATE desc ) as RANK ");
//		sql.append("FROM TBPMS_BDS070 A ");
//		sql.append(") A ");
//		sql.append("LEFT JOIN TBPMS_BDS060 B ON A.BDG01 = B.BDF01 ");
//		sql.append("AND TRUNC(A.BDG05) = TRUNC(B.BDF05) ");
//		sql.append("AND TRUNC(A.BDG04) = TRUNC(B.BDF04) ");
//		sql.append("LEFT JOIN TBPRD_BDS650 C ON ");
//		sql.append("TRUNC(A.SNAP_DATE) = TRUNC(C.SNAP_DATE) ");
//		sql.append("AND A.BDG01 = C.BDPE1 ");
//		sql.append("AND TRUNC(A.BDG05) = TRUNC(C.BDPE2) ");
//		sql.append("WHERE TRIM(A.BDG01) = :prod_id ");
//		sql.append("AND TRIM(A.BDG06)   = :cust_id ");
//		sql.append("AND A.RANK = 1 ");
//
//		querycondition4.setObject("prod_id", inputVO.getProd_id().trim());
//		querycondition4.setObject("cust_id", inputVO.getCust_id().trim());
//
//		querycondition4.setQueryString(sql.toString());
//		
//		List<Map<String,Object>> divList = dam.exeQuery(querycondition4);
//		
//		if(divList != null){
//			for (Map<String,Object> divMap : divList) {
//				java.util.Date BDF09 = new EsbUtil().toAdYearMMdd(divMap.get("BDF09").toString());
//				java.util.Date BDF0A = new EsbUtil().toAdYearMMdd(divMap.get("BDF0A").toString());
//				divMap.put("BDF09", BDF09);
//				divMap.put("BDF0A", BDF0A);
//			}			
//		}
//		
//		return_VO.setDivList(divList);
	
		this.sendRtnObject(return_VO);
	}
	
	public void inquire_DB(Object body, IPrimitiveMap header) throws Exception {
		CRM825InputVO inputVO = (CRM825InputVO) body;
		CRM825OutputVO return_VO = new CRM825OutputVO();
		StringBuffer sql = new StringBuffer();
		
		dam = this.getDataAccessManager();
		QueryConditionIF querycondition2 = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql = new StringBuffer();
		sql.append("SELECT TRANS_DATE, DATE_OF_FLOTATION, PRD_ID FROM TBPRD_SNINFO  ");
		sql.append("WHERE 1=1  ");
		sql.append("AND PRD_ID IN (:prod_id_List) ");

		querycondition2.setObject("prod_id_List", inputVO.getProd_id_list());

		querycondition2.setQueryString(sql.toString());		
		List<Map<String,Object>> sninfoList = dam.exeQuery(querycondition2);
		return_VO.setSninfoList(sninfoList);
		
		this.sendRtnObject(return_VO);
	}
}