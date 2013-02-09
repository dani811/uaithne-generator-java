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

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.HashSet;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;
import org.uaithne.annotations.*;

public class FieldInfo {
    private String[] documentation;
    private String name;
    private DataTypeInfo dataType;
    private boolean markAsOvwrride;
    private boolean markAsTransient;
    private String defaultValue;
    private VariableElement element;
    private HashMap<String, Object> extraInfo = new HashMap<String, Object>();
    private boolean optional;
    private String mappedName;
    private boolean orderBy;
    private boolean identifier;
    private boolean extra;
    private boolean deletionMark;
    private boolean manually;
    private FieldInfo related;
    private boolean deprecated;

    public String[] getDocumentation() {
        if (documentation == null && related != null) {
            return related.getDocumentation();
        }
        return documentation;
    }

    public void setDocumentation(String[] documentation) {
        this.documentation = documentation;
    }
    
    public void appendImports(String currentPackage, HashSet<String> imports) {
        dataType.appendImports(currentPackage, imports);
    }

    public DataTypeInfo getDataType() {
        return dataType;
    }

    public void setDataType(DataTypeInfo dataType) {
        this.dataType = dataType;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public String getLowerCaseName() {
        return name.toLowerCase();
    }
    
    public String getCapitalizedName() {
        return name.substring(0,1).toUpperCase() + name.substring(1);
    }

    public boolean isMarkAsTransient() {
        return markAsTransient;
    }

    public void setMarkAsTransient(boolean markAsTransient) {
        this.markAsTransient = markAsTransient;
    }

    public boolean isMarkAsOvwrride() {
        return markAsOvwrride;
    }

    public void setMarkAsOvwrride(boolean markAsOvwrride) {
        this.markAsOvwrride = markAsOvwrride;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public VariableElement getElement() {
        return element;
    }

    public void setElement(VariableElement element) {
        this.element = element;
    }
    
    public <A extends Annotation> A getAnnotation(Class<A> type) {
        if (element == null) {
            if (related != null) {
                return related.getAnnotation(type);
            }
            return null;
        }
        A a = element.getAnnotation(type);
        if (a == null && related != null) {
            return related.getAnnotation(type);
        } 
        return a;
    }

    public HashMap<String, Object> getExtraInfo() {
        return extraInfo;
    }
    
    public Object addExtraInfo(String key, Object value) {
        return extraInfo.put(key, value);
    }
    
    public boolean isOptional() {
        return optional;
    }

    public void setOptional(boolean optional) {
        this.optional = optional;
    }

    public String getMappedName() {
        if (mappedName == null && related != null) {
            return related.getMappedName();
        }
        return mappedName;
    }
    
    public boolean hasOwnMappedName() {
        return mappedName != null;
    }

    public String getMappedNameOrName() {
        String result = getMappedName();
        if (result == null || result.isEmpty()) {
            return name;
        }
        return result;
    }

    public void setMappedName(String mappedName) {
        this.mappedName = mappedName;
    }

    public boolean isOrderBy() {
        return orderBy;
    }

    public void setOrderBy(boolean orderBy) {
        this.orderBy = orderBy;
    }

    public boolean isIdentifier() {
        return identifier;
    }

    public void setIdentifier(boolean identifier) {
        this.identifier = identifier;
    }

    public boolean isExtra() {
        return extra;
    }

    public void setExtra(boolean extra) {
        this.extra = extra;
    }

    public boolean isDeletionMark() {
        if (!deletionMark) {
            if (related != null) {
                return related.isDeletionMark();
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    public void setDeletionMark(boolean deletionMark) {
        this.deletionMark = deletionMark;
    }
    
    public String generateEqualsRule() {
        return dataType.generateEqualsRule(name);
    }
    
    public String generateHashCodeRule() {
        return dataType.generateHashCodeRule(name);
    }

    public boolean isManually() {
        if (!manually) {
            if (related != null) {
                return related.isManually();
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    public void setManually(boolean manually) {
        this.manually = manually;
    }

    public FieldInfo getRelated() {
        return related;
    }

    public void setRelated(FieldInfo related) {
        this.related = related;
    }

    public boolean isDeprecated() {
        return deprecated;
    }

    public void setDeprecated(boolean deprecated) {
        this.deprecated = deprecated;
    }
    
    public FieldInfo(VariableElement element) {
        this.element = element;
        name = element.getSimpleName().toString();
        if (element.getModifiers().contains(Modifier.TRANSIENT)) {
            markAsTransient = true;
        }
        dataType = NamesGenerator.createDataTypeFor(element.asType());
        defaultValue = Utils.getDefaultValue(dataType, element);
        orderBy = element.getAnnotation(OrderBy.class) != null;
        optional = element.getAnnotation(Optional.class) != null;
        extra = element.getAnnotation(Extra.class) != null;
        identifier = element.getAnnotation(Id.class) != null;
        deletionMark = element.getAnnotation(DeletionMark.class) != null;
        MappedName mn = element.getAnnotation(MappedName.class);
        if (mn != null) {
            String[] value = mn.value();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < value.length - 1; i++) {
                sb.append(value[i]);
                sb.append(" ");
            }
            sb.append(value[value.length - 1]);
            mappedName = sb.toString();
        }
        manually = element.getAnnotation(Manually.class) != null;
        
        Doc doc = element.getAnnotation(Doc.class);
        if (doc != null) {
            documentation = doc.value();
        }
        
        Deprecated deprecatedAnnotation = element.getAnnotation(Deprecated.class);
        if (deprecatedAnnotation != null) {
            this.deprecated = true;
        }
    }

    public FieldInfo(String name, DataTypeInfo dataType) {
        this.name = name;
        this.dataType = dataType;
    }
    
    public FieldInfo(String name, FieldInfo fieldInfo) {
        documentation = fieldInfo.documentation;
        this.name = name;
        dataType = fieldInfo.dataType;
        defaultValue = fieldInfo.defaultValue;
        element = fieldInfo.element;
        mappedName = fieldInfo.mappedName;
        orderBy = fieldInfo.orderBy;
        optional = fieldInfo.optional;
        identifier = fieldInfo.identifier;
        deletionMark = fieldInfo.deletionMark;
        manually = fieldInfo.manually;
        related = fieldInfo;
    }
}
