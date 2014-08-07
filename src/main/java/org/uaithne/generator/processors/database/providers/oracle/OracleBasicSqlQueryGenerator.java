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
import java.util.regex.Matcher;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;
import org.uaithne.annotations.sql.CustomSqlQuery;
import org.uaithne.generator.commons.EntityInfo;
import org.uaithne.generator.commons.FieldInfo;
import org.uaithne.generator.commons.OperationInfo;
import org.uaithne.generator.commons.Utils;
import org.uaithne.generator.processors.database.sql.BasicSqlQueryGenerator;

public class OracleBasicSqlQueryGenerator extends BasicSqlQueryGenerator {
    
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
        r[0] = "select * from (select t.*, rownum as oracle__rownum__ from (";
        System.arraycopy(query, 0, r, 1, query.length);
        r[r.length - 1] = ") t) where (@offset is null or oracle__rownum__ > @offset) and (@maxRowNumber is null or oracle__rownum__ <= @maxRowNumber)";
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
        return null;
    }

    @Override
    public String selectOneRowAfterWhere() {
        return "rownum = 1";
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
    public void appendOrderBy(StringBuilder result, ArrayList<FieldInfo> orderBys, CustomSqlQuery customQuery) {
        for (FieldInfo orderBy : orderBys) {
            getProcessingEnv().getMessager().printMessage(Diagnostic.Kind.ERROR, "Unsuported sql order by generation", orderBy.getElement());
        }
    }
    
    @Override
    public void appendStartSetValueIfNotNull(StringBuilder result, FieldInfo field) {
        result.append(getColumnName(field))
                .append(" = ")
                .append("NVL(")
                .append(getParameterValue(field))
                .append(", ")
                .append(getColumnName(field))
                .append(")");
    }
    
    @Override
    public void appendEndSetValueIfNotNull(StringBuilder result, boolean requireComma) {
        if (requireComma) {
            result.append(",");
        }
        
    }

    @Override
    public String getParameterValue(FieldInfo field, boolean ignoreValueWhenNull) {
        String name;
        ArrayList<FieldInfo> applicationParametersUsed = getConfiguration().getUsedApplicationParameters();
        if (applicationParametersUsed != null && applicationParametersUsed.contains(field)) {
            if (field.getRelated() != null) {
                field = field.getRelated();
            }
            name = "a" + Utils.firstUpper(field.getName());
        } else {
            name = "p" + Utils.firstUpper(field.getName());
        }
        if (ignoreValueWhenNull || field.getValueWhenNull() == null) {
            return name;
        } else if (field.isExcludedFromObject()) {
            return field.getValueWhenNull();
        } else {
            return "NVL(" + name + ", " + field.getValueWhenNull() + ")";
        }
    }

    @Override
    public void appendOrderByForSelectPage(StringBuilder result, ArrayList<FieldInfo> orderBys, CustomSqlQuery customQuery) {
        appendOrderBy(result, orderBys, customQuery);
    }

    @Override
    public void appendOrderByAfterSelectForSelectPage(StringBuilder result, ArrayList<FieldInfo> orderBys, CustomSqlQuery customQuery) {
    }

    @Override
    public void appendParameterValueOrDefaultInDatabaseWhenNullForInsert(StringBuilder result, OperationInfo operation, EntityInfo entity, FieldInfo field) {
        String defaultValue = getDefaultValue(entity, field);
        if (defaultValue == null) {
            Element element = operation.getElement();
            if (element == null) {
                element = entity.getElement();
            }
            getProcessingEnv().getMessager().printMessage(Diagnostic.Kind.ERROR, "You must set the way for retrieve the default value rule in the backend configuration", element);
            return;
        }
        result.append("NVL(")
                .append(getParameterValue(field))
                .append(", ")
                .append(defaultValue)
                .append(")");
    }

    @Override
    public FieldInfo getApplicationParameter(String name, Matcher matcher, OperationInfo operation) {
        FieldInfo result = super.getApplicationParameter(name, matcher, operation);
        if (result != null) {
            getConfiguration().addUsedApplicationParameter(result);
        }
        return result;
    }
    
}
