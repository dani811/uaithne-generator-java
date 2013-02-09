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

<#if entity.documentation??>
/**
 <#list entity.documentation as doc>
 * ${doc}
 </#list>
 */
</#if>
public class ${className}<#if entity.extend??> extends ${entity.extend.simpleName}</#if><#if entity.implement?size &gt; 0> implements <#list entity.implement as implement>${implement.simpleName}<#if implement_has_next>, </#if></#list></#if> {

    <#list entity.fieldsWithExtras as field>
    <#if field.documentation??>
    /**
     <#list field.documentation as doc>
     * ${doc}
     </#list>
     */
    </#if>
    private <#if field.markAsTransient>transient </#if>${field.dataType.simpleName} ${field.name}<#if field.defaultValue?has_content> = ${field.defaultValue}</#if>;
    </#list>

    <#list entity.fieldsWithExtras as field>
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

    <#-- See how netbeans generate it: http://hg.netbeans.org/main/file/84453ca69694/java.editor/src/org/netbeans/modules/java/editor/codegen/ToStringGenerator.java -->
    @Override
    public String toString(){
        return "${className}{" +
        <#list entity.fieldsWithExtras as field>
            "${field.name}=" + ${field.name} + <#if field_has_next || entity.extend??>"| " +</#if>
        </#list>
        <#if entity.extend??>
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
        <#if entity.extend?? || entity.idFields?size <= 0>
        if (!super.equals(obj)) {
            return false;
        }
        </#if>
        <#if entity.idFields?size &gt; 0>
        final ${className} other = (${className}) obj;
            <#list entity.idFields as field>
        if (${field.generateEqualsRule()}) {
            return false;
        }
            </#list>
        </#if>
        return true;
    }

    <#-- See how netbeans generate it: http://hg.netbeans.org/main/file/84453ca69694/java.editor/src/org/netbeans/modules/java/editor/codegen/EqualsHashCodeGenerator.java -->
    @Override
    public int hashCode() {
        int hash = ${entity.generateFistPrimeNumberForHashCode()};
        <#assign prime=entity.generateSecondPrimeNumberForHashCode()>
        <#if entity.extend?? || entity.idFields?size <= 0>
        hash = ${prime} * hash + super.hashCode();
        </#if>
        <#list entity.idFields as field>
        hash = ${prime} * hash + ${field.generateHashCodeRule()};
        </#list>
        return hash;
    }

    <#if entity.combined.mandatoryFields?size &gt; 0>
    public ${className}() {
    }
    </#if>

    public ${className}(<#list entity.combined.mandatoryFields as field>${field.dataType.simpleName} ${field.name}<#if field_has_next>, </#if></#list>) {
        <#if entity.parent??>
        super(<#list entity.parent.mandatoryFields as field>${field.name}<#if field_has_next>, </#if></#list>);
        </#if>
        <#list entity.mandatoryFields as field>
        this.${field.name} = ${field.name};
        </#list>
    }

    <#if entity.combined.idFields?size &gt; 0>
    <#if entity.combined.idAndMandatoryFields?size &gt; 0>
    public ${className}(<#list entity.combined.idAndMandatoryFields as field>${field.dataType.simpleName} ${field.name}<#if field_has_next>, </#if></#list>) {
        <#if entity.parent??>
        super(<#list entity.parent.idAndMandatoryFields as field>${field.name}<#if field_has_next>, </#if></#list>);
        </#if>
        <#list entity.idAndMandatoryFields as field>
        this.${field.name} = ${field.name};
        </#list>
    }
    </#if>
    </#if>
}
