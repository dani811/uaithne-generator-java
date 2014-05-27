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
package org.uaithne.generator.processors.database.sql;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.tools.Diagnostic;
import org.junit.Test;
import static org.junit.Assert.*;
import org.uaithne.annotations.Comparator;
import static org.uaithne.annotations.Comparator.CONTAINS;
import static org.uaithne.annotations.Comparator.CONTAINS_INSENSITIVE;
import static org.uaithne.annotations.Comparator.END_WITH;
import static org.uaithne.annotations.Comparator.END_WITH_INSENSITIVE;
import static org.uaithne.annotations.Comparator.EQUAL;
import static org.uaithne.annotations.Comparator.EQUAL_NOT_NULLABLE;
import static org.uaithne.annotations.Comparator.EQUAL_NULLABLE;
import static org.uaithne.annotations.Comparator.IN;
import static org.uaithne.annotations.Comparator.LARGER;
import static org.uaithne.annotations.Comparator.LARGER_AS;
import static org.uaithne.annotations.Comparator.LIKE;
import static org.uaithne.annotations.Comparator.LIKE_INSENSITIVE;
import static org.uaithne.annotations.Comparator.NOT_CONTAINS;
import static org.uaithne.annotations.Comparator.NOT_CONTAINS_INSENSITIVE;
import static org.uaithne.annotations.Comparator.NOT_END_WITH;
import static org.uaithne.annotations.Comparator.NOT_END_WITH_INSENSITIVE;
import static org.uaithne.annotations.Comparator.NOT_EQUAL;
import static org.uaithne.annotations.Comparator.NOT_EQUAL_NOT_NULLABLE;
import static org.uaithne.annotations.Comparator.NOT_EQUAL_NULLABLE;
import static org.uaithne.annotations.Comparator.NOT_IN;
import static org.uaithne.annotations.Comparator.NOT_LIKE;
import static org.uaithne.annotations.Comparator.NOT_LIKE_INSENSITIVE;
import static org.uaithne.annotations.Comparator.NOT_START_WITH;
import static org.uaithne.annotations.Comparator.NOT_START_WITH_INSENSITIVE;
import static org.uaithne.annotations.Comparator.SMALLER;
import static org.uaithne.annotations.Comparator.SMALL_AS;
import static org.uaithne.annotations.Comparator.START_WITH;
import static org.uaithne.annotations.Comparator.START_WITH_INSENSITIVE;
import org.uaithne.annotations.EntityQueries;
import org.uaithne.annotations.IdQueries;
import org.uaithne.annotations.MappedName;
import org.uaithne.annotations.PageQueries;
import org.uaithne.annotations.Query;
import org.uaithne.annotations.UseComparator;
import org.uaithne.annotations.UseCustomComparator;
import org.uaithne.annotations.myBatis.MyBatisTypeHandler;
import org.uaithne.annotations.sql.CustomSqlQuery;
import org.uaithne.generator.commons.DataTypeInfo;
import org.uaithne.generator.commons.EntityInfo;
import org.uaithne.generator.commons.EntityKind;
import org.uaithne.generator.commons.FieldInfo;
import org.uaithne.generator.commons.OperationInfo;
import org.uaithne.generator.commons.OperationKind;
import org.uaithne.generator.processors.database.myBatis.MyBatisUtils;
import org.uaithne.generator.utils.MessageContent;
import org.uaithne.generator.utils.ProcessingEnviromentImpl;
import org.uaithne.generator.utils.TestCustomSqlQuery;

public class SqlQueryGeneratorTest {

    @Test
    public void testCompleteQueryWithNullQueries() {
        String[] query = null;
        OperationInfo operation = getOperationForWhere(false);
        boolean count = false;
        boolean selectPage = false;
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        String[] expResult = null;
        String[] result = instance.completeQuery(query, operation, count, selectPage);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testCompleteQuery() {
        String[] query = new String[]{"where", "field = 1"};
        OperationInfo operation = getOperationForWhere(false);
        EntityInfo entity = new EntityInfo(new DataTypeInfo("MyEntity"), EntityKind.ENTITY);
        operation.setEntity(entity);
        boolean count = false;
        boolean selectPage = false;
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        String[] expResult = new String[]{"select", "from", "    MyEntity ", "where", "field = 1"};
        String[] result = instance.completeQuery(query, operation, count, selectPage);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testCompleteQueryWithOperationLimitToOneResult() {
        String[] query = new String[]{"where", "field = 1"};
        OperationInfo operation = getOperationForWhere(false);
        operation.setLimitToOneResult(true);
        EntityInfo entity = new EntityInfo(new DataTypeInfo("MyEntity"), EntityKind.ENTITY);
        operation.setEntity(entity);
        boolean count = false;
        boolean selectPage = false;
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        String[] expResult = new String[]{"select top 1 ", "from", "    MyEntity ", "selectOneRowAfterOrderBy", "where", "field = 1", "envolveInSelectOneRow"};
        String[] result = instance.completeQuery(query, operation, count, selectPage);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testCompleteQueryWithCustomQuery() {
        String[] query = null;
        OperationInfo operation = new OperationInfo(DataTypeInfo.LIST_DATA_TYPE);
        operation.setEntity(getEntityForSelect());

        boolean count = false;
        boolean selectPage = false;
        TestCustomSqlQuery customQuery = getCustomSqlQuery();
        customQuery.beforeQuery = new String[]{"before"};
        customQuery.query = new String[]{"my custom query"};
        customQuery.afterQuery = new String[]{"after"};
        operation.addAnnotation(customQuery);
        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        String[] expResult = {"before ",
            "my custom query ",
            "after "};
        String[] result = instance.completeQuery(query, operation, count, selectPage);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testCompleteQueryWithoutEnvolveWithNullQueriesAndEntity() {
        String query = null;
        OperationInfo operation = new OperationInfo(DataTypeInfo.LIST_DATA_TYPE);
        boolean count = false;
        boolean selectPage = false;
        CustomSqlQuery customQuery = null;
        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        String expResult = null;
        String result = instance.completeQueryWithoutEnvolve(query, operation, count, selectPage, customQuery);
        ArrayList<MessageContent> actualMsg = instance.getProcessingEnv().getMessager().getContent();
        ArrayList<MessageContent> expectedMsg = new ArrayList<MessageContent>();
        expectedMsg.add(new MessageContent(Diagnostic.Kind.ERROR,
                "Unable to automatically generate the query for this operation, enter the query manually using Query annotation",
                operation.getElement()));
        assertArrayEquals(expectedMsg.toArray(), actualMsg.toArray());
        assertEquals(expResult, result);
    }

    @Test
    public void testCompleteQueryWithoutEnvolveWithNullQueriesAndSelectPageOperationWithOrderBy() {
        String query = null;
        OperationInfo operation = new OperationInfo(DataTypeInfo.LIST_DATA_TYPE);
        operation.setEntity(getEntityForSelect());
        //operation.setOperationKind(OperationKind.SELECT_PAGE);
        FieldInfo field1 = new FieldInfo("myField1", new DataTypeInfo("Integer"));
        field1.setManually(true);
        FieldInfo field2 = new FieldInfo("myField2", new DataTypeInfo("Integer"));
        field2.setSetValueMark(true);
        FieldInfo field3 = new FieldInfo("myField3", new DataTypeInfo("Integer"));
        field3.setOrderBy(true);
        operation.addField(field1);
        operation.addField(field2);
        operation.addField(field3);
        boolean count = false;
        boolean selectPage = true;
        CustomSqlQuery customQuery = null;
        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        String expResult = "select\n"
                + "    mappedName as \"mappedField\",\n"
                + "    fieldappendOrderByAfterSelectForSelectPage\n"
                + "from\n"
                + "    MyEntity \n"
                + "order by myField3\n"
                + "selectPageAfterOrderBy";
        String result = instance.completeQueryWithoutEnvolve(query, operation, count, selectPage, customQuery);
        assertEquals(expResult, result);
    }

    @Test
    public void testCompleteQueryWithoutEnvolveWithEmptyQueryAfterTrimAndNullEntity() {
        String query = "   ";
        OperationInfo operation = new OperationInfo(DataTypeInfo.LIST_DATA_TYPE);
        boolean count = false;
        boolean selectPage = false;
        CustomSqlQuery customQuery = null;
        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        String expResult = null;
        String result = instance.completeQueryWithoutEnvolve(query, operation, count, selectPage, customQuery);
        ArrayList<MessageContent> actualMsg = instance.getProcessingEnv().getMessager().getContent();
        ArrayList<MessageContent> expectedMsg = new ArrayList<MessageContent>();
        expectedMsg.add(new MessageContent(Diagnostic.Kind.ERROR,
                "Unable to automatically generate the query for this operation, enter the query manually using Query annotation",
                operation.getElement()));
        assertArrayEquals(expectedMsg.toArray(), actualMsg.toArray());
        assertEquals(expResult, result);
    }

    @Test
    public void testCompleteQueryWithoutEnvolveWithEmptyQueryAfterTrim() {
        String query = "   ";
        OperationInfo operation = new OperationInfo(DataTypeInfo.LIST_DATA_TYPE);
        operation.setEntity(getEntityForSelect());
        FieldInfo field1 = new FieldInfo("myField1", new DataTypeInfo("Integer"));
        field1.setManually(true);
        FieldInfo field2 = new FieldInfo("myField2", new DataTypeInfo("Integer"));
        field2.setSetValueMark(true);
        FieldInfo field3 = new FieldInfo("myField3", new DataTypeInfo("Integer"));
        field3.setOrderBy(true);
        operation.addField(field1);
        operation.addField(field2);
        operation.addField(field3);
        boolean count = false;
        boolean selectPage = false;
        CustomSqlQuery customQuery = null;
        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        String expResult = "select\n"
                + "    mappedName as \"mappedField\",\n"
                + "    field\n"
                + "from\n"
                + "    MyEntity \n"
                + "order by myField3";
        String result = instance.completeQueryWithoutEnvolve(query, operation, count, selectPage, customQuery);
        assertEquals(expResult, result);
    }

    @Test
    public void testCompleteQueryWithoutEnvolveWithFromQueryAndNullEntity() {
        String query = "FROM MI_TABLA";
        OperationInfo operation = new OperationInfo(DataTypeInfo.LIST_DATA_TYPE);
        boolean count = false;
        boolean selectPage = false;
        CustomSqlQuery customQuery = null;
        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        String expResult = "\n"
                + "FROM MI_TABLA";
        String result = instance.completeQueryWithoutEnvolve(query, operation, count, selectPage, customQuery);
        ArrayList<MessageContent> actualMsg = instance.getProcessingEnv().getMessager().getContent();
        ArrayList<MessageContent> expectedMsg = new ArrayList<MessageContent>();
        expectedMsg.add(new MessageContent(Diagnostic.Kind.ERROR,
                "Unable to automatically complete the query for this operation, enter the full query manually using Query annotation",
                operation.getElement()));
        assertArrayEquals(expectedMsg.toArray(), actualMsg.toArray());
        assertEquals(expResult, result);
    }

    @Test
    public void testCompleteQueryWithoutEnvolveWithFromQuery() {
        String query = "FROM MI_TABLA";
        OperationInfo operation = new OperationInfo(DataTypeInfo.LIST_DATA_TYPE);
        operation.setEntity(getEntityForSelect());
        boolean count = false;
        boolean selectPage = false;
        CustomSqlQuery customQuery = null;
        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        String expResult = "select\n"
                + "    mappedName as \"mappedField\",\n"
                + "    field\n"
                + "FROM MI_TABLA";
        String result = instance.completeQueryWithoutEnvolve(query, operation, count, selectPage, customQuery);
        assertEquals(expResult, result);
    }

    @Test
    public void testCompleteQueryWithoutEnvolveWithWhereQueryAndNullEntity() {
        String query = "where field = 1";
        OperationInfo operation = new OperationInfo(DataTypeInfo.LIST_DATA_TYPE);
        boolean count = false;
        boolean selectPage = false;
        CustomSqlQuery customQuery = null;
        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        String expResult = "\n"
                + "where field = 1";
        String result = instance.completeQueryWithoutEnvolve(query, operation, count, selectPage, customQuery);
        ArrayList<MessageContent> actualMsg = instance.getProcessingEnv().getMessager().getContent();
        ArrayList<MessageContent> expectedMsg = new ArrayList<MessageContent>();
        expectedMsg.add(new MessageContent(Diagnostic.Kind.ERROR,
                "Unable to automatically complete the query for this operation, enter the full query manually using Query annotation",
                operation.getElement()));
        assertArrayEquals(expectedMsg.toArray(), actualMsg.toArray());
        assertEquals(expResult, result);
    }

    @Test
    public void testCompleteQueryWithoutEnvolveWithWhereQuery() {
        String query = "where field = 1";
        OperationInfo operation = new OperationInfo(DataTypeInfo.LIST_DATA_TYPE);
        operation.setEntity(getEntityForSelect());
        boolean count = false;
        boolean selectPage = false;
        CustomSqlQuery customQuery = null;
        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        String expResult = "select\n"
                + "    mappedName as \"mappedField\",\n"
                + "    field\n"
                + "from\n"
                + "    MyEntity \n"
                + "where field = 1";
        String result = instance.completeQueryWithoutEnvolve(query, operation, count, selectPage, customQuery);
        assertEquals(expResult, result);
    }

    @Test
    public void testCompleteQueryWithoutEnvolveWithWhereQueryLimitToOneResult() {
        String query = "where field = 1";
        OperationInfo operation = new OperationInfo(DataTypeInfo.LIST_DATA_TYPE);
        operation.setLimitToOneResult(true);
        operation.setEntity(getEntityForSelect());
        boolean count = false;
        boolean selectPage = false;
        CustomSqlQuery customQuery = null;
        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        String expResult = "select top 1 \n"
                + "    mappedName as \"mappedField\",\n"
                + "    field\n"
                + "from\n"
                + "    MyEntity \n"
                + "selectOneRowAfterOrderBy\n"
                + "where field = 1";
        String result = instance.completeQueryWithoutEnvolve(query, operation, count, selectPage, customQuery);
        assertEquals(expResult, result);
    }

    @Test
    public void testCompleteQueryWithoutEnvolveWithGroupQueryAndNullEntity() {
        String query = "group by field";
        OperationInfo operation = new OperationInfo(DataTypeInfo.LIST_DATA_TYPE);
        boolean count = false;
        boolean selectPage = false;
        CustomSqlQuery customQuery = null;
        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        String expResult = "\n"
                + "group by field";
        String result = instance.completeQueryWithoutEnvolve(query, operation, count, selectPage, customQuery);
        ArrayList<MessageContent> actualMsg = instance.getProcessingEnv().getMessager().getContent();
        ArrayList<MessageContent> expectedMsg = new ArrayList<MessageContent>();
        expectedMsg.add(new MessageContent(Diagnostic.Kind.ERROR,
                "Unable to automatically generate the query for this operation, enter the query manually using Query annotation",
                operation.getElement()));
        assertArrayEquals(expectedMsg.toArray(), actualMsg.toArray());
        assertEquals(expResult, result);
    }

    @Test
    public void testCompleteQueryWithoutEnvolveWithGroupQuery() {
        String query = "group by field";
        OperationInfo operation = new OperationInfo(DataTypeInfo.LIST_DATA_TYPE);
        operation.setEntity(getEntityForSelect());
        boolean count = false;
        boolean selectPage = false;
        CustomSqlQuery customQuery = null;
        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        String expResult = "select\n"
                + "    mappedName as \"mappedField\",\n"
                + "    field\n"
                + "from\n"
                + "    MyEntity \n"
                + "group by field";
        String result = instance.completeQueryWithoutEnvolve(query, operation, count, selectPage, customQuery);
        assertEquals(expResult, result);
    }

    @Test
    public void testCompleteQueryWithoutEnvolveWithOrderQueryAndNullEntity() {
        String query = "order by field";
        OperationInfo operation = new OperationInfo(DataTypeInfo.LIST_DATA_TYPE);
        boolean count = false;
        boolean selectPage = false;
        CustomSqlQuery customQuery = null;
        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        String expResult = "\n"
                + "order by field";
        String result = instance.completeQueryWithoutEnvolve(query, operation, count, selectPage, customQuery);
        ArrayList<MessageContent> actualMsg = instance.getProcessingEnv().getMessager().getContent();
        ArrayList<MessageContent> expectedMsg = new ArrayList<MessageContent>();
        expectedMsg.add(new MessageContent(Diagnostic.Kind.ERROR,
                "Unable to automatically generate the query for this operation, enter the query manually using Query annotation",
                operation.getElement()));
        assertArrayEquals(expectedMsg.toArray(), actualMsg.toArray());
        assertEquals(expResult, result);
    }

    @Test
    public void testCompleteQueryWithoutEnvolveWithOrderQuery() {
        String query = "order by field";
        OperationInfo operation = new OperationInfo(DataTypeInfo.LIST_DATA_TYPE);
        operation.setEntity(getEntityForSelect());
        boolean count = false;
        boolean selectPage = false;
        CustomSqlQuery customQuery = null;
        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        String expResult = "select\n"
                + "    mappedName as \"mappedField\",\n"
                + "    field\n"
                + "from\n"
                + "    MyEntity \n"
                + "order by field";
        String result = instance.completeQueryWithoutEnvolve(query, operation, count, selectPage, customQuery);
        assertEquals(expResult, result);
    }

    @Test
    public void testCompleteQueryWithoutEnvolveWithNonClasifiedQuery() {
        String query = "non clasified query";
        OperationInfo operation = new OperationInfo(DataTypeInfo.LIST_DATA_TYPE);
        operation.setEntity(getEntityForSelect());
        boolean count = false;
        boolean selectPage = false;
        CustomSqlQuery customQuery = null;
        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        String expResult = "non clasified query";
        String result = instance.completeQueryWithoutEnvolve(query, operation, count, selectPage, customQuery);
        assertEquals(expResult, result);
    }

    @Test
    public void testCompleteQueryWithoutEnvolveWithCustomQuery() {
        String query = null;
        OperationInfo operation = new OperationInfo(DataTypeInfo.LIST_DATA_TYPE);
        operation.setEntity(getEntityForSelect());
        boolean count = false;
        boolean selectPage = false;
        TestCustomSqlQuery customQuery = getCustomSqlQuery();
        customQuery.beforeQuery = new String[]{"before"};
        customQuery.query = new String[]{"my custom query"};
        customQuery.afterQuery = new String[]{"after"};
        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        String expResult = "my custom query "; // TODO: drop extra space
        String result = instance.completeQueryWithoutEnvolve(query, operation, count, selectPage, customQuery);
        assertEquals(expResult, result);
    }

    @Test
    public void testCompleteQueryWithoutEnvolveWithCustomQueryButNotQueryValue() {
        String query = null;
        OperationInfo operation = new OperationInfo(DataTypeInfo.LIST_DATA_TYPE);
        operation.setEntity(getEntityForSelect());
        boolean count = false;
        boolean selectPage = false;
        TestCustomSqlQuery customQuery = getCustomSqlQuery();
        customQuery.beforeQuery = new String[]{"before"};
        customQuery.afterQuery = new String[]{"after"};
        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        String expResult = "select\n"
                + "    mappedName as \"mappedField\",\n"
                + "    field\n"
                + "from\n"
                + "    MyEntity \n"
                + "order by";
        String result = instance.completeQueryWithoutEnvolve(query, operation, count, selectPage, customQuery);
        assertEquals(expResult, result);
    }

    @Test
    public void testCompleteQueryWithoutEnvolveCompletingSelect() {
        String query = "select...";
        OperationInfo operation = new OperationInfo(DataTypeInfo.LIST_DATA_TYPE);
        operation.setEntity(getEntityForSelect());
        operation.addField(new FieldInfo("field", new DataTypeInfo("int")));
        boolean count = false;
        boolean selectPage = false;
        TestCustomSqlQuery customQuery = getCustomSqlQuery();
        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        String expResult = "select\n"
                + "    mappedName as \"mappedField\",\n"
                + "    field\n"
                + "from\n"
                + "    MyEntity \n"
                + "where\n"
                + "    field = parameterValue\n"
                + "order by";
        String result = instance.completeQueryWithoutEnvolve(query, operation, count, selectPage, customQuery);
        assertEquals(expResult, result);
    }

    @Test
    public void testCompleteQueryWithoutEnvolveCompletingSelectWithoutEntity() {
        String query = "select...";
        OperationInfo operation = new OperationInfo(DataTypeInfo.LIST_DATA_TYPE);
        operation.addField(new FieldInfo("field", new DataTypeInfo("int")));
        boolean count = false;
        boolean selectPage = false;
        TestCustomSqlQuery customQuery = getCustomSqlQuery();
        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        String expResult = "\nselect...";
        String result = instance.completeQueryWithoutEnvolve(query, operation, count, selectPage, customQuery);
        ArrayList<MessageContent> actualMsg = instance.getProcessingEnv().getMessager().getContent();
        ArrayList<MessageContent> expectedMsg = new ArrayList<MessageContent>();
        expectedMsg.add(new MessageContent(Diagnostic.Kind.ERROR,
                "Unable to automatically complete the query for this operation, enter the full query manually using Query annotation",
                operation.getElement()));
        assertArrayEquals(expectedMsg.toArray(), actualMsg.toArray());
        assertEquals(expResult, result);
    }

    @Test
    public void testCompleteQueryWithoutEnvolveCompletingFrom() {
        String query = "select asd from...";
        OperationInfo operation = new OperationInfo(DataTypeInfo.LIST_DATA_TYPE);
        operation.setEntity(getEntityForSelect());
        operation.addField(new FieldInfo("field", new DataTypeInfo("int")));
        boolean count = false;
        boolean selectPage = false;
        TestCustomSqlQuery customQuery = getCustomSqlQuery();
        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        String expResult = "select asd \n"
                + "from\n"
                + "    MyEntity \n"
                + "where\n"
                + "    field = parameterValue\n"
                + "order by";
        String result = instance.completeQueryWithoutEnvolve(query, operation, count, selectPage, customQuery);
        assertEquals(expResult, result);
    }

    @Test
    public void testCompleteQueryWithoutEnvolveCompletingFromWithoutEntity() {
        String query = "select asd from...";
        OperationInfo operation = new OperationInfo(DataTypeInfo.LIST_DATA_TYPE);
        operation.addField(new FieldInfo("field", new DataTypeInfo("int")));
        boolean count = false;
        boolean selectPage = false;
        TestCustomSqlQuery customQuery = getCustomSqlQuery();
        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        String expResult = "\nselect asd from...";
        String result = instance.completeQueryWithoutEnvolve(query, operation, count, selectPage, customQuery);
        ArrayList<MessageContent> actualMsg = instance.getProcessingEnv().getMessager().getContent();
        ArrayList<MessageContent> expectedMsg = new ArrayList<MessageContent>();
        expectedMsg.add(new MessageContent(Diagnostic.Kind.ERROR,
                "Unable to automatically complete the query for this operation, enter the full query manually using Query annotation",
                operation.getElement()));
        assertArrayEquals(expectedMsg.toArray(), actualMsg.toArray());
        assertEquals(expResult, result);
    }

    @Test
    public void testCompleteQueryWithoutEnvolveCompletingWhere() {
        String query = "select asd from zxc where...";
        OperationInfo operation = new OperationInfo(DataTypeInfo.LIST_DATA_TYPE);
        operation.setEntity(getEntityForSelect());
        operation.addField(new FieldInfo("field", new DataTypeInfo("int")));
        boolean count = false;
        boolean selectPage = false;
        TestCustomSqlQuery customQuery = getCustomSqlQuery();
        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        String expResult = "select asd from zxc \n"
                + "where\n"
                + "    field = parameterValue\n"
                + "order by";
        String result = instance.completeQueryWithoutEnvolve(query, operation, count, selectPage, customQuery);
        assertEquals(expResult, result);
    }

    @Test
    public void testCompleteQueryWithoutEnvolveCompletingWhereWithoutEntity() {
        String query = "select asd from zxc where...";
        OperationInfo operation = new OperationInfo(DataTypeInfo.LIST_DATA_TYPE);
        operation.addField(new FieldInfo("field", new DataTypeInfo("int")));
        boolean count = false;
        boolean selectPage = false;
        TestCustomSqlQuery customQuery = getCustomSqlQuery();
        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        String expResult = "\nselect asd from zxc where...";
        String result = instance.completeQueryWithoutEnvolve(query, operation, count, selectPage, customQuery);
        ArrayList<MessageContent> actualMsg = instance.getProcessingEnv().getMessager().getContent();
        ArrayList<MessageContent> expectedMsg = new ArrayList<MessageContent>();
        expectedMsg.add(new MessageContent(Diagnostic.Kind.ERROR,
                "Unable to automatically complete the query for this operation, enter the full query manually using Query annotation",
                operation.getElement()));
        assertArrayEquals(expectedMsg.toArray(), actualMsg.toArray());
        assertEquals(expResult, result);
    }

    @Test
    public void testCompleteQueryWithoutEnvolveCompletingGroup() {
        String query = "select asd from zxc where qwe group...";
        OperationInfo operation = new OperationInfo(DataTypeInfo.LIST_DATA_TYPE);
        operation.setEntity(getEntityForSelect());
        operation.addField(new FieldInfo("field", new DataTypeInfo("int")));
        boolean count = false;
        boolean selectPage = false;
        TestCustomSqlQuery customQuery = getCustomSqlQuery();
        customQuery.groupBy = new String[]{"contentGroupBy"};
        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        String expResult = "select asd from zxc where qwe \n"
                + "group by\n"
                + "    contentGroupBy \n"
                + "order by";
        String result = instance.completeQueryWithoutEnvolve(query, operation, count, selectPage, customQuery);
        assertEquals(expResult, result);
    }

    @Test
    public void testCompleteQueryWithoutEnvolveCompletingGroupWithoutEntity() {
        String query = "select asd from zxc where qwe group...";
        OperationInfo operation = new OperationInfo(DataTypeInfo.LIST_DATA_TYPE);
        operation.addField(new FieldInfo("field", new DataTypeInfo("int")));
        boolean count = false;
        boolean selectPage = false;
        TestCustomSqlQuery customQuery = getCustomSqlQuery();
        customQuery.groupBy = new String[]{"contentGroupBy"};
        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        String expResult = "\nselect asd from zxc where qwe group...";
        String result = instance.completeQueryWithoutEnvolve(query, operation, count, selectPage, customQuery);
        ArrayList<MessageContent> actualMsg = instance.getProcessingEnv().getMessager().getContent();
        ArrayList<MessageContent> expectedMsg = new ArrayList<MessageContent>();
        expectedMsg.add(new MessageContent(Diagnostic.Kind.ERROR,
                "Unable to automatically complete the query for this operation, enter the full query manually using Query annotation",
                operation.getElement()));
        assertArrayEquals(expectedMsg.toArray(), actualMsg.toArray());
        assertEquals(expResult, result);
    }

    @Test
    public void testCompleteQueryWithoutEnvolveCompletingOrder() {
        String query = "select asd from zxc where qwe group by bnm order...";
        OperationInfo operation = new OperationInfo(DataTypeInfo.LIST_DATA_TYPE);
        operation.setEntity(getEntityForSelect());
        operation.addField(new FieldInfo("field", new DataTypeInfo("int")));
        boolean count = false;
        boolean selectPage = false;
        TestCustomSqlQuery customQuery = getCustomSqlQuery();
        customQuery.groupBy = new String[]{"contentGroupBy"};
        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        String expResult = "select asd from zxc where qwe group by bnm \n"
                + "order by";
        String result = instance.completeQueryWithoutEnvolve(query, operation, count, selectPage, customQuery);
        assertEquals(expResult, result);
    }

    @Test
    public void testCompleteQueryWithoutEnvolveCompletingOrderWithoutEntity() {
        String query = "select asd from zxc where qwe group by bnm order...";
        OperationInfo operation = new OperationInfo(DataTypeInfo.LIST_DATA_TYPE);
        operation.addField(new FieldInfo("field", new DataTypeInfo("int")));
        boolean count = false;
        boolean selectPage = false;
        TestCustomSqlQuery customQuery = getCustomSqlQuery();
        customQuery.groupBy = new String[]{"contentGroupBy"};
        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        String expResult = "\nselect asd from zxc where qwe group by bnm order...";
        String result = instance.completeQueryWithoutEnvolve(query, operation, count, selectPage, customQuery);
        ArrayList<MessageContent> actualMsg = instance.getProcessingEnv().getMessager().getContent();
        ArrayList<MessageContent> expectedMsg = new ArrayList<MessageContent>();
        expectedMsg.add(new MessageContent(Diagnostic.Kind.ERROR,
                "Unable to automatically complete the query for this operation, enter the full query manually using Query annotation",
                operation.getElement()));
        assertArrayEquals(expectedMsg.toArray(), actualMsg.toArray());
        assertEquals(expResult, result);
    }

    @Test
    public void testAppendSelectCount() {
        StringBuilder result = new StringBuilder();
        OperationInfo operation = new OperationInfo(DataTypeInfo.LIST_DATA_TYPE);
        EntityInfo entity = null;
        boolean count = true;
        CustomSqlQuery customQuery = null;
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        instance.appendSelect(result, operation, entity, count, customQuery);
        assertEquals("select\n"
                + "    count(*)", result.toString());
    }

    @Test
    public void testAppendSelectDistinct() {
        StringBuilder result = new StringBuilder();
        OperationInfo operation = new OperationInfo(DataTypeInfo.LIST_DATA_TYPE);
        operation.setDistinct(true);
        EntityInfo entity = getEntityForSelect();
        boolean count = false;
        CustomSqlQuery customQuery = null;
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        instance.appendSelect(result, operation, entity, count, customQuery);
        assertEquals("select distinct\n"
                + "    mappedName as \"mappedField\",\n"
                + "    field", result.toString());
    }

    @Test
    public void testAppendSelectLimitToOneResult() {
        StringBuilder result = new StringBuilder();
        OperationInfo operation = new OperationInfo(DataTypeInfo.LIST_DATA_TYPE);
        operation.setLimitToOneResult(true);
        EntityInfo entity = getEntityForSelect();
        boolean count = false;
        CustomSqlQuery customQuery = null;
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        instance.appendSelect(result, operation, entity, count, customQuery);
        assertEquals(result.toString(), "select top 1 \n"
                + "    mappedName as \"mappedField\",\n"
                + "    field");
    }

    @Test
    public void testAppendSelectPage() {
        StringBuilder result = new StringBuilder();
        OperationInfo operation = new OperationInfo(DataTypeInfo.LIST_DATA_TYPE);
        operation.setOperationKind(OperationKind.SELECT_PAGE);
        EntityInfo entity = getEntityForSelect();
        boolean count = false;
        CustomSqlQuery customQuery = null;
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        instance.appendSelect(result, operation, entity, count, customQuery);
        assertEquals("selectselectPageBeforeSelect \n"
                + "    mappedName as \"mappedField\",\n"
                + "    field", result.toString());
    }

    @Test
    public void testAppendSelectUsingCustomQueryWithSelectValue() {
        StringBuilder result = new StringBuilder();
        OperationInfo operation = new OperationInfo(DataTypeInfo.LIST_DATA_TYPE);
        EntityInfo entity = getEntityForSelect();
        boolean count = false;
        TestCustomSqlQuery customQuery = getCustomSqlQuery();
        customQuery.select = new String[]{"field1", "field2"};
        customQuery.beforeSelectExpression = new String[]{"before", "before2"};
        customQuery.afterSelectExpression = new String[]{"after", "after2"};
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        instance.appendSelect(result, operation, entity, count, customQuery);
        assertEquals("select\n"
                + "    before\n"
                + "    before2 \n"
                + "    field1\n"
                + "    field2 \n"
                + "    after\n"
                + "    after2 ", result.toString());
    }

    @Test
    public void testAppendSelectUsingCustomQueryWithoutSelectValue() {
        StringBuilder result = new StringBuilder();
        OperationInfo operation = new OperationInfo(DataTypeInfo.LIST_DATA_TYPE);
        EntityInfo entity = getEntityForSelect();
        boolean count = false;
        TestCustomSqlQuery customQuery = getCustomSqlQuery();
        customQuery.beforeSelectExpression = new String[]{"before", "before2"};
        customQuery.afterSelectExpression = new String[]{"after", "after2"};
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        instance.appendSelect(result, operation, entity, count, customQuery);
        assertEquals("select\n"
                + "    before\n"
                + "    before2 \n"
                + "    mappedName as \"mappedField\",\n"
                + "    field\n"
                + "    after\n"
                + "    after2 ", result.toString());
    }

    @Test
    public void testAppendSelectFields() {
        StringBuilder result = new StringBuilder();
        OperationInfo operation = null;
        EntityInfo entity = getEntityForSelect();
        boolean count = false;
        CustomSqlQuery customQuery = null;
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        instance.appendSelectFields(result, operation, entity, count, customQuery);
        assertEquals("\n    mappedName as \"mappedField\",\n"
                + "    field", result.toString());
    }

    private EntityInfo getEntityForSelect() {
        EntityInfo entity = new EntityInfo(new DataTypeInfo("MyEntity"), EntityKind.ENTITY);

        FieldInfo field1 = new FieldInfo("mappedField", DataTypeInfo.LIST_DATA_TYPE);
        field1.setMappedName("mappedName");
        FieldInfo field2 = new FieldInfo("manuallyField", DataTypeInfo.LIST_DATA_TYPE);
        field2.setManually(true);
        FieldInfo field3 = new FieldInfo("field", DataTypeInfo.LIST_DATA_TYPE);
        entity.addField(field1);
        entity.addField(field2);
        entity.addField(field3);
        return entity;
    }

    @Test
    public void testAppendFrom() {
        StringBuilder result = new StringBuilder();
        EntityInfo entity = new EntityInfo(new DataTypeInfo("MyEntity"), EntityKind.ENTITY);
        CustomSqlQuery customQuery = null;
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        instance.appendFrom(result, entity, customQuery);
        assertEquals(result.toString(), "\nfrom\n    MyEntity ");
    }

    @Test
    public void testAppendFromWithCustomQueryWithFromValue() {
        StringBuilder result = new StringBuilder();
        EntityInfo entity = new EntityInfo(new DataTypeInfo("MyEntity"), EntityKind.ENTITY);
        TestCustomSqlQuery customQuery = getCustomSqlQuery();
        customQuery.beforeFromExpression = new String[]{"before"};
        customQuery.from = new String[]{"MyTable"};
        customQuery.afterFromExpression = new String[]{"after"};
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        instance.appendFrom(result, entity, customQuery);
        assertEquals(result.toString(), "\n"
                + "from\n"
                + "    before \n"
                + "    MyTable \n"
                + "    after ");
    }

    @Test
    public void testAppendFromWithCustomQueryWithoutFromValue() {
        StringBuilder result = new StringBuilder();
        EntityInfo entity = new EntityInfo(new DataTypeInfo("MyEntity"), EntityKind.ENTITY);
        TestCustomSqlQuery customQuery = getCustomSqlQuery();
        customQuery.beforeFromExpression = new String[]{"before"};
        customQuery.from = new String[]{""};
        customQuery.afterFromExpression = new String[]{"after"};
        customQuery.tableAlias = "tableAlias";
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        instance.appendFrom(result, entity, customQuery);
        assertEquals(result.toString(), "\n"
                + "from\n"
                + "    before \n"
                + "    MyEntity tableAlias \n"
                + "    after ");
    }

    @Test
    public void testAppendWhereWithCustomQuery() {
        StringBuilder query = new StringBuilder();
        OperationInfo operation = new OperationInfo(DataTypeInfo.LIST_DATA_TYPE);
        TestCustomSqlQuery customQuery = getCustomSqlQuery();
        customQuery.beforeWhereExpression = new String[]{"before"};
        customQuery.where = new String[]{"field = 1"};
        customQuery.afterWhereExpression = new String[]{"after"};
        boolean count = false;
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        instance.appendWhere(query, operation, customQuery, count);
        assertEquals(query.toString(), "\n"
                + "<where>\n"
                + "    before \n"
                + "    field = 1 \n"
                + "    after \n"
                + "</where>");
    }

    @Test
    public void testAppendWhere() {
        StringBuilder query = new StringBuilder();
        OperationInfo operation = getOperationForWhere(false);
        CustomSqlQuery customQuery = null;
        boolean count = false;
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        instance.appendWhere(query, operation, customQuery, count);
        assertEquals(query.toString(), "\n"
                + "<where>\n"
                + "    normalField = parameterValue\n"
                + "    <if test='optionalField != null'>and optionalField in <foreach collection='optionalField' open='(' separator=',' close=')' item='_item_optionalField'> #{_item_optionalField,jdbcType=NUMERIC} </foreach></if>\n"
                + "    and listField in <foreach collection='listField' open='(' separator=',' close=')' item='_item_listField'> #{_item_listField,jdbcType=NUMERIC} </foreach>\n"
                + "</where>");
    }

    @Test
    public void testAppendWhereReverseRequireAnds() {
        StringBuilder query = new StringBuilder();
        OperationInfo operation = getOperationForWhere(true);
//        operation.setOperationKind(OperationKind.SELECT_PAGE);
        CustomSqlQuery customQuery = null;
        boolean count = false;
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        instance.appendWhere(query, operation, customQuery, count);
        assertEquals(query.toString(), "\n"
                + "<where>\n"
                + "    <if test='optionalField != null'>optionalField in <foreach collection='optionalField' open='(' separator=',' close=')' item='_item_optionalField'> #{_item_optionalField,jdbcType=NUMERIC} </foreach></if>\n"
                + "    and normalField = parameterValue\n"
                + "    and listField in <foreach collection='listField' open='(' separator=',' close=')' item='_item_listField'> #{_item_listField,jdbcType=NUMERIC} </foreach>\n"
                + "</where>");
    }

    @Test
    public void testAppendWhereLimitToOneResultOperation() {
        StringBuilder query = new StringBuilder();
        OperationInfo operation = getOperationForWhere(false);
        operation.setLimitToOneResult(true);
        CustomSqlQuery customQuery = null;
        boolean count = false;
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        instance.appendWhere(query, operation, customQuery, count);
        assertEquals(query.toString(), "\n"
                + "<where>\n"
                + "    normalField = parameterValue\n"
                + "    <if test='optionalField != null'>and optionalField in <foreach collection='optionalField' open='(' separator=',' close=')' item='_item_optionalField'> #{_item_optionalField,jdbcType=NUMERIC} </foreach></if>\n"
                + "    and listField in <foreach collection='listField' open='(' separator=',' close=')' item='_item_listField'> #{_item_listField,jdbcType=NUMERIC} </foreach>\n"
                + "    and rownum = 1\n"
                + "</where>");
    }

    @Test
    public void testAppendWhereLimitToOneResultOperationWithEmptyOperation() {
        StringBuilder query = new StringBuilder();
        OperationInfo operation = new OperationInfo(new DataTypeInfo("MyOperation"));
        operation.setLimitToOneResult(true);
        CustomSqlQuery customQuery = null;
        boolean count = false;
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        instance.appendWhere(query, operation, customQuery, count);
        assertEquals(query.toString(), "\n"
                + "where\n"
                + "    rownum = 1");
    }

    @Test
    public void testAppendWhereLimitToOneResultOperationWithLogicalDeletionField() {
        StringBuilder query = new StringBuilder();
        OperationInfo operation = new OperationInfo(new DataTypeInfo("myOperation"));

        EntityInfo entity = new EntityInfo(new DataTypeInfo("myEntity"), EntityKind.ENTITY);
        FieldInfo field = new FieldInfo("myField", DataTypeInfo.LIST_DATA_TYPE);
        field.setDeletionMark(true);
        FieldInfo field1 = new FieldInfo("myField1", DataTypeInfo.LIST_DATA_TYPE);
        field1.setDeletionMark(true);
        FieldInfo field2 = new FieldInfo("myManuallyField", DataTypeInfo.LIST_DATA_TYPE);
        field2.setManually(true);
        entity.addField(field);
        entity.addField(field1);
        entity.addField(field2);
        operation.setEntity(entity);

        CustomSqlQuery customQuery = null;
        boolean count = false;
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        instance.appendWhere(query, operation, customQuery, count);
        assertEquals(query.toString(), "\n"
                + "where\n"
                + "    myField = false\n"
                + "    and myField1 = false");
    }

    @Test
    public void testAppendWhereWithSelectPageOperationAndFalseHasConditions() {
        StringBuilder query = new StringBuilder();
        OperationInfo operation = new OperationInfo(new DataTypeInfo("MyOperation"));
        operation.setOperationKind(OperationKind.SELECT_PAGE);
        CustomSqlQuery customQuery = null;
        boolean count = false;
        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        instance.appendSelectPageAfterWhere = false;
        instance.appendWhere(query, operation, customQuery, count);
        assertEquals(query.toString(), "");
    }

    @Test
    public void testAppendWhereWithSelectPageOperationAndTrueHasConditions() {
        StringBuilder query = new StringBuilder();
        OperationInfo operation = new OperationInfo(new DataTypeInfo("MyOperation"));
        operation.setOperationKind(OperationKind.SELECT_PAGE);
        CustomSqlQuery customQuery = null;
        boolean count = false;
        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        instance.appendSelectPageAfterWhere = true;
        instance.appendWhere(query, operation, customQuery, count);
        assertEquals(query.toString(), "\nwhere\n");
    }

    @Test
    public void testAppendWhereWithCustomQueryAndWithoutConditions() {
        StringBuilder query = new StringBuilder();
        OperationInfo operation = new OperationInfo(new DataTypeInfo("MyOperation"));
        operation.setOperationKind(OperationKind.SELECT_PAGE);
        TestCustomSqlQuery customQuery = getCustomSqlQuery();
        customQuery.beforeWhereExpression = new String[]{"before"};
        customQuery.afterWhereExpression = new String[]{"after"};
        boolean count = false;
        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        instance.appendWhere(query, operation, customQuery, count);
        assertEquals(query.toString(), "\n"
                + "<where>\n"
                + "    before \n"
                + "    after \n"
                + "</where>");
    }

    @Test
    public void testAppendWhereWithCustomQueryAndWithConditions() {
        StringBuilder query = new StringBuilder();
        OperationInfo operation = getOperationForWhere(false);
        operation.setOperationKind(OperationKind.SELECT_PAGE);
        TestCustomSqlQuery customQuery = getCustomSqlQuery();
        customQuery.beforeWhereExpression = new String[]{"before"};
        customQuery.afterWhereExpression = new String[]{"after"};
        boolean count = false;
        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        instance.appendWhere(query, operation, customQuery, count);
        assertEquals(query.toString(), "\n"
                + "<where>\n"
                + "    before \n"
                + "    normalField = parameterValue\n"
                + "    <if test='optionalField != null'>and optionalField in <foreach collection='optionalField' open='(' separator=',' close=')' item='_item_optionalField'> #{_item_optionalField,jdbcType=NUMERIC} </foreach></if>\n"
                + "    and listField in <foreach collection='listField' open='(' separator=',' close=')' item='_item_listField'> #{_item_listField,jdbcType=NUMERIC} </foreach>\n"
                + "    after \n"
                + "</where>");
    }

    private OperationInfo getOperationForWhere(boolean reverseOptional) {
        OperationInfo operation = new OperationInfo(new DataTypeInfo("MyOperation"));

        FieldInfo field1 = new FieldInfo("setValueField", new DataTypeInfo("Integer"));
        field1.setSetValueMark(true);
        FieldInfo field2 = new FieldInfo("manuallyField", new DataTypeInfo("Integer"));
        field2.setManually(true);
        FieldInfo field3 = new FieldInfo("orderByField", new DataTypeInfo("Integer"));
        field3.setOrderBy(true);
        FieldInfo field4 = new FieldInfo("normalField", new DataTypeInfo("Integer"));
        FieldInfo field5 = new FieldInfo("optionalField", DataTypeInfo.LIST_DATA_TYPE);
        field5.setOptional(true);
        FieldInfo field6 = new FieldInfo("listField", DataTypeInfo.LIST_DATA_TYPE);
        operation.addField(field1);
        operation.addField(field2);
        operation.addField(field3);
        if (reverseOptional) {
            operation.addField(field5);
            operation.addField(field4);
        } else {
            operation.addField(field4);
            operation.addField(field5);
        }
        operation.addField(field6);
        return operation;
    }

    @Test
    public void testAppendCondition() {
        StringBuilder result = new StringBuilder();
        FieldInfo field = new FieldInfo("myField", new DataTypeInfo("Integer"));
        field.addAnnotation(new UseComparator() {
            @Override
            public Comparator value() {
                return Comparator.SMALLER;
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return UseComparator.class;
            }
        });
        CustomSqlQuery customQuery = null;
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        instance.appendCondition(result, field, customQuery);
        assertEquals("myField &lt; parameterValue", result.toString());
    }

    @Test
    public void testAppendGroupBy() {
        StringBuilder result = new StringBuilder();
        OperationInfo operation = null;
        EntityInfo entity = null;
        TestCustomSqlQuery customQuery = getCustomSqlQuery();
        customQuery.beforeGroupByExpression = new String[]{"before"};
        customQuery.groupBy = new String[]{"customContent"};
        customQuery.afterGroupByExpression = new String[]{"after"};
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        instance.appendGroupBy(result, operation, entity, customQuery);
        assertEquals("\n"
                + "group by\n"
                + "    before \n"
                + "    customContent \n"
                + "    after ", result.toString());

    }

    @Test
    public void testAppendGroupByWithoutCustomGroupByProperties() {
        StringBuilder result = new StringBuilder();
        OperationInfo operation = getOperationForWhere(true);
        EntityInfo entity = null;
        TestCustomSqlQuery customQuery = getCustomSqlQuery();
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        instance.appendGroupBy(result, operation, entity, customQuery);
        assertEquals("", result.toString());
    }
//
//    /**
//     * Test of appendOrderBy method, of class SqlQueryGenerator.
//     */
//    @Test
//    public void testAppendOrderBy() {
//        System.out.println("appendOrderBy");
//        StringBuilder result_2 = null;
//        ArrayList<FieldInfo> orderBys = null;
//        CustomSqlQuery customQuery = null;
//        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
//        instance.appendOrderBy(result_2, orderBys, customQuery);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of appendOrderByForSelectPage method, of class SqlQueryGenerator.
//     */
//    @Test
//    public void testAppendOrderByForSelectPage() {
//        System.out.println("appendOrderByForSelectPage");
//        StringBuilder result_2 = null;
//        ArrayList<FieldInfo> orderBys = null;
//        CustomSqlQuery customQuery = null;
//        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
//        instance.appendOrderByForSelectPage(result_2, orderBys, customQuery);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of appendOrderByAfterSelectForSelectPage method, of class SqlQueryGenerator.
//     */
//    @Test
//    public void testAppendOrderByAfterSelectForSelectPage() {
//        System.out.println("appendOrderByAfterSelectForSelectPage");
//        StringBuilder result_2 = null;
//        ArrayList<FieldInfo> orderBys = null;
//        CustomSqlQuery customQuery = null;
//        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
//        instance.appendOrderByAfterSelectForSelectPage(result_2, orderBys, customQuery);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

    @Test
    public void testGetColumnNameForSelect() {
        FieldInfo field = new FieldInfo("myField", new DataTypeInfo("int"));
        CustomSqlQuery customQuery = null;
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        String expResult = "myField";
        String result = instance.getColumnNameForSelect(field, customQuery);
        assertEquals(expResult, result);
    }

    @Test
    public void testGetColumnNameForSelectWithMappedName() {
        FieldInfo field = new FieldInfo("myField", new DataTypeInfo("int"));
        field.setMappedName("fieldTableName");
        CustomSqlQuery customQuery = null;
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        String expResult = "fieldTableName as \"myField\"";
        String result = instance.getColumnNameForSelect(field, customQuery);
        assertEquals(expResult, result);
    }

    @Test
    public void testGetColumnNameForSelectWithCustomSqlQuery() {
        FieldInfo field = new FieldInfo("myField", new DataTypeInfo("int"));
        field.setMappedName("fieldTableName");
        TestCustomSqlQuery customQuery = getCustomSqlQuery();
        customQuery.tableAlias = "tableAlias";
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        String expResult = "tableAlias.fieldTableName as \"myField\"";
        String result = instance.getColumnNameForSelect(field, customQuery);
        assertEquals(expResult, result);
    }

    @Test
    public void testGetColumnNameForSelectWithCustomSqlQueryAndNullTableAlias() {
        FieldInfo field = new FieldInfo("myField", new DataTypeInfo("int"));
        field.setMappedName("fieldTableName");
        TestCustomSqlQuery customQuery = getCustomSqlQuery();
        customQuery.tableAlias = null;
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        String expResult = "fieldTableName as \"myField\"";
        String result = instance.getColumnNameForSelect(field, customQuery);
        assertEquals(expResult, result);
    }

    @Test
    public void testGetColumnNameForSelectWithCustomSqlQueryAndEmptyTableAlias() {
        FieldInfo field = new FieldInfo("myField", new DataTypeInfo("int"));
        field.setMappedName("fieldTableName");
        TestCustomSqlQuery customQuery = getCustomSqlQuery();
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        String expResult = "fieldTableName as \"myField\"";
        String result = instance.getColumnNameForSelect(field, customQuery);
        assertEquals(expResult, result);
    }

    private TestCustomSqlQuery getCustomSqlQuery() {
        return new TestCustomSqlQuery();
    }

    @Test
    public void testGetTableNameForSelectFromMappedEntity() {
        EntityInfo entity = new EntityInfo(DataTypeInfo.LIST_DATA_TYPE, EntityKind.ENTITY);
        entity.addAnnotation(new MappedName() {
            @Override
            public String[] value() {
                return new String[]{"mappedEntity"};
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return MappedName.class;
            }
        });

        CustomSqlQuery customQuery = null;
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        String[] expResult = new String[]{"mappedEntity"};
        String[] result = instance.getTableNameForSelect(entity, customQuery);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testGetTableNameForSelect() {
        EntityInfo entity = new EntityInfo(new DataTypeInfo("MyEntity"), EntityKind.ENTITY);
        CustomSqlQuery customQuery = null;
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        String[] expResult = new String[]{"MyEntity"};
        String[] result = instance.getTableNameForSelect(entity, customQuery);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testGetTableNameForSelectWithCustomQuery() {
        EntityInfo entity = new EntityInfo(new DataTypeInfo("MyEntity"), EntityKind.ENTITY);
        TestCustomSqlQuery customQuery = getCustomSqlQuery();
        customQuery.tableAlias = "tableAlias";
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        String[] expResult = new String[]{"MyEntity tableAlias"};
        String[] result = instance.getTableNameForSelect(entity, customQuery);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testGetColumnNameOfMappedField() {
        FieldInfo field = new FieldInfo("myField", new DataTypeInfo("Integer"));
        field.setMappedName("mappedField");
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        String expResult = "mappedField";
        String result = instance.getColumnName(field);
        assertEquals(expResult, result);
    }

    @Test
    public void testGetColumnName() {
        FieldInfo field = new FieldInfo("myField", new DataTypeInfo("Integer"));
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        String expResult = "myField";
        String result = instance.getColumnName(field);
        assertEquals(expResult, result);
    }

    @Test
    public void testGetColumnNameForWhere() {
        FieldInfo field = new FieldInfo("myField", new DataTypeInfo("Integer"));
        field.setMappedName("mappedField");
        TestCustomSqlQuery customQuery = getCustomSqlQuery();
        customQuery.tableAlias = "tableAlias";
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        String expResult = "mappedField";
        String result = instance.getColumnNameForWhere(field, customQuery);
        assertEquals(expResult, result);
    }

    @Test
    public void testGetColumnNameForWhereWithFieldWithoutOwnMappedName() {
        FieldInfo field = new FieldInfo("myField", new DataTypeInfo("Integer"));
        TestCustomSqlQuery customQuery = getCustomSqlQuery();
        customQuery.tableAlias = "tableAlias";
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        String expResult = "tableAlias.myField";
        String result = instance.getColumnNameForWhere(field, customQuery);
        assertEquals(expResult, result);
    }
//
//    /**
//     * Test of appendStartWhere method, of class SqlQueryGenerator.
//     */
//    @Test
//    public void testAppendStartWhere() {
//        System.out.println("appendStartWhere");
//        StringBuilder result_2 = null;
//        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
//        instance.appendStartWhere(result_2);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of appendEndWhere method, of class SqlQueryGenerator.
//     */
//    @Test
//    public void testAppendEndWhere() {
//        System.out.println("appendEndWhere");
//        StringBuilder result_2 = null;
//        String separator = "";
//        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
//        instance.appendEndWhere(result_2, separator);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of appendStartSet method, of class SqlQueryGenerator.
//     */
//    @Test
//    public void testAppendStartSet() {
//        System.out.println("appendStartSet");
//        StringBuilder result_2 = null;
//        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
//        instance.appendStartSet(result_2);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of appendEndSet method, of class SqlQueryGenerator.
//     */
//    @Test
//    public void testAppendEndSet() {
//        System.out.println("appendEndSet");
//        StringBuilder result_2 = null;
//        String separator = "";
//        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
//        instance.appendEndSet(result_2, separator);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of appendStartSetValueIfNotNull method, of class SqlQueryGenerator.
//     */
//    @Test
//    public void testAppendStartSetValueIfNotNull() {
//        System.out.println("appendStartSetValueIfNotNull");
//        StringBuilder result_2 = null;
//        FieldInfo field = null;
//        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
//        instance.appendStartSetValueIfNotNull(result_2, field);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of appendEndSetValueIfNotNull method, of class SqlQueryGenerator.
//     */
//    @Test
//    public void testAppendEndSetValueIfNotNull() {
//        System.out.println("appendEndSetValueIfNotNull");
//        StringBuilder result_2 = null;
//        boolean requireComma = false;
//        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
//        instance.appendEndSetValueIfNotNull(result_2, requireComma);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of appendConditionStartIfNull method, of class SqlQueryGenerator.
//     */
//    @Test
//    public void testAppendConditionStartIfNull() {
//        System.out.println("appendConditionStartIfNull");
//        StringBuilder result_2 = null;
//        FieldInfo field = null;
//        String separator = "";
//        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
//        instance.appendConditionStartIfNull(result_2, field, separator);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of appendConditionStartIfNotNull method, of class SqlQueryGenerator.
//     */
//    @Test
//    public void testAppendConditionStartIfNotNull() {
//        System.out.println("appendConditionStartIfNotNull");
//        StringBuilder result_2 = null;
//        FieldInfo field = null;
//        String separator = "";
//        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
//        instance.appendConditionStartIfNotNull(result_2, field, separator);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of appendConditionEndIf method, of class SqlQueryGenerator.
//     */
//    @Test
//    public void testAppendConditionEndIf() {
//        System.out.println("appendConditionEndIf");
//        StringBuilder result_2 = null;
//        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
//        instance.appendConditionEndIf(result_2);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of translateComparator method, of class SqlQueryGenerator.
//     */
//    @Test
//    public void testTranslateComparator() {
//        System.out.println("translateComparator");
//        Comparator comparator = null;
//        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
//        String expResult = "";
//        String result = instance.translateComparator(comparator);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getConditionElementValue method, of class SqlQueryGenerator.
//     */
//    @Test
//    public void testGetConditionElementValue() {
//        System.out.println("getConditionElementValue");
//        String rule = "";
//        FieldInfo field = null;
//        CustomSqlQuery customQuery = null;
//        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
//        String expResult = "";
//        String result = instance.getConditionElementValue(rule, field, customQuery);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of currentSqlDate method, of class SqlQueryGenerator.
//     */
//    @Test
//    public void testCurrentSqlDate() {
//        System.out.println("currentSqlDate");
//        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
//        String expResult = "";
//        String result = instance.currentSqlDate();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of falseValue method, of class SqlQueryGenerator.
//     */
//    @Test
//    public void testFalseValue() {
//        System.out.println("falseValue");
//        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
//        String expResult = "";
//        String result = instance.falseValue();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of trueValue method, of class SqlQueryGenerator.
//     */
//    @Test
//    public void testTrueValue() {
//        System.out.println("trueValue");
//        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
//        String expResult = "";
//        String result = instance.trueValue();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of envolveInSelectPage method, of class SqlQueryGenerator.
//     */
//    @Test
//    public void testEnvolveInSelectPage() {
//        System.out.println("envolveInSelectPage");
//        String[] query = null;
//        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
//        String[] expResult = null;
//        String[] result = instance.envolveInSelectPage(query);
//        assertArrayEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of selectPageBeforeSelect method, of class SqlQueryGenerator.
//     */
//    @Test
//    public void testSelectPageBeforeSelect() {
//        System.out.println("selectPageBeforeSelect");
//        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
//        String expResult = "";
//        String result = instance.selectPageBeforeSelect();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of appendSelectPageAfterWhere method, of class SqlQueryGenerator.
//     */
//    @Test
//    public void testAppendSelectPageAfterWhere() {
//        System.out.println("appendSelectPageAfterWhere");
//        StringBuilder result_2 = null;
//        boolean requireAnd = false;
//        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
//        boolean expResult = false;
//        boolean result = instance.appendSelectPageAfterWhere(result_2, requireAnd);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of selectPageAfterOrderBy method, of class SqlQueryGenerator.
//     */
//    @Test
//    public void testSelectPageAfterOrderBy() {
//        System.out.println("selectPageAfterOrderBy");
//        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
//        String expResult = "";
//        String result = instance.selectPageAfterOrderBy();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of envolveInSelectOneRow method, of class SqlQueryGenerator.
//     */
//    @Test
//    public void testEnvolveInSelectOneRow() {
//        System.out.println("envolveInSelectOneRow");
//        String[] query = null;
//        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
//        String[] expResult = null;
//        String[] result = instance.envolveInSelectOneRow(query);
//        assertArrayEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of selectOneRowBeforeSelect method, of class SqlQueryGenerator.
//     */
//    @Test
//    public void testSelectOneRowBeforeSelect() {
//        System.out.println("selectOneRowBeforeSelect");
//        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
//        String expResult = "";
//        String result = instance.selectOneRowBeforeSelect();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of selectOneRowAfterWhere method, of class SqlQueryGenerator.
//     */
//    @Test
//    public void testSelectOneRowAfterWhere() {
//        System.out.println("selectOneRowAfterWhere");
//        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
//        String expResult = "";
//        String result = instance.selectOneRowAfterWhere();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of selectOneRowAfterOrderBy method, of class SqlQueryGenerator.
//     */
//    @Test
//    public void testSelectOneRowAfterOrderBy() {
//        System.out.println("selectOneRowAfterOrderBy");
//        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
//        String expResult = "";
//        String result = instance.selectOneRowAfterOrderBy();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//

    @Test
    public void testAppendNotDeleted() {
        StringBuilder result = new StringBuilder();
        FieldInfo field = new FieldInfo("myField", new DataTypeInfo("Boolean"));
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        instance.appendNotDeleted(result, field);
        assertEquals("myField = false", result.toString());
    }

    @Test
    public void testAppendNotDeletedWithOptionalField() {
        StringBuilder result = new StringBuilder();
        FieldInfo field = new FieldInfo("myField", new DataTypeInfo("Boolean"));
        field.setOptional(true);
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        instance.appendNotDeleted(result, field);
        assertEquals("myField is not null", result.toString());
    }

    @Test
    public void testAppendNotDeletedValue() {
        StringBuilder result = new StringBuilder();
        FieldInfo field = new FieldInfo("myField", new DataTypeInfo("Integer"));
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        instance.appendNotDeletedValue(result, field);
        assertEquals("false", result.toString());
    }

    @Test
    public void testAppendNotDeletedValueWithOptionalField() {
        StringBuilder result = new StringBuilder();
        FieldInfo field = new FieldInfo("myField", new DataTypeInfo("Integer"));
        field.setOptional(true);
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        instance.appendNotDeletedValue(result, field);
        assertEquals("null", result.toString());
    }

    @Test
    public void testAppendDeletedValue() {
        StringBuilder result = new StringBuilder();
        FieldInfo field = new FieldInfo("myField", new DataTypeInfo("Integer"));
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        instance.appendDeletedValue(result, field);
        assertEquals("true", result.toString());
    }

    @Test
    public void testAppendDeletedValueWithDateField() {
        StringBuilder result = new StringBuilder();
        FieldInfo field = new FieldInfo("myField", new DataTypeInfo("java.util", "Date"));
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        instance.appendDeletedValue(result, field);
        assertEquals("current_timestamp", result.toString());
    }
//
//    /**
//     * Test of handleVersionFieldOnInsert method, of class SqlQueryGenerator.
//     */
//    @Test
//    public void testHandleVersionFieldOnInsert() {
//        System.out.println("handleVersionFieldOnInsert");
//        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
//        boolean expResult = false;
//        boolean result = instance.handleVersionFieldOnInsert();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of handleVersionFieldOnUpdate method, of class SqlQueryGenerator.
//     */
//    @Test
//    public void testHandleVersionFieldOnUpdate() {
//        System.out.println("handleVersionFieldOnUpdate");
//        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
//        boolean expResult = false;
//        boolean result = instance.handleVersionFieldOnUpdate();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of handleVersionFieldOnDelete method, of class SqlQueryGenerator.
//     */
//    @Test
//    public void testHandleVersionFieldOnDelete() {
//        System.out.println("handleVersionFieldOnDelete");
//        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
//        boolean expResult = false;
//        boolean result = instance.handleVersionFieldOnDelete();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of appendInitialVersionValue method, of class SqlQueryGenerator.
//     */
//    @Test
//    public void testAppendInitialVersionValue() {
//        System.out.println("appendInitialVersionValue");
//        StringBuilder result_2 = null;
//        EntityInfo entity = null;
//        FieldInfo field = null;
//        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
//        instance.appendInitialVersionValue(result_2, entity, field);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of appendNextVersionValue method, of class SqlQueryGenerator.
//     */
//    @Test
//    public void testAppendNextVersionValue() {
//        System.out.println("appendNextVersionValue");
//        StringBuilder result_2 = null;
//        EntityInfo entity = null;
//        FieldInfo field = null;
//        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
//        instance.appendNextVersionValue(result_2, entity, field);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

    @Test
    public void testGetCustomInsertQueryWithCustomSqlQueryWithQueryValue() {
        OperationInfo operation = new OperationInfo(DataTypeInfo.LIST_DATA_TYPE);
        TestCustomSqlQuery customSqlQuery = getCustomSqlQuery();
        customSqlQuery.query = new String[]{"custom", "sql", "query"};
        operation.addAnnotation(customSqlQuery);
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        String[] expResult = new String[]{"custom", "sql", "query"};
        String[] result = instance.getCustomInsertQuery(operation);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testGetCustomInsertQueryWithQuery() {
        OperationInfo operation = new OperationInfo(DataTypeInfo.LIST_DATA_TYPE);
        operation.addAnnotation(new Query() {
            @Override
            public String[] value() {
                return new String[]{"query", "value"};
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return Query.class;
            }
        });
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        String[] expResult = new String[]{"query", "value"};
        String[] result = instance.getCustomInsertQuery(operation);
        assertArrayEquals(expResult, result);
    }

    private EntityInfo getEntityForCustomInsertQuery() {
        EntityInfo entity = new EntityInfo(new DataTypeInfo("MyEntity"), EntityKind.ENTITY);

        FieldInfo field0 = new FieldInfo("idField", DataTypeInfo.LIST_DATA_TYPE);
        field0.setIdentifier(true);
        FieldInfo field0BIS = new FieldInfo("idFieldBIS", DataTypeInfo.LIST_DATA_TYPE);
        field0BIS.setIdentifier(true);
        FieldInfo field1 = new FieldInfo("mappedField", DataTypeInfo.LIST_DATA_TYPE);
        field1.setMappedName("mappedName");
        FieldInfo field2 = new FieldInfo("manuallyField", DataTypeInfo.LIST_DATA_TYPE);
        field2.setManually(true);
        FieldInfo field3 = new FieldInfo("field", DataTypeInfo.LIST_DATA_TYPE);
        FieldInfo field4 = new FieldInfo("idFieldInsertDateMark", DataTypeInfo.LIST_DATA_TYPE);
        field4.setInsertDateMark(true);
        field4.setIdentifier(true);
        FieldInfo field5 = new FieldInfo("deletionMarkField", new DataTypeInfo("Boolean"));
        field5.setDeletionMark(true);
        FieldInfo field6 = new FieldInfo("insertDateMarkField", new DataTypeInfo("java.util", "Date"));
        field6.setInsertDateMark(true);
        FieldInfo field7 = new FieldInfo("insertUserMarkField", new DataTypeInfo("java.util", "Date"));
        field7.setInsertUserMark(true);
        FieldInfo field8 = new FieldInfo("updateDateMarkField", new DataTypeInfo("java.util", "Date"));
        field8.setUpdateDateMark(true);
        FieldInfo field9 = new FieldInfo("updateUserMarkField", new DataTypeInfo("java.util", "Date"));
        field9.setUpdateUserMark(true);
        FieldInfo field10 = new FieldInfo("deleteDateMarkField", new DataTypeInfo("java.util", "Date"));
        field10.setDeleteDateMark(true);
        FieldInfo field11 = new FieldInfo("deleteUserMarkField", new DataTypeInfo("java.util", "Date"));
        field11.setDeleteUserMark(true);
        FieldInfo field12 = new FieldInfo("versionMarkField", new DataTypeInfo("String"));
        field12.setVersionMark(true);


        entity.addField(field0);
        entity.addField(field0BIS);
        entity.addField(field1);
        entity.addField(field2);
        entity.addField(field3);
        entity.addField(field4);
        entity.addField(field5);
        entity.addField(field6);
        entity.addField(field7);
        entity.addField(field8);
        entity.addField(field9);
        entity.addField(field10);
        entity.addField(field11);
        entity.addField(field12);

        return entity;
    }

    @Test
    public void testGetCustomInsertQuery() {
        OperationInfo operation = new OperationInfo(DataTypeInfo.LIST_DATA_TYPE);
        operation.setEntity(getEntityForCustomInsertQuery());
        FieldInfo manuallyOperationField = new FieldInfo("manuallyOperationField", DataTypeInfo.LIST_DATA_TYPE);
        manuallyOperationField.setManually(true);
        operation.addField(manuallyOperationField);
        operation.addField(new FieldInfo("operationField", DataTypeInfo.LIST_DATA_TYPE));
        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        instance.handleVersionFieldOnInsert = true;
        String[] expResult = new String[]{"insert into",
            "    MyEntity ",
            "(",
            "    idField,",
            "    idFieldBIS,",
            "    idFieldInsertDateMark,",
            "    operationField,",
            "    deletionMarkField,",
            "    insertDateMarkField,",
            "    versionMarkField",
            ") values (",
            "    parameterValue,",
            "    parameterValue,",
            "    current_timestamp,",
            "    parameterValue,",
            "    false,",
            "    current_timestamp,",
            "    initalVersionValue",
            ")"};
        String[] result = instance.getCustomInsertQuery(operation);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testGetCustomInsertQueryWithoutIncludeIdOnInsertAndNotHandlingVersionFieldOnInsert() {
        OperationInfo operation = new OperationInfo(DataTypeInfo.LIST_DATA_TYPE);
        operation.setEntity(getEntityForCustomInsertQuery());

        FieldInfo autoGeneratedId = new FieldInfo("autoGeneratedId", DataTypeInfo.LIST_DATA_TYPE);
        autoGeneratedId.setIdentifier(true);
        autoGeneratedId.setIdentifierAutogenerated(true);
        operation.getEntity().addField(autoGeneratedId);

        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        instance.setUseAutoIncrementId(true);
        String[] expResult = new String[]{"insert into",
            "    MyEntity ",
            "(",
            "    idField,",
            "    idFieldBIS,",
            "    idFieldInsertDateMark,",
            "    deletionMarkField,",
            "    insertDateMarkField",
            ") values (",
            "    parameterValue,",
            "    parameterValue,",
            "    current_timestamp,",
            "    false,",
            "    current_timestamp",
            ")"};
        String[] result = instance.getCustomInsertQuery(operation);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testGetCustomInsertQueryWithCustomSqlQueryWithoutQueryValue() {
        OperationInfo operation = new OperationInfo(DataTypeInfo.LIST_DATA_TYPE);
        operation.setEntity(getEntityForCustomInsertQuery());

        TestCustomSqlQuery customSqlQuery = getCustomSqlQuery();
        customSqlQuery.from = new String[]{"custom", "sql", "from"};
        customSqlQuery.beforeInsertIntoExpression = new String[]{"before", "insertIntoExpression"};
        customSqlQuery.insertInto = new String[]{"insertInto", "code"};
        customSqlQuery.afterInsertIntoExpression = new String[]{"after", "insertIntoExpression"};
        customSqlQuery.beforeInsertValuesExpression = new String[]{"before", "insertValuesExpression"};
        customSqlQuery.insertValues = new String[]{"insertValues", "code"};
        customSqlQuery.afterInsertValuesExpression = new String[]{"after", "insertValuesExpression"};
        operation.addAnnotation(customSqlQuery);

        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        instance.setUseAutoIncrementId(true);
        String[] expResult = new String[]{"insert into",
            "    custom",
            "    sql",
            "    from ",
            "(",
            "    before",
            "    insertIntoExpression ",
            "    insertInto",
            "    code ",
            "    after",
            "    insertIntoExpression ",
            ") values (",
            "    before",
            "    insertValuesExpression ",
            "    insertValues",
            "    code ",
            "    after",
            "    insertValuesExpression ",
            ")"};
        String[] result = instance.getCustomInsertQuery(operation);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testGetCustomUpdateQueryWithCustomSqlQueryWithQueryValue() {
        OperationInfo operation = new OperationInfo(DataTypeInfo.LIST_DATA_TYPE);
        TestCustomSqlQuery customSqlQuery = getCustomSqlQuery();
        customSqlQuery.query = new String[]{"custom", "sql", "query"};
        operation.addAnnotation(customSqlQuery);
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        String[] expResult = new String[]{"custom", "sql", "query"};
        String[] result = instance.getCustomUpdateQuery(operation);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testGetCustomUpdateQueryWithQuery() {
        OperationInfo operation = new OperationInfo(DataTypeInfo.LIST_DATA_TYPE);
        operation.addAnnotation(new Query() {
            @Override
            public String[] value() {
                return new String[]{"query", "value"};
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return Query.class;
            }
        });
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        String[] expResult = new String[]{"query", "value"};
        String[] result = instance.getCustomUpdateQuery(operation);
        assertArrayEquals(expResult, result);
    }

    private EntityInfo getEntityForCustomUpdateQuery() {
        EntityInfo entity = new EntityInfo(new DataTypeInfo("MyEntity"), EntityKind.ENTITY);

        FieldInfo field1 = new FieldInfo("manuallyField", DataTypeInfo.LIST_DATA_TYPE);
        field1.setManually(true);
        FieldInfo field2 = new FieldInfo("updateDateMarkField1", new DataTypeInfo("java.util", "Date"));
        field2.setUpdateDateMark(true);
        FieldInfo field3 = new FieldInfo("updateDateMarkField2", new DataTypeInfo("java.util", "Date"));
        field3.setUpdateDateMark(true);
        FieldInfo field4 = new FieldInfo("versionMarkField", new DataTypeInfo("String"));
        field4.setVersionMark(true);


        entity.addField(field1);
        entity.addField(field2);
        entity.addField(field3);
        entity.addField(field4);

        return entity;
    }

    @Test
    public void testGetCustomUpdateQuery() {
        OperationInfo operation = new OperationInfo(DataTypeInfo.LIST_DATA_TYPE);
        operation.setEntity(getEntityForCustomUpdateQuery());

        FieldInfo manuallyOperationField = new FieldInfo("manuallyOperationField", DataTypeInfo.LIST_DATA_TYPE);
        manuallyOperationField.setManually(true);
        operation.addField(manuallyOperationField);
        operation.addField(new FieldInfo("operationField", DataTypeInfo.LIST_DATA_TYPE));
        FieldInfo setValueField1 = new FieldInfo("setValueField1", DataTypeInfo.LIST_DATA_TYPE);
        setValueField1.setSetValueMark(true);
        operation.addField(setValueField1);
        FieldInfo setValueField2 = new FieldInfo("setValueField2", DataTypeInfo.LIST_DATA_TYPE);
        setValueField2.setSetValueMark(true);
        operation.addField(setValueField2);

        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        instance.handleVersionFieldOnUpdate = true;
        String[] expResult = new String[]{"update",
            "    MyEntity ",
            "set",
            "    setValueField1 = parameterValue,",
            "    setValueField2 = parameterValue,",
            "    updateDateMarkField1 = current_timestamp,",
            "    updateDateMarkField2 = current_timestamp,",
            "    versionMarkField = nextVersion",
            "where",
            "    operationField in <foreach collection='operationField' open='(' separator=',' close=')' item='_item_operationField'> #{_item_operationField,jdbcType=NUMERIC} </foreach>"};
        String[] result = instance.getCustomUpdateQuery(operation);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testGetCustomUpdateQueryWithoutHandleVersionFieldOnUpdate() {
        OperationInfo operation = new OperationInfo(DataTypeInfo.LIST_DATA_TYPE);
        operation.setEntity(getEntityForCustomUpdateQuery());

        FieldInfo manuallyOperationField = new FieldInfo("manuallyOperationField", DataTypeInfo.LIST_DATA_TYPE);
        manuallyOperationField.setManually(true);
        operation.addField(manuallyOperationField);
        operation.addField(new FieldInfo("operationField", DataTypeInfo.LIST_DATA_TYPE));
        FieldInfo setValueField1 = new FieldInfo("setValueField1", DataTypeInfo.LIST_DATA_TYPE);
        setValueField1.setSetValueMark(true);
        operation.addField(setValueField1);
        FieldInfo setValueField2 = new FieldInfo("setValueField2", DataTypeInfo.LIST_DATA_TYPE);
        setValueField2.setSetValueMark(true);
        operation.addField(setValueField2);

        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        String[] expResult = new String[]{"update",
            "    MyEntity ",
            "set",
            "    setValueField1 = parameterValue,",
            "    setValueField2 = parameterValue,",
            "    updateDateMarkField1 = current_timestamp,",
            "    updateDateMarkField2 = current_timestamp",
            "where",
            "    operationField in <foreach collection='operationField' open='(' separator=',' close=')' item='_item_operationField'> #{_item_operationField,jdbcType=NUMERIC} </foreach>"};
        String[] result = instance.getCustomUpdateQuery(operation);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testGetCustomUpdateQueryWithoutSetValues() {
        OperationInfo operation = new OperationInfo(DataTypeInfo.LIST_DATA_TYPE);
        operation.setEntity(getEntityForCustomUpdateQuery());

        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        String[] expResult = new String[]{""};
        String[] result = instance.getCustomUpdateQuery(operation);
        ArrayList<MessageContent> actualMsg = instance.getProcessingEnv().getMessager().getContent();
        ArrayList<MessageContent> expectedMsg = new ArrayList<MessageContent>();
        expectedMsg.add(new MessageContent(Diagnostic.Kind.ERROR,
                "An update operation must has SetValue fields for generate the query",
                operation.getElement()));
        assertArrayEquals(expectedMsg.toArray(), actualMsg.toArray());
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testGetCustomUpdateQueryWithCustomSqlQueryWithoutQueryValue() {
        OperationInfo operation = new OperationInfo(DataTypeInfo.LIST_DATA_TYPE);
        operation.setEntity(getEntityForCustomUpdateQuery());

        TestCustomSqlQuery customSqlQuery = getCustomSqlQuery();
        customSqlQuery.from = new String[]{"custom", "sql", "from"};
        customSqlQuery.beforeUpdateSetExpression = new String[]{"before", "insertIntoExpression"};
        customSqlQuery.updateSet = new String[]{"insertInto", "code"};
        customSqlQuery.afterUpdateSetExpression = new String[]{"after", "insertIntoExpression"};
        operation.addAnnotation(customSqlQuery);

        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        String[] expResult = new String[]{"update",
            "    custom",
            "    sql",
            "    from ",
            "set",
            "    before",
            "    insertIntoExpression ",
            "    insertInto",
            "    code ",
            "    after",
            "    insertIntoExpression "};
        String[] result = instance.getCustomUpdateQuery(operation);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testGetCustomDeleteQueryWithCustomSqlQueryWithQueryValue() {
        OperationInfo operation = new OperationInfo(DataTypeInfo.LIST_DATA_TYPE);
        TestCustomSqlQuery customSqlQuery = getCustomSqlQuery();
        customSqlQuery.query = new String[]{"custom", "sql", "query"};
        operation.addAnnotation(customSqlQuery);
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        String[] expResult = new String[]{"custom", "sql", "query"};
        String[] result = instance.getCustomDeleteQuery(operation);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testGetCustomDeleteQueryWithQuery() {
        OperationInfo operation = new OperationInfo(DataTypeInfo.LIST_DATA_TYPE);
        operation.addAnnotation(new Query() {
            @Override
            public String[] value() {
                return new String[]{"query", "value"};
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return Query.class;
            }
        });
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        String[] expResult = new String[]{"query", "value"};
        String[] result = instance.getCustomDeleteQuery(operation);
        assertArrayEquals(expResult, result);
    }

    private EntityInfo getEntityForCustomDeleteQuery() {
        EntityInfo entity = new EntityInfo(new DataTypeInfo("MyEntity"), EntityKind.ENTITY);



        FieldInfo field1 = new FieldInfo("manuallyField", DataTypeInfo.LIST_DATA_TYPE);
        field1.setManually(true);
        FieldInfo field2 = new FieldInfo("deletionMarkField", new DataTypeInfo("Boolean"));
        field2.setDeletionMark(true);
        FieldInfo field2BIS = new FieldInfo("deletionMarkFieldBIS", new DataTypeInfo("Boolean"));
        field2BIS.setDeletionMark(true);
        FieldInfo field3 = new FieldInfo("deleteDateMarkField", new DataTypeInfo("java.util", "Date"));
        field3.setDeleteDateMark(true);
        FieldInfo field4 = new FieldInfo("deleteUserMarkField", new DataTypeInfo("java.util", "Date"));
        field4.setDeleteUserMark(true);
        FieldInfo field5 = new FieldInfo("versionMarkField", new DataTypeInfo("String"));
        field5.setVersionMark(true);

        entity.addField(field1);
        entity.addField(field2);
        entity.addField(field2BIS);
        entity.addField(field3);
        entity.addField(field4);
        entity.addField(field5);

        return entity;
    }

    @Test
    public void testGetCustomDeleteQueryUsingLogicalDeletion() {
        OperationInfo operation = new OperationInfo(DataTypeInfo.LIST_DATA_TYPE);
        operation.setEntity(getEntityForCustomDeleteQuery());

        FieldInfo operationField1 = new FieldInfo("manuallyOperationField", new DataTypeInfo("Integer"));
        operationField1.setManually(true);
        FieldInfo operationField2 = new FieldInfo("operationField", new DataTypeInfo("Integer"));
        FieldInfo operationField3 = new FieldInfo("deleteUserMarkOperationField", new DataTypeInfo("Boolean"));
        operationField3.setSetValueMark(true);
        operationField3.setDeleteUserMark(true);
        FieldInfo operationField4 = new FieldInfo("deleteUserMarkOperationField2", new DataTypeInfo("Boolean"));
        operationField4.setSetValueMark(true);
        operationField4.setDeleteUserMark(true);
        operation.addField(operationField1);
        operation.addField(operationField2);
        operation.addField(operationField3);
        operation.addField(operationField4);

        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        instance.handleVersionFieldOnDelete = true;
        String[] expResult = new String[]{"update",
            "    MyEntity ",
            "set",
            "    deleteUserMarkOperationField = parameterValue,",
            "    deleteUserMarkOperationField2 = parameterValue,",
            "    deletionMarkField = true,",
            "    deletionMarkFieldBIS = true,",
            "    deleteDateMarkField = current_timestamp,",
            "    versionMarkField = nextVersion",
            "where",
            "    operationField = parameterValue",
            "    and deletionMarkField = false",
            "    and deletionMarkFieldBIS = false"};
        String[] result = instance.getCustomDeleteQuery(operation);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testGetCustomDeleteQueryUsingLogicalDeletionWithoutHandlingVersionFieldOnDelete() {
        OperationInfo operation = new OperationInfo(DataTypeInfo.LIST_DATA_TYPE);
        operation.setEntity(getEntityForCustomDeleteQuery());

        FieldInfo operationField1 = new FieldInfo("manuallyOperationField", new DataTypeInfo("Integer"));
        operationField1.setManually(true);
        FieldInfo operationField2 = new FieldInfo("operationField", new DataTypeInfo("Integer"));
        FieldInfo operationField3 = new FieldInfo("deleteUserMarkOperationField", new DataTypeInfo("Boolean"));
        operationField3.setSetValueMark(true);
        operationField3.setDeleteUserMark(true);
        FieldInfo operationField4 = new FieldInfo("deleteUserMarkOperationField2", new DataTypeInfo("Boolean"));
        operationField4.setSetValueMark(true);
        operationField4.setDeleteUserMark(true);
        operation.addField(operationField1);
        operation.addField(operationField2);
        operation.addField(operationField3);
        operation.addField(operationField4);

        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        String[] expResult = new String[]{"update",
            "    MyEntity ",
            "set",
            "    deleteUserMarkOperationField = parameterValue,",
            "    deleteUserMarkOperationField2 = parameterValue,",
            "    deletionMarkField = true,",
            "    deletionMarkFieldBIS = true,",
            "    deleteDateMarkField = current_timestamp",
            "where",
            "    operationField = parameterValue",
            "    and deletionMarkField = false",
            "    and deletionMarkFieldBIS = false"};
        String[] result = instance.getCustomDeleteQuery(operation);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testGetCustomDeleteQueryUsingLogicalDeletionWithCustomSqlQueryWithoutQueryValue() {
        OperationInfo operation = new OperationInfo(DataTypeInfo.LIST_DATA_TYPE);
        operation.setEntity(getEntityForCustomDeleteQuery());

        TestCustomSqlQuery customSqlQuery = getCustomSqlQuery();
        customSqlQuery.from = new String[]{"custom", "sql", "from"};
        operation.addAnnotation(customSqlQuery);

        FieldInfo operationField1 = new FieldInfo("manuallyOperationField", new DataTypeInfo("Integer"));
        operationField1.setManually(true);
        FieldInfo operationField2 = new FieldInfo("operationField", new DataTypeInfo("Integer"));
        FieldInfo operationField3 = new FieldInfo("deleteUserMarkOperationField", new DataTypeInfo("Boolean"));
        operationField3.setSetValueMark(true);
        operationField3.setDeleteUserMark(true);
        FieldInfo operationField4 = new FieldInfo("deleteUserMarkOperationField2", new DataTypeInfo("Boolean"));
        operationField4.setSetValueMark(true);
        operationField4.setDeleteUserMark(true);
        operation.addField(operationField1);
        operation.addField(operationField2);
        operation.addField(operationField3);
        operation.addField(operationField4);

        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        String[] expResult = new String[]{"update",
            "    custom",
            "    sql",
            "    from ",
            "set",
            "    deleteUserMarkOperationField = parameterValue,",
            "    deleteUserMarkOperationField2 = parameterValue,",
            "    deletionMarkField = true,",
            "    deletionMarkFieldBIS = true,",
            "    deleteDateMarkField = current_timestamp",
            "where",
            "    operationField = parameterValue",
            "    and deletionMarkField = false",
            "    and deletionMarkFieldBIS = false"};
        String[] result = instance.getCustomDeleteQuery(operation);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testGetCustomDeleteQueryNotUsingLogicalDeletion() {
        OperationInfo operation = new OperationInfo(DataTypeInfo.LIST_DATA_TYPE);
        operation.setEntity(getEntityForCustomDeleteQuery());
        operation.setIgnoreLogicalDeletionEnabled(true);

        FieldInfo operationField1 = new FieldInfo("operationField", new DataTypeInfo("Integer"));
        operation.addField(operationField1);

        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        String[] expResult = new String[]{"delete from",
            "    MyEntity ",
            "where",
            "    operationField = parameterValue",};
        String[] result = instance.getCustomDeleteQuery(operation);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testGetCustomDeleteQueryNotUsingLogicalDeletionWithCustomSqlQueryWithFromValue() {
        OperationInfo operation = new OperationInfo(DataTypeInfo.LIST_DATA_TYPE);
        operation.setEntity(getEntityForCustomDeleteQuery());
        operation.setIgnoreLogicalDeletionEnabled(true);

        TestCustomSqlQuery customSqlQuery = getCustomSqlQuery();
        customSqlQuery.from = new String[]{"custom", "sql", "from"};
        operation.addAnnotation(customSqlQuery);

        FieldInfo operationField1 = new FieldInfo("operationField", new DataTypeInfo("Integer"));
        operation.addField(operationField1);

        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        String[] expResult = new String[]{"delete from",
            "    custom",
            "    sql",
            "    from ",
            "where",
            "    operationField = parameterValue",};
        String[] result = instance.getCustomDeleteQuery(operation);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testGetSelectOneQuery() {
        OperationInfo operation = getOperationForWhere(false);
        operation.setEntity(getEntityForSelect());
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        String[] expResult = new String[]{"select",
            "    mappedName as \"mappedField\",",
            "    field",
            "from",
            "    MyEntity ",
            "<where>",
            "    normalField = parameterValue",
            "    <if test='optionalField != null'>and optionalField in <foreach collection='optionalField' open='(' separator=',' close=')' item='_item_optionalField'> #{_item_optionalField,jdbcType=NUMERIC} </foreach></if>",
            "    and listField in <foreach collection='listField' open='(' separator=',' close=')' item='_item_listField'> #{_item_listField,jdbcType=NUMERIC} </foreach>",
            "</where>",
            "order by orderByField"};
        String[] result = instance.getSelectOneQuery(operation);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testGetSelectOneQueryWithQueryAnnotation() {
        OperationInfo operation = getOperationForWhere(false);
        operation.setEntity(getEntityForSelect());
        operation.addAnnotation(new Query() {
            @Override
            public String[] value() {
                return new String[]{"query", "value"};
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return Query.class;
            }
        });
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        String[] expResult = new String[]{"query",
            "value"};
        String[] result = instance.getSelectOneQuery(operation);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testGetSelectManyQuery() {
        OperationInfo operation = getOperationForWhere(false);
        operation.setEntity(getEntityForSelect());
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        String[] expResult = new String[]{"select",
            "    mappedName as \"mappedField\",",
            "    field",
            "from",
            "    MyEntity ",
            "<where>",
            "    normalField = parameterValue",
            "    <if test='optionalField != null'>and optionalField in <foreach collection='optionalField' open='(' separator=',' close=')' item='_item_optionalField'> #{_item_optionalField,jdbcType=NUMERIC} </foreach></if>",
            "    and listField in <foreach collection='listField' open='(' separator=',' close=')' item='_item_listField'> #{_item_listField,jdbcType=NUMERIC} </foreach>",
            "</where>",
            "order by orderByField"};
        String[] result = instance.getSelectManyQuery(operation);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testGetSelectManyQueryWithQueryAnnotation() {
        OperationInfo operation = getOperationForWhere(false);
        operation.setEntity(getEntityForSelect());
        operation.addAnnotation(new Query() {
            @Override
            public String[] value() {
                return new String[]{"query", "value"};
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return Query.class;
            }
        });
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        String[] expResult = new String[]{"query",
            "value"};
        String[] result = instance.getSelectManyQuery(operation);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testGetSelectPageQuery() {
        OperationInfo operation = new OperationInfo(DataTypeInfo.LIST_DATA_TYPE);
        operation.setEntity(getEntityForSelect());
        FieldInfo field1 = new FieldInfo("myField1", new DataTypeInfo("Integer"));
        field1.setManually(true);
        FieldInfo field2 = new FieldInfo("myField2", new DataTypeInfo("Integer"));
        field2.setSetValueMark(true);
        FieldInfo field3 = new FieldInfo("myField3", new DataTypeInfo("Integer"));
        field3.setOrderBy(true);
        operation.addField(field1);
        operation.addField(field2);
        operation.addField(field3);

        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        String[] expResult = new String[]{"select",
            "    mappedName as \"mappedField\",",
            "    fieldappendOrderByAfterSelectForSelectPage",
            "from",
            "    MyEntity ",
            "order by myField3",
            "selectPageAfterOrderBy",
            "envolveInSelectPage"};
        String[] result = instance.getSelectPageQuery(operation);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testGetSelectPageQueryWithNullResult() {
        OperationInfo operation = new OperationInfo(DataTypeInfo.LIST_DATA_TYPE);
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        String[] expResult = null;
        String[] result = instance.getSelectPageQuery(operation);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testGetSelectPageQueryWithQueryAnnotation() {
        OperationInfo operation = new OperationInfo(DataTypeInfo.LIST_DATA_TYPE);
        operation.setEntity(new EntityInfo(DataTypeInfo.LIST_DATA_TYPE, EntityKind.ENTITY));
        operation.addAnnotation(new Query() {
            @Override
            public String[] value() {
                return new String[]{"query", "value"};
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return Query.class;
            }
        });
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        String[] expResult = new String[]{"query",
            "value",
            "envolveInSelectPage"};
        String[] result = instance.getSelectPageQuery(operation);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testGetSelectPageQueryWithPageQueriesAnnotation() {
        OperationInfo operation = new OperationInfo(DataTypeInfo.LIST_DATA_TYPE);
        operation.setEntity(new EntityInfo(DataTypeInfo.LIST_DATA_TYPE, EntityKind.ENTITY));
        operation.addAnnotation(new PageQueries() {
            @Override
            public String[] selectCount() {
                return null;
            }

            @Override
            public String[] selectPage() {
                return new String[]{"selectPage", "value"};
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return PageQueries.class;
            }
        });
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        String[] expResult = new String[]{"selectPage",
            "value"};
        String[] result = instance.getSelectPageQuery(operation);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testGetSelectPageCountQuery() {
        OperationInfo operation = new OperationInfo(DataTypeInfo.LIST_DATA_TYPE);
        operation.setEntity(new EntityInfo(new DataTypeInfo("MyEntity"), EntityKind.ENTITY));
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        String[] expResult = new String[]{"select",
            "    count(*)",
            "from",
            "    MyEntity "};
        String[] result = instance.getSelectPageCountQuery(operation);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testGetSelectPageCountQueryWithQueryAnnotation() {
        OperationInfo operation = new OperationInfo(DataTypeInfo.LIST_DATA_TYPE);
        operation.setEntity(new EntityInfo(new DataTypeInfo("MyEntity"), EntityKind.ENTITY));
        operation.addAnnotation(new Query() {
            @Override
            public String[] value() {
                return new String[]{"query", "value"};
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return Query.class;
            }
        });
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        String[] expResult = new String[]{"select count(*) from (",
            "query",
            "value",
            ")"};
        String[] result = instance.getSelectPageCountQuery(operation);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testGetSelectPageCountQueryWithPageQueriesAnnotation() {
        OperationInfo operation = new OperationInfo(DataTypeInfo.LIST_DATA_TYPE);
        operation.setEntity(new EntityInfo(new DataTypeInfo("MyEntity"), EntityKind.ENTITY));

        operation.addAnnotation(new PageQueries() {
            @Override
            public String[] selectCount() {
                return new String[]{"selectCount", "value"};
            }

            @Override
            public String[] selectPage() {
                return new String[]{"selectPage", "value"};
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return PageQueries.class;
            }
        });

        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        String[] expResult = new String[]{"selectCount",
            "value"};
        String[] result = instance.getSelectPageCountQuery(operation);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testGetEntitySelectByIdQueryWithEntityWithoutEntityQueriesAnnotation() {
        EntityInfo entity = getEntityForSelectByIdQuery(false);
        OperationInfo operation = null;
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        String[] expResult = new String[]{"select",
            "    mappedName as \"mappedField\",",
            "    field,",
            "    deletionMarkField,",
            "    identifierField",
            "from",
            "    MyEntity ",
            "where",
            "    deletionMarkField = false",
            "    and identifierField = parameterValue"};
        String[] result = instance.getEntitySelectByIdQuery(entity, operation);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testGetEntitySelectByIdQueryWithEntityWithoutEntityQueriesAnnotationReverseAnds() {
        EntityInfo entity = getEntityForSelectByIdQuery(true);
        OperationInfo operation = null;
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        String[] expResult = new String[]{"select",
            "    mappedName as \"mappedField\",",
            "    field,",
            "    identifierField,",
            "    deletionMarkField",
            "from",
            "    MyEntity ",
            "where",
            "    identifierField = parameterValue",
            "    and deletionMarkField = false"};
        String[] result = instance.getEntitySelectByIdQuery(entity, operation);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testGetEntitySelectByIdQueryWithEntityWithoutEntityQueriesAnnotationAndEntityWithoutLogicalDeletion() {
        EntityInfo entity = getEntityForSelectByIdQuery(false);
        entity.setIgnoreLogicalDeletionEnabled(true);
        OperationInfo operation = null;
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        String[] expResult = new String[]{"select",
            "    mappedName as \"mappedField\",",
            "    field,",
            "    deletionMarkField,",
            "    identifierField",
            "from",
            "    MyEntity ",
            "where",
            "    identifierField = parameterValue"};
        String[] result = instance.getEntitySelectByIdQuery(entity, operation);
        assertArrayEquals(expResult, result);
    }

    private EntityInfo getEntityForSelectByIdQuery(boolean reverseAnds) {
        EntityInfo entity = new EntityInfo(new DataTypeInfo("MyEntity"), EntityKind.ENTITY);

        FieldInfo field1 = new FieldInfo("mappedField", DataTypeInfo.LIST_DATA_TYPE);
        field1.setMappedName("mappedName");
        FieldInfo field2 = new FieldInfo("manuallyField", DataTypeInfo.LIST_DATA_TYPE);
        field2.setManually(true);
        FieldInfo field3 = new FieldInfo("field", DataTypeInfo.LIST_DATA_TYPE);
        FieldInfo field4 = new FieldInfo("identifierField", DataTypeInfo.LIST_DATA_TYPE);
        field4.setIdentifier(true);
        FieldInfo field5 = new FieldInfo("deletionMarkField", DataTypeInfo.LIST_DATA_TYPE);
        field5.setDeletionMark(true);
        entity.addField(field1);
        entity.addField(field2);
        entity.addField(field3);
        if (reverseAnds) {
            entity.addField(field4);
            entity.addField(field5);
        } else {
            entity.addField(field5);
            entity.addField(field4);
        }

        return entity;
    }

    @Test
    public void testGetEntitySelectByIdQueryWithEntityWithEntityQueriesAnnotation() {
        EntityInfo entity = new EntityInfo(DataTypeInfo.LIST_DATA_TYPE, EntityKind.ENTITY);
        entity.addAnnotation(getEntityQueriesSample(false));
        OperationInfo operation = null;
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        String[] expResult = new String[]{"selectById", "code"};
        String[] result = instance.getEntitySelectByIdQuery(entity, operation);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testGetEntityInsertQueryHandleVersionFieldOnInsert() {
        EntityInfo entity = getEntityForInsertQuery();
        OperationInfo operation = null;
        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        instance.handleVersionFieldOnInsert = true;
        instance.setUseAutoIncrementId(true);
        String[] expResult = new String[]{"insert into",
            "    MyEntity ",
            "(",
            "    mappedName,",
            "    idField,",
            "    field,",
            "    deletionMarkField,",
            "    insertDateMarkField,",
            "    insertUserMarkField,",
            "    versionMarkField",
            ") values (",
            "    parameterValue,",
            "    parameterValue,",
            "    parameterValue,",
            "    false,",
            "    current_timestamp,",
            "    parameterValue,",
            "    initalVersionValue",
            ")"};
        String[] result = instance.getEntityInsertQuery(entity, operation);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testGetEntityInsertQueryWithoutHandleVersionFieldOnInsert() {
        EntityInfo entity = getEntityForInsertQuery();
        OperationInfo operation = null;
        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        instance.handleVersionFieldOnInsert = false;
        instance.setUseAutoIncrementId(true);
        String[] expResult = new String[]{"insert into",
            "    MyEntity ",
            "(",
            "    mappedName,",
            "    idField,",
            "    field,",
            "    deletionMarkField,",
            "    insertDateMarkField,",
            "    insertUserMarkField",
            ") values (",
            "    parameterValue,",
            "    parameterValue,",
            "    parameterValue,",
            "    false,",
            "    current_timestamp,",
            "    parameterValue",
            ")"};
        String[] result = instance.getEntityInsertQuery(entity, operation);
        assertArrayEquals(expResult, result);
    }

    private EntityQueries getEntityQueriesSample(final boolean nullLastInsertedId) {
        return new EntityQueries() {
            @Override
            public String[] selectById() {
                return new String[]{"selectById", "code"};
            }

            @Override
            public String[] insert() {
                return new String[]{"insert", "code"};
            }

            @Override
            public String[] lastInsertedId() {
                if (nullLastInsertedId) {
                    return null;
                }
                return new String[]{"last", "inserted", "value"};
            }

            @Override
            public String[] update() {
                return new String[]{"update", "query"};
            }

            @Override
            public String[] deleteById() {
                return new String[]{"deleteById", "query"};
            }

            @Override
            public String[] merge() {
                return new String[]{"merge", "query"};
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return EntityQueries.class;
            }
        };
    }

    @Test
    public void testGetEntityInsertQueryWithEntityWithEntityQueriesAnnotation() {
        EntityInfo entity = new EntityInfo(DataTypeInfo.LIST_DATA_TYPE, EntityKind.ENTITY);
        entity.addAnnotation(getEntityQueriesSample(false));
        OperationInfo operation = null;
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        String[] expResult = new String[]{"insert", "code"};
        String[] result = instance.getEntityInsertQuery(entity, operation);
        assertArrayEquals(expResult, result);
    }

    private EntityInfo getEntityForInsertQuery() {
        EntityInfo entity = new EntityInfo(new DataTypeInfo("MyEntity"), EntityKind.ENTITY);

        FieldInfo field0 = new FieldInfo("idField", DataTypeInfo.LIST_DATA_TYPE);
        field0.setIdentifier(true);
        FieldInfo field1 = new FieldInfo("mappedField", DataTypeInfo.LIST_DATA_TYPE);
        field1.setMappedName("mappedName");
        FieldInfo field2 = new FieldInfo("manuallyField", DataTypeInfo.LIST_DATA_TYPE);
        field2.setManually(true);
        FieldInfo field3 = new FieldInfo("field", DataTypeInfo.LIST_DATA_TYPE);
        FieldInfo field4 = new FieldInfo("idFieldNotIncluyed", DataTypeInfo.LIST_DATA_TYPE);
        field4.setIdentifier(true);
        field4.setIdentifierAutogenerated(true);
        FieldInfo field5 = new FieldInfo("deletionMarkField", new DataTypeInfo("Boolean"));
        field5.setDeletionMark(true);
        FieldInfo field6 = new FieldInfo("insertDateMarkField", new DataTypeInfo("java.util", "Date"));
        field6.setInsertDateMark(true);
        FieldInfo field7 = new FieldInfo("insertUserMarkField", new DataTypeInfo("java.util", "Date"));
        field7.setInsertUserMark(true);
        FieldInfo field8 = new FieldInfo("updateDateMarkField", new DataTypeInfo("java.util", "Date"));
        field8.setUpdateDateMark(true);
        FieldInfo field9 = new FieldInfo("updateUserMarkField", new DataTypeInfo("java.util", "Date"));
        field9.setUpdateUserMark(true);
        FieldInfo field10 = new FieldInfo("deleteDateMarkField", new DataTypeInfo("java.util", "Date"));
        field10.setDeleteDateMark(true);
        FieldInfo field11 = new FieldInfo("deleteUserMarkField", new DataTypeInfo("java.util", "Date"));
        field11.setDeleteUserMark(true);
        FieldInfo field12 = new FieldInfo("versionMarkField", new DataTypeInfo("String"));
        field12.setVersionMark(true);


        entity.addField(field1);
        entity.addField(field0);
        entity.addField(field2);
        entity.addField(field3);
        entity.addField(field4);
        entity.addField(field5);
        entity.addField(field6);
        entity.addField(field7);
        entity.addField(field8);
        entity.addField(field9);
        entity.addField(field10);
        entity.addField(field11);
        entity.addField(field12);

        return entity;
    }

    @Test
    public void testGetEntityLastInsertedIdQuery() {
        EntityInfo entity = new EntityInfo(DataTypeInfo.LIST_DATA_TYPE, EntityKind.ENTITY);
        FieldInfo field = new FieldInfo("myField", DataTypeInfo.LIST_DATA_TYPE);
        field.setIdentifier(true);
        entity.addField(field);
        OperationInfo operation = null;
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        String[] expResult = new String[]{"currentValue"};
        String[] result = instance.getEntityLastInsertedIdQuery(entity, operation);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testGetEntityLastInsertedIdQueryWithEntityWithEntityQueriesAnnotation() {
        EntityInfo entity = new EntityInfo(DataTypeInfo.LIST_DATA_TYPE, EntityKind.ENTITY);
        entity.addAnnotation(getEntityQueriesSample(false));
        OperationInfo operation = null;
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        String[] expResult = new String[]{"last", "inserted", "value"};
        String[] result = instance.getEntityLastInsertedIdQuery(entity, operation);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testGetEntityLastInsertedIdQueryWithNullLastInsertedId() {
        EntityInfo entity = new EntityInfo(DataTypeInfo.LIST_DATA_TYPE, EntityKind.ENTITY);
        entity.addAnnotation(getEntityQueriesSample(true));
        OperationInfo operation = null;
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        String[] expResult = null;
        String[] result = instance.getEntityLastInsertedIdQuery(entity, operation);
        assertArrayEquals(expResult, result);
    }

    private EntityInfo getEntityForUpdateQuery(boolean changeOrder) {
        EntityInfo entity = new EntityInfo(new DataTypeInfo("MyEntity"), EntityKind.ENTITY);


        FieldInfo field1 = new FieldInfo("mappedField", DataTypeInfo.LIST_DATA_TYPE);
        field1.setMappedName("mappedName");
        FieldInfo field2 = new FieldInfo("manuallyField", DataTypeInfo.LIST_DATA_TYPE);
        field2.setManually(true);
        FieldInfo field3 = new FieldInfo("field", DataTypeInfo.LIST_DATA_TYPE);
        FieldInfo field4 = new FieldInfo("idField", DataTypeInfo.LIST_DATA_TYPE);
        field4.setIdentifier(true);
        FieldInfo field5 = new FieldInfo("deletionMarkField", new DataTypeInfo("Boolean"));
        field5.setDeletionMark(true);
        FieldInfo field6 = new FieldInfo("insertDateMarkField", new DataTypeInfo("java.util", "Date"));
        field6.setInsertDateMark(true);
        FieldInfo field7 = new FieldInfo("insertUserMarkField", new DataTypeInfo("java.util", "Date"));
        field7.setInsertUserMark(true);
        FieldInfo field8 = new FieldInfo("updateDateMarkField", new DataTypeInfo("java.util", "Date"));
        field8.setUpdateDateMark(true);
        FieldInfo field9 = new FieldInfo("updateUserMarkField", new DataTypeInfo("java.util", "Date"));
        field9.setUpdateUserMark(true);
        FieldInfo field10 = new FieldInfo("deleteDateMarkField", new DataTypeInfo("java.util", "Date"));
        field10.setDeleteDateMark(true);
        FieldInfo field11 = new FieldInfo("deleteUserMarkField", new DataTypeInfo("java.util", "Date"));
        field11.setDeleteUserMark(true);
        FieldInfo field12 = new FieldInfo("versionMarkField", new DataTypeInfo("String"));
        field12.setVersionMark(true);


        entity.addField(field1);
        entity.addField(field2);
        entity.addField(field3);
        if (changeOrder) {
            entity.addField(field5);
            entity.addField(field4);
        } else {
            entity.addField(field4);
            entity.addField(field5);
        }
        entity.addField(field6);
        entity.addField(field7);
        entity.addField(field8);
        entity.addField(field9);
        entity.addField(field10);
        entity.addField(field11);
        entity.addField(field12);

        return entity;
    }

    @Test
    public void testGetEntityUpdateQuery() {
        EntityInfo entity = getEntityForUpdateQuery(false);
        OperationInfo operation = null;
        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        instance.handleVersionFieldOnUpdate = true;
        String[] expResult = new String[]{"update",
            "    MyEntity ",
            "set",
            "    mappedName = parameterValue,",
            "    field = parameterValue,",
            "    updateDateMarkField = current_timestamp,",
            "    updateUserMarkField = parameterValue,",
            "    versionMarkField = nextVersion",
            "where",
            "    idField = parameterValue",
            "    and deletionMarkField = false"};
        String[] result = instance.getEntityUpdateQuery(entity, operation);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testGetEntityUpdateQueryChangingFieldsOrder() {
        EntityInfo entity = getEntityForUpdateQuery(true);
        OperationInfo operation = null;
        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        instance.handleVersionFieldOnUpdate = true;
        String[] expResult = new String[]{"update",
            "    MyEntity ",
            "set",
            "    mappedName = parameterValue,",
            "    field = parameterValue,",
            "    updateDateMarkField = current_timestamp,",
            "    updateUserMarkField = parameterValue,",
            "    versionMarkField = nextVersion",
            "where",
            "    deletionMarkField = false",
            "    and idField = parameterValue"};
        String[] result = instance.getEntityUpdateQuery(entity, operation);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testGetEntityUpdateQueryWithoutHandlingVersionFieldOnUpdate() {
        EntityInfo entity = getEntityForUpdateQuery(false);
        OperationInfo operation = null;
        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        instance.handleVersionFieldOnUpdate = false;
        String[] expResult = new String[]{"update",
            "    MyEntity ",
            "set",
            "    mappedName = parameterValue,",
            "    field = parameterValue,",
            "    updateDateMarkField = current_timestamp,",
            "    updateUserMarkField = parameterValue",
            "where",
            "    idField = parameterValue",
            "    and deletionMarkField = false"};
        String[] result = instance.getEntityUpdateQuery(entity, operation);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testGetEntityUpdateQueryWithoutUsingLogicalDeletion() {
        EntityInfo entity = getEntityForUpdateQuery(false);
        entity.setIgnoreLogicalDeletionEnabled(true);
        OperationInfo operation = null;
        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        String[] expResult = new String[]{"update",
            "    MyEntity ",
            "set",
            "    mappedName = parameterValue,",
            "    field = parameterValue,",
            "    updateDateMarkField = current_timestamp,",
            "    updateUserMarkField = parameterValue",
            "where",
            "    idField = parameterValue"};
        String[] result = instance.getEntityUpdateQuery(entity, operation);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testGetEntityUpdateQueryWithEntityWithEntityQueriesAnnotation() {
        EntityInfo entity = new EntityInfo(DataTypeInfo.LIST_DATA_TYPE, EntityKind.ENTITY);
        entity.addAnnotation(getEntityQueriesSample(false));
        OperationInfo operation = null;
        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        String[] expResult = new String[]{"update", "query"};
        String[] result = instance.getEntityUpdateQuery(entity, operation);
        assertArrayEquals(expResult, result);
    }

    private EntityInfo getEntityForMergeQuery(boolean changeOrder) {
        EntityInfo entity = new EntityInfo(new DataTypeInfo("MyEntity"), EntityKind.ENTITY);


        FieldInfo field1 = new FieldInfo("mappedField", DataTypeInfo.LIST_DATA_TYPE);
        field1.setMappedName("mappedName");
        FieldInfo field2 = new FieldInfo("manuallyField", DataTypeInfo.LIST_DATA_TYPE);
        field2.setManually(true);
        FieldInfo field3 = new FieldInfo("field", DataTypeInfo.LIST_DATA_TYPE);
        FieldInfo field4 = new FieldInfo("idField", DataTypeInfo.LIST_DATA_TYPE);
        field4.setIdentifier(true);
        FieldInfo field5 = new FieldInfo("deletionMarkField", new DataTypeInfo("Boolean"));
        field5.setDeletionMark(true);
        FieldInfo field6 = new FieldInfo("insertDateMarkField", new DataTypeInfo("java.util", "Date"));
        field6.setInsertDateMark(true);
        FieldInfo field7 = new FieldInfo("insertUserMarkField", new DataTypeInfo("java.util", "Date"));
        field7.setInsertUserMark(true);
        FieldInfo field8 = new FieldInfo("updateDateMarkField", new DataTypeInfo("java.util", "Date"));
        field8.setUpdateDateMark(true);
        FieldInfo field8BIS = new FieldInfo("updateDateMarkFieldBIS", new DataTypeInfo("java.util", "Date"));
        field8BIS.setUpdateDateMark(true);
        FieldInfo field9 = new FieldInfo("updateUserMarkField", new DataTypeInfo("java.util", "Date"));
        field9.setUpdateUserMark(true);
        FieldInfo field9BIS = new FieldInfo("updateUserMarkFieldBIS", new DataTypeInfo("java.util", "Date"));
        field9BIS.setUpdateUserMark(true);
        FieldInfo field10 = new FieldInfo("deleteDateMarkField", new DataTypeInfo("java.util", "Date"));
        field10.setDeleteDateMark(true);
        FieldInfo field11 = new FieldInfo("deleteUserMarkField", new DataTypeInfo("java.util", "Date"));
        field11.setDeleteUserMark(true);
        FieldInfo field12 = new FieldInfo("versionMarkField", new DataTypeInfo("String"));
        field12.setVersionMark(true);
        FieldInfo field12BIS = new FieldInfo("versionMarkFieldBIS", new DataTypeInfo("String"));
        field12BIS.setVersionMark(true);
        FieldInfo field13 = new FieldInfo("normalField", DataTypeInfo.LIST_DATA_TYPE);


        entity.addField(field1);
        entity.addField(field2);
        entity.addField(field3);
        if (changeOrder) {
            entity.addField(field5);
            entity.addField(field4);
        } else {
            entity.addField(field4);
            entity.addField(field5);
        }
        entity.addField(field6);
        entity.addField(field7);
        entity.addField(field8);
        entity.addField(field8BIS);
        entity.addField(field9);
        entity.addField(field9BIS);
        entity.addField(field10);
        entity.addField(field11);
        entity.addField(field12);
        entity.addField(field12BIS);
        entity.addField(field13);

        return entity;
    }

    @Test
    public void testGetEntityMergeQuery() {
        EntityInfo entity = getEntityForMergeQuery(false);
        OperationInfo operation = null;
        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        instance.handleVersionFieldOnUpdate = true;
        String[] expResult = new String[]{"update",
            "    MyEntity ",
            "<set>",
            "    <if test='mappedField != null'>mappedName = parameterValue,</if>",
            "    <if test='field != null'>field = parameterValue,</if>",
            "    updateDateMarkField = current_timestamp,",
            "    updateDateMarkFieldBIS = current_timestamp,",
            "    <if test='updateUserMarkField != null'>updateUserMarkField = parameterValue,</if>",
            "    <if test='updateUserMarkFieldBIS != null'>updateUserMarkFieldBIS = parameterValue,</if>",
            "    versionMarkField = nextVersion,",
            "    versionMarkFieldBIS = nextVersion,",
            "    <if test='normalField != null'>normalField = parameterValue</if>",
            "</set>",
            "where",
            "    idField = parameterValue",
            "    and deletionMarkField = false"};
        String[] result = instance.getEntityMergeQuery(entity, operation);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testGetEntityMergeQueryChangingOrder() {
        EntityInfo entity = getEntityForMergeQuery(true);
        OperationInfo operation = null;
        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        instance.handleVersionFieldOnUpdate = true;
        String[] expResult = new String[]{"update",
            "    MyEntity ",
            "<set>",
            "    <if test='mappedField != null'>mappedName = parameterValue,</if>",
            "    <if test='field != null'>field = parameterValue,</if>",
            "    updateDateMarkField = current_timestamp,",
            "    updateDateMarkFieldBIS = current_timestamp,",
            "    <if test='updateUserMarkField != null'>updateUserMarkField = parameterValue,</if>",
            "    <if test='updateUserMarkFieldBIS != null'>updateUserMarkFieldBIS = parameterValue,</if>",
            "    versionMarkField = nextVersion,",
            "    versionMarkFieldBIS = nextVersion,",
            "    <if test='normalField != null'>normalField = parameterValue</if>",
            "</set>",
            "where",
            "    deletionMarkField = false",
            "    and idField = parameterValue"};
        String[] result = instance.getEntityMergeQuery(entity, operation);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testGetEntityMergeQueryWithoutHandlingVersionFieldOnUpdate() {
        EntityInfo entity = getEntityForMergeQuery(false);
        OperationInfo operation = null;
        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        instance.handleVersionFieldOnUpdate = false;
        String[] expResult = new String[]{"update",
            "    MyEntity ",
            "<set>",
            "    <if test='mappedField != null'>mappedName = parameterValue,</if>",
            "    <if test='field != null'>field = parameterValue,</if>",
            "    updateDateMarkField = current_timestamp,",
            "    updateDateMarkFieldBIS = current_timestamp,",
            "    <if test='updateUserMarkField != null'>updateUserMarkField = parameterValue,</if>",
            "    <if test='updateUserMarkFieldBIS != null'>updateUserMarkFieldBIS = parameterValue,</if>",
            "    <if test='normalField != null'>normalField = parameterValue</if>",
            "</set>",
            "where",
            "    idField = parameterValue",
            "    and deletionMarkField = false"};
        String[] result = instance.getEntityMergeQuery(entity, operation);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testGetEntityMergeQueryWithoutUsingLogicalDeletion() {
        EntityInfo entity = getEntityForMergeQuery(false);
        entity.setIgnoreLogicalDeletionEnabled(true);
        OperationInfo operation = null;
        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        String[] expResult = new String[]{"update",
            "    MyEntity ",
            "<set>",
            "    <if test='mappedField != null'>mappedName = parameterValue,</if>",
            "    <if test='field != null'>field = parameterValue,</if>",
            "    updateDateMarkField = current_timestamp,",
            "    updateDateMarkFieldBIS = current_timestamp,",
            "    <if test='updateUserMarkField != null'>updateUserMarkField = parameterValue,</if>",
            "    <if test='updateUserMarkFieldBIS != null'>updateUserMarkFieldBIS = parameterValue,</if>",
            "    <if test='normalField != null'>normalField = parameterValue</if>",
            "</set>",
            "where",
            "    idField = parameterValue"};
        String[] result = instance.getEntityMergeQuery(entity, operation);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testGetEntityMergeQueryWithEntityWithEntityQueriesAnnotation() {
        EntityInfo entity = new EntityInfo(DataTypeInfo.LIST_DATA_TYPE, EntityKind.ENTITY);
        entity.addAnnotation(getEntityQueriesSample(false));
        OperationInfo operation = null;
        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        String[] expResult = new String[]{"merge", "query"};
        String[] result = instance.getEntityMergeQuery(entity, operation);
        assertArrayEquals(expResult, result);
    }

    private EntityInfo getEntityForDeleteQuery(boolean changeOrder) {
        EntityInfo entity = new EntityInfo(new DataTypeInfo("MyEntity"), EntityKind.ENTITY);


        FieldInfo field1 = new FieldInfo("mappedField", DataTypeInfo.LIST_DATA_TYPE);
        field1.setMappedName("mappedName");
        FieldInfo field2 = new FieldInfo("manuallyField", DataTypeInfo.LIST_DATA_TYPE);
        field2.setManually(true);
        FieldInfo field3 = new FieldInfo("field", DataTypeInfo.LIST_DATA_TYPE);
        FieldInfo field4 = new FieldInfo("idField", DataTypeInfo.LIST_DATA_TYPE);
        field4.setIdentifier(true);
        FieldInfo field5 = new FieldInfo("deletionMarkField", new DataTypeInfo("Boolean"));
        field5.setDeletionMark(true);
        FieldInfo field6 = new FieldInfo("deleteDateMarkField", new DataTypeInfo("java.util", "Date"));
        field6.setDeleteDateMark(true);
        FieldInfo field7 = new FieldInfo("deleteUserMarkField", new DataTypeInfo("java.util", "Date"));
        field7.setDeleteUserMark(true);
        FieldInfo field8 = new FieldInfo("versionMarkField", new DataTypeInfo("String"));
        field8.setVersionMark(true);


        entity.addField(field1);
        entity.addField(field2);
        entity.addField(field3);

        if (changeOrder) {
            entity.addField(field4);
            entity.addField(field5);
            entity.addField(field6);
        } else {
            entity.addField(field6);
            entity.addField(field5);
            entity.addField(field4);
        }
        entity.addField(field7);
        entity.addField(field8);

        return entity;
    }

    @Test
    public void testGetEntityDeleteByIdQuery() {
        EntityInfo entity = getEntityForDeleteQuery(false);
        OperationInfo operation = null;
        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        instance.handleVersionFieldOnDelete = true;
        String[] expResult = new String[]{"update",
            "    MyEntity ",
            "set",
            "    deleteDateMarkField = current_timestamp,",
            "    deletionMarkField = true,",
            "    versionMarkField = nextVersion",
            "where",
            "    deletionMarkField = false",
            "    and idField = parameterValue"};
        String[] result = instance.getEntityDeleteByIdQuery(entity, operation);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testGetEntityDeleteByIdQueryChangingOrder() {
        EntityInfo entity = getEntityForDeleteQuery(true);
        OperationInfo operation = null;
        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        instance.handleVersionFieldOnDelete = true;
        String[] expResult = new String[]{"update",
            "    MyEntity ",
            "set",
            "    deletionMarkField = true,",
            "    deleteDateMarkField = current_timestamp,",
            "    versionMarkField = nextVersion",
            "where",
            "    idField = parameterValue",
            "    and deletionMarkField = false"};
        String[] result = instance.getEntityDeleteByIdQuery(entity, operation);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testGetEntityDeleteByIdQueryWithoutHandlingVersionFieldOnDelete() {
        EntityInfo entity = getEntityForDeleteQuery(false);
        OperationInfo operation = null;
        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        instance.handleVersionFieldOnDelete = false;
        String[] expResult = new String[]{"update",
            "    MyEntity ",
            "set",
            "    deleteDateMarkField = current_timestamp,",
            "    deletionMarkField = true",
            "where",
            "    deletionMarkField = false",
            "    and idField = parameterValue"};
        String[] result = instance.getEntityDeleteByIdQuery(entity, operation);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testGetEntityDeleteByIdQueryWithoutUsingLogicalDeletion() {
        EntityInfo entity = getEntityForDeleteQuery(false);
        entity.setIgnoreLogicalDeletionEnabled(true);
        OperationInfo operation = null;
        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        String[] expResult = new String[]{"delete from",
            "    MyEntity ",
            "where",
            "    deletionMarkField = false",
            "    and idField = parameterValue"};
        String[] result = instance.getEntityDeleteByIdQuery(entity, operation);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testGetEntityDeleteByIdQueryWithEntityWithEntityQueriesAnnotation() {
        EntityInfo entity = new EntityInfo(DataTypeInfo.LIST_DATA_TYPE, EntityKind.ENTITY);
        entity.addAnnotation(getEntityQueriesSample(true));
        OperationInfo operation = null;
        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        String[] expResult = new String[]{"deleteById", "query"};
        String[] result = instance.getEntityDeleteByIdQuery(entity, operation);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testGetTableName() {
        EntityInfo entity = new EntityInfo(DataTypeInfo.LIST_DATA_TYPE, EntityKind.ENTITY);
        entity.addAnnotation(new MappedName() {
            @Override
            public String[] value() {
                return new String[]{"MappedEntity"};
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return MappedName.class;
            }
        });
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        String[] expResult = new String[]{"MappedEntity"};
        String[] result = instance.getTableName(entity);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testIncludeIdOnInsertWithAutogeneratedId() {
        EntityInfo entity = null;
        FieldInfo field = new FieldInfo("myField", DataTypeInfo.LIST_DATA_TYPE);
        field.setIdentifierAutogenerated(false);
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        boolean expResult = true;
        boolean result = instance.includeIdOnInsert(entity, field);
        assertEquals(expResult, result);
    }

    @Test
    public void testIncludeIdOnInsertWithIdQueriesAnnotationField() {
        EntityInfo entity = null;
        FieldInfo field = new FieldInfo("myField", DataTypeInfo.LIST_DATA_TYPE);
        field.setIdentifierAutogenerated(true);
        field.addAnnotation(new IdQueries() {
            @Override
            public String[] selectNextValue() {
                return new String[]{};
            }

            @Override
            public String[] selectCurrentValue() {
                return new String[]{};
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return IdQueries.class;
            }
        });
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        boolean expResult = true;
        boolean result = instance.includeIdOnInsert(entity, field);
        assertEquals(expResult, result);
    }

    @Test
    public void testIncludeIdOnInsertWithIdQueriesAnnotationEntity() {
        EntityInfo entity = new EntityInfo(DataTypeInfo.LIST_DATA_TYPE, EntityKind.ENTITY);
        FieldInfo field = new FieldInfo("myField", DataTypeInfo.LIST_DATA_TYPE);
        field.setIdentifierAutogenerated(true);
        entity.addAnnotation(new IdQueries() {
            @Override
            public String[] selectNextValue() {
                return new String[]{};
            }

            @Override
            public String[] selectCurrentValue() {
                return new String[]{};
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return IdQueries.class;
            }
        });
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        boolean expResult = true;
        boolean result = instance.includeIdOnInsert(entity, field);
        assertEquals(expResult, result);
    }

    @Test
    public void testIncludeIdOnInsertGettingUseAutoIncrementId() {
        EntityInfo entity = new EntityInfo(DataTypeInfo.LIST_DATA_TYPE, EntityKind.ENTITY);
        FieldInfo field = new FieldInfo("myField", DataTypeInfo.LIST_DATA_TYPE);
        field.setIdentifierAutogenerated(true);
        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        instance.setUseAutoIncrementId(true);
        boolean expResult = false;
        boolean result = instance.includeIdOnInsert(entity, field);
        assertEquals(expResult, result);
    }

    @Test
    public void testGetIdNextValueWithoutAutogeneratedIdentifier() {
        EntityInfo entity = null;
        FieldInfo field = new FieldInfo("myField", DataTypeInfo.LIST_DATA_TYPE);
        field.setIdentifierAutogenerated(false);
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        String[] expResult = new String[]{instance.getParameterValue(field)};
        String[] result = instance.getIdNextValue(entity, field);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testGetIdNextValueWithIdQueriesAnnotationField() {
        EntityInfo entity = null;
        FieldInfo field = new FieldInfo("myField", DataTypeInfo.LIST_DATA_TYPE);
        field.setIdentifierAutogenerated(true);
        field.addAnnotation(new IdQueries() {
            @Override
            public String[] selectNextValue() {
                return new String[]{"nextValue"};
            }

            @Override
            public String[] selectCurrentValue() {
                return new String[]{};
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return IdQueries.class;
            }
        });
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        String[] expResult = new String[]{"nextValue"};
        String[] result = instance.getIdNextValue(entity, field);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testGetIdNextValueWithIdQueriesAnnotationEntity() {
        EntityInfo entity = new EntityInfo(DataTypeInfo.LIST_DATA_TYPE, EntityKind.ENTITY);
        FieldInfo field = new FieldInfo("myField", DataTypeInfo.LIST_DATA_TYPE);
        field.setIdentifierAutogenerated(true);
        entity.addAnnotation(new IdQueries() {
            @Override
            public String[] selectNextValue() {
                return new String[]{"nextValue"};
            }

            @Override
            public String[] selectCurrentValue() {
                return new String[]{};
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return IdQueries.class;
            }
        });
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        String[] expResult = new String[]{"nextValue"};
        String[] result = instance.getIdNextValue(entity, field);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testGetIdNextValueGettingIdSequenceNextValue() {
        EntityInfo entity = new EntityInfo(DataTypeInfo.LIST_DATA_TYPE, EntityKind.ENTITY);
        FieldInfo field = new FieldInfo("myField", DataTypeInfo.LIST_DATA_TYPE);
        field.setIdentifierAutogenerated(true);
        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        instance.idSequenceNextValue = new String[]{"idSequenceNextValue"};
        String[] expResult = new String[]{"idSequenceNextValue"};
        String[] result = instance.getIdNextValue(entity, field);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testAppendIdNextValueNull() {
        StringBuilder result = new StringBuilder();
        EntityInfo entity = new EntityInfo(DataTypeInfo.LIST_DATA_TYPE, EntityKind.ENTITY);
        FieldInfo field = new FieldInfo("myField", DataTypeInfo.LIST_DATA_TYPE);
        field.setIdentifierAutogenerated(true);
        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        instance.idSequenceNextValue = null;
        instance.appendIdNextValue(result, entity, field);
        assertEquals(result.toString(), "null");
    }

    @Test
    public void testAppendIdNextValue() {
        StringBuilder result = new StringBuilder();
        EntityInfo entity = new EntityInfo(DataTypeInfo.LIST_DATA_TYPE, EntityKind.ENTITY);
        FieldInfo field = new FieldInfo("myField", DataTypeInfo.LIST_DATA_TYPE);
        field.setIdentifierAutogenerated(true);
        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        instance.idSequenceNextValue = new String[]{"next", "value", "query"};
        instance.appendIdNextValue(result, entity, field);
        assertEquals(result.toString(), "next value query");
    }

    @Test
    public void testGetIdCurrentValueWithIdQueriesAnnotationField() {
        EntityInfo entity = new EntityInfo(DataTypeInfo.LIST_DATA_TYPE, EntityKind.ENTITY);
        FieldInfo field = new FieldInfo("myField", DataTypeInfo.LIST_DATA_TYPE);
        field.addAnnotation(new IdQueries() {
            @Override
            public String[] selectNextValue() {
                return new String[]{};
            }

            @Override
            public String[] selectCurrentValue() {
                return new String[]{"currentValue"};
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return IdQueries.class;
            }
        });
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        String[] expResult = new String[]{"currentValue"};
        String[] result = instance.getIdCurrentValue(entity, field);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testGetIdCurrentValueWithIdQueriesAnnotationEntity() {
        EntityInfo entity = new EntityInfo(DataTypeInfo.LIST_DATA_TYPE, EntityKind.ENTITY);
        FieldInfo field = new FieldInfo("myField", DataTypeInfo.LIST_DATA_TYPE);
        entity.addAnnotation(new IdQueries() {
            @Override
            public String[] selectNextValue() {
                return new String[]{};
            }

            @Override
            public String[] selectCurrentValue() {
                return new String[]{"currentValue"};
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return IdQueries.class;
            }
        });
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        String[] expResult = new String[]{"currentValue"};
        String[] result = instance.getIdCurrentValue(entity, field);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testGetIdCurrentValueGettingIdSequenceCurrentValue() {
        EntityInfo entity = new EntityInfo(DataTypeInfo.LIST_DATA_TYPE, EntityKind.ENTITY);
        FieldInfo field = new FieldInfo("myField", DataTypeInfo.LIST_DATA_TYPE);
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        String[] expResult = new String[]{"currentValue"};
        String[] result = instance.getIdCurrentValue(entity, field);
        assertArrayEquals(expResult, result);
    }
//
//    /**
//     * Test of getIdSequenceNextValue method, of class SqlQueryGenerator.
//     */
//    @Test
//    public void testGetIdSequenceNextValue() {
//        System.out.println("getIdSequenceNextValue");
//        EntityInfo entity = null;
//        FieldInfo field = null;
//        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
//        String[] expResult = null;
//        String[] result = instance.getIdSequenceNextValue(entity, field);
//        assertArrayEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
//    /**
//     * Test of getIdSequenceCurrentValue method, of class SqlQueryGenerator.
//     */
//    @Test
//    public void testGetIdSequenceCurrentValue() {
//        System.out.println("getIdSequenceCurrentValue");
//        EntityInfo entity = null;
//        FieldInfo field = null;
//        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
//        String[] expResult = null;
//        String[] result = instance.getIdSequenceCurrentValue(entity, field);
//        assertArrayEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//

    @Test
    public void testGetConditionComparatorWithNullTemplate() {
        String comparatorRule = "";
        String template = null;
        FieldInfo field = null;
        CustomSqlQuery customQuery = null;
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        String expResult = null;
        String result = instance.getConditionComparator(comparatorRule, template, field, customQuery);
        assertEquals(expResult, result);
    }

    @Test
    public void testGetConditionComparatorWithUnrealComparator() {
        String comparatorRule = "[[unreal]] = [[value]]";
        String template = "[[unreal]] = [[value]]";
        FieldInfo field = new FieldInfo("myField", new DataTypeInfo("Integer"));
        CustomSqlQuery customQuery = null;
        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        String expResult = "[[unreal]] = parameterValue";
        String result = instance.getConditionComparator(comparatorRule, template, field, customQuery);
        ArrayList<MessageContent> actualMsg = instance.getProcessingEnv().getMessager().getContent();
        ArrayList<MessageContent> expectedMsg = new ArrayList<MessageContent>();
        expectedMsg.add(new MessageContent(Diagnostic.Kind.ERROR,
                "Invalid comparator element: [[unreal]]",
                field.getElement()));
        assertArrayEquals(expectedMsg.toArray(), actualMsg.toArray());
        assertEquals(expResult, result);
    }

    @Test
    public void testGetConditionComparatorWithConditionComparator() {
        String comparatorRule = "[[column]] = [[value]]";
        String template = "[[condition]] customComparator content";
        FieldInfo field = new FieldInfo("myField", new DataTypeInfo("Integer"));
        CustomSqlQuery customQuery = null;
        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        String expResult = "myField = parameterValue customComparator content";
        String result = instance.getConditionComparator(comparatorRule, template, field, customQuery);
        assertEquals(expResult, result);
    }

    @Test
    public void testGetConditionComparatorWithConditionComparatorAndUnrealComparatorRule() {
        String comparatorRule = "[[unreal]] = [[value]]";
        String template = "[[condition]] customComparator content";
        FieldInfo field = new FieldInfo("myField", new DataTypeInfo("Integer"));
        CustomSqlQuery customQuery = null;
        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        String expResult = "[[unreal]] = parameterValue customComparator content";
        String result = instance.getConditionComparator(comparatorRule, template, field, customQuery);
        ArrayList<MessageContent> actualMsg = instance.getProcessingEnv().getMessager().getContent();
        ArrayList<MessageContent> expectedMsg = new ArrayList<MessageContent>();
        expectedMsg.add(new MessageContent(Diagnostic.Kind.ERROR,
                "Invalid comparator rule element: [[unreal]]",
                field.getElement()));
        assertArrayEquals(expectedMsg.toArray(), actualMsg.toArray());
        assertEquals(expResult, result);
    }

    @Test
    public void testGetConditionComparator() {
        String comparatorRule = "[[column]] = [[value]]";
        String template = "[[column]] = [[value]]";
        FieldInfo field = new FieldInfo("myField", new DataTypeInfo("Integer"));
        CustomSqlQuery customQuery = null;
        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        String expResult = "myField = parameterValue";
        String result = instance.getConditionComparator(comparatorRule, template, field, customQuery);
        assertEquals(expResult, result);
    }

    @Test
    public void testFinalizeQueryWithNullQueries() {
        String query = null;
        OperationInfo operation = null;
        CustomSqlQuery customQuery = null;
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        String expResult = null;
        String result = instance.finalizeQuery(query, operation, customQuery);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testFinalizeQueryWithoutRulesAndWithCustomComparator() {
        String query = "{{myField}}";
        OperationInfo operation = getOperationForWhere(false);
        FieldInfo field = new FieldInfo("myField", new DataTypeInfo("Integer"));
        field.addAnnotation(new UseCustomComparator() {

            @Override
            public String value() {
                return "custom";
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return UseCustomComparator.class;
            }
        });
        operation.addField(field);
        CustomSqlQuery customQuery = null;
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        String expResult = "custom";
        String result = instance.finalizeQuery(query, operation, customQuery);
        assertEquals(expResult, result);
    }

    @Test
    public void testFinalizeQueryWithComparators() {
        String query = "{{myField}} {{myField:condition}} {{myField:CONDITION}}\n"
                + "{{myField:=}} {{myField:==}} {{myField:equal}} {{myField:EQUAL}}\n"
                + "{{myField:!=}} {{myField:<>}} {{myField:notEqual}} {{myField:NOT_EQUAL}}\n"
                + "{{myField:=?}} {{myField:==?}} {{myField:equalNullable}} {{myField:EQUAL_NULLABLE}}\n"
                + "{{myField:smaller}} {{myField:SMALLER}}\n"
                + "{{myField:larger}} {{myField:LARGER}}\n"
                + "{{myField:smallAs}} {{myField:SMALL_AS}}\n"
                + "{{myField:largerAs}} {{myField:LARGER_AS}}\n"
                + "{{myField:in}} {{myField:IN}}\n"
                + "{{myField:notIn}} {{myField:NOT_IN}}\n"
                + "{{myField:like}} {{myField:LIKE}}\n"
                + "{{myField:ilike}} {{myField:ILIKE}} {{myField:likeInsensitive}} {{myField:LIKE_INSENSITIVE}}\n"
                + "{{myField:notIlike}} {{myField:NOT_ILIKE}} {{myField:notLikeInsensitive}} {{myField:NOT_LIKE_INSENSITIVE}}\n"
                + "{{myField:startWith}} {{myField:START_WITH}}\n"
                + "{{myField:notStartWith}} {{myField:NOT_START_WITH}}\n"
                + "{{myField:endWith}} {{myField:END_WITH}}\n"
                + "{{myField:notEndWith}} {{myField:NOT_END_WITH}}\n"
                + "{{myField:istartWith}} {{myField:ISTART_WITH}} {{myField:startWithInsensitive}} {{myField:START_WITH_INSENSITIVE}}\n"
                + "{{myField:notIstartWith}} {{myField:NOT_ISTART_WITH}} {{myField:notStartWithInsensitive}} {{myField:NOT_START_WITH_INSENSITIVE}}\n"
                + "{{myField:iendWith}} {{myField:IEND_WITH}} {{myField:endWithInsensitive}} {{myField:END_WITH_INSENSITIVE}}\n"
                + "{{myField:notIendWith}} {{myField:NOT_IEND_WITH}} {{myField:notEndWithInsensitive}} {{myField:NOT_END_WITH_INSENSITIVE}}\n"
                + "{{myField:contains}} {{myField:CONTAINS}}\n"
                + "{{myField:notContains}} {{myField:NOT_CONTAINS}}\n"
                + "{{myField:icontains}} {{myField:ICONTAINS}} {{myField:containsInsensitive}} {{myField:CONTAINS_INSENSITIVE}}\n"
                + "{{myField:notIcontains}} {{myField:NOT_ICONTAINS}} {{myField:notContainsInsensitive}} {{myField:NOT_CONTAINS_INSENSITIVE}}";
        OperationInfo operation = getOperationForWhere(false);
        FieldInfo field = new FieldInfo("myField", new DataTypeInfo("Integer"));
        field.addAnnotation(new UseComparator() {
            @Override
            public Comparator value() {
                return Comparator.SMALLER;
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return UseComparator.class;
            }
        });
        operation.addField(field);
        CustomSqlQuery customQuery = null;
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        String expResult = "myField &lt; parameterValue myField &lt; parameterValue myField &lt; parameterValue\n"
                + "myField = parameterValue myField = parameterValue myField = parameterValue myField = parameterValue\n"
                + "myField &lt;&gt; parameterValue myField &lt;&gt; parameterValue myField &lt;&gt; parameterValue myField &lt;&gt; parameterValue\n"
                + "<if test='myField != null'> myField = parameterValue </if> <if test='myField == null'> myField is null </if> <if test='myField != null'> myField = parameterValue </if> <if test='myField == null'> myField is null </if> <if test='myField != null'> myField = parameterValue </if> <if test='myField == null'> myField is null </if> <if test='myField != null'> myField = parameterValue </if> <if test='myField == null'> myField is null </if>\n"
                + "myField &lt; parameterValue myField &lt; parameterValue\n"
                + "myField &gt; parameterValue myField &gt; parameterValue\n"
                + "myField &lt;= parameterValue myField &lt;= parameterValue\n"
                + "myField &gt;= parameterValue myField &gt;= parameterValue\n"
                + "myField in <foreach collection='myField' open='(' separator=',' close=')' item='_item_myField'> #{_item_myField,jdbcType=NUMERIC} </foreach> myField in <foreach collection='myField' open='(' separator=',' close=')' item='_item_myField'> #{_item_myField,jdbcType=NUMERIC} </foreach>\n"
                + "myField not in <foreach collection='myField' open='(' separator=',' close=')' item='_item_myField'> #{_item_myField,jdbcType=NUMERIC} </foreach> myField not in <foreach collection='myField' open='(' separator=',' close=')' item='_item_myField'> #{_item_myField,jdbcType=NUMERIC} </foreach>\n"
                + "myField like parameterValue myField like parameterValue\n"
                + "lower(myField) like lower(parameterValue) lower(myField) like lower(parameterValue) lower(myField) like lower(parameterValue) lower(myField) like lower(parameterValue)\n"
                + "lower(myField) not like lower(parameterValue) lower(myField) not like lower(parameterValue) lower(myField) not like lower(parameterValue) lower(myField) not like lower(parameterValue)\n"
                + "myField like (parameterValue || '%') myField like (parameterValue || '%')\n"
                + "myField not like (parameterValue || '%') myField not like (parameterValue || '%')\n"
                + "myField like ('%' || parameterValue) myField like ('%' || parameterValue)\n"
                + "myField not like ('%' || parameterValue) myField not like ('%' || parameterValue)\n"
                + "lower(myField) like (lower(parameterValue) || '%') lower(myField) like (lower(parameterValue) || '%') lower(myField) like (lower(parameterValue) || '%') lower(myField) like (lower(parameterValue) || '%')\n"
                + "lower(myField) not like (lower(parameterValue) || '%') lower(myField) not like (lower(parameterValue) || '%') lower(myField) not like (lower(parameterValue) || '%') lower(myField) not like (lower(parameterValue) || '%')\n"
                + "lower(myField) like ('%' || lower(parameterValue)) lower(myField) like ('%' || lower(parameterValue)) lower(myField) like ('%' || lower(parameterValue)) lower(myField) like ('%' || lower(parameterValue))\n"
                + "lower(myField) not like ('%' || lower(parameterValue)) lower(myField) not like ('%' || lower(parameterValue)) lower(myField) not like ('%' || lower(parameterValue)) lower(myField) not like ('%' || lower(parameterValue))\n"
                + "myField like ('%' || parameterValue || '%') myField like ('%' || parameterValue || '%')\n"
                + "myField not like ('%' || parameterValue || '%') myField not like ('%' || parameterValue || '%')\n"
                + "lower(myField) like ('%' || lower(parameterValue) || '%') lower(myField) like ('%' || lower(parameterValue) || '%') lower(myField) like ('%' || lower(parameterValue) || '%') lower(myField) like ('%' || lower(parameterValue) || '%')\n"
                + "lower(myField) not like ('%' || lower(parameterValue) || '%') lower(myField) not like ('%' || lower(parameterValue) || '%') lower(myField) not like ('%' || lower(parameterValue) || '%') lower(myField) not like ('%' || lower(parameterValue) || '%')";
        String result = instance.finalizeQuery(query, operation, customQuery);
        assertEquals(expResult, result);
    }

    @Test
    public void testFinalizeQueryWithCustomComparator() {
        String query = "{{myField:custom:my custom comparator [[value]]}} {{myField:CUSTOM:my custom comparator [[value]]}}";
        OperationInfo operation = getOperationForWhere(false);
        FieldInfo field = new FieldInfo("myField", new DataTypeInfo("Integer"));
        operation.addField(field);
        CustomSqlQuery customQuery = null;
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        String expResult = "my custom comparator parameterValue my custom comparator parameterValue";
        String result = instance.finalizeQuery(query, operation, customQuery);
        assertEquals(expResult, result);
    }

    @Test
    public void testFinalizeQueryWithDefaultCustomComparator() {
        String query = "{{myField:custom}} {{myField:CUSTOM}}";
        OperationInfo operation = getOperationForWhere(false);
        FieldInfo field = new FieldInfo("myField", new DataTypeInfo("Integer"));
        operation.addField(field);
        CustomSqlQuery customQuery = null;
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        String expResult = "myField = parameterValue myField = parameterValue";
        String result = instance.finalizeQuery(query, operation, customQuery);
        assertEquals(expResult, result);
    }

    @Test
    public void testFinalizeQueryWithIfNullRule() {
        String query = "{{myField:ifNull:separator}}\n"
                + "{{myField:IF_NULL:separator}}";
        OperationInfo operation = getOperationForWhere(false);
        FieldInfo field = new FieldInfo("myField", new DataTypeInfo("Integer"));
        field.addAnnotation(new UseComparator() {
            @Override
            public Comparator value() {
                return Comparator.SMALLER;
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return UseComparator.class;
            }
        });
        operation.addField(field);
        CustomSqlQuery customQuery = null;
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        String expResult = "<if test='myField == null'>separator\n"
                + "<if test='myField == null'>separator";
        String result = instance.finalizeQuery(query, operation, customQuery);
        assertEquals(expResult, result);
    }

    @Test
    public void testFinalizeQueryWithIfNullRuleWithoutSeparator() {
        String query = "{{myField:ifNull}}\n"
                + "{{myField:IF_NULL}}";
        OperationInfo operation = getOperationForWhere(false);
        FieldInfo field = new FieldInfo("myField", new DataTypeInfo("Integer"));
        field.addAnnotation(new UseComparator() {
            @Override
            public Comparator value() {
                return Comparator.SMALLER;
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return UseComparator.class;
            }
        });
        operation.addField(field);
        CustomSqlQuery customQuery = null;
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        String expResult = "<if test='myField == null'>\n"
                + "<if test='myField == null'>";
        String result = instance.finalizeQuery(query, operation, customQuery);
        assertEquals(expResult, result);
    }

    @Test
    public void testFinalizeQueryWithIfNotNullRule() {
        String query = "{{myField:ifNotNull:separator}}\n"
                + "{{myField:IF_NOT_NULL:separator}}";
        OperationInfo operation = getOperationForWhere(false);
        FieldInfo field = new FieldInfo("myField", new DataTypeInfo("Integer"));
        field.addAnnotation(new UseComparator() {
            @Override
            public Comparator value() {
                return Comparator.SMALLER;
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return UseComparator.class;
            }
        });
        operation.addField(field);
        CustomSqlQuery customQuery = null;
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        String expResult = "<if test='myField != null'>separator\n"
                + "<if test='myField != null'>separator";
        String result = instance.finalizeQuery(query, operation, customQuery);
        assertEquals(expResult, result);
    }

    @Test
    public void testFinalizeQueryWithIfNotNullRuleWithoutSeparator() {
        String query = "{{myField:ifNotNull}}\n"
                + "{{myField:IF_NOT_NULL}}";
        OperationInfo operation = getOperationForWhere(false);
        FieldInfo field = new FieldInfo("myField", new DataTypeInfo("Integer"));
        field.addAnnotation(new UseComparator() {
            @Override
            public Comparator value() {
                return Comparator.SMALLER;
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return UseComparator.class;
            }
        });
        operation.addField(field);
        CustomSqlQuery customQuery = null;
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        String expResult = "<if test='myField != null'>\n"
                + "<if test='myField != null'>";
        String result = instance.finalizeQuery(query, operation, customQuery);
        assertEquals(expResult, result);
    }

    @Test
    public void testFinalizeQueryWithEndIfRule() {
        String query = "{{myField:endIf}}\n"
                + "{{myField:END_IF}}";
        OperationInfo operation = getOperationForWhere(false);
        FieldInfo field = new FieldInfo("myField", new DataTypeInfo("Integer"));
        field.addAnnotation(new UseComparator() {
            @Override
            public Comparator value() {
                return Comparator.SMALLER;
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return UseComparator.class;
            }
        });
        operation.addField(field);
        CustomSqlQuery customQuery = null;
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        String expResult = "</if>\n"
                + "</if>";
        String result = instance.finalizeQuery(query, operation, customQuery);
        assertEquals(expResult, result);
    }

    @Test
    public void testFinalizeQueryWithDefinedConditionElementValues() {
        String query = "{{myField:column}} {{myField:COLUMN}}\n"
                + "{{myField:name}} {{myField:NAME}}\n"
                + "{{myField:value}} {{myField:VALUE}}\n"
                + "{{myField:jdbcType}} {{myField:JDBC_TYPE}}\n"
                + "{{myField:typeHandler}} {{myField:TYPE_HANDLER}}";
        OperationInfo operation = getOperationForWhere(false);
        FieldInfo field = new FieldInfo("myField", new DataTypeInfo("Integer"));
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
        operation.addField(field);
        CustomSqlQuery customQuery = null;
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        String expResult = "myField myField\n"
                + "myField myField\n"
                + "parameterValue parameterValue\n"
                + ",jdbcType=NUMERIC ,jdbcType=NUMERIC\n"
                + ",typeHandler=java.lang.Integer ,typeHandler=java.lang.Integer";
        String result = instance.finalizeQuery(query, operation, customQuery);
        assertEquals(expResult, result);
    }

    @Test
    public void testFinalizeQueryWithInvalidField() {
        String query = "{{myInvalidField:column}} {{myField:COLUMN}}";
        OperationInfo operation = getOperationForWhere(false);
        FieldInfo field = new FieldInfo("myField", new DataTypeInfo("Integer"));
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
        operation.addField(field);
        CustomSqlQuery customQuery = null;
        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        String expResult = "{{myInvalidField:column}} myField";
        String result = instance.finalizeQuery(query, operation, customQuery);
        ArrayList<MessageContent> actualMsg = instance.getProcessingEnv().getMessager().getContent();
        ArrayList<MessageContent> expectedMsg = new ArrayList<MessageContent>();
        expectedMsg.add(new MessageContent(Diagnostic.Kind.ERROR,
                "Unable to find the field used in the query element: {{myInvalidField:column}}",
                operation.getElement()));
        assertArrayEquals(expectedMsg.toArray(), actualMsg.toArray());
        assertEquals(expResult, result);
    }

    @Test
    public void testFinalizeQueryWithInvalidComparator() {
        String query = "{{myField:invalidComparator}} {{myField:COLUMN}}";
        OperationInfo operation = getOperationForWhere(false);
        FieldInfo field = new FieldInfo("myField", new DataTypeInfo("Integer"));
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
        operation.addField(field);
        CustomSqlQuery customQuery = null;
        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        String expResult = "{{myField:invalidComparator}} myField";
        String result = instance.finalizeQuery(query, operation, customQuery);
        ArrayList<MessageContent> actualMsg = instance.getProcessingEnv().getMessager().getContent();
        ArrayList<MessageContent> expectedMsg = new ArrayList<MessageContent>();
        expectedMsg.add(new MessageContent(Diagnostic.Kind.ERROR,
                "Invalid comparator rule used in: {{myField:invalidComparator}}",
                operation.getElement()));
        assertArrayEquals(expectedMsg.toArray(), actualMsg.toArray());
        assertEquals(expResult, result);
    }

    @Test
    public void testFinalizeEntityQuery() {
        String query = "myQuery";
        EntityInfo entity = null;
        OperationInfo operation = null;
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        String expResult = "myQuery";
        String result = instance.finalizeEntityQuery(query, entity, operation);
        assertEquals(expResult, result);

    }

    public static class SqlQueryGeneratorImpl extends SqlQueryGenerator {

        public boolean appendSelectPageAfterWhere;
        public String[] idSequenceNextValue;
        public boolean handleVersionFieldOnInsert = false;
        public boolean handleVersionFieldOnUpdate = false;
        public boolean handleVersionFieldOnDelete = false;

        @Override
        public void appendOrderBy(StringBuilder result, ArrayList<FieldInfo> orderBys, CustomSqlQuery customQuery) {
            result.append("\norder by");// Always add order by to verify method invocation, even if it is not required
            for (FieldInfo fieldInfo : orderBys) {
                result.append(" ").append(fieldInfo.getName());
            }
        }

        @Override
        public void appendOrderByForSelectPage(StringBuilder result, ArrayList<FieldInfo> orderBys, CustomSqlQuery customQuery) {
            appendOrderBy(result, orderBys, customQuery);
        }

        @Override
        public void appendOrderByAfterSelectForSelectPage(StringBuilder result, ArrayList<FieldInfo> orderBys, CustomSqlQuery customQuery) {
            result.append("appendOrderByAfterSelectForSelectPage");
        }

        @Override
        public void appendStartWhere(StringBuilder result) {
            result.append("<where>");
        }

        @Override
        public void appendEndWhere(StringBuilder result, String separator) {
            result.append(separator);
            result.append("</where>");
        }

        @Override
        public void appendStartSet(StringBuilder result) {
            result.append("<set>");
        }

        @Override
        public void appendEndSet(StringBuilder result, String separator) {
            result.append(separator);
            result.append("</set>");
        }

        @Override
        public void appendStartSetValueIfNotNull(StringBuilder result, FieldInfo field) {
            appendConditionStartIfNotNull(result, field, "");
            result.append(getColumnName(field));
            result.append(" = ");
            result.append(getParameterValue(field));
        }

        @Override
        public void appendEndSetValueIfNotNull(StringBuilder result, boolean requireComma) {
            if (requireComma) {
                result.append(",");
            }
            appendConditionEndIf(result);
        }

        @Override
        public void appendConditionStartIfNull(StringBuilder result, FieldInfo field, String separator) {
            result.append("<if test='");
            result.append(field.getName());
            result.append(" == null'>");
            result.append(separator);
        }

        @Override
        public void appendConditionStartIfNotNull(StringBuilder result, FieldInfo field, String separator) {
            result.append("<if test='");
            result.append(field.getName());
            result.append(" != null'>");
            result.append(separator);
        }

        @Override
        public void appendConditionEndIf(StringBuilder result) {
            result.append("</if>");
        }

        @Override
        public String translateComparator(Comparator comparator) {
            if (comparator == null) {
                return null;
            }
            switch (comparator) {
                case EQUAL:
                    return "[[column]] = [[value]]";
                case NOT_EQUAL:
                    return "[[column]] &lt;&gt; [[value]]";
                case EQUAL_NULLABLE:
                    return "<if test='[[name]] != null'> [[column]] = [[value]] </if> <if test='[[name]] == null'> [[column]] is null </if>";
                case EQUAL_NOT_NULLABLE:
                    return "<if test='[[name]] != null'> [[column]] = [[value]] </if> <if test='[[name]] == null'> [[column]] is not null </if>";
                case NOT_EQUAL_NULLABLE:
                    return "<if test='[[name]] != null'> [[column]] &lt;&gt; [[value]] </if> <if test='[[name]] == null'> [[column]] is null </if>";
                case NOT_EQUAL_NOT_NULLABLE:
                    return "<if test='[[name]] != null'> [[column]] &lt;&gt; [[value]] </if> <if test='[[name]] == null'> [[column]] is not null </if>";
                case SMALLER:
                    return "[[column]] &lt; [[value]]";
                case LARGER:
                    return "[[column]] &gt; [[value]]";
                case SMALL_AS:
                    return "[[column]] &lt;= [[value]]";
                case LARGER_AS:
                    return "[[column]] &gt;= [[value]]";
                case IN:
                    return "[[column]] in <foreach collection='[[name]]' open='(' separator=',' close=')' item='_item_[[name]]'> #{_item_[[name]][[jdbcType]][[typeHandler]]} </foreach>";
                case NOT_IN:
                    return "[[column]] not in <foreach collection='[[name]]' open='(' separator=',' close=')' item='_item_[[name]]'> #{_item_[[name]][[jdbcType]][[typeHandler]]} </foreach>";
                case LIKE:
                    return "[[column]] like [[value]]";
                case NOT_LIKE:
                    return "[[column]] not like [[value]]";
                case LIKE_INSENSITIVE:
                    return "lower([[column]]) like lower([[value]])";
                case NOT_LIKE_INSENSITIVE:
                    return "lower([[column]]) not like lower([[value]])";
                case START_WITH:
                    return "[[column]] like ([[value]] || '%')";
                case NOT_START_WITH:
                    return "[[column]] not like ([[value]] || '%')";
                case END_WITH:
                    return "[[column]] like ('%' || [[value]])";
                case NOT_END_WITH:
                    return "[[column]] not like ('%' || [[value]])";
                case START_WITH_INSENSITIVE:
                    return "lower([[column]]) like (lower([[value]]) || '%')";
                case NOT_START_WITH_INSENSITIVE:
                    return "lower([[column]]) not like (lower([[value]]) || '%')";
                case END_WITH_INSENSITIVE:
                    return "lower([[column]]) like ('%' || lower([[value]]))";
                case NOT_END_WITH_INSENSITIVE:
                    return "lower([[column]]) not like ('%' || lower([[value]]))";
                case CONTAINS:
                    return "[[column]] like ('%' || [[value]] || '%')";
                case NOT_CONTAINS:
                    return "[[column]] not like ('%' || [[value]] || '%')";
                case CONTAINS_INSENSITIVE:
                    return "lower([[column]]) like ('%' || lower([[value]]) || '%')";
                case NOT_CONTAINS_INSENSITIVE:
                    return "lower([[column]]) not like ('%' || lower([[value]]) || '%')";
                default:
                    throw new IllegalArgumentException("Unimplemented comparator " + comparator);
            }
        }

        @Override
        public String getConditionElementValue(String rule, FieldInfo field, CustomSqlQuery customQuery) {
            if (rule == null) {
                return null;
            } else if ("column".equals(rule) || "COLUMN".equals(rule)) {
                return getColumnNameForWhere(field, customQuery);
            } else if ("name".equals(rule) || "NAME".equals(rule)) {
                return field.getName();
            } else if ("value".equals(rule) || "VALUE".equals(rule)) {
                return getParameterValue(field);
            } else if ("jdbcType".equals(rule) || "JDBC_TYPE".equals(rule)) {
                return MyBatisUtils.getJdbcType(field);
            } else if ("typeHandler".equals(rule) || "TYPE_HANDLER".equals(rule)) {
                return MyBatisUtils.getTypeHandler(processingEnv, field);
            } else {
                return null;
            }
        }

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
        public String[] envolveInSelectPage(String[] query) {
            List<String> queryList = new ArrayList<String>(Arrays.asList(query));
            queryList.add("envolveInSelectPage");
            return queryList.toArray(query);
        }

        @Override
        public String selectPageBeforeSelect() {
            return "selectPageBeforeSelect";
        }

        @Override
        public boolean appendSelectPageAfterWhere(StringBuilder result, boolean requireAnd) {
            return appendSelectPageAfterWhere;
        }

        @Override
        public String selectPageAfterOrderBy() {
            return "selectPageAfterOrderBy";
        }

        @Override
        public String[] envolveInSelectOneRow(String[] query) {
            List<String> queryList = new ArrayList<String>(Arrays.asList(query));
            queryList.add("envolveInSelectOneRow");
            return queryList.toArray(query);
        }

        @Override
        public String selectOneRowBeforeSelect() {
            return "top 1";
        }

        @Override
        public String selectOneRowAfterWhere() {
            return "rownum = 1";
        }

        @Override
        public String selectOneRowAfterOrderBy() {
            return "selectOneRowAfterOrderBy";
        }

        @Override
        public boolean handleVersionFieldOnInsert() {
            return handleVersionFieldOnInsert;
        }

        @Override
        public boolean handleVersionFieldOnUpdate() {
            return handleVersionFieldOnUpdate;
        }

        @Override
        public boolean handleVersionFieldOnDelete() {
            return handleVersionFieldOnDelete;
        }

        @Override
        public void appendInitialVersionValue(StringBuilder result, EntityInfo entity, FieldInfo field) {
            result.append("initalVersionValue");
        }

        @Override
        public void appendNextVersionValue(StringBuilder result, EntityInfo entity, FieldInfo field) {
            result.append("nextVersion");
        }

        @Override
        public String getParameterValue(FieldInfo field) {
            return "parameterValue";
        }

        @Override
        public String[] getIdSequenceNextValue(EntityInfo entity, FieldInfo field) {
            return idSequenceNextValue;
        }

        @Override
        public String[] getIdSequenceCurrentValue(EntityInfo entity, FieldInfo field) {
            return new String[]{"currentValue"};
        }

        public SqlQueryGeneratorImpl() {
            processingEnv = new ProcessingEnviromentImpl();
        }

        @Override
        public ProcessingEnviromentImpl getProcessingEnv() {
            return (ProcessingEnviromentImpl) super.getProcessingEnv();
        }
    }
}