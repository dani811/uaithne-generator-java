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

public class CombinedGwtOperationTemplate extends ClassTemplate {

    public CombinedGwtOperationTemplate(String sharedGwtPackageDot) {
        String packageName = sharedGwtPackageDot + "shared.rpc";
        addImport(DataTypeInfo.EXECUTOR_DATA_TYPE, packageName);
        addImport(DataTypeInfo.OPERATION_DATA_TYPE, packageName);
        setPackageName(packageName);
        setClassName("CombinedGwtOperation");
        addGenericArgument("RESULT extends CombinedGwtResult");
        addImplement("Operation<RESULT>");
        setAbstract(true);
    }
    
    @Override
    protected void writeContent(Appendable appender) throws IOException {
        appender.append("    @Override\n"
                + "    public Object getExecutorSelector() {\n"
                + "        return GwtOperationExecutor.SELECTOR;\n"
                + "    }\n"
                + "\n"
                + "    @Override\n"
                + "    public RESULT execute(Executor executor) {\n"
                + "        if (executor == null) {\n"
                + "            throw new IllegalArgumentException(\"executor for execute \" + this.getClass().getName() + \" CombinedGwtOperation cannot be null\");\n"
                + "        }\n"
                + "        return ((GwtOperationExecutor)executor).executeCombinedGwtOperation(this);\n"
                + "    }\n"
                + "\n"
                + "    public abstract RESULT executeOnServer(Executor executor);");
        
        if (getGenerationInfo().isIncludeExecutePostOperationInOperations()) {
            appender.append("\n"
                + "\n"
                + "    @Override\n"
                + "    public RESULT executePostOperation(RESULT result) {\n"
                + "        return result;\n"
                + "    }");
        }
    }
    
}
