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

import java.util.List;
import org.uaithne.annotations.DeleteUser;
import org.uaithne.annotations.InsertDate;
import org.uaithne.annotations.InsertUser;
import org.uaithne.annotations.UpdateUser;
import org.uaithne.annotations.Version;
import org.uaithne.generator.commons.EntityInfo;
import org.uaithne.generator.commons.FieldInfo;
import org.uaithne.generator.commons.OperationInfo;

public abstract class AbstractSqlCallGenerator extends AbstractSqlGenerator {
    
    public abstract void appendInParameter(StringBuilder query, FieldInfo field);
    public abstract void appendOutParameter(StringBuilder query, FieldInfo field);
    public abstract void appendInOutParameter(StringBuilder query, FieldInfo field);

//    TODO: add support for select operations as a procedure call?
//    public abstract void appendStartSelectManyQuery(StringBuilder query, OperationInfo operation);
//    public abstract void appendStartSelectOneQuery(StringBuilder query, OperationInfo operation);
//    public abstract void appendStartSelectPageCountQuery(StringBuilder query, OperationInfo operation);
//    public abstract void appendStartSelectPageQuery(StringBuilder query, OperationInfo operation);
//    public abstract void appendStartEntitySelectByIdQuery(StringBuilder query, EntityInfo entity);
    
    public abstract void appendStartEntityDeleteByIdQuery(StringBuilder query, EntityInfo entity);
    public abstract void appendStartEntityInsertQuery(StringBuilder query, EntityInfo entity);
    public abstract void appendStartEntityLastInsertedIdQuery(StringBuilder query, EntityInfo entity);
    public abstract void appendStartEntityMergeQuery(StringBuilder query, EntityInfo entity);
    public abstract void appendStartEntityUpdateQuery(StringBuilder query, EntityInfo entity);
    public abstract void appendStartCustomDeleteQuery(StringBuilder query, OperationInfo operation);
    public abstract void appendStartCustomInsertQuery(StringBuilder query, OperationInfo operation);
    public abstract void appendStartCustomUpdateQuery(StringBuilder query, OperationInfo operation);
    public abstract void appendEndQuery(StringBuilder query);
    
    public boolean omitIdOnInsert(EntityInfo entity, FieldInfo field) {
        return omitOnInsert(field);
    }
    
    public String finalizeQuery(String query, EntityInfo entity) {
        return query;
    }
    
    public String finalizeQuery(String query, OperationInfo operation) {
        return query;
    }

    //<editor-fold defaultstate="collapsed" desc="Entity queries">
    @Override
    public String[] getEntitySelectByIdQuery(EntityInfo entity) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String[] getEntityInsertQuery(EntityInfo entity) {
        StringBuilder result = new StringBuilder();
        appendStartEntityInsertQuery(result, entity);

        boolean requireComma = false;
        List<FieldInfo> idFields = entity.getIdFields();
        for (FieldInfo field : idFields) {
            if (omitIdOnInsert(entity, field)) {
                continue;
            }
            if (requireComma) {
                result.append(", ");
            }
            appendOutParameter(result, field);
            requireComma = true;
        }
        for (FieldInfo field : entity.getMandatoryAndOptionalFields()) {
            if (omitOnInsert(field)) {
                continue;
            }
            if (requireComma) {
                result.append(", ");
            }
            appendInParameter(result, field);
            requireComma = true;
        }
        List<FieldInfo> fieldsWithExtras = entity.getFieldsWithExtras();
        for (FieldInfo field : fieldsWithExtras) {
            if (field.isManually()) {
                continue;
            } else if (field.getAnnotation(InsertUser.class) != null) {
                if (requireComma) {
                    result.append(",\n");
                }
                appendInParameter(result, field);
                requireComma = true;
            }
        }
        for (FieldInfo field : fieldsWithExtras) {
            if (field.isManually()) {
                continue;
            } else if (field.getAnnotation(Version.class) != null) {
                if (requireComma) {
                    result.append(",\n");
                }
                appendOutParameter(result, field);
                requireComma = true;
            }
        }

        appendEndQuery(result);
        
        String finalQuery = result.toString();
        finalQuery = finalizeQuery(finalQuery, entity);
        return finalQuery.split("\n");
    }

    @Override
    public String[] getEntityLastInsertedIdQuery(EntityInfo entity) {
        StringBuilder result = new StringBuilder();
        appendStartEntityLastInsertedIdQuery(result, entity);
        appendEndQuery(result);
        
        String finalQuery = result.toString();
        finalQuery = finalizeQuery(finalQuery, entity);
        return finalQuery.split("\n");
    }

    @Override
    public String[] getEntityUpdateQuery(EntityInfo entity) {
        StringBuilder result = new StringBuilder();
        appendStartEntityUpdateQuery(result, entity);

        boolean requireComma = false;
        List<FieldInfo> idFields = entity.getIdFields();
        for (FieldInfo field : idFields) {
            if (omitOnUpdate(field)) {
                continue;
            }
            if (requireComma) {
                result.append(", ");
            }
            appendOutParameter(result, field);
            requireComma = true;
        }
        for (FieldInfo field : entity.getMandatoryAndOptionalFields()) {
            if (omitOnUpdate(field)) {
                continue;
            }
            if (requireComma) {
                result.append(", ");
            }
            appendInParameter(result, field);
            requireComma = true;
        }
        List<FieldInfo> fieldsWithExtras = entity.getFieldsWithExtras();
        for (FieldInfo field : fieldsWithExtras) {
            if (field.isManually()) {
                continue;
            } else if (field.getAnnotation(UpdateUser.class) != null) {
                if (requireComma) {
                    result.append(",\n");
                }
                appendInParameter(result, field);
                requireComma = true;
            }
        }
        for (FieldInfo field : fieldsWithExtras) {
            if (field.isManually()) {
                continue;
            } else if (field.getAnnotation(Version.class) != null) {
                if (requireComma) {
                    result.append(",\n");
                }
                appendInOutParameter(result, field);
                requireComma = true;
            }
        }

        appendEndQuery(result);
        
        String finalQuery = result.toString();
        finalQuery = finalizeQuery(finalQuery, entity);
        return finalQuery.split("\n");
    }

    @Override
    public String[] getEntityMergeQuery(EntityInfo entity) {
        StringBuilder result = new StringBuilder();
        appendStartEntityMergeQuery(result, entity);

        boolean requireComma = false;
        List<FieldInfo> idFields = entity.getIdFields();
        for (FieldInfo field : idFields) {
            if (omitOnUpdate(field)) {
                continue;
            }
            if (requireComma) {
                result.append(", ");
            }
            appendOutParameter(result, field);
            requireComma = true;
        }
        for (FieldInfo field : entity.getMandatoryAndOptionalFields()) {
            if (omitOnUpdate(field)) {
                continue;
            }
            if (requireComma) {
                result.append(", ");
            }
            appendInParameter(result, field);
            requireComma = true;
        }
        List<FieldInfo> fieldsWithExtras = entity.getFieldsWithExtras();
        for (FieldInfo field : fieldsWithExtras) {
            if (field.isManually()) {
                continue;
            } else if (field.getAnnotation(UpdateUser.class) != null) {
                if (requireComma) {
                    result.append(",\n");
                }
                appendInParameter(result, field);
                requireComma = true;
            }
        }
        for (FieldInfo field : fieldsWithExtras) {
            if (field.isManually()) {
                continue;
            } else if (field.getAnnotation(Version.class) != null) {
                if (requireComma) {
                    result.append(",\n");
                }
                appendInOutParameter(result, field);
                requireComma = true;
            }
        }

        appendEndQuery(result);
        
        String finalQuery = result.toString();
        finalQuery = finalizeQuery(finalQuery, entity);
        return finalQuery.split("\n");
    }

    @Override
    public String[] getEntityDeleteByIdQuery(EntityInfo entity) {
        StringBuilder result = new StringBuilder();
        appendStartEntityDeleteByIdQuery(result, entity);

        boolean requireComma = false;
        List<FieldInfo> idFields = entity.getIdFields();
        for (FieldInfo field : idFields) {
            if (omitOnDelete(field)) {
                continue;
            }
            if (requireComma) {
                result.append(", ");
            }
            appendOutParameter(result, field);
            requireComma = true;
        }
        List<FieldInfo> fieldsWithExtras = entity.getFieldsWithExtras();
        for (FieldInfo field : fieldsWithExtras) {
            if (field.isManually()) {
                continue;
            } else if (field.getAnnotation(DeleteUser.class) != null) {
                if (requireComma) {
                    result.append(",\n");
                }
                appendInParameter(result, field);
                requireComma = true;
            }
        }
        for (FieldInfo field : fieldsWithExtras) {
            if (field.isManually()) {
                continue;
            } else if (field.getAnnotation(Version.class) != null) {
                if (requireComma) {
                    result.append(",\n");
                }
                appendInParameter(result, field);
                requireComma = true;
            }
        }

        appendEndQuery(result);
        
        String finalQuery = result.toString();
        finalQuery = finalizeQuery(finalQuery, entity);
        return finalQuery.split("\n");
    }
    //</editor-fold>

    
    @Override
    public String[] getSelectManyQuery(OperationInfo operation) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String[] getSelectOneQuery(OperationInfo operation) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String[] getSelectPageCountQuery(OperationInfo operation) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String[] getSelectPageQuery(OperationInfo operation) {
        throw new UnsupportedOperationException("Not supported yet.");
    }


    //<editor-fold defaultstate="collapsed" desc="Custom operations queries">
    @Override
    public String[] getCustomInsertQuery(OperationInfo operation) {
        EntityInfo entity = operation.getEntity().getCombined();
        StringBuilder result = new StringBuilder();
        appendStartCustomInsertQuery(result, operation);

        boolean requireComma = false;
        List<FieldInfo> idFields = entity.getIdFields();
        for (FieldInfo field : idFields) {
            if (field.getAnnotation(InsertDate.class) == null && 
                    omitIdOnInsert(entity, field)) {
                continue;
            }
            if (requireComma) {
                result.append(", ");
            }
            appendOutParameter(result, field);
            requireComma = true;
        }

        List<FieldInfo> fields = operation.getFields();
        for (FieldInfo field : fields) {
            if (omitOnInsert(field)) {
                continue;
            }
            if (requireComma) {
                result.append(", ");
            }
            appendInParameter(result, field);
            requireComma = true;
        }
        List<FieldInfo> fieldsWithExtras = operation.getFieldsWithExtras();
        for (FieldInfo field : fieldsWithExtras) {
            if (field.isManually()) {
                continue;
            } else if (field.getAnnotation(InsertUser.class) != null) {
                if (requireComma) {
                    result.append(",\n");
                }
                appendInParameter(result, field);
                requireComma = true;
            }
        }

        appendEndQuery(result);
        
        String finalQuery = result.toString();
        finalQuery = finalizeQuery(finalQuery, operation);
        return finalQuery.split("\n");
    }

    @Override
    public String[] getCustomUpdateQuery(OperationInfo operation) {
        StringBuilder result = new StringBuilder();
        appendStartCustomUpdateQuery(result, operation);

        boolean requireComma = false;
        List<FieldInfo> fields = operation.getFields();
        for (FieldInfo field : fields) {
            if (omitOnUpdate(field)) {
                continue;
            }
            if (requireComma) {
                result.append(", ");
            }
            appendInParameter(result, field);
            requireComma = true;
        }
        List<FieldInfo> fieldsWithExtras = operation.getFieldsWithExtras();
        for (FieldInfo field : fieldsWithExtras) {
            if (field.isManually()) {
                continue;
            } else if (field.getAnnotation(UpdateUser.class) != null) {
                if (requireComma) {
                    result.append(",\n");
                }
                appendInParameter(result, field);
                requireComma = true;
            }
        }

        appendEndQuery(result);
        
        String finalQuery = result.toString();
        finalQuery = finalizeQuery(finalQuery, operation);
        return finalQuery.split("\n");
    }

    @Override
    public String[] getCustomDeleteQuery(OperationInfo operation) {
        StringBuilder result = new StringBuilder();
        appendStartCustomDeleteQuery(result, operation);

        boolean requireComma = false;
        List<FieldInfo> fields = operation.getFields();
        for (FieldInfo field : fields) {
            if (omitOnDelete(field)) {
                continue;
            }
            if (requireComma) {
                result.append(", ");
            }
            appendInParameter(result, field);
            requireComma = true;
        }
        List<FieldInfo> fieldsWithExtras = operation.getFieldsWithExtras();
        for (FieldInfo field : fieldsWithExtras) {
            if (field.isManually()) {
                continue;
            } else if (field.getAnnotation(DeleteUser.class) != null) {
                if (requireComma) {
                    result.append(",\n");
                }
                appendInParameter(result, field);
                requireComma = true;
            }
        }

        appendEndQuery(result);
        
        String finalQuery = result.toString();
        finalQuery = finalizeQuery(finalQuery, operation);
        return finalQuery.split("\n");
    }
    //</editor-fold>

    
}
