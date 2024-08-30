package com.systex.jbranch.comutil.sso.dao;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import com.systex.jbranch.comutil.collection.GenericMap;
import com.systex.jbranch.comutil.dao.CommonDao;
import com.systex.jbranch.platform.common.errHandle.JBranchException;

@Repository("SingleSignOnDao")
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class SingleSignOnDao extends CommonDao implements SingleSignOnDaoInf{
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Map queryMemberInfo(String enpId) throws JBranchException {
		List<Map> resultList = getDataAccessManager().exeQueryWithoutSort(
			genDefaultQueryConditionIF().setQueryString(new StringBuffer()
				.append("SELECT DEPT_ID, ")
				.append("       EMP_NAME, ")
				.append("       CUST_ID, ")
				.append("       ( SELECT DEPT_NAME FROM TBORG_DEFN WHERE DEPT_ID = M.DEPT_ID ) AS DEPT_NAME ")
				.append("FROM TBORG_MEMBER M ")
				.append("WHERE EMP_ID = :EMP_ID ")
				.append("AND NOT EXISTS (SELECT 1 FROM VWORG_EMP_UHRM_INFO UHRM WHERE M.EMP_ID = UHRM.EMP_ID) ")
				.append("UNION ")
				.append("SELECT BRANCH_NBR AS DEPT_ID, ")
				.append("       EMP_NAME, ")
				.append("       CUST_ID, ")
				.append("       BRANCH_NAME AS DEPT_NAME ")
				.append("FROM VWORG_EMP_UHRM_INFO UHRM ")
				.append("WHERE EMP_ID = :EMP_ID ")
				.toString()
			)
			.setObject("EMP_ID" , enpId)
		);
		
		return CollectionUtils.isNotEmpty(resultList) ? resultList.get(0) : null;
	}
	
	/** 行動投保 - MAPP 相關設定  **/
	public GenericMap querySsoConfig() throws JBranchException {
		return queryConfig("SYS.MAPP_SSO_ACC_PW");
	}
	
	/** 保經代  - WS相關設定 **/
	public GenericMap queryAasSsoConfig() throws JBranchException {
		return queryConfig("SYS.AAS_SSO_ACC_PW");
	}
	
	public GenericMap queryConfig(String paramType) throws JBranchException {
		return querySysConfig(paramType , "PARAM_CODE" , "PARAM_NAME");
	}

	/** 人壽視訊投保簽署系統SSO 相關設定  **/
	public GenericMap queryInsSigSsoConfig() throws JBranchException {
		return queryConfig("SYS.INSSIG_SSO_ACC_PW");
	}
}
