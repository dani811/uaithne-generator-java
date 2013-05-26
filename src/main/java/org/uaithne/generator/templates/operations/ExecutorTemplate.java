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

public class ExecutorTemplate extends ExecutorModuleTemplate {
    
    public ExecutorTemplate(ExecutorModuleInfo executorModule, String packageName, String sharedPackageDot) {
        setPackageName(packageName);
        addImport(sharedPackageDot + "Executor", packageName);
        executorModule.appendDefinitionImports(packageName, getImport());
        setClassName(executorModule.getExecutorInterfaceName());
        setDocumentation(executorModule.getDocumentation());
        setExecutorModule(executorModule);
        addImplement("Executor");
        setInterface(true);
    }
    
    @Override
    protected void writeContent(Appendable appender) throws IOException {
        appender.append("    public static final Object SELECTOR = ").append(getClassName()).append(".class;\n\n");
        for (OperationInfo operation : getExecutorModule().getOperations()) {
            String[] opDoc = operation.getDocumentation();
            if (opDoc != null) {
                appender.append("    /**\n");
                for (String ds : opDoc) {
                    appender.append("     * ").append(ds).append("\n");
                }
                appender.append("     */\n");
            }
            writeOperationMethodHeader(appender, operation);
            appender.append(";\n");
        }
    }
    
}
