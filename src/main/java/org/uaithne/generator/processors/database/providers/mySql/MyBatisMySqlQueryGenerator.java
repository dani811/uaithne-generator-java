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
package org.uaithne.generator.processors.database.providers.mySql;

import java.util.ArrayList;
import org.uaithne.annotations.Comparators;
import org.uaithne.annotations.sql.CustomSqlQuery;
import org.uaithne.generator.commons.EntityInfo;
import org.uaithne.generator.commons.FieldInfo;
import org.uaithne.generator.processors.database.myBatis.MyBatisSqlQueryGenerator;

public class MyBatisMySqlQueryGenerator extends MyBatisSqlQueryGenerator {

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
        // nextval function must be manually defined. See: http://www.microshell.com/database/mysql/emulating-nextval-function-to-get-sequence-in-mysql/
        return new String[] {"nextval('" + getIdSequenceName(entity, field) + "')"};
    }

    @Override
    public String[] getIdSequenceCurrentValue(EntityInfo entity, FieldInfo field) {
        // currval function must be manually defined. See: http://www.microshell.com/database/mysql/emulating-nextval-function-to-get-sequence-in-mysql/
        return new String[] {"select currval('" + getIdSequenceName(entity, field) + "')"};
    }

    @Override
    public String[] envolveInSelectPage(String[] query) {
        return query;
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
        result.append("\n"
             + "{[if test='offset != null or limit != null']}\n"
             + "    {[if test='limit != null']}limit #{limit,jdbcType=NUMERIC}{[/if]}\n"
             + "    {[if test='offset != null']}offset #{offset,jdbcType=NUMERIC}{[/if]}\n"
             + "{[/if]}");
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
        return false;
    }

    @Override
    public void appendSelectOneRowAfterOrderBy(StringBuilder result, ArrayList<FieldInfo> orderBys, CustomSqlQuery customQuery) {
        result.append("\nlimit 1");
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
    public String translateComparator(Comparators comparator) {
        if (comparator == null) {
            return super.translateComparator(comparator);
        }
        switch (comparator) {
            case START_WITH:     return "[[column]] like concat([[value]], '%')";
            case NOT_START_WITH: return "[[column]] not like concat([[value]], '%')";
            case END_WITH:       return "[[column]] like concat('%', [[value]])";
            case NOT_END_WITH:   return "[[column]] not like concat('%', [[value]])";
            case START_WITH_INSENSITIVE:     return "lower([[column]]) like concat(lower([[value]]), '%')";
            case NOT_START_WITH_INSENSITIVE: return "lower([[column]]) not like concat(lower([[value]]), '%')";
            case END_WITH_INSENSITIVE:       return "lower([[column]]) like concat('%', lower([[value]]))";
            case NOT_END_WITH_INSENSITIVE:   return "lower([[column]]) not like concat('%', lower([[value]]))";
            case CONTAINS:       return "[[column]] like concat('%', [[value]], '%')";
            case NOT_CONTAINS:   return "[[column]] not like concat('%', [[value]], '%')";
            case CONTAINS_INSENSITIVE:       return "lower([[column]]) like concat('%', lower([[value]]), '%')";
            case NOT_CONTAINS_INSENSITIVE:   return "lower([[column]]) not like concat('%', lower([[value]]), '%')";
            default:
                return super.translateComparator(comparator);
        }
    }
    
}
