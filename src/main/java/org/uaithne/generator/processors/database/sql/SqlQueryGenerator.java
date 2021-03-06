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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;
import org.uaithne.annotations.Comparators;
import org.uaithne.annotations.EntityQueries;
import org.uaithne.annotations.IdQueries;
import org.uaithne.annotations.IdSequenceName;
import org.uaithne.annotations.MappedName;
import org.uaithne.annotations.PageQueries;
import org.uaithne.annotations.Query;
import org.uaithne.annotations.sql.CustomSqlQuery;
import org.uaithne.generator.commons.*;

public abstract class SqlQueryGenerator extends SqlGenerator {
    
    //<editor-fold defaultstate="collapsed" desc="Complete query">
    public String[] completeQuery(String[] query, OperationInfo operation, boolean count, boolean selectPage) {
        return completeQuery(query, operation, count, selectPage, true);
    }
    
    public String[] completeQuery(String[] query, OperationInfo operation, boolean count, boolean selectPage, boolean ignoreCustomQueryWhenCount) {
        CustomSqlQuery customQuery = operation.getAnnotation(CustomSqlQuery.class);
        String completed;
        if (query == null) {
            completed = completeQueryWithoutEnvolve(null, operation, count, selectPage, customQuery, ignoreCustomQueryWhenCount);
        } else {
            completed = joinln(query);
            completed = completeQueryWithoutEnvolve(completed, operation, count, selectPage, customQuery, ignoreCustomQueryWhenCount);
        }
        completed = finalizeQuery(completed, operation, customQuery);

        String[] result;
        if (completed == null) {
            result = null;
        } else {
            result = completed.split("\n");
        }
        if (operation.isLimitToOneResult()) {
            return envolveInSelectOneRow(result);
        } else {
            return result;
        }

    }
    
    public String completeQueryWithoutEnvolve(String query, OperationInfo operation, boolean count, boolean selectPage, CustomSqlQuery customQuery) {
        return completeQueryWithoutEnvolve(query, operation, count, selectPage, customQuery, true);
    }

    public String completeQueryWithoutEnvolve(String query, OperationInfo operation, boolean count, boolean selectPage, CustomSqlQuery customQuery, boolean ignoreCustomQueryWhenCount) {
        boolean addSelect = false;
        boolean addFrom = false;
        boolean addWhere = false;
        boolean addGroupBy = false;
        boolean addOrderBy = false;
        boolean ignoreQuery = false;
        boolean prepend = false;
        StringBuilder appender = new StringBuilder();
        EntityInfo entity = operation.getEntity();
        if (entity != null) {
            entity = entity.getCombined();
        }

        if (query == null && customQuery == null) {
            if (entity == null) {
                getProcessingEnv().getMessager().printMessage(Diagnostic.Kind.ERROR,
                        "Unable to automatically generate the query for this operation, enter the query manually using Query annotation or specify the related entity",
                        operation.getElement());
                return null;
            }
            addSelect = addFrom = addWhere = addGroupBy = addOrderBy = ignoreQuery = true;
        } else if (query != null && !query.isEmpty()) {
            query = query.trim();
            String queryLowerCase = query.toLowerCase();
            if (query.isEmpty()) {
                if (entity == null) {
                    getProcessingEnv().getMessager().printMessage(Diagnostic.Kind.ERROR,
                            "Unable to automatically generate the query for this operation, enter the query manually using Query annotation or specify the related entity",
                            operation.getElement());
                    return null;
                }
                addSelect = addFrom = addWhere = addGroupBy = addOrderBy = ignoreQuery = true;
            } else if (queryLowerCase.startsWith("from")) {
                if (entity == null) {
                    getProcessingEnv().getMessager().printMessage(Diagnostic.Kind.ERROR,
                            "Unable to automatically complete the query for this operation, enter the full query manually using Query annotation or specify the related entity",
                            operation.getElement());
                } else {
                    addSelect = true;
                }
            } else if (queryLowerCase.startsWith("where")) {
                if (entity == null) {
                    getProcessingEnv().getMessager().printMessage(Diagnostic.Kind.ERROR,
                            "Unable to automatically complete the query for this operation, enter the full query manually using Query annotation or specify the related entity",
                            operation.getElement());
                } else {
                    addSelect = addFrom = true;
                }
            } else if (queryLowerCase.startsWith("group")) {
                if (entity == null) {
                    getProcessingEnv().getMessager().printMessage(Diagnostic.Kind.ERROR,
                            "Unable to automatically generate the query for this operation, enter the query manually using Query annotation or specify the related entity",
                            operation.getElement());
                } else {
                    addSelect = addFrom = addWhere = true;
                }
            } else if (queryLowerCase.startsWith("order")) {
                if (entity == null) {
                    getProcessingEnv().getMessager().printMessage(Diagnostic.Kind.ERROR,
                            "Unable to automatically generate the query for this operation, enter the query manually using Query annotation or specify the related entity",
                            operation.getElement());
                } else {
                    addSelect = addFrom = addWhere = addGroupBy = true;
                }
            } else if (queryLowerCase.endsWith("select...")) {
                if (entity == null) {
                    getProcessingEnv().getMessager().printMessage(Diagnostic.Kind.ERROR,
                            "Unable to automatically complete the query for this operation, enter the full query manually using Query annotation or specify the related entity",
                            operation.getElement());
                } else {
                    addSelect = addFrom = addWhere = addGroupBy = addOrderBy = prepend = true;
                    query = query.substring(0, query.length() - "select...".length());
                }
            } else if (queryLowerCase.endsWith("from...")) {
                if (entity == null) {
                    getProcessingEnv().getMessager().printMessage(Diagnostic.Kind.ERROR,
                            "Unable to automatically complete the query for this operation, enter the full query manually using Query annotation or specify the related entity",
                            operation.getElement());
                } else {
                    addFrom = addWhere = addGroupBy = addOrderBy = prepend = true;
                    query = query.substring(0, query.length() - "from...".length());
                }
            } else if (queryLowerCase.endsWith("where...")) {
                if (entity == null) {
                    getProcessingEnv().getMessager().printMessage(Diagnostic.Kind.ERROR,
                            "Unable to automatically complete the query for this operation, enter the full query manually using Query annotation or specify the related entity",
                            operation.getElement());
                } else {
                    addWhere = addGroupBy = addOrderBy = prepend = true;
                    query = query.substring(0, query.length() - "where...".length());
                }
            } else if (queryLowerCase.endsWith("group...")) {
                if (entity == null) {
                    getProcessingEnv().getMessager().printMessage(Diagnostic.Kind.ERROR,
                            "Unable to automatically complete the query for this operation, enter the full query manually using Query annotation or specify the related entity",
                            operation.getElement());
                } else {
                    addGroupBy = addOrderBy = prepend = true;
                    query = query.substring(0, query.length() - "group...".length());
                }
            } else if (queryLowerCase.endsWith("order...")) {
                if (entity == null) {
                    getProcessingEnv().getMessager().printMessage(Diagnostic.Kind.ERROR,
                            "Unable to automatically complete the query for this operation, enter the full query manually using Query annotation or specify the related entity",
                            operation.getElement());
                } else {
                    addOrderBy = prepend = true;
                    query = query.substring(0, query.length() - "order...".length());
                }
            } else {
                return query;
            }
        }

        if (customQuery != null) {
            if (hasQueryValue(customQuery.query())) {
                appendToQueryln(appender, customQuery.query(), "");
                return appender.toString();
            } else if (query == null) {
                if (entity == null) {
                    if (!hasQueryValue(customQuery.select()) || !hasQueryValue(customQuery.from())) {
                        getProcessingEnv().getMessager().printMessage(Diagnostic.Kind.ERROR,
                                "Unable to automatically complete the query for this operation, enter the full query manually using Query annotation or specify the related entity",
                                operation.getElement());
                    } else if (operation.isOrdered()) {
                        getProcessingEnv().getMessager().printMessage(Diagnostic.Kind.ERROR,
                                "Unable to automatically complete the query for this operation, enter the full query manually using Query annotation or specify the related entity",
                                operation.getElement());
                    } else {
                        addSelect = addFrom = addWhere = addGroupBy = addOrderBy = ignoreQuery = true;
                    }
                } else {
                    addSelect = addFrom = addWhere = addGroupBy = addOrderBy = ignoreQuery = true;
                }
            }
        }
        
        ArrayList<FieldInfo> orderBys = new ArrayList<FieldInfo>(0);
        for (FieldInfo field : operation.getFields()) {
            if (field.isManually()) {
                continue;
            }
            if (field.isSetValueMark()) {
                continue;
            }
            if (field.isOrderBy()) {
                orderBys.add(field);
            }
        }

        if (prepend && !ignoreQuery) {
            appender.append(query);
        }

        if (addSelect) {
            appendSelect(appender, operation, entity, count, customQuery, ignoreCustomQueryWhenCount, orderBys);
        }
        if (operation.isLimitToOneResult() && (addSelect || addFrom)) {
            appendOrderByAfterSelectForSelectOneRow(appender, orderBys, customQuery);
        }
        if (selectPage && !count && (addSelect || addFrom)) {
            appendOrderByAfterSelectForSelectPage(appender, orderBys, customQuery);
        }
        if (addFrom) {
            appendFrom(appender, entity, customQuery);
        }
        if (addWhere) {
            appendWhere(appender, operation, customQuery, count, orderBys);
        }
        if (addGroupBy) {
            appendGroupBy(appender, operation, entity, customQuery);
        }
        if (!count) {
            if (addOrderBy) {
                if (selectPage) {
                    appendOrderByForSelectPage(appender, orderBys, customQuery);
                } else if (operation.isLimitToOneResult()) {
                    appendOrderByForSelectOneRow(appender, orderBys, customQuery);
                } else {
                    appendOrderBy(appender, orderBys, customQuery);
                }
            }
            if (operation.isLimitToOneResult()) {
                appendSelectOneRowAfterOrderBy(appender, orderBys, customQuery);
            }
            if (selectPage) {
                appendSelectPageAfterOrderBy(appender, orderBys, customQuery);
            }
        }

        String result;
        if (ignoreQuery || prepend) {
            result = appender.toString();
        } else {
            result = appender.toString();
            if (query != null && !query.isEmpty()) {
                result = result + "\n" + query;
            }
        }
        return result;
    }
        
    public void appendSelect(StringBuilder result, OperationInfo operation, EntityInfo entity, boolean count, CustomSqlQuery customQuery, ArrayList<FieldInfo> orderBys) {
        appendSelect(result, operation, entity, count, customQuery, true, orderBys);
    }
    
    public void appendSelect(StringBuilder result, OperationInfo operation, EntityInfo entity, boolean count, CustomSqlQuery customQuery, boolean ignoreCustomQueryWhenCount, ArrayList<FieldInfo> orderBys) {
        result.append("select");
        // When the result query is count and distinct at the same time is not 
        // possible do it in the same query, in consequence count is ignored, 
        // allowing to add the select count in the next steep
        count = count && !operation.isDistinct();
        if (operation.isDistinct()) {
            result.append(" distinct");
        }
        if (count && (ignoreCustomQueryWhenCount || customQuery == null)) {
            result.append("\n    count(*)");
        } else {
            if (operation.isLimitToOneResult()) {
                appendSelectOneRowBeforeSelect(result, orderBys, customQuery);
            }

            if (operation.getOperationKind() == OperationKind.SELECT_PAGE) {
                appendSelectPageBeforeSelect(result, orderBys, customQuery);
            }
            if (customQuery!= null) {
                appendToQueryln(result, customQuery.beforeSelectExpression(), "    ");

                if (hasQueryValue(customQuery.select())) {
                    appendToQueryln(result, customQuery.select(), "    ");
                } else {
                    if (count) {
                       result.append("\n    count(*)"); 
                    } else {
                        appendSelectFields(result, operation, entity, count, customQuery);
                    }
                }

                appendToQueryln(result, customQuery.afterSelectExpression(), "    ");
            } else {
                appendSelectFields(result, operation, entity, count, null);
            }
        }
    }

    public void appendSelectFields(StringBuilder result, OperationInfo operation, EntityInfo entity, boolean count, CustomSqlQuery customQuery) {
        HashSet<FieldInfo> excludeFields;
        if (customQuery != null) {
            excludeFields = retrieveFieldsForExclude(customQuery.excludeEntityFields(), entity, operation.getElement());
        } else {
            excludeFields = new HashSet<FieldInfo>(0);
        }
        boolean requireComma = false;
        for (FieldInfo field : entity.getFields()) {
            if (field.isExcludedFromObject()) {
                continue;
            }
            if (excludeFields.contains(field)) {
                continue;
            }
            if (field.isManually()) {
                continue;
            }
            if (requireComma) {
                result.append(",\n    ");
            } else {
                result.append("\n    ");
            }
            result.append(getColumnNameForSelect(field, customQuery));
            requireComma = true;
        }
    }

    public void appendFrom(StringBuilder result, EntityInfo entity, CustomSqlQuery customQuery) {
        result.append("\nfrom");
        if (customQuery != null) {
            appendToQueryln(result, customQuery.beforeFromExpression(), "    ");
            String[] from = customQuery.from();
            if (hasQueryValue(from)) {
                appendToQueryln(result, from, "    ");
            } else {
                appendToQueryln(result, getTableName(entity, customQuery), "    ");
            }
            appendToQueryln(result, customQuery.afterFromExpression(), "    ");
        } else {
            appendToQueryln(result, getTableName(entity, null), "    ");
        }
    }

    public void appendWhere(StringBuilder query, OperationInfo operation, CustomSqlQuery customQuery, boolean count, ArrayList<FieldInfo> orderBys) {
        ArrayList<FieldInfo> fields = operation.getFields();
        appendWhereWithFields(query, operation, customQuery, count, fields, orderBys);
    }
    
    public void appendWhereWithFields(StringBuilder query, OperationInfo operation, CustomSqlQuery customQuery, boolean count, ArrayList<FieldInfo> fields, ArrayList<FieldInfo> orderBys) {
        if (customQuery != null) {
            if (hasQueryValue(customQuery.where())) {
                query.append("\n");
                appendStartWhere(query);
                appendToQueryln(query, customQuery.beforeWhereExpression(), "    ");
                appendToQueryln(query, customQuery.where(), "    ");
                appendToQueryln(query, customQuery.afterWhereExpression(), "    ");
                appendSelectPageAfterWhere(query, true, orderBys, customQuery);
                appendEndWhere(query, "\n");
                return;
            }
        }

        boolean hasConditions = false;
        boolean hasOptionals = false;
        StringBuilder result = new StringBuilder();
        boolean requireAnd = false;
        for (FieldInfo field : fields) {
            if (field.isManually()) {
                continue;
            }
            if (field.isSetValueMark()) {
                continue;
            }
            if (field.isOrderBy()) {
                continue;
            }
            boolean optional = field.isQueryOptional();
            if (optional) {
                if (requireAnd) {
                    result.append("\n    ");
                } else {
                    result.append("    ");
                }
                String separator;
                if (requireAnd) {
                    separator = "and ";
                } else {
                    separator = "";
                }
                appendConditionStartIfNotNull(result, field, separator);
                hasOptionals = true;
            } else {
                if (requireAnd) {
                    result.append("\n    and ");
                } else {
                    result.append("    ");
                }
            }
            appendCondition(result, field, customQuery);
            hasConditions = true;
            if (optional) {
                appendConditionEndIf(result);
            }
            requireAnd = true;
        }
        if (operation.isLimitToOneResult()) {
            boolean hasAfterWhere = appendSelectOneRowAfterWhere(result, requireAnd, orderBys, customQuery);
            hasConditions = hasConditions || hasAfterWhere;
            requireAnd = requireAnd || hasAfterWhere;
        }
        if (operation.isUseLogicalDeletion() && operation.getEntity() != null) {
            List<FieldInfo> entityFields = operation.getEntity().getCombined().getFields();
            for (FieldInfo field : entityFields) {
                if (field.isManually()) {
                    continue;
                }
                if (field.isDeletionMark()) {
                    if (requireAnd) {
                        result.append("\n    and ");
                    } else {
                        result.append("    ");
                    }
                    appendNotDeleted(result, field, customQuery);
                    requireAnd = true;
                    hasConditions = true;
                }
            }
        }
        if (!count && operation.getOperationKind() == OperationKind.SELECT_PAGE) {
            hasConditions = hasConditions || appendSelectPageAfterWhere(result, requireAnd, orderBys, customQuery);
        }

        if (hasConditions) {
            if (customQuery != null && (hasQueryValue(customQuery.beforeWhereExpression()) || hasQueryValue(customQuery.afterWhereExpression())) ) {
                query.append("\n");
                appendStartWhere(query);
                appendToQueryln(query, customQuery.beforeWhereExpression(), "    ");
                query.append("\n");
                query.append(result);
                appendToQueryln(query, customQuery.afterWhereExpression(), "    ");
                appendEndWhere(query, "\n");
            } else {
                if (hasOptionals) {
                    query.append("\n");
                    appendStartWhere(query);
                    query.append("\n");
                    query.append(result);
                    appendEndWhere(query, "\n");
                } else {
                    query.append("\nwhere\n");
                    query.append(result);
                }
            }
        } else if (customQuery != null && (hasQueryValue(customQuery.beforeWhereExpression()) || hasQueryValue(customQuery.afterWhereExpression())) ) {
            query.append("\n");
            appendStartWhere(query);
            appendToQueryln(query, customQuery.beforeWhereExpression(), "    ");
            appendToQueryln(query, customQuery.where(), "    ");
            appendToQueryln(query, customQuery.afterWhereExpression(), "    ");
            appendEndWhere(query, "\n");
        }
    }

    public void appendCondition(StringBuilder result, FieldInfo field, CustomSqlQuery customQuery) {
        Comparators comparator = getComparator(field);

        String comparatorTemplate = translateComparator(comparator);
        String template = getConditionTemplate(field, comparatorTemplate);

        String condition = getConditionComparator(comparatorTemplate, template, field, customQuery);
        result.append(condition);
    }


    public void appendGroupBy(StringBuilder result, OperationInfo operation, EntityInfo entity, CustomSqlQuery customQuery) {
        if (customQuery != null) {
            if (hasQueryValue(customQuery.beforeGroupByExpression()) ||
                    hasQueryValue(customQuery.groupBy()) ||
                    hasQueryValue(customQuery.afterGroupByExpression())) {
                result.append("\ngroup by");
                appendToQueryln(result, customQuery.beforeGroupByExpression(), "    ");
                appendToQueryln(result, customQuery.groupBy(), "    ");
                appendToQueryln(result, customQuery.afterGroupByExpression(), "    ");
            }
        }
    }

    public abstract void appendOrderBy(StringBuilder result, ArrayList<FieldInfo> orderBys, CustomSqlQuery customQuery);
    public abstract void appendOrderByForSelectPage(StringBuilder result, ArrayList<FieldInfo> orderBys, CustomSqlQuery customQuery);
    public abstract void appendOrderByForSelectOneRow(StringBuilder result, ArrayList<FieldInfo> orderBys, CustomSqlQuery customQuery);
    public abstract void appendOrderByAfterSelectForSelectPage(StringBuilder result, ArrayList<FieldInfo> orderBys, CustomSqlQuery customQuery);
    public abstract void appendOrderByAfterSelectForSelectOneRow(StringBuilder result, ArrayList<FieldInfo> orderBys, CustomSqlQuery customQuery);
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Utils for generate the select query">
    public String getColumnNameForSelect(FieldInfo field, CustomSqlQuery customQuery) {
        String mappedName = field.getMappedNameOrName();
        String name = field.getName();
        String result;
        if (name.equals(mappedName)) {
            result = name;
        } else {
            result = mappedName + " as \"" + field.getName() + "\"";
        }
        if (customQuery != null) {
            String tableAlias = customQuery.tableAlias();
            if (tableAlias != null && !tableAlias.isEmpty()) {
                return tableAlias + "." + result;
            }
        }
        return result;
    }

    public String[] getTableName(EntityInfo entity, CustomSqlQuery customQuery) {
        MappedName mappedName = entity.getAnnotation(MappedName.class);
        String[] result;
        if (mappedName == null) {
            while (entity.getRelated() != null) {
                entity = entity.getRelated();
            }
            result = entity.getDataType().getSimpleNameWithoutGenerics().split("\n");
        } else {
            result = joinln(mappedName.value()).split("\n");
        }
        if (customQuery != null) {
            String tableAlias = customQuery.tableAlias();
            if (tableAlias != null && !tableAlias.isEmpty()) {
                for (int i = 0; i < result.length; i++) {
                    result[i] = result[i] + " " + tableAlias;
                }
            }
        }
        return result;
    }

    public String getColumnName(FieldInfo field) {
        return field.getMappedNameOrName();
    }

    public String getColumnNameForWhere(FieldInfo field, CustomSqlQuery customQuery) {
        if (customQuery != null) {
            String tableAlias = customQuery.tableAlias();
            if (tableAlias != null && !tableAlias.isEmpty()) {
                if (field.hasOwnMappedName()) {
                    return field.getMappedNameOrName();
                } else {
                    return tableAlias + "." + field.getMappedNameOrName();
                }
            }
        }
        return field.getMappedNameOrName();
    }
    
    public String getColumnNameForWhereFromEntity(FieldInfo field, CustomSqlQuery customQuery) {
        if (customQuery != null) {
            String tableAlias = customQuery.tableAlias();
            if (tableAlias != null && !tableAlias.isEmpty()) {
                return tableAlias + "." + field.getMappedNameOrName();
            }
        }
        return field.getMappedNameOrName();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="For generate queries">
    public abstract void appendStartWhere(StringBuilder result);

    public abstract void appendEndWhere(StringBuilder result, String separator);

    public abstract void appendStartSet(StringBuilder result);

    public abstract void appendEndSet(StringBuilder result, String separator);
    
    public abstract void appendStartSetValueIfNotNull(StringBuilder result, FieldInfo field);
    
    public abstract void appendEndSetValueIfNotNull(StringBuilder result, boolean requireComma);
    
    public abstract void appendParameterValueOrDefaultInDatabaseWhenNullForInsert(StringBuilder result, OperationInfo operation, EntityInfo entity, FieldInfo field);
    
    public abstract void appendConditionStartIfNull(StringBuilder result, FieldInfo field, String separator);

    public abstract void appendConditionStartIfNotNull(StringBuilder result, FieldInfo field, String separator);

    public abstract void appendConditionEndIf(StringBuilder result);

    public abstract String translateComparator(Comparators comparator);

    public abstract String getConditionElementValue(String rule, FieldInfo field, CustomSqlQuery customQuery);
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Sql Values">
    public abstract String currentSqlDate();

    public abstract String falseValue();

    public abstract String trueValue();

    public abstract String[] envolveInSelectPage(String[] query);

    /*
     * Expected:
     * result.append(page);
     * result.append(" ");
     */
    public abstract void appendSelectPageBeforeSelect(StringBuilder result, ArrayList<FieldInfo> orderBys, CustomSqlQuery customQuery);

    /*
     * if (requireAnd) {
     *     result.append("\n    and ");
     * } else {
     *     result.append("    ");
     * }
     * result.append(page);
     */
    public abstract boolean appendSelectPageAfterWhere(StringBuilder result, boolean requireAnd, ArrayList<FieldInfo> orderBys, CustomSqlQuery customQuery);
    
    /*
     * Expected:
     * appender.append("\n");
     * appender.append(page);
     */
    public abstract void appendSelectPageAfterOrderBy(StringBuilder result, ArrayList<FieldInfo> orderBys, CustomSqlQuery customQuery);

    public abstract String[] envolveInSelectOneRow(String[] query);

    /*
     * Expected:
     * result.append(" ");
     * result.append(limitOne);
     * result.append(" ");
     */
    public abstract void appendSelectOneRowBeforeSelect(StringBuilder result, ArrayList<FieldInfo> orderBys, CustomSqlQuery customQuery);
    
    /*
     * if (requireAnd) {
     *     result.append("\n    and ");
     * } else {
     *     result.append("    ");
     * }
     * result.append(limitOne);
     */
    public abstract boolean appendSelectOneRowAfterWhere(StringBuilder result, boolean requireAnd, ArrayList<FieldInfo> orderBys, CustomSqlQuery customQuery);
    
    /*
     * Expected:
     * appender.append("\n");
     * appender.append(limitOne);
     */
    public abstract void appendSelectOneRowAfterOrderBy(StringBuilder result, ArrayList<FieldInfo> orderBys, CustomSqlQuery customQuery);
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Deletion mark managment">
    public void appendNotDeleted(StringBuilder result, FieldInfo field, CustomSqlQuery customQuery) {
        if (field.isOptional()) {
            result.append(getColumnNameForWhereFromEntity(field, customQuery));
            result.append(" is null");
        } else if (field.getDataType().isBoolean()) {
            result.append(getColumnNameForWhereFromEntity(field, customQuery));
            result.append(" = ");
            result.append(falseValue());
        } else {
            getProcessingEnv().getMessager().printMessage(Diagnostic.Kind.ERROR, "Unable to handle deletion mark field, it must be optional or boolean", field.getElement());
            result.append(getColumnNameForWhereFromEntity(field, customQuery));
            result.append(" = ");
            result.append("INVALID_NOT_DELETED");
        }
    }

    public void appendNotDeletedValue(StringBuilder result, FieldInfo field) {
        if (field.isOptional()) {
            result.append("null");
        } else if (field.getDataType().isBoolean()) {
            result.append(falseValue());
        } else {
            getProcessingEnv().getMessager().printMessage(Diagnostic.Kind.ERROR, "Unable to handle deletion mark field, it must be optional or boolean", field.getElement());
            result.append("INVALID_NOT_DELETED_VALUE");
        }
    }

    public void appendDeletedValue(StringBuilder result, FieldInfo field) {
        if (field.getDataType().isDate()) {
            result.append(currentSqlDate());
        } else if (field.getDataType().isBoolean()) {
            result.append(trueValue());
        } else {
            getProcessingEnv().getMessager().printMessage(Diagnostic.Kind.ERROR, "Unable to handle deletion mark field, it must be optional or boolean", field.getElement());
            result.append("INVALID_DELETED_VALUE");
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Version managment">
    public abstract boolean handleVersionFieldOnInsert();
    public abstract boolean handleVersionFieldOnUpdate();
    public abstract boolean handleVersionFieldOnDelete();
    
    public abstract void appendInitialVersionValue(StringBuilder result, EntityInfo entity, FieldInfo field);
    public abstract void appendNextVersionValue(StringBuilder result, EntityInfo entity, FieldInfo field);
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Custom operations queries">
    public void appendTableName(StringBuilder result, EntityInfo entity, CustomSqlQuery customQuery, String prefix) {
        if (customQuery != null) {
            appendToQueryln(result, customQuery.beforeFromExpression(), prefix);
            String[] from = customQuery.from();
            if (hasQueryValue(from)) {
                appendToQueryln(result, from, prefix);
            } else {
                appendToQueryln(result, getTableName(entity, customQuery), prefix);
            }
            appendToQueryln(result, customQuery.afterFromExpression(), prefix);
        } else {
            appendToQueryln(result, getTableName(entity, null), prefix);
        }
    }
    
    @Override
    public String[] getCustomInsertQuery(OperationInfo operation) {
        Query query = operation.getAnnotation(Query.class);
        CustomSqlQuery customQuery = operation.getAnnotation(CustomSqlQuery.class);
        String finalQuery;
        if (customQuery != null && hasQueryValue(customQuery.query())) {
            finalQuery = joinln(customQuery.query());
        } else if (query == null) {
            EntityInfo entity = operation.getEntity().getCombined();
            StringBuilder result = new StringBuilder();
            result.append("insert into");
            appendTableName(result, entity, customQuery, "    ");
            result.append("\n(");

            HashSet<FieldInfo> excludeFields;
            if (customQuery != null) {
                excludeFields = retrieveFieldsForExclude(customQuery.excludeEntityFields(), entity, operation.getElement());
                appendToQueryln(result, customQuery.beforeInsertIntoExpression(), "    ");
            } else {
                excludeFields = new HashSet<FieldInfo>(0);
            }

            if (customQuery != null && hasQueryValue(customQuery.insertInto())) {
                appendToQueryln(result, customQuery.insertInto(), "    ");
            } else {
                result.append("\n");
                boolean requireComma = false;
                for (FieldInfo field : entity.getFields()) {
                    if (excludeFields.contains(field)) {
                        continue;
                    } else if (field.isManually()) {
                        continue;
                    } else if (operation.getFieldByName(field.getName()) != null) {
                        continue;
                    } else if (!field.isIdentifier()) {
                        continue;
                    } else if (field.isInsertDateMark()) {
                        if (requireComma) {
                            result.append(",\n");
                        }
                        result.append("    ");
                        result.append(getColumnName(field));
                        requireComma = true;
                    } else if (!includeIdOnInsert(entity, field)) {
                        continue;
                    } else {
                        if (requireComma) {
                            result.append(",\n");
                        }
                        result.append("    ");
                        result.append(getColumnName(field));
                        requireComma = true;
                    }
                }
                for (FieldInfo field : operation.getFields()) {
                    if (field.isManually()) {
                        continue;
                    }
                    if (requireComma) {
                        result.append(",\n");
                    }
                    result.append("    ");
                    result.append(getColumnName(field));
                    requireComma = true;
                }
                for (FieldInfo field : entity.getFields()) {
                    if (excludeFields.contains(field)) {
                        continue;
                    } else if (field.isManually()) {
                        continue;
                    } else if (operation.getFieldByName(field.getName()) != null) {
                        continue;
                    } else if (field.isIdentifier()) {
                        continue;
                    } else if (field.isInsertDateMark()) {
                        if (requireComma) {
                            result.append(",\n");
                        }
                        result.append("    ");
                        result.append(getColumnName(field));
                        requireComma = true;
                    } else if (field.isInsertUserMark()) {
                        if (requireComma) {
                            result.append(",\n");
                        }
                        result.append("    ");
                        result.append(getColumnName(field));
                        requireComma = true;
                    } else if (field.isDeletionMark()) {
                        if (requireComma) {
                            result.append(",\n");
                        }
                        result.append("    ");
                        result.append(getColumnName(field));
                        requireComma = true;
                    } else if (field.isVersionMark()) {
                        if (!handleVersionFieldOnInsert()) {
                            continue;
                        }
                        if (requireComma) {
                            result.append(",\n");
                        }
                        result.append("    ");
                        result.append(getColumnName(field));
                        requireComma = true;
                    }
                }
            }

            if (customQuery != null) {
                appendToQueryln(result, customQuery.afterInsertIntoExpression(), "    ");
            }

            result.append("\n) values (");

            if (customQuery != null) {
                appendToQueryln(result, customQuery.beforeInsertValuesExpression(), "    ");
            }

            if (customQuery != null && hasQueryValue(customQuery.insertValues())) {
                appendToQueryln(result, customQuery.insertValues(), "    ");
            } else {
                result.append("\n");
                boolean requireComma = false;
                for (FieldInfo field : entity.getFields()) {
                    if (excludeFields.contains(field)) {
                        continue;
                    } else if (field.isManually()) {
                        continue;
                    } else if (operation.getFieldByName(field.getName()) != null) {
                        continue;
                    } else if (!field.isIdentifier()) {
                        continue;
                    } else if (field.isInsertDateMark()) {
                        if (requireComma) {
                            result.append(",\n");
                        }
                        result.append("    ");
                        result.append(currentSqlDate());
                        requireComma = true;
                    } else if (!includeIdOnInsert(entity, field)) {
                        continue;
                    } else {
                        if (requireComma) {
                            result.append(",\n");
                        }
                        result.append("    ");
                        appendIdNextValue(result, entity, field);
                        requireComma = true;
                    }
                }
                for (FieldInfo field : operation.getFields()) {
                    if (field.isManually()) {
                        continue;
                    }
                    if (requireComma) {
                        result.append(",\n");
                    }
                    result.append("    ");
                    if (field.hasDefaultValueWhenInsert()) {
                        appendParameterValueOrDefaultInDatabaseWhenNullForInsert(result, operation, entity, field);
                    } else if (field.isInsertDateMark()) {
                        result.append(currentSqlDate());
                    } else if (field.isInsertUserMark()) {
                        result.append(getParameterValue(field));
                    } else if (field.isDeletionMark()) {
                        appendNotDeletedValue(result, field);
                    } else if (field.isVersionMark()) {
                        appendInitialVersionValue(result, entity, field);
                    } else {
                        result.append(getParameterValue(field));
                    }
                    requireComma = true;
                }
                for (FieldInfo field : entity.getFields()) {
                    if (excludeFields.contains(field)) {
                        continue;
                    } else if (field.isManually()) {
                        continue;
                    } else if (operation.getFieldByName(field.getName()) != null) {
                        continue;
                    } else if (field.isIdentifier()) {
                        continue;
                    } else if (field.isInsertDateMark()) {
                        if (requireComma) {
                            result.append(",\n");
                        }
                        result.append("    ");
                        result.append(currentSqlDate());
                        requireComma = true;
                    } else if (field.isInsertUserMark()) {
                        if (requireComma) {
                            result.append(",\n");
                        }
                        result.append("    ");
                        requireComma = true;
                        
                        if (field.getValueWhenNull() == null && field.getForcedValue() == null) {
                            getProcessingEnv().getMessager().printMessage(Diagnostic.Kind.ERROR, "Unable to generate Insert operation query, because is not possible to determine the insert user value for the field '" + field.getName() + "' present in the entity. Include this field as a member of the operation for allow specify one or use @CustomSqlQuery for handle it.", operation.getElement());
                            result.append("INVALID_INSERT_USER");
                        } else {
                            result.append(getParameterValue(field));
                        }
                    } else if (field.isDeletionMark()) {
                        if (requireComma) {
                            result.append(",\n");
                        }
                        result.append("    ");
                        appendNotDeletedValue(result, field);
                        requireComma = true;
                    } else if (field.isVersionMark()) {
                        if (!handleVersionFieldOnInsert()) {
                            continue;
                        }
                        if (requireComma) {
                            result.append(",\n");
                        }
                        result.append("    ");
                        appendInitialVersionValue(result, entity, field);
                        requireComma = true;
                    }
                }
            }

            if (customQuery != null) {
                appendToQueryln(result, customQuery.afterInsertValuesExpression(), "    ");
            }

            result.append("\n)");
            finalQuery = result.toString();
        } else {
            finalQuery = joinln(query.value());
        }
        finalQuery = finalizeQuery(finalQuery, operation, customQuery);
        return finalQuery.split("\n");
    }

    @Override
    public String[] getCustomUpdateQuery(OperationInfo operation) {
        Query query = operation.getAnnotation(Query.class);
        CustomSqlQuery customQuery = operation.getAnnotation(CustomSqlQuery.class);
        String finalQuery;
        if (customQuery != null && hasQueryValue(customQuery.query())) {
            finalQuery = joinln(customQuery.query());
        } else if (query == null) {
            EntityInfo entity = operation.getEntity().getCombined();
            StringBuilder result = new StringBuilder();
            result.append("update");
            appendTableName(result, entity, customQuery, "    ");
            
            boolean useSetTag = operation.hasIgnoreWhenNull();
            if (customQuery != null) {
                useSetTag = useSetTag ||
                    hasQueryValue(customQuery.beforeUpdateSetExpression()) || 
                    hasQueryValue(customQuery.updateSet()) || 
                    hasQueryValue(customQuery.afterUpdateSetExpression());
            }
            
            if (useSetTag) {
                result.append("\n");
                appendStartSet(result);
            } else {
                result.append("\nset");
            }

            HashSet<FieldInfo> excludeFields;
            if (customQuery != null) {
                excludeFields = retrieveFieldsForExclude(customQuery.excludeEntityFields(), entity, operation.getElement());
                appendToQueryln(result, customQuery.beforeUpdateSetExpression(), "    ");
            } else {
                excludeFields = new HashSet<FieldInfo>(0);
            }

            if (customQuery != null && hasQueryValue(customQuery.updateSet())) {
                appendToQueryln(result, customQuery.updateSet(), "    ");
            } else {
                result.append("\n");
                boolean requireComma = false;
                boolean hasSetValueFields = false;
                boolean requireEndSetValueIfNotNull = false;
                for (FieldInfo field : operation.getFields()) {
                    if (field.isManually()) {
                        continue;
                    } else if (!field.isSetValueMark()) {
                        continue;
                    }
                    hasSetValueFields = true;
                    if (requireEndSetValueIfNotNull) {
                        appendEndSetValueIfNotNull(result, requireComma);
                        result.append("\n");
                        requireEndSetValueIfNotNull = false;
                    } else if (requireComma) {
                        result.append(",\n");
                    }
                    result.append("    ");
                    if (field.isIgnoreWhenNull()) {
                        appendStartSetValueIfNotNull(result, field);
                        requireEndSetValueIfNotNull = true;
                    } else {
                        result.append(getColumnName(field));
                        result.append(" = ");
                        if (field.isUpdateDateMark()) {
                            result.append(currentSqlDate());
                        } else if (field.isUpdateUserMark()) {
                            result.append(getParameterValue(field));
                        } else if (field.isVersionMark()) {
                            appendNextVersionValue(result, entity, field);
                        } else {
                            result.append(getParameterValue(field));
                        }
                    }
                    requireComma = true;
                }
                for (FieldInfo field : entity.getFields()) {
                    FieldInfo operationField = operation.getFieldByName(field.getName());
                    if (excludeFields.contains(field)) {
                        continue;
                    } else if (field.isManually()) {
                        continue;
                    } else if (operationField != null && operationField.isSetValueMark()) {
                        continue;
                    } else if (field.isUpdateDateMark()) {
                        if (requireEndSetValueIfNotNull) {
                            appendEndSetValueIfNotNull(result, requireComma);
                            result.append("\n");
                            requireEndSetValueIfNotNull = false;
                        } else if (requireComma) {
                            result.append(",\n");
                        }
                        result.append("    ");
                        result.append(getColumnName(field));
                        result.append(" = ");
                        result.append(currentSqlDate());
                        requireComma = true;
                    } else if (field.isUpdateUserMark()) {
                        if (requireComma) {
                            result.append(",\n");
                        }
                        result.append("    ");
                        result.append(getColumnName(field));
                        result.append(" = ");
                        requireComma = true;
                        
                        if (field.getValueWhenNull() == null && field.getForcedValue() == null) {
                            getProcessingEnv().getMessager().printMessage(Diagnostic.Kind.ERROR, "Unable to generate Update operation query, because is not possible to determine the update user value for the field '" + field.getName() + "' present in the entity. Include this field as a member of the operation with the annotation @SetValue for allow specify one or use @CustomSqlQuery for handle it.", operation.getElement());
                            result.append("INVALID_UPDATE_USER");
                        } else {
                            result.append(getParameterValue(field));
                        }
                    } else if (field.isVersionMark()) {
                        if (!handleVersionFieldOnUpdate()) {
                            continue;
                        }
                        if (requireComma) {
                            result.append(",\n");
                        }
                        result.append("    ");
                        result.append(getColumnName(field));
                        result.append(" = ");
                        appendNextVersionValue(result, entity, field);
                        requireComma = true;
                    }
                }
                
                if (requireEndSetValueIfNotNull) {
                    appendEndSetValueIfNotNull(result, false);
                }
                
                if (!hasSetValueFields) {
                    getProcessingEnv().getMessager().printMessage(Diagnostic.Kind.ERROR, "An update operation must has SetValue fields for generate the query", operation.getElement());
                    return new String[]{""};
                }

            }

            if (customQuery != null) {
                appendToQueryln(result, customQuery.afterUpdateSetExpression(), "    ");
            }
            
            if (useSetTag) {
                appendEndSet(result, "\n");
            }
            
            ArrayList<FieldInfo> orderBys = new ArrayList<FieldInfo>(0); // No order by for this kind of operation
            appendWhere(result, operation, customQuery, false, orderBys);
            finalQuery = result.toString();
        } else {
            finalQuery = joinln(query.value());
        }
        finalQuery = finalizeQuery(finalQuery, operation, customQuery);
        return finalQuery.split("\n");
    }

    @Override
    public String[] getCustomDeleteQuery(OperationInfo operation) {
        Query query = operation.getAnnotation(Query.class);
        CustomSqlQuery customQuery = operation.getAnnotation(CustomSqlQuery.class);
        String finalQuery;
        if (customQuery != null && hasQueryValue(customQuery.query())) {
            finalQuery = joinln(customQuery.query());
        } else if (query == null) {
            EntityInfo entity = operation.getEntity().getCombined();
            StringBuilder result = new StringBuilder();
            if (operation.isUseLogicalDeletion()) {
                HashSet<FieldInfo> excludeFields;
                if (customQuery != null) {
                    excludeFields = retrieveFieldsForExclude(customQuery.excludeEntityFields(), entity, operation.getElement());
                } else {
                    excludeFields = new HashSet<FieldInfo>(0);
                }
                result.append("update");
                appendTableName(result, entity, customQuery, "    ");
                result.append("\nset\n");
                boolean requireComma = false;
                for (FieldInfo field : operation.getFields()) {
                    if (field.isManually()) {
                        continue;
                    } else if (!field.isSetValueMark()) {
                        continue;
                    } else if (field.isDeleteDateMark()) {
                        if (requireComma) {
                            result.append(",\n");
                        }
                        result.append("    ");
                        result.append(getColumnName(field));
                        result.append(" = ");
                        result.append(currentSqlDate());
                        requireComma = true;
                    } else if (field.isDeleteUserMark()) {
                        if (requireComma) {
                            result.append(",\n");
                        }
                        result.append("    ");
                        result.append(getColumnName(field));
                        result.append(" = ");
                        result.append(getParameterValue(field));
                        requireComma = true;
                    } else if (field.isDeletionMark()) {
                        if (requireComma) {
                            result.append(",\n");
                        }
                        result.append("    ");
                        result.append(getColumnName(field));
                        result.append(" = ");
                        appendDeletedValue(result, field);
                        requireComma = true;
                    } else if (field.isVersionMark()) {
                        if (!handleVersionFieldOnDelete()) {
                            continue;
                        }
                        if (requireComma) {
                            result.append(",\n");
                        }
                        result.append("    ");
                        result.append(getColumnName(field));
                        result.append(" = ");
                        appendNextVersionValue(result, entity, field);
                        requireComma = true;
                    }
                }
                for (FieldInfo field : entity.getFields()) {
                    FieldInfo operationField = operation.getFieldByName(field.getName());
                    if (excludeFields.contains(field)) {
                        continue;
                    } else if (field.isManually()) {
                        continue;
                    } else if (operationField != null && operationField.isSetValueMark()) {
                        continue;
                    } else if (field.isDeleteDateMark()) {
                        if (requireComma) {
                            result.append(",\n");
                        }
                        result.append("    ");
                        result.append(getColumnName(field));
                        result.append(" = ");
                        result.append(currentSqlDate());
                        requireComma = true;
                    } else if (field.isDeleteUserMark()) {
                        if (requireComma) {
                            result.append(",\n");
                        }
                        result.append("    ");
                        result.append(getColumnName(field));
                        result.append(" = ");
                        requireComma = true;
                            
                        if (field.getValueWhenNull() == null && field.getForcedValue() == null) {
                            getProcessingEnv().getMessager().printMessage(Diagnostic.Kind.ERROR, "Unable to generate Delete operation query, because is not possible to determine the delete user value for the field '" + field.getName() + "' present in the entity. Include this field as a member of the operation with the annotation @SetValue for allow specify one or use @CustomSqlQuery for handle it.", operation.getElement());
                            result.append("INVALID_DELETE_USER");
                        } else {
                            result.append(getParameterValue(field));
                        }
                    } else if (field.isDeletionMark()) {
                        if (requireComma) {
                            result.append(",\n");
                        }
                        result.append("    ");
                        result.append(getColumnName(field));
                        result.append(" = ");
                        appendDeletedValue(result, field);
                        requireComma = true;
                    } else if (field.isVersionMark()) {
                        if (!handleVersionFieldOnDelete()) {
                            continue;
                        }
                        if (requireComma) {
                            result.append(",\n");
                        }
                        result.append("    ");
                        result.append(getColumnName(field));
                        result.append(" = ");
                        appendNextVersionValue(result, entity, field);
                        requireComma = true;
                    }
                }
            } else {
                result.append("delete from");
                appendTableName(result, entity, customQuery, "    ");
            }
            ArrayList<FieldInfo> orderBys = new ArrayList<FieldInfo>(0); // No order by for this kind of operation
            appendWhere(result, operation, customQuery, false, orderBys);
            finalQuery = result.toString();
        } else {
            finalQuery = joinln(query.value());
        }
        finalQuery = finalizeQuery(finalQuery, operation, customQuery);
        return finalQuery.split("\n");
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Queries for diferent select operation">
    
    @Override
    public String[] getSelectCountQuery(OperationInfo operation) {
        Query query = operation.getAnnotation(Query.class);
        if (query == null) {
            return completeQuery(null, operation, true, false, false);
        }
        return completeQuery(query.value(), operation, true, false, false);
    }
    
    @Override
    public String[] getSelectOneQuery(OperationInfo operation) {
        Query query = operation.getAnnotation(Query.class);
        if (query == null) {
            return completeQuery(null, operation, false, false);
        }
        return completeQuery(query.value(), operation, false, false);
    }

    @Override
    public String[] getSelectManyQuery(OperationInfo operation) {
        Query query = operation.getAnnotation(Query.class);
        if (query == null) {
            return completeQuery(null, operation, false, false);
        }
        return completeQuery(query.value(), operation, false, false);
    }

    @Override
    public String[] getSelectPageQuery(OperationInfo operation) {
        PageQueries pageQueries = operation.getAnnotation(PageQueries.class);
        if (pageQueries == null) {
            Query query = operation.getAnnotation(Query.class);
            String[] result;
            if (query == null) {
                result = completeQuery(null, operation, false, true);
            } else {
                result = completeQuery(query.value(), operation, false, true);
            }

            if (result != null && result.length > 0) {
                return envolveInSelectPage(result);
            } else {
                return result;
            }
        }
        return completeQuery(pageQueries.selectPage(), operation, false, true);
    }

    @Override
    public String[] getSelectPageCountQuery(OperationInfo operation) {
        PageQueries pageQueries = operation.getAnnotation(PageQueries.class);
        if (pageQueries == null) {
            Query query = operation.getAnnotation(Query.class);
            String[] result;
            if (query == null) {
                result = completeQuery(null, operation, true, true);
            } else {
                result = completeQuery(query.value(), operation, true, true);
            }
            if (result != null) {
                String s = joinsp(result);
                s = s.replaceAll("\n", " ");
                s = s.toLowerCase();
                if (!s.matches("\\s*select\\s+count\\s*\\(.*")) {
                    String[] r = new String[result.length + 2];
                    r[0] = "select count(*) from (";
                    System.arraycopy(result, 0, r, 1, result.length);
                    r[r.length - 1] = ")";
                    return r;
                }
            }
            return result;
        }
        return completeQuery(pageQueries.selectCount(), operation, true, true);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Entity queries">
    @Override
    public String[] getEntitySelectByIdQuery(EntityInfo entity, OperationInfo operation) {
        EntityQueries entityQueries = entity.getAnnotation(EntityQueries.class);
        if (entityQueries != null && hasQueryValue(entityQueries.selectById())) {
            return completeQuery(entityQueries.selectById(), operation, false, false);
        } else {
            Query query = operation.getAnnotation(Query.class);
            if (query != null) {
                return completeQuery(query.value(), operation, false, false);
            } else {
                return completeQuery(null, operation, false, false);
            }
        }
    }

    @Override
    public String[] getEntityInsertQuery(EntityInfo entity, OperationInfo operation) {
        EntityQueries entityQueries = entity.getAnnotation(EntityQueries.class);
        Query query = operation.getAnnotation(Query.class);
        CustomSqlQuery customQuery = operation.getAnnotation(CustomSqlQuery.class);
        String finalQuery;
        if (entityQueries != null && hasQueryValue(entityQueries.insert())) {
            finalQuery = joinln(entityQueries.insert());
        } else if (customQuery != null && hasQueryValue(customQuery.query())) {
            finalQuery = joinln(customQuery.query());
        } else if (query != null) {
            finalQuery = joinln(query.value());
        } else {
            StringBuilder result = new StringBuilder();
            result.append("insert into");
            appendTableName(result, entity, customQuery, "    ");
            result.append("\n(");

            HashSet<FieldInfo> excludeFields;
            if (customQuery != null) {
                excludeFields = retrieveFieldsForExclude(customQuery.excludeEntityFields(), entity, operation.getElement());
                appendToQueryln(result, customQuery.beforeInsertIntoExpression(), "    ");
            } else {
                excludeFields = new HashSet<FieldInfo>(0);
            }

            if (customQuery != null && hasQueryValue(customQuery.insertInto())) {
                appendToQueryln(result, customQuery.insertInto(), "    ");
            } else {
                result.append("\n");
                boolean requireComma = false;
                for (FieldInfo field : entity.getFields()) {
                    if (excludeFields.contains(field)) {
                        continue;
                    } else if (field.isManually()) {
                        continue;
                    } else if (field.isInsertDateMark()) {
                        if (requireComma) {
                            result.append(",\n");
                        }
                        result.append("    ");
                        result.append(getColumnName(field));
                        requireComma = true;
                    } else if (field.isIdentifier()) {

                        if (!includeIdOnInsert(entity, field)) {
                            continue;
                        }
                        if (requireComma) {
                            result.append(",\n");
                        }
                        result.append("    ");
                        result.append(getColumnName(field));
                        requireComma = true;
                    } else if (field.isInsertUserMark()) {
                        if (requireComma) {
                            result.append(",\n");
                        }
                        result.append("    ");
                        result.append(getColumnName(field));
                        requireComma = true;
                    } else if (field.isUpdateDateMark()) {
                        continue;
                    } else if (field.isUpdateUserMark()) {
                        continue;
                    } else if (field.isDeleteDateMark()) {
                        continue;
                    } else if (field.isDeleteUserMark()) {
                        continue;
                    } else if (field.isDeletionMark()) {
                        if (requireComma) {
                            result.append(",\n");
                        }
                        result.append("    ");
                        result.append(getColumnName(field));
                        requireComma = true;
                    } else if (field.isVersionMark()) {
                        if (!handleVersionFieldOnInsert()) {
                            continue;
                        }
                        if (requireComma) {
                            result.append(",\n");
                        }
                        result.append("    ");
                        result.append(getColumnName(field));
                        requireComma = true;
                    } else {
                        if (requireComma) {
                            result.append(",\n");
                        }
                        result.append("    ");
                        result.append(getColumnName(field));
                        requireComma = true;
                    }
                }
            }

            if (customQuery != null) {
                appendToQueryln(result, customQuery.afterInsertIntoExpression(), "    ");
            }

            result.append("\n) values (");

            if (customQuery != null) {
                appendToQueryln(result, customQuery.beforeInsertValuesExpression(), "    ");
            }

            if (customQuery != null && hasQueryValue(customQuery.insertValues())) {
                appendToQueryln(result, customQuery.insertValues(), "    ");
            } else {
                result.append("\n");
                boolean requireComma = false;
                for (FieldInfo field : entity.getFields()) {
                    if (excludeFields.contains(field)) {
                        continue;
                    } else if (field.isManually()) {
                        continue;
                    } else if (field.isInsertDateMark()) {
                        if (requireComma) {
                            result.append(",\n");
                        }
                        result.append("    ");
                        result.append(currentSqlDate());
                        requireComma = true;
                    } else if (field.isIdentifier()) {
                        if (!includeIdOnInsert(entity, field)) {
                            continue;
                        }
                        if (requireComma) {
                            result.append(",\n");
                        }
                        result.append("    ");
                        appendIdNextValue(result, entity, field);
                        requireComma = true;
                    } else if (field.isInsertUserMark()) {
                        if (requireComma) {
                            result.append(",\n");
                        }
                        result.append("    ");
                        result.append(getParameterValue(field));
                        requireComma = true;
                    } else if (field.isUpdateDateMark()) {
                        continue;
                    } else if (field.isUpdateUserMark()) {
                        continue;
                    } else if (field.isDeleteDateMark()) {
                        continue;
                    } else if (field.isDeleteUserMark()) {
                        continue;
                    } else if (field.isDeletionMark()) {
                        if (requireComma) {
                            result.append(",\n");
                        }
                        result.append("    ");
                        appendNotDeletedValue(result, field);
                        requireComma = true;
                    } else if (field.isVersionMark()) {
                        if (!handleVersionFieldOnInsert()) {
                            continue;
                        }
                        if (requireComma) {
                            result.append(",\n");
                        }
                        result.append("    ");
                        appendInitialVersionValue(result, entity, field);
                        requireComma = true;
                    } else {
                        if (requireComma) {
                            result.append(",\n");
                        }
                        result.append("    ");
                        if (field.hasDefaultValueWhenInsert()) {
                            appendParameterValueOrDefaultInDatabaseWhenNullForInsert(result, operation, entity, field);
                        } else {
                            result.append(getParameterValue(field));
                        }
                        requireComma = true;
                    }
                }
            }

            if (customQuery != null) {
                appendToQueryln(result, customQuery.afterInsertValuesExpression(), "    ");
            }

            result.append("\n)");
            finalQuery = result.toString();
        }
        finalQuery = finalizeQuery(finalQuery, operation, customQuery);
        return finalQuery.split("\n");
    }

    @Override
    public String[] getEntityLastInsertedIdQuery(EntityInfo entity, OperationInfo operation, boolean excludeSequenceQuery) {
        EntityQueries query = entity.getAnnotation(EntityQueries.class);
        String finalQuery;
        if (query != null && hasQueryValue(query.lastInsertedId())) {
            finalQuery = joinln(query.lastInsertedId());
        } else {
            finalQuery = joinln(getIdCurrentValue(entity, entity.getFirstIdField(), excludeSequenceQuery));
            if (finalQuery == null) {
                return null;
            }
        }
        finalQuery = finalizeQuery(finalQuery, operation, null);
        if (finalQuery != null) {
            return finalQuery.split("\n");
        } else {
            return null;
        }
    }

    @Override
    public String[] getEntityUpdateQuery(EntityInfo entity, OperationInfo operation) {
        EntityQueries entityQueries = entity.getAnnotation(EntityQueries.class);
        Query query = operation.getAnnotation(Query.class);
        CustomSqlQuery customQuery = operation.getAnnotation(CustomSqlQuery.class);
        String finalQuery;
        if (entityQueries != null && hasQueryValue(entityQueries.update())) {
            finalQuery = joinln(entityQueries.update());
        } else if (customQuery != null && hasQueryValue(customQuery.query())) {
            finalQuery = joinln(customQuery.query());
        } else if (query != null) {
            finalQuery = joinln(query.value());
        } else {
            StringBuilder result = new StringBuilder();
            result.append("update");
            appendTableName(result, entity, customQuery, "    ");
            
            boolean useSetTag = false;
            if (customQuery != null) {
                useSetTag = useSetTag ||
                    hasQueryValue(customQuery.beforeUpdateSetExpression()) || 
                    hasQueryValue(customQuery.updateSet()) || 
                    hasQueryValue(customQuery.afterUpdateSetExpression());
            }
            
            if (useSetTag) {
                result.append("\n");
                appendStartSet(result);
            } else {
                result.append("\nset");
            }

            HashSet<FieldInfo> excludeFields;
            if (customQuery != null) {
                excludeFields = retrieveFieldsForExclude(customQuery.excludeEntityFields(), entity, operation.getElement());
                appendToQueryln(result, customQuery.beforeUpdateSetExpression(), "    ");
            } else {
                excludeFields = new HashSet<FieldInfo>(0);
            }

            if (customQuery != null && hasQueryValue(customQuery.updateSet())) {
                appendToQueryln(result, customQuery.updateSet(), "    ");
            } else {
                result.append("\n");
                boolean requireComma = false;
                for (FieldInfo field : entity.getFields()) {
                    if (excludeFields.contains(field)) {
                        continue;
                    } else if (field.isManually()) {
                        continue;
                    } else if (field.isIdentifier()) {
                        continue;
                    } else if (field.isUpdateDateMark()) {
                        if (requireComma) {
                            result.append(",\n");
                        }
                        result.append("    ");
                        result.append(getColumnName(field));
                        result.append(" = ");
                        result.append(currentSqlDate());
                        requireComma = true;
                    } else if (field.isUpdateUserMark()) {
                        if (requireComma) {
                            result.append(",\n");
                        }
                        result.append("    ");
                        result.append(getColumnName(field));
                        result.append(" = ");
                        result.append(getParameterValue(field));
                        requireComma = true;
                    } else if (field.isInsertDateMark()) {
                        continue;
                    } else if (field.isInsertUserMark()) {
                        continue;
                    } else if (field.isDeleteDateMark()) {
                        continue;
                    } else if (field.isDeleteUserMark()) {
                        continue;
                    } else if (field.isDeletionMark()) {
                        continue;
                    } else if (field.isVersionMark()) {
                        if (!handleVersionFieldOnUpdate()) {
                            continue;
                        }
                        if (requireComma) {
                            result.append(",\n");
                        }
                        result.append("    ");
                        result.append(getColumnName(field));
                        result.append(" = ");
                        appendNextVersionValue(result, entity, field);
                        requireComma = true;
                    } else {
                        if (requireComma) {
                            result.append(",\n");
                        }
                        result.append("    ");
                        result.append(getColumnName(field));
                        result.append(" = ");
                        result.append(getParameterValue(field));
                        requireComma = true;
                    }
                }
            }

            if (customQuery != null) {
                appendToQueryln(result, customQuery.afterUpdateSetExpression(), "    ");
            }
            
            if (useSetTag) {
                appendEndSet(result, "\n");
            }
            
            ArrayList<FieldInfo> fields = new ArrayList<FieldInfo>(1);
            for (FieldInfo field : entity.getFields()) {
                if (field.isManually()) {
                } else if (field.isIdentifier()) {
                    fields.add(field);
                }
            }
            ArrayList<FieldInfo> orderBys = new ArrayList<FieldInfo>(0); // No order by for this kind of operation
            appendWhereWithFields(result, operation, customQuery, false, fields, orderBys);
            finalQuery = result.toString();
        }
        finalQuery = finalizeQuery(finalQuery, operation, customQuery);
        return finalQuery.split("\n");
    }

    @Override
    public String[] getEntityMergeQuery(EntityInfo entity, OperationInfo operation) {
        EntityQueries entityQueries = entity.getAnnotation(EntityQueries.class);
        Query query = operation.getAnnotation(Query.class);
        CustomSqlQuery customQuery = operation.getAnnotation(CustomSqlQuery.class);
        String finalQuery;
        if (entityQueries != null && hasQueryValue(entityQueries.merge())) {
            finalQuery = joinln(entityQueries.merge());
        } else if (customQuery != null && hasQueryValue(customQuery.query())) {
            finalQuery = joinln(customQuery.query());
        } else if (query != null) {
            finalQuery = joinln(query.value());
        } else {
            StringBuilder result = new StringBuilder();
            result.append("update");
            appendTableName(result, entity, customQuery, "    ");
            result.append("\n");
            appendStartSet(result);

            HashSet<FieldInfo> excludeFields;
            if (customQuery != null) {
                excludeFields = retrieveFieldsForExclude(customQuery.excludeEntityFields(), entity, operation.getElement());
                appendToQueryln(result, customQuery.beforeUpdateSetExpression(), "    ");
            } else {
                excludeFields = new HashSet<FieldInfo>(0);
            }

            if (customQuery != null && hasQueryValue(customQuery.updateSet())) {
                appendToQueryln(result, customQuery.updateSet(), "    ");
            } else {
                result.append("\n");
                boolean requireComma = false;
                boolean requireEndSetValueIfNotNull = false;
                for (FieldInfo field : entity.getFields()) {
                    if (excludeFields.contains(field)) {
                        continue;
                    } else if (field.isManually()) {
                        continue;
                    } else if (field.isIdentifier()) {
                        continue;
                    } else if (field.isUpdateDateMark()) {
                        if (requireEndSetValueIfNotNull) {
                            appendEndSetValueIfNotNull(result, requireComma);
                            result.append("\n");
                            requireEndSetValueIfNotNull = false;
                        } else if (requireComma) {
                            result.append(",\n");
                        }
                        result.append("    ");
                        result.append(getColumnName(field));
                        result.append(" = ");
                        result.append(currentSqlDate());
                        requireComma = true;
                    } else if (field.isUpdateUserMark()) {
                        if (requireEndSetValueIfNotNull) {
                            appendEndSetValueIfNotNull(result, requireComma);
                            result.append("\n");
                            requireEndSetValueIfNotNull = false;
                        } else if (requireComma) {
                            result.append(",\n");
                        }
                        result.append("    ");
                        if (field.getValueWhenNull() == null) {
                            appendStartSetValueIfNotNull(result, field);
                            requireEndSetValueIfNotNull = true;
                        } else {
                            result.append(getColumnName(field));
                            result.append(" = ");
                            result.append(getParameterValue(field));
                        }
                        requireComma = true;
                    } else if (field.isInsertDateMark()) {
                        continue;
                    } else if (field.isInsertUserMark()) {
                        continue;
                    } else if (field.isDeleteDateMark()) {
                        continue;
                    } else if (field.isDeleteUserMark()) {
                        continue;
                    } else if (field.isDeletionMark()) {
                        continue;
                    } else if (field.isVersionMark()) {
                        if (!handleVersionFieldOnUpdate()) {
                            continue;
                        }
                        if (requireEndSetValueIfNotNull) {
                            appendEndSetValueIfNotNull(result, requireComma);
                            result.append("\n");
                            requireEndSetValueIfNotNull = false;
                        } else if (requireComma) {
                            result.append(",\n");
                        }
                        result.append("    ");
                        result.append(getColumnName(field));
                        result.append(" = ");
                        appendNextVersionValue(result, entity, field);
                        requireComma = true;
                    } else {
                        if (requireEndSetValueIfNotNull) {
                            appendEndSetValueIfNotNull(result, requireComma);
                            result.append("\n");
                            requireEndSetValueIfNotNull = false;
                        } else if (requireComma) {
                            result.append(",\n");
                        }
                        result.append("    ");
                        DataTypeInfo dataType = field.getDataType();
                        if (dataType.isPrimitive()) {
                            getProcessingEnv().getMessager().printMessage(Diagnostic.Kind.ERROR, "Fields of a entity used in a merge operation must allow use must allow null values, but a primitive data types do not allow it; you must use " + dataType.ensureBoxed().getSimpleName() + " instead of " + dataType.getSimpleName() + " in the field " + field.getName(), field.getElement());
                        }
                        if (field.getValueWhenNull() == null) {
                            appendStartSetValueIfNotNull(result, field);
                            requireEndSetValueIfNotNull = true;
                        } else {
                            result.append(getColumnName(field));
                            result.append(" = ");
                            result.append(getParameterValue(field));
                        }
                        requireComma = true;
                    }
                }
                if (requireEndSetValueIfNotNull) {
                    appendEndSetValueIfNotNull(result, false);
                }
            }

            if (customQuery != null) {
                appendToQueryln(result, customQuery.afterUpdateSetExpression(), "    ");
            }
            appendEndSet(result, "\n");
            
            ArrayList<FieldInfo> fields = new ArrayList<FieldInfo>(1);
            for (FieldInfo field : entity.getFields()) {
                if (field.isManually()) {
                } else if (field.isIdentifier()) {
                    fields.add(field);
                }
            }
            ArrayList<FieldInfo> orderBys = new ArrayList<FieldInfo>(0); // No order by for this kind of operation
            appendWhereWithFields(result, operation, customQuery, false, fields, orderBys);
            finalQuery = result.toString();
        }
        finalQuery = finalizeQuery(finalQuery, operation, customQuery);
        return finalQuery.split("\n");
    }

    @Override
    public String[] getEntityDeleteByIdQuery(EntityInfo entity, OperationInfo operation) {
        EntityQueries entityQueries = entity.getAnnotation(EntityQueries.class);
        Query query = operation.getAnnotation(Query.class);
        CustomSqlQuery customQuery = operation.getAnnotation(CustomSqlQuery.class);
        String finalQuery;
        if (entityQueries != null && hasQueryValue(entityQueries.deleteById())) {
            finalQuery = joinln(entityQueries.deleteById());
        } else if (customQuery != null && hasQueryValue(customQuery.query())) {
            finalQuery = joinln(customQuery.query());
        } else if (query != null) {
            finalQuery = joinln(query.value());
        } else {
            StringBuilder result = new StringBuilder();
            if (operation.isUseLogicalDeletion()) {
                HashSet<FieldInfo> excludeFields;
                if (customQuery != null) {
                    excludeFields = retrieveFieldsForExclude(customQuery.excludeEntityFields(), entity, operation.getElement());
                } else {
                    excludeFields = new HashSet<FieldInfo>(0);
                }
                result.append("update");
                appendTableName(result, entity, customQuery, "    ");
                result.append("\nset\n");
                boolean requireComma = false;
                for (FieldInfo field : entity.getFields()) {
                    if (excludeFields.contains(field)) {
                        continue;
                    } else if (field.isManually()) {
                        continue;
                    } else if (field.isDeleteDateMark()) {
                        if (requireComma) {
                            result.append(",\n");
                        }
                        result.append("    ");
                        result.append(getColumnName(field));
                        result.append(" = ");                
                        result.append(currentSqlDate());
                        requireComma = true;
                    } else if (field.isDeleteUserMark()) {
                        if (requireComma) {
                            result.append(",\n");
                        }
                        result.append("    ");
                        result.append(getColumnName(field));
                        result.append(" = ");
                        requireComma = true;
                            
                        if (field.getValueWhenNull() == null && field.getForcedValue() == null) {
                            getProcessingEnv().getMessager().printMessage(Diagnostic.Kind.ERROR, "Unable to generate DeleteEntityById operation query, because is not possible to determine the delete user value for the field '" + field.getName() + "'. Create your own delete operation for specify one or use @CustomSqlQuery for handle it.", operation.getElement());
                            result.append("INVALID_ENTITY_DELETE_USER");
                        } else {
                            result.append(getParameterValue(field));
                        }
                    } else if (field.isDeletionMark()) {
                        if (requireComma) {
                            result.append(",\n");
                        }
                        result.append("    ");
                        result.append(getColumnName(field));
                        result.append(" = ");
                        appendDeletedValue(result, field);
                        requireComma = true;
                    } else if (field.isVersionMark()) {
                        if (!handleVersionFieldOnDelete()) {
                            continue;
                        }
                        if (requireComma) {
                            result.append(",\n");
                        }
                        result.append("    ");
                        result.append(getColumnName(field));
                        result.append(" = ");
                        appendNextVersionValue(result, entity, field);
                        requireComma = true;
                    }
                }
            } else {
                result.append("delete from");
                appendTableName(result, entity, customQuery, "    ");
            }
            
            ArrayList<FieldInfo> fields = new ArrayList<FieldInfo>(1);
            for (FieldInfo field : operation.getFields()) {
                if (field.isManually()) {
                } else if (field.isIdentifier()) {
                    fields.add(field);
                }
            }
            ArrayList<FieldInfo> orderBys = new ArrayList<FieldInfo>(0); // No order by for this kind of operation
            appendWhereWithFields(result, operation, customQuery, false, fields, orderBys);
            finalQuery = result.toString();
        }
        finalQuery = finalizeQuery(finalQuery, operation, customQuery);
        return finalQuery.split("\n");
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="For generate entity queries">
    private static final Pattern idSecuenceNameTemplatePattern = Pattern.compile("\\[\\[(.*?)\\]\\]");
    public String getIdSequenceName(EntityInfo entity, FieldInfo field) {
        String table = joinsp(getTableName(entity, null));
        String column = getColumnName(field);
        String template;
        IdSequenceName idSequenceName = field.getAnnotation(IdSequenceName.class);
        if (idSequenceName != null) {
            template = idSequenceName.value();
        } else {
            idSequenceName = entity.getAnnotation(IdSequenceName.class);
            if (idSequenceName != null) {
                template = idSequenceName.value();
            } else {
                template = getConfiguration().getIdSecuenceNameTemplate();
            }
        }
        if (template == null || template.isEmpty()) {
            return null;
        }
        Matcher matcher = idSecuenceNameTemplatePattern.matcher(template);
        StringBuffer sb = new StringBuffer(template.length());
        while (matcher.find()) {
            String rule = matcher.group(1);
            if ("column".equals(rule)) {
                matcher.appendReplacement(sb, column);
            } else if ("COLUMN".equals(rule)) {
                matcher.appendReplacement(sb, column);
            } else if ("table".equals(rule)) {
                matcher.appendReplacement(sb, table);
            } else if ("TABLE".equals(rule)) {
                matcher.appendReplacement(sb, table);
            } else {
                getProcessingEnv().getMessager().printMessage(Diagnostic.Kind.ERROR, "Invalid template rule for generate the id sequence name: " + matcher.group(), field.getElement());
            }
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
    
    private static final Pattern defaultValueTemplatePattern = Pattern.compile("\\[\\[(.*?)\\]\\]");
    public String getDefaultValue(EntityInfo entity, FieldInfo field) {
        String table = joinsp(getTableName(entity, null));
        String column = getColumnName(field);
        String template = getConfiguration().getDefaultValue();
        if (template == null || template.isEmpty()) {
            return null;
        }
        Matcher matcher = defaultValueTemplatePattern.matcher(template);
        StringBuffer sb = new StringBuffer(template.length());
        while (matcher.find()) {
            String rule = matcher.group(1);
            if ("column".equals(rule)) {
                matcher.appendReplacement(sb, column);
            } else if ("COLUMN".equals(rule)) {
                matcher.appendReplacement(sb, column);
            } else if ("table".equals(rule)) {
                matcher.appendReplacement(sb, table);
            } else if ("TABLE".equals(rule)) {
                matcher.appendReplacement(sb, table);
            } else {
                getProcessingEnv().getMessager().printMessage(Diagnostic.Kind.ERROR, "Invalid template rule for generate the default value: " + matcher.group(), field.getElement());
            }
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
    
    public abstract String getParameterValue(FieldInfo field, boolean ignoreValueWhenNull, boolean ignoreForcedValue);
    public String getParameterValue(FieldInfo field, boolean ignoreValueWhenNull) {
        return getParameterValue(field, ignoreValueWhenNull, false);
    }
    public String getParameterValue(FieldInfo field) {
        return getParameterValue(field, false);
    }
    
    public boolean includeIdOnInsert(EntityInfo entity, FieldInfo field) {
        if (!field.isIdentifierAutogenerated()) {
            return true;
        }
        IdQueries idQueries = field.getAnnotation(IdQueries.class);
        if (idQueries != null) {
            return true;
        } else {
            idQueries = entity.getAnnotation(IdQueries.class);
            if (idQueries != null) {
                return true;
            }
        }
        IdSequenceName idSequenceName = field.getAnnotation(IdSequenceName.class);
        if (idSequenceName != null) {
            return true;
        } else {
            idSequenceName = entity.getAnnotation(IdSequenceName.class);
            if (idSequenceName != null) {
                return true;
            }
        }
        return !getConfiguration().useAutoIncrementId();
    }

    public String[] getIdNextValue(EntityInfo entity, FieldInfo field) {
        if (!field.isIdentifierAutogenerated()) {
            String value = getParameterValue(field);
            return new String[]{value};
        }
        IdQueries idQueries = field.getAnnotation(IdQueries.class);
        if (idQueries != null) {
            return idQueries.selectNextValue();
        } else {
            idQueries = entity.getAnnotation(IdQueries.class);
            if (idQueries != null) {
                return idQueries.selectNextValue();
            } else {
                return getIdSequenceNextValue(entity, field);
            }
        }
    }
    
    public void appendIdNextValue(StringBuilder result, EntityInfo entity, FieldInfo field) {
        String[] nextValueQuery = getIdNextValue(entity, field);
        if (nextValueQuery != null) {
            for (int i = 0; i < nextValueQuery.length - 1; i++) {
                result.append(nextValueQuery[i]);
                result.append(" ");
            }
            result.append(nextValueQuery[nextValueQuery.length - 1]);
        } else {
            result.append("null");
        }
    }

    public String[] getIdCurrentValue(EntityInfo entity, FieldInfo field, boolean excludeSequenceQuery) {
        IdQueries idQueries = field.getAnnotation(IdQueries.class);
        if (idQueries != null) {
            return idQueries.selectCurrentValue();
        } else {
            idQueries = entity.getAnnotation(IdQueries.class);
            if (idQueries != null) {
                return idQueries.selectCurrentValue();
            } else if (!excludeSequenceQuery) {
                return getIdSequenceCurrentValue(entity, field);
            } else {
                return null;
            }
        }

    }

    public abstract String[] getIdSequenceNextValue(EntityInfo entity, FieldInfo field);

    public abstract String[] getIdSequenceCurrentValue(EntityInfo entity, FieldInfo field);
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Inline query elements managment">
    private static final Pattern comparatorPattern = Pattern.compile("\\[\\[(.*?)\\]\\]");
    public String getConditionComparator(String comparatorRule, String template, FieldInfo field, CustomSqlQuery customQuery) {
        if (template == null) {
            return null;
        }
        String condition = null;

        Matcher matcher = comparatorPattern.matcher(template);
        StringBuffer sb = new StringBuffer(template.length());
        while (matcher.find()) {
            String rule = matcher.group(1);
            String value = getConditionElementValue(rule, field, customQuery);
            if (value != null) {
                matcher.appendReplacement(sb, value);
            } else if ("condition".equals(rule) || "CONDITION".equals(rule)) {
                if (condition == null) {
                    Matcher matcherc = comparatorPattern.matcher(comparatorRule);
                    StringBuffer sbc = new StringBuffer(comparatorRule.length());
                    while (matcherc.find()) {
                        String rulec = matcherc.group(1);
                        String valuec = getConditionElementValue(rulec, field, customQuery);
                        if (valuec == null) {
                            getProcessingEnv().getMessager().printMessage(Diagnostic.Kind.ERROR, "Invalid comparator rule element: " + matcherc.group(), field.getElement());
                        } else {
                            matcherc.appendReplacement(sbc, valuec);
                        }
                    }
                    matcherc.appendTail(sbc);
                    condition = sbc.toString();
                }
                matcher.appendReplacement(sb, condition);
            } else {
                getProcessingEnv().getMessager().printMessage(Diagnostic.Kind.ERROR, "Invalid comparator element: " + matcher.group(), field.getElement());
            }
        }
        matcher.appendTail(sb);
        template = sb.toString();
        
        return template;
    }

    private static final Pattern finalizationPattern = Pattern.compile("\\{\\{(.*?)(?:\\:(.*?)(?:\\:(.*?))?)?\\}\\}");;
    public String finalizeQuery(String query, OperationInfo operation, CustomSqlQuery customQuery) {
        if (customQuery != null) {
            StringBuilder sb = new StringBuilder();
            appendToQueryln(sb, customQuery.beforeQuery(), "");
            if (sb.length() > 0) {
                sb.append("\n");
            }
            sb.append(query);
            appendToQueryln(sb, customQuery.afterQuery(), "");
            query = sb.toString();
        }
        if (query == null) {
            return null;
        }
        Matcher matcher = finalizationPattern.matcher(query);
        StringBuffer result = new StringBuffer(query.length());
        while(matcher.find()) {
            String name = matcher.group(1);
            FieldInfo field = operation.getFieldByName(name);
            if (field == null) {
                if (name != null && name.startsWith("_app.")) {
                    field = getApplicationParameter(name, matcher, operation);
                    if (field == null) {
                        continue;
                    }
                } else {
                    getProcessingEnv().getMessager().printMessage(Diagnostic.Kind.ERROR, "Unable to find the field used in the query element: " + matcher.group(), operation.getElement());
                    continue;
                }
            }
            
            if (field.isManuallyProgrammatically()) {
                getProcessingEnv().getMessager().printMessage(Diagnostic.Kind.ERROR, "The field '" + field.getName() + "' is marked as not available in the sql, you cannot use in the query element: " + matcher.group(), operation.getElement());
                continue;
            }

            Comparators comparator;
            String comparatorTemplate;
            String template;
            String rule = matcher.group(2);
            String value = getConditionElementValue(rule, field, customQuery);
            if (value != null) {
                matcher.appendReplacement(result, value);
                continue;
            } else if (rule == null) { // alias for {{field:value}}
                value = getParameterValue(field);
                matcher.appendReplacement(result, value);
                continue;
            } else if ("condition".equals(rule) || "CONDITION".equals(rule)) {
                comparator = getComparator(field);
                comparatorTemplate = translateComparator(comparator);
                template = getConditionTemplate(field, comparatorTemplate);
                if (!template.equals(comparatorTemplate)) {
                    template = getConditionComparator(comparatorTemplate, template, field, customQuery);
                    template = finalizeQuery(template, operation, customQuery);
                    matcher.appendReplacement(result, template);
                    continue;
                }
            } else if ("custom".equals(rule) || "CUSTOM".equals(rule)) {
                template = matcher.group(3);
                comparator = getComparator(field);
                comparatorTemplate = translateComparator(comparator);
                if (template != null) {
                    template = getConditionComparator(comparatorTemplate, template, field, customQuery);
                    template = finalizeQuery(template, operation, customQuery);
                    matcher.appendReplacement(result, template);
                    continue;
                } else {
                    template = comparatorTemplate;
                }
            } else if ("ifNull".equals(rule) || "IF_NULL".equals(rule)) {
                StringBuilder sb = new StringBuilder();
                String separator = matcher.group(3);
                if (separator == null) {
                    separator = "";
                }
                appendConditionStartIfNull(sb, field, separator);
                matcher.appendReplacement(result, sb.toString());
                continue;
            } else if ("ifNotNull".equals(rule) || "IF_NOT_NULL".equals(rule)) {
                StringBuilder sb = new StringBuilder();
                String separator = matcher.group(3);
                if (separator == null) {
                    separator = "";
                }
                appendConditionStartIfNotNull(sb, field, separator);
                matcher.appendReplacement(result, sb.toString());
                continue;
            } else if ("endIf".equals(rule) || "END_IF".equals(rule)) {
                StringBuilder sb = new StringBuilder();
                appendConditionEndIf(sb);
                matcher.appendReplacement(result, sb.toString());
                continue;
            } else {
                if ("=".equals(rule) || "==".equals(rule) || "equal".equals(rule) || "EQUAL".equals(rule)) {
                    comparator = Comparators.EQUAL;
                } else if ("!=".equals(rule) || "<>".equals(rule) || "notEqual".equals(rule) || "NOT_EQUAL".equals(rule)) {
                    comparator = Comparators.NOT_EQUAL;
                } else if ("i=".equals(rule) || "i==".equals(rule) || "I=".equals(rule) || "I==".equals(rule) || "iequal".equals(rule) || "IEQUAL".equals(rule) || "equalInsensitive".equals(rule) || "EQUAL_INSENSITIVE".equals(rule)) {
                    comparator = Comparators.EQUAL_INSENSITIVE;
                } else if ("i!=".equals(rule) || "i<>".equals(rule) || "I!=".equals(rule) || "I<>".equals(rule) || "inotEqual".equals(rule) || "INOT_EQUAL".equals(rule) || "notEqualInsensitive".equals(rule) || "NOT_EQUAL_INSENSITIVE".equals(rule)) {
                    comparator = Comparators.NOT_EQUAL_INSENSITIVE;
                } else if ("<".equals(rule) || "smaller".equals(rule) || "SMALLER".equals(rule)) {
                    comparator = Comparators.SMALLER;
                } else if (">".equals(rule) || "larger".equals(rule) || "LARGER".equals(rule)) {
                    comparator = Comparators.LARGER;
                } else if ("<=".equals(rule) || "smallAs".equals(rule) || "SMALL_AS".equals(rule)) {
                    comparator = Comparators.SMALL_AS;
                } else if (">=".equals(rule) || "largerAs".equals(rule) || "LARGER_AS".equals(rule)) {
                    comparator = Comparators.LARGER_AS;
                } else if ("in".equals(rule) || "IN".equals(rule)) {
                    comparator = Comparators.IN;
                } else if ("notIn".equals(rule) || "NOT_IN".equals(rule)) {
                    comparator = Comparators.NOT_IN;
                } else if ("like".equals(rule) || "LIKE".equals(rule)) {
                    comparator = Comparators.LIKE;
                } else if ("notLike".equals(rule) || "NOT_LIKE".equals(rule)) {
                    comparator = Comparators.NOT_LIKE;
                } else if ("ilike".equals(rule) || "ILIKE".equals(rule) || "likeInsensitive".equals(rule) || "LIKE_INSENSITIVE".equals(rule)) {
                    comparator = Comparators.LIKE_INSENSITIVE;
                } else if ("notIlike".equals(rule) || "NOT_ILIKE".equals(rule) || "notLikeInsensitive".equals(rule) || "NOT_LIKE_INSENSITIVE".equals(rule)) {
                    comparator = Comparators.NOT_LIKE_INSENSITIVE;
                } else if ("startWith".equals(rule) || "START_WITH".equals(rule)) {
                    comparator = Comparators.START_WITH;
                } else if ("notStartWith".equals(rule) || "NOT_START_WITH".equals(rule)) {
                    comparator = Comparators.NOT_START_WITH;
                } else if ("endWith".equals(rule) || "END_WITH".equals(rule)) {
                    comparator = Comparators.END_WITH;
                } else if ("notEndWith".equals(rule) || "NOT_END_WITH".equals(rule)) {
                    comparator = Comparators.NOT_END_WITH;
                } else if ("istartWith".equals(rule) || "ISTART_WITH".equals(rule) || "startWithInsensitive".equals(rule) || "START_WITH_INSENSITIVE".equals(rule)) {
                    comparator = Comparators.START_WITH_INSENSITIVE;
                } else if ("notIstartWith".equals(rule) || "NOT_ISTART_WITH".equals(rule) || "notStartWithInsensitive".equals(rule) || "NOT_START_WITH_INSENSITIVE".equals(rule)) {
                    comparator = Comparators.NOT_START_WITH_INSENSITIVE;
                } else if ("iendWith".equals(rule) || "IEND_WITH".equals(rule) || "endWithInsensitive".equals(rule) || "END_WITH_INSENSITIVE".equals(rule)) {
                    comparator = Comparators.END_WITH_INSENSITIVE;
                } else if ("notIendWith".equals(rule) || "NOT_IEND_WITH".equals(rule) || "notEndWithInsensitive".equals(rule) || "NOT_END_WITH_INSENSITIVE".equals(rule)) {
                    comparator = Comparators.NOT_END_WITH_INSENSITIVE;
                } else if ("contains".equals(rule) || "CONTAINS".equals(rule)) {
                    comparator = Comparators.CONTAINS;
                } else if ("notContains".equals(rule) || "NOT_CONTAINS".equals(rule)) {
                    comparator = Comparators.NOT_CONTAINS;
                } else if ("icontains".equals(rule) || "ICONTAINS".equals(rule) || "containsInsensitive".equals(rule) || "CONTAINS_INSENSITIVE".equals(rule)) {
                    comparator = Comparators.CONTAINS_INSENSITIVE;
                } else if ("notIcontains".equals(rule) || "NOT_ICONTAINS".equals(rule) || "notContainsInsensitive".equals(rule) || "NOT_CONTAINS_INSENSITIVE".equals(rule)) {
                    comparator = Comparators.NOT_CONTAINS_INSENSITIVE;
                } else {
                    getProcessingEnv().getMessager().printMessage(Diagnostic.Kind.ERROR, "Invalid comparator rule used in: " + matcher.group(), operation.getElement());
                    continue;
                }
                comparatorTemplate = translateComparator(comparator);
                template = comparatorTemplate;
            }
            String condition = getConditionComparator(comparatorTemplate, template, field, customQuery);
            matcher.appendReplacement(result, condition);
        }
        matcher.appendTail(result);
        return result.toString();
    }
    
    public FieldInfo getApplicationParameter(String name, Matcher matcher, OperationInfo operation) {
        String parameterName = name.substring(5);
        EntityInfo _app = getConfiguration().getApplicationParameter();
        if (_app == null) {
            getProcessingEnv().getMessager().printMessage(Diagnostic.Kind.ERROR, "You must configure the application parameter type using the @UaithneConfiguration annotation to allow use it in the query element: " + matcher.group(), operation.getElement());
            return null;
        }
        FieldInfo field = _app.getFieldByName(parameterName);
        if (field == null) {
            getProcessingEnv().getMessager().printMessage(Diagnostic.Kind.ERROR, "Unable to find the field used in the query element: " + matcher.group(), operation.getElement());
            return null;
        }
        field = new FieldInfo(name, field);
        return field;
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Retrieve fields for exclude">
    public HashSet<FieldInfo> retrieveFieldsForExclude(String[] fields, EntityInfo entity, Element element) {
        if (fields == null) {
            return new HashSet<FieldInfo>(0);
        }
        HashSet<FieldInfo> result = new HashSet<FieldInfo>(fields.length);
        for (String s : fields) {
            FieldInfo field = entity.getFieldByName(s);
            if (field != null) {
                result.add(field);
            } else {
                getProcessingEnv().getMessager().printMessage(Diagnostic.Kind.ERROR, "Unable to find the field with name '" + s + "' specified as exclude entity field in the CustomSqlQuery annotation", element);
            }
        }
        return result;
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Complex call queries">
    @Override
    public String[] getComplexSelectCallQuery(OperationInfo operation) {
        Query query = operation.getAnnotation(Query.class);
        CustomSqlQuery customQuery = operation.getAnnotation(CustomSqlQuery.class);
        String finalQuery;
        if (customQuery != null && hasQueryValue(customQuery.query())) {
            finalQuery = joinln(customQuery.query());
        } else if (query == null) {
            getProcessingEnv().getMessager().printMessage(Diagnostic.Kind.ERROR, "Unable to generate ComplexSelectCall operation query, you must specify one manually.", operation.getElement());
            return new String[]{""};
        } else {
            finalQuery = joinln(query.value());
        }
        finalQuery = finalizeQuery(finalQuery, operation, customQuery);
        return finalQuery.split("\n");
    }
    
    @Override
    public String[] getComplexInsertCallQuery(OperationInfo operation) {
        Query query = operation.getAnnotation(Query.class);
        CustomSqlQuery customQuery = operation.getAnnotation(CustomSqlQuery.class);
        String finalQuery;
        if (customQuery != null && hasQueryValue(customQuery.query())) {
            finalQuery = joinln(customQuery.query());
        } else if (query == null) {
            getProcessingEnv().getMessager().printMessage(Diagnostic.Kind.ERROR, "Unable to generate ComplexInsertCall operation query, you must specify one manually.", operation.getElement());
            return new String[]{""};
        } else {
            finalQuery = joinln(query.value());
        }
        finalQuery = finalizeQuery(finalQuery, operation, customQuery);
        return finalQuery.split("\n");
    }
    
    @Override
    public String[] getComplexUpdateCallQuery(OperationInfo operation) {
        Query query = operation.getAnnotation(Query.class);
        CustomSqlQuery customQuery = operation.getAnnotation(CustomSqlQuery.class);
        String finalQuery;
        if (customQuery != null && hasQueryValue(customQuery.query())) {
            finalQuery = joinln(customQuery.query());
        } else if (query == null) {
            getProcessingEnv().getMessager().printMessage(Diagnostic.Kind.ERROR, "Unable to generate ComplexUpdateCall operation query, you must specify one manually.", operation.getElement());
            return new String[]{""};
        } else {
            finalQuery = joinln(query.value());
        }
        finalQuery = finalizeQuery(finalQuery, operation, customQuery);
        return finalQuery.split("\n");
    }
    
    @Override
    public String[] getComplexDeleteCallQuery(OperationInfo operation) {
        Query query = operation.getAnnotation(Query.class);
        CustomSqlQuery customQuery = operation.getAnnotation(CustomSqlQuery.class);
        String finalQuery;
        if (customQuery != null && hasQueryValue(customQuery.query())) {
            finalQuery = joinln(customQuery.query());
        } else if (query == null) {
            getProcessingEnv().getMessager().printMessage(Diagnostic.Kind.ERROR, "Unable to generate ComplexDeleteCall operation query, you must specify one manually.", operation.getElement());
            return new String[]{""};
        } else {
            finalQuery = joinln(query.value());
        }
        finalQuery = finalizeQuery(finalQuery, operation, customQuery);
        return finalQuery.split("\n");
    }
    //</editor-fold>
}
