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
package org.uaithne.generator.templates.operations.myBatis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import org.uaithne.generator.commons.DataTypeInfo;
import static org.uaithne.generator.commons.DataTypeInfo.*;
import org.uaithne.generator.commons.EntityInfo;
import org.uaithne.generator.commons.ExecutorModuleInfo;
import org.uaithne.generator.commons.FieldInfo;
import org.uaithne.generator.commons.GenerationInfo;
import org.uaithne.generator.commons.InsertedIdOrigin;
import org.uaithne.generator.commons.OperationInfo;
import org.uaithne.generator.commons.OperationKind;
import org.uaithne.generator.templates.operations.ExecutorModuleTemplate;

public class MyBatisTemplate extends ExecutorModuleTemplate {

    private String namespace;
    private boolean useAliasInOrderBy;
    private boolean hasUnimplementedOperations;
    private String indentation = "        ";

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public boolean isUseAliasInOrderBy() {
        return useAliasInOrderBy;
    }

    public void setUseAliasInOrderBy(boolean useAliasInOrderBy) {
        this.useAliasInOrderBy = useAliasInOrderBy;
    }

    public boolean hasUnimplementedOperations() {
        return hasUnimplementedOperations;
    }

    public void setUnimplementedOperations(boolean hasUnimplementedOperations) {
        this.hasUnimplementedOperations = hasUnimplementedOperations;
    }
    
    public MyBatisTemplate(ExecutorModuleInfo executorModule, String packageName, String className, String namespace, boolean useAliasInOrderBy, boolean hasUnimplementedOperations) {
        setPackageName(packageName);
        addImport(OPERATION_DATA_TYPE, packageName);
        addImport("org.apache.ibatis.session.SqlSession", packageName);
        addImport(executorModule.getOperationPackage() + "." + executorModule.getExecutorInterfaceName(), packageName);
        executorModule.appendNotMannuallyDefinitionImports(packageName, getImport());
        if (executorModule.isContainOrderedOperations()) {
            addImport(HASHMAP_DATA_TYPE, packageName);
        }
        if (executorModule.isContainPagedOperations()) {
            addImport(PAGE_INFO_DATA_TYPE, packageName);
            addImport(LIST_DATA_TYPE, packageName);            
        }
        addImport(MYBATIS_SQL_SESSION_PROVIDER_DATA_TYPE, packageName);
        for (OperationInfo operation : executorModule.getOperations()) {
            if (operation.isManually()) {
                continue;
            }
            switch (operation.getOperationKind()) {
                case INSERT:
                case SAVE:
                case JUST_SAVE:
                    addImport(operation.getEntity().getDataType(), packageName);
                    break;
                case COMPLEX_SELECT_CALL:
                case COMPLEX_INSERT_CALL:
                case COMPLEX_UPDATE_CALL:
                case COMPLEX_DELETE_CALL: {
                    GenerationInfo generationInfo = getGenerationInfo();
                    EntityInfo resultEntity = operation.getEntity();
                    if (resultEntity != null) {
                        resultEntity = resultEntity.getCombined();
                        ArrayList<FieldInfo> resultFields = resultEntity.getFields();
                        HashSet<String> processedFields = new HashSet<String>(resultFields.size() + 2);
                        processedFields.add("operation");
                        processedFields.add("result");
                        for (FieldInfo resultField : resultFields) {
                            if (processedFields.contains(resultField.getName())) {
                                continue;
                            }
                            DataTypeInfo fieldDataType = resultField.getDataType();
                            FieldInfo operationField = operation.getFieldByName(resultField.getName());
                            if (operationField == null) {
                                continue;
                            }
                            if (!fieldDataType.equals(operationField.getDataType())) {
                                continue;
                            }

                            addImport(resultField.getDataType(), packageName);
                            processedFields.add(resultField.getName());
                        }

                        for (FieldInfo operationField : operation.getFields()) {
                            if (processedFields.contains(operationField.getName())) {
                                continue;
                            }
                            EntityInfo operationFieldEntity = generationInfo.getEntityByName(operationField.getDataType());
                            if (operationFieldEntity == null) {
                                continue;
                            }
                            operationFieldEntity = operationFieldEntity.getCombined();

                            for (FieldInfo field : operationFieldEntity.getFields()) {
                                if (processedFields.contains(field.getName())) {
                                    continue;
                                }
                                DataTypeInfo fieldDataType = field.getDataType();
                                FieldInfo resultField = resultEntity.getFieldByName(field.getName());
                                if (resultField == null) {
                                    continue;
                                }
                                if (!fieldDataType.equals(resultField.getDataType())) {
                                    continue;
                                }

                                addImport(field.getDataType(), packageName);
                                processedFields.add(field.getName());
                            }
                        }
                    }
                }
                default:
            }
            if (operation.getInsertedIdOrigin() == InsertedIdOrigin.RETAINED) {
                addImport(MYBATIS_RETAIN_ID_PLUGIN_DATA_TYPE, packageName);
            }
        }
        setClassName(className);
        setExecutorModule(executorModule);
        addImplement(executorModule.getExecutorInterfaceName());
        setAbstract(hasUnimplementedOperations);
        this.namespace = namespace;
        this.useAliasInOrderBy = useAliasInOrderBy;
        this.hasUnimplementedOperations = hasUnimplementedOperations;
    }

    @Override
    protected void writeContent(Appendable appender) throws IOException {

        for (EntityInfo entity : getExecutorModule().getEntities()) {
            if (entity.isUsedInOrderedOperation()) {
                appender.append("    protected HashMap<String,String> orderByTranslationsFor").append(entity.getDataType().getSimpleNameWithoutGenerics()).append(" = new HashMap<String,String>();\n");
            }
        }

        appender.append("    private SqlSessionProvider provider;\n"
                + "\n"
                + "    public SqlSession getSession() {\n"
                + "        return provider.getSqlSession();\n"
                + "    }\n"
                + "\n");

        writeGetExecutorSelector(appender);
        appender.append("\n");

        writeExecuteMethods(appender);
        
        GenerationInfo generationInfo = getGenerationInfo();

        for (OperationInfo operation : getExecutorModule().getOperations()) {
            if (operation.isManually() || operation.getOperationKind() == OperationKind.CUSTOM) {
                continue;
            }
            
            appender.append("\n");

            if (operation.getOperationKind() == OperationKind.INSERT && operation.getInsertedIdOrigin() == InsertedIdOrigin.QUERY) {
                appender.append("    public ").append(operation.getEntity().getCombined().getFirstIdField().getDataType().getSimpleName()).append(" getLastInsertedIdFor").append(operation.getEntity().getDataType().getSimpleNameWithoutGenerics()).append("() {\n"
                        + "        return (").append(operation.getEntity().getCombined().getFirstIdField().getDataType().getSimpleName()).append(") getSession().selectOne(\"").append(namespace).append(".lastInsertedIdFor").append(operation.getEntity().getDataType().getSimpleNameWithoutGenerics()).append("\");\n"
                        + "    }\n"
                        + "\n");
            }

            OperationKind operationKind = operation.getOperationKind();
            
            appender.append("    @Override\n");
            if (operationKind.isComplexCall()) {
                writeComplexCallMethodHeader(appender, operation);
            } else {
                writeOperationMethodHeader(appender, operation);
            }
            appender.append(" {\n");

            String returnTypeName = operation.getReturnDataType().getSimpleName();
            switch (operation.getOperationKind()) {
                case SELECT_COUNT: {
                    writeStartOrderByVariable(appender, operation);
                    appender.append(indentation).append(returnTypeName).append(" result = (").append(returnTypeName).append(") getSession().selectOne(\"").append(operation.getQueryId()).append("\", operation);\n");
                    appender.append(indentation).append("return result;\n");
                    writeEndOrderByVariable(appender, operation);
                    break;
                }
                case SELECT_ONE: {
                    writeStartOrderByVariable(appender, operation);
                    appender.append(indentation).append(returnTypeName).append(" result = (").append(returnTypeName).append(") getSession().selectOne(\"").append(operation.getQueryId()).append("\", operation);\n");
                    appender.append(indentation).append("return result;\n");
                    writeEndOrderByVariable(appender, operation);
                    break;
                }
                case SELECT_MANY: {
                    writeStartOrderByVariable(appender, operation);
                    appender.append(indentation).append(returnTypeName).append(" result = getSession().selectList(\"").append(operation.getQueryId()).append("\", operation);\n");
                    appender.append(indentation).append("return result;\n");
                    writeEndOrderByVariable(appender, operation);
                    break;
                }
                case SELECT_PAGE: {
                    writeStartOrderByVariable(appender, operation);
                    appender.append(indentation).append(returnTypeName).append(" result = new ").append(returnTypeName).append("();\n")
                            .append(indentation).append(PAGE_INFO_DATA).append(" count = operation.getDataCount();\n")
                            .append(indentation).append("if (count == null) {\n")
                            .append(indentation).append("    count = (").append(PAGE_INFO_DATA).append(") getSession().selectOne(\"").append(operation.getCountQueryId()).append("\", operation);\n")
                            .append(indentation).append("}\n")
                            .append(indentation).append("result.setDataCount(count);\n")
                            .append(indentation).append("if (operation.isOnlyDataCount()) {\n")
                            .append(indentation).append("    return result;\n")
                            .append(indentation).append("}\n"
                            + "\n")
                            .append(indentation).append(LIST_DATA).append("<").append(operation.getOneItemReturnDataType().getSimpleName()).append("> data = getSession().selectList(\"").append(operation.getQueryId()).append("\", operation);\n");
                    appender.append(indentation).append("result.setData(data);\n")
                            .append(indentation).append("result.setLimit(operation.getLimit());\n")
                            .append(indentation).append("result.setOffset(operation.getOffset());\n");
                    appender.append(indentation).append("return result;\n");
                    writeEndOrderByVariable(appender, operation);
                    break;
                }
                case DELETE_BY_ID: {
                    appender.append("        ").append(returnTypeName).append(" result = getSession().delete(\"").append(operation.getQueryId()).append("\", operation.getId());\n");
                    appender.append("        return result;\n");
                    break;
                }
                case INSERT: {
                    appender.append("        ").append(operation.getEntity().getDataType().getSimpleName()).append(" value = operation.getValue();\n" 
                            + "        SqlSession session = getSession();\n"
                            + "        int i = session.insert(\"").append(operation.getQueryId()).append("\", value);\n"
                            + "        if (i == 1) {\n");
                    FieldInfo idField = operation.getEntity().getCombined().getFirstIdField();
                    InsertedIdOrigin idOrigin = operation.getInsertedIdOrigin();
                    if (idOrigin == InsertedIdOrigin.FIELD) {
                        appender.append("            ").append(returnTypeName).append(" result = ").append("value.");
                        if (idField.getDataType().isPrimitiveBoolean()) {
                            appender.append("is");
                        } else {
                            appender.append("get");
                        }
                        appender.append(idField.getCapitalizedName()).append("();\n");
                    } else if (idOrigin == InsertedIdOrigin.RETAINED) {
                        appender.append("            ").append(returnTypeName).append(" result = (").append(returnTypeName).append(") RetainIdPlugin.getRetainedId();\n");
                        if (generationInfo.isSetResultingIdInOperationEnabled()) {
                            appender.append("            value.set").append(idField.getCapitalizedName()).append("(result);\n");
                        }
                    } else {
                        appender.append("            ").append(returnTypeName).append(" result = (").append(returnTypeName).append(") session.selectOne(\"").append(namespace).append(".lastInsertedIdFor").append(operation.getEntity().getDataType().getSimpleNameWithoutGenerics()).append("\");\n");
                        if (generationInfo.isSetResultingIdInOperationEnabled()) {
                            appender.append("            value.set").append(idField.getCapitalizedName()).append("(result);\n");
                        }
                    }
                    appender.append("            return result;\n"
                            + "        } else {\n"
                            + "            throw new IllegalStateException(\"Unable to insert a ").append(operation.getEntity().getDataType().getSimpleName()).append(". Operation: \" + operation + \". Insertion result: \" + i);\n"
                            + "        }\n");
                    break;
                }
                case JUST_INSERT: {
                    appender.append("        ").append(returnTypeName).append(" result = getSession().insert(\"").append(operation.getQueryId()).append("\", operation.getValue());\n"
                            + "        return result;\n");
                    break;
                }
                case SAVE: {
                    appender.append("        ").append(operation.getEntity().getDataType().getSimpleName()).append(" value = operation.getValue();\n"
                            + "        if (value.get").append(operation.getEntity().getCombined().getFirstIdField().getCapitalizedName()).append("() == null) {\n"
                            + "            SqlSession session = getSession();\n"
                            + "            int i = session.insert(\"").append(operation.getSaveInsertQueryId()).append("\", value);\n"
                            + "            if (i == 1) {\n");
                    FieldInfo idField = operation.getEntity().getCombined().getFirstIdField();
                    InsertedIdOrigin idOrigin = operation.getInsertedIdOrigin();
                    if (idOrigin == InsertedIdOrigin.FIELD) {
                        appender.append("                ").append(returnTypeName).append(" result = ").append("value.");
                        if (idField.getDataType().isPrimitiveBoolean()) {
                            appender.append("is");
                        } else {
                            appender.append("get");
                        }
                        appender.append(idField.getCapitalizedName()).append("();\n");
                    } else if (idOrigin == InsertedIdOrigin.RETAINED) {
                        appender.append("                ").append(returnTypeName).append(" result = (").append(returnTypeName).append(") RetainIdPlugin.getRetainedId();\n");
                        if (generationInfo.isSetResultingIdInOperationEnabled()) {
                            appender.append("                value.set").append(idField.getCapitalizedName()).append("(result);\n");
                        }
                    } else {
                        appender.append("                ").append(returnTypeName).append(" result = (").append(returnTypeName).append(") session.selectOne(\"").append(namespace).append(".lastInsertedIdFor").append(operation.getEntity().getDataType().getSimpleNameWithoutGenerics()).append("\");\n");
                        if (generationInfo.isSetResultingIdInOperationEnabled()) {
                            appender.append("                value.set").append(idField.getCapitalizedName()).append("(result);\n");
                        }
                    }
                    appender.append("                return result;\n"
                            + "            } else {\n"
                            + "                throw new IllegalStateException(\"Unable to insert a ").append(operation.getEntity().getDataType().getSimpleName()).append(". Operation: \" + operation + \". Insertion result: \" + i);\n"
                            + "            }\n"
                            + "        } else {\n"
                            + "            int i = getSession().update(\"").append(operation.getQueryId()).append("\", value);\n"
                            + "            if (i == 1) {\n"
                            + "                ").append(returnTypeName).append(" result = value.get").append(operation.getEntity().getCombined().getFirstIdField().getCapitalizedName()).append("();\n"
                            + "                return result;\n"
                            + "            } else {\n"
                            + "                throw new IllegalStateException(\"Unable to update a ").append(operation.getEntity().getDataType().getSimpleName()).append(". Operation: \" + operation + \". Update result: \" + i);\n"
                            + "            }\n"
                            + "        }\n");
                    break;
                }
                case JUST_SAVE: {
                    appender.append("        ").append(operation.getEntity().getDataType().getSimpleName()).append(" value = operation.getValue();\n"
                            + "        ").append(returnTypeName).append(" result;\n"
                            + "        if (value.get").append(operation.getEntity().getCombined().getFirstIdField().getCapitalizedName()).append("() == null) {\n"
                            + "            result = getSession().insert(\"").append(operation.getSaveInsertQueryId()).append("\", value);\n"
                            + "        } else {\n"
                            + "            result = getSession().update(\"").append(operation.getQueryId()).append("\", value);\n"
                            + "        }\n"
                            + "        return result;\n");
                    break;
                }
                case SELECT_BY_ID: {
                    appender.append("        ").append(returnTypeName).append(" result = (").append(returnTypeName).append(") getSession().selectOne(\"").append(operation.getQueryId()).append("\", operation.getId());\n"
                            + "        return result;\n");
                    break;
                }
                case UPDATE: {
                    appender.append("        ").append(returnTypeName).append(" result = getSession().update(\"").append(operation.getQueryId()).append("\", operation.getValue());\n"
                            + "        return result;\n");
                    break;
                }
                case CUSTOM_INSERT: {
                    appender.append("        ").append(returnTypeName).append(" result = getSession().insert(\"").append(operation.getQueryId()).append("\", operation);\n"
                            + "        return result;\n");
                    break;
                }
                case CUSTOM_UPDATE: {
                    appender.append("        ").append(returnTypeName).append(" result = getSession().update(\"").append(operation.getQueryId()).append("\", operation);\n"
                            + "        return result;\n");
                    break;
                }
                case CUSTOM_DELETE: {
                    appender.append("        ").append(returnTypeName).append(" result = getSession().delete(\"").append(operation.getQueryId()).append("\", operation);\n"
                            + "        return result;\n");
                    break;
                }
                case CUSTOM_INSERT_WITH_ID: {
                    appender.append("        SqlSession session = getSession();\n"
                            + "        int i = session.insert(\"").append(operation.getQueryId()).append("\", operation);\n"
                            + "        if (i == 1) {\n");
                    
                    FieldInfo idField = operation.getEntity().getCombined().getFirstIdField();
                    InsertedIdOrigin idOrigin = operation.getInsertedIdOrigin();
                    if (idOrigin == InsertedIdOrigin.FIELD) {
                        appender.append("            ").append(returnTypeName).append(" result = ").append("operation.");
                        if (idField.getDataType().isPrimitiveBoolean()) {
                            appender.append("is");
                        } else {
                            appender.append("get");
                        }
                        appender.append(idField.getCapitalizedName()).append("();\n");
                    } else if (idOrigin == InsertedIdOrigin.RETAINED) {
                        appender.append("            ").append(returnTypeName).append(" result = (").append(returnTypeName).append(") RetainIdPlugin.getRetainedId();\n");
                        if (generationInfo.isSetResultingIdInOperationEnabled() && operation.hasReferenceToField(idField)) {
                            appender.append("            operation.set").append(idField.getCapitalizedName()).append("(result);\n");
                        }
                    } else {
                        appender.append("            ").append(returnTypeName).append(" result = (").append(returnTypeName).append(") session.selectOne(\"").append(namespace).append(".lastInsertedIdFor").append(operation.getEntity().getDataType().getSimpleNameWithoutGenerics()).append("\");\n");
                        if (generationInfo.isSetResultingIdInOperationEnabled() && operation.hasReferenceToField(idField)) {
                            appender.append("            operation.set").append(idField.getCapitalizedName()).append("(result);\n");
                        }
                    }
                    appender.append("            return result;\n"
                            + "        } else {\n"
                            + "            throw new IllegalStateException(\"Unable to insert. Operation: \" + operation + \". Insertion result: \" + i);\n"
                            + "        }\n");
                    break;
                }
                case MERGE: {
                    appender.append("        ").append(returnTypeName).append(" result = getSession().update(\"").append(operation.getQueryId()).append("\", operation.getValue());\n"
                            + "        return result;\n");
                    break;
                }
                case COMPLEX_SELECT_CALL: {
                    writeStartOrderByVariable(appender, operation);
                    writeComplexCallBody(appender, operation, "selectOne");
                    writeEndOrderByVariable(appender, operation);
                    break;
                }
                case COMPLEX_INSERT_CALL: {
                    writeComplexCallBody(appender, operation, "insert");
                    break;
                }
                case COMPLEX_UPDATE_CALL: {
                    writeComplexCallBody(appender, operation, "update");
                    break;
                }
                case COMPLEX_DELETE_CALL: {
                    writeComplexCallBody(appender, operation, "delete");
                    break;
                }
                default:
                    throw new AssertionError(operation.getOperationKind().name());
            }

            appender.append("    }\n");
        }

        appender.append("\n");
        if (getExecutorModule().isContainOrderedOperations()) {

            appender.append("    public String translateOrderBy(String orderBy, HashMap<String, String> orderByTranslations) {\n"
                    + "        if (orderBy == null || orderBy.isEmpty()) {\n"
                    + "            return null;\n"
                    + "        }\n"
                    + "        if (orderByTranslations == null || orderByTranslations.isEmpty()) {\n"
                    + "            return null;\n"
                    + "        }\n"
                    + "        orderBy = orderBy.toLowerCase();\n"
                    + "        orderBy = orderBy.replaceAll(\"\\\\s+\", \" \");\n"
                    + "        String[] split = orderBy.split(\"\\\\s*,\\\\s*\");\n"
                    + "\n"
                    + "        StringBuilder sb = new StringBuilder();\n"
                    + "        if (split != null) {\n"
                    + "            for (String s : split) {\n"
                    + "                s = s.trim();\n"
                    + "                String translated = orderByTranslations.get(s);\n"
                    + "                if (translated == null) {\n"
                    + "                    throw new IllegalArgumentException(\"Invalid order by clause: \" + s);\n"
                    + "                }\n"
                    + "                if (!translated.isEmpty()) {\n"
                    + "                    sb.append(translated);\n"
                    + "                    sb.append(\", \");\n"
                    + "                }\n"
                    + "            }\n"
                    + "        }\n"
                    + "\n"
                    + "        if (sb.length() > 2) {\n"
                    + "            return sb.substring(0, sb.length() - 2);\n"
                    + "        } else {\n"
                    + "            return null;\n"
                    + "        }\n"
                    + "    }\n"
                    + "\n");
        }

        appender.append("    public ").append(getClassName()).append("(SqlSessionProvider provider) {\n"
                + "        if (provider == null) {\n"
                + "            throw new IllegalArgumentException(\"provider for the MyBatisOracleCampaignsMapperImpl cannot be null\");\n"
                + "        }\n"
                + "        this.provider = provider;\n"
                + "\n");
        if (useAliasInOrderBy) {
            for (EntityInfo entity : getExecutorModule().getEntities()) {
                if (entity.isUsedInOrderedOperation()) {
                    for (FieldInfo field : entity.getCombined().getFields()) {
                        appender.append("        orderByTranslationsFor").append(entity.getDataType().getSimpleNameWithoutGenerics()).append(".put(\"").append(field.getLowerCaseName()).append("\", \"").append(field.getLowerCaseName()).append("\");\n"
                                + "        orderByTranslationsFor").append(entity.getDataType().getSimpleNameWithoutGenerics()).append(".put(\"").append(field.getLowerCaseName()).append(" asc\", \"").append(field.getLowerCaseName()).append(" asc\");\n"
                                + "        orderByTranslationsFor").append(entity.getDataType().getSimpleNameWithoutGenerics()).append(".put(\"").append(field.getLowerCaseName()).append(" desc\", \"").append(field.getLowerCaseName()).append(" desc\");\n");
                    }
                }
            }
        } else {
            for (EntityInfo entity : getExecutorModule().getEntities()) {
                if (entity.isUsedInOrderedOperation()) {
                    for (FieldInfo field : entity.getCombined().getFields()) {
                        appender.append("        orderByTranslationsFor").append(entity.getDataType().getSimpleNameWithoutGenerics()).append(".put(\"").append(field.getLowerCaseName()).append("\", \"").append(field.getMappedNameOrName()).append("\");\n"
                                + "        orderByTranslationsFor").append(entity.getDataType().getSimpleNameWithoutGenerics()).append(".put(\"").append(field.getLowerCaseName()).append(" asc\", \"").append(field.getMappedNameOrName()).append(" asc\");\n"
                                + "        orderByTranslationsFor").append(entity.getDataType().getSimpleNameWithoutGenerics()).append(".put(\"").append(field.getLowerCaseName()).append(" desc\", \"").append(field.getMappedNameOrName()).append(" desc\");\n");
                    }
                }
            }
        }
        appender.append("    }");
    }

    void writeStartOrderByVariable(Appendable appender, OperationInfo operation) throws IOException {
        if (!operation.isOrdered() || operation.getEntity() == null) {
            return;
        }
        for (FieldInfo field : operation.getFields()) {
            if (field.isOrderBy()) {
                appender.append("        ").append(field.getDataType().getSimpleName()).append(" ").append(field.getName()).append("Old = operation.get").append(field.getCapitalizedName()).append("();\n"
                        + "        operation.set").append(field.getCapitalizedName()).append("(translateOrderBy(").append(field.getName()).append("Old, orderByTranslationsFor").append(operation.getEntity().getDataType().getSimpleName()).append("));\n");
            }
        }
        appender.append("        try {\n");
        indentation = "            ";
    }

    void writeEndOrderByVariable(Appendable appender, OperationInfo operation) throws IOException {
        if (!operation.isOrdered() || operation.getEntity() == null) {
            return;
        }
        appender.append("        } finally {\n");
        for (FieldInfo field : operation.getFields()) {
            if (field.isOrderBy()) {
                appender.append("            operation.set").append(field.getCapitalizedName()).append("(").append(field.getName()).append("Old);\n");
            }
        }
        appender.append("        }\n");
        indentation = "        ";
    }
    
    void writeComplexCallMethodHeader(Appendable appender, OperationInfo operation) throws IOException {
        appender.append("    public ");
        appender.append(operation.getReturnDataType().getSimpleName());
        appender.append(" ");
        appender.append(operation.getMethodName());
        appender.append("(final ");
        appender.append(operation.getDataType().getSimpleName());
        appender.append(" _operation)");
    }
    
    void writeComplexCallBody(Appendable appender, OperationInfo operation, String myBatisMethod) throws IOException {
        String returnTypeName = operation.getReturnDataType().getSimpleName();
        appender.append(indentation).append("class Wrapper {\n");
        appender.append(indentation).append("    ").append(operation.getDataType().getSimpleName()).append(" operation = _operation;\n");
        appender.append(indentation).append("    ").append(returnTypeName).append(" result");
        if (operation.isInitComplexResult()) {
            appender.append(" = new ").append(returnTypeName).append("();\n");
        } else {
            appender.append(";\n");
        }
        
        GenerationInfo generationInfo = getGenerationInfo();
        EntityInfo resultEntity = operation.getEntity();
        if (resultEntity != null) {
            resultEntity = resultEntity.getCombined();
            ArrayList<FieldInfo> resultFields = resultEntity.getFields();
            HashSet<String> processedFields = new HashSet<String>(resultFields.size() + 2);
            processedFields.add("operation");
            processedFields.add("result");
            for (FieldInfo resultField : resultFields) {
                if (processedFields.contains(resultField.getName())) {
                    continue;
                }
                DataTypeInfo fieldDataType = resultField.getDataType();
                FieldInfo operationField = operation.getFieldByName(resultField.getName());
                if (operationField == null) {
                    continue;
                }
                if (!fieldDataType.equals(operationField.getDataType())) {
                    continue;
                }
                
                writeJoinFieldGetterAndSetter(appender, resultField, null, generationInfo);
                processedFields.add(resultField.getName());
            }
            
            for (FieldInfo operationField : operation.getFields()) {
                if (processedFields.contains(operationField.getName())) {
                    continue;
                }
                EntityInfo operationFieldEntity = generationInfo.getEntityByName(operationField.getDataType());
                if (operationFieldEntity == null) {
                    continue;
                }
                operationFieldEntity = operationFieldEntity.getCombined();
             
                for (FieldInfo field : operationFieldEntity.getFields()) {
                    if (processedFields.contains(field.getName())) {
                        continue;
                    }
                    DataTypeInfo fieldDataType = field.getDataType();
                    FieldInfo resultField = resultEntity.getFieldByName(field.getName());
                    if (resultField == null) {
                        continue;
                    }
                    if (!fieldDataType.equals(resultField.getDataType())) {
                        continue;
                    }

                    writeJoinFieldGetterAndSetter(appender, field, operationField, generationInfo);
                    processedFields.add(field.getName());
                }
            }
        }
        appender.append(indentation).append("}\n");
        appender.append(indentation).append("Wrapper wrapper = new Wrapper();\n");
        appender.append(indentation).append("getSession().").append(myBatisMethod).append("(\"").append(operation.getQueryId()).append("\", wrapper);\n");
        appender.append(indentation).append("return wrapper.result;\n");
    }
    
    void writeJoinFieldGetterAndSetter(Appendable appender, FieldInfo field, FieldInfo getter, GenerationInfo generationInfo) throws IOException {
        DataTypeInfo fieldDataType = field.getDataType();
        String getterPrefix = fieldDataType.getGetterPrefix(generationInfo);
        String capitalizedName = field.getCapitalizedName();
        String type = fieldDataType.getSimpleName();
        appender.append(indentation).append("    ");
        appender.append(type).append(" ");
        appender.append(getterPrefix).append(capitalizedName).append("() { return operation.");
        if (getter != null) {
            appender.append(getter.getDataType().getGetterPrefix(generationInfo)).append(getter.getCapitalizedName()).append("().");
        }
        appender.append(getterPrefix).append(capitalizedName).append("(); }\n");

        appender.append(indentation).append("    ");
        appender.append("void set").append(capitalizedName).append("(").append(type).append(" ").append(field.getName()).append(") { result.");
        appender.append("set").append(capitalizedName).append("(").append(field.getName()).append("); }\n");
    }
}
