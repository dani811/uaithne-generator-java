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
import java.util.ArrayList;
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
        entity.appendImportsForConstructor(packageName, getImport());
        setDocumentation(entity.getDocumentation());
        setClassName(entity.getDataType().getSimpleNameWithoutGenerics());
        if (entity.getExtend() != null) {
            setExtend(entity.getExtend().getSimpleName());
        }
        for (DataTypeInfo type : entity.getImplement()) {
            addImplement(type.getSimpleName());
        }
        for (FieldInfo field : entity.getFields()) {
            appendAnnotationImports(packageName, getImport(), field);
        }
        setDeprecated(entity.isDeprecated());
        this.entity = entity;
    }

    void writeConstructors(Appendable appender) throws IOException {
        ArrayList<FieldInfo> fields = entity.getFields();
        ArrayList<FieldInfo> filteredMandatoryFields = new ArrayList<FieldInfo>(fields.size());
        for (FieldInfo field : fields) {
            if (field.isIdentifier()) {
                continue;
            }
            if (field.isOptional()) {
                continue;
            }
            if (!field.isExcludedFromConstructor()) {
                filteredMandatoryFields.add(field);
            }
        }

        ArrayList<FieldInfo> combinedFields = entity.getCombined().getFields();
        ArrayList<FieldInfo> filteredCombinedMandatoryFields = new ArrayList<FieldInfo>(combinedFields.size());
        for (FieldInfo field : combinedFields) {
            if (field.isIdentifier()) {
                continue;
            }
            if (field.isOptional()) {
                continue;
            }
            if (!field.isExcludedFromConstructor()) {
                filteredCombinedMandatoryFields.add(field);
            }
        }

        if (!filteredCombinedMandatoryFields.isEmpty()) {
            appender.append("    public ").append(getClassName()).append("() {\n"
                    + "    }\n"
                    + "\n");
        }

        appender.append("    public ").append(getClassName()).append("(");
        writeArguments(appender, filteredCombinedMandatoryFields);
        appender.append(") {\n");
        if (entity.getParent() != null) {
            ArrayList<FieldInfo> combinedParentFields = entity.getParent().getCombined().getFields();
            ArrayList<FieldInfo> filteredCombinedParentMandatoryFields = new ArrayList<FieldInfo>(combinedParentFields.size());
            for (FieldInfo field : combinedParentFields) {
                if (field.isIdentifier()) {
                    continue;
                }
                if (field.isOptional()) {
                    continue;
                }
                if (!field.isExcludedFromConstructor()) {
                    filteredCombinedParentMandatoryFields.add(field);
                }
            }

            appender.append("        super(");
            writeCallArguments(appender, filteredCombinedParentMandatoryFields);
            appender.append(");\n");
        }
        writeFieldsInitialization(appender, filteredMandatoryFields);
        appender.append("    }");

        ArrayList<FieldInfo> filteredIdAndMandatoryFields = new ArrayList<FieldInfo>(fields.size());
        for (FieldInfo field : fields) {
            if (field.isIdentifier() || !field.isOptional()) {
                if (!field.isExcludedFromConstructor()) {
                    filteredIdAndMandatoryFields.add(field);
                }
            }
        }

        ArrayList<FieldInfo> filteredCombinedIdFields = new ArrayList<FieldInfo>(combinedFields.size());
        for (FieldInfo field : combinedFields) {
            if (!field.isIdentifier()) {
                continue;
            }
            if (!field.isExcludedFromConstructor()) {
                filteredCombinedIdFields.add(field);
            }
        }

        ArrayList<FieldInfo> filteredCombinedIdAndMandatoryFields = new ArrayList<FieldInfo>(combinedFields.size());
        for (FieldInfo field : combinedFields) {
            if (field.isIdentifier() || !field.isOptional()) {
                if (!field.isExcludedFromConstructor()) {
                    filteredCombinedIdAndMandatoryFields.add(field);
                }
            }
        }

        if (!filteredCombinedIdFields.isEmpty()) {
            appender.append("\n\n");
            appender.append("    public ").append(getClassName()).append("(");
            writeArguments(appender, filteredCombinedIdAndMandatoryFields);
            appender.append(") {\n");
            if (entity.getParent() != null) {
                ArrayList<FieldInfo> combinedParentFields = entity.getParent().getCombined().getFields();
                ArrayList<FieldInfo> filteredCombinedParentIdAndMandatoryFields = new ArrayList<FieldInfo>(combinedParentFields.size());
                for (FieldInfo field : combinedParentFields) {
                    if (field.isIdentifier() || !field.isOptional()) {
                        if (!field.isExcludedFromConstructor()) {
                            filteredCombinedParentIdAndMandatoryFields.add(field);
                        }
                    }
                }

                appender.append("        super(");
                writeCallArguments(appender, filteredCombinedParentIdAndMandatoryFields);
                appender.append(");\n");
            }
            writeFieldsInitialization(appender, filteredIdAndMandatoryFields);
            appender.append("    }");
        }
    }

    @Override
    protected void writeContent(Appendable appender) throws IOException {
        boolean hasExtend = entity.getExtend() != null;
        ArrayList<FieldInfo> fields = entity.getFields();
        ArrayList<FieldInfo> idFields = new ArrayList<FieldInfo>(fields.size());
        for (FieldInfo field : entity.getFields()) {
            if (field.isIdentifier()) {
                idFields.add(field);
            }
            writeField(appender, field);
        }
        for (FieldInfo field : entity.getFields()) {
            appender.append("\n");
            writeFieldGetter(appender, field);
            appender.append("\n");
            writeFieldSetter(appender, field);
        }

        appender.append("\n");
        writeToString(appender, entity.getFields(), hasExtend);

        appender.append("\n");
        writeEquals(appender, idFields, hasExtend);

        String firstPrime = Integer.toString(entity.generateFistPrimeNumberForHashCode());
        String secondPrime = Integer.toString(entity.generateSecondPrimeNumberForHashCode());

        appender.append("\n");
        writeHashCode(appender, idFields, hasExtend, firstPrime, secondPrime);

        appender.append("\n");
        writeConstructors(appender);
    }
}
