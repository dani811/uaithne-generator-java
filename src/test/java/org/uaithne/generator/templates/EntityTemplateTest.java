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
import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.Assert;
import org.junit.Test;
import static org.junit.Assert.*;
import org.uaithne.generator.commons.DataTypeInfo;
import org.uaithne.generator.commons.EntityInfo;
import org.uaithne.generator.commons.EntityKind;
import org.uaithne.generator.commons.FieldInfo;

public class EntityTemplateTest {

    @Test
    public void testWriteEntityTemplateWithImports() {
        Appendable appender = new StringBuilder();
        EntityInfo entity = getEntityInfoTest();

        // Another field that needs import
        FieldInfo field5 = new FieldInfo("field5", new DataTypeInfo("java.io", "StringWriter"));
        entity.addField(field5);

        EntityTemplate instance = new EntityTemplate(entity);
        try {
            instance.write(appender);
        } catch (IOException ex) {
            fail(ex.toString());
            Logger.getLogger(EntityTemplateTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        Assert.assertEquals("package uaithne.tests;\n"
                + "\n"
                + "import java.io.StringWriter;\n"
                + "import java.util.Date;\n"
                + "\n"
                + "public class Test {\n"
                + "\n"
                + "    private int field1;\n"
                + "    private boolean field2;\n"
                + "    private double field3;\n"
                + "    private Date field4;\n"
                + "    private StringWriter field5;\n"
                + "\n"
                + "    /**\n"
                + "     * @return the field1\n"
                + "     */\n"
                + "    public int getField1() {\n"
                + "        return field1;\n"
                + "    }\n"
                + "\n"
                + "    /**\n"
                + "     * @param field1 the field1 to set\n"
                + "     */\n"
                + "    public void setField1(int field1) {\n"
                + "        this.field1 = field1;\n"
                + "    }\n"
                + "\n"
                + "    /**\n"
                + "     * @return the field2\n"
                + "     */\n"
                + "    public boolean isField2() {\n"
                + "        return field2;\n"
                + "    }\n"
                + "\n"
                + "    /**\n"
                + "     * @param field2 the field2 to set\n"
                + "     */\n"
                + "    public void setField2(boolean field2) {\n"
                + "        this.field2 = field2;\n"
                + "    }\n"
                + "\n"
                + "    /**\n"
                + "     * @return the field3\n"
                + "     */\n"
                + "    public double getField3() {\n"
                + "        return field3;\n"
                + "    }\n"
                + "\n"
                + "    /**\n"
                + "     * @param field3 the field3 to set\n"
                + "     */\n"
                + "    public void setField3(double field3) {\n"
                + "        this.field3 = field3;\n"
                + "    }\n"
                + "\n"
                + "    /**\n"
                + "     * @return the field4\n"
                + "     */\n"
                + "    public Date getField4() {\n"
                + "        return field4;\n"
                + "    }\n"
                + "\n"
                + "    /**\n"
                + "     * @param field4 the field4 to set\n"
                + "     */\n"
                + "    public void setField4(Date field4) {\n"
                + "        this.field4 = field4;\n"
                + "    }\n"
                + "\n"
                + "    /**\n"
                + "     * @return the field5\n"
                + "     */\n"
                + "    public StringWriter getField5() {\n"
                + "        return field5;\n"
                + "    }\n"
                + "\n"
                + "    /**\n"
                + "     * @param field5 the field5 to set\n"
                + "     */\n"
                + "    public void setField5(StringWriter field5) {\n"
                + "        this.field5 = field5;\n"
                + "    }\n"
                + "\n"
                + "    @Override\n"
                + "    public String toString(){\n"
                + "        return \"Test{\" +\n"
                + "            \"field1=\" + field1 + \"| \" +\n"
                + "            \"field2=\" + field2 + \"| \" +\n"
                + "            \"field3=\" + field3 + \"| \" +\n"
                + "            \"field4=\" + field4 + \"| \" +\n"
                + "            \"field5=\" + field5 + \n"
                + "            \"}\";\n"
                + "    }\n"
                + "\n"
                + "    @Override\n"
                + "    public boolean equals(Object obj) {\n"
                + "        if (this == obj) {\n"
                + "            return true;\n"
                + "        }\n"
                + "        if (obj == null) {\n"
                + "            return false;\n"
                + "        }\n"
                + "        if (getClass() != obj.getClass()) {\n"
                + "            return false;\n"
                + "        }\n"
                + "        final Test other = (Test) obj;\n"
                + "        if (this.field1 != other.field1) {\n"
                + "            return false;\n"
                + "        }\n"
                + "        return true;\n"
                + "    }\n"
                + "\n"
                + "    @Override\n"
                + "    public int hashCode() {\n"
                + "        int hash = 3;\n"
                + "        hash = 71 * hash + this.field1;\n"
                + "        return hash;\n"
                + "    }\n"
                + "\n"
                + "    public Test() {\n"
                + "    }\n"
                + "\n"
                + "    public Test(Date field4, StringWriter field5) {\n"
                + "        this.field4 = field4;\n"
                + "        this.field5 = field5;\n"
                + "    }\n"
                + "\n"
                + "    public Test(int field1, Date field4, StringWriter field5) {\n"
                + "        this.field1 = field1;\n"
                + "        this.field4 = field4;\n"
                + "        this.field5 = field5;\n"
                + "    }\n"
                + "\n"
                + "}", appender.toString());

    }

    @Test
    public void testWriteEntityTemplateWithDocumentation() {
        Appendable appender = new StringBuilder();
        EntityInfo entity = new EntityInfo(new DataTypeInfo("uaithne.tests", "Test"), EntityKind.ENTITY);
        entity.setDocumentation(new String[]{"Documentation", "for the", "class"});
        EntityTemplate instance = new EntityTemplate(entity);
        try {
            instance.write(appender);
        } catch (IOException ex) {
            fail(ex.toString());
            Logger.getLogger(EntityTemplateTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        Assert.assertEquals("package uaithne.tests;\n"
                + "\n"
                + "/**\n"
                + " * Documentation\n"
                + " * for the\n"
                + " * class\n"
                + " */\n"
                + "public class Test {\n"
                + "\n"
                + "\n"
                + "    @Override\n"
                + "    public String toString(){\n"
                + "        return \"Test{\" +\n"
                + "            \"}\";\n"
                + "    }\n"
                + "\n"
                + "    @Override\n"
                + "    public boolean equals(Object obj) {\n"
                + "        if (this == obj) {\n"
                + "            return true;\n"
                + "        }\n"
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
                + "    }\n"
                + "\n"
                + "    @Override\n"
                + "    public int hashCode() {\n"
                + "        int hash = 3;\n"
                + "        hash = 71 * hash + super.hashCode();\n"
                + "        return hash;\n"
                + "    }\n"
                + "\n"
                + "    public Test() {\n"
                + "    }\n"
                + "\n"
                + "}", appender.toString());
    }

    @Test
    public void testWriteEntityTemplateWithExtend() {
        Appendable appender = new StringBuilder();
        EntityInfo entity = new EntityInfo(new DataTypeInfo("uaithne.tests", "Test"), EntityKind.ENTITY);
        entity.setExtend(new DataTypeInfo("java.util", "Date"));
        EntityTemplate instance = new EntityTemplate(entity);
        try {
            instance.write(appender);
        } catch (IOException ex) {
            fail(ex.toString());
            Logger.getLogger(EntityTemplateTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        Assert.assertEquals("package uaithne.tests;\n"
                + "\n"
                + "import java.util.Date;\n"
                + "\n"
                + "public class Test extends Date {\n"
                + "\n"
                + "\n"
                + "    @Override\n"
                + "    public String toString(){\n"
                + "        return \"Test{\" +\n"
                + "            \"superclass=\" + super.toString() +\n"
                + "            \"}\";\n"
                + "    }\n"
                + "\n"
                + "    @Override\n"
                + "    public boolean equals(Object obj) {\n"
                + "        if (this == obj) {\n"
                + "            return true;\n"
                + "        }\n"
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
                + "    }\n"
                + "\n"
                + "    @Override\n"
                + "    public int hashCode() {\n"
                + "        int hash = 3;\n"
                + "        hash = 71 * hash + super.hashCode();\n"
                + "        return hash;\n"
                + "    }\n"
                + "\n"
                + "    public Test() {\n"
                + "    }\n"
                + "\n"
                + "}", appender.toString());
    }

    @Test
    public void testWriteEntityTemplateWithImplements() {
        Appendable appender = new StringBuilder();
        EntityInfo entity = new EntityInfo(new DataTypeInfo("uaithne.tests", "Test"), EntityKind.ENTITY);
        entity.addImplement(new DataTypeInfo("java.io", "Serializable"));
        entity.addImplement(new DataTypeInfo("java.lang", "Cloneable"));
        EntityTemplate instance = new EntityTemplate(entity);
        try {
            instance.write(appender);
        } catch (IOException ex) {
            fail(ex.toString());
            Logger.getLogger(EntityTemplateTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        Assert.assertEquals("package uaithne.tests;\n"
                + "\n"
                + "import java.io.Serializable;\n"
                + "\n"
                + "public class Test implements Cloneable, Serializable {\n"
                + "\n"
                + "\n"
                + "    @Override\n"
                + "    public String toString(){\n"
                + "        return \"Test{\" +\n"
                + "            \"}\";\n"
                + "    }\n"
                + "\n"
                + "    @Override\n"
                + "    public boolean equals(Object obj) {\n"
                + "        if (this == obj) {\n"
                + "            return true;\n"
                + "        }\n"
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
                + "    }\n"
                + "\n"
                + "    @Override\n"
                + "    public int hashCode() {\n"
                + "        int hash = 3;\n"
                + "        hash = 71 * hash + super.hashCode();\n"
                + "        return hash;\n"
                + "    }\n"
                + "\n"
                + "    public Test() {\n"
                + "    }\n"
                + "\n"
                + "}", appender.toString());
    }

    @Test
    public void testWriteDeprecatedEntityTemplate() {
        Appendable appender = new StringBuilder();
        EntityInfo entity = new EntityInfo(new DataTypeInfo("uaithne.tests", "Test"), EntityKind.ENTITY);
        entity.setDeprecated(true);
        EntityTemplate instance = new EntityTemplate(entity);
        try {
            instance.write(appender);
        } catch (IOException ex) {
            fail(ex.toString());
            Logger.getLogger(EntityTemplateTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        Assert.assertEquals("package uaithne.tests;\n"
                + "\n"
                + "@Deprecated\n"
                + "public class Test {\n"
                + "\n"
                + "\n"
                + "    @Override\n"
                + "    public String toString(){\n"
                + "        return \"Test{\" +\n"
                + "            \"}\";\n"
                + "    }\n"
                + "\n"
                + "    @Override\n"
                + "    public boolean equals(Object obj) {\n"
                + "        if (this == obj) {\n"
                + "            return true;\n"
                + "        }\n"
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
                + "    }\n"
                + "\n"
                + "    @Override\n"
                + "    public int hashCode() {\n"
                + "        int hash = 3;\n"
                + "        hash = 71 * hash + super.hashCode();\n"
                + "        return hash;\n"
                + "    }\n"
                + "\n"
                + "    public Test() {\n"
                + "    }\n"
                + "\n"
                + "}", appender.toString());
    }

    @Test
    public void testWriteEmptyConstructor() throws Exception {
        Appendable appender = new StringBuilder();
        EntityInfo entity = new EntityInfo(new DataTypeInfo("uaithne.tests", "Test"), EntityKind.ENTITY);
        EntityTemplate instance = new EntityTemplate(entity);
        instance.writeConstructors(appender);
        Assert.assertEquals("    public Test() {\n"
                + "    }", appender.toString());
    }

    @Test
    public void testWriteConstructorWithFields() throws Exception {
        Appendable appender = new StringBuilder();
        EntityInfo entity = getEntityInfoTest();

        EntityTemplate instance = new EntityTemplate(entity);
        instance.writeConstructors(appender);
        Assert.assertEquals("    public Test() {\n"
                + "    }\n"
                + "\n"
                + "    public Test(Date field4) {\n"
                + "        this.field4 = field4;\n"
                + "    }\n"
                + "\n"
                + "    public Test(int field1, Date field4) {\n"
                + "        this.field1 = field1;\n"
                + "        this.field4 = field4;\n"
                + "    }", appender.toString());
    }

    @Test
    public void testWriteConstructorWithCombinedParentFields() throws Exception {
        Appendable appender = new StringBuilder();

        EntityInfo entity = getEntityInfoTest();
        entity.combineWithParent(getParentEntityInfoTest());

        EntityTemplate instance = new EntityTemplate(entity);
        instance.writeConstructors(appender);
        Assert.assertEquals("    public Test() {\n"
                + "    }\n"
                + "\n"
                + "    public Test(Date field4p, Date field4) {\n"
                + "        super(field4p);\n"
                + "        this.field4 = field4;\n"
                + "    }\n"
                + "\n"
                + "    public Test(int field1p, Date field4p, int field1, Date field4) {\n"
                + "        super(field1p, field4p);\n"
                + "        this.field1 = field1;\n"
                + "        this.field4 = field4;\n"
                + "    }", appender.toString());
    }

    private EntityInfo getEntityInfoTest() {
        EntityInfo entity = new EntityInfo(new DataTypeInfo("uaithne.tests", "Test"), EntityKind.ENTITY);

        // Identifier field
        FieldInfo field1 = new FieldInfo("field1", new DataTypeInfo("int"));
        field1.setIdentifier(true);
        entity.addField(field1);

        // Optional field
        FieldInfo field2 = new FieldInfo("field2", new DataTypeInfo("boolean"));
        field2.setOptional(true);
        entity.addField(field2);

        // Excluded from constructor field
        FieldInfo field3 = new FieldInfo("field3", new DataTypeInfo("double"));
        field3.setExcludedFromConstructor(true);
        entity.addField(field3);

        // Normal field
        FieldInfo field4 = new FieldInfo("field4", new DataTypeInfo("java.util", "Date"));
        entity.addField(field4);

        return entity;
    }

    private EntityInfo getParentEntityInfoTest() {
        EntityInfo entity = new EntityInfo(new DataTypeInfo("uaithne.tests", "SuperTest"), EntityKind.ENTITY);

        // Identifier field
        FieldInfo field1 = new FieldInfo("field1p", new DataTypeInfo("int"));
        field1.setIdentifier(true);
        entity.addField(field1);

        // Optional field
        FieldInfo field2 = new FieldInfo("field2p", new DataTypeInfo("boolean"));
        field2.setOptional(true);
        entity.addField(field2);

        // Excluded from constructor field
        FieldInfo field3 = new FieldInfo("field3p", new DataTypeInfo("double"));
        field3.setExcludedFromConstructor(true);
        entity.addField(field3);

        // Normal field
        FieldInfo field4 = new FieldInfo("field4p", new DataTypeInfo("java.util", "Date"));
        entity.addField(field4);

        return entity;
    }

    @Test
    public void testWriteContent() throws Exception {
        Appendable appender = new StringBuilder();
        EntityInfo entity = getEntityInfoTest();
        EntityTemplate instance = new EntityTemplate(entity);
        instance.writeContent(appender);
        Assert.assertEquals("    private int field1;\n"
                + "    private boolean field2;\n"
                + "    private double field3;\n"
                + "    private Date field4;\n"
                + "\n"
                + "    /**\n"
                + "     * @return the field1\n"
                + "     */\n"
                + "    public int getField1() {\n"
                + "        return field1;\n"
                + "    }\n"
                + "\n"
                + "    /**\n"
                + "     * @param field1 the field1 to set\n"
                + "     */\n"
                + "    public void setField1(int field1) {\n"
                + "        this.field1 = field1;\n"
                + "    }\n"
                + "\n"
                + "    /**\n"
                + "     * @return the field2\n"
                + "     */\n"
                + "    public boolean isField2() {\n"
                + "        return field2;\n"
                + "    }\n"
                + "\n"
                + "    /**\n"
                + "     * @param field2 the field2 to set\n"
                + "     */\n"
                + "    public void setField2(boolean field2) {\n"
                + "        this.field2 = field2;\n"
                + "    }\n"
                + "\n"
                + "    /**\n"
                + "     * @return the field3\n"
                + "     */\n"
                + "    public double getField3() {\n"
                + "        return field3;\n"
                + "    }\n"
                + "\n"
                + "    /**\n"
                + "     * @param field3 the field3 to set\n"
                + "     */\n"
                + "    public void setField3(double field3) {\n"
                + "        this.field3 = field3;\n"
                + "    }\n"
                + "\n"
                + "    /**\n"
                + "     * @return the field4\n"
                + "     */\n"
                + "    public Date getField4() {\n"
                + "        return field4;\n"
                + "    }\n"
                + "\n"
                + "    /**\n"
                + "     * @param field4 the field4 to set\n"
                + "     */\n"
                + "    public void setField4(Date field4) {\n"
                + "        this.field4 = field4;\n"
                + "    }\n"
                + "\n"
                + "    @Override\n"
                + "    public String toString(){\n"
                + "        return \"Test{\" +\n"
                + "            \"field1=\" + field1 + \"| \" +\n"
                + "            \"field2=\" + field2 + \"| \" +\n"
                + "            \"field3=\" + field3 + \"| \" +\n"
                + "            \"field4=\" + field4 + \n"
                + "            \"}\";\n"
                + "    }\n"
                + "\n"
                + "    @Override\n"
                + "    public boolean equals(Object obj) {\n"
                + "        if (this == obj) {\n"
                + "            return true;\n"
                + "        }\n"
                + "        if (obj == null) {\n"
                + "            return false;\n"
                + "        }\n"
                + "        if (getClass() != obj.getClass()) {\n"
                + "            return false;\n"
                + "        }\n"
                + "        final Test other = (Test) obj;\n"
                + "        if (this.field1 != other.field1) {\n"
                + "            return false;\n"
                + "        }\n"
                + "        return true;\n"
                + "    }\n"
                + "\n"
                + "    @Override\n"
                + "    public int hashCode() {\n"
                + "        int hash = 3;\n"
                + "        hash = 71 * hash + this.field1;\n"
                + "        return hash;\n"
                + "    }\n"
                + "\n"
                + "    public Test() {\n"
                + "    }\n"
                + "\n"
                + "    public Test(Date field4) {\n"
                + "        this.field4 = field4;\n"
                + "    }\n"
                + "\n"
                + "    public Test(int field1, Date field4) {\n"
                + "        this.field1 = field1;\n"
                + "        this.field4 = field4;\n"
                + "    }", appender.toString());
    }

    @Test
    public void testWriteContentWithExtend() throws Exception {
        Appendable appender = new StringBuilder();
        EntityInfo entity = getEntityInfoTest();
        entity.setExtend(new DataTypeInfo("java.util", "Date"));
        EntityTemplate instance = new EntityTemplate(entity);
        instance.writeContent(appender);

        Assert.assertEquals("    private int field1;\n"
                + "    private boolean field2;\n"
                + "    private double field3;\n"
                + "    private Date field4;\n"
                + "\n"
                + "    /**\n"
                + "     * @return the field1\n"
                + "     */\n"
                + "    public int getField1() {\n"
                + "        return field1;\n"
                + "    }\n"
                + "\n"
                + "    /**\n"
                + "     * @param field1 the field1 to set\n"
                + "     */\n"
                + "    public void setField1(int field1) {\n"
                + "        this.field1 = field1;\n"
                + "    }\n"
                + "\n"
                + "    /**\n"
                + "     * @return the field2\n"
                + "     */\n"
                + "    public boolean isField2() {\n"
                + "        return field2;\n"
                + "    }\n"
                + "\n"
                + "    /**\n"
                + "     * @param field2 the field2 to set\n"
                + "     */\n"
                + "    public void setField2(boolean field2) {\n"
                + "        this.field2 = field2;\n"
                + "    }\n"
                + "\n"
                + "    /**\n"
                + "     * @return the field3\n"
                + "     */\n"
                + "    public double getField3() {\n"
                + "        return field3;\n"
                + "    }\n"
                + "\n"
                + "    /**\n"
                + "     * @param field3 the field3 to set\n"
                + "     */\n"
                + "    public void setField3(double field3) {\n"
                + "        this.field3 = field3;\n"
                + "    }\n"
                + "\n"
                + "    /**\n"
                + "     * @return the field4\n"
                + "     */\n"
                + "    public Date getField4() {\n"
                + "        return field4;\n"
                + "    }\n"
                + "\n"
                + "    /**\n"
                + "     * @param field4 the field4 to set\n"
                + "     */\n"
                + "    public void setField4(Date field4) {\n"
                + "        this.field4 = field4;\n"
                + "    }\n"
                + "\n"
                + "    @Override\n"
                + "    public String toString(){\n"
                + "        return \"Test{\" +\n"
                + "            \"field1=\" + field1 + \"| \" +\n"
                + "            \"field2=\" + field2 + \"| \" +\n"
                + "            \"field3=\" + field3 + \"| \" +\n"
                + "            \"field4=\" + field4 + \"| \" +\n"
                + "            \"superclass=\" + super.toString() +\n"
                + "            \"}\";\n"
                + "    }\n"
                + "\n"
                + "    @Override\n"
                + "    public boolean equals(Object obj) {\n"
                + "        if (this == obj) {\n"
                + "            return true;\n"
                + "        }\n"
                + "        if (obj == null) {\n"
                + "            return false;\n"
                + "        }\n"
                + "        if (getClass() != obj.getClass()) {\n"
                + "            return false;\n"
                + "        }\n"
                + "        if (!super.equals(obj)) {\n"
                + "            return false;\n"
                + "        }\n"
                + "        final Test other = (Test) obj;\n"
                + "        if (this.field1 != other.field1) {\n"
                + "            return false;\n"
                + "        }\n"
                + "        return true;\n"
                + "    }\n"
                + "\n"
                + "    @Override\n"
                + "    public int hashCode() {\n"
                + "        int hash = 3;\n"
                + "        hash = 71 * hash + super.hashCode();\n"
                + "        hash = 71 * hash + this.field1;\n"
                + "        return hash;\n"
                + "    }\n"
                + "\n"
                + "    public Test() {\n"
                + "    }\n"
                + "\n"
                + "    public Test(Date field4) {\n"
                + "        this.field4 = field4;\n"
                + "    }\n"
                + "\n"
                + "    public Test(int field1, Date field4) {\n"
                + "        this.field1 = field1;\n"
                + "        this.field4 = field4;\n"
                + "    }", appender.toString());
    }
}