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
package org.uaithne.generator.templates;

import java.io.IOException;
import org.uaithne.generator.commons.DataTypeInfo;
import org.uaithne.generator.commons.EntityInfo;
import org.uaithne.generator.commons.FieldInfo;

public class EntityTemplate extends PojoTemplate {
    
    private EntityInfo entity;

    public EntityInfo getEntity() {
        return entity;
    }

    public void setEntity(EntityInfo entity) {
        this.entity = entity;
    }

    public EntityTemplate(EntityInfo entity) {
        String packageName = entity.getDataType().getPackageName();
        setPackageName(packageName);
        entity.appendFullImports(packageName, getImport());
        setDocumentation(entity.getDocumentation());
        setClassName(entity.getDataType().getSimpleName());
        if (entity.getExtend() != null) {
            setExtend(entity.getExtend().getSimpleName());
        }
        for (DataTypeInfo type : entity.getImplement()) {
            addImplement(type.getSimpleName());
        }
        this.entity = entity;
    }
    
    void writeConstructors(Appendable appender) throws IOException {
        if (!entity.getMandatoryFields().isEmpty()) {
            appender.append("    public ").append(getClassName()).append("() {\n"
                    + "    }\n"
                    + "\n");
        }

        appender.append("    public ").append(getClassName()).append("(");
        writeArguments(appender, entity.getCombined().getMandatoryFields());
        appender.append(") {\n");
        if (entity.getParent() != null) {
            appender.append("        super(");
            writeCallArguments(appender, entity.getParent().getMandatoryFields());
            appender.append(");\n");
        }
        writeFieldsInitialization(appender, entity.getMandatoryFields());
        appender.append("    }");

        if (!entity.getCombined().getIdFields().isEmpty()) {
            appender.append("\n\n");
            appender.append("    public ").append(getClassName()).append("(");
            writeArguments(appender, entity.getCombined().getIdAndMandatoryFields());
            appender.append(") {\n");
            if (entity.getParent() != null) {
                appender.append("        super(");
                writeCallArguments(appender, entity.getParent().getIdAndMandatoryFields());
                appender.append(");\n");
            }
            writeFieldsInitialization(appender, entity.getIdAndMandatoryFields());
            appender.append("    }");
        }
    }
    
    @Override
    protected void writeContent(Appendable appender) throws IOException {
        for (FieldInfo field : entity.getFieldsWithExtras()) {
            writeField(appender, field);
        }
        for (FieldInfo field : entity.getFieldsWithExtras()) {
            appender.append("\n");
            writeFieldGetter(appender, field);
            appender.append("\n");
            writeFieldSetter(appender, field);
        }
        
        appender.append("\n");
        writeToString(appender, entity.getFieldsWithExtras(), entity.getExtend() != null);
        
        boolean callSuper = entity.getExtend() != null || entity.getIdFields().isEmpty();
        
        appender.append("\n");
        writeStartEquals(appender, entity.getIdFields(), callSuper, false);
        writeEndEquals(appender);
        
        String firstPrime = Integer.toString(entity.generateFistPrimeNumberForHashCode());
        String secondPrime = Integer.toString(entity.generateSecondPrimeNumberForHashCode());

        appender.append("\n");
        writeStartHashCode(appender, entity.getIdFields(), callSuper, firstPrime, secondPrime);
        writeEndHashCode(appender);
        
        appender.append("\n");
        writeConstructors(appender);
    }
    
}
