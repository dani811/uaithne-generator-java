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
package org.uaithne.generator.templates.shared.gwt.shared.rpc;

import java.io.IOException;
import org.uaithne.generator.commons.DataTypeInfo;
import org.uaithne.generator.templates.ClassTemplate;

public class AwaitGwtOperationTemplate extends ClassTemplate {

    public AwaitGwtOperationTemplate(String sharedGwtPackageDot) {
        String packageName = sharedGwtPackageDot + "shared.rpc";
        setPackageName(packageName);
        addImport(DataTypeInfo.EXECUTOR_DATA_TYPE, packageName);
        addImport(DataTypeInfo.OPERATION_DATA_TYPE, packageName);
        setClassName("AwaitGwtOperation");
        addImplement("Operation<AwaitGwtResult>");
    }
    
    @Override
    protected void writeContent(Appendable appender) throws IOException {
        appender.append("    @Override\n"
                + "    public Object getExecutorSelector() {\n"
                + "        return GwtOperationExecutor.SELECTOR;\n"
                + "    }\n"
                + "\n"
                + "    @Override\n"
                + "    public AwaitGwtResult execute(Executor executor) {\n"
                + "        if (executor == null) {\n"
                + "            throw new IllegalArgumentException(\"executor for execute \" + this.getClass().getName() + \" AwaitGwtOperation cannot be null\");\n"
                + "        }\n"
                + "        return ((GwtOperationExecutor)executor).executeAwaitGwtOperation(this);\n"
                + "    }\n"
                + "\n"
                + "    @Override\n"
                + "    public AwaitGwtResult executePostOperation(AwaitGwtResult result) {\n"
                + "        return result;\n"
                + "    }");
    }
    
}
