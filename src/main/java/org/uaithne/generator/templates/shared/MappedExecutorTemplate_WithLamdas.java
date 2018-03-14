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

public class MappedExecutorTemplate_WithLamdas extends ClassTemplate {

    public MappedExecutorTemplate_WithLamdas(String packageName) {
        setPackageName(packageName);
        setClassName("MappedExecutor");
        setExtend("Executor");
        setFinal(true);
        addContextImport(packageName);
    }

    @Override
    protected void writeContent(Appendable appender) throws IOException {
        appender.append("    public final void handle(Executor executor) {\n"
                + "        Executor current = executor;\n"
                + "        while (current != null && current != next && current != this) {\n"
                + "            if (current.handlers != null) {\n"
                + "                for (Class operationType : current.handlers.keySet()) {\n"
                + "                    handle(operationType, executor::execute);\n"
                + "                }\n"
                + "            }\n"
                + "\n"
                + "            current = current.next;\n"
                + "        }\n"
                + "    }\n"
                + "\n"
                + "    public final ").append(OPERATION_BASE_DEFINITION).append(" void handle(Class<OPERATION> operationType, Executor executor) {\n"
                + "        handle(operationType, executor::execute);\n"
                + "    }\n"
                + "\n"
                + "    public MappedExecutor() {\n"
                + "    }\n"
                + "\n"
                + "    public MappedExecutor(Executor next) {\n"
                + "        super(next);\n"
                + "    }");
    }

}
