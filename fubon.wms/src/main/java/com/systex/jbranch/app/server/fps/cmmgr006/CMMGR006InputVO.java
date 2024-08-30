package com.systex.jbranch.app.server.fps.cmmgr006;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Scope("request")
public class CMMGR006InputVO {
    /**
     * TBSYSPARAMTYPE
     **/
    private String paramType;
    private String ptypeName;
    private String ptypeBuss;
    private String ptypeDesc;

    /**
     * TBSYSPARAMETER
     **/
    private String paramCode;
    private String paramOrder;
    private String paramName;
    private String paramDesc;
    private String prevCode;
    private Map codeMap;

    public String getParamType() {
        return paramType;
    }

    public void setParamType(String paramType) {
        this.paramType = paramType;
    }

    public String getPtypeName() {
        return ptypeName;
    }

    public void setPtypeName(String ptypeName) {
        this.ptypeName = ptypeName;
    }

    public String getPtypeBuss() {
        return ptypeBuss;
    }

    public void setPtypeBuss(String ptypeBuss) {
        this.ptypeBuss = ptypeBuss;
    }

    public String getPtypeDesc() {
        return ptypeDesc;
    }

    public void setPtypeDesc(String ptypeDesc) {
        this.ptypeDesc = ptypeDesc;
    }

    public String getParamCode() {
        return paramCode;
    }

    public void setParamCode(String paramCode) {
        this.paramCode = paramCode;
    }

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public String getParamDesc() {
        return paramDesc;
    }

    public void setParamDesc(String paramDesc) {
        this.paramDesc = paramDesc;
    }

    public String getPrevCode() {
        return prevCode;
    }

    public void setPrevCode(String prevCode) {
        this.prevCode = prevCode;
    }

    public String getParamOrder() {
        return paramOrder;
    }

    public void setParamOrder(String paramOrder) {
        this.paramOrder = paramOrder;
    }

    public Map<String, String> getCodeMap() {
        return codeMap;
    }

    public void setCodeMap(Map<String, String> codeMap) {
        this.codeMap = codeMap;
    }
}
