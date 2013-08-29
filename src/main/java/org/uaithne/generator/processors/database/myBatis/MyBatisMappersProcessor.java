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
package org.uaithne.generator.processors.database.myBatis;

import java.io.IOException;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import org.uaithne.annotations.myBatis.MyBatisCustomSqlStatementId;
import org.uaithne.generator.commons.*;
import org.uaithne.generator.processors.database.QueryGenerator;
import org.uaithne.generator.templates.operations.myBatis.MyBatisTemplate;

public abstract class MyBatisMappersProcessor extends TemplateProcessor {
    
    protected QueryGenerator sqlGenerator;

    public MyBatisMappersProcessor(QueryGenerator sqlGenerator) {
        this.sqlGenerator = sqlGenerator;
    }

    //<editor-fold defaultstate="collapsed" desc="Process">
    public void process(RoundEnvironment re, Element element) {
        TypeElement classElement = (TypeElement) element;

        ExecutorModuleInfo module = getGenerationInfo().getExecutorModuleByRealName(classElement);
        if (module == null) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "For create a myBatis mapper the annotated class must be an operation module", element);
            return;
        }

        String packageName = NamesGenerator.createGenericPackageName(classElement, subPackage());
        String name = mapperPrefix() + module.getNameUpper() + "Mapper";

        String namespace;
        if (packageName == null || packageName.isEmpty()) {
            namespace = name;
        } else {
            namespace = packageName + "." + name;
        }

        Writer writer = null;
        boolean hasUnimplementedOperations = false;
        try {
            FileObject fo = processingEnv.getFiler().createResource(StandardLocation.SOURCE_OUTPUT, packageName, name + ".xml", element);
            writer = fo.openWriter();
            writer.write("<?xml version='1.0' encoding='UTF-8'?>\n");
            writer.write("<!DOCTYPE mapper PUBLIC '-//mybatis.org//DTD Mapper 3.0//EN' 'http://mybatis.org/dtd/mybatis-3-mapper.dtd'>\n");
            writer.write("<mapper namespace='");
            writer.write(namespace);
            writer.write("'>\n\n");
            for (OperationInfo operation : module.getOperations()) {
                processOperation(operation, namespace, writer);
                hasUnimplementedOperations = hasUnimplementedOperations || operation.isManually();
            }
            writer.write("</mapper>");
        } catch (IOException ex) {
            Logger.getLogger(MyBatisMappersProcessor.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(MyBatisMappersProcessor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        processClassTemplate(new MyBatisTemplate(module, packageName, name, namespace, useAliasInOrderBy(), hasUnimplementedOperations), element);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Process operation">
    public void processOperation(OperationInfo operation, String namespace, Writer writer) throws IOException {
        if (operation.isManually()) {
            return;
        }
        MyBatisCustomSqlStatementId myBatisCustomSqlStatement = operation.getAnnotation(MyBatisCustomSqlStatementId.class);
        if (myBatisCustomSqlStatement != null) {
            String statementId = myBatisCustomSqlStatement.value();
            if (statementId.trim().isEmpty()) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "You must provide a valid mybatis statement id", operation.getElement());
            }
            operation.setQueryId(statementId);
            if (operation.getOperationKind() == OperationKind.SELECT_PAGE) {
                String countStatementId = myBatisCustomSqlStatement.countStatementId();
                if (countStatementId.trim().isEmpty()) {
                    processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "You must provide a valid mybatis statement id for count the numbers of the rows", operation.getElement());
                }
                operation.setCountQueryId(countStatementId);
            } else {
                String countStatementId = myBatisCustomSqlStatement.countStatementId();
                if (!countStatementId.isEmpty()) {
                    processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "For this kind of operation is not allowed provide a mybatis statement id for count the numbers of the rows", operation.getElement());
                }
            }
            return;
        }
        EntityInfo entity = operation.getEntity();
        if (entity != null) {
            entity = entity.getCombined();
        }

        switch (operation.getOperationKind()) {
            case CUSTOM: {
            }
            break;
            case SELECT_ONE: {
                operation.setQueryId(namespace + "." + operation.getMethodName());
                String[] query = sqlGenerator.getSelectOneQuery(operation);
                if (query != null) {
                    writeSelect(writer,
                            operation.getMethodName(),
                            operation.getDataType().getQualifiedNameWithoutGenerics(),
                            operation.getReturnDataType().getQualifiedNameWithoutGenerics(),
                            query);
                }
            }
            break;
            case SELECT_MANY: {
                operation.setQueryId(namespace + "." + operation.getMethodName());
                String[] query = sqlGenerator.getSelectManyQuery(operation);
                if (query != null) {
                    writeSelect(writer,
                            operation.getMethodName(),
                            operation.getDataType().getQualifiedNameWithoutGenerics(),
                            operation.getOneItemReturnDataType().getQualifiedNameWithoutGenerics(),
                            query);
                }
            }
            break;
            case SELECT_PAGE: {
                operation.setQueryId(namespace + "." + operation.getMethodName() + "Page");
                operation.setCountQueryId(namespace + "." + operation.getMethodName() + "Count");
                String[] query = sqlGenerator.getSelectPageQuery(operation);
                if (query != null) {
                    writeSelect(writer,
                            operation.getMethodName() + "Page",
                            operation.getDataType().getQualifiedNameWithoutGenerics(),
                            operation.getOneItemReturnDataType().getQualifiedNameWithoutGenerics(),
                            query);
                }
                query = sqlGenerator.getSelectPageCountQuery(operation);
                if (query != null) {
                    writeSelect(writer,
                            operation.getMethodName() + "Count",
                            operation.getDataType().getQualifiedNameWithoutGenerics(),
                            DataTypeInfo.PAGE_INFO_DATA_TYPE.getQualifiedNameWithoutGenerics(),
                            query);
                }
            }
            break;
            case DELETE_BY_ID: {
                if (entity == null) {
                    processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Unable to find the entity related to the operation", operation.getElement());
                    break;
                }
                String[] query = sqlGenerator.getEntityDeleteByIdQuery(entity, operation);
                if (query != null) {
                    writeDelete(writer,
                            operation.getMethodName(),
                            entity.getFirstIdField().getDataType().getQualifiedNameWithoutGenerics(),
                            query);
                }
            }
            break;
            case INSERT: {
                if (entity == null) {
                    processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Unable to find the entity related to the operation", operation.getElement());
                    break;
                }
                String[] query = sqlGenerator.getEntityLastInsertedIdQuery(entity, operation);
                if (query != null) {
                    writeSelectWithoutParameter(writer,
                            "lastInsertedIdFor" + entity.getDataType().getSimpleNameWithoutGenerics(),
                            operation.getReturnDataType().getQualifiedNameWithoutGenerics(),
                            query);
                }
                query = sqlGenerator.getEntityInsertQuery(entity, operation);
                if (query != null) {
                    writeInsert(writer,
                            operation.getMethodName(),
                            entity.getDataType().getQualifiedNameWithoutGenerics(),
                            query);
                }
            }
            break;
            case JUST_INSERT: {
            }
            break;
            case SAVE: {
            }
            break;
            case JUST_SAVE: {
            }
            break;
            case SELECT_BY_ID: {
                if (entity == null) {
                    processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Unable to find the entity related to the operation", operation.getElement());
                    break;
                }
                String[] query = sqlGenerator.getEntitySelectByIdQuery(entity, operation);
                if (query != null) {
                    writeSelect(writer,
                            operation.getMethodName(),
                            entity.getFirstIdField().getDataType().getQualifiedNameWithoutGenerics(),
                            operation.getReturnDataType().getQualifiedNameWithoutGenerics(),
                            query);
                }
            }
            break;
            case UPDATE: {
                if (entity == null) {
                    processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Unable to find the entity related to the operation", operation.getElement());
                    break;
                }
                String[] query = sqlGenerator.getEntityUpdateQuery(entity, operation);
                if (query != null) {
                    writeUpdate(writer,
                            operation.getMethodName(),
                            entity.getDataType().getQualifiedNameWithoutGenerics(),
                            query);
                }
            }
            break;
            case CUSTOM_INSERT: {
                operation.setQueryId(namespace + "." + operation.getMethodName());
                String[] query = sqlGenerator.getCustomInsertQuery(operation);
                if (query != null) {
                    writeInsert(writer,
                            operation.getMethodName(),
                            operation.getDataType().getQualifiedNameWithoutGenerics(),
                            query);
                }
            }
            break;
            case CUSTOM_UPDATE: {
                operation.setQueryId(namespace + "." + operation.getMethodName());
                String[] query = sqlGenerator.getCustomUpdateQuery(operation);
                if (query != null) {
                    writeUpdate(writer,
                            operation.getMethodName(),
                            operation.getDataType().getQualifiedNameWithoutGenerics(),
                            query);
                }
            }
            break;
            case CUSTOM_DELETE: {
                operation.setQueryId(namespace + "." + operation.getMethodName());
                String[] query = sqlGenerator.getCustomDeleteQuery(operation);
                if (query != null) {
                    writeDelete(writer,
                            operation.getMethodName(),
                            operation.getDataType().getQualifiedNameWithoutGenerics(),
                            query);
                }
            }
            break;
            case CUSTOM_INSERT_WITH_ID: {
                operation.setQueryId(namespace + "." + operation.getMethodName());
                String[] query = sqlGenerator.getCustomInsertQuery(operation);
                if (query != null) {
                    writeInsert(writer,
                            operation.getMethodName(),
                            operation.getDataType().getQualifiedNameWithoutGenerics(),
                            query);
                }
            }
            break;
            case MERGE: {
                if (entity == null) {
                    processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Unable to find the entity related to the operation", operation.getElement());
                    break;
                }
                String[] query = sqlGenerator.getEntityMergeQuery(entity, operation);
                if (query != null) {
                    writeUpdate(writer,
                            operation.getMethodName(),
                            entity.getDataType().getQualifiedNameWithoutGenerics(),
                            query);
                }
            }
            break;
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Write xml entries">
    public void writeUpdate(Writer writer, String id, String parameterType, String[] lines) throws IOException {
        writer.write("    <update id='");
        writer.write(id);
        writer.write("' parameterType='");
        writer.write(parameterType);
        writer.write("'>\n");
        if (lines != null) {
            for (int i = 0; i < lines.length; i++) {
                writer.write("        ");
                writer.write(lines[i]);
                writer.write("\n");
            }
        }
        writer.write("    </update>\n\n");
    }

    public void writeSelect(Writer writer, String id, String parameterType, String resultType, String[] lines) throws IOException {
        writer.write("    <select id='");
        writer.write(id);
        writer.write("' parameterType='");
        writer.write(parameterType);
        writer.write("' resultType='");
        writer.write(resultType);
        writer.write("'>\n");
        if (lines != null) {
            for (int i = 0; i < lines.length; i++) {
                writer.write("        ");
                writer.write(lines[i]);
                writer.write("\n");
            }
        }
        writer.write("    </select>\n\n");
    }

    public void writeSelectWithoutParameter(Writer writer, String id, String resultType, String[] lines) throws IOException {
        writer.write("    <select id='");
        writer.write(id);
        writer.write("' resultType='");
        writer.write(resultType);
        writer.write("'>\n");
        if (lines != null) {
            for (int i = 0; i < lines.length; i++) {
                writer.write("        ");
                writer.write(lines[i]);
                writer.write("\n");
            }
        }
        writer.write("    </select>\n\n");
    }

    public void writeInsert(Writer writer, String id, String parameterType, String[] lines) throws IOException {
        writer.write("    <insert id='");
        writer.write(id);
        writer.write("' parameterType='");
        writer.write(parameterType);
        writer.write("'>\n");
        if (lines != null) {
            for (int i = 0; i < lines.length; i++) {
                writer.write("        ");
                writer.write(lines[i]);
                writer.write("\n");
            }
        }
        writer.write("    </insert>\n\n");
    }

    public void writeDelete(Writer writer, String id, String parameterType, String[] lines) throws IOException {
        writer.write("    <delete id='");
        writer.write(id);
        writer.write("' parameterType='");
        writer.write(parameterType);
        writer.write("'>\n");
        if (lines != null) {
            for (int i = 0; i < lines.length; i++) {
                writer.write("        ");
                writer.write(lines[i]);
                writer.write("\n");
            }
        }
        writer.write("    </delete>\n\n");
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Configuration">
    public abstract boolean useAliasInOrderBy();
    public abstract String subPackage();
    public abstract String mapperPrefix();
    //</editor-fold>
}
