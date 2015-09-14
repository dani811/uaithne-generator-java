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
package org.uaithne.generator.processors.database.providers.mySql;

import java.util.ArrayList;
import org.uaithne.annotations.sql.CustomSqlQuery;
import org.uaithne.generator.commons.EntityInfo;
import org.uaithne.generator.commons.FieldInfo;
import org.uaithne.generator.processors.database.providers.sql.MyBatisSql2008QueryGenerator;

public class MyBatisMySql2008QueryGenerator extends MyBatisSql2008QueryGenerator {

    @Override
    public String[] getIdSequenceNextValue(EntityInfo entity, FieldInfo field) {
        // nextval function must be manually defined. See: http://www.microshell.com/database/mysql/emulating-nextval-function-to-get-sequence-in-mysql/
        return new String[] {"nextval('" + getIdSequenceName(entity, field) + "')"};
    }
    
    @Override
    public String[] getIdSequenceCurrentValue(EntityInfo entity, FieldInfo field) {
        // currval function must be manually defined. See: http://www.microshell.com/database/mysql/emulating-nextval-function-to-get-sequence-in-mysql/
        return new String[] {"select currval('" + getIdSequenceName(entity, field) + "')"};
    }

    @Override
    public void appendSelectPageAfterOrderBy(StringBuilder result, ArrayList<FieldInfo> orderBys, CustomSqlQuery customQuery) {
        result.append("\n"
             + "{[if test='offset != null or limit != null']}\n"
             + "    {[if test='limit != null']}limit #{limit,jdbcType=NUMERIC}{[/if]}\n"
             + "    {[if test='offset != null']}offset #{offset,jdbcType=NUMERIC}{[/if]}\n"
             + "{[/if]}");
    }

    @Override
    public void appendSelectOneRowAfterOrderBy(StringBuilder result, ArrayList<FieldInfo> orderBys, CustomSqlQuery customQuery) {
        result.append("\nlimit 1");
    }
}
