package com.systex.jbranch.app.server.fps.ins147;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ibm.icu.text.SimpleDateFormat;
import com.systex.jbranch.app.server.fps.ins810.INS810;
import com.systex.jbranch.app.server.fps.ins810.INS810InputVO;
import com.systex.jbranch.app.server.fps.ins810.INS810OutputVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataManager.UUID;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.report.ReportIF;
import com.systex.jbranch.platform.common.report.factory.ReportFactory;
import com.systex.jbranch.platform.common.report.generator.ReportGeneratorIF;
import com.systex.jbranch.platform.common.report.reportdata.ReportData;
import com.systex.jbranch.platform.common.report.reportdata.ReportDataIF;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.server.conversation.ConversationIF;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Component("ins147")
@Scope("request")
public class INS147 extends FubonWmsBizLogic{
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(INS147.class);
	
	public String printReport (String custId, UUID uuid, ConversationIF conversation) throws JBranchException, ParseException {
		INS147InputVO inputVO = new INS147InputVO();
		inputVO.setCustID(custId);
		dam = this.getDataAccessManager();
		this.conversation = conversation;
		QueryConditionIF queryCondition = null;
		StringBuilder sb = null;
		
		String url = null;
		String txnCode = "INS147";
		String reportID = "R1";
		ReportIF report = null;
		
		ReportFactory factory = new ReportFactory();
		ReportDataIF data = new ReportData();
		ReportGeneratorIF gen = factory.getGenerator(); // 產出pdf
		
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb = new StringBuilder();
		sb.append(" SELECT cfd.*, ");
		sb.append(" DECODE(cm.CUST_NAME, null, im.CUST_NAME, cm.CUST_NAME) as CUST_NAME, "); 
		sb.append(" DECODE(cm.GENDER, null, im.GENDER, cm.GENDER) as GENDER, ");
		sb.append(" DECODE(cm.BIRTH_DATE, null, im.BIRTH_DATE, cm.BIRTH_DATE) as BIRTH_DATE ");
		sb.append(" FROM TBINS_CUST_FAMILY_DATA cfd "); 
		sb.append(" LEFT JOIN TBCRM_CUST_MAST cm on (cm.CUST_ID = cfd.CUST_ID) ");
		sb.append(" LEFT JOIN TBINS_CUST_MAST im on (im.CUST_ID = cfd.CUST_ID) ");
		sb.append(" WHERE cfd.CUST_ID = :custId ");
		queryCondition.setObject("custId", inputVO.getCustID());
		queryCondition.setQueryString(sb.toString());
		List<Map<String,Object>> lstFamilyData = dam.exeQuery(queryCondition);
		
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb = new StringBuilder();
		sb.append(" SELECT * ");
		sb.append(" FROM TBINS_SPPEDU_DETAIL ");
		sb.append(" WHERE CUST_ID = :custId ");
		queryCondition.setObject("custId", inputVO.getCustID());
		queryCondition.setQueryString(sb.toString());
		List<Map<String,Object>> lstEduDetail = dam.exeQuery(queryCondition);
		if (lstEduDetail.size() > 0) {
			for (Map<String,Object> map : lstEduDetail) {
				map.put("EDU_AMT", getBigDecimal(map.get("EDU_AMT")).divide(new BigDecimal("10000")));
				map.put("CHILD_BIRTHDATE", map.get("CHILD_BIRTHDATE").toString().substring(0,10).replaceAll("-", "/"));
			}
		}
		
		if (lstFamilyData.size() > 0) {
			for (Map<String,Object> map : lstFamilyData) {
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sb = new StringBuilder();
				sb.append(" SELECT PARAM_NAME FROM TBSYSPARAMETER ");
				sb.append(" WHERE PARAM_TYPE = 'CRM.CUST_GENDER' ");
				sb.append(" AND PARAM_CODE = :paramCode ");
				queryCondition.setObject("paramCode", map.get("GENDER"));
				queryCondition.setQueryString(sb.toString());
				List<Map<String,Object>> list = dam.exeQuery(queryCondition);
				map.put("GENDER", list.get(0).get("PARAM_NAME").toString());
				
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sb = new StringBuilder();
				sb.append(" SELECT PARAM_NAME FROM TBSYSPARAMETER ");
				sb.append(" WHERE PARAM_TYPE = 'CRM.CUST_GENDER' ");
				sb.append(" AND PARAM_CODE = :paramCode ");
				queryCondition.setObject("paramCode", map.get("PARTNER_GENDER"));
				queryCondition.setQueryString(sb.toString());
				List<Map<String,Object>> list2 = dam.exeQuery(queryCondition);
				map.put("PARTNER_GENDER", list2.get(0).get("PARAM_NAME").toString());
				
			}
			INS810InputVO ins810inputVO = new INS810InputVO();
			INS810OutputVO ins810outputVO = new INS810OutputVO();
			INS810 ins810 = (INS810) PlatformContext.getBean("ins810");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			
			Date birthday = sdf.parse(lstFamilyData.get(0).get("BIRTH_DATE").toString());			
			ins810inputVO.setBirthday(birthday);
			ins810outputVO = ins810.getAge(ins810inputVO);				
			data.addParameter("AGE", getString(ins810outputVO.getAge()));
			
			Date partnerBirthday = sdf.parse(lstFamilyData.get(0).get("PARTNER_BIRTH_DATE").toString());			
			ins810inputVO.setBirthday(partnerBirthday);
			ins810outputVO = ins810.getAge(ins810inputVO);				
			data.addParameter("PARTNER_AGE", getString(ins810outputVO.getAge()));
			
			data.addParameter("NAME", getString(lstFamilyData.get(0).get("CUST_NAME")));
			data.addParameter("GENDER", getString(lstFamilyData.get(0).get("GENDER")));
			data.addParameter("BIRTHDAY", lstFamilyData.get(0).get("BIRTH_DATE").toString().substring(0,10).replaceAll("-", "/"));
			data.addParameter("PARTNER_NAME", getString(lstFamilyData.get(0).get("PARTNER_NAME")));
			data.addParameter("PARTNER_GENDER", getString(lstFamilyData.get(0).get("PARTNER_GENDER")));
			data.addParameter("PARTNER_BIRTHDAY",lstFamilyData.get(0).get("PARTNER_BIRTH_DATE").toString().substring(0,10).replaceAll("-", "/"));
			
			data.addParameter("LIVING_EXP", getString(lstFamilyData.get(0).get("LIVING_EXP")));
			data.addParameter("NOTLIVING_EXP", getString(lstFamilyData.get(0).get("NOTLIVING_EXP")));
			data.addParameter("CUST_LIVING_FEE", getString(lstFamilyData.get(0).get("CUST_LIVING_FEE")));
			data.addParameter("CHILD_LIVING_FEE", getString(lstFamilyData.get(0).get("CHILD_LIVING_FEE")));
			data.addParameter("HOU_DEBT_AMT", getString(lstFamilyData.get(0).get("HOU_DEBT_AMT")));
			data.addParameter("HOU_DEBT_Y", getString(lstFamilyData.get(0).get("HOU_DEBT_Y")));
			data.addParameter("CAR_DEBT_AMT", getString(lstFamilyData.get(0).get("CAR_DEBT_AMT")));
			data.addParameter("CAR_DEBT_Y", getString(lstFamilyData.get(0).get("CAR_DEBT_Y")));
			data.addParameter("CARD_DEBT_AMT", getString(lstFamilyData.get(0).get("CARD_DEBT_AMT")));
			data.addParameter("CARD_DEBT_Y", getString(lstFamilyData.get(0).get("CARD_DEBT_Y")));
			data.addParameter("STOCK_DEBT_AMT", getString(lstFamilyData.get(0).get("STOCK_DEBT_AMT")));
			data.addParameter("OTHER_DEBT_AMT", getString(lstFamilyData.get(0).get("OTHER_DEBT_AMT")));
			data.addParameter("TAX_IN", getString(lstFamilyData.get(0).get("TAX_IN")));
			data.addParameter("INCOME", getString(lstFamilyData.get(0).get("INCOME")));
			data.addParameter("COU_INCOME", getString(lstFamilyData.get(0).get("COU_INCOME")));
			data.addParameter("RENT_INCOME", getString(lstFamilyData.get(0).get("RENT_INCOME")));
			data.addParameter("CASH_AMT", getString(lstFamilyData.get(0).get("CASH_AMT")));
			data.addParameter("STOCK_AMT", getString(lstFamilyData.get(0).get("STOCK_AMT")));
			data.addParameter("CT_AMT", getString(lstFamilyData.get(0).get("CT_AMT")));
			data.addParameter("FUND_AMT", getString(lstFamilyData.get(0).get("FUND_AMT")));
			data.addParameter("SN_AMT", getString(lstFamilyData.get(0).get("SN_AMT")));
			data.addParameter("INVEST_INS_AMT", getString(lstFamilyData.get(0).get("INVEST_INS_AMT")));
			data.addParameter("SELF_IMMOVE_AMT", getString(lstFamilyData.get(0).get("SELF_IMMOVE_AMT")));
			data.addParameter("INVEST_IMMOVE_AMT", getString(lstFamilyData.get(0).get("INVEST_IMMOVE_AMT")));
			data.addParameter("TRUST_AMT", getString(lstFamilyData.get(0).get("TRUST_AMT")));
		}
		
		if (lstEduDetail.size() > 0) {
			data.addRecordList("Script Mult Data Set1", lstEduDetail);
			data.addParameter("eduList", "1");
		} else {
			data.addParameter("eduList", "0");
		}
		data.setMerge(true);
		
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String dateStr = sdf.format(date);
		data.addParameter("rptDate", dateStr);
		
		report = gen.generateReport(txnCode, reportID, data);
		url = report.getLocation();
		
		return url;
	}
	
	private String getString(Object val){
		if(val == null){
			return "";
		}else{
			return val.toString();
		}
	}
	
	private BigDecimal getBigDecimal(Object val){
		if(val == null){
			return new BigDecimal(0);
		}else{
			return new BigDecimal(val.toString());
		}
	}
}
