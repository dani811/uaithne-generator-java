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
package org.uaithne.annotations.myBatis;

import org.uaithne.annotations.Ternary;
import org.uaithne.annotations.sql.DefaultJdbcType;

public @interface MyBatisBackendConfiguration {
    MyBatisBackend backend();
    String subPackageName() default "myBatis";
    String mapperPrefix() default "MyBatis";
    String mapperSuffix() default "Mapper";
    String idSecuenceNameTemplate() default "[[table]]_[[column]]_seq";
    Ternary useAutoIncrementId() default Ternary.UNSPECIFIED;
    DefaultJdbcType[] defaultJdbcTypes() default {};
}
