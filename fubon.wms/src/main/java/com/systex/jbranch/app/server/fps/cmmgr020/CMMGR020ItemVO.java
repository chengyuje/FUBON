package com.systex.jbranch.app.server.fps.cmmgr020;

import com.systex.jbranch.platform.common.dataaccess.vo.PagingInputVO;

import java.util.Map;

public class CMMGR020ItemVO extends PagingInputVO {
    private String item;
    private String menuId;
    private Map<String, Object> menu;

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getMenuId() {
        return menuId;
    }

    public void setMenuId(String menuId) {
        this.menuId = menuId;
    }

    public Map<String, Object> getMenu() {
        return menu;
    }

    public void setMenu(Map<String, Object> menu) {
        this.menu = menu;
    }
}
