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

import java.util.HashMap;
import org.uaithne.generator.processors.database.QueryGenerator;
import javax.annotation.processing.ProcessingEnvironment;
import org.uaithne.annotations.Comparators;
import org.uaithne.annotations.Comparator;
import org.uaithne.annotations.CustomComparator;
import org.uaithne.annotations.sql.JdbcType;
import org.uaithne.annotations.sql.JdbcTypes;
import org.uaithne.generator.commons.DataTypeInfo;
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
    
    //<editor-fold defaultstate="collapsed" desc="JDBC Type">
    public JdbcTypes getJdbcType(FieldInfo field) {
        JdbcType ujt = field.getAnnotation(JdbcType.class);
        if (ujt != null) {
            return ujt.value();
        }
        
        DataTypeInfo dataType = field.getDataType();
        String name = dataType.getQualifiedName();
        if (configuration != null) {
            JdbcTypes result = configuration.getCustomJdbcTypeMap().get(name);
            if (result != null) {
                return result;
            }
        }
        if (dataType.isEnum()) {
            return JdbcTypes.INTEGER;
        }
        return jdbcTypeMap.get(name);
    }
    
    private static final HashMap<String, JdbcTypes> jdbcTypeMap;
    static {
        jdbcTypeMap = new HashMap<String, JdbcTypes>(65);
        
        jdbcTypeMap.put("java.lang.String", JdbcTypes.VARCHAR);
        jdbcTypeMap.put("java.util.Date", JdbcTypes.TIMESTAMP);
        jdbcTypeMap.put("java.sql.Date", JdbcTypes.DATE);
        jdbcTypeMap.put("java.sql.Time", JdbcTypes.TIME);
        jdbcTypeMap.put("java.sql.Timestamp", JdbcTypes.TIMESTAMP);
        jdbcTypeMap.put("boolean", JdbcTypes.BOOLEAN);
        jdbcTypeMap.put("java.lang.Boolean", JdbcTypes.BOOLEAN);
        jdbcTypeMap.put("byte", JdbcTypes.TINYINT);
        jdbcTypeMap.put("java.lang.Byte", JdbcTypes.TINYINT);
        jdbcTypeMap.put("short", JdbcTypes.SMALLINT);
        jdbcTypeMap.put("java.lang.Short", JdbcTypes.SMALLINT);
        jdbcTypeMap.put("int", JdbcTypes.INTEGER);
        jdbcTypeMap.put("java.lang.Integer", JdbcTypes.INTEGER);
        jdbcTypeMap.put("long", JdbcTypes.BIGINT);
        jdbcTypeMap.put("java.lang.Long", JdbcTypes.BIGINT);
        jdbcTypeMap.put("float", JdbcTypes.REAL);
        jdbcTypeMap.put("java.lang.Float", JdbcTypes.REAL);
        jdbcTypeMap.put("double", JdbcTypes.DOUBLE);
        jdbcTypeMap.put("java.lang.Double", JdbcTypes.DOUBLE);
        jdbcTypeMap.put("java.math.BigDecimal", JdbcTypes.NUMERIC);
        jdbcTypeMap.put("java.math.BigInteger", JdbcTypes.NUMERIC);
        jdbcTypeMap.put("char", JdbcTypes.CHAR);
        jdbcTypeMap.put("java.lang.Character", JdbcTypes.CHAR);
        
        jdbcTypeMap.put("java.lang.Character[]", JdbcTypes.CHAR);
        jdbcTypeMap.put("java.lang.String[]", JdbcTypes.VARCHAR);
        jdbcTypeMap.put("java.util.Date[]", JdbcTypes.TIMESTAMP);
        jdbcTypeMap.put("java.sql.Date[]", JdbcTypes.DATE);
        jdbcTypeMap.put("java.sql.Time[]", JdbcTypes.TIME);
        jdbcTypeMap.put("java.sql.Timestamp[]", JdbcTypes.TIMESTAMP);
        jdbcTypeMap.put("boolean[]", JdbcTypes.BOOLEAN);
        jdbcTypeMap.put("java.lang.Boolean[]", JdbcTypes.BOOLEAN);
        jdbcTypeMap.put("byte[]", JdbcTypes.TINYINT);
        jdbcTypeMap.put("java.lang.Byte[]", JdbcTypes.TINYINT);
        jdbcTypeMap.put("short[]", JdbcTypes.SMALLINT);
        jdbcTypeMap.put("java.lang.Short[]", JdbcTypes.SMALLINT);
        jdbcTypeMap.put("int[]", JdbcTypes.INTEGER);
        jdbcTypeMap.put("java.lang.Integer[]", JdbcTypes.INTEGER);
        jdbcTypeMap.put("long[]", JdbcTypes.BIGINT);
        jdbcTypeMap.put("java.lang.Long[]", JdbcTypes.BIGINT);
        jdbcTypeMap.put("float[]", JdbcTypes.REAL);
        jdbcTypeMap.put("java.lang.Float[]", JdbcTypes.REAL);
        jdbcTypeMap.put("double[]", JdbcTypes.DOUBLE);
        jdbcTypeMap.put("java.lang.Double[]", JdbcTypes.DOUBLE);
        jdbcTypeMap.put("java.math.BigDecimal[]", JdbcTypes.NUMERIC);
        jdbcTypeMap.put("java.math.BigInteger[]", JdbcTypes.NUMERIC);
        jdbcTypeMap.put("char[]", JdbcTypes.CHAR);
        jdbcTypeMap.put("java.lang.Character[]", JdbcTypes.CHAR);
        
        jdbcTypeMap.put("java.util.List<java.lang.String>", JdbcTypes.VARCHAR);
        jdbcTypeMap.put("java.util.List<java.util.Date>", JdbcTypes.TIMESTAMP);
        jdbcTypeMap.put("java.util.List<java.sql.Date>", JdbcTypes.DATE);
        jdbcTypeMap.put("java.util.List<java.sql.Time>", JdbcTypes.TIME);
        jdbcTypeMap.put("java.util.List<java.sql.Timestamp>", JdbcTypes.TIMESTAMP);
        jdbcTypeMap.put("java.util.List<java.lang.Boolean>", JdbcTypes.BOOLEAN);
        jdbcTypeMap.put("java.util.List<java.lang.Byte>", JdbcTypes.TINYINT);
        jdbcTypeMap.put("java.util.List<java.lang.Short>", JdbcTypes.SMALLINT);
        jdbcTypeMap.put("java.util.List<java.lang.Integer>", JdbcTypes.INTEGER);
        jdbcTypeMap.put("java.util.List<java.lang.Long>", JdbcTypes.BIGINT);
        jdbcTypeMap.put("java.util.List<java.lang.Float>", JdbcTypes.REAL);
        jdbcTypeMap.put("java.util.List<java.lang.Double>", JdbcTypes.DOUBLE);
        jdbcTypeMap.put("java.util.List<java.math.BigDecimal>", JdbcTypes.NUMERIC);
        jdbcTypeMap.put("java.util.List<java.math.BigInteger>", JdbcTypes.NUMERIC);
        jdbcTypeMap.put("java.util.List<java.lang.Character>", JdbcTypes.CHAR);
        
        jdbcTypeMap.put("java.util.ArrayList<java.lang.String>", JdbcTypes.VARCHAR);
        jdbcTypeMap.put("java.util.ArrayList<java.util.Date>", JdbcTypes.TIMESTAMP);
        jdbcTypeMap.put("java.util.ArrayList<java.sql.Date>", JdbcTypes.DATE);
        jdbcTypeMap.put("java.util.ArrayList<java.sql.Time>", JdbcTypes.TIME);
        jdbcTypeMap.put("java.util.ArrayList<java.sql.Timestamp>", JdbcTypes.TIMESTAMP);
        jdbcTypeMap.put("java.util.ArrayList<java.lang.Boolean>", JdbcTypes.BOOLEAN);
        jdbcTypeMap.put("java.util.ArrayList<java.lang.Byte>", JdbcTypes.TINYINT);
        jdbcTypeMap.put("java.util.ArrayList<java.lang.Short>", JdbcTypes.SMALLINT);
        jdbcTypeMap.put("java.util.ArrayList<java.lang.Integer>", JdbcTypes.INTEGER);
        jdbcTypeMap.put("java.util.ArrayList<java.lang.Long>", JdbcTypes.BIGINT);
        jdbcTypeMap.put("java.util.ArrayList<java.lang.Float>", JdbcTypes.REAL);
        jdbcTypeMap.put("java.util.ArrayList<java.lang.Double>", JdbcTypes.DOUBLE);
        jdbcTypeMap.put("java.util.ArrayList<java.math.BigDecimal>", JdbcTypes.NUMERIC);
        jdbcTypeMap.put("java.util.ArrayList<java.math.BigInteger>", JdbcTypes.NUMERIC);
        jdbcTypeMap.put("java.util.ArrayList<java.lang.Character>", JdbcTypes.CHAR);
    }
    //</editor-fold>

}
