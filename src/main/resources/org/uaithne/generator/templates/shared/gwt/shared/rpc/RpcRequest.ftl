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
import com.google.gwt.user.client.rpc.GwtTransient;
import ${generation.sharedPackageDot}Operation;

public class RpcRequest implements Serializable {

    @GwtTransient
    private ArrayList<Operation> operations;

    public ArrayList<Operation> getOperations() {
        return operations;
    }

    public void setOperations(ArrayList<Operation> operations) {
        this.operations = operations;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final RpcRequest other = (RpcRequest) obj;
        if (this.operations != other.operations && (this.operations == null || !this.operations.equals(other.operations))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 23 * hash + (this.operations != null ? this.operations.hashCode() : 0);
        return hash;
    }

    public RpcRequest(ArrayList<Operation> operations) {
        this.operations = operations;
    }

    public RpcRequest() {
    }
    
}