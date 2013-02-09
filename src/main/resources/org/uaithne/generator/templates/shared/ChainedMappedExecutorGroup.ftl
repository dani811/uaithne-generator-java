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

public class ChainedMappedExecutorGroup implements ExecutorGroup {

    private HashMap<Object, Executor> executorMap;
    private ExecutorGroup chainedExecutorGroup;

    public Executor getCustomizedExecutor(Operation operation) {
        if (executorMap == null) {
            return null;
        }
        return executorMap.get(operation.getExecutorSelector());
    }

    @Override
    public ${operationBaseDefinition} RESULT execute(OPERATION operation) {
        Executor executor = getCustomizedExecutor(operation);
        if (executor == null) {
            return chainedExecutorGroup.execute(operation);
        } else {
            return executor.execute(operation);
        }
    }

    /**
     * @return the chainedExecutorGroup
     */
    public ExecutorGroup getChainedExecutorGroup() {
        return chainedExecutorGroup;
    }
    
    public Executor addCustomExecutor(Executor executor) {
        if (executor == null) {
            throw new IllegalArgumentException("executor for add to the ChainedMappedExecutorGroup cannot be null");
        }
        if (executorMap == null) {
            executorMap = new HashMap<Object, Executor>();
        }
        return executorMap.put(executor.getExecutorSelector(), executor);
    }
    
    public boolean removeCustomExecutor(Executor executor) {
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

    public ChainedMappedExecutorGroup(ExecutorGroup chainedExecutorGroup) {
        if (chainedExecutorGroup == null) {
            throw new IllegalArgumentException("chainedExecutorGroup for the ChainedMappedExecutorGroup cannot be null");
        }
        this.chainedExecutorGroup = chainedExecutorGroup;
    }
    
}
