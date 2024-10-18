package com.systex.jbranch.app.server.fps.kyc311;

import com.systex.jbranch.app.common.fps.table.TBKYC_COOLING_PERIODVO;
import com.systex.jbranch.app.common.fps.table.TBKYC_INVESTOREXAM_MVO;
import com.systex.jbranch.app.common.fps.table.TBKYC_INVESTOREXAM_M_HISVO;
import com.systex.jbranch.app.server.fps.cmfpg000.CMFPG000;
import com.systex.jbranch.app.server.fps.kyc.chk.KYCCheckIdentityWeights;
import com.systex.jbranch.app.server.fps.kycoperation.KycOperationDao;
import com.systex.jbranch.app.server.fps.oth001.OTH001;
import com.systex.jbranch.app.server.fps.sot701.FC032153DataVO;
import com.systex.jbranch.app.server.fps.sot701.FP032675DataVO;
import com.systex.jbranch.app.server.fps.sot701.SOT701;
import com.systex.jbranch.app.server.fps.sot701.SOT701InputVO;
import com.systex.jbranch.common.io.util.PdfInputOutputUtils;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.fubon.commons.cbs.CBSService;
import com.systex.jbranch.fubon.commons.esb.vo.fc032154.FC032154InputVO;
import com.systex.jbranch.fubon.commons.esb.vo.fp032151.FP032151OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.fp032675.FP032675OutputVO;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.DateUtil;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
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
import java.sql.Blob;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * KYC311
 *
 * @author Jimmy
 * @date 2016/08/22
 * @spec null
 */
@Component("kyc311")
@Scope("request")
public class KYC311 extends FubonWmsBizLogic {
	Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	@Qualifier("KYCCheckIdentityWeights")
	private KYCCheckIdentityWeights KYCCheck;

	@Autowired
	@Qualifier("sot701")
	private SOT701 sot701;

	@Autowired
	@Qualifier("cmfpg000")
	private CMFPG000 cmfpg000;

	@Autowired
	@Qualifier("KycOperationDao")
	private KycOperationDao kycOperationDao;

	@Autowired
	private CBSService cbsService;

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { APException.class, JBranchException.class, Exception.class })
	public void delete(Object body, IPrimitiveMap<Object> header) throws JBranchException {
		KYC311InputVO inputVO = (KYC311InputVO) body;
		Map<String, Object> errorMsg = new HashMap<String, Object>();
		TBKYC_INVESTOREXAM_MVO investorexamMaster = null;// 現行主檔
		Map<String, Object> param = new Hashtable<String, Object>();

		try {
			// 發送LDAP確認主管帳密是否正確
			checkBossAdmin(inputVO);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new JBranchException(e);
		}

		try {
			// 取客戶主檔
			investorexamMaster = (TBKYC_INVESTOREXAM_MVO) getDataAccessManager().findByPKey(TBKYC_INVESTOREXAM_MVO.TABLE_UID, inputVO.getSEQ());

			// (1)主管權限-個金主管僅能審核個金OP鑑機人員的案件，企金主管僅能審核企金OP鑑機人員的案件
			List<String> supervisoList = KYCCheck.checkBossBossJurisdiction(investorexamMaster.getCreator(), inputVO.getEMP_ID());
			if (CollectionUtils.isNotEmpty(supervisoList)) {
				errorMsg.put("ErrorMsg", supervisoList);
				sendRtnObject(errorMsg);
				return;
			}
			// (2)判斷鑑機人員是否為住管轄下
			else if (!KYCCheck.checkInBossUnderJurisdiction(inputVO.getEMP_ID(), investorexamMaster.getCreator())) {
				errorMsg.put("ErrorMsg016", "ehl_02_KYC310_016");
				sendRtnObject(errorMsg);
				return;
			}

			// (3)變更現行主檔狀態為刪除
			Date now = new Date();
			investorexamMaster.setSTATUS("04");
			investorexamMaster.setSIGNOFF_ID(inputVO.getEMP_ID());
			investorexamMaster.setSIGNOFF_DATE(new Timestamp(now.getTime()));

			// 客戶欲調降風險屬性為"請選擇"給予測試完結果
			investorexamMaster.setCUST_RISK_MDF(StringUtils.isNotBlank(inputVO.getDown_risk_level()) ? inputVO.getDown_risk_level() : inputVO.getCUST_RISK_AFR());

			investorexamMaster.setOUT_ACCESS(inputVO.getOUT_ACCESS());
			investorexamMaster.setDEL_TYPE(inputVO.getDEL_TYPE());

			// 更新客戶主檔
			getDataAccessManager().update(investorexamMaster);

			// 設定客戶ID
			param.put("cust_id", inputVO.getCUST_ID());

			// (4)將現行的主檔寫入歷程
			exeUpdateForMap(" Insert into TBKYC_INVESTOREXAM_M_HIS  Select * from TBKYC_INVESTOREXAM_M  Where cust_id = :cust_id ", param);

			// (5)將現行的明細寫入歷程
			exeUpdateForMap(" Insert into TBKYC_INVESTOREXAM_D_HIS  Select * from TBKYC_INVESTOREXAM_D  Where cust_id = :cust_id", param);

			// (6)刪除現行明細
			exeUpdateForQcf(genDefaultQueryConditionIF().setQueryString(" delete TBKYC_INVESTOREXAM_D where seq =　:seq ").setObject("seq", inputVO.getSEQ()));

			// (7)刪除現行的主檔
			investorexamMaster = new TBKYC_INVESTOREXAM_MVO();
			investorexamMaster = (TBKYC_INVESTOREXAM_MVO) getDataAccessManager().findByPKey(TBKYC_INVESTOREXAM_MVO.TABLE_UID, inputVO.getSEQ());
			getDataAccessManager().delete(investorexamMaster);

			// (8)並取上次生效的主檔歷程寫入主檔
			param.put("seq", inputVO.getSEQ());

			exeUpdateForMap(new StringBuilder().append(" Insert into TBKYC_INVESTOREXAM_M ").append(" Select * from TBKYC_INVESTOREXAM_M_HIS ").append(" Where status = '03'")// 生效
					.append(" and seq <> :seq ")// 排除這次的
					.append(" and cust_id = :cust_id").append(" order by CREATE_DATE DESC FETCH FIRST 1 ROWS ONLY ")// 歷程中最新的
					.toString(), param);

			// (9)排除剛寫入的新歷程，並取上次生效的主檔歷程寫入主檔
			exeUpdateForMap(new StringBuilder().append(" Insert into TBKYC_INVESTOREXAM_D ").append(" Select * from TBKYC_INVESTOREXAM_D_HIS ").append(" Where seq in( ").append(" Select seq from TBKYC_INVESTOREXAM_M_HIS ").append(" Where status = '03'")// 生效
					.append(" and seq <> :seq ")// 排除這次的
					.append(" and cust_id = :cust_id").append(" order by CREATE_DATE DESC FETCH FIRST 1 ROWS ONLY ")// 歷程中最新的
					.append(" ) ").toString(), param);

			sendRtnObject(null);
			return;
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}

	public String initTp032675Data(Object body, IPrimitiveMap<Object> header) throws JBranchException, ParseException {
		KYC311InputVO inputVO = (KYC311InputVO) body;
		XmlInfo xmlInfo = new XmlInfo();
		String CUST_DATA = "                                          ";
		String CUST_RISK_AFR = null;// 風險值
		String KYC_TEST_DATE = "00000000";// 承作日期
		String EXPIRY_DATE = "00000000";// 有效月份
		String loginbranch = "";// 承作分行

		if (inputVO.isBranch()) {
			loginbranch = "999";
		} else {
			loginbranch = (String) getCommonVariable(SystemVariableConsts.LOGINBRH);
		}

		loginbranch = loginbranch == null ? "000" : loginbranch;

		String EMP_ID;// 承作員編
		String EDUCATION;// 學歷
		String MARRAGE;// 婚姻
		String CHILD_NO;// 子女數
		String CAREER;// 職業
		String SICK_TYPE_YN = " ";// 重大傷病
		String SICK_TYPE;// 重大傷病等級
		String Y_N_update;// 是否更新

		// 風險值
		if (inputVO.getCUST_RISK_AFR() != null) {
			CUST_RISK_AFR = inputVO.getCUST_RISK_AFR();
		} else {
			CUST_RISK_AFR = "  ";
		}

		// 承作日期
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat sdf_toDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

		if (inputVO.getBasic_information().get("KYC_TEST_DATE") != null) {
			KYC_TEST_DATE = sdf.format(inputVO.getBasic_information().get("KYC_TEST_DATE"));
		} else {
			if (inputVO.getBasic_information().get("KYC_TEST_DATE_TEMP") != null) {
				KYC_TEST_DATE = sdf.format(sdf_toDate.parse(inputVO.getBasic_information().get("KYC_TEST_DATE_TEMP").toString()));
			} else {
				KYC_TEST_DATE = sdf.format(new Date());
			}
		}

		// 有效月份
		String SEQ;
		if (inputVO.getBasic_information().get("seq") != null) {
			SEQ = inputVO.getBasic_information().get("seq").toString();

			QueryConditionIF qc = getDataAccessManager().getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuilder sb = new StringBuilder();
			sb.append(" select EXPIRY_DATE from TBKYC_INVESTOREXAM_M where SEQ = :seq ");
			qc.setObject("seq", SEQ);
			qc.setQueryString(sb.toString());
			List<Map<String, Object>> SEQList = getDataAccessManager().exeQueryWithoutSort(qc);
			SimpleDateFormat sdf_month = new SimpleDateFormat("MM");
			EXPIRY_DATE = "000000" + sdf_month.format(SEQList.get(0).get("EXPIRY_DATE"));
		}

		// 承作員編
		if (inputVO.getBasic_information().get("COMMENTARY_STAFF") != null) {
			EMP_ID = inputVO.getBasic_information().get("COMMENTARY_STAFF").toString();
		} else {
			EMP_ID = "      ";
		}

		// 學歷
		if (inputVO.getBasic_information().get("EDUCATION") != null) {
			String EDUCATION_Change = xmlInfo.getVariable("KYC.EDUCATION", inputVO.getBasic_information().get("EDUCATION").toString(), "F3");
			if (EDUCATION_Change.length() > 2) {
				EDUCATION_Change = EDUCATION_Change.substring(0, 2);
			}

			QueryConditionIF qc = getDataAccessManager().getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuilder sb = new StringBuilder();
			sb.append(" select PARAM_CODE from TBSYSPARAMETER where PARAM_TYPE ='KYC.EDUCATION_UP' and PARAM_NAME like :education_name ");
			qc.setObject("education_name", "%" + EDUCATION_Change + "%");
			qc.setQueryString(sb.toString());
			List<Map<String, Object>> educationList = getDataAccessManager().exeQueryWithoutSort(qc);

			if (educationList.size() > 0) {
				EDUCATION = educationList.get(0).get("PARAM_CODE").toString();
			} else {
				EDUCATION = inputVO.getBasic_information().get("EDUCATION").toString();
			}
		} else {
			EDUCATION = " ";
		}

		// 婚姻
		if (inputVO.getBasic_information().get("MARRAGE") != null) {
			MARRAGE = inputVO.getBasic_information().get("MARRAGE").toString();
		} else {
			MARRAGE = " ";
		}

		// 子女數
		if (inputVO.getBasic_information().get("CHILD_NO") != null) {
			if ("0".equals(inputVO.getBasic_information().get("CHILD_NO").toString())) {
				CHILD_NO = "5";
			} else {
				CHILD_NO = inputVO.getBasic_information().get("CHILD_NO").toString();
			}
		} else {
			CHILD_NO = " ";
		}

		// 職業
		if (inputVO.getBasic_information().get("CAREER") != null) {
			if (inputVO.getBasic_information().get("CAREER").toString().length() < 2) {
				CAREER = inputVO.getBasic_information().get("CAREER").toString() + " ";
			} else {
				CAREER = inputVO.getBasic_information().get("CAREER").toString();
			}
		} else {
			CAREER = "  ";
		}

		// 重大傷病及重大傷病等級
		if (inputVO.getBasic_information().get("SICK_TYPE") != null) {
			SICK_TYPE = inputVO.getBasic_information().get("SICK_TYPE").toString();

			if (!"1".equals(SICK_TYPE)) {
				SICK_TYPE_YN = "Y";
			}
		} else {
			SICK_TYPE = " ";
		}

		// 法人
		if (inputVO.getCUST_ID().length() < 10) {
			CUST_DATA = CUST_RISK_AFR + KYC_TEST_DATE + EXPIRY_DATE + loginbranch + EMP_ID + "Y";
		}
		// 自然人
		else {
			if (inputVO.getBasic_information().get("Y_N_update") != null) {
				BigInteger Y_N_update_change = new BigDecimal(inputVO.getBasic_information().get("Y_N_update").toString()).toBigInteger();
				Y_N_update = Y_N_update_change.toString();
			} else {
				Y_N_update = "1";
			}

			switch (Y_N_update) {
			case "1":
				CUST_DATA = CUST_RISK_AFR + KYC_TEST_DATE + EXPIRY_DATE + loginbranch + EMP_ID + "Y";
				break;
			case "2":
				CUST_DATA = CUST_RISK_AFR + KYC_TEST_DATE + EXPIRY_DATE + loginbranch + EMP_ID + "Y" + EDUCATION + MARRAGE + CHILD_NO + CAREER + SICK_TYPE_YN + SICK_TYPE;
				break;
			default:
				break;
			}
		}
		return CUST_DATA;
	}

	/** 確定後記錄歷程檔、調整主黨狀態為03...等 **/
	public void Submit(Object body, IPrimitiveMap<Object> header) throws Exception {
		KYC311InputVO inputVO = (KYC311InputVO) body;// 頁面帶進來的物件
		Map<String, Object> message = new HashMap<String, Object>();// 錯誤訊息

		// (1)發送LDAP驗證主管帳密
		checkBossAdmin(inputVO);

		// (2)檢核轄下關係
		if (MapUtils.isNotEmpty(message = checkKycEmpId(inputVO.getEMP_ID(), inputVO.getSEQ()))) {
			sendRtnObject(message);
			return;
		}
		// (3)更新kyc相關的資料表以及發送電文更新390資料
		message = saveUpdateKyc(inputVO, header);

		if (MapUtils.isEmpty(message)) {
			message.put("success", "ehl_01_common_001");
		}

		sendRtnObject(message);
	}

	public Map<String, Object> checkKycEmpId(String empId, String seq) throws Exception {
		String loginID = (String) getCommonVariable(SystemVariableConsts.LOGINID);
		Map<String, Object> errorMsg = new HashMap<String, Object>();// 錯誤訊息
		TBKYC_INVESTOREXAM_MVO timVo = (TBKYC_INVESTOREXAM_MVO) getDataAccessManager().findByPKey(TBKYC_INVESTOREXAM_MVO.TABLE_UID, seq);
		String creater = timVo.getCreator();

		List<String> supervisoList = KYCCheck.checkBossBossJurisdiction(creater, empId);
		// (1)檢核鍵機與覆核是否為同一人
		if (StringUtils.equals(creater, empId)) {
			errorMsg.put("ErrorMsg043", "ehl_01_KYC310_043");
		}
		// (2)檢核權限
		else if (CollectionUtils.isNotEmpty(supervisoList)) {
			errorMsg.put("ErrorMsg", supervisoList);
		}
		// (3)判斷轄下關係
		else if (!KYCCheck.checkInBossUnderJurisdiction(empId, creater)) {
			errorMsg.put("ErrorMsg016", "ehl_02_KYC310_016");
		}

		return errorMsg;
	}

	@SuppressWarnings("unchecked")
	public void checkBossAdmin(KYC311InputVO inputVO) throws Exception {
		String bossEmpId = ObjectUtils.toString(inputVO.getEMP_ID());
		String bossPd = ObjectUtils.toString(inputVO.getEMP_PASSWORD());
		String email = null;
		List<Map<String, Object>> bossMsglist = null;

		if (StringUtils.isBlank(bossEmpId)) {
			throw new JBranchException("請輸入主管員編");
		}

		if (StringUtils.isBlank(bossPd)) {
			throw new JBranchException("請輸入主管密碼");
		}

		DataAccessManager dam = getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		qc.setQueryString("select EMP_EMAIL_ADDRESS from TBORG_MEMBER where EMP_ID = :userId");
		qc.setObject("userId", bossEmpId);
		bossMsglist = dam.exeQuery(qc);

		if (CollectionUtils.isEmpty(bossMsglist)) {
			throw new JBranchException("無此員編:" + inputVO.getEMP_ID());
		}

		email = ObjectUtils.toString(bossMsglist.get(0).get("EMP_EMAIL_ADDRESS"));
		// 20190716避開ldap檢核
		// 富盛案平測，避開ldap檢核
		if (!PlatformContext.getBean(OTH001.class).skipKycLdap()) {
			cmfpg000.checkLoginForLdap(bossEmpId, bossPd, email);
		}
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = { JBranchException.class, Exception.class })
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map<String, Object> saveUpdateKyc(KYC311InputVO inputVO, IPrimitiveMap<Object> header) throws Exception {
		StringBuffer message = new StringBuffer();
		Map<String, Object> errorMsg = new HashMap();

		try {
			Timestamp expiryDate = null;// 到期日

			TBKYC_INVESTOREXAM_MVO timVo = (TBKYC_INVESTOREXAM_MVO) getDataAccessManager().findByPKey(TBKYC_INVESTOREXAM_MVO.TABLE_UID, inputVO.getSEQ());

			// (3)客戶主檔處理
			timVo.setSTATUS("03");// 主管放行
			timVo.setSIGNOFF_ID(inputVO.getEMP_ID());
			timVo.setSIGNOFF_DATE(new Timestamp(new Date().getTime()));
			timVo.setDIFF_DAYS(new BigDecimal(0));

			// 抓上一次的鑑機日期
			List<Map<String, Object>> lastCreateDatelist = exeQueryWithoutSortForQcf(genDefaultQueryConditionIF().setQueryString(new StringBuilder().append(" select CREATE_DATE from TBKYC_INVESTOREXAM_M_HIS where STATUS = '03' ").append(" and CUST_ID = :custid ").append(" order by CREATE_DATE DESC FETCH FIRST 1 ROWS ONLY ").toString()).setObject("custid", inputVO.getCUST_ID()));

			// 如果有上次已放行的kyc，計算本次鑑機日期與上次鑑機日期的相距天數
			if (CollectionUtils.isNotEmpty(lastCreateDatelist) && lastCreateDatelist.get(0).get("CREATE_DATE") != null) {
				// 上次的鑑機日期
				Date lastCreateDate = new SimpleDateFormat("yyyy-MM-dd").parse(lastCreateDatelist.get(0).get("CREATE_DATE").toString());

				if (lastCreateDate != null) {
					Date nowCreateDate = DateUtil.replaceTime(timVo.getCreatetime());
					lastCreateDate = DateUtil.replaceTime(lastCreateDate);
					long DIFF_DAYS = (nowCreateDate.getTime() - lastCreateDate.getTime()) / (24 * 60 * 60 * 1000);
					timVo.setDIFF_DAYS(new BigDecimal(DIFF_DAYS));
				}
			}

			// 檢查是否需要進入冷靜期
			Map<String, String> coolingMap = getCoolingPeriodRiskVal(inputVO);
			String coolingRiskVal = coolingMap.get("COOLING_RISK");
			Boolean isStillInCooling = StringUtils.equals("Y", coolingMap.get("STILL_IN_COOLING")) ? true : false;

			if (isStillInCooling && StringUtils.isBlank(coolingRiskVal)) {
				// 取消冷靜期資料
				updateKycCoolingPeriod(coolingRiskVal, isStillInCooling, inputVO);
				// 取消冷靜期後，將上一次測驗的值寫回正確的C值
				timVo.setCUST_RISK_BEF(inputVO.getCUST_RISK_BEF());
			} else if (StringUtils.isNotBlank(coolingRiskVal)) { // 需進入冷靜期
				// 需要進入冷靜期,寫入冷靜期表格
				// 將須傳送390資料記錄下來
				inputVO.setCUST_DATA_390(initTp032675Data(inputVO, null));
				// 更新冷靜期資料
				updateKycCoolingPeriod(coolingRiskVal, isStillInCooling, inputVO);

				// 進入冷靜期，C值為前一次C值
				inputVO.setCUST_RISK_AFR(coolingRiskVal);
				timVo.setCUST_RISK_AFR(coolingRiskVal);

				message.append("\r\nCooling Period Risk level [").append(coolingRiskVal).append("]\r\n");
			}

			// 客戶欲調降風險屬性為"請選擇"給予測試完結果
			timVo.setCUST_RISK_MDF(StringUtils.isNotBlank(inputVO.getDown_risk_level()) ? inputVO.getDown_risk_level() : inputVO.getCUST_RISK_AFR());
			timVo.setOUT_ACCESS(inputVO.getOUT_ACCESS());

			// 送390，如果有errorMsg代表更新email或地址失敗，其他則會拋出例外

			//20200519_CBS_麗文_KYC67157分行問題 根據UAT抓登入員編分行
			String loginbranch = (String) getCommonVariable(SystemVariableConsts.LOGINBRH);
			loginbranch = loginbranch == null ? "000" : loginbranch;
			inputVO.setBRANCH(loginbranch);
			errorMsg = sendCustDataTo390(inputVO, header);

			message.append("\r\nsend 390 update risk level [").append(inputVO.getCUST_ID()).append("]\r\n");

			// (5)更新客戶主檔
			getDataAccessManager().update(timVo);

			// 新增錄音編號
			exeUpdateForQcf(genDefaultQueryConditionIF().setQueryString(new StringBuilder().append(" update TBKYC_INVESTOREXAM_M set REC_SEQ = :rec_seq").append(" where SEQ = :seq ").toString()).setObject("rec_seq", inputVO.getREC_SEQ()).setObject("seq", inputVO.getSEQ()));

			message.append("update TBKYC_INVESTOREXAM_MVO [").append(inputVO.getCUST_ID()).append("]\r\n");

			// (6)寫入主檔歷程
			exeUpdateForQcf(genDefaultQueryConditionIF().setQueryString(" Insert into TBKYC_INVESTOREXAM_M_HIS  Select * from TBKYC_INVESTOREXAM_M  Where cust_id = :cust_id ").setObject("cust_id", inputVO.getCUST_ID().toUpperCase()));

			message.append("insert TBKYC_INVESTOREXAM_M_HIS ").append("[").append(inputVO.getCUST_ID()).append("]\r\n");

			// (7)寫入明細歷程
			exeUpdateForQcf(genDefaultQueryConditionIF().setQueryString(" Insert into TBKYC_INVESTOREXAM_D_HIS  Select * from TBKYC_INVESTOREXAM_D  Where cust_id = :cust_id ").setObject("cust_id", inputVO.getCUST_ID().toUpperCase()));

			message.append("insert TBKYC_INVESTOREXAM_D_HIS ").append("[").append(inputVO.getCUST_ID()).append("]\r\n");

			// 取現行主檔到期日
			List<Map<String, Object>> exDateList = exeQueryWithoutSortForQcf(genDefaultQueryConditionIF().setQueryString(" Select EXPIRY_DATE from TBKYC_INVESTOREXAM_M where CUST_ID = :cust_id ").setObject("cust_id", inputVO.getCUST_ID().toUpperCase()));

			// 到期日
			expiryDate = exDateList.get(0).get("EXPIRY_DATE") != null ? new Timestamp(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(exDateList.get(0).get("EXPIRY_DATE").toString()).getTime()) : expiryDate;

			// (8)更新當下最新風險屬性及到期日
			try {
				updateKycNowStatus(inputVO.getCUST_ID(), inputVO.getQuestionList(), inputVO.getCUST_RISK_AFR(), expiryDate);
			} catch (Exception ex) {
				logger.error(StringUtil.getStackTraceAsString(ex));
			}
		} finally {
			logger.info(message.toString());
		}

		return errorMsg;
	}

	/**
	 * 取得冷靜期C值，若為空值表示不需進入冷靜期
	 * 測試案例：
	 *	1.	全新客戶(從未做過KYC客戶)第一次評估 => 不用進入冷靜期
	 *	2.	全新客戶當天第二次做且較前次C值為高 => 進入冷靜期
	 *	3.	當日做的C值較前次C值(不論是否為當日)為高  => 進入冷靜期
	 *	4.	當日取消冷靜期後；當日又重新評估且較前次C值為高 => 再次進入冷靜期 (其實同第3點，只要非冷靜期中，KYC評估較前次C值為高，則進入冷靜期)
	 *	5.	在冷靜期期間重新評估，重新評估後的C值沒有較未進入冷靜期前的C值為高 => 取消冷靜期
	 *		範例：
	 *			客戶原KYC為C2
	 *			6/27承做KYC為C3 => 進入冷靜期
	 *			6/28於冷靜期中，承做KYC為C2 => 取消冷靜期 (與原C2比較，沒有高於C2)
	 *	6.	在冷靜期期間又重新評估，仍較前次C值為高 => 仍為冷靜期，生效日重新計算T+2
	 *		範例：
	 *			客戶原KYC為C1
	 *			6/27承做KYC為C3 => 進入冷靜期，冷靜期解除日為6/27 + 2
	 *			6/28於冷靜期中，承做KYC為C2 => 仍為冷靜期，冷靜期解除日重新計算為6/28 + 2 (與原C1比較，高於C1)
	 *
	 * @param inputVO
	 * @return COOLING_RISK: 冷靜期C值 ; STILL_IN_COOLING: 是否原來就在冷靜期中
	 * @throws DAOException
	 * @throws JBranchException
	 */
	public Map<String, String> getCoolingPeriodRiskVal(KYC311InputVO inputVO) throws DAOException, JBranchException {
		DataAccessManager dam = getDataAccessManager();
		StringBuilder sb = new StringBuilder();
		String coolingRiskVal = "";
		String isStillInCooling = "N";

		// 檢查現在是否仍在冷靜期中
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		qc.setQueryString("SELECT SEQ, CUST_RISK_BEF, CUST_RISK_AFR FROM TBKYC_COOLING_PERIOD WHERE CUST_ID = :custId AND STATUS = 'C' ");
		qc.setObject("custId", inputVO.getCUST_ID());
		List<Map<String, Object>> mlist = dam.exeQuery(qc);

		if (CollectionUtils.isNotEmpty(mlist)) { //現仍在冷靜期中
			isStillInCooling = "Y";

			if (isRiskAftGreaterRiskBef((String)mlist.get(0).get("CUST_RISK_BEF"), inputVO.getCUST_RISK_AFR())) {
				//在冷靜期期間又重新評估，重新評估後的C值較未進入冷靜期前的C值為高
				coolingRiskVal = ObjectUtils.toString(mlist.get(0).get("CUST_RISK_BEF"));
			} else {
				//在冷靜期期間又重新評估，重新評估後的C值沒有較未進入冷靜期前的C值為高 => 取消冷靜期
				coolingRiskVal = "";
				// 取消冷靜期後，將上一次測驗的值寫回
				inputVO.setCUST_RISK_BEF(ObjectUtils.toString(mlist.get(0).get("CUST_RISK_AFR")));
			}
		} else { //目前不在冷靜期中
			// 取得前一次KYC評估結果
			String lastCustRiskBef = "";
			if (inputVO.isBranch()) { // 網行銀KYC還未寫入TBKYC_INVESTOREXAM_M，由歷史檔取得上次風險屬性CUST_RISK_AFR
				// 取上一次的風險屬性 //與網銀取得方式一致
				List<Map<String, Object>> lastMasHis03List = kycOperationDao.queryLastMastHisForStatus03(inputVO.getCUST_ID());
				if (CollectionUtils.isNotEmpty(lastMasHis03List)) {
					// 上次測驗的風險屬性
					lastCustRiskBef = (String) lastMasHis03List.get(0).get("CUST_RISK_AFR");
				}
			} else { // 臨櫃KYC，鍵機頁面已寫入TBKYC_INVESTOREXAM_M，覆核頁面直接取得上次風險屬性CUST_RISK_BEF，避免不一致
				qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				qc.setQueryString("SELECT CUST_RISK_BEF FROM TBKYC_INVESTOREXAM_M WHERE SEQ = :seq ");
				qc.setObject("seq", inputVO.getSEQ());
				List<Map<String, Object>> alist = dam.exeQuery(qc);
				if (CollectionUtils.isNotEmpty(alist) && StringUtils.isNotBlank(ObjectUtils.toString(alist.get(0).get("CUST_RISK_BEF")))) {
					lastCustRiskBef = alist.get(0).get("CUST_RISK_BEF").toString();
				}
			}

			//若前次C值為C5，則同現行最高C4
			if(StringUtils.equals(lastCustRiskBef, "C5")) lastCustRiskBef = "C4";

			if(StringUtils.isBlank(lastCustRiskBef)) {
				//若沒有之前KYC評估資料，為當天第一次做KYC，不須進入冷靜期
			} else if(isRiskAftGreaterRiskBef(lastCustRiskBef, inputVO.getCUST_RISK_AFR())) {
				//本次評估C值是否較前次為高，則進入冷靜期
				coolingRiskVal = lastCustRiskBef;
			}
		}

		inputVO.setCOOLING(StringUtils.isBlank(coolingRiskVal) ? false : true);
		Map<String, String> rtnMap = new HashMap<String, String>();
		rtnMap.put("COOLING_RISK", coolingRiskVal); // 是否要進入冷靜期 空值:不需要, 有值:KYC最後一次C值(進入冷靜期後維持此C值)
		rtnMap.put("STILL_IN_COOLING", isStillInCooling); // 是否仍在冷靜期 Y:是 N:否

		return rtnMap;
	}

	/***
	 * 本次評估C值是否較前次為高
	 * 本次較前次為高 => return TRUE
	 * @param riskBEF
	 * @param riskAFR
	 * @return
	 */
	private boolean isRiskAftGreaterRiskBef(String riskBEF, String riskAFR) {
		try {
			int bef = Integer.parseInt(riskBEF.substring(1));
			int afr = Integer.parseInt(riskAFR.substring(1));
			if(afr > bef)
				return true; //本次評估C值較前次為高
			else
				return false;
		} catch(Exception e) {
			return false;
		}
	}

	/***
	 * 更新: KYC測驗冷靜期資料檔
	 *
	 * @param coolingRiskVal
	 * @param inputVO
	 * @throws JBranchException
	 * @throws ParseException
	 */
	public void updateKycCoolingPeriod(String coolingRiskVal, Boolean isStillInCooling, KYC311InputVO inputVO) throws JBranchException, ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		DataAccessManager dam = getDataAccessManager();
		Timestamp effDate = null;

		if (isStillInCooling) {
			// 仍在冷靜期中，更新註解並將原冷靜期資料狀態改為"取消"
			List<Criterion> criList = new ArrayList<Criterion>();
			criList.add(Restrictions.eq("CUST_ID", inputVO.getCUST_ID()));
			criList.add(Restrictions.eq("STATUS", "C"));
			List<TBKYC_COOLING_PERIODVO> result = (List<TBKYC_COOLING_PERIODVO>) dam.findByCriteria(TBKYC_COOLING_PERIODVO.TABLE_UID, criList);

			for (TBKYC_COOLING_PERIODVO tvo : result) {
				// 取得原冷靜期生效日
				effDate = tvo.getEFFECTIVE_DATE();
				// 取得SEQ
				String seq = tvo.getSEQ();

				if (StringUtils.isBlank(coolingRiskVal)) {
					tvo.setMEMO(ObjectUtils.toString(tvo.getMEMO()) + sdf.format(new Date()) + ":" + inputVO.getSEQ() + "重測取消冷靜期; ");
				} else {
					tvo.setMEMO(ObjectUtils.toString(tvo.getMEMO()) + sdf.format(new Date()) + ":" + inputVO.getSEQ() + "重測仍為冷靜期，將此筆取消; ");
				}
				tvo.setSTATUS("N"); // 取消
				tvo.setSENT_390_STATUS("C"); // 取消

				getDataAccessManager().update(tvo);

				// 原冷靜期取消後，將KYC主檔及歷史檔資料還原，下次重作KYC的時候CUST_RISK_BEF才會抓到正確的值
				// 更新KYC主檔
				TBKYC_INVESTOREXAM_MVO timVO = (TBKYC_INVESTOREXAM_MVO) dam.findByPKey(TBKYC_INVESTOREXAM_MVO.TABLE_UID, seq);
				if (timVO != null) {
					timVO.setCUST_RISK_BEF(tvo.getCUST_RISK_BEF());
					timVO.setCUST_RISK_AFR(tvo.getCUST_RISK_AFR());
					dam.update(timVO);
				}

				// 更新KYC主檔歷史資料
				TBKYC_INVESTOREXAM_M_HISVO timHisVO = (TBKYC_INVESTOREXAM_M_HISVO) dam.findByPKey(TBKYC_INVESTOREXAM_M_HISVO.TABLE_UID, seq);
				if (timHisVO != null) {
					timHisVO.setCUST_RISK_BEF(tvo.getCUST_RISK_BEF());
					timHisVO.setCUST_RISK_AFR(tvo.getCUST_RISK_AFR());
					dam.update(timHisVO);
				}
			}
		}

		//若仍需進入冷靜期，寫一筆新紀錄 (若只是取消冷靜期就不需要)
		if (StringUtils.isNotBlank(coolingRiskVal)) {
			Date now = new Date();
			effDate = new Timestamp(DateUtils.addDays(now, 2).getTime()); //生效日為2天後

			Date dEffDate = sdf.parse(sdf.format(effDate));
			Date dExpiryDate = dEffDate;

			if(inputVO.isBranch()) {
				//從網銀過來
				dExpiryDate = sdf.parse(inputVO.getEXPIRY_DATE());
			} else {
				//臨櫃
				TBKYC_INVESTOREXAM_MVO timVO = (TBKYC_INVESTOREXAM_MVO) dam.findByPKey(TBKYC_INVESTOREXAM_MVO.TABLE_UID, inputVO.getSEQ());
				if (timVO != null) {
					dExpiryDate = sdf.parse(sdf.format(timVO.getEXPIRY_DATE()));
				}
			}

			TBKYC_COOLING_PERIODVO tcpVO = new TBKYC_COOLING_PERIODVO();
			tcpVO.setSEQ(inputVO.getSEQ());
			tcpVO.setCUST_ID(inputVO.getCUST_ID());
			tcpVO.setCUST_RISK_AFR(inputVO.getCUST_RISK_AFR());
			tcpVO.setCUST_RISK_BEF(coolingRiskVal);
			if(dEffDate.compareTo(dExpiryDate) > 0) {	//解除冷靜期生效日 > KYC到期日 ==> 不須解除冷靜期，不須傳送主機
				tcpVO.setSTATUS("N");			//取消
				tcpVO.setSENT_390_STATUS("C");	//取消
				tcpVO.setMEMO("冷靜期解除生效日大於KYC到期日。則不解除冷靜期且不傳送主機。");
			} else {
				tcpVO.setSTATUS("C");			//冷靜期中
				tcpVO.setSENT_390_STATUS("N");	//待傳送主機
			}
			tcpVO.setSENT_390_DATA(inputVO.getCUST_DATA_390());
			tcpVO.setCREATE_DATE(new Timestamp(now.getTime()));
			tcpVO.setEFFECTIVE_DATE(effDate);
			getDataAccessManager().create(tcpVO);

			// 回傳冷靜期解除生效日，網行銀需要這個欄位
			inputVO.setCOOLING_EFF_DATE(effDate);
		}
	}

	// /**更新當下最新風險屬性及到期日**/
	// @SuppressWarnings({ "unchecked", "null" })
	// public void updateKycNowStatus(String custId ,List<Map<String, Object>>
	// questionList, String riskLevel , Timestamp expiryDate) throws
	// JBranchException{
	// StringBuffer queryStr = new StringBuffer();
	// Set<String> allkey = null;
	// int nowIdx = 1;
	//
	// QueryConditionIF queryCondition = genDefaultQueryConditionIF();
	// Map<String , Object> kycNowDataMap = new HashMap<String , Object>();
	//
	// kycNowDataMap.put("CUST_RISK_ATR", null);//風險屬性
	// kycNowDataMap.put("KYC_DUE_DATE", null);//到期日
	// kycNowDataMap.put("ANNUAL_INCOME_AMT", null);//到期日
	// kycNowDataMap.put("STOP_LOSS_PT", new BigDecimal(0));//停損點
	// kycNowDataMap.put("TAKE_PRFT_PT", new BigDecimal(0));//停利點
	//
	// List<Map<String , Object>> kycNowDatas =
	// exeQueryWithoutSortForQcf(genDefaultQueryConditionIF()
	// .setQueryString(" select CUST_ID from TBKYC_INVESTOREXAM_NOW WHERE cust_id =:custId ")
	// .setObject("custId", custId));
	//
	// if(riskLevel != null)
	// kycNowDataMap.put("CUST_RISK_ATR", riskLevel);//風險屬性
	//
	// if(expiryDate != null)
	// kycNowDataMap.put("KYC_DUE_DATE", expiryDate);//到期日
	//
	// kycNowDataMap.putAll(doGetToUpdateCustMastData(custId , questionList));
	//
	// if(MapUtils.isEmpty(kycNowDataMap))
	// return;
	//
	// //尚未有資料，要新增
	// if(CollectionUtils.isEmpty(kycNowDatas)){
	// StringBuffer values = new StringBuffer();
	// queryStr.append(" insert into TBKYC_INVESTOREXAM_NOW ( CUST_ID ,");
	// values.append(" :CUST_ID , ");
	//
	// for(String key : allkey = kycNowDataMap.keySet()){
	// queryStr.append(key).append(nowIdx < allkey.size() ? " , " : "");
	// values.append(" :").append(key).append(nowIdx < allkey.size() ? " , " :
	// "");
	// queryCondition.setObject(key , kycNowDataMap.get(key));
	// nowIdx++;
	// }
	// queryStr.append(" ) VALUES(").append(values).append(" ) ");
	// }
	// //有資料要更新
	// else{
	// updateCustMast("TBKYC_INVESTOREXAM_NOW" , custId , kycNowDataMap);
	// }
	//
	// updateCustMast("TBCRM_CUST_MAST" , custId , kycNowDataMap);
	// }

	/** 更新當下最新風險屬性及到期日 **/
	@SuppressWarnings({ "unchecked", "null" })
	public void updateKycNowStatus(String custId, List<Map<String, Object>> questionList, String riskLevel, Timestamp expiryDate) throws JBranchException {
		StringBuffer queryStr = new StringBuffer();
		Set<String> allkey = null;
		int nowIdx = 1;

		Map<String, Object> kycNowDataMap = new HashMap<String, Object>();

		kycNowDataMap.put("CUST_ID", custId);// 風險屬性
		kycNowDataMap.put("CUST_RISK_ATR", null);// 風險屬性
		kycNowDataMap.put("KYC_DUE_DATE", null);// 到期日
		kycNowDataMap.put("ANNUAL_INCOME_AMT", null);// 到期日
		kycNowDataMap.put("STOP_LOSS_PT", new BigDecimal(0));// 停損點
		kycNowDataMap.put("TAKE_PRFT_PT", new BigDecimal(0));// 停利點

		List<Map<String, Object>> kycNowDatas = exeQueryWithoutSortForQcf(genDefaultQueryConditionIF().setQueryString(" select CUST_ID from TBKYC_INVESTOREXAM_NOW WHERE cust_id =:custId ").setObject("custId", custId));

		if (riskLevel != null)
			kycNowDataMap.put("CUST_RISK_ATR", riskLevel);// 風險屬性

		if (expiryDate != null)
			kycNowDataMap.put("KYC_DUE_DATE", expiryDate);// 到期日

		kycNowDataMap.putAll(doGetToUpdateCustMastData(custId, questionList));

		if (MapUtils.isEmpty(kycNowDataMap))
			return;

		// 尚未有資料，要新增
		if (CollectionUtils.isEmpty(kycNowDatas)) {
			QueryConditionIF queryCondition = genDefaultQueryConditionIF();
			StringBuffer values = new StringBuffer();
			queryStr.append(" insert into TBKYC_INVESTOREXAM_NOW (");

			for (String key : allkey = kycNowDataMap.keySet()) {
				queryStr.append(key).append(nowIdx < allkey.size() ? " , " : "");
				values.append(" :").append(key).append(nowIdx < allkey.size() ? " , " : "");
				queryCondition.setObject(key, kycNowDataMap.get(key));
				nowIdx++;
			}
			queryStr.append(" ) VALUES(").append(values).append(" ) ");
			queryCondition.setQueryString(queryStr.toString());

			exeUpdateForQcf(queryCondition);
		}
		// 有資料要更新
		else {
			updateCustMast("TBKYC_INVESTOREXAM_NOW", custId, kycNowDataMap);
		}

		updateCustMast("TBCRM_CUST_MAST", custId, kycNowDataMap);
	}

	public void updateCustMast(String tableName, String custId, Map<String, Object> kycNowDataMap) throws DAOException, JBranchException {
		if (StringUtils.isEmpty(custId) || MapUtils.isEmpty(kycNowDataMap))
			return;

		QueryConditionIF queryCondition = getDataAccessManager().getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer queryStr = new StringBuffer();
		Set<String> allkey = null;
		int nowIdx = 1;

		queryStr.append(" update ").append(tableName).append(" set ");
		allkey = kycNowDataMap.keySet();

		for (String key : allkey) {
			queryStr.append(key).append(" = :").append(key).append(nowIdx < allkey.size() ? " , " : "");
			queryCondition.setObject(key, kycNowDataMap.get(key));
			nowIdx++;
		}
		queryStr.append(" where CUST_ID = :custId");
		queryCondition.setObject("custId", custId);

		exeUpdateForQcf(queryCondition.setQueryString(queryStr.toString()));
	}

	// /**上送390電文**/
	// @SuppressWarnings("unchecked")
	// public Map<String, Object> sendCustDataTo390(KYC311InputVO inputVO ,
	// IPrimitiveMap<Object> header) throws Exception{
	// Map<String, Object> errorMsg = new HashMap<String, Object>();
	// boolean isEmeilAddr = false;//是否為更新email錯誤
	// boolean isTel = false;//是否更新電話錯誤
	//
	// //(10)更新電話、email
	// FC032153DataVO fc032153datavo = new FC032153DataVO();
	// fc032153datavo.setCustID(inputVO.getCUST_ID().toUpperCase());
	//
	// //自然人更新電話、EMAIL
	// if(inputVO.getCUST_ID().length() >= 10){
	// fc032153datavo.setCUST_ADDR_1(inputVO.getCUST_ADDR_1());
	// fc032153datavo.setEMAIL_ADDR(inputVO.getEMAIL_ADDR());
	//
	// List<Map<String, Object>> beforePersional = (List<Map<String, Object>>)
	// inputVO.getBasic_information().get("before_persional");
	// fc032153datavo.setBefData(beforePersional);
	//
	// //E-mail及地址更新。
	// try{
	// sot701.sendFC032153Data(fc032153datavo, header);
	// }catch(Exception e){
	// e.printStackTrace();
	// isEmeilAddr = true;
	// }
	//
	// //電話更新
	// try{
	// Tel_process(beforePersional, inputVO.getBasic_information(),header);
	// }catch(Exception e){
	// e.printStackTrace();
	// isTel = true;
	// }
	// }
	//
	// //(11)電文上送(上送C值電文修改資料)
	// fc032153datavo = new FC032153DataVO();
	// fc032153datavo.setCustID(inputVO.getCUST_ID().toUpperCase());
	// fc032153datavo.setCUST_DATA(initTp032675Data(inputVO , header));
	// sot701.checkKycMiddleWare(fc032153datavo, header);//執行TP032675更新
	//
	// if(isEmeilAddr)
	// errorMsg.put("success", "ehl_02_KYC310_017");
	// else if(isTel)
	// errorMsg.put("success", "ehl_02_KYC310_018");
	// else if(isEmeilAddr && isTel)
	// errorMsg.put("success", "ehl_02_KYC310_019");
	//
	// return errorMsg;
	// }
	//
	// @SuppressWarnings("unused")
	// public void Tel_process(List<Map<String, Object>> beforData,Map<String,
	// Object> afterData, IPrimitiveMap<Object> header) throws JBranchException{
	// String cust_id = afterData.get("CUST_ID").toString();
	// String codeBef_D = "";
	// String codeAft_D = "";
	// String telBef_D = "";
	// String telAft_D = "";
	// String codeBef_N = "";
	// String codeAft_N = "";
	// String telBef_N = "";
	// String telAft_N = "";
	// String PhoneBef = "";
	// String PhoneAft = "";
	// String FAXBef = "";
	// String FAXAft = "";
	//
	// if(beforData.get(0).get("DAYCOD") != null){
	// codeBef_D = beforData.get(0).get("DAYCOD").toString();
	// }
	// if(afterData.get("DAY_COD") != null){
	// codeAft_D = afterData.get("DAY_COD").toString();
	// }
	// if(beforData.get(0).get("DAY") != null){
	// telBef_D = beforData.get(0).get("DAY").toString();
	// }
	// if(afterData.get("DAY") != null){
	// telAft_D = afterData.get("DAY").toString();
	// if("null".equals(telAft_D)){
	// telAft_D = "";
	// }
	// }
	// if(beforData.get(0).get("NIGHTCOD") != null){
	// codeBef_N = beforData.get(0).get("NIGHTCOD").toString();
	// }
	// if(afterData.get("NIGHT_COD") != null){
	// codeAft_N = afterData.get("NIGHT_COD").toString();
	// }
	// if(beforData.get(0).get("NIGHT") != null){
	// telBef_N = beforData.get(0).get("NIGHT").toString();
	// }
	// if(afterData.get("NIGHT") != null){
	// telAft_N = afterData.get("NIGHT").toString();
	// if("null".equals(telAft_N)){
	// telAft_N = "";
	// }
	// }
	// if(beforData.get(0).get("TEL_NO") != null){
	// PhoneBef = beforData.get(0).get("TEL_NO").toString();
	// }
	// if(afterData.get("TEL_NO") != null){
	// PhoneAft = afterData.get("TEL_NO").toString();
	// if("null".equals(PhoneAft)){
	// PhoneAft = "";
	// }
	// }
	// if(beforData.get(0).get("FAX") != null){
	// FAXBef = beforData.get(0).get("FAX").toString();
	// }
	// if(afterData.get("FAX") != null){
	// FAXAft = afterData.get("FAX").toString();
	// if("null".equals(FAXAft)){
	// FAXAft = "";
	// }
	// }
	//
	// //日間&夜間都沒有CODE
	// if(StringUtils.isBlank(codeBef_D) && StringUtils.isBlank(codeBef_N)){
	// //修改後日間電話有值 & 夜間電話為空
	// if(StringUtils.isNotBlank(telAft_D) && StringUtils.isBlank(telAft_N)){
	// getPhoneDetailData(cust_id, "0001", telAft_D, 2, "1",header);
	// }else if(StringUtils.isBlank(telAft_D) &&
	// StringUtils.isNotBlank(telAft_N)){//修改後日間電話為空 & 夜間電話有值
	// getPhoneDetailData(cust_id, "0001", telAft_N, 3, "1",header);
	// }else if(StringUtils.isNotBlank(telAft_D) &&
	// StringUtils.isNotBlank(telAft_N)){////修改後日間電話有值 & 夜間電話有值
	// getPhoneDetailData(cust_id, "0001", telAft_D, 2, "1",header);
	// getPhoneDetailData(cust_id, "0002", telAft_N, 3, "1",header);
	// }
	// }
	//
	// if(StringUtils.isNotBlank(codeBef_D) && StringUtils.isBlank(codeBef_N)){
	// String newCode = NewCod(codeBef_D, codeBef_D);
	// if(!telBef_D.equals(telAft_D)){//日間電話變更
	// if("".equals(telAft_D)){
	// //刪除
	// // if(!"0001".equals(codeBef_D)){
	// getPhoneDetailData(cust_id, codeBef_D,telAft_D , 2, "3",header);
	// // }
	// }else{
	// //修改
	// getPhoneDetailData(cust_id, codeBef_D, telAft_D, 2, "2",header);
	// }
	// }
	// if(StringUtils.isNotBlank(telAft_N)){//夜間電話新增
	// getPhoneDetailData(cust_id, newCode, telAft_N, 3, "1",header);
	// }
	// }
	//
	// if(StringUtils.isBlank(codeBef_D) && StringUtils.isNotBlank(codeBef_N)){
	// String newCode = NewCod(codeBef_N, codeBef_N);
	//
	// if(!telBef_N.equals(telAft_N)){//夜間電話變更
	// if("".equals(telAft_N)){
	// //刪除
	// // if(!"0001".equals(codeBef_N)){
	// getPhoneDetailData(cust_id, codeBef_N, telAft_N, 3, "3",header);
	// // }
	// }else{
	// //修改
	// getPhoneDetailData(cust_id, codeBef_N, telAft_N, 3, "2",header);
	// }
	// }
	// if(StringUtils.isNotBlank(telAft_D)){//日間電話新增
	// getPhoneDetailData(cust_id, newCode, telAft_N, 2, "1",header);
	// }
	// }
	//
	// if(StringUtils.isNotBlank(codeBef_D) &&
	// StringUtils.isNotBlank(codeBef_N)){
	// String newCode = NewCod(codeBef_D, codeBef_N);
	//
	// if(codeBef_D.equals(codeBef_N)){//日夜間電話電話代碼相同
	// if(!telBef_D.equals(telAft_D) && !telBef_N.equals(telAft_N)){//日&夜間電話變更
	// if("".equals(telAft_D) && "".equals(telAft_N)){//刪除日&夜間電話
	// getPhoneDetailData(cust_id, codeBef_D, telAft_D, 2, "3",header);
	// }else if("".equals(telAft_D)){//刪除日間電話&修改(新增)夜間電話
	// getPhoneDetailData(cust_id, codeBef_D, telAft_D, 2, "3",header);
	// getPhoneDetailData(cust_id, codeBef_N, telAft_N, 3, "1",header);
	// }else if("".equals(telAft_N)){//刪除夜間電話&修改(新增)日間電話
	// getPhoneDetailData(cust_id, codeBef_N, telAft_N, 3, "3",header);
	// getPhoneDetailData(cust_id, codeBef_D, telAft_D, 2, "1",header);
	// }else if(!"".equals(telAft_D) && !"".equals(telAft_N)){//修改日&夜間電話
	// getPhoneDetailData(cust_id, codeBef_D, telAft_D, 2, "2",header);
	// getPhoneDetailData(cust_id, newCode, telAft_N, 3, "1",header);
	// }
	// }else if(!telBef_D.equals(telAft_D)){//修改日間電話(須新增一組電話代碼拆分日夜間電話)
	// if("".equals(telAft_D)){
	// getPhoneDetailData(cust_id, codeBef_D, telAft_D, 2, "3",header);
	// getPhoneDetailData(cust_id, newCode, telAft_N, 3, "1",header);
	// }else{
	// getPhoneDetailData(cust_id, codeBef_D, telAft_D, 2, "2",header);
	// getPhoneDetailData(cust_id, newCode, telAft_N, 3, "1",header);
	// }
	// }else if(!telBef_N.equals(telAft_N)){//修改夜間電話(須新增一組電話代碼拆分日夜間電話)
	// if("".equals(telAft_N)){
	// getPhoneDetailData(cust_id, codeBef_N, telAft_N, 2, "3",header);
	// getPhoneDetailData(cust_id, newCode, telAft_D, 2, "1",header);
	// }else{
	// getPhoneDetailData(cust_id, codeBef_N, telAft_N, 3, "2",header);
	// getPhoneDetailData(cust_id, newCode, telAft_D, 2, "1",header);
	// }
	// }else{
	// //未變更不做更新
	// }
	// }else{//日夜間電話電話代碼不同
	// if(!telBef_D.equals(telAft_D) && !telBef_N.equals(telAft_N)){//日&夜間電話變更
	// if("".equals(telAft_D) && "".equals(telAft_N)){//刪除日&夜間電話
	// getPhoneDetailData(cust_id, codeBef_D, telAft_D, 2, "3",header);
	// getPhoneDetailData(cust_id, codeBef_N, telAft_N, 3, "3",header);
	// }else if("".equals(telAft_D)){//刪除日間電話&修改夜間電話
	// getPhoneDetailData(cust_id, codeBef_D, telAft_D, 2, "3",header);
	// getPhoneDetailData(cust_id, codeBef_N, telAft_N, 3, "2",header);
	// }else if("".equals(telAft_N)){//刪除夜間電話&修改日間電話
	// getPhoneDetailData(cust_id, codeBef_N, telAft_N, 3, "3",header);
	// getPhoneDetailData(cust_id, codeBef_D, telAft_D, 2, "2",header);
	// }else if(!"".equals(telAft_D) && !"".equals(telAft_N)){//修改日&夜間電話
	// getPhoneDetailData(cust_id, codeBef_D, telAft_D, 2, "2",header);
	// getPhoneDetailData(cust_id, codeBef_N, telAft_N, 3, "2",header);
	// }
	// }else if(!telBef_D.equals(telAft_D)){//修改日間電話
	// if("".equals(telAft_D)){
	// getPhoneDetailData(cust_id, codeBef_D, telAft_D, 2, "3",header);
	// }else{
	// getPhoneDetailData(cust_id, codeBef_D, telAft_D, 2, "2",header);
	// }
	// }else if(!telBef_N.equals(telAft_N)){//修改夜間電話
	// if("".equals(telAft_N)){
	// getPhoneDetailData(cust_id, codeBef_N, telAft_N, 3, "3",header);
	// }else{
	// getPhoneDetailData(cust_id, codeBef_N, telAft_N, 3, "2",header);
	// }
	// }else{
	// //未變更不做更新
	// }
	// }
	// }
	//
	// //手機
	// if(!PhoneBef.equals(PhoneAft)){
	// String func = "";
	// int type = -1;
	// String code = "8001";
	// //func3=刪除，2=修改，1=新增
	// if("8001".equals(beforData.get(0).get("TEL_COD"))){
	// func = "2";
	// }else{
	// func = "1";
	// }
	// type = 4;
	// getPhoneDetailData(cust_id, code, PhoneAft, type, func,header);
	// }
	//
	// //傳真
	// if(!FAXBef.equals(FAXAft)){
	// String func = "";
	// int type = -1;
	// String code = "9001";
	// //func3=刪除，2=修改，1=新增
	// if("9001".equals(beforData.get(0).get("FAX_COD").toString())){
	// func = "2";
	// }else{
	// func = "1";
	// }
	// type = 5;
	// getPhoneDetailData(cust_id, code, FAXAft, type, func,header);
	// }
	// //end
	// }

	/** 上送390電文 **/
	@SuppressWarnings("unchecked")
	public Map<String, Object> sendCustDataTo390(KYC311InputVO inputVO, IPrimitiveMap<Object> header) throws Exception {
		Map<String, Object> errorMsg = new HashMap<String, Object>();
		boolean isEmeilAddr = false;// 是否為更新email錯誤
		boolean isTel = false;// 是否更新電話錯誤
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		SimpleDateFormat ft = new SimpleDateFormat("ddMMyyyy");

		//#0695 法人也能更新EMAIL了
		try {
			if(!StringUtils.equals(inputVO.getCUST_EMAIL_BEFORE(),inputVO.getEMAIL_ADDR())){
				// 更新email
				sot701.kycUpdateEmail(inputVO);
			}
		} catch (Exception e) {
			e.printStackTrace();
			isEmeilAddr = true;
		}
		// 自然人更新電話、EMAIL
		if (cbsService.isNaturalPerson(cbsService.getCBSIDCode(inputVO.getCUST_ID()))) {
			try {
				// 更新地址
				sot701.kycUpdateAddress(inputVO);
			} catch (Exception e) {
				e.printStackTrace();
				isEmeilAddr = true;

			}

			try {
				// 更新電話
				sot701.kycUpdatePhone(inputVO);
			} catch (Exception e) {
				e.printStackTrace();
				isTel = true;
			}
		}

		// 更新基本資料
		sot701.kycUpdateBasic(inputVO);

		// 更新C值
		Date expiryDate = sdf.parse(inputVO.getEXPIRY_DATE());
		inputVO.setEXPIRY_DATE(ft.format(expiryDate));		//將yyyyMMdd轉為ddMMyyyy
//		inputVO.setKYC_TEST_DATE(ft.format(new Date()));	//將yyyyMMdd轉為ddMMyyyy
		inputVO.setKYC_TEST_DATE(ft.format(new SimpleDateFormat("yyyyMMddHHmmss").parse(cbsService.getCBSTestDate())));	//#1344 對應各種測試需求要採用模擬測試日期
		sot701.kycUpdateCValue(inputVO);

		// 更新主管已覆核
		sot701.kycUpdateSupervisorCheck(inputVO);

		if (isEmeilAddr && isTel)
			errorMsg.put("success", "ehl_02_KYC310_019");
		else if (isTel)
			errorMsg.put("success", "ehl_02_KYC310_018");
		else if (isEmeilAddr)
			errorMsg.put("success", "ehl_02_KYC310_017");
		return errorMsg;
	}

	public void updatePhoneData(String custIdUpper, Map<String, Object> beforData, Map<String, Object> afterData, IPrimitiveMap<Object> header) throws Exception {
		FC032154InputVO fc032154inputVO = new FC032154InputVO();
		fc032154inputVO.setCUST_NO(ObjectUtils.toString(custIdUpper).toUpperCase());
		List<String> allCods = sot701.doGetFc032154Cods(fc032154inputVO);
		List<String> cods = new ArrayList();

		for (String allCod : allCods) {
			if (StringUtils.isBlank(allCod)) {
				continue;
			}

			if (allCod.compareTo("8001") < 0) {
				cods.add(allCod);
			}
		}

		// 是否勾選日間電話
		String Day_Type = (String) afterData.get("DAY_TYPE");

		// 日間電話代碼
		String codeBefDay = initCode(beforData.get("DAYCOD"));
		String codeAftDay = initCode(afterData.get("DAY_COD"));
		codeAftDay = StringUtils.isBlank(codeAftDay) ? initCode(afterData.get("DAY_COD")) : codeAftDay;

		// 日間電話更改前後
		String telBefDay = initCode(beforData.get("DAY")).replaceAll("[^\\d]+$", "");
		String telAftDay = initCode(afterData.get("DAY")).replaceAll("[^\\d]+$", "");

		// 是否勾選夜間電話
		String Night_Type = (String) afterData.get("NIGHT_TYPE");

		// 夜間電話代碼更改前後
		String codeBefNight = initCode(beforData.get("NIGHTCOD"));
		String codeAftNight = initCode(afterData.get("NIGHTCOD"));
		codeAftNight = StringUtils.isBlank(codeAftNight) ? initCode(afterData.get("NIGHT_COD")) : codeAftNight;

		// 夜間電話更改前後
		String telBefNight = initCode(beforData.get("NIGHT")).replaceAll("[^\\d]+$", "");
		String telAftNight = initCode(afterData.get("NIGHT")).replaceAll("[^\\d]+$", "");

		// 是否勾選行動電話
		String Phone_Type = (String) afterData.get("TEL_NO_TYPE");

		// 行動電話代碼更改前後
		String phoneCodBef = initCode(beforData.get("TELNO_COD"));
		String phoneCodAft = initCode(afterData.get("TELNO_COD"));

		// 行動電話更改前後
		String phoneBef = initCode(beforData.get("TEL_NO")).replaceAll("[^\\d]+$", "");
		String phoneAft = initCode(afterData.get("TEL_NO")).replaceAll("[^\\d]+$", "");

		for (String telCod : new String[] { codeBefDay, codeAftDay, codeBefNight, codeAftNight }) {
			boolean isAdd = true;

			if (StringUtils.isBlank(telCod)) {
				continue;
			}

			if (StringUtils.isNotBlank(telCod)) {
				for (String tmpCod : cods) {
					if (tmpCod.equals(telCod)) {
						isAdd = false;
						continue;
					}
				}
			}

			if (isAdd) {
				cods.add(telCod);
			}
		}

		// 是否勾選傳真
		String Fax_Type = (String) afterData.get("FAX_TYPE");

		// 傳真代碼更改前後
		String faxCodBef = (String) beforData.get("FAX_COD");
		String faxCodAft = (String) afterData.get("FAX_COD");

		// 傳真更改前後
		String faxBef = initCode(beforData.get("FAX")).replaceAll("[^\\d]+$", "");
		String faxAft = initCode(afterData.get("FAX")).replaceAll("[^\\d]+$", "");

		// 取最大code
		String newCod = "";

		// 日間電話有勾選
		if ("1".equals(Day_Type)) {
			// 如果日間有異動
			if (!telBefDay.equals(telAftDay)) {
				// 新增
				if (StringUtils.isBlank(telBefDay)) {
					// 取最新code，日夜前後最大code + 1
					getPhoneDetailData(custIdUpper, newCod = doGetBigNewCod(cods), telAftDay, 2, "1", header);
					cods.add(newCod);
				}
				// 刪除
				else if (StringUtils.isBlank(telAftDay)) {
					if (!"0001".equals(codeBefDay)) {
						getPhoneDetailData(custIdUpper, codeBefDay, telBefDay, 2, "3", header);
					}
				}
				// 更新
				else {
					getPhoneDetailData(custIdUpper, codeBefDay, telAftDay, 2, "2", header);
				}
			}
		}

		// 夜間電話有勾選
		if ("1".equals(Night_Type)) {
			// 如果夜間有異動
			if (!telBefNight.equals(telAftNight)) {
				// 新增
				if (StringUtils.isBlank(telBefNight)) {
					// 取最新code，日夜前後及日間新增code最大code + 1
					getPhoneDetailData(custIdUpper, newCod = doGetBigNewCod(cods), telAftNight, 3, "1", header);
				}
				// //刪除
				// else if(StringUtils.isBlank(telAftNight)){
				// if(!"0001".equals(codeBefNight)){
				// getPhoneDetailData(custIdUpper , codeBefNight , telBefNight ,
				// 3 , "3" , header);
				// }
				// }
				// 更新
				// else{
				else if (StringUtils.isBlank(telAftNight)) {
					getPhoneDetailData(custIdUpper, codeBefNight, telAftNight, 3, "2", header);
				}
			}
		}

		// 行動電話有勾選
		if ("1".equals(Phone_Type)) {
			// 手機新增
			if (StringUtils.isBlank(phoneBef) && StringUtils.isNotBlank(phoneAft)) {
				newCod = doGetBigNewCod("8001", phoneCodBef, phoneCodAft);
				getPhoneDetailData(custIdUpper, newCod, phoneAft, 4, "1", header);
			}
			// //手機刪除
			// else if(StringUtils.isNotBlank(phoneBef) &&
			// StringUtils.isBlank(phoneAft)){
			// getPhoneDetailData(custIdUpper , phoneCodBef , phoneBef , 4 , "3"
			// , header);
			// }
			// 手機更新
			else if ((StringUtils.isNotBlank(phoneBef) && StringUtils.isNotBlank(phoneAft) && !phoneBef.equals(phoneAft)) || (StringUtils.isNotBlank(phoneBef) && StringUtils.isBlank(phoneAft))) {
				getPhoneDetailData(custIdUpper, phoneCodBef, phoneAft, 4, "2", header);
			}
		}

		// 傳真有勾選
		if ("1".equals(Fax_Type)) {
			// 傳真新增
			if (StringUtils.isBlank(faxBef) && StringUtils.isNotBlank(faxAft)) {
				getPhoneDetailData(custIdUpper, "9001", faxAft, 5, "1", header);
			}
			// //傳真刪除
			// else if(StringUtils.isNotBlank(faxBef) &&
			// StringUtils.isBlank(faxAft)){
			// getPhoneDetailData(custIdUpper , "9001" , faxBef , 5 , "3" ,
			// header);
			// }
			// 傳真更新
			else if ((StringUtils.isNotBlank(faxBef) && StringUtils.isNotBlank(faxAft) && !faxBef.equals(faxAft)) || (StringUtils.isNotBlank(faxBef) && StringUtils.isBlank(faxAft))) {
				getPhoneDetailData(custIdUpper, "9001", faxAft, 5, "2", header);
			}
		}
	}

	// public void getPhoneDetailData(String custId, String code, String phone,
	// int type, String func, IPrimitiveMap<Object> header) throws APException{
	// HashMap map = new HashMap();
	// String[] data = null;
	// if(type==2){
	// data = new String[]{code,phone,"1", "", "", "", func};
	// map.put(code+phone, data);
	// }else if(type==3){
	// data = new String[]{code, phone, "","1","","",func};
	// map.put(code+phone, data);
	// }else if(type==4){
	// data = new String[]{code, phone,"","","1","",func};
	// map.put(code+phone, data);
	// }else if(type==5){
	// data = new String[]{code,phone,"","","","1",func};
	// map.put(code+phone, data);
	// }
	//
	// //電文上送
	// try {
	// FC032153DataVO fc032153datavo = new FC032153DataVO();
	// fc032153datavo.setCustID(custId);
	// fc032153datavo.setPhoneData(map);
	// sot701.sendFC032154Data(fc032153datavo, header);
	// } catch (Exception e) {
	// throw new APException(e.getMessage());
	// }
	// }

	private void getPhoneDetailData(String custId, String code, String phone, int type, String func, IPrimitiveMap<Object> header) throws Exception {
		HashMap map = new HashMap();
		String[] data = null;

		if (type == 2) {
			data = new String[] { code, phone, "1", "", "", "", func };
			map.put(code + phone, data);
		} else if (type == 3) {
			data = new String[] { code, phone, "", "1", "", "", func };
			map.put(code + phone, data);
		} else if (type == 4) {
			data = new String[] { code, phone, "", "", "1", "", func };
			map.put(code + phone, data);
		} else if (type == 5) {
			data = new String[] { code, phone, "", "", "", "1", func };
			map.put(code + phone, data);
		}

		try {
			FC032153DataVO fc032153datavo = new FC032153DataVO();
			fc032153datavo.setCustID(custId);
			fc032153datavo.setPhoneData(map);
			sot701.sendFC032154Data(fc032153datavo, header);
		} catch (Exception e) {
			throw new APException(e.getMessage());
		}

	}

	// public static void main(String...args){
	// System.out.println(doGetBigNewCod("0002" , "0802" , "0902"));
	// }

	public static String doGetBigNewCod(List<String> cods) {
		BigDecimal tempCodNum = null;

		for (String cod : cods) {
			tempCodNum = tempCodNum == null ? new BigDecimal(StringUtils.isNotBlank(cod) ? cod : "1") : tempCodNum;
			BigDecimal codNum = new BigDecimal(StringUtils.isNotBlank(cod) ? cod : "1");

			tempCodNum = tempCodNum.intValue() > codNum.intValue() ? tempCodNum : codNum;
		}

		int cod = tempCodNum.intValue();
		return String.format("%04d", cod + 1);
	}

	public static String doGetBigNewCod(String... cods) {
		BigDecimal tempCodNum = null;

		for (String cod : cods) {
			tempCodNum = tempCodNum == null ? new BigDecimal(StringUtils.isNotBlank(cod) ? cod : "1") : tempCodNum;
			BigDecimal codNum = new BigDecimal(StringUtils.isNotBlank(cod) ? cod : "1");

			tempCodNum = tempCodNum.intValue() > codNum.intValue() ? tempCodNum : codNum;
		}

		int cod = tempCodNum.intValue();
		return String.format("%04d", cod + 1);
	}

	public static String doGetNewCode(String cod) {
		BigDecimal codNum = new BigDecimal(cod);
		return String.format("%04d", codNum.intValue() + 1);
	}

	public static String initCode(Object codeVal) {
		String code = ObjectUtils.toString(codeVal);
		return code.matches("null") ? "" : code;
	}

	private String newCod(String newCod, List<String> codes) {
		try {
			int idx = 0;

			if ((idx = codes.indexOf(newCod)) != -1) {
				return fillStr(String.valueOf(Integer.parseInt(codes.get(idx)) + 1), 4, "0", false);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return newCod;
	}

	public static String fillStr(String value, int len, String filler, boolean alignLeft) {
		StringBuffer sbr = new StringBuffer();

		for (int i = value.length(); i < len; i++) {
			sbr.append(filler);
		}

		return alignLeft ? value + sbr.toString() : sbr.toString() + value;
	}

	/** 更新客戶主檔 **/
	@SuppressWarnings("unchecked")
	public Map<String, Object> doGetToUpdateCustMastData(String custId, List<Map<String, Object>> questionList) throws DAOException, JBranchException {
		Map<String, Object> resultMap = new HashMap<String, Object>();

		// 處理要埋入客戶主檔資訊
		for (Map<String, Object> map : questionList) {
			List<Map<String, Object>> ansList = null;

			if (map.get("DEFINITION") == null) {
				continue;
			}

			ansList = (List<Map<String, Object>>) map.get("ANSWER_LIST");

			for (Map<String, Object> ansmap : ansList) {
				// 排除沒勾選的選項
				if (!"true".equals(ObjectUtils.toString(ansmap.get("select"))) || ansmap.get("ANSWER_DESC") == null)
					continue;

				if ("KYC01".equals(map.get("DEFINITION"))) {
					resultMap.put("ANNUAL_INCOME_AMT", ansmap.get("ANSWER_DESC").toString().replaceFirst("\\(\\d+\\)", ""));
					break;
				}
				// 停損停利
				else if ("KYC02".equals(map.get("DEFINITION"))) {
					// substring從3開始是避免抓到題號
					String answer = ObjectUtils.toString(ansmap.get("ANSWER_DESC"));
					String regEx = "[^0-9]";// 非數字0-9
					String first = answer.substring(3, answer.indexOf("，"));// 從題號後到第一個，
					Pattern pattern = Pattern.compile(regEx);// 定義表達式
					Matcher matchar = pattern.matcher(first);// 排掉非數字mapping物件
					String loss = answer.substring(answer.indexOf("，") - 1, answer.length());// 從第一個問號開始到最後
					Matcher lm = pattern.matcher(loss);// 排掉非數字mapping物件

					resultMap.put("STOP_LOSS_PT", new BigDecimal("-" + lm.replaceAll("").trim()));// 停損點
					resultMap.put("TAKE_PRFT_PT", new BigDecimal(matchar.replaceAll("").trim()));// 停利點
					break;
				}
			}
		}

		return resultMap;
	}

	public static String NewCod(String codeBef_D, String codeBef_N) {
		String newCode = "";
		int codeBef_N_int = Integer.parseInt(codeBef_N);
		int codeBef_D_int = Integer.parseInt(codeBef_D);
		if (codeBef_D.equals(codeBef_N)) {
			String newcode_temp = String.valueOf(Integer.parseInt(codeBef_D) + 1);
			if (newcode_temp.length() < 2) {
				newCode = "000" + newcode_temp;
			} else {
				newCode = "00" + newcode_temp;
			}
		} else if (codeBef_D_int > codeBef_N_int) {
			String newcode_temp = String.valueOf(codeBef_D_int + 1);
			if (newcode_temp.length() < 2) {
				newCode = "000" + newcode_temp;
			} else {
				newCode = "00" + newcode_temp;
			}
		} else if (codeBef_D_int < codeBef_N_int) {
			String newcode_temp = String.valueOf(codeBef_N_int + 1);
			if (newcode_temp.length() < 2) {
				newCode = "000" + newcode_temp;
			} else {
				newCode = "00" + newcode_temp;
			}
		}

		return newCode;
	}

	// 檢核by儲存
	public void chkAuth(Object body, IPrimitiveMap<Object> header) throws JBranchException {
		KYC311InputVO inputVO = (KYC311InputVO) body;
		KYC311OutputVO outputVO = new KYC311OutputVO();
//		String COM_Experience = getCOM_Experience(inputVO.getCUST_ID().toUpperCase());
//
//		if("5".equals(COM_Experience)){
//			if(inputVO.getQuestionList().size()>0){
//				Derivative(inputVO.getQuestionList(), COM_Experience, outputVO);
//			}
//		}

		sendRtnObject(outputVO);
	}

	/*
	 * 前次與目前承做的基本資料及Q8、Q9題目與答案比對
	 * 20220316 #0881 基本資料中的before data 來源從電文改成TBKYC_INVESTOREXAM_D
	 */
	public void getLastResult(Object body, IPrimitiveMap<Object> header) throws JBranchException {
		KYC311InputVO inputVO = (KYC311InputVO) body;
		KYC311OutputVO outputVO = new KYC311OutputVO();
		SOT701InputVO sot701inputVO = new SOT701InputVO();
		FP032675DataVO fp032675DataVO = new FP032675DataVO();
		FP032151OutputVO fp032151OutputVO = new FP032151OutputVO();
		FP032675OutputVO fp032675OutputVO = new FP032675OutputVO();
		String answer2 = null;

		try {
			List<Map<String, Object>> questList 	= inputVO.getQuestionList();
			List<Map<String, Object>> beforeData1 	= new ArrayList<Map<String,Object>>();
			Boolean isNewQues = true;	//前一次KYC測驗是否為新問卷題型(原8, 9題改為第3題)

			QueryConditionIF qc = getDataAccessManager().getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuilder sb = new StringBuilder();
			sb.append(" SELECT B.ANSWER_2, A.CRR_ORI ");
			sb.append(" FROM TBKYC_INVESTOREXAM_M_HIS A JOIN TBKYC_INVESTOREXAM_D_HIS B ");
			sb.append("　ON A.SEQ = B.SEQ WHERE A.CUST_ID = :custid AND A.STATUS = '03' and B.ANSWER_2 is not null ");
			sb.append("　ORDER BY A.CREATE_DATE DESC ");
			sb.append("　FETCH FIRST 1 ROW ONLY");
			qc.setObject("custid", inputVO.getCUST_ID().toUpperCase());
			qc.setQueryString(sb.toString());
			beforeData1 = getDataAccessManager().exeQueryWithoutSort(qc);

			if (CollectionUtils.isNotEmpty(beforeData1)) {
				answer2 = ObjectUtils.toString(beforeData1.get(0).get("ANSWER_2"));
				//以CRR_ORI這個欄位檢查前一次KYC測驗是使用新問卷題目還是舊問卷題目；舊問卷沒有這個欄位資料
				//新問卷檢查第3題，舊問卷檢查第8、9題
				isNewQues = StringUtils.isEmpty(ObjectUtils.toString(beforeData1.get(0).get("CRR_ORI"))) ? false : true;

				//為了避免新舊版本的題目答案不一致，因而比對錯誤時代表一定不是同個版本的題目
				try {
					if(answer2.replaceAll("\\,|\\;|\\s", "").matches("\\d+")) {	//排除特殊舊資料答案()
						if(!isNewQues && StringUtils.equals("03", inputVO.getQuestionList().get(0).get("QUEST_TYPE").toString())) {
							//法人上一次為舊問卷，不需檢查第3題
						} else {
							checkProdExperience(isNewQues, answer2, questList, outputVO);
						}
					}
				} catch(Exception ex){
					ex.printStackTrace();
				}
			}

			// 從TBKYC_INVESTOREXAM_D取BEFORE資料
			List<Map<String, Object>> personalIformation = new ArrayList<Map<String,Object>>();
			QueryConditionIF qc2 = getDataAccessManager().getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuilder sb2 = new StringBuilder();
            sb2.append("SELECT CUST_EDUCTION_BEFORE, CUST_CAREER_BEFORE, CUST_MARRIAGE_BEFORE, CUST_CHILDREN_BEFORE, CUST_HEALTH_BEFORE, CUST_EMAIL_BEFORE ");
            sb2.append("FROM TBKYC_INVESTOREXAM_D WHERE CUST_ID = :custid");
            qc2.setObject("custid", inputVO.getCUST_ID().toUpperCase());
			qc2.setQueryString(sb2.toString());
			personalIformation = getDataAccessManager().exeQueryWithoutSort(qc2);

			String EDUCATION = StringUtils.isBlank((String) personalIformation.get(0).get("CUST_EDUCTION_BEFORE")) ? "" : personalIformation.get(0).get("CUST_EDUCTION_BEFORE").toString(); //教育程度
			String CAREER = StringUtils.isBlank((String) personalIformation.get(0).get("CUST_CAREER_BEFORE")) ? "" : personalIformation.get(0).get("CUST_CAREER_BEFORE").toString(); //職業別
		    String MARRAGE = StringUtils.isBlank((String) personalIformation.get(0).get("CUST_MARRIAGE_BEFORE")) ? "" : personalIformation.get(0).get("CUST_MARRIAGE_BEFORE").toString(); //婚姻狀況
			String CHILD_NO = StringUtils.isBlank((String) personalIformation.get(0).get("CUST_CHILDREN_BEFORE")) ? "" : personalIformation.get(0).get("CUST_CHILDREN_BEFORE").toString(); //子女數
			String EMAIL = StringUtils.isBlank((String) personalIformation.get(0).get("CUST_EMAIL_BEFORE")) ? "" : personalIformation.get(0).get("CUST_EMAIL_BEFORE").toString(); //信箱
			String SICK_TYPE = StringUtils.isBlank((String) personalIformation.get(0).get("CUST_HEALTH_BEFORE")) ? "" : personalIformation.get(0).get("CUST_HEALTH_BEFORE").toString(); //重大傷病

			Map<String, Map<String, Object>> map2 = new HashMap<String, Map<String, Object>>();
			if (!EDUCATION.equals(inputVO.getEDUCATION())) {
				Map<String, Object> check = new HashMap<String, Object>();
				check.put("beforEDUCTION", EDUCATION);
				check.put("afterEDUCTION", inputVO.getEDUCATION());
				map2.put("EDUCTION", check);
			}
			if (!CAREER.equals(inputVO.getCAREER())) {
				Map<String, Object> check = new HashMap<String, Object>();
				check.put("beforCAREER", CAREER);
				check.put("afterCAREER", inputVO.getCAREER());
				map2.put("CAREER", check);
			}
			if (!MARRAGE.equals(inputVO.getMARRAGE())) {
				Map<String, Object> check = new HashMap<String, Object>();
				check.put("beforMARRIAGE", MARRAGE);
				check.put("afterMARRIAGE", inputVO.getMARRAGE());
				map2.put("MARRIAGE", check);
			}
			if (!CHILD_NO.equals(inputVO.getCHILD_NO())) {
				Map<String, Object> check = new HashMap<String, Object>();
				check.put("beforCHILDREN", CHILD_NO);
				check.put("afterCHILDREN", inputVO.getCHILD_NO());
				map2.put("CHILDREN", check);
			}
			if (!SICK_TYPE.equals(inputVO.getSICK_TYPE())) {
				Map<String, Object> check = new HashMap<String, Object>();
				check.put("beforHEALTH", SICK_TYPE);
				check.put("afterHEALTH", inputVO.getSICK_TYPE());
				map2.put("HEALTH", check);
			}
			//信箱資訊
			Map<String, Object> check = new HashMap<String, Object>();
			check.put("beforEMAIL", StringUtils.endsWith(null,EMAIL) ? "" : EMAIL);
			check.put("afterEMAIL", StringUtils.endsWith(null,inputVO.getEMAIL_ADDR()) ? "" : inputVO.getEMAIL_ADDR());
			map2.put("EMAIL", check);


			outputVO.setComparison(map2);
			sendRtnObject(outputVO);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}

	}

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
	public void checkProdExperience(Boolean isNewQues, String answer2 , List<Map<String, Object>> questList , KYC311OutputVO outputVO) throws JBranchException{
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

	//衍生性金融商品
//	public boolean Derivative(List<Map<String, Object>> QUESTLIST,String COM_Experience,KYC311OutputVO outputVO) throws APException{
//		Boolean found = Boolean.FALSE;	//是否有填答第3題投資年期>無經驗
//
//		for(int i = 0 ; i < QUESTLIST.size() ; i++) {
//			String qstNo = QUESTLIST.get(i).get("QST_NO").toString();
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
//			outputVO.setCheckCOM_Experience(true);
//		}
//
//		return true;
//	}

	// 檢核交易經驗
//	public String getCOM_Experience(String CUST_ID) throws JBranchException {
//		SOT701InputVO sot701InputVO = new SOT701InputVO();
//		sot701InputVO.setCustID(CUST_ID.toUpperCase());
//		String COM_Experience = null;
//		try {
//			COM_Experience = sot701.getCustComExp(sot701InputVO).trim();
//			if (StringUtils.isBlank(COM_Experience)) {
//				COM_Experience = "6";
//			} else {
//				String[] COM_ExperienceList = COM_Experience.split(",");
//				for (int a = 0; a < COM_ExperienceList.length; a++) {
//					if ("1".equals(COM_ExperienceList[a].toString()) || "2".equals(COM_ExperienceList[a].toString()) || "3".equals(COM_ExperienceList[a].toString()) || "4".equals(COM_ExperienceList[a].toString())) {
//						COM_Experience = "4";
//						break;
//					} else if ("5".equals(COM_ExperienceList[a].toString())) {
//						COM_Experience = "5";
//					}
//				}
//			}
//		} catch (Exception e) {
//			// Auto-generated catch block
//			e.printStackTrace();
//		}
//		return COM_Experience;
//	}

	/**
	 * 列印中文版表單
	 * @param body
	 * @param header
	 * @throws JBranchException
	 * @throws SQLException
	 */
	public void print(Object body, IPrimitiveMap<Object> header) throws JBranchException, SQLException {
		KYC311InputVO inputVO = (KYC311InputVO) body;
		QueryConditionIF qc = getDataAccessManager().getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();

		sb.append("SELECT REPORT_FILE FROM TBKYC_REPORT where SEQ = :SEQ");
		qc.setObject("SEQ", inputVO.getSEQ());
		qc.setQueryString(sb.toString());
		List dataList = getDataAccessManager().exeQueryWithoutSort(qc);

		if (CollectionUtils.isNotEmpty(dataList)) {
			Blob blob = (Blob) ((Map) dataList.get(0)).get("REPORT_FILE");
			int blobLength = (int) blob.length();
			byte[] reportData = blob.getBytes(1, blobLength);
			String fileName = "reports/" + UUID.randomUUID().toString();
			String url = new PdfInputOutputUtils().doWritePdfFile(reportData, fileName);

			// 若 CUST_ID().length() >= 10 為自然人表單，否則法人表單.pdf
			notifyClientViewDoc(url, "pdf");
		}

		sendRtnObject(null);
	}

	/**
	 * 列印英文版表單
	 * @param body
	 * @param header
	 * @throws JBranchException
	 * @throws SQLException
	 */
	public void print_ENG(Object body, IPrimitiveMap<Object> header) throws JBranchException, SQLException {
		KYC311InputVO inputVO = (KYC311InputVO) body;
		QueryConditionIF qc = getDataAccessManager().getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();

		sb.append("SELECT REPORT_FILE_ENG FROM TBKYC_REPORT where SEQ = :SEQ");
		qc.setObject("SEQ", inputVO.getSEQ());
		qc.setQueryString(sb.toString());
		List dataList = getDataAccessManager().exeQueryWithoutSort(qc);

		if (CollectionUtils.isNotEmpty(dataList)) {
			Blob blob = (Blob) ((Map) dataList.get(0)).get("REPORT_FILE_ENG");
			int blobLength = (int) blob.length();
			byte[] reportData = blob.getBytes(1, blobLength);

			String fileName = "reports/" + UUID.randomUUID().toString();
			String url = new PdfInputOutputUtils().doWritePdfFile(reportData, fileName);

			// 若 CUST_ID().length() >= 10 為自然人表單_英文版，否則法人表單_英文版.pdf
			notifyClientViewDoc(url, "pdf");
		}

		sendRtnObject(null);
	}

	/***
	 * 列印客戶風險屬性評估問卷差異說明表單
	 * @param body
	 * @param header
	 * @throws JBranchException
	 * @throws SQLException
	 */
	public void print_COMP(Object body, IPrimitiveMap<Object> header) throws JBranchException, SQLException {
		KYC311InputVO inputVO = (KYC311InputVO) body;
		QueryConditionIF qc = getDataAccessManager().getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();

		sb.append("SELECT REPORT_FILE_COMP FROM TBKYC_REPORT where SEQ = :SEQ");
		qc.setObject("SEQ", inputVO.getSEQ());
		qc.setQueryString(sb.toString());
		List dataList = getDataAccessManager().exeQueryWithoutSort(qc);

		if (CollectionUtils.isNotEmpty(dataList)) {
			Blob blob = (Blob) ((Map) dataList.get(0)).get("REPORT_FILE_COMP");
			int blobLength = (int) blob.length();
			byte[] reportData = blob.getBytes(1, blobLength);

			String fileName = "reports/" + UUID.randomUUID().toString();
			String url = new PdfInputOutputUtils().doWritePdfFile(reportData, fileName);

			notifyClientViewDoc(url, "pdf");
		}

		sendRtnObject(null);
	}
	
}
