package com.systex.jbranch.comutil.dao;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;

import com.systex.jbranch.comutil.collection.GenericMap;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.bizLogic.BizLogic;

public class CommonDao extends BizLogic{
	@SuppressWarnings("unchecked")
	public GenericMap querySysConfig(String paramType , String key , String val) throws JBranchException {
		GenericMap genMap = new GenericMap();
		
		List<Map> resultList = getDataAccessManager().exeQueryWithoutSort(
			genDefaultQueryConditionIF().setQueryString(
				"select PARAM_CODE , PARAM_NAME from TBSYSPARAMETER WHERE PARAM_TYPE = :paramType "
			).setObject("paramType", paramType)
		);
		
		if(CollectionUtils.isEmpty(resultList)){
			return null;
		}
		
		for(Map<String , Object> resultMap : resultList){
			genMap.put(resultMap.get(key) , resultMap.get(val));
		}
		
		return genMap; 
	}
}
