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
import org.apache.ibatis.session.SqlSession;

public<#if abstractClass> abstract</#if> class ${className} extends ${abstractExecutorName} {
    <#list module.entities as entity>
    <#if entity.usedInOrderedOperation>
    protected HashMap<String,String> orderByTranslationsFor${entity.dataType.simpleName} = new HashMap<String,String>();
    </#if>
    </#list>

    <#if generateGetSession>
    private SqlSessionProvider provider;

    public SqlSession getSession() {
        return provider.getSqlSession();
    }
    <#else>
    public abstract SqlSession getSession();
    </#if>

    <#list module.operations as operation>
    <#if operation.manually>
    <#elseif operation.operationKind.id == 1>
    @Override
    public ${operation.returnDataType.simpleName} ${operation.methodName}(${operation.dataType.simpleName} operation) {
        <#list operation.fields as field>
        <#if field.orderBy>
        ${field.dataType.simpleName} ${field.name}Old = operation.get${field.capitalizedName}();
        operation.set${field.capitalizedName}(translateOrderBy(${field.name}Old, orderByTranslationsFor${operation.entity.dataType.simpleName}));
        </#if>
        </#list>

        ${operation.returnDataType.simpleName} result = (${operation.returnDataType.simpleName}) getSession().selectOne("${operation.extraInfo.myBatisStatementId}", operation);

        <#list operation.fields as field>
        <#if field.orderBy>
        operation.set${field.capitalizedName}(${field.name}Old);
        </#if>
        </#list>

        return result;
    }
    <#elseif operation.operationKind.id == 2>
    @Override
    public ${operation.returnDataType.simpleName} ${operation.methodName}(${operation.dataType.simpleName} operation) {
        <#list operation.fields as field>
        <#if field.orderBy>
        ${field.dataType.simpleName} ${field.name}Old = operation.get${field.capitalizedName}();
        operation.set${field.capitalizedName}(translateOrderBy(${field.name}Old, orderByTranslationsFor${operation.entity.dataType.simpleName}));
        </#if>
        </#list>

        <#if generation.useConcreteCollections>
        List<${operation.oneItemReturnDataType.simpleName}> tmp = getSession().selectList("${operation.extraInfo.myBatisStatementId}", operation);
        ArrayList<${operation.oneItemReturnDataType.simpleName}> result;
        if (tmp == null) {
            result = null;
        } else if (tmp instanceof ArrayList) {
            result = (ArrayList<${operation.oneItemReturnDataType.simpleName}>) tmp;
        } else {
            <#if operation.oneItemReturnDataType != operation.oneItemReturnDataType>
            result = new ArrayList<${operation.oneItemReturnDataType.simpleName}>(tmp.size());
            for(${operation.oneItemReturnDataType.simpleName} element : tmp) {
                result.add(new ${operation.oneItemReturnDataType.simpleName}(element));
            }
            <#else>
            result = new ArrayList<${operation.oneItemReturnDataType.simpleName}>(tmp);
            </#if>
        }
        <#else>
        List<${operation.oneItemReturnDataType.simpleName}> result = getSession().selectList("${operation.extraInfo.myBatisStatementId}", operation);
        </#if>

        <#list operation.fields as field>
        <#if field.orderBy>
        operation.set${field.capitalizedName}(${field.name}Old);
        </#if>
        </#list>

        return result;
    }
    <#elseif operation.operationKind.id == 3>
    @Override
    public ${operation.returnDataType.simpleName} ${operation.methodName}(${operation.dataType.simpleName} operation) {
        <#list operation.fields as field>
        <#if field.orderBy>
        ${field.dataType.simpleName} ${field.name}Old = operation.get${field.capitalizedName}();
        operation.set${field.capitalizedName}(translateOrderBy(${field.name}Old, orderByTranslationsFor${operation.entity.dataType.simpleName}));
        </#if>
        </#list>

        ${operation.returnDataType.simpleName} result = new ${operation.returnDataType.simpleName}();
        ${operation.pageInfoDataType.simpleName} count = operation.getDataCount();
        if (count == null) {
            count = (${operation.pageInfoDataType.simpleName}) getSession().selectOne("${operation.extraInfo.myBatisCountStatementId}", operation);
        }
        result.setDataCount(count);
        if (operation.isOnlyDataCount()) {
            return result;
        }

        <#if generation.useConcreteCollections>
        List<${operation.oneItemReturnDataType.simpleName}> tmp = getSession().selectList("${operation.extraInfo.myBatisStatementId}", operation);
        ArrayList<${operation.oneItemReturnDataType.simpleName}> data;
        if (tmp == null) {
            data = null;
        } else if (tmp instanceof ArrayList) {
            data = (ArrayList<${operation.oneItemReturnDataType.simpleName}>) tmp;
        } else {
            data = new ArrayList<${operation.oneItemReturnDataType.simpleName}>(tmp);
        }
        <#else>
        List<${operation.oneItemReturnDataType.simpleName}> data = getSession().selectList("${operation.extraInfo.myBatisStatementId}", operation);
        </#if>

        <#list operation.fields as field>
        <#if field.orderBy>
        operation.set${field.capitalizedName}(${field.name}Old);
        </#if>
        </#list>

        result.setData(data);
        result.setLimit(operation.getLimit());
        result.setOffset(operation.getOffset());

        return result;
    }
    <#elseif operation.operationKind.id == 4>
    @Override
    public ${operation.returnDataType.simpleName} ${operation.methodName}(${operation.dataType.simpleName} operation) {
        ${operation.returnDataType.simpleName} result = getSession().delete("${namespace}.${operation.methodName}", operation.getId());
        return result;
    }
    <#elseif operation.operationKind.id == 5>
    public ${operation.entity.combined.firstIdField.dataType.simpleName} getLastInsertedIdFor${operation.entity.dataType.simpleName}() {
        return (${operation.entity.combined.firstIdField.dataType.simpleName}) getSession().selectOne("${namespace}.lastInsertedIdFor${operation.entity.dataType.simpleName}");
    }

    @Override
    public ${operation.returnDataType.simpleName} ${operation.methodName}(${operation.dataType.simpleName} operation) {
        SqlSession session = getSession();
        Integer i = session.insert("${namespace}.insert${operation.entity.dataType.simpleName}", operation.getValue());
        if (i != null && i.intValue() == 1) {
            ${operation.returnDataType.simpleName} result = (${operation.entity.combined.firstIdField.dataType.simpleName}) session.selectOne("${namespace}.lastInsertedIdFor${operation.entity.dataType.simpleName}");
            return result;
        } else {
            throw new IllegalStateException("Unable to insert a ${operation.entity.dataType.simpleName}. Operation: " + operation + ". Insertion result: " + i);
        }
    }
    <#elseif operation.operationKind.id == 6>
    @Override
    public ${operation.returnDataType.simpleName} ${operation.methodName}(${operation.dataType.simpleName} operation) {
        ${operation.returnDataType.simpleName} result = getSession().insert("${namespace}.insert${operation.entity.dataType.simpleName}", operation.getValue());
        return result;
    }
    <#elseif operation.operationKind.id == 7>
    @Override
    public ${operation.returnDataType.simpleName} ${operation.methodName}(${operation.dataType.simpleName} operation) {
        ${operation.entity.dataType.simpleName} value = operation.getValue();
        if (value.get${operation.entity.combined.firstIdField.capitalizedName}() == null) {
            SqlSession session = getSession();
            Integer i = session.insert("${namespace}.insert${operation.entity.dataType.simpleName}", value);
            if (i != null && i.intValue() == 1) {
                ${operation.returnDataType.simpleName} result = (${operation.entity.combined.firstIdField.dataType.simpleName}) session.selectOne("${namespace}.lastInsertedIdFor${operation.entity.dataType.simpleName}");
                return result;
            } else {
                throw new IllegalStateException("Unable to insert a ${operation.entity.dataType.simpleName}. Operation: " + operation + ". Insertion result: " + i);
            }
        } else {
            Integer i = getSession().update("${namespace}.update${operation.entity.dataType.simpleName}", value);
            if (i != null && i.intValue() == 1) {
                ${operation.returnDataType.simpleName} result = value.get${operation.entity.combined.firstIdField.capitalizedName}();
                return result;
            } else {
                throw new IllegalStateException("Unable to update a ${operation.entity.dataType.simpleName}. Operation: " + operation + ". Update result: " + i);
            }
        }
    }
    <#elseif operation.operationKind.id == 8>
    @Override
    public ${operation.returnDataType.simpleName} ${operation.methodName}(${operation.dataType.simpleName} operation) {
        ${operation.entity.dataType.simpleName} value = operation.getValue();
        ${operation.returnDataType.simpleName} result;
        if (value.get${operation.entity.combined.firstIdField.capitalizedName}() == null) {
            result = getSession().insert("${namespace}.insert${operation.entity.dataType.simpleName}", value);
        } else {
            result = getSession().update("${namespace}.update${operation.entity.dataType.simpleName}", value);
        }
        return result;
    }
    <#elseif operation.operationKind.id == 9>
    @Override
    public ${operation.returnDataType.simpleName} ${operation.methodName}(${operation.dataType.simpleName} operation) {
        ${operation.returnDataType.simpleName} result = (${operation.returnDataType.simpleName}) getSession().selectOne("${namespace}.${operation.methodName}", operation.getId());
        return result;
    }
    <#elseif operation.operationKind.id == 10>
    @Override
    public ${operation.returnDataType.simpleName} ${operation.methodName}(${operation.dataType.simpleName} operation) {
        ${operation.returnDataType.simpleName} result = getSession().update("${namespace}.update${operation.entity.dataType.simpleName}", operation.getValue());
        return result;
    }
    <#elseif operation.operationKind.id == 11>
    @Override
    public ${operation.returnDataType.simpleName} ${operation.methodName}(${operation.dataType.simpleName} operation) {
        ${operation.returnDataType.simpleName} result = getSession().insert("${operation.extraInfo.myBatisStatementId}", operation);
        return result;
    }
    <#elseif operation.operationKind.id == 12>
    @Override
    public ${operation.returnDataType.simpleName} ${operation.methodName}(${operation.dataType.simpleName} operation) {
        ${operation.returnDataType.simpleName} result = getSession().update("${operation.extraInfo.myBatisStatementId}", operation);
        return result;
    }
    <#elseif operation.operationKind.id == 13>
    @Override
    public ${operation.returnDataType.simpleName} ${operation.methodName}(${operation.dataType.simpleName} operation) {
        ${operation.returnDataType.simpleName} result = getSession().delete("${operation.extraInfo.myBatisStatementId}", operation);
        return result;
    }
    <#elseif operation.operationKind.id == 14>
    @Override
    public ${operation.returnDataType.simpleName} ${operation.methodName}(${operation.dataType.simpleName} operation) {
        SqlSession session = getSession();
        Integer i = session.insert("${operation.extraInfo.myBatisStatementId}", operation);
        if (i != null && i.intValue() == 1) {
            ${operation.returnDataType.simpleName} result = session.selectOne("${namespace}.lastInsertedIdFor${operation.entity.dataType.simpleName}");
            return result;
        } else {
            throw new IllegalStateException("Unable to insert. Operation: " + operation + ". Insertion result: " + i);
        }
    }
    <#elseif operation.operationKind.id == 15>
    @Override
    public ${operation.returnDataType.simpleName} ${operation.methodName}(${operation.dataType.simpleName} operation) {
        ${operation.returnDataType.simpleName} result = getSession().update("${namespace}.merge${operation.entity.dataType.simpleName}", operation.getValue());
        return result;
    }
    </#if>

    </#list>

    <#if module.containOrderedOperations>
    public String translateOrderBy(String orderBy, HashMap<String, String> orderByTranslations) {
        if (orderBy == null || orderBy.isEmpty()) {
            return null;
        }
        if (orderByTranslations == null || orderByTranslations.isEmpty()) {
            return null;
        }
        orderBy = orderBy.toLowerCase();
        orderBy = orderBy.replaceAll("\\s+", " ");
        String[] split = orderBy.split("\\s*,\\s*");

        StringBuilder sb = new StringBuilder();
        if (split != null) {
            for (String s : split) {
                s = s.trim();
                String translated = orderByTranslations.get(s);
                if (translated == null) {
                    throw new IllegalArgumentException("Invalid order by clause: " + s);
                }
                if (!translated.isEmpty()) {
                    sb.append(translated);
                    sb.append(", ");
                }
            }
        }

        if (sb.length() > 2) {
            return sb.substring(0, sb.length() - 2);
        } else {
            return null;
        }
    }
    </#if>

    <#if generateGetSession>
    public ${className}(SqlSessionProvider provider) {
        if (provider == null) {
            throw new IllegalArgumentException("provider for the MyBatisOracleCampaignsMapperImpl cannot be null");
        }
        this.provider = provider;

    <#else>
    public ${className}() {
    </#if>
        <#if useAliasInOrderBy>
        <#list module.entities as entity>
        <#if entity.combined.usedInOrderedOperation>
        <#list entity.combined.fields as field>
        orderByTranslationsFor${entity.dataType.simpleName}.put("${field.lowerCaseName}", "\"${field.lowerCaseName}\"");
        orderByTranslationsFor${entity.dataType.simpleName}.put("${field.lowerCaseName} asc", "\"${field.lowerCaseName}\" asc");
        orderByTranslationsFor${entity.dataType.simpleName}.put("${field.lowerCaseName} desc", "\"${field.lowerCaseName}\" desc");
        </#list>
        </#if>

        </#list>
        </#if>
        <#if !useAliasInOrderBy>
        <#list module.entities as entity>
        <#if entity.combined.usedInOrderedOperation>
        <#list entity.combined.fields as field>
        orderByTranslationsFor${entity.dataType.simpleName}.put("${field.lowerCaseName}", "${field.mappedNameOrName}");
        orderByTranslationsFor${entity.dataType.simpleName}.put("${field.lowerCaseName} asc", "${field.mappedNameOrName} asc");
        orderByTranslationsFor${entity.dataType.simpleName}.put("${field.lowerCaseName} desc", "${field.mappedNameOrName} desc");
        </#list>
        </#if>

        </#list>
        </#if>
    }
}