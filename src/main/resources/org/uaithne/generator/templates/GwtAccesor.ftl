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

import com.google.gwt.user.client.rpc.AsyncCallback;
<#list imports as import>
import ${import};
</#list>

public class ${className} implements AsyncExecutorGroup {

    private AsyncExecutorGroup chainedExecutorGroup;

    @Override
    public ${operationBaseDefinition}
        void execute(final OPERATION operation, final AsyncCallback<RESULT> asyncCallback) {
        chainedExecutorGroup.execute(operation, asyncCallback);
    }

    public <RESULT extends CombinedGwtResult> void execute(CombinedGwtOperation<RESULT> operation,  AsyncCallback<RESULT> asyncCallback) {
        chainedExecutorGroup.execute(operation, asyncCallback);
    }

    public void execute(AwaitGwtOperation operation,  AsyncCallback<AwaitResult> asyncCallback) {
        chainedExecutorGroup.execute(operation, asyncCallback);
    }

    <#list operations as operation>
    public void execute(${operation.dataType.qualifiedName} operation, AsyncCallback<${operation.returnDataType.simpleName}> asyncCallback) {
        chainedExecutorGroup.execute(operation, asyncCallback);
    }
    </#list>

    public AsyncExecutorGroup getChainedExecutorGroup() {
        return chainedExecutorGroup;
    }

    public ${className}(AsyncExecutorGroup chainedExecutorGroup) {
        if (chainedExecutorGroup == null) {
            throw new IllegalArgumentException("chainedExecutorGroup for the ${className} cannot be null");
        }
        this.chainedExecutorGroup = chainedExecutorGroup;
    }
}