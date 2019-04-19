package org.wso2.charon3.core.protocol.endpoints;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.wso2.charon3.core.config.ResourceTypeRegistration;
import org.wso2.charon3.core.config.SchemaRegistration;
import org.wso2.charon3.core.encoder.JSONEncoder;
import org.wso2.charon3.core.exceptions.AbstractCharonException;
import org.wso2.charon3.core.exceptions.CharonException;
import org.wso2.charon3.core.exceptions.NotFoundException;
import org.wso2.charon3.core.objects.ListedResource;
import org.wso2.charon3.core.objects.SchemaDefinition;
import org.wso2.charon3.core.protocol.ResponseCodeConstants;
import org.wso2.charon3.core.protocol.SCIMResponse;
import org.wso2.charon3.core.schema.SCIMConstants;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.wso2.charon3.core.protocol.endpoints.AbstractResourceManager.encodeSCIMException;
import static org.wso2.charon3.core.protocol.endpoints.AbstractResourceManager.getResourceEndpointURL;

/**
 * this class will provide the functionality for the schemas endpoint as defined in RFC7644 section 4
 * <br><br>
 * created at: 19.04.2019
 * @author Pascal Knüppel
 */
public class SchemasResourceManager {

    private static final Logger log = LoggerFactory.getLogger(SchemasResourceManager.class);

    /*
     * this call is done to initialize the ResourceTypeRegistration if it was not already initialized. The
     * initialization is mandatory for the schemas endpoint because it registers the schemata within the
     * SchemaRegistration
     */
    static {
        ResourceTypeRegistration.getResouceTypeCount();
    }

    /**
     * will get a single
     *
     * @param id the schema uri of the wanted schema
     */
    public SCIMResponse get(String id) {
        try {
            SchemaDefinition schemaDefinition = SchemaRegistration.getInstance().getSchemaListCopy().stream().filter(
                schema -> schema.getId().equals(id)).findAny().orElse(null);
            if (schemaDefinition == null) {
                log.debug("Schema definition with id '{}' does not exist", id);
                throw new NotFoundException("Schema definition with id '" + id + "' does not exist");
            }
            String encodedObject = new JSONEncoder().encodeSCIMObject(schemaDefinition);
            Map<String, String> responseHeaders = new HashMap<>();
            responseHeaders.put(SCIMConstants.LOCATION_HEADER, getResourceEndpointURL(SCIMConstants.SCHEMAS_ENDPOINT));
            responseHeaders.put(SCIMConstants.CONTENT_TYPE_HEADER, SCIMConstants.APPLICATION_JSON);
            return new SCIMResponse(ResponseCodeConstants.CODE_OK, encodedObject, responseHeaders);
        } catch (AbstractCharonException ex) {
            log.debug(ex.getSchemas(), ex);
            return encodeSCIMException(ex);
        } catch (RuntimeException ex) {
            log.error(ex.getMessage(), ex);
            return encodeSCIMException(new CharonException(ex.getMessage()));
        }
    }

    /**
     * gets all schemata definitions as listed response
     */
    public SCIMResponse listResources() {
        try {
            List<SchemaDefinition> schemaDefinitionList = SchemaRegistration.getInstance().getSchemaListCopy();
            ListedResource listedResource = new ListedResource();
            listedResource.setTotalResults(schemaDefinitionList.size());
            schemaDefinitionList.forEach(listedResource::addResource);
            String encodedObject = new JSONEncoder().encodeSCIMObject(listedResource);
            Map<String, String> responseHeaders = new HashMap<>();
            responseHeaders.put(SCIMConstants.LOCATION_HEADER,
                getResourceEndpointURL(SCIMConstants.RESOURCE_TYPE_ENDPOINT));
            responseHeaders.put(SCIMConstants.CONTENT_TYPE_HEADER, SCIMConstants.APPLICATION_JSON);

            //put the uri of the resource type object in the response header parameter.
            return new SCIMResponse(ResponseCodeConstants.CODE_OK, encodedObject, responseHeaders);
        } catch (AbstractCharonException ex) {
            log.debug(ex.getSchemas(), ex);
            return encodeSCIMException(ex);
        } catch (RuntimeException ex) {
            log.error(ex.getMessage(), ex);
            return encodeSCIMException(new CharonException(ex.getMessage()));
        }
    }

}
