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

public class ${className} implements ${plainExecutorName} {

    private ${executorInterfaceName} chainedExecutor;

    public ${executorInterfaceName} getChainedExecutor() {
        return chainedExecutor;
    }
    <#list operations as operation>

    <#if operation.operationKind.id != 3>
    @Override
    public ${operation.returnDataType.simpleName} ${operation.methodName}(<#list operation.mandatoryFields as field>${field.dataType.simpleName} ${field.name}<#if field_has_next>, </#if></#list>) {
        ${operation.dataType.simpleName} op = new ${operation.dataType.simpleName}(<#list operation.mandatoryFields as field>${field.name}<#if field_has_next>, </#if></#list>);
        return chainedExecutor.${operation.methodName}(op);
    }
    <#if operation.optionalFields?size &gt; 0>

    @Override
    public ${operation.returnDataType.simpleName} ${operation.methodName}WithOptionals(<#list operation.fields as field>${field.dataType.simpleName} ${field.name}<#if field_has_next>, </#if></#list>) {
        ${operation.dataType.simpleName} op = new ${operation.dataType.simpleName}(<#list operation.mandatoryFields as field>${field.name}<#if field_has_next>, </#if></#list>);
        <#list operation.optionalFields as field>
        op.set${field.capitalizedName}(${field.name});
        </#list>
        return chainedExecutor.${operation.methodName}(op);
    }
    </#if>
    <#else>
    @Override
    public ${operation.pageInfoDataType.simpleName} ${operation.methodName}Count(<#list operation.mandatoryFields as field>${field.dataType.simpleName} ${field.name}<#if field_has_next>, </#if></#list>) {
        ${operation.dataType.simpleName} op = new ${operation.dataType.simpleName}(<#list operation.mandatoryFields as field>${field.name}, </#list>true);
        ${operation.returnDataType.simpleName} result = chainedExecutor.${operation.methodName}(op);
        if (result != null) {
            return result.getDataCount();
        } else {
            return null;
        }
    }

    @Override
    public ${listName}<${operation.oneItemReturnDataType.simpleName}> ${operation.methodName}(<#list operation.mandatoryFields as field>${field.dataType.simpleName} ${field.name}, </#list>${operation.pageInfoDataType.simpleName} limit) {
        ${operation.dataType.simpleName} op = new ${operation.dataType.simpleName}(<#list operation.mandatoryFields as field>${field.name}, </#list>limit);
        ${operation.returnDataType.simpleName} result = chainedExecutor.${operation.methodName}(op);
        if (result != null) {
            return result.getData();
        } else {
            return null;
        }
    }

    @Override
    public ${listName}<${operation.oneItemReturnDataType.simpleName}> ${operation.methodName}(<#list operation.mandatoryFields as field>${field.dataType.simpleName} ${field.name}, </#list>${operation.pageInfoDataType.simpleName} limit, ${operation.pageInfoDataType.simpleName} offset) {
        ${operation.dataType.simpleName} op = new ${operation.dataType.simpleName}(<#list operation.mandatoryFields as field>${field.name}, </#list>limit, offset);
        ${operation.returnDataType.simpleName} result = chainedExecutor.${operation.methodName}(op);
        if (result != null) {
            return result.getData();
        } else {
            return null;
        }
    }
    <#if operation.optionalFields?size &gt; 0>

    @Override
    public ${operation.pageInfoDataType.simpleName} ${operation.methodName}CountWithOptionals(<#list operation.fields as field>${field.dataType.simpleName} ${field.name}<#if field_has_next>, </#if></#list>) {
        ${operation.dataType.simpleName} op = new ${operation.dataType.simpleName}(<#list operation.mandatoryFields as field>${field.name}, </#list>true);
        <#list operation.optionalFields as field>
        op.set${field.capitalizedName}(${field.name});
        </#list>
        ${operation.returnDataType.simpleName} result = chainedExecutor.${operation.methodName}(op);
        if (result != null) {
            return result.getDataCount();
        } else {
            return null;
        }
    }

    @Override
    public ${listName}<${operation.oneItemReturnDataType.simpleName}> ${operation.methodName}WithOptionals(<#list operation.fields as field>${field.dataType.simpleName} ${field.name}, </#list>${operation.pageInfoDataType.simpleName} limit) {
        ${operation.dataType.simpleName} op = new ${operation.dataType.simpleName}(<#list operation.mandatoryFields as field>${field.name}, </#list>limit);
        <#list operation.optionalFields as field>
        op.set${field.capitalizedName}(${field.name});
        </#list>
        ${operation.returnDataType.simpleName} result = chainedExecutor.${operation.methodName}(op);
        if (result != null) {
            return result.getData();
        } else {
            return null;
        }
    }

    @Override
    public ${listName}<${operation.oneItemReturnDataType.simpleName}> ${operation.methodName}WithOptionals(<#list operation.fields as field>${field.dataType.simpleName} ${field.name}, </#list>${operation.pageInfoDataType.simpleName} limit, ${operation.pageInfoDataType.simpleName} offset) {
        ${operation.dataType.simpleName} op = new ${operation.dataType.simpleName}(<#list operation.mandatoryFields as field>${field.name}, </#list>limit, offset);
        <#list operation.optionalFields as field>
        op.set${field.capitalizedName}(${field.name});
        </#list>
        ${operation.returnDataType.simpleName} result = chainedExecutor.${operation.methodName}(op);
        if (result != null) {
            return result.getData();
        } else {
            return null;
        }
    }
    </#if>
    </#if>
    </#list>

    public ${className}(${executorInterfaceName} chainedExecutor) {
        if (chainedExecutor == null) {
            throw new IllegalArgumentException("chainedExecutor for the ${className} cannot be null");
        }
        this.chainedExecutor = chainedExecutor;
    }
}