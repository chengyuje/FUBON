package com.systex.jbranch.app.server.fps.prd177;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

import java.util.List;

public class PRD177OutputVO extends PagingOutputVO {
    private List resultList;    // 查詢結果
    private List codeList;      // Bound 所需的 Code
    private List boundList;     // 該保險產品的 Bound 資料

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

    public List getCodeList() {
        return codeList;
    }

    public void setCodeList(List codeList) {
        this.codeList = codeList;
    }

    public List getBoundList() {
        return boundList;
    }

    public void setBoundList(List boundList) {
        this.boundList = boundList;
    }
}
