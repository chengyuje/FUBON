package com.systex.jbranch.app.server.fps.crm616;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.server.fps.crm831.CRM831;
import com.systex.jbranch.app.server.fps.crm831.CRM831InputVO;
import com.systex.jbranch.app.server.fps.crm831.CRM831OutputVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * @author walalala
 * @date 2016/06/01
 * 
 */
@Component("crm616")
@Scope("request")
public class CRM616 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(CRM616.class);
	
	public void initial(Object body, IPrimitiveMap header) throws JBranchException {
		CRM616InputVO inputVO = (CRM616InputVO) body;
		CRM616OutputVO return_VO = new CRM616OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT CUST_ID, CUST_NAME, BRA_NBR, VIP_DEGREE, AO_CODE FROM TBCRM_CUST_MAST where 1=1 ");
		sql.append("and CUST_ID = :cust_id ");
		queryCondition.setQueryString(sql.toString());
		queryCondition.setObject("cust_id", inputVO.getCust_id());
		
		List list = dam.exeQuery(queryCondition);
		CRM616OutputVO outputVO = new CRM616OutputVO();
		outputVO.setResultList(list);
		sendRtnObject(outputVO);
	}
	
	public void inquire(Object body, IPrimitiveMap header) throws JBranchException {
		CRM616InputVO inputVO = (CRM616InputVO) body;
		CRM616OutputVO return_VO = new CRM616OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT * FROM TBCRM_CUST_TRST_INCOME_OVS where 1=1 ");
		sql.append("and CUST_ID = :cust_id ");
		sql.append("and DATA_YEAR = :data_year ");
		queryCondition.setQueryString(sql.toString());
		queryCondition.setObject("cust_id", inputVO.getCust_id());
		queryCondition.setObject("data_year", inputVO.getData_year());
		
		List list = dam.exeQuery(queryCondition);
		CRM616OutputVO outputVO = new CRM616OutputVO();
		outputVO.setResultList2(list);
		sendRtnObject(outputVO);
	}
	
	
	public void getDataYear(Object body, IPrimitiveMap header) throws JBranchException {
		CRM616InputVO inputVO = (CRM616InputVO) body;
		CRM616OutputVO return_VO = new CRM616OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT * FROM( ");
		sql.append("SELECT DISTINCT DATA_YEAR FROM TBCRM_CUST_TRST_INCOME_OVS ");
		sql.append("WHERE CUST_ID = :cust_id ORDER BY DATA_YEAR DESC) ");
		sql.append("WHERE ROWNUM < 4 ");		//顯示近三年有資料的資料年度(#4138)
		queryCondition.setQueryString(sql.toString());
		queryCondition.setObject("cust_id", inputVO.getCust_id());
		
		List list = dam.exeQuery(queryCondition);
		return_VO.setResultList(list);
		sendRtnObject(return_VO);
	}
	
}