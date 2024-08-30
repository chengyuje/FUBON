package com.systex.jbranch.app.server.fps.crm2312;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.server.fps.crm2311.CRM2311;
import com.systex.jbranch.app.server.fps.crm2311.CRM2311InputVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * 俄羅斯特種基金查詢
 * 
 * @author Sam Tu
 * @date 2022/07/07
 * @spec #1195 客戶關係管理→客戶篩選→基金下方，增加項目名稱: 施羅德新興歐洲基金新級別查詢
 * @spec #1340 
 * @spec #1451 
 * 
 */
@Component("crm2312")
@Scope("request")
public class CRM2312 extends FubonWmsBizLogic {

	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(CRM2312.class);

	public void inquire(Object body, IPrimitiveMap header) throws JBranchException {
		CRM2312InputVO inputVO = (CRM2312InputVO) body;
		CRM2312OutputVO outputVO = new CRM2312OutputVO();

		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> changeRateList = xmlInfo.doGetVariable("PRD.FUND_NEW_LEVEL_RATE", FormatHelper.FORMAT_3); // 基金轉換率
		
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT ");
		sql.append("CUST_ID ");
		sql.append(", BRA_NBR ");
		sql.append(", FUND_CODE ");
		sql.append(", NET_VALUE_DATE ");
		sql.append(", CERT_NBR ");
		sql.append(", TOTAL_ACUM_INV_UNIT ");
		sql.append("FROM TBCRM_AST_INV_FUND_OVS_COOL ");
		sql.append("where 1 = 1 ");
		sql.append("and CUST_ID = :cust_ID ");
		if(StringUtils.isNotBlank(inputVO.getCompanyName())) {
			sql.append("and FUND_CODE IN (:FUND_CODE_LIST) ");
			String[] specialFundTempList = xmlInfo.doGetVariable("PRD.FUND_NEW_LEVEL_LIST", FormatHelper.FORMAT_3).get(inputVO.getCompanyName()).toString().split(",");
			List<String> specialFundList = new ArrayList();
			for (String str : specialFundTempList) {
				specialFundList.add(str);
			}
			queryCondition.setObject("FUND_CODE_LIST", specialFundList);
		}

		queryCondition.setObject("cust_ID", inputVO.getCust_ID());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> resultList = dam.exeQuery(queryCondition);
		for(Map map : resultList) {
			
			BigDecimal unit = new BigDecimal(map.get("TOTAL_ACUM_INV_UNIT").toString());
			BigDecimal changeRate = new BigDecimal(changeRateList.get(map.get("FUND_CODE")).toString());
			if(getExactRateList().contains(map.get("FUND_CODE").toString())) {
				map.put("changeRate", changeRate.multiply(new BigDecimal(100)));
			} else {
				map.put("changeRate", String.format("%.03f", changeRate.multiply(new BigDecimal(100)).floatValue()));
			}		
			map.put("changedUnit", unit.multiply(changeRate).setScale(3, RoundingMode.DOWN));
		}
		
		outputVO.setResultList(resultList);
		sendRtnObject(outputVO);
	}
	
	//#1451 有些基金的轉換率要完整顯示
	private List<String> getExactRateList() throws JBranchException {
		XmlInfo xmlInfo = new XmlInfo();
		List<String> tempList = new ArrayList();
		
		try {
			String[] tempArray = xmlInfo.doGetVariable("CRM2312_SPECIAL_SPEC", FormatHelper.FORMAT_3).get("EXACT_RATE_LIST").toString().split(",");
			
			for (String str : tempArray) {
				tempList.add(str);
			}
			return tempList;
		} catch (Exception e) {
			logger.debug("參數CRM2312_SPECIAL_SPEC設定不正確，使CRM2312模組的完整轉換率邏輯無法正確執行");
			return tempList;
		}
		
		
	}

} 
