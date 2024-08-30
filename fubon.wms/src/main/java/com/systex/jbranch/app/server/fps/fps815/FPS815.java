package com.systex.jbranch.app.server.fps.fps815;

import java.text.SimpleDateFormat;

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
import com.systex.jbranch.platform.util.IPrimitiveMap;
//import com.systex.jbranch.app.common.sfa.table.TBFPS_DOC_FILEVO;

/**
 * 使用理財規劃金額明細
 * @author Johnson
 * @since 18-01-31
 */
@Component("fps815")
@Scope("request")
public class FPS815 extends FubonWmsBizLogic {

    SimpleDateFormat sdf = new SimpleDateFormat("yyMMmmss");
    private Logger logger = LoggerFactory.getLogger(FPS815.class);
    
    /**
     * 根據不同登入角色呼叫不同function
     * @param body
     * @param header
     * @throws JBranchException
     */
    public void dispatcher(Object body, IPrimitiveMap header) throws JBranchException {
		FPS815InputVO inputVO = (FPS815InputVO) body;
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
    	FPS815InputVO inputVO = (FPS815InputVO) body;
        FPS815OutputVO outputVO = new FPS815OutputVO();
        DataAccessManager dam = this.getDataAccessManager();
        QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
        StringBuffer sb = new StringBuffer();
        sb.append(" SELECT BRANCH_NBR, BRANCH_NAME, N_PCH, PCH, N_PCH+PCH AS TOTAL, FEE FROM ( ");
        sb.append("   SELECT BRANCH_NBR, BRANCH_NAME, SUM(N_PCH_AMOUNT) AS N_PCH, SUM(PCH_AMOUNT) AS PCH, SUM(PCH_FEE) AS FEE ");
        sb.append("   FROM ");
        sb.append("Y".equals(inputVO.getType()) ? " TBFPS_DASHBOARD_YTD " : " TBFPS_DASHBOARD_MTD ");
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
        FPS815InputVO inputVO = (FPS815InputVO) body;
        FPS815OutputVO outputVO = new FPS815OutputVO();
        DataAccessManager dam = this.getDataAccessManager();
        QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
        StringBuffer sb = new StringBuffer();
        sb.append(" SELECT BRANCH_AREA_ID, BRANCH_AREA_NAME, N_PCH, PCH, N_PCH+PCH AS TOTAL, FEE FROM ( ");
        sb.append(" 	SELECT BRANCH_AREA_ID, BRANCH_AREA_NAME, ");
        sb.append(" 	SUM(N_PCH_AMOUNT) AS N_PCH, SUM(PCH_AMOUNT) AS PCH, SUM(PCH_FEE) AS FEE ");
        sb.append(" 	FROM ");
        sb.append("Y".equals(inputVO.getType()) ? " TBFPS_DASHBOARD_YTD " : " TBFPS_DASHBOARD_MTD ");
        sb.append(" 	WHERE 1=1  ");  
        sb.append(" 	AND (REGION_CENTER_ID IN (:rcIdList)) ");
		sb.append(" 	AND (BRANCH_AREA_ID IN (:opIdList)) ");
		sb.append(" 	AND (BRANCH_NBR IN (:brNbrList)) ");
        sb.append(" 	GROUP BY BRANCH_AREA_ID, BRANCH_AREA_NAME ) ");
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
    	FPS815InputVO inputVO = (FPS815InputVO) body;
        FPS815OutputVO outputVO = new FPS815OutputVO();
        DataAccessManager dam = this.getDataAccessManager();
        QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
        StringBuffer sb = new StringBuffer();
        sb.append(" SELECT AO_JOB_RANK, EMP_ID, EMP_NAME, N_PCH, PCH, N_PCH+PCH AS TOTAL, FEE FROM ( ");
        sb.append("   SELECT MTD.AO_JOB_RANK, MTD.EMP_ID, MEM.EMP_NAME, SUM(MTD.N_PCH_AMOUNT) N_PCH, SUM(MTD.PCH_AMOUNT) PCH, SUM(MTD.PCH_FEE) FEE ");
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
    
    // 執行查詢回傳
    private void rtnQuery(DataAccessManager dam, QueryConditionIF qc, StringBuffer sb, FPS815OutputVO outputVO)
            throws DAOException, JBranchException {
        qc.setQueryString(sb.toString());
        outputVO.setOutputList(dam.exeQuery(qc));// 回傳資料
        this.sendRtnObject(outputVO);
    }

    // 執行查詢分頁回傳
    private void rtnPaginQuery(DataAccessManager dam, QueryConditionIF qc, StringBuffer sb, FPS350OutputVO outputVO,
            FPS815InputVO inputVO) throws DAOException, JBranchException {
        qc.setQueryString(sb.toString());
        ResultIF pageList = dam.executePaging(qc, inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
        outputVO.setOutputList(pageList);// 回傳資料
        outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());// 當前頁次
        outputVO.setTotalPage(pageList.getTotalPage());// 總頁次
        outputVO.setTotalRecord(pageList.getTotalRecord());// 總筆數
        this.sendRtnObject(outputVO);
    }
    
}