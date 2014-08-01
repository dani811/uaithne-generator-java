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
package org.uaithne.generator.processors.database;

import java.util.HashMap;
import javax.annotation.processing.ProcessingEnvironment;
import org.uaithne.annotations.sql.DefaultJdbcType;
import org.uaithne.annotations.sql.JdbcTypes;
import org.uaithne.generator.commons.ExecutorModuleInfo;

public class QueryGeneratorConfiguration {
    
    private boolean useAutoIncrementId;
    private boolean useGeneratedKeys;
    private String idSecuenceNameTemplate;
    private ProcessingEnvironment processingEnv;
    private ExecutorModuleInfo module;
    private String packageName;
    private String name;
    private String callPrefix;
    private HashMap<String, JdbcTypes> customJdbcTypeMap = new HashMap<String, JdbcTypes>(0);
    private String defaultValue;

    public boolean useAutoIncrementId() {
        return useAutoIncrementId;
    }

    public void setUseAutoIncrementId(boolean useAutoIncrementId) {
        this.useAutoIncrementId = useAutoIncrementId;
    }

    public boolean useGeneratedKeys() {
        return useGeneratedKeys;
    }

    public void setUseGeneratedKeys(boolean useGeneratedKeys) {
        this.useGeneratedKeys = useGeneratedKeys;
    }

    public String getIdSecuenceNameTemplate() {
        return idSecuenceNameTemplate;
    }

    public void setIdSecuenceNameTemplate(String idSecuenceNameTemplate) {
        this.idSecuenceNameTemplate = idSecuenceNameTemplate;
    }

    public ProcessingEnvironment getProcessingEnv() {
        return processingEnv;
    }

    public void setProcessingEnv(ProcessingEnvironment processingEnv) {
        this.processingEnv = processingEnv;
    }

    public ExecutorModuleInfo getModule() {
        return module;
    }

    public void setModule(ExecutorModuleInfo module) {
        this.module = module;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCallPrefix() {
        return callPrefix;
    }

    public void setCallPrefix(String callPrefix) {
        this.callPrefix = callPrefix;
    }

    public HashMap<String, JdbcTypes> getCustomJdbcTypeMap() {
        return customJdbcTypeMap;
    }

    public void setCustomJdbcTypeMap(HashMap<String, JdbcTypes> customJdbcTypeMap) {
        this.customJdbcTypeMap = customJdbcTypeMap;
    }
    
    public void loadCustomJdbcTypeMap(DefaultJdbcType[] defaultJdbcTypes) {
        for (DefaultJdbcType defaultJdbcType : defaultJdbcTypes) {
            JdbcTypes jdbcType = defaultJdbcType.jdbcType();
            for (String klass : defaultJdbcType.classQualifiedName()) {
                customJdbcTypeMap.put(klass, jdbcType);
            }
        }
        for (DefaultJdbcType defaultJdbcType : defaultJdbcTypes) {
            JdbcTypes jdbcType = defaultJdbcType.jdbcType();
            for (String klass : defaultJdbcType.classQualifiedName()) {
                klass = "java.util.List<" + klass + ">";
                JdbcTypes old = customJdbcTypeMap.put(klass, jdbcType);
                if (old != null) {
                    customJdbcTypeMap.put(klass, old);
                }
            }
        }
        for (DefaultJdbcType defaultJdbcType : defaultJdbcTypes) {
            JdbcTypes jdbcType = defaultJdbcType.jdbcType();
            for (String klass : defaultJdbcType.classQualifiedName()) {
                klass = "java.util.ArrayList<" + klass + ">";
                JdbcTypes old = customJdbcTypeMap.put(klass, jdbcType);
                if (old != null) {
                    customJdbcTypeMap.put(klass, old);
                }
            }
        }
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }
    
}
