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

import ${generation.sharedPackageDot}Result;
<#if rpcErrorAsString>
<#assign errorDataType="String">
<#else>
<#assign errorDataType="Throwable">
</#if>

public class ErrorResult implements Result {
    
    private ${errorDataType} error;

    public ${errorDataType} getError() {
        return error;
    }

    public void setError(${errorDataType} error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return "ErrorResult{error=" + error + "}";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ErrorResult other = (ErrorResult) obj;
        if ((this.error == null) ? (other.error != null) : !this.error.equals(other.error)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + (this.error != null ? this.error.hashCode() : 0);
        return hash;
    }

    public ErrorResult() {
    }

    public ErrorResult(${errorDataType} error) {
        this.error = error;
    }
    
}
