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

public class PostOperationExecutorGroup implements ExecutorGroup {
    
    private ExecutorGroup chainedExecutorGroup;

    /**
     * @return the chainedExecutorGroup
     */
    public ExecutorGroup getChainedExecutorGroup() {
        return chainedExecutorGroup;
    }

    @Override
    public ${operationBaseDefinition} RESULT execute(OPERATION operation) {
        RESULT result = chainedExecutorGroup.execute(operation);
        return operation.executePostOperation(result);
    }

    public PostOperationExecutorGroup(ExecutorGroup chainedExecutorGroup) {
        if (chainedExecutorGroup == null) {
            throw new IllegalArgumentException("chainedExecutorGroup for the PostOperationExecutorGroup cannot be null");
        }
        this.chainedExecutorGroup = chainedExecutorGroup;
    }
    
}
