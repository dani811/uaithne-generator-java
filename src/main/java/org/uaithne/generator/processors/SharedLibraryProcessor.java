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
import org.uaithne.annotations.SharedLibrary;
import org.uaithne.generator.commons.DataTypeInfo;
import org.uaithne.generator.commons.GenerationInfo;
import org.uaithne.generator.commons.NamesGenerator;
import org.uaithne.generator.commons.TemplateProcessor;
import org.uaithne.generator.templates.shared.ChainedExecutorGroupTemplate;
import org.uaithne.generator.templates.shared.ChainedExecutorTemplate;
import org.uaithne.generator.templates.shared.ChainedGroupingExecutorTemplate;
import org.uaithne.generator.templates.shared.ChainedMappedExecutorGroupTemplate;
import org.uaithne.generator.templates.shared.DataPageRequestTemplate;
import org.uaithne.generator.templates.shared.DataPageTemplate;
import org.uaithne.generator.templates.shared.DeleteByIdOperationTemplate;
import org.uaithne.generator.templates.shared.ExecutorGroupTemplate;
import org.uaithne.generator.templates.shared.ExecutorTemplate;
import org.uaithne.generator.templates.shared.InsertValueOperationTemplate;
import org.uaithne.generator.templates.shared.JustInsertValueOperationTemplate;
import org.uaithne.generator.templates.shared.JustSaveValueOperationTemplate;
import org.uaithne.generator.templates.shared.LoggedExecutorGroupTemplate;
import org.uaithne.generator.templates.shared.MappedExecutorGroupTemplate;
import org.uaithne.generator.templates.shared.MergeValueOperationTemplate;
import org.uaithne.generator.templates.shared.OperationTemplate;
import org.uaithne.generator.templates.shared.PostOperationExecutorGroupTemplate;
import org.uaithne.generator.templates.shared.PostOperationExecutorTemplate;
import org.uaithne.generator.templates.shared.SaveValueOperationTemplate;
import org.uaithne.generator.templates.shared.SelectByIdOperationTemplate;
import org.uaithne.generator.templates.shared.UpdateValueOperationTemplate;

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
                        DataTypeInfo.updateSharedPackage("");
                    } else {
                        DataTypeInfo.updateSharedPackage(packageName);
                    }
                    if (!sl.generate()) {
                        continue;
                    }
                }

                processClassTemplate(new ExecutorTemplate(packageName), element);
                processClassTemplate(new OperationTemplate(packageName), element);
                processClassTemplate(new ExecutorGroupTemplate(packageName), element);
                processClassTemplate(new DeleteByIdOperationTemplate(packageName), element);
                processClassTemplate(new InsertValueOperationTemplate(packageName), element);
                processClassTemplate(new JustInsertValueOperationTemplate(packageName), element);
                processClassTemplate(new JustSaveValueOperationTemplate(packageName), element);
                processClassTemplate(new SaveValueOperationTemplate(packageName), element);
                processClassTemplate(new MergeValueOperationTemplate(packageName), element);
                processClassTemplate(new SelectByIdOperationTemplate(packageName), element);
                processClassTemplate(new UpdateValueOperationTemplate(packageName), element);
                processClassTemplate(new ChainedMappedExecutorGroupTemplate(packageName), element);
                processClassTemplate(new LoggedExecutorGroupTemplate(packageName), element);
                processClassTemplate(new MappedExecutorGroupTemplate(packageName), element);
                processClassTemplate(new ChainedExecutorTemplate(packageName), element);
                processClassTemplate(new ChainedExecutorGroupTemplate(packageName), element);
                processClassTemplate(new ChainedGroupingExecutorTemplate(packageName), element);
                processClassTemplate(new PostOperationExecutorTemplate(packageName), element);
                processClassTemplate(new PostOperationExecutorGroupTemplate(packageName), element);
                processClassTemplate(new DataPageTemplate(packageName), element);
                processClassTemplate(new DataPageRequestTemplate(packageName), element);
            }
        }
        return true; // no further processing of this annotation type
    }
}
