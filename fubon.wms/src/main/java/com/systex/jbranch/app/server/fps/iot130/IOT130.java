package com.systex.jbranch.app.server.fps.iot130;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBIOT_DOC_CHKPK;
import com.systex.jbranch.app.common.fps.table.TBIOT_DOC_CHKVO;
import com.systex.jbranch.app.common.fps.table.TBIOT_FUND_LINKVO;
import com.systex.jbranch.app.common.fps.table.TBIOT_MAINVO;
import com.systex.jbranch.app.server.fps.iot120.IOT120InputVO;
import com.systex.jbranch.app.server.fps.iot120.IOT120OutputVO;
import com.systex.jbranch.app.server.fps.iot920.INSIDDataVO;
import com.systex.jbranch.app.server.fps.iot920.INSIDInputVOinputVO;
import com.systex.jbranch.app.server.fps.iot920.IOT920;
import com.systex.jbranch.app.server.fps.sot701.AcctVO;
import com.systex.jbranch.app.server.fps.sot701.CustAcctDataVO;
import com.systex.jbranch.app.server.fps.sot701.SOT701;
import com.systex.jbranch.app.server.fps.sot701.SOT701InputVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.fubon.commons.ValidUtil;
import com.systex.jbranch.fubon.commons.cbs.CBSService;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;




/**
 * IOT130
 *
 * @author Jimmy
 * @date 2016/09/13
 * @spec null
 */
@Component("iot130")
@Scope("request")
public class IOT130 extends FubonWmsBizLogic{
	@Autowired
	private CBSService cbsservice;
	Logger logger = LoggerFactory.getLogger(this.getClass());

	DataAccessManager dam_obj;

	public void retrieve_data(Object body, IPrimitiveMap<Object> header) throws JBranchException{
		IOT130InputVO inputVO = (IOT130InputVO) body;
		IOT130OutputVO outputVO = new IOT130OutputVO();;
		dam_obj = this.getDataAccessManager();
		QueryConditionIF qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();
//		try {
			sb.append(" Select A.REG_TYPE,A.INS_ID,to_char(A.KEYIN_DATE,'YYYY/MM/DD hh:mm:ss') as KEYIN_DATE,A.BRANCH_NBR,A.RECRUIT_ID,A.OTH_TYPE,A.AO_CODE,A.APPLY_DATE, ");
			sb.append(" A.POLICY_NO1,A.POLICY_NO2,A.POLICY_NO3,A.CUST_ID,A.PROPOSER_NAME,A.PROPOSER_BIRTH,A.PROPOSER_CM_FLAG,A.INSURED_ID,A.INSURED_NAME,A.INSURED_CM_FLAG, ");
			sb.append(" 'NIL' REPRESET_ID_CM_FLAG,A.REPRESET_ID,A.RLT_BT_PROREP, (SELECT PROD_NAME || '(' || PROD_PERIOD || ')' FROM TBPRD_FX_DISCOUNT_PROD WHERE FXD_KEYNO = A.FXD_KEYNO) FX_PROD, ");
			sb.append(" (SELECT FXD_KEYNO FROM TBPRD_FX_DISCOUNT_PROD WHERE FXD_KEYNO = A.FXD_KEYNO) FXD_KEYNO,A.REMARK_INS, ");
			sb.append(" A.TERMINATED_INC,A.STATUS,A.INS_KEYNO,A.INSPRD_KEYNO, ");
			sb.append(" (CASE WHEN A.FB_COM_YN = 'Y' THEN PRD.INSPRD_ID ELSE JPRD.PRODUCTID END) AS INSPRD_ID, ");
			sb.append(" (CASE WHEN A.FB_COM_YN = 'Y' THEN PRD.INSPRD_NAME ELSE JPRD.PRODUCTNAME END) AS INSPRD_NAME, ");
			sb.append(" (CASE WHEN A.FB_COM_YN = 'Y' THEN PRD.INSPRD_TYPE ELSE (CASE WHEN TRIM(JPRD.PRODUCTTYPE1) <> '投資型' THEN '1' ELSE '2' END) END) AS INSPRD_TYPE, ");
			sb.append(" A.LOAN_PRD_YN, A.QC_IMMI, A.PREMATCH_SEQ, A.GUILD_RPT_DATE, A.NOT_PASS_REASON, A.PREMIUM_TRANSSEQ, A.QC_PROPOSER_CHG, ");
			sb.append(" A.PREMIUM_USAGE, A.PAY_WAY, A.PAYER_ID, A.RLT_BT_PROPAY, A.LOAN_SOURCE_YN, A.DOC_KEYIN_DATE, P.AML, P.PRECHECK, P.BUSINESS_REL, ");
			sb.append(" A.VALID_CHG_CUST_YN, P.CHG_CUST_ID, P.C_SENIOR_PVAL, B.CNAME AS INS_COM_NAME, ");
			sb.append(" A.FB_COM_YN, A.COMPANY_NUM, A.REVISE_CONFIRM_YN, ");
			sb.append(" A.OTH_FUND_PURPOSE_1, A.OTH_FUND_PURPOSE_2, A.OTH_FUND_PURPOSE_3, A.OTH_FUND_PURPOSE_4, A.OTH_FUND_PURPOSE_5, A.OTH_FUND_PURPOSE_6, ");
			sb.append(" A.OTH_FUND_PURPOSE_RMK_1, A.OTH_FUND_PURPOSE_RMK_2, (CASE WHEN P.STATUS = '3' THEN 'Y' ELSE 'N' END) AS AUTH_YN ");
			sb.append(" From TBIOT_MAIN A ");
			sb.append(" LEFT JOIN TBPRD_INS_MAIN PRD ON A.INSPRD_KEYNO = PRD.INSPRD_KEYNO ");
			sb.append(" LEFT JOIN TBIOT_PREMATCH P ON P.PREMATCH_SEQ = A.PREMATCH_SEQ ");
			sb.append(" LEFT JOIN TBJSB_INS_PROD_COMPANY B ON B.SERIALNUM = A.COMPANY_NUM ");
			sb.append(" LEFT JOIN TBJSB_INS_PROD_MAIN JPRD ON JPRD.PRODUCTSERIALNUM = A.INSPRD_KEYNO ");
			sb.append(" Where A.INS_KEYNO = :in_INSKEYNO ");

			qc.setObject("in_INSKEYNO", inputVO.getINS_KEYNO());
			qc.setQueryString(sb.toString());
			List<Map<String, Object>> list = dam_obj.exeQuery(qc);
			outputVO.setINS_INFORMATION(list);

			// 投資型連結標的清單
			List<Map<String, Object>> invlist = new ArrayList<Map<String, Object>>();
			String prematchSeq = "";
			if(CollectionUtils.isNotEmpty(list)) {
				prematchSeq = ObjectUtils.toString(list.get(0).get("PREMATCH_SEQ"));
			}

			dam_obj = this.getDataAccessManager();
			qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sb = new StringBuilder();
			sb.append(" select b.TARGET_ID,b.LINKED_NAME,a.LINK_PCT as ALLOCATION_RATIO,b.PRD_RISK,a.INS_KEYNO,a.PRD_LK_KEYNO ");
			sb.append(" from TBIOT_FUND_LINK a,TBPRD_INS_LINKING b ");
			sb.append(" where a.PRD_LK_KEYNO = b.KEY_NO and (a.INS_KEYNO = :ins_keyno OR a.PREMATCH_SEQ  = :prematchSeq) ");
			qc.setObject("ins_keyno", inputVO.getINS_KEYNO());
			qc.setObject("prematchSeq", prematchSeq);
			qc.setQueryString(sb.toString());
			invlist = dam_obj.exeQuery(qc);

			outputVO.setINVESTList(invlist);

			sendRtnObject(outputVO);
//		} catch (Exception e) {
//			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
//			throw new APException("系統發生錯誤請洽系統管理員");
//		}

	}

	public void POLICY_NO_Query(Object body, IPrimitiveMap<Object> header) throws JBranchException{
		IOT130InputVO inputVO = (IOT130InputVO) body;
		IOT130OutputVO outputVO = new IOT130OutputVO();
		List<Map<String, Object>> PRDList = new ArrayList<Map<String,Object>>();
		dam_obj = this.getDataAccessManager();
		QueryConditionIF qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();

		sb.append(" SELECT M.CUST_ID, M.APPL_NAME, M.INS_ID, M.INS_NAME, M.INS_TYPE, M.INS_COMP_NAME, PRD.INSPRD_TYPE ");
		if(StringUtils.equals("Y", inputVO.getFB_COM_YN())) { //富壽
			sb.append(" FROM TBCRM_AST_INS_MAST M ");
		} else {
			sb.append(" FROM TBJSB_AST_INS_MAST M ");
		}
		sb.append(" LEFT JOIN TBPRD_INS_MAIN PRD ON PRD.INSPRD_ID = M.INS_TYPE ");
		sb.append(" WHERE M.POLICY_NBR =:POLICY_NO1 ");
		qc.setObject("POLICY_NO1", inputVO.getPOLICY_NO1());

		//POLICY_NO1第一碼為英文字母才需要檢查POLICY_NO2
		if(StringUtils.isNotBlank(inputVO.getPOLICY_NO1()) && inputVO.getPOLICY_NO1().substring(0, 1).matches("[a-zA-Z]")) {
			sb.append(" AND M.POLICY_SEQ = :POLICY_NO2 ");
			qc.setObject("POLICY_NO2", inputVO.getPOLICY_NO2());
		}

		if(StringUtils.isNotBlank(inputVO.getPOLICY_NO3())) {
			sb.append(" AND M.ID_DUP =:POLICY_NO3 ");
			qc.setObject("POLICY_NO3", inputVO.getPOLICY_NO3());
		} else if(StringUtils.equals("Y", inputVO.getFB_COM_YN())) {
			sb.append(" AND M.ID_DUP =:POLICY_NO3 ");
			qc.setObject("POLICY_NO3", " ");
		}

		qc.setQueryString(sb.toString());
		PRDList = dam_obj.exeQuery(qc);
		outputVO.setPOLICY_Data(PRDList);
		sendRtnObject(outputVO);
	}

	public void queryCNCT(Object body, IPrimitiveMap<Object> header) throws JBranchException{
		IOT130InputVO inputVO = (IOT130InputVO) body;
		IOT130OutputVO outputVO = new IOT130OutputVO();
		dam_obj = this.getDataAccessManager();
		QueryConditionIF qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();
		try {
			sb.append(" select * from TBPRD_INS_MAIN ");
			sb.append(" where EFFECT_DATE<sysdate and EXPIRY_DATE>=sysdate-1 and MAIN_RIDER='M' ");
			sb.append(" and INSPRD_ID like :insprd_id ");
			qc.setObject("insprd_id", "%"+inputVO.getINSPRD_ID()+"%");
			qc.setQueryString(sb.toString());
			outputVO.setCNCTList(dam_obj.exeQuery(qc));
			sendRtnObject(outputVO);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}


	public void chkData_5(Object body, IPrimitiveMap<Object> header) throws JBranchException{
		IOT130InputVO inputVO = (IOT130InputVO) body;
		IOT130OutputVO outputVO = new IOT130OutputVO();
		dam_obj = this.getDataAccessManager();
		QueryConditionIF qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();
		try {

		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}

	public void chkData(Object body, IPrimitiveMap<Object> header) throws JBranchException{
		IOT130InputVO inputVO = (IOT130InputVO) body;
		IOT130OutputVO outputVO = new IOT130OutputVO();
		dam_obj = this.getDataAccessManager();
		QueryConditionIF qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();
		try {
			sb.append(" select 'x' from TBORG_MEMBER_CERT ");
			sb.append(" where CERTIFICATE_CODE='01' ");
			sb.append(" and emp_id = :recruit_id ");
			qc.setObject("recruit_id", inputVO.getRECRUIT_ID());
			qc.setQueryString(sb.toString());
			outputVO.setChkData(dam_obj.exeQuery(qc));
			ValidUtil.isValidIDorRCNumber(inputVO.getCUST_ID());
			sendRtnObject(outputVO);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}




	public void submit(Object body, IPrimitiveMap<Object> header) throws JBranchException{
		IOT920 iot920 = (IOT920) PlatformContext.getBean("iot920");
		INSIDInputVOinputVO iot920_inputVO = new INSIDInputVOinputVO();
		INSIDDataVO iot920_outputVO = new INSIDDataVO();
		IOT130InputVO inputVO = (IOT130InputVO) body;
		IOT130OutputVO outputVO = new IOT130OutputVO();
		String loginbrh = (String) getCommonVariable(SystemVariableConsts.LOGINBRH);
		String INS_ID = "";
		String SIGN_INC = "N";
		String errorMsg = "";
		iot920_inputVO.setBRANCH_NBR(loginbrh);
		iot920_inputVO.setINS_KIND("1");
		iot920_inputVO.setREG_TYPE(inputVO.getReg_type());
		if("new".equals(inputVO.getOPR_STATUS())){

			try {
				List<Map<String, Object>> INS_KEYNOList = new ArrayList<Map<String,Object>>();
				dam_obj = this.getDataAccessManager();
				QueryConditionIF qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				StringBuilder sb = new StringBuilder();
				sb.append(" select TBIOT_MAIN_SEQ.NEXTVAL from dual ");
				qc.setQueryString(sb.toString());
				INS_KEYNOList = dam_obj.exeQuery(qc);
				String INS_KEYNO = INS_KEYNOList.get(0).get("NEXTVAL").toString();
				BigDecimal keyno_change = new BigDecimal(INS_KEYNO);

				//文件檢核
				int in_DOC_N=0;
				int out_DOC_N=0;

				if(inputVO.getInList() != null){
					INSERT_TBIOT_DOC(inputVO.getInList(), keyno_change, "1");

					for(Map<String, Object> INitem:inputVO.getInList()){
						if("1".equals(INitem.get("DOC_LEVEL")) && "N".equals(INitem.get("DOC_CHK"))){
							inputVO.setSTATUS("10");
							in_DOC_N++;
						}
						if("Y".equals(INitem.get("SIGN_INC"))  && "Y".equals(INitem.get("DOC_CHK"))){
							SIGN_INC = "Y";
						}
						if(StringUtils.equals("Y", inputVO.getBUSINESS_REL()) && (Double)INitem.get("DOC_SEQ") == 1) {
							if(!"Y".equals(INitem.get("DOC_CHK"))) {
								errorMsg = "分行留存文件應檢附「洗錢防制姓名檢核結果」";
								throw new APException(errorMsg);
							}
						}
					}
				}

				if(inputVO.getOutList() != null){
					INSERT_TBIOT_DOC(inputVO.getOutList(), keyno_change, "2");

					for(Map<String, Object> OUTitem:inputVO.getOutList()){
						if("1".equals(OUTitem.get("DOC_LEVEL")) && "N".equals(OUTitem.get("DOC_CHK"))){
							inputVO.setSTATUS("10");
							out_DOC_N++;
						}
						if("Y".equals(OUTitem.get("SIGN_INC")) && "Y".equals(OUTitem.get("DOC_CHK"))){
							SIGN_INC = "Y";
						}
					}
				}

				if(in_DOC_N<=0 && out_DOC_N <=0){
					inputVO.setSTATUS("20");
				}

				//若要保人有變更，要保人適合度檢核編碼的保單號碼與此輸入畫面的保單號碼不同時，需提示訊息”檢核與送件的保單號碼不同請跟理專確認”
				if(StringUtils.equals("Y", inputVO.getQC_PROPOSER_CHG()) && StringUtils.isNotBlank(inputVO.getPREMATCH_SEQ())) {
					dam_obj = this.getDataAccessManager();
					qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					sb = new StringBuilder();
					sb.append(" SELECT * FROM TBIOT_PREMATCH WHERE PREMATCH_SEQ = :prematchSeq ");
					qc.setObject("prematchSeq", inputVO.getPREMATCH_SEQ());
					qc.setQueryString(sb.toString());
					List<Map<String, Object>> pList = dam_obj.exeQuery(qc);
					if(CollectionUtils.isEmpty(pList)) {
						errorMsg = "請至要保人適合度檢核執行壽險新契約";
						throw new APException(errorMsg);
					} else {
						if(StringUtils.isNotBlank(inputVO.getPOLICY_NO1()) && inputVO.getPOLICY_NO1().substring(0, 1).matches("[a-zA-Z]")) {
							if(!StringUtils.equals(ObjectUtils.toString(inputVO.getPOLICY_NO1().toUpperCase()), ObjectUtils.toString(pList.get(0).get("POLICY_NO1")).toUpperCase()) ||
							   !StringUtils.equals(ObjectUtils.toString(inputVO.getPOLICY_NO2()), ObjectUtils.toString(pList.get(0).get("POLICY_NO2"))) ||
							   !StringUtils.equals(ObjectUtils.toString(inputVO.getPOLICY_NO3()), ObjectUtils.toString(pList.get(0).get("POLICY_NO3")))) {
								errorMsg = "檢核與送件的保單號碼不同請跟理專確認";
								throw new APException(errorMsg);
							}
						} else {
							//若保單號碼第一碼為數字，只需檢查POLICY_NO1
							if(!StringUtils.equals(ObjectUtils.toString(inputVO.getPOLICY_NO1().toUpperCase()), ObjectUtils.toString(pList.get(0).get("POLICY_NO1")).toUpperCase())) {
								errorMsg = "檢核與送件的保單號碼不同請跟理專確認";
								throw new APException(errorMsg);
							}
						}
					}
				}

				//繳費用途勾選•首期保險費，需須依繳款人ID+以保單號碼勾稽保險文件編號檢核
				if(StringUtils.equals("1", inputVO.getPREMIUM_USAGE())) {
					dam_obj = this.getDataAccessManager();
					qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					sb = new StringBuilder();
					sb.append(" SELECT * FROM TBIOT_PREMATCH WHERE INS_KIND = '1' AND REG_TYPE = '1' ");
					sb.append(" AND POLICY_NO1 = :policyNo1 AND POLICY_NO2 = :policyNo2 AND POLICY_NO3 = :policyNo3 ");
					qc.setObject("policyNo1", inputVO.getPOLICY_NO1());
					qc.setObject("policyNo2", inputVO.getPOLICY_NO2());
					qc.setObject("policyNo3", inputVO.getPOLICY_NO3());
					qc.setQueryString(sb.toString());
					List<Map<String, Object>> pList = dam_obj.exeQuery(qc);
					if(CollectionUtils.isEmpty(pList)) {
						errorMsg = "請至要保人適合度檢核執行壽險新契約";
						throw new APException(errorMsg);
					} else if(!StringUtils.equals(inputVO.getPAYER_ID(), pList.get(0).get("PAYER_ID").toString())) {
						errorMsg = "因繳款人有異動，請理專至要保人適合度檢核修改繳款人資料，適合度編碼為" + pList.get(0).get("PREMATCH_SEQ").toString();
						throw new APException(errorMsg);
					}
				}

				dam_obj = this.getDataAccessManager();
				TBIOT_MAINVO tmvo = new TBIOT_MAINVO();
				tmvo.setRECRUIT_ID(inputVO.getRECRUIT_ID());
				tmvo.setINS_KEYNO(keyno_change);
				tmvo.setINS_KIND("1");

				tmvo.setREG_TYPE(inputVO.getReg_type());
				tmvo.setPOLICY_NO1(inputVO.getPOLICY_NO1());
				tmvo.setPOLICY_NO2(inputVO.getPOLICY_NO2());
				tmvo.setPOLICY_NO3(inputVO.getPOLICY_NO3());
				tmvo.setOTH_TYPE(inputVO.getOTH_TYPE());
				tmvo.setBRANCH_NBR(loginbrh);
				tmvo.setCUST_ID(inputVO.getCUST_ID().toUpperCase());
				tmvo.setPROPOSER_NAME(inputVO.getPROPOSER_NAME().toUpperCase());

				if(inputVO.getINSURED_ID() != null)
					tmvo.setINSURED_ID(inputVO.getINSURED_ID().toUpperCase());
				else
					tmvo.setINSURED_ID("");
				tmvo.setINSURED_NAME(inputVO.getINSURED_NAME().toUpperCase());
				if(inputVO.getREPRESET_ID()!= null){
					tmvo.setREPRESET_ID(inputVO.getREPRESET_ID().toUpperCase());
				}
				tmvo.setRLT_BT_PROREP(inputVO.getREPRESET_CUST());
				if(StringUtils.isNotBlank(inputVO.getINSPRD_KEYNO())){
					BigDecimal insprd_keyno = new BigDecimal(inputVO.getINSPRD_KEYNO());
					tmvo.setINSPRD_KEYNO(insprd_keyno);
				}

				tmvo.setAO_CODE(inputVO.getAO_CODE());
				tmvo.setPROPOSER_CM_FLAG(inputVO.getPROPOSER_CM_FLAG());
				tmvo.setINSURED_CM_FLAG(inputVO.getINSURED_CM_FLAG());
				if(inputVO.getAPPLY_DATE() != null) {
					Timestamp apply_date = new Timestamp(inputVO.getAPPLY_DATE().getTime());
					tmvo.setAPPLY_DATE(apply_date);
				}
				SimpleDateFormat keyin_sdf = new SimpleDateFormat("yyyy-MM-dd");
				String now_change = keyin_sdf.format(new Date());
				Date now = keyin_sdf.parse(now_change);
				Timestamp keyin_date = new Timestamp(now.getTime());
				tmvo.setKEYIN_DATE(keyin_date);
				if(inputVO.getMATCH_DATE() != null){
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Date MATCH_DATE = sdf.parse(inputVO.getMATCH_DATE());
					tmvo.setMATCH_DATE(new Timestamp(MATCH_DATE.getTime()));
				}
				if(inputVO.getPROPOSER_RISK() != null){
					tmvo.setPROPOSER_RISK(inputVO.getPROPOSER_RISK());
				}
				BigDecimal status = new BigDecimal(inputVO.getSTATUS());
				tmvo.setSTATUS(status);
				Date OP_DATE_NOW = new Date();

				if("20".equals(inputVO.getSTATUS().toString())){
					tmvo.setOP_DATE(new Timestamp(OP_DATE_NOW.getTime()));
				}
				tmvo.setTERMINATED_INC(inputVO.getTERMINATED_INC());
				if(!"".equals(SIGN_INC)){
					tmvo.setSIGN_INC(SIGN_INC);
				}else{
					tmvo.setSIGN_INC("N");
				}

				iot920_outputVO = iot920.getINS_ID(iot920_inputVO);
				INS_ID = iot920_outputVO.getINS_ID().toString();
				tmvo.setINS_ID(INS_ID);

				// 2018/1/9 change PROPOSER_BIRDAY
				if(inputVO.getPROPOSER_BIRDAY() != null) {
					Calendar calendar = new GregorianCalendar();
					calendar.setTime(inputVO.getPROPOSER_BIRDAY());
					calendar.set(Calendar.HOUR_OF_DAY, 0);
					calendar.set(Calendar.MINUTE, 0);
					calendar.set(Calendar.SECOND, 0);
					calendar.set(Calendar.MILLISECOND, 0);
					tmvo.setPROPOSER_BIRTH(new Timestamp(calendar.getTimeInMillis()));
				}

				tmvo.setPREMATCH_SEQ(inputVO.getPREMATCH_SEQ());
				tmvo.setLOAN_PRD_YN(inputVO.getLOAN_PRD_YN());
				tmvo.setQC_IMMI(inputVO.getQC_IMMI());
				tmvo.setNOT_PASS_REASON(inputVO.getNOT_PASS_REASON());
				tmvo.setPREMIUM_TRANSSEQ(inputVO.getPREMIUM_TRANSSEQ());
				tmvo.setQC_PROPOSER_CHG(inputVO.getQC_PROPOSER_CHG());
				tmvo.setPREMIUM_USAGE(inputVO.getPREMIUM_USAGE());
				tmvo.setPAY_WAY(inputVO.getPAY_WAY());
				tmvo.setPAYER_ID(inputVO.getPAYER_ID());
				tmvo.setRLT_BT_PROPAY(inputVO.getRLT_BT_PROPAY());
				if(inputVO.getDOC_KEYIN_DATE() != null) {
					tmvo.setDOC_KEYIN_DATE(new Timestamp(inputVO.getDOC_KEYIN_DATE().getTime()));
				}
				tmvo.setLOAN_SOURCE_YN(inputVO.getLOAN_SOURCE_YN());
				tmvo.setVALID_CHG_CUST_YN(inputVO.getVALID_CHG_CUST_YN());
				tmvo.setFB_COM_YN(inputVO.getFB_COM_YN());
				tmvo.setCOMPANY_NUM(inputVO.getCOMPANY_NUM());
				tmvo.setREVISE_CONFIRM_YN(inputVO.getREVISE_CONFIRM_YN());
				tmvo.setOTH_FUND_PURPOSE_1(inputVO.getOTH_FUND_PURPOSE_1());
				tmvo.setOTH_FUND_PURPOSE_2(inputVO.getOTH_FUND_PURPOSE_2());
				tmvo.setOTH_FUND_PURPOSE_3(inputVO.getOTH_FUND_PURPOSE_3());
				tmvo.setOTH_FUND_PURPOSE_4(inputVO.getOTH_FUND_PURPOSE_4());
				tmvo.setOTH_FUND_PURPOSE_5(inputVO.getOTH_FUND_PURPOSE_5());
				tmvo.setOTH_FUND_PURPOSE_6(inputVO.getOTH_FUND_PURPOSE_6());
				tmvo.setOTH_FUND_PURPOSE_RMK_1(inputVO.getOTH_FUND_PURPOSE_RMK_1());
				tmvo.setOTH_FUND_PURPOSE_RMK_2(inputVO.getOTH_FUND_PURPOSE_RMK_2());
				
				dam_obj.create(tmvo);

				outputVO.setINS_ID(INS_ID);
				outputVO.setINSKEY_NO(INS_KEYNO);
				sendRtnObject(outputVO);
			} catch (Exception e) {
				if(errorMsg.length()>0){
					throw new APException(errorMsg);
				}else{
					logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
					throw new APException("系統發生錯誤請洽系統管理員"+StringUtil.getStackTraceAsString(e));
				}
			}
		}else if("UPDATE".equals(inputVO.getOPR_STATUS())){
			dam_obj = getDataAccessManager();
			QueryConditionIF qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuilder sb = new StringBuilder();
			BigDecimal ins_keyno_change = new BigDecimal(inputVO.getINS_KEYNO());


			//文件檢核
			int in_DOC_N=0;
			int out_DOC_N=0;

			//文件種類更改時，留存文件清除
			if(inputVO.isEditOTH_TYPE()){
				dam_obj = getDataAccessManager();
				qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sb = new StringBuilder();
				sb.append(" DELETE TBIOT_DOC_CHK where INS_KEYNO = :ins_keyno ");
				qc.setObject("ins_keyno", ins_keyno_change);
				qc.setQueryString(sb.toString());
				dam_obj.exeUpdate(qc);
			}

			if(inputVO.getInList() != null){
				//文件種類更改時，留存文件重新新增
				if(inputVO.isEditOTH_TYPE()){
					INSERT_TBIOT_DOC(inputVO.getInList(), ins_keyno_change, "1");
				}

				for(Map<String, Object> INitem:inputVO.getInList()){
					if("1".equals(INitem.get("DOC_LEVEL")) && "N".equals(INitem.get("DOC_CHK"))){
						inputVO.setSTATUS("10");
						in_DOC_N++;
					}
					if("Y".equals(INitem.get("SIGN_INC")) && "Y".equals(INitem.get("DOC_CHK"))){
						SIGN_INC = "Y";
					}
					if(StringUtils.equals("Y", inputVO.getBUSINESS_REL()) && (Double)INitem.get("DOC_SEQ") == 1) {
						if(!"Y".equals(INitem.get("DOC_CHK"))) {
							errorMsg = "分行留存文件應檢附「洗錢防制姓名檢核結果」";
							throw new APException(errorMsg);
						}
					}
				}
			}
			if(inputVO.getOutList() != null){
				//文件種類更改時，保險文件重新新增
				if(inputVO.isEditOTH_TYPE()){
					INSERT_TBIOT_DOC(inputVO.getOutList(), ins_keyno_change, "2");
				}

				for(Map<String, Object> OUTitem:inputVO.getOutList()){
					if("1".equals(OUTitem.get("DOC_LEVEL")) && "N".equals(OUTitem.get("DOC_CHK"))){
						inputVO.setSTATUS("10");
						out_DOC_N++;
					}
					if("Y".equals(OUTitem.get("SIGN_INC")) && "Y".equals(OUTitem.get("DOC_CHK"))){
						SIGN_INC = "Y";
					}
				}
			}
			if(in_DOC_N<=0 && out_DOC_N <=0){
				inputVO.setSTATUS("20");
			}

			//若要保人有變更，要保人適合度檢核編碼的保單號碼與此輸入畫面的保單號碼不同時，需提示訊息”檢核與送件的保單號碼不同請跟理專確認”
			if(StringUtils.equals("Y", inputVO.getQC_PROPOSER_CHG()) && StringUtils.isNotBlank(inputVO.getPREMATCH_SEQ())) {
				dam_obj = this.getDataAccessManager();
				qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sb = new StringBuilder();
				sb.append(" SELECT * FROM TBIOT_PREMATCH WHERE PREMATCH_SEQ = :prematchSeq ");
				qc.setObject("prematchSeq", inputVO.getPREMATCH_SEQ());
				qc.setQueryString(sb.toString());
				List<Map<String, Object>> pList = dam_obj.exeQuery(qc);
				if(CollectionUtils.isEmpty(pList)) {
					errorMsg = "請至要保人適合度檢核執行壽險新契約";
					throw new APException(errorMsg);
				} else {
					if(StringUtils.isNotBlank(inputVO.getPOLICY_NO1()) && inputVO.getPOLICY_NO1().substring(0, 1).matches("[a-zA-Z]")) {
						if(!StringUtils.equals(ObjectUtils.toString(inputVO.getPOLICY_NO1().toUpperCase()), ObjectUtils.toString(pList.get(0).get("POLICY_NO1")).toUpperCase()) ||
						   !StringUtils.equals(ObjectUtils.toString(inputVO.getPOLICY_NO2()), ObjectUtils.toString(pList.get(0).get("POLICY_NO2"))) ||
						   !StringUtils.equals(ObjectUtils.toString(inputVO.getPOLICY_NO3()), ObjectUtils.toString(pList.get(0).get("POLICY_NO3")))) {
							errorMsg = "檢核與送件的保單號碼不同請跟理專確認";
							throw new APException(errorMsg);
						}
					} else {
						//若保單號碼第一碼為數字，只需檢查POLICY_NO1
						if(!StringUtils.equals(ObjectUtils.toString(inputVO.getPOLICY_NO1().toUpperCase()), ObjectUtils.toString(pList.get(0).get("POLICY_NO1")).toUpperCase())) {
							errorMsg = "檢核與送件的保單號碼不同請跟理專確認";
							throw new APException(errorMsg);
						}
					}
				}
			}

			//繳費用途勾選•首期保險費，需須依繳款人ID+以保單號碼勾稽保險文件編號檢核
			if(StringUtils.equals("1", inputVO.getPREMIUM_USAGE())) {
				dam_obj = this.getDataAccessManager();
				qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sb = new StringBuilder();
				sb.append(" SELECT * FROM TBIOT_PREMATCH WHERE INS_KIND = '1' AND REG_TYPE = '1' ");
				sb.append(" AND POLICY_NO1 = :policyNo1 AND POLICY_NO2 = :policyNo2 AND POLICY_NO3 = :policyNo3 ");
				qc.setObject("policyNo1", inputVO.getPOLICY_NO1());
				qc.setObject("policyNo2", inputVO.getPOLICY_NO2());
				qc.setObject("policyNo3", inputVO.getPOLICY_NO3());
				qc.setQueryString(sb.toString());
				List<Map<String, Object>> pList = dam_obj.exeQuery(qc);
				if(CollectionUtils.isEmpty(pList)) {
					throw new APException("請至要保人適合度檢核執行壽險新契約");
				} else if(!StringUtils.equals(inputVO.getPAYER_ID(), pList.get(0).get("PAYER_ID").toString())) {
					throw new APException("因繳款人有異動，請理專至要保人適合度檢核修改繳款人資料，適合度編碼為" + pList.get(0).get("PREMATCH_SEQ").toString());
				}
			}

			try {
//				List<Map<String, Object>> INS_KEYNOList = new ArrayList<Map<String,Object>>();
//				dam_obj = this.getDataAccessManager();
//				QueryConditionIF qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
//				StringBuilder sb = new StringBuilder();
//				sb.append(" select TBIOT_MAIN_SEQ.NEXTVAL from dual ");
//				qc.setQueryString(sb.toString());
//				INS_KEYNOList = dam_obj.exeQuery(qc);
//				String INS_KEYNO = INS_KEYNOList.get(0).get("NEXTVAL").toString();
//				BigDecimal keyno_change = new BigDecimal(INS_KEYNO);
				dam_obj = this.getDataAccessManager();
				TBIOT_MAINVO tmvo = new TBIOT_MAINVO();
				tmvo = (TBIOT_MAINVO) dam_obj.findByPKey(TBIOT_MAINVO.TABLE_UID, ins_keyno_change);
				tmvo.setRECRUIT_ID(inputVO.getRECRUIT_ID());
				tmvo.setINS_KIND("1");
//				tmvo.setINS_ID(INS_ID);
				tmvo.setREG_TYPE(inputVO.getReg_type());
				tmvo.setPOLICY_NO1(inputVO.getPOLICY_NO1());
				tmvo.setPOLICY_NO2(inputVO.getPOLICY_NO2());
				tmvo.setPOLICY_NO3(inputVO.getPOLICY_NO3());
				tmvo.setOTH_TYPE(inputVO.getOTH_TYPE());
				tmvo.setBRANCH_NBR(loginbrh);
				tmvo.setCUST_ID(inputVO.getCUST_ID().toUpperCase());
				tmvo.setPROPOSER_NAME(inputVO.getPROPOSER_NAME().toUpperCase());

				if(inputVO.getINSURED_ID() != null){
					tmvo.setINSURED_ID(inputVO.getINSURED_ID().toUpperCase());
				}
				tmvo.setINSURED_NAME(inputVO.getINSURED_NAME().toUpperCase());
				if(StringUtils.isNotBlank(inputVO.getREPRESET_ID())){
					tmvo.setREPRESET_ID(inputVO.getREPRESET_ID().toUpperCase());
				}
				tmvo.setRLT_BT_PROREP(inputVO.getREPRESET_CUST());
				if(StringUtils.isNotBlank(inputVO.getINSPRD_KEYNO())){
					BigDecimal insprd_keyno = new BigDecimal(inputVO.getINSPRD_KEYNO());
					tmvo.setINSPRD_KEYNO(insprd_keyno);
				}else{
					tmvo.setINSPRD_KEYNO(null);
				}
				tmvo.setAO_CODE(inputVO.getAO_CODE());
				tmvo.setPROPOSER_CM_FLAG(inputVO.getPROPOSER_CM_FLAG());
				tmvo.setINSURED_CM_FLAG(inputVO.getINSURED_CM_FLAG());
				if(inputVO.getAPPLY_DATE() != null) {
					Timestamp apply_date = new Timestamp(inputVO.getAPPLY_DATE().getTime());
					tmvo.setAPPLY_DATE(apply_date);
				}
//				tmvo.setMATCH_DATE(MATCH_DATE);
				BigDecimal status = new BigDecimal(inputVO.getSTATUS());
				tmvo.setSTATUS(status);
				Date now = new Date();
				Timestamp op_date = new Timestamp(now.getTime());
				if("20".equals(inputVO.getSTATUS().toString())){
					tmvo.setOP_DATE(op_date);
				}else if("10".equals(inputVO.getSTATUS())){
					tmvo.setOP_DATE(null);
				}
				tmvo.setTERMINATED_INC(inputVO.getTERMINATED_INC());
				if(!"".equals(SIGN_INC)){
					tmvo.setSIGN_INC(SIGN_INC);
				}else{
					tmvo.setSIGN_INC("N");
				}
//				tmvo.setQC_ERASER(QC_ERASER);
				// 2018/1/9 change PROPOSER_BIRDAY
				if(inputVO.getPROPOSER_BIRDAY() != null) {
					Calendar calendar = new GregorianCalendar();
					calendar.setTime(inputVO.getPROPOSER_BIRDAY());
					calendar.set(Calendar.HOUR_OF_DAY, 0);
					calendar.set(Calendar.MINUTE, 0);
					calendar.set(Calendar.SECOND, 0);
					calendar.set(Calendar.MILLISECOND, 0);
					tmvo.setPROPOSER_BIRTH(new Timestamp(calendar.getTimeInMillis()));
				}

				tmvo.setPREMATCH_SEQ(inputVO.getPREMATCH_SEQ());
				tmvo.setLOAN_PRD_YN(inputVO.getLOAN_PRD_YN());
				tmvo.setQC_IMMI(inputVO.getQC_IMMI());
				tmvo.setNOT_PASS_REASON(inputVO.getNOT_PASS_REASON());
				tmvo.setPREMIUM_TRANSSEQ(inputVO.getPREMIUM_TRANSSEQ());
				tmvo.setQC_PROPOSER_CHG(inputVO.getQC_PROPOSER_CHG());
				tmvo.setPREMIUM_USAGE(inputVO.getPREMIUM_USAGE());
				tmvo.setPAY_WAY(inputVO.getPAY_WAY());
				tmvo.setPAYER_ID(inputVO.getPAYER_ID());
				tmvo.setRLT_BT_PROPAY(inputVO.getRLT_BT_PROPAY());
				if(inputVO.getDOC_KEYIN_DATE() != null) {
					tmvo.setDOC_KEYIN_DATE(new Timestamp(inputVO.getDOC_KEYIN_DATE().getTime()));
				}
				tmvo.setLOAN_SOURCE_YN(inputVO.getLOAN_SOURCE_YN());
				tmvo.setFB_COM_YN(inputVO.getFB_COM_YN());
				tmvo.setCOMPANY_NUM(inputVO.getCOMPANY_NUM());
				tmvo.setREVISE_CONFIRM_YN(inputVO.getREVISE_CONFIRM_YN());
				tmvo.setOTH_FUND_PURPOSE_1(inputVO.getOTH_FUND_PURPOSE_1());
				tmvo.setOTH_FUND_PURPOSE_2(inputVO.getOTH_FUND_PURPOSE_2());
				tmvo.setOTH_FUND_PURPOSE_3(inputVO.getOTH_FUND_PURPOSE_3());
				tmvo.setOTH_FUND_PURPOSE_4(inputVO.getOTH_FUND_PURPOSE_4());
				tmvo.setOTH_FUND_PURPOSE_5(inputVO.getOTH_FUND_PURPOSE_5());
				tmvo.setOTH_FUND_PURPOSE_6(inputVO.getOTH_FUND_PURPOSE_6());
				tmvo.setOTH_FUND_PURPOSE_RMK_1(inputVO.getOTH_FUND_PURPOSE_RMK_1());
				tmvo.setOTH_FUND_PURPOSE_RMK_2(inputVO.getOTH_FUND_PURPOSE_RMK_2());
				
				dam_obj.update(tmvo);

				sendRtnObject(null);
			} catch (Exception e) {
				logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
				throw new APException("系統發生錯誤請洽系統管理員");
			}
		}
	}

	public void getExchangeRate(Object body, IPrimitiveMap<Object> header) throws JBranchException{
		IOT130InputVO inputVO = (IOT130InputVO) body;
		IOT130OutputVO outputVO = new IOT130OutputVO();
		dam_obj = this.getDataAccessManager();
		QueryConditionIF qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();
		try {
			sb.append(" Select FXD_KEYNO as DATA,prod_name||'('||prod_period||')' LABEL,'('||prod_period||')' PROD_PERIOD ");
			sb.append(" from TBPRD_FX_DISCOUNT_PROD ");
			sb.append(" where effect_date<= sysdate and expiry_date>= sysdate ");
			qc.setQueryString(sb.toString());
			outputVO.setExchangeRateList(dam_obj.exeQuery(qc));
			sendRtnObject(outputVO);
		} catch (Exception e) {
			logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
			throw new APException("系統發生錯誤請洽系統管理員");
		}
	}


	public void EX_submit(Object body, IPrimitiveMap<Object> header) throws JBranchException{
		IOT920 iot920 = (IOT920) PlatformContext.getBean("iot920");
		INSIDInputVOinputVO iot920_inputVO = new INSIDInputVOinputVO();
		INSIDDataVO iot920_outputVO = new INSIDDataVO();
		IOT130InputVO inputVO = (IOT130InputVO) body;
		IOT130OutputVO outputVO = new IOT130OutputVO();
		String loginbrh = (String) getCommonVariable(SystemVariableConsts.LOGINBRH);
		String loginID = (String) getCommonVariable(SystemVariableConsts.LOGINID);
		String INS_ID = "";
		String SIGN_INC = "N";
		String errorMsg = "";
		iot920_inputVO.setBRANCH_NBR(loginbrh);
		iot920_inputVO.setINS_KIND("1");
		iot920_inputVO.setREG_TYPE(inputVO.getReg_type());
		if("new".equals(inputVO.getOPR_STATUS())){


			try {
				//取INS_KEYNO
				List<Map<String, Object>> INS_KEYNOList = new ArrayList<Map<String,Object>>();
				dam_obj = this.getDataAccessManager();
				QueryConditionIF qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				StringBuilder sb = new StringBuilder();
				sb.append(" select TBIOT_MAIN_SEQ.NEXTVAL from dual ");
				qc.setQueryString(sb.toString());
				INS_KEYNOList = dam_obj.exeQuery(qc);
				String INS_KEYNO = INS_KEYNOList.get(0).get("NEXTVAL").toString();
				BigDecimal keyno_change = new BigDecimal(INS_KEYNO);

				//分行留存文件檢核/保險送件文件檢核
				int in_DOC_N=0;
				int out_DOC_N=0;


				if(inputVO.getInList() != null){
					INSERT_TBIOT_DOC(inputVO.getInList(), keyno_change, "1");

					for(Map<String, Object> INitem:inputVO.getInList()){
						if("1".equals(INitem.get("DOC_LEVEL")) && "N".equals(INitem.get("DOC_CHK"))){
							inputVO.setSTATUS("10");
							in_DOC_N++;
						}
						if("Y".equals(INitem.get("SIGN_INC")) && "Y".equals(INitem.get("DOC_CHK"))){
							SIGN_INC = "Y";
						}

					}
				}


				if(inputVO.getOutList() != null){
					INSERT_TBIOT_DOC(inputVO.getOutList(), keyno_change, "2");

					for(Map<String, Object> OUTitem:inputVO.getOutList()){
						if("1".equals(OUTitem.get("DOC_LEVEL")) && "N".equals(OUTitem.get("DOC_CHK"))){
							inputVO.setSTATUS("10");
							out_DOC_N++;
						}
						if("Y".equals(OUTitem.get("SIGN_INC")) && "Y".equals(OUTitem.get("DOC_CHK"))){
							SIGN_INC = "Y";
						}
					}
				}

				//如沒任何必填未選則staus=20
				if(in_DOC_N<=0 && out_DOC_N <=0){
					inputVO.setSTATUS("20");
				}

				TBIOT_MAINVO tmvo = new TBIOT_MAINVO();
				tmvo.setRECRUIT_ID(inputVO.getRECRUIT_ID());
				tmvo.setINS_KEYNO(keyno_change);
				tmvo.setINS_KIND("1");

				tmvo.setREG_TYPE(inputVO.getReg_type());
				tmvo.setBRANCH_NBR(loginbrh);
				tmvo.setCUST_ID(inputVO.getEX_CUST_ID().toUpperCase());
				tmvo.setPROPOSER_NAME(inputVO.getEX_PROPOSER_NAME().toUpperCase());
				tmvo.setAO_CODE(inputVO.getAO_CODE());
				tmvo.setFXD_KEYNO(new BigDecimal(inputVO.getFX_PROD()));
				Timestamp apply_date = new Timestamp(inputVO.getCONTRAT_DATE().getTime());
				tmvo.setAPPLY_DATE(apply_date);
				SimpleDateFormat keyin_sdf = new SimpleDateFormat("yyyy-MM-dd");
				String now_change = keyin_sdf.format(new Date());
				Date now = keyin_sdf.parse(now_change);
				Timestamp keyin_date = new Timestamp(now.getTime());
				tmvo.setKEYIN_DATE(keyin_date);
				tmvo.setREMARK_INS(inputVO.getREMARK_INS());
//				tmvo.setMATCH_DATE(MATCH_DATE);
				BigDecimal status = new BigDecimal(inputVO.getSTATUS());
				tmvo.setSTATUS(status);
				Date OP_DATE_NOW = new Date();
				if("20".equals(inputVO.getSTATUS().toString())){
					tmvo.setOP_DATE(new Timestamp(OP_DATE_NOW.getTime()));
				}
				iot920_outputVO = iot920.getINS_ID(iot920_inputVO);
				INS_ID = iot920_outputVO.getINS_ID().toString();
				tmvo.setINS_ID(INS_ID);

				// 2018/1/9 change PROPOSER_BIRDAY
				if(inputVO.getPROPOSER_BIRDAY() != null) {
					Calendar calendar = new GregorianCalendar();
					calendar.setTime(inputVO.getPROPOSER_BIRDAY());
					calendar.set(Calendar.HOUR_OF_DAY, 0);
					calendar.set(Calendar.MINUTE, 0);
					calendar.set(Calendar.SECOND, 0);
					calendar.set(Calendar.MILLISECOND, 0);
					tmvo.setPROPOSER_BIRTH(new Timestamp(calendar.getTimeInMillis()));
				}
				dam_obj.create(tmvo);

				outputVO.setINS_ID(INS_ID);
				outputVO.setINSKEY_NO(INS_KEYNO);
				sendRtnObject(outputVO);			} catch (Exception e) {
				logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
				throw new APException("系統發生錯誤請洽系統管理員");
			}
		}else if("UPDATE".equals(inputVO.getOPR_STATUS())){
			BigDecimal ins_keyno_change = new BigDecimal(inputVO.getINS_KEYNO().toString());
			dam_obj = this.getDataAccessManager();
			QueryConditionIF qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuilder sb = new StringBuilder();

			//文件檢核
			int in_DOC_N=0;
			int out_DOC_N=0;

			//文件種類更改時，留存文件清除
			if(inputVO.isEditOTH_TYPE()){
				dam_obj = getDataAccessManager();
				qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sb = new StringBuilder();
				sb.append(" DELETE TBIOT_DOC_CHK where INS_KEYNO = :ins_keyno ");
				qc.setObject("ins_keyno", ins_keyno_change);
				qc.setQueryString(sb.toString());
				dam_obj.exeUpdate(qc);
			}


			if(inputVO.getInList() != null){
				if(inputVO.isEditOTH_TYPE()){
					INSERT_TBIOT_DOC(inputVO.getInList(), ins_keyno_change, "1");
				}
				for(Map<String, Object> INitem:inputVO.getInList()){
					if("1".equals(INitem.get("DOC_LEVEL")) && "N".equals(INitem.get("DOC_CHK"))){
						inputVO.setSTATUS("10");
						in_DOC_N++;
					}
					if("Y".equals(INitem.get("SIGN_INC")) && "Y".equals(INitem.get("DOC_CHK"))){
						SIGN_INC = "Y";
					}
				}
			}

			if(inputVO.getOutList() != null){
				if(inputVO.isEditOTH_TYPE()){
					INSERT_TBIOT_DOC(inputVO.getOutList(), ins_keyno_change, "2");
				}

				for(Map<String, Object> OUTitem:inputVO.getOutList()){
					if("1".equals(OUTitem.get("DOC_LEVEL")) && "N".equals(OUTitem.get("DOC_CHK"))){
						inputVO.setSTATUS("10");
						out_DOC_N++;
					}
					if("Y".equals(OUTitem.get("SIGN_INC")) && "Y".equals(OUTitem.get("DOC_CHK"))){
						SIGN_INC = "Y";
					}
				}
			}

			if(in_DOC_N<=0 && out_DOC_N <=0){
				inputVO.setSTATUS("20");
			}

			try {
				dam_obj = this.getDataAccessManager();
				TBIOT_MAINVO tmvo = new TBIOT_MAINVO();
				tmvo = (TBIOT_MAINVO) dam_obj.findByPKey(TBIOT_MAINVO.TABLE_UID, ins_keyno_change);

				tmvo.setRECRUIT_ID(inputVO.getRECRUIT_ID());
				tmvo.setINS_KIND("1");
				tmvo.setREG_TYPE(inputVO.getReg_type());
				tmvo.setBRANCH_NBR(loginbrh);
				tmvo.setCUST_ID(inputVO.getEX_CUST_ID().toUpperCase());
				tmvo.setPROPOSER_NAME(inputVO.getEX_PROPOSER_NAME().toUpperCase());
				tmvo.setAO_CODE(inputVO.getAO_CODE());
				tmvo.setFXD_KEYNO(new BigDecimal(inputVO.getFX_PROD()));
				Timestamp apply_date = new Timestamp(inputVO.getCONTRAT_DATE().getTime());
				tmvo.setAPPLY_DATE(apply_date);
				Date now = new Date();
				Timestamp OP_DATE = new Timestamp(now.getTime());
				tmvo.setREMARK_INS(inputVO.getREMARK_INS());
//				tmvo.setMATCH_DATE(MATCH_DATE);
				BigDecimal status = new BigDecimal(inputVO.getSTATUS());
				tmvo.setSTATUS(status);
				if("20".equals(inputVO.getSTATUS().toString())){
					tmvo.setOP_DATE(OP_DATE);
				}else if("10".equals(inputVO.getSTATUS())){
					tmvo.setOP_DATE(null);
				}
				// 2018/1/9 change PROPOSER_BIRDAY
				if(inputVO.getPROPOSER_BIRDAY() != null) {
					Calendar calendar = new GregorianCalendar();
					calendar.setTime(inputVO.getPROPOSER_BIRDAY());
					calendar.set(Calendar.HOUR_OF_DAY, 0);
					calendar.set(Calendar.MINUTE, 0);
					calendar.set(Calendar.SECOND, 0);
					calendar.set(Calendar.MILLISECOND, 0);
					tmvo.setPROPOSER_BIRTH(new Timestamp(calendar.getTimeInMillis()));
				}
				dam_obj.update(tmvo);

				sendRtnObject(null);
			} catch (Exception e) {
				logger.error(String.format("發生錯誤:%s", StringUtil.getStackTraceAsString(e)));
				throw new APException("系統發生錯誤請洽系統管理員");
			}
		}
	}

	public void checkREMARK_INS(Object body, IPrimitiveMap<Object> header) throws JBranchException{
		IOT130InputVO inputVO = (IOT130InputVO) body;
		IOT130OutputVO outputVO = new IOT130OutputVO();

		SOT701InputVO inputVO_701 = new SOT701InputVO();
		CustAcctDataVO CustAcctDataVO = new CustAcctDataVO();
		List<Map<String, Object>> trustAcctList = new ArrayList<Map<String,Object>>();

		inputVO_701.setCustID(inputVO.getEX_CUST_ID().toUpperCase());
		inputVO_701.setProdType("6");

		SOT701 sot701 = (SOT701) PlatformContext.getBean("sot701");
		try {
			CustAcctDataVO = sot701.getCustAcctData(inputVO_701);
			//信託帳號
			List<AcctVO> trustAcct = CustAcctDataVO.getTrustAcctList();
			for (AcctVO vo : trustAcct) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("LABEL", cbsservice.checkAcctLength(vo.getAcctNo()));
				map.put("DATA", cbsservice.checkAcctLength(vo.getAcctNo()));
//				map.put("ACCT_BALACNE", vo.getAcctBalance());
				trustAcctList.add(map);
			}
			outputVO.setCheckREMARK_INS(trustAcctList);

		} catch (Exception e) {
			e.printStackTrace();
			throw new APException(e.getMessage());
		}

		sendRtnObject(outputVO);
	}


	//留存/送件文件新增
	public void INSERT_TBIOT_DOC(List<Map<String, Object>> in_outList,BigDecimal ins_keyno_change,String type) throws JBranchException {

		for(Map<String, Object> item:in_outList){
			dam_obj = this.getDataAccessManager();
			TBIOT_DOC_CHKPK tdc_pk = new TBIOT_DOC_CHKPK();
			tdc_pk.setDOC_TYPE(type);
			tdc_pk.setINS_KEYNO(ins_keyno_change);
			tdc_pk.setDOC_SEQ(new BigDecimal(item.get("DOC_SEQ").toString()));
			TBIOT_DOC_CHKVO tdc_vo = new TBIOT_DOC_CHKVO();
			tdc_vo = (TBIOT_DOC_CHKVO) dam_obj.findByPKey(TBIOT_DOC_CHKVO.TABLE_UID, tdc_pk);
			if(tdc_vo==null){
				tdc_vo = new TBIOT_DOC_CHKVO();
				tdc_vo.setcomp_id(tdc_pk);
				tdc_vo.setDOC_CHK(item.get("DOC_CHK").toString());
				tdc_vo.setDOC_NAME(item.get("DOC_NAME").toString());
				tdc_vo.setDOC_LEVEL(item.get("DOC_LEVEL").toString());
				if(item.get("SIGN_INC") != null){
					if(!"".equals(item.get("SIGN_INC").toString())){
						tdc_vo.setSIGN_INC(item.get("SIGN_INC").toString());
					}else{
						tdc_vo.setSIGN_INC("N");
					}
				}else{
					tdc_vo.setSIGN_INC("N");
				}
				if(item.get("DOC_NAME_OTH") != null){
					tdc_vo.setDOC_NAME_OTH(item.get("DOC_NAME_OTH").toString());
				}
				dam_obj.create(tdc_vo);
			}else{
				throw new APException("ehl_01_common_016");
			}
		}
	}

	public void getPreMatchData(Object body, IPrimitiveMap<Object> header) throws JBranchException {
		IOT130InputVO inputVO = (IOT130InputVO) body;
		IOT130OutputVO outputVO = new IOT130OutputVO();

		outputVO.setPreMatchList(getPreMatchData(inputVO));
		sendRtnObject(outputVO);
	}

	/***
	 * 根據案件編號取得購買檢核資料
	 * @param inputVO
	 * @return
	 * @throws DAOException
	 * @throws JBranchException
	 */
	public List<Map<String, Object>> getPreMatchData(IOT130InputVO inputVO) throws DAOException, JBranchException {
		dam_obj = this.getDataAccessManager();
		QueryConditionIF qc = dam_obj.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuilder sb = new StringBuilder();

		sb.append(" Select A.PREMATCH_SEQ, A.INS_KIND, A.INS_ID, A.CASE_ID, A.POLICY_NO1, A.POLICY_NO2, A.POLICY_NO3, A.OTH_TYPE, A.BRANCH_NBR ");
		sb.append(" 	,A.CUST_ID, A.CUST_RISK, A.PROPOSER_NAME, A.PROPOSER_BIRTH, A.INSURED_ID, A.INSURED_NAME, A.REPRESET_ID, A.REPRESET_NAME, A.RLT_BT_PROREP ");
		sb.append(" 	,A.PAYER_ID, A.PAYER_NAME, A.RLT_BT_PROPAY, A.INSPRD_KEYNO, A.INSPRD_ID, A.REAL_PREMIUM, A.BASE_PREMIUM, A.MOP2, A.LOAN_SOURCE_YN, A.APPLY_DATE ");
		sb.append(" 	,A.MATCH_DATE, A.RECRUIT_ID, A.PROPOSER_CM_FLAG, A.INSURED_CM_FLAG, A.PAYER_CM_FLAG, A.AML, A.PRECHECK, A.BUSINESS_REL, A.PROPOSER_INCOME1, A.PROPOSER_INCOME2 ");
		sb.append(" 	,A.PROPOSER_INCOME3, A.INSURED_INCOME1, A.INSURED_INCOME2, A.INSURED_INCOME3, A.LOAN_CHK1_YN, A.LOAN_CHK2_YN, A.CD_CHK_YN, A.INCOME_REMARK ");
		sb.append("		,A.LOAN_SOURCE_REMARK, A.STATUS, (CASE WHEN A.STATUS = '3' THEN 'Y' ELSE 'N' END) AS AUTH_YN, A.CHG_CUST_ID, A.C_SENIOR_PVAL ");
		sb.append(" From TBIOT_PREMATCH A ");
		sb.append(" 	Where A.REG_TYPE = '3' ");

		if(StringUtils.isNotBlank(inputVO.getPREMATCH_SEQ())) {
			sb.append("	AND A.PREMATCH_SEQ = :prematchSeq ");
			qc.setObject("prematchSeq", inputVO.getPREMATCH_SEQ());
		}

		qc.setQueryString(sb.toString());
		List<Map<String, Object>> list = dam_obj.exeQuery(qc);

		if(CollectionUtils.isEmpty(list)) {
			throw new APException("查無此適合度檢核資料");
		}

		return list;
	}


	//end
}
