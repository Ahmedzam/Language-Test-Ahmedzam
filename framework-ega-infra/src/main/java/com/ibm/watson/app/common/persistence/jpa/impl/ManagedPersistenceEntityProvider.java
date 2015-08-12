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

import javax.naming.NamingException;
import javax.persistence.EntityManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ibm.watson.app.common.persistence.impl.CloseableInitialContext;
import com.ibm.watson.app.common.persistence.jpa.ApplicationTransaction;
import com.ibm.watson.app.common.persistence.jpa.PersistenceEntityProvider;

public class ManagedPersistenceEntityProvider implements PersistenceEntityProvider {
    private static final Logger logger = LogManager.getLogger();
    
    private final String entityManagerName;
    
    ManagedPersistenceEntityProvider(String persistenceUnitName) {
        this.entityManagerName = "java:comp/env/" + persistenceUnitName + "/entitymanager";
    }

    @Override
    public synchronized EntityManager getEntityManager() {
        try(CloseableInitialContext ic = new CloseableInitialContext()) {
            if ( logger.isDebugEnabled() )
               logger.debug("Getting entityManager from Context ");
            return (EntityManager) ic.lookup( entityManagerName );
        } catch (NamingException e) {
            //e.printStackTrace();
        } 
        return null;
    }

    @Override
    public ApplicationTransaction getTransaction(EntityManager em) {
        return new ApplicationUserTransaction();
    }

    @Override
    public boolean isManaged() {
        return true;
    }
}
