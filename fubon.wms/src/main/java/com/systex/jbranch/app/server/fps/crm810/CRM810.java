package com.systex.jbranch.app.server.fps.crm810;

import com.systex.jbranch.common.xmlInfo.XMLInfo;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.fubon.commons.cbs.CBSService;
import com.systex.jbranch.fubon.commons.cbs.service.WMS032675Service;
import com.systex.jbranch.fubon.commons.cbs.vo.basic.CBSUtilOutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.wms032675.WMS032675OutputDetailsVO;
import com.systex.jbranch.fubon.commons.esb.vo.wms032675.WMS032675OutputVO;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.util.StringUtil;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author walalala
 * @date 2016/12/06
 * 
 */
@Component("crm810")
@Scope("request")
public class CRM810 extends FubonWmsBizLogic {
	@Autowired
	private CBSService cbsservice;
	@Autowired
	private WMS032675Service wms032675Service;

	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(CRM810.class);

	public void inquire(Object body, IPrimitiveMap header) throws Exception {
		CRM810OutputVO return_VO = new CRM810OutputVO();
		CRM810InputVO inputVO = (CRM810InputVO) body;

		String custID = inputVO.getCust_id();
		if (StringUtils.isNotBlank(custID)) {

			try {
				Map<String, BigDecimal> currencyMap = new HashMap();
				XMLInfo xmlinfo = new XMLInfo();
				HashMap<String, BigDecimal> ex_map = xmlinfo.getExchangeRate();

				BigDecimal crm811_amt = new BigDecimal("0");
				BigDecimal crm812_amt = new BigDecimal("0");
				BigDecimal crm813_amt = new BigDecimal("0");

				List<CBSUtilOutputVO> acctData = inputVO.getAcctData();
				List<WMS032675OutputVO> wms032675Data = acctData == null ?
						wms032675Service.searchAcct(custID) :
						wms032675Service.filterAcctData(acctData);

				for (WMS032675OutputVO wms032675OutputVO: wms032675Data) {
					List<WMS032675OutputDetailsVO> details = wms032675OutputVO.getDetails();
					details = (CollectionUtils.isEmpty(details)) ? new ArrayList<WMS032675OutputDetailsVO>() : details;

					for (WMS032675OutputDetailsVO data : details) {
						if (StringUtils.isNotBlank(data.getTOTAL_SUM()) || StringUtils.isNotBlank(data.getAVAILABLE_AMT_BAL())) {

							//全撈所以有些值是外幣，都轉成台幣
							String currency = StringUtils.trim(data.getCURRENCY());
							if (data.getBUSINESS_CODE().equals("1")) { // 活存
								BigDecimal totalSum = exchangeToTWD(ex_map, data.getTOTAL_SUM(), currency);
								crm811_amt = crm811_amt.add(totalSum);
								statisticsOfCurrency(currencyMap, currency, totalSum);
							}
							if (data.getBUSINESS_CODE().equals("2")) { // 支存
								BigDecimal totalSum = exchangeToTWD(ex_map, data.getTOTAL_SUM(), currency);
								crm812_amt = crm812_amt.add(totalSum);
								statisticsOfCurrency(currencyMap, currency, totalSum);
							}
							if (data.getBUSINESS_CODE().equals("3")) { // 定存
								BigDecimal avalSum = exchangeToTWD(ex_map, data.getAVAILABLE_AMT_BAL(), currency);
								crm813_amt = crm813_amt.add(avalSum);
								statisticsOfCurrency(currencyMap, currency, avalSum);
							}
						}
					}
				}

				return_VO.setCrm811_amt(crm811_amt);
				return_VO.setCrm812_amt(crm812_amt);
				return_VO.setCrm813_amt(crm813_amt);
				return_VO.setNo_cur_list(new ArrayList());
				return_VO.setCur_list(currencyMap);
			} catch (Exception e) {
				logger.debug("發送存款總覽電文失敗:客戶ID=" + custID);
				logger.debug("ESB error:=wms032675" + StringUtil.getStackTraceAsString(e));
			}

		} else {
			throw new APException("未傳入客戶ID，請重新操作。");
		}
		this.sendRtnObject(return_VO);
	}

	private void statisticsOfCurrency(Map<String, BigDecimal> currencyMap, String currency, BigDecimal money) {
		if (!currency.equals("XXX") && currencyMap.containsKey(currency)) {
			currencyMap.put(currency, currencyMap.get(currency).add(money));
		} else {
			currencyMap.put(currency, money);
		}
	}

	private BigDecimal exchangeToTWD(HashMap<String, BigDecimal> exMap, String money, String currency) {
		return new BigDecimal(cbsservice.amountFormat(money))
				.multiply(exMap.get(currency)).setScale(0, BigDecimal.ROUND_HALF_UP);
	}

}