/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.systex.jbranch.platform.common.multiLang;


import java.util.List;

import com.systex.jbranch.platform.common.platformdao.table.TbsysmultilangVO;

/**
 *
 * @author Benson
 */
public class IdGroupType {

    private String attributeName;
    private List<TbsysmultilangVO> idAttributes;

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public List<TbsysmultilangVO> getIdAttributes() {
        return idAttributes;
    }

    public void setIdAttributes(List<TbsysmultilangVO> idAttributes) {
        this.idAttributes = idAttributes;
    }
    


}
