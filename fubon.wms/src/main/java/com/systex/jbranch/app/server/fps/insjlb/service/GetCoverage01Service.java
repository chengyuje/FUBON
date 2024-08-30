package com.systex.jbranch.app.server.fps.insjlb.service;

import static com.systex.jbranch.comutil.collection.MapObjectUtils.arrayObjectToMap;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.systex.jbranch.app.server.fps.insjlb.InsjlbParamInf;
import com.systex.jbranch.app.server.fps.insjlb.InsjlbUtils;
import com.systex.jbranch.app.server.fps.insjlb.callBack.InsjlbCallBackFactory;
import com.systex.jbranch.app.server.fps.insjlb.vo.DoGetCoverage01InputVO;
import com.systex.jbranch.app.server.fps.insjlb.vo.DoGetCoverage01OutputVO;
import com.systex.jbranch.app.server.fps.insjlb.ws.client.vo.CoverageDataSet;
import com.systex.jbranch.app.server.fps.insjlb.ws.client.vo.Expression;
import com.systex.jbranch.app.server.fps.insjlb.ws.client.vo.GetCoverage01;
import com.systex.jbranch.comutil.collection.CollectionSearchUtils;
import com.systex.jbranch.comutil.collection.GenericMap;
import com.systex.jbranch.comutil.collection.MapObjectUtils;
import com.systex.jbranch.comutil.collection.SpecialCase;
import com.systex.jbranch.platform.common.errHandle.JBranchException;

/**健檢攤列的各項資料(單筆)*/
@Service("GetCoverage01Service")
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
@SuppressWarnings({"unchecked" , "rawtypes"})
public class GetCoverage01Service extends GetCoverageService implements GetCoverage01ServiceInf{
	public static Logger logger = LoggerFactory.getLogger(GetCoverage01Service.class);
	
	@Resource(name = "GetCoverage01Service.rePattern1")
	private Map<String , String> rePattern;

	/**呼叫doGetCoverage01取保險資訊源資料*/
	public DoGetCoverage01OutputVO doGetCoverage01(Map lstInsDetail) throws JBranchException{
		DoGetCoverage01InputVO doGetCoverage01InputVO = new DoGetCoverage01InputVO();
		doGetCoverage01InputVO.setLstInsDetail(lstInsDetail);
		return doGetCoverage01(doGetCoverage01InputVO);
	}
	
	@Override
	public DoGetCoverage01OutputVO doGetCoverage01(DoGetCoverage01InputVO inputVo) throws JBranchException {
		logger.info("in doGetCoverage01");
		DoGetCoverage01OutputVO outputVo = null;
		List<Map<String , Object>> sortList = null;
//		boolean isExNotNull = false;
		
		//檢核
		GetInsValiateUtils.validate(inputVo);

		try{
			inputVo.setCoverage01Map(InsjlbUtils.reMapKey(
				InsjlbCallBackFactory.CHG_KEY_FOR_COV01_DEF_VAL.getCallBack() , inputVo.getLstInsDetail() , rePattern)
			);
			
			//向保險資訊源取得健檢攤列的各項資料(單筆)
			outputVo = getCoverage01(inputVo);
			
			//給付中文
			sortList = getInsjlbDao().querySortMap();
			
			//逐筆設定給付編號的中文名
			if(CollectionUtils.isNotEmpty(outputVo.getLstExpression())){
				addExpressionDsc(outputVo.getLstExpression() , sortList);
			}
			
			//設定壽險保障/終身保障/意外保障/癌症保障/重大疾病的給付項目(Bxxxx)/當期還本金及滿期金			
			addOtherLstExpression(inputVo , outputVo , sortList);
			outputVo.getLstExpression().add(addDefLstEx());
			
			// mantis 5007 不用先寫判斷，直接用 outputVo.getLstExpression() 判斷
//			isExNotNull = CollectionUtils.isNotEmpty(outputVo.getLstExpression());
			
			//合併給附項目
			if(CollectionUtils.isNotEmpty(outputVo.getLstExpression())){
				outputVo.setLstExpression(mergerPayment(outputVo.getLstExpression()));
			}
	
		}catch(Exception ex){
			throw new JBranchException(ExceptionUtils.getStackTrace(ex));
		}
		
		for(Map lstExMap : outputVo.getLstExpression()){
			lstExMap.put("IS_DETAIL" , InsjlbParamInf.SUM_DESC.equals(lstExMap.get("INSCOMPANY")) ? "0" : "1");
		}
		
		return outputVo;
	}
	
	/**設定壽險保障/終身保障/意外保障/癌症保障/重大疾病的給付項目(Bxxxx)
	 * @throws JBranchException */
	public void addOtherLstExpression(
		DoGetCoverage01InputVO inputVo , DoGetCoverage01OutputVO outputVo , List<Map<String, Object>> sortList
	) throws JBranchException{

		//參考匯率All
		Map<String , BigDecimal> refExcRateMap = getIns810().queryRefExcRate();
		
		if(CollectionUtils.isNotEmpty(outputVo.getLstCoverAgePrem())){
			Map<String , Object> LstInsDetail = inputVo.getLstInsDetail();
			//原幣幣別
			String currCd = ObjectUtils.toString(LstInsDetail.get("CURR_CD"));

			//原幣參考匯率
			BigDecimal refExcRate = BigDecimal.valueOf(1.0);
			//生效日
			Date effDate = (Date) LstInsDetail.get("EFFECTED_DATE");
			//保單有效的年份
			int effCalYear = InsjlbUtils.doGetEffectedDateRealAge(effDate);
			//給付中間檔
			List exList = outputVo.getLstExpression();
			//暫存用LstExpression參考變數
			Map tempEx = null;
			
			//取有效的那一年的保障中間檔
			GenericMap lstCoverAgePrem = 
				outputVo.getLstCoverAgePrem() != null && outputVo.getLstCoverAgePrem().size() >= effCalYear ?
				new GenericMap(outputVo.getLstCoverAgePrem().get(effCalYear - 1)) : 
				new GenericMap();
			
			//取得參考匯率
			if(StringUtils.isNotBlank(currCd) && MapUtils.isNotEmpty(refExcRateMap) && refExcRateMap.get(currCd) != null){
				refExcRate = refExcRateMap.get(currCd);
			}

			//壽險保障
			if((tempEx = addLstEx("B0009" , "LIFE" , "LIFE" , sortList , refExcRate , lstCoverAgePrem)) != null  && tempEx != null){
				exList.add(tempEx);
				
				//終身保障 - 依賴B0009所做出來的Ex
				if((tempEx = addLstExB0014(inputVo , sortList , tempEx)) != null  && tempEx != null){
					exList.add(tempEx);	
				}
			}
			
			//意外險保障
			if((tempEx = addLstEx("B0010" , "PA" , "PA" , sortList , refExcRate , lstCoverAgePrem)) != null  && tempEx != null){
				exList.add(tempEx);	
			}
			
			//癌症保障
			if((tempEx = addLstEx("B0011" , "CL" , "CL" , sortList , refExcRate , lstCoverAgePrem)) != null  && tempEx != null){
				exList.add(tempEx);	
			}	
			
			//重大疾病
			if((tempEx = addLstEx("B0012" , "DDB" , "DDB" , sortList , refExcRate , lstCoverAgePrem)) != null && tempEx != null){
				exList.add(tempEx);	
			}
		}
		
		//設定當期還本金及滿期金
		addRefundOriginalMaturityAmt(inputVo , outputVo , sortList, refExcRateMap);
	}
	
	/**B00014的給付中間檔組合-依賴於B0009的給付中間檔資料*/
	public Map addLstExB0014( 
		DoGetCoverage01InputVO inputVo , 
		List<Map<String, Object>> sortList ,
		Map exB009Map
	){
		String sortNo = "B00014";
		Map exB0014Map = new HashMap();
		Map sortMap = CollectionSearchUtils.findMapInColleciton(sortList , "SORTNO" , sortNo);
		
		if(exB009Map == null || !"Y".equals(inputVo.getLstInsDetail().get("isWholeLife"))){
			return null;
		}
		
		for(Object key : exB009Map.keySet()){
			if("EXPRESSDESC".equals(key)){
				exB009Map.put("EXPRESSDESC ", sortMap == null ? "" : sortMap.get("DSC"));
				
			}
			else if("SORTNO".equals(key)){
				exB0014Map.put("SORTNO" , sortNo);
			}
			else{
				exB0014Map.put(key , exB009Map.get(key));
			}
		}
		
		return exB0014Map;
	}
	
	/**組合自訂的給付中間檔*/
	public Map addDefLstEx() {
		//要組成的LstExpression
		Map exMap = new HashMap();
		exMap.put("FIRSTKIND" , "D");
		exMap.put("SECONDKIND" , "UM");
		exMap.put("FIRSTKINDDESC" , "");
		exMap.put("SECONDKINDDESC" , "noShow");
		exMap.put("EXPRESSDESC ", "noShow");
		exMap.put("BEGUNITPRICE" , BigDecimal.ZERO);
		exMap.put("ENDUNITPRICE" , BigDecimal.ZERO);
		exMap.put("PAYMODE" , "");
		exMap.put("RELEXPRESSION" , 0);
		exMap.put("SORTNO" , "DUMMY");
		exMap.put("MUL_UNIT" , 0);
		exMap.put("IS_DETAIL" , "1");
		
		return exMap;
	}
	
	/**組合自訂的給付中間檔*/
	public Map addLstEx(
		String sortNo , String minKey , String maxKey , List<Map<String, Object>> sortList , BigDecimal refExcRate , GenericMap lstCoverAgePrem
	) throws JBranchException{
		//判斷壽險起訖
		Map sortMap = CollectionSearchUtils.findMapInColleciton(sortList , "SORTNO" , sortNo);
		//給付起值
		BigDecimal min = lstCoverAgePrem.getBigDecimal(minKey);
		//給付訖值
		BigDecimal max = lstCoverAgePrem.getBigDecimal(maxKey);
		//給付起值 - 千份未表示的字串
		String paMinStr = "";
		//給付訖值 - 千份未表示的字串
		String paMaxStr = "";
		//要組成的LstExpression
		Map exMap = new HashMap();
		
		if(BigDecimal.ZERO.equals(min) && BigDecimal.ZERO.equals(max)){
			return null;
		}

		//給付起值金額，若不為0就乘以匯率，並取四捨五入整數位
		if(!BigDecimal.ZERO.equals(min)){
			min = min.multiply(refExcRate).setScale(0 , BigDecimal.ROUND_HALF_UP);			
			exMap.put("BEGUNITPRICE" , min);				
		}
		
		//給付訖值金額，若不為0就乘以匯率，並取四捨五入整數位
		if(!BigDecimal.ZERO.equals(max)){
			max = max.multiply(refExcRate).setScale(0 , BigDecimal.ROUND_HALF_UP);
			exMap.put("ENDUNITPRICE" , max);
		}
		
		//給付迄值字串加上千分位
		paMaxStr = InsjlbUtils.changeStr(ObjectUtils.toString(
			!BigDecimal.ZERO.equals(max) ? max.divide(new BigDecimal(10000)) : BigDecimal.ZERO
		));
		
		//給付起值字串加上千分位
		paMinStr = InsjlbUtils.changeStr(ObjectUtils.toString(
			!BigDecimal.ZERO.equals(min) ? min.divide(new BigDecimal(10000)) : BigDecimal.ZERO
		));
				
		if(max.equals(min)){
			exMap.put("DESCRIPTION" , paMinStr);
		}
		else{
			exMap.put("DESCRIPTION" , paMinStr + "~" + paMaxStr);
		}
		
		exMap.put("FIRSTKIND" , "B");
		exMap.put("SECONDKIND" , "00");
		exMap.put("FIRSTKINDDESC" , "");
		exMap.put("SECONDKINDDESC" , "保障");
		exMap.put("EXPRESSDESC ", sortMap == null ? "" : sortMap.get("DSC"));
		exMap.put("PAYMODE" , "");//付費模式
		exMap.put("RELEXPRESSION" , 0);
		exMap.put("SORTNO" , sortNo);
		exMap.put("MUL_UNIT" , 0);
		
		return exMap;
	}
	
	
	/**設定當期還本金及滿期金**/
	public void addRefundOriginalMaturityAmt(
		DoGetCoverage01InputVO inputVo , 
		DoGetCoverage01OutputVO outputVo ,
		List<Map<String, Object>> sortList,
		Map<String, BigDecimal> refExcRateMap
	){
		BigDecimal tmpRePay1 = null;
		BigDecimal tmpRePay2 = null;
		Date effDate = (Date) inputVo.getLstInsDetail().get("EFFECTED_DATE");
		int effCalYear = InsjlbUtils.doGetEffectedDate(effDate);
		
		Map<String , Object> LstInsDetail = inputVo.getLstInsDetail();
		
		//原幣幣別
		String currCd = ObjectUtils.toString(LstInsDetail.get("CURR_CD"));

		//原幣參考匯率
		BigDecimal refExcRate = BigDecimal.valueOf(1.0);
		
		//取得參考匯率
		if(StringUtils.isNotBlank(currCd) && MapUtils.isNotEmpty(refExcRateMap) && refExcRateMap.get(currCd) != null){
			refExcRate = refExcRateMap.get(currCd);
		}
		
		//逐筆跑保障中間檔
		for(Map<String, Object> coverAgePrem : outputVo.getLstCoverAgePrem()){
			GenericMap covAgePremGmap = new GenericMap(coverAgePrem);
			BigDecimal theYear = covAgePremGmap.getBigDecimal("THEYEAR");//保障中間檔
			BigDecimal covRepay = covAgePremGmap.getBigDecimal("REPAY");//領回金額
			
			//領回金額要大於0，且至少要為當年度的，若否則跳下一筆
			if(covRepay.intValue() <= 0 || theYear.intValue() < effCalYear){
				continue;
			}
			
			//當年度，設定給付起值及迄值
			if(theYear.intValue() == effCalYear){
				tmpRePay1 = covRepay;
				tmpRePay2 = covRepay;
			}
			//若是超過當年度則只設定迄值，給付迄值要抓符合的最大年度的領回金額
			else if(theYear.intValue() > effCalYear && covRepay!= null){
				tmpRePay2 = covRepay;
			}
		}
		
		//如果有給付起值
		if(tmpRePay1 != null){
			tmpRePay1 = tmpRePay1.multiply(refExcRate).setScale(0, BigDecimal.ROUND_HALF_UP);
			Map sortMap = CollectionSearchUtils.findMapInColleciton(sortList , "SORTNO" , "R0001");
			Map rePaylstExpression = new HashMap();
			rePaylstExpression.put("FIRSTKIND" , "R");
			rePaylstExpression.put("SECONDKIND" , "00");
			rePaylstExpression.put("FIRSTKINDDESC" , "還本");
			rePaylstExpression.put("SECONDKINDDESC" , "還本金額");
			rePaylstExpression.put("EXPRESSDESC ", sortMap == null ? "" : sortMap.get("DSC"));
			rePaylstExpression.put("BEGUNITPRICE" , tmpRePay1);
			rePaylstExpression.put("ENDUNITPRICE" , tmpRePay1);
			rePaylstExpression.put("DESCRIPTION" , tmpRePay1);
			rePaylstExpression.put("PAYMODE" , "當年");
			rePaylstExpression.put("RELEXPRESSION" , 0);
			rePaylstExpression.put("SORTNO" , "R0001");
			rePaylstExpression.put("MUL_UNIT" , 0);
			//rePaylstExpression.put("INSCOMPANY" , InsjlbParamInf.SUM_DESC);
			outputVo.getLstExpression().add(rePaylstExpression);
		}
		
		if(tmpRePay2 != null){
			tmpRePay2 = tmpRePay2.multiply(refExcRate).setScale(0, BigDecimal.ROUND_HALF_UP);
			Map sortMap = CollectionSearchUtils.findMapInColleciton(sortList , "SORTNO" , "R0002");
			Map rePaylstExpression = new HashMap();
			rePaylstExpression.put("FIRSTKIND" , "R");
			rePaylstExpression.put("SECONDKIND" , "00");
			rePaylstExpression.put("FIRSTKINDDESC" , "還本");
			rePaylstExpression.put("SECONDKINDDESC" , "還本金額");
			rePaylstExpression.put("EXPRESSDESC ", sortMap == null ? "" : sortMap.get("DSC"));
			rePaylstExpression.put("BEGUNITPRICE" , tmpRePay2);
			rePaylstExpression.put("ENDUNITPRICE" , tmpRePay2);
			rePaylstExpression.put("DESCRIPTION" , tmpRePay2);
			rePaylstExpression.put("PAYMODE" , "一次");
			rePaylstExpression.put("RELEXPRESSION" , 0);
			rePaylstExpression.put("SORTNO" , "R0002");
			rePaylstExpression.put("MUL_UNIT" , 0);
			//rePaylstExpression.put("INSCOMPANY" , InsjlbParamInf.SUM_DESC);
			outputVo.getLstExpression().add(rePaylstExpression);
		}
	}
	
	/**逐筆將對應的給付編號塞入對應的中文**/
	public void addExpressionDsc(List<Map> lstExpressionList , List<Map<String, Object>> sortList) throws JBranchException{
		for(Map lstExpression : lstExpressionList){
			//給附編號
			String sortno = ObjectUtils.toString(lstExpression.get("SORTNO"));
			//找出此給付編號所對應的中文
			Map sortMap = CollectionSearchUtils.findMapInColleciton(sortList , "SORTNO" , sortno);
			
			if(MapUtils.isNotEmpty(sortMap)){
				lstExpression.put("EXPRESSDESC ", sortMap.get("DSC"));
			}
		}
	}
	
	/**取得健檢攤列的各項資料(單筆)**/
	public DoGetCoverage01OutputVO getCoverage01(DoGetCoverage01InputVO inputVo) throws JBranchException{
		DoGetCoverage01OutputVO outputVo = new DoGetCoverage01OutputVO();
		logger.info("before init call 25b data getCoverage01 statrt");
		try {
			outputVo.setLstCoverAgePrem(new ArrayList());//保障中間檔
			outputVo.setLstCoverageTable(new ArrayList());//保障保費中間檔
			outputVo.setLstExpression(new ArrayList());//給付中間檔
			
			//轉換特殊處理
			SpecialCase upperCase = MapObjectUtils.getInsMapToObjUpperCase();//大寫
			SpecialCase exCase = getExpressionScase();//給附中間檔的特殊轉換處理
			
			Map coverage01Map = inputVo.getCoverage01Map();
			//向資訊源要健檢攤列的各項資料(單筆)
			CoverageDataSet dataSet = getCmjlb210().getCoverage01(coverage01Map);
			
			//集合內物件轉集合內Map
			outputVo.getLstCoverAgePrem().addAll(arrayObjectToMap(dataSet.getCoverAgePremArray() , upperCase));
			outputVo.getLstCoverageTable().addAll(arrayObjectToMap(dataSet.getCoverageTableArray() , upperCase));
			outputVo.getLstExpression().addAll(arrayObjectToMap(dataSet.getExpressionArray() , upperCase , exCase));
		}
		catch(Exception ex){
			throw new JBranchException(ex);
		}
		
		logger.info("before init call 25b data getCoverage01 end");
		return outputVo;
	}
	
	public GetCoverage01 initGetCoverage01Default(GetCoverage01 getCoverage01) throws Exception{
		StringBuffer patternDef = new StringBuffer()
		.append(("GET("))
		.append("(PLAN)|(IDAY)|(ICOUNT)|(UNIT)|(KIND)|(ACCUTERM)|")
		.append("(COUNTTYPE)|(IPREMIUM)|(SOCIALSECURITY)|(FIELDG)|(FIELDX)")
		.append(")");
	
		for(Method method : getCoverage01.getClass().getMethods()){
			if(!method.getName().toUpperCase().matches(patternDef.toString())){
				continue;
			}
	
			Object value = method.invoke(getCoverage01);
			value = value == null ? "" : value;			
			getCoverage01.getClass().getMethod(method.getName().replaceFirst("^get", "set"), method.getReturnType())
				.invoke(getCoverage01 , value);
		}
		
		return getCoverage01;
	}
	
	public SpecialCase getExpressionScase(){
		return new SpecialCase(){
			public boolean isSpaceial(Method method) {
				return method.getName().matches("getMulUnit");
			}
			
			public void mapToObject(Object object, Map map) {
				map.put("MUL_UNIT" , ((Expression)object).getMulUnit());
			}
		};
	}
	
	/**合併給附項目**/
	public List<Map> mergerPayment(List<Map> lstExpressionList) throws JBranchException{
		//取得回呼物件，在排除B0013時，會設定IS_DETAIL，合計為0，非合計為1
		//CallBackExcute initDetailCallback = InsjlbCallBackFactory.DO_SET_IS_DETAIL.getCallBack();
		List<Map> lstExStructureList = selectNotB0013(lstExpressionList, null);
		Map<String , List<GenericMap>> dataMap = new HashMap();
		List<GenericMap> tmpExpression = null;
		
		List<Map> lstExpressionStructure = new ArrayList();
		List<GenericMap> allLstExpressionList = new ArrayList();
		
		//把相同給附編號的放在同一個集合之中
		for(Map exMap : lstExStructureList){
			GenericMap exGmap = new GenericMap(exMap);
			allLstExpressionList.add(exGmap);
			
			String sortNo = exGmap.getNotNullStr("SORTNO");
			
			if(dataMap.get(sortNo) == null){
				dataMap.put(sortNo , new ArrayList());				
			}
			
			dataMap.get(sortNo).add(exGmap);
		}
		
		//第一次合併
		tmpExpression = GetCoverage01SumStructure.sumXml(
			dataMap , 
			getInsjlbDao().queryParameterForType("TBFPS_INS.SUM_STRUCTURE_1")
		);
		
		if(CollectionUtils.isNotEmpty(tmpExpression)){
			allLstExpressionList.addAll(tmpExpression);
			
			for(GenericMap exGmap : tmpExpression){
				String sortNo = exGmap.getNotNullStr("SORTNO");
				
				if(dataMap.get(sortNo) == null){
					dataMap.put(sortNo , new ArrayList());				
				}
				
				if(!sortNo.equals(dataMap.get(sortNo).get(0).get("SORTNO"))) {
					dataMap.get(sortNo).add(exGmap);
				}
			}
		}
		
		if(CollectionUtils.isNotEmpty(tmpExpression = GetCoverage01SumStructure.sumXml(
			dataMap , 
			getInsjlbDao().queryParameterForType("TBFPS_INS.SUM_STRUCTURE_2")
		))){
			allLstExpressionList.addAll(tmpExpression);
			
			for(GenericMap exGmap : allLstExpressionList){
				lstExpressionStructure.add(exGmap.getParamMap());
			}
		}
		
		return lstExpressionStructure;
	}

	public Map<String, String> getRePattern() {
		return rePattern;
	}

	public void setRePattern(Map<String, String> rePattern) {
		this.rePattern = rePattern;
	}
}
