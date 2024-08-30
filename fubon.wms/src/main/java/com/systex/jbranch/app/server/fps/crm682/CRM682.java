package com.systex.jbranch.app.server.fps.crm682;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.fubon.commons.esb.EsbUtil;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Component("crm682")
@Scope("request")
public class CRM682 extends EsbUtil {

	private Logger logger = LoggerFactory.getLogger(CRM682.class);


	public void inquire(Object body, IPrimitiveMap header) throws Exception {
		CRM682InputVO inputVO = (CRM682InputVO) body;
		CRM682OutputVO return_VO = new CRM682OutputVO();
		
		String custID = inputVO.getCust_id();
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		List<Map<String,Object>> list1 = new ArrayList<>();
		List<Map<String,Object>> list2 = new ArrayList<>();
		List<Map<String,Object>> list3 = new ArrayList<>();
		List<Map<String,Object>> list4 = new ArrayList<>();
   
		 try{
			 sql.append(" SELECT COLLATERAL_ADDR, COLLATERAL_SEC, COLLATERAL_AMT3, ISU_AMT, ESTABLISHED_AMT, ");
			 sql.append(" 	     OTH_ADD_RATE1, OTH_ADD_RATE2, OTH_ADD_RATE3, OTH_ADD_RATE4, ");
			 sql.append("  		 OTH_ADD_LIMIT1,OTH_ADD_LIMIT2 ");
			 sql.append(" FROM   TBCRM_AST_LIB_INC_INFO ");
			 sql.append(" WHERE  CUST_ID = :cust_id AND LIB_TYPE = '1' ");
			 condition.setObject("cust_id", custID);
			 condition.setQueryString(sql.toString());
			 list1 = dam.exeQuery(condition);
			 
			 sql = new StringBuffer();
			 condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			 sql.append(" SELECT LIMIT_ACCT_NBR, OUTSTANDING_BAL_AMT, CREDIT_LOAN_RATE, INTEREST_START_DATE ");
			 sql.append(" FROM TBCRM_AST_LIB_INC_INFO ");
			 sql.append(" WHERE CUST_ID = :cust_id AND LIB_TYPE = '2' ");
			 condition.setObject("cust_id", custID);
			 condition.setQueryString(sql.toString());
			 list2 = dam.exeQuery(condition);
			 
			 sql = new StringBuffer();
			 condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			 sql.append(" SELECT CREDIT_PRE_SCORE, CREDIT_ALLOCATION, QUALITY_COMP, CREDIT_INC_RATE, NVL(CREDIT_ALLOCATION_R,0) AS CREDIT_ALLOCATION_R, NVL(CREDIT_RATES_R,0) AS CREDIT_RATES_R ");
			 sql.append(" FROM TBCRM_AST_LIB_INC_INFO ");
			 sql.append(" WHERE CUST_ID = :cust_id AND LIB_TYPE = '3' ");
			 condition.setObject("cust_id", custID);
			 condition.setQueryString(sql.toString());
			 list3 = dam.exeQuery(condition);
			 
			 sql = new StringBuffer();
			 condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			 sql.append(" SELECT CREDIT_CARD_AMT, CREDIT_CARD_INC_AMT ");
			 sql.append(" FROM TBCRM_AST_LIB_INC_INFO ");
			 sql.append(" WHERE CUST_ID = :cust_id AND LIB_TYPE = '4' ");
			 condition.setObject("cust_id", custID);
			 condition.setQueryString(sql.toString());	
			 list4 = dam.exeQuery(condition);
			 
		 }catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
	     }
		 
		 return_VO.setResultList(list1);
		 return_VO.setResultList2(list2);
		 return_VO.setResultList3(list3);
		 return_VO.setResultList4(list4);
    	 this.sendRtnObject(return_VO);
	}


}