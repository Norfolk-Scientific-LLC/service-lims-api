package com.nfsci.servicelimsapi.config;

import org.hibernate.dialect.PostgreSQL94Dialect;

import java.sql.Types;

/**
 * service-ngs-api
 */

public class PostgreSql94Dialect extends PostgreSQL94Dialect {

    public PostgreSql94Dialect() {
        this.registerColumnType(Types.JAVA_OBJECT, "jsonb");
    }
}
