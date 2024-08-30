package com.systex.jbranch.fubon.bth.crm;

import java.io.FileWriter;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBCRM_TRS_AOCHG_PLISTVO;
import com.systex.jbranch.app.common.fps.table.TBORG_BRH_CONTACTVO;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.bizLogic.BizLogic;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;
@Component("btcrm3501")
@Scope("prototype")
public class BTCRM3501 extends BizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	//SimpleDateFormat sdf2 = new SimpleDateFormat("MMdd");
	
	public void calculateTRSData(Object body, IPrimitiveMap<?> header) throws Exception {
		dam = this.getDataAccessManager();
		
		// 每日抓客群身份又同時是空Code的客戶名單，直接丟主管待覆核清單
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT CUST_ID, BRA_NBR FROM TBCRM_CUST_MAST ");
		sql.append("WHERE VIP_DEGREE in ('H', 'T', 'K', 'C') AND AO_CODE IS NULL ");
		sql.append("AND CUST_ID NOT in (SELECT CUST_ID FROM TBCRM_TRS_AOCHG_PLIST WHERE PROCESS_STATUS in ('L0', 'L1', 'L2', 'L3', 'L4', 'L5', 'BS')) ");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		for(Map<String, Object> map : list) {
			BigDecimal SN = new BigDecimal(getSN());
			TBCRM_TRS_AOCHG_PLISTVO vo = new TBCRM_TRS_AOCHG_PLISTVO();
			vo.setSEQ(SN);
			vo.setCUST_ID(ObjectUtils.toString(map.get("CUST_ID")));
			vo.setORG_AO_BRH(ObjectUtils.toString(map.get("BRA_NBR")));
			vo.setNEW_AO_BRH(ObjectUtils.toString(map.get("BRA_NBR")));
			vo.setAPL_EMP_ID("SYSTEM");
			vo.setAPL_EMP_ROLE("SYSTEM");
			vo.setAPL_DATETIME(new Timestamp(System.currentTimeMillis()));
			vo.setAPL_REASON("16");
			vo.setTRS_FLOW_TYPE("1");
			vo.setPROCESS_STATUS("L1");
			vo.setTRS_TYPE("1");
			vo.setTRS_TXN_SOURCE("6");
			dam.create(vo);
		}
	}
	
	public void getBranchContactInfo(Object body, IPrimitiveMap<?> header) throws Exception {
		dam = this.getDataAccessManager();
		
		// 吃檔 VIPIQ21.TXT 寫入 TBORG_IQ021_SG other will do
		
		// 讀取SG TABLE寫入分行聯絡資訊檔 TBCRM_BRH_CONTACT
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT BRH_COD, UNIT_COD, DB_APP_NO, CHK_DIGIT, CHIN_FL_NAME, CHIN_ALS, ZIP_COD, CHIN_ADDR, ENGL_FL_NAME, ENGL_ADDR, WWW_ADDR, TEL_NO_MAIN, TEL_NO_MGER, TEL_NO_SADP, TEL_NO_SERV, TEL_NO_LNDP, TEL_NO_DBOX, TEL_NO_FCDP, TEL_NO_FAX, TEL_NO_TLX1, TEL_NO_TLX2, BRH_TYPE, BRH_BUS, GL_BRH_TYPE, BRH_ACT, BOOKING_CUR, DIST_COD, EXCG_COD, OFC_CTBRH, FRT_BUS_TYPE, FRT_BUS_CTBRH, FRT_BUS_ODT, SWIFT_NO, ASGN_NAM, NORL_BUS_ODT, BONDS_BUS, BONDS_BUS_ODT, BOX_BUS, BOX_BUS_ODT, LOCAL_SERVER1, LOCAL_SERVER2, LST_WK_DATE, SIGNON_TIME, SIGNOFF_TIME, SIGNON_TIM_24H, SIGNOFF_TIM_24H, LST_UP_STS_TIME, PRS_STS, COR_PRS_STS, T_TX_BRH_STS, F_TX_BRH_STS, NET_BRH_STS, INT_TRAN_FLG, NTD_CLS_TLR_NO, NTD_CLS_TIME, NTD_CLS_SPV_NO, NTD_CLS_CAN_TLR, NTD_CLS_CAN_SPV, NTD_CLS_CAN_TIME, AUDIT_COD, INACT_NO, EFT_DATE, MGR_NAME, PUNCH_CARD, CREATE_DATE, TX_EMP_ID, AUT_EMP_ID, LST_MTN_DATE, LST_MTN_TIME, ACTING_BRH, CONF_BDG_FLG ");
		sql.append("FROM TBORG_IQ021_SG ");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		for(Map<String, Object> map : list) {
			if(map.get("BRH_COD") == null)
				continue;
			// if exist update
			TBORG_BRH_CONTACTVO qvo = new TBORG_BRH_CONTACTVO();
			qvo = (TBORG_BRH_CONTACTVO) dam.findByPKey(TBORG_BRH_CONTACTVO.TABLE_UID, ObjectUtils.toString(map.get("BRH_COD")));
			if (qvo != null) {
				qvo.setUNIT_COD(ObjectUtils.toString(map.get("UNIT_COD")));
				if(map.get("DB_APP_NO") != null)
					qvo.setDB_APP_NO(new BigDecimal(ObjectUtils.toString(map.get("DB_APP_NO"))));
				qvo.setCHK_DIGIT(ObjectUtils.toString(map.get("CHK_DIGIT")));
				qvo.setCHIN_FL_NAME(ObjectUtils.toString(map.get("CHIN_FL_NAME")));
				qvo.setCHIN_ALS(ObjectUtils.toString(map.get("CHIN_ALS")));
				qvo.setZIP_COD(ObjectUtils.toString(map.get("ZIP_COD")));
				qvo.setCHIN_ADDR(ObjectUtils.toString(map.get("CHIN_ADDR")));
				qvo.setENGL_FL_NAME(ObjectUtils.toString(map.get("ENGL_FL_NAME")));
				qvo.setENGL_ADDR(ObjectUtils.toString(map.get("ENGL_ADDR")));
				qvo.setWWW_ADDR(ObjectUtils.toString(map.get("WWW_ADDR")));
				qvo.setTEL_NO_MAIN(ObjectUtils.toString(map.get("TEL_NO_MAIN")));
				qvo.setTEL_NO_MGER(ObjectUtils.toString(map.get("TEL_NO_MGER")));
				qvo.setTEL_NO_SADP(ObjectUtils.toString(map.get("TEL_NO_SADP")));
				qvo.setTEL_NO_SERV(ObjectUtils.toString(map.get("TEL_NO_SERV")));
				qvo.setTEL_NO_LNDP(ObjectUtils.toString(map.get("TEL_NO_LNDP")));
				qvo.setTEL_NO_DBOX(ObjectUtils.toString(map.get("TEL_NO_DBOX")));
				qvo.setTEL_NO_FCDP(ObjectUtils.toString(map.get("TEL_NO_FCDP")));
				qvo.setTEL_NO_FAX(ObjectUtils.toString(map.get("TEL_NO_FAX")));
				qvo.setTEL_NO_TLX1(ObjectUtils.toString(map.get("TEL_NO_TLX1")));
				qvo.setTEL_NO_TLX2(ObjectUtils.toString(map.get("TEL_NO_TLX2")));
				qvo.setBRH_TYPE(ObjectUtils.toString(map.get("BRH_TYPE")));
				qvo.setBRH_BUS(ObjectUtils.toString(map.get("BRH_BUS")));
				qvo.setGL_BRH_TYPE(ObjectUtils.toString(map.get("GL_BRH_TYPE")));
				qvo.setBRH_ACT(ObjectUtils.toString(map.get("BRH_ACT")));
				qvo.setBOOKING_CUR(ObjectUtils.toString(map.get("BOOKING_CUR")));
				qvo.setDIST_COD(ObjectUtils.toString(map.get("DIST_COD")));
				qvo.setEXCG_COD(ObjectUtils.toString(map.get("EXCG_COD")));
				qvo.setOFC_CTBRH(ObjectUtils.toString(map.get("OFC_CTBRH")));
				qvo.setFRT_BUS_TYPE(ObjectUtils.toString(map.get("FRT_BUS_TYPE")));
				qvo.setFRT_BUS_CTBRH(ObjectUtils.toString(map.get("FRT_BUS_CTBRH")));
				if(map.get("FRT_BUS_ODT") != null)
					qvo.setFRT_BUS_ODT(new Timestamp(((Date)map.get("FRT_BUS_ODT")).getTime()));
				qvo.setSWIFT_NO(ObjectUtils.toString(map.get("SWIFT_NO")));
				qvo.setASGN_NAM(ObjectUtils.toString(map.get("ASGN_NAM")));
				if(map.get("NORL_BUS_ODT") != null)
					qvo.setNORL_BUS_ODT(new Timestamp(((Date)map.get("NORL_BUS_ODT")).getTime()));
				qvo.setBONDS_BUS(ObjectUtils.toString(map.get("BONDS_BUS")));
				if(map.get("BONDS_BUS_ODT") != null)
					qvo.setBONDS_BUS_ODT(new Timestamp(((Date)map.get("BONDS_BUS_ODT")).getTime()));
				qvo.setBOX_BUS(ObjectUtils.toString(map.get("BOX_BUS")));
				if(map.get("BOX_BUS_ODT") != null)
					qvo.setBOX_BUS_ODT(new Timestamp(((Date)map.get("BOX_BUS_ODT")).getTime()));
				qvo.setLOCAL_SERVER1(ObjectUtils.toString(map.get("LOCAL_SERVER1")));
				qvo.setLOCAL_SERVER2(ObjectUtils.toString(map.get("LOCAL_SERVER2")));
				if(map.get("LST_WK_DATE") != null)
					qvo.setLST_WK_DATE(new Timestamp(((Date)map.get("LST_WK_DATE")).getTime()));
				qvo.setSIGNON_TIME(ObjectUtils.toString(map.get("SIGNON_TIME")));
				qvo.setSIGNOFF_TIME(ObjectUtils.toString(map.get("SIGNOFF_TIME")));
				qvo.setSIGNON_TIM_24H(ObjectUtils.toString(map.get("SIGNON_TIM_24H")));
				qvo.setSIGNOFF_TIM_24H(ObjectUtils.toString(map.get("SIGNOFF_TIM_24H")));
				qvo.setLST_UP_STS_TIME(ObjectUtils.toString(map.get("LST_UP_STS_TIME")));
				qvo.setPRS_STS(ObjectUtils.toString(map.get("PRS_STS")));
				qvo.setCOR_PRS_STS(ObjectUtils.toString(map.get("COR_PRS_STS")));
				qvo.setT_TX_BRH_STS(ObjectUtils.toString(map.get("T_TX_BRH_STS")));
				qvo.setF_TX_BRH_STS(ObjectUtils.toString(map.get("F_TX_BRH_STS")));
				qvo.setNET_BRH_STS(ObjectUtils.toString(map.get("NET_BRH_STS")));
				qvo.setINT_TRAN_FLG(ObjectUtils.toString(map.get("INT_TRAN_FLG")));
				qvo.setNTD_CLS_TLR_NO(ObjectUtils.toString(map.get("NTD_CLS_TLR_NO")));
				qvo.setNTD_CLS_TIME(ObjectUtils.toString(map.get("NTD_CLS_TIME")));
				qvo.setNTD_CLS_SPV_NO(ObjectUtils.toString(map.get("NTD_CLS_SPV_NO")));
				qvo.setNTD_CLS_CAN_TLR(ObjectUtils.toString(map.get("NTD_CLS_CAN_TLR")));
				qvo.setNTD_CLS_CAN_SPV(ObjectUtils.toString(map.get("NTD_CLS_CAN_SPV")));
				qvo.setNTD_CLS_CAN_TIME(ObjectUtils.toString(map.get("NTD_CLS_CAN_TIME")));
				qvo.setAUDIT_COD(ObjectUtils.toString(map.get("AUDIT_COD")));
				qvo.setINACT_NO(ObjectUtils.toString(map.get("INACT_NO")));
				if(map.get("EFT_DATE") != null)
					qvo.setEFT_DATE(new Timestamp(((Date)map.get("EFT_DATE")).getTime()));
				qvo.setMGR_NAME(ObjectUtils.toString(map.get("MGR_NAME")));
				qvo.setPUNCH_CARD(ObjectUtils.toString(map.get("PUNCH_CARD")));
				if(map.get("CREATE_DATE") != null&&isThisDateValid(map.get("CREATE_DATE").toString()))
					qvo.setCREATE_DATE(new Timestamp(((Date)map.get("CREATE_DATE")).getTime()));
				qvo.setTX_EMP_ID(ObjectUtils.toString(map.get("TX_EMP_ID")));
				qvo.setAUT_EMP_ID(ObjectUtils.toString(map.get("AUT_EMP_ID")));
				if(map.get("LST_MTN_DATE") != null&&isThisDateValid(map.get("LST_MTN_DATE").toString()))
					qvo.setLST_MTN_DATE(new Timestamp(((Date)map.get("LST_MTN_DATE")).getTime()));
				qvo.setLST_MTN_TIME(ObjectUtils.toString(map.get("LST_MTN_TIME")));
				qvo.setACTING_BRH(ObjectUtils.toString(map.get("ACTING_BRH")));
				qvo.setCONF_BDG_FLG(ObjectUtils.toString(map.get("CONF_BDG_FLG")));
				dam.update(qvo);
			}
			// add
			else {
				TBORG_BRH_CONTACTVO vo = new TBORG_BRH_CONTACTVO();
				vo.setBRH_COD(ObjectUtils.toString(map.get("BRH_COD")));
				vo.setUNIT_COD(ObjectUtils.toString(map.get("UNIT_COD")));
				if(map.get("DB_APP_NO") != null)
					vo.setDB_APP_NO(new BigDecimal(ObjectUtils.toString(map.get("DB_APP_NO"))));
				vo.setCHK_DIGIT(ObjectUtils.toString(map.get("CHK_DIGIT")));
				vo.setCHIN_FL_NAME(ObjectUtils.toString(map.get("CHIN_FL_NAME")));
				vo.setCHIN_ALS(ObjectUtils.toString(map.get("CHIN_ALS")));
				vo.setZIP_COD(ObjectUtils.toString(map.get("ZIP_COD")));
				vo.setCHIN_ADDR(ObjectUtils.toString(map.get("CHIN_ADDR")));
				vo.setENGL_FL_NAME(ObjectUtils.toString(map.get("ENGL_FL_NAME")));
				vo.setENGL_ADDR(ObjectUtils.toString(map.get("ENGL_ADDR")));
				vo.setWWW_ADDR(ObjectUtils.toString(map.get("WWW_ADDR")));
				vo.setTEL_NO_MAIN(ObjectUtils.toString(map.get("TEL_NO_MAIN")));
				vo.setTEL_NO_MGER(ObjectUtils.toString(map.get("TEL_NO_MGER")));
				vo.setTEL_NO_SADP(ObjectUtils.toString(map.get("TEL_NO_SADP")));
				vo.setTEL_NO_SERV(ObjectUtils.toString(map.get("TEL_NO_SERV")));
				vo.setTEL_NO_LNDP(ObjectUtils.toString(map.get("TEL_NO_LNDP")));
				vo.setTEL_NO_DBOX(ObjectUtils.toString(map.get("TEL_NO_DBOX")));
				vo.setTEL_NO_FCDP(ObjectUtils.toString(map.get("TEL_NO_FCDP")));
				vo.setTEL_NO_FAX(ObjectUtils.toString(map.get("TEL_NO_FAX")));
				vo.setTEL_NO_TLX1(ObjectUtils.toString(map.get("TEL_NO_TLX1")));
				vo.setTEL_NO_TLX2(ObjectUtils.toString(map.get("TEL_NO_TLX2")));
				vo.setBRH_TYPE(ObjectUtils.toString(map.get("BRH_TYPE")));
				vo.setBRH_BUS(ObjectUtils.toString(map.get("BRH_BUS")));
				vo.setGL_BRH_TYPE(ObjectUtils.toString(map.get("GL_BRH_TYPE")));
				vo.setBRH_ACT(ObjectUtils.toString(map.get("BRH_ACT")));
				vo.setBOOKING_CUR(ObjectUtils.toString(map.get("BOOKING_CUR")));
				vo.setDIST_COD(ObjectUtils.toString(map.get("DIST_COD")));
				vo.setEXCG_COD(ObjectUtils.toString(map.get("EXCG_COD")));
				vo.setOFC_CTBRH(ObjectUtils.toString(map.get("OFC_CTBRH")));
				vo.setFRT_BUS_TYPE(ObjectUtils.toString(map.get("FRT_BUS_TYPE")));
				vo.setFRT_BUS_CTBRH(ObjectUtils.toString(map.get("FRT_BUS_CTBRH")));
				if(map.get("FRT_BUS_ODT") != null)
					vo.setFRT_BUS_ODT(new Timestamp(((Date)map.get("FRT_BUS_ODT")).getTime()));
				vo.setSWIFT_NO(ObjectUtils.toString(map.get("SWIFT_NO")));
				vo.setASGN_NAM(ObjectUtils.toString(map.get("ASGN_NAM")));
				if(map.get("NORL_BUS_ODT") != null)
					vo.setNORL_BUS_ODT(new Timestamp(((Date)map.get("NORL_BUS_ODT")).getTime()));
				vo.setBONDS_BUS(ObjectUtils.toString(map.get("BONDS_BUS")));
				if(map.get("BONDS_BUS_ODT") != null)
					vo.setBONDS_BUS_ODT(new Timestamp(((Date)map.get("BONDS_BUS_ODT")).getTime()));
				vo.setBOX_BUS(ObjectUtils.toString(map.get("BOX_BUS")));
				if(map.get("BOX_BUS_ODT") != null)
					vo.setBOX_BUS_ODT(new Timestamp(((Date)map.get("BOX_BUS_ODT")).getTime()));
				vo.setLOCAL_SERVER1(ObjectUtils.toString(map.get("LOCAL_SERVER1")));
				vo.setLOCAL_SERVER2(ObjectUtils.toString(map.get("LOCAL_SERVER2")));
				if(map.get("LST_WK_DATE") != null)
					vo.setLST_WK_DATE(new Timestamp(((Date)map.get("LST_WK_DATE")).getTime()));
				vo.setSIGNON_TIME(ObjectUtils.toString(map.get("SIGNON_TIME")));
				vo.setSIGNOFF_TIME(ObjectUtils.toString(map.get("SIGNOFF_TIME")));
				vo.setSIGNON_TIM_24H(ObjectUtils.toString(map.get("SIGNON_TIM_24H")));
				vo.setSIGNOFF_TIM_24H(ObjectUtils.toString(map.get("SIGNOFF_TIM_24H")));
				vo.setLST_UP_STS_TIME(ObjectUtils.toString(map.get("LST_UP_STS_TIME")));
				vo.setPRS_STS(ObjectUtils.toString(map.get("PRS_STS")));
				vo.setCOR_PRS_STS(ObjectUtils.toString(map.get("COR_PRS_STS")));
				vo.setT_TX_BRH_STS(ObjectUtils.toString(map.get("T_TX_BRH_STS")));
				vo.setF_TX_BRH_STS(ObjectUtils.toString(map.get("F_TX_BRH_STS")));
				vo.setNET_BRH_STS(ObjectUtils.toString(map.get("NET_BRH_STS")));
				vo.setINT_TRAN_FLG(ObjectUtils.toString(map.get("INT_TRAN_FLG")));
				vo.setNTD_CLS_TLR_NO(ObjectUtils.toString(map.get("NTD_CLS_TLR_NO")));
				vo.setNTD_CLS_TIME(ObjectUtils.toString(map.get("NTD_CLS_TIME")));
				vo.setNTD_CLS_SPV_NO(ObjectUtils.toString(map.get("NTD_CLS_SPV_NO")));
				vo.setNTD_CLS_CAN_TLR(ObjectUtils.toString(map.get("NTD_CLS_CAN_TLR")));
				vo.setNTD_CLS_CAN_SPV(ObjectUtils.toString(map.get("NTD_CLS_CAN_SPV")));
				vo.setNTD_CLS_CAN_TIME(ObjectUtils.toString(map.get("NTD_CLS_CAN_TIME")));
				vo.setAUDIT_COD(ObjectUtils.toString(map.get("AUDIT_COD")));
				vo.setINACT_NO(ObjectUtils.toString(map.get("INACT_NO")));
				if(map.get("EFT_DATE") != null)
					vo.setEFT_DATE(new Timestamp(((Date)map.get("EFT_DATE")).getTime()));
				vo.setMGR_NAME(ObjectUtils.toString(map.get("MGR_NAME")));
				vo.setPUNCH_CARD(ObjectUtils.toString(map.get("PUNCH_CARD")));
				if(map.get("CREATE_DATE") != null)
					vo.setCREATE_DATE(new Timestamp(((Date)map.get("CREATE_DATE")).getTime()));
				vo.setTX_EMP_ID(ObjectUtils.toString(map.get("TX_EMP_ID")));
				vo.setAUT_EMP_ID(ObjectUtils.toString(map.get("AUT_EMP_ID")));
				if(map.get("LST_MTN_DATE") != null)
					vo.setLST_MTN_DATE(new Timestamp(((Date)map.get("LST_MTN_DATE")).getTime()));
				vo.setLST_MTN_TIME(ObjectUtils.toString(map.get("LST_MTN_TIME")));
				vo.setACTING_BRH(ObjectUtils.toString(map.get("ACTING_BRH")));
				vo.setCONF_BDG_FLG(ObjectUtils.toString(map.get("CONF_BDG_FLG")));
				dam.create(vo);
			}
		}
	}
	
	public void uploadDelCamFile(Object body, IPrimitiveMap<?> header) throws Exception {
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition1 = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_SQL);
		StringBuffer sql1 = new StringBuffer();
		//#5835 檢視之前產出的換手經營名單若已不符合條件時,需更新為無效名單:PROCESS_STATUS = 'F99'
		sql1.append("UPDATE (");
		sql1.append("	SELECT P.* ");
		sql1.append("	FROM TBCRM_TRS_AOCHG_PLIST P ");
		sql1.append("	LEFT JOIN TBCRM_CUST_NOTE CN ON P.CUST_ID = CN.CUST_ID ");
		sql1.append("	LEFT JOIN TBCRM_CUST_MAST CM ON P.CUST_ID = CM.CUST_ID ");
		sql1.append("	WHERE P.TRS_TYPE = '7' ");
		//正在待移轉清單的資料
		sql1.append("	AND P.PROCESS_STATUS in ('L0','L1', 'L2', 'L3', 'L4', 'L5', 'BS') ");
		//具客訴戶/禁止銷售註記之客戶NS/拒絕接受銷售註記之客戶RS任何一項註記者
		sql1.append("	AND (CN.COMM_RS_YN = 'Y'  OR CN.COMM_NS_YN = 'Y' OR CN.COMPLAIN_YN = 'Y' ");
		//特定客戶註記(符合其中任一條件：國中學歷以下、年齡70歲以上、持有健保局核發重大傷病証明卡客戶)
		sql1.append("		 OR CN.SP_CUST_YN = 'Y' ");
		//客戶評估客戶風險承受度(Know Your Customer 簡稱KYC)註記為C0
		sql1.append("		 OR NVL(CM.CUST_RISK_ATR, ' ') = 'C0' ");
		//高端客戶
		sql1.append("        OR EXISTS ( ");
		sql1.append("          SELECT CUST_ID FROM TBORG_CUST_UHRM_PLIST UP WHERE UP.CUST_ID = P.CUST_ID ");
		sql1.append("        ) ");
		//法人金融版塊客戶
		sql1.append("		OR CN.CO_ACCT_YN = 'Y') ");
		//排除公司戶    
		sql1.append("    AND LENGTH(P.CUST_ID) < 10 ");
		sql1.append(") t SET t.PROCESS_STATUS = 'F99', t.LASTUPDATE = SYSDATE, t.MODIFIER = 'BTCRM3501_uploadDelCamFile'	");				
		queryCondition1.setQueryString(sql1.toString());				
		dam.exeUpdate(queryCondition1);
		
		
		// 2017/10/12 mantis 3832
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT CU.CUST_ID,CU.BRA_NBR,CU.AO_CODE ");
		sql.append("FROM TBCRM_CUST_MAST CU ");
		sql.append("LEFT JOIN TBCRM_CUST_NOTE CN ON CU.CUST_ID = CN.CUST_ID ");
		sql.append("WHERE TO_CHAR(BIRTH_DATE,'MM') = TO_CHAR(SYSDATE,'MM') ");
		
		// --0.找出目前有人經營的客戶
		sql.append("AND NVL(CU.AO_CODE,' ') <> ' ' ");
		
		// --0.排除近一年有手收客戶
		sql.append("AND NOT EXISTS(");
		sql.append("  SELECT 'Y' ");
		sql.append("  FROM (");
		sql.append("    SELECT CUST_ID, SUM(TXN_FEE) ");
		sql.append("    FROM TBCRM_CUST_PROFEE ");
		sql.append("    WHERE DATA_YEAR || DATA_MONTH >= TO_CHAR(ADD_MONTHS(SYSDATE,-12),'YYYYMM') ");
		sql.append("    GROUP BY CUST_ID ");
		sql.append("    HAVING SUM(TXN_FEE) > 0");
		sql.append("  ) FEE ");
		sql.append("  WHERE CU.CUST_ID = FEE.CUST_ID ) ");
		
		// --1.排除FCH轄下客戶
		sql.append("AND NOT EXISTS( ");
		sql.append("  SELECT 1 ");
		sql.append("  FROM TBORG_MEMBER MEM ");
		sql.append("  LEFT JOIN TBORG_MEMBER_ROLE ROL ON MEM.EMP_ID = ROL.EMP_ID ");
		sql.append("  LEFT JOIN TBSYSSECUROLPRIASS PRI ON ROL.ROLE_ID = PRI.ROLEID ");
		sql.append("  LEFT JOIN TBORG_SALES_AOCODE AO ON MEM.EMP_ID = AO.EMP_ID ");
		sql.append("  WHERE MEM.SERVICE_FLAG = 'A' ");
		sql.append("  AND CHANGE_FLAG IN ('A', 'M', 'P') ");
		sql.append("  AND PRI.PRIVILEGEID = '003' ");
		sql.append("  AND CU.AO_CODE = AO.AO_CODE ");
		sql.append(") ");
		
		// -- 1.1排除維護CODE
		sql.append("AND NOT EXISTS(SELECT 'Y' FROM TBORG_SALES_AOCODE AO WHERE CU.AO_CODE =  AO.AO_CODE AND AO.TYPE = '3') ");
		
		// --2.排除法人金融版塊客戶
		sql.append("AND CN.CO_ACCT_YN <> 'Y' ");
		
		// --3.所屬理財專員服務經營未滿半年
		sql.append("AND ADD_MONTHS(CU.AO_LASTUPDATE,6) < SYSDATE ");
		
		// --4.年齡大於80且非專投
		sql.append("AND NOT((SYSDATE - BIRTH_DATE) / 365 >= 80 AND CN.PROF_INVESTOR_YN <> 'Y') ");
		
		// --5.客戶評估客戶風險承受度(Know Your Customer 簡稱KYC)註記為C0
		sql.append("AND CU.CUST_RISK_ATR <> 'C0' ");
		
		// --6.特定客戶註記(符合其中任一條件：國中學歷以下、年齡70歲以上、持有健保局核發重大傷病証明卡客戶)
		sql.append("AND CN.SP_CUST_YN <> 'Y' ");
		
		// --7.具客訴戶/禁止銷售註記之客戶NS/拒絕接受銷售註記之客戶RS任何一項註記者。
		sql.append("AND CN.COMM_RS_YN <> 'Y'  AND CN.COMM_NS_YN <> 'Y' AND CN.COMPLAIN_YN <> 'Y' ");
		
		// --8.排除重複名單
		sql.append("AND NOT EXISTS ( ");
		sql.append("  SELECT 'Y' ");
		sql.append("  FROM TBCRM_TRS_AOCHG_PLIST D ");
		sql.append("  WHERE D.PROCESS_STATUS IN ('L0', 'L1', 'L2', 'L3', 'L4', 'L5', 'BS') ");
		sql.append("  AND CU.CUST_ID=D.CUST_ID ");
		sql.append(") ");
		
		// --9.排除高端客戶
		sql.append("AND NOT EXISTS ( ");
		sql.append("  SELECT CUST_ID FROM TBORG_CUST_UHRM_PLIST UP WHERE UP.CUST_ID = CU.CUST_ID ");
		sql.append(") ");
		

		// --10.排除公司戶
		sql.append("AND LENGTH(CU.CUST_ID) >= 10 ");
		
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		for(Map<String, Object> map : list) {
			BigDecimal SN = new BigDecimal(getSN());
			TBCRM_TRS_AOCHG_PLISTVO pvo = new TBCRM_TRS_AOCHG_PLISTVO();
			pvo.setSEQ(SN);
			pvo.setCUST_ID(ObjectUtils.toString(map.get("CUST_ID")));
			pvo.setORG_AO_CODE(ObjectUtils.toString(map.get("AO_CODE")));
			pvo.setORG_AO_BRH(ObjectUtils.toString(map.get("BRA_NBR")));
			pvo.setNEW_AO_BRH(ObjectUtils.toString(map.get("BRA_NBR")));
			pvo.setAPL_EMP_ID("SYSTEM");
			pvo.setAPL_EMP_ROLE("SYSTEM");
			pvo.setAPL_DATETIME(new Timestamp(System.currentTimeMillis()));
			pvo.setAPL_REASON("97");
			pvo.setTRS_FLOW_TYPE("2");
			pvo.setPROCESS_STATUS("L1");
			pvo.setTRS_TYPE("7");
			pvo.setTRS_TXN_SOURCE("5");
			dam.create(pvo);
		}
		
		//#1859_換手經營名單邏輯調整
		QueryConditionIF queryCondition2 = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_SQL);
		StringBuffer sql2 = new StringBuffer();
		sql2.append(" update TBCRM_TRS_AOCHG_PLIST ");
		sql2.append(" set ");
		sql2.append(" PROCESS_STATUS = 'F', ");
		sql2.append(" LASTUPDATE = SYSDATE, ");
		sql2.append(" MODIFIER = 'BTCRM3501_uploadDelCamFile_#1859' ");
		sql2.append(" WHERE ");
		sql2.append(" TRS_TYPE = '7' ");
		sql2.append(" AND APL_REASON = '97' ");
		sql2.append(" AND APL_EMP_ID = 'SYSTEM' ");
		sql2.append(" AND APL_EMP_ROLE = 'SYSTEM' ");
		sql2.append(" AND TRS_TXN_SOURCE = '5' ");
		sql2.append(" AND MODIFIER = 'SCHEDULER' "); //用來區分還沒有被動過的條件
		sql2.append(" AND TO_CHAR(APL_DATETIME,'yyyyMM') = TO_CHAR(ADD_MONTHS(SYSDATE,-1) , 'yyyyMM') ");
		queryCondition2.setQueryString(sql2.toString());				
		dam.exeUpdate(queryCondition2);
		
	}
	
	public void uploadDelCamFile2(Object body, IPrimitiveMap<?> header) throws Exception {
		dam = this.getDataAccessManager();
		
		String tempPath = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH) + "reports\\";
		String fileName = "WM_SPLIT_O_N200_" + sdf.format(new Date()) + "." + sdf.format(new Date());
		String fileName2 = "BR_SPLIT_O_N200_" + sdf.format(new Date()) + "." + sdf.format(new Date());
		// 取得新上傳分流名單(上傳格式)
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT SEQ_NO, BRH_COD, CONTACT_ADDRESS, BRH_OTH, POINTER_1, POINTER_2, SNAP_DATE FROM TBCRM_TRS_DELCAM_ULIST_DTL ");
		sql.append("WHERE 1=1 AND LIST_SEQ IN (SELECT SEQ FROM TBCRM_TRS_DELCAM_ULIST_MAST WHERE LIST_STATUS = 'U') ");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		
		// 將名單寫到檔案
		FileWriter fw = new FileWriter(tempPath + fileName + ".NTF");
		
		if(list.size() > 0) {
			fw.write("\r\n");
			fw.write("\\upload\\A0000353\\GIS\r\n"); //fw.write("D:\\BNK_D\\CDWEA01\\GIS\\ \r\n");
			fw.write("hsinyi.chang@fubon.com,laurance.liang@fubon.com,jimmy.chen@fubon.com,zoe.m.wu@fubon.com,dolly.kuo@fubon.com\r\n");
			fw.write(fileName + "\r\n");
			fw.write(fileName2 + "\r\n");
		}
		fw.close();
		// 輸出資料檔-名單檔
		FileWriter fw2 = new FileWriter(tempPath + fileName);
		
		for(Map<String, Object> map : list) {
			String temp = "";
			temp += LenString(ObjectUtils.toString(map.get("SEQ_NO")), 15);
			temp += LenString(ObjectUtils.toString(map.get("BRH_COD")), 3);
			temp += LenString(ObjectUtils.toString(map.get("CONTACT_ADDRESS")), 250);
			temp += LenString(ObjectUtils.toString(map.get("BRH_OTH")), 3);
			temp += LenString(ObjectUtils.toString(map.get("POINTER_1")), 10);
			temp += LenString(ObjectUtils.toString(map.get("POINTER_2")), 10);
			temp += LenString(ObjectUtils.toString(map.get("SNAP_DATE")), 8);
			
			fw2.write(temp+"\r\n");
		}
		fw2.close();
		
		// 輸出資料檔-分行檔
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		sql = new StringBuffer();
		sql.append("SELECT BRH_NBR, BRH_NAME, BRH_ADDR, BRH_TEL FROM TBCRM_TRS_DELCAM_ULIST_BRH ");
		sql.append("WHERE LIST_SEQ IN (SELECT SEQ FROM TBCRM_TRS_DELCAM_ULIST_MAST WHERE LIST_STATUS = 'U') ");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> list2 = dam.exeQuery(queryCondition);
		FileWriter fw3 = new FileWriter(tempPath + fileName2);
		
		for(Map<String, Object> map : list2) {
			String temp = "";
			temp += LenString(ObjectUtils.toString(map.get("BRH_NBR")), 3);
			temp += LenString(ObjectUtils.toString(map.get("BRH_NAME")), 22);
			temp += LenString(ObjectUtils.toString(map.get("BRH_ADDR")), 250);
			temp += LenString(ObjectUtils.toString(map.get("BRH_TEL")), 20);
			
			fw3.write(temp+"\r\n");
		}
		fw3.close();
		
		// TBCRM_TRS_DELCAM_ULIST_MAST
		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition.setQueryString("UPDATE TBCRM_TRS_DELCAM_ULIST_MAST M SET LIST_STATUS = '0' WHERE LIST_STATUS = 'U'");
		dam.exeUpdate(queryCondition);
	}
	
	
	private String getSN() throws JBranchException {
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT TBCRM_TRS_AOCHG_PLIST_SEQ.nextval AS SEQ FROM DUAL ");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> SEQLIST = dam.exeQuery(queryCondition);
		
		return ObjectUtils.toString(SEQLIST.get(0).get("SEQ"));
	}
	
	private String getSN2() throws JBranchException {
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT SQ_TBCRM_TRS_DELCAM_COMM_FAIL.nextval AS SEQ FROM DUAL ");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> SEQLIST = dam.exeQuery(queryCondition);
		
		return ObjectUtils.toString(SEQLIST.get(0).get("SEQ"));
	}
	
	private String getSN3() throws JBranchException {
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT SQ_TBCRM_TRS_DELCAM_COMM_DTL.nextval AS SEQ FROM DUAL ");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> SEQLIST = dam.exeQuery(queryCondition);
		
		return ObjectUtils.toString(SEQLIST.get(0).get("SEQ"));
	}
	
	// 2017/4/17
	private String LenString(String inputStr, int StrLen) {
		String returnString = "";
	    int LenCtrl = 0;
	    int inputStrLen;
	    int i = 0;
	    
	    if (StringUtils.isBlank(inputStr)) {
	    	inputStrLen = 0;
	    	inputStr = "";
	    }
	    else
	    	inputStrLen = inputStr.length();
	    while (inputStrLen > i) {
	    	//判斷該字元是否為雙位元資料
	    	if (Integer.parseInt(Integer.toString(inputStr.charAt(i), 16), 16) >= 128) {
	    		if ( (StrLen - LenCtrl - 1) >= 2) {
	    			returnString = returnString + inputStr.substring(i, i + 1);
	    			LenCtrl = LenCtrl + 2;
	    		}
	        }
	    	else {
	    		if ( (StrLen - LenCtrl) >= 1 ) {
	    			returnString = returnString + inputStr.substring(i, i + 1);
	    			LenCtrl++;
	    		}
	        }
	    	i++;
	    }
	    
	    // 現有字串以後補空白
	    while(LenCtrl < StrLen) {
	    	returnString = returnString + " ";
	    	LenCtrl++;
	    }
	    // 表示該字串中含有雙位元資料
	    if (returnString.length() < StrLen)
	    	StrLen = returnString.length();
	    return returnString.substring(0, StrLen);
	}
	
	/**
	 * 判斷欄位資料是否為日期
	 * **/
	public boolean isThisDateValid(String dateToValidate){
		if(dateToValidate == null){
			return false;
		}

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		sdf.setLenient(false);

		try {

			//if not valid, it will throw ParseException
			Date date = sdf.parse(dateToValidate);

		} catch (ParseException e) {
			return false;
		}

		return true;
	}
	
	
	
}