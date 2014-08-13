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
import org.uaithne.generator.commons.TemplateProcessor;

public class PojoTemplateTest {

    @Test
    public void testWriteDocumentation() throws Exception {
        Appendable appender = new StringBuilder();
        String[] doc = new String[]{"Documentation", "for", "test"};
        PojoTemplate instance = new PojoTemplateImpl();
        instance.writeDocumentation(appender, doc);
        Assert.assertEquals("    /**\n"
                + "     * Documentation\n"
                + "     * for\n"
                + "     * test\n"
                + "     */\n", appender.toString());
    }

    @Test
    public void testWriteGetterDocumentation() throws Exception {
        Appendable appender = new StringBuilder();
        String[] doc = new String[]{"Documentation", "for", "getter"};
        String fieldName = "returnFieldName";
        PojoTemplate instance = new PojoTemplateImpl();
        instance.writeGetterDocumentation(appender, doc, fieldName);
        Assert.assertEquals("    /**\n"
                + "     * Documentation\n"
                + "     * for\n"
                + "     * getter\n"
                + "     * @return the " + fieldName + "\n"
                + "     */\n", appender.toString());
    }

    @Test
    public void testWriteSetterDocumentation() throws Exception {
        Appendable appender = new StringBuilder();
        String[] doc = new String[]{"Documentation", "for", "setter"};
        String fieldName = "paramField";
        PojoTemplate instance = new PojoTemplateImpl();
        instance.writeSetterDocumentation(appender, doc, fieldName);
        Assert.assertEquals("    /**\n"
                + "     * Documentation\n"
                + "     * for\n"
                + "     * setter\n"
                + "     * @param paramField the paramField to set\n"
                + "     */\n", appender.toString());
    }

    @Test
    public void testWriteTransientField() throws Exception {
        Appendable appender = new StringBuilder();
        FieldInfo field = new FieldInfo("field1", DataTypeInfo.LIST_DATA_TYPE);
        field.setMarkAsTransient(true);
        field.ensureValidationsInfo(TemplateProcessor.getGenerationInfo());
        PojoTemplate instance = new PojoTemplateImpl();
        instance.writeField(appender, field);
        Assert.assertEquals("    private transient List field1;\n", appender.toString());
    }

    @Test
    public void testWriteFieldWithDoc() throws Exception {
        Appendable appender = new StringBuilder();
        FieldInfo field = new FieldInfo("field1", DataTypeInfo.LIST_DATA_TYPE);
        field.setDocumentation(new String[]{"Documentation", "for", "field"});
        field.ensureValidationsInfo(TemplateProcessor.getGenerationInfo());
        PojoTemplate instance = new PojoTemplateImpl();
        instance.writeField(appender, field);
        Assert.assertEquals("    /**\n"
                + "     * Documentation\n"
                + "     * for\n"
                + "     * field\n"
                + "     */\n"
                + "    private List field1;\n", appender.toString());
    }

    @Test
    public void testWriteFieldWithDefaultValue() throws Exception {
        Appendable appender = new StringBuilder();
        FieldInfo field = new FieldInfo("field1", new DataTypeInfo("int"));
        field.setDefaultValue("5");
        field.ensureValidationsInfo(TemplateProcessor.getGenerationInfo());
        PojoTemplate instance = new PojoTemplateImpl();
        instance.writeField(appender, field);
        Assert.assertEquals("    private int field1 = 5;\n", appender.toString());
    }

    @Test
    public void testWriteFieldGetter() throws Exception {
        Appendable appender = new StringBuilder();
        FieldInfo field = new FieldInfo("field1", new DataTypeInfo("int"));
        PojoTemplate instance = new PojoTemplateImpl();
        instance.writeFieldGetter(appender, field);
        Assert.assertEquals("    /**\n"
                + "     * @return the field1\n"
                + "     */\n"
                + "    public int getField1() {\n"
                + "        return field1;\n"
                + "    }\n", appender.toString());
    }

    @Test
    public void testWriteFieldOverrideGetter() throws Exception {
        Appendable appender = new StringBuilder();
        FieldInfo field = new FieldInfo("field1", new DataTypeInfo("int"));
        field.setMarkAsOvwrride(true);
        PojoTemplate instance = new PojoTemplateImpl();
        instance.writeFieldGetter(appender, field);
        Assert.assertEquals("    /**\n"
                + "     * @return the field1\n"
                + "     */\n"
                + "    @Override\n"
                + "    public int getField1() {\n"
                + "        return field1;\n"
                + "    }\n", appender.toString());
    }

    @Test
    public void testWriteFieldDeprecatedGetter() throws Exception {
        Appendable appender = new StringBuilder();
        FieldInfo field = new FieldInfo("field1", new DataTypeInfo("int"));
        field.setDeprecated(true);
        PojoTemplate instance = new PojoTemplateImpl();
        instance.writeFieldGetter(appender, field);
        Assert.assertEquals("    /**\n"
                + "     * @return the field1\n"
                + "     */\n"
                + "    @Deprecated\n"
                + "    public int getField1() {\n"
                + "        return field1;\n"
                + "    }\n", appender.toString());
    }

    @Test
    public void testWriteFieldPrimitiveBooleanGetter() throws Exception {
        Appendable appender = new StringBuilder();
        FieldInfo field = new FieldInfo("field1", new DataTypeInfo("boolean"));
        PojoTemplate instance = new PojoTemplateImpl();
        instance.writeFieldGetter(appender, field);
        Assert.assertEquals("    /**\n"
                + "     * @return the field1\n"
                + "     */\n"
                + "    public boolean isField1() {\n"
                + "        return field1;\n"
                + "    }\n", appender.toString());
    }

    @Test
    public void testWriteFieldSetter() throws Exception {
        Appendable appender = new StringBuilder();
        FieldInfo field = new FieldInfo("field1", new DataTypeInfo("int"));
        PojoTemplate instance = new PojoTemplateImpl();
        instance.writeFieldSetter(appender, field);
        Assert.assertEquals("    /**\n"
                + "     * @param field1 the field1 to set\n"
                + "     */\n"
                + "    public void setField1(int field1) {\n"
                + "        this.field1 = field1;\n"
                + "    }\n", appender.toString());
    }

    @Test
    public void testWriteFieldOverrrideSetter() throws Exception {
        Appendable appender = new StringBuilder();
        FieldInfo field = new FieldInfo("field1", new DataTypeInfo("int"));
        field.setMarkAsOvwrride(true);
        PojoTemplate instance = new PojoTemplateImpl();
        instance.writeFieldSetter(appender, field);
        Assert.assertEquals("    /**\n"
                + "     * @param field1 the field1 to set\n"
                + "     */\n"
                + "    @Override\n"
                + "    public void setField1(int field1) {\n"
                + "        this.field1 = field1;\n"
                + "    }\n", appender.toString());
    }

    @Test
    public void testWriteFieldDeprecatedSetter() throws Exception {
        Appendable appender = new StringBuilder();
        FieldInfo field = new FieldInfo("field1", new DataTypeInfo("int"));
        field.setDeprecated(true);
        PojoTemplate instance = new PojoTemplateImpl();
        instance.writeFieldSetter(appender, field);
        Assert.assertEquals("    /**\n"
                + "     * @param field1 the field1 to set\n"
                + "     */\n"
                + "    @Deprecated\n"
                + "    public void setField1(int field1) {\n"
                + "        this.field1 = field1;\n"
                + "    }\n", appender.toString());
    }

    @Test
    public void testWriteFieldsInitialization() throws Exception {
        Appendable appender = new StringBuilder();
        ArrayList<FieldInfo> fields = new ArrayList<FieldInfo>();
        fields.add(new FieldInfo("field1", new DataTypeInfo("int")));
        fields.add(new FieldInfo("field2", new DataTypeInfo("boolean")));
        PojoTemplate instance = new PojoTemplateImpl();
        instance.writeFieldsInitialization(appender, fields);
        Assert.assertEquals("        this.field1 = field1;\n"
                + "        this.field2 = field2;\n", appender.toString());
    }

    @Test
    public void testWriteToString() throws Exception {
        Appendable appender = new StringBuilder();
        ArrayList<FieldInfo> fields = new ArrayList<FieldInfo>();
        fields.add(new FieldInfo("field1", new DataTypeInfo("int")));
        fields.add(new FieldInfo("field2", new DataTypeInfo("boolean")));
        boolean callSuper = false;
        PojoTemplate instance = new PojoTemplateImpl();
        instance.writeToString(appender, fields, callSuper);
        Assert.assertEquals("    @Override\n"
                + "    public String toString(){\n"
                + "        return \"null{\" +\n"
                + "            \"field1=\" + field1 + \"| \" +\n"
                + "            \"field2=\" + field2 + \n"
                + "            \"}\";\n"
                + "    }\n", appender.toString());
    }

    @Test
    public void testWriteToStringWithSuperCall() throws Exception {
        Appendable appender = new StringBuilder();
        ArrayList<FieldInfo> fields = new ArrayList<FieldInfo>();
        fields.add(new FieldInfo("field1", new DataTypeInfo("int")));
        fields.add(new FieldInfo("field2", new DataTypeInfo("boolean")));
        boolean callSuper = true;
        PojoTemplate instance = new PojoTemplateImpl();
        instance.writeToString(appender, fields, callSuper);
        Assert.assertEquals("    @Override\n"
                + "    public String toString(){\n"
                + "        return \"null{\" +\n"
                + "            \"field1=\" + field1 + \"| \" +\n"
                + "            \"field2=\" + field2 + \"| \" +\n"
                + "            \"superclass=\" + super.toString() +\n"
                + "            \"}\";\n"
                + "    }\n", appender.toString());

    }

    @Test
    public void testWriteToStringWithSuperCallAndWithoutFields() throws Exception {
        Appendable appender = new StringBuilder();
        ArrayList<FieldInfo> fields = new ArrayList<FieldInfo>();
        boolean callSuper = true;
        PojoTemplate instance = new PojoTemplateImpl();
        instance.writeToString(appender, fields, callSuper);
        Assert.assertEquals("    @Override\n"
                + "    public String toString(){\n"
                + "        return \"null{\" +\n"
                + "            \"superclass=\" + super.toString() +\n"
                + "            \"}\";\n"
                + "    }\n", appender.toString());

    }

    @Test
    public void testWriteEquals() throws Exception {
        Appendable appender = new StringBuilder();
        ArrayList<FieldInfo> fields = new ArrayList<FieldInfo>();
        fields.add(new FieldInfo("field1", new DataTypeInfo("int")));
        fields.add(new FieldInfo("field2", new DataTypeInfo("boolean")));
        boolean callSuper = false;
        PojoTemplate instance = new PojoTemplateImpl();
        instance.writeEquals(appender, fields, callSuper);
        Assert.assertEquals("    @Override\n"
                + "    public boolean equals(Object obj) {\n"
                + "        if (obj == null) {\n"
                + "            return false;\n"
                + "        }\n"
                + "        if (getClass() != obj.getClass()) {\n"
                + "            return false;\n"
                + "        }\n"
                + "        final null other = (null) obj;\n"
                + "        if (this.field1 != other.field1) {\n"
                + "            return false;\n"
                + "        }\n"
                + "        if (this.field2 != other.field2) {\n"
                + "            return false;\n"
                + "        }\n"
                + "        return true;\n"
                + "    }\n", appender.toString());
    }

    @Test
    public void testWriteEqualsWithSuperCall() throws Exception {
        Appendable appender = new StringBuilder();
        ArrayList<FieldInfo> fields = new ArrayList<FieldInfo>();
        fields.add(new FieldInfo("field1", new DataTypeInfo("int")));
        fields.add(new FieldInfo("field2", new DataTypeInfo("boolean")));
        boolean callSuper = true;
        PojoTemplate instance = new PojoTemplateImpl();
        instance.writeEquals(appender, fields, callSuper);
        Assert.assertEquals("    @Override\n"
                + "    public boolean equals(Object obj) {\n"
                + "        if (obj == null) {\n"
                + "            return false;\n"
                + "        }\n"
                + "        if (getClass() != obj.getClass()) {\n"
                + "            return false;\n"
                + "        }\n"
                + "        if (!super.equals(obj)) {\n"
                + "            return false;\n"
                + "        }\n"
                + "        final null other = (null) obj;\n"
                + "        if (this.field1 != other.field1) {\n"
                + "            return false;\n"
                + "        }\n"
                + "        if (this.field2 != other.field2) {\n"
                + "            return false;\n"
                + "        }\n"
                + "        return true;\n"
                + "    }\n", appender.toString());
    }

    @Test
    public void testWriteEqualsWithTransientField() throws Exception {
        Appendable appender = new StringBuilder();
        ArrayList<FieldInfo> fields = new ArrayList<FieldInfo>();
        FieldInfo field1 = new FieldInfo("field1", new DataTypeInfo("int"));
        field1.setMarkAsTransient(true);
        fields.add(field1);
        fields.add(new FieldInfo("field2", new DataTypeInfo("boolean")));
        boolean callSuper = false;
        PojoTemplate instance = new PojoTemplateImpl();
        instance.writeEquals(appender, fields, callSuper);
        Assert.assertEquals("    @Override\n"
                + "    public boolean equals(Object obj) {\n"
                + "        if (obj == null) {\n"
                + "            return false;\n"
                + "        }\n"
                + "        if (getClass() != obj.getClass()) {\n"
                + "            return false;\n"
                + "        }\n"
                + "        final null other = (null) obj;\n"
                + "        if (this.field2 != other.field2) {\n"
                + "            return false;\n"
                + "        }\n"
                + "        return true;\n"
                + "    }\n", appender.toString());
    }

    @Test
    public void testWriteEqualsWithoutFields() throws Exception {
        Appendable appender = new StringBuilder();
        ArrayList<FieldInfo> fields = new ArrayList<FieldInfo>();
        boolean callSuper = false;
        PojoTemplate instance = new PojoTemplateImpl();
        instance.writeEquals(appender, fields, callSuper);
        Assert.assertEquals("    @Override\n"
                + "    public boolean equals(Object obj) {\n"
                + "        if (obj == null) {\n"
                + "            return false;\n"
                + "        }\n"
                + "        if (getClass() != obj.getClass()) {\n"
                + "            return false;\n"
                + "        }\n"
                + "        if (!super.equals(obj)) {\n"
                + "            return false;\n"
                + "        }\n"
                + "        return true;\n"
                + "    }\n", appender.toString());
    }

    @Test
    public void testWriteHashCode() throws Exception {
        Appendable appender = new StringBuilder();
        ArrayList<FieldInfo> fields = new ArrayList<FieldInfo>();
        fields.add(new FieldInfo("field1", new DataTypeInfo("int")));
        fields.add(new FieldInfo("field2", new DataTypeInfo("boolean")));
        boolean callSuper = false;
        String firstPrime = "7";
        String secondPrime = "31";
        PojoTemplate instance = new PojoTemplateImpl();
        instance.writeHashCode(appender, fields, callSuper, firstPrime, secondPrime);
        Assert.assertEquals("    @Override\n"
                + "    public int hashCode() {\n"
                + "        int hash = 7;\n"
                + "        hash = 31 * hash + this.field1;\n"
                + "        hash = 31 * hash + (this.field2 ? 1 : 0);\n"
                + "        return hash;\n"
                + "    }\n", appender.toString());

    }

    @Test
    public void testWriteHashCodeWithSuperCall() throws Exception {
        Appendable appender = new StringBuilder();
        ArrayList<FieldInfo> fields = new ArrayList<FieldInfo>();
        fields.add(new FieldInfo("field1", new DataTypeInfo("int")));
        fields.add(new FieldInfo("field2", new DataTypeInfo("boolean")));
        boolean callSuper = true;
        String firstPrime = "7";
        String secondPrime = "31";
        PojoTemplate instance = new PojoTemplateImpl();
        instance.writeHashCode(appender, fields, callSuper, firstPrime, secondPrime);
        Assert.assertEquals("    @Override\n"
                + "    public int hashCode() {\n"
                + "        int hash = 7;\n"
                + "        hash = 31 * hash + super.hashCode();\n"
                + "        hash = 31 * hash + this.field1;\n"
                + "        hash = 31 * hash + (this.field2 ? 1 : 0);\n"
                + "        return hash;\n"
                + "    }\n", appender.toString());

    }

    @Test
    public void testWriteHashCodeWithTransientFields() throws Exception {
        Appendable appender = new StringBuilder();
        ArrayList<FieldInfo> fields = new ArrayList<FieldInfo>();
        FieldInfo field1 = new FieldInfo("field1", new DataTypeInfo("int"));
        field1.setMarkAsTransient(true);
        fields.add(field1);
        fields.add(new FieldInfo("field2", new DataTypeInfo("boolean")));
        boolean callSuper = false;
        String firstPrime = "7";
        String secondPrime = "31";
        PojoTemplate instance = new PojoTemplateImpl();
        instance.writeHashCode(appender, fields, callSuper, firstPrime, secondPrime);
        Assert.assertEquals("    @Override\n"
                + "    public int hashCode() {\n"
                + "        int hash = 7;\n"
                + "        hash = 31 * hash + (this.field2 ? 1 : 0);\n"
                + "        return hash;\n"
                + "    }\n", appender.toString());

    }

    @Test
    public void testWriteHashCodeWithoutFields() throws Exception {
        Appendable appender = new StringBuilder();
        ArrayList<FieldInfo> fields = new ArrayList<FieldInfo>();
        boolean callSuper = false;
        String firstPrime = "7";
        String secondPrime = "31";
        PojoTemplate instance = new PojoTemplateImpl();
        instance.writeHashCode(appender, fields, callSuper, firstPrime, secondPrime);
        Assert.assertEquals("    @Override\n"
                + "    public int hashCode() {\n"
                + "        int hash = 7;\n"
                + "        hash = 31 * hash + super.hashCode();\n"
                + "        return hash;\n"
                + "    }\n", appender.toString());

    }

    public class PojoTemplateImpl extends PojoTemplate {

        @Override
        protected void writeContent(Appendable appender) throws IOException {
            throw new UnsupportedOperationException("No needed for test");
        }
    }
}