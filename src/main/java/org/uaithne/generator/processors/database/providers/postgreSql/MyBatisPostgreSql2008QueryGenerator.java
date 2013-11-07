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
package org.uaithne.generator.processors.database.providers.postgreSql;

import org.uaithne.generator.commons.EntityInfo;
import org.uaithne.generator.commons.FieldInfo;
import org.uaithne.generator.processors.database.providers.sql.MyBatisSql2008QueryGenerator;

public class MyBatisPostgreSql2008QueryGenerator extends MyBatisSql2008QueryGenerator {

    @Override
    public String[] getIdSequenceNextValue(EntityInfo entity, FieldInfo field) {
        return new String[] {"nextval('" + getSequenceName(getTableName(entity)) + "')"};
    }
    
    @Override
    public String[] getIdSequenceCurrentValue(EntityInfo entity, FieldInfo field) {
        return new String[] {"select currval('" + getSequenceName(getTableName(entity)) + "')"};
    }

}
