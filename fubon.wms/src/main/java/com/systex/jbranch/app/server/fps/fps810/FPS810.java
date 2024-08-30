package com.systex.jbranch.app.server.fps.fps810;

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
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Component("fps810")
@Scope("request")
public class FPS810 extends FubonWmsBizLogic {
  Logger logger = LoggerFactory.getLogger(this.getClass());
  String FPS810TABLE_Y = "TBFPS_DASHBOARD_YTD";
  String FPS810TABLE_M = "TBFPS_DASHBOARD_MTD";
  String TABLE_HEAD = "_HEAD";


  public void inquire(Object body, IPrimitiveMap<?> header) throws DAOException, JBranchException{
    FPS810InputVO inputVO = (FPS810InputVO) body;
    FPS810OutputVO outputVO = new FPS810OutputVO();
    DataAccessManager dam = this.getDataAccessManager();
    
    String planType = inputVO.getPlanType();
    // T whole
    outputVO.setWholeEmpTotalM(getWholeEmpTotal(dam, "M", planType));
    outputVO.setWholeEmpTotalY(getWholeEmpTotal(dam, "Y", planType));
    outputVO.setWholeCustTotalM(getWholeCustTotal(dam, "M", planType));
    outputVO.setWholeCustTotalY(getWholeCustTotal(dam, "Y", planType));
    // T region
    outputVO.setRegionEmpTotalM(getRegionEmpTotal(dam, "M", inputVO.getRegionID(), inputVO.getAreaID(), inputVO.getBranchID(), planType));
    outputVO.setRegionEmpTotalY(getRegionEmpTotal(dam, "Y", inputVO.getRegionID(), inputVO.getAreaID(), inputVO.getBranchID(), planType));
    outputVO.setRegionCustTotalM(getRegionCustTotal(dam, "M", inputVO.getRegionID(), inputVO.getAreaID(), inputVO.getBranchID(), planType));
    outputVO.setRegionCustTotalY(getRegionCustTotal(dam, "Y", inputVO.getRegionID(), inputVO.getAreaID(), inputVO.getBranchID(), planType));
    // M
    outputVO.setEmpCntM(getEmpCnt(dam, "M", inputVO.getRegionID(), inputVO.getAreaID(), inputVO.getBranchID(), planType));
    outputVO.setCustCntM(getCustCnt(dam, "M", inputVO.getRegionID(), inputVO.getAreaID(), inputVO.getBranchID(), planType));
    outputVO.setAmountCntM(getAmountCnt(dam, "M", inputVO.getRegionID(), inputVO.getAreaID(), inputVO.getBranchID(), planType));
    // Y
    outputVO.setEmpCntY(getEmpCnt(dam, "Y", inputVO.getRegionID(), inputVO.getAreaID(), inputVO.getBranchID(), planType));
    outputVO.setCustCntY(getCustCnt(dam, "Y", inputVO.getRegionID(), inputVO.getAreaID(), inputVO.getBranchID(), planType));
    outputVO.setAmountCntY(getAmountCnt(dam, "Y", inputVO.getRegionID(), inputVO.getAreaID(), inputVO.getBranchID(), planType));
    
    this.sendRtnObject(outputVO);
  }
  
  public void getRegions(Object body, IPrimitiveMap<?> header) throws DAOException, JBranchException{
    DataAccessManager dam = this.getDataAccessManager();
    QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    StringBuffer sb = new StringBuffer();

    sb.append("SELECT DISTINCT REGION_CENTER_ID as REGION_ID, SUBSTR(REGION_CENTER_NAME,7,8) as REGION_NAME ");
    sb.append("FROM VWORG_DEFN_INFO ");
    sb.append("ORDER BY REGION_CENTER_ID ");
    qc.setQueryString(sb.toString());
    
    this.sendRtnObject(dam.exeQuery(qc));
  }

  @SuppressWarnings("unchecked")
  private List<Map<String, Object>> getWholeEmpTotal(DataAccessManager dam, String type, String planType) throws DAOException, JBranchException{
    QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    StringBuffer sb = new StringBuffer();
    // 全行各級理專總數
    sb.append("SELECT COUNT(DISTINCT EMP_ID) AS CNT, AO_JOB_RANK ");
    sb.append("FROM ");
    sb.append((("Y").equals(type) ? FPS810TABLE_Y : FPS810TABLE_M) + (StringUtils.isNotBlank(planType) ? TABLE_HEAD : "") + " ");
    if (StringUtils.isNotBlank(planType)){
      sb.append("WHERE PLAN_TYPE = :planType ");
      qc.setObject("planType", planType);
    }
    sb.append("GROUP BY AO_JOB_RANK ");

    qc.setQueryString(addPivot(sb.toString(), false));
    return dam.exeQuery(qc);
  }

  @SuppressWarnings("unchecked")
  private List<Map<String, Object>> getWholeCustTotal(DataAccessManager dam, String type, String planType) throws DAOException, JBranchException{
    QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    StringBuffer sb = new StringBuffer();
    // 全行各級客戶總數
    sb.append("SELECT SUM(CUST_TNUM_V) AS CNT_V, SUM(CUST_TNUM_A) AS CNT_A,SUM(CUST_TNUM_B) AS CNT_B ");
    sb.append("FROM ");
    sb.append((("Y").equals(type) ? FPS810TABLE_Y : FPS810TABLE_M) + (StringUtils.isNotBlank(planType) ? TABLE_HEAD : "") + " ");
    if (StringUtils.isNotBlank(planType)){
      sb.append("WHERE PLAN_TYPE = :planType ");
      qc.setObject("planType", planType);
    }

    qc.setQueryString(sb.toString());
    return dam.exeQuery(qc);
  }

  @SuppressWarnings("unchecked")
  private List<Map<String, Object>> getRegionEmpTotal(DataAccessManager dam, String type, String regionID, String areaID, String branchID, String planType) throws DAOException, JBranchException{
    QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
      
    StringBuffer sb = new StringBuffer();

    sb.append("SELECT REGION_CENTER_ID AS REGION_ID, SUBSTR(REGION_CENTER_NAME,7,8) AS REGION_NAME, COUNT(DISTINCT EMP_ID) AS CNT, AO_JOB_RANK ");
    sb.append("FROM ");
    sb.append((("Y").equals(type) ? FPS810TABLE_Y : FPS810TABLE_M) + (StringUtils.isNotBlank(planType) ? TABLE_HEAD : "") + " ");
    sb.append("where 1 = 1 ");
    if (StringUtils.isNotBlank(planType)){
      sb.append("AND PLAN_TYPE = :planType ");
      qc.setObject("planType", planType);
    }
    addRegionCondition(sb, qc, regionID, areaID, branchID);
    sb.append("GROUP BY AO_JOB_RANK,REGION_CENTER_NAME, REGION_CENTER_ID ");

    qc.setQueryString(addPivot(sb.toString(), true));
    return dam.exeQuery(qc);
  }

  @SuppressWarnings("unchecked")
  private List<Map<String, Object>> getRegionCustTotal(DataAccessManager dam, String type, String regionID, String areaID, String branchID, String planType) throws DAOException, JBranchException{
    QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
      
    StringBuffer sb = new StringBuffer();
    // 轄下各級客戶總數
    sb.append("SELECT REGION_CENTER_ID AS REGION_ID, SUBSTR(REGION_CENTER_NAME,7,8) AS REGION_NAME, SUM(CUST_TNUM_V) AS CNT_V, SUM(CUST_TNUM_A) AS CNT_A,SUM(CUST_TNUM_B) AS CNT_B ");
    sb.append("FROM ");
    sb.append((("Y").equals(type) ? FPS810TABLE_Y : FPS810TABLE_M) + (StringUtils.isNotBlank(planType) ? TABLE_HEAD : "") + " ");
    sb.append("where 1 = 1 ");
    if (StringUtils.isNotBlank(planType)){
      sb.append("AND PLAN_TYPE = :planType ");
      qc.setObject("planType", planType);
    }
    addRegionCondition(sb, qc, regionID, areaID, branchID);
    sb.append("GROUP BY REGION_CENTER_NAME, REGION_CENTER_ID ");
    sb.append("ORDER BY REGION_CENTER_ID ");

    qc.setQueryString(sb.toString());
    return dam.exeQuery(qc);
  }

  @SuppressWarnings("unchecked")
  private List<Map<String, Object>> getEmpCnt(DataAccessManager dam, String type, String regionID, String areaID, String branchID, String planType) throws DAOException, JBranchException{
    QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    StringBuffer sb = new StringBuffer();

    sb.append("SELECT REGION_CENTER_ID AS REGION_ID, SUBSTR(REGION_CENTER_NAME,7,8) AS REGION_NAME, COUNT(DISTINCT EMP_ID) AS CNT, AO_JOB_RANK ");
    sb.append("FROM ");
    sb.append((("Y").equals(type) ? FPS810TABLE_Y : FPS810TABLE_M) + (StringUtils.isNotBlank(planType) ? TABLE_HEAD : "") + " ");
    sb.append("where 1 = 1 ");
    if (StringUtils.isNotBlank(planType)){
      sb.append("AND PLAN_TYPE = :planType ");
      qc.setObject("planType", planType);
    }
    sb.append("AND (CUST_NUM_V + CUST_NUM_A + CUST_NUM_B) > 0 ");
    addRegionCondition(sb, qc, regionID, areaID, branchID);
    sb.append("GROUP BY AO_JOB_RANK,REGION_CENTER_NAME, REGION_CENTER_ID ");
 
    qc.setQueryString(addPivot(sb.toString(), true));
    return dam.exeQuery(qc);
  }
  
  @SuppressWarnings("unchecked")
  private List<Map<String, Object>> getCustCnt(DataAccessManager dam, String type, String regionID, String areaID, String branchID, String planType) throws DAOException, JBranchException{
    QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    StringBuffer sb = new StringBuffer();

    // 業務處處長 / DASHBOARD 有使用的客戶數
    sb.append("SELECT SUBSTR(REGION_CENTER_NAME,7,8) AS REGION_NAME, REGION_CENTER_ID, SUM(CUST_NUM_V) AS CNT_V, SUM(CUST_NUM_A) AS CNT_A,SUM(CUST_NUM_B) AS CNT_B ");
    sb.append("FROM ");
    sb.append((("Y").equals(type) ? FPS810TABLE_Y : FPS810TABLE_M) + (StringUtils.isNotBlank(planType) ? TABLE_HEAD : "") + " ");
    sb.append("where 1 = 1 ");
    if (StringUtils.isNotBlank(planType)){
      sb.append("AND PLAN_TYPE = :planType ");
      qc.setObject("planType", planType);
    }
    addRegionCondition(sb, qc, regionID, areaID, branchID);
    sb.append("GROUP BY REGION_CENTER_NAME, REGION_CENTER_ID ");
    sb.append("ORDER BY REGION_CENTER_ID ");
 
    qc.setQueryString(sb.toString());
    return dam.exeQuery(qc);
  }

  @SuppressWarnings("unchecked")
  private List<Map<String, Object>> getAmountCnt(DataAccessManager dam, String type, String regionID, String areaID, String branchID, String planType) throws DAOException, JBranchException{
    QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    StringBuffer sb = new StringBuffer();

    // 業務處處長 / DASHBOARD 理財規劃金額
    sb.append("SELECT SUBSTR(REGION_CENTER_NAME,7,8) AS REGION_NAME, SUM(N_PCH_AMOUNT) AS N_PCH_AMOUNT, SUM(PCH_FEE) AS PCH_FEE, SUM(PCH_AMOUNT) AS PCH_AMOUNT ");
    sb.append("FROM ");
    sb.append((("Y").equals(type) ? FPS810TABLE_Y : FPS810TABLE_M) + (StringUtils.isNotBlank(planType) ? TABLE_HEAD : "") + " ");
    sb.append("where 1 = 1 ");
    if (StringUtils.isNotBlank(planType)){
      sb.append("AND PLAN_TYPE = :planType ");
      qc.setObject("planType", planType);
    }
    addRegionCondition(sb, qc, regionID, areaID, branchID);
    sb.append("GROUP BY REGION_CENTER_NAME ");
 
    qc.setQueryString(sb.toString());
    return dam.exeQuery(qc);
  }
  
  private void addRegionCondition(StringBuffer sb, QueryConditionIF qc, String regionID, String areaID, String branchID){
    if (StringUtils.isNotBlank(regionID)){
      sb.append("AND REGION_CENTER_ID = :regionID ");
      qc.setObject("regionID", regionID);
    }
    if (StringUtils.isNotBlank(areaID)){
      sb.append("AND BRANCH_AREA_ID = :areaID ");
      qc.setObject("areaID", areaID);
    }
    if (StringUtils.isNotBlank(branchID)){
      sb.append("AND BRANCH_NBR = :branchID ");
      qc.setObject("branchID", branchID);
    }
  }

  private String addPivot(String str, Boolean withName){
    StringBuffer sb = new StringBuffer();
    sb.append("SELECT ");
    if(withName){
      sb.append("REGION_ID, REGION_NAME,"); 
    }
    sb.append("NVL(FC1,0) AS FC1, NVL(FC2,0) AS FC2, NVL(FC3,0) AS FC3, NVL(FC4,0) AS FC4, NVL(FC5,0) AS FC5 ");
    sb.append("FROM ( ");
    sb.append(str);
    sb.append(")PIVOT (SUM(CNT) FOR AO_JOB_RANK IN ");
    sb.append("('FC1' AS \"FC1\", 'FC2' AS \"FC2\", 'FC3' AS \"FC3\", 'FC4' AS \"FC4\", 'FC5' AS \"FC5\")) ");
    if(withName){
      sb.append("ORDER BY REGION_ID ");
    }
    return sb.toString();
  }

}