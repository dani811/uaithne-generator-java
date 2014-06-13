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
import static org.uaithne.generator.commons.TemplateProcessor.getGenerationInfo;
import org.uaithne.generator.templates.operations.AbstractExecutorTemplate;
import org.uaithne.generator.templates.operations.ChainedExecutorTemplate;
import org.uaithne.generator.templates.operations.ChainedGroupingExecutorTemplate;
import org.uaithne.generator.templates.operations.ExecutorTemplate;
import org.uaithne.generator.templates.operations.OperationTemplate;
import org.uaithne.generator.templates.operations.PlainChainedExecutorTemplate;
import org.uaithne.generator.templates.operations.PlainChainedGroupingExecutorTemplate;
import org.uaithne.generator.templates.operations.PlainExecutorTemplate;

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
            getGenerationInfo().combineAllEntities(false, processingEnv);
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
                InsertEntity insertEntity = enclosedModuleElement.getAnnotation(InsertEntity.class);
                if (insertEntity != null) {
                    processInsertEntity(re, (TypeElement) enclosedModuleElement, executorModuleInfo, insertEntity);
                    continue;
                }
                UpdateEntity updateEntity = enclosedModuleElement.getAnnotation(UpdateEntity.class);
                if (updateEntity != null) {
                    processUpdateEntity(re, (TypeElement) enclosedModuleElement, executorModuleInfo, updateEntity);
                    continue;
                }
                DeleteEntityById deleteEntityById = enclosedModuleElement.getAnnotation(DeleteEntityById.class);
                if (deleteEntityById != null) {
                    processDeleteEntityById(re, (TypeElement) enclosedModuleElement, executorModuleInfo, deleteEntityById);
                    continue;
                }
                SelectEntityById selectEntityById = enclosedModuleElement.getAnnotation(SelectEntityById.class);
                if (selectEntityById != null) {
                    processSelectEntityById(re, (TypeElement) enclosedModuleElement, executorModuleInfo, selectEntityById);
                    continue;
                }
                SaveEntity saveEntity = enclosedModuleElement.getAnnotation(SaveEntity.class);
                if (saveEntity != null) {
                    processSaveEntity(re, (TypeElement) enclosedModuleElement, executorModuleInfo, saveEntity);
                    continue;
                }
                MergeEntity mergeEntity = enclosedModuleElement.getAnnotation(MergeEntity.class);
                if (mergeEntity != null) {
                    processMergeEntity(re, (TypeElement) enclosedModuleElement, executorModuleInfo, mergeEntity);
                }
            }
        }
        
        for (Element enclosedModuleElement : moduleElement.getEnclosedElements()) {
            if (enclosedModuleElement.getKind() == ElementKind.CLASS) {
                Entity entity = enclosedModuleElement.getAnnotation(Entity.class);
                if (entity != null) {
                    processEntityElement(re, (TypeElement) enclosedModuleElement, executorModuleInfo);
                }
            }
        }
    }

    public void processSelectPage(RoundEnvironment re, TypeElement element, ExecutorModuleInfo executorModuleInfo, SelectPage selectPage) {
        GenerationInfo generationInfo = getGenerationInfo();
        DataTypeInfo resultDataType;
        try {
            resultDataType = NamesGenerator.createResultDataType(selectPage.result());
        } catch (MirroredTypeException ex) {
            // See: http://blog.retep.org/2009/02/13/getting-class-values-from-annotations-in-an-annotationprocessor/
            resultDataType = NamesGenerator.createDataTypeFor(ex.getTypeMirror());
        }
        if (resultDataType == null) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Unable to find the result element", element);
            return;
        }
        EntityInfo entityInfo = generationInfo.getEntityByName(resultDataType);

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

        if (!relatedDataType.isVoid()) {
            EntityInfo relatedEntityInfo = generationInfo.getEntityByName(relatedDataType);
            if (relatedEntityInfo == null) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Unable to find the related entity", element);
                return;
            }
            if (entityInfo == null) {
                entityInfo = relatedEntityInfo;
            } else {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Related is not allowed when the operation result is an entity", element);
            }
        }

        DataTypeInfo pageResultDataType = DataTypeInfo.DATA_PAGE_DATA_TYPE.of(resultDataType);
        pageResultDataType.getImports().addAll(resultDataType.getImports());

        OperationInfo operationInfo = new OperationInfo(element, executorModuleInfo.getOperationPackage());
        operationInfo.setReturnDataType(pageResultDataType);
        operationInfo.setOneItemReturnDataType(resultDataType);
        operationInfo.setOperationKind(OperationKind.SELECT_PAGE);
        operationInfo.setDistinct(selectPage.distinct());
        operationInfo.setEntity(entityInfo);

        DataTypeInfo operationInterface = DataTypeInfo.OPERATION_DATA_TYPE.of(pageResultDataType);
        operationInfo.addImplement(operationInterface);

        DataTypeInfo dataPageRequestInterface = DataTypeInfo.DATA_PAGE_REQUEST_DATA_TYPE;
        operationInfo.addImplement(dataPageRequestInterface);

        DataTypeInfo pageInfoDataType = DataTypeInfo.PAGE_INFO_DATA_TYPE;
        DataTypeInfo onlyDataCountDataType = DataTypeInfo.PAGE_ONLY_DATA_COUNT_DATA_TYPE;

        loadShared(re, element, executorModuleInfo, operationInfo);
        
        FieldInfo limitInfo = new FieldInfo("limit", pageInfoDataType);
        limitInfo.setMarkAsOvwrride(true);
        limitInfo.setExcludedFromConstructor(true);
        limitInfo.setManually(true);
        operationInfo.addField(limitInfo);

        FieldInfo offsetInfo = new FieldInfo("offset", pageInfoDataType);
        offsetInfo.setMarkAsOvwrride(true);
        offsetInfo.setExcludedFromConstructor(true);
        offsetInfo.setManually(true);
        operationInfo.addField(offsetInfo);

        FieldInfo dataCountInfo = new FieldInfo("dataCount", pageInfoDataType);
        dataCountInfo.setMarkAsOvwrride(true);
        dataCountInfo.setExcludedFromConstructor(true);
        dataCountInfo.setManually(true);
        operationInfo.addField(dataCountInfo);

        FieldInfo onlyDataCount = new FieldInfo("onlyDataCount", onlyDataCountDataType);
        onlyDataCount.setMarkAsOvwrride(true);
        onlyDataCount.setExcludedFromConstructor(true);
        onlyDataCount.setManually(true);
        operationInfo.addField(onlyDataCount);

        generationInfo.addOperation(operationInfo, executorModuleInfo);
    }

    public void processSelectOne(RoundEnvironment re, TypeElement element, ExecutorModuleInfo executorModuleInfo, SelectOne selectOne) {
        GenerationInfo generationInfo = getGenerationInfo();
        DataTypeInfo resultDataType;
        try {
            resultDataType = NamesGenerator.createResultDataType(selectOne.result());
        } catch (MirroredTypeException ex) {
            // See: http://blog.retep.org/2009/02/13/getting-class-values-from-annotations-in-an-annotationprocessor/
            resultDataType = NamesGenerator.createDataTypeFor(ex.getTypeMirror());
        }
        if (resultDataType == null) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Unable to find the result element", element);
            return;
        }
        EntityInfo entityInfo = generationInfo.getEntityByName(resultDataType);

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

        if (!relatedDataType.isVoid()) {
            EntityInfo relatedEntityInfo = generationInfo.getEntityByName(relatedDataType);
            if (relatedEntityInfo == null) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Unable to find the related entity", element);
                return;
            }
            if (entityInfo == null) {
                entityInfo = relatedEntityInfo;
            } else {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Related is not allowed when the operation result is an entity", element);
            }
        }

        OperationInfo operationInfo = new OperationInfo(element, executorModuleInfo.getOperationPackage());
        operationInfo.setReturnDataType(resultDataType);
        operationInfo.setOperationKind(OperationKind.SELECT_ONE);
        operationInfo.setLimitToOneResult(selectOne.limit());
        operationInfo.setEntity(entityInfo);

        DataTypeInfo operationInterface = DataTypeInfo.OPERATION_DATA_TYPE.of(resultDataType);
        operationInfo.addImplement(operationInterface);

        loadShared(re, element, executorModuleInfo, operationInfo);
        generationInfo.addOperation(operationInfo, executorModuleInfo);
    }

    public void processSelectMany(RoundEnvironment re, TypeElement element, ExecutorModuleInfo executorModuleInfo, SelectMany selectMany) {
        GenerationInfo generationInfo = getGenerationInfo();
        DataTypeInfo resultDataType;
        try {
            resultDataType = NamesGenerator.createResultDataType(selectMany.result());
        } catch (MirroredTypeException ex) {
            // See: http://blog.retep.org/2009/02/13/getting-class-values-from-annotations-in-an-annotationprocessor/
            resultDataType = NamesGenerator.createDataTypeFor(ex.getTypeMirror());
        }
        if (resultDataType == null) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Unable to find the result element", element);
            return;
        }
        EntityInfo entityInfo = generationInfo.getEntityByName(resultDataType);

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

        if (!relatedDataType.isVoid()) {
            EntityInfo relatedEntityInfo = generationInfo.getEntityByName(relatedDataType);
            if (relatedEntityInfo == null) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Unable to find the related entity", element);
                return;
            }
            if (entityInfo == null) {
                entityInfo = relatedEntityInfo;
            } else {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Related is not allowed when the operation result is an entity", element);
            }
            
        }

        DataTypeInfo listResultDataType = DataTypeInfo.LIST_DATA_TYPE.of(resultDataType);
        OperationInfo operationInfo = new OperationInfo(element, executorModuleInfo.getOperationPackage());
        operationInfo.setReturnDataType(listResultDataType);
        operationInfo.setOneItemReturnDataType(resultDataType);
        operationInfo.setOperationKind(OperationKind.SELECT_MANY);
        operationInfo.setDistinct(selectMany.distinct());
        operationInfo.setEntity(entityInfo);

        DataTypeInfo operationInterface = DataTypeInfo.OPERATION_DATA_TYPE.of(listResultDataType);
        operationInfo.addImplement(operationInterface);

        loadShared(re, element, executorModuleInfo, operationInfo);
        generationInfo.addOperation(operationInfo, executorModuleInfo);
    }

    public void processOperation(RoundEnvironment re, TypeElement element, ExecutorModuleInfo executorModuleInfo, Operation operation) {
        GenerationInfo generationInfo = getGenerationInfo();
        DataTypeInfo resultDataType;
        try {
            resultDataType = NamesGenerator.createResultDataType(operation.result());
        } catch (MirroredTypeException ex) {
            // See: http://blog.retep.org/2009/02/13/getting-class-values-from-annotations-in-an-annotationprocessor/
            resultDataType = NamesGenerator.createDataTypeFor(ex.getTypeMirror());
        }
        if (resultDataType == null) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Unable to find the result element", element);
            return;
        }
        EntityInfo entityInfo = generationInfo.getEntityByName(resultDataType);

        OperationInfo operationInfo = new OperationInfo(element, executorModuleInfo.getOperationPackage());
        operationInfo.setReturnDataType(resultDataType);
        operationInfo.setOperationKind(OperationKind.CUSTOM);
        operationInfo.setEntity(entityInfo);

        DataTypeInfo operationInterface = DataTypeInfo.OPERATION_DATA_TYPE.of(resultDataType);
        operationInfo.addImplement(operationInterface);
        operationInfo.setManually(true);

        loadShared(re, element, executorModuleInfo, operationInfo);
        generationInfo.addOperation(operationInfo, executorModuleInfo);
    }

    public void processInsert(RoundEnvironment re, TypeElement element, ExecutorModuleInfo executorModuleInfo, Insert operation) {
        GenerationInfo generationInfo = getGenerationInfo();
        EntityInfo entity;
        DataTypeInfo entityDataType;
        DataTypeInfo resultDataType;
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

        entity = generationInfo.getEntityByName(entityDataType);
        if (entity == null) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Unable to find the related entity", element);
            return;
        }

        if (operation.returnLastInsertedId()) {
            operationKind = OperationKind.CUSTOM_INSERT_WITH_ID;
            FieldInfo id = entity.getCombined().getFirstIdField();
            if (id == null) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Unable to find the related entity id field", element);
                return;
            } else if (entity.getCombined().hasMultiplesIds()) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "An entity must define only one id for use in an insert operation that return the last inserted id", element);
                return;
            }
            resultDataType = id.getDataType().ensureBoxed();
        } else {
            resultDataType = DataTypeInfo.AFFECTED_ROW_COUNT_DATA_TYPE;
            operationKind = OperationKind.CUSTOM_INSERT;
        }

        OperationInfo operationInfo = new OperationInfo(element, executorModuleInfo.getOperationPackage());
        operationInfo.setEntity(entity);
        operationInfo.setReturnDataType(resultDataType);
        operationInfo.setOperationKind(operationKind);

        DataTypeInfo operationInterface = DataTypeInfo.OPERATION_DATA_TYPE.of(resultDataType);
        operationInfo.addImplement(operationInterface);

        loadShared(re, element, executorModuleInfo, operationInfo);

        generationInfo.addOperation(operationInfo, executorModuleInfo);
    }

    public void processUpdate(RoundEnvironment re, TypeElement element, ExecutorModuleInfo executorModuleInfo, Update operation) {
        GenerationInfo generationInfo = getGenerationInfo();
        DataTypeInfo resultDataType = DataTypeInfo.AFFECTED_ROW_COUNT_DATA_TYPE;
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

        entity = generationInfo.getEntityByName(entityDataType);
        if (entity == null) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Unable to find the related entity", element);
            return;
        }

        OperationInfo operationInfo = new OperationInfo(element, executorModuleInfo.getOperationPackage());
        operationInfo.setEntity(entity);
        operationInfo.setReturnDataType(resultDataType);
        operationInfo.setOperationKind(OperationKind.CUSTOM_UPDATE);

        DataTypeInfo operationInterface = DataTypeInfo.OPERATION_DATA_TYPE.of(resultDataType);
        operationInfo.addImplement(operationInterface);

        loadShared(re, element, executorModuleInfo, operationInfo);
        generationInfo.addOperation(operationInfo, executorModuleInfo);
    }

    public void processDelete(RoundEnvironment re, TypeElement element, ExecutorModuleInfo executorModuleInfo, Delete operation) {
        GenerationInfo generationInfo = getGenerationInfo();
        DataTypeInfo resultDataType = DataTypeInfo.AFFECTED_ROW_COUNT_DATA_TYPE;
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

        entity = generationInfo.getEntityByName(entityDataType);
        if (entity == null) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Unable to find the related entity", element);
            return;
        }

        OperationInfo operationInfo = new OperationInfo(element, executorModuleInfo.getOperationPackage());
        operationInfo.setEntity(entity);
        operationInfo.setReturnDataType(resultDataType);
        operationInfo.setOperationKind(OperationKind.CUSTOM_DELETE);

        DataTypeInfo operationInterface = DataTypeInfo.OPERATION_DATA_TYPE.of(resultDataType);
        operationInfo.addImplement(operationInterface);

        loadShared(re, element, executorModuleInfo, operationInfo);
        generationInfo.addOperation(operationInfo, executorModuleInfo);
    }

    public void loadShared(RoundEnvironment re, TypeElement element, ExecutorModuleInfo executorModuleInfo, OperationInfo operationInfo) {
        boolean generateNotNullValidation = getGenerationInfo().isGenerateNotNullValidationForMandatoryFields();
        EntityInfo entityInfo = operationInfo.getEntity();
        for (Element enclosedElement : element.getEnclosedElements()) {
            if (enclosedElement.getKind() == ElementKind.FIELD) {
                VariableElement ve = (VariableElement) enclosedElement;

                FieldInfo fi = new FieldInfo(ve, generateNotNullValidation, processingEnv);
                if (entityInfo != null) {
                    FieldInfo fieldInEntity = entityInfo.getFieldByName(fi.getName());
                    fi.setRelated(fieldInEntity);
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
        FieldInfo idInfo = combinedEntity.getFirstIdField();
        if (idInfo == null) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "An entity must define an id for use in an executor group", element);
            return;
        } else if (combinedEntity.hasMultiplesIds()) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "An entity must define only one id for use in an executor group", element);
            return;
        } else {
            idInfo = combinedEntity.getFirstIdField();
            if (idInfo == null) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Unable to find the id field element", element);
                return;
            }
        }
        DataTypeInfo realIdDataType = idInfo.getDataType();
        
        idInfo = new FieldInfo("id", idInfo);
        idInfo.setMarkAsOvwrride(true);
        idInfo.setOptional(false);
        idInfo.setIdentifier(true);
        
        DataTypeInfo idDataType = idInfo.getDataType().ensureBoxed();
        idInfo.setDataType(idDataType);

        DataTypeInfo entityDataType = entityInfo.getDataType();
        FieldInfo valueInfo = new FieldInfo("value", entityDataType);
        valueInfo.setMarkAsOvwrride(true);
        valueInfo.setOptional(false);
        valueInfo.setIdentifier(false);
        DataTypeInfo affectedRowCountDataType = DataTypeInfo.AFFECTED_ROW_COUNT_DATA_TYPE;

        executorModuleInfo.addEntity(entityInfo);

        boolean generateDefaultEntityOperations = generationInfo.isGenerateDefaultEntityOperations();
        boolean defaultGenerateDeleteByIdOperation = generateDefaultEntityOperations;
        boolean defaultGenerateInsertOperation = generateDefaultEntityOperations;
        boolean defaultGenerateJustInsertOperation = generateDefaultEntityOperations && generationInfo.isGenerateJustOperationsEnabled();
        boolean defaultGenerateSaveOperation = generateDefaultEntityOperations && generationInfo.isGenerateSaveOperationsEnabled();
        boolean defaultGenerateJustSaveOperation = generateDefaultEntityOperations && generationInfo.isGenerateJustOperationsEnabled() && generationInfo.isGenerateSaveOperationsEnabled();
        boolean defaultGenerateSelectByIdOperation = generateDefaultEntityOperations;
        boolean defaultGenerateUpdateOperation = generateDefaultEntityOperations;
        boolean defaultGenerateMergeOperation = generateDefaultEntityOperations && generationInfo.isGenerateMergeOperationsEnabled();

        boolean generateDeleteByIdOperation;
        boolean generateInsertOperation;
        boolean generateJustInsertOperation;
        boolean generateSaveOperation;
        boolean generateJustSaveOperation;
        boolean generateSelectByIdOperation;
        boolean generateUpdateOperation;
        boolean generateMergeOperation;

        Entity entityAnnotation = entityInfo.getAnnotation(Entity.class);
        if (entityAnnotation == null) {
            generateDeleteByIdOperation = defaultGenerateDeleteByIdOperation;
            generateInsertOperation = defaultGenerateInsertOperation;
            generateJustInsertOperation = defaultGenerateJustInsertOperation;
            generateSaveOperation = defaultGenerateSaveOperation;
            generateJustSaveOperation = defaultGenerateJustSaveOperation;
            generateSelectByIdOperation = defaultGenerateSelectByIdOperation;
            generateUpdateOperation = defaultGenerateUpdateOperation;
            generateMergeOperation = defaultGenerateMergeOperation;
        } else {
            generateDeleteByIdOperation = entityAnnotation.generateDeleteByIdOperation().solve(defaultGenerateDeleteByIdOperation);
            generateInsertOperation = entityAnnotation.generateInsertOperation().solve(defaultGenerateInsertOperation);
            generateJustInsertOperation = entityAnnotation.generateJustInsertOperation().solve(defaultGenerateJustInsertOperation);
            generateSaveOperation = entityAnnotation.generateSaveOperation().solve(defaultGenerateSaveOperation);
            generateJustSaveOperation = entityAnnotation.generateJustSaveOperation().solve(defaultGenerateJustSaveOperation);
            generateSelectByIdOperation = entityAnnotation.generateSelectByIdOperation().solve(defaultGenerateSelectByIdOperation);
            generateUpdateOperation = entityAnnotation.generateUpdateOperation().solve(defaultGenerateUpdateOperation);
            generateMergeOperation = entityAnnotation.generateMergeOperation().solve(defaultGenerateMergeOperation);
        }

        // Todo: remove this limitation
        generateInsertOperation = generateInsertOperation || generateSaveOperation || generateJustSaveOperation;
        generateUpdateOperation = generateUpdateOperation || generateSaveOperation || generateJustSaveOperation;

        /* ****************************************************************************************
         * *** Delete By Id operation
         */
        if (generateDeleteByIdOperation) {
            DataTypeInfo deleteOperationName = new DataTypeInfo(executorModuleInfo.getOperationPackage(),
                    "Delete" + entityDataType.getSimpleNameWithoutGenerics()+ "ById");
            if (executorModuleInfo.getOperationByName(deleteOperationName) == null) {
                OperationInfo deleteOperationInfo = new OperationInfo(deleteOperationName);
                deleteOperationInfo.setReturnDataType(affectedRowCountDataType);
                deleteOperationInfo.setOperationKind(OperationKind.DELETE_BY_ID);

                DataTypeInfo deleteOperationInterface = DataTypeInfo.DELETE_BY_ID_OPERATION_DATA_TYPE.of(idDataType, affectedRowCountDataType);
                deleteOperationInterface.getImports().addAll(idDataType.getImports());
                deleteOperationInterface.getImports().addAll(affectedRowCountDataType.getImports());
                deleteOperationInfo.addImplement(deleteOperationInterface);

                deleteOperationInfo.addField(idInfo);
                deleteOperationInfo.setEntity(entityInfo);
                deleteOperationInfo.setManually(entityInfo.getCombined().isManually());
                generationInfo.addOperation(deleteOperationInfo, executorModuleInfo);
            }
        }

        /* ****************************************************************************************
         * *** Insert operation
         */
        if (generateInsertOperation) {
            DataTypeInfo insertOperationName = new DataTypeInfo(executorModuleInfo.getOperationPackage(),
                    "Insert" + entityDataType.getSimpleNameWithoutGenerics());
            if (executorModuleInfo.getOperationByName(insertOperationName) == null) {
                OperationInfo insertOperationInfo = new OperationInfo(insertOperationName);
                insertOperationInfo.setReturnDataType(idDataType);
                insertOperationInfo.setOperationKind(OperationKind.INSERT);

                DataTypeInfo insertOperationInterface = DataTypeInfo.INSERT_VALUE_OPERATION_DATA_TYPE.of(entityDataType, idDataType);
                insertOperationInterface.getImports().addAll(entityDataType.getImports());
                insertOperationInterface.getImports().addAll(idDataType.getImports());
                insertOperationInfo.addImplement(insertOperationInterface);

                insertOperationInfo.addField(valueInfo);
                insertOperationInfo.setEntity(entityInfo);
                insertOperationInfo.setManually(entityInfo.getCombined().isManually());
                generationInfo.addOperation(insertOperationInfo, executorModuleInfo);
            }
        }

        /* ****************************************************************************************
         * *** Just Insert operation
         */
        if (generateJustInsertOperation) {
            DataTypeInfo justInsertOperationName = new DataTypeInfo(executorModuleInfo.getOperationPackage(),
                    "JustInsert" + entityDataType.getSimpleNameWithoutGenerics());
            if (executorModuleInfo.getOperationByName(justInsertOperationName) == null) {
                OperationInfo justInsertOperationInfo = new OperationInfo(justInsertOperationName);
                justInsertOperationInfo.setReturnDataType(affectedRowCountDataType);
                justInsertOperationInfo.setOperationKind(OperationKind.JUST_INSERT);

                DataTypeInfo justInsertOperationInterface = DataTypeInfo.JUST_INSERT_VALUE_OPERATION_DATA_TYPE.of(entityDataType, affectedRowCountDataType);
                justInsertOperationInterface.getImports().addAll(entityDataType.getImports());
                justInsertOperationInterface.getImports().addAll(affectedRowCountDataType.getImports());
                justInsertOperationInfo.addImplement(justInsertOperationInterface);

                justInsertOperationInfo.addField(valueInfo);
                justInsertOperationInfo.setEntity(entityInfo);
                justInsertOperationInfo.setManually(entityInfo.getCombined().isManually());
                generationInfo.addOperation(justInsertOperationInfo, executorModuleInfo);
            }
        }

        /* ****************************************************************************************
         * *** Save operation
         */
        
        boolean showSaveError = false;
        if (generateSaveOperation || generateJustSaveOperation) {
            if (realIdDataType.isPrimitive()) {
                showSaveError = true;
            }   
        }
        
        if (generateSaveOperation) {
            DataTypeInfo saveOperationName = new DataTypeInfo(executorModuleInfo.getOperationPackage(),
                    "Save" + entityDataType.getSimpleNameWithoutGenerics());
            if (executorModuleInfo.getOperationByName(saveOperationName) == null) {
                if (showSaveError) {
                    processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "For generate the save entity operation the id field must allow null values, but a primitive data types do not allow it; you must use " + realIdDataType.ensureBoxed().getSimpleName() + " instead of " + realIdDataType.getSimpleName(), element);
                    showSaveError = false;
                }
                OperationInfo saveOperationInfo = new OperationInfo(saveOperationName);
                saveOperationInfo.setReturnDataType(idDataType);
                saveOperationInfo.setOperationKind(OperationKind.SAVE);

                DataTypeInfo saveOperationInterface = DataTypeInfo.SAVE_VALUE_OPERATION_DATA_TYPE.of(entityDataType, idDataType);
                saveOperationInterface.getImports().addAll(entityDataType.getImports());
                saveOperationInterface.getImports().addAll(idDataType.getImports());
                saveOperationInfo.addImplement(saveOperationInterface);

                saveOperationInfo.addField(valueInfo);
                saveOperationInfo.setEntity(entityInfo);
                saveOperationInfo.setManually(entityInfo.getCombined().isManually());
                generationInfo.addOperation(saveOperationInfo, executorModuleInfo);
            }
        }

        /* ****************************************************************************************
         * *** Just Save operation
         */
        if (generateJustSaveOperation) {
            DataTypeInfo justSaveOperationName = new DataTypeInfo(executorModuleInfo.getOperationPackage(),
                    "JustSave" + entityDataType.getSimpleNameWithoutGenerics());
            if (executorModuleInfo.getOperationByName(justSaveOperationName) == null) {
                if (showSaveError) {
                    processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "For generate the save entity operation the id field must allow null values, but a primitive data types do not allow it; you must use " + realIdDataType.ensureBoxed().getSimpleName() + " instead of " + realIdDataType.getSimpleName(), element);
                }
                OperationInfo justSaveOperationInfo = new OperationInfo(justSaveOperationName);
                justSaveOperationInfo.setReturnDataType(affectedRowCountDataType);
                justSaveOperationInfo.setOperationKind(OperationKind.JUST_SAVE);

                DataTypeInfo justSaveOperationInterface = DataTypeInfo.JUST_SAVE_VALUE_OPERATION_DATA_TYPE.of(entityDataType, affectedRowCountDataType);
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
        if (generateSelectByIdOperation) {
            DataTypeInfo selectOperationName = new DataTypeInfo(executorModuleInfo.getOperationPackage(),
                    "Select" + entityDataType.getSimpleNameWithoutGenerics()+ "ById");
            if (executorModuleInfo.getOperationByName(selectOperationName) == null) {
                OperationInfo selectOperationInfo = new OperationInfo(selectOperationName);
                selectOperationInfo.setReturnDataType(entityDataType);
                selectOperationInfo.setOperationKind(OperationKind.SELECT_BY_ID);

                DataTypeInfo selectOperationInterface = DataTypeInfo.SELECT_BY_ID_OPERATION_DATA_TYPE.of(idDataType, entityDataType);
                selectOperationInterface.getImports().addAll(entityDataType.getImports());
                selectOperationInfo.addImplement(selectOperationInterface);

                selectOperationInfo.addField(idInfo);
                selectOperationInfo.setEntity(entityInfo);
                selectOperationInfo.setManually(entityInfo.getCombined().isManually());
                generationInfo.addOperation(selectOperationInfo, executorModuleInfo);
            }
        }

        /* ****************************************************************************************
         * *** Update operation
         */
        if (generateUpdateOperation) {
            DataTypeInfo updateOperationName = new DataTypeInfo(executorModuleInfo.getOperationPackage(),
                    "Update" + entityDataType.getSimpleNameWithoutGenerics());
            if (executorModuleInfo.getOperationByName(updateOperationName) == null) {
                OperationInfo updateOperationInfo = new OperationInfo(updateOperationName);
                updateOperationInfo.setReturnDataType(affectedRowCountDataType);
                updateOperationInfo.setOperationKind(OperationKind.UPDATE);

                DataTypeInfo updateOperationInterface = DataTypeInfo.UPDATE_VALUE_OPERATION_DATA_TYPE.of(entityDataType, affectedRowCountDataType);
                updateOperationInterface.getImports().addAll(entityDataType.getImports());
                updateOperationInterface.getImports().addAll(affectedRowCountDataType.getImports());
                updateOperationInfo.addImplement(updateOperationInterface);

                updateOperationInfo.addField(valueInfo);
                updateOperationInfo.setEntity(entityInfo);
                updateOperationInfo.setManually(entityInfo.getCombined().isManually());
                generationInfo.addOperation(updateOperationInfo, executorModuleInfo);
            }
        }

        /* ****************************************************************************************
         * *** Merge operation
         */
        if (generateMergeOperation) {
            DataTypeInfo mergeOperationName = new DataTypeInfo(executorModuleInfo.getOperationPackage(),
                    "Merge" + entityDataType.getSimpleNameWithoutGenerics());
            if (executorModuleInfo.getOperationByName(mergeOperationName) == null) {
                OperationInfo mergeOperationInfo = new OperationInfo(mergeOperationName);
                mergeOperationInfo.setReturnDataType(affectedRowCountDataType);
                mergeOperationInfo.setOperationKind(OperationKind.MERGE);

                DataTypeInfo mergeOperationInterface = DataTypeInfo.MERGE_VALUE_OPERATION_DATA_TYPE.of(entityDataType, affectedRowCountDataType);
                mergeOperationInterface.getImports().addAll(entityDataType.getImports());
                mergeOperationInterface.getImports().addAll(affectedRowCountDataType.getImports());
                mergeOperationInfo.addImplement(mergeOperationInterface);

                mergeOperationInfo.addField(valueInfo);
                mergeOperationInfo.setEntity(entityInfo);
                mergeOperationInfo.setManually(entityInfo.getCombined().isManually());
                generationInfo.addOperation(mergeOperationInfo, executorModuleInfo);
            }
        }
    }
    
    private boolean hasMembers(TypeElement element) {
        for (Element enclosedElement : element.getEnclosedElements()) {
            if (enclosedElement.getKind() == ElementKind.FIELD) {
                return true;
            }
        }
        return false;
    }

    public void processInsertEntity(RoundEnvironment re, TypeElement element, ExecutorModuleInfo executorModuleInfo, InsertEntity operation) {
        GenerationInfo generationInfo = getGenerationInfo();
        DataTypeInfo entityDataType;

        try {
            entityDataType = NamesGenerator.createResultDataType(operation.value());
        } catch (MirroredTypeException ex) {
            // See: http://blog.retep.org/2009/02/13/getting-class-values-from-annotations-in-an-annotationprocessor/
            entityDataType = NamesGenerator.createDataTypeFor(ex.getTypeMirror());
        }
        if (entityDataType == null) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Unable to find the related element", element);
            return;
        }

        EntityInfo entity = generationInfo.getEntityByName(entityDataType);
        if (entity == null) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Unable to find the related entity", element);
            return;
        }
        
        FieldInfo valueInfo = new FieldInfo("value", entityDataType);
        valueInfo.setMarkAsOvwrride(true);
        valueInfo.setOptional(false);
        valueInfo.setIdentifier(false);

        OperationInfo operationInfo = new OperationInfo(element, executorModuleInfo.getOperationPackage());
        operationInfo.setEntity(entity);
        operationInfo.addField(valueInfo);
        
        if (operation.justInsert()) {
            operationInfo.setReturnDataType(DataTypeInfo.AFFECTED_ROW_COUNT_DATA_TYPE);
            operationInfo.setOperationKind(OperationKind.JUST_INSERT);

            DataTypeInfo justInsertOperationInterface = DataTypeInfo.JUST_INSERT_VALUE_OPERATION_DATA_TYPE.of(entityDataType, DataTypeInfo.AFFECTED_ROW_COUNT_DATA_TYPE);
            operationInfo.addImplement(justInsertOperationInterface);
        } else {
            FieldInfo id = entity.getCombined().getFirstIdField();
            if (id == null) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Unable to find the related entity id field", element);
                return;
            } else if (entity.getCombined().hasMultiplesIds()) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "An entity must define only one id for use in an insert entity operation that return the last inserted id", element);
                return;
            }
            DataTypeInfo idDataType = id.getDataType().ensureBoxed();
            operationInfo.setReturnDataType(idDataType);
            operationInfo.setOperationKind(OperationKind.INSERT);

            DataTypeInfo insertOperationInterface = DataTypeInfo.INSERT_VALUE_OPERATION_DATA_TYPE.of(entityDataType, idDataType);
            operationInfo.addImplement(insertOperationInterface);
        }
        
        if (hasMembers(element)) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Insert entity operations do not allow define members", element);
        }
        generationInfo.addOperation(operationInfo, executorModuleInfo);
    }

    public void processUpdateEntity(RoundEnvironment re, TypeElement element, ExecutorModuleInfo executorModuleInfo, UpdateEntity operation) {
        GenerationInfo generationInfo = getGenerationInfo();
        DataTypeInfo entityDataType;

        try {
            entityDataType = NamesGenerator.createResultDataType(operation.value());
        } catch (MirroredTypeException ex) {
            // See: http://blog.retep.org/2009/02/13/getting-class-values-from-annotations-in-an-annotationprocessor/
            entityDataType = NamesGenerator.createDataTypeFor(ex.getTypeMirror());
        }
        if (entityDataType == null) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Unable to find the related element", element);
            return;
        }

        EntityInfo entity = generationInfo.getEntityByName(entityDataType);
        if (entity == null) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Unable to find the related entity", element);
            return;
        }
        
        FieldInfo id = entity.getCombined().getFirstIdField();
        if (id == null) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Unable to find the related entity id field", element);
            return;
        } else if (entity.getCombined().hasMultiplesIds()) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "An entity must define only one id for use in an update entity operation", element);
            return;
        }
        
        FieldInfo valueInfo = new FieldInfo("value", entityDataType);
        valueInfo.setMarkAsOvwrride(true);
        valueInfo.setOptional(false);
        valueInfo.setIdentifier(false);

        OperationInfo operationInfo = new OperationInfo(element, executorModuleInfo.getOperationPackage());
        operationInfo.setReturnDataType(DataTypeInfo.AFFECTED_ROW_COUNT_DATA_TYPE);
        operationInfo.setOperationKind(OperationKind.UPDATE);

        DataTypeInfo updateOperationInterface = DataTypeInfo.UPDATE_VALUE_OPERATION_DATA_TYPE.of(entityDataType, DataTypeInfo.AFFECTED_ROW_COUNT_DATA_TYPE);
        operationInfo.addImplement(updateOperationInterface);

        operationInfo.addField(valueInfo);
        operationInfo.setEntity(entity);
        
        if (hasMembers(element)) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Update entity operations do not allow define members", element);
        }
        generationInfo.addOperation(operationInfo, executorModuleInfo);
    }

    public void processDeleteEntityById(RoundEnvironment re, TypeElement element, ExecutorModuleInfo executorModuleInfo, DeleteEntityById operation) {
        GenerationInfo generationInfo = getGenerationInfo();
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

        EntityInfo entity = generationInfo.getEntityByName(entityDataType);
        if (entity == null) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Unable to find the related entity", element);
            return;
        }
        
        FieldInfo valueInfo = new FieldInfo("value", entityDataType);
        valueInfo.setMarkAsOvwrride(true);
        valueInfo.setOptional(false);
        valueInfo.setIdentifier(false);

        OperationInfo operationInfo = new OperationInfo(element, executorModuleInfo.getOperationPackage());
        FieldInfo id = entity.getCombined().getFirstIdField();
        if (id == null) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Unable to find the related entity id field", element);
            return;
        } else if (entity.getCombined().hasMultiplesIds()) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "An entity must define only one id for use in an delete entity by id operation", element);
            return;
        }
        id = new FieldInfo("id", id);
        id.setMarkAsOvwrride(true);
        id.setOptional(false);
        id.setIdentifier(true);

        DataTypeInfo idDataType = id.getDataType().ensureBoxed();
        id.setDataType(idDataType);

        operationInfo.setReturnDataType(DataTypeInfo.AFFECTED_ROW_COUNT_DATA_TYPE);
        operationInfo.setOperationKind(OperationKind.DELETE_BY_ID);

        DataTypeInfo deleteOperationInterface = DataTypeInfo.DELETE_BY_ID_OPERATION_DATA_TYPE.of(idDataType, DataTypeInfo.AFFECTED_ROW_COUNT_DATA_TYPE);
        operationInfo.addImplement(deleteOperationInterface);

        operationInfo.addField(id);
        operationInfo.setEntity(entity);
        
        if (hasMembers(element)) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Delete entity by id operations do not allow define members", element);
        }
        generationInfo.addOperation(operationInfo, executorModuleInfo);
    }

    public void processSelectEntityById(RoundEnvironment re, TypeElement element, ExecutorModuleInfo executorModuleInfo, SelectEntityById operation) {
        GenerationInfo generationInfo = getGenerationInfo();
        DataTypeInfo entityDataType;

        try {
            entityDataType = NamesGenerator.createResultDataType(operation.result());
        } catch (MirroredTypeException ex) {
            // See: http://blog.retep.org/2009/02/13/getting-class-values-from-annotations-in-an-annotationprocessor/
            entityDataType = NamesGenerator.createDataTypeFor(ex.getTypeMirror());
        }
        if (entityDataType == null) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Unable to find the related element", element);
            return;
        }

        EntityInfo entity = generationInfo.getEntityByName(entityDataType);
        if (entity == null) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Unable to find the related entity", element);
            return;
        }
        
        FieldInfo valueInfo = new FieldInfo("value", entityDataType);
        valueInfo.setMarkAsOvwrride(true);
        valueInfo.setOptional(false);
        valueInfo.setIdentifier(false);

        OperationInfo operationInfo = new OperationInfo(element, executorModuleInfo.getOperationPackage());
        FieldInfo id = entity.getCombined().getFirstIdField();
        if (id == null) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Unable to find the related entity id field", element);
            return;
        } else if (entity.getCombined().hasMultiplesIds()) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "An entity must define only one id for use in an select entity by id operation", element);
            return;
        } 
        id = new FieldInfo("id", id);
        id.setMarkAsOvwrride(true);
        id.setOptional(false);
        id.setIdentifier(true);

        DataTypeInfo idDataType = id.getDataType().ensureBoxed();
        id.setDataType(idDataType);

        operationInfo.setReturnDataType(entityDataType);
        operationInfo.setOperationKind(OperationKind.SELECT_BY_ID);

        DataTypeInfo selectOperationInterface = DataTypeInfo.SELECT_BY_ID_OPERATION_DATA_TYPE.of(idDataType, entityDataType);
        operationInfo.addImplement(selectOperationInterface);

        operationInfo.addField(id);
        operationInfo.setEntity(entity);
        
        if (hasMembers(element)) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Select entity by id operations do not allow define members", element);
        }
        generationInfo.addOperation(operationInfo, executorModuleInfo);
    }

    public void processSaveEntity(RoundEnvironment re, TypeElement element, ExecutorModuleInfo executorModuleInfo, SaveEntity operation) {
        GenerationInfo generationInfo = getGenerationInfo();
        DataTypeInfo entityDataType;

        try {
            entityDataType = NamesGenerator.createResultDataType(operation.value());
        } catch (MirroredTypeException ex) {
            // See: http://blog.retep.org/2009/02/13/getting-class-values-from-annotations-in-an-annotationprocessor/
            entityDataType = NamesGenerator.createDataTypeFor(ex.getTypeMirror());
        }
        if (entityDataType == null) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Unable to find the related element", element);
            return;
        }

        EntityInfo entity = generationInfo.getEntityByName(entityDataType);
        if (entity == null) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Unable to find the related entity", element);
            return;
        }
        
        FieldInfo id = entity.getCombined().getFirstIdField();
        if (id == null) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Unable to find the related entity id field", element);
            return;
        } else if (entity.getCombined().hasMultiplesIds()) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "An entity must define only one id for use in an save entity operation", element);
            return;
        }
        DataTypeInfo idDataType = id.getDataType();
        if (idDataType.isPrimitive()) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "For use a save entity operation the id field must allow null values, but a primitive data types do not allow it; you must use " + idDataType.ensureBoxed().getSimpleName() + " instead of " + idDataType.getSimpleName(), element);
        }
        
        FieldInfo valueInfo = new FieldInfo("value", entityDataType);
        valueInfo.setMarkAsOvwrride(true);
        valueInfo.setOptional(false);
        valueInfo.setIdentifier(false);

        OperationInfo operationInfo = new OperationInfo(element, executorModuleInfo.getOperationPackage());
        operationInfo.setEntity(entity);
        operationInfo.addField(valueInfo);
        
        if (operation.justSave()) {
            operationInfo.setReturnDataType(DataTypeInfo.AFFECTED_ROW_COUNT_DATA_TYPE);
            operationInfo.setOperationKind(OperationKind.JUST_SAVE);

            DataTypeInfo justSaveOperationInterface = DataTypeInfo.JUST_SAVE_VALUE_OPERATION_DATA_TYPE.of(entityDataType, DataTypeInfo.AFFECTED_ROW_COUNT_DATA_TYPE);
            operationInfo.addImplement(justSaveOperationInterface);
        } else {
            idDataType = idDataType.ensureBoxed();
            operationInfo.setReturnDataType(idDataType);
            operationInfo.setOperationKind(OperationKind.SAVE);

            DataTypeInfo saveOperationInterface = DataTypeInfo.SAVE_VALUE_OPERATION_DATA_TYPE.of(entityDataType, idDataType);
            operationInfo.addImplement(saveOperationInterface);
        }
        
        if (hasMembers(element)) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Save entity operations do not allow define members", element);
        }
        generationInfo.addOperation(operationInfo, executorModuleInfo);
    }

    public void processMergeEntity(RoundEnvironment re, TypeElement element, ExecutorModuleInfo executorModuleInfo, MergeEntity operation) {
        GenerationInfo generationInfo = getGenerationInfo();
        DataTypeInfo entityDataType;

        try {
            entityDataType = NamesGenerator.createResultDataType(operation.value());
        } catch (MirroredTypeException ex) {
            // See: http://blog.retep.org/2009/02/13/getting-class-values-from-annotations-in-an-annotationprocessor/
            entityDataType = NamesGenerator.createDataTypeFor(ex.getTypeMirror());
        }
        if (entityDataType == null) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Unable to find the related element", element);
            return;
        }

        EntityInfo entity = generationInfo.getEntityByName(entityDataType);
        if (entity == null) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Unable to find the related entity", element);
            return;
        }
        
        FieldInfo id = entity.getCombined().getFirstIdField();
        if (id == null) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Unable to find the related entity id field", element);
            return;
        } else if (entity.getCombined().hasMultiplesIds()) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "An entity must define only one id for use in an merge entity operation", element);
            return;
        }
        
        FieldInfo valueInfo = new FieldInfo("value", entityDataType);
        valueInfo.setMarkAsOvwrride(true);
        valueInfo.setOptional(false);
        valueInfo.setIdentifier(false);

        OperationInfo operationInfo = new OperationInfo(element, executorModuleInfo.getOperationPackage());
        operationInfo.setReturnDataType(DataTypeInfo.AFFECTED_ROW_COUNT_DATA_TYPE);
        operationInfo.setOperationKind(OperationKind.MERGE);

        DataTypeInfo mergeOperationInterface = DataTypeInfo.MERGE_VALUE_OPERATION_DATA_TYPE.of(entityDataType, DataTypeInfo.AFFECTED_ROW_COUNT_DATA_TYPE);
        operationInfo.addImplement(mergeOperationInterface);

        operationInfo.addField(valueInfo);
        operationInfo.setEntity(entity);
        
        if (hasMembers(element)) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Merge entity operations do not allow define members", element);
        }
        generationInfo.addOperation(operationInfo, executorModuleInfo);
    }

    public void generateOperations(RoundEnvironment re, ExecutorModuleInfo executorModuleInfo) {
        GenerationInfo generationInfo = getGenerationInfo();

        boolean defaultGenerateModuleAbstractExecutorsEnabled = generationInfo.isGenerateModuleAbstractExecutorsEnabled();
        boolean defaultGenerateModuleChainedExecutorsEnabled = generationInfo.isGenerateModuleChainedExecutorsEnabled();
        boolean defaultGenerateModuleChainedGroupingExecutorsEnabled = generationInfo.isGenerateModuleChainedGroupingExecutorsEnabled();

        boolean generateModuleAbstractExecutorsEnabled;
        boolean generateModuleChainedExecutorsEnabled;
        boolean generateModuleChainedGroupingExecutorsEnabled;

        OperationModule operationModuleAnnotation = executorModuleInfo.getAnnotation(OperationModule.class);
        if (operationModuleAnnotation == null) {
            generateModuleAbstractExecutorsEnabled = defaultGenerateModuleAbstractExecutorsEnabled;
            generateModuleChainedExecutorsEnabled = defaultGenerateModuleChainedExecutorsEnabled;
            generateModuleChainedGroupingExecutorsEnabled = defaultGenerateModuleChainedGroupingExecutorsEnabled;
        } else {
            generateModuleAbstractExecutorsEnabled = operationModuleAnnotation.generateAbstractExecutor().solve(defaultGenerateModuleAbstractExecutorsEnabled);
            generateModuleChainedExecutorsEnabled = operationModuleAnnotation.generateChainedExecutor().solve(defaultGenerateModuleChainedExecutorsEnabled);
            generateModuleChainedGroupingExecutorsEnabled = operationModuleAnnotation.generateChainedGroupingExecutor().solve(defaultGenerateModuleChainedGroupingExecutorsEnabled);
        }
        String packageName = executorModuleInfo.getOperationPackage();

        processClassTemplate(new ExecutorTemplate(executorModuleInfo, packageName), executorModuleInfo.getElement());

        for (OperationInfo operation : executorModuleInfo.getOperations()) {
            processClassTemplate(new OperationTemplate(operation, packageName, executorModuleInfo.getExecutorInterfaceName()), operation.getElement());
        }

        if (generateModuleChainedExecutorsEnabled) {
            processClassTemplate(new ChainedExecutorTemplate(executorModuleInfo, packageName), executorModuleInfo.getElement());
        }

        if (generateModuleAbstractExecutorsEnabled) {
            processClassTemplate(new AbstractExecutorTemplate(executorModuleInfo, packageName), executorModuleInfo.getElement());
        }

        if (generateModuleChainedGroupingExecutorsEnabled) {
            processClassTemplate(new ChainedGroupingExecutorTemplate(executorModuleInfo, packageName), executorModuleInfo.getElement());
        }

        if (executorModuleInfo.getAnnotation(PlainExecutor.class) != null) {
            processClassTemplate(new PlainExecutorTemplate(executorModuleInfo, packageName), executorModuleInfo.getElement());
            processClassTemplate(new PlainChainedExecutorTemplate(executorModuleInfo, packageName), executorModuleInfo.getElement());
            processClassTemplate(new PlainChainedGroupingExecutorTemplate(executorModuleInfo, packageName), executorModuleInfo.getElement());
        }
    }
}
