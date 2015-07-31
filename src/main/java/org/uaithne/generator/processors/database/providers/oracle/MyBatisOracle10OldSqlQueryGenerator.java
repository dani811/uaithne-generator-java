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

import java.util.ArrayList;
import org.uaithne.annotations.sql.CustomSqlQuery;
import org.uaithne.annotations.sql.JdbcType;
import org.uaithne.annotations.sql.JdbcTypes;
import org.uaithne.generator.commons.DataTypeInfo;
import org.uaithne.generator.commons.EntityInfo;
import org.uaithne.generator.commons.FieldInfo;
import org.uaithne.generator.processors.database.QueryGeneratorConfiguration;
import org.uaithne.generator.processors.database.myBatis.MyBatisSqlQueryGenerator;

public class MyBatisOracle10OldSqlQueryGenerator extends MyBatisSqlQueryGenerator {
    
    @Override
    public String currentSqlDate() {
        return "sysdate";
    }
    
    @Override
    public String falseValue() {
        return "0";
    }
    
    @Override
    public String trueValue() {
        return "1";
    }
    
    @Override
    public String[] getIdSequenceNextValue(EntityInfo entity, FieldInfo field) {
        return new String[] {getIdSequenceName(entity, field) + ".nextval"};
    }
    
    @Override
    public String[] getIdSequenceCurrentValue(EntityInfo entity, FieldInfo field) {
        return new String[] {"select " + getIdSequenceName(entity, field) + ".currval from dual"};
    }

    @Override
    public String[] envolveInSelectPage(String[] query) {
        String[] r = new String[query.length + 2];
        // Note: 'offset != null and maxRowNumber != null' must be 'offset != null or maxRowNumber != null'
        r[0] = "<if test='offset != null and maxRowNumber != null'> select * from (select t.*, rownum as oracle__rownum__ from (</if>";
        System.arraycopy(query, 0, r, 1, query.length);
        r[r.length - 1] = "<if test='offset != null and maxRowNumber != null'>) t) <where> <if test='offset != null'>oracle__rownum__ &gt; #{offset,jdbcType=NUMERIC}</if> <if test='maxRowNumber != null'>and oracle__rownum__ &lt;= #{maxRowNumber,jdbcType=NUMERIC}</if></where></if>";
        return r;
    }

    @Override
    public void appendSelectPageBeforeSelect(StringBuilder result, ArrayList<FieldInfo> orderBys, CustomSqlQuery customQuery) {
    }

    @Override
    public boolean appendSelectPageAfterWhere(StringBuilder result, boolean requireAnd, ArrayList<FieldInfo> orderBys, CustomSqlQuery customQuery) {
        return false;
    }
    
    @Override
    public void appendSelectPageAfterOrderBy(StringBuilder result, ArrayList<FieldInfo> orderBys, CustomSqlQuery customQuery) {
    }

    @Override
    public String[] envolveInSelectOneRow(String[] query) {
        return query;
    }

    @Override
    public void appendSelectOneRowBeforeSelect(StringBuilder result, ArrayList<FieldInfo> orderBys, CustomSqlQuery customQuery) {
    }

    @Override
    public boolean appendSelectOneRowAfterWhere(StringBuilder result, boolean requireAnd, ArrayList<FieldInfo> orderBys, CustomSqlQuery customQuery) {
        if (requireAnd) {
            result.append("\n    and ");
        } else {
            result.append("    ");
        }
        result.append("rownum = 1");
        return true;
    }

    @Override
    public void appendSelectOneRowAfterOrderBy(StringBuilder result, ArrayList<FieldInfo> orderBys, CustomSqlQuery customQuery) {
    }

    @Override
    public void appendOrderByForSelectOneRow(StringBuilder result, ArrayList<FieldInfo> orderBys, CustomSqlQuery customQuery) {
        appendOrderBy(result, orderBys, customQuery);
    }

    @Override
    public void appendOrderByAfterSelectForSelectOneRow(StringBuilder result, ArrayList<FieldInfo> orderBys, CustomSqlQuery customQuery) {
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
    public void appendInitialVersionValue(StringBuilder result, EntityInfo entity, FieldInfo field) {
    }

    @Override
    public void appendNextVersionValue(StringBuilder result, EntityInfo entity, FieldInfo field) {
    }

    @Override
    public void appendOrderByForSelectPage(StringBuilder result, ArrayList<FieldInfo> orderBys, CustomSqlQuery customQuery) {
        appendOrderBy(result, orderBys, customQuery);
    }

    @Override
    public void appendOrderByAfterSelectForSelectPage(StringBuilder result, ArrayList<FieldInfo> orderBys, CustomSqlQuery customQuery) {
    }
    
    @Override
    public JdbcTypes getJdbcType(FieldInfo field) {
        JdbcType ujt = field.getAnnotation(JdbcType.class);
        if (ujt != null) {
            return ujt.value();
        }
        
        DataTypeInfo dataType = field.getDataType();
        QueryGeneratorConfiguration configuration = getConfiguration();
        if (configuration != null) {
            JdbcTypes result = configuration.getCustomJdbcTypeMap().get(dataType.getQualifiedName());
            if (result != null) {
                return result;
            }
        }

        String name = field.getDataType().getSimpleName();
        if ("String".equals(name)) {
            return JdbcTypes.VARCHAR;
        } else if ("Date".equals(name)) {
            return JdbcTypes.TIMESTAMP;
        } else if ("Time".equals(name)) {
            return JdbcTypes.TIME;
        } else if ("Timestamp".equals(name)) {
            return JdbcTypes.TIMESTAMP;
        } else if ("List<String>".equals(name)) {
            return JdbcTypes.VARCHAR;
        } else if ("ArrayList<String>".equals(name)) {
            return JdbcTypes.VARCHAR;
        } else if ("List<Date>".equals(name)) {
            return JdbcTypes.TIMESTAMP;
        } else if ("ArrayList<Date>".equals(name)) {
            return JdbcTypes.TIMESTAMP;
        } else if ("List<Time>".equals(name)) {
            return JdbcTypes.TIME;
        } else if ("ArrayList<Time>".equals(name)) {
            return JdbcTypes.TIME;
        } else if ("List<Timestamp>".equals(name)) {
            return JdbcTypes.TIMESTAMP;
        } else if ("ArrayList<Timestamp>".equals(name)) {
            return JdbcTypes.TIMESTAMP;
        } else {
            return JdbcTypes.NUMERIC;
        }
    }
    
}
