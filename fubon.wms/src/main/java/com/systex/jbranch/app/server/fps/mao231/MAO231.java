package com.systex.jbranch.app.server.fps.mao231;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBMAO_DEV_APL_PLISTVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataManager.WorkStation;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.server.mail.FubonMail;
import com.systex.jbranch.platform.server.mail.FubonSendJavaMail;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Component("mao231")
@Scope("request")
public class MAO231 extends FubonWmsBizLogic {

	private DataAccessManager dam = null;
	SimpleDateFormat sdfYYYYMMDD = new SimpleDateFormat("yyyy-MM-dd");
	
	public void inquire(Object body, IPrimitiveMap header) throws JBranchException {
		
		MAO231InputVO inputVO = (MAO231InputVO) body;
		MAO231OutputVO outputVO = new MAO231OutputVO();
		dam = this.getDataAccessManager();

		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT E.EMP_ID, ");
		sql.append("       E.EMP_NAME, ");
		sql.append("       P.USE_DATE, ");
		sql.append("       P.VISIT_CUST_LIST, ");
		sql.append("       SUBSTR(P.USE_PERIOD_S_TIME, 1, 2) || ':00' AS START_TIME, ");
		sql.append("       SUBSTR(P.USE_PERIOD_E_TIME, 1, 2) || ':00' AS END_TIME, ");
		sql.append("       P.DEV_NBR, ");
		sql.append("       P.DEV_STATUS, ");
		sql.append("       P.APL_EMP_ID, ");
		sql.append("       P.SEQ ");
		sql.append("FROM TBMAO_DEV_APL_PLIST P, TBORG_MEMBER E ");
		sql.append("WHERE 1 = 1 ");
		sql.append("AND P.APL_EMP_ID = E.EMP_ID ");
		sql.append("AND P.DEV_STATUS IN ('B04') ");
		sql.append("AND EXISTS (SELECT DISTINCT EMP_ID FROM VWORG_EMP_UHRM_INFO UP WHERE UP.DEPT_ID = E.DEPT_ID AND P.APL_EMP_ID = UP.EMP_ID) ");

		switch (getCommonVariable(FubonSystemVariableConsts.MEM_LOGIN_FLAG).toString()) {
			case "uhrmMGR":
			case "uhrmBMMGR":
				//有UHRM權限人員只能查詢UHRM人員鍵機或UHRM為招攬人員的案件
				sql.append("AND EXISTS (SELECT 1 FROM VWORG_EMP_UHRM_INFO MT WHERE E.DEPT_ID = MT.DEPT_ID AND MT.EMP_ID = :loginID AND MT.DEPT_ID = :loginArea) ");
				
				queryCondition.setObject("loginID", (String) SysInfo.getInfoValue(SystemVariableConsts.LOGINID));
				queryCondition.setObject("loginArea", getUserVariable(FubonSystemVariableConsts.LOGIN_AREA));
				break;
		}
		
		if (inputVO.getUse_date_bgn() != null) {
			sql.append("AND TRUNC(P.USE_DATE) >= TRUNC(:start) ");
			queryCondition.setObject("start", new Timestamp(inputVO.getUse_date_bgn().getTime()));
		}

		if (inputVO.getUse_date_end() != null) {
			sql.append("AND TRUNC(P.USE_DATE) <= TRUNC(:end) ");
			queryCondition.setObject("end", new Timestamp(inputVO.getUse_date_end().getTime()));
		}

		if (StringUtils.isNotBlank(inputVO.getSeq())) {
			sql.append("AND P.SEQ = :seqno ");
			queryCondition.setObject("seqno", inputVO.getSeq());
		}

		queryCondition.setQueryString(sql.toString());

		outputVO.setResultList(dam.exeQuery(queryCondition));

		this.sendRtnObject(outputVO);
	}

	public void reply(Object body, IPrimitiveMap header) throws JBranchException, Exception {

		WorkStation ws = DataManager.getWorkStation(uuid);
		MAO231InputVO inputVO = (MAO231InputVO) body;
		dam = this.getDataAccessManager();
		boolean reply = false;

		TBMAO_DEV_APL_PLISTVO vo = new TBMAO_DEV_APL_PLISTVO();
		vo = (TBMAO_DEV_APL_PLISTVO) dam.findByPKey(TBMAO_DEV_APL_PLISTVO.TABLE_UID, inputVO.getSeq());
		if (null != vo) {
			if (StringUtils.equals(inputVO.getReply_type(), "Y")) {
				vo.setDEV_STATUS("C05");
				vo.setLETGO_EMP_ID(ws.getUser().getUserID());
				vo.setLETGO_YN(inputVO.getReply_type());
				vo.setLETGO_DATETIME(new Timestamp(Calendar.getInstance().getTime().getTime()));
				vo.setDEV_TAKE_EMP_ID(ws.getUser().getUserID());
				vo.setDEV_TAKE_DATETIME(new Timestamp(Calendar.getInstance().getTime().getTime()));
				dam.update(vo);

				reply = true;
			} else if (StringUtils.equals(inputVO.getReply_type(), "N")) {
				vo.setDEV_STATUS("A01");
				vo.setLETGO_EMP_ID(ws.getUser().getUserID());
				vo.setLETGO_YN(inputVO.getReply_type());
				vo.setLETGO_DATETIME(new Timestamp(Calendar.getInstance().getTime().getTime()));
				dam.update(vo);

				reply = false;
			} else if (StringUtils.equals(inputVO.getReply_type(), "X")) {
				vo.setDEV_STATUS(inputVO.getReply_type());
				vo.setLETGO_EMP_ID(ws.getUser().getUserID());
				vo.setLETGO_YN("N");
				vo.setLETGO_DATETIME(new Timestamp(Calendar.getInstance().getTime().getTime()));
				dam.update(vo);

				reply = false;
			}
		} else {
			throw new APException("ehl_01_common_008");
		}

		// 發送通知信給申請人
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT EMP_ID, EMP_NAME, EMP_EMAIL_ADDRESS AS EMAIL ");
		sql.append("FROM TBORG_MEMBER ");
		sql.append("WHERE EMP_ID = :emp_id ");

		queryCondition.setQueryString(sql.toString());
		queryCondition.setObject("emp_id", inputVO.getEmail_id());

		List<Map<String, Object>> list = dam.exeQuery(queryCondition);

		String email = list.get(0).get("EMAIL").toString();
		String name = list.get(0).get("EMP_NAME").toString();

		Map<String, Object> annexData = new HashMap<String, Object>();
		String START_TIME = vo.getUSE_PERIOD_S_TIME().substring(0, 2);
		String END_TIME = vo.getUSE_PERIOD_E_TIME().substring(0, 2);

		if (isEmail(email) == false) {
			this.sendRtnObject("Error");
		} else {
			FubonSendJavaMail sendMail = new FubonSendJavaMail();
			FubonMail mail = new FubonMail();

			List<Map<String, String>> mailList = new ArrayList<Map<String, String>>();
			Map<String, String> mailMap = new HashMap<String, String>();

			mailMap.put(FubonSendJavaMail.MAIL, email);
			mailList.add(mailMap);

			mail.setLstMailTo(mailList);
			mail.setFromName("台北富邦銀行");
			mail.setFromMail("wmsr_bank@fbt.com");
			mail.setSubject("行動載具審核通知信"); // 設定信件主旨
			
			// 設定信件內容
			if (reply) { 
				mail.setContent("親愛的" + name + " 您好：<br />您於" + sdfYYYYMMDD.format(vo.getUSE_DATE()) + " " + START_TIME + ":00~" + END_TIME + ":00 " + "時段申請借用行動載具，經主管審核完成。");
			} else {
				mail.setContent("親愛的" + name + " 您好：<br />您於" + sdfYYYYMMDD.format(vo.getUSE_DATE()) + " " + START_TIME + ":00~" + END_TIME + ":00 " + "時段申請借用行動載具，經主管退回。");
			}
			
			// 寄出信件-無附件
			sendMail.sendMail(mail, annexData);

			this.sendRtnObject(null);
		}
	}

	// 信箱Email格式檢查
	public static boolean isEmail(String email) {
		Pattern emailPattern = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
		Matcher matcher = emailPattern.matcher(email);
		if (matcher.find()) {
			return true;
		}
		return false;
	}
}
