package org.wso2.charon3.core.setup.resourcehandler;

import org.wso2.charon3.core.exceptions.AbstractCharonException;
import org.wso2.charon3.core.exceptions.ConflictException;
import org.wso2.charon3.core.exceptions.NotFoundException;
import org.wso2.charon3.core.extensions.ResourceHandler;
import org.wso2.charon3.core.objects.AbstractSCIMObject;
import org.wso2.charon3.core.utils.codeutils.Node;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * <br><br>
 * created at: 03.04.2019
 * @author Pascal Knüppel
 */
public abstract class InMemoryResourceHandler<R extends AbstractSCIMObject> implements ResourceHandler<R> {

    /**
     * the in memory resource storage
     */
    private final Map<String, R> resourceStore = new HashMap<>();

    /**
     * {@inheritDoc}
     */
    @Override
    public R create(R resource, Map<String, Boolean> requiredAttributes) throws AbstractCharonException {
        R r = resourceStore.get(resource.getId());
        if (r != null) {
            throw new ConflictException(getResourceType() + " with id '" + r.getId() + "' does already exist");
        }
        // this works only because charon is generating an id in the resource manager
        resourceStore.put(resource.getId(), resource);
        resource.replaceResourceType(getResourceType());
        resource.replaceCreated(Instant.now());
        resource.replaceLastModified(Instant.now());
        resource.replaceLocation(getResourceEndpoint() + "/" + resource.getId());
        return resource;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public R get(String id, Map<String, Boolean> requiredAttributes) throws AbstractCharonException {
        R r = resourceStore.get(id);
        if (r == null) {
            throw new NotFoundException(getResourceType() + " with id '" + id + "' does not exist");
        }
        return r;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(String id) throws AbstractCharonException {
        R r = resourceStore.get(id);
        if (r == null) {
            throw new NotFoundException(getResourceType() + " with id '" + id + "' does not exist");
        }
        resourceStore.remove(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Object> listResources(Node node,
                                      Integer startIndex,
                                      Integer count,
                                      String sortBy,
                                      String sortOrder,
                                      String domainName,
                                      Map<String, Boolean> requiredAttributes) throws AbstractCharonException {
        List<Object> resources = new ArrayList<>(resourceStore.values());
        if (resources.isEmpty()) {
            return resources;
        }
        // startIndex must be at least 1 due to specification
        int fromIndex = startIndex - 1;
        fromIndex =  fromIndex >= resources.size() ? resources.size() - 1 : fromIndex;
        int toIndex = startIndex - 1 + count;
        toIndex = toIndex >= resources.size() ? resources.size() - 1 : toIndex;
        resources = resources.subList(fromIndex, toIndex);
        return resources;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public R update(R resourceUpdate, Map<String, Boolean> requiredAttributes) throws AbstractCharonException {
        R r = resourceStore.get(resourceUpdate.getId());
        if (r == null) {
            throw new NotFoundException(getResourceType() + " with id '" + resourceUpdate.getId() + "' does not exist");
        }
        resourceStore.put(resourceUpdate.getId(), resourceUpdate);
        resourceUpdate.setLastModifiedInstant(Instant.now());
        return resourceUpdate;
    }

    /**
     * @return the resource type that is represented by this handler (used for error messages)
     */
    protected abstract String getResourceType();
}
