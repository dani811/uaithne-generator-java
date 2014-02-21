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
        appender.append("    private final SqlSessionFactory sqlSessionFactory;\n"
                + "    private final ThreadLocal<SqlSession> currentSqlSession = new ThreadLocal<SqlSession>();\n"
                + "    private final ThreadLocal<Integer> currentSessionLevel = new ThreadLocal<Integer>();\n"
                + "\n"
                + "    public ManagedSqlSessionProvider(String configurationUrl) throws IOException {\n"
                + "        InputStream inputStream = Resources.getResourceAsStream(configurationUrl);\n"
                + "        sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);\n"
                + "    }\n"
                + "\n"
                + "    public ManagedSqlSessionProvider(ClassLoader loader, String configurationUrl) throws IOException {\n"
                + "        InputStream inputStream = Resources.getResourceAsStream(loader, configurationUrl);\n"
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
                + "    public ManagedSqlSessionProvider(ClassLoader loader, String configurationUrl, String environment) throws IOException {\n"
                + "        InputStream inputStream = Resources.getResourceAsStream(loader, configurationUrl);\n"
                + "        sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream, environment);\n"
                + "    }\n"
                + "\n"
                + "    public void beginSqlSessionLevel() {\n"
                + "        Integer currentLevel = currentSessionLevel.get();\n"
                + "        if (currentLevel == null || currentLevel <= 0) {\n"
                + "            currentLevel = 1;\n"
                + "        } else {\n"
                + "            currentLevel++;\n"
                + "        }\n"
                + "        currentSessionLevel.set(currentLevel);\n"
                + "    }\n"
                + "\n"
                + "    public boolean isSqlSessionOpened() {\n"
                + "        return currentSqlSession.get() != null;\n"
                + "    }\n"
                + "\n"
                + "    @Override\n"
                + "    public SqlSession getSqlSession() {\n"
                + "        Integer currentLevel = currentSessionLevel.get();\n"
                + "        if (currentLevel == null || currentLevel <= 0) {\n"
                + "            throw new IllegalStateException(\"No open connection is available in this context, the operation must cross a ManagedSqlSessionExecutorGroup before get the sql session\");\n"
                + "        }\n"
                + "        SqlSession result = currentSqlSession.get();\n"
                + "        if (result == null) {\n"
                + "            result = openNewSqlSession(sqlSessionFactory);\n"
                + "            currentSqlSession.set(result);\n"
                + "        }\n"
                + "        return result;\n"
                + "    }\n"
                + "\n"
                + "    public void endSqlSessionLevel(boolean maybeRollback) {\n"
                + "        Integer currentLevel = currentSessionLevel.get();\n"
                + "        if (currentLevel == null || currentLevel <= 1) {\n"
                + "            currentLevel = 0;\n"
                + "        } else {\n"
                + "            currentLevel--;\n"
                + "        }\n"
                + "\n"
                + "        if (currentLevel > 0) {\n"
                + "            currentSessionLevel.set(currentLevel);\n"
                + "            return;\n"
                + "        }\n"
                + "\n"
                + "        currentSessionLevel.set(null);\n"
                + "\n"
                + "        SqlSession session = currentSqlSession.get();\n"
                + "        if (session != null) {\n"
                + "            currentSqlSession.set(null);\n"
                + "\n"
                + "            if (maybeRollback) {\n"
                + "                rollback(session);\n"
                + "            } else {\n"
                + "                commit(session);\n"
                + "            }\n"
                + "            close(session);\n"
                + "        }\n"
                + "    }\n"
                + "\n"
                + "    public int getCurrentLevel() {\n"
                + "        Integer level = currentSessionLevel.get();\n"
                + "        if (level == null || level <= 0) {\n"
                + "            return 0;\n"
                + "        }\n"
                + "        return level;\n"
                + "    }\n"
                + "\n"
                + "    public SqlSessionFactory getSqlSessionFactory() {\n"
                + "        return sqlSessionFactory;\n"
                + "    }\n"
                + "\n"
                + "    protected SqlSession openNewSqlSession(SqlSessionFactory sqlSessionFactory) {\n"
                + "        return sqlSessionFactory.openSession();\n"
                + "    }\n"
                + "\n"
                + "    protected void commit(SqlSession sqlSession) {\n"
                + "        sqlSession.commit();\n"
                + "    }\n"
                + "\n"
                + "    protected void rollback(SqlSession sqlSession) {\n"
                + "        sqlSession.rollback();\n"
                + "    }\n"
                + "\n"
                + "    protected void close(SqlSession sqlSession) {\n"
                + "        sqlSession.close();\n"
                + "    }");
    }
    
}
