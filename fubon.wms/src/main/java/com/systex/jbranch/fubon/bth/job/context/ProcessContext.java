package com.systex.jbranch.fubon.bth.job.context;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import com.systex.jbranch.fubon.bth.job.inf.AccessSQLInf;
import com.systex.jbranch.platform.common.dataaccess.datasource.DataSource;
import com.systex.jbranch.platform.common.errHandle.APException;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.PlatformContext;
import com.systex.jbranch.platform.server.info.FormatHelper;
import com.systex.jbranch.platform.server.info.XmlInfo;
import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.orm.hibernate4.SessionFactoryUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * ProcessContext SQL相關邏輯服務器
 *
 * @author Eli
 * @date 2017/10/18
 * @date 2018/11/14 調整DataSource在初始化的時候就附值
 */
@SuppressWarnings({"rawtypes", "unchecked"})
@Scope("prototype")
public class ProcessContext {
    // DataSource物件
    private static javax.sql.DataSource oracleDS;
    private static javax.sql.DataSource sqlServerBnkDS;
    private static javax.sql.DataSource sqlServerInsDS;

    static {
        // 初始化Oracle DataSource
        try {
            oracleDS = SessionFactoryUtils.getDataSource(
                    (SessionFactory) PlatformContext.getBean(new DataSource().getDataSource()));
        } catch (JBranchException e) {
            e.printStackTrace();
        }

        // 初始化SqlServer DataSource (For 叫號)
        try {
            sqlServerBnkDS = configureSqlServer(ProcessContext.SQL_SERVER, ProcessContext.SQL_BNK);
        } catch (JBranchException e) {
            e.printStackTrace();
        }

        // 初始化SqlServer DataSource (For 保險證照)
        try {
            sqlServerInsDS = configureSqlServer(ProcessContext.SQL_SERVER, ProcessContext.SQL_INS);
        } catch (JBranchException e) {
            e.printStackTrace();
        }
    }

    /** 根據傳入字串回傳指定資料庫連線 **/
    public static Connection getConnection(String typeSql) throws SQLException {
        switch (typeSql) {
            case "WMS":
                return oracleDS.getConnection();
            case "BNK":
                return sqlServerBnkDS.getConnection();
            case "INS":
                return sqlServerInsDS.getConnection();
        }
        return null;
    }

    //Oracle常數
    public static final String ORACLE_SERVER = "SYS.OracleServer_INFO";
    public static final String ORACLE_WMS = "SYS.OracleServerWMS_INFO";

    //SQL常數
    public static final String SQL_SERVER = "SYS.SQLServer_INFO";
    public static final String SQL_INS = "SYS.SQLServerINS_INFO";  //保險UserInfo
    public static final String SQL_BNK = "SYS.SQLServerBNK_INFO";  //叫號UserInfo

    //db物件
    private AccessSQLInf db;
    private Connection conn;

    //初始建構必須指定SQL物件
    public ProcessContext(String str) throws APException {
        try {
            db = (AccessSQLInf) PlatformContext.getBean(str);
        } catch (JBranchException e) {
            throw new APException("初始化物件錯誤！");
        }
    }

    /**
     * 根據客戶端傳之參數，決定使用什麼連線
     *
     * @return Connection
     * @throws Exception
     */
    public void prepareConnection(String server, String user) throws Exception {
        if (server.equals(ORACLE_SERVER)) setConn(oracleDS.getConnection());
        else if (server.equals(SQL_SERVER))
            if (user.equals(SQL_INS)) setConn(sqlServerInsDS.getConnection());
            else if (user.equals(SQL_BNK)) setConn(sqlServerBnkDS.getConnection());
    }

    /**
     * 取得DB連線參數，並回傳設置完成的DataSource
     */
    private static javax.sql.DataSource configureSqlServer(String server, String user) throws JBranchException {
        XmlInfo xmlInfo = new XmlInfo();
        //取得server info
        Map<String, String> serverMap =
                xmlInfo.doGetVariable(server, FormatHelper.FORMAT_3);
        //取得user info
        Map<String, String> userMap =
                xmlInfo.doGetVariable(user, FormatHelper.FORMAT_3);

        return getSqlServerDS(serverMap.get("INFO1"), userMap.get("INFO1"), AccessContext.dec(userMap.get("INFO2")));
    }

    /**
     * 設置SqlServer DataSource
     *
     * @return
     * @throws Exception
     */
    private static javax.sql.DataSource getSqlServerDS(String url, String userId, String password) {
        SQLServerDataSource sqlServerDS = new SQLServerDataSource();
        sqlServerDS.setURL(url);
        sqlServerDS.setUser(userId);
        sqlServerDS.setPassword(password);
        return sqlServerDS;
    }

    /**
     * 設定SQL
     *
     * @param str : String : SQL 語法
     * @param arr : Object[] : 參數陣列，對應SQL語法的佔位字元
     * @return void
     * @throws Exception
     */
    public void setCondition(String str, Object[] arr) throws Exception {
        db.setExeInfo(str, arr);
    }

    /**
     * 查詢SQL語法
     *
     * @return List : 查詢SQL語法後的查詢結果
     * @throws Exception
     */
    public List exeQuery() throws Exception {
        return db.exeQuery(getConn());
    }

    /**
     * 查詢SQL語法
     *
     * @return List : 查詢SQL語法的KEYS
     * @throws Exception
     */
    public List exeQueryMeta() throws Exception {
        return db.exeQueryMeta(getConn());
    }

    /**
     * 更新SQL語法
     *
     * @return void
     * @throws Exception
     */
    public void exeUpdate() throws Exception {
        db.exeUpdate(getConn());
    }

    /**
     * 設置SQL集 for exeUpdate
     *
     * @param sqls : 設置SQL集
     * @return void
     * @throws APException
     */
    public void setSpcVectorSqls(Vector sqls) throws APException {
        db.setSpcVectorSqls(sqls);
    }

    /**
     * 設置資料源
     */
    public void setDataList(List list) {
        db.setDatalist(list);
    }

    public Connection getConn() {
        return conn;
    }

    public void setConn(Connection conn) {
        this.conn = conn;
    }
}
