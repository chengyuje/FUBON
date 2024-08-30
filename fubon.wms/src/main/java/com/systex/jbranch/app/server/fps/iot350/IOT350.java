package com.systex.jbranch.app.server.fps.iot350;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * iot350
 * 
 * @author Joe
 * @date 2016/09/20
 * @spec null
 */
@Component("iot350")
@Scope("request")
public class IOT350 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(IOT350.class);

	public void queryData(Object body, IPrimitiveMap header) throws JBranchException {
		IOT350InputVO inputVO = (IOT350InputVO) body;
		IOT350OutputVO return_VO = new IOT350OutputVO();
//		String loginbrh = (String) getCommonVariable(SystemVariableConsts.LOGINBRH);
		
		dam = this.getDataAccessManager();		
		QueryConditionIF queryCondition = dam.getQueryCondition();
		StringBuffer sql = new StringBuffer();
		ArrayList<String> sql_list = new ArrayList<String>();
		
		if(inputVO.getApplyDateFrom() != null && inputVO.getApplyDateTo() == null){
			inputVO.setApplyDateTo(inputVO.getApplyDateFrom());
		}
		
		sql.append("SELECT * FROM (SELECT '成功' IMPORT_STATUS,INS_ID,BRANCH_NBR,CUST_ID,PROPOSER_NAME, ");
		sql.append("INSURED_ID,INSURED_NAME,APPLY_DATE,RECRUIT_ID,REAL_PREMIUM,PPT_TYPE ");
		sql.append("FROM TBIOT_PPT_MAIN where REG_TYPE='3' UNION ");
		sql.append("SELECT '失敗' IMPORT_STATUS,INS_ID,BRANCH_NBR,CUST_ID,PROPOSER_NAME,INSURED_ID, ");
		sql.append("INSURED_NAME,APPLY_DATE,RECRUIT_ID,REAL_PREMIUM,PPT_TYPE ");
		sql.append("FROM TBIOT_PPT_FUGGX88_FAIL) ");
		sql.append("WHERE 1 = 1");
		if(inputVO.getInsId() != null && !inputVO.getInsId().equals("")) {			
			sql.append("AND INS_ID = ? ");
			sql_list.add(inputVO.getInsId());
		}
		if(inputVO.getCustId() != null && !inputVO.getCustId().equals("")) {			
			sql.append("AND CUST_ID = ? ");
			sql_list.add(inputVO.getCustId());
		}
		if(inputVO.getInsuredId() != null && !inputVO.getInsuredId().equals("")) {			
			sql.append("AND INSURED_ID = ? ");
			sql_list.add(inputVO.getInsuredId());
		}
		if(inputVO.getApplyDateFrom() != null) {			
			sql.append("AND trunc(APPLY_DATE) >= NVL(TO_DATE(?, 'YYYY-MM-DD'),apply_date) ");
			sql_list.add(new java.text.SimpleDateFormat("yyyy-MM-dd").format(inputVO.getApplyDateFrom()));
			sql.append("AND trunc(APPLY_DATE) <= NVL(TO_DATE(?, 'YYYY-MM-DD'),apply_date) ");
			sql_list.add(new java.text.SimpleDateFormat("yyyy-MM-dd").format(inputVO.getApplyDateTo()));
		}
		queryCondition.setQueryString(sql.toString());
		for (int sql_i = 0; sql_i < sql_list.size(); sql_i ++) {
			queryCondition.setString(sql_i + 1, sql_list.get(sql_i));
		}
		
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		return_VO.setResultList(list);
		
		sendRtnObject(return_VO);
	}
}