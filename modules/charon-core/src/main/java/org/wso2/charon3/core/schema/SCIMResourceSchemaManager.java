/*
 * Copyright (c) 2016-2023, WSO2 LLC. (http://www.wso2.com).
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
package org.wso2.charon3.core.schema;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.charon3.core.config.SCIMAgentSchemaExtensionBuilder;
import org.wso2.charon3.core.config.SCIMCustomSchemaExtensionBuilder;
import org.wso2.charon3.core.config.SCIMSystemSchemaExtensionBuilder;
import org.wso2.charon3.core.config.SCIMUserSchemaExtensionBuilder;
import org.wso2.charon3.core.exceptions.BadRequestException;
import org.wso2.charon3.core.exceptions.CharonException;
import org.wso2.charon3.core.exceptions.NotImplementedException;
import org.wso2.charon3.core.extensions.UserManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
* This is to check for extension schema for the user and buildTree a custom user schema with it.
* Unless an extension is defined, core-user schema need to be returned.
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

        AttributeSchema enterpriseSchemaExtension = SCIMUserSchemaExtensionBuilder.getInstance().getExtensionSchema();
        AttributeSchema systemSchemaExtension = SCIMSystemSchemaExtensionBuilder.getInstance().getExtensionSchema();
        AttributeSchema agentSchemaExtension = SCIMAgentSchemaExtensionBuilder.getInstance().getExtensionSchema();

        List<String> schemaURIs = new ArrayList<>();
        schemaURIs.add(SCIMConstants.USER_CORE_SCHEMA_URI);

        List<AttributeSchema> schemaDefinitions = new ArrayList<>(Arrays.asList(
                SCIMSchemaDefinitions.ID,
                SCIMSchemaDefinitions.EXTERNAL_ID,
                SCIMSchemaDefinitions.META,
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
                SCIMSchemaDefinitions.SCIMUserSchemaDefinition.X509CERTIFICATES
        ));

        if (Boolean.TRUE.equals(SCIMResourceSchemaManager.getInstance().isExtensionSet())) {
            schemaURIs.add(enterpriseSchemaExtension.getURI());
            schemaURIs.add(systemSchemaExtension.getURI());

            schemaDefinitions.add(enterpriseSchemaExtension);
            schemaDefinitions.add(systemSchemaExtension);

            // add agent schema extension if it is not null
            if (agentSchemaExtension != null) {
                schemaURIs.add(agentSchemaExtension.getURI());
                schemaDefinitions.add(agentSchemaExtension);
            }
        }

        return SCIMResourceTypeSchema.createSCIMResourceSchema(
                schemaURIs, schemaDefinitions.toArray(new AttributeSchema[0]));
    }

    /*
     * Return the SCIM User Resource Schema
     *
     * @return SCIMResourceTypeSchema
     */
    public SCIMResourceTypeSchema getUserResourceSchema(UserManager userManager)
            throws BadRequestException, NotImplementedException, CharonException {

        AttributeSchema enterpriseSchemaExtension = SCIMUserSchemaExtensionBuilder.getInstance().getExtensionSchema();
        AttributeSchema systemSchemaExtension = SCIMSystemSchemaExtensionBuilder.getInstance().getExtensionSchema();
        AttributeSchema customSystemAttributeSchema = userManager.getCustomAttributeSchemaInSystemExtension();
        Map<String, AttributeSchema> systemSchemaSubAttributesMap =
                Optional.ofNullable(systemSchemaExtension)
                        .map(AttributeSchema::getSubAttributeSchemas)
                        .orElse(Collections.emptyList())
                        .stream()
                        .filter(attr -> attr.getURI() != null)
                        .collect(Collectors.toMap(
                                AttributeSchema::getURI,
                                Function.identity(),
                                (existing, replacement) -> existing));

        if (systemSchemaExtension == null && customSystemAttributeSchema != null) {
            systemSchemaExtension = customSystemAttributeSchema;
        } else if (customSystemAttributeSchema != null) {
            for (AttributeSchema attributeSchema : customSystemAttributeSchema.getSubAttributeSchemas()) {
                if (!systemSchemaSubAttributesMap.containsKey(attributeSchema.getURI())) {
                    systemSchemaExtension.getSubAttributeSchemas().add(attributeSchema);
                }
            }
        }
        AttributeSchema agentSchemaExtension = SCIMAgentSchemaExtensionBuilder.getInstance().getExtensionSchema();
        AttributeSchema customAgentAttributeSchema = userManager.getCustomAttributeSchemaInAgentExtension();
        Map<String, AttributeSchema> agentSchemaSubAttributesMap =
                Optional.ofNullable(agentSchemaExtension)
                        .map(AttributeSchema::getSubAttributeSchemas)
                        .orElse(Collections.emptyList())
                        .stream()
                        .collect(Collectors.toMap(AttributeSchema::getURI, Function.identity()));

        if (agentSchemaExtension == null && customAgentAttributeSchema != null) {
            agentSchemaExtension = customAgentAttributeSchema;
        } else if (customAgentAttributeSchema != null) {
            for (AttributeSchema attributeSchema : customAgentAttributeSchema.getSubAttributeSchemas()) {
                if (!agentSchemaSubAttributesMap.containsKey(attributeSchema.getURI())) {
                    agentSchemaExtension.getSubAttributeSchemas().add(attributeSchema);
                }
            }
        }

        AttributeSchema customSchemaExtension = userManager.getCustomUserSchemaExtension();

        List<String> schemas = new ArrayList<>();
        schemas.add(SCIMConstants.USER_CORE_SCHEMA_URI);

        List<AttributeSchema> schemaDefinitions = new ArrayList<>(Arrays.asList(
                SCIMSchemaDefinitions.ID,
                SCIMSchemaDefinitions.EXTERNAL_ID,
                SCIMSchemaDefinitions.META,
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
                SCIMSchemaDefinitions.SCIMUserSchemaDefinition.X509CERTIFICATES
        ));

        if (Boolean.TRUE.equals(SCIMResourceSchemaManager.getInstance().isExtensionSet())) {
            schemas.add(enterpriseSchemaExtension.getURI());
            schemas.add(systemSchemaExtension.getURI());

            schemaDefinitions.add(enterpriseSchemaExtension);
            schemaDefinitions.add(systemSchemaExtension);

            // add agent schema extension if it is not null
            if (agentSchemaExtension != null) {
                schemas.add(agentSchemaExtension.getURI());
                schemaDefinitions.add(agentSchemaExtension);
            }
        }

        if (customSchemaExtension != null) {
            schemas.add(customSchemaExtension.getURI());
            schemaDefinitions.add(customSchemaExtension);
        } else {
            log.warn("Could not find custom schema.");
        }

        return SCIMResourceTypeSchema.createSCIMResourceSchema(
                schemas, schemaDefinitions.toArray(new AttributeSchema[0]));
    }

    /**
     * Check whether the extension is enabled.
     *
     * @return true if extension is enabled.
     */
    public Boolean isExtensionSet() {

        AttributeSchema schemaExtension = SCIMUserSchemaExtensionBuilder.getInstance().getExtensionSchema();
        return schemaExtension != null;
    }

    /**
     * Return the extension name.
     *
     * @return extension name
     */
    public String getExtensionName() {

        AttributeSchema schemaExtension = SCIMUserSchemaExtensionBuilder.getInstance().getExtensionSchema();
        if (schemaExtension == null) {
            return null;
        }
        return schemaExtension.getName();
    }

    /**
     * Return the system schema extension name.
     *
     * @return system schema extension name
     */
    public String getSystemSchemaExtensionName() {

        AttributeSchema schemaExtension = SCIMSystemSchemaExtensionBuilder.getInstance().getExtensionSchema();
        if (schemaExtension == null) {
            return null;
        }
        return schemaExtension.getName();
    }

    /**
     * Return the custom schema extension name.
     *
     * @return custom schema extension name
     */
    public String getCustomSchemaExtensionURI() {

        return SCIMCustomSchemaExtensionBuilder.getInstance().getURI();
    }

    /**
     * Return the extension uri.
     *
     * @return extension uri
     */
    public String getExtensionURI() {

        AttributeSchema schemaExtension = SCIMUserSchemaExtensionBuilder.getInstance().getExtensionSchema();
        if (schemaExtension == null) {
            return null;
        }
        return schemaExtension.getURI();
    }

    /**
     * Return the system schema extension uri.
     *
     * @return system schema extension uri
     */
    public String getSystemSchemaExtensionURI() {

        AttributeSchema schemaExtension = SCIMSystemSchemaExtensionBuilder.getInstance().getExtensionSchema();
        if (schemaExtension == null) {
            return null;
        }
        return schemaExtension.getURI();
    }

    /**
     * Return the system schema extension's required property.
     *
     * @return extension's required property
     */
    public boolean getSystemSchemaExtensionRequired() {

        AttributeSchema schemaExtension = SCIMSystemSchemaExtensionBuilder.getInstance().getExtensionSchema();
        if (schemaExtension == null) {
            return false;
        }
        return schemaExtension.getRequired();
    }

    /**
     * Return the extension's required property.
     *
     * @return extension's required property
     */
    public boolean getExtensionRequired() {
        AttributeSchema schemaExtension = SCIMUserSchemaExtensionBuilder.getInstance().getExtensionSchema();
        if (schemaExtension == null) {
            return false;
        }
        return schemaExtension.getRequired();
    }

    /**
     * Return the agent schema extension name.
     *
     * @return agent schema extension name
     */
    public String getAgentSchemaExtensionName() {

        AttributeSchema schemaExtension = SCIMAgentSchemaExtensionBuilder.getInstance().getExtensionSchema();
        if (schemaExtension == null) {
            return null;
        }
        return schemaExtension.getName();
    }

    /**
     * Return the agent schema extension uri.
     *
     * @return agent schema extension uri
     */
    public String getAgentSchemaExtensionURI() {

        AttributeSchema schemaExtension = SCIMAgentSchemaExtensionBuilder.getInstance().getExtensionSchema();
        if (schemaExtension == null) {
            return null;
        }
        return schemaExtension.getURI();
    }

    /**
     * Return the agent schema extension's required property.
     *
     * @return agent extension's required property
     */
    public boolean getAgentSchemaExtensionRequired() {

        AttributeSchema schemaExtension = SCIMAgentSchemaExtensionBuilder.getInstance().getExtensionSchema();
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

    public SCIMResourceTypeSchema getRoleResourceV2Schema() {

        return SCIMSchemaDefinitions.SCIM_ROLE_V2_SCHEMA;
    }

    public SCIMResourceTypeSchema getResourceTypeResourceSchema() {

        return SCIMSchemaDefinitions.SCIM_RESOURCE_TYPE_SCHEMA;
    }

    public SCIMResourceTypeSchema getResourceTypeResourceSchemaWithoutMultiValuedSchemaExtensions() {

        return SCIMSchemaDefinitions.SCIM_RESOURCE_TYPE_SCHEMA_WITHOUT_MULTIVALUED_SCHEMA_EXTENSIONS;
    }

}
