package com.systex.jbranch.app.server.fps.org121;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBORG_MEMBERVO;
import com.systex.jbranch.app.common.fps.table.TBORG_SALES_AOCODE_REVIEWVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataManager.DataManager;
import com.systex.jbranch.platform.common.dataManager.WorkStation;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Component("org121")
@Scope("request")
public class ORG121 extends FubonWmsBizLogic{
	
	public DataAccessManager dam = null;
	
	/*
	 * 取得可執行編輯的群組
	 */
	public List<Map<String, Object>> getFuncList (DataAccessManager dam, String function) throws JBranchException {
		
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT PRIVILEGEID ");
		sb.append("FROM TBSYSSECUROLPRIASS ");
		sb.append("WHERE PRIVILEGEID IN (SELECT PRIVILEGEID FROM TBSYSSECUPRIFUNMAP WHERE ITEMID = 'ORG120' AND FUNCTIONID = :function) "); 
		sb.append("AND ROLEID = :roleID ");
		queryCondition.setObject("roleID", getUserVariable(FubonSystemVariableConsts.LOGINROLE));
		queryCondition.setObject("function", function);
		queryCondition.setQueryString(sb.toString());
		
		return dam.exeQuery(queryCondition);
	}

	public void getBranchMbrQuotaLst(Object body, IPrimitiveMap header) throws JBranchException {
		
		WorkStation ws = DataManager.getWorkStation(uuid);
		ORG121InputVO inputVO = (ORG121InputVO)body;
		ORG121OutputVO outputVO = new ORG121OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		List<Map<String, Object>> reviewList = getFuncList(dam, "confirm");
		List<Map<String, Object>> maintenanceList = getFuncList(dam, "maintenance");
		
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT CASE WHEN (SELECT COUNT(1) FROM TBORG_SALES_AOCODE_REVIEW RE WHERE RE.EMP_ID = INFO.EMP_ID AND REVIEW_STATUS = 'W') > 0 THEN 'W' ELSE 'Y' END AS REVIEW_STATUS, ");
		sb.append("       CASE WHEN (SELECT PRIVILEGEID FROM TBSYSSECUROLPRIASS WHERE ROLEID = INFO.ROLE_ID) = '002' THEN 'FC' ");
		sb.append("            WHEN (SELECT PRIVILEGEID FROM TBSYSSECUROLPRIASS WHERE ROLEID = INFO.ROLE_ID) = '003' THEN 'FCH' ");
		sb.append("            ELSE NULL ");
		sb.append("       END AS PRIVILEGEID, ");
		sb.append("       INFO.REGION_CENTER_ID, INFO.REGION_CENTER_NAME, INFO.BRANCH_AREA_ID, INFO.BRANCH_AREA_NAME, INFO.BRANCH_NBR, INFO.BRANCH_NAME, ");
		sb.append("       INFO.EMP_ID, INFO.EMP_NAME, ");
		sb.append("       CASE WHEN REVIEW.PERF_EFF_DATE IS NOT NULL THEN REVIEW.PERF_EFF_DATE ELSE MEM.PERF_EFF_DATE END AS PERF_EFF_DATE, ");
		sb.append("       MEM.CREATETIME, MEM.CREATOR, ");
		sb.append("       MEM.MODIFIER, MEM.LASTUPDATE, ");
		sb.append("       (SELECT COUNT(1) FROM TBORG_SALES_AOCODE_REVIEW SA WHERE SA.EMP_ID = INFO.EMP_ID) AS COUNTS ");
		sb.append("FROM VWORG_EMP_INFO INFO ");
		sb.append("LEFT JOIN TBORG_MEMBER MEM ON INFO.EMP_ID = MEM.EMP_ID ");
		sb.append("LEFT JOIN TBORG_SALES_AOCODE_REVIEW REVIEW ON MEM.EMP_ID = REVIEW.EMP_ID AND REVIEW.REVIEW_STATUS = 'W' ");
		sb.append("WHERE INFO.PRIVILEGEID = 'JRM' ");
		
		if (StringUtils.isNotBlank(inputVO.getRegion_center_id()) && !"null".equals(inputVO.getRegion_center_id())) {
			sb.append("AND INFO.REGION_CENTER_ID = :regionCenterID "); 
			queryCondition.setObject("regionCenterID", inputVO.getRegion_center_id());
		} else {
			sb.append("AND INFO.REGION_CENTER_ID IN (:regionCenterIDList) ");
			queryCondition.setObject("regionCenterIDList", getUserVariable(FubonSystemVariableConsts.AVAILREGIONLIST));
		}
	
		if (StringUtils.isNotBlank(inputVO.getBranch_area_id()) && !"null".equals(inputVO.getBranch_area_id())) {
			sb.append("AND INFO.BRANCH_AREA_ID = :branchAreaID "); 
			queryCondition.setObject("branchAreaID", inputVO.getBranch_area_id());
		} else {
			sb.append("AND INFO.BRANCH_AREA_ID IN (:branchAreaIDList) ");
			queryCondition.setObject("branchAreaIDList", getUserVariable(FubonSystemVariableConsts.AVAILAREALIST));
		}
	
		if (StringUtils.isNotBlank(inputVO.getBranch_nbr()) && Integer.valueOf(inputVO.getBranch_nbr()) > 0) {
			sb.append("AND INFO.BRANCH_NBR = :branchID ");
			queryCondition.setObject("branchID", inputVO.getBranch_nbr());
		} else {
			sb.append("AND INFO.BRANCH_NBR IN (:branchIDList) ");
			queryCondition.setObject("branchIDList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
		}
		
		sb.append("ORDER BY CASE WHEN REVIEW_STATUS = 'W' THEN 0 ELSE 1 END, INFO.REGION_CENTER_ID, INFO.BRANCH_AREA_ID, INFO.BRANCH_NBR, INFO.EMP_ID ");

		queryCondition.setQueryString(sb.toString());
		
		List<Map<String, Object>> resList = dam.exeQuery(queryCondition);
		if (resList.size() > 0) {
			for (Map<String, Object> map : resList) {
				if (reviewList.size() > 0) {
					map.put("ACTION_R", "review");
				} 
				
				if (maintenanceList.size() > 0) {
					map.put("ACTION_M", "modify");
				} 

				if (reviewList.size() == 0 && maintenanceList.size() == 0){
					map.put("ACTION", "readonly");
				}
			}
		}
		
		outputVO.setDataList(resList);
		
		sendRtnObject(outputVO);
	}
	
	
	public void showORG121MOD (Object body, IPrimitiveMap header) throws JBranchException {
		
		ORG121InputVO inputVO = (ORG121InputVO) body;
		ORG121OutputVO outputVO = new ORG121OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT  ");
		sb.append("       INFO.REGION_CENTER_ID, ");
		sb.append("       INFO.REGION_CENTER_NAME, ");
		sb.append("       INFO.BRANCH_AREA_ID, ");
		sb.append("       INFO.BRANCH_AREA_NAME, ");
		sb.append("       INFO.BRANCH_NBR, ");
		sb.append("       INFO.BRANCH_NAME, ");
		sb.append("       INFO.EMP_ID, ");
		sb.append("       INFO.EMP_NAME, ");
		sb.append("       MEM.PERF_EFF_DATE ");
		sb.append("FROM VWORG_EMP_INFO INFO ");
		sb.append("LEFT JOIN TBORG_MEMBER MEM ON INFO.EMP_ID = MEM.EMP_ID ");
		sb.append("WHERE 1 = 1 ");
		sb.append("AND INFO.EMP_ID = :empID ");
		queryCondition.setObject("empID", inputVO.getEmpID());
		queryCondition.setQueryString(sb.toString());

		outputVO.setModList(dam.exeQuery(queryCondition));
		
		sendRtnObject(outputVO);
	}
	
	public void review (Object body, IPrimitiveMap header) throws JBranchException {
		
		ORG121InputVO inputVO = (ORG121InputVO)body;
		ORG121OutputVO outputVO = new ORG121OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT SEQNO, EMP_ID, AO_CODE, TYPE, AO_CODE_ATCH_REASON, ACTIVE_DATE, PERF_EFF_DATE, ACT_TYPE, REVIEW_STATUS, VERSION, CREATETIME, CREATOR, MODIFIER, LASTUPDATE ");
		sb.append("FROM TBORG_SALES_AOCODE_REVIEW ");
		sb.append("WHERE EMP_ID = :empID ");
		sb.append("AND REVIEW_STATUS = 'W' ");
		queryCondition.setObject("empID", inputVO.getEmpID());
		queryCondition.setQueryString(sb.toString());

		List<Map<String, Object>> list = dam.exeQuery(queryCondition);
		
		if (list.size() > 0) {
			for (Map<String, Object> map : list) {
				TBORG_SALES_AOCODE_REVIEWVO reVO = new TBORG_SALES_AOCODE_REVIEWVO();
				reVO = (TBORG_SALES_AOCODE_REVIEWVO) dam.findByPKey(TBORG_SALES_AOCODE_REVIEWVO.TABLE_UID, (BigDecimal) map.get("SEQNO"));
				
				if (null != reVO) {
					if (StringUtils.equals("Y", inputVO.getREVIEW_STATUS())) { //核可
						if (StringUtils.equals("M", reVO.getACT_TYPE()) && StringUtils.equals("W", reVO.getREVIEW_STATUS())) {
							TBORG_MEMBERVO memVO = new TBORG_MEMBERVO();
							memVO = (TBORG_MEMBERVO) dam.findByPKey(TBORG_MEMBERVO.TABLE_UID, reVO.getEMP_ID());
							memVO.setPERF_EFF_DATE(reVO.getPERF_EFF_DATE());
							dam.update(memVO);
						} 
						
						reVO.setREVIEW_STATUS(inputVO.getREVIEW_STATUS());
						dam.update(reVO);
					} else { //退回
						reVO.setREVIEW_STATUS(inputVO.getREVIEW_STATUS());
						
						dam.update(reVO);
					}
				}
			}
		}
		
		sendRtnObject(outputVO);
	}
	
	// === insert into review start ===
	public void addAoCodeSetting(Object body, IPrimitiveMap header) throws JBranchException {
		
		ORG121InputVO inputVO = (ORG121InputVO) body;
		dam = this.getDataAccessManager();
		
		TBORG_SALES_AOCODE_REVIEWVO vo = new TBORG_SALES_AOCODE_REVIEWVO();
		vo.setEMP_ID(inputVO.getEmpID());
		vo.setTYPE(inputVO.getTypeOne());
		vo.setACT_TYPE("M");
		vo.setREVIEW_STATUS("W");
		
		vo.setSEQNO(new BigDecimal(getSEQ()));
		vo.setAO_CODE(inputVO.getAo_code());
		vo.setPERF_EFF_DATE(new Timestamp(inputVO.getAoPerfEffDate().getTime()));

		dam.create(vo);
		
		sendRtnObject(null);
	}
	//  === insert into review end ===
	
	public void getReviewList (Object body, IPrimitiveMap header) throws JBranchException {
		
		ORG121InputVO inputVO = (ORG121InputVO) body;
		ORG121OutputVO outputVO = new ORG121OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sb = new StringBuffer(); 
		sb.append("SELECT SEQNO, EMP_ID, AO_CODE, TYPE, AO_CODE_ATCH_REASON, ACTIVE_DATE, PERF_EFF_DATE, ACT_TYPE, REVIEW_STATUS, VERSION, CREATETIME, CREATOR, MODIFIER, LASTUPDATE ");
		sb.append("FROM TBORG_SALES_AOCODE_REVIEW ");
		sb.append("WHERE EMP_ID = :empID ");
		sb.append("ORDER BY CREATETIME DESC");
		
		queryCondition.setQueryString(sb.toString());
		
		queryCondition.setObject("empID", inputVO.getEmpID());
		
		outputVO.setReviewList(dam.exeQuery(queryCondition));
	
		sendRtnObject(outputVO);
	}
	
	private String getSEQ() throws JBranchException {
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT SQ_TBORG_SALES_AOCODE_REVIEW.nextval AS SEQ FROM DUAL ");
		queryCondition.setQueryString(sql.toString());
		List<Map<String, Object>> SEQLIST = dam.exeQuery(queryCondition);
		
		return ObjectUtils.toString(SEQLIST.get(0).get("SEQ"));
	}

}
