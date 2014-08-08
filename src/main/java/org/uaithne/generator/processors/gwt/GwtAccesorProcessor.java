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
import org.uaithne.annotations.gwt.GwtAccesor;
import org.uaithne.generator.commons.*;
import org.uaithne.generator.templates.gwt.GwtAccesorTemplate;

@SupportedSourceVersion(SourceVersion.RELEASE_6)
@SupportedAnnotationTypes("org.uaithne.annotations.gwt.GwtAccesor")
public class GwtAccesorProcessor extends TemplateProcessor {

    @Override
    public boolean doProcess(Set<? extends TypeElement> set, RoundEnvironment re) {
        GenerationInfo generationInfo = getGenerationInfo();

        for (Element element : re.getElementsAnnotatedWith(GwtAccesor.class)) {
            if (element.getKind() == ElementKind.CLASS) {
                TypeElement classElement = (TypeElement) element;
                DataTypeInfo dataType = NamesGenerator.createClassBasedDataType(classElement);
                GwtAccesorTemplate template = new GwtAccesorTemplate(
                        generationInfo.getOperations(),
                        dataType.getPackageName(),
                        dataType.getSimpleNameWithoutGenerics());
                processClassTemplate(template, element);
            }
        }
        return true; // no further processing of this annotation type
    }
}
