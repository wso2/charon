/*
*  Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
package org.wso2.charon.core.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Programmatic representation of the provisioning-config.xml
 */
public class SCIMConfig {

    private Log logger = LogFactory.getLog(SCIMConfig.class);

    private Map<String, SCIMProvider> providersMap;
    private Map<String, SCIMConsumer> consumersMap;

    /*any additional properties defined in provisioning-config could be read by providing the property name*/
    private Map<String, String> additionalProperties;
    
    public Map<String, SCIMProvider> getProvidersMap() {
        return providersMap;
    }

    public void setProvidersMap(Map<String, SCIMProvider> providersMap) {
        this.providersMap = providersMap;
    }

    public Map<String, SCIMConsumer> getConsumersMap() {
        return consumersMap;
    }

    public void setConsumersMap(Map<String, SCIMConsumer> consumersMap) {
        this.consumersMap = consumersMap;
    }

    /**
     * Obtain the SCIM Consumer with all the provider details processed according to the conventions
     * of provisioning-config.xml
     *
     * @param consumerId
     * @return
     */
    public SCIMConsumer getConsumerProcessed(String consumerId) {
        if (consumersMap != null && consumersMap.containsKey(consumerId)) {
            //when returning the consumer, go through the providers map defined under consumer element
            //and fill their properties map if no property map is in the particular provider, or if there are
            //any properties in this provider, skip filling those attributes and inherit only the other attributes.
            SCIMConsumer scimConsumer = consumersMap.get(consumerId);
            Map<String, SCIMProvider> providers = scimConsumer.getScimProviders();

            if ((providers != null) && (!providers.isEmpty())) {
                for (Map.Entry<String, SCIMProvider> scimProviderEntry : providers.entrySet()) {
                    String providerId = scimProviderEntry.getKey();
                    SCIMProvider scimProvider = scimProviderEntry.getValue();
                    if ((scimProvider.getProperties() != null) &&
                        (!scimProvider.getProperties().isEmpty())) {
                        //inherit properties from provider list other than the ones in the consumer's provider
                        Map<String, String> scimProviderPropMap = scimProvider.getProperties();

                        if ((providersMap != null) && (!providersMap.isEmpty())) {
                            SCIMProvider provider = providersMap.get(providerId);
                            Map<String, String> providerProperties = provider.getProperties();
                            for (Map.Entry<String, String> entry : providerProperties.entrySet()) {
                                if (!scimProviderPropMap.containsKey(entry.getKey())) {
                                    scimProviderPropMap.put(entry.getKey(), entry.getValue());
                                }
                            }
                        }
                    } else {
                        //go through the providers map, get the relevant provider's map,
                        if ((providersMap != null) && (!providersMap.isEmpty())) {
                            SCIMProvider provider = providersMap.get(providerId);
                            if (provider != null) {
                                Map<String, String> providerProperties = provider.getProperties();
                                //TODO:verify whether the properties are updated in the provider of the map
                                scimProvider.setProperties(providerProperties);
                            } else {
                                logger.error("Configuration Error..!!! Provider with id: " +
                                             providerId + "does not exist in the system.");
                            }
                        }

                    }
                }
            }
            //get the updated provider map and if <includeAll> is true, fill the providers other than
            //the ones in the providers map
            if (scimConsumer.isIncludeAll()) {
                //get the updated provider list of the consumer
                //Map<String, SCIMProvider> scimProviders = scimConsumer.getScimProviders();
                //go through all providers and
                for (String providerId : providersMap.keySet()) {
                    if (!providers.containsKey(providerId)) {
                        providers.put(providerId, providersMap.get(providerId));
                    }
                }
            }
            //once again get the updated provider map and see whether any providers are excluded.
            List<String> excludedProviderList = scimConsumer.getExcludedProviderList();
            if ((excludedProviderList != null) && (!excludedProviderList.isEmpty())) {
                Map<String, SCIMProvider> scimProviders = scimConsumer.getScimProviders();
                for (String excludedProvider : excludedProviderList) {
                    if (scimProviders.containsKey(excludedProvider)) {
                        scimProviders.remove(excludedProvider);
                    }
                }
            }
            return scimConsumer;
        } else {
            return null;
        }
    }

    public SCIMConsumer getSCIMConsumer(String id) {
        if (consumersMap.containsKey(id)) {
            return consumersMap.get(id);
        } else {
            return null;
        }
    }

    public String getAdditionalPropertyValue(String propertyName) {
        if (additionalProperties != null && additionalProperties.size() != 0) {
            return additionalProperties.get(propertyName);
        }
        return null;
    }

    public boolean isDumbMode() {
        String dumbMode = this.getAdditionalPropertyValue(SCIMConfigConstants.PROPERTY_NAME_DUMB_MODE);
        return Boolean.parseBoolean(dumbMode);
    }

    public String getProvisioningHandler() {
        return this.getAdditionalPropertyValue(SCIMConfigConstants.PROPERTY_NAME_PROVISIONING_HANDLER);
    }

    void setAdditionalProperties(Map<String, String> additionalProperties) {
        this.additionalProperties = additionalProperties;
    }
}
