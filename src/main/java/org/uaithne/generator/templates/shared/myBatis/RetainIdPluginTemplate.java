/*
 * Copyright 2012 and beyond, Juan Luis Paz
 *
 * This file is part of Uaithne.
 *
 * Uaithne is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Uaithne is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Uaithne. If not, see <http://www.gnu.org/licenses/>.
 */
package org.uaithne.generator.templates.shared.myBatis;

import java.io.IOException;
import org.uaithne.generator.templates.ClassTemplate;

// Base on Jdbc3KeyGenerator in MyBatis 3.2.2
public class RetainIdPluginTemplate extends ClassTemplate {

    public RetainIdPluginTemplate(String packageName) {
        setPackageName(packageName);
        addImport("java.lang.reflect.Field", packageName);
        addImport("java.sql.ResultSet", packageName);
        addImport("java.sql.ResultSetMetaData", packageName);
        addImport("java.sql.SQLException", packageName);
        addImport("java.sql.Statement", packageName);
        addImport("java.util.List", packageName);
        addImport("java.util.Properties", packageName);
        addImport("org.apache.ibatis.executor.Executor", packageName);
        addImport("org.apache.ibatis.executor.ExecutorException", packageName);
        addImport("org.apache.ibatis.executor.keygen.Jdbc3KeyGenerator", packageName);
        addImport("org.apache.ibatis.executor.keygen.KeyGenerator", packageName);
        addImport("org.apache.ibatis.mapping.MappedStatement", packageName);
        addImport("org.apache.ibatis.plugin.Interceptor", packageName);
        addImport("org.apache.ibatis.plugin.Intercepts", packageName);
        addImport("org.apache.ibatis.plugin.Invocation", packageName);
        addImport("org.apache.ibatis.plugin.Plugin", packageName);
        addImport("org.apache.ibatis.plugin.Signature", packageName);
        addImport("org.apache.ibatis.reflection.MetaObject", packageName);
        addImport("org.apache.ibatis.session.Configuration", packageName);
        addImport("org.apache.ibatis.type.TypeAliasRegistry", packageName);
        addImport("org.apache.ibatis.type.TypeHandler", packageName);
        addImport("org.apache.ibatis.type.TypeHandlerRegistry", packageName);
        setClassName("RetainIdPlugin");
        addImplement("Interceptor");
    }

    @Override
    protected void writeClassAnnotations(Appendable appender) throws IOException {
        super.writeClassAnnotations(appender);
        appender.append("@Intercepts({\n" +
            "    @Signature(\n" +
            "            type = Executor.class,\n" +
            "            method = \"update\",\n" +
            "            args = {MappedStatement.class, Object.class}\n" +
            "    )}\n" +
            ")\n");
    }
    
    @Override
    protected void writeContent(Appendable appender) throws IOException {
        appender.append("    private static Field keyGeneratorField;\n" +
            "    private static final RetainKeyGenerator retainKeyGenerator = new RetainKeyGenerator();\n" +
            "    private static final ThreadLocal<Object> retain = new ThreadLocal<Object>();\n" +
            "    private static final ThreadLocal<Boolean> configured = new ThreadLocal<Boolean>();\n" +
            "\n" +
            "    public static Object getRetainedId() {\n" +
            "        if (configured.get() != Boolean.TRUE) {\n" +
            "            throw new IllegalStateException(\"RetainIdPlugin is not loaded, you must put RetainIdPlugin class as a plugin in the MyBatis configuration\");\n" +
            "        }\n" +
            "        return retain.get();\n" +
            "    }\n" +
            "\n" +
            "    @Override\n" +
            "    public Object intercept(Invocation invocation) throws Throwable {\n" +
            "        MappedStatement mappedStatement = (MappedStatement) invocation.getArgs()[0];\n" +
            "        String[] keys = mappedStatement.getKeyProperties();\n" +
            "        boolean containsRetain = false;\n" +
            "        if (keys != null) {\n" +
            "            for (String key : keys) {\n" +
            "                containsRetain = containsRetain || key.startsWith(\"__retain_\");\n" +
            "            }\n" +
            "        }\n" +
            "        KeyGenerator keyGenerator = mappedStatement.getKeyGenerator();\n" +
            "        if (containsRetain && keyGenerator instanceof Jdbc3KeyGenerator) {\n" +
            "            if (keyGeneratorField == null) {\n" +
            "                keyGeneratorField = MappedStatement.class.getDeclaredField(\"keyGenerator\");\n" +
            "                keyGeneratorField.setAccessible(true);\n" +
            "            }\n" +
            "            keyGeneratorField.set(mappedStatement, retainKeyGenerator);\n" +
            "        }\n" +
            "        return invocation.proceed();\n" +
            "    }\n" +
            "\n" +
            "    @Override\n" +
            "    public Object plugin(Object target) {\n" +
            "        configured.set(Boolean.TRUE);\n" +
            "        return Plugin.wrap(target, this);\n" +
            "    }\n" +
            "\n" +
            "    @Override\n" +
            "    public void setProperties(Properties properties) {\n" +
            "    }\n" +
            "\n" +
            "    private static class RetainKeyGenerator extends Jdbc3KeyGenerator {\n" +
            "\n" +
            "        @Override\n" +
            "        public void processBatch(MappedStatement ms, Statement stmt, List<Object> parameters) {\n" +
            "\n" +
            "            ResultSet rs = null;\n" +
            "            try {\n" +
            "                rs = stmt.getGeneratedKeys();\n" +
            "                final Configuration configuration = ms.getConfiguration();\n" +
            "                final TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();\n" +
            "                final String[] keyProperties = ms.getKeyProperties();\n" +
            "                final TypeAliasRegistry typeAliasRegistry = configuration.getTypeAliasRegistry();\n" +
            "\n" +
            "                final ResultSetMetaData rsmd = rs.getMetaData();\n" +
            "                TypeHandler<?>[] typeHandlers = null;\n" +
            "                if (keyProperties != null && rsmd.getColumnCount() >= keyProperties.length) {\n" +
            "                    for (Object parameter : parameters) {\n" +
            "                        if (!rs.next()) {\n" +
            "                            break; // there should be one row for each statement (also one for each parameter)\n" +
            "                        }\n" +
            "                        final MetaObject metaParam = configuration.newMetaObject(parameter);\n" +
            "                        if (typeHandlers == null) {\n" +
            "                            typeHandlers = getTypeHandlers(typeAliasRegistry, typeHandlerRegistry, metaParam, keyProperties);\n" +
            "                        }\n" +
            "                        populateKeys(rs, metaParam, keyProperties, typeHandlers);\n" +
            "                    }\n" +
            "                }\n" +
            "            } catch (Exception e) {\n" +
            "                throw new ExecutorException(\"Error getting generated key or setting result to parameter object. Cause: \" + e, e);\n" +
            "            } finally {\n" +
            "                if (rs != null) {\n" +
            "                    try {\n" +
            "                        rs.close();\n" +
            "                    } catch (Exception e) {\n" +
            "                        // ignore\n" +
            "                    }\n" +
            "                }\n" +
            "            }\n" +
            "        }\n" +
            "\n" +
            "        private TypeHandler<?>[] getTypeHandlers(TypeAliasRegistry typeAliasRegistry, TypeHandlerRegistry typeHandlerRegistry, MetaObject metaParam, String[] keyProperties) {\n" +
            "            TypeHandler<?>[] typeHandlers = new TypeHandler<?>[keyProperties.length];\n" +
            "            for (int i = 0; i < keyProperties.length; i++) {\n" +
            "                String key = keyProperties[i];\n" +
            "                if (metaParam.hasSetter(key)) {\n" +
            "                    Class<?> keyPropertyType = metaParam.getSetterType(key);\n" +
            "                    TypeHandler<?> th = typeHandlerRegistry.getTypeHandler(keyPropertyType);\n" +
            "                    typeHandlers[i] = th;\n" +
            "                } else if (key.startsWith(\"__retain_\")) {\n" +
            "                    String type = key.substring(9);\n" +
            "                    Class<?> keyPropertyType = typeAliasRegistry.resolveAlias(type);\n" +
            "                    TypeHandler<?> th = typeHandlerRegistry.getTypeHandler(keyPropertyType);\n" +
            "                    typeHandlers[i] = th;\n" +
            "                }\n" +
            "            }\n" +
            "            return typeHandlers;\n" +
            "        }\n" +
            "\n" +
            "        private void populateKeys(ResultSet rs, MetaObject metaParam, String[] keyProperties, TypeHandler<?>[] typeHandlers) throws SQLException {\n" +
            "            for (int i = 0; i < keyProperties.length; i++) {\n" +
            "                String key = keyProperties[i];\n" +
            "                TypeHandler<?> th = typeHandlers[i];\n" +
            "                if (th != null) {\n" +
            "                    Object value = th.getResult(rs, i + 1);\n" +
            "                    if (metaParam.hasSetter(key)) {\n" +
            "                        metaParam.setValue(key, value);\n" +
            "                    } else {\n" +
            "                        retain.set(value);\n" +
            "                    }\n" +
            "                }\n" +
            "            }\n" +
            "        }\n" +
            "\n" +
            "    }");
    }
    
}
