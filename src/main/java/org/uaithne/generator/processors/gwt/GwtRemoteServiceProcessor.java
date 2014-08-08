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
package org.uaithne.generator.processors.gwt;

import java.util.Set;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import org.uaithne.annotations.gwt.GwtRemoteService;
import org.uaithne.generator.commons.*;
import org.uaithne.generator.templates.gwt.GwtRemoteServiceAsyncTemplate;
import org.uaithne.generator.templates.gwt.GwtRemoteServiceRequestTypeIncluder;
import org.uaithne.generator.templates.gwt.GwtRemoteServiceResponseTypeIncluder;
import org.uaithne.generator.templates.gwt.GwtRemoteServiceTemplate;

@SupportedSourceVersion(SourceVersion.RELEASE_6)
@SupportedAnnotationTypes("org.uaithne.annotations.gwt.GwtRemoteService")
public class GwtRemoteServiceProcessor extends TemplateProcessor {

    @Override
    public boolean doProcess(Set<? extends TypeElement> set, RoundEnvironment re) {
        GenerationInfo generationInfo = getGenerationInfo();

        for (Element element : re.getElementsAnnotatedWith(GwtRemoteService.class)) {
            if (element.getKind() == ElementKind.CLASS) {
                TypeElement classElement = (TypeElement) element;
                GwtRemoteService rs = element.getAnnotation(GwtRemoteService.class);
                String relativePath = rs.relativePath();
                DataTypeInfo dataType = NamesGenerator.createClassBasedDataType(classElement);
                String packageName = dataType.getPackageName();
                String serviceName = dataType.getSimpleNameWithoutGenerics();
                processClassTemplate(new GwtRemoteServiceTemplate(packageName, serviceName, relativePath), element);
                processClassTemplate(new GwtRemoteServiceAsyncTemplate(packageName, serviceName), element);
                processClassTemplate(new GwtRemoteServiceRequestTypeIncluder(packageName, serviceName, generationInfo), element);
                processClassTemplate(new GwtRemoteServiceResponseTypeIncluder(packageName, serviceName, generationInfo), element);
            }
        }
        return true; // no further processing of this annotation type
    }
}
