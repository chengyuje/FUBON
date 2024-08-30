package com.systex.jbranch.platform.common.dataManager.impl.db;

import com.systex.jbranch.platform.common.platformdao.table.TbsysdmuserVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
@SuppressWarnings("unchecked")
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class UserPlatformVarManager implements VarManager {
// ------------------------------ FIELDS ------------------------------

    private HibernateTemplate template;

// ------------------------ INTERFACE METHODS ------------------------


// --------------------- Interface VarManager ---------------------

    public Object getVar(String refId, String key) {
        return getUser(refId).getPlatformVars().get(key);
    }

    public void setVar(String refId, String key, Object value) {
        TbsysdmuserVO user = getUser(refId);
        Map<String, Serializable> platformVars = user.getPlatformVars();
        if (platformVars == null) {
            platformVars = new HashMap<String, Serializable>();
            user.setPlatformVars(platformVars);
        }
        platformVars.put(key, (Serializable) value);
    }

    private TbsysdmuserVO getUser(String wsID) {
        return template.get(TbsysdmuserVO.class, wsID);
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    public void setTemplate(HibernateTemplate template) {
        this.template = template;
    }
}
