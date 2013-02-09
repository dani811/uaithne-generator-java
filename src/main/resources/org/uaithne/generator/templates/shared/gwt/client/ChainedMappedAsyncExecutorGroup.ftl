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
import java.util.HashMap;

public class ChainedMappedAsyncExecutorGroup implements AsyncExecutorGroup {

    private HashMap<Object, AsyncExecutor> executorMap;
    private AsyncExecutorGroup chainedExecutorGroup;

    public AsyncExecutor getCustomizedExecutor(Operation operation) {
        if (executorMap == null) {
            return null;
        }
        return executorMap.get(operation.getExecutorSelector());
    }

    @Override
    public ${operationBaseDefinition} void execute(OPERATION operation, AsyncCallback<RESULT> asyncCallback) {
        AsyncExecutor executor = getCustomizedExecutor(operation);
        if (executor == null) {
            chainedExecutorGroup.execute(operation, asyncCallback);
        } else {
            executor.execute(operation, asyncCallback);
        }
    }

    /**
     * @return the chainedExecutorGroup
     */
    public AsyncExecutorGroup getChainedExecutorGroup() {
        return chainedExecutorGroup;
    }
    
    public AsyncExecutor addCustomExecutor(AsyncExecutor executor) {
        if (executor == null) {
            throw new IllegalArgumentException("executor for add to the ChainedMappedAsyncExecutorGroup cannot be null");
        }
        if (executorMap == null) {
            executorMap = new HashMap<Object, AsyncExecutor>();
        }
        return executorMap.put(executor.getExecutorSelector(), executor);
    }
    
    public boolean removeCustomExecutor(AsyncExecutor executor) {
        if (executor == null) {
            return false;
        }
        if (executorMap == null) {
            return false;
        }
        if (executorMap.containsValue(executor)) {
            executorMap.remove(executor.getExecutorSelector());
            return true;
        } else {
            return false;
        }
    }

    public ChainedMappedAsyncExecutorGroup(AsyncExecutorGroup chainedExecutorGroup) {
        if (chainedExecutorGroup == null) {
            throw new IllegalArgumentException("chainedExecutorGroup for the ChainedMappedAsyncExecutorGroup cannot be null");
        }
        this.chainedExecutorGroup = chainedExecutorGroup;
    }
    
}
