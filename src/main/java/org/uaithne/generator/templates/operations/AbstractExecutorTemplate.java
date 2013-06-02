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
import org.uaithne.generator.commons.DataTypeInfo;
import org.uaithne.generator.commons.ExecutorModuleInfo;

public class AbstractExecutorTemplate extends ExecutorModuleTemplate {

    public AbstractExecutorTemplate(ExecutorModuleInfo executorModule, String packageName) {
        setPackageName(packageName);
        addImport(DataTypeInfo.OPERATION_DATA_TYPE, packageName);
        setClassName(executorModule.getNameUpper() + "AbstractExecutor");
        setExecutorModule(executorModule);
        addImplement(executorModule.getExecutorInterfaceName());
        setAbstract(true);
    }

    @Override
    protected void writeContent(Appendable appender) throws IOException {
        writeGetExecutorSelector(appender);
        appender.append("\n");
        writeExecuteMethods(appender);
        appender.append("\n"
                + "    public ").append(getClassName()).append("() {\n"
                + "    }");
    }
}
