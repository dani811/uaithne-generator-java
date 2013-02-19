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

import ${generation.sharedGwtPackageDot}shared.rpc.AwaitGwtOperation;
import ${generation.sharedGwtPackageDot}shared.rpc.AwaitResult;
import ${generation.sharedGwtPackageDot}shared.rpc.CombinedGwtOperation;
import ${generation.sharedGwtPackageDot}shared.rpc.GwtOperationExecutor;
import ${generation.sharedGwtPackageDot}shared.rpc.CombinedGwtResult;
import ${generation.sharedGwtPackageDot}shared.rpc.ExecutorGroupRpc;
import ${generation.sharedGwtPackageDot}shared.rpc.RpcRequest;
import ${generation.sharedGwtPackageDot}shared.rpc.RpcResponse;
<#if generation.useResultInterface>
import ${generation.sharedPackageDot}Result;
import ${generation.sharedGwtPackageDot}shared.rpc.ErrorResult;
<#else>
import ${generation.sharedPackageDot}ResultWrapper;
import ${generation.sharedGwtPackageDot}shared.rpc.ErrorResultWrapper;
</#if>
import ${generation.sharedPackageDot}ExecutorGroup;
import ${generation.sharedPackageDot}Operation;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import java.util.ArrayList;

public class ExecutorGroupRpcImpl extends RemoteServiceServlet implements ExecutorGroupRpc {

    class GwtOperationExecutorImpl implements GwtOperationExecutor {

        @Override
        public <RESULT extends CombinedGwtResult, OPERATION extends CombinedGwtOperation<RESULT>> RESULT executeCombinedGwtOperation(OPERATION operation) {
            return operation.executeOnServer(this);
        }

        @Override
        public AwaitResult executeAwaitGwtOperation(AwaitGwtOperation operation) {
            return null;
        }

        @Override
        public Object getExecutorSelector() {
            return GwtOperationExecutor.SELECTOR;
        }

        @Override
        public ${operationBaseDefinition} RESULT execute(OPERATION operation) {
            return ExecutorGroupRpcImpl.this.runOperation(operation);
        }

        @Override
        public ${operationBaseDefinition} RESULT executeOther(OPERATION operation) {
            return execute(operation);
        }

    }

    private GwtOperationExecutorImpl gwtOperationExecutorImpl;

    private ExecutorGroup chainedExecutorGroup;

    /**
     * @return the chainedExecutorGroup
     */
    public ExecutorGroup getChainedExecutorGroup() {
        return chainedExecutorGroup;
    }

    @Override
    public RpcResponse execute(RpcRequest request) {
        if (request == null) {
            return null;
        }
        ArrayList<<#if generation.useResultInterface>Result<#else>ResultWrapper</#if>> result = execute(request.getOperations());
        return new RpcResponse(result);
    }

    protected ArrayList<<#if generation.useResultInterface>Result<#else>ResultWrapper</#if>> execute(ArrayList<Operation> operations) {
        if (operations == null) {
            return null;
        }
        ArrayList<<#if generation.useResultInterface>Result<#else>ResultWrapper</#if>> result = new ArrayList<<#if generation.useResultInterface>Result<#else>ResultWrapper</#if>>(operations.size());

        for (int i = 0; i < operations.size(); i++) {
            result.add(executeOperation(operations.get(i)));
        }

        return result;
    }

    <#if generation.useResultInterface>Result<#else>ResultWrapper</#if> executeOperation(Operation o) {
        try {
            <#if generation.useResultInterface>
            return runOperation(o);
            <#else>
            Object result = runOperation(o);
            return o.wrapResult(result);
            </#if>
        } catch (Throwable e) {
            <#if rpcErrorAsString>
            String error = transformError(e);
            <#if generation.useResultInterface>
            return new ErrorResult(error);
            <#else>
            return new ErrorResultWrapper(error);
            </#if>
            <#else>
            <#if generation.useResultInterface>
            return new ErrorResult(e);
            <#else>
            return new ErrorResultWrapper(e);
            </#if>
            </#if>
        }
    }

    <#if rpcErrorAsString>
    protected String transformError(Throwable e) {
        return e.getClass().getName() + ": " + e.getMessage();
    }
    </#if>

    ${operationBaseDefinition} RESULT runOperation(OPERATION o) {
        if (o.getExecutorSelector() == GwtOperationExecutor.SELECTOR) {
            return o.execute(gwtOperationExecutorImpl);
        } else {
            return chainedExecutorGroup.execute(o);
        }
    }

    public ExecutorGroupRpcImpl(ExecutorGroup chainedExecutorGroup) {
        if (chainedExecutorGroup == null) {
            throw new IllegalArgumentException("chainedExecutorGroup for the ExecutorGroupRpcImpl cannot be null");
        }
        this.chainedExecutorGroup = chainedExecutorGroup;
        this.gwtOperationExecutorImpl = new GwtOperationExecutorImpl();
    }

    ExecutorGroupRpcImpl(ExecutorGroup chainedExecutorGroup, GwtOperationExecutorImpl gwtOperationExecutorImpl) {
        if (chainedExecutorGroup == null) {
            throw new IllegalArgumentException("chainedExecutorGroup for the ExecutorGroupRpcImpl cannot be null");
        }
        if (gwtOperationExecutorImpl == null) {
            throw new IllegalArgumentException("gwtOperationExecutorImpl for the ExecutorGroupRpcImpl cannot be null");
        }
        this.gwtOperationExecutorImpl = gwtOperationExecutorImpl;
    }

}
