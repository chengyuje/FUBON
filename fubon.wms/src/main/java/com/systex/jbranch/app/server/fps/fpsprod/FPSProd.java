package com.systex.jbranch.app.server.fps.fpsprod;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.systex.jbranch.app.server.fps.prd120.PRD120;
import com.systex.jbranch.comutil.collection.CustomComparator;
import com.systex.jbranch.platform.common.util.PlatformContext;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.DAOException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.util.IPrimitiveMap;

@Component("fpsprod")
@Scope("request")
public class FPSProd extends FubonWmsBizLogic {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    public void inquire(Object body, IPrimitiveMap header) throws Exception{
        FPSProdInputVO inputVO = (FPSProdInputVO) body;
        FPSProdOutputVO outputVO = new FPSProdOutputVO();
        DataAccessManager dam = this.getDataAccessManager();

        if (StringUtils.isBlank(inputVO.getType())) {
            throw new APException("no type");
        }

        List<Map<String, Object>> prodList = new ArrayList<Map<String, Object>>();

        switch (inputVO.getType()) {
        case "currency":
            prodList = getCurrency(dam, inputVO);
            break;
        case "MFD":
            prodList = getMFD(dam, inputVO);
            break;
        case "savingIns":
            prodList = getSavingIns(dam, inputVO);
            break;
        case "bond":
            prodList = getBond(dam, inputVO);
            break;
        case "SI":
            prodList = getSI(dam, inputVO);
            break;
        case "SN":
            prodList = getSN(dam, inputVO);
            break;
        case "ETF":
            prodList = getETF(dam, inputVO);
            break;
        case "investIns":
            prodList = getInvestIns(dam, inputVO);
            break;
        case "insTarget":
            prodList = getInsTarget(dam, inputVO);
            break;
        }

        if (prodList.size() <= 0) {
            throw new APException("查無該商品資訊");
        }

        outputVO.setProdList(prodList);
        this.sendRtnObject(outputVO);
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> getCurrency(DataAccessManager dam, FPSProdInputVO inputVO) throws DAOException,
            JBranchException{
        QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
        StringBuffer sb = new StringBuffer();

        sb.append(" SELECT PA.DEPOSIT_CURR");
        sb.append(" FROM TBFPS_OTHER_PARA_HEAD HD");
        sb.append(" LEFT JOIN TBFPS_OTHER_PARA PA ON HD.PARAM_NO = PA.PARAM_NO");
        sb.append(" WHERE HD.STATUS = 'A'");
        sb.append(" AND SYSDATE BETWEEN NVL(HD.EFFECT_START_DATE, TO_DATE('1999/01/01', 'yyyy-MM-dd')) AND NVL(HD.EFFECT_END_DATE, TO_DATE('2999/01/01', 'yyyy-MM-dd'))");

        qc.setQueryString(sb.toString());
        return dam.exeQuery(qc);
    }

    @SuppressWarnings("unchecked")
    public static List<Map<String, Object>> getMFD(DataAccessManager dam, FPSProdInputVO inputVO) throws DAOException,
            JBranchException{
        QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
        StringBuffer sb = new StringBuffer();

        sb.append(" WITH GEN AS (");
        sb.append(" SELECT PARAM_CODE,PARAM_NAME FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'SOT.NF_MIN_BUY_AMT_1'),");
        sb.append(" SML AS (");
        sb.append(" SELECT PARAM_CODE,PARAM_NAME FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'SOT.NF_MIN_BUY_AMT_2'),");
        sb.append(" SYSPAR AS (");
        sb.append(" SELECT PARAM_CODE,PARAM_NAME FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'FPS.FUND_TYPE'),");
        sb.append(" AREA AS (");
        sb.append(" SELECT PARAM_CODE,PARAM_NAME FROM TBSYSPARAMETER WHERE PARAM_TYPE = 'PRD.MKT_TIER3'),");
        sb.append(" TREND AS (");
        sb.append(" SELECT TRD.TYPE, TRD.TREND, TRD.OVERVIEW");
        sb.append(" FROM TBFPS_MARKET_TREND TR");
        sb.append(" LEFT JOIN TBFPS_MARKET_TREND_DETAIL TRD ON TR.PARAM_NO = TRD.PARAM_NO");
        sb.append(" WHERE TR.STATUS = 'A'),");
        sb.append(" RM AS (");
        sb.append(" SELECT PRD_ID, RETURN_3M, RETURN_6M, RETURN_1Y, RETURN_3Y, VOLATILITY");
        sb.append(" FROM TBFPS_PRD_RETURN_M");
        sb.append(" WHERE PRD_TYPE = 'MFD'");
        sb.append(" AND DATA_YEARMONTH = (SELECT MAX(DATA_YEARMONTH) FROM TBFPS_PRD_RETURN_M WHERE PRD_TYPE='MFD'))");
        sb.append(" SELECT INFO.STOCK_BOND_TYPE, ");
        sb.append(" CASE WHEN (TRUNC(SYSDATE) BETWEEN INFO.MAIN_PRD_SDATE AND INFO.MAIN_PRD_EDATE) THEN 'Y'");
        sb.append(" WHEN (TRUNC(SYSDATE) BETWEEN INFO.RAISE_FUND_SDATE AND INFO.RAISE_FUND_EDATE) THEN 'Y'");
        sb.append(" ELSE 'N' END AS CROWN,");
        sb.append(" MAIN.PRD_ID,");
        sb.append(" MAIN.FUND_CNAME,");
        sb.append(" MAIN.RISKCATE_ID,");
        sb.append(" MAIN.CURRENCY_STD_ID,");
        sb.append(" MAIN.DIVIDEND_FREQUENCY,");
        sb.append(" TREND.TREND AS CIS_3M,");
        sb.append(" TREND.OVERVIEW,");
        sb.append(" GEN.PARAM_NAME AS GEN_SUBS_MINI_AMT_FOR,");
        sb.append(" SML.PARAM_NAME AS SML_SUBS_MINI_AMT_FOR,");
        sb.append(" MAIN.FUND_TYPE,");
        sb.append(" SYSPAR.PARAM_NAME AS FUND_TYPE_NAME,");
        sb.append(" MAIN.INV_AREA AS MF_MKT_CAT,");
        sb.append(" AREA.PARAM_NAME AS NAME,");
        sb.append(" ROUND(RM.RETURN_3M,2) AS RETURN_3M, ROUND(RM.RETURN_6M,2) AS RETURN_6M, ROUND(RM.RETURN_1Y,2) AS RETURN_1Y,");
        sb.append(" ROUND(RM.RETURN_3Y,2) AS RETURN_3Y, ROUND(RM.VOLATILITY,2) AS VOLATILITY,");
        sb.append(" p1.PARAM_ORDER PORDER1,");
        sb.append(" p2.PARAM_ORDER PORDER2 ");
        sb.append(" FROM TBPRD_FUND MAIN");
        sb.append(" INNER JOIN TBPRD_FUNDINFO INFO ON MAIN.PRD_ID = INFO.PRD_ID");
        sb.append(" LEFT JOIN TREND ON TREND.TYPE = MAIN.INV_TARGET");
        sb.append(" LEFT JOIN GEN ON GEN.PARAM_CODE = MAIN.CURRENCY_STD_ID");
        sb.append(" LEFT JOIN SML ON SML.PARAM_CODE = MAIN.CURRENCY_STD_ID");
        sb.append(" LEFT JOIN SYSPAR ON SYSPAR.PARAM_CODE = MAIN.FUND_TYPE");
        sb.append(" LEFT JOIN AREA ON AREA.PARAM_CODE = TREND.TYPE");
        sb.append(" LEFT JOIN RM ON MAIN.PRD_ID = RM.PRD_ID");
        sb.append(" left join (select PARAM_CODE, PARAM_ORDER from TBSYSPARAMETER where PARAM_TYPE = 'PRD.FUND_PROJECT') p1 on MAIN.PROJECT1 = p1.PARAM_CODE "); //專案名稱1 取param_order
        sb.append(" left join (select PARAM_CODE, PARAM_ORDER from TBSYSPARAMETER where PARAM_TYPE = 'PRD.FUND_PROJECT') p2 on MAIN.PROJECT2 = p2.PARAM_CODE "); //專案名稱2 取param_order
        sb.append(" WHERE MAIN.IS_SALE = '1'");

        if(inputVO.getStockBondType() != null) {
            sb.append(" AND INFO.STOCK_BOND_TYPE = :stockBondType ");
            qc.setObject("stockBondType", inputVO.getStockBondType());
        }

        // OBU
        // OBU_BUY 限OBU申購。此欄位為N時禁止OBU客戶申購。
        if (!StringUtils.isBlank(inputVO.getOBU()) && "Y".equals(inputVO.getOBU())) {
            sb.append(" AND (NVL(MAIN.OBU_BUY,'N') != 'N' AND MAIN.CURRENCY_STD_ID != 'TWD')");
        }
        //#1339 基金標籤 主題&專案名稱  2022.1028
        if (!StringUtils.isBlank(inputVO.getFund_subject())) {
            sb.append("and REGEXP_LIKE(MAIN.SUBJECT1 || MAIN.SUBJECT2 || MAIN.SUBJECT3 , :fund_subject) ");
//      		sb.append("and (MAIN.SUBJECT1 = :fund_subject or MAIN.SUBJECT2 = :fund_subject or MAIN.SUBJECT3 = :fund_subject) ");
            qc.setObject("fund_subject", inputVO.getFund_subject().replace(";", "|"));
        }
        if (!StringUtils.isBlank(inputVO.getFund_project())) {
            sb.append("and REGEXP_LIKE(MAIN.PROJECT1 || MAIN.PROJECT2, :fund_project) ");
//      		sb.append("and (MAIN.PROJECT1 = :fund_project or MAIN.PROJECT2 = :fund_project) ");
            qc.setObject("fund_project", inputVO.getFund_project().replace(";", "|"));
        }
        //#1404 客群標籤
        if (!StringUtils.isBlank(inputVO.getFund_customer_level())) {
            sb.append("and REGEXP_LIKE(MAIN.CUSTOMER_LEVEL, :fund_customer_level) ");
            qc.setObject("fund_customer_level", inputVO.getFund_customer_level().replace(";", "|"));
        }

        // only ranked
        if (StringUtils.isNotBlank(inputVO.getIsRanked())) {
            sb.append(" AND ((TRUNC(SYSDATE) BETWEEN INFO.MAIN_PRD_SDATE AND NVL(INFO.MAIN_PRD_EDATE, SYSDATE))");
            sb.append(" OR (TRUNC(SYSDATE) BETWEEN INFO.RAISE_FUND_SDATE AND NVL(INFO.RAISE_FUND_EDATE, SYSDATE)))");
        } else {
            setMDFQuery(sb, qc, inputVO);
        }

        sb.append(" AND TO_NUMBER(SUBSTR(MAIN.RISKCATE_ID,2,1)) <= :riskNum");
        qc.setObject("riskNum", Integer.parseInt(inputVO.getRiskType().substring(1, 2)));

        sb.append(" ORDER BY CROWN DESC,");
        sb.append(" least(coalesce(PORDER1,PORDER2),coalesce(PORDER2,PORDER1)), "); // 1
        sb.append(" case when PORDER1 is not null and PORDER2 is not null then '1' when PORDER1 is null and PORDER2 is null then '3' else '2' end "); // 2

        qc.setQueryString(sb.toString());
        return dam.exeQuery(qc);
    }

    @SuppressWarnings("unchecked")
    public static List<Map<String, Object>> getETF(DataAccessManager dam, FPSProdInputVO inputVO) throws DAOException,
            JBranchException{
        QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
        StringBuffer sb = new StringBuffer();

        sb.append(" WITH TREND AS (");
        sb.append("  SELECT TRD.TYPE, TRD.TREND, TRD.OVERVIEW");
        sb.append("  FROM TBFPS_MARKET_TREND TR");
        sb.append("  LEFT JOIN TBFPS_MARKET_TREND_DETAIL TRD ON TR.PARAM_NO = TRD.PARAM_NO");
        sb.append("  WHERE TR.STATUS = 'A')");
        sb.append(" SELECT MAIN.PRD_RANK, MAIN.STOCK_CODE, MAIN.TXN_AMOUNT,");
        sb.append(" MAIN.PRD_ID, MAIN.ETF_CNAME, MAIN.RISKCATE_ID, MAIN.CURRENCY_STD_ID, MAIN.PROJECT, ");
        sb.append(" TREND.TREND CIS_3M, TREND.OVERVIEW ");
        sb.append(" FROM TBPRD_ETF MAIN");
        sb.append(" LEFT JOIN TBPRD_FUND FU ON FU.PRD_ID = MAIN.PRD_ID");
        sb.append(" LEFT JOIN TREND ON TREND.TYPE = MAIN.INV_TARGET");
        sb.append(" WHERE 1=1");
        sb.append(" AND MAIN.IS_SALE IS NULL");

        sb.append(" AND MAIN.STOCK_BOND_TYPE = :stockBondType ");
        qc.setObject("stockBondType", inputVO.getStockBondType());

        //#1404 客群、專案標籤
        if (!StringUtils.isBlank(inputVO.getEtf_project())) {
            sb.append("and REGEXP_LIKE(MAIN.PROJECT, :etf_project) ");
            qc.setObject("etf_project", inputVO.getEtf_project().replace(";", "|"));
        }
        if (!StringUtils.isBlank(inputVO.getEtf_customer_level())) {
            sb.append("and REGEXP_LIKE(MAIN.CUSTOMER_LEVEL, :etf_customer_level) ");
            qc.setObject("etf_customer_level", inputVO.getEtf_customer_level().replace(";", "|"));
        }


        // OBU 專投
        if (!StringUtils.isBlank(inputVO.getOBU())) {
            sb.append(" AND MAIN.DBU_OBU != " + (("Y").equals(inputVO.getOBU()) ? "'D'" : "'O'"));
        }
        // 專投
        if (!StringUtils.isBlank(inputVO.getIsPro()) && !inputVO.getIsPro().equals("Y")) {
            sb.append(" AND NVL(TRIM(MAIN.PI_BUY), 'N') = 'N'");
        }

        // only ranked
        if (StringUtils.isNotBlank(inputVO.getIsRanked())) {
            sb.append(" AND MAIN.PRD_RANK IS NOT NULL");
        } else {
            setETFQuery(sb, qc, inputVO);
        }

        sb.append(" AND TO_NUMBER(SUBSTR(MAIN.RISKCATE_ID,2,1)) <= :riskNum");
        qc.setObject("riskNum", Integer.parseInt(inputVO.getRiskType().substring(1, 2)));

//        sb.append(" ORDER BY MAIN.PRD_RANK"); 改用 Java 排序

        qc.setQueryString(sb.toString());
        List<Map<String, Object>> list = dam.exeQuery(qc);

        PRD120 prd120 = PlatformContext.getBean(PRD120.class);
        Map<String, Object> tagsMap = prd120.getProjectTagsMap("PRD.ETF_PROJECT");

        for (Map<String, Object> eachRaw: list) {
            prd120.orderProjectTags(eachRaw, tagsMap);
        }

        Collections.sort(list, new CustomComparator<>(
        		CustomComparator.byField("PRD_RANK", CustomComparator.ORDER.ASC),
                PRD120.projComparator(tagsMap)
        ));

        return list;
    }

    @SuppressWarnings("unchecked")
    public static List<Map<String, Object>> getBond(DataAccessManager dam, FPSProdInputVO inputVO) throws DAOException,
            JBranchException{
        QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
        StringBuffer sb = new StringBuffer();
        sb.append(" SELECT");
        sb.append(" CASE WHEN MAIN.PRD_RANK IS NOT NULL THEN 'Y' ELSE 'N' END AS PRD_RANK,");
        sb.append(" MAIN.PRD_RANK AS RAW_PRD_RANK,");
        sb.append(" MAIN.PRD_ID,");
        sb.append(" MAIN.BOND_CNAME,");
        sb.append(" MAIN.CURRENCY_STD_ID,");
        sb.append(" MAIN.RISKCATE_ID,");
        sb.append(" ROUND((MAIN.DATE_OF_MATURITY - SYSDATE) / 365, 2) AS DATE_OF_MATURITY,");
        sb.append(" DE.BOND_CREDIT_RATING_SP,");
        sb.append(" MAIN.YTM,");
        sb.append(" MAIN.BOND_CATE_ID,");
        sb.append(" DE.BASE_AMT_OF_PURCHASE,");
        sb.append(" DE.BASE_AMT_OF_BUYBACK,");
        sb.append(" DE.UNIT_OF_VALUE, ");
        sb.append(" MAIN.PROJECT ");
        sb.append(" FROM TBPRD_BOND MAIN");
        sb.append(" LEFT JOIN TBPRD_BONDINFO DE ON MAIN.PRD_ID = DE.PRD_ID");
        sb.append(" LEFT JOIN TBSYSPARAMETER PARAM ON DE.BOND_CREDIT_RATING_SP = PARAM.PARAM_CODE");
        sb.append("   AND PARAM.PARAM_TYPE = 'PRD.CREDIT_RATING_SP_DTL'");
        sb.append(" WHERE 1=1");
        sb.append(" AND not exists(select 1 from TBPRD_CUSTOMIZED CM where CM.PRD_ID = MAIN.PRD_ID AND PTYPE = 'BND')");
        sb.append(" AND MAIN.DATE_OF_MATURITY > SYSDATE");
        sb.append(" AND IS_SALE = 'Y'");

        sb.append(" AND DE.STOCK_BOND_TYPE = :stockBondType ");
        qc.setObject("stockBondType", inputVO.getStockBondType());
        // sb.append(" AND PI_BUY = :PI_BUY"); // 依客戶是否專投帶入
        // sb.append(" AND OBU_BUY = :OBU_BUY"); // 依客戶是否OBU/DBU

        //#1404 客群、專案標籤
        if (!StringUtils.isBlank(inputVO.getBondProject())) {
            sb.append("and REGEXP_LIKE(MAIN.PROJECT, :bondProject) ");
            qc.setObject("bondProject", inputVO.getBondProject().replace(";", "|"));
        }
        if (!StringUtils.isBlank(inputVO.getBondCustLevel())) {
            sb.append("and REGEXP_LIKE(MAIN.CUSTOMER_LEVEL, :bondCustLevel) ");
            qc.setObject("bondCustLevel", inputVO.getBondCustLevel().replace(";", "|"));
        }

        // OBU 專投
        if (!StringUtils.isBlank(inputVO.getOBU())) {
            sb.append(" AND NVL(MAIN.OBU_BUY,'N') != " + (("Y").equals(inputVO.getOBU()) ? "'D'" : "'O'"));
        }
        // 專投
        if (!StringUtils.isBlank(inputVO.getIsPro()) && !inputVO.getIsPro().equals("Y")) {
            sb.append(" AND NVL(TRIM(MAIN.PI_BUY), 'N') = 'N'");
        }

        // only ranked
        if (StringUtils.isNotBlank(inputVO.getIsRanked())) {
            sb.append(" AND MAIN.PRD_RANK IS NOT NULL");
        } else {
            setBondQuery(sb, qc, inputVO);
        }

        sb.append(" AND TO_NUMBER(SUBSTR(MAIN.RISKCATE_ID,2,1)) <= :riskNum");
        qc.setObject("riskNum", Integer.parseInt(inputVO.getRiskType().substring(1, 2)));

//        sb.append(" ORDER BY MAIN.PRD_RANK"); // 改用 Java 排序

        qc.setQueryString(sb.toString());
        List<Map<String, Object>> list = dam.exeQuery(qc);

        PRD120 prd120 = PlatformContext.getBean(PRD120.class);
        Map<String, Object> tagsMap = prd120.getProjectTagsMap("PRD.BOND_PROJECT");

        for (Map<String, Object> eachRaw: list) {
            prd120.orderProjectTags(eachRaw, tagsMap);
        }

        Collections.sort(list, new CustomComparator<>(
        		CustomComparator.byField("RAW_PRD_RANK", CustomComparator.ORDER.ASC),
                PRD120.projComparator(tagsMap)               
        ));

        return list;
    }

    @SuppressWarnings("unchecked")
    public static List<Map<String, Object>> getSI(DataAccessManager dam, FPSProdInputVO inputVO) throws DAOException,
            JBranchException{
        QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
        StringBuffer sb = new StringBuffer();
        sb.append(" SELECT");
        sb.append(" CASE WHEN PABTH_UTIL.FC_GETBUSIDAY(SYSDATE, 'TWD', 3) > DE.INV_EDATE THEN 'Y' ELSE 'N' END AS PRD_RANK,");
        sb.append(" MAIN.PRD_ID,");
        sb.append(" MAIN.SI_CNAME,");
        sb.append(" MAIN.CURRENCY_STD_ID,");
        sb.append(" MAIN.RISKCATE_ID,");
        sb.append(" ROUND((MAIN.DATE_OF_MATURITY - SYSDATE) / 365, 2) AS DATE_OF_MATURITY,");
        sb.append(" CASE WHEN MAIN.RATE_GUARANTEEPAY IS NULL THEN 0 ELSE MAIN.RATE_GUARANTEEPAY END AS RATE_GUARANTEEPAY,");
        sb.append(" DE.BASE_AMT_OF_PURCHASE,");
        sb.append(" DE.INV_SDATE,");
        sb.append(" MAIN.PROJECT");
        // sb.append(" DE.BASE_AMT_OF_BUYBACK"); 無此欄位
        sb.append(" FROM TBPRD_SI MAIN");
        sb.append(" LEFT JOIN TBPRD_SIINFO DE ON MAIN.PRD_ID = DE.PRD_ID");
        sb.append(" WHERE 1=1");
        sb.append(" AND not exists(select 1 from TBPRD_CUSTOMIZED CM where CM.PRD_ID = MAIN.PRD_ID AND PTYPE = 'SI')");
        sb.append(" AND MAIN.DATE_OF_MATURITY > SYSDATE");
        sb.append(" AND MAIN.RECORD_FLAG != 'Y'");
        sb.append(" AND (TRUNC(SYSDATE) BETWEEN TRUNC(DE.INV_SDATE) AND NVL(TRUNC(DE.INV_EDATE), SYSDATE))");

        sb.append(" AND DE.STOCK_BOND_TYPE = :stockBondType ");
        qc.setObject("stockBondType", inputVO.getStockBondType());

        // OBU 專投
        if (!StringUtils.isBlank(inputVO.getOBU())) {
            sb.append(" AND NVL(MAIN.OBU_BUY,'N') != " + (("Y").equals(inputVO.getOBU()) ? "'D'" : "'O'"));
        }
        // 專投
        if (!StringUtils.isBlank(inputVO.getIsPro()) && !inputVO.getIsPro().equals("Y")) {
            sb.append(" AND NVL(TRIM(MAIN.PI_BUY), 'N') = 'N'");
        }

        // only ranked
        if (StringUtils.isNotBlank(inputVO.getIsRanked())) {
            // sb.append(" AND PABTH_UTIL.FC_GETBUSIDAY(SYSDATE, 'TWD', 3) > DE.INV_EDATE");
        } else {
            setSIQuery(sb, qc, inputVO);
        }
        //#1404 客群、專案標籤
        if (!StringUtils.isBlank(inputVO.getSi_project())) {
            sb.append("and REGEXP_LIKE(MAIN.PROJECT, :etf_project) ");
            qc.setObject("etf_project", inputVO.getSi_project().replace(";", "|"));
        }
        if (!StringUtils.isBlank(inputVO.getSi_customer_level())) {
            sb.append("and REGEXP_LIKE(MAIN.CUSTOMER_LEVEL, :etf_customer_level) ");
            qc.setObject("etf_customer_level", inputVO.getSi_customer_level().replace(";", "|"));
        }

        sb.append(" AND TO_NUMBER(SUBSTR(MAIN.RISKCATE_ID,2,1)) <= :riskNum");
        qc.setObject("riskNum", Integer.parseInt(inputVO.getRiskType().substring(1, 2)));

//        sb.append(" ORDER BY PRD_RANK DESC, DE.INV_SDATE"); 改用 JAVA 排序
        qc.setQueryString(sb.toString());
        List<Map<String, Object>> list = dam.exeQuery(qc);

        PRD120 prd120 = PlatformContext.getBean(PRD120.class);
        Map<String, Object> tagsMap = prd120.getProjectTagsMap("PRD.SI_PROJECT");

        for (Map<String, Object> eachRaw: list) {
            prd120.orderProjectTags(eachRaw, tagsMap);
        }

        Collections.sort(list, new CustomComparator<>(
        		CustomComparator.byField("PRD_RANK", CustomComparator.ORDER.DESC),
                PRD120.projComparator(tagsMap),      
                CustomComparator.byField("INV_SDATE")
        ));

        return list;
    }

    @SuppressWarnings("unchecked")
    public static List<Map<String, Object>> getSN(DataAccessManager dam, FPSProdInputVO inputVO) throws DAOException,
            JBranchException{
        QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
        StringBuffer sb = new StringBuffer();
        sb.append(" SELECT");
        sb.append(" CASE WHEN PABTH_UTIL.FC_GETBUSIDAY(SYSDATE, 'TWD', 3) > DE.INV_EDATE THEN 'Y' ELSE 'N' END AS PRD_RANK,");
        sb.append(" MAIN.PRD_ID,");
        sb.append(" MAIN.SN_CNAME,");
        sb.append(" MAIN.CURRENCY_STD_ID,");
        sb.append(" MAIN.RISKCATE_ID,");
        sb.append(" ROUND((MAIN.DATE_OF_MATURITY - SYSDATE) / 365, 2) AS DATE_OF_MATURITY,");
        sb.append(" CASE WHEN main.rate_guaranteepay IS NULL THEN 'N' WHEN main.rate_guaranteepay >=100 THEN 'Y' ELSE 'N' END AS rate_guaranteepay,");
        sb.append(" DE.BASE_AMT_OF_PURCHASE,");
        sb.append(" DE.BASE_AMT_OF_BUYBACK,");
        sb.append(" DE.UNIT_OF_VALUE, ");
        sb.append(" DE.INV_SDATE, ");
        sb.append(" MAIN.PROJECT ");
        sb.append(" FROM TBPRD_SN MAIN");
        sb.append(" LEFT JOIN TBPRD_SNINFO DE ON MAIN.PRD_ID = DE.PRD_ID");
        sb.append(" WHERE 1=1");
        sb.append(" AND not exists(select 1 from TBPRD_CUSTOMIZED CM where CM.PRD_ID = MAIN.PRD_ID AND PTYPE = 'SN')");
        sb.append(" AND MAIN.DATE_OF_MATURITY > SYSDATE");
        sb.append(" AND MAIN.RECORD_FLAG != 'Y'");
        sb.append(" AND (TRUNC(SYSDATE) BETWEEN TRUNC(DE.INV_SDATE) AND NVL(TRUNC(DE.INV_EDATE), SYSDATE))");

        sb.append(" AND DE.STOCK_BOND_TYPE = :stockBondType ");
        qc.setObject("stockBondType", inputVO.getStockBondType());

        //#1404 客群、專案標籤
        if (!StringUtils.isBlank(inputVO.getSnProject())) {
            sb.append("and REGEXP_LIKE(MAIN.PROJECT, :snProject) ");
            qc.setObject("snProject", inputVO.getSnProject().replace(";", "|"));
        }
        if (!StringUtils.isBlank(inputVO.getSnCustLevel())) {
            sb.append("and REGEXP_LIKE(MAIN.CUSTOMER_LEVEL, :snCustLevel) ");
            qc.setObject("snCustLevel", inputVO.getSnCustLevel().replace(";", "|"));
        }

        // OBU 專投
        if (!StringUtils.isBlank(inputVO.getOBU())) {
            sb.append(" AND NVL(MAIN.OBU_BUY,'N') != " + (("Y").equals(inputVO.getOBU()) ? "'D'" : "'O'"));
        }
        // 專投
        if (!StringUtils.isBlank(inputVO.getIsPro()) && !inputVO.getIsPro().equals("Y")) {
            sb.append(" AND NVL(TRIM(MAIN.PI_BUY), 'N') = 'N'");
        }

        // only ranked
        if (StringUtils.isNotBlank(inputVO.getIsRanked())) {
            // sb.append(" AND PABTH_UTIL.FC_GETBUSIDAY(SYSDATE, 'TWD', 3) > DE.INV_EDATE");
        } else {
            setSNQuery(sb, qc, inputVO);
        }

        sb.append(" AND TO_NUMBER(SUBSTR(MAIN.RISKCATE_ID,2,1)) <= :riskNum");
        qc.setObject("riskNum", Integer.parseInt(inputVO.getRiskType().substring(1, 2)));

//        sb.append(" ORDER BY MAIN.PRD_RANK DESC, DE.INV_SDATE"); // 改用 Java 排序
        qc.setQueryString(sb.toString());
        List<Map<String, Object>> list = dam.exeQuery(qc);

        PRD120 prd120 = PlatformContext.getBean(PRD120.class);
        Map<String, Object> tagsMap = prd120.getProjectTagsMap("PRD.SN_PROJECT");

        for (Map<String, Object> eachRaw: list) {
            prd120.orderProjectTags(eachRaw, tagsMap);
        }

        Collections.sort(list, new CustomComparator<>(
        		CustomComparator.byField("PRD_RANK", CustomComparator.ORDER.DESC),
                PRD120.projComparator(tagsMap),
                CustomComparator.byField("INV_SDATE", CustomComparator.ORDER.ASC)
        ));

        return list;
    }

    @SuppressWarnings("unchecked")
    public static List<Map<String, Object>> getSavingIns(DataAccessManager dam, FPSProdInputVO inputVO)
            throws DAOException, JBranchException{
        QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
        StringBuffer sb = new StringBuffer();

        sb.append(" WITH");
        sb.append(" PARAM AS (");
        sb.append("   SELECT");
        sb.append("   CASE WHEN PARAM_CODE = '4' THEN 10000");
        sb.append("     WHEN PARAM_CODE = '3' THEN 1000");
        sb.append("     WHEN PARAM_CODE = '2' THEN 100");
        sb.append("     ELSE 1 END AS INS_UNIT,");
        sb.append("   PARAM_CODE");
        sb.append("   FROM TBSYSPARAMETER");
        sb.append("   WHERE PARAM_TYPE= 'INS.UNIT'),");
        sb.append(" SAVING AS (");
        sb.append("   SELECT B.KEY_NO, B.POLICY_AMT_MIN, C.PRD_UNIT,");
        sb.append("   B.POLICY_AMT_MIN*PARAM.INS_UNIT AS BASE_RESULT");
        sb.append("   FROM TBINS_PARA_HEADER A");
        sb.append("   INNER JOIN TBPRD_INS_SUGGEST B ON A.PARA_NO = B.PARA_NO");
        sb.append("   INNER JOIN TBPRD_INS C ON B.KEY_NO = C.KEY_NO");
        sb.append("   LEFT JOIN PARAM ON PARAM.PARAM_CODE = C.PRD_UNIT");
        sb.append("   WHERE A.PARA_TYPE = '5'");
        sb.append("   AND A.STATUS = 'A'");
        sb.append("   AND A.EFFECT_DATE <= SYSDATE");
        sb.append("   AND NVL(A.EXPIRY_DATE, SYSDATE) >= TRUNC(SYSDATE)");
        sb.append(" )");
        sb.append(" SELECT DISTINCT MAIN.PRD_ID, MAIN.INSPRD_NAME, MAIN.CURR_CD, MAIN.INS_TYPE, MAIN.PRD_RANK,");
        sb.append(" FIRST_VALUE(MAIN.KEY_NO) OVER (PARTITION BY MAIN.PRD_ID ORDER BY MAIN.KEY_NO) AS KEY_NO,");
        sb.append(" NVL(INFO.BASE_AMT_OF_PURCHASE, SAVING.BASE_RESULT) AS BASE_AMT_OF_PURCHASE");
        sb.append(" FROM TBPRD_INS MAIN");
        sb.append(" LEFT JOIN TBPRD_INSINFO INFO ON MAIN.PRD_ID = INFO.PRD_ID");
        sb.append(" LEFT JOIN SAVING ON SAVING.KEY_NO = MAIN.KEY_NO");
        sb.append(" WHERE MAIN.INS_TYPE = '1'");
        sb.append(" AND TRUNC(SYSDATE) BETWEEN MAIN.SALE_SDATE AND NVL(MAIN.SALE_EDATE, SYSDATE)");

        // OBU 專投
        if (!StringUtils.isBlank(inputVO.getOBU())) {
            sb.append(" AND NVL(MAIN.OBU_BUY,'N') = :OBU");
            qc.setObject("OBU", inputVO.getOBU());
        }
        // only ranked
        if (StringUtils.isNotBlank(inputVO.getIsRanked())) {
            sb.append(" AND PRD_RANK IS NOT NULL");
        } else {
            setInsQuery(sb, qc, inputVO);
        }
        sb.append(" ORDER BY PRD_RANK");

        qc.setQueryString(sb.toString());
        return dam.exeQuery(qc);
    }

    @SuppressWarnings("unchecked")
    public static List<Map<String, Object>> getInvestIns(DataAccessManager dam, FPSProdInputVO inputVO)
            throws DAOException, JBranchException{
        QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
        StringBuffer sb = new StringBuffer();

        sb.append(" WITH TG AS (");
        sb.append("     SELECT LISTAGG(TARGET_ID, '/') WITHIN GROUP (ORDER BY TARGET_ID) AS TARGETS, INSPRD_ID");
        sb.append("     FROM TBPRD_RANK_INS_TARGET");
        sb.append("     WHERE STATUS = 'A'");
        sb.append("     GROUP BY INSPRD_ID");
        sb.append(" )");
        sb.append(" SELECT DISTINCT MAIN.PRD_ID, MAIN.INSPRD_NAME, MAIN.CURR_CD, MAIN.INS_TYPE, MAIN.PRD_RANK,");
        sb.append(" FIRST_VALUE(MAIN.KEY_NO) OVER (PARTITION BY MAIN.PRD_ID ORDER BY MAIN.KEY_NO) AS KEY_NO,");
        sb.append(" TG.TARGETS,");
        sb.append(" INFO.BASE_AMT_OF_PURCHASE");
        sb.append(" FROM TBPRD_INS MAIN");
        sb.append(" LEFT JOIN TBPRD_INSINFO INFO ON INFO.PRD_ID = MAIN.PRD_ID");
        sb.append(" LEFT JOIN TG ON TG.INSPRD_ID = MAIN.PRD_ID");
        sb.append(" WHERE MAIN.INS_TYPE = '2'");
        sb.append(" AND INFO.IS_INV = 'Y'");
        sb.append(" AND TRUNC(SYSDATE) BETWEEN MAIN.SALE_SDATE AND NVL(MAIN.SALE_EDATE, SYSDATE)");

        // OBU 專投
        if (!StringUtils.isBlank(inputVO.getOBU())) {
            sb.append(" AND NVL(MAIN.OBU_BUY,'N') = :OBU");
            qc.setObject("OBU", inputVO.getOBU());
        }
        // only ranked
        if (StringUtils.isNotBlank(inputVO.getIsRanked())) {
            sb.append(" AND PRD_RANK IS NOT NULL");
        } else {
            setInsQuery(sb, qc, inputVO);
        }
        sb.append(" ORDER BY PRD_RANK");

        qc.setQueryString(sb.toString());
        return dam.exeQuery(qc);
    }

    @SuppressWarnings("unchecked")
    public static List<Map<String, Object>> getInsTarget(DataAccessManager dam, FPSProdInputVO inputVO)
            throws DAOException, JBranchException{
        QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
        StringBuffer sb = new StringBuffer();

        if (StringUtils.isBlank(inputVO.getInsID())) {
            throw new APException("no insID");
        }
        sb.append(" SELECT TARGET_ID, LINKED_NAME, PRD_RISK FROM TBPRD_INS_LINKING");
        sb.append(" WHERE INSPRD_ID = :insID");
        qc.setObject("insID", inputVO.getInsID());
        sb.append(" AND TO_NUMBER(SUBSTR(PRD_RISK,2,1)) <= :riskNum");
        qc.setObject("riskNum", Integer.parseInt(inputVO.getRiskType().substring(1, 2)));

        qc.setQueryString(sb.toString());
        return dam.exeQuery(qc);
    }

    // about MDF query
    private static void setMDFQuery(StringBuffer sb, QueryConditionIF qc, FPSProdInputVO inputVO){
        if (!StringUtils.isBlank(inputVO.getFund_id())) {
            sb.append(" AND MAIN.PRD_ID LIKE :prd_id");
            qc.setObject("prd_id", inputVO.getFund_id() + "%");
        }
        if (!StringUtils.isBlank(inputVO.getFund_name())) {
            sb.append(" AND MAIN.FUND_CNAME LIKE :name");
            qc.setObject("name", "%" + inputVO.getFund_name() + "%");
        }
        if (!StringUtils.isBlank(inputVO.getCurrency())) {
            sb.append(" AND MAIN.CURRENCY_STD_ID = :curr");
            qc.setObject("curr", inputVO.getCurrency());
        }
        if (!StringUtils.isBlank(inputVO.getDividend_type())) {
            sb.append(" AND MAIN.DIVIDEND_TYPE = :div_type");
            qc.setObject("div_type", inputVO.getDividend_type());
        }
        if (inputVO.getDividend_fre() != null && !"".equals(inputVO.getDividend_fre())) {
            sb.append(" AND MAIN.DIVIDEND_FREQUENCY = :div_fre");
            qc.setObject("div_fre", inputVO.getDividend_fre());
        }
        if (!StringUtils.isBlank(inputVO.getFund_type())) {
            sb.append(" AND MAIN.FUND_TYPE = :fund_type");
            qc.setObject("fund_type", inputVO.getFund_type());
        }
        if (!StringUtils.isBlank(inputVO.getInv_area())) {
            sb.append(" AND MAIN.INV_AREA = :inv_area");
            qc.setObject("inv_area", inputVO.getInv_area());
        }
        if (!StringUtils.isBlank(inputVO.getInv_target())) {
            sb.append(" AND MAIN.INV_TARGET = :inv_tar");
            qc.setObject("inv_tar", inputVO.getInv_target());
        }
        if (!StringUtils.isBlank(inputVO.getTrust_com())) {
            sb.append(" AND substr(MAIN.PRD_ID,1,2) = :trust_com");
            qc.setObject("trust_com", inputVO.getTrust_com());
        }
        if (!StringUtils.isBlank(inputVO.getRiskLev())) {
            sb.append(" AND MAIN.RISKCATE_ID = :riskLev");
            qc.setObject("riskLev", inputVO.getRiskLev());
        }
    }

    // about ETF query
    private static void setETFQuery(StringBuffer sb, QueryConditionIF qc, FPSProdInputVO inputVO){
        if (!StringUtils.isBlank(inputVO.getEtfID())) {
            sb.append(" AND MAIN.PRD_ID LIKE :prd_id");
            qc.setObject("prd_id", inputVO.getEtfID() + "%");
        }
        if (!StringUtils.isBlank(inputVO.getEtfName())) {
            sb.append(" AND MAIN.ETF_CNAME LIKE :name");
            qc.setObject("name", "%" + inputVO.getEtfName() + "%");
        }
        if (!StringUtils.isBlank(inputVO.getCurrency())) {
            sb.append(" AND MAIN.CURRENCY_STD_ID = :curr");
            qc.setObject("curr", inputVO.getCurrency());
        }
        if (!StringUtils.isBlank(inputVO.getRiskLev())) {
            sb.append(" AND MAIN.RISKCATE_ID = :riskLev");
            qc.setObject("riskLev", inputVO.getRiskLev());
        }
        if (!StringUtils.isBlank(inputVO.getCountry())) {
            sb.append(" AND MAIN.LISTED_COUNTRY = :country");
            qc.setObject("country", inputVO.getCountry());
        }
        if (!StringUtils.isBlank(inputVO.getStrategy())) {
            sb.append(" AND MAIN.STRATEGY = :strategy");
            qc.setObject("strategy", inputVO.getStrategy());
        }
        if (!StringUtils.isBlank(inputVO.getInvType())) {
            sb.append(" AND MAIN.INVESTMENT_TYPE = :invType");
            qc.setObject("invType", inputVO.getInvType());
        }
        if (!StringUtils.isBlank(inputVO.getCompany())) {
            sb.append(" AND MAIN.CORPORATION = :corpo");
            qc.setObject("corpo", inputVO.getCompany());
        }
    }

    // about Bond query
    private static void setBondQuery(StringBuffer sb, QueryConditionIF qc, FPSProdInputVO inputVO){
        if (!StringUtils.isBlank(inputVO.getBondID())) {
            sb.append(" AND MAIN.PRD_ID LIKE :prd_id");
            qc.setObject("prd_id", inputVO.getBondID() + "%");
        }

        if (!StringUtils.isBlank(inputVO.getBondName())) {
            sb.append(" AND MAIN.BOND_CNAME LIKE :name");
            qc.setObject("name", "%" + inputVO.getBondName() + "%");
        }

        if (!StringUtils.isBlank(inputVO.getCurrency())) {
            sb.append(" AND MAIN.CURRENCY_STD_ID = :curr");
            qc.setObject("curr", inputVO.getCurrency());
        }

        if (!StringUtils.isBlank(inputVO.getRiskLev())) {
            sb.append(" AND MAIN.RISKCATE_ID = :riskLev");
            qc.setObject("riskLev", inputVO.getRiskLev());
        }

        if (!StringUtils.isBlank(inputVO.getBondCate())) {
            sb.append(" AND MAIN.BOND_CATE_ID = :bondCate");
            qc.setObject("bondCate", inputVO.getBondCate());
        }

        if (!StringUtils.isBlank(inputVO.getFaceVal())) {
            switch (inputVO.getFaceVal()) {
            case "01":
                sb.append(" AND MAIN.FACE_VALUE BETWEEN 1 AND 2.9999999");
                break;
            case "02":
                sb.append(" AND MAIN.FACE_VALUE BETWEEN 3 AND 4.9999999");
                break;
            case "03":
                sb.append(" AND MAIN.FACE_VALUE BETWEEN 5 AND 6.9999999");
                break;
            case "04":
                sb.append(" AND MAIN.FACE_VALUE >=7");
                break;
            }
        }

        if (!StringUtils.isBlank(inputVO.getMaturity())) {
            switch (inputVO.getMaturity()) {
            case "01":
                sb.append(" AND ROUND((MAIN.DATE_OF_MATURITY - SYSDATE) / 365, 2) <= 1");
                break;
            case "02":
                sb.append(" AND ROUND((MAIN.DATE_OF_MATURITY - SYSDATE) / 365, 2) BETWEEN 1 AND 5");
                break;
            case "03":
                sb.append(" AND ROUND((MAIN.DATE_OF_MATURITY - SYSDATE) / 365, 2) BETWEEN 5 AND 10");
                break;
            case "04":
                sb.append(" AND ROUND((MAIN.DATE_OF_MATURITY - SYSDATE) / 365, 2) BETWEEN 10 AND 15");
                break;
            case "05":
                sb.append(" AND ROUND((MAIN.DATE_OF_MATURITY - SYSDATE) / 365, 2) >= 15");
                break;
            }
        }

        if (!StringUtils.isBlank(inputVO.getYTM())) {
            sb.append(" AND MAIN.YTM = :ytm");
            qc.setObject("ytm", inputVO.getYTM());
        }

        if (!StringUtils.isBlank(inputVO.getRatingSP())) {
            sb.append(" AND MAIN.BOND_CREDIT_RATING_SP = :ratingSP");
            qc.setObject("ratingSP", inputVO.getRatingSP());
            switch (inputVO.getRatingSP()) {
            case "01":
                sb.append(" AND PARAM.PARAM_NAME IN ('01','02','03','04')");
                break;
            case "02":
                sb.append(" AND PARAM.PARAM_NAME IN ('01','02','03','04','05','06','07')");
                break;
            case "03":
                sb.append(" AND PARAM.PARAM_NAME IN ('01','02','03','04','05','06','07','08','09','10')");
                break;
            case "04":
                sb.append(" AND PARAM.PARAM_NAME IN ('11','12','13','14','15','16','17','18','19')");
                break;
            case "05":
                sb.append(" AND MAIN.BOND_CREDIT_RATING_SP IS NULL");
                break;
            }
        }
    }

    // about SI query
    private static void setSIQuery(StringBuffer sb, QueryConditionIF qc, FPSProdInputVO inputVO){
        if (!StringUtils.isBlank(inputVO.getSIID())) {
            sb.append(" AND MAIN.PRD_ID LIKE :prd_id");
            qc.setObject("prd_id", inputVO.getSIID() + "%");
        }
        if (!StringUtils.isBlank(inputVO.getSIName())) {
            sb.append(" AND MAIN.SI_CNAME LIKE :name");
            qc.setObject("name", "%" + inputVO.getSIName() + "%");
        }
        if (!StringUtils.isBlank(inputVO.getCurrency())) {
            sb.append(" AND MAIN.CURRENCY_STD_ID = :curr");
            qc.setObject("curr", inputVO.getCurrency());
        }
        if (!StringUtils.isBlank(inputVO.getRiskLev())) {
            sb.append(" AND MAIN.RISKCATE_ID = :riskLev");
            qc.setObject("riskLev", inputVO.getRiskLev());
        }
        if (!StringUtils.isBlank(inputVO.getMaturity())) {
            switch (inputVO.getMaturity()) {
            case "01":
                sb.append(" AND ROUND((MAIN.DATE_OF_MATURITY - SYSDATE) / 365, 2) <= 1");
                break;
            case "02":
                sb.append(" AND ROUND((MAIN.DATE_OF_MATURITY - SYSDATE) / 365, 2) BETWEEN 1 AND 5");
                break;
            case "03":
                sb.append(" AND ROUND((MAIN.DATE_OF_MATURITY - SYSDATE) / 365, 2) BETWEEN 5 AND 10");
                break;
            case "04":
                sb.append(" AND ROUND((MAIN.DATE_OF_MATURITY - SYSDATE) / 365, 2) BETWEEN 10 AND 15");
                break;
            case "05":
                sb.append(" AND ROUND((MAIN.DATE_OF_MATURITY - SYSDATE) / 365, 2) >= 15");
                break;
            }
        }
        if (!StringUtils.isBlank(inputVO.getRateGuarantee())) {
            if("Y".equals(inputVO.getRateGuarantee()))
                sb.append("and MAIN.RATE_GUARANTEEPAY >= 100 ");
            else
                sb.append("and (MAIN.RATE_GUARANTEEPAY is null or MAIN.RATE_GUARANTEEPAY < 100) ");
        }
    }

    // about SN query
    private static void setSNQuery(StringBuffer sb, QueryConditionIF qc, FPSProdInputVO inputVO){
        if (!StringUtils.isBlank(inputVO.getSNID())) {
            sb.append(" AND MAIN.PRD_ID LIKE :prd_id");
            qc.setObject("prd_id", inputVO.getSNID() + "%");
        }
        if (!StringUtils.isBlank(inputVO.getSNName())) {
            sb.append(" AND MAIN.SN_CNAME LIKE :name");
            qc.setObject("name", "%" + inputVO.getSNName() + "%");
        }
        if (!StringUtils.isBlank(inputVO.getCurrency())) {
            sb.append(" AND MAIN.CURRENCY_STD_ID = :curr");
            qc.setObject("curr", inputVO.getCurrency());
        }
        if (!StringUtils.isBlank(inputVO.getRiskLev())) {
            sb.append(" AND MAIN.RISKCATE_ID = :riskLev");
            qc.setObject("riskLev", inputVO.getRiskLev());
        }
        if (!StringUtils.isBlank(inputVO.getMaturity())) {
            switch (inputVO.getMaturity()) {
            case "01":
                sb.append(" AND ROUND((MAIN.DATE_OF_MATURITY - SYSDATE) / 365, 2) <= 1");
                break;
            case "02":
                sb.append(" AND ROUND((MAIN.DATE_OF_MATURITY - SYSDATE) / 365, 2) BETWEEN 1 AND 5");
                break;
            case "03":
                sb.append(" AND ROUND((MAIN.DATE_OF_MATURITY - SYSDATE) / 365, 2) BETWEEN 5 AND 10");
                break;
            case "04":
                sb.append(" AND ROUND((MAIN.DATE_OF_MATURITY - SYSDATE) / 365, 2) BETWEEN 10 AND 15");
                break;
            case "05":
                sb.append(" AND ROUND((MAIN.DATE_OF_MATURITY - SYSDATE) / 365, 2) >= 15");
                break;
            }
        }
        if (!StringUtils.isBlank(inputVO.getRateGuarantee())) {
            if("Y".equals(inputVO.getRateGuarantee()))
                sb.append("and MAIN.RATE_GUARANTEEPAY >= 100 ");
            else
                sb.append("and (MAIN.RATE_GUARANTEEPAY is null or MAIN.RATE_GUARANTEEPAY < 100) ");
        }
    }

    // about ins query
    private static void setInsQuery(StringBuffer sb, QueryConditionIF qc, FPSProdInputVO inputVO){
        if (StringUtils.isNotBlank(inputVO.getInsID())) {
            sb.append(" AND MAIN.PRD_ID LIKE :insID");
            qc.setObject("insID", inputVO.getInsID() + "%");
        }

        if (StringUtils.isNotBlank(inputVO.getInsName())) {
            sb.append(" AND MAIN.INSPRD_NAME LIKE :insName");
            qc.setObject("insName", inputVO.getInsName() + "%");
        }

        if (StringUtils.isNotBlank(inputVO.getInsAnnual())) {
            int temp = Integer.parseInt(inputVO.getInsAnnual());
            if (temp <= 6) {
                sb.append(" AND MAIN.INSPRD_ANNUAL = :insAnnual");
                qc.setObject("insAnnual", temp);
            } else if (temp == 10) {
                sb.append(" AND MAIN.INSPRD_ANNUAL BETWEEN 10 AND 14");
            } else if (temp == 15) {
                sb.append(" AND MAIN.INSPRD_ANNUAL BETWEEN 15 AND 19");
            } else {
                sb.append(" AND MAIN.INSPRD_ANNUAL > 19");
            }
        }

        if (StringUtils.isNotBlank(inputVO.getInsCurrency())) {
            sb.append(" AND MAIN.CURR_CD = :insCurrency");
            qc.setObject("insCurrency", inputVO.getInsCurrency());
        }

        if (StringUtils.isNotBlank(inputVO.getIsIncreasing())) {
            sb.append(" AND MAIN.IS_INCREASING = :isIncreasing");
            qc.setObject("isIncreasing", inputVO.getIsIncreasing());
        }

        if (StringUtils.isNotBlank(inputVO.getIsRepay())) {
            sb.append(" AND MAIN.IS_REPAY = :isRePay");
            qc.setObject("isRePay", inputVO.getIsRepay());
        }

        if (StringUtils.isNotBlank(inputVO.getIsRateChange())) {
            sb.append(" AND MAIN.IS_RATE_CHANGE = :isRateChange");
            qc.setObject("isRateChange", inputVO.getIsRateChange());
        }
        //
        // if (StringUtils.isNotBlank(inputVO.getInsGuarntee())){
        // if ((" 02").equals(inputVO.getInsGuarntee())){
        // sb.append(" AND IS_LIFELONG = 'Y'");
        // } else {
        // sb.append(" AND (IS_LIFELONG != 'Y' OR IS_LIFELONG IS NULL)");
        // }
        // }
        //
        // if (StringUtils.isNotBlank(inputVO.getIsMain())){
        // sb.append(" AND MAIN_RIDER = :isMain");
        // qc.setObject("isMain", inputVO.getIsMain());
        // }
        //
        if (StringUtils.isNotBlank(inputVO.getIsCom01())) {
            if ((" Y").equals(inputVO.getIsCom01())) {
                sb.append(" AND MAIN.IS_ANNUITY = 'Y'");
            } else {
                sb.append(" AND (MAIN.IS_ANNUITY != 'Y' OR MAIN.IS_ANNUITY IS NULL)");
            }
        }
    }

}
