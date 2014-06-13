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
import org.uaithne.annotations.sql.JdbcType;
import org.uaithne.generator.commons.EntityInfo;
import org.uaithne.generator.commons.FieldInfo;
import org.uaithne.generator.commons.OperationInfo;
import org.uaithne.generator.commons.Utils;
import org.uaithne.generator.processors.database.QueryGenerator;
import org.uaithne.generator.processors.database.QueryGeneratorConfiguration;
import org.uaithne.generator.processors.database.myBatis.MyBatisUtils;
import org.uaithne.generator.processors.database.sql.SqlCallGenerator;

public class OracleSqlProcedureGenerator extends SqlCallGenerator {

    private QueryGenerator queryGenerator = new OracleBasicSqlQueryGenerator();

    @Override
    public void setConfiguration(QueryGeneratorConfiguration configuration) {
        super.setConfiguration(configuration);
        if (queryGenerator != null) {
            queryGenerator.setConfiguration(configuration);
        }
    }

    @Override
    public void begin() {
        super.begin();
        if (queryGenerator != null) {
            queryGenerator.begin();
        }
    }

    @Override
    public void end() {
        super.end();
        if (queryGenerator != null) {
            queryGenerator.end();
        }
    }

    public String JdbcTypeToOracleDataType(JdbcType jdbcType, Element element) {
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
        query.append("create or replace\nprocedure ")
                .append(getNamePrefix())
                .append(operation.getMethodName());
    }

    @Override
    public void appendStartEntityInsertQuery(StringBuilder query, EntityInfo entity, OperationInfo operation) {
        query.append("create or replace\nprocedure ")
                .append(getNamePrefix())
                .append(operation.getMethodName());
    }

    @Override
    public void appendStartEntityLastInsertedIdQuery(StringBuilder query, EntityInfo entity, OperationInfo operation) {
        query.append("create or replace\nprocedure ")
                .append(getNamePrefix())
                .append(operation.getMethodName());
    }

    @Override
    public void appendStartEntityMergeQuery(StringBuilder query, EntityInfo entity, OperationInfo operation) {
        query.append("create or replace\nprocedure ")
                .append(getNamePrefix())
                .append(operation.getMethodName());
    }

    @Override
    public void appendStartEntityUpdateQuery(StringBuilder query, EntityInfo entity, OperationInfo operation) {
        query.append("create or replace\nprocedure ")
                .append(getNamePrefix())
                .append(operation.getMethodName());
    }

    @Override
    public void appendStartCustomDeleteQuery(StringBuilder query, OperationInfo operation) {
        query.append("create or replace\nprocedure ")
                .append(getNamePrefix())
                .append(operation.getMethodName());
    }

    @Override
    public void appendStartCustomInsertQuery(StringBuilder query, OperationInfo operation) {
        query.append("create or replace\nprocedure ")
                .append(getNamePrefix())
                .append(operation.getMethodName());

    }

    @Override
    public void appendStartCustomUpdateQuery(StringBuilder query, OperationInfo operation) {
        query.append("create or replace\nprocedure ")
                .append(getNamePrefix())
                .append(operation.getMethodName());
    }

    @Override
    public void appendEndEntityDeleteByIdQuery(StringBuilder query, EntityInfo entity, OperationInfo operation, boolean requireSeparator) {
        if (requireSeparator) {
            query.append("\n) is\n");
        } else {
            query.append("\nis\n");
        }
        query.append("begin\n");
        appendToQuery(query, queryGenerator.getEntityDeleteByIdQuery(entity, operation), "    ");
        query.append(";\nend ")
                .append(getNamePrefix())
                .append(operation.getMethodName())
                .append(";\n/");
    }

    @Override
    public void appendEndEntityInsertQuery(StringBuilder query, EntityInfo entity, OperationInfo operation, boolean requireSeparator) {
        if (requireSeparator) {
            query.append("\n) is\n");
        } else {
            query.append("\nis\n");
        }
        query.append("begin\n");
        appendToQuery(query, queryGenerator.getEntityInsertQuery(entity, operation), "    ");
        query.append(";\nend ")
                .append(getNamePrefix())
                .append(operation.getMethodName())
                .append(";\n/");
    }

    @Override
    public void appendEndEntityLastInsertedIdQuery(StringBuilder query, EntityInfo entity, OperationInfo operation, boolean requireSeparator) {
        if (requireSeparator) {
            query.append("\n) is\n");
        } else {
            query.append("\nis\n");
        }
        query.append("begin\n");
        appendToQuery(query, queryGenerator.getEntityLastInsertedIdQuery(entity, operation, false), "    ");
        query.append(";\nend ")
                .append(getNamePrefix())
                .append(operation.getMethodName())
                .append(";\n/");
    }

    @Override
    public void appendEndEntityMergeQuery(StringBuilder query, EntityInfo entity, OperationInfo operation, boolean requireSeparator) {
        if (requireSeparator) {
            query.append("\n) is\n");
        } else {
            query.append("\nis\n");
        }
        query.append("begin\n");
        appendToQuery(query, queryGenerator.getEntityMergeQuery(entity, operation), "    ");
        query.append(";\nend ")
                .append(getNamePrefix())
                .append(operation.getMethodName())
                .append(";\n/");
    }

    @Override
    public void appendEndEntityUpdateQuery(StringBuilder query, EntityInfo entity, OperationInfo operation, boolean requireSeparator) {
        if (requireSeparator) {
            query.append("\n) is\n");
        } else {
            query.append("\nis\n");
        }
        query.append("begin\n");
        appendToQuery(query, queryGenerator.getEntityUpdateQuery(entity, operation), "    ");
        query.append(";\nend ")
                .append(getNamePrefix())
                .append(operation.getMethodName())
                .append(";\n/");
    }

    @Override
    public void appendEndCustomDeleteQuery(StringBuilder query, OperationInfo operation, boolean requireSeparator) {
        if (requireSeparator) {
            query.append("\n) is\n");
        } else {
            query.append("\nis\n");
        }
        query.append("begin\n");
        appendToQuery(query, queryGenerator.getCustomDeleteQuery(operation), "    ");
        query.append(";\nend ")
                .append(getNamePrefix())
                .append(operation.getMethodName())
                .append(";\n/");
    }

    @Override
    public void appendEndCustomInsertQuery(StringBuilder query, OperationInfo operation, boolean requireSeparator) {
        if (requireSeparator) {
            query.append("\n) is\n");
        } else {
            query.append("\nis\n");
        }
        query.append("begin\n");
        appendToQuery(query, queryGenerator.getCustomInsertQuery(operation), "    ");
        query.append(";\nend ")
                .append(getNamePrefix())
                .append(operation.getMethodName())
                .append(";\n/");
    }

    @Override
    public void appendEndCustomUpdateQuery(StringBuilder query, OperationInfo operation, boolean requireSeparator) {
        if (requireSeparator) {
            query.append("\n) is\n");
        } else {
            query.append("\nis\n");
        }
        query.append("begin\n");
        appendToQuery(query, queryGenerator.getCustomUpdateQuery(operation), "    ");
        query.append(";\nend ")
                .append(getNamePrefix())
                .append(operation.getMethodName())
                .append(";\n/");
    }

    @Override
    public String firstFieldSeparator() {
        return " (\n    ";
    }

    @Override
    public String fieldSeparator() {
        return ",\n    ";
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

    public String getNamePrefix() {
        return "";
    }

    @Override
    public boolean includeIdOnInsert() {
        return false;
    }
}
