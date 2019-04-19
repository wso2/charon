package org.wso2.charon3.core.config;

import org.wso2.charon3.core.exceptions.ConflictException;
import org.wso2.charon3.core.objects.SchemaDefinition;
import org.wso2.charon3.core.resourcetypes.ResourceType;
import org.wso2.charon3.core.schema.SCIMConstants;
import org.wso2.charon3.core.schema.SCIMResourceTypeExtensionSchema;
import org.wso2.charon3.core.schema.SCIMSchemaDefinitions;
import org.wso2.charon3.core.utils.LambdaExceptionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * this class represents a configuration class that is used to register resource types in charon. All registered
 * types will be returned from the {@link org.wso2.charon3.core.protocol.endpoints.ResourceTypeResourceManager}
 * <br><br>
 * created at: 08.04.2019
 * @author Pascal Knüppel
 */
public final class ResourceTypeRegistration {

    private static final List<ResourceType> RESOURCE_TYPE_LIST = new ArrayList<>();

    /**
     * adds the default scim resources into the resource type list
     */
    static {
        addResourceType(getUserResourceType());
        addResourceType(getGroupResourceType());
    }

    private ResourceTypeRegistration() {
    }


    /**
     * creates the user resource type
     */
    private static ResourceType getUserResourceType() {
        ResourceType userResourceType = new ResourceType(SCIMConstants.USER, SCIMConstants.USER, "User Account",
            SCIMConstants.USER_ENDPOINT, SCIMSchemaDefinitions.SCIM_USER_SCHEMA);
        return userResourceType;
    }

    /**
     * creates the group resource type
     */
    private static ResourceType getGroupResourceType() {
        ResourceType groupResourceType = new ResourceType(SCIMConstants.GROUP, SCIMConstants.GROUP, "Group",
            SCIMConstants.GROUP_ENDPOINT, SCIMSchemaDefinitions.SCIM_GROUP_SCHEMA);
        return groupResourceType;
    }

    /**
     * the original resource type list that can be used to manipulate the list of resource types
     *
     * @return the original resource type list
     */
    public static List<ResourceType> getResourceTypeList() {
        return RESOURCE_TYPE_LIST;
    }

    /**
     * this method is used by the {@link org.wso2.charon3.core.protocol.endpoints.ResourceTypeResourceManager}
     * because this manager is manipulating the meta data in the resource objects. Therefore we need to copy these
     * objects because the meta data is immutable and the validation will not allow to override the value
     *
     * @return a list of copied resources
     */
    public static List<ResourceType> getResourceTypeListCopy() {
        List<ResourceType> resourceTypeList = new ArrayList<>();
        RESOURCE_TYPE_LIST.forEach(resourceType -> resourceTypeList.add((ResourceType) resourceType.copy()));
        return resourceTypeList;
    }

    /**
     *  @return the number of currently registered resource types
     */
    public static int getResouceTypeCount() {
        return RESOURCE_TYPE_LIST.size();
    }

    /**
     * registeres a new resource type that will be returned from the resource types endpoint by the
     * {@link org.wso2.charon3.core.protocol.endpoints.ResourceTypeResourceManager}
     * @param resourceType the new resource type
     */
    public static void addResourceType(ResourceType resourceType) {
        Optional<ResourceType> resourceTypeOptional = RESOURCE_TYPE_LIST.stream().filter(
            rt -> rt.getName().equals(resourceType.getName())).findFirst();
        if (resourceTypeOptional.isPresent()) {
            LambdaExceptionUtils.rethrowSupplier(() -> {
                throw new ConflictException("a resource type with the same name was already registered");
            }).get();
        }
        RESOURCE_TYPE_LIST.add(resourceType);
        addSchemataToSchemaRegistration(resourceType);
    }

    /**
     * this method will add the resource schemas  to the {@link SchemaRegistration} in order to make them available
     * from the schemas endpoint
     *
     * @param resourceType the current resource type that represents the schema definition that should be added
     */
    private static void addSchemataToSchemaRegistration(ResourceType resourceType) {
        SchemaRegistration.getInstance().addSchemaDefinition(new SchemaDefinition(resourceType));
        for (SCIMResourceTypeExtensionSchema extension : resourceType.getResourceTypeSchema().getExtensions()) {
            ResourceType extensionType = new ResourceType(extension.getSchema(), extension.getName(),
                extension.getDescription(), "/" + extension.getSchema(), extension);
            SchemaRegistration.getInstance().addSchemaDefinition(new SchemaDefinition(extensionType));
        }
    }
}
