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

<#list imports as import>
import ${import};
</#list>

public class ${className} implements Result {
    
    private ${valueType.simpleName} value;

    public ${valueType.simpleName} getValue() {
        return value;
    }

    public void setValue(${valueType.simpleName} value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "${className}{value=" + value + "}";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ${className} other = (${className}) obj;
        if (${valueType.generateEqualsRule("value")}) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = ${valueType.generateFistPrimeNumberForHashCode()};
        <#assign prime=valueType.generateSecondPrimeNumberForHashCode()>
        hash = ${prime} * hash + ${valueType.generateHashCodeRule("value")};
        return hash;
    }

    public ${className}() {
    }

    public ${className}(${valueType.simpleName} value) {
        this.value = value;
    }
    
}
