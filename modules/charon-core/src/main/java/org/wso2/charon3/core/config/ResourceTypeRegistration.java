package org.wso2.charon3.core.config;

import org.wso2.charon3.core.exceptions.ConflictException;
import org.wso2.charon3.core.resourcetypes.ResourceType;
import org.wso2.charon3.core.schema.SCIMConstants;
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
        RESOURCE_TYPE_LIST.add(getUserResourceType());
        RESOURCE_TYPE_LIST.add(getGroupResourceType());
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

    public static List<ResourceType> getResourceTypeList() {
        return RESOURCE_TYPE_LIST;
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
    }
}
