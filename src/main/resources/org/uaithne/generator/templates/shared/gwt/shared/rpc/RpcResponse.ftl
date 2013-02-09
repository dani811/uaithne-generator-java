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
import java.util.ArrayList;
<#if generation.useResultInterface>
import ${generation.sharedPackageDot}Result;
<#else>
import ${generation.sharedPackageDot}ResultWrapper;
</#if>


public class RpcResponse implements Serializable {

    private ArrayList<<#if generation.useResultInterface>Result<#else>ResultWrapper</#if>> result;

    public ArrayList<<#if generation.useResultInterface>Result<#else>ResultWrapper</#if>> getResult() {
        return result;
    }

    public void setResult(ArrayList<<#if generation.useResultInterface>Result<#else>ResultWrapper</#if>> result) {
        this.result = result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final RpcResponse other = (RpcResponse) obj;
        if (this.result != other.result && (this.result == null || !this.result.equals(other.result))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + (this.result != null ? this.result.hashCode() : 0);
        return hash;
    }

    public RpcResponse(ArrayList<<#if generation.useResultInterface>Result<#else>ResultWrapper</#if>> result) {
        this.result = result;
    }

    public RpcResponse() {
    }
    
}
