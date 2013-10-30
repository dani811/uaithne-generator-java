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
import java.util.ArrayList;
import org.uaithne.generator.commons.DataTypeInfo;
import static org.uaithne.generator.commons.DataTypeInfo.*;
import org.uaithne.generator.commons.FieldInfo;
import org.uaithne.generator.commons.OperationInfo;
import org.uaithne.generator.commons.OperationKind;
import org.uaithne.generator.templates.PojoTemplate;

public class OperationTemplate extends PojoTemplate {

    private OperationInfo operation;
    private String executorName;

    public OperationInfo getOperation() {
        return operation;
    }

    public void setOperation(OperationInfo operation) {
        this.operation = operation;
    }

    public String getExecutorName() {
        return executorName;
    }

    public void setExecutorName(String executorName) {
        this.executorName = executorName;
    }

    public OperationTemplate(OperationInfo operation, String packageName, String executorName) {
        setPackageName(packageName);
        addImport(EXECUTOR_DATA_TYPE, packageName);
        operation.appendFullImports(packageName, getImport());
        setDocumentation(operation.getDocumentation());
        setClassName(operation.getDataType().getSimpleNameWithoutGenerics());
        if (operation.getExtend() != null) {
            setExtend(operation.getExtend().getSimpleName());
        }
        for (DataTypeInfo type : operation.getImplement()) {
            addImplement(type.getSimpleName());
        }
        setDeprecated(operation.isDeprecated());
        this.executorName = executorName;
        this.operation = operation;
    }

    void writeGetMaxRowNumber(Appendable appender) throws IOException {
        appender.append("    @Override\n");
        appender.append("    public ").append(PAGE_INFO_DATA_TYPE.getSimpleName()).append(" getMaxRowNumber() {\n");
        if (PAGE_INFO_DATA_TYPE.getPackageName().isEmpty()) {
            appender.append("        return limit + offset;\n");
        } else {
            appender.append("        if (limit == null) {\n"
                    + "            return null;\n"
                    + "        }\n"
                    + "        if (offset == null) {\n"
                    + "            return limit;\n"
                    + "        }\n"
                    + "        return limit.add(offset);\n");
        }
        appender.append("    }\n");
    }

    void writeVisit(Appendable appender) throws IOException {
        String returnName = operation.getReturnDataType().getSimpleName();
        appender.append("    @Override\n"
                + "    public ").append(returnName).append(" execute(Executor executor) {\n"
                + "        return execute((").append(executorName).append(")executor);\n"
                + "    }\n"
                + "    \n"
                + "    public ").append(returnName).append(" execute(").append(executorName).append(" executor) {\n"
                + "        return executor.").append(operation.getMethodName()).append("(this);\n"
                + "    }\n"
                + "\n"
                + "    @Override\n"
                + "    public ").append(returnName).append(" executePostOperation(").append(returnName).append(" result) {\n");
        if (operation.getOperationKind() == OperationKind.INSERT || operation.getOperationKind() == OperationKind.SAVE) {
            appender.append("        if (value != null) {\n"
                    + "            value.set").append(operation.getEntity().getCombined().getFirstIdField().getCapitalizedName()).append("(result);\n"
                    + "        }\n"
                    + "        return result;\n");
        } else {
            appender.append("        return result;\n");
        }
        appender.append("    }\n");
    }

    void writeConstructors(Appendable appender) throws IOException {
        ArrayList<FieldInfo> fields = operation.getFields();
        ArrayList<FieldInfo> filteredMandatoryFields = new ArrayList<FieldInfo>(fields.size());
        for (FieldInfo field : fields) {
            if (field.isOptional()) {
                continue;
            }
            if (!field.isExcludedFromConstructor()) {
                filteredMandatoryFields.add(field);
            }
        }
        
        if (!filteredMandatoryFields.isEmpty()) {
            appender.append("    public ").append(getClassName()).append("() {\n"
                    + "    }\n"
                    + "\n");
        }

        appender.append("    public ").append(getClassName()).append("(");
        writeArguments(appender, filteredMandatoryFields);
        appender.append(") {\n");
        writeFieldsInitialization(appender, filteredMandatoryFields);
        appender.append("    }");

        if (operation.getOperationKind() == OperationKind.SELECT_PAGE) {
            appender.append("\n"
                    + "\n"
                    + "    public ").append(getClassName()).append("(");
            writeArgumentsForAddMore(appender, filteredMandatoryFields);
            appender.append(PAGE_INFO_DATA_TYPE.getSimpleName());
            appender.append(" limit) {\n");
            writeFieldsInitialization(appender, filteredMandatoryFields);
            appender.append("        this.limit = limit;\n"
                    + "    }\n"
                    + "\n"
                    + "    public ").append(getClassName()).append("(");
            writeArgumentsForAddMore(appender, filteredMandatoryFields);
            appender.append(PAGE_INFO_DATA_TYPE.getSimpleName());
            appender.append(" limit, ");
            appender.append(PAGE_INFO_DATA_TYPE.getSimpleName());
            appender.append(" offset) {\n");
            writeFieldsInitialization(appender, filteredMandatoryFields);
            appender.append("        this.limit = limit;\n"
                    + "        this.offset = offset;\n"
                    + "    }\n"
                    + "\n"
                    + "    public ").append(getClassName()).append("(");
            writeArgumentsForAddMore(appender, filteredMandatoryFields);
            appender.append(PAGE_INFO_DATA_TYPE.getSimpleName());
            appender.append(" limit, ");
            appender.append(PAGE_INFO_DATA_TYPE.getSimpleName());
            appender.append(" offset, ");
            appender.append(PAGE_INFO_DATA_TYPE.getSimpleName());
            appender.append(" dataCount) {\n");
            writeFieldsInitialization(appender, filteredMandatoryFields);
            appender.append("        this.limit = limit;\n"
                    + "        this.offset = offset;\n"
                    + "        this.dataCount = dataCount;\n"
                    + "    }\n"
                    + "\n"
                    + "    public ").append(getClassName()).append("(");
            writeArgumentsForAddMore(appender, filteredMandatoryFields);
            appender.append(PAGE_ONLY_DATA_COUNT_DATA_TYPE.getSimpleName());
            appender.append(" onlyDataCount) {\n");
            writeFieldsInitialization(appender, filteredMandatoryFields);
            appender.append("        this.onlyDataCount = onlyDataCount;\n"
                    + "    }");
        }
    }

    @Override
    protected void writeContent(Appendable appender) throws IOException {
        boolean hasExtend = operation.getExtend() != null;
        
        for (FieldInfo field : operation.getFields()) {
            writeField(appender, field);
        }

        for (FieldInfo field : operation.getFields()) {
            appender.append("\n");
            writeFieldGetter(appender, field);
            appender.append("\n");
            writeFieldSetter(appender, field);
        }

        if (operation.getOperationKind() == OperationKind.SELECT_PAGE) {
            appender.append("\n");
            writeGetMaxRowNumber(appender);
        }

        appender.append("\n");
        writeToString(appender, operation.getFields(), hasExtend);

        appender.append("\n");
        writeEquals(appender, operation.getFields(), hasExtend);
        
        String firstPrime = Integer.toString(operation.generateFistPrimeNumberForHashCode());
        String secondPrime = Integer.toString(operation.generateSecondPrimeNumberForHashCode());
        
        appender.append("\n");
        writeHashCode(appender, operation.getFields(), hasExtend, firstPrime, secondPrime);

        appender.append("\n");
        writeVisit(appender);
        appender.append("\n");

        appender.append("    @Override\n"
                + "    public Object getExecutorSelector() {\n"
                + "        return ").append(executorName).append(".SELECTOR;\n"
                + "    }\n"
                + "\n");

        writeConstructors(appender);
    }
}
