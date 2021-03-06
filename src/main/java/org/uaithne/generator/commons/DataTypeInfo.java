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

import java.util.HashSet;

public class DataTypeInfo {

    private final HashSet<String> imports = new HashSet<String>(0);
    private String packageName;
    private String simpleName;
    private String simpleNameWithoutGenerics;
    private String qualifiedName;
    private String qualifiedNameWithoutGenerics;
    private DataTypeInfo boxed;
    private boolean isEnum;

    public String getSimpleName() {
        return simpleName;
    }

    public void setSimpleName(String simpleName) {
        this.simpleName = simpleName;
    }

    public String getSimpleNameWithoutGenerics() {
        return simpleNameWithoutGenerics;
    }

    public void setSimpleNameWithoutGenerics(String simpleNameWithoutGenerics) {
        this.simpleNameWithoutGenerics = simpleNameWithoutGenerics;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getQualifiedName() {
        return qualifiedName;
    }

    public String getPrintableQualifiedName() {
        return qualifiedName.replaceAll("java\\.lang\\.", "");
    }

    public void setQualifiedName(String qualifiedName) {
        this.qualifiedName = qualifiedName;
    }

    public String getQualifiedNameWithoutGenerics() {
        return qualifiedNameWithoutGenerics;
    }

    public String getPrintableQualifiedNameWithoutGenerics() {
        return qualifiedNameWithoutGenerics.replaceAll("java\\.lang\\.", "");
    }

    public void setQualifiedNameWithoutGenerics(String qualifiedNameWithoutGenerics) {
        this.qualifiedNameWithoutGenerics = qualifiedNameWithoutGenerics;
    }

    public HashSet<String> getImports() {
        return imports;
    }

    public void appendImports(String currentPackage, HashSet<String> importsList) {
        for (String imp : imports) {
            Utils.appendImportIfRequired(importsList, currentPackage, imp);
        }
    }

    public boolean isEnum() {
        return isEnum;
    }

    public void setIsEnum(boolean isEnum) {
        this.isEnum = isEnum;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DataTypeInfo other = (DataTypeInfo) obj;
        if ((this.packageName == null) ? (other.packageName != null) : !this.packageName.equals(other.packageName)) {
            return false;
        }
        if ((this.simpleName == null) ? (other.simpleName != null) : !this.simpleName.equals(other.simpleName)) {
            return false;
        }
        if ((this.simpleNameWithoutGenerics == null) ? (other.simpleNameWithoutGenerics != null) : !this.simpleNameWithoutGenerics.equals(other.simpleNameWithoutGenerics)) {
            return false;
        }
        if ((this.qualifiedName == null) ? (other.qualifiedName != null) : !this.qualifiedName.equals(other.qualifiedName)) {
            return false;
        }
        if ((this.qualifiedNameWithoutGenerics == null) ? (other.qualifiedNameWithoutGenerics != null) : !this.qualifiedNameWithoutGenerics.equals(other.qualifiedNameWithoutGenerics)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.packageName != null ? this.packageName.hashCode() : 0);
        hash = 97 * hash + (this.simpleName != null ? this.simpleName.hashCode() : 0);
        hash = 97 * hash + (this.simpleNameWithoutGenerics != null ? this.simpleNameWithoutGenerics.hashCode() : 0);
        hash = 97 * hash + (this.qualifiedName != null ? this.qualifiedName.hashCode() : 0);
        hash = 97 * hash + (this.qualifiedNameWithoutGenerics != null ? this.qualifiedNameWithoutGenerics.hashCode() : 0);
        return hash;
    }

    private DataTypeInfo() {
        
    }
    
    public DataTypeInfo(String simpleName, DataTypeInfo boxed) {
        this(simpleName);
        this.boxed = boxed;
    }
    
    public DataTypeInfo(String simpleName) {
        if (simpleName == null) {
            throw new IllegalArgumentException("The simple name cannot be null");
        } else if (simpleName.isEmpty()) {
            throw new IllegalArgumentException("The simple name cannot be empty");
        }

        this.packageName = "";
        this.simpleName = simpleName;
        this.simpleNameWithoutGenerics = simpleName;
        this.qualifiedName = simpleName;
        this.qualifiedNameWithoutGenerics = simpleName;
    }

    public DataTypeInfo(String packageName, String simpleName) {
        if (simpleName == null) {
            throw new IllegalArgumentException("The simple name cannot be null");
        } else if (simpleName.isEmpty()) {
            throw new IllegalArgumentException("The simple name cannot be empty");
        }

        if (packageName == null) {
            packageName = "";
        }

        if (packageName.isEmpty()) {
            qualifiedName = simpleName;
        } else {
            qualifiedName = packageName + "." + simpleName;
        }
        qualifiedNameWithoutGenerics = qualifiedName;

        this.packageName = packageName;
        this.simpleName = simpleName;
        this.simpleNameWithoutGenerics = simpleName;

        if (!qualifiedName.isEmpty()) {
            imports.add(qualifiedName);
        }
    }

    public DataTypeInfo(String packageName, String simpleName, String qualifiedName, DataTypeInfo boxed) {
        this(packageName, simpleName, qualifiedName);
        this.boxed = boxed;
    }
    
    public DataTypeInfo(String packageName, String simpleName, String qualifiedName) {
        if (simpleName == null) {
            throw new IllegalArgumentException("The simple name cannot be null");
        } else if (simpleName.isEmpty()) {
            throw new IllegalArgumentException("The simple name cannot be empty");
        }

        if (packageName == null) {
            packageName = "";
        }

        if (qualifiedName == null) {
            qualifiedName = "";
        }

        this.packageName = packageName;
        this.simpleName = simpleName;
        this.simpleNameWithoutGenerics = simpleName;
        this.qualifiedName = qualifiedName;
        this.qualifiedNameWithoutGenerics = qualifiedName;

        if (!qualifiedName.isEmpty()) {
            imports.add(qualifiedName);
        }
    }

    public DataTypeInfo(String packageName, String simpleName, String qualifiedName, String importt) {
        if (simpleName == null) {
            throw new IllegalArgumentException("The simple name cannot be null");
        } else if (simpleName.isEmpty()) {
            throw new IllegalArgumentException("The simple name cannot be empty");
        }

        if (packageName == null) {
            packageName = "";
        }

        if (qualifiedName == null) {
            qualifiedName = "";
        }

        this.packageName = packageName;
        this.simpleName = simpleName;
        this.simpleNameWithoutGenerics = simpleName;
        this.qualifiedName = qualifiedName;
        this.qualifiedNameWithoutGenerics = qualifiedName;

        if (importt != null && !importt.isEmpty()) {
            imports.add(importt);
        }
    }

    public DataTypeInfo(DataTypeInfo other) {
        imports.addAll(other.imports);
        packageName = other.packageName;
        simpleName = other.simpleName;
        simpleNameWithoutGenerics = other.simpleNameWithoutGenerics;
        qualifiedName = other.qualifiedName;
        qualifiedNameWithoutGenerics = other.qualifiedNameWithoutGenerics;
        isEnum = other.isEnum;
    }

    public DataTypeInfo of(DataTypeInfo genericArgument) {
        DataTypeInfo result = new DataTypeInfo();
        result.packageName = packageName;
        result.simpleNameWithoutGenerics = simpleNameWithoutGenerics;
        result.qualifiedNameWithoutGenerics = qualifiedNameWithoutGenerics;
        result.simpleName = simpleName + "<" + genericArgument.simpleName + ">";
        result.qualifiedName = qualifiedName + "<" + genericArgument.qualifiedName + ">";
        result.imports.addAll(imports);
        result.imports.addAll(genericArgument.imports);
        if (isList()) {
            isEnum = genericArgument.isEnum;
        }
        return result;
    }

    public DataTypeInfo of(DataTypeInfo genericArgument, DataTypeInfo genericArgument2) {
        DataTypeInfo result = new DataTypeInfo();
        result.packageName = packageName;
        result.simpleNameWithoutGenerics = simpleNameWithoutGenerics;
        result.qualifiedNameWithoutGenerics = qualifiedNameWithoutGenerics;
        result.simpleName = simpleName + "<" + genericArgument.simpleName + ", " + genericArgument2.simpleName + ">";
        result.qualifiedName = qualifiedName + "<" + genericArgument.qualifiedName + ", " + genericArgument2.qualifiedName + ">";
        result.imports.addAll(imports);
        result.imports.addAll(genericArgument.imports);
        result.imports.addAll(genericArgument2.imports);
        return result;
    }

    public String generateEqualsRule(String name) {
        // See how netbeans generate it: http://hg.netbeans.org/main/file/84453ca69694/java.editor/src/org/netbeans/modules/java/editor/codegen/EqualsHashCodeGenerator.java
        if ("boolean".equals(qualifiedName)) {
            return "this." + name + " != other." + name;
        } else if ("byte".equals(qualifiedName)) {
            return "this." + name + " != other." + name;
        } else if ("short".equals(qualifiedName)) {
            return "this." + name + " != other." + name;
        } else if ("int".equals(qualifiedName)) {
            return "this." + name + " != other." + name;
        } else if ("long".equals(qualifiedName)) {
            return "this." + name + " != other." + name;
        } else if ("char".equals(qualifiedName)) {
            return "this." + name + " != other." + name;
        } else if ("float".equals(qualifiedName)) {
            return "java.lang.Float.floatToIntBits(this." + name + ") != java.lang.Float.floatToIntBits(other." + name + ")";
        } else if ("double".equals(qualifiedName)) {
            return "java.lang.Double.doubleToLongBits(this." + name + ") != java.lang.Double.doubleToLongBits(other." + name + ")";
        } else if ("boolean[]".equals(qualifiedName)) {
            return "! java.util.Arrays.equals(this." + name + ", other." + name + ")";
        } else if ("byte[]".equals(qualifiedName)) {
            return "! java.util.Arrays.equals(this." + name + ", other." + name + ")";
        } else if ("short[]".equals(qualifiedName)) {
            return "! java.util.Arrays.equals(this." + name + ", other." + name + ")";
        } else if ("int[]".equals(qualifiedName)) {
            return "! java.util.Arrays.equals(this." + name + ", other." + name + ")";
        } else if ("long[]".equals(qualifiedName)) {
            return "! java.util.Arrays.equals(this." + name + ", other." + name + ")";
        } else if ("char[]".equals(qualifiedName)) {
            return "! java.util.Arrays.equals(this." + name + ", other." + name + ")";
        } else if ("float[]".equals(qualifiedName)) {
            return "! java.util.Arrays.equals(this." + name + ", other." + name + ")";
        } else if ("double[]".equals(qualifiedName)) {
            return "! java.util.Arrays.equals(this." + name + ", other." + name + ")";
        } else if (qualifiedName.endsWith("[]")) {
            return "! java.util.Arrays.deepEquals(this." + name + ", other." + name + ")";
        } else if ("java.lang.String".equals(qualifiedName)) {
            return "(this." + name + " == null) ? (other." + name + " != null) : !this." + name + ".equals(other." + name + ")";
        } else {
            return "this." + name + " != other." + name + " && (this." + name + " == null || !this." + name + ".equals(other." + name + "))";
        }
    }

    public String generateHashCodeRule(String name) {
        // See how netbeans generate it: http://hg.netbeans.org/main/file/84453ca69694/java.editor/src/org/netbeans/modules/java/editor/codegen/EqualsHashCodeGenerator.java
        if ("boolean".equals(qualifiedName)) {
            return "(this." + name + " ? 1 : 0)";
        } else if ("byte".equals(qualifiedName)) {
            return "this." + name;
        } else if ("short".equals(qualifiedName)) {
            return "this." + name;
        } else if ("int".equals(qualifiedName)) {
            return "this." + name;
        } else if ("long".equals(qualifiedName)) {
            return "(int) (this." + name + " ^ (this." + name + " >>> 32))";
        } else if ("char".equals(qualifiedName)) {
            return "this." + name;
        } else if ("float".equals(qualifiedName)) {
            return "java.lang.Float.floatToIntBits(this." + name + ")";
        } else if ("double".equals(qualifiedName)) {
            return "(int) (Double.doubleToLongBits(this." + name + ") ^ (Double.doubleToLongBits(this." + name + ") >>> 32))";
        } else if ("boolean[]".equals(qualifiedName)) {
            return "java.util.Arrays.hashCode(this." + name + ")";
        } else if ("byte[]".equals(qualifiedName)) {
            return "java.util.Arrays.hashCode(this." + name + ")";
        } else if ("short[]".equals(qualifiedName)) {
            return "java.util.Arrays.hashCode(this." + name + ")";
        } else if ("int[]".equals(qualifiedName)) {
            return "java.util.Arrays.hashCode(this." + name + ")";
        } else if ("long[]".equals(qualifiedName)) {
            return "java.util.Arrays.hashCode(this." + name + ")";
        } else if ("char[]".equals(qualifiedName)) {
            return "java.util.Arrays.hashCode(this." + name + ")";
        } else if ("float[]".equals(qualifiedName)) {
            return "java.util.Arrays.hashCode(this." + name + ")";
        } else if ("double[]".equals(qualifiedName)) {
            return "java.util.Arrays.hashCode(this." + name + ")";
        } else if (qualifiedName.endsWith("[]")) {
            return "java.util.Arrays.deepHashCode(this." + name + ")";
        } else if ("java.lang.String".equals(qualifiedName)) {
            return "(this." + name + " != null ? this." + name + ".hashCode() : 0)";
        } else {
            return "(this." + name + " != null ? this." + name + ".hashCode() : 0)";
        }
    }

    public int generateFistPrimeNumberForHashCode() {
        int index = calculatePrimeIndex(4);
        return PRIMES[index];
    }

    public int generateSecondPrimeNumberForHashCode() {
        int index = calculatePrimeIndex(21);
        return PRIMES[index + 4];
    }

    private int calculatePrimeIndex(int maxItem) {
        int result = 0;
        for (int i = 0; i < qualifiedName.length(); i++) {
            result = result + qualifiedName.charAt(i);
            result = result % maxItem;
        }
        return result;
    }
    
    public DataTypeInfo ensureBoxed() {
        if (!packageName.isEmpty()) {
            return this;
        } else if (boxed != null) {
            return boxed;
        } else if ("int".equals(qualifiedName)) {
            return BOXED_INT_DATA_TYPE;
        } else if ("double".equals(qualifiedName)) {
            return BOXED_DOUBLE_DATA_TYPE;
        } else if ("long".equals(qualifiedName)) {
            return BOXED_LONG_DATA_TYPE;
        } else if ("boolean".equals(qualifiedName)) {
            return BOXED_BOOLEAN_DATA_TYPE;
        } else if ("float".equals(qualifiedName)) {
            return BOXED_FLOAT_DATA_TYPE;
        } else if ("byte".equals(qualifiedName)) {
            return BOXED_BYTE_DATA_TYPE;
        } else if ("char".equals(qualifiedName)) {
            return BOXED_CHAR_DATA_TYPE;
        } else if ("short".equals(qualifiedName)) {
            return BOXED_SHORT_DATA_TYPE;
        } else {
            return this;
        }
    }
    
    public boolean isPrimitive() {
        return packageName.isEmpty() && (
                "int".equals(qualifiedName) ||
                "double".equals(qualifiedName) ||
                "long".equals(qualifiedName) ||
                "boolean".equals(qualifiedName) ||
                "float".equals(qualifiedName) ||
                "byte".equals(qualifiedName) ||
                "char".equals(qualifiedName) ||
                "short".equals(qualifiedName));
    }
    
    public boolean isPrimitiveBoolean() {
        return "boolean".equals(qualifiedName);
    }

    public boolean isBoolean() {
        return "boolean".equals(qualifiedName)
                || "java.lang.Boolean".equals(qualifiedName);
    }

    public boolean isNumeric() {
        return "byte".equals(qualifiedName)
                || "java.lang.Byte".equals(qualifiedName)
                || "short".equals(qualifiedName)
                || "java.lang.Short".equals(qualifiedName)
                || "int".equals(qualifiedName)
                || "java.lang.Integer".equals(qualifiedName)
                || "long".equals(qualifiedName)
                || "java.lang.Long".equals(qualifiedName)
                || "float".equals(qualifiedName)
                || "java.lang.Float".equals(qualifiedName)
                || "double".equals(qualifiedName)
                || "java.lang.Double".equals(qualifiedName)
                || "java.math.BigDecimal".equals(qualifiedName)
                || "java.math.BigInteger".equals(qualifiedName);
    }

    public boolean isChar() {
        return "char".equals(qualifiedName)
                || "java.lang.Character".equals(qualifiedName);
    }

    public boolean isString() {
        return "java.lang.String".equals(qualifiedName);
    }

    public boolean isDate() {
        return "java.util.Date".equals(qualifiedName)
                || "java.sql.Date".equals(qualifiedName)
                || "java.sql.Time".equals(qualifiedName)
                || "java.sql.Timestamp".equals(qualifiedName);
    }

    public boolean isList() {
        return "java.util.List".equals(qualifiedNameWithoutGenerics)
                || "java.util.ArrayList".equals(qualifiedNameWithoutGenerics)
                || qualifiedNameWithoutGenerics.endsWith("[]");
    }

    public boolean isVoid() {
        return "java.lang.Void".equals(qualifiedName);
    }

    public boolean isObject() {
        return "java.lang.Object".equals(qualifiedName);
    }
    
    public String getGetterPrefix(GenerationInfo generationInfo) {
        if (isPrimitiveBoolean() || (generationInfo.isUseIsInBooleanObjectGetter() && isBoolean())) {
            return "is";
        } else {
            return "get";
        }
    }
    
    private static final int[] PRIMES = new int[] {2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97};

    public static final DataTypeInfo VOID_DATA_TYPE = new DataTypeInfo("java.lang", "Void", "java.lang.Void");
    
    public static final DataTypeInfo BOXED_INT_DATA_TYPE = new DataTypeInfo("java.lang", "Integer", "java.lang.Integer");
    public static final DataTypeInfo BOXED_DOUBLE_DATA_TYPE = new DataTypeInfo("java.lang", "Double", "java.lang.Double");
    public static final DataTypeInfo BOXED_LONG_DATA_TYPE = new DataTypeInfo("java.lang", "Long", "java.lang.Long");
    public static final DataTypeInfo BOXED_BOOLEAN_DATA_TYPE = new DataTypeInfo("java.lang", "Boolean", "java.lang.Boolean");
    public static final DataTypeInfo BOXED_FLOAT_DATA_TYPE = new DataTypeInfo("java.lang", "Float", "java.lang.Float");
    public static final DataTypeInfo BOXED_BYTE_DATA_TYPE = new DataTypeInfo("java.lang", "Byte", "java.lang.Byte");
    public static final DataTypeInfo BOXED_CHAR_DATA_TYPE = new DataTypeInfo("java.lang", "Character", "java.lang.Character");
    public static final DataTypeInfo BOXED_SHORT_DATA_TYPE = new DataTypeInfo("java.lang", "Short", "java.lang.Short");
    
    public static final DataTypeInfo INT_DATA_TYPE = new DataTypeInfo("int", BOXED_INT_DATA_TYPE);
    public static final DataTypeInfo DOUBLE_DATA_TYPE = new DataTypeInfo("double", BOXED_DOUBLE_DATA_TYPE);
    public static final DataTypeInfo LONG_DATA_TYPE = new DataTypeInfo("long", BOXED_LONG_DATA_TYPE);
    public static final DataTypeInfo BOOLEAN_DATA_TYPE = new DataTypeInfo("boolean", BOXED_BOOLEAN_DATA_TYPE);
    public static final DataTypeInfo FLOAT_DATA_TYPE = new DataTypeInfo("float", BOXED_FLOAT_DATA_TYPE);
    public static final DataTypeInfo BYTE_DATA_TYPE = new DataTypeInfo("byte", BOXED_BYTE_DATA_TYPE);
    public static final DataTypeInfo CHAR_DATA_TYPE = new DataTypeInfo("char", BOXED_CHAR_DATA_TYPE);
    public static final DataTypeInfo SHORT_DATA_TYPE = new DataTypeInfo("short", BOXED_SHORT_DATA_TYPE);
    
    public static final String PAGE_INFO_ZERO = "BigInteger.ZERO";
    public static final String PAGE_INFO_DATA = "BigInteger";
    public static final DataTypeInfo PAGE_INFO_DATA_TYPE = new DataTypeInfo("java.math", PAGE_INFO_DATA, "java.math.BigInteger");
    public static final String PAGE_ONLY_DATA_COUNT_DATA = "boolean";
    public static final DataTypeInfo PAGE_ONLY_DATA_COUNT_DATA_TYPE = new DataTypeInfo(PAGE_ONLY_DATA_COUNT_DATA, BOXED_BOOLEAN_DATA_TYPE);
    public static final String SERIALIZABLE_DATA = "Serializable";
    public static final DataTypeInfo SERIALIZABLE_DATA_TYPE = new DataTypeInfo("java.io", SERIALIZABLE_DATA, "java.io.Serializable");
    public static final String AFFECTED_ROW_COUNT_DATA = "Integer";
    public static final DataTypeInfo AFFECTED_ROW_COUNT_DATA_TYPE = new DataTypeInfo("java.lang", AFFECTED_ROW_COUNT_DATA, "java.lang.Integer");
    public static final String LIST_DATA = "List";
    public static final DataTypeInfo LIST_DATA_TYPE = new DataTypeInfo("java.util", LIST_DATA, "java.util.List");
    public static final String ARRAYLIST_DATA = "ArrayList";
    public static final DataTypeInfo ARRAYLIST_DATA_TYPE = new DataTypeInfo("java.util", ARRAYLIST_DATA, "java.util.ArrayList");
    public static final DataTypeInfo HASHMAP_DATA_TYPE = new DataTypeInfo("java.util", "HashMap", "java.util.HashMap");
    private static final String DEFAULT_SHARED_PACKAGE = "org.uaithne.shared";
    // All of this field are no final because the user can change it's package name
    public static DataTypeInfo DATA_PAGE_DATA_TYPE = new DataTypeInfo(DEFAULT_SHARED_PACKAGE, "DataPage", DEFAULT_SHARED_PACKAGE + ".DataPage");
    public static DataTypeInfo OPERATION_DATA_TYPE = new DataTypeInfo(DEFAULT_SHARED_PACKAGE, "Operation", DEFAULT_SHARED_PACKAGE + ".Operation");
    public static DataTypeInfo DELETE_BY_ID_OPERATION_DATA_TYPE = new DataTypeInfo(DEFAULT_SHARED_PACKAGE, "DeleteByIdOperation", DEFAULT_SHARED_PACKAGE + ".DeleteByIdOperation");
    public static DataTypeInfo INSERT_VALUE_OPERATION_DATA_TYPE = new DataTypeInfo(DEFAULT_SHARED_PACKAGE, "InsertValueOperation", DEFAULT_SHARED_PACKAGE + ".InsertValueOperation");
    public static DataTypeInfo JUST_INSERT_VALUE_OPERATION_DATA_TYPE = new DataTypeInfo(DEFAULT_SHARED_PACKAGE, "JustInsertValueOperation", DEFAULT_SHARED_PACKAGE + ".JustInsertValueOperation");
    public static DataTypeInfo SAVE_VALUE_OPERATION_DATA_TYPE = new DataTypeInfo(DEFAULT_SHARED_PACKAGE, "SaveValueOperation", DEFAULT_SHARED_PACKAGE + ".SaveValueOperation");
    public static DataTypeInfo JUST_SAVE_VALUE_OPERATION_DATA_TYPE = new DataTypeInfo(DEFAULT_SHARED_PACKAGE, "JustSaveValueOperation", DEFAULT_SHARED_PACKAGE + ".JustSaveValueOperation");
    public static DataTypeInfo SELECT_BY_ID_OPERATION_DATA_TYPE = new DataTypeInfo(DEFAULT_SHARED_PACKAGE, "SelectByIdOperation", DEFAULT_SHARED_PACKAGE + ".SelectByIdOperation");
    public static DataTypeInfo UPDATE_VALUE_OPERATION_DATA_TYPE = new DataTypeInfo(DEFAULT_SHARED_PACKAGE, "UpdateValueOperation", DEFAULT_SHARED_PACKAGE + ".UpdateValueOperation");
    public static DataTypeInfo MERGE_VALUE_OPERATION_DATA_TYPE = new DataTypeInfo(DEFAULT_SHARED_PACKAGE, "MergeValueOperation", DEFAULT_SHARED_PACKAGE + ".MergeValueOperation");
    public static DataTypeInfo DATA_PAGE_REQUEST_DATA_TYPE = new DataTypeInfo(DEFAULT_SHARED_PACKAGE, "DataPageRequest", DEFAULT_SHARED_PACKAGE + ".DataPageRequest");
    public static DataTypeInfo EXECUTOR_DATA_TYPE = new DataTypeInfo(DEFAULT_SHARED_PACKAGE, "Executor", DEFAULT_SHARED_PACKAGE + ".Executor");
    public static DataTypeInfo EXECUTOR_GROUP_DATA_TYPE = new DataTypeInfo(DEFAULT_SHARED_PACKAGE, "ExecutorGroup", DEFAULT_SHARED_PACKAGE + ".ExecutorGroup");
    public static DataTypeInfo CHAINED_EXECUTOR_GROUP_DATA_TYPE = new DataTypeInfo(DEFAULT_SHARED_PACKAGE, "ChainedExecutorGroup", DEFAULT_SHARED_PACKAGE + ".ChainedExecutorGroup");
    // end of no final fields

    public static void updateSharedPackage(String sharedPackage) {
        DATA_PAGE_DATA_TYPE = new DataTypeInfo(sharedPackage, "DataPage", sharedPackage + ".DataPage");
        OPERATION_DATA_TYPE = new DataTypeInfo(sharedPackage, "Operation", sharedPackage + ".Operation");
        DELETE_BY_ID_OPERATION_DATA_TYPE = new DataTypeInfo(sharedPackage, "DeleteByIdOperation", sharedPackage + ".DeleteByIdOperation");
        INSERT_VALUE_OPERATION_DATA_TYPE = new DataTypeInfo(sharedPackage, "InsertValueOperation", sharedPackage + ".InsertValueOperation");
        JUST_INSERT_VALUE_OPERATION_DATA_TYPE = new DataTypeInfo(sharedPackage, "JustInsertValueOperation", sharedPackage + ".JustInsertValueOperation");
        SAVE_VALUE_OPERATION_DATA_TYPE = new DataTypeInfo(sharedPackage, "SaveValueOperation", sharedPackage + ".SaveValueOperation");
        JUST_SAVE_VALUE_OPERATION_DATA_TYPE = new DataTypeInfo(sharedPackage, "JustSaveValueOperation", sharedPackage + ".JustSaveValueOperation");
        SELECT_BY_ID_OPERATION_DATA_TYPE = new DataTypeInfo(sharedPackage, "SelectByIdOperation", sharedPackage + ".SelectByIdOperation");
        UPDATE_VALUE_OPERATION_DATA_TYPE = new DataTypeInfo(sharedPackage, "UpdateValueOperation", sharedPackage + ".UpdateValueOperation");
        MERGE_VALUE_OPERATION_DATA_TYPE = new DataTypeInfo(sharedPackage, "MergeValueOperation", sharedPackage + ".MergeValueOperation");
        DATA_PAGE_REQUEST_DATA_TYPE = new DataTypeInfo(sharedPackage, "DataPageRequest", sharedPackage + ".DataPageRequest");
        EXECUTOR_DATA_TYPE = new DataTypeInfo(sharedPackage, "Executor", sharedPackage + ".Executor");
        EXECUTOR_GROUP_DATA_TYPE = new DataTypeInfo(sharedPackage, "ExecutorGroup", sharedPackage + ".ExecutorGroup");
        CHAINED_EXECUTOR_GROUP_DATA_TYPE = new DataTypeInfo(sharedPackage, "ChainedExecutorGroup", sharedPackage + ".ChainedExecutorGroup");
    }
    private static final String DEFAULT_SHARED_MYBATIS_PACKAGE = "org.uaithne.shared.myBatys";
    public static DataTypeInfo MYBATIS_SQL_SESSION_PROVIDER_DATA_TYPE = new DataTypeInfo(DEFAULT_SHARED_MYBATIS_PACKAGE, "SqlSessionProvider", DEFAULT_SHARED_MYBATIS_PACKAGE + ".SqlSessionProvider");
    public static DataTypeInfo MYBATIS_RETAIN_ID_PLUGIN_DATA_TYPE = new DataTypeInfo(DEFAULT_SHARED_MYBATIS_PACKAGE, "RetainIdPlugin", DEFAULT_SHARED_MYBATIS_PACKAGE + ".RetainIdPlugin");
    public static DataTypeInfo MYBATIS_APPLICATION_PARAMETER_DRIVER_DATA_TYPE = new DataTypeInfo(DEFAULT_SHARED_MYBATIS_PACKAGE, "ApplicationParameterDriver", DEFAULT_SHARED_MYBATIS_PACKAGE + ".ApplicationParameterDriver");
    
    public static void updateSharedMyBatisPackage(String sharedMyBatisPackage) {
        MYBATIS_SQL_SESSION_PROVIDER_DATA_TYPE = new DataTypeInfo(sharedMyBatisPackage, "SqlSessionProvider", sharedMyBatisPackage + ".SqlSessionProvider");
        MYBATIS_RETAIN_ID_PLUGIN_DATA_TYPE = new DataTypeInfo(sharedMyBatisPackage, "RetainIdPlugin", sharedMyBatisPackage + ".RetainIdPlugin");
        MYBATIS_APPLICATION_PARAMETER_DRIVER_DATA_TYPE = new DataTypeInfo(sharedMyBatisPackage, "ApplicationParameterDriver", sharedMyBatisPackage + ".ApplicationParameterDriver");
    }
    private static final String DEFAULT_SHARED_GWT_PACKAGE = "org.uaithne.shared.myBatys";
    public static DataTypeInfo GWT_ASYNC_EXECUTOR_GROUP_DATA_TYPE = new DataTypeInfo(DEFAULT_SHARED_GWT_PACKAGE + ".client", "AsyncExecutorGroup", DEFAULT_SHARED_GWT_PACKAGE + ".client.AsyncExecutorGroup");
    public static DataTypeInfo GWT_AWAIT_GWT_OPERATION_DATA_TYPE = new DataTypeInfo(DEFAULT_SHARED_GWT_PACKAGE + ".shared.rpc", "AwaitGwtOperation", DEFAULT_SHARED_GWT_PACKAGE + ".shared.rpc.AwaitGwtOperation");
    public static DataTypeInfo GWT_AWAIT_GWT_RESULT_DATA_TYPE = new DataTypeInfo(DEFAULT_SHARED_GWT_PACKAGE + ".shared.rpc", "AwaitGwtResult", DEFAULT_SHARED_GWT_PACKAGE + ".shared.rpc.AwaitGwtResult");
    public static DataTypeInfo GWT_COMBINED_GWT_OPERATION_DATA_TYPE = new DataTypeInfo(DEFAULT_SHARED_GWT_PACKAGE + ".shared.rpc", "CombinedGwtOperation", DEFAULT_SHARED_GWT_PACKAGE + ".shared.rpc.CombinedGwtOperation");
    public static DataTypeInfo GWT_COMBINED_GWT_RESULT_DATA_TYPE = new DataTypeInfo(DEFAULT_SHARED_GWT_PACKAGE + ".shared.rpc", "CombinedGwtResult", DEFAULT_SHARED_GWT_PACKAGE + ".shared.rpc.CombinedGwtResult");
    public static DataTypeInfo GWT_EXECUTOR_GROUP_RPC_DATA_TYPE = new DataTypeInfo(DEFAULT_SHARED_GWT_PACKAGE + ".shared.rpc", "ExecutorGroupRpc", DEFAULT_SHARED_GWT_PACKAGE + ".shared.rpc.ExecutorGroupRpc");
    public static DataTypeInfo GWT_EXECUTOR_GROUP_RPC_ASYNC_DATA_TYPE = new DataTypeInfo(DEFAULT_SHARED_GWT_PACKAGE + ".shared.rpc", "ExecutorGroupRpcAsync", DEFAULT_SHARED_GWT_PACKAGE + ".shared.rpc.ExecutorGroupRpcAsync");
    public static DataTypeInfo GWT_RPC_EXCEPTION_DATA_TYPE = new DataTypeInfo(DEFAULT_SHARED_GWT_PACKAGE + ".shared.rpc", "RpcException", DEFAULT_SHARED_GWT_PACKAGE + ".shared.rpc.RpcException");

    public static void updateSharedGwtPackage(String sharedGwtPackage) {
        GWT_ASYNC_EXECUTOR_GROUP_DATA_TYPE = new DataTypeInfo(sharedGwtPackage + ".client", "AsyncExecutorGroup", sharedGwtPackage + ".client.AsyncExecutorGroup");
        GWT_AWAIT_GWT_OPERATION_DATA_TYPE = new DataTypeInfo(sharedGwtPackage + ".shared.rpc", "AwaitGwtOperation", sharedGwtPackage + ".shared.rpc.AwaitGwtOperation");
        GWT_AWAIT_GWT_RESULT_DATA_TYPE = new DataTypeInfo(sharedGwtPackage + ".shared.rpc", "AwaitGwtResult", sharedGwtPackage + ".shared.rpc.AwaitGwtResult");
        GWT_COMBINED_GWT_OPERATION_DATA_TYPE = new DataTypeInfo(sharedGwtPackage + ".shared.rpc", "CombinedGwtOperation", sharedGwtPackage + ".shared.rpc.CombinedGwtOperation");
        GWT_COMBINED_GWT_RESULT_DATA_TYPE = new DataTypeInfo(sharedGwtPackage + ".shared.rpc", "CombinedGwtResult", sharedGwtPackage + ".shared.rpc.CombinedGwtResult");
        GWT_EXECUTOR_GROUP_RPC_DATA_TYPE = new DataTypeInfo(sharedGwtPackage + ".shared.rpc", "ExecutorGroupRpc", sharedGwtPackage + ".shared.rpc.ExecutorGroupRpc");
        GWT_EXECUTOR_GROUP_RPC_ASYNC_DATA_TYPE = new DataTypeInfo(sharedGwtPackage + ".shared.rpc", "ExecutorGroupRpcAsync", sharedGwtPackage + ".shared.rpc.ExecutorGroupRpcAsync");
        GWT_RPC_EXCEPTION_DATA_TYPE = new DataTypeInfo(sharedGwtPackage + ".shared.rpc", "RpcException", sharedGwtPackage + ".shared.rpc.RpcException");
    }
}
