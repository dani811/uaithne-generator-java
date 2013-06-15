package org.uaithne.generator.processors.sql;

import javax.annotation.processing.ProcessingEnvironment;
import org.uaithne.generator.commons.EntityInfo;
import org.uaithne.generator.commons.OperationInfo;

public interface SqlQueryGenerator {
    public ProcessingEnvironment getProcessingEnv();
    public void setProcessingEnv(ProcessingEnvironment processingEnv);
    public String[] getSelectManyQuery(OperationInfo operation);
    public String[] getSelectOneQuery(OperationInfo operation);
    public String[] getSelectPageCountQuery(OperationInfo operation);
    public String[] getSelectPageQuery(OperationInfo operation);
    public String[] getEntityDeleteByIdQuery(EntityInfo entity);
    public String[] getEntityInsertQuery(EntityInfo entity);
    public String[] getEntityLastInsertedIdQuery(EntityInfo entity);
    public String[] getEntityMergeQuery(EntityInfo entity);
    public String[] getEntitySelectByIdQuery(EntityInfo entity);
    public String[] getEntityUpdateQuery(EntityInfo entity);
    public String[] getCustomDeleteQuery(OperationInfo operation);
    public String[] getCustomInsertQuery(OperationInfo operation);
    public String[] getCustomUpdateQuery(OperationInfo operation);
}
