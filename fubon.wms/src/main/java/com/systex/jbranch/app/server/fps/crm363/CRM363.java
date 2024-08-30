package com.systex.jbranch.app.server.fps.crm363;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBCRM_CUST_AOCODE_CHGLOGVO;
import com.systex.jbranch.app.common.fps.table.TBCRM_CUST_MASTVO;
import com.systex.jbranch.app.common.fps.table.TBCRM_TRS_AOCHG_PLISTVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * @author Levi
 * @date 2020/03/18
 * 
 */
@Component("crm363")
@Scope("request")
public class CRM363 extends FubonWmsBizLogic {
	
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(CRM363.class);

	/***
	 * 下拉式選單:所有專案，不包含加強管控_高端理專
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void getAllPRJ(Object body, IPrimitiveMap header) throws JBranchException {
		CRM363InputVO inputVO = (CRM363InputVO) body;
		CRM363OutputVO return_VO = new CRM363OutputVO();
		dam = this.getDataAccessManager();
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT PRJ_ID, PRJ_NAME, PRJ_STATUS, PRJ_TYPE FROM TBCRM_TRS_PRJ_MAST ");
		sql.append(" WHERE NVL(PRJ_SUB_TYPE, '0') <> '2' "); //不包含加強管控_高端理專
		
		if(StringUtils.isNotBlank(inputVO.getPRJ_TYPE())) {
			sql.append(" AND PRJ_TYPE = :prjType ");
			queryCondition.setObject("prjType", inputVO.getPRJ_TYPE());
		} else {
			sql.append(" AND PRJ_TYPE IS NOT NULL "); //輪調換手專案
		}

		sql.append("ORDER BY PRJ_ID DESC ");
		queryCondition.setQueryString(sql.toString());
		List list = dam.exeQuery(queryCondition);
		return_VO.setPrj_list(list);
		sendRtnObject(return_VO);
	}
	
	public void inquire(Object body, IPrimitiveMap header) throws JBranchException {
		CRM363InputVO inputVO = (CRM363InputVO) body;
		CRM363OutputVO outputVO = new CRM363OutputVO();
		dam = this.getDataAccessManager();

		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT DISTINCT P.CUST_ID, N.COMPLAIN_YN, C.CUST_NAME, P.TRS_TYPE, P.TEMP_CAL_YN, C.CON_DEGREE, P.SEQ AS TRS_SEQ, P.ACT_DATE, ");
		sql.append("       C.VIP_DEGREE, C.AUM_AMT, P.ORG_AO_CODE, P.NEW_AO_CODE, P.APL_REASON, P.APL_OTH_REASON, P.ORG_AO_BRH, ");
		sql.append("       P.OVER_CUST_NO_LIMIT_UP_YN, P.NEW_AO_BRH, P.TRS_FLOW_TYPE, P.PROCESS_STATUS, P.APL_DATETIME, ");
		sql.append("       EMP.EMP_NAME AS ORG_AO_NAME, DEFN1.BRANCH_NAME AS ORG_BRANCH_NAME, EMP.EMP_ID AS ORG_AO_EMP_ID, ");
		sql.append("	   EMP2.EMP_NAME AS NEW_AO_NAME, DEFN2.BRANCH_NAME AS NEW_BRANCH_NAME, EMP2.EMP_ID AS NEW_AO_EMP_ID, ");
		sql.append("	   (CASE EMP2.TYPE WHEN '1' THEN '(主)' WHEN '2' THEN '(副)' WHEN '3' THEN '(維護)' ELSE '' END) AS NEW_AO_TYPE, ");
		sql.append("	   Y.EMP_ID AS RM_AO_EMP_ID, Y.BRANCH_NBR AS RM_AO_BRANCH_NBR, ");
		sql.append("	   RMEMP.EMP_NAME AS RM_EMP_NAME, RMDEFN.BRANCH_NAME AS RM_AO_BRANCH_NAME, ");
		sql.append("	   CHGLOG.ORG_AO_CODE AS CHGLOG_AO_CODE, CHGLOG.ORG_AO_NAME AS CHGLOG_EMP_NAME, ");
		sql.append("	   (CASE CHEMP.TYPE WHEN '1' THEN '(主)' WHEN '2' THEN '(副)' WHEN '3' THEN '(維護)' ELSE '' END) AS CHGLOG_AO_TYPE ");
		sql.append(" FROM TBCRM_CUST_MAST C, TBCRM_CUST_NOTE N, TBCRM_TRS_AOCHG_PLIST P ");
		sql.append(" LEFT JOIN TBPMS_ROTATION_MAIN RM ON RM.PRJ_ID = P.PRJ_ID AND RM.CUST_ID = P.CUST_ID  ");
		sql.append(" LEFT JOIN TBPMS_ROTATION_5YCUST Y ON Y.PRJ_ID = RM.PRJ_ID AND Y.CUST_ID = RM.CUST_ID ");
		sql.append(" LEFT JOIN VWORG_DEFN_INFO DEFN1 ON DEFN1.BRANCH_NBR = P.ORG_AO_BRH ");
		sql.append(" LEFT JOIN VWORG_DEFN_INFO DEFN2 ON DEFN2.BRANCH_NBR = P.NEW_AO_BRH ");
		sql.append(" LEFT JOIN VWORG_DEFN_INFO RMDEFN ON RMDEFN.BRANCH_NBR = Y.BRANCH_NBR ");
		sql.append(" LEFT JOIN VWORG_AO_INFO EMP ON  P.ORG_AO_CODE = EMP.AO_CODE ");
		sql.append(" LEFT JOIN VWORG_AO_INFO EMP2 ON P.NEW_AO_CODE = EMP2.AO_CODE ");
		sql.append(" LEFT JOIN TBORG_MEMBER RMEMP ON RMEMP.EMP_ID = Y.EMP_ID ");
		sql.append(" LEFT JOIN TBCRM_CUST_AOCODE_CHGLOG CHGLOG ON CHGLOG.CUST_ID = P.CUST_ID AND CHGLOG.PRJ_ID = P.PRJ_ID ");
		sql.append(" LEFT JOIN VWORG_AO_INFO CHEMP ON CHGLOG.ORG_AO_CODE = CHEMP.AO_CODE ");
		sql.append(" WHERE C.CUST_ID = P.CUST_ID AND C.CUST_ID = N.CUST_ID ");
		sql.append(" AND P.PRJ_ID = :prjId "); //拔CODE，主管重新分派
		queryCondition.setObject("prjId", inputVO.getPRJ_ID());
		
		if(!inputVO.getPRIVILEGE_ID().matches("043|044")) { //總行分行管理科人員可以看到全部
			sql.append(" AND P.PROCESS_STATUS NOT IN ('F', 'S') ");
		}
		
		//客戶ID
		if (!StringUtils.isBlank(inputVO.getCust_id())) {
			sql.append("and P.CUST_ID = :cust_id ");
			queryCondition.setObject("cust_id", inputVO.getCust_id());
		}
		
		//客戶姓名
		if (!StringUtils.isBlank(inputVO.getCust_name())) {
			sql.append("AND C.CUST_NAME like :cust_name ");
			queryCondition.setObject("cust_name", "%" + inputVO.getCust_name() + "%");
		}
		
		//等級
		if (!StringUtils.isBlank(inputVO.getCon_degree())) {
			sql.append("and C.CON_DEGREE = :con_degree ");
			queryCondition.setObject("con_degree", inputVO.getCon_degree());
		}
		
		//客群身份
		if (!StringUtils.isBlank(inputVO.getVip_degree())) {
			sql.append("and C.VIP_DEGREE = :vip_degree ");
			queryCondition.setObject("vip_degree", inputVO.getVip_degree());
		}
		
//		// 舊理專篩選條件改為抓新理專, 沒改變數名
		if (StringUtils.isNotBlank(inputVO.getOrg_branch_nbr())) {
			sql.append("AND P.NEW_AO_BRH = :new_ao_brh ");
			queryCondition.setObject("new_ao_brh", inputVO.getOrg_branch_nbr());
		} else {
			sql.append("AND P.NEW_AO_BRH in (:branchlist) ");
			queryCondition.setObject("branchlist", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
		}
		
		
		//加入覆核流程&權限判斷
		
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> headmgrMap = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2);
		Map<String, String> bmmgrMap = xmlInfo.doGetVariable("FUBONSYS.BMMGR_ROLE", FormatHelper.FORMAT_2);
		Map<String, String> mbrmMap = xmlInfo.doGetVariable("FUBONSYS.MBRMGR_ROLE", FormatHelper.FORMAT_2);
		Map<String, String> armgrMap = xmlInfo.doGetVariable("FUBONSYS.ARMGR_ROLE", FormatHelper.FORMAT_2);

		//總行，非分行管理科人員
		if (headmgrMap.containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE)) && !inputVO.getPRIVILEGE_ID().matches("043|044")) {
			sql.append("AND P.PROCESS_STATUS in ('L1','L2','L3','L4') ");
		}
		//登入者身份=主管
		else if (bmmgrMap.containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			sql.append("AND P.PROCESS_STATUS = 'L1' ");

			//登入者身份=營運區督導
		} else if (mbrmMap.containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			sql.append("AND  P.PROCESS_STATUS = 'L2' "); 

			//登入者身份=區域中心主管
		} else if (armgrMap.containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			sql.append("AND  P.PROCESS_STATUS = 'L3' "); 
			
		} else if(!inputVO.getPRIVILEGE_ID().matches("043|044")) {
			throw new APException("使用者登入身分不適用");
		}
				
				
		sql.append(" ORDER BY P.NEW_AO_BRH, P.CUST_ID ");
		queryCondition.setQueryString(sql.toString());

		outputVO.setResultList(dam.exeQuery(queryCondition));
		this.sendRtnObject(outputVO);
	}

	public void save(Object body, IPrimitiveMap header) throws Exception {
		CRM363InputVO inputVO = (CRM363InputVO) body;
		CRM363OutputVO return_VO = new CRM363OutputVO();
		dam = this.getDataAccessManager();

		String CURRUSER = DataManager.getWorkStation(uuid).getUser().getCurrentUserId();
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> headmgrMap = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2);
		//總行 不給放
		if (headmgrMap.containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			throw new APException("使用者登入身分不可操作");
		}
		//禁銷戶
		if (inputVO.getApply_list().get(0).get("COMM_NS_YN") != null) {
			String text1 = "";
			if (StringUtils.equals(inputVO.getApply_list().get(0).get("COMM_NS_YN").toString(), "Y")) {
				for (Map<String, Object> data : inputVO.getApply_list()) {
					text1 += data.get("CUST_NAME").toString() + "(" + data.get("CUST_ID").toString() + ")" + "、";
				}
				throw new APException(text1.substring(0, text1.length() - 1) + "為NS戶，無法申請客戶移入，請洽分行內控品管科。");
			}
		}
		//進行分派				
		Set<String> ao_List = new HashSet<String>();

		for (Map<String, Object> data : inputVO.getApply_list()) {
			// get new role by user select
			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sql = new StringBuffer();
			sql.append("SELECT PRI.PRIVILEGEID FROM VWORG_AO_INFO EMP ");
			sql.append("LEFT JOIN TBORG_MEMBER_ROLE MR ON EMP.EMP_ID = MR.EMP_ID ");
			sql.append("LEFT JOIN TBORG_ROLE RO ON MR.ROLE_ID = RO.ROLE_ID ");
			sql.append("LEFT JOIN TBSYSSECUROLPRIASS PRI ON RO.ROLE_ID = PRI.ROLEID ");
			sql.append("WHERE MR.IS_PRIMARY_ROLE = 'Y' AND RO.REVIEW_STATUS = 'Y' AND RO.IS_AO = 'Y' ");
			sql.append("AND EMP.AO_CODE = :ao_code ");
			queryCondition.setObject("ao_code", data.get("NEW_AO_CODE"));
			queryCondition.setQueryString(sql.toString());
			List<Map<String, Object>> list = dam.exeQuery(queryCondition);
			if (list.size() == 0)
				throw new APException(data.get("NEW_AO_CODE") + ":該理專無角色，請先設定角色");
			String PRIVILEGEID = ObjectUtils.toString(list.get(0).get("PRIVILEGEID"));
			//分行主管
			if ("L1".equals(data.get("PROCESS_STATUS"))) {
				//十保不判斷TRS_FLOW_TYPE
				TBCRM_TRS_AOCHG_PLISTVO vo = new TBCRM_TRS_AOCHG_PLISTVO();
				vo = (TBCRM_TRS_AOCHG_PLISTVO) dam.findByPKey(TBCRM_TRS_AOCHG_PLISTVO.TABLE_UID, new BigDecimal(ObjectUtils.toString(data.get("TRS_SEQ"))));
				vo.setAPL_BRH_MGR(CURRUSER);
				vo.setAPL_BRH_MGR_RPL_DATETIME(new Timestamp(Calendar.getInstance().getTime().getTime()));
				vo.setAPL_BRH_MGR_RPL_STATUS("Y");
				vo.setTEMP_CAL_YN("N");
				vo.setTEMP_CAL_EMP_ID(null);
				vo.setNEW_AO_CODE(ObjectUtils.toString(data.get("NEW_AO_CODE")));
				// 超過最適戶
				if (StringUtils.isNotBlank(inputVO.getFlag()) && inputVO.getFlaggedList().contains(ObjectUtils.toString(data.get("NEW_AO_CODE")))) {
					vo.setPROCESS_STATUS("L2");
					vo.setOVER_CUST_NO_LIMIT_UP_YN("Y");

					// mantis 0004267: 主管覆核客戶移轉 先埋log
					logger.info("CRM363NEW_AO-超過最適戶 L2:" + ObjectUtils.toString(data.get("NEW_AO_CODE")));
					logger.info("CRM363處理流程-超過最適戶 L2:" + vo.getPROCESS_STATUS());
				} else {
					vo.setPROCESS_STATUS("S");
					vo.setACT_DATE(new Timestamp(Calendar.getInstance().getTime().getTime()));
					// jacky
					ao_List.add(ObjectUtils.toString(data.get("ORG_AO_CODE")));
					ao_List.add(ObjectUtils.toString(data.get("NEW_AO_CODE")));
				}
				dam.update(vo);

				if ((StringUtils.isBlank(inputVO.getFlag()) ||  
						(StringUtils.isNotBlank(inputVO.getFlag()) && !inputVO.getFlaggedList().contains(ObjectUtils.toString(data.get("NEW_AO_CODE"))))
						))
					this.ReCustAO(dam, data, PRIVILEGEID, CURRUSER);
			}
			// 原分行主管
			else if ("L4".equals(data.get("PROCESS_STATUS"))) {
				TBCRM_TRS_AOCHG_PLISTVO vo = new TBCRM_TRS_AOCHG_PLISTVO();
				vo = (TBCRM_TRS_AOCHG_PLISTVO) dam.findByPKey(TBCRM_TRS_AOCHG_PLISTVO.TABLE_UID, new BigDecimal(ObjectUtils.toString(data.get("TRS_SEQ"))));
				vo.setORG_BRH_MGR(CURRUSER);
				vo.setORG_BRH_MGR_RPL_DATETIME(new Timestamp(Calendar.getInstance().getTime().getTime()));
				vo.setORG_BRH_MGR_RPL_STATUS("Y");
				vo.setPROCESS_STATUS("L5");
				vo.setTEMP_CAL_YN("N");
				vo.setTEMP_CAL_EMP_ID(null);
				dam.update(vo);
			}
			// 營運區督導
			else if ("L2".equals(data.get("PROCESS_STATUS"))) {
				if (("1".equals(data.get("TRS_FLOW_TYPE")) || "2".equals(data.get("TRS_FLOW_TYPE")))) {
					TBCRM_TRS_AOCHG_PLISTVO vo = new TBCRM_TRS_AOCHG_PLISTVO();
					vo = (TBCRM_TRS_AOCHG_PLISTVO) dam.findByPKey(TBCRM_TRS_AOCHG_PLISTVO.TABLE_UID, new BigDecimal(ObjectUtils.toString(data.get("TRS_SEQ"))));
					vo.setOP_MGR(CURRUSER);
					vo.setOP_MGR_RPL_DATETIME(new Timestamp(Calendar.getInstance().getTime().getTime()));
					vo.setOP_MGR_RPL_STATUS("Y");
					vo.setPROCESS_STATUS("S");
					vo.setTEMP_CAL_YN("N");
					vo.setTEMP_CAL_EMP_ID(null);
					vo.setNEW_AO_CODE(ObjectUtils.toString(data.get("NEW_AO_CODE")));
					vo.setACT_DATE(new Timestamp(Calendar.getInstance().getTime().getTime()));
					dam.update(vo);
					this.ReCustAO(dam, data, PRIVILEGEID, CURRUSER);
				}
				// old code
				else {
					TBCRM_TRS_AOCHG_PLISTVO vo = new TBCRM_TRS_AOCHG_PLISTVO();
					vo = (TBCRM_TRS_AOCHG_PLISTVO) dam.findByPKey(TBCRM_TRS_AOCHG_PLISTVO.TABLE_UID, new BigDecimal(ObjectUtils.toString(data.get("TRS_SEQ"))));
					vo.setOP_MGR(CURRUSER);
					vo.setOP_MGR_RPL_DATETIME(new Timestamp(Calendar.getInstance().getTime().getTime()));
					vo.setOP_MGR_RPL_STATUS("Y");
					vo.setPROCESS_STATUS("L3");
					vo.setTEMP_CAL_YN("N");
					vo.setTEMP_CAL_EMP_ID(null);
					vo.setNEW_AO_CODE(ObjectUtils.toString(data.get("NEW_AO_CODE")));
					dam.update(vo);
				}
				//如果不小心有料到業務處長的. 也可以覆核成功
			} else if ("L3".equals(data.get("PROCESS_STATUS"))) {
				if (("1".equals(data.get("TRS_FLOW_TYPE")) || "2".equals(data.get("TRS_FLOW_TYPE")))) {
					TBCRM_TRS_AOCHG_PLISTVO vo = new TBCRM_TRS_AOCHG_PLISTVO();
					vo = (TBCRM_TRS_AOCHG_PLISTVO) dam.findByPKey(TBCRM_TRS_AOCHG_PLISTVO.TABLE_UID, new BigDecimal(ObjectUtils.toString(data.get("TRS_SEQ"))));
					vo.setOP_MGR(CURRUSER);
					vo.setOP_MGR_RPL_DATETIME(new Timestamp(Calendar.getInstance().getTime().getTime()));
					vo.setOP_MGR_RPL_STATUS("Y");
					vo.setPROCESS_STATUS("S");
					vo.setTEMP_CAL_YN("N");
					vo.setTEMP_CAL_EMP_ID(null);
					vo.setNEW_AO_CODE(ObjectUtils.toString(data.get("NEW_AO_CODE")));
					vo.setACT_DATE(new Timestamp(Calendar.getInstance().getTime().getTime()));
					dam.update(vo);
					
					this.ReCustAO(dam, data, PRIVILEGEID, CURRUSER);
				}
			}
		}
		
		this.sendRtnObject(return_VO);
	}
	
	private void ReCustAO(DataAccessManager dam, Map<String, Object> data, String PRIVILEGEID, String CURRUSER) throws Exception {
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		// Step1.更新客戶主檔，並回傳名單檔案更新主機資料
		TBCRM_CUST_MASTVO vo_mast = new TBCRM_CUST_MASTVO();
		vo_mast = (TBCRM_CUST_MASTVO) dam.findByPKey(TBCRM_CUST_MASTVO.TABLE_UID, ObjectUtils.toString(data.get("CUST_ID")));
		vo_mast.setAO_CODE(ObjectUtils.toString(data.get("NEW_AO_CODE")));
		vo_mast.setBRA_NBR(ObjectUtils.toString(data.get("NEW_AO_BRH")));
		vo_mast.setAO_LASTUPDATE(new Timestamp(Calendar.getInstance().getTime().getTime()));
		dam.update(vo_mast);

		// Step2.新增一筆到移轉紀錄TBCRM_CUST_AOCHG_LOG
		TBCRM_CUST_AOCODE_CHGLOGVO vo_log = new TBCRM_CUST_AOCODE_CHGLOGVO();
		vo_log.setSEQ(getSEQ());
		vo_log.setCUST_ID(ObjectUtils.toString(data.get("CUST_ID")));
		vo_log.setORG_AO_CODE(ObjectUtils.toString(data.get("ORG_AO_CODE")));
		vo_log.setORG_AO_BRH(ObjectUtils.toString(data.get("ORG_AO_BRH")));
		vo_log.setORG_AO_NAME(ObjectUtils.toString(data.get("ORG_AO_NAME")));
		vo_log.setNEW_AO_CODE(ObjectUtils.toString(data.get("NEW_AO_CODE")));
		vo_log.setNEW_AO_BRH(ObjectUtils.toString(data.get("NEW_AO_BRH")));
		vo_log.setNEW_AO_NAME(ObjectUtils.toString(data.get("NEW_AO_NAME")));
		vo_log.setREG_AOCODE_EMP_ID(ObjectUtils.toString(data.get("APL_EMP_ID")));
		vo_log.setREG_AOCODE_SUB_DATETIME(new Timestamp(sdf1.parse(ObjectUtils.toString(data.get("APL_DATETIME"))).getTime()));
		vo_log.setLETGO_EMP_ID(CURRUSER);
		vo_log.setLETGO_DATETIME(new Timestamp(Calendar.getInstance().getTime().getTime()));
		vo_log.setAPL_REASON(ObjectUtils.toString(data.get("APL_REASON")));
		if (data.get("APL_OTH_REASON") != null)
			vo_log.setAPL_OTH_REASON(ObjectUtils.toString(data.get("APL_OTH_REASON")));
		vo_log.setTRS_TXN_SOURCE(ObjectUtils.toString(data.get("TRS_TXN_SOURCE")));
		vo_log.setRETURN_400_YN("N");
		dam.create(vo_log);
	}
	
	/** 產生seq No */
	private String getSEQ() throws JBranchException {
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT TBCRM_CUST_AOCODE_CHGLOG_SEQ.nextval AS SEQ FROM DUAL ");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> SEQLIST = dam.exeQuery(queryCondition);

		return ObjectUtils.toString(SEQLIST.get(0).get("SEQ"));
	}
	
	// 匯出
	public void export(Object body, IPrimitiveMap header) throws Exception {

		CRM363InputVO inputVO = (CRM363InputVO) body;

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		String fileName = "";
		if(StringUtils.equals("1", inputVO.getPRJ_TYPE())) { //必輪調
			fileName = "輪調未帶走客戶主管重新分派_" + sdf.format(new Date()) + ".csv";
		} else {
			fileName = "帳務未確認主管重新分派_" + sdf.format(new Date()) + ".csv";
		}
		
		//匯出欄位
		String[] csvHeader = getCsvHeader(inputVO.getPRJ_TYPE());
		String[] csvMain = getCsvMain(inputVO.getPRJ_TYPE());
		List<Object[]> csvData = new ArrayList<Object[]>();

		for (Map<String, Object> map : inputVO.isPrintAllData() ? getExportAllData(inputVO.getPRJ_ID()) : inputVO.getPrintList()) {
			String[] records = new String[csvHeader.length];			
			for (int i = 0; i < csvHeader.length; i++) {
				records[i] = "\"" + checkIsNull(map, csvMain[i]) + "\"";
			}
			csvData.add(records);
		}

		CSVUtil csv = new CSVUtil();
		csv.setHeader(csvHeader);
		csv.addRecordList(csvData);

		String url = csv.generateCSV();

		notifyClientToDownloadFile(url, fileName);
	}

	private String checkIsNull(Map map, String key) {

		if (StringUtils.isNotBlank(ObjectUtils.toString(map.get(key)))) {
			return String.valueOf(map.get(key));
		} else {
			return "";
		}
	}

	private List<Map<String, Object>> getExportAllData(String prjId) throws JBranchException, ParseException {

		DataAccessManager dam = this.getDataAccessManager();
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		sql.append("SELECT DISTINCT P.CUST_ID, N.COMPLAIN_YN, C.CUST_NAME, P.TRS_TYPE, P.TEMP_CAL_YN, C.CON_DEGREE, P.SEQ AS TRS_SEQ, P.ACT_DATE, ");
		sql.append("       C.VIP_DEGREE, C.AUM_AMT, P.ORG_AO_CODE, P.NEW_AO_CODE, P.APL_REASON, P.APL_OTH_REASON, P.ORG_AO_BRH, ");
		sql.append("       P.OVER_CUST_NO_LIMIT_UP_YN, P.NEW_AO_BRH, P.TRS_FLOW_TYPE, P.PROCESS_STATUS, P.APL_DATETIME, ");
		sql.append("       EMP.EMP_NAME AS ORG_AO_NAME, DEFN1.BRANCH_NAME AS ORG_BRANCH_NAME, EMP.EMP_ID AS ORG_AO_EMP_ID, ");
		sql.append("	   EMP2.EMP_NAME AS NEW_AO_NAME, DEFN2.BRANCH_NAME AS NEW_BRANCH_NAME, EMP2.EMP_ID AS NEW_AO_EMP_ID, ");
		sql.append("	   Y.EMP_ID AS RM_AO_EMP_ID, Y.BRANCH_NBR AS RM_AO_BRANCH_NBR, ");
		sql.append("	   RMEMP.EMP_NAME AS RM_EMP_NAME, RMDEFN.BRANCH_NAME AS RM_AO_BRANCH_NAME, ");
		sql.append("	   CHGLOG.ORG_AO_CODE AS CHGLOG_AO_CODE, CHGLOG.ORG_AO_NAME AS CHGLOG_EMP_NAME, ");
		sql.append("	   (CASE CHEMP.TYPE WHEN '1' THEN '(主)' WHEN '2' THEN '(副)' WHEN '3' THEN '(維護)' ELSE '' END) AS CHGLOG_AO_TYPE ");
		sql.append(" FROM TBCRM_CUST_MAST C, TBCRM_CUST_NOTE N, TBCRM_TRS_AOCHG_PLIST P ");
		sql.append(" LEFT JOIN TBPMS_ROTATION_MAIN RM ON RM.PRJ_ID = P.PRJ_ID AND RM.CUST_ID = P.CUST_ID "); //換手
		sql.append(" LEFT JOIN TBPMS_ROTATION_5YCUST Y ON Y.PRJ_ID = RM.PRJ_ID AND Y.CUST_ID = RM.CUST_ID ");
//		sql.append(" LEFT JOIN TBCRM_TRS_PRJ_ROTATION_D RMD ON RMD.PRJ_ID = P.PRJ_ID AND RMD.CUST_ID = P.CUST_ID "); //必輪調
		sql.append(" LEFT JOIN VWORG_DEFN_INFO DEFN1 ON DEFN1.BRANCH_NBR = P.ORG_AO_BRH ");
		sql.append(" LEFT JOIN VWORG_DEFN_INFO DEFN2 ON DEFN2.BRANCH_NBR = P.NEW_AO_BRH ");
		sql.append(" LEFT JOIN VWORG_DEFN_INFO RMDEFN ON RMDEFN.BRANCH_NBR = Y.BRANCH_NBR ");
		sql.append(" LEFT JOIN VWORG_AO_INFO EMP ON  P.ORG_AO_CODE = EMP.AO_CODE ");
		sql.append(" LEFT JOIN VWORG_AO_INFO EMP2 ON P.NEW_AO_CODE = EMP2.AO_CODE ");
		sql.append(" LEFT JOIN TBORG_MEMBER RMEMP ON RMEMP.EMP_ID = Y.EMP_ID ");
		sql.append(" LEFT JOIN TBCRM_CUST_AOCODE_CHGLOG CHGLOG ON CHGLOG.CUST_ID = P.CUST_ID AND CHGLOG.PRJ_ID = P.PRJ_ID ");
		sql.append(" LEFT JOIN VWORG_AO_INFO CHEMP ON CHGLOG.ORG_AO_CODE = CHEMP.AO_CODE ");
		sql.append(" WHERE C.CUST_ID = P.CUST_ID AND C.CUST_ID = N.CUST_ID ");
		sql.append(" AND P.PRJ_ID = :prjId "); //拔CODE，主管重新分派
		sql.append(" ORDER BY P.NEW_AO_BRH, P.CUST_ID ");
		
		condition.setObject("prjId", prjId);
		condition.setQueryString(sql.toString());
		List<Map<String, Object>> list = dam.exeQuery(condition);

		return list;		
	}

	private String[] getCsvHeader(String prjType) {
		if(StringUtils.equals("1", prjType)) { //必輪調
			String[] str = { "客戶ID", "客戶姓名", "現行分行", "現行分行名稱", "現行理專AO", "現行理專", "新分行", "新分行名稱", "新理專AO", "新理專", "覆核同意日期"};
			return str;
		} else { //換手
			String[] str = { "客戶ID", "客戶姓名", "原經營滿5年理專分行", "原經營滿5年理專分行名稱", "原經營滿5年理專員編", "原經營滿5年理專姓名", "現行分行", "現行分行名稱", "現行理專AO", "現行理專", "新分行", "新分行名稱", "新理專AO", "新理專", "覆核同意日期"};
			return str;
		}
	}

	private String[] getCsvMain(String prjType) {
		if(StringUtils.equals("1", prjType)) { //必輪調
			String[] str = {"CUST_ID", "CUST_NAME", "NEW_AO_BRH", "NEW_BRANCH_NAME", "CHGLOG_AO_CODE", "CHGLOG_EMP_NAME", "NEW_AO_BRH", "NEW_BRANCH_NAME", "NEW_AO_CODE", "NEW_AO_NAME", "ACT_DATE"};
			return str;
		} else { //換手
			String[] str = {"CUST_ID", "CUST_NAME", "RM_AO_BRANCH_NBR", "RM_AO_BRANCH_NAME", "RM_AO_EMP_ID", "RM_EMP_NAME", "NEW_AO_BRH", "NEW_BRANCH_NAME", "CHGLOG_AO_CODE", "CHGLOG_EMP_NAME", "NEW_AO_BRH", "NEW_BRANCH_NAME", "NEW_AO_CODE", "NEW_AO_NAME", "ACT_DATE"};
			return str;
		}
		
	}
	
}