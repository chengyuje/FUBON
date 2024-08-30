package com.systex.jbranch.app.server.fps.insjlb.chk;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;

import com.systex.jbranch.app.server.fps.insjlb.InsjlbParamInf;
import com.systex.jbranch.app.server.fps.insjlb.InsjlbUtils;
import com.systex.jbranch.app.server.fps.insjlb.LstInsDataParamInf;
import com.systex.jbranch.comutil.collection.GenericMap;
import com.systex.jbranch.platform.common.errHandle.JBranchException;

public enum ChkLstInsData implements InsjlbParamInf , LstInsDataParamInf{
	/**檢查 產品代碼**/
	PRD_NAME_CHK("chkItemPrdName"),
	/**檢查 ITEM_Y (繳費年期)**/
	PAYMENTYEAR_SEL_CHK("chkItemPaymentyearSel"),
	/**檢查 ITEM_A (保障年期)**/
	COVERYEAR_SEL_CHK("chkItemCoveryearSel"),
	/**檢查 ITEM_K (型別)**/
	KIND_SEL_CHK("chkItemKindSel"),
	/**檢查 ITEM_P (計畫)**/
	PLAN_CHK("chkItemPlan"),
	/**檢查 ITEM_U (單位)**/
	UNIT_CHK("chkItemUnit"),
	/**檢查 LIST_O (對象)**/
	OBJECT_CHK("chkListObject"),
	/**檢計算＆檢查投保年齡**/
	INSURED_AGE_CHK("chkInsuredAge");
	
	public static Logger logger = org.slf4j.LoggerFactory.getLogger(ChkLstInsData.class);
	
	private String mathodName;
	
	ChkLstInsData(String mathodName){
		this.mathodName = mathodName;
	}
	
	/**檢查繳費年期PAYMENTYEAR_SEL有沒有包含在ITEM_Y中*/
	public Map<String, Object> chkItemPaymentyearSel(GenericMap lstInsData){
    	return chkItem(ITEM_Y , PAYMENTYEAR_SEL ,lstInsData , "ehl_01_insjlb_002");
	}
	
	/**檢查保障年期COVERYEAR_SEL有沒有在ITEM_A中*/
	public Map<String, Object> chkItemCoveryearSel(GenericMap lstInsData){
		return chkItem(ITEM_A , COVERYEAR_SEL , lstInsData , "ehl_01_insjlb_003");
	}
	
	/**檢查型別KIND_SEL是否存在ITEM_K中*/
	public Map<String, Object> chkItemKindSel(GenericMap lstInsData){
		return chkItem(ITEM_K , KIND_SEL , lstInsData , "ehl_01_insjlb_004");
	}
	
	/**檢查對象*/
	public Map<String, Object> chkListObject(GenericMap lstInsData){
		return chkItem(LIST_O , INSURED_OBJECT ,lstInsData , "ehl_01_insjlb_008");
	}
	
	/**檢查商品帶碼*/
	public Map<String, Object> chkItemPrdName(GenericMap lstInsData){
		Map<String, Object> result_map = null;
		
		//產品代碼不可以是空的
		if(lstInsData.isStringBank(PRD_NAME)){
			logger.info("error : PRD_NAME is empty");
			
			return createErrorMap(
	    		lstInsData.getNotNullStr(PRD_ID) ,
	    		lstInsData.getNotNullStr(CUST_ID) , 
	    		"Y".equals(lstInsData.get(IS_MAIN)) ? "主約" : "附約" , 
	    		lstInsData.getNotNullStr(POLICY_NBR) , 
	    		"ehl_01_insjlb_001"
	    	);
		}
		
		return result_map;
	}
	
	/**檢查計畫，當ITEM_P有值，UPTYPE必須為02，且UPQTY_SEL必須存在於ITEM_P之中*/
	public Map<String, Object> chkItemPlan(GenericMap lstInsData){
		if(lstInsData.isStringBank(ITEM_P))
			return null;
		
		if(!"02".equals(lstInsData.get(UPTYPE))){
			logger.info("error : in ITEM_P not empty , UPTYPE is not 02 →　ehl_01_insjlb_005");
			return createErrorMap(lstInsData , "ehl_01_insjlb_005");
		}
		
		return chkItem(ITEM_P , UPQTY_SEL , lstInsData , "ehl_01_insjlb_006");
	}
	
	/**檢查單位*/
	public Map<String, Object> chkItemUnit(GenericMap lstInsData){
		if(lstInsData.isStringBank(ITEM_U)){
			return null;
		}
		
		//UPTYPE <> 01 或 UPTYPE = 01 但 UPQTY_SEL = 空白 
		if(	!"01".equals(lstInsData.get(UPTYPE)) || lstInsData.isStringBank(UPQTY_SEL)){
			logger.info("error : in ITEM_U not empty , UPTYPE is not 01 or UPQTY_SEL is empty →　ehl_01_insjlb_007");
			return createErrorMap(lstInsData , "ehl_01_insjlb_007");
		}
		
		return null;
	}
	
	/**檢計算＆檢查投保年齡 call calculateInsAge傳入INSURED_BIRTHDAY, 
	 * EFFECTED_DATE回傳年齡寫入 INSURED_AGE*/
	public Map<String, Object> chkInsuredAge(GenericMap lstInsData){
		//投保年齡
		int insuredAge = -1;
		//生日
		Date insuredBirthday = lstInsData.getDate(INSURED_BIRTHDAY);
		//生效日
		Date effectedDate = lstInsData.getDate(EFFECTED_DATE);
		
		String msgTitle = "chkInsuredAge : ";
		String errorMsg = null;
		
		//判斷生日與生效日是否為空，若有空拋錯
		errorMsg = insuredBirthday == null ? "INSURED_BIRTHDAY is null" 
				 : effectedDate == null ? "EFFECTED_DATE is null" 
				 : null;
		
		//若生日與生效日期一為空拋錯
		if(StringUtils.isNotBlank(errorMsg)){
			logger.error(msgTitle + errorMsg);
			return createErrorMap(lstInsData , "ehl_01_insjlb_011");
		}	
		
		//計算投保年齡:保單生效日與生日的相差幾年
		insuredAge = InsjlbUtils.calculateInsAge(effectedDate , insuredBirthday);
				
		lstInsData.put(INSURED_AGE, new BigDecimal(insuredAge));

		//若投保年齡計算出來小於0(保單生效日在此人出生之前)
		if(insuredAge < 0) {
			logger.debug("error : insuredAge " + insuredAge + " < 0 →　ehl_01_insjlb_011");
			return createErrorMap(lstInsData , "ehl_01_insjlb_011");
		}
		
		return null;
	}
	
	
	/**ITEM沒有值的時候對應欄位不檢查，若有值，需檢查對應欄位是否存在於ITEM範圍內的*/
	public Map<String, Object> chkItem(String itemName , String needInItemValName , GenericMap lstInsData , String errCode){
		String item = lstInsData.getNotNullStr(itemName);
		String needInItemVal = lstInsData.getNotNullStr(needInItemValName);
		
		//範圍
		String tmpItem = ObjectUtils.toString(item)
			.replaceFirst(",$", "")
			.replaceAll(",", "|");
		
		//需在範圍內的對象
		needInItemVal = ObjectUtils.toString(needInItemVal);
		
		if(StringUtils.isBlank(item) || needInItemVal.matches(tmpItem)){
			return null;
		}
		
		logger.info("error : " +needInItemValName + " not in " + itemName + " →　" + errCode);
	
		return createErrorMap(lstInsData , errCode);
	}
	
	
	@SuppressWarnings("unchecked")
	public Map<String, Object> chkItem(GenericMap lstInsData) throws JBranchException{
		try{
			return (Map<String, Object>) this.getClass()
				.getMethod(this.mathodName , new Class[]{GenericMap.class})
				.invoke(this , new Object[]{lstInsData});
		}
		catch(Exception ex){
			ex.printStackTrace();
			throw new JBranchException(ex);
		}
	}
	
	public static Map<String, Object> createErrorMap(GenericMap lstInsData , String logStr){
		return createErrorMap(
			lstInsData.getNotNullStr(PRD_ID) ,
			lstInsData.getNotNullStr(CUST_ID) , 
			lstInsData.getNotNullStr(PRD_NAME) , 
			lstInsData.getNotNullStr(POLICY_NBR) , 
			logStr
		);
	}
	
	public static Map<String, Object> createErrorMap(String allProductsID , String custid , String prodName , String policyNo , String logStr){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("AllProductsID", allProductsID);//產品代碼
		resultMap.put("custID ", custid);
		resultMap.put("ProdName", prodName);
		resultMap.put("PolicyNo", policyNo);//保單編號 
		resultMap.put("logStr", logStr);//保單編號
    	
    	return resultMap;
	}
}
