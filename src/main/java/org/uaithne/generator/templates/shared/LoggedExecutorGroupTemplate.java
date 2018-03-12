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

public class LoggedExecutorGroupTemplate extends ClassTemplate {

    public LoggedExecutorGroupTemplate(String packageName) {
        setPackageName(packageName);
        addImport("java.util.logging.Level", packageName);
        addImport("java.util.logging.Logger", packageName);
        setClassName("LoggedExecutorGroup");
        addImplement("ExecutorGroup");
        addContextImport(packageName);
    }
    
    @Override
    protected void writeContent(Appendable appender) throws IOException {
        String context;
        if (HAS_CONTEXT) {
            context = " + \". Context: \" + context";
        } else {
            context = "";
        }
        appender.append("    private ExecutorGroup chainedExecutorGroup;\n"
                + "    private Logger logger;\n"
                + "    private String shortName;\n"
                + "\n"
                + "    @Override\n"
                + "    public ").append(OPERATION_BASE_DEFINITION).append(" RESULT execute(OPERATION operation").append(CONTEXT_PARAM).append(") {\n"
                + "        boolean fineLevelEnabled = logger.isLoggable(Level.FINE);\n"
                + "        try {\n"
                + "            if (fineLevelEnabled) {\n"
                + "                logger.log(Level.FINE, shortName + \": Execute operation requested. Operation: \" + operation").append(context).append(");\n"
                + "                RESULT result = chainedExecutorGroup.execute(operation").append(CONTEXT_VALUE).append(");\n"
                + "                logger.log(Level.FINE, shortName + \": Execute operation executed. Operation: \" + operation").append(context).append(" + \". Result: \" + result);\n"
                + "                return result;\n"
                + "            } else {\n"
                + "                return chainedExecutorGroup.execute(operation").append(CONTEXT_VALUE).append(");\n"
                + "            }\n"
                + "        } catch (RuntimeException e) {\n"
                + "            logger.log(Level.SEVERE, \"An exception has been ocurrend when execute an operation. Operation: \" + operation").append(context).append(", e);\n"
                + "            throw e;\n"
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
                + "    public LoggedExecutorGroup(ExecutorGroup chainedExecutorGroup, Logger logger, String shortName) {\n"
                + "        if (chainedExecutorGroup == null) {\n"
                + "            throw new IllegalArgumentException(\"chainedExecutorGroup for the LoggedExecutorGroup cannot be null\");\n"
                + "        }\n"
                + "        if (logger == null) {\n"
                + "            throw new IllegalArgumentException(\"logger for the LoggedExecutorGroup cannot be null\");\n"
                + "        }\n"
                + "        if (shortName == null) {\n"
                + "            throw new IllegalArgumentException(\"shortName for the LoggedExecutorGroup cannot be null\");\n"
                + "        }\n"
                + "        this.shortName = shortName;\n"
                + "        this.logger = logger;\n"
                + "        this.chainedExecutorGroup = chainedExecutorGroup;\n"
                + "    }");
    }
    
}
