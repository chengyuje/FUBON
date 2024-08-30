package com.systex.jbranch.app.server.fps.cam110;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * MENU
 * 
 * @author moron
 * @date 2016/04/20
 * @spec null
 */
@Component("cam110")
@Scope("request")
public class CAM110 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(CAM110.class);
	
	public void initial(Object body, IPrimitiveMap header) throws JBranchException {
		CAM110OutputVO return_VO = new CAM110OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("select PARAM_CODE, PARAM_NAME from TBSYSPARAMETER where PARAM_TYPE = 'CAM.MAX_CONTACT' ");
		condition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(condition);
		return_VO.setResultList(list);
		this.sendRtnObject(return_VO);
	}
	
	public void save(Object body, IPrimitiveMap header) throws JBranchException {
		CAM110InputVO inputVO = (CAM110InputVO) body;
		dam = this.getDataAccessManager();
		
		String sql = "update TBSYSPARAMETER set PARAM_NAME = :name where PARAM_TYPE = 'CAM.MAX_CONTACT' and PARAM_CODE = :code ";
		for(Map<String, Object> map : inputVO.getList()) {
			QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			condition.setQueryString(sql.toString());
			condition.setObject("name", map.get("PARAM_NAME"));
			condition.setObject("code", map.get("PARAM_CODE"));
			dam.exeUpdate(condition);
		}
		this.sendRtnObject(null);
	}
	
	
}