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

import org.apache.commons.lang3.StringUtils;
import org.wso2.charon3.core.attributes.*;
import org.wso2.charon3.core.exceptions.InternalErrorException;
import org.wso2.charon3.core.objects.plainobjects.Meta;
import org.wso2.charon3.core.objects.plainobjects.MultiValuedComplexType;
import org.wso2.charon3.core.schema.SCIMAttributeSchema;
import org.wso2.charon3.core.schema.SCIMDefinitions;
import org.wso2.charon3.core.schema.SCIMResourceTypeExtensionSchema;
import org.wso2.charon3.core.schema.SCIMSchemaDefinitions;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import static org.wso2.charon3.core.attributes.DefaultAttributeFactory.createAttribute;
import static org.wso2.charon3.core.utils.LambdaExceptionUtils.*;

/**
 * This class is used as a helper implementation and shall provide additional functionality to the {@link
 * AbstractSCIMObject} that will help reading, replacing attributes from objects and also an equals implementation of
 * {@link AbstractSCIMObject}s is added.
 */
public abstract class ScimAttributeAware {

    /**
     * @return the id of the SCIM {@link #getResource()}
     */
    public String getId () {

        SCIMAttributeSchema idDefinition = SCIMSchemaDefinitions.ID;
        return getSimpleAttribute(idDefinition).map(rethrowFunction(SimpleAttribute::getStringValue)).orElse(null);
    }

    /**
     * sets or overrides the id attribute of the given {@link #getResource()} object
     *
     * @param id
     *     the id attribute to write
     */
    public void replaceId (String id) {

        SCIMAttributeSchema externalIdDefinition = SCIMSchemaDefinitions.ID;
        replaceSimpleAttribute(externalIdDefinition, id);
    }

    /**
     * @return the external id of the SCIM {@link #getResource()}
     */
    public String getExternalId () {

        SCIMAttributeSchema idDefinition = SCIMSchemaDefinitions.EXTERNAL_ID;
        return getSimpleAttribute(idDefinition).map(rethrowFunction(SimpleAttribute::getStringValue)).orElse(null);
    }

    /**
     * sets or overrides the external id attribute of the given {@link #getResource()} object
     *
     * @param externalId
     *     the external id attribute to write
     */
    public void replaceExternalId (String externalId) {

        SCIMAttributeSchema externalIdDefinition = SCIMSchemaDefinitions.EXTERNAL_ID;
        replaceSimpleAttribute(externalIdDefinition, externalId);
    }

    /**
     * @return the resource type of the SCIM {@link #getResource()}
     */
    public String getResourceType () {

        SCIMAttributeSchema metaDefinition = SCIMSchemaDefinitions.META;
        SCIMAttributeSchema resourceTypeDefinition = SCIMSchemaDefinitions.RESOURCE_TYPE;
        return getComplexAttribute(metaDefinition).map(meta -> getSimpleAttribute(resourceTypeDefinition, meta).map(
            rethrowFunction(SimpleAttribute::getStringValue)).orElse(null)).orElse(null);
    }

    /**
     * sets or overrides the resource type attribute of the given {@link #getResource()} object
     *
     * @param resourceType
     *     the resource type attribute to write
     */
    public void replaceResourceType (String resourceType) {

        SCIMAttributeSchema metaDefinition = SCIMSchemaDefinitions.META;
        SCIMAttributeSchema resourceTypeDefinition = SCIMSchemaDefinitions.RESOURCE_TYPE;
        ComplexAttribute meta = getOrCrateComplexAttribute(metaDefinition);
        getSetSubAttributeConsumer(meta).accept(resourceTypeDefinition, () -> resourceType);
    }

    /**
     * @return the location of the SCIM {@link #getResource()}
     */
    public String getLocation () {

        SCIMAttributeSchema metaDefinition = SCIMSchemaDefinitions.META;
        SCIMAttributeSchema resourceTypeDefinition = SCIMSchemaDefinitions.LOCATION;
        return getComplexAttribute(metaDefinition).map(meta -> getSimpleAttribute(resourceTypeDefinition, meta).map(
            rethrowFunction(SimpleAttribute::getStringValue)).orElse(null)).orElse(null);
    }

    /**
     * sets or overrides the location attribute of the given {@link #getResource()} object
     *
     * @param resourceType
     *     the location attribute to write
     */
    public void replaceLocation (String resourceType) {

        SCIMAttributeSchema metaDefinition = SCIMSchemaDefinitions.META;
        SCIMAttributeSchema locationDefinition = SCIMSchemaDefinitions.LOCATION;
        ComplexAttribute meta = getOrCrateComplexAttribute(metaDefinition);
        getSetSubAttributeConsumer(meta).accept(locationDefinition, () -> resourceType);
    }

    /**
     * @return the created timestamp as long of the SCIM {@link #getResource()} in UTC
     */
    public Long getCreatedLong () {

        SCIMAttributeSchema metaDefinition = SCIMSchemaDefinitions.META;
        SCIMAttributeSchema resourceTypeDefinition = SCIMSchemaDefinitions.CREATED;
        return getComplexAttribute(metaDefinition).map(meta -> getSimpleAttribute(resourceTypeDefinition, meta).map(
            rethrowFunction(simpleAttribute -> simpleAttribute.getDateValue().getTime())).orElse(null)).orElse(null);
    }

    /**
     * @return the created timestamp as {@link LocalDateTime} of the SCIM {@link #getResource()}
     */
    public LocalDateTime getCreatedDateTime () {

        SCIMAttributeSchema metaDefinition = SCIMSchemaDefinitions.META;
        SCIMAttributeSchema createdDefinition = SCIMSchemaDefinitions.CREATED;
        return getComplexAttribute(metaDefinition).map(meta -> {
            return getSimpleAttribute(createdDefinition, meta).map(rethrowFunction(SimpleAttribute::getInstantValue))
                .map(instant -> LocalDateTime.ofInstant(instant,
                    TimeZone.getDefault().
                        toZoneId())).orElse(null);
        }).orElse(null);
    }

    /**
     * @return the created timestamp as {@link Instant} of the SCIM {@link #getResource()} in UTC
     */
    public Instant getCreatedInstant () {

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
     * @param createdDate
     *     the java local date time representaiton
     */
    public void replaceCreated (LocalDateTime createdDate) {

        SCIMAttributeSchema metaDefinition = SCIMSchemaDefinitions.META;
        SCIMAttributeSchema createdDefinition = SCIMSchemaDefinitions.CREATED;
        ComplexAttribute meta = getOrCrateComplexAttribute(metaDefinition);
        getSetSubAttributeConsumer(meta).accept(createdDefinition,
            () -> createdDate.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * sets the created date into the given SCIM resource
     *
     * @param createdDate
     *     the java local date time representaiton
     */
    public void replaceCreated (Instant createdDate) {

        SCIMAttributeSchema metaDefinition = SCIMSchemaDefinitions.META;
        SCIMAttributeSchema createdDefinition = SCIMSchemaDefinitions.CREATED;
        ComplexAttribute meta = getOrCrateComplexAttribute(metaDefinition);
        getSetSubAttributeConsumer(meta).accept(createdDefinition, () -> createdDate);
    }

    /**
     * sets the created date into the given SCIM resource as localized date string based on the current system time
     *
     * @param createdTimestamp
     *     the UTC timestamp as long
     */
    public void replaceCreated (Long createdTimestamp) {

        SCIMAttributeSchema metaDefinition = SCIMSchemaDefinitions.META;
        SCIMAttributeSchema createdDefinition = SCIMSchemaDefinitions.CREATED;
        ComplexAttribute meta = getOrCrateComplexAttribute(metaDefinition);
        Instant created = Instant.ofEpochMilli(createdTimestamp);
        getSetSubAttributeConsumer(meta).accept(createdDefinition, () -> created);
    }

    /**
     * @return the last modified timestamp as {@link LocalDateTime} of the SCIM {@link #getResource()}
     */
    public LocalDateTime getLastModifiedDateTime () {

        SCIMAttributeSchema metaDefinition = SCIMSchemaDefinitions.META;
        SCIMAttributeSchema lastModifiedDefinition = SCIMSchemaDefinitions.LAST_MODIFIED;
        return getComplexAttribute(metaDefinition).map(meta -> {
            return getSimpleAttribute(lastModifiedDefinition, meta).map(
                rethrowFunction(SimpleAttribute::getInstantValue)).map(
                instant -> LocalDateTime.ofInstant(instant, TimeZone.getDefault().toZoneId())).orElse(null);
        }).orElse(null);
    }

    /**
     * @return the last modified timestamp as {@link LocalDateTime} of the SCIM {@link #getResource()} in UTC
     */
    public Instant getLastModifiedInstant () {

        SCIMAttributeSchema metaDefinition = SCIMSchemaDefinitions.META;
        SCIMAttributeSchema lastModifiedDefinition = SCIMSchemaDefinitions.LAST_MODIFIED;
        return getComplexAttribute(metaDefinition).map(meta -> {
            return getSimpleAttribute(lastModifiedDefinition, meta).map(
                rethrowFunction(SimpleAttribute::getInstantValue)).orElse(null);
        }).orElse(null);
    }

    /**
     * @return the created timestamp as long of the SCIM {@link #getResource()} in UTC
     */
    public Long getLastModifiedLong () {

        SCIMAttributeSchema metaDefinition = SCIMSchemaDefinitions.META;
        SCIMAttributeSchema lastModifiedDefinition = SCIMSchemaDefinitions.LAST_MODIFIED;
        return getComplexAttribute(metaDefinition).map(meta -> getSimpleAttribute(lastModifiedDefinition, meta).map(
            rethrowFunction(simpleAttribute -> simpleAttribute.getInstantValue().toEpochMilli())).orElse(null)).orElse(
            null);
    }

    /**
     * sets the last modified date into the given SCIM resource
     *
     * @param lastModifiedDateTime
     *     the java local date time representaiton
     */
    public void replaceLastModified (LocalDateTime lastModifiedDateTime) {

        SCIMAttributeSchema metaDefinition = SCIMSchemaDefinitions.META;
        SCIMAttributeSchema lastModifiedDefinition = SCIMSchemaDefinitions.LAST_MODIFIED;
        ComplexAttribute meta = getOrCrateComplexAttribute(metaDefinition);
        getSetSubAttributeConsumer(meta).accept(lastModifiedDefinition,
            () -> lastModifiedDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * sets the last modified date into the given SCIM resource
     *
     * @param lastModifiedInstant
     *     the java local date time representaiton
     */
    public void replaceLastModified (Instant lastModifiedInstant) {

        SCIMAttributeSchema metaDefinition = SCIMSchemaDefinitions.META;
        SCIMAttributeSchema lastModifiedDefinition = SCIMSchemaDefinitions.LAST_MODIFIED;
        ComplexAttribute meta = getOrCrateComplexAttribute(metaDefinition);
        getSetSubAttributeConsumer(meta).accept(lastModifiedDefinition, () -> lastModifiedInstant);
    }

    /**
     * sets the last modified timestamp date into the given SCIM resource as localized date string based on the current
     * system time
     *
     * @param lastModifiedTimestamp
     *     the UTC timestamp as long
     */
    public void replaceLastModified (Long lastModifiedTimestamp) {

        SCIMAttributeSchema metaDefinition = SCIMSchemaDefinitions.META;
        SCIMAttributeSchema lastModifiedDefinition = SCIMSchemaDefinitions.LAST_MODIFIED;
        ComplexAttribute meta = getOrCrateComplexAttribute(metaDefinition);
        getSetSubAttributeConsumer(meta).accept(lastModifiedDefinition,
            () -> Instant.ofEpochMilli(lastModifiedTimestamp));
    }

    /**
     * gets a {@link SimpleAttribute} from the given {@link #getResource()} object
     *
     * @param scimAttributeSchema
     *     the attribute that should be read from the {@link #getResource()}
     *
     * @return the attribute from the {@link #getResource()} or an empty
     */
    public Optional<SimpleAttribute> getSimpleAttribute (SCIMAttributeSchema scimAttributeSchema) {

        if (scimAttributeSchema == null) {
            return Optional.empty();
        }
        return Optional.ofNullable((SimpleAttribute) getResource().getAttribute(scimAttributeSchema.getName()));
    }

    /**
     * gets a {@link SimpleAttribute} from the given {@code complexAttribute} object
     *
     * @param scimAttributeSchema
     *     the attribute that should be read from the {@code complexAttribute}
     * @param complexAttribute
     *     the attribute that should be read from the {@code complexAttribute}
     *
     * @return the attribute from the {@code complexAttribute} or an empty
     */
    public Optional<SimpleAttribute> getSimpleAttribute (SCIMAttributeSchema scimAttributeSchema,
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
     * @param scimAttributeSchema
     *     the attribute that should be read from the {@code complexAttribute}
     * @param complexAttribute
     *     the attribute that should be read from the {@code complexAttribute} if (scimAttributeSchema == null ||
     *     complexAttribute == null) { return Optional.empty(); }
     *
     * @return the value from the {@code complexAttribute} or an empty
     */
    public Optional<String> getSimpleAttributeValue (SCIMAttributeSchema scimAttributeSchema,
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
     * @param scimAttributeSchema
     *     the attribute that should be read from the {@code complexAttribute}
     * @param complexAttribute
     *     the attribute that should be read from the {@code complexAttribute} if (scimAttributeSchema == null ||
     *     complexAttribute == null) { return Optional.empty(); }
     *
     * @return the value from the {@code complexAttribute} or an empty
     */
    public Optional<Boolean> getSimpleAttributeBoolean (SCIMAttributeSchema scimAttributeSchema,
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
     * gets a {@link ComplexAttribute} from the given {@link #getResource()} object
     *
     * @param scimAttributeSchema
     *     the attribute that should be read from the {@link #getResource()}
     *
     * @return the attribute from the {@link #getResource()} or an empty
     */
    public Optional<ComplexAttribute> getComplexAttribute (SCIMAttributeSchema scimAttributeSchema) {

        return Optional.ofNullable((ComplexAttribute) getResource().getAttribute(scimAttributeSchema.getName()));
    }

    /**
     * gets a {@link ComplexAttribute} from the given {@link #getResource()} object
     *
     * @param attributeName
     *     the attribute name that should be read from the {@link #getResource()}
     *
     * @return the attribute from the {@link #getResource()} or an empty
     */
    public Optional<ComplexAttribute> getComplexAttribute (String attributeName) {

        return Optional.ofNullable((ComplexAttribute) getResource().getAttribute(attributeName));
    }

    /**
     * gets a {@link ComplexAttribute} from the given {@link #getResource()} object if it does exist and will create it
     * if it does not exist
     *
     * @param scimAttributeSchema
     *     the attribute that should be read from the {@link #getResource()}
     *
     * @return the attribute from the {@link #getResource()} or a new attribute that will also be added to the {@link
     * #getResource()} object
     */
    public ComplexAttribute getOrCrateComplexAttribute (SCIMAttributeSchema scimAttributeSchema) {
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
     * gets a {@link MultiValuedAttribute} from the given {@link #getResource()} object
     *
     * @param scimAttributeSchema
     *     the attribute that should be read from the {@link #getResource()}
     *
     * @return the attribute from the {@link #getResource()} or an empty
     */
    public Optional<MultiValuedAttribute> getMultiValuedAttribute (SCIMAttributeSchema scimAttributeSchema) {

        return Optional.ofNullable((MultiValuedAttribute) getResource().getAttribute(scimAttributeSchema.getName()));
    }

    /**
     * gets a {@link MultiValuedAttribute} from the given complex attribute
     *
     * @param scimAttributeSchema
     *     the attribute that should be read from the complex attribute
     *
     * @return the multivalued attribute from the complex attribute
     */
    public Optional<MultiValuedAttribute> getMultiValuedAttribute (ComplexAttribute complexAttribute,
                                                                   SCIMAttributeSchema scimAttributeSchema) {
        return Optional.ofNullable((MultiValuedAttribute) rethrowSupplier(
            () -> complexAttribute.getSubAttribute(scimAttributeSchema.getName())).get());
    }

    /**
     * sets a {@link SimpleAttribute} for the given {@link #getResource()}
     *
     * @param scimAttributeSchema
     *     the attribute to set
     * @param value
     *     the value that represents the attribute
     */
    public void replaceSimpleAttribute (SCIMAttributeSchema scimAttributeSchema, Object value) {

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
     * @param multiValuedComplexTypeList
     *     the list of attributes that should be added if they are present
     * @param complexDefinition
     *     the definition of the complex attribute that should be added to the {@link #getResource()}
     * @param valueDefinition
     *     the definition of the value-attribute for the given complex-type
     * @param displayDefinition
     *     the definition of the display-attribute for the given complex-type
     * @param typeDefinition
     *     the definition of the type-attribute for the given complex-type
     * @param primaryDefinition
     *     the definition of the primary-attribute for the given complex-type
     * @param referenceDefinition
     *     the definition of the reference-attribute for the given complex-type
     */
    protected void addMultivaluedComplexAtribute (List<MultiValuedComplexType> multiValuedComplexTypeList,
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
     * returns a {@link BiConsumer} that will add a new {@link SimpleAttribute} to the given {@link ComplexAttribute}
     *
     * @param cAttr
     *     the {@link ComplexAttribute} that will be extended by a {@link SimpleAttribute}
     *
     * @return the consumer that performs the execution of adding a {@link SimpleAttribute} to the given {@link
     * ComplexAttribute}
     */
    protected BiConsumer<SCIMAttributeSchema, Supplier<Object>> getSetSubAttributeConsumer (ComplexAttribute cAttr) {

        return (scimAttributeSchema, objectSupplier) -> {
            Optional.ofNullable(scimAttributeSchema).ifPresent(schema -> {
                Optional.ofNullable(objectSupplier.get()).ifPresent(value -> {
                    SimpleAttribute valueAttribute = new SimpleAttribute(schema.getName(), objectSupplier.get());
                    rethrowBiConsumer(DefaultAttributeFactory::createAttribute).accept(schema, valueAttribute);
                    rethrowConsumer(cAttr::setSubAttribute).accept(valueAttribute);
                });
            });
        };
    }

    /**
     * extracts a multi-valued-complex-type from the {@link #getResource()} object by the given attribute definitions
     *
     * @param multiValuedDef
     *     the multi valued complex type definition
     * @param valueDefinition
     *     the value-definition of the multi-valued-complex type
     * @param displayDefinition
     *     the display-definition of the multi-valued-complex type
     * @param typeDefinition
     *     the type-definition of the multi-valued-complex type
     * @param primaryDefinition
     *     the primary-definition of the multi-valued-complex type
     * @param referenceDef
     *     the reference-definition of the multi-valued-complex type
     *
     * @return a list of the given {@link MultiValuedComplexType}s
     */
    protected Optional<List<MultiValuedComplexType>> getMultivaluedComplexType (SCIMAttributeSchema multiValuedDef,
                                                                                SCIMAttributeSchema valueDefinition,
                                                                                SCIMAttributeSchema displayDefinition,
                                                                                SCIMAttributeSchema typeDefinition,
                                                                                SCIMAttributeSchema primaryDefinition,
                                                                                SCIMAttributeSchema referenceDef) {

        return getMultiValuedAttribute(multiValuedDef).map(multiValuedAttribute -> {
            List<MultiValuedComplexType> multiValuedComplexTypes = new ArrayList<>();
            for (Attribute attributeValue : multiValuedAttribute.getAttributeValues()) {
                getMultiValuedComplexType((ComplexAttribute) attributeValue, valueDefinition, displayDefinition,
                    typeDefinition, primaryDefinition, referenceDef).ifPresent(multiValuedComplexType -> {
                    multiValuedComplexTypes.add(multiValuedComplexType);
                });
            }
            return multiValuedComplexTypes;
        });
    }

    /**
     * parses a {@link ComplexAttribute} into a {@link MultiValuedComplexType}
     *
     * @param attributeValue
     *     the complex type to parse
     * @param valueDefinition
     *     the value attribute description
     * @param displayDefinition
     *     the display attribute description
     * @param typeDefinition
     *     the type attribute description
     * @param primaryDefinition
     *     the primary attribute description
     * @param referenceDefinition
     *     the reference attribute description
     *
     * @return the {@link MultiValuedComplexType} or an empty if no such sub-attributes exist
     */
    public Optional<MultiValuedComplexType> getMultiValuedComplexType (ComplexAttribute attributeValue,
                                                                       SCIMAttributeSchema valueDefinition,
                                                                       SCIMAttributeSchema displayDefinition,
                                                                       SCIMAttributeSchema typeDefinition,
                                                                       SCIMAttributeSchema primaryDefinition,
                                                                       SCIMAttributeSchema referenceDefinition) {
        ComplexAttribute complexAttribute = attributeValue;
        MultiValuedComplexType multiValuedComplexType = new MultiValuedComplexType();
        getSimpleAttributeValue(valueDefinition, complexAttribute).ifPresent(multiValuedComplexType::setValue);
        getSimpleAttributeValue(displayDefinition, complexAttribute).ifPresent(multiValuedComplexType::setDisplay);
        getSimpleAttributeValue(typeDefinition, complexAttribute).ifPresent(multiValuedComplexType::setType);
        getSimpleAttribute(primaryDefinition, complexAttribute).map(rethrowFunction(SimpleAttribute::getBooleanValue))
            .ifPresent(multiValuedComplexType::setPrimary);
        getSimpleAttribute(referenceDefinition, complexAttribute).map(
            simpleAttribute -> (String) simpleAttribute.getValue()).ifPresent(multiValuedComplexType::setReference);

        if (!( isBlank(multiValuedComplexType.getValue()) && isBlank(multiValuedComplexType.getDisplay()) && isBlank(
            multiValuedComplexType.getType()) && isBlank(multiValuedComplexType.getReference()) &&
            !multiValuedComplexType.isPrimary() )) {
            return Optional.of(multiValuedComplexType);
        }
        return Optional.empty();
    }


    /**
     * @return the meta attribute from the current scim resource
     */
    public Meta getMeta () {
        Optional<ComplexAttribute> metaAttribute = getComplexAttribute(SCIMSchemaDefinitions.META);
        if (metaAttribute.isPresent()) {
            ComplexAttribute metaComplex = metaAttribute.get();
            Meta meta = new Meta();
            getSimpleAttribute(SCIMSchemaDefinitions.CREATED, metaComplex).ifPresent(simpleAttribute -> {
                meta.setCreated(rethrowSupplier(simpleAttribute::getInstantValue).get());
            });
            getSimpleAttribute(SCIMSchemaDefinitions.LAST_MODIFIED, metaComplex).ifPresent(simpleAttribute -> {
                meta.setLastModified(rethrowSupplier(simpleAttribute::getInstantValue).get());
            });
            getSimpleAttributeValue(SCIMSchemaDefinitions.LOCATION, metaComplex).ifPresent(meta::setLocation);
            getSimpleAttributeValue(SCIMSchemaDefinitions.RESOURCE_TYPE, metaComplex).ifPresent(meta::setResourceType);
            getSimpleAttributeValue(SCIMSchemaDefinitions.VERSION, metaComplex).ifPresent(meta::setVersion);
            return meta;
        }
        return null;
    }

    /**
     * sets the meta attributes
     */
    public void setMeta (Meta meta) {
        if (meta == null || meta.isEmpty()) {
            getResource().deleteAttribute(SCIMSchemaDefinitions.META.getName());
            return;
        }
        ComplexAttribute metaAttribute = getComplexAttribute(SCIMSchemaDefinitions.META).orElse(null);
        if (metaAttribute == null) {
            metaAttribute = new ComplexAttribute(SCIMSchemaDefinitions.META.getName());
            rethrowConsumer(
                o -> DefaultAttributeFactory.createAttribute(SCIMSchemaDefinitions.META, (AbstractAttribute) o)).accept(
                metaAttribute);
            getResource().setAttribute(metaAttribute);
        }
        getSetSubAttributeConsumer(metaAttribute).accept(SCIMSchemaDefinitions.CREATED, meta::getCreated);
        getSetSubAttributeConsumer(metaAttribute).accept(SCIMSchemaDefinitions.LAST_MODIFIED, meta::getLastModified);
        getSetSubAttributeConsumer(metaAttribute).accept(SCIMSchemaDefinitions.RESOURCE_TYPE, meta::getResourceType);
        getSetSubAttributeConsumer(metaAttribute).accept(SCIMSchemaDefinitions.LOCATION, meta::getLocation);
        getSetSubAttributeConsumer(metaAttribute).accept(SCIMSchemaDefinitions.VERSION, meta::getVersion);
    }

    /**
     * this method is used to compare to scim objects
     *
     * @param object
     *     the object to compare with this object
     *
     * @return true if both attributes do contain the same attributes and values
     */
    @Override
    public boolean equals (Object object) {

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
        if (!getResource().getAttributeList().keySet().equals(
            scimAttributeAware.getResource().getAttributeList().keySet())) {

            return false;
        }

        return getResource().getAttributeList().keySet().stream().allMatch(attributeName -> {
            return attributesEquals(getResource().getAttribute(attributeName),
                scimAttributeAware.getResource().getAttribute(attributeName));
        });
    }

    /**
     * checks that two given attributes are equals by running through their structure recursively
     *
     * @return true if the given attributes are equals, false else
     */
    public static boolean attributesEquals (Attribute attribute,
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
    public static boolean attributeMetaEquals (Attribute attribute, Attribute attributeOther) {

        if (!Objects.equals(attribute.getMultiValued(), attributeOther.getMultiValued())) {
            return false;
        }
        if (!Objects.equals(attribute.getCaseExact(), attributeOther.getCaseExact())) {
            return false;
        }
        if (!Objects.equals(attribute.getRequired(), attributeOther.getRequired())) {
            return false;
        }
        if (!Objects.equals(attribute.getMutability(), attributeOther.getMutability())) {
            return false;
        }
        if (!Objects.equals(attribute.getReturned(), attributeOther.getReturned())) {
            return false;
        }
        if (!Objects.equals(attribute.getUniqueness(), attributeOther.getUniqueness())) {
            return false;
        }
        if (attribute.getURI() == null || !attribute.getURI().equals(attributeOther.getURI())) {
            return false;
        }
        return Objects.equals(attribute.getType(), attributeOther.getType());
    }

    /**
     * tells us if two simple attributes are identical
     *
     * @return true if the attributes are identical, false else
     */
    public static boolean simpleAttributeEquals (SimpleAttribute attribute1, SimpleAttribute attribute2) {

        if (!Objects.equals(attribute1.getValue(), attribute2.getValue())) {
            return false;
        }
        return attributeMetaEquals(attribute1, attribute2);
    }

    /**
     * tells us if two simple attributes are identical
     *
     * @return true if the attributes are identical, false else
     */
    public static boolean multiValuedAttributeEquals (MultiValuedAttribute attribute,
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
    public static boolean complexAttributeEquals (ComplexAttribute attribute,
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

    @Override
    public int hashCode () {

        return getResource().getAttributeList().hashCode();
    }

    /**
     * tells us if this string is blank or not
     */
    protected boolean isBlank (String s) {

        return s == null || s.trim().isEmpty();
    }

    /**
     * tells us if this string is blank or not
     */
    protected boolean isNotBlank (String s) {

        return s != null && !s.trim().isEmpty();
    }

    /**
     * returns null if this string is blank and removed all whitespaces in the front and at the rear if it is not blank
     */
    protected String stripToNull (String s) {

        return isBlank(s) ? null : s.trim();
    }

    /**
     * gets the value of the attribute in the given resource extension as string value
     *
     * @param extensionSchema
     *     the resource schema extension that should hold the attribute
     * @param attributeSchema
     *     the attribute to read
     *
     * @return the value of the attribute or null
     *
     * @throws ClassCastException
     *     if the attribute to extract is not of type {@link SimpleAttribute}
     */
    public String getExtensionAttributeAsString (SCIMResourceTypeExtensionSchema extensionSchema,
                                                 SCIMAttributeSchema attributeSchema) {

        Optional<ComplexAttribute> extensionAttributeOptional = getComplexAttribute(extensionSchema.getSchema());
        if (!extensionAttributeOptional.isPresent()) {
            return null;
        }
        ComplexAttribute complexAttribute = extensionAttributeOptional.get();

        return getSimpleAttributeValue(attributeSchema, complexAttribute).orElse(null);
    }

    public ComplexAttribute getComplexAttributeFromExtension (SCIMResourceTypeExtensionSchema extensionSchema,
                                                              SCIMAttributeSchema attributeSchema) {
        ComplexAttribute extensionAttribute = getComplexAttribute(extensionSchema.getSchema()).orElse(null);
        if (extensionAttribute == null) {
            return null;
        }
        if (!SCIMDefinitions.DataType.COMPLEX.equals(attributeSchema.getType())) {
            rethrowSupplier(() -> {
                throw new InternalErrorException(
                    "cannot extract a complex type if the type is a simple type or a " + "multi valued type");
            });
        }
        return getOrCreateComplexAttributeFromComplexAttribute(extensionAttribute, attributeSchema);
    }

    /**
     * adds the given attribute to the extension schema of the current resource
     *
     * @param extensionSchema
     *     the extension definition
     * @param attribute
     *     the attribute that should be added
     */
    public void addAttributeToSchemaExtension (SCIMResourceTypeExtensionSchema extensionSchema, Attribute attribute) {
        ComplexAttribute extensionAttribute = getOrCreateExtensionAttribute(extensionSchema);
        rethrowConsumer(o -> extensionAttribute.setSubAttribute((Attribute) o)).accept(attribute);
    }

    /**
     * gets the desired extension as complex attribute
     *
     * @param extensionSchema
     *     the extension definition
     *
     * @return the extension as complex attribute
     */
    public ComplexAttribute getExtensionAttribute (SCIMResourceTypeExtensionSchema extensionSchema) {
        return getComplexAttribute(extensionSchema.getSchema()).orElse(null);
    }

    /**
     * gets or creates the desired schema extension of the current scim resource
     *
     * @param extensionSchema
     *     the extension definition
     *
     * @return the extension as complex attribute
     */
    public ComplexAttribute getOrCreateExtensionAttribute (SCIMResourceTypeExtensionSchema extensionSchema) {
        ComplexAttribute schemaExtension = getExtensionAttribute(extensionSchema);
        if (schemaExtension == null) {
            schemaExtension = new ComplexAttribute(extensionSchema.getSchema());
            rethrowConsumer(o -> createAttribute(extensionSchema.getAsAttributeSchema(), (AbstractAttribute) o)).accept(
                schemaExtension);
            getResource().setAttribute(schemaExtension);
        }
        return schemaExtension;
    }

    /**
     * sets the value of the attribute in the given resource extension
     *
     * @param extensionSchema
     *     the resource schema extension that should hold the attribute
     * @param attributeSchema
     *     the attribute to set
     * @param value
     *     the value to set into the attribute
     *
     * @throws ClassCastException
     *     if the attribute to extract is not of type {@link SimpleAttribute}
     */
    public void setExtensionAttribute (SCIMResourceTypeExtensionSchema extensionSchema,
                                       SCIMAttributeSchema attributeSchema,
                                       Object value) {

        ComplexAttribute extensionAttribute = getOrCreateExtensionAttribute(extensionSchema);
        setComplexTypeValue(extensionAttribute, attributeSchema, value);
    }

    /**
     * sets the value of a complex attribute
     *
     * @param extensionAttribute
     *     the complex attribute that should be extended
     * @param attributeSchema
     *     the attribute definition of the attribute that should be added to the complex attribute
     * @param value
     *     the value
     */
    public void setComplexTypeValue (ComplexAttribute extensionAttribute,
                                     SCIMAttributeSchema attributeSchema,
                                     Object value) {
        if (SCIMDefinitions.DataType.COMPLEX.equals(attributeSchema.getType())) {
            if (attributeSchema.getMultiValued()) {
                rethrowSupplier(() -> {
                    // this usecase should be implemented manually based on the specific application requirements
                    throw new InternalErrorException("cannot set value of Multivalued complex type without knowing " +
                        "which complex type should be updated.");
                }).get();
            } else {
                ComplexAttribute attribute = getOrCreateComplexAttributeFromComplexAttribute(extensionAttribute,
                    attributeSchema);
                getSetSubAttributeConsumer(attribute).accept(attributeSchema, () -> value);
            }
        }
        if (attributeSchema.getMultiValued()) {
            getOrCreateMultiValuedAttributeOfComplexType(extensionAttribute, attributeSchema)
                .setAttributePrimitiveValue(value);
        } else {
            if (value != null) {
                getOrCreateSimpleAttributeOfComplexType(extensionAttribute, attributeSchema).setValue(value);
            } else {
                deleteAttributeOfComplexAttribute(extensionAttribute, attributeSchema);
            }
        }
        if (extensionAttribute.getSubAttributesList().isEmpty()) {
            getResource().deleteAttribute(extensionAttribute.getName());
        }
    }

    protected void addSubAttributeToComplexExtensionAttribute (SCIMResourceTypeExtensionSchema ext,
                                                               SCIMAttributeSchema complexAttributeDef,
                                                               SCIMAttributeSchema simpleAttributeDef,
                                                               Object value) {
        if (value == null || ( value instanceof String && StringUtils.isBlank((String) value) )) {
            return;
        }
        ComplexAttribute extension = getOrCreateExtensionAttribute(ext);
        ComplexAttribute complexAttribute = getComplexAttributeFromExtension(ext, complexAttributeDef);
        if (complexAttribute == null) {
            complexAttribute = new ComplexAttribute(complexAttributeDef.getName());
            rethrowConsumer(o -> createAttribute(complexAttributeDef, (AbstractAttribute) o)).accept(complexAttribute);
            rethrowConsumer(o -> extension.setSubAttribute((Attribute) o)).accept(complexAttribute);
        }
        getSetSubAttributeConsumer(complexAttribute).accept(simpleAttributeDef, () -> value);
    }

    private ComplexAttribute getOrCreateComplexAttributeFromComplexAttribute (ComplexAttribute complexAttribute,
                                                                              SCIMAttributeSchema scimAttributeSchema) {
        Attribute attribute = rethrowSupplier(() -> complexAttribute.getSubAttribute(scimAttributeSchema.getName()))
            .get();
        ComplexAttribute subComplexAttribute = (ComplexAttribute) attribute;
        if (attribute == null) {
            subComplexAttribute = new ComplexAttribute(scimAttributeSchema.getName());
            rethrowConsumer(o -> createAttribute(scimAttributeSchema, (AbstractAttribute) o)).accept(
                subComplexAttribute);
            rethrowConsumer(o -> complexAttribute.setSubAttribute((Attribute) o)).accept(subComplexAttribute);
            return subComplexAttribute;
        } else {
            return subComplexAttribute;
        }
    }

    private MultiValuedAttribute getOrCreateMultiValuedAttributeOfComplexType (ComplexAttribute complexAttribute,
                                                                               SCIMAttributeSchema scimAttributeSchema) {
        Attribute attribute = rethrowSupplier(() -> complexAttribute.getSubAttribute(scimAttributeSchema.getName()))
            .get();
        MultiValuedAttribute subMultiValuedAttribute = (MultiValuedAttribute) attribute;
        if (subMultiValuedAttribute == null) {
            subMultiValuedAttribute = new MultiValuedAttribute(scimAttributeSchema.getName());
            rethrowConsumer(o -> createAttribute(scimAttributeSchema, (AbstractAttribute) o)).accept(
                subMultiValuedAttribute);
            rethrowConsumer(o -> complexAttribute.setSubAttribute((Attribute) o)).accept(subMultiValuedAttribute);
            return subMultiValuedAttribute;
        } else {
            return subMultiValuedAttribute;
        }
    }

    private SimpleAttribute getOrCreateSimpleAttributeOfComplexType (ComplexAttribute complexAttribute,
                                                                     SCIMAttributeSchema scimAttributeSchema) {
        Attribute attribute = rethrowSupplier(() -> complexAttribute.getSubAttribute(scimAttributeSchema.getName()))
            .get();
        SimpleAttribute subSimpleAttribute = (SimpleAttribute) attribute;
        if (subSimpleAttribute == null) {
            subSimpleAttribute = new SimpleAttribute(scimAttributeSchema.getName(), "");
            rethrowConsumer(o -> createAttribute(scimAttributeSchema, (AbstractAttribute) o)).accept(
                subSimpleAttribute);
            rethrowConsumer(o -> complexAttribute.setSubAttribute((Attribute) o)).accept(subSimpleAttribute);
            return subSimpleAttribute;
        } else {
            return subSimpleAttribute;
        }
    }

    private void deleteAttributeOfComplexAttribute (ComplexAttribute complexAttribute,
                                                    SCIMAttributeSchema scimAttributeSchema) {
        Map<String, Attribute> attributeMap = complexAttribute.getSubAttributesList();
        for (Map.Entry<String, Attribute> stringAttributeEntry : attributeMap.entrySet()) {
            Attribute attribute = attributeMap.get(stringAttributeEntry.getKey());
            if (attribute.getURI().equals(scimAttributeSchema.getURI())) {
                complexAttribute.removeSubAttribute(attribute.getName());
                return;
            }
        }
    }

    /**
     * this method will extract a complex attribute from another complex-attribute that is defined just like a {@link
     * MultiValuedComplexType}. <br>
     * <br>
     * <b>NOTE:</b><br>
     * this method is actually a specification violation because SCIM does not allow complex attributes within complex
     * attributes. But since extensions are an exception in their representation this method ignores this violation to
     * get complex-attributes from an extension
     *
     * @param extensionSchema
     *     the schema extension that might hold the complex attribute
     * @param attributeSchema
     *     the complex schema definition of the schema extension
     * @param valueDefinition
     * @param displayDefinition
     * @param typeDefinition
     * @param primaryDefinition
     * @param referenceDefinition
     *
     * @return
     *
     * @see <a href="https://tools.ietf.org/html/rfc7643#section-2.3.8">
     * https://tools.ietf.org/html/rfc7643#section-2.3.8
     * </a>
     */
    public MultiValuedComplexType getExtensionAttributeAsComplexType (SCIMResourceTypeExtensionSchema extensionSchema,
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
    public abstract AbstractSCIMObject getResource ();
}
