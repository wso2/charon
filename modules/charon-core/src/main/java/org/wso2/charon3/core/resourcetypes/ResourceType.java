package org.wso2.charon3.core.resourcetypes;

import org.wso2.charon3.core.attributes.Attribute;
import org.wso2.charon3.core.attributes.ComplexAttribute;
import org.wso2.charon3.core.attributes.MultiValuedAttribute;
import org.wso2.charon3.core.attributes.SimpleAttribute;
import org.wso2.charon3.core.objects.AbstractSCIMObject;
import org.wso2.charon3.core.objects.plainobjects.ResourceTypeSchemaExtension;
import org.wso2.charon3.core.schema.SCIMAttributeSchema;
import org.wso2.charon3.core.schema.SCIMSchemaDefinitions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.wso2.charon3.core.utils.LambdaExceptionUtils.rethrowFunction;

/**
 * this class represents a resource type as it was defined in RFC7643 section 6
 * <br><br>
 * created at: 04.04.2019
 * @author Pascal Knüppel
 */
public class ResourceType extends AbstractSCIMObject {

    private static final long serialVersionUID = 1263327926680608288L;

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
     * The resource type's human-readable description.  When applicable,
     * service providers MUST specify the description.  OPTIONAL.
     */
    public String getDescription() {
        SCIMAttributeSchema idDefinition = SCIMSchemaDefinitions.SCIMResourceTypeSchemaDefinition.DESCRIPTION;
        return getSimpleAttribute(idDefinition).map(rethrowFunction(SimpleAttribute::getStringValue)).orElse(null);
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

}
