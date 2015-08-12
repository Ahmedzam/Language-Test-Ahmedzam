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

package com.ibm.watson.app.common.services.impl;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.inject.Binder;
import com.google.inject.multibindings.Multibinder;
import com.ibm.watson.app.common.services.BluemixConfiguredService;
import com.ibm.watson.app.common.services.nlclassifier.NLClassifierService;
import com.ibm.watson.app.common.services.nlclassifier.impl.NLClassifierServiceImpl;
import com.ibm.watson.app.common.services.sqldb.SqlDBService;
import com.ibm.watson.app.common.services.sqldb.impl.SqlDBServiceImpl;
import com.ibm.watson.app.common.util.rest.MessageKey;

public final class BluemixServicesBinder {
    private static final Logger logger = LogManager.getLogger();

    private static final String VCAP_SERVICES_ENV_VAR = "VCAP_SERVICES";
    private static final String VCAP_SERVICES_FILE = "vcap.services";
    
    // This map holds all of the info needed for Bluemix service's configuration
    static final ConcurrentMap<String, BluemixServiceInfo<? extends BluemixConfiguredService>> CONFIGURED_SERVICES = new ConcurrentHashMap<>();
    static {
        // List currently available services, additional ones can be registered later
        addBluemixServiceConfig("natural_language_classifier", NLClassifierService.class, NLClassifierServiceImpl.class);
        addBluemixServiceConfig("sqldb", SqlDBService.class, SqlDBServiceImpl.class);
    }
    
    // A list of all available bluemix services
    static final ConcurrentMap<Class<? extends BluemixConfiguredService>, List<BluemixConfiguredService>> AVAILABLE_SERVICES = new ConcurrentHashMap<>();
    
    public static <T extends BluemixConfiguredService> int bindAll(Binder binder, Class<T> clazz) {
        
    	if ( AVAILABLE_SERVICES.isEmpty() ) {
    		registerBluemixServices();
    	}
    	
    	// No matter what, we must create the multibinder, even if there are no available instances
        // Otherwise, Guice will fail to find a binding for Set<T>, when ultimately we want an empty one
        final Multibinder<T> mbinder = Multibinder.newSetBinder(binder, clazz);
        
        if(!AVAILABLE_SERVICES.containsKey(clazz)) {
            return 0;
        }
        
        @SuppressWarnings("unchecked")
        List<T> services = (List<T>) AVAILABLE_SERVICES.get(clazz);
        for(T service : services) {
            mbinder.addBinding().toInstance(service);
        }
        return services.size();
    }

    public static <T extends BluemixConfiguredService> int bind(Binder binder, Class<T> clazz) {
        
    	if ( AVAILABLE_SERVICES.isEmpty() ) {
    		registerBluemixServices();
    	}
    	
        @SuppressWarnings("unchecked")
        List<T> services = (List<T>) AVAILABLE_SERVICES.get(clazz);
        if (services == null || services.isEmpty() ) {
        	return 0;
        }
        if ( services.size() > 1 ) {
        	throw new IllegalStateException(MessageKey.AQWEGA04002E_multiple_instances_of_service_found_1.getMessage(clazz.getSimpleName()).getFormattedMessage());
        }
        
        binder.bind(clazz).toInstance(services.get(0));
        return 1;
    }
    
    
    private static boolean  registerBaseBluemixServices() {
        String jsonString = null;

        // First, check the system property and try to get a valid JSON file
        String jsonFilename = System.getProperty(VCAP_SERVICES_FILE);
        if (jsonFilename != null) {
            try {
                jsonString = FileUtils.readFileToString(new File(jsonFilename), StandardCharsets.UTF_8);
            } catch (IOException e) {
                logger.catching(Level.DEBUG, e);
            }
            if (jsonString != null && !jsonString.isEmpty()) {
            	logger.info(MessageKey.AQWEGA10000I_parsing_vcap_services_json_conf_from_file_1.getMessage(jsonFilename));
                parseBluemixConfig(jsonString);
                return true;
            }
        }

      
        // No system property for file, see if the the VCAP_SERVICES system property itself is set
        jsonString = System.getProperty(VCAP_SERVICES_ENV_VAR);
        if (jsonString != null && !jsonString.isEmpty()) {
        	logger.info(MessageKey.AQWEGA10001I_parsing_vcap_services_json_from_system_property.getMessage());
            parseBluemixConfig(jsonString);
            return true;
        }

        // No system property, now try the environment
        jsonString = System.getenv(VCAP_SERVICES_ENV_VAR);
        if (jsonString != null && !jsonString.isEmpty()) {
        	logger.info(MessageKey.AQWEGA10001I_parsing_vcap_services_json_from_system_property.getMessage());
            parseBluemixConfig(jsonString);
            return true;
        }
        return false;
    } 
    
    private synchronized static void registerBluemixServices() {
        
    	// If already parsed, return
    	if ( !AVAILABLE_SERVICES.isEmpty() ) {
    		return; 
    	}
        boolean baseServicesFound = registerBaseBluemixServices();
        String jsonString = null;
        
        jsonString = System.getenv("SERVICES_OVERRIDE");
        if(jsonString != null && !jsonString.isEmpty()) {
           logger.info(MessageKey.AQWEGA10002I_parsing_vcap_services_json_from_system_property_override.getMessage());
           parseBluemixConfig(jsonString);
           return;
        }
      
        if ( !baseServicesFound ) {
        	logger.warn(MessageKey.AQWEGA12000W_no_vcap_services_conf_available.getMessage());
        }

    }

    static void parseBluemixConfig(String jsonString) {
        final BluemixServicesConfigurationParser parser = new BluemixServicesConfigurationParser(){
            @Override
            protected void register(BluemixServiceInfo<? extends BluemixConfiguredService> service, BluemixConfiguredService serviceImpl) {
                // To not break backwards compatability, 
                // override this method here and register the implementations with this class instead of the ServicesFactory
                List<BluemixConfiguredService> services = AVAILABLE_SERVICES.get(service.iface);
                if(services == null) {
                    final List<BluemixConfiguredService> newServicesList = new CopyOnWriteArrayList<>();
                    services = AVAILABLE_SERVICES.putIfAbsent(service.iface, newServicesList);
                    if(services == null) {
                        services = newServicesList;
                    }
                }
                services.add(serviceImpl);
            }
        };

        if (logger.isDebugEnabled()) {
            logger.debug("Parsing service configuration: " + jsonString);
        }

        parser.parseAndRegisterServices(jsonString);
    }
    
    // Service configuration 
    
    /**
     * Add a configuration for a given Bluemix service.
     * @param iface
     * @param impl
     * @param serviceNames
     */
    public static <T extends BluemixConfiguredService> void addBluemixServiceConfig(String serviceName, Class<T> iface, Class<? extends T> impl) {
        @SuppressWarnings("unchecked")
        BluemixServiceInfo<T> service = (BluemixServiceInfo<T>) CONFIGURED_SERVICES.get(serviceName);
        if(service == null) {
            service = new BluemixServiceInfo<T>(serviceName, iface, impl);
            CONFIGURED_SERVICES.putIfAbsent(serviceName, service);
        }
    }
    
    protected static BluemixServiceInfo<? extends BluemixConfiguredService> getBluemixServiceByName(String key) {
        for(BluemixServiceInfo<? extends BluemixConfiguredService> service : CONFIGURED_SERVICES.values()) {
            if(key.equals(service.name)) {
                return service;
            }
        }
        // Couldn't find a match on exact name. Try by prefix
        for(BluemixServiceInfo<? extends BluemixConfiguredService> service : CONFIGURED_SERVICES.values()) {
            if(key.startsWith(service.name)) {
                return service;
            }
        }
        return null;
    }
    
    // Bluemix only service registration (done during deserialization)
    protected static <T extends BluemixConfiguredService> void registerBluemixService(Class<T> iface, BluemixConfiguredService impl) {
        Objects.requireNonNull(impl);
        
        List<BluemixConfiguredService> services = AVAILABLE_SERVICES.get(iface);
        if(services == null) {
            final List<BluemixConfiguredService> newServicesList = new CopyOnWriteArrayList<>();
            services = AVAILABLE_SERVICES.putIfAbsent(iface, newServicesList);
            if(services == null) {
                services = newServicesList;
            }
        }
        services.add(impl);
    }
}
