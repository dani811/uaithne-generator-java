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

public class OperationInfo {
    private String[] documentation;
    private String realName;
    private DataTypeInfo returnDataType;
    private DataTypeInfo oneItemReturnDataType;
    private String methodName;
    private DataTypeInfo dataType;
    private DataTypeInfo extend;
    private HashSet<DataTypeInfo> implement = new HashSet<DataTypeInfo>();
    private ArrayList<FieldInfo> fields = new ArrayList<FieldInfo>(0);
    private ArrayList<FieldInfo> mandatoryFields = new ArrayList<FieldInfo>(0);
    private ArrayList<FieldInfo> optionalFields = new ArrayList<FieldInfo>(0);
    private OperationKind operationKind;
    private EntityInfo entity;
    private TypeElement element;
    private HashMap<String, Object> extraInfo = new HashMap<String, Object>();
    private boolean ordered;
    private boolean manually;
    private boolean distinct;

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

    public DataTypeInfo getReturnDataType() {
        return returnDataType;
    }

    public void setReturnDataType(DataTypeInfo returnDataType) {
        this.returnDataType = returnDataType;
    }

    public DataTypeInfo getOneItemReturnDataType() {
        return oneItemReturnDataType;
    }

    public void setOneItemReturnDataType(DataTypeInfo oneItemReturnDataType) {
        this.oneItemReturnDataType = oneItemReturnDataType;
    }
    
    public DataTypeInfo getResultItem() {
        if (oneItemReturnDataType != null) {
            return oneItemReturnDataType;
        } else {
            return returnDataType;
        }
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public DataTypeInfo getDataType() {
        return dataType;
    }

    public void setDataType(DataTypeInfo dataType) {
        this.dataType = dataType;
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

    public ArrayList<FieldInfo> getFields() {
        return fields;
    }
    
    public void addField(FieldInfo fieldInfo) {
        if (fieldInfo.isOptional()) {
            addOptionalField(fieldInfo);
        } else {
            addMandatoryField(fieldInfo);
        }
    }
    
    public FieldInfo getFieldByName(String name) {
        for(FieldInfo field : fields) {
            if (field.getName().equals(name)) {
                return field;
            }
        }
        return null;
    }

    public ArrayList<FieldInfo> getMandatoryFields() {
        return mandatoryFields;
    }
    
    private void addMandatoryField(FieldInfo fieldInfo) {
        fields.add(fieldInfo);
        mandatoryFields.add(fieldInfo);
        if (fieldInfo.isOrderBy()) {
            ordered = true;
        }
    }

    public ArrayList<FieldInfo> getOptionalFields() {
        return optionalFields;
    }
    
    private void addOptionalField(FieldInfo fieldInfo) {
        fields.add(fieldInfo);
        optionalFields.add(fieldInfo);
        if (fieldInfo.isOrderBy()) {
            ordered = true;
        }
    }

    public EntityInfo getEntity() {
        return entity;
    }

    public void setEntity(EntityInfo entity) {
        this.entity = entity;
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

    public OperationKind getOperationKind() {
        return operationKind;
    }

    public void setOperationKind(OperationKind operationKind) {
        this.operationKind = operationKind;
    }
    
    public void appendFullImports(String currentPackage, HashSet<String> imports) {
        if (extend != null) {
            extend.appendImports(currentPackage, imports);
        }
        for (DataTypeInfo imp : implement) {
            imp.appendImports(currentPackage, imports);
        }
        for (FieldInfo field : fields) {
            field.appendImports(currentPackage, imports);
        }
        returnDataType.appendImports(currentPackage, imports);
        dataType.appendImports(currentPackage, imports);
    }
    
    public void appendUseImports(String currentPackage, HashSet<String> imports) {
        returnDataType.appendImports(currentPackage, imports);
        dataType.appendImports(currentPackage, imports);
    }
    
    public void appendPlainImplementationImports(String currentPackage, HashSet<String> imports) {
        for (FieldInfo field : fields) {
            field.appendImports(currentPackage, imports);
        }
        returnDataType.appendImports(currentPackage, imports);
        dataType.appendImports(currentPackage, imports);
    }
    
    public void appendPlainDefinitionImports(String currentPackage, HashSet<String> imports) {
        for (FieldInfo field : fields) {
            field.appendImports(currentPackage, imports);
        }
        if (oneItemReturnDataType != null) {
            oneItemReturnDataType.appendImports(currentPackage, imports);
        } else {
            returnDataType.appendImports(currentPackage, imports);
        }
    }

    public HashMap<String, Object> getExtraInfo() {
        return extraInfo;
    }
    
    public Object addExtraInfo(String key, Object value) {
        return extraInfo.put(key, value);
    }

    public boolean isOrdered() {
        return ordered;
    }

    public void setOrdered(boolean ordered) {
        this.ordered = ordered;
    }
    
    public int generateFistPrimeNumberForHashCode() {
        return dataType.generateFistPrimeNumberForHashCode();
    }
    
    public int generateSecondPrimeNumberForHashCode() {
        return dataType.generateSecondPrimeNumberForHashCode();
    }
    
    @Deprecated
    public DataTypeInfo getPageInfoDataType() {
        return DataTypeInfo.PAGE_INFO_DATA_TYPE;
    }
    
    @Deprecated
    public DataTypeInfo getPageOnlyDataCountDataType() {
        return DataTypeInfo.PAGE_ONLY_DATA_COUNT_DATA_TYPE;
    }

    public boolean isManually() {
        return manually;
    }

    public void setManually(boolean manually) {
        this.manually = manually;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }
    
    public OperationInfo(TypeElement classElement, String packageName) {
        element = classElement;
        realName = classElement.getQualifiedName().toString();
        dataType = NamesGenerator.createOperationDataType(element, packageName);
        
        String elementName = classElement.getSimpleName().toString();
        elementName = Utils.dropFirstUnderscore(elementName);
        methodName = Utils.firstLower(elementName);
        
        extend = NamesGenerator.createDataTypeFor(classElement.getSuperclass());
        if (extend != null && extend.isObject()) {
            extend = null;
        }
        for (TypeMirror i : classElement.getInterfaces()) {
            DataTypeInfo interfaceDataType = NamesGenerator.createDataTypeFor(i);
            if (interfaceDataType != null) {
                implement.add(interfaceDataType);
            }
        }
        manually = classElement.getAnnotation(Manually.class) != null;
        
        Doc doc = element.getAnnotation(Doc.class);
        if (doc != null) {
            documentation = doc.value();
        }
    }

    public OperationInfo(DataTypeInfo dataType) {
        this.dataType = dataType;
        methodName = Utils.firstLower(dataType.getSimpleNameWithoutGenerics());
    }

}
