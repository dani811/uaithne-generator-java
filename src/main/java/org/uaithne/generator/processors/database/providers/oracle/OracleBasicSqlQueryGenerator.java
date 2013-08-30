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
import org.uaithne.annotations.sql.CustomSqlQuery;
import org.uaithne.generator.commons.EntityInfo;
import org.uaithne.generator.commons.FieldInfo;
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
    public boolean insertQueryIncludeId() {
        return true;
    }
    
    @Override
    public String[] getDefaultIdNextValue(EntityInfo entity, FieldInfo field) {
        return new String[] {"seq_" + getTableName(entity)[0] + ".nextval"};
    }
    
    @Override
    public String[] getDefaultIdCurrentValue(EntityInfo entity, FieldInfo field) {
        return new String[] {"select seq_" + getTableName(entity)[0] + ".currval from dual"};
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
    public String selectPageAfterWhere() {
        return null;
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
    public void appendOrderBy(StringBuilder result, FieldInfo orderBy, CustomSqlQuery customQuery) {
        if (orderBy != null) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Unsuported sql order by generation", orderBy.getElement());
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
    public String getParameterValue(FieldInfo field) {
        return "@" + field.getName();
    }
    
}
