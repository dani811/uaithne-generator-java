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

import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

public abstract class TemplateProcessor extends AbstractProcessor {
    private static GenerationInfo GENERATION_INFO = new GenerationInfo();
    
    public static GenerationInfo getGenerationInfo() {
        return GENERATION_INFO;
    }
    
    Configuration configuration;

    @Override
    public synchronized void init(ProcessingEnvironment pe) {
        super.init(pe);
        configuration = new Configuration();
        configuration.setClassForTemplateLoading(TemplateLoader.class, "/org/uaithne/generator/templates/");
        configuration.setObjectWrapper(new DefaultObjectWrapper());
    }

    public Configuration getConfiguration() {
        return configuration;
    }
    
    public Template getTemplate(String templateName) throws IOException {
        return configuration.getTemplate(templateName);
    }
    
    public HashMap<String, Object> createDefaultData() {
        GenerationInfo generationInfo = getGenerationInfo();
        HashMap<String,Object> result = new HashMap<String, Object>();
        result.put("system", System.getProperties());
        result.put("env", System.getenv());
        result.put("generation", generationInfo);
        if (generationInfo.isUseResultInterface()) {
            result.put("resultBaseDefinition", "RESULT extends Result");
            result.put("operationBaseDefinition", "<RESULT extends Result, OPERATION extends Operation<RESULT>>");
        } else {
            result.put("resultBaseDefinition", "RESULT");
            result.put("operationBaseDefinition", "<RESULT, OPERATION extends Operation<RESULT>>");
        }
        if (generationInfo.isUseConcreteCollections()) {
            result.put("listName", "ArrayList");
            result.put("listQualifiedName", "java.util.ArrayList");
        } else {
            result.put("listName", "List");
            result.put("listQualifiedName", "java.util.List");
        }
        return result;
    }
    
    public boolean processClassTemplate(String templateName, String packageName, String className, HashMap<String, Object> data, Element element) {
        Writer writer = null;
        try {
            data.put("className", className);
            data.put("packageName", packageName);
            Template template = getTemplate(templateName);
            JavaFileObject jfo;
            if (packageName == null || packageName.isEmpty()) {
                jfo = processingEnv.getFiler().createSourceFile(className, element);
            } else {
                jfo = processingEnv.getFiler().createSourceFile(packageName + "." + className, element);
            }
            writer = jfo.openWriter();
            template.process(data, writer);
            writer.flush();
        } catch (IOException ex) {
            String errorMessage = "An IOException has been ocurred processing the template: '" + templateName + "'. Package name: '" + packageName + "'. Class name: '" + className + "'. Message: " + ex.toString();
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, errorMessage, element);
            Logger.getLogger(TemplateProcessor.class.getName()).log(Level.SEVERE, errorMessage, ex);
            return false;
        } catch (TemplateException ex) {
            String errorMessage = "An TemplateEception has been ocurred processing the template: '" + templateName + "'. Package name: '" + packageName + "'. Class name: '" + className + "'. Message: " + ex.toString();
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, errorMessage, element);
            Logger.getLogger(TemplateProcessor.class.getName()).log(Level.SEVERE, errorMessage, ex);
            return false;
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException ex) {
                String errorMessage = "An IOException has been ocurred closing the writer for processing the template: '" + templateName + "'. Package name: '" + packageName + "'. Class name: '" + className + "'. Message: " + ex.toString();
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, errorMessage, element);
                Logger.getLogger(TemplateProcessor.class.getName()).log(Level.SEVERE, errorMessage, ex);
                return false;
            }
        }
        return true;
    }
    
}
