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
package org.uaithne.generator.processors;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.MirroredTypeException;
import javax.tools.Diagnostic;
import org.uaithne.annotations.*;
import org.uaithne.annotations.executors.PlainExecutor;
import org.uaithne.generator.commons.*;

@SupportedSourceVersion(SourceVersion.RELEASE_6)
@SupportedAnnotationTypes("org.uaithne.annotations.OperationModule")
public class ExecutorModuleProcessor extends TemplateProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment re) {
        boolean generate = false;
        for (Element element : re.getElementsAnnotatedWith(OperationModule.class)) {
            if (element.getKind() == ElementKind.CLASS) {
                process(set, re, element);
                generate = true;
            }
        }
        if (generate) {
            getGenerationInfo().combineAllEntities();
            for (ExecutorModuleInfo module : getGenerationInfo().getExecutorModules()) {
                generateOperations(re, module);
            }
        }
        return true; // no further processing of this annotation type
    }
    
    public void process(Set<? extends TypeElement> set, RoundEnvironment re, Element element) {
        TypeElement moduleElement = (TypeElement) element;
        ExecutorModuleInfo executorModuleInfo = new ExecutorModuleInfo(moduleElement);
        getGenerationInfo().addExecutorModule(executorModuleInfo);
        
        for (Element enclosedModuleElement : moduleElement.getEnclosedElements()) {
            if (enclosedModuleElement.getKind() == ElementKind.CLASS) {
                Entity entity = enclosedModuleElement.getAnnotation(Entity.class);
                if (entity != null) {
                    processEntityElement(re, (TypeElement) enclosedModuleElement, executorModuleInfo);
                    continue;
                }
                EntityView entityView = enclosedModuleElement.getAnnotation(EntityView.class);
                if (entityView != null) {
                    processEntityViewElement(re, (TypeElement) enclosedModuleElement, executorModuleInfo);
                    continue;
                }
                Operation operation = enclosedModuleElement.getAnnotation(Operation.class);
                if (operation != null) {
                    processOperation(re, (TypeElement) enclosedModuleElement, executorModuleInfo, operation);
                    continue;
                }
                SelectMany selectMany = enclosedModuleElement.getAnnotation(SelectMany.class);
                if (selectMany != null) {
                    processSelectMany(re, (TypeElement) enclosedModuleElement, executorModuleInfo, selectMany);
                    continue;
                }
                SelectOne selectOne = enclosedModuleElement.getAnnotation(SelectOne.class);
                if (selectOne != null) {
                    processSelectOne(re, (TypeElement) enclosedModuleElement, executorModuleInfo, selectOne);
                    continue;
                }
                SelectPage selectPage = enclosedModuleElement.getAnnotation(SelectPage.class);
                if (selectPage != null) {
                    processSelectPage(re, (TypeElement) enclosedModuleElement, executorModuleInfo, selectPage);
                    continue;
                }
                Insert insert = enclosedModuleElement.getAnnotation(Insert.class);
                if (insert != null) {
                    processInsert(re, (TypeElement) enclosedModuleElement, executorModuleInfo, insert);
                    continue;
                }
                Update update = enclosedModuleElement.getAnnotation(Update.class);
                if (update != null) {
                    processUpdate(re, (TypeElement) enclosedModuleElement, executorModuleInfo, update);
                    continue;
                }
                Delete delete = enclosedModuleElement.getAnnotation(Delete.class);
                if (delete != null) {
                    processDelete(re, (TypeElement) enclosedModuleElement, executorModuleInfo, delete);
                    continue;
                }
            }
        }
    }
    
    public void processSelectPage(RoundEnvironment re, TypeElement element, ExecutorModuleInfo executorModuleInfo, SelectPage selectPage) {
        GenerationInfo generationInfo = getGenerationInfo();
        DataTypeInfo realResultDataType;
        try {
            realResultDataType = NamesGenerator.createResultDataType(selectPage.result());
        } catch (MirroredTypeException ex) {
            // See: http://blog.retep.org/2009/02/13/getting-class-values-from-annotations-in-an-annotationprocessor/
            realResultDataType = NamesGenerator.createDataTypeFor(ex.getTypeMirror());
        }
        if (realResultDataType == null) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Unable to find the result element", element);
            return;
        }
        DataTypeInfo resultDataType = getResultType(realResultDataType, element);
        EntityInfo entityInfo = generationInfo.getEntitiesByName().get(realResultDataType.getQualifiedName());
        
        DataTypeInfo relatedDataType;
        try {
            relatedDataType = NamesGenerator.createResultDataType(selectPage.related());
        } catch (MirroredTypeException ex) {
            // See: http://blog.retep.org/2009/02/13/getting-class-values-from-annotations-in-an-annotationprocessor/
            relatedDataType = NamesGenerator.createDataTypeFor(ex.getTypeMirror());
        }
        if (relatedDataType == null) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Unable to find the related element", element);
            return;
        }

        if (!"java.lang.Void".equals(relatedDataType.getQualifiedName())) {
            EntityInfo relatedEntityInfo = generationInfo.getEntitiesByName().get(relatedDataType.getQualifiedName());
            if (relatedEntityInfo == null) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Unable to find the related entity", element);
                return;
            }
            entityInfo = relatedEntityInfo;
        }
        
        DataTypeInfo pageResultDataType = new DataTypeInfo(generationInfo.getSharedPackage(), 
                "DataPage<" + resultDataType.getSimpleName() + ">", 
                generationInfo.getSharedPackageDot() + "DataPage");
        pageResultDataType.getImports().addAll(resultDataType.getImports());
        
        OperationInfo operationInfo = new OperationInfo(element, executorModuleInfo.getOperationPackage());
        operationInfo.setReturnDataType(pageResultDataType);
        operationInfo.setOneItemReturnDataType(resultDataType);
        operationInfo.setRealOneItemReturnDataType(realResultDataType);
        operationInfo.setOperationKind(OperationKind.SELECT_PAGE);
        operationInfo.setDistinct(selectPage.distinct());
        operationInfo.setEntity(entityInfo);
        
        DataTypeInfo operationInterface = new DataTypeInfo(generationInfo.getSharedPackage(), 
                "Operation<" + pageResultDataType.getSimpleName() + ">", 
                generationInfo.getSharedPackageDot() + "Operation");
        operationInfo.addImplement(operationInterface);
        
        DataTypeInfo dataPageRequestInterface = new DataTypeInfo(generationInfo.getSharedPackage(), 
                "DataPageRequest", 
                generationInfo.getSharedPackageDot() + "DataPageRequest");
        operationInfo.addImplement(dataPageRequestInterface);
        
        DataTypeInfo pageInfoDataType = DataTypeInfo.PAGE_INFO_DATA_TYPE;
        DataTypeInfo onlyDataCountDataType = DataTypeInfo.PAGE_ONLY_DATA_COUNT_DATA_TYPE;
        
        FieldInfo limitInfo = new FieldInfo("limit", pageInfoDataType);
        limitInfo.setMarkAsOvwrride(true);
        limitInfo.setExtra(true);
        operationInfo.addField(limitInfo);
        
        FieldInfo offsetInfo = new FieldInfo("offset", pageInfoDataType);
        offsetInfo.setMarkAsOvwrride(true);
        offsetInfo.setExtra(true);
        operationInfo.addField(offsetInfo);
        
        FieldInfo dataCountInfo = new FieldInfo("dataCount", pageInfoDataType);
        dataCountInfo.setMarkAsOvwrride(true);
        dataCountInfo.setExtra(true);
        operationInfo.addField(dataCountInfo);
        
        FieldInfo onlyDataCount = new FieldInfo("onlyDataCount", onlyDataCountDataType);
        onlyDataCount.setMarkAsOvwrride(true);
        onlyDataCount.setExtra(true);
        operationInfo.addField(onlyDataCount);
        
        loadShared(re, element, executorModuleInfo, operationInfo);
        generationInfo.addOperation(operationInfo, executorModuleInfo);
    }
    
    public void processSelectOne(RoundEnvironment re, TypeElement element, ExecutorModuleInfo executorModuleInfo, SelectOne selectOne) {
        GenerationInfo generationInfo = getGenerationInfo();
        DataTypeInfo realResultDataType;
        try {
            realResultDataType = NamesGenerator.createResultDataType(selectOne.result());
        } catch (MirroredTypeException ex) {
            // See: http://blog.retep.org/2009/02/13/getting-class-values-from-annotations-in-an-annotationprocessor/
            realResultDataType = NamesGenerator.createDataTypeFor(ex.getTypeMirror());
        }
        if (realResultDataType == null) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Unable to find the result element", element);
            return;
        }
        DataTypeInfo resultDataType = getResultType(realResultDataType, element);
        EntityInfo entityInfo = generationInfo.getEntitiesByName().get(resultDataType.getQualifiedName());
        
        DataTypeInfo relatedDataType;
        try {
            relatedDataType = NamesGenerator.createResultDataType(selectOne.related());
        } catch (MirroredTypeException ex) {
            // See: http://blog.retep.org/2009/02/13/getting-class-values-from-annotations-in-an-annotationprocessor/
            relatedDataType = NamesGenerator.createDataTypeFor(ex.getTypeMirror());
        }
        if (relatedDataType == null) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Unable to find the related element", element);
            return;
        }

        if (!"java.lang.Void".equals(relatedDataType.getQualifiedName())) {
            EntityInfo relatedEntityInfo = generationInfo.getEntitiesByName().get(relatedDataType.getQualifiedName());
            if (relatedEntityInfo == null) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Unable to find the related entity", element);
                return;
            }
            entityInfo = relatedEntityInfo;
        }
        
        OperationInfo operationInfo = new OperationInfo(element, executorModuleInfo.getOperationPackage());
        operationInfo.setReturnDataType(resultDataType);
        operationInfo.setRealReturnDataType(realResultDataType);
        operationInfo.setOperationKind(OperationKind.SELECT_ONE);
        operationInfo.setEntity(entityInfo);
        
        DataTypeInfo operationInterface = new DataTypeInfo(generationInfo.getSharedPackage(), 
                "Operation<" + resultDataType.getSimpleName() + ">", 
                generationInfo.getSharedPackageDot() + "Operation");
        operationInfo.addImplement(operationInterface);
        
        loadShared(re, element, executorModuleInfo, operationInfo);
        generationInfo.addOperation(operationInfo, executorModuleInfo);
    }
    
    public void processSelectMany(RoundEnvironment re, TypeElement element, ExecutorModuleInfo executorModuleInfo, SelectMany selectMany) {
        GenerationInfo generationInfo = getGenerationInfo();
        DataTypeInfo realResultDataType;
        try {
            realResultDataType = NamesGenerator.createResultDataType(selectMany.result());
        } catch (MirroredTypeException ex) {
            // See: http://blog.retep.org/2009/02/13/getting-class-values-from-annotations-in-an-annotationprocessor/
            realResultDataType = NamesGenerator.createDataTypeFor(ex.getTypeMirror());
        }
        if (realResultDataType == null) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Unable to find the result element", element);
            return;
        }
        DataTypeInfo resultDataType = getResultType(realResultDataType, element);
        EntityInfo entityInfo = generationInfo.getEntitiesByName().get(realResultDataType.getQualifiedName());
        
        DataTypeInfo relatedDataType;
        try {
            relatedDataType = NamesGenerator.createResultDataType(selectMany.related());
        } catch (MirroredTypeException ex) {
            // See: http://blog.retep.org/2009/02/13/getting-class-values-from-annotations-in-an-annotationprocessor/
            relatedDataType = NamesGenerator.createDataTypeFor(ex.getTypeMirror());
        }
        if (relatedDataType == null) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Unable to find the related element", element);
            return;
        }

        if (!"java.lang.Void".equals(relatedDataType.getQualifiedName())) {
            EntityInfo relatedEntityInfo = generationInfo.getEntitiesByName().get(relatedDataType.getQualifiedName());
            if (relatedEntityInfo == null) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Unable to find the related entity", element);
                return;
            }
            entityInfo = relatedEntityInfo;
        }
        
        DataTypeInfo listResultDataType;
        if (generationInfo.isUseResultInterface()) {
            listResultDataType = new DataTypeInfo(generationInfo.getSharedPackage(), "DataList<" + resultDataType.getSimpleName() + ">", generationInfo.getSharedPackageDot() + "DataList");
        } else {
            if (generationInfo.isUseConcreteCollections()) {
                listResultDataType = new DataTypeInfo("java.util", "ArrayList<" + resultDataType.getSimpleName() + ">", "java.util.ArrayList");
            } else {
                listResultDataType = new DataTypeInfo("java.util", "List<" + resultDataType.getSimpleName() + ">", "java.util.List");
            }
        }
        listResultDataType.getImports().addAll(resultDataType.getImports());
        
        OperationInfo operationInfo = new OperationInfo(element, executorModuleInfo.getOperationPackage());
        operationInfo.setReturnDataType(listResultDataType);
        operationInfo.setOneItemReturnDataType(resultDataType);
        operationInfo.setRealOneItemReturnDataType(realResultDataType);
        operationInfo.setOperationKind(OperationKind.SELECT_MANY);
        operationInfo.setDistinct(selectMany.distinct());
        operationInfo.setEntity(entityInfo);
        
        DataTypeInfo operationInterface = new DataTypeInfo(generationInfo.getSharedPackage(), 
                "Operation<" + listResultDataType.getSimpleName() + ">", 
                generationInfo.getSharedPackageDot() + "Operation");
        operationInfo.addImplement(operationInterface);
        
        loadShared(re, element, executorModuleInfo, operationInfo);
        generationInfo.addOperation(operationInfo, executorModuleInfo);
    }
    
    public void processOperation(RoundEnvironment re, TypeElement element, ExecutorModuleInfo executorModuleInfo, Operation operation) {
        GenerationInfo generationInfo = getGenerationInfo();
        DataTypeInfo realResultDataType;
        try {
            realResultDataType = NamesGenerator.createResultDataType(operation.result());
        } catch (MirroredTypeException ex) {
            // See: http://blog.retep.org/2009/02/13/getting-class-values-from-annotations-in-an-annotationprocessor/
            realResultDataType = NamesGenerator.createDataTypeFor(ex.getTypeMirror());
        }
        if (realResultDataType == null) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Unable to find the result element", element);
            return;
        }
        DataTypeInfo resultDataType = getResultType(realResultDataType, element);
        EntityInfo entityInfo = generationInfo.getEntitiesByName().get(realResultDataType.getQualifiedName());
        
        OperationInfo operationInfo = new OperationInfo(element, executorModuleInfo.getOperationPackage());
        operationInfo.setReturnDataType(resultDataType);
        operationInfo.setRealReturnDataType(realResultDataType);
        operationInfo.setOperationKind(OperationKind.CUSTOM);
        operationInfo.setEntity(entityInfo);
        
        DataTypeInfo operationInterface = new DataTypeInfo(generationInfo.getSharedPackage(), 
                "Operation<" + resultDataType.getSimpleName() + ">", 
                generationInfo.getSharedPackageDot() + "Operation");
        operationInfo.addImplement(operationInterface);
        operationInfo.setManually(true);
        
        loadShared(re, element, executorModuleInfo, operationInfo);
        generationInfo.addOperation(operationInfo, executorModuleInfo);
    }
    
    public void processInsert(RoundEnvironment re, TypeElement element, ExecutorModuleInfo executorModuleInfo, Insert operation) {
        GenerationInfo generationInfo = getGenerationInfo();
        EntityInfo entity;
        DataTypeInfo entityDataType;
        DataTypeInfo realResultDataType;
        OperationKind operationKind;
        
        try {
            entityDataType = NamesGenerator.createResultDataType(operation.related());
        } catch (MirroredTypeException ex) {
            // See: http://blog.retep.org/2009/02/13/getting-class-values-from-annotations-in-an-annotationprocessor/
            entityDataType = NamesGenerator.createDataTypeFor(ex.getTypeMirror());
        }
        if (entityDataType == null) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Unable to find the related element", element);
            return;
        }

        entity = generationInfo.getEntitiesByName().get(entityDataType.getQualifiedName());
        if (entity == null) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Unable to find the related entity", element);
            return;
        }
        
        if (operation.returnLastInsertedId()) {
            operationKind = OperationKind.CUSTOM_INSERT_WITH_ID;
            FieldInfo id = entity.getCombined().getFirstIdField();
            if (id != null) {
                realResultDataType = id.getDataType();
            } else {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Unable to find the related entity id field", element);
                return;
            }
        } else {
            realResultDataType = DataTypeInfo.AFFECTED_ROW_COUNT_DATA_TYPE;
            operationKind = OperationKind.CUSTOM_INSERT;
        }
        DataTypeInfo resultDataType = getResultType(realResultDataType, element);
        
        OperationInfo operationInfo = new OperationInfo(element, executorModuleInfo.getOperationPackage());
        operationInfo.setEntity(entity);
        operationInfo.setReturnDataType(resultDataType);
        operationInfo.setRealReturnDataType(realResultDataType);
        operationInfo.setOperationKind(operationKind);
        
        DataTypeInfo operationInterface = new DataTypeInfo(generationInfo.getSharedPackage(), 
                "Operation<" + resultDataType.getSimpleName() + ">", 
                generationInfo.getSharedPackageDot() + "Operation");
        operationInfo.addImplement(operationInterface);
        
        loadShared(re, element, executorModuleInfo, operationInfo);
        
        generationInfo.addOperation(operationInfo, executorModuleInfo);
    }
    
    public void processUpdate(RoundEnvironment re, TypeElement element, ExecutorModuleInfo executorModuleInfo, Update operation) {
        GenerationInfo generationInfo = getGenerationInfo();
        DataTypeInfo realResultDataType = DataTypeInfo.AFFECTED_ROW_COUNT_DATA_TYPE;
        EntityInfo entity;
        DataTypeInfo entityDataType;
        
        try {
            entityDataType = NamesGenerator.createResultDataType(operation.related());
        } catch (MirroredTypeException ex) {
            // See: http://blog.retep.org/2009/02/13/getting-class-values-from-annotations-in-an-annotationprocessor/
            entityDataType = NamesGenerator.createDataTypeFor(ex.getTypeMirror());
        }
        if (entityDataType == null) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Unable to find the related element", element);
            return;
        }

        entity = generationInfo.getEntitiesByName().get(entityDataType.getQualifiedName());
        if (entity == null) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Unable to find the related entity", element);
            return;
        }
        DataTypeInfo resultDataType = getResultType(realResultDataType, element);
        
        OperationInfo operationInfo = new OperationInfo(element, executorModuleInfo.getOperationPackage());
        operationInfo.setEntity(entity);
        operationInfo.setReturnDataType(resultDataType);
        operationInfo.setRealReturnDataType(realResultDataType);
        operationInfo.setOperationKind(OperationKind.CUSTOM_UPDATE);
        
        DataTypeInfo operationInterface = new DataTypeInfo(generationInfo.getSharedPackage(), 
                "Operation<" + resultDataType.getSimpleName() + ">", 
                generationInfo.getSharedPackageDot() + "Operation");
        operationInfo.addImplement(operationInterface);
        
        loadShared(re, element, executorModuleInfo, operationInfo);
        generationInfo.addOperation(operationInfo, executorModuleInfo);
    }
    
    public void processDelete(RoundEnvironment re, TypeElement element, ExecutorModuleInfo executorModuleInfo, Delete operation) {
        GenerationInfo generationInfo = getGenerationInfo();
        DataTypeInfo realResultDataType = DataTypeInfo.AFFECTED_ROW_COUNT_DATA_TYPE;
        EntityInfo entity;
        DataTypeInfo entityDataType;
        
        try {
            entityDataType = NamesGenerator.createResultDataType(operation.related());
        } catch (MirroredTypeException ex) {
            // See: http://blog.retep.org/2009/02/13/getting-class-values-from-annotations-in-an-annotationprocessor/
            entityDataType = NamesGenerator.createDataTypeFor(ex.getTypeMirror());
        }
        if (entityDataType == null) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Unable to find the related element", element);
            return;
        }

        entity = generationInfo.getEntitiesByName().get(entityDataType.getQualifiedName());
        if (entity == null) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Unable to find the related entity", element);
            return;
        }
        DataTypeInfo resultDataType = getResultType(realResultDataType, element);
        
        OperationInfo operationInfo = new OperationInfo(element, executorModuleInfo.getOperationPackage());
        operationInfo.setEntity(entity);
        operationInfo.setReturnDataType(resultDataType);
        operationInfo.setReturnDataType(realResultDataType);
        operationInfo.setOperationKind(OperationKind.CUSTOM_DELETE);
        
        DataTypeInfo operationInterface = new DataTypeInfo(generationInfo.getSharedPackage(), 
                "Operation<" + resultDataType.getSimpleName() + ">", 
                generationInfo.getSharedPackageDot() + "Operation");
        operationInfo.addImplement(operationInterface);
        
        loadShared(re, element, executorModuleInfo, operationInfo);
        generationInfo.addOperation(operationInfo, executorModuleInfo);
    }
    
    public void loadShared(RoundEnvironment re, TypeElement element, ExecutorModuleInfo executorModuleInfo, OperationInfo operationInfo) {
        for (Element enclosedElement : element.getEnclosedElements()) {
            if (enclosedElement.getKind() == ElementKind.FIELD) {
                VariableElement ve = (VariableElement) enclosedElement;
                
                FieldInfo fi = new FieldInfo(ve);
                if (fi.getMappedName() == null) {
                    EntityInfo entityInfo = operationInfo.getEntity();
                    if (entityInfo != null) {
                        FieldInfo fieldInEntity = entityInfo.getFieldByName(fi.getName());
                        fi.setRelated(fieldInEntity);
                    }
                }
                operationInfo.addField(fi);
            }
        }
    }
    
    public void processEntityViewElement(RoundEnvironment re, TypeElement element, ExecutorModuleInfo executorModuleInfo) {
        GenerationInfo generationInfo = getGenerationInfo();
        EntityInfo entityInfo = generationInfo.getEntitiesByRealName().get(element.getQualifiedName().toString());
        if (entityInfo == null) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Unable to find the entity view element", element);
            return;
        }
        executorModuleInfo.addEntity(entityInfo);
    }
    
    public void processEntityElement(RoundEnvironment re, TypeElement element, ExecutorModuleInfo executorModuleInfo) {
        GenerationInfo generationInfo = getGenerationInfo();
        EntityInfo entityInfo = generationInfo.getEntitiesByRealName().get(element.getQualifiedName().toString());
        if (entityInfo == null) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Unable to find the entity element", element);
            return;
        }
        
        EntityInfo combinedEntity = entityInfo.getCombined();
        FieldInfo idInfo;
        List<FieldInfo> idFields = combinedEntity.getIdFields();
        if (idFields.size() <= 0) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "An entity must define an id for use in an executor group", element);
            return;
        } else if (idFields.size() > 1) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "An entity must define only one id for use in an executor group", element);
            return;
        } else {
            idInfo = combinedEntity.getFirstIdField();
            if (idInfo == null) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Unable to find the id field element", element);
                return;
            }
        }
        idInfo = new FieldInfo("id", idInfo);
        idInfo.setMarkAsOvwrride(true);
        idInfo.setOptional(false);
        idInfo.setIdentifier(true);
        DataTypeInfo realIdDataType = idInfo.getDataType();
        DataTypeInfo idDataType;
        
        DataTypeInfo entityDataType = entityInfo.getDataType();
        FieldInfo valueInfo = new FieldInfo("value", entityDataType);
        valueInfo.setMarkAsOvwrride(true);
        valueInfo.setOptional(false);
        valueInfo.setIdentifier(false);
        DataTypeInfo realAffectedRowCountDataType = DataTypeInfo.AFFECTED_ROW_COUNT_DATA_TYPE;
        DataTypeInfo affectedRowCountDataType;
        
        if (generationInfo.isUseResultInterface()) {
            affectedRowCountDataType = getResultType(realAffectedRowCountDataType, element);
            idDataType = getResultType(realIdDataType, element);
        } else {
            affectedRowCountDataType = realAffectedRowCountDataType;
            idDataType = realIdDataType;
        }
        
        executorModuleInfo.addEntity(entityInfo);
        
        /* ****************************************************************************************
         * *** Delete By Id operation
         */
        {
            DataTypeInfo deleteOperationName = new DataTypeInfo(executorModuleInfo.getOperationPackage(), 
                    "Delete" + entityDataType.getSimpleName() + "ById");
            OperationInfo deleteOperationInfo = new OperationInfo(deleteOperationName);
            deleteOperationInfo.setReturnDataType(affectedRowCountDataType);
            deleteOperationInfo.setRealReturnDataType(realAffectedRowCountDataType);
            deleteOperationInfo.setOperationKind(OperationKind.DELETE_BY_ID);

            DataTypeInfo deleteOperationInterface = new DataTypeInfo(generationInfo.getSharedPackage(), 
                    "DeleteByIdOperation<" + realIdDataType.getSimpleName() + ", " + affectedRowCountDataType.getSimpleName() + ">", 
                    generationInfo.getSharedPackageDot() + "DeleteByIdOperation");
            deleteOperationInterface.getImports().addAll(realIdDataType.getImports());
            deleteOperationInterface.getImports().addAll(affectedRowCountDataType.getImports());
            deleteOperationInfo.addImplement(deleteOperationInterface);

            deleteOperationInfo.addField(idInfo);
            deleteOperationInfo.setEntity(entityInfo);
            deleteOperationInfo.setManually(entityInfo.getCombined().isManually());
            generationInfo.addOperation(deleteOperationInfo, executorModuleInfo);
        }
        
        /* ****************************************************************************************
         * *** Insert operation
         */
        {
            DataTypeInfo insertOperationName = new DataTypeInfo(executorModuleInfo.getOperationPackage(), 
                    "Insert" + entityDataType.getSimpleName());
            OperationInfo insertOperationInfo = new OperationInfo(insertOperationName);
            insertOperationInfo.setReturnDataType(idDataType);
            insertOperationInfo.setRealReturnDataType(realIdDataType);
            insertOperationInfo.setOperationKind(OperationKind.INSERT);

            DataTypeInfo insertOperationInterface = new DataTypeInfo(generationInfo.getSharedPackage(), 
                    "InsertValueOperation<" + entityDataType.getSimpleName() + ", " + idDataType.getSimpleName() + ">", 
                    generationInfo.getSharedPackageDot() + "InsertValueOperation");
            insertOperationInterface.getImports().addAll(entityDataType.getImports());
            insertOperationInterface.getImports().addAll(idDataType.getImports());
            insertOperationInfo.addImplement(insertOperationInterface);

            insertOperationInfo.addField(valueInfo);
            insertOperationInfo.setEntity(entityInfo);
            insertOperationInfo.setManually(entityInfo.getCombined().isManually());
            generationInfo.addOperation(insertOperationInfo, executorModuleInfo);
        }
        
        if (generationInfo.isGenerateJustOperationsEnabled()) {
            /* ****************************************************************************************
            * *** Just Insert operation
            */
            {
                DataTypeInfo justInsertOperationName = new DataTypeInfo(executorModuleInfo.getOperationPackage(), 
                        "JustInsert" + entityDataType.getSimpleName());
                OperationInfo justInsertOperationInfo = new OperationInfo(justInsertOperationName);
                justInsertOperationInfo.setReturnDataType(affectedRowCountDataType);
                justInsertOperationInfo.setRealReturnDataType(realAffectedRowCountDataType);
                justInsertOperationInfo.setOperationKind(OperationKind.JUST_INSERT);

                DataTypeInfo justInsertOperationInterface = new DataTypeInfo(generationInfo.getSharedPackage(), 
                        "JustInsertValueOperation<" + entityDataType.getSimpleName() + ", " + affectedRowCountDataType.getSimpleName() + ">", 
                        generationInfo.getSharedPackageDot() + "JustInsertValueOperation");
                justInsertOperationInterface.getImports().addAll(entityDataType.getImports());
                justInsertOperationInterface.getImports().addAll(affectedRowCountDataType.getImports());
                justInsertOperationInfo.addImplement(justInsertOperationInterface);

                justInsertOperationInfo.addField(valueInfo);
                justInsertOperationInfo.setEntity(entityInfo);
                justInsertOperationInfo.setManually(entityInfo.getCombined().isManually());
                generationInfo.addOperation(justInsertOperationInfo, executorModuleInfo);
            }
        }
        
        if (generationInfo.isGenerateSaveOperationsEnabled()) {
            /* ****************************************************************************************
            * *** Save operation
            */
            {
                DataTypeInfo saveOperationName = new DataTypeInfo(executorModuleInfo.getOperationPackage(), 
                        "Save" + entityDataType.getSimpleName());
                OperationInfo saveOperationInfo = new OperationInfo(saveOperationName);
                saveOperationInfo.setReturnDataType(idDataType);
                saveOperationInfo.setRealReturnDataType(realIdDataType);
                saveOperationInfo.setOperationKind(OperationKind.SAVE);

                DataTypeInfo saveOperationInterface = new DataTypeInfo(generationInfo.getSharedPackage(), 
                        "SaveValueOperation<" + entityDataType.getSimpleName() + ", " + idDataType.getSimpleName() + ">", 
                        generationInfo.getSharedPackageDot() + "SaveValueOperation");
                saveOperationInterface.getImports().addAll(entityDataType.getImports());
                saveOperationInterface.getImports().addAll(idDataType.getImports());
                saveOperationInfo.addImplement(saveOperationInterface);

                saveOperationInfo.addField(valueInfo);
                saveOperationInfo.setEntity(entityInfo);
                saveOperationInfo.setManually(entityInfo.getCombined().isManually());
                generationInfo.addOperation(saveOperationInfo, executorModuleInfo);
            }
        }
        
        if (generationInfo.isGenerateJustOperationsEnabled() && generationInfo.isGenerateSaveOperationsEnabled()) {
            /* ****************************************************************************************
            * *** Just Save operation
            */
            {
                DataTypeInfo justSaveOperationName = new DataTypeInfo(executorModuleInfo.getOperationPackage(), 
                        "JustSave" + entityDataType.getSimpleName());
                OperationInfo justSaveOperationInfo = new OperationInfo(justSaveOperationName);
                justSaveOperationInfo.setReturnDataType(affectedRowCountDataType);
                justSaveOperationInfo.setRealReturnDataType(realAffectedRowCountDataType);
                justSaveOperationInfo.setOperationKind(OperationKind.JUST_SAVE);

                DataTypeInfo justSaveOperationInterface = new DataTypeInfo(generationInfo.getSharedPackage(), 
                        "JustSaveValueOperation<" + entityDataType.getSimpleName() + ", " + affectedRowCountDataType.getSimpleName() + ">", 
                        generationInfo.getSharedPackageDot() + "JustSaveValueOperation");
                justSaveOperationInterface.getImports().addAll(entityDataType.getImports());
                justSaveOperationInterface.getImports().addAll(affectedRowCountDataType.getImports());
                justSaveOperationInfo.addImplement(justSaveOperationInterface);

                justSaveOperationInfo.addField(valueInfo);
                justSaveOperationInfo.setEntity(entityInfo);
                justSaveOperationInfo.setManually(entityInfo.getCombined().isManually());
                generationInfo.addOperation(justSaveOperationInfo, executorModuleInfo);
            }
        }
        
        /* ****************************************************************************************
         * *** Select By Id operation
         */
        {
            DataTypeInfo selectOperationName = new DataTypeInfo(executorModuleInfo.getOperationPackage(), 
                    "Select" + entityDataType.getSimpleName() + "ById");
            OperationInfo selectOperationInfo = new OperationInfo(selectOperationName);
            selectOperationInfo.setReturnDataType(entityDataType);
            selectOperationInfo.setOperationKind(OperationKind.SELECT_BY_ID);

            DataTypeInfo selectOperationInterface = new DataTypeInfo(generationInfo.getSharedPackage(), 
                    "SelectByIdOperation<" + realIdDataType.getSimpleName() + ", " + entityDataType.getSimpleName() + ">", 
                    generationInfo.getSharedPackageDot() + "SelectByIdOperation");
            selectOperationInterface.getImports().addAll(realIdDataType.getImports());
            selectOperationInterface.getImports().addAll(entityDataType.getImports());
            selectOperationInfo.addImplement(selectOperationInterface);

            selectOperationInfo.addField(idInfo);
            selectOperationInfo.setEntity(entityInfo);
            selectOperationInfo.setManually(entityInfo.getCombined().isManually());
            generationInfo.addOperation(selectOperationInfo, executorModuleInfo);
        }
        
        /* ****************************************************************************************
         * *** Update operation
         */
        {
            DataTypeInfo updateOperationName = new DataTypeInfo(executorModuleInfo.getOperationPackage(), 
                    "Update" + entityDataType.getSimpleName());
            OperationInfo updateOperationInfo = new OperationInfo(updateOperationName);
            updateOperationInfo.setReturnDataType(affectedRowCountDataType);
            updateOperationInfo.setRealReturnDataType(realAffectedRowCountDataType);
            updateOperationInfo.setOperationKind(OperationKind.UPDATE);

            DataTypeInfo updateOperationInterface = new DataTypeInfo(generationInfo.getSharedPackage(), 
                    "UpdateValueOperation<" + entityDataType.getSimpleName() + ", " + affectedRowCountDataType.getSimpleName() + ">", 
                    generationInfo.getSharedPackageDot() + "UpdateValueOperation");
            updateOperationInterface.getImports().addAll(entityDataType.getImports());
            updateOperationInterface.getImports().addAll(affectedRowCountDataType.getImports());
            updateOperationInfo.addImplement(updateOperationInterface);

            updateOperationInfo.addField(valueInfo);
            updateOperationInfo.setEntity(entityInfo);
            updateOperationInfo.setManually(entityInfo.getCombined().isManually());
            generationInfo.addOperation(updateOperationInfo, executorModuleInfo);
        }
        
        /* ****************************************************************************************
         * *** Merge operation
         */
        if (generationInfo.isGenerateMergeOperationsEnabled()) {
            DataTypeInfo mergeOperationName = new DataTypeInfo(executorModuleInfo.getOperationPackage(), 
                    "Merge" + entityDataType.getSimpleName());
            OperationInfo mergeOperationInfo = new OperationInfo(mergeOperationName);
            mergeOperationInfo.setReturnDataType(affectedRowCountDataType);
            mergeOperationInfo.setRealReturnDataType(realAffectedRowCountDataType);
            mergeOperationInfo.setOperationKind(OperationKind.MERGE);

            DataTypeInfo mergeOperationInterface = new DataTypeInfo(generationInfo.getSharedPackage(), 
                    "MergeValueOperation<" + entityDataType.getSimpleName() + ", " + affectedRowCountDataType.getSimpleName() + ">", 
                    generationInfo.getSharedPackageDot() + "MergeValueOperation");
            mergeOperationInterface.getImports().addAll(entityDataType.getImports());
            mergeOperationInterface.getImports().addAll(affectedRowCountDataType.getImports());
            mergeOperationInfo.addImplement(mergeOperationInterface);

            mergeOperationInfo.addField(valueInfo);
            mergeOperationInfo.setEntity(entityInfo);
            mergeOperationInfo.setManually(entityInfo.getCombined().isManually());
            generationInfo.addOperation(mergeOperationInfo, executorModuleInfo);
        }
    }
    
    public void generateOperations(RoundEnvironment re, ExecutorModuleInfo executorModuleInfo) {
        GenerationInfo generationInfo = getGenerationInfo();
        
        HashMap<String, Object> data = createDefaultData();
        data.put("operations", executorModuleInfo.getOperations());
        
        String packageName = executorModuleInfo.getOperationPackage();
        boolean inShared = generationInfo.getSharedPackage().equals(packageName);
        HashSet<String> executorModuleImports = new HashSet<String>();
        executorModuleInfo.appendDefinitionImports(packageName, executorModuleImports);
        
        HashSet<String> executorImports = new HashSet<String>();
        executorImports.addAll(executorModuleImports);
        if (!inShared) {
            executorImports.add(generationInfo.getSharedPackage() + ".Executor");
        }
        data.put("imports", executorImports);
        data.put("documentation", executorModuleInfo.getDocumentation());

        String executorName = executorModuleInfo.getNameUpper() + "Executor";
        processExecutorTemplate(executorName, executorModuleInfo.getOperationPackage(), data, executorModuleInfo.getElement());
        
        for (OperationInfo operation : executorModuleInfo.getOperations()) {
            HashSet<String> operationImports = new HashSet<String>();
            operation.appendFullImports(packageName, operationImports);
            if (!inShared) {
                operationImports.add(generationInfo.getSharedPackage() + ".Executor");
                if (generationInfo.isUseResultWrapperInterface()) {
                    operationImports.add(generationInfo.getSharedPackage() + ".ResultWrapper");
                }
            }
            
            HashMap<String, Object> dataOperation = createDefaultData();
            dataOperation.put("executorName", executorName);
            dataOperation.put("operation", operation);
            dataOperation.put("imports", operationImports);
            processOperationTemplate(operation, packageName, dataOperation, operation.getElement());
        }
        
        data.put("executorInterfaceName", executorName);
        data.put("imports", executorModuleImports);
        if (!inShared) {
            executorModuleImports.add(generationInfo.getSharedPackage() + ".Operation");
        }
        if (generationInfo.isUseResultInterface()) {
            executorModuleImports.add(generationInfo.getSharedPackage() + ".Result");
        }
        processChainedExecutorTemplate(executorModuleInfo.getNameUpper() + "ChainedExecutor", packageName, data, executorModuleInfo.getElement());
        
        String executorAbstractName = executorModuleInfo.getNameUpper() + "AbstractExecutor";
        HashSet<String> abstractExecutorImports = new HashSet<String>();
        if (!inShared) {
            abstractExecutorImports.add(generationInfo.getSharedPackage() + ".Operation");
        }
        if (generationInfo.isUseResultInterface()) {
            abstractExecutorImports.add(generationInfo.getSharedPackage() + ".Result");
        }
        data.put("imports", abstractExecutorImports);
        data.put("executorAbstractName", executorAbstractName);
        processAbstractExecutorTemplate(executorAbstractName, packageName, data, executorModuleInfo.getElement());

        data.put("imports", executorModuleImports);
        if (!inShared) {
            executorModuleImports.add(generationInfo.getSharedPackage() + ".ExecutorGroup");
        }
        processChainedGroupingExecutorTemplate(executorModuleInfo.getNameUpper() + "ChainedGroupingExecutor", packageName, data, executorModuleInfo.getElement());
        
        if (executorModuleInfo.getAnnotation(PlainExecutor.class) != null) {
            boolean useConcreteCollections = generationInfo.isUseConcreteCollections();
            String plainExecutorName = executorModuleInfo.getNameUpper() + "PlainExecutor";
            HashSet<String> plainExecutorImports = new HashSet<String>();
            executorModuleInfo.appendPlainDefinitionImports(packageName, plainExecutorImports, useConcreteCollections);
            data.put("imports", plainExecutorImports);
            if (useConcreteCollections) {
                data.put("listName", "ArrayList");
            } else {
                data.put("listName", "List");
            }
            processPlainExecutorTemplate(plainExecutorName, packageName, data, executorModuleInfo.getElement());

            HashSet<String> plainExecutorImplementationImports = new HashSet<String>();
            executorModuleInfo.appendPlainImplementationImports(packageName, plainExecutorImplementationImports, useConcreteCollections);
            data.put("imports", plainExecutorImplementationImports);
            data.put("plainExecutorName", plainExecutorName);
            data.put("executorInterfaceName", executorName);
            processPlainChainedExecutorTemplate(executorModuleInfo.getNameUpper() + "PlainChainedExecutor", packageName, data, executorModuleInfo.getElement());

            if (!inShared) {
                plainExecutorImplementationImports.add(generationInfo.getSharedPackage() + ".ExecutorGroup");
            }
            processPlainChainedGroupingExecutorTemplate(executorModuleInfo.getNameUpper() + "PlainChainedGroupingExecutor", packageName, data, executorModuleInfo.getElement());
        }
    }
    
    public boolean processExecutorTemplate(String className, String packageName, HashMap<String, Object> data, Element element) {
        return processClassTemplate("operations/Executor.ftl", packageName, className, data, element);
    }
    
    public boolean processOperationTemplate(OperationInfo operation, String packageName, HashMap<String, Object> data, Element element) {
        return processClassTemplate("operations/Operation.ftl", packageName, operation.getDataType().getSimpleName(), data, element);
    }

    public boolean processChainedExecutorTemplate(String className, String packageName, HashMap<String, Object> data, Element element) {
        return processClassTemplate("operations/ChainedExecutor.ftl", packageName, className, data, element);
    }

    public boolean processChainedGroupingExecutorTemplate(String className, String packageName, HashMap<String, Object> data, Element element) {
        return processClassTemplate("operations/ChainedGroupingExecutor.ftl", packageName, className, data, element);
    }
    
    public boolean processAbstractExecutorTemplate(String className, String packageName, HashMap<String, Object> data, Element element) {
        return processClassTemplate("operations/AbstractExecutor.ftl", packageName, className, data, element);
    }
    
    public boolean processPlainExecutorTemplate(String className, String packageName, HashMap<String, Object> data, Element element) {
        return processClassTemplate("operations/PlainExecutor.ftl", packageName, className, data, element);
    }
    
    public boolean processPlainChainedExecutorTemplate(String className, String packageName, HashMap<String, Object> data, Element element) {
        return processClassTemplate("operations/PlainChainedExecutor.ftl", packageName, className, data, element);
    }
    
    public boolean processPlainChainedGroupingExecutorTemplate(String className, String packageName, HashMap<String, Object> data, Element element) {
        return processClassTemplate("operations/PlainChainedGroupingExecutor.ftl", packageName, className, data, element);
    }
    
    public boolean processResultTemplate(String className, String packageName, HashMap<String, Object> data, Element element) {
        return processClassTemplate("Result.ftl", packageName, className, data, element);
    }

    public DataTypeInfo getResultType(DataTypeInfo resultType, TypeElement element) {
        GenerationInfo generationInfo = getGenerationInfo();
        if (generationInfo.isUseResultInterface()) {
            EntityInfo entityInfo = generationInfo.getEntityByName(resultType);
            if (entityInfo != null) {
                return resultType;
            }
            DataTypeInfo resultWrapper = generationInfo.getResultTypeByName(resultType);
            if (resultWrapper != null) {
                return resultWrapper;
            }
            
            String name;
            String packageName = resultType.getPackageName();
            if (packageName == null || packageName.isEmpty()) {
                name = resultType.getSimpleName() + "Result";
            } else {
                if ("java.lang".equals(packageName)) {
                    name = resultType.getSimpleName();
                } else if ("java.math".equals(packageName)) {
                    name = resultType.getSimpleName();
                } else if ("java.util".equals(packageName)) {
                    name = resultType.getSimpleName();
                } else {
                    name = resultType.getQualifiedName();
                }

                String[] parts = name.split("\\.");
                for (int i = 0; i < parts.length; i++) {
                    parts[i] = Utils.firstUpper(parts[i]);
                }

                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < parts.length; i++) {
                    sb.append(parts[i]);
                }
                sb.append("Result");
                name = sb.toString();
            }
            
            HashMap<String, Object> data = createDefaultData();
            data.put("valueType", resultType);

            packageName = generationInfo.getResultsPackage();
            boolean inShared = generationInfo.getSharedPackage().equals(packageName);

            HashSet<String> imports = new HashSet<String>();
            resultType.appendImports(packageName, imports);
            if (!inShared) {
                imports.add(generationInfo.getSharedPackage() + ".Result");
            }
            data.put("imports", imports);

            processResultTemplate(name, packageName, data, null);

            DataTypeInfo result = new DataTypeInfo(packageName, name);
            result.getImports().addAll(resultType.getImports());
            generationInfo.addResultType(resultType, result);
            return result;
        } else {
            return resultType;
        }
    }
}
