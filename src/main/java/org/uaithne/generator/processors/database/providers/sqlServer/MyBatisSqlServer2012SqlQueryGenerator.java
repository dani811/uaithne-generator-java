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
package org.uaithne.generator.processors.database.providers.sqlServer;

import org.uaithne.annotations.Comparators;
import org.uaithne.generator.commons.EntityInfo;
import org.uaithne.generator.commons.FieldInfo;
import org.uaithne.generator.processors.database.providers.sql.MyBatisSql2008QueryGenerator;

public class MyBatisSqlServer2012SqlQueryGenerator extends MyBatisSql2008QueryGenerator {
    
    @Override
    public String falseValue() {
        return "0";
    }
    
    @Override
    public String trueValue() {
        return "1";
    }
    
    @Override
    public String[] getIdSequenceNextValue(EntityInfo entity, FieldInfo field) {
        return new String[] {"next value for " + getIdSequenceName(entity, field)};
    }
    
    @Override
    public String[] getIdSequenceCurrentValue(EntityInfo entity, FieldInfo field) {
        return new String[] {"select current_value from sys.sequences where name = '" + getIdSequenceName(entity, field) + "'"};
    }

    @Override
    public String translateComparator(Comparators comparator) {
        if (comparator == null) {
            return super.translateComparator(comparator);
        }
        switch (comparator) {
            case START_WITH:     return "[[column]] like ([[value]] + '%')";
            case NOT_START_WITH: return "[[column]] not like ([[value]] + '%')";
            case END_WITH:       return "[[column]] like ('%' + [[value]])";
            case NOT_END_WITH:   return "[[column]] not like ('%' + [[value]])";
            case START_WITH_INSENSITIVE:     return "lower([[column]]) like (lower([[value]]) + '%')";
            case NOT_START_WITH_INSENSITIVE: return "lower([[column]]) not like (lower([[value]]) + '%')";
            case END_WITH_INSENSITIVE:       return "lower([[column]]) like ('%' + lower([[value]]))";
            case NOT_END_WITH_INSENSITIVE:   return "lower([[column]]) not like ('%' + lower([[value]]))";
            case CONTAINS:       return "[[column]] like ('%' + [[value]] + '%')";
            case NOT_CONTAINS:   return "[[column]] not like ('%' + [[value]] + '%')";
            case CONTAINS_INSENSITIVE:       return "lower([[column]]) like ('%' + lower([[value]]) + '%')";
            case NOT_CONTAINS_INSENSITIVE:   return "lower([[column]]) not like ('%' + lower([[value]]) + '%')";
            default:
                return super.translateComparator(comparator);
        }
    }
    
}
