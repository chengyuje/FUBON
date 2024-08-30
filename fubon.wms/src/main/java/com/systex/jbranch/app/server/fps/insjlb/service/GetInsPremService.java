package com.systex.jbranch.app.server.fps.insjlb.service;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.systex.jbranch.app.server.fps.insjlb.InsjlbUtils;
import com.systex.jbranch.app.server.fps.insjlb.dao.InsjlbDaoInf;
import com.systex.jbranch.app.server.fps.insjlb.vo.DoGetCoverage01OutputVO;
import com.systex.jbranch.app.server.fps.insjlb.vo.GetInsPremInputVO;
import com.systex.jbranch.app.server.fps.insjlb.vo.GetInsPremOutputVO;
import com.systex.jbranch.comutil.collection.GenericMap;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;

@Service("GetInsPremService")
public class GetInsPremService extends GetCoverageService implements GetInsPremServiceInf{
	@Autowired @Qualifier("GetCoverage01Service")
	private GetCoverage01ServiceInf getCoverage01Service;
	
	@Autowired @Qualifier("insjlbDao")
	private InsjlbDaoInf insjlbDao;
	
	public GetInsPremOutputVO getInsPrem(GetInsPremInputVO inputVo)throws JBranchException{
		// CHECK參數是否有值
		checkParam(inputVo);
		
		GenericMap lstInsData = new GenericMap((Map<String, Object>) inputVo.getLstInsData().get(0));
		//主附約產品編號
		String prodKeyNo = lstInsData.getNotNullStr("PRD_KEYNO");
		//資訊源商品
		List<Map<String , Object>> productList =  null;
		//保單錯誤訊息
		List lstLogTableErMsg = null;
		//讀取資訊源商品檔，並判斷是否有資料
		if(CollectionUtils.isEmpty(productList = getInsjlbDao().queryFinancialProduct(Arrays.asList(prodKeyNo)))){
			throw new JBranchException("資訊源無此產品代號");
		}
		
		// 重組資料目前單一筆 (lsInsData map部分)
		combinationMap(lstInsData , new GenericMap(productList.get(0)));
		
		//檢查保單資料LstInsData
		if(CollectionUtils.isNotEmpty(lstLogTableErMsg = GetInsValiateUtils.chkLstInsData(inputVo.getLstInsData()))){
			GetInsPremOutputVO outputVO = new GetInsPremOutputVO();
			outputVO.setLstLogTable(lstLogTableErMsg);
			return outputVO;
		}
		
		// 調用資訊源
		return setOutputVO(getCoverage01Service.doGetCoverage01(initLstInsDetail(lstInsData, inputVo)) , lstInsData);
	}
	
	/**
	 * CHECK參數是否有值
	 * @param inputVo
	 * @throws JBranchException
	 */
	private void checkParam(GetInsPremInputVO inputVo) throws JBranchException {
		// IF (INPUT.lstInsData Is null or 空) then 錯誤訊息 ehl_01_common_006 (傳入參數 ‘lstInsData’)
		if(inputVo.getLstInsData() == null || inputVo.getLstInsData().size() == 0) {
			throw new APException("ehl_01_common_006", createErrorMsgList("lstInsData"));
		}
		
		// IF (INPUT.funcType Is null or 空) then 錯誤訊息 ehl_01_common_006 (傳入參數 ‘funcType’)
		if(StringUtils.isEmpty(inputVo.getFuncType())) {
			throw new APException("ehl_01_common_006", createErrorMsgList("funcType"));
		} 
		
		// 上面兩個都沒 throw 表示 lstInsData有資料 && funcType 有值
		Map<String, Object> firstLstInsData = (Map<String, Object>)inputVo.getLstInsData().get(0);
		if("2".equals(inputVo.getFuncType()) && StringUtils.isEmpty(firstLstInsData.get("PRD_KEYNO"))) {
			throw new APException("ehl_01_common_006", createErrorMsgList("PRD_KEYNO"));
		}
	}

	/**
	 * 重組 來源資料Map
	 * @param lstInsData 來源資料
	 * @param productMap 產品資料檔
	 * @throws JBranchException
	 */
	private void combinationMap(GenericMap lstInsData, GenericMap productMap) throws JBranchException {
		GenericMap gInsMap = InsjlbUtils.reDftSettingToMap(productMap.getNotNullStr("DFT_SETTING"));
		
		// 根據CURR_CD取匯率 (只有一筆這樣做，多筆要提出去)
		Map<String , BigDecimal> refExcRateMap = getIns810().queryRefExcRate(); // 參考匯率All
		lstInsData.put("CURR_RATE", refExcRateMap.get(lstInsData.get("CURR_CD"))); // CURR_RATE 匯率
		
		// PAYTYPE	繳別
		if(lstInsData.isStringBank("PAYTYPE")){
			lstInsData.put("PAYTYPE", "1");
		}
		
		//生效日
		if(lstInsData.isNull("EFFECTED_DATE")){
			lstInsData.put("EFFECTED_DATE" , new Date());
		}
		
		//被保人生日
		if(lstInsData.isNull("INSURED_BIRTHDAY")){
			lstInsData.put("INSURED_BIRTHDAY" , new Date());
		}
		
		// TODO 要確認 
		// INSURED_AGE	被保險人投保年齡
		lstInsData.put("INSURED_AGE" , lstInsData.isStringBank("INSURED_AGE") ? 
			InsjlbUtils.calculateInsAge(lstInsData.getDate("EFFECTED_DATE") , lstInsData.getDate("INSURED_BIRTHDAY")) : 
			lstInsData.get("INSURED_AGE")
		); 
		
		lstInsData.put("INSURED_OBJECT", "本人");
  
		//productMap copy to lstInsData
		for(String cloneKey : new String[]{
			"ITEM_Y" , "ITEM_A" , "ITEM_X" , "ITEM_K" , "ITEM_P" , "ITEM_U" , "ITEM_E" , "LIST_O" ,
			"PRD_ID" , "PRD_NAME" , "COM_ID" , "IS_WL" , "WL_TERM" , "IS_OVERSEA" , "QUANTITY_STYLE" , 
			"MENU_TYPE" , "MENU_ITEM" , "MENU_LIST" , "COVERCACULUNIT" , "COVERCACULUNITDESC" 
		}){
			lstInsData.put(cloneKey , productMap.get(cloneKey));
		}
		
		//在lstInsData增加繳費年期PAYMENTYEAR_SEL for ITEM_Y
		doSetItem("PAYMENTYEAR_SEL", "Y", lstInsData , gInsMap, productMap);
		//在lstInsData增加保障年期COVERYEAR_SEL的內容 for ITEM_A
		doSetItem("COVERYEAR_SEL", "A", lstInsData, gInsMap, productMap);
		//在lstInsData增加屬性歸類KIND_SEL的內容 for ITEM_K
		doSetItem("KIND_SEL", "K", lstInsData, gInsMap, productMap);
		
		// COVER_TYPE 保障期間
		lstInsData.put("COVER_TYPE" , lstInsData.isStringBank("COVER_TYPE") ? 
			(productMap.equals("IS_WL" , "Y") ? "01" : "00") : 
			lstInsData.get("COVER_TYPE"));
	}
	
	/**
	 * 設定 ITEM Y A K
	 * @param columnName 來源資料 欄位名稱
	 * @param itemName Y A K 其中一個
	 * @param lstInsData 來源資料
	 * @param gInsmap map_預設值
	 * @param prodMap 產品資料檔
	 */
	private void doSetItem(String columnName, String itemName, GenericMap lstInsData, GenericMap gInsMap, GenericMap prodMap) {
		String resourceMapValue = ObjectUtils.toString(lstInsData.get(columnName));
		String prodMapValue = ObjectUtils.toString(prodMap.get("ITEM_" + itemName));
		String gInsMapValue = ObjectUtils.toString(gInsMap.get(itemName));
		
		if(StringUtils.isEmpty(resourceMapValue) && !StringUtils.isEmpty(prodMapValue)) {
			if(!StringUtils.isEmpty(gInsMapValue) &&  prodMapValue.indexOf(gInsMapValue) != -1) {
				// set= map_預設值(Y / A / K) 
				lstInsData.put(columnName, gInsMapValue);
			} else {  
				// set=A.ITEM_K第一個值
				lstInsData.put(columnName, (prodMapValue.split(","))[0]);
			}
		} else {
			lstInsData.put(columnName, resourceMapValue);
		} 
	}
	
	/**
	 * 組合資訊源要的格式 map
	 * @param lstInsData
	 * @param lstInsDetail
	 */
	@SuppressWarnings("unchecked")
	private Map<String, Object> initLstInsDetail(GenericMap lstInsDataGmap, GetInsPremInputVO inputVo) {
		String mainPayYear = inputVo.getMainPayYear();
		String mainCovYear = inputVo.getMainCovYear();
		Map<String, Object> lstInsDetail = new HashMap();
	
		lstInsDetail.put(ALLPRODUCTSID, lstInsDataGmap.get(PRD_KEYNO)); // ALLPRODUCTSID	商品ID	PRD_KEYNO
		lstInsDetail.put(PREMIUM, lstInsDataGmap.get(INSYEARFEE)); // PREMIUM	單項總保費(原幣)	INSYEARFEE
		lstInsDetail.put(SOCIALSECURITY, ""); // SOCIALSECURITY	有無社保	空白
		lstInsDetail.put(INSURED_AGE, lstInsDataGmap.get(INSURED_AGE)); // INSURED_AGE	投保年齡	INSURED_AGE
		lstInsDetail.put(INSURED_GENDER, lstInsDataGmap.get(INSURED_GENDER)); // INSURED_GENDER	性別	INSURED_GENDER
		lstInsDetail.put(PAYTYPE, lstInsDataGmap.get(PAYTYPE)); // PAYTYPE	繳法	PAYTYPE
		lstInsDetail.put(JOB_GRADE, lstInsDataGmap.get(JOB_GRADE)); // JOB_GRADE	職等級	JOB_GRADE
		lstInsDetail.put(IDAYS, "0"); // IDAYS	旅行平安險購買天數	固定 0
		lstInsDetail.put(ICOUNT, ""); // ICOUNT		空白
		lstInsDetail.put(FIELDG, ""); // FIELDG	年金給付方式	空白
		lstInsDetail.put(FIELDX, ""); // FIELDX	年金每次金額	空白
		lstInsDetail.put(IOBJECT, "0"); // IOBJECT	保險對象	固定’0’
		lstInsDetail.put(POLICYDESC, ""); // POLICYDESC	保單保全資料	空字串
		lstInsDetail.put(IS_WHOLE_LIFE, lstInsDataGmap.get(IS_WL)); // isWholeLife	是否終身	IS_WL(from TBPRD_INSDATA_PROD_MAIN)
		lstInsDetail.put(EFFECTED_DATE, lstInsDataGmap.get(EFFECTED_DATE));
		lstInsDetail.put(KIND, //屬性歸類(中文)
			lstInsDataGmap.isStringNotBank(ITEM_K) ? 
			lstInsDataGmap.get(KIND_SEL) : "");

		lstInsDetail.put(UNIT, // UNIT	單位
			lstInsDataGmap.equals(UPTYPE, "01") && lstInsDataGmap.isStringNotBank(ITEM_U) 
			? lstInsDataGmap.getNotNullStr(UPQTY_SEL) : "");
		
		lstInsDetail.put(PLAN, // PLAN	計畫
			lstInsDataGmap.equals(UPTYPE, "02") && lstInsDataGmap.isStringNotBank(ITEM_P) 
			? lstInsDataGmap.getNotNullStr(UPQTY_SEL) : "");
		
		lstInsDetail.put(PRODQUANTITY, // PRODQUANTITY	商品數量或保額(原幣)
			lstInsDataGmap.isStringBank(ITEM_U) && lstInsDataGmap.isStringBank(ITEM_P) 
			? lstInsDataGmap.getNotNullStr(INSUREDAMT) : "");
		
		if(!lstInsDataGmap.isStringBank(PREMTERM)) {		// PREMTERM 繳費年期
			lstInsDetail.put(PREMTERM, lstInsDataGmap.getNotNullStr(PREMTERM));	
	    } else {
			lstInsDetail.put(PREMTERM, 
				!lstInsDataGmap.isStringBank(ITEM_Y) ? lstInsDataGmap.getNotNullStr(PAYMENTYEAR_SEL) : 
				!StringUtils.isEmpty(mainPayYear) ? mainPayYear: "");
	    }
	    
		lstInsDetail.put(ACCUTERM, // ACCUTERM 累積年期(保障年期)
			!lstInsDataGmap.isStringBank(ITEM_A) ? lstInsDataGmap.getNotNullStr(COVERYEAR_SEL) : 
			!StringUtils.isEmpty(mainCovYear) ? mainCovYear: "");
		
		return lstInsDetail;
	}
	
	/**
	 * 從資訊源取直到 OutputVO 裡
	 * @param doGetCoverage01OutputVO 資訊源取得的資料
	 * @param lstInsData 來源資料
	 * @return 整理過的 outputVO
	 * @throws JBranchException
	 */
	private GetInsPremOutputVO setOutputVO(DoGetCoverage01OutputVO doGetCoverage01OutputVO, GenericMap lstInsData) throws JBranchException{
		List<Map> lstCoverageTableList = doGetCoverage01OutputVO.getLstCoverageTable();
		GenericMap lstCoverageTable = null;
		GetInsPremOutputVO outputVO = new GetInsPremOutputVO();
		
		if(CollectionUtils.isNotEmpty(lstCoverageTableList)) {
			lstCoverageTable = new GenericMap(lstCoverageTableList.get(0));
			
			if(lstCoverageTable.getBigDecimal("YEARPREM").intValue() == 0) {
				throw new APException("無此險種保費資訊");
			}
		} 
		else{
			throw new APException("無此險種保費資訊");
		}
		
		outputVO.setInsco(lstInsData.getNotNullStr("INSCO")); // insco	保險公司編號	lstInsData.INSCO
		outputVO.setInsco_name(lstCoverageTable.getNotNullStr("INSCOMPANY")); // insco_name	保險公司名稱	lstCoverageTable.InsCompany
		outputVO.setProdKeyno(lstInsData.getNotNullStr("PRD_KEYNO")); // prod_keyno	資訊源商品代碼	lstInsData.PRD_KEYNO
		outputVO.setProd_id(lstInsData.getNotNullStr("PRD_ID")); // prod_id	商品代號	lstInsData.PRD_ID
		outputVO.setProd_name(lstCoverageTable.getNotNullStr("PRODNAME")); // prod_name	商品名稱	lstCoverageTable.ProdName
		outputVO.setPremterm(lstCoverageTable.getNotNullStr("PREM")); // premterm	年期	lstCoverageTable.prem
		
		// 保險年度
		Calendar cal = Calendar.getInstance();
		int nowYear = cal.get(Calendar.YEAR);
		cal.setTime(lstInsData.getDate("EFFECTED_DATE"));
		int effectedYear = cal.get(Calendar.YEAR);;
		outputVO.setYear((short)(nowYear - effectedYear + 1)); // premterm	年期	lstCoverageTable.prem
		
		if(lstInsData.getNotNullStr("UPTYPE").matches("0[1,2]")) {
			outputVO.setQuantity(NumberFormat.getNumberInstance().format(new BigDecimal((String)lstInsData.get("UPQTY_SEL"))));
		} 
		else if(lstInsData.isStringBank("UPTYPE") && lstInsData.isNotNull("INSUREDAMT")) {
			outputVO.setQuantity(NumberFormat.getNumberInstance().format(lstInsData.getBigDecimal(("INSUREDAMT"))));
		} 

		outputVO.setCoverunit(lstInsData.getNotNullStr("COVERCACULUNITDESC")); // coverunit	保額單位	COVERCACULUNITDESC
		
		// YEARPREM
		BigDecimal yearPrem = lstCoverageTable.getBigDecimal("YEARPREM");
		outputVO.setPremium((yearPrem.compareTo(new BigDecimal(0)) == 0 ? null : yearPrem));
		
		// tmpRepay
		// 這情況只會有一筆
		List<Map<String, Object>> sortnoList = insjlbDao.queryOlditemSortnoMap(new String[]{"R"});
		BigDecimal tmpRepay = BigDecimal.ZERO;
		
		if(CollectionUtils.isNotEmpty(doGetCoverage01OutputVO.getLstExpression()) && CollectionUtils.isNotEmpty(sortnoList)) {
			for(Map<String, Object> expressMap : doGetCoverage01OutputVO.getLstExpression()) {
				if (expressMap.get("SORTNO").equals(sortnoList.get(0).get("PARAM_NAME"))) {
					tmpRepay = (BigDecimal) expressMap.get("BEGUNITPRICE");	
					break;
				}
			}
		}
		
		outputVO.setPayType(lstInsData.getNotNullStr("PAYTYPE"));
		outputVO.setRepay(tmpRepay); // repay	當年度生存還本金	tmpRepay
		premiumConvert(outputVO);
		outputVO.setLstCoverAgePrem(doGetCoverage01OutputVO.getLstCoverAgePrem()); // lstCoverAgePrem	逐年保障領回清單	=doGetCoverage01.lstCoverAgePrem
		outputVO.setLstExpression(doGetCoverage01OutputVO.getLstExpression());
		return outputVO;
	}

	/**
	 * ERROR MESSAGE SHOW
	 * @param args
	 * @return
	 */
	private static List<String> createErrorMsgList(String...args){
		List errorMsg = new ArrayList<String>();
		CollectionUtils.addAll(errorMsg, args);
		return errorMsg;
	}
	
	/**
	 * 處理年化保費反推回去個期繳別要繳多少錢
	 * @param outputVO
	 * @return
	 */
	private void premiumConvert(GetInsPremOutputVO outputVO) {
		BigDecimal afterConvertPremium = BigDecimal.ZERO;
		// 處理化年保費 根據繳別反推
		String payType = outputVO.getPayType();
		payType = ObjectUtils.toString(payType).isEmpty() ? "1" : payType;
		BigDecimal premium = outputVO.getPremium();
		BigDecimal premiumPayValue = BigDecimal.ONE;
		switch(payType) {
			// 半年繳 化年保費/0.52 /2
			case "2":{
				premiumPayValue = new BigDecimal("0.52");
				break;
			}
			// 季繳 化年保費/0.262 /4
			case "4":{
				premiumPayValue = new BigDecimal("0.262");
				break;
			}
			// 月繳 化年保費/0.088 /12
			case "12":{ 
				premiumPayValue = new BigDecimal("0.088");
				break;
			}
			default :{ // 1-年繳, 0-躉繳, 99-彈性繳
				break;
			}
		}
		afterConvertPremium = premium.multiply(premiumPayValue).setScale(0 , BigDecimal.ROUND_HALF_UP);
		outputVO.setPremium(afterConvertPremium);
	}
	
	/** 底下為注入的 Service set get */
	
	/** 注入的 Service set get*/
	public GetCoverage01ServiceInf getGetCoverage01Service() {
		return getCoverage01Service;
	}
	

	public void setGetCoverage01Service(GetCoverage01ServiceInf getCoverage01Service) {
		this.getCoverage01Service = getCoverage01Service;
	}

	
	public InsjlbDaoInf getInsjlbDao() {
		return insjlbDao;
	}
	

	public void setInsjlbDao(InsjlbDaoInf insjlbDao) {
		this.insjlbDao = insjlbDao;
	}
	
	
}
