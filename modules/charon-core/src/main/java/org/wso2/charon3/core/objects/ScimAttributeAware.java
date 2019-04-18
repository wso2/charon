/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package org.wso2.charon3.core.objects;

import org.wso2.charon3.core.attributes.Attribute;
import org.wso2.charon3.core.attributes.ComplexAttribute;
import org.wso2.charon3.core.attributes.DefaultAttributeFactory;
import org.wso2.charon3.core.attributes.MultiValuedAttribute;
import org.wso2.charon3.core.attributes.SimpleAttribute;
import org.wso2.charon3.core.objects.plainobjects.MultiValuedComplexType;
import org.wso2.charon3.core.schema.SCIMAttributeSchema;
import org.wso2.charon3.core.schema.SCIMDefinitions;
import org.wso2.charon3.core.schema.SCIMResourceTypeExtensionSchema;
import org.wso2.charon3.core.schema.SCIMSchemaDefinitions;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import static org.wso2.charon3.core.utils.LambdaExceptionUtils.rethrowBiConsumer;
import static org.wso2.charon3.core.utils.LambdaExceptionUtils.rethrowConsumer;
import static org.wso2.charon3.core.utils.LambdaExceptionUtils.rethrowFunction;
import static org.wso2.charon3.core.utils.LambdaExceptionUtils.rethrowSupplier;

/**
 * This class is used as a helper implementation and shall provide additional functionality to the
 * {@link AbstractSCIMObject} that will help reading, replacing attributes from objects and also an equals
 * implementation of {@link AbstractSCIMObject}s is added.
 */
public abstract class ScimAttributeAware {

    /**
     * checks that two given attributes are equals by running through their structure recursively
     *
     * @return true if the given attributes are equals, false else
     */
    public static boolean attributesEquals(Attribute attribute,
                                           Attribute attributeOther) {

        if (!attribute.getClass().equals(attributeOther.getClass())) {
            return false;
        }
        if (!attributeMetaEquals(attribute, attributeOther)) {
            return false;
        }
        if (attribute instanceof SimpleAttribute && attributeOther instanceof SimpleAttribute) {
            return simpleAttributeEquals((SimpleAttribute) attribute, (SimpleAttribute) attributeOther);
        } else if (attribute instanceof MultiValuedAttribute && attributeOther instanceof MultiValuedAttribute) {
            return multiValuedAttributeEquals((MultiValuedAttribute) attribute, (MultiValuedAttribute) attributeOther);
        } else if (attribute instanceof ComplexAttribute && attributeOther instanceof ComplexAttribute) {
            return complexAttributeEquals((ComplexAttribute) attribute, (ComplexAttribute) attributeOther);
        }
        return false;
    }

    /**
     * tells us if the given two attributes do contain the same meta-data
     *
     * @return if the meta-data is identical
     */
    public static boolean attributeMetaEquals(Attribute attribute,
                                              Attribute attributeOther) {

        if (!attribute.getMultiValued().equals(attributeOther.getMultiValued())) {
            return false;
        }
        if (!attribute.getCaseExact().equals(attributeOther.getCaseExact())) {
            return false;
        }
        if (!attribute.getRequired().equals(attributeOther.getRequired())) {
            return false;
        }
        if (!attribute.getMutability().equals(attributeOther.getMutability())) {
            return false;
        }
        if (!attribute.getReturned().equals(attributeOther.getReturned())) {
            return false;
        }
        if (!attribute.getUniqueness().equals(attributeOther.getUniqueness())) {
            return false;
        }
        if (!attribute.getURI().equals(attributeOther.getURI())) {
            return false;
        }
        return attribute.getType().equals(attributeOther.getType());
    }

    /**
     * tells us if two simple attributes are identical
     *
     * @return true if the attributes are identical, false else
     */
    public static boolean simpleAttributeEquals(SimpleAttribute attribute1,
                                                SimpleAttribute attribute2) {

        if (!attribute1.getValue().equals(attribute2.getValue())) {
            return false;
        }
        return attributeMetaEquals(attribute1, attribute2);
    }

    /**
     * tells us if two simple attributes are identical
     *
     * @return true if the attributes are identical, false else
     */
    public static boolean multiValuedAttributeEquals(MultiValuedAttribute attribute,
                                                     MultiValuedAttribute otherAttribute) {

        boolean metaEquals = attributeMetaEquals(attribute, otherAttribute);
        if (!metaEquals) {
            return false;
        }
        if (attribute.getAttributePrimitiveValues().isEmpty()) {
            if (attribute.getAttributeValues().size() != otherAttribute.getAttributeValues().size()) {
                return false;
            }
            return attribute.getAttributeValues().stream().allMatch(innerAttribute -> {
                return otherAttribute.getAttributeValues().stream().anyMatch(
                        otherInnerAttribute -> attributesEquals(innerAttribute, otherInnerAttribute));
            });
        } else {
            return attribute.getAttributePrimitiveValues().containsAll(otherAttribute.getAttributePrimitiveValues());
        }
    }

    /**
     * tells us if two complex attributes are identical or not
     *
     * @return true if the attributes are identical, false else
     */
    public static boolean complexAttributeEquals(ComplexAttribute attribute,
                                                 ComplexAttribute otherAttribute) {

        boolean metaDataEquals = attributeMetaEquals(attribute, otherAttribute);
        if (!metaDataEquals) {
            return false;
        }

        // @formatter:off
        return attribute.getSubAttributesList().keySet().stream().allMatch(attributeName -> {
            return otherAttribute.getSubAttributesList().keySet().stream().anyMatch(oAttributeName -> rethrowFunction(
                    otherAttributeName -> attributesEquals(attribute.getSubAttribute(attributeName),
                            otherAttribute.getSubAttribute((String) otherAttributeName)))
                    .apply(oAttributeName));
        });
        // @formatter:on
    }

    /**
     * @return the id of the SCIM {@link #getResource()}
     */
    public String getId() {

        SCIMAttributeSchema idDefinition = SCIMSchemaDefinitions.ID;
        return getSimpleAttribute(idDefinition).map(rethrowFunction(SimpleAttribute::getStringValue)).orElse(null);
    }

    /**
     * sets or overrides the id attribute of the given {@link #getResource()} object
     *
     * @param id the id attribute to write
     */
    public void replaceId(String id) {

        SCIMAttributeSchema externalIdDefinition = SCIMSchemaDefinitions.ID;
        replaceSimpleAttribute(externalIdDefinition, id);
    }

    /**
     * @return the external id of the SCIM {@link #getResource()}
     */
    public String getExternalId() {

        SCIMAttributeSchema idDefinition = SCIMSchemaDefinitions.EXTERNAL_ID;
        return getSimpleAttribute(idDefinition).map(rethrowFunction(SimpleAttribute::getStringValue)).orElse(null);
    }

    /**
     * sets or overrides the external id attribute of the given {@link #getResource()} object
     *
     * @param externalId the external id attribute to write
     */
    public void replaceExternalId(String externalId) {

        SCIMAttributeSchema externalIdDefinition = SCIMSchemaDefinitions.EXTERNAL_ID;
        replaceSimpleAttribute(externalIdDefinition, externalId);
    }

    /**
     * @return the resource type of the SCIM {@link #getResource()}
     */
    public String getResourceType() {

        SCIMAttributeSchema metaDefinition = SCIMSchemaDefinitions.META;
        SCIMAttributeSchema resourceTypeDefinition = SCIMSchemaDefinitions.RESOURCE_TYPE;
        return getComplexAttribute(metaDefinition).map(meta -> getSimpleAttribute(resourceTypeDefinition, meta)
                .map(rethrowFunction(SimpleAttribute::getStringValue)).orElse(null)).orElse(null);
    }

    /**
     * sets or overrides the resource type attribute of the given {@link #getResource()} object
     *
     * @param resourceType the resource type attribute to write
     */
    public void replaceResourceType(String resourceType) {

        SCIMAttributeSchema metaDefinition = SCIMSchemaDefinitions.META;
        SCIMAttributeSchema resourceTypeDefinition = SCIMSchemaDefinitions.RESOURCE_TYPE;
        ComplexAttribute meta = getOrCrateComplexAttribute(metaDefinition);
        getSetSubAttributeConsumer(meta).accept(resourceTypeDefinition, () -> resourceType);
    }

    /**
     * @return the location of the SCIM {@link #getResource()}
     */
    public String getLocation() {

        SCIMAttributeSchema metaDefinition = SCIMSchemaDefinitions.META;
        SCIMAttributeSchema resourceTypeDefinition = SCIMSchemaDefinitions.LOCATION;
        return getComplexAttribute(metaDefinition).map(meta -> getSimpleAttribute(resourceTypeDefinition, meta)
                .map(rethrowFunction(SimpleAttribute::getStringValue)).orElse(null)).orElse(null);
    }

    /**
     * sets or overrides the location attribute of the given {@link #getResource()} object
     *
     * @param resourceType the location attribute to write
     */
    public void replaceLocation(String resourceType) {

        SCIMAttributeSchema metaDefinition = SCIMSchemaDefinitions.META;
        SCIMAttributeSchema locationDefinition = SCIMSchemaDefinitions.LOCATION;
        ComplexAttribute meta = getOrCrateComplexAttribute(metaDefinition);
        getSetSubAttributeConsumer(meta).accept(locationDefinition, () -> resourceType);
    }

    /**
     * @return the created timestamp as long of the SCIM {@link #getResource()} in UTC
     */
    public Long getCreatedLong() {

        SCIMAttributeSchema metaDefinition = SCIMSchemaDefinitions.META;
        SCIMAttributeSchema resourceTypeDefinition = SCIMSchemaDefinitions.CREATED;
        return getComplexAttribute(metaDefinition).map(meta -> getSimpleAttribute(resourceTypeDefinition, meta)
                .map(rethrowFunction(simpleAttribute -> simpleAttribute.getDateValue().getTime())).orElse(null))
                .orElse(null);
    }

    /**
     * @return the created timestamp as {@link LocalDateTime} of the SCIM {@link #getResource()}
     */
    public LocalDateTime getCreatedDateTime() {

        SCIMAttributeSchema metaDefinition = SCIMSchemaDefinitions.META;
        SCIMAttributeSchema createdDefinition = SCIMSchemaDefinitions.CREATED;
        return getComplexAttribute(metaDefinition).map(meta -> {
            return getSimpleAttribute(createdDefinition, meta).map(rethrowFunction(SimpleAttribute::getInstantValue))
                    .map(instant -> LocalDateTime.ofInstant(instant, TimeZone.getDefault().
                            toZoneId())).orElse(null);
        }).orElse(null);
    }

    /**
     * @return the created timestamp as {@link Instant} of the SCIM {@link #getResource()} in UTC
     */
    public Instant getCreatedInstant() {

        SCIMAttributeSchema metaDefinition = SCIMSchemaDefinitions.META;
        SCIMAttributeSchema createdDefinition = SCIMSchemaDefinitions.CREATED;
        return getComplexAttribute(metaDefinition).map(meta -> {
            return getSimpleAttribute(createdDefinition, meta).map(rethrowFunction(SimpleAttribute::getInstantValue))
                    .orElse(null);
        }).orElse(null);
    }

    /**
     * sets the created date into the given SCIM resource
     *
     * @param createdDate the java local date time representaiton
     */
    public void replaceCreated(LocalDateTime createdDate) {

        SCIMAttributeSchema metaDefinition = SCIMSchemaDefinitions.META;
        SCIMAttributeSchema createdDefinition = SCIMSchemaDefinitions.CREATED;
        ComplexAttribute meta = getOrCrateComplexAttribute(metaDefinition);
        getSetSubAttributeConsumer(meta).accept(createdDefinition, () ->
                createdDate.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * sets the created date into the given SCIM resource
     *
     * @param createdDate the java local date time representaiton
     */
    public void replaceCreated(Instant createdDate) {

        SCIMAttributeSchema metaDefinition = SCIMSchemaDefinitions.META;
        SCIMAttributeSchema createdDefinition = SCIMSchemaDefinitions.CREATED;
        ComplexAttribute meta = getOrCrateComplexAttribute(metaDefinition);
        getSetSubAttributeConsumer(meta).accept(createdDefinition, () -> createdDate);
    }

    /**
     * sets the created date into the given SCIM resource as localized date string based on the current system
     * time
     *
     * @param createdTimestamp the UTC timestamp as long
     */
    public void replaceCreated(Long createdTimestamp) {

        SCIMAttributeSchema metaDefinition = SCIMSchemaDefinitions.META;
        SCIMAttributeSchema createdDefinition = SCIMSchemaDefinitions.CREATED;
        ComplexAttribute meta = getOrCrateComplexAttribute(metaDefinition);
        Instant created = Instant.ofEpochMilli(createdTimestamp);
        getSetSubAttributeConsumer(meta).accept(createdDefinition, () -> created);
    }

    /**
     * @return the last modified timestamp as {@link LocalDateTime} of the SCIM {@link #getResource()}
     */
    public LocalDateTime getLastModifiedDateTime() {

        SCIMAttributeSchema metaDefinition = SCIMSchemaDefinitions.META;
        SCIMAttributeSchema lastModifiedDefinition = SCIMSchemaDefinitions.LAST_MODIFIED;
        return getComplexAttribute(metaDefinition).map(meta -> {
            return getSimpleAttribute(lastModifiedDefinition, meta)
                    .map(rethrowFunction(SimpleAttribute::getInstantValue))
                    .map(instant -> LocalDateTime
                            .ofInstant(instant, TimeZone.getDefault().toZoneId()))
                    .orElse(null);
        }).orElse(null);
    }

    /**
     * @return the last modified timestamp as {@link LocalDateTime} of the SCIM {@link #getResource()} in UTC
     */
    public Instant getLastModifiedInstant() {

        SCIMAttributeSchema metaDefinition = SCIMSchemaDefinitions.META;
        SCIMAttributeSchema lastModifiedDefinition = SCIMSchemaDefinitions.LAST_MODIFIED;
        return getComplexAttribute(metaDefinition).map(meta -> {
            return getSimpleAttribute(lastModifiedDefinition, meta)
                    .map(rethrowFunction(SimpleAttribute::getInstantValue))
                    .orElse(null);
        }).orElse(null);
    }

    /**
     * @return the created timestamp as long of the SCIM {@link #getResource()} in UTC
     */
    public Long getLastModifiedLong() {

        SCIMAttributeSchema metaDefinition = SCIMSchemaDefinitions.META;
        SCIMAttributeSchema lastModifiedDefinition = SCIMSchemaDefinitions.LAST_MODIFIED;
        return getComplexAttribute(metaDefinition).map(meta ->
                getSimpleAttribute(lastModifiedDefinition, meta)
                        .map(rethrowFunction(simpleAttribute -> simpleAttribute.getInstantValue().toEpochMilli()))
                        .orElse(null))
                .orElse(null);
    }

    /**
     * sets the last modified date into the given SCIM resource
     *
     * @param lastModifiedDateTime the java local date time representaiton
     */
    public void replaceLastModified(LocalDateTime lastModifiedDateTime) {

        SCIMAttributeSchema metaDefinition = SCIMSchemaDefinitions.META;
        SCIMAttributeSchema lastModifiedDefinition = SCIMSchemaDefinitions.LAST_MODIFIED;
        ComplexAttribute meta = getOrCrateComplexAttribute(metaDefinition);
        getSetSubAttributeConsumer(meta).accept(lastModifiedDefinition,
                () -> lastModifiedDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * sets the last modified date into the given SCIM resource
     *
     * @param lastModifiedInstant the java local date time representaiton
     */
    public void replaceLastModified(Instant lastModifiedInstant) {

        SCIMAttributeSchema metaDefinition = SCIMSchemaDefinitions.META;
        SCIMAttributeSchema lastModifiedDefinition = SCIMSchemaDefinitions.LAST_MODIFIED;
        ComplexAttribute meta = getOrCrateComplexAttribute(metaDefinition);
        getSetSubAttributeConsumer(meta).accept(lastModifiedDefinition, () -> lastModifiedInstant);
    }

    /**
     * sets the last modified timestamp date into the given SCIM resource as localized date string based on the
     * current system time
     *
     * @param lastModifiedTimestamp the UTC timestamp as long
     */
    public void replaceLastModified(Long lastModifiedTimestamp) {

        SCIMAttributeSchema metaDefinition = SCIMSchemaDefinitions.META;
        SCIMAttributeSchema lastModifiedDefinition = SCIMSchemaDefinitions.LAST_MODIFIED;
        ComplexAttribute meta = getOrCrateComplexAttribute(metaDefinition);
        getSetSubAttributeConsumer(meta).accept(lastModifiedDefinition,
                () -> Instant.ofEpochMilli(lastModifiedTimestamp));
    }

    /**
     * gets a {@link SimpleAttribute} from the given {@link #getResource()} object
     *
     * @param scimAttributeSchema the attribute that should be read from the {@link #getResource()}
     * @return the attribute from the {@link #getResource()} or an empty
     */
    public Optional<SimpleAttribute> getSimpleAttribute(SCIMAttributeSchema scimAttributeSchema) {

        if (scimAttributeSchema == null) {
            return Optional.empty();
        }
        return Optional.ofNullable((SimpleAttribute) getResource().getAttribute(scimAttributeSchema.getName()));
    }

    /**
     * gets a {@link SimpleAttribute} from the given {@code complexAttribute} object
     *
     * @param scimAttributeSchema the attribute that should be read from the {@code complexAttribute}
     * @param complexAttribute    the attribute that should be read from the {@code complexAttribute}
     * @return the attribute from the {@code complexAttribute} or an empty
     */
    public Optional<SimpleAttribute> getSimpleAttribute(SCIMAttributeSchema scimAttributeSchema,
                                                        ComplexAttribute complexAttribute) {

        if (scimAttributeSchema == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(
                rethrowSupplier(() -> (SimpleAttribute) complexAttribute.getSubAttribute(scimAttributeSchema.getName()))
                        .get());
    }

    /**
     * gets a {@link SimpleAttribute} from the given {@code complexAttribute} object
     *
     * @param scimAttributeSchema the attribute that should be read from the {@code complexAttribute}
     * @param complexAttribute    the attribute that should be read from the {@code complexAttribute} if
     *                            (scimAttributeSchema == null || complexAttribute == null) { return Optional.empty(); }
     * @return the value from the {@code complexAttribute} or an empty
     */
    public Optional<String> getSimpleAttributeValue(SCIMAttributeSchema scimAttributeSchema,
                                                    ComplexAttribute complexAttribute) {

        if (scimAttributeSchema == null || complexAttribute == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(
                rethrowSupplier(() -> (SimpleAttribute) complexAttribute.getSubAttribute(scimAttributeSchema.getName()))
                        .get()).map(simpleAttribute -> rethrowFunction(sa -> {
            if (SCIMDefinitions.DataType.REFERENCE.equals(scimAttributeSchema.getType())) {
                return (String) simpleAttribute.getValue();
            } else {
                return simpleAttribute.getStringValue();
            }
        }).apply(simpleAttribute)).map(s -> s.trim().isEmpty() ? null : s);
    }

    /**
     * gets a {@link SimpleAttribute} from the given {@code complexAttribute} object
     *
     * @param scimAttributeSchema the attribute that should be read from the {@code complexAttribute}
     * @param complexAttribute    the attribute that should be read from the {@code complexAttribute} if
     *                            (scimAttributeSchema == null || complexAttribute == null) { return Optional.empty(); }
     * @return the value from the {@code complexAttribute} or an empty
     */
    public Optional<Boolean> getSimpleAttributeBooleanValue(SCIMAttributeSchema scimAttributeSchema,
                                                            ComplexAttribute complexAttribute) {

        if (scimAttributeSchema == null || complexAttribute == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(
                rethrowSupplier(() -> (SimpleAttribute) complexAttribute.getSubAttribute(scimAttributeSchema.getName()))
                        .get()).map(simpleAttribute -> rethrowFunction(sa -> {
                return simpleAttribute.getBooleanValue();
        }).apply(simpleAttribute));
    }

    /**
     * gets a {@link SimpleAttribute} from the given {@code complexAttribute} object
     *
     * @param scimAttributeSchema the attribute that should be read from the {@code complexAttribute}
     * @param complexAttribute    the attribute that should be read from the {@code complexAttribute} if
     *                            (scimAttributeSchema == null || complexAttribute == null) { return Optional.empty(); }
     * @return the value from the {@code complexAttribute} or an empty
     */
    public Optional<Integer> getSimpleAttributeIntegerValue(SCIMAttributeSchema scimAttributeSchema,
                                                            ComplexAttribute complexAttribute) {

        if (scimAttributeSchema == null || complexAttribute == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(
                rethrowSupplier(() -> (SimpleAttribute) complexAttribute.getSubAttribute(scimAttributeSchema.getName()))
                        .get()).map(simpleAttribute -> rethrowFunction(sa -> {
                return (Integer) simpleAttribute.getValue();
        }).apply(simpleAttribute));
    }

    /**
     * gets a {@link ComplexAttribute} from the given {@link #getResource()} object
     *
     * @param scimAttributeSchema the attribute that should be read from the {@link #getResource()}
     * @return the attribute from the {@link #getResource()} or an empty
     */
    public Optional<ComplexAttribute> getComplexAttribute(SCIMAttributeSchema scimAttributeSchema) {

        return Optional.ofNullable((ComplexAttribute) getResource().getAttribute(scimAttributeSchema.getName()));
    }

    /**
     * gets a {@link ComplexAttribute} from the given {@link #getResource()} object
     *
     * @param attributeName the attribute name that should be read from the {@link #getResource()}
     * @return the attribute from the {@link #getResource()} or an empty
     */
    public Optional<ComplexAttribute> getComplexAttribute(String attributeName) {

        return Optional.ofNullable((ComplexAttribute) getResource().getAttribute(attributeName));
    }

    /**
     * gets a {@link ComplexAttribute} from the given {@link #getResource()} object if it does exist and will
     * create it if it does not exist
     *
     * @param scimAttributeSchema the attribute that should be read from the {@link #getResource()}
     * @return the attribute from the {@link #getResource()} or a new attribute that will also be added to the
     * {@link #getResource()} object
     */
    public ComplexAttribute getOrCrateComplexAttribute(SCIMAttributeSchema scimAttributeSchema) {
        // @formatter:off
        Optional<ComplexAttribute> attribute = Optional
                .ofNullable((ComplexAttribute) getResource().getAttribute(scimAttributeSchema.getName()));
        // @formatter:on
        if (attribute.isPresent()) {
            return attribute.get();
        } else {
            ComplexAttribute complexAttribute = new ComplexAttribute(scimAttributeSchema.getName());
            rethrowBiConsumer(DefaultAttributeFactory::createAttribute).accept(scimAttributeSchema, complexAttribute);
            getResource().setAttribute(complexAttribute);
            return complexAttribute;
        }
    }

    /**
     * gets a {@link MultiValuedAttribute} from the given {@link #getResource()} object if it does exist and will
     * create it if it does not exist
     *
     * @param scimAttributeSchema the attribute that should be read from the {@link #getResource()}
     * @return the attribute from the {@link #getResource()} or a new attribute that will also be added to the
     * {@link #getResource()} object
     */
    public MultiValuedAttribute getOrCrateMultivaluedAttribute(SCIMAttributeSchema scimAttributeSchema) {
        // @formatter:off
        Optional<MultiValuedAttribute> attribute = Optional
                .ofNullable((MultiValuedAttribute) getResource().getAttribute(scimAttributeSchema.getName()));
        // @formatter:on
        if (attribute.isPresent()) {
            return attribute.get();
        } else {
            MultiValuedAttribute complexAttribute = new MultiValuedAttribute(scimAttributeSchema.getName());
            rethrowBiConsumer(DefaultAttributeFactory::createAttribute).accept(scimAttributeSchema, complexAttribute);
            getResource().setAttribute(complexAttribute);
            return complexAttribute;
        }
    }

    /**
     * gets a {@link MultiValuedAttribute} from the given {@link #getResource()} object
     *
     * @param scimAttributeSchema the attribute that should be read from the {@link #getResource()}
     * @return the attribute from the {@link #getResource()} or an empty
     */
    public Optional<MultiValuedAttribute> getMultiValuedAttribute(SCIMAttributeSchema scimAttributeSchema) {

        return Optional.ofNullable((MultiValuedAttribute) getResource().getAttribute(scimAttributeSchema.getName()));
    }

    /**
     * sets a {@link SimpleAttribute} for the given {@link #getResource()}
     *
     * @param scimAttributeSchema the attribute to set
     * @param value               the value that represents the attribute
     */
    public void replaceSimpleAttribute(SCIMAttributeSchema scimAttributeSchema,
                                       Object value) {

        if (scimAttributeSchema == null || value == null) {
            if (scimAttributeSchema != null) {
                getResource().deleteAttribute(scimAttributeSchema.getName());
            }
            return;
        }
        SimpleAttribute simpleAttribute = new SimpleAttribute(scimAttributeSchema.getName(), value);
        rethrowBiConsumer(DefaultAttributeFactory::createAttribute).accept(scimAttributeSchema, simpleAttribute);
        getResource().deleteAttribute(scimAttributeSchema.getName());
        getResource().setAttribute(simpleAttribute);
    }

    /**
     * this method will add a {@link MultiValuedComplexType} list to the given {@link #getResource()}
     *
     * @param multiValuedComplexTypeList the list of attributes that should be added if they are present
     * @param complexDefinition          the definition of the complex attribute that should be added to the
     *                                   {@link #getResource()}
     * @param valueDefinition            the definition of the value-attribute for the given complex-type
     * @param displayDefinition          the definition of the display-attribute for the given complex-type
     * @param typeDefinition             the definition of the type-attribute for the given complex-type
     * @param primaryDefinition          the definition of the primary-attribute for the given complex-type
     * @param referenceDefinition        the definition of the reference-attribute for the given complex-type
     */
    protected void addMultivaluedComplexAtribute(List<MultiValuedComplexType> multiValuedComplexTypeList,
                                                 SCIMAttributeSchema complexDefinition,
                                                 SCIMAttributeSchema valueDefinition,
                                                 SCIMAttributeSchema displayDefinition,
                                                 SCIMAttributeSchema typeDefinition,
                                                 SCIMAttributeSchema primaryDefinition,
                                                 SCIMAttributeSchema referenceDefinition) {

        getResource().deleteAttribute(complexDefinition.getName());

        MultiValuedAttribute multiValuedList = new MultiValuedAttribute(complexDefinition.getName());
        rethrowBiConsumer(DefaultAttributeFactory::createAttribute).accept(complexDefinition, multiValuedList);

        multiValuedComplexTypeList.forEach(complexObject -> {
            ComplexAttribute complexAttribute = new ComplexAttribute(complexDefinition.getName());

            BiConsumer<SCIMAttributeSchema, Supplier<Object>> setSubAttribute = getSetSubAttributeConsumer(
                    complexAttribute);

            setSubAttribute.accept(valueDefinition, complexObject::getValue);
            setSubAttribute.accept(displayDefinition, complexObject::getDisplay);
            setSubAttribute.accept(typeDefinition, complexObject::getType);
            setSubAttribute.accept(primaryDefinition, () -> complexObject.isPrimary() ? true : null);
            setSubAttribute.accept(referenceDefinition, complexObject::getReference);
            multiValuedList.setAttributeValue(complexAttribute);
        });
        getResource().setAttribute(multiValuedList);
    }

    /**
     * returns a {@link BiConsumer} that will add a new {@link SimpleAttribute} to the given
     * {@link ComplexAttribute}
     *
     * @param complexAttribute the {@link ComplexAttribute} that will be extended by a {@link SimpleAttribute}
     * @return the consumer that performs the execution of adding a {@link SimpleAttribute} to the given
     * {@link ComplexAttribute}
     */
    protected BiConsumer<SCIMAttributeSchema, Supplier<Object>> getSetSubAttributeConsumer(
            ComplexAttribute complexAttribute) {

        return (scimAttributeSchema, objectSupplier) -> {
            Optional.ofNullable(scimAttributeSchema).ifPresent(schema -> {
                Optional.ofNullable(objectSupplier.get()).ifPresent(value -> {
                    SimpleAttribute valueAttribute = new SimpleAttribute(schema.getName(), objectSupplier.get());
                    rethrowBiConsumer(DefaultAttributeFactory::createAttribute).accept(schema, valueAttribute);
                    rethrowConsumer(complexAttribute::setSubAttribute).accept(valueAttribute);
                });
            });
        };
    }

    /**
     * extracts a multi-valued-complex-type from the {@link #getResource()} object by the given attribute
     * definitions
     *
     * @param multiValuedDefinition the multi valued complex type definition
     * @param valueDefinition       the value-definition of the multi-valued-complex type
     * @param displayDefinition     the display-definition of the multi-valued-complex type
     * @param typeDefinition        the type-definition of the multi-valued-complex type
     * @param primaryDefinition     the primary-definition of the multi-valued-complex type
     * @param referenceDefinition   the reference-definition of the multi-valued-complex type
     * @return a list of the given {@link MultiValuedComplexType}s
     */
    protected Optional<List<MultiValuedComplexType>> getMultivaluedComplexType(
            SCIMAttributeSchema multiValuedDefinition,
            SCIMAttributeSchema valueDefinition,
            SCIMAttributeSchema displayDefinition,
            SCIMAttributeSchema typeDefinition,
            SCIMAttributeSchema primaryDefinition,
            SCIMAttributeSchema referenceDefinition) {

        return getMultiValuedAttribute(multiValuedDefinition).map(multiValuedAttribute -> {
            List<MultiValuedComplexType> multiValuedComplexTypes = new ArrayList<>();
            for (Attribute attributeValue : multiValuedAttribute.getAttributeValues()) {
                getMultiValuedComplexType((ComplexAttribute) attributeValue,
                        valueDefinition, displayDefinition, typeDefinition,
                        primaryDefinition, referenceDefinition).ifPresent(multiValuedComplexType -> {
                            multiValuedComplexTypes.add(multiValuedComplexType);
                });
            }
            return multiValuedComplexTypes;
        });
    }

    /**
     * parses a {@link ComplexAttribute} into a {@link MultiValuedComplexType}
     * @param attributeValue the complex type to parse
     * @param valueDefinition the value attribute description
     * @param displayDefinition the display attribute description
     * @param typeDefinition the type attribute description
     * @param primaryDefinition the primary attribute description
     * @param referenceDefinition the reference attribute description
     * @return the {@link MultiValuedComplexType} or an empty if no such sub-attributes exist
     */
    public Optional<MultiValuedComplexType> getMultiValuedComplexType(ComplexAttribute attributeValue,
                                                                      SCIMAttributeSchema valueDefinition,
                                                                      SCIMAttributeSchema displayDefinition,
                                                                      SCIMAttributeSchema typeDefinition,
                                                                      SCIMAttributeSchema primaryDefinition,
                                                                      SCIMAttributeSchema referenceDefinition) {
        ComplexAttribute complexAttribute = attributeValue;
        MultiValuedComplexType multiValuedComplexType = new MultiValuedComplexType();
        getSimpleAttributeValue(valueDefinition, complexAttribute).ifPresent(multiValuedComplexType::setValue);
        getSimpleAttributeValue(displayDefinition, complexAttribute)
                .ifPresent(multiValuedComplexType::setDisplay);
        getSimpleAttributeValue(typeDefinition, complexAttribute).ifPresent(multiValuedComplexType::setType);
        getSimpleAttribute(primaryDefinition, complexAttribute)
                .map(rethrowFunction(SimpleAttribute::getBooleanValue))
                .ifPresent(multiValuedComplexType::setPrimary);
        getSimpleAttribute(referenceDefinition, complexAttribute)
                .map(simpleAttribute -> (String) simpleAttribute.getValue())
                .ifPresent(multiValuedComplexType::setReference);

        if (!(isBlank(multiValuedComplexType.getValue()) && isBlank(
                multiValuedComplexType.getDisplay()) && isBlank(multiValuedComplexType.getType()) && isBlank(
                multiValuedComplexType.getReference()) && !multiValuedComplexType.isPrimary())) {
            return Optional.of(multiValuedComplexType);
        }
        return Optional.empty();
    }

    /**
     * this method is used to compare to scim objects
     *
     * @param object the object to compare with this object
     * @return true if both attributes do contain the same attributes and values
     */
    @Override
    public boolean equals(Object object) {

        if (object == null) {
            return false;
        }
        if (!ScimAttributeAware.class.isAssignableFrom(object.getClass())) {
            return false;
        }
        ScimAttributeAware scimAttributeAware = (ScimAttributeAware) object;
        if (!getResource().getSchemaList().equals(scimAttributeAware.getResource().getSchemaList())) {
            return false;
        }
        if (!getResource().getAttributeList().keySet()
                .equals(scimAttributeAware.getResource().getAttributeList().keySet())) {

            return false;
        }

        return getResource().getAttributeList().keySet().stream().allMatch(attributeName -> {
            return attributesEquals(getResource().getAttribute(attributeName),
                    scimAttributeAware.getResource().getAttribute(attributeName));
        });
    }

    @Override
    public int hashCode() {

        return getResource().getAttributeList().hashCode();
    }

    /**
     * tells us if this string is blank or not
     */
    protected boolean isBlank(String s) {

        return s == null || s.trim().isEmpty();
    }

    /**
     * tells us if this string is blank or not
     */
    protected boolean isNotBlank(String s) {

        return s != null && !s.trim().isEmpty();
    }

    /**
     * returns null if this string is blank and removed all whitespaces in the front and at the rear if it is not blank
     */
    protected String stripToNull(String s) {

        return isBlank(s) ? null : s.trim();
    }

    /**
     * gets the value of the attribute in the given resource extension as string value
     *
     * @param extensionSchema the resource schema extension that should hold the attribute
     * @param attributeSchema the attribute to read
     * @return the value of the attribute or null
     * @throws ClassCastException if the attribute to extract is not of type {@link SimpleAttribute}
     */
    public String getExtensionAttributeAsString(SCIMResourceTypeExtensionSchema extensionSchema,
                                                SCIMAttributeSchema attributeSchema) {

        Optional<ComplexAttribute> extensionAttributeOptional = getComplexAttribute(extensionSchema.getSchema());
        if (!extensionAttributeOptional.isPresent()) {
            return null;
        }
        ComplexAttribute complexAttribute = extensionAttributeOptional.get();

        return getSimpleAttributeValue(attributeSchema, complexAttribute).orElse(null);
    }

    /**
     * this method will extract a complex attribute from another complex-attribute that is defined just like a
     * {@link MultiValuedComplexType}. <br>
     * <br>
     * <b>NOTE:</b><br>
     * this method is actually a specification violation because SCIM does not allow complex attributes
     * within complex attributes. But since extensions are an exception in their representation this method ignores
     * this violation to get complex-attributes from an extension
     * @param extensionSchema the schema extension that might hold the complex attribute
     * @param attributeSchema the complex schema definition of the schema extension
     * @param valueDefinition
     * @param displayDefinition
     * @param typeDefinition
     * @param primaryDefinition
     * @param referenceDefinition
     * @see <a href="https://tools.ietf.org/html/rfc7643#section-2.3.8">
     *     https://tools.ietf.org/html/rfc7643#section-2.3.8
     *     </a>
     * @return
     */
    public MultiValuedComplexType getExtensionAttributeAsComplexType(SCIMResourceTypeExtensionSchema extensionSchema,
                                                                     SCIMAttributeSchema attributeSchema,
                                                                     SCIMAttributeSchema valueDefinition,
                                                                     SCIMAttributeSchema displayDefinition,
                                                                     SCIMAttributeSchema typeDefinition,
                                                                     SCIMAttributeSchema primaryDefinition,
                                                                     SCIMAttributeSchema referenceDefinition) {

        Optional<ComplexAttribute> extensionAttributeOptional = getComplexAttribute(extensionSchema.getSchema());
        if (!extensionAttributeOptional.isPresent()) {
            return null;
        }
        ComplexAttribute extensionAttribute = extensionAttributeOptional.get();
        Attribute extensionSubAttribute = rethrowFunction(name -> extensionAttribute.getSubAttribute((String) name))
                .apply(attributeSchema.getName());
        ComplexAttribute complexSubAttribute = (ComplexAttribute) extensionSubAttribute;
        return getMultiValuedComplexType(complexSubAttribute, valueDefinition, displayDefinition, typeDefinition,
                                         primaryDefinition, referenceDefinition).orElse(null);
    }

    /**
     * @return the SCIM resource object that is used for in the given implementation class
     */
    public abstract AbstractSCIMObject getResource();
}
