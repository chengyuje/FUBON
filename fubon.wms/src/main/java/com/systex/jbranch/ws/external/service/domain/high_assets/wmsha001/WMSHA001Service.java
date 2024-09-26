package com.systex.jbranch.ws.external.service.domain.high_assets.wmsha001;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.app.server.fps.crm810.CRM810;
import com.systex.jbranch.common.xmlInfo.XMLInfo;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.fubon.commons.cbs.CBSService;
import com.systex.jbranch.fubon.commons.cbs.service.WMS032675Service;
import com.systex.jbranch.fubon.commons.esb.vo.wms032675.WMS032675OutputDetailsVO;
import com.systex.jbranch.fubon.commons.esb.vo.wms032675.WMS032675OutputVO;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("request")
public class WMSHA001Service extends FubonWmsBizLogic {
	
	@Autowired
	private CBSService cbsservice;
	@Autowired
	private WMS032675Service wms032675Service;
	
	private Logger logger = LoggerFactory.getLogger(WMSHA001Service.class);

    public WMSHA001OutputVO search(WMSHA001InputVO inputVO) throws JBranchException, Exception {
    	WMSHA001OutputVO outputVO = new WMSHA001OutputVO();
		String custID = inputVO.getCustId();


		if (StringUtils.isBlank(custID))
			throw new APException("未傳入客戶ID，請重新操作。");

		String defaultAmt = cbsservice.padLeft("0", 16, "0");
		try {
			Map<String, BigDecimal> currencyMap = new HashMap();
			XMLInfo xmlinfo = new XMLInfo();
			HashMap<String, BigDecimal> ex_map = xmlinfo.getExchangeRate();

			BigDecimal sum = new BigDecimal("0");
			BigDecimal temp = new BigDecimal("0");
			String[] array = {"1", "2", "3"};
			List<WMS032675OutputVO> wms032675Data = wms032675Service.searchAcctForWMSHA001(custID);

			for (WMS032675OutputVO wms032675OutputVO : wms032675Data) {
				List<WMS032675OutputDetailsVO> details = wms032675OutputVO.getDetails();
				details = (CollectionUtils.isEmpty(details)) ? new ArrayList<WMS032675OutputDetailsVO>() : details;

				for (WMS032675OutputDetailsVO data : details) {
					if (StringUtils.isNotBlank(data.getTOTAL_SUM()) || StringUtils.isNotBlank(data.getAVAILABLE_AMT_BAL())) {


						//全撈所以有些值是外幣，都轉成台幣
						if (ArrayUtils.contains(array, data.getBUSINESS_CODE())) {
							String currency = StringUtils.trim(data.getCURRENCY());
							temp = exchangeToTWD(ex_map, data.getAVAILABLE_AMT_BAL(), currency);
							sum = sum.add(temp);
						}

//							//全撈所以有些值是外幣，都轉成台幣
//							String currency = StringUtils.trim(data.getCURRENCY());
//							if (data.getBUSINESS_CODE().equals("1")) { // 活存
//								temp = exchangeToTWD(ex_map, data.getTOTAL_SUM(), currency);
//								sum = sum.add(temp);
//							}
//							if (data.getBUSINESS_CODE().equals("2")) { // 支存
//								temp = exchangeToTWD(ex_map, data.getTOTAL_SUM(), currency);
//								sum = sum.add(temp);
//							}
//							if (data.getBUSINESS_CODE().equals("3")) { // 定存
//								temp = exchangeToTWD(ex_map, data.getAVAILABLE_AMT_BAL(), currency);
//								sum = sum.add(temp);
//							}
					}
				}
			}
			outputVO.setDenoAmt1(cbsservice.padLeft(sum.toString(),16,"0"));
		} catch (Exception e) {
			// API 不拋出任何錯誤，仍然返回預設值。
			outputVO.setDenoAmt1(defaultAmt);
			logger.error(e.toString());
		}
		outputVO.setCustId(cbsservice.padRight(custID,11," "));
		outputVO.setDenoAmt2(defaultAmt);
		outputVO.setNumeAmt1(defaultAmt);
		outputVO.setNumeAmt2(defaultAmt);

		return outputVO;
    }
    
	private BigDecimal exchangeToTWD(HashMap<String, BigDecimal> exMap, String money, String currency) {
		return new BigDecimal(cbsservice.amountFormat(money))
				.multiply(exMap.get(currency)).setScale(0, BigDecimal.ROUND_HALF_UP);
	}
}
