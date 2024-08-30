package com.systex.jbranch.app.server.fps.insjlb.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.systex.jbranch.app.server.fps.ins810.INS810;
import com.systex.jbranch.app.server.fps.ins810.INS810InputVO;
import com.systex.jbranch.app.server.fps.insjlb.InsjlbParamInf;
import com.systex.jbranch.app.server.fps.insjlb.InsjlbUtils;
import com.systex.jbranch.app.server.fps.insjlb.dao.FubonInsjlbDaoInf;
import com.systex.jbranch.app.server.fps.insjlb.vo.CalFamilyGapInputVO;
import com.systex.jbranch.app.server.fps.insjlb.vo.CalFamilyGapOutputVO;
import com.systex.jbranch.app.server.fps.insjlb.vo.DoGetCoverage03InputVO;
import com.systex.jbranch.app.server.fps.insjlb.vo.DoGetCoverage03OutputVO;
import com.systex.jbranch.comutil.collection.GenericMap;
import com.systex.jbranch.comutil.io.JoinDifferentSysBizLogic;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.info.FormatHelper;

/**計算家庭財務安全缺口*/
@Service("CalFamilyGapService")
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class CalFamilyGapService extends JoinDifferentSysBizLogic implements CalFamilyGapServiceInf{
	@Autowired @Qualifier("GetCoverage03Service")
	private GetCoverage03ServiceInf getCoverage03Service;
	
	@Autowired @Qualifier("ins810")
	private INS810 ins810;
	
	@Autowired @Qualifier("insjlbDao")
	private FubonInsjlbDaoInf insjlbDao;
	
	public CalFamilyGapOutputVO doCalFamilyGap(CalFamilyGapInputVO inputVo) throws JBranchException {
		setUuid(inputVo.getUuid());
		//資訊源資料
		DoGetCoverage03OutputVO doGetCoverage03OutputVO = null;
		//回傳VO
		CalFamilyGapOutputVO calFamilyGapOutputVO = null;
		//系統日
		Date sysDate = new Date();
		
		//(1)CHECK參數是否有值
		GetInsValiateUtils.validate(inputVo);
		
		//(2)取得保單健診業務參數的 退休年齡、預期壽命(男、女)
		GenericMap lifeAgeConfig = initpolicyDiagnosisBusAge();
		//預期壽命(男)
		BigDecimal manLifeAge = lifeAgeConfig.getBigDecimal("1"); 
		//預期壽命(女)
		BigDecimal womanLifeAge = lifeAgeConfig.getBigDecimal("2");
		//退休年齡
		BigDecimal retiredAge = lifeAgeConfig.getBigDecimal("3");
		
		//(3)取得家庭成員與財務安全資訊(取得本人與配偶姓名、出生日期、性別)
		GenericMap lstFamilyData = new GenericMap(
			getInsjlbDao().queryFamilyMemberFinanceSafetyInf(inputVo.getInsCustID()));
						
		//(4)判斷外部有沒有帶入保障領回中間檔，如果沒有帶入就向資訊源索取，並將保障領回中間檔、給付中間檔回塞到inputVo
		callPolicySourceInWholeLifeIsEmpty(inputVo);
		
		//(5(取得保障缺口對應SORTNO，並來初始化CalFamilyGapOutputVO的各個金額
		calFamilyGapOutputVO = builderOutputVOForSecurityAmount(inputVo);
		
		//(6)設定temp變數
		//本人的客戶id
		String tempCustID = ObjectUtils.toString(inputVo.getInsCustID());
		//本人姓名
		String tempCustName = ObjectUtils.toString(inputVo.getInsCustName());
		//本人性別
		String tempCustGender = ObjectUtils.toString(inputVo.getInsCustGender());
		//本人生日
		Date tempCustBirthday= inputVo.getInsCustBirthday();
		//保險年齡
		int tempNowAge = InsjlbUtils.calculateInsAge(sysDate , tempCustBirthday);
		//生日轉roc year
		int tempCustBirthdayRocYear = doGetYear(tempCustBirthday) - 1911;
		//生日的ROCYEAR + 預期壽命
		int tempListEndROCYear = tempCustBirthdayRocYear + (
			"1".equals(tempCustGender) ? manLifeAge.intValue() : //男
			"2".equals(tempCustGender) ? womanLifeAge.intValue() : //女
			0
		);
		
		//配偶資料Map
		GenericMap couDataMap = initCouData(inputVo , manLifeAge , womanLifeAge , retiredAge , lstFamilyData); 
		//轉roc year (tempCouBirthday的年 +參數-預期壽命(男女不同))
		int tempFinishROCYear = couDataMap.getBigDecimal("FINISH_ROC_YEAR").intValue();
		//轉roc year (tempCouBirthday的年  + 參數 - 退休年齡)
		int tempCouRetireYear = couDataMap.getBigDecimal("RETIRE_YEAR").intValue();
		//預期壽命取本人與配偶中較大的那一個  >> 改為 預期壽命取本人的餘命年度
		//tempListEndROCYear = tempListEndROCYear < tempFinishROCYear ? tempFinishROCYear : tempListEndROCYear;		

		
		//取該客戶的life(壽險保障)、pa(意外保障)、cl(癌症保障)、ai(特定意外保障)、ddb(重大疾病保障)逐年的最大金額
		Map<Integer, BigDecimal> lifeInsuranceMaxAmt = wholeLifeMaxAmtForEveryYear(tempCustID , inputVo.getLstWholeLife());
		
		//(7)起始民國年: 系統日期的民國年+1 
		int tempStartROCYear = doGetYear(sysDate) - 1911 + 1;

		//(8)依照家庭財務資料計算子女教育費用
		GenericMap lstSppeduDataMap = doGetLstSppedu(tempCustID , tempStartROCYear , sysDate);
		//尚須繳納最大年度
		int tempEduEndYear = lstSppeduDataMap.getBigDecimal("MAX_ROC_YEAR").intValue();
		//各個年度到子女教育結束年度尚需繳納的金額整理
		Map<Integer , BigDecimal> relstEduDetailMap = lstSppeduDataMap.get("RE_LST_EDU_DETAIL_MAP");

		//(9)依照家庭財務資料計算生活費用
		GenericMap lifeFeeGenMap = doGetLifeFeeMap(tempStartROCYear , tempFinishROCYear , tempEduEndYear , lstFamilyData);
		Map<Integer , BigDecimal> lifeFeeMap = lifeFeeGenMap.get("LIFE_FEE_MAP");
		BigDecimal firstYearLifeFee = lifeFeeGenMap.get("FIRST_YEAR_LIFE_FEE");
		
		//(10)依照家庭財務資料計算房貸費用
		GenericMap houseDebtGenMap = doGetHouseDebtMap(tempStartROCYear , lstFamilyData);
		Map<Integer , BigDecimal> houseDebtMap = houseDebtGenMap.get("HOURSE_DEBT_MAP");
		BigDecimal firstYearHourceLifeFee = houseDebtGenMap.get("FIRST_YEAR_HOURSE_LIFE_FEE");

		//(11)依照家庭財務資料計算其他費用(車貸/卡債/其他融資)
		GenericMap otherDebtGenMap = doGetOtherDebtMap(tempStartROCYear , lstFamilyData);
		Map<Integer , BigDecimal> otherDebtMap = otherDebtGenMap.get("OTHER_DEBT_MAP");
		BigDecimal firstYearOtherAmt = otherDebtGenMap.get("FIRST_YEAR_OTHER_AMT");
		
		//(12)照家庭財務資料計算配偶收入
		GenericMap couIncomeGenMap = doGetCouIncomeMap(tempStartROCYear , tempFinishROCYear , tempCouRetireYear , lstFamilyData);
		Map<Integer , BigDecimal> couIncomeMap = couIncomeGenMap.get("COU_INCOME_MAP"); 

		//(13)照家庭財務資料計算理財收入
		GenericMap rentIncomeGenMap = doGetRentIncomeMap(tempStartROCYear , tempFinishROCYear , lstFamilyData);
		Map<Integer , BigDecimal> rentIncomeMap = rentIncomeGenMap.get("RENT_INCOME_MAP");
		
		//(14)計算缺口，並產生OUTPUTVO回傳		
		//遺產稅(萬元)
		BigDecimal taxIn = lstFamilyData.get("TAX_IN");
		taxIn = taxIn == null ? BigDecimal.ZERO : taxIn.setScale(0 , BigDecimal.ROUND_HALF_UP);
		//現金及活期存款(萬元)
		BigDecimal cashAmt = lstFamilyData.get("CASH_AMT");
		//股票(萬元)
		BigDecimal stockAmt = lstFamilyData.get("STOCK_AMT");
		//定存(萬元)
		BigDecimal ctAmt = lstFamilyData.get("CT_AMT");
		//共同基金(萬元)
		BigDecimal fundAmt = lstFamilyData.get("FUND_AMT");
		//連動債(萬元)
		BigDecimal snAmt = lstFamilyData.get("SN_AMT");
		//保單投資型(萬元)
		BigDecimal investInsAmt = lstFamilyData.get("INVEST_INS_AMT");
		//自用不動產(萬元)
		BigDecimal selfImmoveAmt = lstFamilyData.get("SELF_IMMOVE_AMT");
		//投資型不動產(萬元)
		BigDecimal investImmoveAmt = lstFamilyData.get("INVEST_IMMOVE_AMT");
		//信託資產(萬元)
		BigDecimal trustAmt = lstFamilyData.get("TRUST_AMT");
		//最大缺口
		BigDecimal tempMaxGap = BigDecimal.ZERO;
		//現有資產
		BigDecimal allAsset = sum(
			cashAmt , stockAmt , ctAmt , fundAmt , snAmt , investInsAmt , selfImmoveAmt , investImmoveAmt , trustAmt
		);
		BigDecimal maxGap = BigDecimal.ZERO;
		
		//初始化費用不足明細
		calFamilyGapOutputVO.setLstCashFlow(new ArrayList());
		
		int i = 1;
		
		for(int rocYear = tempStartROCYear ; rocYear <= tempListEndROCYear ; rocYear++){
			GenericMap cashFlowGmap = new GenericMap();
			
			//配偶收入：抓rocYear那一年
			BigDecimal couIncome = couIncomeMap.get(rocYear);
			
			//理財收入(房租收入)：抓rocYear那一年
			BigDecimal finIncome = rentIncomeMap.get(rocYear);
			
			//房貸費用：抓rocYear那一年
			BigDecimal houseFee = houseDebtMap.get(rocYear);
			
			//教育費用：抓rocYear那一年
			BigDecimal educationFee = relstEduDetailMap.get(rocYear);
			
			//其他費用：抓rocYear那一年
			BigDecimal otherFee = otherDebtMap.get(rocYear);
			
			//生活費用：抓rocYear那一年
			BigDecimal lifeFee = lifeFeeMap.get(rocYear);
			
			//應備費用合計：生活費用 + 房貸費用 + 教育費用 + 其他費用 + 遺產稅
			BigDecimal feeSum = sum(lifeFee , houseFee , educationFee , otherFee , taxIn);
			
			//抓rocYear那一年此客戶的壽險保障、意外保障、癌症保障、特定意外保障、重大疾病保障中最大金額
			BigDecimal thisYearLifeInsAmt = lifeInsuranceMaxAmt != null ? lifeInsuranceMaxAmt.get(rocYear) : null;
			
			//壽險保障
			BigDecimal insAmt = thisYearLifeInsAmt == null ? 
				BigDecimal.ZERO : thisYearLifeInsAmt.divide(BigDecimal.valueOf(10000) , 0 , BigDecimal.ROUND_DOWN);
			
			//已備費用合計：配偶收入 + 理財收入 + 現有資產 + 壽險保障
			BigDecimal incomAll = sum(couIncome , finIncome , allAsset , insAmt);
			
			//不足費用
			BigDecimal gapAmt = feeSum.compareTo(incomAll) == 1 ? feeSum.subtract(incomAll) : BigDecimal.ZERO;
						
			//保險年齡累加
			cashFlowGmap.put("AGE", tempNowAge + i);//累加一(第一筆也要加一)
			//生活費用
			cashFlowGmap.put("LIFE_FEE", lifeFee);
			//房貸費用
			cashFlowGmap.put("HOUSE_FEE", houseFee);
			//教育費用
			cashFlowGmap.put("EDUCATION_FEE", educationFee);
			//其他費用
			cashFlowGmap.put("OTHER_FEE", otherFee);
			//遺產稅準備
			cashFlowGmap.put("TAX_IN", taxIn);
			//應備費用合計
			cashFlowGmap.put("FEE_SUM" , feeSum);
			//配偶收入
			cashFlowGmap.put("COU_INCOME", couIncome);
			//房租收入
			cashFlowGmap.put("FIN_INCOME", finIncome);
			//現有資產
			cashFlowGmap.put("ALL_ASSET", allAsset);
			//壽險保障
			cashFlowGmap.put("INS_AMT" , insAmt);
			//已備費用合計
			cashFlowGmap.put("INCOME_ALL" , incomAll);
			//不足費用
			cashFlowGmap.put("GAP_AMT" , gapAmt);
			
			
			calFamilyGapOutputVO.getLstCashFlow().add(cashFlowGmap);
			
			//抓出最大的不除費用
			maxGap = gapAmt.compareTo(maxGap) == 1 ? gapAmt : maxGap;
			
			i++;
		}
		
		//客戶ID
		calFamilyGapOutputVO.setInsCustId(inputVo.getInsCustID());
		//客戶姓名
		calFamilyGapOutputVO.setInsCustName(tempCustName);
		//庭財務安全區口
		calFamilyGapOutputVO.setInsFamilyGap(maxGap);
		//家庭費用缺口
		calFamilyGapOutputVO.setInsFamilyFee(firstYearLifeFee);
		//將第一年的房貸費用 加上 第一年的其他費用
		calFamilyGapOutputVO.setInsFamilyDebt(firstYearHourceLifeFee.add(firstYearOtherAmt));

		return calFamilyGapOutputVO;
	}
	
	public static void main(String...sa){
		System.out.println(BigDecimal.valueOf(9999123).divide(BigDecimal.valueOf(3331) , 0 , BigDecimal.ROUND_DOWN).intValue());
		
	}
	
	public Map<Integer , BigDecimal> wholeLifeMaxAmtForEveryYear(String custID , List<Map<String, Object>> lstWholeLifes){
		Map<Integer , BigDecimal> result = new HashMap();
		
		if(lstWholeLifes!= null) {
			for(Map<String, Object> lstWholeLife: lstWholeLifes){
				GenericMap lstWholeLifeGmap = new GenericMap(lstWholeLife);
				double maxNumber = 0;
				
				//要為同一個客戶Id的才會塞入Map
				if(!custID.equals(lstWholeLifeGmap.getNotNullStr("INSURED_ID"))){
					continue;
				}
				
				//取該客戶的life(壽險保障)、pa(意外保障)、cl(癌症保障)、ai(特定意外保障)、ddb(重大疾病保障)中最大的金額
				for(String key : new String[]{"LIFE" , "PA" , "CL" , "AI" , "DDB"}){
					maxNumber = Math.max(maxNumber , lstWholeLifeGmap.getBigDecimal(key).doubleValue());
				}
				
				result.put(lstWholeLifeGmap.getBigDecimal("ROCYEAR").intValue() , new BigDecimal(maxNumber));
			}
		}
		return result;
	}
	
	public BigDecimal sum(BigDecimal...nums){
		BigDecimal sum = BigDecimal.ZERO;
		
		for(BigDecimal num : nums){
			if(num != null){
				sum = sum.add(num);
			}
		}
		
		return sum;
	}
	
	
	/**將資料整理成Map，並計算逐年尚須繳納多少費用*/ 
	public GenericMap doGetlstEduDetailReGenericMap(List<GenericMap> lstEduDetailList){
		Map<Integer , BigDecimal> lstEduDetailMap = new HashMap<Integer , BigDecimal>();
		Map<Integer , BigDecimal> relstEduDetailMap = new HashMap<Integer , BigDecimal>();
		int maxRocYear = 0;
		List<Integer> years = new ArrayList<Integer>();
		
		if(CollectionUtils.isNotEmpty(lstEduDetailList)){
			//依照rocyear排序
			Collections.sort(lstEduDetailList , new Comparator<GenericMap>(){
				public int compare(GenericMap o1 , GenericMap o2) {
					BigDecimal rocYear = o1.getBigDecimal("ROCYEAR");
					BigDecimal rocYear2 = o2.getBigDecimal("ROCYEAR");
					return rocYear.compareTo(rocYear2);
				}
			});
			
			for(GenericMap lstEduDetail : lstEduDetailList){
				int rocYear = lstEduDetail.getBigDecimal("ROCYEAR").intValue();
				double reqAmt = lstEduDetail.getBigDecimal("REQ_AMT").doubleValue();
				
				//四捨五入取整入後做累加
				if(reqAmt > 0){
					BigDecimal sumEducationFee = BigDecimal.ZERO;
					
					//該年尚未處理過
					if(lstEduDetailMap.get(rocYear) == null){
						years.add(rocYear); 
					} 
					//該年有處理過
					else{
						sumEducationFee = lstEduDetailMap.get(rocYear);
					}
					
					//key為年，value為該年累積金額，最後一次的rocYear為最大年
					lstEduDetailMap.put(maxRocYear = rocYear , 
						sumEducationFee.add(new BigDecimal(reqAmt))
					);
				}
			}
			
			//計算個個年度到教育結束時還需要繳多少錢
			for(int i = 0 ; i < years.size() ; i++){
				Integer thisYear = years.get(i);
				BigDecimal thisReqAmt = BigDecimal.ZERO;
				
				for(int j = i ; j < years.size() ; j++){
					Integer rocYear = years.get(j);
					thisReqAmt = thisReqAmt.add(lstEduDetailMap.get(rocYear));
				}
				
				relstEduDetailMap.put(thisYear , thisReqAmt.setScale(0 , BigDecimal.ROUND_HALF_UP));
			}
		}
		
		return new GenericMap()
			//.put("LST_EDU_DETAIL_MAP", lstEduDetailMap)
			.put("RE_LST_EDU_DETAIL_MAP", relstEduDetailMap)
			.put("MAX_ROC_YEAR", maxRocYear);	
	}
	
	/**依照家庭財務資料計算生活費用*/
	public GenericMap doGetLifeFeeMap(int tempStartROCYear , int tempFinishROCYear , int tempEduEndYear , GenericMap lstFamilyData){
		Map<Integer , BigDecimal> tempMap = new HashMap<Integer , BigDecimal>();
		Map<Integer , BigDecimal> lifeFeeMap = new HashMap<Integer , BigDecimal>();
		List<Integer> years = new ArrayList<Integer>();
		
		//生活支出(萬元)
		BigDecimal livingExp = lstFamilyData.getBigDecimal("LIVING_EXP");
		//非必要生活支出(萬元)
		BigDecimal notlivingExp = lstFamilyData.getBigDecimal("NOTLIVING_EXP");
		//子女經濟獨立後生活費用比
		BigDecimal childLivingFeer = lstFamilyData.getBigDecimal("CHILD_LIVING_FEER");
		
		BigDecimal custLivingFee = lstFamilyData.getBigDecimal("CUST_LIVING_FEE");
		
		//本人生活費用比//BigDecimal livingFeeratio = lstFamilyData.getBigDecimal("LIVING_FEERATIO");
		
		BigDecimal firstLifeFee = BigDecimal.ZERO;
		
		BigDecimal tempAmt = livingExp.add(notlivingExp).subtract(custLivingFee).multiply(BigDecimal.valueOf(12));
		
		//從當年到配偶預期壽命逐項計算
		for(int rocYear = tempStartROCYear ; rocYear <= tempFinishROCYear ; rocYear++){
			//民國年 /生活費用 = 生活支出 + 非必要生活支出 - custLivingFee
			tempMap.put(rocYear , rocYear < tempEduEndYear ? tempAmt : tempAmt.subtract(childLivingFeer));
			years.add(rocYear);
		}
		
		//計算個個年度到教育結束時還需要繳多少錢
		for(int i = 0 ; i < years.size() ; i++){
			Integer thisYear = years.get(i);
			BigDecimal thisLifeFee = BigDecimal.ZERO;
			
			for(int j = i ; j < years.size() ; j++){
				Integer rocYear = years.get(j);
				thisLifeFee = thisLifeFee.add(tempMap.get(rocYear));
			}
			
			if(i == 0){
				firstLifeFee = thisLifeFee;
			}
			
			lifeFeeMap.put(thisYear , thisLifeFee);
		}
		
		return new GenericMap()
			.put("LIFE_FEE_MAP", lifeFeeMap)
			.put("FIRST_YEAR_LIFE_FEE", firstLifeFee);
	}
	
	
	/**依照家庭財務資料計算房貸費用*/
	public GenericMap doGetHouseDebtMap(int tempStartROCYear , GenericMap lstFamilyData){
		//每月房貸攤還金額(萬元)
		BigDecimal houDebtAmt = lstFamilyData.getBigDecimal("HOU_DEBT_AMT");
		//還需幾年繳清房貸
		BigDecimal houDebtYear = lstFamilyData.getBigDecimal("HOU_DEBT_Y");
		
		Map<Integer , BigDecimal> houseDebtMap = new HashMap<Integer , BigDecimal>();
		Map<Integer , BigDecimal> reHouDebtMap = new HashMap<Integer , BigDecimal>();
		List<Integer> years = new ArrayList<Integer>();
		int yearNum = 1;
		
		BigDecimal firstYearLifeFee = BigDecimal.ZERO;
		
		int tempEndROCYear = tempStartROCYear + houDebtYear.intValue();
		for(int rocYear = tempStartROCYear ; rocYear <= tempEndROCYear ; rocYear++){
			if(yearNum > houDebtYear.intValue()){
				break;
			}
			
			years.add(rocYear);
			houseDebtMap.put(rocYear, houDebtAmt.multiply(BigDecimal.valueOf(12)));
			yearNum++;
		}
		
		//計算個個年度到教育結束時還需要繳多少錢
		for(int i = 0 ; i < years.size() ; i++){
			Integer thisYear = years.get(i);
			BigDecimal thisLifeFee = BigDecimal.ZERO;
			
			for(int j = i ; j < years.size() ; j++){
				Integer rocYear = years.get(j);
				thisLifeFee = thisLifeFee.add(houseDebtMap.get(rocYear));
			}
			
			if(i == 0){
				firstYearLifeFee = thisLifeFee;
			}
			
			reHouDebtMap.put(thisYear , thisLifeFee);
		}
		
		
		return new GenericMap()
			.put("HOURSE_DEBT_MAP", reHouDebtMap)
			.put("FIRST_YEAR_HOURSE_LIFE_FEE", firstYearLifeFee);
	}
	
	
	/**依照家庭財務資料計算其他費用(車貸/卡債/其他融資)*/
	public GenericMap doGetOtherDebtMap(int tempStartROCYear , GenericMap lstFamilyData){
		//車貸(萬元)
		BigDecimal carDebtAmt = lstFamilyData.getBigDecimal("CAR_DEBT_AMT");
		//車貸年數
		BigDecimal carDebtYear = lstFamilyData.getBigDecimal("CAR_DEBT_Y");
		//信貸(萬元)
		BigDecimal cardDebtAmt = lstFamilyData.getBigDecimal("CARD_DEBT_AMT");
		//信貸年數
		BigDecimal cardDebtYear = lstFamilyData.getBigDecimal("CARD_DEBT_Y");
		//其他融資(萬元)
		BigDecimal otherDebtAmt = lstFamilyData.getBigDecimal("OTHER_DEBT_AMT");
		
		Map<Integer , BigDecimal> otherDebtMap = new HashMap<Integer , BigDecimal>();
		Map<Integer , BigDecimal> reOtherDebtMap = new HashMap<Integer , BigDecimal>();
		List<Integer> years = new ArrayList<Integer>();
		
		int endYearNum = (carDebtYear.compareTo(cardDebtYear) != -1 ? carDebtYear : cardDebtYear).intValue();
		BigDecimal firstYearAmt = BigDecimal.ZERO;
		
		int tempEndROCYear = tempStartROCYear + endYearNum;
		for(int rocYear = tempStartROCYear ; rocYear <= tempEndROCYear ; rocYear++){
			BigDecimal sumTempAmt = BigDecimal.ZERO;
			
			//年度小於車貸年數
			if(rocYear < (tempStartROCYear + carDebtYear.intValue())){
				sumTempAmt = sumTempAmt.add(carDebtAmt.multiply(BigDecimal.valueOf(12)));
			}
			
			//年度小於信貸年數
			if(rocYear < (tempStartROCYear + cardDebtYear.intValue())){
				sumTempAmt = sumTempAmt.add(cardDebtAmt.multiply(BigDecimal.valueOf(12)));
			}
			
			otherDebtMap.put(rocYear , sumTempAmt);
			years.add(rocYear);
		}
		
		//計算個個年度到教育結束時還需要繳多少錢
		for(int i = 0 ; i < years.size() ; i++){
			Integer thisYear = years.get(i);
			BigDecimal thisLifeFee = BigDecimal.ZERO;
			BigDecimal thisLifeFeeInt = BigDecimal.ZERO;
			
			for(int j = i ; j < years.size() ; j++){
				Integer rocYear = years.get(j);
				thisLifeFee = thisLifeFee.add(otherDebtMap.get(rocYear));
			}
			
			reOtherDebtMap.put(thisYear , 
				thisLifeFeeInt = thisLifeFee.add(otherDebtAmt).setScale(0 , BigDecimal.ROUND_HALF_UP));
			
			if(i == 0){
				firstYearAmt = thisLifeFeeInt;
			}
		}
		
		return new GenericMap()
			.put("OTHER_DEBT_MAP", reOtherDebtMap)
			.put("FIRST_YEAR_OTHER_AMT", firstYearAmt);
	}
	
	
	/**照家庭財務資料計算配偶收入*/
	public GenericMap doGetCouIncomeMap(int tempStartROCYear , int tempFinishROCYear , int tempCouRetireYear , GenericMap lstFamilyData){
		Map<Integer , BigDecimal> tempMap = new HashMap<Integer , BigDecimal>();
		Map<Integer , BigDecimal> couIncomeMap = new HashMap<Integer , BigDecimal>();
		List<Integer> years = new ArrayList<Integer>();
		
		//配偶年薪(萬元)
		BigDecimal couIncome = lstFamilyData.getBigDecimal("COU_INCOME");
		int minYear = tempFinishROCYear < tempCouRetireYear ? tempFinishROCYear : tempCouRetireYear;
		
		//從當年到配偶預期壽命逐項計算
		for(int rocYear = tempStartROCYear ; rocYear <= minYear ; rocYear++){
			years.add(rocYear);
			//民國年 /生活費用
			tempMap.put(rocYear , couIncome);
		}
		
		//計算個個年度到教育結束時還需要繳多少錢
		for(int i = 0 ; i < years.size() ; i++){
			Integer thisYear = years.get(i);
			BigDecimal thisLifeFee = BigDecimal.ZERO;
			
			for(int j = i ; j < years.size() ; j++){
				Integer rocYear = years.get(j);
				thisLifeFee = thisLifeFee.add(tempMap.get(rocYear));
			}
			
			couIncomeMap.put(thisYear , thisLifeFee.setScale(0 , BigDecimal.ROUND_HALF_UP));
		}
		
		return new GenericMap().put("COU_INCOME_MAP", couIncomeMap);
	}
	
	
	/**照家庭財務資料計算理財收入*/
	public GenericMap doGetRentIncomeMap(int tempStartROCYear , int tempFinishROCYear , GenericMap lstFamilyData){
		Map<Integer , BigDecimal> tempMap = new HashMap<Integer , BigDecimal>();
		Map<Integer , BigDecimal> rentIncomeMap = new HashMap<Integer , BigDecimal>();
		List<Integer> years = new ArrayList<Integer>();
		
		//房租收入(萬元)
		BigDecimal rentIncome = lstFamilyData.getBigDecimal("RENT_INCOME");
		
		//從當年到配偶預期壽命逐項計算
		for(int rocYear = tempStartROCYear ; rocYear <= tempFinishROCYear ; rocYear++){
			years.add(rocYear);
			//民國年 /生活費用
			tempMap.put(rocYear , rentIncome);
		}
		
		//計算個個年度到教育結束時還需要繳多少錢
		for(int i = 0 ; i < years.size() ; i++){
			Integer thisYear = years.get(i);
			BigDecimal thisLifeFee = BigDecimal.ZERO;
			
			for(int j = i ; j < years.size() ; j++){
				Integer rocYear = years.get(j);
				thisLifeFee = thisLifeFee.add(tempMap.get(rocYear));
			}
			
			rentIncomeMap.put(thisYear , thisLifeFee.setScale(0 , BigDecimal.ROUND_HALF_UP));
		}
		
		return new GenericMap().put("RENT_INCOME_MAP", rentIncomeMap);
	}
	
	
	/**取保單健診業務參數 - 預期壽命、退休年齡*/
	private GenericMap initpolicyDiagnosisBusAge() throws JBranchException{
		//保單健診業務參數
		Map<String , String> policyDiagnosisBusParam = InsjlbUtils.getConfigParameter("FPS.INS_AVERAGE" , FormatHelper.FORMAT_3);
		
		//設定保單健診業務參數 - 預期壽命(男、女)、退休年齡
		if(MapUtils.isNotEmpty(policyDiagnosisBusParam)){
			return new GenericMap(policyDiagnosisBusParam);
		}
		else{
			throw new NullPointerException("not found FPS.INS_AVERAGE");
		}
	}
	
	/**依照家庭財務資料計算子女教育費用*/
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public GenericMap doGetLstSppedu(String custId , int tempStartROCYear , Date sysDate) throws JBranchException{
		//子女教育試算資料
		List<Map<String, Object>> lstSppeduList = null;
		List<GenericMap>lstEduDetailList = new ArrayList(); 
		
		GenericMap resultGenMap = new GenericMap();
		
		//取得子女教育試算資料
		if(CollectionUtils.isEmpty(lstSppeduList = getInsjlbDao().queryLstSppedu(custId))){
//			return resultGenMap;
			return resultGenMap.putAll(doGetlstEduDetailReGenericMap(lstEduDetailList));
		}
		
		//逐筆跑子女教育試算資料
		for(Map<String , Object> lstSppeduMap : lstSppeduList){
			GenericMap lstSppeduGenMap = new GenericMap(lstSppeduMap);
			
			//上一次更新的日期
			Date lastupDate = lstSppeduGenMap.getDate("LASTUPDATE");
			//子女生日
			Date childBirthdate = lstSppeduGenMap.getDate("CHILD_BIRTHDATE");
			//子女學齡結束年紀
			BigDecimal endEduAge = lstSppeduGenMap.getBigDecimal("END_EDU_AGE");
			//教育需求金額
			BigDecimal eduAmt = lstSppeduGenMap.getBigDecimal("EDU_AMT").divide(BigDecimal.valueOf(10000));
			//系統年分 - 子女出生年分的年數
			BigDecimal startEduAge = BigDecimal.valueOf(InsjlbUtils.doGetEffectedDateRealAge(sysDate, childBirthdate));
			//上一次登打的年分 - 子女出生年分的年數
			BigDecimal dataInitAge = BigDecimal.valueOf(InsjlbUtils.doGetEffectedDateRealAge(lastupDate, childBirthdate));
			//教育所需幾年
			int years = 0;
			
			lstSppeduMap.put("START_EDU_AGE", startEduAge);
			lstSppeduMap.put("DATA_INIT_AGE", dataInitAge);
			
			BigDecimal avgEduAmt = BigDecimal.ZERO;
			
			//如果已經超過教育年齡
			if(endEduAge.doubleValue() < startEduAge.doubleValue()){
				lstSppeduMap.put("AVG_EDU_AMT", avgEduAmt = BigDecimal.ZERO);
			}
			else{
				//EDU_AMT/( END_EDU_AGE- DATA_INIT_AGE) (四捨五入至小數二位)
				BigDecimal gapDateCnt = endEduAge.subtract(dataInitAge);
				lstSppeduMap.put("AVG_EDU_AMT", avgEduAmt = eduAmt.divide(gapDateCnt , 2 , BigDecimal.ROUND_HALF_UP));
				lstSppeduMap.put("YEARS" , years = endEduAge.subtract(startEduAge).intValue());
				
				//孩子的年紀
				int childrenAge = startEduAge.add(BigDecimal.valueOf(1)).intValue();
				int j = 0;
				
				//孩子的年齡到教育年齡結束
				for(; childrenAge <= endEduAge.intValue() ; childrenAge++){
					lstEduDetailList.add(new GenericMap()
						.put("CHILD_NAME" , lstSppeduMap.get("CHILD_NAME"))
						.put("ROCYEAR", tempStartROCYear + j)
						.put("CHILDREN_AGE", childrenAge)
						.put("REQ_AMT", avgEduAmt)
					);
					j++;
				}
			}
		}
		
		//逐年尚須繳納多少費用
		return resultGenMap.putAll(doGetlstEduDetailReGenericMap(lstEduDetailList));
	}
	
	/**初始化配偶資料*/
	public GenericMap initCouData(
		CalFamilyGapInputVO inputVo , 
		BigDecimal manLifeAge , 
		BigDecimal womanLifeAge , 
		BigDecimal retiredAge , 
		GenericMap lstFamilyData 
	) throws JBranchException{
		//家庭成員與財務安全資訊是否無資料
		boolean isSafetyInfEmpty = false;
		GenericMap couMap = new GenericMap();
		
		//配偶姓名
		couMap.put("PARTNER_NAME" , lstFamilyData.get("PARTNER_NAME"));
		
		//取得配偶生日並判斷是否為空
		couMap.put("PARTNER_BIRTH_DATE" , lstFamilyData.get("PARTNER_BIRTH_DATE"));
		isSafetyInfEmpty = isSafetyInfEmpty || couMap.get("PARTNER_BIRTH_DATE") == null;
		
		//取得配偶性別並判斷配是否為空
		couMap.put("PARTNER_GENDER" , lstFamilyData.get("PARTNER_GENDER"));
		isSafetyInfEmpty = isSafetyInfEmpty || couMap.get("PARTNER_GENDER") == null;
		
		//若符合上述檢核情況時，因家庭成員資料有缺漏，因此無法試算
		if(isSafetyInfEmpty){
			throw new JBranchException("無家庭成員資料，無法試算");
		}
		
		//生日轉roc year
		int tempCustBirthdayRocYear = doGetYear(couMap.getDate("PARTNER_BIRTH_DATE")) - 1911;
		
		//生日的ROCYEAR + 預期壽命
		couMap.put("FINISH_ROC_YEAR" , tempCustBirthdayRocYear + (
			"1".equals(couMap.getNotNullStr("PARTNER_GENDER")) ? manLifeAge.intValue() : 
			"2".equals(couMap.getNotNullStr("PARTNER_GENDER")) ? womanLifeAge.intValue() : 0
		));

		//生日的ROCYEAR + 退休年齡
		couMap.put("RETIRE_YEAR" , tempCustBirthdayRocYear + retiredAge.intValue());
		
		return couMap;
	}
	
	/**取得保障缺口對應給付中間檔的SORTNO來初始化CalFamilyGapOutputVO的各個金額*/
	@SuppressWarnings("unchecked")
	public CalFamilyGapOutputVO builderOutputVOForSecurityAmount(CalFamilyGapInputVO inputVo) throws JBranchException {
		//保障缺口對應SORTNO並轉換為Mapping用Ｍap
		GenericMap securityGapSortNoMap = reSecurityGapSortNoToMap(getInsjlbDao().querySecurityGapSortNo());
		//給付中間檔
		List<Map> lstExpressionList = inputVo.getLstExpression();
		//客戶代碼
		String custId = ObjectUtils.toString(inputVo.getInsCustID());
		
		//outputVo
		CalFamilyGapOutputVO calFamilyGapOutputVO = new CalFamilyGapOutputVO();
		calFamilyGapOutputVO.setOldItemLife(BigDecimal.ZERO);
		calFamilyGapOutputVO.setOldItemAccident(BigDecimal.ZERO);
		calFamilyGapOutputVO.setOldItemDread(BigDecimal.ZERO);
		calFamilyGapOutputVO.setOldItemHealth(BigDecimal.ZERO);
	
		//以給付中間檔取得各個保障金額
		if(lstExpressionList != null) {
			for(Map lstExpression : lstExpressionList) {
				GenericMap lstExpressionGenMap = new GenericMap(lstExpression);
				BigDecimal begunitPrice = lstExpressionGenMap.getBigDecimal("BEGUNITPRICE");
				
				String insCompay = ObjectUtils.toString(lstExpression.get("INSCOMPANY"));
				String insuredId = ObjectUtils.toString(lstExpression.get("INSURED_ID"));
				String sortNo = ObjectUtils.toString(lstExpression.get("SORTNO"));
				String type = securityGapSortNoMap.getNotNullStr(sortNo);
				
				//非合計的跳掉
				if(!InsjlbParamInf.SUM_DESC.equals(insCompay) || !custId.equals(insuredId)){
					continue;
				}
				
				//既有壽險保障金額(元)
				if("L".equals(type)){
					calFamilyGapOutputVO.setOldItemLife(begunitPrice);
				}
				//既有意外保障金額(元)
				else if("P".equals(type)){
					calFamilyGapOutputVO.setOldItemAccident(begunitPrice);
				}
				//既有重大疾病保障金額(元)
				else if("D".equals(type)){
					calFamilyGapOutputVO.setOldItemDread(begunitPrice);						
				}
				//既有住院日額保障金額(元)
				else if("HD".equals(type)){
					calFamilyGapOutputVO.setOldItemHealth(begunitPrice);
				}
			}
		}
		return calFamilyGapOutputVO;
	}
	
	
	/**保障缺口對應SORTNO並轉換為Mapping用Ｍap*/
	public GenericMap reSecurityGapSortNoToMap(List<Map<String , Object>> securityGapSortNo) throws JBranchException{
		GenericMap resultMap = new GenericMap();
		
		if(CollectionUtils.isEmpty(securityGapSortNo)){
			throw new JBranchException("OLDITEM_SORTNO_MAP is not found");
		}
		
		for(Map<String , Object> securityGapSortNoMap : securityGapSortNo){
			String sortNo = ObjectUtils.toString(securityGapSortNoMap.get("PARAM_NAME"));
			String type = ObjectUtils.toString(securityGapSortNoMap.get("PARAM_CODE"));
			resultMap.put(sortNo , type);
		}
		
		
		return resultMap;
	}
	
	/**當inputVo沒有WholeLife(保障領回中間檔)，就向取資訊源資訊(多張保單)*/
	private CalFamilyGapInputVO callPolicySourceInWholeLifeIsEmpty(CalFamilyGapInputVO inputVo) throws JBranchException{
		if(CollectionUtils.isNotEmpty(inputVo.getLstWholeLife())){
			return inputVo;
		}
		
		DoGetCoverage03InputVO doGetCoverage03InputVo = new DoGetCoverage03InputVO();
		DoGetCoverage03OutputVO doGetCoverage03OutputVO = null;
		List lstData = null;
		
		//設定查詢保單所需要的資料
		INS810InputVO ins810Inputvo = new INS810InputVO();
		ins810Inputvo.setCUST_ID(inputVo.getInsCustID());
		ins810Inputvo.setLoginAOCode(getLoginAoCode());
		ins810Inputvo.setLoginBranch(getLoginBranch());
		lstData = getIns810().queryInOutBuyMutiPolicy(ins810Inputvo);
		
		// 當沒有保單資料時什麼都不要做
		if(CollectionUtils.isEmpty(lstData)) return inputVo;
		
		//設定資訊源所需要的資料
		// 客戶ID
		doGetCoverage03InputVo.setInsCustID(inputVo.getInsCustID());		
		// 呼叫類型(1: 要保人'得到全家',2: 被保險人)
		doGetCoverage03InputVo.setCallType("1");						
		// 是否包含非家庭戶
		doGetCoverage03InputVo.setIncludeAll("N");
		// 客戶保單資訊
		doGetCoverage03InputVo.setLstInsData(lstData);				
		// 是否存檔(Y: 存檔,N: 不存檔,空白/null：亦不存檔
		doGetCoverage03InputVo.setDoSave("Y");
		
		//取資訊源資訊
		doGetCoverage03OutputVO = getGetCoverage03Service().doGetCoverage03(doGetCoverage03InputVo);
		
		//檢核判斷，若有錯誤則跳回頁面顯示錯誤信息
		if (CollectionUtils.isNotEmpty(doGetCoverage03OutputVO.getLstLogTable())) {
			List errorMsg = doGetCoverage03OutputVO.getLstLogTable();
			throw new JBranchException(errorMsg.toString());
		}
		else if(CollectionUtils.isEmpty(doGetCoverage03OutputVO.getLstWholeLife())){
			throw new JBranchException("LstWholeLife is empty");	
		}
		else if(CollectionUtils.isEmpty(doGetCoverage03OutputVO.getLstExpression())){
			throw new JBranchException("LstExpression is empty");	
		}
		
		inputVo.setLstWholeLife(doGetCoverage03OutputVO.getLstWholeLife());
		inputVo.setLstExpression(doGetCoverage03OutputVO.getLstExpression());
		
		return inputVo;
	}
	
	
	private int doGetYear(Date date){
		Calendar tempCustBirthdayCal = Calendar.getInstance();
		tempCustBirthdayCal.setTime(date);
		return tempCustBirthdayCal.get(Calendar.YEAR);
	}

	//getter & setter
	public GetCoverage03ServiceInf getGetCoverage03Service() {
		return getCoverage03Service;
	}

	public void setGetCoverage03Service(GetCoverage03ServiceInf getCoverage03Service) {
		this.getCoverage03Service = getCoverage03Service;
	}

	public FubonInsjlbDaoInf getInsjlbDao() {
		return insjlbDao;
	}

	public void setInsjlbDao(FubonInsjlbDaoInf insjlbDao) {
		this.insjlbDao = insjlbDao;
	}

	public INS810 getIns810() {
		return ins810;
	}

	public void setIns810(INS810 ins810) {
		this.ins810 = ins810;
	}
}
