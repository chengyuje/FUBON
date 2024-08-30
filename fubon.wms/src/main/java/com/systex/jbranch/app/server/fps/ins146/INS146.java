package com.systex.jbranch.app.server.fps.ins146;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ibm.icu.text.SimpleDateFormat;
import com.systex.jbranch.app.server.fps.ins130.INS130;
import com.systex.jbranch.app.server.fps.ins130.INS130InputVO;
import com.systex.jbranch.app.server.fps.ins132.INS132;
import com.systex.jbranch.app.server.fps.insjlb.vo.CalFamilyGapInputVO;
import com.systex.jbranch.app.server.fps.insjlb.vo.CalFamilyGapOutputVO;
import com.systex.jbranch.comutil.collection.GenericMap;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataManager.UUID;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.report.ReportIF;
import com.systex.jbranch.platform.common.report.factory.ReportFactory;
import com.systex.jbranch.platform.common.report.generator.ReportGeneratorIF;
import com.systex.jbranch.platform.common.report.reportdata.ReportData;
import com.systex.jbranch.platform.common.report.reportdata.ReportDataIF;
import com.systex.jbranch.platform.server.conversation.ConversationIF;

@Component("ins146")
@Scope("request")
public class INS146 extends FubonWmsBizLogic{
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(INS146.class);
	
	@Autowired @Qualifier("ins132")
	private INS132 ins132;
	
	public INS132 getIns132() {
		return ins132;
	}

	public void setIns132(INS132 ins132) {
		this.ins132 = ins132;
	}
	
	@Autowired @Qualifier("ins130")
	private INS130 ins130;
	
	public INS130 getIns130() {
		return ins130;
	}

	public void setIns130(INS130 ins130) {
		this.ins130 = ins130;
	}

	public String printReport (String custId, UUID uuid, ConversationIF conversation) throws JBranchException, ParseException {
		dam = this.getDataAccessManager();
		this.conversation = conversation;
		CalFamilyGapInputVO paramsVO = new CalFamilyGapInputVO();
		INS130InputVO inputVO = new INS130InputVO();
		inputVO.setCustId(custId);
		Map<String, Object> custMap = getIns130().getPersionalList(dam, inputVO.getCustId()).get(0);
		paramsVO.setInsCustID(custId);
		paramsVO.setInsCustName((String)custMap.get("CUST_NAME"));
		paramsVO.setInsCustGender((String)custMap.get("GENDER"));
		paramsVO.setInsCustBirthday((Date)custMap.get("BIRTH_DATE"));
		paramsVO.setUuid(uuid);
		
		CalFamilyGapOutputVO calFamilyGapVO = getIns132().getCalFamilyGapVo(paramsVO);
		
		String url = null;
		String txnCode = "INS146";
		String reportID = "R1";
		ReportIF report = null;
		List<String> url_list = new ArrayList<String>();
		ReportFactory factory = new ReportFactory();
		ReportGeneratorIF gen = factory.getGenerator(); // 產出pdf
		ReportDataIF data = new ReportData();
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		
		for(GenericMap dataMap : (List<GenericMap>)calFamilyGapVO.getLstCashFlow()) {
			Map<String, Object> tempMap = dataMap.getParamMap();
			Map<String, Object> outputMap = new HashMap<String, Object>();
			for(Entry<String, Object> dataEntry : tempMap.entrySet()) {
				Object valueObj = dataEntry.getValue();
				if(!"AGE".equals(dataEntry.getKey())) {
					valueObj = valueObj == null ? new BigDecimal(0) : valueObj;
				}
				outputMap.put(dataEntry.getKey(), valueObj); 
			}
			dataList.add(outputMap);
		}
		
		Date now = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("YYYY/MM/dd");
		String now_date = sdf.format(now);
		data.addRecordList("Script Mult Data Set", dataList); 
		data.addParameter("CUSTNAME", (String)custMap.get("CUST_NAME"));
		data.addParameter("rptDate", now_date);
		
		data.setMerge(true);
		
		report = gen.generateReport(txnCode, reportID, data);
		url = report.getLocation();
		return url;
//		url_list.add(url);
//		String reportURL = PdfUtil.mergePDF(url_list,false);
//		
//		Date now = new Date();
//		SimpleDateFormat sdf = new SimpleDateFormat("YYYYMMdd");
//		String now_date = sdf.format(now);
//		notifyClientToDownloadFile(reportURL, now_date+"應被費用計不足表.pdf");
	}
}
