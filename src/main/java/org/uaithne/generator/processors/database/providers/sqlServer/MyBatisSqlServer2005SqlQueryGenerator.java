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
package org.uaithne.generator.processors.database.providers.sqlServer;

import java.util.ArrayList;
import org.uaithne.annotations.Comparators;
import org.uaithne.annotations.sql.CustomSqlQuery;
import org.uaithne.generator.commons.EntityInfo;
import org.uaithne.generator.commons.FieldInfo;
import org.uaithne.generator.processors.database.myBatis.MyBatisSqlQueryGenerator;

public class MyBatisSqlServer2005SqlQueryGenerator extends MyBatisSqlQueryGenerator {
    
    @Override
    public String currentSqlDate() {
        return "current_timestamp";
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
        return new String[] {"next value for " + getIdSequenceName(entity, field)};
    }
    
    @Override
    public String[] getIdSequenceCurrentValue(EntityInfo entity, FieldInfo field) {
        return new String[] {"select current_value from sys.sequences where name = '" + getIdSequenceName(entity, field) + "'"};
    }

    @Override
    public String[] envolveInSelectPage(String[] query) {
        String[] r = new String[query.length + 9];
        r[0] = "<if test='offset != null or maxRowNumber != null'> select * from (</if>";
        r[1] = "";
        System.arraycopy(query, 0, r, 2, query.length);
        r[r.length - 7] = "";
        r[r.length - 6] = "<if test='offset != null or maxRowNumber != null'> ) t";
        r[r.length - 5] = "    <where>";
        r[r.length - 4] = "        <if test='offset != null'>rownumber__ &gt; #{offset,jdbcType=NUMERIC}</if>";
        r[r.length - 3] = "        <if test='maxRowNumber != null'>and rownumber__ &lt;= #{maxRowNumber,jdbcType=NUMERIC}</if>";
        r[r.length - 2] = "    </where>";
        r[r.length - 1] = "</if>";
        return r;
    }

    @Override
    public String selectPageBeforeSelect() {
        return null;
    }

    @Override
    public boolean appendSelectPageAfterWhere(StringBuilder result, boolean requireAnd) {
        return false;
    }
    
    @Override
    public String selectPageAfterOrderBy() {
        return null;
    }

    @Override
    public String[] envolveInSelectOneRow(String[] query) {
        return query;
    }

    @Override
    public String selectOneRowBeforeSelect() {
        return "top 1";
    }

    @Override
    public String selectOneRowAfterWhere() {
        return null;
    }

    @Override
    public String selectOneRowAfterOrderBy() {
        return null;
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
    }

    @Override
    public void appendOrderByAfterSelectForSelectPage(StringBuilder result, ArrayList<FieldInfo> orderBys, CustomSqlQuery customQuery) {
        appendOrderByContent(result, orderBys, ",\n    ", "", ", ", "row_number() over (order by ", ") as rownumber__");
    }

    @Override
    public String translateComparator(Comparators comparator) {
        if (comparator == null) {
            return super.translateComparator(comparator);
        }
        switch (comparator) {
            case START_WITH:     return "[[column]] like ([[value]] + '%')";
            case NOT_START_WITH: return "[[column]] not like ([[value]] + '%')";
            case END_WITH:       return "[[column]] like ('%' + [[value]])";
            case NOT_END_WITH:   return "[[column]] not like ('%' + [[value]])";
            case START_WITH_INSENSITIVE:     return "lower([[column]]) like (lower([[value]]) + '%')";
            case NOT_START_WITH_INSENSITIVE: return "lower([[column]]) not like (lower([[value]]) + '%')";
            case END_WITH_INSENSITIVE:       return "lower([[column]]) like ('%' + lower([[value]]))";
            case NOT_END_WITH_INSENSITIVE:   return "lower([[column]]) not like ('%' + lower([[value]]))";
            case CONTAINS:       return "[[column]] like ('%' + [[value]] + '%')";
            case NOT_CONTAINS:   return "[[column]] not like ('%' + [[value]] + '%')";
            case CONTAINS_INSENSITIVE:       return "lower([[column]]) like ('%' + lower([[value]]) + '%')";
            case NOT_CONTAINS_INSENSITIVE:   return "lower([[column]]) not like ('%' + lower([[value]]) + '%')";
            default:
                return super.translateComparator(comparator);
        }
    }
    
}
