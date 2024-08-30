package com.systex.jbranch.app.server.fps.cmmgr020;

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.server.info.FubonSystemVariableConsts;
import com.systex.jbranch.platform.server.info.SysInfo;
import com.systex.jbranch.platform.util.IPrimitiveMap;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MENU
 *
 * @author moron
 * @author Eli
 * @date 2015/12/25
 * @date 2018/09/03 refactoring
 * @date 2018/12/18 ROOT根目錄對應處理
 * @date 2019/01/18 改繼承 FubonWmsBizLogic，使得發生錯誤得以RollBack
 */
@Component("cmmgr020")
@Scope("request")
public class CMMGR020 extends FubonWmsBizLogic {
    @Autowired
    private CMMGR020DataVO returnVO;
    private CMMGR020ItemVO inputVO;

    public void setInputVO(Object body) {
        inputVO = (CMMGR020ItemVO) body;
    }


    /**
     * 取得功能樹
     *
     * @param body
     * @param header
     * @throws JBranchException
     */
    public void getMENUTree(Object body, IPrimitiveMap header) throws JBranchException {
        returnVO.setMeunTreeLS(body instanceof CMMGR020ItemVO ? forMaintain(body) : forLogin());
        this.sendRtnObject(returnVO);
    }

    /**
     * 登入相關頁面使用此SQL撈出資料
     **/
    private List forLogin() throws JBranchException {
        String sql = new StringBuffer()
                .append("SELECT level1_m.MENU_ID as LEVEL1_MENU_ID, level1_m.ITEM_ID as LEVEL1_ITEM_ID, level1_m.SEQ_NUM as LEVEL1_SEQ_NUM, level1_m.MENU_TYPE as LEVEL1_MENU_TYPE, level1_m.MENU_NAME as LEVEL1_MENU_NAME, ")
                .append("level2_m.MENU_ID as LEVEL2_MENU_ID, level2_m.ITEM_ID as LEVEL2_ITEM_ID, level2_m.SEQ_NUM as LEVEL2_SEQ_NUM, level2_m.MENU_TYPE as LEVEL2_MENU_TYPE, level2_m.MENU_NAME as LEVEL2_MENU_NAME, ")
                .append("level3_m.MENU_ID as LEVEL3_MENU_ID, level3_m.ITEM_ID as LEVEL3_ITEM_ID, level3_m.SEQ_NUM as LEVEL3_SEQ_NUM, level3_m.MENU_TYPE as LEVEL3_MENU_TYPE, level3_m.MENU_NAME as LEVEL3_MENU_NAME, ")
                .append("level4_m.MENU_ID as LEVEL4_MENU_ID, level4_m.ITEM_ID as LEVEL4_ITEM_ID, level4_m.SEQ_NUM as LEVEL4_SEQ_NUM, level4_m.MENU_TYPE as LEVEL4_MENU_TYPE, level4_m.MENU_NAME as LEVEL4_MENU_NAME, ")
                .append("level5_m.MENU_ID as LEVEL5_MENU_ID, level5_m.ITEM_ID as LEVEL5_ITEM_ID, level5_m.SEQ_NUM as LEVEL5_SEQ_NUM, level5_m.MENU_TYPE as LEVEL5_MENU_TYPE, level5_m.MENU_NAME as LEVEL5_MENU_NAME ")
                .append("FROM TBSFA_FUNC_MENU level1_m ")
                .append("left outer join TBSFA_FUNC_MENU level2_m on level2_m.prev_menu=level1_m.MENU_ID ")
                .append("left outer join TBSFA_FUNC_MENU level3_m on level3_m.prev_menu=level2_m.MENU_ID ")
                .append("left outer join TBSFA_FUNC_MENU level4_m on level4_m.prev_menu=level3_m.MENU_ID ")
                .append("left outer join TBSFA_FUNC_MENU level5_m on level5_m.prev_menu=level4_m.MENU_ID ")
                .append("where level1_m.PREV_MENU is null ")
                .append("order by LEVEL1_SEQ_NUM,LEVEL2_SEQ_NUM,LEVEL3_SEQ_NUM,LEVEL4_SEQ_NUM,LEVEL5_SEQ_NUM ")
                .toString();

        return exeQueryForMap(sql, new HashMap());
    }

    /**
     * 維護頁 (CMMGR017.html) 使用此SQL撈出資料
     **/
    private List forMaintain(Object body) throws JBranchException {
        setInputVO(body);
        StringBuffer sql = new StringBuffer();
        sql.append("select * from ( ")
                // 維護頁多select一個ROOT來顯示用
                .append("   select 'ROOT' MENU_ID, 0 SEQ_NUM, 'R' MENU_TYPE, 'MENU' MENU_NAME, null PREV_MENU from dual ")
                .append("   union ")
                .append("   select MENU_ID, SEQ_NUM, MENU_TYPE, MENU_NAME, nvl(PREV_MENU, 'ROOT') PREV_MENU from TBSFA_FUNC_MENU ")
                .append(") start with MENU_ID = :menuId ")
                .append("connect by PRIOR MENU_ID = PREV_MENU ").toString();

        Map params = new HashMap();
        if (StringUtils.isBlank(inputVO.getMenuId()))
            params.put("menuId", "ROOT");
        else
            params.put("menuId", inputVO.getMenuId());

        return exeQueryForMap(sql.toString(), params);
    }

    /**
     * 取得交易功能
     *
     * @param body
     * @param header
     * @throws JBranchException
     */
    public void getItem(Object body, IPrimitiveMap header) throws JBranchException {
        setInputVO(body);
        StringBuffer sql = new StringBuffer();
        sql.append("select A.TXNCODE, A.TXNNAME, to_char(A.LASTUPDATE, 'yyyy-MM-dd') LASTUPDATE, ")
                .append("to_char(A.LASTUPDATE, 'hh24:mi:ss') LASTTIME, A.MODIFIER, nvl(C.EMP_NAME,'') MODIFIER_NAME ")
                .append("from TBSYSTXN A ")
                .append("left join TBSFA_FUNC_MENU B on A.TXNCODE = B.ITEM_ID ")
                .append("left join TBORG_MEMBER C on A.MODIFIER = C.EMP_ID ")
                .append("where 1=1 ");

        Map params = new HashMap();
        if (!StringUtils.isBlank(inputVO.getItem())) {
            sql.append("and (A.TXNCODE like :item or A.TXNNAME like :item) ");
            params.put("item", "%" + inputVO.getItem() + "%");
        }
        sql.append("order by TXNCODE ");
        returnVO.setData(exeQueryForMap(sql.toString(), params));
        this.sendRtnObject(returnVO);
    }

    /**
     * 將前端維護的 Menu 資訊，存入資料庫
     * 步驟 : 先更新目錄 => 刪除目錄的子項 => 在新增目錄的子項
     *
     * @param body
     * @param header
     * @throws JBranchException
     */
    public void confirmMenu(Object body, IPrimitiveMap header) throws JBranchException {
        setInputVO(body);

        if (!"ROOT".equals(inputVO.getMenu().get("MENU_ID")))  // ROOT MENU只在UI上顯示，資料庫不存在這筆資訊，所以不更新
            exeUpdateForMap(getUpdateMenuSql(), getUpdateMenuParams(inputVO.getMenu()));

        deleteSubMenu(inputVO.getMenu());
        insertSubMenu(inputVO.getMenu());
        this.sendRtnObject(null);
    }

    /**
     * 新增 Menu的 Sub Menu
     **/
    private void insertSubMenu(Map<String, Object> map) throws JBranchException {
        if (map.get("SUBITEM") == null) return;
        int index = 0;
        for (Map<String, Object> sub : (List<Map<String, Object>>) map.get("SUBITEM")) {
            String menuId = StringUtils.isBlank((String) sub.get("MENU_ID")) ? getSN() : (String) sub.get("MENU_ID");
            exeUpdateForMap(getInsertSubMenuSql(),
                    getInsertSubMenuParams(sub, menuId, ++index));
        }
    }

    /**
     * 取得 insert sub menu 的 SQL 參數
     **/
    private Map getInsertSubMenuParams(Map<String, Object> sub, String menuId, int index) throws JBranchException {
        Map insertSubMenuParams = new HashMap();
        insertSubMenuParams.put("menuId", menuId);
        insertSubMenuParams.put("itemId", menuId);
        insertSubMenuParams.put("seqNum", index);
        insertSubMenuParams.put("menuType", sub.get("MENU_TYPE"));
        insertSubMenuParams.put("menuName", sub.get("MENU_NAME"));
        insertSubMenuParams.put("prevMenu", checkPrevMenu(sub));
        setBasicParams(insertSubMenuParams);
        return insertSubMenuParams;
    }

    // PREV_MENU為ROOT時，代表為最上層MENU，其PREV_MENU在資料庫為空
    private Object checkPrevMenu(Map<String, Object> map) {
        return "ROOT".equals(map.get("PREV_MENU")) ? null : map.get("PREV_MENU");
    }

    /**
     * 取得 insert sub menu 的 SQL
     **/
    private String getInsertSubMenuSql() {
        return new StringBuffer()
                .append("insert into TBSFA_FUNC_MENU (MENU_ID, ITEM_ID, VERSION, SEQ_NUM, MENU_TYPE, ")
                .append("MENU_NAME, PREV_MENU, CREATOR, CREATETIME, MODIFIER, LASTUPDATE) ")
                .append("values (:menuId, :itemId, 0, :seqNum, :menuType, :menuName, :prevMenu, :creator, :createtime, :modifier, :lastUpdate )")
                .toString();
    }

    /**
     * 刪除 Menu 的 Sub Menu
     **/
    private void deleteSubMenu(Map<String, Object> map) throws JBranchException {
        String delteSubMenuSql = "delete from TBSFA_FUNC_MENU where nvl(PREV_MENU, 'ROOT') = :prevMenu ";
        Map deleteSubMenuParams = new HashMap();
        deleteSubMenuParams.put("prevMenu", map.get("MENU_ID"));
        exeUpdateForMap(delteSubMenuSql, deleteSubMenuParams);
    }

    /**
     * 取得更新 Menu 的Sql 的參數
     **/
    private Map getUpdateMenuParams(Map<String, Object> map) throws JBranchException {
        Map updateMenuParams = new HashMap();
        updateMenuParams.put("menuId", map.get("MENU_ID"));
        updateMenuParams.put("menuName", map.get("MENU_NAME"));
        updateMenuParams.put("prevMenu", checkPrevMenu(map));

        setBasicParams(updateMenuParams);
        return updateMenuParams;
    }

    /**
     * 設置固定參數
     **/
    private void setBasicParams(Map params) throws JBranchException {
        params.put("creator", SysInfo.getInfoValue(FubonSystemVariableConsts.LOGINID));
        params.put("createtime", new Timestamp(System.currentTimeMillis()));
        params.put("modifier", SysInfo.getInfoValue(FubonSystemVariableConsts.LOGINID));
        params.put("lastUpdate", new Timestamp(System.currentTimeMillis()));
    }

    /**
     * 取得更新 Menu 的Sql
     **/
    private String getUpdateMenuSql() {
        return new StringBuffer()
                .append("update TBSFA_FUNC_MENU ")
                .append("set MENU_NAME = :menuName, PREV_MENU  = :prevMenu,   ")
                .append("    CREATOR   = :creator , CREATETIME = :createtime, ")
                .append("    MODIFIER  = :modifier, LASTUPDATE = :lastUpdate  ")
                .append("where MENU_ID = :menuId ")
                .toString();
    }

    /**
     * 取得SEQ (新建目錄若沒有自訂編號，將會自動取號)
     **/
    private String getSN() throws JBranchException {
        return ((Map) exeQueryForMap("select TBSFA_FUNC_MENU_SEQ.nextval SEQ from dual ", new HashMap()).get(0)).get("SEQ").toString();
    }

    /**
     * 刪除該目錄及底下的所有子類
     *
     * @param body
     * @param header
     * @throws JBranchException
     */
    public void delMenu(Object body, IPrimitiveMap header) throws JBranchException {
        setInputVO(body);
        exeUpdateForMap(getDelMenuSql(), getDelMenuParams());
        this.sendRtnObject(null);
    }


    /**
     * 取得刪除整個目錄的SQL參數
     **/
    private Map getDelMenuParams() {
        Map delMenuParams = new HashMap();
        delMenuParams.put("menuId", inputVO.getMenuId());
        return delMenuParams;
    }

    /**
     * 取得刪除整個目錄的SQL
     **/
    private String getDelMenuSql() {
        return new StringBuffer()
                .append("delete from TBSFA_FUNC_MENU where MENU_ID in ( ")
                .append("    select MENU_ID from TBSFA_FUNC_MENU ")
                .append("    start with MENU_ID = :menuId ")
                .append("    connect by prior MENU_ID = PREV_MENU ")
                .append(") ")
                .toString();
    }

    /**
     * MenuBarController　會使用這個Function
     *
     * @param body
     * @param header
     * @throws JBranchException
     */
    public void getMobileAUTH(Object body, IPrimitiveMap header) throws JBranchException {
        DataAccessManager dam = this.getDataAccessManager();
        CMMGR020DataVO outputVO = new CMMGR020DataVO();

        QueryConditionIF queryCondition = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
        queryCondition.setQueryString("SELECT DISTINCT ITEMID, PRIVILEGEID FROM TBSYSSECUPRIFUNMAP WHERE FUNCTIONID = 'mobile'");
        List<Map<String, Object>> list = dam.exeQuery(queryCondition);
        outputVO.setData(list);
        this.sendRtnObject(outputVO);
    }
}
