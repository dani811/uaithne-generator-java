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

<#list imports as import>
import ${import};
</#list>

public class ChainedGroupingAsyncExecutor implements AsyncExecutor {
    
    private Object executorSelector;
    private AsyncExecutorGroup chainedExecutorGroup;

    /**
     * @return the chainedExecutorGroup
     */
    public AsyncExecutorGroup getChainedExecutorGroup() {
        return chainedExecutorGroup;
    }

    @Override
    public Object getExecutorSelector() {
        return executorSelector;
    }

    @Override
    public ${operationBaseDefinition} void execute(OPERATION operation, AsyncCallback<RESULT> asyncCallback) {
        chainedExecutorGroup.execute(operation, asyncCallback);
    }

    @Override
    public ${operationBaseDefinition} void executeOther(OPERATION operation, AsyncCallback<RESULT> asyncCallback) {
        execute(operation, asyncCallback);
    }

    public ChainedGroupingAsyncExecutor(AsyncExecutorGroup chainedExecutorGroup, Object executorSelector) {
        if (chainedExecutorGroup == null) {
            throw new IllegalArgumentException("chainedExecutorGroup for the ChainedGroupingAsyncExecutor cannot be null");
        }
        if (executorSelector == null) {
            throw new IllegalArgumentException("executorSelector for the ChainedGroupingAsyncExecutor cannot be null");
        }
        this.chainedExecutorGroup = chainedExecutorGroup;
        this.executorSelector = executorSelector;
    }

}
