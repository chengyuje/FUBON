package com.systex.jbranch.fubon.commons;

import com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager;
import com.systex.jbranch.platform.common.dataaccess.query.QueryConditionIF;
import com.systex.jbranch.platform.common.dataaccess.query.ResultIF;
import com.systex.jbranch.platform.common.errHandle.JBranchException;
import com.systex.jbranch.platform.common.util.PlatformContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static com.systex.jbranch.platform.common.dataaccess.delegate.DataAccessManager.QUERY_LANGUAGE_TYPE_VAR_SQL;

/**
 * <p>The DataAccessManager Service.
 * To make the code easier and let the proposes explicit.
 *
 * <p>Example and Explination：
 * <pre>// Factory, the static method, which must first call and inject your DataAccessManager.
 *
 *  Manager.manage(this.getDataAccessManager())
 *
 * <pre>// you can according your demands, appends the commands such like:
 *
 * .append(SQL)  // [SQL]: your domain logic sql
 * .put(key, value) // [key, value]: parameter's name and value
 * .condition(flag, SQL, key, value) // [flag]: external strategy expression, if flag true, append the SQl and attach (key, value)
 *
 * <pre>// Finally, you can call the 「delegate method」 to get your result, such like:
 *
 * .paging(currentPageIndex, pageCount) // The result which divide by pageCount
 * .query()  // get your results
 * .update() // update your data
 *
 */
@Component
@Scope("prototype")
public class Manager {
    private DataAccessManager dam;
    private QueryConditionIF condition;
    /** sql saver **/
    private StringBuilder sqlBuilder;
    /** sort flag : default false **/
    private boolean sortFlag = false;
    /** autoCommit flag : default false **/
    private boolean autoCommitFlag = false;

    /**
     * factory method
     * @param dam users must inject their dataAccessManager
     * @return Manager Object
     */
    public static Manager manage(DataAccessManager dam) throws JBranchException {
        Manager manager = PlatformContext.getBean("manager", Manager.class);
        manager.setDam(dam);
        manager.setCondition(dam.getQueryCondition(QUERY_LANGUAGE_TYPE_VAR_SQL));
        manager.setBuilder(new StringBuilder());
        return manager;
    }

    /** dataAccessManager setter method **/
    private void setDam(DataAccessManager dam) {
        this.dam = dam;
    }

    /** QueryConditionIF setter method **/
    private void setCondition(QueryConditionIF condition) {
        this.condition = condition;
    }

    /** SQL Builder setter method **/
    private void setBuilder(StringBuilder builder) {
        this.sqlBuilder = builder;
    }

    /**
     * add the users' sql to the StringBuilder
     * @param sql the command of executing
     * @return this
     */
    public Manager append(String sql) {
        sqlBuilder.append(sql);
        return this;
    }

    /**
     * put the params of sql to the QueryConditionIF
     * @param key parameter's key
     * @param value parameter's value
     * @return this
     */
    public Manager put(String key, Object value) {
        condition.setObject(key, value);
        return this;
    }

    /**
     * condition expression
     * @param flag expression which decide to append and put.
     * @param sql the command of executing
     * @param key parameter's key
     * @param value parameter's value
     * @return this
     */
    public Manager condition(boolean flag, String sql, String key, Object value) {
        if (flag) {
            sqlBuilder.append(sql);
            condition.setObject(key, value);
        }
        return this;
    }

    /**
     * condition expression
     * @param flag
     * @param sql
     * @return
     */
    public Manager condition(boolean flag, String sql) {
        if (flag)
            sqlBuilder.append(sql);
        return this;
    }

    /**
     * condition expression
     * @param flag
     * @param key
     * @param value
     * @return
     */
    public Manager condition(boolean flag, String key, String value) {
        if (flag)
            condition.setObject(key, value);
        return this;
    }

    /**
     * callable: input parameter
     * @param index parameter position
     * @param arg parameter
     * @return this
     */
    public Manager fnIn(int index, String arg) {
        condition.setString(index, arg);
        return this;
    }

    /**
     * callable: output parameter
     * @param index  parameter position
     * @param type type of parameter
     * @return this
     */
    public Manager fnOut(int index, int type) {
        condition.registerOutParameter(index, type);
        return this;
    }

    /**
     * if ui need sort the data, call this method.
     * @return this
     */
    public Manager needSort() {
        sortFlag = true;
        return this;
    }

    /**
     * if need autocommit, call this method.
     * @return
     */
    public Manager needAutoCommit() {
        autoCommitFlag = true;
        return this;
    }

    /**
     * set the maximum results
     * @param maxLimit
     * @return
     */
    public Manager setMaxResults(int maxLimit) {
        condition.setMaxResults(maxLimit);
        return this;
    }

    /**
     * the pre-action of executing
     */
    private void pre() {
        condition.setQueryString(sqlBuilder.toString());
    }

    /**
     * execute query action of dataAccessManager
     * @return this
     */
    public List query() throws JBranchException {
        pre();
        return sortFlag? dam.exeQuery(condition): dam.exeQueryWithoutSort(condition);
    }

    /**
     * execute update action of dataAccessManager
     * @return
     * @throws JBranchException
     */
    public int update() throws JBranchException {
        try {
            pre();
            dam.setAutoCommit(autoCommitFlag);
            return dam.exeUpdate(condition);
        } finally {
            dam.setAutoCommit(false);
        }
    }

    /**
     * execute callable action of dataAccessManager
     * @return
     * @throws JBranchException
     */
    public Map<Integer, Object> callable() throws JBranchException {
        pre();
        return dam.executeCallable(condition);
    }

    /**
     * execute paging action of dataAccessManager
     * dam.executePagingWithoutSort not work, so paging only delegate dam.executePaging
     * @param currentPageIndex
     * @param pageCount
     * @return
     * @throws JBranchException
     */

    public ResultIF paging(int currentPageIndex, int pageCount) throws JBranchException {
        pre();
        return dam.executePaging(condition, currentPageIndex, pageCount);
    }
}