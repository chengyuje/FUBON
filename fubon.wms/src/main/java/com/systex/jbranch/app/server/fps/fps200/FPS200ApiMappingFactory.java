package com.systex.jbranch.app.server.fps.fps200;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;

import com.ibm.icu.math.BigDecimal;

public class FPS200ApiMappingFactory {

    public static String defaultStringValue(Object obj) {
        return defaultStringValue(obj, null);
    }
    
    public static String defaultStringValue(Object obj, String defaultValue) {
        String v = ObjectUtils.toString(obj);
        return StringUtils.isNotBlank(v) ? v : (StringUtils.isNotBlank(defaultValue) ? defaultValue : "");
    }
    
    public static BigDecimal defaultBigDecimalValue(Object obj) {
        return defaultBigDecimalValue(obj, null);
    }
    
    public static BigDecimal defaultBigDecimalValue(Object obj, BigDecimal defaultValue) {
        String v = ObjectUtils.toString(obj);
        return StringUtils.isNotBlank(v) ? new BigDecimal(v) : defaultValue != null ? defaultValue : BigDecimal.ZERO;
    }
    
    public static int defaultIntValue(Object obj) {
        return defaultIntValue(obj, null);
    }
    
    public static int defaultIntValue(Object obj, Integer defaultValue) {
        String v = ObjectUtils.toString(obj);
        return StringUtils.isNotBlank(v) ? (new BigDecimal(v)).intValue() : defaultValue != null ? defaultValue : 0;
    }
    
    public static double defaultDoubleValue(Object obj) {
        return defaultDoubleValue(obj, null);
    }
    
    public static double defaultDoubleValue(Object obj, Double defaultValue) {
        String v = ObjectUtils.toString(obj);
        return StringUtils.isNotBlank(v) ? (new BigDecimal(v)).doubleValue() : defaultValue != null ? defaultValue : 0;
    }
}
