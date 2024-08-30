package com.systex.jbranch.app.server.fps.crm911;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBCRM_EMP_FAVOR_LINKVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.serialnumber.SerialNumberUtil;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * MENU
 * 
 * @author moron
 * @date 2016/04/20
 * @spec null
 * --
 *   Date        Memo                            Modifier
 *   2016/10/18   指定角色可使用新增、刪除功能	     Stella 
 *   
 * --
 */
@Component("crm911")
@Scope("request")
public class CRM911 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(CRM911.class);
	
	public void initial(Object body, IPrimitiveMap header) throws JBranchException {
		CRM911InputVO inputVO = (CRM911InputVO) body;
		CRM911OutputVO return_VO = new CRM911OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition.setQueryString("SELECT SEQ, ORDER_NO, EMP_ID, LINK_NAME, LINK_URL FROM TBCRM_EMP_FAVOR_LINK ORDER BY ORDER_NO");
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		return_VO.setResultList(list);
		this.sendRtnObject(return_VO);
	}
	
	// 給客戶快查用 借放
	public void checkQuick(Object body, IPrimitiveMap header) throws JBranchException {
		CRM911OutputVO return_VO = new CRM911OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition.setQueryString("SELECT PARAM_CODE FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'SYS.NO_SEARCH_QUICK_CUST'");
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		return_VO.setResultList(list);
		this.sendRtnObject(return_VO);
	}
	
	public void save(Object body, IPrimitiveMap header) throws JBranchException {
		CRM911InputVO inputVO = (CRM911InputVO) body;
		dam = this.getDataAccessManager();
		
		// del first
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		condition.setQueryString("truncate table TBCRM_EMP_FAVOR_LINK ");
		dam.exeUpdate(condition);
		// then add
		for(Map<String, Object> map : inputVO.getList()) {
			TBCRM_EMP_FAVOR_LINKVO vo = new TBCRM_EMP_FAVOR_LINKVO();
			vo.setSEQ(new BigDecimal(getSN()));
			vo.setORDER_NO(new BigDecimal(map.get("ORDER_NO").toString()));
			vo.setEMP_ID(inputVO.getEmp_id());
			vo.setLINK_NAME(map.get("LINK_NAME").toString());
			if(ObjectUtils.toString(map.get("LINK_URL")).indexOf("http://") == -1 && ObjectUtils.toString(map.get("LINK_URL")).indexOf("https://") == -1)
				vo.setLINK_URL("http://"+ObjectUtils.toString(map.get("LINK_URL")));
			else
				vo.setLINK_URL(ObjectUtils.toString(map.get("LINK_URL")));
			dam.create(vo);
		}
		this.sendRtnObject(null);
	}
	private String getSN() throws JBranchException {
		  SerialNumberUtil sn = new SerialNumberUtil();
		  String seqNum = "";
		  try{
		    seqNum = sn.getNextSerialNumber("CRM911");
		  }
		  catch(Exception e){
		   sn.createNewSerial("CRM911", "0000000000", null, null, null, 1, new Long("99999999"), "y", new Long("0"), null);
		   seqNum = sn.getNextSerialNumber("CRM911");
		   }
		  return seqNum;
	}
	
	
	
}