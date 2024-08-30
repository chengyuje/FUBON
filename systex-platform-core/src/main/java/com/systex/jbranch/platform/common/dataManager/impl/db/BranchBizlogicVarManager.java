package com.systex.jbranch.platform.common.dataManager.impl.db;

import com.systex.jbranch.platform.common.platformdao.table.TbsysdmbranchVO;
import org.springframework.orm.hibernate4.HibernateTemplate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Alex Lin
 * @version 2011/03/14 1:57 PM
 */
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class BranchBizlogicVarManager implements VarManager {
// ------------------------------ FIELDS ------------------------------

    private HibernateTemplate template;

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface VarManager ---------------------

    public Object getVar(String refId, String key) {
        return getBranch(refId).getBizlogicVars().get(key);
    }

    public void setVar(String refId, String key, Object value) {
        getBranch(refId).getBizlogicVars().put(key, (Serializable) value);
        TbsysdmbranchVO branch = getBranch(refId);
        Map<String, Serializable> bizlogicVars = branch.getBizlogicVars();
        if (bizlogicVars == null) {
            bizlogicVars = new HashMap<String, Serializable>();
            branch.setPlatformVars(bizlogicVars);
        }
        bizlogicVars.put(key, (Serializable) value);
    }

    private TbsysdmbranchVO getBranch(String branchId) {
        return template.get(TbsysdmbranchVO.class, branchId);
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    public void setTemplate(HibernateTemplate template) {
        this.template = template;
    }
}
