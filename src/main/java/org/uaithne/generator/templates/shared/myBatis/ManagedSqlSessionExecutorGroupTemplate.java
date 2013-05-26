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
package org.uaithne.generator.templates.shared.myBatis;

import java.io.IOException;
import org.uaithne.generator.templates.ClassTemplate;

public class ManagedSqlSessionExecutorGroupTemplate extends ClassTemplate {

    public ManagedSqlSessionExecutorGroupTemplate(String packageName, String sharedPackageDot) {
        setPackageName(packageName);
        addImport(sharedPackageDot + "ChainedExecutorGroup", packageName);
        addImport(sharedPackageDot + "ExecutorGroup", packageName);
        addImport(sharedPackageDot + "Operation", packageName);
        setClassName("ManagedSqlSessionExecutorGroup");
        setExtend("ChainedExecutorGroup");
    }
    
    @Override
    protected void writeContent(Appendable appender) throws IOException {
        appender.append("    private ManagedSqlSessionProvider provider;\n"
                + "\n"
                + "    @Override\n"
                + "    public ").append(OPERATION_BASE_DEFINITION).append(" RESULT execute(OPERATION operation) {\n"
                + "        boolean rollback = true;\n"
                + "        boolean newConnection = false;\n"
                + "        try {\n"
                + "            newConnection = provider.openSqlSession();\n"
                + "            RESULT result = super.execute(operation);\n"
                + "            rollback = false;\n"
                + "            return result;\n"
                + "        } finally {\n"
                + "            if (newConnection) {\n"
                + "                provider.closeSqlSession(rollback);\n"
                + "            }\n"
                + "        }\n"
                + "    }\n"
                + "\n"
                + "    public ManagedSqlSessionExecutorGroup(ExecutorGroup chainedExecutorGroup, ManagedSqlSessionProvider provider) {\n"
                + "        super(chainedExecutorGroup);\n"
                + "        \n"
                + "        if (provider == null) {\n"
                + "            throw new IllegalArgumentException(\"provider for the ManagedSqlSessionExecutorGroup cannot be null\");\n"
                + "        }\n"
                + "        this.provider = provider;\n"
                + "    }");
    }
    
}
