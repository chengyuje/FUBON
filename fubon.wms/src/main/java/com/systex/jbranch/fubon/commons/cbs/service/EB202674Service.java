package com.systex.jbranch.fubon.commons.cbs.service;

import com.systex.jbranch.fubon.commons.cbs.dao._000454_032081DAO;
import com.systex.jbranch.fubon.commons.cbs.vo._000454_032081.CBS032081OutputDetailsVO;
import com.systex.jbranch.fubon.commons.cbs.vo.basic.CBSUtilOutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.eb202674.EB202674OutputDetailsVO;
import com.systex.jbranch.fubon.commons.esb.vo.eb202674.EB202674OutputVO;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static org.apache.commons.lang.StringUtils.*;

/**
 * 此服務將代替原本的 EB202674 ESB 電文 （Note: 原本的 EB202674 ESB 電文改走 CBS 路線）
 */
@Service
public class EB202674Service {
	@Autowired
	private _000454_032081DAO _000454_032081dao;

	/**
	 * 查詢客戶的一本萬利資料
	 * 
	 * @param acctId
	 *            存款帳號
	 * @param start
	 *            起日
	 * @param end
	 *            迄日
	 */
	public EB202674OutputVO search(String acctId, String start, String end, String txType, String dateCheck) throws Exception {
		SimpleDateFormat sdf2 = new SimpleDateFormat("ddMMyyyy");
		//第二個邏輯 要起訖日相同"並且"hd00070000有查到東西 使dateCheck!=null
		if (sdf2.parse(start).after(sdf2.parse(end)) || (start.equals(end) && StringUtils.isNotBlank(dateCheck))) {
			return null;
		}
		List<CBSUtilOutputVO> data = _000454_032081dao.search(acctId, start, end, txType);
		if (isEmpty(data))
			return null;

		List ebDetails = new ArrayList();
		
		for (CBSUtilOutputVO cbsoutputVO : data) {
			for (CBS032081OutputDetailsVO cbsDetail : cbsoutputVO.getCbs032081OutputVO().getDetails()) {
				if (isBlank(cbsDetail.getBAL()))
					continue;

				ebDetails.add(_000454_032081Transfer(cbsDetail));
			}
		}

		EB202674OutputVO output = new EB202674OutputVO();
		output.setDetails(ebDetails);

		return output;
	}

	/**
	 * 規格書轉換 _000454_032081 detail to EB202674 detail
	 **/
	private EB202674OutputDetailsVO _000454_032081Transfer(CBS032081OutputDetailsVO cbs) {
		EB202674OutputDetailsVO esb = new EB202674OutputDetailsVO();
		esb.setACT_DATE(cbs.getPOST_DATE());
		esb.setTX_DATE(cbs.getSYS_DATE());
		esb.setTX_TIME(cbs.getSYS_TIME());
		esb.setMEMO(cbs.getTRN_DESP());
		if ("D".equals(cbs.getTRN_SIGN())) {
			esb.setCR_TXT(getFirstNARR(cbs.getNARR()));
			esb.setDR_TXT(substring(cbs.getAMT(), 17, 18) + substring(cbs.getAMT(), 0, 17));
		} else if ("C".equals(cbs.getTRN_SIGN())) {
			esb.setCR_TXT(substring(cbs.getAMT(), 17, 18) + substring(cbs.getAMT(), 0, 17));
			esb.setDR_TXT(getFirstNARR(cbs.getNARR()));
		}

		esb.setPB_BAL(substring(cbs.getBAL(), 17, 18) + substring(cbs.getBAL(), 0, 17));
		esb.setTX_BRH(cbs.getTRN_BRCH());
		esb.setTX_TYPE1(cbs.getJRNL_NO());
		esb.setTRUST_NO(cbs.getTRN_SIGN());
		esb.setCUR(trim(cbs.getSUB_CURRENCY()));
	    esb.setRMK(cbs.getTRN_BK_ID() + cbs.getTRN_BRCH()); //先加入銀行別+分行別，到CRM814.java再來處理
		return esb;
	}

	private String getFirstNARR(String NARR) {
		String[] list = NARR.split(" ");
		for (int i = 0; i < list.length; i++) {
			if (StringUtils.isNotBlank(list[i])) {
				return list[i];
			}
		}
		return NARR;
	}
}
