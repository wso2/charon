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
package org.wso2.charon.core.endpoints;

import junit.framework.Assert;
import org.junit.Test;
import org.wso2.charon.core.encoder.json.JSONDecoder;
import org.wso2.charon.core.encoder.json.JSONEncoder;
import org.wso2.charon.core.exceptions.CharonException;
import org.wso2.charon.core.protocol.SCIMResponse;
import org.wso2.charon.core.protocol.endpoints.AbstractResourceEndpoint;
import org.wso2.charon.core.protocol.endpoints.BulkResourceEndpoint;
import org.wso2.charon.core.schema.SCIMConstants;
import org.wso2.charon.core.utils.InMemroyUserManager;

/**
 *
 */
public class BulkEndpointTest {
    InMemroyUserManager userManager = new InMemroyUserManager(1, "wso2.org");

    @Test
    public void testBulkEndpointCreatingUsers() throws CharonException {
        try {
            //encode the user in JSON format
            String encodedUser;
            AbstractResourceEndpoint.registerEncoder(SCIMConstants.JSON, new JSONEncoder());
            AbstractResourceEndpoint.registerDecoder(SCIMConstants.JSON, new JSONDecoder());

            encodedUser = "{\n" +
                          "  \"schemas\":[\n" +
                          "    \"urn:scim:schemas:core:1.0\"\n" +
                          "  ],\n" +
                          "  \"failOnErrors\":1,\n" +
                          "  \"Operations\":[\n" +
                          "    {\n" +
                          "      \"method\":\"POST\",\n" +
                          "      \"path\":\"/Users\",\n" +
                          "      \"bulkId\":\"qwerty\",\n" +
                          "      \"data\":{\n" +
                          "        \"schemas\":[\n" +
                          "          \"urn:scim:schemas:core:1.0\"\n" +
                          "        ],\n" +
                          "        \"userName\":\"Alice\"\n" +
                          "      }\n" +
                          "    },\n" +
                          "    {\n" +
                          "      \"method\":\"POST\",\n" +
                          "      \"path\":\"/Users\",\n" +
                          "      \"bulkId\":\"qwerty\",\n" +
                          "      \"data\":{\n" +
                          "        \"schemas\":[\n" +
                          "          \"urn:scim:schemas:core:1.0\"\n" +
                          "        ],\n" +
                          "        \"userName\":\"Dinuka\"\n" +
                          "      }\n" +
                          "    }," +
                          " {\n" +
                          "      \"method\":\"POST\",\n" +
                          "      \"path\":\"/Users\",\n" +
                          "      \"bulkId\":\"qwerty\",\n" +
                          "      \"data\":{\n" +
                          "        \"schemas\":[\n" +
                          "          \"urn:scim:schemas:core:1.0\"\n" +
                          "        ],\n" +
                          "        \"userName\":\"Malalanayake\"\n" +
                          "      }\n" +
                          "    }\n" +
                          "  ]\n" +
                          "}";


            BulkResourceEndpoint bulkResourceEndpoint = new BulkResourceEndpoint();
            SCIMResponse responseString = bulkResourceEndpoint.processBulkData(encodedUser, SCIMConstants.APPLICATION_JSON, SCIMConstants.APPLICATION_JSON, userManager);

        } catch (Exception e) {
            Assert.assertFalse(e.toString(), true);
        }


    }
}
