package com.systex.jbranch.platform.common.dataaccess.dialect;

import java.sql.Types;

import org.hibernate.type.StringType;


public class DB2Dialect extends org.hibernate.dialect.DB2Dialect {
    public DB2Dialect() {
        super();
        registerHibernateType(Types.CHAR, new StringType().getName());
    }
}
