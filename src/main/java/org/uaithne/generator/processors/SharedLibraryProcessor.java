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
import java.util.Set;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import org.uaithne.annotations.SharedLibrary;
import org.uaithne.generator.commons.DataTypeInfo;
import org.uaithne.generator.commons.GenerationInfo;
import org.uaithne.generator.commons.NamesGenerator;
import org.uaithne.generator.commons.TemplateProcessor;

@SupportedSourceVersion(SourceVersion.RELEASE_6)
@SupportedAnnotationTypes("org.uaithne.annotations.SharedLibrary")
public class SharedLibraryProcessor extends TemplateProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment re) {
        GenerationInfo generationInfo = getGenerationInfo();
        for (Element element : re.getElementsAnnotatedWith(SharedLibrary.class)) {
            if (element.getKind() == ElementKind.CLASS) {
                TypeElement classElement = (TypeElement) element;
                String packageName = NamesGenerator.createPackageNameFromFullName(classElement.getQualifiedName());
                
                SharedLibrary sl = element.getAnnotation(SharedLibrary.class);
                if (sl != null) {
                    if (packageName == null || packageName.isEmpty()) {
                        generationInfo.setSharedPackage("");
                        generationInfo.setSharedPackageDot("");
                    } else {
                        generationInfo.setSharedPackage(packageName);
                        generationInfo.setSharedPackageDot(packageName + ".");
                    }
                    if (!sl.generate()) {
                        continue;
                    }
                }
                
                HashMap<String, Object> data = createDefaultData();
                
                if (generationInfo.isUseResultInterface()) {
                    processClassTemplate("DataList", packageName, data, element);
                }
                processClassTemplate("Executor", packageName, data, element);
                processClassTemplate("Operation", packageName, data, element);
                if (generationInfo.isUseResultWrapperInterface()) {
                    processClassTemplate("ResultWrapper", packageName, data, element);
                }
                processClassTemplate("ExecutorGroup", packageName, data, element);
                processClassTemplate("DeleteByIdOperation", packageName, data, element);
                processClassTemplate("InsertValueOperation", packageName, data, element);
                if (generationInfo.isGenerateJustOperationsEnabled()) {
                    processClassTemplate("JustInsertValueOperation", packageName, data, element);
                    if (generationInfo.isGenerateSaveOperationsEnabled()) {
                        processClassTemplate("JustSaveValueOperation", packageName, data, element);
                    }
                }
                if (generationInfo.isGenerateSaveOperationsEnabled()) {
                    processClassTemplate("SaveValueOperation", packageName, data, element);
                }
                if (generationInfo.isGenerateMergeOperationsEnabled()) {
                    processClassTemplate("MergeValueOperation", packageName, data, element);
                }
                processClassTemplate("SelectByIdOperation", packageName, data, element);
                processClassTemplate("UpdateValueOperation", packageName, data, element);
                processClassTemplate("ChainedMappedExecutorGroup", packageName, data, element);
                processClassTemplate("LoggedExecutorGroup", packageName, data, element);
                processClassTemplate("MappedExecutorGroup", packageName, data, element);
                processClassTemplate("ChainedExecutor", packageName, data, element);
                processClassTemplate("ChainedExecutorGroup", packageName, data, element);
                processClassTemplate("ChainedGroupingExecutor", packageName, data, element);
                processClassTemplate("PostOperationExecutor", packageName, data, element);
                processClassTemplate("PostOperationExecutorGroup", packageName, data, element);
                
                HashSet<String> imports = new HashSet<String>();
                DataTypeInfo.PAGE_INFO_DATA_TYPE.appendImports(packageName, imports);
                data.put("imports", imports);
                data.put("pageInfoDataType", DataTypeInfo.PAGE_INFO_DATA_TYPE);
                processClassTemplate("DataPage", packageName, data, element);
                DataTypeInfo.PAGE_ONLY_DATA_COUNT_DATA_TYPE.appendImports(packageName, imports);
                data.put("pageOnlyDataCountDataType", DataTypeInfo.PAGE_ONLY_DATA_COUNT_DATA_TYPE);
                processClassTemplate("DataPageRequest", packageName, data, element);
            }
        }
        return true; // no further processing of this annotation type
    }
    
    public boolean processClassTemplate(String className, String packageName, HashMap<String, Object> data, Element element) {
        return processClassTemplate("shared/" + className + ".ftl", packageName, className, data, element);
    }
    
}
