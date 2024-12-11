package com.systex.jbranch.app.server.fps.iot920;


import com.systex.jbranch.app.server.fps.crm681.CRM681;
import com.systex.jbranch.app.server.fps.crm681.CRM681InputVO;
import com.systex.jbranch.app.server.fps.crm681.CRM681OutputVO;
import com.systex.jbranch.app.server.fps.sot701.CustHighNetWorthDataVO;
import com.systex.jbranch.app.server.fps.sot701.CustKYCDataVO;
import com.systex.jbranch.app.server.fps.sot701.FP032675DataVO;
import com.systex.jbranch.app.server.fps.sot701.SOT701;
import com.systex.jbranch.app.server.fps.sot701.SOT701InputVO;
import com.systex.jbranch.app.server.fps.sot714.SOT714;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.fubon.commons.ValidUtil;
import com.systex.jbranch.fubon.commons.cbs.CBSService;
import com.systex.jbranch.fubon.commons.cbs.dao._067164_067165DAO;
import com.systex.jbranch.fubon.commons.cbs.vo.basic.CBSUtilOutputVO;
import com.systex.jbranch.fubon.commons.esb.service.AML004Service;
import com.systex.jbranch.fubon.commons.esb.vo.aml004.AML004OutputVO;
import com.systex.jbranch.fubon.commons.esb.vo.eb032168.EB032168OutputVO;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * IOT920
 *
 * @author Jimmy
 * @date 2016/10/03
 * @spec null
 */

@Component("iot920")
@Scope("request")
public class IOT920 extends FubonWmsBizLogic{
	Logger logger = LoggerFactory.getLogger(this.getClass());
	DataAccessManager dam_obj;
	@Autowired
	private _067164_067165DAO _067164_067165dao;

	@Autowired
	private CBSService cbsService;

	@Autowired
	private AML004Service aml004Service;

	//送件點收通過/不通過權限檢核
	public void chkXsetStatus(Object body, IPrimitiveMap<Object> header) throws JBranchException {
		IOT920InputVO inputVO = (IOT920InputVO) body;
		IOT920OutputVO outputVO = new IOT920OutputVO();
		String LoginRoleID = (String) getCommonVariable(SystemVariableConsts.LOGINROLE);

		//若為視訊投保，送出覆核及核可時，需檢查是否影像品質檢核項目皆有通過
		if(StringUtils.equals("Y", inputVO.getMAPPVIDEO_YN())) {
			if(!validMAPPVideoCheck(inputVO)) {
				outputVO.setMessage("視訊簽單影像未完成確認請勾選，或若有項目為不通過應填寫不通過原因。");
			}
		}

		dam_obj = this.getDataAccessManager();
		QueryConditionIF qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();
		try {
			sb.append(" Select next_status from TBIOT_STATUS_FLOW ");
			sb.append(" Where curr_status = :curr_status ");
			sb.append(" And opr = :in_opr ");
			sb.append(" And NVL(role_id,  :roleID)   = :roleID ");
			sb.append(" And NVL(sign_yn,  :sign_inc) = :sign_inc ");
			sb.append(" AND NVL(INS_KIND, :ins_kind) = :ins_kind ");
			sb.append(" AND NVL(REG_TYPE, :reg_type) = :reg_type ");
			qc.setObject("curr_status", inputVO.getCurr_status());
			qc.setObject("roleID", LoginRoleID);
			qc.setObject("in_opr", inputVO.getIn_opr());
			qc.setObject("reg_type", inputVO.getREG_TYPE());
			if("1".equals(inputVO.getSIGN_INC())){
				inputVO.setSIGN_INC("Y");
			}
			if("2".equals(inputVO.getSIGN_INC())){
				inputVO.setSIGN_INC("N");
			}
			qc.setObject("sign_inc", inputVO.getSIGN_INC());
			qc.setObject("ins_kind", inputVO.getINS_KIND());
			qc.setQueryString(sb.toString());
			outputVO.setNext_statusList(dam_obj.exeQuery(qc));
			sendRtnObject(outputVO);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}

	//戶況檢核
	public List<Map<String, Object>> getCM_FLAG(String cust_id) throws APException{
		SOT701InputVO inputVO_iot701 = new SOT701InputVO();
		FP032675DataVO fp032675DataVO = new FP032675DataVO();
		inputVO_iot701.setCustID(cust_id);
		List<Map<String , Object>> COMPLAIN_YNList = new ArrayList<Map<String,Object>>();
		List<Map<String , Object>> CLOSE_YNList = new ArrayList<Map<String,Object>>();
		List<Map<String , Object>> All = new ArrayList<Map<String,Object>>();
		Map<String , Object> data = new HashMap<String, Object>();
		String CM_FLAGE = "";
		String COMPLAIN_YN = "";
		String REJECT_YN = "";
		String NO_SALE = "";
		String CLOSE_YN = "";
		String CUST_REMARKS = ""; //特定客戶
		try {
			dam_obj = this.getDataAccessManager();
			QueryConditionIF qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuilder sb = new StringBuilder();
			sb.append(" Select COMPLAIN_YN from TBCRM_CUST_NOTE Where CUST_ID = :cust_id ");
			qc.setObject("cust_id", cust_id);
			qc.setQueryString(sb.toString());
			COMPLAIN_YNList = dam_obj.exeQuery(qc);
			if(COMPLAIN_YNList.size() >0){
				if(COMPLAIN_YNList.get(0).get(COMPLAIN_YN) != null){
					COMPLAIN_YN = COMPLAIN_YNList.get(0).get(COMPLAIN_YN).toString();
				}else{
					COMPLAIN_YN = "N";
				}
			}else{
				COMPLAIN_YN = "N";
			}
			SOT701 sot701 = (SOT701) PlatformContext.getBean("sot701");

			List<CBSUtilOutputVO> _067164_067165Data = new ArrayList<>();
			try {
				_067164_067165Data.addAll(_067164_067165dao.search(cust_id, cbsService.getCBSIDCode(cust_id)));
			} catch (Exception e) { // 忽略該電文拋錯（共同行銷註記查無資料..etc）
			}
			inputVO_iot701.setData067164_067165(_067164_067165Data);

			fp032675DataVO = sot701.getFP032675Data(inputVO_iot701);
			if(!"".equals(fp032675DataVO.getRejectProdFlag())){
				REJECT_YN = fp032675DataVO.getRejectProdFlag();

			}else{
				REJECT_YN = "N";
			}
			if(!"".equals(fp032675DataVO.getNoSale())){
				NO_SALE = fp032675DataVO.getNoSale();
			}else{
				NO_SALE = "N";
			}
			
			//結清戶
			qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb = new StringBuilder();
			sb.append(" Select 1 from TBCRM_CUST_VALID Where CUST_ID = :cust_id ");
			qc.setObject("cust_id", cust_id);
			qc.setQueryString(sb.toString());
			CLOSE_YNList = dam_obj.exeQuery(qc);
			CLOSE_YN = CollectionUtils.isEmpty(CLOSE_YNList) ? "Y" : "N";

			//戶況檢核
			if("N".equals(COMPLAIN_YN) && "N".equals(REJECT_YN) && "N".equals(NO_SALE)){
				CM_FLAGE = "1";
			}
			if("Y".equals(COMPLAIN_YN) && "N".equals(REJECT_YN) && "N".equals(NO_SALE)){
				CM_FLAGE = "2";
			}
			if("N".equals(COMPLAIN_YN) && "Y".equals(REJECT_YN) && "N".equals(NO_SALE)){
				CM_FLAGE = "3";
			}
			if("N".equals(COMPLAIN_YN) && "N".equals(REJECT_YN) && "Y".equals(NO_SALE)){
				CM_FLAGE = "4";
			}
			if("Y".equals(COMPLAIN_YN) && "Y".equals(REJECT_YN) && "N".equals(NO_SALE)){
				CM_FLAGE = "5";
			}
			if("Y".equals(COMPLAIN_YN) && "N".equals(REJECT_YN) && "Y".equals(NO_SALE)){
				CM_FLAGE = "6";
			}
			if("N".equals(COMPLAIN_YN) && "Y".equals(REJECT_YN) && "Y".equals(NO_SALE)){
				CM_FLAGE = "7";
			}
			if("Y".equals(COMPLAIN_YN) && "Y".equals(REJECT_YN) && "Y".equals(NO_SALE)){
				CM_FLAGE = "8";
			}
			if("Y".equals(CLOSE_YN)) {
				CM_FLAGE = "9";
			}
			//是否為弱勢

//			if(!"".equals(fc032675DataVO.getCustRemarks())){
//				if("Y".equals(fc032675DataVO.getCustRemarks())){
			if("N".equals(fp032675DataVO.getAgeUnder70Flag())
					|| "N".equals(fp032675DataVO.getEduJrFlag())
					|| "N".equals(fp032675DataVO.getHealthFlag())   ){
				data.put("UNDER_YN", "Y");
			}else{
				data.put("UNDER_YN", "N");
//				}
			}
			////確認是否為專投
			if(!"".equals(fp032675DataVO.getCustProFlag())){
				if("Y".equals(fp032675DataVO.getCustProFlag())){
					data.put("PRO_YN", "Y");
				}else{
					data.put("PRO_YN", "N");
				}
			}
			data.put("CM_FLAGE", CM_FLAGE);
			//特定客戶
			CUST_REMARKS = StringUtils.isBlank(fp032675DataVO.getCustRemarks()) ? "N" : fp032675DataVO.getCustRemarks();
			data.put("CUST_REMARKS", CUST_REMARKS);
			
			All.add(data);

			return All;
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new APException(e.getMessage());
		}
	}

	//抓取客戶基本資料
	public void getCUSTInfo(Object body, IPrimitiveMap<Object> header) throws Exception{
		IOT920InputVO inputVO = (IOT920InputVO) body;
		IOT920OutputVO outputVO = new IOT920OutputVO();
		String loginID = (String) getCommonVariable(SystemVariableConsts.LOGINID);
		String errorMsg = "";
		dam_obj = this.getDataAccessManager();
		QueryConditionIF qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();
//		try {
			switch (inputVO.getIn_column()) {
				case "RECRUIT":
					if(inputVO.getRECRUIT_ID() != null && StringUtils.isNotBlank(inputVO.getRECRUIT_ID())){
						qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
						sb = new StringBuilder();
						sb.append(" Select emp_name from tborg_member ");
						sb.append(" Where emp_id = :recurit_id ");
						sb.append(" AND SERVICE_FLAG = 'A' ");
						sb.append(" AND CHANGE_FLAG IN('A', 'M', 'P') ");
						qc.setObject("recurit_id", inputVO.getRECRUIT_ID());
						qc.setQueryString(sb.toString());
						outputVO.setEMP_NAME(dam_obj.exeQuery(qc));
						//AOCode
						List<Map<String, Object>> AO_CODEList = new ArrayList<Map<String,Object>>();
						String AO_CODE = "";
						qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
						sb = new StringBuilder();
						sb.append(" SELECT AO_CODE FROM TBORG_SALES_AOCODE ");
						sb.append(" WHERE EMP_ID = :emp_id ");
						qc.setObject("emp_id", inputVO.getRECRUIT_ID());
						qc.setQueryString(sb.toString());
						AO_CODEList = dam_obj.exeQuery(qc);
						if(AO_CODEList.size()>0){
							AO_CODE = AO_CODEList.get(0).get("AO_CODE").toString();
						}
						if(outputVO.getEMP_NAME().size()>0){
							outputVO.getEMP_NAME().get(0).put("AO_CODE", AO_CODE);
						}
						//招攬人員是否有分紅證照
						outputVO.getEMP_NAME().get(0).put("EMP_DIVIDEND_CERT", getEmpDividendCert(inputVO.getRECRUIT_ID()));
						if(inputVO.getAPPLY_DATE() != null) {
							//招攬人員是否有公平待客完訓資格
							outputVO.getEMP_NAME().get(0).put("EMP_FAIR_CERT", getFairCert(inputVO.getRECRUIT_ID(), inputVO.getAPPLY_DATE()));
							//招攬人員是否有高齡完訓資格
							outputVO.getEMP_NAME().get(0).put("EMP_SENIOR_CERT", getSeniorCert(inputVO.getRECRUIT_ID(), inputVO.getAPPLY_DATE()));
						} else {
							outputVO.getEMP_NAME().get(0).put("EMP_FAIR_CERT", "N");
							outputVO.getEMP_NAME().get(0).put("EMP_SENIOR_CERT", "N");
						}
					}
					break;
				case "CHG_CUST":
					inputVO.setCUST_ID(inputVO.getCHG_CUST_ID());
				case "CUST":
					if(inputVO.getCUST_ID() != null && StringUtils.isNotBlank(inputVO.getCUST_ID())){
						if(ValidUtil.isValidIDorRCNumber(inputVO.getCUST_ID())){
							//取得AML & precheck
							SOT701 sot701 = (SOT701) PlatformContext.getBean("sot701");
							EB032168OutputVO eb032168OutputVO = sot701.getEB032168(inputVO.getCUST_ID().toUpperCase());
							
							List<Map<String, Object>> CM_FLAG = getCM_FLAG(inputVO.getCUST_ID().toUpperCase());
							String PROPOSER_CM_FLAG = CM_FLAG.get(0).get("CM_FLAGE").toString();
							String UNDER_YN = CM_FLAG.get(0).get("UNDER_YN").toString();
							String PRO_YN = CM_FLAG.get(0).get("PRO_YN").toString();
							String CUST_REMARKS = CM_FLAG.get(0).get("CUST_REMARKS").toString(); //特定客戶

							String C_LOAN_CHK1_YN = insLoanChk(inputVO.getCUST_ID(), inputVO.getAPPLY_DATE());
							String C_LOAN_CHK3_YN = insTerminateChk(inputVO.getCUST_ID(), inputVO.getAPPLY_DATE());
							String C_LOAN_CHK4_YN = quotaLoanChk(inputVO.getCUST_ID());
							String C_REVOLVING_LOAN_YN = revolingLoanChk(inputVO.getCUST_ID());
							String C_CD_CHK_YN = intCDChk(inputVO.getCUST_ID(), inputVO.getAPPLY_DATE());
							Date C_CD_DUE_DATE = getCDDueDate(inputVO.getCUST_ID(), inputVO.getAPPLY_DATE());

							Map<String, Object> houseLoanMap = inHouseLoanChk(inputVO.getCUST_ID(), inputVO.getAPPLY_DATE());
							String C_LOAN_CHK2_YN = (String) houseLoanMap.get("isInHouseLoan");
							Date C_LOAN_CHK2_DATE = (Date) houseLoanMap.get("inHouseLoanDate");//行內貸款最近撥貸日

							Map<String, Object> loanMap = getCustLoanDate(inputVO.getCUST_ID(), inputVO.getAPPLY_DATE());
							String C_LOAN_APPLY_YN = (String) loanMap.get("isLoanApply");
							Date C_LOAN_APPLY_DATE = (Date) loanMap.get("loanApplyDate");
							Date C_PREM_DATE = getCustPremDate(inputVO.getCUST_ID(), inputVO.getAPPLY_DATE());
							String C_KYC_INCOME = getKycQ2Answer(inputVO.getCUST_ID(), inputVO.getAPPLY_DATE());
							BigDecimal CUST_DEBIT = getFu(inputVO.getCUST_ID());
							
							Date kycDueDate = null;
							String custRisk = "";
							//查無客戶KYC資料不回傳錯誤到前端
							try {
								SOT701InputVO sot701inputVO = new SOT701InputVO();
								sot701inputVO.setCustID(inputVO.getCUST_ID());
								CustKYCDataVO custKYCDataVO = sot701.getCustKycData(sot701inputVO);
								if(custKYCDataVO.getKycDueDate() != null) {
									kycDueDate = custKYCDataVO.getKycDueDate();
									custRisk = custKYCDataVO.getKycLevel();
								}
							} catch(Exception e) {}
							
							qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
							sb = new StringBuilder();
							sb.append(" Select cust_name,birth_date,AO_CODE From TBCRM_CUST_MAST ");
							sb.append(" Where cust_id = :cust_id ");
							qc.setObject("cust_id", inputVO.getCUST_ID().toUpperCase());
							qc.setQueryString(sb.toString());
							List<Map<String, Object>> tempList = dam_obj.exeQuery(qc);
							// 2018/1/9 TBCRM_CUST_MAST change old BIRTH_DATE
							if(CollectionUtils.isNotEmpty(tempList)) {
								for(Map<String, Object> map : tempList) {
									if(map.get("BIRTH_DATE") != null) {
										map.put("BIRTHDAY", map.get("BIRTH_DATE"));
									}
									map.put("PROPOSER_CM_FLAG", PROPOSER_CM_FLAG);
									map.put("UNDER_YN", UNDER_YN);
									map.put("PRO_YN", PRO_YN);
									map.put("CUST_REMARKS", CUST_REMARKS);
									map.put("C_LOAN_CHK1_YN", C_LOAN_CHK1_YN);
									map.put("C_LOAN_CHK2_YN", C_LOAN_CHK2_YN);
									map.put("C_LOAN_CHK2_DATE", C_LOAN_CHK2_DATE);
									map.put("C_LOAN_CHK3_YN", C_LOAN_CHK3_YN);
									map.put("C_LOAN_CHK4_YN", C_LOAN_CHK4_YN);
									map.put("C_REVOLVING_LOAN_YN", C_REVOLVING_LOAN_YN);
									map.put("C_CD_CHK_YN", C_CD_CHK_YN);
									map.put("C_CD_DUE_DATE", C_CD_DUE_DATE);
									map.put("C_LOAN_APPLY_YN", C_LOAN_APPLY_YN);
									map.put("C_LOAN_APPLY_DATE", C_LOAN_APPLY_DATE);
									map.put("C_PREM_DATE", C_PREM_DATE);
									map.put("C_KYC_INCOME", C_KYC_INCOME);
									map.put("CUST_DEBIT", CUST_DEBIT);
									map.put("CUST_RISK_ATR", custRisk);
									map.put("KYC_DUE_DATE", kycDueDate);
								}
							} else {
								tempList = new ArrayList<Map<String, Object>>();
								Map<String, Object> map = new HashMap<String, Object>();
								map.put("PROPOSER_CM_FLAG", "10");	//不在客戶主檔中，非本行客戶
								map.put("UNDER_YN", UNDER_YN);
								map.put("PRO_YN", PRO_YN);
								map.put("CUST_REMARKS", CUST_REMARKS);
								map.put("C_LOAN_CHK1_YN", C_LOAN_CHK1_YN);
								map.put("C_LOAN_CHK2_YN", C_LOAN_CHK2_YN);
								map.put("C_LOAN_CHK2_DATE", C_LOAN_CHK2_DATE);
								map.put("C_LOAN_CHK3_YN", C_LOAN_CHK3_YN);
								map.put("C_LOAN_CHK4_YN", C_LOAN_CHK4_YN);
								map.put("C_REVOLVING_LOAN_YN", C_REVOLVING_LOAN_YN);
								map.put("C_CD_CHK_YN", C_CD_CHK_YN);
								map.put("C_CD_DUE_DATE", C_CD_DUE_DATE);
								map.put("C_LOAN_APPLY_YN", C_LOAN_APPLY_YN);
								map.put("C_LOAN_APPLY_DATE", C_LOAN_APPLY_DATE);
								map.put("C_PREM_DATE", C_PREM_DATE);
								map.put("C_KYC_INCOME", C_KYC_INCOME);
								map.put("CUST_DEBIT", CUST_DEBIT);
								map.put("CUST_RISK_ATR", custRisk);
								map.put("KYC_DUE_DATE", kycDueDate);

								tempList.add(map);
							}
							outputVO.setCUST_NAME(tempList);

							String defaultText = "NONE";
							if(eb032168OutputVO != null) {
								outputVO.setPRECHECK(StringUtils.defaultIfEmpty(StringUtils.trim(eb032168OutputVO.getCCD_PRE()), defaultText));
							} else {
								outputVO.setPRECHECK(defaultText);
							}
							// AML 防洗錢註記
							AML004OutputVO aml004OutputVO = aml004Service.search(inputVO.getCUST_ID().toUpperCase());
							outputVO.setAML(aml004OutputVO != null ? aml004OutputVO.getRiskRanking(): "");
							//取得保險庫存金額
							CRM681 crm681 = (CRM681) PlatformContext.getBean("crm681");
							outputVO.setINS_ASSET((crm681.inquire(inputVO.getCUST_ID())).getInsurance());
							
							outputVO.setINCOME3(getIncome3(inputVO.getCUST_ID().toUpperCase()));
							
							//取得要保人高資產註記
							CustHighNetWorthDataVO hnwcData = new CustHighNetWorthDataVO();
							try {
								SOT714 sot714 = (SOT714) PlatformContext.getBean("sot714");
								hnwcData = sot714.getHNWCData(inputVO.getCUST_ID().toUpperCase());
								outputVO.setHnwcData(hnwcData);
							} catch(Exception e) {}
						}else{
							errorMsg = "ehl_01_common_030";
							throw new APException(errorMsg);
						}
					}
					break;
				case "INSURED":
					if(inputVO.getINSURED_ID() != null && StringUtils.isNotBlank(inputVO.getINSURED_ID())){
						if(ValidUtil.isValidIDorRCNumber(inputVO.getINSURED_ID())){
							List<Map<String, Object>> CM_FLAG = getCM_FLAG(inputVO.getINSURED_ID().toUpperCase());
							String INSURED_CM_FLAG = CM_FLAG.get(0).get("CM_FLAGE").toString();
							String UNDER_YN = CM_FLAG.get(0).get("UNDER_YN").toString();
							String PRO_YN = CM_FLAG.get(0).get("PRO_YN").toString();

							String I_LOAN_CHK1_YN = insLoanChk(inputVO.getINSURED_ID(), inputVO.getAPPLY_DATE());
							String I_LOAN_CHK3_YN = insTerminateChk(inputVO.getINSURED_ID(), inputVO.getAPPLY_DATE());
							String I_LOAN_CHK4_YN = quotaLoanChk(inputVO.getINSURED_ID());
							String I_REVOLVING_LOAN_YN = revolingLoanChk(inputVO.getINSURED_ID());
							String I_CD_CHK_YN = intCDChk(inputVO.getINSURED_ID(), inputVO.getAPPLY_DATE());
							Date I_CD_DUE_DATE = getCDDueDate(inputVO.getINSURED_ID(), inputVO.getAPPLY_DATE());

							Map<String, Object> houseLoanMap = inHouseLoanChk(inputVO.getINSURED_ID(), inputVO.getAPPLY_DATE());
							String I_LOAN_CHK2_YN = (String) houseLoanMap.get("isInHouseLoan");
							Date I_LOAN_CHK2_DATE = (Date) houseLoanMap.get("inHouseLoanDate");//行內貸款最近撥貸日

							Map<String, Object> loanMap = getCustLoanDate(inputVO.getINSURED_ID(), inputVO.getAPPLY_DATE());
							String I_LOAN_APPLY_YN = (String) loanMap.get("isLoanApply");
							Date I_LOAN_APPLY_DATE = (Date) loanMap.get("loanApplyDate");
							String I_KYC_INCOME = getKycQ2Answer(inputVO.getINSURED_ID(), inputVO.getAPPLY_DATE());
							BigDecimal INSURED_DEBIT = getFu(inputVO.getINSURED_ID());

							List<Map<String, Object>> insuredList = new ArrayList<Map<String,Object>>();
							qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
							sb = new StringBuilder();
							sb.append(" Select cust_name,birth_date,gender From TBCRM_CUST_MAST ");
							sb.append(" Where cust_id = :insured_id ");
							qc.setObject("insured_id", inputVO.getINSURED_ID().toUpperCase());
							qc.setQueryString(sb.toString());
							insuredList = dam_obj.exeQuery(qc);
							// 2018/1/9 TBCRM_CUST_MAST change old BIRTH_DATE

							if(CollectionUtils.isNotEmpty(insuredList)) {
								for(Map<String, Object> map : insuredList) {
									if(map.get("BIRTH_DATE") != null) {
										map.put("BIRTHDAY", map.get("BIRTH_DATE"));
									}
									map.put("INSURED_CM_FLAG", INSURED_CM_FLAG);
									map.put("UNDER_YN", UNDER_YN);
									map.put("PRO_YN", PRO_YN);
									map.put("I_LOAN_CHK1_YN", I_LOAN_CHK1_YN);
									map.put("I_LOAN_CHK2_YN", I_LOAN_CHK2_YN);
									map.put("I_LOAN_CHK2_DATE", I_LOAN_CHK2_DATE);
									map.put("I_LOAN_CHK3_YN", I_LOAN_CHK3_YN);
									map.put("I_LOAN_CHK4_YN", I_LOAN_CHK4_YN);
									map.put("I_REVOLVING_LOAN_YN", I_REVOLVING_LOAN_YN);
									map.put("I_CD_CHK_YN", I_CD_CHK_YN);
									map.put("I_CD_DUE_DATE", I_CD_DUE_DATE);
									map.put("I_LOAN_APPLY_YN", I_LOAN_APPLY_YN);
									map.put("I_LOAN_APPLY_DATE", I_LOAN_APPLY_DATE);
									map.put("I_KYC_INCOME", I_KYC_INCOME);
									map.put("INSURED_DEBIT", INSURED_DEBIT);
								}
							} else {
								insuredList = new ArrayList<Map<String, Object>>();
								Map<String, Object> map = new HashMap<String, Object>();
								map.put("INSURED_CM_FLAG", "10");	//不在客戶主檔中，非本行客戶
								map.put("UNDER_YN", UNDER_YN);
								map.put("PRO_YN", PRO_YN);
								map.put("I_LOAN_CHK1_YN", I_LOAN_CHK1_YN);
								map.put("I_LOAN_CHK2_YN", I_LOAN_CHK2_YN);
								map.put("I_LOAN_CHK2_DATE", I_LOAN_CHK2_DATE);
								map.put("I_LOAN_CHK3_YN", I_LOAN_CHK3_YN);
								map.put("I_LOAN_CHK4_YN", I_LOAN_CHK4_YN);
								map.put("I_REVOLVING_LOAN_YN", I_REVOLVING_LOAN_YN);
								map.put("I_CD_CHK_YN", I_CD_CHK_YN);
								map.put("I_CD_DUE_DATE", I_CD_DUE_DATE);
								map.put("I_LOAN_APPLY_YN", I_LOAN_APPLY_YN);
								map.put("I_LOAN_APPLY_DATE", I_LOAN_APPLY_DATE);
								map.put("I_KYC_INCOME", I_KYC_INCOME);
								map.put("INSURED_DEBIT", INSURED_DEBIT);

								insuredList.add(map);
							}
							outputVO.setINSURED_NAME(insuredList);

							outputVO.setINCOME3(getIncome3(inputVO.getINSURED_ID().toUpperCase()));
						}else{
							errorMsg = "ehl_01_common_030";
							throw new APException(errorMsg);
						}
					}
					break;
				case "REPRESET":
					if(inputVO.getREPRESET_ID() != null && StringUtils.isNotBlank(inputVO.getREPRESET_ID())){
						if(ValidUtil.isValidIDorRCNumber(inputVO.getREPRESET_ID())){
							List<Map<String, Object>> CM_FLAG = getCM_FLAG(inputVO.getREPRESET_ID().toUpperCase());
							String REPRESET_CM_FLAG = CM_FLAG.get(0).get("CM_FLAGE").toString();
							String UNDER_YN = CM_FLAG.get(0).get("UNDER_YN").toString();
							String PRO_YN = CM_FLAG.get(0).get("PRO_YN").toString();
							List<Map<String, Object>> represetList = new ArrayList<Map<String,Object>>();
							qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
							sb = new StringBuilder();
							sb.append(" Select cust_name,AO_CODE From TBCRM_CUST_MAST ");
							sb.append(" Where cust_id = :represet_id ");
							qc.setObject("represet_id", inputVO.getREPRESET_ID().toUpperCase());
							qc.setQueryString(sb.toString());
							represetList = dam_obj.exeQuery(qc);
							if(represetList.size()>0){
								represetList.get(0).put("REPRESET_CM_FLAG", REPRESET_CM_FLAG);
								represetList.get(0).put("UNDER_YN", UNDER_YN);
								represetList.get(0).put("PRO_YN", PRO_YN);
							}
							outputVO.setREPRESETList(represetList);
						}else{
							errorMsg = "ehl_01_common_030";
							throw new APException(errorMsg);
						}
					}
					break;
				case "ADDR":
					if(!"".equals(inputVO.getCUST_ID()) || inputVO.getREPRESET_ID() != null){
						qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
						sb = new StringBuilder();
						sb.append(" SELECT COM_ADDRESS FROM TBCRM_CUST_CONTACT ");
						sb.append(" WHERE CUST_ID = NVL(:represet_id, :cust_id) ");
						qc.setObject("represet_id", ObjectUtils.toString(inputVO.getREPRESET_ID()));
						qc.setObject("cust_id", ObjectUtils.toString(inputVO.getCUST_ID()));
						qc.setQueryString(sb.toString());
						outputVO.setCOM_ADDRESS(dam_obj.exeQuery(qc));
					}
					if(inputVO.getREPRESET_ID() != null && StringUtils.isNotBlank(inputVO.getREPRESET_ID())){
						if(!"".equals(inputVO.getREPRESET_ID())){
							if(ValidUtil.isValidIDorRCNumber(inputVO.getREPRESET_ID())){
								List<Map<String, Object>> CM_FLAG = getCM_FLAG(inputVO.getREPRESET_ID().toUpperCase());
								String REPRESET_CM_FLAG = CM_FLAG.get(0).get("CM_FLAGE").toString();
								String UNDER_YN = CM_FLAG.get(0).get("UNDER_YN").toString();
								String PRO_YN = CM_FLAG.get(0).get("PRO_YN").toString();
								List<Map<String, Object>> represetList = new ArrayList<Map<String,Object>>();
								qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
								sb = new StringBuilder();
								sb.append(" Select cust_name,AO_CODE From TBCRM_CUST_MAST ");
								sb.append(" Where cust_id = :represet_id ");
								qc.setObject("represet_id", inputVO.getREPRESET_ID().toUpperCase());
								qc.setQueryString(sb.toString());
								represetList = dam_obj.exeQuery(qc);
								if(represetList.size()>0){
									represetList.get(0).put("REPRESET_CM_FLAG", REPRESET_CM_FLAG);
									represetList.get(0).put("UNDER_YN", UNDER_YN);
									represetList.get(0).put("PRO_YN", PRO_YN);
								}
								outputVO.setREPRESETList(represetList);
							}else{
								errorMsg = "ehl_01_common_030";
								throw new APException(errorMsg);
							}
						}
					}
					break;
				case "REF_CON_ID":
					if(!"".equals(inputVO.getREF_CON_ID())){
						if(inputVO.getCUST_ID() != null){
							qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
							sb = new StringBuilder();
							sb.append(" SELECT SALES_PERSON,SALES_NAME FROM TBCAM_LOAN_SALEREC ");
							sb.append(" WHERE NVL(REF_CON_ID,SEQ) = :ref_con_id AND REF_PROD = '5' ");
							sb.append(" AND CUST_ID = :cust_id ");
							qc.setObject("ref_con_id", inputVO.getREF_CON_ID());
							qc.setObject("cust_id", inputVO.getCUST_ID().toUpperCase());
							qc.setQueryString(sb.toString());
							outputVO.setREFList(dam_obj.exeQuery(qc));
						}else{
							errorMsg = "ehl_01_KYC310_004";
							throw new APException(errorMsg);
						}
					}
					break;
				case "PAYER":
					if(inputVO.getPAYER_ID() != null && StringUtils.isNotBlank(inputVO.getPAYER_ID())){
						if(ValidUtil.isValidIDorRCNumber(inputVO.getPAYER_ID())){
							List<Map<String, Object>> CM_FLAG = getCM_FLAG(inputVO.getPAYER_ID().toUpperCase());
							String PAYER_CM_FLAG = CM_FLAG.get(0).get("CM_FLAGE").toString();
							String UNDER_YN = CM_FLAG.get(0).get("UNDER_YN").toString();
							String PRO_YN = CM_FLAG.get(0).get("PRO_YN").toString();
							String LOAN_CHK1_YN = insLoanChk(inputVO.getPAYER_ID(), inputVO.getAPPLY_DATE());
							String LOAN_CHK3_YN = insTerminateChk(inputVO.getPAYER_ID(), inputVO.getAPPLY_DATE());
							String LOAN_CHK4_YN = quotaLoanChk(inputVO.getPAYER_ID());
							String P_REVOLVING_LOAN_YN = revolingLoanChk(inputVO.getPAYER_ID());
							String CD_CHK_YN = intCDChk(inputVO.getPAYER_ID(), inputVO.getAPPLY_DATE());
							Date P_CD_DUE_DATE = getCDDueDate(inputVO.getPAYER_ID(), inputVO.getAPPLY_DATE());

							Map<String, Object> houseLoanMap = inHouseLoanChk(inputVO.getPAYER_ID(), inputVO.getAPPLY_DATE());
							String LOAN_CHK2_YN = (String) houseLoanMap.get("isInHouseLoan");
							Date LOAN_CHK2_DATE = (Date) houseLoanMap.get("inHouseLoanDate");//行內貸款最近撥貸日

							Map<String, Object> loanMap = getCustLoanDate(inputVO.getPAYER_ID(), inputVO.getAPPLY_DATE());
							String P_LOAN_APPLY_YN = (String) loanMap.get("isLoanApply");
							Date P_LOAN_APPLY_DATE = (Date) loanMap.get("loanApplyDate");

							List<Map<String, Object>> payerList = new ArrayList<Map<String,Object>>();
							qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
							sb = new StringBuilder();
							sb.append(" Select cust_name,AO_CODE, birth_date From TBCRM_CUST_MAST ");
							sb.append(" Where cust_id = :payer_id ");
							qc.setObject("payer_id", inputVO.getPAYER_ID().toUpperCase());
							qc.setQueryString(sb.toString());
							payerList = dam_obj.exeQuery(qc);

							if(CollectionUtils.isNotEmpty(payerList)) {
								for(Map<String, Object> map : payerList) {
									if(map.get("BIRTH_DATE") != null) {
										map.put("BIRTHDAY", map.get("BIRTH_DATE"));
									}
									map.put("PAYER_CM_FLAG", PAYER_CM_FLAG);
									map.put("UNDER_YN", UNDER_YN);
									map.put("PRO_YN", PRO_YN);
									map.put("LOAN_CHK1_YN", LOAN_CHK1_YN);
									map.put("LOAN_CHK2_YN", LOAN_CHK2_YN);
									map.put("LOAN_CHK2_DATE", LOAN_CHK2_DATE);
									map.put("LOAN_CHK3_YN", LOAN_CHK3_YN);
									map.put("LOAN_CHK4_YN", LOAN_CHK4_YN);
									map.put("P_REVOLVING_LOAN_YN", P_REVOLVING_LOAN_YN);
									map.put("CD_CHK_YN", CD_CHK_YN);
									map.put("P_CD_DUE_DATE", P_CD_DUE_DATE);
									map.put("P_LOAN_APPLY_YN", P_LOAN_APPLY_YN);
									map.put("P_LOAN_APPLY_DATE", P_LOAN_APPLY_DATE);
								}
							} else {
								payerList = new ArrayList<Map<String, Object>>();
								Map<String, Object> map = new HashMap<String, Object>();
								map.put("PAYER_CM_FLAG", "10");	//不在客戶主檔中，非本行客戶
								map.put("UNDER_YN", UNDER_YN);
								map.put("PRO_YN", PRO_YN);
								map.put("LOAN_CHK1_YN", LOAN_CHK1_YN);
								map.put("LOAN_CHK2_YN", LOAN_CHK2_YN);
								map.put("LOAN_CHK2_DATE", LOAN_CHK2_DATE);
								map.put("LOAN_CHK3_YN", LOAN_CHK3_YN);
								map.put("LOAN_CHK4_YN", LOAN_CHK4_YN);
								map.put("P_REVOLVING_LOAN_YN", P_REVOLVING_LOAN_YN);
								map.put("CD_CHK_YN", CD_CHK_YN);
								map.put("P_CD_DUE_DATE", P_CD_DUE_DATE);
								map.put("P_LOAN_APPLY_YN", P_LOAN_APPLY_YN);
								map.put("P_LOAN_APPLY_DATE", P_LOAN_APPLY_DATE);
								payerList.add(map);
							}
							outputVO.setPAYERList(payerList);
						}else{
							errorMsg = "ehl_01_common_030";
							throw new APException(errorMsg);
						}
					}
					break;
				default:
					break;
			}
			sendRtnObject(outputVO);
//		} catch (Exception e) {
//			if(errorMsg.length()>0){
//				throw new APException(errorMsg);
//			}else{
//				logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
//				throw new APException(e.getMessage());
//			}
//
//		}
	}

	//產生保險文件編號
	public INSIDDataVO getINS_ID(Object body) throws JBranchException{
		INSIDInputVOinputVO inputVO = (INSIDInputVOinputVO) body;
		INSIDDataVO outputVO = new INSIDDataVO();
		String Year = "";
		String seq = "";
		String BRANCH_NBR_change = inputVO.getBRANCH_NBR().toString().substring(0, 3);
		List<Map<String, Object>> yearList = new ArrayList<Map<String,Object>>();
		List<Map<String, Object>> seqList = new ArrayList<Map<String,Object>>();
		dam_obj = this.getDataAccessManager();
		QueryConditionIF qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();
		try {
			sb.append(" SELECT SUBSTR(TO_CHAR(SYSDATE, 'YYYY'),3,2) YY FROM DUAL ");
			qc.setQueryString(sb.toString());
			yearList = dam_obj.exeQuery(qc);
			Year = yearList.get(0).get("YY").toString();
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
		switch (inputVO.getINS_KIND()) {
			case "1":
				//新契約-紙本要保書
				if("2".equals(inputVO.getREG_TYPE())){
					dam_obj = this.getDataAccessManager();
					qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					sb = new StringBuilder();
					try {
						sb.append(" SELECT (CASE WHEN MAX(SUBSTRB(A.INS_ID, 6, 9)) IS NULL THEN '0001' ");
						sb.append(" WHEN MAX(SUBSTRB(A.INS_ID, 6, 9)) >= '2000' THEN '0001' ELSE LPAD(MAX(SUBSTRB(A.INS_ID, 6, 9))+1,4, '0') END) AS seq ");
						sb.append(" FROM TBIOT_MAIN A WHERE A.BRANCH_NBR= :BRANCH_NBR ");
						sb.append(" AND TRUNC(A.CREATETIME,'Y')=TRUNC(SYSDATE,'Y') ");
						sb.append(" AND REG_TYPE = :reg_type ");
						qc.setObject("BRANCH_NBR", inputVO.getBRANCH_NBR());
						qc.setObject("reg_type", inputVO.getREG_TYPE());
						qc.setQueryString(sb.toString());
						seqList = dam_obj.exeQuery(qc);
					} catch (Exception e) {
						logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
						throw new APException("系統發生錯誤請洽系統管理員");
					}
					//其他文件與匯利專案
				}else if("3".equals(inputVO.getREG_TYPE()) || "4".equals(inputVO.getREG_TYPE())){
					dam_obj = this.getDataAccessManager();
					qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					sb = new StringBuilder();
					try {
						sb.append(" SELECT (CASE WHEN MAX(SUBSTRB(A.INS_ID, 6, 9)) IS NULL THEN '6001' ");
						sb.append(" WHEN MAX(SUBSTRB(A.INS_ID, 6, 9)) >= '9999' THEN '6001' ELSE LPAD(MAX(SUBSTRB(A.INS_ID, 6, 9))+1,4, '0') END) AS seq ");
						sb.append(" FROM TBIOT_MAIN A WHERE A.BRANCH_NBR= :BRANCH_NBR ");
						sb.append(" AND TRUNC(A.CREATETIME,'Y')=TRUNC(SYSDATE,'Y') ");
						sb.append(" AND REG_TYPE IN (3,4) ");
						qc.setObject("BRANCH_NBR", inputVO.getBRANCH_NBR());
						qc.setQueryString(sb.toString());
						seqList = dam_obj.exeQuery(qc);
					} catch (Exception e) {
						logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
						throw new APException("系統發生錯誤請洽系統管理員");
					}
				}
				break;
			//產險
			case "2":
				dam_obj = this.getDataAccessManager();
				qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sb = new StringBuilder();
				try {
					sb.append(" SELECT (CASE WHEN MAX(SUBSTRB(A.INS_ID, 6, 9)) IS NULL THEN '0001' ");
					sb.append(" WHEN MAX(SUBSTRB(A.INS_ID, 6, 9)) >= '9999' THEN '0001' ELSE LPAD(MAX(SUBSTRB(A.INS_ID, 6, 9))+1,4, '0') END) AS seq ");
					sb.append(" FROM TBIOT_PPT_MAIN A WHERE A.BRANCH_NBR= :BRANCH_NBR ");
//				sb.append(" AND REG_TYPE = :reg_type ");	//檢查是否重複時沒有以登錄類型檢查
					sb.append(" AND TRUNC(A.CREATETIME,'Y')=TRUNC(SYSDATE,'Y') ");
					qc.setObject("BRANCH_NBR", inputVO.getBRANCH_NBR());
//				qc.setObject("reg_type", inputVO.getREG_TYPE());
					qc.setQueryString(sb.toString());
					seqList = dam_obj.exeQuery(qc);
				} catch (Exception e) {
					logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
					throw new APException("系統發生錯誤請洽系統管理員");
				}
				break;
			default:
				break;
		}
		seq = seqList.get(0).get("SEQ").toString();
		String INS_ID = BRANCH_NBR_change+Year+seq;

		outputVO.setINS_ID(check_INS_ID(inputVO.getINS_KIND(), INS_ID));
		return outputVO;
	}

	//檢核保險文件編號是否重複
	public String check_INS_ID(String INS_KIND,String INS_ID) throws APException{
		try {
			List<Map<String, Object>> ins_idList = new ArrayList<Map<String,Object>>();
			dam_obj = this.getDataAccessManager();
			QueryConditionIF qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);;
			StringBuilder sb = new StringBuilder();
			switch (INS_KIND) {
				case "1":
					sb.append(" select count(*) as count from TBIOT_MAIN where INS_ID = :ins_id ");
					qc.setObject("ins_id", INS_ID);
					qc.setQueryString(sb.toString());

					break;
				case "2":
					sb.append(" select count(*) as count from TBIOT_PPT_MAIN where INS_ID = :ins_id ");
					qc.setObject("ins_id", INS_ID);
					qc.setQueryString(sb.toString());
					break;
				default:
					break;
			}
			ins_idList = dam_obj.exeQuery(qc);
			BigDecimal count_change = new BigDecimal(ins_idList.get(0).get("COUNT").toString());
			int count = count_change.intValue();
			if(count > 0){
				throw new APException("ehl_02_IOT920_001");
			}else{
				return INS_ID;
			}
		} catch (JBranchException e) {
			e.printStackTrace();
			throw new APException(e.getMessage());
		}
	}

	//首購判斷
	public FirstBuyDataVO chk_FirstBuy(Object body) throws JBranchException{
		FirstBuyInputVO inputVO = (FirstBuyInputVO) body;
		FirstBuyDataVO outputVO = new FirstBuyDataVO();
		List<Map<String, Object>> first = new ArrayList<Map<String,Object>>();
		List<Map<String, Object>> second = new ArrayList<Map<String,Object>>();
		dam_obj = this.getDataAccessManager();
		QueryConditionIF qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();
		try {
			sb.append(" select count(*) as count from TBSYS_REC_LOG ");
			sb.append(" where substr(transseq, 7, 1) = '7' ");
			sb.append(" and custid = :CUST_ID and (");
			sb.append(" (:INSPRD_TYPE1 in ('2','3') and substr(transseq, 8, 1) = '2') Or ");
			sb.append(" (:INSPRD_TYPE2 = '1' and :curr_cd <> 'TWD' and substr(transseq, 8, 1) = '1') ) ");
			qc.setObject("CUST_ID", inputVO.getCUST_ID());
			qc.setObject("INSPRD_TYPE1", inputVO.getINSPRD_TYPE());
			qc.setObject("INSPRD_TYPE2", inputVO.getINSPRD_TYPE());
			qc.setObject("curr_cd", inputVO.getCURR_CD());
			qc.setQueryString(sb.toString());
			first = dam_obj.exeQuery(qc);
			BigDecimal bg= new BigDecimal(first.get(0).get("COUNT").toString());
			int first_check = bg.intValue();
			if(first_check>=1){
				outputVO.setFirstBuy_YN("N");
			}else{
				dam_obj = this.getDataAccessManager();
				qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sb = new StringBuilder();
				//富壽保單
				sb.append(" select 1 from TBCRM_NPOLM ");
				sb.append(" where APPL_ID = :CUST_ID ");
				sb.append(" and NVL(POLICY_STATUS,'01') in ('01','02','04','05','06','07','10','11','12','16','20','21','22','25','27','28') ");
				//sb.append(" and (POLICY_STATUS = '01' AND SEND_DATE-12 >sysdate) ");
				sb.append(" and ((:INSPRD_TYPE in ('2','3') and ITEM_REMRK in ('U','F')) or (:INSPRD_TYPE = '1' and POLICY_CUR <> 'TWD'))");
				sb.append(" UNION ");
				//日盛保單
				sb.append(" SELECT 1 FROM TBJSB_AST_INS_MAST ");
				sb.append(" WHERE CUST_ID = :CUST_ID ");
				sb.append(" AND REPLACE(TRIM(CONTRACT_TEXT), ' ', '') IN (SELECT PARAM_NAME FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'IOT.JSB_CONTRACT_TEXT_CHK') ");
				sb.append(" AND ((:INSPRD_TYPE in ('2','3') AND SP_POLICY_NOTE = 'U') OR (:INSPRD_TYPE = '1' AND CRCY_TYPE <> 'TWD')) ");

				qc.setObject("INSPRD_TYPE", inputVO.getINSPRD_TYPE());
				qc.setObject("CUST_ID", inputVO.getCUST_ID());
				qc.setQueryString(sb.toString());
				second = dam_obj.exeQuery(qc);
				outputVO.setFirstBuy_YN(CollectionUtils.isNotEmpty(second) ? "N" : "Y");
			}
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
		return outputVO;
	}


	//非常態錄音電訪判斷
	public chk_AbOutputVO chk_Ab(Object body) throws JBranchException{
		chk_AbInputVO inputVO = (chk_AbInputVO) body;
		chk_AbOutputVO outputVO = new chk_AbOutputVO();
		//年化保費
		BigDecimal year_cast = null;
		BigDecimal RP_change = null;
		String RP = "";
		if(inputVO.getREAL_PREMIUM() != null){
			RP = inputVO.getREAL_PREMIUM().replaceAll(",", "");
			RP_change = new BigDecimal(RP);
		}
		BigDecimal AE_change = null;
		BigDecimal pay_type = null;
		BigDecimal one_million = new BigDecimal("1000000");
		BigDecimal Three_hundred_thousand = new BigDecimal("300000");
		if(inputVO.getAB_EXCH_RATE() != null){
			AE_change = new BigDecimal(inputVO.getAB_EXCH_RATE());
		}else{
			throw new APException("非常態交易匯率為null");
		}
		//實收保費
		BigDecimal Actual_charge = RP_change.multiply(AE_change);
		if("1".equals(inputVO.getPAY_TYPE().toString())){
			pay_type = new BigDecimal("1");
			year_cast = RP_change.multiply(pay_type).multiply(AE_change);
		}else{
			switch (inputVO.getMOP2()) {
				case "A":
					pay_type = new BigDecimal("1");
					year_cast = RP_change.multiply(pay_type).multiply(AE_change);
					break;
				case "S":
					pay_type = new BigDecimal("2");
					year_cast = RP_change.multiply(pay_type).multiply(AE_change);
					break;
				case "Q":
					pay_type = new BigDecimal("4");
					year_cast = RP_change.multiply(pay_type).multiply(AE_change);
					break;
				case "M":
					pay_type = new BigDecimal("12");
					year_cast = RP_change.multiply(pay_type).multiply(AE_change);
					break;
				default:
					break;
			}
		}
		//比較實收保費
		int Actual_Comparison = Actual_charge.compareTo(one_million);
		//比較年化保費
		int Year_Comparison = year_cast.compareTo(Three_hundred_thousand);

		if(("1".equals(inputVO.getPAY_TYPE()) && Actual_Comparison<0) || (!"1".equals(inputVO.getPAY_TYPE()) && Year_Comparison <0)){
			if("Y".equals(inputVO.getUNDER_YN())){
				outputVO.setAbTranSEQ_YN("Y");
				return outputVO;
			}else if("Y".equals(inputVO.getPRO_YN())){
				outputVO.setAbTranSEQ_YN("N");
				return outputVO;
			}else if("Y".equals(inputVO.getFirstBuy_YN())){
				outputVO.setAbTranSEQ_YN("Y");
				return outputVO;
			}else{
				outputVO.setAbTranSEQ_YN("N");
			}
		}else{
			outputVO.setAbTranSEQ_YN("N");
		}
		return outputVO;
	}

	//依照客戶風險屬性列出投資型連結標的清單
	public void Get_List(Object body, IPrimitiveMap<Object> header) throws Exception{
		InsFundListInputVO inputVO =  (InsFundListInputVO) body;
		InsFundListDataVO outputVO = new InsFundListDataVO();
		
		outputVO.setINVESTList(Get_List(inputVO));
		this.sendRtnObject(outputVO);
	}

	//依照客戶風險屬性列出投資型連結標的清單
	public List<Map<String, Object>> Get_List(InsFundListInputVO inputVO) throws Exception{
		String errorMsg = "";
		CustKYCDataVO custKYCDataVO = new CustKYCDataVO();
		SOT701InputVO sot701inputVO = new SOT701InputVO();
		sot701inputVO.setCustID(inputVO.getCustID());
		SOT701 sot701 = (SOT701) PlatformContext.getBean("sot701");

		if(inputVO.getPRD_RISK() == null) {
			custKYCDataVO = sot701.getCustKycData(sot701inputVO);
			SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
			Date now = df.parse(df.format(new Date()));
			if(custKYCDataVO.getKycDueDate() == null) {
				errorMsg = "查不到風險屬性或KYC截止日到期";
				throw new APException(errorMsg);
			}
			
			Date kycDueDate = df.parse(df.format(custKYCDataVO.getKycDueDate().getTime()));

			if(custKYCDataVO.getKycLevel() != null && kycDueDate.compareTo(now) >= 0){
				inputVO.setPRD_RISK(custKYCDataVO.getKycLevel());
			}else{
				errorMsg = "查不到風險屬性或KYC截止日到期";
				throw new APException(errorMsg);
			}
		}
		
		//執行SQL
		dam_obj = this.getDataAccessManager();
		List<Map<String, Object>> INVESTListTemp = new ArrayList<Map<String,Object>>();
		QueryConditionIF queryCondition = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append(" select a.KEY_NO AS PRD_LK_KEYNO,a.INSPRD_ID,a.TARGET_ID,a.FUND_ID, ");
		sql.append(" a.LIPPER_ID,a.LINKED_NAME, a.prd_risk, a.TARGET_CURR, a.COM_PRD_RISK ");
		sql.append(" from TBPRD_INS_LINKING a ");
		sql.append(" where a.INSPRD_ID = :insprd_id ");
		//若有高齡投組適配選擇越級P值
		if(StringUtils.isNotBlank(inputVO.getSENIOR_OVER_PVAL())) {
			sql.append(" and (substr(PRD_RISK,2) <= :cust_risk OR substr(PRD_RISK,2) = substrb(:seniorOverPval, 2, 1)) ");
			queryCondition.setObject("seniorOverPval", inputVO.getSENIOR_OVER_PVAL());
		} else {
			sql.append(" and substr(PRD_RISK,2) <= :cust_risk  ");
		}
		//要保人高齡限制P值
		if(StringUtils.isNotBlank(inputVO.getC_SENIOR_PVAL())) {
			sql.append("AND substrb(PRD_RISK, 2, 1) <= substrb(:seniorPVal, 2, 1) ");
			queryCondition.setObject("seniorPVal", inputVO.getC_SENIOR_PVAL());
		}
		queryCondition.setObject("insprd_id", inputVO.getINSPRD_ID());
		queryCondition.setObject("cust_risk", inputVO.getPRD_RISK().substring(1));
		queryCondition.setQueryString(sql.toString());
		INVESTListTemp = dam_obj.exeQuery(queryCondition);

		if(inputVO.getINVESTList() != null){
			if(inputVO.getINVESTList().size()>0){
				for(Map<String, Object> temp:INVESTListTemp){
					Map<String, Object> delete_temp = new HashMap<String, Object>();
					for(Map<String, Object> map:inputVO.getINVESTList()){
						if(temp.get("TARGET_ID").equals(map.get("TARGET_ID"))){
							temp.put("ALLOCATION_RATIO", map.get("ALLOCATION_RATIO"));
							delete_temp.putAll(map);
						}
					}
					inputVO.getINVESTList().remove(delete_temp);
				}
			}
		}

		return INVESTListTemp;
	}

	//壽險證照檢核
	public void chk_CertTraining(Object body, IPrimitiveMap<Object> header) throws JBranchException{
		chk_CTInputVO inputVO = (chk_CTInputVO) body;
		chk_CTOutputVO outputVO = chk_CertTraining(inputVO);
		sendRtnObject(outputVO);
	}
	
	//壽險證照檢核
	public chk_CTOutputVO chk_CertTraining(chk_CTInputVO inputVO) throws JBranchException{
		chk_CTOutputVO outputVO = new chk_CTOutputVO();
		dam_obj = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		outputVO.setChk_Pass("Y");
		outputVO.setCERT_chk("1");
		outputVO.setTRAINING_chk("1");
		if(StringUtils.isNotBlank(inputVO.getCERT_TYPE())){
			if(inputVO.getDOC_KEYIN_DATE() != null) {
				inputVO.setAPPLY_DATE(inputVO.getDOC_KEYIN_DATE());	//配合IOT130新增"要保書申請日APPLY_DATE"欄位，原"文件申請填寫日"改為DOC_KEYIN_DATE
			}
			dam_obj = this.getDataAccessManager();
			queryCondition = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql = new StringBuffer();
			sql.append(" SELECT COUNT(*) as count FROM TBORG_MEMBER_CERT ");
			sql.append(" WHERE CERTIFICATE_CODE = (SELECT PARAM_NAME FROM TBSYSPARAMETER ");
			sql.append(" WHERE PARAM_TYPE = 'IOT.CERT_TYPE_CHK' ");
			sql.append(" AND PARAM_CODE = :CERT_TYPE) ");
			sql.append(" AND EMP_ID = :EMP_ID ");
			sql.append(" AND REG_DATE <= :APPLY_DATE ");
			queryCondition.setObject("CERT_TYPE", inputVO.getCERT_TYPE());
			queryCondition.setObject("EMP_ID", inputVO.getEMP_ID());
			queryCondition.setObject("APPLY_DATE", inputVO.getAPPLY_DATE());
			queryCondition.setQueryString(sql.toString());
			List<Map<String, Object>> temp = new ArrayList<Map<String,Object>>();
			temp = dam_obj.exeQuery(queryCondition);
			outputVO.setCERT_chk(temp.get(0).get("COUNT").toString());
		}
		if(StringUtils.isNotBlank(inputVO.getTRAINING_TYPE())){
			dam_obj = this.getDataAccessManager();
			queryCondition = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql = new StringBuffer();
			sql.append(" SELECT COUNT(*) as count FROM TBORG_MEMBER_CERT ");
			sql.append(" WHERE CERTIFICATE_CODE = (SELECT PARAM_NAME FROM TBSYSPARAMETER ");
			sql.append(" WHERE PARAM_TYPE = 'IOT.TRAINING_TYPE_CHK' ");
			sql.append(" AND PARAM_CODE = :TRAINING_TYPE) ");
			sql.append(" AND EMP_ID = :EMP_ID ");
			queryCondition.setObject("TRAINING_TYPE", inputVO.getTRAINING_TYPE());
			queryCondition.setObject("EMP_ID", inputVO.getEMP_ID());
			queryCondition.setQueryString(sql.toString());
			List<Map<String, Object>> temp1 = new ArrayList<Map<String,Object>>();
			temp1 = dam_obj.exeQuery(queryCondition);
			outputVO.setTRAINING_chk(temp1.get(0).get("COUNT").toString());
		}
		if("0".equals(outputVO.getCERT_chk()) || "0".equals(outputVO.getTRAINING_chk())){
			outputVO.setChk_Pass("N");
		}
		//檢核招攬人員是否有分紅證照
		if(StringUtils.isNotBlank(inputVO.getEMP_ID())){
			outputVO.setEmpDividendCert(getEmpDividendCert(inputVO.getEMP_ID()));
		}
		
		return outputVO;
	}

	/***
	 * 招攬人員是否有分紅證照
	 * @param empId 招攬人員員編
	 * @return 	EMP_DIVIDEND_YN Y:有證照 N:沒證照
	 * 			EMP_DIVIDEND_END_DATE 完訓日
	 * @throws DAOException
	 * @throws JBranchException
	 */
	private Map<String, Object> getEmpDividendCert(String empId) throws DAOException, JBranchException {
		dam_obj = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT TRUNC(END_DATE) AS END_DATE FROM TBORG_PS_SA_INS_EMPFUND ");
		sql.append(" WHERE CLASS = 'MA02' AND EMPID = :EMP_ID ");
		queryCondition.setObject("EMP_ID", empId);
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> temp1 = dam_obj.exeQuery(queryCondition);
		
		Map<String, Object> map = new HashMap<String, Object>();
		if(CollectionUtils.isEmpty(temp1)) {
			map.put("EMP_DIVIDEND_YN", "N");
			map.put("EMP_DIVIDEND_END_DATE", null);
		} else {
			map.put("EMP_DIVIDEND_YN", "Y");
			map.put("EMP_DIVIDEND_END_DATE", temp1.get(0).get("END_DATE"));
		}
				
		return map;
	}
	
	/***
	 * 招攬人員公平待客完訓資格
	 * @param empId 招攬人員員編
	 * @param applyDate 要保書申請日
	 * @return Y: 有完訓資格 N:沒有完訓資格
	 * @throws DAOException
	 * @throws JBranchException
	 */
	private String getFairCert(String empId, Date applyDate) throws DAOException, JBranchException {
		dam_obj = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT 1 FROM TBORG_MEMBER_CERT ");
		sql.append(" WHERE CERTIFICATE_CODE = '11' AND EMP_ID = :EMP_ID ");
		sql.append("   AND (TO_CHAR(TRAIN_DATE, 'YYYY') = TO_CHAR(:applyDate, 'YYYY') OR ");
		sql.append("        TO_CHAR(TRAIN_DATE, 'YYYY') = TO_CHAR(ADD_MONTHS(:applyDate, -12), 'YYYY') OR ");
		sql.append("		(TRAIN_DATE_BEFORE IS NOT NULL AND TO_CHAR(TRAIN_DATE_BEFORE, 'YYYY') = TO_CHAR(:applyDate, 'YYYY')) OR ");
		sql.append("		(TRAIN_DATE_BEFORE IS NOT NULL AND TO_CHAR(TRAIN_DATE_BEFORE, 'YYYY') = TO_CHAR(ADD_MONTHS(:applyDate, -12), 'YYYY'))) ");
		queryCondition.setObject("EMP_ID", empId);
		queryCondition.setObject("applyDate", applyDate);
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> chkList = dam_obj.exeQuery(queryCondition);
		
		return (CollectionUtils.isNotEmpty(chkList) ? "Y" : "N");
	}
	
	/***
	 * 招攬人員高齡完訓資格
	 * @param empId 招攬人員員編
	 * @param applyDate 要保書申請日
	 * @return Y: 有完訓資格 N:沒有完訓資格
	 * @throws DAOException
	 * @throws JBranchException
	 */
	private String getSeniorCert(String empId, Date applyDate) throws DAOException, JBranchException {
		dam_obj = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT 1 FROM TBORG_MEMBER_CERT ");
		sql.append(" WHERE CERTIFICATE_CODE = '09' AND EMP_ID = :EMP_ID ");
		sql.append("   AND (TO_CHAR(TRAIN_DATE, 'YYYY') = TO_CHAR(ADD_MONTHS(:applyDate, -12), 'YYYY') OR ");
		sql.append("   		(TRAIN_DATE_BEFORE IS NOT NULL AND TO_CHAR(TRAIN_DATE_BEFORE, 'YYYY') = TO_CHAR(ADD_MONTHS(:applyDate, -12), 'YYYY'))) ");
		queryCondition.setObject("EMP_ID", empId);
		queryCondition.setObject("applyDate", applyDate);
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> chkList = dam_obj.exeQuery(queryCondition);
		
		return (CollectionUtils.isNotEmpty(chkList) ? "Y" : "N");
	}
	
	//抓取產險險種
	public void getPPTID()throws JBranchException{
		IOT920InputVO inputVO = new IOT920InputVO();
		PPTIDDataVO outputVO = new PPTIDDataVO();
		dam_obj = getDataAccessManager();
		QueryConditionIF queryCondition = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		try {
			sql.append(" Select INSPRD_ID, INSPRD_NAME From TBPRD_INS_PPT_MAIN ");
			queryCondition.setQueryString(sql.toString());
			outputVO.setPPTIDData(dam_obj.exeQuery(queryCondition)); // data
			sendRtnObject(outputVO);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}

	//產險證照檢核
	public void chk_PPTCert(Object body, IPrimitiveMap<Object> header) throws JBranchException{
		chk_PPTCInputVO inputVO = (chk_PPTCInputVO) body;
		chk_PPTCOutputVO outputVO = new chk_PPTCOutputVO();
		List<Map<String, Object>> temp_data = new ArrayList<Map<String,Object>>();
		List<Map<String, Object>> chk_passList = new ArrayList<Map<String,Object>>();
		try {
			//以PM專區基本資料為基礎
			dam_obj = getDataAccessManager();
			QueryConditionIF queryCondition = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sql = new StringBuffer();
			if (!inputVO.getINSPRD_ID().equals("")) {
				sql.append(" SELECT CERT_03,CERT_05 FROM TBPRD_INS_PPT_MAIN ");
				sql.append(" WHERE INSPRD_ID = :INSPRD_ID ");
				queryCondition.setObject("INSPRD_ID", inputVO.getINSPRD_ID());
				queryCondition.setQueryString(sql.toString());
				temp_data = dam_obj.exeQuery(queryCondition);
				// 檢核證照檔
				dam_obj = getDataAccessManager();
				queryCondition = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sql = new StringBuffer();
				if (temp_data.get(0).get("CERT_03") != null || temp_data.get(0).get("CERT_05") != null) {
					sql.append(" SELECT 'x' FROM DUAL WHERE 1 = 1 ");
					if(temp_data.get(0).get("CERT_03") != null){
						sql.append(" AND (EXISTS (SELECT 'x' FROM TBORG_MEMBER_CERT  ");
						sql.append(" WHERE CERTIFICATE_CODE = '03' ");
						sql.append(" AND EMP_ID = :emp_id "
								+ "AND REG_DATE <= :APPLY_DATE))  ");
					}
					if(temp_data.get(0).get("CERT_05") != null){
						sql.append(" AND (EXISTS (SELECT 'x' FROM TBORG_MEMBER_CERT  ");
						sql.append(" WHERE CERTIFICATE_CODE = '05'  ");
						sql.append(" AND EMP_ID = :emp_id "
								+ "AND REG_DATE <= :APPLY_DATE)) ");
					}
					queryCondition.setObject("emp_id", inputVO.getEMP_ID());
					queryCondition.setObject("APPLY_DATE", inputVO.getAPPLY_DATE());
					queryCondition.setQueryString(sql.toString());
					chk_passList = dam_obj.exeQuery(queryCondition);
				}

				if (chk_passList.size() == 0) {
					if (temp_data.get(0).get("CERT_03") == null && temp_data.get(0).get("CERT_05") == null) {
						outputVO.setChk_Pass("Y");
					} else {
						outputVO.setChk_Pass("N");
					}
				} else {
					outputVO.setChk_Pass("Y");
				}
			}
			sendRtnObject(outputVO);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}

	//取消打包確認
	public String unPackchk(String status, String RoleID)
			throws JBranchException {
		dam_obj = getDataAccessManager();
		QueryConditionIF qc = dam_obj
				.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();
		List<Map<String, Object>> chkList = new ArrayList<Map<String, Object>>();
		String chk = "";
		try {
			sb.append(" select  FN_CHK_IOTSTATUSFLOW(:status,'20',:roleID) as CHK from dual ");
			qc.setObject("status", status);
			qc.setObject("roleID", RoleID);
			qc.setQueryString(sb.toString());
			chkList = dam_obj.exeQuery(qc);
			chk = chkList.get(0).get("CHK").toString();
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s",
					StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
		return chk;
	}

	/***
	 * 繳款人貸款檢核(透過本行送件)
	 * 1. 以繳款人ID搜尋保單主檔8A檔(繳款人id=要保人id)，貸款餘額LOAN_AMT<>0且要保書申請日 - 契約狀態日(cnct_date) < 93 天，若符合條件則為”Y”，若無則為”N”
	 * 		都無主檔8A資料，則回"N"
	 * 2. 以要保人ID、被保人ID、繳款人ID，搜尋其他送件登錄作業，若符合以下條件，則保險單借款檢核=”Y”
	 * 		A.文件種類=保險單借款
	 * 		B.ID符合其他送件案件的要保人ID
	 * 		C.新契約要保書申請日-93天<文件申請日<=系統日
	 * @param inputVO
	 * @return
	 * @throws DAOException
	 * @throws JBranchException
	 */
	public String insLoanChk(String custID, Date applyDate) throws DAOException, JBranchException {
		if(applyDate == null || StringUtils.isBlank(custID))
			return "";

		dam_obj = getDataAccessManager();
		QueryConditionIF qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();
		List<Map<String, Object>> chkList = new ArrayList<Map<String, Object>>();

		sb.append(" SELECT 1 FROM DUAL ");
		sb.append(" WHERE EXISTS (SELECT 1 FROM TBCRM_FBRNY8A0 WHERE APPL_ID = :custID) ");
		sb.append("	  AND EXISTS (SELECT 1 FROM TBCRM_FBRNY8A0 WHERE APPL_ID = :custID ");
		sb.append(" 		  	  AND LOAN_AMT <> 0 AND CNCT_STAT IN ('01', '02', '04', '16', 'WP') ");
		sb.append("				  AND (TRUNC(CNCT_DATE) - TRUNC(:applyDate)) > -93 AND (TRUNC(CNCT_DATE) - TRUNC(:applyDate)) < 93) ");
		qc.setObject("custID", custID);
		qc.setObject("applyDate", new Timestamp(applyDate.getTime()));
		qc.setQueryString(sb.toString());
		chkList = dam_obj.exeQuery(qc);
		//以繳款人ID搜尋保單主檔8A檔(繳款人id=要保人id)，貸款餘額LOAN_AMT<>0且要保書申請日 - 契約狀態日(cnct_date) < 93 天，若符合條件則為”Y”，若無則為”N”
		String chk1 = CollectionUtils.isEmpty(chkList) ? "N" : "Y";

		//搜尋其他送件登錄作業
		qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb = new StringBuilder();
		chkList = new ArrayList<Map<String, Object>>();

		sb.append(" SELECT 1 ");
		sb.append(" FROM TBIOT_MAIN A ");
		sb.append(" WHERE A.CUST_ID = :custID "); //ID符合其他送件案件的要保人ID
		sb.append(" AND A.REG_TYPE = '3' AND A.OTH_TYPE = 'P' "); //其他送件登錄，文件種類=保險單借款
		sb.append(" AND TRUNC(:applyDate) - 93 < TRUNC(A.DOC_KEYIN_DATE) AND TRUNC(A.DOC_KEYIN_DATE) <= TRUNC(SYSDATE) "); //新契約要保書申請日-93天<文件申請日<=系統日
		sb.append(" AND A.STATUS <> 99 "); //文件狀態不等於99(刪除)
		qc.setObject("custID", custID);
		qc.setObject("applyDate", new Timestamp(applyDate.getTime()));
		qc.setQueryString(sb.toString());
		chkList = dam_obj.exeQuery(qc);
		//其他送件登錄作業，A.文件種類=契變-解約/縮小保額 B.ID符合其他送件案件的要保人ID C.新契約要保書申請日-93天<文件申請日<=系統日
		String chk2 = CollectionUtils.isEmpty(chkList) ? "N" : "Y";

		//日盛保單借款檢核
		qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb = new StringBuilder();
		chkList = new ArrayList<Map<String, Object>>();

		sb.append(" SELECT 1 ");
		sb.append(" FROM TBJSB_INS_POLICY2 A ");
		sb.append(" LEFT JOIN TBJSB_INS_POLICY2STCHANGE B ON B.POLICYSERIALNUM = A.POLICYSERIALNUM ");
		sb.append(" WHERE A.PAYERIDNO = :custID "); //要保人ID
		sb.append(" AND B.POLICYLOAN =  'Y' "); //保單借款
		sb.append(" AND TRUNC(B.POLICYSTATUSCHANGEDATE) < TRUNC(:applyDate) - 93 "); //註記日期<要保書申請日-93天
		qc.setObject("custID", custID);
		qc.setObject("applyDate", new Timestamp(applyDate.getTime()));
		qc.setQueryString(sb.toString());
		chkList = dam_obj.exeQuery(qc);
		//日盛保單有保單貸款註記，且註記日期<要保書申請日-93天
		String chk3 = CollectionUtils.isEmpty(chkList) ? "N" : "Y";

		return ((StringUtils.equals("Y", chk1) || StringUtils.equals("Y", chk2) || StringUtils.equals("Y", chk3)) ? "Y" : "N");
	}

	/***
	 * 行內貸款檢核
	 * 		-- 貸款餘額loan_bal <> 0且要保書申請日 - 撥貸日(interest_start_date) < 93 天，若符合條件則為”Y”，若無則為”N”，都無貸款資料，則回"N"
	 * 行內貸款最近撥貸日
	 * 		-- 貸款餘額loan_bal <> 0且要保書申請日 - 撥貸日(interest_start_date) < 365 天(一年)，取一年內最近撥貸日
	 * @param inputVO
	 * @return
	 * @throws DAOException
	 * @throws JBranchException
	 */
	public Map<String, Object> inHouseLoanChk(String custID, Date applyDate) throws DAOException, JBranchException {
		Map<String, Object> map = new HashMap<String, Object>();

		if(applyDate == null || StringUtils.isBlank(custID))
			return map;

		dam_obj = getDataAccessManager();
		QueryConditionIF qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();
		List<Map<String, Object>> chkList = new ArrayList<Map<String, Object>>();

		sb.append(" SELECT 1 FROM DUAL ");
		sb.append(" WHERE EXISTS (SELECT 1 FROM TBCRM_AST_LIB_DTL WHERE CUST_ID = :custID) ");
		sb.append("	  AND EXISTS (SELECT 1 FROM TBCRM_AST_LIB_DTL WHERE CUST_ID = :custID ");
		sb.append(" 		  	  AND (TRUNC(INTEREST_START_DATE) - TRUNC(:applyDate)) > -93 AND (TRUNC(INTEREST_START_DATE) - TRUNC(:applyDate)) < 93) ");
		qc.setObject("custID", custID);
		qc.setObject("applyDate", new Timestamp(applyDate.getTime()));
		qc.setQueryString(sb.toString());
		chkList = dam_obj.exeQuery(qc);

		map.put("isInHouseLoan", CollectionUtils.isEmpty(chkList) ? "N" : "Y");

		qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb = new StringBuilder();
		sb.append(" SELECT TRUNC(MAX(INTEREST_START_DATE)) AS INTEREST_START_DATE FROM TBCRM_AST_LIB_DTL ");
		sb.append(" WHERE CUST_ID = :custID AND DEBIT_TYPE IN ('1', '2', '8', '10') ");
		sb.append("  AND (TRUNC(INTEREST_START_DATE) - TRUNC(:applyDate)) > -365 AND (TRUNC(INTEREST_START_DATE) - TRUNC(:applyDate)) < 365 ");
		qc.setObject("custID", custID);
		qc.setObject("applyDate", new Timestamp(applyDate.getTime()));
		qc.setQueryString(sb.toString());
		chkList = dam_obj.exeQuery(qc);

		map.put("inHouseLoanDate", CollectionUtils.isEmpty(chkList) ? null : (Date)chkList.get(0).get("INTEREST_START_DATE"));


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
	 * @param custID
	 * @param applyDate
	 * @return
	 * @throws DAOException
	 * @throws JBranchException
	 */
	public String insTerminateChk(String custID, Date applyDate) throws DAOException, JBranchException {
		if(applyDate == null || StringUtils.isBlank(custID))
			return "";

		dam_obj = getDataAccessManager();
		QueryConditionIF qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();
		List<Map<String, Object>> chkList = new ArrayList<Map<String, Object>>();

		sb.append(" SELECT 1 ");
		sb.append(" FROM TBCRM_NPOLM A ");
		sb.append(" INNER JOIN (SELECT POLICY_NO, POLICY_SEQ, ID_DUP FROM TBCRM_NPOLM WHERE APPL_ID = :custID ");
		sb.append(" 			UNION ");
		sb.append(" 			SELECT POLICY_NO, POLICY_SEQ, ID_DUP FROM TBCRM_NPOLD WHERE BELONG_ID = :custID) B ");
		sb.append("		ON A.POLICY_NO = B.POLICY_NO AND A.POLICY_SEQ = B.POLICY_SEQ AND A.ID_DUP = B.ID_DUP ");
		sb.append(" WHERE A.POLICY_STATUS = '21' AND (TRUNC(A.TERMINATE_DATE) - TRUNC(:applyDate)) > -93 AND (TRUNC(A.TERMINATE_DATE) - TRUNC(:applyDate)) < 93 ");
		qc.setObject("custID", custID);
		qc.setObject("applyDate", new Timestamp(applyDate.getTime()));
		qc.setQueryString(sb.toString());
		chkList = dam_obj.exeQuery(qc);

		//客戶ID(要保人或被保人)93天內有保單解約，則Y
		String chk1 = CollectionUtils.isEmpty(chkList) ? "N" : "Y";

		qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb = new StringBuilder();
		chkList = new ArrayList<Map<String, Object>>();

		sb.append(" SELECT 1 ");
		sb.append(" FROM TBCRM_NPOLM A ");
		sb.append(" INNER JOIN (SELECT POLICY_NO, POLICY_SEQ, ID_DUP FROM TBCRM_NPOLM WHERE APPL_ID = :custID ");
		sb.append(" 			UNION ");
		sb.append(" 			SELECT POLICY_NO, POLICY_SEQ, ID_DUP FROM TBCRM_NPOLD WHERE BELONG_ID = :custID) B ");
		sb.append("		ON A.POLICY_NO = B.POLICY_NO AND A.POLICY_SEQ = B.POLICY_SEQ AND A.ID_DUP = B.ID_DUP ");
		sb.append(" WHERE A.POLICY_STATUS IN ('1', '2', '16') AND (TRUNC(:applyDate) - TRUNC(A.TERMINATE_DATE)) < 180 ");
		qc.setObject("custID", custID);
		qc.setObject("applyDate", new Timestamp(applyDate.getTime()));
		qc.setQueryString(sb.toString());
		chkList = dam_obj.exeQuery(qc);

		//保單要保書申請日-提領保價日<180天，則行內解約=Y (要保人舊保單提領保額/保價日為要保書申請日前3個月，行內解約為Y)
//		String chk2 = CollectionUtils.isEmpty(chkList) ? "N" : "Y";
		String chk2 = "N"; //先拿掉，邏輯有變

		//搜尋其他送件登錄作業
		qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb = new StringBuilder();
		chkList = new ArrayList<Map<String, Object>>();

		sb.append(" SELECT 1 ");
		sb.append(" FROM TBIOT_MAIN A ");
		sb.append(" WHERE A.CUST_ID = :custID "); //ID符合其他送件案件的要保人ID
		sb.append(" AND A.REG_TYPE = '3' AND A.OTH_TYPE = '2' "); //其他送件登錄，文件種類為"契變-解約/縮小保額"
		sb.append(" AND TRUNC(:applyDate) - 93 < TRUNC(A.DOC_KEYIN_DATE) AND TRUNC(A.DOC_KEYIN_DATE) <= TRUNC(SYSDATE) "); //新契約要保書申請日-93天<文件申請日<=系統日
		sb.append(" AND A.STATUS <> 99 "); //文件狀態不等於99(刪除)
		qc.setObject("custID", custID);
		qc.setObject("applyDate", new Timestamp(applyDate.getTime()));
		qc.setQueryString(sb.toString());
		chkList = dam_obj.exeQuery(qc);

		//其他送件登錄作業，A.文件種類=契變-解約/縮小保額 B.ID符合其他送件案件的要保人ID C.新契約要保書申請日-93天<文件申請日<=系統日
		String chk3 = CollectionUtils.isEmpty(chkList) ? "N" : "Y";

		//日盛保單解約檢核
		qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb = new StringBuilder();
		chkList = new ArrayList<Map<String, Object>>();

		sb.append(" SELECT 1 ");
		sb.append(" FROM TBJSB_INS_POLICY2 A ");
		sb.append(" LEFT JOIN TBJSB_INS_POLICY2STCHANGE B ON B.POLICYSERIALNUM = A.POLICYSERIALNUM ");
		sb.append(" WHERE A.PAYERIDNO = :custID "); //要保人ID
		sb.append(" AND B.POLICYSTATUSCODE = '08' "); //保單解約
		sb.append(" AND TRUNC(B.POLICYSTATUSCHANGEDATE) > TRUNC(:applyDate) - 93 "); //註記日期>要保書申請日-93天
		qc.setObject("custID", custID);
		qc.setObject("applyDate", new Timestamp(applyDate.getTime()));
		qc.setQueryString(sb.toString());
		chkList = dam_obj.exeQuery(qc);
		//日盛保單為保單解約狀態，且註記日期>要保書申請日-93天
		String chk4 = CollectionUtils.isEmpty(chkList) ? "N" : "Y";

		return ((StringUtils.equals("Y", chk1) || StringUtils.equals("Y", chk2)
				|| StringUtils.equals("Y", chk3) || StringUtils.equals("Y", chk4)) ? "Y" : "N");
	}

	/***
	 * 繳款人定存不打折檢核
	 * 以繳款人ID搜尋定存不打折檔(d:\bnk_d\cdwea01\vp\tdbd040)，要保書申請日 - 解定日 < 93 天，若符合條件則為”Y”，若無則為”N”
	 * @param inputVO
	 * @return
	 * @throws DAOException
	 * @throws JBranchException
	 */
	public String intCDChk(String custID, Date applyDate) throws DAOException, JBranchException {
		if(applyDate == null || StringUtils.isBlank(custID))
			return "";

		dam_obj = getDataAccessManager();
		QueryConditionIF qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();
		List<Map<String, Object>> chkList = new ArrayList<Map<String, Object>>();

		sb.append(" SELECT 1 FROM TBCRM_TDBD040 WHERE CUST_ID = :custID ");
		sb.append(" AND (TRUNC(TX_DATE) - TRUNC(:applyDate)) > -93 AND (TRUNC(TX_DATE) - TRUNC(:applyDate)) < 93 ");
		qc.setObject("custID", custID);
		qc.setObject("applyDate", new Timestamp(applyDate.getTime()));
		qc.setQueryString(sb.toString());
		chkList = dam_obj.exeQuery(qc);

		return (CollectionUtils.isEmpty(chkList) ? "N" : "Y");
	}
	
	/***
	 * 取得要保人/被保人/繳款人的定存單解約/到期日
	 * @param custID
	 * @param applyDate
	 * @return
	 * @throws DAOException
	 * @throws JBranchException
	 */
	public Date getCDDueDate(String custID, Date applyDate) throws DAOException, JBranchException {
		if(applyDate == null || StringUtils.isBlank(custID))
			return null;

		dam_obj = getDataAccessManager();
		QueryConditionIF qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();
		List<Map<String, Object>> dList = new ArrayList<Map<String, Object>>();

		sb.append(" SELECT TRUNC(MAX(CLS_DTE)) AS CLS_DTE FROM TBCRM_TDFD_CANCEL ");
		sb.append(" WHERE CUST_ID = :custID ");
		sb.append("  AND TRUNC(CLS_DTE) BETWEEN (TRUNC(:applyDate) -93) AND TRUNC(:applyDate) ");
		qc.setObject("custID", custID);
		qc.setObject("applyDate", new Timestamp(applyDate.getTime()));
		qc.setQueryString(sb.toString());
		dList = dam_obj.exeQuery(qc);

		return (CollectionUtils.isEmpty(dList) ? null : (Date)dList.get(0).get("CLS_DTE"));
	}

	/***
	 * 取得工作年收入(授信) -- 來源: 徵審系統
	 * 個金與法金都要看，取異動日期最近的那一筆收入
	 * @param custID
	 * @return
	 * @throws JBranchException
	 * @throws DAOException
	 */
	public BigDecimal getIncome3(String custID) throws DAOException, JBranchException {
		BigDecimal income3 = BigDecimal.ZERO;

		if(StringUtils.isBlank(custID))
			return income3;

		dam_obj = getDataAccessManager();
		QueryConditionIF qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();
		List<Map<String, Object>> chkList = new ArrayList<Map<String, Object>>();
		//20201027因法金徵審客戶ID檔會回傳多筆資訊，將子查詢語法的=改成in
		sb.append("SELECT UPD_DATETIME AS SNAP_DATE, NVL(ANNUAL_INCOME, 0) AS INCOME, 'TWD' AS CURR_CODE ");
		sb.append("  FROM TBCRM_TB_CASE_CRD_CUSTINFO ");	//個金徵審客戶資訊檔
		sb.append("  WHERE CUST_ID = :custId AND UPD_DATETIME IS NOT NULL ");
		sb.append("UNION ");
		sb.append("SELECT A.UPD_DT AS SNAP_DATE, (NVL(A.REAL_AMT, 0) * NVL(B.SEL_RATE, 1)) AS INCOME, A.REAL_CURR AS CURR_CODE ");
		sb.append("  FROM TBCRM_TB_CCSM_CUSTB A ");			//法金徵審客戶資訊檔(公司戶)
		sb.append("  LEFT JOIN TBPMS_IQ053 B ON B.CUR_COD = A.REAL_CURR AND B.MTN_DATE = (SELECT MAX(MTN_DATE) FROM TBPMS_IQ053) ");
		sb.append("WHERE A.CUST_SYSID IN (SELECT CUST_SYSID  ");
		sb.append("                       FROM TBCRM_TB_CCSM_ID_CUST ");	//法金徵審客戶ID檔
		sb.append("                      WHERE JCIC_CD = :custId) ");
		sb.append("      AND A.REAL_CURR IS NOT NULL AND A.UPD_DT IS NOT NULL ");
		sb.append("UNION ");
		sb.append("SELECT A.UPD_DT AS SNAP_DATE, (NVL(A.INCO_AMT, 0) * NVL(B.SEL_RATE, 1)) AS INCOME, A.INCO_CURR AS CURR_CODE ");
		sb.append("  FROM TBCRM_TB_CCSM_CUSTP A ");			//法金徵審客戶資訊檔(個人戶)
		sb.append("  LEFT JOIN TBPMS_IQ053 B ON B.CUR_COD = A.INCO_CURR AND B.MTN_DATE = (SELECT MAX(MTN_DATE) FROM TBPMS_IQ053) ");
		sb.append("WHERE A.CUST_SYSID IN (SELECT CUST_SYSID  ");
		sb.append("                       FROM TBCRM_TB_CCSM_ID_CUST ");	//法金徵審客戶ID檔
		sb.append("                      WHERE JCIC_CD = :custId) ");
		sb.append("      AND A.INCO_CURR IS NOT NULL AND A.UPD_DT IS NOT NULL ");
		sb.append("ORDER BY SNAP_DATE DESC ");
		qc.setObject("custId", custID);
		qc.setQueryString(sb.toString());
		chkList = dam_obj.exeQuery(qc);

		if(CollectionUtils.isNotEmpty(chkList)) {
			income3 = new BigDecimal(chkList.get(0).get("INCOME").toString());
		}

		return income3;
	}

	/***
	 * 取得行內貸款申請日 & 檢核客戶ID(要保人或被保人或繳款人)93天內是否有行內貸款申請
	 * @param custID
	 * @return
	 * @throws JBranchException
	 * @throws DAOException
	 */
	public Map<String, Object> getCustLoanDate(String custID, Date applyDate) throws DAOException, JBranchException {
		Map<String, Object> map = new HashMap<String, Object>();

		if(applyDate == null || StringUtils.isBlank(custID))
			return map;

		dam_obj = getDataAccessManager();
		QueryConditionIF qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();
		List<Map<String, Object>> chkList = new ArrayList<Map<String, Object>>();

		sb.append(" SELECT 1 FROM DUAL ");
		sb.append(" WHERE EXISTS (SELECT 1 FROM TBPMS_TB_CASE_MAIN WHERE CUST_ID = :custID AND RECEIVE_DATE IS NOT NULL AND PROD_KIND IN ('A', 'B', 'C') AND SUBSTR(CASE_NO, 7, 1) IN ('W', 'M', 'S')) ");
		sb.append("	  AND EXISTS (SELECT 1 FROM TBPMS_TB_CASE_MAIN WHERE CUST_ID = :custID AND RECEIVE_DATE IS NOT NULL AND PROD_KIND IN ('A', 'B', 'C') AND SUBSTR(CASE_NO, 7, 1) IN ('W', 'M', 'S') ");
		sb.append(" 		  	  AND (TRUNC(TO_DATE(RECEIVE_DATE, 'YYYYMMDD')) - TRUNC(:applyDate)) > -93 AND (TRUNC(TO_DATE(RECEIVE_DATE, 'YYYYMMDD')) - TRUNC(:applyDate)) < 93) ");
		sb.append(" UNION ALL ");
		sb.append(" SELECT 1 FROM DUAL ");
		sb.append(" WHERE EXISTS (SELECT 1 FROM TBPMS_TB_CASE_MAIN A LEFT JOIN TBPMS_TB_CASE_CHG_ITEM_CRD B ON A.CASE_NO = B.CASE_NO ");
		sb.append(" 				WHERE A.CUST_ID = :custID AND A.RECEIVE_DATE IS NOT NULL AND A.FLOW_TYPE IN ('MB', 'MC') AND B.DITEM_ID IN ('C1', 'J1', 'J3', 'J4', 'A1')) ");
		sb.append("	  AND EXISTS (SELECT 1 FROM TBPMS_TB_CASE_MAIN A LEFT JOIN TBPMS_TB_CASE_CHG_ITEM_CRD B ON A.CASE_NO = B.CASE_NO ");
		sb.append(" 				WHERE A.CUST_ID = :custID AND A.RECEIVE_DATE IS NOT NULL AND A.FLOW_TYPE IN ('MB', 'MC') AND B.DITEM_ID IN ('C1', 'J1', 'J3', 'J4', 'A1') ");
		sb.append(" 		  	  AND (TRUNC(TO_DATE(RECEIVE_DATE, 'YYYYMMDD')) - TRUNC(:applyDate)) > -93 AND (TRUNC(TO_DATE(RECEIVE_DATE, 'YYYYMMDD')) - TRUNC(:applyDate)) < 93) ");
		sb.append(" UNION ALL ");
		sb.append(" SELECT 1 FROM DUAL ");	//法金貸款起案日
		sb.append(" WHERE EXISTS (SELECT 1 FROM TBCRM_AST_LIB_DTL_CORP WHERE CUST_ID = :custID AND CRE_DT IS NOT NULL) ");
		sb.append("	  AND EXISTS (SELECT 1 FROM TBCRM_AST_LIB_DTL_CORP WHERE CUST_ID = :custID AND CRE_DT IS NOT NULL ");
		sb.append(" 		  	  AND (TRUNC(CRE_DT) - TRUNC(:applyDate)) > -93 AND (TRUNC(CRE_DT) - TRUNC(:applyDate)) < 93) ");
		qc.setObject("custID", custID);
		qc.setObject("applyDate", new Timestamp(applyDate.getTime()));
		qc.setQueryString(sb.toString());
		chkList = dam_obj.exeQuery(qc);

		map.put("isLoanApply", CollectionUtils.isEmpty(chkList) ? "N" : "Y");

		if(CollectionUtils.isEmpty(chkList)) {
			map.put("loanApplyDate", null);
		} else {
			qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb = new StringBuilder();

			sb.append(" SELECT * FROM ( ");
			sb.append(" 	SELECT TO_DATE(MAX(RECEIVE_DATE), 'YYYYMMDD') AS RECEIVE_DATE FROM TBPMS_TB_CASE_MAIN ");
			sb.append(" 	 WHERE CUST_ID = :custID AND RECEIVE_DATE IS NOT NULL AND PROD_KIND IN ('A', 'B', 'C') AND SUBSTR(CASE_NO, 7, 1) IN ('W', 'M', 'S') ");
			sb.append("  		AND (TRUNC(TO_DATE(RECEIVE_DATE, 'YYYYMMDD')) - TRUNC(:applyDate)) > -93 AND (TRUNC(TO_DATE(RECEIVE_DATE, 'YYYYMMDD')) - TRUNC(:applyDate)) < 93 ");
			sb.append(" 	UNION ALL ");
			sb.append("	    SELECT TO_DATE(MAX(A.RECEIVE_DATE), 'YYYYMMDD') AS RECEIVE_DATE FROM TBPMS_TB_CASE_MAIN A LEFT JOIN TBPMS_TB_CASE_CHG_ITEM_CRD B ON A.CASE_NO = B.CASE_NO ");
			sb.append(" 	 WHERE A.CUST_ID = :custID AND A.RECEIVE_DATE IS NOT NULL AND A.FLOW_TYPE IN ('MB', 'MC') AND B.DITEM_ID IN ('C1', 'J1', 'J3', 'J4', 'A1') ");
			sb.append(" 		AND (TRUNC(TO_DATE(RECEIVE_DATE, 'YYYYMMDD')) - TRUNC(:applyDate)) > -93 AND (TRUNC(TO_DATE(RECEIVE_DATE, 'YYYYMMDD')) - TRUNC(:applyDate)) < 93 ");
			sb.append(" 	UNION ALL ");
			sb.append(" 	SELECT CRE_DT AS RECEIVE_DATE FROM TBCRM_AST_LIB_DTL_CORP ");	//法金貸款起案日
			sb.append("		 WHERE CUST_ID = :custID AND CRE_DT IS NOT NULL ");
			sb.append("  		AND (TRUNC(CRE_DT) - TRUNC(:applyDate)) > -93 AND (TRUNC(CRE_DT) - TRUNC(:applyDate)) < 93 ");
			sb.append(" ) WHERE RECEIVE_DATE IS NOT NULL ");
			sb.append(" ORDER BY RECEIVE_DATE DESC ");
			qc.setObject("custID", custID);
			qc.setObject("applyDate", new Timestamp(applyDate.getTime()));
			qc.setQueryString(sb.toString());
			chkList = dam_obj.exeQuery(qc);

			map.put("loanApplyDate", CollectionUtils.isEmpty(chkList) ? null : (Date)chkList.get(0).get("RECEIVE_DATE"));
		}

		return map;
	}

	/***
	 * 取得要保人就保單提領保額/保價日
	 * @param custID
	 * @param applyDate
	 * @return
	 * @throws DAOException
	 * @throws JBranchException
	 */
	private Date getCustPremDate(String custID, Date applyDate) throws DAOException, JBranchException {
		if(applyDate == null || StringUtils.isBlank(custID))
			return null;

		dam_obj = getDataAccessManager();
		QueryConditionIF qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();
		List<Map<String, Object>> chkList = new ArrayList<Map<String, Object>>();

		sb.append(" SELECT MAX(TERMINATE_DATE) AS TERMINATE_DATE FROM TBCRM_NPOLM ");
		sb.append(" WHERE APPL_ID = :custID AND POLICY_STATUS <> '21' ");
		sb.append(" AND (TRUNC(TERMINATE_DATE) - TRUNC(:applyDate)) > -93 AND (TRUNC(TERMINATE_DATE) - TRUNC(:applyDate)) < 93 ");
		qc.setObject("custID", custID);
		qc.setObject("applyDate", new Timestamp(applyDate.getTime()));
		qc.setQueryString(sb.toString());
		chkList = dam_obj.exeQuery(qc);

		return (Date) (CollectionUtils.isEmpty(chkList) ? null : chkList.get(0).get("TERMINATE_DATE"));
	}

	/***
	 * 額度式貸款檢核
	 * 有額度式貸款且貸款餘額loan_bal <> 0，若符合條件則為”Y”，若無則為”N”
	 * 都無貸款資料，則回"N"
	 * @param inputVO
	 * @return
	 * @throws DAOException
	 * @throws JBranchException
	 */
	private String quotaLoanChk(String custID) throws DAOException, JBranchException {
		if(StringUtils.isBlank(custID))
			return "";

		dam_obj = getDataAccessManager();
		QueryConditionIF qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();
		List<Map<String, Object>> chkList = new ArrayList<Map<String, Object>>();

		sb.append(" SELECT 1 FROM TBCRM_AST_LIB_DTL ");
		sb.append(" WHERE CUST_ID = :custID AND LOAN_BAL <> 0 AND ACT_TYPE IN ('5001', '5002', '5004') ");
		qc.setObject("custID", custID);
		qc.setQueryString(sb.toString());
		chkList = dam_obj.exeQuery(qc);

		return (CollectionUtils.isEmpty(chkList) ? "N" : "Y");
	}

	/***
	 * 循環型貸款檢核
	 * CREDIT_TYPE=R為循環型貸款，若符合條件則為”Y”，若無則為”N”
	 * 都無貸款資料，則回"N"
	 * @param inputVO
	 * @return
	 * @throws DAOException
	 * @throws JBranchException
	 */
	private String revolingLoanChk(String custID) throws DAOException, JBranchException {
		if(StringUtils.isBlank(custID))
			return "";

		dam_obj = getDataAccessManager();
		QueryConditionIF qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();
		List<Map<String, Object>> chkList = new ArrayList<Map<String, Object>>();

		sb.append(" SELECT 1 FROM TBCRM_AST_LIB_DTL ");
		sb.append(" WHERE CUST_ID = :custID AND CREDIT_TYPE = 'R' ");
		qc.setObject("custID", custID);
		qc.setQueryString(sb.toString());
		chkList = dam_obj.exeQuery(qc);

		return (CollectionUtils.isEmpty(chkList) ? "N" : "Y");
	}
	
	/***
	 * 視訊投保影像品質檢核項目是否皆通過
	 * @param inputVO
	 * @return
	 * @throws DAOException
	 * @throws JBranchException
	 */
	public boolean validMAPPVideoCheck(IOT920InputVO inputVO) throws DAOException, JBranchException {
		dam_obj = getDataAccessManager();
		QueryConditionIF qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();

		//檢查是否全部皆為通過
		sb.append("SELECT (CASE WHEN A.COUNT = B.COUNT THEN 'Y' "); //全部通過(所有檢核項目數目=通過項目數目) =>通過檢核
		sb.append("	 			WHEN D.COUNT > 0 AND C.NP_REASON IS NOT NULL THEN 'Y' "); //有不通過項目且有填寫不通過原因 =>通過檢核
		sb.append("				ELSE 'N' END) AS PASS_YN "); //其他 =>不通過檢核
		sb.append(" FROM ");
		sb.append(" (SELECT COUNT(1) AS COUNT FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'IOT.MAPPVIDEO_CHK_CODE' AND PARAM_CODE NOT IN ('97', '98', '99')) A, "); //所有檢核項目數目
		sb.append(" (SELECT COUNT(1) AS COUNT FROM TBIOT_MAPPVIDEO_CHKLIST ");
		sb.append("		WHERE PREMATCH_SEQ = :prematchSeq AND CHK_STEP = :chkStep AND CHK_YN = 'Y' ");
		sb.append("		AND CHK_CODE IN (SELECT PARAM_CODE FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'IOT.MAPPVIDEO_CHK_CODE' AND PARAM_CODE NOT IN ('97', '98', '99'))) B, "); //通過項目數目
		sb.append(" (SELECT NP_REASON FROM TBIOT_MAPPVIDEO_CHKLIST WHERE PREMATCH_SEQ = :prematchSeq AND CHK_STEP = :chkStep AND CHK_CODE = '97') C, "); //不通過原因
		sb.append(" (SELECT COUNT(1) AS COUNT FROM TBIOT_MAPPVIDEO_CHKLIST ");
		sb.append("		WHERE PREMATCH_SEQ = :prematchSeq AND CHK_STEP = :chkStep AND CHK_YN = 'N' ");
		sb.append("		AND CHK_CODE IN (SELECT PARAM_CODE FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'IOT.MAPPVIDEO_CHK_CODE' AND PARAM_CODE NOT IN ('97', '98', '99'))) D "); //不通過項目數目

		qc.setObject("chkStep", inputVO.getMAPP_CHKLIST_TYPE());
		qc.setObject("prematchSeq", inputVO.getPREMATCH_SEQ());

		qc.setQueryString(sb.toString());
		List<Map<String, Object>> chkList = dam_obj.exeQuery(qc);

		return (CollectionUtils.isNotEmpty(chkList) && StringUtils.equals("Y", chkList.get(0).get("PASS_YN").toString()) ? true : false);
	}

	/**
	 * 取得客戶最近一次KYC總所得Q2答案
	 * @param custId
	 * @param applyDate
	 * @return
	 * @throws DAOException
	 * @throws JBranchException
	 */
	public String getKycQ2Answer(String custId, Date applyDate) throws DAOException, JBranchException {
		List<Map<String, Object>> list = getKycQ2AnswerInfo(custId, applyDate);	
		
		return CollectionUtils.isEmpty(list) ? "" : ObjectUtils.toString(list.get(0).get("ANSWER_DESC"));
	}
	
	/**
	 * 取得客戶最近一次KYC總所得Q2答案
	 * @param custId
	 * @param applyDate
	 * @return
	 * @throws DAOException
	 * @throws JBranchException
	 */
	public List<Map<String, Object>> getKycQ2AnswerInfo(String custId, Date applyDate) throws DAOException, JBranchException {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		
		if(applyDate == null || StringUtils.isBlank(custId))
			return list;

		dam_obj = getDataAccessManager();
		QueryConditionIF qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();

		sb.append("SELECT ANS.ANSWER_SEQ, ANS.ANSWER_DESC, R.CREATE_DATE ");
		sb.append(" FROM TBSYS_QST_ANSWER ANS ");
		sb.append(" INNER JOIN (SELECT C.QUESTION_VERSION, C.QST_NO AS QST_NO, ");
		sb.append("   	REPLACE(REPLACE(CASE WHEN DECODE(C.QST_NO, 1, 0, INSTR(B.ANSWER_2, ';', 1 , C.QST_NO - 1 ) + 1) - ");
		sb.append("   		DECODE(C.QST_NO, 1, -1, 0) + INSTR(B.ANSWER_2, ';', 1, C.QST_NO) - (DECODE(C.QST_NO, 1, 0, INSTR(B.ANSWER_2, ';', 1, C.QST_NO-1)+1)) = 0 ");
		sb.append("   		THEN TRIM(REPLACE(ANSWER_2 , REGEXP_SUBSTR(ANSWER_2 , '.*;') , '') ) ");
		sb.append("   	ELSE TRIM(SUBSTR(B.ANSWER_2, DECODE(C.QST_NO, 1, 0, INSTR(B.ANSWER_2, ';', 1 , C.QST_NO - 1 ) + 1), ");
		sb.append("   		DECODE(C.QST_NO, 1, -1, 0) + INSTR(B.ANSWER_2, ';', 1, C.QST_NO) - (DECODE(C.QST_NO, 1, 0, INSTR(B.ANSWER_2, ';', 1, C.QST_NO-1)+1)) ");
		sb.append("   	)) END,',','|'),' ','') RESULTA, A.CREATE_DATE ");
		sb.append(" 	FROM TBKYC_INVESTOREXAM_M_HIS A ");
		sb.append(" 	INNER JOIN TBKYC_INVESTOREXAM_D_HIS B on A.SEQ = B.SEQ ");
		sb.append(" 	INNER JOIN TBSYS_QUESTIONNAIRE C on A.EXAM_VERSION = C.EXAM_VERSION ");
		sb.append(" 	WHERE A.CUST_ID = :custId AND C.QST_NO = '2' AND A.STATUS = '03' ");
		sb.append("		  AND TRUNC(A.CREATE_DATE) >= ADD_MONTHS(TRUNC(:applyDate), -12) "); //KYC鍵機日在要保書申請日一年內
		sb.append("		  AND TRUNC(A.CREATE_DATE) <= TRUNC(:applyDate) ");
		sb.append("	) R ON ANS.QUESTION_VERSION = R.QUESTION_VERSION AND ANS.ANSWER_SEQ = R.RESULTA ");
		sb.append("	ORDER BY R.CREATE_DATE DESC ");

		qc.setObject("custId", custId);
		qc.setObject("applyDate", new Timestamp(applyDate.getTime()));
		qc.setQueryString(sb.toString());
		list = dam_obj.exeQuery(qc);
		
		return list;
	}

	/**
	 * 取得行內負債(不含信用卡): 與CRM681同
	 * @param custId
	 * @return
	 * @throws Exception
	 */
	public BigDecimal getFu(String custId) throws Exception {
		BigDecimal fuTotal = BigDecimal.ZERO;

		try {
			CRM681 crm681 = (CRM681) PlatformContext.getBean("crm681");
			CRM681InputVO inputVO = new CRM681InputVO();
			inputVO.setCust_id(custId);

			CRM681OutputVO outputVO = crm681.getFu(inputVO);
			BigDecimal fu = outputVO.getFuTotal();
			fuTotal = fu.divide(new BigDecimal(10000)); //以萬元為單位
		} catch(Exception e) {
//			return BigDecimal.ZERO;
		}

		return fuTotal;
	}
}
