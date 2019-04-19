package org.wso2.charon3.core.config;

import org.wso2.charon3.core.objects.SchemaDefinition;

import java.util.ArrayList;
import java.util.List;

/**
 * This is the schema holder that holds all registrated schema definitions. The registration is done automatically if
 * a new {@link org.wso2.charon3.core.resourcetypes.ResourceType} gets added within the {@link ResourceTypeRegistration}
 * <br><br>
 * created at: 19.04.2019
 * @author Pascal Knüppel
 */
public class SchemaRegistration {

    private static final SchemaRegistration SCHEMA_REGISTRATION = new SchemaRegistration();

    private List<SchemaDefinition> schemaDefinitionList;

    private SchemaRegistration() {
        schemaDefinitionList = new ArrayList<>();
    }

    public static SchemaRegistration getInstance() {
        return SCHEMA_REGISTRATION;
    }

    /**
     * sets all schemas that will be visible from the schemas-endpoint <br>
     * will be called from {@link ResourceTypeRegistration}
     *
     * @param schemaDefinitionList the schemata to register
     */
    protected void setSchemaDefinitions(List<SchemaDefinition> schemaDefinitionList) {
        this.schemaDefinitionList = schemaDefinitionList;
    }

    /**
     * adds a schema that will be visible from the schema-endpoint<br>
     * will be called from {@link ResourceTypeRegistration}
     *
     * @param schemaDefinition the schemata to register
     */
    protected void addSchemaDefinition(SchemaDefinition schemaDefinition) {
        this.schemaDefinitionList.add(schemaDefinition);
    }

    /**
     * this method is used by the {@link org.wso2.charon3.core.protocol.endpoints.SchemasResourceManager}
     * because this manager is manipulating the meta data in the resource objects. Therefore we need to copy these
     * objects because the meta data is immutable and the validation will not allow to override the value
     *
     * @return a list of copied schemas
     */
    public List<SchemaDefinition> getSchemaListCopy() {
        List<SchemaDefinition> schemaDefinitions = new ArrayList<>();
        schemaDefinitionList.forEach(
            schemaDefinition -> schemaDefinitions.add((SchemaDefinition) schemaDefinition.copy()));
        return schemaDefinitions;
    }

}
