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
package org.uaithne.generator.commons;

import java.io.IOException;
import java.io.Writer;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import org.uaithne.generator.templates.ClassTemplate;

public abstract class TemplateProcessor extends AbstractProcessor {

    private static final GenerationInfo GENERATION_INFO = new GenerationInfo();
    
    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment re) {
        try {
            return doProcess(set, re);
        } catch(RuntimeException ex) {
            Logger.getLogger(TemplateProcessor.class.getName()).log(Level.SEVERE, "An unexpected error has occurred", ex);
            throw ex;
        }
    }
    
    public abstract boolean doProcess(Set<? extends TypeElement> set, RoundEnvironment re);

    public static GenerationInfo getGenerationInfo() {
        return GENERATION_INFO;
    }

    public boolean processClassTemplate(ClassTemplate template, Element element) {
        Writer writer = null;
        try {
            JavaFileObject jfo;
            String packageName = template.getPackageName();
            if (packageName == null || packageName.isEmpty()) {
                jfo = processingEnv.getFiler().createSourceFile(template.getClassName(), element);
            } else {
                jfo = processingEnv.getFiler().createSourceFile(packageName + "." + template.getClassName(), element);
            }
            writer = jfo.openWriter();
            template.write(writer);
            writer.flush();
        } catch (IOException ex) {
            String errorMessage = "An IOException has been ocurred processing the template: '" + template.getClass().getSimpleName() + "'. Package name: '" + template.getPackageName() + "'. Class name: '" + template.getClassName() + "'. Message: " + ex.toString();
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, errorMessage, element);
            Logger.getLogger(TemplateProcessor.class.getName()).log(Level.SEVERE, errorMessage, ex);
            return false;

        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException ex) {
                String errorMessage = "An IOException has been ocurred closing the writer for processing the template: '" + template.getClass().getSimpleName() + "'. Package name: '" + template.getPackageName() + "'. Class name: '" + template.getClassName() + "'. Message: " + ex.toString();
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, errorMessage, element);
                Logger.getLogger(TemplateProcessor.class.getName()).log(Level.SEVERE, errorMessage, ex);
                return false;
            }
        }
        return true;
    }
}
