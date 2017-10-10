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
package org.wso2.charon3.core.attributes;

import org.wso2.charon3.core.schema.SCIMDefinitions;

/**
 * This class abstracts out the common characteristics of different types of attributes defined in
 * SCIM Core Schema Spec.
*/
public abstract class AbstractAttribute implements Attribute {

    //unique identifier for the attribute
    protected String uri;
    //name of the attribute
    protected String name;
    //data type of the attribute
    protected SCIMDefinitions.DataType type = null;
    //Boolean value indicating the attribute's plurality.
    protected Boolean multiValued;
    //The attribute's human readable description
    protected String description;
    //A Boolean value that specifies if the attribute is required
    protected Boolean required;
    //A Boolean value that specifies if the String attribute is case sensitive
    protected Boolean caseExact;
    //A SCIM defined value that specifies if the attribute's mutability.
    protected SCIMDefinitions.Mutability mutability = SCIMDefinitions.Mutability.READ_WRITE;
    //A SCIM defined value that specifies when the attribute's value need to be returned.
    protected SCIMDefinitions.Returned returned;
    //A SCIM defined value that specifies the uniqueness level of an attribute.
    protected SCIMDefinitions.Uniqueness uniqueness;

    public String getURI() {
        return uri; }

    public void setURI(String uri) {
        this.uri = uri; }

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

    public Boolean getMultiValued() {
        return multiValued;
    }

    public void setMultiValued(Boolean multiValued) {
        this.multiValued = multiValued;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    public Boolean getCaseExact() {
        return caseExact;
    }

    public void setCaseExact(Boolean caseExact) {
        this.caseExact = caseExact;
    }

    public SCIMDefinitions.Mutability getMutability() {
        return mutability;
    }

    public void setMutability(SCIMDefinitions.Mutability mutability) {
        this.mutability = mutability;
    }

    public SCIMDefinitions.Returned getReturned() {
        return returned; }

    public void setReturned(SCIMDefinitions.Returned returned) {
        this.returned = returned;
    }

    public SCIMDefinitions.Uniqueness getUniqueness() {
        return uniqueness;
    }

    public void setUniqueness(SCIMDefinitions.Uniqueness uniqueness) {
        this.uniqueness = uniqueness;
    }

}
