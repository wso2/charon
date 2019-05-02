package org.wso2.charon3.core.extensions;

import org.wso2.charon3.core.exceptions.AbstractCharonException;
import org.wso2.charon3.core.objects.AbstractSCIMObject;
import org.wso2.charon3.core.schema.SCIMResourceTypeSchema;
import org.wso2.charon3.core.utils.codeutils.Node;

import java.util.List;
import java.util.Map;

/**
 * resource handler that will be used by the resource-managers to handle SCIM resources.
 * <br><br>
 * created at: 01.04.2019
 *
 * @param <R> the scim object type that should be handled by this manager
 *
 * @author Pascal Knüppel
 *
 */
public interface ResourceHandler<R extends AbstractSCIMObject> {

    /**
     * creates a resource of the given type.
     *
     * @param resource the resource that should be created
     * @param requiredAttributes A multi-valued list of strings indicating the names of resource attributes to return
     *                           in the response, overriding the set of attributes that would be returned by default.
     * @return the created resource
     */
    public R create(R resource, Map<String, Boolean> requiredAttributes) throws AbstractCharonException;

    /**
     * loads a resource based on its id.
     *
     * @param id the id of the resource
     * @param requiredAttributes A multi-valued list of strings indicating the names of resource attributes to return
     *                           in the response, overriding the set of attributes that would be returned by default.
     * @return the loaded resource
     */
    public R get(String id, Map<String, Boolean> requiredAttributes) throws AbstractCharonException;

    /**
     * deletes the resource with the given id.
     *
     * @param id the id of the resource to delete
     */
    public void delete(String id) throws AbstractCharonException;

    /**
     * lists all users that match the given parameters.
     *
     * @param node the filter expression
     * @param startIndex index of the first entry
     * @param count number of resources to return in the request
     * @param sortBy a string indicating the attribute whose value SHALL be used to order the returned responses
     * @param sortOrder sortOrder  A string indicating the order in which the "sortBy" parameter is applied
     * @param domainName specific parameter for charon idp
     * @param requiredAttributes A multi-valued list of strings indicating the names of resource attributes to return
     *                          in the response, overriding the set of attributes that would be returned by default.
     * @return a list of all resources that are matchuing the given conditions
     */
    public List<Object> listResources(Node node,
                                      Integer startIndex,
                                      Integer count,
                                      String sortBy,
                                      String sortOrder,
                                      String domainName,
                                      Map<String, Boolean> requiredAttributes) throws AbstractCharonException;

    /**
     * will update the given resource with the new values.
     *
     * @param resourceUpdate the resource which values should be used to update the resource under the given id
     * @param requiredAttributes A multi-valued list of strings indicating the names of resource attributes to return
     *                           in the response, overriding the set of attributes that would be returned by default.
     * @return the updated resource
     */
    public R update(R resourceUpdate, Map<String, Boolean> requiredAttributes) throws AbstractCharonException;

    /**
     * the resource endpoint under which the resources are available. For the user endpoint this would be
     * {@link org.wso2.charon3.core.schema.SCIMConstants#USER_ENDPOINT} and for the groups endpoint
     * {@link org.wso2.charon3.core.schema.SCIMConstants#GROUP_ENDPOINT}
     */
    public String getResourceEndpoint();

    /**
     * must return the schema that represents the scim type that should be handled by this implementation.
     *
     * @see org.wso2.charon3.core.schema.SCIMSchemaDefinitions#SCIM_USER_SCHEMA
     * @see org.wso2.charon3.core.schema.SCIMSchemaDefinitions#SCIM_GROUP_SCHEMA
     */
    public SCIMResourceTypeSchema getResourceSchema();
}
