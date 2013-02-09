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

import java.util.HashMap;

public class MappedExecutorGroup implements ExecutorGroup {
    
    private HashMap<Object, Executor> executorMap = new HashMap<Object, Executor>();
    
    public Executor addExecutor(Executor executor) {
        if (executor == null) {
            throw new IllegalArgumentException("executor for add to the MappedExecutorGroup cannot be null");
        }
        return executorMap.put(executor.getExecutorSelector(), executor);
    }
    
    public boolean removeExecutor(Executor executor) {
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
    
    public Executor getExecutor(Operation operation) {
        return executorMap.get(operation.getExecutorSelector());
    }

    @Override
    public ${operationBaseDefinition} RESULT execute(OPERATION operation) {
        Executor executor = getExecutor(operation);
        if (executor == null) {
            throw new IllegalStateException("Unable to find a executor for the operation: " +
                    "'" + operation.getClass().getName() + "', expected executor selector: " +
                    "'" + operation.getExecutorSelector() + "'. " +
                    "Operation: " + operation.toString());
        }
        return executor.execute(operation);
    }

    public MappedExecutorGroup() {
    }

}
