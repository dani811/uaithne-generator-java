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
import java.util.List;
import javax.tools.Diagnostic;
import org.uaithne.generator.commons.EntityInfo;
import org.uaithne.generator.commons.FieldInfo;
import org.uaithne.generator.commons.OperationInfo;

public abstract class SqlCallGenerator extends SqlGenerator {
    
    //<editor-fold defaultstate="collapsed" desc="Version managment">
    public abstract boolean handleVersionFieldOnInsert();
    public abstract boolean handleVersionFieldOnUpdate();
    public abstract boolean handleVersionFieldOnDelete();
    public void appendVersionFieldOnInsert(StringBuilder result, EntityInfo entity, OperationInfo operation, FieldInfo field) {
        appendOutParameter(result, field);
    }
    public void appendVersionFieldOnUpdate(StringBuilder result, EntityInfo entity, OperationInfo operation, FieldInfo field) {
        appendOutParameter(result, field);
    }
    public void appendVersionFieldOnDelete(StringBuilder result, EntityInfo entity, OperationInfo operation, FieldInfo field) {
        appendOutParameter(result, field);
    }
    //</editor-fold>
    
    public abstract boolean includeIdOnInsert();
    public abstract void appendInParameter(StringBuilder query, FieldInfo field);
    public abstract void appendOutParameter(StringBuilder query, FieldInfo field);
    public abstract void appendInOutParameter(StringBuilder query, FieldInfo field);
    public abstract void appendInApplicationParameter(StringBuilder query, FieldInfo field);

//    TODO: add support for select operations as a procedure call?
//    public abstract void appendStartSelectManyQuery(StringBuilder query, OperationInfo operation);
//    public abstract void appendStartSelectOneQuery(StringBuilder query, OperationInfo operation);
//    public abstract void appendStartSelectPageCountQuery(StringBuilder query, OperationInfo operation);
//    public abstract void appendStartSelectPageQuery(StringBuilder query, OperationInfo operation);
//    public abstract void appendStartEntitySelectByIdQuery(StringBuilder query, EntityInfo entity);
    
    public abstract void appendStartEntityDeleteByIdQuery(StringBuilder query, EntityInfo entity, OperationInfo operation);
    public abstract void appendStartEntityInsertQuery(StringBuilder query, EntityInfo entity, OperationInfo operation);
    public abstract void appendStartEntityLastInsertedIdQuery(StringBuilder query, EntityInfo entity, OperationInfo operation);
    public abstract void appendStartEntityMergeQuery(StringBuilder query, EntityInfo entity, OperationInfo operation);
    public abstract void appendStartEntityUpdateQuery(StringBuilder query, EntityInfo entity, OperationInfo operation);
    public abstract void appendStartCustomDeleteQuery(StringBuilder query, OperationInfo operation);
    public abstract void appendStartCustomInsertQuery(StringBuilder query, OperationInfo operation);
    public abstract void appendStartCustomUpdateQuery(StringBuilder query, OperationInfo operation);
    
    public abstract void appendEndEntityDeleteByIdQuery(StringBuilder query, EntityInfo entity, OperationInfo operation, boolean requireSeparator);
    public abstract void appendEndEntityInsertQuery(StringBuilder query, EntityInfo entity, OperationInfo operation, boolean requireSeparator);
    public abstract void appendEndEntityLastInsertedIdQuery(StringBuilder query, EntityInfo entity, OperationInfo operation, boolean requireSeparator);
    public abstract void appendEndEntityMergeQuery(StringBuilder query, EntityInfo entity, OperationInfo operation, boolean requireSeparator);
    public abstract void appendEndEntityUpdateQuery(StringBuilder query, EntityInfo entity, OperationInfo operation, boolean requireSeparator);
    public abstract void appendEndCustomDeleteQuery(StringBuilder query, OperationInfo operation, boolean requireSeparator);
    public abstract void appendEndCustomInsertQuery(StringBuilder query, OperationInfo operation, boolean requireSeparator);
    public abstract void appendEndCustomUpdateQuery(StringBuilder query, OperationInfo operation, boolean requireSeparator);
    
    public abstract void appendFirstFieldSeparator(StringBuilder query);
    public abstract void appendFieldSeparator(StringBuilder query);
    
    public String finalizeEntityQuery(String query, EntityInfo entity, OperationInfo operation) {
        return query;
    }
    
    public String finalizeQuery(String query, OperationInfo operation) {
        return query;
    }

    //<editor-fold defaultstate="collapsed" desc="Entity queries">
    @Override
    public String[] getEntitySelectByIdQuery(EntityInfo entity, OperationInfo operation) {
        getProcessingEnv().getMessager().printMessage(Diagnostic.Kind.ERROR, "Unsuported sql procedure call generation for select entity by id", entity.getElement());
        return null;
    }

    @Override
    public String[] getEntityInsertQuery(EntityInfo entity, OperationInfo operation) {
        StringBuilder result = new StringBuilder();
        appendStartEntityInsertQuery(result, entity, operation);

        boolean requireComma = false;
        for (FieldInfo field : entity.getFields()) {
            if (field.isManually()) {
                continue;
            } else if (field.isInsertDateMark()) {
                continue;
            } else if (field.isIdentifier()) {
                if (!includeIdOnInsert()) {
                    continue;
                }
                if (requireComma) {
                    appendFieldSeparator(result);
                } else {
                    appendFirstFieldSeparator(result);
                }
                appendOutParameter(result, field);
                requireComma = true;
            } else if (field.isInsertUserMark()) {
                if (requireComma) {
                    appendFieldSeparator(result);
                } else {
                    appendFirstFieldSeparator(result);
                }
                appendInParameter(result, field);
                requireComma = true;
            } else if (field.isVersionMark()) {
                if (!handleVersionFieldOnInsert()) {
                    continue;
                }
                if (requireComma) {
                    appendFieldSeparator(result);
                } else {
                    appendFirstFieldSeparator(result);
                }
                appendVersionFieldOnInsert(result, entity, operation, field);
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
                    appendFieldSeparator(result);
                } else {
                    appendFirstFieldSeparator(result);
                }
                appendInParameter(result, field);
                requireComma = true;
            }
        }
        
        ArrayList<FieldInfo> applicationParameters = getConfiguration().getUsedApplicationParameters();
        if (applicationParameters != null) {
            for (FieldInfo field : applicationParameters) {
                if (requireComma) {
                    appendFieldSeparator(result);
                } else {
                    appendFirstFieldSeparator(result);
                }
                appendInApplicationParameter(result, field);
                requireComma = true;
            }
        }

        appendEndEntityInsertQuery(result, entity, operation, requireComma);
        
        String finalQuery = result.toString();
        finalQuery = finalizeEntityQuery(finalQuery, entity, operation);
        return finalQuery.split("\n");
    }

    @Override
    public String[] getEntityLastInsertedIdQuery(EntityInfo entity, OperationInfo operation, boolean excludeSequenceQuery) {
        StringBuilder result = new StringBuilder();
        appendStartEntityLastInsertedIdQuery(result, entity, operation);
        appendEndEntityLastInsertedIdQuery(result, entity, operation, false);
        
        String finalQuery = result.toString();
        finalQuery = finalizeEntityQuery(finalQuery, entity, operation);
        return finalQuery.split("\n");
    }

    @Override
    public String[] getEntityUpdateQuery(EntityInfo entity, OperationInfo operation) {
        StringBuilder result = new StringBuilder();
        appendStartEntityUpdateQuery(result, entity, operation);

        boolean requireComma = false;
        for (FieldInfo field : entity.getFields()) {
            if (field.isManually()) {
                continue;
            } else if (field.isIdentifier()) {
                if (requireComma) {
                    appendFieldSeparator(result);
                } else {
                    appendFirstFieldSeparator(result);
                }
                appendInParameter(result, field);
                requireComma = true;
            } else if (field.isUpdateUserMark()) {
                if (requireComma) {
                    appendFieldSeparator(result);
                } else {
                    appendFirstFieldSeparator(result);
                }
                appendInParameter(result, field);
                requireComma = true;
            } else if (field.isVersionMark()) {
                if (!handleVersionFieldOnUpdate()) {
                    continue;
                }
                if (requireComma) {
                    appendFieldSeparator(result);
                } else {
                    appendFirstFieldSeparator(result);
                }
                appendVersionFieldOnUpdate(result, entity, operation, field);
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
                    appendFieldSeparator(result);
                } else {
                    appendFirstFieldSeparator(result);
                }
                appendInParameter(result, field);
                requireComma = true;
            }
        }
        
        ArrayList<FieldInfo> applicationParameters = getConfiguration().getUsedApplicationParameters();
        if (applicationParameters != null) {
            for (FieldInfo field : applicationParameters) {
                if (requireComma) {
                    appendFieldSeparator(result);
                } else {
                    appendFirstFieldSeparator(result);
                }
                appendInApplicationParameter(result, field);
                requireComma = true;
            }
        }

        appendEndEntityUpdateQuery(result, entity, operation, requireComma);
        
        String finalQuery = result.toString();
        finalQuery = finalizeEntityQuery(finalQuery, entity, operation);
        return finalQuery.split("\n");
    }

    @Override
    public String[] getEntityMergeQuery(EntityInfo entity, OperationInfo operation) {
        StringBuilder result = new StringBuilder();
        appendStartEntityMergeQuery(result, entity, operation);

        boolean requireComma = false;
        for (FieldInfo field : entity.getFields()) {
            if (field.isManually()) {
                continue;
            } else if (field.isIdentifier()) {
                if (requireComma) {
                    appendFieldSeparator(result);
                } else {
                    appendFirstFieldSeparator(result);
                }
                appendInParameter(result, field);
                requireComma = true;
            } else if (field.isUpdateUserMark()) {
                if (requireComma) {
                    appendFieldSeparator(result);
                } else {
                    appendFirstFieldSeparator(result);
                }
                appendInParameter(result, field);
                requireComma = true;
            } else if (field.isVersionMark()) {
                if (!handleVersionFieldOnUpdate()) {
                    continue;
                }
                if (requireComma) {
                    appendFieldSeparator(result);
                } else {
                    appendFirstFieldSeparator(result);
                }
                appendVersionFieldOnUpdate(result, entity, operation, field);
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
                    appendFieldSeparator(result);
                } else {
                    appendFirstFieldSeparator(result);
                }
                appendInParameter(result, field);
                requireComma = true;
            }
        }
        
        ArrayList<FieldInfo> applicationParameters = getConfiguration().getUsedApplicationParameters();
        if (applicationParameters != null) {
            for (FieldInfo field : applicationParameters) {
                if (requireComma) {
                    appendFieldSeparator(result);
                } else {
                    appendFirstFieldSeparator(result);
                }
                appendInApplicationParameter(result, field);
                requireComma = true;
            }
        }

        appendEndEntityMergeQuery(result, entity, operation, requireComma);
        
        String finalQuery = result.toString();
        finalQuery = finalizeEntityQuery(finalQuery, entity, operation);
        return finalQuery.split("\n");
    }

    @Override
    public String[] getEntityDeleteByIdQuery(EntityInfo entity, OperationInfo operation) {
        StringBuilder result = new StringBuilder();
        appendStartEntityDeleteByIdQuery(result, entity, operation);

        boolean requireComma = false;
        for (FieldInfo field : entity.getFields()) {
            if (field.isManually()) {
                continue;
            } else if (field.isIdentifier()) {
                if (requireComma) {
                    appendFieldSeparator(result);
                } else {
                    appendFirstFieldSeparator(result);
                }
                appendInParameter(result, field);
                requireComma = true;
            } else if (field.isDeleteUserMark()) {
                // TODO: deleteUser
                continue;
            } else if (field.isVersionMark()) {
                if (!handleVersionFieldOnDelete()) {
                    continue;
                }
                if (requireComma) {
                    appendFieldSeparator(result);
                } else {
                    appendFirstFieldSeparator(result);
                }
                appendVersionFieldOnDelete(result, entity, operation, field);
                requireComma = true;
            }
        }
        
        ArrayList<FieldInfo> applicationParameters = getConfiguration().getUsedApplicationParameters();
        if (applicationParameters != null) {
            for (FieldInfo field : applicationParameters) {
                if (requireComma) {
                    appendFieldSeparator(result);
                } else {
                    appendFirstFieldSeparator(result);
                }
                appendInApplicationParameter(result, field);
                requireComma = true;
            }
        }
        
        appendEndEntityDeleteByIdQuery(result, entity, operation, requireComma);
        
        String finalQuery = result.toString();
        finalQuery = finalizeEntityQuery(finalQuery, entity, operation);
        return finalQuery.split("\n");
    }
    //</editor-fold>

    
    @Override
    public String[] getSelectManyQuery(OperationInfo operation) {
        getProcessingEnv().getMessager().printMessage(Diagnostic.Kind.ERROR, "Unsuported sql procedure call generation for select many", operation.getElement());
        return null;
    }

    @Override
    public String[] getSelectOneQuery(OperationInfo operation) {
        getProcessingEnv().getMessager().printMessage(Diagnostic.Kind.ERROR, "Unsuported sql procedure call generation for select one", operation.getElement());
        return null;
    }

    @Override
    public String[] getSelectCountQuery(OperationInfo operation) {
        getProcessingEnv().getMessager().printMessage(Diagnostic.Kind.ERROR, "Unsuported sql procedure call generation for select count", operation.getElement());
        return null;
    }

    @Override
    public String[] getSelectPageCountQuery(OperationInfo operation) {
        getProcessingEnv().getMessager().printMessage(Diagnostic.Kind.ERROR, "Unsuported sql procedure call generation for select page count", operation.getElement());
        return null;
    }

    @Override
    public String[] getSelectPageQuery(OperationInfo operation) {
        getProcessingEnv().getMessager().printMessage(Diagnostic.Kind.ERROR, "Unsuported sql procedure call generation for select page", operation.getElement());
        return null;
    }


    //<editor-fold defaultstate="collapsed" desc="Custom operations queries">
    @Override
    public String[] getCustomInsertQuery(OperationInfo operation) {
        EntityInfo entity = operation.getEntity().getCombined();
        StringBuilder result = new StringBuilder();
        appendStartCustomInsertQuery(result, operation);

        boolean requireComma = false;
        if (includeIdOnInsert()) {
            for (FieldInfo field : entity.getFields()) {
                if (field.isManually()) {
                    continue;
                }
                if (!field.isIdentifier()) {
                    continue;
                }
                if (requireComma) {
                    appendFieldSeparator(result);
                } else {
                    appendFirstFieldSeparator(result);
                }
                appendOutParameter(result, field);
                requireComma = true;
            }
        }
        for (FieldInfo field : operation.getFields()) {
            if (field.isManually()) {
                continue;
            }
            if (requireComma) {
                appendFieldSeparator(result);
            } else {
                appendFirstFieldSeparator(result);
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
                    appendFieldSeparator(result);
                } else {
                    appendFirstFieldSeparator(result);
                }
                appendVersionFieldOnInsert(result, entity, operation, field);
                requireComma = true;
            }
        }
        
        ArrayList<FieldInfo> applicationParameters = getConfiguration().getUsedApplicationParameters();
        if (applicationParameters != null) {
            for (FieldInfo field : applicationParameters) {
                if (requireComma) {
                    appendFieldSeparator(result);
                } else {
                    appendFirstFieldSeparator(result);
                }
                appendInApplicationParameter(result, field);
                requireComma = true;
            }
        }

        appendEndCustomInsertQuery(result, operation, requireComma);
        
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
                appendFieldSeparator(result);
            } else {
                appendFirstFieldSeparator(result);
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
                    appendFieldSeparator(result);
                } else {
                    appendFirstFieldSeparator(result);
                }
                appendVersionFieldOnUpdate(result, entity, operation, field);
                requireComma = true;
            }
        }
        
        ArrayList<FieldInfo> applicationParameters = getConfiguration().getUsedApplicationParameters();
        if (applicationParameters != null) {
            for (FieldInfo field : applicationParameters) {
                if (requireComma) {
                    appendFieldSeparator(result);
                } else {
                    appendFirstFieldSeparator(result);
                }
                appendInApplicationParameter(result, field);
                requireComma = true;
            }
        }

        appendEndCustomUpdateQuery(result, operation, requireComma);
        
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
                appendFieldSeparator(result);
            } else {
                appendFirstFieldSeparator(result);
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
                        appendFieldSeparator(result);
                    } else {
                        appendFirstFieldSeparator(result);
                    }
                    appendVersionFieldOnDelete(result, entity, operation, field);
                    requireComma = true;
                }
            }
        }
        
        ArrayList<FieldInfo> applicationParameters = getConfiguration().getUsedApplicationParameters();
        if (applicationParameters != null) {
            for (FieldInfo field : applicationParameters) {
                if (requireComma) {
                    appendFieldSeparator(result);
                } else {
                    appendFirstFieldSeparator(result);
                }
                appendInApplicationParameter(result, field);
                requireComma = true;
            }
        }

        appendEndCustomDeleteQuery(result, operation, requireComma);
        
        String finalQuery = result.toString();
        finalQuery = finalizeQuery(finalQuery, operation);
        return finalQuery.split("\n");
    }
    //</editor-fold>

}
