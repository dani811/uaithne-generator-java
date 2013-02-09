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

<#if generation.useConcreteCollections>
    <#assign listName="ArrayList">
<#else>
    <#assign listName="List">
</#if>
<#if !generation.useResultInterface>
import java.io.Serializable;
</#if>
import java.util.${listName};

public class DataList<${resultBaseDefinition}> implements <#if generation.useResultInterface>Result<#else>Serializable</#if> {
    private ${listName}<RESULT> data;

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
        final DataList other = (DataList) obj;
        if ((this.data == null) ? (other.data != null) : !this.data.equals(other.data)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 47 * hash + (this.data != null ? this.data.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "DataList{data=" + data + "}";
    }

    public DataList() {
    }

    public DataList(${listName}<RESULT> data) {
        this.data = data;
    }
    
}
