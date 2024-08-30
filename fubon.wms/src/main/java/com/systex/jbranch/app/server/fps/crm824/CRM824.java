package com.systex.jbranch.app.server.fps.crm824;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.fubon.commons.cbs.CBSService;
import com.systex.jbranch.fubon.commons.esb.EsbFmpJRunConfiguer;
import com.systex.jbranch.fubon.commons.esb.EsbUtil;
import com.systex.jbranch.fubon.commons.esb.cons.EsbSotCons;
import com.systex.jbranch.fubon.commons.esb.vo.ESBUtilInputVO;
import com.systex.jbranch.fubon.commons.esb.vo.ESBUtilOutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.TxHeadVO;
import com.systex.jbranch.fubon.commons.esb.vo.spwebq1.SPWEBQ1InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.spwebq1.SPWEBQ1OutputVO;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * crm824
 * 
 * @author moron
 * @date 2016/06/14
 * @spec null
 */
@Component("crm824")
@Scope("request")
public class CRM824 extends EsbUtil {
	@Autowired
	private CBSService cbsService;

	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(CRM824.class);

	private String ESB_TYPE = EsbFmpJRunConfiguer.ESB_TYPE;
	private String thisClaz = this.getClass().getSimpleName() + ".";

	public void inquire(Object body, IPrimitiveMap header) throws Exception {
		CRM824InputVO inputVO = (CRM824InputVO) body;
		CRM824OutputVO return_VO = new CRM824OutputVO();

		dam = this.getDataAccessManager();
		StringBuffer sql = new StringBuffer();

//		QueryConditionIF querycondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
//		StringBuffer sql = new StringBuffer();
//		sql.append("SELECT A.RISKCATE_ID, B.START_DATE_OF_BUYBACK, B.FIXED_RATE_DURATION, ");
//		sql.append("B.FIXED_DIVIDEND_RATE, B.FLOATING_DIVIDEND_RATE, B.INVESTMENT_TARGETS FROM TBPRD_SI A LEFT JOIN ");
//		sql.append("TBPRD_SIINFO B ON A.PRD_ID = B.PRD_ID WHERE A.PRD_ID = :prod_id ");
//
//		querycondition.setObject("prod_id", inputVO.getProd_id());		
//
//		querycondition.setQueryString(sql.toString());		
//		List<Map<String,Object>> list = dam.exeQuery(querycondition);
//		return_VO.setResultList(list);

		// SI交易明細
		QueryConditionIF querycondition2 = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql = new StringBuffer();
		sql.append(" With BaseDate As ( ");
		sql.append("   SELECT DISTINCT PLADTE FROM TBPRD_SIDIVIDEND  ");
		sql.append("    WHERE PLARNO = :entrNO AND PLAID = :custID AND PLAPRD = :prdNO AND PLATYP = '1'), ");
		sql.append(" BaseCnt As ( ");
		sql.append("   SELECT TRUNC(PLADTE) AS INBOX_DT, '配息' AS TXN_TYPE, PLACCY AS CUR, PLALAM AS CONT_AMT  ");
		sql.append("   FROM TBPRD_SIDIVIDEND  ");
		sql.append("   WHERE PLARNO = :entrNO AND PLAID = :custID AND PLAPRD = :prdNO AND PLATYP = '1' ");
		sql.append("   AND PLADTE in (SELECT PLADTE FROM BaseDate) ");
		sql.append(
				"   AND SNAP_DATE = (SELECT MAX(SNAP_DATE) FROM TBPRD_SIDIVIDEND WHERE PLARNO = :entrNO AND PLAID = :custID AND PLAPRD = :prdNO AND PLATYP = '1') ");
		sql.append("   UNION ");
		sql.append("   SELECT TRUNC(PLADTE) AS INBOX_DT, '配息' AS TXN_TYPE, PLACCY AS CUR, PLALAM AS CONT_AMT  ");
		sql.append("   FROM TBPRD_SIDIVIDEND  ");
		sql.append("   WHERE PLARNO = :entrNO AND PLAID = :custID AND PLAPRD = :prdNO AND PLATYP = '2' ");
		sql.append("   AND PLADTE in (SELECT PLADTE FROM BaseDate)) ");
		sql.append(" SELECT INBOX_DT, TXN_TYPE, CUR, SUM(CONT_AMT) AS CONT_AMT FROM BaseCnt ");
		sql.append(" GROUP BY INBOX_DT, TXN_TYPE, CUR  ");
		sql.append(" ORDER BY INBOX_DT DESC  ");

		querycondition2.setObject("custID", inputVO.getCust_id());
		querycondition2.setObject("entrNO", inputVO.getCert_id());
		querycondition2.setObject("prdNO", inputVO.getProd_id());

		querycondition2.setQueryString(sql.toString());
		List<Map<String, Object>> txnList = dam.exeQuery(querycondition2);

		return_VO.setTxnList(txnList);

		// 報價
//		QueryConditionIF querycondition3 = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
//		sql = new StringBuffer();
//		sql.append("SELECT SDDTE, SDAMT2 FROM TBPRD_SIPRICE WHERE SDPRD = :prod_id ");
//
//		querycondition3.setObject("prod_id", inputVO.getProd_id());	
//		querycondition3.setQueryString(sql.toString());
//		List<Map<String,Object>> priceList = dam.exeQuery(querycondition3);
//		
//		return_VO.setPriceList(priceList);

		this.sendRtnObject(return_VO);

	}

	/**
	 * #1913 取得SI類總資產 電文 => SPWEBQ1
	 */
	public void getSIDeposit(Object body, IPrimitiveMap header) throws Exception {
		CRM824InputVO inputVO = (CRM824InputVO) body;
		CRM824OutputVO outputVO = new CRM824OutputVO();

		// init util
		ESBUtilInputVO esbUtilInputVO = getTxInstance(ESB_TYPE, EsbSotCons.SPWEBQ1);
		esbUtilInputVO.setModule(thisClaz + new Object() {
		}.getClass().getEnclosingMethod().getName());

		// head
		TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
		esbUtilInputVO.setTxHeadVO(txHead);
		txHead.setDefaultTxHead();

		SPWEBQ1InputVO txBody = new SPWEBQ1InputVO();
		esbUtilInputVO.setSpwebq1InputVO(txBody);
		txBody.setCustId(inputVO.getCust_id());
		
		try {
			List<ESBUtilOutputVO> esbUtilOutputVO = send(esbUtilInputVO);
			SPWEBQ1OutputVO spwebq1tputVO = esbUtilOutputVO.get(0).getSpwebq1OutputVO();
			String siSmount = "0";
			if (CollectionUtils.isNotEmpty(spwebq1tputVO.getDetails())) {
				siSmount = spwebq1tputVO.getDetails().get(0).getAmount();
			}
			outputVO.setSiAmount(new BigDecimal(siSmount));
		} catch (Exception e) {
			logger.info(e.getMessage());
		}
		
		this.sendRtnObject(outputVO);
	}

}