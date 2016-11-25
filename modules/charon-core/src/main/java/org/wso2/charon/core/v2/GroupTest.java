/*
 * Copyright (c) 2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.wso2.charon.core.v2;

import org.wso2.charon.core.v2.protocol.SCIMResponse;
import org.wso2.charon.core.v2.protocol.endpoints.AbstractResourceManager;
import org.wso2.charon.core.v2.protocol.endpoints.GroupResourceManager;
import org.wso2.charon.core.v2.schema.SCIMConstants;

import java.util.HashMap;

/**
 * This class is only for testing purpose
 */
public class GroupTest {
    public static void main(String [] args) {

        GroupResourceManager um = new GroupResourceManager();
        HashMap hmp = new HashMap<String, String>();
        hmp.put(SCIMConstants.GROUP_ENDPOINT, "http://localhost:8080/scim/v2/Groups");
        um.setEndpointURLMap(hmp);

        String array = "{\n" +
                "     \"schemas\": [\"urn:ietf:params:scim:schemas:core:2.0:Group\"],\n" +
                "     \"displayName\": \"Doctors\",\n" +
                "     \"members\": [\n" +
                "       {\n" +
                "         \"value\": \"e01b5773-c8f3-446d-8958-31c603b65660\",\n" +
                "         \"$ref\":\n" +
                "   \"https://example.com/v2/Users/2819c223-7f76-453a-919d-413861904646\",\n" +
                "         \"display\": \"Babs Jensen\"\n" +
                "       },\n" +
                "       {\n" +
                "         \"value\": \"902c246b-6245-4190-8e05-00816be7344a\",\n" +
                "         \"$ref\":\n" +
                "   \"https://example.com/v2/Users/902c246b-6245-4190-8e05-00816be7344a\",\n" +
                "         \"display\": \"Mandy Pepperidge\"\n" +
                "       }\n" +
                "     ]\n" +
                "     }";

        String attributes = "displayName";
        String excludeAttributes = "members";

        //----CREATE Group--------
        //SCIMResponse res=um.create(array,new SCIMUserManager(),null,null);

        //-----GET GROUP ---------
        //SCIMResponse res= um.get("c2fa9b6d-5865-4378-948a-f349b64d1544",new SCIMUserManager(),null,excludeAttributes);

        //-----DELETE GROUP  ---------
        //SCIMResponse res= um.delete("c2fa9b6d-5865-4378-948a-f349b64d1544",new SCIMUserManager());

        //-----LIST GROUPS ---------
        //SCIMResponse res= um.list(new SCIMUserManager(),null,null);

        //-----LIST GROUPS WITH PAGINATION  ---------
        //SCIMResponse res= um.listWithPagination(2,1,new SCIMUserManager(),null,null);

        //-----FILTER GROUPS at Groups Endpoint  ---------
        //String filter ="members.value eq 2819c223-7f76-453a-919d-413861904646";
        String filter = "displayName eq Doctors";
        //SCIMResponse res= um.listByFilter(filter, new SCIMUserManager(), null, null);

        //-----SORT GROUPS  ---------
        //SCIMResponse res= um.listBySort(null,"AsCEnding",new SCIMUserManager(),attributes,null);

        //-----UPDATE GROUP WITH PUT ---------
        SCIMResponse res = um.updateWithPUT("e01b5773-c8f3-446d-8958-31c603b65660", array, new SCIMUserManager(), null, null);

        System.out.println(res.getResponseStatus());
        System.out.println("");
        System.out.println(res.getHeaderParamMap());
        System.out.println("");
        System.out.println(res.getResponseMessage());
    }
}
