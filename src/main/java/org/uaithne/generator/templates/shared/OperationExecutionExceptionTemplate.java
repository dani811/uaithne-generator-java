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

public class OperationExecutionExceptionTemplate extends ClassTemplate {

    public OperationExecutionExceptionTemplate(String packageName) {
        setPackageName(packageName);
        setClassName("OperationExecutionException");
        setExtend("RuntimeException");
        addContextImport(packageName);
    }

    @Override
    protected void writeContent(Appendable appender) throws IOException {
        appender.append("    private Operation operation;\n");
        if (HAS_CONTEXT) {
            appender.append("    private ").append(CONTEXT_TYPE).append(" context;\n");
        }
        appender.append("    private String simpleMessage;\n"
                + "\n"
                + "    public Operation getOperation() {\n"
                + "        return operation;\n"
                + "    }\n"
                + "\n");
        if (HAS_CONTEXT) {
            appender.append("    public ").append(CONTEXT_TYPE).append(" getContext() {\n"
                    + "        return context;\n"
                    + "    }\n"
                    + "\n");
        }
        appender.append("    public String getSimpleMessage() {\n"
                + "        if (simpleMessage == null) {\n"
                + "            Throwable cause = getCause();\n"
                + "            if (cause != null) {\n"
                + "                if (cause instanceof OperationExecutionException) {\n"
                + "                    return ((OperationExecutionException) cause).getSimpleMessage();\n"
                + "                } else {\n"
                + "                    return cause.getMessage();\n"
                + "                }\n"
                + "            }\n"
                + "        }\n"
                + "        return simpleMessage;\n"
                + "    }\n"
                + "\n");
        if (HAS_CONTEXT) {
            appender.append("    public boolean sameContent(Operation operation").append(CONTEXT_PARAM).append(") {\n"
                    + "        return this.operation == operation && this.context == context;\n"
                    + "    }\n"
                    + "\n");
        } else {
            appender.append("    public boolean sameContent(Operation operation) {\n"
                    + "        return this.operation == operation;\n"
                    + "    }\n"
                    + "\n");
        }
        appender.append("    public OperationExecutionException(Operation operation").append(CONTEXT_PARAM).append(") {\n"
                + "        super(createMessage(operation").append(CONTEXT_VALUE).append(", null, null));\n"
                + "    }\n"
                + "\n"
                + "    public OperationExecutionException(Operation operation").append(CONTEXT_PARAM).append(", String message) {\n"
                + "        super(createMessage(operation").append(CONTEXT_VALUE).append(", message, null));\n"
                + "        this.operation = operation;\n");
        if (HAS_CONTEXT) {
            appender.append("        this.context = context;\n");
        }
        appender.append("        simpleMessage = message;\n"
                + "    }\n"
                + "\n"
                + "    public OperationExecutionException(Operation operation").append(CONTEXT_PARAM).append(", String message,\n"
                + "            Throwable cause) {\n"
                + "        super(createMessage(operation").append(CONTEXT_VALUE).append(", message, cause), cause);\n"
                + "        this.operation = operation;\n");
        if (HAS_CONTEXT) {
            appender.append("        this.context = context;\n");
        }
        appender.append("        simpleMessage = message;\n"
                + "    }\n"
                + "\n"
                + "    public OperationExecutionException(Operation operation").append(CONTEXT_PARAM).append(", Throwable cause) {\n"
                + "        super(createMessage(operation").append(CONTEXT_VALUE).append(", null, cause), cause);\n"
                + "        this.operation = operation;\n");
        if (HAS_CONTEXT) {
            appender.append("        this.context = context;\n");
        }
        appender.append("    }\n"
                + "\n"
                + "    private static String createMessage(Operation operation").append(CONTEXT_PARAM).append(", String message,\n"
                + "            Throwable cause) {\n"
                + "        StringBuilder result = new StringBuilder();\n"
                + "\n"
                + "        if (message == null) {\n"
                + "            if (operation == null) {\n"
                + "                result.append(\"An error happens executing an operation\");\n"
                + "            } else {\n"
                + "                result.append(\"An error happens executing the operation \");\n"
                + "                result.append(operation.getClass().getSimpleName());\n"
                + "            }\n"
                + "            if (cause != null) {\n"
                + "                result.append(\": \");\n"
                + "                if (cause instanceof OperationExecutionException) {\n"
                + "                    result.append(((OperationExecutionException) cause).getSimpleMessage());\n"
                + "                } else {\n"
                + "                    result.append(cause.getMessage());\n"
                + "                }\n"
                + "            }\n"
                + "        } else {\n"
                + "            result.append(message);\n"
                + "        }\n"
                + "\n"
                + "        result.append(\"\\n\\nOperation type: \");\n"
                + "        if (operation != null) {\n"
                + "            result.append(operation.getClass().getTypeName());\n"
                + "        } else {\n"
                + "            result.append(\"null\");\n"
                + "        }\n"
                + "\n"
                + "        result.append(\"\\n\\nOperation: \").append(operation);\n");
        if (HAS_CONTEXT) {
            appender.append("        result.append(\"\\n\\nContext: \").append(context);\n");
        }
        appender.append("        return result.toString();\n"
                + "    }");
    }

}
