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
import org.uaithne.annotations.Comparator;
import org.uaithne.annotations.MappedName;
import org.uaithne.annotations.UseComparator;
import org.uaithne.annotations.UseCustomComparator;
import org.uaithne.generator.commons.DataTypeInfo;
import org.uaithne.generator.commons.EntityInfo;
import org.uaithne.generator.commons.EntityKind;
import org.uaithne.generator.commons.FieldInfo;
import org.uaithne.generator.commons.OperationInfo;

public class SqlGeneratorTest {

    @Test
    public void testGetSequenceName() {
        String[] tableName = new String[]{"test", "for", "get", "sequenceName"};
        SqlGenerator instance = new SqlGeneratorImpl();
        instance.setSequenceRegex("[eo][nr]|e$");
        instance.setSequenceReplacement("x");
        String expResult = "test fx get sequxceNamx";
        String result = instance.getSequenceName(tableName);
        assertEquals(expResult, result);
    }

    @Test
    public void testGetComparatorFromFieldWithUseComparatorAnnotation() {
        FieldInfo field = new FieldInfo("field", DataTypeInfo.LIST_DATA_TYPE);
        field.addAnnotation(new UseComparator() {
            @Override
            public Comparator value() {
                return Comparator.NOT_END_WITH;
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return UseComparator.class;
            }
        });
        SqlGenerator instance = new SqlGeneratorImpl();
        Comparator expResult = Comparator.NOT_END_WITH;
        Comparator result = instance.getComparator(field);
        assertEquals(expResult, result);
    }

    @Test
    public void testGetComparatorFromListField() {
        FieldInfo field = new FieldInfo("field", DataTypeInfo.LIST_DATA_TYPE);
        SqlGenerator instance = new SqlGeneratorImpl();
        Comparator expResult = Comparator.IN;
        Comparator result = instance.getComparator(field);
        assertEquals(expResult, result);
    }

    @Test
    public void testGetComparatorFromField() {
        FieldInfo field = new FieldInfo("field", new DataTypeInfo("Integer"));
        SqlGenerator instance = new SqlGeneratorImpl();
        Comparator expResult = Comparator.EQUAL;
        Comparator result = instance.getComparator(field);
        assertEquals(expResult, result);
    }

    @Test
    public void testGetConditionTemplateFromFieldWithCustomComparatorAnnotation() {
        FieldInfo field = new FieldInfo("field", DataTypeInfo.LIST_DATA_TYPE);
        field.addAnnotation(new UseCustomComparator() {
            @Override
            public String value() {
                return "CustomComparator";
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return UseCustomComparator.class;
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
    public void testGetMappedNameFromEntityWithMappedNameAnnotation() {
        EntityInfo entity = new EntityInfo(new DataTypeInfo("myEntity"), EntityKind.ENTITY);
        entity.addAnnotation(new MappedName() {
            @Override
            public String[] value() {
                return new String[]{"JOIN", "databaseTable\nON"};
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return MappedName.class;
            }
        });
        SqlGenerator instance = new SqlGeneratorImpl();
        String[] expResult = new String[]{"JOIN", "databaseTable", "ON"};
        String[] result = instance.getMappedName(entity);
        assertArrayEquals(expResult, result);
    }

    @Test
    public void testGetMappedName() {
        EntityInfo entity = new EntityInfo(DataTypeInfo.LIST_DATA_TYPE, EntityKind.ENTITY);
        SqlGenerator instance = new SqlGeneratorImpl();
        String[] expResult = new String[]{"List"};
        String[] result = instance.getMappedName(entity);
        assertArrayEquals(expResult, result);
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

    public class SqlGeneratorImpl extends SqlGenerator {

        @Override
        public String[] getSelectManyQuery(OperationInfo operation) {
            throw new UnsupportedOperationException("No needed for test");
        }

        @Override
        public String[] getSelectOneQuery(OperationInfo operation) {
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
        public String[] getEntityLastInsertedIdQuery(EntityInfo entity, OperationInfo operation) {
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
    }
}