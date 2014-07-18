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

public class OperationInfo {

    private String[] documentation;
    private String realName;
    private DataTypeInfo returnDataType;
    private DataTypeInfo oneItemReturnDataType;
    private String methodName;
    private DataTypeInfo dataType;
    private DataTypeInfo extend;
    private final HashSet<DataTypeInfo> implement = new HashSet<DataTypeInfo>(0);
    private final ArrayList<FieldInfo> fields = new ArrayList<FieldInfo>(0);
    private OperationKind operationKind;
    private EntityInfo entity;
    private TypeElement element;
    private boolean ordered;
    private boolean manually;
    private boolean distinct;
    private boolean limitToOneResult;
    private boolean ignoreLogicalDeletion;
    private String queryId;
    private String countQueryId;
    private InsertedIdOrigin insertedIdOrigin;
    private boolean deprecated;
    private final HashMap<Class<?>, Object> annotations = new HashMap<Class<?>, Object>(0);
    private boolean reuseEntityOperations;

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
        fields.add(fieldInfo);
        if (fieldInfo.isOrderBy()) {
            ordered = true;
        }
    }

    public FieldInfo getFieldByName(String name) {
        for (FieldInfo field : fields) {
            if (field.getName().equals(name)) {
                return field;
            }
        }
        return null;
    }

    public boolean hasReferenceToField(FieldInfo fieldInfo) {
        for (FieldInfo field : fields) {
            if (field == fieldInfo) {
                return true;
            }
            if (field.getRelated() == fieldInfo) {
                return true;
            }
        }
        return false;
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

    public boolean isOrdered() {
        return ordered;
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

    public boolean isDistinct() {
        return distinct;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public boolean isLimitToOneResult() {
        return limitToOneResult;
    }

    public void setLimitToOneResult(boolean limitToOneResult) {
        this.limitToOneResult = limitToOneResult;
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
        } else if (entity != null) {
            return entity.getCombined().isUseLogicalDeletion();
        } else {
            return false;
        }
    }

    public String getQueryId() {
        return queryId;
    }

    public void setQueryId(String queryId) {
        this.queryId = queryId;
    }

    public String getCountQueryId() {
        return countQueryId;
    }

    public void setCountQueryId(String countQueryId) {
        this.countQueryId = countQueryId;
    }

    public InsertedIdOrigin getInsertedIdOrigin() {
        return insertedIdOrigin;
    }

    public void setInsertedIdOrigin(InsertedIdOrigin insertedIdOrigin) {
        this.insertedIdOrigin = insertedIdOrigin;
    }

    public boolean isDeprecated() {
        return deprecated;
    }

    public void setDeprecated(boolean deprecated) {
        this.deprecated = deprecated;
    }

    public boolean isReuseEntityOperations() {
        return reuseEntityOperations;
    }

    public void setReuseEntityOperations(boolean reuseEntityOperations) {
        this.reuseEntityOperations = reuseEntityOperations;
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
        ignoreLogicalDeletion = classElement.getAnnotation(IgnoreLogicalDeletion.class) != null;

        Doc doc = element.getAnnotation(Doc.class);
        if (doc != null) {
            documentation = doc.value();
        }

        deprecated = element.getAnnotation(Deprecated.class) != null;
    }

    public OperationInfo(DataTypeInfo dataType) {
        this.dataType = dataType;
        methodName = Utils.firstLower(dataType.getSimpleNameWithoutGenerics());
    }
}
