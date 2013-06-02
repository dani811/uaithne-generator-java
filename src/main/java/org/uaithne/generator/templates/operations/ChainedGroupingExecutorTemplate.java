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
import org.uaithne.generator.commons.ExecutorModuleInfo;
import org.uaithne.generator.commons.OperationInfo;

public class ChainedGroupingExecutorTemplate extends ExecutorModuleTemplate {

    public ChainedGroupingExecutorTemplate(ExecutorModuleInfo executorModule, String packageName, String sharedPackageDot) {
        setPackageName(packageName);
        executorModule.appendDefinitionImports(packageName, getImport());
        addImport(sharedPackageDot + "ExecutorGroup", packageName);
        addImport(sharedPackageDot + "Operation", packageName);
        setClassName(executorModule.getNameUpper() + "ChainedGroupingExecutor");
        addImplement(executorModule.getExecutorInterfaceName());
        setExecutorModule(executorModule);
    }

    @Override
    protected void writeContent(Appendable appender) throws IOException {
        appender.append("    private ExecutorGroup chainedExecutorGroup;\n"
                + "\n");
        writeGetExecutorSelector(appender);
        appender.append("\n"
                + "    public ExecutorGroup getChainedExecutorGroup() {\n"
                + "        return chainedExecutorGroup;\n"
                + "    }\n"
                + "\n"
                + "    @Override\n"
                + "    public ").append(OPERATION_BASE_DEFINITION).append(" RESULT execute(OPERATION operation) {\n"
                + "        return operation.execute(this);\n"
                + "    }\n"
                + "\n"
                + "    @Override\n"
                + "    public ").append(OPERATION_BASE_DEFINITION).append(" RESULT executeOther(OPERATION operation) {\n"
                + "        return chainedExecutorGroup.execute(operation);\n"
                + "    }\n");

        for (OperationInfo operation : getExecutorModule().getOperations()) {
            appender.append("\n"
                    + "    @Override\n");
            writeOperationMethodHeader(appender, operation);
            appender.append(" {\n"
                    + "        return chainedExecutorGroup.execute(operation);\n"
                    + "    }\n");
        }

        appender.append("\n"
                + "    public ").append(getClassName()).append("(ExecutorGroup chainedExecutorGroup) {\n"
                + "        if (chainedExecutorGroup == null) {\n"
                + "            throw new IllegalArgumentException(\"chainedExecutorGroup for the ").append(getClassName()).append(" cannot be null\");\n"
                + "        }\n"
                + "        this.chainedExecutorGroup = chainedExecutorGroup;\n"
                + "    }");
    }
}
