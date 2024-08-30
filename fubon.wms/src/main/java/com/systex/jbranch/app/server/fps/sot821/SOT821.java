package com.systex.jbranch.app.server.fps.sot821;

import com.systex.jbranch.app.server.fps.sot712.PRDFitInputVO;
import com.systex.jbranch.app.server.fps.sot712.SotPdf;
import com.systex.jbranch.fubon.commons.Manager;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.report.factory.ReportFactory;
import com.systex.jbranch.platform.common.report.reportdata.ReportData;
import com.systex.jbranch.platform.common.report.reportdata.ReportDataIF;
import java.util.Arrays;
import org.apache.commons.lang.ObjectUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.Types;
import java.util.List;
import java.util.Map;

@Component("sot821")
@Scope("request")
public class SOT821 extends SotPdf {
    @Override
    public List<String> printReport() throws JBranchException {
        PRDFitInputVO inputVO = getInputVO();
        Map<String, Object> data = null;;
        
        
        if("5".equals(inputVO.getPrdType())) {
        	data = getSnData(inputVO.getTradeSeq(), inputVO.getBatchSeq());
        } else if ("4".equals(inputVO.getPrdType())) {
        	data = getSiData(inputVO.getTradeSeq());
        }
  
        if (data == null || notPrint(data)) return null;

        return Arrays.asList(new String[]{ReportFactory
                .getGenerator()
                .generateReport("SOT821", "R1", getReportData(inputVO, data))
                .getLocation()});
    }

    private ReportDataIF getReportData(PRDFitInputVO inputVO, Map<String, Object> data) throws JBranchException {
        // computed 保本率
        BigDecimal computedSnRateGuaranteepay = getComputedSnRateGuaranteepay(
                inputVO.getCustId(),
                (String) data.get("PROD_CURR"),
                (BigDecimal) data.get("PURCHASE_AMT"));

        ReportDataIF reportData = new ReportData();
        reportData.addParameter("PRD_ID", data.get("PROD_ID"));                    // 商品代號
        reportData.addParameter("RATE_GUARANTEE_PAY", computedSnRateGuaranteepay); // 保本率
        reportData.addParameter("CUST_ID", inputVO.getCustId());                  // 客戶 ID
        return reportData;
    }

    /**
     * 判斷 TBPRD_SN.RATE_GUARANTEEPAY 是否大於等於 100，若大於等於 100，則不列印。如果為空值，則預設不列印。
     **/
    private boolean notPrint(Map<String, Object> data) { 
        BigDecimal base = new BigDecimal(100);
        BigDecimal rateGuaranteePay = (BigDecimal) ObjectUtils.defaultIfNull(
                (BigDecimal) data.get("RATE_GUARANTEEPAY"), base);
        return rateGuaranteePay.compareTo(base) > -1;
    }

    private BigDecimal getComputedSnRateGuaranteepay(String custId, String prodCurr, BigDecimal purchaseAmt) throws JBranchException {
        DataAccessManager dam = this.getDataAccessManager();
        QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);

        StringBuilder sb = new StringBuilder();
        sb.append(" call P_RATE_GUARANTEEPAY(?, ?, ?, ?) ");
        queryCondition.setString(1, custId);
        queryCondition.setString(2, prodCurr);
        queryCondition.setBigDecimal(3, purchaseAmt);
        queryCondition.registerOutParameter(4, Types.DECIMAL);
        queryCondition.setQueryString(sb.toString());
        Map<Integer, Object> result = dam.executeCallable(queryCondition);
        // 如果回傳 null 則預設為 0
        return (BigDecimal) ObjectUtils.defaultIfNull(result.get(4), new BigDecimal(0));
    }

    private Map<String, Object> getSnData(String tradeSeq, String batchSeq) throws JBranchException {
        List<Map<String, Object>> result = Manager.manage(this.getDataAccessManager())
                .append("select D.PROD_ID, D.PROD_CURR, D.PURCHASE_AMT, SN.RATE_GUARANTEEPAY ")
                .append("from TBSOT_TRADE_MAIN M ")
                .append("join TBSOT_SN_TRADE_D D on (M.TRADE_SEQ = D.TRADE_SEQ) ")
                .append("join TBPRD_SN SN on (SN.PRD_ID = D.PROD_ID) ")
                .append("where M.TRADE_SEQ = :tradeSeq ")
                .append("and D.BATCH_SEQ = :batchSeq ")
                .put("tradeSeq", tradeSeq)
                .put("batchSeq", batchSeq)
                .query();

        if (result.isEmpty()) return null;
        return result.get(0);
    }
    
    private Map<String, Object> getSiData(String tradeSeq) throws JBranchException {
        List<Map<String, Object>> result = Manager.manage(this.getDataAccessManager())
                .append("select D.PROD_ID, D.PROD_CURR, D.PURCHASE_AMT, SI.RATE_GUARANTEEPAY ")
                .append("from TBSOT_TRADE_MAIN M ")
                .append("join TBSOT_SI_TRADE_D D on (M.TRADE_SEQ = D.TRADE_SEQ) ")
                .append("join TBPRD_SI SI on (SI.PRD_ID = D.PROD_ID) ")
                .append("where M.TRADE_SEQ = :tradeSeq ")
                .put("tradeSeq", tradeSeq)
                .query();

        if (result.isEmpty()) return null;
        return result.get(0);
    }
}
