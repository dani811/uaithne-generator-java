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
package org.uaithne.generator.templates.gwt;

import java.io.IOException;
import java.util.HashSet;
import org.uaithne.generator.commons.DataTypeInfo;
import org.uaithne.generator.commons.EntityInfo;
import org.uaithne.generator.commons.GenerationInfo;
import org.uaithne.generator.commons.OperationInfo;
import org.uaithne.generator.templates.ClassTemplate;

public class GwtRemoteServiceResponseTypeIncluder extends ClassTemplate {
    
    private String serviceName;
    private GenerationInfo generationInfo;

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public GenerationInfo getGenerationInfo() {
        return generationInfo;
    }

    public void setGenerationInfo(GenerationInfo generationInfo) {
        this.generationInfo = generationInfo;
    }

    public GwtRemoteServiceResponseTypeIncluder(String packageName, String serviceName, GenerationInfo generationInfo) {
        setPackageName(packageName);
        addImport(DataTypeInfo.SERIALIZABLE_DATA_TYPE, packageName);
        addImport(DataTypeInfo.ARRAYLIST_DATA_TYPE, packageName);
        addImport(DataTypeInfo.DATA_PAGE_DATA_TYPE, packageName);
        addImport(DataTypeInfo.GWT_RPC_EXCEPTION_DATA_TYPE, packageName);
        addImport(DataTypeInfo.GWT_AWAIT_GWT_RESULT_DATA_TYPE, packageName);
        addImport(DataTypeInfo.GWT_COMBINED_GWT_RESULT_DATA_TYPE, packageName);
        if (generationInfo.getEntitiesImplements() != null) {
             addImport(generationInfo.getEntitiesImplements(), packageName);
        }
        setClassName(serviceName + "ResponseTypeIncluder");
        addImplement(DataTypeInfo.SERIALIZABLE_DATA);
        this.serviceName = serviceName;
        this.generationInfo = generationInfo;
    }

    @Override
    protected void writeContent(Appendable appender) throws IOException {
        appender.append("    ").append(DataTypeInfo.ARRAYLIST_DATA).append("<").append(DataTypeInfo.GWT_RPC_EXCEPTION_DATA_TYPE.getSimpleName()).append("> arraylist;\n");
        appender.append("    ").append(DataTypeInfo.DATA_PAGE_DATA_TYPE.getSimpleNameWithoutGenerics()).append("<").append(DataTypeInfo.GWT_RPC_EXCEPTION_DATA_TYPE.getSimpleName()).append("> datapage;\n");
        appender.append("    ").append(DataTypeInfo.GWT_AWAIT_GWT_RESULT_DATA_TYPE.getSimpleNameWithoutGenerics()).append(" awaitresult;\n");
        appender.append("    ").append(DataTypeInfo.GWT_COMBINED_GWT_RESULT_DATA_TYPE.getSimpleNameWithoutGenerics()).append(" combinedresult;\n");
        HashSet<String> types = new HashSet<String>(0);
        if (generationInfo.getEntitiesImplements() != null) {
            appender.append("    ").append(generationInfo.getEntitiesImplements().getSimpleName()).append(" entityBase;\n");
            for (OperationInfo operation : generationInfo.getOperations()) {
                EntityInfo entity = generationInfo.getEntityByName(operation.getResultItem());
                if (entity == null) {
                    types.add(operation.getResultItem().getPrintableQualifiedName());
                }
            }
        } else {
            for (OperationInfo operation : generationInfo.getOperations()) {
                types.add(operation.getResultItem().getPrintableQualifiedName());
            }
        }

        int i = 0;
        for (String type : types) {
            appender.append("    ").append(type).append(" result").append(Integer.toString(i)).append(";\n");
            i++;
        }
    }
    
}
