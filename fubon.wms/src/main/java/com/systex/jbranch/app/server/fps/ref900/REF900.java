package com.systex.jbranch.app.server.fps.ref900;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBCAM_LOAN_SALERECVO;
import com.systex.jbranch.app.common.fps.table.TBCAM_LOAN_SALEREC_REVIEWVO;
import com.systex.jbranch.app.common.fps.table.TBCRM_WKPG_MD_MASTVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataManager.WorkStation;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.server.mail.FubonMail;
import com.systex.jbranch.platform.server.mail.FubonSendJavaMail;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Component("ref900")
@Scope("request")
public class REF900 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;

	public void inquire(Object body, IPrimitiveMap header) throws JBranchException {
		
		REF900InputVO inputVO = (REF900InputVO) body;
		REF900OutputVO return_VO = new REF900OutputVO();
		dam = this.getDataAccessManager();

		WorkStation ws = DataManager.getWorkStation(uuid);
		// 依系統角色決定下拉選單可視範圍
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> headmgrMap = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2); //總行
		Map<String, String> armgrMap = xmlInfo.doGetVariable("FUBONSYS.ARMGR_ROLE", FormatHelper.FORMAT_2); //業務處
		Map<String, String> mbrmgrMap = xmlInfo.doGetVariable("FUBONSYS.MBRMGR_ROLE", FormatHelper.FORMAT_2); //營運區
		Map<String, String> bmmgrMap = xmlInfo.doGetVariable("FUBONSYS.BMMGR_ROLE", FormatHelper.FORMAT_2);

		// follow ref120
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();

		sql.append("WITH SALEREC_LIST AS ( ");
		sql.append("  SELECT A.SEQ, A.EMP_ID, REL.ROLE_ID, ");
		sql.append("         CASE WHEN PRIVILEGEID = '012' THEN 'BRANCH_AREA_EMP' ");
		sql.append("              WHEN ROLE_ID IN ( ");
		sql.append("                SELECT ROLE_ID FROM TBORG_ROLE WHERE JOB_TITLE_NAME IS NOT NULL ");
		sql.append("              ) THEN 'BRANCH_EMP' ");
		sql.append("              WHEN PRIVILEGEID = '033' THEN 'REGION_CENTER_EMP' ");
		sql.append("              WHEN PRIVILEGEID = '045' THEN 'HEAD_EMP' ");
		sql.append("         ELSE NULL END AS TO_EMP_BOSS, ");
		sql.append("         CASE WHEN PRIVILEGEID = '012' THEN REL.BRANCH_AREA_ID ");
		sql.append("              WHEN ROLE_ID IN (SELECT ROLE_ID FROM TBORG_ROLE WHERE JOB_TITLE_NAME IS NOT NULL) THEN REL.BRANCH_NBR ");
		sql.append("              WHEN PRIVILEGEID = '033' THEN REL.REGION_CENTER_ID ");
		sql.append("              WHEN PRIVILEGEID = '045' THEN NULL ");
		sql.append("         ELSE NULL END AS TO_EMP_DEPT ");
		sql.append("  FROM ( ");
		sql.append("    SELECT SEQ, EMP_ID ");
		sql.append("    FROM ( ");
		sql.append("    SELECT SEQ, SALES_PERSON, CREATOR ");
		sql.append("    FROM TBCAM_LOAN_SALEREC_REVIEW ");
		sql.append("    ) UNPIVOT (EMP_ID FOR EMP_LIST IN (SALES_PERSON, CREATOR)) ");
		sql.append("  ) A ");
		sql.append("  LEFT JOIN VWORG_EMP_INFO REL ON A.EMP_ID = REL.EMP_ID ");
		sql.append(") ");

		// 2019/07/01 modify by ocean : 效能 
		sql.append(", BOSS_LIST AS ( ");
		sql.append("  SELECT EMP_ID AS BOSS_ID, PRIVILEGEID, ");
		sql.append("         CASE WHEN PRIVILEGEID = '011' THEN 'BRANCH_EMP' ");
		sql.append("              WHEN PRIVILEGEID = '012' THEN 'BRANCH_AREA_EMP' ");
		sql.append("              WHEN PRIVILEGEID = '013' THEN 'REGION_CENTER_EMP' ");
		sql.append("              WHEN PRIVILEGEID = '046' THEN 'HEAD_EMP' ");
		sql.append("         ELSE NULL END AS EMP_BOSS, ");
		sql.append("         CASE WHEN PRIVILEGEID = '011' THEN BRANCH_NBR ");
		sql.append("              WHEN PRIVILEGEID = '012' THEN BRANCH_AREA_ID ");
		sql.append("              WHEN PRIVILEGEID = '013' THEN REGION_CENTER_ID ");
		sql.append("              WHEN PRIVILEGEID = '046' THEN EMP_DEPT_ID ");
		sql.append("         ELSE NULL END AS EMP_DEPT ");
		sql.append("  FROM VWORG_EMP_INFO ");
		sql.append("  WHERE CASE WHEN PRIVILEGEID = '011' THEN 'BRANCH_EMP' ");
		sql.append("             WHEN PRIVILEGEID = '012' THEN 'BRANCH_AREA_EMP' ");
		sql.append("             WHEN PRIVILEGEID = '013' THEN 'REGION_CENTER_EMP' ");
		sql.append("             WHEN PRIVILEGEID = '046' THEN 'HEAD_EMP' ");
		sql.append("         ELSE NULL END IS NOT NULL ");
		sql.append("  AND CASE WHEN PRIVILEGEID = '011' THEN BRANCH_NBR ");
		sql.append("           WHEN PRIVILEGEID = '012' THEN BRANCH_AREA_ID ");
		sql.append("           WHEN PRIVILEGEID = '013' THEN REGION_CENTER_ID ");
		sql.append("           WHEN PRIVILEGEID = '046' THEN EMP_DEPT_ID ");
		sql.append("      ELSE NULL END IS NOT NULL ");
		sql.append(") ");
		
		sql.append(", BOSS_EMP_LIST AS ( ");
		sql.append("  SELECT SEQ, ");
		sql.append("         PRIVILEGEID, ");
		sql.append("         LISTAGG(EMP_ID, ',') WITHIN GROUP (ORDER BY PRIVILEGEID) AS EMP_LIST ");
		sql.append("  FROM ( ");
		sql.append("    SELECT DISTINCT SALEREC.SEQ, SALEREC.EMP_ID, SALEREC.PRIVILEGEID ");
		sql.append("    FROM ( ");
		sql.append("      SELECT SEQ, EMP_ID, PRIVILEGEID ");
		sql.append("      FROM ( ");
		sql.append("        SELECT DISTINCT SL.SEQ, SL.TO_EMP_BOSS, SL.EMP_ID AS SALES_PERSON, BEL.BOSS_ID, BEL.PRIVILEGEID ");
		sql.append("        FROM SALEREC_LIST SL ");
		sql.append("        LEFT JOIN BOSS_LIST BEL ON SL.TO_EMP_BOSS = BEL.EMP_BOSS AND SL.TO_EMP_BOSS <> 'HEAD_EMP' AND SL.TO_EMP_DEPT = BEL.EMP_DEPT ");
		sql.append("        WHERE PRIVILEGEID IS NOT NULL ");
		sql.append("        UNION ");
		sql.append("        SELECT DISTINCT SL.SEQ, SL.TO_EMP_BOSS, SL.EMP_ID AS SALES_PERSON, BEL.BOSS_ID, BEL.PRIVILEGEID ");
		sql.append("        FROM SALEREC_LIST SL ");
		sql.append("        LEFT JOIN BOSS_LIST BEL ON SL.TO_EMP_BOSS = BEL.EMP_BOSS AND SL.TO_EMP_BOSS = 'HEAD_EMP' ");
		sql.append("        WHERE PRIVILEGEID IS NOT NULL ");
		sql.append("      ) UNPIVOT (EMP_ID FOR EMP_LIST IN (SALES_PERSON, BOSS_ID)) ");
		sql.append("    ) SALEREC ");
		sql.append("    LEFT JOIN TBORG_MEMBER M ON SALEREC.EMP_ID = M.EMP_ID ");
		sql.append("  ) ");
		sql.append("  GROUP BY SEQ, PRIVILEGEID ");
		sql.append(") ");

		sql.append("SELECT DISTINCT ");
		sql.append("       SALEREC.SEQ, SALEREC.TXN_DATE, SALEREC.SALES_PERSON, SALEREC.SALES_NAME, SALEREC.SALES_ROLE, SALEREC.CUST_ID, SALEREC.CUST_NAME, SALEREC.REF_PROD, ");
		sql.append("       SALEREC.USERID, SALEREC.USERNAME, SALEREC.USERROLE, SALEREC.ARBITRATE_DATE, SALEREC.STATUS, SALEREC.NO_ACCEPT_REASON, ");
		sql.append("       INFO.REGION_CENTER_ID, INFO.REGION_CENTER_NAME, INFO.BRANCH_AREA_ID, INFO.BRANCH_AREA_NAME AS BRANCH_AREA, INFO.BRANCH_NBR, INFO.BRANCH_NAME AS BRANCH, ");
		sql.append("       INFO_U.REGION_CENTER_ID AS REGION_CENTER_ID_U, INFO_U.REGION_CENTER_NAME AS REGION_CENTER_NAME_U, INFO_U.BRANCH_AREA_ID AS BRANCH_AREA_ID_U, INFO_U.BRANCH_AREA_NAME AS BRANCH_AREA_U, INFO_U.BRANCH_NBR AS BRANCH_NBR_U, INFO_U.BRANCH_NAME AS BRANCH_U, ");
		sql.append("       CASE WHEN REL.PRIVILEGEID = '012' THEN '012' ");
		sql.append("            WHEN REL.ROLE_ID IN (SELECT ROLE_ID FROM TBORG_ROLE WHERE JOB_TITLE_NAME IS NOT NULL) THEN '011' ");
		sql.append("            WHEN REL.PRIVILEGEID = '033' THEN '013' ");
		sql.append("            WHEN REL.PRIVILEGEID = '045' THEN '046' ");
		sql.append("       ELSE NULL END AS SALES_BOSS, ");
		sql.append("       BEL.EMP_LIST AS SALES_BOSS_EMP_LIST ");
		sql.append("FROM TBCAM_LOAN_SALEREC_REVIEW SALEREC ");
		sql.append("LEFT JOIN VWORG_DEFN_INFO INFO ON SALEREC.REF_ORG_ID = INFO.BRANCH_NBR ");
		sql.append("LEFT JOIN VWORG_BRANCH_EMP_DETAIL_INFO INFO_U ON INFO_U.EMP_ID = SALEREC.USERID ");
		sql.append("LEFT JOIN VWORG_EMP_INFO REL ON SALEREC.CREATOR = REL.EMP_ID ");
		sql.append("LEFT JOIN BOSS_EMP_LIST BEL ON SALEREC.SEQ = BEL.SEQ ");
		sql.append("WHERE 1 = 1 ");
		
		if (headmgrMap.containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			sql.append("AND (INFO.REGION_CENTER_ID IN (:rcIdList) OR INFO.REGION_CENTER_ID IS NULL OR SALEREC.SALES_PERSON = :loginID) ");
			queryCondition.setObject("rcIdList", getUserVariable(FubonSystemVariableConsts.AVAILREGIONLIST));
			queryCondition.setObject("loginID", ws.getUser().getUserID());
		} else if (armgrMap.containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			sql.append("AND (SALEREC.SALES_PERSON IN (SELECT EMP_ID FROM VWORG_EMP_INFO WHERE REGION_CENTER_ID IN (:rcIdList)) ");
			sql.append("	    OR SALEREC.USERID IN (SELECT EMP_ID FROM VWORG_EMP_INFO WHERE REGION_CENTER_ID IN (:rcIdList)) ");
			sql.append("	    OR SALEREC.SALES_PERSON = :loginID) ");
			queryCondition.setObject("rcIdList", getUserVariable(FubonSystemVariableConsts.AVAILREGIONLIST));
			queryCondition.setObject("loginID", ws.getUser().getUserID());
		} else if (mbrmgrMap.containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			sql.append("AND (SALEREC.SALES_PERSON IN (SELECT EMP_ID FROM VWORG_EMP_INFO WHERE BRANCH_AREA_ID IN (:opIdList)) ");
			sql.append("	    OR SALEREC.USERID IN (SELECT EMP_ID FROM VWORG_EMP_INFO WHERE BRANCH_AREA_ID IN (:opIdList)) ");
			sql.append("	    OR SALEREC.SALES_PERSON = :loginID) ");
			queryCondition.setObject("opIdList", getUserVariable(FubonSystemVariableConsts.AVAILAREALIST));
			queryCondition.setObject("loginID", ws.getUser().getUserID());
		} else if (bmmgrMap.containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			sql.append("AND (SALEREC.SALES_PERSON IN (SELECT EMP_ID FROM VWORG_EMP_INFO WHERE BRANCH_NBR IN (:brIdList)) ");
			sql.append("	    OR SALEREC.USERID IN (SELECT EMP_ID FROM VWORG_EMP_INFO WHERE BRANCH_NBR IN (:brIdList)) ");
			sql.append("	    OR SALEREC.SALES_PERSON = :loginID) ");
			queryCondition.setObject("brIdList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
			queryCondition.setObject("loginID", ws.getUser().getUserID());
		} else {
			sql.append("AND (SALEREC.CREATOR = :loginID OR SALEREC.USERID = :loginID) ");
			queryCondition.setObject("loginID", ws.getUser().getUserID());
		}

		// where
		if (StringUtils.isNotBlank(inputVO.getSeq())) {
			sql.append("and SALEREC.SEQ = :seq ");
			queryCondition.setObject("seq", inputVO.getSeq());
		}
		
		// 轉介日期
		if (inputVO.getStartDate() != null) {
			sql.append("and SALEREC.TXN_DATE >= TRUNC(:start) ");
			queryCondition.setObject("start", inputVO.getStartDate());
		}
		
		if (inputVO.getEndDate() != null) {
			sql.append("and SALEREC.TXN_DATE < TRUNC(:end)+1 ");
			queryCondition.setObject("end", inputVO.getEndDate());
		}
		
		// 區域
		if (StringUtils.isNotBlank(inputVO.getRegion_center_id())) {
			sql.append("AND (INFO.REGION_CENTER_ID = :rc_id OR INFO_U.REGION_CENTER_ID = :rc_id) ");
			queryCondition.setObject("rc_id", inputVO.getRegion_center_id());
		}
		
		if (StringUtils.isNotBlank(inputVO.getBranch_area_id())) {
			sql.append("AND (INFO.BRANCH_AREA_ID = :op_id OR INFO_U.BRANCH_AREA_ID = :op_id) ");
			queryCondition.setObject("op_id", inputVO.getBranch_area_id());
		}
		
		if (StringUtils.isNotBlank(inputVO.getBranch_nbr())) {
			sql.append("AND (INFO.BRANCH_NBR = :br_id OR INFO_U.BRANCH_NBR = :br_id) ");
			queryCondition.setObject("br_id", inputVO.getBranch_nbr());
		}
		
		// 轉介人
		if (StringUtils.isNotBlank(inputVO.getSales_person())) {
			sql.append("AND SALEREC.SALES_PERSON LIKE :salesPerson ");
			queryCondition.setObject("salesPerson", inputVO.getSales_person() + "%");
		}
		
		if (StringUtils.isNotBlank(inputVO.getSales_name())) {
			sql.append("AND SALEREC.SALES_NAME LIKE :salesName ");
			queryCondition.setObject("salesName", inputVO.getSales_name() + "%");
		}
		
		// 受轉介人
		if (StringUtils.isNotBlank(inputVO.getUser_id())) {
			sql.append("AND SALEREC.USERID LIKE :userID ");
			queryCondition.setObject("userID", inputVO.getUser_id() + "%");
		}
		
		if (StringUtils.isNotBlank(inputVO.getUser_name())) {
			sql.append("AND SALEREC.USERNAME LIKE :userName ");
			queryCondition.setObject("userName", inputVO.getUser_name() + "%");
		}
		
		// 狀態
		if (StringUtils.isNotBlank(inputVO.getStatus())) {
			sql.append("AND SALEREC.STATUS = :rec_status ");
			queryCondition.setObject("rec_status", inputVO.getStatus());
		}
		
		// 客戶ID
		if (StringUtils.isNotBlank(inputVO.getCust_id())) {
			sql.append("AND SALEREC.CUST_ID LIKE :custID ");
			queryCondition.setObject("custID", inputVO.getCust_id().toUpperCase() + "%");
		}
		
		if (StringUtils.isNotBlank(inputVO.getCust_name())) {
			sql.append("AND SALEREC.CUST_NAME LIKE :custName ");
			queryCondition.setObject("custName", inputVO.getCust_name() + "%");
		}
		
		// 轉介商品
		if (StringUtils.isNotBlank(inputVO.getRef_prod())) {
			sql.append("AND SALEREC.REF_PROD = :refProd ");
			queryCondition.setObject("refProd", inputVO.getRef_prod());
		}

		sql.append("ORDER BY CASE WHEN SALEREC.STATUS = 'A' THEN 0 WHEN SALEREC.STATUS = 'W' THEN 1 ELSE 2 END ASC, SALEREC.TXN_DATE DESC ");
		queryCondition.setQueryString(sql.toString());
		return_VO.setResultList(dam.exeQuery(queryCondition));

		this.sendRtnObject(return_VO);
	}

	public void goEffect(Object body, IPrimitiveMap header) throws Exception {
		
		REF900InputVO inputVO = (REF900InputVO) body;
		dam = this.getDataAccessManager();

		Calendar calendar = Calendar.getInstance();
		calendar.setFirstDayOfWeek(Calendar.MONDAY);
		calendar.setMinimalDaysInFirstWeek(4);

		TBCAM_LOAN_SALEREC_REVIEWVO rvo = new TBCAM_LOAN_SALEREC_REVIEWVO();
		rvo = (TBCAM_LOAN_SALEREC_REVIEWVO) dam.findByPKey(TBCAM_LOAN_SALEREC_REVIEWVO.TABLE_UID, inputVO.getSeq());
		if (rvo != null) {
			rvo.setSTATUS("Y");
			dam.update(rvo);

			TBCAM_LOAN_SALERECVO vo = new TBCAM_LOAN_SALERECVO();
			vo.setSEQ(rvo.getSEQ());
			vo.setTERRITORY_ID(rvo.getTERRITORY_ID());
			vo.setTXN_DATE(rvo.getTXN_DATE());
			vo.setREF_ORG_ID(rvo.getREF_ORG_ID());
			vo.setCUST_ID(rvo.getCUST_ID());
			vo.setCUST_NAME(rvo.getCUST_NAME());
			vo.setSALES_PERSON(rvo.getSALES_PERSON());
			vo.setSALES_NAME(rvo.getSALES_NAME());
			vo.setREF_PROD(rvo.getREF_PROD());
			vo.setCASE_PROPERTY("1");
			vo.setSALES_ROLE(rvo.getSALES_ROLE());
			vo.setUSERID(rvo.getUSERID());
			vo.setUSERNAME(rvo.getUSERNAME());
			vo.setUSERROLE(rvo.getUSERROLE());
			vo.setNEW_CUST_FLAG(rvo.getNEW_CUST_FLAG());
			dam.create(vo);
			vo.setCreator(rvo.getCreator());
			dam.update(vo);

			// 內部事件通知
			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sql = new StringBuffer();
			sql.append("SELECT PRI.PRIVILEGEID FROM TBORG_MEMBER M ");
			sql.append("LEFT JOIN TBORG_MEMBER_ROLE MR ON M.EMP_ID = MR.EMP_ID AND MR.IS_PRIMARY_ROLE = 'Y' ");
			sql.append("LEFT JOIN TBSYSSECUROLPRIASS PRI ON MR.ROLE_ID = PRI.ROLEID ");
			sql.append("WHERE M.EMP_ID = :empID ");
			queryCondition.setObject("empID", rvo.getUSERID());
			queryCondition.setQueryString(sql.toString());
			List<Map<String, Object>> empList = dam.exeQuery(queryCondition);
			if (empList.size() == 0)
				throw new APException("受轉介人沒有設定權限群組");

			TBCRM_WKPG_MD_MASTVO msvo = new TBCRM_WKPG_MD_MASTVO();
			msvo.setSEQ(getSeqNum());
			msvo.setPRIVILEGEID((String) empList.get(0).get("PRIVILEGEID"));
			msvo.setEMP_ID(rvo.getUSERID());
			msvo.setROLE_LINK_YN("Y");
			msvo.setFRQ_TYPE("D");
			msvo.setFRQ_MWD(String.format("%02d", calendar.get(Calendar.DAY_OF_MONTH)));
			msvo.setDISPLAY_NO("103");
			msvo.setCLICK_YN("N");
			msvo.setCLICK_DATE(new Timestamp(System.currentTimeMillis()));
			msvo.setRPT_NAME("待回報轉介案件-" + rvo.getSEQ());
			msvo.setRPT_PROG_URL("REF120");
			msvo.setPASS_PARAMS(rvo.getSEQ());
			msvo.setFRQ_YEAR(String.valueOf(calendar.get(Calendar.YEAR)));
			dam.create(msvo);
		} else
			throw new APException("ehl_01_common_001");

		this.sendRtnObject(null);
	}

	public void backEffect(Object body, IPrimitiveMap header) throws Exception {
		
		REF900InputVO inputVO = (REF900InputVO) body;
		REF900OutputVO return_VO = new REF900OutputVO();
		XmlInfo xmlInfo = new XmlInfo();
		dam = this.getDataAccessManager();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
		Calendar calendar = Calendar.getInstance();
		calendar.setFirstDayOfWeek(Calendar.MONDAY);
		calendar.setMinimalDaysInFirstWeek(4);
		Map<String, String> ref_prod = xmlInfo.doGetVariable("CAM.REF_PROD", FormatHelper.FORMAT_3);

		List<String> errorMap = new ArrayList<String>();
		List<String> errorMap2 = new ArrayList<String>();

		TBCAM_LOAN_SALEREC_REVIEWVO rvo = new TBCAM_LOAN_SALEREC_REVIEWVO();
		rvo = (TBCAM_LOAN_SALEREC_REVIEWVO) dam.findByPKey(TBCAM_LOAN_SALEREC_REVIEWVO.TABLE_UID, inputVO.getSeq());
		if (rvo != null) {
			rvo.setSTATUS("A");
			rvo.setNO_ACCEPT_REASON(inputVO.getReason());
			rvo.setARBITRATE_DATE(new Timestamp(System.currentTimeMillis()));
			dam.update(rvo);

			List<Map<String, Object>> emp_List = this.getWKPG_LIST(rvo.getSEQ());
			for (Map<String, Object> map : emp_List) {
				// 內部事件通知, 只通知主管
				if (!rvo.getSALES_PERSON().equals(ObjectUtils.toString(map.get("EMP_ID"))) && !rvo.getCreator().equals(ObjectUtils.toString(map.get("EMP_ID")))) {
					TBCRM_WKPG_MD_MASTVO msvo = new TBCRM_WKPG_MD_MASTVO();
					msvo.setSEQ(getSeqNum());
					msvo.setPRIVILEGEID(ObjectUtils.toString(map.get("PRIVILEGEID")));
					msvo.setEMP_ID(ObjectUtils.toString(map.get("EMP_ID")));
					msvo.setROLE_LINK_YN("Y");
					msvo.setFRQ_TYPE("D");
					msvo.setFRQ_MWD(String.format("%02d", calendar.get(Calendar.DAY_OF_MONTH)));
					msvo.setDISPLAY_NO("102");
					msvo.setCLICK_YN("N");
					msvo.setCLICK_DATE(new Timestamp(System.currentTimeMillis()));
					msvo.setRPT_NAME("待仲裁轉介案件-" + rvo.getSEQ());
					msvo.setRPT_PROG_URL("REF900");
					msvo.setPASS_PARAMS(rvo.getSEQ());
					msvo.setFRQ_YEAR(String.valueOf(calendar.get(Calendar.YEAR)));
					dam.create(msvo);
				}
				// mail
				String mail_address = ObjectUtils.toString(map.get("EMP_EMAIL_ADDRESS"));
				// 無E-mail
				if (StringUtils.isBlank(mail_address))
					errorMap.add(ObjectUtils.toString(map.get("EMP_ID")));
				// Email格式錯誤
				else if (isEmail(mail_address) == false)
					errorMap2.add(ObjectUtils.toString(map.get("EMP_ID")));
				else {
					List<Map<String, String>> mailList = new ArrayList<Map<String, String>>();
					Map<String, String> mailMap = new HashMap<String, String>();
					mailMap.put(FubonSendJavaMail.MAIL, mail_address);
					mailList.add(mailMap);

					// 因為他覺得測試機的MAIL他都設同一個, 他測不出他要收三封的效果, 所以改成迴圈跑
					// old code mail
					QueryConditionIF salecon = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					salecon.setQueryString("select BRANCH_NBR, BRANCH_NAME from VWORG_DEFN_INFO where BRANCH_NBR = :bra_nbr ");
					salecon.setObject("bra_nbr", rvo.getREF_ORG_ID());
					List<Map<String, String>> sale_data = dam.exeQuery(salecon);
					String BRANCH_NAME = sale_data.size() > 0 ? sale_data.get(0).get("BRANCH_NAME") : "";

					FubonSendJavaMail sendMail = new FubonSendJavaMail();
					FubonMail mail = new FubonMail();
					Map<String, Object> annexData = new HashMap<String, Object>();
					mail.setLstMailTo(mailList);
					//設定信件主旨
					mail.setSubject("個金業務管理系統轉介客戶仲裁通知");
					//設定信件內容
					String content = "<table border=\"1\" style=\"text-align:center;\">";
					content += "<tr><td>案件編號</td>";
					content += "<td>轉介日期</td>";
					content += "<td>轉介人分行</td>";
					content += "<td>轉介人員編</td>";
					content += "<td>轉介人姓名</td></tr>";
					content += "<tr><td>" + rvo.getSEQ() + "</td>";
					content += "<td>" + sdf.format(rvo.getTXN_DATE()) + "</td>";
					content += "<td>" + rvo.getREF_ORG_ID() + "-" + BRANCH_NAME + "</td>";
					content += "<td>" + rvo.getSALES_PERSON() + "</td>";
					content += "<td>" + rvo.getSALES_NAME() + "</td></tr>";
					content += "<tr><td>客戶姓名</td>";
					content += "<td>轉介商品</td>";
					content += "<td>受轉介人員編</td>";
					content += "<td>受轉介人姓名</td>";
					content += "<td>拒絕原因</td></tr>";
					content += "<tr><td>" + rvo.getCUST_NAME() + "</td>";
					content += "<td>" + ref_prod.get(rvo.getREF_PROD()) + "</td>";
					content += "<td>" + rvo.getUSERID() + "</td>";
					content += "<td>" + rvo.getUSERNAME() + "</td>";
					content += "<td>" + rvo.getNO_ACCEPT_REASON() + "</td></tr></table>";
					SimpleDateFormat sdf2 = new SimpleDateFormat("yyy/MM/dd");
					Calendar calendar2 = Calendar.getInstance();
					calendar2.setTimeInMillis(getBusiDay(rvo.getTXN_DATE()).getTime());
					calendar2.add(Calendar.YEAR, -1911);
					content += "<br>受轉介人未接受該筆轉介案件，請主管" + sdf2.format(calendar2.getTime()) + "前完成仲裁，若確認有轉介事實，請將該案件發回受轉介人，";
					content += "若確認未有轉介事實，請於系統刪除該筆轉介案件。";
					mail.setContent(content);
					sendMail.sendMail(mail, annexData);
				}
			}
		} else
			throw new APException("ehl_01_common_001");

		return_VO.setErrorMsg(errorMap);
		return_VO.setErrorMsg2(errorMap2);
		this.sendRtnObject(return_VO);
	}

	public void delEffect(Object body, IPrimitiveMap header) throws Exception {
		
		REF900InputVO inputVO = (REF900InputVO) body;
		dam = this.getDataAccessManager();

		TBCAM_LOAN_SALEREC_REVIEWVO rvo = new TBCAM_LOAN_SALEREC_REVIEWVO();
		rvo = (TBCAM_LOAN_SALEREC_REVIEWVO) dam.findByPKey(TBCAM_LOAN_SALEREC_REVIEWVO.TABLE_UID, inputVO.getSeq());
		if (rvo != null) {
			rvo.setSTATUS("C");
			dam.update(rvo);
		} else
			throw new APException("ehl_01_common_001");

		this.sendRtnObject(null);
	}

	//信箱Email格式檢查
	public static boolean isEmail(String email) {
		
		Pattern emailPattern = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
		Matcher matcher = emailPattern.matcher(email);
		if (matcher.find())
			return true;
		return false;
	}

	private String getSeqNum() throws JBranchException {
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition.setQueryString("SELECT SQ_TBCRM_WKPG_MD_MAST.nextval AS SEQ FROM DUAL");
		List<Map<String, Object>> SEQLIST = dam.exeQuery(queryCondition);
		return ObjectUtils.toString(SEQLIST.get(0).get("SEQ"));
	}

	public Timestamp getBusiDay(Timestamp date) throws JBranchException {
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition.setQueryString("SELECT PABTH_UTIL.FC_getBusiDay(TO_DATE(:endDate, 'yyyyMMdd'), 'TWD', 5) AS TXN_DATE FROM DUAL");
		queryCondition.setObject("endDate", new SimpleDateFormat("yyyyMMdd").format(date));
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		return (Timestamp) list.get(0).get("TXN_DATE");
	}

	private List<Map<String, Object>> getWKPG_LIST(String seq) throws Exception {
		
		// ocean sql SELECT SEQ, SALES_PERSON, CREATOR FROM TBCAM_LOAN_SALEREC_REVIEW
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		sql.append("WITH SALEREC_LIST AS ( ");
		sql.append("  SELECT  DISTINCT A.SEQ, A.EMP_ID, REL.ROLE_ID, ");
		sql.append("          CASE  WHEN PRIVILEGEID = '012' THEN 'BRANCH_AREA_EMP' ");
		sql.append("                WHEN ROLE_ID IN (SELECT ROLE_ID FROM TBORG_ROLE WHERE JOB_TITLE_NAME IS NOT NULL) THEN 'BRANCH_EMP' ");
		sql.append("                WHEN PRIVILEGEID = '033' THEN 'REGION_CENTER_EMP' ");
		sql.append("                WHEN PRIVILEGEID = '045' THEN 'HEAD_EMP' ");
		sql.append("          ELSE NULL END AS EMP_BOSS, ");
		sql.append("          CASE  WHEN PRIVILEGEID = '012' THEN REL.BRANCH_AREA_ID ");
		sql.append("                WHEN ROLE_ID IN (SELECT ROLE_ID FROM TBORG_ROLE WHERE JOB_TITLE_NAME IS NOT NULL) THEN REL.BRANCH_NBR ");
		sql.append("                WHEN PRIVILEGEID = '033' THEN REL.REGION_CENTER_ID ");
		sql.append("                WHEN PRIVILEGEID = '045' THEN NULL ");
		sql.append("          ELSE NULL END AS EMP_DEPT ");
		sql.append("  FROM ( ");
		sql.append("    SELECT SEQ, EMP_ID ");
		sql.append("    FROM ( ");
		sql.append("      SELECT SEQ, SALES_PERSON, CREATOR ");
		sql.append("      FROM TBCAM_LOAN_SALEREC_REVIEW ");
		sql.append("    ) UNPIVOT (EMP_ID FOR EMP_LIST IN (SALES_PERSON, CREATOR)) ");
		sql.append("  ) A ");
		sql.append("  LEFT JOIN VWORG_EMP_INFO REL ON A.EMP_ID = REL.EMP_ID ");
		sql.append("  WHERE SEQ = :seq ");
		sql.append(") ");
		sql.append(", BASE_BOSS AS ( ");
		sql.append("  SELECT SALEREC.SEQ, SALEREC.EMP_ID, SALEREC.PRIVILEGEID, M.EMP_EMAIL_ADDRESS ");
		sql.append("  FROM ( ");
		sql.append("    SELECT SEQ, EMP_ID, PRIVILEGEID ");
		sql.append("    FROM ( ");
		sql.append("      SELECT SL.SEQ, SL.EMP_BOSS, SL.EMP_ID AS SALES_PERSON, REL.EMP_ID AS BOSS_ID, REL.PRIVILEGEID ");
		sql.append("      FROM SALEREC_LIST SL ");
		sql.append("      LEFT JOIN VWORG_EMP_INFO REL ON REL.PRIVILEGEID = '011' AND SL.EMP_DEPT = REL.BRANCH_NBR ");
		sql.append("      WHERE SL.EMP_BOSS = 'BRANCH_EMP' ");
		sql.append("      UNION ");
		sql.append("      SELECT SL.SEQ,  SL.EMP_BOSS, SL.EMP_ID AS SALES_PERSON, REL.EMP_ID AS BOSS_ID, REL.PRIVILEGEID ");
		sql.append("      FROM SALEREC_LIST SL ");
		sql.append("      LEFT JOIN VWORG_EMP_INFO REL ON REL.PRIVILEGEID = '012' AND SL.EMP_DEPT = REL.BRANCH_AREA_ID ");
		sql.append("      WHERE SL.EMP_BOSS = 'BRANCH_AREA_EMP' ");
		sql.append("      UNION ");
		sql.append("      SELECT SL.SEQ,  SL.EMP_BOSS, SL.EMP_ID AS SALES_PERSON, REL.EMP_ID AS BOSS_ID, REL.PRIVILEGEID ");
		sql.append("      FROM SALEREC_LIST SL ");
		sql.append("      LEFT JOIN VWORG_EMP_INFO REL ON REL.PRIVILEGEID = '013' AND SL.EMP_DEPT = REL.REGION_CENTER_ID ");
		sql.append("      WHERE SL.EMP_BOSS = 'REGION_CENTER_EMP' ");
		sql.append("      UNION ");
		sql.append("      SELECT SL.SEQ,  SL.EMP_BOSS, SL.EMP_ID AS SALES_PERSON, REL.EMP_ID AS BOSS_ID, REL.PRIVILEGEID ");
		sql.append("      FROM SALEREC_LIST SL ");
		sql.append("      LEFT JOIN VWORG_EMP_INFO REL ON REL.PRIVILEGEID = '046' ");
		sql.append("      WHERE SL.EMP_BOSS = 'HEAD_EMP' ");
		sql.append("    ) UNPIVOT (EMP_ID FOR EMP_LIST IN (SALES_PERSON, BOSS_ID)) ");
		sql.append("  ) SALEREC ");
		sql.append("  LEFT JOIN TBORG_MEMBER M ON SALEREC.EMP_ID = M.EMP_ID ");
		sql.append(") ");

		sql.append("SELECT (SELECT BB.SEQ FROM BASE_BOSS BB WHERE BB.EMP_ID = AG.EMP_ID) AS SEQ, ");
		sql.append("	   AGENT_ID AS EMP_ID, ");
		sql.append("	   (SELECT BB.PRIVILEGEID FROM BASE_BOSS BB WHERE BB.EMP_ID = AG.EMP_ID) AS PRIVILEGEID, ");
		sql.append("	   M.EMP_EMAIL_ADDRESS ");
		sql.append("FROM TBORG_AGENT AG ");
		sql.append("LEFT JOIN TBORG_MEMBER M ON AG.AGENT_ID = M.EMP_ID ");
		sql.append("WHERE AG.EMP_ID IN (SELECT EMP_ID FROM BASE_BOSS) ");
		sql.append("AND SYSDATE BETWEEN AG.START_DATE AND AG.END_DATE ");
		sql.append("AND AG.AGENT_STATUS = 'S' ");
		sql.append("UNION ");
		sql.append("SELECT SEQ, EMP_ID, PRIVILEGEID, EMP_EMAIL_ADDRESS ");
		sql.append("FROM BASE_BOSS ");
		queryCondition.setObject("seq", seq);
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);

		return list;
	}

}