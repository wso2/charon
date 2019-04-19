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

package org.wso2.charon3.core.objects.plainobjects;

import java.util.Objects;

/**
 * this class representation can be used to easily add a multi valued complex type representation like an email
 * or a phonenumber to an {@link org.wso2.charon3.core.objects.AbstractSCIMObject} object
 */
public class MultiValuedComplexType {

    /**
     * A label indicating the attribute's function, e.g., "work" or "home".
     */
    private String type;

    /**
     * A Boolean value indicating the 'primary' or preferred attribute value for this attribute, e.g., the
     * preferred mailing address or the primary email address. The primary attribute value "true" MUST appear
     * no more than once. If not specified, the value of "primary" SHALL be assumed to be "false".
     */
    private Boolean primary;

    /**
     * A human-readable name, primarily used for display purposes and having a mutability of "immutable".
     */
    private String display;

    /**
     * The attribute's significant value, e.g., email address, phone number.
     */
    private String value;

    /**
     * The reference URI of a target resource, if the attribute is a reference. URIs are canonicalized per
     * Section 6.2 of [RFC3986]. While the representation of a resource may vary in different SCIM protocol
     * API versions (see Section 3.13 of [RFC7644]), URIs for SCIM resources with an API version SHALL be
     * considered comparable to URIs without a version or with a different version. For example,
     * "https://example.com/Users/12345" is equivalent to "https://example.com/v2/Users/12345".
     */
    private String reference;

    public MultiValuedComplexType() {

    }

    public MultiValuedComplexType(String type, Boolean primary, String display, String value, String reference) {

        this.type = type;
        this.primary = primary;
        this.display = display;
        this.value = value;
        this.reference = reference;
    }

    /**
     * @return true if the values of this complex type are all null
     */
    public boolean isEmpty() {
        return type == null && primary == null && display == null && value == null && reference == null;
    }

    /**
     * @see #type
     */
    public String getType() {

        return type;
    }

    /**
     * @see #type
     */
    public void setType(String type) {

        this.type = type;
    }

    /**
     * @see #primary
     */
    public boolean isPrimary() {

        return primary == null ? false : primary;
    }

    /**
     * @see #primary
     */
    public void setPrimary(Boolean primary) {

        this.primary = primary;
    }

    /**
     * @see #display
     */
    public String getDisplay() {

        return display;
    }

    /**
     * @see #display
     */
    public void setDisplay(String display) {

        this.display = display;
    }

    /**
     * @see #value
     */
    public String getValue() {

        return value;
    }

    /**
     * @see #value
     */
    public void setValue(String value) {

        this.value = value;
    }

    /**
     * @see #reference
     */
    public String getReference() {

        return reference;
    }

    /**
     * @see #reference
     */
    public void setReference(String reference) {

        this.reference = reference;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MultiValuedComplexType)) {
            return false;
        }
        MultiValuedComplexType that = (MultiValuedComplexType) o;
        return Objects.equals(type, that.type) && Objects.equals(primary, that.primary) && Objects.equals(display,
            that.display) && Objects.equals(value, that.value) && Objects.equals(reference, that.reference);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, primary, display, value, reference);
    }

    @Override
    public String toString() {
        return "MultiValuedComplexType{" +
                 "type='" + type + '\'' +
                 ", primary=" + primary +
                 ", display='" + display + '\'' +
                 ", value='" + value + '\'' +
                 ", reference='" + reference + '\'' +
                 '}';
    }

    @Override
    public String toString() {
        return "MultiValuedComplexType{" +
                 "type='" + type + '\'' +
                 ", primary=" + primary +
                 ", display='" + display + '\'' +
                 ", value='" + value + '\'' +
                 ", reference='" + reference + '\'' +
                 '}';
    }
}
