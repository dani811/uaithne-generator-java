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

public class MappedAsyncExecutorGroup implements AsyncExecutorGroup {

    private HashMap<Object, AsyncExecutor> executorMap = new HashMap<Object, AsyncExecutor>();

    public AsyncExecutor addExecutor(AsyncExecutor executor) {
        if (executor == null) {
            throw new IllegalArgumentException("executor for add to the MappedAsyncExecutorGroup cannot be null");
        }
        return executorMap.put(executor.getExecutorSelector(), executor);
    }

    public boolean removeExecutor(AsyncExecutor executor) {
        if (executor == null) {
            return false;
        }
        if (executorMap.containsValue(executor)) {
            executorMap.remove(executor.getExecutorSelector());
            return true;
        } else {
            return false;
        }
    }

    public AsyncExecutor getExecutor(Operation operation) {
        return executorMap.get(operation.getExecutorSelector());
    }

    @Override
    public ${operationBaseDefinition} void execute(OPERATION operation, AsyncCallback<RESULT> asyncCallback) {
        AsyncExecutor executor = getExecutor(operation);
        if (executor == null) {
            throw new IllegalStateException("Unable to find a executor for the operation: " +
                    "'" + operation.getClass().getName() + "', expected executor selector: " +
                    "'" + operation.getExecutorSelector() + "'. " +
                    "Operation: " + operation.toString());
        }
        executor.execute(operation, asyncCallback);
    }

    public MappedAsyncExecutorGroup() {
    }

}
