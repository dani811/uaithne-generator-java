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
    private HashSet<String> imports = new HashSet<String>(0);
    private String packageName;
    private String simpleName;
    private String qualifiedName;

    public String getSimpleName() {
        return simpleName;
    }

    public void setSimpleName(String simpleName) {
        this.simpleName = simpleName;
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

    public void setQualifiedName(String qualifiedName) {
        this.qualifiedName = qualifiedName;
    }

    public HashSet<String> getImports() {
        return imports;
    }
    
    public void appendImports(String currentPackage, HashSet<String> importsList) {
        for (String imp : imports) {
            Utils.appendImportIfRequired(importsList, currentPackage, imp);
        }
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
        if ((this.qualifiedName == null) ? (other.qualifiedName != null) : !this.qualifiedName.equals(other.qualifiedName)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + (this.packageName != null ? this.packageName.hashCode() : 0);
        hash = 97 * hash + (this.simpleName != null ? this.simpleName.hashCode() : 0);
        hash = 97 * hash + (this.qualifiedName != null ? this.qualifiedName.hashCode() : 0);
        return hash;
    }

    private DataTypeInfo() {
        
    }
    
    public DataTypeInfo(String simpleName) {
        if (simpleName == null) {
            throw new IllegalArgumentException("The simple name cannot be null");
        } else if (simpleName.isEmpty()) {
            throw new IllegalArgumentException("The simple name cannot be empty");
        }
        
        this.packageName = "";
        this.simpleName = simpleName;
        this.qualifiedName = simpleName;
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
        
        this.packageName = packageName;
        this.simpleName = simpleName;
        
        if (!qualifiedName.isEmpty()) {
            imports.add(qualifiedName);
        }
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
        this.qualifiedName = qualifiedName;
        
        if (!qualifiedName.isEmpty()) {
            imports.add(qualifiedName);
        }
    }
    
    public DataTypeInfo of(DataTypeInfo genericArgument) {
        DataTypeInfo result = new DataTypeInfo();
        result.packageName = packageName;
        result.simpleName = simpleName + "<" + genericArgument.simpleName + ">";
        result.qualifiedName = qualifiedName + "<" + genericArgument.simpleName + ">";
        result.imports.addAll(imports);
        result.imports.addAll(genericArgument.imports);
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
    
    public boolean isBoolean() {
        return "boolean".equals(qualifiedName) ||
            "java.lang.Boolean".equals(qualifiedName);
    }
    
    public boolean isNumeric() {
        return "byte".equals(qualifiedName) ||
                "java.lang.Byte".equals(qualifiedName) || 
                "short".equals(qualifiedName) || 
                "java.lang.Short".equals(qualifiedName) || 
                "int".equals(qualifiedName) || 
                "java.lang.Integer".equals(qualifiedName) ||
                "long".equals(qualifiedName) || 
                "java.lang.Long".equals(qualifiedName) ||
                "java.math.BigDecimal".equals(qualifiedName) ||
                "java.math.BigInteger".equals(qualifiedName);
    }
    
    public boolean isChar() {
        return "char".equals(qualifiedName) || 
                "java.lang.Character".equals(qualifiedName);
    }
    
    public boolean isString() {
        return "java.lang.String".equals(qualifiedName);
    }
    
    public boolean isDate() {
        return "java.util.Date".equals(qualifiedName) ||
                "java.sql.Date".equals(qualifiedName) ||
                "java.sql.Time".equals(qualifiedName) ||
                "java.sql.Timestamp".equals(qualifiedName);
    }
    
    public boolean isList() {
        return qualifiedName.startsWith("java.util.List") || 
                qualifiedName.startsWith("java.util.ArrayList");
    }
    
    private static final int[] PRIMES = new int[] {2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97};
    
    public static final DataTypeInfo PAGE_INFO_DATA_TYPE = new DataTypeInfo("java.math", "BigInteger", "java.math.BigInteger");
    public static final DataTypeInfo PAGE_ONLY_DATA_COUNT_DATA_TYPE = new DataTypeInfo("boolean");
    public static final DataTypeInfo SERIALIZABLE_DATA_TYPE = new DataTypeInfo("java.io", "Serializable", "java.io.Serializable");
    public static final DataTypeInfo AFFECTED_ROW_COUNT_DATA_TYPE = new DataTypeInfo("java.lang", "Integer", "java.lang.Integer");
    public static final DataTypeInfo LIST_DATA_TYPE = new DataTypeInfo("java.util", "List", "java.util.List");
    public static final DataTypeInfo ARRAYLIST_DATA_TYPE = new DataTypeInfo("java.util", "ArrayList", "java.util.ArrayList");
    public static final DataTypeInfo HASHMAP_DATA_TYPE = new DataTypeInfo("java.util", "HashMap", "java.util.HashMap");
}
