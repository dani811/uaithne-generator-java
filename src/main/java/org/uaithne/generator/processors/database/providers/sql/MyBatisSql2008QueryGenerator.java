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
package org.uaithne.generator.processors.database.providers.sql;

import java.util.ArrayList;
import javax.tools.Diagnostic;
import org.uaithne.annotations.Comparators;
import org.uaithne.annotations.sql.CustomSqlQuery;
import org.uaithne.generator.commons.EntityInfo;
import org.uaithne.generator.commons.FieldInfo;
import org.uaithne.generator.processors.database.myBatis.MyBatisSqlQueryGenerator;

public class MyBatisSql2008QueryGenerator extends MyBatisSqlQueryGenerator {

    @Override
    public String currentSqlDate() {
        return "current_timestamp";
    }

    @Override
    public String falseValue() {
        return "false";
    }

    @Override
    public String trueValue() {
        return "true";
    }

    @Override
    public String[] getIdSequenceNextValue(EntityInfo entity, FieldInfo field) {
        getProcessingEnv().getMessager().printMessage(Diagnostic.Kind.ERROR,
                            "Unable to automatically generate the next and current id value query",
                            field.getElement());
        return null;
    }

    @Override
    public String[] getIdSequenceCurrentValue(EntityInfo entity, FieldInfo field) {
        getProcessingEnv().getMessager().printMessage(Diagnostic.Kind.ERROR,
                            "Unable to automatically generate the current and next id value query",
                            field.getElement());
        return null;
    }

    @Override
    public String[] envolveInSelectPage(String[] query) {
        return query;
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
        return "{[if test='offset != null or maxRowNumber != null']}\n"
             + "    {[if test='offset != null']}offset #{offset,jdbcType=NUMERIC} rows {[/if]}\n"
             + "    {[if test='maxRowNumber != null']}fetch next #{limit,jdbcType=NUMERIC} rows only{[/if]}\n"
             + "{[/if]}";
    }

    @Override
    public String[] envolveInSelectOneRow(String[] query) {
        return query;
    }

    @Override
    public String selectOneRowBeforeSelect() {
        return null;
    }

    @Override
    public String selectOneRowAfterWhere() {
        return null;
    }

    @Override
    public String selectOneRowAfterOrderBy() {
        return "fetch next 1 rows only";
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
