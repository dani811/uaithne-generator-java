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

public class PostOperationExecutor implements Executor {
    
    private Executor chainedExecutor;

    /**
     * @return the chainedExecutor
     */
    public Executor getChainedExecutor() {
        return chainedExecutor;
    }

    @Override
    public Object getExecutorSelector() {
        return chainedExecutor.getExecutorSelector();
    }

    @Override
    public ${operationBaseDefinition} RESULT execute(OPERATION operation) {
        RESULT result = chainedExecutor.execute(operation);
        return operation.executePostOperation(result);
    }

    @Override
    public ${operationBaseDefinition} RESULT executeOther(OPERATION operation) {
        return execute(operation);
    }

    public PostOperationExecutor(Executor chainedExecutor) {
        if (chainedExecutor == null) {
            throw new IllegalArgumentException("chainedExecutor for the PostOperationExecutor cannot be null");
        }
        this.chainedExecutor = chainedExecutor;
    }
    
}
