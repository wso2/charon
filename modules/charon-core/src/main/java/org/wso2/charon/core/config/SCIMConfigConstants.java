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

public class SCIMConfigConstants {

    public static final String PROVISIONING_CONFIG_NAME = "provisioning-config.xml";
    public static final String SCIM_SCHEMA_EXTENSION_CONFIG = "scim-schema-extension.config";
    public static final String ELEMENT_NAME_SCIM_PROVIDERS = "scim-providers";
    public static final String ELEMENT_NAME_SCIM_CONSUMERS = "scim-consumers";
    public static final String ELEMENT_NAME_SCIM_PROVIDER = "scim-provider";
    public static final String ELEMENT_NAME_SCIM_CONSUMER = "scim-consumer";
    public static final String ELEMENT_NAME_USERNAME = "userName";
    public static final String ELEMENT_NAME_PASSWORD = "password";
    public static final String ELEMENT_NAME_SCIM_ENDPOINTS = "scim-endpoints";
    public static final String ELEMENT_NAME_USER_ENDPOINT = "userEndpoint";
    public static final String ELEMENT_NAME_GROUP_ENDPOINT = "groupEndpoint";
    public static final String ELEMENT_NAME_INCLUDE = "includeAll";
    public static final String ELEMENT_NAME_EXCLUDE = "exclude";
    public static final String ELEMENT_NAME_PROPERTY = "Property";
    public static final String ELEMENT_NAME_APPLIED_TO_PRIVILEGED_ACTIONS = "isAppliedToPrivilegedActions";
    public static final String ELEMENT_NAME_APPLIED_TO_SCIM_OPERATIONS = "isAppliedToSCIMOperations";

    public static final String ATTRIBUTE_NAME_ID = "id";
    public static final String ATTRIBUTE_NAME_USERNAME = "username";
    public static final String ATTRIBUTE_NAME_PASSWORD = "password";
    public static final String ATTRIBUTE_NAME_NAME = "name";

    public static final String PROPERTY_NAME_DUMB_MODE = "dumb-mode";
    public static final String PROPERTY_NAME_PROVISIONING_HANDLER = "provisioning-handlers";
}
