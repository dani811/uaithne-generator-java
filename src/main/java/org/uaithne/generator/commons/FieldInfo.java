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
    private boolean setValueMark;
    private boolean insertUserMark;
    private boolean insertDateMark;
    private boolean updateUserMark;
    private boolean updateDateMark;
    private boolean deleteUserMark;
    private boolean deleteDateMark;
    private boolean deletionMark;
    private boolean versionMark;
    private boolean manually;
    private FieldInfo related;
    private boolean deprecated;
    private boolean excludedFromConstructor;

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

    public boolean isSetValueMark() {
        if (!setValueMark) {
            if (related != null) {
                return related.isSetValueMark();
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    public void setSetValueMark(boolean setValueMark) {
        this.setValueMark = setValueMark;
    }

    public boolean isInsertUserMark() {
        if (!insertUserMark) {
            if (related != null) {
                return related.isInsertUserMark();
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    public void setInsertUserMark(boolean insertUserMark) {
        this.insertUserMark = insertUserMark;
    }

    public boolean isInsertDateMark() {
        if (!insertDateMark) {
            if (related != null) {
                return related.isInsertDateMark();
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    public void setInsertDateMark(boolean insertDateMark) {
        this.insertDateMark = insertDateMark;
    }

    public boolean isUpdateUserMark() {
        if (!updateUserMark) {
            if (related != null) {
                return related.isUpdateUserMark();
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    public void setUpdateUserMark(boolean updateUserMark) {
        this.updateUserMark = updateUserMark;
    }

    public boolean isUpdateDateMark() {
        if (!updateDateMark) {
            if (related != null) {
                return related.isUpdateDateMark();
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    public void setUpdateDateMark(boolean updateDateMark) {
        this.updateDateMark = updateDateMark;
    }

    public boolean isDeleteUserMark() {
        if (!deleteUserMark) {
            if (related != null) {
                return related.isDeleteUserMark();
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    public void setDeleteUserMark(boolean deleteUserMark) {
        this.deleteUserMark = deleteUserMark;
    }

    public boolean isDeleteDateMark() {
        if (!deleteDateMark) {
            if (related != null) {
                return related.isDeleteDateMark();
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    public void setDeleteDateMark(boolean deleteDateMark) {
        this.deleteDateMark = deleteDateMark;
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

    public boolean isVersionMark() {
        if (!versionMark) {
            if (related != null) {
                return related.isVersionMark();
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    public void setVersionMark(boolean versionMark) {
        this.versionMark = versionMark;
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

    public boolean isExcludedFromConstructor() {
        return excludedFromConstructor;
    }

    public void setExcludedFromConstructor(boolean excludedFromConstructor) {
        this.excludedFromConstructor = excludedFromConstructor;
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
        identifier = element.getAnnotation(Id.class) != null;
        setValueMark = element.getAnnotation(SetValue.class) != null;
        insertUserMark = element.getAnnotation(InsertUser.class) != null;
        insertDateMark = element.getAnnotation(InsertDate.class) != null;
        updateUserMark = element.getAnnotation(UpdateUser.class) != null;
        updateDateMark = element.getAnnotation(UpdateDate.class) != null;
        deleteUserMark = element.getAnnotation(DeleteUser.class) != null;
        deleteDateMark = element.getAnnotation(DeleteDate.class) != null;
        deletionMark = element.getAnnotation(DeletionMark.class) != null;
        versionMark = element.getAnnotation(Version.class) != null;
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
        
        ExcludeFromConstructor excludeFromConstructorAnnotation = element.getAnnotation(ExcludeFromConstructor.class);
        if (excludeFromConstructorAnnotation != null) {
            this.excludedFromConstructor = true;
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
        setValueMark = fieldInfo.setValueMark;
        insertUserMark = fieldInfo.insertUserMark;
        insertDateMark = fieldInfo.insertDateMark;
        updateUserMark = fieldInfo.updateUserMark;
        updateDateMark = fieldInfo.updateDateMark;
        deleteUserMark = fieldInfo.deleteUserMark;
        deleteDateMark = fieldInfo.deleteDateMark;
        deletionMark = fieldInfo.deletionMark;
        versionMark = fieldInfo.versionMark;
        manually = fieldInfo.manually;
        related = fieldInfo;
        deprecated = fieldInfo.deprecated;
        excludedFromConstructor = fieldInfo.excludedFromConstructor;
        markAsOvwrride = fieldInfo.markAsOvwrride;
        markAsTransient = fieldInfo.markAsTransient;
    }
}
