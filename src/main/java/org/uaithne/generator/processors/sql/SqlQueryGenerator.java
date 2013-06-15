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
package org.uaithne.generator.processors.sql;

import javax.annotation.processing.ProcessingEnvironment;
import org.uaithne.generator.commons.EntityInfo;
import org.uaithne.generator.commons.OperationInfo;

public interface SqlQueryGenerator {
    public ProcessingEnvironment getProcessingEnv();
    public void setProcessingEnv(ProcessingEnvironment processingEnv);
    public String[] getSelectManyQuery(OperationInfo operation);
    public String[] getSelectOneQuery(OperationInfo operation);
    public String[] getSelectPageCountQuery(OperationInfo operation);
    public String[] getSelectPageQuery(OperationInfo operation);
    public String[] getEntityDeleteByIdQuery(EntityInfo entity);
    public String[] getEntityInsertQuery(EntityInfo entity);
    public String[] getEntityLastInsertedIdQuery(EntityInfo entity);
    public String[] getEntityMergeQuery(EntityInfo entity);
    public String[] getEntitySelectByIdQuery(EntityInfo entity);
    public String[] getEntityUpdateQuery(EntityInfo entity);
    public String[] getCustomDeleteQuery(OperationInfo operation);
    public String[] getCustomInsertQuery(OperationInfo operation);
    public String[] getCustomUpdateQuery(OperationInfo operation);
}
