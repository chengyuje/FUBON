package com.systex.jbranch.app.server.fps.crm847;

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.fubon.commons.cbs.CBSService;
import com.systex.jbranch.fubon.commons.cbs.service.SC120100Service;
import com.systex.jbranch.fubon.commons.esb.vo.sc120100.SC120100DetailOutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.sc120100.SC120100OutputVO;
import com.systex.jbranch.platform.util.IPrimitiveMap;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang.StringUtils.defaultString;
import static org.apache.commons.lang.StringUtils.trim;

/**
 * @author walalala
 * @date 2016/11/21
 * 
 */
@Component("crm847")
@Scope("request")
public class CRM847 extends FubonWmsBizLogic {
	@Autowired
    private CBSService cbsservice;
	@Autowired
	private SC120100Service sc120100Service;

	public void inquire(Object body, IPrimitiveMap header) throws Exception {
		CRM847OutputVO return_VO = new CRM847OutputVO();
		CRM847InputVO inputVO = (CRM847InputVO) body;
		//發送電文
		List<SC120100OutputVO> vos = sc120100Service.searchMortgage(inputVO.getCust_id().trim());

		System.out.println("測試成功!");
		List<SC120100DetailOutputVO> results = new ArrayList<>();
		List<SC120100DetailOutputVO> results1 = new ArrayList<>();

		for(SC120100OutputVO sc120100OutputVO : vos) {
			List<SC120100DetailOutputVO> details = sc120100OutputVO.getDetails();
			details = (CollectionUtils.isEmpty(details)) ? new ArrayList<SC120100DetailOutputVO>() : details;

			for (SC120100DetailOutputVO data : details) {
				String acctType = defaultString(trim(data.getWA_X_ATYPE()));
				//存單質借
				if (cbsservice.isMortgage1(acctType)) {
					data.setACT_BAL_NT(cbsservice.amountFormat(data.getACT_BAL_NT(),0));
					data.setORI_LOAN_BAL(cbsservice.amountFormat(data.getORI_LOAN_BAL(),0));
					data.setDUE_DATE(cbsservice.changeDateView(data.getDUE_DATE(),"3"));
					data.setINT_CYCLE(cbsservice.changeDateView(data.getINT_CYCLE(),"2"));
					data.setRATE(cbsservice.amountFormat(data.getRATE(),7));
					results.add(data);	
				}
				//信託質借 
				else if(cbsservice.isMortgage2(acctType)){
					data.setACT_BAL_NT(cbsservice.amountFormat(data.getACT_BAL_NT(),0));
					data.setORI_LOAN_BAL(cbsservice.amountFormat(data.getORI_LOAN_BAL(),0));
					data.setDUE_DATE(cbsservice.changeDateView(data.getDUE_DATE(),"3"));
					data.setINT_CYCLE(cbsservice.changeDateView(data.getINT_CYCLE(),"2"));
					data.setRATE(cbsservice.amountFormat(data.getRATE(),7));
					results1.add(data);	
				}
			     
				
			}
		}
		
		return_VO.setResultList(results);	
		return_VO.setResultList1(results1);
		
		this.sendRtnObject(return_VO);
	}    	
}