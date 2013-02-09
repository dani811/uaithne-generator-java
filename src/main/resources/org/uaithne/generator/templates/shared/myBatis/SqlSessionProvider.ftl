<#-- 
Copyright 2012 and beyond, Juan Luis Paz

This file is part of Uaithne.

Uaithne is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Uaithne is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with Uaithne. If not, see <http://www.gnu.org/licenses/>.
-->
package ${packageName};

import java.io.IOException;
import java.io.InputStream;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

public class SqlSessionProvider {
    private SqlSessionFactory sqlSessionFactory;
    private ThreadLocal<SqlSession> currentSqlSession = new ThreadLocal<SqlSession>();

    public SqlSessionProvider(String configurationUrl) throws IOException {
        InputStream inputStream = Resources.getResourceAsStream(configurationUrl);
        sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
    }

    public SqlSessionProvider(String configurationUrl, String envieroment) throws IOException {
        InputStream inputStream = Resources.getResourceAsStream(configurationUrl);
        sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream, envieroment);
    }

    public SqlSessionProvider(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
    }
    
    public boolean openSqlSession() {
        SqlSession session = currentSqlSession.get();
        if (session == null) {
            session = sqlSessionFactory.openSession();
            currentSqlSession.set(session);
            return true;
        }
        return false;
    }
    
    public boolean isSqlSessionOpened() {
        return currentSqlSession.get() != null;
    }
    
    public SqlSession getSqlSession() {
        SqlSession result = currentSqlSession.get();
        if (result == null) {
            openSqlSession();
            result = currentSqlSession.get();
        }
        return result;
    }
    
    public void closeSqlSession(boolean roolback) {
        SqlSession session = currentSqlSession.get();
        if (session != null) {
            if (roolback) {
                session.rollback();
            } else {
                session.commit();
            }
            session.close();
            currentSqlSession.set(null);
        }
    }

    public SqlSessionFactory getSqlSessionFactory() {
        return sqlSessionFactory;
    }
    
}
