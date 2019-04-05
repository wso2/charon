package org.wso2.charon3.core.objects.plainobjects;

import java.util.Objects;

/**
 * this class represents a plain representation for the schema extension object within a
 * {@link org.wso2.charon3.core.resourcetypes.ResourceType}. This is defined in RFC7643 section 6
 * <br><br>
 * created at: 04.04.2019
 * @author Pascal Knüppel
 */
public class ResourceTypeSchemaExtension {

    /**
     * The URI of an extended schema, e.g., "urn:edu:2.0:Staff".
     * This MUST be equal to the "id" attribute of a "Schema"
     * resource.  REQUIRED.
     */
    private String schema;

    /**
     * A Boolean value that specifies whether or not the schema
     * extension is required for the resource type.  If true, a
     * resource of this type MUST include this schema extension and
     * also include any attributes declared as required in this schema
     * extension.  If false, a resource of this type MAY omit this
     * schema extension.  REQUIRED.
     */
    private boolean required;

    public ResourceTypeSchemaExtension() {
    }

    public ResourceTypeSchemaExtension(String schema, boolean required) {
        this.schema = schema;
        this.required = required;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ResourceTypeSchemaExtension)) {
            return false;
        }
        ResourceTypeSchemaExtension that = (ResourceTypeSchemaExtension) o;
        return required == that.required && Objects.equals(schema, that.schema);
    }

    @Override
    public int hashCode() {
        return Objects.hash(schema, required);
    }

    @Override
    public String toString() {
        return "ResourceTypeSchemaExtension{" + "schema='" + schema + '\'' + ", required=" + required + '}';
    }
}
