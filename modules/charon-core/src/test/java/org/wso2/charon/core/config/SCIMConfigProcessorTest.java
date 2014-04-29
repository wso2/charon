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

import org.junit.Assert;
import org.junit.Test;
import org.wso2.charon.core.exceptions.CharonException;

import java.io.IOException;
import java.io.InputStream;

public class SCIMConfigProcessorTest {
    @Test
    public void testBuildingSampleConfig1() throws CharonException, IOException {
        SCIMConfigProcessor scimConfigProcessor = new SCIMConfigProcessor();
        InputStream inStream = this.getClass().getClassLoader().getResource(
                "provisioning-config-sample1.xml").openStream();
        SCIMConfig scimConfig = scimConfigProcessor.buildConfigFromInputStream(inStream);
        //test consumer, provider ids
        Assert.assertEquals("carbon.super", scimConfig.getSCIMConsumer("carbon.super").getId());
        Assert.assertEquals("node2", scimConfig.getProvidersMap().get("node2").getId());

        //test provider properties
        Assert.assertEquals("https://localhost:9444/wso2/scim/Users",
                            scimConfig.getProvidersMap().get("node2").getProperty(
                                    SCIMConfigConstants.ELEMENT_NAME_USER_ENDPOINT));
        //test consumer properties
        Assert.assertEquals(true,
                            Boolean.parseBoolean(scimConfig.getSCIMConsumer(
                                    "carbon.super").getPropertiesMap().get(
                                    SCIMConfigConstants.ELEMENT_NAME_APPLIED_TO_SCIM_OPERATIONS)));

        //test processed consumer properties
        Assert.assertEquals("https://localhost:9444/wso2/scim/Users",
                            scimConfig.getConsumerProcessed("carbon.super").
                                    getScimProviders().get("node2").getProperty(
                                    SCIMConfigConstants.ELEMENT_NAME_USER_ENDPOINT));
        if (inStream != null) {
            inStream.close();
        }
    }

    @Test
    public void testBuildingSampleConfig2() throws IOException, CharonException {
        SCIMConfigProcessor scimConfigProcessor = new SCIMConfigProcessor();
        InputStream inStream = this.getClass().getClassLoader().getResource(
                "provisioning-config-sample2.xml").openStream();
        SCIMConfig scimConfig = scimConfigProcessor.buildConfigFromInputStream(inStream);

        //test all the providers are in provider list
        Assert.assertNotNull(scimConfig.getProvidersMap().get("wso2"));
        Assert.assertNotNull(scimConfig.getProvidersMap().get("google"));
        Assert.assertNotNull(scimConfig.getProvidersMap().get("salesforce"));

        //test all consumers are in consumer list
        Assert.assertNotNull(scimConfig.getConsumersMap().get("example.com"));
        Assert.assertNotNull(scimConfig.getConsumersMap().get("admin@carbon.super"));

        //test all the providers are in example.com consumer
        SCIMConsumer exampleConsumer = scimConfig.getConsumerProcessed("example.com");
        Assert.assertNotNull(exampleConsumer.getScimProviders().get("wso2"));
        Assert.assertNotNull(exampleConsumer.getScimProviders().get("google"));
        Assert.assertNotNull(exampleConsumer.getScimProviders().get("salesforce"));

        //test admin user name, password and user endpoint in salesforce provider are customized for example.com consumer
        Assert.assertEquals("adminabc", scimConfig.getProvidersMap().get("salesforce").getProperty(
                SCIMConfigConstants.ELEMENT_NAME_USERNAME));
        Assert.assertEquals("adminabc", scimConfig.getProvidersMap().get("salesforce").getProperty(
                SCIMConfigConstants.ELEMENT_NAME_PASSWORD));
        Assert.assertEquals("https://localhost:9444/salesforce/scim/Users",
                            scimConfig.getProvidersMap().get("salesforce").getProperty(
                                    SCIMConfigConstants.ELEMENT_NAME_USER_ENDPOINT));

        Assert.assertEquals("admin1", scimConfig.getConsumerProcessed(
                "example.com").getScimProviders().get("salesforce").getProperty(
                SCIMConfigConstants.ELEMENT_NAME_USERNAME));
        Assert.assertEquals("admin1", scimConfig.getConsumerProcessed(
                "example.com").getScimProviders().get("salesforce").getProperty(
                SCIMConfigConstants.ELEMENT_NAME_PASSWORD));
        Assert.assertEquals("https://localhost:9444/salesforce/t/example.com/scim/Users",
                            scimConfig.getConsumerProcessed("example.com").getScimProviders().
                                    get("salesforce").getProperty(SCIMConfigConstants.ELEMENT_NAME_USER_ENDPOINT));

        //test all providers are included in admin@carbon.super consumer except wso2 provider
        Assert.assertNull(scimConfig.getConsumerProcessed("admin@carbon.super").getScimProviders().get("wso2"));
        Assert.assertNotNull(scimConfig.getConsumerProcessed("admin@carbon.super").getScimProviders().get("google"));
        Assert.assertNotNull(scimConfig.getConsumerProcessed("admin@carbon.super").getScimProviders().get("salesforce"));

    }

    @Test
    public void testBuildingSampleConfig3() throws IOException, CharonException {
        SCIMConfigProcessor scimConfigProcessor = new SCIMConfigProcessor();
        InputStream inStream = this.getClass().getClassLoader().getResource(
                "provisioning-config-sample3.xml").openStream();
        SCIMConfig scimConfig = scimConfigProcessor.buildConfigFromInputStream(inStream);
    }

    @Test
    public void testBuildingSampleConfig4() throws IOException, CharonException {
        SCIMConfigProcessor scimConfigProcessor = new SCIMConfigProcessor();
        InputStream inStream = this.getClass().getClassLoader().getResource(
                "provisioning-config-sample4.xml").openStream();
        SCIMConfig scimConfig = scimConfigProcessor.buildConfigFromInputStream(inStream);
    }

    @Test
    public void testBuildingSampleConfig5() throws IOException, CharonException {
        SCIMConfigProcessor scimConfigProcessor = new SCIMConfigProcessor();
        InputStream inStream = this.getClass().getClassLoader().getResource(
                "provisioning-config-sample5.xml").openStream();
        SCIMConfig scimConfig = scimConfigProcessor.buildConfigFromInputStream(inStream);
        Assert.assertFalse(scimConfig.isDumbMode());
        Assert.assertEquals("org.wso2.charon.core.provisioning.SampleProvisioningHandler",
                            scimConfig.getProvisioningHandler());
    }
}
