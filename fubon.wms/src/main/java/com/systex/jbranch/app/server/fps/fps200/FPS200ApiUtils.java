package com.systex.jbranch.app.server.fps.fps200;

import java.math.BigDecimal;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

abstract public class FPS200ApiUtils {
    public enum FPSType {
        INV, SPP
    }
    
    public enum FPSExt {
        rtnRateExt
    }
    
    abstract public Map<String, Object> getMapFromDBValue(Map<String, Object> dbval, FPSType type, FPSExt[] exts);
    abstract protected Map<String, Object> getInitMap();
    // do customize main setter extension
    abstract protected boolean extendSetter(Map<String, Object> target, Map<String, Object> dbval, String param, String val);
    // do customize extension
    abstract protected void addExt(Map<String, Object> target, Map<String, Object> dbval, FPSExt ext);
    protected boolean extendTypeSetter(FPSType type, Map<String, Object> target, Map<String, Object> dbval, String param,
            String val){
        boolean f = true;
        switch (type) {
        case INV:
            f = extendINVsetter(target, dbval, param, val);
            break;
        case SPP:
            f = extendSPPsetter(target, dbval, param, val);
        default:
            f = false;
        }

        return f;
    }

    abstract protected boolean extendINVsetter(Map<String, Object> target, Map<String, Object> dbval, String param, String val);
    abstract protected boolean extendSPPsetter(Map<String, Object> target, Map<String, Object> dbval, String param, String val);
    
    public Map<String, Object> getMapFromDBValue(Map<String, Object> dbval, FPSType type){
        return getMapFromDBValue(dbval, type, new FPSExt[0]);
    }

    // put value into map with parsing
    public void setMap(Map<String, Object> target, String key, String valStr){
        if (StringUtils.isNotBlank(valStr)) {
            if (target.get(key) instanceof String) {
                target.put(key, valStr);
            } else if (target.get(key) instanceof BigDecimal) {
                target.put(key, new BigDecimal(valStr));
            }
        }
    }
}
