package com.systex.jbranch.app.server.fps.crm239;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.server.fps.crm230.CRM230;
import com.systex.jbranch.app.server.fps.crm230.CRM230OutputVO;
import com.systex.jbranch.app.server.fps.crm230.CRM230_ALLInputVO;
import com.systex.jbranch.app.server.fps.crm239.CRM239;
import com.systex.jbranch.common.xmlInfo.XMLInfo;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataManager.WorkStation;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * MENU
 * 
 * @author Stella
 * @date 2016/05/27 初版
 * @spec null
 */

@Component("crm239")
@Scope("request")
public class CRM239 extends FubonWmsBizLogic{
	
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(CRM239.class);
	
	@SuppressWarnings("unchecked")
	public void inquire(Object body, IPrimitiveMap header) throws JBranchException {
		
		WorkStation ws = DataManager.getWorkStation(uuid);
		CRM239InputVO inputVO = (CRM239InputVO) body;
		
		CRM230_ALLInputVO inputVO_all = new CRM230_ALLInputVO();
		inputVO_all.setCrm239inputVO(inputVO);
		inputVO_all.setCrm230inputVO(inputVO);
		inputVO_all.setAvailRegionList(getUserVariable(FubonSystemVariableConsts.AVAILREGIONLIST));
		inputVO_all.setAvailAreaList(getUserVariable(FubonSystemVariableConsts.AVAILAREALIST));
		inputVO_all.setAvailBranchList(getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
		inputVO_all.setLoginEmpID(ws.getUser().getUserID());
		inputVO_all.setLoginRole(getUserVariable(FubonSystemVariableConsts.LOGINROLE));
		
		CRM230 crm230 = (CRM230) PlatformContext.getBean("crm230");
		CRM230OutputVO outputVO_crm230 = new CRM230OutputVO();
		CRM230OutputVO outputVO_crm230_1 = new CRM230OutputVO();
		CRM230OutputVO outputVO_crm230_2 = new CRM230OutputVO();
		
		if(StringUtils.equals("1", inputVO.getIns_source())) {
			//富邦銀庫存資料
			outputVO_crm230 = crm230.inquire_common(inputVO_all, "CRM239");
		} else if(StringUtils.equals("2", inputVO.getIns_source())) {
			//日盛銀庫存資料
			outputVO_crm230 = crm230.inquire_common(inputVO_all, "CRM239_JSB");
		} else { 
			//富邦銀&日盛銀庫存資料
			outputVO_crm230_1 = crm230.inquire_common(inputVO_all, "CRM239"); //富邦銀
			outputVO_crm230_2 = crm230.inquire_common(inputVO_all, "CRM239_JSB"); //日盛銀
			
			List<Map<String, Object>> list1 = new ArrayList<Map<String, Object>>(outputVO_crm230_1.getResultList());
			List<Map<String, Object>> list2 = new ArrayList<Map<String, Object>>(outputVO_crm230_2.getResultList());
			list1.addAll(list2);
			
			outputVO_crm230.setResultList(list1);
			outputVO_crm230.setTotalCntRecord(outputVO_crm230_1.getTotalCntRecord().add(outputVO_crm230_2.getTotalCntRecord()));
		}

		
		this.sendRtnObject(outputVO_crm230);
	}
	
	public void getPolicy_simp_name(Object body, IPrimitiveMap header) throws JBranchException {
		CRM239OutputVO return_VO = new CRM239OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT distinct POLICY_SIMP_NAME FROM TBCRM_AST_INS_DAILY order by POLICY_SIMP_NAME ");
		queryCondition.setQueryString(sql.toString());
		// result
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		return_VO.setResultList(list);
		this.sendRtnObject(return_VO);
	}
	
	public void getInsPrdName(Object body, IPrimitiveMap header) throws JBranchException {
		CRM239OutputVO return_VO = new CRM239OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT INSPRD_ID, INSPRD_NAME FROM VWPRD_INS_MAIN ORDER BY INSPRD_ID ");
		queryCondition.setQueryString(sql.toString());
		// result
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		return_VO.setResultList(list);
		this.sendRtnObject(return_VO);
	}
	
	//取得保險公司下拉選單資料
	public void getInsCompany(Object body, IPrimitiveMap header) throws JBranchException {
		CRM239OutputVO return_VO = new CRM239OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT SERIALNUM AS DATA, CNAME AS LABEL ");
		sql.append(" FROM TBJSB_INS_PROD_COMPANY ORDER BY SERIALNUM ");
		queryCondition.setQueryString(sql.toString());
		// result
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		return_VO.setResultList(list);
		
		this.sendRtnObject(return_VO);
	}
	
}
