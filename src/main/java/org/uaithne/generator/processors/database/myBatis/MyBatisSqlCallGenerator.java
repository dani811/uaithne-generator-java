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

import javax.lang.model.type.MirroredTypeException;
import javax.tools.Diagnostic;
import org.uaithne.annotations.myBatis.MyBatisTypeHandler;
import org.uaithne.annotations.sql.JdbcTypes;
import org.uaithne.generator.commons.DataTypeInfo;
import org.uaithne.generator.commons.EntityInfo;
import org.uaithne.generator.commons.FieldInfo;
import org.uaithne.generator.commons.NamesGenerator;
import org.uaithne.generator.commons.OperationInfo;
import org.uaithne.generator.processors.database.QueryGeneratorConfiguration;
import org.uaithne.generator.processors.database.sql.SqlCallGenerator;

public abstract class MyBatisSqlCallGenerator extends SqlCallGenerator {

    public String getNamePrefix() {
        QueryGeneratorConfiguration configuration = getConfiguration();
        if (configuration == null) {
            return "";
        }

        String result = configuration.getCallPrefix();
        if (result == null) {
            return "";
        }
        return result;
    }

    @Override
    public void appendInParameter(StringBuilder query, FieldInfo field) {
        query.append("#{")
                .append(field.getName())
                .append(getJdbcTypeAttribute(field))
                .append(getTypeHandler(field))
                .append(",mode=IN}");
    }

    @Override
    public void appendOutParameter(StringBuilder query, FieldInfo field) {

        query.append("#{")
                .append(field.getName())
                .append(getJdbcTypeAttribute(field))
                .append(getTypeHandler(field))
                .append(",mode=OUT}");
    }

    @Override
    public void appendInOutParameter(StringBuilder query, FieldInfo field) {

        query.append("#{")
                .append(field.getName())
                .append(getJdbcTypeAttribute(field))
                .append(getTypeHandler(field))
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
    public void appendFieldSeparator(StringBuilder query) {
        query.append(",\n    ");
    }

    @Override
    public void appendFirstFieldSeparator(StringBuilder query) {
        query.append("\n    ");
    }
    
    public String getJdbcTypeAttribute(FieldInfo field) {
        JdbcTypes jdbcType = getJdbcType(field);
        if (jdbcType == null) {
            getProcessingEnv().getMessager().printMessage(Diagnostic.Kind.ERROR, "Uknown jdbc data type. Use @JdbcType annotation for specify the jdbc data type.", field.getElement());
            return "";
        }
        return ",jdbcType=" + jdbcType.name();
    }

    public String getTypeHandler(FieldInfo field) {
        MyBatisTypeHandler th = field.getAnnotation(MyBatisTypeHandler.class);

        if (th == null) {
            return "";
        }

        DataTypeInfo typeHandler;
        try {
            typeHandler = NamesGenerator.createResultDataType(th.value());
        } catch (MirroredTypeException ex) {
            // See: http://blog.retep.org/2009/02/13/getting-class-values-from-annotations-in-an-annotationprocessor/
            typeHandler = NamesGenerator.createDataTypeFor(ex.getTypeMirror());
        }
        if (typeHandler == null) {
            getProcessingEnv().getMessager().printMessage(Diagnostic.Kind.ERROR, "Unable to find the type handler", field.getElement());
            return "";
        }

        return ",typeHandler=" + typeHandler.getQualifiedNameWithoutGenerics();
    }

}
