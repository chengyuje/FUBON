package com.systex.jbranch.common.xmlInfo;

import java.math.BigDecimal;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Repository("xmlinfo")
@Scope("prototype")
@Component
public class XMLInfo extends FubonWmsBizLogic {
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	DataAccessManager dam_obj = null;
	
	private Date ex_date; //最新匯率日期
	
	public void getXMLInfo(Object body, IPrimitiveMap<Object> header)
			throws JBranchException {
		
		XMLInfoInputVO inputVO = (XMLInfoInputVO) body;
		XMLInfoOutputVO outputVO = new XMLInfoOutputVO();
		List<Map<String, Object>> xmlInfoList = new ArrayList<Map<String, Object>>();
		List<Object> data_lst = new ArrayList<Object>();
		
		List<?> xmlList = inputVO.getXmlInfoList();
		
		dam_obj = PlatformContext.getBean(DataAccessManager.class); // this.getDataAccessManager();
		
		for (int i = 0; i < xmlList.size(); i++) {
			List<Map<String, Object>> result_lst = new ArrayList<Map<String, Object>>();
			QueryConditionIF ratio_qc = dam_obj
					.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

			StringBuilder sb = new StringBuilder();
			sb.append("SELECT PARAM_CODE as DATA,PARAM_NAME as LABEL FROM TBSYSPARAMETER WHERE PARAM_TYPE =:paramType");
			
			if (xmlList.get(i) instanceof ArrayList<?>) {
				List<?> list = (List<?>) xmlList.get(i);
				
				if (list.size() > 1) {
					sb.append(" AND PARAM_CODE =:paramCode");
					ratio_qc.setObject("paramCode", list.get(1));
				}
				
				ratio_qc.setObject("paramType", list.get(0));
			} else {
				ratio_qc.setObject("paramType", xmlList.get(i));
			}
			
			sb.append(" ORDER BY PARAM_ORDER");
			ratio_qc.setQueryString(sb.toString());
			result_lst = dam_obj.exeQuery(ratio_qc);
			data_lst.add(result_lst);
		}
		
		Map<String, Object> bigMap = new HashMap<String, Object>();
		bigMap.put("key", (Object)xmlList);
		bigMap.put("data", (Object)data_lst);
		xmlInfoList.add(bigMap);
		outputVO.setXmlInfoList(xmlInfoList);
		sendRtnObject(outputVO);
	}
	public List getXMLInfo(List<String> pTypeList)	throws JBranchException {
		dam_obj = PlatformContext.getBean(DataAccessManager.class); // this.getDataAccessManager();
		QueryConditionIF queryCondition = dam_obj
				.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT PARAM_CODE as DATA,PARAM_NAME as LABEL FROM TBSYSPARAMETER WHERE PARAM_TYPE IN (:paramType)");
		
		queryCondition.setObject("paramType", pTypeList);
		queryCondition.setQueryString(sb.toString());
		List<Map<String, Object>> resultList = dam_obj.exeQuery(queryCondition);
		
		return resultList;
	}
	
	public void getTableInfo(Object body, IPrimitiveMap<Object> header)
			throws JBranchException {
		
		XMLInfoInputVO inputVO = (XMLInfoInputVO) body;
		XMLInfoOutputVO outputVO = new XMLInfoOutputVO();
				
		dam_obj = PlatformContext.getBean(DataAccessManager.class); // this.getDataAccessManager();
		
//		for(int i=0;i<inputVO.getXmlInfoList().size();i++){
			List<Map<String, Object>> result_lst = new ArrayList<Map<String, Object>>();
			QueryConditionIF ratio_qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			
			StringBuilder sb = new StringBuilder();
			sb.append(" SELECT ").append(inputVO.getFieldValue()).append(" as DATA,");
			sb.append(" ").append(inputVO.getFieldLabel()).append(" as LABEL ");
			sb.append(" FROM ").append(inputVO.getTableName());
			ratio_qc.setQueryString(sb.toString());
			outputVO.setXmlInfoList(dam_obj.exeQuery(ratio_qc));
//		}
		sendRtnObject(outputVO);
	}
	
	public HashMap<String,BigDecimal> getExchangeRate()
				throws JBranchException,Exception {
		    dam_obj = PlatformContext.getBean(DataAccessManager.class); // this.getDataAccessManager();
		
			List<Map<String, Object>> result_lst = new ArrayList<Map<String, Object>>();
			QueryConditionIF ratio_qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			
			StringBuilder sb = new StringBuilder();
			sb.append(" SELECT to_char(MTN_DATE,'yyyy-MM-dd') as MTN_DATE,CUR_COD,BUY_RATE,SEL_RATE,BUY_RATE EX_RATE " //用買價
					+ "FROM TBPMS_IQ053 WHERE MTN_DATE = (SELECT MAX(MTN_DATE) FROM TBPMS_IQ053) ");
			
			ratio_qc.setQueryString(sb.toString());
			result_lst = dam_obj.exeQuery(ratio_qc);
			
			HashMap<String,BigDecimal> ex_map = new HashMap<String,BigDecimal>();
			SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
			if(!result_lst.isEmpty()){
				for(Map<String,Object> map:result_lst){
					ex_map.put((String)map.get("CUR_COD"),(BigDecimal)map.get("EX_RATE"));
					ex_date = sdf1.parse((String)map.get("MTN_DATE"));
				}
			}
			ex_map.put("TWD", new BigDecimal(1));
			ex_map.put("NTD", new BigDecimal(1));
			return ex_map;
	}
	
	/**
	 * 傳入錯誤代碼回傳錯誤訊息
	 * @param errorCode
	 * @return
	 * @throws DAOException
	 * @throws JBranchException
	 */
	public String getErrorMsg(String errorCode) throws DAOException, JBranchException {
		dam_obj = PlatformContext.getBean(DataAccessManager.class); // this.getDataAccessManager();
		QueryConditionIF ratio_qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		List<Map<String, Object>> result_lst = new ArrayList<Map<String, Object>>();
		String msg = "";
		
		ratio_qc.setQueryString(" SELECT TEXT FROM TBSYSI18N WHERE CODE = :code ");
		ratio_qc.setObject("code", errorCode);
		result_lst = dam_obj.exeQuery(ratio_qc);
		
		if(CollectionUtils.isNotEmpty(result_lst)) {
			String txt = (String) result_lst.get(0).get("TEXT");
			msg = StringUtils.isBlank(txt) ? "" : txt.trim();
		}
		
		return msg;
	}
	
	@Override
	public DataAccessManager getDataAccessManager() {
		try {
			return (DataAccessManager)PlatformContext.getBean("dataAccessManager");
		} catch (JBranchException e) {
		   return null;
		}
	}
	
	/***
	 * 回傳PARAM_CODE, PARAM_NAME列表，以PARAM_ORDER排序
	 * @param paramType
	 * @return
	 * @throws JBranchException
	 */
	public List<Map<String, Object>> getXMLInfo(String paramType) throws JBranchException {
		dam_obj = PlatformContext.getBean(DataAccessManager.class); // this.getDataAccessManager();
		QueryConditionIF qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT PARAM_CODE, PARAM_NAME FROM TBSYSPARAMETER WHERE PARAM_TYPE =:paramType ORDER BY PARAM_ORDER ");
		qc.setObject("paramType", paramType);
		qc.setQueryString(sb.toString());

		return dam_obj.exeQuery(qc);
	}
}
