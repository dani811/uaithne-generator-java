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

import org.uaithne.generator.processors.database.sql.SqlCallOrQueryGenerator;
import java.io.IOException;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import org.uaithne.generator.commons.EntityInfo;
import org.uaithne.generator.commons.OperationInfo;
import org.uaithne.generator.processors.database.QueryGenerator;
import org.uaithne.generator.processors.database.QueryGeneratorConfiguration;
import org.uaithne.generator.processors.database.sql.SqlGenerator;

public class OracleCallPackageOrQueryGenerator extends SqlGenerator {

    private boolean useCallForSelect;
    private boolean useCallForInsert = true;
    private boolean useCallForUpdate = true;
    private boolean useCallForDelete = true;
    private boolean useCallForGetLastInsertedId;
    private QueryGenerator callGenerator;
    private QueryGenerator queryGenerator;
    private QueryGenerator headerGenerator;
    private QueryGenerator bodyGenerator;
    private StringBuilder body;
    private Writer writer;

    public OracleCallPackageOrQueryGenerator(QueryGenerator callGenerator, QueryGenerator queryGenerator, QueryGenerator headerGenerator, QueryGenerator bodyGenerator) {
        this.callGenerator = callGenerator;
        this.queryGenerator = queryGenerator;
        this.headerGenerator = headerGenerator;
        this.bodyGenerator = bodyGenerator;
    }

    public boolean isUseCallForSelect() {
        return useCallForSelect;
    }

    public void setUseCallForSelect(boolean useCallForSelect) {
        this.useCallForSelect = useCallForSelect;
    }

    public boolean isUseCallForInsert() {
        return useCallForInsert;
    }

    public void setUseCallForInsert(boolean useCallForInsert) {
        this.useCallForInsert = useCallForInsert;
    }

    public boolean isUseCallForUpdate() {
        return useCallForUpdate;
    }

    public void setUseCallForUpdate(boolean useCallForUpdate) {
        this.useCallForUpdate = useCallForUpdate;
    }

    public boolean isUseCallForDelete() {
        return useCallForDelete;
    }

    public void setUseCallForDelete(boolean useCallForDelete) {
        this.useCallForDelete = useCallForDelete;
    }

    public boolean isUseCallForGetLastInsertedId() {
        return useCallForGetLastInsertedId;
    }

    public void setUseCallForGetLastInsertedId(boolean useCallForGetLastInsertedId) {
        this.useCallForGetLastInsertedId = useCallForGetLastInsertedId;
    }

    public QueryGenerator getCallGenerator() {
        return callGenerator;
    }

    public void setCallGenerator(QueryGenerator callGenerator) {
        this.callGenerator = callGenerator;
    }

    public QueryGenerator getQueryGenerator() {
        return queryGenerator;
    }

    public void setQueryGenerator(QueryGenerator queryGenerator) {
        this.queryGenerator = queryGenerator;
    }

    public QueryGenerator getHeaderGenerator() {
        return headerGenerator;
    }

    public void setHeaderGenerator(QueryGenerator headerGenerator) {
        this.headerGenerator = headerGenerator;
    }

    public QueryGenerator getBodyGenerator() {
        return bodyGenerator;
    }

    public void setBodyGenerator(QueryGenerator bodyGenerator) {
        this.bodyGenerator = bodyGenerator;
    }

    @Override
    public void setConfiguration(QueryGeneratorConfiguration configuration) {
        super.setConfiguration(configuration);
        if (callGenerator != null) {
            callGenerator.setConfiguration(configuration);
        }
        if (queryGenerator != null) {
            queryGenerator.setConfiguration(configuration);
        }
        if (headerGenerator != null) {
            headerGenerator.setConfiguration(configuration);
        }
        if (bodyGenerator != null) {
            bodyGenerator.setConfiguration(configuration);
        }
    }

    @Override
    public void begin() {
        super.begin();
        if (callGenerator != null) {
            callGenerator.begin();
        }
        if (queryGenerator != null) {
            queryGenerator.begin();
        }
        if (headerGenerator != null) {
            headerGenerator.begin();
        }
        if (bodyGenerator != null) {
            bodyGenerator.begin();
        }
    }

    @Override
    public void end() {
        super.end();
        if (callGenerator != null) {
            callGenerator.end();
        }
        if (queryGenerator != null) {
            queryGenerator.end();
        }
        if (headerGenerator != null) {
            headerGenerator.end();
        }
        if (bodyGenerator != null) {
            bodyGenerator.end();
        }
        try {
            QueryGeneratorConfiguration configuration = getConfiguration();
            if (writer != null) {
                String name = configuration.getName();
                writer.write("end ");
                writer.write(name);
                writer.write(";\n");
                writer.write("/\n\n");
                writer.write("create or replace\n");
                writer.write("package body ");
                writer.write(name);
                writer.write(" as\n\n");
                writer.write(body.toString());
                writer.write("end ");
                writer.write(name);
                writer.write(";\n/");
                writer.close();
            }
            body = null;
            configuration.setCallPrefix(null);
        } catch (IOException ex) {
            Logger.getLogger(SqlCallOrQueryGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }

        this.writer = null;
    }

    private void initSqlFile() {
        if (writer != null) {
            return;
        }
        try {
            QueryGeneratorConfiguration config = getConfiguration();
            String name = config.getName();
            FileObject fo = getProcessingEnv().getFiler().createResource(StandardLocation.SOURCE_OUTPUT, config.getPackageName(), name + ".sql", config.getModule().getElement());
            writer = fo.openWriter();
            body = new StringBuilder();
            writer.write("create or replace\npackage ");
            writer.write(name);
            writer.write(" as\n\n");
            config.setCallPrefix(name + ".");
        } catch (IOException ex) {
            Logger.getLogger(SqlCallOrQueryGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void write(String[] lines) {
        try {
            initSqlFile();
            if (writer != null && lines != null) {
                for (String line : lines) {
                    writer.write(line);
                    writer.write("\n");
                }
                writer.write("\n");
            }
        } catch (IOException ex) {
            Logger.getLogger(SqlCallOrQueryGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void writeBody(String[] lines) {
        initSqlFile();
        if (body != null && lines != null) {
            for (String line : lines) {
                body.append(line);
                body.append("\n");
            }
            body.append("\n");
        }
    }

    @Override
    public String[] getSelectManyQuery(OperationInfo operation) {
        if (useCallForSelect) {
            String[] header = headerGenerator.getSelectManyQuery(operation);
            write(header);
            writeBody(bodyGenerator.getSelectManyQuery(operation));
            return callGenerator.getSelectManyQuery(operation);
        } else {
            return queryGenerator.getSelectManyQuery(operation);
        }
    }

    @Override
    public String[] getSelectOneQuery(OperationInfo operation) {
        if (useCallForSelect) {
            String[] header = headerGenerator.getSelectOneQuery(operation);
            write(header);
            writeBody(bodyGenerator.getSelectOneQuery(operation));
            return callGenerator.getSelectOneQuery(operation);
        } else {
            return queryGenerator.getSelectOneQuery(operation);
        }
    }

    @Override
    public String[] getSelectCountQuery(OperationInfo operation) {
        if (useCallForSelect) {
            String[] header = headerGenerator.getSelectCountQuery(operation);
            write(header);
            writeBody(bodyGenerator.getSelectCountQuery(operation));
            return callGenerator.getSelectCountQuery(operation);
        } else {
            return queryGenerator.getSelectCountQuery(operation);
        }
    }

    @Override
    public String[] getSelectPageCountQuery(OperationInfo operation) {
        if (useCallForSelect) {
            String[] header = headerGenerator.getSelectPageCountQuery(operation);
            write(header);
            writeBody(bodyGenerator.getSelectPageCountQuery(operation));
            return callGenerator.getSelectPageCountQuery(operation);
        } else {
            return queryGenerator.getSelectPageCountQuery(operation);
        }
    }

    @Override
    public String[] getSelectPageQuery(OperationInfo operation) {
        if (useCallForSelect) {
            String[] header = headerGenerator.getSelectPageQuery(operation);
            write(header);
            writeBody(bodyGenerator.getSelectPageQuery(operation));
            return callGenerator.getSelectPageQuery(operation);
        } else {
            return queryGenerator.getSelectPageQuery(operation);
        }
    }

    @Override
    public String[] getEntityDeleteByIdQuery(EntityInfo entity, OperationInfo operation) {
        if (useCallForDelete) {
            String[] header = headerGenerator.getEntityDeleteByIdQuery(entity, operation);
            write(header);
            writeBody(bodyGenerator.getEntityDeleteByIdQuery(entity, operation));
            return callGenerator.getEntityDeleteByIdQuery(entity, operation);
        } else {
            return queryGenerator.getEntityDeleteByIdQuery(entity, operation);
        }
    }

    @Override
    public String[] getEntityInsertQuery(EntityInfo entity, OperationInfo operation) {
        if (useCallForInsert) {
            String[] header = headerGenerator.getEntityInsertQuery(entity, operation);
            write(header);
            writeBody(bodyGenerator.getEntityInsertQuery(entity, operation));
            return callGenerator.getEntityInsertQuery(entity, operation);
        } else {
            return queryGenerator.getEntityInsertQuery(entity, operation);
        }
    }

    @Override
    public String[] getEntityLastInsertedIdQuery(EntityInfo entity, OperationInfo operation, boolean excludeSequenceQuery) {
        if (useCallForGetLastInsertedId) {
            String[] header = headerGenerator.getEntityLastInsertedIdQuery(entity, operation, excludeSequenceQuery);
            write(header);
            writeBody(bodyGenerator.getEntityLastInsertedIdQuery(entity, operation, excludeSequenceQuery));
            return callGenerator.getEntityLastInsertedIdQuery(entity, operation, excludeSequenceQuery);
        } else {
            return queryGenerator.getEntityLastInsertedIdQuery(entity, operation, excludeSequenceQuery);
        }
    }

    @Override
    public String[] getEntityMergeQuery(EntityInfo entity, OperationInfo operation) {
        if (useCallForUpdate) {
            String[] header = headerGenerator.getEntityMergeQuery(entity, operation);
            write(header);
            writeBody(bodyGenerator.getEntityMergeQuery(entity, operation));
            return callGenerator.getEntityMergeQuery(entity, operation);
        } else {
            return queryGenerator.getEntityMergeQuery(entity, operation);
        }
    }

    @Override
    public String[] getEntitySelectByIdQuery(EntityInfo entity, OperationInfo operation) {
        if (useCallForSelect) {
            String[] header = headerGenerator.getEntitySelectByIdQuery(entity, operation);
            write(header);
            writeBody(bodyGenerator.getEntitySelectByIdQuery(entity, operation));
            return callGenerator.getEntitySelectByIdQuery(entity, operation);
        } else {
            return queryGenerator.getEntitySelectByIdQuery(entity, operation);
        }
    }

    @Override
    public String[] getEntityUpdateQuery(EntityInfo entity, OperationInfo operation) {
        if (useCallForUpdate) {
            String[] header = headerGenerator.getEntityUpdateQuery(entity, operation);
            write(header);
            writeBody(bodyGenerator.getEntityUpdateQuery(entity, operation));
            return callGenerator.getEntityUpdateQuery(entity, operation);
        } else {
            return queryGenerator.getEntityUpdateQuery(entity, operation);
        }
    }

    @Override
    public String[] getCustomDeleteQuery(OperationInfo operation) {
        if (useCallForDelete) {
            String[] header = headerGenerator.getCustomDeleteQuery(operation);
            write(header);
            writeBody(bodyGenerator.getCustomDeleteQuery(operation));
            return callGenerator.getCustomDeleteQuery(operation);
        } else {
            return queryGenerator.getCustomDeleteQuery(operation);
        }
    }

    @Override
    public String[] getCustomInsertQuery(OperationInfo operation) {
        if (useCallForInsert) {
            String[] header = headerGenerator.getCustomInsertQuery(operation);
            write(header);
            writeBody(bodyGenerator.getCustomInsertQuery(operation));
            return callGenerator.getCustomInsertQuery(operation);
        } else {
            return queryGenerator.getCustomInsertQuery(operation);
        }
    }

    @Override
    public String[] getCustomUpdateQuery(OperationInfo operation) {
        if (useCallForUpdate) {
            String[] header = headerGenerator.getCustomUpdateQuery(operation);
            write(header);
            writeBody(bodyGenerator.getCustomUpdateQuery(operation));
            return callGenerator.getCustomUpdateQuery(operation);
        } else {
            return queryGenerator.getCustomUpdateQuery(operation);
        }
    }
}
