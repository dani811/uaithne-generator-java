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
package org.uaithne.generator.processors.database.providers.derby;

import java.util.Set;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import org.uaithne.annotations.myBatis.MyBatisDerbyMapper;
import org.uaithne.generator.processors.database.myBatis.MyBatisMappersProcessor;

@SupportedSourceVersion(SourceVersion.RELEASE_6)
@SupportedAnnotationTypes("org.uaithne.annotations.myBatis.MyBatisDerbyMapper")
public class MyBatisDerbyMappersProcessor extends MyBatisMappersProcessor {

    public MyBatisDerbyMappersProcessor() {
        super(new MyBatisDerbySqlQueryGenerator());
        sqlGenerator.setProcessingEnv(processingEnv);
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment re) {
        for (Element element : re.getElementsAnnotatedWith(MyBatisDerbyMapper.class)) {
            if (element.getKind() == ElementKind.CLASS) {
                process(re, element);
            }
        }
        return true; // no further processing of this annotation type
    }

    @Override
    public boolean useAliasInOrderBy() {
        return false;
    }

    @Override
    public String subPackage() {
        return "myBatis.derby";
    }

    @Override
    public String mapperPrefix() {
        return "MyBatisDerby";
    }

    @Override
    public boolean useGeneratedKeys() {
        return true;
    }

}
