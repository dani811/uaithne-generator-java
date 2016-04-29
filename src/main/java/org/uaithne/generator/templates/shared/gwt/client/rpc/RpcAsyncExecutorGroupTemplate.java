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
package org.uaithne.generator.templates.shared.gwt.client.rpc;

import java.io.IOException;
import static org.uaithne.generator.commons.DataTypeInfo.*;
import org.uaithne.generator.templates.ClassTemplate;

public class RpcAsyncExecutorGroupTemplate extends ClassTemplate {

    public RpcAsyncExecutorGroupTemplate(String sharedGwtPackageDot) {
        String packageName = sharedGwtPackageDot + "client.rpc";
        setPackageName(packageName);
        addImport(OPERATION_DATA_TYPE, packageName);
        addImport(sharedGwtPackageDot + "client.AsyncExecutorGroup", packageName);
        addImport(sharedGwtPackageDot + "shared.rpc.ExecutorGroupRpcAsync", packageName);
        addImport(sharedGwtPackageDot + "shared.rpc.AwaitGwtOperation", packageName);
        addImport(sharedGwtPackageDot + "shared.rpc.RpcRequest", packageName);
        addImport(sharedGwtPackageDot + "shared.rpc.RpcResponse", packageName);
        addImport(sharedGwtPackageDot + "shared.rpc.RpcException", packageName);
        addImport(ARRAYLIST_DATA_TYPE, packageName);
        addImport("com.google.gwt.core.client.GWT", packageName);
        addImport("com.google.gwt.core.client.GWT.UncaughtExceptionHandler", packageName);
        addImport("com.google.gwt.core.client.Scheduler", packageName);
        addImport("com.google.gwt.user.client.rpc.AsyncCallback", packageName);
        setClassName("RpcAsyncExecutorGroup");
        addImplement("AsyncExecutorGroup");
    }
    
    @Override
    protected void writeContent(Appendable appender) throws IOException {
        appender.append("    private ExecutorGroupRpcAsync executorGroupRpc;\n");
        
        if (getGenerationInfo().isIncludeExecutePostOperationInOperations()) {
            appender.append("    private boolean executePostOperationEnabled = true;\n");
        }
        
        appender.append("    private boolean sendDeferredEnabled = true;\n"
                + "    private ArrayList<Operation> deferredOperations;\n"
                + "    private ArrayList<AsyncCallback> deferredCallbacks;\n"
                + "    private boolean containsExecutableOperations;\n"
                + "\n"
                + "    /**\n"
                + "     * @return the executorGroupRpc\n"
                + "     */\n"
                + "    public ExecutorGroupRpcAsync getExecutorGroupRpc() {\n"
                + "        return executorGroupRpc;\n"
                + "    }\n");
        
        if (getGenerationInfo().isIncludeExecutePostOperationInOperations()) {
            appender.append("\n"
                + "    /**\n"
                + "     * @return the executePostOperationEnabled\n"
                + "     */\n"
                + "    public boolean isExecutePostOperationEnabled() {\n"
                + "        return executePostOperationEnabled;\n"
                + "    }\n"
                + "\n"
                + "    /**\n"
                + "     * @param executePostOperationEnabled the executePostOperationEnabled to set\n"
                + "     */\n"
                + "    public void setExecutePostOperationEnabled(boolean executePostOperationEnabled) {\n"
                + "        this.executePostOperationEnabled = executePostOperationEnabled;\n"
                + "    }\n");
        }
        
        appender.append("\n"
                + "    /**\n"
                + "     * @return the sendDeferredEnabled\n"
                + "     */\n"
                + "    public boolean isSendDeferredEnabled() {\n"
                + "        return sendDeferredEnabled;\n"
                + "    }\n"
                + "\n"
                + "    /**\n"
                + "     * @param sendDeferredEnabled the sendDeferredEnabled to set\n"
                + "     */\n"
                + "    public void setSendDeferredEnabled(boolean sendDeferredEnabled) {\n"
                + "        this.sendDeferredEnabled = sendDeferredEnabled;\n"
                + "    }\n"
                + "\n"
                + "    @Override\n"
                + "    public ").append(OPERATION_BASE_DEFINITION).append(" void execute(OPERATION operation, AsyncCallback<RESULT> asyncCallback) {\n"
                + "        if (sendDeferredEnabled) {\n"
                + "            executeDeferred(operation, asyncCallback);\n"
                + "        } else {\n"
                + "            executeImmediately(operation, asyncCallback);\n"
                + "        }\n"
                + "    }\n"
                + "\n"
                + "    public ").append(OPERATION_BASE_DEFINITION).append(" void executeImmediately(OPERATION operation, AsyncCallback<RESULT> asyncCallback) {\n"
                + "        if (operation == null) {\n"
                + "            throw new IllegalArgumentException(\"The operation cannot be null\");\n"
                + "        }\n"
                + "        if (asyncCallback == null) {\n"
                + "            throw new IllegalArgumentException(\"The asyncCallback cannot be null\");\n"
                + "        }\n"
                + "        if (operation instanceof AwaitGwtOperation) {\n"
                + "            asyncCallback.onSuccess(null);\n"
                + "        }\n"
                + "        if (deferredOperations == null) {\n"
                + "            deferredOperations = new ArrayList<Operation>(1);\n"
                + "        }\n"
                + "        if (deferredCallbacks == null) {\n"
                + "            deferredCallbacks = new ArrayList<AsyncCallback>(1);\n"
                + "        }\n"
                + "\n"
                + "        deferredOperations.add(operation);\n"
                + "        deferredCallbacks.add(asyncCallback);\n"
                + "        containsExecutableOperations = true;\n"
                + "\n"
                + "        flush();\n"
                + "    }\n"
                + "\n"
                + "    public ").append(OPERATION_BASE_DEFINITION).append(" void executeDeferred(OPERATION operation, AsyncCallback<RESULT> asyncCallback) {\n"
                + "        if (operation == null) {\n"
                + "            throw new IllegalArgumentException(\"The operation cannot be null\");\n"
                + "        }\n"
                + "        if (asyncCallback == null) {\n"
                + "            throw new IllegalArgumentException(\"The asyncCallback cannot be null\");\n"
                + "        }\n"
                + "        containsExecutableOperations = containsExecutableOperations || !(operation instanceof AwaitGwtOperation);\n"
                + "        boolean schedule = false;\n"
                + "        if (deferredOperations == null) {\n"
                + "            deferredOperations = new ArrayList<Operation>(1);\n"
                + "            schedule = true;\n"
                + "        }\n"
                + "        if (deferredCallbacks == null) {\n"
                + "            deferredCallbacks = new ArrayList<AsyncCallback>(1);\n"
                + "            schedule = true;\n"
                + "        }\n"
                + "\n"
                + "        deferredOperations.add(operation);\n"
                + "        deferredCallbacks.add(asyncCallback);\n"
                + "\n"
                + "        if (schedule) {\n"
                + "            Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {\n"
                + "\n"
                + "                @Override\n"
                + "                public void execute() {\n"
                + "                    flush();\n"
                + "                }\n"
                + "            });\n"
                + "        }\n"
                + "    }\n"
                + "\n"
                + "    public void flush() {\n"
                + "        ArrayList<Operation> currentDeferredOperations = deferredOperations;\n"
                + "        ArrayList<AsyncCallback> currentDeferredCallbacks = deferredCallbacks;\n"
                + "        boolean currentContainsExecutableOperations = containsExecutableOperations;\n"
                + "\n"
                + "        deferredOperations = null;\n"
                + "        deferredCallbacks = null;\n"
                + "        containsExecutableOperations = false;\n"
                + "\n"
                + "        if (currentDeferredOperations == null || currentDeferredCallbacks == null) {\n"
                + "            return;\n"
                + "        }\n"
                + "\n"
                + "        if (currentContainsExecutableOperations) {\n"
                + "            send(currentDeferredOperations, currentDeferredCallbacks);\n"
                + "        } else {\n"
                + "            for (AsyncCallback callback : currentDeferredCallbacks) {\n"
                + "                callback.onSuccess(null);\n"
                + "            }\n"
                + "        }\n"
                + "    }\n"
                + "\n"
                + "    protected RpcResult createRequest(ArrayList<Operation> operations, ArrayList<AsyncCallback> callbacks) {\n"
                + "        return new RpcResult(new RpcRequest(operations), operations, callbacks);\n"
                + "    }\n"
                + "\n"
                + "    void send(ArrayList<Operation> operations, ArrayList<AsyncCallback> callbacks) {\n"
                + "        final RpcResult rpcResult = createRequest(operations, callbacks);\n"
                + "\n"
                + "        executorGroupRpc.execute(rpcResult.getRequest(), new AsyncCallback<RpcResponse>() {\n"
                + "\n"
                + "            @Override\n"
                + "            public void onFailure(Throwable caught) {\n"
                + "                throw new RuntimeException(\"ExecutorGroupRpc fail\", caught);\n"
                + "            }\n"
                + "\n"
                + "            @Override\n"
                + "            public void onSuccess(RpcResponse response) {\n"
                + "                rpcResult.setResponse(response);\n"
                + "                process(rpcResult);\n"
                + "            }\n"
                + "\n"
                + "        });\n"
                + "    }\n"
                + "\n"
                + "    public void resend(RpcResult rpcResult) {\n"
                + "        send(rpcResult.getOperations(), rpcResult.getCallbacks());\n"
                + "    }\n"
                + "\n"
                + "    public void process(RpcResult rpcResult) {\n"
                + "        ArrayList<Operation> operations = rpcResult.getOperations();\n"
                + "        ArrayList<AsyncCallback> callbacks = rpcResult.getCallbacks();\n"
                + "        RpcResponse response = rpcResult.getResponse();\n"
                + "\n"
                + "        ArrayList<Object> results;\n"
                + "        if (response != null) {\n"
                + "            results = response.getResult();\n"
                + "        } else {\n"
                + "            results = null;\n"
                + "        }\n"
                + "        \n"
                + "        int operationsSize = 0;\n"
                + "        if (operations != null) {\n"
                + "            operationsSize = operations.size();\n"
                + "        }\n"
                + "        \n"
                + "        int resultsSize = 0;\n"
                + "        if (results != null) {\n"
                + "            resultsSize = results.size();\n"
                + "        }\n"
                + "        \n"
                + "        int callbacksSize = 0;\n"
                + "        if (callbacks != null) {\n"
                + "            callbacksSize = callbacks.size();\n"
                + "        }\n"
                + "\n"
                + "        if (operationsSize != resultsSize) {\n"
                + "            throw new IllegalStateException(\"Wrong number of results\");\n"
                + "        }\n"
                + "        if (operationsSize != callbacksSize) {\n"
                + "            throw new IllegalStateException(\"Wrong number of callbacks\");\n"
                + "        }\n"
                + "        if (operationsSize <= 0) {\n"
                + "            return;\n"
                + "        }\n"
                + "\n"
                + "        UncaughtExceptionHandler handler = GWT.getUncaughtExceptionHandler();\n"
                + "        for (int i = 0; i < results.size(); i++) {\n"
                + "            Operation operation = operations.get(i);\n"
                + "            AsyncCallback callback = callbacks.get(i);\n"
                + "            Object result = results.get(i);\n"
                + "\n"
                + "            try {\n"
                + "                process(operation, result, callback);\n"
                + "            } catch(Throwable e) {\n"
                + "                if (handler == null) {\n"
                + "                    GWT.log(\"Uncaught exception escaped\", e);\n"
                + "                } else {\n"
                + "                    handler.onUncaughtException(e);\n"
                + "                }\n"
                + "            }\n"
                + "        }\n"
                + "    }\n"
                + "\n"
                + "    void process(Operation operation, Object result, AsyncCallback callback) {\n"
                + "        if (callback == null) {\n"
                + "            throw new IllegalStateException(\"No callback found for the operation: \" + operation);\n"
                + "        }\n"
                + "        if (result instanceof RpcException) {\n"
                + "            RpcException rpcException = (RpcException) result;\n"
                + "            if (rpcException.getCause() != null) {\n"
                + "                callback.onFailure(rpcException.getCause());\n"
                + "            } else {\n"
                + "                callback.onFailure(rpcException);\n"
                + "            }\n"
                + "        } else {\n");
        
        if (getGenerationInfo().isIncludeExecutePostOperationInOperations()) {
            appender.append(
                  "            if (executePostOperationEnabled) {\n"
                + "                result = operation.executePostOperation(result);\n"
                + "            }\n");
        }
        
        appender.append(
                  "            callback.onSuccess(result);\n"
                + "        }\n"
                + "    }"
                + "\n"
                + "    public RpcAsyncExecutorGroup(ExecutorGroupRpcAsync executorGroupRpc) {\n"
                + "        if (executorGroupRpc == null) {\n"
                + "            throw new IllegalArgumentException(\"executorGroupRpc for the RpcAsyncExecutorGroup cannot be null\");\n"
                + "        }\n"
                + "        this.executorGroupRpc = executorGroupRpc;\n"
                + "    }");
    }
    
}
