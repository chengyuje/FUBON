package com.systex.jbranch.app.server.fps.kyc310;

import static com.systex.jbranch.app.server.fps.kyccons.KYCCons.ARM;
import static com.systex.jbranch.app.server.fps.kyccons.KYCCons.BRANCH_AUTHORIZED;
import static com.systex.jbranch.app.server.fps.kyccons.KYCCons.BRANCH_SIGNATURE;
import static com.systex.jbranch.app.server.fps.kyccons.KYCCons.LEGAL_PERSON_FINANCE_OP;
import static com.systex.jbranch.app.server.fps.kyccons.KYCCons.PERSONAL_FINANCE_OP;
import static com.systex.jbranch.app.server.fps.kyccons.KYCCons.PRINT_QUESTIONNAIRE;
import static com.systex.jbranch.app.server.fps.kyccons.KYCCons.PRINT_QUESTIONNAIRE_ENG;
import static com.systex.jbranch.app.server.fps.kyccons.KYCCons.TRADER;
import static org.apache.commons.lang.StringUtils.isNotBlank;

import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.fubon.commons.PdfConfigVO;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.systex.jbranch.app.common.fps.table.TBCRM_CUST_MASTVO;
import com.systex.jbranch.app.common.fps.table.TBCRM_CUST_NOTEVO;
import com.systex.jbranch.app.common.fps.table.TBKYC_INVESTOREXAM_COMPVO;
import com.systex.jbranch.app.common.fps.table.TBKYC_INVESTOREXAM_DVO;
import com.systex.jbranch.app.common.fps.table.TBKYC_INVESTOREXAM_MVO;
import com.systex.jbranch.app.common.fps.table.TBKYC_REPORTVO;
import com.systex.jbranch.app.common.fps.table.TBORG_MEMBERVO;
import com.systex.jbranch.app.server.fps.kyc.chk.KYCCheckIdentityWeights;
import com.systex.jbranch.app.server.fps.kyc311.KYC311InputVO;
import com.systex.jbranch.app.server.fps.kycoperation.KycOperationDao;
import com.systex.jbranch.app.server.fps.sot701.CustHighNetWorthDataVO;
import com.systex.jbranch.app.server.fps.sot701.CustKYCDataVO;
import com.systex.jbranch.app.server.fps.sot701.FP032675DataVO;
import com.systex.jbranch.app.server.fps.sot701.SOT701;
import com.systex.jbranch.app.server.fps.sot701.SOT701InputVO;
import com.systex.jbranch.app.server.fps.sot701.basicVO;
import com.systex.jbranch.app.server.fps.sot712.PRDFitInputVO;
import com.systex.jbranch.app.server.fps.sot712.SOT712;
import com.systex.jbranch.app.server.fps.sot712.SOT712InputVO;
import com.systex.jbranch.app.server.fps.sot712.SOT712OutputVO;
import com.systex.jbranch.app.server.fps.sot712.SotPdfContext;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.fubon.commons.PdfUtil;
import com.systex.jbranch.fubon.commons.cbs.CBSService;
import com.systex.jbranch.fubon.commons.cbs.dao._060503_060503DAO;
import com.systex.jbranch.fubon.commons.cbs.dao._067050_067101DAO;
import com.systex.jbranch.fubon.commons.cbs.vo._060503_060503.CBS060503OutputDetailsVO;
import com.systex.jbranch.fubon.commons.cbs.vo._060503_060503.CBS060503OutputVO;
import com.systex.jbranch.fubon.commons.cbs.vo._067050_067115.CBS067101OutputVO;
import com.systex.jbranch.fubon.commons.cbs.vo.basic.CBSUtilOutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.fp032151.FP032151OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.fp032675.FP032675OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.wms032154.WMS032154OutputDetailsVO;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.serialnumber.SeriaNextSequence;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.ObjectUtil;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * KYC310
 * 
 * @author Jimmy
 * @date 2016/08/09
 * @spec null
 */
@Component("kyc310")
@Scope("request")
public class KYC310 extends FubonWmsBizLogic{
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private CBSService cbsservice;

	@Autowired@Qualifier("KYCCheckIdentityWeights")
	private KYCCheckIdentityWeights KYCCheck;
	
	@Autowired@Qualifier("SeriaNextSequence")
	private SeriaNextSequence seriaNextSequence;
	
	@Autowired@Qualifier("sot701")
	private SOT701 sot701;
	
	@Autowired
    private _060503_060503DAO _060503_060503dao;
	@Autowired
	private _067050_067101DAO _067050_067101dao;
	
	@Autowired
	@Qualifier("KycOperationDao")
	private KycOperationDao kycOperationDao;
	
	@SuppressWarnings("unchecked")
	public void chkTimes(Object body, IPrimitiveMap<Object> header) throws Exception{
		KYC310InputVO inputVO = (KYC310InputVO) body;
		KYC310OutputVO outputVO = new KYC310OutputVO();
		
		SOT701InputVO sot701inputVO = new SOT701InputVO();
		basicVO basicVO = new basicVO();
		FP032675DataVO fp032675DataVO = new FP032675DataVO();
		FP032675OutputVO fp032675OutputVO = new FP032675OutputVO();
		FP032151OutputVO fp032151OutputVO = new FP032151OutputVO();
		WMS032154OutputDetailsVO wms032154OutputDetailsVO = new WMS032154OutputDetailsVO();
		boolean isNotBus = inputVO.getCUST_ID() != null && inputVO.getCUST_ID().length() >= 10;
		boolean isNotOtherFrom = !inputVO.getFromElsePrintBlank();
		Date BDAY = null;
		SOT712InputVO inputVO_712 = new SOT712InputVO();
		SOT712OutputVO outputVO_712 = new SOT712OutputVO();
		
		String errorMsg = "";
		chkAuth_query(body);//檢核登入者 & 解說人員
		
		try {	
			inputVO.setChk_type("submit");
			
			QueryConditionIF qc = getDataAccessManager().getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			List<Map<String, Object>> checkReview 			= new ArrayList<Map<String,Object>>();
			List<Map<String, Object>> checkDoubleDo 		= new ArrayList<Map<String,Object>>();
			List<Map<String, Object>> checkDeleteFrequency  = new ArrayList<Map<String,Object>>();
			StringBuilder sb = new StringBuilder();
			
			//確認是否做過KYC並已覆核
			//臨櫃/網銀/行銀/奈米投，合計一日限1次
			sb.append(" SELECT COUNT(*) FROM TBKYC_INVESTOREXAM_M ");
			sb.append(" WHERE TRUNC(CREATETIME) = TRUNC(SYSDATE) ");
			sb.append(" AND CUST_ID = :cust_id ");
			sb.append(" AND STATUS = '03' ");
			qc.setObject("cust_id", inputVO.getCUST_ID().toUpperCase());
			qc.setQueryString(sb.toString());
			checkDoubleDo = getDataAccessManager().exeQueryWithoutSort(qc);
			BigDecimal checkDoubleDo_big = (BigDecimal) checkDoubleDo.get(0).get("COUNT(*)");
			int checkDoubleDo_int = checkDoubleDo_big.intValue();

			//2018/08/01之後沒有做過網行銀KYC，臨櫃KYC也沒有輸入過錄音序號，回傳N，需跳題；否則回傳Y
			inputVO_712.setCustID(inputVO.getCUST_ID().toUpperCase());
			SOT712 sot712 = (SOT712) PlatformContext.getBean("sot712");	
			outputVO_712 = sot712.identifyKYCDateAndRecord(inputVO_712);				
			outputVO.setDoneKYC(outputVO_712.getKYCResult());
			
			//非網銀或承作分行為999但網銀有帶IP資訊
			if(checkDoubleDo_int >= 1)
				throw new APException(errorMsg = "ehl_01_KYC310_017");
		
			qc = getDataAccessManager().getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			qc.setQueryString(" Select count(*) from TBKYC_INVESTOREXAM_M where CUST_ID = :cust_id and STATUS = '01' ");
			qc.setObject("cust_id", inputVO.getCUST_ID().toUpperCase());
			checkReview = getDataAccessManager().exeQueryWithoutSort(qc);
			
			BigDecimal checkReview_big = (BigDecimal) checkReview.get(0).get("COUNT(*)");
			int checkReview_int = checkReview_big.intValue();
			
			//#0437 客戶已填過「衍生性金融商品客戶適性評估問卷」
			outputVO.setIsPSR(getPSRResult(inputVO.getCUST_ID().toUpperCase()));
			
			//待審核
			if(checkReview_int >= 1){
				outputVO.setCheckdoquestion(true);
				List<Map<String, Object>> personalIformation = new ArrayList<Map<String,Object>>();
				List<Map<String, Object>> questList = new ArrayList<Map<String, Object>>();
				String DAY = new String();
				String DAYCOD = new String();
				String NIGHT = new String();
				String NIGHTCOD = new String();
				String TEL_NO = new String();
				String FAX = new String();
				//add by Brian
				String DAY_TYPE = new String();
				String NIGHT_TYPE = new String();
				String TEL_NO_TYPE = new String();
				String FAX_TYPE = new String();
				String TELNO_COD = new String();
				String FAX_COD = new String();
				
				//kyc主檔(待審核)　明細　客戶主檔
				qc = getDataAccessManager().getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sb = new StringBuilder();
				sb.append(" select a.EXPIRY_DATE, ");
				sb.append(" 	a.SEQ,a.CUST_NAME,a.CUST_ID,a.EXAM_VERSION,a.CREATE_DATE as KYC_TEST_DATE,c.GENDER,b.CUST_EDUCTION_AFTER as EDUCATION, ");
				sb.append(" 	b.CUST_CAREER_AFTER as CAREER,b.CUST_MARRIAGE_AFTER as MARRAGE,b.CUST_CHILDREN_AFTER as CHILD_NO, ");
				sb.append(" 	b.CUST_HEALTH_AFTER as SICK_TYPE,b.ANSWER_2,a.CUST_RISK_AFR,c.BRA_NBR,c.RPRS_NAME,c.RPRS_ID, ");
				sb.append(" 	b.CUST_TEL,b.CUST_EMAIL as EMAIL_ADDR, b.CUST_EMAIL_BEFORE as EMAIL_BEFORE, b.CUST_ADDRESS as CUST_ADDR_1,b.UPDATE_YN, a.REC_SEQ, ");//電話、EMAIL、地址、是否更新
				sb.append("		TRIM(b.CUST_SCHOOL) AS CUST_SCHOOL, TRIM(b.CUST_EDU_CHANGE) AS CUST_EDU_CHANGE, ");
				sb.append("		b.CUST_EMAIL_BEFORE, b.SAMEEMAIL_REASON, b.SAMEEMAIL_CHOOSE, a.NEED_COMPARISON_YN ");
				sb.append(" from TBKYC_INVESTOREXAM_M a,TBKYC_INVESTOREXAM_D b,TBCRM_CUST_MAST c ");
				sb.append(" where a.SEQ=b.SEQ and a.CUST_ID = b.CUST_ID and a.CUST_ID = c.CUST_ID ");
				sb.append(" and a.CUST_ID = :cust_id and a.STATUS = '01' ");
				qc.setObject("cust_id", inputVO.getCUST_ID().toUpperCase());
				qc.setQueryString(sb.toString());
				personalIformation = getDataAccessManager().exeQueryWithoutSort(qc);
				
				if(personalIformation.size() <= 0)
					throw new APException(errorMsg = "ehl_01_common_009");//客戶主檔、kyc主檔、明細其一找不到：查無資料

				//取電文FC032151學歷、職業、婚姻、子女數
				sot701inputVO.setCustID(inputVO.getCUST_ID());
				fp032151OutputVO = sot701.getFP032151Data(sot701inputVO, header);

				//取電文FC032675重大傷病註記
				sot701inputVO.setCustID(inputVO.getCUST_ID().toUpperCase());
				sot701inputVO.setIsOBU("FAKE"); // 用不到 OBU，所以傳入假的 OBU FLAG 避免撈取 085081_085105、NFEI003
				sot701inputVO.setNeedDesc(false); // 用不到 DESC，設為 false 可避免撈取 NFEI002 電文
				fp032675DataVO = sot701.getFP032675Data(sot701inputVO);
				
				//ＫＹＣ免降等註記
				outputVO.setDegrade(fp032675DataVO.getDegrade());  //沒說明
				
				//ＫＹＣ免降等註記到期日
				String degrade_date_s = fp032675DataVO.getDegradeDate();
				Date degrade_date = null;
				if (StringUtils.isNotBlank(degrade_date_s)) {
					SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
					degrade_date = sdf.parse(degrade_date_s);
				}
				outputVO.setDegradeDate(degrade_date);

				//高資產客戶註記資料
				CustHighNetWorthDataVO hnwcVO = sot701.getHNWCData(inputVO.getCUST_ID());
				outputVO.setHnwcDueDate(hnwcVO.getDueDate());
				outputVO.setHnwcInvaidDate(hnwcVO.getInvalidDate());
				outputVO.setHnwcValidYN(hnwcVO.getValidHnwcYN());
				
				//取得電文 電話資料
				wms032154OutputDetailsVO = sot701.getPhoneData(sot701inputVO);
                checkLostContact(wms032154OutputDetailsVO);
				//取得地址 信箱資料
				fp032675OutputVO = sot701.getAddrandMail(sot701inputVO);

				for (Map<String, Object> addData : personalIformation) {
					//取電文生日   生日轉換成日期給前端
					if(!cbsservice.checkJuristicPerson(sot701inputVO.getCustID())){
						String pattern = "ddMMyyyy";
						BDAY = new SimpleDateFormat(pattern).parse(fp032151OutputVO.getBDAY());
						addData.put("birthday", BDAY);
					}


					
					//把資料庫電話欄位中包含的資訊取出來(包含類型:日、夜、行動、傳真。每一種包含:勾選類型、cod、電話)
					if(addData.get("CUST_TEL") != null){
						//取每一個連絡方式加到contactList中
						String[] split = addData.get("CUST_TEL").toString().split(";");
						int count = 0;  
						for(String str : split){
							//若str最後一碼是: 幫加個空格避免split沒有三個值
							if(StringUtils.equals(":", str.substring(str.length()-1))){
								str = str + " ";
							}
							//切三段 對應 TYPE,COD,DATA
							//處理空值以及null
							String [] detail = str.split(":"); 
							for(int i = 0 ; i < detail.length ; i++){
								if(StringUtils.isBlank(detail[i]) || StringUtils.equals("null",detail[i]) ){
									//如果是TYPE 給2  其它給""
									detail[i] = i == 0 ? "2" : "";
								}
							}
							
							switch(count){
							// 0:日 1:夜 2:手機 3:傳真
							case 0:
								DAY_TYPE = detail[0];
								DAYCOD = detail[1];
								DAY = detail[2];
								break;
							case 1:
								NIGHT_TYPE = detail[0];
								NIGHTCOD = detail[1];
								NIGHT = detail[2];
								break;
							case 2:
								TEL_NO_TYPE = detail[0];
								TELNO_COD = detail[1];
								TEL_NO = detail[2];
								break;
							case 3:	
								FAX_TYPE = detail[0];
								FAX_COD = detail[1];
								FAX = detail[2];
								break;
							}
							count++;
						}


					}
					
//					if(!"null".equals(DAY)){
//						addData.put("DAY", DAY);
//						addData.put("DAYCOD", DAYCOD);
//					}
					addData.put("DAY_TYPE", DAY_TYPE);
					addData.put("DAYCOD", DAYCOD);
					addData.put("DAY", DAY);
										
//					if(!"null".equals(NIGHT)){
//						addData.put("NIGHT", NIGHT);
//						addData.put("NIGHTCOD", NIGHTCOD);
//					}
					addData.put("NIGHT_TYPE", NIGHT_TYPE);
					addData.put("NIGHTCOD", NIGHTCOD);
					addData.put("NIGHT", NIGHT);
										
//					if(!"null".equals(TEL_NO)){
//						addData.put("TEL_NO", TEL_NO);
//					}
					
					addData.put("TEL_NO_TYPE", TEL_NO_TYPE);
					addData.put("TELNO_COD", TELNO_COD);
					addData.put("TEL_NO", TEL_NO);
					
//					if(!"null".equals(FAX)){
//						addData.put("FAX", FAX);
//					}
					
					addData.put("FAX_TYPE", FAX_TYPE);
					addData.put("FAX_COD", FAX_COD);
					addData.put("FAX", FAX);
					
					//取電話電文資料
					List<Map<String, Object>> FC032675Data = new ArrayList<Map<String,Object>>();
					Map<String, Object> FC032675Data_Map = new HashMap<String, Object>();
					FC032675Data_Map.put("DAYCOD", basicVO.getDAY_COD());
					FC032675Data_Map.put("DAY", wms032154OutputDetailsVO.getResd_tel());
					FC032675Data_Map.put("NIGHTCOD", basicVO.getNIGHT_COD());
					FC032675Data_Map.put("NIGHT", wms032154OutputDetailsVO.getCon_tel());
					FC032675Data_Map.put("TELNO_COD", basicVO.getTELNO_COD());
					FC032675Data_Map.put("TEL_NO", wms032154OutputDetailsVO.getHandphone());
					FC032675Data_Map.put("FAX_COD", basicVO.getFAX_COD());
					FC032675Data_Map.put("FAX", wms032154OutputDetailsVO.getFax());
					FC032675Data_Map.put("CUSTADDR_COD", basicVO.getCUSTADDR_COD());
					FC032675Data_Map.put("CUST_ADDR_1", fp032675OutputVO.getDATA3());
					FC032675Data_Map.put("EMAILADDR_COD", basicVO.getEMAILADDR_COD());
					FC032675Data_Map.put("EMAIL_ADDR", fp032675OutputVO.getE_MAIL());
					FC032675Data.add(FC032675Data_Map);
					//取電話電文end
					addData.put("before_persional", FC032675Data);
				}
				
				//設定要輸出的待審核資料
				outputVO.setPersonalIformationList(personalIformation);
				//
				//分割答案(依照題目來分割)
				String[] ansList = ObjectUtils.toString(outputVO.getPersonalIformationList().get(0).get("ANSWER_2")).split(";");
				//依類型找題目
				questList = queryQuestion(inputVO.getQUESTION_TYPE());
				
				//若有該問卷的題目
				if(CollectionUtils.isNotEmpty(questList)){
					for (int questIdx = 0; questIdx < questList.size(); questIdx++) {
						String exam_version 	= ObjectUtils.toString(questList.get(questIdx).get("EXAM_VERSION"));
						String question_version = ObjectUtils.toString(questList.get(questIdx).get("QUESTION_VERSION"));
						String rl_version 		= ObjectUtils.toString(questList.get(questIdx).get("RL_VERSION"));
						List<Map<String, Object>> ansMapList = queryAnswer(exam_version, question_version);//依照問卷版本及題目找出答案
						
						//如果題目的長度少於答案的長度
						if(questIdx < ansList.length && !"".equals(ansList[questIdx].toString())){
							String[] ansList2 = ansList[questIdx].split(",");//切割複選題目
							
							for (Map<String, Object> map : ansMapList) {
								//將那堤有選的設定為true
								for (String ans2 : ansList2) {
									if (map.get("ANSWER_SEQ").toString().equals(ans2.trim())) {
										map.put("select", true);
									}
								}
							}
						}
						//設定該題的檔案
						questList.get(questIdx).put("ANSWER_LIST", ansMapList);
						//依照題目找出差異表答案
						//第三題差異表答案，再開啟差異表頁面時再依商品取一次
						List<Map<String, Object>> ansCompMapList = queryAnswerComp(question_version);
						questList.get(questIdx).put("ANSWER_LIST_COMP", ansCompMapList);
						
						//依照風險級距版號取風險屬性設定
						qc = getDataAccessManager().getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
						sb = new StringBuilder();
						sb.append(" SELECT CUST_RL_ID , RL_NAME,RL_UP_RATE , PROD_RL_UP_RATE ");
						sb.append(" FROM TBKYC_QUESTIONNAIRE_RISK_LEVEL N ");
						sb.append(" WHERE N.RL_VERSION = :rl_version ");
						sb.append(" order by RL_UP_RATE ");
						qc.setObject("rl_version", rl_version);
						qc.setQueryString(sb.toString());
						questList.get(questIdx).put("RISK_LIST", getDataAccessManager().exeQueryWithoutSort(qc));
					}//quest_end	
				}

				//將題目設定給輸出物件
				outputVO.setQuestionnaireList(questList);
					
				//如果不是其他程式直接返回前端
				if(isNotOtherFrom){
					sendRtnObject(outputVO);
				}
				//其他程式
				else
				{
					inputVO.setQuest_list(questList);//題目
					inputVO.setBefore_persional(personalIformation);//第一區塊
					
					//是否更新
					if(personalIformation.get(0).get("UPDATE_YN") != null)
						inputVO.setY_N_update(personalIformation.get(0).get("UPDATE_YN").toString());
					//客戶姓名
					if(personalIformation.get(0).get("CUST_NAME") != null)
						inputVO.setCust_name(personalIformation.get(0).get("CUST_NAME").toString());
					//客戶ID
					if(personalIformation.get(0).get("CUST_ID") != null)
						inputVO.setCUST_ID(personalIformation.get(0).get("CUST_ID").toString().toUpperCase());
					//性別
					if("1".equals(personalIformation.get(0).get("GENDER")))
						inputVO.setGender_M(true);
					else if("2".equals(personalIformation.get(0).get("GENDER")))
						inputVO.setGender_W(true);
					
					inputVO.setBirthday(ObjectUtils.toString(personalIformation.get(0).get("birthday")));
					inputVO.setEDUCATION(ObjectUtils.toString(personalIformation.get(0).get("EDUCATION")));
					inputVO.setCAREER(ObjectUtils.toString(personalIformation.get(0).get("CAREER")));
					inputVO.setMARRAGE(ObjectUtils.toString(personalIformation.get(0).get("MARRAGE")));
					inputVO.setCHILD_NO(ObjectUtils.toString(personalIformation.get(0).get("CHILD_NO")));
					inputVO.setSICK_TYPE(ObjectUtils.toString(personalIformation.get(0).get("SICK_TYPE")));
					inputVO.setCUST_ADDR_1(ObjectUtils.toString(personalIformation.get(0).get("CUST_ADDR_1")));
					inputVO.setEMAIL_ADDR(ObjectUtils.toString(personalIformation.get(0).get("EMAIL_ADDR")));
					
					if(personalIformation.get(0).get("RPRS_ID") != null)
						inputVO.setRPRS_ID(personalIformation.get(0).get("RPRS_ID").toString());
					
					if(personalIformation.get(0).get("RPRS_NAME") != null)
						inputVO.setRPRS_NAME(personalIformation.get(0).get("RPRS_NAME").toString());
					
					inputVO.setDAY(ObjectUtils.toString(personalIformation.get(0).get("DAY")));
					inputVO.setNIGHT(ObjectUtils.toString(personalIformation.get(0).get("NIGHT")));
					inputVO.setTEL_NO(ObjectUtils.toString(personalIformation.get(0).get("TEL_NO")));
					inputVO.setFAX(ObjectUtils.toString(personalIformation.get(0).get("FAX")));
				}
			}
			//不是待簽核
			else{
				//確認該客戶是否已經刪除問卷兩次
				qc = getDataAccessManager().getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sb = new StringBuilder();
				sb.append(" SELECT COUNT(*) FROM TBKYC_INVESTOREXAM_M_HIS ");
				sb.append(" WHERE TRUNC(CREATETIME) = TRUNC(SYSDATE) ");
				sb.append(" AND CUST_ID = :cust_id AND STATUS = '04' ");
				qc.setObject("cust_id", inputVO.getCUST_ID().toUpperCase());
				qc.setQueryString(sb.toString());
				checkDeleteFrequency = getDataAccessManager().exeQueryWithoutSort(qc);

				//一天做超過兩次
				if(((BigDecimal) checkDeleteFrequency.get(0).get("COUNT(*)")).intValue() >= 2){
					outputVO.setDeletetwo(true);
					
					if(isNotOtherFrom)
						sendRtnObject(outputVO);//如果不是其他程式直接返回前端
					else
						throw new APException(errorMsg = "ehl_01_KYC310_002");//一天最多只能執行兩次KYC問卷, 該客戶今天不能再執行
				}
								
				//確認該客戶為新客戶或舊客戶
				List<Map<String, Object>> checkcustomer = exeQueryWithoutSortForQcf(genDefaultQueryConditionIF().setQueryString(
					" SELECT 'x' from TBCRM_CUST_MAST WHERE CUST_ID =:cust_id ")
					.setObject("cust_id", inputVO.getCUST_ID().toUpperCase()));
				
				//新客戶：
				if(checkcustomer.size() <= 0){
					//取得主機客戶基本資料並檢核是否開戶；以及新增客戶主檔及註記檔
					chkNewCust(body, header);
					return;
				}
				
				long birthday = 0;
//				birthday = basicVO.getBirthday() == null ? birthday : basicVO.getBirthday().getTime();
				
				//取上次的風險屬性
				outputVO.setLastRiskLevel(exeQueryWithoutSortForQcf(genDefaultQueryConditionIF().setQueryString(new StringBuffer()
					.append(" select SEQ, to_char(A.create_date,'YYYY-MM-DD HH24:MI:SS') as LastDate, A.CUST_RISK_AFR, B.RL_NAME ")
					.append(" from TBKYC_INVESTOREXAM_M A,TBKYC_QUESTIONNAIRE_RISK_LEVEL B ")
					.append(" WHERE CUST_RISK_AFR = B.CUST_RL_ID ")
					.append(" AND  A.STATUS = '03' ")
					.append(" AND A.CUST_ID = :custid ").toString())
					.setObject("custid", inputVO.getCUST_ID().toUpperCase())));

				//取得第一部分:個人基本資料
				List<Map<String, Object>> personalIformation = exeQueryWithoutSortForQcf(genDefaultQueryConditionIF().setQueryString(new StringBuffer()
					.append(" SELECT CUST_NAME,GENDER,CUST_ID,BRA_NBR,RPRS_ID,RPRS_NAME FROM TBCRM_CUST_MAST ")
					.append(" WHERE CUST_ID = :cust_id ").toString())
					.setObject("cust_id", inputVO.getCUST_ID().toUpperCase()));

				//取電文FC032151學歷、職業、婚姻、子女數					
				sot701inputVO.setCustID(inputVO.getCUST_ID());
				fp032151OutputVO = sot701.getFP032151Data(sot701inputVO, header);

				//取電文FC032675重大傷病註記
				sot701inputVO.setCustID(inputVO.getCUST_ID().toUpperCase());
				sot701inputVO.setIsOBU("FAKE"); // 用不到 OBU，所以傳入假的 OBU FLAG 避免撈取 085081_085105、NFEI003
				sot701inputVO.setNeedDesc(false); // 用不到 DESC，設為 false 可避免撈取 NFEI002 電文
				fp032675DataVO	 = sot701.getFP032675Data(sot701inputVO);

				
				//ＫＹＣ免降等註記
//				outputVO.setDegrade("Y");  //沒說明
				outputVO.setDegrade(fp032675DataVO.getDegrade());  //沒說明
				
				//ＫＹＣ免降等註記到期日
				String degrade_date_s = fp032675DataVO.getDegradeDate();
//				String degrade_date_s = "20230915";
				Date degrade_date = null;
				if (StringUtils.isNotBlank(degrade_date_s)) {
					SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
					degrade_date = sdf.parse(degrade_date_s);
				}
				outputVO.setDegradeDate(degrade_date);

				//高資產客戶註記資料
				CustHighNetWorthDataVO hnwcVO = sot701.getHNWCData(inputVO.getCUST_ID());
				outputVO.setHnwcDueDate(hnwcVO.getDueDate());
				outputVO.setHnwcInvaidDate(hnwcVO.getInvalidDate());
				outputVO.setHnwcValidYN(hnwcVO.getValidHnwcYN());
				
				//取得電文 電話資料
				wms032154OutputDetailsVO = sot701.getPhoneData(sot701inputVO);
				checkLostContact(wms032154OutputDetailsVO);
				//取得地址 信箱資料
				fp032675OutputVO = sot701.getAddrandMail(sot701inputVO);

				
				//逐筆設定電文資料
				for (Map<String, Object> addData : personalIformation) {
					//電文生日
					//取電文生日   生日轉換成日期給前端
					if(!cbsservice.checkJuristicPerson(sot701inputVO.getCustID())){
						String pattern = "ddMMyyyy";
						BDAY = new SimpleDateFormat(pattern).parse(fp032151OutputVO.getBDAY());
						addData.put("birthday", BDAY);
					}
//					addData.put("birthday", BDAY);
					addData.put("DAY", wms032154OutputDetailsVO.getResd_tel()); //電話1
					addData.put("DAYCOD", basicVO.getDAY_COD());
					addData.put("NIGHT", wms032154OutputDetailsVO.getCon_tel()); //電話2
					addData.put("NIGHTCOD", basicVO.getNIGHT_COD());
					addData.put("TELNO_COD", basicVO.getTELNO_COD());
					addData.put("TEL_NO", wms032154OutputDetailsVO.getHandphone()); //手機
					addData.put("FAX_COD", basicVO.getFAX_COD());
					addData.put("FAX", wms032154OutputDetailsVO.getFax()); //傳真
					addData.put("CUSTADDR_COD", basicVO.getCUSTADDR_COD());
					addData.put("CUST_ADDR_1", fp032675OutputVO.getDATA3()); //通訊地址
					addData.put("EMAILADDR_COD", basicVO.getEMAILADDR_COD());
					addData.put("EMAIL_ADDR", fp032675OutputVO.getE_MAIL()); //mail

					addData.put("EDUCATION", fp032151OutputVO.getEDUCATION());
					addData.put("CAREER", fp032151OutputVO.getCAREER());
					addData.put("MARRAGE", fp032151OutputVO.getMARRAGE());
					addData.put("CHILD_NO", fp032151OutputVO.getCHILD_NO());
					addData.put("SICK_TYPE", fp032675DataVO.getSickType());
					addData.put("TX_FLAG", fp032675DataVO.getCustProFlag());
					
					// 是否為弱勢（Y/N）
					String age_un70_flag = fp032675DataVO.getAgeUnder70Flag();
					String edu_ov_jr_flag = fp032675DataVO.getEduJrFlag();
					String health_flag = fp032675DataVO.getHealthFlag();
					String vul_flag = "N";
					if ("N".equals(age_un70_flag) || "N".equals(edu_ov_jr_flag) || "N".equals(health_flag)) {
						vul_flag = "Y";
					}
					addData.put("VUL_FLAG", vul_flag);
					
					// 專業投資人效期
					Date cust_pro_date = fp032675DataVO.getCustProDate();
					addData.put("CUST_PRO_DATE", cust_pro_date);
					
					SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
					String today_s = sdf.format(new Date());
					Date today =  sdf.parse(today_s);
					
					if (cust_pro_date != null) {
						if (cust_pro_date.after(today)) {
							addData.put("CUST_PRO_END", "N");
						} else {
							addData.put("CUST_PRO_END", "Y");
						}
					} else {
						addData.put("CUST_PRO_END", null);
					}
					
					if (degrade_date != null) {
						if (degrade_date.after(today) || degrade_date.equals(today)) {
							// 免降等註記未到期
							addData.put("DEGRADE_END", "N");
						} else {
							addData.put("DEGRADE_END", "Y");
						}
					} else {
						addData.put("DEGRADE_END", null);
					}
				}
				
				//設定ouput基本資料
				outputVO.setPersonalIformationList(personalIformation);
				birthday = BDAY == null ? 0 : BDAY.getTime();

				//計算生日的年月
				int age = new Date().getYear() - new Date(birthday).getYear();
				int age_month = new Date().getMonth() - new Date(birthday).getMonth();

				if(age_month < 0)
					age--;
				
				outputVO.setCustEduHighSchool("N");
				//自然人高中職以上學歷註記
				if(StringUtils.equals("02", inputVO.getQUESTION_TYPE())) {
					SOT701InputVO sot701vo = new SOT701InputVO();
					sot701vo.setCustID(inputVO.getCUST_ID().toUpperCase());
					SOT701 sot701 = (SOT701) PlatformContext.getBean("sot701");	
					
					outputVO.setCustEduHighSchool(sot701.getCustEduHighSchool(sot701vo));
				}
				
//				//取得法代與年收入資料
//				Map<String, String> legalRegMap = getData067050ByType(inputVO.getCUST_ID()); 
//				//年收入
//				BigDecimal yearIncome = BigDecimal.ZERO;
//				if(StringUtils.isNotBlank(legalRegMap.get("INCOME_FROM_CBS"))) {
//					try {
//						yearIncome = new BigDecimal(legalRegMap.get("INCOME_FROM_CBS"));
//					} catch(Exception e) {
//						yearIncome = BigDecimal.ZERO;
//					}
//				}
//				outputVO.setIncomeFromCBS(yearIncome); //端末系統留存的年收入
				//未滿18歲自然人，取得法定代理人KYC
				if(inputVO.getCUST_ID().length() >= 10 && age < 18) {
					//取得法代與年收入資料
					Map<String, String> legalRegMap = getData067050ByType(inputVO.getCUST_ID()); 
					//年收入
					BigDecimal yearIncome = BigDecimal.ZERO;
					if(StringUtils.isNotBlank(legalRegMap.get("INCOME_FROM_CBS"))) {
						try {
							yearIncome = new BigDecimal(legalRegMap.get("INCOME_FROM_CBS"));
						} catch(Exception e) {
							yearIncome = BigDecimal.ZERO;
						}
					}
					//取得法代資料
					if(StringUtils.isNotBlank(legalRegMap.get("REP_RISK_1")) && StringUtils.isBlank(legalRegMap.get("REP_RISK_2"))) {
						//法代1有風險屬性，法代2沒有；取法代1風險屬性
						outputVO.setLegalRegKycLevel(legalRegMap.get("REP_RISK_1"));
					} else if(StringUtils.isBlank(legalRegMap.get("REP_RISK_1")) && StringUtils.isNotBlank(legalRegMap.get("REP_RISK_2"))) {
						//法代2有風險屬性，法代1沒有；取法代2風險屬性
						outputVO.setLegalRegKycLevel(legalRegMap.get("REP_RISK_2"));
					} else if(StringUtils.isNotBlank(legalRegMap.get("REP_RISK_1")) && StringUtils.isNotBlank(legalRegMap.get("REP_RISK_2"))) {
						//法代1與法代2都有風險屬性，取孰低
						if(Integer.parseInt(legalRegMap.get("REP_RISK_1").substring(1)) > Integer.parseInt(legalRegMap.get("REP_RISK_2").substring(1))) {
							outputVO.setLegalRegKycLevel(legalRegMap.get("REP_RISK_2"));
						} else {
							outputVO.setLegalRegKycLevel(legalRegMap.get("REP_RISK_1"));
						}
					}
				}else {outputVO.setIncomeFromCBS(BigDecimal.ZERO);}
				
				//依照類型取題目(法人、自然人)
				List<Map<String, Object>> questList = queryQuestion(inputVO.getQUESTION_TYPE());//找題目
				
				//若找到該份問卷的答案
				if(CollectionUtils.isNotEmpty(questList)){
					//每題找尋各自的答案選項
					for(Map<String,Object> map : questList){
						String exam_version 	= (String) map.get("EXAM_VERSION");//問卷版本
						String question_version = (String) map.get("QUESTION_VERSION");//題目序號
						String rl_version 		= (String) map.get("RL_VERSION");//風險級距編號
						
						//取指定問卷版本及對應題目的答案列表
						List<Map<String, Object>> ansList = queryAnswer(exam_version, question_version);
						
						//第三題特殊條件
						if("3".equals(map.get("QST_NO").toString())){
							//生日離系統日不到3年
							if(age < 3){
								for(Map<String, Object> ans:ansList){
									//選項1、2
									if(ans.get("ANSWER_SEQ").toString().substring(1, 2).equals("1") || ans.get("ANSWER_SEQ").toString().substring(1, 2).equals("2")){
										ans.put("ansdisabled", false);
									}else{
										ans.put("ansdisabled", true);
									}
								}
							}
							//生日離系統日不到5年
							else if(age < 5){
								for(Map<String, Object> ans:ansList){
									//選項4、5
									if(ans.get("ANSWER_SEQ").toString().substring(1, 2).equals("4") || ans.get("ANSWER_SEQ").toString().substring(1, 2).equals("5")){
										ans.put("ansdisabled", true);
									}else{
										ans.put("ansdisabled", false);
									}
								}
							}
							//生日離系統日不到10年
							else if(age < 10){
								for(Map<String, Object> ans:ansList){
									//選項5
									if(ans.get("ANSWER_SEQ").toString().substring(1, 2).equals("5")){
										ans.put("ansdisabled", true);
									}else{
										ans.put("ansdisabled", false);
									}
								}
							}
							//其他
							else{
								for(Map<String, Object> ans:ansList){
									ans.put("ansdisabled", false);
								}
							}
						}
						//將選項埋回問題中
						map.put("ANSWER_LIST", ansList);
						//依照題目找出差異表答案
						//第三題差異表答案，再開啟差異表頁面時再依商品取一次
						List<Map<String, Object>> ansCompMapList = queryAnswerComp(question_version);
						map.put("ANSWER_LIST_COMP", ansCompMapList);
						
						//risk
						qc = getDataAccessManager().getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
						sb = new StringBuilder();
						sb.append(" SELECT CUST_RL_ID,RL_NAME,RL_UP_RATE,PROD_RL_UP_RATE ");
						sb.append(" FROM TBKYC_QUESTIONNAIRE_RISK_LEVEL N ");
						sb.append(" WHERE N.RL_VERSION = :rl_version ");
						sb.append(" order by RL_UP_RATE ");
						qc.setObject("rl_version", rl_version);
						qc.setQueryString(sb.toString());
						
						//將風險屬性相關資訊埋回問題中
						map.put("RISK_LIST", getDataAccessManager().exeQueryWithoutSort(qc));
					}
				}
			
				
				//設定output題目答案
				outputVO.setQuestionnaireList(questList);
					
				//取上次的明細
				qc = getDataAccessManager().getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sb = new StringBuilder();
				sb.append(" select CUST_EDUCTION_AFTER , CUST_CAREER_AFTER , CUST_MARRIAGE_AFTER , CUST_CHILDREN_AFTER , CUST_HEALTH_AFTER ");
				sb.append(" from TBKYC_INVESTOREXAM_D N where seq = :seq ");
				
				if(outputVO.getLastRiskLevel().size() <= 0){
					qc.setObject("seq", null);
				}else{
					qc.setObject("seq", outputVO.getLastRiskLevel().get(0).get("SEQ").toString());
				}
				
				qc.setQueryString(sb.toString());
				outputVO.setLast_firs_ans(getDataAccessManager().exeQueryWithoutSort(qc));
				
				//如果不是其他程式直接返回前端
				if(isNotOtherFrom){
					sendRtnObject(outputVO);
				}
				else{
					inputVO.setQuest_list(questList);
					if(personalIformation.size()>0){
						inputVO.setBefore_persional(personalIformation);
						if(personalIformation.get(0).get("UPDATE_YN") != null){
							inputVO.setY_N_update(personalIformation.get(0).get("UPDATE_YN").toString());
						}
						
						inputVO.setCust_name(ObjectUtils.toString(personalIformation.get(0).get("CUST_NAME")));
						inputVO.setCUST_ID(ObjectUtils.toString(personalIformation.get(0).get("CUST_ID")).toUpperCase());
						
						if(personalIformation.get(0).get("GENDER") != null){
							switch (personalIformation.get(0).get("GENDER").toString()) {
							case "1":
								inputVO.setGender_M(true);
								break;
							case "2":
								inputVO.setGender_W(true);
								break;
							default:
								break;
							}
						}						
						
						inputVO.setBirthday(ObjectUtils.toString(personalIformation.get(0).get("birthday")));
						inputVO.setEDUCATION(ObjectUtils.toString(personalIformation.get(0).get("EDUCATION")));
						inputVO.setCAREER(ObjectUtils.toString(personalIformation.get(0).get("CAREER")));
						inputVO.setMARRAGE(ObjectUtils.toString(personalIformation.get(0).get("MARRAGE")));
						inputVO.setCHILD_NO(ObjectUtils.toString(personalIformation.get(0).get("CHILD_NO")));
						inputVO.setSICK_TYPE(ObjectUtils.toString(personalIformation.get(0).get("SICK_TYPE")));
						inputVO.setCUST_ADDR_1(ObjectUtils.toString(personalIformation.get(0).get("CUST_ADDR_1")));
						inputVO.setEMAIL_ADDR(ObjectUtils.toString(personalIformation.get(0).get("EMAIL_ADDR")));
						
						if(personalIformation.get(0).get("RPRS_ID") != null){
							inputVO.setRPRS_ID(ObjectUtils.toString(personalIformation.get(0).get("RPRS_ID")));
						}
						if(personalIformation.get(0).get("RPRS_NAME") != null){
							inputVO.setRPRS_NAME(ObjectUtils.toString(personalIformation.get(0).get("RPRS_NAME")));
						}
						
						inputVO.setDAY(ObjectUtils.toString(personalIformation.get(0).get("DAY")));
						inputVO.setNIGHT(ObjectUtils.toString(personalIformation.get(0).get("NIGHT")));
						inputVO.setTEL_NO(ObjectUtils.toString(personalIformation.get(0).get("TEL_NO")));
						inputVO.setFAX(ObjectUtils.toString(personalIformation.get(0).get("FAX")));
					}
				}
			}//客戶基本資料end	
			//已做KYC未覆核
		} catch(APException ape){
			if(errorMsg.length() > 0){
				throw new APException(errorMsg);
			}else if(ape.getMessage().contains("共同行銷註記查無資料")){
				throw new APException("客戶尚未開戶: 共同行銷註記查無資料");
			}else if(ape.getMessage().contains("無此客戶統編")){
				throw new APException("客戶尚未開戶: 電文067050-067101  查無此客戶統編/統編類型不正確 ");
			}else{
				throw new APException(errorMsg = "ehl_01_common_029");
			}
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new APException(e.getMessage());
		}
	}

	/*
	 * 檢查失聯註記
	 */
	public void checkLostContact(WMS032154OutputDetailsVO detail) throws Exception {

		if("Y".equals(detail.getLOST_FLG1()) ||
				"Y".equals(detail.getLOST_FLG2()) ||
				"Y".equals(detail.getLOST_FLG3()) ||
				"Y".equals(detail.getLOST_FLG4()) ||
				"Y".equals(detail.getLOST_FLG5()) ||
				"Y".equals(detail.getLOST_FLG6()) ||
				"Y".equals(detail.getLOST_FLG7()) ||
				"Y".equals(detail.getLOST_FLG8()) ||
				"Y".equals(detail.getLOST_FLG9()) ||
				"Y".equals(detail.getLOST_FLG10()) ||
				"Y".equals(detail.getLOST_FLG11()) ||
				"Y".equals(detail.getLOST_FLG12()) ||
				"Y".equals(detail.getLOST_FLG13()) ||
				"Y".equals(detail.getLOST_FLG14()) ||
				"Y".equals(detail.getLOST_FLG15()) ||
				"Y".equals(detail.getLOST_FLG16()) ||
				"Y".equals(detail.getLOST_FLG17()) ||
				"Y".equals(detail.getLOST_FLG18()) ||
				"Y".equals(detail.getLOST_FLG19()) ){
			throw new Exception("無法承作! 請客戶重新檢視通訊資料並於端末06750100解除失聯戶註記");
			
		}
	}

	//查詢題目
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> queryQuestion(String question_type){
		List<Map<String, Object>> questList = new ArrayList<Map<String,Object>>();

		if(StringUtils.isNotBlank(question_type)){
			try {
				QueryConditionIF qc = getDataAccessManager().getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				StringBuilder sb = new StringBuilder();
				
				//問卷資料檔，題庫題目檔
				sb = new StringBuilder();
				sb.append(" SELECT n.EXAM_VERSION,n.QUESTION_VERSION,n.EXAM_NAME,n.QUEST_TYPE, ");
				sb.append(" n.QST_NO,n.ESSENTIAL_FLAG,n.RL_VERSION,n.STATUS,n.ACTIVE_DATE, n.SCORE_TYPE, n.INT_SCORE, n.RS_VERSION, n.RLR_VERSION, ");
				sb.append(" Q.QUESTION_DESC,Q.QUESTION_DESC_ENG,Q.QUESTION_TYPE,Q.ANS_OTHER_FLAG,Q.ANS_MEMO_FLAG,Q.DEFINITION,Q.PICTURE ");
				sb.append(" from TBSYS_QUESTIONNAIRE n,TBSYS_QST_QUESTION Q ");
				sb.append(" where n.STATUS = '02' AND n.QUESTION_VERSION = Q.QUESTION_VERSION ");//待啟用
				sb.append(" and n.QUEST_TYPE = :quest_type ");
				//該類型問卷待啟用日期為系統日前最大的那個待啟用日
				sb.append(" AND (ACTIVE_DATE) = (SELECT MAX(ACTIVE_DATE) FROM TBSYS_QUESTIONNAIRE WHERE STATUS = '02' ");
				sb.append(" AND QUEST_TYPE = :quest_type AND ACTIVE_DATE <= SYSDATE) order by QST_NO "); //預計啟用日小於等於系統日
//				sb.append(" AND n.EXAM_VERSION = 'KYC202003097811' order by QST_NO ");	//測試:法人 KYC202003097811 自然人KYC202003097859 //測試:法人 KYC202001082817 自然人KYC201912196748
				qc.setObject("quest_type", question_type);
				qc.setQueryString(sb.toString());
				questList = getDataAccessManager().exeQueryWithoutSort(qc);
				
			} catch (JBranchException e) {
				// Auto-generated catch block
				e.printStackTrace();
			}
		}
			return questList;
	}
	
	//答案
	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> queryAnswer(String exam_version,String question_version){
		
		List<Map<String, Object>> ansList = new ArrayList<Map<String,Object>>();
		
		if(StringUtils.isNotBlank(exam_version) && StringUtils.isNotBlank(question_version)){
			try {
				QueryConditionIF qc = getDataAccessManager().getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				StringBuilder sb = new StringBuilder();
				//answer
				sb.append(" SELECT A.QUESTION_VERSION,A.ANSWER_SEQ,A.ANSWER_DESC,A.ANSWER_DESC_ENG,W.FRACTION ");
				sb.append(" FROM TBSYS_QST_ANSWER A,TBKYC_QUESTIONNAIRE_ANS_WEIGHT W ");//題目答案列表檔、答案權重檔
				sb.append(" WHERE A.QUESTION_VERSION = W.QUESTION_VERSION and A.ANSWER_SEQ = W.ANSWER_SEQ ");
				sb.append("  AND W.EXAM_VERSION = :exam_version AND A.QUESTION_VERSION = :question_version ");
				sb.append(" ORDER BY A.ANSWER_SEQ ");
				qc.setObject("exam_version", exam_version);
				qc.setObject("question_version", question_version);
				qc.setQueryString(sb.toString());
				ansList = getDataAccessManager().exeQueryWithoutSort(qc);
				
			} catch (JBranchException e) {
				// Auto-generated catch block
				e.printStackTrace();
			}
		}
		return ansList;
	}
	
	//取得差異表答案選項
	public List<Map<String, Object>> queryAnswerComp(String question_version) throws DAOException, JBranchException{
		
		List<Map<String, Object>> ansList = new ArrayList<Map<String,Object>>();
		
		if(StringUtils.isNotBlank(question_version)){
			QueryConditionIF qc = getDataAccessManager().getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuilder sb = new StringBuilder();
			//answer
			sb.append(" SELECT A.QUESTION_VERSION,A.ANSWER_SEQ,A.ANSWER_DESC,A.ANSWER_DESC_ENG ");
			sb.append(" FROM TBSYS_QST_ANSWER_COMP A ");//題目差異表答案列表檔
			sb.append(" WHERE A.QUESTION_VERSION = :question_version ");
			sb.append(" ORDER BY A.ANSWER_SEQ ");
			qc.setObject("question_version", question_version);
			qc.setQueryString(sb.toString());
			ansList = getDataAccessManager().exeQueryWithoutSort(qc);
		}
		return ansList;
	}

	//只查問卷
	public void NonCustID(Object body, IPrimitiveMap<Object> header) throws Exception{
		
		KYC310InputVO inputVO = (KYC310InputVO) body;
		KYC310OutputVO outputVO = new KYC310OutputVO();
		
		//設定列印空白表單操作行為
		inputVO.setChk_type("print");
		
		//解說人員＆鑑機人員檢核
		chkAuth_query(body);
		
		//題目
		List<Map<String, Object>> questList = new ArrayList<Map<String,Object>>();
		questList = queryQuestion(inputVO.getQUESTION_TYPE());
		
		if(questList.size()>0){
			for(Map<String,Object> map:questList){
				String exam_version = (String) map.get("EXAM_VERSION");
				String question_version = (String) map.get("QUESTION_VERSION");
				String rl_version = (String) map.get("RL_VERSION");
				
				//答案
				List<Map<String, Object>> ansList = queryAnswer(exam_version, question_version);				
				map.put("ANSWER_LIST", ansList);
				//依照題目找出差異表答案
				//第三題差異表答案，再開啟差異表頁面時再依商品取一次
				List<Map<String, Object>> ansCompMapList = queryAnswerComp(question_version);
				map.put("ANSWER_LIST_COMP", ansCompMapList);
			}
		}
		
		outputVO.setQuestionnaireList(questList);
		sendRtnObject(outputVO);
	}

    /**
     * 登入者 v.s 解說人員 v.s 自然人/法人的檢核
	 *
	 * 1. QUESTION_TYPE: 自然人=02; 法人=03 
     * 2. Chk_type     : 列印=print; 查詢=submit
	 *
	 * 使用method: chk_cert證照檢核
	 *             chkRole角色檢核
	 *
     * @param  body
	 * @return Boolean
     * @throws Exception 
     */
	 public void chkAuth_query(Object body) throws Exception{
		KYC310InputVO inputVO = (KYC310InputVO) body;//inputVO
		String loginID = (String) getCommonVariable(SystemVariableConsts.LOGINID);//登入者ID(鍵機人員)
		String staff = inputVO.getCOMMENTARY_STAFF();//解說員
		String qType = inputVO.getQUESTION_TYPE() == null ? "" : inputVO.getQUESTION_TYPE();//法人or自然人
		boolean isBusBlock = KYCCheck.chkIsBusBlock(staff);//解說員為企金板塊
		boolean isPersonalBlock = !isBusBlock;//解說員為個金板塊(非企金板塊則為個金板塊)
		boolean isBusOP = KYCCheck.chkRole(loginID, LEGAL_PERSON_FINANCE_OP);//判斷是否為企金OP
		boolean isPersonalOP= KYCCheck.chkRole(loginID, PERSONAL_FINANCE_OP);//判斷解說員為個金OP
		boolean isBranchSig= KYCCheck.chkRole(loginID, BRANCH_SIGNATURE);//判斷是否為分行有權簽章
		boolean isBranchAuth= KYCCheck.chkRole(loginID, BRANCH_AUTHORIZED);//判斷是否為分行覆核人員
		Integer cnt = null;
		
		if(!KYCCheck.checkIsMember(staff))//解說人員是否為行員
			throw new APException("ehl_01_KYC310_029");
//		else if(!KYCCheck.chk_cert(staff))//不分來源解說人員必須有信託證照 
//			throw new APException("ehl_01_KYC310_030");
		else if(!KYCCheck.chk_cert_derive(staff))//不分來源解說人員必須有信託證照以及衍商推介證照 (這隻電文會檢查這兩個證照)
			throw new APException("ehl_01_KYC310_040");
		else if(isBusBlock && !KYCCheck.chkRole(staff , ARM) && !KYCCheck.chkRole(staff , TRADER))//解說人員為企金板塊時檢核是否為ARM或RM或交易員
			throw new APException("ehl_01_KYC310_031");
		else if(isPersonalBlock && (cnt = KYCCheck.queryOtherRoleCnt(staff , "002" , "003")) != null && cnt > 0)//解說人員為個金板塊時，不可以為理專 
			throw new APException("ehl_01_KYC310_032");
		else if(!(isBusOP || isPersonalOP || isBranchSig || isBranchAuth))//鍵機人員必須為企金、個金OP、有權簽章、覆核人員
			throw new APException("ehl_01_KYC310_033");
		else if(isBusOP && !"03".equals(qType))//如果鍵機人員為企金OP時，僅可(填寫/列印)法人問卷
			throw new APException("ehl_01_KYC310_034");
		else if(isPersonalOP && !qType.matches("0(2|3)"))//如果鍵機人員為個金OP時，僅可(填寫/列印)法人及自然人問卷
			throw new APException("ehl_01_KYC310_035");
//		else if(FILL_QUESTIONNAIRE_OVER.equals(inputVO.getChk_type()) && !KYCCheck.chk_cert(loginID))//填寫問卷時，鍵機人員必須持有信託證照
//			throw new APException("ehl_01_KYC310_036");	//未來僅檢核解說人員是否同時符合信託及衍商資格
	 }
	
	//檢核by儲存
	public void chkAuth(Object body, IPrimitiveMap<Object> header) throws JBranchException{
		try{
			KYC310InputVO inputVO = (KYC310InputVO) body;
			KYC310OutputVO outputVO = new KYC310OutputVO();
			SOT701InputVO sot701inputVO = new SOT701InputVO();
						
			if("submit".equals(inputVO.getChk_type())){	
				//儲存
//				String comExperience = getComExperience(inputVO.getCUST_ID().toUpperCase());
//				
//				if(!"6".equals(comExperience)){
//					derivative(inputVO.getQuest_list(), comExperience, outputVO);
//				}
				
				//取得Q10~Q11答案，給前端做投資虧損檢核，Q10 ~ Q11
				getQ1011Ans(inputVO.getQuest_list(), outputVO);
				
				sendRtnObject(outputVO);
			} else {	//列印空白表單
				//取電文FC032675簽署推介同意書註記&特定客戶註記
				sot701inputVO.setCustID(inputVO.getCUST_ID().toUpperCase());
				sot701inputVO.setIsOBU("FAKE"); // 用不到 OBU，所以傳入假的 OBU FLAG 避免撈取 085081_085105、NFEI003
				sot701inputVO.setNeedDesc(false); // 用不到 DESC，設為 false 可避免撈取 NFEI002 電文
				FP032675DataVO fp032675DataVO = sot701.getFP032675Data(sot701inputVO);
				
				getPicture(inputVO);
				String filePath = "";
				KYC310InputVO test = new KYC310InputVO();
				test.setEDUCATIONList(inputVO.getEDUCATIONList());
				test.setCAREERList(inputVO.getCAREERList());
				test.setMARRAGEList(inputVO.getMARRAGEList());
				test.setCHILD_NOList(inputVO.getCHILD_NOList());
				test.setSICK_TYPEList(inputVO.getSICK_TYPEList());
				test.setQuest_list(inputVO.getQuest_list());
				test.setCUST_ID("");
				test.setEDUCATION("");
				test.setCAREER("");
				test.setMARRAGE("");
				test.setCHILD_NO("");
				test.setCUST_ADDR_1("");
				if(PRINT_QUESTIONNAIRE.equals(inputVO.getChk_type())){
					//列印中文空白表單
					if ("03".equals(inputVO.getQUESTION_TYPE())){	//法人中文
						KYC310_CorpRptEmpty report = PlatformContext.getBean("kyc310CorpRptEmpty", KYC310_CorpRptEmpty.class);
						report.setInputVO(test);
						filePath = report.genBlankPDF();
					} else {	//自然人中文
						KYC310_PersonRptEmpty report = PlatformContext.getBean("kyc310PersonRptEmpty", KYC310_PersonRptEmpty.class);
						report.setInputVO(inputVO);
						filePath = report.genBlankPDF();
					}
				} else if(PRINT_QUESTIONNAIRE_ENG.equals(inputVO.getChk_type())) {
					//列印英文空白表單
					if ("03".equals(inputVO.getQUESTION_TYPE())){	//法人英文
						KYC310_CorpRptEmpty_ENG report = PlatformContext.getBean("kyc310CorpRptEmptyENG", KYC310_CorpRptEmpty_ENG.class);
						report.setInputVO(test);
						filePath = report.genBlankPDF();
					} else {	//自然人英文
						KYC310_PersonRptEmpty_ENG report = PlatformContext.getBean("kyc310PersonRptEmptyENG", KYC310_PersonRptEmpty_ENG.class);
						report.setInputVO(inputVO);
						filePath = report.genBlankPDF();
					}
				}
				
				List<String> mergeUrl = new ArrayList();
				mergeUrl.add(filePath);
				
				if(!StringUtils.equals("Y", fp032675DataVO.getCustRemarks()) && !StringUtils.equals("Y", fp032675DataVO.getCustProRemark())) {
					//若非特定客戶且尚未簽屬信託推介同意書，則需一併產出信託推介同意書
					PRDFitInputVO pdfInputVO = new PRDFitInputVO();
					pdfInputVO.setCaseCode(2);
					pdfInputVO.setCustId(inputVO.getCUST_ID());
//					mergeUrl.addAll(new SotPdfContext(pdfInputVO, "sot813").getSotPdfULst());
				}
				
				String url = PdfUtil.process(new PdfConfigVO(this.getDataAccessManager(), true, mergeUrl));
//	        	notifyClientToDownloadFile(url, PRINT_QUESTIONNAIRE.equals(inputVO.getChk_type()) ? "KYC空白表單.pdf" : "KYC英文空白表單.pdf");
				notifyClientViewDoc(url, "pdf");
			} 
		} catch(Exception e) {
			throw new JBranchException(e);
		}
	}

	/***
	 * 取得Q10~Q11答案，給前端做投資虧損檢核
	 * @param questList
	 * @param outputVO
	 * @throws APException
	 */
	private void getQ1011Ans(List<Map<String, Object>> questList, KYC310OutputVO outputVO) throws APException {
		if(CollectionUtils.isEmpty(questList)) return;	
		
		for(int i = 0 ; i < questList.size() ; i++) {
			String qstNo = ObjectUtils.toString((((Double)questList.get(i).get("QST_NO")).intValue()));
			List<Map<String, Object>> ansList = (List<Map<String, Object>>) questList.get(i).get("ANSWER_LIST");
			
			if(StringUtils.equals("10", qstNo) || StringUtils.equals("11", qstNo)) {	//檢查第10~11題答案
				for(Map<String, Object> ans : ansList) {
					if(ans.get("select") != null && StringUtils.equals("true", ans.get("select").toString())) {
						if(StringUtils.equals("10", qstNo)) outputVO.setAnsSeqQ10(ObjectUtils.toString((((Double)ans.get("ANSWER_SEQ")).intValue())));//答案選項
						if(StringUtils.equals("11", qstNo)) outputVO.setAnsSeqQ11(ObjectUtils.toString((((Double)ans.get("ANSWER_SEQ")).intValue())));//答案選項
					}
				}
			}
		}
		
		return;
	}
	
	//檢核交易經驗
//	public String getComExperience(String CUST_ID) throws JBranchException{
//		SOT701InputVO sot701InputVO = new SOT701InputVO();
//		sot701InputVO.setCustID(CUST_ID.toUpperCase());
//		String comExperience = null;
//		
//		try {
//			comExperience = sot701.getCustComExp(sot701InputVO).trim();//客戶投資經驗
//		
//			if(StringUtils.isBlank(comExperience)){
//				comExperience = "6";
//			}
//			else{
//				String[] comExperienceList = comExperience.split(",");
//				
//				for(int a = 0; a < comExperienceList.length ; a++){
//					if(ObjectUtils.toString(comExperienceList[a]).matches("[1,2,3,4]")){
//						comExperience="4";
//						break;
//					}else if("5".equals(comExperienceList[a].toString())){
//						comExperience="5";
//					}
//				}
//			}
//		} catch (Exception e) {
//			// Auto-generated catch block
//			e.printStackTrace();
//		}
//		return comExperience;
//	}

	/***
	 * 檢核理財商品投資經驗差異
	 * 舊題型檢查第8 & 9題
	 * 新題型檢查第3題
	 * @param isNewQues: 是否為新題型
	 * @param answer2 : 上一次KYC測驗答案
	 * @param questList : 題目
	 * @param outputVO : 回傳值
	 * @throws JBranchException 
	 */
	public void checkProdExperience(Boolean isNewQues, String answer2 , List<Map<String, Object>> questList , KYC310OutputVO outputVO) throws JBranchException{
		XmlInfo xmlinfo = new XmlInfo();
		String preAns = "";	//上一次KYC測驗，商品投資經驗答案
		List<Map<String, Object>> q3ProdExpChgList = new ArrayList<Map<String, Object>>();
		String q3ProdDecrease = "";
		
		//取得上一次KYC測驗，商品投資經驗答案
		if(!isNewQues) {
			//舊題型答案
			preAns = getOld89Ans(answer2);
		} else {
			//新題型答案
			if(StringUtils.isNotBlank(answer2)) {
				String[] ansList = answer2.split(";");//切割答案
				//第3題
				if(ansList.length >= 3 && ansList[2].toString().replaceAll("\\,|\\;|\\s", "").matches("\\d+")){
					preAns = ansList[2].trim();
				}
			}
		}
		
		//前一次答案不是空的且，本次也不是
		if(CollectionUtils.isNotEmpty(questList) && StringUtils.isNotBlank(preAns)) {
			List<Map<String, Object>> quest3 = (List<Map<String, Object>>) questList.get(2).get("ANSWER_LIST");
			
			for(Map<String, Object> ans : quest3) {	//本次答案
				if(ans.get("select") != null && (boolean) ans.get("select")) {
					String ansSEQ = ans.get("ANSWER_SEQ").toString().trim();
					if(ansSEQ.length() > 2) ansSEQ = ansSEQ.substring(0, 2);
					String answerDesc = ans.get("ANSWER_DESC").toString().trim();
					String prdType = ansSEQ.substring(0,  1);
					
					//若為上一次KYC測驗為舊題目，只需檢查基金、股票及衍生性商品才需要檢查是否上升2級以上
					//因為舊題目的第9題只針對基金、股票及衍生性商品年期
					if(isNewQues || prdType.matches("4|5|6")) {
						//檢核投資經驗年期是否比上次上升2級(含)以上
						Map<String, Object> q3ProdExpChg = prodExpUp2Degree(ansSEQ, preAns);
						if(q3ProdExpChg != null) {
							q3ProdExpChgList.add(q3ProdExpChg);						
						}
					}
				}
			}
			
			List<String> preAnsList = Arrays.asList(preAns.split(","));
			for(String preItem : preAnsList) {	//上次答案
				//檢核買過商品是否比上次減少
				Boolean found = Boolean.FALSE;
				String preItemPrdType = preItem.trim().substring(0, 1);
				String preItemPrdYear = preItem.trim().substring(1, 2);
				
				if(!StringUtils.equals(preItemPrdYear, "1")) {	//上次購買過的商品不是無經驗
					for(Map<String, Object> ans : quest3) {	//本次答案
						if(ans.get("select") != null && (boolean) ans.get("select")) {
							String prdType = ans.get("ANSWER_SEQ").toString().trim().substring(0, 1);
							String prdYear = ans.get("ANSWER_SEQ").toString().trim().substring(1, 2);
							if(StringUtils.equals(preItemPrdType, prdType) && StringUtils.equals(prdYear, "1")) {
								//本次的商品是無經驗
								found = Boolean.TRUE;
							}
						}
					}
					
					//上次買過的商品，本次沒有買
					if(found) {
						String prodTypeName = xmlinfo.getVariable("KYC.Q3_PROD_TYPE", preItemPrdType, "F3");
						//買過商品與前次填答相比，減少【{0}】，請再次向客戶確認。{0}=q3PrdDecrease
						q3ProdDecrease =  q3ProdDecrease + (StringUtils.isEmpty(q3ProdDecrease) ? "" : ", ") + prodTypeName;
					}
				}
			}
		}
		
		//回傳第3題須跳題資訊
		outputVO.setQ3ProdExpChgList(q3ProdExpChgList);
		outputVO.setQ3ProdDecrease(q3ProdDecrease);
	}
	
	/***
	 * 檢核投資經驗年期是否比上次上升2級(含)以上
	 * @param currAns: 本次測驗第三題其中一個答案，如：31
	 * @param preAns: 上次測驗第8 & 9題答案，如：11,21,32
	 * @return
	 * @throws JBranchException 
	 */
	private Map<String, Object> prodExpUp2Degree(String currAns, String preAns) throws JBranchException {
		XmlInfo xmlinfo = new XmlInfo();
		String prdType = currAns.substring(0, 1);
		int prdExp = Integer.parseInt(currAns.substring(1, 2));
		Boolean found = Boolean.FALSE;
		Map<String, Object> up2Degree = null;
		
		List<String> preAnsList = Arrays.asList(preAns.split(","));
		for(String preItem : preAnsList) {
			preItem = preItem.trim();
			String preItemPrdType = preItem.substring(0, 1);
			int preItemPrdExp = Integer.parseInt(preItem.substring(1, 2));
			
			//相同商品，是否投資經驗上升2級以上
			if(StringUtils.equals(prdType, preItemPrdType)) {
				found = Boolean.TRUE;
				if((prdExp - preItemPrdExp) >= 2) {
					up2Degree = new HashMap<String, Object>();
					up2Degree.put("ProdType", xmlinfo.getVariable("KYC.Q3_PROD_TYPE", prdType, "F3"));
					up2Degree.put("PreProdExp", xmlinfo.getVariable("KYC.Q3_PROD_EXP", Integer.toString(preItemPrdExp), "F3"));
					up2Degree.put("CurProdExp", xmlinfo.getVariable("KYC.Q3_PROD_EXP", Integer.toString(prdExp), "F3"));
				}
			}
		}
		
		//若舊資料找不到答案，表示無經驗，新答案選3~5年表示上升2級
		if(!found && prdExp >= 3) {
			up2Degree = new HashMap<String, Object>();
			up2Degree.put("ProdType", xmlinfo.getVariable("KYC.Q3_PROD_TYPE", prdType, "F3"));
			up2Degree.put("PreProdExp", xmlinfo.getVariable("KYC.Q3_PROD_EXP", "1", "F3"));
			up2Degree.put("CurProdExp", xmlinfo.getVariable("KYC.Q3_PROD_EXP", Integer.toString(prdExp), "F3"));
		}
		
		return up2Degree;
	}
	
	/***
	 * 取得舊問卷題型的第8及9題的答案，將其展開同新題型答案 (EX: 13,21,33,44,51,62)
	 * @param answer2
	 * @param questList
	 * @param outputVO
	 * @throws JBranchException 
	 */
	private String getOld89Ans(String answer2) throws JBranchException{
		String ans89 = "";
		String q8Ans = "";
		String q9Ans = "";
		XmlInfo xmlinfo = new XmlInfo();
		Map<String, String> prodType = xmlinfo.getVariable("KYC.Q3_PROD_TYPE", "F3");
		
		//答案不是空的且選項也不是空的
		if(StringUtils.isNotBlank(answer2)){
			String[] ansList = answer2.split(";");//切割答案
			
			//第８題
			if(ansList.length >= 8 && ansList[7].toString().replaceAll("\\,|\\;|\\s", "").matches("\\d+")){
				q8Ans = ansList[7].trim();
				
				if(StringUtils.equals("7", q8Ans)) {
					//第8題：是否買過下列商品，選擇"以上皆無"
					ans89 = "11,21,31,41,51,61";
				} else {
					//第9題：投資經驗年數異動
					if(ansList.length >= 9 && ansList[8].toString().replaceAll("\\,|\\;|\\s", "").matches("\\d+")){
						q9Ans = ansList[8].trim();
						//舊答案對應到新答案
						//舊答案：(1)無經驗(2)一年以下(3)一年含~未滿三年(4)三年含~未滿五年(5)五年含~未滿十年(6)十年含以上
						//新答案：(1)無經驗(2)3年以下經驗(3)3~5年經驗(4)5~10年經驗(5)10年以上經驗
						if(!(StringUtils.equals(q9Ans, "1") || StringUtils.equals(q9Ans, "2"))) {
							int q9d = Integer.parseInt(q9Ans) - 1;
							q9Ans = ObjectUtils.toString(q9d);
						}						
						
						for(Map.Entry<String, String> map : prodType.entrySet()) {
							//舊答案沒有選擇的商品，表示"無經驗"
							ans89 += (StringUtils.isNotBlank(ans89) ? "," : "") + map.getKey() + (q8Ans.indexOf(map.getKey()) >= 0 ? q9Ans : "1");
						}
					}
				}
			}
		}
		
		return ans89;
	}
	
	/**取之前結果**/
	@SuppressWarnings("unchecked")
	public void getLastResult(Object body, IPrimitiveMap<Object> header) throws JBranchException{
		KYC310InputVO inputVO 		  = (KYC310InputVO) body;
		KYC310OutputVO outputVO 	  = new KYC310OutputVO();
		SOT701InputVO sot701inputVO   = new SOT701InputVO();
		FP032675DataVO fp032675DataVO = new FP032675DataVO();
		FP032151OutputVO fp032151OutputVO = new FP032151OutputVO();
		try {
			List<Map<String, Object>> questList = inputVO.getQuest_list();
			List<Map<String, Object>> beforeData1 = new ArrayList<Map<String,Object>>();
			List<Map<String, Object>> beforeData2 = new ArrayList<Map<String,Object>>();
			String EDUCATION = "";
			String CAREER 	 = "";
			String MARRAGE 	 = "";
			String CHILD_NO  = "";
			String answer2 	 = null;
			Boolean isNewQues = true;	//前一次KYC測驗是否為新問卷題型(原8, 9題改為第3題)
			
			//理財商品投資經驗檢核
			//舊問卷題型為第8、9題，新問卷題型為第3題
			beforeData1 = exeQueryWithoutSortForQcf(genDefaultQueryConditionIF().setQueryString(
				" SELECT D.ANSWER_2, M.CRR_ORI FROM TBKYC_INVESTOREXAM_D D"
				+ " INNER JOIN TBKYC_INVESTOREXAM_M M ON M.SEQ = D.SEQ "
				+ " WHERE D.CUST_ID = :custid and D.ANSWER_2 is not null ")
				.setObject("custid", inputVO.getCUST_ID().toUpperCase()));
			
			if(CollectionUtils.isNotEmpty(beforeData1)){
				answer2 = ObjectUtils.toString(beforeData1.get(0).get("ANSWER_2"));
				//以CRR_ORI這個欄位檢查前一次KYC測驗是使用新問卷題目還是舊問卷題目；舊問卷沒有這個欄位資料
				//新問卷檢查第3題，舊問卷檢查第8、9題
				isNewQues = StringUtils.isEmpty(ObjectUtils.toString(beforeData1.get(0).get("CRR_ORI"))) ? false : true;
				
				try{
					//8、9題特殊處
					if(answer2.replaceAll("\\,|\\;|\\s", "").matches("\\d+")) {	//排除特殊舊資料答案
						if(!isNewQues && StringUtils.equals("03", inputVO.getQUESTION_TYPE())) {
							//法人上一次為舊問卷，不需檢查第3題
						} else {
							checkProdExperience(isNewQues, answer2, questList, outputVO);
						}
					}
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}

			//取電文FC032151學歷、職業、婚姻、子女數
			sot701inputVO.setCustID(inputVO.getCUST_ID());
			fp032151OutputVO = sot701.getFP032151Data(sot701inputVO, header);
			//因應法人沒有相關值 所以給邏輯判斷
			if(StringUtils.isNotBlank(fp032151OutputVO.getEDUCATION())) EDUCATION 	= fp032151OutputVO.getEDUCATION().trim();
			if(StringUtils.isNotBlank(fp032151OutputVO.getCAREER())) CAREER 		= fp032151OutputVO.getCAREER().trim();
			if(StringUtils.isNotBlank(fp032151OutputVO.getMARRAGE())) MARRAGE 	= fp032151OutputVO.getMARRAGE().trim();
			if(StringUtils.isNotBlank(fp032151OutputVO.getCHILD_NO())) CHILD_NO 	= fp032151OutputVO.getCHILD_NO().trim();

			//取電文FC032675重大傷病註記
			String SICK_TYPE = ""; 	//重大傷病
			//String plType = "";		//專業投資人
			sot701inputVO.setCustID(inputVO.getCUST_ID().toUpperCase());
			sot701inputVO.setIsOBU("FAKE"); // 用不到 OBU，所以傳入假的 OBU FLAG 避免撈取 085081_085105、NFEI003
			sot701inputVO.setNeedDesc(false); // 用不到 DESC，設為 false 可避免撈取 NFEI002 電文
			fp032675DataVO = sot701.getFP032675Data(sot701inputVO);//查詢客戶註記資料
			//plType = fc032675DataVO.getCustProFlag();
			SICK_TYPE = fp032675DataVO.getSickType();
			
			beforeData2 = exeQueryWithoutSortForQcf(genDefaultQueryConditionIF().setQueryString(
				" SELECT CUST_RISK_AFR ,CREATE_DATE FROM TBKYC_INVESTOREXAM_M  WHERE CUST_ID = :custid ")
				.setObject("custid", inputVO.getCUST_ID().toUpperCase()));
			
			Map<String, Map<String, Object>> map2 = new HashMap<String, Map<String,Object>>();
			if(!EDUCATION.equals(inputVO.getEDUCATION())){
				Map<String, Object> check = new HashMap<String, Object>();
				check.put("beforEDUCTION", EDUCATION);
				check.put("afterEDUCTION", inputVO.getEDUCATION());
				map2.put("EDUCTION", check);
			}
			if(!CAREER.equals(inputVO.getCAREER())){
				Map<String, Object> check = new HashMap<String, Object>();
				check.put("beforCAREER", CAREER);
				check.put("afterCAREER", inputVO.getCAREER());
				map2.put("CAREER", check);
			}
			if(!MARRAGE.equals(inputVO.getMARRAGE())){
				Map<String, Object> check = new HashMap<String, Object>();
				check.put("beforMARRIAGE", MARRAGE);
				check.put("afterMARRIAGE", inputVO.getMARRAGE());
				map2.put("MARRIAGE", check);
			}
			if(!CHILD_NO.equals(inputVO.getCHILD_NO())){
				Map<String, Object> check = new HashMap<String, Object>();
				check.put("beforCHILDREN", CHILD_NO);
				check.put("afterCHILDREN", inputVO.getCHILD_NO());
				map2.put("CHILDREN", check);
			}
			if(!SICK_TYPE.equals(inputVO.getSICK_TYPE())){
				Map<String, Object> check = new HashMap<String, Object>();
				check.put("beforHEALTH", SICK_TYPE);
				check.put("afterHEALTH", inputVO.getSICK_TYPE());
				map2.put("HEALTH", check);
			}				
			//年收入比較
			//第2題年收入勾選與端末系統留存年收入比較
			boolean incomeChk = false;
			String Q2AnsDesc = "";
			if(CollectionUtils.isNotEmpty(questList)) {
				List<Map<String, Object>> quest2 = (List<Map<String, Object>>) questList.get(1).get("ANSWER_LIST");
				for(Map<String, Object> ans : quest2) {	//第2題答案
					if(ans.get("select") != null && (boolean) ans.get("select")) {
						String q2Ans = ans.get("ANSWER_SEQ").toString().trim().substring(0, 1);
						Q2AnsDesc = ans.get("ANSWER_DESC").toString().trim();
						incomeChk = getIncomeCBSChk(q2Ans, inputVO.getIncomeFromCBS());
					}
				}
			}
			Map<String, Object> incomeMap = new HashMap<String, Object>();
			if(incomeChk) {
				incomeMap.put("INCOMECBS_CHK", "Y");//檢核通過
			} else {
				incomeMap.put("INCOMECBS_CHK", "N");//KYC收入與端末收入不符
				incomeMap.put("Q2ANS_DESC", Q2AnsDesc);
				incomeMap.put("INCOME_CBS", ObjectUtils.toString((inputVO.getIncomeFromCBS().divide(new BigDecimal(10000))).intValue()));
			}
			map2.put("INCOME_CBS", incomeMap);
			
			for(Map<String, Object> map3:beforeData2){
				Map<String, Object> check2 = new HashMap<String, Object>();
				check2.put("TEST_BEF_DATE", map3.get("CREATE_DATE"));
				check2.put("CUST_RISK_BEF", map3.get("CUST_RISK_AFR"));
				map2.put("INVESTOREXAM_M", check2);
			}
			outputVO.setComparison(map2);
			sendRtnObject(outputVO);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new APException(e.getMessage());
		}
		
	}
	
	public String nextSeqLpad() throws JBranchException{
		return seriaNextSequence.nextSeqLpad("SEQ_TBKYC_INVESTOREXAM", "'KYC' || TO_CHAR(SYSDATE , 'YYYYMMDD')", 5, "0");
	}
	
	
	@Transactional(propagation = Propagation.REQUIRED , rollbackFor = {APException.class , JBranchException.class , Exception.class})
	public void submit(Object body, IPrimitiveMap<Object> header) throws Exception{
		KYC310InputVO inputVO = (KYC310InputVO) body;	
		KYC310OutputVO outputVO = new KYC310OutputVO();
		
		try {
			DataAccessManager dam = this.getDataAccessManager();
			String seq = nextSeqLpad();//序號
			TBKYC_INVESTOREXAM_MVO mvo = inputVoToKycMasterVo(inputVO , seq);
			dam.create(mvo);//新增客戶於投資人KYC測驗記錄主檔
			dam.create(inputVoToKycDetailVo(inputVO , seq));//新增客戶於投資人KYC測驗記錄明細
			createTbkycDetailExp(inputVO);//將kyc相關資料搬到TBKYC_INVESTOREXAM_D_EXP
			dam.create(inputVoToKycReportvo(inputVO , seq));//產生KYC表單並存於blob
			
			//若需填寫差異表，寫入差異表資料
			if(StringUtils.equals("Y", inputVO.getNEED_COMPARISON_YN())) {
				dam.create(inputVoToKycCompVo(inputVO , seq));//新增差異表資料
				dam.update(genCompRpt(inputVO, seq));//產生差異表表單並存於blob
//				printCompRpt(inputVO, seq); //測試時印出
			}
			//設定查詢參數
			String loginID = (String) getCommonVariable(SystemVariableConsts.LOGINID);
			Map<String , Object> param = new Hashtable<String , Object>();
			param.put("seq", seq);
			param.put("loginid", loginID);

			//update CREATOR：若登入者為代理人時，CREATOR應為被代理人ID
			exeUpdateForMap(" UPDATE TBKYC_INVESTOREXAM_M SET CREATOR = :loginid, MODIFIER = :loginid WHERE SEQ = :seq " , param);
			//update CREATOR：若登入者為代理人時，CREATOR應為被代理人ID
			exeUpdateForMap(" UPDATE TBKYC_INVESTOREXAM_D SET CREATOR = :loginid, MODIFIER = :loginid WHERE SEQ = :seq " , param);
			//update CREATOR：若登入者為代理人時，CREATOR應為被代理人ID
			exeUpdateForMap(" UPDATE TBKYC_REPORT SET CREATOR = :loginid, MODIFIER = :loginid WHERE SEQ = :seq " , param);
			
			outputVO.setSeq(seq);
			outputVO.setEXPIRY_DATE(mvo.getEXPIRY_DATE());
			
			sendRtnObject(outputVO);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new APException(e.getMessage());
		}
	}
	
	private String getRocDate(Date date) throws Exception {
		String rocDate = null;
		if (date != null) {
			Calendar cal = Calendar.getInstance();  
			cal.setTime(date);
			String year = cal.get(Calendar.YEAR) - 1911 + "";
			System.out.println(year);
			String month = cal.get(Calendar.MONTH) + 1 + "";
			System.out.println(month);
			String day = cal.get(Calendar.DAY_OF_MONTH) + "";
			System.out.println(day);
			
			if (month.length() < 2) {
				month = "0" + month;
			}
			if (day.length() < 2) {
				day = "0" + day;
			}
			rocDate = year + "/" + month + "/" + day;
		}
		return rocDate;
	}
	
	public String queuryInvestBranchNbr(String empId) throws Exception{
		//判斷當下鑑機人員是否為企金OP或主管
		if(KYCCheck.checkIsLegalPerson(empId)){
			TBORG_MEMBERVO memberVo = (TBORG_MEMBERVO) getDataAccessManager().findByPKey(TBORG_MEMBERVO.TABLE_UID, empId);
			return memberVo.getDEPT_ID();
		}
		else 
			return (String) getCommonVariable(SystemVariableConsts.LOGINBRH);
	}
	
	//檢核新開戶客戶
	public void chkNewCust(Object body, IPrimitiveMap<Object> header) throws Exception{
		String loginID = (String) getCommonVariable(SystemVariableConsts.LOGINID);
		String investBranchNbr = queuryInvestBranchNbr(loginID);//客戶承作問卷分行
		
		KYC310InputVO inputVO = (KYC310InputVO) body;
		SOT701InputVO inputVO_iot701 = new SOT701InputVO();
		inputVO_iot701.setCustID(inputVO.getCUST_ID().toUpperCase());

		FP032151OutputVO fp032151VO = sot701.getFP032151Data(inputVO_iot701, null);
		
		//新增客戶主檔
		TBCRM_CUST_MASTVO tcm = new TBCRM_CUST_MASTVO();
		tcm.setCUST_ID(inputVO.getCUST_ID().toUpperCase());
		tcm.setCUST_NAME(fp032151VO.getCUST_NAME());
		//取電文生日   生日轉換成日期給前端
		if(!cbsservice.checkJuristicPerson(inputVO_iot701.getCustID())){
			Date BDAY = (StringUtils.isBlank(fp032151VO.getBDAY())) ? null : new SimpleDateFormat("ddMMyyyy").parse(fp032151VO.getBDAY());
			tcm.setBIRTH_DATE(new Timestamp(BDAY.getTime()));
		}
		tcm.setBRA_NBR(investBranchNbr);
		
		getDataAccessManager().create(tcm);
		
		//客戶駐記檔		
		TBCRM_CUST_NOTEVO tcn = new TBCRM_CUST_NOTEVO();
		tcn.setCUST_ID(inputVO.getCUST_ID().toUpperCase());
		getDataAccessManager().create(tcn);
		
		chkTimes(body, header);
	}
	
	/**inputVo 轉 客戶於投資人KYC測驗記錄主檔**/
	@SuppressWarnings("unchecked")
	private TBKYC_INVESTOREXAM_MVO inputVoToKycMasterVo(KYC310InputVO inputVO , String seq) throws Exception{
		Date now = new Date();
		String loginID = (String) getCommonVariable(SystemVariableConsts.LOGINID);
		String custId = inputVO == null || inputVO.getCUST_ID() == null ? "" : inputVO.getCUST_ID().toUpperCase();
		String aoCode = null;
		String custBranchNbr = new String();//客戶歸屬分行
		
		String investBranchNbr = queuryInvestBranchNbr(loginID);//客戶承作問卷分行
		List<Map<String, Object>> aoBranch = null;
		
		//設定查詢參數
		Map<String , Object> param = new Hashtable<String , Object>();
		param.put("custid", custId);

		//刪除此客戶於投資人KYC測驗記錄明細的資料
		exeUpdateForMap(" DELETE TBKYC_INVESTOREXAM_D WHERE CUST_ID = :custid " , param);
		//刪除此客戶於投資人KYC測驗記錄主檔的資料
		exeUpdateForMap(" DELETE TBKYC_INVESTOREXAM_M WHERE CUST_ID = :custid " , param);
		//查客戶主檔，查負責的理專代碼及 開戶分行代碼
		aoBranch = exeQueryWithoutSortForMap(" SELECT AO_CODE,BRA_NBR FROM TBCRM_CUST_MAST WHERE CUST_ID = :custid ", param);
		
		//不存在於客戶主檔、沒負責的理專、無開戶分行
		if(CollectionUtils.isEmpty(aoBranch) || aoBranch.get(0).get("AO_CODE") == null || aoBranch.get(0).get("BRA_NBR") ==null){
			aoCode = null;
			custBranchNbr = investBranchNbr;//客戶歸屬於分等同承做問卷分行
		}
		else{
			aoCode = aoBranch.get(0).get("AO_CODE").toString();
			
			if(aoBranch.get(0).get("BRA_NBR") != null){
				custBranchNbr = aoBranch.get(0).get("BRA_NBR").toString();//客戶所屬分行為開戶分行代碼
			}
		}
		
		//開始建立投資人KYC測驗記錄主檔
		TBKYC_INVESTOREXAM_MVO tim = new TBKYC_INVESTOREXAM_MVO();
		tim.setSEQ(seq);//客戶風險評估問卷主鍵
		tim.setEXAM_VERSION(inputVO.getEXAM_VERSION());//版本
		tim.setCIB_FLAG(custId.length() >= 10 ? "N" : "Y");//是否為企金KYC
		tim.setCUST_ID(custId);//客戶ID
		tim.setCUST_NAME(inputVO.getCust_name());//客戶姓名
		tim.setAO_CODE(aoCode);//理專代碼
		tim.setCUST_BRANCH_NBR(custBranchNbr);//客戶所屬分行
		tim.setINVEST_BRANCH_NBR(investBranchNbr);//客戶承作問卷分行
		
		//判斷是否有測試前一次修改時間
		if(inputVO.getTEST_BEF_DATE() != null){
			Date testBefDate = new SimpleDateFormat("yyyy-MM-dd").parse(inputVO.getTEST_BEF_DATE());
			Timestamp TTEST_BEF_DATE = new Timestamp(testBefDate.getTime());
			tim.setTEST_BEF_DATE(TTEST_BEF_DATE);
		}
		else{
			tim.setTEST_BEF_DATE(null);
		}
		
		tim.setCUST_RISK_BEF(inputVO.getCUST_RISK_BEF());//測試前一次客戶風險屬性
		tim.setCUST_RISK_AFR(inputVO.getCUST_RISK_AFR());//測試後客戶風險屬性
		tim.setRISKRANGE(new BigDecimal(inputVO.getRISKRANGE()));//分數
		tim.setEMP_ID(inputVO.getCOMMENTARY_STAFF());//解說人員
		tim.setSTATUS("01");//狀態為待審核
		tim.setCREATE_DATE(new Timestamp(now.getTime()));//KYC鍵機日期
//		tim.setEXPIRY_DATE(new Timestamp(DateUtils.addDays(now , 364).getTime()));//KYC截止日(加十二個月減一天)
		//FOR CBS測試日期修改
		SimpleDateFormat ft = new SimpleDateFormat("yyyyMMddHHmmss");
		Date testDate = ft.parse(cbsservice.getCBSTestDate());
		
		if (inputVO.getEXPIRY_DATE() != null) {
			tim.setEXPIRY_DATE(new Timestamp(inputVO.getEXPIRY_DATE().getTime()));
		} else {
			Calendar cal = Calendar.getInstance();
			cal.setTime(testDate);
//			cal.add(cal.DATE, 364); //KYC截止日(加十二個月減一天)
			//KYC截止日(加一年減一天)
			cal.add(cal.YEAR, 1);
			cal.add(cal.DATE, -1);
			Date storeDate = cal.getTime();
			Timestamp expiryDate = new Timestamp(storeDate.getTime());
			tim.setEXPIRY_DATE(expiryDate);//KYC截止日(加十二個月減一天)		
			inputVO.setEXPIRY_DATE(expiryDate);
		}
		
		tim.setCRR_ORI(inputVO.getCRR_ORI());//原始總分取得的風險屬性
		tim.setCRR_MATRIX(inputVO.getCRR_MATRIX());//降等機制矩陣取得的風險屬性
		tim.setSCORE_ORI_TOT(inputVO.getSCORE_ORI_TOT());//原始總分
		tim.setSCORE_C(inputVO.getSCORE_C());//風險偏好總分
		tim.setSCORE_W(inputVO.getSCORE_W());//風險承受能力總分
		tim.setSCORE_CW_TOT(inputVO.getSCORE_CW_TOT());//降等機制矩陣總分
		tim.setRISK_LOSS_RATE(inputVO.getRISK_LOSS_RATE());//可承受風險損失率(%)
		tim.setRISK_LOSS_LEVEL(inputVO.getRISK_LOSS_LEVEL());//可承受風險損失率C值
		tim.setNEED_COMPARISON_YN(inputVO.getNEED_COMPARISON_YN()); //是否需填寫差異表
		
		return tim;
	}
	
	/**inputVo 轉 客戶於投資人KYC測驗記錄明細**/
	private TBKYC_INVESTOREXAM_DVO inputVoToKycDetailVo(KYC310InputVO inputVO , String seq) throws DAOException, JBranchException{
		String anslist = inputVO.getANSWER_2().toString();
		String sureans = anslist.replace("[", "").replace(".0", "").replace("],", ";").replace("]", "").trim();
		String custId = inputVO == null || inputVO.getCUST_ID() == null ? "" : inputVO.getCUST_ID().toUpperCase();

		//新增是否選取類型
		String DAY_TYPE = new String();		
		String NIGHT_TYPE = new String();		
		String TEL_NO_TYPE = new String();		
		String FAX_TYPE = new String();
		
		//是否選取轉成數字
		if("true".equals(inputVO.getDAY_TYPE())){
			DAY_TYPE = "1";
		}else{
			DAY_TYPE = "2";
		}
		
		if("true".equals(inputVO.getNIGHT_TYPE())){
			NIGHT_TYPE = "1";
		}else{
			NIGHT_TYPE = "2";
		}
		
		if("true".equals(inputVO.getTEL_NO_TYPE())){
			TEL_NO_TYPE = "1";
		}else{
			TEL_NO_TYPE = "2";
		}
		
		if("true".equals(inputVO.getFAX_TYPE())){
			FAX_TYPE = "1";
		}else{
			FAX_TYPE = "2";
		}
		
		String CUST_TEL = DAY_TYPE + ":" + inputVO.getDAY_COD() + ":" + inputVO.getDAY() + ";" + 
				NIGHT_TYPE + ":" + inputVO.getNIGHT_COD() + ":" + inputVO.getNIGHT() + ";" + 
				TEL_NO_TYPE + ":" + inputVO.getTELNO_COD() + ":" + inputVO.getTEL_NO() + ";" + 
				FAX_TYPE + ":" + inputVO.getFAX_COD() + ":" +inputVO.getFAX();
		
		String degrade = inputVO.getDegrade();
		
		TBKYC_INVESTOREXAM_DVO kycDetail = new TBKYC_INVESTOREXAM_DVO();
		kycDetail.setSEQ(seq);
		kycDetail.setCUST_ID(custId);
		kycDetail.setCUST_EDUCTION_BEFORE(inputVO.getCUST_EDUCTION_BEFORE());
		kycDetail.setCUST_CAREER_BEFORE(inputVO.getCUST_CAREER_BEFORE());
		kycDetail.setCUST_MARRIAGE_BEFORE(inputVO.getCUST_MARRIAGE_BEFORE());
		kycDetail.setCUST_CHILDREN_BEFORE(inputVO.getCUST_CHILDREN_BEFORE());
		kycDetail.setCUST_HEALTH_BEFORE(inputVO.getCUST_HEALTH_BEFORE());
		kycDetail.setCUST_EMAIL_BEFORE(inputVO.getCUST_EMAIL_BEFORE());
		kycDetail.setCUST_EDUCTION_AFTER(inputVO.getEDUCATION());
		kycDetail.setCUST_CAREER_AFTER(inputVO.getCAREER());
		kycDetail.setCUST_MARRIAGE_AFTER(inputVO.getMARRAGE());
		kycDetail.setCUST_CHILDREN_AFTER(inputVO.getCHILD_NO());
		kycDetail.setCUST_HEALTH_AFTER(inputVO.getSICK_TYPE());
		
		if(inputVO.getCUST_ADDR_1() != null)
			kycDetail.setCUST_ADDRESS(inputVO.getCUST_ADDR_1());//地址
		
		if(inputVO.getEMAIL_ADDR() != null)
			kycDetail.setCUST_EMAIL(inputVO.getEMAIL_ADDR());//EMAIL
		
		kycDetail.setCUST_TEL(CUST_TEL);//連絡電話
		kycDetail.setUPDATE_YN(inputVO.getY_N_update());
		kycDetail.setANSWER_2(sureans);
		kycDetail.setCUST_DEGRADE(inputVO.getDegrade());//免降等註記
		//add by Brian
		kycDetail.setCUST_SCHOOL(inputVO.getSCHOOL());//就讀學校
		kycDetail.setCUST_EDU_CHANGE(inputVO.getEDU_CHANGE());//學歷選擇與轉變
		kycDetail.setCUST_HEALTH_CHANGE(inputVO.getHEALTH_CHANGE());//重大傷病由有轉無
		kycDetail.setSAMEEMAIL_REASON(inputVO.getSAMEEMAIL_REASON());//全行重覆信箱原因
		kycDetail.setSAMEEMAIL_CHOOSE(inputVO.getSAMEEMAIL_CHOOSE());//全行重覆信箱原因
		
		kycDetail.setDEGRADE_DUE_DATE(inputVO.getDEGRADE_DATE() == null ? null : new Timestamp(inputVO.getDEGRADE_DATE().getTime()));
		kycDetail.setPRO_DUE_DATE(inputVO.getCUST_PRO_DATE() == null ? null : new Timestamp(inputVO.getCUST_PRO_DATE().getTime()));
		kycDetail.setHNWC_DUE_DATE(inputVO.getHNWC_DUE_DATE() == null ? null : new Timestamp(inputVO.getHNWC_DUE_DATE().getTime())); //客戶高資產註記到期日
		kycDetail.setHNWC_INVALID_DATE(inputVO.getHNWC_INVALID_DATE() == null ? null : new Timestamp(inputVO.getHNWC_INVALID_DATE().getTime())); //客戶高資產註記註銷日
		
		return kycDetail;
	}
	
	/**將表單內容存入pdf*/
	@SuppressWarnings("unchecked")
	public TBKYC_REPORTVO inputVoToKycReportvo(KYC310InputVO input310VO , String seq) throws Exception{
		//與db溝通物件
		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF qc = null;
		StringBuilder sb = null;
		
		//pdf暫存檔檔名
		String fileName = "kyc_" + uuid + ".pdf";
		String fileNameEng = "kyc_eng_" + uuid + ".pdf";
//		String fileName = "kyc_" + "" + ".pdf";
//		fileName = fileName.replaceAll(":", "");
		//String fileName = "kyc_" + uuid + ".pdf";
		//產出pdf暫存位置
		List<String> megerurl = new ArrayList<String>();
		List<String> megerurlEng = new ArrayList<String>();
		//root path
		String serverPath = (String) SysInfo.getInfoValue(SystemVariableConsts.SERVER_PATH);
		//模擬kyc310 to kyc311的轉換參數
		Map<String, Object> basicInformation = input310VO.getBasic_information();
		//客戶ID
		String custId = ObjectUtils.toString(input310VO.getCUST_ID());
		//判斷是否為自然人
		boolean isIdentityCard = input310VO.getCUST_ID().length() >= 10;
		//生日
		String birthdayDate = null;
		//parameter參數物件
		XmlInfo xmlInfo = new XmlInfo();
		//風險級距版號
		String examVersion = basicInformation.get("EXAM_VERSION").toString();
		//風險等級列表
		List<Map<String , Object>> riskList = null;
		//風險級距名稱
		List<Map<String, Object>> riskTypelist = new ArrayList<Map<String,Object>>();
		//風險類型
		String riskType = null;
		//風險類型英文
		String riskType_ENG = null;
		//前次風險類型
		String riskTypeBef = null;
		//前次風險類型英文
		String riskTypeBef_ENG = null;
		//問卷相關資訊
		List<Map<String, Object>> kycInfs = new ArrayList<Map<String,Object>>();
		//處理題目與選擇的答案
		List<Map<String, Object>> questionList = new ArrayList<Map<String,Object>>();
		//教育程度
		String education = (String)basicInformation.get("EDUCATION");
		//教育程度選項
		String educationItem = education;
		//子女數
		String childNo = ObjectUtils.toString(basicInformation.get("CHILD_NO"));
		//子女數選項
		String childNoItem = childNo;;
		//負責人資訊
		List<Map<String,Object>> tbcrmCustMasts = null;
		//pdf產生物件
		FubonWmsBizLogic kyc310Pdf = null;
		FubonWmsBizLogic kyc310PdfEng = null;
		
		//生日
		if(ObjectUtils.toString(basicInformation.get("birthday")).matches("\\-?((\\d+\\.\\d+(e|E|f|F|d|D)?\\d*)|(\\d+(e|E|f|F|d|D|l|L)?\\d*))$")){
			long birthdayLong = new BigDecimal(ObjectUtils.toString(basicInformation.get("birthday"))).longValue();
			birthdayDate = String.valueOf(birthdayLong);
		}
				
		//處理題目與答案
		for(Map<String, Object> map : input310VO.getQuest_list()){
			List<Map<String, Object>> answerList = (List<Map<String, Object>>) map.get("ANSWER_LIST");
			List<String> selectAnswer = new ArrayList<String>();
			Map<String, Object> temp = new HashMap<String, Object>();
			
			for(Map<String, Object> map2 : answerList){
				String selectVal = ObjectUtils.toString(map2.get("select"));
				
				if("true".equals(selectVal)){
					selectAnswer.add(map2.get("ANSWER_SEQ").toString().substring(0 , map2.get("ANSWER_SEQ").toString().indexOf('.')));
				}
			}
			
			temp.put("QUESTION_DESC", map.get("QUESTION_DESC"));
			temp.put("QUESTION_DESC_ENG", map.get("QUESTION_DESC_ENG"));
			temp.put("Ans", selectAnswer);
			questionList.add(temp);
		}
		
		try {
			//取風險類型
			qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb = new StringBuilder();
			sb.append(" SELECT DISTINCT L.RL_NAME, L.RL_NAME_ENG  ");
			sb.append(" FROM TBKYC_QUESTIONNAIRE_RISK_LEVEL L LEFT JOIN TBSYS_QUESTIONNAIRE A ON A.RL_VERSION = L.RL_VERSION ");
			sb.append(" WHERE A.EXAM_VERSION = :exam_version and CUST_RL_ID = :risk ");
			qc.setObject("exam_version", examVersion);
			qc.setObject("risk", (String)basicInformation.get("CUST_RISK_AFR"));
			qc.setQueryString(sb.toString());
			
			if(CollectionUtils.isNotEmpty(riskTypelist = dam.exeQueryWithoutSort(qc))){
				riskType = riskTypelist.get(0).get("RL_NAME").toString();
				riskType_ENG = riskTypelist.get(0).get("RL_NAME_ENG").toString();
			}
			
			//取前次風險類型
			if(StringUtils.isNotBlank((String)basicInformation.get("CUST_RISK_BEF"))) {			
				qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sb = new StringBuilder();
				sb.append(" SELECT DISTINCT L.RL_NAME, L.RL_NAME_ENG  ");
				sb.append(" FROM TBKYC_QUESTIONNAIRE_RISK_LEVEL L LEFT JOIN TBSYS_QUESTIONNAIRE A ON A.RL_VERSION = L.RL_VERSION ");
				sb.append(" WHERE A.EXAM_VERSION = :exam_version and CUST_RL_ID = :risk ");
				qc.setObject("exam_version", examVersion);
				qc.setObject("risk", (String)basicInformation.get("CUST_RISK_BEF"));
				qc.setQueryString(sb.toString());
				
				if(CollectionUtils.isNotEmpty(riskTypelist = dam.exeQueryWithoutSort(qc))){
					riskTypeBef = riskTypelist.get(0).get("RL_NAME").toString();
					riskTypeBef_ENG = riskTypelist.get(0).get("RL_NAME_ENG").toString();
				}
			}
			
			//取風險列表
			qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb = new StringBuilder();
			sb.append(" SELECT DISTINCT CUST_RL_ID,L.CUST_RL_ID||'等級('||L.RL_NAME||')' as RISKList, ");
			sb.append(" L.CUST_RL_ID||'('||L.RL_NAME_ENG||')' as RISKList_ENG ");
			sb.append("");
			sb.append(" FROM TBKYC_QUESTIONNAIRE_RISK_LEVEL L LEFT JOIN TBSYS_QUESTIONNAIRE A ON A.RL_VERSION = L.RL_VERSION ");
			sb.append(" WHERE A.EXAM_VERSION = :exam_version1 order by RISKList");
			qc.setObject("exam_version1", examVersion);
			qc.setQueryString(sb.toString());
			riskList = dam.exeQueryWithoutSort(qc);
			
			//解說人員姓名、鍵機人員姓名等...
			qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb = new StringBuilder();
			sb.append(" select B.EMP_NAME as COMMENTATOR,C.EMP_NAME as KEYIN,D.EMP_NAME as SUPERVISOR, ");
			sb.append(" A.EMP_ID as COMMENTATOR_ID,A.CREATOR as KEYIN_ID,A.SIGNOFF_ID as SUPERVISOR_ID,A.EXPIRY_DATE,A.SEQ ,A.CREATETIME , A.CREATE_DATE ");
			sb.append(" from TBKYC_INVESTOREXAM_M a ");
			sb.append(" LEFT JOIN TBORG_MEMBER B ON B.EMP_ID = A.EMP_ID ");
			sb.append(" LEFT JOIN TBORG_MEMBER C ON C.EMP_ID = A.CREATOR ");
			sb.append(" LEFT JOIN TBORG_MEMBER D ON D.EMP_ID = A.SIGNOFF_ID ");
			sb.append(" where SEQ = :seq ");
			qc.setObject("seq" , seq);
			qc.setQueryString(sb.toString());
			
			if(CollectionUtils.isNotEmpty(kycInfs = dam.exeQueryWithoutSort(qc))){
				Map<String, Object> kycInf = kycInfs.get(0);
				basicInformation.put("COMMENTATOR" , kycInf.get("COMMENTATOR"));//解說人員姓名
				basicInformation.put("KEYIN" , kycInf.get("KEYIN"));//鍵機人員姓名
				basicInformation.put("SUPERVISOR" , kycInf.get("SUPERVISOR"));//主管簽核ID姓名
				basicInformation.put("COMMENTATOR_ID" , kycInf.get("COMMENTATOR_ID"));//解說人員ID
				basicInformation.put("KEYIN_ID" , kycInf.get("KEYIN_ID"));//鍵機人員ID
				basicInformation.put("SUPERVISOR_ID" , kycInf.get("SUPERVISOR_ID"));//主管簽核ID
				basicInformation.put("EXPIRY_DATE" , kycInf.get("EXPIRY_DATE"));//本次測驗有效期限
				basicInformation.put("SEQ" , kycInf.get("SEQ"));//測試序號
				//FOR CBS測試日期修改
//				basicInformation.put("CREATETIME" , kycInf.get("CREATETIME"));//評估日期
//				basicInformation.put("CREATE_DATE" , kycInf.get("CREATE_DATE"));//鑑機日期
				SimpleDateFormat ft = new SimpleDateFormat("yyyyMMddHHmmss");
				Date testDate = ft.parse(cbsservice.getCBSTestDate());
				basicInformation.put("CREATETIME" , new Timestamp(testDate.getTime()));//評估日期
				basicInformation.put("CREATE_DATE" ,new Timestamp(testDate.getTime()));//鑑機日期

			}
						
			//教育程度選項轉換
			if(education != null){
		    	List<Map<String,Object>> educationList = null;
		    	String educationChange = xmlInfo.getVariable("KYC.EDUCATION" , education , "F3");
		    	educationChange = educationChange.length() > 2 ? educationChange.substring(0 , 2) : educationChange;
		    	
				qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sb = new StringBuilder();
				sb.append(" select PARAM_CODE from TBSYSPARAMETER where PARAM_TYPE ='KYC.EDUCATION_UP' and PARAM_NAME like :education_name ");
		    	qc.setObject("education_name", "%" + educationChange + "%");
		    	qc.setQueryString(sb.toString());
		    	
		    	if(CollectionUtils.isNotEmpty(educationList = dam.exeQueryWithoutSort(qc))){
		    		educationItem = educationList.get(0).get("PARAM_CODE").toString();
		    	}
			}
			
			//子女數選項轉換
			if(StringUtils.isNotBlank(childNo)){
				List<Map<String,Object>> childList = null;
		    	String childNoChange = xmlInfo.getVariable("KYC.CHILD_NO", childNo, "F3");

				qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sb = new StringBuilder();
				sb.append(" select PARAM_CODE from TBSYSPARAMETER where PARAM_TYPE ='KYC.CHILD_NO_UP' and PARAM_NAME like :child_no_name ");
		    	qc.setObject("child_no_name" , "%" + childNoChange + "%");
		    	qc.setQueryString(sb.toString());
		    			
		    	if(CollectionUtils.isNotEmpty(childList = dam.exeQueryWithoutSort(qc))){
		    		childNoItem = childList.get(0).get("PARAM_CODE").toString();
		    	}
			}
			
			//取負責人
			tbcrmCustMasts = dam.exeQueryWithoutSort(
				dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL)
					.setQueryString(" SELECT RPRS_ID , RPRS_NAME FROM TBCRM_CUST_MAST WHERE CUST_ID = :cust_id ")
					.setObject("cust_id" , custId.toUpperCase()
				)
			);
		} 
		catch (Exception e) {
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new APException(e.getMessage());
		}
		
		KYC311InputVO kyc311InputVO = new KYC311InputVO();
		kyc311InputVO.setBasic_information(basicInformation);
		
		//序號
		kyc311InputVO.setSEQ(seq);
		//客戶姓名
		kyc311InputVO.setCust_name((String)basicInformation.get("cust_name"));
		//客戶id
//		kyc311InputVO.setCUST_ID(((String)basicInformation.get("CUST_ID")).toUpperCase());
		kyc311InputVO.setCUST_ID(((String)basicInformation.get("CUST_ID")));
		//風險屬性
		kyc311InputVO.setDown_risk_level((String)basicInformation.get("CUST_RISK_AFR"));
		//性別
		kyc311InputVO.setGENDER((String)basicInformation.get("GENDER"));
		//教育程度
		kyc311InputVO.setEDUCATION((String)basicInformation.get("EDUCATION"));
		//職業
		kyc311InputVO.setCAREER((String)basicInformation.get("CAREER"));
		//婚姻
		kyc311InputVO.setMARRAGE((String)basicInformation.get("MARRAGE"));
		//子女數
		kyc311InputVO.setCHILD_NO((String)basicInformation.get("CHILD_NO"));
		//動大傷病
		kyc311InputVO.setSICK_TYPE((String)basicInformation.get("SICK_TYPE"));
		//日間電話
		kyc311InputVO.setDAY((String)basicInformation.get("DAY"));
		//夜間電話
		kyc311InputVO.setNIGHT((String)basicInformation.get("NIGHT"));
		//手機
		kyc311InputVO.setTEL_NO((String)basicInformation.get("TEL_NO"));
		//傳真
		kyc311InputVO.setFAX((String)basicInformation.get("FAX"));
		//問卷題目
		kyc311InputVO.setQuestionList(input310VO.getQuest_list());
		//住址
		kyc311InputVO.setCUST_ADDR_1((String)basicInformation.get("CUST_ADDR_1"));
		//email
		kyc311InputVO.setEMAIL_ADDR((String)basicInformation.get("EMAIL_ADDR"));
		//風險等級
		kyc311InputVO.setCUST_RISK_AFR((String)basicInformation.get("CUST_RISK_AFR"));
		//前次風險等級
		kyc311InputVO.setCUST_RISK_BEF((String)basicInformation.get("CUST_RISK_BEF"));
		//生日
		kyc311InputVO.setBirthday(birthdayDate);
		//鑑機日期
		kyc311InputVO.setTest_date(new Date());
		//題目與選擇的答案
		kyc311InputVO.setQuestionList_change(questionList);
		//風險類型
		kyc311InputVO.setRISK_TYPE(riskType);
		//風險類型英文
		kyc311InputVO.setRISK_TYPE_ENG(riskType_ENG);
		//前次風險類型
		kyc311InputVO.setRISK_TYPE_BEF(riskTypeBef);
		//前次風險類型英文
		kyc311InputVO.setRISK_TYPE_BEF_ENG(riskTypeBef_ENG);
		//風險等級列表
		kyc311InputVO.setRISKList(riskList);
		//教育程度選項
		kyc311InputVO.setEDUCATION_Item(educationItem);
		//子女數選項
		kyc311InputVO.setCHILD_NO_Item(childNoItem);
		//主管帳號
		kyc311InputVO.setEMP_ID("");
		kyc311InputVO.setOUT_ACCESS("");
		//add by Brian
		//就讀學校
		kyc311InputVO.setSCHOOL((String)basicInformation.get("SCHOOL"));
		//教育程度選擇與改變
		kyc311InputVO.setEDU_CHANGE((String)basicInformation.get("EDU_CHANGE"));
		//重大傷病從有轉無
		kyc311InputVO.setHEALTH_CHANGE((String)basicInformation.get("HEALTH_CHANGE"));
		//設定負責人
		if(CollectionUtils.isNotEmpty(tbcrmCustMasts)){
			kyc311InputVO.setRPRS_ID((String)tbcrmCustMasts.get(0).get("RPRS_ID"));
			kyc311InputVO.setRPRS_NAME((String)tbcrmCustMasts.get(0).get("RPRS_NAME"));
		}
		
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
		//KYC到期日
		Date expiry_date_d = null;
		String expiry_date = "";
		if (basicInformation.get("EXPIRY_DATE") != null) {
			expiry_date_d = (Date) basicInformation.get("EXPIRY_DATE");
//			expiry_date = sdf.format(expiry_date_d);
			expiry_date = this.getRocDate(expiry_date_d);
		}
		//專投到期日
		Date cust_pro_date_d = null;
		String cust_pro_date = "";
		if (basicInformation.get("CUST_PRO_DATE") != null) {
			cust_pro_date_d = input310VO.getCUST_PRO_DATE();
//			cust_pro_date = sdf.format(cust_pro_date_d);
			cust_pro_date = this.getRocDate(cust_pro_date_d);
		}
		//免降等到期日
		Date degrade_date_d = null;
		String degrade_date = "";
		if (basicInformation.get("DEGRADE_DATE") != null) {
			degrade_date_d = input310VO.getDEGRADE_DATE();
//			degrade_date = sdf.format(degrade_date_d);
			degrade_date = this.getRocDate(degrade_date_d);
		}
		//客戶高資產註記到期日
		Date hnwcDueDate = null;
		String hnwcDueDateStr = "";
		if (basicInformation.get("HNWC_DUE_DATE") != null) {
			hnwcDueDate = input310VO.getHNWC_DUE_DATE();
			hnwcDueDateStr = this.getRocDate(hnwcDueDate);
		}
		//客戶高資產註記註銷日
		Date hnwcInvlidDate = null;
		String hnwcInvlidDateStr = "";
		if (basicInformation.get("HNWC_INVALID_DATE") != null) {
			hnwcInvlidDate = input310VO.getHNWC_INVALID_DATE();
			hnwcInvlidDateStr = this.getRocDate(hnwcInvlidDate);
		}
				
		kyc311InputVO.setEXPIRY_DATE(expiry_date);
		kyc311InputVO.setCUST_PRO_DATE(cust_pro_date);
		kyc311InputVO.setDEGRADE_DATE(degrade_date);
		kyc311InputVO.setHNWC_DUE_DATE(hnwcDueDateStr);
		kyc311InputVO.setHNWC_INVALID_DATE(hnwcInvlidDateStr);
		
		if (StringUtils.isNotBlank(input310VO.getPDF_KYC_FLAG())) {
			kyc311InputVO.setPDF_KYC_FLAG(input310VO.getPDF_KYC_FLAG());
		}
		
    	//前一次信箱
		kyc311InputVO.setCUST_EMAIL_BEFORE(input310VO.getCUST_EMAIL_BEFORE());
    	//信箱重覆原因
		kyc311InputVO.setSAMEEMAIL_CHOOSE(input310VO.getSAMEEMAIL_CHOOSE());  
    	//信箱重覆理由
		kyc311InputVO.setSAMEEMAIL_REASON(input310VO.getSAMEEMAIL_REASON());  
		
		//判斷自然人或法人pdf產生器
		//中文表單
		kyc310Pdf = isIdentityCard ? new KYC310_PersonRpt() : new KYC310_CorpRpt();
		kyc310Pdf.setDataAccessManager(dam);
		
		//gen pdf內容
		kyc310Pdf.getClass()
			.getMethod("genBlankPDF" , new Class[]{KYC311InputVO.class , String.class})
			.invoke(kyc310Pdf , new Object[]{kyc311InputVO , fileName});
		
		megerurl.add("/temp/reports/" + fileName);
    	String url = PdfUtil.process(new PdfConfigVO(this.getDataAccessManager(), megerurl));
    	
    	//用Blob寫入資料庫
    	File file = new File(serverPath, url);
    	byte[] resultByte =  Files.readAllBytes(file.toPath());
    	System.out.println(file.delete());
		    		
    	//英文表單
    	kyc310PdfEng = isIdentityCard ? new KYC310_PersonRpt_ENG() : new KYC310_CorpRpt_ENG();
    	kyc310PdfEng.setDataAccessManager(dam);
		
		//gen pdf內容
    	kyc310PdfEng.getClass()
			.getMethod("genBlankPDF" , new Class[]{KYC311InputVO.class , String.class})
			.invoke(kyc310PdfEng , new Object[]{kyc311InputVO , fileNameEng});
		
		megerurlEng.add("/temp/reports/" + fileNameEng);
		String urlEng = PdfUtil.process(new PdfConfigVO(this.getDataAccessManager(), megerurlEng));

    	//用Blob寫入資料庫
    	File fileEng = new File(serverPath, urlEng);
    	byte[] resultByteEng =  Files.readAllBytes(fileEng.toPath());
    	System.out.println(fileEng.delete());
    	
		TBKYC_REPORTVO repVO = new TBKYC_REPORTVO();
    	repVO.setSEQ(seq);
    	repVO.setREPORT_FILE(ObjectUtil.byteArrToBlob(resultByte));
    	repVO.setREPORT_FILE_ENG(ObjectUtil.byteArrToBlob(resultByteEng));
    	
    	return repVO;
	}
	
	public void createTbkycDetailExp(KYC310InputVO inputVO) throws DAOException, JBranchException{
		Map param = new HashMap();
		param.put("cust_id", inputVO.getCUST_ID().toUpperCase());

		exeUpdateForMap(new StringBuffer()
			.append(" insert into TBKYC_INVESTOREXAM_D_EXP ")
			.append(" select SEQ, EXAM_VERSION, QUEST_TYPE, QUESTION_VERSION, QST_NO, ")
			.append(" replace(trim(regexp_substr(ANSWER_2, '[^;]+', 1, QST_NO)),', ','|') ")
			.append(" from ( ")
			.append("   select M.SEQ, M.EXAM_VERSION, Q.QUEST_TYPE, Q.QUESTION_VERSION, Q.QST_NO, D.ANSWER_2 ")
			.append("   from TBKYC_INVESTOREXAM_D D ")
			.append("   join TBKYC_INVESTOREXAM_M M ")
			.append("   on M.SEQ = D.SEQ ")
			.append("   join TBSYS_QUESTIONNAIRE Q ")
			.append("   on Q.EXAM_VERSION = M.EXAM_VERSION ")
			.append("   where M.CUST_ID = :cust_id ")
			.append(" ) ").toString(), 
			param
		);	
	}	
	
	public void printBlank(Object body, IPrimitiveMap<Object> header) throws Exception{
		KYC310InputVO inputVO = (KYC310InputVO) body;
		
		if(inputVO.getCUST_ID().length() >= 10){
			inputVO.setQUESTION_TYPE("02");
		}
		else{
			inputVO.setQUESTION_TYPE("03");
		}
		
		inputVO.setFromElsePrintBlank(true);
		chkTimes(body, header);
		inputVO.setChk_type("print");
		chkAuth(body, header);
	}
	
	//抓問卷的圖片
	@SuppressWarnings("unchecked")
	public void getPicture(KYC310InputVO inputVO){
		try {
			QueryConditionIF qc = getDataAccessManager().getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuilder sb = new StringBuilder();
			
			for(Map<String, Object> map : inputVO.getQuest_list()){
				String question_version = (String) map.get("QUESTION_VERSION");
				
				if(map.get("PICTURE") != null){
					qc = getDataAccessManager().getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					sb = new StringBuilder();
					sb.append(" select doc_file from TBSYS_FILE_DETAIL ");
					sb.append(" where doc_id = (select picture from ");
					sb.append(" tbsys_qst_question where question_version = :question_version) ");
					qc.setObject("question_version", question_version);
					qc.setQueryString(sb.toString());
					List<Map<String, Object>> PictureFile = new ArrayList<Map<String,Object>>();
					PictureFile = getDataAccessManager().exeQueryWithoutSort(qc);
					map.put("PICTUREObject", PictureFile.get(0).get("DOC_FILE"));
				}
			}
		} 
		catch (JBranchException e) {
			e.printStackTrace();
		}
	}
	
	//衍生性金融商品檢核
	@SuppressWarnings("unchecked")
//	public boolean derivative(List<Map<String, Object>> QUESTLIST,String COM_Experience,KYC310OutputVO outputVO) throws APException{
//		if(CollectionUtils.isEmpty(QUESTLIST))
//			return false;
//	
//		Boolean found = Boolean.FALSE;	//是否有填答第3題投資年期>無經驗
//		
//		for(int i = 0 ; i < QUESTLIST.size() ; i++) {
//			String qstNo = ObjectUtils.toString((((Double)QUESTLIST.get(i).get("QST_NO")).intValue()));
//			List<Map<String, Object>> ansList = (List<Map<String, Object>>) QUESTLIST.get(i).get("ANSWER_LIST");
//			
//			if(StringUtils.equals("3", qstNo)) {	//檢查第3題答案
//				for(Map<String, Object> ans : ansList) {
//					if(ans.get("select") != null && StringUtils.equals("true", ans.get("select").toString())) {
//						String ansSeq = ans.get("ANSWER_SEQ").toString();	//答案選項
//						if(ansSeq.length() >= 2 && 
//								StringUtils.equals("6", ansSeq.substring(0, 1)) &&
//								!StringUtils.equals("1", ansSeq.substring(1, 2))) {
//							//第三題有選擇衍生性商品，且投資年期非無經驗
//							found = Boolean.TRUE;
//						}
//					}
//				}
//			}
//		}
//		
//		if(!found) {
//			if(StringUtils.equals("5", COM_Experience)) {
//				//如果第5碼是Y，如果沒有勾選，則顯示確認訊息
//				outputVO.setCheckCOM_Experience(true);
//			} else {
//				//如果第1~4 碼中有任何一碼是Y，如果沒有勾選，則顯示錯誤不能再繼續執行
//				throw new APException("ehl_01_KYC310_007");
//			}
//		} 
//		
//		return true;
//	}
	
	/***
	 * 取得風險級距矩陣C值 & 可承受風險損失率對應C值
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void getCRRMatrix(Object body, IPrimitiveMap<Object> header) throws JBranchException {
		KYC310InputVO inputVO = (KYC310InputVO) body;
		KYC310OutputVO outputVO = new KYC310OutputVO();
		
		//取得風險級距矩陣C值
		QueryConditionIF qc = getDataAccessManager().getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();		
		sb.append(" SELECT CUST_RL_ID FROM TBKYC_QUESTIONNAIRE_RISK_SCORE ");
		sb.append(" WHERE (C_SCORE_BTN <= :scoreC AND :scoreC < C_SCORE_TOP) ");
		sb.append("   AND (W_SCORE_BTN <= :scoreW AND :scoreW < W_SCORE_TOP) ");
		sb.append("   AND RS_VERSION = :rs_version ");
		qc.setObject("scoreC", inputVO.getSCORE_C());
		qc.setObject("scoreW", inputVO.getSCORE_W());
		qc.setObject("rs_version", inputVO.getRS_VERSION());
		qc.setQueryString(sb.toString());
		List<Map<String, Object>> clist = getDataAccessManager().exeQueryWithoutSort(qc);
		String crrMatrix = CollectionUtils.isNotEmpty(clist) ? (String) clist.get(0).get("CUST_RL_ID") : "";
		outputVO.setCRR_MATRIX(crrMatrix);
		
		//取得可承受風險損失率對應C值
		qc = getDataAccessManager().getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb = new StringBuilder();		
		sb.append(" SELECT CUST_RL_ID FROM TBKYC_QUESTIONNAIRE_RISK_LRATE ");
		sb.append(" WHERE :lossRate <= RL_UP_RATE AND RLR_VERSION = :rlr_version ORDER BY CUST_RL_ID ");
		qc.setObject("lossRate", inputVO.getRISK_LOSS_RATE());
		qc.setObject("rlr_version", inputVO.getRLR_VERSION());
		qc.setQueryString(sb.toString());
		List<Map<String, Object>> rlist = getDataAccessManager().exeQueryWithoutSort(qc);
		String rLevel = CollectionUtils.isNotEmpty(rlist) ? (String) rlist.get(0).get("CUST_RL_ID") : "";
		outputVO.setRISK_LOSS_LEVEL(rLevel);
		
		sendRtnObject(outputVO);
	}
	
	//
	private Boolean getPSRResult(String custID) throws DAOException, JBranchException{
		QueryConditionIF qc = getDataAccessManager().getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();	
		sb.append(" SELECT PSR17 FROM TBKYC_PSR_SUM_MKT ");
		sb.append(" WHERE PSR17 = :custID ");
		qc.setObject("custID", custID);
		qc.setQueryString(sb.toString());
		List<Map<String, Object>> rlist = getDataAccessManager().exeQueryWithoutSort(qc);
		if(rlist.size() > 0){
			return true;
		}else{
			return false;
		}
		
	}
	
	/***
	 * 查詢全行資料是否有重覆email
	 * @param body
	 * @param header
	 * @throws Exception 
	 */
	public void checkAllBranchEmail(Object body, IPrimitiveMap<Object> header) throws Exception {
		KYC310InputVO inputVO = (KYC310InputVO) body;
		KYC310OutputVO outputVO = new KYC310OutputVO();
		List<Map<String, Object>> sameEmailData = new ArrayList<Map<String, Object>>();
		if(StringUtils.isBlank(inputVO.getEMAIL_ADDR())){
			outputVO.setHaveSameEmail(false);
			sendRtnObject(outputVO);
		}
		List<CBSUtilOutputVO> list = _060503_060503dao.search(inputVO.getEMAIL_ADDR());
		CBS060503OutputVO cbs060503OutputVO = list.get(0).getCbs060503OutputVO();

		if(cbs060503OutputVO.getIsuse()){
			outputVO.setHaveSameEmail(true);
			for(CBSUtilOutputVO cbsUtilOutputVO : list ){
				CBS060503OutputVO cbsoutputVO = cbsUtilOutputVO.getCbs060503OutputVO();
				if(cbsoutputVO.getEmail_addr().indexOf("END OF TXN") < 0){
					Map map = new HashMap();
					map.put("name", cbsoutputVO.getEmail_addr().trim());
					
					for(CBS060503OutputDetailsVO detail : cbsoutputVO.getDetails()){
						if(StringUtils.isNotBlank(detail.getDefaultString1())){
							String origin = detail.getDefaultString1();
							String[] filter1 = origin.split("/");
							String[] filter2 = filter1[1].split(" ");
							map.put("id", filter2[0].trim());
							sameEmailData.add(map);
							break;
						}
					}
				}
		
			}		
			outputVO.setSameEmailData(sameEmailData);
		} else {
			outputVO.setHaveSameEmail(false);
		}
		
		
		sendRtnObject(outputVO);
	}
	
	/***
	 * 取得學歷、職業、婚姻、子女數、重大傷病中英文參數資料
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void getParamData(Object body, IPrimitiveMap<Object> header) throws JBranchException {
		KYC310OutputVO outputVO = new KYC310OutputVO();		
		
		//學歷
		outputVO.setEduList(getParamList("KYC.EDUCATION", "KYC.EDUCATION_ENG"));	
		//職業
		outputVO.setCrrList(getParamList("KYC.CAREER", "KYC.CAREER_ENG"));
		//婚姻
		outputVO.setMarList(getParamList("KYC.MARRAGE", "KYC.MARRAGE_ENG"));
		//子女數
		outputVO.setChlList(getParamList("KYC.CHILD_NO", "KYC.CHILD_NO_ENG"));
		//重大傷病
		outputVO.setHeaList(getParamList("KYC.HEALTH_FLAG", "KYC.HEALTH_FLAG_ENG"));
		//第三題商品類別
		outputVO.setQ3ProdTypeList(getParamList("KYC.Q3_PROD_TYPE", "KYC.Q3_PROD_TYPE_ENG"));
		//第三題商品經驗年期
		outputVO.setQ3ProdExpList(getParamList("KYC.Q3_PROD_EXP", "KYC.Q3_PROD_EXP_ENG"));
		//學歷變更原因
		outputVO.setEduChangeList(getParamList("KYC.EDU_CHANGE", "KYC.EDU_CHANGE_ENG"));
		//重大傷病變更原因
		outputVO.setHealthChangeList(getParamList("KYC.HEALTH_CHANGE", "KYC.HEALTH_CHANGE_ENG"));
		//EMAIL變更原因
		outputVO.setEmailChangeList(getParamList("KYC.EMAIL_CHANGE", "KYC.EMAIL_CHANGE_ENG"));
		
		sendRtnObject(outputVO);
	}
	
	@SuppressWarnings("unchecked")
	private List<Map<String, Object>> getParamList(String paramCH, String paramENG) throws DAOException, JBranchException {
		QueryConditionIF qc = getDataAccessManager().getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();	

		sb.append("SELECT CH.PARAM_CODE AS DATA, (CH.PARAM_NAME || '('|| NVL(EN.PARAM_NAME, '') || ')') AS LABEL, ");
		sb.append(" CH.PARAM_NAME AS LABEL_CH, NVL(EN.PARAM_NAME, '') AS LABEL_ENG ");
		sb.append(" FROM TBSYSPARAMETER CH ");
		sb.append(" LEFT JOIN TBSYSPARAMETER EN ON EN.PARAM_TYPE = :paramENG AND EN.PARAM_CODE = CH.PARAM_CODE ");
		sb.append(" WHERE CH.PARAM_TYPE = :paramCH ");
		sb.append(" ORDER BY CH.PARAM_ORDER ");
		qc.setObject("paramENG", paramENG);
		qc.setObject("paramCH", paramCH);
		qc.setQueryString(sb.toString());

		return getDataAccessManager().exeQuery(qc);
	}
	
	/**
	 * 由電文取得法定代理人KYC資料
	 *
	 * @param body
	 * @param header
	 * @throws Exception
	 */
	public Map<String, String> getData067050ByType(String custId) throws Exception {
		Map<String, String> rtnMap = new HashMap<String, String>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		Date today_s = sdf.parse(sdf.format(new Date()));
		
		rtnMap.put("REP_ID_1", "");
		rtnMap.put("REP_ID_2", "");
		rtnMap.put("REP_RISK_1", "");
		rtnMap.put("REP_RISK_2", "");
		
		String idType = cbsservice.getCBSIDCode(custId);
		//取得法定代理人資料
		List<CBSUtilOutputVO> data = _067050_067101dao.search(custId, idType);
		CBS067101OutputVO outputVO67101 = data.get(0).getCbs067101OutputVO();
		
		if (isNotBlank(outputVO67101.getDefaultString12())) { //法定代理人ID1
			rtnMap.put("REP_ID_1", outputVO67101.getDefaultString12().trim());
			try {
				CustKYCDataVO kycRep1 = sot701.getCustKycData(outputVO67101.getDefaultString12().trim());
				if(kycRep1.getKycDueDate() != null && kycRep1.getKycDueDate().after(today_s)) {
					//尚未過期
					rtnMap.put("REP_RISK_1", kycRep1.getKycLevel()); //法定代理人ID1_C值
				}
			} catch(Exception e) {
				//找不到法定代理人ID1的KYC
				rtnMap.put("REP_RISK_1", "");
			}
		}
		if (isNotBlank(outputVO67101.getIdNo3())) { //法定代理人ID2
			rtnMap.put("REP_ID_2", outputVO67101.getIdNo3().trim());
			try {
				CustKYCDataVO kycRep2 = sot701.getCustKycData(outputVO67101.getIdNo3().trim());
				if(kycRep2.getKycDueDate() != null && kycRep2.getKycDueDate().after(today_s)) {
					//尚未過期
					rtnMap.put("REP_RISK_2", kycRep2.getKycLevel()); //法定代理人ID2_C值
				}
			} catch(Exception e) {
				//找不到法定代理人ID2的KYC
				rtnMap.put("REP_RISK_2", "");
			}
		}
		
		//年收入
		rtnMap.put("INCOME_FROM_CBS", outputVO67101.getSigned01());
		
		return rtnMap;
	}
	
	/***
	 * 是否需填寫差異說明表：客戶是否於6個月內提高KYC邏輯檢核
	 * 如果KYC的C值做起來會進入冷靜期，是以還未進入冷靜期的C值與前一次C值做比較
	 * KYC鍵機：自然人 & 法人
	 * 若需填寫差異說明表，回傳上次KYC填答內容
	 */
	public void getLastKYCComparisonData(Object body, IPrimitiveMap<Object> header) throws JBranchException {
		KYC310InputVO inputVO = (KYC310InputVO) body;
		KYC310OutputVO outputVO = new KYC310OutputVO();	
		
		outputVO.setLastKYCComparisonData(getLastKYCComparisonData(inputVO));
		sendRtnObject(outputVO);
	}
	
	/***
	 * 是否需填寫差異說明表：客戶是否於6個月內提高KYC邏輯檢核
	 * 如果KYC的C值做起來會進入冷靜期，是以還未進入冷靜期的C值與前一次C值做比較
	 * KYC鍵機：自然人 & 法人
	 * 若需填寫差異說明表，回傳上次KYC填答內容
	 * 
	 * 情境說明
	 * 1. 首次做KYC ==> 不須產出表單
	 * 2. C值結果跟前一次「相同」 ==> 不須產出表單
	 * 3. C值結果比前一次「低」 ==> 不須產出表單
	 * 4. 前次已超過6個月(不含)，C值結果比前一次「高」一個等級(含)以上 ==> 進入冷靜期，不須產出表單
	 * 5. 距前次未超過6個月(不含)，C值結果比前一次「高」一個等級(含)以上 ==> 進入冷靜期，須產出表單
	 * 6. 距前次未超過6個月(不含)，C值結果比前一次「高」一個等級(含)以上，隔天又重做KYC，C值結果跟前一日「相同」 ==> 進入冷靜期，須產出表單
	 *		[舉例]
	 *		前一次KYC：2024/02/28 C2
	 *		重新做KYC：2024/03/17 C3 ->進入冷靜期、須產出表單
	 *		重新做KYC：2024/03/18 C3 -> 03/17 C3進入冷靜期，C3尚未生效，C值與02/28 C2比較 ==> 須產出表單
	 *		差異是與前一次KYC：2024/02/28 C2做比較
	 * 7. 距前次未超過6個月(不含)，C值結果比前一次「高」一個等級(含)以上，隔天又重做KYC，C值結果跟前一日「高」一個等級(含)以上 ==> 進入冷靜期，須產出表單
	 * 		[舉例]
	 * 		前一次KYC：2024/02/28 C2
	 * 		重新做KYC：2024/03/17 C3 ->進入冷靜期、須產出表單
	 * 		重新做KYC：2024/03/18 C4 -> 03/17 C3尚未生效，C值與02/28 C2比較 ==> 須產出表單，冷靜期自03/18重新起算
	 * 		差異是與前一次KYC：2024/02/28 C2做比較
	 * 8. 距前次未超過6個月(不含)，C值結果比前一次「高」一個等級(含)以上，隔天又重做KYC，C值結果比前一日「低」 ==> 不須產出表單，無冷靜期
	 * 		[舉例]
	 * 		前一次KYC：2024/02/28 C2
	 * 		重新做KYC：2024/03/17 C3 ->須產出表單
	 * 		重新做KYC：2024/03/18 C2 -> 03/17 C3尚未生效，C值與02/28 C2比較 ==> 不須產出表單、C2立即生效，無冷靜期
	 * 
	 * @param inputVO
	 * @return Map<String, String> 
	 * 			NEED_COMPARISON_YN = "Y" => 須顯示差異表
	 * 			LAST_ANSWER ==> 上次KYC填答內容
	 * @throws DAOException
	 * @throws JBranchException
	 */
	public Map<String, Object> getLastKYCComparisonData(KYC310InputVO inputVO) throws DAOException, JBranchException {
		DataAccessManager dam = getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		Map<String, Object> rtnMap = new HashMap<String, Object>();
				
		String lastCustRisk = "";
		String lastSeq = "";
		Date lastTestDateAdd6M = null;
		Date today = new Date();
		//取上一次的風險屬性
		List<Map<String, Object>> lastMasHis03List = kycOperationDao.queryLastMastHisForStatus03(inputVO.getCUST_ID());
		if (CollectionUtils.isNotEmpty(lastMasHis03List)) {
			lastCustRisk = (String) lastMasHis03List.get(0).get("CUST_RISK_AFR"); //上次測驗的風險屬性
			lastTestDateAdd6M = (Date) lastMasHis03List.get(0).get("TESTDATE_ADD6MON"); //上次測驗的日期+6個月
			lastSeq = (String) lastMasHis03List.get(0).get("SEQ"); //上次測驗的客戶風險評估問卷主鍵
		}		
		//若前次C值為C5，則同現行最高C4
		if(StringUtils.equals(lastCustRisk, "C5")) lastCustRisk = "C4";
		
		//1.若沒有之前KYC評估資料，為當天第一次做KYC，不須顯示差異表
		//2.前一次承作KYC日期超過6個月，不須顯示差異表
		//3.這次C值結果沒有比前一次「高」一個等級(含)以上，不須顯示差異表
		//  若仍在冷靜期中，新C值尚未生效，HIS裡面的是進入冷靜期前的C值
		if(StringUtils.isBlank(lastCustRisk) 
				|| (lastTestDateAdd6M != null && today.after(lastTestDateAdd6M))
				|| (Integer.parseInt(inputVO.getCUST_RISK_AFR().substring(1)) <= Integer.parseInt(lastCustRisk.substring(1)))) {
			rtnMap.put("NEED_COMPARISON_YN", "N"); //不須顯示差異表
			rtnMap.put("COMP_QUES", null); //差異表問卷內容
			rtnMap.put("LAST_SEQ", "");//放入前次問卷序號
			rtnMap.put("LAST_ANSWER_2", "");//放入前次填答答案
		} else {
			//需顯示差異表
			rtnMap.put("NEED_COMPARISON_YN", "Y"); //顯示差異表
			
			//檢查現在是否仍在冷靜期中
			qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			qc.setQueryString("SELECT SEQ, CUST_RISK_BEF, CUST_RISK_AFR FROM TBKYC_COOLING_PERIOD WHERE CUST_ID = :custId AND STATUS = 'C' ");
			qc.setObject("custId", inputVO.getCUST_ID());
			List<Map<String, Object>> mlist = dam.exeQuery(qc);

			if (CollectionUtils.isNotEmpty(mlist)) { 
				//現仍在冷靜期中，取進入冷靜期前的KYC填答內容
				StringBuilder sb = new StringBuilder();
				qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sb.append("SELECT A.SEQ, A.ANSWER_2 FROM TBKYC_INVESTOREXAM_D_HIS A ");
				sb.append(" INNER JOIN (SELECT SEQ FROM TBKYC_INVESTOREXAM_M_HIS WHERE CUST_ID = :custId and STATUS = '03' AND SEQ <> :seq ");
				sb.append(" 			 ORDER BY CREATE_DATE DESC FETCH FIRST 1 ROWS ONLY) B ON B.SEQ = A.SEQ ");
				qc.setQueryString(sb.toString());
				qc.setObject("custId", inputVO.getCUST_ID());
				qc.setObject("seq", lastSeq);
			} else { 
				//目前不在冷靜期中，取得前一次KYC評估結果填答內容
				qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				qc.setQueryString("SELECT SEQ, ANSWER_2 FROM TBKYC_INVESTOREXAM_D_HIS WHERE SEQ = :seq ");
//				lastSeq = "KYC2024081340821"; //測試自然人
//				lastSeq = "KYC2024080840718"; //測試法人
				qc.setObject("seq", lastSeq);
			}
			//上次KYC填答內容
			List<Map<String, Object>> alist = dam.exeQuery(qc);
			//冷靜期中，抓冷靜期前的問卷編號以及填答；不在冷靜期中，就是上一次的問卷
			lastSeq = ObjectUtils.toString(alist.get(0).get("SEQ"));
			//前次填答答案
			String lastAnswer2 = ObjectUtils.toString(alist.get(0).get("ANSWER_2"));
			
			//放入差異表問卷內容
			//網行銀不用放差異表內容
			if(!StringUtils.equals("Y", inputVO.getFromWebYN())) { 
				rtnMap.put("COMP_QUES", getComparisonQues(inputVO, lastAnswer2));
			}
			//放入前次問卷序號
			rtnMap.put("LAST_SEQ", lastSeq);
			//放入前次填答答案
			rtnMap.put("LAST_ANSWER_2", lastAnswer2);
		}

		return rtnMap;
	}
	
	/***
	 * 取得差異表問卷內容
	 * @param inputVO
	 * @return
	 */
	private List<Map<String, Object>> getComparisonQues(KYC310InputVO inputVO, String lastAnswer2) {
		List<Map<String, Object>> compList = new ArrayList<Map<String, Object>>();
		//前次填答答案(依照題目來分割)
		String[] lastAnsList = lastAnswer2.split(";");
		
		if(!inputVO.getQuest_list().isEmpty()){
			for(Map<String,Object> questMap : inputVO.getQuest_list()){
				//題號
				Double questNo = Double.parseDouble(questMap.get("QST_NO").toString());
				//差異表答案選項
				List<Map<String, Object>> compAns = (List<Map<String, Object>>)questMap.get("ANSWER_LIST_COMP"); 
				//有差異表答案選項
				if(!compAns.isEmpty()) { 
					Double thisAnsSeq = new Double(0); //本次填答題號先預設0
					Double lastAnsSeq = new Double(0); //前次填答題號先預設0
					List<Map<String, Object>> q3ProdTypeAns = new ArrayList<>(inputVO.getQ3_PROD_TYPEList()); //第三題答案依商品類別比較差異
					
					//答案選項
					for (Map<String, Object> ansMap : (List<Map<String, Object>>)questMap.get("ANSWER_LIST")) { 
						//本次填答題號
						Double ansSeq = Double.parseDouble(ansMap.get("ANSWER_SEQ").toString());
						if(ansMap.get("select") != null && (boolean)ansMap.get("select")) thisAnsSeq = ansSeq;
						
						for(int lastAnsIdx = 0; lastAnsIdx < lastAnsList.length; lastAnsIdx++) { //前次填答答案
							if(questNo.equals(Double.parseDouble(ObjectUtils.toString(lastAnsIdx + 1)))) {
								//取得該題目前次填答答案
								String[] ansList = ObjectUtils.toString(lastAnsList[lastAnsIdx]).split(","); 
								//前次填答答案
								for(String ans : ansList) {
									if (ansSeq.equals(Double.parseDouble(ans.trim()))) {
										//前次填答答案有選的設定為true
										ansMap.put("lastAnsSelect", true); 
										lastAnsSeq = Double.parseDouble(ans.trim());
										//本次填答
										if(thisAnsSeq.equals(lastAnsSeq)) { 
											//前次填答答案與本次填答填答答案相同
											ansMap.put("sameAnsSelect", true); 
										}
									}
								}
							}
						}
						//將第三題本次與前次答案放入LIST中
						if(questNo.equals(new Double(3))) {
							for(Map<String, Object> q3Map : q3ProdTypeAns) {
								if(StringUtils.equals((String)q3Map.get("DATA"), ansMap.get("ANSWER_SEQ").toString().substring(0, 1))) {
									if(thisAnsSeq.compareTo(new Double(0)) > 0) q3Map.put("thisAnsSeq", thisAnsSeq);
									if(lastAnsSeq.compareTo(new Double(0)) > 0) q3Map.put("lastAnsSeq", lastAnsSeq);
								}
							}
						}
					}
					
					if(questNo.equals(new Double(3))) {
						//第三題
						//檢核是否本次填答比上次上升兩級以上
						for(Map<String, Object> q3Map : q3ProdTypeAns) {
							if(((Double)q3Map.get("thisAnsSeq")).compareTo((Double)q3Map.get("lastAnsSeq") + 1) > 0) {
								q3Map.put("Q3Over2Degree", true); //本次填答比上次上升兩級以上
							}
							//前次填答答案與本次填答填答是否相同
							if(((Double)q3Map.get("thisAnsSeq")).compareTo((Double)q3Map.get("lastAnsSeq")) == 0) { 
								q3Map.put("sameAnsSelect", true); //前次填答答案與本次填答填答答案相同
							}
							//將差異表答案選項依商品類別放入
							q3Map.put("ANSWER_LIST_COMP", compAns);
						}
						//將第三題比較後資料放入差異表題目答案列表中
						questMap.put("Q3_PROD_TYPEList_COMP", q3ProdTypeAns);
					} else {
						//非第三題
						//前次填答答案與本次填答填答是否相同
						if(thisAnsSeq.compareTo(lastAnsSeq) == 0) questMap.put("sameAnsSelect", true); //前次填答答案與本次填答填答答案相同
					}
					
					//加入差異表題目答案列表
					compList.add(questMap);
				}
			}
		}
		
		return compList;
	}
	
	/***
	 * 寫入KYC差異表資料檔
	 * @param inputVO
	 * @param seq
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private TBKYC_INVESTOREXAM_COMPVO inputVoToKycCompVo(KYC310InputVO inputVO , String seq) throws Exception {
		//本次填答轉為字串
		String ans2list = inputVO.getANSWER_2().toString();
		String ans2 = ans2list.replace("[", "").replace(".0", "").replace("],", ";").replace("]", "").trim();
		//差異表填答轉為字串
		String ansComplist = inputVO.getANSWER_COMP().toString();
		String ansComp = ansComplist.replace("[", "").replace(".0", "").replace("],", ";").replace("]", "").trim();
		
		TBKYC_INVESTOREXAM_COMPVO cvo = new TBKYC_INVESTOREXAM_COMPVO();
		cvo.setSEQ(seq); //本次問卷主鍵
		cvo.setEXAM_VERSION(inputVO.getEXAM_VERSION()); //問卷版本
		cvo.setLAST_SEQ(inputVO.getLAST_SEQ()); //前次問卷主鍵
		cvo.setANSWER_2(ans2); //本次KYC填答
		cvo.setLAST_ANSWER_2(inputVO.getLAST_ANSWER_2()); //前次KYC填答
		cvo.setANSWER_COMP(ansComp); //差異表填答
		
		return cvo;
	}
	
	/***
	 * 列印空白差異表單
	 * @param body
	 * @param header
	 * @throws Exception 
	 */
	public void printBlankCompRpt(Object body, IPrimitiveMap<Object> header) throws Exception{
		KYC310InputVO inputVO = (KYC310InputVO) body;
		KYC310OutputVO outputVO = new KYC310OutputVO();
		List<String> mergeUrl = new ArrayList();
					
		if ("03".equals(inputVO.getQUESTION_TYPE())){	//法人空白差異表單
			KYC310_CorpRptEmptyComp report = PlatformContext.getBean("kyc310CorpRptEmptyComp", KYC310_CorpRptEmptyComp.class);
			report.setInputVO(inputVO);
			String filePath = report.genBlankPDF();
			mergeUrl.add(filePath);
		} else {	//自然人空白差異表單
			KYC310_PersonRptEmptyComp report = PlatformContext.getBean("kyc310PersonRptEmptyComp", KYC310_PersonRptEmptyComp.class);
			report.setInputVO(inputVO);
			String filePath = report.genBlankPDF();
			mergeUrl.add(filePath);
		}
			
		String url = PdfUtil.process(new PdfConfigVO(this.getDataAccessManager(), true, mergeUrl));
		notifyClientViewDoc(url, "pdf"); 
	}
	
	/***
	 * 產生KYC差異表單，存入DB
	 * @param body
	 * @param header
	 * @throws Exception 
	 */
	private TBKYC_REPORTVO genCompRpt(KYC310InputVO inputVO, String seq) throws Exception{
		inputVO.setKYC_SEQ(seq); //本次KYC填答主鍵
		List<String> mergeUrl = new ArrayList();
		
		if ("03".equals(inputVO.getQUESTION_TYPE())){	//法人差異表單
			KYC310_CorpRptComp report = PlatformContext.getBean("kyc310CorpRptComp", KYC310_CorpRptComp.class);
			report.setInputVO(inputVO);
			String filePath = report.getPDFPath();
			mergeUrl.add(filePath);
		} else {	//自然人差異表單
			KYC310_PersonRptComp report = PlatformContext.getBean("kyc310PersonRptComp", KYC310_PersonRptComp.class);
			report.setInputVO(inputVO);
			String filePath = report.getPDFPath();
			mergeUrl.add(filePath);
		}
		String url = PdfUtil.process(new PdfConfigVO(this.getDataAccessManager(), true, mergeUrl));
		//root path
		String serverPath = (String) SysInfo.getInfoValue(SystemVariableConsts.SERVER_PATH);
				
    	//用Blob寫入資料庫
    	File fileEng = new File(serverPath, url);
    	byte[] resultByte =  Files.readAllBytes(fileEng.toPath());
    	System.out.println(fileEng.delete());
    	
    	TBKYC_REPORTVO repVO = (TBKYC_REPORTVO) getDataAccessManager().findByPKey(TBKYC_REPORTVO.TABLE_UID, seq);
    	if(repVO != null) {
    		repVO.setREPORT_FILE_COMP(ObjectUtil.byteArrToBlob(resultByte));
    	}
    	
    	return repVO;
		
	}
	
	/***
	 * KYC差異表問卷表單 (測試用)
	 * @param inputVO
	 * @param seq
	 * @throws Exception
	 */
	private void printCompRpt(KYC310InputVO inputVO, String seq) throws Exception{
		inputVO.setKYC_SEQ(seq); //本次KYC填答主鍵
		List<String> mergeUrl = new ArrayList();
		
		if ("03".equals(inputVO.getQUESTION_TYPE())){	//法人差異表單
			KYC310_CorpRptComp report = PlatformContext.getBean("kyc310CorpRptComp", KYC310_CorpRptComp.class);
			report.setInputVO(inputVO);
			String filePath = report.getPDFPath();
			mergeUrl.add(filePath);
		} else {	//自然人差異表單
			KYC310_PersonRptComp report = PlatformContext.getBean("kyc310PersonRptComp", KYC310_PersonRptComp.class);
			report.setInputVO(inputVO);
			String filePath = report.getPDFPath();
			mergeUrl.add(filePath);
		}
		String url = PdfUtil.process(new PdfConfigVO(this.getDataAccessManager(), true, mergeUrl));
		
		notifyClientViewDoc(url, "pdf"); 	
	}
	
	/***
	 * 客戶勾選年收入區間與端末系統留存年收入比較
	 * @param q2Ans：第二題年收入答案選項
	 * @param incomeCBS：端末系統留存年收入
	 * @return true: 客戶端末收入介於KYC收入區間 false:客戶端末收入"不"介於KYC收入區間
	 * @throws JBranchException
	 */
	private boolean getIncomeCBSChk(String q2Ans, BigDecimal incomeCBS) throws JBranchException {
		boolean rtnResult = false;
		
		//取得答案序號
		int ansSeq = Integer.parseInt(q2Ans);
		//KYC收入下限
		BigDecimal lowerLimit = new BigDecimal(getParamName("IOT.API_KYC_Q2_ANS", String.valueOf(ansSeq-1)));
		//KYC收入上限
		BigDecimal upperLimit = new BigDecimal(getParamName("IOT.API_KYC_Q2_ANS", String.valueOf(ansSeq)));
		//客戶端末收入介於KYC收入區間
		if(incomeCBS.compareTo(lowerLimit) >= 0 && incomeCBS.compareTo(upperLimit) <= 0) {
			rtnResult = true;
		}
		
		return rtnResult;
	}
	
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
}
