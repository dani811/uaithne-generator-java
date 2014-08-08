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

public class ApplicationParameterDriverTemplate extends ClassTemplate {

    public ApplicationParameterDriverTemplate(String packageName) {
        setPackageName(packageName);
        addImport("org.apache.ibatis.executor.parameter.ParameterHandler", packageName);
        addImport("org.apache.ibatis.mapping.BoundSql", packageName);
        addImport("org.apache.ibatis.mapping.MappedStatement", packageName);
        addImport("org.apache.ibatis.scripting.xmltags.XMLLanguageDriver", packageName);
        setClassName("ApplicationParameterDriver");
        setExtend("XMLLanguageDriver");
    }
    
    @Override
    protected void writeContent(Appendable appender) throws IOException {
        appender.append("    private final ThreadLocal<Object> applicationParameters = new ThreadLocal<Object>();\n" +
            "\n" +
            "    public Object getApplicationParameter() {\n" +
            "        return applicationParameters.get();\n" +
            "    }\n" +
            "\n" +
            "    /**\n" +
            "     * Important: only set this value one time per thread, only if getApplicationParameter() return null\n" +
            "     * @param applicationParameter\n" +
            "     */\n" +
            "    public void setApplicationParameter(Object applicationParameter) {\n" +
            "        applicationParameters.set(applicationParameter);\n" +
            "    }\n" +
            "\n" +
            "    @Override\n" +
            "    public ParameterHandler createParameterHandler(MappedStatement mappedStatement, Object parameterObject, BoundSql boundSql) {\n" +
            "        Object appParameter = getApplicationParameter();\n" +
            "        boundSql.setAdditionalParameter(\"_app\", appParameter);\n" +
            "        return super.createParameterHandler(mappedStatement, parameterObject, boundSql);\n" +
            "    }");
    }
    
}
