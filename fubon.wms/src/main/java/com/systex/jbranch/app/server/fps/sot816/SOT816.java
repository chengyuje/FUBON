package com.systex.jbranch.app.server.fps.sot816;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBCRM_TRS_APL_SEQ_DTLVO;
import com.systex.jbranch.app.server.fps.crm341.CRM341InputVO;
import com.systex.jbranch.app.server.fps.sot712.PRDFitInputVO;
import com.systex.jbranch.app.server.fps.sot712.SotPdf;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.report.ReportIF;
import com.systex.jbranch.platform.common.report.factory.ReportFactory;
import com.systex.jbranch.platform.common.report.generator.ReportGeneratorIF;
import com.systex.jbranch.platform.common.report.reportdata.ReportData;
import com.systex.jbranch.platform.common.report.reportdata.ReportDataIF;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * MENU
 * 
 * @author Allen
 * @date 2018/09/12
 * @spec null
 */
@Component("sot816")
@Scope("request")
public class SOT816 extends SotPdf {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(SOT816.class);

	@Override
	public List<String> printReport() throws JBranchException {
		List<String> result_list = new ArrayList<String>();
		String url = null;
		String txnCode = "SOT816";
		String reportID = "R1";
		ReportIF report = null;

		ReportFactory factory = new ReportFactory();
		ReportDataIF data = new ReportData();
		ReportGeneratorIF gen = factory.getGenerator(); // 產出pdf
		
		List<Map<String, Object>> totalList = new ArrayList<Map<String, Object>>();
		PRDFitInputVO inputVO = getInputVO();
		
		try {
			int caseCode = inputVO.getCaseCode();
			
			if (caseCode == 1) {
				List<Map<String, Object>> list = getSqlResultList(inputVO);

				if(CollectionUtils.isEmpty(list) || list.size() == 0){
					//沒資料，不列印報表
					return new ArrayList<String>();
				}else{
					for(Map<String, Object> map : list){
						HashMap<String, Object> addMap = new HashMap<String, Object>();
						addMap.put("PROD_ID", checkIsNull(map, "PROD_ID"));
						addMap.put("PROD_NAME", checkIsNull(map, "PROD_NAME"));
						addMap.put("CUST_ID", checkIsNull(map, "CUST_ID"));
						totalList.add(addMap);
					}
				}
			}else if (caseCode == 2) {
				HashMap<String, Object> addMap = new HashMap<String, Object>();
				addMap.put("PROD_ID", inputVO.getPrdId());
				addMap.put("PROD_NAME", inputVO.getPrdName());
				addMap.put("CUST_ID", inputVO.getCustId());
				totalList.add(addMap);
			}
			
			
			if(CollectionUtils.isNotEmpty(totalList) || totalList.size() > 0){
				data.addRecordList("Script Mult Data Set", totalList);
			}else{
				//沒資料，不列印報表
				return new ArrayList<String>();
			}

			report = gen.generateReport(txnCode, reportID, data);
			url = report.getLocation();
			result_list.add(url);

			return result_list;
			
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}
	
	/**
	 * 檢查Map取出欄位是否為Null
	 * 
	 * @param map
	 * @return String
	 */
	private String checkIsNull(Map map, String key) {
		if (StringUtils.isNotBlank(String.valueOf(map.get(key))) && map.get(key) != null) {
			return String.valueOf(map.get(key));
		} else {
			return "";
		}
	}
	
	public List<Map<String, Object>> getSqlResultList(PRDFitInputVO inputVO) throws DAOException, JBranchException{

		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		if(StringUtils.equals("FCI", inputVO.getPrdType())) {
			sql.append(" select ");
			sql.append(" 	M.CUST_ID,   ");
			sql.append(" 	D.BATCH_SEQ AS PROD_ID,   ");
			sql.append(" 	(C.PARAM_NAME || D.PROD_NAME) AS PROD_NAME ");
			sql.append(" from TBSOT_FCI_TRADE_D D ");
			sql.append(" LEFT JOIN TBSOT_TRADE_MAIN M on M.TRADE_SEQ = D.TRADE_SEQ ");
			sql.append(" LEFT JOIN TBSYSPARAMETER C ON C.PARAM_TYPE = 'PRD.FCI_CURRENCY' AND C.PARAM_CODE = D.PROD_CURR ");
			sql.append(" where D.TRADE_SEQ = :tradeSeq ");
		} else {
			sql.append(" select ");
			sql.append(" 	M.CUST_ID,   ");
			sql.append(" 	D.PROD_ID,   ");
			sql.append(" 	D.PROD_NAME  ");
			sql.append(" from TBSOT_SI_TRADE_D D ");
			sql.append(" 	inner join TBSOT_TRADE_MAIN M on M.TRADE_SEQ = D.TRADE_SEQ ");
			sql.append(" where D.TRADE_SEQ = :tradeSeq ");
		}
		
		queryCondition.setObject("tradeSeq", inputVO.getTradeSeq());
		queryCondition.setQueryString(sql.toString());
		
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		
		return list;
	}
}