package com.idd.db.entiry;

class RuntimeParam {
    final SqlParam def;
    Object value;

    RuntimeParam(SqlParam def, Object value) {
        this.def = def;
        this.value = value;
    }
}
