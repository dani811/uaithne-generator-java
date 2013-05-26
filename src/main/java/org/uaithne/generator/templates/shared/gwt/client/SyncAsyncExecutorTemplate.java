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
import org.uaithne.generator.templates.ClassTemplate;

public class SyncAsyncExecutorTemplate extends ClassTemplate {

    public SyncAsyncExecutorTemplate(String sharedGwtPackageDot, String sharedPackageDot) {
        String packageName = sharedGwtPackageDot + "client";
        setPackageName(packageName);
        addImport("com.google.gwt.user.client.rpc.AsyncCallback", sharedPackageDot);
        addImport(sharedPackageDot + "Operation", packageName);
        addImport(sharedPackageDot + "Executor", packageName);
        setClassName("SyncAsyncExecutor");
        addImplement("AsyncExecutor");
    }
    
    @Override
    protected void writeContent(Appendable appender) throws IOException {
        appender.append("    private Executor chainedExecutor;\n"
                + "\n"
                + "    /**\n"
                + "     * @return the chainedExecutor\n"
                + "     */\n"
                + "    public Executor getChainedExecutor() {\n"
                + "        return chainedExecutor;\n"
                + "    }\n"
                + "\n"
                + "    @Override\n"
                + "    public Object getExecutorSelector() {\n"
                + "        return chainedExecutor.getExecutorSelector();\n"
                + "    }\n"
                + "\n"
                + "    @Override\n"
                + "    public ").append(OPERATION_BASE_DEFINITION).append(" void execute(OPERATION operation, AsyncCallback<RESULT> asyncCallback) {\n"
                + "        try {\n"
                + "            RESULT result = chainedExecutor.execute(operation);\n"
                + "            asyncCallback.onSuccess(result);\n"
                + "        } catch (Throwable caught) {\n"
                + "            asyncCallback.onFailure(caught);\n"
                + "        }\n"
                + "    }\n"
                + "\n"
                + "    @Override\n"
                + "    public ").append(OPERATION_BASE_DEFINITION).append(" void executeOther(OPERATION operation, AsyncCallback<RESULT> asyncCallback) {\n"
                + "        execute(operation, asyncCallback);\n"
                + "    }\n"
                + "\n"
                + "    public SyncAsyncExecutor(Executor chainedExecutor) {\n"
                + "        if (chainedExecutor == null) {\n"
                + "            throw new IllegalArgumentException(\"chainedExecutor for the SyncAsyncExecutor cannot be null\");\n"
                + "        }\n"
                + "        this.chainedExecutor = chainedExecutor;\n"
                + "    }");
    }
    
}
