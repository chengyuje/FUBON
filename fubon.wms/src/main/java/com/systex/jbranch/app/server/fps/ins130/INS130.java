package com.systex.jbranch.app.server.fps.ins130;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.ibm.icu.text.SimpleDateFormat;
import com.systex.jbranch.app.common.fps.table.TBINS_CUST_FAMILY_DATAVO;
import com.systex.jbranch.app.server.fps.ins.parse.WSMappingParserUtils;
import com.systex.jbranch.app.server.fps.ins400.INS400;
import com.systex.jbranch.app.server.fps.ins810.INS810;
import com.systex.jbranch.app.server.fps.ins810.INS810InputVO;
import com.systex.jbranch.app.server.fps.ins810.INS810OutputVO;
import com.systex.jbranch.comutil.collection.GenericMap;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;


/**
 * ins130
 * 
 * @author James
 * @date 2017/08/09
 * @spec null
 */
@Component("ins130")
@Scope("request")
public class INS130 extends FubonWmsBizLogic {
	
	private DataAccessManager dam = null;
	
	//======================== WEB 調用 ========================	
	//取得客戶基本資料和家庭財務安全問卷資料
	public void queryData (Object body, IPrimitiveMap header) throws JBranchException, ParseException {
		INS130InputVO inputVO = (INS130InputVO) body;
		INS130OutputVO outputVO = new INS130OutputVO();
		
		List<Map<String, Object>> custMastlist = getCustMastlist(inputVO.getCustId());
		List<Map<String, Object>> familyFinlist = getFamilyFinlist(inputVO.getCustId());		
		outputVO.setCustMastlist(custMastlist);			
		outputVO.setFamilyFinlist(familyFinlist);
				
		this.sendRtnObject(outputVO);
	}
	
	public void saveDataFromINS431 (Object body, IPrimitiveMap header) throws JBranchException, Exception {
		INS130InputVO inputVO = (INS130InputVO) body;
		INS130OutputVO outputVO = new INS130OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = null;
		
		//透過被保人ID (CUST_ID)判斷該客戶是否填寫過家庭財務安全問卷
		TBINS_CUST_FAMILY_DATAVO vo = new TBINS_CUST_FAMILY_DATAVO();
		vo = (TBINS_CUST_FAMILY_DATAVO) dam.findByPKey(TBINS_CUST_FAMILY_DATAVO.TABLE_UID, inputVO.getCustId());
		if (null == vo) {
			//如果該客戶沒有填寫過，則INSERT
			TBINS_CUST_FAMILY_DATAVO saveDataVo = new TBINS_CUST_FAMILY_DATAVO();
			saveDataVo.setCUST_ID(inputVO.getCustId());			
			saveDataVo.setEDU_AMT(inputVO.getEduAmt());					
			dam.create(saveDataVo);
		} else {
			//如果該客戶有填寫過，如有修正則UPDATE	
			vo.setEDU_AMT(inputVO.getEduAmt());			
			dam.update(vo);
		}
		this.sendRtnObject(outputVO);
	}

	//取得特定PDF檔案
	public void getPdfFile(Object body, IPrimitiveMap header) throws JBranchException {
		INS130InputVO inputVO = (INS130InputVO) body;
		INS130OutputVO return_VO = new INS130OutputVO();

		List<Map<String, Object>> list = getPdfFileSql(inputVO.getParaType());
		return_VO.setReportList(list);
			
		this.sendRtnObject(return_VO);
	}
	
	//取得特定PDF檔案的sql
	public List<Map<String, Object>> getPdfFileSql(String paraType) throws JBranchException {
		dam = this.getDataAccessManager();
		
		// TBINS_PARA_HEADER and TBINS_REPORT
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT A.PARA_TYPE, A.EFFECT_DATE, A.EXPIRY_DATE, A.STATUS,B.KEYNO, ");
		sql.append("B.FILE_NAME, B.REPORT_FILE, B.CREATOR ");
		sql.append("FROM TBINS_PARA_HEADER A, ");
		sql.append("TBINS_REPORT B ");
		sql.append("WHERE TO_CHAR(A.PARA_NO) = B.PLAN_ID ");
		sql.append("AND  (A.PARA_TYPE, A.EFFECT_DATE) IN ( ");
		sql.append("SELECT PARA_TYPE, MAX(EFFECT_DATE) ");
		sql.append("FROM TBINS_PARA_HEADER ");
		sql.append("WHERE EFFECT_DATE < SYSDATE ");
		sql.append("AND NVL(EXPIRY_DATE, SYSDATE) >= SYSDATE ");
		sql.append("AND PARA_TYPE IN ('A', 'B', 'C') ");
		sql.append("AND STATUS = 'A' ");
		sql.append("GROUP BY PARA_TYPE) ");
		sql.append("AND A.PARA_TYPE = :paraType ");
		sql.append("Order by A.PARA_TYPE ");		
		queryCondition.setQueryString(sql.toString());
		queryCondition.setObject("paraType", paraType);
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		
		return list;		
	}
	
	//儲存家庭財務安全試算資料
	public void saveData (Object body, IPrimitiveMap header) throws JBranchException, Exception {
		INS130InputVO inputVO = (INS130InputVO) body;
		INS130OutputVO outputVO = new INS130OutputVO();
		
		saveOrUpdateFinancialData(inputVO);

		this.sendRtnObject(outputVO);
	}
	
	//======================== WS 調用 ========================	
	/**
     * 提供給 APP 使用的 API <br>
     * <code> <b> 2.5.3 </b> 取得家庭財務安全試算資料</code>
     * @param inputVO : custId : 身分證字號
     * @return Object : info : 前次儲存之試算資料
     * @throws JBranchException : <br>
     *                <p>
     *                             <b>客製訊息如下 :</b>
     *                             <li>查無資料: </li>
     *                </p>
     */
	public void getFinancialTrialInfo (Object body, IPrimitiveMap header) throws JBranchException, ParseException {
		
		GenericMap inputGmap = new GenericMap((Map) body);
		//取得外界傳來的custId
		String custId = inputGmap.getNotNullStr("custId");
		//其他方法回傳的map
		Map inputMap = new HashMap(); 
		//存放家庭財務安全試算資料的map
		Map familyFinMap = new HashMap(); 
				
		/**家庭財務安全試算**/
		inputMap = mobileQueryData(custId);
		//info : 前次儲存之試算資料
		familyFinMap.put("info", inputMap);
		
		this.sendRtnObject(familyFinMap);
	}
	
	/**
     * 提供給 APP 使用的 API <br>
     * <code> <b> 2.5.11 </b> 儲存家庭財務安全試算資料</code>
     * @param inputVO : custId : 身分證字號, info : 前次儲存之試算資料
     * @return Object : null
     * @throws JBranchException : <br>
     *                <p>
     *                             <b>客製訊息如下 :</b>
     *                             <li>查無資料: </li>
     *                </p>
     */
	public void saveFinancialTrialInfo(Object body, IPrimitiveMap header) throws JBranchException, Exception {		
		doSaveFinancial(body);
		this.sendRtnObject(null);
	}
	
	//======================== WS 處理========================
	//ws用取得客戶基本資料和家庭財務安全問卷資料
	public Map mobileQueryData (String custId) throws JBranchException, ParseException {			
		//回傳的map
		Map outputMap = new HashMap(); 					
		//客戶基本資料
		List<Map<String, Object>> custMastlist = getCustMastlist(custId);
		//家庭財務安全問卷
		List<Map<String, Object>> familyFinlist = getFamilyFinlist(custId);			
		// 日期轉換
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd"); 
			
		//hasRel : TODO
		outputMap.put("hasRel", custMastlist.get(0).get("HAS_REL"));
		//如果客戶基本資料存在
		if(custMastlist.size() > 0){				
			//hasRel為TRUE，顯示為有審核過的關係戶 TODO true
			if((Boolean) custMastlist.get(0).get("HAS_REL")){
				//partnerName : 配偶姓名
				outputMap.put("partnerName", custMastlist.get(0).get("REL_NAME"));
				//partnerBirthDate : 配偶生日
				outputMap.put("partnerBirthDate", custMastlist.get(0).get("REL_BIRTH") != null
						? sdf.format(custMastlist.get(0).get("REL_BIRTH")) : "");								
				//partnerGender : 配偶性別
				outputMap.put("partnerGender", custMastlist.get(0).get("REL_GENDER"));
				
				if(familyFinlist.size() == 0){
					/**支出和收入的資料(預設值)**/	
					outputMap = defaultFinancialTrialInfo(outputMap);
				}
			}else{
				if(familyFinlist.size() == 0){
					/**關係戶資料**/	
					//partnerName : 配偶姓名
					outputMap.put("partnerName", "");
					//partnerBirthDate : 配偶生日
					outputMap.put("partnerBirthDate", "");								
					//partnerGender : 配偶性別
					outputMap.put("partnerGender", "");
					
					/**支出和收入的資料(預設值)**/	
					outputMap = defaultFinancialTrialInfo(outputMap);
				}
			}
		}
		
		//如果家庭財務安全問卷存在
		if(familyFinlist.size() > 0){
			//hasRel為TRUE，顯示為無審核過的關係戶 TODO false
			if(!(Boolean) custMastlist.get(0).get("HAS_REL")){
				outputMap = WSMappingParserUtils.dataMappingByColumn(familyFinlist.get(0), 
						new String[]{"PARTNER_NAME", "PARTNER_BIRTH_DATE", "PARTNER_GENDER"});	
				outputMap.put("hasRel", custMastlist.get(0).get("HAS_REL"));
			}
				
			/**處理支出的資料**/			
			//expense : 支出
			outputMap.put("expense", WSMappingParserUtils.dataMappingByColumn(familyFinlist.get(0), 
					new String[]{"HOU_DEBT_AMT","HOU_DEBT_Y","EDU_AMT","CAR_DEBT_AMT","CAR_DEBT_Y","CARD_DEBT_AMT","CARD_DEBT_Y","OTHER_DEBT_AMT","LIVING_EXP","NOTLIVING_EXP","CUST_LIVING_FEE","CHILD_LIVING_FEE","TAX_IN"}));
				
			/**處理收入的資料**/	
			//revenue : 收入
			outputMap.put("revenue", WSMappingParserUtils.dataMappingByColumn(familyFinlist.get(0), 
					new String[]{"INCOME","COU_INCOME","RENT_INCOME","CASH_AMT","STOCK_AMT","CT_AMT","FUND_AMT","SN_AMT","INVEST_INS_AMT","INVEST_IMMOVE_AMT","TRUST_AMT"}));
		}
			
		/**子女教育所需基金細項**/
		INS400 ins400 = (INS400) PlatformContext.getBean("ins400");
		List<Map<String, Object>> eduExplist = ins400.inquireChildDataForMobile(custId);
		//etuDetail : 子女教育所需基金細項
		outputMap.put("etuDetail", eduExplist);
					
		return outputMap;
	}
		
	//ws將傳入的map轉成inputVO
	public void doSaveFinancial(Object body) throws Exception {
		GenericMap inputGmap = new GenericMap((Map) body);
		//取得外界傳來的custId
		String custId = inputGmap.getNotNullStr("custId");
		//取得取得家庭財務安全試算資料
		Map info = inputGmap.get("info");
		//取得取得家庭財務安全試算資料中的支出
		Map expense = (Map) info.get("expense");
		//取得取得家庭財務安全試算資料中的收入
		Map revenue = (Map) info.get("revenue");
		//取得取得家庭財務安全試算資料中的子女教育所需基金細項(傳進來的資料)
		List<Map<String, Object>> etuDetail = (List<Map<String, Object>>)info.get("etuDetail");
			
		INS130InputVO inputVO = null;
		INS130OutputVO outputVO = new INS130OutputVO();
			
		/**儲存子女教育所需基金細項**/
		INS400 ins400 = (INS400) PlatformContext.getBean("ins400");
		ins400.saveChildDataForMobile(custId,etuDetail);
			
		// 日期轉換
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			
		//將細項的map移除
		info.remove("etuDetail");
		info.remove("expense");
		info.remove("revenue");
			
		//將細項內的entry加入map中
		info.putAll(expense);
		info.putAll(revenue);
			
		//將map中日期的字串轉成date
		info.put("partnerBirthDate", sdf.parse((String) info.get("partnerBirthDate")));
			
		//將map轉成相對應的inputVO
		inputVO = (INS130InputVO)WSMappingParserUtils.mapToPojo(info, INS130InputVO.class);
		inputVO.setCustId(custId);
			
		saveOrUpdateFinancialData(inputVO);				
	}
	
	//家庭財務安全試算的預設值
	public Map<String,Object> defaultFinancialTrialInfo(Map<String,Object> outputMap){
		/**處理支出的資料**/			
		//expense : 支出(預設為空值)
		outputMap.put("expense", WSMappingParserUtils.allKeysTheSameDefaultValueWithLowerCamel(
				new String[]{"HOU_DEBT_AMT","HOU_DEBT_Y","EDU_AMT","CAR_DEBT_AMT","CAR_DEBT_Y","CARD_DEBT_AMT","CARD_DEBT_Y","OTHER_DEBT_AMT","LIVING_EXP","NOTLIVING_EXP","CUST_LIVING_FEE","CHILD_LIVING_FEE","TAX_IN"}, 0));
		
		/**處理收入的資料**/	
		//revenue : 收入(預設為空值)
		outputMap.put("revenue", WSMappingParserUtils.allKeysTheSameDefaultValueWithLowerCamel(
				new String[]{"INCOME","COU_INCOME","RENT_INCOME","CASH_AMT","STOCK_AMT","CT_AMT","FUND_AMT","SN_AMT","INVEST_INS_AMT","SELF_IMMOVE_AMT","INVEST_IMMOVE_AMT","TRUST_AMT"}, 0));
		
		return outputMap;		
	}
		
	//======================== 共用 調用 & 處理========================
	//取得客戶基本資料
	public List<Map<String, Object>> getCustMastlist (String custId) throws JBranchException, ParseException{
		
		dam = this.getDataAccessManager();
		
		List<Map<String, Object>> relationList = relationList(custId);
		
		List<Map<String, Object>> custMastlist = new ArrayList<Map<String,Object>>();
			
		if(relationList.size() == 0) {
			custMastlist = getPersionalList(dam, custId);
		} else {
			custMastlist = relationList;
		}
		return custMastlist;
	}
	
	//取得家庭財務安全問卷資料
	public List<Map<String, Object>> getFamilyFinlist (String custId) throws JBranchException, ParseException {
		
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = null;
		StringBuilder sb2 = null;
		StringBuilder sb3 = null;
		
		List<Map<String, Object>> familyFinlist = new ArrayList<Map<String,Object>>();
				
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb2 = new StringBuilder();
		sb2.append(" SELECT PARTNER_NAME, PARTNER_BIRTH_DATE, PARTNER_GENDER ");
		sb2.append(" ,LIVING_EXP,NOTLIVING_EXP,CUST_LIVING_FEE,CHILD_LIVING_FEE,HOU_DEBT_AMT,HOU_DEBT_Y ");
		sb2.append(" ,EDU_AMT,CAR_DEBT_AMT,CAR_DEBT_Y,CARD_DEBT_AMT,CARD_DEBT_Y,OTHER_DEBT_AMT ");
		sb2.append(" ,TAX_IN,INCOME,COU_INCOME,RENT_INCOME,CASH_AMT,STOCK_AMT,CT_AMT,FUND_AMT,SN_AMT,INVEST_INS_AMT ");
		sb2.append(" ,SELF_IMMOVE_AMT,INVEST_IMMOVE_AMT,TRUST_AMT ");						
		sb2.append(" FROM TBINS_CUST_FAMILY_DATA ");
		sb2.append(" WHERE CUST_ID = :custId ");
		queryCondition.setObject("custId", custId);
		queryCondition.setQueryString(sb2.toString());
		familyFinlist = dam.exeQuery(queryCondition);
		
		if(familyFinlist != null && familyFinlist.size()>0) {
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb3 = new StringBuilder();
			sb3.append(" SELECT COUNT(1) SPD_COUNT FROM TBINS_SPPEDU_DETAIL ");
			sb3.append(" WHERE CUST_ID = :custId ");
			queryCondition.setObject("custId", custId);
			queryCondition.setQueryString(sb3.toString());
			List<Map<String, Object>> sppeduDetailList = dam.exeQuery(queryCondition);
			if(sppeduDetailList != null && sppeduDetailList.size()>0) {
				String spdCount = ObjectUtils.toString(sppeduDetailList.get(0).get("SPD_COUNT"));
				(familyFinlist.get(0)).put("SPD_COUNT", spdCount);
			}
		}
		return familyFinlist;
	}
	
	public List<Map<String, Object>> getPersionalList(DataAccessManager dam, String custId) throws DAOException, JBranchException, ParseException{
		List<Map<String, Object>> fromList = null;
		fromList = persionalFromCRMList(dam, custId);
		if(!(fromList != null && fromList.size() >0)) {
			fromList = persionalFromINSList(dam, custId);
		}
		return fromList;
	}
	
	//新增或更新家庭財務安全試算資料
	public void saveOrUpdateFinancialData(INS130InputVO inputVO) throws JBranchException{
		
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = null;
		
		//透過被保人ID (CUST_ID)判斷該客戶是否填寫過家庭財務安全問卷
		TBINS_CUST_FAMILY_DATAVO vo = null;
		vo = (TBINS_CUST_FAMILY_DATAVO) dam.findByPKey(TBINS_CUST_FAMILY_DATAVO.TABLE_UID, inputVO.getCustId());
		if (null == vo) {
			vo = new TBINS_CUST_FAMILY_DATAVO();;
			vo.setCUST_ID(inputVO.getCustId());
			saveOrUpdateData(vo, inputVO);
			dam.create(vo);
		} else {
			saveOrUpdateData(vo, inputVO);
			dam.update(vo);
		}
	}
	
	//用來儲存或更新家庭財務安全試算資料的VO
	public TBINS_CUST_FAMILY_DATAVO saveOrUpdateData(TBINS_CUST_FAMILY_DATAVO vo,INS130InputVO inputVO) throws JBranchException{
		
		vo.setPARTNER_NAME(inputVO.getPartnerName());
		if(org.springframework.util.StringUtils.isEmpty(inputVO.getPartnerBirthDate())) {
			vo.setPARTNER_BIRTH_DATE(null);
		} else {
			vo.setPARTNER_BIRTH_DATE(new Timestamp(inputVO.getPartnerBirthDate().getTime()));
		}
		vo.setPARTNER_GENDER(inputVO.getPartnerGender());
		vo.setLIVING_EXP(inputVO.getLivingExp());
		vo.setNOTLIVING_EXP(inputVO.getNotlivingExp());
		vo.setCUST_LIVING_FEE(inputVO.getCustLivingFee());
		vo.setCHILD_LIVING_FEE(inputVO.getChildLivingFee());
		vo.setHOU_DEBT_AMT(inputVO.getHouDebtAmt());
		vo.setHOU_DEBT_Y(inputVO.getHouDebtY());
		vo.setEDU_AMT(inputVO.getEduAmt());
		vo.setCAR_DEBT_AMT(inputVO.getCarDebtAmt());
		vo.setCAR_DEBT_Y(inputVO.getCarDebtY());
		vo.setCARD_DEBT_AMT(inputVO.getCardDebtAmt());
		vo.setCARD_DEBT_Y(inputVO.getCardDebtY());
//		vo.setSTOCK_DEBT_AMT(inputVO.getStockDebtAmt());
		vo.setOTHER_DEBT_AMT(inputVO.getOtherDebtAmt());
		vo.setTAX_IN(inputVO.getTaxIn());
		vo.setINCOME(inputVO.getIncome());
		vo.setCOU_INCOME(inputVO.getCouIncome());
		vo.setRENT_INCOME(inputVO.getRentIncome());
		vo.setCASH_AMT(inputVO.getCashAmt());
		vo.setSTOCK_AMT(inputVO.getStockAmt());
		vo.setCT_AMT(inputVO.getCtAmt());
		vo.setFUND_AMT(inputVO.getFundAmt());
		vo.setSN_AMT(inputVO.getSnAmt());
		vo.setINVEST_INS_AMT(inputVO.getInvestInsAmt());
		vo.setSELF_IMMOVE_AMT(inputVO.getSelfImmoveAmt());
		vo.setINVEST_IMMOVE_AMT(inputVO.getInvestImmoveAmt());
		vo.setTRUST_AMT(inputVO.getTrustAmt());	
		return vo;
	}
	
	private List<Map<String, Object>> relationList(String custId) throws JBranchException, ParseException {
		INS810InputVO ins810inputVO = new INS810InputVO();
		INS810OutputVO ins810outputVO = new INS810OutputVO();
		INS810 ins810 = (INS810) PlatformContext.getBean("ins810");
		ArrayList<String> loginAO = (ArrayList<String>) getUserVariable(FubonSystemVariableConsts.LOGIN_AOCODE_LIST);
		String LoginBraNbr = (String) getCommonVariable(SystemVariableConsts.LOGINBRH);
		
		ins810inputVO.setCUST_ID(custId);
		ins810inputVO.setLoginAOCode(loginAO);
		ins810inputVO.setLoginBranch(LoginBraNbr);
		ins810outputVO = ins810.getFamailyLst(ins810inputVO);
		
		boolean hasRelation = false;
		List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
		if(ins810outputVO.getGenealogyList().size()>0) {
			Map<String, Object> resultMap = new HashMap<String, Object>();
			for(Map<String, Object> genealogyMap : ins810outputVO.getGenealogyList()) {
				if("00".equals((String)genealogyMap.get("RELATION_TYPE"))){
					resultMap.put("CUST_ID", genealogyMap.get("CUST_ID"));
					resultMap.put("CUST_NAME", genealogyMap.get("RELATION_NAME"));
					resultMap.put("AGE", getAge((Date)genealogyMap.get("RELATION_BIRTHDAY")));
					resultMap.put("BIRTH_DATE", genealogyMap.get("RELATION_BIRTHDAY"));
					resultMap.put("GENDER", genealogyMap.get("RELATION_GENDER"));
				} else if("07".equals((String)genealogyMap.get("RELATION_TYPE"))) {
					resultMap.put("REL_NAME", genealogyMap.get("RELATION_NAME"));
					resultMap.put("REL_BIRTH", genealogyMap.get("RELATION_BIRTHDAY"));
					resultMap.put("REL_GENDER", genealogyMap.get("RELATION_GENDER"));
					hasRelation = true;
				}
			}
			resultMap.put("HAS_REL", hasRelation);
			resultList.add(resultMap);
		}
		return resultList;
	}
	
	public List<Map<String, Object>> persionalFromCRMList(DataAccessManager dam, String custId) throws DAOException, JBranchException {
		QueryConditionIF queryCondition = null;
		StringBuilder sb1 = null;
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb1 = new StringBuilder();
		sb1.append(" SELECT CUST_ID, CUST_NAME, AGE, GENDER, BIRTH_DATE");
		sb1.append(" FROM TBCRM_CUST_MAST ");
		sb1.append(" WHERE CUST_ID = :custId ");
		queryCondition.setObject("custId", custId);
		queryCondition.setQueryString(sb1.toString());
		List resultList = dam.exeQuery(queryCondition);
		if(resultList != null && resultList.size()>0) {
			((Map<String, Object>)resultList.get(0)).put("HAS_REL",false);
		}
		return resultList;
	}
	
	public List<Map<String, Object>> persionalFromINSList(DataAccessManager dam, String custId) throws DAOException, JBranchException, ParseException {
		QueryConditionIF queryCondition = null;
		StringBuilder sb1 = null;
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb1 = new StringBuilder();
		sb1.append(" SELECT CUST_ID, CUST_NAME, GENDER, BIRTH_DATE");
		sb1.append(" FROM TBINS_CUST_MAST ");
		sb1.append(" WHERE CUST_ID = :custId ");
		queryCondition.setObject("custId", custId);
		queryCondition.setQueryString(sb1.toString());
		List resultList = dam.exeQuery(queryCondition);
		if(resultList != null && resultList.size()>0) {
			Map<String, Object> resultMap = (Map<String, Object>)resultList.get(0);
			resultMap.put("AGE", getAge((Date)resultMap.get("BIRTH_DATE")));
			resultMap.put("HAS_REL",false);
		}
		return resultList;
	}
	
	private int getAge(Date birthDay) {
	    Calendar cal = Calendar.getInstance();  
	    if (cal.before(birthDay)) {  
	        return 0;  
	    }  
	  
	    int yearNow = cal.get(Calendar.YEAR);  
	    int monthNow = cal.get(Calendar.MONTH);  
	    int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);  
	    cal.setTime(birthDay);  
	  
	    int yearBirth = cal.get(Calendar.YEAR);  
	    int monthBirth = cal.get(Calendar.MONTH);  
	    int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);  
	  
	    int age = yearNow - yearBirth;  
	  
	    if (monthNow <= monthBirth) {  
	        if (monthNow == monthBirth) {  
	            if (dayOfMonthNow < dayOfMonthBirth) {  
	                age--;  
	            }  
	        } else {  
	            age--;  
	        }  
	    }  
	  
	    return age;  
	}
}