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

import org.wso2.charon3.core.utils.CopyUtil;

import java.io.Serializable;
import java.util.List;

/**
 * This defines the attributes schema as in SCIM Spec.
 */

public class SCIMAttributeSchema implements AttributeSchema, Serializable {

    private static final long serialVersionUID = 6106269076155338045L;
    /**
     * describes a parent attribute if present. For example the display value for email would be referenced in
     * filters with "email.display". To be able to easily build this expression from the child nodes the child nodes
     * need to know which node is the parent node.
     */
    private SCIMAttributeSchema parent;
    /**
     * unique identifier for the attribute
     */
    private String uri;
    /**
     * name of the attribute
     */
    private String name;
    /**
     * data type of the attribute
     */
    private SCIMDefinitions.DataType type;
    /**
     * Boolean value indicating the attribute's plurality.
     */
    private Boolean multiValued;
    /**
     * The attribute's human readable description
     */
    private String description;
    /**
     * A Boolean value that specifies if the attribute is required
     */
    private Boolean required;
    /**
     * A Boolean value that specifies if the String attribute is case sensitive
     */
    private Boolean caseExact;
    /**
     * A SCIM defined value that specifies if the attribute's mutability.
     */
    private SCIMDefinitions.Mutability mutability;
    /**
     * A SCIM defined value that specifies when the attribute's value need to be returned.
     */
    private SCIMDefinitions.Returned returned;
    /**
     * A SCIM defined value that specifies the uniqueness level of an attribute.
     */
    private SCIMDefinitions.Uniqueness uniqueness;
    /**
     * A list specifying the contained attributes. OPTIONAL.
     */
    private List<AttributeSchema> subAttributes;
    /**
     * A collection of suggested canonical values that MAY be used -OPTIONAL
     */
    private List<String> canonicalValues;
    /**
     * A multi-valued array of JSON strings that indicate the SCIM resource types that may be referenced
     * only applicable for attributes that are of type "reference"
     */
    private List<SCIMDefinitions.ReferenceType> referenceTypes;

    private SCIMAttributeSchema(String uri,
                                String name,
                                SCIMDefinitions.DataType type,
                                Boolean multiValued,
                                String description,
                                Boolean required,
                                Boolean caseExact,
                                SCIMDefinitions.Mutability mutability,
                                SCIMDefinitions.Returned returned,
                                SCIMDefinitions.Uniqueness uniqueness,
                                List<String> canonicalValues,
                                List<SCIMDefinitions.ReferenceType> referenceTypes,
                                List<AttributeSchema> subAttributes) {
        this.uri = uri;
        this.name = name;
        this.type = type;
        this.multiValued = multiValued;
        this.description = description;
        this.required = required;
        this.caseExact = caseExact;
        this.mutability = mutability;
        this.returned = returned;
        this.uniqueness = uniqueness;
        this.subAttributes = subAttributes;
        this.canonicalValues = canonicalValues;
        this.referenceTypes = referenceTypes;
        // set parent into children
        if (subAttributes != null) {
            for (AttributeSchema subAttribute : subAttributes) {
                if (subAttribute instanceof SCIMAttributeSchema) {
                    SCIMAttributeSchema child = (SCIMAttributeSchema) subAttribute;
                    child.setParent(this);
                }
            }
        }
    }

    public static SCIMAttributeSchema createSCIMAttributeSchema(String uri,
                                                                String name,
                                                                SCIMDefinitions.DataType type,
                                                                Boolean multiValued,
                                                                String description,
                                                                Boolean required,
                                                                Boolean caseExact,
                                                                SCIMDefinitions.Mutability mutability,
                                                                SCIMDefinitions.Returned returned,
                                                                SCIMDefinitions.Uniqueness uniqueness,
                                                                List<String> canonicalValues,
                                                                List<SCIMDefinitions.ReferenceType> referenceTypes,
                                                                List<AttributeSchema> subAttributes) {

        return new SCIMAttributeSchema(uri, name, type, multiValued, description, required, caseExact, mutability,
            returned, uniqueness, canonicalValues, referenceTypes, subAttributes);
    }

    /**
     * @see #parent
     */
    public SCIMAttributeSchema getParent() {
        return parent;
    }

    /**
     * @see #parent
     */
    public void setParent(SCIMAttributeSchema parent) {
        this.parent = parent;
    }

    @Override
    public String getURI() {
        return uri;
    }

    @Override
    public void setURI(String uri) {
        this.uri = uri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SCIMDefinitions.DataType getType() {
        return type;
    }

    public void setType(SCIMDefinitions.DataType type) {
        this.type = type;
    }

    public boolean getMultiValued() {
        return multiValued;
    }

    public void setMultiValued(boolean isMultiValued) {
        this.multiValued = isMultiValued;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean getRequired() {
        return required;
    }

    public void setRequired(boolean isRequired) {
        this.required = isRequired;
    }

    public boolean getCaseExact() {
        return caseExact;
    }

    public void setCaseExact(boolean isCaseExact) {
        this.caseExact = isCaseExact;
    }

    public SCIMDefinitions.Mutability getMutability() {
        return mutability;
    }

    public void setMutability(SCIMDefinitions.Mutability mutability) {
        this.mutability = mutability;
    }

    public SCIMDefinitions.Returned getReturned() {
        return returned;
    }

    public void setReturned(SCIMDefinitions.Returned returned) {
        this.returned = returned;
    }

    public SCIMDefinitions.Uniqueness getUniqueness() {
        return uniqueness;
    }

    public void setUniqueness(SCIMDefinitions.Uniqueness uniqueness) {
        this.uniqueness = uniqueness;
    }

    @Override
    public List<AttributeSchema> getSubAttributeSchemas() {
        return subAttributes;
    }

    @Override
    public AttributeSchema getSubAttributeSchema(String subAttribute) {
        for (AttributeSchema subAttributeSchema : subAttributes) {
            if (subAttributeSchema.getName().equals(subAttribute)) {
                return subAttributeSchema;
            }
        }
        return null;
    }

    @Override
    public void removeSubAttribute(String subAttributeName) {
        List<AttributeSchema> tempSubAttributes = (List<AttributeSchema>) CopyUtil.deepCopy(subAttributes);
        int count = 0;
        for (AttributeSchema subAttributeSchema : tempSubAttributes) {

            if (subAttributeSchema.getName().equals(subAttributeName)) {
                subAttributes.remove(count);
                return;
            }
            count++;
        }
    }

    public void setSubAttributes(List<AttributeSchema> subAttributes) {
        this.subAttributes = subAttributes;
    }

    public List<String> getCanonicalValues() {
        return canonicalValues;
    }

    public void setCanonicalValues(List<String> canonicalValues) {
        this.canonicalValues = canonicalValues;
    }

    public List<SCIMDefinitions.ReferenceType> getReferenceTypes() {
        return referenceTypes;
    }

    public void setReferenceTypes(List<SCIMDefinitions.ReferenceType> referenceTypes) {
        this.referenceTypes = referenceTypes;
    }
}
