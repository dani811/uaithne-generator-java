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
import org.uaithne.annotations.Manually;

public class EntityInfo {
    private String[] documentation;
    private String realName;
    private DataTypeInfo extend;
    private HashSet<DataTypeInfo> implement = new HashSet<DataTypeInfo>();
    private DataTypeInfo dataType;
    private ArrayList<FieldInfo> fields = new ArrayList<FieldInfo>(0);
    private ArrayList<FieldInfo> mandatoryFields = new ArrayList<FieldInfo>(0);
    private ArrayList<FieldInfo> idFields = new ArrayList<FieldInfo>(0);
    private ArrayList<FieldInfo> optionalFields = new ArrayList<FieldInfo>(0);
    private ArrayList<FieldInfo> idAndMandatoryFields = new ArrayList<FieldInfo>(0);
    private ArrayList<FieldInfo> mandatoryAndOptionalFields = new ArrayList<FieldInfo>(0);
    private ArrayList<FieldInfo> extraFields = new ArrayList<FieldInfo>(0);
    private ArrayList<FieldInfo> fieldsWithExtras = new ArrayList<FieldInfo>(0);
    private ArrayList<FieldInfo> deletionMarks = new ArrayList<FieldInfo>(0);
    private ArrayList<OperationInfo> operations = new ArrayList<OperationInfo>();
    private EntityKind entityKind;
    private TypeElement element;
    private EntityInfo combined = this;
    private EntityInfo parent = null;
    private HashMap<String, Object> extraInfo = new HashMap<String, Object>();
    private boolean usedInOrderedOperation;
    private boolean manually;

    public String[] getDocumentation() {
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

    public ArrayList<FieldInfo> getFieldsWithExtras() {
        return fieldsWithExtras;
    }

    public ArrayList<FieldInfo> getFields() {
        return fields;
    }
    
    public void addField(FieldInfo fieldInfo) {
        if (fieldInfo.isExtra()) {
            addExtraField(fieldInfo);
        } else if (fieldInfo.isIdentifier()) {
            addIdField(fieldInfo);
        } else if (fieldInfo.isOptional()) {
            addOptionalField(fieldInfo);
        } else {
            addMandatoryField(fieldInfo);
        }
        if (fieldInfo.isDeletionMark()) {
            deletionMarks.add(fieldInfo);
        }
    }
    
    public FieldInfo getFieldByName(String name) {
        for(FieldInfo field : fieldsWithExtras) {
            if (field.getName().equals(name)) {
                return field;
            }
        }
        return null;
    }

    public ArrayList<FieldInfo> getMandatoryFields() {
        return mandatoryFields;
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
    
    private void addMandatoryField(FieldInfo fieldInfo) {
        fields.add(fieldInfo);
        fieldsWithExtras.add(fieldInfo);
        mandatoryFields.add(fieldInfo);
        idAndMandatoryFields.add(fieldInfo);
        mandatoryAndOptionalFields.add(fieldInfo);
    }

    public FieldInfo getFirstIdField() {
        if (idFields.size() <= 0) {
            return null;
        }
        return idFields.get(0);
    }
    
    public ArrayList<FieldInfo> getIdFields() {
        return idFields;
    }
    
    public ArrayList<FieldInfo> getDeletionMarksFields() {
        return deletionMarks;
    }
    
    private void addIdField(FieldInfo fieldInfo) {
        fields.add(fieldInfo);
        fieldsWithExtras.add(fieldInfo);
        idFields.add(fieldInfo);
        idAndMandatoryFields.add(fieldInfo);
    }

    public ArrayList<FieldInfo> getOptionalFields() {
        return optionalFields;
    }
    
    private void addOptionalField(FieldInfo fieldInfo) {
        fields.add(fieldInfo);
        fieldsWithExtras.add(fieldInfo);
        optionalFields.add(fieldInfo);
        mandatoryAndOptionalFields.add(fieldInfo);
    }

    public ArrayList<FieldInfo> getIdAndMandatoryFields() {
        return idAndMandatoryFields;
    }

    public ArrayList<FieldInfo> getMandatoryAndOptionalFields() {
        return mandatoryAndOptionalFields;
    }

    public ArrayList<FieldInfo> getExtraFields() {
        return extraFields;
    }
    
    private void addExtraField(FieldInfo fieldInfo) {
        extraFields.add(fieldInfo);
        fieldsWithExtras.add(fieldInfo);
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
        if (element == null) {
            return null;
        }
        return element.getAnnotation(type);
    }

    public EntityInfo getCombined() {
        return combined;
    }

    public EntityInfo getParent() {
        return parent;
    }

    public HashMap<String, Object> getExtraInfo() {
        return extraInfo;
    }
    
    public Object addExtraInfo(String key, Object value) {
        return extraInfo.put(key, value);
    }

    public boolean isUsedInOrderedOperation() {
        return usedInOrderedOperation;
    }

    public void setUsedInOrderedOperation(boolean usedInOrderedOperation) {
        this.usedInOrderedOperation = usedInOrderedOperation;
    }
    
    public void appendFullImports(String currentPackage, HashSet<String> imports) {
        if (extend != null) {
            extend.appendImports(currentPackage, imports);
        }
        for (DataTypeInfo imp : implement) {
            imp.appendImports(currentPackage, imports);
        }
        for (FieldInfo field : combined.fieldsWithExtras) {
            field.appendImports(currentPackage, imports);
        }
    }

    public boolean isLogicalDeletion() {
        return !deletionMarks.isEmpty();
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
    
    public EntityInfo(TypeElement classElement, EntityKind entityKind) {
        element = classElement;
        dataType = NamesGenerator.createEntityDataType(classElement);
        realName = classElement.getQualifiedName().toString();
        extend = NamesGenerator.createDataTypeFor(classElement.getSuperclass());
        if (extend != null && "java.lang.Object".equals(extend.getQualifiedName())) {
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
        
        Doc doc = classElement.getAnnotation(Doc.class);
        if (doc != null) {
            documentation = doc.value();
        }
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
        combined.extraFields.addAll(other.extraFields);
        combined.extraFields.addAll(this.extraFields);
        combined.fieldsWithExtras.addAll(other.fieldsWithExtras);
        combined.fieldsWithExtras.addAll(this.fieldsWithExtras);
        combined.mandatoryFields.addAll(other.mandatoryFields);
        combined.mandatoryFields.addAll(this.mandatoryFields);
        combined.idFields.addAll(other.idFields);
        combined.idFields.addAll(this.idFields);
        combined.optionalFields.addAll(other.optionalFields);
        combined.optionalFields.addAll(this.optionalFields);
        combined.idAndMandatoryFields.addAll(other.idAndMandatoryFields);
        combined.idAndMandatoryFields.addAll(this.idAndMandatoryFields);
        combined.mandatoryAndOptionalFields.addAll(other.mandatoryAndOptionalFields);
        combined.mandatoryAndOptionalFields.addAll(this.mandatoryAndOptionalFields);
        combined.operations.addAll(this.operations);
        combined.entityKind = this.entityKind;
        combined.element = this.element;
        if (other.extraInfo.isEmpty()) {
            combined.extraInfo.putAll(this.extraInfo);
        } else if (this.extraInfo.isEmpty()) {
            combined.extraInfo.putAll(other.extraInfo);
        } else {
            combined.extraInfo = (HashMap<String, Object>) Utils.combine(other.extraInfo, this.extraInfo);
        }
        combined.usedInOrderedOperation = this.usedInOrderedOperation || other.usedInOrderedOperation;
        combined.deletionMarks.addAll(other.deletionMarks);
        combined.deletionMarks.addAll(this.deletionMarks);
        combined.manually = this.manually || other.manually;
    }
    
}
