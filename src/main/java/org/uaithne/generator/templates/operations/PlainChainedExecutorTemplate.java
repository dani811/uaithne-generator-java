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
package org.uaithne.generator.templates.operations;

import java.io.IOException;
import java.util.ArrayList;
import org.uaithne.generator.commons.DataTypeInfo;
import static org.uaithne.generator.commons.DataTypeInfo.*;
import org.uaithne.generator.commons.ExecutorModuleInfo;
import org.uaithne.generator.commons.FieldInfo;
import org.uaithne.generator.commons.OperationInfo;
import org.uaithne.generator.commons.OperationKind;
import static org.uaithne.generator.templates.ClassTemplate.getGenerationInfo;

public class PlainChainedExecutorTemplate extends ExecutorModuleTemplate {

    public PlainChainedExecutorTemplate(ExecutorModuleInfo executorModule, String packageName, boolean usePlainInterface) {
        setPackageName(packageName);
        executorModule.appendPlainImplementationImports(packageName, getImport());
        setClassName(executorModule.getNameUpper() + "PlainChainedExecutor");
        if (getGenerationInfo().isExecutorExtendsExecutorGroup()) {
            addImport(DataTypeInfo.EXECUTOR_GROUP_DATA_TYPE, packageName);
        }
        if (usePlainInterface) {
            String executorInterfaceName = executorModule.getNameUpper() + "PlainExecutor";
            addImplement(executorInterfaceName);
        }
        setExecutorModule(executorModule);
        if (HAS_CONTEXT) {
            setAbstract(true);
            addContextImport(packageName);
        }
    }

    @Override
    protected void writeContent(Appendable appender) throws IOException {
        String executorInterfaceName;
        if (getGenerationInfo().isExecutorExtendsExecutorGroup()) {
            executorInterfaceName = "ExecutorGroup";
        } else {
            executorInterfaceName = getExecutorModule().getExecutorInterfaceName();
        }
        appender.append("    private ").append(executorInterfaceName).append(" chainedExecutor;\n"
                + "\n"
                + "    public ").append(executorInterfaceName).append(" getChainedExecutor() {\n"
                + "        return chainedExecutor;\n"
                + "    }\n"
                + "\n");
        
        String context;
        if (HAS_CONTEXT) {
            appender.append("    protected abstract ").append(getGenerationInfo().getContextParameterType().getSimpleName()).append(" getContext();\n\n");
            context = ", getContext()";
        } else {
            context = "";
        }

        for (OperationInfo operation : getExecutorModule().getOperations()) {
            ArrayList<FieldInfo> allFields = operation.getFields();
            ArrayList<FieldInfo> fields = new ArrayList<FieldInfo>(allFields.size());
            ArrayList<FieldInfo> mandatoryFields = new ArrayList<FieldInfo>(allFields.size());
            ArrayList<FieldInfo> optionalFields = new ArrayList<FieldInfo>(allFields.size());
            for (FieldInfo field : allFields) {
                if (field.isExcludedFromObject()) {
                    continue;
                }
                if (field.isSelectPageField()) {
                    continue;
                }
                fields.add(field);
                if (field.isOptional() || field.isExcludedFromConstructor()) {
                    optionalFields.add(field);
                } else {
                    mandatoryFields.add(field);
                }
            }
            
            if (operation.getOperationKind() != OperationKind.SELECT_PAGE) {
                appender.append("    public ").append(operation.getReturnDataType().getSimpleName()).append(" ").append(operation.getMethodName()).append("(");
                writeArguments(appender, mandatoryFields);
                appender.append(") {\n"
                        + "        ").append(operation.getDataType().getSimpleName()).append(" operation__instance = new ").append(operation.getDataType().getSimpleName()).append("(");
                writeCallArguments(appender, mandatoryFields);
                appender.append(");\n"
                        + "        return chainedExecutor.execute(operation__instance").append(context).append(");\n"
                        + "    }\n"
                        + "\n");

                if (!optionalFields.isEmpty()) {
                    appender.append("    public ").append(operation.getReturnDataType().getSimpleName()).append(" ").append(operation.getMethodName()).append("WithOptionals(");
                    writeArguments(appender, fields);
                    appender.append(") {\n"
                            + "        ").append(operation.getDataType().getSimpleName()).append(" operation__instance = new ").append(operation.getDataType().getSimpleName()).append("(");
                    writeCallArguments(appender, mandatoryFields);
                    appender.append(");\n");
                    for (FieldInfo field : optionalFields) {
                        appender.append("        operation__instance.set").append(field.getCapitalizedName()).append("(").append(field.getName()).append(");\n");
                    }
                    appender.append("        return chainedExecutor.execute(operation__instance").append(context).append(");\n"
                            + "    }\n"
                            + "\n");
                }
            } else {
                appender.append("    public ").append(PAGE_INFO_DATA).append(" ").append(operation.getMethodName()).append("Count(");
                writeArguments(appender, mandatoryFields);
                appender.append(") {\n"
                        + "        ").append(operation.getDataType().getSimpleName()).append(" operation__instance = new ").append(operation.getDataType().getSimpleName()).append("(");
                writeCallArgumentsForAddMore(appender, mandatoryFields);
                appender.append("true);\n"
                        + "        ").append(operation.getReturnDataType().getSimpleName()).append(" operation__result = chainedExecutor.execute(operation__instance").append(context).append(");\n"
                        + "        if (operation__result != null) {\n"
                        + "            return operation__result.getDataCount();\n"
                        + "        } else {\n"
                        + "            return null;\n"
                        + "        }\n"
                        + "    }\n"
                        + "\n");

                appender.append("    public ").append(LIST_DATA).append("<").append(operation.getOneItemReturnDataType().getSimpleName()).append("> ").append(operation.getMethodName()).append("(");
                writeArgumentsForAddMore(appender, mandatoryFields);
                appender.append(PAGE_INFO_DATA).append(" limit) {\n"
                        + "        ").append(operation.getDataType().getSimpleName()).append(" operation__instance = new ").append(operation.getDataType().getSimpleName()).append("(");
                writeCallArgumentsForAddMore(appender, mandatoryFields);
                appender.append("limit, null, ").append(PAGE_INFO_ZERO).append(");\n"
                        + "        ").append(operation.getReturnDataType().getSimpleName()).append(" operation__result = chainedExecutor.execute(operation__instance").append(context).append(");\n"
                        + "        if (operation__result != null) {\n"
                        + "            return operation__result.getData();\n"
                        + "        } else {\n"
                        + "            return null;\n"
                        + "        }\n"
                        + "    }\n"
                        + "\n");

                appender.append("    public ").append(LIST_DATA).append("<").append(operation.getOneItemReturnDataType().getSimpleName()).append("> ").append(operation.getMethodName()).append("(");
                writeArgumentsForAddMore(appender, mandatoryFields);
                appender.append(PAGE_INFO_DATA).append(" limit, ").append(PAGE_INFO_DATA).append(" offset) {\n"
                        + "        ").append(operation.getDataType().getSimpleName()).append(" operation__instance = new ").append(operation.getDataType().getSimpleName()).append("(");
                writeCallArgumentsForAddMore(appender, mandatoryFields);
                appender.append("limit, offset, ").append(PAGE_INFO_ZERO).append(");\n"
                        + "        ").append(operation.getReturnDataType().getSimpleName()).append(" operation__result = chainedExecutor.execute(operation__instance").append(context).append(");\n"
                        + "        if (operation__result != null) {\n"
                        + "            return operation__result.getData();\n"
                        + "        } else {\n"
                        + "            return null;\n"
                        + "        }\n"
                        + "    }\n"
                        + "\n");

                if (!optionalFields.isEmpty()) {
                    appender.append("    public ").append(PAGE_INFO_DATA).append(" ").append(operation.getMethodName()).append("CountWithOptionals(");
                    writeArguments(appender, fields);
                    appender.append(") {\n"
                            + "        ").append(operation.getDataType().getSimpleName()).append(" operation__instance = new ").append(operation.getDataType().getSimpleName()).append("(");
                    writeCallArgumentsForAddMore(appender, mandatoryFields);
                    appender.append("true);\n");
                    for (FieldInfo field : optionalFields) {
                        appender.append("        operation__instance.set").append(field.getCapitalizedName()).append("(").append(field.getName()).append(");\n");
                    }
                    appender.append("        ").append(operation.getReturnDataType().getSimpleName()).append(" operation__result = chainedExecutor.execute(operation__instance").append(context).append(");\n"
                            + "        if (operation__result != null) {\n"
                            + "            return operation__result.getDataCount();\n"
                            + "        } else {\n"
                            + "            return null;\n"
                            + "        }\n"
                            + "    }\n"
                            + "\n");

                    appender.append("    public ").append(LIST_DATA).append("<").append(operation.getOneItemReturnDataType().getSimpleName()).append("> ").append(operation.getMethodName()).append("WithOptionals(");
                    writeArgumentsForAddMore(appender, fields);
                    appender.append(PAGE_INFO_DATA).append(" limit) {\n"
                            + "        ").append(operation.getDataType().getSimpleName()).append(" operation__instance = new ").append(operation.getDataType().getSimpleName()).append("(");
                    writeCallArgumentsForAddMore(appender, mandatoryFields);
                    appender.append("limit, null, ").append(PAGE_INFO_ZERO).append(");\n");
                    for (FieldInfo field : optionalFields) {
                        appender.append("        operation__instance.set").append(field.getCapitalizedName()).append("(").append(field.getName()).append(");\n");
                    }
                    appender.append("        ").append(operation.getReturnDataType().getSimpleName()).append(" operation__result = chainedExecutor.execute(operation__instance").append(context).append(");\n"
                            + "        if (operation__result != null) {\n"
                            + "            return operation__result.getData();\n"
                            + "        } else {\n"
                            + "            return null;\n"
                            + "        }\n"
                            + "    }\n"
                            + "\n");

                    appender.append("    public ").append(LIST_DATA).append("<").append(operation.getOneItemReturnDataType().getSimpleName()).append("> ").append(operation.getMethodName()).append("WithOptionals(");
                    writeArgumentsForAddMore(appender, fields);
                    appender.append(PAGE_INFO_DATA).append(" limit, ").append(PAGE_INFO_DATA).append(" offset) {\n"
                            + "        ").append(operation.getDataType().getSimpleName()).append(" operation__instance = new ").append(operation.getDataType().getSimpleName()).append("(");
                    writeCallArgumentsForAddMore(appender, mandatoryFields);
                    appender.append("limit, offset, ").append(PAGE_INFO_ZERO).append(");\n");
                    for (FieldInfo field : optionalFields) {
                        appender.append("        operation__instance.set").append(field.getCapitalizedName()).append("(").append(field.getName()).append(");\n");
                    }
                    appender.append("        ").append(operation.getReturnDataType().getSimpleName()).append(" operation__result = chainedExecutor.execute(operation__instance").append(context).append(");\n"
                            + "        if (operation__result != null) {\n"
                            + "            return operation__result.getData();\n"
                            + "        } else {\n"
                            + "            return null;\n"
                            + "        }\n"
                            + "    }\n"
                            + "\n");
                }
            }
        } // end for each operation

        appender.append("    public ").append(getClassName()).append("(").append(executorInterfaceName).append(" chainedExecutor) {\n"
                + "        if (chainedExecutor == null) {\n"
                + "            throw new IllegalArgumentException(\"chainedExecutor for the ").append(getClassName()).append(" cannot be null\");\n"
                + "        }\n"
                + "        this.chainedExecutor = chainedExecutor;\n"
                + "    }");
    }
}
