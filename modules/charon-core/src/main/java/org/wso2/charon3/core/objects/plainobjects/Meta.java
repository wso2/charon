package org.wso2.charon3.core.objects.plainobjects;

import java.time.Instant;
import java.util.Objects;

/**
 *  A complex attribute containing resource metadata.  All "meta"
 *  sub-attributes are assigned by the service provider (have a
 *  "mutability" of "readOnly"), and all of these sub-attributes have
 *  a "returned" characteristic of "default".  This attribute SHALL be
 *  ignored when provided by clients.  "meta" contains the following
 *  sub-attributes:
 * <br><br>
 * created at: 14.04.2019
 * @author Pascal Knüppel
 */
public class Meta {

    /**
     * The name of the resource type of the resource.  This
     * attribute has a mutability of "readOnly" and "caseExact" as
     * "true".
     */
    private String resourceType;

    /**
     * The "DateTime" that the resource was added to the service
     * provider.  This attribute MUST be a DateTime.
     */
    private Instant created;

    /**
     * The most recent DateTime that the details of this
     * resource were updated at the service provider.  If this
     * resource has never been modified since its initial creation,
     * the value MUST be the same as the value of "created".
     */
    private Instant lastModified;

    /**
     * The URI of the resource being returned.  This value MUST
     * be the same as the "Content-Location" HTTP response header (see
     * Section 3.1.4.2 of [RFC7231]).
     */
    private String location;

    /**
     * The version of the resource being returned.  This value
     * must be the same as the entity-tag (ETag) HTTP response header
     * (see Sections 2.1 and 2.3 of [RFC7232]).  This attribute has
     * "caseExact" as "true".  Service provider support for this
     * attribute is optional and subject to the service provider's
     * support for versioning (see Section 3.14 of [RFC7644]).  If a
     * service provider provides "version" (entity-tag) for a
     * representation and the generation of that entity-tag does not
     * satisfy all of the characteristics of a strong validator (see
     * Section 2.1 of [RFC7232]), then the origin server MUST mark the
     *"version" (entity-tag) as weak by prefixing its opaque value
     *with "W/" (case sensitive).
     */
    private String version;

    public Meta() {
    }

    public Meta(String resourceType, Instant created, Instant lastModified, String location, String version) {
        this.resourceType = resourceType;
        this.created = created;
        this.lastModified = lastModified;
        this.location = location;
        this.version = version;
    }

    /**
     * tells us if all attributes are null or not.
     */
    public boolean isEmpty() {
        return getCreated() == null && getLastModified() == null && getLocation() == null &&
                   getResourceType() == null && getVersion() == null;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public Instant getCreated() {
        return created;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    public Instant getLastModified() {
        return lastModified;
    }

    public void setLastModified(Instant lastModified) {
        this.lastModified = lastModified;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Meta)) {
            return false;
        }
        Meta meta = (Meta) o;
        return Objects.equals(resourceType, meta.resourceType) && Objects.equals(created, meta.created) &&
                   Objects.equals(lastModified, meta.lastModified) && Objects.equals(location, meta.location) &&
                   Objects.equals(version, meta.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(resourceType, created, lastModified, location, version);
    }

    @Override
    public String toString() {
        return "Meta{" + "resourceType='" + resourceType + '\'' + ", created=" + created + ", lastModified=" +
                   lastModified + ", location='" + location + '\'' + ", version='" + version + '\'' + '}';
    }
}
