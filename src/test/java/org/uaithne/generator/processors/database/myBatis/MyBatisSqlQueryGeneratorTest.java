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

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import javax.tools.Diagnostic;
import org.junit.Test;
import static org.junit.Assert.*;
import org.uaithne.annotations.Comparators;
import org.uaithne.annotations.myBatis.MyBatisTypeHandler;
import org.uaithne.annotations.sql.CustomSqlQuery;
import org.uaithne.generator.commons.DataTypeInfo;
import org.uaithne.generator.commons.EntityInfo;
import org.uaithne.generator.commons.FieldInfo;
import org.uaithne.generator.processors.database.QueryGeneratorConfiguration;
import org.uaithne.generator.utils.MessageContent;
import org.uaithne.generator.utils.ProcessingEnviromentImpl;
import org.uaithne.generator.utils.TestCustomSqlQuery;

public class MyBatisSqlQueryGeneratorTest {

    @Test
    public void testAppendOrderBy() {
        StringBuilder result = new StringBuilder();
        ArrayList<FieldInfo> orderBys = getOrderBys(true);
        CustomSqlQuery customQuery = null;
        MyBatisSqlQueryGenerator instance = new MyBatisSqlQueryGeneratorImpl();
        instance.appendOrderBy(result, orderBys, customQuery);
        assertEquals("\n"
                + "{[trim prefix='order by ' suffix=' ' prefixOverrides=', ']}\n"
                + "    {[if test='optionalField != null']}${optionalField} {[/if]}, \n"
                + "    ${normalField}\n"
                + "{[/trim]}", result.toString());
    }

    @Test
    public void testAppendOrderByWithCustomQueryWithCustomOrderBy() {
        StringBuilder result = new StringBuilder();
        ArrayList<FieldInfo> orderBys = getOrderBys(true);
        TestCustomSqlQuery customQuery = new TestCustomSqlQuery();
        customQuery.beforeOrderByExpression = new String[]{"before", "expression"};
        customQuery.orderBy = new String[]{"expression", "code"};
        customQuery.afterOrderByExpression = new String[]{"after", "expression"};
        MyBatisSqlQueryGenerator instance = new MyBatisSqlQueryGeneratorImpl();
        instance.appendOrderBy(result, orderBys, customQuery);
        assertEquals("\n"
                + "order by\n"
                + "    before\n"
                + "    expression \n"
                + "    expression\n"
                + "    code \n"
                + "    after\n"
                + "    expression ", result.toString());
    }

    @Test
    public void testAppendOrderByWithCustomQueryWithCustomBeforeAndAfterOrderBy() {
        StringBuilder result = new StringBuilder();
        ArrayList<FieldInfo> orderBys = getOrderBys(true);
        TestCustomSqlQuery customQuery = new TestCustomSqlQuery();
        customQuery.beforeOrderByExpression = new String[]{"before", "expression"};
        customQuery.afterOrderByExpression = new String[]{"after", "expression"};
        MyBatisSqlQueryGenerator instance = new MyBatisSqlQueryGeneratorImpl();
        instance.appendOrderBy(result, orderBys, customQuery);
        assertEquals("\n"
                + "order by\n"
                + "    before\n"
                + "    expression \n"
                + "    {[trim prefix='' suffix='' prefixOverrides=', ']}\n"
                + "    {[if test='optionalField != null']}${optionalField} {[/if]}, \n"
                + "    ${normalField}\n"
                + "    {[/trim]}\n"
                + "    after\n"
                + "    expression ", result.toString());
    }

    @Test
    public void testAppendOrderByWithCustomQueryWithCustomAfterOrderBy() {
        StringBuilder result = new StringBuilder();
        ArrayList<FieldInfo> orderBys = getOrderBys(true);
        TestCustomSqlQuery customQuery = new TestCustomSqlQuery();
        customQuery.afterOrderByExpression = new String[]{"after", "expression"};
        MyBatisSqlQueryGenerator instance = new MyBatisSqlQueryGeneratorImpl();
        instance.appendOrderBy(result, orderBys, customQuery);
        assertEquals("\n"
                + "order by\n"
                + "    {[trim prefix='' suffix='' prefixOverrides=', ']}\n"
                + "    {[if test='optionalField != null']}${optionalField} {[/if]}, \n"
                + "    ${normalField}\n"
                + "    {[/trim]}\n"
                + "    after\n"
                + "    expression ", result.toString());
    }

    @Test
    public void testAppendOrderByWithCustomQueryWithoutCustomOrderBy() {
        StringBuilder result = new StringBuilder();
        ArrayList<FieldInfo> orderBys = getOrderBys(true);
        TestCustomSqlQuery customQuery = new TestCustomSqlQuery();
        MyBatisSqlQueryGenerator instance = new MyBatisSqlQueryGeneratorImpl();
        instance.appendOrderBy(result, orderBys, customQuery);
        assertEquals("\n"
                + "{[trim prefix='order by ' suffix=' ' prefixOverrides=', ']}\n"
                + "    {[if test='optionalField != null']}${optionalField} {[/if]}, \n"
                + "    ${normalField}\n"
                + "{[/trim]}", result.toString());
    }

    private ArrayList<FieldInfo> getOrderBys(boolean reverse) {
        ArrayList<FieldInfo> result = new ArrayList<FieldInfo>();
        FieldInfo field1 = new FieldInfo("normalField", new DataTypeInfo("int"));
        FieldInfo field2 = new FieldInfo("optionalField", new DataTypeInfo("int"));
        field2.setOptional(true);
        if (reverse) {
            result.add(field2);
            result.add(field1);
        } else {
            result.add(field1);
            result.add(field2);
        }
        return result;
    }

    @Test
    public void testAppendOrderByContentWithEmptyOrderBys() {
        StringBuilder result = new StringBuilder();
        ArrayList<FieldInfo> orderBys = new ArrayList<FieldInfo>();
        String start = "start";
        String separator = "separator";
        String commaSeparator = ", ";
        String startOrderBy = "startOrderBy";
        String endOrderBy = "endOrderBy";
        MyBatisSqlQueryGenerator instance = new MyBatisSqlQueryGeneratorImpl();
        instance.appendOrderByContent(result, orderBys, start, separator, commaSeparator, startOrderBy, endOrderBy);
        assertEquals("", result.toString());
    }

    @Test
    public void testAppendOrderByContentWithOnlyOneOrderByField() {
        StringBuilder result = new StringBuilder();
        ArrayList<FieldInfo> orderBys = new ArrayList<FieldInfo>();
        FieldInfo field1 = new FieldInfo("normalField", new DataTypeInfo("int"));
        orderBys.add(field1);
        String start = "start";
        String separator = "\n    ";
        String commaSeparator = ", ";
        String startOrderBy = "startOrderBy ";
        String endOrderBy = "endOrderBy";
        MyBatisSqlQueryGenerator instance = new MyBatisSqlQueryGeneratorImpl();
        instance.appendOrderByContent(result, orderBys, start, separator, commaSeparator, startOrderBy, endOrderBy);
        assertEquals("startstartOrderBy ${normalField}endOrderBy", result.toString());
    }

    @Test
    public void testAppendOrderByContentWithOnlyOneOrderByFieldWithForceValue() {
        StringBuilder result = new StringBuilder();
        ArrayList<FieldInfo> orderBys = new ArrayList<FieldInfo>();
        FieldInfo field1 = new FieldInfo("normalField", new DataTypeInfo("int"));
        field1.setForcedValue("field1FrocedValue");
        orderBys.add(field1);
        String start = "start";
        String separator = "\n    ";
        String commaSeparator = ", ";
        String startOrderBy = "startOrderBy ";
        String endOrderBy = " endOrderBy";
        MyBatisSqlQueryGenerator instance = new MyBatisSqlQueryGeneratorImpl();
        instance.appendOrderByContent(result, orderBys, start, separator, commaSeparator, startOrderBy, endOrderBy);
        assertEquals("startstartOrderBy field1FrocedValue endOrderBy", result.toString());
    }

    @Test
    public void testAppendOrderByContentWithOnlyOneOptionalOrderByField() {
        StringBuilder result = new StringBuilder();
        ArrayList<FieldInfo> orderBys = new ArrayList<FieldInfo>();
        FieldInfo field1 = new FieldInfo("optionalField", new DataTypeInfo("int"));
        field1.setOptional(true);
        orderBys.add(field1);
        String start = "start";
        String separator = "\n    ";
        String commaSeparator = ", ";
        String startOrderBy = "startOrderBy ";
        String endOrderBy = "endOrderBy";
        MyBatisSqlQueryGenerator instance = new MyBatisSqlQueryGeneratorImpl();
        instance.appendOrderByContent(result, orderBys, start, separator, commaSeparator, startOrderBy, endOrderBy);
        assertEquals("start{[if test='optionalField != null']}startOrderBy ${optionalField}endOrderBy{[/if]}", result.toString());
    }

    @Test
    public void testAppendOrderByContentWithOnlyOneOptionalOrderByFieldWithValueWhenNull() {
        StringBuilder result = new StringBuilder();
        ArrayList<FieldInfo> orderBys = new ArrayList<FieldInfo>();
        FieldInfo field1 = new FieldInfo("optionalField", new DataTypeInfo("int"));
        field1.setOptional(true);
        field1.setValueWhenNull("field1ValueWhenNull");
        orderBys.add(field1);
        String start = "start";
        String separator = "\n    ";
        String commaSeparator = ", ";
        String startOrderBy = "startOrderBy ";
        String endOrderBy = " endOrderBy";
        MyBatisSqlQueryGenerator instance = new MyBatisSqlQueryGeneratorImpl();
        instance.appendOrderByContent(result, orderBys, start, separator, commaSeparator, startOrderBy, endOrderBy);
        assertEquals("startstartOrderBy {[if test='optionalField != null']}${optionalField}{[/if]} {[if test='optionalField == null']}field1ValueWhenNull{[/if]} endOrderBy", result.toString());
    }

    @Test
    public void testAppendOrderByContentWithOnlyOneOptionalOrderByFieldWithForceValue() {
        StringBuilder result = new StringBuilder();
        ArrayList<FieldInfo> orderBys = new ArrayList<FieldInfo>();
        FieldInfo field1 = new FieldInfo("optionalField", new DataTypeInfo("int"));
        field1.setOptional(true);
        field1.setForcedValue("field1ForcedValue");
        orderBys.add(field1);
        String start = "start";
        String separator = "\n    ";
        String commaSeparator = ", ";
        String startOrderBy = "startOrderBy ";
        String endOrderBy = " endOrderBy";
        MyBatisSqlQueryGenerator instance = new MyBatisSqlQueryGeneratorImpl();
        instance.appendOrderByContent(result, orderBys, start, separator, commaSeparator, startOrderBy, endOrderBy);
        assertEquals("startstartOrderBy field1ForcedValue endOrderBy", result.toString());
    }

    @Test
    public void testAppendOrderByContentWithOnlyOneOptionalOrderByFieldWithValueWhenNullAndForceValue() {
        StringBuilder result = new StringBuilder();
        ArrayList<FieldInfo> orderBys = new ArrayList<FieldInfo>();
        FieldInfo field1 = new FieldInfo("optionalField", new DataTypeInfo("int"));
        field1.setOptional(true);
        field1.setValueWhenNull("field1ValueWhenNull");
        field1.setForcedValue("field1ForcedValue");
        orderBys.add(field1);
        String start = "start";
        String separator = "\n    ";
        String commaSeparator = ", ";
        String startOrderBy = "startOrderBy ";
        String endOrderBy = " endOrderBy";
        MyBatisSqlQueryGenerator instance = new MyBatisSqlQueryGeneratorImpl();
        instance.appendOrderByContent(result, orderBys, start, separator, commaSeparator, startOrderBy, endOrderBy);
        assertEquals("startstartOrderBy field1ForcedValue endOrderBy", result.toString());
    }

    @Test
    public void testAppendOrderByContentWithNormalAndOptionalOrderByFields() {
        StringBuilder result = new StringBuilder();
        ArrayList<FieldInfo> orderBys = getOrderBys(false);
        String start = "start";
        String separator = "\n    ";
        String commaSeparator = ", ";
        String startOrderBy = "startOrderBy ";
        String endOrderBy = "endOrderBy";
        MyBatisSqlQueryGenerator instance = new MyBatisSqlQueryGeneratorImpl();
        instance.appendOrderByContent(result, orderBys, start, separator, commaSeparator, startOrderBy, endOrderBy);
        assertEquals("startstartOrderBy \n"
                + "    ${normalField}\n"
                + "    {[if test='optionalField != null']}, ${optionalField}{[/if]}endOrderBy", result.toString());
    }

    @Test
    public void testAppendOrderByContentWithForcedValueAndOptionalOrderByFields() {
        StringBuilder result = new StringBuilder();
        ArrayList<FieldInfo> orderBys = new ArrayList<FieldInfo>();
        FieldInfo field1 = new FieldInfo("normalField", new DataTypeInfo("int"));
        field1.setForcedValue("normalFieldForcedValue");
        FieldInfo field2 = new FieldInfo("optionalField", new DataTypeInfo("int"));
        field2.setOptional(true);
        orderBys.add(field1);
        orderBys.add(field2);
        String start = "start";
        String separator = "\n    ";
        String commaSeparator = ", ";
        String startOrderBy = "startOrderBy ";
        String endOrderBy = "endOrderBy";
        MyBatisSqlQueryGenerator instance = new MyBatisSqlQueryGeneratorImpl();
        instance.appendOrderByContent(result, orderBys, start, separator, commaSeparator, startOrderBy, endOrderBy);
        assertEquals("startstartOrderBy \n"
                + "    normalFieldForcedValue\n"
                + "    {[if test='optionalField != null']}, ${optionalField}{[/if]}endOrderBy", result.toString());
    }

    @Test
    public void testAppendOrderByContentWithForcedValueAndOptionalOrderByFields2() {
        StringBuilder result = new StringBuilder();
        ArrayList<FieldInfo> orderBys = new ArrayList<FieldInfo>();
        FieldInfo field1 = new FieldInfo("normalField", new DataTypeInfo("int"));
        field1.setOptional(true);
        field1.setForcedValue("normalFieldForcedValue");
        FieldInfo field2 = new FieldInfo("optionalField", new DataTypeInfo("int"));
        field2.setOptional(true);
        orderBys.add(field1);
        orderBys.add(field2);
        String start = "start";
        String separator = "\n    ";
        String commaSeparator = ", ";
        String startOrderBy = "startOrderBy ";
        String endOrderBy = "endOrderBy";
        MyBatisSqlQueryGenerator instance = new MyBatisSqlQueryGeneratorImpl();
        instance.appendOrderByContent(result, orderBys, start, separator, commaSeparator, startOrderBy, endOrderBy);
        assertEquals("startstartOrderBy \n"
                + "    normalFieldForcedValue\n"
                + "    {[if test='optionalField != null']}, ${optionalField}{[/if]}endOrderBy", result.toString());
    }

    @Test
    public void testAppendOrderByContentWithForcedValueAndOptionalValueWhenNullOrderByFields() {
        StringBuilder result = new StringBuilder();
        ArrayList<FieldInfo> orderBys = new ArrayList<FieldInfo>();
        FieldInfo field1 = new FieldInfo("normalField", new DataTypeInfo("int"));
        field1.setForcedValue("normalFieldForcedValue");
        FieldInfo field2 = new FieldInfo("optionalField", new DataTypeInfo("int"));
        field2.setValueWhenNull("optionalFieldValueWhenNull");
        field2.setOptional(true);
        orderBys.add(field1);
        orderBys.add(field2);
        String start = "start";
        String separator = "\n    ";
        String commaSeparator = ", ";
        String startOrderBy = "startOrderBy ";
        String endOrderBy = "endOrderBy";
        MyBatisSqlQueryGenerator instance = new MyBatisSqlQueryGeneratorImpl();
        instance.appendOrderByContent(result, orderBys, start, separator, commaSeparator, startOrderBy, endOrderBy);
        assertEquals("startstartOrderBy \n"
                + "    normalFieldForcedValue\n"
                + "    {[if test='optionalField != null']}, ${optionalField}{[/if]} {[if test='optionalField == null']}, optionalFieldValueWhenNull{[/if]}endOrderBy", result.toString());
    }

    @Test
    public void testAppendOrderByContentWithForcedValueAndOptionalValueWhenNullOrderByFields2() {
        StringBuilder result = new StringBuilder();
        ArrayList<FieldInfo> orderBys = new ArrayList<FieldInfo>();
        FieldInfo field1 = new FieldInfo("normalField", new DataTypeInfo("int"));
        FieldInfo field2 = new FieldInfo("optionalField", new DataTypeInfo("int"));
        field2.setValueWhenNull("optionalFieldValueWhenNull");
        field2.setForcedValue("optionalFieldForcedValue");
        field2.setOptional(true);
        orderBys.add(field1);
        orderBys.add(field2);
        String start = "start";
        String separator = "\n    ";
        String commaSeparator = ", ";
        String startOrderBy = "startOrderBy ";
        String endOrderBy = "endOrderBy";
        MyBatisSqlQueryGenerator instance = new MyBatisSqlQueryGeneratorImpl();
        instance.appendOrderByContent(result, orderBys, start, separator, commaSeparator, startOrderBy, endOrderBy);
        assertEquals("startstartOrderBy \n"
                + "    ${normalField}\n"
                + "    , optionalFieldForcedValueendOrderBy", result.toString());
    }

    @Test
    public void testAppendOrderByContentWithForcedValueAndOptionalValueWhenNullOrderByFields3() {
        StringBuilder result = new StringBuilder();
        ArrayList<FieldInfo> orderBys = new ArrayList<FieldInfo>();
        FieldInfo field1 = new FieldInfo("normalField", new DataTypeInfo("int"));
        field1.setOptional(true);
        FieldInfo field2 = new FieldInfo("optionalField", new DataTypeInfo("int"));
        field2.setValueWhenNull("optionalFieldValueWhenNull");
        field2.setForcedValue("optionalFieldForcedValue");
        field2.setOptional(true);
        orderBys.add(field1);
        orderBys.add(field2);
        String start = "start";
        String separator = "\n    ";
        String commaSeparator = ", ";
        String startOrderBy = "startOrderBy ";
        String endOrderBy = "endOrderBy";
        MyBatisSqlQueryGenerator instance = new MyBatisSqlQueryGeneratorImpl();
        instance.appendOrderByContent(result, orderBys, start, separator, commaSeparator, startOrderBy, endOrderBy);
        assertEquals("start{[trim prefix='startOrderBy ' suffix='endOrderBy' prefixOverrides=', ']}\n"
                + "    {[if test='normalField != null']}${normalField} {[/if]}, \n"
                + "    optionalFieldForcedValuestart{[/trim]}", result.toString());
    }

    @Test
    public void testAppendOrderByContentWithNormalAndOptionalValueWhenNullOrderByFields() {
        StringBuilder result = new StringBuilder();
        ArrayList<FieldInfo> orderBys = new ArrayList<FieldInfo>();
        FieldInfo field1 = new FieldInfo("normalField", new DataTypeInfo("int"));
        FieldInfo field2 = new FieldInfo("optionalField", new DataTypeInfo("int"));
        field2.setValueWhenNull("optionalFieldValueWhenNull");
        field2.setOptional(true);
        orderBys.add(field1);
        orderBys.add(field2);
        String start = "start";
        String separator = "\n    ";
        String commaSeparator = ", ";
        String startOrderBy = "startOrderBy ";
        String endOrderBy = "endOrderBy";
        MyBatisSqlQueryGenerator instance = new MyBatisSqlQueryGeneratorImpl();
        instance.appendOrderByContent(result, orderBys, start, separator, commaSeparator, startOrderBy, endOrderBy);
        assertEquals("startstartOrderBy \n"
                + "    ${normalField}\n"
                + "    {[if test='optionalField != null']}, ${optionalField}{[/if]} {[if test='optionalField == null']}, optionalFieldValueWhenNull{[/if]}endOrderBy", result.toString());
    }

    @Test
    public void testAppendOrderByContentWithNormalAndOptionalValueWhenNullOrderByFields2() {
        StringBuilder result = new StringBuilder();
        ArrayList<FieldInfo> orderBys = new ArrayList<FieldInfo>();
        FieldInfo field1 = new FieldInfo("normalField", new DataTypeInfo("int"));
        field1.setOptional(true);
        FieldInfo field2 = new FieldInfo("optionalField", new DataTypeInfo("int"));
        field2.setValueWhenNull("optionalFieldValueWhenNull");
        field2.setOptional(true);
        orderBys.add(field1);
        orderBys.add(field2);
        String start = "start";
        String separator = "\n    ";
        String commaSeparator = ", ";
        String startOrderBy = "startOrderBy ";
        String endOrderBy = "endOrderBy";
        MyBatisSqlQueryGenerator instance = new MyBatisSqlQueryGeneratorImpl();
        instance.appendOrderByContent(result, orderBys, start, separator, commaSeparator, startOrderBy, endOrderBy);
        assertEquals("start{[trim prefix='startOrderBy ' suffix='endOrderBy' prefixOverrides=', ']}\n"
                + "    {[if test='normalField != null']}${normalField} {[/if]}\n"
                + "    {[if test='optionalField != null']}, ${optionalField} {[/if]} {[if test='optionalField == null']}, optionalFieldValueWhenNull {[/if]}start{[/trim]}", result.toString());
    }

    @Test
    public void testAppendOrderByContentWithOptionalAndNormalOrderByFields() {
        StringBuilder result = new StringBuilder();
        ArrayList<FieldInfo> orderBys = getOrderBys(true);
        String start = "start";
        String separator = "\n    ";
        String commaSeparator = ", ";
        String startOrderBy = "startOrderBy ";
        String endOrderBy = "endOrderBy";
        MyBatisSqlQueryGenerator instance = new MyBatisSqlQueryGeneratorImpl();
        instance.appendOrderByContent(result, orderBys, start, separator, commaSeparator, startOrderBy, endOrderBy);
        assertEquals("start{[trim prefix='startOrderBy ' suffix='endOrderBy' prefixOverrides=', ']}\n"
                + "    {[if test='optionalField != null']}${optionalField} {[/if]}, \n"
                + "    ${normalField}start{[/trim]}", result.toString());
    }

    @Test
    public void testAppendOrderByContentWithOptionalAndForcedValueOrderByFields() {
        StringBuilder result = new StringBuilder();
        ArrayList<FieldInfo> orderBys = new ArrayList<FieldInfo>();
        FieldInfo field1 = new FieldInfo("normalField", new DataTypeInfo("int"));
        field1.setForcedValue("normalFieldForcedValue");
        FieldInfo field2 = new FieldInfo("optionalField", new DataTypeInfo("int"));
        field2.setOptional(true);
        orderBys.add(field2);
        orderBys.add(field1);
        String start = "start";
        String separator = "\n    ";
        String commaSeparator = ", ";
        String startOrderBy = "startOrderBy ";
        String endOrderBy = "endOrderBy";
        MyBatisSqlQueryGenerator instance = new MyBatisSqlQueryGeneratorImpl();
        instance.appendOrderByContent(result, orderBys, start, separator, commaSeparator, startOrderBy, endOrderBy);
        assertEquals("start{[trim prefix='startOrderBy ' suffix='endOrderBy' prefixOverrides=', ']}\n"
                + "    {[if test='optionalField != null']}${optionalField} {[/if]}, \n"
                + "    normalFieldForcedValuestart{[/trim]}", result.toString());
    }

    @Test
    public void testAppendOrderByContentWithOptionalAndForcedValueOrderByFields2() {
        StringBuilder result = new StringBuilder();
        ArrayList<FieldInfo> orderBys = new ArrayList<FieldInfo>();
        FieldInfo field1 = new FieldInfo("normalField", new DataTypeInfo("int"));
        field1.setOptional(true);
        field1.setForcedValue("normalFieldForcedValue");
        FieldInfo field2 = new FieldInfo("optionalField", new DataTypeInfo("int"));
        field2.setOptional(true);
        orderBys.add(field2);
        orderBys.add(field1);
        String start = "start";
        String separator = "\n    ";
        String commaSeparator = ", ";
        String startOrderBy = "startOrderBy ";
        String endOrderBy = "endOrderBy";
        MyBatisSqlQueryGenerator instance = new MyBatisSqlQueryGeneratorImpl();
        instance.appendOrderByContent(result, orderBys, start, separator, commaSeparator, startOrderBy, endOrderBy);
        assertEquals("start{[trim prefix='startOrderBy ' suffix='endOrderBy' prefixOverrides=', ']}\n"
                + "    {[if test='optionalField != null']}${optionalField} {[/if]}, \n"
                + "    normalFieldForcedValuestart{[/trim]}", result.toString());
    }

    @Test
    public void testAppendOrderByContentWithOptionalValueWhenNullAndForcedValueOrderByFields() {
        StringBuilder result = new StringBuilder();
        ArrayList<FieldInfo> orderBys = new ArrayList<FieldInfo>();
        FieldInfo field1 = new FieldInfo("normalField", new DataTypeInfo("int"));
        field1.setForcedValue("normalFieldForcedValue");
        FieldInfo field2 = new FieldInfo("optionalField", new DataTypeInfo("int"));
        field2.setValueWhenNull("optionalFieldValueWhenNull");
        field2.setOptional(true);
        orderBys.add(field2);
        orderBys.add(field1);
        String start = "start";
        String separator = "\n    ";
        String commaSeparator = ", ";
        String startOrderBy = "startOrderBy ";
        String endOrderBy = "endOrderBy";
        MyBatisSqlQueryGenerator instance = new MyBatisSqlQueryGeneratorImpl();
        instance.appendOrderByContent(result, orderBys, start, separator, commaSeparator, startOrderBy, endOrderBy);
        assertEquals("startstartOrderBy \n"
                + "    {[if test='optionalField != null']}${optionalField}{[/if]} {[if test='optionalField == null']}optionalFieldValueWhenNull{[/if]}\n"
                + "    , normalFieldForcedValueendOrderBy", result.toString());
    }

    @Test
    public void testAppendOrderByContentWithOptionalValueWhenNullAndForcedValueOrderByFields2() {
        StringBuilder result = new StringBuilder();
        ArrayList<FieldInfo> orderBys = new ArrayList<FieldInfo>();
        FieldInfo field1 = new FieldInfo("normalField", new DataTypeInfo("int"));
        FieldInfo field2 = new FieldInfo("optionalField", new DataTypeInfo("int"));
        field2.setValueWhenNull("optionalFieldValueWhenNull");
        field2.setForcedValue("optionalFieldForcedValue");
        field2.setOptional(true);
        orderBys.add(field2);
        orderBys.add(field1);
        String start = "start";
        String separator = "\n    ";
        String commaSeparator = ", ";
        String startOrderBy = "startOrderBy ";
        String endOrderBy = "endOrderBy";
        MyBatisSqlQueryGenerator instance = new MyBatisSqlQueryGeneratorImpl();
        instance.appendOrderByContent(result, orderBys, start, separator, commaSeparator, startOrderBy, endOrderBy);
        assertEquals("startstartOrderBy \n"
                + "    optionalFieldForcedValue\n"
                + "    , ${normalField}endOrderBy", result.toString());
    }

    @Test
    public void testAppendOrderByContentWithOptionalValueWhenNullAndForcedValueOrderByFields3() {
        StringBuilder result = new StringBuilder();
        ArrayList<FieldInfo> orderBys = new ArrayList<FieldInfo>();
        FieldInfo field1 = new FieldInfo("normalField", new DataTypeInfo("int"));
        field1.setOptional(true);
        FieldInfo field2 = new FieldInfo("optionalField", new DataTypeInfo("int"));
        field2.setValueWhenNull("optionalFieldValueWhenNull");
        field2.setForcedValue("optionalFieldForcedValue");
        field2.setOptional(true);
        orderBys.add(field2);
        orderBys.add(field1);
        String start = "start";
        String separator = "\n    ";
        String commaSeparator = ", ";
        String startOrderBy = "startOrderBy ";
        String endOrderBy = "endOrderBy";
        MyBatisSqlQueryGenerator instance = new MyBatisSqlQueryGeneratorImpl();
        instance.appendOrderByContent(result, orderBys, start, separator, commaSeparator, startOrderBy, endOrderBy);
        assertEquals("startstartOrderBy \n"
                + "    optionalFieldForcedValue\n"
                + "    {[if test='normalField != null']}, ${normalField}{[/if]}endOrderBy", result.toString());
    }

    @Test
    public void testAppendOrderByContentWithOptionalValueWhenNullAndNormalOrderByFields() {
        StringBuilder result = new StringBuilder();
        ArrayList<FieldInfo> orderBys = new ArrayList<FieldInfo>();
        FieldInfo field1 = new FieldInfo("normalField", new DataTypeInfo("int"));
        FieldInfo field2 = new FieldInfo("optionalField", new DataTypeInfo("int"));
        field2.setValueWhenNull("optionalFieldValueWhenNull");
        field2.setOptional(true);
        orderBys.add(field2);
        orderBys.add(field1);
        String start = "start";
        String separator = "\n    ";
        String commaSeparator = ", ";
        String startOrderBy = "startOrderBy ";
        String endOrderBy = "endOrderBy";
        MyBatisSqlQueryGenerator instance = new MyBatisSqlQueryGeneratorImpl();
        instance.appendOrderByContent(result, orderBys, start, separator, commaSeparator, startOrderBy, endOrderBy);
        assertEquals("startstartOrderBy \n"
                + "    {[if test='optionalField != null']}${optionalField}{[/if]} {[if test='optionalField == null']}optionalFieldValueWhenNull{[/if]}\n"
                + "    , ${normalField}endOrderBy", result.toString());
    }

    @Test
    public void testAppendOrderByContentWithOptionalValueWhenNullAndNormalOrderByFields2() {
        StringBuilder result = new StringBuilder();
        ArrayList<FieldInfo> orderBys = new ArrayList<FieldInfo>();
        FieldInfo field1 = new FieldInfo("normalField", new DataTypeInfo("int"));
        field1.setOptional(true);
        FieldInfo field2 = new FieldInfo("optionalField", new DataTypeInfo("int"));
        field2.setValueWhenNull("optionalFieldValueWhenNull");
        field2.setOptional(true);
        orderBys.add(field2);
        orderBys.add(field1);
        String start = "start";
        String separator = "\n    ";
        String commaSeparator = ", ";
        String startOrderBy = "startOrderBy ";
        String endOrderBy = "endOrderBy";
        MyBatisSqlQueryGenerator instance = new MyBatisSqlQueryGeneratorImpl();
        instance.appendOrderByContent(result, orderBys, start, separator, commaSeparator, startOrderBy, endOrderBy);
        assertEquals("startstartOrderBy \n"
                + "    {[if test='optionalField != null']}${optionalField}{[/if]} {[if test='optionalField == null']}optionalFieldValueWhenNull{[/if]}\n"
                + "    {[if test='normalField != null']}, ${normalField}{[/if]}endOrderBy", result.toString());
    }

    @Test
    public void testAppendOrderByContentWithTwoOptionalOrderByFields() {
        StringBuilder result = new StringBuilder();
        ArrayList<FieldInfo> orderBys = new ArrayList<FieldInfo>();
        FieldInfo field1 = new FieldInfo("optionalField1", new DataTypeInfo("int"));
        FieldInfo field2 = new FieldInfo("optionalField2", new DataTypeInfo("int"));
        field1.setOptional(true);
        field2.setOptional(true);
        orderBys.add(field1);
        orderBys.add(field2);
        String start = "start";
        String separator = "\n    ";
        String commaSeparator = ", ";
        String startOrderBy = "startOrderBy ";
        String endOrderBy = "endOrderBy";
        MyBatisSqlQueryGenerator instance = new MyBatisSqlQueryGeneratorImpl();
        instance.appendOrderByContent(result, orderBys, start, separator, commaSeparator, startOrderBy, endOrderBy);
        assertEquals("start{[trim prefix='startOrderBy ' suffix='endOrderBy' prefixOverrides=', ']}\n"
                + "    {[if test='optionalField1 != null']}${optionalField1} {[/if]}\n"
                + "    {[if test='optionalField2 != null']}, ${optionalField2} {[/if]}start{[/trim]}", result.toString());
    }

    @Test
    public void testAppendOrderByContentWithTwoNormalOrderByFields() {
        StringBuilder result = new StringBuilder();
        ArrayList<FieldInfo> orderBys = new ArrayList<FieldInfo>();
        FieldInfo field1 = new FieldInfo("normalField", new DataTypeInfo("int"));
        FieldInfo field2 = new FieldInfo("optionalField", new DataTypeInfo("int"));
        orderBys.add(field1);
        orderBys.add(field2);
        String start = "start";
        String separator = "\n    ";
        String commaSeparator = ", ";
        String startOrderBy = "startOrderBy ";
        String endOrderBy = "endOrderBy";
        MyBatisSqlQueryGenerator instance = new MyBatisSqlQueryGeneratorImpl();
        instance.appendOrderByContent(result, orderBys, start, separator, commaSeparator, startOrderBy, endOrderBy);
        assertEquals("startstartOrderBy \n"
                + "    ${normalField}\n"
                + "    , ${optionalField}endOrderBy", result.toString());
    }

    private FieldInfo getFieldWithTypeHandler() {
        FieldInfo field = new FieldInfo("myField", new DataTypeInfo("int"));
        field.addAnnotation(new MyBatisTypeHandler() {
            @Override
            public Class<?> value() {
                return Integer.class;
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return MyBatisTypeHandler.class;
            }
        });
        return field;
    }

    private FieldInfo getFieldWithValueWhenNull() {
        FieldInfo field = new FieldInfo("myFieldWithNull", new DataTypeInfo("int"));
        field.setValueWhenNull("valueWhenNullForMyFieldWithNull");
        field.addAnnotation(new MyBatisTypeHandler() {
            @Override
            public Class<?> value() {
                return Integer.class;
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return MyBatisTypeHandler.class;
            }
        });
        return field;
    }

    private FieldInfo getFieldWithIngoneFromObjectAndValueWhenNull() {
        FieldInfo field = new FieldInfo("myFieldOnlySql", new DataTypeInfo("int"));
        field.setValueWhenNull("valueWhenNullForMyFieldOnlySql");
        field.setExcludedFromObject(true);
        field.addAnnotation(new MyBatisTypeHandler() {
            @Override
            public Class<?> value() {
                return Integer.class;
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return MyBatisTypeHandler.class;
            }
        });
        return field;
    }

    @Test
    public void testGetParameterValue() {
        FieldInfo field = getFieldWithTypeHandler();
        MyBatisSqlQueryGenerator instance = new MyBatisSqlQueryGeneratorImpl();
        String expResult = "#{myField,jdbcType=INTEGER,typeHandler=java.lang.Integer}";
        String result = instance.getParameterValue(field);
        assertEquals(expResult, result);
    }

    @Test
    public void testGetParameterValue2() {
        FieldInfo field = getFieldWithValueWhenNull();
        MyBatisSqlQueryGenerator instance = new MyBatisSqlQueryGeneratorImpl();
        String expResult = "{[if test='myFieldWithNull != null']} #{myFieldWithNull,jdbcType=INTEGER,typeHandler=java.lang.Integer} {[/if]} {[if test='myFieldWithNull == null']} valueWhenNullForMyFieldWithNull {[/if]}";
        String result = instance.getParameterValue(field);
        assertEquals(expResult, result);
    }

    @Test
    public void testGetParameterValue3() {
        FieldInfo field = getFieldWithIngoneFromObjectAndValueWhenNull();
        MyBatisSqlQueryGenerator instance = new MyBatisSqlQueryGeneratorImpl();
        String expResult = "valueWhenNullForMyFieldOnlySql";
        String result = instance.getParameterValue(field);
        assertEquals(expResult, result);
    }

    @Test
    public void testGetParameterValueWithIgnoreValueWhenNull() {
        FieldInfo field = getFieldWithValueWhenNull();
        MyBatisSqlQueryGenerator instance = new MyBatisSqlQueryGeneratorImpl();
        String expResult = "#{myFieldWithNull,jdbcType=INTEGER,typeHandler=java.lang.Integer}";
        String result = instance.getParameterValue(field, true);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testGetParameterValue4() {
        FieldInfo field = new FieldInfo("field1", DataTypeInfo.INT_DATA_TYPE);
        MyBatisSqlQueryGenerator instance = new MyBatisSqlQueryGeneratorImpl();
        String expResult = "#{field1,jdbcType=INTEGER}";
        String result = instance.getParameterValue(field, false, false);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testGetParameterValue4t() {
        FieldInfo field = new FieldInfo("field1", DataTypeInfo.INT_DATA_TYPE);
        MyBatisSqlQueryGenerator instance = new MyBatisSqlQueryGeneratorImpl();
        String expResult = "#{field1,jdbcType=INTEGER}";
        String result = instance.getParameterValue(field, true, false);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testGetParameterValue4ft() {
        FieldInfo field = new FieldInfo("field1", DataTypeInfo.INT_DATA_TYPE);
        MyBatisSqlQueryGenerator instance = new MyBatisSqlQueryGeneratorImpl();
        String expResult = "#{field1,jdbcType=INTEGER}";
        String result = instance.getParameterValue(field, false, true);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testGetParameterValue4tt() {
        FieldInfo field = new FieldInfo("field1", DataTypeInfo.INT_DATA_TYPE);
        MyBatisSqlQueryGenerator instance = new MyBatisSqlQueryGeneratorImpl();
        String expResult = "#{field1,jdbcType=INTEGER}";
        String result = instance.getParameterValue(field, true, true);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testGetParameterValue5() {
        FieldInfo field = new FieldInfo("field1", DataTypeInfo.INT_DATA_TYPE);
        field.setValueWhenNull("field1ValueWhenNull");
        MyBatisSqlQueryGenerator instance = new MyBatisSqlQueryGeneratorImpl();
        String expResult = "{[if test='field1 != null']} #{field1,jdbcType=INTEGER} {[/if]} {[if test='field1 == null']} field1ValueWhenNull {[/if]}";
        String result = instance.getParameterValue(field, false, false);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testGetParameterValue5t() {
        FieldInfo field = new FieldInfo("field1", DataTypeInfo.INT_DATA_TYPE);
        field.setValueWhenNull("field1ValueWhenNull");
        MyBatisSqlQueryGenerator instance = new MyBatisSqlQueryGeneratorImpl();
        String expResult = "#{field1,jdbcType=INTEGER}";
        String result = instance.getParameterValue(field, true, false);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testGetParameterValue5ft() {
        FieldInfo field = new FieldInfo("field1", DataTypeInfo.INT_DATA_TYPE);
        field.setValueWhenNull("field1ValueWhenNull");
        MyBatisSqlQueryGenerator instance = new MyBatisSqlQueryGeneratorImpl();
        String expResult = "{[if test='field1 != null']} #{field1,jdbcType=INTEGER} {[/if]} {[if test='field1 == null']} field1ValueWhenNull {[/if]}";
        String result = instance.getParameterValue(field, false, true);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testGetParameterValue5tt() {
        FieldInfo field = new FieldInfo("field1", DataTypeInfo.INT_DATA_TYPE);
        field.setValueWhenNull("field1ValueWhenNull");
        MyBatisSqlQueryGenerator instance = new MyBatisSqlQueryGeneratorImpl();
        String expResult = "#{field1,jdbcType=INTEGER}";
        String result = instance.getParameterValue(field, true, true);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testGetParameterValue6() {
        FieldInfo field = new FieldInfo("field1", DataTypeInfo.INT_DATA_TYPE);
        field.setValueWhenNull("field1ValueWhenNull");
        field.setExcludedFromObject(true);
        MyBatisSqlQueryGenerator instance = new MyBatisSqlQueryGeneratorImpl();
        String expResult = "field1ValueWhenNull";
        String result = instance.getParameterValue(field, false, false);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testGetParameterValue6t() {
        FieldInfo field = new FieldInfo("field1", DataTypeInfo.INT_DATA_TYPE);
        field.setValueWhenNull("field1ValueWhenNull");
        field.setExcludedFromObject(true);
        MyBatisSqlQueryGenerator instance = new MyBatisSqlQueryGeneratorImpl();
        String expResult = "field1ValueWhenNull";
        String result = instance.getParameterValue(field, true, false);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testGetParameterValue6ft() {
        FieldInfo field = new FieldInfo("field1", DataTypeInfo.INT_DATA_TYPE);
        field.setValueWhenNull("field1ValueWhenNull");
        field.setExcludedFromObject(true);
        MyBatisSqlQueryGenerator instance = new MyBatisSqlQueryGeneratorImpl();
        String expResult = "field1ValueWhenNull";
        String result = instance.getParameterValue(field, false, true);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testGetParameterValue6tt() {
        FieldInfo field = new FieldInfo("field1", DataTypeInfo.INT_DATA_TYPE);
        field.setValueWhenNull("field1ValueWhenNull");
        field.setExcludedFromObject(true);
        MyBatisSqlQueryGenerator instance = new MyBatisSqlQueryGeneratorImpl();
        String expResult = "field1ValueWhenNull";
        String result = instance.getParameterValue(field, true, true);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testGetParameterValue7() {
        FieldInfo field = new FieldInfo("field1", DataTypeInfo.INT_DATA_TYPE);
        field.setForcedValue("field1ForcedValue");
        MyBatisSqlQueryGenerator instance = new MyBatisSqlQueryGeneratorImpl();
        String expResult = "field1ForcedValue";
        String result = instance.getParameterValue(field, false, false);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testGetParameterValue7t() {
        FieldInfo field = new FieldInfo("field1", DataTypeInfo.INT_DATA_TYPE);
        field.setForcedValue("field1ForcedValue");
        MyBatisSqlQueryGenerator instance = new MyBatisSqlQueryGeneratorImpl();
        String expResult = "field1ForcedValue";
        String result = instance.getParameterValue(field, true, false);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testGetParameterValue7ft() {
        FieldInfo field = new FieldInfo("field1", DataTypeInfo.INT_DATA_TYPE);
        field.setForcedValue("field1ForcedValue");
        MyBatisSqlQueryGenerator instance = new MyBatisSqlQueryGeneratorImpl();
        String expResult = "#{field1,jdbcType=INTEGER}";
        String result = instance.getParameterValue(field, false, true);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testGetParameterValue7tt() {
        FieldInfo field = new FieldInfo("field1", DataTypeInfo.INT_DATA_TYPE);
        field.setForcedValue("field1ForcedValue");
        MyBatisSqlQueryGenerator instance = new MyBatisSqlQueryGeneratorImpl();
        String expResult = "#{field1,jdbcType=INTEGER}";
        String result = instance.getParameterValue(field, true, true);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testGetParameterValue8() {
        FieldInfo field = new FieldInfo("field1", DataTypeInfo.INT_DATA_TYPE);
        field.setValueWhenNull("field1ValueWhenNull");
        field.setForcedValue("field1ForcedValue");
        MyBatisSqlQueryGenerator instance = new MyBatisSqlQueryGeneratorImpl();
        String expResult = "field1ForcedValue";
        String result = instance.getParameterValue(field, false, false);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testGetParameterValue8t() {
        FieldInfo field = new FieldInfo("field1", DataTypeInfo.INT_DATA_TYPE);
        field.setValueWhenNull("field1ValueWhenNull");
        field.setForcedValue("field1ForcedValue");
        MyBatisSqlQueryGenerator instance = new MyBatisSqlQueryGeneratorImpl();
        String expResult = "field1ForcedValue";
        String result = instance.getParameterValue(field, true, false);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testGetParameterValue8ft() {
        FieldInfo field = new FieldInfo("field1", DataTypeInfo.INT_DATA_TYPE);
        field.setValueWhenNull("field1ValueWhenNull");
        field.setForcedValue("field1ForcedValue");
        MyBatisSqlQueryGenerator instance = new MyBatisSqlQueryGeneratorImpl();
        String expResult = "{[if test='field1 != null']} #{field1,jdbcType=INTEGER} {[/if]} {[if test='field1 == null']} field1ValueWhenNull {[/if]}";
        String result = instance.getParameterValue(field, false, true);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testGetParameterValue8tt() {
        FieldInfo field = new FieldInfo("field1", DataTypeInfo.INT_DATA_TYPE);
        field.setValueWhenNull("field1ValueWhenNull");
        field.setForcedValue("field1ForcedValue");
        MyBatisSqlQueryGenerator instance = new MyBatisSqlQueryGeneratorImpl();
        String expResult = "#{field1,jdbcType=INTEGER}";
        String result = instance.getParameterValue(field, true, true);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testGetParameterValue9() {
        FieldInfo field = new FieldInfo("field1", DataTypeInfo.INT_DATA_TYPE);
        field.setForcedValue("field1ForcedValue");
        field.setExcludedFromObject(true);
        MyBatisSqlQueryGenerator instance = new MyBatisSqlQueryGeneratorImpl();
        String expResult = "field1ForcedValue";
        String result = instance.getParameterValue(field, false, false);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testGetParameterValue9t() {
        FieldInfo field = new FieldInfo("field1", DataTypeInfo.INT_DATA_TYPE);
        field.setForcedValue("field1ForcedValue");
        field.setExcludedFromObject(true);
        MyBatisSqlQueryGenerator instance = new MyBatisSqlQueryGeneratorImpl();
        String expResult = "field1ForcedValue";
        String result = instance.getParameterValue(field, true, false);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testGetParameterValue9ft() {
        FieldInfo field = new FieldInfo("field1", DataTypeInfo.INT_DATA_TYPE);
        field.setForcedValue("field1ForcedValue");
        field.setExcludedFromObject(true);
        MyBatisSqlQueryGenerator instance = new MyBatisSqlQueryGeneratorImpl();
        String expResult = "field1ForcedValue";
        String result = instance.getParameterValue(field, false, true);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testGetParameterValue9tt() {
        FieldInfo field = new FieldInfo("field1", DataTypeInfo.INT_DATA_TYPE);
        field.setForcedValue("field1ForcedValue");
        field.setExcludedFromObject(true);
        MyBatisSqlQueryGenerator instance = new MyBatisSqlQueryGeneratorImpl();
        String expResult = "field1ForcedValue";
        String result = instance.getParameterValue(field, true, true);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testGetParameterValue10() {
        FieldInfo field = new FieldInfo("field1", DataTypeInfo.INT_DATA_TYPE);
        field.setValueWhenNull("field1ValueWhenNull");
        field.setForcedValue("field1ForcedValue");
        field.setExcludedFromObject(true);
        MyBatisSqlQueryGenerator instance = new MyBatisSqlQueryGeneratorImpl();
        String expResult = "field1ForcedValue";
        String result = instance.getParameterValue(field, false, false);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testGetParameterValue10t() {
        FieldInfo field = new FieldInfo("field1", DataTypeInfo.INT_DATA_TYPE);
        field.setValueWhenNull("field1ValueWhenNull");
        field.setForcedValue("field1ForcedValue");
        field.setExcludedFromObject(true);
        MyBatisSqlQueryGenerator instance = new MyBatisSqlQueryGeneratorImpl();
        String expResult = "field1ForcedValue";
        String result = instance.getParameterValue(field, true, false);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testGetParameterValue10ft() {
        FieldInfo field = new FieldInfo("field1", DataTypeInfo.INT_DATA_TYPE);
        field.setValueWhenNull("field1ValueWhenNull");
        field.setForcedValue("field1ForcedValue");
        field.setExcludedFromObject(true);
        MyBatisSqlQueryGenerator instance = new MyBatisSqlQueryGeneratorImpl();
        String expResult = "field1ForcedValue";
        String result = instance.getParameterValue(field, false, true);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testGetParameterValue10tt() {
        FieldInfo field = new FieldInfo("field1", DataTypeInfo.INT_DATA_TYPE);
        field.setValueWhenNull("field1ValueWhenNull");
        field.setForcedValue("field1ForcedValue");
        field.setExcludedFromObject(true);
        MyBatisSqlQueryGenerator instance = new MyBatisSqlQueryGeneratorImpl();
        String expResult = "field1ForcedValue";
        String result = instance.getParameterValue(field, true, true);
        assertEquals(expResult, result);
    }

    @Test
    public void testAppendStartWhere() {
        StringBuilder result = new StringBuilder();
        MyBatisSqlQueryGenerator instance = new MyBatisSqlQueryGeneratorImpl();
        instance.appendStartWhere(result);
        assertEquals(result.toString(), "{[where]}");
    }

    @Test
    public void testAppendEndWhere() {
        StringBuilder result = new StringBuilder();
        String separator = "separator";
        MyBatisSqlQueryGenerator instance = new MyBatisSqlQueryGeneratorImpl();
        instance.appendEndWhere(result, separator);
        assertEquals(result.toString(), "separator{[/where]}");
    }

    @Test
    public void testAppendStartSet() {
        StringBuilder result = new StringBuilder();
        MyBatisSqlQueryGenerator instance = new MyBatisSqlQueryGeneratorImpl();
        instance.appendStartSet(result);
        assertEquals(result.toString(), "{[set]}");
    }

    @Test
    public void testAppendEndSet() {
        StringBuilder result = new StringBuilder();
        String separator = "separator";
        MyBatisSqlQueryGenerator instance = new MyBatisSqlQueryGeneratorImpl();
        instance.appendEndSet(result, separator);
        assertEquals(result.toString(), "separator{[/set]}");
    }

    @Test
    public void testAppendStartSetValueIfNotNull() {
        StringBuilder result = new StringBuilder();
        FieldInfo field = getFieldWithTypeHandler();
        MyBatisSqlQueryGenerator instance = new MyBatisSqlQueryGeneratorImpl();
        instance.appendStartSetValueIfNotNull(result, field);
        assertEquals(result.toString(), "{[if test='myField != null']}myField = #{myField,jdbcType=INTEGER,typeHandler=java.lang.Integer}");
    }

    @Test
    public void testAppendEndSetValueIfNotNullWithComma() {
        StringBuilder result = new StringBuilder();
        boolean requireComma = true;
        MyBatisSqlQueryGenerator instance = new MyBatisSqlQueryGeneratorImpl();
        instance.appendEndSetValueIfNotNull(result, requireComma);
        assertEquals(result.toString(), ",{[/if]}");
    }

    @Test
    public void testAppendEndSetValueIfNotNullWithoutComma() {
        StringBuilder result = new StringBuilder();
        boolean requireComma = false;
        MyBatisSqlQueryGenerator instance = new MyBatisSqlQueryGeneratorImpl();
        instance.appendEndSetValueIfNotNull(result, requireComma);
        assertEquals(result.toString(), "{[/if]}");
    }  
        


    @Test
    public void testAppendParameterValueOrDefaultInDatabaseWhenNullForInsert() {
        StringBuilder result = new StringBuilder();
        FieldInfo field = getFieldWithTypeHandler();
        MyBatisSqlQueryGenerator instance = new MyBatisSqlQueryGeneratorImpl();
        String expResult = "{[if test='myField != null']} #{myField,jdbcType=INTEGER,typeHandler=java.lang.Integer} {[/if]} {[if test='myField == null']} default {[/if]}";
        instance.appendParameterValueOrDefaultInDatabaseWhenNullForInsert(result, null, null, field);
        assertEquals(expResult, result.toString());
    }

    @Test
    public void testAppendConditionStartIfNull() {
        StringBuilder result = new StringBuilder();
        FieldInfo field = new FieldInfo("myField", new DataTypeInfo("int"));
        String separator = "separator";
        MyBatisSqlQueryGenerator instance = new MyBatisSqlQueryGeneratorImpl();
        instance.appendConditionStartIfNull(result, field, separator);
        assertEquals("{[if test='myField == null']}separator", result.toString());
    }

    @Test
    public void testAppendConditionStartIfNotNull() {
        StringBuilder result = new StringBuilder();
        FieldInfo field = new FieldInfo("myField", new DataTypeInfo("int"));
        String separator = "separator";
        MyBatisSqlQueryGenerator instance = new MyBatisSqlQueryGeneratorImpl();
        instance.appendConditionStartIfNotNull(result, field, separator);
        assertEquals("{[if test='myField != null']}separator", result.toString());
    }

    @Test
    public void testAppendConditionEndIf() {
        StringBuilder result = new StringBuilder();
        MyBatisSqlQueryGenerator instance = new MyBatisSqlQueryGeneratorImpl();
        instance.appendConditionEndIf(result);
        assertEquals("{[/if]}", result.toString());
    }

    @Test
    public void testTranslateNullComparator() {
        Comparators comparator = null;
        MyBatisSqlQueryGenerator instance = new MyBatisSqlQueryGeneratorImpl();
        String expResult = null;
        String result = instance.translateComparator(comparator);
        assertEquals(expResult, result);
    }

    @Test
    public void testTranslateComparator() {
        StringBuilder result = new StringBuilder();
        MyBatisSqlQueryGenerator instance = new MyBatisSqlQueryGeneratorImpl();
        for (Comparators comparator : Comparators.values()) {
            String comparatorTransalation = instance.translateComparator(comparator);
            result.append(comparatorTransalation);
            result.append("\n");
        }
        String expResult = "[[column]] = [[value]]\n"
                + "[[column]] <> [[value]]\n" 
                + "lower([[column]]) = lower([[value]])\n"
                + "lower([[column]]) <> lower([[value]])\n"
                + "[[column]] < [[value]]\n"
                + "[[column]] > [[value]]\n"
                + "[[column]] <= [[value]]\n"
                + "[[column]] >= [[value]]\n"
                + "[[column]] in {[foreach collection='[[name]]' open='(' separator=',' close=')' item='_item_[[name]]']} #{_item_[[name]][[jdbcType]][[typeHandler]]} {[/foreach]}\n"
                + "[[column]] not in {[foreach collection='[[name]]' open='(' separator=',' close=')' item='_item_[[name]]']} #{_item_[[name]][[jdbcType]][[typeHandler]]} {[/foreach]}\n"
                + "[[column]] like [[value]]\n"
                + "[[column]] not like [[value]]\n"
                + "lower([[column]]) like lower([[value]])\n"
                + "lower([[column]]) not like lower([[value]])\n"
                + "[[column]] like ([[value]] || '%')\n"
                + "[[column]] not like ([[value]] || '%')\n"
                + "[[column]] like ('%' || [[value]])\n"
                + "[[column]] not like ('%' || [[value]])\n"
                + "lower([[column]]) like (lower([[value]]) || '%')\n"
                + "lower([[column]]) not like (lower([[value]]) || '%')\n"
                + "lower([[column]]) like ('%' || lower([[value]]))\n"
                + "lower([[column]]) not like ('%' || lower([[value]]))\n"
                + "[[column]] like ('%' || [[value]] || '%')\n"
                + "[[column]] not like ('%' || [[value]] || '%')\n"
                + "lower([[column]]) like ('%' || lower([[value]]) || '%')\n"
                + "lower([[column]]) not like ('%' || lower([[value]]) || '%')\n";
        assertEquals(expResult, result.toString());
    }

    @Test
    public void testGetConditionElementValueWithNullRule() {
        String rule = null;
        FieldInfo field = null;
        CustomSqlQuery customQuery = null;
        MyBatisSqlQueryGenerator instance = new MyBatisSqlQueryGeneratorImpl();
        String expResult = null;
        String result = instance.getConditionElementValue(rule, field, customQuery);
        assertEquals(expResult, result);
    }

    @Test
    public void testGetConditionElementValueWithColumnRule() {
        FieldInfo field = getFieldWithTypeHandler();
        CustomSqlQuery customQuery = null;
        MyBatisSqlQueryGenerator instance = new MyBatisSqlQueryGeneratorImpl();
        String rule = "column";
        StringBuilder result = new StringBuilder();
        result.append(instance.getConditionElementValue(rule, field, customQuery));
        result.append(" ");
        rule = "COLUMN";
        result.append(instance.getConditionElementValue(rule, field, customQuery));
        String expResult = "myField myField";
        assertEquals(expResult, result.toString());
    }

    @Test
    public void testGetConditionElementValueWithNameRule() {
        FieldInfo field = getFieldWithTypeHandler();
        CustomSqlQuery customQuery = null;
        MyBatisSqlQueryGenerator instance = new MyBatisSqlQueryGeneratorImpl();
        String rule = "name";
        StringBuilder result = new StringBuilder();
        result.append(instance.getConditionElementValue(rule, field, customQuery));
        result.append(" ");
        rule = "NAME";
        result.append(instance.getConditionElementValue(rule, field, customQuery));
        String expResult = "myField myField";
        assertEquals(expResult, result.toString());
    }

    @Test
    public void testGetConditionElementValueWithValueRule() {
        FieldInfo field = getFieldWithTypeHandler();
        CustomSqlQuery customQuery = null;
        MyBatisSqlQueryGenerator instance = new MyBatisSqlQueryGeneratorImpl();
        String rule = "value";
        StringBuilder result = new StringBuilder();
        result.append(instance.getConditionElementValue(rule, field, customQuery));
        result.append(" ");
        rule = "VALUE";
        result.append(instance.getConditionElementValue(rule, field, customQuery));
        String expResult = "#{myField,jdbcType=INTEGER,typeHandler=java.lang.Integer} #{myField,jdbcType=INTEGER,typeHandler=java.lang.Integer}";
        assertEquals(expResult, result.toString());
    }

    @Test
    public void testGetConditionElementValueWithValueRule2() {
        FieldInfo field = getFieldWithValueWhenNull();
        CustomSqlQuery customQuery = null;
        MyBatisSqlQueryGenerator instance = new MyBatisSqlQueryGeneratorImpl();
        String rule = "value";
        StringBuilder result = new StringBuilder();
        result.append(instance.getConditionElementValue(rule, field, customQuery));
        result.append(" ");
        rule = "VALUE";
        result.append(instance.getConditionElementValue(rule, field, customQuery));
        String expResult = "{[if test='myFieldWithNull != null']} #{myFieldWithNull,jdbcType=INTEGER,typeHandler=java.lang.Integer} {[/if]} {[if test='myFieldWithNull == null']} valueWhenNullForMyFieldWithNull {[/if]} {[if test='myFieldWithNull != null']} #{myFieldWithNull,jdbcType=INTEGER,typeHandler=java.lang.Integer} {[/if]} {[if test='myFieldWithNull == null']} valueWhenNullForMyFieldWithNull {[/if]}";
        assertEquals(expResult, result.toString());
    }

    @Test
    public void testGetConditionElementValueWithValueRule3() {
        FieldInfo field = getFieldWithIngoneFromObjectAndValueWhenNull();
        CustomSqlQuery customQuery = null;
        MyBatisSqlQueryGenerator instance = new MyBatisSqlQueryGeneratorImpl();
        String rule = "value";
        StringBuilder result = new StringBuilder();
        result.append(instance.getConditionElementValue(rule, field, customQuery));
        result.append(" ");
        rule = "VALUE";
        result.append(instance.getConditionElementValue(rule, field, customQuery));
        String expResult = "valueWhenNullForMyFieldOnlySql valueWhenNullForMyFieldOnlySql";
        assertEquals(expResult, result.toString());
    }

    @Test
    public void testGetConditionElementValueNullableWithValueRule() {
        FieldInfo field = getFieldWithTypeHandler();
        CustomSqlQuery customQuery = null;
        MyBatisSqlQueryGenerator instance = new MyBatisSqlQueryGeneratorImpl();
        String rule = "valueNullable";
        StringBuilder result = new StringBuilder();
        result.append(instance.getConditionElementValue(rule, field, customQuery));
        result.append(" ");
        rule = "VALUE_NULLABLE";
        result.append(instance.getConditionElementValue(rule, field, customQuery));
        String expResult = "#{myField,jdbcType=INTEGER,typeHandler=java.lang.Integer} #{myField,jdbcType=INTEGER,typeHandler=java.lang.Integer}";
        assertEquals(expResult, result.toString());
    }

    @Test
    public void testGetConditionElementValueNullableWithValueRule2() {
        FieldInfo field = getFieldWithValueWhenNull();
        CustomSqlQuery customQuery = null;
        MyBatisSqlQueryGenerator instance = new MyBatisSqlQueryGeneratorImpl();
        String rule = "valueNullable";
        StringBuilder result = new StringBuilder();
        result.append(instance.getConditionElementValue(rule, field, customQuery));
        result.append(" ");
        rule = "VALUE_NULLABLE";
        result.append(instance.getConditionElementValue(rule, field, customQuery));
        String expResult = "#{myFieldWithNull,jdbcType=INTEGER,typeHandler=java.lang.Integer} #{myFieldWithNull,jdbcType=INTEGER,typeHandler=java.lang.Integer}";
        assertEquals(expResult, result.toString());
    }

    @Test
    public void testGetConditionElementValueNullableWithValueRule3() {
        FieldInfo field = getFieldWithIngoneFromObjectAndValueWhenNull();
        CustomSqlQuery customQuery = null;
        MyBatisSqlQueryGeneratorImpl instance = new MyBatisSqlQueryGeneratorImpl();
        String rule = "valueNullable";
        StringBuilder result = new StringBuilder();
        result.append(instance.getConditionElementValue(rule, field, customQuery));
        result.append(" ");
        rule = "VALUE_NULLABLE";
        result.append(instance.getConditionElementValue(rule, field, customQuery));
        String expResult = "FIELD_NOT_PRESENT_IN_OBJECT FIELD_NOT_PRESENT_IN_OBJECT";
        ArrayList<MessageContent> actualMsg = instance.getProcessingEnv().getMessager().getContent();
        ArrayList<MessageContent> expectedMsg = new ArrayList<MessageContent>();
        expectedMsg.add(new MessageContent(Diagnostic.Kind.ERROR,
                "The field 'myFieldOnlySql' is not present in the object, you cannot access to the inexistent property value using valueNullable",
                field.getElement()));
        expectedMsg.add(new MessageContent(Diagnostic.Kind.ERROR,
                "The field 'myFieldOnlySql' is not present in the object, you cannot access to the inexistent property value using valueNullable",
                field.getElement()));
        assertArrayEquals(expectedMsg.toArray(), actualMsg.toArray());
        assertEquals(expResult, result.toString());
    }

    @Test
    public void testGetConditionElementValueWhenNullWithValueRule() {
        FieldInfo field = getFieldWithTypeHandler();
        CustomSqlQuery customQuery = null;
        MyBatisSqlQueryGeneratorImpl instance = new MyBatisSqlQueryGeneratorImpl();
        String rule = "valueWhenNull";
        StringBuilder result = new StringBuilder();
        result.append(instance.getConditionElementValue(rule, field, customQuery));
        result.append(" ");
        rule = "VALUE_WHEN_NULL";
        result.append(instance.getConditionElementValue(rule, field, customQuery));
        String expResult = "UNKOWN_DEFAULT_VALUE UNKOWN_DEFAULT_VALUE";
        ArrayList<MessageContent> actualMsg = instance.getProcessingEnv().getMessager().getContent();
        ArrayList<MessageContent> expectedMsg = new ArrayList<MessageContent>();
        expectedMsg.add(new MessageContent(Diagnostic.Kind.ERROR,
                "The field 'myField' has no defined value when null, use @ValueWhenNull annotation for define one",
                field.getElement()));
        expectedMsg.add(new MessageContent(Diagnostic.Kind.ERROR,
                "The field 'myField' has no defined value when null, use @ValueWhenNull annotation for define one",
                field.getElement()));
        assertArrayEquals(expectedMsg.toArray(), actualMsg.toArray());
        assertEquals(expResult, result.toString());
    }

    @Test
    public void testGetConditionElementValueWhenNullWithValueRul2() {
        FieldInfo field = getFieldWithValueWhenNull();
        CustomSqlQuery customQuery = null;
        MyBatisSqlQueryGeneratorImpl instance = new MyBatisSqlQueryGeneratorImpl();
        String rule = "valueWhenNull";
        StringBuilder result = new StringBuilder();
        result.append(instance.getConditionElementValue(rule, field, customQuery));
        result.append(" ");
        rule = "VALUE_WHEN_NULL";
        result.append(instance.getConditionElementValue(rule, field, customQuery));
        String expResult = "valueWhenNullForMyFieldWithNull valueWhenNullForMyFieldWithNull";
        assertEquals(expResult, result.toString());
    }

    @Test
    public void testGetConditionElementValueWhenNullWithValueRul3() {
        FieldInfo field = getFieldWithIngoneFromObjectAndValueWhenNull();
        CustomSqlQuery customQuery = null;
        MyBatisSqlQueryGeneratorImpl instance = new MyBatisSqlQueryGeneratorImpl();
        String rule = "valueWhenNull";
        StringBuilder result = new StringBuilder();
        result.append(instance.getConditionElementValue(rule, field, customQuery));
        result.append(" ");
        rule = "VALUE_WHEN_NULL";
        result.append(instance.getConditionElementValue(rule, field, customQuery));
        String expResult = "valueWhenNullForMyFieldOnlySql valueWhenNullForMyFieldOnlySql";
        assertEquals(expResult, result.toString());
    }

    @Test
    public void testGetConditionElementValueWithJdbcTypeRule() {
        FieldInfo field = getFieldWithTypeHandler();
        CustomSqlQuery customQuery = null;
        MyBatisSqlQueryGenerator instance = new MyBatisSqlQueryGeneratorImpl();
        String rule = "jdbcType";
        StringBuilder result = new StringBuilder();
        result.append(instance.getConditionElementValue(rule, field, customQuery));
        result.append(" ");
        rule = "JDBC_TYPE";
        result.append(instance.getConditionElementValue(rule, field, customQuery));
        String expResult = ",jdbcType=INTEGER ,jdbcType=INTEGER";
        assertEquals(expResult, result.toString());
    }

    @Test
    public void testGetConditionElementValueWithTypeHandlerRule() {
        FieldInfo field = getFieldWithTypeHandler();
        CustomSqlQuery customQuery = null;
        MyBatisSqlQueryGenerator instance = new MyBatisSqlQueryGeneratorImpl();
        String rule = "typeHandler";
        StringBuilder result = new StringBuilder();
        result.append(instance.getConditionElementValue(rule, field, customQuery));
        result.append(" ");
        rule = "TYPE_HANDLER";
        result.append(instance.getConditionElementValue(rule, field, customQuery));
        String expResult = ",typeHandler=java.lang.Integer ,typeHandler=java.lang.Integer";
        assertEquals(expResult, result.toString());
    }

    @Test
    public void testGetConditionElementValueWithUndefinedRule() {
        FieldInfo field = getFieldWithTypeHandler();
        CustomSqlQuery customQuery = null;
        MyBatisSqlQueryGenerator instance = new MyBatisSqlQueryGeneratorImpl();
        String rule = "undefinedRule";
        String result = instance.getConditionElementValue(rule, field, customQuery);
        String expResult = null;
        assertEquals(expResult, result);
    }
    
    @Test
    public void testFinalizeQuery() {
        MyBatisSqlQueryGenerator instance = new MyBatisSqlQueryGeneratorImpl();
        String result = instance.finalizeQuery("a<b>c{[d]}e", null, null);
        String expResult = "a&lt;b&gt;c<d>e";
        assertEquals(expResult, result);
    }

    public class MyBatisSqlQueryGeneratorImpl extends MyBatisSqlQueryGenerator {

        @Override
        public void appendOrderByForSelectPage(StringBuilder result, ArrayList<FieldInfo> orderBys, CustomSqlQuery customQuery) {
            throw new UnsupportedOperationException("No needed for test.");
        }

        @Override
        public void appendOrderByAfterSelectForSelectPage(StringBuilder result, ArrayList<FieldInfo> orderBys, CustomSqlQuery customQuery) {
            throw new UnsupportedOperationException("No needed for test.");
        }

        @Override
        public void appendOrderByForSelectOneRow(StringBuilder result, ArrayList<FieldInfo> orderBys, CustomSqlQuery customQuery) {
            throw new UnsupportedOperationException("No needed for test.");
        }

        @Override
        public void appendOrderByAfterSelectForSelectOneRow(StringBuilder result, ArrayList<FieldInfo> orderBys, CustomSqlQuery customQuery) {
            throw new UnsupportedOperationException("No needed for test.");
        }

        @Override
        public String currentSqlDate() {
            throw new UnsupportedOperationException("No needed for test.");
        }

        @Override
        public String falseValue() {
            throw new UnsupportedOperationException("No needed for test.");
        }

        @Override
        public String trueValue() {
            throw new UnsupportedOperationException("No needed for test.");
        }

        @Override
        public String[] envolveInSelectPage(String[] query) {
            throw new UnsupportedOperationException("No needed for test.");
        }

        @Override
        public void appendSelectPageBeforeSelect(StringBuilder result, ArrayList<FieldInfo> orderBys, CustomSqlQuery customQuery) {
            throw new UnsupportedOperationException("No needed for test.");
        }

        @Override
        public boolean appendSelectPageAfterWhere(StringBuilder result, boolean requireAnd, ArrayList<FieldInfo> orderBys, CustomSqlQuery customQuery) {
            throw new UnsupportedOperationException("No needed for test.");
        }

        @Override
        public void appendSelectPageAfterOrderBy(StringBuilder result, ArrayList<FieldInfo> orderBys, CustomSqlQuery customQuery) {
            throw new UnsupportedOperationException("No needed for test.");
        }

        @Override
        public String[] envolveInSelectOneRow(String[] query) {
            throw new UnsupportedOperationException("No needed for test.");
        }

        @Override
        public void appendSelectOneRowBeforeSelect(StringBuilder result, ArrayList<FieldInfo> orderBys, CustomSqlQuery customQuery) {
            throw new UnsupportedOperationException("No needed for test.");
        }

        @Override
        public boolean appendSelectOneRowAfterWhere(StringBuilder result, boolean requireAnd, ArrayList<FieldInfo> orderBys, CustomSqlQuery customQuery) {
            throw new UnsupportedOperationException("No needed for test.");
        }

        @Override
        public void appendSelectOneRowAfterOrderBy(StringBuilder result, ArrayList<FieldInfo> orderBys, CustomSqlQuery customQuery) {
            throw new UnsupportedOperationException("No needed for test.");
        }

        @Override
        public boolean handleVersionFieldOnInsert() {
            throw new UnsupportedOperationException("No needed for test.");
        }

        @Override
        public boolean handleVersionFieldOnUpdate() {
            throw new UnsupportedOperationException("No needed for test.");
        }

        @Override
        public boolean handleVersionFieldOnDelete() {
            throw new UnsupportedOperationException("No needed for test.");
        }

        @Override
        public void appendInitialVersionValue(StringBuilder result, EntityInfo entity, FieldInfo field) {
            throw new UnsupportedOperationException("No needed for test.");
        }

        @Override
        public void appendNextVersionValue(StringBuilder result, EntityInfo entity, FieldInfo field) {
            throw new UnsupportedOperationException("No needed for test.");
        }

        @Override
        public String[] getIdSequenceNextValue(EntityInfo entity, FieldInfo field) {
            throw new UnsupportedOperationException("No needed for test.");
        }

        @Override
        public String[] getIdSequenceCurrentValue(EntityInfo entity, FieldInfo field) {
            throw new UnsupportedOperationException("No needed for test.");
        }

        @Override
        public ProcessingEnviromentImpl getProcessingEnv() {
            return (ProcessingEnviromentImpl) super.getProcessingEnv();
        }

        public MyBatisSqlQueryGeneratorImpl() {
            QueryGeneratorConfiguration config = new QueryGeneratorConfiguration();
            config.setProcessingEnv(new ProcessingEnviromentImpl());
            setConfiguration(config);
        }
        
    }
}