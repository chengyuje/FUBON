package com.systex.jbranch.app.server.fps.cus140;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBCAM_IVG_PLAN_CONTENTPK;
import com.systex.jbranch.app.common.fps.table.TBCAM_IVG_PLAN_CONTENTVO;
import com.systex.jbranch.app.common.fps.table.TBCAM_IVG_PLAN_FIELDPK;
import com.systex.jbranch.app.common.fps.table.TBCAM_IVG_PLAN_FIELDVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * Created by SebastianWu on 2016/7/13.
 */
@Component("cus140")
@Scope("request")
public class CUS140 extends FubonWmsBizLogic {

	private DataAccessManager dam = null;

	/**
	 * 依登入使用者與查詢條件,查詢計畫內容
	 *
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void queryData(Object body, IPrimitiveMap header) throws JBranchException {

		// 依系統角色決定下拉選單可視範圍
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> headmgrMap = xmlInfo.doGetVariable("FUBONSYS.HEADMGR_ROLE", FormatHelper.FORMAT_2); //總行
		Map<String, String> armgrMap = xmlInfo.doGetVariable("FUBONSYS.ARMGR_ROLE", FormatHelper.FORMAT_2); //業務處
		Map<String, String> mbrmgrMap = xmlInfo.doGetVariable("FUBONSYS.MBRMGR_ROLE", FormatHelper.FORMAT_2); //營運區
		Map<String, String> FAIAMap = xmlInfo.doGetVariable("FUBONSYS.FAIA_ROLE", FormatHelper.FORMAT_2);

		CUS140InputVO inputVO = (CUS140InputVO) body;
		CUS140OutputVO outputVO = new CUS140OutputVO();
		dam = this.getDataAccessManager();

		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT MAIN.IVG_PLAN_SEQ, CONTENT.IVG_RESULT_SEQ, CONTENT.EMP_ID as CONTENT_EMP_ID, MEMBER.EMP_NAME as CONTENT_EMP_NAME, CONTENT.REGION_CENTER_ID as CONTENT_REGION, CONTENT.BRANCH_AREA_ID as CONTENT_AREA, CONTENT.BRANCH_NBR as CONTENT_BRANCH, CONTENT.ROLE_ID as CONTENT_ROLE_ID, MAIN.IVG_PLAN_NAME, MAIN.IVG_PLAN_DESC, MAIN.IVG_TYPE, MAIN.IVG_PLAN_TYPE, MAIN.IVG_START_DATE, MAIN.IVG_END_DATE, MAIN.MODIFIER, MEMBER2.EMP_NAME as MODIFIER_NAME, MAIN.LASTUPDATE, CONTENT.RES_FLAG, FL.DOC_ID, FL.DOC_NAME ");
		sql.append("FROM TBCAM_IVG_PLAN_MAIN MAIN ");
		//多筆回報有複數content，取ivg_result_seq 最新一筆回報
		sql.append("LEFT JOIN ( ");
		sql.append("  SELECT Z.IVG_PLAN_SEQ, Z.IVG_RESULT_SEQ, Z.EMP_ID, Z.RES_FLAG, Z.REGION_CENTER_ID, Z.BRANCH_AREA_ID, Z.BRANCH_NBR, Z.ROLE_ID ");
		sql.append("  FROM TBCAM_IVG_PLAN_CONTENT Z ");
		sql.append("  WHERE Z.IVG_RESULT_SEQ in ( ");
		sql.append("    SELECT MAX(Y.IVG_RESULT_SEQ) ");
		sql.append("    FROM TBCAM_IVG_PLAN_CONTENT Y ");
		sql.append("    WHERE Y.EMP_ID = :emp_id ");
		sql.append("    AND Y.ROLE_ID = :role_id ");

		if (headmgrMap.containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			sql.append("    AND (Y.REGION_CENTER_ID IN (:rcIdList) OR Y.REGION_CENTER_ID IS NULL) ");
			sql.append("    AND (Y.BRANCH_AREA_ID IN (:opIdList) OR Y.BRANCH_AREA_ID IS NULL) ");
			sql.append("    AND (Y.BRANCH_NBR IN (:brNbrList) OR Y.BRANCH_NBR IS NULL) ");
		} else if (armgrMap.containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			sql.append("    AND (Y.REGION_CENTER_ID IN (:rcIdList)) ");
			sql.append("    AND (Y.BRANCH_AREA_ID IN (:opIdList) OR Y.BRANCH_AREA_ID IS NULL) ");
			sql.append("    AND (Y.BRANCH_NBR IN (:brNbrList) OR Y.BRANCH_NBR IS NULL) ");
		} else if (mbrmgrMap.containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			sql.append("    AND (Y.REGION_CENTER_ID IN (:rcIdList)) ");
			sql.append("    AND (Y.BRANCH_AREA_ID IN (:opIdList)) ");
			sql.append("    AND (Y.BRANCH_NBR IN (:brNbrList) OR Y.BRANCH_NBR IS NULL) ");
		} else if (FAIAMap.containsKey((String) getUserVariable(FubonSystemVariableConsts.LOGINROLE))) {
			sql.append("    AND (Y.REGION_CENTER_ID IN (:rcIdList) OR Y.REGION_CENTER_ID IS NULL) ");
			sql.append("    AND (Y.BRANCH_AREA_ID IN (:opIdList) OR Y.BRANCH_AREA_ID IS NULL) ");
			sql.append("    AND (Y.BRANCH_NBR IN (:brNbrList) OR Y.BRANCH_NBR IS NULL) ");
		} else {
			if (StringUtils.lowerCase(inputVO.getMemLoginFlag()).indexOf("uhrm") < 0 || 
				StringUtils.lowerCase(inputVO.getMemLoginFlag()).equals("uhrm")) {
				sql.append("    AND (Y.REGION_CENTER_ID IN (:rcIdList)) ");
				sql.append("    AND (Y.BRANCH_AREA_ID IN (:opIdList)) ");
				sql.append("    AND (Y.BRANCH_NBR IN (:brNbrList)) ");
			} else {
				sql.append("    AND (Y.REGION_CENTER_ID IN (:rcIdList) OR Y.REGION_CENTER_ID IS NULL) ");
				sql.append("    AND (Y.BRANCH_AREA_ID IN (:opIdList) OR Y.BRANCH_AREA_ID IS NULL) ");
				sql.append("    AND (Y.BRANCH_NBR IN (:brNbrList) OR Y.BRANCH_NBR IS NULL) ");
			}
		}

		queryCondition.setObject("rcIdList", getUserVariable(FubonSystemVariableConsts.AVAILREGIONLIST));
		queryCondition.setObject("opIdList", getUserVariable(FubonSystemVariableConsts.AVAILAREALIST));
		queryCondition.setObject("brNbrList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));

		sql.append("    GROUP BY Y.IVG_PLAN_SEQ");
		sql.append("  )");
		sql.append(") CONTENT ON MAIN.IVG_PLAN_SEQ = CONTENT.IVG_PLAN_SEQ ");
		sql.append("LEFT JOIN TBORG_MEMBER MEMBER ON CONTENT.EMP_ID = MEMBER.EMP_ID ");
		sql.append("LEFT JOIN TBORG_MEMBER MEMBER2 ON MAIN.MODIFIER = MEMBER2.EMP_ID ");
		sql.append("LEFT JOIN TBSYS_FILE_MAIN FL ON MAIN.DOC_ID = FL.DOC_ID ");
		sql.append("WHERE 1=1 ");

		// 只查自己 2016/12/13
		sql.append("AND CONTENT.EMP_ID = :emp_id ");
		queryCondition.setObject("emp_id", getUserVariable(FubonSystemVariableConsts.LOGINID));
		queryCondition.setObject("role_id", getUserVariable(FubonSystemVariableConsts.LOGINROLE));

		if (inputVO.getIvgPlanSeq() != null) {
			sql.append("AND MAIN.IVG_PLAN_SEQ = :plan_seq ");
			queryCondition.setObject("plan_seq", inputVO.getIvgPlanSeq());
		}

		//回報計畫名稱
		if (!StringUtils.isBlank(inputVO.getIvgPlanName())) {
			sql.append("AND MAIN.IVG_PLAN_NAME LIKE :plan_name ");
			queryCondition.setObject("plan_name", "%" + inputVO.getIvgPlanName() + "%");
		}

		//回報類型
		if (!StringUtils.isBlank(inputVO.getIvgType())) {
			sql.append("AND MAIN.IVG_TYPE = :ivg_type ");
			queryCondition.setObject("ivg_type", inputVO.getIvgType());
		}

		// 計畫狀態 mantis always 有效
		sql.append("AND MAIN.IVG_PLAN_TYPE = '1' and TRUNC(sysdate) between TRUNC(MAIN.IVG_START_DATE) and TRUNC(MAIN.IVG_END_DATE) ");

		//是否已回報
		if (!StringUtils.isBlank(inputVO.getResFlag())) {
			sql.append("AND CONTENT.RES_FLAG = :res_flag ");
			queryCondition.setObject("res_flag", inputVO.getResFlag());
		}

		//開始日期
		if (inputVO.getIvgStartDate() != null) {
			sql.append("AND TRUNC(MAIN.IVG_START_DATE) >= :ivg_start_date ");
			queryCondition.setObject("ivg_start_date", inputVO.getIvgStartDate());
		}

		//截止日期
		if (inputVO.getIvgEndDate() != null) {
			sql.append("AND TRUNC(MAIN.IVG_END_DATE) <= :ivg_end_date ");
			queryCondition.setObject("ivg_end_date", inputVO.getIvgEndDate());
		}

		//依到期日由近到遠排序，未回報者排在上面，接著才是已回報者
		sql.append("ORDER BY MAIN.IVG_END_DATE, CONTENT.RES_FLAG ");

		queryCondition.setQueryString(sql.toString());
		ResultIF list = dam.executePaging(queryCondition, inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
		int totalPage = list.getTotalPage();
		outputVO.setTotalPage(totalPage);
		outputVO.setResultList(list);
		outputVO.setTotalRecord(list.getTotalRecord());
		outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());
		this.sendRtnObject(outputVO);
	}

	/**
	 * 依各計畫,顯示其動態欄位
	 *
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void queryField(Object body, IPrimitiveMap header) throws JBranchException {
		CUS140InputVO inputVO = (CUS140InputVO) body;
		CUS140OutputVO outputVO = new CUS140OutputVO();
		dam = this.getDataAccessManager();

		//得到 IvgResultSeq
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT IVG_RESULT_SEQ ");
		sql.append("FROM TBCAM_IVG_PLAN_CONTENT ");
		sql.append("WHERE 1 = 1 ");
		sql.append("AND IVG_PLAN_SEQ = :plan_seq ");
		sql.append("AND EMP_ID = :emp_id ");
		sql.append("AND ROLE_ID = :role_id ");

		queryCondition.setObject("plan_seq", inputVO.getIvgPlanSeq());
		queryCondition.setObject("emp_id", inputVO.getEmpID());
		queryCondition.setObject("role_id", inputVO.getRoleID());
		if (StringUtils.isNotBlank(inputVO.getRegionID())) {
			sql.append("AND REGION_CENTER_ID = :region ");
			queryCondition.setObject("region", inputVO.getRegionID());
		}
		if (StringUtils.isNotBlank(inputVO.getAreaID())) {
			sql.append("AND BRANCH_AREA_ID = :area ");
			queryCondition.setObject("area", inputVO.getAreaID());
		}
		if (StringUtils.isNotBlank(inputVO.getBranchID())) {
			sql.append("AND BRANCH_NBR = :bra_nbr ");
			queryCondition.setObject("bra_nbr", inputVO.getBranchID());
		}
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> tmplist = dam.exeQuery(queryCondition);
		List<BigDecimal> resultSeqList = new ArrayList<BigDecimal>();
		for (Map<String, Object> map : tmplist) {
			resultSeqList.add((BigDecimal) map.get("IVG_RESULT_SEQ"));
		}

		//根據resultSeqList得到回報
		List<List<Map<String, Object>>> resultList = new ArrayList<List<Map<String, Object>>>();
		if (resultSeqList.size() > 0) {
			for (Object seq : resultSeqList) {
				queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
				sql = new StringBuffer();
				sql.append("SELECT FIELD_LABEL, FIELD_TYPE, DROPDOWN_CONTENT, FIELD_VALUE, IVG_FIELD_SEQ, IVG_RESULT_SEQ, FIELD_RANK AS ORDER_NO, DISPLAYONLY ");
				sql.append("FROM TBCAM_IVG_PLAN_FIELD ");
				sql.append("WHERE 1 = 1 ");

				sql.append("AND IVG_RESULT_SEQ = :res_seq ");
				queryCondition.setObject("res_seq", seq);

				if (inputVO.getIvgPlanSeq() != null) {
					sql.append("AND IVG_PLAN_SEQ = :plan_seq ");
					queryCondition.setObject("plan_seq", inputVO.getIvgPlanSeq());
				}
				sql.append("ORDER BY IVG_RESULT_SEQ, FIELD_RANK ");

				queryCondition.setQueryString(sql.toString());
				resultList.add(dam.exeQuery(queryCondition));
			}
			outputVO.setResultList(resultList);
		}

		this.sendRtnObject(outputVO);
	}

	private void updateData(CUS140InputVO inputVO, List<Map<String, Object>> resultList) throws Exception {
		Set<BigDecimal> idList = new HashSet<BigDecimal>();
		for (Map<String, Object> maps : resultList) {
			if (!"Y".equals(maps.get("DISPLAYONLY")) && StringUtils.isNotBlank(ObjectUtils.toString(maps.get("FIELD_VALUE")))) {
				// TBCAM_IVG_PLAN_FIELD
				TBCAM_IVG_PLAN_FIELDPK fieldPkVO = new TBCAM_IVG_PLAN_FIELDPK(new BigDecimal((Double) maps.get("IVG_FIELD_SEQ")), inputVO.getIvgPlanSeq(), new BigDecimal((Double) maps.get("IVG_RESULT_SEQ")));
				TBCAM_IVG_PLAN_FIELDVO fieldVO = (TBCAM_IVG_PLAN_FIELDVO) dam.findByPKey(TBCAM_IVG_PLAN_FIELDVO.TABLE_UID, fieldPkVO);
				fieldVO.setFIELD_VALUE(ObjectUtils.toString(maps.get("FIELD_VALUE")));
				dam.update(fieldVO);

				idList.add(new BigDecimal((Double) maps.get("IVG_RESULT_SEQ")));
			}
		}

		// TBCAM_IVG_PLAN_CONTENT
		for (BigDecimal seq : idList) {
			TBCAM_IVG_PLAN_CONTENTPK ivgpk = new TBCAM_IVG_PLAN_CONTENTPK();
			ivgpk.setEMP_ID(inputVO.getEmpID());
			ivgpk.setIVG_PLAN_SEQ(inputVO.getIvgPlanSeq());
			ivgpk.setIVG_RESULT_SEQ(seq);
			TBCAM_IVG_PLAN_CONTENTVO ivgvo = (TBCAM_IVG_PLAN_CONTENTVO) dam.findByPKey(TBCAM_IVG_PLAN_CONTENTVO.TABLE_UID, ivgpk);
			if (ivgvo != null) {
				ivgvo.setRES_FLAG("Y");
				dam.update(ivgvo);
			}
		}
	}

	public void saveData(Object body, IPrimitiveMap header) throws JBranchException, Exception {
		CUS140InputVO inputVO = (CUS140InputVO) body;
		dam = this.getDataAccessManager();

		//單筆提供修改功能
		if ("1".equals(inputVO.getIvgType())) {
			// TBCAM_IVG_PLAN_FIELD
			List<List<Map<String, Object>>> fields = inputVO.getInputDynamicField();
			for (List<Map<String, Object>> resultList : fields) {
				updateData(inputVO, resultList);
			}
		} else if ("2".equals(inputVO.getIvgType())) //多筆提供新增功能
		{
			// TBCAM_IVG_PLAN_FIELD
			List<Map<String, Object>> fields = inputVO.getInputDynamicField();
			//第一筆為更新資料，所以IVG_FIELD_SEQ 已存在，並且大於0。  
			BigDecimal fieldSeq = new BigDecimal(ObjectUtils.toString(fields.get(0).get("IVG_FIELD_SEQ")));
			if (fieldSeq.intValue() > 0) {
				updateData(inputVO, fields);
			} else {
				String pid = ObjectUtils.toString(inputVO.getIvgPlanSeq());
				String rid = getSN("CUS130_CONTENT");
				// TBCAM_IVG_PLAN_CONTENT
				insertCONTENT(inputVO, pid, rid);
				// TBCAM_IVG_PLAN_FIELD
				insertFIELD(fields, pid, rid);
			}
		}

		this.sendRtnObject(null);
	}

	// 流水號
	private String getSN(String name) throws JBranchException {
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		switch (name) {
		case "CUS130_CONTENT":
			sql.append("SELECT SQ_CUS130_CONTENT.nextval AS SEQ FROM DUAL ");
			break;
		case "CUS130_FIELD":
			sql.append("SELECT SQ_CUS130_FIELD.nextval AS SEQ FROM DUAL ");
			break;
		}
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> SEQLIST = dam.exeQuery(queryCondition);
		return ObjectUtils.toString(SEQLIST.get(0).get("SEQ"));
	}

	// TBCAM_IVG_PLAN_CONTENT
	private void insertCONTENT(CUS140InputVO inputVO, String pid, String rid) throws Exception {
		TBCAM_IVG_PLAN_CONTENTVO cvo = new TBCAM_IVG_PLAN_CONTENTVO();
		TBCAM_IVG_PLAN_CONTENTPK cpk = new TBCAM_IVG_PLAN_CONTENTPK();
		cpk.setIVG_RESULT_SEQ(new BigDecimal(rid));
		cpk.setIVG_PLAN_SEQ(new BigDecimal(pid));
		cpk.setEMP_ID(inputVO.getEmpID());
		cvo.setcomp_id(cpk);
		cvo.setREGION_CENTER_ID(inputVO.getRegionID());
		cvo.setBRANCH_AREA_ID(inputVO.getAreaID());
		cvo.setBRANCH_NBR(inputVO.getBranchID());
		cvo.setROLE_ID(inputVO.getRoleID());
		cvo.setRES_FLAG("Y");
		dam.create(cvo);
	}

	// TBCAM_IVG_PLAN_FIELD
	private void insertFIELD(List<Map<String, Object>> list, String pid, String rid) throws Exception {
		for (Map<String, Object> fmap : list) {
			TBCAM_IVG_PLAN_FIELDVO pvo = new TBCAM_IVG_PLAN_FIELDVO();
			TBCAM_IVG_PLAN_FIELDPK ppk = new TBCAM_IVG_PLAN_FIELDPK();
			ppk.setIVG_FIELD_SEQ(new BigDecimal(getSN("CUS130_FIELD")));
			ppk.setIVG_PLAN_SEQ(new BigDecimal(pid));
			ppk.setIVG_RESULT_SEQ(new BigDecimal(rid));
			pvo.setcomp_id(ppk);
			pvo.setFIELD_RANK(new BigDecimal(ObjectUtils.toString(fmap.get("ORDER_NO"))));
			pvo.setFIELD_LABEL(ObjectUtils.toString(fmap.get("FIELD_LABEL")));
			pvo.setFIELD_VALUE(ObjectUtils.toString(fmap.get("FIELD_VALUE")));
			pvo.setFIELD_TYPE(ObjectUtils.toString(fmap.get("FIELD_TYPE")));
			pvo.setDROPDOWN_CONTENT(ObjectUtils.toString(fmap.get("DROPDOWN_CONTENT")));
			dam.create(pvo);
		}
	}

	public void delField(Object body, IPrimitiveMap header) throws Exception {
		CUS140InputVO inputVO = (CUS140InputVO) body;
		dam = this.getDataAccessManager();

		// TBCAM_IVG_PLAN_CONTENT
		QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		condition.setQueryString("delete TBCAM_IVG_PLAN_CONTENT where IVG_PLAN_SEQ = :seq and IVG_RESULT_SEQ = :resultSeq");
		condition.setObject("seq", inputVO.getIvgPlanSeq());
		condition.setObject("resultSeq", inputVO.getIvgResultSeq());
		dam.exeUpdate(condition);

		// TBCAM_IVG_PLAN_FIELD
		condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		condition.setQueryString("delete TBCAM_IVG_PLAN_FIELD where IVG_PLAN_SEQ = :seq and IVG_RESULT_SEQ = :resultSeq");
		condition.setObject("seq", inputVO.getIvgPlanSeq());
		condition.setObject("resultSeq", inputVO.getIvgResultSeq());
		dam.exeUpdate(condition);

		this.sendRtnObject(null);
	}
}