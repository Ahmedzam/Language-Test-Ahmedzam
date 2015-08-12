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
import javax.transaction.UserTransaction;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.ibm.watson.app.common.persistence.impl.CloseableInitialContext;
import com.ibm.watson.app.common.persistence.jpa.PersistenceEntityProvider;
import com.ibm.watson.app.common.services.general.ConfigurationService;

public class ApplicationPersistenceModule extends AbstractModule {
    private static final Logger logger = LogManager.getLogger();
    
    private final String persistenceUnitName;
    
    public ApplicationPersistenceModule(String persistenceUnitName) {
        this.persistenceUnitName = persistenceUnitName;
    }

    @Override
    protected void configure() {
        requireBinding(ConfigurationService.class);
    }

    @Provides
    PersistenceEntityProvider getEntityProvider(ConfigurationService cfgService) {
        boolean managed = false;
        
        try(CloseableInitialContext ctx = new CloseableInitialContext()) {
           UserTransaction t = (UserTransaction) ctx.lookup("java:comp/UserTransaction");
           if(t != null) {
              managed = true;
           }
        } catch (NamingException e) {
            // Ignore, not in a managed env
            // logger.catching(e);
        }
        
        if(logger.isDebugEnabled()) {
            logger.debug("Persistence container is " + (managed ? "managed" : "unmanaged"));
        }
        
        return managed ? new ManagedPersistenceEntityProvider(persistenceUnitName) : new UnmanagedPersistenceEntityProvider(cfgService, persistenceUnitName);
    }
}
