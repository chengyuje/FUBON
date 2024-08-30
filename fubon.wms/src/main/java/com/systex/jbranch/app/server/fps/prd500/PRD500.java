package com.systex.jbranch.app.server.fps.prd500;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.platformdao.table.TBSYSPARAMETERPK;
import com.systex.jbranch.platform.common.platformdao.table.TBSYSPARAMETERVO;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Component("prd500")
@Scope("request")
public class PRD500 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(PRD500.class);
	
	public void inquire(Object body, IPrimitiveMap header) throws JBranchException {
		PRD500OutputVO return_VO = new PRD500OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition.setQueryString("select PARAM_NAME from TBSYSPARAMETER where PARAM_TYPE = 'PRD.TAXLINK' and PARAM_CODE = 'PDF01'");
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		if(list.size() > 0)
			return_VO.setLink_url(ObjectUtils.toString(list.get(0).get("PARAM_NAME")));
		
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition.setQueryString("select PARAM_NAME from TBSYSPARAMETER where PARAM_TYPE = 'PRD.TAXLINK' and PARAM_CODE = 'PDF02'");
		List<Map<String, Object>> list2 = dam.exeQuery(queryCondition);
		if(list2.size() > 0)
			return_VO.setLink_url2(ObjectUtils.toString(list2.get(0).get("PARAM_NAME")));
		
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition.setQueryString("select PARAM_NAME from TBSYSPARAMETER where PARAM_TYPE = 'PRD.TAXLINK' and PARAM_CODE = 'LINK01'");
		List<Map<String, Object>> list3 = dam.exeQuery(queryCondition);
		if(list3.size() > 0)
			return_VO.setLink_url3(ObjectUtils.toString(list3.get(0).get("PARAM_NAME")));
		
		this.sendRtnObject(return_VO);
	}
	
	public void save(Object body, IPrimitiveMap header) throws JBranchException {
		PRD500InputVO inputVO = (PRD500InputVO) body;
		dam = this.getDataAccessManager();
		
		TBSYSPARAMETERPK pk = new TBSYSPARAMETERPK();
		pk.setPARAM_TYPE("PRD.TAXLINK");
		pk.setPARAM_CODE("PDF01");
		TBSYSPARAMETERVO vo = (TBSYSPARAMETERVO) dam.findByPKey(TBSYSPARAMETERVO.TABLE_UID, pk);
		if (vo != null) {
			vo.setPARAM_NAME(inputVO.getLink_url());
			vo.setPARAM_NAME_EDIT(inputVO.getLink_url());
			dam.update(vo);
		} else
			throw new APException("ehl_01_common_001");
		
		pk.setPARAM_CODE("PDF02");
		TBSYSPARAMETERVO vo2 = (TBSYSPARAMETERVO) dam.findByPKey(TBSYSPARAMETERVO.TABLE_UID, pk);
		if (vo2 != null) {
			vo2.setPARAM_NAME(inputVO.getLink_url2());
			vo2.setPARAM_NAME_EDIT(inputVO.getLink_url2());
			dam.update(vo2);
		} else
			throw new APException("ehl_01_common_001");
		
		pk.setPARAM_CODE("LINK01");
		TBSYSPARAMETERVO vo3 = (TBSYSPARAMETERVO) dam.findByPKey(TBSYSPARAMETERVO.TABLE_UID, pk);
		if (vo3 != null) {
			vo3.setPARAM_NAME(inputVO.getLink_url3());
			vo3.setPARAM_NAME_EDIT(inputVO.getLink_url3());
			dam.update(vo3);
		} else
			throw new APException("ehl_01_common_001");
		
		this.sendRtnObject(null);
	}
	
}