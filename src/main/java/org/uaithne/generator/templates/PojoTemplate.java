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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import org.uaithne.generator.commons.DataTypeInfo;
import org.uaithne.generator.commons.FieldInfo;
import org.uaithne.generator.commons.NamesGenerator;

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
        appendFieldBeanValidations(appender, field);
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
            appender.append("    @Deprecated\n");
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
            appender.append("    @Deprecated\n");
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
            if (field.isExcludedFromToString()) {
                continue;
            }
            if (requireSeparator) {
                appender.append("\"| \" +\n");
            } else {
                requireSeparator = true;
            }
            appender.append("            \"").append(field.getName()).append("=\" + ").append(field.getName()).append(" + ");
        }
        if (callSuper) {
            if (requireSeparator) {
                appender.append("\"| \" +\n");
            }
            appender.append("            \"superclass=\" + super.toString() +");
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

    protected void appendAnnotationImports(String currentPackage, HashSet<String> imports, FieldInfo field) {
        VariableElement element = field.getElement();
        if (element == null) {
            return;
        }

        boolean hasValidationAnnotation = false;
        DataTypeInfo validationAnnotation = field.getValidationAnnotation();
        for (AnnotationMirror annotation : element.getAnnotationMirrors()) {
            boolean hasAnnotation = appendAnnotationImports(currentPackage, imports, annotation, validationAnnotation, true);
            hasValidationAnnotation = hasValidationAnnotation || hasAnnotation;
        }
        if (!hasValidationAnnotation && validationAnnotation != null) {
            validationAnnotation.appendImports(currentPackage, imports);
        }
    }

    private boolean appendAnnotationImports(String currentPackage, HashSet<String> imports, AnnotationMirror annotation, DataTypeInfo validationAnnotation, boolean ignoreUaithne) {
        DataTypeInfo dataType = NamesGenerator.createDataTypeFor(annotation.getAnnotationType(), true);
        String packageName = dataType.getPackageName();

        if (ignoreUaithne && packageName != null && packageName.startsWith("org.uaithne.")) {
            return false;
        }
        dataType.appendImports(currentPackage, imports);
        for (AnnotationValue value : annotation.getElementValues().values()) {
            appendAnnotationImports(currentPackage, imports, value, validationAnnotation);
        }
        
        if (validationAnnotation == null) {
            return false;
        }

        boolean hasValidationAnnotation = validationAnnotation.getQualifiedNameWithoutGenerics().equals(dataType.getQualifiedName());
        return hasValidationAnnotation;
    }

    private void appendAnnotationImports(String currentPackage, HashSet<String> imports, AnnotationValue annotationValue, DataTypeInfo validationAnnotation) {
        Object value = annotationValue.getValue();
        if (value instanceof TypeMirror) {
            DataTypeInfo type = NamesGenerator.createDataTypeFor((TypeMirror) value, true);
            type.appendImports(currentPackage, imports);
        } else if (value instanceof VariableElement) {
            VariableElement variableElement = (VariableElement) value;
            DataTypeInfo type = NamesGenerator.createDataTypeFor(variableElement.asType(), true);
            type.appendImports(currentPackage, imports);
        } else if (value instanceof AnnotationMirror) {
            appendAnnotationImports(currentPackage, imports, (AnnotationMirror) value, validationAnnotation, false);
        } else if (value instanceof List) {
            List<? extends AnnotationValue> annotations = (List<? extends AnnotationValue>) value;
            for (AnnotationValue annotation : annotations) {
                appendAnnotationImports(currentPackage, imports, annotation, validationAnnotation);
            }
        }
    }

    private void appendAnnotationParamsContent(Appendable appender, AnnotationValue annotationValue, String ident) {
        Object value = annotationValue.getValue();
        try {

            if (value instanceof String) {
                appender.append("\"").append(value.toString().replace("\"", "\\\"")).append("\"");
            } else if (value instanceof VariableElement) {
                VariableElement variableElement = (VariableElement) value;
                DataTypeInfo type = NamesGenerator.createDataTypeFor(variableElement.asType(), true);
                appender.append(type.getSimpleName());
                appender.append(".");
                appender.append(variableElement.getSimpleName());
            } else if (value instanceof TypeMirror) {
                DataTypeInfo type = NamesGenerator.createDataTypeFor((TypeMirror) value, true);
                appender.append(type.getSimpleName());
                appender.append(".class");
            } else if (value instanceof AnnotationMirror) {
                AnnotationMirror annotation = ((AnnotationMirror) value);
                DataTypeInfo dataType = NamesGenerator.createDataTypeFor(annotation.getAnnotationType(), true);
                appendAnnotation(appender, dataType, annotation, ident + "    ", false);
            } else if (value instanceof List) {
                List<? extends AnnotationValue> annotations = (List<? extends AnnotationValue>) value;
                int listSize = annotations.size();
                if (listSize > 1) {
                    appender.append("{");
                }
                boolean requireComma = false;
                for (AnnotationValue annotation : annotations) {
                    if (requireComma) {
                        appender.append(", ");
                    }
                    appendAnnotationParamsContent(appender, annotation, ident);
                    requireComma = true;
                }
                if (listSize > 1) {
                    appender.append("}");
                }
            } else { // a wrapper class for a primitive type
                appender.append(value.toString());
            }
        } catch (IOException ex) {
            Logger.getLogger(PojoTemplate.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void appendFieldBeanValidations(Appendable appender, FieldInfo field) {
        VariableElement element = field.getElement();
        if (element == null) {
            return;
        }

        DataTypeInfo validationAnnotation = field.getValidationAnnotation();
        boolean hasValidationAnnotation = false;
        for (AnnotationMirror annotation : element.getAnnotationMirrors()) {
            DataTypeInfo dataType = NamesGenerator.createDataTypeFor(annotation.getAnnotationType(), true);
            String packageName = dataType.getPackageName();
            if (packageName != null && !packageName.startsWith("org.uaithne.")) {
                appendAnnotation(appender, dataType, annotation, "    ", true);
            }
            if (validationAnnotation != null && validationAnnotation.getPrintableQualifiedNameWithoutGenerics().equals(dataType.getQualifiedName())) {
                hasValidationAnnotation = true;
            }
        }

        if (!hasValidationAnnotation && validationAnnotation != null) {
            try {
                appender.append("    @");
                appender.append(validationAnnotation.getSimpleName());
                appender.append("\n");
            } catch (IOException ex) {
                Logger.getLogger(PojoTemplate.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void appendAnnotation(Appendable appender, DataTypeInfo dataType, AnnotationMirror annotation, String ident, boolean identFirstLevel) {
        try {
            if (!identFirstLevel) {
                appender.append("\n");
            }
            appender.append(ident);
            appender.append("@");
            appender.append(dataType.getSimpleName());

            int numberOfElements = annotation.getElementValues().size();
            if (numberOfElements != 0) {
                appender.append("(");
            }
            boolean requireComma = false;
            boolean hasOnlyOne = numberOfElements == 1;
            for (Map.Entry<? extends ExecutableElement, ? extends AnnotationValue> entry : annotation.getElementValues().entrySet()) {
                if (requireComma) {
                    appender.append(", ");
                }
                String name = entry.getKey().getSimpleName().toString();
                if (!"value".equals(name) || !hasOnlyOne) {
                    appender.append(name);
                    appender.append(" = ");
                }
                appendAnnotationParamsContent(appender, entry.getValue(), ident);
                requireComma = true;
            }
            if (numberOfElements != 0) {
                appender.append(")");
            }
            if (identFirstLevel) {
                appender.append("\n");
            }
        } catch (IOException ex) {
            Logger.getLogger(PojoTemplate.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
