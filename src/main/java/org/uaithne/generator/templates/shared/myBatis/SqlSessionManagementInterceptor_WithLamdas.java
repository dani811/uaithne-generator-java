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
import org.uaithne.generator.commons.DataTypeInfo;
import org.uaithne.generator.templates.ClassTemplate;

public class SqlSessionManagementInterceptor_WithLamdas extends ClassTemplate {

    public SqlSessionManagementInterceptor_WithLamdas(String packageName) {
        setPackageName(packageName);
        addImport(DataTypeInfo.EXECUTOR_DATA_TYPE, packageName);
        addImport(DataTypeInfo.OPERATION_DATA_TYPE, packageName);
        setClassName("SqlSessionManagementInterceptor");
        setExtend("Executor");
        addContextImport(packageName);
        setFinal(true);
    }
    
    @Override
    protected void writeContent(Appendable appender) throws IOException {
        appender.append("    private final ManagedSqlSessionProvider provider;\n"
                + "\n"
                + "    @Override\n"
                + "    ").append(EXECUTE_ANY_VISIBILITY).append(OPERATION_BASE_DEFINITION).append(" RESULT ").append(EXECUTE_ANY).append("(OPERATION operation").append(CONTEXT_PARAM).append(") {\n"
                + "        boolean maybeRollback = true;\n"
                + "        try {\n"
                + "            provider.beginSqlSessionLevel();\n"
                + "            RESULT result = next.execute(operation").append(CONTEXT_VALUE).append(");\n"
                + "            maybeRollback = false;\n"
                + "            return result;\n"
                + "        } finally {\n"
                + "            provider.endSqlSessionLevel(maybeRollback);\n"
                + "        }\n"
                + "    }\n"
                + "\n"
                + "    public SqlSessionManagementInterceptor(Executor next, ManagedSqlSessionProvider provider) {\n"
                + "        super(next);\n"
                + "        if (next == null) {\n"
                + "            throw new IllegalArgumentException(\"next for the SqlSessionManagementInterceptor cannot be null\");\n"
                + "        }\n"
                + "        if (provider == null) {\n"
                + "            throw new IllegalArgumentException(\"provider for the SqlSessionManagementInterceptor cannot be null\");\n"
                + "        }\n"
                + "        this.provider = provider;\n"
                + "    }");
    }
    
}
