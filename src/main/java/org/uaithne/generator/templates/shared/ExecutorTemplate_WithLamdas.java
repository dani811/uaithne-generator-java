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

public class ExecutorTemplate_WithLamdas extends ClassTemplate {

    public ExecutorTemplate_WithLamdas(String packageName) {
        setPackageName(packageName);
        setClassName("Executor");
        addImport("java.util.HashMap", packageName);
        if (HAS_CONTEXT) {
            addImport("java.util.function.BiFunction", packageName);
        } else {
            addImport("java.util.function.Function", packageName);
        }
        addContextImport(packageName);
    }

    @Override
    protected void writeContent(Appendable appender) throws IOException {
        String functionClass;
        String functionType;
        if (HAS_CONTEXT) {
            functionClass = "BiFunction";
            functionType = "BiFunction<OPERATION, " + CONTEXT_TYPE + ", RESULT>";
        } else {
            functionClass = "Function";
            functionType = "Function<OPERATION, RESULT>";
        }

        appender.append("    protected final Executor next;\n"
                + "    HashMap<Class, ").append(functionClass).append("> handlers;\n"
                + "\n"
                + "    public final Executor getNext() {\n"
                + "        return next;\n"
                + "    }\n"
                + "\n");
        if (ERROR_MANAGEMENT) {
            appender.append("    public final ").append(OPERATION_BASE_DEFINITION).append(" RESULT execute(OPERATION operation").append(CONTEXT_PARAM).append(") {\n"
                    + "        try {\n"
                    + "            return executeAnyOperation(operation").append(CONTEXT_VALUE).append(");\n"
                    + "        } catch (OperationExecutionException ex) {\n"
                    + "            if (ex.sameContent(operation").append(CONTEXT_VALUE).append(")) {\n"
                    + "                throw ex;\n"
                    + "            }\n"
                    + "            throw new OperationExecutionException(operation").append(CONTEXT_VALUE).append(", ex);\n"
                    + "        } catch (PublicException ex) {\n"
                    + "            throw ex;\n"
                    + "        } catch (Exception ex) {\n"
                    + "            throw new OperationExecutionException(operation").append(CONTEXT_VALUE).append(", ex);\n"
                    + "        }\n"
                    + "    }\n"
                    + "\n");
        }
        appender.append("    ").append(EXECUTE_ANY_VISIBILITY).append(OPERATION_BASE_DEFINITION).append(" RESULT ").append(EXECUTE_ANY).append("(OPERATION operation").append(CONTEXT_PARAM).append(") {\n"
                + "        if (handlers != null) {\n"
                + "            ").append(functionType).append(" handler = handlers.get(operation.getClass());\n"
                + "            if (handler != null) {\n"
                + "                return handler.apply(operation").append(CONTEXT_VALUE).append(");\n"
                + "            }\n"
                + "        }\n"
                + "        if (next != null) {\n"
                + "            return next.execute(operation").append(CONTEXT_VALUE).append(");\n"
                + "        }\n");
        if (ERROR_MANAGEMENT) {
            appender.append("        throw new OperationExecutionException(operation").append(CONTEXT_VALUE).append(", \"No handler found for the operation: \" + operation.getClass().getTypeName());\n");
        } else {
            if (HAS_CONTEXT) {
                appender.append("        throw new IllegalStateException(\"No handler found for the operation: \" + operation.getClass().getTypeName() + \". Operation: \" + operation + \". Context: \" + context);\n");
            } else {
                appender.append("        throw new IllegalStateException(\"No handler found for the operation: \" + operation.getClass().getTypeName() + \". Operation: \" + operation);\n");
            }
        }
        appender.append("    }\n"
                + "\n"
                + "    protected final ").append(OPERATION_BASE_DEFINITION).append(" void handle(Class<OPERATION> operationType, ").append(functionType).append(" handler) {\n"
                + "        if (handlers == null) {\n"
                + "            handlers = new HashMap<>();\n"
                + "        }\n"
                + "        handlers.put(operationType, handler);\n"
                + "    }\n"
                + "\n"
                + "    public Executor() {\n"
                + "        this(null);\n"
                + "    }\n"
                + "\n"
                + "    public Executor(Executor next) {\n"
                + "        this.next = next;\n"
                + "    }");
    }

}
