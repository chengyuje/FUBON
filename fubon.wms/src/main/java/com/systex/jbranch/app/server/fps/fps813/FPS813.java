package com.systex.jbranch.app.server.fps.fps813;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.server.fps.fps350.FPS350OutputVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;
//import com.systex.jbranch.app.common.sfa.table.TBFPS_DOC_FILEVO;

/**
 * 使用理財規劃功能的理專數
 * @author Johnson
 * @since 18-01-31
 */
@Component("fps813")
@Scope("request")
public class FPS813 extends FubonWmsBizLogic {

    SimpleDateFormat sdf = new SimpleDateFormat("yyMMmmss");
    private Logger logger = LoggerFactory.getLogger(FPS813.class);
    
    /**
     * 根據不同登入角色呼叫不同function
     * @param body
     * @param header
     * @throws JBranchException
     */
    public void dispatcher(Object body, IPrimitiveMap header) throws JBranchException {
		FPS813InputVO inputVO = (FPS813InputVO) body;
		// roletype -1: 空, 1: 業務處處長,2: 督導,3: 業務主管OR分行個金主管, 4: 理專
		switch (inputVO.getRoleType()){
			case "1":
				inquireForDirector(body, header);
				break;
			case "2":
				inquireForMbrmgr(body, header);
				break;
			case "3":
				inquireForSupervisor(body, header);
				break;
			default:
				break;
		}
    }
    
    /**
     * 取得督導查詢結果
     * @param body
     * @param header
     * @throws DAOException 
     * @throws JBranchException
     */
    private void inquireForMbrmgr(Object body, IPrimitiveMap header) throws JBranchException {
    	FPS813InputVO inputVO = (FPS813InputVO) body;
        FPS813OutputVO outputVO = new FPS813OutputVO();
        DataAccessManager dam = this.getDataAccessManager();
        QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
        StringBuffer sb = new StringBuffer();
        sb.append(" SELECT BRANCH_NBR, BRANCH_NAME,  ");
        sb.append(" CASE   ");
        sb.append(" WHEN NVL(FC1_TNUM,0) + NVL(FC2_TNUM,0) + NVL(FC3_TNUM,0) + NVL(FC4_TNUM,0) + NVL(FC5_TNUM,0) = 0 ");
        sb.append(" THEN 0 ");
        sb.append(" WHEN NVL(FC1_TNUM,0) + NVL(FC2_TNUM,0) + NVL(FC3_TNUM,0) + NVL(FC4_TNUM,0) + NVL(FC5_TNUM,0) > 0 ");  
        sb.append(" THEN ROUND(100*((NVL(FC1_NUM,0) + NVL(FC2_NUM,0) + NVL(FC3_NUM,0) + NVL(FC4_NUM,0) + NVL(FC5_NUM,0)) / (NVL(FC1_TNUM,0) + NVL(FC2_TNUM,0) + NVL(FC3_TNUM,0) + NVL(FC4_TNUM,0) + NVL(FC5_TNUM,0))),2) END as TOTAL, ");
        sb.append(" ROUND(100*NVL(FC1_NUM/NULLIF(FC1_TNUM,0),0),2) FC1_PERCENT, ");
        sb.append(" ROUND(100*NVL(FC2_NUM/NULLIF(FC2_TNUM,0),0),2) FC2_PERCENT, ");
        sb.append(" ROUND(100*NVL(FC3_NUM/NULLIF(FC3_TNUM,0),0),2) FC3_PERCENT, ");
        sb.append(" ROUND(100*NVL(FC4_NUM/NULLIF(FC4_TNUM,0),0),2) FC4_PERCENT, ");
        sb.append(" ROUND(100*NVL(FC5_NUM/NULLIF(FC5_TNUM,0),0),2) FC5_PERCENT ");
        sb.append(" FROM ( ");
        sb.append(" 	SELECT * FROM (  ");
        sb.append("  		SELECT BRANCH_NBR, BRANCH_NAME, AO_JOB_RANK,  ");
        sb.append("  		(NVL(CUST_NUM_V, 0)+ NVL(CUST_NUM_A, 0) + NVL(CUST_NUM_B, 0)) NUM, ");
        sb.append("  		(NVL(CUST_TNUM_V, 0) + NVL(CUST_TNUM_A, 0) + NVL(CUST_TNUM_B, 0)) TNUM ");
        sb.append("  		from ");
        sb.append((inputVO.getType().equals("Y") ? " TBFPS_DASHBOARD_YTD " : " TBFPS_DASHBOARD_MTD " ));
        sb.append("  		WHERE 1=1 ");
        sb.append(" 		AND (REGION_CENTER_ID IN (:rcIdList)) ");
		sb.append(" 		AND (BRANCH_AREA_ID IN (:opIdList)) ");
		sb.append(" 		AND (BRANCH_NBR IN (:brNbrList)) ");
        sb.append("  	) ");
        sb.append("  	PIVOT ( ");
        sb.append("  		SUM(NUM)NUM , SUM(TNUM)TNUM ");
        sb.append("  		FOR AO_JOB_RANK IN ('FC1' AS FC1, 'FC2' AS FC2, 'FC3' AS FC3, 'FC4' AS FC4, 'FC5' AS FC5) ");
        sb.append("  	) ");
        sb.append("	) ");
        sb.append(" ORDER BY BRANCH_NBR ");
        qc.setObject("rcIdList", getUserVariable(FubonSystemVariableConsts.AVAILREGIONLIST));
		qc.setObject("brNbrList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
		qc.setObject("opIdList", getUserVariable(FubonSystemVariableConsts.AVAILAREALIST));
        rtnQuery(dam, qc, sb, outputVO);
        //rtnPaginQuery(dam, qc, sb, outputVO, inputVO);
	}

	/**
     * 取得業務處處長查詢結果
     * @param body
     * @param header
     * @throws JBranchException
     */
    private void inquireForDirector(Object body, IPrimitiveMap header) throws JBranchException {
        FPS813InputVO inputVO = (FPS813InputVO) body;
        FPS813OutputVO outputVO = new FPS813OutputVO();
        DataAccessManager dam = this.getDataAccessManager();
        QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
        StringBuffer sb = new StringBuffer();
        sb.append(" SELECT BRANCH_AREA_ID, BRANCH_AREA_NAME, ");
        sb.append(" CASE WHEN NVL(FC1_TNUM,0) + NVL(FC2_TNUM,0) + NVL(FC3_TNUM,0) + NVL(FC4_TNUM,0) + NVL(FC5_TNUM,0) = 0 ");
        sb.append(" THEN 0 ");
        sb.append(" WHEN NVL(FC1_TNUM,0) + NVL(FC2_TNUM,0) + NVL(FC3_TNUM,0) + NVL(FC4_TNUM,0) + NVL(FC5_TNUM,0) > 0 ");
        sb.append(" THEN ROUND(100*((NVL(FC1_NUM,0) + NVL(FC2_NUM,0) + NVL(FC3_NUM,0) + NVL(FC4_NUM,0) + NVL(FC5_NUM,0)) / (NVL(FC1_TNUM,0) + NVL(FC2_TNUM,0) + NVL(FC3_TNUM,0) + NVL(FC4_TNUM,0) + NVL(FC5_TNUM,0))),2) ");  
        sb.append(" END as TOTAL, ");
        sb.append(" ROUND(100*NVL(FC1_NUM/NULLIF(FC1_TNUM,0),0),2) FC1_PERCENT, ");
        sb.append(" ROUND(100*NVL(FC2_NUM/NULLIF(FC2_TNUM,0),0),2) FC2_PERCENT, ");
        sb.append(" ROUND(100*NVL(FC3_NUM/NULLIF(FC3_TNUM,0),0),2) FC3_PERCENT, ");
        sb.append(" ROUND(100*NVL(FC4_NUM/NULLIF(FC4_TNUM,0),0),2) FC4_PERCENT, ");
        sb.append(" ROUND(100*NVL(FC5_NUM/NULLIF(FC5_TNUM,0),0),2) FC5_PERCENT ");
        sb.append(" FROM ( ");
        sb.append(" 	SELECT * FROM (  ");
        sb.append("  		SELECT BRANCH_AREA_ID, BRANCH_AREA_NAME, AO_JOB_RANK, ");
        sb.append("  		(NVL(CUST_NUM_V, 0)+ NVL(CUST_NUM_A, 0) + NVL(CUST_NUM_B, 0)) NUM, ");
        sb.append("  		(NVL(CUST_TNUM_V, 0) + NVL(CUST_TNUM_A, 0) + NVL(CUST_TNUM_B, 0)) TNUM ");
        sb.append("  		from ");
        sb.append((inputVO.getType().equals("Y") ? " TBFPS_DASHBOARD_YTD " : " TBFPS_DASHBOARD_MTD " ));
        sb.append("  		WHERE 1=1 ");
        sb.append(" 		AND (REGION_CENTER_ID IN (:rcIdList)) ");
		sb.append(" 		AND (BRANCH_AREA_ID IN (:opIdList)) ");
		sb.append(" 		AND (BRANCH_NBR IN (:brNbrList)) ");
        sb.append("  	) ");
        sb.append("  	PIVOT ( ");
        sb.append("  		SUM(NUM)NUM , SUM(TNUM)TNUM ");
        sb.append("  		FOR AO_JOB_RANK IN ('FC1' AS FC1, 'FC2' AS FC2, 'FC3' AS FC3, 'FC4' AS FC4, 'FC5' AS FC5) ");
        sb.append("  	) ");
        sb.append("	) ");
        sb.append(" ORDER BY BRANCH_AREA_ID ");
        qc.setObject("rcIdList", getUserVariable(FubonSystemVariableConsts.AVAILREGIONLIST));
		qc.setObject("brNbrList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
		qc.setObject("opIdList", getUserVariable(FubonSystemVariableConsts.AVAILAREALIST));
        rtnQuery(dam, qc, sb, outputVO);
        //rtnPaginQuery(dam, qc, sb, outputVO, inputVO);
    }
    
    /** 
     * 取得分行經理&業務主管查詢結果
     * @param body
     * @param header
     * @throws DAOException 
     * @throws JBranchException
     */
    private void inquireForSupervisor(Object body, IPrimitiveMap header) throws JBranchException {
    	FPS813InputVO inputVO = (FPS813InputVO) body;
        FPS813OutputVO outputVO = new FPS813OutputVO();
        DataAccessManager dam = this.getDataAccessManager();
        QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
        StringBuffer sb = new StringBuffer();
        sb.append(" SELECT MTD.EMP_ID, MEM.EMP_NAME , ");
        sb.append(" MTD.ao_job_rank, ");
        sb.append(" SUM(MTD.CUST_NUM_V + MTD.CUST_NUM_A + MTD.CUST_NUM_B) AS NUM, ");
        sb.append(" SUM(MTD.CUST_TNUM_V + MTD.CUST_TNUM_A + MTD.CUST_TNUM_B) AS TNUM ");
        sb.append(" FROM ");
        sb.append("Y".equals(inputVO.getType()) ? " TBFPS_DASHBOARD_YTD " : " TBFPS_DASHBOARD_MTD ");
        sb.append(" MTD ");
        sb.append(" LEFT JOIN TBORG_MEMBER MEM ON MEM.EMP_ID = MTD.EMP_ID ");
        sb.append(" WHERE 1=1 ");
		sb.append(" AND (MTD.REGION_CENTER_ID IN (:rcIdList)) ");
		sb.append(" AND (MTD.BRANCH_AREA_ID IN (:opIdList)) ");
		sb.append(" AND (MTD.BRANCH_NBR IN (:brNbrList)) ");
		sb.append(" group by MTD.ao_job_rank, MTD.EMP_ID, MEM.EMP_NAME ");
		qc.setObject("rcIdList", getUserVariable(FubonSystemVariableConsts.AVAILREGIONLIST));
		qc.setObject("opIdList", getUserVariable(FubonSystemVariableConsts.AVAILAREALIST));
		qc.setObject("brNbrList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
		//sb.append(" order by MTD.ao_job_rank, MTD.EMP_ID, MEM.EMP_NAME ");
		// 開始封裝
		qc.setQueryString(sb.toString());
		List<Map<String, Object>> result = dam.exeQuery(qc);
		List<Map<String, Object>> fc1 = new ArrayList<Map<String,Object>>();
		List<Map<String, Object>> fc2 = new ArrayList<Map<String,Object>>();
		List<Map<String, Object>> fc3 = new ArrayList<Map<String,Object>>();
		List<Map<String, Object>> fc4 = new ArrayList<Map<String,Object>>();
		List<Map<String, Object>> fc5 = new ArrayList<Map<String,Object>>();
		for(Map map : result) {
			switch((String)map.get("AO_JOB_RANK")) {
				case "FC1":
					fc1.add(map);
					break;
				case "FC2":
					fc2.add(map);
					break;
				case "FC3":
					fc3.add(map);
					break;
				case "FC4":
					fc4.add(map);
					break;
				case "FC5":
					fc5.add(map);
					break;
				default:
					break;
			}
		}
		balance(fc1,fc2,fc3,fc4,fc5);
		result = new ArrayList<Map<String,Object>>();
		for(int i=0;i<fc1.size();i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("FC1", fc1.get(i).get("EMP_NAME"));
			map.put("FC2", fc2.get(i).get("EMP_NAME"));
			map.put("FC3", fc3.get(i).get("EMP_NAME"));
			map.put("FC4", fc4.get(i).get("EMP_NAME"));
			map.put("FC5", fc5.get(i).get("EMP_NAME"));
			result.add(map);
		}
		outputVO.setOutputList(result);
		this.sendRtnObject(outputVO);
	}
    
    // 讓進來的list size一樣
    private void balance(List<Map<String, Object>>... list) {
		int len = Integer.MIN_VALUE;
    	for(List<Map<String,Object>> tmp : list) {
    		len = (len>tmp.size()) ? len : tmp.size();
    	}
    	for(int i=0;i<list.length;i++) {
    		while(list[i].size()<len) {
    			Map<String, Object> map = new HashMap<String, Object>();
    			map.put("EMP_NAME", "");
    			list[i].add(map);
    		}
    	}
	}
    
    // 執行查詢回傳
    private void rtnQuery(DataAccessManager dam, QueryConditionIF qc, StringBuffer sb, FPS813OutputVO outputVO)
            throws DAOException, JBranchException {
        qc.setQueryString(sb.toString());
        outputVO.setOutputList(dam.exeQuery(qc));// 回傳資料
        this.sendRtnObject(outputVO);
    }

    // 執行查詢分頁回傳
    private void rtnPaginQuery(DataAccessManager dam, QueryConditionIF qc, StringBuffer sb, FPS350OutputVO outputVO,
            FPS813InputVO inputVO) throws DAOException, JBranchException {
        qc.setQueryString(sb.toString());
        ResultIF pageList = dam.executePaging(qc, inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
        outputVO.setOutputList(pageList);// 回傳資料
        outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());// 當前頁次
        outputVO.setTotalPage(pageList.getTotalPage());// 總頁次
        outputVO.setTotalRecord(pageList.getTotalRecord());// 總筆數
        this.sendRtnObject(outputVO);
    }
    
}