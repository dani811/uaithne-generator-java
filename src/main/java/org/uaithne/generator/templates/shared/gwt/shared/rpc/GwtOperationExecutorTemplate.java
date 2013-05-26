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
import static org.uaithne.generator.commons.DataTypeInfo.*;
import org.uaithne.generator.templates.ClassTemplate;

public class GwtOperationExecutorTemplate extends ClassTemplate {

    public GwtOperationExecutorTemplate(String sharedGwtPackageDot, String sharedPackageDot) {
        String packageName = sharedGwtPackageDot + "shared.rpc";
        setPackageName(packageName);
        addImport(sharedPackageDot + "Executor", packageName);
        setClassName("GwtOperationExecutor");
        setExtend("Executor");
        setInterface(true);
    }
    
    @Override
    protected void writeContent(Appendable appender) throws IOException {
        appender.append("    public static final Object SELECTOR = GwtOperationExecutor.class;\n"
                + "\n"
                + "    public <RESULT extends CombinedGwtResult, OPERATION extends CombinedGwtOperation<RESULT>> RESULT executeCombinedGwtOperation(OPERATION operation);\n"
                + "\n"
                + "    public AwaitGwtResult executeAwaitGwtOperation(AwaitGwtOperation operation);");
    }
    
}
