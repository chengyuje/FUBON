package com.systex.jbranch.app.server.fps.crm860;

import com.systex.jbranch.app.server.fps.crm822.CRM822;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.fubon.commons.esb.vo.ESBUtilOutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.nrbrq01.NRBRQ01OutputDetailsVO;
import com.systex.jbranch.fubon.commons.esb.vo.nrbrq01.NRBRQ01OutputVO;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataSortManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.util.IPrimitiveMap;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;

@Component("crm860")
@Scope("request")
public class CRM860 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(CRM860.class);

	public void inquire(Object body, IPrimitiveMap header) throws Exception {
		CRM860InputVO inputVO = (CRM860InputVO) body;
		CRM860OutputVO return_VO = new CRM860OutputVO();
		dam = this.getDataAccessManager();

		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql_query = new StringBuffer();
		StringBuffer sql_group = new StringBuffer();
		StringBuffer sql = new StringBuffer();

		if(StringUtils.isNotBlank(inputVO.getPrdType()) && StringUtils.equals("03", inputVO.getPrdType())) {
			//海外債: PD22-海外債(到期), PD23-海外債(贖回)
			sql_query.append("SELECT A.*, C.TXN_DIVID_ORGD, B.TXN_DIVID_TWD, ");
			sql_query.append(" (NVL(A.RDM_PRE_DIVID, 0) * NVL(A.REF_EXCH_RATE, 1)) AS RDM_PRE_DIVID_TW, ");
			sql_query.append(" (NVL(A.PUR_PRE_DIVID, 0) * NVL(A.REF_EXCH_RATE, 1)) AS PUR_PRE_DIVID_TW ");
			sql_query.append(" FROM TBCRM_AST_INV_DTL A ");
			sql_query.append(" LEFT JOIN (SELECT CUST_ID, CERT_NBR, BOND_CODE, SUM(NVL(TXN_DIVID_ORGD, 0)) AS TXN_DIVID_ORGD, SUM(NVL(TXN_DIVID_TWD, 0)) AS TXN_DIVID_TWD ");
			sql_query.append("				 FROM TBCRM_AST_INV_BOND_DIVID GROUP BY CUST_ID, CERT_NBR, BOND_CODE ) B ");
			sql_query.append("		ON A.CUST_ID = B.CUST_ID AND A.CERT_NBR = B.CERT_NBR AND A.PROD_ID = B.BOND_CODE ");
			sql_query.append(" LEFT JOIN (SELECT  B.BDAD9 AS CERT_NBR, TRIM(B.BDAD5) AS CUST_ID ,B.BDAD3 AS BOND_CODE, B.BDAD2 AS DATADATE, SUM(B.BDADF/A.BDG07*A.BDG0O) TXN_DIVID_ORGD ");
			sql_query.append(" FROM TBPMS_BDS070 A, TBPMS_BDS140_DAY B ");
			sql_query.append(" WHERE A.BDG0K = B.BDAD9 ");
			sql_query.append(" AND B.BDAD6 = 'S' ");
			sql_query.append(" AND TO_CHAR(A.BDG04, 'YYYYMMDD') <= TO_CHAR(B.BDAD2, 'YYYYMMDD') ");
			sql_query.append(" GROUP BY TRIM(B.BDAD5), B.BDAD9, B.BDAD3, B.BDAD2) C  ");
			sql_query.append("		ON A.CUST_ID = C.CUST_ID AND A.CERT_NBR = C.CERT_NBR AND A.PROD_ID = C.BOND_CODE ");
			sql_query.append(" WHERE A.TXN_TYPE = '2' AND A.CUST_ID = :cust_id ");

			sql_group.append("SELECT A.INV_CRCY_TYPE, SUM(A.INV_COST_ORGD) AS INV_COST_ORGD, SUM(A.REF_AMT_ORGD) AS REF_AMT_ORGD, SUM(A.INV_COST_TWD) AS INV_COST_TWD, ");
			sql_group.append("		 SUM(A.REF_AMT_TWD) AS REF_AMT_TWD, SUM(A.REF_EXCH_RATE) AS REF_EXCH_RATE ");
//			sql_group.append("		 ,SUM(NVL(A.RDM_PRE_DIVID, 0)) AS RDM_PRE_DIVID, SUM(NVL(A.PUR_PRE_DIVID, 0)) AS PUR_PRE_DIVID ");
//			sql_group.append("		 ,SUM(NVL(A.RDM_PRE_DIVID, 0) * NVL(A.REF_EXCH_RATE, 1)) AS RDM_PRE_DIVID_TW, SUM(NVL(A.PUR_PRE_DIVID, 0) * NVL(A.REF_EXCH_RATE, 1)) AS PUR_PRE_DIVID_TW ");
			sql_group.append("FROM TBCRM_AST_INV_DTL A WHERE TXN_TYPE = '2' AND A.CUST_ID = :cust_id ");
			queryCondition.setObject("cust_id", inputVO.getCustID());

			sql.append("AND A.PROD_TYPE IN ('PD22', 'PD23') ");

		} else {
			//除海外債其他商品TAB
			sql_query.append("SELECT A.* FROM TBCRM_AST_INV_DTL A WHERE A.TXN_TYPE = '2' AND A.CUST_ID = :cust_id ");

			sql_group.append("SELECT A.INV_CRCY_TYPE, SUM(A.INV_COST_ORGD) AS INV_COST_ORGD, SUM(A.REF_AMT_ORGD) AS REF_AMT_ORGD, ");
			sql_group.append("SUM(A.INV_COST_TWD) AS INV_COST_TWD, SUM(A.REF_AMT_TWD) AS REF_AMT_TWD, SUM(A.REF_EXCH_RATE) AS REF_EXCH_RATE ");
			sql_group.append("FROM TBCRM_AST_INV_DTL A WHERE A.TXN_TYPE = '2' AND A.CUST_ID = :cust_id ");
			queryCondition.setObject("cust_id", inputVO.getCustID());

			if(StringUtils.isNotBlank(inputVO.getPrdType())) {
				switch(inputVO.getPrdType()) {
					case "01":
						// 基金
						sql.append("AND A.PROD_TYPE in ('PD01', 'PD02', 'PD03', 'PD04', 'PD06') ");
						break;
					case "02":
						// 海外ETF/股票
						sql.append("AND A.PROD_TYPE = 'PD10' ");
						break;
					case "03":
						// 海外債到期、海外債贖回
						//sql.append("AND PROD_TYPE = 'PD07' AND INV_TYPE NOT IN ('SN','SD') ");
						sql.append("AND A.PROD_TYPE IN ('PD22', 'PD23') ");
						break;
					case "04":
						// 組合式商品(SI)
						sql.append("AND A.PROD_TYPE = 'PD08' ");
						break;
					case "05":
						// 境外結構型商品(SN)
						sql.append("AND A.PROD_TYPE = 'PD07' AND A.INV_TYPE = 'SN' ");
						break;
					case "06":
						// 外匯雙享利(DCI)
						sql.append("AND A.PROD_TYPE = 'PD11' ");
						break;
					case "07":
						// 金錢信託
	//					sql.append("AND A.PROD_TYPE = 'PD12' ");
						break;
					case "08":
						// 黃金存摺
						sql.append("AND A.PROD_TYPE = 'PD12' ");
						break;
					case "09":
						// 奈米投
						sql.append("AND A.PROD_TYPE = 'PD20' ");
						break;
					case "10":
						// 金市海外債
						sql.append("AND A.PROD_TYPE = 'PD21' ");
						break;
				}
			}else{
				// #6415:排除SI、SN
				sql.append("AND A.PROD_TYPE <> 'PD08' ");  //排除SI
				sql.append("AND NOT (A.PROD_TYPE = 'PD07' AND A.INV_TYPE = 'SN') ");  //排除SN
				sql.append("AND NOT (A.PROD_TYPE = 'PD07' AND A.INV_TYPE NOT IN ('SN','SD')) ");	//排除海外債
				sql.append("AND A.PROD_TYPE <> 'PD11' "); //排除DCI
			}
		}

		if (inputVO.getStartDate() != null) {
			sql.append("and A.DATA_DATE >= TRUNC(:start) ");
			queryCondition.setObject("start", inputVO.getStartDate());
		}
		if (inputVO.getEndDate() != null) {
			sql.append("and A.DATA_DATE < TRUNC(:end)+1 ");
			queryCondition.setObject("end", inputVO.getEndDate());
		}
		queryCondition.setQueryString(sql_query.append(sql).toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		return_VO.setPrdList(list);

		DataSortManager.setSortPool(null);	//幣別加總SQL不須排序
		queryCondition.setQueryString(sql_group.append(sql).append("GROUP BY A.INV_CRCY_TYPE").toString());
		List<Map<String, Object>> list2 = (List<Map<String, Object>>)dam.exeQuery(queryCondition);
		return_VO.setCurList(list2);

		extraProcess(inputVO, return_VO);

		this.sendRtnObject(return_VO);
	}

	private void extraProcess(CRM860InputVO inputVO, CRM860OutputVO returnVO) throws Exception {
        /*
         	# 0791 海外 ETF/股票，使用電文欄位替代 DB 欄位：
         	贖回金額（原幣）：（DB）REF_AMT_ORGD 改成 （電文）NRBRQ01.TxRepeat.TotalAmt
         	投資成本(原幣)：（DB）INV_COST_ORGD 改成（電文）NRBRQ01．TxRepeat.TradeCostBal
         	※ 台幣金額欄位使用電文欄位與匯率相乘
         */
        compareAndChange(
				// 組裝電文跟 DB 匹配的 compareMap(key:eachMap)
        		getCompareMap(inquireAllAccountDealData(inputVO)),
				// 將 DB 同天商品給蒐集起來
				getPrdDateIdMap(returnVO.getPrdList()));
        // 贖回金額已更換，所以重新加總這個欄位
        reSum(returnVO);
	}

    private void reSum(CRM860OutputVO returnVO) {
	    // 依照幣別加總，並使用 Map 保存該幣別相關金額欄位
		String emptyCur = "XXX"; // 幣別空值預設為 XXX
        Map<String, Map<String, BigDecimal>> curSumMap = new HashMap<>();
		for (Object each: returnVO.getPrdList()) {
            Map prdMap = (Map) each;
            String curr = StringUtils.defaultIfEmpty((String) prdMap.get("INV_CRCY_TYPE"), emptyCur);
			BigDecimal refAmtOrgd = (BigDecimal) ObjectUtils.defaultIfNull(prdMap.get("REF_AMT_ORGD"), new BigDecimal(0));
			BigDecimal invCostOrgd = (BigDecimal) ObjectUtils.defaultIfNull(prdMap.get("INV_COST_ORGD"), new BigDecimal(0));
			BigDecimal refAmtTwd = (BigDecimal) ObjectUtils.defaultIfNull(prdMap.get("REF_AMT_TWD"), new BigDecimal(0));
			BigDecimal invCostTwd = (BigDecimal) ObjectUtils.defaultIfNull(prdMap.get("INV_COST_TWD"), new BigDecimal(0));

			if (curSumMap.containsKey(curr)) {
				Map<String, BigDecimal> info = curSumMap.get(curr);
				info.put("REF_AMT_ORGD", info.get("REF_AMT_ORGD").add(refAmtOrgd));
				info.put("INV_COST_ORGD", info.get("INV_COST_ORGD").add(invCostOrgd));
				info.put("REF_AMT_TWD", info.get("REF_AMT_TWD").add(refAmtTwd));
				info.put("INV_COST_TWD", info.get("INV_COST_TWD").add(invCostTwd));
			} else {
				Map<String, BigDecimal> info = new HashMap<>();
				info.put("REF_AMT_ORGD", refAmtOrgd);
				info.put("INV_COST_ORGD", invCostOrgd);
				info.put("REF_AMT_TWD", refAmtTwd);
				info.put("INV_COST_TWD", invCostTwd);
				curSumMap.put(curr, info);
			}

        }
        // 替換加總後的結果
		for (Object each: returnVO.getCurList()) {
			Map curMap = (Map) each;
			String curr = StringUtils.defaultIfEmpty((String) curMap.get("INV_CRCY_TYPE"), emptyCur);
			if (curSumMap.containsKey(curr)) {
				Map<String, BigDecimal> info = curSumMap.get(curr);
				curMap.putAll(info);
			}
		}
    }

    private void compareAndChange(Map<String, Map> compareDealMap, Map<String, LinkedList<Map>> prdDateIdMap) {
		for (Map.Entry<String, LinkedList<Map>> entry: prdDateIdMap.entrySet()) {
			LinkedList<Map> items = entry.getValue();
			// 當天的同個商品多次交易，電文將只有一筆加總，DB 會依照憑證編號分為多筆，
			// 所以將同一天的「贖回金額（原幣）」加總起來，以匹配電文的資料
        	BigDecimal refAmtOrgdTotal = new BigDecimal(0);
			for (Map item: items) {
				refAmtOrgdTotal = refAmtOrgdTotal.add((BigDecimal) ObjectUtils.defaultIfNull(
						item.get("REF_AMT_ORGD"), new BigDecimal(0)));
			}
			// 組合成 key，以跟 DB 匹配取值
			String prdKey = entry.getKey() + refAmtOrgdTotal.setScale(2, RoundingMode.HALF_UP); // 避免小數出現問題
			if (compareDealMap.containsKey(prdKey)) {
				Map dealMap = compareDealMap.get(prdKey);
				// 實付金額，小數兩位
				BigDecimal totalAmt = (BigDecimal) ObjectUtils.defaultIfNull(dealMap.get("TotalAmt"), new BigDecimal(0));
				// 投資成本，小數兩位
				BigDecimal tradeCostBal = (BigDecimal) ObjectUtils.defaultIfNull(dealMap.get("TradeCostBal"), new BigDecimal(0));

				if (items.size() > 1) { // 當天多筆成交，依比例分配 TotalAmt
					// 成交金額，小數兩位
					BigDecimal tradeCost = (BigDecimal) ObjectUtils.defaultIfNull(dealMap.get("TotalAmt"), new BigDecimal(0));

					for (Map item: items) {
						BigDecimal refAmtOrgd = (BigDecimal) ObjectUtils.defaultIfNull(item.get("REF_AMT_ORGD"), new BigDecimal(0));
						// 此筆成交佔該商品總成交百分率，設定 10 讓結果更精確。
						BigDecimal percent = refAmtOrgd.divide(tradeCost, 10, RoundingMode.HALF_UP);

						BigDecimal partOfTotalAmt = percent.multiply(totalAmt).setScale(2, RoundingMode.HALF_UP);
						BigDecimal partOfTradeCostBal = percent.multiply(tradeCostBal).setScale(2, RoundingMode.HALF_UP);
						item.put("REF_AMT_ORGD", partOfTotalAmt);
						item.put("INV_COST_ORGD", partOfTradeCostBal);

						// 贖回匯率
						BigDecimal refExchRate = (BigDecimal) ObjectUtils.defaultIfNull(item.get("REF_EXCH_RATE"), new BigDecimal(0)) ;
						item.put("REF_AMT_TWD", partOfTotalAmt.multiply(refExchRate).setScale(0,  RoundingMode.HALF_UP));
						item.put("INV_COST_TWD", partOfTradeCostBal.multiply(refExchRate).setScale(0,  RoundingMode.HALF_UP));
					}
				} else { // 當天只有一筆成交，直接作替換
					Map first = items.getFirst();
					first.put("REF_AMT_ORGD", totalAmt);
					first.put("INV_COST_ORGD", tradeCostBal);
					// 贖回匯率
					BigDecimal refExchRate = (BigDecimal) ObjectUtils.defaultIfNull(first.get("REF_EXCH_RATE"), new BigDecimal(0)) ;
					first.put("REF_AMT_TWD", totalAmt.multiply(refExchRate).setScale(0,  RoundingMode.HALF_UP));
					first.put("INV_COST_TWD", tradeCostBal.multiply(refExchRate).setScale(0,  RoundingMode.HALF_UP));
				}
			}
		}
    }

	private Map<String, LinkedList<Map>> getPrdDateIdMap(List<Map<String, Object>> prdList) {
		Map<String, LinkedList<Map>> prdDateIdMap = new HashMap<>();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		for (Map<String, Object> prdMap: prdList) {
			// 目前只有 ETF 需要替換成電文欄位
			if (!"PD10".equals(prdMap.get("PROD_TYPE"))) continue;

			Date dataDate = (Date) prdMap.get("DATA_DATE");
			String dataDateStr = null == dataDate ? "" : sdf.format(dataDate);	// 贖回日期
			String prodId = StringUtils.defaultString((String) prdMap.get("PROD_ID")); // 產品代碼
			String key = dataDateStr + prodId;
			if (!prdDateIdMap.containsKey(key))
				prdDateIdMap.put(key, new LinkedList<Map>());
			prdDateIdMap.get(key).add(prdMap);
		}
		return prdDateIdMap;
	}

	private Map<String, Map> getCompareMap(List<Map<String, Object>> dealData) {
		Map<String, Map> compareMap = new HashMap<>();
		for (Map<String, Object> eachDeal: dealData) {
			String tradeDate = StringUtils.trimToEmpty((String) eachDeal.get("TradeDate")); // 成交日期 yyyy/MM/dd
			String insuranceNo = StringUtils.trimToEmpty((String) eachDeal.get("InsuranceNo")); // 產品代碼
			// 成交金額，小數兩位
			BigDecimal tradeCost = (BigDecimal) ObjectUtils.defaultIfNull(eachDeal.get("TradeCost"), new BigDecimal(0));

			// 這裡用數個欄位組合成匹配 Key，並保留電文資料。
			String key = tradeDate + insuranceNo + tradeCost.setScale(2, RoundingMode.HALF_UP); // 避免小數出現問題
			compareMap.put(key, eachDeal);
		}
		return compareMap;
	}

	public void inquire2(Object body, IPrimitiveMap header) throws JBranchException {
		CRM860InputVO inputVO = (CRM860InputVO) body;
		CRM860OutputVO return_VO = new CRM860OutputVO();
		dam = this.getDataAccessManager();

		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		// A.PROD_ID = B.BOND_CODE
		sql.append("SELECT A.PROD_TYPE, A.CERT_NBR, A.PROD_ID, A.PROD_NAME, B.VALU_CRCY_TYPE, SUM(B.TXN_DIVID_ORGD) AS TXN_DIVID_ORGD, SUM(B.TXN_DIVID_TWD) AS TXN_DIVID_TWD, A.INV_TYPE ");
		sql.append("FROM TBCRM_AST_INV_BOND_DIVID B ");
		sql.append("INNER JOIN TBCRM_AST_INV_DTL A ON A.CUST_ID = B.CUST_ID AND A.CERT_NBR = B.CERT_NBR AND A.PROD_ID = B.BOND_CODE WHERE A.TXN_TYPE = '2' AND A.CUST_ID = :cust_id ");
		// where
		if(StringUtils.isBlank(inputVO.getPrdType()) || StringUtils.equals("03", inputVO.getPrdType())) {
			// 海外債到期、海外債贖回
			sql.append("AND A.PROD_TYPE IN ('PD22', 'PD23') ");
		} else {
			sql.append(" AND 1 = 2 ");
		}

		if (inputVO.getStartDate() != null) {
			sql.append("and A.DATA_DATE >= TRUNC(:start) ");
			queryCondition.setObject("start", inputVO.getStartDate());
		}
		if (inputVO.getEndDate() != null) {
			sql.append("and A.DATA_DATE < TRUNC(:end)+1 ");
			queryCondition.setObject("end", inputVO.getEndDate());
		}
		//
		sql.append("GROUP BY A.PROD_TYPE, A.CERT_NBR, A.PROD_ID, A.PROD_NAME, B.VALU_CRCY_TYPE, A.INV_TYPE ");
		sql.append("UNION ");
		// A.PROD_ID = B.DCI_CODE
		sql.append("SELECT A.PROD_TYPE, A.CERT_NBR, A.PROD_ID, A.PROD_NAME, B.VALU_CRCY_TYPE, SUM(B.TXN_DIVID_ORGD) AS TXN_DIVID_ORGD, SUM(B.TXN_DIVID_TWD) AS TXN_DIVID_TWD, A.INV_TYPE ");
		sql.append("FROM TBCRM_AST_INV_DCI_DIVID B ");
		sql.append("INNER JOIN TBCRM_AST_INV_DTL A ON A.CUST_ID = B.CUST_ID AND A.CERT_NBR = (B.CERT_NBR||B.DEPOSIT_NBR) AND A.PROD_ID = B.DCI_CODE WHERE A.TXN_TYPE = '2' AND A.CUST_ID = :cust_id ");
		// where
		if(StringUtils.isBlank(inputVO.getPrdType()) || StringUtils.equals("06", inputVO.getPrdType())) {
			// 外匯雙享利(DCI)
			sql.append("AND A.PROD_TYPE = 'PD11' ");
		} else {
			sql.append(" AND 1 = 2 ");
		}

		if (inputVO.getStartDate() != null) {
			sql.append("and A.DATA_DATE >= TRUNC(:start) ");
			queryCondition.setObject("start", inputVO.getStartDate());
		}
		if (inputVO.getEndDate() != null) {
			sql.append("and A.DATA_DATE < TRUNC(:end)+1 ");
			queryCondition.setObject("end", inputVO.getEndDate());
		}
		//
		sql.append("GROUP BY A.PROD_TYPE, A.CERT_NBR, A.PROD_ID, A.PROD_NAME, B.VALU_CRCY_TYPE, A.INV_TYPE ");
		sql.append("UNION ");
		// A.PROD_ID = B.FUND_CODE
		sql.append("SELECT A.PROD_TYPE, A.CERT_NBR, A.PROD_ID, A.PROD_NAME, B.VALU_CRCY_TYPE, SUM(B.TXN_DIVID_ORGD) AS TXN_DIVID_ORGD, SUM(B.TXN_DIVID_TWD) AS TXN_DIVID_TWD, A.INV_TYPE ");
		sql.append("FROM TBCRM_AST_INV_FUND_DIVID B ");
		sql.append("INNER JOIN ( SELECT DISTINCT CUST_ID,PROD_TYPE,CERT_NBR,PROD_ID,PROD_NAME,INV_TYPE FROM TBCRM_AST_INV_DTL WHERE CUST_ID = :cust_id AND TXN_TYPE = '2' AND DATA_DATE >= TRUNC(:start) AND DATA_DATE < TRUNC(:end)+1" );
		sql.append(") A " );
		sql.append("ON A.CUST_ID = B.CUST_ID AND A.CERT_NBR = B.CERT_NBR AND A.PROD_ID = B.FUND_CODE ");
		// where
		if(StringUtils.isBlank(inputVO.getPrdType()) || StringUtils.equals("01", inputVO.getPrdType())) {
			// 基金
			sql.append("AND A.PROD_TYPE in ('PD01', 'PD02', 'PD03', 'PD04', 'PD06') ");
		} else {
			sql.append(" AND 1 = 2 ");
		}

//		if (inputVO.getStartDate() != null) {
//			sql.append("and B.DATA_DATE >= TRUNC(:start) ");
//			queryCondition.setObject("start", inputVO.getStartDate());
//		}
//		if (inputVO.getEndDate() != null) {
//			sql.append("and B.DATA_DATE < TRUNC(:end)+1 ");
//			queryCondition.setObject("end", inputVO.getEndDate());
//		}
		//
		sql.append("GROUP BY A.PROD_TYPE, A.CERT_NBR, A.PROD_ID, A.PROD_NAME, B.VALU_CRCY_TYPE, A.INV_TYPE ");

		// # 0860 因需求隱藏SI、SN
//		sql.append("UNION ");
//		// A.PROD_ID = B.SI_CODE
//		sql.append("SELECT A.PROD_TYPE, A.CERT_NBR, A.PROD_ID, A.PROD_NAME, B.VALU_CRCY_TYPE, SUM(B.TXN_DIVID_ORGD) AS TXN_DIVID_ORGD, SUM(B.TXN_DIVID_TWD) AS TXN_DIVID_TWD, A.INV_TYPE ");
//		sql.append("FROM TBCRM_AST_INV_SI_DIVID B ");
//		sql.append("INNER JOIN TBCRM_AST_INV_DTL A ON A.CUST_ID = B.CUST_ID AND A.CERT_NBR = (B.CERT_NBR||B.DEPOSIT_NBR) AND A.PROD_ID = B.SI_CODE ");
//		sql.append("WHERE A.TXN_TYPE = '2' AND A.CUST_ID = :cust_id ");
//		// where
//		if(StringUtils.isBlank(inputVO.getPrdType()) || StringUtils.equals("04", inputVO.getPrdType())) {
//			// 組合式商品(SI)
//			sql.append("AND A.PROD_TYPE = 'PD08' ");
//		} else {
//			sql.append(" AND 1 = 2 ");
//		}
//
//		if (inputVO.getStartDate() != null) {
//			sql.append("and A.DATA_DATE >= TRUNC(:start) ");
//			queryCondition.setObject("start", inputVO.getStartDate());
//		}
//		if (inputVO.getEndDate() != null) {
//			sql.append("and A.DATA_DATE < TRUNC(:end)+1 ");
//			queryCondition.setObject("end", inputVO.getEndDate());
//		}
//		//
//		sql.append("GROUP BY A.PROD_TYPE, A.CERT_NBR, A.PROD_ID, A.PROD_NAME, B.VALU_CRCY_TYPE, A.INV_TYPE ");

		sql.append("UNION ");
		sql.append("SELECT A.PROD_TYPE, A.CERT_NBR, A.PROD_ID, A.PROD_NAME, A.VALU_CRCY_TYPE, SUM(A.TXN_DIVID_ORGD) AS TXN_DIVID_ORGD, SUM(A.TXN_DIVID_TWD) AS TXN_DIVID_TWD, A.INV_TYPE ");
		sql.append("  FROM (");
		sql.append("		SELECT DISTINCT A.PROD_TYPE, b.CERT_NBR, A.PROD_ID, A.PROD_NAME, B.VALU_CRCY_TYPE, B.TXN_DIVID_ORGD, B.TXN_DIVID_TWD, A.INV_TYPE ");
		sql.append("  		FROM TBCRM_AST_INV_STOCK_DIVID B ");
		sql.append(" 		INNER JOIN TBCRM_AST_INV_DTL A ON A.CUST_ID = B.CUST_ID AND A.PROD_ID = B.STOCK_CODE ");
		sql.append("       	WHERE A.TXN_TYPE = '2' AND A.CUST_ID = :cust_id ");
		// where
		if(StringUtils.isBlank(inputVO.getPrdType()) || StringUtils.equals("02", inputVO.getPrdType())) {
			// 海外ETF/股票
			sql.append("AND A.PROD_TYPE = 'PD10' ");
		} else {
			sql.append(" AND 1 = 2 ");
		}

		if (inputVO.getStartDate() != null) {
			sql.append("and A.DATA_DATE >= TRUNC(:start) ");
			queryCondition.setObject("start", inputVO.getStartDate());
		}
		if (inputVO.getEndDate() != null) {
			sql.append("and A.DATA_DATE < TRUNC(:end)+1 ");
			queryCondition.setObject("end", inputVO.getEndDate());
		}
		sql.append(" ) A ");
		//
		sql.append("GROUP BY A.PROD_TYPE,A.CERT_NBR,A.PROD_ID,A.PROD_NAME,A.VALU_CRCY_TYPE,A.INV_TYPE ");
		sql.append(" UNION ");
		//金市海外債
		sql.append("SELECT A.PROD_TYPE, A.CERT_NBR, A.PROD_ID, A.PROD_NAME, A.VALU_CRCY_TYPE, SUM(A.TXN_DIVID_ORGD) AS TXN_DIVID_ORGD, SUM(A.TXN_DIVID_TWD) AS TXN_DIVID_TWD, A.INV_TYPE ");
		sql.append("  FROM (");
		sql.append("		SELECT DISTINCT A.PROD_TYPE, A.CERT_NBR, B.M_SE_CODE AS PROD_ID, B.M_INSTRUMENT AS PROD_NAME, B.M_TP_NOMCUR AS VALU_CRCY_TYPE, B.INT_AMT AS TXN_DIVID_ORGD, B.INT_AMT_TWD AS TXN_DIVID_TWD, A.INV_TYPE ");
		sql.append("  		FROM TBCRM_AST_INV_VPBND_1006 B ");
		sql.append(" 		INNER JOIN TBCRM_AST_INV_DTL A ON A.CUST_ID = B.M_TP_CNTRP AND A.PROD_ID = B.M_SE_CODE ");
		sql.append("       	WHERE A.TXN_TYPE = '2' AND A.CUST_ID = :cust_id ");
		if(StringUtils.isBlank(inputVO.getPrdType()) || StringUtils.equals("10", inputVO.getPrdType())) {
			//金市海外債
			sql.append(" AND A.PROD_TYPE = 'PD21' ");
		} else {
			sql.append(" AND 1 = 2 ");
		}

		if (inputVO.getStartDate() != null) {
			sql.append("and A.DATA_DATE >= TRUNC(:start) ");
			queryCondition.setObject("start", inputVO.getStartDate());
		}
		if (inputVO.getEndDate() != null) {
			sql.append("and A.DATA_DATE < TRUNC(:end)+1 ");
			queryCondition.setObject("end", inputVO.getEndDate());
		}
		sql.append(" ) A ");
		//
		sql.append(" GROUP BY A.PROD_TYPE,A.CERT_NBR,A.PROD_ID,A.PROD_NAME,A.VALU_CRCY_TYPE,A.INV_TYPE ");
		sql.append(" UNION ");
		//奈米投
		sql.append("SELECT A.PROD_TYPE, A.CERT_NBR, A.PROD_ID, A.PROD_NAME, B.VALU_CRCY_TYPE, B.TXN_DIVID_ORGD, B.TXN_DIVID_TWD, A.INV_TYPE ");
		sql.append("FROM TBCRM_AST_INV_NANO_DIVID B ");
		sql.append("INNER JOIN TBCRM_AST_INV_DTL A ON A.CUST_ID = B.CUST_ID AND A.CERT_NBR = B.CERT_NBR ");
		sql.append("WHERE A.TXN_TYPE = '2' AND A.CUST_ID = :cust_id ");
		if(StringUtils.isBlank(inputVO.getPrdType()) || StringUtils.equals("09", inputVO.getPrdType())) {
			// 奈米投
			sql.append("AND A.PROD_TYPE = 'PD20' ");
		} else {
			sql.append(" AND 1 = 2 ");
		}

		if (inputVO.getStartDate() != null) {
			sql.append("and A.DATA_DATE >= TRUNC(:start) ");
			queryCondition.setObject("start", inputVO.getStartDate());
		}
		if (inputVO.getEndDate() != null) {
			sql.append("and A.DATA_DATE < TRUNC(:end)+1 ");
			queryCondition.setObject("end", inputVO.getEndDate());
		}
		//
		sql.append(" AND A.DATA_DATE = (SELECT MAX(DATA_DATE) FROM TBCRM_AST_INV_DTL WHERE CUST_ID = A.CUST_ID AND CERT_NBR = A.CERT_NBR) ");
		sql.append(" AND B.TXN_DATE = (SELECT MAX(TXN_DATE) FROM TBCRM_AST_INV_NANO_DIVID WHERE CUST_ID = B.CUST_ID AND CERT_NBR = B.CERT_NBR) ");

		queryCondition.setObject("cust_id", inputVO.getCustID());
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		return_VO.setDivList(list);

		this.sendRtnObject(return_VO);
	}

	/**
	 * 查詢 NRBRQ01，取得該名客戶所有帳號的交易資料
	 *
	 *
	 *
	 * @return**/
	private List<Map<String, Object>> inquireAllAccountDealData(CRM860InputVO inputVO) throws Exception {
		List<String> account = new LinkedList<>();
		CRM822 crm822 = PlatformContext.getBean(CRM822.class);
		for(ESBUtilOutputVO vo : crm822.getCustAcct(inputVO.getCustID())) {
			NRBRQ01OutputVO nrbrq01OutputVO = vo.getNrbrq01OutputVO();
			List<NRBRQ01OutputDetailsVO> details = nrbrq01OutputVO.getDetails();
			details = (CollectionUtils.isEmpty(details)) ? new ArrayList<NRBRQ01OutputDetailsVO>() : details;

			for (NRBRQ01OutputDetailsVO detail : details) {
				account.add(detail.getTrustAcct());
			}
		}

		List<Map<String, Object>> result = new LinkedList<>();
		if (CollectionUtils.isNotEmpty(account)) {
			for (String acct: account) {
				result.addAll(crm822.inquire(inputVO.getCustID(), acct, inputVO.getStartDate(), inputVO.getEndDate()));
			}
		}
		return result;
	}
}