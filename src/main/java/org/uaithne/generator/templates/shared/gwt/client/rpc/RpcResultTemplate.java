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

public class RpcResultTemplate extends ClassTemplate {

    public RpcResultTemplate(String sharedGwtPackageDot, String sharedPackageDot) {
        String packageName = sharedGwtPackageDot + "client.rpc";
        setPackageName(packageName);
        addImport(ARRAYLIST_DATA_TYPE, packageName);
        addImport(sharedPackageDot + "Operation", packageName);
        addImport(sharedGwtPackageDot + "shared.rpc.RpcRequest", packageName);
        addImport(sharedGwtPackageDot + "shared.rpc.RpcResponse", packageName);
        addImport("com.google.gwt.user.client.rpc.AsyncCallback", packageName);
        setClassName("RpcResult");
    }
    
    @Override
    protected void writeContent(Appendable appender) throws IOException {
        appender.append("    private RpcRequest request;\n"
                + "    private ArrayList<Operation> operations;\n"
                + "    private ArrayList<AsyncCallback> callbacks;\n"
                + "    private RpcResponse response;\n"
                + "\n"
                + "    public RpcRequest getRequest() {\n"
                + "        return request;\n"
                + "    }\n"
                + "\n"
                + "    public void setRequest(RpcRequest request) {\n"
                + "        this.request = request;\n"
                + "    }\n"
                + "\n"
                + "    public ArrayList<Operation> getOperations() {\n"
                + "        return operations;\n"
                + "    }\n"
                + "\n"
                + "    public void setOperations(ArrayList<Operation> operations) {\n"
                + "        this.operations = operations;\n"
                + "    }\n"
                + "\n"
                + "    public ArrayList<AsyncCallback> getCallbacks() {\n"
                + "        return callbacks;\n"
                + "    }\n"
                + "\n"
                + "    public void setCallbacks(ArrayList<AsyncCallback> callbacks) {\n"
                + "        this.callbacks = callbacks;\n"
                + "    }\n"
                + "\n"
                + "    public RpcResponse getResponse() {\n"
                + "        return response;\n"
                + "    }\n"
                + "\n"
                + "    public void setResponse(RpcResponse response) {\n"
                + "        this.response = response;\n"
                + "    }\n"
                + "\n"
                + "    public RpcResult() {\n"
                + "    }\n"
                + "\n"
                + "    public RpcResult(RpcRequest request, ArrayList<Operation> operations, ArrayList<AsyncCallback> callbacks) {\n"
                + "        this.request = request;\n"
                + "        this.operations = operations;\n"
                + "        this.callbacks = callbacks;\n"
                + "    }\n"
                + "\n"
                + "    public void resend() {\n"
                + "    }");
    }
    
}
