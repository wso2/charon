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

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import org.wso2.charon.core.exceptions.CharonException;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Class responsible for building a programmatic representation of provisioning-config.xml.
 * Any application using this library can either pass the file path or root config element
 * in expected format to get it parsed.
 */
public class SCIMConfigProcessor {

    private Log logger = LogFactory.getLog(SCIMConfigProcessor.class);

    public SCIMConfig buildConfigFromFile(String filePath) throws CharonException {
        try {
            InputStream inputStream = null;
            File provisioningConfig = new File(filePath);
            if (provisioningConfig.exists()) {
                inputStream = new FileInputStream(provisioningConfig);
                StAXOMBuilder staxOMBuilder = new StAXOMBuilder(inputStream);
                OMElement documentElement = staxOMBuilder.getDocumentElement();
                if (inputStream != null) {
                    inputStream.close();
                }
                return buildConfigFromRootElement(documentElement);
            } else {
                throw new FileNotFoundException();
            }
        } catch (FileNotFoundException e) {
            throw new CharonException(SCIMConfigConstants.PROVISIONING_CONFIG_NAME + "not found.");
        } catch (XMLStreamException e) {
            throw new CharonException("Error in building the configuration file: " +
                                      SCIMConfigConstants.PROVISIONING_CONFIG_NAME);
        } catch (IOException e) {
            throw new CharonException("Error in building the configuration file: " +
                                      SCIMConfigConstants.PROVISIONING_CONFIG_NAME);
        }
    }

    public SCIMConfig buildConfigFromInputStream(InputStream inStream) throws CharonException {
        try {
            StAXOMBuilder omBuilder = new StAXOMBuilder(inStream);
            OMElement rootElement = omBuilder.getDocumentElement();
            if (inStream != null) {
                inStream.close();
            }
            return buildConfigFromRootElement(rootElement);
        } catch (XMLStreamException e) {
            throw new CharonException("Error in building the configuration file: " +
                                      SCIMConfigConstants.PROVISIONING_CONFIG_NAME);

        } catch (IOException e) {
            throw new CharonException("Error in closing the input stream.");
        }
    }

    public SCIMConfig buildConfigFromRootElement(OMElement rootElement) {

        //build scim config
        SCIMConfig scimConfig = new SCIMConfig();

        OMElement scimConsumersElement = rootElement.getFirstChildWithName(
                new QName(SCIMConfigConstants.ELEMENT_NAME_SCIM_CONSUMERS));

        OMElement scimProvidersElement = rootElement.getFirstChildWithName(
                new QName(SCIMConfigConstants.ELEMENT_NAME_SCIM_PROVIDERS));

        //iterate over the individual elements and create scim provider map
        Iterator<OMElement> scimProvidersIterator = scimProvidersElement.getChildrenWithName(new QName(
                SCIMConfigConstants.ELEMENT_NAME_SCIM_PROVIDER));

        //build providers map
        if (scimProvidersIterator != null) {
            Map<String, SCIMProvider> providers = buildSCIMProviderMap(scimProvidersIterator);
            scimConfig.setProvidersMap(providers);
        }

        //iterate over the individual elements and create scim consumer map
        Iterator<OMElement> scimConsumersIterator = scimConsumersElement.getChildrenWithName(new QName(
                SCIMConfigConstants.ELEMENT_NAME_SCIM_CONSUMER));

        //build consumers map
        if (scimConsumersIterator != null) {
            Map<String, SCIMConsumer> consumers = buildSCIMConsumersMap(scimConsumersIterator);
            scimConfig.setConsumersMap(consumers);
        }
        //read any additional properties defined.
        Iterator<OMElement> propertiesIterator = rootElement.getChildrenWithName(
                new QName(SCIMConfigConstants.ELEMENT_NAME_PROPERTY));
        Map<String, String> properties = new HashMap<String, String>();
        while (propertiesIterator.hasNext()) {
            OMElement propertyElement = propertiesIterator.next();
            String propertyName = propertyElement.getAttributeValue(
                    new QName(SCIMConfigConstants.ATTRIBUTE_NAME_NAME));
            String propertyValue = propertyElement.getText();
            properties.put(propertyName, propertyValue);
        }
        scimConfig.setAdditionalProperties(properties);

        return scimConfig;
    }

    private Map<String, SCIMProvider> buildSCIMProviderMap(Iterator<OMElement> providersIterator) {

        Map<String, SCIMProvider> providersMap = new ConcurrentHashMap<String, SCIMProvider>();

        while (providersIterator.hasNext()) {
            OMElement providerElement = providersIterator.next();
            SCIMProvider scimProvider = new SCIMProvider();
            Map<String, String> propertiesMap = new HashMap<String, String>();

            //get provider id
            String providerId = providerElement.getAttributeValue(new QName(
                    SCIMConfigConstants.ATTRIBUTE_NAME_ID));
            scimProvider.setId(providerId);

            //read provider properties
            Iterator<OMElement> propertiesIterator = providerElement.getChildrenWithName(
                    new QName(SCIMConfigConstants.ELEMENT_NAME_PROPERTY));
            while (propertiesIterator.hasNext()) {
                OMElement propertyElement = propertiesIterator.next();
                String propertyName = propertyElement.getAttributeValue(
                        new QName(SCIMConfigConstants.ATTRIBUTE_NAME_NAME));
                String propertyValue = propertyElement.getText();
                propertiesMap.put(propertyName, propertyValue);
            }
            scimProvider.setProperties(propertiesMap);
            providersMap.put(providerId, scimProvider);
        }
        return providersMap;
    }

    private Map<String, SCIMConsumer> buildSCIMConsumersMap(Iterator<OMElement> consumersIterator) {

        Map<String, SCIMConsumer> consumersMap = new ConcurrentHashMap<String, SCIMConsumer>();

        //iterate
        while (consumersIterator.hasNext()) {
            SCIMConsumer scimConsumer = new SCIMConsumer();
            OMElement scimConsumerElement = consumersIterator.next();
            //get consumer id
            String consumerId = scimConsumerElement.getAttributeValue(new QName(
                    SCIMConfigConstants.ATTRIBUTE_NAME_ID));
            scimConsumer.setId(consumerId);
            //get providers
            Iterator<OMElement> scimProviders = scimConsumerElement.getChildrenWithName(
                    new QName(SCIMConfigConstants.ELEMENT_NAME_SCIM_PROVIDER));

            Map<String, SCIMProvider> providersMap = new HashMap<String, SCIMProvider>();

            //iterate through providers and build the consumer specific provider map
            if (scimProviders != null) {
                while (scimProviders.hasNext()) {
                    SCIMProvider scimProvider = new SCIMProvider();

                    OMElement scimProviderElement = scimProviders.next();

                    //get provider id
                    String providerId = scimProviderElement.getAttributeValue(
                            new QName(SCIMConfigConstants.ATTRIBUTE_NAME_ID));
                    scimProvider.setId(providerId);

                    //read properties if exist
                    Map<String, String> providerProperties = new HashMap<String, String>();
                    Iterator<OMElement> propertiesMapIterator = scimProviderElement.getChildrenWithName(
                            new QName(SCIMConfigConstants.ELEMENT_NAME_PROPERTY));
                    if (propertiesMapIterator != null) {
                        //iterate through propertiesMapIterator
                        while (propertiesMapIterator.hasNext()) {
                            OMElement property = propertiesMapIterator.next();
                            String propertyName = property.getAttributeValue(new QName(
                                    SCIMConfigConstants.ATTRIBUTE_NAME_NAME));
                            String propertyValue = property.getText();
                            providerProperties.put(propertyName, propertyValue);
                        }
                    }
                    scimProvider.setProperties(providerProperties);

                    //read customized credentials as attributes and set as properties
                    String userName = scimProviderElement.getAttributeValue(new QName(
                            SCIMConfigConstants.ATTRIBUTE_NAME_USERNAME));
                    if (userName != null) {
                        scimProvider.setProperty(SCIMConfigConstants.ELEMENT_NAME_USERNAME, userName);
                    }
                    String password = scimProviderElement.getAttributeValue(new QName(
                            SCIMConfigConstants.ATTRIBUTE_NAME_PASSWORD));
                    if (password != null) {
                        scimProvider.setProperty(SCIMConfigConstants.ELEMENT_NAME_PASSWORD, password);
                    }
                    providersMap.put(providerId, scimProvider);
                }
                scimConsumer.setScimProviders(providersMap);
            }
            consumersMap.put(consumerId, scimConsumer);

            //read for <includeAll/> element
            OMElement includeAllElement = scimConsumerElement.getFirstChildWithName(
                    new QName(SCIMConfigConstants.ELEMENT_NAME_INCLUDE));
            if (includeAllElement != null) {
                String isIncludeAll = includeAllElement.getText();
                scimConsumer.setIncludeAll(Boolean.parseBoolean(isIncludeAll));
            } else {
                scimConsumer.setIncludeAll(false);
            }
            //read for excluded provider list
            OMElement excludedProviderListElement = scimConsumerElement.getFirstChildWithName(
                    new QName(SCIMConfigConstants.ELEMENT_NAME_EXCLUDE));
            if (excludedProviderListElement != null) {
                Iterator<OMElement> excludedProviderIterator =
                        excludedProviderListElement.getChildrenWithName(
                                new QName(SCIMConfigConstants.ELEMENT_NAME_SCIM_PROVIDER));
                List<String> excludedProviders = new ArrayList<String>();

                if (excludedProviderIterator != null) {
                    while (excludedProviderIterator.hasNext()) {
                        OMElement excludedProvider = excludedProviderIterator.next();
                        String id = excludedProvider.getAttributeValue(new QName(
                                SCIMConfigConstants.ATTRIBUTE_NAME_ID));
                        excludedProviders.add(id);
                    }
                }
                if (!excludedProviders.isEmpty()) {
                    scimConsumer.setExcludedProviderList(excludedProviders);
                }
            }

            //read implementer specific consumer properties
            Iterator<OMElement> customPropIterator = scimConsumerElement.getChildrenWithName(
                    new QName(SCIMConfigConstants.ELEMENT_NAME_PROPERTY));
            Map<String, String> customPropertiesMap = new HashMap<String, String>();
            if (customPropIterator != null) {
                while (customPropIterator.hasNext()) {
                    OMElement propElement = customPropIterator.next();
                    String propName = propElement.getAttributeValue(new QName(
                            SCIMConfigConstants.ATTRIBUTE_NAME_NAME));
                    String propValue = propElement.getText();
                    customPropertiesMap.put(propName, propValue);
                }
                scimConsumer.setPropertiesMap(customPropertiesMap);
            }
        }
        return consumersMap;
    }
}