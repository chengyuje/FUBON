package com.systex.jbranch.app.server.fps.insjlb.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ObjectUtils;

import com.systex.jbranch.app.server.fps.cmsub302.CMSUB302Util;
import com.systex.jbranch.app.server.fps.insjlb.InsjlbUtils;
import com.systex.jbranch.comutil.collection.CollectionSearchUtils;
import com.systex.jbranch.comutil.collection.GenericMap;
import com.systex.jbranch.platform.common.errHandle.JBranchException;

public class GetCoverage03SumStructure{
	
	public static GetCoverage03SumStructure newInstance(String paramType){
		return new GetCoverage03SumStructure(paramType);
	}
	
	private GetCoverage03SumStructure(String paramType) {
		this.paramType = paramType;
	}
	
	private Map<String, Object> dataMap = new HashMap<String, Object>(); // 存放數據的大MAP
	private Map<String, Object> bacBdataMap = new HashMap<String, Object>(); // 備份30301的資料
	private List<Map> temLst = new ArrayList<Map>();
	private String inscompanyStr = "";
	private String insseqStr = "";
	private String insuredIdStr = "";
	private Boolean isAppendBol = true; // 判斷是否需要追加
	private Boolean flagBol = false; // 標記 10401 或 10402是否有出現過
	private String paramType;

	
	public void initDataMapAndKey(Map<String, Object> lstExpression){
		initKey(lstExpression);
		
		if(dataMap == null || MapUtils.isNotEmpty(dataMap))
			dataMap = new HashMap<String, Object>();
	}
	
	/**設定key，若逐筆比對時發現key有不同代表進入下一個key的資料處理*/
	public void initKey(Map<String, Object> lstExpression){
		GenericMap tmpEx = new GenericMap(lstExpression);
		inscompanyStr = tmpEx.getNotNullStr("INSCOMPANY");//合計
		insseqStr = tmpEx.getNotNullStr("INSSEQ");//保單ID
		insuredIdStr = tmpEx.getNotNullStr("INSURED_ID");//被保人id
	}
	
	public boolean checkSameKey(GenericMap lstExpression){
		return inscompanyStr.equals(lstExpression.getNotNullStr("INSCOMPANY")) && 
			insseqStr.equals(lstExpression.getNotNullStr("INSSEQ")) && 
			insuredIdStr.equals(lstExpression.getNotNullStr("INSURED_ID"));
	}
	
	/**
	 * 合併給付項目
	 * 
	 * @param dataSou_lst
	 * @throws JBranchException
	 */
	public List<Map> sumXml(List<Map> lstExpressionList) throws JBranchException {
		if(CollectionUtils.isEmpty(lstExpressionList)){
			return null;
		}
		
		//初始化保單號、合計、客戶姓名
		initKey(lstExpressionList.get(0));
		
		//取得合併給付設定(TBFPS_INS.SUM_STRUCTURE_1 OR TBFPS_INS.SUM_STRUCTURE_2)
		Map<String, String> sumStructureConfig = InsjlbUtils.getParameter3(paramType);
		Iterator sumStructureConfigIter = null;
		
		//逐筆跑給付中間檔
		for (Map<String, Object> lstExpressionMap : lstExpressionList) {
			GenericMap lstExpression = new GenericMap(lstExpressionMap);
			
			//逐筆對應合併給付設定與給付中間檔資料
			sumStructureConfigIter = sumStructureConfig.entrySet().iterator();
			
			sumStructureFlag:
			while (sumStructureConfigIter.hasNext()) {
				Map.Entry<String, String> sumStructureConfigEntry = 
					(Map.Entry<String, String>) sumStructureConfigIter.next();
				
				//類型：_B、 _S、 _R、 _P、 _1S、 _2S、 _210S、 _209S
				String sumStructureConfigKey = sumStructureConfigEntry.getKey();
				
				//公式：例如50705+50701+50714+50513+50003或20403+20401_1S等等
				String sumStructureConfigVal = sumStructureConfigEntry.getValue();
				
				//給付編號
				String sortNo = lstExpression.getNotNullStr("SORTNO");
				
				//判斷設定中是否存在此給附編號
				if(!checkSortNo(sumStructureConfigVal , sortNo)){
					continue sumStructureFlag;
				}
				
				//合計類運算 (給付起迄值，依照給付編號加總)
				if(sumStructureConfigKey.matches(".+S$")){
					isAppendBol = false;
					caseSthen(sumStructureConfigKey, lstExpression);
				}
				//年化類運算
				else if(sumStructureConfigKey.matches(".+P$")){
					isAppendBol = false;
					casePthen(sumStructureConfigKey, sumStructureConfigVal , lstExpression);
				}
				//起迄類運算
				else if(sumStructureConfigKey.matches(".+R$")){
					isAppendBol = false;
					caseRthen(sumStructureConfigKey, lstExpression);
				}
				//終身定期類運算
				else if(sumStructureConfigKey.matches(".+B$")){
					String[] bthen = sumStructureConfigVal.split("@");
					
					//判斷@前面的公式中是否包含此筆的給付編號
					if (bthen[0].indexOf(sortNo) != -1) {
						isAppendBol = false;
						caseBthen(sumStructureConfigKey, lstExpression);
					}
					
					//若切割後有前段亦有後段，判斷後段是否包含此給付編號
					if (bthen.length == 2 && bthen[1].indexOf(sortNo) != -1) {
						flagBol = true;
					}
				}
			}
			
			//若非上述的處理，同時當下的客戶姓名、保單編號、合計不相同時，將資料倒到temLst
			if (isAppendBol && !checkSameKey(lstExpression)) {
				if (dataMap.size() > 0) {
					isDelB();
					Iterator itT = dataMap.entrySet().iterator();
					
					while (itT.hasNext()) {
						temLst.add(((Map.Entry<String, Map<String, Object>>) itT.next()).getValue());
					}
				}
				
				initDataMapAndKey(lstExpression.getParamMap());
			}
			
			temLst.add(lstExpression.getParamMap());
			isAppendBol = true;
		}
		
		//	4833
		//	合併給付項目時
		//	A B C 三檔
		//	A 產生的合併給付項目會在 B 一開始時加入
		//	B 產生的合併給付項目會在 C 一開始時加入
		//	但 C 的合併給付 卻沒再被加入
		if (dataMap.size() > 0) {
			isDelB();
			Iterator itT = dataMap.entrySet().iterator();
			
			while (itT.hasNext()) {
				temLst.add(((Map.Entry<String, Map<String, Object>>) itT.next()).getValue());
			}
		}
		
		return temLst;
	}
	
	
	/**判斷sortNo是否存在於設定中**/
	private boolean checkSortNo(String value, String sortNo) {
		Map<String, Object> split_map = new HashMap<String, Object>();
		
		//取得@與+的給附編號，例如(10301+30301@10401_S+10402+10505_S+50705_S+50706_S)
		if (value.indexOf("@") != -1) {
			for (String b_str : value.split("\\@")) {
				//判斷是否有+號
				if (b_str.indexOf("+") != -1) {
					for (String add_str : b_str.split("\\+")) {
						split_map.put(add_str, add_str);
					}
				} 
				else {
					split_map.put(b_str, b_str);
				}
			}
		}
		//取得有"乘"的給附編號
		else if (value.indexOf("*") != -1) {
			String[] muStr = null;
			
			for (String bstr : value.split("\\+")) {
				muStr = bstr.split("\\*");
				
				if (muStr.length > 0) {
					split_map.put(muStr[0], muStr[0]);
				}
			}
		}
		//取得有"除"的給附編號
		else if (value.indexOf("/") != -1) {
			String[] muStr = null;
			
			for (String bstr : value.split("\\+")) {
				muStr = bstr.split("/");
				
				if (muStr.length > 0) {
					split_map.put(muStr[0], muStr[0]);
				}
			}
		}
		//抓出有+號的給附編號
		else {
			for (String addStr : value.split("\\+")) {
				split_map.put(addStr, addStr);
			}
		}
	
		//判斷如果給付編號存在於設定中為true
		return sortNo.equals(split_map.get(sortNo));
	}
	
	/**
	 * 判斷是否刪除30301_B的項
	 */
	private void isDelB() {
		//如果10401、10402沒有出現過
		if (!flagBol) {
			//刪除30301_B
			dataMap.remove("30301_B");
			
			//如果備份30301不是空的，塞到30301_B中
			if (!bacBdataMap.isEmpty()) {
				dataMap.put("30301_B", bacBdataMap);
			}
		}
		
		flagBol = false;
		bacBdataMap = new HashMap<String, Object>();
	}

	/** 合計類運算
	 * 	案例：10401_S = 10401+10405+10420
	 *  說明10401:每日住院費, 10405:住院費用補償保險金, 10420:住院慰問金
	 *  合計BEGUNITPRICE, ENDUNITPRICE
	 */ 
	private void caseSthen(String sortno, GenericMap lstExpression) {
		BigDecimal tempBeg = BigDecimal.ZERO;
		BigDecimal tempEnd = BigDecimal.ZERO;
		BigDecimal lstExBegunitPrice = CMSUB302Util.getBigDecimal(lstExpression.get("BEGUNITPRICE"));
		BigDecimal lstExEndunitPrice = CMSUB302Util.getBigDecimal(lstExpression.get("ENDUNITPRICE"));
		Map<String, Object> newAppendMap = new HashMap<String, Object>();
		
		if("B0012_S".equals(sortno) || "B0012_2S".equals(sortno)) {
			BigDecimal beg = lstExpression.getBigDecimal("BEGUNITPRICE");
			BigDecimal end = lstExpression.getBigDecimal("ENDUNITPRICE");
			if("B0012".equals(lstExpression.get("SORTNO")) || "61122".equals(lstExpression.get("SORTNO")) || "61115".equals(lstExpression.get("SORTNO")) || "61119".equals(lstExpression.get("SORTNO"))) {
				lstExBegunitPrice = lstExpression.getBigDecimal("BEGUNITPRICE").divide(new BigDecimal(10000));
				lstExEndunitPrice = lstExpression.getBigDecimal("ENDUNITPRICE").divide(new BigDecimal(10000));
			}
		}
		
		//合計、保單號、客戶姓名相同
		if(checkSameKey(lstExpression)) {
			tempBeg = lstExBegunitPrice;//給付起值
			tempEnd = lstExEndunitPrice;//給付訖值
			
			//如果給附編號存在就加總起來
			if (dataMap.get(sortno) != null) {
				newAppendMap = (Map<String, Object>) dataMap.get(sortno);
				
				//	4833
				//	bigdecimal 在處理 自己 的四則運算必須要接值
				//	否則不會加入自己本身
				tempBeg = tempBeg.add(CMSUB302Util.getBigDecimal(newAppendMap.get("BEGUNITPRICE")));
				tempEnd = tempEnd.add(CMSUB302Util.getBigDecimal(newAppendMap.get("ENDUNITPRICE")));
			}
			
			newAppendMap.put("INSCOMPANY", lstExpression.get("INSCOMPANY"));//合計
			newAppendMap.put("INSSEQ", lstExpression.get("INSSEQ"));//保單序號
			newAppendMap.put("INSURED_ID", lstExpression.get("INSURED_ID"));
		}
		//合計、保單號、客戶姓名不相同時，代表第一筆或是換到下一筆
		else {
			isDelB();

			Iterator it = dataMap.entrySet().iterator();
			
			while (it.hasNext()) {
				temLst.add(((Map.Entry<String, Map<String, Object>>) it.next()).getValue());
			}
			
			newAppendMap.put("INSCOMPANY", inscompanyStr);
			newAppendMap.put("INSSEQ", insseqStr);
			newAppendMap.put("INSURED_ID", insuredIdStr);
			
			initDataMapAndKey(lstExpression.getParamMap());
			
			tempBeg = lstExBegunitPrice;
			tempEnd = lstExEndunitPrice;
		}

		//設定預設值
		setNewExpData(tempBeg , tempEnd , sortno , newAppendMap , lstExpression);

		if (tempBeg.compareTo(tempEnd) == 0) {
			if (tempBeg.compareTo(BigDecimal.ZERO) == 0) {
				newAppendMap.put("DESCRIPTION", "");
			} 
			else {
				newAppendMap.put("DESCRIPTION", InsjlbUtils.changeStr(tempBeg.toString()));
			}
		}
		//給付起值千分位~起付迄值千分位
		else {
			newAppendMap.put("DESCRIPTION", 
				InsjlbUtils.changeStr(tempBeg.toString()) + "~" + InsjlbUtils.changeStr(tempEnd.toString()));
		}
		
		dataMap.put(sortno, newAppendMap);
	}

	/** 年化類運算
	 *  案例：70514_P=70514*1+70502*2+70511*4+70509*12+81115*1+61120*12+81130*12
	 *  (70514:長期看護保險金(每年), 70502:長期看護保險金(每半年), 70511:長期看護保險金(每三個月), 70509:長期看護保險金(每月),
	 *  81115:特定傷病保險金(每年), 61120:特定傷病暨全殘保險金(每月), 70504:特定傷病照護保險金(每月))
	 *  合計BEGUNITPRICE, ENDUNITPRICE但需各自*1/*2/*4/*12
	 */
	private void casePthen(String sortno, String sortValue, GenericMap lstExpression) {
		BigDecimal tempBeg = BigDecimal.ZERO;
		BigDecimal tempEnd = BigDecimal.ZERO;
		BigDecimal lstExBegunitPrice = CMSUB302Util.getBigDecimal(lstExpression.get("BEGUNITPRICE"));
		BigDecimal lstExEndunitPrice = CMSUB302Util.getBigDecimal(lstExpression.get("ENDUNITPRICE"));
		Map<String, Object> newAppendMap = null;
		List<Map<String , Object>> calculatingList = new ArrayList<Map<String , Object>>();
		Matcher matcherAdd = Pattern.compile("[^+]*").matcher(sortValue);

		while(matcherAdd.find()){
			String sumStructureGrop = matcherAdd.group();
			Matcher matcherMD = Pattern.compile("(.+)(\\*|/)(.+)").matcher(sumStructureGrop);
			
			//要計算的整理到calculatingList
			if(matcherMD.find()){
				Map<String, Object> calculatingMap = new HashMap<String, Object>();
				calculatingMap.put("SORTNO", matcherMD.group(1));//乘數(給付編號)
				calculatingMap.put("CALCULATING", matcherMD.group(2));//計算符號
				calculatingMap.put("VALUE", matcherMD.group(3));//被乘數
				calculatingList.add(calculatingMap);
			}
		}
		
		//若為同保單、客戶姓名、合計
		if (checkSameKey(lstExpression)) {
			//判斷是否為已處理過的給附編號
			if(dataMap.get(sortno) != null){
				//上一次的起訖
				newAppendMap = (Map<String, Object>)dataMap.get(sortno);
				tempBeg = CMSUB302Util.getBigDecimal(newAppendMap.get("BEGUNITPRICE"));
				tempEnd = CMSUB302Util.getBigDecimal(newAppendMap.get("ENDUNITPRICE"));
			}
			else{
				newAppendMap = new HashMap();
			}
			
			for (Map<String, Object> calculatingMap : calculatingList) {
				GenericMap calculatingGenMap = new GenericMap(calculatingMap);
				
				//找出給付編號相同的，不相同的跳下一筆
				if (!calculatingGenMap.getNotNullStr("SORTNO").equals(lstExpression.getNotNullStr("SORTNO"))) {
					continue;
				}
				
				//給付編號相同
				BigDecimal value = CMSUB302Util.getBigDecimal(calculatingGenMap.get("VALUE"));
				String calculating = calculatingGenMap.getNotNullStr("CALCULATING");
				
				//乘
				if(calculating.matches("\\*")){
					tempBeg = tempBeg.add(lstExBegunitPrice.multiply(value));
					tempEnd = tempEnd.add(lstExEndunitPrice.multiply(value));	
				}
				//除
				else if(calculating.matches("/")){
					//四捨五入到整數
					tempBeg = tempBeg.add(lstExBegunitPrice.divide(value , 0 , BigDecimal.ROUND_HALF_UP));
					tempEnd = tempEnd.add(lstExEndunitPrice.divide(value , 0 , BigDecimal.ROUND_HALF_UP));
				}
				
				break;
			}
			
			newAppendMap.put("INSCOMPANY", lstExpression.get("INSCOMPANY"));//合計
			newAppendMap.put("INSSEQ", lstExpression.get("INSSEQ"));//保單序號
			newAppendMap.put("INSURED_ID", lstExpression.get("INSURED_ID"));
			
		} 
		//非同保單、同客戶、合計，代表開始處理下一次的(單、同客戶、合計)資料
		else {
			isDelB();
			Iterator it = dataMap.entrySet().iterator();//已處理過的資料
			
			//逐筆將以處理過的temLst
			while (it.hasNext()) {
				temLst.add(((Map.Entry<String, Map<String, Object>>) it.next()).getValue());
			}
		
			//針對下一個(客戶姓名、保單編號、合計)做初始化，代表上一個 (客戶姓名、保單編號、合計)已處理完畢
			newAppendMap = new HashMap<String, Object>();
			newAppendMap.put("INSCOMPANY", inscompanyStr);
			newAppendMap.put("INSSEQ", insseqStr);
			newAppendMap.put("INSURED_ID", insuredIdStr);
			
			initDataMapAndKey(lstExpression.getParamMap());

			for (Map<String, Object> calculatingMap : calculatingList) {
				GenericMap calculatingGenMap = new GenericMap(calculatingMap);
				
				//找出給付編號相同的，不相同的跳下一筆
				if (!calculatingGenMap.getNotNullStr("SORTNO").equals(lstExpression.getNotNullStr("SORTNO"))) {
					continue;
				}
				
				//給付編號相同
				BigDecimal value = CMSUB302Util.getBigDecimal(calculatingGenMap.get("VALUE"));
				
				if(calculatingGenMap.getNotNullStr("CALCULATING").matches("\\*")){
					tempBeg = lstExBegunitPrice.multiply(value);
					tempEnd = lstExEndunitPrice.multiply(value);	
				}
				//除
				else if(calculatingGenMap.getNotNullStr("CALCULATING").matches("/")){
					tempBeg = lstExBegunitPrice.divide(value , 0 , BigDecimal.ROUND_HALF_UP);
					tempEnd = lstExEndunitPrice.divide(value , 0 , BigDecimal.ROUND_HALF_UP);
				}
				
				break;
			}
		}
		
		//設定預設值
		setNewExpData(tempBeg , tempEnd , sortno , newAppendMap , lstExpression);

		if (tempBeg.compareTo(tempEnd) == 0) {
			if (tempBeg.compareTo(BigDecimal.ZERO) == 0) {
				newAppendMap.put("DESCRIPTION", "");
			} 
			else {
				newAppendMap.put("DESCRIPTION", InsjlbUtils.changeStr(tempBeg.toString()));
			}
		} else {
			newAppendMap.put("DESCRIPTION", 
				InsjlbUtils.changeStr(tempBeg.toString()) + "~" + InsjlbUtils.changeStr(tempEnd.toString()));
		}
		
		dataMap.put(sortno, newAppendMap);
	}

	//TODO:判斷@前都有同時@後其中一個出現即可進行合併計算
	/** 終身定期類運算
	 *  案例：10301+30301@10401_S+10402+10505_S+50705_S+50706_S
	 *  (10402:加護病房費, 10505_S:出院後居家療養金, 50705_S:住院手術保險金, 50706_S:門診手術保險金)
	 *  合計BEGUNITPRICE(代表終身), ENDUNITPRICE (代表定期)
	 */
	private void caseBthen(String sortno, GenericMap lstExpression) {
		BigDecimal tempBeg = BigDecimal.ZERO;
		BigDecimal tempEnd = BigDecimal.ZERO;
		BigDecimal lstExBegunitPrice = CMSUB302Util.getBigDecimal(lstExpression.get("BEGUNITPRICE"));
		BigDecimal lstExEndunitPrice = CMSUB302Util.getBigDecimal(lstExpression.get("ENDUNITPRICE"));
		Map<String, Object> newAppendMap = new HashMap<String, Object>();
		
		//若前五碼與給付編號相同，將此筆資料暫存
		//案例：10301+30301@10401_S+10402+10505_S+50705_S+50706_S，若為前五碼10301時暫存到bacBdataMap
		if (sortno.substring(0, 5).equals(lstExpression.getNotNullStr("SORTNO"))) {
			bacBdataMap.putAll(lstExpression.getParamMap());
			bacBdataMap.put("SORTNO", "30301_B");
		}
		
		//若為同保單、客戶姓名、合計
		if (checkSameKey(lstExpression)) {
			//如果此給付編號已處理過
			if (dataMap.get(sortno) == null) {
				newAppendMap = new HashMap<String, Object>();
				tempBeg = lstExBegunitPrice;
				tempEnd = lstExEndunitPrice;
			} 
			//如果此給付編號尚未處理過
			else {
				newAppendMap = (Map<String, Object>) dataMap.get(sortno);
				tempBeg = lstExBegunitPrice.add(CMSUB302Util.getBigDecimal(newAppendMap.get("BEGUNITPRICE")));
				tempEnd = lstExEndunitPrice.add(CMSUB302Util.getBigDecimal(newAppendMap.get("ENDUNITPRICE")));
			}

			newAppendMap.put("INSCOMPANY", lstExpression.get("INSCOMPANY"));//合計
			newAppendMap.put("INSSEQ", lstExpression.get("INSSEQ"));//保單序號
			newAppendMap.put("INSURED_ID", lstExpression.get("INSURED_ID"));
		} 
		//若為非同保單、客戶姓名、合計
		else {
			isDelB();
			Iterator it = dataMap.entrySet().iterator();
			
			while (it.hasNext()) {
				temLst.add(((Map.Entry<String, Map<String, Object>>) it.next()).getValue());
			}
			
			newAppendMap.put("INSCOMPANY", inscompanyStr);
			newAppendMap.put("INSSEQ", insseqStr);
			newAppendMap.put("INSURED_ID", insuredIdStr);
			
			initDataMapAndKey(lstExpression.getParamMap());

			tempBeg = lstExBegunitPrice;
			tempEnd = lstExEndunitPrice;
		}

		//設定預設值
		setNewExpData(tempBeg , tempEnd , sortno , newAppendMap , lstExpression);
		
		if (tempBeg.compareTo(BigDecimal.ZERO) == 1 && tempEnd.compareTo(BigDecimal.ZERO) == 1) {
			newAppendMap.put("DESCRIPTION", "終身型/定期型");
		} 
		else if (tempBeg.compareTo(BigDecimal.ZERO) == 1 && tempEnd.compareTo(BigDecimal.ZERO) != 1) {
			newAppendMap.put("DESCRIPTION", "終身型");
		} 
		else if (tempBeg.compareTo(BigDecimal.ZERO) != 1 && tempEnd.compareTo(BigDecimal.ZERO) == 1) {
			newAppendMap.put("DESCRIPTION", "定期型");
		} 
		else {
			newAppendMap.put("DESCRIPTION", "");
		}
		
		dataMap.put(sortno, newAppendMap);
	}

	/** 起迄類運算
	 *  案例：31204_R=31204+31212+31206
	 *  (31204:初次罹患癌症保險金, 31212:初次罹患低侵襲癌症保險金, 31206:初次罹患侵襲性癌保險金)
	 *  取最小(BEGUNITPRICE), 取最大(ENDUNITPRICE)
	 */
	private void caseRthen(String sortno, GenericMap lstExpression) {
		BigDecimal tempBeg = BigDecimal.ZERO;
		BigDecimal tempEnd = BigDecimal.ZERO;
		BigDecimal lstExBegunitPrice = CMSUB302Util.getBigDecimal(lstExpression.get("BEGUNITPRICE"));
		BigDecimal lstExEndunitPrice = CMSUB302Util.getBigDecimal(lstExpression.get("ENDUNITPRICE"));
		Map<String, Object> newAppendMap = new HashMap<String, Object>();
		
		if (checkSameKey(lstExpression)){
			if (dataMap.get(sortno) == null) {
				tempBeg = lstExBegunitPrice;
				tempEnd = lstExEndunitPrice;
			} 
			else {
				newAppendMap = (Map<String, Object>) dataMap.get(sortno);
				//取比較小的值
				tempBeg = lstExBegunitPrice.min(CMSUB302Util.getBigDecimal(newAppendMap.get("BEGUNITPRICE")));
				//取比較大的值
				tempEnd = lstExEndunitPrice.max(CMSUB302Util.getBigDecimal(newAppendMap.get("ENDUNITPRICE")));
			}
			
			newAppendMap.put("INSCOMPANY", lstExpression.get("INSCOMPANY"));//合計
			newAppendMap.put("INSSEQ", lstExpression.get("INSSEQ"));//保單序號
			newAppendMap.put("INSURED_ID", lstExpression.get("INSURED_ID"));
		} 
		else {
			isDelB();
			
			//將處理過的dataMap逐筆
			Iterator it = dataMap.entrySet().iterator();
			
			while (it.hasNext()) {
				temLst.add(((Map.Entry<String, Map<String, Object>>) it.next()).getValue());
			}
			
			newAppendMap.put("INSCOMPANY", inscompanyStr);
			newAppendMap.put("INSSEQ", insseqStr);
			newAppendMap.put("INSURED_ID", insuredIdStr);
			
			//重新指定key代表不同筆資料
			initDataMapAndKey(lstExpression.getParamMap());

			tempBeg = lstExBegunitPrice;
			tempEnd = lstExEndunitPrice;
		}

		//設定預設值
		setNewExpData(tempBeg , tempEnd , sortno , newAppendMap , lstExpression);
		
		if (tempBeg.compareTo(tempEnd) == 0) {
			if (tempBeg.compareTo(BigDecimal.ZERO) == 0) {
				newAppendMap.put("DESCRIPTION", "");
			} 
			else {
				newAppendMap.put("DESCRIPTION", InsjlbUtils.changeStr(tempBeg.toString()));
			}
		} 
		else {
			newAppendMap.put("DESCRIPTION", 
				InsjlbUtils.changeStr(tempBeg.toString()) + "~" + InsjlbUtils.changeStr(tempEnd.toString()));
		}
		
		dataMap.put(sortno, newAppendMap);
	}
	
	
	public void setNewExpData(BigDecimal tempBeg , BigDecimal tempEnd , String sortNo , Map<String, Object> newAppendMap , GenericMap lstExpression) {
		newAppendMap.put("BEGUNITPRICE", tempBeg);
		newAppendMap.put("ENDUNITPRICE", tempEnd);
		newAppendMap.put("SORTNO", sortNo);
		newAppendMap.put("FIRSTKIND", sortNo.substring(0, 1));
		newAppendMap.put("SECONDKIND", sortNo.substring(1, 3));
		newAppendMap.put("SECONDKINDDESC", "");
		newAppendMap.put("EXPRESSDESC", sortNo);
		newAppendMap.put("CUSTPOLICY", lstExpression.get("CUSTPOLICY")); // 保單序號+客戶姓名或客戶姓名
		newAppendMap.put("RELEXPRESSION", "");
		newAppendMap.put("MUL_UNIT", 0);
		newAppendMap.put("RELATIONCODE", lstExpression.get("RELATIONCODE"));
		newAppendMap.put("BIRTH", lstExpression.get("BIRTH"));
		newAppendMap.put("SIGNDATE", lstExpression.get("SIGNDATE"));
		newAppendMap.put("INSQUANTITY", lstExpression.get("INSQUANTITY"));
		newAppendMap.put("PAYTYPEPEMIUM", lstExpression.get("PAYTYPEPEMIUM"));
		newAppendMap.put("MAINPROD", lstExpression.get("MAINPROD"));
		newAppendMap.put("POLICYSTATUS", lstExpression.get("POLICYSTATUS"));
		newAppendMap.put("INSSEQ", lstExpression.get("INSSEQ"));
		newAppendMap.put("INSURED_ID", lstExpression.get("INSURED_ID"));
		newAppendMap.put("CUSTNAME", lstExpression.get("CUSTNAME"));
		newAppendMap.put("POLICYNO", lstExpression.get("POLICYNO"));
	}
}
