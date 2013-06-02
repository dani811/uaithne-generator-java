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
import java.util.ArrayList;
import static org.uaithne.generator.commons.DataTypeInfo.*;
import org.uaithne.generator.commons.OperationInfo;
import org.uaithne.generator.templates.ClassTemplate;

public class GwtAccesorTemplate extends ClassTemplate {

    private ArrayList<OperationInfo> operations;

    public ArrayList<OperationInfo> getOperations() {
        return operations;
    }

    public void setOperations(ArrayList<OperationInfo> operations) {
        this.operations = operations;
    }

    public GwtAccesorTemplate(ArrayList<OperationInfo> operations, String packageName, String className) {
        setPackageName(packageName);
        addImport("com.google.gwt.user.client.rpc.AsyncCallback", packageName);
        addImport(GWT_ASYNC_EXECUTOR_GROUP_DATA_TYPE, packageName);
        addImport(GWT_AWAIT_GWT_OPERATION_DATA_TYPE, packageName);
        addImport(GWT_AWAIT_GWT_RESULT_DATA_TYPE, packageName);
        addImport(GWT_COMBINED_GWT_OPERATION_DATA_TYPE, packageName);
        addImport(GWT_COMBINED_GWT_RESULT_DATA_TYPE, packageName);
        addImport(OPERATION_DATA_TYPE, packageName);
        setClassName(className);
        addImplement("AsyncExecutorGroup");
        this.operations = operations;
    }

    @Override
    protected void writeContent(Appendable appender) throws IOException {
        appender.append("    private AsyncExecutorGroup chainedExecutorGroup;\n"
                + "\n"
                + "    @Override\n"
                + "    public ").append(OPERATION_BASE_DEFINITION).append("\n"
                + "        void execute(final OPERATION operation, final AsyncCallback<RESULT> asyncCallback) {\n"
                + "        chainedExecutorGroup.execute(operation, asyncCallback);\n"
                + "    }\n"
                + "\n"
                + "    public <RESULT extends CombinedGwtResult> void execute(CombinedGwtOperation<RESULT> operation,  AsyncCallback<RESULT> asyncCallback) {\n"
                + "        chainedExecutorGroup.execute(operation, asyncCallback);\n"
                + "    }\n"
                + "\n"
                + "    public void execute(AwaitGwtOperation operation,  AsyncCallback<AwaitGwtResult> asyncCallback) {\n"
                + "        chainedExecutorGroup.execute(operation, asyncCallback);\n"
                + "    }\n"
                + "\n");

        for (OperationInfo operation : operations) {
            appender.append("    public void execute(").append(operation.getDataType().getPrintableQualifiedName()).append(" operation, AsyncCallback<").append(operation.getReturnDataType().getPrintableQualifiedName()).append("> asyncCallback) {\n"
                    + "        chainedExecutorGroup.execute(operation, asyncCallback);\n"
                    + "    }\n"
                    + "\n");
        }

        appender.append("    public AsyncExecutorGroup getChainedExecutorGroup() {\n"
                + "        return chainedExecutorGroup;\n"
                + "    }\n"
                + "\n"
                + "    public ").append(getClassName()).append("(AsyncExecutorGroup chainedExecutorGroup) {\n"
                + "        if (chainedExecutorGroup == null) {\n"
                + "            throw new IllegalArgumentException(\"chainedExecutorGroup for the ").append(getClassName()).append(" cannot be null\");\n"
                + "        }\n"
                + "        this.chainedExecutorGroup = chainedExecutorGroup;\n"
                + "    }");
    }
}
