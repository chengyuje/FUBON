package com.systex.jbranch.fubon.bth.job.sevEntity;

import com.systex.jbranch.fubon.bth.job.business_logic.ReportConfig;
import com.systex.jbranch.fubon.bth.job.context.AccessContext;
import com.systex.jbranch.fubon.bth.job.context.ProcessContext;
import com.systex.jbranch.fubon.bth.job.context.StoredContext;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.util.PlatformContext;

import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * @author Eli
 * @date 2017/1/05
 * @spec 提供跨系統查詢服務與產出檔案邏輯<br>
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class IntegrationService {
    private static final String PROCESS = "JobDam";

    //Query Logic =========================================================================

    /**
     * 查詢叫號系統
     *
     * @param stored
     * @return List:查詢結果
     * @throws Exception
     */
    public static List exeQuerySQLServerBNK(StoredContext stored) throws Exception {
        return exeQuerySQL(stored, ProcessContext.SQL_SERVER, ProcessContext.SQL_BNK);
    }

    /**
     * 查詢保險證照系統
     *
     * @param stored
     * @return List:查詢結果
     * @throws Exception
     */
    public static List exeQuerySQLServerINS(StoredContext stored) throws Exception {
        return exeQuerySQL(stored, ProcessContext.SQL_SERVER, ProcessContext.SQL_INS);
    }

    /**
     * 查詢新理專系統
     *
     * @param stored
     * @return List:查詢結果
     * @throws Exception
     */
    public static List exeQueryOracleWMS(StoredContext stored) throws Exception {
        return exeQuerySQL(stored, ProcessContext.ORACLE_SERVER, ProcessContext.ORACLE_WMS);
    }

    /***
     * 查詢新理專系統
     * @param queryString
     * @return List:查詢結果
     * @throws Exception
     */
    public static List exeQueryOracleWMS(String queryString) throws Exception {
        return exeQuerySQL(queryString, ProcessContext.ORACLE_SERVER, ProcessContext.ORACLE_WMS);
    }

    /***
     * 查詢保險證照系統
     * @param queryString
     * @return List:查詢結果
     * @throws Exception
     */
    public static List exeQuerySQLServerINS(String queryString) throws Exception {
        return exeQuerySQL(queryString, ProcessContext.SQL_SERVER, ProcessContext.SQL_INS);
    }

    /***
     * 查詢叫號系統
     * @param queryString
     * @return List:查詢結果
     * @throws Exception
     */
    public static List exeQuerySQLServerBNK(String queryString) throws Exception {
        return exeQuerySQL(queryString, ProcessContext.SQL_SERVER, ProcessContext.SQL_BNK);
    }

    /**
     * 查詢SQL的KEYS
     *
     * @param queryString
     * @param type
     * @return
     * @throws APException
     */
    public static List exeQueryMeta(String queryString, String type) throws APException {
        switch (type) {
            case "WMS":
                return exeQueryMeta(queryString, ProcessContext.ORACLE_SERVER, ProcessContext.ORACLE_WMS);
            case "BNK":
                return exeQueryMeta(queryString, ProcessContext.SQL_SERVER, ProcessContext.SQL_BNK);
            case "INS":
                return exeQueryMeta(queryString, ProcessContext.SQL_SERVER, ProcessContext.SQL_INS);
            default:
                return null;
        }
    }

    /**
     * 查詢SQL的KEYS
     */
    private static List exeQueryMeta(String queryString, String server, String user) throws APException {
        List<String> metaList = null;
        try {
            //Process
            ProcessContext process = new ProcessContext(PROCESS);
            process.prepareConnection(server, user);
            process.setCondition(queryString, new Object[]{});
            metaList = process.exeQueryMeta();
        } catch (APException e) {
            throw new APException(e.getMessage());
        } catch (Exception e) {
            throw new APException("系統發生錯誤請洽系統管理員");
        }
        return metaList;
    }

    /**
     * 查詢邏輯
     */
    private static List exeQuerySQL(Object stored, String server, String user) throws Exception {
        List<Map<String, Object>> list = null;
        try {
            //Process
            ProcessContext process = new ProcessContext(PROCESS);
            process.prepareConnection(server, user);
            setQueryCondition(process, stored);
            list = process.exeQuery();
        } catch (APException e) {
            throw new APException(e.getMessage());
        } catch (Exception e) {
            throw new APException("系統發生錯誤請洽系統管理員");
        }
        return list;
    }

    /**
     * 型態判別 & 設定參數
     */
    private static void setQueryCondition(ProcessContext process, Object stored) throws Exception {
        if (stored instanceof StoredContext) {
            process.setCondition(((StoredContext) stored).getSQL(), ((StoredContext) stored).getParams());
        } else if (stored instanceof String) {
            process.setCondition(stored.toString(), new Object[]{});
        } else {
            throw new APException("Condition的參數不正確。");
        }
    }

    //GenerateLogic=========================================================================

    /***
     * 產出CSV
     * @param stored
     * @param list
     * @throws Exception
     */
    public static void generateCsv(StoredContext stored, List list) throws Exception {
        try {
            AccessContext.generateCSV(
                    stored.getCsvFileName(),
                    stored.getCsvHeader(),
                    stored.getCsvColumn(),
                    list);
        } catch (APException e) {
            throw new APException(e.getMessage());
        } catch (Exception e) {
            throw new APException("系統發生錯誤請洽系統管理員");
        }
    }

    /***
     * 產出file檔
     * @param stored
     * @param list
     * @param flag : {@link AccessContext#DOT}, {@link AccessContext#FIX}
     * @throws Exception
     */
    public static void generateFile(StoredContext stored, List list, boolean flag) throws Exception {
        try {
            AccessContext.generateFile(
                    ((ReportConfig) PlatformContext.getBean("reportConfig"))
                            .configureDefaultArg(stored.getFileName(), stored.getFileColumns(), stored.getFileColWidth(), list, flag));
        } catch (APException e) {
            throw new APException(e.getMessage());
        } catch (Exception e) {
            throw new APException("系統發生錯誤請洽系統管理員");
        }
    }

    //Update Logic =========================================================================

    /**
     * 執行修改叫號系統
     *
     * @param sqls
     * @param list
     * @throws Exception
     */
    public static void exeUpdateSQLServerBNK(Vector sqls, List list) throws Exception {
        exeUpdateSQL(sqls, list, ProcessContext.SQL_SERVER, ProcessContext.SQL_BNK);
    }

    /**
     * 執行修改新理專系統
     *
     * @param sqls
     * @param list
     * @throws Exception
     */
    public static void exeUpdateOracleWMS(Vector sqls, List list) throws Exception {
        exeUpdateSQL(sqls, list, ProcessContext.ORACLE_SERVER, ProcessContext.ORACLE_WMS);
    }

    /**
     * 執行修改邏輯
     */
    private static void exeUpdateSQL(Vector sqls, List list, String server, String user) throws Exception {
        try {
            //Process
            ProcessContext process = new ProcessContext(PROCESS);
            process.prepareConnection(server, user);
            process.setSpcVectorSqls(sqls);
            process.setDataList(list);
            process.exeUpdate();
        } catch (APException e) {
            throw new APException(e.getMessage());
        } catch (Exception e) {
            throw new APException("系統發生錯誤請洽系統管理員");
        }
    }
}
