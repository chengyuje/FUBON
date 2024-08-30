package com.systex.jbranch.app.server.fps.service.sso;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.bizLogic.BizLogic;

@Repository("WmsSsoDao")
public class WmsSsoDao extends BizLogic implements WmsSsoDaoInf{
	
	@SuppressWarnings("unchecked")
	public List<Map<String , Object>> queryForRandomNo(String sysCode , BigDecimal rabdomNum) throws DAOException, JBranchException{
		String queryStr = " select * from TBSYS_SSO_INFO where SYS_CODE = :SYS_CODE and RANDOM_NO = :RANDOM_NO ";
		
		return getDataAccessManager().exeQuery(genDefaultQueryConditionIF()
			.setQueryString(queryStr)
			.setObject("SYS_CODE" , sysCode)
			.setObject("RANDOM_NO" , rabdomNum.toString())
		);
	}
	
	/** 刪除過時資料 **/
	public int delTbsysSsoInfoTimeOutData() throws DAOException, JBranchException{
		String queryStr = "DELETE FROM TBSYS_SSO_INFO where SYS_CODE = 'SIG' AND TO_NUMBER(sysdate - CREATETIME) * 24 * 60 * 60 >= 61";
		return getDataAccessManager().exeUpdate(genDefaultQueryConditionIF().setQueryString(queryStr));
	}
}
