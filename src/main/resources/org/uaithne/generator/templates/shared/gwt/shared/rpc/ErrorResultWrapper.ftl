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

import ${generation.sharedPackageDot}ResultWrapper;
<#if rpcErrorAsString>
<#assign errorDataType="String">
<#else>
<#assign errorDataType="Throwable">
</#if>

public class ErrorResultWrapper implements ResultWrapper<${errorDataType}> {
    
    private ${errorDataType} value;

    @Override
    public ${errorDataType} getValue() {
        return value;
    }

    public void setValue(${errorDataType} value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "ErrorResultWrapper{value=" + value + "}";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ErrorResultWrapper other = (ErrorResultWrapper) obj;
        if ((this.value == null) ? (other.value != null) : !this.value.equals(other.value)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + (this.value != null ? this.value.hashCode() : 0);
        return hash;
    }

    public ErrorResultWrapper() {
    }

    public ErrorResultWrapper(${errorDataType} value) {
        this.value = value;
    }
    
}
