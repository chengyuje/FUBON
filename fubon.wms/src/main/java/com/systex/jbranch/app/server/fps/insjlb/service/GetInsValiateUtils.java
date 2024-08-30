package com.systex.jbranch.app.server.fps.insjlb.service;

import static com.systex.jbranch.app.server.fps.insjlb.chk.ChkLstInsData.COVERYEAR_SEL_CHK;
import static com.systex.jbranch.app.server.fps.insjlb.chk.ChkLstInsData.INSURED_AGE_CHK;
import static com.systex.jbranch.app.server.fps.insjlb.chk.ChkLstInsData.KIND_SEL_CHK;
import static com.systex.jbranch.app.server.fps.insjlb.chk.ChkLstInsData.OBJECT_CHK;
import static com.systex.jbranch.app.server.fps.insjlb.chk.ChkLstInsData.PAYMENTYEAR_SEL_CHK;
import static com.systex.jbranch.app.server.fps.insjlb.chk.ChkLstInsData.PLAN_CHK;
import static com.systex.jbranch.app.server.fps.insjlb.chk.ChkLstInsData.PRD_NAME_CHK;
import static com.systex.jbranch.app.server.fps.insjlb.chk.ChkLstInsData.UNIT_CHK;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;

import com.systex.jbranch.app.server.fps.insjlb.InsjlbUtils;
import com.systex.jbranch.app.server.fps.insjlb.chk.ChkLstInsData;
import com.systex.jbranch.app.server.fps.insjlb.vo.DoGetCoverage01InputVO;
import com.systex.jbranch.app.server.fps.insjlb.vo.DoGetCoverage03InputVO;
import com.systex.jbranch.app.server.fps.insjlb.vo.GetInsPdfInputVO;
import com.systex.jbranch.app.server.fps.insjlb.vo.GetOdItemListInputVO;
import com.systex.jbranch.app.server.fps.insjlb.vo.ThirdInsProdInputVO;
import com.systex.jbranch.comutil.collection.GenericMap;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;

@SuppressWarnings({ "unchecked" , "rawtypes"})
public class GetInsValiateUtils extends FubonGetInsValiateUtils{
	private static Logger logger = org.slf4j.LoggerFactory.getLogger(GetInsValiateUtils.class);
	public static final String IN_BUY = "1";
	public static final String OUT_BUY = "2";
	
	public static void validate(DoGetCoverage01InputVO inputVo) throws JBranchException{
		//判斷是否有保單資訊
		if(inputVo.getLstInsDetail() == null){
			throw new JBranchException("lstInsDetail is empty");
		}
	}
	
	public static List validate(DoGetCoverage03InputVO inputVo) throws JBranchException{
		//如果沒有客戶Id拋錯
		if(StringUtils.isBlank(inputVo.getInsCustID())) {
			throw new APException("insCustID is empty" , InsjlbUtils.createErrorMsgList("insCustID"));
		}
		//如果沒有客戶保單資訊拋錯
		else if(CollectionUtils.isEmpty(inputVo.getLstInsData())){
			throw new JBranchException("LstInsData is empty");
		}
		
		logger.info("check LstInsData[" + ObjectUtils.toString(inputVo.getInsCustID()) + "] dataMap :\r\n " + inputVo.getLstInsData());
		return chkLstInsData(inputVo.getLstInsData());
	}
	
	/**檢查保單資訊**/
	public static List chkLstInsData(List<Map> lstInsDataList) throws JBranchException{
		List lstLogTable = new ArrayList();//錯誤資訊
		
		//行內檢核
		ChkLstInsData[] inChk = new ChkLstInsData[]{
			PRD_NAME_CHK , //檢查 產品代碼
			KIND_SEL_CHK , //檢查 ITEM_K (型別)
			PLAN_CHK , //檢查 ITEM_P (計畫) 
			UNIT_CHK , //檢查 ITEM_U (單位)
			OBJECT_CHK , //檢查 LIST_O (對象)
			INSURED_AGE_CHK //檢計算＆檢查投保年齡
		};
		
		//行外檢核
		ChkLstInsData[] outChk = new ChkLstInsData[]{
			PRD_NAME_CHK , //檢查 產品代碼
			PAYMENTYEAR_SEL_CHK , //檢查 ITEM_Y (繳費年期)
			COVERYEAR_SEL_CHK , //檢查 ITEM_A (保障年期)
			KIND_SEL_CHK , //檢查 ITEM_K (型別)
			PLAN_CHK , //檢查 ITEM_P (計畫) 
			UNIT_CHK , //檢查 ITEM_U (單位)
			OBJECT_CHK , //檢查 LIST_O (對象)
			INSURED_AGE_CHK //檢計算＆檢查投保年齡
		};
		
		//逐筆檢查
		lstInsdataForEach:
		for(Map<String , Object> lstInsData : lstInsDataList){
			GenericMap lstInsDataGenMap = new GenericMap(lstInsData);
			String inOutType = lstInsDataGenMap.getNotNullStr("INOUT");
			Map<String, Object> errorMsgMap = null;
			ChkLstInsData[] chkDataAr = null;
			
			chkDataAr = IN_BUY.equals(inOutType) ? inChk : outChk;
			
			//檢核LstInsData
			for(ChkLstInsData checkItem : chkDataAr){
				if((errorMsgMap = checkItem.chkItem(new GenericMap(lstInsData))) != null){
					lstLogTable.add(errorMsgMap);
					
					//檢查產品代碼，若連產品代碼都無法辨識則無法進行後續檢核
					if(ChkLstInsData.PRD_NAME.equals(checkItem)){
						continue lstInsdataForEach;
					}
				}
			}
		}
		
		return lstLogTable;
	}
	
	/**檢核保險百科參數<br>
	 * ___ PdfType：檔案種類不可為空<br>
	 * ___ LstInsProd：險種清單不可為空<br>
	 */
	public static void validate(GetInsPdfInputVO inputVo) throws APException{
		//檔案種類不可為空
		if(StringUtils.isBlank(inputVo.getPdfType())){
			throw new APException("pdfType is empty" , InsjlbUtils.createErrorMsgList("input.pdfType"));
		}
		//險種清單不可為空
		else if(CollectionUtils.isEmpty(inputVo.getLstInsProd())){
			throw new APException("lstInsProd is empty" , InsjlbUtils.createErrorMsgList("input.lstInsProd"));
		}
	}
	
	public static void validate(GetOdItemListInputVO inputVo) throws APException{
		if(StringUtils.isBlank(inputVo.getCustId())){
			throw new APException("lstCustID is empty" , InsjlbUtils.createErrorMsgList("lstCustID"));
		}
		else if(StringUtils.isEmpty(inputVo.getPlanTypes())){
			throw new APException("planTypes is empty" , InsjlbUtils.createErrorMsgList("planTypes"));
		}
	}
	
	public static void validate(ThirdInsProdInputVO inputVO) throws JBranchException{
		// 檢核如果沒有輸入產品編號，保險公司及產品類別為必輸
		if(StringUtils.isBlank(inputVO.getProdName())){
			if(StringUtils.isBlank(inputVO.getInsCO())){
				throw new APException("insCO is empty", InsjlbUtils.createErrorMsgList("insCO"));
			}
			else if(StringUtils.isBlank(inputVO.getQid())){
				throw new APException("qId is emtpy", InsjlbUtils.createErrorMsgList("qId"));
			}
		}
	}
}
