package com.systex.jbranch.app.server.fps.insjlb.service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;

import com.systex.jbranch.app.server.fps.insjlb.InsjlbParamInf;
import com.systex.jbranch.app.server.fps.insjlb.InsjlbUtils;
import com.systex.jbranch.app.server.fps.insjlb.LstInsDataParamInf;
import com.systex.jbranch.comutil.callBack.CallBackExcute;
import com.systex.jbranch.comutil.collection.CollectionSearchUtils;
import com.systex.jbranch.comutil.collection.GenericMap;
import com.systex.jbranch.platform.common.errHandle.JBranchException;

import java.util.Collections;

@SuppressWarnings({"rawtypes" , "unchecked"})
public class GetCoverageService extends GetInsService implements LstInsDataParamInf{	
	/**檢核保單資料是否符合資訊源要求 - mast and detail*/
	public Map<String , List<Map<String , Object>>> initPolicyInfSourceData(List<Map> lstInsDataList , List<Map<String , Object>> lstFamily) throws JBranchException{
		//保單主檔明細
		Map<String , List<Map<String , Object>>> result = doGetPcMasterDetail(lstInsDataList);
		//親屬關係
		result.put(LST_RELATION, doGetPcRelation(lstFamily));
		
		return result;
	}
	
	/**檢核保單資料是否符合資訊源要求 - mast and detail*/
	public Map<String , List<Map<String , Object>>> doGetPcMasterDetail(List<Map> lstInsDataList) throws JBranchException{
		Map<String , List<Map<String , Object>>> result = new HashMap<String , List<Map<String , Object>>>();
		result.put(POLICY_MAST, new ArrayList());//主檔
		result.put(POLICY_DETAIL, new ArrayList());//明細
		
		for(Map<String , Object> lstInsDataMap : lstInsDataList){
			//保單明細：逐筆資料放入getCoverage03InputVO.lstPolicyDetail
			GenericMap lstInsData = new GenericMap(lstInsDataMap);
			result.get(POLICY_DETAIL).add(initLstPolicyDetail(lstInsData));
			
			//保單主檔：判斷主檔中是否有指定的元素存在 - 相同的X.INSSEQ資料只放入一筆getCoverage03InputVO.lstPolicyMaster
			if(!"Y".equals(lstInsData.getNotNullStr(IS_MAIN))){
				continue;
			}
			
			//判斷用map
			GenericMap checkGmap = new GenericMap();
			//保單序號
			checkGmap.put(INSSEQ, lstInsData.getNotNullStr(INSSEQ));
			//保險公司代號
			checkGmap.put(COM_ID, lstInsData.getNotNullStr(COM_ID));
			//保單狀態
			checkGmap.put(POLICY_STATUS, lstInsData.getNotNullStr(POLICY_STATUS));
			
			//尋找有沒有相同INSSEQ , COM_ID , POLICY_STATUS的資料在result.get(POLICY_MAST)中，若無加一筆，若重複僅保留一筆即可
			Map resultMap = CollectionSearchUtils.findMapByKey(
				result.get(POLICY_MAST) , checkGmap.getParamMap() , INSSEQ , COM_ID , POLICY_STATUS);
			
			if(resultMap == null){
				result.get(POLICY_MAST).add(initLstPolicyMast(lstInsData));
			}	
		}
		
		return result;
	}
	
	/**LstInsData to 明細*/
	public Map initLstPolicyDetail(GenericMap lstInsData) throws JBranchException{
		//投保對象
		String insuredRelation = "";
		//原幣參考匯率
		BigDecimal refExcRate = BigDecimal.valueOf(1.0);
		//原幣幣別
		String currCd = ObjectUtils.toString(lstInsData.get("CURR_CD"));
		//參考匯率All
		Map<String , BigDecimal> refExcRateMap = getIns810().queryRefExcRate();
		//取得參考匯率
		if(StringUtils.isNotBlank(currCd) && MapUtils.isNotEmpty(refExcRateMap) && refExcRateMap.get(currCd) != null){
			refExcRate = refExcRateMap.get(currCd);
		}
				
		Map lstPolicyDetail = new HashMap<String , Object>();
		lstPolicyDetail.put("INSSEQ" , lstInsData.get(INSSEQ));//保單序號
		lstPolicyDetail.put("INSCO" , lstInsData.get(COM_ID));//保險公司代號
		lstPolicyDetail.put("IS_MAIN" , "Y".equals(ObjectUtils.toString(lstInsData.get(IS_MAIN))) ? "1" : "0");//是否主約
		lstPolicyDetail.put("THIRDPROD_CODE" , InsjlbParamInf.BLANK);//商品型號
		lstPolicyDetail.put("PROD_NAME" , lstInsData.get(PRD_NAME));//商品描述
		lstPolicyDetail.put("Premium" , lstInsData.get(INSYEARFEE));//單項總保費(原幣)
		lstPolicyDetail.put("SocialSecurity" , InsjlbParamInf.BLANK);//有無社保
		lstPolicyDetail.put("INSURED_NAME" , ObjectUtils.toString(lstInsData.get(INSURED_NAME)).trim());//客戶姓名 
		lstPolicyDetail.put("INSURED_AGE" , lstInsData.get(INSURED_AGE));//投保年齡
		lstPolicyDetail.put("INSURED_GENDER" , lstInsData.get(INSURED_GENDER));//性別
		lstPolicyDetail.put("JOB_GRADE" , 1);//職等級
		lstPolicyDetail.put("Days" , 0);//旅行平安險購買天數
		lstPolicyDetail.put("EFFECTED_DATE" , lstInsData.get(EFFECTED_DATE));//保單生效日
		lstPolicyDetail.put("AllProductsID" , lstInsData.get(PRD_KEYNO));//AllProductsID
		lstPolicyDetail.put("AnnuityPayType" , 1);//年金給付方式
		lstPolicyDetail.put("AnnuityBgnPayAge" , 0);//年金給付開始年齡
		lstPolicyDetail.put("PolicyDesc" , InsjlbParamInf.BLANK);//保單保險全資料
		lstPolicyDetail.put("INSURED_BIRTHDAY" , lstInsData.get(INSURED_BIRTHDAY));//生日
		lstPolicyDetail.put(INSURED_ID , lstInsData.get(INSURED_ID));//被保險人ID
		lstPolicyDetail.put("DetailSEQ" , lstInsData.get(KEYNO));//主附約唯一辨識序號
		lstPolicyDetail.put("CUR_RATE" , refExcRate.doubleValue());//匯率
		
		//計畫
		if("02".equals(lstInsData.get(UPTYPE)) && lstInsData.isStringNotBank(ITEM_P)){
			lstPolicyDetail.put("Plan" , lstInsData.get(UPQTY_SEL));
		}
		else{
			lstPolicyDetail.put("Plan" , InsjlbParamInf.BLANK);
		}
		
		//商品數量或保額(原幣)
		if("01".equals(lstInsData.get(UPTYPE)) && lstInsData.isStringNotBank(ITEM_U)){
			lstPolicyDetail.put("ProdQuantity" , lstInsData.getBigDecimal(UPQTY_SEL));
		}
		else if("02".equals(lstInsData.get(UPTYPE)) && lstInsData.isStringNotBank(ITEM_P)){
			lstPolicyDetail.put("ProdQuantity" , 0);
		}
		else{
			lstPolicyDetail.put("ProdQuantity" , lstInsData.get(INSUREDAMT));
		}
		
		//屬性歸類(中文)
		if(lstInsData.isStringNotBank(ITEM_K)){
			lstPolicyDetail.put("Kind" , lstInsData.get(KIND_SEL)); 
		}
		else{
			lstPolicyDetail.put("Kind" , InsjlbParamInf.BLANK);
		}
		
		//投保對象
		if(lstInsData.isStringBank(INSURED_OBJECT) || lstInsData.isStringBank(LIST_O)){
			lstPolicyDetail.put("INSURED_RELATION" , insuredRelation = "0");//投保對象			
		}
		else {
			insuredRelation = InsjlbUtils.getParameterVal3(
				InsjlbParamInf.INSURED_OBJECT_MAP, lstInsData.getNotNullStr(INSURED_OBJECT)
			);
			
			lstPolicyDetail.put("INSURED_RELATION" , insuredRelation);//投保對象
		}
		
		//保險對象
		lstPolicyDetail.put("IObject" , insuredRelation.matches("[0,1,2]") ? insuredRelation : "0");//保險對象
		
		//TODO要改
		//親屬代碼
		//lstPolicyDetail.put("RelationCode" , lstInsData.getNotNullStr("RELATIONCODE"));
		lstPolicyDetail.put("RelationCode" , 0);
		
		//繳費年期
		if(lstInsData.isStringNotBank(ITEM_Y))
			lstPolicyDetail.put("PremTerm" , lstInsData.get(PAYMENTYEAR_SEL)); 
		else{
			lstPolicyDetail.put("PremTerm" , InsjlbParamInf.BLANK);
		}
		
		//累積年期(保障年期)
		if(lstInsData.isStringNotBank(ITEM_A))
			lstPolicyDetail.put("AccuTerm" , lstInsData.get(COVERYEAR_SEL));
		else{
			lstPolicyDetail.put("AccuTerm" , InsjlbParamInf.BLANK); 
		}

		return lstPolicyDetail;
	}
	
	/**LstInsData to 主檔內容*/
	public Map initLstPolicyMast(GenericMap lstInsData) throws JBranchException{
		Map lstPolicyMast = null;
		lstPolicyMast = new HashMap<String , Object>();
		lstPolicyMast.put("INSSEQ" , lstInsData.get(INSSEQ));//保單序號
		lstPolicyMast.put("POLICYNO" , lstInsData.get(POLICY_NBR));//保單號碼
		
		//TODO繳法要調整
		lstPolicyMast.put("PAYTYPE" , lstInsData.get(PAY_TYPE));//繳法
		
		//01→主約繳費期滿附約繳費中、22→保單正常、32→本保單已豁免保費、41→保單繳費期滿、42→躉繳、44→保單已辦理展期定期保險、45→保單已辦理減額繳清保險、
		//54→天數墊繳保費中、94→保單已辦理轉換、95→保單已解約、96→身故或全殘、97→保單已滿期、98→保單已辦理契約撤銷、99→保單停效、0→其它
		lstPolicyMast.put("POLICY_STATUS", "22");//保單狀態(正常件為22)
		
		if(lstInsData.get(EFFECTED_DATE) != null){
			Matcher mat = Pattern.compile("(\\d{4})(\\d{2})(\\d{2})").matcher(
				new SimpleDateFormat("yyyyMMdd").format(lstInsData.getDate(EFFECTED_DATE))
			);
			
			if(mat.find()){
				String chYear = new BigDecimal(mat.group(1)).subtract(new BigDecimal(1911)).toString();
				lstPolicyMast.put("SIGN_YY" , chYear);//民國年
				lstPolicyMast.put("SIGN_MM" , mat.group(2));//月
				lstPolicyMast.put("SIGN_DD" , mat.group(3));//日
			}
		}
		
		return lstPolicyMast;
	}
	
	
	/**親屬關係*/
	public List doGetPcRelation(List<Map<String , Object>> lstFamilys) throws JBranchException{
		if(CollectionUtils.isEmpty(lstFamilys)){
			return null;
		}
		
		List lstRelations = new ArrayList();
		int child = 0;
		try{
			if(CollectionUtils.isNotEmpty(lstFamilys)){
				for(Map<String , Object> lstFamily : lstFamilys){
					GenericMap lstFamilyGmap = new GenericMap(lstFamily);
					String relationCode = null;
					String relationSeq = null;
					
					//1:本人/配偶 2:子女
					relationCode =  lstFamilyGmap.matches("RELATIONCODE" , "0|1")? "1" : "2";
					
					//0：本人、1:配偶、2：子女
					if(!lstFamilyGmap.matches("RELATIONCODE" , "0|1|2"))
						continue;
					
					//本人
					if("0".equals(lstFamilyGmap.getNotNullStr("RELATIONCODE"))){
						relationSeq = "1";
					}
					//配偶
					else if("1".equals(lstFamilyGmap.getNotNullStr("RELATIONCODE"))){
						relationSeq = "2";
					}
					//子女
					else{
						relationSeq = String.valueOf(2+ ++child);
					}
					
					String relationBirthday = new SimpleDateFormat("yyyy/MM/dd").format(
						new SimpleDateFormat("yyyy-MM-dd").parse(lstFamilyGmap.getNotNullStr("RELATION_BIRTHDAY")));
					
					
					lstRelations.add(new GenericMap()
						.put("RELATIONCODE" , relationCode)//親屬代碼
						.put("INSURED_ID" , lstFamilyGmap.getNotNullStr("RELATION_ID"))//客戶姓名
						.put("Birth" , relationBirthday)//生日
						.put("Sex" , lstFamilyGmap.getNotNullStr("RELATION_GENDER"))//性別
						.put("RelationSEQ" , relationSeq)//順序
						.getParamMap()
					);
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
			throw new JBranchException(ex);
		}
		
		return lstRelations;
	}
	
	public List<Map> selectNotB0013(List<Map> lstExpressionList , CallBackExcute callBack) throws JBranchException{
		List<Map> lstExpressionStructureList = CollectionSearchUtils.selectMapNotInList(
				lstExpressionList, "SORTNO", "B0013" , callBack);
		
		final String [] keys = {INSURED_ID , INSSEQ , "INSCOMPANY"};
		
		//找出不是B0013的lstExpression
		if(CollectionUtils.isNotEmpty(lstExpressionStructureList)){
			//排序
			Collections.sort(lstExpressionStructureList, new Comparator<Map>() {
				public int compare(Map obj, Map obj2) {
					GenericMap objGmap = new GenericMap(obj);
					GenericMap objGmap2 = new GenericMap(obj2);
					int result = 0;
					
					for(String key : keys){
						if((result = objGmap.getNotNullStr(key).compareTo(objGmap2.getNotNullStr(key))) != 0){
							return result;
						}
					}
					
					return result;
				}
			});
		}

		return lstExpressionStructureList;
	}
}
