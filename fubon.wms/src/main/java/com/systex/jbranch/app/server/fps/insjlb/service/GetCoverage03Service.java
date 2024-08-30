package com.systex.jbranch.app.server.fps.insjlb.service;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.systex.jbranch.app.server.fps.cmjlb210.GetCoverage03InputVO;
import com.systex.jbranch.app.server.fps.cmjlb210.GetCoverage03OutputVO;
import com.systex.jbranch.app.server.fps.insjlb.InsjlbParamInf;
import com.systex.jbranch.app.server.fps.insjlb.InsjlbUtils;
import com.systex.jbranch.app.server.fps.insjlb.callBack.InsjlbCallBackFactory;
import com.systex.jbranch.app.server.fps.insjlb.vo.DoGetCoverage03InputVO;
import com.systex.jbranch.app.server.fps.insjlb.vo.DoGetCoverage03OutputVO;
import com.systex.jbranch.comutil.callBack.CallBackExcute;
import com.systex.jbranch.comutil.collection.CollectionSearchUtils;
import com.systex.jbranch.comutil.collection.GenericMap;
import com.systex.jbranch.platform.common.errHandle.JBranchException;

import java.util.Collections;
/**健檢攤列的各項資料(多筆)(報表)*/
@Service("GetCoverage03Service")
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
@SuppressWarnings({ "rawtypes", "unchecked" })
public class GetCoverage03Service extends GetCoverageService implements GetCoverage03ServiceInf {
	private Logger logger = LoggerFactory.getLogger(GetCoverage03Service.class);
	
	public DoGetCoverage03OutputVO doGetCoverage03(DoGetCoverage03InputVO inputVo) throws JBranchException {
		//result
		DoGetCoverage03OutputVO outputVO = new DoGetCoverage03OutputVO();
		
		//呼叫保險資訊源Coverage03參數
		GetCoverage03InputVO getCoverage03InputVO = null;
		
		//保險資訊源Coverage03回傳結果
		GetCoverage03OutputVO coverage03OutputVO = null;
		
		//合併給附項目
		List<Map> lstExpressionStructureList = null;
		
		//保單錯誤訊息
		List lstLogTableErMsg = null;
		
		//資料源Coverage03回來的給附說明中間檔
		List<Map> lstExpressionList = null;
		
		List<Map<String, Object>> sortList = getInsjlbDao().querySortMap();
		
		//驗證傳參、檢查保單資料
		if(CollectionUtils.isNotEmpty(lstLogTableErMsg = GetInsValiateUtils.validate(inputVo))){
			outputVO.setLstLogTable(lstLogTableErMsg);
			return outputVO;
		}
		
		//整理要呼叫保單資訊源Coverage03資料，主檔、明細、親屬關係
		getCoverage03InputVO = initCallCoverage03Data(inputVo.getLstInsData() , inputVo.getLstFamily());
		
		//呼叫資料源Coverage037
		coverage03OutputVO = getCmjlb210().getCoverage03(getCoverage03InputVO);
		
		//取得資料源的給附中間檔
		lstExpressionList = coverage03OutputVO.getLstExpression();
		
		for(Map lstExMap : lstExpressionList){
			lstExMap.put("IS_DETAIL" , InsjlbParamInf.SUM_DESC.equals(lstExMap.get("INSCOMPANY")) ? "0" : "1");
		}
		
		//將非家庭戶(個人)的資訊移除
		removeNotInFamily(inputVo.getIncludeAll() , coverage03OutputVO , inputVo.getLstFamily());
		
		//轉換保單狀態為原值
		reSetPolStatusChrToVal(lstExpressionList);
		
		//給付項目中文說明增加paymode欄位
		addLstExPaymode(sortList , lstExpressionList , coverage03OutputVO.getLstNewReportExpression());
		
		//合計資料加工，補上資訊源回來合計資料的客戶姓名，並將單筆資料補上合計資料
		lstExpressionList = processLstExSumData(lstExpressionList);
		
		//設定當年度還本金、滿期金給付項目
		doSetRefundOriginalMaturityAmt(coverage03OutputVO , sortList);
		
		//合計lstExpression 中SORTNO=R0001 or R0002的數字到合計項目
		addSumWholeLifeDtlMoney(coverage03OutputVO , lstExpressionList , sortList);
		
		// 項目 61122 & 61115 只會被併到 B0012_S (單位：萬)
		// 單位不一樣會有問題 mantis : 5148
//		specialCaseSortNoProcess(lstExpressionList, "B0012");
//		specialCaseSortNoProcess(lstExpressionList, "61122");
//		specialCaseSortNoProcess(lstExpressionList, "61115");
//		specialCaseSortNoProcess(lstExpressionList, "61119");
		
		//合併給附項目
		lstExpressionStructureList = mergerPayment(lstExpressionList);
		
		/**補上合計標記*/
		addIsDetailFlag(lstExpressionStructureList);
		
		outputVO.setLstPolicyDetail(getCoverage03InputVO.getLstPolicyDetail());
		outputVO.setLstPolicyMaster(getCoverage03InputVO.getLstPolicyMaster());
		outputVO.setLstExpression(lstExpressionStructureList);
		outputVO.setLstWholeLife(coverage03OutputVO.getLstWholeLife());
		outputVO.setLstPremPerMonth(coverage03OutputVO.getLstPremPerMonth());
		outputVO.setLstPremDetail(coverage03OutputVO.getLstPremDetail());
		outputVO.setLstTmpExpression(coverage03OutputVO.getLstTmpExpression());
		outputVO.setLstAllPolicyObj(coverage03OutputVO.getLstAllPolicyObj());
		outputVO.setLstWholeLifeDtl(coverage03OutputVO.getLstWholeLifeDtl());
		outputVO.setLstLogTable(coverage03OutputVO.getLstLogTable());

		return outputVO;
	}
	
	/**將資料轉換成主檔、明細、親屬關係*/
	public GetCoverage03InputVO initCallCoverage03Data(List<Map> lstInsDataList , List<Map<String , Object>> lstFamily) throws JBranchException{
		//整理要呼叫保單資訊源Coverage03資料，mast、detail、relation
		GetCoverage03InputVO getCoverage03InputVO = new GetCoverage03InputVO();
		Map<String , List<Map<String , Object>>> policyMap = super.initPolicyInfSourceData(lstInsDataList , lstFamily);
		getCoverage03InputVO.setLstPolicyDetail(policyMap.get(POLICY_DETAIL));//明細
		getCoverage03InputVO.setLstPolicyMaster(policyMap.get(POLICY_MAST));//主檔
		getCoverage03InputVO.setLstRelation(policyMap.get(LST_RELATION));//親屬關係
		
		return getCoverage03InputVO;
	}
	
	
	/**設定當年度還本金、滿期金給付項目
	 * @throws JBranchException 
	 */
	public void doSetRefundOriginalMaturityAmt(GetCoverage03OutputVO coverage03OutputVO , List<Map<String, Object>> sortList) throws JBranchException{	
		//判斷
		if(CollectionUtils.isEmpty(coverage03OutputVO.getLstWholeLifeDtl()) || CollectionUtils.isEmpty(coverage03OutputVO.getLstExpression())){
			return;
		}
		
		List<Map> lstWholeLifeDtlList = coverage03OutputVO.getLstWholeLifeDtl();
		List<Map> lstExpressionList = coverage03OutputVO.getLstExpression();
		
		//整理後的資料
		GenericMap relstWholeLifeDtlGmap = new GenericMap();
		Iterator<Entry<String , GenericMap>> relstWholeLifeDtlIter = null;
		
		//排除保單號不在給付中間檔的保單號
		List<String> noLstExpression = new ArrayList();
		
		//系統年度
		final int sysRocYear = Calendar.getInstance().get(Calendar.YEAR) - 1911;
		
		//最後整理結果
		List newLstExpressionList = new ArrayList();
		
		//先找出各個保單號，將資料先整理起來
		for(Map whoLifeDtl : lstWholeLifeDtlList){
			GenericMap whoLifeDtlGmap = new GenericMap(whoLifeDtl);
			
			GenericMap reGenericMap = null;
			List<GenericMap> lstWholeLifeDtlTmpList = null;
			String issseq = whoLifeDtlGmap.getNotNullStr(INSSEQ);
			
			//排除沒有保單號、沒有年分、沒有給附中間檔的資料
			if(StringUtils.isBlank(issseq) || whoLifeDtlGmap.isNull("ROCYEAR") || noLstExpression.indexOf(issseq) != -1){
				continue;
			}
			
			//取得此單號已處理過的資料
			reGenericMap = relstWholeLifeDtlGmap.get(issseq);
			
			//若無整理過就做初始化
			if(reGenericMap == null){
				Map sumMap = new HashMap();
				sumMap.put(INSSEQ , issseq);
				
				List<Map> result = CollectionSearchUtils.selectMapByKey(lstExpressionList, sumMap, INSSEQ);			
				Map lstExpression = CollectionUtils.isNotEmpty(result) ? result.get(0) : null;
				
				//如果lstExpression沒這保單號，排除
				if(lstExpression == null){
					//記錄此單無給付中間檔
					noLstExpression.add(issseq);
					continue;
				}
		
				//初始化
				relstWholeLifeDtlGmap.put(issseq , reGenericMap = new GenericMap()
					.put("lstExpression", lstExpression)
					.put("lstWholeLifeDtlList", new ArrayList())
				);
			}

			//將此筆資料整理到同保單號的資料之中
			lstWholeLifeDtlTmpList = reGenericMap.get("lstWholeLifeDtlList");
			lstWholeLifeDtlTmpList.add(whoLifeDtlGmap);
		}
		
		//如果整理出來都沒有與給附編號相符的資料就跳出
		if(MapUtils.isEmpty(relstWholeLifeDtlGmap.getParamMap())){
			return;
		}
		
		//開始逐筆保單號找出當年度還本金、滿期金給付項目
		relstWholeLifeDtlIter = relstWholeLifeDtlGmap.getParamMap().entrySet().iterator();
		
		while(relstWholeLifeDtlIter.hasNext()){
			Entry<String , GenericMap> entry = relstWholeLifeDtlIter.next();
			//保單號
			String insseq = entry.getKey();
			//同保單號彙整資料
			GenericMap reGenericMap = entry.getValue();
			//同保單集合
			List<GenericMap> lstWholeLifeDtlTmpList = reGenericMap.get("lstWholeLifeDtlList");
			//單筆給付中間檔
			Map lstExpression = reGenericMap.get("lstExpression");
			//當年度還本金
			GenericMap tmpRePay1 = null;
			//滿期金給付項目
			GenericMap tmpRePay2 = null;
			
			//依照日期排序
			Collections.sort(lstWholeLifeDtlTmpList , new Comparator<GenericMap>() {
				public int compare(GenericMap o1, GenericMap o2) {
					return o1.getBigDecimal("ROCYEAR").compareTo(o2.getBigDecimal("ROCYEAR"));
				}
			});
			
			//依照排序後日期由小到大開始找出當年度還本金、滿期金給付項目
			for(GenericMap whoLifeDtlGmap : lstWholeLifeDtlTmpList){
				int rocYear = whoLifeDtlGmap.getBigDecimal("ROCYEAR").intValue();
				int repay = whoLifeDtlGmap.getBigDecimal("REPAY").intValue();
				
				//與系統年度相同為當年度還本金的資料，大於等於系統年度同時有repay的資料為滿期金給付項目
				// 如果 ROCYEAR = 系統日期之民國年 ,若REPAY > 0 then tmpRePay1=此筆REPAY
				if(sysRocYear == rocYear && repay > 0){
					tmpRePay1 = whoLifeDtlGmap;
					tmpRePay2 = whoLifeDtlGmap;
				}
				else if(rocYear > sysRocYear && repay > 0){
					tmpRePay2 = whoLifeDtlGmap;
				}
			}
			
			//R0001、R0002流水號
			int tmpRePayIdx = 1;
			
			for(GenericMap tmpRePay : new GenericMap[]{tmpRePay1 , tmpRePay2}){
				BigDecimal repay = BigDecimal.ZERO;
				
				if(tmpRePay == null || (repay = tmpRePay.getBigDecimal("REPAY")).intValue() == 0){
					tmpRePayIdx++;
					continue;
				}
				
				String sortno = "R000" + tmpRePayIdx;
				Map<String , Object> sortMap = CollectionSearchUtils.findMapInColleciton(sortList , "SORTNO" , sortno);
				String dsc = null;
				
				if(sortMap != null){
					ObjectUtils.toString(sortMap.get("DSC"));
				}
				
				Map newLstExpression = new HashMap();
				newLstExpression.put("FIRSTKIND" , "R");//給付編號的第一層分類
				newLstExpression.put("SECONDKIND" , "00");//給付編號的第二層分類
				newLstExpression.put("FIRSTKINDDESC" , "還本");//給付編號的第一層中文說明
				newLstExpression.put("SECONDKINDDESC" , "還本金額");//給付編號的第二層中文說明
				newLstExpression.put("EXPRESSDESC" , dsc);//給付編號的中文名
				newLstExpression.put("BEGUNITPRICE" , repay);//給付起值
				newLstExpression.put("ENDUNITPRICE" , repay);//給付迄值
				newLstExpression.put("DESCRIPTION" , repay);//給付起迄值
				newLstExpression.put("CUSTNAME" , lstExpression.get("CUSTNAME"));//客戶姓名
				newLstExpression.put("CUSTPOLICY" , lstExpression.get("CUSTPOLICY"));//保單序號+客戶姓名或客戶姓名
				newLstExpression.put("RELEXPRESSION" , 0);//RelExpression
				newLstExpression.put("SORTNO" , sortno);//給付編號
				newLstExpression.put("MUL_UNIT" , 0);//倍數
				newLstExpression.put("RELATIONCODE" , lstExpression.get("RELATIONCODE"));//親屬關係代碼
				newLstExpression.put("BIRTH" , lstExpression.get("BIRTH"));//客戶生日
				newLstExpression.put("INSCOMPANY" , lstExpression.get("INSCOMPANY"));//保險公司名稱
				newLstExpression.put("POLCYNO" , lstExpression.get("POLCYNO"));//保單序號
				newLstExpression.put("SIGNDATE" , lstExpression.get("SIGNDATE"));//簽約日期
				newLstExpression.put("INSQUANTITY" , lstExpression.get("INSQUANTITY"));	//數量
				newLstExpression.put("PAYTYPEPEMIUM" , lstExpression.get("PAYTYPEPEMIUM"));//每期保費
				newLstExpression.put("MAINPROD" , lstExpression.get("MAINPROD"));//主約險種代號
				newLstExpression.put("POLICYSTATUS" , lstExpression.get("POLICYSTATUS"));//保單狀態
				newLstExpression.put("INSSEQ" , lstExpression.get(INSSEQ));//保單自定序號
				newLstExpression.put("INSURED_ID" , lstExpression.get("INSURED_ID"));//被保險人ID
				newLstExpression.put("POLICYNO" , lstExpression.get("POLICYNO"));//保單號碼
				newLstExpressionList.add(newLstExpression);
				tmpRePayIdx++;
			}
		}

		coverage03OutputVO.getLstExpression().addAll(newLstExpressionList);
	}
	
	/**補上IS_DETAIL*/
	public void addIsDetailFlag(List<Map> lstExpressionList){
		for(Map lstExpressionMap : lstExpressionList){
			if(StringUtils.isNotBlank(ObjectUtils.toString(lstExpressionMap.get("IS_DETAIL")))){
				continue;
			}
			
			//合計是0 非合計為1
			lstExpressionMap.put("IS_DETAIL" , InsjlbParamInf.SUM_DESC.equals(ObjectUtils.toString(lstExpressionMap.get("INSCOMPANY"))) ? "0" : "1");
		}
		
	}
	
	/**合計lstExpression 中SORTNO=R0001 or R0002的數字到'合計'項目**/
	public void addSumWholeLifeDtlMoney(GetCoverage03OutputVO coverage03OutputVO , List<Map> lstExpressionList , List<Map<String, Object>> sortList){
		Map<String , Map> begEndUnitPriceMap = new HashMap();
		List newSumData = new ArrayList();
		Object paytypepemium = null;
		
		Map sumMap = new HashMap();
		sumMap.put(INSCOMPANY , InsjlbParamInf.SUM_DESC);
		Map exMap = CollectionSearchUtils.findMapByKey(lstExpressionList, sumMap, INSCOMPANY);
	
		if(MapUtils.isNotEmpty(exMap)){
			paytypepemium = exMap.get("PAYTYPEPEMIUM");
		}
			
		for(Map lstExpression : lstExpressionList){
			GenericMap dataGenMap = null;
			BigDecimal begunitPrice = null;
			BigDecimal endunitPrice = null;
			BigDecimal begunitPriceSum = null;
			BigDecimal endunitPriceSum = null;
			GenericMap lstExpressionGenMap = new GenericMap(lstExpression);
			
			//分組條件INSURED_ID , SORTNO
			String insuredId = lstExpressionGenMap.getNotNullStr("INSURED_ID");
			String sortno = lstExpressionGenMap.getNotNullStr("SORTNO");

			String groupKey = "(" +insuredId + ")|(" + sortno + ")";
			
			if(!sortno.matches("R000[1,2]")){
				continue;
			}
			
			//同KEY為同MAP做給負起訖加總
			dataGenMap = new GenericMap(begEndUnitPriceMap.get(groupKey) == null ? 
				new HashMap() : begEndUnitPriceMap.get(groupKey));
			
			//給付起值加總
			begunitPriceSum = dataGenMap.getBigDecimal("BEGUNITPRICE");
			//給付迄值加總
			endunitPriceSum = dataGenMap.getBigDecimal("ENDUNITPRICE");
			//給付起值
			begunitPrice = lstExpressionGenMap.getBigDecimal("BEGUNITPRICE");
			//給付迄值
			endunitPrice = lstExpressionGenMap.getBigDecimal("ENDUNITPRICE");
			
			//起訖加總
			dataGenMap.put("BEGUNITPRICE" , begunitPriceSum.add(begunitPrice)); 
			dataGenMap.put("ENDUNITPRICE" , endunitPriceSum.add(endunitPrice));
			dataGenMap.put("INSURED_ID", lstExpressionGenMap.get("INSURED_ID"));
			begEndUnitPriceMap.put(groupKey , dataGenMap.getParamMap());
		}
		
		//加總後開始製作合計資料
		for(String key : begEndUnitPriceMap.keySet()){
			GenericMap lstExpressionA = new GenericMap(begEndUnitPriceMap.get(key));
			final String begEndInsuredId = lstExpressionA.getNotNullStr("INSURED_ID");
			
			//同被保人id取一筆資料作為要新增的合計資料的資料來源
			Map lstExpression = (Map)CollectionUtils.find(lstExpressionList , new Predicate(){
				public boolean evaluate(Object object) {					
					return begEndInsuredId.equals(ObjectUtils.toString(((Map)object).get("INSURED_ID")));
				}
			});
			
			String sortno = key.replaceAll("(.*\\|)|(\\(|\\))", "");
			
			Map<String , Object> sortMap = CollectionSearchUtils.findMapInColleciton(sortList , "SORTNO" , sortno);
			String expressdesc = null;
			
			if(MapUtils.isNotEmpty(sortMap)){
				expressdesc = ObjectUtils.toString(sortMap.get("DSC"));
			}
			
			BigDecimal begunitPrice = lstExpressionA.get("BEGUNITPRICE");
			BigDecimal endunitPrice = lstExpressionA.get("ENDUNITPRICE");
			String description = "";
			
			if(begunitPrice.doubleValue() == endunitPrice.doubleValue()){
				if(endunitPrice.doubleValue() != 0){
					description = NumberFormat.getNumberInstance().format(begunitPrice);
				}
			}
			else{
				description += NumberFormat.getNumberInstance().format(begunitPrice) + "~"; 
				description += NumberFormat.getNumberInstance().format(endunitPrice);
			}
				
			Map newSumDataMap = new HashMap();
			newSumDataMap.put("FIRSTKIND" , "R");//給付編號的第一層分類
			newSumDataMap.put("SECONDKIND" , "00");//給付編號的第二層分類
			newSumDataMap.put("FIRSTKINDDESC" , "還本");//給付編號的第一層中文說明
			newSumDataMap.put("SECONDKINDDESC" , "還本金額");//給付編號的第二層中文說明
			newSumDataMap.put("EXPRESSDESC" , expressdesc);//給附中文
			newSumDataMap.put("BEGUNITPRICE" , begunitPrice);//給付起值
			newSumDataMap.put("ENDUNITPRICE" , endunitPrice);//給付迄值
			newSumDataMap.put("DESCRIPTION" , description);//給付起迄值
			newSumDataMap.put("CUSTNAME" , lstExpression.get("CUSTNAME"));//客戶姓名
			newSumDataMap.put("CUSTPOLICY" , InsjlbParamInf.BLANK);//保單序號+客戶姓名或客戶姓名
			newSumDataMap.put("RELEXPRESSION" , 0);
			newSumDataMap.put("SORTNO" , sortno);//給付編號
			newSumDataMap.put("MUL_UNIT" , 0);//倍數
			newSumDataMap.put("RELATIONCODE" , lstExpression.get("RELATIONCODE"));//親屬關係代碼
			newSumDataMap.put("BIRTH" , lstExpression.get("BIRTH"));//客戶生日
			newSumDataMap.put("INSCOMPANY" , InsjlbParamInf.SUM_DESC);//保險公司名稱
			newSumDataMap.put("POLCYNO" , InsjlbParamInf.BLANK);//保單序號
			newSumDataMap.put("SIGNDATE" , InsjlbParamInf.BLANK);//簽約日期
			newSumDataMap.put("INSQUANTITY" , InsjlbParamInf.BLANK);//數量
			newSumDataMap.put("PAYTYPEPEMIUM" , paytypepemium);//每期保費
			newSumDataMap.put("MAINPROD" , InsjlbParamInf.BLANK);//主約險種代號
			newSumDataMap.put("POLICYSTATUS" , InsjlbParamInf.BLANK);//保單狀態
			newSumDataMap.put("INSSEQ" , lstExpression.get(INSSEQ));//保單自定序號
			newSumDataMap.put("INSURED_ID" , lstExpression.get("INSURED_ID"));//被保險人ID
			newSumData.add(newSumDataMap);
		}
		
		lstExpressionList.addAll(newSumData);
	}

	/**給付項目中文說明增加paymode欄位**/
	public void addLstExPaymode(List<Map<String, Object>> sortList , List<Map>...lstExpressionLists) throws JBranchException{
		for(List<Map> lstExpressionList : lstExpressionLists){
			for(Map lstExpression : lstExpressionList){
				String exSortNo = ObjectUtils.toString(lstExpression.get("SORTNO"));
				Map<String , Object> sortMap = CollectionSearchUtils.findMapInColleciton(sortList , "SORTNO" , exSortNo);
				
				//更新ExpressDESC = sortList內相同SORTNO的DSC
				if(MapUtils.isNotEmpty(sortMap)){
					lstExpression.put("ExpressDESC ", sortMap.get("DSC"));
				}
			}
		}
	}
	
	/**處理合計資料，補上合計姓名以及將單筆資料補上合計資料**/
	public List<Map> processLstExSumData(List<Map> lstExpressionList) throws JBranchException{
		List<Map> sumDataList = new ArrayList();//合計資料
		List<Map> singleSumList = new ArrayList();//要新增的合計資料
		List<Map> noSumDataList = null;
		
		final String checkColumn[] = {
			//被保險人Id			客戶姓名				親屬代碼				  生日					每期保費
			"INSURED_ID" 	, 	"CUSTNAME" 		, 	"RELATIONCODE" 	,	 "BIRTH" 		, 	"PAYTYPEPEMIUM" 	, "SORTNO"
		};
		
		//原值copy
		String [] cloneKeys  = {
			//給付編號的第一層分類		 給付編號的第二層分類		給付編號的第一層中文說明	給付編號的第二層中文說明	給付編號的中文名
			"FIRSTKIND" 	,	"SECONDKIND" 	,	"FIRSTKINDDESC" ,	"SECONDKINDDESC",	"EXPRESSDESC" 	,  
			//給付起值				給付迄值				給付起迄值				客戶姓名				RelEpression
			"BEGUNITPRICE" 	,	"ENDUNITPRICE" 	,	"DESCRIPTION" 	,	"CUSTNAME" 		,	"RELEXPRESSION" ,
			//給付編號				倍數					親屬關係代碼			客戶生日				被保人ID
			"SORTNO" 		,	"MUL_UNIT" 		,	"RELATIONCODE" 	,	"BIRTH" 		,	"INSURED_ID"	,
			//繳別/每期保費
			"PAYTYPEPEMIUM"
		};
		
		//空白欄位
		String [] blankKeys = {
			//保單序號+客戶姓名		主約險種代號			保單狀態				保單號碼				簽約日期			
			"CUSTPOLICY"	,	"MAINPROD"		,	"POLICYSTATUS"	,	"POLCYNO"		,	"SIGNDATE"		,	
			//數量				保單ID(Key)
			"INSQUANTITY" 	,	"INSSEQ"	
		};
		
		//找出非合計資料
		noSumDataList = CollectionSearchUtils.selectMapNotInList(lstExpressionList , "INSCOMPANY" , InsjlbParamInf.SUM_DESC);
		
		for(final Map lstExpression : lstExpressionList){
			//不是合計的跳掉
			if(!InsjlbParamInf.SUM_DESC.equals(lstExpression.get("INSCOMPANY"))){
				continue;
			}
			
			//保單號
			String insuredId = ObjectUtils.toString(lstExpression.get("INSURED_ID"));	
			//找出同保單號非合計的一筆
			Map<String , Object> poilicyData = CollectionSearchUtils.findMapInColleciton(noSumDataList , "INSURED_ID", insuredId);
			
			if(MapUtils.isNotEmpty(poilicyData)){
				String custName = ObjectUtils.toString(lstExpression.get("CUSTNAME"));
				String policyCustName = ObjectUtils.toString(poilicyData.get("CUSTNAME"));
				
				if(StringUtils.isBlank(custName) && StringUtils.isNotBlank(policyCustName)){
					lstExpression.put("CUSTNAME" , policyCustName);
				}
			}
		}
		
		//將lstExpressionList中INSCOMPANY欄位為"合計"的資料取出
		sumDataList = CollectionSearchUtils.selectMapInList(lstExpressionList , "INSCOMPANY" , InsjlbParamInf.SUM_DESC);
		
		//逐筆判斷是否為單筆沒合計的資料，找出來後針對此筆新增一筆合計
		for(final Map lstExpression : noSumDataList){
			final String insuredId = ObjectUtils.toString(lstExpression.get("INSURED_ID"));
			Map sumMap = null;
			
			//找出此保單的合計資料
			Map<String , Object> sumPoilicyData = CollectionSearchUtils.findMapInColleciton(sumDataList , "INSURED_ID", insuredId);
			//找出已處理過的合計資料
			Map<String , Object> singleSumMap = CollectionSearchUtils.findMapByKey(singleSumList , lstExpression , checkColumn);
			
			if(sumPoilicyData != null || singleSumMap != null){
				continue;
			}

			sumMap = new HashMap();
			
			for(String key : cloneKeys){
				sumMap.put(key , lstExpression.get(key));
			}
			
			for(String key : blankKeys){
				sumMap.put(key , InsjlbParamInf.BLANK);
			}
			
			sumMap.put("INSCOMPANY" , InsjlbParamInf.SUM_DESC);
			
			singleSumList.add(sumMap);//將新增的合計塞到singleSumList
		}

		if(CollectionUtils.isNotEmpty(singleSumList)){
			lstExpressionList.addAll(singleSumList);
		}
		
		return lstExpressionList;
	}
	

	/**轉換保單狀態為原值：給付中間檔狀態資訊源會回傳中文，轉換回PARAM_CODE*/
	public void reSetPolStatusChrToVal(List<Map> lstExpressionList) throws JBranchException{
		List<Map<String , Object>> polStatus = getInsjlbDao().queryParameterConf("TBFPS_INS.POL_STATUS");		
	
		if(CollectionUtils.isEmpty(polStatus) || CollectionUtils.isEmpty(lstExpressionList)){
			return;
		}
		
		//給附中間檔將中文做取代回對應的代碼
		for(Map lstExpression : lstExpressionList){
			GenericMap lstexGmap = new GenericMap(lstExpression);
		
			for(Map<String , Object> polStatusMap : polStatus){
				if(lstexGmap.equals("POLICYSTATUS" , polStatusMap.get("PARAM_NAME"))){
					lstexGmap.put("POLICYSTATUS", polStatusMap.get("PARAM_CODE"));
				}
			}
		}
	}
	
	/**將非家庭戶(個人)的資訊移除*/
	public void removeNotInFamily(String includeAll , GetCoverage03OutputVO covOutputVO , List<Map<String , Object>> lstFamail) throws JBranchException{
		if(StringUtils.isNotBlank(includeAll) && !"N".equals(includeAll)){
			return;
		}
		// 保障領回中間檔
		covOutputVO.setLstWholeLife(InsjlbUtils.changeCoverage(covOutputVO.getLstWholeLife() , lstFamail));
		// 給說明中間檔
		covOutputVO.setLstExpression(InsjlbUtils.changeCoverage(covOutputVO.getLstExpression() , lstFamail));
		// 逐月保費中間檔
		covOutputVO.setLstPremPerMonth(InsjlbUtils.changeCoverage(covOutputVO.getLstPremPerMonth() , lstFamail));
		// 保費明細中間檔
		covOutputVO.setLstPremDetail(InsjlbUtils.changeCoverage(covOutputVO.getLstPremDetail() , lstFamail));
		// 給付編號流水檔
		covOutputVO.setLstNewReportExpression(InsjlbUtils.changeCoverage(covOutputVO.getLstNewReportExpression() , lstFamail));
		// 檢視總表合計用
		covOutputVO.setLstTmpExpression(InsjlbUtils.changeCoverage(covOutputVO.getLstTmpExpression() , lstFamail));
		// 客戶姓名+流水號多筆
		covOutputVO.setLstWholeLifeDtl(InsjlbUtils.changeCoverage(covOutputVO.getLstWholeLifeDtl() , lstFamail));			
	}
	
	/**合併給附項目**/
	public List<Map> mergerPayment(List<Map> lstExpressionList) throws JBranchException{
		if(CollectionUtils.isEmpty(lstExpressionList)){
			return lstExpressionList;
		}
		
		//取得回呼物件，在排除B0013時，會設定IS_DETAIL，合計為0，非合計為1
		CallBackExcute initDetailCallback = InsjlbCallBackFactory.DO_SET_IS_DETAIL.getCallBack();
		List<Map> lstExpressionStructureList = selectNotB0013(lstExpressionList, null);
		
//		specialCaseSortNoProcess(lstExpressionStructureList, "B0012");
//		specialCaseSortNoProcess(lstExpressionStructureList, "61122");
//		specialCaseSortNoProcess(lstExpressionStructureList, "61115");
//		specialCaseSortNoProcess(lstExpressionStructureList, "61119");
		
		//進行第一次合併給付項目，將原給附項目與第一次合併結果組合起來
		lstExpressionStructureList.addAll(
			GetCoverage03SumStructure.newInstance("TBFPS_INS.SUM_STRUCTURE_1").sumXml(lstExpressionStructureList)
		);
		
		//進行第二次合併給付項目，將原給付項目、第一次與第二次的合併項目組合起來
		lstExpressionStructureList.addAll(
			GetCoverage03SumStructure.newInstance("TBFPS_INS.SUM_STRUCTURE_2").sumXml(lstExpressionStructureList)
		);
		
		for(Map lstExMap : lstExpressionStructureList){
			lstExMap.put("IS_DETAIL" , InsjlbParamInf.SUM_DESC.equals(lstExMap.get("INSCOMPANY")) ? "0" : "1");
		}
	
		return lstExpressionStructureList;
	}
	
//	private void specialCaseSortNoProcess(List<Map> listMap, String sortNo) {
//		Map sortMap = CollectionSearchUtils.findMapInColleciton(listMap, "SORTNO", sortNo);
//		if(MapUtils.isNotEmpty(sortMap)) {
//			sortMap.put("BEGUNITPRICE", new GenericMap(sortMap).getBigDecimal("BEGUNITPRICE").divide(new BigDecimal(10000), 1, BigDecimal.ROUND_DOWN));
//			sortMap.put("ENDUNITPRICE", new GenericMap(sortMap).getBigDecimal("ENDUNITPRICE").divide(new BigDecimal(10000), 1, BigDecimal.ROUND_DOWN));
//			sortMap.put("DESCRIPTION", ObjectUtils.toString(sortMap.get("BEGUNITPRICE")) + "~" + ObjectUtils.toString(sortMap.get("ENDUNITPRICE")));
//		}
//	}
}
