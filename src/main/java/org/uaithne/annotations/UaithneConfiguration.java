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
package org.uaithne.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.uaithne.annotations.myBatis.MyBatisBackendConfiguration;

@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface UaithneConfiguration {
    boolean enableDefaultEntityOperations() default false;
    boolean enableJustOperations() default false;
    boolean enableSaveOperations() default true;
    boolean enableMergeOperations() default true;
    boolean enableModuleAbstractExecutors() default false;
    boolean enableModuleChainedExecutors() default false;
    @Deprecated
    boolean enableModuleChainedGroupingExecutors() default false;
    Class<?> entitiesImplements() default Void.class;
    MyBatisBackendConfiguration[] myBatisBackendConfigurations() default {};
    Class<?> applicationParameterType() default Void.class;
    Class<?> contextParameterType() default Void.class;
    boolean enableBeanValidations() default false;
    AnnotationConfiguration[] annotationConfigurations() default {};
    AnnotationSubstitution[] annotationSubstitutions() default {};
    @Deprecated
    boolean includeExecuteOtherMethodInExecutors() default false;
    @Deprecated
    boolean includeExecutePostOperationInOperations() default false;
    @Deprecated
    boolean setResultingIdInOperation() default false;
    @Deprecated
    boolean executorExtendsExecutorGroup() default true;
    boolean booleanObjectGetterWithIs() default true;
}
