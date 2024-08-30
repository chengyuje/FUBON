package com.systex.jbranch.app.server.fps.ins610;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * 產險新契約查詢-績效報表
 * @author Johnson
 *
 */
@Component("ins610")
@Scope("request")
public class INS610 extends FubonWmsBizLogic {
	private DataAccessManager dam = null;
	private Logger logger = LoggerFactory.getLogger(INS610.class);
	
	/**
	 * 執行查詢
	 * @param body
	 * @param header
	 * @throws JBranchException
	 */
	public void query(Object body, IPrimitiveMap header) throws JBranchException {
		INS610InputVO inputVO = (INS610InputVO) body;
		INS610OutputVO outputVO = new INS610OutputVO();
		String LoginBraNbr = (String) getCommonVariable(SystemVariableConsts.LOGINBRH);
		ArrayList<String> loginAO = (ArrayList<String>) getUserVariable(FubonSystemVariableConsts.LOGIN_AOCODE_LIST);
		dam = this.getDataAccessManager();
        QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
        setQuerySql(qc,inputVO);
		outputVO.setResultList(dam.exeQuery(qc));
		outputVO.setPlanRatio(getPlanRatio(outputVO.getResultList()));
		sendRtnObject(outputVO);
	}
	
	private void setQuerySql(QueryConditionIF qc, INS610InputVO inputVO){
		StringBuffer sb = new StringBuffer();
		sb.append("	SELECT ");
		sb.append(" A.PLAN_KEYNO MAIN_KEYNO, ");
		sb.append(" A.CUST_ID, ");
		sb.append(" B.PLAN_TYPE, ");
		sb.append(" B.STATUS, ");
		sb.append("	A.CREATOR, ");
		sb.append("	MEM.EMP_NAME, ");
		sb.append("	ORG.REGION_CENTER_ID, ");
		sb.append("	ORG.REGION_CENTER_NAME, ");
		sb.append("	ORG.BRANCH_AREA_ID, ");
		sb.append("	ORG.BRANCH_AREA_NAME, ");
		sb.append("	ORG.BRANCH_NBR, ");
		sb.append("	ORG.BRANCH_NAME, ");
		sb.append("	'保險規劃-'||TO_CHAR(B.CREATETIME, 'yyyymmdd') MAIN_NAME, ");
		sb.append(" TO_CHAR(A.CREATETIME,'YYYY/MM/DD') CREATETIME, ");
		sb.append(" TO_CHAR(A.LASTUPDATE,'YYYY/MM/DD') LASTUPDATE, ");
		sb.append(" C.CREATETIME INCOMING_TIME, ");
		sb.append("	C.INSPRD_ID, ");
		sb.append("	C.POLICY_NO ");
		sb.append("	FROM TBINS_PLAN_MAIN A ");
		sb.append("	JOIN TBINS_PLAN_DTL B ON A.PLAN_KEYNO = B.PLAN_KEYNO ");
		sb.append("	LEFT JOIN ( ");
		sb.append("		SELECT D.PLAN_D_KEYNO, ");
		sb.append("		LISTAGG(D.INSPRD_ID, ', ') WITHIN GROUP ( ");
		sb.append("			ORDER BY D.INSPRD_ID ");
		sb.append("		) INSPRD_ID, ");
		sb.append(" 	LISTAGG( ");
		sb.append(" 		E.POLICY_NO1||E.POLICY_NO2||E.POLICY_NO3, ', ') WITHIN GROUP ( ");
		sb.append(" 		ORDER BY D.INS_KEYNO ");
		sb.append(" 	) POLICY_NO, ");
		sb.append(" 	LISTAGG( ");
		sb.append(" 		TO_CHAR(E.CREATETIME,'YYYY/MM/DD'), ', ') WITHIN GROUP ( ");
		sb.append(" 		ORDER BY D.INS_KEYNO ");
		sb.append(" 	) CREATETIME ");
		sb.append(" 	FROM TBINS_PLAN_SUG_INSLIST D ");
		sb.append(" 	LEFT JOIN TBIOT_MAIN E ON D.INS_KEYNO = E.INS_KEYNO ");
		sb.append(" 	GROUP BY D.PLAN_D_KEYNO ");
		sb.append(" ) C ON C.PLAN_D_KEYNO = B.PLAN_D_KEYNO ");
		sb.append(" LEFT JOIN TBORG_MEMBER MEM ON MEM.EMP_ID = A.CREATOR ");
		sb.append(" LEFT JOIN VWORG_DEFN_INFO ORG ON MEM.DEPT_ID = ORG.BRANCH_NBR ");
		sb.append(" WHERE 1=1 ");
		if(!StringUtils.isBlank(inputVO.getStatus())) {
			sb.append(" AND B.STATUS = :status ");
			qc.setObject("status", inputVO.getStatus());
		}
		if(!StringUtils.isBlank(inputVO.getBranchAreaId())) {
			sb.append(" AND ORG.BRANCH_AREA_ID = :areaId ");
			qc.setObject("areaId", inputVO.getBranchAreaId());
		}
		if(!StringUtils.isBlank(inputVO.getBranchId())) {
			sb.append(" AND ORG.BRANCH_NBR = :branchId ");
			qc.setObject("branchId", inputVO.getBranchId());
		}
		if(!StringUtils.isBlank(inputVO.getCustId())) {
			sb.append(" AND A.CUST_ID = :custId ");
			qc.setObject("custId", inputVO.getCustId());
		}
		if(!StringUtils.isBlank(inputVO.getEmpId())) {
			sb.append(" AND A.CREATOR like :empId ");
			qc.setObject("empId", "%"+inputVO.getEmpId()+"%");
		}
		if(!StringUtils.isBlank(inputVO.getInsCode())) {
			sb.append(" AND C.INSPRD_ID = :insId ");
			qc.setObject("insId", inputVO.getInsCode());
		}
		if(inputVO.getsCreDate() != null || inputVO.geteCreDate() != null) {
			if(inputVO.getsCreDate() == null) {
				sb.append(" AND TRUNC(A.CREATETIME) <= :eDate ");
				qc.setObject("eDate", inputVO.geteCreDate());
			} else if(inputVO.geteCreDate() == null) {
				sb.append(" AND TRUNC(A.CREATETIME) >= :sDate ");
				qc.setObject("sDate", inputVO.getsCreDate());
			} else {
				sb.append(" AND TRUNC(A.CREATETIME) BETWEEN :sDate AND :eDate ");
				qc.setObject("sDate", inputVO.getsCreDate());
				qc.setObject("eDate", inputVO.geteCreDate());
			}
		}
		// --------------- SPP_SIDE ----------------------
		sb.append(" UNION ");
		sb.append(" SELECT ");
		sb.append(" X.SPP_ID MAIN_KEYNO, ");
		sb.append(" X.CUST_ID, ");
		sb.append(" X.SPP_TYPE PLAN_TYPE, ");
		sb.append(" X.STATUS, ");
		sb.append(" X.CREATOR, ");
		sb.append("	MEM.EMP_NAME, ");
		sb.append("	ORG.REGION_CENTER_ID, ");
		sb.append("	ORG.REGION_CENTER_NAME, ");
		sb.append("	ORG.BRANCH_AREA_ID, ");
		sb.append("	ORG.BRANCH_AREA_NAME, ");
		sb.append("	ORG.BRANCH_NBR, ");
		sb.append("	ORG.BRANCH_NAME, ");
		sb.append(" '特定目的-'||X.SPP_NAME MAIN_NAME, ");
		sb.append(" TO_CHAR(X.CREATETIME,'YYYY/MM/DD') CREATETIME, ");
		sb.append(" TO_CHAR(X.LASTUPDATE,'YYYY/MM/DD') LASTUPDATE, ");
		sb.append(" TO_CHAR(Y.CREATETIME,'YYYY/MM/DD') INCOMING_TIME, ");
		sb.append(" X.INSPRD_ID, ");
		sb.append(" Y.POLICY_NO1||Y.POLICY_NO2||Y.POLICY_NO3 POLICY_NO ");
		sb.append(" FROM TBINS_SPP_MAIN X ");
		sb.append(" LEFT JOIN TBIOT_MAIN Y ON X.INS_KEYNO = Y.INS_KEYNO ");
		sb.append(" LEFT JOIN TBORG_MEMBER MEM ON MEM.EMP_ID = X.CREATOR ");
		sb.append(" LEFT JOIN VWORG_DEFN_INFO ORG ON MEM.DEPT_ID = ORG.BRANCH_NBR ");
		sb.append(" WHERE 1=1 ");
		if(!StringUtils.isBlank(inputVO.getStatus())) {
			sb.append(" AND X.STATUS = :status ");
			qc.setObject("status", inputVO.getStatus());
		}
		if(!StringUtils.isBlank(inputVO.getBranchAreaId())) {
			sb.append(" AND ORG.BRANCH_AREA_ID = :areaId ");
			qc.setObject("areaId", inputVO.getBranchAreaId());
		}
		if(!StringUtils.isBlank(inputVO.getBranchId())) {
			sb.append(" AND ORG.BRANCH_NBR = :branchId ");
			qc.setObject("branchId", inputVO.getBranchId());
		}
		if(!StringUtils.isBlank(inputVO.getCustId())) {
			sb.append(" AND X.CUST_ID = :custId ");
			qc.setObject("custId", inputVO.getCustId());
		}
		if(!StringUtils.isBlank(inputVO.getEmpId())) {
			sb.append(" AND X.CREATOR like :empId ");
			qc.setObject("empId", "%"+inputVO.getEmpId()+"%");
		}
		if(!StringUtils.isBlank(inputVO.getInsCode())) {
			sb.append(" AND X.INSPRD_ID = :insId ");
			qc.setObject("insId", inputVO.getInsCode());
		}
		if(inputVO.getsCreDate() != null || inputVO.geteCreDate() != null) {
			if(inputVO.getsCreDate() == null) {
				sb.append(" AND TRUNC(X.CREATETIME) <= :eDate ");
				qc.setObject("eDate", inputVO.geteCreDate());
			} else if(inputVO.geteCreDate() == null) {
				sb.append(" AND TRUNC(X.CREATETIME) >= :sDate ");
				qc.setObject("sDate", inputVO.getsCreDate());
			} else {
				sb.append(" AND TRUNC(X.CREATETIME) BETWEEN :sDate AND :eDate ");
				qc.setObject("sDate", inputVO.getsCreDate());
				qc.setObject("eDate", inputVO.geteCreDate());
			}
		}
		sb.append(" ORDER BY CREATETIME DESC ");
		qc.setQueryString(sb.toString());
	}
	
	//規劃成效率=查詢案件中狀態為已投保之筆數/查詢案件筆數
	private String getPlanRatio(List<Map<String, Object>> resultList) throws JBranchException {
		XmlInfo xmlInfo = new XmlInfo();
		Map<String, String> sMap = xmlInfo.doGetVariable("INS.STATUS", FormatHelper.FORMAT_3);
		int n = 0;
		String key = null;
		for(String str : sMap.keySet()) {
			if(StringUtils.equals(sMap.get(str),"已投保")) {
				key = str;
				break;
			}
		}
		for(Map map : resultList) {
			String obj = ((Character)map.get("STATUS")).toString();
			if(StringUtils.equals(obj, key)) {
				n ++;
			}
			// 順便整理輸出 ->key變成vaule
			map.put("STATUS",sMap.get(obj));
		}
		
		return "規劃成效率: " + Math.round(10000.0 * n / resultList.size()) / 100.0 + "%";
	}
	
	//查詢客戶姓名
	public void getCustName (Object body, IPrimitiveMap header) throws JBranchException {
		INS610InputVO inputVO = (INS610InputVO) body;
		INS610OutputVO outputVO = new INS610OutputVO();
		dam = this.getDataAccessManager();
		QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
		
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT CUST_NAME FROM TBCRM_CUST_MAST WHERE CUST_ID = :cust_id ");
		queryCondition.setObject("cust_id", inputVO.getCustId());
		queryCondition.setQueryString(sb.toString());
		
		outputVO.setResultList(dam.exeQuery(queryCondition));
		
		this.sendRtnObject(outputVO);
	}
	
}
