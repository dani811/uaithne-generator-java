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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypeException;
import javax.tools.Diagnostic;
import org.uaithne.annotations.UaithneConfiguration;
import org.uaithne.annotations.AnnotationConfiguration;
import org.uaithne.annotations.AnnotationConfigurationKeys;
import org.uaithne.annotations.AnnotationSubstitution;
import org.uaithne.generator.commons.DataTypeInfo;
import org.uaithne.generator.commons.GenerationInfo;
import org.uaithne.generator.commons.NamesGenerator;
import org.uaithne.generator.commons.TemplateProcessor;

@SupportedSourceVersion(SourceVersion.RELEASE_6)
@SupportedAnnotationTypes("org.uaithne.annotations.UaithneConfiguration")
public class UaithneConfigurationProcessor extends TemplateProcessor {

    @Override
    public boolean doProcess(Set<? extends TypeElement> set, RoundEnvironment re) {
        GenerationInfo generationInfo = getGenerationInfo();
        for (Element element : re.getElementsAnnotatedWith(UaithneConfiguration.class)) {
            if (element.getKind() == ElementKind.CLASS) {
                UaithneConfiguration configuration = element.getAnnotation(UaithneConfiguration.class);
                if (configuration != null) {
                    generationInfo.setConfigurationElement(element);
                    generationInfo.setGenerateDefaultEntityOperations(configuration.enableDefaultEntityOperations());
                    generationInfo.setGenerateJustOperationsEnabled(configuration.enableJustOperations());
                    generationInfo.setGenerateSaveOperationsEnabled(configuration.enableSaveOperations());
                    generationInfo.setGenerateMergeOperationsEnabled(configuration.enableMergeOperations());
                    generationInfo.setGenerateModuleAbstractExecutorsEnabled(configuration.enableModuleAbstractExecutors());
                    generationInfo.setGenerateModuleChainedExecutorsEnabled(configuration.enableModuleChainedExecutors());
                    generationInfo.setGenerateModuleChainedGroupingExecutorsEnabled(configuration.enableModuleChainedGroupingExecutors());
                    generationInfo.setMyBatisBackends(configuration.myBatisBackendConfigurations());
                    
                    generationInfo.setEnableBeanValidations(configuration.enableBeanValidations());
                    
                    HashMap<AnnotationConfigurationKeys, ArrayList<DataTypeInfo>> validationConfigurations = generationInfo.getValidationConfigurations();
                    for (AnnotationConfiguration annotationConfiguration : configuration.annotationConfigurations()) {
                        AnnotationConfigurationKeys key = annotationConfiguration.key();
                        DataTypeInfo dataType;
                        try {
                            dataType = NamesGenerator.createResultDataType(annotationConfiguration.value());
                        } catch (MirroredTypeException ex) {
                            // See: http://blog.retep.org/2009/02/13/getting-class-values-from-annotations-in-an-annotationprocessor/
                            dataType = NamesGenerator.createDataTypeFor(ex.getTypeMirror());
                        }
                        validationConfigurations.get(key).add(dataType);
                    }

                    ArrayList<DataTypeInfo> validationDataTypes = validationConfigurations.get(AnnotationConfigurationKeys.ID_STRING);
                    if (validationDataTypes.isEmpty()) {
                        validationDataTypes.addAll(validationConfigurations.get(AnnotationConfigurationKeys.ID));
                    }

                    validationDataTypes = validationConfigurations.get(AnnotationConfigurationKeys.MANDATORY_STRING);
                    if (validationDataTypes.isEmpty()) {
                        validationDataTypes.addAll(validationConfigurations.get(AnnotationConfigurationKeys.MANDATORY));
                    }

                    validationDataTypes = validationConfigurations.get(AnnotationConfigurationKeys.MANDATORY_WITH_DEFAULT_VALUE_WHEN_INSERT_STRING);
                    if (validationDataTypes.isEmpty()) {
                        validationDataTypes.addAll(validationConfigurations.get(AnnotationConfigurationKeys.MANDATORY_WITH_DEFAULT_VALUE_WHEN_INSERT));
                    }

                    validationDataTypes = validationConfigurations.get(AnnotationConfigurationKeys.OPTIONAL_STRING);
                    if (validationDataTypes.isEmpty()) {
                        validationDataTypes.addAll(validationConfigurations.get(AnnotationConfigurationKeys.OPTIONAL));
                    }

                    validationDataTypes = validationConfigurations.get(AnnotationConfigurationKeys.INSERT_ENTITY_VALUE);
                    if (validationDataTypes.isEmpty()) {
                        validationDataTypes.addAll(validationConfigurations.get(AnnotationConfigurationKeys.MANDATORY));
                    }

                    validationDataTypes = validationConfigurations.get(AnnotationConfigurationKeys.UPDATE_ENTITY_VALUE);
                    if (validationDataTypes.isEmpty()) {
                        validationDataTypes.addAll(validationConfigurations.get(AnnotationConfigurationKeys.MANDATORY));
                    }

                    validationDataTypes = validationConfigurations.get(AnnotationConfigurationKeys.MERGE_ENTITY_VALUE);
                    if (validationDataTypes.isEmpty()) {
                        validationDataTypes.addAll(validationConfigurations.get(AnnotationConfigurationKeys.MANDATORY));
                    }

                    validationDataTypes = validationConfigurations.get(AnnotationConfigurationKeys.SAVE_ENTITY_VALUE);
                    if (validationDataTypes.isEmpty()) {
                        validationDataTypes.addAll(validationConfigurations.get(AnnotationConfigurationKeys.MANDATORY));
                    }
                    
                    validationDataTypes = validationConfigurations.get(AnnotationConfigurationKeys.INSERT_GROUP);
                    if (validationDataTypes.size() > 1) {
                        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Only one INSERT_GROUP can exist", element);
                    }
                    
                    validationDataTypes = validationConfigurations.get(AnnotationConfigurationKeys.UPDATE_GROUP);
                    if (validationDataTypes.size() > 1) {
                        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Only one UPDATE_GROUP can exist", element);
                    }
                    
                    validationDataTypes = validationConfigurations.get(AnnotationConfigurationKeys.MERGE_GROUP);
                    if (validationDataTypes.size() > 1) {
                        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Only one MERGE_GROUP can exist", element);
                    }
                    
                    validationDataTypes = validationConfigurations.get(AnnotationConfigurationKeys.SAVE_GROUP);
                    if (validationDataTypes.size() > 1) {
                        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Only one SAVE_GROUP can exist", element);
                    }
                    
                    for (ArrayList<DataTypeInfo> types : validationConfigurations.values()) {
                        while(types.remove(DataTypeInfo.VOID_DATA_TYPE)) {
                            
                        }
                    }
                    
                    HashMap<AnnotationConfigurationKeys, HashMap<DataTypeInfo, DataTypeInfo>> validationSubstitutions = generationInfo.getValidationSubstitutions();
                    for (AnnotationSubstitution annotationSubstitution : configuration.annotationSubstitutions()) {
                        AnnotationConfigurationKeys key = annotationSubstitution.when();
                        
                        DataTypeInfo from;
                        try {
                            from = NamesGenerator.createResultDataType(annotationSubstitution.from());
                        } catch (MirroredTypeException ex) {
                            // See: http://blog.retep.org/2009/02/13/getting-class-values-from-annotations-in-an-annotationprocessor/
                            from = NamesGenerator.createDataTypeFor(ex.getTypeMirror());
                        }
                        
                        DataTypeInfo to;
                        try {
                            to = NamesGenerator.createResultDataType(annotationSubstitution.to());
                        } catch (MirroredTypeException ex) {
                            // See: http://blog.retep.org/2009/02/13/getting-class-values-from-annotations-in-an-annotationprocessor/
                            to = NamesGenerator.createDataTypeFor(ex.getTypeMirror());
                        }
                        
                        validationSubstitutions.get(key).put(from, to);
                        
                    }
                    
                    DataTypeInfo entitiesImplementsDataType;
                    try {
                        entitiesImplementsDataType = NamesGenerator.createResultDataType(configuration.entitiesImplements());
                    } catch (MirroredTypeException ex) {
                        // See: http://blog.retep.org/2009/02/13/getting-class-values-from-annotations-in-an-annotationprocessor/
                        entitiesImplementsDataType = NamesGenerator.createDataTypeFor(ex.getTypeMirror());
                    }
                    
                    if (entitiesImplementsDataType == null) {
                        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Unable to find the interface that the entities implements", element);
                        return true;
                    }
                    
                    if (!entitiesImplementsDataType.isVoid()) {
                        generationInfo.setEntitiesImplements(entitiesImplementsDataType);
                    }
                    
                    DataTypeInfo applicationParameterType;
                    try {
                        applicationParameterType = NamesGenerator.createResultDataType(configuration.applicationParameterType());
                    } catch (MirroredTypeException ex) {
                        // See: http://blog.retep.org/2009/02/13/getting-class-values-from-annotations-in-an-annotationprocessor/
                        applicationParameterType = NamesGenerator.createDataTypeFor(ex.getTypeMirror());
                    }
                    if (!applicationParameterType.isVoid()) {
                        generationInfo.setApplicationParameterType(applicationParameterType);
                    }
                }
            }
        }
        return true; // no further processing of this annotation type
    }
    
}
