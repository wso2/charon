package org.wso2.charon3.core.config;

import java.net.URL;
import java.util.Objects;

/**
 * A multi-valued complex type that specifies supported
 * authentication scheme properties.  To enable seamless discovery of
 * configurations, the service provider SHOULD, with the appropriate
 * security considerations, make the authenticationSchemes attribute
 * publicly accessible without prior authentication.  REQUIRED.
 * <br><br>
 * created at: 17.04.2019
 * @author Pascal Knüppel
 */
public class AuthenticationScheme {

    /**
     * The authentication scheme.  This specification defines the
     * values "oauth", "oauth2", "oauthbearertoken", "httpbasic", and
     * "httpdigest".  REQUIRED.
     */
    private String type;

    /**
     * The common authentication scheme name, e.g., HTTP Basic.
     * REQUIRED.
     */
    private String name;

    /**
     * A description of the authentication scheme.
     * REQUIRED.
     */
    private String description;

    /**
     * An HTTP-addressable URL pointing to the authentication
     * scheme's specification.  OPTIONAL.
     */
    private URL specUri;

    /**
     * An HTTP-addressable URL pointing to the
     * authentication scheme's usage documentation.  OPTIONAL.
     */
    private URL documentationUri;

    public AuthenticationScheme() {
    }

    public AuthenticationScheme(String type, String name, String description, URL specUri, URL documentationUri) {
        this.type = type;
        this.name = name;
        this.description = description;
        this.specUri = specUri;
        this.documentationUri = documentationUri;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public URL getSpecUri() {
        return specUri;
    }

    public void setSpecUri(URL specUri) {
        this.specUri = specUri;
    }

    public URL getDocumentationUri() {
        return documentationUri;
    }

    public void setDocumentationUri(URL documentationUri) {
        this.documentationUri = documentationUri;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AuthenticationScheme)) {
            return false;
        }
        AuthenticationScheme that = (AuthenticationScheme) o;
        return Objects.equals(type, that.type) && Objects.equals(name, that.name) && Objects.equals(description,
            that.description) && Objects.equals(specUri, that.specUri) && Objects.equals(documentationUri,
            that.documentationUri);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, name, description, specUri, documentationUri);
    }


    @Override
    public String toString() {
        return "AuthenticationScheme{" + "type='" + type + '\'' + ", name='" + name + '\'' + ", description='" +
                   description + '\'' + ", specUri=" + specUri + ", documentationUri=" + documentationUri + '}';
    }
}
