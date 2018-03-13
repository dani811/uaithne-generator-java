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
package org.uaithne.generator.templates.shared;

import java.io.IOException;
import static org.uaithne.generator.commons.DataTypeInfo.*;
import org.uaithne.generator.templates.ClassTemplate;

public class MappedExecutorGroupTemplate_WithExecutorGroup extends ClassTemplate {

    public MappedExecutorGroupTemplate_WithExecutorGroup(String packageName) {
        setPackageName(packageName);
        addImport(HASHMAP_DATA_TYPE, packageName);
        setClassName("MappedExecutorGroup");
        if (ERROR_MANAGEMENT) {
            setExtend("ExecutorGroup");
        } else {
            addImplement("ExecutorGroup");
        }
        addContextImport(packageName);
    }
    
    @Override
    protected void writeContent(Appendable appender) throws IOException {
        String context;
        if (HAS_CONTEXT) {
            context = " +\n"
                + "                    \"Context: \" + context";
        } else {
            context = "";
        }
        appender.append("    private final HashMap<Object, ExecutorGroup> executorMap = new HashMap<Object, ExecutorGroup>();\n"
                + "\n"
                + "    public ExecutorGroup addExecutor(Executor executor) {\n"
                + "        if (executor == null) {\n"
                + "            throw new IllegalArgumentException(\"executor for add to the MappedExecutorGroup cannot be null\");\n"
                + "        }\n"
                + "        return executorMap.put(executor.getExecutorSelector(), executor);\n"
                + "    }\n"
                + "\n"
                + "    public ExecutorGroup addExecutor(ExecutorGroup executorGroup, Object executorSelector) {\n"
                + "        if (executorGroup == null) {\n"
                + "            throw new IllegalArgumentException(\"executorGroup for add to the MappedExecutorGroup cannot be null\");\n"
                + "        }\n"
                + "        if (executorSelector == null) {\n"
                + "            throw new IllegalArgumentException(\"executorSelector for add to the MappedExecutorGroup cannot be null\");\n"
                + "        }\n"
                + "        return executorMap.put(executorSelector, executorGroup);\n"
                + "    }\n"
                + "\n"
                + "    public boolean removeExecutor(Executor executor) {\n"
                + "        if (executor == null) {\n"
                + "            return false;\n"
                + "        }\n"
                + "        if (executorMap.containsValue(executor)) {\n"
                + "            executorMap.remove(executor.getExecutorSelector());\n"
                + "            return true;\n"
                + "        } else {\n"
                + "            return false;\n"
                + "        }\n"
                + "    }\n"
                + "\n"
                + "    public boolean removeExecutorBySelector(Object executorSelector) {\n"
                + "        if (executorSelector == null) {\n"
                + "            return false;\n"
                + "        }\n"
                + "        if (executorMap.containsKey(executorSelector)) {\n"
                + "            executorMap.remove(executorSelector);\n"
                + "            return true;\n"
                + "        } else {\n"
                + "            return false;\n"
                + "        }\n"
                + "    }\n"
                + "\n"
                + "    public ExecutorGroup getExecutor(Operation operation) {\n"
                + "        return executorMap.get(operation.getExecutorSelector());\n"
                + "    }\n"
                + "\n"
                + "    @Override\n"
                + "    ").append(EXECUTE_ANY_VISIBILITY).append(OPERATION_BASE_DEFINITION).append(" RESULT ").append(EXECUTE_ANY).append("(OPERATION operation").append(CONTEXT_PARAM).append(") {\n"
                + "        ExecutorGroup executor = getExecutor(operation);\n"
                + "        if (executor == null) {\n");
        if (ERROR_MANAGEMENT) {
            appender.append("            throw new IllegalStateException(\"Unable to find a executor for the operation: \" +\n"
                    + "                    \"'\" + operation.getClass().getName() + \"', expected executor selector: \" +\n"
                    + "                    \"'\" + operation.getExecutorSelector() + \"'\");\n");
        } else {
            appender.append("            throw new IllegalStateException(\"Unable to find a executor for the operation: \" +\n"
                    + "                    \"'\" + operation.getClass().getName() + \"', expected executor selector: \" +\n"
                    + "                    \"'\" + operation.getExecutorSelector() + \"'. \" +\n"
                    + "                    \"Operation: \" + operation").append(context).append(");\n");
        }
        appender.append("        }\n"
                + "        return executor.execute(operation").append(CONTEXT_VALUE).append(");\n"
                + "    }\n"
                + "\n"
                + "    public MappedExecutorGroup() {\n"
                + "    }");
    }
    
}
