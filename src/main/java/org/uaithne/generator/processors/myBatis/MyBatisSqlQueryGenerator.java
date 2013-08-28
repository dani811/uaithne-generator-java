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
package org.uaithne.generator.processors.myBatis;

import javax.lang.model.type.MirroredTypeException;
import javax.tools.Diagnostic;
import org.uaithne.annotations.Comparator;
import static org.uaithne.annotations.Comparator.CONTAINS;
import static org.uaithne.annotations.Comparator.CONTAINS_INSENSITIVE;
import static org.uaithne.annotations.Comparator.END_WITH;
import static org.uaithne.annotations.Comparator.END_WITH_INSENSITIVE;
import static org.uaithne.annotations.Comparator.EQUAL;
import static org.uaithne.annotations.Comparator.EQUAL_NULLABLE;
import static org.uaithne.annotations.Comparator.IN;
import static org.uaithne.annotations.Comparator.LARGER;
import static org.uaithne.annotations.Comparator.LARGER_AS;
import static org.uaithne.annotations.Comparator.LIKE;
import static org.uaithne.annotations.Comparator.LIKE_INSENSITIVE;
import static org.uaithne.annotations.Comparator.NOT_CONTAINS;
import static org.uaithne.annotations.Comparator.NOT_CONTAINS_INSENSITIVE;
import static org.uaithne.annotations.Comparator.NOT_END_WITH;
import static org.uaithne.annotations.Comparator.NOT_END_WITH_INSENSITIVE;
import static org.uaithne.annotations.Comparator.NOT_EQUAL;
import static org.uaithne.annotations.Comparator.NOT_EQUAL_NULLABLE;
import static org.uaithne.annotations.Comparator.NOT_IN;
import static org.uaithne.annotations.Comparator.NOT_LIKE;
import static org.uaithne.annotations.Comparator.NOT_LIKE_INSENSITIVE;
import static org.uaithne.annotations.Comparator.NOT_START_WITH;
import static org.uaithne.annotations.Comparator.NOT_START_WITH_INSENSITIVE;
import static org.uaithne.annotations.Comparator.SMALLER;
import static org.uaithne.annotations.Comparator.SMALL_AS;
import static org.uaithne.annotations.Comparator.START_WITH;
import static org.uaithne.annotations.Comparator.START_WITH_INSENSITIVE;
import org.uaithne.annotations.myBatis.MyBatisTypeHandler;
import org.uaithne.annotations.sql.CustomSqlQuery;
import org.uaithne.annotations.sql.JdbcType;
import org.uaithne.annotations.sql.UseJdbcType;
import org.uaithne.generator.commons.DataTypeInfo;
import org.uaithne.generator.commons.FieldInfo;
import org.uaithne.generator.commons.NamesGenerator;
import org.uaithne.generator.processors.sql.AbstractSqlQueryGenerator;

public abstract class MyBatisSqlQueryGenerator extends AbstractSqlQueryGenerator {
    
    //<editor-fold defaultstate="collapsed" desc="Order by">
    @Override
    public void appendOrderBy(StringBuilder result, FieldInfo orderBy, CustomSqlQuery customQuery) {
        if (customQuery != null) {
            if (hasQueryValue(customQuery.orderBy())) {
                result.append("\norder by");
                appendToQueryln(result, customQuery.beforeOrderByExpression(), "    ");
                appendToQueryln(result, customQuery.orderBy(), "    ");
                appendToQueryln(result, customQuery.afterOrderByExpression(), "    ");
            } else if (hasQueryValue(customQuery.beforeOrderByExpression()) || hasQueryValue(customQuery.afterOrderByExpression())) {
                result.append("\norder by");
                appendToQueryln(result, customQuery.beforeOrderByExpression(), "    ");
                if (orderBy != null) {
                    if (orderBy.isOptional()) {
                        result.append("\n    <if test='");
                        result.append(orderBy.getName());
                        result.append(" != null'> ${");
                        result.append(orderBy.getName());
                        result.append("} </if>");
                    } else {
                        result.append("\n    ${");
                        result.append(orderBy.getName());
                        result.append("}");
                    }
                }
                appendToQueryln(result, customQuery.afterOrderByExpression(), "    ");
            } else if (orderBy != null) {
                if (orderBy.isOptional()) {
                    result.append("\n<if test='");
                    result.append(orderBy.getName());
                    result.append(" != null'> order by ${");
                    result.append(orderBy.getName());
                    result.append("} </if>");
                } else {
                    result.append("\norder by ${");
                    result.append(orderBy.getName());
                    result.append("}");
                }
            }
        } else if (orderBy != null) {
            if (orderBy.isOptional()) {
                result.append("\n<if test='");
                result.append(orderBy.getName());
                result.append(" != null'> order by ${");
                result.append(orderBy.getName());
                result.append("} </if>");
            } else {
                result.append("\norder by ${");
                result.append(orderBy.getName());
                result.append("}");
            }
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Parameter">
    @Override
    public String getParameterValue(FieldInfo field) {
        return "#{" + field.getName() + getJdbcType(field) + getTypeHandler(field) + "}";
    }

    public String getJdbcType(FieldInfo field) {
        UseJdbcType ujt = field.getAnnotation(UseJdbcType.class);
        JdbcType jdbcType;
        if (ujt != null) {
            jdbcType = ujt.value();
        } else {
            jdbcType = null;
        }
        if (jdbcType != null) {
            return ",jdbcType=" + jdbcType.name();
        }

        String name = field.getDataType().getSimpleName();
        if ("String".equals(name)) {
            return ",jdbcType=VARCHAR";
        } else if ("Date".equals(name)) {
            return ",jdbcType=TIMESTAMP";
        } else if ("Time".equals(name)) {
            return ",jdbcType=TIME";
        } else if ("Timestamp".equals(name)) {
            return ",jdbcType=TIMESTAMP";
        } else if ("List<String>".equals(name)) {
            return ",jdbcType=VARCHAR";
        } else if ("ArrayList<String>".equals(name)) {
            return ",jdbcType=VARCHAR";
        } else if ("List<Date>".equals(name)) {
            return ",jdbcType=TIMESTAMP";
        } else if ("ArrayList<Date>".equals(name)) {
            return ",jdbcType=TIMESTAMP";
        } else if ("List<Time>".equals(name)) {
            return ",jdbcType=TIME";
        } else if ("ArrayList<Time>".equals(name)) {
            return ",jdbcType=TIME";
        } else if ("List<Timestamp>".equals(name)) {
            return ",jdbcType=TIMESTAMP";
        } else if ("ArrayList<Timestamp>".equals(name)) {
            return ",jdbcType=TIMESTAMP";
        } else {
            return ",jdbcType=NUMERIC";
        }
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
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Unable to find the type handler", field.getElement());
            return "";
        }

        return ",typeHandler=" + typeHandler.getQualifiedNameWithoutGenerics();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Where">
    @Override
    public void appendStartWhere(StringBuilder result) {
        result.append("<where>");
    }

    @Override
    public void appendEndWhere(StringBuilder result) {
        result.append("</where>");
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Set">
    @Override
    public void appendStartSet(StringBuilder result) {
        result.append("<set>");
    }

    @Override
    public void appendEndSet(StringBuilder result) {
        result.append("</set>");
    }
    
    @Override
    public void appendSetValueIfNotNull(StringBuilder result, FieldInfo field) {
        appendConditionStartIfNotNull(result, field);
        result.append(getColumnName(field));
        result.append(" = ");
        result.append(getParameterValue(field));
        result.append(",");
        appendConditionEndIf(result);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="If (not) null">
    @Override
    public void appendConditionStartIfNull(StringBuilder result, FieldInfo field) {
        result.append("<if test='");
        result.append(field.getName());
        result.append(" == null'>");
    }

    @Override
    public void appendConditionStartIfNotNull(StringBuilder result, FieldInfo field) {
        result.append("<if test='");
        result.append(field.getName());
        result.append(" != null'>");
    }

    @Override
    public void appendConditionEndIf(StringBuilder result) {
        result.append("</if>");
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Comparators">
    @Override
    public String translateComparator(Comparator comparator) {
        if (comparator == null) {
            return null;
        }
        switch (comparator) {
            case EQUAL:      return "[[column]] = [[value]]";
            case NOT_EQUAL:  return "[[column]] &lt;&gt; [[value]]";
            case EQUAL_NULLABLE:         return "<if test='[[name]] != null'> [[column]] = [[value]] </if> <if test='[[name]] == null'> [[column]] is null </if>";
            case NOT_EQUAL_NULLABLE:     return "<if test='[[name]] != null'> [[column]] &lt;&gt; [[value]] </if> <if test='[[name]] == null'> [[column]] is not null </if>";
            case SMALLER:    return "[[column]] &lt; [[value]]";
            case LARGER:     return "[[column]] &gt; [[value]]";
            case SMALL_AS:   return "[[column]] &lt;= [[value]]";
            case LARGER_AS:  return "[[column]] &gt;= [[value]]";
            case IN:         return "[[column]] in <foreach close=')' collection='[[name]]' item='_item_[[name]]' open='(' separator=','> #{_item_[[name]][[jdbcType]][[typeHandler]]} </foreach>";
            case NOT_IN:     return "[[column]] not in <foreach close=')' collection='[[name]]' item='_item_[[name]]' open='(' separator=','> #{_item_[[name]][[jdbcType]][[typeHandler]]} </foreach>";
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
        } else if ("jdbcType".equals(rule) || "JDBC_TYPE".equals(rule)) {
            return getJdbcType(field);
        } else if ("typeHandler".equals(rule) || "TYPE_HANDLER".equals(rule)) {
            return getTypeHandler(field);
        } else {
            return null;
        }
    }
    //</editor-fold>

}
