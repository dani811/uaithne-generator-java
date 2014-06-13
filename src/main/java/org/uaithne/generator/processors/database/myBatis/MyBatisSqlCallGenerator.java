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
package org.uaithne.generator.processors.database.myBatis;

import org.uaithne.generator.commons.EntityInfo;
import org.uaithne.generator.commons.FieldInfo;
import org.uaithne.generator.commons.OperationInfo;
import org.uaithne.generator.processors.database.sql.SqlCallGenerator;

public abstract class MyBatisSqlCallGenerator extends SqlCallGenerator {

    public abstract String getNamePrefix();

    @Override
    public void appendInParameter(StringBuilder query, FieldInfo field) {
        query.append("#{")
                .append(field.getName())
                .append(MyBatisUtils.getJdbcTypeAttribute(field))
                .append(MyBatisUtils.getTypeHandler(getProcessingEnv(), field))
                .append(",mode=IN}");
    }

    @Override
    public void appendOutParameter(StringBuilder query, FieldInfo field) {

        query.append("#{")
                .append(field.getName())
                .append(MyBatisUtils.getJdbcTypeAttribute(field))
                .append(MyBatisUtils.getTypeHandler(getProcessingEnv(), field))
                .append(",mode=OUT}");
    }

    @Override
    public void appendInOutParameter(StringBuilder query, FieldInfo field) {

        query.append("#{")
                .append(field.getName())
                .append(MyBatisUtils.getJdbcTypeAttribute(field))
                .append(MyBatisUtils.getTypeHandler(getProcessingEnv(), field))
                .append(",mode=INOUT}");
    }

    @Override
    public void appendStartEntityDeleteByIdQuery(StringBuilder query, EntityInfo entity, OperationInfo operation) {
        query.append("call ")
                .append(getNamePrefix())
                .append(operation.getMethodName())
                .append(" (");
    }

    @Override
    public void appendStartEntityInsertQuery(StringBuilder query, EntityInfo entity, OperationInfo operation) {
        query.append("call ")
                .append(getNamePrefix())
                .append(operation.getMethodName())
                .append(" (");
    }

    @Override
    public void appendStartEntityLastInsertedIdQuery(StringBuilder query, EntityInfo entity, OperationInfo operation) {
        query.append("call ")
                .append(getNamePrefix())
                .append("lastInsertedIdFor")
                .append(entity.getDataType().getSimpleNameWithoutGenerics())
                .append(" (");
    }

    @Override
    public void appendStartEntityMergeQuery(StringBuilder query, EntityInfo entity, OperationInfo operation) {
        query.append("call ")
                .append(getNamePrefix())
                .append(operation.getMethodName())
                .append(" (");
    }

    @Override
    public void appendStartEntityUpdateQuery(StringBuilder query, EntityInfo entity, OperationInfo operation) {
        query.append("call ")
                .append(getNamePrefix())
                .append(operation.getMethodName())
                .append(" (");
    }

    @Override
    public void appendStartCustomDeleteQuery(StringBuilder query, OperationInfo operation) {
        query.append("call ")
                .append(getNamePrefix())
                .append(operation.getMethodName())
                .append(" (");
    }

    @Override
    public void appendStartCustomInsertQuery(StringBuilder query, OperationInfo operation) {
        query.append("call ")
                .append(getNamePrefix())
                .append(operation.getMethodName())
                .append(" (");
    }

    @Override
    public void appendStartCustomUpdateQuery(StringBuilder query, OperationInfo operation) {
        query.append("call ")
                .append(getNamePrefix())
                .append(operation.getMethodName())
                .append(" (");
    }

    @Override
    public void appendEndEntityDeleteByIdQuery(StringBuilder query, EntityInfo entity, OperationInfo operation, boolean requireSeparator) {
        appendEndQuery(query, requireSeparator);
    }

    @Override
    public void appendEndEntityInsertQuery(StringBuilder query, EntityInfo entity, OperationInfo operation, boolean requireSeparator) {
        appendEndQuery(query, requireSeparator);
    }

    @Override
    public void appendEndEntityLastInsertedIdQuery(StringBuilder query, EntityInfo entity, OperationInfo operation, boolean requireSeparator) {
        appendEndQuery(query, requireSeparator);
    }

    @Override
    public void appendEndEntityMergeQuery(StringBuilder query, EntityInfo entity, OperationInfo operation, boolean requireSeparator) {
        appendEndQuery(query, requireSeparator);
    }

    @Override
    public void appendEndEntityUpdateQuery(StringBuilder query, EntityInfo entity, OperationInfo operation, boolean requireSeparator) {
        appendEndQuery(query, requireSeparator);
    }

    @Override
    public void appendEndCustomDeleteQuery(StringBuilder query, OperationInfo operation, boolean requireSeparator) {
        appendEndQuery(query, requireSeparator);
    }

    @Override
    public void appendEndCustomInsertQuery(StringBuilder query, OperationInfo operation, boolean requireSeparator) {
        appendEndQuery(query, requireSeparator);
    }

    @Override
    public void appendEndCustomUpdateQuery(StringBuilder query, OperationInfo operation, boolean requireSeparator) {
        appendEndQuery(query, requireSeparator);
    }

    public void appendEndQuery(StringBuilder query, boolean requireSeparator) {
        if (requireSeparator) {
            query.append("\n)");
        } else {
            query.append(")");
        }
    }

    @Override
    public String firstFieldSeparator() {
        return "\n    ";
    }

    @Override
    public String fieldSeparator() {
        return ",\n    ";
    }
}
