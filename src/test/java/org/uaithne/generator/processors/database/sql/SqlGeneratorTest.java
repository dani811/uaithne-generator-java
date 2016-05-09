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
import org.junit.Test;
import static org.junit.Assert.*;
import org.uaithne.annotations.Comparators;
import org.uaithne.annotations.Comparator;
import org.uaithne.annotations.CustomComparator;
import org.uaithne.annotations.sql.JdbcType;
import org.uaithne.annotations.sql.JdbcTypes;
import org.uaithne.generator.commons.DataTypeInfo;
import org.uaithne.generator.commons.EntityInfo;
import org.uaithne.generator.commons.FieldInfo;
import org.uaithne.generator.commons.OperationInfo;
import org.uaithne.generator.processors.database.QueryGeneratorConfiguration;
import org.uaithne.generator.utils.ProcessingEnviromentImpl;

public class SqlGeneratorTest {

    @Test
    public void testGetComparatorFromFieldWithUseComparatorAnnotation() {
        FieldInfo field = new FieldInfo("field", DataTypeInfo.LIST_DATA_TYPE);
        field.addAnnotation(new Comparator() {
            @Override
            public Comparators value() {
                return Comparators.NOT_END_WITH;
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return Comparator.class;
            }
        });
        SqlGenerator instance = new SqlGeneratorImpl();
        Comparators expResult = Comparators.NOT_END_WITH;
        Comparators result = instance.getComparator(field);
        assertEquals(expResult, result);
    }

    @Test
    public void testGetComparatorFromListField() {
        FieldInfo field = new FieldInfo("field", DataTypeInfo.LIST_DATA_TYPE);
        SqlGenerator instance = new SqlGeneratorImpl();
        Comparators expResult = Comparators.IN;
        Comparators result = instance.getComparator(field);
        assertEquals(expResult, result);
    }

    @Test
    public void testGetComparatorFromField() {
        FieldInfo field = new FieldInfo("field", new DataTypeInfo("Integer"));
        SqlGenerator instance = new SqlGeneratorImpl();
        Comparators expResult = Comparators.EQUAL;
        Comparators result = instance.getComparator(field);
        assertEquals(expResult, result);
    }

    @Test
    public void testGetConditionTemplateFromFieldWithCustomComparatorAnnotation() {
        FieldInfo field = new FieldInfo("field", DataTypeInfo.LIST_DATA_TYPE);
        field.addAnnotation(new CustomComparator() {
            @Override
            public String value() {
                return "CustomComparator";
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return CustomComparator.class;
            }
        });
        String comparatorTemplate = "comparatorTemplate";
        SqlGenerator instance = new SqlGeneratorImpl();
        String expResult = "CustomComparator";
        String result = instance.getConditionTemplate(field, comparatorTemplate);
        assertEquals(expResult, result);
    }

    @Test
    public void testGetConditionTemplate() {
        FieldInfo field = new FieldInfo("field", DataTypeInfo.LIST_DATA_TYPE);
        String comparatorTemplate = "comparatorTemplate";
        SqlGenerator instance = new SqlGeneratorImpl();
        String expResult = "comparatorTemplate";
        String result = instance.getConditionTemplate(field, comparatorTemplate);
        assertEquals(expResult, result);
    }

    @Test
    public void testAppendToQueryWithNullArray() {
        StringBuilder query = new StringBuilder("initial_value");
        String[] array = null;
        String prefix = "pre";
        SqlGenerator instance = new SqlGeneratorImpl();
        instance.appendToQuery(query, array, prefix);
        assertEquals("initial_value", query.toString());
    }

    @Test
    public void testAppendToQuery() {
        StringBuilder query = new StringBuilder();
        String[] array = new String[]{"append", "to", "query"};
        String prefix = "pre";
        SqlGenerator instance = new SqlGeneratorImpl();
        instance.appendToQuery(query, array, prefix);
        assertEquals("preappend\npreto\nprequery ", query.toString());
    }

    @Test
    public void testAppendToQuerylnWithNullArray() {
        StringBuilder query = new StringBuilder("initial_value");
        String[] array = null;
        String prefix = "";
        SqlGenerator instance = new SqlGeneratorImpl();
        instance.appendToQueryln(query, array, prefix);
        assertEquals("initial_value", query.toString());
    }

    @Test
    public void testAppendToQuerylnWithInitialQuery() {
        StringBuilder query = new StringBuilder("initial_value");
        String[] array = new String[]{"append", "to", "query", "ln"};
        String prefix = "pre";
        SqlGenerator instance = new SqlGeneratorImpl();
        instance.appendToQueryln(query, array, prefix);
        assertEquals("initial_value\n"
                + "preappend\n"
                + "preto\n"
                + "prequery\n"
                + "preln ", query.toString());
    }

    @Test
    public void testAppendToQuerylnWithEmptyInitialQuery() {
        StringBuilder query = new StringBuilder("");
        String[] array = new String[]{"append", "to", "query", "ln"};
        String prefix = "pre";
        SqlGenerator instance = new SqlGeneratorImpl();
        instance.appendToQueryln(query, array, prefix);
        assertEquals("preappend\n"
                + "preto\n"
                + "prequery\n"
                + "preln ", query.toString());
    }

    @Test
    public void testJoinln() {
        String[] strings = new String[]{"test", "for", "joinln"};
        SqlGenerator instance = new SqlGeneratorImpl();
        String expResult = "test\nfor\njoinln";
        String result = instance.joinln(strings);
        assertEquals(expResult, result);
    }

    @Test
    public void testJoinlnWithNullArray() {
        String[] strings = null;
        SqlGenerator instance = new SqlGeneratorImpl();
        String expResult = null;
        String result = instance.joinln(strings);
        assertEquals(expResult, result);
    }

    @Test
    public void testJoinsp() {
        String[] strings = new String[]{"test", "for", "joinsp"};
        SqlGenerator instance = new SqlGeneratorImpl();
        String expResult = "test for joinsp";
        String result = instance.joinsp(strings);
        assertEquals(expResult, result);
    }

    @Test
    public void testJoinspWithNullArray() {
        String[] strings = null;
        SqlGenerator instance = new SqlGeneratorImpl();
        String expResult = null;
        String result = instance.joinsp(strings);
        assertEquals(expResult, result);
    }

    @Test
    public void testHasQueryValueWithNullArray() {
        String[] array = null;
        SqlGenerator instance = new SqlGeneratorImpl();
        boolean expResult = false;
        boolean result = instance.hasQueryValue(array);
        assertEquals(expResult, result);
    }

    @Test
    public void testHasQueryValueWithEmptyArray() {
        String[] array = new String[]{};
        SqlGenerator instance = new SqlGeneratorImpl();
        boolean expResult = false;
        boolean result = instance.hasQueryValue(array);
        assertEquals(expResult, result);
    }

    @Test
    public void testHasQueryValueWithArrayWithNullStrings() {
        String[] array = new String[]{null, null};
        SqlGenerator instance = new SqlGeneratorImpl();
        boolean expResult = false;
        boolean result = instance.hasQueryValue(array);
        assertEquals(expResult, result);
    }

    @Test
    public void testHasQueryValueWithArrayWithEmptyStrings() {
        String[] array = new String[]{"", ""};
        SqlGenerator instance = new SqlGeneratorImpl();
        boolean expResult = false;
        boolean result = instance.hasQueryValue(array);
        assertEquals(expResult, result);
    }

    @Test
    public void testHasQueryValueWithArrayWithValues() {
        String[] array = new String[]{"string"};
        SqlGenerator instance = new SqlGeneratorImpl();
        boolean expResult = true;
        boolean result = instance.hasQueryValue(array);
        assertEquals(expResult, result);
    }
    
    @Test
    public void testGetJdbcType() {
        FieldInfo field = new FieldInfo("field", DataTypeInfo.BOOLEAN_DATA_TYPE);
        SqlGenerator instance = new SqlGeneratorImpl();
        JdbcTypes type = instance.getJdbcType(field);
        assertEquals(JdbcTypes.BOOLEAN, type);
    }
    
    @Test
    public void testGetJdbcType2() {
        DataTypeInfo dataType = new DataTypeInfo(DataTypeInfo.DOUBLE_DATA_TYPE);
        dataType.setIsEnum(true);
        FieldInfo field = new FieldInfo("field", dataType);
        SqlGenerator instance = new SqlGeneratorImpl();
        JdbcTypes type = instance.getJdbcType(field);
        assertEquals(JdbcTypes.INTEGER, type);
    }
    
    @Test
    public void testGetJdbcType3() {
        DataTypeInfo dataType = new DataTypeInfo("MyType");
        FieldInfo field = new FieldInfo("field", dataType);
        SqlGenerator instance = new SqlGeneratorImpl();
        JdbcTypes type = instance.getJdbcType(field);
        assertEquals(JdbcTypes.ARRAY, type);
    }
    
    @Test
    public void testGetJdbcType4() {
        DataTypeInfo dataType = new DataTypeInfo(DataTypeInfo.DOUBLE_DATA_TYPE);
        FieldInfo field = new FieldInfo("field", dataType);
        field.addAnnotation(new JdbcType() {

            @Override
            public JdbcTypes value() {
                return JdbcTypes.CLOB;
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return JdbcType.class;
            }
        });
        SqlGenerator instance = new SqlGeneratorImpl();
        JdbcTypes type = instance.getJdbcType(field);
        assertEquals(JdbcTypes.CLOB, type);
    }

    public class SqlGeneratorImpl extends SqlGenerator {

        public SqlGeneratorImpl() {
            QueryGeneratorConfiguration config = new QueryGeneratorConfiguration();
            config.setProcessingEnv(new ProcessingEnviromentImpl());
            config.getCustomJdbcTypeMap().put("MyType", JdbcTypes.ARRAY);
            setConfiguration(config);
        }

        @Override
        public String[] getSelectManyQuery(OperationInfo operation) {
            throw new UnsupportedOperationException("No needed for test");
        }

        @Override
        public String[] getSelectOneQuery(OperationInfo operation) {
            throw new UnsupportedOperationException("No needed for test");
        }

        @Override
        public String[] getSelectCountQuery(OperationInfo operation) {
            throw new UnsupportedOperationException("No needed for test");
        }

        @Override
        public String[] getSelectPageCountQuery(OperationInfo operation) {
            throw new UnsupportedOperationException("No needed for test");
        }

        @Override
        public String[] getSelectPageQuery(OperationInfo operation) {
            throw new UnsupportedOperationException("No needed for test");
        }

        @Override
        public String[] getEntityDeleteByIdQuery(EntityInfo entity, OperationInfo operation) {
            throw new UnsupportedOperationException("No needed for test");
        }

        @Override
        public String[] getEntityInsertQuery(EntityInfo entity, OperationInfo operation) {
            throw new UnsupportedOperationException("No needed for test");
        }

        @Override
        public String[] getEntityLastInsertedIdQuery(EntityInfo entity, OperationInfo operation, boolean excludeSequenceQuery) {
            throw new UnsupportedOperationException("No needed for test");
        }

        @Override
        public String[] getEntityMergeQuery(EntityInfo entity, OperationInfo operation) {
            throw new UnsupportedOperationException("No needed for test");
        }

        @Override
        public String[] getEntitySelectByIdQuery(EntityInfo entity, OperationInfo operation) {
            throw new UnsupportedOperationException("No needed for test");
        }

        @Override
        public String[] getEntityUpdateQuery(EntityInfo entity, OperationInfo operation) {
            throw new UnsupportedOperationException("No needed for test");
        }

        @Override
        public String[] getCustomDeleteQuery(OperationInfo operation) {
            throw new UnsupportedOperationException("No needed for test");
        }

        @Override
        public String[] getCustomInsertQuery(OperationInfo operation) {
            throw new UnsupportedOperationException("No needed for test");
        }

        @Override
        public String[] getCustomUpdateQuery(OperationInfo operation) {
            throw new UnsupportedOperationException("No needed for test");
        }

        @Override
        public String[] getComplexSelectCallQuery(OperationInfo operation) {
            throw new UnsupportedOperationException("No needed for test");
        }

        @Override
        public String[] getComplexInsertCallQuery(OperationInfo operation) {
            throw new UnsupportedOperationException("No needed for test");
        }

        @Override
        public String[] getComplexUpdateCallQuery(OperationInfo operation) {
            throw new UnsupportedOperationException("No needed for test");
        }

        @Override
        public String[] getComplexDeleteCallQuery(OperationInfo operation) {
            throw new UnsupportedOperationException("No needed for test");
        }
    }
}