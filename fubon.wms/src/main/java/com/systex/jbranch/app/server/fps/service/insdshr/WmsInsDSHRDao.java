package com.systex.jbranch.app.server.fps.service.insdshr;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Repository;

import com.systex.jbranch.app.common.fps.table.TBIOT_PREMATCHVO;
import com.systex.jbranch.app.server.fps.iot110.IOT110;
import com.systex.jbranch.app.server.fps.iot110.IOT110InputVO;
import com.systex.jbranch.app.server.fps.iot120.IOT120;
import com.systex.jbranch.app.server.fps.iot120.IOT120InputVO;
import com.systex.jbranch.app.server.fps.iot120.IOT120OutputVO;
import com.systex.jbranch.app.server.fps.iot910.IOT910;
import com.systex.jbranch.app.server.fps.iot910.IOT910InputVO;
import com.systex.jbranch.app.server.fps.iot920.IOT920;
import com.systex.jbranch.app.server.fps.iot920.InsFundListInputVO;
import com.systex.jbranch.app.server.fps.iot920.chk_CTInputVO;
import com.systex.jbranch.app.server.fps.iot920.chk_CTOutputVO;
import com.systex.jbranch.app.server.fps.sot701.AcctVO;
import com.systex.jbranch.app.server.fps.sot701.CustAcctDataVO;
import com.systex.jbranch.app.server.fps.sot701.CustKYCDataVO;
import com.systex.jbranch.app.server.fps.sot701.FP032675DataVO;
import com.systex.jbranch.app.server.fps.sot701.SOT701;
import com.systex.jbranch.app.server.fps.sot701.SOT701InputVO;
import com.systex.jbranch.comutil.collection.GenericMap;
import com.systex.jbranch.fubon.commons.esb.vo.fp032151.FP032151OutputVO;
import com.systex.jbranch.fubon.commons.seniorValidation.SeniorValidation;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.errHandle.NotFoundException;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.server.bizLogic.BizLogic;

@Repository("WmsInsDSHRDao")
public class WmsInsDSHRDao extends BizLogic implements WmsInsDSHRDaoInf{
	//要保人KYC資料
	private CustKYCDataVO custKYCDataVO = null;
	//要保人客戶註記
	private FP032675DataVO fp032675DataVO = null;
	//要保人是否為高齡(>=64.5歲)
	private boolean isCustSenior = false;
	//主約險種資料
	List<Map<String , Object>> insPrd = new ArrayList<Map<String , Object>>();
	//要保人學歷值
	String custEdu = "";
	
	/**
	 * 人壽端回傳業管要保書暫存資料
	 * 寫入一筆資料保險購買檢核暫存資料
	 */
	public void setCaseSaveData(String caseId, String uploadDatetime, String empId, String deptId) throws Exception {
		SimpleDateFormat sdFormat = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
		IOT110 iot110 = (IOT110) PlatformContext.getBean("iot110");		
		
		TBIOT_PREMATCHVO pvo = new TBIOT_PREMATCHVO();
		String prematchSeq = iot110.getPrematchSeq();
		pvo.setPREMATCH_SEQ(prematchSeq); //購買檢核編號
		pvo.setINS_KIND("1"); //壽險
		pvo.setREG_TYPE("1"); //保險交易項目 1.壽險新契約
		pvo.setOTH_TYPE("1"); //要保書類型 1.非手寫電子要保書
		pvo.setCASE_ID(caseId); //案件編號
		pvo.setMATCH_DATE(new Timestamp(sdFormat.parse(uploadDatetime).getTime())); //檢核日期
		pvo.setAPPLY_DATE(new Timestamp(sdFormat.parse(uploadDatetime).getTime())); //要保書申請日
		pvo.setDATA_SHR_YN("Y"); //人壽資料共享案件
		pvo.setRECRUIT_ID(empId); //招攬人員
		pvo.setBRANCH_NBR(deptId); //鍵機分行代碼
		pvo.setFB_COM_YN("Y"); //進件來源 富壽:Y
		pvo.setCOMPANY_NUM(new BigDecimal(82)); //保險公司序號
		pvo.setSTATUS("1"); //暫存
		
		getDataAccessManager().create(pvo);
		
		//CREATOR = MODIFIER = 招攬人員；API抓不到LOGINID
		String queryStr = "UPDATE TBIOT_PREMATCH SET CREATOR = :empId, MODIFIER = :empId WHERE PREMATCH_SEQ = :prematchSeq ";
		getDataAccessManager().exeUpdate(genDefaultQueryConditionIF()
			.setQueryString(queryStr)
			.setObject("empId", empId)
			.setObject("prematchSeq", prematchSeq)
		);
	}
	
	/***
	 * 由理專身分證字號取得員編及分行代碼
	 * @param custId
	 * @return
	 * @throws DAOException
	 * @throws JBranchException
	 */
	@SuppressWarnings("unchecked")
	public List<Map<String , Object>> getEmpInfo(String custId) throws DAOException, JBranchException {
		String queryStr = "SELECT EMP_ID, DEPT_ID FROM TBORG_MEMBER WHERE CUST_ID = :custId AND SERVICE_FLAG = 'A' AND CHANGE_FLAG IN ('A', 'M', 'P') ";		
		return getDataAccessManager().exeQuery(genDefaultQueryConditionIF().setQueryString(queryStr).setObject("custId" , custId));
	}
	
	/***
	 * 此案件編號是否已存在
	 * @param caseId
	 * @return
	 * @throws NotFoundException
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public boolean isCaseIdExisted(String caseId) throws NotFoundException, DAOException {
		TBIOT_PREMATCHVO pvo = new TBIOT_PREMATCHVO();
        pvo.setCASE_ID(caseId);
		List<TBIOT_PREMATCHVO> pvoList = getDataAccessManager().findByFields(pvo, "CASE_ID");
		
		return CollectionUtils.isNotEmpty(pvoList);
	}
	
	/**
	 * 人壽端取得行動要保書檢核
	 * @throws Exception 
	 */
	public Map<String , Object> validateInsData(GenericMap paramMap) throws Exception {
		Map<String , Object> map = new HashMap<String , Object>();
		map.put("validateInsTerm", insTerminateChk(paramMap)); 				//行內保單解約檢核
		map.put("validateLoan", insLoanChk(paramMap)); 						//貸款或保單借款檢核
		map.put("validateIncome_A", incomeChkA(paramMap)); 					//要保人收入檢核
		map.put("validateIncome_I", incomeChkI(paramMap));					//被保人收入檢核		
		map.put("validatePrematch", prematchChk(paramMap));					//主約險種適配資訊檢核
		map.put("validateTarPrematch", tarPrematchChk(paramMap));			//標的適配資訊檢核
		map.put("validateKYCIncome_A", KYCIncomeChkA(paramMap));			//要保人KYC收入檢核
		map.put("validateKYCIncome_I", KYCIncomeChkI(paramMap));			//被保人KYC收入檢核	
		map.put("validateDebit_A", debitChkA(paramMap));					//要保人負債金額檢核
		map.put("validateDebit_I", debitChkI(paramMap));					//被保人負債金額檢核
		map.put("validateCDTerm_A1", CDTermChkA1(paramMap));				//A1定存解約利息有打折檢核
		map.put("validateCDTerm_A2", CDTermChkA2(paramMap));				//A2定存解約利息免打折檢核
		//帳號檢核
		map.put("validateCustAccount", custAccountChk(paramMap));			//要保書要保人帳號檢核
		map.put("validateFirstAccount", firstAuthAccountChk(paramMap));		//轉帳授權書帳號-首期檢核
		map.put("validateRenewAccount", renewAuthAccountChk(paramMap));		//轉帳授權書帳號-續期檢核
		map.put("validateRecruitorCert", recruitorCertChk(paramMap));		//理專(招攬人員)商品教育訓練檢核
////		//學歷值檢核
//		map.put("validateIlliterate", illiterateChk(paramMap));				//要保人或被保人或法定代理人或眷屬123不識字檢核
//		map.put("validateCustEdu", custEduChk(paramMap));					//要保人學歷值檢核
//		map.put("validateInsuredEdu", insuredEduChk(paramMap));				//被保人學歷值檢核
//		map.put("validatePayerEdu", payerEduChk(paramMap));					//繳款人學歷值檢核
//		map.put("validateFamilyEdu1", familyEduChk1(paramMap));				//眷屬1學歷值檢核
////		//高齡評估量表：維護日檢核
//		map.put("validateCustEvalDate", custEvalDateChk(paramMap));			//要保人高齡評估量表維護日檢核
//		map.put("validateInsuredEvalDate", insuredEvalDateChk(paramMap));	//被保人高齡評估量表維護日檢核
//		map.put("validatePayerEvalDate", payerEvalDateChk(paramMap));		//繳款人高齡評估量表維護日檢核
//		map.put("validateFamilyEvalDate1", familyEvalDateChk1(paramMap));	//眷屬1高齡評估量表維護日檢核
////		//高齡評估量表：健康情況檢核
//		map.put("validateCustHealth", custHealthChk(paramMap));				//要保人高齡評估量表健康情況檢核
//		map.put("validateInsuredHealth", insuredHealthChk(paramMap));		//被保人高齡評估量表健康情況檢核
//		map.put("validatePayerHealth", payerHealthChk(paramMap));			//繳款人高齡評估量表健康情況檢核
//		map.put("validateFamilyHealth1", familyHealthChk1(paramMap));		//眷屬1高齡評估量表健康情況檢核
////		//高齡評估量表：金融認知檢核
//		map.put("validateCustCogn", custCognChk(paramMap));					//要保人高齡評估量表金融認知檢核
//		map.put("validateInsuredCogn", insuredCognChk(paramMap));			//被保人高齡評估量表金融認知檢核
//		map.put("validatePayerCogn", payerCognChk(paramMap));				//繳款人高齡評估量表金融認知檢核
//		map.put("validateFamilyCogn1", familyCognChk1(paramMap));			//眷屬1高齡評估量表金融認知檢核
////		//高齡評估量表：評估結果檢核
//		map.put("validateCustEvalRslt", custEvalRsltChk(paramMap));			//要保人高齡評估量表評估結果檢核
//		map.put("validateInsuredEvalRslt", insuredEvalRsltChk(paramMap));	//被保人高齡評估量表評估結果檢核
//		map.put("validatePayerEvalRslt", payerEvalRsltChk(paramMap));		//繳款人高齡評估量表評估結果檢核
//		map.put("validateFamilyEvalRslt1", familyEvalRsltChk1(paramMap));	//眷屬1高齡評估量表評估結果檢核
		
		return map;
	}	
	
	/**
	 * 行內保單解約檢核
	 * 檢核客戶ID(要保人或被保人)93天內是否有保單解約
	 * 若要保人舊保單提領保額/保價日為要保書申請日前3個月，則行內解約也為Y
	 * 搜尋其他送件登錄作業，若符合以下條件，則行內保險單解約檢核也為Y
	 * 		A.文件種類=契變-解約/縮小保額
	 * 		B.ID符合其他送件案件的要保人ID
	 * 		C.新契約要保書申請日-93天<文件申請日<=系統日
	 *  @param paramMap
	 * @return N：人壽須處理 (要保人、被保人及繳款人，投保前三個月有辦理契約終止(解約))
	 * 		   Y：人壽無須處理 
	 * @throws DAOException
	 * @throws JBranchException
	 * @throws ParseException 
	 */
	private String insTerminateChk(GenericMap paramMap) throws DAOException, JBranchException, ParseException {
		IOT920 iot920 = (IOT920) PlatformContext.getBean("iot920");
		
		//要保人ID
		String custId = paramMap.getNotNullStr("custId");
		//被保人ID
		String insuredId = paramMap.getNotNullStr("insuredId");
		//繳款人ID
		String payerId = paramMap.getNotNullStr("payerId");
		//要保書申請日
		Date applyDate = new SimpleDateFormat("yyyyMMdd").parse(paramMap.getNotNullStr("applyDate"));
		
		String custYN = iot920.insTerminateChk(custId, applyDate);
		String insuredYN = iot920.insTerminateChk(insuredId, applyDate);
		String payerYN = iot920.insTerminateChk(payerId, applyDate);
		
		if("Y".equals(custYN) || "Y".equals(insuredYN) || "Y".equals(payerYN)) {
			//要保人、被保人及繳款人，投保前三個月有辦理契約終止(解約)
			//回傳N：人壽須處理
			return "N";
		} else {
			return "Y";
		}
	}
	
	/***
	 * 要保人、被保人及繳款人，投保前三個月內是否有辦理貸款或保險單借款的情形
	 * @param paramMap
	 * @return  N：人壽須處理 (要保人、被保人及繳款人，投保前三個月內有辦理貸款或保險單借款的情形)
	 * 			Y：人壽無須處理
	 * @throws DAOException
	 * @throws JBranchException
	 * @throws ParseException
	 */
	private String insLoanChk(GenericMap paramMap) throws DAOException, JBranchException, ParseException {
		IOT920 iot920 = (IOT920) PlatformContext.getBean("iot920");
		
		//要保人ID
		String custId = paramMap.getNotNullStr("custId");
		//被保人ID
		String insuredId = paramMap.getNotNullStr("insuredId");
		//繳款人ID
		String payerId = paramMap.getNotNullStr("payerId");
		//要保書申請日
		Date applyDate = new SimpleDateFormat("yyyyMMdd").parse(paramMap.getNotNullStr("applyDate"));
		
		//貸款檢核(透過本行送件)
		String custYN1 = iot920.insLoanChk(custId, applyDate);
		String insuredYN1 = iot920.insLoanChk(insuredId, applyDate);
		String payerYN1 = iot920.insLoanChk(payerId, applyDate);
		//行內貸款檢核
		String custYN2 = (String)iot920.inHouseLoanChk(custId, applyDate).get("isInHouseLoan");
		String insuredYN2 = (String)iot920.inHouseLoanChk(insuredId, applyDate).get("isInHouseLoan");
		String payerYN2 = (String)iot920.inHouseLoanChk(payerId, applyDate).get("isInHouseLoan");
		//93天內是否有行內貸款申請
		String custYN3 = (String)iot920.getCustLoanDate(custId, applyDate).get("isLoanApply");
		String insuredYN3 = (String)iot920.getCustLoanDate(insuredId, applyDate).get("isLoanApply");
		String payerYN3 = (String)iot920.getCustLoanDate(payerId, applyDate).get("isLoanApply");
		
		if("Y".equals(custYN1) || "Y".equals(insuredYN1) || "Y".equals(payerYN1) ||
				"Y".equals(custYN2) || "Y".equals(insuredYN2) || "Y".equals(payerYN2) ||
				"Y".equals(custYN3) || "Y".equals(insuredYN3) || "Y".equals(payerYN3)) {
			//要保人、被保人及繳款人，投保前三個月內是否有辦理貸款或保險單借款的情形
			//回傳N：人壽須處理
			return "N";
		} else {
			return "Y";
		}
	}
	
	/***
	 * 要保人收入檢核
	 * 要保人有最近一年內撥貸日者，才需要檢核授信收入
	 * @param paramMap
	 * @return 	N1：人壽收入 > 銀行收入
	 * 			N2：人壽收入 < 銀行收入
	 * 			Y：人壽不須處理
	 * @throws DAOException
	 * @throws JBranchException
	 * @throws ParseException
	 */
	private String incomeChkA(GenericMap paramMap) throws DAOException, JBranchException, ParseException {
		IOT920 iot920 = (IOT920) PlatformContext.getBean("iot920");
		
		//要保人ID
		String custId = paramMap.getNotNullStr("custId");
		//要保書申請日
		Date applyDate = new SimpleDateFormat("yyyyMMdd").parse(paramMap.getNotNullStr("applyDate"));
		//要保人工作收入(元)
		BigDecimal custWIncome = new BigDecimal(paramMap.getNotNullStr("custWIncome"));
		//要保人其他收入(元)
		BigDecimal custOIncome = new BigDecimal(paramMap.getNotNullStr("custOIncome"));
		//最近授信收入維護日
		Map<String, Object> creditData = iot920.getCreditData(custId, applyDate);
		Date creditLastupdate = (Date)creditData.get("creditLastUpdate");
		//取得工作年收入(授信徵審)
		BigDecimal bankIncome = (BigDecimal)creditData.get("income3");
		
		//要保人有最近一年內最近授信收入維護日者，才需要檢核授信收入
		if(creditLastupdate != null) {
			BigDecimal insIncome = custWIncome.add(custOIncome);
			if(insIncome.compareTo(bankIncome) > 0)
				return "N1"; //人壽收入 > 銀行收入
			else if(insIncome.compareTo(bankIncome) < 0) 
				return "N2"; //人壽收入 < 銀行收入
		}
		
		return "Y"; //人壽不須處理
	}
	
	/***
	 * 被保人收入檢核
	 * 被保人有最近一年內撥貸日者，才需要檢核授信收入
	 * @param paramMap
	 * @return 	N1：人壽收入 > 銀行收入
	 * 			N2：人壽收入 < 銀行收入
	 * 			Y：人壽不須處理
	 * @throws DAOException
	 * @throws JBranchException
	 * @throws ParseException
	 */
	private String incomeChkI(GenericMap paramMap) throws DAOException, JBranchException, ParseException {
		IOT920 iot920 = (IOT920) PlatformContext.getBean("iot920");
		
		//被保人ID
		String insuredId = paramMap.getNotNullStr("insuredId");
		//要保書申請日
		Date applyDate = new SimpleDateFormat("yyyyMMdd").parse(paramMap.getNotNullStr("applyDate"));
		//被保人工作收入(元)
		BigDecimal insuredWIncome = new BigDecimal(paramMap.getNotNullStr("insuredWIncome"));
		//被保人其他收入(元)
		BigDecimal insuredOIncome = new BigDecimal(paramMap.getNotNullStr("insuredOIncome"));
		//最近授信收入維護日
		Map<String, Object> creditData = iot920.getCreditData(insuredId, applyDate);
		Date creditLastupdate = (Date)creditData.get("creditLastUpdate");
		//取得工作年收入(授信徵審)
		BigDecimal bankIncome = (BigDecimal)creditData.get("income3");
		
		//被保人有最近一年內最近授信收入維護日者，才需要檢核授信收入
		if(creditLastupdate != null) {
			BigDecimal insIncome = insuredWIncome.add(insuredOIncome);
			if(insIncome.compareTo(bankIncome) > 0)
				return "N1"; //人壽收入 > 銀行收入
			else if(insIncome.compareTo(bankIncome) < 0) 
				return "N2"; //人壽收入 < 銀行收入
		}
		
		return "Y"; //人壽不須處理
	}
	
	/***
	 * 適配資訊檢核
	 * 投資型商品、非投資型但須適配商品、外幣商品，需適配
	 * 需適配商品取得要保人KYC資料，進行KYC檢核
	 * @param paramMap
	 * @return N：人壽須處理 (無KYC資料、KYC已過期)
	 * 		   Y：人壽無須處理
	 * @throws Exception
	 */
	private String prematchChk(GenericMap paramMap) throws Exception {
		IOT910 iot910 = (IOT910) PlatformContext.getBean("iot910");
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
		String rtnString = "";
		
		//要保人ID
		String custId = paramMap.getNotNullStr("custId");
		//要保書申請日
		Date applyDate = df.parse(paramMap.getNotNullStr("applyDate"));
		//主約險種代碼
		String insPrdId = paramMap.getNotNullStr("insPrdId");
		
		//取得主約險種資訊
		IOT910InputVO inputVO910 = new IOT910InputVO();
		inputVO910.setINSPRD_ID(insPrdId);
		inputVO910.setINS_RIDER_DLT(false);
		insPrd = iot910.getINSPRD_FB(inputVO910).getINSPRDList();
		
		if(CollectionUtils.isEmpty(insPrd)) {
			throw new JBranchException("查無此主約險種資料");
		} else {
			rtnString = "Y"; //人壽不須處理
			
			//投資型商品、非投資型但須適配商品、外幣商品，需適配
			if(!"1".equals(ObjectUtils.toString(insPrd.get(0).get("INSPRD_TYPE"))) || 
					StringUtils.equals("Y", ObjectUtils.toString(insPrd.get(0).get("NEED_MATCH"))) ||
					!StringUtils.equals("TWD", ObjectUtils.toString(insPrd.get(0).get("CURR_CD")))) {	
				//取得客戶資料
				getCustInfo(custId);
				//檢核KYC
				if(custKYCDataVO == null || custKYCDataVO.getKycDueDate() == null) {
					rtnString = "N"; //查不到風險屬性或KYC截止日到期
				} else {
					Date kycDueDate = df.parse(df.format(custKYCDataVO.getKycDueDate().getTime()));
					if(StringUtils.isNotBlank(custKYCDataVO.getKycLevel()) && kycDueDate.compareTo(applyDate) >= 0){
						String prdRisk = ObjectUtils.toString(insPrd.get(0).get("PRD_RISK")); //商品風險屬性
						//非投資型但須適配商品，需檢核險種適配
						if("1".equals(ObjectUtils.toString(insPrd.get(0).get("INSPRD_TYPE"))) && 
								StringUtils.equals("Y", ObjectUtils.toString(insPrd.get(0).get("NEED_MATCH")))) {
							if(StringUtils.isBlank(prdRisk) || prdRisk.length() < 2) {
								rtnString = "N"; //查無此商品風險屬性資料
							} else if(Integer.parseInt(prdRisk.substring(1)) > Integer.parseInt(custKYCDataVO.getKycLevel().substring(1))) {
								rtnString = "N"; //商品不適配
							}
						}
					} else {
						rtnString = "N"; //查不到風險屬性或KYC截止日到期
					}
				}
			}
		}
		
		return rtnString;
	}
	
	/***
	 * 標的適配資訊檢核
	 * 傳入的標的是否在可申購標的清單中
	 * @param paramMap
	 * @return Y:可申購 N:不可申購
	 * 每檔傳入標的都須回傳 {"1111":"Y", "2222":"N", "3333":"Y"}
	 * @throws Exception
	 */
	private Map<String, String> tarPrematchChk(GenericMap paramMap) throws Exception {
		Map<String, String> rtnList = new HashMap<String, String>();
		//要保人ID
		String custId = paramMap.getNotNullStr("custId");
		//要保書申請日
		Date applyDate = new SimpleDateFormat("yyyyMMdd").parse(paramMap.getNotNullStr("applyDate"));
		//主約險種代碼
		String insPrdId = paramMap.getNotNullStr("insPrdId");
		//標的資料
		ArrayList<String> targetIds = (ArrayList<String>) paramMap.get("targetIds");
		
		if(CollectionUtils.isNotEmpty(targetIds)) {
			if("1".equals(ObjectUtils.toString(insPrd.get(0).get("INSPRD_TYPE")))) {
				throw new JBranchException("非投資型商品無標的適配資訊");
			} else {
				//要保人為高齡客戶(年齡>=64.5歲)購買投資型商品，取得可購買標的商品P值
				String seniorPval = getSeniorCustPVal(custId, applyDate);
				//取得可購買標的清單
				IOT920 iot920 = (IOT920) PlatformContext.getBean("iot920");
				InsFundListInputVO inputVO920 = new InsFundListInputVO();
				inputVO920.setCustID(custId);
				inputVO920.setC_SENIOR_PVAL(seniorPval);
				inputVO920.setINSPRD_ID(insPrdId);
				List<Map<String, Object>> targetList = iot920.Get_List(inputVO920);
				//傳入的標的是否在可申購標的清單中
				for(String target : targetIds) {
					rtnList.put(target, isTargetInList(target, targetList) ? "Y" : "N");
				}
			}
		}
		
		return rtnList;
	}
	
	/***
	 * 要保人KYC收入檢核
	 * @param paramMap
	 * @return 	Y:  無KYC收入
	 * 			Y:  人壽收入介於KYC問卷收入內
	 * 			N1: 人壽收入大於KYC問卷收入
	 * 			N2: 人壽收入小於KYC問卷收入
	 * @throws ParseException
	 * @throws DAOException
	 * @throws JBranchException
	 */
	private String KYCIncomeChkA(GenericMap paramMap) throws ParseException, JBranchException {
		//要保人ID
		String custId = paramMap.getNotNullStr("custId");
		//要保書申請日
		Date applyDate = new SimpleDateFormat("yyyyMMdd").parse(paramMap.getNotNullStr("applyDate"));
		//要保人工作收入(元)
		BigDecimal custWIncome = new BigDecimal(paramMap.getNotNullStr("custWIncome"));
		//要保人其他收入(元)
		BigDecimal custOIncome = new BigDecimal(paramMap.getNotNullStr("custOIncome"));
		//人壽收入=要保人工作收入+其他收入
		BigDecimal insIncome = custWIncome.add(custOIncome);
		
		//要保人KYC收入檢核
		return getKYCIncomeChk(custId, applyDate, insIncome);
	}
	
	/***
	 * 被保人KYC收入檢核
	 * @param paramMap
	 * @return 	Y:  無KYC收入
	 * 			Y:  人壽收入介於KYC問卷收入內
	 * 			N1: 人壽收入大於KYC問卷收入
	 * 			N2: 人壽收入小於KYC問卷收入
	 * @throws ParseException
	 * @throws DAOException
	 * @throws JBranchException
	 */
	private String KYCIncomeChkI(GenericMap paramMap) throws JBranchException, ParseException {
		//被保人ID
		String insuredId = paramMap.getNotNullStr("insuredId");
		//要保書申請日
		Date applyDate = new SimpleDateFormat("yyyyMMdd").parse(paramMap.getNotNullStr("applyDate"));
		//被保人工作收入(元)
		BigDecimal insuredWIncome = new BigDecimal(paramMap.getNotNullStr("insuredWIncome"));
		//被保人其他收入(元)
		BigDecimal insuredOIncome = new BigDecimal(paramMap.getNotNullStr("insuredOIncome"));
		//人壽收入=被保人工作收入+其他收入
		BigDecimal insIncome = insuredWIncome.add(insuredOIncome);
				
		//被保人KYC收入檢核
		return getKYCIncomeChk(insuredId, applyDate, insIncome);
	}
	
	/***
	 * 要保人負債金額檢核
	 * @param paramMap
	 * @return 	"Y": 人壽負債為空白則通過此項檢核
	 * 			"Y": 人壽負債 >= 銀行負債
	 * 			"N2": 人壽負債<銀行負債
	 * @throws Exception
	 */
	private String debitChkA(GenericMap paramMap) throws Exception {
		String rtnString = "Y";
		//要保人ID
		String custId = paramMap.getNotNullStr("custId");
		//要保人財告書負債欄位
		String custDebit = paramMap.getNotNullStr("custDebit");
		
		//人壽負債為空白則通過此項檢核，不為空白才檢核
		if(StringUtils.isNotBlank(custDebit)) {
			BigDecimal insCustDebit = (new BigDecimal(custDebit)).divide(new BigDecimal(10000)); //以萬元為單位
			//取得要保人銀行負債
			IOT920 iot920 = (IOT920) PlatformContext.getBean("iot920");
			BigDecimal custFu = iot920.getFu(custId);
			
			if(insCustDebit.compareTo(custFu) < 0) {
				//人壽負債<銀行負債，回N2
				rtnString = "N2";
			}
		}
		
		return rtnString;
	}
	
	/***
	 * 被保人負債金額檢核
	 * @param paramMap
	 * @return 	"Y": 人壽負債為空白則通過此項檢核
	 * 			"Y": 人壽負債 >= 銀行負債
	 * 			"N2": 人壽負債<銀行負債
	 * @throws Exception
	 */
	private String debitChkI(GenericMap paramMap) throws Exception {
		String rtnString = "Y";
		//被保人ID
		String insuredId = paramMap.getNotNullStr("insuredId");
		//被保人財告書負債欄位
		String insuredDebit = paramMap.getNotNullStr("insuredDebit");
		
		//人壽負債為空白則通過此項檢核，不為空白才檢核
		if(StringUtils.isNotBlank(insuredDebit)) {
			BigDecimal insCustDebit = (new BigDecimal(insuredDebit)).divide(new BigDecimal(10000)); //以萬元為單位
			//取得被保人銀行負債
			IOT920 iot920 = (IOT920) PlatformContext.getBean("iot920");
			BigDecimal insuredFu = iot920.getFu(insuredId);
			
			if(insCustDebit.compareTo(insuredFu) < 0) {
				//人壽負債<銀行負債，回N2
				rtnString = "N2";
			}
		}
				
		return rtnString;
	}
	
	/***
	 * A1定存解約利息有打折檢核(定存單解約/到期日檢核)
	 * @param paramMap
	 * @return 	"N": 人壽A1定存解約利息有打折未勾選，銀行端檢核93日內有定存解約或到期
	 * 			"Y": 其他皆通過
	 * @throws ParseException
	 * @throws JBranchException
	 */
	private String CDTermChkA1(GenericMap paramMap) throws ParseException, JBranchException {
		String rtnString = "Y";
		//要保人ID
		String custId = paramMap.getNotNullStr("custId");
		//被保人ID
		String insuredId = paramMap.getNotNullStr("insuredId");
		//繳款人ID
		String payerId = paramMap.getNotNullStr("payerId");
		//要保書申請日
		Date applyDate = new SimpleDateFormat("yyyyMMdd").parse(paramMap.getNotNullStr("applyDate"));
		//A1定存解約利息有打折，是否有勾選
		String premSource_A1 = paramMap.getNotNullStr("premSource_A1");
		
		//人壽A1定存解約利息有打折未勾選
		if(StringUtils.isBlank(premSource_A1) || StringUtils.equals("N", premSource_A1)) {
			//要保人、被保人、繳款人，任一有定存解約日落於系統日-93天~系統日，表示有定存解約或到期
			IOT920 iot920 = (IOT920) PlatformContext.getBean("iot920");
			String intCDDueDateYN = (iot920.getCDDueDate(custId, applyDate) != null ||
								 	 iot920.getCDDueDate(insuredId, applyDate) != null ||
								 	 iot920.getCDDueDate(payerId, applyDate) != null) ? "Y" : "N";
			
			//人壽A1定存解約利息有打折未勾選，銀行端檢核93日內有定存解約或到期則回"N"不通過，否則為"Y"通過
			rtnString = StringUtils.equals("Y", intCDDueDateYN) ? "N" : "Y";
		}
		
		return rtnString;
	}
	
	/***
	 * A2定存解約利息免打折檢核
	 * @param paramMap
	 * @return 	"N": 人壽A2定存解約利息免打折未勾選，銀行端檢核有定存解約免打折
	 * 			"Y": 其他皆通過
	 * @throws ParseException
	 * @throws JBranchException
	 */
	private String CDTermChkA2(GenericMap paramMap) throws ParseException, JBranchException {
		String rtnString = "Y";
		//要保人ID
		String custId = paramMap.getNotNullStr("custId");
		//被保人ID
		String insuredId = paramMap.getNotNullStr("insuredId");
		//繳款人ID
		String payerId = paramMap.getNotNullStr("payerId");
		//要保書申請日
		Date applyDate = new SimpleDateFormat("yyyyMMdd").parse(paramMap.getNotNullStr("applyDate"));
		//A2定存解約利息免打折，是否有勾選
		String premSource_A2 = paramMap.getNotNullStr("premSource_A2");
		
		//人壽A2定存解約利息免打折未勾選
		if(StringUtils.isBlank(premSource_A2) || StringUtils.equals("N", premSource_A2)) {
			//要保人、被保人、繳款人，任一有定存解約免打折，表示有定存解約免打折
			IOT920 iot920 = (IOT920) PlatformContext.getBean("iot920");
			String intCDChkYN = (StringUtils.equals("Y", iot920.intCDChk(custId, applyDate)) || 
								 StringUtils.equals("Y", iot920.intCDChk(insuredId, applyDate)) ||
								 StringUtils.equals("Y", iot920.intCDChk(payerId, applyDate))) ? "Y" : "N";
			
			//人壽A2定存解約利息免打折未勾選，銀行端檢核有定存解約免打折則回"N"不通過，否則為"Y"通過
			rtnString = StringUtils.equals("Y", intCDChkYN) ? "N" : "Y";
		}
		
		return rtnString;
	}
	
	/***
	 * 要保人帳號檢核
	 * @param paramMap
	 * @return Y:檢核通過，人壽無須處理  N:檢核不通過，人壽須處理
	 * @throws Exception
	 */
	private String custAccountChk(GenericMap paramMap) throws Exception {
		String rtnString = "Y";
		//要保人ID
		String custId = paramMap.getNotNullStr("custId");
		//要保人帳號
		String custAcct = paramMap.getNotNullStr("custAccount"); //指定銀行3碼+帳戶號碼14碼(未足14碼前面補0)
		
		if(StringUtils.isBlank(custAcct) || custAcct.length() < 17) {
			rtnString = "N"; //沒有給帳號，人壽須處理
		} else if(!StringUtils.equals("012", custAcct.substring(0, 3))) {
			rtnString = "Y"; //銀行代碼不為012(富邦銀)，不須檢核，人壽不須處理
		} else {
			rtnString = getAccountChk(custId, custAcct.substring(3)); //檢核帳號是否存在
		}
		
		return rtnString;
	}
	
	/***
	 * 首期保費授權人帳號檢核
	 * @param paramMap
	 * @return Y:檢核通過，人壽無須處理  N:檢核不通過，人壽須處理
	 * @throws Exception
	 */
	private String firstAuthAccountChk(GenericMap paramMap) throws Exception {
		String rtnString = "Y";
		//首期保費授權人ID
		String firstAuthId = paramMap.getNotNullStr("firstAuthId");
		//首期保費授權人帳戶
		String firstAuthAccount = paramMap.getNotNullStr("firstAuthAccount"); //指定銀行3碼+帳戶號碼14碼(未足14碼前面補0)
		
		if(StringUtils.isBlank(firstAuthAccount) || firstAuthAccount.length() < 17) {
			rtnString = "N"; //沒有給帳號，人壽須處理
		} else if(!StringUtils.equals("012", firstAuthAccount.substring(0, 3))) {
			rtnString = "Y"; //銀行代碼不為012(富邦銀)，不須檢核，人壽不須處理
		} else {
			rtnString = getAccountChk(firstAuthId, firstAuthAccount.substring(3)); //檢核帳號是否存在
		}
		
		return rtnString;
	}
	
	/***
	 * 續期保費授權人帳號檢核
	 * @param paramMap
	 * @return Y:檢核通過，人壽無須處理  N:檢核不通過，人壽須處理
	 * @throws Exception
	 */
	private String renewAuthAccountChk(GenericMap paramMap) throws Exception {
		String rtnString = "Y";
		//續期保費授權人ID
		String renewAuthId = paramMap.getNotNullStr("renewAuthId");
		//續期保費授權人帳戶
		String renewAuthAccount = paramMap.getNotNullStr("renewAuthAccount"); //指定銀行3碼+帳戶號碼14碼(未足14碼前面補0)
		
		if(StringUtils.isBlank(renewAuthAccount) || renewAuthAccount.length() < 17) {
			rtnString = "N"; //沒有給帳號，人壽須處理
		} else if(!StringUtils.equals("012", renewAuthAccount.substring(0, 3))) {
			rtnString = "Y"; //銀行代碼不為012(富邦銀)，不須檢核，人壽不須處理
		} else {
			rtnString = getAccountChk(renewAuthId, renewAuthAccount.substring(3)); //檢核帳號是否存在
		}
		
		return rtnString;
	}
	
	/***
	 * 理專(招攬人員)商品教育訓練檢核
	 * @param paramMap
	 * @return Y:檢核通過，人壽無須處理  N:檢核不通過，人壽須處理
	 * @throws Exception
	 */
	private String recruitorCertChk(GenericMap paramMap) throws Exception {
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
		String rtnString = "Y";
		//招攬人員身分證字號
		String recruitorId = paramMap.getNotNullStr("recruitorId");
		//招攬人員編
		String empId = getEmpId(recruitorId);
		//要保書申請日
		Date applyDate = df.parse(paramMap.getNotNullStr("applyDate"));
		//主約險種代碼
		String insPrdId = paramMap.getNotNullStr("insPrdId");
				
		if(StringUtils.isBlank(empId)) {
			throw new JBranchException("查無此招攬人員員編資料");
		} else {
			//證照檢核資料
			IOT920 iot920 = (IOT920) PlatformContext.getBean("iot920");
			chk_CTInputVO inputVOCert = new chk_CTInputVO();
			inputVOCert.setCERT_TYPE(ObjectUtils.toString(insPrd.get(0).get("CERT_TYPE")));
			inputVOCert.setEMP_ID(empId);
			inputVOCert.setAPPLY_DATE(applyDate);
			inputVOCert.setTRAINING_TYPE(ObjectUtils.toString(insPrd.get(0).get("TRAINING_TYPE")));
			chk_CTOutputVO chkVO = iot920.chk_CertTraining(inputVOCert);
			rtnString = chkVO.getChk_Pass(); //Y:有通過 N:沒有通過
		}
		if(StringUtils.equals("Y", rtnString)) { //檢核不通過就不用繼續做了
			IOT120 iot120 = (IOT120) PlatformContext.getBean("iot120");
			IOT120InputVO inputVO120 = new IOT120InputVO();
			inputVO120.setRECRUIT_ID(empId);
			inputVO120.setEmpId(empId);
			inputVO120.setAPPLY_DATE(applyDate);
			inputVO120.setINSPRD_ID(insPrdId);
			IOT120OutputVO outputVO120 = iot120.chkValid(inputVO120);
			if(CollectionUtils.isNotEmpty(outputVO120.getREFList())) {
				if(StringUtils.equals("Y", ObjectUtils.toString(outputVO120.getREFList().get(0).get("EXPIRED")))) {
					rtnString = "N"; //壽險證照已超過法定換證期限無法進行招攬
				}
				if(StringUtils.equals("Y", ObjectUtils.toString(outputVO120.getREFList().get(0).get("UNREG")))) {
					rtnString = "N"; //壽險證照已註銷無法進行招攬
				}
			}
			if(CollectionUtils.isNotEmpty(outputVO120.getTMSList())) {
				if(StringUtils.equals("N", ObjectUtils.toString(outputVO120.getTMSList().get(0).get("STUDY_COMPLETED")))) {
					rtnString = "N"; //主約商品教育訓練尚未完成，無法通過檢核
				}
			}
		}
		
		return rtnString;
	}
	
	/***
	 * 要保人或被保人或法定代理人不識字檢核
	 * @param paramMap
	 * @return	要保人、被保人、法定代理人各別回傳檢核結果
	 * 			Y:識字，人壽無須處理 (無學歷紀錄，無須處理)
	 * 			N:不識字，人壽須處理
	 * @throws Exception
	 */
	private Map<String, String> illiterateChk(GenericMap paramMap) throws Exception {
		Map<String, String> rtnList = new HashMap<String, String>();
		//要保人ID
		String custId = paramMap.getNotNullStr("custId");
		//被保人ID
		String insuredId = paramMap.getNotNullStr("insuredId");
		//要保人法定代理人ID
		String legalRepId = paramMap.getNotNullStr("legalRepId");
		//眷屬1ID
		String familyId1 = paramMap.getNotNullStr("familyId1");
		//眷屬2ID
		String familyId2 = paramMap.getNotNullStr("familyId2");
		//眷屬3ID
		String familyId3 = paramMap.getNotNullStr("familyId3");
		
		SOT701 sot701 = (SOT701) PlatformContext.getBean("sot701");
		//要保人學歷值
		SOT701InputVO inputVO701 = new SOT701InputVO();
		inputVO701.setCustID(custId.toUpperCase());
		FP032151OutputVO custVO = sot701.getFP032151Data(inputVO701, null);
		custEdu = custVO.getEDUCATION(); 
		//被保人學歷值
		String insuredEdu = "Y";
		if(isCustInBank(insuredId)) { //要保人為本行客戶才檢核
			inputVO701 = new SOT701InputVO();
			inputVO701.setCustID(insuredId.toUpperCase());
			FP032151OutputVO insuredVO = sot701.getFP032151Data(inputVO701, null);
			insuredEdu = insuredVO.getEDUCATION(); 
		}
		//要保人法定代理人學歷值
		String repEdu = "Y";
		if(StringUtils.isNotBlank(legalRepId) && isCustInBank(legalRepId)) {
			inputVO701 = new SOT701InputVO();
			inputVO701.setCustID(legalRepId.toUpperCase());
			FP032151OutputVO repVO = sot701.getFP032151Data(inputVO701, null);
			repEdu = repVO.getEDUCATION(); 
		}
		//眷屬1學歷值
		String famEdu1 = "Y";
		if(StringUtils.isNotBlank(familyId1) && isCustInBank(familyId1)) {
			inputVO701 = new SOT701InputVO();
			inputVO701.setCustID(familyId1.toUpperCase());
			FP032151OutputVO repVO = sot701.getFP032151Data(inputVO701, null);
			famEdu1 = repVO.getEDUCATION(); 
		}
		//眷屬2學歷值
		String famEdu2 = "Y";
		if(StringUtils.isNotBlank(familyId2) && isCustInBank(familyId2)) {
			inputVO701 = new SOT701InputVO();
			inputVO701.setCustID(familyId2.toUpperCase());
			FP032151OutputVO repVO = sot701.getFP032151Data(inputVO701, null);
			famEdu2 = repVO.getEDUCATION(); 
		}
		//眷屬3學歷值
		String famEdu3 = "Y";
		if(StringUtils.isNotBlank(familyId3) && isCustInBank(familyId3)) {
			inputVO701 = new SOT701InputVO();
			inputVO701.setCustID(familyId3.toUpperCase());
			FP032151OutputVO repVO = sot701.getFP032151Data(inputVO701, null);
			famEdu3 = repVO.getEDUCATION(); 
		}
		//8:不識字，若無學歷紀錄，無須處理
		rtnList.put("CUST", (StringUtils.isNotBlank(custEdu) && "8".equals(custEdu)) ? "N" : "Y");
		rtnList.put("INSURED", (StringUtils.isNotBlank(insuredEdu) && "8".equals(insuredEdu)) ? "N" : "Y");
		rtnList.put("LEGALREP", (StringUtils.isNotBlank(repEdu) && "8".equals(repEdu)) ? "N" : "Y");
		rtnList.put("FAM1", (StringUtils.isNotBlank(famEdu1) && "8".equals(famEdu1)) ? "N" : "Y");
		rtnList.put("FAM2", (StringUtils.isNotBlank(famEdu2) && "8".equals(famEdu2)) ? "N" : "Y");
		rtnList.put("FAM3", (StringUtils.isNotBlank(famEdu3) && "8".equals(famEdu3)) ? "N" : "Y");
				
		return rtnList;
	}
	
	/***
	 * 要保人學歷值檢核
	 * 銀行端高齡客戶學歷與人壽端高齡客戶投保評估量表，客戶學歷是否一致
	 * @param paramMap
	 * @return	Y:人壽不需處理 (客戶學歷值一致，或非高齡或非本行客戶，不須處理)
	 * 			N:人壽需處理
	 * @throws Exception
	 */
	private String custEduChk(GenericMap paramMap) throws Exception {
		String rtnString = "Y"; //非高齡或非本行客戶，不須處理
		
		if(isCustSenior) { //高齡客戶
			//要保人富壽學歷值
			String insEdu = paramMap.getNotNullStr("custEdu");
			//由北富銀學歷值取得對應的富壽學歷值
			boolean eduMapping = isParamMapping("IOT.EDUCATION_MAPPING", custEdu.concat(insEdu));
			//Y:客戶學歷值一致不須處理
			rtnString = eduMapping ? "Y" : "N";
		}
		
		return rtnString;
	}
	
	/***
	 * 被保人學歷值檢核
	 * 銀行端高齡客戶學歷與人壽端高齡客戶投保評估量表，客戶學歷是否一致
	 * @param paramMap
	 * @return	Y:人壽不需處理 (客戶學歷值一致，或非高齡或非本行客戶，不須處理)
	 * 			N:人壽需處理
	 * @throws Exception
	 */
	private String insuredEduChk(GenericMap paramMap) throws Exception {
		String rtnString = "Y"; //非高齡或非本行客戶，不須處理
		
		//被保人ID
		String insuredId = paramMap.getNotNullStr("insuredId");
		//被保人富壽學歷值
		String insInsuredEdu = paramMap.getNotNullStr("insuredEdu");
		
		if(chkCustSenior(insuredId)) { //高齡客戶
			//取得被保人學歷值
			SOT701 sot701 = (SOT701) PlatformContext.getBean("sot701");
			SOT701InputVO inputVO701 = new SOT701InputVO();
			inputVO701.setCustID(insuredId.toUpperCase());
			FP032151OutputVO insuredVO = sot701.getFP032151Data(inputVO701, null);
			String insuredEdu = insuredVO.getEDUCATION();
			//由北富銀學歷值取得對應的富壽學歷值
			boolean eduMapping = isParamMapping("IOT.EDUCATION_MAPPING", insuredEdu.concat(insInsuredEdu));
			//Y:客戶學歷值一致不須處理
			rtnString = eduMapping ? "Y" : "N";
		}
		
		return rtnString;
	}
	
	/***
	 * 繳款人學歷值檢核
	 * 銀行端高齡客戶學歷與人壽端高齡客戶投保評估量表，客戶學歷是否一致
	 * @param paramMap
	 * @return	Y:人壽不需處理 (客戶學歷值一致，或非高齡或非本行客戶，不須處理)
	 * 			N:人壽需處理
	 * @throws Exception
	 */
	private String payerEduChk(GenericMap paramMap) throws Exception {
		String rtnString = "Y"; //非高齡或非本行客戶，不須處理
		
		//繳款人ID
		String payerId = paramMap.getNotNullStr("payerId");
		//繳款人富壽學歷值
		String insPayerEdu = paramMap.getNotNullStr("payerEdu");
		
		if(chkCustSenior(payerId)) { //高齡客戶
			//取得繳款人在北富銀學歷值
			SOT701 sot701 = (SOT701) PlatformContext.getBean("sot701");
			SOT701InputVO inputVO701 = new SOT701InputVO();
			inputVO701.setCustID(payerId.toUpperCase());
			FP032151OutputVO custVO = sot701.getFP032151Data(inputVO701, null);
			String payerEdu = custVO.getEDUCATION();			
			//由北富銀學歷值取得對應的富壽學歷值
			boolean eduMapping = isParamMapping("IOT.EDUCATION_MAPPING", payerEdu.concat(insPayerEdu));
			
			//Y:客戶學歷值一致不須處理
			rtnString = eduMapping ? "Y" : "N";
		}
		
		return rtnString;
	}
	
	/***
	 * 眷屬1學歷值檢核
	 * 銀行端高齡客戶學歷與人壽端高齡客戶投保評估量表，客戶學歷是否一致
	 * @param paramMap
	 * @return	Y:人壽不需處理 (客戶學歷值一致，或非高齡或非本行客戶，不須處理)
	 * 			N:人壽需處理
	 * @throws Exception
	 */
	private String familyEduChk1(GenericMap paramMap) throws Exception {
		String rtnString = "Y"; //非高齡或非本行客戶，不須處理
		
		//眷屬1ID
		String familyId1 = paramMap.getNotNullStr("familyId1");
		//眷屬1富壽學歷值
		String insFamilyEdu1 = paramMap.getNotNullStr("familyEdu1");
		
		if(StringUtils.isNotBlank(familyId1) && chkCustSenior(familyId1)) { //高齡客戶
			//取得眷屬1在北富銀學歷值
			SOT701 sot701 = (SOT701) PlatformContext.getBean("sot701");
			SOT701InputVO inputVO701 = new SOT701InputVO();
			inputVO701.setCustID(familyId1.toUpperCase());
			FP032151OutputVO custVO = sot701.getFP032151Data(inputVO701, null);
			String familyEdu = custVO.getEDUCATION();			
			//由北富銀學歷值取得對應的富壽學歷值
			boolean eduMapping = isParamMapping("IOT.EDUCATION_MAPPING", familyEdu.concat(insFamilyEdu1));
			
			//Y:客戶學歷值一致不須處理
			rtnString = eduMapping ? "Y" : "N";
		}
		
		return rtnString;
	}
	
	/***
	 * 要保人高齡評估量表維護日檢核
	 * @param paramMap
	 * @return	Y:人壽不需處理 (銀行高齡評估量表維護日介於系統日-3個工作天，或非高齡或非本行客戶)
	 * 			N:人壽需處理 (銀行高齡評估量表維護日不介於系統日-3個工作天)
	 * @throws Exception
	 */
	private String custEvalDateChk(GenericMap paramMap) throws Exception {
		String rtnString = "Y"; //非高齡或非本行客戶，不須處理
		//要保人ID
		String custId = paramMap.getNotNullStr("custId");
		
		if(isCustSenior) { //高齡客戶
			//系統日
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			String today_s = sdf.format(new Date());
			Date today =  sdf.parse(today_s);
			//取得系統日-3個工作天
			IOT110 iot110 = (IOT110) PlatformContext.getBean("iot110");
			Date beforeSysdateDate3 = iot110.getBeforeApplyDate3(today);
			//取得高齡評估表維護日
			SeniorValidation sv = (SeniorValidation) PlatformContext.getBean("SeniorValidation");
			Date svMatchDate = sv.validateSeniorCustEvalDateIns(custId);
			//檢核銀行高齡評估量表維護日是否介於系統日-3個工作天~系統日區間
			if(svMatchDate == null || svMatchDate.before(beforeSysdateDate3) || svMatchDate.after(today)) {
				rtnString = "N"; 
			}
		}
		
		return rtnString;
	}
	
	/***
	 * 被保人高齡評估量表維護日檢核
	 * @param paramMap
	 * @return	Y:人壽不需處理 (銀行高齡評估量表維護日介於系統日-3個工作天，或非高齡或非本行客戶)
	 * 			N:人壽需處理 (銀行高齡評估量表維護日不介於系統日-3個工作天)
	 * @throws Exception
	 */
	private String insuredEvalDateChk(GenericMap paramMap) throws Exception {
		String rtnString = "Y"; //非高齡或非本行客戶，不須處理
		//被保人ID
		String insuredId = paramMap.getNotNullStr("insuredId");
		
		if(chkCustSenior(insuredId)) { //高齡客戶
			//系統日
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			String today_s = sdf.format(new Date());
			Date today =  sdf.parse(today_s);
			//取得系統日-3個工作天
			IOT110 iot110 = (IOT110) PlatformContext.getBean("iot110");
			Date beforeSysdateDate3 = iot110.getBeforeApplyDate3(today);
			//取得高齡評估表維護日
			SeniorValidation sv = (SeniorValidation) PlatformContext.getBean("SeniorValidation");
			Date svMatchDate = sv.validateSeniorCustEvalDateIns(insuredId);
			//檢核銀行高齡評估量表維護日是否介於系統日-3個工作天~系統日區間
			if(svMatchDate == null || svMatchDate.before(beforeSysdateDate3) || svMatchDate.after(today)) {
				rtnString = "N"; 
			}
		}
		
		return rtnString;
	}
	
	/***
	 * 繳款人高齡評估量表維護日檢核
	 * @param paramMap
	 * @return	Y:人壽不需處理 (銀行高齡評估量表維護日介於系統日-3個工作天，或非高齡或非本行客戶)
	 * 			N:人壽需處理 (銀行高齡評估量表維護日不介於系統日-3個工作天)
	 * @throws Exception
	 */
	private String payerEvalDateChk(GenericMap paramMap) throws Exception {
		String rtnString = "Y"; //非高齡或非本行客戶，不須處理
		//繳款人ID
		String payerId = paramMap.getNotNullStr("payerId");
		
		if(chkCustSenior(payerId)) { //高齡客戶
			//系統日
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			String today_s = sdf.format(new Date());
			Date today =  sdf.parse(today_s);
			//取得系統日-3個工作天
			IOT110 iot110 = (IOT110) PlatformContext.getBean("iot110");
			Date beforeSysdateDate3 = iot110.getBeforeApplyDate3(today);
			//取得高齡評估表維護日
			SeniorValidation sv = (SeniorValidation) PlatformContext.getBean("SeniorValidation");
			Date svMatchDate = sv.validateSeniorCustEvalDateIns(payerId);
			//檢核銀行高齡評估量表維護日是否介於系統日-3個工作天~系統日區間
			if(svMatchDate == null || svMatchDate.before(beforeSysdateDate3) || svMatchDate.after(today)) {
				rtnString = "N"; 
			}
		}
		
		return rtnString;
	}
	
	/***
	 * 眷屬1高齡評估量表維護日檢核
	 * @param paramMap
	 * @return	Y:人壽不需處理 (銀行高齡評估量表維護日介於系統日-3個工作天，或非高齡或非本行客戶)
	 * 			N:人壽需處理 (銀行高齡評估量表維護日不介於系統日-3個工作天)
	 * @throws Exception
	 */
	private String familyEvalDateChk1(GenericMap paramMap) throws Exception {
		String rtnString = "Y"; //非高齡或非本行客戶，不須處理
		//眷屬1ID
		String familyId1 = paramMap.getNotNullStr("familyId1");
		
		if(StringUtils.isNotBlank(familyId1) && chkCustSenior(familyId1)) { //高齡客戶
			//系統日
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			String today_s = sdf.format(new Date());
			Date today =  sdf.parse(today_s);
			//取得系統日-3個工作天
			IOT110 iot110 = (IOT110) PlatformContext.getBean("iot110");
			Date beforeSysdateDate3 = iot110.getBeforeApplyDate3(today);
			//取得高齡評估表維護日
			SeniorValidation sv = (SeniorValidation) PlatformContext.getBean("SeniorValidation");
			Date svMatchDate = sv.validateSeniorCustEvalDateIns(familyId1);
			//檢核銀行高齡評估量表維護日是否介於系統日-3個工作天~系統日區間
			if(svMatchDate == null || svMatchDate.before(beforeSysdateDate3) || svMatchDate.after(today)) {
				rtnString = "N"; 
			}
		}
		
		return rtnString;
	}
	
	/***
	 * 要保人高齡評估量表健康情況檢核
	 * @param paramMap
	 * @return	Y:人壽不需處理 
	 * 			N:人壽需處理 
	 * @throws Exception
	 */
	private String custHealthChk(GenericMap paramMap) throws Exception {
		String rtnString = "Y"; //非高齡或非本行客戶，不須處理
		//要保人ID
		String custId = paramMap.getNotNullStr("custId");
		
		//本行高齡客戶才做檢核
		if(isCustSenior) { 
			//要保人富壽健康情況
			ArrayList<String> insCustHealth = (ArrayList<String>) paramMap.get("custHealth");
			if(CollectionUtils.isEmpty(insCustHealth)) {
				throw new JBranchException("要保人高齡評估量表健康情況資料為空值");
			} else {
				rtnString = getBankInsHealthMapping(custId, insCustHealth);
			}
		}
		
		return rtnString;
	}
	
	/***
	 * 被保人高齡評估量表健康情況檢核
	 * @param paramMap
	 * @return	Y:人壽不需處理 
	 * 			N:人壽需處理 
	 * @throws Exception
	 */
	private String insuredHealthChk(GenericMap paramMap) throws Exception {
		String rtnString = "Y"; //非高齡或非本行客戶，不須處理
		//被保人ID
		String insuredId = paramMap.getNotNullStr("insuredId");
		
		//本行高齡客戶才做檢核
		if(chkCustSenior(insuredId)) { 
			//被保人富壽健康情況
			ArrayList<String> insuredHealth = (ArrayList<String>) paramMap.get("insuredHealth");
			if(CollectionUtils.isEmpty(insuredHealth)) {
				throw new JBranchException("被保人高齡評估量表健康情況資料為空值");
			} else {
				rtnString = getBankInsHealthMapping(insuredId, insuredHealth);
			}
		}
		
		return rtnString;
	}
	
	/***
	 * 繳款人高齡評估量表健康情況檢核
	 * @param paramMap
	 * @return	Y:人壽不需處理 
	 * 			N:人壽需處理 
	 * @throws Exception
	 */
	private String payerHealthChk(GenericMap paramMap) throws Exception {
		String rtnString = "Y"; //非高齡或非本行客戶，不須處理
		//繳款人ID
		String payerId = paramMap.getNotNullStr("payerId");

		//本行高齡客戶才做檢核
		if(chkCustSenior(payerId)) {
			//繳款人富壽健康情況
			ArrayList<String> payerHealth = (ArrayList<String>) paramMap.get("payerHealth");
			if(CollectionUtils.isEmpty(payerHealth)) {
				throw new JBranchException("繳款人高齡評估量表健康情況資料為空值");
			} else {
				rtnString = getBankInsHealthMapping(payerId, payerHealth);
			}
		}
		
		return rtnString;
	}
	
	/***
	 * 眷屬1高齡評估量表健康情況檢核
	 * @param paramMap
	 * @return	Y:人壽不需處理 
	 * 			N:人壽需處理 
	 * @throws Exception
	 */
	private String familyHealthChk1(GenericMap paramMap) throws Exception {
		String rtnString = "Y"; //非高齡或非本行客戶，不須處理
		//眷屬1
		String familyId1 = paramMap.getNotNullStr("familyId1");

		//本行高齡客戶才做檢核
		if(StringUtils.isNotBlank(familyId1) && chkCustSenior(familyId1)) {
			//眷屬1富壽健康情況
			ArrayList<String> familyHealth1 = (ArrayList<String>) paramMap.get("familyHealth1");
			if(CollectionUtils.isEmpty(familyHealth1)) {
				throw new JBranchException("眷屬1高齡評估量表健康情況資料為空值");
			} else {
				rtnString = getBankInsHealthMapping(familyId1, familyHealth1);
			}
		}
		
		return rtnString;
	}
	
	/***
	 * 要保人高齡評估量表金融認知檢核
	 * @param paramMap
	 * @return	Y:人壽不需處理 
	 * 			N:人壽需處理 
	 * @throws Exception
	 */
	private String custCognChk(GenericMap paramMap) throws Exception {
		String rtnString = "Y"; //非高齡或非本行客戶，不須處理
		//要保人ID
		String custId = paramMap.getNotNullStr("custId");
		
		//本行高齡客戶才做檢核
		if(isCustSenior) { 
			//要保人富壽金融認知
			String custCognition = paramMap.getNotNullStr("custCognition");
			rtnString = getBankInsCognitionMapping(custId, custCognition);
		}
		
		return rtnString;
	}
	
	/***
	 * 被保人高齡評估量表金融認知檢核
	 * @param paramMap
	 * @return	Y:人壽不需處理 
	 * 			N:人壽需處理 
	 * @throws Exception
	 */
	private String insuredCognChk(GenericMap paramMap) throws Exception {
		String rtnString = "Y"; //非高齡或非本行客戶，不須處理
		//被保人ID
		String insuredId = paramMap.getNotNullStr("insuredId");
		//要保人富壽金融認知
		String insuredCognition = paramMap.getNotNullStr("insuredCognition");
		//本行高齡客戶才做檢核
		if(chkCustSenior(insuredId)) { 
			rtnString = getBankInsCognitionMapping(insuredId, insuredCognition);
		}
		
		return rtnString;
	}
	
	/***
	 * 繳款人高齡評估量表金融認知檢核
	 * @param paramMap
	 * @return	Y:人壽不需處理 
	 * 			N:人壽需處理 
	 * @throws Exception
	 */
	private String payerCognChk(GenericMap paramMap) throws Exception {
		String rtnString = "Y"; //非高齡或非本行客戶，不須處理
		//繳款人ID
		String payerId = paramMap.getNotNullStr("payerId");
		//繳款人富壽金融認知
		String payerCognition = paramMap.getNotNullStr("payerCognition");	
		//本行高齡客戶才做檢核
		if(chkCustSenior(payerId)) {
			rtnString = getBankInsCognitionMapping(payerId, payerCognition);
		}
		
		return rtnString;
	}
	
	/***
	 * 眷屬1高齡評估量表金融認知檢核
	 * @param paramMap
	 * @return	Y:人壽不需處理 
	 * 			N:人壽需處理 
	 * @throws Exception
	 */
	private String familyCognChk1(GenericMap paramMap) throws Exception {
		String rtnString = "Y"; //非高齡或非本行客戶，不須處理
		//眷屬1
		String familyId1 = paramMap.getNotNullStr("familyId1");
		//眷屬1富壽金融認知
		String familyCognition1 = paramMap.getNotNullStr("familyCognition1");	
		//本行高齡客戶才做檢核
		if(StringUtils.isNotBlank(familyId1) && chkCustSenior(familyId1)) {
			rtnString = getBankInsCognitionMapping(familyId1, familyCognition1);
		}
		
		return rtnString;
	}
	
	/***
	 * 要保人高齡評估量表評估結果檢核
	 * @param paramMap
	 * @return	Y:人壽不需處理 
	 * 			N:人壽需處理 
	 * @throws Exception
	 */
	private String custEvalRsltChk(GenericMap paramMap) throws Exception {
		String rtnString = "Y"; //非高齡或非本行客戶，不須處理
		//要保人ID
		String custId = paramMap.getNotNullStr("custId");
		//要保人富壽評估結果
		String custSeniorResult = paramMap.getNotNullStr("custSeniorResult");
		//本行高齡客戶才做檢核
		if(isCustSenior) { 
			rtnString = getBankInsResultMapping(custId, custSeniorResult);
		}
		
		return rtnString;
	}
	
	/***
	 * 被保人高齡評估量表評估結果檢核
	 * @param paramMap
	 * @return	Y:人壽不需處理 
	 * 			N:人壽需處理 
	 * @throws Exception
	 */
	private String insuredEvalRsltChk(GenericMap paramMap) throws Exception {
		String rtnString = "Y"; //非高齡或非本行客戶，不須處理
		//被保人ID
		String insuredId = paramMap.getNotNullStr("insuredId");
		//被保人富壽評估結果
		String insuredSeniorResult = paramMap.getNotNullStr("insuredSeniorResult");
		//本行高齡客戶才做檢核
		if(chkCustSenior(insuredId)) { 
			rtnString = getBankInsResultMapping(insuredId, insuredSeniorResult);
		}
		
		return rtnString;
	}
	
	/***
	 * 繳款人高齡評估量表評估結果檢核
	 * @param paramMap
	 * @return	Y:人壽不需處理 
	 * 			N:人壽需處理 
	 * @throws Exception
	 */
	private String payerEvalRsltChk(GenericMap paramMap) throws Exception {
		String rtnString = "Y"; //非高齡或非本行客戶，不須處理
		//繳款人ID
		String payerId = paramMap.getNotNullStr("payerId");
		//繳款人富壽評估結果
		String payerSeniorResult = paramMap.getNotNullStr("payerSeniorResult");
		//本行高齡客戶才做檢核
		if(chkCustSenior(payerId)) { 
			rtnString = getBankInsResultMapping(payerId, payerSeniorResult);
		}
		
		return rtnString;
	}
	
	
	
	/***
	 * 眷屬1高齡評估量表評估結果檢核
	 * @param paramMap
	 * @return	Y:人壽不需處理 
	 * 			N:人壽需處理 
	 * @throws Exception
	 */
	private String familyEvalRsltChk1(GenericMap paramMap) throws Exception {
		String rtnString = "Y"; //非高齡或非本行客戶，不須處理
		//眷屬1ID
		String familyId1 = paramMap.getNotNullStr("familyId1");
		//眷屬1富壽評估結果
		String familySeniorResult1 = paramMap.getNotNullStr("familySeniorResult1");
		//本行高齡客戶才做檢核
		if(StringUtils.isNotBlank(familyId1) && chkCustSenior(familyId1)) { 
			rtnString = getBankInsResultMapping(familyId1, familySeniorResult1);
		}
		
		return rtnString;
	}
	
	/***
	 * 北富銀健康情況與富壽健康情況是否對應
	 * @param custId
	 * @param insHealthyAry 富壽健康情況Array
	 * @return	Y: 檢核通過 (參數有對應值)
	 * 			N: 檢核不通過
	 * @throws Exception
	 */
	private String getBankInsHealthMapping(String custId, List<String> insHealthyAry) throws Exception {
		String rtnString = "Y"; //檢核通過

		//北富銀高齡評估表健康情況
		SeniorValidation sv = (SeniorValidation) PlatformContext.getBean("SeniorValidation");
		List<String> bankHealthyAry = sv.getSeniorCustHealthDtlIns(custId);
		
		for(String insHealth : insHealthyAry) { //富壽健康情況選項
			String insHealthNo = insHealth.trim();
			for(String bankHealth : bankHealthyAry) { //北富銀健康情況選項
				if(StringUtils.isNotBlank(bankHealth)) {
					String bankHealthNo = bankHealth.trim().substring(0, 1); //取第一碼題號
					//北富銀健康情況與富壽檢康情況是否對應
					if(!isParamMapping("IOT.SENIOR_HEALTH_MAPPING", bankHealthNo.concat(insHealthNo))) {
						//沒有找到參數值對應，富壽須處理
						rtnString = "N";
					}
				}
			}
		}
		
		return rtnString;
	}
	
	/***
	 * 北富銀金融認知與富壽金融認知是否對應
	 * @param custId
	 * @param insCognition 富壽金融認知選項
	 * @return	Y: 檢核通過 (參數有對應值)
	 * 			N: 檢核不通過
	 * @throws Exception
	 */
	private String getBankInsCognitionMapping(String custId, String insCognition) throws Exception {
		String rtnString = "Y"; //檢核通過
		//北富銀高齡評估表金融認知
		SeniorValidation sv = (SeniorValidation) PlatformContext.getBean("SeniorValidation");
		List<String> bankCognAry = sv.getSeniorCustCognitionDtlIns(custId);
		
		for(String bankCogn : bankCognAry) { //北富銀金融認知選項
			if(StringUtils.isNotBlank(bankCogn)) {
				String bankCognNo = bankCogn.trim().substring(0, 1); //取第一碼題號
				//北富銀金融認知與富壽金融認知是否對應
				if(!isParamMapping("IOT.SENIOR_COGNITION_MAPPING", bankCognNo.concat(insCognition.trim()))) {
					//沒有找到參數值對應，富壽須處理
					rtnString = "N";
				}
			}
		}
		
		return rtnString;
	}
	
	/***
	 * 北富銀金融認知與富壽高齡評估量表結果是否對應
	 * @param custId
	 * @param insResult
	 * @return	Y: 檢核通過
	 * 			N: 檢核不通過
	 * @throws Exception
	 */
	private String getBankInsResultMapping(String custId, String insResult) throws Exception {
		String rtnString = "Y"; //檢核通過
		//北富銀高齡評估表金融認知
		SeniorValidation sv = (SeniorValidation) PlatformContext.getBean("SeniorValidation");
		
		//true：能力表現是否填答1~7項
		boolean ability17 = sv.validSeniorCust01BChkBuy(custId, "IOT");
		//true:金融認知結果=4沒有上述情形 &&能力表現填答8
		boolean chk1 = sv.validSeniorCust02FAns4(custId, "IOT") && !ability17;
		//true：能力表現填答1~7項 或 金融認知填答第一選項
		boolean chk2 = ability17 || sv.validSeniorCust01FChkBuy(custId, "IOT");
		//true：有勾選金融認知中第二或第三個選項
		boolean chk3 = sv.validSeniorCust02FNoBuy(custId, "IOT");
		
		//富壽結果1:具有辨識能力；北富銀結果金融認知能力表現有填答不具辨識選項
		if(insResult.matches("1") && !chk1) rtnString = "N";
		//富壽結果2:不具有辨識能力；北富銀結果金融認知能力表現填答為具辨識能力
		if(insResult.matches("2") && !(chk2 || chk3)) rtnString = "N";
		
		return rtnString;
	}
	
	/***
	 * 取得要保人資料
	 * @param custId
	 * @throws Exception
	 */
	private void getCustInfo(String custId) throws Exception {
		SOT701InputVO sot701inputVO = new SOT701InputVO();
		sot701inputVO.setCustID(custId.toUpperCase());
		SOT701 sot701 = (SOT701) PlatformContext.getBean("sot701");
		//取得要保人KYC資料
		custKYCDataVO = sot701.getCustKycData(sot701inputVO);
		//取得要保人客戶註記
		fp032675DataVO = sot701.getFP032675Data(sot701inputVO);
		//要保人是否為高齡(>=64.5歲)
		isCustSenior = chkCustSenior(custId);
	}

	/***
	 * 要保人是否>=64.5歲
	 * @param custBirthday
	 * @return true: >=  false: <
	 * @throws JBranchException 
	 */
	private boolean chkCustSenior(String custId) throws JBranchException {
		String queryStr = "SELECT BIRTH_DATE FROM TBCRM_CUST_MAST WHERE CUST_ID = :custId ";		
		List<Map<String, Object>> cList = getDataAccessManager().exeQuery(genDefaultQueryConditionIF().setQueryString(queryStr).setObject("custId" , custId.toUpperCase()));
		if(CollectionUtils.isNotEmpty(cList) && cList.get(0).get("BIRTH_DATE") != null) {
			Date custBirthday = (Date) cList.get(0).get("BIRTH_DATE");
			Calendar cal = Calendar.getInstance();
		    cal.setTime(new Date());//系統日往前推64.5歲
		    cal.add(Calendar.YEAR, -64);
		    cal.add(Calendar.MONTH, -6);

		    return (custBirthday.after(cal.getTime())) ? false : true;
		} else {
			 return false; //非本行客戶不處理
			//throw new JBranchException("查無此要保人客戶資料");
		}
	}
	
	/***
	 * 是否為本行客戶
	 * @param custId
	 * @return true:本行客戶 false:非本行客戶
	 * @throws JBranchException
	 */
	private boolean isCustInBank(String custId) throws JBranchException {
		String queryStr = "SELECT 1 FROM TBCRM_CUST_MAST WHERE CUST_ID = :custId ";		
		List<Map<String, Object>> cList = getDataAccessManager().exeQuery(genDefaultQueryConditionIF().setQueryString(queryStr).setObject("custId" , custId.toUpperCase()));
		return CollectionUtils.isNotEmpty(cList) ? true : false;
	}
	
	/***
	 * 取得高齡客戶可購買標的商品P值
	 * @param custId
	 * @param applyDate
	 * @return "":非高齡 P3:高齡專投或高齡非專投但符合條件 P2:其他
	 * @throws Exception
	 */
	private String getSeniorCustPVal(String custId, Date applyDate) throws Exception {
		//要保人為高齡客戶(年齡>=64.5歲)購買投資型商品，檢核KYC
		String seniorPval = "";
		if(isCustSenior) {
			seniorPval = "P3"; //高齡預設可買P3商品
			if(!StringUtils.equals("Y", fp032675DataVO.getCustProFlag())) {
				//要保人為高齡非專投，需檢核是否可買投資P3商品
				IOT110 iot110 = (IOT110) PlatformContext.getBean("iot110");
				IOT110InputVO inputVO110 = new IOT110InputVO();
				inputVO110.setCUST_ID(custId);
				inputVO110.setREG_TYPE("1");
				inputVO110.setAPPLY_DATE(new Timestamp(applyDate.getTime()));
				seniorPval = iot110.validSeniorInvP2(inputVO110, custKYCDataVO) ? "P3" : "P2";
			}			
		}
		
		return seniorPval;
	}
	
	/***
	 * 商品標的是否可申購
	 * @param targetId
	 * @param targetList
	 * @return true:可申購 false:不可申購
	 */
	private boolean isTargetInList(String targetId, List<Map<String, Object>> targetList) {
		for(Map<String, Object> map : targetList) {
			//P值符合可申購
			if(StringUtils.equals(targetId.trim(), map.get("TARGET_ID").toString())) return true;
		}
		//都不符合
		return false;
	}
	
	/***
	 * KYC收入檢核
	 * @param custId
	 * @param applyDate
	 * @param insIncome
	 * @return 	Y:  無KYC收入
	 * 			Y:  人壽收入介於KYC問卷收入內
	 * 			N1: 人壽收入大於KYC問卷收入
	 * 			N2: 人壽收入小於KYC問卷收入
	 * @throws JBranchException
	 */
	private String getKYCIncomeChk(String custId, Date applyDate, BigDecimal insIncome) throws JBranchException {
		String rtnString = "Y";
		
		IOT920 iot920 = (IOT920) PlatformContext.getBean("iot920");
		List<Map<String, Object>> q2AnsInfo = iot920.getKycQ2AnswerInfo(custId, applyDate);
		if(CollectionUtils.isNotEmpty(q2AnsInfo)) {
			//取得答案序號
			int ansSeq = ((BigDecimal)q2AnsInfo.get(0).get("ANSWER_SEQ")).intValue();
			//KYC收入下限
			BigDecimal lowerLimit = new BigDecimal(getParamName("IOT.API_KYC_Q2_ANS", String.valueOf(ansSeq-1)));
			//KYC收入上限
			BigDecimal upperLimit = new BigDecimal(getParamName("IOT.API_KYC_Q2_ANS", String.valueOf(ansSeq)));
			//人壽收入介於KYC收入區間
			if(insIncome.compareTo(lowerLimit) < 0) {
				rtnString = "N2";
			} else if(insIncome.compareTo(upperLimit) >= 0) {
				rtnString = "N1";
			}
		}
		
		return rtnString;
	}
	
	/***
	 * 取得客戶所有帳號資料，檢核傳入帳號是否存在
	 * @param custId
	 * @param account
	 * @return Y:帳號存在，檢核通過  N:帳號不存在，檢核不通過
	 * @throws Exception
	 */
	private String getAccountChk(String custId, String account) throws Exception {
		SOT701 sot701 = (SOT701) PlatformContext.getBean("sot701");
		SOT701InputVO inputVO701 = new SOT701InputVO();
		inputVO701.setCustID(custId);
		inputVO701.setProdType("1");
		CustAcctDataVO acctVO = sot701.getCustAcctData(inputVO701);
		
		//取得所有帳號
		List<String> acctList = new ArrayList<String>();
		for(AcctVO avo : acctVO.getTrustAcctList()) {
			if(!acctList.contains(avo.getAcctNo())) acctList.add(avo.getAcctNo());
		}
		for(AcctVO avo : acctVO.getCreditAcctList()) {
			if(!acctList.contains(avo.getAcctNo())) acctList.add(avo.getAcctNo());
		}
		for(AcctVO avo : acctVO.getDebitAcctList()) {
			if(!acctList.contains(avo.getAcctNo())) acctList.add(avo.getAcctNo());
		}
		
		return acctList.contains(account) ? "Y" : "N";
	}
	
	/***
	 * 由身份證字號取得員編
	 * @param custId
	 * @return
	 * @throws DAOException
	 * @throws JBranchException
	 */
	private String getEmpId(String custId) throws DAOException, JBranchException {
		List<Map<String , Object>> empList = getEmpInfo(custId);
		if(CollectionUtils.isEmpty(empList)) {
			return "";
		}

		return ObjectUtils.toString(empList.get(0).get("EMP_ID"));
	}
	
	/***
	 * 取得參數值
	 * @param paramType
	 * @param paramCode
	 * @return paramName
	 * @throws DAOException
	 * @throws JBranchException
	 */
	private String getParamName(String paramType, String paramCode) throws DAOException, JBranchException {
		String paramName = "0";		
		String queryStr = "SELECT PARAM_NAME FROM TBSYSPARAMETER WHERE PARAM_TYPE = :paramType AND PARAM_CODE = :paramCode ";		
		List<Map<String, Object>> list = getDataAccessManager().exeQuery(genDefaultQueryConditionIF().setQueryString(queryStr)
				.setObject("paramType" , paramType).setObject("paramCode" , paramCode));
		if(CollectionUtils.isNotEmpty(list)) {
			paramName = ObjectUtils.toString(list.get(0).get("PARAM_NAME"));
		}

		return paramName;
	}
	
	/***
	 * 北富銀以及富壽參數值是否有對應
	 * @param paramType
	 * @param paramCode: 第一碼北富銀參數值; 第二碼富壽參數值
	 * @return true:有對應，檢核通過  false:沒有找到對應，檢核不通過
	 * @throws DAOException
	 * @throws JBranchException
	 */
	private boolean isParamMapping(String paramType, String paramCode) throws DAOException, JBranchException {
		String queryStr = "SELECT 1 FROM TBSYSPARAMETER WHERE PARAM_TYPE = :paramType AND PARAM_CODE = :paramCode ";		
		List<Map<String, Object>> list = getDataAccessManager().exeQuery(genDefaultQueryConditionIF().setQueryString(queryStr)
				.setObject("paramType" , paramType).setObject("paramCode" , paramCode));

		return CollectionUtils.isNotEmpty(list) ? true : false;
	}

}
