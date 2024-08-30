package com.systex.jbranch.app.server.fps.insjlb.service;

import static com.systex.jbranch.app.server.fps.insjlb.LstInsDataParamInf.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.systex.jbranch.app.server.fps.ins810.INS810InputVO;
import com.systex.jbranch.app.server.fps.insjlb.InsjlbParamInf;
import com.systex.jbranch.app.server.fps.insjlb.InsjlbUtils;
import com.systex.jbranch.app.server.fps.insjlb.vo.DoGetCoverage01OutputVO;
import com.systex.jbranch.app.server.fps.insjlb.vo.GetOdItemListInputVO;
import com.systex.jbranch.app.server.fps.insjlb.vo.GetOldItemListOutputVO;
import com.systex.jbranch.comutil.collection.CollectionSearchUtils;
import com.systex.jbranch.comutil.collection.GenericMap;
import com.systex.jbranch.platform.common.errHandle.JBranchException;

@Service("GetOldItemListService")
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
@SuppressWarnings({ "unchecked", "rawtypes" , "unused"})
/**取得既有保障明細**/
public class GetOldItemListService extends GetInsService implements GetOldItemListServiceInf {
	@Autowired @Qualifier("GetCoverage01Service")
	private GetCoverage01ServiceInf getCoverage01Service;
	
	@Resource(name = "GetOldItemListService.rePattern1")
	private Map<String , String> rePattern;
	
	/**取得既有保障明細*/
	public GetOldItemListOutputVO getOldItemList(GetOdItemListInputVO inputVo)throws JBranchException{
		//保單錯誤訊息
		List lstLogTableErMsg = null;
		//給附中間檔
		List<Map> lstExpressionList = null;
		//result
		GetOldItemListOutputVO outputVo = new GetOldItemListOutputVO();
		
		//基本檢核
		GetInsValiateUtils.validate(inputVo);
		
		//呼叫INS810取得行內外表單的資料並轉成LstInsDetail格式
		searchInOutPolicyData(inputVo);
		
		//檢查保單資料LstInsData
		if(CollectionUtils.isNotEmpty(lstLogTableErMsg = GetInsValiateUtils.chkLstInsData(inputVo.getLstInsData()))){
			outputVo.setLstLogTable(lstLogTableErMsg);
			return outputVo;
		}
		
		//呼叫保單資訊源，並產生既有保障明細資料
		outputVo.setOldItemLlist(callGetCoverage01ToGenOldItem(inputVo));
		
		return outputVo;
	}
	
	/**開始組合WEBSERVICE的INPUT資料(每一筆放入後立即call doGetCoverage01)**/
	public List<Map> callGetCoverage01ToGenOldItem(GetOdItemListInputVO inputVo) throws JBranchException{
		//主約資料
		List<Map<String, Object>> lstInsDataMainList = null;
		
		//保險規劃缺口類型
		String planTypes = inputVo.getPlanTypes();
		
		//既有保障缺口對應的給附編號的parameter
		List<Map<String , Object>> sortNoList = null;
		
		//result
		List<Map> oldItemLlist = new ArrayList();
		
		//篩選出主約資料(IS_MAIN = Y為主約)
		lstInsDataMainList = CollectionSearchUtils.selectMapInListByString(inputVo.getLstInsData() , IS_MAIN , "Y");
		lstInsDataMainList = lstInsDataMainList == null ? new ArrayList() : lstInsDataMainList;
		
		//planTypes若帶有H，另外加上HD以及HR
		planTypes = planTypes + (planTypes.matches(".*H.*") ? ",HD,HR" : "");
		
		// 取得職等 為空預設帶 1
		String jobGrade = inputVo.getJobGrade() == null ? "1" : inputVo.getJobGrade();
		
		//查詢既有保障缺口對應的給附編號
		sortNoList = getInsjlbDao().queryOlditemSortnoMap(planTypes.split(","));
		
		//逐筆呼叫保險資訊源doGetCoverage01：健檢攤列的各項資料(單筆)
		for(Map<String , Object> lstInsData : inputVo.getLstInsData()){
			if(!inputVo.getCustId().equals(lstInsData.get("INSURED_ID"))){
				continue;
			}
			
			Map lstInsDetail = null;
			//資訊源回傳資料
			DoGetCoverage01OutputVO doGetCoverage01OutputVO = null;
			//給附說明中間檔
			List<Map> lstExpressionList = null;
			//保障中間檔
			List<Map> lstCoverageTableList = null;
			//保障保費中間檔
			List<Map> lstCoverAgePrem = null;
			//保障清單
			List lstCoverageList = null;
			//相同商品id的保障中間檔
			GenericMap sameCovTable = null;
			//result map
			Map oldItem = new HashMap();
			//單位數或計畫數
			String upqtySel = ObjectUtils.toString(lstInsData.get(UPQTY_SEL));
			//原幣保險金額(保額)
			String insuredamt = ObjectUtils.toString(lstInsData.get(INSUREDAMT));
			
			//從lstInsData傳換成lstInsDetail
			lstInsDetail = initLstInsDetail(lstInsData , lstInsDataMainList, jobGrade);
			
			//inputVo.getCustId().equals()
			
			//呼叫保險資訊源，向保險資訊源取得健檢攤列的各項資料(單筆)
			doGetCoverage01OutputVO = getGetCoverage01Service().doGetCoverage01(lstInsDetail);
			
			//若無給付中間檔，跳過
			if(doGetCoverage01OutputVO == null || CollectionUtils.isEmpty(doGetCoverage01OutputVO.getLstExpression())){
				continue;
			}
			
			//給付中間檔
			lstExpressionList = doGetCoverage01OutputVO.getLstExpression();
			
			//保障中間檔
			lstCoverageTableList = doGetCoverage01OutputVO.getLstCoverageTable();
			
			//保障保費中間檔
			lstCoverAgePrem = doGetCoverage01OutputVO.getLstCoverAgePrem();
					
			//找出符合的保障清單並組合成lstCoverageList回傳
			if(CollectionUtils.isEmpty(lstCoverageList = searchLstCoverage(lstExpressionList , sortNoList))){
				continue;
			}
			
			//既有保障來源
			oldItem.put(SOURCE_TYPE , lstInsData.get(SOURCE_TYPE));
			//保險公司編號
			oldItem.put(INSCO , lstInsData.get(INSCO));
			//保單號碼
			oldItem.put(POLICYNO , lstInsData.get(POLICYNO));
			//保險資訊源商品代碼
			oldItem.put(PROD_KEYNO , lstInsData.get(PROD_KEYNO));
			//商品代號
			oldItem.put(PROD_ID , lstInsData.get(PRD_ID));
			
			// 幣別 紀錄幣別
			oldItem.put(CURR_CD, lstInsData.get(CURR_CD));
			
			//處理保額
			if(ObjectUtils.toString(lstInsData.get(UPTYPE)).equals("01")){
				oldItem.put(QUANTITY , InsjlbUtils.changeStr(StringUtils.isBlank(upqtySel) ? "0" : upqtySel));
			}
			else if(ObjectUtils.toString(lstInsData.get(UPTYPE)).equals("02")){
				String itemP = ObjectUtils.toString(lstInsData.get(ITEM_P));
				String listP = ObjectUtils.toString(lstInsData.get(LIST_P));
				oldItem.put(QUANTITY , InsjlbUtils.findByItemList(itemP , listP , upqtySel , ","));
			}
			else if(StringUtils.isBlank(ObjectUtils.toString(lstInsData.get(UPTYPE)))){
				oldItem.put(QUANTITY , InsjlbUtils.changeStr(StringUtils.isBlank(insuredamt) ? "0" : insuredamt));
			}
			
			//找出相同商品id
			sameCovTable = new GenericMap(CollectionSearchUtils.findMapInColleciton(
				lstCoverageTableList , ALLPRODUCTSID , lstInsData.get(PROD_KEYNO)
			));
			
			if(sameCovTable.getParamMap() != null){
				//保險公司名稱
				oldItem.put(INSCO_NAME , sameCovTable.get(INSCOMPANY));
				//商品名稱
				oldItem.put(PROD_NAME , sameCovTable.get(PRODNAME));
				//年期
				oldItem.put(PREMTERM , sameCovTable.get(PREM));
				//保額單位
				oldItem.put(COVERUNIT , sameCovTable.get(COVERCACULUNITDESC));
				//年化保費
				
				oldItem.put(PREMIUM , !sameCovTable.getBigDecimal(YEARPREM).equals(BigDecimal.ZERO) ? 
					sameCovTable.get(YEARPREM) : null 
				);
				
				// 直接算出折合匯率後的台幣
				if("TWD".equals(oldItem.get(CURR_CD))) {
					oldItem.put(LOCAL_INSYEARFEE, oldItem.get(PREMIUM));
				} else {
					BigDecimal localInsYearfee = BigDecimal.ZERO;
					localInsYearfee = new BigDecimal(oldItem.get(PREMIUM).toString()).multiply(new BigDecimal(lstInsData.get("RATE").toString()));
					localInsYearfee = localInsYearfee.setScale(0, RoundingMode.HALF_UP);
					oldItem.put(LOCAL_INSYEARFEE, localInsYearfee);
				}
				
			}
			
			//保險年度
			oldItem.put(YEAR , lstInsData.get(EFFECTED_DATE) != null ? 
				InsjlbUtils.doGetEffectedDate((Date) lstInsData.get(EFFECTED_DATE)) :
				null
			);
			
			//繳別
			oldItem.put(PAYTYPE, lstInsData.get(PAYTYPE));
			
			//當年度還本金
			Map<String , Object> rListCoverage = CollectionSearchUtils.findMapInColleciton(
				lstCoverageList , "planTypes" , "R");
			
			if(rListCoverage != null){
				oldItem.put(REPAY , rListCoverage.get("coverage"));
			}

			//保障清單
			oldItem.put(LST_COVERAGE_LOWER , lstCoverageList);
			//逐年保障領回清單
			oldItem.put(LST_COVER_AGE_PREM_LOWER , lstCoverAgePrem);
			
			// 增加當有實支實付時要處理 40000_S 
			oldItem.put("IS_HR", MapUtils.isNotEmpty(CollectionSearchUtils.findMapInColleciton(lstExpressionList, "SORTNO", "40000_S")) ? "Y" : "N");
			
			oldItemLlist.add(oldItem);
		}
		
		return oldItemLlist;
	}
	

	/**轉換成LstInsData*/
	public Map initLstInsDetail(Map<String , Object> lstInsData , List<Map<String, Object>> lstInsDataMainList, String jobGrade) throws JBranchException{
		Map lstInsDetail = new HashMap();
		
		//投保對象
		String insuredRelation = "";
		
		GenericMap lstInsDataGmap = new GenericMap(lstInsData);
		boolean isMain = lstInsDataGmap.equals(IS_MAIN , "Y");//是否為主約
		String paymentyearSel = null;//主約繳費年期
		String coveryearSel = null;//主約累積年期
				
		lstInsDetail.put(ALLPRODUCTSID , lstInsDataGmap.get(PROD_KEYNO));//商品ID
		
		/**
		 * 老爹：getOldItemlist的PREMIUM 那一行要回復，要塞 INSYEARFEE 因為那是既有保單的部份
		 * 先打開後續再確認資料正確性 TODO
		 */
		lstInsDetail.put(PREMIUM , lstInsDataGmap.get(INSYEARFEE));//單項總保費(原幣)  
		lstInsDetail.put(KIND , lstInsDataGmap.isStringNotBank(KIND) ? lstInsDataGmap.get(KIND_SEL) : "");//屬性歸類(中文)
		lstInsDetail.put(SOCIALSECURITY , "");//有無社保
		lstInsDetail.put(INSURED_AGE , lstInsDataGmap.get(INSURED_AGE));//投保年齡
		lstInsDetail.put(INSURED_GENDER , lstInsDataGmap.get(INSURED_GENDER));//性別
		lstInsDetail.put(PAYTYPE , lstInsDataGmap.get(PAYTYPE));//繳法
		
		//投保對象
		if(lstInsDataGmap.isStringBank(INSURED_OBJECT) || lstInsDataGmap.isStringBank(LIST_O)){
			lstInsDetail.put(INSURED_OBJECT , insuredRelation = "0");//投保對象			
		}
		else {
			insuredRelation = InsjlbUtils.getParameterVal3(
				InsjlbParamInf.INSURED_OBJECT_MAP, lstInsDataGmap.getNotNullStr(INSURED_OBJECT)
			);
			
			lstInsDetail.put(INSURED_OBJECT , insuredRelation);//投保對象
		}
		
		//保險對象
		lstInsDetail.put(IOBJECT , insuredRelation.matches("[0,1,2]") ? insuredRelation : "0");//保險對象

		
//		lstInsDetail.put(INSURED_OBJECT , lstInsDataGmap.get(INSURED_OBJECT));//繳法
//		lstInsDetail.put(IOBJECT , "0");//保險對象

		lstInsDetail.put(JOB_GRADE , jobGrade);//職等級
		lstInsDetail.put(IDAYS , "0");//旅行平安險購買天數
		lstInsDetail.put(ICOUNT , "");
		lstInsDetail.put(FIELDG , "");//年金給付方式
		lstInsDetail.put(FIELDX , "");//年金每次金額
		lstInsDetail.put(POLICYDESC , "");//保單保全資料
		lstInsDetail.put(IS_WHOLE_LIFE , lstInsDataGmap.get(IS_WL));//是否終身
		lstInsDetail.put(EFFECTED_DATE , lstInsDataGmap.get(EFFECTED_DATE));//保單生效日
		lstInsDetail.put(CURR_RATE , lstInsDataGmap.get("RATE"));
		lstInsDetail.put(CURR_CD , lstInsDataGmap.get("CURR_CD"));
		//主約
		if(isMain){
			paymentyearSel = lstInsDataGmap.getNotNullStr(PAYMENTYEAR_SEL);
			coveryearSel = lstInsDataGmap.getNotNullStr(COVERYEAR_SEL);				
		}
		//附約
		else{
			//找出此附約所對應的主約資料，並抓出繳費年期、累積年期
			Map lstInsDataMain = CollectionSearchUtils.findMapByKey(lstInsDataMainList , lstInsData , INSSEQ);
			
			//如果有找到同保單號主約就抓主約的繳費年其、累積年期
			if(lstInsDataMain != null){
				paymentyearSel = ObjectUtils.toString(lstInsDataMain.get(PAYMENTYEAR_SEL));
				coveryearSel = ObjectUtils.toString(lstInsDataMain.get(COVERYEAR_SEL));
			}
		}
		
		//繳費年期代碼不是空的，抓自已這張的繳費年期
		if(lstInsDataGmap.isStringNotBank(ITEM_Y)){
			lstInsDetail.put(PREMTERM , lstInsDataGmap.getNotNullStr(PAYMENTYEAR_SEL));
		}
		//主約繳費年期不是空的
		else if(StringUtils.isNotBlank(paymentyearSel)){
			lstInsDetail.put(PREMTERM , paymentyearSel);	
		}
		else{
			lstInsDetail.put(PREMTERM , "");
		}
		
		//累績年期代碼不是空的，抓自已這張的繳費年期
		if(lstInsDataGmap.isStringNotBank(ITEM_A)){
			lstInsDetail.put(ACCUTERM , lstInsDataGmap.getNotNullStr(COVERYEAR_SEL));
		}
		//主約累積年期不是空的
		else if(StringUtils.isNotBlank(coveryearSel)){
			lstInsDetail.put(ACCUTERM , coveryearSel);	
		}
		else{
			lstInsDetail.put(ACCUTERM , "");
		}
		
		//單位
		lstInsDetail.put(UNIT , 
			lstInsDataGmap.equals(UPTYPE, "01") && lstInsDataGmap.isStringNotBank(ITEM_U) ?
			lstInsDataGmap.getNotNullStr(UPQTY_SEL) : ""
		);
		
		//計畫
		lstInsDetail.put(PLAN , 
			lstInsDataGmap.equals(UPTYPE, "02") && lstInsDataGmap.isStringNotBank(ITEM_P) ?
			lstInsDataGmap.getNotNullStr(UPQTY_SEL) : 
			""
		);
		
		//商品數量或保額(原幣)
		lstInsDetail.put(PRODQUANTITY , 
			lstInsDataGmap.isStringBank(ITEM_U) && lstInsDataGmap.isStringBank(ITEM_P) ?
			lstInsDataGmap.getNotNullStr(INSUREDAMT) : 
			""
		);

		return lstInsDetail;
	}
	
	/**找出保障清單**/
	public List searchLstCoverage(List<Map> lstExpressionList , List<Map<String , Object>> sortNoList){
		List lstCoverageList = new ArrayList();
		
		BigDecimal coverageSum = BigDecimal.ZERO;
		
		//彙整保障清單
		for(Map<String , Object> lstExpression : lstExpressionList){
			Map sortNo = CollectionSearchUtils.findMapInColleciton(sortNoList, "PARAM_NAME", lstExpression.get("SORTNO"));
			GenericMap lstExpressionGmap = new GenericMap(lstExpression);
			
			if(sortNo != null){
				Map lstCoverageMap = new HashMap();
				coverageSum = coverageSum.add(lstExpressionGmap.getBigDecimal("BEGUNITPRICE"));
				
				lstCoverageMap.put("planTypes", sortNo.get("PARAM_CODE"));//保險規劃缺口類型
				lstCoverageMap.put("coverage", lstExpression.get("BEGUNITPRICE"));//coverage
				lstCoverageList.add(lstCoverageMap);
			}
		}
		
		return coverageSum.doubleValue() > 0 ? lstCoverageList : new ArrayList();
	}
	
	/**取行內保單*/
	public void searchInOutPolicyData(GetOdItemListInputVO inputVo) throws JBranchException{
		INS810InputVO ins810inputvo = new INS810InputVO();
		ins810inputvo.setCUST_ID(inputVo.getCustId());
		ins810inputvo.setLoginAOCode(inputVo.getLoginAOCode());
		ins810inputvo.setLoginBranch(inputVo.getLoginBranch());
		
		//查詢行內外保單資料
		Map<String , List<Map<String , Object>>> inOutBuyMap = inOutBuyToLstInsData(ins810inputvo);
		inputVo.setLstInsData(new ArrayList());
		
		//行外保單標記為02
		if(CollectionUtils.isNotEmpty(inOutBuyMap.get("OUT_BUY"))){
			inputVo.getLstInsData().addAll(CollectionSearchUtils.addEntry(inOutBuyMap.get("OUT_BUY"), "SOURCE_TYPE" , "02"));
		}
		
		//行內保單標記為01
		if(CollectionUtils.isNotEmpty(inOutBuyMap.get("IN_BUY"))){
			inputVo.getLstInsData().addAll(CollectionSearchUtils.addEntry(inOutBuyMap.get("IN_BUY"), "SOURCE_TYPE" , "01"));
		}
		
		try{
			//轉換map格式為INSJLB所需的格式
			inputVo.setLstInsData(InsjlbUtils.reMapKey(inputVo.getLstInsData() , rePattern));
		}
		catch(Exception ex){
			ex.printStackTrace();
			throw new JBranchException(ex);
		}
	}
	
	public GetCoverage01ServiceInf getGetCoverage01Service() {
		return getCoverage01Service;
	}

	public void setGetCoverage01Service(GetCoverage01ServiceInf getCoverage01Service) {
		this.getCoverage01Service = getCoverage01Service;
	}

	public Map<String, String> getRePattern() {
		return rePattern;
	}

	public void setRePattern(Map<String, String> rePattern) {
		this.rePattern = rePattern;
	}
}
