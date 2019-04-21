package org.wso2.charon3.core.resourcetypes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.charon3.core.attributes.Attribute;
import org.wso2.charon3.core.attributes.ComplexAttribute;
import org.wso2.charon3.core.attributes.DefaultAttributeFactory;
import org.wso2.charon3.core.attributes.MultiValuedAttribute;
import org.wso2.charon3.core.attributes.SimpleAttribute;
import org.wso2.charon3.core.exceptions.CharonException;
import org.wso2.charon3.core.objects.AbstractSCIMObject;
import org.wso2.charon3.core.objects.plainobjects.ResourceTypeSchemaExtension;
import org.wso2.charon3.core.protocol.endpoints.AbstractResourceManager;
import org.wso2.charon3.core.schema.ResourceTypeSchema;
import org.wso2.charon3.core.schema.SCIMAttributeSchema;
import org.wso2.charon3.core.schema.SCIMConstants;
import org.wso2.charon3.core.schema.SCIMSchemaDefinitions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.wso2.charon3.core.utils.LambdaExceptionUtils.rethrowConsumer;
import static org.wso2.charon3.core.utils.LambdaExceptionUtils.rethrowFunction;
import static org.wso2.charon3.core.utils.LambdaExceptionUtils.rethrowSupplier;

/**
 * this class represents a resource type as it was defined in RFC7643 section 6
 * <br><br>
 * created at: 04.04.2019
 * @author Pascal Knüppel
 */
public class ResourceType extends AbstractSCIMObject {

    private static final long serialVersionUID = 1263327926680608288L;

    private static final Logger log = LoggerFactory.getLogger(ResourceType.class);

    /**
     * the schema that is represented by this resource type
     */
    private ResourceTypeSchema resourceTypeSchema;

    public ResourceType() {
        super.setSchema(SCIMConstants.RESOURCE_TYPE_SCHEMA_URI);
    }

    /**
     *
     * @param id The resource type's server unique id.  This is often the same
     *       value as the "name" attribute.  OPTIONAL.
     * @param name The resource type name.  When applicable, service providers MUST
     *       specify the name, e.g., "User" or "Group".  This name is
     *       referenced by the "meta.resourceType" attribute in all resources.
     *       REQUIRED.
     * @param description  The resource type's human-readable description.  When applicable,
     *       service providers MUST specify the description.  OPTIONAL.
     * @param endpointAddress The resource type's HTTP-addressable endpoint relative to the Base
     *       URL of the service provider, e.g., "/Users" or "/Groups".  REQUIRED.
     * @param resourceTypeSchema used to get the resource type schema and the schema extensions (all extensions will
     *                           be set to non required by default. If you wish to change this do it manually)
     */
    public ResourceType(String id,
                        String name,
                        String description,
                        String endpointAddress,
                        ResourceTypeSchema resourceTypeSchema) {
        this();
        this.replaceId(id);
        this.replaceName(Objects.requireNonNull(name, "name for schema '" + id + "' must not be null"));
        this.replaceDescription(description);
        Objects.requireNonNull(endpointAddress, "endpoint address must not be null");
        if (!endpointAddress.startsWith("/")) {
            rethrowSupplier(() -> {
                log.error("endpoint address must start with '/' but was '{}'", endpointAddress);
                throw new CharonException("endpoint address must start with \"/\"");
            }).get();
        }
        String endpoint = rethrowSupplier(() -> AbstractResourceManager.getResourceEndpointURL(endpointAddress)).get();
        this.replaceEndpoint(endpoint);
        Objects.requireNonNull(resourceTypeSchema, "resource type schema must not be null");
        this.replaceSchema(resourceTypeSchema.getSchemasList().get(0));
        resourceTypeSchema.getExtensions().forEach(extension -> {
            this.addSchemaExtension(extension.getSchema(), false);
        });
        this.resourceTypeSchema = resourceTypeSchema;
    }

    public ResourceTypeSchema getResourceTypeSchema() {
        return resourceTypeSchema;
    }

    /**
     * The resource type's server unique id.  This is often the same
     * value as the "name" attribute.  OPTIONAL.
     */
    @Override
    public String getId() {
        SCIMAttributeSchema idDefinition = SCIMSchemaDefinitions.SCIMResourceTypeSchemaDefinition.ID;
        return getSimpleAttribute(idDefinition).map(rethrowFunction(SimpleAttribute::getStringValue)).orElse(null);
    }

    /**
     * The resource type's server unique id.  This is often the same
     * value as the "name" attribute.  OPTIONAL.
     */
    @Override
    public void replaceId(String id) {
        SCIMAttributeSchema idDefinition = SCIMSchemaDefinitions.SCIMResourceTypeSchemaDefinition.ID;
        replaceSimpleAttribute(idDefinition, id);
    }


    /**
     * The resource type name.  When applicable, service providers MUST
     * specify the name, e.g., "User" or "Group".  This name is
     * referenced by the "meta.resourceType" attribute in all resources.
     * REQUIRED.
     */
    public String getName() {
        SCIMAttributeSchema idDefinition = SCIMSchemaDefinitions.SCIMResourceTypeSchemaDefinition.NAME;
        return getSimpleAttribute(idDefinition).map(rethrowFunction(SimpleAttribute::getStringValue)).orElse(null);
    }

    /**
     * The resource type name.  When applicable, service providers MUST
     * specify the name, e.g., "User" or "Group".  This name is
     * referenced by the "meta.resourceType" attribute in all resources.
     * REQUIRED.
     */
    public void replaceName(String name) {
        SCIMAttributeSchema idDefinition = SCIMSchemaDefinitions.SCIMResourceTypeSchemaDefinition.NAME;
        replaceSimpleAttribute(idDefinition, name);
    }

    /**
     * The resource type's human-readable description.  When applicable,
     * service providers MUST specify the description.  OPTIONAL.
     */
    public String getDescription() {
        SCIMAttributeSchema idDefinition = SCIMSchemaDefinitions.SCIMResourceTypeSchemaDefinition.DESCRIPTION;
        return getSimpleAttribute(idDefinition).map(rethrowFunction(SimpleAttribute::getStringValue)).orElse(null);
    }

    /**
     * The resource type's human-readable description.  When applicable,
     * service providers MUST specify the description.  OPTIONAL.
     */
    public void replaceDescription(String description) {
        SCIMAttributeSchema idDefinition = SCIMSchemaDefinitions.SCIMResourceTypeSchemaDefinition.DESCRIPTION;
        replaceSimpleAttribute(idDefinition, description);
    }

    /**
     * The resource type's HTTP-addressable endpoint relative to the Base
     * URL of the service provider, e.g., "Users".  REQUIRED.
     */
    public String getEndpoint() {
        SCIMAttributeSchema idDefinition = SCIMSchemaDefinitions.SCIMResourceTypeSchemaDefinition.ENDPOINT;
        return (String) getSimpleAttribute(idDefinition).map(rethrowFunction(SimpleAttribute::getValue)).orElse(null);
    }

    /**
     * The resource type's HTTP-addressable endpoint relative to the Base
     * URL of the service provider, e.g., "Users".  REQUIRED.
     */
    public void replaceEndpoint(String endpoint) {
        SCIMAttributeSchema idDefinition = SCIMSchemaDefinitions.SCIMResourceTypeSchemaDefinition.ENDPOINT;
        replaceSimpleAttribute(idDefinition, endpoint);
    }

    /**
     * The resource type's primary/base schema URI, e.g.,
     * "urn:ietf:params:scim:schemas:core:2.0:User".  This MUST be equal
     * to the "id" attribute of the associated "Schema" resource.
     * REQUIRED.
     */
    public String getSchema() {
        SCIMAttributeSchema idDefinition = SCIMSchemaDefinitions.SCIMResourceTypeSchemaDefinition.SCHEMA;
        return (String) getSimpleAttribute(idDefinition).map(rethrowFunction(SimpleAttribute::getValue)).orElse(null);
    }

    /**
     * The resource type's primary/base schema URI, e.g.,
     * "urn:ietf:params:scim:schemas:core:2.0:User".  This MUST be equal
     * to the "id" attribute of the associated "Schema" resource.
     * REQUIRED.
     */
    public void replaceSchema(String schema) {
        SCIMAttributeSchema idDefinition = SCIMSchemaDefinitions.SCIMResourceTypeSchemaDefinition.SCHEMA;
        replaceSimpleAttribute(idDefinition, schema);
    }

    /**
     * The resource type's primary/base schema URI, e.g.,
     * "urn:ietf:params:scim:schemas:core:2.0:User".  This MUST be equal
     * to the "id" attribute of the associated "Schema" resource.
     * REQUIRED.
     */
    public List<ResourceTypeSchemaExtension> getSchemaExtensions() {
        SCIMAttributeSchema idDefinition = SCIMSchemaDefinitions.SCIMResourceTypeSchemaDefinition.SCHEMA_EXTENSIONS;
        MultiValuedAttribute extensions = getMultiValuedAttribute(idDefinition).orElse(null);
        if (extensions == null) {
            return Collections.emptyList();
        }
        List<ResourceTypeSchemaExtension> resourceTypeExtensions = new ArrayList<>();
        for (Attribute attributeValue : extensions.getAttributeValues()) {
            SCIMAttributeSchema requirdDef =
                SCIMSchemaDefinitions.SCIMResourceTypeSchemaDefinition.SCHEMA_EXTENSION_REQUIRED;
            SCIMAttributeSchema schemaDef =
                SCIMSchemaDefinitions.SCIMResourceTypeSchemaDefinition.SCHEMA_EXTENSION_SCHEMA;
            ComplexAttribute complexAttribute = (ComplexAttribute) attributeValue;
            String schema = (String) getSimpleAttribute(schemaDef, complexAttribute).map(
                rethrowFunction(SimpleAttribute::getValue)).orElse(null);
            boolean required = getSimpleAttribute(requirdDef, complexAttribute).map(
                rethrowFunction(SimpleAttribute::getBooleanValue)).orElse(false);
            ResourceTypeSchemaExtension schemaExtension = new ResourceTypeSchemaExtension(schema, required);
            resourceTypeExtensions.add(schemaExtension);
        }
        return resourceTypeExtensions;
    }

    /**
     * The resource type's primary/base schema URI, e.g.,
     * "urn:ietf:params:scim:schemas:core:2.0:User".  This MUST be equal
     * to the "id" attribute of the associated "Schema" resource.
     * REQUIRED.
     */
    public void replaceSchemaExtensions(List<ResourceTypeSchemaExtension> resourceTypeExtensions) {
        SCIMAttributeSchema idDefinition = SCIMSchemaDefinitions.SCIMResourceTypeSchemaDefinition.SCHEMA_EXTENSIONS;
        MultiValuedAttribute extensions = getMultiValuedAttribute(idDefinition).orElse(null);
        if (extensions != null) {
            deleteAttribute(idDefinition.getName());
        }

        List<Attribute> extensionAttributes = new ArrayList<>();
        for (ResourceTypeSchemaExtension resourceTypeExtension : resourceTypeExtensions) {
            ComplexAttribute extension = getSchemaExtensionAttribute(resourceTypeExtension);
            extensionAttributes.add(extension);
        }

        MultiValuedAttribute multiValuedAttribute = (MultiValuedAttribute) rethrowSupplier(() -> {
            return DefaultAttributeFactory.createAttribute(idDefinition,
                new MultiValuedAttribute(idDefinition.getName(), extensionAttributes));
        }).get();

        setAttribute(multiValuedAttribute);
    }

    /**
     * creates a schema extension attribute as complex type
     *
     * @param resourceTypeExtension the payload data of the schema extension attribute
     */
    private ComplexAttribute getSchemaExtensionAttribute(ResourceTypeSchemaExtension resourceTypeExtension) {
        SCIMAttributeSchema idDefinition = SCIMSchemaDefinitions.SCIMResourceTypeSchemaDefinition.SCHEMA_EXTENSIONS;
        ComplexAttribute extension = (ComplexAttribute) rethrowSupplier(() -> {
            return DefaultAttributeFactory.createAttribute(idDefinition, new ComplexAttribute(idDefinition.getName()));
        }).get();
        SimpleAttribute required = (SimpleAttribute) rethrowSupplier(() -> {
            SCIMAttributeSchema attributeSchema =
                SCIMSchemaDefinitions.SCIMResourceTypeSchemaDefinition.SCHEMA_EXTENSION_REQUIRED;
            return DefaultAttributeFactory.createAttribute(attributeSchema, new SimpleAttribute(
                SCIMSchemaDefinitions.SCIMResourceTypeSchemaDefinition.SCHEMA_EXTENSION_REQUIRED.getName(),
                resourceTypeExtension.isRequired()));
        }).get();
        SimpleAttribute schema = (SimpleAttribute) rethrowSupplier(() -> {
            SCIMAttributeSchema attributeSchema =
                SCIMSchemaDefinitions.SCIMResourceTypeSchemaDefinition.SCHEMA_EXTENSION_SCHEMA;
            return DefaultAttributeFactory.createAttribute(attributeSchema, new SimpleAttribute(
                SCIMSchemaDefinitions.SCIMResourceTypeSchemaDefinition.SCHEMA_EXTENSION_SCHEMA.getName(),
                resourceTypeExtension.getSchema()));
        }).get();
        rethrowConsumer(o -> extension.setSubAttribute((Attribute) o)).accept(schema);
        rethrowConsumer(o -> extension.setSubAttribute((Attribute) o)).accept(required);
        return extension;
    }

    /**
     * adds a new schema extension
     *
     * @param schema the schema uri
     * @param required if the extension is required or not
     */
    public void addSchemaExtension(String schema, boolean required) {
        SCIMAttributeSchema idDefinition = SCIMSchemaDefinitions.SCIMResourceTypeSchemaDefinition.SCHEMA_EXTENSIONS;
        MultiValuedAttribute extensions = getMultiValuedAttribute(idDefinition).orElse(null);

        ResourceTypeSchemaExtension schemaExtension = new ResourceTypeSchemaExtension(schema, required);
        if (extensions == null) {
            replaceSchemaExtensions(Collections.singletonList(schemaExtension));
            return;
        }

        extensions.getAttributeValues().add(getSchemaExtensionAttribute(schemaExtension));
    }

}
