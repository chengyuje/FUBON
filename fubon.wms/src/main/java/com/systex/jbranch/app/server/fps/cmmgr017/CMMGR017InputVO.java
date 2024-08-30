package com.systex.jbranch.app.server.fps.cmmgr017;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingOutputVO;

public class CMMGR017InputVO extends PagingOutputVO {
    private String sql; // 欲執行的SQL語法
    private String pckName; // 欲查詢的package名稱

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public String getPckName() {
        return pckName;
    }

    public void setPckName(String pckName) {
        this.pckName = pckName;
    }
}
