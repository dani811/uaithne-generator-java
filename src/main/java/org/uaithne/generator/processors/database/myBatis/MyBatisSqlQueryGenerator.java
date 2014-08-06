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
package org.uaithne.generator.processors.database.myBatis;

import java.util.ArrayList;
import javax.lang.model.type.MirroredTypeException;
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
import org.uaithne.annotations.myBatis.MyBatisTypeHandler;
import org.uaithne.annotations.sql.CustomSqlQuery;
import org.uaithne.annotations.sql.JdbcTypes;
import org.uaithne.generator.commons.DataTypeInfo;
import org.uaithne.generator.commons.EntityInfo;
import org.uaithne.generator.commons.FieldInfo;
import org.uaithne.generator.commons.NamesGenerator;
import org.uaithne.generator.commons.OperationInfo;
import org.uaithne.generator.processors.database.sql.SqlQueryGenerator;

public abstract class MyBatisSqlQueryGenerator extends SqlQueryGenerator {
    
    //<editor-fold defaultstate="collapsed" desc="Order by">
    @Override
    public void appendOrderBy(StringBuilder result, ArrayList<FieldInfo> orderBys, CustomSqlQuery customQuery) {
        if (customQuery != null) {
            if (hasQueryValue(customQuery.orderBy())) {
                result.append("\norder by");
                appendToQueryln(result, customQuery.beforeOrderByExpression(), "    ");
                appendToQueryln(result, customQuery.orderBy(), "    ");
                appendToQueryln(result, customQuery.afterOrderByExpression(), "    ");
            } else if (hasQueryValue(customQuery.beforeOrderByExpression()) || hasQueryValue(customQuery.afterOrderByExpression())) {
                result.append("\norder by");
                appendToQueryln(result, customQuery.beforeOrderByExpression(), "    ");
                appendOrderByContent(result, orderBys, "\n    ", "\n    ", ", ", "", "");
                appendToQueryln(result, customQuery.afterOrderByExpression(), "    ");
            } else {
                appendOrderByContent(result, orderBys, "\n", "\n    ", ", ", "order by ", " ");
            }
        } else {
            appendOrderByContent(result, orderBys, "\n", "\n    ", ", ", "order by ", " ");
        }
    }
    
    public void appendOrderByContent(StringBuilder result, ArrayList<FieldInfo> orderBys, String start, String separator, String commaSeparator, String startOrderBy, String endOrderBy) {
        if (orderBys.isEmpty()) {
            return;
        }
        
        boolean optional = false;
        boolean requireComma = false;
        
        result.append(start);
        for (FieldInfo orderBy : orderBys) {
            optional = optional || orderBy.isOptional();
        }
        boolean firstOptional = orderBys.get(0).isOptional();
        
        if (!optional || !firstOptional || orderBys.size() <= 1) {
            boolean hasOnlyOne = orderBys.size() == 1;
            if (!hasOnlyOne || (!firstOptional && hasOnlyOne)) {
                result.append(startOrderBy);
            }
            for (FieldInfo orderBy : orderBys) {
                if (!hasOnlyOne) {
                    result.append(separator);
                }
                if (orderBy.isOptional()) {
                    result.append("{[if test='");
                    result.append(orderBy.getName());
                    result.append(" != null']}");
                }
                if (hasOnlyOne && firstOptional) {
                    result.append(startOrderBy);
                }
                if (requireComma) {
                    result.append(commaSeparator);
                }
                result.append("${");
                result.append(orderBy.getName());
                result.append("}");
                if (hasOnlyOne) {
                    result.append(endOrderBy);
                }
                if (orderBy.isOptional()) {
                    result.append("{[/if]}");
                }
                requireComma = true;
            }
            if (!hasOnlyOne) {
                result.append(endOrderBy);
            }
            return;
        }
        
        result.append("{[trim prefix='");
        result.append(startOrderBy);
        result.append("' suffix='");
        result.append(endOrderBy);
        result.append("' prefixOverrides='");
        result.append(commaSeparator);
        result.append("']}");
        
        for (FieldInfo orderBy : orderBys) {
            if (orderBy.isOptional()) {
                result.append(separator);
                result.append("{[if test='");
                result.append(orderBy.getName());
                result.append(" != null']}");
                if (requireComma) {
                    result.append(commaSeparator);
                }
                result.append("${");
                result.append(orderBy.getName());
                result.append("} {[/if]}");
            } else {
                if (requireComma) {
                    result.append(commaSeparator);
                }
                result.append(separator);
                result.append("${");
                result.append(orderBy.getName());
                result.append("}");
            }
            requireComma=true;
        }
        result.append(start);
        result.append("{[/trim]}");
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Parameter">
    @Override
    public String getParameterValue(FieldInfo field, boolean ignoreValueWhenNull) {
        if (ignoreValueWhenNull || field.getValueWhenNull() == null) {
            return "#{" + field.getName() + getJdbcTypeAttribute(field) + getTypeHandler(field) + "}";
        } else if (field.isExcludedFromObject()) {
            return field.getValueWhenNull();
        } else {
            return "{[if test='" + field.getName() + " != null']} " + 
                "#{" + field.getName() + getJdbcTypeAttribute(field) + getTypeHandler(field) + "} " +
                "{[/if]} " + 
                "{[if test='" + field.getName() + " == null']} " + 
                field.getValueWhenNull()+ " " +
                "{[/if]}";
        }
    }
    
    public String getJdbcTypeAttribute(FieldInfo field) {
        JdbcTypes jdbcType = getJdbcType(field);
        if (jdbcType == null) {
            getProcessingEnv().getMessager().printMessage(Diagnostic.Kind.ERROR, "Uknown jdbc data type. Use @JdbcType annotation for specify the jdbc data type.", field.getElement());
            return "";
        }
        return ",jdbcType=" + jdbcType.name();
    }

    public String getTypeHandler(FieldInfo field) {
        MyBatisTypeHandler th = field.getAnnotation(MyBatisTypeHandler.class);

        if (th == null) {
            return "";
        }

        DataTypeInfo typeHandler;
        try {
            typeHandler = NamesGenerator.createResultDataType(th.value());
        } catch (MirroredTypeException ex) {
            // See: http://blog.retep.org/2009/02/13/getting-class-values-from-annotations-in-an-annotationprocessor/
            typeHandler = NamesGenerator.createDataTypeFor(ex.getTypeMirror());
        }
        if (typeHandler == null) {
            getProcessingEnv().getMessager().printMessage(Diagnostic.Kind.ERROR, "Unable to find the type handler", field.getElement());
            return "";
        }

        return ",typeHandler=" + typeHandler.getQualifiedNameWithoutGenerics();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Where">
    @Override
    public void appendStartWhere(StringBuilder result) {
        result.append("{[where]}");
    }

    @Override
    public void appendEndWhere(StringBuilder result, String separator) {
        result.append(separator);
        result.append("{[/where]}");
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Set">
    @Override
    public void appendStartSet(StringBuilder result) {
        result.append("{[set]}");
    }

    @Override
    public void appendEndSet(StringBuilder result, String separator) {
        result.append(separator);
        result.append("{[/set]}");
    }
    
    @Override
    public void appendStartSetValueIfNotNull(StringBuilder result, FieldInfo field) {
        appendConditionStartIfNotNull(result, field, "");
        result.append(getColumnName(field));
        result.append(" = ");
        result.append(getParameterValue(field));
    }
    
    @Override
    public void appendEndSetValueIfNotNull(StringBuilder result, boolean requireComma) {
        if (requireComma) {
            result.append(",");
        }
        appendConditionEndIf(result);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="If (not) null">
    @Override
    public void appendConditionStartIfNull(StringBuilder result, FieldInfo field, String separator) {
        result.append("{[if test='");
        result.append(field.getName());
        result.append(" == null']}");
        result.append(separator);
    }

    @Override
    public void appendConditionStartIfNotNull(StringBuilder result, FieldInfo field, String separator) {
        result.append("{[if test='");
        result.append(field.getName());
        result.append(" != null']}");
        result.append(separator);
    }

    @Override
    public void appendConditionEndIf(StringBuilder result) {
        result.append("{[/if]}");
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Insert selective">
    @Override
    public void appendStartInsertColumnIfNotNull(StringBuilder result, FieldInfo field) {
        appendConditionStartIfNotNull(result, field, "");
        result.append(getColumnName(field));
    }

    @Override
    public void appendEndInsertColumnIfNotNull(StringBuilder result, boolean requireComma) {
        if (requireComma) {
            result.append(",");
        }
        appendConditionEndIf(result);
    }

    @Override
    public void appendStartInsertValueIfNotNull(StringBuilder result, OperationInfo operation, EntityInfo entity, FieldInfo field) {
        appendConditionStartIfNotNull(result, field, "");
        result.append(getParameterValue(field));
    }

    @Override
    public void appendEndInsertValueIfNotNull(StringBuilder result, boolean requireComma) {
        if (requireComma) {
            result.append(",");
        }
        appendConditionEndIf(result);
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
            case EQUAL_NULLABLE:         return "{[if test='[[name]] != null']} [[column]] = [[value]] {[/if]} {[if test='[[name]] == null']} [[column]] is null {[/if]}";
            case EQUAL_NOT_NULLABLE:     return "{[if test='[[name]] != null']} [[column]] = [[value]] {[/if]} {[if test='[[name]] == null']} [[column]] is not null {[/if]}";
            case NOT_EQUAL_NULLABLE:     return "{[if test='[[name]] != null']} [[column]] <> [[value]] {[/if]} {[if test='[[name]] == null']} [[column]] is null {[/if]}";
            case NOT_EQUAL_NOT_NULLABLE: return "{[if test='[[name]] != null']} [[column]] <> [[value]] {[/if]} {[if test='[[name]] == null']} [[column]] is not null {[/if]}";
            case SMALLER:    return "[[column]] < [[value]]";
            case LARGER:     return "[[column]] > [[value]]";
            case SMALL_AS:   return "[[column]] <= [[value]]";
            case LARGER_AS:  return "[[column]] >= [[value]]";
            case IN:         return "[[column]] in {[foreach collection='[[name]]' open='(' separator=',' close=')' item='_item_[[name]]']} #{_item_[[name]][[jdbcType]][[typeHandler]]} {[/foreach]}";
            case NOT_IN:     return "[[column]] not in {[foreach collection='[[name]]' open='(' separator=',' close=')' item='_item_[[name]]']} #{_item_[[name]][[jdbcType]][[typeHandler]]} {[/foreach]}";
            case LIKE:       return "[[column]] like [[value]]";
            case NOT_LIKE:   return "[[column]] not like [[value]]";
            case LIKE_INSENSITIVE:       return "lower([[column]]) like lower([[value]])";
            case NOT_LIKE_INSENSITIVE:   return "lower([[column]]) not like lower([[value]])";
            case START_WITH:     return "[[column]] like ([[value]] || '%')";
            case NOT_START_WITH: return "[[column]] not like ([[value]] || '%')";
            case END_WITH:       return "[[column]] like ('%' || [[value]])";
            case NOT_END_WITH:   return "[[column]] not like ('%' || [[value]])";
            case START_WITH_INSENSITIVE:     return "lower([[column]]) like (lower([[value]]) || '%')";
            case NOT_START_WITH_INSENSITIVE: return "lower([[column]]) not like (lower([[value]]) || '%')";
            case END_WITH_INSENSITIVE:       return "lower([[column]]) like ('%' || lower([[value]]))";
            case NOT_END_WITH_INSENSITIVE:   return "lower([[column]]) not like ('%' || lower([[value]]))";
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
        } else if ("jdbcType".equals(rule) || "JDBC_TYPE".equals(rule)) {
            return getJdbcTypeAttribute(field);
        } else if ("typeHandler".equals(rule) || "TYPE_HANDLER".equals(rule)) {
            return getTypeHandler(field);
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
        } else {
            return null;
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="finalizeQuery">
    @Override
    public String finalizeQuery(String query, OperationInfo operation, CustomSqlQuery customQuery) {
        String result = super.finalizeQuery(query, operation, customQuery);
        result = result.replace("<", "&lt;");
        result = result.replace(">", "&gt;");
        result = result.replace("{[", "<");
        result = result.replace("]}", ">");
        return result;
    }
    //</editor-fold>
}
