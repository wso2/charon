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
 * This class representation can be used to easily add an address to an
 * {@link org.wso2.charon3.core.objects.AbstractSCIMObject} object
 */
public class ScimAddress {

    /**
     * formatted The full mailing address, formatted for display or use with a mailing label. This attribute
     * MAY contain newlines.
     */
    private String formatted;

    /**
     * A label indicating the attribute's function, e.g., "work" or "home".
     */
    private String type;

    /**
     * The full street address component, which may include house number, street name, P.O. box, and
     * multi-line extended street address information. This attribute MAY contain newlines.
     */
    private String streetAddress;

    /**
     * The city or locality component.
     */
    private String locality;

    /**
     * The state or region component.
     */
    private String region;

    /**
     * The zip code or postal code component.
     */
    private String postalCode;

    /**
     * The country name component. When specified, the value MUST be in ISO 3166-1 "alpha-2" code format
     * [ISO3166]; e.g., the United States and Sweden are "US" and "SE", respectively.
     */
    private String country;

    /**
     * A Boolean value indicating the 'primary' or preferred attribute value for this attribute, e.g., the
     * preferred mailing address or the primary email address. The primary attribute value "true" MUST appear
     * no more than once. If not specified, the value of "primary" SHALL be assumed to be "false".
     */
    private Boolean primary;

    public ScimAddress() {

    }

    public ScimAddress(String formatted,
                       String type,
                       String streetAddress,
                       String locality,
                       String region,
                       String postalCode,
                       String country,
                       Boolean primary) {

        this.formatted = formatted;
        this.type = type;
        this.streetAddress = streetAddress;
        this.locality = locality;
        this.region = region;
        this.postalCode = postalCode;
        this.country = country;
        this.primary = primary;
    }

    /**
     * @return true if all values of this object are null
     */
    public boolean isEmpty() {
        return formatted == null && type == null && streetAddress == null && locality == null && region == null &&
                   postalCode == null && country == null && primary == null;
    }

    /**
     * @see #formatted
     */
    public String getFormatted() {

        return formatted;
    }

    /**
     * @see #formatted
     */
    public void setFormatted(String formatted) {

        this.formatted = formatted;
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
     * @see #streetAddress
     */
    public String getStreetAddress() {

        return streetAddress;
    }

    /**
     * @see #streetAddress
     */
    public void setStreetAddress(String streetAddress) {

        this.streetAddress = streetAddress;
    }

    /**
     * @see #locality
     */
    public String getLocality() {

        return locality;
    }

    /**
     * @see #locality
     */
    public void setLocality(String locality) {

        this.locality = locality;
    }

    /**
     * @see #region
     */
    public String getRegion() {

        return region;
    }

    /**
     * @see #region
     */
    public void setRegion(String region) {

        this.region = region;
    }

    /**
     * @see #postalCode
     */
    public String getPostalCode() {

        return postalCode;
    }

    /**
     * @see #postalCode
     */
    public void setPostalCode(String postalCode) {

        this.postalCode = postalCode;
    }

    /**
     * @see #country
     */
    public String getCountry() {

        return country;
    }

    /**
     * @see #country
     */
    public void setCountry(String country) {

        this.country = country;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ScimAddress)) {
            return false;
        }
        ScimAddress that = (ScimAddress) o;
        return Objects.equals(formatted, that.formatted) && Objects.equals(type, that.type) && Objects.equals(
            streetAddress, that.streetAddress) && Objects.equals(locality, that.locality) && Objects.equals(region,
            that.region) && Objects.equals(postalCode, that.postalCode) && Objects.equals(country, that.country) &&
                   Objects.equals(primary, that.primary);
    }

    @Override
    public int hashCode() {
        return Objects.hash(formatted, type, streetAddress, locality, region, postalCode, country, primary);
    }

    @Override
    public String toString() {
        return "ScimAddress{" +
                 "formatted='" + formatted + '\'' +
                 ", type='" + type + '\'' +
                 ", streetAddress='" + streetAddress + '\'' +
                 ", locality='" + locality + '\'' +
                 ", region='" + region + '\'' +
                 ", postalCode='" + postalCode + '\'' +
                 ", country='" + country + '\'' +
                 ", primary=" + primary +
                 '}';
    }
}
