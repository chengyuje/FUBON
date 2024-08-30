package com.systex.jbranch.app.server.fps.pms203;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.app.common.fps.table.TBPMS_AO_TRC_TAR_MPK;
import com.systex.jbranch.app.common.fps.table.TBPMS_AO_TRC_TAR_MVO;
import com.systex.jbranch.app.common.fps.table.TBPMS_BR_PRD_TAR_MPK;
import com.systex.jbranch.app.common.fps.table.TBPMS_BR_PRD_TAR_MVO;
import com.systex.jbranch.app.common.fps.table.TBPMS_PRO_FUN_TR_PROD_SETPK;
import com.systex.jbranch.app.common.fps.table.TBPMS_PRO_FUN_TR_PROD_SETVO;
import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.CSVUtil;
import com.systex.jbranch.platform.common.util.StringUtil;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;

/**
 * Copy Right Information : <br>
 * Project : fubon.wms<br>
 * JDK version used : JDK1.7.0 <br>
 * Description :<br>
 * Comments Name : PMS203.java<br>
 * Author : frank<br>
 * Date :2016年05月17日 <br>
 * Version : 1.0 <br>
 * Editor : KevinHsu<br>
 * Editor Date : 2017年01月12日<br>
 */

@Component("pms203")
@Scope("request")
public class PMS203 extends FubonWmsBizLogic {
    private Logger logger = LoggerFactory.getLogger(PMS203.class);
    private static String finalstring;

    /**
     * ===== 查詢資料 =====
     **/
    public void queryData(Object body, IPrimitiveMap header)
            throws JBranchException {
        PMS203InputVO  inputVO  = (PMS203InputVO) body;
        PMS203OutputVO outputVO = new PMS203OutputVO();
        
        DataAccessManager dam       = this.getDataAccessManager();
        QueryConditionIF  condition = dam.getQueryCondition();
        ArrayList<String> sql_list  = new ArrayList<String>();
        
        
        StringBuffer sql = new StringBuffer("select ROWNUM, t.* from ( ");

        // ==== 查詢 「理專職級設定」====
        if (inputVO.getTgtType().equals("SET")) {
            sql.append(" select ");
            sql.append("    a.DATA_YEARMON, a.LEVEL_DISTANCE, ");
            sql.append("    a.OL_TITLE, a.JOB_TITLE_ID, ");
            sql.append("    b.PARAM_NAME as OL_TITLE_N, c.PARAM_NAME as JOB_TITLE_ID_N,   ");
            sql.append("    a.PRODUCT_GOALS, a.KEEP_GOALS, a.ADVAN_GOALS, a.PRO_STR_LINE, ");
            sql.append("    a.PRO_HOR_LINE, a.PRO_SLA_LINE, a.DEM_STR_LINE, ");
            sql.append("    a.DEM_HOR_LINE, a.DEM_SLA_LINE, a.FIX_SAL       ");
            sql.append(" from TBPMS_PRO_FUN_TR_PROD_SET a ");
            sql.append("    left join TBSYSPARAMETER b    ");
            sql.append("        on b.PARAM_CODE = a.OL_TITLE AND b.PARAM_TYPE = 'PMS.RANK_TITLE'      ");
            sql.append("    left join TBSYSPARAMETER c ");
            sql.append("        on c.PARAM_CODE = a.JOB_TITLE_ID AND c.PARAM_TYPE = 'PMS.AO_JOB_RANK' ");
            sql.append(" where 1=1 ");
            // 資料統計月份
            if (inputVO.getReportDate() != null
                    && !inputVO.getReportDate().equals("")) {
                sql.append(" and a.DATA_YEARMON = ? ");
                sql_list.add(inputVO.getReportDate());
            }

            sql.append(" order by a.LEVEL_DISTANCE) t ");
        }

        // ===== 查詢 「理專生產力目標」======
        else if (inputVO.getTgtType().equals("TAR")) {
            sql.append(" select ");
            sql.append("    DATA_YEARMON, REGION_CENTER_ID, REGION_CENTER_NAME,    ");
            sql.append("    BRANCH_AREA_ID, BRANCH_AREA_NAME, BRANCH_NBR, AO_CODE, ");
            sql.append("    BRANCH_NAME, TAR.EMP_ID, TAR.EMP_NAME, JOB_TITLE_ID,   ");
            sql.append("    to_char(FIRST_DATE, 'YYYY-MM-DD') as FIRST_DATE,       ");
            sql.append("    JOB_TITLE, TAR_AMOUNT, ");
            sql.append("    to_char(MAINTAIN_DATE, 'YYYY/MM/DD') as MAINTAIN_DATE, ");
            sql.append("    MAINTAIN_NAME||'-'||NVL(MEMBER.EMP_NAME, 'SYSTEM') as MAINTAIN_NAME ");
            sql.append(" from TBPMS_EMP_PRD_TAR_M TAR");
            sql.append("    left join TBORG_MEMBER MEMBER on MEMBER.EMP_ID = TAR.MAINTAIN_NAME ");
            sql.append(" where 1=1 ");
            
            // 資料統計月份
            if (inputVO.getReportDate() != null
                    && !inputVO.getReportDate().equals("")) {
                sql.append(" and DATA_YEARMON = ? ");
                sql_list.add(inputVO.getReportDate());
            }
            
            // 分行
            if (inputVO.getBranch_nbr() != null
                    && !inputVO.getBranch_nbr().equals("")) {
                sql.append(" and BRANCH_NBR = ? ");
                sql_list.add(inputVO.getBranch_nbr());
            }

            if (inputVO.getEmp_id() != null
                    && !inputVO.getEmp_id().equals("")) {
                sql.append(" and TAR.EMP_ID = ? ");
                sql_list.add(inputVO.getEmp_id());
            }

            sql.append(" order by  BRANCH_NBR, MAINTAIN_DATE desc) t ");
        }

        // ==== 查詢 「分行投保計績收益目標」=====
        else if (inputVO.getTgtType().equals("INS")) {
            sql.append(" select ");
            sql.append("    DATA_YEARMON, REGION_CENTER_ID, REGION_CENTER_NAME,");
            sql.append("    BRANCH_AREA_ID, BRANCH_AREA_NAME, BRANCH_NBR, BRANCH_NAME, ");
            sql.append("    INV_TAR_AMOUNT, INS_TAR_AMOUNT, TOT_TAR_AMOUNT, EXCHG_TAR_AMOUNT, ");
            sql.append("    to_char(MAINTAIN_DATE, 'YYYY/MM/DD') as MAINTAIN_DATE, MAINTAIN_NAME ");
            sql.append(" from TBPMS_BR_PRD_TAR_M ");
            sql.append(" where 1=1 ");
            // 資料統計月份
            if (inputVO.getReportDate() != null
                    && !inputVO.getReportDate().equals("")) {
                sql.append(" and DATA_YEARMON = ? ");
                sql_list.add(inputVO.getReportDate());
            }
            // 分行
            if (inputVO.getBranch_nbr() != null
                    && !inputVO.getBranch_nbr().equals("")) {
                sql.append(" and BRANCH_NBR = ? ");
                sql_list.add(inputVO.getBranch_nbr());
            }
            sql.append(" order by REGION_CENTER_ID, BRANCH_NBR) t ");
        }

        // ==== 查詢 「分行投保計績銷量目標」=====
        else if (inputVO.getTgtType().equals("SALE")) {
            sql.append(" select ");
            sql.append("     DATA_YEARMON, REGION_CENTER_ID, REGION_CENTER_NAME,");
            sql.append("	 BRANCH_AREA_ID, BRANCH_AREA_NAME, BRANCH_NBR, BRANCH_NAME, ");
            sql.append("	 INV_TAR_AMOUNT, INS_TAR_AMOUNT, TOT_TAR_AMOUNT, ");
            sql.append("	 to_char(MAINTAIN_DATE, 'YYYY/MM/DD') as MAINTAIN_DATE, MAINTAIN_NAME ");
            sql.append(" from TBPMS_BR_SALE_TAR_M ");
            sql.append(" where 1=1 ");
            // 資料統計月份
            if (inputVO.getReportDate() != null
                    && !inputVO.getReportDate().equals("")) {
                sql.append(" and DATA_YEARMON = ? ");
                sql_list.add(inputVO.getReportDate());
            }
            // 分行
            if (inputVO.getBranch_nbr() != null
                    && !inputVO.getBranch_nbr().equals("")) {
                sql.append(" and BRANCH_NBR = ? ");
                sql_list.add(inputVO.getBranch_nbr());
            }
            sql.append(" order by REGION_CENTER_ID, BRANCH_NBR) t ");
        }

        // ==== 查詢「理專追蹤商品目標」
        else if (inputVO.getTgtType().equals("PRD")) {
            sql.append(" select PRD.YEARMON AS DATA_YEARMON, PRD.REGION_CENTER_ID, PRD.REGION_CENTER_NAME,");
            sql.append("    PRD.BRANCH_AREA_ID, PRD.BRANCH_AREA_NAME, PRD.BRANCH_NBR, PRD.BRANCH_NAME, ");
            sql.append("    PRD.AO_CODE, PRD.EMP_ID, EMP.EMP_NAME, PRD.AO_JOB_RANK as JOB_TITLE_ID_N, ");
            sql.append("    PRD.MAIN_PRD_ID, PRD.MAIN_PRD_NAME, PRD.TAR_AMT, ");
            sql.append("    to_char(PRD.MAINTAIN_DATE, 'YYYY/MM/DD') as MAINTAIN_DATE, PRD.MAINTAIN_NAME ");
            sql.append(" from TBPMS_AO_TRC_TAR_M PRD ");
            sql.append("    left join VWORG_BRANCH_EMP_DETAIL_INFO EMP on PRD.EMP_ID = EMP.EMP_ID  ");
            sql.append("        and PRD.AO_CODE = EMP.AO_CODE ");
            sql.append(" where 1=1 ");

            // 資料統計月份
            if (inputVO.getReportDate() != null
                    && !inputVO.getReportDate().equals("")) {
                sql.append(" and PRD.YEARMON = ? ");
                sql_list.add(inputVO.getReportDate());
            }

            // 分行
            if (inputVO.getBranch_nbr() != null
                    && !inputVO.getBranch_nbr().equals("")) {
                sql.append(" and PRD.BRANCH_NBR = ? ");
                sql_list.add(inputVO.getBranch_nbr());
            }

            // 理專員編
            if (inputVO.getAo_code() != null
                    && !inputVO.getAo_code().equals("")) {
                sql.append(" and  PRD.EMP_ID= ( select emp.EMP_ID from TBPMS_SALES_AOCODE_REC emp where AO_CODE= ? ");
                sql.append(" and  last_day(TO_DATE(PRD.DATA_YEARMON||'01','YYYYMMDD')) between start_time and end_time ) ");
                sql_list.add(inputVO.getAo_code());
            }
            
            sql.append(" order by PRD.BRANCH_NBR, PRD.AO_CODE ) t ");
        }

        condition.setQueryString(sql.toString());
        
        for (int sql_i = 0; sql_i < sql_list.size(); sql_i++) {
            condition.setString(sql_i + 1, sql_list.get(sql_i));
        }
        
        outputVO.setTotalList(dam.exeQuery(condition));
        ResultIF list = dam.executePaging(condition,
                inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());
        if (list.size() > 0) {
            int totalPage = list.getTotalPage();
            outputVO.setTotalPage(totalPage);
            outputVO.setResultList(list);
            outputVO.setTotalRecord(list.getTotalRecord());
            outputVO.setCurrentPageIndex(inputVO.getCurrentPageIndex());
            
            sendRtnObject(outputVO);
        } else {
            throw new APException("ehl_01_common_009");
        }
    }

    /**
     * ===== 新增「理專職級設定」資料 =====
     **/

    public void insertSET(Object body, IPrimitiveMap header)
            throws JBranchException {
        PMS203InputVO inputVO = (PMS203InputVO) body;
        DataAccessManager dam = this.getDataAccessManager();
        QueryConditionIF condition = dam.getQueryCondition();
        
        
        String loginID = (String) getUserVariable(FubonSystemVariableConsts.LOGINID);
        
        StringBuffer sql = new StringBuffer();
        sql.append("INSERT INTO TBPMS_PRO_FUN_TR_PROD_SET ( ");
        sql.append("    DATA_YEARMON, LEVEL_DISTANCE, OL_TITLE, JOB_TITLE_ID, ");
        sql.append("    PRODUCT_GOALS, KEEP_GOALS, ADVAN_GOALS, ");
        sql.append("    PRO_STR_LINE, PRO_HOR_LINE, PRO_SLA_LINE, ");
        sql.append("    DEM_STR_LINE, DEM_HOR_LINE, DEM_SLA_LINE,  ");
        sql.append("    FIX_SAL, ");
        sql.append("    CREATETIME, CREATOR, MODIFIER, LASTUPDATE ");
        sql.append("   ) ");
        sql.append("VALUES ( ");
        sql.append("'" + inputVO.getReportDate() + "',  ");
        sql.append(inputVO.getLevelDist() + ", '" + inputVO.getOlTitle());
        sql.append("', '" + inputVO.getJobTitleId() + "', ");
        sql.append(inputVO.getProdGoals() + ", " + inputVO.getKeepGoals());
        sql.append(", " + inputVO.getAdvanGoals() + ", ");
        sql.append(inputVO.getProStrLine() + ", " + inputVO.getProHorLine());
        sql.append(", " + inputVO.getProSlaLine() + ", ");
        sql.append(inputVO.getDemStrLine() + ", " + inputVO.getDemHorLine());
        sql.append(", " + inputVO.getDemSlaLine() + ", ");
        sql.append(inputVO.getFixSal() + ", ");
        sql.append("SYSDATE, '" + loginID + "', '" + loginID + "',");
        sql.append(" SYSDATE) ");

        condition.setQueryString(sql.toString());
        try {
            dam.exeUpdate(condition);
        } catch (JBranchException e) {
            throw new APException("");
        }

        this.sendRtnObject(null);
    }

    /**
     * ===== 新增「理專生產力目標」資料 =====
     **/
    public void insertTAR(Object body, IPrimitiveMap header)
            throws JBranchException {
        PMS203InputVO inputVO = (PMS203InputVO) body;
        DataAccessManager dam = this.getDataAccessManager();
        String loginID = (String) getUserVariable(FubonSystemVariableConsts.LOGINID);

        QueryConditionIF condition = dam.getQueryCondition();
        StringBuffer sqlc = new StringBuffer();
        sqlc.append(" SELECT * FROM TBPMS_EMP_PRD_TAR_M ");
        sqlc.append(" WHERE DATA_YEARMON = '" + inputVO.getReportDate() + "'");
        sqlc.append("   AND EMP_ID ='" + inputVO.getEmp_id() + "'");
        //sqlc.append("   AND AO_CODE ='" + inputVO.getAo_code() + "'");
        condition.setQueryString(sqlc.toString());

        if (dam.exeQuery(condition).size() > 0) {
            this.sendRtnObject("duplicate");
            return;
        }

        StringBuffer sql = new StringBuffer();
        sql.append(" INSERT INTO TBPMS_EMP_PRD_TAR_M ( ");
        sql.append(" 	DATA_YEARMON, REGION_CENTER_ID, ");
        sql.append("    BRANCH_AREA_ID, BRANCH_NBR, EMP_ID, REGION_CENTER_NAME, ");
        sql.append("    BRANCH_AREA_NAME, BRANCH_NAME, AO_CODE, EMP_NAME, ");
        sql.append("    JOB_TITLE_ID, FIRST_DATE, JOB_TITLE, TAR_AMOUNT, ");
        sql.append("    MAINTAIN_DATE, MAINTAIN_NAME, CREATETIME, CREATOR, ");
        sql.append("    MODIFIER, LASTUPDATE ");
        sql.append("  ) ");
        sql.append(" VALUES ( ");
        sql.append("'" + inputVO.getReportDate() + "','" + inputVO.getRegion_center_id() + "','");
        sql.append(inputVO.getBranch_area_id() + "','" + inputVO.getBranch_nbr() + "','");
        sql.append(inputVO.getEmp_id() + "','" + inputVO.getRc_name() + "','");
        sql.append(inputVO.getOp_name() + "','" + inputVO.getBr_name() + "','");
        sql.append(inputVO.getAo_code() + "','" + inputVO.getEmp_name() + "','");
        sql.append(inputVO.getJobTitleId() + "', TO_DATE('" + inputVO.getFirstDate() + "', 'YYYY-MM-DD'), '");
        sql.append(inputVO.getJobTitle() + "'," + inputVO.getTarAmount() + ",");
        sql.append("SYSDATE, '" + loginID + "', SYSDATE, '");
        sql.append(loginID + "', '" + loginID + "'," + " SYSDATE) ");

        condition.setQueryString(sql.toString());

        try {
            dam.exeUpdate(condition);
        } catch (JBranchException e) {
            throw new APException("");
        }

        this.sendRtnObject(null);
    }

    /**
     * ===== 新增「分行投保計績收益目標」資料 =====
     **/
    public void insertINS(Object body, IPrimitiveMap header)
            throws JBranchException {
        PMS203InputVO inputVO = (PMS203InputVO) body;
        DataAccessManager dam = this.getDataAccessManager();
        String loginID = (String) getUserVariable(FubonSystemVariableConsts.LOGINID);
        Timestamp currentTM = new Timestamp(System.currentTimeMillis());
        QueryConditionIF condition = dam.getQueryCondition();

        StringBuffer sqlc = new StringBuffer();
        sqlc.append(" SELECT * FROM TBPMS_BR_PRD_TAR_M ");
        sqlc.append(" WHERE DATA_YEARMON = '" + inputVO.getReportDate() + "'");
        sqlc.append("   AND BRANCH_NBR ='" + inputVO.getBranch_nbr() + "'");
        condition.setQueryString(sqlc.toString());
        int size = dam.exeQuery(condition).size();

        if (size == 0) {
            StringBuffer sql = new StringBuffer();
            sql.append(" INSERT INTO TBPMS_BR_PRD_TAR_M ( ");
            sql.append("    DATA_YEARMON, REGION_CENTER_ID, BRANCH_AREA_ID, ");
            sql.append("    BRANCH_NBR, REGION_CENTER_NAME, BRANCH_AREA_NAME, ");
            sql.append("    BRANCH_NAME, INV_TAR_AMOUNT, INS_TAR_AMOUNT, ");
            sql.append("    TOT_TAR_AMOUNT, MAINTAIN_DATE, MAINTAIN_NAME, ");
            sql.append("    CREATETIME, CREATOR, MODIFIER, LASTUPDATE ,EXCHG_TAR_AMOUNT) ");
            sql.append(" VALUES ( ");
            sql.append("'" + inputVO.getReportDate() + "', '" + inputVO.getRegion_center_id() + "', '");
            sql.append(inputVO.getBranch_area_id() + "', '" + inputVO.getBranch_nbr() + "', '");
            sql.append(inputVO.getRc_name() + "', '" + inputVO.getOp_name() + "', '");
            sql.append(inputVO.getBr_name() + "', " + inputVO.getInvTarAmount() + ", ");
            sql.append(inputVO.getInsTarAmount() + ", " + inputVO.getTotTarAmount() + ", ");
            sql.append("SYSDATE, '" + loginID + "', SYSDATE, '" + loginID + "', '");
            sql.append(loginID + "', SYSDATE , '" + inputVO.getExchgTarAmount() + "' ) ");

            condition.setQueryString(sql.toString());

            try {
                dam.exeUpdate(condition);
            } catch (JBranchException e) {
                throw new APException("");
            }

            this.sendRtnObject(null);
        } else {
            this.sendRtnObject("duplicate");
            return;
        }
    }

    /**
     * ===== 新增「分行投保計績銷量目標」資料 =====
     **/
    public void insertSALE(Object body, IPrimitiveMap header)
            throws JBranchException {
        PMS203InputVO inputVO = (PMS203InputVO) body;
        DataAccessManager dam = this.getDataAccessManager();
        QueryConditionIF condition = dam.getQueryCondition();
        
        String loginID = (String) getUserVariable(FubonSystemVariableConsts.LOGINID);
        Timestamp currentTM = new Timestamp(System.currentTimeMillis());
        

        StringBuffer sqlc = new StringBuffer();
        sqlc.append(" SELECT * FROM TBPMS_BR_SALE_TAR_M ");
        sqlc.append(" WHERE DATA_YEARMON = '" + inputVO.getReportDate() + "'");
        sqlc.append("   AND BRANCH_NBR ='" + inputVO.getBranch_nbr() + "'");
        condition.setQueryString(sqlc.toString());
        int size = dam.exeQuery(condition).size();

        if (size == 0) {
            StringBuffer sql = new StringBuffer();
            sql.append(" INSERT INTO TBPMS_BR_SALE_TAR_M ( ");
            sql.append("    DATA_YEARMON, REGION_CENTER_ID, BRANCH_AREA_ID, ");
            sql.append("    BRANCH_NBR, REGION_CENTER_NAME, BRANCH_AREA_NAME, ");
            sql.append("    BRANCH_NAME, INV_TAR_AMOUNT, INS_TAR_AMOUNT, ");
            sql.append("    TOT_TAR_AMOUNT, MAINTAIN_DATE, MAINTAIN_NAME, ");
            sql.append("    CREATETIME, CREATOR, MODIFIER, LASTUPDATE) ");
            sql.append(" VALUES ( ");
            sql.append("'" + inputVO.getReportDate() + "', '" + inputVO.getRegion_center_id() + "', '");
            sql.append(inputVO.getBranch_area_id() + "', '" + inputVO.getBranch_nbr() + "', '");
            sql.append(inputVO.getRc_name() + "', '" + inputVO.getOp_name() + "', '");
            sql.append(inputVO.getBr_name() + "', " + inputVO.getInvTarAmount() + ", ");
            sql.append(inputVO.getInsTarAmount() + ", " + inputVO.getTotTarAmount() + ", ");
            sql.append("SYSDATE, '" + loginID + "', SYSDATE, '" + loginID + "', '");
            sql.append(loginID + "', SYSDATE " + " ) ");

            condition.setQueryString(sql.toString());

            try {
                dam.exeUpdate(condition);
            } catch (JBranchException e) {
                throw new APException("");
            }

            this.sendRtnObject(null);
        } else {
            this.sendRtnObject("duplicate");
            return;
        }
    }


    /**
     * ===== 新增「理專追蹤商品目標」資料 =====
     **/
    public void insertPRD(Object body, IPrimitiveMap header)
            throws JBranchException {
        PMS203InputVO inputVO = (PMS203InputVO) body;
        DataAccessManager dam = this.getDataAccessManager();
        String loginID = (String) getUserVariable(FubonSystemVariableConsts.LOGINID);
        List<Map<String, String>> map = null;
        Timestamp currentTM = new Timestamp(System.currentTimeMillis());

        map = getPRDName(inputVO.getMainPrdId());
        if (map.size() == 0) {
            throw new APException("無此商品代號！");
        } else {
            QueryConditionIF condition = dam.getQueryCondition();
            StringBuffer sqlc = new StringBuffer();
            sqlc.append(" SELECT * FROM TBPMS_AO_TRC_TAR_M ");
            sqlc.append(" WHERE YEARMON = '" + inputVO.getReportDate() + "'");
            sqlc.append("   AND AO_CODE ='" + inputVO.getAo_code() + "'");
            condition.setQueryString(sqlc.toString());
            if (dam.exeQuery(condition).size() > 0) {
                this.sendRtnObject("duplicate");
                return;
            }

            StringBuffer sql = new StringBuffer();
            sql.append(" INSERT INTO TBPMS_AO_TRC_TAR_M (YEARMON, REGION_CENTER_ID, ");
            sql.append("    REGION_CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME, ");
            sql.append("    BRANCH_NBR, BRANCH_NAME, AO_CODE, EMP_ID, AO_JOB_RANK, ");
            sql.append("    MAIN_PRD_ID, MAIN_PRD_NAME, TAR_AMT, ");
            sql.append("    MAINTAIN_DATE, MAINTAIN_NAME, ");
            sql.append("    CREATETIME, CREATOR, MODIFIER, LASTUPDATE) ");
            sql.append(" VALUES ('");
            sql.append(inputVO.getReportDate() + "','" + inputVO.getRegion_center_id() + "','");
            sql.append(inputVO.getRc_name() + "','" + inputVO.getBranch_area_id() + "','");
            sql.append(inputVO.getOp_name() + "','" + inputVO.getBranch_nbr() + "','");
            sql.append(inputVO.getBr_name() + "','" + inputVO.getAo_code() + "','");
            sql.append(inputVO.getEmp_id() + "','" + inputVO.getJobTitleId() + "','");
            sql.append(inputVO.getMainPrdId() + "','" + map.get(0).get("PNAME") + "'," + inputVO.getTarAmount() + ", ");
            sql.append("SYSDATE, '" + loginID + "', ");
            sql.append("SYSDATE, '" + loginID + "', '" + loginID + "'," + " SYSDATE) ");

            condition.setQueryString(sql.toString());

            try {
                dam.exeUpdate(condition);
            } catch (JBranchException e) {
                throw new APException("");
            }


            this.sendRtnObject(null);
        }
    }

    /**
     * ===== 修改「理專職級設定」資料 =====
     **/
    public void updateSET(Object body, IPrimitiveMap header)
            throws JBranchException {
        PMS203InputVO inputVO = (PMS203InputVO) body;
        DataAccessManager dam = this.getDataAccessManager();
        String loginID = (String) getUserVariable(FubonSystemVariableConsts.LOGINID);
        Timestamp currentTM = new Timestamp(System.currentTimeMillis());

        QueryConditionIF condition = dam.getQueryCondition();
        StringBuffer sql = new StringBuffer();
        sql.append(" UPDATE TBPMS_PRO_FUN_TR_PROD_SET ");
        sql.append(" SET PRODUCT_GOALS = " + inputVO.getProdGoals() + ", ");
        sql.append("    KEEP_GOALS = " + inputVO.getKeepGoals() + ", ");
        sql.append("    PRO_STR_LINE = " + inputVO.getProStrLine() + ", ");
        sql.append("    PRO_HOR_LINE = " + inputVO.getProHorLine() + ", ");
        sql.append("    PRO_SLA_LINE = " + inputVO.getProSlaLine() + ", ");
        sql.append("    DEM_STR_LINE = " + inputVO.getDemStrLine() + ", ");
        sql.append("    DEM_HOR_LINE = " + inputVO.getDemHorLine() + ", ");
        sql.append("    DEM_SLA_LINE = " + inputVO.getDemSlaLine() + ", ");
        sql.append("    FIX_SAL = " + inputVO.getFixSal() + ", ");
        sql.append("    MODIFIER = '" + loginID + "', ");
        sql.append("    LASTUPDATE = SYSDATE ");
        sql.append(" WHERE DATA_YEARMON = '" + inputVO.getReportDate() + "' ");
        sql.append("    AND OL_TITLE = '" + inputVO.getOlTitle() + "' ");
        sql.append("    AND JOB_TITLE_ID = '" + inputVO.getJobTitleId() + "' ");

        condition.setQueryString(sql.toString());
        dam.exeUpdate(condition);
        this.sendRtnObject(null);
    }

    /**
     * ===== 修改「理專生產力目標」資料 =====
     **/
    public void updateTAR(Object body, IPrimitiveMap header)
            throws JBranchException {
        PMS203InputVO inputVO = (PMS203InputVO) body;
        DataAccessManager dam = this.getDataAccessManager();
        String loginID = (String) getUserVariable(FubonSystemVariableConsts.LOGINID);
        Timestamp currentTM = new Timestamp(System.currentTimeMillis());

        QueryConditionIF condition = dam.getQueryCondition();
        StringBuffer sql = new StringBuffer();
        //sql.append(" UPDATE TBPMS_AO_PRD_TAR_M ");
        sql.append(" UPDATE TBPMS_EMP_PRD_TAR_M ");
        sql.append(" SET TAR_AMOUNT   = " + inputVO.getTarAmount() + ", ");
        sql.append("    MAINTAIN_DATE = SYSDATE , ");
        sql.append("    MODIFIER      = '" + loginID + "', ");
        sql.append("    LASTUPDATE    = SYSDATE ");
        sql.append(" WHERE DATA_YEARMON      = '" + inputVO.getReportDate() + "' ");
        sql.append("    AND REGION_CENTER_ID = '" + inputVO.getRegion_center_id() + "' ");
        sql.append("    AND BRANCH_AREA_ID   = '" + inputVO.getBranch_area_id() + "' ");
        sql.append("    AND BRANCH_NBR       = '" + inputVO.getBranch_nbr() + "' ");
        sql.append("    AND EMP_ID           = '" + inputVO.getEmp_id() + "' ");

        condition.setQueryString(sql.toString());
        dam.exeUpdate(condition);
        this.sendRtnObject(null);
    }

    /**
     * ===== 修改「分行投保計績收益目標」資料 =====
     **/
    public void updateINS(Object body, IPrimitiveMap header)
            throws JBranchException {
        PMS203InputVO inputVO = (PMS203InputVO) body;
        DataAccessManager dam = this.getDataAccessManager();
        String loginID = (String) getUserVariable(FubonSystemVariableConsts.LOGINID);
        Timestamp currentTM = new Timestamp(System.currentTimeMillis());



        QueryConditionIF condition = dam.getQueryCondition();
        StringBuffer sql = new StringBuffer();
        sql.append(" UPDATE TBPMS_BR_PRD_TAR_M ");
        sql.append(" SET INV_TAR_AMOUNT  = " + inputVO.getInvTarAmount() + ", ");
        sql.append("    INS_TAR_AMOUNT   = " + inputVO.getInsTarAmount() + ", ");
        sql.append("    TOT_TAR_AMOUNT   = " + inputVO.getTotTarAmount() + ", ");
        sql.append("    EXCHG_TAR_AMOUNT = " + inputVO.getExchgTarAmount() + ", ");
        sql.append("    MAINTAIN_DATE    = SYSDATE , ");
        sql.append("    MODIFIER         = '" + loginID + "', ");
        sql.append("    LASTUPDATE       = SYSDATE ");
        sql.append(" WHERE DATA_YEARMON      = '" + inputVO.getReportDate() + "' ");
        sql.append("    AND REGION_CENTER_ID = '" + inputVO.getRegion_center_id() + "' ");
        sql.append("    AND BRANCH_AREA_ID   = '" + inputVO.getBranch_area_id() + "' ");
        sql.append("    AND BRANCH_NBR       = '" + inputVO.getBranch_nbr() + "' ");

        condition.setQueryString(sql.toString());
        dam.exeUpdate(condition);
        this.sendRtnObject(null);
    }

    /**
     * ===== 修改「分行投保計績銷售目標」資料 =====
     **/
    public void updateSALE(Object body, IPrimitiveMap header)
            throws JBranchException {
        PMS203InputVO inputVO = (PMS203InputVO) body;
        DataAccessManager dam = this.getDataAccessManager();
        String loginID = (String) getUserVariable(FubonSystemVariableConsts.LOGINID);
        Timestamp currentTM = new Timestamp(System.currentTimeMillis());

        QueryConditionIF condition = dam.getQueryCondition();
        StringBuffer sql = new StringBuffer();
        sql.append(" UPDATE TBPMS_BR_SALE_TAR_M ");
        sql.append(" SET INV_TAR_AMOUNT  = " + inputVO.getInvTarAmount() + ", ");
        sql.append("    INS_TAR_AMOUNT   = " + inputVO.getInsTarAmount() + ", ");
        sql.append("    TOT_TAR_AMOUNT   = " + inputVO.getTotTarAmount() + ", ");
        //sql.append("    EXCHG_TAR_AMOUNT = " + inputVO.getExchgTarAmount() + ", ");
        sql.append("    MAINTAIN_DATE    = SYSDATE , ");
        sql.append("    MODIFIER         = '" + loginID + "', ");
        sql.append("    LASTUPDATE = SYSDATE ");
        sql.append(" WHERE DATA_YEARMON      = '" + inputVO.getReportDate() + "' ");
        sql.append("    AND REGION_CENTER_ID = '" + inputVO.getRegion_center_id() + "' ");
        sql.append("    AND BRANCH_AREA_ID   = '" + inputVO.getBranch_area_id() + "' ");
        sql.append("    AND BRANCH_NBR       = '" + inputVO.getBranch_nbr() + "' ");

        condition.setQueryString(sql.toString());
        dam.exeUpdate(condition);
        this.sendRtnObject(null);
    }

    /**
     * ===== 修改「理專追蹤商品目標」資料 =====
     **/
    public void updatePRD(Object body, IPrimitiveMap header)
            throws JBranchException {
        PMS203InputVO inputVO = (PMS203InputVO) body;
        DataAccessManager dam = this.getDataAccessManager();
        String loginID = (String) getUserVariable(FubonSystemVariableConsts.LOGINID);
        Timestamp currentTM = new Timestamp(System.currentTimeMillis());

        QueryConditionIF condition = dam.getQueryCondition();
        StringBuffer sql = new StringBuffer();

        sql.append(" UPDATE TBPMS_AO_TRC_TAR_M ");
        sql.append(" SET TAR_AMT      = " + inputVO.getTarAmount() + ", ");
        sql.append("    MAINTAIN_DATE = SYSDATE , ");
        sql.append("    MODIFIER      = '" + loginID + "', ");
        sql.append("    LASTUPDATE    = SYSDATE ");
        sql.append(" WHERE YEARMON    = '" + inputVO.getReportDate() + "' ");
        sql.append("    AND REGION_CENTER_ID = '" + inputVO.getRegion_center_id() + "' ");
        sql.append("    AND BRANCH_AREA_ID   = '" + inputVO.getBranch_area_id() + "' ");
        sql.append("    AND BRANCH_NBR       = '" + inputVO.getBranch_nbr() + "' ");
        sql.append("    AND EMP_ID           = '" + inputVO.getEmp_id() + "' ");
        sql.append("    AND MAIN_PRD_ID      = '" + inputVO.getMainPrdId() + "' ");

        condition.setQueryString(sql.toString());
        dam.exeUpdate(condition);
        this.sendRtnObject(null);
    }

    /**
     * ====== 刪除「理專職級設定」資料 =======
     */

    public void delSET(Object body, IPrimitiveMap header)
            throws JBranchException {
        PMS203InputVO inputVO = (PMS203InputVO) body;
        DataAccessManager dam = this.getDataAccessManager();
        List<Map<String, Object>> list = inputVO.getList();

        for (int i = 0; i < list.size(); i++) {
            TBPMS_PRO_FUN_TR_PROD_SETPK pk = new TBPMS_PRO_FUN_TR_PROD_SETPK();

            pk.setDATA_YEARMON(list.get(i).get("data_yearmon").toString());
            pk.setOL_TITLE(list.get(i).get("ol_title").toString());
            pk.setJOB_TITLE_ID(list.get(i).get("job_title_id").toString());

            TBPMS_PRO_FUN_TR_PROD_SETVO vo = (TBPMS_PRO_FUN_TR_PROD_SETVO) dam
                    .findByPKey(TBPMS_PRO_FUN_TR_PROD_SETVO.TABLE_UID, pk);

            if (vo != null) {
                dam.delete(vo);
            } else {
                // 顯示資料不存在
                throw new APException("ehl_01_common_017");
            }
            this.sendRtnObject(null);
        }
    }

    /**
     * ====== 刪除「理專生產力目標」資料 =======
     */
    public void delTAR(Object body, IPrimitiveMap header)
            throws JBranchException {
        PMS203InputVO inputVO = (PMS203InputVO) body;
        List<Map<String, Object>> list = inputVO.getDelTARList();
        //List<Map<String, Object>> list = inputVO.getList();

        for (int i = 0; i < list.size(); i++) {
        	DataAccessManager dam = this.getDataAccessManager();
            QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
            StringBuffer sql = new StringBuffer();

            sql.append(" select * ");
            sql.append(" from TBPMS_EMP_PRD_TAR_M ");
            sql.append(" where DATA_YEARMON = :reportDate ");
            sql.append(" 	and EMP_ID = :emp_id ");

            condition.setObject("reportDate", list.get(i).get("data_yearmon"));
            condition.setObject("emp_id", list.get(i).get("emp_id"));

            condition.setQueryString(sql.toString());
            List<Map<String, Object>> delList = dam.exeQuery(condition);


            if (CollectionUtils.isNotEmpty(delList)
                    && delList.size() > 0) {
                // Delete 語法
                dam = this.getDataAccessManager();
                condition = dam.getQueryCondition();
                StringBuffer delSql = new StringBuffer();

                delSql.append(" delete from TBPMS_EMP_PRD_TAR_M ");
                delSql.append(" where DATA_YEARMON = :reportDate ");
                delSql.append(" 	and EMP_ID = :emp_id ");

                condition.setObject("reportDate", delList.get(0).get("DATA_YEARMON"));
                condition.setObject("emp_id", delList.get(0).get("EMP_ID"));

                condition.setQueryString(delSql.toString());
                dam.exeUpdate(condition);

            } else {
                // 顯示資料不存在
                throw new APException("ehl_01_common_017");
            }

            this.sendRtnObject(null);
        }
    }

    /**
     * ====== 刪除「分行投保計績收益目標」資料 =======
     */
    public void delINS(Object body, IPrimitiveMap header)
            throws JBranchException {
        PMS203InputVO inputVO = (PMS203InputVO) body;
        DataAccessManager dam = this.getDataAccessManager();
        List<Map<String, Object>> list = inputVO.getList();

        for (int i = 0; i < list.size(); i++) {
            TBPMS_BR_PRD_TAR_MPK pk = new TBPMS_BR_PRD_TAR_MPK();

            pk.setDATA_YEARMON(list.get(i).get("data_yearmon").toString());
            pk.setREGION_CENTER_ID(list.get(i).get("region_center_id").toString());
            pk.setBRANCH_AREA_ID(list.get(i).get("branch_area_id").toString());
            pk.setBRANCH_NBR(list.get(i).get("branch_nbr").toString());

            TBPMS_BR_PRD_TAR_MVO vo = (TBPMS_BR_PRD_TAR_MVO) dam
                    .findByPKey(TBPMS_BR_PRD_TAR_MVO.TABLE_UID, pk);

            if (vo != null) {
                dam.delete(vo);
            } else {
                // 顯示資料不存在
                throw new APException("ehl_01_common_017");
            }
            this.sendRtnObject(null);
        }
    }

    /**
     * ====== 刪除「分行投保計績銷量目標」資料 =======
     */
    public void delSALE(Object body, IPrimitiveMap header)
            throws JBranchException {

        PMS203InputVO inputVO = (PMS203InputVO) body;
        List<Map<String, Object>> list = inputVO.getList();


        for (int i = 0; i < list.size(); i++) {
        	DataAccessManager dam = this.getDataAccessManager();
            QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
            StringBuffer sql = new StringBuffer();

            sql.append(" select * ");
            sql.append(" from TBPMS_BR_SALE_TAR_M ");
            sql.append(" where DATA_YEARMON = :data_yearmon ");
            sql.append("    and REGION_CENTER_ID = :region_center_id ");
            sql.append("    and BRANCH_AREA_ID = :branch_area_id ");
            sql.append("    and BRANCH_NBR = :branch_nbr ");

            condition.setObject("data_yearmon", list.get(i).get("data_yearmon").toString());
            condition.setObject("region_center_id", list.get(i).get("region_center_id").toString());
            condition.setObject("branch_area_id", list.get(i).get("branch_area_id").toString());
            condition.setObject("branch_nbr", list.get(i).get("branch_nbr").toString());

            condition.setQueryString(sql.toString());
            List<Map<String, Object>> resultList = dam.exeQuery(condition);

            if (CollectionUtils.isNotEmpty(resultList) && resultList.size() > 0) {
                dam = this.getDataAccessManager();
                QueryConditionIF delCondition = dam.getQueryCondition();
                StringBuffer delSql = new StringBuffer();

                delSql.append(" delete from TBPMS_BR_SALE_TAR_M ");
                delSql.append(" where DATA_YEARMON = :data_yearmon ");
                delSql.append("    and REGION_CENTER_ID = :region_center_id ");
                delSql.append("    and BRANCH_AREA_ID = :branch_area_id ");
                delSql.append("    and BRANCH_NBR = :branch_nbr ");

                delCondition.setObject("data_yearmon", list.get(i).get("data_yearmon").toString());
                delCondition.setObject("region_center_id", list.get(i).get("region_center_id").toString());
                delCondition.setObject("branch_area_id", list.get(i).get("branch_area_id").toString());
                delCondition.setObject("branch_nbr", list.get(i).get("branch_nbr").toString());

                delCondition.setQueryString(delSql.toString());
                dam.exeUpdate(delCondition);

            } else {
                // 顯示資料不存在
                throw new APException("ehl_01_common_017");
            }

            this.sendRtnObject(null);
        }
    }

    /**
     * ====== 刪除「理專追蹤商品目標」資料 =======
     */
    public void delPRD(Object body, IPrimitiveMap header)
            throws JBranchException {
        PMS203InputVO inputVO = (PMS203InputVO) body;
        DataAccessManager dam = this.getDataAccessManager();
        List<Map<String, Object>> list = inputVO.getList();

        for (int i = 0; i < list.size(); i++) {
            TBPMS_AO_TRC_TAR_MPK pk = new TBPMS_AO_TRC_TAR_MPK();

            pk.setYEARMON(list.get(i).get("data_yearmon").toString());
            pk.setEMP_ID(list.get(i).get("emp_id").toString());
            pk.setMAIN_PRD_ID(list.get(i).get("main_prd_id").toString());

            TBPMS_AO_TRC_TAR_MVO vo = (TBPMS_AO_TRC_TAR_MVO) dam
                    .findByPKey(TBPMS_AO_TRC_TAR_MVO.TABLE_UID, pk);

            if (vo != null) {
                dam.delete(vo);
            } else {
                // 顯示資料不存在
                throw new APException("ehl_01_common_017");
            }
            this.sendRtnObject(null);
        }
    }

    /**
     * ==== 檢查CSV內分行代碼 ====
     **/
    public void checkBRANCH_NBR(Object body, IPrimitiveMap header)
            throws Exception {
        PMS203InputVO inputVO = (PMS203InputVO) body;
        PMS203OutputVO outputVO = new PMS203OutputVO();
        DataAccessManager dam = this.getDataAccessManager();
        Path path = Paths.get(new File(
        			(String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH)
        			, inputVO.getFileName()).toString()
        		);

        String dd = inputVO.getReportDate();
        List<String> lines = FileUtils.readLines(new File(path.toString()), "big5");
        List<Map<String, String>> map = null;

        for (int i = 1; i < lines.size(); i++) {
            liesCheck(lines.get(i).toString());
            String str_line = finalstring;
            String[] str = str_line.split(",");
            map = getORGInfo(str[0], "", dd, "");

            if (map.size() == 0) {
                outputVO.setNBR_state("0");
                outputVO.setBRANCH_NBR(str[0]);
                sendRtnObject(outputVO);
                return;
            }
        }
        this.sendRtnObject(outputVO);
    }

    /**
     * ==== 整批匯入 ====
     **/
    public void insertCSVFile(Object body, IPrimitiveMap header)
            throws Exception {
    	
        PMS203InputVO inputVO = (PMS203InputVO) body;
        DataAccessManager dam = this.getDataAccessManager();
        QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
        
        Path path = Paths.get(
        						new File((String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH)
        						,inputVO.getFileName()).toString()
        					);

        String loginID = (String) getUserVariable(FubonSystemVariableConsts.LOGINID);
        String reportDate = inputVO.getReportDate();
        String errMsg = "";

        List<Map<String, String>> map = null;
        List<Map<String, String>> map2 = null;
        
        
        List<String> lines = FileUtils.readLines(new File(path.toString()), "big5");

        //===============================理專職級設定===============================\\
        if ("SET".equals(inputVO.getTgtType())) {
            String errol = "";
            for (int i = 1; i < lines.size(); i++) {
                liesCheck(lines.get(i).toString());
                String str_line = finalstring;
                String[] str = str_line.split(",");
                String ol_title = "";

                //職等/職稱轉換為代碼
                List<Map<String, Object>> checktitle = checkSetTitleList(str);
               
                if (checktitle.size() > 0) {
                    ol_title = "0" + checktitle.get(0).get("PARAM_ORDER").toString();
                } else {    // 職等/職稱錯誤會使資料重複檢核失效，因此優先判斷
                    errol += i + "、";
                }

                //資料檢核
                List<Map<String, Object>> checklist = dataSetList(inputVO, ol_title, str);

                if (checklist.size() > 0) {    //資料重複:開始編輯
                    StringBuffer sql = new StringBuffer();
                    sql.append(" UPDATE TBPMS_PRO_FUN_TR_PROD_SET SET ");
                    sql.append("    LEVEL_DISTANCE = " + str[0] + ", ");
                    sql.append("    PRODUCT_GOALS = " + str[3] + ", ");
                    sql.append("    KEEP_GOALS = " + str[4] + ", ");
                    sql.append("    PRO_STR_LINE = " + str[5] + ", ");
                    sql.append("    PRO_HOR_LINE = " + str[6] + ", ");
                    sql.append("    PRO_SLA_LINE = " + str[7] + ", ");
                    sql.append("    DEM_STR_LINE = " + str[8] + ", ");
                    sql.append("    DEM_HOR_LINE = " + str[9] + ", ");
                    sql.append("    DEM_SLA_LINE = " + str[10] + ", ");
                    sql.append("    FIX_SAL = " + str[11] + ", ");
                    sql.append("    CREATETIME = SYSDATE, ");
                    sql.append("    CREATOR  = " + loginID + ", ");
                    sql.append("    MODIFIER = " + loginID + ", ");
                    sql.append("    LASTUPDATE = SYSDATE ");
                    sql.append(" WHERE DATA_YEARMON = '" + inputVO.getReportDate() + "'");
                    sql.append("    AND OL_TITLE = '" + ol_title + "'");
                    sql.append("    AND JOB_TITLE_ID = '" + str[2] + "'");

                    condition.setQueryString(sql.toString());
                    dam.exeUpdate(condition);
                } else {    //	正確:開始新增
                    StringBuffer sql = new StringBuffer();
                    sql.append(" INSERT INTO TBPMS_PRO_FUN_TR_PROD_SET ( ");
                    sql.append("    DATA_YEARMON, LEVEL_DISTANCE, OL_TITLE, ");
                    sql.append("    JOB_TITLE_ID, PRODUCT_GOALS, KEEP_GOALS, ");
                    sql.append("    PRO_STR_LINE, PRO_HOR_LINE, PRO_SLA_LINE, ");
                    sql.append("    DEM_STR_LINE, DEM_HOR_LINE, DEM_SLA_LINE, ");
                    sql.append("    FIX_SAL, CREATETIME, CREATOR, ");
                    sql.append("    MODIFIER, LASTUPDATE ) ");
                    sql.append(" VALUES ('" + inputVO.getReportDate() + "','");
                    sql.append(str[0] + "','" + ol_title + "','");
                    sql.append(str[2] + "','" + str[3] + "','" + str[4] + "','");
                    sql.append(str[5] + "','" + str[6] + "','" + str[7] + "','");
                    sql.append(str[8] + "','" + str[9] + "','" + str[10] + "','");
                    sql.append(str[11] + "',SYSDATE,'" + loginID + "','");
                    sql.append(loginID + "',SYSDATE)");
                    condition.setQueryString(sql.toString());

                    dam.exeUpdate(condition);
                }
            }

            //統整錯誤訊息
            if (!StringUtils.equals(errol, "")) {
                errol = "第" + errol.substring(0, errol.length() - 1) + "筆資料檢核錯誤:職等/職稱錯誤或不存在" + "\r\n";
            }
            errMsg = errol;
        }

        //===============================理專生產力目標==============================\\
        if ("TAR".equals(inputVO.getTgtType())) {
            


            
            /* 抓出CSV File中的所有資料 */
        	List<Map<String, Object>> tarTotalList = tarCSVInfoList(lines);

            String emp_id = "";
            int runCnt = 0;
        	/* 組出所有的 emp_id */
            for(Map<String, Object> tarMap : tarTotalList){
            	if(tarTotalList.size() > 1 && (runCnt+1) != tarTotalList.size()){
            		emp_id = emp_id + "'" + ObjectUtils.toString(tarMap.get("EMP_ID")) + "',";
            	}else{
            		emp_id = emp_id + "'" + ObjectUtils.toString(tarMap.get("EMP_ID")) + "'";
            	}
            	runCnt++;
            }
            
            /* 塞選要塞入DB中的值 */
            String amount = "";
            List<Map<String, String>> insertList    = getTarORGInfo(emp_id, reportDate);
            List<Map<String, String>> tarAmountList = new ArrayList<Map<String,String>>();
            Map<String, String> tarAmountMap = new HashMap<String, String>();
            /* 抓出 TAR_AMOUNT */
            for(int i=0; i<insertList.size();i++){
            	for(int j=0; j<tarTotalList.size(); j++){
            		if(insertList.get(i).get("EMP_ID").equals(tarTotalList.get(j).get("EMP_ID"))){
            			amount = ObjectUtils.toString(tarTotalList.get(j).get(tarTotalList.get(j).get("EMP_ID")));
            			tarAmountMap.put(insertList.get(i).get("EMP_ID"), amount);
            			
            			break;
            		}
            	}
            }
            tarAmountList.add(tarAmountMap);
            
            for (int i = 0; i < insertList.size(); i++) {
                if ("N".equals(insertList.get(i).get("INSERT_FLAG"))) {    
                	//資料重複:開始編輯
                	boolean isAutoCommit = dam.isAutoCommit();
                	dam.setAutoCommit(true);
                	
                    StringBuffer sql = new StringBuffer();
                    sql.append(" UPDATE TBPMS_EMP_PRD_TAR_M SET ");
                    sql.append("    TAR_AMOUNT = " + tarAmountList.get(0).get(insertList.get(i).get("EMP_ID")) + ", ");
                    sql.append("    MAINTAIN_DATE = SYSDATE, ");
                    sql.append("    MAINTAIN_NAME = '" + loginID + "' , ");
                    sql.append(" 	REGION_CENTER_ID = '" + insertList.get(i).get("REGION_CENTER_ID") + "' , "      );
					sql.append(" 	REGION_CENTER_NAME = '" + insertList.get(i).get("REGION_CENTER_NAME") +"' ,  ");
					sql.append(" 	BRANCH_AREA_ID  = '" + insertList.get(i).get("BRANCH_AREA_ID") +"' ,  ");
					sql.append(" 	BRANCH_AREA_NAME = '" + insertList.get(i).get("BRANCH_AREA_NAME") +"' ,  ");
					sql.append(" 	BRANCH_NBR     = '" + insertList.get(i).get("BRANCH_NBR") +"' ,  ");
					sql.append(" 	BRANCH_NAME    = '" + insertList.get(i).get("BRANCH_NAME") +"' ,  ");         
					sql.append(" 	AO_CODE        = '" + insertList.get(i).get("AO_CODE") +"' ,  ");
                    sql.append("    CREATETIME = SYSDATE, ");
                    sql.append("    CREATOR  = '" + loginID + "' , ");
                    sql.append("    MODIFIER = '" + loginID + "' , ");
                    sql.append("    LASTUPDATE = SYSDATE ");
                    sql.append(" WHERE DATA_YEARMON = '" + inputVO.getReportDate() + "'");
                    sql.append("  	AND EMP_ID = '" + insertList.get(i).get("EMP_ID") + "'");
                    
                    condition.setQueryString(sql.toString());
                    dam.exeUpdate(condition);
                    
                    dam.setAutoCommit(isAutoCommit);
                    
                } else if("Y".equals(insertList.get(i).get("INSERT_FLAG"))){    
                	//正確:開始新增
                	boolean isAutoCommit = dam.isAutoCommit();
                	dam.setAutoCommit(true);
                	
                    StringBuffer sql = new StringBuffer();
                    sql.append(" INSERT INTO TBPMS_EMP_PRD_TAR_M ( ");
                    sql.append("    DATA_YEARMON, REGION_CENTER_ID, REGION_CENTER_NAME, ");
                    sql.append("    BRANCH_AREA_ID, BRANCH_AREA_NAME, BRANCH_NBR, BRANCH_NAME, ");
                    sql.append("    AO_CODE, EMP_ID, EMP_NAME, JOB_TITLE_ID, FIRST_DATE, ");
                    sql.append("    JOB_TITLE, TAR_AMOUNT, MAINTAIN_DATE, MAINTAIN_NAME, ");
                    sql.append("    CREATETIME, CREATOR, MODIFIER, LASTUPDATE) ");
                    sql.append(" VALUES ( '");
                    sql.append(inputVO.getReportDate() + "','" + insertList.get(i).get("REGION_CENTER_ID") + "','");
                    sql.append(insertList.get(i).get("REGION_CENTER_NAME") + "','" + insertList.get(i).get("BRANCH_AREA_ID") + "','");
                    sql.append(insertList.get(i).get("BRANCH_AREA_NAME") + "','" + insertList.get(i).get("BRANCH_NBR") + "','");
                    sql.append(insertList.get(i).get("BRANCH_NAME") + "','" + insertList.get(i).get("AO_CODE") + "','" + insertList.get(i).get("EMP_ID") + "','");
                    sql.append(insertList.get(i).get("EMP_NAME") + "','" + insertList.get(i).get("AO_JOB_RANK") + "', ");
                    sql.append(" TO_DATE('" + insertList.get(i).get("ONBOARD_DATE") + "', 'YYYY-MM-DD'), '            ");
                    sql.append(insertList.get(i).get("JOB_TITLE_NAME") + "'," + tarAmountList.get(0).get(insertList.get(i).get("EMP_ID")) + ", SYSDATE, '" + loginID + "', ");
                    sql.append(" SYSDATE, '" + loginID + "', '" + loginID + "',  SYSDATE)");
                    
                    condition.setQueryString(sql.toString());
                    dam.exeUpdate(condition);
                    
                    dam.setAutoCommit(isAutoCommit);
                }            
            }
        }

        //=============================分行投保計績收益目標============================\\
        else if ("INS".equals(inputVO.getTgtType())) {
            String errnull = "";
            for (int i = 1; i < lines.size(); i++) {
                liesCheck(lines.get(i).toString());
                String str_line = finalstring;
                String[] str = str_line.split(",");
                map = getORGInfo(str[0], "", reportDate, "");

                //資料檢核
                List<Map<String, Object>> checklist = dataInsList(inputVO, str);

                if (StringUtils.equals(str[0], "")) {    //錯誤:分行代號為空
                    errnull += i + "、";
                } else if (checklist.size() > 0) {    //資料重複:開始編輯
                    StringBuffer sql = new StringBuffer();
                    sql.append(" UPDATE TBPMS_BR_PRD_TAR_M ");
                    sql.append(" SET");
                    sql.append("    INV_TAR_AMOUNT = " + str[1] + ", ");
                    sql.append("    INS_TAR_AMOUNT = " + str[2] + ", ");
                    sql.append("    EXCHG_TAR_AMOUNT = " + str[3] + ", ");
                    sql.append("    TOT_TAR_AMOUNT = " + str[4] + ", ");
                    sql.append("    MAINTAIN_DATE = SYSDATE, ");
                    sql.append("    MAINTAIN_NAME = " + loginID + ", ");
                    sql.append("    CREATETIME = SYSDATE, ");
                    sql.append("    CREATOR = " + loginID + ", ");
                    sql.append("    MODIFIER = " + loginID + ", ");
                    sql.append("    LASTUPDATE = SYSDATE ");
                    sql.append(" WHERE DATA_YEARMON = " + inputVO.getReportDate());
                    sql.append("    AND BRANCH_NBR = " + map.get(0).get("BRANCH_NBR"));

                    condition.setQueryString(sql.toString());
                    dam.exeUpdate(condition);

                } else if (map.size() == 0) { //該分行不存在，依然寫入，區域中心ID以及營運區ID以"000"帶入
                    StringBuffer sql = new StringBuffer();
                    sql.append(" INSERT INTO TBPMS_BR_PRD_TAR_M ( ");
                    sql.append("    DATA_YEARMON, REGION_CENTER_ID, REGION_CENTER_NAME, ");
                    sql.append("    BRANCH_AREA_ID, BRANCH_AREA_NAME, BRANCH_NBR, BRANCH_NAME, ");
                    sql.append("    INV_TAR_AMOUNT, INS_TAR_AMOUNT, EXCHG_TAR_AMOUNT, TOT_TAR_AMOUNT,  ");
                    sql.append("    MAINTAIN_DATE, MAINTAIN_NAME, ");
                    sql.append("    CREATETIME, CREATOR, MODIFIER, LASTUPDATE) ");
                    sql.append(" VALUES ('");
                    sql.append(inputVO.getReportDate() + "',000,'");
                    sql.append("" + "',000,'");
                    sql.append("" + "','" + str[0] + "','");
                    sql.append("" + "', " + str[1] + "," + str[2] + "," + str[3] + "," + str[4]);
                    sql.append(", SYSDATE, '" + loginID + "', ");
                    sql.append(" SYSDATE, '" + loginID + "', '" + loginID + "',  SYSDATE)");

                    condition.setQueryString(sql.toString());
                    dam.exeUpdate(condition);
                } else {    //正確:開始新增
                    StringBuffer sql = new StringBuffer();
                    sql.append(" INSERT INTO TBPMS_BR_PRD_TAR_M ( ");
                    sql.append("    DATA_YEARMON, REGION_CENTER_ID, REGION_CENTER_NAME, ");
                    sql.append("    BRANCH_AREA_ID, BRANCH_AREA_NAME, BRANCH_NBR, BRANCH_NAME, ");
                    sql.append("    INV_TAR_AMOUNT, INS_TAR_AMOUNT, EXCHG_TAR_AMOUNT, TOT_TAR_AMOUNT,  ");
                    sql.append("    MAINTAIN_DATE, MAINTAIN_NAME, ");
                    sql.append("    CREATETIME, CREATOR, MODIFIER, LASTUPDATE) ");
                    sql.append(" VALUES ('");
                    sql.append(inputVO.getReportDate() + "','" + map.get(0).get("REGION_CENTER_ID") + "','");
                    sql.append(map.get(0).get("REGION_CENTER_NAME") + "','" + map.get(0).get("BRANCH_AREA_ID") + "','");
                    sql.append(map.get(0).get("BRANCH_AREA_NAME") + "','" + str[0] + "','");
                    sql.append(map.get(0).get("BRANCH_NAME") + "', " + str[1] + "," + str[2] + "," + str[3] + "," + str[4]);
                    sql.append(", SYSDATE, '" + loginID + "', ");
                    sql.append(" SYSDATE, '" + loginID + "', '" + loginID + "',  SYSDATE)");

                    condition.setQueryString(sql.toString());
                    dam.exeUpdate(condition);
                }
            }
            //統整錯誤訊息
            if (!StringUtils.equals(errnull, "")) {
                errnull = "第" + errnull.substring(0, errnull.length() - 1) + "筆資料檢核錯誤:分行代碼不可為空" + "\r\n";
            }
            errMsg = errnull;
        }


        //=============================分行投保計績銷量目標============================\\
        else if ("SALE".equals(inputVO.getTgtType())) {
            String errnull = "";
            for (int i = 1; i < lines.size(); i++) {
                liesCheck(lines.get(i).toString());
                String str_line = finalstring;
                String[] str = str_line.split(",");
                map = getORGInfo(str[0], "", reportDate, "");

                //資料檢核
                List<Map<String, Object>> checklist = datSaleList(inputVO, str);

                if (StringUtils.equals(str[0], "")) {    //錯誤:分行代號為空
                    errnull += i + "、";
                } else if (checklist.size() > 0) {    //資料重複:開始編輯
                    StringBuffer sql = new StringBuffer();
                    sql.append(" UPDATE TBPMS_BR_SALE_TAR_M ");
                    sql.append(" SET");
                    sql.append("    INV_TAR_AMOUNT = " + str[1] + ", ");
                    sql.append("    INS_TAR_AMOUNT = " + str[2] + ", ");
                    sql.append("    TOT_TAR_AMOUNT = " + str[3] + ", ");
                    sql.append("    MAINTAIN_DATE = SYSDATE, ");
                    sql.append("    MAINTAIN_NAME = " + loginID + ", ");
                    sql.append("    CREATETIME = SYSDATE, ");
                    sql.append("    CREATOR = " + loginID + ", ");
                    sql.append("    MODIFIER = " + loginID + ", ");
                    sql.append("    LASTUPDATE = SYSDATE ");
                    sql.append(" WHERE DATA_YEARMON = " + inputVO.getReportDate());
                    sql.append("    AND BRANCH_NBR = " + map.get(0).get("BRANCH_NBR"));

                    condition.setQueryString(sql.toString());
                    dam.exeUpdate(condition);

                } else if (map.size() == 0) { //該分行不存在，依然寫入，區域中心ID以及營運區ID以"000"帶入
                    StringBuffer sql = new StringBuffer();
                    sql.append(" INSERT INTO TBPMS_BR_SALE_TAR_M ( ");
                    sql.append("    DATA_YEARMON, REGION_CENTER_ID, REGION_CENTER_NAME, ");
                    sql.append("    BRANCH_AREA_ID, BRANCH_AREA_NAME, BRANCH_NBR, BRANCH_NAME, ");
                    sql.append("    INV_TAR_AMOUNT, INS_TAR_AMOUNT, TOT_TAR_AMOUNT,  ");
                    sql.append("    MAINTAIN_DATE, MAINTAIN_NAME, ");
                    sql.append("    CREATETIME, CREATOR, MODIFIER, LASTUPDATE) ");
                    sql.append(" VALUES ('");
                    sql.append(inputVO.getReportDate() + "',000,'");
                    sql.append("" + "',000,'");
                    sql.append("" + "','" + str[0] + "','");
                    sql.append("" + "', " + str[1] + "," + str[2] + "," + str[3]);
                    sql.append(", SYSDATE, '" + loginID + "', ");
                    sql.append(" SYSDATE, '" + loginID + "', '" + loginID + "',  SYSDATE)");

                    condition.setQueryString(sql.toString());
                    dam.exeUpdate(condition);
                } else {    //正確:開始新增
                    StringBuffer sql = new StringBuffer();
                    sql.append(" INSERT INTO TBPMS_BR_SALE_TAR_M ( ");
                    sql.append("    DATA_YEARMON, REGION_CENTER_ID, REGION_CENTER_NAME, ");
                    sql.append("    BRANCH_AREA_ID, BRANCH_AREA_NAME, BRANCH_NBR, BRANCH_NAME, ");
                    sql.append("    INV_TAR_AMOUNT, INS_TAR_AMOUNT, TOT_TAR_AMOUNT,  ");
                    sql.append("    MAINTAIN_DATE, MAINTAIN_NAME, ");
                    sql.append("    CREATETIME, CREATOR, MODIFIER, LASTUPDATE) ");
                    sql.append(" VALUES ('");
                    sql.append(inputVO.getReportDate() + "','" + map.get(0).get("REGION_CENTER_ID") + "','");
                    sql.append(map.get(0).get("REGION_CENTER_NAME") + "','" + map.get(0).get("BRANCH_AREA_ID") + "','");
                    sql.append(map.get(0).get("BRANCH_AREA_NAME") + "','" + str[0] + "','");
                    sql.append(map.get(0).get("BRANCH_NAME") + "', " + str[1] + "," + str[2] + "," + str[3]);
                    sql.append(", SYSDATE, '" + loginID + "', ");
                    sql.append(" SYSDATE, '" + loginID + "', '" + loginID + "',  SYSDATE)");

                    condition.setQueryString(sql.toString());
                    dam.exeUpdate(condition);
                }
            }
            //統整錯誤訊息
            if (!StringUtils.equals(errnull, "")) {
                errnull = "第" + errnull.substring(0, errnull.length() - 1) + "筆資料檢核錯誤:分行代碼不可為空" + "\r\n";
            }
            errMsg = errnull;
        }

        //==============================理專追蹤商品目標=============================\\
        else if ("PRD".equals(inputVO.getTgtType())) {
            String errnull1 = "", errnull2 = "", errao = "", errao2 = "", errprd = "", errprd2 = "";
            for (int i = 1; i < lines.size(); i++) {
                liesCheck(lines.get(i).toString());
                String str_line = finalstring;
                String[] str = str_line.split(",");
                map = getORGInfo("", str[0], reportDate, "");
                map2 = getPRDName(str[1]);

                //資料檢核
                List<Map<String, Object>> checklist = dataPrdList(inputVO, str);

                if (StringUtils.equals(str[0], "") || StringUtils.equals(str[1], "")) {
                    if (StringUtils.equals(str[0], "")) {    //錯誤:AO_CODE為空
                        errnull1 += i + "、";
                    }
                    if (StringUtils.equals(str[1], "")) {    //錯誤:商品代碼為空
                        errnull2 += i + "、";
                    }
                } else if (map.size() == 0 || map2.size() == 0) {
                    if (map.size() == 0) {    //錯誤:AO_CODE不存在
                        errao += i + "、";
                        errao2 += str[0] + "、";
                    }
                    if (map2.size() == 0) {    //錯誤:商品代碼不存在
                        errprd += i + "、";
                        errprd2 += str[1] + "、";
                    }
                } else if (checklist.size() > 0) {    //資料重複:開始編輯
                    StringBuffer sql = new StringBuffer();
                    sql.append(" UPDATE TBPMS_AO_TRC_TAR_M SET");
                    sql.append("    TAR_AMT = " + str[2] + ", ");
                    sql.append("    MAINTAIN_DATE = SYSDATE, ");
                    sql.append("    MAINTAIN_NAME = " + loginID + ", ");
                    sql.append("    CREATETIME = SYSDATE, ");
                    sql.append("    CREATOR = " + loginID + ", ");
                    sql.append("    MODIFIER = " + loginID + ", ");
                    sql.append("    LASTUPDATE = SYSDATE ");
                    sql.append(" WHERE YEARMON = " + inputVO.getReportDate());
                    sql.append("    AND AO_CODE = '" + str[0] + "' ");
                    sql.append("    AND MAIN_PRD_ID = '" + str[1] + "' ");
                    condition.setQueryString(sql.toString());
                    dam.exeUpdate(condition);
                } else {    //正確:開始新增
                    StringBuffer sql = new StringBuffer();
                    sql.append(" INSERT INTO TBPMS_AO_TRC_TAR_M ( ");
                    sql.append("    YEARMON, REGION_CENTER_ID, REGION_CENTER_NAME, ");
                    sql.append("    BRANCH_AREA_ID, BRANCH_AREA_NAME, BRANCH_NBR, BRANCH_NAME, ");
                    sql.append("    AO_CODE, EMP_ID, AO_JOB_RANK, ");
                    sql.append("    MAIN_PRD_ID, MAIN_PRD_NAME, TAR_AMT, ");
                    sql.append("    MAINTAIN_DATE, MAINTAIN_NAME, ");
                    sql.append("    CREATETIME, CREATOR, MODIFIER, LASTUPDATE) ");
                    sql.append(" VALUES ('");
                    sql.append(inputVO.getReportDate() + "','" + checkIsNull(map.get(0), "REGION_CENTER_ID") + "','");
                    sql.append(checkIsNull(map.get(0), "REGION_CENTER_NAME") + "','" + checkIsNull(map.get(0), "BRANCH_AREA_ID") + "','");
                    sql.append(checkIsNull(map.get(0), "BRANCH_AREA_NAME") + "','" + checkIsNull(map.get(0), "BRANCH_NBR") + "','");
                    sql.append(checkIsNull(map.get(0), "BRANCH_NAME") + "','" + str[0] + "','" + checkIsNull(map.get(0), "EMP_ID") + "','");
                    sql.append(checkIsNull(map.get(0), "AO_JOB_RANK") + "','");
                    sql.append(str[1] + "','" + checkIsNull(map2.get(0), "PNAME") + "'," + str[2] + ",");
                    sql.append(" SYSDATE, '" + loginID + "', ");
                    sql.append(" SYSDATE, '" + loginID + "', '" + loginID + "',  SYSDATE)");

                    condition.setQueryString(sql.toString());
                    dam.exeUpdate(condition);
                }
            }
            //統整錯誤訊息
            if (!StringUtils.equals(errnull1, "")) {
                errnull1 = "第" + errnull1.substring(0, errnull1.length() - 1) + "筆資料檢核錯誤:AO_CODE不可為空" + "\r\n";
            }
            if (!StringUtils.equals(errnull2, "")) {
                errnull2 = "第" + errnull2.substring(0, errnull2.length() - 1) + "筆資料檢核錯誤:商品代碼不可為空" + "\r\n";
            }
            if (!StringUtils.equals(errao, "")) {
                errao = "第" + errao.substring(0, errao.length() - 1) + "筆資料檢核錯誤:沒有以下AO_CODE : " + errao2.substring(0, errao2.length() - 1) + "\r\n";
            }
            if (!StringUtils.equals(errprd, "")) {
                errprd = "第" + errprd.substring(0, errprd.length() - 1) + "筆資料檢核錯誤:沒有以下商品代碼 : " + errprd2.substring(0, errprd2.length() - 1) + "\r\n";
            }
            errMsg = errnull1 + errnull2 + errao + errprd;
        }

        if (!errMsg.equals(""))
            throw new APException(errMsg);
        else
            this.sendRtnObject(null);
    }

	private List<Map<String, Object>> tarCSVInfoList(List<String> lines) throws JBranchException {
        List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
        Map<String, Object> map = new HashMap<String, Object>();
        
        for (int i = 1; i < lines.size(); i++) {
            liesCheck(lines.get(i).toString());
            String data = finalstring;
            String[] dataLine = data.split(",");
            String empID = dataLine[0];
            empID = addZeroForNum(empID, 6);
            
            map = new HashMap<String, Object>();

            map = new HashMap<String, Object>();
            map.put("EMP_ID", empID);
            map.put(empID, dataLine[1]);
            list.add(map);
        }
		return list;
	}

	private List<Map<String, Object>> checkSetTitleList(String[] str) 
    		throws DAOException, JBranchException {
		DataAccessManager dam = this.getDataAccessManager();
    	QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    	
    	StringBuffer sql = new StringBuffer();
    	sql.append(" SELECT PARAM_ORDER FROM TBSYSPARAMETER ");
    	sql.append(" WHERE PARAM_TYPE = 'PMS.RANK_TITLE' ");
    	sql.append("    AND PARAM_NAME = '" + str[1] + "' ");
        
    	condition.setQueryString(sql.toString());
		return dam.exeQuery(condition);
	}

    private List<Map<String, Object>> dataSetList(PMS203InputVO inputVO, String ol_title, String[] str) 
    		throws DAOException, JBranchException {
    	DataAccessManager dam = this.getDataAccessManager();
    	dam = this.getDataAccessManager();
    	QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    	
    	StringBuffer sql = new StringBuffer();
    	sql.append(" SELECT * FROM TBPMS_PRO_FUN_TR_PROD_SET ");
    	sql.append(" WHERE DATA_YEARMON = '" + inputVO.getReportDate() + "'");
    	sql.append("      AND OL_TITLE = '" + ol_title + "'");
    	sql.append("      AND JOB_TITLE_ID = '" + str[2] + "'");

    	condition.setQueryString(sql.toString());
		return dam.exeQuery(condition);
	}
    
//	private List<Map<String, Object>> dataTarList(PMS203InputVO inputVO) 
//    		throws DAOException, JBranchException {
//		DataAccessManager dam = this.getDataAccessManager();
//    	dam = this.getDataAccessManager();
//    	QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
//    	
//    	StringBuffer sql = new StringBuffer();
//        sql.append(" SELECT * FROM TBPMS_EMP_PRD_TAR_M ");
//        sql.append(" WHERE DATA_YEARMON = '" + inputVO.getReportDate() + "'");
//
//        condition.setQueryString(sql.toString());
//        
//		return dam.exeQuery(condition);
//	}
	
    private List<Map<String, Object>> dataInsList(PMS203InputVO inputVO, String[] str) 
    		throws DAOException, JBranchException {
    	DataAccessManager dam = this.getDataAccessManager();
    	dam = this.getDataAccessManager();
    	QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    	
    	StringBuffer sql = new StringBuffer();
    	sql.append(" SELECT * FROM TBPMS_BR_PRD_TAR_M ");
    	sql.append(" WHERE DATA_YEARMON = '" + inputVO.getReportDate() + "'");
    	sql.append("  AND BRANCH_NBR = '" + str[0] + "'");
    	
        condition.setQueryString(sql.toString());
        return dam.exeQuery(condition);
	}
    
	private List<Map<String, Object>> datSaleList(PMS203InputVO inputVO, String[] str) 
			throws DAOException, JBranchException {
		DataAccessManager dam = this.getDataAccessManager();
    	dam = this.getDataAccessManager();
    	QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    	
    	StringBuffer sql = new StringBuffer();
    	sql.append(" SELECT * FROM TBPMS_BR_SALE_TAR_M ");
    	sql.append(" WHERE DATA_YEARMON = '" + inputVO.getReportDate() + "'");
    	sql.append("  AND BRANCH_NBR = '" + str[0] + "'");
    	
        condition.setQueryString(sql.toString());
        return dam.exeQuery(condition);
	}
	
	private List<Map<String, Object>> dataPrdList(PMS203InputVO inputVO, String[] str) 
			throws DAOException, JBranchException {
		DataAccessManager dam = this.getDataAccessManager();
    	dam = this.getDataAccessManager();
    	QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    	
    	StringBuffer sql = new StringBuffer();
    	sql.append(" SELECT * FROM TBPMS_AO_TRC_TAR_M ");
        sql.append(" WHERE YEARMON = '" + inputVO.getReportDate() + "'");
        sql.append("  AND AO_CODE = '" + str[0] + "'");
        sql.append("  AND MAIN_PRD_ID = '" + str[1] + "'");
        
        condition.setQueryString(sql.toString());
        return dam.exeQuery(condition);
	}
	/**
     * 取業務處、營運區、分行資訊
     **/
    public List<Map<String, String>> getORGInfo(String branchNbr, String empID, String reportDate, String type)
            throws JBranchException {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Integer.parseInt(reportDate.substring(0, 4)), Integer.parseInt(reportDate.substring(4)) - 1, 1);
        calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE));
        Date date = calendar.getTime();
        DateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");

        DataAccessManager dam = this.getDataAccessManager();
        QueryConditionIF condition = dam.getQueryCondition();

        StringBuffer sql = new StringBuffer();
        sql.append(" select org.REGION_CENTER_ID, org.REGION_CENTER_NAME, ");
        sql.append("    org.BRANCH_AREA_ID, org.BRANCH_AREA_NAME, ");
        sql.append("    org.BRANCH_NBR, org.BRANCH_NAME, ");
        sql.append("    emp.EMP_ID, emp.EMP_NAME, ");
        sql.append("    emp.AO_JOB_RANK, ");
        sql.append("    TO_CHAR( emp.ONBOARD_DATE,'yyyymmdd') as ONBOARD_DATE, ");
        sql.append("    emp.JOB_TITLE_NAME ");
        if (type.equals("TAR")) {
            sql.append("    ,sal.AO_CODE ");
        }
        sql.append(" from ( ");
        sql.append("    select ORG_ID, ");
        sql.append("        REGION_CENTER_ID, REGION_CENTER_NAME, ");
        sql.append("        BRANCH_AREA_ID, BRANCH_AREA_NAME, ");
        sql.append("        BRANCH_NBR, BRANCH_NAME ");
        sql.append("    from TBPMS_ORG_REC_N ");
        sql.append("    where TO_DATE(" + DATE_FORMAT.format(date) + ",'YYYYMMDD') between START_TIME and END_TIME  ");
        sql.append(" ) org ");
        sql.append("    left join TBPMS_EMPLOYEE_REC_N emp ");
        sql.append("        on org.ORG_ID = emp.ORG_ID and TO_DATE(" + DATE_FORMAT.format(date) + ",'YYYYMMDD') between emp.START_TIME and emp.END_TIME ");

        if (!empID.equals("")) {
            sql.append(" left join TBPMS_SALES_AOCODE_REC sal  on emp.EMP_ID = sal.EMP_ID and sal.type ='1' ");
        }

        sql.append(" where 1=1 ");

        if (!branchNbr.equals(""))
            sql.append(" and org.BRANCH_NBR = '" + branchNbr + "'");

        if (!empID.equals("") || type.equals("TAR")) {
            sql.append(" and TO_DATE(" + DATE_FORMAT.format(date) + ",'YYYYMMDD') between sal.START_TIME and sal.END_TIME  ");
            sql.append(" and emp.EMP_ID = " + empID);
        }

        condition.setQueryString(sql.toString());

        return dam.exeQuery(condition);
    }

    public List<Map<String, String>> getTarORGInfo(String empID, String reportDate)
            throws JBranchException {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Integer.parseInt(reportDate.substring(0, 4)), Integer.parseInt(reportDate.substring(4)) - 1, 1);
        calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE));
        Date date = calendar.getTime();
        DateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");

        DataAccessManager dam = this.getDataAccessManager();
        QueryConditionIF condition = dam.getQueryCondition();

        StringBuffer sql = new StringBuffer();
        sql.append(" select org.REGION_CENTER_ID, org.REGION_CENTER_NAME, ");
        sql.append("    org.BRANCH_AREA_ID, org.BRANCH_AREA_NAME, 		  ");
        sql.append("    org.BRANCH_NBR, org.BRANCH_NAME, 				  ");
        sql.append("    emp.EMP_ID, emp.EMP_NAME, 						  ");
        sql.append("    emp.AO_JOB_RANK, 								  ");
        sql.append("    TO_CHAR( emp.ONBOARD_DATE,'yyyymmdd') as ONBOARD_DATE, ");
        sql.append("    emp.JOB_TITLE_NAME, ");
        sql.append("    sal.AO_CODE,        ");
        sql.append("    case                ");
        sql.append("		when tar.EMP_ID is null then 'Y' ");
        sql.append("		else 'N'                         ");
        sql.append("	end as insert_flag,                  ");
        sql.append(" 	tar.EMP_ID as TAR_EMP_ID             ");
        sql.append(" from TBPMS_EMPLOYEE_REC_N emp ");
        sql.append(" left join ( 				   ");
        sql.append("    select ORG_ID, DEPT_ID,    ");
        sql.append("        REGION_CENTER_ID, REGION_CENTER_NAME, ");
        sql.append("        BRANCH_AREA_ID, BRANCH_AREA_NAME, 	  ");
        sql.append("        BRANCH_NBR, BRANCH_NAME 			  ");
        sql.append("    from TBPMS_ORG_REC_N 					  ");
        sql.append("    where TO_DATE(" + DATE_FORMAT.format(date) + ",'YYYYMMDD') between START_TIME and END_TIME  ");
        sql.append(" 		and ORG_TYPE = '50' AND DEPT_ID >= '200' and DEPT_ID <= '900' and LENGTH(DEPT_ID) = 3   ");
        sql.append(" ) org on org.DEPT_ID = emp.DEPT_ID 															");
        sql.append(" 	left join TBPMS_SALES_AOCODE_REC sal on emp.EMP_ID = sal.EMP_ID and sal.type ='1' 			");
		sql.append(" 	left join TBPMS_EMP_PRD_TAR_M tar on tar.EMP_ID = emp.EMP_ID and tar.DATA_YEARMON = " + reportDate );
        sql.append(" where 1=1 ");
        sql.append(" 	and TO_DATE(" + DATE_FORMAT.format(date) + ",'YYYYMMDD') between sal.START_TIME and sal.END_TIME  ");
        sql.append("	and TO_DATE(" + DATE_FORMAT.format(date) + ",'YYYYMMDD') between emp.START_TIME and emp.END_TIME  ");
        sql.append("	and emp.DEPT_ID >= '200' AND emp.DEPT_ID <= '900' AND LENGTH(emp.DEPT_ID) = 3                     ");
        sql.append("    and TO_NUMBER(emp.DEPT_ID) <> 806 AND TO_NUMBER(emp.DEPT_ID) <> 810" );
        sql.append("	and emp.EMP_ID is not null and org.BRANCH_NAME is not null                                        ");
        sql.append(" 	and emp.EMP_ID in (" + empID + ")");

        condition.setQueryString(sql.toString());

        return dam.exeQuery(condition);
    }
    
    /**
     * 取商品名稱
     **/
    public List<Map<String, String>> getPRDName(String prdid)
            throws JBranchException {
    	DataAccessManager dam = this.getDataAccessManager();
        QueryConditionIF condition = dam.getQueryCondition();
        StringBuffer sql = new StringBuffer();
        sql.append(" SELECT * FROM VWPRD_MASTER ");
        sql.append(" WHERE PRD_ID = '" + prdid + "'");

        condition.setQueryString(sql.toString());
        List originQuery = dam.exeQuery(condition);
        sql.setLength(0);
        sql.append(" SELECT * FROM VWPRD_MASTER ");
        sql.append(" WHERE PRD_ID = '0" + prdid + "'");
        condition.setQueryString(sql.toString());
        dam.exeQuery(condition);
        if (originQuery.size() > 0)
            return originQuery;
        return dam.exeQuery(condition);
    }

    public void getEmpInfo(Object body, IPrimitiveMap header)
            throws JBranchException {
        PMS203InputVO inputVO = (PMS203InputVO) body;
        PMS203OutputVO outputVO = new PMS203OutputVO();
        DataAccessManager dam = this.getDataAccessManager();
        QueryConditionIF condition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
        
        StringBuffer sql = new StringBuffer();
        sql.append(" select a.BRANCH_NBR, a.AO_CODE, a.EMP_NAME, a.AO_JOB_RANK, ");
        sql.append("    a.BRANCH_NAME, a.REGION_CENTER_NAME, a.BRANCH_AREA_NAME, ");
        sql.append("    b.ONBOARD_DATE, a.JOB_TITLE_NAME, a.EMP_ID ");
        sql.append(" from VWORG_BRANCH_EMP_DETAIL_INFO a ");
        sql.append("    left join TBORG_MEMBER b ");
        sql.append("        on b.EMP_ID = a.EMP_ID ");
        sql.append(" where 1=1 ");

        if (StringUtils.isNotBlank(inputVO.getBranch_nbr())) {
            sql.append(" AND a.BRANCH_NBR = :branch_nbr ");
            condition.setObject("branch_nbr", inputVO.getBranch_nbr());
        }

        if (StringUtils.isNotBlank(inputVO.getEmp_id())) {
            sql.append(" AND a.EMP_ID = :emp_id ");
            condition.setObject("emp_id", inputVO.getEmp_id());
        }

        sql.append(" and a.AO_CODE is not null ");
        sql.append(" order by a.EMP_ID ");

        condition.setQueryString(sql.toString());

        ResultIF list = dam.executePaging(condition, inputVO.getCurrentPageIndex() + 1, inputVO.getPageCount());

        outputVO.setTotalList(dam.executeQuery(condition));

        sendRtnObject(outputVO);
    }

    public void getBnrInfo(Object body, IPrimitiveMap header)
            throws JBranchException {
        PMS203InputVO inputVO = (PMS203InputVO) body;
        PMS203OutputVO outputVO = new PMS203OutputVO();
        DataAccessManager dam = this.getDataAccessManager();
        QueryConditionIF condition = dam.getQueryCondition();
        ArrayList<String> sql_list = new ArrayList<String>();
        StringBuffer sql = new StringBuffer();

        sql.append(" select REGION_CENTER_ID, REGION_CENTER_NAME, BRANCH_AREA_ID, BRANCH_AREA_NAME, ");
        sql.append("    BRANCH_NBR, BRANCH_NAME ");
        sql.append(" from VWORG_DEFN_INFO  ");
        //sql.append(" from TBPMS_BR_PRD_TAR_M  ");

        sql.append(" where 1=1 ");

        if (StringUtils.isNotBlank(inputVO.getBranch_nbr())) {
            sql.append(" and BRANCH_NBR = :branch_nbr ");
//            condition.setObject("branch_nbr", inputVO.getBranch_nbr());
            sql_list.add(inputVO.getBranch_nbr());
        }
        sql.append(" order by REGION_CENTER_ID, BRANCH_NBR ");

        condition.setQueryString(sql.toString());

        for (int sql_i = 0; sql_i < sql_list.size(); sql_i++) {
            condition.setString(sql_i + 1, sql_list.get(sql_i));
        }

        outputVO.setTotalList(dam.executeQuery(condition));
        sendRtnObject(outputVO);
    }

    /**
     * ==== 目標維護更新 ====
     **/
    public void runJob(Object body, IPrimitiveMap header)
            throws JBranchException {
        PMS203InputVO inputVO = (PMS203InputVO) body;

        DataAccessManager dam = this.getDataAccessManager();
        QueryConditionIF queryCondition = dam
                .getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
        StringBuffer sql = new StringBuffer();

        try {
            sql.append(" CALL PABTH_BTPMS200.MAIN(?)");
            queryCondition.setString(1, inputVO.getReportDate());
            queryCondition.setQueryString(sql.toString());
            dam.exeUpdate(queryCondition);

        } catch (Exception e) {
            logger.error(String.format("發生錯誤:%s",
                    StringUtil.getStackTraceAsString(e)));
            throw new APException("系統發生錯誤請洽系統管理員");
        }
    }

    /**
     * ==== 產出Excel ====
     **/
    public void export(Object body, IPrimitiveMap header)
            throws JBranchException {
        PMS203OutputVO outputVO = (PMS203OutputVO) body;
        String strTitle = "";
        String loginID = (String) getUserVariable(FubonSystemVariableConsts.LOGINID);
        String rptType = outputVO.getTargetType();

        if (rptType.equals("SET"))
            strTitle = "理專職級設定";

        if (rptType.equals("TAR"))
            strTitle = "理專生產力目標";

        if (rptType.equals("INS"))
            strTitle = "分行投保利潤目標";

        if (rptType.equals("SALE"))
            strTitle = "分行投保銷量目標";

        if (rptType.equals("PRD"))
            strTitle = "理專追蹤商品目標";

        List<Map<String, Object>> list = outputVO.getTotalList();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String fileName = "生產力目標維護_" + strTitle + "_" + sdf.format(new Date()) + "_" + loginID + ".csv";
        List listCSV = new ArrayList();

        int rec = 0;

        if (rptType.equals("SET"))
            rec = 14;
        else if (rptType.equals("TAR"))
            rec = 12;
        else if (rptType.equals("INS"))
            rec = 9;
        else if (rptType.equals("SALE"))
            rec = 8;
        else if (rptType.equals("PRD"))
            rec = 10;

        for (Map<String, Object> map : list) {

            String[] records = new String[rec];
            int i = 0;

            if (rptType.equals("SET")) {
                records[i] = checkIsNull(map, "LEVEL_DISTANCE"); // 級距
                records[++i] = checkIsNull(map, "OL_TITLE_N"); // 職等/職稱
            } else if (rptType.equals("TAR")) {
                records[i] = checkIsNull(map, "DATA_YEARMON");       // 資料年月
                records[++i] = checkIsNull(map, "REGION_CENTER_ID");
                records[++i] = checkIsNull(map, "REGION_CENTER_NAME"); // 業務處
                records[++i] = checkIsNull(map, "BRANCH_AREA_ID");     // 營運區
                records[++i] = checkIsNull(map, "BRANCH_AREA_NAME");
                records[++i] = checkIsNull(map, "BRANCH_NBR") + "-" + checkIsNull(map, "BRANCH_NAME"); // 分行
            } else {
                records[i] = checkIsNull(map, "REGION_CENTER_NAME"); // 業務處
                records[++i] = checkIsNull(map, "BRANCH_AREA_NAME"); // 營運區
                records[++i] = checkIsNull(map, "BRANCH_NBR") + "-" + checkIsNull(map, "BRANCH_NAME"); // 分行
            }

            if (rptType.equals("PRD"))
                records[++i] = checkIsNull(map, "AO_CODE") + "-" + checkIsNull(map, "EMP_NAME"); // 理專

            if (!rptType.equals("INS") && !rptType.equals("SALE") && !rptType.equals("TAR"))
                records[++i] = checkIsNull(map, "JOB_TITLE_ID_N"); // 職級

            if (rptType.equals("PRD")) {
                records[++i] = "=\"" + checkIsNull(map, "MAIN_PRD_ID") + "\""; // 主商品代號'
                records[++i] = checkIsNull(map, "MAIN_PRD_NAME"); // 主商品名稱
            }

            if (rptType.equals("SET")) {
                records[++i] = checkIsNull(map, "PRODUCT_GOALS"); // 生產力目標
                records[++i] = checkIsNull(map, "KEEP_GOALS"); // 維持級距目標
                records[++i] = checkIsNull(map, "PRO_STR_LINE"); // 晉級直線級距
                records[++i] = checkIsNull(map, "PRO_HOR_LINE"); // 晉級橫向級距
                records[++i] = checkIsNull(map, "PRO_SLA_LINE"); // 晉級45度級距
                records[++i] = checkIsNull(map, "DEM_STR_LINE"); // 降級直線級距
                records[++i] = checkIsNull(map, "DEM_HOR_LINE"); // 降級橫向級距
                records[++i] = checkIsNull(map, "DEM_SLA_LINE"); // 降級45度級距
                records[++i] = checkIsNull(map, "FIX_SAL"); // 定額調薪
            }

            if (rptType.equals("TAR")) {
                records[++i] = "=\"" + checkIsNull(map, "EMP_ID")  + "-" + checkIsNull(map, "EMP_NAME") + "\""; // 理專員編
                records[++i] = "=\"" + checkIsNull(map, "AO_CODE") + "\""; // 理專
                records[++i] = checkIsNull(map, "JOB_TITLE_ID");  // 職級
                records[++i] = currencyFormat(map, "TAR_AMOUNT"); // 目標金額
            }

            if (rptType.equals("PRD"))
                records[++i] = checkIsNull(map, "TAR_AMT"); // 目標金額

            if (rptType.equals("INS")) {
                records[++i] = currencyFormat(map, "INV_TAR_AMOUNT"); // 投資目標金額
                records[++i] = currencyFormat(map, "INS_TAR_AMOUNT"); // 保險目標金額
                records[++i] = currencyFormat(map, "EXCHG_TAR_AMOUNT"); // 匯兌目標金額
                records[++i] = currencyFormat(map, "TOT_TAR_AMOUNT"); // 總目標金額
            }

            if (rptType.equals("SALE")) {
                records[++i] = currencyFormat(map, "INV_TAR_AMOUNT"); // 投資目標金額
                records[++i] = currencyFormat(map, "INS_TAR_AMOUNT"); // 保險目標金額
                records[++i] = currencyFormat(map, "TOT_TAR_AMOUNT"); // 總目標金額
            }

            if (!rptType.equals("SET")) {
                records[++i] = checkIsNull(map, "MAINTAIN_DATE"); // 維護日期
                records[++i] = "=\"" + checkIsNull(map, "MAINTAIN_NAME") + "\""; // 維護人員
            }

            listCSV.add(records);
        }

        // header
        String[] csvHeader = new String[rec];
        int j = 0;

        if (rptType.equals("SET")) {
            csvHeader[j] = "級距";
            csvHeader[++j] = "職等/職稱";
        } else if (rptType.equals("TAR")) {
            csvHeader[j] = "資料年月";
            csvHeader[++j] = "業務處代碼";
            csvHeader[++j] = "業務處";
            csvHeader[++j] = "營運區代碼";
            csvHeader[++j] = "營運區";
            csvHeader[++j] = "分行";

        } else {
            csvHeader[j] = "業務處";
            csvHeader[++j] = "營運區";
            csvHeader[++j] = "分行";
        }

        if (rptType.equals("PRD"))
            csvHeader[++j] = "理專";

        if (!rptType.equals("INS") && !rptType.equals("SALE") && !rptType.equals("TAR"))
            csvHeader[++j] = "職級";

        if (rptType.equals("PRD")) {
            csvHeader[++j] = "主商品代號";
            csvHeader[++j] = "主商品名稱";
        }

        if (rptType.equals("SET")) {
            csvHeader[++j] = "生產力目標";
            csvHeader[++j] = "維持級距目標";
//			csvHeader[++j] = "晉級目標";
            csvHeader[++j] = "晉級直線級距";
            csvHeader[++j] = "晉級橫向級距";
            csvHeader[++j] = "晉級45度級距";
            csvHeader[++j] = "降級直線級距";
            csvHeader[++j] = "降級橫向級距";
            csvHeader[++j] = "降級45度級距";
            csvHeader[++j] = "定額調薪";
        }

        if (rptType.equals("TAR")) {
            csvHeader[++j] = "員編";
            csvHeader[++j] = "理專";
            csvHeader[++j] = "職級";
            //csvHeader[++j] = "到職日";
        }

        if (rptType.equals("TAR") || rptType.equals("PRD"))
            csvHeader[++j] = "目標金額";

        if (rptType.equals("INS")) {
            csvHeader[++j] = "投資目標金額";
            csvHeader[++j] = "保險目標金額";
            csvHeader[++j] = "匯兌目標金額";
            csvHeader[++j] = "總目標金額";
        }

        if (rptType.equals("SALE")) {
            csvHeader[++j] = "投資目標金額";
            csvHeader[++j] = "保險目標金額";
            csvHeader[++j] = "總目標金額";
        }

        if (!rptType.equals("SET")) {
            csvHeader[++j] = "維護日期";
            csvHeader[++j] = "維護人員";
        }

        CSVUtil csv = new CSVUtil();
        csv.setHeader(csvHeader);
        csv.addRecordList(listCSV);
        String url = csv.generateCSV();
        notifyClientToDownloadFile(url, fileName);
        this.sendRtnObject(null);
    }

    /**
     * 檢查Map取出欄位是否為Null
     *
     * @param map
     * @return String
     */
    private String checkIsNull(Map map, String key) {
        if (StringUtils.isNotBlank(String.valueOf(map.get(key)))
                && map.get(key) != null) {
            return String.valueOf(map.get(key));
        } else {
            return "";
        }
    }

    // 處理貨幣/數字格式
    private String currencyFormat(Map map, String key) {
        if (StringUtils.isNotBlank(String.valueOf(map.get(key)))
                && map.get(key) != null) {
            DecimalFormat df = new DecimalFormat("#,##0");
            return df.format(map.get(key));
        } else
            return "0";
    }

    /**
     * 下載範例檔
     **/
    public void downloadSample(Object body, IPrimitiveMap header)
            throws Exception {
        PMS203InputVO inputVO = (PMS203InputVO) body;

        if ("SET".equals(inputVO.getTgtType())) {
            notifyClientToDownloadFile("doc//PMS//PMS203_EXAMPLE_SET.csv",
                    "上傳範例_理專職級設定.csv");
        } else if ("TAR".equals(inputVO.getTgtType())) {
            notifyClientToDownloadFile("doc//PMS//PMS203_EXAMPLE_AO.csv",
                    "上傳範例_理專生產力目標.csv");
        } else if ("INS".equals(inputVO.getTgtType())) {
            notifyClientToDownloadFile("doc//PMS//PMS203_EXAMPLE_BR.csv",
                    "上傳範例_分行投保計績收益目標.csv");
        } else if ("SALE".equals(inputVO.getTgtType())) {
            notifyClientToDownloadFile("doc//PMS//PMS203_EXAMPLE_SALE.csv",
                    "上傳範例_分行投保計績銷量目標.csv");
        } else if ("PRD".equals(inputVO.getTgtType())) {
            notifyClientToDownloadFile("doc//PMS//PMS203_EXAMPLE_PRD.csv",
                    "上傳範例_理專追蹤商品目標.csv");
        }

        this.sendRtnObject(null);
    }


    /**
     * 找字串 " \" " 功能
     **/

    public String liesCheck(String lines) throws JBranchException {

        if (lines.indexOf("\"") != -1) {
            //抓出第一個"
            int acc = lines.indexOf("\"");

            //原本所有字串
            StringBuffer lines1 = new StringBuffer(lines.toString());

            //切掉前面的字串
            String ori = lines.substring(0, acc);

            String oriline = ori.toString();

            String fist = lines1.substring(acc);
            fist = fist.substring(1, fist.indexOf("\"", 1));
            String[] firstsub = fist.split(",");

            for (int i = 0; i < firstsub.length; i++) {
                oriline += firstsub[i].toString();
            }

            String okfirst = ori + "\"" + fist + "\"";   //前面組好的
            String oriline2 = lines.substring(acc);

            oriline2 = oriline2.replaceFirst("\"" + fist + "\"", "");
            String fistTwo = lines1.substring(0, lines1.indexOf("\""));
            lines = oriline + oriline2;

        }

        if (lines.indexOf("\"") != -1) {
            liesCheck(lines);
        } else {
            finalstring = lines;
        }

        return lines;
    }
    
    // EMP_ID 不足6位左補0，補滿6位
	public String addZeroForNum(String str, int strLength) {
		int strLen = str.length();
		if (strLen < strLength) {
			while (strLen < strLength) {
				StringBuffer sb = new StringBuffer();
				
				sb.append("0").append(str);// 左补0
				str = sb.toString();
				strLen = str.length();
			}
		}
		return str;
	}
}