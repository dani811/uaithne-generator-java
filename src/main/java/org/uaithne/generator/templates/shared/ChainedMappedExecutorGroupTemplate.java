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
import org.uaithne.generator.templates.ClassTemplate;
import static org.uaithne.generator.commons.DataTypeInfo.*;

public class ChainedMappedExecutorGroupTemplate extends ClassTemplate {

    public ChainedMappedExecutorGroupTemplate(String packageName) {
        setPackageName(packageName);
        addImport(HASHMAP_DATA_TYPE, packageName);
        setClassName("ChainedMappedExecutorGroup");
        addImplement("ExecutorGroup");
    }
    
    @Override
    protected void writeContent(Appendable appender) throws IOException {
        appender.append("    private HashMap<Object, Executor> executorMap;\n"
                + "    private ExecutorGroup chainedExecutorGroup;\n"
                + "\n"
                + "    public Executor getCustomizedExecutor(Operation operation) {\n"
                + "        if (executorMap == null) {\n"
                + "            return null;\n"
                + "        }\n"
                + "        return executorMap.get(operation.getExecutorSelector());\n"
                + "    }\n"
                + "\n"
                + "    @Override\n"
                + "    public ").append(OPERATION_BASE_DEFINITION).append(" RESULT execute(OPERATION operation) {\n"
                + "        Executor executor = getCustomizedExecutor(operation);\n"
                + "        if (executor == null) {\n"
                + "            return chainedExecutorGroup.execute(operation);\n"
                + "        } else {\n"
                + "            return executor.execute(operation);\n"
                + "        }\n"
                + "    }\n"
                + "\n"
                + "    /**\n"
                + "     * @return the chainedExecutorGroup\n"
                + "     */\n"
                + "    public ExecutorGroup getChainedExecutorGroup() {\n"
                + "        return chainedExecutorGroup;\n"
                + "    }\n"
                + "    \n"
                + "    public Executor addCustomExecutor(Executor executor) {\n"
                + "        if (executor == null) {\n"
                + "            throw new IllegalArgumentException(\"executor for add to the ChainedMappedExecutorGroup cannot be null\");\n"
                + "        }\n"
                + "        if (executorMap == null) {\n"
                + "            executorMap = new HashMap<Object, Executor>();\n"
                + "        }\n"
                + "        return executorMap.put(executor.getExecutorSelector(), executor);\n"
                + "    }\n"
                + "    \n"
                + "    public boolean removeCustomExecutor(Executor executor) {\n"
                + "        if (executor == null) {\n"
                + "            return false;\n"
                + "        }\n"
                + "        if (executorMap == null) {\n"
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
                + "    public ChainedMappedExecutorGroup(ExecutorGroup chainedExecutorGroup) {\n"
                + "        if (chainedExecutorGroup == null) {\n"
                + "            throw new IllegalArgumentException(\"chainedExecutorGroup for the ChainedMappedExecutorGroup cannot be null\");\n"
                + "        }\n"
                + "        this.chainedExecutorGroup = chainedExecutorGroup;\n"
                + "    }");
    }
    
}
