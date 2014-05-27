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
import java.io.InputStream;
import java.util.Scanner;
import junit.framework.Assert;
import org.junit.Test;
import org.uaithne.generator.commons.DataTypeInfo;

public class ClassTemplateTest {

    @Test
    public void testWriteSimpleClassWithDoc() throws Exception {
        Appendable appender = new StringBuilder();
        ClassTemplate instance = new ClassTemplateImpl();
        instance.setClassName("ClassTemplateTestResult1");
        instance.setPackageName("org.uaithne.generator.templates");
        instance.setDocumentation(new String[]{"Documentation", "of the class"});
        instance.write(appender);
        String expected = loadResult("ClassTemplateTestResult1");
        Assert.assertEquals(expected, appender.toString());
    }

    @Test
    public void testWriteSimpleAbstractClass() throws Exception {
        Appendable appender = new StringBuilder();
        ClassTemplate instance = new ClassTemplateImpl();
        instance.setClassName("ClassTemplateTestResult2");
        instance.setPackageName("org.uaithne.generator.templates");
        instance.setAbstract(true);
        instance.write(appender);
        String expected = loadResult("ClassTemplateTestResult2");
        Assert.assertEquals(expected, appender.toString());
    }

    @Test
    public void testWriteSimpleInterface() throws Exception {
        Appendable appender = new StringBuilder();
        ClassTemplate instance = new ClassTemplateImpl();
        instance.setClassName("ClassTemplateTestResult3");
        instance.setPackageName("org.uaithne.generator.templates");
        instance.setInterface(true);
        instance.write(appender);
        String expected = loadResult("ClassTemplateTestResult3");
        Assert.assertEquals(expected, appender.toString());
    }

    @Test
    public void testWriteSimpleClassWithImports() throws Exception {
        Appendable appender = new StringBuilder();
        ClassTemplate instance = new ClassTemplateImpl();
        instance.setClassName("ClassTemplateTestResult4");
        instance.setPackageName("org.uaithne.generator.templates");
        instance.addImport(DataTypeInfo.LIST_DATA_TYPE, "org.uaithne.generator.templates");
        instance.addImport("java.util.Date", "org.uaithne.generator.templates");
        instance.write(appender);
        String expected = loadResult("ClassTemplateTestResult4");
        Assert.assertEquals(expected, appender.toString());
    }

    @Test
    public void testWriteClassWithGenericsArguments() throws Exception {
        Appendable appender = new StringBuilder();
        ClassTemplate instance = new ClassTemplateImpl();
        instance.setClassName("ClassTemplateTestResult5");
        instance.setPackageName("org.uaithne.generator.templates");
        instance.addGenericArgument("Integer");
        instance.addGenericArgument("Double");
        instance.write(appender);
        String expected = loadResult("ClassTemplateTestResult5");
        Assert.assertEquals(expected, appender.toString());
    }

    @Test
    public void testWriteSubClass() throws Exception {
        Appendable appender = new StringBuilder();
        ClassTemplate instance = new ClassTemplateImpl();
        instance.setClassName("ClassTemplateTestResult6");
        instance.setPackageName("org.uaithne.generator.templates");
        instance.setExtend("Object");
        instance.write(appender);
        String expected = loadResult("ClassTemplateTestResult6");
        Assert.assertEquals(expected, appender.toString());
    }

    @Test
    public void testWriteClassImplementsInterfaces() throws Exception {
        Appendable appender = new StringBuilder();
        ClassTemplate instance = new ClassTemplateImpl();
        instance.setClassName("ClassTemplateTestResult7");
        instance.setPackageName("org.uaithne.generator.templates");
        instance.addImplement("java.io.Serializable");
        instance.addImplement("Cloneable");
        instance.write(appender);
        String expected = loadResult("ClassTemplateTestResult7");
        Assert.assertEquals(expected, appender.toString());
    }

    @Test
    public void testWriteSubInterface() throws Exception {
        Appendable appender = new StringBuilder();
        ClassTemplate instance = new ClassTemplateImpl();
        instance.setClassName("ClassTemplateTestResult8");
        instance.setPackageName("org.uaithne.generator.templates");
        instance.setInterface(true);
        instance.addImplement("java.io.Serializable");
        instance.addImplement("Cloneable");
        instance.write(appender);
        String expected = loadResult("ClassTemplateTestResult8");
        Assert.assertEquals(expected, appender.toString());
    }

    @Test
    public void testWriteDeprecatedClass() throws Exception {
        Appendable appender = new StringBuilder();
        ClassTemplate instance = new ClassTemplateImpl();
        instance.setClassName("ClassTemplateTestResult9");
        instance.setPackageName("org.uaithne.generator.templates");
        instance.setDeprecated(true);
        instance.write(appender);
        String expected = loadResult("ClassTemplateTestResult9");
        Assert.assertEquals(expected, appender.toString());
    }

    String loadResult(String template) {
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("org/uaithne/generator/templates/" + template + ".java");
        Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    public class ClassTemplateImpl extends ClassTemplate {

        public void writeContent(Appendable appender) throws IOException {
        }
    }
}