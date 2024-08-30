package com.systex.jbranch.app.server.fps.insjlb.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ObjectUtils;

import com.systex.jbranch.app.server.fps.insjlb.InsjlbUtils;
import com.systex.jbranch.comutil.collection.CollectionSearchUtils;
import com.systex.jbranch.comutil.collection.GenericMap;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class GetCoverage01SumStructure {	
	public static List<GenericMap> sumXml(Map<String , List<GenericMap>> dataMap , List<Map<String , Object>> paramList){
		List<GenericMap> resultList = new ArrayList<GenericMap>();
		
		PARAMTER_FLAG:
		for(Map parameter : paramList){
			GenericMap parameterGmap = new GenericMap(parameter);
			String paramCode = parameterGmap.getNotNullStr("PARAM_CODE");
			String paramName = parameterGmap.getNotNullStr("PARAM_NAME");
			BigDecimal tempBeg = BigDecimal.ZERO;//給付起值
			BigDecimal tempEnd = BigDecimal.ZERO;//給付迄值
			String tempDesc = "";
			boolean isFind = false;
			
			//在設定中的給附編號存在於給付中間檔時，將該筆給付中間檔的給附起訖值各自做加總
			if(paramCode.matches(".+S$")){
				//分割＋號中的各個給附編號
				for(String sortNoInParName : paramName.split("\\+")){
					if(CollectionUtils.isEmpty(dataMap.get(sortNoInParName))){
						continue;
					}
					
					//找出此設定中有出現的給附編號，找到後將起訖加總起來
					for(GenericMap exGmap : dataMap.get(sortNoInParName)){
						isFind = true;
						
						if("B0012_S".equals(paramCode) || "B0012_2S".equals(paramCode)) {
							// 項目 61122 & 61115 只會被併到 B0012_S (單位：萬)
							// 單位不一樣會有問題 mantis : 5148
							BigDecimal beg = exGmap.getBigDecimal("BEGUNITPRICE");
							BigDecimal end = exGmap.getBigDecimal("ENDUNITPRICE");
							if("B0012".equals(exGmap.get("SORTNO")) || "61122".equals(exGmap.get("SORTNO")) || "61115".equals(exGmap.get("SORTNO")) || "61119".equals(exGmap.get("SORTNO"))) {
								beg = exGmap.getBigDecimal("BEGUNITPRICE").divide(new BigDecimal(10000));
								end = exGmap.getBigDecimal("ENDUNITPRICE").divide(new BigDecimal(10000));
							}
							tempBeg = tempBeg.add(beg);
							tempEnd = tempEnd.add(end);
						} else {
							tempBeg = tempBeg.add(exGmap.getBigDecimal("BEGUNITPRICE"));
							tempEnd = tempEnd.add(exGmap.getBigDecimal("ENDUNITPRICE"));
						}
						
					}
				}
				
				//找不到給附編號，此設定不成立
				if(!isFind){
					continue PARAMTER_FLAG;
				}
				
				//設定DESCRIPTION
				//起訖相同取其一即可
				if(tempBeg.compareTo(tempEnd) == 0){
					if(!tempBeg.equals(BigDecimal.ZERO)){
						tempDesc = InsjlbUtils.changeStr(tempBeg.toString());
					}
				}
				//起訖不同：起值加總~迄值加總
				else{
					tempDesc = InsjlbUtils.changeStr(tempBeg.toString()) + "~" + InsjlbUtils.changeStr(tempEnd.toString());
				}
			}
			//終身定期類運算，當＠後面的給付編號存在，同時＠前面的給附編號存在其一，將該起訖編號的起訖值做加總
			else if(paramCode.matches(".+B$")){
				String[] paramNames = paramName.split("@");
				boolean isBefortNotEmpty = false;
				boolean isAfterNotEmpty = false;
				
				//如果有小老鼠符號，代表＠之前的給附項目要合併的話，必須小老鼠後面的給附項目都存在才進行合併
				if(paramNames.length > 1){
					CHECK_BEFORT_MOUSE:
					for(String chkSortno : paramNames[0].split("\\+")){
						if(CollectionUtils.isEmpty(dataMap.get(chkSortno))){
							continue;
						}
						
						//如果不存在小老鼠後方的給附項目，不進行合併
						isBefortNotEmpty = true;
						break CHECK_BEFORT_MOUSE;
					}
				
					CHECK_AFTER_MOUSE:
					for(String chkSortno : paramNames[1].split("\\+")){
						if(CollectionUtils.isEmpty(dataMap.get(chkSortno))){
							continue;
						}
						
						//如果不存在小老鼠後方的給附項目，不進行合併
						isAfterNotEmpty = true;
						break CHECK_AFTER_MOUSE;
					}
				}
				
				//@前後找不到給附編號，此設定不成立
				isFind = isBefortNotEmpty && isAfterNotEmpty;

				if(!isFind){
					continue PARAMTER_FLAG;
				}
				
				//將@前存在的給付起訖各自加總
				for(String sortNo : paramNames[0].split("\\+")){
					if(CollectionUtils.isEmpty(dataMap.get(sortNo))){
						continue;
					}
					
					for(GenericMap exGmap : dataMap.get(sortNo)){
						tempBeg = tempBeg.add(exGmap.getBigDecimal("BEGUNITPRICE"));
						tempEnd = tempEnd.add(exGmap.getBigDecimal("ENDUNITPRICE"));
					}
				}
				
				//設定DESCRIPTION
				if (tempBeg.compareTo(BigDecimal.ZERO) == 1 && tempEnd.compareTo(BigDecimal.ZERO) == 1) {
					tempDesc = "終身型/定期型";
				} 
				else if (tempBeg.compareTo(BigDecimal.ZERO) == 1 && tempEnd.compareTo(BigDecimal.ZERO) != 1) {
					tempDesc = "終身型";
				} 
				else if (tempBeg.compareTo(BigDecimal.ZERO) != 1 && tempEnd.compareTo(BigDecimal.ZERO) == 1) {
					tempDesc = "定期型";
				} 
			}
			//年化類運算
			else if(paramCode.matches(".+P$")){
				Pattern mdpt = Pattern.compile("(\\d+)(\\*|/)(\\d+)");
				
				for(String sortNoInParName : paramName.split("\\+")){
					Matcher matcher = mdpt.matcher(sortNoInParName);
					BigDecimal mdTempBeg = BigDecimal.ZERO;
					BigDecimal mdTempEnd = BigDecimal.ZERO;
					
					if(matcher.find()){
						String mdKey = matcher.group(1);//代表乘數的給付編號
						BigDecimal md2 = new BigDecimal(matcher.group(3));//被乘數
						
						if(CollectionUtils.isNotEmpty(dataMap.get(mdKey))){
							for(GenericMap exGmap : dataMap.get(mdKey)){
								isFind = true;
								mdTempBeg = mdTempBeg.add(exGmap.getBigDecimal("BEGUNITPRICE"));
								mdTempEnd = mdTempEnd.add(exGmap.getBigDecimal("ENDUNITPRICE"));
							}
						}
						
						//找不到給附編號，此設定不成立
						if(!isFind){
							continue;
						}
						
						//乘
						if("*".equals(matcher.group(2))){
							mdTempBeg = mdTempBeg.multiply(md2);
							mdTempEnd = mdTempEnd.multiply(md2);
						}
						//除
						else if("/".equals(matcher.group(2))){
							//四捨五入取整數
							mdTempBeg = mdTempBeg.divide(md2 , 0 , BigDecimal.ROUND_HALF_UP);
							mdTempEnd = mdTempEnd.divide(md2 , 0 , BigDecimal.ROUND_HALF_UP);
						}
					}
					
					// bigdecimal 要回設
					tempBeg = tempBeg.add(mdTempBeg);
					tempEnd = tempEnd.add(mdTempEnd);
				}
				
				if (tempBeg.compareTo(tempEnd) == 0) {
					if (tempBeg.compareTo(BigDecimal.ZERO) == 0) {
						tempDesc = "";
					} 
					else {
						tempDesc = InsjlbUtils.changeStr(tempBeg.toString());
					}
				} else {
					tempDesc = InsjlbUtils.changeStr(
						tempBeg.toString()) + "~" + InsjlbUtils.changeStr(tempEnd.toString());
				}
			}
			//起迄類運算
			else if(paramCode.matches(".+R$")){
				tempBeg = null;
				tempEnd = null;
				
				for(String sortNoInParName : paramName.split("\\+")){
					if(CollectionUtils.isEmpty(dataMap.get(sortNoInParName))){
						continue;
					}
					
					for(GenericMap exGmap : dataMap.get(sortNoInParName)){
						isFind = true;
						tempBeg = tempBeg == null ? exGmap.getBigDecimal("BEGUNITPRICE") : tempBeg;
						tempEnd = tempEnd == null ? exGmap.getBigDecimal("ENDUNITPRICE") : tempEnd;
						
						//給付起值要取最小的
						if(tempBeg.compareTo(exGmap.getBigDecimal("BEGUNITPRICE")) > 0){
							tempBeg = exGmap.getBigDecimal("BEGUNITPRICE");
						}
						
						//給付迄值要取最大的
						if(tempEnd.compareTo(exGmap.getBigDecimal("ENDUNITPRICE")) < 0){
							tempEnd = exGmap.getBigDecimal("ENDUNITPRICE");
						}
					}
				}
				
				//找不到給附編號，此設定不成立
				if(!isFind){
					continue PARAMTER_FLAG;
				}
				
				if (tempBeg.compareTo(tempEnd) == 0) {
					if (tempEnd.compareTo(BigDecimal.ZERO) == 0) {
						tempDesc = "";
					} 
					else {
						tempDesc = InsjlbUtils.changeStr(tempBeg.toString());
					}
				} 
				else {
					tempDesc = InsjlbUtils.changeStr(tempBeg.toString()) + "~" + InsjlbUtils.changeStr(tempEnd.toString());
				}
			}
			
			//合併給付項目
			GenericMap newAppendMap = new GenericMap();
			newAppendMap.put("FIRSTKIND", paramCode.substring(0, 1));//第一碼
			newAppendMap.put("SECONDKIND", paramCode.substring(1, 3));//2~3碼
			newAppendMap.put("SECONDKINDDESC", "");//給付編號的第二層中文說明
			newAppendMap.put("EXPRESSDESC", paramCode);//給付編號的中文名
			newAppendMap.put("BEGUNITPRICE", tempBeg);//給付起值
			newAppendMap.put("ENDUNITPRICE", tempEnd);//給付迄值	
			newAppendMap.put("DESCRIPTION", tempDesc);//給付起訖
			newAppendMap.put("PAYMODE", "");//付費方式
			newAppendMap.put("RELEXPRESSION", "");//RelEpression
			newAppendMap.put("SORTNO", paramCode);//給付編號
			newAppendMap.put("MUL_UNIT", 0);
			
			List list = dataMap.get(paramCode) == null ? new ArrayList() : dataMap.get(paramCode);
			
			list.add(newAppendMap);
			dataMap.put(paramCode, list);
			
			resultList.add(newAppendMap);//合併後的給附項目
		}
		
		return resultList;
	}
}
