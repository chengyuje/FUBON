package com.systex.jbranch.app.server.fps.fps240;

import com.systex.jbranch.app.common.fps.table.TBFPS_PORTFOLIO_PLAN_INV_HEADVO;
import com.systex.jbranch.app.server.fps.fps200.FPS200;
import com.systex.jbranch.app.server.fps.fps210.FPS210;
import com.systex.jbranch.app.server.fps.fps220.FPS220;
import com.systex.jbranch.app.server.fps.fps230.FPS230;
import com.systex.jbranch.app.server.fps.fpsjlb.FPSJLB;
import com.systex.jbranch.app.server.fps.fpsjlb.dao.FpsjlbDao;
import com.systex.jbranch.app.server.fps.fpsutils.FPSUtils;
import com.systex.jbranch.app.server.fps.fpsutils.FPSUtilsPdfInputVO;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.ObjectUtil;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.server.info.SystemVariableConsts;
import com.systex.jbranch.platform.util.IPrimitiveMap;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

@Component("fps240")
@Scope("request")
public class FPS240 extends FPS200 {
    Logger logger = LoggerFactory.getLogger(this.getClass());
    String PLANSTATUS = "4";

    @Autowired
    @Qualifier("fps210")
    private FPS210 fps210;
    @Autowired
    @Qualifier("fps220")
    private FPS220 fps220;
    @Autowired
    @Qualifier("fps230")
    private FPS230 fps230;
    @Autowired
    @Qualifier("fpsjlb")
    private FPSJLB fpsjlb;
    @Autowired
    @Qualifier("fpsjlbDao")
    private FpsjlbDao fpsDao;
    @Autowired
    @Qualifier("fpsutils")
    private FPSUtils fpsUtils;

    private DataAccessManager dam = null;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

    public void inquire(Object body, IPrimitiveMap<?> header) throws Exception{
        FPS240InputVO inputVO = (FPS240InputVO) body;
        FPS240OutputVO outputVO = new FPS240OutputVO();
        dam = this.getDataAccessManager();
        String custID = inputVO.getCustID();
        String planID = inputVO.getPlanID();
        String riskType = inputVO.getRiskType();
        String commRsYn = inputVO.getCommRsYn();
        String isPro = inputVO.getIsPro();
        String isOBU = inputVO.getOBU();
        boolean hasInvest = this.hasInvest(dam, planID);
        String fpsType = this.getFpsType(commRsYn, hasInvest);

        if (StringUtils.isBlank(planID) || StringUtils.isBlank(custID) || StringUtils.isBlank(riskType)) {
            throw new APException("沒有PlanID || 沒有CustID || 沒有riskType"); //
        }
        outputVO.setHeadList(fps210.getCustPlan(dam, planID));
        outputVO.setOutputList(fps230.hisQuery(dam, planID));
        outputVO.setFxRateList(getFxRate(dam, ""));
        outputVO.setInitModelPortfolioList(fps230.modelQuery(dam, riskType, null, isOBU, isPro));
        outputVO.setHasInvest(hasInvest(dam, planID));
        // 規劃日期請抓標的的最新修改日期。
        outputVO.setPlanDate(FPSUtils.getLastUpdate(dam, planID, "INV"));
        outputVO.setStockRiskLevel(getCustRisKPct(dam));
        // set init key map for checkValidYear
        Map<String, String> keyMap = new HashMap<String, String>();
        keyMap.put("prodType", "PTYPE");
        keyMap.put("prodID", "PRD_ID");
        keyMap.put("targets", "TARGETS");
        // 庫存波動度
        if (outputVO.isHasInvest() && outputVO.getOutputList().size() > 0) {
            List<Map<String, Object>> stockList = fps220.calStockVolaility(dam, custID);
            List<Map<String, Object>> formatedStockLs = formatWeightMap(stockList);
            if (FPSUtils.checkValidYear(dam, stockList, 1, keyMap).length == 0) {
                // 庫存波動率
                BigDecimal stockVolatility = FPSUtils.getStandardDeviation(dam, formatedStockLs, 36, 12, false);
                outputVO.setStockVolatility(stockVolatility == null ? null : stockVolatility.doubleValue());
            }
        }

        List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
        dataList.addAll(filterList(outputVO.getOutputList(), "2"));
        dataList.addAll(filterList(outputVO.getOutputList(), "3"));
        
        // 基股類商品
        List<Map<String, Object>> prdList = rmBackItem(dataList);
        // 刪除贖回

        List<Map<String, Object>> formatedProdLs = formatWeightMap(prdList);

        // 1年
        if (FPSUtils.checkValidYear(dam, prdList, 1, keyMap).length == 0) {
            // 歷史年度平均報酬率
            BigDecimal yRate = FPSUtils.getYRate(dam, formatedProdLs, 120, 12);
            outputVO.setHistoryYRate(yRate == null ? null : yRate.doubleValue());
            // 波動率
            BigDecimal volatility = FPSUtils.getStandardDeviation(dam, formatedProdLs, 36, 12, false);
            outputVO.setVolatility(volatility == null ? null : volatility.doubleValue());
            // 有滿一年波動率
            BigDecimal FYvolatility = FPSUtils.getStandardDeviation(dam, formatedProdLs, 36, 12, true);
            outputVO.setFullYearVolatility(FYvolatility == null ? null : FYvolatility.doubleValue());
            // 歷史績效
            outputVO.setYearRateList(FPSUtils.getReturnAnnM(dam, formatedProdLs, 10, 1));
        } else {
            outputVO.setVolatility(null);
            outputVO.setYearRateList(null);
            outputVO.setHistoryYRate(null);
        }

        // 波動度參數
        outputVO.setRecoVolatility(fps220.getRecoVolaility(dam, riskType));

        fpsType = getFpsType(commRsYn, outputVO.isHasInvest());
        // email
        // outputVO.seteMail(this.getEmail(custID));
        // 前言
        outputVO.setBriefList(this.getBriefList(fpsType));
        // 使用指南
        outputVO.setManualList(this.getManualList(fpsType));
        // 商品的警語
        // outputVO.setWarningList(this.getWarningList());

        outputVO.setMFDPerformanceList(getPerformance(dam, planID, "MFD"));
        outputVO.setDebtPerformanceList(getPerformance(dam, planID, "DEBT"));
        outputVO.setRptPicture(getRptPicture());
        outputVO.setPrevBusiDay(getPrevBussinessDay(dam));
        
        Map<String, Object> param = new HashMap<String, Object>();
        
        if(null != inputVO.getHasIns()) {
        	param.put("hasIns", inputVO.getHasIns());
        	param.put("custID", inputVO.getCustID());
        	param.put("riskType", inputVO.getRiskType());
        	BigDecimal[] beforeAdj = fps210.getSuggestPct(dam, param);
        	
        	outputVO.getHeadList().get(0).put("fixedPct", beforeAdj[0]);
        	outputVO.getHeadList().get(0).put("stockPct", beforeAdj[1]);	
        	
        	if(beforeAdj.length > 2) {
        		outputVO.getHeadList().get(0).put("fixedAmt", beforeAdj[2]);
        		outputVO.getHeadList().get(0).put("stockAmt", beforeAdj[3]);        		
        	}
        }
        
        this.sendRtnObject(outputVO);
    }

    /**
     * 依首作/非首作、有推介/無推介判斷規劃書類型
     * 
     * @param commRsYn
     *            推介
     * @param hasInvest
     *            首作
     * @return
     */
    private String getFpsType(String commRsYn, boolean hasInvest){
        StringBuilder result = new StringBuilder();
        result.append("N");
        switch (commRsYn) {
        case "Y":// 有推介
            if (hasInvest)// 非首作
                result.append("4");
            else
                // 首作
                result.append("2");
            break;
        case "N":// 無推介
            if (hasInvest)// 非首作
                result.append("3");
            else
                // 首作
                result.append("1");
            break;
        }

        return result.toString();
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> getBriefList(String fpsType) throws DAOException, JBranchException{
        QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT MANUAL.*");
        sb.append(" FROM TBFPS_RPT_PARA_HEAD HE");
        sb.append(" LEFT JOIN TBFPS_OTHER_PARA_MANUAL MANUAL ON MANUAL.PARAM_NO = HE.PARAM_NO");
        sb.append(" WHERE HE.STATUS = 'A' AND MANUAL.DESC_TYPE = 'F'");
        sb.append(" AND FPS_TYPE = :fpsType"); // 依不同規劃書
        queryCondition.setQueryString(sb.toString());
        queryCondition.setObject("fpsType", fpsType);
        return dam.exeQuery(queryCondition);
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> getManualList(String fpsType) throws DAOException, JBranchException{
        QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT MANUAL.*");
        sb.append(" FROM TBFPS_RPT_PARA_HEAD HE");
        sb.append(" LEFT JOIN TBFPS_OTHER_PARA_MANUAL MANUAL ON MANUAL.PARAM_NO = HE.PARAM_NO");
        sb.append(" WHERE HE.STATUS = 'A' AND MANUAL.DESC_TYPE = 'M'");
        sb.append(" AND FPS_TYPE = :fpsType"); // -- 依不同規劃書
        sb.append(" ORDER BY MANUAL.RANK");
        queryCondition.setQueryString(sb.toString());
        queryCondition.setObject("fpsType", fpsType);
        return dam.exeQuery(queryCondition);
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> getWarningList() throws DAOException, JBranchException{
        QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT CASE WHEN WARN.PRD_TYPE='ETF' THEN 'ETF'");
        sb.append("		WHEN WARN.PRD_TYPE='FUND' THEN '基金'");
        sb.append("		WHEN WARN.PRD_TYPE='INS' THEN '保險'");
        sb.append("		WHEN WARN.PRD_TYPE='NANO' THEN '奈米投'");
        sb.append("   ELSE '固定收益商品' END AS PRD_TYPE, WARNING");
        sb.append(" FROM TBFPS_RPT_PARA_HEAD HE");
        sb.append(" LEFT JOIN TBFPS_OTHER_PARA_WARNING WARN ON WARN.PARAM_NO = HE.PARAM_NO");
        sb.append(" WHERE HE.STATUS = 'A'");
        sb.append(" ORDER BY WARN.PRD_TYPE, WARN.RANK");
        queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
        queryCondition.setQueryString(sb.toString());
        return dam.exeQuery(queryCondition);
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> getRptPicture() throws DAOException, JBranchException, SQLException{
        QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT HE.*");
        sb.append(" FROM TBFPS_RPT_PARA_HEAD HE");
        sb.append(" WHERE HE.STATUS = 'A'");

        queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
        queryCondition.setQueryString(sb.toString());
        List<Map<String, Object>> list = dam.exeQuery(queryCondition);
        for (Map<String, Object> map : list) {
            if (map.get("RPT_PIC") != null) {
                Blob blob = (Blob) map.get("RPT_PIC");
                int blobLength = (int) blob.length();
                byte[] blobAsBytes = blob.getBytes(1, blobLength);
                map.put("RPT_PIC", blobAsBytes);
                blob.free();
            }
        }
        return list;
    }

    // 波動度
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getStockLsByPlanID(DataAccessManager dam, String planID) throws DAOException,
            JBranchException{
        QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
        StringBuffer sb = new StringBuffer();
        sb.append(" SELECT INV.PRD_ID, SUM((INV.PORTFOLIO_RATIO/100))WEIGHT,");
        sb.append(" INV.PRD_TYPE");
        sb.append(" FROM TBFPS_PORTFOLIO_PLAN_INV INV");
        sb.append(" WHERE PLAN_ID = :planID");
        sb.append(" AND INV_PRD_TYPE = '3'");
        sb.append(" GROUP BY PRD_ID, PRD_TYPE");

        qc.setObject("planID", planID);
        qc.setQueryString(sb.toString());
        List<Map<String, Object>> list = dam.exeQuery(qc);

        for (Map<String, Object> cell : list) {
            cell.put("WEIGHT", Double.parseDouble(cell.get("WEIGHT").toString()));
        }
        return list;
    }

    // 歷史績效
    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> getPerformance(DataAccessManager dam, String planID, String type) throws DAOException, JBranchException{
        QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
        qc.setObject("planID", planID);
        qc.setQueryString("MFD".equals(type) ? getMfdSQL() : getDebtSQL());
        return dam.exeQuery(qc);
    }

    /**
     * 參數
     * 
     * @param body
     * @param header
     * @throws JBranchException
     * @throws IOException
     */
    public void getParameter(Object body, IPrimitiveMap<?> header) throws DAOException, JBranchException{
        DataAccessManager dam = this.getDataAccessManager();
        FPS240OutputVO outputVO = new FPS240OutputVO();
        FPS240InputVO inputVO = (FPS240InputVO) body;
        outputVO.setOutputList(getNoticeList(dam, inputVO.getPlanID()));
        this.sendRtnObject(outputVO);
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> getNoticeList(DataAccessManager dam, String planId) throws DAOException,
            JBranchException{
        QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
        StringBuffer sb = new StringBuffer();

        sb.append(" SELECT WARN.PRD_TYPE, WARN.RANK, WARN.WARNING, WARN.FONT");
        sb.append(" FROM TBFPS_RPT_PARA_HEAD HE");
        sb.append(" LEFT JOIN TBFPS_OTHER_PARA_WARNING WARN ON WARN.PARAM_NO = HE.PARAM_NO");
        sb.append(" WHERE HE.STATUS = 'A'");
        sb.append(" GROUP BY WARN.PRD_TYPE, WARN.RANK, WARN.WARNING, WARN.FONT");
        sb.append(" ORDER BY WARN.PRD_TYPE, WARN.RANK");
        qc.setQueryString(sb.toString());
        return dam.exeQuery(qc);
    }

    /**
     * 檢核
     * 
     * @param body
     * @param header
     * @throws JBranchException
     * @throws IOException
     */
    public void checker(Object body, IPrimitiveMap<?> header) throws DAOException, JBranchException{
        DataAccessManager dam = this.getDataAccessManager();
        FPS240InputVO inputVO = (FPS240InputVO) body;
        String checkType = inputVO.getCheckType();
        String checker = "";
        String planID = inputVO.getPlanID();

        switch (checkType) {
        case "print":
            checker = checkPrint(dam, planID);
            break;
        }
        this.sendRtnObject(checker);
    }

    /**
     * NOT USED
     * 
     * @param dam
     * @param planID
     * @return
     * @throws DAOException
     * @throws JBranchException
     */
    @SuppressWarnings("unchecked")
    private String checkPrint(DataAccessManager dam, String planID) throws DAOException, JBranchException{
        QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
        StringBuffer sb = new StringBuffer();

        sb.append(" SELECT COUNT(*) CHECKER");
        sb.append(" FROM TBFPS_PORTFOLIO_PLAN_FILE");
        sb.append(" WHERE PLAN_ID = :planID");

        qc.setObject("planID", planID);
        qc.setQueryString(sb.toString());
        return ((List<Map<String, Object>>) dam.exeQuery(qc)).get(0).get("CHECKER").toString();
    }

    // 刪除贖回 purchase_amt = 0
    public List<Map<String, Object>> rmBackItem(List<Map<String, Object>> ls){
        List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();
        for (Map<String, Object> item : ls) {
            if (((BigDecimal) item.get("PURCHASE_ORG_AMT")).intValue() != 0) {
                resultList.add(item);
            }
        }
        return resultList;
    }

    /**
     * 儲存
     * 
     * @param body
     * @param header
     * @throws JBranchException
     * @throws IOException
     */
    public void save(Object body, IPrimitiveMap<?> header) throws JBranchException{
        // head
        String planID = exeSave(body);

        this.sendRtnObject(planID); // 新增成功
    }

    // update head
    protected void update(Object body, DataAccessManager dam, String planID) throws DAOException, JBranchException{
        FPS240InputVO inputVO = (FPS240InputVO) body;
        TBFPS_PORTFOLIO_PLAN_INV_HEADVO vo = (TBFPS_PORTFOLIO_PLAN_INV_HEADVO) dam.findByPKey(TBFPS_PORTFOLIO_PLAN_INV_HEADVO.TABLE_UID, planID);
        if (vo != null) {
            dam.update(putPlanInvHeadVO(vo, inputVO));
        } else {
            throw new APException("ehl_01_common_009"); // 查無資料
        }
    }

    private TBFPS_PORTFOLIO_PLAN_INV_HEADVO putPlanInvHeadVO(TBFPS_PORTFOLIO_PLAN_INV_HEADVO vo, FPS240InputVO inputVO){
        vo.setPLAN_STATUS(inputVO.getPlanStatus());
        // if (inputVO.getPlanStatus().equals("PRINT_REJECT")) {
        // vo.setVALID_FLAG("N0");
        // }
        return vo;
    }

    /**
     * generatePdf
     * 
     * @throws Exception
     */
    public void generatePdf(Object body, IPrimitiveMap<?> header) throws Exception{
        DataAccessManager dam = this.getDataAccessManager();
        FPSUtilsPdfInputVO inputVO = (FPSUtilsPdfInputVO) body;

        BigDecimal printSEQ = inputVO.getPrintSEQ();
        String action = inputVO.getAction();
        String planID = inputVO.getPlanID();
        String custID = inputVO.getCustID();
        String fileName = inputVO.getFileName();
        String aoCode = inputVO.getAoCode();
        String tempFileName = inputVO.getTempFileName();

        if (printSEQ.compareTo(BigDecimal.ZERO) == -1) {
            printSEQ = FPSUtils.newPDFFile(dam, custID, aoCode, fileName, tempFileName, planID, "INV", action);
        }

        if (("resend").equals(action)) {
            sendMail(dam, FPSUtils.getPDFFile(dam, planID, printSEQ, "INV"), planID, custID, printSEQ, "INV");
        } else if (("download").equals(action)) {
            logger.info(printSEQ.toString());
            downloadFile(dam, FPSUtils.getPDFFile(dam, planID, printSEQ, "INV"), planID, printSEQ, "INV");
        }

        this.sendRtnObject(printSEQ);
    }

    // do file
    public void downloadFile(DataAccessManager dam, List<Map<String, Object>> result, String planID, BigDecimal seq,
            String sppType) throws JBranchException{
        if (!result.isEmpty()) {
            String filename = (String) result.get(0).get("FILE_NAME");
            byte[] blobarray;
            try {
                blobarray = ObjectUtil.blobToByteArr((Blob) result.get(0).get("PLAN_PDF_FILE"));
            } catch (Exception e) {
                throw new APException("文件下載錯誤！"); // 文件下載錯誤！
            }
            String temp_path = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH);
            String temp_path_relative = (String) SysInfo.getInfoValue(SystemVariableConsts.TEMP_PATH_RELATIVE);
            FileOutputStream download_file = null;
            File tmpdirPath = new File(temp_path);

            try {
                download_file = new FileOutputStream(new File(temp_path, filename));
                download_file.write(blobarray);
                FPSUtils.execLog(dam, planID, seq, "P", sppType);
            } catch (Exception e) {
                e.printStackTrace();
                throw new APException("文件下載錯誤！"); // 下載檔案失敗
            } finally {
                try {
                    download_file.close();
                } catch (Exception e) {
                    throw new APException("產出檔案錯誤!"); // 產出檔案錯誤
                    // IGNORE
                }

                logger.info("default" + temp_path + filename);
                File[] tmpFiles = tmpdirPath.listFiles();

                if (!ArrayUtils.isEmpty(tmpFiles)) {
                    for (File tmpFile : tmpFiles) {
                        logger.info(tmpFile.getPath());
                    }
                }
            }

            Date now = new Date();
            notifyClientToDownloadFile(temp_path_relative + "//" + filename, sdf.format(now) + "全資產理財規劃書.pdf");
        } else {
            throw new APException("無可下載的檔案!"); // 無可下載的檔案
        }
    }

    // 轉寄
    public void sendMail(DataAccessManager dam, List<Map<String, Object>> result, String planID, String custID,
            BigDecimal seq, String sppType) throws Exception{

        // 全資產規畫
        boolean isSuccess = FPSUtils.sendMain(dam, result, FPSUtils.getCustEmail(dam, custID), FPSUtils.getMailContent(0));
        if (isSuccess) {
            FPSUtils.execLog(dam, planID, seq, "E", sppType);
        } else {
            throw new APException("E-Mail 發送失敗!");
        }
    }

    // DEMO
    // 歷史績效
    @SuppressWarnings("unchecked")
    public void getPerformanceDemo(Object body, IPrimitiveMap<?> header) throws DAOException, JBranchException{
    	Map<String, Object> param = (Map<String, Object>)body;
    	FPS240OutputVO outputVO = new FPS240OutputVO();
    	dam = this.getDataAccessManager();
        QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
        StringBuffer sb = new StringBuffer();
        sb.append(" SELECT RM.PRD_ID, FU.FUND_CNAME AS PRD_CNAME, FU.CURRENCY_STD_ID AS CURRENCY_TYPE, RM.RETURN_3M, RM.RETURN_6M,");
        sb.append(" RM.RETURN_1Y, RM.RETURN_2Y, RM.RETURN_3Y, RM.RETURN_LTM, RM.VOLATILITY");
        sb.append(" FROM TBFPS_PRD_RETURN_M RM");
        sb.append(" LEFT JOIN TBPRD_FUND FU ON FU.PRD_ID = RM.PRD_ID");
        sb.append(" WHERE RM.DATA_YEARMONTH = (SELECT MAX(DATA_YEARMONTH) FROM TBFPS_PRD_RETURN_M WHERE PRD_TYPE = 'MFD')");
        sb.append(" AND RM.PRD_TYPE = 'MFD'");
        sb.append(" AND RM.PRD_ID IN (:prodList)");

        qc.setObject("prodList", param.get("prodList"));
        qc.setQueryString(sb.toString());
        outputVO.setMFDPerformanceList(dam.exeQuery(qc));
        this.sendRtnObject(outputVO);
    }
    
    public Map<String,Object> getCustRisKPct(DataAccessManager dam) throws DAOException, JBranchException {
    	Map<String, Object> custRiskPctMap = new HashMap<String, Object>();
    	QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
    	qc.setQueryString(getCustRiskPctSQL());
    	List<Map<String, Object>> resultList = dam.exeQuery(qc);
    	if(CollectionUtils.isNotEmpty(resultList)) {
    		for(Map<String, Object> map : resultList) {
    			custRiskPctMap.put(ObjectUtils.toString(map.get("CUST_RISK_ATR")), map.get("VOLATILITY"));
    		}
    	}
    	return custRiskPctMap;
    }
    
    private final static String getCustRiskPctSQL() {
    	StringBuffer sb = new StringBuffer();
    	sb.append(" select body.cust_risk_atr CUST_RISK_ATR, body.reinv_stock_vol VOLATILITY "); 
    	sb.append(" from TBFPS_CUSTRISK_VOLATILITY_HEAD head");
    	sb.append(" inner join TBFPS_CUSTRISK_VOLATILITY body on head.param_no = body.param_no");
    	sb.append(" where head.status = 'A'");
    	return sb.toString();
    }
    
    private final static String getMfdSQL() {
    	StringBuffer sb = new StringBuffer();
    	sb.append(" SELECT distinct RM.PRD_ID, '基金' PRD_TYPE, FU.FUND_CNAME AS PRD_CNAME, FU.CURRENCY_STD_ID AS CURRENCY_TYPE, RM.RETURN_3M, RM.RETURN_6M, ");
    	sb.append(" RM.RETURN_1Y, RM.RETURN_2Y, RM.RETURN_3Y, RM.RETURN_LTM, RM.VOLATILITY, PARAM.PARAM_NAME FREQUENCY, decode(RD.PRD_NAV, null, '--', RD.PRD_NAV) PRD_NAV ");
    	sb.append(" FROM TBFPS_PRD_RETURN_M RM ");
    	sb.append(" LEFT JOIN TBPRD_FUND FU ON FU.PRD_ID = RM.PRD_ID ");
    	sb.append(" INNER JOIN TBFPS_PORTFOLIO_PLAN_INV PLAN_INV on PLAN_INV.PRD_ID = RM.PRD_ID and PLAN_ID = :planID AND PLAN_INV.PRD_TYPE='MFD' ");
    	sb.append(" LEFT JOIN TBSYSPARAMETER PARAM ON PARAM.PARAM_TYPE = 'FPS.DIVIDEND_FREQUENCY' and FU.DIVIDEND_FREQUENCY = PARAM.PARAM_CODE ");
    	sb.append(" LEFT JOIN TBFPS_PRD_RETURN_D RD ON RD.DATA_DATE = (SELECT MAX(DATA_DATE) FROM TBFPS_PRD_RETURN_D WHERE PRD_TYPE = 'MFD' and PRD_ID = PLAN_INV.PRD_ID) and RD.PRD_ID = RM.PRD_ID and RD.PRD_TYPE = 'MFD' ");
    	sb.append(" WHERE RM.DATA_YEARMONTH = (SELECT MAX(DATA_YEARMONTH) FROM TBFPS_PRD_RETURN_M WHERE PRD_TYPE = 'MFD' and PRD_ID = PLAN_INV.PRD_ID) ");
    	sb.append(" AND RM.PRD_TYPE = 'MFD'");
    	sb.append(" union");
    	sb.append(" SELECT distinct RM.PRD_ID, 'ETF' PRD_TYPE, ETF.ETF_CNAME AS PRD_CNAME, ETF.CURRENCY_STD_ID AS CURRENCY_TYPE, RM.RETURN_3M, RM.RETURN_6M, ");
    	sb.append(" RM.RETURN_1Y, RM.RETURN_2Y, RM.RETURN_3Y, RM.RETURN_LTM, RM.VOLATILITY, '--' FREQUENCY, decode(RD.PRD_NAV, null, '--', RD.PRD_NAV) PRD_NAV ");
    	sb.append(" FROM TBFPS_PRD_RETURN_M RM ");
    	sb.append(" LEFT JOIN TBPRD_ETF ETF ON ETF.PRD_ID = RM.PRD_ID ");
    	sb.append(" INNER JOIN TBFPS_PORTFOLIO_PLAN_INV PLAN_INV on PLAN_INV.PRD_ID = RM.PRD_ID and PLAN_ID = :planID AND PLAN_INV.PRD_TYPE='ETF' ");
    	sb.append(" LEFT JOIN TBFPS_PRD_RETURN_D RD ON RD.DATA_DATE = (SELECT MAX(DATA_DATE) FROM TBFPS_PRD_RETURN_D WHERE PRD_TYPE = 'ETF' ");
    	sb.append(" and PRD_ID = PLAN_INV.PRD_ID) and RD.PRD_ID = RM.PRD_ID and RD.PRD_TYPE = 'MFD' ");
    	sb.append(" WHERE RM.DATA_YEARMONTH = (SELECT MAX(DATA_YEARMONTH) FROM TBFPS_PRD_RETURN_M WHERE PRD_TYPE = 'ETF' and PRD_ID = PLAN_INV.PRD_ID) ");
    	sb.append(" AND RM.PRD_TYPE = 'ETF'");
        return sb.toString();
    }
    
    private final static String getDebtSQL() {
    	StringBuffer sb = new StringBuffer();
    	sb.append(" with lastBND as ( ");
    	sb.append(" 	select a.BDPE1 PRD_ID, ");
    	sb.append(" 	 CASE b.FREQUENCY_OF_INTEST_PAY WHEN 1 THEN a.BDPE9/2 WHEN 3 THEN a.BDPE9/4 WHEN 4 THEN a.BDPE9/12 ELSE a.BDPE9 END AS BDPE9  ");
    	sb.append(" 	 from TBPRD_BDS650 a left join TBPRD_BONDINFO b on a.BDPE1 = b.PRD_ID where a.BDPE1 in (SELECT PRD_ID FROM TBFPS_PORTFOLIO_PLAN_INV WHERE PLAN_ID = :planID AND PRD_TYPE = 'BND') ");
    	sb.append(" 	 and a.snap_date = (select max(snap_date) from TBPRD_BDS650 where BDPE1 in (SELECT PRD_ID FROM TBFPS_PORTFOLIO_PLAN_INV WHERE PLAN_ID = :planID AND PRD_TYPE = 'BND'))  ");
    	sb.append(" 	 and a.BDPE2 <= sysdate order by a.BDPE2 desc fetch first 1 rows only ");
    	sb.append(" 	), lastSN as ( ");
    	sb.append(" 	 select a.BDPE1 PRD_ID, CASE b.FREQUENCY_OF_INTEST_PAY WHEN 1 THEN a.BDPE9/2 WHEN 3 THEN a.BDPE9/4 WHEN 4 THEN a.BDPE9/12 ELSE a.BDPE9 END AS BDPE9  ");
    	sb.append(" 	 from TBPRD_BDS650 a left join TBPRD_SNINFO b on a.BDPE1 = b.PRD_ID  ");
    	sb.append(" 	 where a.BDPE1 in (SELECT PRD_ID FROM TBFPS_PORTFOLIO_PLAN_INV WHERE PLAN_ID = :planID AND PRD_TYPE = 'BND') and TRUNC(a.BDPE2) <= TRUNC(SYSDATE) and a.BDPEA is not null  ");
    	sb.append(" 	 and a.snap_date = (select max(snap_date) from TBPRD_BDS650 ) and a.BDPE2 <= sysdate order by a.BDPE2 desc fetch first 1 rows only ");
    	sb.append(" 	), lastSI as ( ");
    	sb.append("     select PLADTE, PLALAM/IVAMT2*100 as BDPE9 ,SDPRD from (   ");
    	sb.append(" 	 select A.SDPRD, sum(A.IVAMT2) IVAMT2, B.PLADTE, B.PLALAM from  ");
    	sb.append(" 	 (SELECT T1.IVRDTE, T1.SDPRD, SUM(T1.IVAMT2) IVAMT2 FROM  ");
    	sb.append(" 	 (select  ");
    	sb.append("          case ivsts1  ");
    	sb.append("          when 'S3' then to_char(sysdate+1,'yyyy/mm/dd')   ");
    	sb.append("         when 'S4' then to_char(ivdte1,'yyyy/mm/dd')   ");
    	sb.append("         when 'S5' then to_char(ivdte4,'yyyy/mm/dd')   ");
    	sb.append("         when 'S9' then to_char(ivdte4,'yyyy/mm/dd')   ");
    	sb.append("         end IVRDTE, SDPRD, IVAMT2  ");
    	sb.append(" 	 from TBPMS_SDINVMP0_day  ");
    	sb.append(" 	where snap_date = (select max(snap_date) from TBPMS_SDINVMP0_day) ");
    	sb.append(" 	and SDPRD in ((SELECT PRD_ID FROM TBFPS_PORTFOLIO_PLAN_INV WHERE PLAN_ID = :planID AND PRD_TYPE='SI')) "); //先判 PRD
    	sb.append(" 	and IVSTS1 IN ('S3','S4','S5','S9')) T1  ");
    	sb.append("    GROUP BY T1.IVRDTE, T1.SDPRD) a,   ");
    	sb.append(" 	 (select f.PLAPRD, sum(f.PLALAM) as PLALAM, to_char(f.PLADTE,'yyyy/mm/dd') as PLADTE  ");
    	sb.append(" 	 from (select * from TBPRD_SIDIVIDEND where import_flag='F' ");
    	sb.append(" 	and PLAPRD  in ((SELECT PRD_ID FROM TBFPS_PORTFOLIO_PLAN_INV WHERE PLAN_ID = :planID AND PRD_TYPE='SI'))) f "); //先判 PRD
    	sb.append(" 	where f.snap_date = (select max(snap_date) from (select * from TBPRD_SIDIVIDEND where import_flag='F' ");
    	sb.append("		and PLAPRD  in ((SELECT PRD_ID FROM TBFPS_PORTFOLIO_PLAN_INV WHERE PLAN_ID = :planID AND PRD_TYPE='SI')) )) "); //先判 PRD
    	sb.append(" 	  and (f.platyp='1' or f.platyp='2' or (f.platyp='F' and EXISTS  ");
    	sb.append(" 	 (select G.ivdte4 from TBPMS_SDINVMP0_day G WHERE F.PLAPRD = G.SDPRD AND F.PLADTE = G.IVDTE4 ");
    	sb.append(" 	and SDPRD in ((SELECT PRD_ID FROM TBFPS_PORTFOLIO_PLAN_INV WHERE PLAN_ID = :planID AND PRD_TYPE='SI')) )) "); //先判 PRD
    	sb.append("     )  ");
    	sb.append(" 	 group by f.PLAPRD, f.PLADTE order by f.PLAPRD) b   ");
    	sb.append(" 	 where a.SDPRD = b.PLAPRD   ");
    	sb.append(" 	 AND A.IVRDTE >= B.PLADTE   ");
    	sb.append(" 	 AND b.PLAPRD in ((SELECT PRD_ID FROM TBFPS_PORTFOLIO_PLAN_INV WHERE PLAN_ID = :planID AND PRD_TYPE='SI'))  ");
    	sb.append(" 	 GROUP BY A.SDPRD, B.PLADTE, B.PLALAM   ");
    	sb.append(" 	 ORDER BY B.PLADTE,A.SDPRD) ");
    	sb.append("      left join TBPRD_SIINFO sif on sif.PRD_ID = SDPRD  ");
    	sb.append("       where sif.PRD_ID in (SELECT PRD_ID FROM TBFPS_PORTFOLIO_PLAN_INV WHERE PLAN_ID = :planID AND PRD_TYPE='SI') ");
    	sb.append("       and PLADTE <= to_char(SYSDATE,'yyyy/mm/dd') ) ");
    	sb.append(" select distinct '海外債' PRD_TYPE, M.PRD_ID, M.BOND_CNAME, ROUND((M.DATE_OF_MATURITY - SYSDATE) / 365, 2) AS DATE_OF_MATURITY, PARAM.PARAM_NAME AS FREQUENCY_OF_INTEST_PAY ");
    	sb.append(" 	,BONDPRICE.BUY_PRICE BUY_PRICE, lastBND.BDPE9 lastRate ");
    	sb.append(" 	from TBPRD_BOND M ");
    	sb.append(" 	    inner join TBPRD_BONDINFO D on M.PRD_ID = D.PRD_ID ");
    	sb.append(" 	    left join TBSYSPARAMETER PARAM ON PARAM.PARAM_TYPE = 'FPS.DIVIDEND_FREQ_BOND' and TO_CHAR(D.FREQUENCY_OF_INTEST_PAY) = PARAM.PARAM_CODE ");
    	sb.append(" 		left join (select a.PRD_ID, a.BARGAIN_DATE, a.BUY_PRICE from TBPRD_BONDPRICE a  ");
    	sb.append(" 		inner join (select PRD_ID, max(BARGAIN_DATE) BARGAIN_DATE from TBPRD_BONDPRICE group by PRD_ID) b on a.PRD_ID = b.PRD_ID and a.BARGAIN_DATE = b.BARGAIN_DATE) BONDPRICE on BONDPRICE.PRD_ID = M.PRD_ID  ");
    	sb.append("     			left join lastBND on M.PRD_ID = lastBND.PRD_ID ");
    	sb.append(" where M.PRD_ID in (SELECT PRD_ID FROM TBFPS_PORTFOLIO_PLAN_INV WHERE PLAN_ID = :planID AND PRD_TYPE='BND') and M.DATE_OF_MATURITY >=sysdate ");
    	sb.append(" union ");
    	sb.append(" select distinct 'SN' PRD_TYPE, M.PRD_ID, M.SN_CNAME, ROUND((M.DATE_OF_MATURITY - SYSDATE) / 365, 2) AS DATE_OF_MATURITY, PARAM.PARAM_NAME AS FREQUENCY_OF_INTEST_PAY   ");
    	sb.append(" 	,BONDPRICE.BUY_PRICE BUY_PRICE, lastSN.BDPE9 lastRate ");
    	sb.append(" from TBPRD_SN M ");
    	sb.append(" 	inner join TBPRD_SNINFO D on M.PRD_ID = D.PRD_ID ");
    	sb.append("       left join TBSYSPARAMETER PARAM ON PARAM.PARAM_TYPE = 'FPS.DIVIDEND_FREQ_BOND' and TO_CHAR(D.FREQUENCY_OF_INTEST_PAY) = PARAM.PARAM_CODE ");
    	sb.append(" left join (select a.PRD_ID, a.BARGAIN_DATE, a.BUY_PRICE from TBPRD_BONDPRICE a inner join (select PRD_ID, max(BARGAIN_DATE) BARGAIN_DATE  ");
    	sb.append(" from TBPRD_BONDPRICE group by PRD_ID) b on a.PRD_ID = b.PRD_ID and a.BARGAIN_DATE = b.BARGAIN_DATE) BONDPRICE on BONDPRICE.PRD_ID = M.PRD_ID  ");
    	sb.append(" 		left join lastSN on M.PRD_ID = lastSN.PRD_ID ");
    	sb.append(" 	where M.PRD_ID in (SELECT PRD_ID FROM TBFPS_PORTFOLIO_PLAN_INV WHERE PLAN_ID = :planID AND PRD_TYPE='SN') and M.DATE_OF_MATURITY >=sysdate ");
    	sb.append(" union ");
    	sb.append(" select distinct 'SI' PRD_TYPE, M.PRD_ID, M.SI_CNAME, ROUND((M.DATE_OF_MATURITY - SYSDATE) / 365, 2) AS DATE_OF_MATURITY, FREQUENCY_OF_INTEST_PAY || ' 次/年' FREQUENCY_OF_INTEST_PAY ");
    	sb.append(" ,SIPRICE.SDAMT3 BUY_PRICE, lastSI_T.BDPE9 lastRate ");
    	sb.append(" from TBPRD_SI M ");
    	sb.append(" 	inner join TBPRD_SIINFO D on M.PRD_ID = D.PRD_ID ");
    	sb.append("     left join (select a.PLADTE, a.BDPE9 ,a.SDPRD from lastSI a inner join (select SDPRD, max(PLADTE) PLADTE ");
    	sb.append("     from lastSI group by SDPRD) b on a.SDPRD = b.SDPRD and a.PLADTE = b.PLADTE) lastSI_T on lastSI_T.SDPRD = M.PRD_ID ");
    	sb.append(" 	left join (select a.SDPRD PRD_ID, SDAMT3 from TBPRD_SIPRICE a inner join (select SDPRD PRD_ID, max(SDDTE) SDDTE ");
    	sb.append(" from TBPRD_SIPRICE group by SDPRD) b on a.SDPRD = b.PRD_ID and a.SDDTE = b.SDDTE) SIPRICE on SIPRICE.PRD_ID = M.PRD_ID ");
    	sb.append(" where M.PRD_ID in (SELECT PRD_ID FROM TBFPS_PORTFOLIO_PLAN_INV WHERE PLAN_ID = :planID AND PRD_TYPE='SI') and M.DATE_OF_MATURITY >=sysdate ");
    	return sb.toString();
    }
}
