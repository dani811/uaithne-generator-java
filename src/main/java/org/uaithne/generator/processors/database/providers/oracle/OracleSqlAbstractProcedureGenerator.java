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
import static org.uaithne.annotations.sql.JdbcTypes.DATE;
import static org.uaithne.annotations.sql.JdbcTypes.DECIMAL;
import static org.uaithne.annotations.sql.JdbcTypes.DOUBLE;
import static org.uaithne.annotations.sql.JdbcTypes.FLOAT;
import static org.uaithne.annotations.sql.JdbcTypes.INTEGER;
import static org.uaithne.annotations.sql.JdbcTypes.LONGVARBINARY;
import static org.uaithne.annotations.sql.JdbcTypes.NULL;
import static org.uaithne.annotations.sql.JdbcTypes.NUMERIC;
import static org.uaithne.annotations.sql.JdbcTypes.OTHER;
import static org.uaithne.annotations.sql.JdbcTypes.REAL;
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
import org.uaithne.generator.processors.database.sql.SqlCallGenerator;

public abstract class OracleSqlAbstractProcedureGenerator extends SqlCallGenerator {

    public String getDataType(FieldInfo field) {
        JdbcTypes jdbcType = getJdbcType(field);
        if (jdbcType == null) {
            getProcessingEnv().getMessager().printMessage(Diagnostic.Kind.ERROR, "Uknown jdbc data type. Use @JdbcType annotation for specify the jdbc data type.", field.getElement());
            return "UNKNOWN";
        }
        switch (jdbcType) {
            case ARRAY:
                getProcessingEnv().getMessager().printMessage(Diagnostic.Kind.ERROR, "Unsupported jdbc data type: " + jdbcType.name(), field.getElement());
                return "ARRAY";
            case BIT:
                return "TINYINT";
            case TINYINT:
                return "TINYINT";
            case SMALLINT:
                return "SMALLINT";
            case INTEGER:
                return "INT";
            case BIGINT:
                return "BIGINT";
            case FLOAT:
                return "FLOAT";
            case REAL:
                return "REAL";
            case DOUBLE:
                return "DOUBLE";
            case NUMERIC:
                return "NUMBER";
            case DECIMAL:
                return "NUMBER";
            case CHAR:
                return "CHAR";
            case VARCHAR:
                return "VARCHAR2";
            case LONGVARCHAR:
                return "TEXT";
            case DATE:
                return "DATE";
            case TIME:
                return "TIME";
            case TIMESTAMP:
                return "TIMESTAMP";
            case BINARY:
                return "BINARY";
            case VARBINARY:
                return "VARBINARY";
            case LONGVARBINARY:
                return "BLOB";
            case NULL:
                getProcessingEnv().getMessager().printMessage(Diagnostic.Kind.ERROR, "Unsupported jdbc data type: " + jdbcType.name(), field.getElement());
                return "NULL";
            case OTHER:
                getProcessingEnv().getMessager().printMessage(Diagnostic.Kind.ERROR, "Unsupported jdbc data type: " + jdbcType.name(), field.getElement());
                return "OTHER";
            case BLOB:
                return "BLOB";
            case CLOB:
                return "CLOB";
            case BOOLEAN:
                return "TINYINT";
            case NVARCHAR: 
                return "NVARCHAR2";
            case NCHAR:
                return "NCHAR";
            case NCLOB:
                return "NCLOB";
            case STRUCT:
                getProcessingEnv().getMessager().printMessage(Diagnostic.Kind.ERROR, "Unsupported jdbc data type: " + jdbcType.name(), field.getElement());
                return "STRUCT";
            default:
                throw new IllegalArgumentException("Unimplemented jdbc data type translation for oracle: " + jdbcType.name());
        }
    }

    @Override
    public void appendInParameter(StringBuilder query, FieldInfo field) {
        query.append("p")
                .append(Utils.firstUpper(field.getName()))
                .append(" ")
                .append(getDataType(field));
    }

    @Override
    public void appendOutParameter(StringBuilder query, FieldInfo field) {
        query.append("p")
                .append(Utils.firstUpper(field.getName()))
                .append(" out ")
                .append(getDataType(field));
    }

    @Override
    public void appendInOutParameter(StringBuilder query, FieldInfo field) {
        query.append("p")
                .append(Utils.firstUpper(field.getName()))
                .append(" in out ")
                .append(getDataType(field));
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
