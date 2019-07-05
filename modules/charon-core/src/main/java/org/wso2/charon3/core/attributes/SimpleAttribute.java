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

import org.wso2.charon3.core.exceptions.CharonException;
import org.wso2.charon3.core.schema.SCIMDefinitions;

import java.time.Instant;

/**
 * This class is a blueprint of SimpleAttribute defined in SCIM Core Schema Spec.
 */
public class SimpleAttribute extends AbstractAttribute {

    private static final long serialVersionUID = 6106269076155338045L;
    //In a simple attribute, only one attribute value is present.
    private Object value;

    public SimpleAttribute(String attributeName, Object value) {
        this.name = attributeName;
        this.value = value;
    }

    /*
     * return the value of the simple attribute
     * @return
     */
    public Object getValue() {
        return value;
    }

    /*
     * set the value of the simple attribute
     * @param value
     */
    public void setValue(Object value) {
        this.value = value;
    }

    /*
     * not supported by simple attributes
     * @param attributeName
     * @return
     * @throws CharonException
     */
    public Attribute getSubAttribute(String attributeName) throws CharonException {
        throw new CharonException("getSubAttribute method not supported by SimpleAttribute.");
    }

    /*
     * not supported by simple attributes
     * @throws CharonException
     */
    @Override
    public void deleteSubAttributes() throws CharonException {
        throw new CharonException("deleteSubAttributes method not supported by SimpleAttribute.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SimpleAttribute copyAttribute() {
        return new SimpleAttribute(this.name, this.value);
    }

    /*
     * return the string type of the attribute value
     * @return
     * @throws CharonException
     */
    public String getStringValue() throws CharonException {
        if (this.type.equals(SCIMDefinitions.DataType.STRING)) {
            return (String) value;
        } else {
            throw new CharonException("Mismatch in requested data type");
        }
    }

    /*
     * return the date type of the attribute value
     * @return
     * @throws CharonException
     */
    public Instant getInstantValue() throws CharonException {
        if (this.type.equals(SCIMDefinitions.DataType.DATE_TIME)) {
            return (Instant) this.value;
        } else {
            throw new CharonException("Datatype doesn\'t match the datatype of the attribute value");
        }
    }

    /*
     * return boolean type of the attribute value
     *
     * @return
     *
     * @throws CharonException
     */
    public Boolean getBooleanValue() throws CharonException {
        if (this.type.equals(SCIMDefinitions.DataType.BOOLEAN)) {
            return (Boolean) this.value;
        } else {
            throw new CharonException("Datatype doesn\'t match the datatype of the attribute value");
        }
    }

    /*
     * uodate the attribute value
     *
     * @param value
     *
     * @throws CharonException
     */
    public void updateValue(Object value) throws CharonException {
        this.value = value;
    }
}
