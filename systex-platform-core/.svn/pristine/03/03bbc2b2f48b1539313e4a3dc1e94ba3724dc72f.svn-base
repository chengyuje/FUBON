package com.systex.jbranch.platform.common.dataManager.impl.db;

import com.systex.jbranch.platform.common.platformdao.table.TbsysdmsysplatformvarsVO;
import org.springframework.orm.hibernate4.HibernateTemplate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;

/**
 * @author Alex Lin
 * @version 2011/03/08 3:39 PM
 */
@SuppressWarnings("unchecked")
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class SysPlatformVarManager implements SingleVarManager {
// ------------------------------ FIELDS ------------------------------

    private HibernateTemplate template;

// -------------------------- OTHER METHODS --------------------------

    public Object getVar(String key) {
        TbsysdmsysplatformvarsVO vo = template.get(TbsysdmsysplatformvarsVO.class, key);
        if (vo != null) {
            return vo.getVarValue();
        }
        else {
            return null;
        }
    }

    public void setVar(String key, Serializable value) {
        TbsysdmsysplatformvarsVO vo = new TbsysdmsysplatformvarsVO();
        vo.setVarkey(key);
        vo.setVarValue(value);
        template.saveOrUpdate(vo);
    }

// --------------------- GETTER / SETTER METHODS ---------------------

    public void setTemplate(HibernateTemplate template) {
        this.template = template;
    }
}
