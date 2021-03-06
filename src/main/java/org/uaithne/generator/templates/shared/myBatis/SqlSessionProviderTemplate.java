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
package org.uaithne.generator.templates.shared.myBatis;

import java.io.IOException;
import org.uaithne.generator.templates.ClassTemplate;

public class SqlSessionProviderTemplate extends ClassTemplate {

    public SqlSessionProviderTemplate(String packageName) {
        setPackageName(packageName);
        addImport("org.apache.ibatis.session.SqlSession", packageName);
        if (HAS_CONTEXT_AND_APPPARAM_AND_ARE_DIFFERENT) {
            addImport(getGenerationInfo().getApplicationParameterType(), packageName);
        }
        setClassName("SqlSessionProvider");
        setInterface(true);
        addContextImport(packageName);
    }
    
    @Override
    protected void writeContent(Appendable appender) throws IOException {
        if (HAS_CONTEXT) {
            appender.append("    public SqlSession getSqlSession(").append(CONTEXT_TYPE).append(" context);");
        } else {
            appender.append("    public SqlSession getSqlSession();");
        }
        if (HAS_CONTEXT_AND_APPPARAM_AND_ARE_DIFFERENT) {
            appender.append("\n    public ").append(getGenerationInfo().getApplicationParameterType().getSimpleName()).append(" getApplicationParameter(").append(CONTEXT_TYPE).append(" context);");
        }
    }
    
}
