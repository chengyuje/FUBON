package com.systex.jbranch.platform.common.dataaccess.delegate;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.QueryUtilityProxyIF;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;

public class DataSortManager {
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	public enum Type{ 
		COLUMN , ASC
	}
	
	private static ThreadLocal<Map<Type , Object>> sortPool = new ThreadLocal<Map<Type , Object>>();
	
	public static void setSortPool(Map<Type , Object> sortParam){
		sortPool.set(sortParam);
	}
	
	
	/**
	 * 
	 * @param body
	 * @param header
	 * @return 
	 * @throws DAOException, JBranchException 
	 * @throws JBranchException
	 */
	public static QueryConditionIF getSorting(QueryUtilityProxyIF queryUtilityProxy, QueryConditionIF queryCondition) throws DAOException, JBranchException {
		Map<Type , Object> paramMap = null;
		
		if(MapUtils.isEmpty(paramMap = sortPool.get())) 
			return queryCondition;
	
		String column = ObjectUtils.toString(paramMap.get(DataSortManager.Type.COLUMN));
		boolean asc = paramMap.get(DataSortManager.Type.ASC) == null ? false : (Boolean)paramMap.get(DataSortManager.Type.ASC);
			
		HashMap<String, Object> strSQL = new HashMap<String, Object>();
    	strSQL.put("original", queryCondition.getQueryString());
    	
    	//select case
    	if(strSQL.get("original").toString().toLowerCase().indexOf(" order by ")>0){
    		strSQL.put("sectionA", queryCondition.getQueryString().substring(0, queryCondition.getQueryString().toLowerCase().lastIndexOf(" order by ")));
    		strSQL.put("sectionB", queryCondition.getQueryString().substring(queryCondition.getQueryString().toLowerCase().lastIndexOf(" order by "), queryCondition.getQueryString().length()-1));
        	strSQL.put("replace", " order by " + column + ( asc ? " ASC " : " DESC " ));
        	strSQL.put("result", strSQL.get("sectionA").toString()+strSQL.get("replace").toString());
    	}else{
    		strSQL.put("replace", " order by "+ column + ( asc ? " ASC " : " DESC " ));
    		strSQL.put("result", strSQL.get("original").toString()+strSQL.get("replace").toString());
    	}  
    	queryCondition.setQueryString(strSQL.get("result").toString());
    	
    	//result
        return queryCondition;
	}
}
