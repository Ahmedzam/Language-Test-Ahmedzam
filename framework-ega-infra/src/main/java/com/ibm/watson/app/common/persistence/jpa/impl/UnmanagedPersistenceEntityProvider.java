/* Copyright IBM Corp. 2015
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ibm.watson.app.common.persistence.jpa.impl;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.commons.lang.BooleanUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ibm.watson.app.common.persistence.jpa.ApplicationTransaction;
import com.ibm.watson.app.common.persistence.jpa.PersistenceEntityProvider;
import com.ibm.watson.app.common.services.general.ConfigurationService;

public class UnmanagedPersistenceEntityProvider implements PersistenceEntityProvider {
    private static final Logger logger = LogManager.getLogger();

    private EntityManagerFactory factory;
    private final boolean useDerby;

    UnmanagedPersistenceEntityProvider(ConfigurationService cfgService, String persistenceUnitName) {
        useDerby = cfgService.getProperty("db.type", "db2").equals("derby");
        
        final Map<String, String> properties = new HashMap<String, String>();
        getDatabaseConnectionProperties(cfgService, properties);
        factory = Persistence.createEntityManagerFactory(persistenceUnitName, properties);
    }

    @Override
    public synchronized EntityManager getEntityManager() {
        return factory.createEntityManager();
    }

    @Override
    public ApplicationTransaction getTransaction(EntityManager em) {
        return new ApplicationEntityManagerTransaction(em);
    }

    @Override
    public boolean isManaged() {
        return false;
    }

    protected void getDatabaseConnectionProperties(ConfigurationService cfgService, Map<String, String> properties) {
        if (logger.isDebugEnabled())
            logger.debug("getDatabaseConnectionProperties() >> ");

        properties.put("openjpa.jdbc.SynchronizeMappings", "false");

        if (BooleanUtils.toBoolean(cfgService.getProperty("jpa.enable.trace", "false"))) {
            properties.put("openjpa.Log", "Runtime=TRACE");
        }

        if (useDerby) {
            // Following are for Derby
            
            // DBCP = Database connection pool from Apache commons
            properties.put("openjpa.ConnectionDriverName", "org.apache.commons.dbcp.BasicDataSource");
            properties.put("openjpa.ConnectionProperties", "DriverClassName=org.apache.derby.jdbc.EmbeddedDriver,Url=" + cfgService.getProperty("db.url"));
            properties.put("openjpa.ConnectionURL", cfgService.getProperty("db.url"));
            properties.put("openjpa.jdbc.SynchronizeMappings", "buildSchema");
            
            // properties.put("javax.persistence.jdbc.url", cfgService.getProperty("db.url"));
            // properties.put("javax.persistence.jdbc.user", "APP");
            // properties.put("javax.persistence.jdbc.password", "APP");
        }
    }
}
