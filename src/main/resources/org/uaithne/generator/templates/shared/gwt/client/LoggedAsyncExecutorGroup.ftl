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
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoggedAsyncExecutorGroup implements AsyncExecutorGroup {

    private AsyncExecutorGroup chainedExecutorGroup;
    private Logger logger;
    private String shortName;

    @Override
    public ${operationBaseDefinition} void execute(final OPERATION operation, final AsyncCallback<RESULT> asyncCallback) {
        AsyncCallback<RESULT> loggedAsyncCallback = new AsyncCallback<RESULT>() {

            @Override
            public void onSuccess(RESULT result) {
                if (logger.isLoggable(Level.FINE)) {
                    logger.log(Level.FINE, shortName + ": Execute operation executed. Operation: " + operation + ". Result: " + result);
                }
                asyncCallback.onSuccess(result);
            }

            @Override
            public void onFailure(Throwable caught) {
                logger.log(Level.SEVERE, "An exception has been ocurrend when execute an operation. Operation: " + operation, caught);
                asyncCallback.onFailure(caught);
            }
        };
        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, shortName + ": Execute operation requested. Operation: " + operation);
        }
        chainedExecutorGroup.execute(operation, loggedAsyncCallback);
    }

    /**
     * @return the chainedExecutorGroup
     */
    public AsyncExecutorGroup getChainedExecutorGroup() {
        return chainedExecutorGroup;
    }
    
    public LoggedAsyncExecutorGroup(AsyncExecutorGroup chainedExecutorGroup, Logger logger, String shortName) {
        if (chainedExecutorGroup == null) {
            throw new IllegalArgumentException("chainedExecutorGroup for the LoggedAsyncExecutorGroup cannot be null");
        }
        if (logger == null) {
            throw new IllegalArgumentException("logger for the LoggedAsyncExecutorGroup cannot be null");
        }
        if (shortName == null) {
            throw new IllegalArgumentException("shortName for the LoggedAsyncExecutorGroup cannot be null");
        }
        this.shortName = shortName;
        this.logger = logger;
        this.chainedExecutorGroup = chainedExecutorGroup;
    }
    
}
