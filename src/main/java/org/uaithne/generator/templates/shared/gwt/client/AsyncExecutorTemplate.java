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
package org.uaithne.generator.templates.shared.gwt.client;

import java.io.IOException;
import org.uaithne.generator.commons.DataTypeInfo;
import org.uaithne.generator.templates.ClassTemplate;

public class AsyncExecutorTemplate extends ClassTemplate {

    public AsyncExecutorTemplate(String sharedGwtPackageDot) {
        String packageName = sharedGwtPackageDot + "client";
        setPackageName(packageName);
        addImport("com.google.gwt.user.client.rpc.AsyncCallback", packageName);
        addImport(DataTypeInfo.OPERATION_DATA_TYPE, packageName);
        setClassName("AsyncExecutor");
        setInterface(true);
    }
    
    @Override
    protected void writeContent(Appendable appender) throws IOException {
        appender.append("    public Object getExecutorSelector();\n"
                + "\n"
                + "    public ").append(OPERATION_BASE_DEFINITION).append(" void execute(OPERATION operation, AsyncCallback<RESULT> asyncCallback);\n");
        
        if (getGenerationInfo().isIncludeExecuteOtherMethodInExecutors()) {
            appender.append("\n"
                + "    public ").append(OPERATION_BASE_DEFINITION).append(" void executeOther(OPERATION operation, AsyncCallback<RESULT> asyncCallback);");
        }
    }
    
}
