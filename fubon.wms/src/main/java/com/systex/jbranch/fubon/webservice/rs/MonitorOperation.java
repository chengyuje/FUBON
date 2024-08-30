package com.systex.jbranch.fubon.webservice.rs;

import com.systex.jbranch.fubon.bth.job.business_logic.RptCommander;
import com.systex.jbranch.fubon.commons.Manager;
import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.PlatformContext;
import org.slf4j.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.sql.Clob;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static org.apache.commons.lang.StringUtils.*;

@Path("/Monitor")
public class MonitorOperation {
    public static final Logger logger = org.slf4j.LoggerFactory.getLogger(MonitorOperation.class);
    private DataAccessManager dataAccessManager;

    /**
     * 根據傳入的 code 撈取相關 monitor sql 所執行的結果.
     * <p>
     * monitor sql 由自動化報表內容設定維護 CMMGR016
     * 欲執行的 monitor 以 '/' 隔開，最後一段 SQL 將為返回結果的 SQL
     *
     * @param code
     * @return
     * @throws Exception
     */
    @GET
    @Path("/MonitorOperation")
    @Produces(MediaType.TEXT_PLAIN + ";charset=utf-8")
    public String monitorByCode(@QueryParam("code") String code) throws Exception {
        dataAccessManager = PlatformContext.getBean(DataAccessManager.class);

        String sql = getMonitorSql(code);

        String[] sqlGroups = sql.split("\n/(?!.)");
        processMiddleSql(sqlGroups);
        return processLastSql(sqlGroups[sqlGroups.length - 1]);
    }

    /**
     * 執行陣列中的最後一個 SQL，收集其結果並返回
     **/
    private String processLastSql(String sqlGroup) throws JBranchException {
        logger.info("MonitorOperation will return result of the sql is : " + sqlGroup);
        List<Map<String, Object>> result = Manager.manage(dataAccessManager)
                .append(sqlGroup)
                .query();

        StringBuilder data = new StringBuilder();
        for (Map each : result) {
            int flag = 1;
            for (Object val : sortedMap(each).values()) {
                data.append(val + (flag++ < each.values().size() ? "," : ""));
            }
            data.append("\r\n");
        }
        return data.toString();
    }

    /**
     * 將 Map 按照 Key 值 做排序，使得結果能夠按照預期而排列
     **/
    private TreeMap<String, Object> sortedMap(Map each) {
        TreeMap<String, Object> treeMap = new TreeMap<>(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return o1.compareTo(o2);
            }
        });
        treeMap.putAll(each);
        return treeMap;
    }

    /**
     * sql 陣列中，非最後一個的所有成員一一執行 SQL
     **/
    private void processMiddleSql(String[] sqlGroups) throws JBranchException {
        for (int index = 0; index < sqlGroups.length - 1; index++) {
            String snippet = defaultString(trim(sqlGroups[index]));
            if (snippet.endsWith(";"))
                snippet = substring(snippet, 0, snippet.length() - 1);

            logger.info("MonitorOperation will execute the sql is :" + snippet);
            Manager manager = Manager.manage(dataAccessManager)
                    .append(snippet);
            if (belongUpdateSqlGroup(snippet)) manager.update();
            else manager.query();
        }
    }

    private String getMonitorSql(String code) throws Exception {
        String execCode = defaultIfEmpty(trim(code), "MONITOR");
        logger.info("MonitorOperation: the code is " + execCode);

        Clob sqlClob = (Clob) ((Map) Manager.manage(dataAccessManager)
                .append("select RPT_SQL from TBSYSRPTSENDER where RPT_CODE = :code ")
                .put("code", execCode)
                .query()
                .get(0))
                .get("RPT_SQL");

        RptCommander rc = PlatformContext.getBean(RptCommander.class);
        return rc.getSqlFromClob(sqlClob);
    }

    /** 需使用 update 方法，開放 insert、update、delete SQL **/
    private boolean belongUpdateSqlGroup(String sql) {
        return upperCase(sql).matches("^(INSERT|UPDATE|DELETE)(.|\r|\n)+");
    }
}
