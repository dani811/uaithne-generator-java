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
import org.uaithne.generator.commons.EntityInfo;
import org.uaithne.generator.commons.FieldInfo;
import org.uaithne.generator.commons.OperationInfo;

public abstract class AbstractSqlCallGenerator extends AbstractSqlGenerator {
    
    //<editor-fold defaultstate="collapsed" desc="Version managment">
    public abstract boolean handleVersionFieldOnInsert();
    public abstract boolean handleVersionFieldOnUpdate();
    public abstract boolean handleVersionFieldOnDelete();
    public void appendVersionFieldOnInsert(StringBuilder result, EntityInfo entity, FieldInfo field) {
        appendOutParameter(result, field);
    }
    public void appendVersionFieldOnUpdate(StringBuilder result, EntityInfo entity, FieldInfo field) {
        appendOutParameter(result, field);
    }
    public void appendVersionFieldOnDelete(StringBuilder result, EntityInfo entity, FieldInfo field) {
        appendOutParameter(result, field);
    }
    //</editor-fold>
    
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
        for (FieldInfo field : entity.getFields()) {
            if (field.isManually()) {
                continue;
            } else if (field.isInsertDateMark()) {
                continue;
            } else if (field.isIdentifier()) {
                if (requireComma) {
                    result.append(", ");
                }
                appendOutParameter(result, field);
                requireComma = true;
            } else if (field.isInsertUserMark()) {
                if (requireComma) {
                    result.append(", ");
                }
                appendInParameter(result, field);
                requireComma = true;
            } else if (field.isVersionMark()) {
                if (!handleVersionFieldOnInsert()) {
                    continue;
                }
                if (requireComma) {
                    result.append(", ");
                }
                appendVersionFieldOnInsert(result, entity, field);
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
                continue;
            } else {
                if (requireComma) {
                    result.append(", ");
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
        for (FieldInfo field : entity.getFields()) {
            if (field.isManually()) {
                continue;
            } else if (field.isIdentifier()) {
                if (requireComma) {
                    result.append(", ");
                }
                appendOutParameter(result, field);
                requireComma = true;
            } else if (field.isUpdateUserMark()) {
                if (requireComma) {
                    result.append(", ");
                }
                appendInParameter(result, field);
                requireComma = true;
            } else if (field.isVersionMark()) {
                if (!handleVersionFieldOnUpdate()) {
                    continue;
                }
                if (requireComma) {
                    result.append(", ");
                }
                appendVersionFieldOnUpdate(result, entity, field);
                requireComma = true;
            } else if (field.isInsertDateMark()) {
                continue;
            } else if (field.isInsertUserMark()) {
                continue;
            } else if (field.isUpdateDateMark()) {
                continue;
            } else if (field.isDeleteDateMark()) {
                continue;
            } else if (field.isDeleteUserMark()) {
                continue;
            } else if (field.isDeletionMark()) {
                continue;
            } else {
                if (requireComma) {
                    result.append(", ");
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

    @Override
    public String[] getEntityMergeQuery(EntityInfo entity) {
        StringBuilder result = new StringBuilder();
        appendStartEntityMergeQuery(result, entity);

        boolean requireComma = false;
        for (FieldInfo field : entity.getFields()) {
            if (field.isManually()) {
                continue;
            } else if (field.isIdentifier()) {
                if (requireComma) {
                    result.append(", ");
                }
                appendOutParameter(result, field);
                requireComma = true;
            } else if (field.isUpdateUserMark()) {
                if (requireComma) {
                    result.append(", ");
                }
                appendInParameter(result, field);
                requireComma = true;
            } else if (field.isVersionMark()) {
                if (!handleVersionFieldOnUpdate()) {
                    continue;
                }
                if (requireComma) {
                    result.append(", ");
                }
                appendVersionFieldOnUpdate(result, entity, field);
                requireComma = true;
            } else if (field.isInsertDateMark()) {
                continue;
            } else if (field.isInsertUserMark()) {
                continue;
            } else if (field.isUpdateDateMark()) {
                continue;
            } else if (field.isDeleteDateMark()) {
                continue;
            } else if (field.isDeleteUserMark()) {
                continue;
            } else if (field.isDeletionMark()) {
                continue;
            } else {
                if (requireComma) {
                    result.append(", ");
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

    @Override
    public String[] getEntityDeleteByIdQuery(EntityInfo entity) {
        StringBuilder result = new StringBuilder();
        appendStartEntityDeleteByIdQuery(result, entity);

        boolean requireComma = false;
        for (FieldInfo field : entity.getFields()) {
            if (field.isManually()) {
                continue;
            } else if (field.isIdentifier()) {
                if (requireComma) {
                    result.append(", ");
                }
                appendOutParameter(result, field);
                requireComma = true;
            } else if (field.isDeleteUserMark()) {
                if (requireComma) {
                    result.append(", ");
                }
                appendInParameter(result, field);
                requireComma = true;
            } else if (field.isVersionMark()) {
                if (!handleVersionFieldOnDelete()) {
                    continue;
                }
                if (requireComma) {
                    result.append(", ");
                }
                appendVersionFieldOnDelete(result, entity, field);
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
        for (FieldInfo field : entity.getIdFields()) {
            if (field.isManually()) {
                continue;
            }
            if (requireComma) {
                result.append(", ");
            }
            appendOutParameter(result, field);
            requireComma = true;
        }
        for (FieldInfo field : operation.getFields()) {
            if (field.isManually()) {
                continue;
            }
            if (requireComma) {
                result.append(", ");
            }
            appendInParameter(result, field);
            requireComma = true;
        }
        for (FieldInfo field : entity.getFields()) {
            if (field.isManually()) {
                continue;
            } else if (field.isIdentifier()) {
                continue;
            } else if (field.isVersionMark()) {
                if (!handleVersionFieldOnInsert()) {
                    continue;
                }
                if (requireComma) {
                    result.append(", ");
                }
                appendVersionFieldOnInsert(result, entity, field);
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
        EntityInfo entity = operation.getEntity().getCombined();
        StringBuilder result = new StringBuilder();
        appendStartCustomUpdateQuery(result, operation);

        boolean requireComma = false;
        for (FieldInfo field : operation.getFields()) {
            if (field.isManually()) {
                continue;
            }
            if (requireComma) {
                result.append(", ");
            }
            appendInParameter(result, field);
            requireComma = true;
        }
        for (FieldInfo field : entity.getFields()) {
            if (field.isManually()) {
                continue;
            } else if (field.isVersionMark()) {
                if (!handleVersionFieldOnUpdate()) {
                    continue;
                }
                if (requireComma) {
                    result.append(", ");
                }
                appendVersionFieldOnUpdate(result, entity, field);
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
        EntityInfo entity = operation.getEntity().getCombined();
        StringBuilder result = new StringBuilder();
        appendStartCustomDeleteQuery(result, operation);

        boolean requireComma = false;
        List<FieldInfo> fields = operation.getFields();
        for (FieldInfo field : fields) {
            if (field.isManually()) {
                continue;
            }
            if (requireComma) {
                result.append(", ");
            }
            appendInParameter(result, field);
            requireComma = true;
        }
        if (handleVersionFieldOnDelete()) {
            for (FieldInfo field : entity.getFields()) {
                if (field.isManually()) {
                    continue;
                } else if (field.isVersionMark()) {
                    if (requireComma) {
                        result.append(", ");
                    }
                    appendVersionFieldOnDelete(result, entity, field);
                    requireComma = true;
                }
            }
        }

        appendEndQuery(result);
        
        String finalQuery = result.toString();
        finalQuery = finalizeQuery(finalQuery, operation);
        return finalQuery.split("\n");
    }
    //</editor-fold>

}
