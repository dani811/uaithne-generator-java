/*
 * Copyright 2012 and beyond, Juan Luis Paz
 *
 * This file is part of Uaithne.
 *
 * Uaithne is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Uaithne is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Uaithne. If not, see <http://www.gnu.org/licenses/>.
 */
package org.uaithne.generator.processors.database.providers.oracle;

import org.uaithne.generator.commons.EntityInfo;
import org.uaithne.generator.commons.OperationInfo;
import org.uaithne.generator.processors.database.QueryGenerator;
import org.uaithne.generator.processors.database.QueryGeneratorConfiguration;

public class OracleSqlProcedureGenerator extends OracleSqlAbstractProcedureGenerator {

    private QueryGenerator queryGenerator = new OracleBasicSqlQueryGenerator();
    private String[] body;

    @Override
    public void setConfiguration(QueryGeneratorConfiguration configuration) {
        super.setConfiguration(configuration);
        if (queryGenerator != null) {
            queryGenerator.setConfiguration(configuration);
        }
    }

    @Override
    public void begin() {
        super.begin();
        if (queryGenerator != null) {
            queryGenerator.begin();
        }
    }

    @Override
    public void end() {
        super.end();
        if (queryGenerator != null) {
            queryGenerator.end();
        }
    }

    @Override
    protected void appendProcedureStart(StringBuilder query) {
        query.append(getFirstLevelIdentation());
        query.append("create or replace\nprocedure ");
    }

    protected void appendProcedureEnd(StringBuilder query) {
        query.append(";\n");
        query.append(getFirstLevelIdentation());
        query.append("/");
    }
    
    @Override
    public void appendStartEntityDeleteByIdQuery(StringBuilder query, EntityInfo entity, OperationInfo operation) {
        // Compute first the query for load the applications parameters used in the other query generators
        body = queryGenerator.getEntityDeleteByIdQuery(entity, operation);
        super.appendStartEntityDeleteByIdQuery(query, entity, operation);
    }

    @Override
    public void appendStartEntityInsertQuery(StringBuilder query, EntityInfo entity, OperationInfo operation) {
        // Compute first the query for load the applications parameters used in the other query generators
        body = queryGenerator.getEntityInsertQuery(entity, operation);
        super.appendStartEntityInsertQuery(query, entity, operation);
    }

    @Override
    public void appendStartEntityLastInsertedIdQuery(StringBuilder query, EntityInfo entity, OperationInfo operation) {
        // Compute first the query for load the applications parameters used in the other query generators
        body = queryGenerator.getEntityLastInsertedIdQuery(entity, operation, false);
        super.appendStartEntityLastInsertedIdQuery(query, entity, operation);
    }

    @Override
    public void appendStartEntityMergeQuery(StringBuilder query, EntityInfo entity, OperationInfo operation) {
        // Compute first the query for load the applications parameters used in the other query generators
        body = queryGenerator.getEntityMergeQuery(entity, operation);
        super.appendStartEntityMergeQuery(query, entity, operation);
    }

    @Override
    public void appendStartEntityUpdateQuery(StringBuilder query, EntityInfo entity, OperationInfo operation) {
        // Compute first the query for load the applications parameters used in the other query generators
        body = queryGenerator.getEntityUpdateQuery(entity, operation);
        super.appendStartEntityUpdateQuery(query, entity, operation);
    }

    @Override
    public void appendStartCustomDeleteQuery(StringBuilder query, OperationInfo operation) {
        // Compute first the query for load the applications parameters used in the other query generators
        body = queryGenerator.getCustomDeleteQuery(operation);
        super.appendStartCustomDeleteQuery(query, operation);
    }

    @Override
    public void appendStartCustomInsertQuery(StringBuilder query, OperationInfo operation) {
        // Compute first the query for load the applications parameters used in the other query generators
        body = queryGenerator.getCustomInsertQuery(operation);
        super.appendStartCustomInsertQuery(query, operation);
    }

    @Override
    public void appendStartCustomUpdateQuery(StringBuilder query, OperationInfo operation) {
        // Compute first the query for load the applications parameters used in the other query generators
        body = queryGenerator.getCustomUpdateQuery(operation);
        super.appendStartCustomUpdateQuery(query, operation);
    }

    @Override
    public void appendEndEntityDeleteByIdQuery(StringBuilder query, EntityInfo entity, OperationInfo operation, boolean requireSeparator) {
        query.append("\n");
        query.append(getFirstLevelIdentation());
        if (requireSeparator) {
            query.append(") is\n");
        } else {
            query.append("is\n");
        }
        query.append(getFirstLevelIdentation());
        query.append("begin\n");
        appendToQuery(query, body, getSecondLevelIdentation());
        query.append(";\n");
        query.append(getFirstLevelIdentation());
        query.append("end ");
        appendProcedureName(query, operation);
        appendProcedureEnd(query);
    }

    @Override
    public void appendEndEntityInsertQuery(StringBuilder query, EntityInfo entity, OperationInfo operation, boolean requireSeparator) {
        query.append("\n");
        query.append(getFirstLevelIdentation());
        if (requireSeparator) {
            query.append(") is\n");
        } else {
            query.append("is\n");
        }
        query.append(getFirstLevelIdentation());
        query.append("begin\n");
        appendToQuery(query, body, getSecondLevelIdentation());
        query.append(";\n");
        query.append(getFirstLevelIdentation());
        query.append("end ");
        appendProcedureName(query, operation);
        appendProcedureEnd(query);
    }

    @Override
    public void appendEndEntityLastInsertedIdQuery(StringBuilder query, EntityInfo entity, OperationInfo operation, boolean requireSeparator) {
        query.append("\n");
        query.append(getFirstLevelIdentation());
        if (requireSeparator) {
            query.append(") is\n");
        } else {
            query.append("is\n");
        }
        query.append(getFirstLevelIdentation());
        query.append("begin\n");
        appendToQuery(query, body, getSecondLevelIdentation());
        query.append(";\n");
        query.append(getFirstLevelIdentation());
        query.append("end ");
        appendProcedureName(query, operation);
        appendProcedureEnd(query);
    }

    @Override
    public void appendEndEntityMergeQuery(StringBuilder query, EntityInfo entity, OperationInfo operation, boolean requireSeparator) {
        query.append("\n");
        query.append(getFirstLevelIdentation());
        if (requireSeparator) {
            query.append(") is\n");
        } else {
            query.append("is\n");
        }
        query.append(getFirstLevelIdentation());
        query.append("begin\n");
        appendToQuery(query, body, getSecondLevelIdentation());
        query.append(";\n");
        query.append(getFirstLevelIdentation());
        query.append("end ");
        appendProcedureName(query, operation);
        appendProcedureEnd(query);
    }

    @Override
    public void appendEndEntityUpdateQuery(StringBuilder query, EntityInfo entity, OperationInfo operation, boolean requireSeparator) {
        query.append("\n");
        query.append(getFirstLevelIdentation());
        if (requireSeparator) {
            query.append(") is\n");
        } else {
            query.append("is\n");
        }
        query.append(getFirstLevelIdentation());
        query.append("begin\n");
        appendToQuery(query, body, getSecondLevelIdentation());
        query.append(";\n");
        query.append(getFirstLevelIdentation());
        query.append("end ");
        appendProcedureName(query, operation);
        appendProcedureEnd(query);
    }

    @Override
    public void appendEndCustomDeleteQuery(StringBuilder query, OperationInfo operation, boolean requireSeparator) {
        query.append("\n");
        query.append(getFirstLevelIdentation());
        if (requireSeparator) {
            query.append(") is\n");
        } else {
            query.append("is\n");
        }
        query.append(getFirstLevelIdentation());
        query.append("begin\n");
        appendToQuery(query, body, getSecondLevelIdentation());
        query.append(";\n");
        query.append(getFirstLevelIdentation());
        query.append("end ");
        appendProcedureName(query, operation);
        appendProcedureEnd(query);
    }

    @Override
    public void appendEndCustomInsertQuery(StringBuilder query, OperationInfo operation, boolean requireSeparator) {
        query.append("\n");
        query.append(getFirstLevelIdentation());
        if (requireSeparator) {
            query.append(") is\n");
        } else {
            query.append("is\n");
        }
        query.append(getFirstLevelIdentation());
        query.append("begin\n");
        appendToQuery(query, body, getSecondLevelIdentation());
        query.append(";\n");
        query.append(getFirstLevelIdentation());
        query.append("end ");
        appendProcedureName(query, operation);
        appendProcedureEnd(query);
    }

    @Override
    public void appendEndCustomUpdateQuery(StringBuilder query, OperationInfo operation, boolean requireSeparator) {
        query.append("\n");
        query.append(getFirstLevelIdentation());
        if (requireSeparator) {
            query.append(") is\n");
        } else {
            query.append("is\n");
        }
        query.append(getFirstLevelIdentation());
        query.append("begin\n");
        appendToQuery(query, body, getSecondLevelIdentation());
        query.append(";\n");
        query.append(getFirstLevelIdentation());
        query.append("end ");
        appendProcedureName(query, operation);
        appendProcedureEnd(query);
    }

}
