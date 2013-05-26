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

public class DataPageRequestTemplate extends ClassTemplate {

    public DataPageRequestTemplate(String packageName) {
        setPackageName(packageName);
        addImport(SERIALIZABLE_DATA_TYPE, packageName);
        addImport(PAGE_INFO_DATA_TYPE, packageName);
        addImport(PAGE_ONLY_DATA_COUNT_DATA_TYPE, packageName);
        setClassName("DataPageRequest");
        addImplement(SERIALIZABLE_DATA);
        setInterface(true);
    }
    
    @Override
    protected void writeContent(Appendable appender) throws IOException {
        appender.append("    public ").append(PAGE_INFO_DATA).append(" getLimit();\n"
                + "    public void setLimit(").append(PAGE_INFO_DATA).append(" limit);\n"
                + "    \n"
                + "    public ").append(PAGE_INFO_DATA).append(" getOffset();\n"
                + "    public void setOffset(").append(PAGE_INFO_DATA).append(" offset);\n"
                + "    \n"
                + "    public ").append(PAGE_INFO_DATA).append(" getMaxRowNumber();\n"
                + "    \n"
                + "    public ").append(PAGE_INFO_DATA).append(" getDataCount();\n"
                + "    public void setDataCount(").append(PAGE_INFO_DATA).append(" dataCount);\n"
                + "    \n"
                + "    public ").append(PAGE_ONLY_DATA_COUNT_DATA).append(" isOnlyDataCount();\n"
                + "    public void setOnlyDataCount(").append(PAGE_ONLY_DATA_COUNT_DATA).append(" onlyDataCount);");
    }
    
}
