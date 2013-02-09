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
<#list imports as import>
import ${import};
</#list>

public interface DataPageRequest extends Serializable {
    public ${pageInfoDataType.simpleName} getLimit();
    public void setLimit(${pageInfoDataType.simpleName} limit);
    
    public ${pageInfoDataType.simpleName} getOffset();
    public void setOffset(${pageInfoDataType.simpleName} offset);
    
    public ${pageInfoDataType.simpleName} getMaxRowNumber();
    
    public ${pageInfoDataType.simpleName} getDataCount();
    public void setDataCount(${pageInfoDataType.simpleName} dataCount);
    
    public ${pageOnlyDataCountDataType.simpleName} isOnlyDataCount();
    public void setOnlyDataCount(${pageOnlyDataCountDataType.simpleName} onlyDataCount);
}
