package com.systex.jbranch.app.server.fps.fpsjlb.business;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import webcab.lib.finance.portfolio.AssetParameters;
import webcab.lib.finance.portfolio.CapitalMarket;

import com.google.gson.Gson;
import com.systex.jbranch.app.server.fps.cmjlb014.CMJLB014;
import com.systex.jbranch.app.server.fps.cmjlb014.ResultObj;
import com.systex.jbranch.app.server.fps.fpsjlb.conf.TableType;
import com.systex.jbranch.comutil.collection.GenericMap;
import com.systex.jbranch.platform.common.errHandle.JBranchException;

/**效率前緣資產配置計算*/
@Component("PortEffFrontier")
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
@SuppressWarnings({"unused" , "rawtypes" , "unchecked"})
public class PortEffFrontier extends AbstractFpsjlbBusinessRtn{
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	/**方差精度**/
	public static final double DATA_PRECISION = 0.0001;
	/**記錄空值**/
	private final static double RECORD_NULL_VAL = 0.0001;
	/**無風險市場利率**/
	private final static double CAPITAL_RATE = 0.01;
	
	public GenericMap afterFindSource(List<Map<String, Object>> queryParamList , List<String> dateSortList , 
		List<Map<String , Object>> historyData , double[][] returnArr , double[] weights , TableType type ) throws Exception {
		return super.afterFindSource(queryParamList, dateSortList, historyData, returnArr, weights, type).put(type.getDayColumnName(), dateSortList);
	}
	
	/**	效率前緣
	 * 	RET_LIST：各檔商品的相關數
	 * 		PRD_ID		產品代碼
	 * 		PRD_TYPE	產品類型(MFD:基金 ETF:指數型 INS:全委保單)
	 * 		WEIGHT		權重
	 */
	public GenericMap excute(GenericMap paramGenericMap) throws Exception {
		Double lower = paramGenericMap.getBigDecimal(LOWER).doubleValue(); // 下限
		Double upper = paramGenericMap.getBigDecimal(UPPER).doubleValue(); // 上限
		int year = paramGenericMap.get("DATA_DATE_YEAR");
		
		List<Map<String,Object>> mfdEtfInsList = paramGenericMap.get(RET_LIST);//各檔產品傳入資訊
		double[][] dblPfoRtn = null;//近一個月報酬
		String[] prdOrder = null; // 產品列表
		
		//計算用參數
		int curCount = 0; //基金檔數
		AssetParameters assetParam = new AssetParameters();//資產參數物件
		double[][] dblCovarianceMatrix = null;//方差矩陣
		double[] dblExpectedRtn = null;//期望收益
		double[] dblLowerBounds = null;//最低配置比例限制
		double[] dblUpperBounds = null;//最高配置比例限制
		boolean checkRtn = false;//有大於0的預期報酬
		double dblMaxRtn = 0;//最高報酬率
		double dblMinRtn = 0;//最低報酬率
		int nEFStartIndex = 0;//最小報酬率索引位置
		int nEFEndIndex = 0;//最大報酬率索引位置
		List nodeDataList = new ArrayList();//每一個節點上的數據
		double[] dblMktPfo = null;//市場組合的資產權重
		int interpolationPoint = paramGenericMap.getBigDecimal(POINT_SIZE).intValue();//方差矩陣長度
		
		//數學運算模組
//		CMJLB014 mathUtil = new CMJLB014();
		//效率前緣元件
		CapitalMarket capitalMarket = new CapitalMarket();
		//回傳
		GenericMap result = new GenericMap();
		
		try {
			// 取共同區間
			Object[] allObj = this.getAllRtnDatas(mfdEtfInsList, year * 12, 12, false);
			
			String[] intervalArray = (String[]) allObj[0];
			
			dblPfoRtn = (double[][]) allObj[1]; // 歷史報酬率陣列 一檔商品 year * 12 個月資料
			
			dblExpectedRtn = (double[]) allObj[2]; // 一檔商品一個報酬率(沒算權重的喔)
			
			prdOrder = (String[]) allObj[3];
			
			double[][] ddd = new double[dblExpectedRtn.length][1];
			int idd = 0;
			for(double d : dblExpectedRtn) {
				ddd[idd][0] = dblExpectedRtn[idd];
				idd++;
			}
			
			result.put("periodStartDate", intervalArray[0]);//起
			result.put("periodEndDate", intervalArray[1]);//訖
			
			curCount = dblPfoRtn.length;//取出基金筆數(擋數)
			dblCovarianceMatrix = assetParam.covarianceMatrix(dblPfoRtn);//方差矩陣
			dblExpectedRtn = assetParam.expectedReturns(ddd);//期望收益(預期報酬的估計)
			dblLowerBounds = new double[curCount];//最低配置比例限制
			dblUpperBounds = new double[curCount];//最高配置比例限制
		
			//進行報酬率檢查	
			for(int i = 0 ; i < dblExpectedRtn.length ; i++) {
				checkRtn = checkRtn || dblExpectedRtn[i] > 0 ;
			}
			
			//如果預期報酬都小於0，無法進行效率前緣計算
			if (checkRtn == false)
				throw new JBranchException("您選的標的預期報酬皆小於等於零，無法進行效率前緣計算！");
			
			//將月報酬逐筆乘以12
			for(int i = 0 ; i < dblCovarianceMatrix.length ; i++){
				for(int j = 0 ; j < dblCovarianceMatrix[0].length ; j++) {
					dblCovarianceMatrix[i][j] = dblCovarianceMatrix[i][j]*12;
				}
			}
			
			//取出每檔商品的上限與下限，用以設定最高與最低的收益比例限制
			for (int i = 0; i < curCount; i++) {
				dblLowerBounds[i] = lower; //最低收益比例限制
			    dblUpperBounds[i] = upper; //最高收益比例限制
			}
			
			// Set asset weight boundary
			//設置可以構建最優投資組合的資產權重的上限和下限
			capitalMarket.setConstraints(dblLowerBounds, dblUpperBounds);
			
			// Calculate efficient frontier curve result 
			//在預期收益的給定範圍內構建效率前緣(構建效率前緣的期望回報範圍)
			capitalMarket.calculateEfficientFrontier(
				dblCovarianceMatrix, //方差矩陣
				dblExpectedRtn, //期望收益
				interpolationPoint, //方差矩陣長度
				DATA_PRECISION //方差精度
			);			
			
			//返回效率前緣已被評估的點集合上的預期回報值
			double[] dblWholeEFPfoRtn = capitalMarket.getEfficientFrontierExpectedReturns();
			
			//評估投資組合在效率前緣建設點的風險
			double[] dblWholeEFPfoRisk = capitalMarket.getEfficientFrontierPortfolioRisks(dblCovarianceMatrix);
			
			// Remove the part of inefficient frontier curve.
			
			//取最小的報酬
			for (int i = 0 ; i < dblWholeEFPfoRisk.length - 1; i++) {
				if (dblWholeEFPfoRisk[i] < dblWholeEFPfoRisk[i + 1]) {
					nEFStartIndex = i;
					continue;
				}				
			}
			
			for (int i = 0 ; i < dblWholeEFPfoRisk.length - 1; i++) {
				if (dblWholeEFPfoRisk[i] > dblWholeEFPfoRisk[i + 1]) {
					nEFEndIndex = i;
					continue;
				}				
			}
			
			//在上下限比例中，取得效率前沿的最高預期報酬率
			result.put("MaxPfoRtn", dblMaxRtn = dblWholeEFPfoRtn[nEFEndIndex]);
			
			//在上下限比例中，取得效率前沿的最低預期報酬率
			result.put("MinPfoRtn", dblMinRtn = dblWholeEFPfoRtn[nEFStartIndex]);
			
			// Calculate the part of efficient frontier curve only.
			
			//在預期收益的給定範圍內(構建效率前緣)
			capitalMarket.calculateEfficientFrontier(dblMinRtn, dblMaxRtn, dblCovarianceMatrix, dblExpectedRtn, interpolationPoint, DATA_PRECISION);
			
			//返回維度二的數組，其中包含(效率前緣上已知投資組合的權重)
			double[][] dblEFAssetWeight = capitalMarket.getEfficientFrontierAssetWeights();
			
			//效率前緣已被評估的節點集合上的預期報酬
			double[] dblEFPfoRtn = capitalMarket.getEfficientFrontierExpectedReturns();
			
			//風險矩陣長度 - 評估投資組合在效率前緣節點集合上的風險
			double[] dblEFPfoRisk = capitalMarket.getEfficientFrontierPortfolioRisks(dblCovarianceMatrix);
			
			// Calculate market portfolio by CAPM
			//評估市場組合的總風險
			double dblMktPfoRisk = capitalMarket.marketPortfolioRisk(dblCovarianceMatrix, dblExpectedRtn, CAPITAL_RATE , DATA_PRECISION);
			
			//市場投資組合預期報酬
			double dblMktPfoRtn = capitalMarket.marketPortfolioExpectedReturn(dblCovarianceMatrix, dblExpectedRtn, CAPITAL_RATE , DATA_PRECISION);			
			
			
			// TODO 權重會有問題
			//設定每一個點上的預期報酬、風險
			for (int db_int = 0; db_int < dblEFPfoRisk.length ; db_int++) {
				Map<String, Object> tmpMap = new HashMap<String, Object>();
				
				//節點上的預期報酬
				tmpMap.put("yearRtn", dblEFPfoRtn[db_int]);
				//節點上的風險
				tmpMap.put("yearStd", dblEFPfoRisk[db_int]);
				
				List pfoallocList = new ArrayList();
				
				//設定在節點上每一檔商品的商品代碼以及權重
				for (int sub_db_int = 0; sub_db_int < curCount ; sub_db_int++) {
					Map<String, Object> tmpSubMap = new HashMap<String, Object>();
					
					//商品代碼
					tmpSubMap.put(PRD_ID , prdOrder[sub_db_int]);
					//權重
					tmpSubMap.put(WEIGHT , dblEFAssetWeight[db_int][sub_db_int]);
					
					pfoallocList.add(tmpSubMap);
				}

				tmpMap.put("PfoAllocList" , pfoallocList);
				nodeDataList.add(tmpMap);
			}
			
			//每一個節點上的數據
			result.put("EfList", nodeDataList);
			
			//Return CML dataset.
			List cmlLst = new ArrayList();//返回的efList
			
			// find asset weight of market Portfolio
			//找尋市場組合的資產權重
			dblMktPfo = new double[curCount];
			
			for (int i = 0 ; i < dblEFPfoRtn.length ; i++) {
				//如果有達到投資組合預期報酬
				if (dblEFPfoRtn[i] == dblMktPfoRtn) {
					// If found the point of expected return of market portfolio, then copy this asset weight of efficient frontier to be market portfolio
					//如果找到了市場組合的期望收益點，那麼把這個有效邊界的資產權重設定為市場組合
					for (int j = 0 ; j < dblEFAssetWeight[i].length ; j++) {
						dblMktPfo[j] = dblEFAssetWeight[i][j];						
					}
					
					break;
				}				
			}
			
			//跑兩次
			for (int db_int = 0; db_int < 2; db_int++) {
				Map<String, Object> map = new HashMap<String, Object>();
				List mktPfoLst = new ArrayList();
				
				if (db_int == 0) {
					map.put("yearRtn", CAPITAL_RATE * 100);//預期報酬：無風險市場利率 * 100
					map.put("yearStd", 0);//風險：0
				}
				else {
					map.put("yearRtn", dblMktPfoRtn * 100);//市場投資組合預期報酬 * 100
					map.put("yearStd", dblMktPfoRisk * 100);//評估市場組合的總風險 * 100
				}

				for (int sub_db_int = 0; sub_db_int < curCount ; sub_db_int++) {
					Map<String, Object> sub_map = new HashMap<String, Object>();
					
					//商品代碼
					sub_map.put(PRD_ID, ((Map<String, Object>) mfdEtfInsList.get(sub_db_int)).get(PRD_ID));
					
					//設定市場組合的資產權重
					if (db_int == 0) {
						sub_map.put(WEIGHT, 0);
					}
					else {
						sub_map.put(WEIGHT, dblMktPfo[sub_db_int]);//市場組合的資產權重
					}
					
					mktPfoLst.add(sub_map);
				}
				
				map.put("PfoAllocList", mktPfoLst);
				cmlLst.add(map);
			}
			
			result.put("CmlList", cmlLst);
		}catch (Exception ex){
			ex.printStackTrace();
			throw new JBranchException("計算效率前緣時，發生預期外的錯誤!");
		}
		
		return result;
	}
	
	/**
	 * 將月報酬無法抓取上一筆的資料均改抓下一筆存入
	 * @param mntlyRtn_arr
	 * @return
	 */
	private static double[][] setBakArray(double[][] dobuleArr){
		
		for(int indexOut_int=dobuleArr.length-1;indexOut_int>=0;indexOut_int--){
			if(dobuleArr[indexOut_int]==null){
				continue;
			}
			for(int index_int=dobuleArr[indexOut_int].length-1;index_int>=0;index_int--){
				if((index_int>=dobuleArr[indexOut_int].length-1)){//最後一筆不跑
					continue;
				}
				if(dobuleArr[indexOut_int][index_int] == RECORD_NULL_VAL){
					dobuleArr[indexOut_int][index_int] = dobuleArr[indexOut_int][index_int+1];
				}
			}
		}
		return dobuleArr;
	}

	@Override
	public TableType getTbConifg(GenericMap paramGenericMap) {
		return TableType.PORT_EFF_FRONTIER;
	}
}
