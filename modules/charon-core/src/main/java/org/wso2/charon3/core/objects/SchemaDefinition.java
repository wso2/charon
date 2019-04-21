package org.wso2.charon3.core.objects;

import org.apache.commons.lang3.StringUtils;
import org.wso2.charon3.core.attributes.AbstractAttribute;
import org.wso2.charon3.core.attributes.Attribute;
import org.wso2.charon3.core.attributes.ComplexAttribute;
import org.wso2.charon3.core.attributes.DefaultAttributeFactory;
import org.wso2.charon3.core.attributes.MultiValuedAttribute;
import org.wso2.charon3.core.resourcetypes.ResourceType;
import org.wso2.charon3.core.schema.AttributeSchema;
import org.wso2.charon3.core.schema.ResourceTypeSchema;
import org.wso2.charon3.core.schema.SCIMAttributeSchema;
import org.wso2.charon3.core.schema.SCIMConstants;
import org.wso2.charon3.core.schema.SCIMDefinitions;
import org.wso2.charon3.core.schema.SCIMSchemaDefinitions;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.wso2.charon3.core.utils.LambdaExceptionUtils.rethrowConsumer;

/**
 * this class represents a schema definition for the schemas endpoint
 * <br><br>
 * created at: 19.04.2019
 *
 * @author Pascal Knüppel
 */
public class SchemaDefinition extends AbstractSCIMObject {

    private static final long serialVersionUID = -2529490956849739563L;

    public SchemaDefinition () {
        setSchema(SCIMConstants.SCHEMA_URI);
    }

    public SchemaDefinition (ResourceType resourceType) {
        this();
        setAttributes(resourceType);
    }

    private void setAttributes (ResourceType resourceType) {
        ResourceTypeSchema resourceTypeSchema = resourceType.getResourceTypeSchema();
        setName(resourceTypeSchema.getName());
        setDescription(resourceTypeSchema.getDescription());
        setId(resourceTypeSchema.getSchemasList().get(0));
        setAttributes(resourceTypeSchema.getAttributesList());
    }

    @Override
    public void setId (String id) {
        replaceId(id);
    }

    private void setName (String name) {
        SCIMAttributeSchema attributeDef = SCIMSchemaDefinitions.SchemaSchemaDefinition.NAME;
        if (StringUtils.isBlank(name)) {
            deleteAttribute(attributeDef.getName());
            return;
        }
        replaceSimpleAttribute(attributeDef, name);
    }

    private void setDescription (String description) {
        SCIMAttributeSchema attributeDef = SCIMSchemaDefinitions.SchemaSchemaDefinition.DESCRIPTION;
        if (StringUtils.isBlank(description)) {
            deleteAttribute(attributeDef.getName());
            return;
        }
        replaceSimpleAttribute(attributeDef, description);
    }

    private void setAttributes (List<AttributeSchema> attributesList) {
        SCIMAttributeSchema attributeDef = SCIMSchemaDefinitions.SchemaSchemaDefinition.ATTRIBUTES;
        MultiValuedAttribute attributes = getOrCrateMultivaluedAttribute(attributeDef);

        setAttributes(attributesList, attributeDef, attributes);
    }

    private void setAttributes (List<AttributeSchema> attributesList,
                                SCIMAttributeSchema attributeDef,
                                MultiValuedAttribute attributes) {
        for (AttributeSchema attributeSchema : attributesList) {
            ComplexAttribute attribute = new ComplexAttribute(attributes.getName());
            rethrowConsumer(o -> DefaultAttributeFactory.createAttribute(attributeDef, (AbstractAttribute) o)).accept(
                attribute);
            SCIMAttributeSchema name = SCIMSchemaDefinitions.SchemaSchemaDefinition.ATTRIBUTES_NAME;
            getSetSubAttributeConsumer(attribute).accept(name, attributeSchema::getName);

            SCIMAttributeSchema type = SCIMSchemaDefinitions.SchemaSchemaDefinition.ATTRIBUTES_TYPE;
            getSetSubAttributeConsumer(attribute).accept(type, () -> attributeSchema.getType().name());

            SCIMAttributeSchema multiValued = SCIMSchemaDefinitions.SchemaSchemaDefinition.ATTRIBUTES_MULTI_VALUED;
            getSetSubAttributeConsumer(attribute).accept(multiValued, attributeSchema::getMultiValued);

            SCIMAttributeSchema description = SCIMSchemaDefinitions.SchemaSchemaDefinition.ATTRIBUTES_DESCRIPTION;
            getSetSubAttributeConsumer(attribute).accept(description, attributeSchema::getDescription);

            SCIMAttributeSchema required = SCIMSchemaDefinitions.SchemaSchemaDefinition.ATTRIBUTES_REQUIRED;
            getSetSubAttributeConsumer(attribute).accept(required, attributeSchema::getRequired);

            SCIMAttributeSchema caseExact = SCIMSchemaDefinitions.SchemaSchemaDefinition.ATTRIBUTES_CASE_EXACT;
            getSetSubAttributeConsumer(attribute).accept(caseExact, attributeSchema::getCaseExact);

            SCIMAttributeSchema mutability = SCIMSchemaDefinitions.SchemaSchemaDefinition.ATTRIBUTES_MUTABILITY;
            getSetSubAttributeConsumer(attribute).accept(mutability,
                () -> attributeSchema.getMutability().name().toLowerCase(Locale.ENGLISH));

            SCIMAttributeSchema returned = SCIMSchemaDefinitions.SchemaSchemaDefinition.ATTRIBUTES_RETURNED;
            getSetSubAttributeConsumer(attribute).accept(returned,
                () -> attributeSchema.getReturned().name().toLowerCase(Locale.ENGLISH));

            SCIMAttributeSchema uniqueness = SCIMSchemaDefinitions.SchemaSchemaDefinition.ATTRIBUTES_UNIQUENESS;
            getSetSubAttributeConsumer(attribute).accept(uniqueness,
                () -> attribute.getUniqueness().name().toLowerCase(Locale.ENGLISH));

            SCIMAttributeSchema canonical = SCIMSchemaDefinitions.SchemaSchemaDefinition.ATTRIBUTES_CANONICAL_VALUES;
            MultiValuedAttribute canonicalAttribute = new MultiValuedAttribute(canonical.getName());
            rethrowConsumer(o -> DefaultAttributeFactory.createAttribute(canonical, (AbstractAttribute) o)).accept(
                canonicalAttribute);
            Optional.ofNullable(attributeSchema.getCanonicalValues()).ifPresent(referenceTypes -> {
                canonicalAttribute.setAttributePrimitiveValues(
                    referenceTypes.stream().map(t -> (Object) t).collect(Collectors.toList()));
            });
            rethrowConsumer(o -> attribute.setSubAttribute((Attribute) o)).accept(canonicalAttribute);

            SCIMAttributeSchema reference = SCIMSchemaDefinitions.SchemaSchemaDefinition.ATTRIBUTES_REFERENCE_TYPES;
            MultiValuedAttribute referenceAttribute = new MultiValuedAttribute(reference.getName());
            rethrowConsumer(o -> DefaultAttributeFactory.createAttribute(reference, (AbstractAttribute) o)).accept(
                referenceAttribute);
            Optional.ofNullable(attributeSchema.getReferenceTypes()).ifPresent(referenceTypes -> {
                referenceAttribute.setAttributePrimitiveValues(
                    referenceTypes.stream().map(t -> (Object) t.name()).collect(Collectors.toList()));
            });
            rethrowConsumer(o -> attribute.setSubAttribute((Attribute) o)).accept(referenceAttribute);

            if (SCIMDefinitions.DataType.COMPLEX.equals(attributeSchema.getType())) {
                SCIMAttributeSchema subAttributeDef =
                    SCIMSchemaDefinitions.SchemaSchemaDefinition.ATTRIBUTES_SUB_ATTRIBUTES;
                MultiValuedAttribute subAttributes = new MultiValuedAttribute(subAttributeDef.getName());
                rethrowConsumer(o -> DefaultAttributeFactory.createAttribute(subAttributeDef, (AbstractAttribute) o))
                    .accept(subAttributes);
                setAttributes(attributeSchema.getSubAttributeSchemas(), subAttributeDef, subAttributes);
                attribute.setSubAttribute(subAttributes);
            }

            attributes.setAttributeValue(attribute);
        }
    }
}
