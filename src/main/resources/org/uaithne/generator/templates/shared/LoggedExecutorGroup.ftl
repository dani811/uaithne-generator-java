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

import java.util.logging.Level;
import java.util.logging.Logger;

public class LoggedExecutorGroup implements ExecutorGroup {

    private ExecutorGroup chainedExecutorGroup;
    private Logger logger;
    private String shortName;

    @Override
    public ${operationBaseDefinition} RESULT execute(OPERATION operation) {
        boolean fineLevelEnabled = logger.isLoggable(Level.FINE);
        try {
            if (fineLevelEnabled) {
                logger.log(Level.FINE, shortName + ": Execute operation requested. Operation: " + operation);
                RESULT result = chainedExecutorGroup.execute(operation);
                logger.log(Level.FINE, shortName + ": Execute operation executed. Operation: " + operation + ". Result: " + result);
                return result;
            } else {
                return chainedExecutorGroup.execute(operation);
            }
        } catch (RuntimeException e) {
            logger.log(Level.SEVERE, "An exception has been ocurrend when execute an operation. Operation: " + operation, e);
            throw e;
        }
    }

    /**
     * @return the chainedExecutorGroup
     */
    public ExecutorGroup getChainedExecutorGroup() {
        return chainedExecutorGroup;
    }
    
    public LoggedExecutorGroup(ExecutorGroup chainedExecutorGroup, Logger logger, String shortName) {
        if (chainedExecutorGroup == null) {
            throw new IllegalArgumentException("chainedExecutorGroup for the LoggedExecutorGroup cannot be null");
        }
        if (logger == null) {
            throw new IllegalArgumentException("logger for the LoggedExecutorGroup cannot be null");
        }
        if (shortName == null) {
            throw new IllegalArgumentException("shortName for the LoggedExecutorGroup cannot be null");
        }
        this.shortName = shortName;
        this.logger = logger;
        this.chainedExecutorGroup = chainedExecutorGroup;
    }
    
}
