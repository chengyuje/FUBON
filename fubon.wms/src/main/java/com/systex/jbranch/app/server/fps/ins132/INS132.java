package com.systex.jbranch.app.server.fps.ins132;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;
import com.systex.jbranch.app.server.fps.ins130.INS130;
import com.systex.jbranch.app.server.fps.ins810.FubonIns810;
import com.systex.jbranch.app.server.fps.ins810.INS810InputVO;
import com.systex.jbranch.app.server.fps.insjlb.FubonInsjlb;
import com.systex.jbranch.app.server.fps.insjlb.vo.CalFamilyGapInputVO;
import com.systex.jbranch.app.server.fps.insjlb.vo.CalFamilyGapOutputVO;
import com.systex.jbranch.comutil.collection.GenericMap;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

//import flex.messaging.io.ArrayList;

/**
 * ins132
 * 
 * @author Sen
 * @date 2018/02/26
 */
@Component("ins132")
@Scope("request")
public class INS132  extends FubonWmsBizLogic {
	
	//======================== spring autowire ========================
	@Autowired @Qualifier("insjlb")
	private FubonInsjlb insjlb;
	
	@Autowired @Qualifier("ins810")
	private FubonIns810 ins810;

	@Autowired @Qualifier("ins130")
	private INS130 ins130;

	public FubonInsjlb getInsjlb() {
		return insjlb;
	}

	public void setInsjlb(FubonInsjlb insjlb) {
		this.insjlb = insjlb;
	}
	
	public FubonIns810 getIns810() {
		return ins810;
	}

	public void setIns810(FubonIns810 ins810) {
		this.ins810 = ins810;
	}
	
	public INS130 getIns130() {
		return ins130;
	}

	public void setIns130(INS130 ins130) {
		this.ins130 = ins130;
	}

	//======================== WEB 調用 ========================
	/**
	 * 調用 JLB 取得資訊源資料
	 * @param body
	 * @param header
	 * @throws JBranchException
	 * @throws ParseException
	 */
	public void queryData(Object body, IPrimitiveMap header) throws JBranchException, ParseException {
		INS132InputVO inputVO = (INS132InputVO)body;
		CalFamilyGapInputVO paramsVO = prepareGapInputVO(inputVO);
		CalFamilyGapOutputVO calFamilyGapVO =  getCalFamilyGapVo(paramsVO);
		int age = getAge(inputVO.getCustBirth());
		sendRtnObject(new Object[]{age, calFamilyGapVO});
	}
	
	//======================== WS 調用 ========================
	/**
	 * ws 調用 取得家庭財務安全試算後的結果
	 * 調用前要存檔存檔存檔 [其實一個服務功能不該做兩件事....]
	 * @param body
	 * @param header
	 * @throws Exception 
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void financialTrial (Object body, IPrimitiveMap header) throws Exception {
		System.out.println(new Gson().toJson((Map)body));
		int isVoObject = body instanceof INS132InputVO ? 0 :1; // 0 >> InputVO, 1 >> Map
		// 做儲存啦
		if(isVoObject == 1) {
			getIns130().doSaveFinancial(body);
		}
		
		Map<String, Object> finacialReturnMap = new HashMap<String, Object>();
		CalFamilyGapInputVO paramsVO = prepareGapInputVO(body);
		CalFamilyGapOutputVO calFamilyGapVO =  getCalFamilyGapVo(paramsVO);
		
		// result
		financialTrialResultProcess(calFamilyGapVO, finacialReturnMap);
		
		// plan
		financialTrialPlanProcess(calFamilyGapVO, finacialReturnMap);
		System.out.println(new Gson().toJson(finacialReturnMap));
		this.sendRtnObject(finacialReturnMap);
	}
	
	//======================== WS 處理 ========================
	/**
	 * 處理 要回傳的 result 部分
	 * @param calFamilyGapVO 資訊源回來的 VO
	 * @param finacialReturnMap 最後要吐給隨想的 MAP
	 */
	@SuppressWarnings("unchecked")
	private void financialTrialResultProcess(CalFamilyGapOutputVO calFamilyGapVO, Map<String, Object> finacialReturnMap) {
		List<String[]> resultList = new ArrayList();
		String[] titleKey = new String[]{"AGE","LIFE_FEE","HOUSE_FEE","EDUCATION_FEE","OTHER_FEE","TAX_IN","FEE_SUM","COU_INCOME","FIN_INCOME","ALL_ASSET","INS_AMT","INCOME_ALL","GAP_AMT"};
		for(GenericMap resultMap : (List<GenericMap>)calFamilyGapVO.getLstCashFlow()) {
			String[] resultArray = new String[13];
			for(int i=0; i<titleKey.length; i++ ) {
				resultArray[i] = String.valueOf(resultMap.getBigDecimal(titleKey[i]));
			}
			resultList.add(resultArray);
		}
		
		finacialReturnMap.put("result", resultList);
	}
	
	/**
	 * 處理 要回傳的 plan 部分
	 * @param calFamilyGapVO 資訊源回來的 VO
	 * @param finacialReturnMap 最後要吐給隨想的 MAP
	 */
	@SuppressWarnings("unchecked")
	private void financialTrialPlanProcess(CalFamilyGapOutputVO calFamilyGapVO, Map<String, Object> finacialReturnMap) throws JBranchException {
		
		 BigDecimal insFamilyGap = (BigDecimal) (calFamilyGapVO.getInsFamilyGap() != null ? calFamilyGapVO.getInsFamilyGap().multiply(new BigDecimal(10000)) : 0);
		// plan
		// 取得參數
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> pGapMap = xmlInfo.doGetVariable("INS.FAMILY_P_GAP", FormatHelper.FORMAT_2); //意外
		BigDecimal accident = insFamilyGap.add(new GenericMap(pGapMap).getBigDecimal(ObjectUtils.toString(pGapMap.keySet().toArray()[0])));

		Map<String, String> hrGapMap = xmlInfo.doGetVariable("INS.FAMILY_HR_GAP", FormatHelper.FORMAT_2); //住院醫療
		BigDecimal health = new GenericMap(hrGapMap).getBigDecimal(ObjectUtils.toString(hrGapMap.keySet().toArray()[0]));
		
		Map<String, String> dGapMap = xmlInfo.doGetVariable("INS.FAMILY_D_GAP", FormatHelper.FORMAT_2); //重大疾病
		BigDecimal disease = new GenericMap(dGapMap).getBigDecimal(ObjectUtils.toString(dGapMap.keySet().toArray()[0]));
		
		// 壽險
		Map<String, Object> lifeMap = new HashMap<String, Object>();
		lifeMap.put("prepaidAmount", calFamilyGapVO.getOldItemLife());
		lifeMap.put("securityAmount", insFamilyGap);
		
		// 意外險
		Map<String, Object> accidentMap = new HashMap<String, Object>();
		accidentMap.put("prepaidAmount", calFamilyGapVO.getOldItemAccident());
		accidentMap.put("securityAmount", accident);
		
		// 住院醫療
		Map<String, Object> healthMap = new HashMap<String, Object>();
		healthMap.put("prepaidAmount", calFamilyGapVO.getOldItemHealth());
		healthMap.put("securityAmount", health);
		
		// 重大疾病
		Map<String, Object> dreadMap = new HashMap<String, Object>();
		dreadMap.put("prepaidAmount", calFamilyGapVO.getOldItemDread());
		dreadMap.put("securityAmount", disease);
		
		Map<String, Object>[] planList = (Map<String, Object>[]) new Map[]{lifeMap, accidentMap, healthMap, dreadMap};
		
		finacialReturnMap.put("plan", planList);
	}
	//======================== 共用 調用 & 處理========================
	/**
	 * 共用統一試算入口
	 * 管你網頁還是行動攏來攏來
	 * @param body
	 * @param header
	 * @return
	 * @throws JBranchException
	 * @throws ParseException
	 */
	public CalFamilyGapOutputVO getCalFamilyGapVo(CalFamilyGapInputVO paramsVO) throws JBranchException, ParseException {
		CalFamilyGapOutputVO calFamilyGapOutputVO = getInsjlb().calFamilyGap(paramsVO);
		return calFamilyGapOutputVO;
	}
	
	/**
	 * 共用統一計算保險年齡
	 * @param birthday
	 * @return
	 * @throws JBranchException
	 */
	private int getAge(Date birthday) throws JBranchException {
		INS810InputVO ins810inputVO = new INS810InputVO();
		ins810inputVO.setBirthday(birthday);
		return getIns810().getAge(ins810inputVO).getAge();
	}
	
	/**
	 * 準備調用資訊源之前的資料準備
	 * 根據 傳入型態會自己決定 Map 可以 InputVO 也可
	 * @param inputObj
	 * @return
	 * @throws ParseException
	 * @throws JBranchException 
	 * @throws DAOException 
	 */
	private CalFamilyGapInputVO prepareGapInputVO (Object inputObj) throws ParseException, DAOException, JBranchException{
		int isVoObject = inputObj instanceof INS132InputVO ? 0 :1; // 0 >> InputVO, 1 >> Map
		
		INS132InputVO inputVO = null;
		GenericMap inputMap = null;
		List<Map<String, Object>> custList = null;
		if(isVoObject == 0) {
			inputVO = (INS132InputVO) inputObj;
		} else {
			inputMap = new GenericMap((Map) inputObj);
		}
		
		// 只有客戶 ID 所以要回去找 可能在 INS_CUST_MAST 也可能在 CRM_CUST_MAST
		if(isVoObject == 1) {
			StringBuffer sql = new StringBuffer();
			sql.append(" SELECT CUST_ID AS INSURED_ID, CUST_NAME AS CUST_NAME, BIRTH_DATE AS BIRTH_DATE, GENDER GENDER");
			sql.append(" FROM TBCRM_CUST_MAST CCM WHERE CUST_ID = :custId ");
			sql.append(" UNION ALL ");
			sql.append(" SELECT CUST_ID, CUST_NAME, BIRTH_DATE, GENDER FROM TBINS_CUST_MAST ICM ");
			sql.append(" WHERE CUST_ID = :custId AND NOT EXISTS  ");
			sql.append(" (SELECT 1 FROM TBCRM_CUST_MAST CCM WHERE CCM.CUST_ID = :custId) ");
			
			DataAccessManager dam = getDataAccessManager();
			QueryConditionIF qc = null;
			qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			qc.setObject("custId", inputMap.get("custId"));
			qc.setQueryString(sql.toString());
			custList = dam.exeQuery(qc);
		}
		
		CalFamilyGapInputVO paramsVO = new CalFamilyGapInputVO();
		paramsVO.setInsCustID(isVoObject == 0 ? inputVO.getCustID() : ObjectUtils.toString(inputMap.get("custId")));
		paramsVO.setInsCustName(isVoObject == 0 ? inputVO.getCustName() : ObjectUtils.toString(custList.get(0).get("CUST_NAME")));
		paramsVO.setInsCustGender(isVoObject == 0 ? inputVO.getCustGender() : ObjectUtils.toString(custList.get(0).get("GENDER")));
		paramsVO.setInsCustBirthday(isVoObject == 0 ? inputVO.getCustBirth() : (Date)custList.get(0).get("BIRTH_DATE"));
		
		paramsVO.setUuid(this.uuid);
		return paramsVO;
	}
	
}
