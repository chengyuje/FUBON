package com.systex.jbranch.app.server.fps.ins142;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.server.fps.insjlb.InsjlbParamInf;
import com.systex.jbranch.app.server.fps.insjlb.InsjlbUtils;
import com.systex.jbranch.app.server.fps.insjlb.vo.DoGetCoverage03OutputVO;
import com.systex.jbranch.comutil.collection.CollectionSearchUtils;
import com.systex.jbranch.comutil.collection.GenericMap;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.report.factory.ReportFactory;
import com.systex.jbranch.platform.common.report.generator.ReportGeneratorIF;
import com.systex.jbranch.platform.common.report.reportdata.ReportData;
import com.systex.jbranch.platform.common.report.reportdata.ReportDataIF;

@Component("Ins142Report")
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class Ins142Report extends AbstractInsReport implements Ins142ReportInf{	
	/**
	 * 檢視結構圖
	 * 
	 * @param doOutputVO
	 * @param reportList
	 * @throws DAOException
	 * @throws JBranchException
	 * @throws ParseException
	 */
	public List<String> printReportINS142(DoGetCoverage03OutputVO doOutputVO , Map<String, Object> sortNoNameMap) throws JBranchException{
		Map<String, Object> resultByNameMap = new HashMap<String, Object>();
		List<String> reportList = new ArrayList<String>();
		
		//先複製一份LstExpression結購
		List<Map<String, Object>> doSortLstEx = 
			(List<Map<String, Object>>)CollectionSearchUtils.cloneMapInList(doOutputVO.getLstExpression());
		
		//篩選出合計
		doSortLstEx = CollectionSearchUtils.selectMapInList(doSortLstEx , "INSCOMPANY" , InsjlbParamInf.SUM_DESC);
		
		//排除給付項目為B0013
		doSortLstEx = CollectionSearchUtils.selectMapNotInList(doSortLstEx, "SORTNO", "B0013");
		
		//依照INSURED_ID排序
		Collections.sort(doSortLstEx , new Comparator<Map<String, Object>>(){
			@Override
			public int compare(Map<String, Object> o1, Map<String, Object> o2) {
				String insuredId1 = ObjectUtils.toString(o1.get("INSURED_ID"));
				String insuredId2 = ObjectUtils.toString(o2.get("INSURED_ID"));
				return insuredId1.compareTo(insuredId2);
			}
		});
		
		//相同的insuredId輸出在同一張
		for (Map<String, Object> lstExpression : doSortLstEx) {
			int index_int = -1;
			String insuredId = ObjectUtils.toString(lstExpression.get("INSURED_ID"));
			
			if (resultByNameMap.get(insuredId) != null) {
				List<Map<String, Object>> resultByNameList = (List<Map<String, Object>>) resultByNameMap.get(insuredId);
				resultByNameList.add(lstExpression);
			} 
			else {
				List<Map<String, Object>> resultByNameList = new ArrayList<Map<String, Object>>();
				resultByNameList.add(lstExpression);
				resultByNameMap.put(insuredId , resultByNameList);
			}
		}

		// 產生report
		ReportGeneratorIF generator = ReportFactory.getGenerator();
		
		SimpleDateFormat date_dft = new SimpleDateFormat(DATE_YMD);
		
		//取出報表欄位對應定(key為給附編號，value為報表對應位置)
		Map<String, String> cmrpt210Map = getBthXML("TBSYS.CMRPT210_R1");
		
		for (String insuredId : resultByNameMap.keySet()) {
			List<Map> resultByNameList = (List<Map>) resultByNameMap.get(insuredId);
			GenericMap policyMap = new GenericMap(resultByNameList.get(0));
			ReportDataIF rptData = new ReportData();
			
			//系統日期時間
			Date sysDate = new Date();
			int rocYear = Integer.valueOf(new SimpleDateFormat(DATE_YEAR).format(sysDate)) - 1911;
			
			String custName = policyMap.getNotNullStr("CUSTNAME");
			
			//客戶姓名
			rptData.addParameter("CUSTNAME", custName);
			//客戶生日
			rptData.addParameter("BIRTH", getDateStr(policyMap.get("BIRTH"), "yyyy/MM/dd", true));			
			//投保年齡
			rptData.addParameter("CUSTAGE", String.valueOf(InsjlbUtils.calculateInsAge(sysDate , policyMap.getDate("BIRTH"))));
			//報表印印日期
			rptData.addParameter("rptDate", new SimpleDateFormat(DATE_YMD).format(sysDate));
			
			for (Map<String, Object> exWholeLifeMap : (List<Map<String, Object>>) doOutputVO.getLstWholeLife()) {
				String exWholeLifeCustName = ObjectUtils.toString(exWholeLifeMap.get("CUSTNAME"));
				int exWholeRocYear = Integer.valueOf(exWholeLifeMap.get("ROCYEAR").toString());
				String premium = ObjectUtils.toString(exWholeLifeMap.get("PREMIUM"));
				
				//同一位客戶且年度沒超過系統年度(均以roc year來處理)
				if (exWholeLifeCustName.equals(custName) && rocYear <= exWholeRocYear) {
					rptData.addParameter("PAYTYPEPEMIUM", changeStr(premium));
					break;
				} else {
					rptData.addParameter("PAYTYPEPEMIUM", "0");
				}
			}

			for (Map<String, Object> lstExpression : doSortLstEx) {
				String sortNo = ObjectUtils.toString(lstExpression.get("SORTNO"));
				String exCustName = ObjectUtils.toString(lstExpression.get("CUSTNAME"));
				//對應報表的位置
				String reportCoordinate = cmrpt210Map.get(sortNo);
				
				if (reportCoordinate != null && custName.equals(exCustName)) {
					rptData.addParameter(getStringToZero(reportCoordinate) , lstExpression.get("DESCRIPTION"));
				}
			}

			// 設置欄位中文名稱
			if (sortNoNameMap != null) {
				for (String sortNoNameStr : sortNoNameMap.keySet()) {
					rptData.addParameter(cmrpt210Map.get(sortNoNameStr) + "_name" , sortNoNameMap.get(sortNoNameStr));
				}
			}

			rptData.setMerge(true);
			reportList.add(generator.generateReport(RPT_INS142, RPT_R1, rptData).getLocation());
		}
		
		return reportList;
	}
}
