package com.systex.jbranch.app.server.fps.fpsvolatilitylistbth;

import com.google.gson.Gson;
import com.ibm.icu.util.Calendar;
import com.systex.jbranch.app.server.fps.fpsutils.FPSUtils;
import com.systex.jbranch.fubon.bth.GenFileTools;
import com.systex.jbranch.fubon.jlb.CAM996;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.bizLogic.BizLogic;
import com.systex.jbranch.platform.util.IPrimitiveMap;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

/**
 * @author Juan
 * @date
 */
@Repository("fpsvolatilitylistbth")
@Scope("prototype")
public class FPSVolatilityListBTH extends BizLogic {
  private Logger logger = LoggerFactory.getLogger(FPSVolatilityListBTH.class);

  public enum paramTypes {
    FUND_AUM, VOLATILITY_LIMIT
  };

  final int FETCH_CUST_SIZE = 100;
  BigDecimal hundred = new BigDecimal(100);
  SimpleDateFormat yyyyMM = new SimpleDateFormat("yyyyMM");
  SimpleDateFormat yyyyMMdd = new SimpleDateFormat("yyyyMMdd");

  /**
   * execute get volatility list
   * 
   * @param body
   * @param header
   * @throws Exception
   */
  public void execute(Object body, IPrimitiveMap<?> header) throws Exception {
    DataAccessManager dam = this.getDataAccessManager();
    // public void execute(DataAccessManager dam) throws Exception {
    List<Map<String, Object>> result;
    result = getParam(dam, paramTypes.FUND_AUM);
    if (result == null || result.size() <= 0) {
      logger.error("FUND AUM is NULL");
      throw new Exception("FUND AUM is NULL");
    }
    // FUND AUM
    BigDecimal fundAum = new BigDecimal(ObjectUtils.toString(result.get(0).get("FUND_AUM")));
    result = getParam(dam, paramTypes.VOLATILITY_LIMIT);
    if (result == null || result.size() <= 0) {
      logger.error("Volatility Limit is NULL");
      throw new Exception("Volatility Limit is NULL");
    }
    // 波動度上限
    Map<String, BigDecimal> volatilityLimit = new HashMap<String, BigDecimal>();
    for (Map<String, Object> v : result) {
      volatilityLimit.put(ObjectUtils.toString(v.get("CUST_RISK_ATR")), (BigDecimal) v.get("VOLATILITY"));
    }

    // 前兩個月內下過名單
    List<String> ignoreCustList = campaignMonths(dam);

    // 警示涵蓋範圍 1 僅檢視使用理財規劃模組功能的基股商品部位 2 所有庫存的基股商品部位
    String alertType = ObjectUtils.toString(result.get(0).get("ALERT_TYPE"));

    List<Map<String, Object>> invCustList = ignoreCust(getInvCustList(dam, fundAum), ignoreCustList);
    //List<Map<String, Object>> sppCustList = ignoreCust(getSppCustList(dam), ignoreCustList);
    List<Map<String, Object>> mixInvalidCustList = new ArrayList<Map<String, Object>>();

    switch (alertType) {
    case "1":
      // invalidCustList= calFromInv(dam, custList);
      break;
    case "2":
      mixInvalidCustList = calFromStock(dam, invCustList, volatilityLimit);
      //mixInvalidCustList.addAll(calFromSpp(dam, sppCustList, volatilityLimit));
      break;
    default:
      logger.error("alert type is NULL");
      throw new Exception("alert type is NULL");
    }

    logger.info(new Gson().toJson(mixInvalidCustList));
    generateList(dam, mixInvalidCustList);
  }

  private void generateList(DataAccessManager dam, List<Map<String, Object>> invalidCustList) throws JBranchException, ClassNotFoundException,
      ParserConfigurationException, SAXException, IOException, SQLException {
    GregorianCalendar gc = new GregorianCalendar();
    Date sd = new Date();
    gc.setTime(new Date());
    gc.add(Calendar.DATE, 30);
    Date ed = gc.getTime();
    String sdStr = yyyyMM.format(sd);

    // 產出新名單標頭
    CAM996 cam996 = new CAM996();
    BigDecimal seqNo = cam996.getCampaignSEQ(dam);

    logger.info("list head seqNo: " + seqNo.toString());

    // 產出新名單
    logger.info("開始執行取得名單");
    Integer custCnt = 0;
    GenFileTools gft = new GenFileTools();
    Connection conn;
    conn = gft.getConnection();
    conn.setAutoCommit(false);
    StringBuffer sql = new StringBuffer();
    sql.append("INSERT INTO TBCAM_SFA_LE_IMP_TEMP(SEQNO, IMP_SEQNO, CUST_ID, CUST_NAME, BRANCH_ID, AO_CODE, START_DATE, END_DATE, LEAD_TYPE, LEAD_ID, ");
    sql.append("VAR_FIELD_LABEL1, VAR_FIELD_VALUE1, VAR_FIELD_LABEL2, VAR_FIELD_VALUE2, VAR_FIELD_LABEL3, VAR_FIELD_VALUE3, ");
    sql.append("VERSION, CREATETIME, CREATOR, MODIFIER, LASTUPDATE) ");
    sql.append("VALUES(?, ?, ?, ?, ?, ?, ?, ?, '03', ?, ");
    sql.append("'規劃名稱', ?, '資產波動率', ?, '波動率上限值', ?, ");
    sql.append("0, SYSDATE, 'FPSVolatilityListBTH', 'FPSVolatilityListBTH', SYSDATE) ");
    PreparedStatement pstmt = conn.prepareStatement(sql.toString());
    try {
      // add invalid list
      for (Map<String, Object> c : invalidCustList) {
        insertRow(dam, pstmt, seqNo, c, sd, ed);
        custCnt++;
        if (custCnt % 1000 == 0) {
          pstmt.executeBatch();
          conn.commit();
        }
        logger.info("完成一筆名單");
      }
      pstmt.executeBatch();
      conn.commit();

      cam996.saveCampaign(dam,
          seqNo,
          "EVENT_FPS01",
          sdStr + "_全資產規劃類股票商品超標警示名單",
          sdStr + "_全資產規劃類股票商品超標警示名單",
          sdStr,
          "04",
          sd,
          ed,
          "03",
          null,
          null,
          null,
          null,
          "FCALL",
          null,
          "IN",
          "00",
          null,
          new BigDecimal(invalidCustList.size()),
          "0000000000");
    } catch (Exception e) {
      if (conn != null)
        conn.rollback();
      e.printStackTrace();
    } finally {
      if (pstmt != null)
        pstmt.close();
      if (conn != null)
        conn.close();
    }

    logger.info("結束執行取得名單");
  }

  /**
   * insert list
   * 
   * @param dam
   * @param seqNo
   *          : lead seqNo
   * @param cust
   *          : cust data with custID, volatility, limit
   * @param SD
   *          : start date
   * @param ED
   *          : end date
   * @throws ClassNotFoundException
   * @throws ParserConfigurationException
   * @throws SAXException
   * @throws IOException
   * @throws SQLException
   * @throws JBranchException
   */
  @SuppressWarnings("unchecked")
  private void insertRow(DataAccessManager dam, PreparedStatement pstmt, BigDecimal seqNo, Map<String, Object> cust, Date SD, Date ED)
      throws ClassNotFoundException,
      ParserConfigurationException, SAXException, IOException, SQLException, JBranchException {

    QueryConditionIF sq_con = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    sq_con.setQueryString("SELECT SQ_TBCAM_SFA_LE_IMP_TEMP.nextval AS SEQNO1, SQ_TBCAM_SFA_LE_IMP_TEMP_LEAD.nextval AS SEQNO2 FROM DUAL");
    List<Map<String, Object>> sq_list = dam.exeQuery(sq_con);

    logger.info("list detail seqNo: " + sq_list.get(0).get("SEQNO1").toString());

    // SEQNO
    pstmt.setBigDecimal(1, (BigDecimal) sq_list.get(0).get("SEQNO1"));
    // IMP_SEQNO
    pstmt.setBigDecimal(2, seqNo);
    // CUST_ID
    pstmt.setString(3, ObjectUtils.toString(cust.get("custID")).trim());
    // CUST_NAME
    pstmt.setString(4, ObjectUtils.toString(cust.get("custName")));
    // BRANCH_ID
    pstmt.setString(5, ObjectUtils.toString(cust.get("branchID")));
    // AO_CODE
    pstmt.setString(6, ObjectUtils.toString(cust.get("aoCode")));
    // START_DATE
    pstmt.setTimestamp(7, new Timestamp(SD.getTime()));
    // END_DATE
    pstmt.setTimestamp(8, new Timestamp(ED.getTime()));
    // LEAD_ID
    pstmt.setString(9, "SYS" + yyyyMMdd.format(SD) + String.format("%014d", ((BigDecimal) sq_list.get(0).get("SEQNO2")).intValue()));
    // PlanName
    String planName = ObjectUtils.toString(cust.get("planName"));
    pstmt.setString(10, planName == null ? "全資產規劃" : planName);
    // Volatility
    pstmt.setString(11, ObjectUtils.toString(cust.get("volatility")));
    // Limit
    pstmt.setString(12, ObjectUtils.toString(cust.get("limit")));

    pstmt.addBatch();
  }

  /**
   * 拿前兩個月客戶名單
   * 
   * @param dam
   * @return
   * @throws DAOException
   * @throws JBranchException
   */
  private List<String> campaignMonths(DataAccessManager dam) throws DAOException, JBranchException {
    GregorianCalendar gc = new GregorianCalendar();
    gc.setTime(new Date());
    gc.add(Calendar.MONTH, -1);
    String firstStr = yyyyMM.format(gc.getTime());
    gc.add(Calendar.MONTH, -1);
    String secondStr = yyyyMM.format(gc.getTime());
    QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    StringBuffer sb = new StringBuffer();
    sb.append(" SELECT CUST_ID FROM TBCAM_SFA_LEADS");
    sb.append(" WHERE CAMPAIGN_ID = 'EVENT_FPS01'");
    sb.append(" AND (LEAD_NAME = :firstMonth OR LEAD_NAME = :secondMonth)");
    qc.setObject("firstMonth", firstStr + "_全資產規劃類股票商品超標警示名單");
    qc.setObject("secondMonth", secondStr + "_全資產規劃類股票商品超標警示名單");
    qc.setQueryString(sb.toString());

    List<Map<String, Object>> custListMap = dam.exeQuery(qc);
    List<String> custList = new ArrayList<String>();
    for (Map<String, Object> cust : custListMap) {
      custList.add(ObjectUtils.toString(cust.get("CUST_ID")));
    }

    return custList;
  }

  /**
   * 過濾排除名單
   * 
   * @param ls
   * @param ignoreLs
   * @return
   */
  private List<Map<String, Object>> ignoreCust(List<Map<String, Object>> ls, List<String> ignoreLs) {
    List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();

    for (Map<String, Object> item : ls) {
      if (!ignoreLs.contains(ObjectUtils.toString(item.get("CUST_ID")))) {
        resultList.add(item);
      }
    }

    return resultList;
  }

  /**
   * 以輸入的custList 找庫存資料並計算波動率
   * 
   * @param dam
   * @param custList
   * @throws Exception
   */
  private List<Map<String, Object>> calFromStock(DataAccessManager dam, List<Map<String, Object>> custList, Map<String, BigDecimal> volatilityLimit)
      throws Exception {
    Map<String, Map<String, Object>> custIDList = new HashMap<String, Map<String, Object>>();
    List<Map<String, Object>> invalidCustIDList = new ArrayList<Map<String, Object>>();

    if (custList == null || custList.size() <= 0) {
      logger.error("null custList in calFromStock()");
      throw new Exception("null custList in calFromStock()");
    }

    for (Map<String, Object> cust : custList) {
      custIDList.put(ObjectUtils.toString(cust.get("CUST_ID")), cust);
    }

    String[] custIDArr = custIDList.keySet().toArray(new String[custIDList.size()]);
    int cnt = custIDArr.length;
    int tail = cnt % FETCH_CUST_SIZE;
    int times = cnt / FETCH_CUST_SIZE + (tail > 0 ? 1 : 0);

    List<String> logs = new ArrayList<String>();
    for (int i = 0; i < times; i++) {
      Map<String, Object> queryParam = new HashMap<String, Object>();
      queryParam.put("custIDList", Arrays.copyOfRange(custIDArr, i * FETCH_CUST_SIZE, times == (i - 1) ? cnt : ((i + 1) * FETCH_CUST_SIZE - 1)));
      Map<String, List<Map<String, Object>>> filteredStock = listMap2Map("CUST_ID", doQuery(dam, queryParam, sql_getInvStocksFromCustID()));

      for (Entry<String, List<Map<String, Object>>> entry : filteredStock.entrySet()) {
        // 客戶持有 > 一檔商品
        if (entry.getValue() != null && entry.getValue().size() > 1) {
          List<Map<String, Object>> formatedStockLs = formatWeightMap(entry.getValue());
          BigDecimal custVolatility = volatilityLimit.get(ObjectUtils.toString(custIDList.get(entry.getKey()).get("CUST_RISK_ATR")));
          if (custVolatility == null) {
            logger.error(entry.getKey() + " riskAtr is null");
            continue;
          }

          // 庫存波動率
          BigDecimal stockVolatility = FPSUtils.getStandardDeviation(dam, formatedStockLs, 36, 12, false);
          logs.add("c: " + entry.getKey() + " l: " + custVolatility.toString() + " v:"
              + (stockVolatility == null ? "null" : stockVolatility.multiply(hundred).toString()));
          if (stockVolatility == null)
            continue;
          stockVolatility = stockVolatility.multiply(hundred);
          if (custVolatility.compareTo(stockVolatility) < 0) {
            Map<String, Object> invalidMap = new HashMap<String, Object>();
            invalidMap.put("custID", entry.getKey());
            invalidMap.put("custName", custIDList.get(entry.getKey()).get("CUST_NAME"));
            invalidMap.put("volatility", stockVolatility);
            invalidMap.put("limit", custVolatility);
            invalidMap.put("aoCode", custIDList.get(entry.getKey()).get("AO_CODE"));
            invalidMap.put("branchID", custIDList.get(entry.getKey()).get("BRA_NBR"));
            invalidCustIDList.add(invalidMap);
          }
        }
      }
    }

    for (String log : logs) {
      logger.info(log);
    }

    for (Map<String, Object> invalidMap : invalidCustIDList) {
      logger.info(new Gson().toJson(invalidMap));
    }

    return invalidCustIDList;
  }

  /**
   * 以輸入的custList 找庫存資料並計算波動率
   * 
   * @param dam
   * @param custList
   * @throws Exception
   */
  private List<Map<String, Object>> calFromSpp(DataAccessManager dam, List<Map<String, Object>> custList, Map<String, BigDecimal> volatilityLimit)
      throws Exception {
    Map<String, Map<String, Object>> custIDList = new HashMap<String, Map<String, Object>>();
    List<Map<String, Object>> invalidCustIDList = new ArrayList<Map<String, Object>>();

    if (custList == null || custList.size() <= 0) {
      logger.error("null custList in calFromSpp()");
      throw new Exception("null custList in calFromSpp()");
    }

    for (Map<String, Object> cust : custList) {
      custIDList.put(ObjectUtils.toString(cust.get("CUST_ID")), cust);
    }

    String[] custIDArr = custIDList.keySet().toArray(new String[custIDList.size()]);
    int cnt = custIDArr.length;
    int tail = cnt % FETCH_CUST_SIZE;
    int times = cnt / FETCH_CUST_SIZE + (tail > 0 ? 1 : 0);

    List<String> logs = new ArrayList<String>();
    for (int i = 0; i < times; i++) {
      Map<String, Object> queryParam = new HashMap<String, Object>();
      queryParam.put("custIDList", Arrays.copyOfRange(custIDArr, i * FETCH_CUST_SIZE, times == (i - 1) ? cnt : ((i + 1) * FETCH_CUST_SIZE - 1)));
      Map<String, List<Map<String, Object>>> filteredStock = listMap2Map("PLAN_ID", doQuery(dam, queryParam, sql_getSppStocksFromCustID()));

      for (Entry<String, List<Map<String, Object>>> entry : filteredStock.entrySet()) {
        // 客戶持有 > 一檔商品
        if (entry.getValue() != null && entry.getValue().size() > 1) {
          List<Map<String, Object>> formatedStockLs = formatWeightMap(entry.getValue());
          String planName = ObjectUtils.toString(entry.getValue().get(0).get("INV_PLAN_NAME"));
          String custID = ObjectUtils.toString(entry.getValue().get(0).get("CUST_ID"));
          String riskAtr = ObjectUtils.toString(entry.getValue().get(0).get("VOL_RISK_ATTR"));
          BigDecimal custVolatility = volatilityLimit.get(ObjectUtils.toString(riskAtr));
          if (custVolatility == null) {
            logger.error(entry.getKey() + " riskAtr is null");
            continue;
          }
          
          // 庫存波動率
          BigDecimal stockVolatility = FPSUtils.getStandardDeviation(dam, formatedStockLs, 36, 12, false);
          logs.add("c: " + custID + "p: " + entry.getKey() + " l: " + custVolatility.toString() + " v:"
              + (stockVolatility == null ? "null" : stockVolatility.multiply(hundred).toString()));
          if (stockVolatility == null)
            continue;
          stockVolatility = stockVolatility.multiply(hundred);
          if (custVolatility.compareTo(stockVolatility) < 0) {
            Map<String, Object> invalidMap = new HashMap<String, Object>();
            invalidMap.put("planName", planName);
            invalidMap.put("custID", custID);
            invalidMap.put("custName", custIDList.get(custID).get("CUST_NAME"));
            invalidMap.put("planID", entry.getKey());
            invalidMap.put("volatility", stockVolatility);
            invalidMap.put("limit", custVolatility);
            invalidMap.put("aoCode", custIDList.get(custID).get("AO_CODE"));
            invalidMap.put("branchID", custIDList.get(custID).get("BRA_NBR"));
            invalidCustIDList.add(invalidMap);
          }
        }
      }
    }

    for (String log : logs) {
      logger.info(log);
    }

    for (Map<String, Object> invalidMap : invalidCustIDList) {
      logger.info(new Gson().toJson(invalidMap));
    }

    return invalidCustIDList;
  }

  /**
   * get simple param
   * 
   * @param dam
   * @param paramType
   * @return
   * @throws DAOException
   * @throws JBranchException
   */
  @SuppressWarnings("unchecked")
  private List<Map<String, Object>> getParam(DataAccessManager dam, paramTypes paramType) throws DAOException, JBranchException {
    String sql = "";
    switch (paramType) {
    case FUND_AUM:
      sql = sql_getParam("TBFPS_OTHER_PARA", "D.FUND_AUM*10000 FUND_AUM");
      break;
    case VOLATILITY_LIMIT:
      sql = sql_getParam("TBFPS_CUSTRISK_VOLATILITY", "D.VOLATILITY, D.CUST_RISK_ATR, H.ALERT_TYPE");
      break;
    }
    if (StringUtils.isBlank(sql)) {
      return null;
    }

    QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    qc.setQueryString(sql);
    return dam.exeQuery(qc);
  }

  private String sql_getParam(String table, String selects) {
    StringBuffer sb = new StringBuffer();

    sb.append(" SELECT " + selects)
        .append(" FROM " + table + "_HEAD H")
        .append(" JOIN " + table + " D ON D.PARAM_NO = H.PARAM_NO")
        .append(" WHERE H.STATUS = 'A'");

    return sb.toString();
  }

  /**
   * 有規劃之客戶名單和PlanID
   * 
   * @param dam
   * @param fundAum
   * @return
   * @throws DAOException
   * @throws JBranchException
   */
  @SuppressWarnings("unchecked")
  private List<Map<String, Object>> getInvCustList(DataAccessManager dam, BigDecimal fundAum) throws DAOException, JBranchException {
    QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    StringBuffer sb = new StringBuffer();
    sb.append(" SELECT HE.PLAN_ID, HE.CUST_ID, HE.CUST_RISK_ATR, CUST.AO_CODE, CUST.BRA_NBR, CUST.CUST_NAME");
    sb.append(" FROM TBFPS_PORTFOLIO_PLAN_INV_HEAD HE");
    sb.append(" LEFT JOIN TBCRM_CUST_MAST CUST ON CUST.CUST_ID = HE.CUST_ID");
    sb.append(" JOIN TBFPS_CUST_LIST LI ON LI.CUST_ID = HE.CUST_ID");
    sb.append(" WHERE HE.VALID_FLAG = 'Y'");
    sb.append(" AND LI.FUND_AMT >= :fundAum");
    qc.setObject("fundAum", fundAum);
    qc.setQueryString(sb.toString());
    return dam.exeQuery(qc);
  }

  /**
   * 有規劃之客戶名單和PlanID
   * 
   * @param dam
   * @param fundAum
   * @return
   * @throws DAOException
   * @throws JBranchException
   */
  @SuppressWarnings("unchecked")
  private List<Map<String, Object>> getSppCustList(DataAccessManager dam) throws DAOException, JBranchException {
    QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    StringBuffer sb = new StringBuffer();
    sb.append(" SELECT DISTINCT HE.CUST_ID, CUST.AO_CODE, CUST.BRA_NBR, CUST.CUST_NAME");
    sb.append(" FROM TBFPS_PORTFOLIO_PLAN_SPP_HEAD HE");
    sb.append(" LEFT JOIN TBCRM_CUST_MAST CUST ON CUST.CUST_ID = HE.CUST_ID");
    sb.append(" WHERE HE.VALID_FLAG = 'Y' AND HE.PLAN_STATUS = 'ACTIVE'");
    qc.setQueryString(sb.toString());
    return dam.exeQuery(qc);
  }

  /* --- HELPING FUNCTION --- */

  /**
   * 
   * @param string
   * @param custList
   * @return
   */
  private Map<String, List<Map<String, Object>>> listMap2Map(String key, List<Map<String, Object>> stockList) {
    if (CollectionUtils.isEmpty(stockList)) {
      return null;
    }

    Map<String, List<Map<String, Object>>> result = new HashMap();
    for (Map<String, Object> row : stockList) {
      String mapKey = ObjectUtils.toString(row.get(key));
      if (mapKey == null) {
        logger.error("null key in stockList in listMap2Map()");
        continue;
      }

      if (result.containsKey(mapKey)) {
        // has this mapKey
        result.get(mapKey).add(row);
      } else {
        // new mapKey
        List<Map<String, Object>> newLs = new ArrayList<Map<String, Object>>();
        newLs.add(row);
        result.put(mapKey, newLs);
      }
    }

    return result;
  }

  /**
   * 
   * @param string
   * @param custList
   * @return
   */
  private String[] listMap2StringArray(String key, List<Map<String, Object>> custList) {
    if (CollectionUtils.isEmpty(custList)) {
      return null;
    }

    String[] result = new String[custList.size()];
    int cntIndex = 0;
    for (Map<String, Object> row : custList) {
      String item = ObjectUtils.toString(row.get(key));
      if (item != null)
        result[cntIndex++] = item;
      else
        logger.error("null item in listMap2StringArray()");
    }

    return result;
  }

  /**
   * 執行 Query
   * 
   * @param dam
   * @param paramMap
   * @return
   * @throws DAOException
   * @throws JBranchException
   */
  @SuppressWarnings({ "unchecked", "rawtypes" })
  private static List<Map<String, Object>> doQuery(DataAccessManager dam, Map<String, Object> paramMap, String sql) throws DAOException,
      JBranchException {
    QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    qc.setQueryString(sql);

    if (paramMap != null) {
      for (Entry entry : paramMap.entrySet()) {
        qc.setObject((String) entry.getKey(), entry.getValue());
      }
    }

    return dam.exeQuery(qc);
  }

  /**
   * 計算新商品權重
   * 
   * @param ls
   * @return
   */
  public List<Map<String, Object>> formatWeightMap(List<Map<String, Object>> ls) {
    List<Map<String, Object>> wmLs = new ArrayList<Map<String, Object>>();
    BigDecimal totalAmount = totalAmt(ls);

    for (Map<String, Object> item : ls) {
      Map<String, Object> wm = new HashMap<String, Object>();
      wm.put("PRD_ID", item.get("PRD_ID"));
      wm.put("PRD_TYPE", item.get("PRD_TYPE"));
      wm.put("TARGETS", item.get("TARGETS"));
      wm.put("WEIGHT", new BigDecimal(ObjectUtils.toString(item.get("PURCHASE_TWD_AMT"))).divide(totalAmount, 4, BigDecimal.ROUND_HALF_UP));
      wmLs.add(wm);
    }

    return wmLs;
  }

  /**
   * 取得總比例
   * 
   * @param mfdEtfInsList
   * @return
   */
  private BigDecimal totalAmt(List<Map<String, Object>> mfdEtfInsList) {
    BigDecimal totalPercent = BigDecimal.ZERO;
    for (Map<String, Object> map : mfdEtfInsList) {
      totalPercent = totalPercent.add(new BigDecimal(ObjectUtils.toString(map.get("PURCHASE_TWD_AMT"))).setScale(2, BigDecimal.ROUND_HALF_UP));
    }
    return totalPercent;
  }

  private String sql_getInvStocksFromCustID() {
    // CRM.AST_ALLPRD_TYPE : AST code
    // '07','08','09','12' 基金 ETF
    // '14' 保險
    StringBuffer sb = new StringBuffer();
    sb.append(" WITH");
    sb.append(" INS_TARGET AS (");
    sb.append("   SELECT (M.POLICY_NBR ||'-'|| TRIM(TO_CHAR(M.POLICY_SEQ  ,'00')) || CASE WHEN M.ID_DUP <> ' ' THEN '-' || M.ID_DUP END ) AS CERT_NBR,");
    sb.append("   M.PRD_ID, LISTAGG(D.INV_TARGET_NO, '/') WITHIN GROUP (ORDER BY D.POLICY_NO) AS TARGETS");
    sb.append("   FROM TBCRM_AST_INS_MAST M");
    sb.append("   INNER JOIN TBCRM_AST_INS_TARGET D ON M.POLICY_NBR = D.POLICY_NO AND M.POLICY_SEQ = D.POLICY_SEQ");
    sb.append("   WHERE M.CUST_ID IN :custIDList");
    sb.append("   GROUP BY M.PRD_ID, (M.POLICY_NBR ||'-'|| TRIM(TO_CHAR(M.POLICY_SEQ ,'00')) || CASE WHEN M.ID_DUP <> ' ' THEN '-' || M.ID_DUP END )");
    sb.append(" )");
    sb.append(" SELECT");
    sb.append(" M.CUST_ID, M.PROD_ID AS PRD_ID,");
    sb.append(" SUM(M.NOW_AMT_TWD) AS PURCHASE_TWD_AMT,");
    sb.append(" CASE WHEN M.AST_TYPE = '07' OR M.AST_TYPE = '08' OR M.AST_TYPE = '09'");
    sb.append("   THEN 'MFD'");
    sb.append("   WHEN M.AST_TYPE = '12' THEN 'ETF'");
    sb.append("   WHEN M.AST_TYPE = '14' THEN 'INS'");
    sb.append("   END AS PRD_TYPE,");
    sb.append(" INS_TARGET.TARGETS");
    sb.append(" FROM MVFPS_AST_ALLPRD_DETAIL M");
    sb.append(" LEFT JOIN INS_TARGET ON INS_TARGET.CERT_NBR = M.CERT_NBR");
    sb.append(" LEFT JOIN TBFPS_PLANID_MAPPING MAPPING ON MAPPING.CERTIFICATE_ID = M.CERT_NBR");
    sb.append(" WHERE M.CUST_ID IN :custIDList");
    sb.append(" AND (AST_TYPE IN ('07','08','09','12')");
    sb.append(" OR (AST_TYPE = '14' AND M.INS_TYPE = '2'))");
    sb.append(" AND M.NOW_AMT_TWD > 0");
    sb.append(" AND MAPPING.CERTIFICATE_ID IS NULL");
    sb.append(" GROUP BY M.CUST_ID, M.PROD_ID,");
    sb.append("   CASE WHEN M.AST_TYPE = '07' OR M.AST_TYPE = '08' OR M.AST_TYPE = '09' THEN 'MFD'");
    sb.append("     WHEN M.AST_TYPE = '12' THEN 'ETF'");
    sb.append("     WHEN M.AST_TYPE = '14' THEN 'INS'");
    sb.append("     END,");
    sb.append("   INS_TARGET.TARGETS");

    return sb.toString();
  }

  private String sql_getSppStocksFromCustID() {
    StringBuffer sb = new StringBuffer();
    sb.append(" WITH");
    sb.append(" TPPS AS (");
    sb.append("  SELECT (POLICY_NO ||'-'|| TRIM(TO_CHAR(POLICY_SEQ  ,'00')) || CASE WHEN ID_DUP <> ' ' THEN '-' || ID_DUP END ) AS CERT_NBR");
    sb.append("  FROM TBFPS_PORTFOLIO_PLAN_SPP WHERE POLICY_NO IS NOT NULL");
    sb.append("  UNION");
    sb.append("  SELECT CERTIFICATE_ID AS CERT_NBR FROM TBFPS_PLANID_MAPPING");
    sb.append(" ),");
    sb.append(" VWFPS AS (");
    sb.append("  SELECT M.* FROM MVFPS_AST_ALLPRD_DETAIL M");
    sb.append("  WHERE AST_TYPE IN ('07','08','09','14')");
    sb.append("  AND M.CUST_ID IN :custIDList");
    sb.append("  AND M.NOW_AMT_TWD > 0");
    sb.append("  AND EXISTS(SELECT TPPS.CERT_NBR FROM TPPS WHERE M.CERT_NBR = TPPS.CERT_NBR)");
    sb.append(" ),");
    sb.append(" INS_TARGET AS (");
    sb.append("   SELECT (M.POLICY_NBR ||'-'|| TRIM(TO_CHAR(M.POLICY_SEQ  ,'00')) || CASE WHEN M.ID_DUP <> ' ' THEN '-' || M.ID_DUP END ) AS CERT_NBR,");
    sb.append("   M.PRD_ID, LISTAGG(D.INV_TARGET_NO, '/') WITHIN GROUP (ORDER BY D.POLICY_NO) AS TARGETS");
    sb.append("   FROM TBCRM_AST_INS_MAST M");
    sb.append("   INNER JOIN TBCRM_AST_INS_TARGET D ON M.POLICY_NBR = D.POLICY_NO AND M.POLICY_SEQ = D.POLICY_SEQ");
    sb.append("   WHERE M.CUST_ID IN :custIDList");
    sb.append("   GROUP BY M.PRD_ID, (M.POLICY_NBR ||'-'|| TRIM(TO_CHAR(M.POLICY_SEQ ,'00')) || CASE WHEN M.ID_DUP <> ' ' THEN '-' || M.ID_DUP END )");
    sb.append(" )");
    sb.append(" SELECT");
    sb.append(" M.CUST_ID, M.PROD_ID AS PRD_ID,");
    sb.append(" SUM(M.NOW_AMT_TWD) AS PURCHASE_TWD_AMT,");
    sb.append(" CASE WHEN M.AST_TYPE = '07' OR M.AST_TYPE = '08' OR M.AST_TYPE = '09'");
    sb.append("   THEN 'MFD'");
    sb.append("   WHEN M.AST_TYPE = '14' THEN 'INS'");
    sb.append("   END AS PRD_TYPE,");
    sb.append(" INS_TARGET.TARGETS,");
    sb.append(" HE.PLAN_ID,");
    sb.append(" HE.INV_PLAN_NAME,");
    sb.append(" SUBSTR(HE.VOL_RISK_ATTR,0,2) AS VOL_RISK_ATTR");
    sb.append(" FROM VWFPS M");
    sb.append(" LEFT JOIN INS_TARGET ON INS_TARGET.CERT_NBR = M.CERT_NBR");
    sb.append(" JOIN TBFPS_PORTFOLIO_PLAN_SPP_HEAD HE ON HE.CUST_ID = M.CUST_ID AND HE.VALID_FLAG = 'Y' and HE.PLAN_STATUS = 'ACTIVE'");
    sb.append(" GROUP BY M.CUST_ID, M.PROD_ID,");
    sb.append("   CASE WHEN M.AST_TYPE = '07' OR M.AST_TYPE = '08' OR M.AST_TYPE = '09' THEN 'MFD'");
    sb.append("     WHEN M.AST_TYPE = '14' THEN 'INS'");
    sb.append("     END,");
    sb.append("   INS_TARGET.TARGETS,");
    sb.append("   HE.PLAN_ID,");
    sb.append("   HE.INV_PLAN_NAME,");
    sb.append("   HE.VOL_RISK_ATTR");

    return sb.toString();
  }
}
