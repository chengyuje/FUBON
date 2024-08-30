package com.systex.jbranch.app.server.fps.fps814;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
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
import com.systex.jbranch.platform.server.info.XmlInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;
//import com.systex.jbranch.app.common.sfa.table.TBFPS_DOC_FILEVO;

/**
 * 使用理財規劃功能的客戶數
 * @author Johnson
 * @since 18-01-31
 */
@Component("fps814")
@Scope("request")
public class FPS814 extends FubonWmsBizLogic {

    SimpleDateFormat sdf = new SimpleDateFormat("yyMMmmss");
    private Logger logger = LoggerFactory.getLogger(FPS814.class);
    
    /**
     * 根據不同登入角色呼叫不同function
     * @param body
     * @param header
     * @throws JBranchException
     */
    public void dispatcher(Object body, IPrimitiveMap header) throws JBranchException {
		FPS814InputVO inputVO = (FPS814InputVO) body;
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
			case "4":
				inquireForFC(body, header);
				break;
			default:
				break;
		}
    }
    
    /**
     * 取得理專查詢結果
     * @param body
     * @param header
     * @throws JBranchException
     */
    private void inquireForFC(Object body, IPrimitiveMap header) throws JBranchException {
    	FPS814InputVO inputVO = (FPS814InputVO) body;
        FPS814OutputVO outputVO = new FPS814OutputVO();
        DataAccessManager dam = this.getDataAccessManager();
        QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
        StringBuffer sb = new StringBuffer();
        sb.append(" SELECT CUST_NAME, ");
        sb.append(" CUST_ID, ");
        sb.append(" VIP_DEGREE, ");
        sb.append(" COUNT(PLAN_TYPE) AS CNT, ");
        sb.append(" MAX(PLAN_TYPE) AS PLAN_TYPE  ");
        sb.append(" FROM ");
        sb.append("Y".equals(inputVO.getType()) ? " TBFPS_DASHBOARD_YTD_CUST " : "TBFPS_DASHBOARD_MTD_CUST ");
        sb.append(" WHERE 1=1 ");
        sb.append(" AND (REGION_CENTER_ID IN (:rcIdList)) ");
		sb.append(" AND (BRANCH_AREA_ID IN (:opIdList)) ");
		sb.append(" AND (BRANCH_NBR IN (:brNbrList)) ");
        sb.append(" GROUP BY CUST_ID, CUST_NAME, VIP_DEGREE ");
        qc.setObject("rcIdList", getUserVariable(FubonSystemVariableConsts.AVAILREGIONLIST));
		qc.setObject("brNbrList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
		qc.setObject("opIdList", getUserVariable(FubonSystemVariableConsts.AVAILAREALIST));
        qc.setQueryString(sb.toString());
        List<Map<String, Object>> result = dam.exeQuery(qc);
        
        // 開始組裝
        List<Map<String, Object>> vipA = new ArrayList<>();
        List<Map<String, Object>> vipB = new ArrayList<>();
        List<Map<String, Object>> vipV = new ArrayList<>();
        for(Map<String,Object> map : result) {
        	if (StringUtils.isNotBlank((String)map.get("VIP_DEGREE"))) {
        		switch ((String)map.get("VIP_DEGREE")) {
        		case "B":
        			vipB.add(map);
        			break;
        		case "A":
        			vipA.add(map);
        			break;
        		case "V":
        			vipV.add(map);
        			break;
        		default:
        			break;
        		}        		
        	}
        }
        result = new ArrayList<Map<String,Object>>();
        outputVO.setVipA(vipA.size());
        outputVO.setVipB(vipB.size());
        outputVO.setVipV(vipV.size());
        balance(vipA,vipB,vipV);
        
        for(int i=0;i<vipA.size();i++) {
        	Map<String, Object> map = new HashMap<String, Object>();
        	map.put("vipA", vipA.get(i));
        	map.put("vipB", vipB.get(i));
        	map.put("vipV", vipV.get(i));
        	result.add(map);
        }
        outputVO.setOutputList(result);
        this.sendRtnObject(outputVO);
        //rtnQuery(dam, qc, sb, outputVO);
	}

	/**
     * 取得督導查詢結果
     * @param body
     * @param header
     * @throws DAOException 
     * @throws JBranchException
     */
    private void inquireForMbrmgr(Object body, IPrimitiveMap header) throws JBranchException {
    	FPS814InputVO inputVO = (FPS814InputVO) body;
        FPS814OutputVO outputVO = new FPS814OutputVO();
        DataAccessManager dam = this.getDataAccessManager();
        QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
        StringBuffer sb = new StringBuffer();
        sb.append(" SELECT BRANCH_NBR, BRANCH_NAME, ROUND(NVL(CNT_V/NULLIF(CNT_T_V,0),0),2) CNT_V, ");
        sb.append(" ROUND(NVL(CNT_B/NULLIF(CNT_T_B,0),0),2) CNT_B, ");
        sb.append(" ROUND(NVL(CNT_A/NULLIF(CNT_T_A,0),0),2) CNT_A, ");
        sb.append(" ROUND(NVL(CNT_V + CNT_B + CNT_A / NULLIF(CNT_T_V + CNT_T_B + CNT_T_A,0),0),2) TOTAL ");
        sb.append(" FROM ( ");  
        sb.append("   SELECT BRANCH_NBR, BRANCH_NAME, ");
        sb.append("   SUM(CUST_NUM_V) AS CNT_V, SUM(CUST_NUM_A) AS CNT_A, ");
        sb.append("   SUM(CUST_NUM_B) AS CNT_B, SUM(CUST_TNUM_V) AS CNT_T_V, ");
        sb.append("   SUM(CUST_TNUM_A) AS CNT_T_A, SUM(CUST_TNUM_B) AS CNT_T_B ");
        sb.append("   FROM ");
        sb.append("Y".equals(inputVO.getType()) ? " TBFPS_DASHBOARD_YTD " : "TBFPS_DASHBOARD_MTD ");
        sb.append("   WHERE 1=1 ");
        sb.append("   AND (REGION_CENTER_ID IN (:rcIdList)) ");
		sb.append("   AND (BRANCH_AREA_ID IN (:opIdList)) ");
		sb.append("   AND (BRANCH_NBR IN (:brNbrList)) ");
        sb.append("   GROUP BY BRANCH_NBR, BRANCH_NAME ) ");
        sb.append(" ORDER BY BRANCH_NBR, BRANCH_NAME ");
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
        FPS814InputVO inputVO = (FPS814InputVO) body;
        FPS814OutputVO outputVO = new FPS814OutputVO();
        DataAccessManager dam = this.getDataAccessManager();
        QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
        StringBuffer sb = new StringBuffer();
        sb.append(" SELECT BRANCH_AREA_ID, BRANCH_AREA_NAME, ROUND(NVL(CNT_V/NULLIF(CNT_T_V,0),0),2) CNT_V, ");
        sb.append(" ROUND(NVL(CNT_B/NULLIF(CNT_T_B,0),0),2) CNT_B, ");
        sb.append(" ROUND(NVL(CNT_A/NULLIF(CNT_T_A,0),0),2) CNT_A, ");
        sb.append(" ROUND(NVL(CNT_V + CNT_B + CNT_A / NULLIF(CNT_T_V + CNT_T_B + CNT_T_A,0),0),2) TOTAL ");
        sb.append(" FROM ( ");  
        sb.append("   SELECT BRANCH_AREA_ID, BRANCH_AREA_NAME, ");
        sb.append("   SUM(CUST_NUM_V) AS CNT_V, SUM(CUST_NUM_A) AS CNT_A, ");
        sb.append("   SUM(CUST_NUM_B) AS CNT_B, SUM(CUST_TNUM_V) AS CNT_T_V, ");
        sb.append("   SUM(CUST_TNUM_A) AS CNT_T_A, SUM(CUST_TNUM_B) AS CNT_T_B ");
        sb.append("   FROM ");
        sb.append("Y".equals(inputVO.getType()) ? " TBFPS_DASHBOARD_YTD " : "TBFPS_DASHBOARD_MTD ");
        sb.append("   WHERE 1=1 ");
        sb.append("   AND (REGION_CENTER_ID IN (:rcIdList)) ");
		sb.append("   AND (BRANCH_AREA_ID IN (:opIdList)) ");
		sb.append("   AND (BRANCH_NBR IN (:brNbrList)) ");
        sb.append("   GROUP BY BRANCH_AREA_ID, BRANCH_AREA_NAME )");
        sb.append(" ORDER BY BRANCH_AREA_ID, BRANCH_AREA_NAME ");
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
    	FPS814InputVO inputVO = (FPS814InputVO) body;
        FPS814OutputVO outputVO = new FPS814OutputVO();
        DataAccessManager dam = this.getDataAccessManager();
        QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
        StringBuffer sb = new StringBuffer();
        sb.append(" SELECT AO_JOB_RANK, EMP_ID, EMP_NAME, ");
        sb.append(" ROUND(NVL(CNT_V/NULLIF(CNT_T_V,0),0),2) CNT_V, ");
        sb.append(" ROUND(NVL(CNT_B/NULLIF(CNT_T_B,0),0),2) CNT_B, ");
        sb.append(" ROUND(NVL(CNT_A/NULLIF(CNT_T_A,0),0),2) CNT_A, ");
        sb.append(" ROUND(NVL(CNT_V + CNT_B + CNT_A / NULLIF(CNT_T_V + CNT_T_B + CNT_T_A,0),0),2) TOTAL ");
        sb.append(" FROM ( ");
        sb.append("   SELECT MTD.AO_JOB_RANK AO_JOB_RANK, MTD.EMP_ID EMP_ID, MEM.EMP_NAME EMP_NAME, SUM(MTD.CUST_NUM_V) AS CNT_V, SUM(MTD.CUST_NUM_A) AS CNT_A, SUM(MTD.CUST_NUM_B) AS CNT_B, SUM(MTD.CUST_TNUM_V) AS CNT_T_V, SUM(MTD.CUST_TNUM_A) AS CNT_T_A, SUM(MTD.CUST_TNUM_B) AS CNT_T_B");
        sb.append("   FROM ");
        sb.append("Y".equals(inputVO.getType()) ? " TBFPS_DASHBOARD_MTD " : "TBFPS_DASHBOARD_YTD ");
        sb.append(" MTD ");
        sb.append(" LEFT JOIN TBORG_MEMBER MEM ON MEM.EMP_ID = MTD.EMP_ID ");
        sb.append(" WHERE 1=1 ");
        sb.append(" AND (MTD.REGION_CENTER_ID IN (:rcIdList)) ");
		sb.append(" AND (MTD.BRANCH_AREA_ID IN (:opIdList)) ");
		sb.append(" AND (MTD.BRANCH_NBR IN (:brNbrList)) ");
		sb.append(" GROUP BY MTD.AO_JOB_RANK, MTD.EMP_ID, MEM.EMP_NAME ) ");
		sb.append(" ORDER BY AO_JOB_RANK DESC, EMP_NAME ");
		qc.setObject("rcIdList", getUserVariable(FubonSystemVariableConsts.AVAILREGIONLIST));
		qc.setObject("opIdList", getUserVariable(FubonSystemVariableConsts.AVAILAREALIST));
		qc.setObject("brNbrList", getUserVariable(FubonSystemVariableConsts.AVAILBRANCHLIST));
		rtnQuery(dam, qc, sb, outputVO);
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
    			map.put("CUST_NAME", "");
    			list[i].add(map);
    		}
    	}
	}
    
    // 執行查詢回傳
    private void rtnQuery(DataAccessManager dam, QueryConditionIF qc, StringBuffer sb, FPS814OutputVO outputVO)
            throws DAOException, JBranchException {
        qc.setQueryString(sb.toString());
        outputVO.setOutputList(dam.exeQuery(qc));// 回傳資料
        this.sendRtnObject(outputVO);
    }

    // 執行查詢分頁回傳
    private void rtnPaginQuery(DataAccessManager dam, QueryConditionIF qc, StringBuffer sb, FPS350OutputVO outputVO,
            FPS814InputVO inputVO) throws DAOException, JBranchException {
        qc.setQueryString(sb.toString());
        ResultIF pageList = dam.executePaging(qc, inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
        outputVO.setOutputList(pageList);// 回傳資料
        outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());// 當前頁次
        outputVO.setTotalPage(pageList.getTotalPage());// 總頁次
        outputVO.setTotalRecord(pageList.getTotalRecord());// 總筆數
        this.sendRtnObject(outputVO);
    }
    
}