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
import org.uaithne.generator.commons.FieldInfo;

public abstract class PojoTemplate extends WithFieldsTemplate {

    protected void writeDocumentation(Appendable appender, String[] doc) throws IOException {
        if (doc != null) {
            appender.append("    /**\n");
            for (String s : doc) {
                appender.append("     * ").append(s).append("\n");
            }
            appender.append("     */\n");
        }
    }

    protected void writeGetterDocumentation(Appendable appender, String[] doc, String fieldName) throws IOException {

        appender.append("    /**\n");
        if (doc != null) {
            for (String s : doc) {
                appender.append("     * ").append(s).append("\n");
            }
        }
        appender.append("     * @return the ");
        appender.append(fieldName);
        appender.append("\n");
        appender.append("     */\n");
    }

    protected void writeSetterDocumentation(Appendable appender, String[] doc, String fieldName) throws IOException {

        appender.append("    /**\n");
        if (doc != null) {
            for (String s : doc) {
                appender.append("     * ").append(s).append("\n");
            }
        }
        appender.append("     * @param ");
        appender.append(fieldName);
        appender.append(" the ");
        appender.append(fieldName);
        appender.append(" to set\n");
        appender.append("     */\n");
    }

    protected void writeField(Appendable appender, FieldInfo field) throws IOException {
        writeDocumentation(appender, field.getDocumentation());
        appender.append("    private ");
        if (field.isMarkAsTransient()) {
            appender.append("transient ");
        }
        appender.append(field.getDataType().getSimpleName());
        appender.append(" ");
        appender.append(field.getName());
        String defaultValue = field.getDefaultValue();
        if (defaultValue != null && !defaultValue.isEmpty()) {
            appender.append(" = ");
            appender.append(field.getDefaultValue());
        }
        appender.append(";\n");
    }

    protected void writeFieldGetter(Appendable appender, FieldInfo field) throws IOException {
        writeGetterDocumentation(appender, field.getDocumentation(), field.getName());
        if (field.isMarkAsOvwrride()) {
            appender.append("    @Override\n");
        }
        if (field.isDeprecated()) {
            appender.append("    @Deprecated");
        }

        appender.append("    public ").append(field.getDataType().getSimpleName());
        if (field.getDataType().isPrimitiveBoolean()) {
            appender.append(" is");
        } else {
            appender.append(" get");
        }
        appender.append(field.getCapitalizedName()).append("() {\n");
        appender.append("        return ").append(field.getName()).append(";\n");
        appender.append("    }\n");
    }

    protected void writeFieldSetter(Appendable appender, FieldInfo field) throws IOException {
        writeSetterDocumentation(appender, field.getDocumentation(), field.getName());
        if (field.isMarkAsOvwrride()) {
            appender.append("    @Override\n");
        }
        if (field.isDeprecated()) {
            appender.append("    @Deprecated");
        }
        appender.append("    public void set").append(field.getCapitalizedName()).append("(").append(field.getDataType().getSimpleName()).append(" ").append(field.getName()).append(") {\n");
        appender.append("        this.").append(field.getName()).append(" = ").append(field.getName()).append(";\n");
        appender.append("    }\n");
    }

    protected void writeFieldsInitialization(Appendable appender, ArrayList<FieldInfo> fields) throws IOException {
        for (FieldInfo field : fields) {
            appender.append("        this.").append(field.getName()).append(" = ").append(field.getName()).append(";\n");
        }
    }

    protected void writeToString(Appendable appender, ArrayList<FieldInfo> fields, boolean callSuper) throws IOException {
        appender.append("    @Override\n"
                + "    public String toString(){\n"
                + "        return \"").append(getClassName()).append("{\" +\n");

        boolean requireSeparator = false;
        for (FieldInfo field : fields) {
            if (requireSeparator) {
                appender.append("\"| \" +\n");
            } else {
                requireSeparator = true;
            }
            appender.append("            \"").append(field.getName()).append("=\" + ").append(field.getName()).append(" + ");
        }
        if (callSuper) {
            appender.append("\"| \" +\n"
                    + "            \"superclass=\" + super.toString() +");
            requireSeparator = true;
        }
        if (requireSeparator) {
            appender.append("\n");
        }
        appender.append("            \"}\";\n"
                + "    }\n");
    }

    protected void writeEquals(Appendable appender, ArrayList<FieldInfo> fields, boolean callSuper) throws IOException {
        appender.append("    @Override\n"
                + "    public boolean equals(Object obj) {\n"
                + "        if (obj == null) {\n"
                + "            return false;\n"
                + "        }\n"
                + "        if (getClass() != obj.getClass()) {\n"
                + "            return false;\n"
                + "        }\n");

        ArrayList<FieldInfo> filteredFields = new ArrayList<FieldInfo>(fields.size());
        for (FieldInfo field : fields) {
            if (!field.isMarkAsTransient()) {
                filteredFields.add(field);
            }
        }
        
        if (callSuper || filteredFields.isEmpty()) {
            appender.append("        if (!super.equals(obj)) {\n"
                    + "            return false;\n"
                    + "        }\n");
        }

        if (!filteredFields.isEmpty()) {
            appender.append("        final ").append(getClassName()).append(" other = (").append(getClassName()).append(") obj;\n");
            for (FieldInfo field : filteredFields) {
                appender.append("        if (").append(field.generateEqualsRule()).append(") {\n"
                        + "            return false;\n"
                        + "        }\n");
            }
        }
        
        appender.append("        return true;\n"
                + "    }\n");
    }

    protected void writeHashCode(Appendable appender, ArrayList<FieldInfo> fields, boolean callSuper, String firstPrime, String secondPrime) throws IOException {
        appender.append("    @Override\n"
                + "    public int hashCode() {\n"
                + "        int hash = ").append(firstPrime).append(";\n");
        
        ArrayList<FieldInfo> filteredFields = new ArrayList<FieldInfo>(fields.size());
        for (FieldInfo field : fields) {
            if (!field.isMarkAsTransient()) {
                filteredFields.add(field);
            }
        }
        
        if (callSuper || filteredFields.isEmpty()) {
            appender.append("        hash = ").append(secondPrime).append(" * hash + super.hashCode();\n");
        }
        
        for (FieldInfo field : filteredFields) {
            appender.append("        hash = ").append(secondPrime).append(" * hash + ").append(field.generateHashCodeRule()).append(";\n");
        }
        
        appender.append("        return hash;\n"
                + "    }\n");
    }
}
