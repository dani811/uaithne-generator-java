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
import org.junit.Test;
import static org.junit.Assert.*;
import org.uaithne.annotations.Comparators;
import org.uaithne.annotations.myBatis.MyBatisTypeHandler;
import org.uaithne.annotations.sql.CustomSqlQuery;
import org.uaithne.generator.commons.DataTypeInfo;
import org.uaithne.generator.commons.EntityInfo;
import org.uaithne.generator.commons.FieldInfo;
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

    @Test
    public void testGetParameterValue() {
        FieldInfo field = getFieldWithTypeHandler();
        MyBatisSqlQueryGenerator instance = new MyBatisSqlQueryGeneratorImpl();
        String expResult = "#{myField,jdbcType=INTEGER,typeHandler=java.lang.Integer}";
        String result = instance.getParameterValue(field);
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
    public void testAppendStartInsertColumnIfNotNull() {
        StringBuilder result = new StringBuilder();
        FieldInfo field = getFieldWithTypeHandler();
        MyBatisSqlQueryGenerator instance = new MyBatisSqlQueryGeneratorImpl();
        instance.appendStartInsertColumnIfNotNull(result, field);
        assertEquals(result.toString(), "{[if test='myField != null']}myField");
    }

    @Test
    public void testAppendEndInsertColumnIfNotNullWithComma() {
        StringBuilder result = new StringBuilder();
        boolean requireComma = true;
        MyBatisSqlQueryGenerator instance = new MyBatisSqlQueryGeneratorImpl();
        instance.appendEndInsertColumnIfNotNull(result, requireComma);
        assertEquals(result.toString(), ",{[/if]}");
    }

    @Test
    public void testAppendEndInsertColumnIfNotNullWithoutComma() {
        StringBuilder result = new StringBuilder();
        boolean requireComma = false;
        MyBatisSqlQueryGenerator instance = new MyBatisSqlQueryGeneratorImpl();
        instance.appendEndInsertColumnIfNotNull(result, requireComma);
        assertEquals(result.toString(), "{[/if]}");
    }

    @Test
    public void testAppendStartInsertValueIfNotNull() {
        StringBuilder result = new StringBuilder();
        FieldInfo field = getFieldWithTypeHandler();
        MyBatisSqlQueryGenerator instance = new MyBatisSqlQueryGeneratorImpl();
        instance.appendStartInsertValueIfNotNull(result, null, null, field);
        assertEquals(result.toString(), "{[if test='myField != null']}#{myField,jdbcType=INTEGER,typeHandler=java.lang.Integer}");
    }

    @Test
    public void testAppendEndInsertValueIfNotNullWithComma() {
        StringBuilder result = new StringBuilder();
        boolean requireComma = true;
        MyBatisSqlQueryGenerator instance = new MyBatisSqlQueryGeneratorImpl();
        instance.appendEndInsertValueIfNotNull(result, requireComma);
        assertEquals(result.toString(), ",{[/if]}");
    }

    @Test
    public void testAppendEndInsertValueIfNotNullWithoutComma() {
        StringBuilder result = new StringBuilder();
        boolean requireComma = false;
        MyBatisSqlQueryGenerator instance = new MyBatisSqlQueryGeneratorImpl();
        instance.appendEndInsertValueIfNotNull(result, requireComma);
        assertEquals(result.toString(), "{[/if]}");
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
                + "{[if test='[[name]] != null']} [[column]] = [[value]] {[/if]} {[if test='[[name]] == null']} [[column]] is null {[/if]}\n"
                + "{[if test='[[name]] != null']} [[column]] = [[value]] {[/if]} {[if test='[[name]] == null']} [[column]] is not null {[/if]}\n"
                + "{[if test='[[name]] != null']} [[column]] <> [[value]] {[/if]} {[if test='[[name]] == null']} [[column]] is null {[/if]}\n"
                + "{[if test='[[name]] != null']} [[column]] <> [[value]] {[/if]} {[if test='[[name]] == null']} [[column]] is not null {[/if]}\n"
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
        public String selectPageBeforeSelect() {
            throw new UnsupportedOperationException("No needed for test.");
        }

        @Override
        public boolean appendSelectPageAfterWhere(StringBuilder result, boolean requireAnd) {
            throw new UnsupportedOperationException("No needed for test.");
        }

        @Override
        public String selectPageAfterOrderBy() {
            throw new UnsupportedOperationException("No needed for test.");
        }

        @Override
        public String[] envolveInSelectOneRow(String[] query) {
            throw new UnsupportedOperationException("No needed for test.");
        }

        @Override
        public String selectOneRowBeforeSelect() {
            throw new UnsupportedOperationException("No needed for test.");
        }

        @Override
        public String selectOneRowAfterWhere() {
            throw new UnsupportedOperationException("No needed for test.");
        }

        @Override
        public String selectOneRowAfterOrderBy() {
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
    }
}