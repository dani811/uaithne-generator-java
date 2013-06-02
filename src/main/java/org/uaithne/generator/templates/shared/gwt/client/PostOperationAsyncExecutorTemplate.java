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

public class PostOperationAsyncExecutorTemplate extends ClassTemplate {

    public PostOperationAsyncExecutorTemplate(String sharedGwtPackageDot) {
        String packageName = sharedGwtPackageDot + "client";
        setPackageName(packageName);
        addImport("com.google.gwt.user.client.rpc.AsyncCallback", packageName);
        addImport(DataTypeInfo.OPERATION_DATA_TYPE, packageName);
        setClassName("PostOperationAsyncExecutor");
        addImplement("AsyncExecutor");
    }
    
    @Override
    protected void writeContent(Appendable appender) throws IOException {
        appender.append("    private AsyncExecutor chainedExecutor;\n"
                + "\n"
                + "    /**\n"
                + "     * @return the chainedExecutor\n"
                + "     */\n"
                + "    public AsyncExecutor getChainedExecutor() {\n"
                + "        return chainedExecutor;\n"
                + "    }\n"
                + "\n"
                + "    @Override\n"
                + "    public Object getExecutorSelector() {\n"
                + "        return chainedExecutor.getExecutorSelector();\n"
                + "    }\n"
                + "\n"
                + "    @Override\n"
                + "    public ").append(OPERATION_BASE_DEFINITION).append(" void execute(final OPERATION operation, final AsyncCallback<RESULT> asyncCallback) {\n"
                + "        AsyncCallback<RESULT> postOperationAsyncCallback = new AsyncCallback<RESULT>() {\n"
                + "\n"
                + "            @Override\n"
                + "            public void onSuccess(RESULT result) {\n"
                + "                result = operation.executePostOperation(result);\n"
                + "                asyncCallback.onSuccess(result);\n"
                + "            }\n"
                + "\n"
                + "            @Override\n"
                + "            public void onFailure(Throwable caught) {\n"
                + "                asyncCallback.onFailure(caught);\n"
                + "            }\n"
                + "        };\n"
                + "        chainedExecutor.execute(operation, postOperationAsyncCallback);\n"
                + "    }\n"
                + "\n"
                + "    @Override\n"
                + "    public ").append(OPERATION_BASE_DEFINITION).append(" void executeOther(OPERATION operation, AsyncCallback<RESULT> asyncCallback) {\n"
                + "        execute(operation, asyncCallback);\n"
                + "    }\n"
                + "\n"
                + "    public PostOperationAsyncExecutor(AsyncExecutor chainedExecutor) {\n"
                + "        if (chainedExecutor == null) {\n"
                + "            throw new IllegalArgumentException(\"chainedExecutor for the PostOperationAsyncExecutor cannot be null\");\n"
                + "        }\n"
                + "        this.chainedExecutor = chainedExecutor;\n"
                + "    }");
    }
    
}
