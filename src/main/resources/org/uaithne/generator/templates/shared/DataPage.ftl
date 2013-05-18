<#-- 
Copyright 2012 and beyond, Juan Luis Paz

This file is part of Uaithne.

Uaithne is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Uaithne is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with Uaithne. If not, see <http://www.gnu.org/licenses/>.
-->
package ${packageName};

import java.io.Serializable;
import ${listQualifiedName};
<#list imports as import>
import ${import};
</#list>

public class DataPage<${resultBaseDefinition}> implements Serializable {
    private ${pageInfoDataType.simpleName} limit;
    private ${pageInfoDataType.simpleName} offset;
    private ${pageInfoDataType.simpleName} dataCount;
    private ${listName}<RESULT> data;

    /**
     * @return the limit
     */
    public ${pageInfoDataType.simpleName} getLimit() {
        return limit;
    }

    /**
     * @param limit the limit to set
     */
    public void setLimit(${pageInfoDataType.simpleName} limit) {
        this.limit = limit;
    }

    /**
     * @return the offset
     */
    public ${pageInfoDataType.simpleName} getOffset() {
        return offset;
    }

    /**
     * @param offset the offset to set
     */
    public void setOffset(${pageInfoDataType.simpleName} offset) {
        this.offset = offset;
    }

    /**
     * @return the maxRowCount
     */
    public ${pageInfoDataType.simpleName} getMaxRowNumber() {
        <#if pageInfoDataType.packageName == "">
        return limit + offset;
        <#else>
        if (limit == null) {
            return null;
        }
        if (offset == null) {
            return limit;
        }
        return limit.add(offset);
        </#if>
    }

    /**
     * @return the dataCount
     */
    public ${pageInfoDataType.simpleName} getDataCount() {
        return dataCount;
    }

    /**
     * @param dataCount the dataCount to set
     */
    public void setDataCount(${pageInfoDataType.simpleName} dataCount) {
        this.dataCount = dataCount;
    }

    /**
     * @return the data
     */
    public ${listName}<RESULT> getData() {
        return data;
    }

    /**
     * @param data the data to set
     */
    public void setData(${listName}<RESULT> data) {
        this.data = data;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DataPage other = (DataPage) obj;
        if (${pageInfoDataType.generateEqualsRule("limit")}) {
            return false;
        }
        if (${pageInfoDataType.generateEqualsRule("offset")}) {
            return false;
        }
        if (${pageInfoDataType.generateEqualsRule("dataCount")}) {
            return false;
        }
        if ((this.data == null) ? (other.data != null) : !this.data.equals(other.data)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 23 * hash + ${pageInfoDataType.generateHashCodeRule("limit")};
        hash = 23 * hash + ${pageInfoDataType.generateHashCodeRule("offset")};
        hash = 23 * hash + ${pageInfoDataType.generateHashCodeRule("dataCount")};
        hash = 23 * hash + (this.data != null ? this.data.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "DataPage{limit=" + limit + "| offset=" + offset + "| dataCount=" + dataCount + "| data=" + data + "}";
    }

    public DataPage() {
    }

    public DataPage(${pageInfoDataType.simpleName} limit, ${pageInfoDataType.simpleName} offset, ${pageInfoDataType.simpleName} dataCount, ${listName}<RESULT> data) {
        this.limit = limit;
        this.offset = offset;
        this.dataCount = dataCount;
        this.data = data;
    }
    
}
