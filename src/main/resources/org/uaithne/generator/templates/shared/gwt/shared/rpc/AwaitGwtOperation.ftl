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

import ${generation.sharedPackageDot}Executor;
import ${generation.sharedPackageDot}Operation;
<#if generation.useResultWrapperInterface>
import ${generation.sharedPackageDot}ResultWrapper;
</#if>

public class AwaitGwtOperation implements Operation<AwaitGwtResult> {

    @Override
    public Object getExecutorSelector() {
        return GwtOperationExecutor.SELECTOR;
    }

    @Override
    public AwaitGwtResult execute(Executor executor) {
        if (executor == null) {
            throw new IllegalArgumentException("executor for execute " + this.getClass().getName() + " AwaitGwtOperation cannot be null");
        }
        return ((GwtOperationExecutor)executor).executeAwaitGwtOperation(this);
    }

    @Override
    public AwaitGwtResult executePostOperation(AwaitGwtResult result) {
        return result;
    }

    <#if generation.useResultWrapperInterface>
    @Override
    public ResultWrapper<AwaitGwtResult> wrapResult(AwaitGwtResult result) {
        return new AwaitGwtOperationResultWrapper(result);
    }

    static class AwaitGwtOperationResultWrapper implements ResultWrapper<AwaitGwtResult> {
        private AwaitGwtResult value;

        @Override
        public AwaitGwtResult getValue() {
            return value;
        }

        public void setValue(AwaitGwtResult value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return "AwaitGwtOperationResultWrapper{value=" + value + "}";
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final AwaitGwtOperationResultWrapper other = (AwaitGwtOperationResultWrapper) obj;
            if ((this.value == null) ? (other.value != null) : !this.value.equals(other.value)) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 17 * hash + (this.value != null ? this.value.hashCode() : 0);
            return hash;
        }

        AwaitGwtOperationResultWrapper(AwaitGwtResult value) {
            this.value = value;
        }

        public AwaitGwtOperationResultWrapper() {
        }
    }
    </#if>
}
