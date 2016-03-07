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
import java.util.HashSet;
import java.util.List;
import javax.lang.model.type.MirroredTypeException;
import javax.tools.Diagnostic;
import org.junit.Test;
import static org.junit.Assert.*;
import org.uaithne.annotations.Comparators;
import static org.uaithne.annotations.Comparators.CONTAINS;
import static org.uaithne.annotations.Comparators.CONTAINS_INSENSITIVE;
import static org.uaithne.annotations.Comparators.END_WITH;
import static org.uaithne.annotations.Comparators.END_WITH_INSENSITIVE;
import static org.uaithne.annotations.Comparators.EQUAL;
import static org.uaithne.annotations.Comparators.EQUAL_NOT_NULLABLE;
import static org.uaithne.annotations.Comparators.EQUAL_NULLABLE;
import static org.uaithne.annotations.Comparators.IN;
import static org.uaithne.annotations.Comparators.LARGER;
import static org.uaithne.annotations.Comparators.LARGER_AS;
import static org.uaithne.annotations.Comparators.LIKE;
import static org.uaithne.annotations.Comparators.LIKE_INSENSITIVE;
import static org.uaithne.annotations.Comparators.NOT_CONTAINS;
import static org.uaithne.annotations.Comparators.NOT_CONTAINS_INSENSITIVE;
import static org.uaithne.annotations.Comparators.NOT_END_WITH;
import static org.uaithne.annotations.Comparators.NOT_END_WITH_INSENSITIVE;
import static org.uaithne.annotations.Comparators.NOT_EQUAL;
import static org.uaithne.annotations.Comparators.NOT_EQUAL_NOT_NULLABLE;
import static org.uaithne.annotations.Comparators.NOT_EQUAL_NULLABLE;
import static org.uaithne.annotations.Comparators.NOT_IN;
import static org.uaithne.annotations.Comparators.NOT_LIKE;
import static org.uaithne.annotations.Comparators.NOT_LIKE_INSENSITIVE;
import static org.uaithne.annotations.Comparators.NOT_START_WITH;
import static org.uaithne.annotations.Comparators.NOT_START_WITH_INSENSITIVE;
import static org.uaithne.annotations.Comparators.SMALLER;
import static org.uaithne.annotations.Comparators.SMALL_AS;
import static org.uaithne.annotations.Comparators.START_WITH;
import static org.uaithne.annotations.Comparators.START_WITH_INSENSITIVE;
import org.uaithne.annotations.EntityQueries;
import org.uaithne.annotations.IdQueries;
import org.uaithne.annotations.MappedName;
import org.uaithne.annotations.PageQueries;
import org.uaithne.annotations.Query;
import org.uaithne.annotations.Comparator;
import org.uaithne.annotations.CustomComparator;
import org.uaithne.annotations.IdSequenceName;
import org.uaithne.annotations.myBatis.MyBatisTypeHandler;
import org.uaithne.annotations.sql.CustomSqlQuery;
import org.uaithne.annotations.sql.JdbcTypes;
import org.uaithne.generator.commons.DataTypeInfo;
import org.uaithne.generator.commons.EntityInfo;
import org.uaithne.generator.commons.EntityKind;
import org.uaithne.generator.commons.FieldInfo;
import org.uaithne.generator.commons.NamesGenerator;
import org.uaithne.generator.commons.OperationInfo;
import org.uaithne.generator.commons.OperationKind;
import org.uaithne.generator.processors.database.QueryGeneratorConfiguration;
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
        String[] expResult = new String[]{"select top 1 appendOrderByAfterSelectForSelectOneRow", "from", "    MyEntity ", "selectOneRowAfterOrderBy", "where", "field = 1", "envolveInSelectOneRow"};
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
                "Unable to automatically generate the query for this operation, enter the query manually using Query annotation or specify the related entity",
                operation.getElement()));
        assertArrayEquals(expectedMsg.toArray(), actualMsg.toArray());
        assertEquals(expResult, result);
    }

    @Test
    public void testCompleteQueryWithoutEnvolveWithNullQueriesAndSelectPageOperationWithOrderBy() {
        String query = null;
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
                "Unable to automatically generate the query for this operation, enter the query manually using Query annotation or specify the related entity",
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
                "Unable to automatically complete the query for this operation, enter the full query manually using Query annotation or specify the related entity",
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
                "Unable to automatically complete the query for this operation, enter the full query manually using Query annotation or specify the related entity",
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
                + "    fieldappendOrderByAfterSelectForSelectOneRow\n"
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
                "Unable to automatically generate the query for this operation, enter the query manually using Query annotation or specify the related entity",
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
                "Unable to automatically generate the query for this operation, enter the query manually using Query annotation or specify the related entity",
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
                + "    field = parameterValue!field\n"
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
                "Unable to automatically complete the query for this operation, enter the full query manually using Query annotation or specify the related entity",
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
                + "    field = parameterValue!field\n"
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
                "Unable to automatically complete the query for this operation, enter the full query manually using Query annotation or specify the related entity",
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
                + "    field = parameterValue!field\n"
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
                "Unable to automatically complete the query for this operation, enter the full query manually using Query annotation or specify the related entity",
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
                "Unable to automatically complete the query for this operation, enter the full query manually using Query annotation or specify the related entity",
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
                "Unable to automatically complete the query for this operation, enter the full query manually using Query annotation or specify the related entity",
                operation.getElement()));
        assertArrayEquals(expectedMsg.toArray(), actualMsg.toArray());
        assertEquals(expResult, result);
    }

    @Test
    public void testCompleteQueryNoSelect() {
        String query = null;
        OperationInfo operation = new OperationInfo(DataTypeInfo.LIST_DATA_TYPE);
        boolean count = false;
        boolean selectPage = false;
        TestCustomSqlQuery customQuery = new TestCustomSqlQuery();
        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        String expResult = ""; // the current implementation retunr "", but can be change for null
        String result = instance.completeQueryWithoutEnvolve(query, operation, count, selectPage, customQuery);
        ArrayList<MessageContent> actualMsg = instance.getProcessingEnv().getMessager().getContent();
        ArrayList<MessageContent> expectedMsg = new ArrayList<MessageContent>();
        expectedMsg.add(new MessageContent(Diagnostic.Kind.ERROR,
                "Unable to automatically complete the query for this operation, enter the full query manually using Query annotation or specify the related entity",
                operation.getElement()));
        assertArrayEquals(expectedMsg.toArray(), actualMsg.toArray());
        assertEquals(expResult, result);
    }

    @Test
    public void testCompleteQueryNoFrom() {
        String query = null;
        OperationInfo operation = new OperationInfo(DataTypeInfo.LIST_DATA_TYPE);
        boolean count = false;
        boolean selectPage = false;
        TestCustomSqlQuery customQuery = new TestCustomSqlQuery();
        customQuery.select = new String[]{"select"};
        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        String expResult = ""; // the current implementation retunr "", but can be change for null
        String result = instance.completeQueryWithoutEnvolve(query, operation, count, selectPage, customQuery);
        ArrayList<MessageContent> actualMsg = instance.getProcessingEnv().getMessager().getContent();
        ArrayList<MessageContent> expectedMsg = new ArrayList<MessageContent>();
        expectedMsg.add(new MessageContent(Diagnostic.Kind.ERROR,
                "Unable to automatically complete the query for this operation, enter the full query manually using Query annotation or specify the related entity",
                operation.getElement()));
        assertArrayEquals(expectedMsg.toArray(), actualMsg.toArray());
        assertEquals(expResult, result);
    }

    @Test
    public void testCompleteQueryNoEntityOrderByMissing() {
        String query = null;
        OperationInfo operation = new OperationInfo(DataTypeInfo.LIST_DATA_TYPE);
        FieldInfo field = new FieldInfo("myField", DataTypeInfo.INT_DATA_TYPE);
        field.setOrderBy(true);
        operation.addField(field);
        boolean count = false;
        boolean selectPage = false;
        TestCustomSqlQuery customQuery = new TestCustomSqlQuery();
        customQuery.select = new String[]{"select"};
        customQuery.from = new String[]{"from"};
        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        String expResult = ""; // the current implementation retunr "", but can be change for null
        String result = instance.completeQueryWithoutEnvolve(query, operation, count, selectPage, customQuery);
        ArrayList<MessageContent> actualMsg = instance.getProcessingEnv().getMessager().getContent();
        ArrayList<MessageContent> expectedMsg = new ArrayList<MessageContent>();
        expectedMsg.add(new MessageContent(Diagnostic.Kind.ERROR,
                "Unable to automatically complete the query for this operation, enter the full query manually using Query annotation or specify the related entity",
                operation.getElement()));
        assertArrayEquals(expectedMsg.toArray(), actualMsg.toArray());
        assertEquals(expResult, result);
    }

    @Test
    public void testCompleteQueryNoEntityOrdered() {
        String query = null;
        OperationInfo operation = new OperationInfo(DataTypeInfo.LIST_DATA_TYPE);
        FieldInfo field = new FieldInfo("myField", DataTypeInfo.INT_DATA_TYPE);
        operation.addField(field);
        boolean count = false;
        boolean selectPage = false;
        TestCustomSqlQuery customQuery = new TestCustomSqlQuery();
        customQuery.select = new String[]{"select"};
        customQuery.from = new String[]{"from"};
        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        String expResult = "select\n" +
            "    select \n" +
            "from\n" +
            "    from \n" +
            "where\n" +
            "    myField = parameterValue!myField\n" +
            "order by";
        String result = instance.completeQueryWithoutEnvolve(query, operation, count, selectPage, customQuery);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testCompleteQueryWithoutEnvolveLimitToOneResultAndOrderBy2() {
        OperationInfo operation = new OperationInfo(DataTypeInfo.LIST_DATA_TYPE);
        operation.setLimitToOneResult(true);
        EntityInfo entity = getEntityForSelect();
        FieldInfo orderBy = new FieldInfo("field", DataTypeInfo.LIST_DATA_TYPE);
        orderBy.setOrderBy(true);
        operation.addField(orderBy);
        operation.setEntity(entity);
        boolean count = false;
        boolean selectPage = false;
        CustomSqlQuery customQuery = null;
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        String result = instance.completeQueryWithoutEnvolve(null, operation, count, selectPage, customQuery);
        assertEquals(result, "select top 1 \n"
                + "    mappedName as \"mappedField\",\n"
                + "    fieldappendOrderByAfterSelectForSelectOneRow\n"
                + "from\n"
                + "    MyEntity \n"
                + "where\n"
                + "    rownum = 1\n"
                + "order by field\n"
                + "selectOneRowAfterOrderBy");
    }

    @Test
    public void testAppendSelectCount() {
        StringBuilder result = new StringBuilder();
        OperationInfo operation = new OperationInfo(DataTypeInfo.LIST_DATA_TYPE);
        EntityInfo entity = null;
        boolean count = true;
        CustomSqlQuery customQuery = null;
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        instance.appendSelect(result, operation, entity, count, customQuery, null);
        assertEquals("select\n"
                + "    count(*)", result.toString());
    }
    
    @Test
    public void testAppendSelectDistinctCount() {
        StringBuilder result = new StringBuilder();
        OperationInfo operation = new OperationInfo(DataTypeInfo.LIST_DATA_TYPE);
        operation.setDistinct(true);
        EntityInfo entity = getEntityForSelect();
        boolean count = true;
        CustomSqlQuery customQuery = null;
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        instance.appendSelect(result, operation, entity, count, customQuery, null);
        assertEquals("select distinct\n" +
            "    mappedName as \"mappedField\",\n" +
            "    field", result.toString());
    }

    @Test
    public void testAppendSelectCountWithCustomSqlQuery() {
        StringBuilder result = new StringBuilder();
        OperationInfo operation = new OperationInfo(DataTypeInfo.LIST_DATA_TYPE);
        TestCustomSqlQuery customQuery = getCustomSqlQuery();
        operation.addAnnotation(customQuery);
        EntityInfo entity = null;
        boolean count = true;
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        instance.appendSelect(result, operation, entity, count, customQuery, false, null);
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
        instance.appendSelect(result, operation, entity, count, customQuery, null);
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
        instance.appendSelect(result, operation, entity, count, customQuery, null);
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
        instance.appendSelect(result, operation, entity, count, customQuery, null);
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
        instance.appendSelect(result, operation, entity, count, customQuery, null);
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
        instance.appendSelect(result, operation, entity, count, customQuery, null);
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

    @Test
    public void testAppendSelectFieldsWithExcludes() {
        StringBuilder result = new StringBuilder();
        OperationInfo operation = new OperationInfo(DataTypeInfo.INT_DATA_TYPE);
        EntityInfo entity = getEntityForSelect();
        boolean count = false;
        TestCustomSqlQuery customQuery = new TestCustomSqlQuery();
        customQuery.excludeEntityFields = new String[]{"mappedField"};
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        instance.appendSelectFields(result, operation, entity, count, customQuery);
        assertEquals("\n    field", result.toString());
    }

    @Test
    public void testAppendSelectFieldsWithExcludeFromObject() {
        StringBuilder result = new StringBuilder();
        OperationInfo operation = new OperationInfo(DataTypeInfo.INT_DATA_TYPE);
        EntityInfo entity = getEntityForSelect();
        FieldInfo field = new FieldInfo("excludedFromObject", DataTypeInfo.LIST_DATA_TYPE);
        field.setExcludedFromObject(true);
        entity.addField(field);
        boolean count = false;
        TestCustomSqlQuery customQuery = null;
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
        instance.appendWhere(query, operation, customQuery, count, null);
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
        instance.appendWhere(query, operation, customQuery, count, null);
        assertEquals(query.toString(), "\n"
                + "<where>\n"
                + "    normalField = parameterValue!normalField\n"
                + "    <if test='optionalField != null'>and optionalField in <foreach collection='optionalField' open='(' separator=',' close=')' item='_item_optionalField'> #{_item_optionalField,jdbcType=INTEGER} </foreach></if>\n"
                + "    and listField in <foreach collection='listField' open='(' separator=',' close=')' item='_item_listField'> #{_item_listField,jdbcType=INTEGER} </foreach>\n"
                + "</where>");
    }

    @Test
    public void testAppendWhereReverseRequireAnds() {
        StringBuilder query = new StringBuilder();
        OperationInfo operation = getOperationForWhere(true);
        CustomSqlQuery customQuery = null;
        boolean count = false;
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        instance.appendWhere(query, operation, customQuery, count, null);
        assertEquals(query.toString(), "\n"
                + "<where>\n"
                + "    <if test='optionalField != null'>optionalField in <foreach collection='optionalField' open='(' separator=',' close=')' item='_item_optionalField'> #{_item_optionalField,jdbcType=INTEGER} </foreach></if>\n"
                + "    and normalField = parameterValue!normalField\n"
                + "    and listField in <foreach collection='listField' open='(' separator=',' close=')' item='_item_listField'> #{_item_listField,jdbcType=INTEGER} </foreach>\n"
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
        instance.appendWhere(query, operation, customQuery, count, null);
        assertEquals(query.toString(), "\n"
                + "<where>\n"
                + "    normalField = parameterValue!normalField\n"
                + "    <if test='optionalField != null'>and optionalField in <foreach collection='optionalField' open='(' separator=',' close=')' item='_item_optionalField'> #{_item_optionalField,jdbcType=INTEGER} </foreach></if>\n"
                + "    and listField in <foreach collection='listField' open='(' separator=',' close=')' item='_item_listField'> #{_item_listField,jdbcType=INTEGER} </foreach>\n"
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
        instance.appendWhere(query, operation, customQuery, count, null);
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
        instance.appendWhere(query, operation, customQuery, count, null);
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
        instance.appendWhere(query, operation, customQuery, count, null);
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
        instance.appendWhere(query, operation, customQuery, count, null);
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
        instance.appendWhere(query, operation, customQuery, count, null);
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
        instance.appendWhere(query, operation, customQuery, count, null);
        assertEquals(query.toString(), "\n"
                + "<where>\n"
                + "    before \n"
                + "    normalField = parameterValue!normalField\n"
                + "    <if test='optionalField != null'>and optionalField in <foreach collection='optionalField' open='(' separator=',' close=')' item='_item_optionalField'> #{_item_optionalField,jdbcType=INTEGER} </foreach></if>\n"
                + "    and listField in <foreach collection='listField' open='(' separator=',' close=')' item='_item_listField'> #{_item_listField,jdbcType=INTEGER} </foreach>\n"
                + "    after \n"
                + "</where>");
    }

    private OperationInfo getOperationForWhere(boolean reverseOptional) {
        OperationInfo operation = new OperationInfo(new DataTypeInfo("MyOperation"));

        FieldInfo field1 = new FieldInfo("setValueField", new DataTypeInfo("Integer"));
        field1.setSetValueMark(true);
        FieldInfo field2 = new FieldInfo("manuallyField", new DataTypeInfo("Integer"));
        field2.setManually(true);
        FieldInfo fieldManuallyProgrammatically = new FieldInfo("manuallyProgrammaticallyField", new DataTypeInfo("Integer"));
        fieldManuallyProgrammatically.setManually(true);
        fieldManuallyProgrammatically.setManuallyProgrammatically(true);
        FieldInfo field3 = new FieldInfo("orderByField", new DataTypeInfo("Integer"));
        field3.setOrderBy(true);
        FieldInfo field4 = new FieldInfo("normalField", new DataTypeInfo("Integer"));
        FieldInfo field5 = new FieldInfo("optionalField", DataTypeInfo.LIST_DATA_TYPE.of(DataTypeInfo.BOXED_INT_DATA_TYPE));
        field5.setOptional(true);
        FieldInfo field6 = new FieldInfo("listField", DataTypeInfo.LIST_DATA_TYPE.of(DataTypeInfo.BOXED_INT_DATA_TYPE));
        operation.addField(field1);
        operation.addField(field2);
        operation.addField(fieldManuallyProgrammatically);
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
        field.addAnnotation(new Comparator() {
            @Override
            public Comparators value() {
                return Comparators.SMALLER;
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return Comparator.class;
            }
        });
        CustomSqlQuery customQuery = null;
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        instance.appendCondition(result, field, customQuery);
        assertEquals("myField &lt; parameterValue!myField", result.toString());
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
    public void testGetTableNameFromMappedEntity() {
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
        String[] result = instance.getTableName(entity, customQuery);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testGetTableName() {
        EntityInfo entity = new EntityInfo(new DataTypeInfo("MyEntity"), EntityKind.ENTITY);
        CustomSqlQuery customQuery = null;
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        String[] expResult = new String[]{"MyEntity"};
        String[] result = instance.getTableName(entity, customQuery);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testGetTableNameWithRelated() {
        EntityInfo entity = new EntityInfo(new DataTypeInfo("MyEntity"), EntityKind.ENTITY);
        EntityInfo entity2 = new EntityInfo(new DataTypeInfo("MyEntity2"), EntityKind.ENTITY);
        EntityInfo entity3 = new EntityInfo(new DataTypeInfo("MyEntity3"), EntityKind.ENTITY);
        entity2.setRelated(entity);
        entity3.setRelated(entity2);
        CustomSqlQuery customQuery = null;
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        String[] expResult = new String[]{"MyEntity"};
        String[] result = instance.getTableName(entity3, customQuery);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testGetTableNameWithCustomQuery() {
        EntityInfo entity = new EntityInfo(new DataTypeInfo("MyEntity"), EntityKind.ENTITY);
        TestCustomSqlQuery customQuery = getCustomSqlQuery();
        customQuery.tableAlias = "tableAlias";
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        String[] expResult = new String[]{"MyEntity tableAlias"};
        String[] result = instance.getTableName(entity, customQuery);
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

    @Test
    public void testGetColumnNameForWhereFromEntity() {
        FieldInfo field = new FieldInfo("myField", new DataTypeInfo("Integer"));
        field.setMappedName("mappedField");
        TestCustomSqlQuery customQuery = getCustomSqlQuery();
        customQuery.tableAlias = "tableAlias";
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        String expResult = "tableAlias.mappedField";
        String result = instance.getColumnNameForWhereFromEntity(field, customQuery);
        assertEquals(expResult, result);
    }

    @Test
    public void testGetColumnNameForWhereFromEntityWithFieldWithoutOwnMappedName() {
        FieldInfo field = new FieldInfo("myField", new DataTypeInfo("Integer"));
        TestCustomSqlQuery customQuery = getCustomSqlQuery();
        customQuery.tableAlias = "tableAlias";
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        String expResult = "tableAlias.myField";
        String result = instance.getColumnNameForWhereFromEntity(field, customQuery);
        assertEquals(expResult, result);
    }

    @Test
    public void testAppendNotDeleted() {
        StringBuilder result = new StringBuilder();
        FieldInfo field = new FieldInfo("myField", new DataTypeInfo("Boolean"));
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        instance.appendNotDeleted(result, field, null);
        assertEquals("myField = false", result.toString());
    }

    @Test
    public void testAppendNotDeletedWithOptionalField() {
        StringBuilder result = new StringBuilder();
        FieldInfo field = new FieldInfo("myField", new DataTypeInfo("Boolean"));
        field.setOptional(true);
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        instance.appendNotDeleted(result, field, null);
        assertEquals("myField is null", result.toString());
    }
    
    @Test
    public void testAppendNotDeletedWithTableAlias() {
        StringBuilder result = new StringBuilder();
        FieldInfo field = new FieldInfo("myField", new DataTypeInfo("Boolean"));
        TestCustomSqlQuery customSqlQuery = getCustomSqlQuery();
        customSqlQuery.tableAlias = "alias";
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        instance.appendNotDeleted(result, field, customSqlQuery);
        assertEquals("alias.myField = false", result.toString());
    }

    @Test
    public void testAppendNotDeletedWithOptionalFieldWithTableAlias() {
        StringBuilder result = new StringBuilder();
        FieldInfo field = new FieldInfo("myField", new DataTypeInfo("Boolean"));
        field.setOptional(true);
        TestCustomSqlQuery customSqlQuery = getCustomSqlQuery();
        customSqlQuery.tableAlias = "alias";
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        instance.appendNotDeleted(result, field, customSqlQuery);
        assertEquals("alias.myField is null", result.toString());
    }

    @Test
    public void testAppendNotDeletedWithMappedName() {
        StringBuilder result = new StringBuilder();
        FieldInfo field = new FieldInfo("myField", new DataTypeInfo("Boolean"));
        field.setMappedName("myFieldMappedName");
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        instance.appendNotDeleted(result, field, null);
        assertEquals("myFieldMappedName = false", result.toString());
    }

    @Test
    public void testAppendNotDeletedWithOptionalFieldWithMappedName() {
        StringBuilder result = new StringBuilder();
        FieldInfo field = new FieldInfo("myField", new DataTypeInfo("Boolean"));
        field.setMappedName("myFieldMappedName");
        field.setOptional(true);
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        instance.appendNotDeleted(result, field, null);
        assertEquals("myFieldMappedName is null", result.toString());
    }
    
    @Test
    public void testAppendNotDeletedWithTableAliasWithMappedName() {
        StringBuilder result = new StringBuilder();
        FieldInfo field = new FieldInfo("myField", new DataTypeInfo("Boolean"));
        field.setMappedName("myFieldMappedName");
        TestCustomSqlQuery customSqlQuery = getCustomSqlQuery();
        customSqlQuery.tableAlias = "alias";
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        instance.appendNotDeleted(result, field, customSqlQuery);
        assertEquals("alias.myFieldMappedName = false", result.toString());
    }

    @Test
    public void testAppendNotDeletedWithOptionalFieldWithTableAliasWithMappedName() {
        StringBuilder result = new StringBuilder();
        FieldInfo field = new FieldInfo("myField", new DataTypeInfo("Boolean"));
        field.setMappedName("myFieldMappedName");
        field.setOptional(true);
        TestCustomSqlQuery customSqlQuery = getCustomSqlQuery();
        customSqlQuery.tableAlias = "alias";
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        instance.appendNotDeleted(result, field, customSqlQuery);
        assertEquals("alias.myFieldMappedName is null", result.toString());
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
            "    parameterValue!idField,",
            "    parameterValue!idFieldBIS,",
            "    current_timestamp,",
            "    parameterValue!operationField,",
            "    false,",
            "    current_timestamp,",
            "    initalVersionValue!versionMarkField",
            ")"};
        String[] result = instance.getCustomInsertQuery(operation);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testGetCustomInsertQueryWithExcludes() {
        OperationInfo operation = new OperationInfo(DataTypeInfo.LIST_DATA_TYPE);
        operation.setEntity(getEntityForCustomInsertQuery());
        FieldInfo manuallyOperationField = new FieldInfo("manuallyOperationField", DataTypeInfo.LIST_DATA_TYPE);
        manuallyOperationField.setManually(true);
        operation.addField(manuallyOperationField);
        operation.addField(new FieldInfo("operationField", DataTypeInfo.LIST_DATA_TYPE));
        TestCustomSqlQuery customQuery = new TestCustomSqlQuery();
        customQuery.excludeEntityFields = new String[]{"idFieldBIS", "deletionMarkField"};
        operation.addAnnotation(customQuery);
        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        instance.handleVersionFieldOnInsert = true;
        String[] expResult = new String[]{"insert into",
            "    MyEntity ",
            "(",
            "    idField,",
            "    idFieldInsertDateMark,",
            "    operationField,",
            "    insertDateMarkField,",
            "    versionMarkField",
            ") values (",
            "    parameterValue!idField,",
            "    current_timestamp,",
            "    parameterValue!operationField,",
            "    current_timestamp,",
            "    initalVersionValue!versionMarkField",
            ")"};
        String[] result = instance.getCustomInsertQuery(operation);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testGetCustomInsertQueryWithDefaultValue() {
        OperationInfo operation = new OperationInfo(DataTypeInfo.LIST_DATA_TYPE);
        operation.setEntity(getEntityForCustomInsertQuery());
        FieldInfo manuallyOperationField = new FieldInfo("manuallyOperationField", DataTypeInfo.LIST_DATA_TYPE);
        manuallyOperationField.setManually(true);
        operation.addField(manuallyOperationField);
        operation.addField(new FieldInfo("operationField", DataTypeInfo.LIST_DATA_TYPE));
        FieldInfo defaultValue = new FieldInfo("defaultValue", DataTypeInfo.LIST_DATA_TYPE);
        defaultValue.setHasDefaultValueWhenInsert(true);
        operation.addField(defaultValue);
        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        instance.handleVersionFieldOnInsert = true;
        String[] expResult = new String[]{"insert into",
            "    MyEntity ",
            "(",
            "    idField,",
            "    idFieldBIS,",
            "    idFieldInsertDateMark,",
            "    operationField,",
            "    defaultValue,",
            "    deletionMarkField,",
            "    insertDateMarkField,",
            "    versionMarkField",
            ") values (",
            "    parameterValue!idField,",
            "    parameterValue!idFieldBIS,",
            "    current_timestamp,",
            "    parameterValue!operationField,",
            "    parameterValueOrDBDefault!defaultValue,",
            "    false,",
            "    current_timestamp,",
            "    initalVersionValue!versionMarkField",
            ")"};
        String[] result = instance.getCustomInsertQuery(operation);
        assertArrayEquals(expResult, result);
    }
    
    

    @Test
    public void testGetCustomInsertQueryWithDefaultValue2() {
        EntityInfo entity = new EntityInfo(new DataTypeInfo("MyEntity"), EntityKind.ENTITY);
        FieldInfo field0 = new FieldInfo("idField", DataTypeInfo.LIST_DATA_TYPE);
        field0.setIdentifier(true);
        entity.addField(field0);
        
        OperationInfo operation = new OperationInfo(DataTypeInfo.LIST_DATA_TYPE);
        operation.setEntity(entity);
        FieldInfo defaultValue = new FieldInfo("defaultValue", DataTypeInfo.LIST_DATA_TYPE);
        defaultValue.setHasDefaultValueWhenInsert(true);
        operation.addField(defaultValue);
        
        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();String[] expResult = new String[]{"insert into",
            "    MyEntity ",
            "(",
            "    idField,",
            "    defaultValue",
            ") values (",
            "    parameterValue!idField,",
            "    parameterValueOrDBDefault!defaultValue",
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
        instance.getConfiguration().setUseAutoIncrementId(true);
        String[] expResult = new String[]{"insert into",
            "    MyEntity ",
            "(",
            "    idField,",
            "    idFieldBIS,",
            "    idFieldInsertDateMark,",
            "    deletionMarkField,",
            "    insertDateMarkField",
            ") values (",
            "    parameterValue!idField,",
            "    parameterValue!idFieldBIS,",
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
        instance.getConfiguration().setUseAutoIncrementId(true);
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
        operation.addField(new FieldInfo("operationField", DataTypeInfo.LIST_DATA_TYPE.of(DataTypeInfo.BOXED_INT_DATA_TYPE)));
        FieldInfo setValueField1 = new FieldInfo("setValueField1", DataTypeInfo.LIST_DATA_TYPE);
        setValueField1.setSetValueMark(true);
        operation.addField(setValueField1);
        FieldInfo setValueField2 = new FieldInfo("setValueField2", DataTypeInfo.LIST_DATA_TYPE);
        setValueField2.setSetValueMark(true);
        operation.addField(setValueField2);
        FieldInfo setValueField3 = new FieldInfo("setValueField3", DataTypeInfo.LIST_DATA_TYPE);
        setValueField3.setSetValueMark(true);
        setValueField3.setIgnoreWhenNull(true);
        operation.addField(setValueField3);

        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        instance.handleVersionFieldOnUpdate = true;
        String[] expResult = new String[]{"update",
            "    MyEntity ",
            "<set>",
            "    setValueField1 = parameterValue!setValueField1,",
            "    setValueField2 = parameterValue!setValueField2,",
            "    <if test='setValueField3 != null'>setValueField3 = parameterValue!setValueField3,</if>",
            "    updateDateMarkField1 = current_timestamp,",
            "    updateDateMarkField2 = current_timestamp,",
            "    versionMarkField = nextVersion!versionMarkField",
            "</set>",
            "where",
            "    operationField in <foreach collection='operationField' open='(' separator=',' close=')' item='_item_operationField'> #{_item_operationField,jdbcType=INTEGER} </foreach>"};
        String[] result = instance.getCustomUpdateQuery(operation);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testGetCustomUpdateQueryWithIgnoreWhenNull() {
        OperationInfo operation = new OperationInfo(DataTypeInfo.LIST_DATA_TYPE);
        EntityInfo entity = new EntityInfo(new DataTypeInfo("MyEntity"), EntityKind.ENTITY);
        operation.setEntity(entity);

        FieldInfo manuallyOperationField = new FieldInfo("manuallyOperationField", DataTypeInfo.LIST_DATA_TYPE);
        manuallyOperationField.setManually(true);
        operation.addField(manuallyOperationField);
        operation.addField(new FieldInfo("operationField", DataTypeInfo.LIST_DATA_TYPE.of(DataTypeInfo.BOXED_INT_DATA_TYPE)));
        FieldInfo setValueField1 = new FieldInfo("setValueField1", DataTypeInfo.LIST_DATA_TYPE);
        setValueField1.setSetValueMark(true);
        operation.addField(setValueField1);
        FieldInfo setValueField2 = new FieldInfo("setValueField2", DataTypeInfo.LIST_DATA_TYPE);
        setValueField2.setSetValueMark(true);
        setValueField2.setIgnoreWhenNull(true);
        operation.addField(setValueField2);
        FieldInfo setValueField3 = new FieldInfo("setValueField3", DataTypeInfo.LIST_DATA_TYPE);
        setValueField3.setSetValueMark(true);
        setValueField3.setIgnoreWhenNull(true);
        operation.addField(setValueField3);

        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        instance.handleVersionFieldOnUpdate = true;
        String[] expResult = new String[]{"update",
            "    MyEntity ",
            "<set>",
            "    setValueField1 = parameterValue!setValueField1,",
            "    <if test='setValueField2 != null'>setValueField2 = parameterValue!setValueField2,</if>",
            "    <if test='setValueField3 != null'>setValueField3 = parameterValue!setValueField3</if>",
            "</set>",
            "where",
            "    operationField in <foreach collection='operationField' open='(' separator=',' close=')' item='_item_operationField'> #{_item_operationField,jdbcType=INTEGER} </foreach>"};
        String[] result = instance.getCustomUpdateQuery(operation);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testGetCustomUpdateQueryWithExcludes() {
        OperationInfo operation = new OperationInfo(DataTypeInfo.LIST_DATA_TYPE);
        operation.setEntity(getEntityForCustomUpdateQuery());

        FieldInfo manuallyOperationField = new FieldInfo("manuallyOperationField", DataTypeInfo.LIST_DATA_TYPE);
        manuallyOperationField.setManually(true);
        operation.addField(manuallyOperationField);
        operation.addField(new FieldInfo("operationField", DataTypeInfo.LIST_DATA_TYPE.of(DataTypeInfo.BOXED_INT_DATA_TYPE)));
        FieldInfo setValueField1 = new FieldInfo("setValueField1", DataTypeInfo.LIST_DATA_TYPE);
        setValueField1.setSetValueMark(true);
        operation.addField(setValueField1);
        FieldInfo setValueField2 = new FieldInfo("setValueField2", DataTypeInfo.LIST_DATA_TYPE);
        setValueField2.setSetValueMark(true);
        operation.addField(setValueField2);
        FieldInfo setValueField3 = new FieldInfo("setValueField3", DataTypeInfo.LIST_DATA_TYPE);
        setValueField3.setSetValueMark(true);
        setValueField3.setIgnoreWhenNull(true);
        operation.addField(setValueField3);
        
        TestCustomSqlQuery customQuery = new TestCustomSqlQuery();
        customQuery.excludeEntityFields = new String[]{"updateDateMarkField1", "versionMarkField"};
        operation.addAnnotation(customQuery);

        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        instance.handleVersionFieldOnUpdate = true;
        String[] expResult = new String[]{"update",
            "    MyEntity ",
            "<set>",
            "    setValueField1 = parameterValue!setValueField1,",
            "    setValueField2 = parameterValue!setValueField2,",
            "    <if test='setValueField3 != null'>setValueField3 = parameterValue!setValueField3,</if>",
            "    updateDateMarkField2 = current_timestamp",
            "</set>",
            "where",
            "    operationField in <foreach collection='operationField' open='(' separator=',' close=')' item='_item_operationField'> #{_item_operationField,jdbcType=INTEGER} </foreach>"};
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
        operation.addField(new FieldInfo("operationField", DataTypeInfo.LIST_DATA_TYPE.of(DataTypeInfo.BOXED_INT_DATA_TYPE)));
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
            "    setValueField1 = parameterValue!setValueField1,",
            "    setValueField2 = parameterValue!setValueField2,",
            "    updateDateMarkField1 = current_timestamp,",
            "    updateDateMarkField2 = current_timestamp",
            "where",
            "    operationField in <foreach collection='operationField' open='(' separator=',' close=')' item='_item_operationField'> #{_item_operationField,jdbcType=INTEGER} </foreach>"};
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
            "<set>",
            "    before",
            "    insertIntoExpression ",
            "    insertInto",
            "    code ",
            "    after",
            "    insertIntoExpression ",
            "</set>"
        };
        String[] result = instance.getCustomUpdateQuery(operation);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testGetCustomUpdateQueryWithCustomSqlQueryWithoutQueryValue2() {
        OperationInfo operation = new OperationInfo(DataTypeInfo.LIST_DATA_TYPE);
        operation.setEntity(getEntityForCustomUpdateQuery());

        TestCustomSqlQuery customSqlQuery = getCustomSqlQuery();
        customSqlQuery.from = new String[]{"custom", "sql", "from"};
        customSqlQuery.updateSet = new String[]{"insertInto", "code"};
        customSqlQuery.afterUpdateSetExpression = new String[]{"after", "insertIntoExpression"};
        operation.addAnnotation(customSqlQuery);

        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        String[] expResult = new String[]{"update",
            "    custom",
            "    sql",
            "    from ",
            "<set>",
            "    insertInto",
            "    code ",
            "    after",
            "    insertIntoExpression ",
            "</set>"
        };
        String[] result = instance.getCustomUpdateQuery(operation);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testGetCustomUpdateQueryWithCustomSqlQueryWithoutQueryValue3() {
        OperationInfo operation = new OperationInfo(DataTypeInfo.LIST_DATA_TYPE);
        operation.setEntity(getEntityForCustomUpdateQuery());
        FieldInfo setValueField1 = new FieldInfo("setValueField1", DataTypeInfo.LIST_DATA_TYPE);
        setValueField1.setSetValueMark(true);
        operation.addField(setValueField1);

        TestCustomSqlQuery customSqlQuery = getCustomSqlQuery();
        customSqlQuery.from = new String[]{"custom", "sql", "from"};
        customSqlQuery.afterUpdateSetExpression = new String[]{"after", "insertIntoExpression"};
        operation.addAnnotation(customSqlQuery);

        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        String[] expResult = new String[]{"update",
            "    custom",
            "    sql",
            "    from ",
            "<set>",
            "    setValueField1 = parameterValue!setValueField1,",
            "    updateDateMarkField1 = current_timestamp,",
            "    updateDateMarkField2 = current_timestamp",
            "    after",
            "    insertIntoExpression ",
            "</set>"
        };
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
            "    deleteUserMarkOperationField = parameterValue!deleteUserMarkOperationField,",
            "    deleteUserMarkOperationField2 = parameterValue!deleteUserMarkOperationField2,",
            "    deletionMarkField = true,",
            "    deletionMarkFieldBIS = true,",
            "    deleteDateMarkField = current_timestamp,",
            "    versionMarkField = nextVersion!versionMarkField",
            "where",
            "    operationField = parameterValue!operationField",
            "    and deletionMarkField = false",
            "    and deletionMarkFieldBIS = false"};
        String[] result = instance.getCustomDeleteQuery(operation);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testGetCustomDeleteQueryUsingLogicalDeletionWithExcludes() {
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
        
        TestCustomSqlQuery customQuery = new TestCustomSqlQuery();
        customQuery.excludeEntityFields = new String[]{"versionMarkField", "deletionMarkField"};
        operation.addAnnotation(customQuery);

        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        instance.handleVersionFieldOnDelete = true;
        String[] expResult = new String[]{"update",
            "    MyEntity ",
            "set",
            "    deleteUserMarkOperationField = parameterValue!deleteUserMarkOperationField,",
            "    deleteUserMarkOperationField2 = parameterValue!deleteUserMarkOperationField2,",
            "    deletionMarkFieldBIS = true,",
            "    deleteDateMarkField = current_timestamp",
            "where",
            "    operationField = parameterValue!operationField",
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
            "    deleteUserMarkOperationField = parameterValue!deleteUserMarkOperationField,",
            "    deleteUserMarkOperationField2 = parameterValue!deleteUserMarkOperationField2,",
            "    deletionMarkField = true,",
            "    deletionMarkFieldBIS = true,",
            "    deleteDateMarkField = current_timestamp",
            "where",
            "    operationField = parameterValue!operationField",
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
            "    deleteUserMarkOperationField = parameterValue!deleteUserMarkOperationField,",
            "    deleteUserMarkOperationField2 = parameterValue!deleteUserMarkOperationField2,",
            "    deletionMarkField = true,",
            "    deletionMarkFieldBIS = true,",
            "    deleteDateMarkField = current_timestamp",
            "where",
            "    operationField = parameterValue!operationField",
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
            "    operationField = parameterValue!operationField",};
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
            "    operationField = parameterValue!operationField",};
        String[] result = instance.getCustomDeleteQuery(operation);
        assertArrayEquals(expResult, result);
    }
    
    @Test
    public void testGetSelectCountQuery() {
        OperationInfo operation = getOperationForWhere(false);
        operation.setEntity(getEntityForSelect());
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        String[] expResult = new String[]{"select",
            "    count(*)",
            "from",
            "    MyEntity ",
            "<where>",
            "    normalField = parameterValue!normalField",
            "    <if test='optionalField != null'>and optionalField in <foreach collection='optionalField' open='(' separator=',' close=')' item='_item_optionalField'> #{_item_optionalField,jdbcType=INTEGER} </foreach></if>",
            "    and listField in <foreach collection='listField' open='(' separator=',' close=')' item='_item_listField'> #{_item_listField,jdbcType=INTEGER} </foreach>",
            "</where>"
        };
        String[] result = instance.getSelectCountQuery(operation);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testGetSelectCountQueryWithQueryAnnotation() {
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
        String[] result = instance.getSelectCountQuery(operation);
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
            "    normalField = parameterValue!normalField",
            "    <if test='optionalField != null'>and optionalField in <foreach collection='optionalField' open='(' separator=',' close=')' item='_item_optionalField'> #{_item_optionalField,jdbcType=INTEGER} </foreach></if>",
            "    and listField in <foreach collection='listField' open='(' separator=',' close=')' item='_item_listField'> #{_item_listField,jdbcType=INTEGER} </foreach>",
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
            "    normalField = parameterValue!normalField",
            "    <if test='optionalField != null'>and optionalField in <foreach collection='optionalField' open='(' separator=',' close=')' item='_item_optionalField'> #{_item_optionalField,jdbcType=INTEGER} </foreach></if>",
            "    and listField in <foreach collection='listField' open='(' separator=',' close=')' item='_item_listField'> #{_item_listField,jdbcType=INTEGER} </foreach>",
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
    public void testGetSelectDistinctPageQuery() {
        OperationInfo operation = new OperationInfo(DataTypeInfo.LIST_DATA_TYPE);
        operation.setEntity(getEntityForSelect());
        operation.setDistinct(true);
        FieldInfo field1 = new FieldInfo("myField1", new DataTypeInfo("Integer"));
        FieldInfo field2 = new FieldInfo("myField2", new DataTypeInfo("Integer"));
        field2.setSetValueMark(true);
        FieldInfo field3 = new FieldInfo("myField3", new DataTypeInfo("Integer"));
        field3.setOrderBy(true);
        operation.addField(field1);
        operation.addField(field2);
        operation.addField(field3);

        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        String[] expResult = new String[]{"select distinct",
            "    mappedName as \"mappedField\",",
            "    fieldappendOrderByAfterSelectForSelectPage",
            "from",
            "    MyEntity ",
            "where",
            "    myField1 = parameterValue!myField1",
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
    public void testGetSelectDistinctPageCountQuery() {
        OperationInfo operation = new OperationInfo(DataTypeInfo.LIST_DATA_TYPE);
        operation.setEntity(getEntityForSelect());
        operation.setDistinct(true);
        FieldInfo field1 = new FieldInfo("myField1", new DataTypeInfo("Integer"));
        FieldInfo field2 = new FieldInfo("myField2", new DataTypeInfo("Integer"));
        field2.setSetValueMark(true);
        FieldInfo field3 = new FieldInfo("myField3", new DataTypeInfo("Integer"));
        field3.setOrderBy(true);
        operation.addField(field1);
        operation.addField(field2);
        operation.addField(field3);

        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        String[] expResult = new String[]{
            "select count(*) from (",
            "select distinct",
            "    mappedName as \"mappedField\",",
            "    field",
            "from",
            "    MyEntity ",
            "where",
            "    myField1 = parameterValue!myField1",
            ")"};
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
    
    private OperationInfo getEntitySelectByIdOperation(EntityInfo entity) {
        FieldInfo id = new FieldInfo("id", entity.getFirstIdField());

        DataTypeInfo selectOperationName = new DataTypeInfo("SelectEntityById");
        OperationInfo selectOperation = new OperationInfo(selectOperationName);
        selectOperation.setReturnDataType(entity.getDataType());
        selectOperation.setOperationKind(OperationKind.SELECT_BY_ID);

        DataTypeInfo selectOperationInterface = DataTypeInfo.SELECT_BY_ID_OPERATION_DATA_TYPE.of(id.getDataType(), entity.getDataType());
        selectOperation.addImplement(selectOperationInterface);

        selectOperation.addField(id);
        selectOperation.setEntity(entity);
        return selectOperation;
    }

    @Test
    public void testGetEntitySelectByIdQueryWithEntityWithoutEntityQueriesAnnotation() {
        EntityInfo entity = getEntityForSelectByIdQuery(false);
        OperationInfo operation = getEntitySelectByIdOperation(entity);
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl(false);
        String[] expResult = new String[]{"select",
            "    mappedName as \"mappedField\",",
            "    field,",
            "    deletionMarkField,",
            "    identifierField",
            "from",
            "    MyEntity ",
            "where",
            "    identifierField = parameterValue!id",
            "    and deletionMarkField = false"};
        String[] result = instance.getEntitySelectByIdQuery(entity, operation);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testGetEntitySelectByIdQueryWithEntityWithoutEntityQueriesAnnotationReverseAnds() {
        EntityInfo entity = getEntityForSelectByIdQuery(true);
        OperationInfo operation = getEntitySelectByIdOperation(entity);
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl(false);
        String[] expResult = new String[]{"select",
            "    mappedName as \"mappedField\",",
            "    field,",
            "    identifierField,",
            "    deletionMarkField",
            "from",
            "    MyEntity ",
            "where",
            "    identifierField = parameterValue!id",
            "    and deletionMarkField = false"};
        String[] result = instance.getEntitySelectByIdQuery(entity, operation);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testGetEntitySelectByIdQueryWithEntityWithoutEntityQueriesAnnotationAndEntityWithoutLogicalDeletion() {
        EntityInfo entity = getEntityForSelectByIdQuery(false);
        entity.setIgnoreLogicalDeletionEnabled(true);
        OperationInfo operation = getEntitySelectByIdOperation(entity);
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl(false);
        String[] expResult = new String[]{"select",
            "    mappedName as \"mappedField\",",
            "    field,",
            "    deletionMarkField,",
            "    identifierField",
            "from",
            "    MyEntity ",
            "where",
            "    identifierField = parameterValue!id"};
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
        FieldInfo field4 = new FieldInfo("identifierField", DataTypeInfo.BOXED_INT_DATA_TYPE);
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
        FieldInfo id = new FieldInfo("id", DataTypeInfo.INT_DATA_TYPE);
        id.setIdentifier(true);
        entity.addField(id);
        entity.addAnnotation(getEntityQueriesSample());
        OperationInfo operation = getEntitySelectByIdOperation(entity);
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        String[] expResult = new String[]{"selectById", "code"};
        String[] result = instance.getEntitySelectByIdQuery(entity, operation);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testGetEntitySelectByIdQueryWithQueryAnnotation() {
        EntityInfo entity = getEntityForSelectByIdQuery(false);
        OperationInfo operation = getEntitySelectByIdOperation(entity);
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
        String[] result = instance.getEntitySelectByIdQuery(entity, operation);
        assertArrayEquals(expResult, result);
    }
    
    private OperationInfo getEntityInsertOperation(EntityInfo entity) {
        DataTypeInfo idDataType = entity.getFirstIdField().getDataType();
        FieldInfo value = new FieldInfo("value", entity.getDataType());

        DataTypeInfo insertOperationName = new DataTypeInfo("InsertEntity");
        OperationInfo insertOperation = new OperationInfo(insertOperationName);
        insertOperation.setReturnDataType(idDataType);
        insertOperation.setOperationKind(OperationKind.INSERT);
        
        DataTypeInfo insertOperationInterface = DataTypeInfo.INSERT_VALUE_OPERATION_DATA_TYPE.of(entity.getDataType(), idDataType);
        insertOperation.addImplement(insertOperationInterface);

        insertOperation.addField(value);
        insertOperation.setEntity(entity);
        return insertOperation;
    }

    @Test
    public void testGetEntityInsertQueryHandleVersionFieldOnInsert() {
        EntityInfo entity = getEntityForInsertQuery();
        OperationInfo operation = getEntityInsertOperation(entity);
        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        instance.handleVersionFieldOnInsert = true;
        instance.getConfiguration().setUseAutoIncrementId(true);
        String[] expResult = new String[]{"insert into",
            "    MyEntity ",
            "(",
            "    mappedName,",
            "    idField,",
            "    field,",
            "    defaultValue,",
            "    deletionMarkField,",
            "    insertDateMarkField,",
            "    insertUserMarkField,",
            "    versionMarkField",
            ") values (",
            "    parameterValue!mappedField,",
            "    parameterValue!idField,",
            "    parameterValue!field,",
            "    parameterValueOrDBDefault!defaultValue,",
            "    false,",
            "    current_timestamp,",
            "    parameterValue!insertUserMarkField,",
            "    initalVersionValue!versionMarkField",
            ")"};
        String[] result = instance.getEntityInsertQuery(entity, operation);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testGetEntityInsertQueryHandleVersionFieldOnInsertWithExcludes() {
        EntityInfo entity = getEntityForInsertQuery();
        OperationInfo operation = getEntityInsertOperation(entity);
        TestCustomSqlQuery customQuery = new TestCustomSqlQuery();
        customQuery.excludeEntityFields = new String[]{"defaultValue", "deletionMarkField"};
        operation.addAnnotation(customQuery);
        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        instance.handleVersionFieldOnInsert = true;
        instance.getConfiguration().setUseAutoIncrementId(true);
        String[] expResult = new String[]{"insert into",
            "    MyEntity ",
            "(",
            "    mappedName,",
            "    idField,",
            "    field,",
            "    insertDateMarkField,",
            "    insertUserMarkField,",
            "    versionMarkField",
            ") values (",
            "    parameterValue!mappedField,",
            "    parameterValue!idField,",
            "    parameterValue!field,",
            "    current_timestamp,",
            "    parameterValue!insertUserMarkField,",
            "    initalVersionValue!versionMarkField",
            ")"};
        String[] result = instance.getEntityInsertQuery(entity, operation);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testGetEntityInsertQueryWithoutHandleVersionFieldOnInsert() {
        EntityInfo entity = getEntityForInsertQuery();
        OperationInfo operation = getEntityInsertOperation(entity);
        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        instance.handleVersionFieldOnInsert = false;
        instance.getConfiguration().setUseAutoIncrementId(true);
        String[] expResult = new String[]{"insert into",
            "    MyEntity ",
            "(",
            "    mappedName,",
            "    idField,",
            "    field,",
            "    defaultValue,",
            "    deletionMarkField,",
            "    insertDateMarkField,",
            "    insertUserMarkField",
            ") values (",
            "    parameterValue!mappedField,",
            "    parameterValue!idField,",
            "    parameterValue!field,",
            "    parameterValueOrDBDefault!defaultValue,",
            "    false,",
            "    current_timestamp,",
            "    parameterValue!insertUserMarkField",
            ")"};
        String[] result = instance.getEntityInsertQuery(entity, operation);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testGetEntityInsertWithDefaultValueAtEndAndAutogeneratedId() {
        EntityInfo entity = new EntityInfo(new DataTypeInfo("MyEntity"), EntityKind.ENTITY);

        FieldInfo field0 = new FieldInfo("idField", DataTypeInfo.LIST_DATA_TYPE);
        field0.setIdentifier(true);
        field0.setIdentifierAutogenerated(true);
        FieldInfo field3 = new FieldInfo("field", DataTypeInfo.LIST_DATA_TYPE);
        FieldInfo fieldDefaultValue = new FieldInfo("defaultValue", DataTypeInfo.LIST_DATA_TYPE);
        fieldDefaultValue.setHasDefaultValueWhenInsert(true);
        
        entity.addField(field0);
        entity.addField(field3);
        entity.addField(fieldDefaultValue);
        
        OperationInfo operation = getEntityInsertOperation(entity);
        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        instance.handleVersionFieldOnInsert = false;
        instance.getConfiguration().setUseAutoIncrementId(true);
        String[] expResult = new String[]{"insert into",
            "    MyEntity ",
            "(",
            "    field,",
            "    defaultValue",
            ") values (",
            "    parameterValue!field,",
            "    parameterValueOrDBDefault!defaultValue",
            ")"};
        String[] result = instance.getEntityInsertQuery(entity, operation);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testGetEntityInsertQueryWithCustomSqlQueryWithQueryValue() {
        EntityInfo entity = getEntityForInsertQuery();
        OperationInfo operation = getEntityInsertOperation(entity);
        TestCustomSqlQuery customSqlQuery = getCustomSqlQuery();
        customSqlQuery.query = new String[]{"custom", "sql", "query"};
        operation.addAnnotation(customSqlQuery);
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        String[] expResult = new String[]{"custom", "sql", "query"};
        String[] result = instance.getEntityInsertQuery(entity, operation);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testGetEntityInsertQueryWithCustomSqlQueryWithoutQueryValue() {
        EntityInfo entity = getEntityForInsertQuery();
        OperationInfo operation = getEntityInsertOperation(entity);

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
        instance.getConfiguration().setUseAutoIncrementId(true);
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
        String[] result = instance.getEntityInsertQuery(entity, operation);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testGetEntityInsertQueryWithQueryAnnotation() {
        EntityInfo entity = getEntityForInsertQuery();
        OperationInfo operation = getEntityInsertOperation(entity);
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
        String[] result = instance.getEntityInsertQuery(entity, operation);
        assertArrayEquals(expResult, result);
    }

    private EntityQueries getEntityQueriesSample() {
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
        FieldInfo id = new FieldInfo("id", DataTypeInfo.INT_DATA_TYPE);
        id.setIdentifier(true);
        entity.addField(id);
        entity.addAnnotation(getEntityQueriesSample());
        OperationInfo operation = getEntityInsertOperation(entity);
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
        FieldInfo fieldDefaultValue = new FieldInfo("defaultValue", DataTypeInfo.LIST_DATA_TYPE);
        fieldDefaultValue.setHasDefaultValueWhenInsert(true);
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
        entity.addField(fieldDefaultValue);
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
        String[] expResult = new String[]{"currentValue!myField"};
        String[] result = instance.getEntityLastInsertedIdQuery(entity, operation, false);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testGetEntityLastInsertedIdQueryWithEntityWithEntityQueriesAnnotation() {
        EntityInfo entity = new EntityInfo(DataTypeInfo.LIST_DATA_TYPE, EntityKind.ENTITY);
        entity.addAnnotation(getEntityQueriesSample());
        OperationInfo operation = null;
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        String[] expResult = new String[]{"last", "inserted", "value"};
        String[] result = instance.getEntityLastInsertedIdQuery(entity, operation, false);
        assertArrayEquals(expResult, result);
    }

    private EntityInfo getEntityForUpdateQuery(boolean changeOrder) {
        EntityInfo entity = new EntityInfo(new DataTypeInfo("MyEntity"), EntityKind.ENTITY);


        FieldInfo field1 = new FieldInfo("mappedField", DataTypeInfo.LIST_DATA_TYPE);
        field1.setMappedName("mappedName");
        FieldInfo field2 = new FieldInfo("manuallyField", DataTypeInfo.LIST_DATA_TYPE);
        field2.setManually(true);
        FieldInfo field3 = new FieldInfo("field", DataTypeInfo.LIST_DATA_TYPE);
        FieldInfo field4 = new FieldInfo("idField", DataTypeInfo.BOXED_INT_DATA_TYPE);
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
    
    private OperationInfo getEntityUpdateOperation(EntityInfo entity) {
        FieldInfo value = new FieldInfo("value", entity.getDataType());

        DataTypeInfo updateOperationName = new DataTypeInfo("UpdateEntity");
        OperationInfo updateOperation = new OperationInfo(updateOperationName);
        updateOperation.setReturnDataType(DataTypeInfo.AFFECTED_ROW_COUNT_DATA_TYPE);
        updateOperation.setOperationKind(OperationKind.UPDATE);
        
        DataTypeInfo updateOperationInterface = DataTypeInfo.UPDATE_VALUE_OPERATION_DATA_TYPE.of(entity.getDataType(), DataTypeInfo.AFFECTED_ROW_COUNT_DATA_TYPE);
        updateOperation.addImplement(updateOperationInterface);

        updateOperation.addField(value);
        updateOperation.setEntity(entity);
        return updateOperation;
    }

    @Test
    public void testGetEntityUpdateQuery() {
        EntityInfo entity = getEntityForUpdateQuery(false);
        OperationInfo operation = getEntityUpdateOperation(entity);
        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        instance.handleVersionFieldOnUpdate = true;
        String[] expResult = new String[]{"update",
            "    MyEntity ",
            "set",
            "    mappedName = parameterValue!mappedField,",
            "    field = parameterValue!field,",
            "    updateDateMarkField = current_timestamp,",
            "    updateUserMarkField = parameterValue!updateUserMarkField,",
            "    versionMarkField = nextVersion!versionMarkField",
            "where",
            "    idField = parameterValue!idField",
            "    and deletionMarkField = false"};
        String[] result = instance.getEntityUpdateQuery(entity, operation);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testGetEntityUpdateQueryWithExcludes() {
        EntityInfo entity = getEntityForUpdateQuery(false);
        OperationInfo operation = getEntityUpdateOperation(entity);
        TestCustomSqlQuery customQuery = new TestCustomSqlQuery();
        customQuery.excludeEntityFields = new String[]{"versionMarkField", "mappedField"};
        operation.addAnnotation(customQuery);
        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        instance.handleVersionFieldOnUpdate = true;
        String[] expResult = new String[]{"update",
            "    MyEntity ",
            "set",
            "    field = parameterValue!field,",
            "    updateDateMarkField = current_timestamp,",
            "    updateUserMarkField = parameterValue!updateUserMarkField",
            "where",
            "    idField = parameterValue!idField",
            "    and deletionMarkField = false"};
        String[] result = instance.getEntityUpdateQuery(entity, operation);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testGetEntityUpdateQueryChangingFieldsOrder() {
        EntityInfo entity = getEntityForUpdateQuery(true);
        OperationInfo operation = getEntityUpdateOperation(entity);
        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        instance.handleVersionFieldOnUpdate = true;
        String[] expResult = new String[]{"update",
            "    MyEntity ",
            "set",
            "    mappedName = parameterValue!mappedField,",
            "    field = parameterValue!field,",
            "    updateDateMarkField = current_timestamp,",
            "    updateUserMarkField = parameterValue!updateUserMarkField,",
            "    versionMarkField = nextVersion!versionMarkField",
            "where",
            "    idField = parameterValue!idField",
            "    and deletionMarkField = false"};
        String[] result = instance.getEntityUpdateQuery(entity, operation);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testGetEntityUpdateQueryWithoutHandlingVersionFieldOnUpdate() {
        EntityInfo entity = getEntityForUpdateQuery(false);
        OperationInfo operation = getEntityUpdateOperation(entity);
        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        instance.handleVersionFieldOnUpdate = false;
        String[] expResult = new String[]{"update",
            "    MyEntity ",
            "set",
            "    mappedName = parameterValue!mappedField,",
            "    field = parameterValue!field,",
            "    updateDateMarkField = current_timestamp,",
            "    updateUserMarkField = parameterValue!updateUserMarkField",
            "where",
            "    idField = parameterValue!idField",
            "    and deletionMarkField = false"};
        String[] result = instance.getEntityUpdateQuery(entity, operation);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testGetEntityUpdateQueryWithoutUsingLogicalDeletion() {
        EntityInfo entity = getEntityForUpdateQuery(false);
        entity.setIgnoreLogicalDeletionEnabled(true);
        OperationInfo operation = getEntityUpdateOperation(entity);
        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        String[] expResult = new String[]{"update",
            "    MyEntity ",
            "set",
            "    mappedName = parameterValue!mappedField,",
            "    field = parameterValue!field,",
            "    updateDateMarkField = current_timestamp,",
            "    updateUserMarkField = parameterValue!updateUserMarkField",
            "where",
            "    idField = parameterValue!idField"};
        String[] result = instance.getEntityUpdateQuery(entity, operation);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testGetEntityUpdateQueryWithEntityWithEntityQueriesAnnotation() {
        EntityInfo entity = new EntityInfo(DataTypeInfo.LIST_DATA_TYPE, EntityKind.ENTITY);
        FieldInfo field = new FieldInfo("myField", DataTypeInfo.BOXED_INT_DATA_TYPE);
        field.setIdentifier(true);
        entity.addField(field);
        entity.addAnnotation(getEntityQueriesSample());
        OperationInfo operation = getEntityUpdateOperation(entity);
        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        String[] expResult = new String[]{"update", "query"};
        String[] result = instance.getEntityUpdateQuery(entity, operation);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testGetEntityUpdateQueryWithCustomSqlQueryWithQueryValue() {
        EntityInfo entity = getEntityForUpdateQuery(false);
        OperationInfo operation = getEntityUpdateOperation(entity);
        TestCustomSqlQuery customSqlQuery = getCustomSqlQuery();
        customSqlQuery.query = new String[]{"custom", "sql", "query"};
        operation.addAnnotation(customSqlQuery);
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        String[] expResult = new String[]{"custom", "sql", "query"};
        String[] result = instance.getEntityUpdateQuery(entity, operation);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testGetEntityUpdateQueryWithCustomSqlQueryWithoutQueryValue() {
        EntityInfo entity = getEntityForUpdateQuery(false);
        OperationInfo operation = getEntityUpdateOperation(entity);

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
            "<set>",
            "    before",
            "    insertIntoExpression ",
            "    insertInto",
            "    code ",
            "    after",
            "    insertIntoExpression ",
            "</set>",
            "where",
            "    idField = parameterValue!idField",
            "    and deletionMarkField = false"
        };
        String[] result = instance.getEntityUpdateQuery(entity, operation);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testGetEntityUpdateQueryWithCustomSqlQueryWithoutQueryValue2() {
        EntityInfo entity = getEntityForUpdateQuery(false);
        OperationInfo operation = getEntityUpdateOperation(entity);

        TestCustomSqlQuery customSqlQuery = getCustomSqlQuery();
        customSqlQuery.from = new String[]{"custom", "sql", "from"};
        customSqlQuery.updateSet = new String[]{"insertInto", "code"};
        customSqlQuery.afterUpdateSetExpression = new String[]{"after", "insertIntoExpression"};
        operation.addAnnotation(customSqlQuery);

        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        String[] expResult = new String[]{"update",
            "    custom",
            "    sql",
            "    from ",
            "<set>",
            "    insertInto",
            "    code ",
            "    after",
            "    insertIntoExpression ",
            "</set>",
            "where",
            "    idField = parameterValue!idField",
            "    and deletionMarkField = false"
        };
        String[] result = instance.getEntityUpdateQuery(entity, operation);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testGetEntityUpdateQueryWithCustomSqlQueryWithoutQueryValue3() {
        EntityInfo entity = getEntityForUpdateQuery(false);
        OperationInfo operation = getEntityUpdateOperation(entity);

        TestCustomSqlQuery customSqlQuery = getCustomSqlQuery();
        customSqlQuery.from = new String[]{"custom", "sql", "from"};
        customSqlQuery.afterUpdateSetExpression = new String[]{"after", "insertIntoExpression"};
        operation.addAnnotation(customSqlQuery);

        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        String[] expResult = new String[]{"update",
            "    custom",
            "    sql",
            "    from ",
            "<set>",
            "    mappedName = parameterValue!mappedField,",
            "    field = parameterValue!field,",
            "    updateDateMarkField = current_timestamp,",
            "    updateUserMarkField = parameterValue!updateUserMarkField",
            "    after",
            "    insertIntoExpression ",
            "</set>",
            "where",
            "    idField = parameterValue!idField",
            "    and deletionMarkField = false"
        };
        String[] result = instance.getEntityUpdateQuery(entity, operation);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testGetEntityUpdateQueryWithQueryAnnotation() {
        EntityInfo entity = getEntityForUpdateQuery(false);
        OperationInfo operation = getEntityUpdateOperation(entity);
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
        FieldInfo field4 = new FieldInfo("idField", DataTypeInfo.BOXED_INT_DATA_TYPE);
        field4.setIdentifier(true);
        FieldInfo fieldWithValueWhenNull = new FieldInfo("fieldWithValueWhenNull", DataTypeInfo.BOXED_INT_DATA_TYPE);
        fieldWithValueWhenNull.setValueWhenNull("WithValueWhenNullValue");
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
            entity.addField(fieldWithValueWhenNull);
        } else {
            entity.addField(field4);
            entity.addField(fieldWithValueWhenNull);
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
    
    private OperationInfo getEntityMergeOperation(EntityInfo entity) {
        FieldInfo value = new FieldInfo("value", entity.getDataType());

        DataTypeInfo mergeOperationName = new DataTypeInfo("MergeEntity");
        OperationInfo mergeOperation = new OperationInfo(mergeOperationName);
        mergeOperation.setReturnDataType(DataTypeInfo.AFFECTED_ROW_COUNT_DATA_TYPE);
        mergeOperation.setOperationKind(OperationKind.MERGE);
        
        DataTypeInfo mergeOperationInterface = DataTypeInfo.MERGE_VALUE_OPERATION_DATA_TYPE.of(entity.getDataType(), DataTypeInfo.AFFECTED_ROW_COUNT_DATA_TYPE);
        mergeOperation.addImplement(mergeOperationInterface);

        mergeOperation.addField(value);
        mergeOperation.setEntity(entity);
        return mergeOperation;
    }

    @Test
    public void testGetEntityMergeQuery() {
        EntityInfo entity = getEntityForMergeQuery(false);
        OperationInfo operation = getEntityMergeOperation(entity);
        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        instance.handleVersionFieldOnUpdate = true;
        String[] expResult = new String[]{"update",
            "    MyEntity ",
            "<set>",
            "    <if test='mappedField != null'>mappedName = parameterValue!mappedField,</if>",
            "    <if test='field != null'>field = parameterValue!field,</if>",
            "    fieldWithValueWhenNull = parameterValueAndWhenNull!fieldWithValueWhenNull!WithValueWhenNullValue,",
            "    updateDateMarkField = current_timestamp,",
            "    updateDateMarkFieldBIS = current_timestamp,",
            "    <if test='updateUserMarkField != null'>updateUserMarkField = parameterValue!updateUserMarkField,</if>",
            "    <if test='updateUserMarkFieldBIS != null'>updateUserMarkFieldBIS = parameterValue!updateUserMarkFieldBIS,</if>",
            "    versionMarkField = nextVersion!versionMarkField,",
            "    versionMarkFieldBIS = nextVersion!versionMarkFieldBIS,",
            "    <if test='normalField != null'>normalField = parameterValue!normalField</if>",
            "</set>",
            "where",
            "    idField = parameterValue!idField",
            "    and deletionMarkField = false"};
        String[] result = instance.getEntityMergeQuery(entity, operation);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testGetEntityMergeQueryWithExcludes() {
        EntityInfo entity = getEntityForMergeQuery(false);
        OperationInfo operation = getEntityMergeOperation(entity);
        TestCustomSqlQuery customQuery = new TestCustomSqlQuery();
        customQuery.excludeEntityFields = new String[]{"updateUserMarkField", "field"};
        operation.addAnnotation(customQuery);
        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        instance.handleVersionFieldOnUpdate = true;
        String[] expResult = new String[]{"update",
            "    MyEntity ",
            "<set>",
            "    <if test='mappedField != null'>mappedName = parameterValue!mappedField,</if>",
            "    fieldWithValueWhenNull = parameterValueAndWhenNull!fieldWithValueWhenNull!WithValueWhenNullValue,",
            "    updateDateMarkField = current_timestamp,",
            "    updateDateMarkFieldBIS = current_timestamp,",
            "    <if test='updateUserMarkFieldBIS != null'>updateUserMarkFieldBIS = parameterValue!updateUserMarkFieldBIS,</if>",
            "    versionMarkField = nextVersion!versionMarkField,",
            "    versionMarkFieldBIS = nextVersion!versionMarkFieldBIS,",
            "    <if test='normalField != null'>normalField = parameterValue!normalField</if>",
            "</set>",
            "where",
            "    idField = parameterValue!idField",
            "    and deletionMarkField = false"};
        String[] result = instance.getEntityMergeQuery(entity, operation);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testGetEntityMergeQueryWithValueWhenNull() {
        EntityInfo entity = getEntityForMergeQuery(false);
        FieldInfo fieldWithValueWhenNull = new FieldInfo("fieldWithValueWhenNull2", DataTypeInfo.BOXED_INT_DATA_TYPE);
        fieldWithValueWhenNull.setValueWhenNull("WithValueWhenNullValue2");
        entity.addField(fieldWithValueWhenNull);
        FieldInfo fieldUserWithValueWhenNull = new FieldInfo("updateUserMarkFieldWithValueWhenNull3", DataTypeInfo.BOXED_INT_DATA_TYPE);
        fieldUserWithValueWhenNull.setValueWhenNull("WithValueWhenNullValue3");
        fieldUserWithValueWhenNull.setUpdateUserMark(true);
        entity.addField(fieldUserWithValueWhenNull);
        OperationInfo operation = getEntityMergeOperation(entity);
        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        instance.handleVersionFieldOnUpdate = true;
        String[] expResult = new String[]{"update",
            "    MyEntity ",
            "<set>",
            "    <if test='mappedField != null'>mappedName = parameterValue!mappedField,</if>",
            "    <if test='field != null'>field = parameterValue!field,</if>",
            "    fieldWithValueWhenNull = parameterValueAndWhenNull!fieldWithValueWhenNull!WithValueWhenNullValue,",
            "    updateDateMarkField = current_timestamp,",
            "    updateDateMarkFieldBIS = current_timestamp,",
            "    <if test='updateUserMarkField != null'>updateUserMarkField = parameterValue!updateUserMarkField,</if>",
            "    <if test='updateUserMarkFieldBIS != null'>updateUserMarkFieldBIS = parameterValue!updateUserMarkFieldBIS,</if>",
            "    versionMarkField = nextVersion!versionMarkField,",
            "    versionMarkFieldBIS = nextVersion!versionMarkFieldBIS,",
            "    <if test='normalField != null'>normalField = parameterValue!normalField,</if>",
            "    fieldWithValueWhenNull2 = parameterValueAndWhenNull!fieldWithValueWhenNull2!WithValueWhenNullValue2,",
            "    updateUserMarkFieldWithValueWhenNull3 = parameterValueAndWhenNull!updateUserMarkFieldWithValueWhenNull3!WithValueWhenNullValue3",
            "</set>",
            "where",
            "    idField = parameterValue!idField",
            "    and deletionMarkField = false"};
        String[] result = instance.getEntityMergeQuery(entity, operation);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testGetEntityMergeQueryWithUpdateDateAtEnd() {
        EntityInfo entity = getEntityForMergeQuery(false);
        FieldInfo fieldUpdate = new FieldInfo("fieldUpdate", new DataTypeInfo("java.util", "Date"));
        fieldUpdate.setUpdateDateMark(true);
        entity.addField(fieldUpdate);
        OperationInfo operation = getEntityMergeOperation(entity);
        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        instance.handleVersionFieldOnUpdate = true;
        String[] expResult = new String[]{"update",
            "    MyEntity ",
            "<set>",
            "    <if test='mappedField != null'>mappedName = parameterValue!mappedField,</if>",
            "    <if test='field != null'>field = parameterValue!field,</if>",
            "    fieldWithValueWhenNull = parameterValueAndWhenNull!fieldWithValueWhenNull!WithValueWhenNullValue,",
            "    updateDateMarkField = current_timestamp,",
            "    updateDateMarkFieldBIS = current_timestamp,",
            "    <if test='updateUserMarkField != null'>updateUserMarkField = parameterValue!updateUserMarkField,</if>",
            "    <if test='updateUserMarkFieldBIS != null'>updateUserMarkFieldBIS = parameterValue!updateUserMarkFieldBIS,</if>",
            "    versionMarkField = nextVersion!versionMarkField,",
            "    versionMarkFieldBIS = nextVersion!versionMarkFieldBIS,",
            "    <if test='normalField != null'>normalField = parameterValue!normalField,</if>",
            "    fieldUpdate = current_timestamp",
            "</set>",
            "where",
            "    idField = parameterValue!idField",
            "    and deletionMarkField = false"};
        String[] result = instance.getEntityMergeQuery(entity, operation);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testGetEntityMergeQueryChangingOrder() {
        EntityInfo entity = getEntityForMergeQuery(true);
        OperationInfo operation = getEntityMergeOperation(entity);
        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        instance.handleVersionFieldOnUpdate = true;
        String[] expResult = new String[]{"update",
            "    MyEntity ",
            "<set>",
            "    <if test='mappedField != null'>mappedName = parameterValue!mappedField,</if>",
            "    <if test='field != null'>field = parameterValue!field,</if>",
            "    fieldWithValueWhenNull = parameterValueAndWhenNull!fieldWithValueWhenNull!WithValueWhenNullValue,",
            "    updateDateMarkField = current_timestamp,",
            "    updateDateMarkFieldBIS = current_timestamp,",
            "    <if test='updateUserMarkField != null'>updateUserMarkField = parameterValue!updateUserMarkField,</if>",
            "    <if test='updateUserMarkFieldBIS != null'>updateUserMarkFieldBIS = parameterValue!updateUserMarkFieldBIS,</if>",
            "    versionMarkField = nextVersion!versionMarkField,",
            "    versionMarkFieldBIS = nextVersion!versionMarkFieldBIS,",
            "    <if test='normalField != null'>normalField = parameterValue!normalField</if>",
            "</set>",
            "where",
            "    idField = parameterValue!idField",
            "    and deletionMarkField = false"};
        String[] result = instance.getEntityMergeQuery(entity, operation);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testGetEntityMergeQueryWithoutHandlingVersionFieldOnUpdate() {
        EntityInfo entity = getEntityForMergeQuery(false);
        OperationInfo operation = getEntityMergeOperation(entity);
        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        instance.handleVersionFieldOnUpdate = false;
        String[] expResult = new String[]{"update",
            "    MyEntity ",
            "<set>",
            "    <if test='mappedField != null'>mappedName = parameterValue!mappedField,</if>",
            "    <if test='field != null'>field = parameterValue!field,</if>",
            "    fieldWithValueWhenNull = parameterValueAndWhenNull!fieldWithValueWhenNull!WithValueWhenNullValue,",
            "    updateDateMarkField = current_timestamp,",
            "    updateDateMarkFieldBIS = current_timestamp,",
            "    <if test='updateUserMarkField != null'>updateUserMarkField = parameterValue!updateUserMarkField,</if>",
            "    <if test='updateUserMarkFieldBIS != null'>updateUserMarkFieldBIS = parameterValue!updateUserMarkFieldBIS,</if>",
            "    <if test='normalField != null'>normalField = parameterValue!normalField</if>",
            "</set>",
            "where",
            "    idField = parameterValue!idField",
            "    and deletionMarkField = false"};
        String[] result = instance.getEntityMergeQuery(entity, operation);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testGetEntityMergeQueryWithoutUsingLogicalDeletion() {
        EntityInfo entity = getEntityForMergeQuery(false);
        entity.setIgnoreLogicalDeletionEnabled(true);
        OperationInfo operation = getEntityMergeOperation(entity);
        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        String[] expResult = new String[]{"update",
            "    MyEntity ",
            "<set>",
            "    <if test='mappedField != null'>mappedName = parameterValue!mappedField,</if>",
            "    <if test='field != null'>field = parameterValue!field,</if>",
            "    fieldWithValueWhenNull = parameterValueAndWhenNull!fieldWithValueWhenNull!WithValueWhenNullValue,",
            "    updateDateMarkField = current_timestamp,",
            "    updateDateMarkFieldBIS = current_timestamp,",
            "    <if test='updateUserMarkField != null'>updateUserMarkField = parameterValue!updateUserMarkField,</if>",
            "    <if test='updateUserMarkFieldBIS != null'>updateUserMarkFieldBIS = parameterValue!updateUserMarkFieldBIS,</if>",
            "    <if test='normalField != null'>normalField = parameterValue!normalField</if>",
            "</set>",
            "where",
            "    idField = parameterValue!idField"};
        String[] result = instance.getEntityMergeQuery(entity, operation);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testGetEntityMergeQueryWithEntityWithEntityQueriesAnnotation() {
        EntityInfo entity = new EntityInfo(DataTypeInfo.LIST_DATA_TYPE, EntityKind.ENTITY);
        FieldInfo field = new FieldInfo("myField", DataTypeInfo.BOXED_INT_DATA_TYPE);
        field.setIdentifier(true);
        entity.addField(field);
        entity.addAnnotation(getEntityQueriesSample());
        OperationInfo operation = getEntityMergeOperation(entity);
        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        String[] expResult = new String[]{"merge", "query"};
        String[] result = instance.getEntityMergeQuery(entity, operation);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testGetEntityMergeQueryWithNotNullableFlied() {
        EntityInfo entity = getEntityForMergeQuery(false);
        FieldInfo notNullableField = new FieldInfo("notNullable", DataTypeInfo.INT_DATA_TYPE);
        entity.addField(notNullableField);
        OperationInfo operation = getEntityMergeOperation(entity);
        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        instance.handleVersionFieldOnUpdate = true;
        String[] expResult = new String[]{"update",
            "    MyEntity ",
            "<set>",
            "    <if test='mappedField != null'>mappedName = parameterValue!mappedField,</if>",
            "    <if test='field != null'>field = parameterValue!field,</if>",
            "    fieldWithValueWhenNull = parameterValueAndWhenNull!fieldWithValueWhenNull!WithValueWhenNullValue,",
            "    updateDateMarkField = current_timestamp,",
            "    updateDateMarkFieldBIS = current_timestamp,",
            "    <if test='updateUserMarkField != null'>updateUserMarkField = parameterValue!updateUserMarkField,</if>",
            "    <if test='updateUserMarkFieldBIS != null'>updateUserMarkFieldBIS = parameterValue!updateUserMarkFieldBIS,</if>",
            "    versionMarkField = nextVersion!versionMarkField,",
            "    versionMarkFieldBIS = nextVersion!versionMarkFieldBIS,",
            "    <if test='normalField != null'>normalField = parameterValue!normalField,</if>",
            "    <if test='notNullable != null'>notNullable = parameterValue!notNullable</if>",
            "</set>",
            "where",
            "    idField = parameterValue!idField",
            "    and deletionMarkField = false"};
        String[] result = instance.getEntityMergeQuery(entity, operation);

        ArrayList<MessageContent> actualMsg = instance.getProcessingEnv().getMessager().getContent();
        ArrayList<MessageContent> expectedMsg = new ArrayList<MessageContent>();
        expectedMsg.add(new MessageContent(Diagnostic.Kind.ERROR,
                "Fields of a entity used in a merge operation must allow use must allow null values, but a primitive data types do not allow it; you must use Integer instead of int in the field notNullable",
                notNullableField.getElement()));
        assertArrayEquals(expectedMsg.toArray(), actualMsg.toArray());
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testGetEntityMergeQueryWithCustomSqlQueryWithQueryValue() {
        EntityInfo entity = getEntityForMergeQuery(false);
        OperationInfo operation = getEntityMergeOperation(entity);
        TestCustomSqlQuery customSqlQuery = getCustomSqlQuery();
        customSqlQuery.query = new String[]{"custom", "sql", "query"};
        operation.addAnnotation(customSqlQuery);
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        String[] expResult = new String[]{"custom", "sql", "query"};
        String[] result = instance.getEntityMergeQuery(entity, operation);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testGetEntityMergeQueryWithCustomSqlQueryWithoutQueryValue() {
        EntityInfo entity = getEntityForMergeQuery(false);
        OperationInfo operation = getEntityMergeOperation(entity);

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
            "<set>",
            "    before",
            "    insertIntoExpression ",
            "    insertInto",
            "    code ",
            "    after",
            "    insertIntoExpression ",
            "</set>",
            "where",
            "    idField = parameterValue!idField",
            "    and deletionMarkField = false"
        };
        String[] result = instance.getEntityMergeQuery(entity, operation);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testGetEntityMergeQueryWithQueryAnnotation() {
        EntityInfo entity = getEntityForMergeQuery(false);
        OperationInfo operation = getEntityMergeOperation(entity);
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
        FieldInfo field4 = new FieldInfo("idField", DataTypeInfo.BOXED_INT_DATA_TYPE);
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
    
    private OperationInfo getEntityDeleteByIdOperation(EntityInfo entity) {
        FieldInfo id = new FieldInfo("id", entity.getFirstIdField());

        DataTypeInfo deleteOperationName = new DataTypeInfo("DeleteEntityById");
        OperationInfo deleteOperation = new OperationInfo(deleteOperationName);
        deleteOperation.setReturnDataType(DataTypeInfo.AFFECTED_ROW_COUNT_DATA_TYPE);
        deleteOperation.setOperationKind(OperationKind.DELETE_BY_ID);

        DataTypeInfo deleteOperationInterface = DataTypeInfo.DELETE_BY_ID_OPERATION_DATA_TYPE.of(id.getDataType(), DataTypeInfo.AFFECTED_ROW_COUNT_DATA_TYPE);
        deleteOperation.addImplement(deleteOperationInterface);

        deleteOperation.addField(id);
        deleteOperation.setEntity(entity);
        return deleteOperation;
    }

    @Test
    public void testGetEntityDeleteByIdQuery() {
        EntityInfo entity = getEntityForDeleteQuery(false);
        OperationInfo operation = getEntityDeleteByIdOperation(entity);
        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        instance.handleVersionFieldOnDelete = true;
        String[] expResult = new String[]{"update",
            "    MyEntity ",
            "set",
            "    deleteDateMarkField = current_timestamp,",
            "    deletionMarkField = true,",
            "    versionMarkField = nextVersion!versionMarkField",
            "where",
            "    idField = parameterValue!id",
            "    and deletionMarkField = false"};
        String[] result = instance.getEntityDeleteByIdQuery(entity, operation);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testGetEntityDeleteByIdQueryWithDeleteUserWithValueWhenNull() {
        EntityInfo entity = getEntityForDeleteQuery(false);
        FieldInfo deleteUser = new FieldInfo("deleteUser", DataTypeInfo.INT_DATA_TYPE);
        deleteUser.setValueWhenNull("deleteUserValue");
        deleteUser.setDeleteUserMark(true);
        entity.addField(deleteUser);
        OperationInfo operation = getEntityDeleteByIdOperation(entity);
        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        instance.handleVersionFieldOnDelete = true;
        String[] expResult = new String[]{"update",
            "    MyEntity ",
            "set",
            "    deleteDateMarkField = current_timestamp,",
            "    deletionMarkField = true,",
            "    versionMarkField = nextVersion!versionMarkField,",
            "    deleteUser = parameterValueAndWhenNull!deleteUser!deleteUserValue",
            "where",
            "    idField = parameterValue!id",
            "    and deletionMarkField = false"};
        String[] result = instance.getEntityDeleteByIdQuery(entity, operation);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testGetEntityDeleteByIdQueryWithEcludes() {
        EntityInfo entity = getEntityForDeleteQuery(false);
        OperationInfo operation = getEntityDeleteByIdOperation(entity);
        TestCustomSqlQuery customQuery = new TestCustomSqlQuery();
        customQuery.excludeEntityFields = new String[]{"deletionMarkField", "deleteDateMarkField"};
        operation.addAnnotation(customQuery);
        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        instance.handleVersionFieldOnDelete = true;
        String[] expResult = new String[]{"update",
            "    MyEntity ",
            "set",
            "    versionMarkField = nextVersion!versionMarkField",
            "where",
            "    idField = parameterValue!id",
            "    and deletionMarkField = false"};
        String[] result = instance.getEntityDeleteByIdQuery(entity, operation);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testGetEntityDeleteByIdQueryChangingOrder() {
        EntityInfo entity = getEntityForDeleteQuery(true);
        OperationInfo operation = getEntityDeleteByIdOperation(entity);
        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        instance.handleVersionFieldOnDelete = true;
        String[] expResult = new String[]{"update",
            "    MyEntity ",
            "set",
            "    deletionMarkField = true,",
            "    deleteDateMarkField = current_timestamp,",
            "    versionMarkField = nextVersion!versionMarkField",
            "where",
            "    idField = parameterValue!id",
            "    and deletionMarkField = false"};
        String[] result = instance.getEntityDeleteByIdQuery(entity, operation);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testGetEntityDeleteByIdQueryWithoutHandlingVersionFieldOnDelete() {
        EntityInfo entity = getEntityForDeleteQuery(false);
        OperationInfo operation = getEntityDeleteByIdOperation(entity);
        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        instance.handleVersionFieldOnDelete = false;
        String[] expResult = new String[]{"update",
            "    MyEntity ",
            "set",
            "    deleteDateMarkField = current_timestamp,",
            "    deletionMarkField = true",
            "where",
            "    idField = parameterValue!id",
            "    and deletionMarkField = false"};
        String[] result = instance.getEntityDeleteByIdQuery(entity, operation);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testGetEntityDeleteByIdQueryWithoutUsingLogicalDeletion() {
        EntityInfo entity = getEntityForDeleteQuery(false);
        entity.setIgnoreLogicalDeletionEnabled(true);
        OperationInfo operation = getEntityDeleteByIdOperation(entity);
        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        String[] expResult = new String[]{"delete from",
            "    MyEntity ",
            "where",
            "    idField = parameterValue!id"};
        String[] result = instance.getEntityDeleteByIdQuery(entity, operation);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testGetEntityDeleteByIdQueryWithEntityWithEntityQueriesAnnotation() {
        EntityInfo entity = new EntityInfo(DataTypeInfo.LIST_DATA_TYPE, EntityKind.ENTITY);
        FieldInfo field = new FieldInfo("myField", DataTypeInfo.BOXED_INT_DATA_TYPE);
        field.setIdentifier(true);
        entity.addField(field);
        entity.addAnnotation(getEntityQueriesSample());
        OperationInfo operation = getEntityDeleteByIdOperation(entity);
        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        String[] expResult = new String[]{"deleteById", "query"};
        String[] result = instance.getEntityDeleteByIdQuery(entity, operation);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testGetEntityDeleteByIdQueryWithCustomSqlQueryWithQueryValue() {
        EntityInfo entity = getEntityForDeleteQuery(false);
        OperationInfo operation = getEntityDeleteByIdOperation(entity);
        TestCustomSqlQuery customSqlQuery = getCustomSqlQuery();
        customSqlQuery.query = new String[]{"custom", "sql", "query"};
        operation.addAnnotation(customSqlQuery);
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        String[] expResult = new String[]{"custom", "sql", "query"};
        String[] result = instance.getEntityDeleteByIdQuery(entity, operation);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testGetEntityDeleteByIdQueryUsingLogicalDeletionWithCustomSqlQueryWithoutQueryValue() {
        EntityInfo entity = getEntityForDeleteQuery(false);
        OperationInfo operation = getEntityDeleteByIdOperation(entity);

        TestCustomSqlQuery customSqlQuery = getCustomSqlQuery();
        customSqlQuery.from = new String[]{"custom", "sql", "from"};
        operation.addAnnotation(customSqlQuery);

        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        String[] expResult = new String[]{"update",
            "    custom",
            "    sql",
            "    from ",
            "set",
            "    deleteDateMarkField = current_timestamp,",
            "    deletionMarkField = true",
            "where",
            "    idField = parameterValue!id",
            "    and deletionMarkField = false"
        };
        String[] result = instance.getEntityDeleteByIdQuery(entity, operation);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testGetEntityDeleteByIdQueryNotUsingLogicalDeletionWithCustomSqlQueryWithFromValue() {
        EntityInfo entity = getEntityForDeleteQuery(false);
        OperationInfo operation = getEntityDeleteByIdOperation(entity);
        operation.setIgnoreLogicalDeletionEnabled(true);

        TestCustomSqlQuery customSqlQuery = getCustomSqlQuery();
        customSqlQuery.from = new String[]{"custom", "sql", "from"};
        operation.addAnnotation(customSqlQuery);

        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        String[] expResult = new String[]{"delete from",
            "    custom",
            "    sql",
            "    from ",
            "where",
            "    idField = parameterValue!id",};
        String[] result = instance.getEntityDeleteByIdQuery(entity, operation);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testGetEntityDeleteByIdQueryWithQueryAnnotation() {
        EntityInfo entity = getEntityForDeleteQuery(false);
        OperationInfo operation = getEntityDeleteByIdOperation(entity);
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
        String[] result = instance.getEntityDeleteByIdQuery(entity, operation);
        assertArrayEquals(expResult, result);
    }
    
    private EntityInfo getEntityFirstUserMark() {
        EntityInfo entity = new EntityInfo(new DataTypeInfo("MyEntity"), EntityKind.ENTITY);
        
        FieldInfo field1 = new FieldInfo("insertUserMarkField", new DataTypeInfo("java.util", "Date"));
        field1.setInsertUserMark(true);
        FieldInfo field2 = new FieldInfo("updateUserMarkField", new DataTypeInfo("java.util", "Date"));
        field2.setUpdateUserMark(true);
        FieldInfo field3 = new FieldInfo("deleteUserMarkField", new DataTypeInfo("java.util", "Date"));
        field3.setDeleteUserMark(true);
        FieldInfo field4 = new FieldInfo("field", DataTypeInfo.LIST_DATA_TYPE);
        FieldInfo field5 = new FieldInfo("idField", DataTypeInfo.BOXED_INT_DATA_TYPE);
        field5.setIdentifier(true);

        entity.addField(field1);
        entity.addField(field2);
        entity.addField(field3);
        entity.addField(field4);
        entity.addField(field5);
        return entity;
    }
    
    @Test
    public void testGetEntityInsertQueryFirstUserMark() {
        EntityInfo entity = getEntityFirstUserMark();
        OperationInfo operation = getEntityInsertOperation(entity);
        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        instance.handleVersionFieldOnUpdate = true;
        String[] expResult = new String[]{"update",
            "    MyEntity ",
            "<set>",
            "    <if test='updateUserMarkField != null'>updateUserMarkField = parameterValue!updateUserMarkField,</if>",
            "    <if test='field != null'>field = parameterValue!field</if>",
            "</set>",
            "where",
            "    idField = parameterValue!idField"
        };
        String[] result = instance.getEntityMergeQuery(entity, operation);
        assertArrayEquals(expResult, result);
    }
    
    @Test
    public void testGetEntityUpdateQueryFirstUserMark() {
        EntityInfo entity = getEntityFirstUserMark();
        OperationInfo operation = getEntityUpdateOperation(entity);
        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        instance.handleVersionFieldOnUpdate = true;
        String[] expResult = new String[]{"update",
            "    MyEntity ",
            "<set>",
            "    <if test='updateUserMarkField != null'>updateUserMarkField = parameterValue!updateUserMarkField,</if>",
            "    <if test='field != null'>field = parameterValue!field</if>",
            "</set>",
            "where",
            "    idField = parameterValue!idField"
        };
        String[] result = instance.getEntityMergeQuery(entity, operation);
        assertArrayEquals(expResult, result);
    }
    
    @Test
    public void testGetEntityMergeQueryFirstUserMark() {
        EntityInfo entity = getEntityFirstUserMark();
        OperationInfo operation = getEntityMergeOperation(entity);
        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        instance.handleVersionFieldOnUpdate = true;
        String[] expResult = new String[]{"update",
            "    MyEntity ",
            "<set>",
            "    <if test='updateUserMarkField != null'>updateUserMarkField = parameterValue!updateUserMarkField,</if>",
            "    <if test='field != null'>field = parameterValue!field</if>",
            "</set>",
            "where",
            "    idField = parameterValue!idField"
        };
        String[] result = instance.getEntityMergeQuery(entity, operation);
        assertArrayEquals(expResult, result);
    }
    
    @Test
    public void testGetEntityDeleteByIdQueryFirstUserMark() {
        EntityInfo entity = getEntityFirstUserMark();
        OperationInfo operation = getEntityDeleteByIdOperation(entity);
        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        instance.handleVersionFieldOnUpdate = true;
        String[] expResult = new String[]{"update",
            "    MyEntity ",
            "<set>",
            "    <if test='updateUserMarkField != null'>updateUserMarkField = parameterValue!updateUserMarkField,</if>",
            "    <if test='field != null'>field = parameterValue!field</if>",
            "</set>",
            "where",
            "    idField = parameterValue!idField"
        };
        String[] result = instance.getEntityMergeQuery(entity, operation);
        assertArrayEquals(expResult, result);
    }
    
    private EntityInfo getEntityFirstDateMark() {
        EntityInfo entity = new EntityInfo(new DataTypeInfo("MyEntity"), EntityKind.ENTITY);
        
        FieldInfo field1 = new FieldInfo("insertDateMarkField", new DataTypeInfo("java.util", "Date"));
        field1.setInsertDateMark(true);
        FieldInfo field2 = new FieldInfo("updateDateMarkField", new DataTypeInfo("java.util", "Date"));
        field2.setUpdateDateMark(true);
        FieldInfo field3 = new FieldInfo("deleteDateMarkField", new DataTypeInfo("java.util", "Date"));
        field3.setDeleteDateMark(true);
        FieldInfo field4 = new FieldInfo("field", DataTypeInfo.LIST_DATA_TYPE);
        FieldInfo field5 = new FieldInfo("idField", DataTypeInfo.BOXED_INT_DATA_TYPE);
        field5.setIdentifier(true);

        entity.addField(field1);
        entity.addField(field2);
        entity.addField(field3);
        entity.addField(field4);
        entity.addField(field5);
        return entity;
    }
    
    @Test
    public void testGetEntityInsertQueryFirstDateMark() {
        EntityInfo entity = getEntityFirstDateMark();
        OperationInfo operation = getEntityInsertOperation(entity);
        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        instance.handleVersionFieldOnUpdate = true;
        String[] expResult = new String[]{"update",
            "    MyEntity ",
            "<set>",
            "    updateDateMarkField = current_timestamp,",
            "    <if test='field != null'>field = parameterValue!field</if>",
            "</set>",
            "where",
            "    idField = parameterValue!idField"
        };
        String[] result = instance.getEntityMergeQuery(entity, operation);
        assertArrayEquals(expResult, result);
    }
    
    @Test
    public void testGetEntityUpdateQueryFirstDateMark() {
        EntityInfo entity = getEntityFirstDateMark();
        OperationInfo operation = getEntityUpdateOperation(entity);
        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        instance.handleVersionFieldOnUpdate = true;
        String[] expResult = new String[]{"update",
            "    MyEntity ",
            "<set>",
            "    updateDateMarkField = current_timestamp,",
            "    <if test='field != null'>field = parameterValue!field</if>",
            "</set>",
            "where",
            "    idField = parameterValue!idField"
        };
        String[] result = instance.getEntityMergeQuery(entity, operation);
        assertArrayEquals(expResult, result);
    }
    
    @Test
    public void testGetEntityMergeQueryFirstDateMark() {
        EntityInfo entity = getEntityFirstDateMark();
        OperationInfo operation = getEntityMergeOperation(entity);
        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        instance.handleVersionFieldOnUpdate = true;
        String[] expResult = new String[]{"update",
            "    MyEntity ",
            "<set>",
            "    updateDateMarkField = current_timestamp,",
            "    <if test='field != null'>field = parameterValue!field</if>",
            "</set>",
            "where",
            "    idField = parameterValue!idField"
        };
        String[] result = instance.getEntityMergeQuery(entity, operation);
        assertArrayEquals(expResult, result);
    }
    
    @Test
    public void testGetEntityDeleteByIdQueryFirstDateMark() {
        EntityInfo entity = getEntityFirstDateMark();
        OperationInfo operation = getEntityDeleteByIdOperation(entity);
        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        instance.handleVersionFieldOnUpdate = true;
        String[] expResult = new String[]{"update",
            "    MyEntity ",
            "<set>",
            "    updateDateMarkField = current_timestamp,",
            "    <if test='field != null'>field = parameterValue!field</if>",
            "</set>",
            "where",
            "    idField = parameterValue!idField"
        };
        String[] result = instance.getEntityMergeQuery(entity, operation);
        assertArrayEquals(expResult, result);
    }
    
    private EntityInfo getEntityFirstVersionMark() {
        EntityInfo entity = new EntityInfo(new DataTypeInfo("MyEntity"), EntityKind.ENTITY);
        
        FieldInfo field1 = new FieldInfo("VersionMarkField", new DataTypeInfo("java.util", "Date"));
        field1.setVersionMark(true);
        FieldInfo field2 = new FieldInfo("field", DataTypeInfo.LIST_DATA_TYPE);
        FieldInfo field3 = new FieldInfo("idField", DataTypeInfo.BOXED_INT_DATA_TYPE);
        field3.setIdentifier(true);

        entity.addField(field1);
        entity.addField(field2);
        entity.addField(field3);

        return entity;
    }
    
    @Test
    public void testGetEntityInsertQueryFirstVersionMark() {
        EntityInfo entity = getEntityFirstVersionMark();
        OperationInfo operation = getEntityInsertOperation(entity);
        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        instance.handleVersionFieldOnUpdate = true;
        String[] expResult = new String[]{"update",
            "    MyEntity ",
            "<set>",
            "    VersionMarkField = nextVersion!VersionMarkField,",
            "    <if test='field != null'>field = parameterValue!field</if>",
            "</set>",
            "where",
            "    idField = parameterValue!idField"
        };
        String[] result = instance.getEntityMergeQuery(entity, operation);
        assertArrayEquals(expResult, result);
    }
    
    @Test
    public void testGetEntityUpdateQueryFirstVersionMark() {
        EntityInfo entity = getEntityFirstVersionMark();
        OperationInfo operation = getEntityUpdateOperation(entity);
        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        instance.handleVersionFieldOnUpdate = true;
        String[] expResult = new String[]{"update",
            "    MyEntity ",
            "<set>",
            "    VersionMarkField = nextVersion!VersionMarkField,",
            "    <if test='field != null'>field = parameterValue!field</if>",
            "</set>",
            "where",
            "    idField = parameterValue!idField"
        };
        String[] result = instance.getEntityMergeQuery(entity, operation);
        assertArrayEquals(expResult, result);
    }
    
    @Test
    public void testGetEntityMergeQueryFirstVersionMark() {
        EntityInfo entity = getEntityFirstVersionMark();
        OperationInfo operation = getEntityMergeOperation(entity);
        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        instance.handleVersionFieldOnUpdate = true;
        String[] expResult = new String[]{"update",
            "    MyEntity ",
            "<set>",
            "    VersionMarkField = nextVersion!VersionMarkField,",
            "    <if test='field != null'>field = parameterValue!field</if>",
            "</set>",
            "where",
            "    idField = parameterValue!idField"
        };
        String[] result = instance.getEntityMergeQuery(entity, operation);
        assertArrayEquals(expResult, result);
    }
    
    @Test
    public void testGetEntityDeleteByIdQueryFirstVersionMark() {
        EntityInfo entity = getEntityFirstVersionMark();
        OperationInfo operation = getEntityDeleteByIdOperation(entity);
        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        instance.handleVersionFieldOnUpdate = true;
        String[] expResult = new String[]{"update",
            "    MyEntity ",
            "<set>",
            "    VersionMarkField = nextVersion!VersionMarkField,",
            "    <if test='field != null'>field = parameterValue!field</if>",
            "</set>",
            "where",
            "    idField = parameterValue!idField"
        };
        String[] result = instance.getEntityMergeQuery(entity, operation);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testGetTableNameSimple() {
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
        String[] result = instance.getTableName(entity, null);
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
        instance.getConfiguration().setUseAutoIncrementId(true);
        boolean expResult = false;
        boolean result = instance.includeIdOnInsert(entity, field);
        assertEquals(expResult, result);
    }

    @Test
    public void testIncludeIdOnInsertWithEntityIdSequenceName() {
        EntityInfo entity = new EntityInfo(new DataTypeInfo("MyEntity"), EntityKind.ENTITY);
        entity.addAnnotation(new IdSequenceName() {

            @Override
            public String value() {
                return "idSequenceName";
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return IdSequenceName.class;
            }
        });
        FieldInfo field = new FieldInfo("myField", DataTypeInfo.LIST_DATA_TYPE);
        field.setIdentifierAutogenerated(true);
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        boolean expResult = true;
        boolean result = instance.includeIdOnInsert(entity, field);
        assertEquals(expResult, result);
    }

    @Test
    public void testIncludeIdOnInsertWithFieldIdSequenceName() {
        EntityInfo entity = new EntityInfo(new DataTypeInfo("MyEntity"), EntityKind.ENTITY);
        FieldInfo field = new FieldInfo("myField", DataTypeInfo.LIST_DATA_TYPE);
        field.setIdentifierAutogenerated(true);
        field.addAnnotation(new IdSequenceName() {

            @Override
            public String value() {
                return "idSequenceName";
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return IdSequenceName.class;
            }
        });
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        boolean expResult = true;
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
        String[] result = instance.getIdCurrentValue(entity, field, false);
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
        String[] result = instance.getIdCurrentValue(entity, field, false);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testGetIdCurrentValueGettingIdSequenceCurrentValue() {
        EntityInfo entity = new EntityInfo(DataTypeInfo.LIST_DATA_TYPE, EntityKind.ENTITY);
        FieldInfo field = new FieldInfo("myField", DataTypeInfo.LIST_DATA_TYPE);
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        String[] expResult = new String[]{"currentValue!myField"};
        String[] result = instance.getIdCurrentValue(entity, field, false);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testGetIdCurrentValueNoValue() {
        EntityInfo entity = new EntityInfo(DataTypeInfo.LIST_DATA_TYPE, EntityKind.ENTITY);
        FieldInfo field = new FieldInfo("myField", DataTypeInfo.LIST_DATA_TYPE);
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        String[] expResult = null;
        String[] result = instance.getIdCurrentValue(entity, field, true);
        assertArrayEquals(expResult, result);
    }

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
        String expResult = "[[unreal]] = parameterValue!myField";
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
        String expResult = "myField = parameterValue!myField customComparator content";
        String result = instance.getConditionComparator(comparatorRule, template, field, customQuery);
        assertEquals(expResult, result);
    }

    @Test
    public void testGetConditionComparatorWithConditionComparatorAndUnrealComparatorRule() {
        String comparatorRule = "[[unreal]] = [[value]]";
        String template = "[[condition]] customComparator content [[CONDITION]]";
        FieldInfo field = new FieldInfo("myField", new DataTypeInfo("Integer"));
        CustomSqlQuery customQuery = null;
        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        String expResult = "[[unreal]] = parameterValue!myField customComparator content [[unreal]] = parameterValue!myField";
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
        String expResult = "myField = parameterValue!myField";
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
    public void testFinalizeQueryWithoutRules() {
        String query = "{{myField}}";
        OperationInfo operation = getOperationForWhere(false);
        FieldInfo field = new FieldInfo("myField", new DataTypeInfo("Integer"));
        operation.addField(field);
        CustomSqlQuery customQuery = null;
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        String expResult = "parameterValue!myField";
        String result = instance.finalizeQuery(query, operation, customQuery);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testFinalizeQueryCondition() {
        String query = "{{myField:condition}} {{myField:CONDITION}}";
        OperationInfo operation = getOperationForWhere(false);
        FieldInfo field = new FieldInfo("myField", new DataTypeInfo("Integer"));
        operation.addField(field);
        CustomSqlQuery customQuery = null;
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        String expResult = "myField = parameterValue!myField myField = parameterValue!myField";
        String result = instance.finalizeQuery(query, operation, customQuery);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testFinalizeQueryConditionWithCustomComparator() {
        String query = "{{myField:condition}} {{myField:CONDITION}}";
        OperationInfo operation = getOperationForWhere(false);
        FieldInfo field = new FieldInfo("myField", new DataTypeInfo("Integer"));
        field.addAnnotation(new CustomComparator() {

            @Override
            public String value() {
                return "custom";
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return CustomComparator.class;
            }
        });
        operation.addField(field);
        CustomSqlQuery customQuery = null;
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        String expResult = "custom custom";
        String result = instance.finalizeQuery(query, operation, customQuery);
        assertEquals(expResult, result);
    }

    @Test
    public void testFinalizeQueryWithComparators() {
        String query = "{{myField}} {{myField:condition}} {{myField:CONDITION}}\n"
                + "{{myField:=}} {{myField:==}} {{myField:equal}} {{myField:EQUAL}}\n"
                + "{{myField:!=}} {{myField:<>}} {{myField:notEqual}} {{myField:NOT_EQUAL}}\n"
                + "{{myField:i=}} {{myField:i==}} {{myField:I=}} {{myField:I==}} {{myField:iequal}} {{myField:IEQUAL}} {{myField:equalInsensitive}} {{myField:EQUAL_INSENSITIVE}}\n"
                + "{{myField:i!=}} {{myField:i<>}} {{myField:I!=}} {{myField:I<>}} {{myField:inotEqual}} {{myField:INOT_EQUAL}} {{myField:notEqualInsensitive}} {{myField:NOT_EQUAL_INSENSITIVE}}\n"
                + "{{myField:=?}} {{myField:==?}} {{myField:equalNullable}} {{myField:EQUAL_NULLABLE}}\n"
                + "{{myField:=!?}} {{myField:==!?}} {{myField:equalNotNullable}} {{myField:EQUAL_NOT_NULLABLE}}\n"
                + "{{myField:!=?}} {{myField:<>?}} {{myField:notEqualNullable}} {{myField:NOT_EQUAL_NULLABLE}}\n"
                + "{{myField:!=!?}} {{myField:<>!?}} {{myField:notEqualNotNullable}} {{myField:NOT_EQUAL_NOT_NULLABLE}}\n"
                + "{{myField:smaller}} {{myField:SMALLER}}\n"
                + "{{myField:larger}} {{myField:LARGER}}\n"
                + "{{myField:smallAs}} {{myField:SMALL_AS}}\n"
                + "{{myField:largerAs}} {{myField:LARGER_AS}}\n"
                + "{{myField:in}} {{myField:IN}}\n"
                + "{{myField:notIn}} {{myField:NOT_IN}}\n"
                + "{{myField:like}} {{myField:LIKE}}\n"
                + "{{myField:notLike}} {{myField:NOT_LIKE}}\n"
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
        FieldInfo field = new FieldInfo("myField", DataTypeInfo.BOXED_INT_DATA_TYPE);
        field.addAnnotation(new Comparator() {
            @Override
            public Comparators value() {
                return Comparators.SMALLER;
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return Comparator.class;
            }
        });
        operation.addField(field);
        CustomSqlQuery customQuery = null;
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        String expResult = "parameterValue!myField myField &lt; parameterValue!myField myField &lt; parameterValue!myField\n"
                + "myField = parameterValue!myField myField = parameterValue!myField myField = parameterValue!myField myField = parameterValue!myField\n"
                + "myField &lt;&gt; parameterValue!myField myField &lt;&gt; parameterValue!myField myField &lt;&gt; parameterValue!myField myField &lt;&gt; parameterValue!myField\n"
                + "lower(myField) = lower(parameterValue!myField) lower(myField) = lower(parameterValue!myField) lower(myField) = lower(parameterValue!myField) lower(myField) = lower(parameterValue!myField) lower(myField) = lower(parameterValue!myField) lower(myField) = lower(parameterValue!myField) lower(myField) = lower(parameterValue!myField) lower(myField) = lower(parameterValue!myField)\n"
                + "lower(myField) <> lower(parameterValue!myField) lower(myField) <> lower(parameterValue!myField) lower(myField) <> lower(parameterValue!myField) lower(myField) <> lower(parameterValue!myField) lower(myField) <> lower(parameterValue!myField) lower(myField) <> lower(parameterValue!myField) lower(myField) <> lower(parameterValue!myField) lower(myField) <> lower(parameterValue!myField)\n"
                + "<if test='myField != null'> myField = parameterValue!myField </if> <if test='myField == null'> myField is null </if> <if test='myField != null'> myField = parameterValue!myField </if> <if test='myField == null'> myField is null </if> <if test='myField != null'> myField = parameterValue!myField </if> <if test='myField == null'> myField is null </if> <if test='myField != null'> myField = parameterValue!myField </if> <if test='myField == null'> myField is null </if>\n"
                + "<if test='myField != null'> myField = parameterValue!myField </if> <if test='myField == null'> myField is not null </if> <if test='myField != null'> myField = parameterValue!myField </if> <if test='myField == null'> myField is not null </if> <if test='myField != null'> myField = parameterValue!myField </if> <if test='myField == null'> myField is not null </if> <if test='myField != null'> myField = parameterValue!myField </if> <if test='myField == null'> myField is not null </if>\n"
                + "<if test='myField != null'> myField &lt;&gt; parameterValue!myField </if> <if test='myField == null'> myField is null </if> <if test='myField != null'> myField &lt;&gt; parameterValue!myField </if> <if test='myField == null'> myField is null </if> <if test='myField != null'> myField &lt;&gt; parameterValue!myField </if> <if test='myField == null'> myField is null </if> <if test='myField != null'> myField &lt;&gt; parameterValue!myField </if> <if test='myField == null'> myField is null </if>\n"
                + "<if test='myField != null'> myField &lt;&gt; parameterValue!myField </if> <if test='myField == null'> myField is not null </if> <if test='myField != null'> myField &lt;&gt; parameterValue!myField </if> <if test='myField == null'> myField is not null </if> <if test='myField != null'> myField &lt;&gt; parameterValue!myField </if> <if test='myField == null'> myField is not null </if> <if test='myField != null'> myField &lt;&gt; parameterValue!myField </if> <if test='myField == null'> myField is not null </if>\n"
                + "myField &lt; parameterValue!myField myField &lt; parameterValue!myField\n"
                + "myField &gt; parameterValue!myField myField &gt; parameterValue!myField\n"
                + "myField &lt;= parameterValue!myField myField &lt;= parameterValue!myField\n"
                + "myField &gt;= parameterValue!myField myField &gt;= parameterValue!myField\n"
                + "myField in <foreach collection='myField' open='(' separator=',' close=')' item='_item_myField'> #{_item_myField,jdbcType=INTEGER} </foreach> myField in <foreach collection='myField' open='(' separator=',' close=')' item='_item_myField'> #{_item_myField,jdbcType=INTEGER} </foreach>\n"
                + "myField not in <foreach collection='myField' open='(' separator=',' close=')' item='_item_myField'> #{_item_myField,jdbcType=INTEGER} </foreach> myField not in <foreach collection='myField' open='(' separator=',' close=')' item='_item_myField'> #{_item_myField,jdbcType=INTEGER} </foreach>\n"
                + "myField like parameterValue!myField myField like parameterValue!myField\n"
                + "myField not like parameterValue!myField myField not like parameterValue!myField\n"
                + "lower(myField) like lower(parameterValue!myField) lower(myField) like lower(parameterValue!myField) lower(myField) like lower(parameterValue!myField) lower(myField) like lower(parameterValue!myField)\n"
                + "lower(myField) not like lower(parameterValue!myField) lower(myField) not like lower(parameterValue!myField) lower(myField) not like lower(parameterValue!myField) lower(myField) not like lower(parameterValue!myField)\n"
                + "myField like (parameterValue!myField || '%') myField like (parameterValue!myField || '%')\n"
                + "myField not like (parameterValue!myField || '%') myField not like (parameterValue!myField || '%')\n"
                + "myField like ('%' || parameterValue!myField) myField like ('%' || parameterValue!myField)\n"
                + "myField not like ('%' || parameterValue!myField) myField not like ('%' || parameterValue!myField)\n"
                + "lower(myField) like (lower(parameterValue!myField) || '%') lower(myField) like (lower(parameterValue!myField) || '%') lower(myField) like (lower(parameterValue!myField) || '%') lower(myField) like (lower(parameterValue!myField) || '%')\n"
                + "lower(myField) not like (lower(parameterValue!myField) || '%') lower(myField) not like (lower(parameterValue!myField) || '%') lower(myField) not like (lower(parameterValue!myField) || '%') lower(myField) not like (lower(parameterValue!myField) || '%')\n"
                + "lower(myField) like ('%' || lower(parameterValue!myField)) lower(myField) like ('%' || lower(parameterValue!myField)) lower(myField) like ('%' || lower(parameterValue!myField)) lower(myField) like ('%' || lower(parameterValue!myField))\n"
                + "lower(myField) not like ('%' || lower(parameterValue!myField)) lower(myField) not like ('%' || lower(parameterValue!myField)) lower(myField) not like ('%' || lower(parameterValue!myField)) lower(myField) not like ('%' || lower(parameterValue!myField))\n"
                + "myField like ('%' || parameterValue!myField || '%') myField like ('%' || parameterValue!myField || '%')\n"
                + "myField not like ('%' || parameterValue!myField || '%') myField not like ('%' || parameterValue!myField || '%')\n"
                + "lower(myField) like ('%' || lower(parameterValue!myField) || '%') lower(myField) like ('%' || lower(parameterValue!myField) || '%') lower(myField) like ('%' || lower(parameterValue!myField) || '%') lower(myField) like ('%' || lower(parameterValue!myField) || '%')\n"
                + "lower(myField) not like ('%' || lower(parameterValue!myField) || '%') lower(myField) not like ('%' || lower(parameterValue!myField) || '%') lower(myField) not like ('%' || lower(parameterValue!myField) || '%') lower(myField) not like ('%' || lower(parameterValue!myField) || '%')";
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
        String expResult = "my custom comparator parameterValue!myField my custom comparator parameterValue!myField";
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
        String expResult = "myField = parameterValue!myField myField = parameterValue!myField";
        String result = instance.finalizeQuery(query, operation, customQuery);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testFinalizeQueryWithDefaultCustomComparatorWithCustomComparator() {
        String query = "{{myField:custom}} {{myField:CUSTOM}}";
        OperationInfo operation = getOperationForWhere(false);
        FieldInfo field = new FieldInfo("myField", new DataTypeInfo("Integer"));
        field.addAnnotation(new CustomComparator() {

            @Override
            public String value() {
                return "custom";
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return CustomComparator.class;
            }
        });
        operation.addField(field);
        CustomSqlQuery customQuery = null;
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        String expResult = "myField = parameterValue!myField myField = parameterValue!myField";
        String result = instance.finalizeQuery(query, operation, customQuery);
        assertEquals(expResult, result);
    }

    @Test
    public void testFinalizeQueryWithIfNullRule() {
        String query = "{{myField:ifNull:separator}}\n"
                + "{{myField:IF_NULL:separator}}";
        OperationInfo operation = getOperationForWhere(false);
        FieldInfo field = new FieldInfo("myField", new DataTypeInfo("Integer"));
        field.addAnnotation(new Comparator() {
            @Override
            public Comparators value() {
                return Comparators.SMALLER;
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return Comparator.class;
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
        field.addAnnotation(new Comparator() {
            @Override
            public Comparators value() {
                return Comparators.SMALLER;
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return Comparator.class;
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
        field.addAnnotation(new Comparator() {
            @Override
            public Comparators value() {
                return Comparators.SMALLER;
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return Comparator.class;
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
        field.addAnnotation(new Comparator() {
            @Override
            public Comparators value() {
                return Comparators.SMALLER;
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return Comparator.class;
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
        field.addAnnotation(new Comparator() {
            @Override
            public Comparators value() {
                return Comparators.SMALLER;
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return Comparator.class;
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
        FieldInfo field = new FieldInfo("myField", DataTypeInfo.BOXED_INT_DATA_TYPE);
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
                + "parameterValue!myField parameterValue!myField\n"
                + ",jdbcType=INTEGER ,jdbcType=INTEGER\n"
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
    public void testFinalizeQueryWithManuallyProgrammaticallyField() {
        String query = "{{manuallyProgrammaticallyField:column}} {{myField:COLUMN}}";
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
        String expResult = "{{manuallyProgrammaticallyField:column}} myField";
        String result = instance.finalizeQuery(query, operation, customQuery);
        ArrayList<MessageContent> actualMsg = instance.getProcessingEnv().getMessager().getContent();
        ArrayList<MessageContent> expectedMsg = new ArrayList<MessageContent>();
        expectedMsg.add(new MessageContent(Diagnostic.Kind.ERROR,
                "The field 'manuallyProgrammaticallyField' is marked as not available in the sql, you cannot use in the query element: {{manuallyProgrammaticallyField:column}}",
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
    public void testFinalizeQueryWithApplicationParameter() {
        String query = "{{_app.myField}}";
        OperationInfo operation = getOperationForWhere(false);
        FieldInfo field = new FieldInfo("myField", new DataTypeInfo("Integer"));
        EntityInfo entity = new EntityInfo(DataTypeInfo.HASHMAP_DATA_TYPE, EntityKind.ENTITY);
        entity.addField(field);
        CustomSqlQuery customQuery = null;
        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        instance.getConfiguration().setApplicationParameter(entity);
        String expResult = "parameterValue!_app.myField";
        String result = instance.finalizeQuery(query, operation, customQuery);
        assertEquals(expResult, result);
    }

    @Test
    public void testFinalizeQueryWithApplicationParameterAndInvalidField() {
        String query = "{{_app.myInvalidField:value}} {{_app.myField:VALUE}}";
        OperationInfo operation = getOperationForWhere(false);
        FieldInfo field = new FieldInfo("myField", new DataTypeInfo("Integer"));
        EntityInfo entity = new EntityInfo(DataTypeInfo.HASHMAP_DATA_TYPE, EntityKind.ENTITY);
        entity.addField(field);
        CustomSqlQuery customQuery = null;
        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        instance.getConfiguration().setApplicationParameter(entity);
        String expResult = "{{_app.myInvalidField:value}} parameterValue!_app.myField";
        String result = instance.finalizeQuery(query, operation, customQuery);
        ArrayList<MessageContent> actualMsg = instance.getProcessingEnv().getMessager().getContent();
        ArrayList<MessageContent> expectedMsg = new ArrayList<MessageContent>();
        expectedMsg.add(new MessageContent(Diagnostic.Kind.ERROR,
                "Unable to find the field used in the query element: {{_app.myInvalidField:value}}",
                operation.getElement()));
        assertArrayEquals(expectedMsg.toArray(), actualMsg.toArray());
        assertEquals(expResult, result);
    }

    @Test
    public void testFinalizeQueryWithApplicationParameterWithoutConfig() {
        String query = "{{_app.myInvalidField}}";
        OperationInfo operation = getOperationForWhere(false);
        CustomSqlQuery customQuery = null;
        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        String expResult = "{{_app.myInvalidField}}";
        String result = instance.finalizeQuery(query, operation, customQuery);
        ArrayList<MessageContent> actualMsg = instance.getProcessingEnv().getMessager().getContent();
        ArrayList<MessageContent> expectedMsg = new ArrayList<MessageContent>();
        expectedMsg.add(new MessageContent(Diagnostic.Kind.ERROR,
                "You must configure the application parameter type in using the @UaithneConfiguration annotation fon allow use it in the query element: {{_app.myInvalidField}}",
                operation.getElement()));
        assertArrayEquals(expectedMsg.toArray(), actualMsg.toArray());
        assertEquals(expResult, result);
    }
    
    @Test
    public void testAppendTableName() {
        EntityInfo entity = new EntityInfo(new DataTypeInfo("MyEntity"), EntityKind.ENTITY);
        StringBuilder result = new StringBuilder();
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        instance.appendTableName(result, entity, null, "prefix");
        String expResult = "prefixMyEntity ";
        assertEquals(expResult, result.toString());
    }
    
    @Test
    public void testAppendTableNameWithCustomSqlQuery() {
        EntityInfo entity = new EntityInfo(new DataTypeInfo("MyEntity"), EntityKind.ENTITY);
        TestCustomSqlQuery customQuery = new TestCustomSqlQuery();
        customQuery.beforeFromExpression = new String[]{"before", "from"};
        customQuery.afterFromExpression = new String[]{"after", "from"};
        StringBuilder result = new StringBuilder();
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        instance.appendTableName(result, entity, customQuery, "prefix");
        String expResult = "prefixbefore\n" +
            "prefixfrom \n" +
            "prefixMyEntity \n" +
            "prefixafter\n" +
            "prefixfrom ";
        assertEquals(expResult, result.toString());
    }
    
    @Test
    public void testAppendTableNameWithCustomSqlQueryAndFrom() {
        EntityInfo entity = new EntityInfo(new DataTypeInfo("MyEntity"), EntityKind.ENTITY);
        TestCustomSqlQuery customQuery = new TestCustomSqlQuery();
        customQuery.beforeFromExpression = new String[]{"before", "from"};
        customQuery.afterFromExpression = new String[]{"after", "from"};
        customQuery.from = new String[]{"inline", "from"};
        StringBuilder result = new StringBuilder();
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        instance.appendTableName(result, entity, customQuery, "prefix");
        String expResult = "prefixbefore\n" +
            "prefixfrom \n" +
            "prefixinline\n" +
            "prefixfrom \n" +
            "prefixafter\n" +
            "prefixfrom ";
        assertEquals(expResult, result.toString());
    }
    
    @Test
    public void testGetDefaultValue() {
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        instance.getConfiguration().setDefaultValue("a[[table]]b[[TABLE]]c[[column]]d[[COLUMN]]e");
        EntityInfo entity = new EntityInfo(new DataTypeInfo("MyEntity"), EntityKind.ENTITY);
        FieldInfo field = new FieldInfo("myField", DataTypeInfo.INT_DATA_TYPE);
        String expResult = "aMyEntitybMyEntitycmyFielddmyFielde";
        String result = instance.getDefaultValue(entity, field);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testGetDefaultValueInvalid() {
        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        instance.getConfiguration().setDefaultValue("a[[table]]b[[TABLE]]c[[invalid]]f[[column]]d[[COLUMN]]e");
        EntityInfo entity = new EntityInfo(new DataTypeInfo("MyEntity"), EntityKind.ENTITY);
        FieldInfo field = new FieldInfo("myField", DataTypeInfo.INT_DATA_TYPE);
        String expResult = "aMyEntitybMyEntityc[[invalid]]fmyFielddmyFielde";
        String result = instance.getDefaultValue(entity, field);
        ArrayList<MessageContent> actualMsg = instance.getProcessingEnv().getMessager().getContent();
        ArrayList<MessageContent> expectedMsg = new ArrayList<MessageContent>();
        expectedMsg.add(new MessageContent(Diagnostic.Kind.ERROR,
                "Invalid template rule for generate the default value: [[invalid]]",
                field.getElement()));
        assertArrayEquals(expectedMsg.toArray(), actualMsg.toArray());
        assertEquals(expResult, result);
    }
    
    @Test
    public void testGetDefaultValueNull() {
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        EntityInfo entity = new EntityInfo(new DataTypeInfo("MyEntity"), EntityKind.ENTITY);
        FieldInfo field = new FieldInfo("myField", DataTypeInfo.INT_DATA_TYPE);
        String expResult = null;
        String result = instance.getDefaultValue(entity, field);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testGetIdSequenceName() {
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        instance.getConfiguration().setIdSecuenceNameTemplate("a[[table]]b[[TABLE]]c[[column]]d[[COLUMN]]e");
        EntityInfo entity = new EntityInfo(new DataTypeInfo("MyEntity"), EntityKind.ENTITY);
        FieldInfo field = new FieldInfo("myField", DataTypeInfo.INT_DATA_TYPE);
        String expResult = "aMyEntitybMyEntitycmyFielddmyFielde";
        String result = instance.getIdSequenceName(entity, field);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testGetIdSequenceNameInvalid() {
        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        instance.getConfiguration().setIdSecuenceNameTemplate("a[[table]]b[[TABLE]]c[[invalid]]f[[column]]d[[COLUMN]]e");
        EntityInfo entity = new EntityInfo(new DataTypeInfo("MyEntity"), EntityKind.ENTITY);
        FieldInfo field = new FieldInfo("myField", DataTypeInfo.INT_DATA_TYPE);
        String expResult = "aMyEntitybMyEntityc[[invalid]]fmyFielddmyFielde";
        String result = instance.getIdSequenceName(entity, field);
        ArrayList<MessageContent> actualMsg = instance.getProcessingEnv().getMessager().getContent();
        ArrayList<MessageContent> expectedMsg = new ArrayList<MessageContent>();
        expectedMsg.add(new MessageContent(Diagnostic.Kind.ERROR,
                "Invalid template rule for generate the id sequence name: [[invalid]]",
                field.getElement()));
        assertArrayEquals(expectedMsg.toArray(), actualMsg.toArray());
        assertEquals(expResult, result);
    }
    
    @Test
    public void testGetIdSequenceNameNull() {
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        EntityInfo entity = new EntityInfo(new DataTypeInfo("MyEntity"), EntityKind.ENTITY);
        FieldInfo field = new FieldInfo("myField", DataTypeInfo.INT_DATA_TYPE);
        String expResult = null;
        String result = instance.getIdSequenceName(entity, field);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testGetIdSequenceNameWithEntityIdSequenceName() {
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        EntityInfo entity = new EntityInfo(new DataTypeInfo("MyEntity"), EntityKind.ENTITY);
        entity.addAnnotation(new IdSequenceName() {

            @Override
            public String value() {
                return "a[[table]]b[[TABLE]]c[[column]]d[[COLUMN]]e";
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return IdSequenceName.class;
            }
        });
        FieldInfo field = new FieldInfo("myField", DataTypeInfo.INT_DATA_TYPE);
        String expResult = "aMyEntitybMyEntitycmyFielddmyFielde";
        String result = instance.getIdSequenceName(entity, field);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testGetIdSequenceNameWithFieldIdSequenceName() {
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        EntityInfo entity = new EntityInfo(new DataTypeInfo("MyEntity"), EntityKind.ENTITY);
        FieldInfo field = new FieldInfo("myField", DataTypeInfo.INT_DATA_TYPE);
        field.addAnnotation(new IdSequenceName() {

            @Override
            public String value() {
                return "a[[table]]b[[TABLE]]c[[column]]d[[COLUMN]]e";
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return IdSequenceName.class;
            }
        });
        String expResult = "aMyEntitybMyEntitycmyFielddmyFielde";
        String result = instance.getIdSequenceName(entity, field);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testRetrieveFieldsForExclude() {
        EntityInfo entity = new EntityInfo(new DataTypeInfo("MyEntity"), EntityKind.ENTITY);
        FieldInfo field1 = new FieldInfo("field1", DataTypeInfo.INT_DATA_TYPE);
        FieldInfo field2 = new FieldInfo("field2", DataTypeInfo.INT_DATA_TYPE);
        FieldInfo field3 = new FieldInfo("field3", DataTypeInfo.INT_DATA_TYPE);
        entity.addField(field1);
        entity.addField(field2);
        entity.addField(field3);
        String[] fields = new String[]{"field1", "field2"};
        HashSet<FieldInfo> expResult = new HashSet<FieldInfo>(2);
        expResult.add(field1);
        expResult.add(field2);
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        HashSet<FieldInfo> result = instance.retrieveFieldsForExclude(fields, entity, null);
        assertEquals(true, result.containsAll(expResult));
        assertEquals(expResult.size(), result.size());
    }
    
    @Test
    public void testRetrieveFieldsForExcludeNoValue() {
        EntityInfo entity = new EntityInfo(new DataTypeInfo("MyEntity"), EntityKind.ENTITY);
        FieldInfo field1 = new FieldInfo("field1", DataTypeInfo.INT_DATA_TYPE);
        FieldInfo field2 = new FieldInfo("field2", DataTypeInfo.INT_DATA_TYPE);
        FieldInfo field3 = new FieldInfo("field3", DataTypeInfo.INT_DATA_TYPE);
        entity.addField(field1);
        entity.addField(field2);
        entity.addField(field3);
        String[] fields = null;
        SqlQueryGenerator instance = new SqlQueryGeneratorImpl();
        HashSet<FieldInfo> result = instance.retrieveFieldsForExclude(fields, entity, null);
        assertEquals(0, result.size());
    }
    
    @Test
    public void testRetrieveFieldsForExcludeInvalid() {
        EntityInfo entity = new EntityInfo(new DataTypeInfo("MyEntity"), EntityKind.ENTITY);
        FieldInfo field1 = new FieldInfo("field1", DataTypeInfo.INT_DATA_TYPE);
        FieldInfo field2 = new FieldInfo("field2", DataTypeInfo.INT_DATA_TYPE);
        FieldInfo field3 = new FieldInfo("field3", DataTypeInfo.INT_DATA_TYPE);
        entity.addField(field1);
        entity.addField(field2);
        entity.addField(field3);
        String[] fields = new String[]{"field1", "invalid", "field2"};
        HashSet<FieldInfo> expResult = new HashSet<FieldInfo>(2);
        expResult.add(field1);
        expResult.add(field2);
        SqlQueryGeneratorImpl instance = new SqlQueryGeneratorImpl();
        HashSet<FieldInfo> result = instance.retrieveFieldsForExclude(fields, entity, null);
        ArrayList<MessageContent> actualMsg = instance.getProcessingEnv().getMessager().getContent();
        ArrayList<MessageContent> expectedMsg = new ArrayList<MessageContent>();
        expectedMsg.add(new MessageContent(Diagnostic.Kind.ERROR,
                "Unable to find the field with name 'invalid' specified as exclude entity field in the CustomSqlQuery annotation",
                null));
        assertArrayEquals(expectedMsg.toArray(), actualMsg.toArray());
        assertEquals(true, result.containsAll(expResult));
        assertEquals(expResult.size(), result.size());
    }

    public static class SqlQueryGeneratorImpl extends SqlQueryGenerator {

        public boolean appendSelectPageAfterWhere;
        public String[] idSequenceNextValue;
        public boolean handleVersionFieldOnInsert = false;
        public boolean handleVersionFieldOnUpdate = false;
        public boolean handleVersionFieldOnDelete = false;
        public boolean useOrderBy = true;

        @Override
        public void appendOrderBy(StringBuilder result, ArrayList<FieldInfo> orderBys, CustomSqlQuery customQuery) {
            if (!useOrderBy) {
                return;
            }
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
            if (!useOrderBy) {
                return;
            }
            result.append("appendOrderByAfterSelectForSelectPage");
        }

        @Override
        public void appendOrderByForSelectOneRow(StringBuilder result, ArrayList<FieldInfo> orderBys, CustomSqlQuery customQuery) {
            appendOrderBy(result, orderBys, customQuery);
        }

        @Override
        public void appendOrderByAfterSelectForSelectOneRow(StringBuilder result, ArrayList<FieldInfo> orderBys, CustomSqlQuery customQuery) {
            if (!useOrderBy) {
                return;
            }
            result.append("appendOrderByAfterSelectForSelectOneRow");
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
        public String translateComparator(Comparators comparator) {
            if (comparator == null) {
                return null;
            }
            switch (comparator) {
                case EQUAL:
                    return "[[column]] = [[value]]";
                case NOT_EQUAL:
                    return "[[column]] &lt;&gt; [[value]]";
                case EQUAL_INSENSITIVE:
                    return "lower([[column]]) = lower([[value]])";
                case NOT_EQUAL_INSENSITIVE:
                    return "lower([[column]]) <> lower([[value]])";
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
                return getJdbcTypeAttribute(field);
            } else if ("typeHandler".equals(rule) || "TYPE_HANDLER".equals(rule)) {
                return getTypeHandler(field);
            } else if ("valueNullable".equals(rule) || "VALUE_NULLABLE".equals(rule)) {
                return getParameterValue(field, true);
            } else if ("valueWhenNull".equals(rule) || "VALUE_WHEN_NULL".equals(rule)) {
                if (field.isExcludedFromObject()) {
                    return "FIELD_NOT_PRESENT_IN_OBJECT";
                }
                return getParameterValue(field, true);
            } else if ("valueWhenNull".equals(rule) || "VALUE_WHEN_NULL".equals(rule)) {
                String result = field.getValueWhenNull();
                if (result == null) {
                    return "UNKOWN_DEFAULT_VALUE";
                }
                return result;
            } else {
                return null;
            }
        }
    
        public String getJdbcTypeAttribute(FieldInfo field) {
            JdbcTypes jdbcType = getJdbcType(field);
            if (jdbcType == null) {
                getProcessingEnv().getMessager().printMessage(Diagnostic.Kind.ERROR, "Uknown jdbc data type. Use @JdbcType annotation for specify the jdbc data type.", field.getElement());
                return "";
            }
            return ",jdbcType=" + jdbcType.name();
        }

        public String getTypeHandler(FieldInfo field) {
            MyBatisTypeHandler th = field.getAnnotation(MyBatisTypeHandler.class);

            if (th == null) {
                return "";
            }

            DataTypeInfo typeHandler;
            try {
                typeHandler = NamesGenerator.createResultDataType(th.value());
            } catch (MirroredTypeException ex) {
                // See: http://blog.retep.org/2009/02/13/getting-class-values-from-annotations-in-an-annotationprocessor/
                typeHandler = NamesGenerator.createDataTypeFor(ex.getTypeMirror());
            }
            if (typeHandler == null) {
                getProcessingEnv().getMessager().printMessage(Diagnostic.Kind.ERROR, "Unable to find the type handler", field.getElement());
                return "";
            }

            return ",typeHandler=" + typeHandler.getQualifiedNameWithoutGenerics();
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
        public void appendSelectPageBeforeSelect(StringBuilder result, ArrayList<FieldInfo> orderBys, CustomSqlQuery customQuery) {
            result.append("selectPageBeforeSelect ");
        }

        @Override
        public boolean appendSelectPageAfterWhere(StringBuilder result, boolean requireAnd, ArrayList<FieldInfo> orderBys, CustomSqlQuery customQuery) {
            return appendSelectPageAfterWhere;
        }

        @Override
        public void appendSelectPageAfterOrderBy(StringBuilder result, ArrayList<FieldInfo> orderBys, CustomSqlQuery customQuery) {
            result.append("\nselectPageAfterOrderBy");
        }

        @Override
        public String[] envolveInSelectOneRow(String[] query) {
            List<String> queryList = new ArrayList<String>(Arrays.asList(query));
            queryList.add("envolveInSelectOneRow");
            return queryList.toArray(query);
        }

        @Override
        public void appendSelectOneRowBeforeSelect(StringBuilder result, ArrayList<FieldInfo> orderBys, CustomSqlQuery customQuery) {
            result.append(" top 1 ");
        }

        @Override
        public boolean appendSelectOneRowAfterWhere(StringBuilder result, boolean requireAnd, ArrayList<FieldInfo> orderBys, CustomSqlQuery customQuery) {
            if (requireAnd) {
                result.append("\n    and ");
            } else {
                result.append("    ");
            }
            result.append("rownum = 1");
            return true;
        }

        @Override
        public void appendSelectOneRowAfterOrderBy(StringBuilder result, ArrayList<FieldInfo> orderBys, CustomSqlQuery customQuery) {
            result.append("\nselectOneRowAfterOrderBy");
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
            result.append("initalVersionValue!" ).append(field.getName());
        }

        @Override
        public void appendNextVersionValue(StringBuilder result, EntityInfo entity, FieldInfo field) {
            result.append("nextVersion!").append(field.getName());
        }

        @Override
        public String getParameterValue(FieldInfo field, boolean ignoreValueWhenNull, boolean ignoreForcedValue) {
            if (!ignoreForcedValue && field.getForcedValue() != null) {
                return "parameterForcedValue!" + field.getName() + "!" + field.getValueWhenNull();
            } else if (ignoreValueWhenNull || field.getValueWhenNull() == null) {
                return "parameterValue!" + field.getName();
            } else if (field.isExcludedFromObject()) {
                return "parameterValueWhenNull!" + field.getName() + "!" + field.getValueWhenNull();
            } else {
                return "parameterValueAndWhenNull!" + field.getName() + "!" + field.getValueWhenNull();
            }
        }

        @Override
        public String[] getIdSequenceNextValue(EntityInfo entity, FieldInfo field) {
            return idSequenceNextValue;
        }

        @Override
        public String[] getIdSequenceCurrentValue(EntityInfo entity, FieldInfo field) {
            return new String[]{"currentValue!" + field.getName()};
        }

        public SqlQueryGeneratorImpl() {
            QueryGeneratorConfiguration config = new QueryGeneratorConfiguration();
            config.setProcessingEnv(new ProcessingEnviromentImpl());
            setConfiguration(config);
        }
        
        public SqlQueryGeneratorImpl(boolean useOrderBy) {
            this();
            this.useOrderBy = useOrderBy;
        }

        @Override
        public ProcessingEnviromentImpl getProcessingEnv() {
            return (ProcessingEnviromentImpl) super.getProcessingEnv();
        }

        @Override
        public void appendParameterValueOrDefaultInDatabaseWhenNullForInsert(StringBuilder result, OperationInfo operation, EntityInfo entity, FieldInfo field) {
            result.append("parameterValueOrDBDefault!").append(field.getName());
        }
    }
}