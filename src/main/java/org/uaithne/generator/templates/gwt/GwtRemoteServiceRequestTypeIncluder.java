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
import org.uaithne.generator.commons.DataTypeInfo;
import org.uaithne.generator.templates.ClassTemplate;

public class GwtRemoteServiceRequestTypeIncluder extends ClassTemplate {
    
    private String serviceName;

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public GwtRemoteServiceRequestTypeIncluder(String packageName, String serviceName) {
        setPackageName(packageName);
        addImport(DataTypeInfo.SERIALIZABLE_DATA_TYPE, packageName);
        addImport(DataTypeInfo.ARRAYLIST_DATA_TYPE, packageName);
        addImport(DataTypeInfo.OPERATION_DATA_TYPE, packageName);
        setClassName(serviceName + "RequestTypeIncluder");
        addImplement(DataTypeInfo.SERIALIZABLE_DATA);
        this.serviceName = serviceName;
    }

    @Override
    protected void writeContent(Appendable appender) throws IOException {
        appender.append("    ").append(DataTypeInfo.ARRAYLIST_DATA).append("<Operation> arraylist;\n");
    }
    
}
