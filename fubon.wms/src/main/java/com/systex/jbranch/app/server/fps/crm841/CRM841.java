package com.systex.jbranch.app.server.fps.crm841;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.fubon.commons.cbs.CBSService;
import com.systex.jbranch.fubon.commons.cbs.service.SC120100Service;
import com.systex.jbranch.fubon.commons.esb.vo.sc120100.SC120100DetailOutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.sc120100.SC120100OutputVO;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.serialnumber.SerialNumberUtil;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Component("crm841")
@Scope("request")
public class CRM841 extends FubonWmsBizLogic {
	
	@Autowired
	private CBSService cbsservice;
	
	@Autowired
	private SC120100Service sc120100Service;

	public DataAccessManager dam = null;

	public void inquire(Object body, IPrimitiveMap header) throws Exception {
		
		CRM841OutputVO outputVO = new CRM841OutputVO();
		CRM841InputVO inputVO = (CRM841InputVO) body;
		dam = this.getDataAccessManager();	
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);		
		StringBuffer sb = new StringBuffer();	
		
		//發送電文
		List<SC120100OutputVO> vos = sc120100Service.searchInstallment(inputVO.getCust_id().trim());
		List<SC120100DetailOutputVO> results = new ArrayList<>();

		for (SC120100OutputVO sc120100OutputVO : vos) {

			List<SC120100DetailOutputVO> details = sc120100OutputVO.getDetails();
			details = (CollectionUtils.isEmpty(details)) ? new ArrayList<SC120100DetailOutputVO>() : details;

			for (SC120100DetailOutputVO data : details) {
				data.setACNO(cbsservice.checkAcctLength(data.getACNO()));
				data.setACNO_SA(cbsservice.checkAcctLength(data.getACNO_SA()));
				data.setACT_BAL_NT(cbsservice.amountFormat(data.getACT_BAL_NT(), 0));
				data.setORI_LOAN_BAL(cbsservice.amountFormat(data.getORI_LOAN_BAL()));
				data.setDUE_DATE(cbsservice.changeDateView(data.getDUE_DATE(), "3"));
				
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);		
				sb = new StringBuffer();	
				sb.append("SELECT ACHIVE_ORG_NM FROM TBCRM_LOAN_CASE WHERE ACNO_LN = :ACNO_LN ");
				queryCondition.setQueryString(sb.toString());
				queryCondition.setObject("ACNO_LN", data.getACNO());
				List<Map<String, Object>> list = dam.exeQuery(queryCondition);
				
				if (list.size() > 0) {
					data.setACHIVE_ORG_NM((String) list.get(0).get("ACHIVE_ORG_NM"));
				}
				
				results.add(data);
			}
		}

		outputVO.setResultList(results);
		this.sendRtnObject(outputVO);
	}

	public String getSeq_Num() throws JBranchException {

		SerialNumberUtil sn = new SerialNumberUtil();
		String seqNum = "";
		Timestamp ts = new Timestamp(System.currentTimeMillis());

		try {
			seqNum = String.format("%06d", Integer.valueOf(sn.getNextSerialNumber("CRM840")));

		} catch (Exception e) {
			sn.createNewSerial("CRM840", "000000", 1, "d", ts, 1, new Long("999999"), "y", new Long("0"), null);
			seqNum = String.format("%06d", Integer.valueOf(sn.getNextSerialNumber("CRM840")));
		}

		return seqNum;
	}
}