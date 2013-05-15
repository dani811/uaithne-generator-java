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
package org.uaithne.generator.commons;

import java.util.ArrayList;
import java.util.HashMap;
import javax.lang.model.element.TypeElement;

public class GenerationInfo {
    
    private String sharedPackage = "";
    private String sharedPackageDot = "";
    private String sharedGwtPackage = "";
    private String sharedGwtPackageDot = "";
    private String sharedMyBatisPackage;
    private String sharedMyBatisPackageDot;
    private String resultsPackage = "";
    private String resultsPackageDot = "";
    private HashMap<String, EntityInfo> entitiesByRealName = new HashMap<String, EntityInfo>(); 
    private HashMap<String, EntityInfo> entitiesByName = new HashMap<String, EntityInfo>(); 
    private ArrayList<EntityInfo> entities = new ArrayList<EntityInfo>();
    private HashMap<String, OperationInfo> operationsByRealName = new HashMap<String, OperationInfo>();
    private HashMap<String, OperationInfo> operationsByName = new HashMap<String, OperationInfo>();
    private ArrayList<OperationInfo> operations = new ArrayList<OperationInfo>();
    private ArrayList<ExecutorModuleInfo> executorModules = new ArrayList<ExecutorModuleInfo>();
    private HashMap<String, ExecutorModuleInfo> executorModulesByRealName = new HashMap<String, ExecutorModuleInfo>();
    private HashMap<String, Object> extraInfo = new HashMap<String, Object>();
    private boolean generateDefaultEntityOperations;
    private boolean generateJustOperationsEnabled;
    private boolean generateSaveOperationsEnabled = true;
    private boolean generateMergeOperationsEnabled = true;
    private boolean generateModuleChainedExecutorsEnabled = true;
    private boolean generateModuleChainedGroupingExecutorsEnabled = true;
    private HashMap<String, DataTypeInfo> resultTypes = new HashMap<String, DataTypeInfo>();
    private boolean useConcreteCollections;
    private boolean includeGwtClientExecutors;
    private DataTypeInfo entitiesImplements;

    public String getSharedPackage() {
        return sharedPackage;
    }

    public void setSharedPackage(String sharedPackage) {
        this.sharedPackage = sharedPackage;
    }

    public String getSharedPackageDot() {
        return sharedPackageDot;
    }

    public void setSharedPackageDot(String sharedPackageDot) {
        this.sharedPackageDot = sharedPackageDot;
    }

    public String getSharedGwtPackage() {
        return sharedGwtPackage;
    }

    public void setSharedGwtPackage(String sharedGwtPackage) {
        this.sharedGwtPackage = sharedGwtPackage;
    }

    public String getSharedGwtPackageDot() {
        return sharedGwtPackageDot;
    }

    public void setSharedGwtPackageDot(String sharedGwtPackageDot) {
        this.sharedGwtPackageDot = sharedGwtPackageDot;
    }

    public String getSharedMyBatisPackage() {
        return sharedMyBatisPackage;
    }

    public void setSharedMyBatisPackage(String sharedMyBatisPackage) {
        this.sharedMyBatisPackage = sharedMyBatisPackage;
    }

    public String getSharedMyBatisPackageDot() {
        return sharedMyBatisPackageDot;
    }

    public void setSharedMyBatisPackageDot(String sharedMyBatisPackageDot) {
        this.sharedMyBatisPackageDot = sharedMyBatisPackageDot;
    }

    public String getResultsPackage() {
        return resultsPackage;
    }

    public void setResultsPackage(String resultsPackage) {
        this.resultsPackage = resultsPackage;
    }

    public String getResultsPackageDot() {
        return resultsPackageDot;
    }

    public void setResultsPackageDot(String resultsPackageDot) {
        this.resultsPackageDot = resultsPackageDot;
    }

    public HashMap<String, EntityInfo> getEntitiesByRealName() {
        return entitiesByRealName;
    }

    public HashMap<String, EntityInfo> getEntitiesByName() {
        return entitiesByName;
    }
    
    public EntityInfo getEntityByRealName(TypeElement klass) {
        return entitiesByRealName.get(klass.getQualifiedName().toString());
    }
    
    public EntityInfo getEntityByName(DataTypeInfo name) {
        return entitiesByName.get(name.getQualifiedName());
    }

    public ArrayList<EntityInfo> getEntities() {
        return entities;
    }

    public HashMap<String, OperationInfo> getOperationsByRealName() {
        return operationsByRealName;
    }

    public HashMap<String, OperationInfo> getOperationsByName() {
        return operationsByName;
    }
    
    public OperationInfo getOperationByRealName(TypeElement klass) {
        return operationsByRealName.get(klass.getQualifiedName().toString());
    }
    
    public OperationInfo getOperationByName(DataTypeInfo name) {
        return operationsByName.get(name.getQualifiedName());
    }

    public ArrayList<OperationInfo> getOperations() {
        return operations;
    }

    public ArrayList<ExecutorModuleInfo> getExecutorModules() {
        return executorModules;
    }

    public HashMap<String, ExecutorModuleInfo> getExecutorModulesByRealName() {
        return executorModulesByRealName;
    }
    
    public ExecutorModuleInfo getExecutorModuleByRealName(TypeElement klass) {
        return executorModulesByRealName.get(klass.getQualifiedName().toString());
    }
    
    public void addExecutorModule(ExecutorModuleInfo executorModule) {
        executorModules.add(executorModule);
        executorModulesByRealName.put(executorModule.getRealName(), executorModule);
    }
    
    public void addEntity(EntityInfo entityInfo) {
        entitiesByRealName.put(entityInfo.getRealName(), entityInfo);
        entitiesByName.put(entityInfo.getDataType().getQualifiedName(), entityInfo);
        entities.add(entityInfo);
    }
    
    public void addOperation(OperationInfo operation, ExecutorModuleInfo executorModule) {
        if (operation.getRealName() != null) {
            operationsByRealName.put(operation.getRealName(), operation);
        }
        EntityInfo entity = operation.getEntity();
        if (entity != null) {
            entity.addOperation(operation);
        }
        operationsByName.put(operation.getDataType().getQualifiedName(), operation);
        operations.add(operation);
        executorModule.addOperation(operation);
    }

    public HashMap<String, Object> getExtraInfo() {
        return extraInfo;
    }
    
    public Object addExtraInfo(String key, Object value) {
        return extraInfo.put(key, value);
    }
    
    public void combineAllEntities() {
        for (EntityInfo entity : entities) {
            entity.uncombine();
        }
        for (EntityInfo entity : entities) {
            combineEntity(entity);
        }
    }
    
    private void combineEntity(EntityInfo entity) {
        if (entity.getCombined() != entity) {
            return;
        }
        EntityInfo parent = getParent(entity);
        if (parent != null) {
            combineEntity(parent);
            entity.combineWithParent(parent.getCombined());
        }
    }
    
    public EntityInfo getParent(EntityInfo entity) {
        DataTypeInfo extend = entity.getExtend();
        if (extend == null) {
            return null;
        }
        return getEntityByName(extend);
    }

    public boolean isGenerateDefaultEntityOperations() {
        return generateDefaultEntityOperations;
    }

    public void setGenerateDefaultEntityOperations(boolean generateDefaultEntityOperations) {
        this.generateDefaultEntityOperations = generateDefaultEntityOperations;
    }

    public boolean isGenerateJustOperationsEnabled() {
        return generateJustOperationsEnabled;
    }

    public void setGenerateJustOperationsEnabled(boolean generateJustOperationsEnabled) {
        this.generateJustOperationsEnabled = generateJustOperationsEnabled;
    }

    public boolean isGenerateSaveOperationsEnabled() {
        return generateSaveOperationsEnabled;
    }

    public void setGenerateSaveOperationsEnabled(boolean generateSaveOperationsEnabled) {
        this.generateSaveOperationsEnabled = generateSaveOperationsEnabled;
    }

    public boolean isGenerateMergeOperationsEnabled() {
        return generateMergeOperationsEnabled;
    }

    public void setGenerateMergeOperationsEnabled(boolean generateMergeOperationsEnabled) {
        this.generateMergeOperationsEnabled = generateMergeOperationsEnabled;
    }

    public boolean isGenerateModuleChainedExecutorsEnabled() {
        return generateModuleChainedExecutorsEnabled;
    }

    public void setGenerateModuleChainedExecutorsEnabled(boolean generateModuleChainedExecutorsEnabled) {
        this.generateModuleChainedExecutorsEnabled = generateModuleChainedExecutorsEnabled;
    }

    public boolean isGenerateModuleChainedGroupingExecutorsEnabled() {
        return generateModuleChainedGroupingExecutorsEnabled;
    }

    public void setGenerateModuleChainedGroupingExecutorsEnabled(boolean generateModuleChainedGroupingExecutorsEnabled) {
        this.generateModuleChainedGroupingExecutorsEnabled = generateModuleChainedGroupingExecutorsEnabled;
    }
    
    public HashMap<String, DataTypeInfo> getResultTypes() {
        return resultTypes;
    }
    
    public DataTypeInfo getResultTypeByName(DataTypeInfo result) {
        return resultTypes.get(result.getQualifiedName());
    }
    
    public void addResultType(DataTypeInfo result, DataTypeInfo resultWrapper) {
        resultTypes.put(result.getQualifiedName(), resultWrapper);
    }

    public boolean isUseConcreteCollections() {
        return useConcreteCollections;
    }

    public void setUseConcreteCollections(boolean useConcreteCollections) {
        this.useConcreteCollections = useConcreteCollections;
    }

    public boolean isIncludeGwtClientExecutors() {
        return includeGwtClientExecutors;
    }

    public void setIncludeGwtClientExecutors(boolean includeGwtClientExecutors) {
        this.includeGwtClientExecutors = includeGwtClientExecutors;
    }

    public DataTypeInfo getEntitiesImplements() {
        return entitiesImplements;
    }

    public void setEntitiesImplements(DataTypeInfo entitiesImplements) {
        this.entitiesImplements = entitiesImplements;
    }
    
}
