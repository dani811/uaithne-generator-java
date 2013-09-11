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

import org.uaithne.generator.commons.EntityInfo;
import org.uaithne.generator.commons.FieldInfo;
import org.uaithne.generator.processors.database.myBatis.MyBatisSqlQueryGenerator;

public class MyBatisSqlServerSqlQueryGenerator extends MyBatisSqlQueryGenerator {
    
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
    public boolean insertQueryIncludeId() {
        return false;
    }
    
    @Override
    public String[] getDefaultIdNextValue(EntityInfo entity, FieldInfo field) {
        return null;
    }
    
    @Override
    public String[] getDefaultIdCurrentValue(EntityInfo entity, FieldInfo field) {
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
    public String selectPageAfterWhere() {
        return null;
    }
    
    @Override
    public String selectPageAfterOrderBy() {
        return "<if test='offset != null and maxRowNumber != null'><if test='offset != null'>offset #{offset,jdbcType=NUMERIC} rows </if><if test='maxRowNumber != null'>fetch next #{maxRowNumber,jdbcType=NUMERIC} rows only</if></if>";
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
    
}
