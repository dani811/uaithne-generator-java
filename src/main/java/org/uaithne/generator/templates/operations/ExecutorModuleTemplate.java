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
    
    protected void writeExecuteMethods(Appendable appender) throws IOException {
        appender.append("    @Override\n"
                + "    public ").append(OPERATION_BASE_DEFINITION).append(" RESULT execute(OPERATION operation) {\n"
                + "        return operation.execute(this);\n"
                + "    }\n");
        
        if (getGenerationInfo().isIncludeExecuteOtherMethodInExecutors()) {
            appender.append("\n"
                + "    @Override\n"
                + "    public ").append(OPERATION_BASE_DEFINITION).append(" RESULT executeOther(OPERATION operation) {\n"
                + "        throw new UnsupportedOperationException(\"Unsuported operation. Operation: \" + operation);\n"
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
        appender.append(" operation)");
    }
}
