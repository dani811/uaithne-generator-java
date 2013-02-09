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

import java.util.ArrayList;
import ${generation.sharedPackageDot}Operation;
import ${generation.sharedGwtPackageDot}shared.rpc.RpcRequest;
import ${generation.sharedGwtPackageDot}shared.rpc.RpcResponse;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class RpcResult {

    private RpcRequest request;
    private ArrayList<Operation> operations;
    private ArrayList<AsyncCallback> callbacks;
    private RpcResponse response;

    public RpcRequest getRequest() {
        return request;
    }

    public void setRequest(RpcRequest request) {
        this.request = request;
    }

    public ArrayList<Operation> getOperations() {
        return operations;
    }

    public void setOperations(ArrayList<Operation> operations) {
        this.operations = operations;
    }

    public ArrayList<AsyncCallback> getCallbacks() {
        return callbacks;
    }

    public void setCallbacks(ArrayList<AsyncCallback> callbacks) {
        this.callbacks = callbacks;
    }

    public RpcResponse getResponse() {
        return response;
    }

    public void setResponse(RpcResponse response) {
        this.response = response;
    }

    public RpcResult() {
    }

    public RpcResult(RpcRequest request, ArrayList<Operation> operations, ArrayList<AsyncCallback> callbacks) {
        this.request = request;
        this.operations = operations;
        this.callbacks = callbacks;
    }

    public void resend() {
    }

}
