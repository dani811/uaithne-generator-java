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
package org.uaithne.annotations.sql;

public @interface CustomSqlQuery {
    String[] query() default {};
    String[] select() default {};
    String[] from() default {};
    String tableAlias() default "";
    String[] where() default {};
    String[] groupBy() default {};
    String[] orderBy() default {};
    String[] beforeQuery() default {};
    String[] afterQuery() default {};
    String[] beforeSelectExpression() default {};
    String[] afterSelectExpression() default {};
    String[] beforeFromExpression() default {};
    String[] afterFromExpression() default {};
    String[] beforeWhereExpression() default {};
    String[] afterWhereExpression() default {};
    String[] beforeGroupByExpression() default {};
    String[] afterGroupByExpression() default {};
    String[] beforeOrderByExpression() default {};
    String[] afterOrderByExpression() default {};
    String[] insertInto() default {};
    String[] beforeInsertIntoExpression() default {};
    String[] afterInsertIntoExpression() default {};
    String[] insertValues() default {};
    String[] beforeInsertValuesExpression() default {};
    String[] afterInsertValuesExpression() default {};
    String[] updateSet() default {};
    String[] beforeUpdateSetExpression() default {};
    String[] afterUpdateSetExpression() default {};
    String[] excludeEntityFields() default {};
    boolean isProcedureInvocation() default false;
}
