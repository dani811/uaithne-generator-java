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
package org.uaithne.generator.processors.database.sql;

import javax.tools.Diagnostic;
import org.uaithne.annotations.Comparators;
import static org.uaithne.annotations.Comparators.CONTAINS;
import static org.uaithne.annotations.Comparators.CONTAINS_INSENSITIVE;
import static org.uaithne.annotations.Comparators.END_WITH;
import static org.uaithne.annotations.Comparators.END_WITH_INSENSITIVE;
import static org.uaithne.annotations.Comparators.EQUAL;
import static org.uaithne.annotations.Comparators.EQUAL_NULLABLE;
import static org.uaithne.annotations.Comparators.IN;
import static org.uaithne.annotations.Comparators.LARGER;
import static org.uaithne.annotations.Comparators.LARGER_AS;
import static org.uaithne.annotations.Comparators.LIKE;
import static org.uaithne.annotations.Comparators.LIKE_INSENSITIVE;
import static org.uaithne.annotations.Comparators.NOT_CONTAINS;
import static org.uaithne.annotations.Comparators.NOT_CONTAINS_INSENSITIVE;
import static org.uaithne.annotations.Comparators.NOT_END_WITH;
import static org.uaithne.annotations.Comparators.NOT_END_WITH_INSENSITIVE;
import static org.uaithne.annotations.Comparators.NOT_EQUAL;
import static org.uaithne.annotations.Comparators.NOT_EQUAL_NULLABLE;
import static org.uaithne.annotations.Comparators.NOT_IN;
import static org.uaithne.annotations.Comparators.NOT_LIKE;
import static org.uaithne.annotations.Comparators.NOT_LIKE_INSENSITIVE;
import static org.uaithne.annotations.Comparators.NOT_START_WITH;
import static org.uaithne.annotations.Comparators.NOT_START_WITH_INSENSITIVE;
import static org.uaithne.annotations.Comparators.SMALLER;
import static org.uaithne.annotations.Comparators.SMALL_AS;
import static org.uaithne.annotations.Comparators.START_WITH;
import static org.uaithne.annotations.Comparators.START_WITH_INSENSITIVE;
import org.uaithne.annotations.sql.CustomSqlQuery;
import org.uaithne.generator.commons.FieldInfo;


public abstract class BasicSqlQueryGenerator extends SqlQueryGenerator {
    
    //<editor-fold defaultstate="collapsed" desc="Where">
    @Override
    public void appendStartWhere(StringBuilder result) {
        result.append("where");
    }

    @Override
    public void appendEndWhere(StringBuilder result, String separator) {
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Set">
    @Override
    public void appendStartSet(StringBuilder result) {
        result.append("set");
    }

    @Override
    public void appendEndSet(StringBuilder result, String separator) {
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="If (not) null">
    @Override
    public void appendConditionStartIfNull(StringBuilder result, FieldInfo field, String separator) {
        result.append(separator);
        result.append("(");
        result.append(getParameterValue(field));
        result.append(" is not null or ");
    }

    @Override
    public void appendConditionStartIfNotNull(StringBuilder result, FieldInfo field, String separator) {
        result.append(separator);
        result.append("(");
        result.append(getParameterValue(field));
        result.append(" is null or ");
    }

    @Override
    public void appendConditionEndIf(StringBuilder result) {
        result.append(")");
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Comparators">
    @Override
    public String translateComparator(Comparators comparator) {
        if (comparator == null) {
            return null;
        }
        switch (comparator) {
            case EQUAL:      return "[[column]] = [[value]]";
            case NOT_EQUAL:  return "[[column]] <> [[value]]";
            case EQUAL_INSENSITIVE:      return "lower([[column]]) = lower([[value]])";
            case NOT_EQUAL_INSENSITIVE:  return "lower([[column]]) <> lower([[value]])";
            case EQUAL_NULLABLE:         return "(([[value]] is null and [[column]] is null) or ([[column]] = [[value]]))";
            case EQUAL_NOT_NULLABLE:     return "(([[value]] is null and [[column]] is not null) or ([[column]] = [[value]]))";
            case NOT_EQUAL_NULLABLE:     return "(([[value]] is not null and [[column]] <> [[value]]) or ([[value]] is null and [[column]] is null))";
            case NOT_EQUAL_NOT_NULLABLE: return "(([[value]] is not null and [[column]] <> [[value]]) or ([[value]] is null and [[column]] is not null))";
            case SMALLER:    return "[[column]] < [[value]]";
            case LARGER:     return "[[column]] > [[value]]";
            case SMALL_AS:   return "[[column]] <= [[value]]";
            case LARGER_AS:  return "[[column]] >= [[value]]";
            case IN:         return "[[column]] in [[value]]"; //TODO
            case NOT_IN:     return "[[column]] not in [[value]]"; //TODO
            case LIKE:       return "[[column]] like [[value]]";
            case NOT_LIKE:   return "[[column]] not like [[value]]";
            case LIKE_INSENSITIVE:       return "lower([[column]]) like lower([[value]])";
            case NOT_LIKE_INSENSITIVE:   return "lower([[column]]) not like lower([[value]])";
            case START_WITH:     return "[[column]] like ([[value]] || '%')";
            case NOT_START_WITH: return "[[column]] not like ([[value]] || '%')";
            case END_WITH:       return "[[column]] like ('%' || [[value]]";
            case NOT_END_WITH:   return "[[column]] not like ('%' || [[value]]";
            case START_WITH_INSENSITIVE:     return "lower([[column]]) like (lower([[value]]) || '%')";
            case NOT_START_WITH_INSENSITIVE: return "lower([[column]]) not like (lower([[value]]) || '%')";
            case END_WITH_INSENSITIVE:       return "lower([[column]]) like ('%' || lower([[value]])";
            case NOT_END_WITH_INSENSITIVE:   return "lower([[column]]) not like ('%' || lower([[value]])";
            case CONTAINS:       return "[[column]] like ('%' || [[value]] || '%')";
            case NOT_CONTAINS:   return "[[column]] not like ('%' || [[value]] || '%')";
            case CONTAINS_INSENSITIVE:       return "lower([[column]]) like ('%' || lower([[value]]) || '%')";
            case NOT_CONTAINS_INSENSITIVE:   return "lower([[column]]) not like ('%' || lower([[value]]) || '%')";
            default:
                throw new IllegalArgumentException("Unimplemented comparator " + comparator);
        }
    }

    @Override
    public String getConditionElementValue(String rule, FieldInfo field, CustomSqlQuery customQuery) {
        if (rule == null) {
            return null;
        } else if ("column".equals(rule) || "COLUMN".equals(rule)) {
            return getColumnNameForWhere(field, customQuery);
        } else if ("name".equals(rule) || "NAME".equals(rule)) {
            return field.getName();
        } else if ("value".equals(rule) || "VALUE".equals(rule)) {
            return getParameterValue(field);
        } else if ("valueNullable".equals(rule) || "VALUE_NULLABLE".equals(rule)) {
            if (field.isExcludedFromObject()) {
                getProcessingEnv().getMessager().printMessage(Diagnostic.Kind.ERROR, "The field '" + field.getName() + "' is not present in the object, you cannot access to the inexistent property value using valueNullable", field.getElement());
                return "FIELD_NOT_PRESENT_IN_OBJECT";
            }
            return getParameterValue(field, true);
        } else if ("valueWhenNull".equals(rule) || "VALUE_WHEN_NULL".equals(rule)) {
            String result = field.getValueWhenNull();
            if (result == null) {
                getProcessingEnv().getMessager().printMessage(Diagnostic.Kind.ERROR, "The field '" + field.getName() + "' has no defined value when null, use @ValueWhenNull annotation for define one", field.getElement());
                return "UNKOWN_DEFAULT_VALUE";
            }
            return result;
        } else if ("unforcedValue".equals(rule) || "UNFORCED_VALUE".equals(rule)) {
            if (field.isExcludedFromObject()) {
                getProcessingEnv().getMessager().printMessage(Diagnostic.Kind.ERROR, "The field '" + field.getName() + "' is not present in the object, you cannot access to the inexistent property value using unforcedValue", field.getElement());
                return "FORCED_VALUE_FIELD_NOT_PRESENT_IN_OBJECT";
            }
            return getParameterValue(field, false, true);
        } else if ("unforcedNullbaleValue".equals(rule) || "UNFORCED_NULLABLE_VALUE".equals(rule)) {
            if (field.isExcludedFromObject()) {
                getProcessingEnv().getMessager().printMessage(Diagnostic.Kind.ERROR, "The field '" + field.getName() + "' is not present in the object, you cannot access to the inexistent property value using unforcedNullableValue", field.getElement());
                return "FORCED_NULLABLE_VALUE_FIELD_NOT_PRESENT_IN_OBJECT";
            }
            return getParameterValue(field, true, true);
        } else if ("forcedValue".equals(rule) || "FORCED_VALUE".equals(rule)) {
            String result = field.getForcedValue();
            if (result == null) {
                getProcessingEnv().getMessager().printMessage(Diagnostic.Kind.ERROR, "The field '" + field.getName() + "' has no forced value, use @ForceValue annotation for define one", field.getElement());
                return "UNKOWN_FORCED_VALUE";
            }
            return result;
        } else {
            return null;
        }
    }
    //</editor-fold>

}
