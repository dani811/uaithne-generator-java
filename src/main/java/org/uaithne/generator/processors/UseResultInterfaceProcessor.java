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
import javax.lang.model.type.MirroredTypeException;
import javax.tools.Diagnostic;
import org.uaithne.annotations.SharedLibrary;
import org.uaithne.annotations.UseResultInterface;
import org.uaithne.generator.commons.DataTypeInfo;
import org.uaithne.generator.commons.EntityInfo;
import org.uaithne.generator.commons.GenerationInfo;
import org.uaithne.generator.commons.NamesGenerator;
import org.uaithne.generator.commons.TemplateProcessor;

@SupportedSourceVersion(SourceVersion.RELEASE_6)
@SupportedAnnotationTypes("org.uaithne.annotations.UseResultInterface")
public class UseResultInterfaceProcessor extends TemplateProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment re) {
        GenerationInfo generationInfo = getGenerationInfo();
        for (Element element : re.getElementsAnnotatedWith(UseResultInterface.class)) {
            if (element.getKind() == ElementKind.CLASS) {
                TypeElement classElement = (TypeElement) element;
                String packageName = NamesGenerator.createPackageNameFromFullName(classElement.getQualifiedName());
                
                UseResultInterface useResultInterface = element.getAnnotation(UseResultInterface.class);
                if (useResultInterface != null) {
                    generationInfo.setUseResultInterface(true);
                    if (packageName == null || packageName.isEmpty()) {
                        generationInfo.setResultsPackage("");
                        generationInfo.setResultsPackageDot("");
                    } else {
                        generationInfo.setResultsPackage(packageName);
                        generationInfo.setResultsPackageDot(packageName + ".");
                    }
                    if (useResultInterface.generate()) {
                        DataTypeInfo relatedDataType;
                        try {
                            relatedDataType = NamesGenerator.createResultDataType(useResultInterface.resultInterfaceExtends());
                        } catch (MirroredTypeException ex) {
                            // See: http://blog.retep.org/2009/02/13/getting-class-values-from-annotations-in-an-annotationprocessor/
                            relatedDataType = NamesGenerator.createDataTypeFor(ex.getTypeMirror());
                        }
                        if (relatedDataType == null) {
                            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Unable to find the class that the result interface extends", element);
                            return true;
                        }
                        
                        for (Element e : re.getElementsAnnotatedWith(SharedLibrary.class)) {
                            if (e.getKind() == ElementKind.CLASS) {
                                TypeElement ce = (TypeElement) e;
                                String pn = NamesGenerator.createPackageNameFromFullName(ce.getQualifiedName());

                                HashSet<String> imports = new HashSet<String>();
                                String extendsClausule = "";
                                if (!"java.lang.Void".equals(relatedDataType.getQualifiedName())) {
                                    relatedDataType.appendImports(pn, imports);
                                    extendsClausule = ", " + relatedDataType.getSimpleName();
                                }

                                HashMap<String, Object> data = createDefaultData();
                                data.put("imports", imports);
                                data.put("extendsClausule", extendsClausule);
                                processClassTemplate("Result", pn, data, element);
                            }
                        }
                    }
                }
            }
        }
        return true; // no further processing of this annotation type
    }
    
    public boolean processClassTemplate(String className, String packageName, HashMap<String, Object> data, Element element) {
        return processClassTemplate("shared/" + className + ".ftl", packageName, className, data, element);
    }
    
}
