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
package org.uaithne.generator.templates.shared.gwt.client;

import java.io.IOException;
import org.uaithne.generator.templates.ClassTemplate;

public class LoggedAsyncExecutorGroupTemplate extends ClassTemplate {

    public LoggedAsyncExecutorGroupTemplate(String sharedGwtPackageDot, String sharedPackageDot) {
        String packageName = sharedGwtPackageDot + "client";
        setPackageName(packageName);
        addImport("com.google.gwt.user.client.rpc.AsyncCallback", sharedPackageDot);
        addImport(sharedPackageDot + "Operation", packageName);
        addImport("java.util.logging.Level", packageName);
        addImport("java.util.logging.Logger", packageName);
        setClassName("LoggedAsyncExecutorGroup");
        addImplement("AsyncExecutorGroup");
    }
    
    @Override
    protected void writeContent(Appendable appender) throws IOException {
        appender.append("    private AsyncExecutorGroup chainedExecutorGroup;\n"
                + "    private Logger logger;\n"
                + "    private String shortName;\n"
                + "\n"
                + "    @Override\n"
                + "    public ").append(OPERATION_BASE_DEFINITION).append(" void execute(final OPERATION operation, final AsyncCallback<RESULT> asyncCallback) {\n"
                + "        AsyncCallback<RESULT> loggedAsyncCallback = new AsyncCallback<RESULT>() {\n"
                + "\n"
                + "            @Override\n"
                + "            public void onSuccess(RESULT result) {\n"
                + "                if (logger.isLoggable(Level.FINE)) {\n"
                + "                    logger.log(Level.FINE, shortName + \": Execute operation executed. Operation: \" + operation + \". Result: \" + result);\n"
                + "                }\n"
                + "                asyncCallback.onSuccess(result);\n"
                + "            }\n"
                + "\n"
                + "            @Override\n"
                + "            public void onFailure(Throwable caught) {\n"
                + "                logger.log(Level.SEVERE, \"An exception has been ocurrend when execute an operation. Operation: \" + operation, caught);\n"
                + "                asyncCallback.onFailure(caught);\n"
                + "            }\n"
                + "        };\n"
                + "        if (logger.isLoggable(Level.FINE)) {\n"
                + "            logger.log(Level.FINE, shortName + \": Execute operation requested. Operation: \" + operation);\n"
                + "        }\n"
                + "        chainedExecutorGroup.execute(operation, loggedAsyncCallback);\n"
                + "    }\n"
                + "\n"
                + "    /**\n"
                + "     * @return the chainedExecutorGroup\n"
                + "     */\n"
                + "    public AsyncExecutorGroup getChainedExecutorGroup() {\n"
                + "        return chainedExecutorGroup;\n"
                + "    }\n"
                + "\n"
                + "    public LoggedAsyncExecutorGroup(AsyncExecutorGroup chainedExecutorGroup, Logger logger, String shortName) {\n"
                + "        if (chainedExecutorGroup == null) {\n"
                + "            throw new IllegalArgumentException(\"chainedExecutorGroup for the LoggedAsyncExecutorGroup cannot be null\");\n"
                + "        }\n"
                + "        if (logger == null) {\n"
                + "            throw new IllegalArgumentException(\"logger for the LoggedAsyncExecutorGroup cannot be null\");\n"
                + "        }\n"
                + "        if (shortName == null) {\n"
                + "            throw new IllegalArgumentException(\"shortName for the LoggedAsyncExecutorGroup cannot be null\");\n"
                + "        }\n"
                + "        this.shortName = shortName;\n"
                + "        this.logger = logger;\n"
                + "        this.chainedExecutorGroup = chainedExecutorGroup;\n"
                + "    }");
    }
    
}
