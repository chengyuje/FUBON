package com.systex.jbranch.app.server.fps.kycoperation;

import com.systex.jbranch.app.common.fps.table.*;
import com.systex.jbranch.app.server.fps.kyc310.KYC310;
import com.systex.jbranch.app.server.fps.kyc310.KYC310InputVO;
import com.systex.jbranch.app.server.fps.kyc310.KYC310OutputVO;
import com.systex.jbranch.app.server.fps.kyc311.KYC311;
import com.systex.jbranch.app.server.fps.kyc311.KYC311InputVO;
import com.systex.jbranch.app.server.fps.sot701.*;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.fubon.commons.cbs.CBSService;
import com.systex.jbranch.fubon.commons.cbs.vo.basic.CBSUtilOutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.fp032151.FP032151OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.fp032675.FP032675OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.wms032154.WMS032154OutputDetailsVO;
import com.systex.jbranch.fubon.webservice.rs.KycOperation;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.DateUtil;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.server.info.XmlInfo;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Component("kycoperation")
@Scope("request")
public class KycOperationJava extends FubonWmsBizLogic {
	enum FC032151 {
		EDUCATION, CAREER, MARRAGE, CHILDNO, BDAY;
	} // 改用FP032151撈資料

	enum FC032675{ CUST_PRO_FLAG, CUST_PRO_DATE, SICK_TYPE, DEGRADE, VUL_FLAG, DEGRADE_DATE}
	enum SEND_390{ PERSONAL_MAP, FINAL_RISK, SEQ, CUST_ID, USER_ID, EXPIRY_DATE}

	private static Logger logger = LoggerFactory.getLogger(KycOperationJava.class);

	@Autowired
	private CBSService cbsservice;

	@Autowired
	@Qualifier("kyc310")
	private KYC310 kyc310;

	@Autowired@Qualifier("kyc311")
	private KYC311 kyc311;

	@Autowired@Qualifier("sot701")
	private SOT701 sot701;

	@Autowired@Qualifier("KycOperationDao")
	private KycOperationDao kycOperationDao;
	
	@Autowired
	private CBSService cbsService;
	
	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.REQUIRES_NEW , rollbackFor = {APException.class , JBranchException.class , Exception.class})
	public Map<String, Object> getKycValue(Object body , TBKYC_LOG tbkycLog) throws Exception {
		Map<String , Object> cacheMap = null;//暫存Map
		List<Map<String, Object>> questionnaireList 	 = null;//題目加答案
		List<Map<String, Object>> personalIformationList = null;
		Map<String, Object>		  personalMap			 = null;
		List<Map<String, Object>> lastDataList			 = null;
		List<Map<String, Object>> riskNameList 			 = null;
		Map<String , Object> 	  kycScoreMap			 = null;
		Map<String, Object> 	  resultMap 			 = new HashMap<String, Object>();
		List<Map<String, Object>> aoBranch 			 	 = new ArrayList<>();

		KycOperationJavaInputVO inputVO = (KycOperationJavaInputVO) body;
		String[] generalQuestionArray 	= inputVO.getGeneralQuestion().split(";");//傳入題目序號
		String lastCustRiskBef 			= null;		//上一次的風險屬性
		Date lastCdate 					= null;		//上一次做問卷的時間
		boolean isLastWait				= false;	//是否上次為待審的案件
		boolean isLastNotWebWait		= false;	//是否為非網銀的待審的案件

		// 因為這是網銀  所以沒有 login id , 999: 網銀
		String investBranchNbr 			= "999";
		String loginID 			 		= inputVO.getUserID();
		String AO_Code 			 		= new String();
		String custBranchNbr 	 		= new String();
		String seq 						= getVersion();//取號
		String examVersion 				= null;	//問卷版本
		float  riskRange				= 0;	//分數
		String cValue 					= null;	//原C值
		String sicktype 				= null;	//重大疾病
		String eduction 				= null;	//學歷
		String riskAfr 					= null;	//降等後的Ｃ值
		String cValueRisk 				= null;	//有降等取降等後的，若無取原值
		String expiry_date				= null;	//KYC截止日
		String degrade_date				= null;	//免降等註記到期日
		String cust_pro_date			= null;	//專業投資人到期日

		//若尚未建立客戶主檔需補建立
		if(CollectionUtils.isEmpty(kycOperationDao.qeuryCustMastForCustId(inputVO.getCustID())))
			sendSotToAddCust(inputVO.getCustID());

		//取前次資料
		if(CollectionUtils.isNotEmpty(lastDataList = kycOperationDao.queryLastData(inputVO.getCustID().toUpperCase()))){
			Map<String, Object> lastData = lastDataList.get(0);
			isLastWait 		 = "01".equals(lastData.get("STATUS"));
			isLastNotWebWait = isLastWait && !"999".equals(lastData.get("INVEST_BRANCH_NBR"));
			lastCustRiskBef  = (String)lastData.get("CUST_RISK_AFR");//上次測驗的風險屬性
			lastCdate 		 = (Date)lastDataList.get(0).get("CREATE_DATE");//上一次問卷填寫完成時間
		}

		//有待審的資料：kyc主檔中這個客戶有status = 01的資料
		if(isLastWait)
			cacheMap = excuteWaitReviewMast(inputVO);
			//沒有待審資料
		else
			cacheMap = excuteNotWaitReviewMast(inputVO);

		//學歷、職業、婚姻、子女數、重大傷病註記、電話、地址、EMAIL、生日
		personalIformationList = (List<Map<String, Object>>)cacheMap.get("PERSONAL_IFORMATION_LIST");
		personalMap	= personalIformationList.get(0);
		questionnaireList = (List<Map<String, Object>>)cacheMap.get("QUESTIONNAIRE_LIST");//問卷題目

		//計算C值
		kycScoreMap  	= kycScore(cacheMap  , inputVO);
		examVersion  	= (String) questionnaireList.get(0).get("EXAM_VERSION");	//問卷版本
		riskRange 	 	= (float)kycScoreMap.get("RISKRANGE");						//分數
		cValue 			= (String)kycScoreMap.get("RISK_LEVEL");					//原C值
		sicktype 	 	= (String)kycScoreMap.get("SICKTYPE");						//重大疾病
		eduction 	 	= (String)kycScoreMap.get("EDUCTION");						//學歷
		riskAfr 	 	= (String)kycScoreMap.get("RISK_AFR");						//降等後的Ｃ值
		cValueRisk 		= StringUtils.isBlank(riskAfr) ? cValue : riskAfr;			//有降等取降等後的，若無取原值
		expiry_date	 	= (String)kycScoreMap.get("EXPIRY_DATE");					//KYC截止日
		degrade_date 	= (String)kycScoreMap.get("DEGRADE_DUE_DATE");				//免降等註記到期日
		cust_pro_date	= (String)kycScoreMap.get("PRO_DUE_DATE");					//專業投資人到期日
		
		//埋log訊息
		tbkycLog.setCustEductionAfter(eduction);
		tbkycLog.setCustRiskAfr(cValueRisk);// 降等後C值
		tbkycLog.setCustHealthAfter(sicktype);
		tbkycLog.setCustMarriageAfter(ObjectUtils.toString(personalMap.get("MARRAGE")));
		tbkycLog.setCustChildrenAfter(ObjectUtils.toString(personalMap.get("CHILD_NO")));
		tbkycLog.setCustCareerAfter(ObjectUtils.toString(personalMap.get("CAREER")));
		tbkycLog.setRiskrange(new BigDecimal(riskRange));

		resultMap.put(SEND_390.PERSONAL_MAP.name(), personalMap);
		resultMap.put(SEND_390.FINAL_RISK.name(), cValueRisk);
		resultMap.put(SEND_390.SEQ.name(), seq);
		resultMap.put(SEND_390.CUST_ID.name(), inputVO.getCustID());
		resultMap.put(SEND_390.USER_ID.name(), inputVO.getUserID());
		resultMap.put("EXPIRY_DATE" , expiry_date);

		//取C值名稱
		riskNameList = kycOperationDao.queryRiskName(examVersion , cValueRisk);
		if(CollectionUtils.isNotEmpty(riskNameList)) {
			resultMap.put("CNAME", ObjectUtils.toString(riskNameList.get(0).get("RL_NAME")));
		}

		resultMap.put("CDATA", cValueRisk);
		//試算時才會回，非試算回空值
		resultMap.put("CDATA_UP", "");
		resultMap.put(SEND_390.USER_ID.name() , inputVO.getUserID());

		logInfo(inputVO.getCustID() , "CDATA"  , cValueRisk);

		//檢查是否需要進入冷靜期
		KYC311InputVO kyc311InputVO = new KYC311InputVO();
		kyc311InputVO.setCUST_ID(inputVO.getCustID());
		kyc311InputVO.setCUST_RISK_AFR(cValueRisk);
		kyc311InputVO.setSEQ(seq);
		kyc311InputVO.setBranch(true);	//網銀
		kyc311InputVO.setEXPIRY_DATE(expiry_date);

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		Map<String, String> coolingMap = kyc311.getCoolingPeriodRiskVal(kyc311InputVO);
		String coolingRiskVal = coolingMap.get("COOLING_RISK");
		Boolean isStillInCooling = StringUtils.equals("Y", coolingMap.get("STILL_IN_COOLING")) ? true : false;

		if(StringUtils.isNotBlank(coolingRiskVal)) { //需進入冷靜期
			//需要進入冷靜期,寫入冷靜期表格
			kyc311InputVO.setCUST_DATA_390(initTp032675Data(inputVO.getCustID() ,personalMap , cValueRisk));
			kyc311.updateKycCoolingPeriod(coolingRiskVal, isStillInCooling, kyc311InputVO);

			//進入冷靜期，C值為非當日最後一次C值
			//原來就在冷靜期中，則為原冷靜期時的C值
			List<Map<String, Object>> cRiskNameList = kycOperationDao.queryRiskName(examVersion , coolingRiskVal);
			resultMap.put("CoolingRiskID", coolingRiskVal);
			resultMap.put("CoolingRiskName", CollectionUtils.isNotEmpty(cRiskNameList) ? ObjectUtils.toString(cRiskNameList.get(0).get("RL_NAME")) : "");
			resultMap.put("CoolingEffDate", sdf.format(kyc311InputVO.getCOOLING_EFF_DATE()));

			//若為冷靜期，將冷靜期C值給cValueRisk
			cValueRisk = coolingRiskVal;
		} else {
			if(isStillInCooling) {	//原本在冷靜期中，取消冷靜期資料				
				kyc311.updateKycCoolingPeriod(coolingRiskVal, isStillInCooling, kyc311InputVO);
			}

			//不用進入冷靜期
			resultMap.put("CoolingRiskID", "");
			resultMap.put("CoolingRiskName", "");
			resultMap.put("CoolingEffDate", null);
		}

		resultMap.put(SEND_390.PERSONAL_MAP.name() , personalMap);
		resultMap.put(SEND_390.FINAL_RISK.name() , cValueRisk);
		resultMap.put(SEND_390.SEQ.name() , seq);
		resultMap.put(SEND_390.CUST_ID.name() , inputVO.getCustID());

		//送390
		send390(resultMap);

		//存在非網銀的待審的案件，將其結案，並標註為以重作KYC
		if(isLastNotWebWait){
			//前一筆臨櫃KYC資料尚未覆核，更新前一筆KYC為已覆核狀態，更新修改者為[已重做KYC]、SIGNOFF_ID=999
			//修改主檔狀態
			logInfo(inputVO.getCustID() , "修改上次主檔狀態" , "before");
			kycOperationDao.updateLastWaitForWeb(lastDataList.get(0).get("SEQ").toString() , inputVO.getCustID());
			logInfo(inputVO.getCustID() , "修改上次主檔狀態" , "after");
			//將上一次問卷寫入主檔歷程
			logInfo(inputVO.getCustID() , "新增上次主檔歷程" , "before");
			kycOperationDao.insertHisForMast(inputVO.getCustID());
			logInfo(inputVO.getCustID() , "新增上次主檔歷程" , "after");
			//將上一次問卷寫入明細歷程
			logInfo(inputVO.getCustID() , "新增上次明細歷程" , "after");
			kycOperationDao.insertHisForDetail(inputVO.getCustID());
			logInfo(inputVO.getCustID() , "新增上次明細歷程" , "after");
		}
		else {
			//取上一次的鑑機日期
			List<Map<String , Object>> lastMasHis03List = kycOperationDao.queryLastMastHisForStatus03(inputVO.getCustID());
			if(CollectionUtils.isNotEmpty(lastMasHis03List)){
				//上次鑑機日期
				lastCdate = (Date)lastMasHis03List.get(0).get("CREATE_DATE");
				//上次測驗的風險屬性
				lastCustRiskBef = (String)lastMasHis03List.get(0).get("CUST_RISK_AFR");
			}
		}

		//查負責此客戶的理專AO_CODE
		aoBranch = kycOperationDao.queryAoBranch(inputVO.getCustID());

		//判斷歸屬行
		if(CollectionUtils.isEmpty(aoBranch)){
			AO_Code 	  = null;
			custBranchNbr = investBranchNbr;
		}
		else{
			AO_Code 	  = ObjectUtils.toString(aoBranch.get(0).get("AO_CODE"));
			custBranchNbr = ObjectUtils.toString(aoBranch.get(0).get("BRA_NBR"));
		}

		logInfo(inputVO.getCustID() , "理專代碼" , AO_Code);
		logInfo(inputVO.getCustID() , "所屬分行" , custBranchNbr);

		//刪除明細
		logInfo(inputVO.getCustID() , "刪除明細" , "brefore");
		kycOperationDao.deleteDetail(inputVO.getCustID());
		logInfo(inputVO.getCustID() , "刪除明細" , "after");

		//刪除主檔
		logInfo(inputVO.getCustID() , "刪除主檔" , "brefore");
		kycOperationDao.deletelMast(inputVO.getCustID());
		logInfo(inputVO.getCustID() , "刪除主檔" , "after");

		// 新增主檔處理
		TBKYC_INVESTOREXAM_MVO mvo = new TBKYC_INVESTOREXAM_MVO();
		mvo.setSEQ(seq);
		mvo.setEXAM_VERSION(examVersion);
		mvo.setCUST_ID(inputVO.getCustID());
		mvo.setCUST_NAME((String)personalMap.get("CUST_NAME"));
		mvo.setAO_CODE(AO_Code);
		mvo.setCUST_BRANCH_NBR(custBranchNbr);
		mvo.setINVEST_BRANCH_NBR(investBranchNbr);
		mvo.setCUST_RISK_AFR(cValueRisk);
		mvo.setRISKRANGE(new BigDecimal(riskRange));
		mvo.setEMP_ID(inputVO.getUserID());
		mvo.setSTATUS("03");
		mvo.setSIGNOFF_ID(inputVO.getUserID());
		mvo.setSIGNOFF_DATE(new Timestamp(new Date().getTime()));
		mvo.setCreator(loginID);
		mvo.setCREATE_DATE(new Timestamp(new Date().getTime()));
		mvo.setCRR_ORI((String)kycScoreMap.get("CRR_ORI"));
		mvo.setCRR_MATRIX((String)kycScoreMap.get("CRR_MATRIX"));
		mvo.setSCORE_ORI_TOT(new BigDecimal((float)kycScoreMap.get("SCORE_ORI_TOT")));
		mvo.setSCORE_C(new BigDecimal((float)kycScoreMap.get("SCORE_C")));
		mvo.setSCORE_W(new BigDecimal((float)kycScoreMap.get("SCORE_W")));
		mvo.setSCORE_CW_TOT(new BigDecimal((float)kycScoreMap.get("SCORE_CW_TOT")));
		mvo.setRISK_LOSS_RATE(new BigDecimal((float)kycScoreMap.get("RISK_LOSS_RATE")));
		mvo.setRISK_LOSS_LEVEL((String)kycScoreMap.get("RISK_LOSS_LEVEL"));

		logInfo(inputVO.getCustID() , "RISK_AFR"  , cValueRisk);
		logInfo(inputVO.getCustID() , "RISKRANGE" , riskRange);
		logInfo(inputVO.getCustID() , "COOLING_RISK_VAL"  , coolingRiskVal);

		if(lastCdate != null)
			mvo.setTEST_BEF_DATE(new Timestamp(lastCdate.getTime()));

		if(lastCustRiskBef != null)
			mvo.setCUST_RISK_BEF(lastCustRiskBef);
				
		Timestamp expiryDate = null;
		if(StringUtils.isNotBlank(expiry_date)) {
			expiryDate = new Timestamp(sdf.parse(expiry_date).getTime());
		}
		mvo.setEXPIRY_DATE(expiryDate);

		//距上次問卷天數
		if(lastCdate != null){
			mvo.setDIFF_DAYS(new BigDecimal((DateUtil.replaceTime(new Date()).getTime() - DateUtil.replaceTime(lastCdate).getTime()) / 1000 / 60 / 60 / 24));
		} else {
			mvo.setDIFF_DAYS(new BigDecimal(0));
		}

		//如果有傳IP參數代表為分行公用電腦
		if(StringUtils.isNotBlank(inputVO.getIP()))
			mvo.setINVEST_IP(inputVO.getIP());//網銀承作KYC問卷時的電腦IP

		//新增主檔
		logInfo(inputVO.getCustID() , "新增主檔" , "before");
		kycOperationDao.getDataAccessManager().create(mvo);
		logInfo(inputVO.getCustID() , "新增主檔" , "after");

		//新增明細檔處理
		TBKYC_INVESTOREXAM_DVO dvo = new TBKYC_INVESTOREXAM_DVO();
		dvo.setSEQ(seq);
		dvo.setCUST_ID(inputVO.getCustID());
		dvo.setCUST_EDUCTION_AFTER(eduction);
		dvo.setCUST_CAREER_AFTER((String) personalMap.get("CAREER"));
		dvo.setCUST_MARRIAGE_AFTER((String) personalMap.get("MARRAGE"));
		dvo.setCUST_CHILDREN_AFTER((String) personalMap.get("CHILD_NO"));
		dvo.setCUST_HEALTH_AFTER(sicktype);
		dvo.setCUST_DEGRADE(ObjectUtils.toString(cacheMap.get(FC032675.DEGRADE.name())).trim());
		
		Timestamp degradDate = null;
		if(StringUtils.isNotBlank(degrade_date)) {
			degradDate = new Timestamp(sdf.parse(degrade_date).getTime());
		}
		dvo.setDEGRADE_DUE_DATE(degradDate);
		
		Timestamp custProDate = null;
		if(StringUtils.isNotBlank(cust_pro_date)) {
			custProDate = new Timestamp(sdf.parse(cust_pro_date).getTime());
		}
		dvo.setPRO_DUE_DATE(custProDate);
		
		String ans = tbkycLog.getAnswerTwo();
		dvo.setANSWER_2(ans);
		
		//高資產客戶註記
//		try {
			if(kycScoreMap.get("HNWC_DUE_DATE") != null) {
				dvo.setHNWC_DUE_DATE(new Timestamp(((Date)kycScoreMap.get("HNWC_DUE_DATE")).getTime()));
			}
			if(kycScoreMap.get("HNWC_INVALID_DATE") != null) {
				dvo.setHNWC_INVALID_DATE(new Timestamp(((Date)kycScoreMap.get("HNWC_INVALID_DATE")).getTime()));
			}
//		} catch(Exception e) {}

		logInfo(inputVO.getCustID() , "ANSWER_2" 		, ans);
		logInfo(inputVO.getCustID() , "EDUCTION_AFTER"  , eduction);
		logInfo(inputVO.getCustID() , "CAREER_AFTER" 	, personalMap.get("CAREER"));
		logInfo(inputVO.getCustID() , "MARRIAGE_AFTER"  , personalMap.get("MARRAGE"));
		logInfo(inputVO.getCustID() , "CHILDREN_AFTER"  , personalMap.get("CHILD_NO"));
		logInfo(inputVO.getCustID() , "HEALTH_AFTER" 	, sicktype);

		//新增明細檔
		logInfo(inputVO.getCustID() , "新增明細" , "before");
		kycOperationDao.getDataAccessManager().create(dvo);
		logInfo(inputVO.getCustID() , "新增明細" , "after");

		//新增給往行銀用的表
		logInfo(inputVO.getCustID() , "新增EXP" , "before");
		String[] ans_insert = ans.split(";");//答案
		for(int i = 0 ; i < ans_insert.length ; i++){
			TBKYC_INVESTOREXAM_D_EXPPK TIDE = new TBKYC_INVESTOREXAM_D_EXPPK();
			TBKYC_INVESTOREXAM_D_EXPVO TIDEVO = new TBKYC_INVESTOREXAM_D_EXPVO();
			TIDE.setPROFILE_TEST_ID(seq);
			TIDE.setEXAMID(examVersion);//問卷版本

			if(isNaturalPerson(inputVO.getCustID())){
				TIDE.setQCLASS("02");//自然人
			}else{
				TIDE.setQCLASS("03");//法人
			}

			TIDE.setQID(generalQuestionArray[i]);
			TIDEVO.setcomp_id(TIDE);
			TIDEVO.setQSEQUENCE(new BigDecimal(i + 1));
			TIDEVO.setANSWER(ans_insert[i].replaceAll(",", "|"));

			kycOperationDao.getDataAccessManager().create(TIDEVO);
		}
		logInfo(inputVO.getCustID() , "新增EXP" , "after");

		//寫入主檔歷程
		logInfo(inputVO.getCustID() , "新增主檔歷程" , "before");
		kycOperationDao.insertHisForMast(inputVO.getCustID());
		logInfo(inputVO.getCustID() , "新增主檔歷程" , "after");

		//寫入明細歷程
		logInfo(inputVO.getCustID() , "新增明細歷程" , "before");
		kycOperationDao.insertHisForDetail(inputVO.getCustID());
		logInfo(inputVO.getCustID() , "新增明細歷程" , "after");

		try{
			kyc311.updateKycNowStatus(inputVO.getCustID() , questionnaireList , cValueRisk , expiryDate);
		}
		catch(Exception ex){
			logInfo(inputVO.getCustID() , StringUtil.getStackTraceAsString(ex));
		}

		kycOperationDao.updateCreatorMofifer(
				Arrays.asList("TBKYC_INVESTOREXAM_M" , "TBKYC_INVESTOREXAM_D" , "TBKYC_INVESTOREXAM_M_HIS" , "TBKYC_INVESTOREXAM_D_HIS"),
				inputVO.getUserID(),  seq
		);

		return resultMap;
	}

	/***
	 * 試算，不存DB & 不上送390
	 *
	 * @param body
	 * @param tbkycLog
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	@Transactional(propagation = Propagation.REQUIRES_NEW , rollbackFor = {APException.class , JBranchException.class , Exception.class})
	public Map<String, Object> getKycValueCal(Object body , TBKYC_LOG tbkycLog) throws Exception {
		Map<String , Object> cacheMap = null;//暫存Map
		List<Map<String, Object>> questionnaireList 	 = null;//題目加答案
		List<Map<String, Object>> personalIformationList = null;
		Map<String, Object>		  personalMap			 = null;
		List<Map<String, Object>> lastDataList			 = null;
		List<Map<String, Object>> riskNameList 			 = null;
		Map<String , Object> 	  kycScoreMap			 = null;
		Map<String, Object> 	  resultMap 			 = new HashMap<String, Object>();

		KycOperationJavaInputVO inputVO = (KycOperationJavaInputVO) body;
		String lastCustRiskBef 			= null;//上一次的風險屬性
		boolean isLastWait				= false;//是否上次為待審的案件

		// 因為這是網銀  所以沒有 login id , 999: 網銀
		String seq 						= getVersion();//取號
		String examVersion 				= null;//問卷版本
		float  riskRange				= 0;//分數
		String cValue 					= null;//原C值
		String sicktype 				= null;//重大疾病
		String eduction 				= null;//學歷
		String riskAfr 					= null;//降等後的Ｃ值
		String cValueRisk 				= null;//有降等取降等後的，若無取原值
		String expiry_date = null;

		//若尚未建立客戶主檔需補建立
		if(CollectionUtils.isEmpty(kycOperationDao.qeuryCustMastForCustId(inputVO.getCustID())))
			sendSotToAddCust(inputVO.getCustID());

		//取前次資料
		if(CollectionUtils.isNotEmpty(lastDataList = kycOperationDao.queryLastData(inputVO.getCustID().toUpperCase()))){
			Map<String, Object> lastData = lastDataList.get(0);
			isLastWait 		 = "01".equals(lastData.get("STATUS"));
			lastCustRiskBef  = (String)lastData.get("CUST_RISK_AFR");//上次測驗的風險屬性
		}

		//有待審的資料：kyc主檔中這個客戶有status = 01的資料
		if(isLastWait)
			cacheMap = excuteWaitReviewMast(inputVO);
			//沒有待審資料
		else
			cacheMap = excuteNotWaitReviewMast(inputVO);

		//學歷、職業、婚姻、子女數、重大傷病註記、電話、地址、EMAIL、生日
		personalIformationList = (List<Map<String, Object>>)cacheMap.get("PERSONAL_IFORMATION_LIST");
		personalMap	= personalIformationList.get(0);
		questionnaireList = (List<Map<String, Object>>)cacheMap.get("QUESTIONNAIRE_LIST");//問卷題目

		//計算C值
		kycScoreMap  = kycScore(cacheMap  , inputVO);
		examVersion  = (String) questionnaireList.get(0).get("EXAM_VERSION");//問卷版本
		riskRange 	 = (float)kycScoreMap.get("RISKRANGE");		//分數
		cValue 		 = (String)kycScoreMap.get("RISK_LEVEL");	//原C值
		sicktype 	 = (String)kycScoreMap.get("SICKTYPE");		//重大疾病
		eduction 	 = (String)kycScoreMap.get("EDUCTION");		//學歷
		riskAfr 	 = (String)kycScoreMap.get("RISK_AFR");		//降等後的Ｃ值
		cValueRisk 	 = StringUtils.isBlank(riskAfr) ? cValue : riskAfr;//有降等取降等後的，若無取原值
		expiry_date	 = (String)kycScoreMap.get("EXPIRY_DATE");	//KYC截止日

		//埋log訊息
		tbkycLog.setCustEductionAfter(eduction);
		tbkycLog.setCustRiskAfr(cValueRisk);//降等後C值	//寫LOG時仍放本次計算出的C值，不管是否為冷靜期
		tbkycLog.setCustHealthAfter(sicktype);
		tbkycLog.setCustMarriageAfter(ObjectUtils.toString(personalMap.get("MARRAGE")));
		tbkycLog.setCustChildrenAfter(ObjectUtils.toString(personalMap.get("CHILD_NO")));
		tbkycLog.setCustCareerAfter(ObjectUtils.toString(personalMap.get("CAREER")));
		tbkycLog.setRiskrange(new BigDecimal(riskRange));

		resultMap.put(SEND_390.PERSONAL_MAP.name() , personalMap);
		resultMap.put(SEND_390.FINAL_RISK.name() , cValueRisk);
		resultMap.put(SEND_390.SEQ.name() , seq);
		resultMap.put(SEND_390.CUST_ID.name() , inputVO.getCustID());
		resultMap.put(SEND_390.USER_ID.name() , inputVO.getUserID());
		resultMap.put("EXPIRY_DATE" , expiry_date);

		//取C值名稱
		riskNameList = kycOperationDao.queryRiskName(examVersion , cValueRisk);

		resultMap.put("CDATA", cValueRisk);

		if(CollectionUtils.isNotEmpty(riskNameList))
			resultMap.put("CNAME", ObjectUtils.toString(riskNameList.get(0).get("RL_NAME")));

		//若本次評估結果較上次結果為高，送"Y"否則送"N"
		resultMap.put("CDATA_UP", "N");
		if(StringUtils.isNotBlank(lastCustRiskBef)) {
			try {
				int rBef = Integer.parseInt(lastCustRiskBef.substring(1));
				int rAfr = Integer.parseInt(cValueRisk.substring(1));

				if(rAfr > rBef) {
					resultMap.put("CDATA_UP", "Y");
				}
			} catch(Exception e) {

			}
		}

		tbkycLog.setPopup(resultMap.get("CDATA_UP").toString());

		//是否需填寫差異表 & 前一次KYC答案選項
		KYC310InputVO inputVO310 = new KYC310InputVO();
		inputVO310.setCUST_ID(inputVO.getCustID());
		inputVO310.setCUST_RISK_AFR(riskAfr);
		inputVO310.setFromWebYN("Y");
		Map<String, Object> compData = kyc310.getLastKYCComparisonData(inputVO310);
		//回傳差異表資料
		resultMap.put("NEED_COMPARISON_YN", ObjectUtils.toString(compData.get("NEED_COMPARISON_YN"))); //是否需填寫差異表
		resultMap.put("LAST_ANSWER_2", ObjectUtils.toString(compData.get("LAST_ANSWER_2"))); //放入前次KYC填答答案
		//寫Log訊息
		tbkycLog.setNEED_COMPARISON_YN(ObjectUtils.toString(compData.get("NEED_COMPARISON_YN")));
		tbkycLog.setLAST_ANSWER_2(ObjectUtils.toString(compData.get("LAST_ANSWER_2")));
		tbkycLog.setLAST_SEQ(ObjectUtils.toString(compData.get("LAST_SEQ"))); //比較差異的(上次)客戶風險評估問卷主鍵
		
		return resultMap;
	}

	@SuppressWarnings("unchecked")
	public void send390(Map<String, Object> resultMap) throws Exception{
		//上送390用
		FC032153DataVO fc032153datavo 	= new FC032153DataVO();
		KYC311InputVO kyc311inputVO   	= new KYC311InputVO();

		String cValueRisk = ObjectUtils.toString(resultMap.get(SEND_390.FINAL_RISK.name()));
		String expiryDate = ObjectUtils.toString(resultMap.get(SEND_390.EXPIRY_DATE.name()));
		String seq 		  = ObjectUtils.toString(resultMap.get(SEND_390.SEQ.name()));
		String custId 	  = ObjectUtils.toString(resultMap.get(SEND_390.CUST_ID.name()));
		String userId 	  = ObjectUtils.toString(resultMap.get(SEND_390.USER_ID.name()));
		Map<String , Object> personalMap  = (Map<String , Object>)resultMap.get(SEND_390.PERSONAL_MAP.name());
		
		SimpleDateFormat ft = new SimpleDateFormat("ddMMyyyy");
		
		//上送C值到TP032675主機	
		personalMap.put("seq", seq);
		personalMap.put("COMMENTARY_STAFF", userId);

		kyc311inputVO.setCUST_ID(custId);
		kyc311inputVO.setBasic_information(personalMap);
		kyc311inputVO.setBranch(true);

		// 電文67157用 C值
		kyc311inputVO.setCUST_RISK_AFR(cValueRisk);
		kyc311inputVO.setEXPIRY_DATE(cbsService.changeDate(expiryDate, 1));	//將yyyyMMdd轉為ddMMyyyy
//		kyc311inputVO.setKYC_TEST_DATE(ft.format(new Date()));				//將yyyyMMdd轉為ddMMyyyy
		kyc311inputVO.setKYC_TEST_DATE(ft.format(new SimpleDateFormat("yyyyMMddHHmmss").parse(cbsService.getCBSTestDate())));	//#1344
		// 網銀來的BRANCH給999供067157上送
		kyc311inputVO.setBRANCH("999");

		//上送KYC基本資料至390(學歷,婚姻,子女人數,職業,重大傷病)
		logger.info(fc032153datavo.getCustID() + "[" + seq + "]上送390CUST_DATA：" + (fc032153datavo.getCUST_DATA() == null ? "" : fc032153datavo.getCUST_DATA()));
		// List<ESBUtilOutputVO> esbOut =
		// getSot701().checkKycMiddleWare(fc032153datavo, null);
		if (cbsservice.isNaturalPerson(cbsservice.getCBSIDCode(kyc311inputVO.getCUST_ID()))) {
			// 更新email
			//sot701.kycUpdateEmail(kyc311inputVO);
			// 更新地址
			//sot701.kycUpdateAddress(kyc311inputVO);
			// 更新電話
			//sot701.kycUpdatePhone(kyc311inputVO);
		}

		// 更新基本資料
		//sot701.kycUpdateBasic(kyc311inputVO);
		// 更新C值
		sot701.kycUpdateCValue(kyc311inputVO);
		// 更新主管已覆核
		sot701.kycUpdateSupervisorCheck(kyc311inputVO);
		logger.info(fc032153datavo.getCustID() + "[" + seq + "]上送390CUST_DATA：end");
	}

	/**初始化上送390(風險屬性)的電文內容*/
	public String initTp032675Data(String custId , Map<String , Object> personalMap , String cValueRisk) throws JBranchException, ParseException{
		XmlInfo xmlInfo  	  = new XmlInfo();
		String CUST_DATA 	  = "                                          ";
		String CUST_RISK_AFR  = null;//風險值
		String KYC_TEST_DATE  = "00000000";//承作日期
		String EXPIRY_DATE 	  = "00000000";//有效月份
		String loginbranch 	  = "";//承作分行

		loginbranch = "999";

		loginbranch = loginbranch == null ? "000" : loginbranch;

		String EMP_ID;//承作員編
		String EDUCATION;//學歷
		String MARRAGE;//婚姻
		String CHILD_NO;//子女數
		String CAREER;//職業
		String SICK_TYPE_YN=" ";//重大傷病
		String SICK_TYPE;//重大傷病等級
		String Y_N_update;//是否更新

		//風險值
		if(cValueRisk != null){
			CUST_RISK_AFR = cValueRisk;
		}else{
			CUST_RISK_AFR = "  ";
		}

		//承作日期
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat sdf_toDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

		if(personalMap.get("KYC_TEST_DATE") != null){
			KYC_TEST_DATE = sdf.format(personalMap.get("KYC_TEST_DATE"));
		}else{
			if(personalMap.get("KYC_TEST_DATE_TEMP") != null){
				KYC_TEST_DATE = sdf.format(sdf_toDate.parse(personalMap.get("KYC_TEST_DATE_TEMP").toString()));
			}else{
				KYC_TEST_DATE = sdf.format(new Date());
			}
		}

		// FOR CBS測試日期修改
		// Date expiry_date = DateUtils.addDays(new Date(),
		// 364);//KYC截止日(加十二個月減一天)
		// Timestamp expiryDate = new Timestamp(expiry_date.getTime());
		// SimpleDateFormat sdf_month = new SimpleDateFormat("MM");
		// EXPIRY_DATE = "000000"+sdf_month.format(expiryDate);
		SimpleDateFormat ft = new SimpleDateFormat("yyyyMMddHHmmss");
		//Date testDate = DateUtils.addDays(ft.parse(cbsservice.getCBSTestDate()), 364);
		//KYC截止日(加一年減一天)
		Date testDate = DateUtils.addYears(ft.parse(cbsservice.getCBSTestDate()), 1);
		testDate = DateUtils.addDays(testDate, -1);
		SimpleDateFormat sdf_month = new SimpleDateFormat("MM");
		EXPIRY_DATE = "000000" + sdf_month.format(testDate);

		//承作員編
		if(StringUtils.equals("999", loginbranch)) {
			EMP_ID = KycOperation.USER_ID;
		} else {
			if(personalMap.get("COMMENTARY_STAFF") != null){
				EMP_ID = personalMap.get("COMMENTARY_STAFF").toString();
			}else{
				EMP_ID = "      ";
			}
		}

		//學歷
		if(personalMap.get("EDUCATION")!=null){
			String EDUCATION_Change = xmlInfo.getVariable("KYC.EDUCATION", personalMap.get("EDUCATION").toString(), "F3");
			if(EDUCATION_Change.length()>2){
				EDUCATION_Change = EDUCATION_Change.substring(0, 2);
			}

			QueryConditionIF qc = getDataAccessManager().getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuilder sb = new StringBuilder();
			sb.append(" select PARAM_CODE from TBSYSPARAMETER where PARAM_TYPE ='KYC.EDUCATION_UP' and PARAM_NAME like :education_name ");
			qc.setObject("education_name", "%"+EDUCATION_Change+"%");
			qc.setQueryString(sb.toString());
			List<Map<String,Object>> educationList = getDataAccessManager().exeQueryWithoutSort(qc);

			if(educationList.size()>0){
				EDUCATION = educationList.get(0).get("PARAM_CODE").toString();
			}
			else{
				EDUCATION = personalMap.get("EDUCATION").toString();
			}
		}else{
			EDUCATION = " ";
		}

		//婚姻
		if(personalMap.get("MARRAGE") != null){
			MARRAGE = personalMap.get("MARRAGE").toString();
		}else{
			MARRAGE = " ";
		}

		//子女數
		if(personalMap.get("CHILD_NO") != null){
			if("0".equals(personalMap.get("CHILD_NO").toString())){
				CHILD_NO = "5";
			}else{
				CHILD_NO = personalMap.get("CHILD_NO").toString();
			}
		}else{
			CHILD_NO = " ";
		}

		//職業
		if(personalMap.get("CAREER") != null){
			if(personalMap.get("CAREER").toString().length() < 2){
				CAREER = personalMap.get("CAREER").toString() + " ";
			}
			else{
				CAREER = personalMap.get("CAREER").toString();
			}
		}else{
			CAREER = "  ";
		}

		//重大傷病及重大傷病等級
		if(personalMap.get("SICK_TYPE") != null){
			SICK_TYPE = personalMap.get("SICK_TYPE").toString();

			if(!"1".equals(SICK_TYPE)){
				SICK_TYPE_YN = "Y";
			}
		}
		else{
			SICK_TYPE = " ";
		}

		//法人
		if(!isNaturalPerson(custId)){
			CUST_DATA = CUST_RISK_AFR + KYC_TEST_DATE + EXPIRY_DATE + loginbranch + EMP_ID + "Y";
		}
		//自然人
		else {
			if(personalMap.get("Y_N_update") != null){
				BigInteger Y_N_update_change = new BigDecimal(personalMap.get("Y_N_update").toString()).toBigInteger();
				Y_N_update = Y_N_update_change.toString();
			}else{
				Y_N_update = "1";
			}

			switch (Y_N_update) {
				case "1":
					CUST_DATA = CUST_RISK_AFR + KYC_TEST_DATE + EXPIRY_DATE + loginbranch + EMP_ID + "Y";
					break;
				case "2":
					CUST_DATA = CUST_RISK_AFR + KYC_TEST_DATE + EXPIRY_DATE + loginbranch + EMP_ID + "Y" +
							EDUCATION + MARRAGE + CHILD_NO + CAREER + SICK_TYPE_YN + SICK_TYPE;
					break;
				default:
					break;
			}
		}
		return CUST_DATA;
	}

	/**
	 * 待審核資料處理
	 *
	 * @throws Exception **/
	public Map excuteWaitReviewMast(KycOperationJavaInputVO inputVO) throws Exception{
		List<Map<String, Object>> personalIformation = null;// 抓KYC主檔 + KYC明細檔 +
		// 客戶主檔
		List<Map<String, Object>> questList = null;//問卷資料、問卷題目
		Map<String , Object> cacheMap = new HashMap<String , Object>();//result

		//找不到做過這個問卷紀錄的客戶資料或明細
		if(CollectionUtils.isEmpty(personalIformation = kycOperationDao.qeuryWaitReviewMastDetail(inputVO.getCustID().toUpperCase()))){
			logInfo(inputVO.getCustID() , "Exception" , "kyc上次待審的主檔、明細檔、客戶主檔無法對應");
			throw new Exception("kyc上次待審的主檔、明細檔、客戶主檔無法對應");
		}

		//發電文FC032151學歷、職業、婚姻、子女數、FC032675重大傷病註記、FC032675電話、地址、EMAIL、生日
		initPpersonalIformationForFcData(cacheMap , inputVO.getCustID() , personalIformation);

		//第二部分答案(存於detail的)
		String ans = ObjectUtils.toString(personalIformation.get(0).get("ANSWER_2"));
		String[] ansSplit = ans.split(";");//第二部分答案(每一題會用;分割)

		//取最接近系統日狀態為02的問卷資料、問卷題目
		questList = kycOperationDao.queryQuestion(inputVO.getCustID());

		//找不到則拋錯
		if(CollectionUtils.isEmpty(questList))
			throw new Exception("questList is null");

		for(int a = 0; a < questList.size(); a++) {
			//問卷版本
			String examVersion = ObjectUtils.toString(questList.get(a).get("EXAM_VERSION"));
			//題目序號
			String questionVersion = ObjectUtils.toString(questList.get(a).get("QUESTION_VERSION"));
			//此題答案與權重
			List<Map<String, Object>> answerList = kycOperationDao.queryAnswer(examVersion , questionVersion);

			//找出客戶於這一題所選的答案
			if(StringUtils.isNotBlank(ansSplit[a])){
				String[] ansSplit2 = ansSplit[a].split(",");//複選題會用,區隔

				for(Map<String, Object> answerMap : answerList){
					for(String ans2 : ansSplit2){
						if(answerMap.get("ANSWER_SEQ").toString().equals(ans2.trim())){
							answerMap.put("select", true);//如果與答案序號相同，代表有勾選該題答案，標記為true
						}
					}
				}
			}
			questList.get(a).put("ANSWER_LIST" , answerList);//標記完客戶處理的資料後，將答案暫存到題目中
		}

		//風險級距
		String rlVersion = ObjectUtils.toString(questList.get(0).get("RL_VERSION"));//風險級距編號
		cacheMap.put("RISK_LIST", kycOperationDao.queryLevel(rlVersion));//risk　風險級距(抓分數)
		cacheMap.put("QUESTIONNAIRE_LIST", questList);//問卷資料、問卷題目

		return cacheMap;
	}


	/**沒有上一次的待審核資料處理**/
	public Map excuteNotWaitReviewMast(KycOperationJavaInputVO inputVO) throws Exception{
		//個人基本資料
		List<Map<String, Object>> personalIformation = null;
		//暫存物件
		Map<String , Object> cacheMap = new HashMap<String , Object>();
		//最接近系統日狀態為02的問卷資料、問卷題目
		List<Map<String, Object>> questList = null;

		//取題目、答案
		//用客戶id取得取最接近系統日狀態為02的問卷資料、問卷題目，找不到則拋錯
		if(CollectionUtils.isEmpty(questList = kycOperationDao.queryQuestion(inputVO.getCustID())))
			throw new Exception("questList is null");

		//逐題抓答案			
		for(Map<String,Object> questMap : questList){
			//問卷版本
			String examVersion 	   = ObjectUtils.toString(questMap.get("EXAM_VERSION"));
			//題目序號
			String questionVersion = ObjectUtils.toString(questMap.get("QUESTION_VERSION"));
			//取得指定問卷版本及指定題目的所有答案及分數
			questMap.put("ANSWER_LIST", kycOperationDao.queryAnswer(examVersion , questionVersion));
		}

		cacheMap.put("QUESTIONNAIRE_LIST", questList);

		//風險級距編號
		String rlVersion = ObjectUtils.toString(questList.get(0).get("RL_VERSION"));
		//用風險級距編號去抓風險級距(用於判斷C值)
		cacheMap.put("RISK_LIST", kycOperationDao.queryLevel(rlVersion));

		//取得第一部分:個人基本資料，並確認該客戶為新客戶或舊客戶：新客戶沒有客戶主檔資料
		if(CollectionUtils.isEmpty(personalIformation = kycOperationDao.queryCustMastData(inputVO.getCustID()))){
			logInfo(inputVO.getCustID() , "Exception" , "客戶主檔無資料");
			throw new Exception("客戶主檔無資料");
		}

		//發電文FC032151學歷、職業、婚姻、子女數、FC032675重大傷病註記、FC032675電話、地址、EMAIL、生日
		initPpersonalIformationForFcData(cacheMap , inputVO.getCustID() , personalIformation);

		return cacheMap;
	}


	/**計算分數及判斷是哪個風險等級，並處以降等**/
	@SuppressWarnings("unchecked")
	public Map<String , Object> kycScore(Map<String , Object> cacheMap , KycOperationJavaInputVO inputVO) throws Exception{
		//客戶基本資料
		List<Map<String, Object>> personalIformationList = (List<Map<String, Object>>)cacheMap.get("PERSONAL_IFORMATION_LIST");
		Map<String, Object> personalMap = personalIformationList.get(0);

		Integer age 	 = getAge(personalMap.get("birthday"));  	//年齡
		String education = (String) personalMap.get("EDUCATION");	//學歷
		String vul_flag  = (String) personalMap.get("VUL_FLAG");	//弱勢
		
		if (isNaturalPerson(inputVO.getCustID())) {
			//自然人高中職以上學歷註記
			SOT701InputVO sot701vo = new SOT701InputVO();
			sot701vo.setCustID(inputVO.getCustID().toUpperCase());
			SOT701 sot701 = (SOT701) PlatformContext.getBean("sot701");	
			String custEduHighSchool = sot701.getCustEduHighSchool(sot701vo);
			
			checkAgeAndEducation(age, education, custEduHighSchool);
		}

		logInfo(inputVO.getCustID() , "計算分數" , "before");
		//分數加總
		Map<String , Object> scoreMap = sumAnswerNum(cacheMap , inputVO);

		//風險等級
		List<Map<String, Object>> riskAfrList = (List<Map<String, Object>>)cacheMap.get("RISK_LIST");
		logInfo(inputVO.getCustID() , "風險級距設定" , riskAfrList);
		// 比對風險屬性
		String cValue = "";//級距代碼
		//原始總分取得的風險屬性
		if(riskAfrList.size() > 1) {
			//分數小於等於風險比例上限
			if((float)scoreMap.get("SCORE_ORI_TOT") <= ((BigDecimal)riskAfrList.get(0).get("RL_UP_RATE")).floatValue()) {
				cValue = (String)riskAfrList.get(0).get("CUST_RL_ID");//級距代碼
			}
			//分數大於風險比例上限
			else {
				int max_risk = riskAfrList.size() - 1;

				for(int i = 0; i < riskAfrList.size(); i++) {
					for(int j = i + 1; j > i; j--) {
						if(j < riskAfrList.size()){
							//分數大於風險比例上限，同時分數小於等於商品風險上限
							if((float)scoreMap.get("SCORE_ORI_TOT") > ((BigDecimal)riskAfrList.get(i).get("RL_UP_RATE")).floatValue()
									&& (float)scoreMap.get("SCORE_ORI_TOT") <= ((BigDecimal)riskAfrList.get(j).get("RL_UP_RATE")).floatValue())
								cValue = (String)riskAfrList.get(j).get("CUST_RL_ID");//級距代碼
								//分數大於等於最大的風險比例上限，就抓最大的那筆
							else if((float)scoreMap.get("SCORE_ORI_TOT") > ((BigDecimal)riskAfrList.get(max_risk).get("RL_UP_RATE")).floatValue()
									|| (float)scoreMap.get("SCORE_ORI_TOT") == ((BigDecimal)riskAfrList.get(max_risk).get("RL_UP_RATE")).floatValue())
								cValue = (String)riskAfrList.get(max_risk).get("CUST_RL_ID");//級距代碼
						}
					}
				}
			}
		} else {
			cValue = (String)riskAfrList.get(0).get("CUST_RL_ID");//級距代碼
		}

		//初始化：以原始總分計分以及計算C值
		scoreMap.put("CRR_ORI", cValue);
		float riskRange = (float)scoreMap.get("SCORE_ORI_TOT");
		String riskLevel = cValue;

		//若參數有設定，則總分與矩陣取低者為C值；否則以原始總分計算
		if(kycOperationDao.getCrrRule()) {
			if(Integer.parseInt(scoreMap.get("CRR_MATRIX").toString().substring(1, 2)) < Integer.parseInt(cValue.substring(1, 2))) {
				riskLevel = scoreMap.get("CRR_MATRIX").toString();
				riskRange = (float)scoreMap.get("SCORE_CW_TOT");
			}
		}

		String sicktype  		= (String) personalMap.get("SICK_TYPE");//重大疾病
		String riskAfr 	 		= "";
		String degrade 			= ObjectUtils.toString(cacheMap.get(FC032675.DEGRADE.name())).toUpperCase();
		String degrade_date_s	= ObjectUtils.toString(cacheMap.get(FC032675.DEGRADE_DATE.name()));
		String cust_pro_date_s	= (String) personalMap.get("CUST_PRO_DATE");
		
		//KYC截止日(加一年減一天)
		SimpleDateFormat ft = new SimpleDateFormat("yyyyMMddHHmmss");
//		Date testDate = DateUtils.addDays(ft.parse(cbsservice.getCBSTestDate()), 364);
		Date testDate = DateUtils.addYears(ft.parse(cbsservice.getCBSTestDate()), 1); 
		testDate = DateUtils.addDays(testDate, -1);
		Date ori_expiry_date = new Timestamp(testDate.getTime());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String expiry_date = sdf.format(ori_expiry_date);

		Date degrade_date = null;
		if (StringUtils.isNotBlank(degrade_date_s)) {
			degrade_date = sdf.parse(degrade_date_s);
		}
		
		Date cust_pro_date = null;
		if (StringUtils.isNotBlank(cust_pro_date_s)) {
			cust_pro_date = sdf.parse(cust_pro_date_s);
		}
		
//		try {
//			logInfo(inputVO.getCustID() , "TX_FLAG " , "TX_FLAG:" + ObjectUtils.toString(personalMap.get("TX_FLAG")));
//			logInfo(inputVO.getCustID() , "age " , "age:" + age.toString());
//			logInfo(inputVO.getCustID() , "sicktype " , "sicktype:" + sicktype);
//			logInfo(inputVO.getCustID() , "education " , "education" + education);
//			logInfo(inputVO.getCustID() , "vul_flag " , "vul_flag:" + vul_flag);
//			logInfo(inputVO.getCustID() , "riskLevel " , "riskLevel:" + riskLevel);		
//			logInfo(inputVO.getCustID() , "degrade " , "degrade:" + degrade);
//			if(degrade_date != null) logInfo(inputVO.getCustID() , "degrade_date " , "degrade_date:" + sdf.format(degrade_date));
//			if(cust_pro_date != null) logInfo(inputVO.getCustID() , "cust_pro_date " , "cust_pro_date:" + sdf.format(cust_pro_date));
//		} catch(Exception e) {}
		
		//高資產客戶註記資料
		SOT701 sot701 = (SOT701) PlatformContext.getBean("sot701");	
		CustHighNetWorthDataVO hnwcVO = sot701.getHNWCData(inputVO.getCustID().toUpperCase());
//		hnwcVO.getDueDate();//高資產客戶註記到期日
//		hnwcVO.getInvalidDate();//高資產客戶註記註銷日
//		hnwcVO.getValidHnwcYN();//是否為有效高資產客戶
		
		//處理降等
		if (isNaturalPerson(inputVO.getCustID())) {
			//自然人
			Map<String, String> map = new HashMap<>();
			String txFlag = ObjectUtils.toString(personalMap.get("TX_FLAG")); //專業投資人YN
			if(StringUtils.equals("Y", txFlag) || StringUtils.equals("Y", hnwcVO.getValidHnwcYN())) {
				//專投或高資產客戶
				//年齡 , 重大疾病 , 學歷 , 級距代碼
				map = txDeclineLevel(age, sicktype, education, vul_flag, riskLevel , degrade, inputVO.getCustID(), degrade_date, cust_pro_date, ori_expiry_date, txFlag, hnwcVO.getValidHnwcYN());
			} else {	
				//一般客戶處理降等
				//年齡 , 重大疾病 , 學歷 , 級距代碼
				map = declineLevel(age, sicktype, education, vul_flag, riskLevel , degrade, inputVO.getCustID(), degrade_date);
			}
			riskAfr = map.get("cValue");
			expiry_date = StringUtils.isNotBlank(map.get("expiry_date")) ? map.get("expiry_date") : expiry_date;
		} else {
			//法人不降等
			riskAfr = riskLevel;
		}
		
//		try {
//			logInfo(inputVO.getCustID() , "riskAfr " , "riskAfr:" + riskAfr);
//			logInfo(inputVO.getCustID() , "expiry_date " , "expiry_date:" + expiry_date);
//		} catch(Exception e) {}
		
		//回傳結果
		Map<String , Object> resultMap = new HashMap<String , Object>();
		resultMap.putAll(scoreMap);							//分數相關資料
		resultMap.put("RISKRANGE"		, riskRange);		//最後總分
		resultMap.put("RISK_LEVEL"		, riskLevel);		//最後未降等前C值
		resultMap.put("AGE"				, age);
		resultMap.put("SICKTYPE"		, sicktype);
		resultMap.put("EDUCTION"		, education);
		resultMap.put("RISK_AFR"		, riskAfr);			//降等後C值
		resultMap.put("EXPIRY_DATE"		, expiry_date);		//KYC截止日
		resultMap.put("DEGRADE_DUE_DATE", degrade_date_s);	//免降等註記到期日
		resultMap.put("PRO_DUE_DATE"	, cust_pro_date_s);	//專業投資人到期日
		resultMap.put("HNWC_DUE_DATE", hnwcVO.getDueDate());		//客戶高資產註記到期日
		resultMap.put("HNWC_INVALID_DATE" , hnwcVO.getInvalidDate());	//客戶高資產註記註銷日

		try {
			//LOGINFO
			logInfo(inputVO.getCustID() , "計算分數 SCORE_ORI_TOT " , "after:" + scoreMap.get("SCORE_ORI_TOT").toString());
			logInfo(inputVO.getCustID() , "計算分數 SCORE_C " , "after:" + scoreMap.get("SCORE_C").toString());
			logInfo(inputVO.getCustID() , "計算分數 SCORE_W " , "after:" + scoreMap.get("SCORE_W").toString());
			logInfo(inputVO.getCustID() , "計算分數 SCORE_CW_TOT " , "after:" + scoreMap.get("SCORE_CW_TOT").toString());
			logInfo(inputVO.getCustID() , "計算分數 RISK_LOSS_RATE " , "after:" + scoreMap.get("RISK_LOSS_RATE").toString());
			logInfo(inputVO.getCustID() , "計算分數 RISK_LOSS_LEVEL " , "after:" + scoreMap.get("RISK_LOSS_LEVEL").toString());
			logInfo(inputVO.getCustID() , "計算分數 CRR_MATRIX " , "after:" + scoreMap.get("CRR_MATRIX").toString());
			logInfo(inputVO.getCustID() , "計算分數 CRR_ORI " , "after:" + scoreMap.get("CRR_ORI").toString());
			logInfo(inputVO.getCustID() , "計算分數 RISKRANGE " , "after:" + ObjectUtils.toString(riskRange));
			logInfo(inputVO.getCustID() , "計算分數 RISK_LEVEL " , "after:" + riskLevel);
		} catch(Exception e) {}

		return resultMap;
	}

	private void checkAgeAndEducation(Integer age, String education, String custEduHighSchool) throws KycException, JBranchException {
		if (age != null && age < 18 && StringUtils.equals("N", custEduHighSchool) //未滿18歲有高中職以上學歷註記
				&& StringUtils.defaultString(education).matches("[1-5]")) {	// 教育請參考 TBSYSPARAMETER 的 KYC.EDUCATION
			//親愛的客戶您好：為維護您的權益，未滿18歲客戶如教育程度為「高中職(含)以上」，請洽任一分行辦理，造成您的不便，敬請見諒。
			Map<String, String> map = kycOperationDao.getKycExceptionMap("KYCNG21");
			throw new KycException(map.get("PARAM_CODE"), map.get("PARAM_NAME"));
		}
	}

	/**計算分數**/
	@SuppressWarnings("unchecked")
	public Map<String , Object> sumAnswerNum(Map<String , Object> cacheMap , KycOperationJavaInputVO inputVO) throws JBranchException, KycException {
		//分割題目
		String[] generalQuestionArray = inputVO.getGeneralQuestion().split(";");
		//分割答案
		String[] generalAnsArray = inputVO.getGeneralAns().split(";");
		//題目
		List<Map<String, Object>> questionnaireList = (List<Map<String, Object>>)cacheMap.get("QUESTIONNAIRE_LIST");

		Map<String, Object> resultMap = new HashMap<String, Object>();
		float sum = 0, sumC = 0, sumW = 0;//原始總分, 風險偏好總分, 風險承受能力總分 分數加總
		float custNumber = 0;//截距分

		//比對網銀有做哪些題目及客戶所選的答案
		for (Map<String, Object> map : questionnaireList) {
			//截距分
			try { custNumber = ((BigDecimal)map.get("INT_SCORE")).floatValue(); } catch(Exception e) { custNumber = 0; }

			String qstNo = ObjectUtils.toString(map.get("QST_NO")); // Question order
			//題目序號
			int index = Arrays.asList(generalQuestionArray).indexOf(map.get("QUESTION_VERSION"));
			// 如果我們的題目集合不存在使用者的題目版本號，必須拋出錯誤（問卷題目每題皆為必選題，應無跳過情境）
			if(index < 0)
				throwsVersionNotExistsException("KYCNG17", qstNo);

			//此題的答案集合
			List<Map<String, Object>> ansList =  (List<Map<String, Object>>) map.get("ANSWER_LIST");
			//將答案「序號」與「分數』放入 Map
			Map<String, Float> ansMap = new HashMap();
			for (Map<String, Object> each : ansList) {
				ansMap.put(ObjectUtils.toString(each.get("ANSWER_SEQ")), ((BigDecimal)each.get("FRACTION")).floatValue());
			}

			//取這題網銀所選的答案
			String ans = generalAnsArray[index];

			String scoreType = ObjectUtils.toString(map.get("SCORE_TYPE"));// 計分類別 W：風險偏好 C：風險承受能力

			//複選題答案用 ',' 分隔
			//第三題計分特殊處理；與複選題不同，只選最高分計算
			if("3".equals(qstNo)) {
				float Q3Score = -999;	//第3題分數

				for (String tmp : ans.split(",")) {
					// 如果我們的答案集合不存在使用者的答案版本號，必須拋出錯誤
					Float fraction = ansMap.get(tmp);
					if (fraction == null)
						throwsVersionNotExistsException("KYCNG18", qstNo);

					if(fraction > Q3Score) {
						//這個答案選項分數比較高，第3題取這個選項的分數
						Q3Score = fraction;
					}
				}
				//分數加上Q3最高分選項分數
				sum += Q3Score;
				if("C".equals(scoreType)) sumC += Q3Score;
				if("W".equals(scoreType)) sumW += Q3Score;
				logInfo("Q3Score" , "計算分數 Q3Score " , ObjectUtils.toString(Q3Score));
			} else {
				for (String tmp : ans.split(",")) {
					// 如果我們的答案集合不存在使用者的答案版本號，必須拋出錯誤
					Float fraction = ansMap.get(tmp);
					if (fraction == null)
						throwsVersionNotExistsException("KYCNG18", qstNo);

					sum += fraction; // 加上該題選項的分數
					if("C".equals(scoreType)) sumC += fraction;
					if("W".equals(scoreType)) sumW += fraction;
				}
			}
			logInfo("sum" , "計算分數 SUM " , ObjectUtils.toString(sum));
			logInfo("sumC" , "計算分數 SUM " , ObjectUtils.toString(sumC));
			logInfo("sumW" , "計算分數 SUM " , ObjectUtils.toString(sumW));
		}

		//原始總分加上截距分
		resultMap.put("SCORE_ORI_TOT", sum + custNumber);
		//CW矩陣計算
		resultMap.put("SCORE_C", sumC);
		resultMap.put("SCORE_W", sumW);
		resultMap.put("SCORE_CW_TOT", sumC + sumW + custNumber);
		//可承受風險損失率
		float rlrate = (sum + custNumber + 62) / 1000 * 100; //#0579
		resultMap.put("RISK_LOSS_RATE", rlrate <= 0 ? 0 : rlrate );

		resultMap.put("CRR_MATRIX", kycOperationDao.getCRRMatrixLevel((String)questionnaireList.get(0).get("RS_VERSION"), sumC, sumW));
		resultMap.put("RISK_LOSS_LEVEL", kycOperationDao.getRiskLossLevel((String)questionnaireList.get(0).get("RLR_VERSION"), (rlrate <= 0 ? 0 : rlrate)));

		return resultMap;
	}

	private void throwsVersionNotExistsException(String errCode, String qstNo) throws JBranchException, KycException {
		Map<String, String> map = kycOperationDao.getKycExceptionMap(errCode);
		throw new KycException(map.get("PARAM_CODE"), map.get("PARAM_NAME") + "第 " + qstNo + " 題" );
	}

	/**問題解碼
	 * @throws APException **/
	public static String uncodeAnswer(String generalAns) throws APException {
		if(StringUtils.isBlank(generalAns)) {
			return "";
		}

		String[] genAns_S 	= generalAns.split("\\.");
		String generalAns_S = "";

		for(int i = 0; i < genAns_S.length; i++){
			if(genAns_S[i].length() > 2) {
				//處理第三題答案，如：4949(ASCII) => 11(String)
				generalAns_S = generalAns_S + charToString(genAns_S[i]);
			} else {
				generalAns_S = generalAns_S + (char)Integer.parseInt(genAns_S[i]);
			}
		}

		return generalAns_S;
	}

	public static String charToString(String str) throws APException {
		String rtnString = "";

		if(str.length() % 2 > 0) {
			throw new APException("答案有誤，請重新檢視");
		} else {
			for(int i = 0; i <= str.length(); i = i + 2) {
				if(str.length() >= i+1) {
					rtnString = rtnString + (char)Integer.parseInt(str.substring(i, i+2));
				}
			}
		}

		return rtnString;
	}

	private Integer getAge(Object day) {
		if (day == null)
			return null;
		Date custDate = (Date) day;
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(new Date());
		Calendar calendar2 = new GregorianCalendar();
		calendar2.setTime(custDate);

		int year1 = calendar.get(Calendar.YEAR);
		int year2 = calendar2.get(Calendar.YEAR);
		int age = year1 - year2;
		int month1 = calendar.get(Calendar.MONTH);
		int month2 = calendar2.get(Calendar.MONTH);
		if (month2 > month1) {
			age--;
		} else if (month1 == month2) {
			int day1 = calendar.get(Calendar.DAY_OF_MONTH);
			int day2 = calendar2.get(Calendar.DAY_OF_MONTH);
			if (day2 > day1)
				age--;
		}
		return age;
	}

	/**專業投資人降等判斷**/
	private Map txDeclineLevel(Integer age, String sicktype, String eduction, String vul_flag, String cValue, String degrade, String custID, Date degrade_date, Date cust_pro_date, Date expiry_date, String txFlag, String hnwcYN) throws Exception{
		Map<String, String> map = new HashMap<>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

		cValue 	 = cValue 	== null ? "" : cValue;
		eduction = eduction == null ? "" : eduction;
		age 	 = age 		== null ? 0  : age;
		vul_flag = vul_flag == null ? "" : vul_flag;
		
		//初始值為原C值
		map.put("cValue", cValue);
		
		//判斷是否可做kyc
		chkCanKyc(age, sicktype, eduction, custID);

		//18歲以下且學歷為國高中
		//沒有法代：最高C2 (不管有沒有重大傷病)
    	//有法代：
    	//	1.沒有重大傷病，取法代孰低者C值
    	//  2.有重大傷病：法代孰低者為C1，則C1，否則最高C2	
		if(age < 18 && eduction.matches("[6]")){
			String regKYClv = getLegalRegKYCLV(custID); //取得法代風險屬性
			if(StringUtils.isNotBlank(regKYClv)) {
				//若有法代風險屬性，則以法代為上限
				String kVal = Integer.parseInt(regKYClv.substring(1)) <= Integer.parseInt(cValue.substring(1)) ? regKYClv : cValue;				
				if(StringUtils.equals("2", sicktype)) { //有全民健康保險重大傷病證明，但不影響投資風險理解
					//若有法代風險屬性且有重大傷病，以法代及未成年取孰低且以C2為上限
					map.put("cValue", kVal.replaceAll("([3-9]|(\\d\\d+))$", "2"));
				} else {
					//若有法代風險屬性且無重大傷病，以法代取孰低為上限
					map.put("cValue", kVal);
				}
			} else {
				//沒有法代KYC資料(法代沒有KYC或已過期)，最高C2
				map.put("cValue", cValue.replaceAll("([3-9]|(\\d\\d+))$", "2"));
			}
			
			return map;
		}		
		
		//判斷免降等註記是否有效
		Boolean degrade_end = (degrade_date == null || degrade_date.before(new Date())) ? true : false;
		
		//非高資產客戶且為弱勢客戶且非65~70歲非弱勢專投
		if(!StringUtils.equals("Y", hnwcYN) && //非高資產客戶
				!(StringUtils.equals("Y", txFlag) && age >= 65 && age < 70 && !chkNoSpecialSigningReduceLevel(40, sicktype, eduction)) && //非65~70歲非弱勢專投
				(chkNoSpecialSigningReduceLevel(age, sicktype, eduction) || StringUtils.equals("Y", vul_flag))) { //弱勢客戶
			if (StringUtils.equals("Y", degrade) && !degrade_end) {
				//有免降等註記且未到期 ==> 不降等
				if ("C4".equals(cValue)) {
					//若C值為C4，則KYC到期日=免降等到期日與評估日+1年-1日取孰近
					if(degrade_date.before(expiry_date)) {
						map.put("expiry_date", sdf.format(degrade_date));
					}
				}
			} else {
				//沒有免降等註記 ==> 最高C3
				map.put("cValue", cValue.replaceAll("([4-9]|(\\d\\d+))$", "3"));
			}
		} else {
			//非弱勢客戶不降等
			//高資產客戶不降等
    		//65~70歲非弱勢專投不降等
		}
		
		return map;
	}

	/**非專業投資人降等判斷**/
	private Map declineLevel(Integer age, String sicktype, String eduction, String vul_flag, String cValue, String degrade, String custID, Date degrade_date) throws Exception{
		Map<String, String> map = new HashMap<>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

		cValue 	 = cValue 	== null ? "" : cValue;
		eduction = eduction == null ? "" : eduction;
		age 	 = age 		== null ? 0  : age;
		vul_flag = vul_flag == null ? "" : vul_flag;

		//初始值為原C值
		map.put("cValue", cValue);
		
		//判斷是否可做kyc
		chkCanKyc(age, sicktype, eduction, custID);

		//18歲以下且學歷為國高中
		//沒有法代：最高C2 (不管有沒有重大傷病)
		//有法代：
		//	1.沒有重大傷病，取法代孰低者C值
		//  2.有重大傷病：法代孰低者為C1，則C1，否則最高C2	
		if(age < 18 && eduction.matches("[6]")){
			String regKYClv = getLegalRegKYCLV(custID); //取得法代風險屬性
			if(StringUtils.isNotBlank(regKYClv)) {
				//若有法代風險屬性，則以法代為上限
				String kVal = Integer.parseInt(regKYClv.substring(1)) <= Integer.parseInt(cValue.substring(1)) ? regKYClv : cValue;				
				if(StringUtils.equals("2", sicktype)) { //有全民健康保險重大傷病證明，但不影響投資風險理解
					//若有法代風險屬性且有重大傷病，以法代及未成年取孰低且以C2為上限
					map.put("cValue", kVal.replaceAll("([3-9]|(\\d\\d+))$", "2"));
				} else {
					//若有法代風險屬性且無重大傷病，以法代取孰低為上限
					map.put("cValue", kVal);
				}
			} else {
				//沒有法代KYC資料(法代沒有KYC或已過期)，最高C2
				map.put("cValue", cValue.replaceAll("([3-9]|(\\d\\d+))$", "2"));
			}
			
			return map;
		}	
			
		//判斷免降等註記是否有效
		Boolean degrade_end = (degrade_date == null || degrade_date.before(new Date())) ? true : false;
		
		//弱勢客戶
		if(chkNoSpecialSigningReduceLevel(age, sicktype, eduction) || StringUtils.equals("Y", vul_flag)) {
			if (StringUtils.equals("Y", degrade) && !degrade_end) {
				//有免降等註記且未到期 ==> 不降等；若C值為C3 or C4，則KYC到期日=免降等到期日
				if (cValue.matches("C3|C4")) {
					map.put("expiry_date", sdf.format(degrade_date));
				}
			} else {
				//沒有免降等註記 ==> 最高C2
				map.put("cValue", cValue.replaceAll("([3-9]|(\\d\\d+))$", "2"));
			}
		} else {
			//非弱勢客戶不降等
		}
		
		return map;
	}

	public void chkCanKyc(Integer age, String sicktype, String eduction, String custID) throws Exception{
		//無法辦理KYC
		if("3".equals(sicktype)){
			throw new Exception("有全民健康保險重大傷病證明，而且會影響本人對投資商品及其風險之理解，無法承做問卷");
		}
		else if(isNaturalPerson(custID) && age >= 18 && "8".equals(eduction)){
			throw new Exception("年齡18歲(含)以上不識字，無法承做問卷");
		}
	}

	public boolean chkNoSpecialSigningReduceLevel(Integer age, String sicktype, String eduction){
		boolean isReduceLevel = false;

		//年齡65歲(含)以上
		isReduceLevel = age >= 65;
		
//		try{logInfo("chkNoSpecialSigningReduceLevel" , "age >= 70 " , "age >= 70:" + ObjectUtils.toString(isReduceLevel));} catch(Exception e) {}
		
		//年齡18歲(含)以上，教育程度圍國中(含以下)
		isReduceLevel = isReduceLevel || (age >= 18 && eduction.matches("[6]"));
		
//		try{logInfo("chkNoSpecialSigningReduceLevel" , "年齡18歲(含)以上，教育程度圍國中(含以下) " , "年齡18歲(含)以上，教育程度圍國中(含以下):" + ObjectUtils.toString((age >= 18 && eduction.matches("[6]"))));} catch(Exception e) {}
		
		//有全民健康保險重大傷病證明，但不影響投資風險理解
		isReduceLevel = isReduceLevel || "2".equals(sicktype);
		
//		try{logInfo("chkNoSpecialSigningReduceLevel" , "重大傷病證明 " , "重大傷病證明:" + ObjectUtils.toString("2".equals(sicktype)));} catch(Exception e) {}

		return isReduceLevel;
	}


	/**發電文FC032151學歷、職業、婚姻、子女數、FC032675重大傷病註記、FC032675電話、地址、EMAIL、生日**/
	public void initPpersonalIformationForFcData(Map<String , Object> cacheMap , String custId , List<Map<String, Object>> personalIformation) throws JBranchException, Exception{
		String education 	 = ""; 	//學歷
		String career 	 	 = ""; 	//職業
		String marrage 	 	 = ""; 	//婚姻
		String childNo 	 	 = ""; 	//子女數
		String sickType  	 = ""; 	//重大傷病
		String txFlg 	 	 = ""; 	//專業投資人
		String cust_pro_date = "";  //專業投資人到期日
		String degrade	 	 = ""; 	//ＫＹＣ免降等註記(Y/N)
		String degrade_date	 = "";  //ＫＹＣ免降等註記到期日
		String vul_flag  	 = ""; 	//弱勢(Y/N)

		basicVO basicVO = new basicVO();
		Map<FC032151 , String> resultFC032151Map = null;
		Map<FC032675 , String> resultFC032675Map = null;

		SOT701InputVO sot701inputVO = new SOT701InputVO();
		sot701inputVO.setCustID(custId);
		FP032675DataVO fp032675DataVO = new FP032675DataVO();
		FP032675OutputVO fp032675OutputVO = new FP032675OutputVO();
		FP032151OutputVO fp032151OutputVO = new FP032151OutputVO();
		WMS032154OutputDetailsVO wms032154OutputDetailsVO = new WMS032154OutputDetailsVO();

		//取電文FC032151學歷、職業、婚姻、子女數
		resultFC032151Map = sendFC032151Data(custId);
		education = resultFC032151Map.get(FC032151.EDUCATION);
		career 	  = resultFC032151Map.get(FC032151.CAREER);
		marrage	  = resultFC032151Map.get(FC032151.MARRAGE);
		childNo   = resultFC032151Map.get(FC032151.CHILDNO);

		// resultFC032151Map = null;

		//取電文FC032675重大傷病註記、專投
		resultFC032675Map = sendFC032675Data(custId);
		txFlg	  	  = resultFC032675Map.get(FC032675.CUST_PRO_FLAG);	//專業投資人
		sickType  	  = resultFC032675Map.get(FC032675.SICK_TYPE);		//重大傷病
		degrade   	  = resultFC032675Map.get(FC032675.DEGRADE);		//ＫＹＣ免降等註記(Y/N)
		vul_flag  	  = resultFC032675Map.get(FC032675.VUL_FLAG);		//特定客戶(Y/N)
		cust_pro_date = resultFC032675Map.get(FC032675.CUST_PRO_DATE);
		degrade_date  = resultFC032675Map.get(FC032675.DEGRADE_DATE);
		
		resultFC032675Map = null;
		/**
		 * 舊的 //取電文FC032675電話、地址、EMAIL、生日 basicVO = sendCustBasicData(custId);
		 *
		 */

		// 取得電文 電話資料
		wms032154OutputDetailsVO = sot701.getPhoneData(sot701inputVO);

		// 取得地址 信箱資料
		fp032675OutputVO = sot701.getAddrandMail(sot701inputVO);

		if(CollectionUtils.isEmpty(personalIformation)) {
			personalIformation = new ArrayList<Map<String, Object>>();
			Map<String, Object> addData = new HashMap<String, Object>();

			// 生日轉換成日期
			if (!cbsservice.checkJuristicPerson(sot701inputVO.getCustID())) {
				String pattern = "ddMMyyyy";
				Date BDAY = new SimpleDateFormat(pattern).parse(resultFC032151Map.get(FC032151.BDAY));
				resultFC032151Map = null;
				addData.put("birthday", BDAY);
			}else{
				addData.put("birthday", basicVO.getBirthday() == null ? null : basicVO.getBirthday().getTime());
			}
			addData.put("DAY"			, wms032154OutputDetailsVO.getResd_tel());
			addData.put("DAYCOD"		, basicVO.getDAY_COD());
			addData.put("NIGHT"			, wms032154OutputDetailsVO.getCon_tel());
			addData.put("NIGHTCOD"		, basicVO.getNIGHT_COD());
			addData.put("TELNO_COD"		, basicVO.getTELNO_COD());
			addData.put("TEL_NO"		, wms032154OutputDetailsVO.getHandphone());
			addData.put("FAX_COD"		, basicVO.getFAX_COD());
			addData.put("FAX"			, wms032154OutputDetailsVO.getFax());
			addData.put("CUSTADDR_COD"	, basicVO.getCUSTADDR_COD());
			addData.put("CUST_ADDR_1"	, fp032675OutputVO.getDATA3());
			addData.put("EMAILADDR_COD"	, basicVO.getEMAILADDR_COD());
			addData.put("EMAIL_ADDR"	, fp032675OutputVO.getE_MAIL());
			addData.put("EDUCATION"		, education);
			addData.put("CAREER"		, career);
			addData.put("MARRAGE"		, marrage);
			addData.put("CHILD_NO"		, childNo);
			addData.put("SICK_TYPE"		, sickType);
			addData.put("TX_FLAG"		, txFlg);
			addData.put("VUL_FLAG"		, vul_flag);
			addData.put("CUST_PRO_DATE"	, cust_pro_date);
			addData.put("DEGRADE_DATE"	, degrade_date);

			personalIformation.add(addData);

			logInfo(custId , "電文取回資料" , addData);
		} else {
			for (Map<String, Object> addData : personalIformation) {
				// 生日轉換成日期
				if (!cbsservice.checkJuristicPerson(sot701inputVO.getCustID())) {
					String pattern = "ddMMyyyy";
					Date BDAY = new SimpleDateFormat(pattern).parse(resultFC032151Map.get(FC032151.BDAY));
					resultFC032151Map = null;
					addData.put("birthday", BDAY);
				}else{
					addData.put("birthday", basicVO.getBirthday() == null ? null : basicVO.getBirthday().getTime());
				}
				addData.put("DAY"			, wms032154OutputDetailsVO.getResd_tel());
				addData.put("DAYCOD"		, basicVO.getDAY_COD());
				addData.put("NIGHT"			, wms032154OutputDetailsVO.getCon_tel());
				addData.put("NIGHTCOD"		, basicVO.getNIGHT_COD());
				addData.put("TELNO_COD"		, basicVO.getTELNO_COD());
				addData.put("TEL_NO"		, wms032154OutputDetailsVO.getHandphone());
				addData.put("FAX_COD"		, basicVO.getFAX_COD());
				addData.put("FAX"			, wms032154OutputDetailsVO.getFax());
				addData.put("CUSTADDR_COD"	, basicVO.getCUSTADDR_COD());
				addData.put("CUST_ADDR_1"	, fp032675OutputVO.getDATA3());
				addData.put("EMAILADDR_COD"	, basicVO.getEMAILADDR_COD());
				addData.put("EMAIL_ADDR"	, fp032675OutputVO.getE_MAIL());
				addData.put("EDUCATION"		, education);
				addData.put("CAREER"		, career);
				addData.put("MARRAGE"		, marrage);
				addData.put("CHILD_NO"		, childNo);
				addData.put("SICK_TYPE"		, sickType);
				addData.put("TX_FLAG"		, txFlg);
				addData.put("VUL_FLAG"		, vul_flag);
				addData.put("CUST_PRO_DATE"	, cust_pro_date);
				addData.put("DEGRADE_DATE"	, degrade_date);

				logInfo(custId , "電文取回資料" , addData);
			}
		}

		//設定特簽
		cacheMap.put(FC032675.DEGRADE.name() , degrade);
		cacheMap.put(FC032675.DEGRADE_DATE.name() , degrade_date);
		cacheMap.put("PERSONAL_IFORMATION_LIST" , personalIformation);
	}


	public void getLastResult(Object body) throws JBranchException{
		KYC310InputVO inputVO = (KYC310InputVO) body;
		KYC310OutputVO outputVO = new KYC310OutputVO();
		try {
			List<Map<String, Object>> beforeData1 = kycOperationDao.queryDetail(inputVO.getCUST_ID());
			List<Map<String, Object>> beforeData2 = kycOperationDao.queryMast(inputVO.getCUST_ID());
			Map<String, Map<String, Object>> map2 = new HashMap<String, Map<String,Object>>();

			for(Map<String, Object> map : beforeData1){
				if(!map.get("CUST_EDUCTION_AFTER").equals(inputVO.getEDUCATION())){
					Map<String, Object> check = new HashMap<String, Object>();
					check.put("beforEDUCTION", map.get("CUST_EDUCTION_AFTER"));
					check.put("afterEDUCTION", inputVO.getEDUCATION());
					map2.put("EDUCTION", check);
				}
				if(!map.get("CUST_CAREER_AFTER").equals(inputVO.getCAREER())){
					Map<String, Object> check = new HashMap<String, Object>();
					check.put("beforCAREER", map.get("CUST_CAREER_AFTER"));
					check.put("afterCAREER", inputVO.getCAREER());
					map2.put("CAREER", check);
				}
				if(!map.get("CUST_MARRIAGE_AFTER").equals(inputVO.getMARRAGE())){
					Map<String, Object> check = new HashMap<String, Object>();
					check.put("beforMARRIAGE", map.get("CUST_MARRIAGE_AFTER"));
					check.put("afterMARRIAGE", inputVO.getMARRAGE());
					map2.put("MARRIAGE", check);
				}
				if(!map.get("CUST_CHILDREN_AFTER").equals(inputVO.getCHILD_NO())){
					Map<String, Object> check = new HashMap<String, Object>();
					check.put("beforCHILDREN", map.get("CUST_CHILDREN_AFTER"));
					check.put("afterCHILDREN", inputVO.getCHILD_NO());
					map2.put("CHILDREN", check);
				}
				if(map.get("CUST_HEALTH_AFTER") != null){
					if(!map.get("CUST_HEALTH_AFTER").equals(inputVO.getSICK_TYPE())){
						Map<String, Object> check = new HashMap<String, Object>();
						check.put("beforHEALTH", map.get("CUST_HEALTH_AFTER"));
						check.put("afterHEALTH", inputVO.getSICK_TYPE());
						map2.put("HEALTH", check);
					}
				}
			}

			for(Map<String, Object> map3 : beforeData2){
				Map<String, Object> check2 = new HashMap<String, Object>();
				check2.put("TEST_BEF_DATE", map3.get("CREATE_DATE"));
				check2.put("CUST_RISK_BEF", map3.get("CUST_RISK_AFR"));
				map2.put("INVESTOREXAM_M", check2);
			}

			outputVO.setComparison(map2);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}

	}

	/**取電文FC032151學歷、職業、婚姻、子女數**/
	public Map<FC032151 , String> sendFC032151Data(String custId) throws JBranchException, Exception{
		Map<FC032151 , String> resultMap = new HashMap<FC032151 , String>();
		FP032151OutputVO fp032151OutputVO = new FP032151OutputVO();
		SOT701InputVO sot701inputVO 	 = new SOT701InputVO();
		sot701inputVO.setCustID(custId);
		fp032151OutputVO = getSot701().getFP032151Data(sot701inputVO, null);
		resultMap.put(FC032151.EDUCATION, fp032151OutputVO.getEDUCATION());
		resultMap.put(FC032151.CAREER, fp032151OutputVO.getCAREER());
		resultMap.put(FC032151.MARRAGE, fp032151OutputVO.getMARRAGE());
		resultMap.put(FC032151.CHILDNO, fp032151OutputVO.getCHILD_NO());
		resultMap.put(FC032151.BDAY, fp032151OutputVO.getBDAY());
		logInfo(custId , "學歷、職業、婚姻、子女數" , resultMap);
		return resultMap;
	}

	/**取電文FC032675重大傷病註記**/
	public Map<FC032675 , String> sendFC032675Data(String custId) throws JBranchException, Exception{
		Map<FC032675 , String> resultMap = new HashMap<FC032675 , String>();
		FP032675DataVO fp032675DataVO = new FP032675DataVO();
		SOT701InputVO sot701inputVO 	 = new SOT701InputVO();

		sot701inputVO.setCustID(custId.toUpperCase());

		// 這邊為避免撈取不必要的電文造成效能低落
		sot701inputVO.setIsOBU("FAKE"); // 用不到 OBU，所以傳入假的 OBU FLAG 避免撈取 085081_085105、NFEI003
		sot701inputVO.setNeedDesc(false); // 用不到 DESC，設為 false 可避免撈取 NFEI002 電文
		sot701inputVO.setData067164_067165(new ArrayList<CBSUtilOutputVO>()); // 用不到 067164_067165 電文的資料，設空集合可避免撈取該電文
		//===============================================================================

		fp032675DataVO = getSot701().getFP032675Data(sot701inputVO);
		resultMap.put(FC032675.CUST_PRO_FLAG, fp032675DataVO.getCustProFlag());	// 專業投資人(Y/N)
		
		Date cust_pro_date = fp032675DataVO.getCustProDate();
		String cust_pro_date_s = null;
		if (cust_pro_date != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			cust_pro_date_s = sdf.format(cust_pro_date);
		}
		
		resultMap.put(FC032675.CUST_PRO_DATE, cust_pro_date_s);					// 專業投資人到期日
		resultMap.put(FC032675.SICK_TYPE, fp032675DataVO.getSickType());		// 重大傷病等級
		resultMap.put(FC032675.DEGRADE, fp032675DataVO.getDegrade());			// ＫＹＣ免降等註記(Y/N)
		resultMap.put(FC032675.DEGRADE_DATE, fp032675DataVO.getDegradeDate());	// ＫＹＣ免降等註記到期日
		//
		String age_under70_flag = fp032675DataVO.getAgeUnder70Flag();
		String edu_jr_flag = fp032675DataVO.getEduJrFlag();
		String health_flag = fp032675DataVO.getHealthFlag();
		String vul_flag = "N";
		if ("N".equals(age_under70_flag) || "N".equals(edu_jr_flag) || "N".equals(health_flag)) {
			vul_flag = "Y";
		}
		resultMap.put(FC032675.VUL_FLAG, vul_flag);
		//
		String degradeDate = fp032675DataVO.getDegradeDate();					// ＫＹＣ免降等註記到期日
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String today_s = sdf.format(new Date());
		Date today =  sdf.parse(today_s);
		Date degrade_date = null;
		if (StringUtils.isNotBlank(degradeDate)) {
			degrade_date = sdf.parse(degradeDate);
		}
		
		logInfo(custId , "重大傷病註記、專業投資人" , resultMap);
		return resultMap;
	}

	public basicVO sendCustBasicData(String custId) throws JBranchException, Exception{
		SOT701InputVO sot701inputVO = new SOT701InputVO();
		sot701inputVO.setCustID(custId.toUpperCase());
		return getSot701().getCustBasicData(sot701inputVO, null);
	}

	public SOT701 getSot701() {
		return sot701;
	}

	private String getVersion() throws JBranchException{
		return kyc310.nextSeqLpad();
	}

	//檢核新開戶客戶
	public void sendSotToAddCust(String custId) throws Exception{
		//input obj設定要查詢的客戶Id
		SOT701InputVO inputVOIot701 = new SOT701InputVO();
		inputVOIot701.setCustID(custId.toUpperCase());

		//發送電文查詢客戶註記資料電文
		FP032151OutputVO fp032151VO = sot701.getFP032151Data(inputVOIot701, null);

		//新增客戶主檔
		TBCRM_CUST_MASTVO tcm = new TBCRM_CUST_MASTVO();
		tcm.setCUST_ID(custId.toUpperCase());
		tcm.setCUST_NAME(fp032151VO.getCUST_NAME());
		if (!cbsservice.checkJuristicPerson(inputVOIot701.getCustID())) {
			String pattern = "ddMMyyyy";
			Date BDAY = new SimpleDateFormat(pattern).parse(fp032151VO.getBDAY());
			tcm.setBIRTH_DATE(new Timestamp(BDAY.getTime()));
		}
		kycOperationDao.getDataAccessManager().create(tcm);

		//客戶駐記檔		
		TBCRM_CUST_NOTEVO tcn = new TBCRM_CUST_NOTEVO();
		tcn.setCUST_ID(custId.toUpperCase());
		kycOperationDao.getDataAccessManager().create(tcn);
	}


	public static String decodeAnswer(String ansStr){
		String rsult = "";
		for(String str : ansStr.split(";")){
			if(str.indexOf(",") != -1){
				for(String st : str.split(",")){
					rsult = rsult + (int)st.charAt(0) + ",";
				}

				rsult = rsult.replaceFirst("\\,$", ";");
			}
			else{
				rsult = rsult + (int)str.charAt(0) + ";";
			}
		}

		return rsult.replaceAll("\\,", ".45.").replaceAll(";", ".94.").replaceFirst("\\.94\\.$", "");
	}


	public static void logInfo(Object msg){
		logger.info(ObjectUtils.toString(msg));
	}

	public static void logInfo(String custId , Object msg){
		logger.info("[" + custId + "]" + ObjectUtils.toString(msg));
	}

	public static void logInfo(String firstTitle , String title , Object msg){
		logger.info("[" + firstTitle + "][" + title + "]" + ObjectUtils.toString(msg));
	}

	private boolean isNaturalPerson(String custId) {
		return StringUtils.length(custId) >= 10;
	}

	/***
	 * 取得法定代理人KYC風險屬性
	 * @param custId
	 * @return
	 * @throws Exception
	 */
	private String getLegalRegKYCLV(String custId) throws Exception {
		String regKycLv = "";
		//取得法代資料
		Map<String, String> legalRegMap = kyc310.getData067050ByType(custId);
		
		if(StringUtils.isNotBlank(legalRegMap.get("REP_RISK_1")) && StringUtils.isBlank(legalRegMap.get("REP_RISK_2"))) {
			//法代1有風險屬性，法代2沒有；取法代1風險屬性
			regKycLv = legalRegMap.get("REP_RISK_1"); 
		} else if(StringUtils.isBlank(legalRegMap.get("REP_RISK_1")) && StringUtils.isNotBlank(legalRegMap.get("REP_RISK_2"))) {
			//法代2有風險屬性，法代1沒有；取法代2風險屬性
			regKycLv = legalRegMap.get("REP_RISK_2");
		} else if(StringUtils.isNotBlank(legalRegMap.get("REP_RISK_1")) && StringUtils.isNotBlank(legalRegMap.get("REP_RISK_2"))) {
			//法代1與法代2都有風險屬性，取孰低
			if(Integer.parseInt(legalRegMap.get("REP_RISK_1").substring(1)) > Integer.parseInt(legalRegMap.get("REP_RISK_2").substring(1))) {
				regKycLv = legalRegMap.get("REP_RISK_2");
			} else {
				regKycLv = legalRegMap.get("REP_RISK_1");
			}
		}
		
		return regKycLv;
	}
	
//	public void initLogin(){
//		UUID uuid = new UUID();
//		uuid.setSectionID(java.util.UUID.randomUUID().toString());
//		uuid.setBranchID(inputVO.getBranch());
//		uuid.setWsId(uuid.getSectionID());
//		
	// DataManagerIF dataManager = (DataManagerIF)
	// PlatformContext.getBean("dataManager");
//		User user = new User();
//		user.setUserID(inputVO.getUserID());
//		
//		WorkStation ws = new WorkStation();
//		ws.setUser(user);
//		ws.setBrchID(uuid.getBranchID());
//		ws.setWsID(uuid.getSectionID());
//		ws.setWsIP(inputVO.getIP());
//		
//		Branch brch = new Branch();
//		brch.setBrchID(uuid.getBranchID());
//		brch.setWorkStation(uuid.getSectionID() , ws);
//		
//		dataManager.setBranch(uuid.getBranchID() , brch);
//		dataManager.setWorkStation(uuid, ws);
//		ThreadDataPool.setData(ThreadDataPool.KEY_UUID , uuid);
//	}
}