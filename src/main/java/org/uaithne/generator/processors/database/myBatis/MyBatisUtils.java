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
package org.uaithne.generator.processors.database.myBatis;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.type.MirroredTypeException;
import javax.tools.Diagnostic;
import org.uaithne.annotations.myBatis.MyBatisTypeHandler;
import org.uaithne.annotations.sql.JdbcType;
import org.uaithne.annotations.sql.UseJdbcType;
import org.uaithne.generator.commons.DataTypeInfo;
import org.uaithne.generator.commons.FieldInfo;
import org.uaithne.generator.commons.NamesGenerator;

public class MyBatisUtils {

    public static JdbcType getJdbcType(FieldInfo field) {
        UseJdbcType ujt = field.getAnnotation(UseJdbcType.class);
        if (ujt != null) {
            return ujt.value();
        }

        String name = field.getDataType().getSimpleName();
        if ("String".equals(name)) {
            return JdbcType.VARCHAR;
        } else if ("Date".equals(name)) {
            return JdbcType.TIMESTAMP;
        } else if ("Time".equals(name)) {
            return JdbcType.TIME;
        } else if ("Timestamp".equals(name)) {
            return JdbcType.TIMESTAMP;
        } else if ("Boolean".equals(name)) {
            return JdbcType.BOOLEAN;
        } else if ("boolean".equals(name)) {
            return JdbcType.BOOLEAN;
        } else if ("List<String>".equals(name)) {
            return JdbcType.VARCHAR;
        } else if ("ArrayList<String>".equals(name)) {
            return JdbcType.VARCHAR;
        } else if ("List<Date>".equals(name)) {
            return JdbcType.TIMESTAMP;
        } else if ("ArrayList<Date>".equals(name)) {
            return JdbcType.TIMESTAMP;
        } else if ("List<Time>".equals(name)) {
            return JdbcType.TIME;
        } else if ("ArrayList<Time>".equals(name)) {
            return JdbcType.TIME;
        } else if ("List<Timestamp>".equals(name)) {
            return JdbcType.TIMESTAMP;
        } else if ("ArrayList<Timestamp>".equals(name)) {
            return JdbcType.TIMESTAMP;
        } else if ("List<Boolean>".equals(name)) {
            return JdbcType.BOOLEAN;
        } else if ("ArrayList<Boolean>".equals(name)) {
            return JdbcType.BOOLEAN;
        } else {
            return JdbcType.NUMERIC;
        }
    }

    public static String getJdbcTypeAttribute(FieldInfo field) {

        JdbcType jdbcType = getJdbcType(field);
        return ",jdbcType=" + jdbcType.name();
    }

    public static String getTypeHandler(ProcessingEnvironment processingEnv, FieldInfo field) {
        MyBatisTypeHandler th = field.getAnnotation(MyBatisTypeHandler.class);

        if (th == null) {
            return "";
        }

        DataTypeInfo typeHandler;
        try {
            typeHandler = NamesGenerator.createResultDataType(th.value());
        } catch (MirroredTypeException ex) {
            // See: http://blog.retep.org/2009/02/13/getting-class-values-from-annotations-in-an-annotationprocessor/
            typeHandler = NamesGenerator.createDataTypeFor(ex.getTypeMirror());
        }
        if (typeHandler == null) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Unable to find the type handler", field.getElement());
            return "";
        }

        return ",typeHandler=" + typeHandler.getQualifiedNameWithoutGenerics();
    }

    private MyBatisUtils() {
    }
}
