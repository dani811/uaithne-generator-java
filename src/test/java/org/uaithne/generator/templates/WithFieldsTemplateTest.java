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
package org.uaithne.generator.templates;

import java.io.IOException;
import java.util.ArrayList;
import junit.framework.Assert;
import org.junit.Test;
import org.uaithne.generator.commons.DataTypeInfo;
import org.uaithne.generator.commons.FieldInfo;

public class WithFieldsTemplateTest {

    @Test
    public void testWriteArguments() throws Exception {
        Appendable appender = new StringBuilder();

        ArrayList<FieldInfo> fields = new ArrayList<FieldInfo>();
        FieldInfo fieldInfo = new FieldInfo("field1", DataTypeInfo.LIST_DATA_TYPE);
        fields.add(fieldInfo);
        fieldInfo = new FieldInfo("field2", new DataTypeInfo("java.util.Date", "Date"));
        fields.add(fieldInfo);
        fieldInfo = new FieldInfo("field3", new DataTypeInfo("int"));
        fields.add(fieldInfo);

        WithFieldsTemplate instance = new WithFieldsTemplateImpl();
        instance.writeArguments(appender, fields);
        Assert.assertEquals("List field1, Date field2, int field3", appender.toString());
    }

    @Test
    public void testWriteArgumentsForAddMore() throws Exception {
        Appendable appender = new StringBuilder();

        ArrayList<FieldInfo> fields = new ArrayList<FieldInfo>();
        FieldInfo fieldInfo = new FieldInfo("field1", DataTypeInfo.LIST_DATA_TYPE);
        fields.add(fieldInfo);
        fieldInfo = new FieldInfo("field2", new DataTypeInfo("java.util.Date", "Date"));
        fields.add(fieldInfo);
        fieldInfo = new FieldInfo("field3", new DataTypeInfo("int"));
        fields.add(fieldInfo);

        WithFieldsTemplate instance = new WithFieldsTemplateImpl();
        instance.writeArgumentsForAddMore(appender, fields);
        Assert.assertEquals("List field1, Date field2, int field3, ", appender.toString());
    }

    @Test
    public void testWriteCallArguments() throws Exception {
        Appendable appender = new StringBuilder();
        ArrayList<FieldInfo> fields = new ArrayList<FieldInfo>();
        FieldInfo fieldInfo = new FieldInfo("field1", DataTypeInfo.LIST_DATA_TYPE);
        fields.add(fieldInfo);
        fieldInfo = new FieldInfo("field2", new DataTypeInfo("java.util.Date", "Date"));
        fields.add(fieldInfo);
        fieldInfo = new FieldInfo("field3", new DataTypeInfo("int"));
        fields.add(fieldInfo);

        WithFieldsTemplate instance = new WithFieldsTemplateImpl();
        instance.writeCallArguments(appender, fields);
        Assert.assertEquals("field1, field2, field3", appender.toString());
    }

    @Test
    public void testWriteCallArgumentsForAddMore() throws Exception {

        Appendable appender = new StringBuilder();
        ArrayList<FieldInfo> fields = new ArrayList<FieldInfo>();
        FieldInfo fieldInfo = new FieldInfo("field1", DataTypeInfo.LIST_DATA_TYPE);
        fields.add(fieldInfo);
        fieldInfo = new FieldInfo("field2", new DataTypeInfo("java.util.Date", "Date"));
        fields.add(fieldInfo);
        fieldInfo = new FieldInfo("field3", new DataTypeInfo("int"));
        fields.add(fieldInfo);

        WithFieldsTemplate instance = new WithFieldsTemplateImpl();
        instance.writeCallArgumentsForAddMore(appender, fields);
        Assert.assertEquals("field1, field2, field3, ", appender.toString());

    }

    public class WithFieldsTemplateImpl extends WithFieldsTemplate {

        @Override
        protected void writeContent(Appendable appender) throws IOException {
            throw new UnsupportedOperationException("No needed for test");
        }
    }
}