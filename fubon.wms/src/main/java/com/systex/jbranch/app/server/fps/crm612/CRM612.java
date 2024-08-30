package com.systex.jbranch.app.server.fps.crm612;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBCRM_CUST_AO_DEF_GROUPVO;
import com.systex.jbranch.app.common.fps.table.TBCRM_CUST_GROUPPK;
import com.systex.jbranch.app.common.fps.table.TBCRM_CUST_GROUPVO;
import com.systex.jbranch.app.common.fps.table.TBCRM_CUST_NOTEVO;
import com.systex.jbranch.app.common.fps.table.TBCRM_CUST_NOTE_OTH_ASTVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.dataaccess.serialnumber.SerialNumberUtil;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * @author walalala
 * @date 2016/06/02
 * 
 */
@Component("crm612")
@Scope("request")
public class CRM612 extends FubonWmsBizLogic {
	
	private DataAccessManager dam = null;

	//查詢當日是否有承作最新一筆KYC問卷[Q8買過商品]未勾選項(6)衍生性金融商品(包括結構型商品等)。
	public void checkKYC(Object body, IPrimitiveMap header) throws JBranchException {
		
		CRM612InputVO inputVO = (CRM612InputVO) body;
		CRM612OutputVO return_VO = new CRM612OutputVO();

		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();

		sql.append("SELECT B.ANSWER_2 FROM TBKYC_INVESTOREXAM_M A ");
		sql.append("JOIN TBKYC_INVESTOREXAM_D B ON A.SEQ = B.SEQ ");
		sql.append("WHERE A.CUST_ID = :cust_id ");
		sql.append("AND A.STATUS = '03' AND trunc(A.SIGNOFF_DATE) = trunc(sysdate) ");

		queryCondition.setQueryString(sql.toString());
		queryCondition.setObject("cust_id", inputVO.getCust_id());

		List<Map<String, Object>> answerList = dam.exeQuery(queryCondition);

		if (answerList.size() > 0) {
			String answers = answerList.get(0).get("ANSWER_2").toString();
			String[] answerArray = answers.split(";");
			String answer = answerArray[7];

			answerList.get(0).put("ANSWER_2", answer);
		}
		
		return_VO.setResultList(answerList);
		
		this.sendRtnObject(return_VO);
	}

	//查詢KYC
	public void kyc_inquire(Object body, IPrimitiveMap header) throws JBranchException {
		
		CRM612InputVO inputVO = (CRM612InputVO) body;
		CRM612OutputVO return_VO = new CRM612OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		/** 2016.12.28 add 風險承受度中文 Stella */
		sql.append("SELECT DISTINCT ");
		sql.append("       H.CREATE_DATE, ");
		sql.append("       H.SEQ, ");
		sql.append("       H.CREATOR, ");
		sql.append("       M.EMP_NAME, ");
		sql.append("       H.REC_TYPE, ");
		sql.append("       R.RL_NAME, ");
		sql.append("       H.CUST_RISK_AFR, ");
		sql.append("       H.RISKRANGE, ");
		sql.append("       H.SIGNOFF_DATE, ");
		sql.append("       H.INVEST_BRANCH_NBR, ");
		sql.append("       (SELECT BRANCH_NAME FROM VWORG_DEFN_INFO WHERE BRANCH_NBR = H.INVEST_BRANCH_NBR)AS INVEST_BRANCH_NAME ");
		sql.append("FROM TBKYC_INVESTOREXAM_M_HIS H ");
		sql.append("LEFT OUTER JOIN TBORG_MEMBER M ON H.CREATOR = M.EMP_ID ");
		sql.append("LEFT OUTER JOIN TBSYS_QUESTIONNAIRE Q ON Q.EXAM_VERSION = H.EXAM_VERSION ");
		sql.append("LEFT OUTER JOIN TBKYC_QUESTIONNAIRE_RISK_LEVEL R ON R.RL_VERSION = Q.RL_VERSION AND R.CUST_RL_ID = H.CUST_RISK_AFR  ");
		sql.append("WHERE H.CUST_ID = :cust_id ");
		sql.append("AND H.STATUS = '03' ");//有效
		sql.append("ORDER BY H.CREATE_DATE DESC, H.SEQ DESC ");//依照鑑機日期、SEQ由大到小排序

		queryCondition.setQueryString(sql.toString());
		queryCondition.setObject("cust_id", inputVO.getCust_id());

		ResultIF list = dam.executePaging(queryCondition, inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
		
		return_VO.setResultList_kyc(list);
		return_VO.setCurrentPageIndex(inputVO.getCurrentPageIndex());// 當前頁次
		return_VO.setTotalPage(list.getTotalPage());// 總頁次
		return_VO.setTotalRecord(list.getTotalRecord());// 總筆數
		
		this.sendRtnObject(return_VO);
	}

	//查詢ASSETS
	public void assets_inquire(Object body, IPrimitiveMap header) throws JBranchException {
		
		CRM612InputVO inputVO = (CRM612InputVO) body;
		CRM612OutputVO return_VO = new CRM612OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		sql.append("SELECT * ");
		sql.append("FROM TBCRM_CUST_NOTE_OTH_AST ");
		sql.append("where 1 = 1 ");
		sql.append("and CUST_ID = :cust_id ");
		sql.append("ORDER BY LASTUPDATE DESC ");
		
		queryCondition.setQueryString(sql.toString());
		
		queryCondition.setObject("cust_id", inputVO.getCust_id());

		ResultIF list = dam.executePaging(queryCondition, inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
		
		return_VO.setResultList_assets(list);
		return_VO.setCurrentPageIndex(inputVO.getCurrentPageIndex());// 當前頁次
		return_VO.setTotalPage(list.getTotalPage());// 總頁次
		return_VO.setTotalRecord(list.getTotalRecord());// 總筆數
		
		this.sendRtnObject(return_VO);
	}

	//新增ASSETS
	public void assets_add(Object body, IPrimitiveMap header) throws JBranchException {
		
		CRM612InputVO inputVO = (CRM612InputVO) body;
		dam = this.getDataAccessManager();
		
		TBCRM_CUST_NOTE_OTH_ASTVO vo = new TBCRM_CUST_NOTE_OTH_ASTVO();
		vo.setASSETS_ID(getAssetsSN());
		vo.setCUST_ID(inputVO.getCust_id());
		vo.setASSETS_NAME(inputVO.getAssets_name());
		vo.setASSETS_AMT(new BigDecimal(inputVO.getAssets_amt()));
		vo.setASSETS_NOTE(inputVO.getAssets_note());

		dam.create(vo);

		this.sendRtnObject(null);
	}

	//修改ASSETS
	public void assets_editconfirm(Object body, IPrimitiveMap header) throws JBranchException {
		
		CRM612InputVO inputVO = (CRM612InputVO) body;
		dam = this.getDataAccessManager();
		
		TBCRM_CUST_NOTE_OTH_ASTVO vo = (TBCRM_CUST_NOTE_OTH_ASTVO) dam.findByPKey(TBCRM_CUST_NOTE_OTH_ASTVO.TABLE_UID, inputVO.getAssets_id());

		if (vo != null) {
			vo.setCUST_ID(inputVO.getCust_id());
			vo.setASSETS_NAME(inputVO.getAssets_name());
			vo.setASSETS_AMT(new BigDecimal(inputVO.getAssets_amt()));
			vo.setASSETS_NOTE(inputVO.getAssets_note());
			
			dam.update(vo);
		} else {
			throw new APException("ehl_01_common_008");
		}
		
		this.sendRtnObject(null);
	}

	//刪除ASSETS
	public void assets_delete(Object body, IPrimitiveMap header) throws JBranchException {
		
		CRM612InputVO inputVO = (CRM612InputVO) body;
		dam = this.getDataAccessManager();

		TBCRM_CUST_NOTE_OTH_ASTVO vo = new TBCRM_CUST_NOTE_OTH_ASTVO();
		vo = (TBCRM_CUST_NOTE_OTH_ASTVO) dam.findByPKey(TBCRM_CUST_NOTE_OTH_ASTVO.TABLE_UID, inputVO.getAssets_id());

		if (vo != null) {
			dam.delete(vo);
		} else {
			// 顯示資料不存在
			throw new APException("ehl_01_common_005");
		}
		
		this.sendRtnObject(null);
	}

	//查詢GROUP
	public void group_inquire(Object body, IPrimitiveMap header) throws JBranchException {
		
		CRM612InputVO inputVO = (CRM612InputVO) body;
		CRM612OutputVO return_VO = new CRM612OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		sql.append("SELECT * ");
		sql.append("FROM TBCRM_CUST_GROUP ");
		sql.append("where 1 = 1 ");
		sql.append("and CUST_ID = :cust_id ");
		
		queryCondition.setQueryString(sql.toString());
		
		queryCondition.setObject("cust_id", inputVO.getCust_id());

		ResultIF list = dam.executePaging(queryCondition, inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());

		return_VO.setResultList_group(list);
		return_VO.setCurrentPageIndex(inputVO.getCurrentPageIndex());// 當前頁次
		return_VO.setTotalPage(list.getTotalPage());// 總頁次
		return_VO.setTotalRecord(list.getTotalRecord());// 總筆數
		
		this.sendRtnObject(return_VO);
	}

	//取得群組清單
	public void getGroups(Object body, IPrimitiveMap header) throws JBranchException {
		
		CRM612OutputVO return_VO = new CRM612OutputVO();
		dam = this.getDataAccessManager();

		// 2018/4/12 same with crm230?
		String privilegeID = "";
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		queryCondition.setQueryString("SELECT PRIVILEGEID FROM TBSYSSECUROLPRIASS WHERE ROLEID = :loginRoleID");
		queryCondition.setObject("loginRoleID", getUserVariable(FubonSystemVariableConsts.LOGINROLE));
		
		List<Map<String, Object>> tempList = dam.exeQueryWithoutSort(queryCondition);
		
		if (tempList.size() > 0)
			privilegeID = ObjectUtils.toString(tempList.get(0).get("PRIVILEGEID"));

		queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT GROUP_ID, GROUP_NAME ");
		sql.append("FROM TBCRM_CUST_AO_DEF_GROUP ");
		
		if (StringUtils.equals(privilegeID, "002") || StringUtils.equals(privilegeID, "003")) {
			sql.append("WHERE AO_CODE IN (:ao_list) ");
			queryCondition.setObject("ao_list", getUserVariable(FubonSystemVariableConsts.LOGIN_AOCODE_LIST));
		} else {
			sql.append("WHERE AO_CODE IN (SELECT AO_CODE FROM VWORG_AO_INFO WHERE BRA_NBR IN ( :branchlist )) ");
			queryCondition.setObject("branchlist", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
		}
		
		sql.append("order by GROUP_ID ");
		
		queryCondition.setQueryString(sql.toString());
		
		return_VO.setResultList(dam.exeQuery(queryCondition));
		
		this.sendRtnObject(return_VO);
	}

	//加入GROUP
	public void group_join(Object body, IPrimitiveMap header) throws JBranchException {
		
		CRM612InputVO inputVO = (CRM612InputVO) body;
		dam = this.getDataAccessManager();
		
		TBCRM_CUST_GROUPPK pk = new TBCRM_CUST_GROUPPK();
		pk.setCUST_ID(inputVO.getCust_id());
		pk.setGROUP_ID(inputVO.getGroup_id());
		
		TBCRM_CUST_GROUPVO vo = (TBCRM_CUST_GROUPVO) dam.findByPKey(TBCRM_CUST_GROUPVO.TABLE_UID, pk);
		
		if (vo == null) {
			vo = new TBCRM_CUST_GROUPVO();
			vo.setGROUP_NAME(inputVO.getGroup_name());
			vo.setcomp_id(pk);
			
			dam.create(vo);
		} else {
			throw new APException("ehl_02_CRM230_001");
		}
		
		this.sendRtnObject(null);
	}

	//新增GROUP
	public void group_addConfirm(Object body, IPrimitiveMap header) throws JBranchException {
		
		CRM612InputVO inputVO = (CRM612InputVO) body;
		dam = this.getDataAccessManager();

		TBCRM_CUST_AO_DEF_GROUPVO vo = new TBCRM_CUST_AO_DEF_GROUPVO();
		vo.setGROUP_ID(getGroupSN());
		vo.setGROUP_NAME(inputVO.getAddgroup());
		vo.setAO_CODE(inputVO.getAo_code());
		
		dam.create(vo);

		this.sendRtnObject(null);
	}

	//刪除GROUP
	public void group_delete(Object body, IPrimitiveMap header) throws JBranchException {
		
		CRM612InputVO inputVO = (CRM612InputVO) body;
		dam = this.getDataAccessManager();

		TBCRM_CUST_GROUPPK pk = new TBCRM_CUST_GROUPPK();
		pk.setCUST_ID(inputVO.getCust_id());
		pk.setGROUP_ID(inputVO.getGroup_id());
		
		TBCRM_CUST_GROUPVO vo = (TBCRM_CUST_GROUPVO) dam.findByPKey(TBCRM_CUST_GROUPVO.TABLE_UID, pk);
		if (vo != null) {
			dam.delete(vo);
		} else {
			// 顯示資料不存在
			throw new APException("ehl_01_common_005");
		}
		
		this.sendRtnObject(null);
	}

	//查詢CUST
	public void cust_inquire(Object body, IPrimitiveMap header) throws JBranchException {
		
		CRM612InputVO inputVO = (CRM612InputVO) body;
		CRM612OutputVO return_VO = new CRM612OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		sql.append("SELECT CUST_ID, DECIS, AO_BEST_VISIT_DATETIME ");
		sql.append("FROM TBCRM_CUST_NOTE ");
		sql.append("where 1 = 1 ");
		sql.append("and CUST_ID = :cust_id ");
		
		queryCondition.setQueryString(sql.toString());
		
		queryCondition.setObject("cust_id", inputVO.getCust_id());

		return_VO.setResultList_cust(dam.exeQuery(queryCondition));
		
		sendRtnObject(return_VO);
	}

	//新增OR修改CUST
	public void cust_edit(Object body, IPrimitiveMap header) throws JBranchException {
		
		CRM612InputVO inputVO = (CRM612InputVO) body;
		dam = this.getDataAccessManager();
		
		TBCRM_CUST_NOTEVO vo = (TBCRM_CUST_NOTEVO) dam.findByPKey(TBCRM_CUST_NOTEVO.TABLE_UID, inputVO.getCust_id());

		if (vo == null) {
			vo = new TBCRM_CUST_NOTEVO();
			vo.setCUST_ID(inputVO.getCust_id());
			vo.setDECIS(inputVO.getDecis());
			vo.setCOM_EXPERIENCE(inputVO.getCom_experience());
			
			if (inputVO.getAo_best_visit_datetime() != null)
				vo.setAO_BEST_VISIT_DATETIME(new Timestamp(inputVO.getAo_best_visit_datetime().getTime()));
			
			dam.create(vo);
		} else {
			vo.setDECIS(inputVO.getDecis());
			vo.setCOM_EXPERIENCE(inputVO.getCom_experience());
			
			if (inputVO.getAo_best_visit_datetime() != null)
				vo.setAO_BEST_VISIT_DATETIME(new Timestamp(inputVO.getAo_best_visit_datetime().getTime()));
			
			dam.update(vo);
		}
		
		this.sendRtnObject(null);
	}

	private String getGroupSN() throws JBranchException {
		
		SerialNumberUtil sn = new SerialNumberUtil();
		String seqNum = "";
		
		try {
			seqNum = sn.getNextSerialNumber("CRM612_group");
		} catch (Exception e) {
			sn.createNewSerial("CRM612_group", "00000", null, null, null, 1, new Long("99999"), "y", new Long("0"), null);
			seqNum = sn.getNextSerialNumber("CRM612_group");
		}
		
		return seqNum;
	}

	private String getAssetsSN() throws JBranchException {
		
		SerialNumberUtil sn = new SerialNumberUtil();
		String seqNum = "";
		
		try {
			seqNum = sn.getNextSerialNumber("CRM612_assets");
		} catch (Exception e) {
			sn.createNewSerial("CRM612_assets", "00000", null, null, null, 1, new Long("99999"), "y", new Long("0"), null);
			seqNum = sn.getNextSerialNumber("CRM612_assets");
		}
		
		return seqNum;
	}

	//查詢VOC
	public void voc_inquire(Object body, IPrimitiveMap header) throws JBranchException {
		
		CRM612InputVO inputVO = (CRM612InputVO) body;
		CRM612OutputVO outputVO = new CRM612OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		
		sql.append("SELECT V.BRANCH_NBR, ");
		sql.append("       DEFN.BRANCH_NAME, ");
		sql.append("       V.SKBOX, ");
		sql.append("       TO_CHAR(V.SKRDAY, 'YYYY/MM/DD') AS SKRDAY, ");
		sql.append("       TO_CHAR(V.SKCDAY, 'YYYY/MM/DD') AS SKCDAY, ");
		sql.append("       TO_CHAR(V.SKEDAY, 'YYYY/MM/DD') AS SKEDAY, ");
		sql.append("       V.SKRAMT, ");
		sql.append("       V.SKGAMT ");
		sql.append("FROM TBCRM_CUST_VOC V ");
		sql.append("LEFT JOIN VWORG_DEFN_INFO DEFN ON V.BRANCH_NBR = DEFN.BRANCH_NBR ");
		sql.append("WHERE 1 = 1 ");
		sql.append("AND V.CUST_ID = :cust_id ");
		sql.append("ORDER BY V.BRANCH_NBR ");
		
		queryCondition.setQueryString(sql.toString());
		
		queryCondition.setObject("cust_id", inputVO.getCust_id());

		outputVO.setResultList_voc(dam.exeQuery(queryCondition));
		
		this.sendRtnObject(outputVO);
	}
	
}