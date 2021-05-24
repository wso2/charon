/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wso2.charon3.core.schema;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.charon3.core.config.SCIMCustomSchemaExtensionBuilder;
import org.wso2.charon3.core.config.SCIMUserSchemaExtensionBuilder;
import org.wso2.charon3.core.exceptions.BadRequestException;
import org.wso2.charon3.core.exceptions.CharonException;
import org.wso2.charon3.core.exceptions.NotImplementedException;
import org.wso2.charon3.core.extensions.UserManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
* This is to check for extension schema for the user and buildTree a custom user schema with it.
* Unless a extension is defined, core-user schema need to be returned.
*/
public class SCIMResourceSchemaManager {

    private static SCIMResourceSchemaManager manager = new SCIMResourceSchemaManager();
    private static final Logger log = LoggerFactory.getLogger(SCIMResourceSchemaManager.class);

    public static SCIMResourceSchemaManager getInstance() {
        return manager;
    }

    /*
     * Return the SCIM User Resource Schema
     *
     * @return SCIMResourceTypeSchema
     */
    public SCIMResourceTypeSchema getUserResourceSchema() {

        AttributeSchema schemaExtension = SCIMUserSchemaExtensionBuilder.getInstance().getExtensionSchema();
        if (schemaExtension != null) {
            return SCIMResourceTypeSchema.createSCIMResourceSchema(
                    new ArrayList<String>(Arrays.asList(SCIMConstants.USER_CORE_SCHEMA_URI, schemaExtension.getURI())),
                    SCIMSchemaDefinitions.ID, SCIMSchemaDefinitions.EXTERNAL_ID, SCIMSchemaDefinitions.META,
                    SCIMSchemaDefinitions.SCIMUserSchemaDefinition.USERNAME,
                    SCIMSchemaDefinitions.SCIMUserSchemaDefinition.NAME,
                    SCIMSchemaDefinitions.SCIMUserSchemaDefinition.DISPLAY_NAME,
                    SCIMSchemaDefinitions.SCIMUserSchemaDefinition.NICK_NAME,
                    SCIMSchemaDefinitions.SCIMUserSchemaDefinition.PROFILE_URL,
                    SCIMSchemaDefinitions.SCIMUserSchemaDefinition.TITLE,
                    SCIMSchemaDefinitions.SCIMUserSchemaDefinition.USER_TYPE,
                    SCIMSchemaDefinitions.SCIMUserSchemaDefinition.PREFERRED_LANGUAGE,
                    SCIMSchemaDefinitions.SCIMUserSchemaDefinition.LOCALE,
                    SCIMSchemaDefinitions.SCIMUserSchemaDefinition.TIME_ZONE,
                    SCIMSchemaDefinitions.SCIMUserSchemaDefinition.ACTIVE,
                    SCIMSchemaDefinitions.SCIMUserSchemaDefinition.PASSWORD,
                    SCIMSchemaDefinitions.SCIMUserSchemaDefinition.EMAILS,
                    SCIMSchemaDefinitions.SCIMUserSchemaDefinition.PHONE_NUMBERS,
                    SCIMSchemaDefinitions.SCIMUserSchemaDefinition.IMS,
                    SCIMSchemaDefinitions.SCIMUserSchemaDefinition.PHOTOS,
                    SCIMSchemaDefinitions.SCIMUserSchemaDefinition.ADDRESSES,
                    SCIMSchemaDefinitions.SCIMUserSchemaDefinition.GROUPS,
                    SCIMSchemaDefinitions.SCIMUserSchemaDefinition.ENTITLEMENTS,
                    SCIMSchemaDefinitions.SCIMUserSchemaDefinition.ROLES,
                    SCIMSchemaDefinitions.SCIMUserSchemaDefinition.X509CERTIFICATES,
                    schemaExtension);
        }
        return SCIMSchemaDefinitions.SCIM_USER_SCHEMA;
    }

    /*
     * Return the SCIM User Resource Schema
     *
     * @return SCIMResourceTypeSchema
     */
    public SCIMResourceTypeSchema getUserResourceSchema(UserManager userManager)
            throws BadRequestException, NotImplementedException, CharonException {

        AttributeSchema enterpriseSchemaExtension = SCIMUserSchemaExtensionBuilder.getInstance().getExtensionSchema();
        AttributeSchema customSchemaExtension = userManager.getCustomUserSchemaExtension();
        if (enterpriseSchemaExtension != null) {
            List<String> schemas = new ArrayList<>();
            schemas.add(SCIMConstants.USER_CORE_SCHEMA_URI);
            schemas.add(enterpriseSchemaExtension.getURI());
            if (customSchemaExtension != null) {
                schemas.add(customSchemaExtension.getURI());
            } else {
                log.warn("Could not find Custom schema.");
            }
            return SCIMResourceTypeSchema.createSCIMResourceSchema(
                    schemas,
                    SCIMSchemaDefinitions.ID, SCIMSchemaDefinitions.EXTERNAL_ID, SCIMSchemaDefinitions.META,
                    SCIMSchemaDefinitions.SCIMUserSchemaDefinition.USERNAME,
                    SCIMSchemaDefinitions.SCIMUserSchemaDefinition.NAME,
                    SCIMSchemaDefinitions.SCIMUserSchemaDefinition.DISPLAY_NAME,
                    SCIMSchemaDefinitions.SCIMUserSchemaDefinition.NICK_NAME,
                    SCIMSchemaDefinitions.SCIMUserSchemaDefinition.PROFILE_URL,
                    SCIMSchemaDefinitions.SCIMUserSchemaDefinition.TITLE,
                    SCIMSchemaDefinitions.SCIMUserSchemaDefinition.USER_TYPE,
                    SCIMSchemaDefinitions.SCIMUserSchemaDefinition.PREFERRED_LANGUAGE,
                    SCIMSchemaDefinitions.SCIMUserSchemaDefinition.LOCALE,
                    SCIMSchemaDefinitions.SCIMUserSchemaDefinition.TIME_ZONE,
                    SCIMSchemaDefinitions.SCIMUserSchemaDefinition.ACTIVE,
                    SCIMSchemaDefinitions.SCIMUserSchemaDefinition.PASSWORD,
                    SCIMSchemaDefinitions.SCIMUserSchemaDefinition.EMAILS,
                    SCIMSchemaDefinitions.SCIMUserSchemaDefinition.PHONE_NUMBERS,
                    SCIMSchemaDefinitions.SCIMUserSchemaDefinition.IMS,
                    SCIMSchemaDefinitions.SCIMUserSchemaDefinition.PHOTOS,
                    SCIMSchemaDefinitions.SCIMUserSchemaDefinition.ADDRESSES,
                    SCIMSchemaDefinitions.SCIMUserSchemaDefinition.GROUPS,
                    SCIMSchemaDefinitions.SCIMUserSchemaDefinition.ENTITLEMENTS,
                    SCIMSchemaDefinitions.SCIMUserSchemaDefinition.ROLES,
                    SCIMSchemaDefinitions.SCIMUserSchemaDefinition.X509CERTIFICATES,
                    enterpriseSchemaExtension, customSchemaExtension);
        }
        return SCIMSchemaDefinitions.SCIM_USER_SCHEMA;
    }

    /*
     * check whether the extension is enabled
     *
     * @return
     */
    public Boolean isExtensionSet() {
        AttributeSchema schemaExtension = SCIMUserSchemaExtensionBuilder.getInstance().getExtensionSchema();
        if (schemaExtension != null) {
            return true;
        } else {
            return false;
        }
    }

    /*
     * return the extension name
     *
     * @return
     */
    public String getExtensionName() {
        AttributeSchema schemaExtension = SCIMUserSchemaExtensionBuilder.getInstance().getExtensionSchema();
        if (schemaExtension == null) {
            return null;
        }
        return schemaExtension.getName();
    }

    /*
     * return the extension name
     *
     * @return
     */
    public String getCustomSchemaExtensionURI() {

        return SCIMCustomSchemaExtensionBuilder.getInstance().getURI();
    }

    /*
     * return the extension uri
     *
     * @return
     */
    public String getExtensionURI() {
        AttributeSchema schemaExtension = SCIMUserSchemaExtensionBuilder.getInstance().getExtensionSchema();
        if (schemaExtension == null) {
            return null;
        }
        return schemaExtension.getURI();
    }

    /*
     * return the extension's required property
     *
     * @return
     */
    public boolean getExtensionRequired() {
        AttributeSchema schemaExtension = SCIMUserSchemaExtensionBuilder.getInstance().getExtensionSchema();
        if (schemaExtension == null) {
            return false;
        }
        return schemaExtension.getRequired();
    }

    /*
     * return service provider config resource schema
     *
     * @return
     */
    public SCIMResourceTypeSchema getServiceProviderConfigResourceSchema() {
        return SCIMSchemaDefinitions.SCIM_SERVICE_PROVIDER_CONFIG_SCHEMA;
    }

    /*
     * return group resource schema
     *
     * @return
     */
    public SCIMResourceTypeSchema getGroupResourceSchema() {
        return SCIMSchemaDefinitions.SCIM_GROUP_SCHEMA;
    }

    public SCIMResourceTypeSchema getRoleResourceSchema() {

        return SCIMSchemaDefinitions.SCIM_ROLE_SCHEMA;
    }

    public SCIMResourceTypeSchema getResourceTypeResourceSchema() {

        return SCIMSchemaDefinitions.SCIM_RESOURCE_TYPE_SCHEMA;
    }

    public SCIMResourceTypeSchema getResourceTypeResourceSchemaWithoutMultiValuedSchemaExtensions() {

        return SCIMSchemaDefinitions.SCIM_RESOURCE_TYPE_SCHEMA_WITHOUT_MULTIVALUED_SCHEMA_EXTENSIONS;
    }

}
