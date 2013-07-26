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

import javax.annotation.processing.ProcessingEnvironment;
import org.uaithne.annotations.Comparator;
import org.uaithne.annotations.DeleteDate;
import org.uaithne.annotations.DeleteUser;
import org.uaithne.annotations.IgnoreLogicalDeletion;
import org.uaithne.annotations.InsertDate;
import org.uaithne.annotations.InsertUser;
import org.uaithne.annotations.MappedName;
import org.uaithne.annotations.SelectOne;
import org.uaithne.annotations.UpdateDate;
import org.uaithne.annotations.UpdateUser;
import org.uaithne.annotations.UseComparator;
import org.uaithne.annotations.UseCustomComparator;
import org.uaithne.annotations.Version;
import org.uaithne.generator.commons.EntityInfo;
import org.uaithne.generator.commons.FieldInfo;
import org.uaithne.generator.commons.OperationInfo;
import org.uaithne.generator.commons.OperationKind;

public abstract class AbstractSqlGenerator implements SqlGenerator {
    
    protected ProcessingEnvironment processingEnv;

    @Override
    public ProcessingEnvironment getProcessingEnv() {
        return processingEnv;
    }

    @Override
    public void setProcessingEnv(ProcessingEnvironment processingEnv) {
        this.processingEnv = processingEnv;
    }
    
    //<editor-fold defaultstate="collapsed" desc="Annotated info">
    public Comparator getComparator(FieldInfo field) {
        UseComparator uc = field.getAnnotation(UseComparator.class);
        Comparator comparator = null;

        if (uc != null) {
            comparator = uc.value();
        }

        if (comparator == null) {
            if (field.getDataType().isList()) {
                comparator = Comparator.IN;
            } else {
                comparator = Comparator.EQUAL;
            }
        }
        return comparator;
    }

    public String getConditionTemplate(FieldInfo field, String comparatorTemplate) {
        UseCustomComparator ucc = field.getAnnotation(UseCustomComparator.class);
        String template;
        if (ucc != null) {
            template = ucc.value();
        } else {
            template = comparatorTemplate;
        }
        return template;
    }
    
    public boolean limitToOneResult(OperationInfo operation) {
        if (operation.getOperationKind() != OperationKind.SELECT_ONE) {
            return false;
        }
        SelectOne selectOne = operation.getAnnotation(SelectOne.class);
        if (selectOne != null) {
            return selectOne.limit();
        }
        return false;
    }
    
    
    public String[] getMappedName(EntityInfo entity) {
        MappedName mappedName = entity.getAnnotation(MappedName.class);
        if (mappedName == null) {
            return entity.getDataType().getSimpleNameWithoutGenerics().split("\n");
        } else {
            return joinln(mappedName.value()).split("\n");
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Utils">
    public void appendToQuery(StringBuilder query, String[] array, String prefix) {
        if (!hasQueryValue(array)) {
            return;
        }
        for (int i = 0; i < array.length - 1; i++) {
            String s = array[i];
            query.append(prefix).append(s).append("\n");
        }
        query.append(prefix);
        String s = array[array.length - 1];
        query.append(s);
        query.append(" ");
    }

    public void appendToQueryln(StringBuilder query, String[] array, String prefix) {
        if (!hasQueryValue(array)) {
            return;
        }
        if (query.length() > 0) {
            if (query.charAt(query.length() - 1) != '\n') {
                query.append("\n");
            }
        }
        for (int i = 0; i < array.length - 1; i++) {
            String s = array[i];
            query.append(prefix).append(s).append("\n");
        }
        query.append(prefix);
        String s = array[array.length - 1];
        query.append(s);
        query.append(" ");
    }

    public String joinln(String[] strings) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < strings.length - 1; i++) {
            sb.append(strings[i]);
            sb.append("\n");
        }
        sb.append(strings[strings.length - 1]);
        return sb.toString();
    }

    public String joinsp(String[] strings) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < strings.length - 1; i++) {
            sb.append(strings[i]);
            sb.append("\n");
        }
        sb.append(strings[strings.length - 1]);
        return sb.toString();
    }

    public boolean hasQueryValue(String[] array) {
        if (array == null || array.length <= 0) {
            return false;
        }
        if (array.length == 1) {
            String s = array[0];
            if (s == null || s.isEmpty()) {
                return false;
            }
        }
        return true;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Entity rules">
    public boolean useLogicalDeletion(EntityInfo entity) {
        IgnoreLogicalDeletion ignoreLogicalDeletion = entity.getAnnotation(IgnoreLogicalDeletion.class);
        if (ignoreLogicalDeletion != null) {
            return false;
        }
        return entity.isLogicalDeletion();
    }

    public boolean useLogicalDeletion(OperationInfo operation) {
        IgnoreLogicalDeletion ignoreLogicalDeletion = operation.getAnnotation(IgnoreLogicalDeletion.class);
        if (ignoreLogicalDeletion != null) {
            return false;
        }
        EntityInfo entity = operation.getEntity();
        if (entity != null) {
            return useLogicalDeletion(entity.getCombined());
        }
        return false;
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Managment of the extra fields">
    public boolean omitField(FieldInfo field) {
        if (field.isManually()) {
            return true;
        }
        if (field.getAnnotation(InsertDate.class) != null) {
            return true;
        }
        if (field.getAnnotation(InsertUser.class) != null) {
            return true;
        }
        if (field.getAnnotation(UpdateDate.class) != null) {
            return true;
        }
        if (field.getAnnotation(UpdateUser.class) != null) {
            return true;
        }
        if (field.getAnnotation(DeleteDate.class) != null) {
            return true;
        }
        if (field.getAnnotation(DeleteUser.class) != null) {
            return true;
        }
        if (field.getAnnotation(Version.class) != null) {
            return true;
        }
        if (field.isDeletionMark()) {
            return true;
        }
        return false;
    }
    
    public boolean extraIncludeOnSelect(FieldInfo field) {
        if (field.isManually()) {
            return false;
        }
        if (field.getAnnotation(InsertDate.class) != null) {
            return true;
        }
        if (field.getAnnotation(InsertUser.class) != null) {
            return true;
        }
        if (field.getAnnotation(UpdateDate.class) != null) {
            return true;
        }
        if (field.getAnnotation(UpdateUser.class) != null) {
            return true;
        }
        if (field.getAnnotation(DeleteDate.class) != null) {
            return true;
        }
        if (field.getAnnotation(DeleteUser.class) != null) {
            return true;
        }
        if (field.getAnnotation(Version.class) != null) {
            return true;
        }
        if (field.isDeletionMark()) {
            return true;
        }
        return false;
    }

    public boolean omitOnSelect(FieldInfo field) {
        return omitField(field);
    }

    public boolean omitOnInsert(FieldInfo field) {
        return omitField(field);
    }

    public boolean omitOnUpdate(FieldInfo field) {
        return omitField(field);
    }

    public boolean omitOnDelete(FieldInfo field) {
        return omitField(field);
    }
    //</editor-fold>
    
}
