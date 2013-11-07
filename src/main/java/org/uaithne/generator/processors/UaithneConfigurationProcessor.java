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
import org.uaithne.annotations.SharedLibrary;
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
        for (Element element : re.getElementsAnnotatedWith(SharedLibrary.class)) {
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
