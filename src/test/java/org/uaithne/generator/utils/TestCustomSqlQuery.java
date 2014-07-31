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
package org.uaithne.generator.utils;

import java.lang.annotation.Annotation;
import org.uaithne.annotations.sql.CustomSqlQuery;

public class TestCustomSqlQuery implements CustomSqlQuery {

    public String[] query = new String[]{};
    public String[] select = new String[]{};
    public String[] from = new String[]{};
    public String tableAlias = "";
    public String[] where = new String[]{};
    public String[] groupBy = new String[]{};
    public String[] orderBy = new String[]{};
    public String[] beforeQuery = new String[]{};
    public String[] afterQuery = new String[]{};
    public String[] beforeSelectExpression = new String[]{};
    public String[] afterSelectExpression = new String[]{};
    public String[] beforeFromExpression = new String[]{};
    public String[] afterFromExpression = new String[]{};
    public String[] beforeWhereExpression = new String[]{};
    public String[] afterWhereExpression = new String[]{};
    public String[] beforeGroupByExpression = new String[]{};
    public String[] afterGroupByExpression = new String[]{};
    public String[] beforeOrderByExpression = new String[]{};
    public String[] afterOrderByExpression = new String[]{};
    public String[] insertInto = new String[]{};
    public String[] beforeInsertIntoExpression = new String[]{};
    public String[] afterInsertIntoExpression = new String[]{};
    public String[] insertValues = new String[]{};
    public String[] beforeInsertValuesExpression = new String[]{};
    public String[] afterInsertValuesExpression = new String[]{};
    public String[] updateSet = new String[]{};
    public String[] beforeUpdateSetExpression = new String[]{};
    public String[] afterUpdateSetExpression = new String[]{};
    public String[] excludeEntityFields = new String[]{};

    @Override
    public String[] query() {
        return query;
    }

    @Override
    public String[] select() {
        return select;
    }

    @Override
    public String[] from() {
        return from;
    }

    @Override
    public String tableAlias() {
        return tableAlias;
    }

    @Override
    public String[] where() {
        return where;
    }

    @Override
    public String[] groupBy() {
        return groupBy;
    }

    @Override
    public String[] orderBy() {
        return orderBy;
    }

    @Override
    public String[] beforeQuery() {
        return beforeQuery;
    }

    @Override
    public String[] afterQuery() {
        return afterQuery;
    }

    @Override
    public String[] beforeSelectExpression() {
        return beforeSelectExpression;
    }

    @Override
    public String[] afterSelectExpression() {
        return afterSelectExpression;
    }

    @Override
    public String[] beforeFromExpression() {
        return beforeFromExpression;
    }

    @Override
    public String[] afterFromExpression() {
        return afterFromExpression;
    }

    @Override
    public String[] beforeWhereExpression() {
        return beforeWhereExpression;
    }

    @Override
    public String[] afterWhereExpression() {
        return afterWhereExpression;
    }

    @Override
    public String[] beforeGroupByExpression() {
        return beforeGroupByExpression;
    }

    @Override
    public String[] afterGroupByExpression() {
        return afterGroupByExpression;
    }

    @Override
    public String[] beforeOrderByExpression() {
        return beforeOrderByExpression;
    }

    @Override
    public String[] afterOrderByExpression() {
        return afterOrderByExpression;
    }

    @Override
    public String[] insertInto() {
        return insertInto;
    }

    @Override
    public String[] beforeInsertIntoExpression() {
        return beforeInsertIntoExpression;
    }

    @Override
    public String[] afterInsertIntoExpression() {
        return afterInsertIntoExpression;
    }

    @Override
    public String[] insertValues() {
        return insertValues;
    }

    @Override
    public String[] beforeInsertValuesExpression() {
        return beforeInsertValuesExpression;
    }

    @Override
    public String[] afterInsertValuesExpression() {
        return afterInsertValuesExpression;
    }

    @Override
    public String[] updateSet() {
        return updateSet;
    }

    @Override
    public String[] beforeUpdateSetExpression() {
        return beforeUpdateSetExpression;
    }

    @Override
    public String[] afterUpdateSetExpression() {
        return afterUpdateSetExpression;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return CustomSqlQuery.class;
    }

    @Override
    public String[] excludeEntityFields() {
        return excludeEntityFields;
    }
}