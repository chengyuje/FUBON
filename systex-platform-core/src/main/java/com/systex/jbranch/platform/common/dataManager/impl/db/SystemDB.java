package com.systex.jbranch.platform.common.dataManager.impl.db;

import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.PlatformContext;

import java.io.Serializable;
import java.util.HashMap;

/**
 * @author Alex Lin
 * @version 2011/03/08 3:38 PM
 */
public class SystemDB extends com.systex.jbranch.platform.common.dataManager.System {
    private SingleVarManager varManager;
    private com.systex.jbranch.platform.common.dataManager.System origin;

    SystemDB(com.systex.jbranch.platform.common.dataManager.System origin) throws JBranchException {
        this.origin = origin;
        this.varManager = (SingleVarManager) PlatformContext.getBean("systemVarManager");
        this.setPlatformVO(new SysPlatformVODB());
        this.setBizlogicVO(new SysBizlogicVODB());
    }

    @Override
    public void setVars(String key, Object value) {
        varManager.setVar(key, (Serializable) value);
    }

    @Override
    public Object getVars(String key) {
        return varManager.getVar(key);
    }

    @Override
    public HashMap getConfigFileName() {
        return origin.getConfigFileName();
    }

    @Override
    public void setConfigFileName(HashMap configFileName) {
        origin.setConfigFileName(configFileName);
    }

    @Override
    public HashMap getDefaultValue() {
        return origin.getDefaultValue();
    }

    @Override
    public void setDefaultValue(HashMap defaultValue) {
        origin.setDefaultValue(defaultValue);
    }

    @Override
    public HashMap getInfo() {
        return origin.getInfo();
    }

    @Override
    public void setInfo(HashMap info) {
        origin.setInfo(info);
    }

    @Override
    public HashMap getPath() {
        return origin.getPath();
    }

    @Override
    public void setPath(HashMap path) {
        origin.setPath(path);
    }

    @Override
    public String getRoot() {
        return origin.getRoot();
    }
}
