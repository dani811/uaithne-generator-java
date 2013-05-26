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

public class ManagedSqlSessionProviderTemplate extends ClassTemplate {

    public ManagedSqlSessionProviderTemplate(String packageName) {
        setPackageName(packageName);
        addImport("java.io.IOException", packageName);
        addImport("java.io.InputStream", packageName);
        addImport("org.apache.ibatis.io.Resources", packageName);
        addImport("org.apache.ibatis.session.SqlSession", packageName);
        addImport("org.apache.ibatis.session.SqlSessionFactory", packageName);
        addImport("org.apache.ibatis.session.SqlSessionFactoryBuilder", packageName);
        setClassName("ManagedSqlSessionProvider");
        addImplement("SqlSessionProvider");
    }
    
    @Override
    protected void writeContent(Appendable appender) throws IOException {
        appender.append("    private SqlSessionFactory sqlSessionFactory;\n"
                + "    private ThreadLocal<SqlSession> currentSqlSession = new ThreadLocal<SqlSession>();\n"
                + "    private ThreadLocal<Integer> currentSessionLevel = new ThreadLocal<Integer>();\n"
                + "\n"
                + "    public ManagedSqlSessionProvider(String configurationUrl) throws IOException {\n"
                + "        InputStream inputStream = Resources.getResourceAsStream(configurationUrl);\n"
                + "        sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);\n"
                + "    }\n"
                + "\n"
                + "    public ManagedSqlSessionProvider(String configurationUrl, String environment) throws IOException {\n"
                + "        InputStream inputStream = Resources.getResourceAsStream(configurationUrl);\n"
                + "        sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream, environment);\n"
                + "    }\n"
                + "\n"
                + "    public ManagedSqlSessionProvider(SqlSessionFactory sqlSessionFactory) {\n"
                + "        this.sqlSessionFactory = sqlSessionFactory;\n"
                + "    }\n"
                + "\n"
                + "    public boolean openSqlSession() {\n"
                + "        SqlSession session = currentSqlSession.get();\n"
                + "        if (session == null) {\n"
                + "            session = sqlSessionFactory.openSession();\n"
                + "            currentSqlSession.set(session);\n"
                + "            currentSessionLevel.set(1);\n"
                + "            return true;\n"
                + "        } else {\n"
                + "            Integer currentLevel = currentSessionLevel.get();\n"
                + "            if (currentLevel == null || currentLevel <= 0) {\n"
                + "                currentLevel = 1;\n"
                + "            } else {\n"
                + "                currentLevel++;\n"
                + "            }\n"
                + "            currentSessionLevel.set(currentLevel);\n"
                + "            return false;\n"
                + "        }\n"
                + "    }\n"
                + "\n"
                + "    public boolean isSqlSessionOpened() {\n"
                + "        return currentSqlSession.get() != null;\n"
                + "    }\n"
                + "\n"
                + "    public SqlSession getSqlSession() {\n"
                + "        SqlSession result = currentSqlSession.get();\n"
                + "        if (result == null) {\n"
                + "            throw new IllegalStateException(\"No open connection is available in this context, the operation must cross a ManagedSqlSessionExecutorGroup before get the sql session\");\n"
                + "        }\n"
                + "        return result;\n"
                + "    }\n"
                + "\n"
                + "    public void closeSqlSession(boolean roolback) {\n"
                + "        SqlSession session = currentSqlSession.get();\n"
                + "        if (session != null) {\n"
                + "            Integer currentLevel = currentSessionLevel.get();\n"
                + "            if (currentLevel == null || currentLevel <= 1) {\n"
                + "                currentLevel = 0;\n"
                + "            } else {\n"
                + "                currentLevel--;\n"
                + "            }\n"
                + "\n"
                + "            if (currentLevel > 0) {\n"
                + "                currentSessionLevel.set(currentLevel);\n"
                + "                return;\n"
                + "            }\n"
                + "\n"
                + "            if (roolback) {\n"
                + "                session.rollback();\n"
                + "            } else {\n"
                + "                session.commit();\n"
                + "            }\n"
                + "            session.close();\n"
                + "            currentSqlSession.set(null);\n"
                + "        }\n"
                + "        currentSessionLevel.set(null);\n"
                + "    }\n"
                + "\n"
                + "    public SqlSessionFactory getSqlSessionFactory() {\n"
                + "        return sqlSessionFactory;\n"
                + "    }");
    }
    
}
