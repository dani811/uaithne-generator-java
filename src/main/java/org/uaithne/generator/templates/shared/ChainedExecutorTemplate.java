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

public class ChainedExecutorTemplate extends ClassTemplate {

    public ChainedExecutorTemplate(String packageName) {
        setPackageName(packageName);
        setClassName("ChainedExecutor");
        if (ERROR_MANAGEMENT) {
            setExtend("Executor");
        } else {
            addImplement("Executor");
        }
        addContextImport(packageName);
    }
    
    @Override
    protected void writeContent(Appendable appender) throws IOException {

        appender.append("    private Executor chainedExecutor;\n"
                + "\n"
                + "    /**\n"
                + "     * @return the chainedExecutor\n"
                + "     */\n"
                + "    public Executor getChainedExecutor() {\n"
                + "        return chainedExecutor;\n"
                + "    }\n"
                + "\n"
                + "    @Override\n"
                + "    public Object getExecutorSelector() {\n"
                + "        return chainedExecutor.getExecutorSelector();\n"
                + "    }\n"
                + "\n"
                + "    @Override\n"
                + "    ").append(EXECUTE_ANY_VISIBILITY).append(OPERATION_BASE_DEFINITION).append(" RESULT ").append(EXECUTE_ANY).append("(OPERATION operation").append(CONTEXT_PARAM).append(") {\n"
                + "        return chainedExecutor.execute(operation").append(CONTEXT_VALUE).append(");\n"
                + "    }\n");
        
        if (getGenerationInfo().isIncludeExecuteOtherMethodInExecutors()) {
            appender.append("\n"
                + "    @Override\n"
                + "    public ").append(OPERATION_BASE_DEFINITION).append(" RESULT executeOther(OPERATION operation").append(CONTEXT_PARAM).append(") {\n"
                + "        return execute(operation").append(CONTEXT_VALUE).append(");\n"
                + "    }\n");
        }
        
        appender.append("\n"
                + "    public ChainedExecutor(Executor chainedExecutor) {\n"
                + "        if (chainedExecutor == null) {\n"
                + "            throw new IllegalArgumentException(\"chainedExecutor for the ChainedExecutor cannot be null\");\n"
                + "        }\n"
                + "        this.chainedExecutor = chainedExecutor;\n"
                + "    }");
    }

}
