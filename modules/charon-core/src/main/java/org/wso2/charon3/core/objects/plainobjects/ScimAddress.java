package org.wso2.charon3.core.objects.plainobjects;

/**
 * author Pascal Knueppel <br>
 * created at: 19.09.2018 - 09:49 <br>
 * <br>
 * this class representation can be used to easily add an address to an
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
    private boolean primary;

    public ScimAddress() {
    }

    public ScimAddress(String formatted,
                       String type,
                       String streetAddress,
                       String locality,
                       String region,
                       String postalCode,
                       String country,
                       boolean primary) {
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
        return primary;
    }

    /**
     * @see #primary
     */
    public void setPrimary(boolean primary) {
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

        if (primary != that.primary) {
            return false;
        }
        if (formatted != null ? !formatted.equals(that.formatted) : that.formatted != null) {
            return false;
        }
        if (type != null ? !type.equals(that.type) : that.type != null) {
            return false;
        }
        if (streetAddress != null ? !streetAddress.equals(that.streetAddress) : that.streetAddress != null) {
            return false;
        }
        if (locality != null ? !locality.equals(that.locality) : that.locality != null) {
            return false;
        }
        if (region != null ? !region.equals(that.region) : that.region != null) {
            return false;
        }
        if (postalCode != null ? !postalCode.equals(that.postalCode) : that.postalCode != null) {
            return false;
        }
        return country != null ? country.equals(that.country) : that.country == null;
    }

    @Override
    public int hashCode() {
        int result = formatted != null ? formatted.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (streetAddress != null ? streetAddress.hashCode() : 0);
        result = 31 * result + (locality != null ? locality.hashCode() : 0);
        result = 31 * result + (region != null ? region.hashCode() : 0);
        result = 31 * result + (postalCode != null ? postalCode.hashCode() : 0);
        result = 31 * result + (country != null ? country.hashCode() : 0);
        result = 31 * result + (primary ? 1 : 0);
        return result;
    }
}
