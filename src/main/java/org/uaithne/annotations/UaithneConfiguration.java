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
    boolean enableDefaultEntityOperations() default true;
    boolean enableJustOperations() default false;
    boolean enableSaveOperations() default true;
    boolean enableMergeOperations() default true;
    boolean enableModuleAbstractExecutors() default true;
    boolean enableModuleChainedExecutors() default true;
    boolean enableModuleChainedGroupingExecutors() default true;
    Class<?> entitiesImplements() default Void.class;
    MyBatisBackendConfiguration[] myBatisBackendConfigurations() default {};
    Class<?> idValidationAnnotation() default Void.class;
    boolean ignoreIdValidationOnPrimitives() default true;
    Class<?> mandatoryValidationAnnotation() default Void.class;
    boolean ignoreMandatoryValidationOnPrimitives() default true;
    Class<?> optionalValidationAnnotation() default Void.class;
    Class<?> insertValueValidationAnnotation() default Void.class;
    Class<?> saveValueValidationAnnotation() default Void.class;
    Class<?> mergeValueValidationAnnotation() default Void.class;
    Class<?> updateValueValidationAnnotation() default Void.class;
}
