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
package org.uaithne.generator.processors.database;

import org.uaithne.generator.commons.EntityInfo;
import org.uaithne.generator.commons.OperationInfo;

public interface QueryGenerator {
    public QueryGeneratorConfiguration getConfiguration();
    public void setConfiguration(QueryGeneratorConfiguration configuration);
    public void begin();
    public void end();
    public String[] getSelectManyQuery(OperationInfo operation);
    public String[] getSelectOneQuery(OperationInfo operation);
    public String[] getSelectPageCountQuery(OperationInfo operation);
    public String[] getSelectPageQuery(OperationInfo operation);
    public String[] getEntityDeleteByIdQuery(EntityInfo entity, OperationInfo operation);
    public String[] getEntityInsertQuery(EntityInfo entity, OperationInfo operation);
    public String[] getEntityLastInsertedIdQuery(EntityInfo entity, OperationInfo operation, boolean excludeSequenceQuery);
    public String[] getEntityMergeQuery(EntityInfo entity, OperationInfo operation);
    public String[] getEntitySelectByIdQuery(EntityInfo entity, OperationInfo operation);
    public String[] getEntityUpdateQuery(EntityInfo entity, OperationInfo operation);
    public String[] getCustomDeleteQuery(OperationInfo operation);
    public String[] getCustomInsertQuery(OperationInfo operation);
    public String[] getCustomUpdateQuery(OperationInfo operation);
    public boolean useAliasInOrderByTranslation();
}
