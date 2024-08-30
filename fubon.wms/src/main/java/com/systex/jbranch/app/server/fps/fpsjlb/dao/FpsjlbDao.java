package com.systex.jbranch.app.server.fps.fpsjlb.dao;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;

import com.systex.jbranch.app.server.fps.fpsjlb.conf.TableType;
import com.systex.jbranch.app.server.fps.fpsutils.FPSUtils;
import com.systex.jbranch.comutil.callBack.CallBackExcute;
import com.systex.jbranch.comutil.collection.CollectionSearchUtils;
import com.systex.jbranch.comutil.collection.GenericMap;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.bizLogic.BizLogic;
@SuppressWarnings({ "unchecked", "rawtypes" })
abstract public class FpsjlbDao extends BizLogic implements FpsjlbDaoInf{

	public List<Map<String , Object>> queryDateByIdType(String[] cloumns , TableType table , Integer year , List<Map<String , Object>> idTypeList) throws JBranchException{
		DataAccessManager dam = getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		int idx = 1;
		boolean isYear = year != null;
		boolean isIdType = CollectionUtils.isNotEmpty(idTypeList);
		
		StringBuffer queryStr = new StringBuffer()
			.append(" select ")
			.append(ArrayUtils.isEmpty(cloumns) ? "*" : CollectionSearchUtils.reString(cloumns, " , " , false))
			.append(" from ").append(table.getTableName());

		StringBuffer queryStr2 = new StringBuffer()
			.append(" select ")
			.append(ArrayUtils.isEmpty(cloumns) ? "*" : CollectionSearchUtils.reString(cloumns, " , " , false))
			.append(" from ").append(table.getTableName());
		
		if(isYear || isIdType){
			queryStr.append(" where ");
			queryStr2.append(" where ");
			
			if(isYear){
				queryStr.append(table.getDayColumnName()).append(" > :afterDate ");
				condition.setObject("afterDate", table.beforeSysDateStr(year));
				
				queryStr2.append(table.getDayColumnName()).append(" > '" + table.beforeSysDateStr(year) +"' ");
			}

			if(isIdType){
				queryStr.append(isYear ? " and ( " : " ( ");
				queryStr2.append(isYear ? " and ( " : " ( ");
				
				for(Map idType : idTypeList){
					String prdIdIdx = "PRD_ID" + idx;
					String prdTypeIdx = "PRD_TYPE" + idx;
					
					queryStr.append(" prd_id = :").append(prdIdIdx);
					queryStr.append(" and ");
					queryStr.append(" prd_type = :").append(prdTypeIdx);
					condition.setObject(prdIdIdx , idType.get("PRD_ID"));
					condition.setObject(prdTypeIdx , idType.get("PRD_TYPE"));
					queryStr.append(idx < idTypeList.size() ? " or " : "");
					
					queryStr2.append(" prd_id = '").append(idType.get("PRD_ID") + "' ");
					queryStr2.append(" and ");
					queryStr2.append(" prd_type = '").append(idType.get("PRD_TYPE") + "' ");
					queryStr2.append(idx < idTypeList.size() ? " or " : "");
					
					idx++;
				}
				
				queryStr.append(" ) ");
				queryStr2.append(" ) ");
			}
		}

		System.out.println(queryStr2);
		return dam.exeQuery(condition.setQueryString(queryStr.toString()));
	}
	
	public List<Map<String , Object>> queryHistoryData(TableType table , List<Map<String , Object>> idTypeDateList) throws JBranchException{
		return queryHistoryData(table , idTypeDateList , new CallBackExcute(){
			public <T> T callBack(GenericMap genericMap) {
				return null;
		}});
	}
	
	public List<Map<String , Object>> queryHistoryData(TableType table , List<Map<String , Object>> idTypeDateList , CallBackExcute callback) throws JBranchException{
		DataAccessManager dam = getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		int idx = 1;
		String dataTypeName = table.getDayColumnName();
		
		StringBuffer queryStr = new StringBuffer();
		GenericMap param = new GenericMap();
		param.put("dam", dam);
		param.put("condition", condition);
		param.put("queryStr", queryStr);
		
		queryStr.append(" select ")
				.append(ArrayUtils.isEmpty(table.getResultColumn()) ? "*" : CollectionSearchUtils.reString(table.getResultColumn(), " , " , false))
				.append(" from ").append(table.getTableName())
				.append(" where ");
		
		param.put("type", "beforeIdTypeDate");
		callback.callBack(param);
		
		queryStr.append(" ( ");
		for(Map idType : idTypeDateList){
			String prdIdIdx = "PRD_ID" + idx;
			String prdTypeIdx = "PRD_TYPE" + idx;
			String dataTypeNameIdx = dataTypeName + idx;
		
			queryStr.append(" prd_id = :").append(prdIdIdx).append(" and ");
			queryStr.append(" prd_type = :").append(prdTypeIdx).append(" and ");
			queryStr.append(dataTypeName).append(" = :").append(dataTypeNameIdx);
			condition.setObject(prdIdIdx , idType.get("PRD_ID"));
			condition.setObject(prdTypeIdx , idType.get("PRD_TYPE"));
			condition.setObject(dataTypeNameIdx , idType.get(table.getDayColumnName()));
			
			param.put("type", "inIdTypeDate");
			callback.callBack(param);
			
			queryStr.append(idx < idTypeDateList.size() ? " or " : "");
			idx++;
		}
		
		queryStr.append(" ) ");
		
		param.put("type", "afterIdTypeDate");
		callback.callBack(param);
		
		System.out.println(queryStr.toString());
		return dam.exeQuery(condition.setQueryString(queryStr.toString()));
	}

	@Override
	public List<Map<String, Object>> queryCommonIntervalList(Map<String, List<String>> groupProdIDMap, List<Map<String, Object>> mfdEtfInsList, int maxMonth, boolean fullYear, String currentYearMonth) throws JBranchException, ParseException {
		return FPSUtils.getCommonIntervalList(this.getDataAccessManager(), groupProdIDMap, mfdEtfInsList, maxMonth, fullYear, currentYearMonth);
	}
	
	@Override
	public List<Map<String, Object>> queryYRateDataResource(Map<String, List<String>> groupProdIDMap, List<Map<String, Object>> mfdEtfInsList, String[] interval) throws JBranchException {
		return FPSUtils.getYRateDataResource(this.getDataAccessManager(), groupProdIDMap, mfdEtfInsList, interval);
	}
}
