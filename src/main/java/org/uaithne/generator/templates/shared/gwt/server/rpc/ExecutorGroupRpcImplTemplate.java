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
package org.uaithne.generator.templates.shared.gwt.server.rpc;

import java.io.IOException;
import static org.uaithne.generator.commons.DataTypeInfo.*;
import org.uaithne.generator.templates.ClassTemplate;

public class ExecutorGroupRpcImplTemplate extends ClassTemplate {

    public ExecutorGroupRpcImplTemplate(String sharedGwtPackageDot) {
        String packageName = sharedGwtPackageDot + "server.rpc";
        setPackageName(packageName);
        addImport(sharedGwtPackageDot + "shared.rpc.AwaitGwtOperation", packageName);
        addImport(sharedGwtPackageDot + "shared.rpc.AwaitGwtResult", packageName);
        addImport(sharedGwtPackageDot + "shared.rpc.CombinedGwtOperation", packageName);
        addImport(sharedGwtPackageDot + "shared.rpc.GwtOperationExecutor", packageName);
        addImport(sharedGwtPackageDot + "shared.rpc.CombinedGwtResult", packageName);
        addImport(sharedGwtPackageDot + "shared.rpc.ExecutorGroupRpc", packageName);
        addImport(sharedGwtPackageDot + "shared.rpc.RpcRequest", packageName);
        addImport(sharedGwtPackageDot + "shared.rpc.RpcResponse", packageName);
        addImport(sharedGwtPackageDot + "shared.rpc.RpcException", packageName);
        addImport(EXECUTOR_GROUP_DATA_TYPE, packageName);
        addImport(OPERATION_DATA_TYPE, packageName);
        addImport("com.google.gwt.user.server.rpc.RemoteServiceServlet", packageName);
        addImport(ARRAYLIST_DATA_TYPE, packageName);
        setClassName("ExecutorGroupRpcImpl");
        setExtend("RemoteServiceServlet");
        addImplement("ExecutorGroupRpc");
        setAbstract(true);
    }
    
    @Override
    protected void writeContent(Appendable appender) throws IOException {
        appender.append("    class GwtOperationExecutorImpl implements GwtOperationExecutor {\n"
                + "\n"
                + "        @Override\n"
                + "        public <RESULT extends CombinedGwtResult, OPERATION extends CombinedGwtOperation<RESULT>> RESULT executeCombinedGwtOperation(OPERATION operation) {\n"
                + "            return operation.executeOnServer(this);\n"
                + "        }\n"
                + "\n"
                + "        @Override\n"
                + "        public AwaitGwtResult executeAwaitGwtOperation(AwaitGwtOperation operation) {\n"
                + "            return null;\n"
                + "        }\n"
                + "\n"
                + "        @Override\n"
                + "        public Object getExecutorSelector() {\n"
                + "            return GwtOperationExecutor.SELECTOR;\n"
                + "        }\n"
                + "\n"
                + "        @Override\n"
                + "        public ").append(OPERATION_BASE_DEFINITION).append(" RESULT execute(OPERATION operation) {\n"
                + "            return ExecutorGroupRpcImpl.this.runOperation(operation);\n"
                + "        }\n"
                + "\n"
                + "        @Override\n"
                + "        public ").append(OPERATION_BASE_DEFINITION).append(" RESULT executeOther(OPERATION operation) {\n"
                + "            return execute(operation);\n"
                + "        }\n"
                + "\n"
                + "    }\n"
                + "\n"
                + "    private GwtOperationExecutorImpl gwtOperationExecutorImpl;\n"
                + "\n"
                + "    private ExecutorGroup chainedExecutorGroup;\n"
                + "\n"
                + "    /**\n"
                + "     * @return the chainedExecutorGroup\n"
                + "     */\n"
                + "    public ExecutorGroup getChainedExecutorGroup() {\n"
                + "        return chainedExecutorGroup;\n"
                + "    }\n"
                + "\n"
                + "    @Override\n"
                + "    public RpcResponse execute(RpcRequest request) {\n"
                + "        if (request == null) {\n"
                + "            return null;\n"
                + "        }\n"
                + "        ArrayList<Object> result = execute(request.getOperations());\n"
                + "        return new RpcResponse(result);\n"
                + "    }\n"
                + "\n"
                + "    protected ArrayList<Object> execute(ArrayList<Operation> operations) {\n"
                + "        if (operations == null) {\n"
                + "            return null;\n"
                + "        }\n"
                + "        ArrayList<Object> result = new ArrayList<Object>(operations.size());\n"
                + "\n"
                + "        for (int i = 0; i < operations.size(); i++) {\n"
                + "            result.add(executeOperation(operations.get(i)));\n"
                + "        }\n"
                + "\n"
                + "        return result;\n"
                + "    }\n"
                + "\n"
                + "    Object executeOperation(Operation o) {\n"
                + "        try {\n"
                + "            return runOperation(o);\n"
                + "        } catch (Throwable e) {\n"
                + "            return wrapOperationEception(e);\n"
                + "        }\n"
                + "    }\n"
                + "\n"
                + "    protected RpcException wrapOperationEception(Throwable e) {\n"
                + "        return new RpcException(e);\n"
                + "    }\n"
                + "\n"
                + "    ").append(OPERATION_BASE_DEFINITION).append(" RESULT runOperation(OPERATION o) {\n"
                + "        if (o.getExecutorSelector() == GwtOperationExecutor.SELECTOR) {\n"
                + "            return o.execute(gwtOperationExecutorImpl);\n"
                + "        } else {\n"
                + "            return chainedExecutorGroup.execute(o);\n"
                + "        }\n"
                + "    }\n"
                + "\n"
                + "    public ExecutorGroupRpcImpl(ExecutorGroup chainedExecutorGroup) {\n"
                + "        if (chainedExecutorGroup == null) {\n"
                + "            throw new IllegalArgumentException(\"chainedExecutorGroup for the ExecutorGroupRpcImpl cannot be null\");\n"
                + "        }\n"
                + "        this.chainedExecutorGroup = chainedExecutorGroup;\n"
                + "        this.gwtOperationExecutorImpl = new GwtOperationExecutorImpl();\n"
                + "    }\n"
                + "\n"
                + "    ExecutorGroupRpcImpl(ExecutorGroup chainedExecutorGroup, GwtOperationExecutorImpl gwtOperationExecutorImpl) {\n"
                + "        if (chainedExecutorGroup == null) {\n"
                + "            throw new IllegalArgumentException(\"chainedExecutorGroup for the ExecutorGroupRpcImpl cannot be null\");\n"
                + "        }\n"
                + "        if (gwtOperationExecutorImpl == null) {\n"
                + "            throw new IllegalArgumentException(\"gwtOperationExecutorImpl for the ExecutorGroupRpcImpl cannot be null\");\n"
                + "        }\n"
                + "        this.gwtOperationExecutorImpl = gwtOperationExecutorImpl;\n"
                + "    }");
    }
    
}
