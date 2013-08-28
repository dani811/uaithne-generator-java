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
import static org.uaithne.generator.commons.DataTypeInfo.*;
import org.uaithne.generator.commons.ExecutorModuleInfo;
import org.uaithne.generator.commons.FieldInfo;
import org.uaithne.generator.commons.OperationInfo;
import org.uaithne.generator.commons.OperationKind;

public class PlainChainedGroupingExecutorTemplate extends ExecutorModuleTemplate {

    private String executorInterfaceName;

    public String getExecutorInterfaceName() {
        return executorInterfaceName;
    }

    public void setExecutorInterfaceName(String executorInterfaceName) {
        this.executorInterfaceName = executorInterfaceName;
    }

    public PlainChainedGroupingExecutorTemplate(ExecutorModuleInfo executorModule, String packageName) {
        executorInterfaceName = executorModule.getNameUpper() + "PlainExecutor";
        setPackageName(packageName);
        executorModule.appendPlainImplementationImports(packageName, getImport());
        setClassName(executorModule.getNameUpper() + "PlainChainedGroupingExecutor");
        addImplement(executorInterfaceName);
        setExecutorModule(executorModule);
    }

    @Override
    protected void writeContent(Appendable appender) throws IOException {
        appender.append("    private ExecutorGroup chainedExecutorGroup;\n"
                + "\n"
                + "    public ExecutorGroup getChainedExecutorGroup() {\n"
                + "        return chainedExecutorGroup;\n"
                + "    }\n");

        for (OperationInfo operation : getExecutorModule().getOperations()) {
            ArrayList<FieldInfo> fields = operation.getFields();
            ArrayList<FieldInfo> mandatoryFields = new ArrayList<FieldInfo>(fields.size());
            ArrayList<FieldInfo> optionalFields = new ArrayList<FieldInfo>(fields.size());
            for (FieldInfo field : fields) {
                if (field.isOptional()) {
                    optionalFields.add(field);
                } else {
                    mandatoryFields.add(field);
                }
            }
            
            if (operation.getOperationKind() != OperationKind.SELECT_PAGE) {
                appender.append("    public ").append(operation.getReturnDataType().getSimpleName()).append(" ").append(operation.getMethodName()).append(" (");
                writeArguments(appender, mandatoryFields);
                appender.append(") {\n"
                        + "        ").append(operation.getDataType().getSimpleName()).append(" operation__instance = new ").append(operation.getDataType().getSimpleName()).append("(");
                writeCallArguments(appender, mandatoryFields);
                appender.append(");\n"
                        + "        return chainedExecutorGroup.").append(operation.getMethodName()).append("(operation__instance);\n"
                        + "}\n"
                        + "\n");

                if (!optionalFields.isEmpty()) {
                    appender.append("    public ").append(operation.getReturnDataType().getSimpleName()).append(" ").append(operation.getMethodName()).append("WithOptionals (");
                    writeArguments(appender, fields);
                    appender.append(") {\n"
                            + "        ").append(operation.getDataType().getSimpleName()).append(" operation__instance = new ").append(operation.getDataType().getSimpleName()).append("(");
                    writeCallArguments(appender, mandatoryFields);
                    appender.append(");\n");
                    for (FieldInfo field : optionalFields) {
                        appender.append("operation__instance.set").append(field.getCapitalizedName()).append("(").append(field.getName()).append(");");
                    }
                    appender.append("        return chainedExecutorGroup.").append(operation.getMethodName()).append("(operation__instance);\n"
                            + "}\n"
                            + "\n");
                }
            } else {
                appender.append("    public ").append(PAGE_INFO_DATA).append(" ").append(operation.getMethodName()).append("Count (");
                writeArguments(appender, mandatoryFields);
                appender.append(") {\n"
                        + "        ").append(operation.getDataType().getSimpleName()).append(" operation__instance = new ").append(operation.getDataType().getSimpleName()).append("(");
                writeCallArgumentsForAddMore(appender, mandatoryFields);
                appender.append("true);\n"
                        + "        ").append(operation.getReturnDataType().getSimpleName()).append(" operation__result = chainedExecutorGroup.").append(operation.getMethodName()).append("(operation__instance);\n"
                        + "        if (operation__result != null) {\n"
                        + "            return operation__result.getDataCount();\n"
                        + "        } else {\n"
                        + "            return null;\n"
                        + "        }\n"
                        + "}\n"
                        + "\n");

                appender.append("    public ").append(LIST_DATA).append("<").append(operation.getOneItemReturnDataType().getSimpleName()).append("> ").append(operation.getMethodName()).append(" (");
                writeArgumentsForAddMore(appender, mandatoryFields);
                appender.append(PAGE_INFO_DATA).append("limit) {\n"
                        + "        ").append(operation.getDataType().getSimpleName()).append(" operation__instance = new ").append(operation.getDataType().getSimpleName()).append("(");
                writeCallArgumentsForAddMore(appender, mandatoryFields);
                appender.append("limit);\n"
                        + "        ").append(operation.getReturnDataType().getSimpleName()).append(" operation__result = chainedExecutorGroup.").append(operation.getMethodName()).append("(operation__instance);\n"
                        + "        if (operation__result != null) {\n"
                        + "            return operation__result.getData();\n"
                        + "        } else {\n"
                        + "            return null;\n"
                        + "        }\n"
                        + "}\n"
                        + "\n");

                appender.append("    public ").append(LIST_DATA).append("<").append(operation.getOneItemReturnDataType().getSimpleName()).append("> ").append(operation.getMethodName()).append(" (");
                writeArgumentsForAddMore(appender, mandatoryFields);
                appender.append(PAGE_INFO_DATA).append("limit, ").append(PAGE_INFO_DATA).append(" offset) {\n"
                        + "        ").append(operation.getDataType().getSimpleName()).append(" operation__instance = new ").append(operation.getDataType().getSimpleName()).append("(");
                writeCallArgumentsForAddMore(appender, mandatoryFields);
                appender.append("limit, offset);\n"
                        + "        ").append(operation.getReturnDataType().getSimpleName()).append(" operation__result = chainedExecutorGroup.").append(operation.getMethodName()).append("(operation__instance);\n"
                        + "        if (operation__result != null) {\n"
                        + "            return operation__result.getData();\n"
                        + "        } else {\n"
                        + "            return null;\n"
                        + "        }\n"
                        + "}\n"
                        + "\n");

                if (!optionalFields.isEmpty()) {
                    appender.append("    public ").append(PAGE_INFO_DATA).append(" ").append(operation.getMethodName()).append("CountWithOptionals (");
                    writeArguments(appender, fields);
                    appender.append(") {\n"
                            + "        ").append(operation.getDataType().getSimpleName()).append(" operation__instance = new ").append(operation.getDataType().getSimpleName()).append("(");
                    writeCallArgumentsForAddMore(appender, mandatoryFields);
                    appender.append("true);\n");
                    for (FieldInfo field : optionalFields) {
                        appender.append("operation__instance.set").append(field.getCapitalizedName()).append("(").append(field.getName()).append(");");
                    }
                    appender.append("        ").append(operation.getReturnDataType().getSimpleName()).append(" operation__result = chainedExecutorGroup.").append(operation.getMethodName()).append("(operation__instance);\n"
                            + "        if (operation__result != null) {\n"
                            + "            return operation__result.getDataCount();\n"
                            + "        } else {\n"
                            + "            return null;\n"
                            + "        }\n"
                            + "}\n"
                            + "\n");

                    appender.append("    public ").append(LIST_DATA).append("<").append(operation.getOneItemReturnDataType().getSimpleName()).append("> ").append(operation.getMethodName()).append("WithOptionals (");
                    writeArgumentsForAddMore(appender, fields);
                    appender.append(PAGE_INFO_DATA).append("limit) {\n"
                            + "        ").append(operation.getDataType().getSimpleName()).append(" operation__instance = new ").append(operation.getDataType().getSimpleName()).append("(");
                    writeCallArgumentsForAddMore(appender, mandatoryFields);
                    appender.append("limit);\n");
                    for (FieldInfo field : optionalFields) {
                        appender.append("operation__instance.set").append(field.getCapitalizedName()).append("(").append(field.getName()).append(");");
                    }
                    appender.append("        ").append(operation.getReturnDataType().getSimpleName()).append(" operation__result = chainedExecutorGroup.").append(operation.getMethodName()).append("(operation__instance);\n"
                            + "        if (operation__result != null) {\n"
                            + "            return operation__result.getData();\n"
                            + "        } else {\n"
                            + "            return null;\n"
                            + "        }\n"
                            + "}\n"
                            + "\n");

                    appender.append("    public ").append(LIST_DATA).append("<").append(operation.getOneItemReturnDataType().getSimpleName()).append("> ").append(operation.getMethodName()).append("WithOptionals (");
                    writeArgumentsForAddMore(appender, fields);
                    appender.append(PAGE_INFO_DATA).append("limit, ").append(PAGE_INFO_DATA).append(" offset) {\n"
                            + "        ").append(operation.getDataType().getSimpleName()).append(" operation__instance = new ").append(operation.getDataType().getSimpleName()).append("(");
                    writeCallArguments(appender, mandatoryFields);
                    appender.append("limit, offset);\n");
                    for (FieldInfo field : optionalFields) {
                        appender.append("operation__instance.set").append(field.getCapitalizedName()).append("(").append(field.getName()).append(");");
                    }
                    appender.append("        ").append(operation.getReturnDataType().getSimpleName()).append(" operation__result = chainedExecutorGroup.").append(operation.getMethodName()).append("(operation__instance);\n"
                            + "        if (operation__result != null) {\n"
                            + "            return operation__result.getData();\n"
                            + "        } else {\n"
                            + "            return null;\n"
                            + "        }\n"
                            + "}\n"
                            + "\n");
                }
            }
        } // end for each operation

        appender.append("    public ").append(getClassName()).append("(ExecutorGroup chainedExecutorGroup) {\n"
                + "        if (chainedExecutorGroup == null) {\n"
                + "            throw new IllegalArgumentException(\"chainedExecutorGroup for the ").append(getClassName()).append(" cannot be null\");\n"
                + "        }\n"
                + "        this.chainedExecutorGroup = chainedExecutorGroup;\n"
                + "    }");
    }
}
