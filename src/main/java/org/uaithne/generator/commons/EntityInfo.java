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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import org.uaithne.annotations.Doc;
import org.uaithne.annotations.IgnoreLogicalDeletion;
import org.uaithne.annotations.Manually;

public class EntityInfo {
    private String[] documentation;
    private String realName;
    private DataTypeInfo extend;
    private final HashSet<DataTypeInfo> implement = new HashSet<DataTypeInfo>(0);
    private DataTypeInfo dataType;
    private final ArrayList<FieldInfo> fields = new ArrayList<FieldInfo>(0);
    private final ArrayList<OperationInfo> operations = new ArrayList<OperationInfo>(0);
    private EntityKind entityKind;
    private TypeElement element;
    private EntityInfo combined = this;
    private EntityInfo parent = null;
    private boolean usedInOrderedOperation;
    private boolean manually;
    private boolean hasDeletionMark;
    private boolean ignoreLogicalDeletion;
    private FieldInfo firstIdField;
    private boolean hasMultiplesIds;
    private boolean deprecated;
    private final HashMap<Class<?>, Object> annotations = new HashMap<Class<?>, Object>(0);
    private DataTypeInfo relatedType;
    private EntityInfo related;
    private final HashMap<String, FieldInfo> fieldsByName = new HashMap<String, FieldInfo>(0);
    private int entityOperationsIndex = Integer.MIN_VALUE;

    public String[] getDocumentation() {
        if (documentation == null && related != null) {
            return related.getDocumentation();
        }
        return documentation;
    }

    public void setDocumentation(String[] documentation) {
        this.documentation = documentation;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public DataTypeInfo getExtend() {
        return extend;
    }

    public void setExtend(DataTypeInfo extend) {
        this.extend = extend;
    }

    public HashSet<DataTypeInfo> getImplement() {
        return implement;
    }
    
    public void addImplement(DataTypeInfo name) {
        implement.add(name);
    }
    
    public DataTypeInfo getDataType() {
        return dataType;
    }

    public void setDataType(DataTypeInfo dataType) {
        this.dataType = dataType;
    }

    public ArrayList<FieldInfo> getFields() {
        return fields;
    }
    
    public void addField(FieldInfo fieldInfo) {
        fields.add(fieldInfo);
        if (fieldInfo.isIdentifier()) {
            if (firstIdField == null) {
                firstIdField = fieldInfo;
            } else {
                hasMultiplesIds = true;
            }
        }
        if (fieldInfo.isDeletionMark()) {
            hasDeletionMark = true;
        }
        fieldsByName.put(fieldInfo.getName(), fieldInfo);
    }
    
    public FieldInfo getFieldByName(String name) {
        return fieldsByName.get(name);
    }

    public ArrayList<OperationInfo> getOperations() {
        return operations;
    }
    
    void addOperation(OperationInfo operation) {
        operations.add(operation);
        if (operation.isOrdered()) {
            usedInOrderedOperation = true;
        }
    }

    public FieldInfo getFirstIdField() {
        return firstIdField;
    }

    public boolean hasMultiplesIds() {
        return hasMultiplesIds;
    }

    public EntityKind getEntityKind() {
        return entityKind;
    }

    public void setEntityKind(EntityKind entityKind) {
        this.entityKind = entityKind;
    }

    public TypeElement getElement() {
        return element;
    }

    public void setElement(TypeElement element) {
        this.element = element;
    }
    
    public <A extends Annotation> A getAnnotation(Class<A> type) {
        A a = getOwnAnnotation(type);
        if (a == null && related != null) {
            return related.getAnnotation(type);
        }
        return a;
    }
    
    public <A extends Annotation> A getOwnAnnotation(Class<A> type) {
        A annotation = (A) annotations.get(type);
        if (annotation != null) {
            return annotation;
        }
        if (annotations.containsKey(type)) {
            return null;
        }
        if (element == null) {
            annotations.put(type, null);
            return null;
        }
        annotation = element.getAnnotation(type);
        annotations.put(type, annotation);
        return annotation;
    }
    
    public void addAnnotation(Annotation annotation) {
        annotations.put(annotation.annotationType(), annotation);
    }
    
    public <A extends Annotation> void removeAnnotation(Class<A> type) {
        annotations.put(type, null);
    }

    public EntityInfo getCombined() {
        return combined;
    }

    public EntityInfo getParent() {
        return parent;
    }

    public boolean isUsedInOrderedOperation() {
        return usedInOrderedOperation;
    }

    public void setUsedInOrderedOperation(boolean usedInOrderedOperation) {
        this.usedInOrderedOperation = usedInOrderedOperation;
    }
    
    public void appendImportsForConstructor(String currentPackage, HashSet<String> imports) {
        if (extend != null) {
            extend.appendImports(currentPackage, imports);
        }
        for (DataTypeInfo imp : implement) {
            imp.appendImports(currentPackage, imports);
        }
        for (FieldInfo field : fields) {
            field.appendImports(currentPackage, imports);
        }
        for (FieldInfo field : combined.fields) {
            if (field.isExcludedFromConstructor()) {
                continue;
            }
            if (field.isIdentifier()) {
                field.appendImports(currentPackage, imports);
            }
            if (!field.isOptional()) {
                field.appendImports(currentPackage, imports);
            }
        }
    }

    public boolean hasDeletionMark() {
        return hasDeletionMark;
    }

    public boolean isIgnoreLogicalDeletionEnabled() {
        return ignoreLogicalDeletion;
    }

    public void setIgnoreLogicalDeletionEnabled(boolean ignoreLogicalDeletion) {
        this.ignoreLogicalDeletion = ignoreLogicalDeletion;
    }

    public boolean isUseLogicalDeletion() {
        if (ignoreLogicalDeletion) {
            return false;
        } else {
            return hasDeletionMark;
        }
    }
    
    public int generateFistPrimeNumberForHashCode() {
        return dataType.generateFistPrimeNumberForHashCode();
    }
    
    public int generateSecondPrimeNumberForHashCode() {
        return dataType.generateSecondPrimeNumberForHashCode();
    }

    public boolean isManually() {
        return manually;
    }

    public void setManually(boolean manually) {
        this.manually = manually;
    }

    public boolean isDeprecated() {
        return deprecated;
    }

    public void setDeprecated(boolean deprecated) {
        this.deprecated = deprecated;
    }

    public DataTypeInfo getRelatedType() {
        return relatedType;
    }

    public void setRelatedType(DataTypeInfo relatedType) {
        this.relatedType = relatedType;
    }

    public EntityInfo getRelated() {
        return related;
    }

    public void setRelated(EntityInfo related) {
        this.related = related;
    }

    public HashMap<String, FieldInfo> getFieldsByName() {
        return fieldsByName;
    }

    public int getEntityOperationsIndex() {
        return entityOperationsIndex;
    }

    public void setEntityOperationsIndex(int entityOperationsIndex) {
        this.entityOperationsIndex = entityOperationsIndex;
    }
    
    public EntityInfo(TypeElement classElement, EntityKind entityKind) {
        element = classElement;
        dataType = NamesGenerator.createEntityDataType(classElement);
        realName = classElement.getQualifiedName().toString();
        extend = NamesGenerator.createDataTypeFor(classElement.getSuperclass());
        if (extend != null && extend.isObject()) {
            extend = null;
        }
        for (TypeMirror i : classElement.getInterfaces()) {
            DataTypeInfo en = NamesGenerator.createDataTypeFor(i);
            if (en != null) {
                implement.add(en);
            }
        }
        this.entityKind = entityKind;
        manually = classElement.getAnnotation(Manually.class) != null;
        ignoreLogicalDeletion = classElement.getAnnotation(IgnoreLogicalDeletion.class) != null;
        
        Doc doc = classElement.getAnnotation(Doc.class);
        if (doc != null) {
            documentation = doc.value();
        }
        
        deprecated = element.getAnnotation(Deprecated.class) != null;
    }

    public EntityInfo(DataTypeInfo dataType, EntityKind entityKind) {
        this.dataType = dataType;
        this.entityKind = entityKind;
        this.realName = dataType.getQualifiedNameWithoutGenerics();
    }
    
    private EntityInfo() {
        
    }
    
    public void uncombine() {
        combined = this;
        parent = null;
    }
    
    @SuppressWarnings("unchecked")
    public void combineWithParent(EntityInfo other) {
        parent = other;
        combined = new EntityInfo();
        combined.documentation = this.documentation;
        combined.realName = this.realName;
        combined.extend = this.extend;
        combined.implement.addAll(other.implement);
        combined.implement.addAll(this.implement);
        combined.dataType = this.dataType;
        combined.fields.addAll(other.fields);
        combined.fields.addAll(this.fields);
        combined.operations.addAll(this.operations);
        combined.entityKind = this.entityKind;
        combined.element = this.element;
        combined.usedInOrderedOperation = this.usedInOrderedOperation || other.usedInOrderedOperation;
        combined.manually = this.manually || other.manually;
        combined.hasDeletionMark = this.hasDeletionMark || other.hasDeletionMark;
        combined.ignoreLogicalDeletion = this.ignoreLogicalDeletion || other.ignoreLogicalDeletion;
        combined.hasMultiplesIds = this.hasMultiplesIds || other.hasMultiplesIds;
        combined.firstIdField = this.firstIdField;
        if (combined.firstIdField == null) {
            combined.firstIdField = other.firstIdField;
        } else if (other.firstIdField != null) {
            hasMultiplesIds = true;
        }
        combined.annotations.putAll(this.annotations);
        combined.fieldsByName.putAll(other.fieldsByName);
        combined.fieldsByName.putAll(this.fieldsByName);
    }
    
    public void handleRelated(EntityInfo related) {
        this.related = related;
        EntityInfo relatedCombined = related.getCombined();
        HashMap<String, FieldInfo> relatedFieldsByName = relatedCombined.fieldsByName;
        for (FieldInfo field : fields) {
            FieldInfo relatedField = relatedFieldsByName.get(field.getName());
            field.setRelated(relatedField);
        }
    }
    
}
