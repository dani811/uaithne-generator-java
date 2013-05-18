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
package org.uaithne.generator.commons;

import java.util.List;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.WildcardType;
import org.uaithne.annotations.Entity;
import org.uaithne.annotations.EntityView;

public class NamesGenerator {

    public static String createPackageNameFromFullName(Name qualifiedName) {
        return createPackageNameFromFullName(qualifiedName.toString());
    }

    public static String createPackageNameFromFullName(String qualifiedName) {
        return qualifiedName.replaceAll("._", ".");
    }

    public static String createGenericPackageName(TypeElement element, String subpackageName) {
        String packageName = element.getQualifiedName().toString();
        int lastDot = packageName.lastIndexOf(".");
        if (lastDot >= 0 && lastDot < packageName.length()) {
            packageName = packageName.substring(0, lastDot);
        }
        if (packageName == null || packageName.isEmpty()) {
            packageName = subpackageName;
        } else {
            if (subpackageName != null) {
                packageName = packageName + "." + subpackageName;
            }
        }
        return createPackageNameFromFullName(packageName);
    }

    public static String createOperationsPackageName(TypeElement element) {
        String simpleName = Utils.dropFirstUnderscore(element.getSimpleName().toString());
        String packageName = element.getQualifiedName().toString();
        int lastDot = packageName.lastIndexOf(".");
        if (lastDot >= 0 && lastDot < packageName.length()) {
            packageName = packageName.substring(0, lastDot);
        }
        if (packageName == null || packageName.isEmpty()) {
            packageName = "operations";
        } else {
            packageName = packageName + ".operations";
        }
        packageName = createPackageNameFromFullName(packageName);
        return packageName + "." + Utils.firstLower(simpleName);
    }

    public static DataTypeInfo createOperationDataType(TypeElement element, String packageName) {
        // TODO: add generic params support
        String elementName = element.getSimpleName().toString();
        elementName = Utils.dropFirstUnderscore(elementName);
        String elementNameUpper = Utils.firstUpper(elementName);
        return new DataTypeInfo(packageName, elementNameUpper);
    }

    public static DataTypeInfo createEntityDataType(TypeElement element) {
        // TODO: add generic params support
        String simpleName = Utils.dropFirstUnderscore(element.getSimpleName().toString());
        String packageName = element.getQualifiedName().toString();
        int lastDot = packageName.lastIndexOf(".");
        if (lastDot >= 0 && lastDot < packageName.length()) {
            packageName = packageName.substring(0, lastDot);
        } else {
            packageName = "";
        }
        if (element.getEnclosingElement().getKind() != ElementKind.PACKAGE) {
            lastDot = packageName.lastIndexOf(".");
            if (lastDot >= 0 && lastDot < packageName.length()) {
                packageName = packageName.substring(0, lastDot);
            }
        }
        if (packageName == null || packageName.isEmpty()) {
            packageName = "model";
        } else {
            packageName = packageName + ".model";
        }
        packageName = createPackageNameFromFullName(packageName);

        return new DataTypeInfo(packageName, simpleName);
    }

    private static DataTypeInfo createEntityDataType(Class klass) {
        String simpleName = Utils.dropFirstUnderscore(klass.getSimpleName());
        String packageName = klass.getCanonicalName();
        int lastDot = packageName.lastIndexOf(".");
        if (lastDot >= 0 && lastDot < packageName.length()) {
            packageName = packageName.substring(0, lastDot);
        } else {
            packageName = "";
        }
        lastDot = packageName.lastIndexOf(".");
        if (lastDot >= 0 && lastDot < packageName.length()) {
            packageName = packageName.substring(0, lastDot);
        }
        if (packageName == null || packageName.isEmpty()) {
            packageName = "model";
        } else {
            packageName = packageName + ".model";
        }
        packageName = createPackageNameFromFullName(packageName);

        return new DataTypeInfo(packageName, simpleName);
    }

    @SuppressWarnings("unchecked")
    public static DataTypeInfo createResultDataType(Class klass) {
        if (klass.getAnnotation(Entity.class) != null) {
            return createEntityDataType(klass);
        } else if (klass.getAnnotation(EntityView.class) != null) {
            return createEntityDataType(klass);
        } else {
            return createDataType(klass);
        }
    }

    private static DataTypeInfo createResultDataType(TypeElement te) {
        if (te.getAnnotation(Entity.class) != null) {
            return createEntityDataType(te);
        } else if (te.getAnnotation(EntityView.class) != null) {
            return createEntityDataType(te);
        } else {
            return createDataType(te.getQualifiedName().toString());
        }
    }

    private static DataTypeInfo createDataType(Class klass) {
        DataTypeInfo mirror = handleMirrors(klass.getCanonicalName());
        if (mirror != null) {
            return mirror;
        }
        String simpleName = Utils.dropFirstUnderscore(klass.getSimpleName());
        String packageName = klass.getCanonicalName();
        int lastDot = packageName.lastIndexOf(".");
        if (lastDot >= 0 && lastDot < packageName.length()) {
            packageName = packageName.substring(0, lastDot);
        } else {
            packageName = "";
        }
        return new DataTypeInfo(packageName, simpleName);
    }

    private static DataTypeInfo createDataType(String className) {
        DataTypeInfo mirror = handleMirrors(className);
        if (mirror != null) {
            return mirror;
        }
        String simpleName;
        String packageName;
        int lastDot = className.lastIndexOf(".");
        if (lastDot >= 0 && lastDot < className.length()) {
            packageName = className.substring(0, lastDot);
            simpleName = Utils.dropFirstUnderscore(className.substring(lastDot + 1));
        } else {
            packageName = "";
            simpleName = Utils.dropFirstUnderscore(className);
        }
        return new DataTypeInfo(packageName, simpleName);
    }

    public static DataTypeInfo createGwtAccesorDataType(TypeElement element) {
        String simpleName = Utils.dropFirstUnderscore(element.getSimpleName().toString());
        String packageName = element.getQualifiedName().toString();
        int lastDot = packageName.lastIndexOf(".");
        if (lastDot >= 0 && lastDot < packageName.length()) {
            packageName = packageName.substring(0, lastDot);
        } else {
            packageName = "";
        }
        packageName = createPackageNameFromFullName(packageName);

        return new DataTypeInfo(packageName, simpleName);
    }

    public static DataTypeInfo createDataTypeFor(TypeMirror t) {
        if (t == null) {
            return null;
        }
        switch (t.getKind()) {
            case BOOLEAN: return new DataTypeInfo("boolean");
            case BYTE: return new DataTypeInfo("byte");
            case SHORT: return new DataTypeInfo("short");
            case INT: return new DataTypeInfo("int");
            case LONG: return new DataTypeInfo("long");
            case CHAR: return new DataTypeInfo("char");
            case FLOAT: return new DataTypeInfo("float");
            case DOUBLE: return new DataTypeInfo("double");
            case VOID: return new DataTypeInfo("void");
            case NONE: return new DataTypeInfo("/*none*/");
            case NULL: return new DataTypeInfo("null");
            case ARRAY: {
                    ArrayType at = (ArrayType) t;
                    DataTypeInfo result = createDataTypeFor(at.getComponentType());
                    result.setSimpleName(result.getSimpleName() + "[]");
                    return result;
                }
            case WILDCARD:
                if (t instanceof WildcardType) {
                    WildcardType wt = (WildcardType) t;
                    if (wt.getExtendsBound() != null) {
                        DataTypeInfo result = createDataTypeFor(wt.getExtendsBound());
                        result.setSimpleName("? extends " + result.getSimpleName());
                        return result;
                    } else if (wt.getSuperBound() != null) {
                        DataTypeInfo result = createDataTypeFor(wt.getSuperBound());
                        result.setSimpleName("? super " + result.getSimpleName());
                        return result;
                    } else {
                        return new DataTypeInfo("?");
                    }
                }
            case DECLARED:
                DeclaredType dt = (DeclaredType) t;
                Element e = dt.asElement();
                if (e instanceof TypeElement) {
                    TypeElement te = (TypeElement) e;
                    List<? extends TypeMirror> typeArguments = dt.getTypeArguments();
                    if (typeArguments != null && !typeArguments.isEmpty()) {
                        DataTypeInfo result = createResultDataType(te);
                        StringBuilder n = new StringBuilder(result.getSimpleName());
                        n.append("<");
                        int limit = typeArguments.size() - 1;
                        for (int i = 0; i < limit; i++) {
                            DataTypeInfo innerElementName = createDataTypeFor(typeArguments.get(i));
                            result.getImports().addAll(innerElementName.getImports());
                            n.append(innerElementName.getSimpleName());
                            n.append(", ");
                        }
                        DataTypeInfo innerElementName = createDataTypeFor(typeArguments.get(limit));
                        result.getImports().addAll(innerElementName.getImports());
                        n.append(innerElementName.getSimpleName());
                        n.append(">");
                        result.setSimpleName(n.toString());
                        return result;
                    } else {
                        return createResultDataType(te);
                    }
                } else {
                    return new DataTypeInfo("/*declared*/");
                }
            case ERROR: return new DataTypeInfo("/*error*/");
            case TYPEVAR: return new DataTypeInfo("/*typevar*/"); // TODO
            case PACKAGE: return new DataTypeInfo("/*package*/");
            case EXECUTABLE: return new DataTypeInfo("/*executable*/"); // TODO
            case OTHER: return new DataTypeInfo("/*other*/");
            default: return new DataTypeInfo("/*default*/");
        }
    }

    private static DataTypeInfo handleMirrors(String className) {
        GenerationInfo gi = TemplateProcessor.getGenerationInfo();
        if ("org.uaithne.mirror.OtherOperation".equals(className)) {
            return new DataTypeInfo(gi.getSharedPackage(), "Operation");
        } if ("org.uaithne.mirror.DataListMirror".equals(className)) {
            return DataTypeInfo.LIST_DATA_TYPE;
        } if ("org.uaithne.mirror.DataPageMirror".equals(className)) {
            return new DataTypeInfo(gi.getSharedPackage(), "DataPage");
        } else {
            return null;
        }
    }

    private NamesGenerator() {
    }

}
