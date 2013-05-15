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
import org.uaithne.annotations.gwt.GwtAccesor;
import org.uaithne.generator.commons.*;

@SupportedSourceVersion(SourceVersion.RELEASE_6)
@SupportedAnnotationTypes("org.uaithne.annotations.gwt.GwtAccesor")
public class GwtAccesorProcessor extends TemplateProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment re) {
        GenerationInfo generationInfo = getGenerationInfo();

        for (Element element : re.getElementsAnnotatedWith(GwtAccesor.class)) {
            if (element.getKind() == ElementKind.CLASS) {
                TypeElement classElement = (TypeElement) element;
                DataTypeInfo dataType = NamesGenerator.createGwtAccesorDataType(classElement);

                HashMap<String, Object> data = createDefaultData();
                HashSet<String> imports = new HashSet<String>();
                for (OperationInfo op : generationInfo.getOperations()) {
                    op.appendReturnImports(dataType.getPackageName(), imports);
                }
                Utils.appendImportIfRequired(imports, dataType.getPackageName(), generationInfo.getSharedGwtPackageDot() + "client.AsyncExecutorGroup");
                Utils.appendImportIfRequired(imports, dataType.getPackageName(), generationInfo.getSharedGwtPackageDot() + "shared.rpc.AwaitGwtOperation");
                Utils.appendImportIfRequired(imports, dataType.getPackageName(), generationInfo.getSharedGwtPackageDot() + "shared.rpc.AwaitGwtResult");
                Utils.appendImportIfRequired(imports, dataType.getPackageName(), generationInfo.getSharedGwtPackageDot() + "shared.rpc.CombinedGwtOperation");
                Utils.appendImportIfRequired(imports, dataType.getPackageName(), generationInfo.getSharedGwtPackageDot() + "shared.rpc.CombinedGwtResult");
                Utils.appendImportIfRequired(imports, dataType.getPackageName(), generationInfo.getSharedPackageDot() + "Operation");
                data.put("imports", imports);
                data.put("operations", generationInfo.getOperations());

                processGwtAccesorTemplate(dataType.getSimpleName(), dataType.getPackageName(), data, element);
            }
        }
        return true; // no further processing of this annotation type
    }

    public boolean processGwtAccesorTemplate(String className, String packageName, HashMap<String, Object> data, Element element) {
        return processClassTemplate("GwtAccesor.ftl", packageName, className, data, element);
    }
}
