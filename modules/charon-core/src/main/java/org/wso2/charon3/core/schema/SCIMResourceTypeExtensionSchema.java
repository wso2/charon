package org.wso2.charon3.core.schema;

import java.util.Collections;
import java.util.Set;

/**
 * author Pascal Knueppel <br>
 * created at: 18.03.2019 - 12:50 <br>
 * <br>
 * this class represents a scim schema extension for a resource
 */
public class SCIMResourceTypeExtensionSchema extends SCIMResourceTypeSchema {

    private static final long serialVersionUID = -9019208071755800613L;

    private SCIMResourceTypeExtensionSchema(String schema, AttributeSchema[] attributeSchemaList) {
        super(Collections.singletonList(schema), null, attributeSchemaList);
    }

    /**
     * creates a new extension schema
     *
     * @param schema           the identifier uri of the extension
     * @param attributeSchemas the attributes that will be describes by this schema
     * @return the new extension schema
     */
    public static SCIMResourceTypeExtensionSchema createSCIMResourcetypeExtension(String schema,
                                                                                  AttributeSchema... attributeSchemas) {
        return new SCIMResourceTypeExtensionSchema(schema, attributeSchemas);
    }

    /**
     * @see SCIMResourceTypeSchema#schemasList
     */
    public String getSchema() {
        return super.getSchemasList().get(0);
    }

    /**
     * @see SCIMResourceTypeSchema#schemasList
     */
    public void setSchema(String schema) {
        super.setSchemasList(schema);
    }

    /**
     * extensions are not supported recursively
     */
    @Override
    public final Set<SCIMResourceTypeExtensionSchema> getExtensions() {
        return Collections.emptySet();
    }

    /**
     * extensions are not supported recursively
     */
    @Override
    public final void setExtensions(Set<SCIMResourceTypeExtensionSchema> extensions) {
        // do nothing
    }

    /**
     * extensions are not supported recursively
     */
    @Override
    public final void addExtension(SCIMResourceTypeExtensionSchema extension) {
        // do nothing
    }

    /**
     * equals if the schema uri identifier is equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SCIMResourceTypeExtensionSchema)) {
            return false;
        }

        SCIMResourceTypeExtensionSchema that = (SCIMResourceTypeExtensionSchema) o;

        return getSchema() != null ? getSchema().equals(that.getSchema()) : that.getSchema() == null;
    }

    @Override
    public int hashCode() {
        return getSchema() != null ? getSchema().hashCode() : 0;
    }
}
