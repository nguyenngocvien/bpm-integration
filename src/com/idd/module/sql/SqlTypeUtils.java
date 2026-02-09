package com.idd.module.sql;

import java.sql.Types;

public final class SqlTypeUtils {

    private SqlTypeUtils() {}

    public static int toJdbcType(String sqlType) {
        try {
            return Types.class
                    .getField(sqlType.toUpperCase())
                    .getInt(null);
        } catch (Exception e) {
            throw new IllegalArgumentException("Unsupported SQL type: " + sqlType, e);
        }
    }
}
