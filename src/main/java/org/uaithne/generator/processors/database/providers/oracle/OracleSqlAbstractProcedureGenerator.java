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
package org.uaithne.generator.processors.database.providers.oracle;

import javax.lang.model.element.Element;
import javax.tools.Diagnostic;
import org.uaithne.annotations.sql.JdbcTypes;
import static org.uaithne.annotations.sql.JdbcTypes.ARRAY;
import static org.uaithne.annotations.sql.JdbcTypes.BIGINT;
import static org.uaithne.annotations.sql.JdbcTypes.BINARY;
import static org.uaithne.annotations.sql.JdbcTypes.BIT;
import static org.uaithne.annotations.sql.JdbcTypes.BLOB;
import static org.uaithne.annotations.sql.JdbcTypes.BOOLEAN;
import static org.uaithne.annotations.sql.JdbcTypes.CHAR;
import static org.uaithne.annotations.sql.JdbcTypes.CLOB;
import static org.uaithne.annotations.sql.JdbcTypes.DATALINK;
import static org.uaithne.annotations.sql.JdbcTypes.DATE;
import static org.uaithne.annotations.sql.JdbcTypes.DECIMAL;
import static org.uaithne.annotations.sql.JdbcTypes.DISTINCT;
import static org.uaithne.annotations.sql.JdbcTypes.DOUBLE;
import static org.uaithne.annotations.sql.JdbcTypes.FLOAT;
import static org.uaithne.annotations.sql.JdbcTypes.INTEGER;
import static org.uaithne.annotations.sql.JdbcTypes.JAVA_OBJECT;
import static org.uaithne.annotations.sql.JdbcTypes.LONGVARBINARY;
import static org.uaithne.annotations.sql.JdbcTypes.NULL;
import static org.uaithne.annotations.sql.JdbcTypes.NUMERIC;
import static org.uaithne.annotations.sql.JdbcTypes.OTHER;
import static org.uaithne.annotations.sql.JdbcTypes.REAL;
import static org.uaithne.annotations.sql.JdbcTypes.REF;
import static org.uaithne.annotations.sql.JdbcTypes.SMALLINT;
import static org.uaithne.annotations.sql.JdbcTypes.STRUCT;
import static org.uaithne.annotations.sql.JdbcTypes.TIME;
import static org.uaithne.annotations.sql.JdbcTypes.TIMESTAMP;
import static org.uaithne.annotations.sql.JdbcTypes.TINYINT;
import static org.uaithne.annotations.sql.JdbcTypes.VARBINARY;
import static org.uaithne.annotations.sql.JdbcTypes.VARCHAR;
import org.uaithne.generator.commons.EntityInfo;
import org.uaithne.generator.commons.FieldInfo;
import org.uaithne.generator.commons.OperationInfo;
import org.uaithne.generator.commons.Utils;
import org.uaithne.generator.processors.database.myBatis.MyBatisUtils;
import org.uaithne.generator.processors.database.sql.SqlCallGenerator;

public abstract class OracleSqlAbstractProcedureGenerator extends SqlCallGenerator {

    public String JdbcTypeToOracleDataType(JdbcTypes jdbcType, Element element) {
        switch (jdbcType) {
            case ARRAY:
                getProcessingEnv().getMessager().printMessage(Diagnostic.Kind.ERROR, "Unsupported jdbc data type: " + jdbcType.name(), element);
                return "ARRAY";
            case BIGINT:
                return "NUMBER";
            case BINARY:
                return "RAW";
            case BIT:
                return "NUMBER";
            case BLOB:
                return "BLOB";
            case BOOLEAN:
                return "NUMBER";
            case CHAR:
                return "CHAR";
            case CLOB:
                return "CLOB";
            case DATALINK:
                getProcessingEnv().getMessager().printMessage(Diagnostic.Kind.ERROR, "Unsupported jdbc data type: " + jdbcType.name(), element);
                return "DATALINK";
            case DATE:
                return "DATE";
            case DECIMAL:
                return "NUMBER";
            case DISTINCT:
                getProcessingEnv().getMessager().printMessage(Diagnostic.Kind.ERROR, "Unsupported jdbc data type: " + jdbcType.name(), element);
                return "DISTINCT";
            case DOUBLE:
                return "NUMBER";
            case FLOAT:
                return "NUMBER";
            case INTEGER:
                return "NUMBER";
            case JAVA_OBJECT:
                getProcessingEnv().getMessager().printMessage(Diagnostic.Kind.ERROR, "Unsupported jdbc data type: " + jdbcType.name(), element);
                return "JAVA_OBJECT";
            case LONGVARBINARY:
                return "LONG RAW";
            case NULL:
                getProcessingEnv().getMessager().printMessage(Diagnostic.Kind.ERROR, "Unsupported jdbc data type: " + jdbcType.name(), element);
                return "NULL";
            case NUMERIC:
                return "NUMBER";
            case OTHER:
                getProcessingEnv().getMessager().printMessage(Diagnostic.Kind.ERROR, "Unsupported jdbc data type: " + jdbcType.name(), element);
                return "OTHER";
            case REAL:
                return "NUMBER";
            case REF:
                getProcessingEnv().getMessager().printMessage(Diagnostic.Kind.ERROR, "Unsupported jdbc data type: " + jdbcType.name(), element);
                return "REF";
            case SMALLINT:
                return "NUMBER";
            case STRUCT:
                getProcessingEnv().getMessager().printMessage(Diagnostic.Kind.ERROR, "Unsupported jdbc data type: " + jdbcType.name(), element);
                return "STRUCT";
            case TIME:
                return "DATE";
            case TIMESTAMP:
                return "TIMESTAMP";
            case TINYINT:
                return "NUMBER";
            case VARBINARY:
                return "RAW";
            case VARCHAR:
                return "VARCHAR2";
            default:
                throw new IllegalArgumentException("Unimplemented jdbc data type translation for oracle: " + jdbcType.name());
        }
    }

    @Override
    public void appendInParameter(StringBuilder query, FieldInfo field) {
        query.append("p")
                .append(Utils.firstUpper(field.getName()))
                .append(" ")
                .append(JdbcTypeToOracleDataType(MyBatisUtils.getJdbcType(field), field.getElement()));
    }

    @Override
    public void appendOutParameter(StringBuilder query, FieldInfo field) {
        query.append("p")
                .append(Utils.firstUpper(field.getName()))
                .append(" out ")
                .append(JdbcTypeToOracleDataType(MyBatisUtils.getJdbcType(field), field.getElement()));
    }

    @Override
    public void appendInOutParameter(StringBuilder query, FieldInfo field) {
        query.append("p")
                .append(Utils.firstUpper(field.getName()))
                .append(" in out ")
                .append(JdbcTypeToOracleDataType(MyBatisUtils.getJdbcType(field), field.getElement()));
    }

    @Override
    public void appendStartEntityDeleteByIdQuery(StringBuilder query, EntityInfo entity, OperationInfo operation) {
        appendProcedureDeclaration(query, operation);
    }

    @Override
    public void appendStartEntityInsertQuery(StringBuilder query, EntityInfo entity, OperationInfo operation) {
        appendProcedureDeclaration(query, operation);
    }

    @Override
    public void appendStartEntityLastInsertedIdQuery(StringBuilder query, EntityInfo entity, OperationInfo operation) {
        appendProcedureDeclaration(query, operation);
    }

    @Override
    public void appendStartEntityMergeQuery(StringBuilder query, EntityInfo entity, OperationInfo operation) {
        appendProcedureDeclaration(query, operation);
    }

    @Override
    public void appendStartEntityUpdateQuery(StringBuilder query, EntityInfo entity, OperationInfo operation) {
        appendProcedureDeclaration(query, operation);
    }

    @Override
    public void appendStartCustomDeleteQuery(StringBuilder query, OperationInfo operation) {
        appendProcedureDeclaration(query, operation);
    }

    @Override
    public void appendStartCustomInsertQuery(StringBuilder query, OperationInfo operation) {
        appendProcedureDeclaration(query, operation);
    }

    @Override
    public void appendStartCustomUpdateQuery(StringBuilder query, OperationInfo operation) {
        appendProcedureDeclaration(query, operation);
    }

    protected String getNamePrefix() {
        return "";
    }

    protected void appendProcedureName(StringBuilder query, OperationInfo operation) {
        query.append(getNamePrefix())
                .append(operation.getMethodName());
    }

    protected abstract void appendProcedureStart(StringBuilder query);

    protected void appendProcedureDeclaration(StringBuilder query, OperationInfo operation) {
        appendProcedureStart(query);
        appendProcedureName(query, operation);
    }
    
    protected String getFirstLevelIdentation() {
        return "";
    }

    protected String getSecondLevelIdentation() {
        return "    ";
    }

    @Override
    public void appendFieldSeparator(StringBuilder query) {
        query.append(",\n");
        query.append(getSecondLevelIdentation());
    }

    @Override
    public void appendFirstFieldSeparator(StringBuilder query) {
        query.append(" (\n");
        query.append(getSecondLevelIdentation());
    }
    
    @Override
    public boolean handleVersionFieldOnInsert() {
        return false;
    }

    @Override
    public boolean handleVersionFieldOnUpdate() {
        return false;
    }

    @Override
    public boolean handleVersionFieldOnDelete() {
        return false;
    }

    @Override
    public boolean includeIdOnInsert() {
        return false;
    }
}
