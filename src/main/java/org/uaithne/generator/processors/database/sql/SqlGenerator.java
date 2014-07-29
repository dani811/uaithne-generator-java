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

import org.uaithne.generator.processors.database.QueryGenerator;
import javax.annotation.processing.ProcessingEnvironment;
import org.uaithne.annotations.Comparators;
import org.uaithne.annotations.MappedName;
import org.uaithne.annotations.Comparator;
import org.uaithne.annotations.CustomComparator;
import org.uaithne.generator.commons.EntityInfo;
import org.uaithne.generator.commons.FieldInfo;
import org.uaithne.generator.processors.database.QueryGeneratorConfiguration;

public abstract class SqlGenerator implements QueryGenerator {
    
    private QueryGeneratorConfiguration configuration;

    @Override
    public QueryGeneratorConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    public void setConfiguration(QueryGeneratorConfiguration configuration) {
        this.configuration = configuration;
    }
    
    public ProcessingEnvironment getProcessingEnv() {
        if (configuration == null) {
            return null;
        }
        return configuration.getProcessingEnv();
    }
    
    @Override
    public boolean useAliasInOrderByTranslation() {
        return false;
    }
    
    @Override
    public void begin() {
    }

    @Override
    public void end() {
    }
    
    //<editor-fold defaultstate="collapsed" desc="Annotated info">
    public Comparators getComparator(FieldInfo field) {
        Comparator uc = field.getAnnotation(Comparator.class);
        Comparators comparator = null;

        if (uc != null) {
            comparator = uc.value();
        }

        if (comparator == null) {
            if (field.getDataType().isList()) {
                comparator = Comparators.IN;
            } else {
                comparator = Comparators.EQUAL;
            }
        }
        return comparator;
    }

    public String getConditionTemplate(FieldInfo field, String comparatorTemplate) {
        CustomComparator ucc = field.getAnnotation(CustomComparator.class);
        String template;
        if (ucc != null) {
            template = ucc.value();
        } else {
            template = comparatorTemplate;
        }
        return template;
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
        if (strings == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < strings.length - 1; i++) {
            sb.append(strings[i]);
            sb.append("\n");
        }
        sb.append(strings[strings.length - 1]);
        return sb.toString();
    }

    public String joinsp(String[] strings) {
        if (strings == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < strings.length - 1; i++) {
            sb.append(strings[i]);
            sb.append(" ");
        }
        sb.append(strings[strings.length - 1]);
        return sb.toString();
    }

    public boolean hasQueryValue(String[] array) {
        if (array == null || array.length <= 0) {
            return false;
        }
        
        for (String s : array) {
            if (s != null && !s.isEmpty()) {
                return true;
            }
        }
        return false;
    }
    //</editor-fold>

}
