package com.systex.jbranch.app.server.fps.crm812;


import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.fubon.commons.cbs.CBSService;
import com.systex.jbranch.fubon.commons.cbs.service.WMS032675Service;
import com.systex.jbranch.fubon.commons.esb.EsbFmpJRunConfiguer;
import com.systex.jbranch.fubon.commons.esb.vo.wms032675.WMS032675OutputDetailsVO;
import com.systex.jbranch.fubon.commons.esb.vo.wms032675.WMS032675OutputVO;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.util.IPrimitiveMap;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author walalala
 * @date 2016/12/06
 * 
 */
@Component("crm812")
@Scope("request")
public class CRM812 extends FubonWmsBizLogic {
	@Autowired
    private CBSService cbsService;
	@Autowired
	private WMS032675Service wms032675Service;
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(CRM812.class);
	/* const */
    private String ESB_TYPE = EsbFmpJRunConfiguer.ESB_TYPE;
    private String thisClaz = this.getClass().getSimpleName() + ".";
    private CBSService cbsservice = new CBSService();
    
	public void inquire(Object body, IPrimitiveMap header) throws Exception {
		CRM812OutputVO return_VO = new CRM812OutputVO();
		CRM812InputVO inputVO = (CRM812InputVO) body;

		List<WMS032675OutputVO> vos = wms032675Service.searchCheckTW(inputVO.getCust_id());

        List<WMS032675OutputDetailsVO> results = new ArrayList<WMS032675OutputDetailsVO>();
        
        for(WMS032675OutputVO wms032675OutputVO : vos) {
            List<WMS032675OutputDetailsVO> details = wms032675OutputVO.getDetails();
            details = (CollectionUtils.isEmpty(details)) ? new ArrayList<WMS032675OutputDetailsVO>() : details;
            for (WMS032675OutputDetailsVO datas : details) {

            	if (!StringUtils.isBlank(datas.getCURRENT_AMT_BAL())) {
					datas.setCURRENT_AMT_BAL(new BigDecimal (cbsservice.amountFormat(datas.getCURRENT_AMT_BAL())).toString());
				}
            	if (!StringUtils.isBlank(datas.getAVAILABLE_AMT_BAL())) {
					datas.setAVAILABLE_AMT_BAL(new BigDecimal (cbsservice.amountFormat(datas.getAVAILABLE_AMT_BAL())).toString());
				}
            	
            	if (!StringUtils.isBlank(datas.getACCT_OPEN_DATE())) {
					datas.setACCT_OPEN_DATE(cbsService.changeDateView(datas.getACCT_OPEN_DATE(),"3"));
				}
				if (!StringUtils.isBlank(datas.getALTERATION())) {
					datas.setALTERATION(datas.getALTERATION().replace("/", ""));
					if(datas.getALTERATION().equals("99999999")){
						datas.setALTERATION("久未異動");
					} else{
						datas.setALTERATION(cbsService.changeDateView(datas.getALTERATION(),"2"));
					}
				}
            	datas.setBRANCH_NBR(datas.getBRANCH_NBR().trim());
            	results.add(datas);
            }
        }
        
        return_VO.setResultList(results);
		
		this.sendRtnObject(return_VO);
	}        

}