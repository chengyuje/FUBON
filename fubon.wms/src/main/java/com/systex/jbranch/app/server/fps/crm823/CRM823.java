package com.systex.jbranch.app.server.fps.crm823;


import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.fubon.commons.esb.EsbFmpJRunConfiguer;
import com.systex.jbranch.fubon.commons.esb.EsbUtil;
import com.systex.jbranch.fubon.commons.esb.cons.EsbSotCons;
import com.systex.jbranch.fubon.commons.esb.vo.ESBUtilInputVO;
import com.systex.jbranch.fubon.commons.esb.vo.ESBUtilOutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.TxHeadVO;
import com.systex.jbranch.fubon.commons.esb.vo.ajw084.AJW084InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.ajw084.AJW084OutputDetailVO;
import com.systex.jbranch.fubon.commons.esb.vo.ajw084.AJW084OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.njw084.NJW084InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.njw084.NJW084OutputDetailVO;
import com.systex.jbranch.fubon.commons.esb.vo.njw084.NJW084OutputVO;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * crm823
 * 
 * @author walalala
 * @date 2016/11/24
 * @spec null
 */
@Component("crm823")
@Scope("request")
public class CRM823 extends EsbUtil {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(CRM823.class);
	
	private String ESB_TYPE = EsbFmpJRunConfiguer.ESB_TYPE;
    private String thisClaz = this.getClass().getSimpleName() + ".";
	
	public void queryPRE_INT(Object body, IPrimitiveMap header) throws Exception {
		CRM823InputVO inputVO = (CRM823InputVO) body;
		CRM823OutputVO return_VO = new CRM823OutputVO();
		
		dam = this.getDataAccessManager();
		QueryConditionIF querycondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		sql.append(" SELECT PRE_INT, CUST_ID, BOND_NBR, CERT_NBR ");
		sql.append(" FROM TBCRM_AST_INV_DTL_BOND_NOW ");
		sql.append(" WHERE CUST_ID = :cust_id ");
		
		querycondition.setObject("cust_id", inputVO.getCust_id());	
		querycondition.setQueryString(sql.toString());	
		
		List<Map<String, Object>> List = dam.exeQuery(querycondition);
		return_VO.setResultList(List);
		this.sendRtnObject(return_VO);
	}
	
	public void inquire(Object body, IPrimitiveMap header) throws Exception {
		CRM823InputVO inputVO = (CRM823InputVO) body;
		CRM823OutputVO return_VO = new CRM823OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF querycondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		// 2017/9/21 russle
		sql.append("WITH Base070 AS ( ");
		sql.append("SELECT * FROM TBPMS_BDS070 WHERE TRIM(BDG06) = :custID AND BDG0K = :entrNO ");
		sql.append(") ");
		sql.append("SELECT A.BDG05 AS ASGN_DT, '配息' AS TXN_TYPE, A.BDG0N AS IVT_CUR, A.BDG08 AS TXN_AMT, B.BDF07 AS DIV_RATE ");
		sql.append("FROM Base070 A ");
		sql.append("LEFT JOIN TBPMS_BDS060 B ON A.BDG01 = B.BDF01 AND (A.BDG05) = (B.BDF05) AND (A.BDG04) = (B.BDF04) ");
		sql.append("WHERE A.SNAP_DATE = (SELECT MAX(B.SNAP_DATE) FROM Base070 B WHERE A.BDG01 = B.BDG01) ");
		sql.append("ORDER BY A.BDG05 DESC ");
		querycondition.setObject("custID", inputVO.getCust_id().trim());
		querycondition.setObject("entrNO", inputVO.getCert_id().trim());
		querycondition.setQueryString(sql.toString());
		List txnList = dam.exeQuery(querycondition);
		return_VO.setTxnList(txnList);
		
		this.sendRtnObject(return_VO);
	}
	
	/**
	 * #1913
	 * 取得海外債、SN類總資產
	 * 電文 => AJW084(OBU) NJW084(DBU)
	 */
	public void getBondSnDeposit(Object body, IPrimitiveMap header) throws Exception {
		CRM823InputVO inputVO = (CRM823InputVO)body;
		CRM823OutputVO outputVO = new CRM823OutputVO();
		
		String custId = inputVO.getCust_id();
		String isOBU = inputVO.getIsOBU();
		
		String constant = null;
		
		if (StringUtils.equals("Y", isOBU)) {
			constant = EsbSotCons.AJW084;
		} else {
			constant = EsbSotCons.NJW084;
		}
		
		// init util
		ESBUtilInputVO esbUtilInputVO = getTxInstance(ESB_TYPE, constant);
		esbUtilInputVO.setModule(thisClaz + new Object() {
		}.getClass().getEnclosingMethod().getName());

		// head
		TxHeadVO txHead = esbUtilInputVO.getTxHeadVO();
		esbUtilInputVO.setTxHeadVO(txHead);
		txHead.setDefaultTxHead();
		
		if (StringUtils.equals("Y", isOBU)) {
			AJW084InputVO txBody = new AJW084InputVO();
			esbUtilInputVO.setAjw084InputVO(txBody);
			txBody.setCustId(custId);
		} else {
			NJW084InputVO txBody = new NJW084InputVO();
			esbUtilInputVO.setNjw084InputVO(txBody);
			txBody.setCustId(custId);
		}
		
		// 發送電文
		List<ESBUtilOutputVO> esbUtilOutputVO = send(esbUtilInputVO);
		
		String bondAmount = "0";
		String snAmount = "0";
		
		if (StringUtils.equals("Y", isOBU)) {
			AJW084OutputVO ajw084OutputVO = esbUtilOutputVO.get(0).getAjw084OutputVO();
			List<AJW084OutputDetailVO> details = ajw084OutputVO.getDetails();
			if (CollectionUtils.isNotEmpty(details)) {
				bondAmount = details.get(0).getBondAmt1();
				snAmount = details.get(0).getBondAmt2();
			}
		} else {
			NJW084OutputVO njw084OutputVO = esbUtilOutputVO.get(0).getNjw084OutputVO();
			List<NJW084OutputDetailVO> details = njw084OutputVO.getDetails();
			if (CollectionUtils.isNotEmpty(details)) {
				bondAmount = details.get(0).getBondAmt1();
				snAmount = details.get(0).getBondAmt2();
			}
		}
		
		outputVO.setBondAmount(new BigDecimal(bondAmount));
		outputVO.setSnAmount(new BigDecimal(snAmount));
		this.sendRtnObject(outputVO);
	}
}