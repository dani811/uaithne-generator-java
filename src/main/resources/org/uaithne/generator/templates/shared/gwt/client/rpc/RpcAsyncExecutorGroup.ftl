<#--
Copyright 2012 and beyond, Juan Luis Paz

This file is part of Uaithne.

Uaithne is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Uaithne is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with Uaithne. If not, see <http://www.gnu.org/licenses/>.
-->
package ${packageName};


<#if generation.useResultInterface>
import ${generation.sharedPackageDot}Result;
import ${generation.sharedGwtPackageDot}shared.rpc.ErrorResult;
<#else>
import ${generation.sharedPackageDot}ResultWrapper;
import ${generation.sharedGwtPackageDot}shared.rpc.ErrorResultWrapper;
</#if>
import ${generation.sharedPackageDot}Operation;
import ${generation.sharedGwtPackageDot}client.AsyncExecutorGroup;
import ${generation.sharedGwtPackageDot}shared.rpc.ExecutorGroupRpc;
import ${generation.sharedGwtPackageDot}shared.rpc.ExecutorGroupRpcAsync;
import ${generation.sharedGwtPackageDot}shared.rpc.RpcRequest;
import ${generation.sharedGwtPackageDot}shared.rpc.RpcResponse;
<#if rpcErrorAsString>
import ${generation.sharedGwtPackageDot}shared.rpc.RpcException;
</#if>
import java.util.ArrayList;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.GWT.UncaughtExceptionHandler;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class RpcAsyncExecutorGroup implements AsyncExecutorGroup {

    private ExecutorGroupRpcAsync executorGroupRpc;
    private boolean executePostOperationEnabled = true;
    private boolean sendDeferredEnabled = true;
    private ArrayList<Operation> deferredOperations;
    private ArrayList<AsyncCallback> deferredCallbacks;

    /**
     * @return the executorGroupRpc
     */
    public ExecutorGroupRpcAsync getExecutorGroupRpc() {
        return executorGroupRpc;
    }

    /**
     * @return the executePostOperationEnabled
     */
    public boolean isExecutePostOperationEnabled() {
        return executePostOperationEnabled;
    }

    /**
     * @param executePostOperationEnabled the executePostOperationEnabled to set
     */
    public void setExecutePostOperationEnabled(boolean executePostOperationEnabled) {
        this.executePostOperationEnabled = executePostOperationEnabled;
    }

    /**
     * @return the sendDeferredEnabled
     */
    public boolean isSendDeferredEnabled() {
        return sendDeferredEnabled;
    }

    /**
     * @param sendDeferredEnabled the sendDeferredEnabled to set
     */
    public void setSendDeferredEnabled(boolean sendDeferredEnabled) {
        this.sendDeferredEnabled = sendDeferredEnabled;
    }

    @Override
    public ${operationBaseDefinition} void execute(OPERATION operation, AsyncCallback<RESULT> asyncCallback) {
        if (sendDeferredEnabled) {
            executeDeferred(operation, asyncCallback);
        } else {
            executeImmediately(operation, asyncCallback);
        }
    }

    public ${operationBaseDefinition} void executeImmediately(OPERATION operation, AsyncCallback<RESULT> asyncCallback) {
        if (operation == null) {
            throw new IllegalArgumentException("The operation cannot be null");
        }
        if (asyncCallback == null) {
            throw new IllegalArgumentException("The asyncCallback cannot be null");
        }
        if (deferredOperations == null) {
            deferredOperations = new ArrayList<Operation>(1);
        }
        if (deferredCallbacks == null) {
            deferredCallbacks = new ArrayList<AsyncCallback>(1);
        }

        deferredOperations.add(operation);
        deferredCallbacks.add(asyncCallback);

        flush();
    }

    public ${operationBaseDefinition} void executeDeferred(OPERATION operation, AsyncCallback<RESULT> asyncCallback) {
        if (operation == null) {
            throw new IllegalArgumentException("The operation cannot be null");
        }
        if (asyncCallback == null) {
            throw new IllegalArgumentException("The asyncCallback cannot be null");
        }
        boolean schedule = false;
        if (deferredOperations == null) {
            deferredOperations = new ArrayList<Operation>(1);
            schedule = true;
        }
        if (deferredCallbacks == null) {
            deferredCallbacks = new ArrayList<AsyncCallback>(1);
            schedule = true;
        }

        deferredOperations.add(operation);
        deferredCallbacks.add(asyncCallback);

        if (schedule) {
            Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {

                @Override
                public void execute() {
                    flush();
                }
            });
        }
    }

    public void flush() {
        if (deferredOperations == null || deferredCallbacks == null) {
            return;
        }

        send(deferredOperations, deferredCallbacks);

        deferredOperations = null;
        deferredCallbacks = null;
    }

    protected RpcResult createRequest(ArrayList<Operation> operations, ArrayList<AsyncCallback> callbacks) {
        return new RpcResult(new RpcRequest(operations), operations, callbacks);
    }

    void send(ArrayList<Operation> operations, ArrayList<AsyncCallback> callbacks) {
        final RpcResult rpcResult = createRequest(operations, callbacks);

        executorGroupRpc.execute(rpcResult.getRequest(), new AsyncCallback<RpcResponse>() {

            @Override
            public void onFailure(Throwable caught) {
                throw new RuntimeException("ExecutorGroupRpc fail", caught);
            }

            @Override
            public void onSuccess(RpcResponse response) {
                rpcResult.setResponse(response);
                process(rpcResult);
            }

        });
    }

    public void resend(RpcResult rpcResult) {
        send(rpcResult.getOperations(), rpcResult.getCallbacks());
    }

    public void process(RpcResult rpcResult) {
        ArrayList<Operation> operations = rpcResult.getOperations();
        ArrayList<AsyncCallback> callbacks = rpcResult.getCallbacks();
        RpcResponse response = rpcResult.getResponse();

        ArrayList<<#if generation.useResultInterface>Result<#else>ResultWrapper</#if>> result;
        if (response != null) {
            result = response.getResult();
        } else {
            result = null;
        }

        boolean hasOperations = operations != null && !operations.isEmpty();
        boolean hasResults = result != null && !result.isEmpty();
        boolean hasCallbacks = callbacks != null && !callbacks.isEmpty();

        if (hasOperations) {
            if (!hasResults) {
                throw new IllegalStateException("Wrong number of results");
            }
            if (!hasCallbacks) {
                throw new IllegalStateException("Wrong number of callbacks");
            }
        } else {
            if (hasResults) {
                throw new IllegalStateException("Wrong number of results");
            }
            if (hasCallbacks) {
                throw new IllegalStateException("Wrong number of callbacks");
            }
            return;
        }

        if (operations.size() != result.size()) {
            throw new IllegalStateException("Wrong number of results");
        }
        if (operations.size() != callbacks.size()) {
            throw new IllegalStateException("Wrong number of callbacks");
        }

        UncaughtExceptionHandler handler = GWT.getUncaughtExceptionHandler();
        for (int i = 0; i < result.size(); i++) {
            Operation operation = operations.get(i);
            AsyncCallback callback = callbacks.get(i);
            <#if generation.useResultInterface>Result<#else>ResultWrapper</#if> r = result.get(i);

            try {
                process(operation, r, callback);
            } catch(Throwable e) {
                if (handler == null) {
                    GWT.log("Uncaught exception escaped", e);
                } else {
                    handler.onUncaughtException(e);
                }
            }
        }
    }

    void process(Operation operation, <#if generation.useResultInterface>Result<#else>ResultWrapper</#if> r, AsyncCallback callback) {
        if (callback == null) {
            throw new IllegalStateException("Invalid result (null ResultWrapper is not allowed) for the operation: " + operation);
        }
        if (callback == null) {
            throw new IllegalStateException("No callback found for the operation: " + operation);
        }
        <#if generation.useResultInterface>
        if (r instanceof ErrorResult) {
            ErrorResult er = (ErrorResult) r;
            <#if rpcErrorAsString>
            callback.onFailure(new RpcException(er.getError()));
            <#else>
            callback.onFailure(er.getError());
            </#if>
        } else {
            if (executePostOperationEnabled) {
                r = operation.executePostOperation(r);
            }
            callback.onSuccess(r);
        }
        <#else>
        if (r instanceof ErrorResultWrapper) {
            <#if rpcErrorAsString>
            callback.onFailure(new RpcException((String)r.getValue()));
            <#else>
            callback.onFailure((Throwable)r.getValue());
            </#if>
        } else {
            Object v = r.getValue();
            if (executePostOperationEnabled) {
                v = operation.executePostOperation(v);
            }
            callback.onSuccess(v);
        }
        </#if>
    }

    public RpcAsyncExecutorGroup() {
        this(GWT.<ExecutorGroupRpcAsync>create(ExecutorGroupRpc.class));
    }

    public RpcAsyncExecutorGroup(ExecutorGroupRpcAsync executorGroupRpc) {
        if (executorGroupRpc == null) {
            throw new IllegalArgumentException("executorGroupRpc for the RpcAsyncExecutorGroup cannot be null");
        }
        this.executorGroupRpc = executorGroupRpc;
    }
}
