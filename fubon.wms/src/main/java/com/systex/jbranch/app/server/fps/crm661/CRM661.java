package com.systex.jbranch.app.server.fps.crm661;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBCRM_CUST_RELVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.serialnumber.SeriaNextSequence;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.errHandle.NotFoundException;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * @author walalala
 * @date 2016/08/16
 * 
 */
@Component("crm661")
@Scope("request")
public class CRM661 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(CRM661.class);

	@Autowired
	@Qualifier("SeriaNextSequence")
	private SeriaNextSequence seriaNextSequence;

	//初始查詢
	public void initial_home(Object body, IPrimitiveMap header) throws JBranchException {
		CRM661InputVO inputVO = (CRM661InputVO) body;
		CRM661OutputVO return_VO = new CRM661OutputVO();
		dam = this.getDataAccessManager();

		//VIP_DEGREE判斷查詢
		QueryConditionIF queryCondition2 = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql2 = new StringBuffer();

		sql2.append("SELECT VIP_DEGREE,FAMILY_DEGREE FROM TBCRM_CUST_MAST ");
		sql2.append("WHERE 1=1 ");
		sql2.append("AND CUST_ID = :cust_id ");

		queryCondition2.setQueryString(sql2.toString());
		queryCondition2.setObject("cust_id", inputVO.getCust_id());
		List list2 = dam.exeQuery(queryCondition2);

		return_VO.setResultList2(list2);
		sendRtnObject(return_VO);
	}

	//初始查詢
	public void initial(Object body, IPrimitiveMap header) throws JBranchException {
		CRM661InputVO inputVO = (CRM661InputVO) body;
		CRM661OutputVO return_VO = new CRM661OutputVO();
		dam = this.getDataAccessManager();

		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT A.CUST_NAME, A.CUST_ID, B.BRANCH_NAME, A.VIP_DEGREE, A.AO_CODE, B.EMP_NAME, B.EMP_ID ");
		sql.append("FROM TBCRM_CUST_MAST A, VWORG_AO_INFO B ");
		sql.append("WHERE 1 =1 AND A.AO_CODE = B.AO_CODE AND A.CUST_ID = :cust_id");
		queryCondition.setQueryString(sql.toString());
		queryCondition.setObject("cust_id", inputVO.getCust_id());

		List list = dam.exeQuery(queryCondition);
		return_VO.setResultList(list);
		sendRtnObject(return_VO);
	}

	//關係戶查詢
	public void inquire(Object body, IPrimitiveMap header) throws JBranchException {
		
		CRM661InputVO inputVO = (CRM661InputVO) body;
		CRM661OutputVO return_VO = new CRM661OutputVO();

		String cust_id = inputVO.getCust_id();
		if (StringUtils.isNotBlank(cust_id)) {
			dam = this.getDataAccessManager();

			XmlInfo xmlInfo = new XmlInfo();
			boolean isFC = xmlInfo.doGetVariable("FUBONSYS.FC_ROLE", FormatHelper.FORMAT_2).containsKey(FubonSystemVariableConsts.LOGINROLE);
			boolean isFCH = xmlInfo.doGetVariable("FUBONSYS.ARMGR_ROLE", FormatHelper.FORMAT_2).containsKey(FubonSystemVariableConsts.LOGINROLE);

			//找出關係戶相關資訊
			QueryConditionIF queryCondition2 = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sql2 = new StringBuffer();
			//找以客戶為主戶的從關係人
			sql2.append("SELECT 'S' AS REL, ");
			sql2.append("       C2.CUST_NAME, ");
			sql2.append("       C2.CUST_ID, ");
			sql2.append("       C2.AO_CODE, ");
			sql2.append("       P1.CUST_NAME AS PRV_MBR_MAST_YN,");
			sql2.append("       R.REL_TYPE, ");
			sql2.append("       R.CUST_ID_S, ");
			sql2.append("       R.REL_TYPE_OTH, ");
			sql2.append("       R.JOIN_SRV_CUST_ID, ");
			sql2.append("       R.SEQ, ");
			sql2.append("       R.REL_STATUS, ");
			sql2.append("       E.BRANCH_NAME, ");
			sql2.append("       E.EMP_NAME, ");
			sql2.append("       E.EMP_ID, ");
			sql2.append("       NVL(C2.VIP_DEGREE, 'M') AS VIP_DEGREE, ");
			sql2.append("       C2.CON_DEGREE ");
			
			if (((getUserVariable(FubonSystemVariableConsts.LOGIN_AOCODE_LIST)).toString().replace("[", "").replace("]", "")).length() > 0 && 
				(isFC || isFCH)) {
				sql2.append(", (CASE WHEN C2.AO_CODE in (:curr_ao_code) THEN 'Y' ELSE 'N' END) AS IN_COVERAGE ");
			} else {
				sql2.append(", (CASE WHEN C2.BRA_NBR IN (:brNbrList) THEN 'Y' ELSE 'N' END) AS IN_COVERAGE ");
			}
			
			// add by ocean 6705
			sql2.append(" , UI.EMP_ID AS UEMP_ID, UI.EMP_NAME || CASE WHEN UI.CODE_TYPE = '1' THEN '(計績)' WHEN UI.CODE_TYPE = '3' THEN '(維護)' ELSE '' END AS UEMP_NAME ");
							
			// add by ocean 6001:WMS-CR-20181113-01_個人高端客群處「業管系統_第一階段需求調整申請」_P2
			sql2.append(" , R.CREATOR, CASE WHEN R.REL_STATUS IN ('RAN', 'RAD', 'PAN', 'PAD') AND ((CREATE_MEM.EMP_STATUS = 'Y' AND R.CREATOR = :loginID) OR (CREATE_MEM.EMP_STATUS = 'N')) THEN 'Y' WHEN R.REL_STATUS NOT IN ('RAN', 'RAD', 'PAN', 'PAD') THEN 'Y' ELSE 'N' END AS SHOW_CODI ");

			sql2.append("FROM TBCRM_CUST_REL R ");
			sql2.append("LEFT JOIN TBCRM_CUST_MAST C2 ON R.CUST_ID_S = C2.CUST_ID ");
			sql2.append("LEFT JOIN VWORG_AO_INFO E ON C2.AO_CODE = E.AO_CODE ");
			sql2.append("LEFT JOIN TBCRM_CUST_PRV P ON R.CUST_ID_S = P.CUST_ID_S ");
			sql2.append("LEFT JOIN TBCRM_CUST_MAST P1 ON P.CUST_ID_M = P1.CUST_ID ");

			// add by ocean 6001:WMS-CR-20181113-01_個人高端客群處「業管系統_第一階段需求調整申請」_P2
			sql2.append("LEFT JOIN (SELECT EMP_ID, CASE WHEN SERVICE_FLAG = 'A' AND CHANGE_FLAG IN ('A', 'M', 'P') THEN 'Y' ELSE 'N' END AS EMP_STATUS FROM TBORG_MEMBER) CREATE_MEM ON R.CREATOR = CREATE_MEM.EMP_ID ");

			// add by ocean 6705
			sql2.append("LEFT JOIN VWORG_EMP_UHRM_INFO UI ON C2.AO_CODE = UI.UHRM_CODE ");
						
			sql2.append("WHERE 1 = 1 ");
			sql2.append("AND (R.REL_MBR_YN = 'Y' OR (R.REL_MBR_YN = 'N' AND R.REL_STATUS = 'RAN')) ");
			sql2.append("AND R.CUST_ID_M = :cust_id ");
			sql2.append("AND R.CUST_ID_M != R.CUST_ID_S ");//當自己是別人的從戶(有一筆關係戶資料)又是主戶(有一筆自建資料)時，從戶區塊不顯示
			
			sql2.append("UNION ");
			
			//找以客戶為從戶的主關係人
			sql2.append("SELECT 'M' AS REL, ");
			sql2.append("       C1.CUST_NAME, ");
			sql2.append("       C1.CUST_ID, ");
			sql2.append("       C1.AO_CODE,");
			sql2.append("       P1.CUST_NAME AS PRV_MBR_MAST_YN, ");
			sql2.append("       R.REL_TYPE, ");
			sql2.append("       R.CUST_ID_S, ");
			sql2.append("       R.REL_TYPE_OTH, ");
			sql2.append("       R.JOIN_SRV_CUST_ID, ");
			sql2.append("       R.SEQ, ");
			sql2.append("       R.REL_STATUS, ");
			sql2.append("       E.BRANCH_NAME, ");
			sql2.append("       E.EMP_NAME, ");
			sql2.append("       E.EMP_ID, ");
			sql2.append("       NVL(C1.VIP_DEGREE,'M') AS VIP_DEGREE, ");
			sql2.append("       C1.CON_DEGREE ");
			
			if (((getUserVariable(FubonSystemVariableConsts.LOGIN_AOCODE_LIST)).toString().replace("[", "").replace("]", "")).length() > 0 && 
				(isFC || isFCH)) {
				sql2.append(", (CASE WHEN C1.AO_CODE in (:curr_ao_code) THEN 'Y' ELSE 'N' END) AS IN_COVERAGE ");
			} else {
				sql2.append(", (CASE WHEN C1.BRA_NBR IN (:brNbrList) THEN 'Y' ELSE 'N' END) AS IN_COVERAGE ");
			}

			// add by ocean 6705
			sql2.append(" , UI.EMP_ID AS UEMP_ID, UI.EMP_NAME || CASE WHEN UI.CODE_TYPE = '5' THEN '(計績)' WHEN UI.CODE_TYPE = '6' THEN '(維護)' ELSE '' END AS UEMP_NAME ");
						
			// add by ocean 6001:WMS-CR-20181113-01_個人高端客群處「業管系統_第一階段需求調整申請」_P2
			sql2.append(" , R.CREATOR, CASE WHEN R.REL_STATUS IN ('RAN', 'RAD', 'PAN', 'PAD') AND ((CREATE_MEM.EMP_STATUS = 'Y' AND R.CREATOR = :loginID) OR (CREATE_MEM.EMP_STATUS = 'N')) THEN 'Y' WHEN R.REL_STATUS NOT IN ('RAN', 'RAD', 'PAN', 'PAD') THEN 'Y' ELSE 'N' END AS SHOW_CODI ");

			sql2.append("FROM TBCRM_CUST_REL R ");
			sql2.append("LEFT JOIN TBCRM_CUST_MAST C1 ON R.CUST_ID_M = C1.CUST_ID ");
			sql2.append("LEFT JOIN VWORG_AO_INFO E ON C1.AO_CODE = E.AO_CODE ");
			sql2.append("LEFT JOIN TBCRM_CUST_PRV P ON R.CUST_ID_S = P.CUST_ID_S ");
			sql2.append("LEFT JOIN TBCRM_CUST_MAST P1 ON P.CUST_ID_M = P1.CUST_ID ");

			// add by ocean 6001:WMS-CR-20181113-01_個人高端客群處「業管系統_第一階段需求調整申請」_P2
			sql2.append("LEFT JOIN (SELECT EMP_ID, CASE WHEN SERVICE_FLAG = 'A' AND CHANGE_FLAG IN ('A', 'M', 'P') THEN 'Y' ELSE 'N' END AS EMP_STATUS FROM TBORG_MEMBER) CREATE_MEM ON R.CREATOR = CREATE_MEM.EMP_ID ");

			// add by ocean 6705
			sql2.append("LEFT JOIN VWORG_EMP_UHRM_INFO UI ON C1.AO_CODE = UI.UHRM_CODE ");
			
			sql2.append("WHERE 1 = 1 ");
			sql2.append("AND R.REL_MBR_YN = 'Y' ");
			sql2.append("AND R.CUST_ID_S = :cust_id ");
			sql2.append("ORDER BY REL_TYPE ");//0002495 : 請將"本人"固定放在最上方那欄以容易辨識。

			queryCondition2.setObject("cust_id", cust_id);
			
			if (((getUserVariable(FubonSystemVariableConsts.LOGIN_AOCODE_LIST)).toString().replace("[", "").replace("]", "")).length() > 0 && 
				(isFC || isFCH)) {
				queryCondition2.setObject("curr_ao_code", getUserVariable(FubonSystemVariableConsts.LOGIN_AOCODE_LIST));
			} else {
				queryCondition2.setObject("brNbrList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
			}

			queryCondition2.setObject("loginID", (String) getUserVariable(FubonSystemVariableConsts.LOGINID));
			
			queryCondition2.setQueryString(sql2.toString());

			return_VO.setResultList_rel(dam.exeQuery(queryCondition2));
		}

		sendRtnObject(return_VO);
	}

	//關係戶一年內最近一次下單費率查詢
	public void inquire_5(Object body, IPrimitiveMap header) throws JBranchException {
		CRM661InputVO inputVO = (CRM661InputVO) body;
		CRM661OutputVO return_VO = new CRM661OutputVO();

		if (inputVO.getCust_list().size() > 0) {
			dam = this.getDataAccessManager();
			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sql = new StringBuffer();

			//--FUND/01:股票，02:債券，03:平衡
			sql.append(" SELECT MAX(TO_DATE(ME.TXN_DATE,'yyyyMMdd')) AS TXN_DT, MAX(ME.ACT_RATE) AS FEE_RT, ME.CUST_ID, TF.FUND_TYPE AS P_TYPE ");
			sql.append(" FROM TBPMS_WMG_P22_DAY ME ");
			sql.append(" INNER JOIN TBPRD_FUND TF ON TF.PRD_ID = ME.FUND_CODE AND FUND_TYPE in ('01','02','03') ");
			sql.append(" WHERE TXN_DATE > TO_CHAR(SYSDATE - 365 ,'yyyyMMdd') ");
			sql.append(" AND CUST_ID in (:cust_list) ");
			sql.append(" GROUP BY CUST_ID, FUND_TYPE ");
			sql.append(" UNION ");
			//--ETF
			sql.append(" SELECT MAX(TXN_DT) AS TXN_DT, MAX(FEE_RT) AS FEE_RT, CUST_ID, 'ETF' AS P_TYPE ");
			sql.append(" FROM TBPMS_WMG_OVERA_STOCK_TXN ");
			sql.append(" WHERE TO_CHAR(TXN_DT, 'yyyyMMdd') > TO_CHAR(SYSDATE - 365 ,'YYYYMMdd') ");
			sql.append(" AND CUST_ID in (:cust_list) ");
			sql.append(" GROUP BY CUST_ID ");
			sql.append(" UNION ");
			//--BOND
			sql.append(" SELECT MAX(DELY_DT) AS TXN_DT, MAX(FEE_RT) AS FEE_RT, CUST_ID, 'BOND' AS P_TYPE ");
			sql.append(" FROM TBPMS_WMG_BOND_TXN ");
			sql.append(" WHERE TO_CHAR(DELY_DT, 'yyyyMMdd') > TO_CHAR(SYSDATE - 365 ,'yyyyMMdd') ");
			sql.append(" AND CUST_ID in (:cust_list) ");
			sql.append(" GROUP BY CUST_ID ");

			queryCondition.setObject("cust_list", inputVO.getCust_list());
			queryCondition.setQueryString(sql.toString());

			List list = dam.exeQuery(queryCondition);

			return_VO.setResultList_rel(list);
		}

		sendRtnObject(return_VO);
	}

	//關係戶新增查詢
	public void rel_inquire(Object body, IPrimitiveMap header) throws JBranchException {
		CRM661InputVO inputVO = (CRM661InputVO) body;
		CRM661OutputVO return_VO = new CRM661OutputVO();
		dam = this.getDataAccessManager();

		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();

		//		sql.append(" WITH REL AS ");
		//		sql.append(" ( ");
		//		sql.append(" SELECT C2.CUST_ID ");
		//		sql.append(" FROM TBCRM_CUST_REL R, TBCRM_CUST_MAST C2 ");
		//		sql.append(" WHERE R.CUST_ID_S = C2.CUST_ID ");
		//		sql.append(" AND R.CUST_ID_M = :cust_id_m ");
		//		sql.append(" ORDER BY REL_TYPE ");
		//		sql.append(" ) ");
		//		sql.append(" SELECT distinct ");
		//		sql.append(" A.CUST_NAME, A.CUST_ID, A.AO_CODE,  NVL(A.VIP_DEGREE,'M') AS VIP_DEGREE, ");
		//		sql.append(" B.EMP_NAME, B.EMP_ID, B.BRANCH_NAME, ");
		//		sql.append(" (CASE WHEN A.CUST_ID IN (SELECT distinct CUST_ID_M AS CUST_ID FROM TBCRM_CUST_REL WHERE CUST_ID_M != CUST_ID_S) THEN 'N' ELSE 'Y' END) AS M_CHECK ");	//給前端判斷是否為其他主戶或清單內的客戶
		//		sql.append(" FROM TBCRM_CUST_MAST A ");
		//		sql.append(" LEFT JOIN VWORG_AO_INFO B ON A.AO_CODE = B.AO_CODE ");
		//		sql.append(" LEFT JOIN TBCRM_CUST_REL R ON R.CUST_ID_S = A.CUST_ID ");
		//		sql.append(" WHERE 1 = 1 ");
		//		sql.append(" AND CUST_ID NOT IN (SELECT * FROM REL) ");

		sql.append(" SELECT distinct ");
		sql.append(" A.CUST_NAME, A.CUST_ID, A.AO_CODE,  NVL(A.VIP_DEGREE,'M') AS VIP_DEGREE, ");
		sql.append(" B.EMP_NAME, B.EMP_ID, B.BRANCH_NAME ");
		sql.append(" FROM TBCRM_CUST_MAST A ");
		sql.append(" LEFT JOIN VWORG_AO_INFO B ON A.AO_CODE = B.AO_CODE ");
		//		sql.append(" LEFT JOIN TBCRM_CUST_REL R ON R.CUST_ID_M = A.CUST_ID ");
		sql.append(" WHERE 1 = 1 ");
		sql.append(" AND NOT EXISTS (SELECT 1 FROM TBCRM_CUST_REL WHERE CUST_ID_M = :cust_id_m AND CUST_ID_S = A.CUST_ID ) ");
		sql.append(" AND NOT EXISTS (SELECT 1 FROM TBCRM_CUST_REL WHERE CUST_ID_M = A.CUST_ID AND CUST_ID_S = :cust_id_m ) ");

		if (StringUtils.isNotBlank(inputVO.getCust_id())) {
			sql.append("AND A.CUST_ID = :cust_id ");
			queryCondition.setObject("cust_id", inputVO.getCust_id());
		}
		if (StringUtils.isNotBlank(inputVO.getCust_name())) {
			sql.append("AND A.CUST_NAME = :cust_name ");
			queryCondition.setObject("cust_name", inputVO.getCust_name());
		}

		queryCondition.setObject("cust_id_m", inputVO.getCust_id_m());
		queryCondition.setQueryString(sql.toString());
		queryCondition.setMaxResults((Integer) SysInfo.getInfoValue(FubonSystemVariableConsts.QRY_MAX_RESULTS));

		List list = dam.exeQuery(queryCondition);
		return_VO.setResultList_rel_add(list);
		sendRtnObject(return_VO);
	}

	//新增關係戶
	public void rel_add(Object body, IPrimitiveMap header) throws JBranchException {
		CRM661InputVO inputVO = (CRM661InputVO) body;
		this.rel_add(inputVO);
		this.sendRtnObject(null);
	}

	public CRM661InputVO rel_add(CRM661InputVO inputVO) throws JBranchException {
		String cust_id = inputVO.getCust_id();
		if (StringUtils.isNotBlank(cust_id)) {

			dam = this.getDataAccessManager();
			//主戶，先搜尋有無自己的資料，沒有資料自建一筆
			QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			StringBuffer sql = new StringBuffer();
			sql.append("SELECT SEQ FROM TBCRM_CUST_REL WHERE CUST_ID_M = :cust_id AND CUST_ID_S = CUST_ID_M");
			queryCondition.setQueryString(sql.toString());
			queryCondition.setObject("cust_id", cust_id);

			List list = dam.exeQuery(queryCondition);

			if (list.size() == 0) {
				TBCRM_CUST_RELVO vo = new TBCRM_CUST_RELVO();
				vo.setSEQ(getSN());
				vo.setCUST_ID_M(cust_id);
				vo.setCUST_ID_S(cust_id);
				vo.setREL_TYPE("00"); //本人
				vo.setREL_MBR_YN("Y");
				//歸戶功能暫不使用
				vo.setJOIN_SRV_YN("N");
				dam.create(vo);
			}

			dam = this.getDataAccessManager();
			//從戶，先搜尋有無自己的資料，沒有資料自建一筆
			queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
			sql = new StringBuffer();
			sql.append("SELECT SEQ FROM TBCRM_CUST_REL WHERE CUST_ID_M = :cust_id AND CUST_ID_S = CUST_ID_M");
			queryCondition.setQueryString(sql.toString());
			queryCondition.setObject("cust_id", inputVO.getRel_cust_id());

			List list1 = dam.exeQuery(queryCondition);

			if (list1.size() == 0) {
				TBCRM_CUST_RELVO vo = new TBCRM_CUST_RELVO();
				vo.setSEQ(getSN());
				vo.setCUST_ID_M(inputVO.getRel_cust_id());
				vo.setCUST_ID_S(inputVO.getRel_cust_id());
				vo.setREL_TYPE("00"); //本人
				vo.setREL_MBR_YN("Y");
				//歸戶功能暫不使用
				vo.setJOIN_SRV_YN("N");
				dam.create(vo);
			}

			TBCRM_CUST_RELVO vo = new TBCRM_CUST_RELVO();
			String seq = getSN();
			inputVO.setSeq(seq);
			vo.setSEQ(seq);
			vo.setCUST_ID_M(cust_id);
			vo.setCUST_ID_S(inputVO.getRel_cust_id());
			vo.setREL_TYPE(inputVO.getRel_type());

			if (!StringUtils.isBlank(inputVO.getRel_type_oth()))
				vo.setREL_TYPE_OTH(inputVO.getRel_type_oth());

			vo.setREL_MBR_YN("N");
			vo.setREL_APL_TYPE("1");
			vo.setREL_STATUS("RAN");
			dam.create(vo);
		}
		return inputVO;
	}

	//刪除關係戶
	public void rel_delete(Object body, IPrimitiveMap header) throws JBranchException {
		CRM661InputVO inputVO = (CRM661InputVO) body;
		dam = this.getDataAccessManager();
		CRM661OutputVO return_VO = new CRM661OutputVO();

		TBCRM_CUST_RELVO vo = new TBCRM_CUST_RELVO();
		vo = (TBCRM_CUST_RELVO) dam.findByPKey(TBCRM_CUST_RELVO.TABLE_UID, inputVO.getSeq());

		//狀況一:申請待覆核後刪除 => 直接刪除
		if (inputVO.getRel_status().equals("RAN")) {
			dam.delete(vo);
			return_VO.setRel_delete("D");
		}
		//狀況二:歸戶申請待覆核後刪除 => 將歸戶申請的設定還原回RAN，狀況改為RAD
		else if (inputVO.getRel_status().equals("RAJ")) {
			vo.setJOIN_SRV_YN("N");
			vo.setJOIN_SRV_CUST_ID("");
			vo.setREL_STATUS("RAD");
			vo.setREL_APL_TYPE("2");
			dam.update(vo);
			return_VO.setRel_delete("U");
		}
		//狀況三:歸戶申請待覆核後刪除 => 將歸戶申請取消的設定還原回RAN，狀況改為RAD
		else if (inputVO.getRel_status().equals("RAC")) {
			vo.setJOIN_SRV_YN("Y");
			vo.setREL_STATUS("RAD");
			vo.setREL_APL_TYPE("2");
			dam.update(vo);
			return_VO.setRel_delete("U");
		}
		//其他正常情況
		else {
			vo.setREL_STATUS("RAD");
			vo.setREL_APL_TYPE("2");
			dam.update(vo);
			return_VO.setRel_delete("U");
		}
		sendRtnObject(return_VO);
	}

	//歸戶服務查詢
	public void rel_set_inquire(Object body, IPrimitiveMap header) throws JBranchException {
		CRM661InputVO inputVO = (CRM661InputVO) body;
		CRM661OutputVO return_VO = new CRM661OutputVO();
		dam = this.getDataAccessManager();

		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		/** 增加查詢JOIN_SRV_YN AS JOIN_SRV_YN_CHECK，做前端設定判斷用 **/
		sql.append("SELECT C2.CUST_NAME, C2.CUST_ID, C2.AO_CODE, ");
		sql.append("R.REL_TYPE, R.REL_TYPE_OTH, R.CUST_ID_M,");
		sql.append("R.JOIN_SRV_CUST_ID, R.SEQ, R.REL_STATUS, ");
		sql.append("E.BRANCH_NAME, E.EMP_NAME, E.EMP_ID, ");
		sql.append("(CASE WHEN R.JOIN_SRV_YN = 'Y' THEN 'Y' ELSE 'N' END) AS JOIN_SRV_YN_CHECK, ");
		sql.append("(CASE WHEN R.JOIN_SRV_YN = 'Y' THEN 'Y' ELSE 'N' END) AS JOIN_SRV_YN, ");
		sql.append("C2.VIP_DEGREE, C2.CON_DEGREE, ");
		sql.append("D.TXFEE_DSCNT_RATE_1, D.TXFEE_DSCNT_RATE_2, ");
		sql.append("D.TXFEE_DSCNT_RATE_3, D.TXFEE_DSCNT_RATE_4, D.TXFEE_DSCNT_RATE_5 ");
		sql.append("FROM TBCRM_CUST_REL R ");
		sql.append("LEFT JOIN TBCRM_CUST_MAST C1 ON R.CUST_ID_M = C1.CUST_ID ");
		sql.append("LEFT JOIN TBCRM_CUST_MAST C2 ON R.CUST_ID_S = C2.CUST_ID ");
		sql.append("LEFT JOIN VWORG_AO_INFO E ON C2.AO_CODE = E.AO_CODE ");
		sql.append("LEFT JOIN VWCRM_REL_TXFEE_DSCNT_RATE D ON C2.CUST_ID = D.CUST_ID ");
		sql.append("WHERE 1 = 1 ");
		sql.append("AND R.REL_MBR_YN = 'Y' ");
		sql.append("AND C1.CUST_ID = :cust_id ");
		sql.append("AND C2.AO_CODE = :ao_code ");
		sql.append("ORDER BY REL_TYPE ");//0002495 : 請將"本人"固定放在最上方那欄以容易辨識。
		queryCondition.setQueryString(sql.toString());
		queryCondition.setObject("cust_id", inputVO.getCust_id());
		queryCondition.setObject("ao_code", inputVO.getAo_code());

		List list = dam.exeQuery(queryCondition);

		return_VO.setResultList_rel_set(list);
		sendRtnObject(return_VO);
	}

	//歸戶服務執行
	public void rel_set(Object body, IPrimitiveMap header) throws JBranchException {
		CRM661InputVO inputVO = (CRM661InputVO) body;
		dam = this.getDataAccessManager();

		for (Map<String, Object> map : inputVO.getRel_set_list()) {

			TBCRM_CUST_RELVO vo = new TBCRM_CUST_RELVO();
			vo = (TBCRM_CUST_RELVO) dam.findByPKey(TBCRM_CUST_RELVO.TABLE_UID, map.get("SEQ").toString());
			vo.setJOIN_SRV_YN(map.get("JOIN_SRV_YN").toString());

			//
			if (!ObjectUtils.equals(map.get("JOIN_SRV_YN"), map.get("JOIN_SRV_YN_CHECK"))) {
				if (StringUtils.equals(map.get("JOIN_SRV_YN").toString(), "Y")) {
					vo.setREL_STATUS("RAJ");
				} else {
					vo.setREL_STATUS("RAC");
				}
			}
			vo.setJOIN_SRV_CUST_ID(inputVO.getJoin_srv_cust_id());
			dam.update(vo);
		}
		this.sendRtnObject(null);
	}

	//關係戶、歸戶覆核查詢
	public void rel_rpy_inquire(Object body, IPrimitiveMap header) throws JBranchException {

		// 依系統角色決定下拉選單可視範圍
		XmlInfo xmlInfo = new XmlInfo();
		boolean isUHRM = xmlInfo.doGetVariable("FUBONSYS.UHRM_ROLE", FormatHelper.FORMAT_2).containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE));
		boolean isUHRMMGR = xmlInfo.doGetVariable("FUBONSYS.UHRMMGR_ROLE", FormatHelper.FORMAT_2).containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE));
		boolean isUHRMBMMGR = xmlInfo.doGetVariable("FUBONSYS.UHRMBMMGR_ROLE", FormatHelper.FORMAT_2).containsKey(getUserVariable(FubonSystemVariableConsts.LOGINROLE));

		CRM661OutputVO return_VO = new CRM661OutputVO();
		dam = this.getDataAccessManager();

		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT R.SEQ, C1.CUST_ID AS CUST_ID_M, C1.CUST_NAME AS CUST_NAME_M, C2.CUST_ID AS CUST_ID_S, ");
		sql.append("       C2.CUST_NAME AS CUST_NAME_S, R.REL_TYPE, R.REL_TYPE_OTH, E2.BRANCH_NAME AS BRANCH_NAME_S, E2.EMP_ID AS EMP_ID_S, ");
		sql.append("       E2.EMP_NAME AS EMP_NAME_S, R.REL_STATUS, E1.EMP_ID AS EMP_ID_M, E1.EMP_NAME AS EMP_NAME_M, R.JOIN_SRV_CUST_ID, R.CREATOR ");
		sql.append("FROM TBCRM_CUST_REL R ");
		sql.append("LEFT JOIN TBCRM_CUST_MAST C1 ON R.CUST_ID_M = C1.CUST_ID ");
		sql.append("LEFT JOIN TBCRM_CUST_MAST C2 ON R.CUST_ID_S = C2.CUST_ID ");
		sql.append("LEFT JOIN VWORG_AO_INFO E1 ON C1.AO_CODE = E1.AO_CODE ");
		sql.append("LEFT JOIN VWORG_AO_INFO E2 ON C2.AO_CODE = E2.AO_CODE ");
		sql.append("LEFT JOIN VWCRM_REL_TXFEE_DSCNT_RATE D ON C2.CUST_ID = D.CUST_ID ");
		sql.append("WHERE 1 = 1 ");
		sql.append("AND (R.REL_STATUS = 'RAN' OR R.REL_STATUS = 'RAD' OR R.REL_STATUS = 'RAJ' OR R.REL_STATUS = 'RAC')  ");

		// add by ocean 0006001:WMS-CR-20181113-01_個人高端客群處「業管系統_第一階段需求調整申請」_P2
		if (isUHRM) {
			sql.append("AND R.MODIFIER = :uhrmEmpID ");
			queryCondition.setObject("uhrmEmpID", (String) getUserVariable(FubonSystemVariableConsts.LOGINID));
		} else if (isUHRMMGR) {
			sql.append("AND EXISTS (SELECT 1 FROM VWORG_EMP_UHRM_INFO T WHERE R.MODIFIER = T.EMP_ID AND T.DEPT_ID = :loginArea) ");
			queryCondition.setObject("loginArea", getUserVariable(FubonSystemVariableConsts.LOGIN_AREA));
		} else {
			sql.append("AND EXISTS (SELECT DISTINCT EMP_ID FROM VWORG_EMP_INFO INFO WHERE INFO.BRANCH_NBR IN (:brNbrList) AND R.MODIFIER = INFO.EMP_ID)");
			queryCondition.setObject("brNbrList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
			
			sql.append("AND NOT EXISTS (SELECT 1 FROM VWORG_EMP_UHRM_INFO T WHERE R.MODIFIER = T.EMP_ID) ");
		}

		queryCondition.setQueryString(sql.toString());

		List list = dam.exeQuery(queryCondition);

		return_VO.setResultList_rel_rpy(list);
		sendRtnObject(return_VO);

	}

	//關係戶、歸戶覆核執行
	public void rel_rpy(Object body, IPrimitiveMap header) throws JBranchException {
		CRM661InputVO inputVO = (CRM661InputVO) body;
		this.rel_rpy(inputVO);
		this.sendRtnObject(null);
	}

	public void rel_rpy(CRM661InputVO inputVO) throws JBranchException, DAOException {
		dam = this.getDataAccessManager();

		TBCRM_CUST_RELVO vo = new TBCRM_CUST_RELVO();
		vo = (TBCRM_CUST_RELVO) dam.findByPKey(TBCRM_CUST_RELVO.TABLE_UID, inputVO.getSeq());

		/** 覆核通過 **/
		if (inputVO.getRel_rpy_type().equals("Y")) {
			//RAN-關係戶申請
			if (inputVO.getRel_status().equals("RAN")) {
				vo.setREL_MBR_YN("Y");
				vo.setREL_STATUS("RSN");
				dam.update(vo);
			}
			//RAD-關係戶取消
			else if (inputVO.getRel_status().equals("RAD")) {
				dam.delete(vo);
			}
			//RAJ-歸戶申請
			else if (inputVO.getRel_status().equals("RAJ")) {
				vo.setJOIN_SRV_YN("Y");
				vo.setREL_STATUS("RSJ");
				dam.update(vo);
			}
			//RAC-歸戶取消
			else if (inputVO.getRel_status().equals("RAC")) {
				//歸戶為主戶:全部歸戶取消
				if (ObjectUtils.equals(inputVO.getJoin_srv_cust_id(), inputVO.getCust_id())) {

					QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
					StringBuffer sql = new StringBuffer();
					sql.append("SELECT * FROM TBCRM_CUST_REL WHERE CUST_ID_M = :cust_id ");
					queryCondition.setObject("cust_id", inputVO.getCust_id_m());
					queryCondition.setQueryString(sql.toString());

					List<Map<String, Object>> list = dam.exeQuery(queryCondition);

					for (Map<String, Object> map : list) {
						TBCRM_CUST_RELVO vo2 = new TBCRM_CUST_RELVO();
						vo2 = (TBCRM_CUST_RELVO) dam.findByPKey(TBCRM_CUST_RELVO.TABLE_UID, map.get("SEQ").toString());

						vo2.setJOIN_SRV_YN("N");
						vo2.setREL_STATUS("RSC");
						vo2.setJOIN_SRV_CUST_ID("");
						dam.update(vo);
					}
				}
				//歸戶為主戶:單一歸戶取消
				else {
					vo.setJOIN_SRV_YN("N");
					vo.setREL_STATUS("RSC");
					vo.setJOIN_SRV_CUST_ID("");
					dam.update(vo);
				}
			}
			/** 覆核拒絕 **/
		} else {
			//RAN-關係戶申請
			if (inputVO.getRel_status().equals("RAN")) {
				dam.delete(vo);
			}
			//RAD-關係戶取消
			else if (inputVO.getRel_status().equals("RAD")) {
				vo.setREL_STATUS("RRD");
				dam.update(vo);
			}
			//RAJ-歸戶申請
			else if (inputVO.getRel_status().equals("RAJ")) {
				vo.setJOIN_SRV_YN("N");
				vo.setREL_STATUS("RRJ");
				dam.update(vo);
			}
			//RAC-歸戶取消
			else if (inputVO.getRel_status().equals("RAC")) {
				vo.setJOIN_SRV_YN("Y");
				vo.setREL_STATUS("RRC");
				dam.update(vo);
			}
		}
	}

	//首頁查詢家庭戶
	public void query_PRV(Object body, IPrimitiveMap header) throws JBranchException {
		CRM661InputVO inputVO = (CRM661InputVO) body;
		CRM661OutputVO return_VO = new CRM661OutputVO();
		dam = this.getDataAccessManager();

		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append(" SELECT * FROM TBCRM_CUST_PRV ");
		sql.append(" WHERE PRV_MBR_YN = 'Y' ");
		sql.append(" 	AND CUST_ID_M != CUST_ID_S ");
		sql.append(" 	AND (CUST_ID_M = :cust_id OR CUST_ID_S = :cust_id)  ");
		sql.append(" UNION ");

		//新增邏輯
		//只要是從戶且為主管待覆核,則無法再為主戶
		sql.append(" SELECT * FROM TBCRM_CUST_PRV ");
		sql.append(" WHERE PRV_MBR_YN = 'N' ");
		sql.append(" 	AND CUST_ID_M != CUST_ID_S ");
		sql.append(" 	AND CUST_ID_S = :cust_id ");
		sql.append(" 	AND PRV_STATUS = 'PAN' ");

		queryCondition.setQueryString(sql.toString());
		queryCondition.setObject("cust_id", inputVO.getCust_id());

		List list = dam.exeQuery(queryCondition);
		return_VO.setResultList(list);
		sendRtnObject(return_VO);
	}

	//主從戶判斷
	public void MS_check(Object body, IPrimitiveMap header) throws JBranchException {
		CRM661InputVO inputVO = (CRM661InputVO) body;
		CRM661OutputVO return_VO = new CRM661OutputVO();
		dam = this.getDataAccessManager();

		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();

		sql.append(" SELECT CUST_ID_M, CUST_ID_S ");
		sql.append(" FROM TBCRM_CUST_REL ");
		sql.append(" WHERE CUST_ID_M != CUST_ID_S ");
		sql.append(" AND (CUST_ID_M = :cust_id OR CUST_ID_S = :cust_id) ");

		queryCondition.setObject("cust_id", inputVO.getCust_id());
		queryCondition.setQueryString(sql.toString());

		List list = dam.exeQuery(queryCondition);

		return_VO.setResultList(list);
		sendRtnObject(return_VO);

	}

	//取序號
	private String getSN() throws JBranchException {
		//		SerialNumberUtil sn = new SerialNumberUtil();
		//		String seqNum = "";
		//		try{
		//			seqNum = sn.getNextSerialNumber("CRM661");
		//		}
		//		catch(Exception e){
		//			sn.createNewSerial("CRM661", "00000", null, null, null, 1, new Long("99999"), "y", new Long("0"), null);
		//			seqNum = sn.getNextSerialNumber("CRM661");
		//		}
		//		return seqNum;

		return seriaNextSequence.nextSeqLpad("SEQ_CRM661", 10, "0");
	}
}