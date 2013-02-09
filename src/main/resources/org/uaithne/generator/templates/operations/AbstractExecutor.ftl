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

public abstract class ${className} implements ${executorInterfaceName} {
    
    @Override
    public Object getExecutorSelector() {
        return ${executorInterfaceName}.SELECTOR;
    }
    
    @Override
    public ${operationBaseDefinition} RESULT execute(OPERATION operation) {
        return operation.execute(this);
    }
    
    @Override
    public ${operationBaseDefinition} RESULT executeOther(OPERATION operation) {
        throw new UnsupportedOperationException("Unsuported operation. Operation: " + operation);
    }

    public ${className}() {
    }
    
}
