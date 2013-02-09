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

public interface ${className} {

    <#list operations as operation>
    <#if operation.operationKind.id != 3>
    public ${operation.returnDataType.simpleName} ${operation.methodName}(<#list operation.mandatoryFields as field>${field.dataType.simpleName} ${field.name}<#if field_has_next>, </#if></#list>);
    <#if operation.optionalFields?size &gt; 0>
    public ${operation.returnDataType.simpleName} ${operation.methodName}WithOptionals(<#list operation.fields as field>${field.dataType.simpleName} ${field.name}<#if field_has_next>, </#if></#list>);
    </#if>
    <#else>
    public ${operation.pageInfoDataType.simpleName} ${operation.methodName}Count(<#list operation.mandatoryFields as field>${field.dataType.simpleName} ${field.name}<#if field_has_next>, </#if></#list>);
    public ${listName}<${operation.oneItemReturnDataType.simpleName}> ${operation.methodName}(<#list operation.mandatoryFields as field>${field.dataType.simpleName} ${field.name}, </#list>${operation.pageInfoDataType.simpleName} limit);
    public ${listName}<${operation.oneItemReturnDataType.simpleName}> ${operation.methodName}(<#list operation.mandatoryFields as field>${field.dataType.simpleName} ${field.name}, </#list>${operation.pageInfoDataType.simpleName} limit, ${operation.pageInfoDataType.simpleName} offset);
    <#if operation.optionalFields?size &gt; 0>
    public ${operation.pageInfoDataType.simpleName} ${operation.methodName}CountWithOptionals(<#list operation.fields as field>${field.dataType.simpleName} ${field.name}<#if field_has_next>, </#if></#list>);
    public ${listName}<${operation.oneItemReturnDataType.simpleName}> ${operation.methodName}WithOptionals(<#list operation.fields as field>${field.dataType.simpleName} ${field.name}, </#list>${operation.pageInfoDataType.simpleName} limit);
    public ${listName}<${operation.oneItemReturnDataType.simpleName}> ${operation.methodName}WithOptionals(<#list operation.fields as field>${field.dataType.simpleName} ${field.name}, </#list>${operation.pageInfoDataType.simpleName} limit, ${operation.pageInfoDataType.simpleName} offset);
    </#if>
    </#if>
    </#list>
}