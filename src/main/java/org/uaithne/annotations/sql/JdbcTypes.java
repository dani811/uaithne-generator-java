/*
 * Copyright 2012 and beyond, Juan Luis Paz
 *
 * This file is part of Uaithne.
 *
 * Uaithne is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Uaithne is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Uaithne. If not, see <http://www.gnu.org/licenses/>.
 */
package org.uaithne.annotations.sql;

public enum JdbcTypes {
    BIT,
    TINYINT,
    SMALLINT,
    INTEGER,
    BIGINT,
    FLOAT,
    REAL,
    DOUBLE,
    NUMERIC,
    DECIMAL,
    CHAR,
    VARCHAR,
    LONGVARCHAR,
    DATE,
    TIME,
    TIMESTAMP,
    BINARY,
    VARBINARY,
    LONGVARBINARY,
    NULL,
    OTHER,
    JAVA_OBJECT,
    DISTINCT,
    STRUCT,
    ARRAY,
    BLOB,
    CLOB,
    REF,
    DATALINK,
    BOOLEAN;
}
