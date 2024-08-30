package com.systex.jbranch.platform.common.platformdao.table;

import java.io.Serializable;

/**
 * @author Alex Lin
 * @version 2011/03/08 3:41 PM
 */
public class TbsysdmsystemvarsVO {
// ------------------------------ FIELDS ------------------------------

    private String varkey;
    private Serializable varValue;

// --------------------- GETTER / SETTER METHODS ---------------------

    public Serializable getVarValue() {
        return varValue;
    }

    public void setVarValue(Serializable varValue) {
        this.varValue = varValue;
    }

    public String getVarkey() {
        return varkey;
    }

    public void setVarkey(String varkey) {
        this.varkey = varkey;
    }

// ------------------------ CANONICAL METHODS ------------------------

    @Override
    public String toString() {
        return "com.systex.jbranch.platform.common.platformdao.table.TbsysdmsystemvarsVO{" +
                "varkey='" + varkey + '\'' +
                ", varValue=" + varValue +
                '}';
    }
}
