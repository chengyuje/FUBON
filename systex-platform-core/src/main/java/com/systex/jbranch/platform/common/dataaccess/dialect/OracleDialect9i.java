package com.systex.jbranch.platform.common.dataaccess.dialect;

import java.sql.Types;

import org.hibernate.type.StringType;

/**
 * 自訂Dialect
 * Hibernate對應Table char為Java char
 * 但業務使用char(x)是為了固定長度，所以改成對應到String
 *
 * @author Alex Lin
 * @version 2010/02/01 11:25:31 AM
 */
public class OracleDialect9i extends org.hibernate.dialect.Oracle9iDialect {
    public OracleDialect9i() {
        super();
        registerHibernateType(Types.CHAR, new StringType().getName());
    }
}
