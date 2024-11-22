package com.systex.jbranch.app.server.fps.iot410;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.server.fps.iot111.IOT111;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Component("iot410")
@Scope("request")
public class IOT410 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(IOT410.class);

	public void getCallOut(Object body, IPrimitiveMap header) throws JBranchException {
		IOT410InputVO inputVO = (IOT410InputVO) body;
		IOT410OutputVO outputVO = new IOT410OutputVO();
		String prematch_seq = inputVO.getPREMATCH_SEQ();
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		
		sb.append(" SELECT S05.STATUS, S05.FAIL_REASON, S05.FAIL_TYPE1, S05.FAIL_TYPE2, S05.CALL_MEMO, ");
		sb.append(" S05.C_PREMIUM_TRANSSEQ, S05.I_PREMIUM_TRANSSEQ, S05.P_PREMIUM_TRANSSEQ, ");
		sb.append(" NVL(S05.TOT_CALL_CNT, 0) AS TOT_CALL_CNT, ");
		sb.append(" NVL(S05.TODAY_CALL_CNT, 0) AS TODAY_CALL_CNT, ");
		sb.append(" NVL(S05.FAIL_CALL_CNT, 0) AS FAIL_CALL_CNT, ");
		sb.append(" S05.PREMATCH_SEQ, S05.C_NEED_CALL_YN, S05.I_NEED_CALL_YN, S05.P_NEED_CALL_YN, ");
		sb.append(" S01.CASE_ID, S01.CUST_ID, S01.INSURED_ID, S01.PAYER_ID, ");
		sb.append(" S01.C_PREMIUM_TRANSSEQ_YN, S01.I_PREMIUM_TRANSSEQ_YN, S01.P_PREMIUM_TRANSSEQ_YN ");
		sb.append(" FROM TBIOT_PREMATCH S01 ");
		sb.append(" LEFT JOIN TBIOT_CALLOUT S05 ON S01.PREMATCH_SEQ = S05.PREMATCH_SEQ ");
		sb.append(" WHERE S01.PREMATCH_SEQ = :prematch_seq ");
		
		qc.setObject("prematch_seq", prematch_seq);
		qc.setQueryString(sb.toString());
		List<Map<String, Object>> resultList = dam.exeQuery(qc);
		outputVO.setResultList(resultList);
		
		Boolean showMsg = false;
		if (resultList.size() > 0 && resultList.get(0).get("CASE_ID") != null) {
			String case_id = resultList.get(0).get("CASE_ID").toString();
			if (StringUtils.isNotBlank(case_id)) {
				qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sb = new StringBuffer();
				sb.append(" SELECT S05.PREMATCH_SEQ, S05.FAIL_CALL_YN FROM TBIOT_CALLOUT S05 ");
				sb.append(" LEFT JOIN TBIOT_PREMATCH S01 ON S01.PREMATCH_SEQ = S05.PREMATCH_SEQ ");
				sb.append(" WHERE S01.CASE_ID = :case_id ");
				qc.setObject("case_id", case_id);
				qc.setQueryString(sb.toString());
				List<Map<String, Object>> sameCaseList = dam.exeQuery(qc);
				
				for (Map<String, Object> map : sameCaseList) {
					if (map.get("FAIL_CALL_YN") != null && StringUtils.isNotBlank(map.get("FAIL_CALL_YN").toString())) {
						String fail_call_yn = map.get("FAIL_CALL_YN").toString();
						if ("Y".equals(fail_call_yn)) {
							showMsg = true;
							break;
						}
					}
				}
			}
		}
		outputVO.setShowMsg(showMsg);
		
		qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sb = new StringBuffer();
		sb.append(" SELECT TO_CHAR(CREATETIME, 'YYYY/MM/DD hh24:mi') AS REJECT_DATE, REJECT_REASON ");
		sb.append(" FROM TBIOT_CALLOUT_REJECT WHERE PREMATCH_SEQ = :prematch_seq ");
		qc.setObject("prematch_seq", prematch_seq);
		qc.setQueryString(sb.toString());
		List<Map<String, Object>> rejectList = dam.exeQuery(qc);
		outputVO.setRejectList(rejectList);
		
		this.sendRtnObject(outputVO);
	}
	
	public void save(Object body, IPrimitiveMap header) throws JBranchException {
		IOT410InputVO inputVO = (IOT410InputVO) body;
		String loginID = (String) getUserVariable(FubonSystemVariableConsts.LOGINID);
		dam = this.getDataAccessManager();
		QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sb = new StringBuffer();
		String status = inputVO.getSTATUS();
		String c_premium_transseq = inputVO.getC_PREMIUM_TRANSSEQ();
		String i_premium_transseq = inputVO.getI_PREMIUM_TRANSSEQ();
		String p_premium_transseq = inputVO.getP_PREMIUM_TRANSSEQ();
		String prematch_seq = inputVO.getPREMATCH_SEQ();
		
		// 若將狀態改為「6.電訪疑義」或「7.取消投保」，需更新 TBIOT_CALLOUT.FAIL_CALL_YN
		sb.append(" UPDATE TBIOT_CALLOUT SET ");
		sb.append(" STATUS = :status, ");
		sb.append(" C_PREMIUM_TRANSSEQ = :c_premium_transseq, ");
		sb.append(" I_PREMIUM_TRANSSEQ = :i_premium_transseq, ");
		sb.append(" P_PREMIUM_TRANSSEQ = :p_premium_transseq, ");
		sb.append(" TOT_CALL_CNT = :tot_call_cnt, ");
		sb.append(" TODAY_CALL_CNT = :today_call_cnt, ");
		sb.append(" FAIL_CALL_CNT = :fail_call_cnt, ");
		sb.append(" FAIL_REASON = :fail_reason, ");
		sb.append(" FAIL_TYPE1 = :fail_type1, ");
		sb.append(" FAIL_TYPE2 = :fail_type2, ");
		sb.append(" CALL_MEMO = :call_memo, ");
		
		if (StringUtils.isNotBlank(status)) {
			if ("6".equals(status) || "7".equals(status)) {
				// 曾經有電訪疑義或取消投保
				sb.append(" FAIL_CALL_YN = 'Y', ");	
				
			} else if ("1".equals(status)) {
				// 若狀態改為『1.未申請』需清掉檢視狀態 ＆電訪員 &18：00~20：00時段
				sb.append(" REVIEW_STATUS = NULL, ");
				sb.append(" CALL_PERSON = NULL, ");
				
				// #2172：電訪預約方便聯絡時間拿掉 18：00~20：00
				sb.append(" C_TIME  = CASE WHEN C_TIME  IS NOT NULL THEN SUBSTR(C_TIME , 1, 3) || '0' ELSE NULL END, ");
				sb.append(" C_TIME2 = CASE WHEN C_TIME2 IS NOT NULL THEN SUBSTR(C_TIME2, 1, 3) || '0' ELSE NULL END, ");
				sb.append(" I_TIME  = CASE WHEN I_TIME  IS NOT NULL THEN SUBSTR(I_TIME , 1, 3) || '0' ELSE NULL END, ");
				sb.append(" I_TIME2 = CASE WHEN I_TIME2 IS NOT NULL THEN SUBSTR(I_TIME2, 1, 3) || '0' ELSE NULL END, ");
				sb.append(" P_TIME  = CASE WHEN P_TIME  IS NOT NULL THEN SUBSTR(P_TIME , 1, 3) || '0' ELSE NULL END, ");
				sb.append(" P_TIME2 = CASE WHEN P_TIME2 IS NOT NULL THEN SUBSTR(P_TIME2, 1, 3) || '0' ELSE NULL END, ");
			}
		}
		
		sb.append(" VERSION = VERSION+1, ");
		sb.append(" MODIFIER = :loginID, ");
		sb.append(" LASTUPDATE = SYSDATE ");
		sb.append(" WHERE PREMATCH_SEQ = :prematch_seq ");
		
		qc.setObject("status", status);
		qc.setObject("c_premium_transseq", c_premium_transseq);
		qc.setObject("i_premium_transseq", i_premium_transseq);
		qc.setObject("p_premium_transseq", p_premium_transseq);
		qc.setObject("tot_call_cnt", inputVO.getTOT_CALL_CNT());
		qc.setObject("today_call_cnt", inputVO.getTODAY_CALL_CNT());
		qc.setObject("fail_call_cnt", inputVO.getFAIL_CALL_CNT());
		qc.setObject("fail_reason", inputVO.getFAIL_REASON());
		qc.setObject("fail_type1", inputVO.getFAIL_TYPE1());
		qc.setObject("fail_type2", inputVO.getFAIL_TYPE2());
		qc.setObject("call_memo", inputVO.getCALL_MEMO());
		qc.setObject("loginID", loginID);
		qc.setObject("prematch_seq", prematch_seq);
		
		qc.setQueryString(sb.toString());
		dam.exeUpdate(qc);
		
		// 由電訪人員拉選該狀態：拉選狀態=4.電訪成功時，錄音序號即時寫入「理專訪談記錄」，並於OP鍵機時帶入。
		if (StringUtils.isNotBlank(status) && "4".equals(status)) {
			String custID = inputVO.getCUST_ID() 	   == null ? "" : inputVO.getCUST_ID().trim();
			String insuredID = inputVO.getINSURED_ID() == null ? "" : inputVO.getINSURED_ID().trim();
			String payerID = inputVO.getPAYER_ID() 	   == null ? "" : inputVO.getPAYER_ID().trim();
			
			IOT111 iot111 = (IOT111) PlatformContext.getBean("iot111");
			Timestamp today = new Timestamp(new Date().getTime());
			String visitor_role = (String) getUserVariable(FubonSystemVariableConsts.LOGINROLE);
			String visit_memo = "通知客戶內容：適合度編碼" + prematch_seq + "，客戶進行銀行端高齡/保費來源錄音保險電訪。";
			String visit_creply = "客戶回應內容：電訪完成，要保人錄音序號為" + c_premium_transseq + 
													"、被保人錄音序號為" + i_premium_transseq + 
													"、繳款人錄音序號為" + p_premium_transseq;
			// 要保人
			if (StringUtils.isNotBlank(custID)) {
				iot111.saveVisit(visitor_role, custID, visit_memo, today, visit_creply, loginID);				
			}
			// 被保人
			if (StringUtils.isNotBlank(insuredID) && !custID.equals(insuredID)) {
				iot111.saveVisit(visitor_role, insuredID, visit_memo, today, visit_creply, loginID);
			}
			// 繳款人
			if (StringUtils.isNotBlank(payerID) && !custID.equals(payerID) && !insuredID.equals(payerID)) {
				iot111.saveVisit(visitor_role, payerID, visit_memo, today, visit_creply, loginID);
			}
		}
		
		this.sendRtnObject(null);
	}
}
