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
package org.wso2.charon.samples.utils;

import java.io.File;

/**
 * Change constants common to all the samples in one place - like credentials
 */
public class SampleConstants {
    //******  This configutration for test with charon sample demo **********
    //credentials for accessing API
    public static final String CRED_USER_NAME = "wso2charonAdmin";
    public static final String CRED_PASSWORD = "charonAdmin123@wso2";

    public static final String CRED_TENANT_DOMAIN = "wso2.edu";
    public static final String KEY_STORE_PATH = "charon-samples" + File.separator + "src" + File.separator +
                                                "main" + File.separator + "resources" + File.separator +
                                                "charonserverkeystore.jks";

    public static final String KEY_STORE_PASS = "wso2@Charon#123";

    /*change the transport of following urls to https if you need secure communication.*/
    public static final String USER_ENDPOINT = "http://localhost:8080/charonDemoApp/scim/Users/";
    public static final String GROUP_ENDPOINT = "http://localhost:8080/charonDemoApp/scim/Groups/";
    public static final String BULK_ENDPOINT = "http://localhost:8080/charonDemoApp/scim/Bulk/";
    public static final String REG_SERVICE_ENDPOINT = "http://localhost:8080/charonDemoApp/scim/RegistrationService";

    //*******************************************

    //******  This configutration for test with IS **********
//    public static final String CRED_USER_NAME = "admin";
//    public static final String CRED_PASSWORD = "admin";
//
//    public static final String CRED_TENANT_DOMAIN = "wso2.edu";
//    public static final String KEY_STORE_PATH ="/home/dinuka/packs/is/02-11-evening-4.0/wso2is-4.0.0/repository/resources/security/wso2carbon.jks";
//    public static final String KEY_STORE_PASS = "wso2carbon";
//
//    public static final String USER_ENDPOINT = "https://localhost:9443/wso2/scim/Users/";
//    public static final String GROUP_ENDPOINT = "https://localhost:9443/wso2/scim/Groups/";
//    public static final String REG_SERVICE_ENDPOINT = "https://localhost:9443/wso2/scim/RegistrationService";

    //*******************************************


    /*public static final String USER_ENDPOINT = "http://localhost:8081/charonDemoApp/scim/Users/";
    public static final String GROUP_ENDPOINT = "http://localhost:8081/charonDemoApp/scim/Groups/";
    public static final String REG_SERVICE_ENDPOINT = "http://localhost:8080/charonDemoApp/scim/RegistrationService";*/
    /*public static final String USER_ENDPOINT = "http://appserver.stratoslive.wso2.com/t/charon.com/webapps/charonDemoApp/scim/Users/";
    //public static final String USER_ENDPOINT = "http://localhost:8080/t/charon.com/webapps/charonDemoApp/scim/Users/";
    public static final String GROUP_ENDPOINT = "http://appserver.stratoslive.wso2.com/t/charon.com/webapps/charonDemoApp/scim/Groups/";
    public static final String REG_SERVICE_ENDPOINT = "http://appserver.stratoslive.wso2.com/t/charon.com/webapps/charonDemoApp/scim/RegistrationService";
    //public static final String REG_SERVICE_ENDPOINT = "http://localhost:8080/t/charon.com/webapps/charonDemoApp/scim/RegistrationService";
    //http://appserver.stratoslive.wso2.com/t/charon.org/webapps/charonDemoApp/*/

    public static void setKeyStore() {
        System.setProperty("javax.net.ssl.trustStore", KEY_STORE_PATH);
        System.setProperty("javax.net.ssl.trustStorePassword", KEY_STORE_PASS);
    }

}
