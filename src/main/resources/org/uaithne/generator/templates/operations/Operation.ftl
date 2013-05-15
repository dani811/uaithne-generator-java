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

<#if operation.documentation??>
/**
 <#list operation.documentation as doc>
 * ${doc}
 </#list>
 */
</#if>
public class ${className}<#if operation.extend??> extends ${operation.extend.simpleName}</#if><#if operation.implement?size &gt; 0> implements <#list operation.implement as implement>${implement.simpleName}<#if implement_has_next>, </#if></#list></#if> {

    <#list operation.fieldsWithExtras as field>
    <#if field.documentation??>
    /**
     <#list field.documentation as doc>
     * ${doc}
     </#list>
     */
    </#if>
    private <#if field.markAsTransient>transient </#if>${field.dataType.simpleName} ${field.name}<#if field.defaultValue?has_content> = ${field.defaultValue}</#if>;
    </#list>

    <#list operation.fieldsWithExtras as field>
    /**
    <#if field.documentation??>
     <#list field.documentation as doc>
     * ${doc}
     </#list>
     *
    </#if>
     * @return the ${field.name}
     */
    <#if field.markAsOvwrride>
    @Override
    </#if>
    <#if field.dataType.simpleName == "boolean">
    <#if field.deprecated>@Deprecated</#if>
    public ${field.dataType.simpleName} is${field.capitalizedName}() {
        return ${field.name};
    }
    <#else>
    <#if field.deprecated>@Deprecated</#if>
    public ${field.dataType.simpleName} get${field.capitalizedName}() {
        return ${field.name};
    }
    </#if>

    /**
    <#if field.documentation??>
     <#list field.documentation as doc>
     * ${doc}
     </#list>
     *
    </#if>
     * @param ${field.name} the ${field.name} to set
     */
    <#if field.markAsOvwrride>
    @Override
    </#if>
    <#if field.deprecated>@Deprecated</#if>
    public void set${field.capitalizedName}(${field.dataType.simpleName} ${field.name}) {
        this.${field.name} = ${field.name};
    }

    </#list>

    <#if operation.operationKind.id == 3>
    @Override
    public ${operation.pageInfoDataType.simpleName} getMaxRowNumber() {
        <#if operation.pageInfoDataType.packageName == "">
        return limit + offset;
        <#else>
        if (limit == null) {
            return null;
        }
        if (offset == null) {
            return limit;
        }
        return limit.add(offset);
        </#if>
    }
    </#if>

    <#-- See how netbeans generate it: http://hg.netbeans.org/main/file/84453ca69694/java.editor/src/org/netbeans/modules/java/editor/codegen/ToStringGenerator.java -->
    @Override
    public String toString(){
        return "${className}{" +
        <#list operation.fieldsWithExtras as field>
            "${field.name}=" + ${field.name} + <#if field_has_next || operation.extend??>"| " +</#if>
        </#list>
        <#if operation.extend??>
            "superclass=" + super.toString() +
        </#if>
            "}";
    }

    <#-- See how netbeans generate it: http://hg.netbeans.org/main/file/84453ca69694/java.editor/src/org/netbeans/modules/java/editor/codegen/EqualsHashCodeGenerator.java -->
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        <#if operation.extend?? || (operation.fields?size <= 0 && operation.operationKind.id != 3)>
        if (!super.equals(obj)) {
            return false;
        }
        </#if>
        <#if operation.fields?size &gt; 0>
        final ${className} other = (${className}) obj;
            <#list operation.fields as field>
        if (${field.generateEqualsRule()}) {
            return false;
        }
            </#list>
        <#elseif operation.operationKind.id == 3> 
        final ${className} other = (${className}) obj;
        </#if>
        <#if operation.operationKind.id == 3>
        if (${operation.pageInfoDataType.generateEqualsRule("limit")}) {
            return false;
        }
        if (${operation.pageInfoDataType.generateEqualsRule("offset")}) {
            return false;
        }
        if (${operation.pageInfoDataType.generateEqualsRule("dataCount")}) {
            return false;
        }
        if (${operation.pageOnlyDataCountDataType.generateEqualsRule("onlyDataCount")}) {
            return false;
        }
        </#if>
        return true;
    }

    <#-- See how netbeans generate it: http://hg.netbeans.org/main/file/84453ca69694/java.editor/src/org/netbeans/modules/java/editor/codegen/EqualsHashCodeGenerator.java -->
    @Override
    public int hashCode() {
        int hash = ${operation.generateFistPrimeNumberForHashCode()};
        <#assign prime=operation.generateSecondPrimeNumberForHashCode()>
        <#if operation.extend?? || (operation.fields?size <= 0 && operation.operationKind.id != 3)>
        hash = ${prime} * hash + super.hashCode();
        </#if>
        <#list operation.fields as field>
        hash = ${prime} * hash + ${field.generateHashCodeRule()};
        </#list>
        <#if operation.operationKind.id == 3>
        hash = ${prime} * hash + ${operation.pageInfoDataType.generateHashCodeRule("limit")};
        hash = ${prime} * hash + ${operation.pageInfoDataType.generateHashCodeRule("offset")};
        hash = ${prime} * hash + ${operation.pageInfoDataType.generateHashCodeRule("dataCount")};
        hash = ${prime} * hash + ${operation.pageOnlyDataCountDataType.generateHashCodeRule("onlyDataCount")};
        </#if>
        return hash;
    }
    
    @Override
    public ${operation.returnDataType.simpleName} execute(Executor executor) {
        return execute((${executorName})executor);
    }
    
    public ${operation.returnDataType.simpleName} execute(${executorName} executor) {
        return executor.${operation.methodName}(this);
    }

    @Override
    public ${operation.returnDataType.simpleName} executePostOperation(${operation.returnDataType.simpleName} result) {
        <#if operation.operationKind.id == 5>
        if (value != null) {
            value.set${operation.entity.combined.firstIdField.capitalizedName}(result);
        }
        return result;
        <#elseif operation.operationKind.id == 7>
        if (value != null) {
            value.set${operation.entity.combined.firstIdField.capitalizedName}(result);
        }
        return result;
        <#else>
        return result;
        </#if>
    }

    @Override
    public Object getExecutorSelector() {
        return ${executorName}.SELECTOR;
    }

    <#if operation.operationKind.id != 3>
    <#if operation.mandatoryFields?size &gt; 0>
    public ${className}() {
    }
    </#if>

    public ${className}(<#list operation.mandatoryFields as field>${field.dataType.simpleName} ${field.name}<#if field_has_next>, </#if></#list>) {
        <#list operation.mandatoryFields as field>
        this.${field.name} = ${field.name};
        </#list>
    }
    <#else>
    <#if operation.mandatoryFields?size &gt; 0>
    public ${className}() {
    }
    </#if>

    public ${className}(<#list operation.mandatoryFields as field>${field.dataType.simpleName} ${field.name}<#if field_has_next>, </#if></#list>) {
        <#list operation.mandatoryFields as field>
        this.${field.name} = ${field.name};
        </#list>
    }

    public ${className}(<#list operation.mandatoryFields as field>${field.dataType.simpleName} ${field.name}, </#list>${operation.pageInfoDataType.simpleName} limit) {
        <#list operation.mandatoryFields as field>
        this.${field.name} = ${field.name};
        </#list>
        this.limit = limit;
    }

    public ${className}(<#list operation.mandatoryFields as field>${field.dataType.simpleName} ${field.name}, </#list>${operation.pageInfoDataType.simpleName} limit, ${operation.pageInfoDataType.simpleName} offset) {
        <#list operation.mandatoryFields as field>
        this.${field.name} = ${field.name};
        </#list>
        this.limit = limit;
        this.offset = offset;
    }

    public ${className}(<#list operation.mandatoryFields as field>${field.dataType.simpleName} ${field.name}, </#list>${operation.pageInfoDataType.simpleName} limit, ${operation.pageInfoDataType.simpleName} offset, ${operation.pageInfoDataType.simpleName} dataCount) {
        <#list operation.mandatoryFields as field>
        this.${field.name} = ${field.name};
        </#list>
        this.limit = limit;
        this.offset = offset;
        this.dataCount = dataCount;
    }

    public ${className}(<#list operation.mandatoryFields as field>${field.dataType.simpleName} ${field.name}, </#list>${operation.pageOnlyDataCountDataType.simpleName} onlyDataCount) {
        <#list operation.mandatoryFields as field>
        this.${field.name} = ${field.name};
        </#list>
        this.onlyDataCount = onlyDataCount;
    }
    </#if>
}
