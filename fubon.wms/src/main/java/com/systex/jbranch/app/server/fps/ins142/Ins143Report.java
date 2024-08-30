package com.systex.jbranch.app.server.fps.ins142;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.server.fps.insjlb.vo.DoGetCoverage03OutputVO;
import com.systex.jbranch.comutil.collection.CollectionSearchUtils;
import com.systex.jbranch.comutil.collection.GenericMap;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.report.factory.ReportFactory;
import com.systex.jbranch.platform.common.report.generator.ReportGeneratorIF;
import com.systex.jbranch.platform.common.report.reportdata.ReportData;
import com.systex.jbranch.platform.common.report.reportdata.ReportDataIF;

@Component("Ins143Report")
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class Ins143Report extends AbstractInsReport implements Ins143ReportInf{	
	/**
	 * 個人生涯彙總表
	 * 
	 * @param doOutputVO
	 * @param reportList
	 * @throws DAOException
	 * @throws JBranchException
	 */
	public List<String> printReportINS143(DoGetCoverage03OutputVO doOutputVO , List<Map<String, Object>> lstFamily) throws DAOException, JBranchException {
		Map<String, Object> result_map = new HashMap<String, Object>();
		List<Map<String, Object>> resultList = null;
		List<String> reportList = new ArrayList<String>();

		SimpleDateFormat nowDate_sdf = new SimpleDateFormat(DATE_YEAR);
		int nowYear_int = Integer.valueOf(nowDate_sdf.format(new Date())) - 1911;

		for (Map<String, Object> wholeLIfe_map : (List<Map<String, Object>>) doOutputVO.getLstWholeLife()) {
			
			if (nowYear_int <= Integer.valueOf(wholeLIfe_map.get("ROCYEAR").toString())) {
				
				//家庭戶清單
				for (Map<String, Object> family_map : lstFamily) {
					String relationId = ObjectUtils.toString(family_map.get("RELATION_ID"));
					String insuredId = ObjectUtils.toString(wholeLIfe_map.get("INSURED_ID"));
					
					if (relationId.equals(insuredId)) {
						wholeLIfe_map.put("THEAGE", getInt(wholeLIfe_map.get("ROCYEAR")) - nowYear_int + getInt(new GenericMap(family_map).getBigDecimal(("RELATION_INSAGE")).intValue()));
						break;
					}
				}
				
				// 調整成用客戶ID，否則家庭戶大家都叫陳OO
				String custID = ObjectUtils.toString(wholeLIfe_map.get("INSURED_ID"));
				
				if (result_map.get(custID) != null) {
					((List<Map<String, Object>>) result_map.get(custID)).add(wholeLIfe_map);
				} 
				else {
					resultList = new ArrayList<Map<String, Object>>();
					resultList.add(wholeLIfe_map);
					result_map.put(custID, resultList);
				}
			}
		}

		ReportGeneratorIF generator = null;
		ReportDataIF rptData = null;

		for (String custID : result_map.keySet()) {
			generator = ReportFactory.getGenerator();
			rptData = new ReportData();
			
			// 透過客戶ID 去找家庭戶成員資料
			String custName = "";
			if(CollectionUtils.isEmpty(lstFamily)) {
				custName = ObjectUtils.toString(((Map<String, Object>)doOutputVO.getLstWholeLife().get(0)).get("CUSTNAME"));
			} else {
				Map<String, Object> custInfoMap = CollectionSearchUtils.findMapInColleciton(lstFamily, "RELATION_ID", custID);
				custName = (String) custInfoMap.get("RELATION_NAME");
			}
			rptData.addParameter("CUSTNAME", custName);
			rptData.addRecordList("lstWholeLife", ((List<Map<String, Object>>) result_map.get(custID)));
			rptData.addParameter("rptDate", getDate());
			rptData.setMerge(true);

			String location = generator.generateReport(RPT_INS143, RPT_R1, rptData).getLocation();
			reportList.add(location);			
		}
		
		return reportList;
	}
}
