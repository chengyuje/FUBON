package com.systex.jbranch.app.server.fps.cmmgr017;

import com.systex.jbranch.fubon.commons.FubonWmsBizLogic;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.util.IPrimitiveMap;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.sql.Types;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * SQL 維護頁面
 *
 * @author eli
 * @date 20180906
 * @date 20181031 修改維護介面，直接在介面輸入SQL 並執行 (避免編碼問題)
 * @date 20181106 增加查詢介面，查詢package相關資訊
 * @date 20181228 添加在執行SQL後，立刻返回執行狀態功能
 * @date 20190819 因為 hibernate 執行 ':=' 等語句會產生錯誤，改為呼叫 Procedure 執行。
 */
@Component("cmmgr017")
@Scope("request")
@SuppressWarnings({"unchecked"})
public class CMMGR017 extends FubonWmsBizLogic {

    private CMMGR017InputVO inputVO;
    @Autowired
    private CMMGR017OutputVO outputVO;

    /**
     * 解析DDL中資料庫物件名稱
     **/
    private static final Pattern OBJ_NAME_PATTERN = Pattern.compile("[\\w\"']+(?=\\s+[ai]s)", Pattern.CASE_INSENSITIVE);

    /**
     * 設置 InputVO
     **/
    private void setInputVO(Object body) {
        this.inputVO = (CMMGR017InputVO) body;
    }

    /**
     * 執行上傳的 sql
     *
     * @param body
     * @param header
     */
    public void executeSql(Object body, IPrimitiveMap header) {
        setInputVO(body);
        List msgList = new ArrayList();

        int index = 0;
        for (String each : cutSqlToSnippet()) {
            if (StringUtils.isBlank(each)) continue;
            msgList.add(executeEachSql(++index, each.trim()));
        }
        outputVO.setMsg(msgList);
        this.sendRtnObject(outputVO);
    }

    /**
     * 執行每個sql片段
     **/
    private Map executeEachSql(int index, String each) {
        Map eachInfo = new HashMap();
        try {
            DataAccessManager dam = this.getDataAccessManager();
            QueryConditionIF qc = dam.getQueryCondition(DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL);
            StringBuffer sb = new StringBuffer();
            sb.append("CALL PACKAGE_MAINTAIN(? ,? ) ");
            qc.setString(1, each);
            qc.registerOutParameter(2, Types.VARCHAR);
            qc.setQueryString(sb.toString());

            eachInfo.put("STATUS", true);
            eachInfo.put("MSG", String.format("%s：【%s】：%s！", index, analysisObjName(each), dam.executeCallable(qc).get(2)));
        } catch (JBranchException e) {
            eachInfo.put("STATUS", false);
            eachInfo.put("MSG", String.format("%s：執行失敗！%s！", index, e.getCause()));
        }
        return eachInfo;
    }

    /**
     * 解析參數，得出資料庫物件名稱
     **/
    private String analysisObjName(String each) {
        try {
            Matcher m = OBJ_NAME_PATTERN.matcher(each);
            m.find();
            return m.group().replaceAll("[\"']", "");
        } catch (IllegalStateException e) {
            return "SQL";
        }
    }

    /**
     * 每一段sql使用 '/'作分隔。須注意尾端多餘'/'以及多行註解的情況
     **/
    private String[] cutSqlToSnippet() {
        return inputVO.getSql()
                .split("\n/(?!.)");
    }

    /**
     * 查詢Package相關資訊
     *
     * @param body
     * @param header
     */
    public void queryPackageInfo(Object body, IPrimitiveMap header) throws JBranchException {
        setInputVO(body);
        outputVO.setPckInfo(
                process(qryPckBasicInfo(inputVO.getPckName()),
                        qryPckContent(inputVO.getPckName()),
                        qryPckErrMsg(inputVO.getPckName())));
        this.sendRtnObject(outputVO);
    }

    /**
     * 處理package資訊
     **/
    private List process(List<Map<String, String>> basic, List<Map<String, String>> content, List<Map<String, String>> err) {
        List result = new ArrayList();

        if (CollectionUtils.isNotEmpty(basic)) {
            for (String type : typeKeySet(basic)) {
                Map eachMap = new HashMap();
                eachMap.put("NAME", inputVO.getPckName());
                eachMap.put("TYPE", type);

                aggregate(type, eachMap, content, "TEXT");

                for (Map<String, String> bMap : basic) {
                    if (bMap.get("TYPE").equals(type)) eachMap.putAll(bMap);
                }

                aggregate(type, eachMap, err, "ERR_MSG");
                result.add(eachMap);
            }
        }
        return result;
    }

    /**
     * 聚合資料
     **/
    private void aggregate(String type, Map eachMap, List<Map<String, String>> data, String col) {
        StringBuilder sb = new StringBuilder();
        for (Map<String, String> sub : data) {
            if (sub.get("TYPE").equals(type)) sb.append(sub.get(col));
        }
        eachMap.put(col, sb.toString());
    }

    /**
     * 取TYPE欄位作為 KEY值
     **/
    private Set<String> typeKeySet(List<Map<String, String>> basic) {
        Set<String> typeKey = new HashSet();
        for (Map<String, String> bMap : basic) typeKey.add(bMap.get("TYPE"));
        return typeKey;
    }

    /**
     * DB撈取Package基本資訊
     **/
    private List qryPckBasicInfo(String pckName) throws JBranchException {
        return exeQueryForQcf(genDefaultQueryConditionIF().setQueryString(new StringBuilder()
                .append("select OBJECT_NAME NAME, OBJECT_TYPE TYPE, CREATED, LAST_DDL_TIME, STATUS ")
                .append("from USER_OBJECTS where OBJECT_NAME = :name order by TYPE ").toString())
                .setObject("name", pckName));
    }

    /**
     * DB撈取Package內容
     **/
    private List qryPckContent(String pckName) throws JBranchException {
        return exeQueryForQcf(genDefaultQueryConditionIF()
                .setQueryString("select NAME, TYPE, TEXT from USER_SOURCE where NAME = :name order by TYPE, LINE ")
                .setObject("name", pckName));
    }

    /**
     * DB撈取Package錯誤訊息
     **/
    private List qryPckErrMsg(String pckName) throws JBranchException {
        return exeQueryForQcf(genDefaultQueryConditionIF().setQueryString(new StringBuilder()
                .append("select NAME, TYPE, ATTRIBUTE || '(' || LINE || ',' || POSITION || '):' || TEXT || '\\n' ERR_MSG ")
                .append("from dba_errors where name = :name order by TYPE, SEQUENCE ").toString())
                .setObject("name", pckName));
    }
}
