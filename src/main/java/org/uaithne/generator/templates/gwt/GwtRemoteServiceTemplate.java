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

public class GwtRemoteServiceTemplate extends ClassTemplate {
    
    private String relativePath;
    private String serviceName;

    public String getRelativePath() {
        return relativePath;
    }

    public void setRelativePath(String relativePath) {
        this.relativePath = relativePath;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
    
    public GwtRemoteServiceTemplate(String packageName, String serviceName, String relativePath) {
        setPackageName(packageName);
        addImport("com.google.gwt.user.client.rpc.RemoteService", packageName);
        addImport("com.google.gwt.user.client.rpc.RemoteServiceRelativePath", packageName);
        addImport(DataTypeInfo.GWT_EXECUTOR_GROUP_RPC_DATA_TYPE, packageName);
        setClassName(serviceName);
        addImplement("RemoteService");
        addImplement(DataTypeInfo.GWT_EXECUTOR_GROUP_RPC_DATA_TYPE.getSimpleName());
        this.relativePath = relativePath;
        this.serviceName = serviceName;
        setInterface(true);
    }

    @Override
    protected void writeContent(Appendable appender) throws IOException {
        appender.append("    public ").append(serviceName).append("ResponseTypeIncluder includeTypes(").append(serviceName).append("RequestTypeIncluder request);");
    }

    @Override
    protected void writeClassAnnotations(Appendable appender) throws IOException {
        super.writeClassAnnotations(appender);
        appender.append("@RemoteServiceRelativePath(\"").append(relativePath).append("\")\n");
    }
    
}
