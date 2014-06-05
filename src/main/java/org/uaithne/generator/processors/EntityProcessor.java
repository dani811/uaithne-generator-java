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
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.MirroredTypeException;
import org.uaithne.annotations.Entity;
import org.uaithne.annotations.EntityView;
import org.uaithne.generator.commons.*;
import org.uaithne.generator.templates.EntityTemplate;

@SupportedSourceVersion(SourceVersion.RELEASE_6)
@SupportedAnnotationTypes({"org.uaithne.annotations.Entity", "org.uaithne.annotations.EntityView"})
public class EntityProcessor extends TemplateProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment re) {

        boolean generate = false;
        for (Element element : re.getElementsAnnotatedWith(Entity.class)) {
            if (element.getKind() == ElementKind.CLASS) {
                EntityInfo entity = process(re, element, EntityKind.ENTITY);
                generate = true;
                
                Entity entityAnnotation = entity.getAnnotation(Entity.class);
                DataTypeInfo relatedDataType;
                try {
                    relatedDataType = NamesGenerator.createResultDataType(entityAnnotation.related());
                } catch (MirroredTypeException ex) {
                    // See: http://blog.retep.org/2009/02/13/getting-class-values-from-annotations-in-an-annotationprocessor/
                    relatedDataType = NamesGenerator.createDataTypeFor(ex.getTypeMirror());
                }
                if (relatedDataType != null && !relatedDataType.isVoid()) {
                    entity.setRelatedType(relatedDataType);
                }
            }
        }
        for (Element element : re.getElementsAnnotatedWith(EntityView.class)) {
            if (element.getKind() == ElementKind.CLASS) {
                EntityInfo entity = process(re, element, EntityKind.VIEW);
                generate = true;
                
                EntityView entityAnnotation = entity.getAnnotation(EntityView.class);
                DataTypeInfo relatedDataType;
                try {
                    relatedDataType = NamesGenerator.createResultDataType(entityAnnotation.related());
                } catch (MirroredTypeException ex) {
                    // See: http://blog.retep.org/2009/02/13/getting-class-values-from-annotations-in-an-annotationprocessor/
                    relatedDataType = NamesGenerator.createDataTypeFor(ex.getTypeMirror());
                }
                if (relatedDataType != null && !relatedDataType.isVoid()) {
                    entity.setRelatedType(relatedDataType);
                }
            }
        }
        if (generate) {
            getGenerationInfo().combineAllEntities(true, processingEnv);
            for (EntityInfo entity : getGenerationInfo().getEntities()) {
                processClassTemplate(new EntityTemplate(entity), entity.getElement());
            }
        }
        return true; // no further processing of this annotation type
    }

    public EntityInfo process(RoundEnvironment re, Element element, EntityKind entityKind) {
        TypeElement classElement = (TypeElement) element;
        EntityInfo entityInfo = new EntityInfo(classElement, entityKind);
        entityInfo.addImplement(DataTypeInfo.SERIALIZABLE_DATA_TYPE);
        
        GenerationInfo generationInfo = getGenerationInfo();
        if (generationInfo.getEntitiesImplements() != null) {
            entityInfo.addImplement(generationInfo.getEntitiesImplements());
        }
        
        for (Element enclosedElement : classElement.getEnclosedElements()) {
            if (enclosedElement.getKind() == ElementKind.FIELD) {
                VariableElement ve = (VariableElement) enclosedElement;
                
                FieldInfo fi = new FieldInfo(ve);
                if (fi.isIdentifier()) {
                    fi.setOptional(false);
                }
                entityInfo.addField(fi);
            }
        }
        getGenerationInfo().addEntity(entityInfo);
        return entityInfo;
    }

}
