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
import org.uaithne.generator.commons.GenerationInfo;
import org.uaithne.generator.templates.ClassTemplate;

public class ExecutorTemplate extends ClassTemplate {

    public ExecutorTemplate(String packageName) {
        setPackageName(packageName);
        setClassName("Executor");
        if (getGenerationInfo().isExecutorExtendsExecutorGroup()) {
            setExtend("ExecutorGroup");
        }
        if (ERROR_MANAGEMENT) {
            setAbstract(true);
        } else {
            setInterface(true);
        }
        addContextImport(packageName);
    }

    @Override
    protected void writeContent(Appendable appender) throws IOException {
        GenerationInfo generationInfo = getGenerationInfo();
        String modifier;
        if (ERROR_MANAGEMENT) {
            modifier = "abstract ";
        } else {
            modifier = "";
        }
        appender.append("    public ").append(modifier).append("Object getExecutorSelector();");
        if (!generationInfo.isExecutorExtendsExecutorGroup()) {
            if (ERROR_MANAGEMENT) {
                appender.append("\n\n"
                        + "    public final ").append(OPERATION_BASE_DEFINITION).append(" RESULT execute(OPERATION operation").append(CONTEXT_PARAM).append(") {\n"
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
                        + "    }");
            }
            appender.append("\n\n"
                    + "    ").append(EXECUTE_ANY_VISIBILITY).append(modifier).append(OPERATION_BASE_DEFINITION).append(" RESULT ").append(EXECUTE_ANY).append("(OPERATION operation").append(CONTEXT_PARAM).append(");");
        }

        if (generationInfo.isIncludeExecuteOtherMethodInExecutors()) {
            appender.append("\n\n"
                    + "    public ").append(modifier).append(OPERATION_BASE_DEFINITION).append(" RESULT executeOther(OPERATION operation").append(CONTEXT_PARAM).append(");");
        }
    }

}
