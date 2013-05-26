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
import static org.uaithne.generator.commons.DataTypeInfo.*;
import org.uaithne.generator.commons.ExecutorModuleInfo;
import org.uaithne.generator.commons.OperationInfo;
import org.uaithne.generator.commons.OperationKind;

public class PlainExecutorTemplate extends ExecutorModuleTemplate {

    public PlainExecutorTemplate(ExecutorModuleInfo executorModule, String packageName) {
        setPackageName(packageName);
        executorModule.appendPlainDefinitionImports(packageName, getImport());
        setClassName(executorModule.getNameUpper() + "PlainExecutor");
        setExecutorModule(executorModule);
        setInterface(true);
    }

    @Override
    protected void writeContent(Appendable appender) throws IOException {
        for (OperationInfo operation : getExecutorModule().getOperations()) {
            if (operation.getOperationKind() != OperationKind.SELECT_PAGE) {
                appender.append("    public ").append(operation.getReturnDataType().getSimpleName()).append(" ").append(operation.getMethodName()).append(" (");
                writeArguments(appender, operation.getMandatoryFields());
                appender.append(");\n");
                if (!operation.getOptionalFields().isEmpty()) {
                    appender.append("    public ").append(operation.getReturnDataType().getSimpleName()).append(" ").append(operation.getMethodName()).append("WithOptionals (");
                    writeArguments(appender, operation.getFields());
                    appender.append(");\n");
                }
            } else {
                appender.append("    public ").append(PAGE_INFO_DATA).append(" ").append(operation.getMethodName()).append("Count (");
                writeArguments(appender, operation.getMandatoryFields());
                appender.append(");\n");
                appender.append("    public ").append(LIST_DATA).append("<").append(operation.getOneItemReturnDataType().getSimpleName()).append("> ").append(operation.getMethodName()).append(" (");
                writeArgumentsForAddMore(appender, operation.getMandatoryFields());
                appender.append(PAGE_INFO_DATA).append("limit);\n");
                appender.append("    public ").append(LIST_DATA).append("<").append(operation.getOneItemReturnDataType().getSimpleName()).append("> ").append(operation.getMethodName()).append(" (");
                writeArgumentsForAddMore(appender, operation.getMandatoryFields());
                appender.append(PAGE_INFO_DATA).append("limit, ").append(PAGE_INFO_DATA).append(" offset);\n");
                if (!operation.getOptionalFields().isEmpty()) {
                    appender.append("    public ").append(PAGE_INFO_DATA).append(" ").append(operation.getMethodName()).append("CountWithOptionals (");
                    writeArguments(appender, operation.getFields());
                    appender.append(");\n");
                    appender.append("    public ").append(LIST_DATA).append("<").append(operation.getOneItemReturnDataType().getSimpleName()).append("> ").append(operation.getMethodName()).append("WithOptionals (");
                    writeArgumentsForAddMore(appender, operation.getFields());
                    appender.append(PAGE_INFO_DATA).append("limit);\n");
                    appender.append("    public ").append(LIST_DATA).append("<").append(operation.getOneItemReturnDataType().getSimpleName()).append("> ").append(operation.getMethodName()).append("WithOptionals (");
                    writeArgumentsForAddMore(appender, operation.getFields());
                    appender.append(PAGE_INFO_DATA).append("limit, ").append(PAGE_INFO_DATA).append(" offset);\n");
                }
            }
        } // end for each operation
    }
}
