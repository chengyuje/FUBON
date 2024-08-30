package com.systex.jbranch.app.server.fps.prd176;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

import java.util.List;

public class PRD176OutputVO extends PagingOutputVO {
    private List resultList;    // 查詢結果

    private List<String> typeError; // 數值不符合資料庫欄位型態（紀錄表格位置）
    private List<String> requiredError; // 若為新增，缺少必要欄位（紀錄表格位置）

    public List getResultList() {
        return resultList;
    }

    public void setResultList(List resultList) {
        this.resultList = resultList;
    }

    public List<String> getTypeError() {
        return typeError;
    }

    public void setTypeError(List<String> typeError) {
        this.typeError = typeError;
    }

    public List<String> getRequiredError() {
        return requiredError;
    }

    public void setRequiredError(List<String> requiredError) {
        this.requiredError = requiredError;
    }
}
