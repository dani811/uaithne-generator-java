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
import org.uaithne.generator.templates.WithFieldsTemplate;

public abstract class ExecutorModuleTemplate extends WithFieldsTemplate {

    private ExecutorModuleInfo executorModule;

    public ExecutorModuleInfo getExecutorModule() {
        return executorModule;
    }

    public void setExecutorModule(ExecutorModuleInfo executorModule) {
        this.executorModule = executorModule;
    }

    protected void writeGetExecutorSelector(Appendable appender) throws IOException {
        appender.append("    @Override\n"
                + "    public Object getExecutorSelector() {\n"
                + "        return ").append(getExecutorModule().getExecutorInterfaceName()).append(".SELECTOR;\n"
                + "    }\n");
    }
    
    protected void writeExecuteMethods(Appendable appender, boolean setContext) throws IOException {
        String contextNewLine;
        String context;
        if (HAS_CONTEXT) {
            contextNewLine = " +\n"
                + "                    \"Context: \" + context";
            context = "+ \". Context: \" + context";
        } else {
            contextNewLine = "";
            context = "";
        }
        appender.append("    @Override\n"
            + "    public ").append(OPERATION_BASE_DEFINITION).append(" RESULT execute(OPERATION operation").append(CONTEXT_PARAM).append(") {\n");
        if (getGenerationInfo().isExecutorExtendsExecutorGroup()) {
            appender.append("        if (operation.getExecutorSelector() == ").append(getExecutorModule().getExecutorInterfaceName()).append(".SELECTOR) {\n");
            if (setContext && HAS_CONTEXT) {
                appender.append(""
                        + "            try {\n"
                        + "                setContext(context);\n"
                        + "                return operation.execute(this").append(CONTEXT_VALUE).append(");\n"
                        + "            } finally {\n"
                        + "                clearContext(context);\n"
                        + "            }\n");
            } else {
                appender.append("            return operation.execute(this").append(CONTEXT_VALUE).append(");\n");
            }
            appender.append("        } else {\n"
                    + "            throw new IllegalStateException(\"Unable to handle the operation: \" +\n"
                    + "                    \"'\" + operation.getClass().getName() + \"', expected executor selector: \" +\n"
                    + "                    \"'\" + operation.getExecutorSelector() + \"'. \" +\n"
                    + "                    \"Operation: \" + operation").append(contextNewLine).append(");\n"
                    + "        }\n");
        } else {
            if (setContext && HAS_CONTEXT) {
                appender.append(""
                        + "        try {\n"
                        + "            setContext(context);\n"
                        + "            return operation.execute(this").append(CONTEXT_VALUE).append(");\n"
                        + "        } finally {\n"
                        + "            clearContext(context);\n"
                        + "        }\n");
            } else {
                appender.append("        return operation.execute(this").append(CONTEXT_VALUE).append(");\n");
            }
        }
        appender.append("    }\n");
        
        if (getGenerationInfo().isIncludeExecuteOtherMethodInExecutors()) {
            appender.append("\n"
                + "    @Override\n"
                + "    public ").append(OPERATION_BASE_DEFINITION).append(" RESULT executeOther(OPERATION operation").append(CONTEXT_PARAM).append(") {\n"
                + "        throw new UnsupportedOperationException(\"Unsuported operation. Operation: \" + operation").append(context).append(");\n"
                + "    }\n");
        }
    }
    
    protected void writeChainedExecuteMethods(Appendable appender, String chainedName) throws IOException {
        String context;
        if (HAS_CONTEXT) {
            context = "+ \". Context: \" + context";
        } else {
            context = "";
        }
        appender.append("    @Override\n"
            + "    public ").append(OPERATION_BASE_DEFINITION).append(" RESULT execute(OPERATION operation").append(CONTEXT_PARAM).append(") {\n");
        if (getGenerationInfo().isExecutorExtendsExecutorGroup()) {
            appender.append("        if (operation.getExecutorSelector() == ").append(getExecutorModule().getExecutorInterfaceName()).append(".SELECTOR) {\n"
                    + "            return operation.execute(this").append(CONTEXT_VALUE).append(");\n"
                    + "        } else {\n"
                    + "            return ").append(chainedName).append(".execute(operation").append(CONTEXT_VALUE).append(");\n"
                    + "        }\n");
        } else {
            appender.append("        return operation.execute(this").append(CONTEXT_VALUE).append(");\n");
        }
        appender.append("    }\n");
        
        if (getGenerationInfo().isIncludeExecuteOtherMethodInExecutors()) {
            appender.append("\n"
                + "    @Override\n"
                + "    public ").append(OPERATION_BASE_DEFINITION).append(" RESULT executeOther(OPERATION operation").append(CONTEXT_PARAM).append(") {\n"
                + "        throw new UnsupportedOperationException(\"Unsuported operation. Operation: \" + operation").append(context).append(");\n"
                + "    }\n");
        }
    }
    
    protected void writeOperationMethodHeader(Appendable appender, OperationInfo operation) throws IOException {
        appender.append("    public ");
        appender.append(operation.getReturnDataType().getSimpleName());
        appender.append(" ");
        appender.append(operation.getMethodName());
        appender.append("(");
        appender.append(operation.getDataType().getSimpleName());
        appender.append(" operation").append(CONTEXT_PARAM).append(")");
    }
}
