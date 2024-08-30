package com.systex.jbranch.app.server.fps.sot630;

import java.sql.Timestamp;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * @author Ocean
 * @date 2023/08/27
 * 
 */
@Component("sot630")
@Scope("request")
public class SOT630 extends FubonWmsBizLogic {

	private DataAccessManager dam = null;

	public void query(Object body, IPrimitiveMap header) throws JBranchException {

		SOT630InputVO inputVO = (SOT630InputVO) body;
		SOT630OutputVO outputVO = new SOT630OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

		StringBuffer sb = new StringBuffer();
		sb.append("SELECT  SEQNO, ");
		sb.append("        CUST_ID, ");
		sb.append("        CUST_NAME, ");
		sb.append("        AUTH_TYPE, ");
		sb.append("        PROD_TYPE, ");
		sb.append("        TRADE_TYPE, ");
		sb.append("        EVALUATE_VALID_DATE, ");
		sb.append("        ABILITY_RESULT, ");
		sb.append("        TRUST_TRADE_TYPE, ");
		sb.append("        AUTH_BRANCH_NBR, ");
		sb.append("        AUTH_DIRECTOR_EMP_ID, ");
		sb.append("        AUTH_DATE, ");
		sb.append("        CREATETIME, ");
		sb.append("        CREATOR, ");
		sb.append("        MODIFIER, ");
		sb.append("        LASTUPDATE, ");
		
		// 20240109 : add by ocean #0001858_WMS-CR-20231226-01_高齡客戶資訊觀察表控管
		sb.append("        FINACIAL_COGNITION_RESULT ");
		
		sb.append("FROM TBSOT_OLD_AGE_CHECK_LIST ");
		
		sb.append("WHERE 1 = 1 ");
		
		if (StringUtils.isNotEmpty(inputVO.getCustID())) {
			sb.append("AND CUST_ID = :custID ");
			queryCondition.setObject("custID", inputVO.getCustID());
		}
		
		if (StringUtils.isNotEmpty(inputVO.getAuthType())) {
			sb.append("AND AUTH_TYPE = :authType ");
			queryCondition.setObject("authType", inputVO.getAuthType());
		}

		if (StringUtils.isNotEmpty(inputVO.getProdType())) {
			sb.append("AND PROD_TYPE = :prodType ");
			queryCondition.setObject("prodType", inputVO.getProdType());
		}

		if (StringUtils.isNotEmpty(inputVO.getTradeType())) {
			sb.append("AND TRADE_TYPE = :tradeType ");
			queryCondition.setObject("tradeType", inputVO.getTradeType());
		}

		if (StringUtils.isNotEmpty(inputVO.getTrustTradeType())) {
			sb.append("AND TRUST_TRADE_TYPE = :trustTradeType ");
			queryCondition.setObject("trustTradeType", inputVO.getTrustTradeType());
		}
		
		if (null != inputVO.getsDate()) {
			sb.append("AND AUTH_DATE >= TRUNC(:startDate) ");
			queryCondition.setObject("startDate", inputVO.getsDate());
		}

		if (null == inputVO.getsDate() && null != inputVO.geteDate()) {
			sb.append("AND AUTH_DATE <= TRUNC(:endDate) ");
			queryCondition.setObject("endDate", inputVO.geteDate());
		}
		
		sb.append("ORDER BY AUTH_DATE DESC ");

		queryCondition.setQueryString(sb.toString());	
		
		outputVO.setTradeList(dam.exeQuery(queryCondition));

		this.sendRtnObject(outputVO);
	}
	
	public void save(Object body, IPrimitiveMap header) throws JBranchException {
		
		SOT630InputVO inputVO = (SOT630InputVO) body;
		
		save(inputVO);
		
		sendRtnObject(null);
	}

	public void save(SOT630InputVO inputVO) throws JBranchException {
		
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		sb = new StringBuffer();
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		sb.append("INSERT INTO TBSOT_OLD_AGE_CHECK_LIST ( ");
		sb.append("  SEQNO, ");
		sb.append("  CUST_ID, ");
		sb.append("  CUST_NAME, ");
		sb.append("  AUTH_TYPE, ");
		sb.append("  PROD_TYPE, ");
		sb.append("  TRADE_TYPE, ");
		sb.append("  EVALUATE_VALID_DATE, ");
		sb.append("  ABILITY_RESULT, ");
		sb.append("  TRUST_TRADE_TYPE, ");
		sb.append("  AUTH_BRANCH_NBR, ");
		sb.append("  AUTH_DIRECTOR_EMP_ID, ");
		sb.append("  AUTH_DATE, ");
		sb.append("  VERSION, ");
		sb.append("  CREATETIME, ");
		sb.append("  CREATOR, ");
		sb.append("  MODIFIER, ");
		sb.append("  LASTUPDATE,  ");
		
		// 20240109 : add by ocean #0001858_WMS-CR-20231226-01_高齡客戶資訊觀察表控管
		sb.append("  FINACIAL_COGNITION_RESULT  ");
		
		sb.append(") ");
		sb.append("VALUES ( ");
		sb.append("  TBSOT_OLD_AGE_CHECK_LIST_SEQ.NEXTVAL, ");
		sb.append("  :CUST_ID, ");
		sb.append("  :CUST_NAME, ");
		sb.append("  :AUTH_TYPE, ");
		sb.append("  :PROD_TYPE, ");
		sb.append("  :TRADE_TYPE, ");
		sb.append("  :EVALUATE_VALID_DATE, ");
		sb.append("  :ABILITY_RESULT, ");
		sb.append("  :TRUST_TRADE_TYPE, ");
		sb.append("  :AUTH_BRANCH_NBR, ");
		sb.append("  :AUTH_DIRECTOR_EMP_ID, ");
		sb.append("  :AUTH_DATE, ");
		sb.append("  :VERSION, SYSDATE, :CREATOR, :MODIFIER, SYSDATE,  ");
		
		// 20240109 : add by ocean #0001858_WMS-CR-20231226-01_高齡客戶資訊觀察表控管
		sb.append("  :FINACIAL_COGNITION_RESULT  ");
		
		sb.append(") ");
		
		queryCondition.setObject("CUST_ID", inputVO.getCUST_ID());
		queryCondition.setObject("CUST_NAME", inputVO.getCUST_NAME());
		queryCondition.setObject("AUTH_TYPE", inputVO.getAUTH_TYPE());
		queryCondition.setObject("PROD_TYPE", inputVO.getPROD_TYPE());
		queryCondition.setObject("TRADE_TYPE", inputVO.getTRADE_TYPE());
		queryCondition.setObject("EVALUATE_VALID_DATE", new Timestamp(inputVO.getEVALUATE_VALID_DATE().getTime()));
		queryCondition.setObject("ABILITY_RESULT", inputVO.getABILITY_RESULT());
		queryCondition.setObject("TRUST_TRADE_TYPE", inputVO.getTRUST_TRADE_TYPE());
		queryCondition.setObject("AUTH_BRANCH_NBR", inputVO.getAUTH_BRANCH_NBR());
		queryCondition.setObject("AUTH_DIRECTOR_EMP_ID", inputVO.getAUTH_DIRECTOR_EMP_ID());
		queryCondition.setObject("AUTH_DATE", new Timestamp(inputVO.getAUTH_DATE().getTime()));
		queryCondition.setObject("VERSION", 0);
		queryCondition.setObject("CREATOR", (String) SysInfo.getInfoValue(FubonSystemVariableConsts.LOGINID));
		queryCondition.setObject("MODIFIER", (String) SysInfo.getInfoValue(FubonSystemVariableConsts.LOGINID));
		
		// 20240109 : add by ocean #0001858_WMS-CR-20231226-01_高齡客戶資訊觀察表控管
		queryCondition.setObject("FINACIAL_COGNITION_RESULT", inputVO.getFINACIAL_COGNITION_RESULT());
		
		queryCondition.setQueryString(sb.toString());
		
		dam.exeUpdate(queryCondition);
	}
}
