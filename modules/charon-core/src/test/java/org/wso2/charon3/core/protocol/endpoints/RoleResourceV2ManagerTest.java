/*
 * Copyright (c) 2025, WSO2 LLC. (http://www.wso2.com).
 *
 * WSO2 LLC. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.charon3.core.protocol.endpoints;

import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.wso2.charon3.core.exceptions.BadRequestException;
import org.wso2.charon3.core.exceptions.CharonException;
import org.wso2.charon3.core.exceptions.ConflictException;
import org.wso2.charon3.core.exceptions.NotImplementedException;
import org.wso2.charon3.core.extensions.RoleV2Manager;
import org.wso2.charon3.core.protocol.ResponseCodeConstants;
import org.wso2.charon3.core.protocol.SCIMResponse;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

public class RoleResourceV2ManagerTest {

    @Mock
    private RoleV2Manager roleV2Manager;

    @BeforeMethod
    public void setup() {

        openMocks(this);
    }

    @Test
    public void testCreateRoleWhileWorkflowEnabled() throws ConflictException, NotImplementedException,
            BadRequestException, CharonException {

        String postRequest = "{\n" +
                "  \"schemas\": [\n" +
                "    \"urn:ietf:params:scim:schemas:extension:role:2.0:Role\"\n" +
                "  ],\n" +
                "  \"displayName\": \"Admin\",\n" +
                "  \"users\": [\n" +
                "    { \"value\": \"user-123\", \"display\": \"John Doe\" }\n" +
                "  ],\n" +
                "  \"groups\": [\n" +
                "    { \"value\": \"group-456\", \"display\": \"Developers\" }\n" +
                "  ],\n" +
                "  \"permissions\": [\n" +
                "    { \"value\": \"perm-789\", \"display\": \"Read\" }\n" +
                "  ]\n" +
                "}";
        CharonException charonException = new CharonException();
        charonException.setStatus(ResponseCodeConstants.CODE_ACCEPTED);
        when(roleV2Manager.createRole(any())).thenThrow(charonException);
        RoleResourceV2Manager roleResourceV2Manager = new RoleResourceV2Manager();
        SCIMResponse scimResponse = roleResourceV2Manager.createRole(postRequest, roleV2Manager);
        assert scimResponse.getResponseStatus() == ResponseCodeConstants.CODE_ACCEPTED;
    }
}
