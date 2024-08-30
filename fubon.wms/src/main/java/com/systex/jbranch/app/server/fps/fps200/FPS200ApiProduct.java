package com.systex.jbranch.app.server.fps.fps200;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;

public class FPS200ApiProduct extends FPS200ApiUtils {
    private static Map<String, String> productMapping = new HashMap<String, String>();
    private static final String[] productParams = { "productId", "productName", "invProductType", "productType",
            "categoryId", "marketId", "riskLevel", "isRecommand", "currCd", "pCurrency", "outlook", "amount",
            "amountNTD", "stockAmount", "stockAmountNTD", "onceLimit", "monthlyLimit", "onceProgressive", "targets",
            "keyNo", "ratio", "categoryName", "SEQNO", "marketName", "invType", "txnType", "certificateID",
            "invAmt", "invAmtTwd", "rtnRateWd", "rtnRateWdTwd", "dataDate"};
    static {
        productMapping.put("productId", "PRD_ID");
        productMapping.put("productName", "PRD_CNAME");
        productMapping.put("invProductType", "INV_PRD_TYPE");
        productMapping.put("productType", "PTYPE");
        productMapping.put("categoryId", "FUND_TYPE");
        productMapping.put("categoryName", "FUND_TYPE_NAME");
        productMapping.put("marketId", "MF_MKT_CAT");
        productMapping.put("riskLevel", "RISK_TYPE");
        productMapping.put("isRecommand", "MAIN_PRD");
        productMapping.put("currCd", "CURRENCY_TYPE");
        productMapping.put("pCurrency", "TRUST_CURR");
        productMapping.put("outlook", "CIS_3M");
        productMapping.put("amount", "PURCHASE_ORG_AMT");
        productMapping.put("amountNTD", "PURCHASE_TWD_AMT");
        productMapping.put("stockAmount", "NOW_AMT");
        productMapping.put("stockAmountNTD", "NOW_AMT_TWD");
        productMapping.put("onceLimit", "GEN_SUBS_MINI_AMT_FOR");
        productMapping.put("monthlyLimit", "SML_SUBS_MINI_AMT_FOR");
        productMapping.put("onceProgressive", "PRD_UNIT");
        productMapping.put("targets", "TARGETS");
        productMapping.put("keyNo", "KEY_NO");
        productMapping.put("ratio", "INV_PERCENT");
        productMapping.put("SEQNO", "SEQNO");
        productMapping.put("marketName", "NAME");
        productMapping.put("certificateID", "CERT_NBR");
        productMapping.put("invType", "INV_TYPE");
        productMapping.put("txnType", "TXN_TYPE");
        //add for information
        productMapping.put("invAmt", "INV_AMT");
        productMapping.put("invAmtTwd", "INV_AMT_TWD");
        productMapping.put("rtnRateWd", "RTN_RATE_WD");
        productMapping.put("rtnRateWdTwd", "RTN_RATE_WD_TWD");
        productMapping.put("dataDate", "DATA_DATE");
    }

    private static Map<String, String> rtnRateExtMapping = new HashMap<String, String>();
    private static final String[] rtnRateExtParams = { "rtnRate", "rtnRateTWD", };
    static {
        rtnRateExtMapping.put("title", "rtnRateExt");
        rtnRateExtMapping.put("rtnRate", "RTN_RATE_WD");
        rtnRateExtMapping.put("rtnRateTWD", "RTN_RATE_WD_TWD");
    }
    
    private static final Map<String, Integer> trendMap = new HashMap<String, Integer>();
    static {
        trendMap.put("", -1);
        trendMap.put("B", 0);
        trendMap.put("H", 1);
        trendMap.put("S", 2);
    }

    @Override
    protected Map<String, Object> getInitMap(){
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("productId", "");
        m.put("productName", "");
        m.put("invProductType", "");
        m.put("productType", "");
        m.put("categoryId", "");
        m.put("marketId", "");
        m.put("riskLevel", "");
        m.put("isRecommand", "0");
        m.put("currCd", "");
        m.put("pCurrency", "");
        m.put("outlook", -1);
        m.put("amount", BigDecimal.ZERO);
        m.put("amountNTD", BigDecimal.ZERO);
        m.put("stockAmount", BigDecimal.ZERO);
        m.put("stockAmountNTD", BigDecimal.ZERO);
        m.put("onceLimit", BigDecimal.ZERO);
        m.put("monthlyLimit", BigDecimal.ZERO);
        m.put("onceProgressive", BigDecimal.ZERO);
        m.put("targets", "");
        m.put("keyNo", "");
        m.put("ratio", BigDecimal.ZERO);
        m.put("categoryName", "");
        m.put("SEQNO", "");
        m.put("certificateID", "");
        m.put("marketName", "");
        m.put("invType", "");
        m.put("txnType", "");
        //add for information
        m.put("invAmt", BigDecimal.ZERO);
        m.put("invAmtTwd", BigDecimal.ZERO);
        m.put("rtnRateWd", BigDecimal.ZERO);
        m.put("rtnRateWdTwd", BigDecimal.ZERO);
        m.put("dataDate", "");
        return m;
    }
    
    @Override
    public Map<String, Object> getMapFromDBValue(Map<String, Object> dbval, FPSType type, FPSExt[] exts){
        Map<String, String> mapping = productMapping;
        Map<String, Object> target = getInitMap();

        for (String param : productParams) {
            String str = ObjectUtils.toString(dbval.get(mapping.get(param)));
            if (extendSetter(target, dbval, param, str) || extendTypeSetter(type, target, dbval, param, str)) {
                continue;
            }
            setMap(target, param, str);
        }

        for (FPSExt ext : exts) {
            addExt(target, dbval, ext);
        }

        return target;
    }

    // do customize extension
    @Override
    protected boolean extendSetter(Map<String, Object> target, Map<String, Object> dbval, String param, String val){
        boolean f = true;

        switch (param) {
        case "outlook":
//            if (val != null && "3".equals(ObjectUtils.toString(dbval.get("INV_PRD_TYPE"))))
        	if (val != null)
                target.put(param, trendMap.get(val));
            break;
        case "productType":
            target.put(param, StringUtils.isBlank(val) ? "DOP" : val);
            break;
        default:
            f = false;
        }

        return f;
    }

    @Override
    protected boolean extendINVsetter(Map<String, Object> target, Map<String, Object> dbval, String param, String val){
        boolean f = true;

        switch (param) {
        default:
            f = false;
        }

        return f;
    }

    @Override
    protected boolean extendSPPsetter(Map<String, Object> target, Map<String, Object> dbval, String param, String val){
        boolean f = true;

        switch (param) {
        case "invProductType":
            target.put(param, "3");
        default:
            f = false;
        }

        return f;
    }

    @Override
    protected void addExt(Map<String, Object> target, Map<String, Object> dbval, FPSExt ext){
        String[] params = new String[0];
        Map<String, String> mapping = new HashMap<String, String>();
        Map<String, Object> extMapping = new HashMap<String, Object>();

        switch (ext) {
        case rtnRateExt:
            mapping = rtnRateExtMapping;
            params = rtnRateExtParams;
            extMapping = getExtRtnRateMap();
            break;
        default:
        }

        for (String param : params) {
            String str = ObjectUtils.toString(dbval.get(mapping.get(param)));
            setMap(extMapping, param, str);
        }
        target.put(mapping.get("title"), extMapping);
    }

    // extends
    private Map<String, Object> getExtRtnRateMap(){
        Map<String, Object> m = new HashMap<String, Object>();
        m.put("rtnRate", BigDecimal.ZERO);
        m.put("rtnRateTWD", BigDecimal.ZERO);
        return m;
    }

}
