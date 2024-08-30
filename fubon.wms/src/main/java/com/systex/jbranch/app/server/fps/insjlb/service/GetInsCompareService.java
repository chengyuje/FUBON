package com.systex.jbranch.app.server.fps.insjlb.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.ibm.icu.math.BigDecimal;
import com.systex.jbranch.app.server.fps.insjlb.InsjlbUtils;
import com.systex.jbranch.app.server.fps.insjlb.vo.DoGetCoverage01OutputVO;
import com.systex.jbranch.app.server.fps.insjlb.vo.GetInsCompareInputVO;
import com.systex.jbranch.app.server.fps.insjlb.vo.GetInsCompareOutputVO;
import com.systex.jbranch.comutil.collection.GenericMap;
import com.systex.jbranch.platform.common.errHandle.JBranchException;

@Service("GetInsCompareService")
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
@SuppressWarnings({ "unchecked", "rawtypes" , "unused"})
public class GetInsCompareService extends GetCoverageService  implements GetInsCompareServiceInf{
	@Autowired @Qualifier("GetCoverage01Service")
	private GetCoverage01ServiceInf getCoverage01Service;
	
	/**保險險種比較<br>*/
	public GetInsCompareOutputVO getInsCompare(GetInsCompareInputVO inputVo) throws JBranchException{
		String tempAge		= null;
		String tempPayType	= null;
		String tempJobGrade = null;
		String tempPremTerm = null;
		String tempAccuTerm = null;
		String tempUnit	 	= null;
		String temp100000	= null;
		String temp10000	= null;
		String temp1000	 	= null;
		String temp100		= null;
		String tempOne 		= null;
		
		//RELATIONCODE
		int wkIdx = 0;
		
		//保險比較參數 - 預設參數
		List<Map<String , Object>> insCompateParams = getInsjlbDao().queryParameterConf("INS.ECCPDA_COMPARE");
		List<Map<String , Object>> financialProducts =  null;
		List<Map> lstExpressionCombindList = new ArrayList();
		
		GetInsCompareOutputVO outputVo = new GetInsCompareOutputVO();
		outputVo.setLstExpressionCombind(lstExpressionCombindList);
		outputVo.setPrintType("C");
		
		if(CollectionUtils.isEmpty(insCompateParams)){
			throw new JBranchException("比較參數未設置");
		}
		
		//設定預設參數
		for(Map<String , Object> insCompateParam : insCompateParams){
			String paramCode = ObjectUtils.toString(insCompateParam.get("PARAM_CODE"));
			String paramName = ObjectUtils.toString(insCompateParam.get("PARAM_NAME"));
			
			if(paramCode.equals("INSURED_AGE")){
				tempAge = paramName;
			}else if(paramCode.equals("PAYTYPE")){
				tempPayType = paramName;
			}else if(paramCode.equals("JOB_GRADE")){
				tempJobGrade = paramName;
			}else if(paramCode.equals("PREMTERM")){
				tempPremTerm = paramName;
			}else if(paramCode.equals("ACCUTERM")){
				tempAccuTerm = paramName;
			}else if(paramCode.equals("UNIT")){
				tempUnit = paramName;
			}else if(paramCode.equals("100000")){
				temp100000 = paramName;
			}else if(paramCode.equals("10000")){
				temp10000 = paramName;
			}else if(paramCode.equals("1000")){
				temp1000 = paramName;
			}else if(paramCode.equals("100")){
				temp100 = paramName;
			}else if(paramCode.equals("1")){
				tempOne = paramName;
			}
		}
		
		//讀取資訊源商品檔
		if(CollectionUtils.isEmpty(financialProducts = getInsjlbDao().queryFinancialProduct(inputVo.getLstInsProd()))){
			throw new JBranchException("查無資料");
		}
		
		//逐筆讀取資料
		for(Map<String , Object> financialProduct : financialProducts){
			wkIdx++;
			
			GenericMap fpGmap = new GenericMap(financialProduct);
			Map<String , Object> lstInsDetail = new HashMap<String , Object>();			
			String [] dftSettings = null;
			List<Map> lstCoverageTableM = null;
			List<Map> lstCoverageTableF = null;
			List<Map> lstExpression = null;
			String premyearMan = null;
			String premyearWoman = null;
			
			//解析參數
			GenericMap dftSetGmap = InsjlbUtils.reDftSettingToMap(fpGmap.getNotNullStr("DFT_SETTING"));
			String tempPremTermDesc = null;
			String tempUnitDesc = null;
			
			//商品ID
			lstInsDetail.put("AllProductsID", fpGmap.get("KEY_NO"));
			//單項總保費(原幣)
			lstInsDetail.put("Premium", "");
			//Kind屬性歸類處理
			lstInsDetail.put("Kind" , doSetItemK(fpGmap , dftSetGmap));
			//有無社保
			lstInsDetail.put("SocialSecurity", "");
			//投保年齡
			lstInsDetail.put("INSURED_AGE", tempAge);
			//繳法
			lstInsDetail.put("PAYTYPE", tempPayType);
			//職等級
			lstInsDetail.put("JOB_GRADE", tempJobGrade);
			
			lstInsDetail.put("CURR_CD", fpGmap.get("CURR_CD"));
			
			//ITEM_Y 繳費年期
			GenericMap itemGmap = new GenericMap(doSetItemY(fpGmap , tempPremTerm));
			lstInsDetail.put("PremTerm", itemGmap.get("term"));
			tempPremTermDesc = itemGmap.getNotNullStr("tempDesc");
			
			//旅行平安險購買天數
			lstInsDetail.put("IDays" , 0);
			//空字串
			lstInsDetail.put("ICOUNT" , "");
			//年金給付方式
			lstInsDetail.put("FIELDG" , "");
			//年金給付開始年齡
			lstInsDetail.put("FIELDX" , "");
			//保險對象
			lstInsDetail.put("IObject" , 0);
			//保單保全資料
			lstInsDetail.put("PolicyDesc" , "");
			//是否終身
			lstInsDetail.put(IS_WHOLE_LIFE , fpGmap.getNotNullStr(IS_WL));	
			//ITEM_A 繳費年期
			lstInsDetail.put("AccuTerm", doSetItemA(fpGmap , tempAccuTerm).get("term"));
			
			//計畫
			if(fpGmap.isStringNotBank(ITEM_P)){
				lstInsDetail.put("Plan" , tempUnitDesc = dftSetGmap.isStringNotBank("P") ? 
					dftSetGmap.getNotNullStr("P") : fpGmap.getNotNullStr(ITEM_P).split(",")[0]
				);
			}
			//單位
			else if(fpGmap.isStringNotBank(ITEM_U)){
				tempUnitDesc = dftSetGmap.isStringNotBank("U") ? 
					dftSetGmap.getNotNullStr("U") : fpGmap.getNotNullStr(ITEM_U).split(",")[0];
				lstInsDetail.put("UNIT" , tempUnitDesc.replaceFirst("\\-.*", ""));
			}
			//商品數量或保額(原幣)
			else {
				int coverCaculunit = fpGmap.getBigDecimal(COVERCACULUNIT).intValue();
				lstInsDetail.put(PRODQUANTITY , tempUnitDesc = 
					coverCaculunit == 100000 ? temp100000 :
					coverCaculunit == 10000 ? temp10000 :
					coverCaculunit == 1000 ? temp1000 :
					coverCaculunit == 100 ? temp100 :
//					coverCaculunit == 10 ? temp10 :
					coverCaculunit == 1 ? tempOne : "0"
				);
			}
			
			tempUnitDesc += fpGmap.getNotNullStr(COVERCACULUNITDESC);
			
			//性別為男性查一次資訊源
			lstInsDetail.put("INSURED_GENDER", "M");
			DoGetCoverage01OutputVO cov01OutputVo = getCoverage01Service.doGetCoverage01(lstInsDetail);
			lstCoverageTableM = cov01OutputVo.getLstCoverageTable();
			lstExpression = cov01OutputVo.getLstExpression();
			
			//性別為女性查一次資訊源
			lstInsDetail.put("INSURED_GENDER", "F");
			cov01OutputVo = getCoverage01Service.doGetCoverage01(lstInsDetail);
			lstCoverageTableF = cov01OutputVo.getLstCoverageTable();
			
			//如果有男性的保障中間檔CoverageTable
			if(CollectionUtils.isNotEmpty(lstCoverageTableM)){
				GenericMap ctM = new GenericMap(lstCoverageTableM.get(0));
				
				if(ctM.getBigDecimal("YEARPREM").intValue() != 0){
					premyearMan = ObjectUtils.toString(ctM.getBigDecimal("YEARPREM").intValue());
					premyearMan = InsjlbUtils.changeStr(ObjectUtils.toString(premyearMan));
					premyearMan = premyearMan + "元";
				}
			}
			
			//如果有女性的保障中間檔CoverageTable
			if(CollectionUtils.isNotEmpty(lstCoverageTableF)){
				GenericMap ctF = new GenericMap(lstCoverageTableF.get(0));
				
				if(ctF.getBigDecimal("YEARPREM").intValue() != 0){
					premyearWoman = ObjectUtils.toString(ctF.getBigDecimal("YEARPREM").intValue());
					premyearWoman = InsjlbUtils.changeStr(ObjectUtils.toString(premyearWoman));
					premyearWoman = premyearWoman + "元";
				}
			}
			
			//給附中間檔
			for(Map lstExpressionMap : lstExpression){
				Map lstExpressionCombindMap = new HashMap();
				lstExpressionCombindMap.putAll(lstExpressionMap);
				lstExpressionCombindMap.put("CUSTNAME" , "");
				lstExpressionCombindMap.put("CUSTOICY" , wkIdx);
				lstExpressionCombindMap.put("RELATIONCODE" , wkIdx);
				lstExpressionCombindMap.put("BIRTH" , "");
				lstExpressionCombindMap.put("INSCOMPANY" , fpGmap.getNotNullStr("KEY_NO").substring(0, 3));
				lstExpressionCombindMap.put("POLICYNO" , fpGmap.getNotNullStr(PRD_ID));
				lstExpressionCombindMap.put("SIGNDATE" , fpGmap.getNotNullStr(PRD_NAME));
				lstExpressionCombindMap.put("INSQUANTITY" , tempUnitDesc);
				lstExpressionCombindMap.put("PAYTYPEPEMIUM" , tempPremTermDesc);
				lstExpressionCombindMap.put("INSSEQ" , fpGmap.getNotNullStr("KEY_NO"));
				lstExpressionCombindMap.put("INSURED_ID" , fpGmap.getNotNullStr("KEY_NO"));
				//如果有男性的保障中間檔CoverageTable
				lstExpressionCombindMap.put("MAINPROD" , StringUtils.isNotBlank(premyearMan) ? premyearMan : "");
				//如果有女性的保障中間檔CoverageTable
				lstExpressionCombindMap.put("POLICYSTATUS" , StringUtils.isNotBlank(premyearWoman) ? premyearWoman : "");
				lstExpressionCombindList.add(lstExpressionCombindMap);
			}
		}
		
		return outputVo;
	}
	
	/**設定屬性歸類(中文)**/
	public String doSetItemK(GenericMap fpGmap , GenericMap dftSetGmap){
		//若ITEM_K沒值，預設帶空白字串
		if(fpGmap.isStringBank(ITEM_K)){
			return "";
		}
		//沒預設值K，取第一個ITEM_K的值(逗號區隔)
		else if(dftSetGmap.isNull("K")){
			return fpGmap.getNotNullStr(ITEM_K).split(",")[0];
		}
		
		//有預設值k，且此值在ITEM_K中
		int itemindexK = fpGmap.getNotNullStr(ITEM_K).indexOf(dftSetGmap.getNotNullStr("K"));
		
		//有預設值k，且此值在ITEM_K中抓預設值k，若不在取第一個ITEM_K的值
		return itemindexK != -1 ? dftSetGmap.getNotNullStr("K") : fpGmap.getNotNullStr(ITEM_K).split(",")[0];
	}
	
	public Map doSetItemA(GenericMap fpGmap , String tempPremTerm){
		return doSetItem(fpGmap , tempPremTerm , ITEM_A , "LIST_A");
	}
	
	public Map doSetItemY(GenericMap fpGmap , String tempPremTerm){
		return doSetItem(fpGmap , tempPremTerm , ITEM_Y , "LIST_Y");
	}
	
	public Map doSetItem(GenericMap fpGmap , String tempPremTerm , String itemType , String listType){
		return doSetItem(fpGmap , tempPremTerm , itemType , listType , true);
	}
	
	public Map doSetItem(GenericMap fpGmap , String tempPremTerm , String itemType , String listType , boolean isChkMouse){
		Map lstInsDetail = new HashMap();
		Integer idx = null;
		
		//沒ITEM，抓預設值參數
		if(fpGmap.isStringBank(itemType)){
			lstInsDetail.put("term", tempPremTerm);
			lstInsDetail.put("tempDesc", "");
			return lstInsDetail;
		}
		
		//ITEM內容
		String itemTypeStr = fpGmap.getNotNullStr(itemType);
		//ITEM切割逗號後的陣列
		String itemTypeStrAr[] = StringUtils.isNotBlank(itemTypeStr) ? itemTypeStr.split(",") : null;
		//LIST內容
		String listTypeStr = fpGmap.getNotNullStr(listType);
		//LIST切割逗號後的陣列
		String listTypeStrAr[] = StringUtils.isNotBlank(listTypeStr) ? listTypeStr.split(",") : null;
		
		//如果只有一個值
		if(itemTypeStrAr.length == 1){
			lstInsDetail.put("term", itemTypeStr.split(",")[0]);
			
			if(!ArrayUtils.isEmpty(listTypeStrAr)){
				lstInsDetail.put("tempDesc", listTypeStrAr[0]);	
			}
			
			return lstInsDetail;
		}
		
		//超過一個值的比對
		//取相符或是最接近的對應值，取出其對應的值與索引
		GenericMap itemGmap = new GenericMap(InsjlbUtils.findSimilarIntVal(itemTypeStr , tempPremTerm , "," , isChkMouse));
		lstInsDetail.put("term" , itemGmap.getNotNullStr("item"));
		lstInsDetail.put("tempDesc", listTypeStrAr[itemGmap.getBigDecimal("idx").intValue()]);
		
		return lstInsDetail;
	}


	public GetCoverage01ServiceInf getGetCoverage01Service() {
		return getCoverage01Service;
	}


	public void setGetCoverage01Service(GetCoverage01ServiceInf getCoverage01Service) {
		this.getCoverage01Service = getCoverage01Service;
	}
}
