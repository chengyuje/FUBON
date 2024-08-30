package com.systex.jbranch.app.server.fps.sot703;

/**
 * 方法命名
 * single：單筆
 * multiple：定期定額
 * N：不定額
 * Long：久久
 * Relax：心投
 *
 *
 * 單筆	                single
 * 定期定額	            multiple
 * 定期不定額	            multipleN
 * 定期定額 (Fund久久)  	multipleL
 * 定期不定額 (Fund久久)	multipleNL
 * 定期定額 (Fund心投)  	multipleR
 * 定期不定額 (Fund心投) 	multipleNR

 */
public class FundRule {

    /**
     * 是否為單筆及定期定額
     * 參考：SOT.NEW_ASSET_TRADE_SUB_TYPE。
     */
    public static boolean isSingleOrMultiple(String tradeSubType) {
        return tradeSubType != null && tradeSubType.matches("1|2|4");
    }

    /**
     * 是否為定期定額
     * 參考：SOT.NEW_ASSET_TRADE_SUB_TYPE。
     */
    public static boolean isMultiple(String tradeSubType) {
        return tradeSubType != null && tradeSubType.matches("2|4");
    }

    /**
     * 是否為定期不定額
     * 參考：SOT.NEW_ASSET_TRADE_SUB_TYPE。
     */
    public static boolean isMultipleN(String tradeSubType) {
        return tradeSubType != null && tradeSubType.matches("3|5");
    }

    /**
     * 是否為定期定額/定期不定額富邦基金專案
     * 參考：SOT.NEW_ASSET_TRADE_SUB_TYPE。
     */
    public static boolean isFundProject(String tradeSubType) {
        return tradeSubType != null && tradeSubType.matches("4|5");
    }

    /**
     * 是否為定期定額/定期不定額 或 富邦基金專案
     * @return
     */
    public static boolean isMultipleOrFundProject(String tradeSubType) {
        return tradeSubType != null && tradeSubType.matches("2|3|4|5");
    }
}
