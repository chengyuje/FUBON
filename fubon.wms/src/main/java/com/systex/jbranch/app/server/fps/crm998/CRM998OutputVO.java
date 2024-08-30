package com.systex.jbranch.app.server.fps.crm998;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

import java.util.Collections;

import java.util.List;

import org.springframework.stereotype.Component;

/**
 * 客戶等級例外處理功能回傳物件
 * @author Eli
 * @date 20180202
 * 
 */
@Component
public class CRM998OutputVO extends PagingOutputVO {
    private List resultList;

    public List getResultList() {
        return Collections.unmodifiableList(resultList);
    }

    public void setResultList(List resultList) {
        this.resultList = resultList;
    }
    
}
