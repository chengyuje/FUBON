package com.systex.jbranch.app.server.fps.ins141;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.server.fps.ins810.INS810;
import com.systex.jbranch.app.server.fps.ins810.INS810InputVO;
import com.systex.jbranch.app.server.fps.ins810.INS810OutputVO;
import com.systex.jbranch.app.server.fps.insjlb.InsjlbUtils;
import com.systex.jbranch.comutil.collection.CollectionSearchUtils;
import com.systex.jbranch.comutil.collection.GenericMap;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.report.ReportIF;
import com.systex.jbranch.platform.common.report.factory.ReportFactory;
import com.systex.jbranch.platform.common.report.generator.ReportGeneratorIF;
import com.systex.jbranch.platform.common.report.reportdata.ReportData;
import com.systex.jbranch.platform.common.report.reportdata.ReportDataIF;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.server.bizLogic.BizLogic;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.XmlInfo;

@Component("ins141")
@Scope("request")
public class INS141 extends BizLogic{
	
	public ReportIF printReport(List<Map<String, Object>> dataList) throws JBranchException, ParseException {
		DataAccessManager dam = getDataAccessManager();

		//計算保險年齡
		INS810 ins810 = (INS810) PlatformContext.getBean("ins810");
		INS810InputVO ins810inputVO = new INS810InputVO();
		INS810OutputVO ins810outputVO = new INS810OutputVO();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf_slash = new SimpleDateFormat("yyyy/MM/dd");
		
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> payType = xmlInfo.doGetVariable("INS.PAY_TYPE", FormatHelper.FORMAT_3);
		String tempPayType = "";
		String tempPolicyNbr = "";
		
		List<Map<String, Object>> INSSEQList = new ArrayList<Map<String,Object>>();
		dataList =(List) CollectionSearchUtils.cloneMapInList(dataList);
		
		int count = 0;
		int mainCount = 0;
		
		Date effectedDate = new Date();	// 有效日期轉換用
		BigDecimal TOTAL_INSYEARFEE = new BigDecimal(0);	// 年度所繳保費計算用
		
		for (Map<String, Object> data : dataList) {
			if (INSSEQList.size() <= 0) {
				Map<String, Object> INSSEQData = new HashMap<String, Object>();
				INSSEQData.put("PRD_ID", data.get("PRD_ID"));
				INSSEQData.put("TOTAL_INSYEARFEE", data.get("INSYEARFEE"));
				INSSEQList.add(INSSEQData);
			} 
			else {
				List<Map<String, Object>> temp_list = new ArrayList<Map<String,Object>>();
				temp_list.addAll(INSSEQList);
				
				for (int i = 0 ; i < temp_list.size() ; i++) {
					
					if (!StringUtils.equals(String.valueOf(data.get("PRD_ID")), String.valueOf(temp_list.get(i).get("PRD_ID")))) {
						//比對到最後一筆都不一樣則新增
						if (i == temp_list.size()-1) {
							Map<String, Object> INSSEQData = new HashMap<String, Object>();
							INSSEQData.put("PRD_ID", data.get("PRD_ID"));
							INSSEQData.put("TOTAL_INSYEARFEE", data.get("INSYEARFEE"));
							INSSEQList.add(INSSEQData);
						}
					} 
					else {
						BigDecimal total_insyearfee = new BigDecimal(String.valueOf(temp_list.get(i).get("TOTAL_INSYEARFEE")));
						BigDecimal INSYEARFEE = new BigDecimal(String.valueOf(data.get("INSYEARFEE")));
						BigDecimal total_insyearfee_end = total_insyearfee.add(INSYEARFEE);
						INSSEQList.get(i).put("TOTAL_INSYEARFEE", total_insyearfee_end);
					}
				}
			}		
			
			// 年度所繳保費計算_start
			if (String.valueOf(data.get("PAY_TYPE")) != null) {
				TOTAL_INSYEARFEE = new BigDecimal(String.valueOf(data.get("INSYEARFEE")));
				
				// 躉繳/彈性繳
				if (StringUtils.equals("0", String.valueOf(data.get("PAY_TYPE"))) || StringUtils.equals("99", String.valueOf(data.get("PAY_TYPE")))) {
					String effect = (String.valueOf(data.get("EFFECTED_DATE"))).substring(0, 4);
					String now = sdf.format(new Date().getTime()).substring(0, 4);
					
					if (new BigDecimal(now).compareTo(new BigDecimal(effect)) >= 0) {
						data.put("INSYEARFEE_YEAR", 0);
					} else {
						data.put("INSYEARFEE_YEAR", TOTAL_INSYEARFEE);
					}
				} else if (StringUtils.equals("2", String.valueOf(data.get("PAY_TYPE")))) {	// 半年繳
					data.put("INSYEARFEE_YEAR", TOTAL_INSYEARFEE.multiply(new BigDecimal(2)));
				} else if (StringUtils.equals("4", String.valueOf(data.get("PAY_TYPE")))) {	// 季繳
					data.put("INSYEARFEE_YEAR",TOTAL_INSYEARFEE.multiply(new BigDecimal(4)));
				} else if (StringUtils.equals("12", String.valueOf(data.get("PAY_TYPE")))) {// 月繳
					data.put("INSYEARFEE_YEAR", TOTAL_INSYEARFEE.multiply(new BigDecimal(12)));
				} else {	// 年繳
					data.put("INSYEARFEE_YEAR", TOTAL_INSYEARFEE);
				}
			}
			
			if (String.valueOf(data.get("IS_MAIN")) != null) {
				if (StringUtils.equals("N", String.valueOf(data.get("IS_MAIN")))) {
					BigDecimal lstTotalFee = new BigDecimal(0);
					if (null != dataList.get(mainCount).get("TOTAL_INSYEARFEE")) {
						lstTotalFee = new BigDecimal(String.valueOf(dataList.get(mainCount).get("TOTAL_INSYEARFEE")));
					} else {
						lstTotalFee = new BigDecimal(String.valueOf(dataList.get(mainCount).get("INSYEARFEE_YEAR")));
					}
					BigDecimal nowFee = new BigDecimal(String.valueOf(data.get("INSYEARFEE_YEAR")));
					
					dataList.get(mainCount).put("TOTAL_INSYEARFEE", lstTotalFee.add(nowFee));
				} else {
					data.put("TOTAL_INSYEARFEE", new BigDecimal(String.valueOf(data.get("INSYEARFEE_YEAR"))));
					mainCount = count;
				}
			}
			
			// 保單號碼換行
			if (((String)data.get("POLICY_NBR")).length() > 10) {
				tempPolicyNbr = String.valueOf(data.get("POLICY_NBR"));
				data.remove("POLICY_NBR");
				data.put("POLICY_NBR", tempPolicyNbr.substring(0, 10) + "\n" + tempPolicyNbr.substring(10));
			}
			
			// 有效日期轉換
			if (data.get("EFFECTED_DATE") != null) {
				effectedDate = (Date)data.get("EFFECTED_DATE");
				data.put("EFFECTED_DATE", new SimpleDateFormat("yyyy/MM/dd").format(effectedDate));
			}
			
			// 保險對象轉換
			if (data.get("INSURED_OBJECT") == null) {
				data.remove("INSURED_OBJECT");
				data.put("INSURED_OBJECT", "本人");
			}
			
			// 狀態一律顯示正常件
			data.remove("STATUS");
			data.put("STATUS", "正常件");
			
			// 保額
			if (data.get("UPQTY_SEL") == null) {
				data.remove("UPQTY_SEL");
				data.put("UPQTY_SEL", data.get("INSUREDAMT"));
			}
			
			// 繳別轉換
			if (data.get("PAY_TYPE") != null) {
				tempPayType = payType.get(String.valueOf(data.get("PAY_TYPE")));
				data.remove("PAY_TYPE");
				data.put("PAY_TYPE", tempPayType);
			}
			
			// 繳費年期轉換
			if (data.get("LIST_Y") != null && data.get("ITEM_Y") != null) {
				String item = ObjectUtils.toString(data.get("ITEM_Y"));
				String list = ObjectUtils.toString(data.get("LIST_Y"));
				String paymentyearSel = ObjectUtils.toString(data.get("PAYMENTYEAR_SEL"));
				int index = -1;
				
				if(StringUtils.isBlank(list) || StringUtils.isBlank(paymentyearSel)){
					data.put("PAYMENTYEAR_SEL", "");
				}
				
				GenericMap itemResult = new GenericMap(
					InsjlbUtils.findSimilarIntVal(item , paymentyearSel , "," , true));
				
				if(MapUtils.isNotEmpty(itemResult.getParamMap())) {
					data.put("PAYMENTYEAR_SEL", 
						list.split(",")[itemResult.getBigDecimal("idx").intValue()]
					);
				}
				else{
					data.put("PAYMENTYEAR_SEL" , list.split(",")[0]);
				}		

			}
			
			// 取得保險年齡
			Date birthday = sdf.parse(String.valueOf(data.get("INSURED_BIRTHDAY")));
			effectedDate = sdf_slash.parse(String.valueOf(data.get("EFFECTED_DATE")));
			long between = (new Date().getTime() - effectedDate.getTime())/(24*60*60*1000);
			
			ins810inputVO.setBirthday(birthday);
			ins810outputVO = ins810.getAge(ins810inputVO);
			data.put("INSURED_AGE", ins810outputVO.getAge()-between/365);
			
			count++;
		}
		
		String txnCode = "INS141";
		ReportIF report = null;
		
		ReportGeneratorIF gen = ReportFactory.getGenerator();
		
		ReportDataIF data = new ReportData();
		data.addRecordList("INSSEQList" , INSSEQList);
		data.addRecordList("dataList" , dataList);
		
		String reportID = "R1";
		
		report = gen.generateReport(txnCode , reportID , data);
		return report;
	}
}
