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

public class OperationTemplate extends ClassTemplate {

    public OperationTemplate(String packageName) {
        setPackageName(packageName);
        addImport(SERIALIZABLE_DATA_TYPE, packageName);
        setClassName("Operation");
        addGenericArgument(RESULT_BASE_DEFINITION);
        addImplement(SERIALIZABLE_DATA);
        setInterface(true);
    }
    
    @Override
    protected void writeContent(Appendable appender) throws IOException {
        appender.append("    public Object getExecutorSelector();\n"
                + "    public RESULT execute(Executor executor);");
        if (getGenerationInfo().isIncludeExecutePostOperationInOperations()) {
            appender.append("\n"
                + "    public RESULT executePostOperation(RESULT result);");
        }
    }
    
}
