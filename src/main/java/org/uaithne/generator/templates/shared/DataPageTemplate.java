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

public class DataPageTemplate extends ClassTemplate {

    public DataPageTemplate(String packageName) {
        setPackageName(packageName);
        addImport(SERIALIZABLE_DATA_TYPE, packageName);
        addImport(LIST_DATA_TYPE, packageName);
        addImport(PAGE_INFO_DATA_TYPE, packageName);
        setClassName("DataPage");
        addGenericArgument(RESULT_BASE_DEFINITION);
        addImplement(SERIALIZABLE_DATA);
    }
    
    @Override
    protected void writeContent(Appendable appender) throws IOException {
        appender.append("    private ").append(PAGE_INFO_DATA).append(" limit;\n"
                + "    private ").append(PAGE_INFO_DATA).append(" offset;\n"
                + "    private ").append(PAGE_INFO_DATA).append(" dataCount;\n"
                + "    private ").append(LIST_DATA).append("<RESULT> data;\n"
                + "\n"
                + "    /**\n"
                + "     * @return the limit\n"
                + "     */\n"
                + "    public ").append(PAGE_INFO_DATA).append(" getLimit() {\n"
                + "        return limit;\n"
                + "    }\n"
                + "\n"
                + "    /**\n"
                + "     * @param limit the limit to set\n"
                + "     */\n"
                + "    public void setLimit(").append(PAGE_INFO_DATA).append(" limit) {\n"
                + "        this.limit = limit;\n"
                + "    }\n"
                + "\n"
                + "    /**\n"
                + "     * @return the offset\n"
                + "     */\n"
                + "    public ").append(PAGE_INFO_DATA).append(" getOffset() {\n"
                + "        return offset;\n"
                + "    }\n"
                + "\n"
                + "    /**\n"
                + "     * @param offset the offset to set\n"
                + "     */\n"
                + "    public void setOffset(").append(PAGE_INFO_DATA).append(" offset) {\n"
                + "        this.offset = offset;\n"
                + "    }\n"
                + "\n"
                + "    /**\n"
                + "     * @return the maxRowCount\n"
                + "     */\n"
                + "    public ").append(PAGE_INFO_DATA).append(" getMaxRowNumber() {\n");
        if (PAGE_INFO_DATA_TYPE.getPackageName().equals("")) {
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

        appender.append("    }\n"
                + "\n"
                + "    /**\n"
                + "     * @return the dataCount\n"
                + "     */\n"
                + "    public ").append(PAGE_INFO_DATA).append(" getDataCount() {\n"
                + "        return dataCount;\n"
                + "    }\n"
                + "\n"
                + "    /**\n"
                + "     * @param dataCount the dataCount to set\n"
                + "     */\n"
                + "    public void setDataCount(").append(PAGE_INFO_DATA).append(" dataCount) {\n"
                + "        this.dataCount = dataCount;\n"
                + "    }\n"
                + "\n"
                + "    /**\n"
                + "     * @return the data\n"
                + "     */\n"
                + "    public ").append(LIST_DATA).append("<RESULT> getData() {\n"
                + "        return data;\n"
                + "    }\n"
                + "\n"
                + "    /**\n"
                + "     * @param data the data to set\n"
                + "     */\n"
                + "    public void setData(").append(LIST_DATA).append("<RESULT> data) {\n"
                + "        this.data = data;\n"
                + "    }\n"
                + "\n"
                + "    @Override\n"
                + "    public boolean equals(Object obj) {\n"
                + "        if (obj == null) {\n"
                + "            return false;\n"
                + "        }\n"
                + "        if (getClass() != obj.getClass()) {\n"
                + "            return false;\n"
                + "        }\n"
                + "        final DataPage other = (DataPage) obj;\n"
                + "        if (").append(PAGE_INFO_DATA_TYPE.generateEqualsRule("limit")).append(") {\n"
                + "            return false;\n"
                + "        }\n"
                + "        if (").append(PAGE_INFO_DATA_TYPE.generateEqualsRule("offset")).append(") {\n"
                + "            return false;\n"
                + "        }\n"
                + "        if (").append(PAGE_INFO_DATA_TYPE.generateEqualsRule("dataCount")).append(") {\n"
                + "            return false;\n"
                + "        }\n"
                + "        if ((this.data == null) ? (other.data != null) : !this.data.equals(other.data)) {\n"
                + "            return false;\n"
                + "        }\n"
                + "        return true;\n"
                + "    }\n"
                + "\n"
                + "    @Override\n"
                + "    public int hashCode() {\n"
                + "        int hash = 5;\n"
                + "        hash = 23 * hash + ").append(PAGE_INFO_DATA_TYPE.generateHashCodeRule("limit")).append(";\n"
                + "        hash = 23 * hash + ").append(PAGE_INFO_DATA_TYPE.generateHashCodeRule("offset")).append(";\n"
                + "        hash = 23 * hash + ").append(PAGE_INFO_DATA_TYPE.generateHashCodeRule("dataCount")).append(";\n"
                + "        hash = 23 * hash + (this.data != null ? this.data.hashCode() : 0);\n"
                + "        return hash;\n"
                + "    }\n"
                + "\n"
                + "    @Override\n"
                + "    public String toString() {\n"
                + "        return \"DataPage{limit=\" + limit + \"| offset=\" + offset + \"| dataCount=\" + dataCount + \"| data=\" + data + \"}\";\n"
                + "    }\n"
                + "\n"
                + "    public DataPage() {\n"
                + "    }\n"
                + "\n"
                + "    public DataPage(").append(PAGE_INFO_DATA).append(" limit, ").append(PAGE_INFO_DATA).append(" offset, ").append(PAGE_INFO_DATA).append(" dataCount, ").append(LIST_DATA).append("<RESULT> data) {\n"
                + "        this.limit = limit;\n"
                + "        this.offset = offset;\n"
                + "        this.dataCount = dataCount;\n"
                + "        this.data = data;\n"
                + "    }");
    }
    
}
