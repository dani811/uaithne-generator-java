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

import java.util.*;
import javax.lang.model.element.VariableElement;
import org.uaithne.annotations.DefaultValue;

public class Utils {
    
    public static void appendImportIfRequired(HashSet<String> imports, String currentPackage, String name) {
        if (name.startsWith("java.lang")) {
            if (name.split("\\.").length == 3) {
                return;
            }
        }
        String packageName = name;
        int lastDot = packageName.lastIndexOf('.');
        if (lastDot >= 0 && lastDot < packageName.length()) {
            packageName = packageName.substring(0, lastDot);
        }
        if (packageName == null || packageName.isEmpty()) {
        } else if (!packageName.equals(currentPackage)) {
            imports.add(name);
        }
    }
    
    public static String dropFirstUnderscore(String s) {
        if (s == null || s.isEmpty()) {
            return s;
        }
        if (s.charAt(0) == '_') {
            return s.substring(1);
        } else {
            return s;
        }
    }
    
    public static String firstUpper(String s) {
        if (s == null || s.isEmpty()) {
            return s;
        }
        char c = Character.toUpperCase(s.charAt(0));
        return c + s.substring(1);
    }
    
    public static String firstLower(String s) {
        if (s == null || s.isEmpty()) {
            return s;
        }
        char c = Character.toLowerCase(s.charAt(0));
        return c + s.substring(1);
    }
    
    @SuppressWarnings("unchecked")
    public static Object combine(Object lessImportant, Object mostImportant) {
        if (lessImportant == null) {
            return mostImportant;
        }
        if (mostImportant == null) {
            return lessImportant;
        }
        if (lessImportant instanceof List) {
            if (mostImportant instanceof List) {
                ArrayList result = new ArrayList((List) lessImportant);
                result.addAll((List) mostImportant);
                return result;
            } else {
                return mostImportant;
            }
        }
        if (lessImportant instanceof Map) {
            if (mostImportant instanceof Map) {
                HashMap result = new HashMap((Map) lessImportant);
                Map mostImportantMap = (Map) mostImportant;
                for (Object entryObject : mostImportantMap.entrySet()) {
                    Map.Entry entry = (Map.Entry) entryObject;
                    Object old = result.put(entry.getKey(), entry.getValue());
                    if (old != null) {
                        result.put(entry.getKey(), combine(old, entry.getValue()));
                    }
                }
                return result;
            } else {
                return mostImportant;
            }
        }
        return mostImportant;
    }
    
    public static String getDefaultValue(DataTypeInfo dataType, VariableElement element) {
        DefaultValue dv = element.getAnnotation(DefaultValue.class);
        String defaultValue;
        if (dv != null) {
            defaultValue = dv.value();
        } else {
            return null;
        }
        String qualifiedName = dataType.getQualifiedName();
        if ("boolean".equals(qualifiedName)) {
            return defaultValue;
        } else if ("java.lang.Boolean".equals(qualifiedName)) {
            return defaultValue;
        } else if ("byte".equals(qualifiedName)) {
            return defaultValue;
        } else if ("java.lang.Byte".equals(qualifiedName)) {
            return defaultValue;
        } else if ("short".equals(qualifiedName)) {
            return defaultValue;
        } else if ("java.lang.Short".equals(qualifiedName)) {
            return defaultValue;
        } else if ("int".equals(qualifiedName)) {
            return defaultValue;
        } else if ("java.lang.Integer".equals(qualifiedName)) {
            return defaultValue;
        } else if ("long".equals(qualifiedName)) {
            return defaultValue;
        } else if ("java.lang.Long".equals(qualifiedName)) {
            return defaultValue;
        } else if ("char".equals(qualifiedName)) {
            return "'" + defaultValue.replace("'", "\\'") + "'";
        } else if ("java.lang.Character".equals(qualifiedName)) {
            return "'" + defaultValue.replace("'", "\\'") + "'";
        } else if ("float".equals(qualifiedName)) {
            return defaultValue;
        } else if ("java.lang.Float".equals(qualifiedName)) {
            return defaultValue;
        } else if ("double".equals(qualifiedName)) {
            return defaultValue;
        } else if ("java.lang.Double".equals(qualifiedName)) {
            return defaultValue;
        } else if ("java.lang.String".equals(qualifiedName)) {
            return "\"" + defaultValue.replace("\"", "\\\"") + "\"";
        } else if ("java.math.BigDecimal".equals(qualifiedName)) {
            return "new BigDecimal(\"" + defaultValue.replace("\"", "\\\"") + "\")";
        } else if ("java.math.BigInteger".equals(qualifiedName)) {
            return "new BigInteger(\"" + defaultValue.replace("\"", "\\\"") + "\")";
        } else {
            return defaultValue;
        }
    }

    private Utils() {
    }
    
}
