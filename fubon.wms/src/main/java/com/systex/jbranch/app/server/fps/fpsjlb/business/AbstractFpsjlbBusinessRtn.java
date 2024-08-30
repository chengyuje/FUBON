package com.systex.jbranch.app.server.fps.fpsjlb.business;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.systex.jbranch.app.server.fps.fpsjlb.conf.TableType;
import com.systex.jbranch.app.server.fps.fpsutils.FPSUtils;
import com.systex.jbranch.comutil.collection.CollectionSearchUtils;
import com.systex.jbranch.comutil.collection.GenericMap;
import com.systex.jbranch.platform.common.errHandle.JBranchException;

import java.util.Collections;

@SuppressWarnings({ "unchecked", "rawtypes" })
/**產品帶碼類型的相同的共同區間*/
abstract public class AbstractFpsjlbBusinessRtn extends AbstractFpsjlbBusiness{
	/**對應月報日報酬的日期欄位設定，可以透過傳入參數paramGenericMap來判斷各種情況所需要用的設定*/
	abstract public TableType getTbConifg(GenericMap paramGenericMap);
	private static Logger logger = LoggerFactory.getLogger(AbstractFpsjlbBusinessRtn.class);
	
	/**撈取資料源*/
	public GenericMap findSource(GenericMap paramGenericMap) throws Exception{
		//傳入傳出參數
		List<Map<String, Object>> ret = paramGenericMap.get(RET_LIST);
		//資料表對應設定
		TableType type = getTbConifg(paramGenericMap);
		//產品代碼 + 產品類型的索引位置
		List<Map<String, Object>> queryParamList = new ArrayList<Map<String , Object>>();
		//商品代碼 + 商品類型數量
		int prdIdTypesize = ret.size();
		//權重
		double[] weights = new double[prdIdTypesize];
		//年化日報酬率
		double[][] returnArr = null;
		//傳入商品的日期共同區間，處理中會做順向排序
		List<String> dateSortList = new ArrayList<String>();
		//歷史報酬查詢結果
		List<Map<String, Object>> historyList = null;
				
		for(int idx = 0 ; idx < ret.size() ; idx++){
			GenericMap retGmap = new GenericMap(ret.get(idx));
			//設定權重
			weights[idx] = retGmap.get(WEIGHT) == null ? 0D : retGmap.getBigDecimal(WEIGHT).doubleValue();
			//設定商品要撈取資料的id 及 type，並記錄各個的索引位置
			Map<String , Object> prdIdDateMap = new HashMap<String , Object>();
			prdIdDateMap.put(PRD_ID, retGmap.getNotNullStr(PRD_ID));
			prdIdDateMap.put(PRD_TYPE, retGmap.getNotNullStr(PRD_TYPE));
			prdIdDateMap.put("idx", idx);
			queryParamList.add(prdIdDateMap);
		}
		
		//找出共同區間，同時將共同區間的日期，塞入每一筆商品之中用以用來查詢歷史報酬
		queryParamList = doSetSameSortDayInPrIdDate(
			queryParamList , 
			dateSortList , 
			paramGenericMap.getBigDecimal(DATA_DATE_YEAR).intValue() , 
			paramGenericMap
		);
		
		if(CollectionUtils.isEmpty(queryParamList) || CollectionUtils.isEmpty(dateSortList) ){
			logger.info("No identical date range data");
			throw new Exception("無共同區間");			
		}
		
		//查詢歷史報酬
		historyList = fpsjlbDao.queryHistoryData(type , queryParamList);
		
		//刪除沒有匯率的資料區間
		removeNoRate(historyList , dateSortList , type);
		
		if(CollectionUtils.isEmpty(historyList) || CollectionUtils.isEmpty(dateSortList) ){
			logger.info("No identical date range data , because " + type.getDayColumnName() + " is empty");
			throw new Exception("無共同區間");
		}
		
		//初始化二為矩陣[產品代碼 + 產品類型數量][由小到大的日報酬日期數量]
		returnArr = new double[prdIdTypesize][dateSortList.size()];
		
		//資料後製處理
		return afterFindSource(queryParamList , dateSortList , historyList , returnArr , weights , type);
	}
	
	/**篩選並排續共同區間*/
	public List<Map<String, Object>> doSetSameSortDayInPrIdDate(
		List<Map<String, Object>> prdIdDateList , List<String> dayList , int year , GenericMap paramGenericMap
	) throws Exception{
		TableType type = getTbConifg(paramGenericMap);
		Integer size = prdIdDateList.size();
		Map<String , Integer> sameDayMap = new HashMap<String , Integer>();
		List<Map<String, Object>> newPrdIdDateList = new ArrayList<Map<String, Object>>();
		
		//計算共同區間
		List<Map<String , Object>> result = getFpsjlbDao().queryDateByIdType(
			new String[]{
				type.getDayColumnName()
			} ,  
			type , 
			year , 
			prdIdDateList
		);
		
		//計算每一個日期區間的商品數
		for(Map<String , Object> map : result){			
			String key = ObjectUtils.toString(map.get(type.getDayColumnName()));
			int idx = sameDayMap.get(key) == null ? 0 : sameDayMap.get(key);
			sameDayMap.put(key , idx + 1);
		}
		
		//排除非所有商品都有的共同區間日期
		for(Entry entry : sameDayMap.entrySet()){
			//商品的共同日期不符
			if(!size.equals(entry.getValue())){
				continue;
			}
			
			dayList.add((String)entry.getKey());
		}
		
		//做日期排序
		Collections.sort(dayList);
		
		//將每一個日期塞入商品之中
		for(String day : dayList){
			for(Map prdIdDaye : prdIdDateList){
				Map map = new HashMap();
				map.putAll(prdIdDaye);
				map.put(type.getDayColumnName() , day);
				newPrdIdDateList.add(map);
			}
		}
		
		return newPrdIdDateList;
	}
	
	public List<Map<String , Object>> removeNoRate(Collection historyList , List<String> dateSortList , TableType type) throws JBranchException{
		final String[] notNullCols = type.getNotNullColumn();
		Set<String> revDaySortList = new HashSet();
		
		//取得沒有匯率的日期
		for(Map<String , Object> his : ((List<Map<String , Object>>)CollectionUtils.select(historyList, new Predicate(){
			public boolean evaluate(Object obj) {
				Map<String , Object> his = (Map<String , Object>)obj;
				for(String key : notNullCols){
					if(his.get(key) == null){
						return true;
					}
				}
				return false;
			}
		}))){
			revDaySortList.add((String)his.get(type.getDayColumnName()));
		}
		
		//刪除沒有匯率的區間資料
		for(String day : revDaySortList){
			historyList.removeAll(
				CollectionSearchUtils.selectMapInList(historyList, type.getDayColumnName() , day)
			);
			
			dateSortList.remove(dateSortList.indexOf(day));
		}
		
		return null;
	}
	
	/**資料後製處理*/
	public GenericMap afterFindSource(List<Map<String, Object>> queryParamList , List<String> dateSortList , 
		List<Map<String , Object>> historyData , double[][] returnArr , double[] weights , TableType type ) throws Exception {
		int financialProductIdx = 0;
		int dayIdx = 0;
		
		//將歷史報酬做forEach
		for(Map<String , Object> historytMap : historyData){
			GenericMap historytGmap = new GenericMap(historytMap);
			String dataDate = historytGmap.get(type.getDayColumnName());
			
			//抓出商品的位應索引位置
			GenericMap idTypeIdxGmap = new GenericMap(
				CollectionSearchUtils.findMapByKey(
					queryParamList, 
					historytMap, 
					PRD_ID , 
					PRD_TYPE
				)
			);
			
			if(idTypeIdxGmap == null || idTypeIdxGmap.get("idx") == null){
				throw new Exception("not found history index");
			}
			
			financialProductIdx = idTypeIdxGmap.getBigDecimal("idx").intValue();
			dayIdx = dateSortList.indexOf(dataDate);
			
			System.out.println(historytGmap.getNotNullStr(PRD_ID) + historytGmap.getNotNullStr(PRD_ID) + " " + dataDate + ":" + financialProductIdx + "/" + dayIdx);
			
			//把年化月報酬放到對應位置
			returnArr[financialProductIdx][dayIdx] = historytGmap.getBigDecimal(type.getRateName()).doubleValue();
		}
		
		return new GenericMap().put(WEIGHT, weights).put(type.getRateName() , returnArr);
	}

	/**
	 * 
	 * @param mfdEtfInsList
	 * @param maxMonth
	 * @param minMonth
	 * @param fullYear
	 * @return Object[] 
	 * 			0 為共同區間 
	 * 			1 為所有資料 
	 * 			2 為權重資料 
	 * 			3 為各檔商品年化報酬率
	 * @throws Exception
	 */
	public Object[] getAllRtnDatas(List<Map<String, Object>> mfdEtfInsList, int maxMonth, int minMonth, boolean fullYear) throws Exception {
		Object[] objArr = new Object[4]; 
		
		double[] rtnYDatas = new double[mfdEtfInsList.size()];
		String[] rtnPrd = new String[mfdEtfInsList.size()];
		
		String year = null, month = null, currentYearMonth = null;
		year = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
		month = String.valueOf(Calendar.getInstance().get(Calendar.MONTH));
		year = "0".equals(month) ? String.valueOf(Calendar.getInstance().get(Calendar.YEAR) - 1) : year;
		month = "0".equals(month) ? "12" : (month.length() == 1 ? "0" + month : month);
		currentYearMonth = year + month;
		
		
		// 取得共同區間
		List<Map<String, Object>> intervalList = fpsjlbDao.queryCommonIntervalList(FPSUtils.groupProdIDList(mfdEtfInsList), mfdEtfInsList, maxMonth, fullYear, currentYearMonth);
		
		// 如果沒有共同區間 或是 不足一年(12個月)
	    if (CollectionUtils.isEmpty(intervalList) || intervalList.size() < minMonth) {
	    	throw new Exception("沒有共同區間，或共同區間不足 1 年不進行計算");
	    }
	    
	    String[] intervalStrList = new String[intervalList.size()];
	    int countInteval = 0;
	    for(Map<String, Object> intervalMap : intervalList) {
	    	intervalStrList[countInteval] = ObjectUtils.toString(intervalMap.get("DATA_YEARMONTH"));
	    	countInteval ++;
	    }

	    String[] intervalStartEnd = FPSUtils.getCommonInterval(intervalList);
	    
	    // 取得共同區間 + 所有資料
	    List<Map<String, Object>> getDataResource = fpsjlbDao.queryYRateDataResource(FPSUtils.groupProdIDList(mfdEtfInsList), mfdEtfInsList, intervalStartEnd);

	    if (CollectionUtils.isEmpty(getDataResource)) {
	    	throw new Exception("沒有共同區間，或共同區間不足 "+ (maxMonth / 12) + "年不進行計算");
	    }

	    double[][] rtnDatas = new double[mfdEtfInsList.size()][intervalList.size()];
	    // 處理資料並運算
	    // 先分類
	    Map<String, List<Map<String, Object>>> groupMap = new HashMap<String, List<Map<String, Object>>>();
	    for(Map<String, Object> mfdEtfInsMap : mfdEtfInsList) {
	      String prdType = ObjectUtils.toString(mfdEtfInsMap.get("PRD_TYPE"));
	      String prdId = ObjectUtils.toString(mfdEtfInsMap.get("PRD_ID"));
	      String targets = ObjectUtils.toString(mfdEtfInsMap.get("TARGETS"));
	      List<Map<String, Object>> firstFind = CollectionSearchUtils.selectMapInList(getDataResource, "PRDTYPE", prdType);
	      if("INS".equals(prdType)) {
	    	  groupMap.put(prdId, reCombineInsDatas(prdId, targets, firstFind, intervalStrList));
	      } else {
	    	  List<Map<String, Object>> currentPrdIdList = CollectionSearchUtils.selectMapInList(firstFind, "PRDID", prdId);
	    	  groupMap.put(prdId, currentPrdIdList);
	      }
	    }
//	    Map<String, List<Map<String, Object>>> groupMap = FPSUtils.groupCombine(mfdEtfInsList, getDataResource);
	    
	    

	    int i =0;
	    for(Entry<String, List<Map<String, Object>>> entry : groupMap.entrySet()) {
	    	int j = 0;
	    	for(Map<String, Object> groupData : entry.getValue()) {
	    		rtnDatas[i][j] = ((BigDecimal)groupData.get("ONEMONTH")).divide(new BigDecimal(100), 8, BigDecimal.ROUND_HALF_UP).doubleValue();
	    		j++;
	    	}
	    	
	    	BigDecimal yRateGrandTotal = FPSUtils.getYRate(entry.getValue());

	        // 要開根號的
	        BigDecimal divideYear = new BigDecimal(12).divide(new BigDecimal(entry.getValue().size()), 6, BigDecimal.ROUND_HALF_UP);

	        // 年化報酬率
	        BigDecimal yRateYear = new BigDecimal(Math.pow((BigDecimal.ONE.add(yRateGrandTotal)).doubleValue(), divideYear.doubleValue())).subtract(BigDecimal.ONE).setScale(6, BigDecimal.ROUND_HALF_UP);
	        
	    	rtnYDatas[i] = yRateYear.doubleValue();
	    	rtnPrd[i] = entry.getKey();
	    	System.out.println(entry.getKey() + " 的年化報酬:" + rtnYDatas[i]);
	    	i++;
	    }
	    objArr[0] = intervalStartEnd;
	    objArr[1] = rtnDatas;
	    objArr[2] = rtnYDatas;
	    objArr[3] = rtnPrd;
	    return objArr;
	}
	
	private List<Map<String, Object>> reCombineInsDatas(String insPrdId, String targetsStr, List<Map<String, Object>> allInsData, String[] intervalArr) throws JBranchException {
		List<Map<String, Object>> reCombineInsList = new ArrayList<Map<String, Object>>();
		String[] targets = targetsStr.split("/");
		Object[] currentPrdIdInsData = new Object[targets.length];
  	  	int i = 0;
		for(String target : targets) {
  	  		currentPrdIdInsData[i] = CollectionSearchUtils.selectMapInList(allInsData, "PRDID", target);
  	  		i ++;
		}
		
		for(String interval : intervalArr) {
			Map<String, Object> reCombineInsMap = new HashMap<String, Object>();
			double sum = 0.0d;
			for(Object obj : currentPrdIdInsData) {
				List<Map<String, Object>> targetList = (List<Map<String, Object>>) obj;
				Map<String, Object> targetMap = CollectionSearchUtils.findMapInColleciton(targetList, "DATEMONTH", interval);
				sum += ((BigDecimal)targetMap.get("ONEMONTH")).doubleValue();
			}
			
			reCombineInsMap.put("DATEMONTH", interval);
			reCombineInsMap.put("PRDTYPE", "INS");
			reCombineInsMap.put("PRDID", insPrdId);
			reCombineInsMap.put("ONEMONTH", new BigDecimal(sum / targets.length));
			reCombineInsList.add(reCombineInsMap);
		}
		return reCombineInsList;
	}
}
