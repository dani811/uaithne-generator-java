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
import javax.lang.model.type.MirroredTypeException;
import javax.tools.Diagnostic;
import org.uaithne.annotations.UaithneConfiguration;
import org.uaithne.generator.commons.DataTypeInfo;
import org.uaithne.generator.commons.GenerationInfo;
import org.uaithne.generator.commons.NamesGenerator;
import org.uaithne.generator.commons.TemplateProcessor;

@SupportedSourceVersion(SourceVersion.RELEASE_6)
@SupportedAnnotationTypes("org.uaithne.annotations.UaithneConfiguration")
public class UaithneConfigurationProcessor extends TemplateProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment re) {
        GenerationInfo generationInfo = getGenerationInfo();
        for (Element element : re.getElementsAnnotatedWith(UaithneConfiguration.class)) {
            if (element.getKind() == ElementKind.CLASS) {
                UaithneConfiguration configuration = element.getAnnotation(UaithneConfiguration.class);
                if (configuration != null) {
                    generationInfo.setGenerateDefaultEntityOperations(configuration.enableDefaultEntityOperations());
                    generationInfo.setGenerateJustOperationsEnabled(configuration.enableJustOperations());
                    generationInfo.setGenerateSaveOperationsEnabled(configuration.enableSaveOperations());
                    generationInfo.setGenerateMergeOperationsEnabled(configuration.enableMergeOperations());
                    generationInfo.setGenerateModuleAbstractExecutorsEnabled(configuration.enableModuleAbstractExecutors());
                    generationInfo.setGenerateModuleChainedExecutorsEnabled(configuration.enableModuleChainedExecutors());
                    generationInfo.setGenerateModuleChainedGroupingExecutorsEnabled(configuration.enableModuleChainedGroupingExecutors());
                    generationInfo.setMyBatisBackends(configuration.myBatisBackendConfigurations());
                    generationInfo.setIgnoreIdValidationOnPrimitives(configuration.ignoreIdValidationOnPrimitives());
                    generationInfo.setIgnoreMandatoryValidationOnPrimitives(configuration.ignoreMandatoryValidationOnPrimitives());
                    
                    DataTypeInfo idValidationAnnotation;
                    try {
                        idValidationAnnotation = NamesGenerator.createResultDataType(configuration.idValidationAnnotation());
                    } catch (MirroredTypeException ex) {
                        // See: http://blog.retep.org/2009/02/13/getting-class-values-from-annotations-in-an-annotationprocessor/
                        idValidationAnnotation = NamesGenerator.createDataTypeFor(ex.getTypeMirror());
                    }
                    if (!idValidationAnnotation.isVoid()) {
                        generationInfo.setIdValidationAnnotation(idValidationAnnotation);
                    }
                    
                    DataTypeInfo mandatoryValidationAnnotation;
                    try {
                        mandatoryValidationAnnotation = NamesGenerator.createResultDataType(configuration.mandatoryValidationAnnotation());
                    } catch (MirroredTypeException ex) {
                        // See: http://blog.retep.org/2009/02/13/getting-class-values-from-annotations-in-an-annotationprocessor/
                        mandatoryValidationAnnotation = NamesGenerator.createDataTypeFor(ex.getTypeMirror());
                    }
                    if (!mandatoryValidationAnnotation.isVoid()) {
                        generationInfo.setMandatoryValidationAnnotation(mandatoryValidationAnnotation);
                    }
                    
                    DataTypeInfo optionalValidationAnnotation;
                    try {
                        optionalValidationAnnotation = NamesGenerator.createResultDataType(configuration.optionalValidationAnnotation());
                    } catch (MirroredTypeException ex) {
                        // See: http://blog.retep.org/2009/02/13/getting-class-values-from-annotations-in-an-annotationprocessor/
                        optionalValidationAnnotation = NamesGenerator.createDataTypeFor(ex.getTypeMirror());
                    }
                    if (!optionalValidationAnnotation.isVoid()) {
                        generationInfo.setOptionalValidationAnnotation(optionalValidationAnnotation);
                    }
                    
                    DataTypeInfo insertValueValidationAnnotation;
                    try {
                        insertValueValidationAnnotation = NamesGenerator.createResultDataType(configuration.insertValueValidationAnnotation());
                    } catch (MirroredTypeException ex) {
                        // See: http://blog.retep.org/2009/02/13/getting-class-values-from-annotations-in-an-annotationprocessor/
                        insertValueValidationAnnotation = NamesGenerator.createDataTypeFor(ex.getTypeMirror());
                    }
                    if (!insertValueValidationAnnotation.isVoid()) {
                        generationInfo.setInsertValueValidationAnnotation(insertValueValidationAnnotation);
                    }
                    
                    DataTypeInfo saveValueValidationAnnotation;
                    try {
                        saveValueValidationAnnotation = NamesGenerator.createResultDataType(configuration.saveValueValidationAnnotation());
                    } catch (MirroredTypeException ex) {
                        // See: http://blog.retep.org/2009/02/13/getting-class-values-from-annotations-in-an-annotationprocessor/
                        saveValueValidationAnnotation = NamesGenerator.createDataTypeFor(ex.getTypeMirror());
                    }
                    if (!saveValueValidationAnnotation.isVoid()) {
                        generationInfo.setSaveValueValidationAnnotation(saveValueValidationAnnotation);
                    }
                    
                    DataTypeInfo mergeValueValidationAnnotation;
                    try {
                        mergeValueValidationAnnotation = NamesGenerator.createResultDataType(configuration.mergeValueValidationAnnotation());
                    } catch (MirroredTypeException ex) {
                        // See: http://blog.retep.org/2009/02/13/getting-class-values-from-annotations-in-an-annotationprocessor/
                        mergeValueValidationAnnotation = NamesGenerator.createDataTypeFor(ex.getTypeMirror());
                    }
                    if (!mergeValueValidationAnnotation.isVoid()) {
                        generationInfo.setMergeValueValidationAnnotation(mergeValueValidationAnnotation);
                    }
                    
                    DataTypeInfo updateValueValidationAnnotation;
                    try {
                        updateValueValidationAnnotation = NamesGenerator.createResultDataType(configuration.updateValueValidationAnnotation());
                    } catch (MirroredTypeException ex) {
                        // See: http://blog.retep.org/2009/02/13/getting-class-values-from-annotations-in-an-annotationprocessor/
                        updateValueValidationAnnotation = NamesGenerator.createDataTypeFor(ex.getTypeMirror());
                    }
                    if (!updateValueValidationAnnotation.isVoid()) {
                        generationInfo.setUpdateValueValidationAnnotation(updateValueValidationAnnotation);
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
                }
            }
        }
        return true; // no further processing of this annotation type
    }
    
}
