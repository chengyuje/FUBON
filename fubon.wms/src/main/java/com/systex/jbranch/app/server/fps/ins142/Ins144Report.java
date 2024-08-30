package com.systex.jbranch.app.server.fps.ins142;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.server.fps.cmsub302.CMSUB302Util;
import com.systex.jbranch.app.server.fps.insjlb.vo.DoGetCoverage03OutputVO;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.report.factory.ReportFactory;
import com.systex.jbranch.platform.common.report.generator.ReportGeneratorIF;
import com.systex.jbranch.platform.common.report.reportdata.ReportData;
import com.systex.jbranch.platform.common.report.reportdata.ReportDataIF;

@Component("Ins144Report")
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class Ins144Report extends AbstractInsReport implements Ins144ReportInf{	
	/**
	 * 生涯透視表
	 * 
	 * @param doOutputVO
	 * @param reportList
	 * @throws DAOException
	 * @throws JBranchException
	 */
	public String printReportINS144(DoGetCoverage03OutputVO doOutputVO , List<Map<String, Object>> familyDataList) throws DAOException, JBranchException {
		// 2012/08/16,Carey,END
		// 壽險保障金額
		List<Map<String, Object>> amt_lst = new ArrayList<Map<String, Object>>();
		// 年齡
		List<Map<String, Object>> age_lst = new ArrayList<Map<String, Object>>();
		// 保費彙總
		Map<String, Object> premium_map = new HashMap<String, Object>();
		// 還本彙總
		Map<String, Object> repay_map = new HashMap<String, Object>();
		// 所有被保險人的名單
		List<String> name_lst = new ArrayList<String>();

		// 最小、最大年
		int minYear_int = -1;
		int maxYear_int = -1;
		// 保費彙總、還本彙總
		BigDecimal tmp_big = new BigDecimal(0);

		int name_int = -1;
		int rocYear_int = 0;

		SimpleDateFormat nowDate_sdf = new SimpleDateFormat(DATE_YEAR);
		int nowYear_int = Integer.valueOf(nowDate_sdf.format(new Date())) - 1911;

		for (Map<String, Object> lstExpression : (List<Map<String, Object>>) doOutputVO.getLstWholeLife()) {
			if (nowYear_int <= Integer.valueOf(lstExpression.get("ROCYEAR").toString())) {
				
				if(CollectionUtils.isNotEmpty(familyDataList)){
					for (Map<String, Object> family_map : familyDataList) {
						if (CMSUB302Util.getString(family_map.get("RELATION_ID")).equals(CMSUB302Util.getString(lstExpression.get("INSURED_ID")))) {
							lstExpression.put("THEAGE", getInt(lstExpression.get("ROCYEAR")) - nowYear_int + getInt(family_map.get("RELATION_INSAGE")));
							break;
						}
					}
				}
				
				// end.
				name_int = name_lst.indexOf(lstExpression.get("CUSTNAME").toString());
				rocYear_int = Integer.valueOf(lstExpression.get("ROCYEAR").toString());
				// 取得最小、最大年
				if (minYear_int > rocYear_int || minYear_int == -1) {
					minYear_int = rocYear_int;
				}
				if (maxYear_int < rocYear_int || maxYear_int == -1) {
					maxYear_int = rocYear_int;
				}
				// 取得保障金額 List
				if (name_int == -1) {
					Map<String, Object> new_map = null;

					new_map = new HashMap<String, Object>();
					new_map.put(String.valueOf(rocYear_int), lstExpression.get("LIFE"));
					amt_lst.add(new_map);

					new_map = new HashMap<String, Object>();
					new_map.put(String.valueOf(rocYear_int), lstExpression.get("THEAGE"));
					age_lst.add(new_map);

					name_lst.add(lstExpression.get("CUSTNAME").toString());
				} else {
					age_lst.get(name_int).put(String.valueOf(rocYear_int), lstExpression.get("THEAGE"));
					amt_lst.get(name_int).put(String.valueOf(rocYear_int), lstExpression.get("LIFE"));
				}
				// 計算保費彙總、還本彙總
				if (premium_map.get(String.valueOf(rocYear_int)) != null) {
					tmp_big = new BigDecimal(premium_map.get(String.valueOf(rocYear_int)).toString());
					tmp_big = tmp_big.add(new BigDecimal(lstExpression.get("PREMIUM").toString()));
					premium_map.put(String.valueOf(rocYear_int), tmp_big);
				} else {
					tmp_big = new BigDecimal(lstExpression.get("PREMIUM").toString());
					premium_map.put(String.valueOf(rocYear_int), tmp_big);
				}
				if (repay_map.get(String.valueOf(rocYear_int)) != null) {
					tmp_big = new BigDecimal(repay_map.get(String.valueOf(rocYear_int)).toString());
					tmp_big = tmp_big.add(new BigDecimal(lstExpression.get("REPAY").toString()));
					repay_map.put(String.valueOf(rocYear_int), tmp_big);
				} else {
					tmp_big = new BigDecimal(lstExpression.get("REPAY").toString());
					repay_map.put(String.valueOf(rocYear_int), tmp_big);
				}
			}
		}
		List<Map<String, Object>> res_lst = new ArrayList<Map<String, Object>>();

		// 總被保險人數
		int nameLength_int = name_lst.size();
		// 每頁被保險人數
		int cntNumber = 4;
		int total_cnt = (nameLength_int - 1) / cntNumber;
		for (name_int = 0; name_int <= total_cnt; name_int++) {
			for (int year_int = minYear_int; year_int <= maxYear_int; year_int++) {
				if (name_int == total_cnt) {
					res_lst.add(setMap(String.valueOf(name_int), year_int, name_int * cntNumber, nameLength_int, name_lst, age_lst, amt_lst, premium_map, repay_map));
				} else {
					res_lst.add(setMap(String.valueOf(name_int), year_int, name_int * cntNumber, (name_int + 1) * cntNumber, name_lst, age_lst, amt_lst, null, null));
				}
			}
		}

		Map<String, Object> res_map = null;

		for (int index_int = res_lst.size() - 1; index_int >= 0; index_int--) {
			res_map = res_lst.get(index_int);
			if (("".equals(CMSUB302Util.getString(res_map.get("LIFE_1"))) || "0".equals(CMSUB302Util.getString(res_map.get("LIFE_1"))))
					&& ("".equals(CMSUB302Util.getString(res_map.get("LIFE_2"))) || "0".equals(CMSUB302Util.getString(res_map.get("LIFE_2"))))
					&& ("".equals(CMSUB302Util.getString(res_map.get("LIFE_3"))) || "0".equals(CMSUB302Util.getString(res_map.get("LIFE_3"))))
					&& ("".equals(CMSUB302Util.getString(res_map.get("LIFE_4"))) || "0".equals(CMSUB302Util.getString(res_map.get("LIFE_4"))))
					&& ("".equals(CMSUB302Util.getString(res_map.get("PREMIUM"))) || "0".equals(CMSUB302Util.getString(res_map.get("PREMIUM"))))
					&& ("".equals(CMSUB302Util.getString(res_map.get("REPAY"))) || "0".equals(CMSUB302Util.getString(res_map.get("REPAY"))))) {
				res_lst.remove(index_int);
			} else {
				break;
			}
		}

		ReportGeneratorIF generator = ReportFactory.getGenerator();
		ReportDataIF rptData = new ReportData();
		rptData.addRecordList("Script Mult Data Set", res_lst);
		rptData.addParameter("rptDate", getDate());
		rptData.setMerge(true);
		
		return generator.generateReport(RPT_INS144, RPT_R1, rptData).getLocation();
	}
	
	private Map<String, Object> setMap(String page, int year, int start, int end, List<String> nameList, List<Map<String, Object>> ageList, List<Map<String, Object>> lifeList,
			Map<String, Object> premiumMap, Map<String, Object> repayMap) {
		Map<String, Object> res_map = new HashMap<String, Object>();

		res_map.put("PAGE", page);
		res_map.put("ROCYEAR", year);
		if (premiumMap != null) {
			res_map.put("PREMIUM", premiumMap.get("" + year));
		}
		if (repayMap != null) {
			res_map.put("REPAY", repayMap.get("" + year));
		}
		int cntNumber = 1;
		for (int page_int = start; page_int < end; page_int++) {
			if (page_int < nameList.size()) {
				res_map.put("CUSTNAME_" + cntNumber, nameList.get(page_int));
				res_map.put("THEAGE_" + cntNumber, ageList.get(page_int).get("" + year));
				res_map.put("LIFE_" + cntNumber, lifeList.get(page_int).get("" + year));
				cntNumber++;
			}
		}

		return res_map;
	}
}
