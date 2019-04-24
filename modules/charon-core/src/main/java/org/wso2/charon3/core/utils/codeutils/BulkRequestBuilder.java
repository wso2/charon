package org.wso2.charon3.core.utils.codeutils;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.wso2.charon3.core.encoder.JSONEncoder;
import org.wso2.charon3.core.objects.AbstractSCIMObject;
import org.wso2.charon3.core.schema.SCIMConstants;
import org.wso2.charon3.core.utils.LambdaExceptionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;

/**
 * This class is used to build a bulk request that can be used by a client
 * <br>
 * created at: 23.04.2019 - 10:34
 *
 * @author Pascal Knueppel
 */
public class BulkRequestBuilder {

    /**
     * a list of all bulk operations
     */
    private List<BulkRequestOperation> bulkOperationsList = new ArrayList<>();

    /**
     * An integer specifying the number of errors that the service
     * provider will accept before the operation is terminated and an
     * error response is returned.  OPTIONAL in a request.  Not valid in
     * a response.
     */
    private Integer failOnErrors;

    private BulkRequestBuilder() {
    }

    /**
     * creates the builder class and a bulk operation that must be configured.
     *
     * @param failOnErrors
     *     An integer specifying the number of errors that the service
     *     provider will accept before the operation is terminated and an
     *     error response is returned.  OPTIONAL in a request.  Not valid in
     *     a response.
     *
     * @return the next operation to use
     */
    public static BulkRequestBuilder builder(Integer failOnErrors) {
        BulkRequestBuilder bulkRequestBuilder = new BulkRequestBuilder();
        bulkRequestBuilder.setFailOnErrors(failOnErrors);
        return bulkRequestBuilder;
    }

    /**
     * creates the builder class and a bulk operation that must be configured.
     *
     * @return the next operation to use
     */
    public static BulkRequestBuilder builder() {
        return new BulkRequestBuilder();
    }

    /**
     * adds a new bulk request operation that must be configured
     *
     * @return the next bulk request operation that must be configured
     */
    public BulkRequestOperation next() {
        BulkRequestOperation operation = new BulkRequestOperation(this);
        this.bulkOperationsList.add(operation);
        return operation;
    }

    /**
     * @return builds the bulk operation into a string object
     */
    public String build() {
        JSONObject request = new JSONObject();
        JSONArray schemas = new JSONArray();
        schemas.put(SCIMConstants.BULK_REQUEST_URI);
        request.put(SCIMConstants.CommonSchemaConstants.SCHEMAS, schemas);
        Optional.ofNullable(failOnErrors)
            .ifPresent(s -> request.put(SCIMConstants.OperationalConstants.FAIL_ON_ERRORS, s));

        JSONArray operations = new JSONArray();
        bulkOperationsList.forEach(bulkRequestOperation -> operations.put(bulkRequestOperation.toJsonObject()));

        request.put(SCIMConstants.OperationalConstants.OPERATIONS, operations);
        return request.toString();
    }

    /**
     * @see #bulkOperationsList
     */
    public List<BulkRequestOperation> getBulkOperationsList() {
        return bulkOperationsList;
    }

    /**
     * @see #bulkOperationsList
     */
    public void setBulkOperationsList(
        List<BulkRequestOperation> bulkOperationsList) {
        this.bulkOperationsList = bulkOperationsList;
    }

    /**
     * @see #failOnErrors
     */
    public Integer getFailOnErrors() {
        return failOnErrors;
    }

    /**
     * @see #failOnErrors
     */
    public BulkRequestBuilder setFailOnErrors(Integer failOnErrors) {
        this.failOnErrors = failOnErrors;
        return this;
    }

    /**
     * represents the HTTP methods that are allowed within a bulk request
     */
    public static enum Method {
        POST, PUT, PATCH, DELETE
    }

    /**
     * represents a bulk request operation
     */
    public static class BulkRequestOperation {

        private BulkRequestBuilder bulkRequestBuilder;

        /**
         * The HTTP method of the current operation.  Possible values
         * are "POST", "PUT", "PATCH", or "DELETE".  REQUIRED.
         */
        private Method method;

        /**
         * The resource's relative path to the SCIM service provider's
         * root.  If "method" is "POST", the value must specify a resource
         * type endpoint, e.g., /Users or /Groups, whereas all other
         * "method" values must specify the path to a specific resource,
         * e.g., /Users/2819c223-7f76-453a-919d-413861904646.  REQUIRED in
         * a request.
         */
        private String path;

        /**
         * The transient identifier of a newly created resource,
         * unique within a bulk request and created by the client.  The
         * bulkId serves as a surrogate resource id enabling clients to
         * uniquely identify newly created resources in the response and
         * cross-reference new resources in and across operations within a
         * bulk request.  REQUIRED when "method" is "POST".
         */
        private String bulkId;

        /**
         * The current resource version.  Version MAY be used if the
         * service provider supports entity-tags (ETags) (Section 2.3 of
         * [RFC7232]) and "method" is "PUT", "PATCH", or "DELETE".
         */
        private String version;

        /**
         * The resource data as it would appear for a single SCIM POST,
         * PUT, or PATCH operation.  REQUIRED in a request when "method"
         * is "POST", "PUT", or "PATCH".
         */
        private AbstractSCIMObject data;

        public BulkRequestOperation(BulkRequestBuilder bulkRequestBuilder) {
            this.bulkRequestBuilder = bulkRequestBuilder;
        }

        /**
         * @return creates a next operation that will be added to the request
         */
        public BulkRequestOperation next() {
            validateOperation();
            BulkRequestOperation bulkRequestOperation = new BulkRequestOperation(bulkRequestBuilder);
            bulkRequestBuilder.getBulkOperationsList().add(bulkRequestOperation);
            return bulkRequestOperation;
        }

        /**
         * @return the builder instance
         */
        public BulkRequestBuilder getBuilder() {
            return bulkRequestBuilder;
        }

        /**
         * @return the json string of the builder
         */
        public String build() {
            return bulkRequestBuilder.build();
        }

        /**
         * checks if the values of the operation have been configured correctly
         */
        private void validateOperation() {
            if (StringUtils.isBlank(path)) {
                throw new IllegalStateException("path is a required field and must not be empty");
            }
            if (method == null) {
                throw new IllegalStateException("method is a required field and must not be empty");
            }
            if (Method.POST.equals(method) && StringUtils.isBlank(bulkId)) {
                throw new IllegalStateException("bulkId is a required field if method is POST");
            }
            if ((Method.POST.equals(method) || Method.PUT.equals(method) ||
                Method.PATCH.equals(method)) && data == null) {
                throw new IllegalStateException("data is a required field if method is POST, PUT or PATCH");
            }
        }

        /**
         * @see #bulkRequestBuilder
         */
        public BulkRequestBuilder getBulkRequestBuilder() {
            return bulkRequestBuilder;
        }

        /**
         * @return this object as JSON object
         */
        public JSONObject toJsonObject() {
            JSONObject jsonObject = new JSONObject();
            BiConsumer<String, String> addValue = (key, value) -> {
                Optional.ofNullable(value).ifPresent(s -> jsonObject.put(key, s));
            };
            addValue.accept(SCIMConstants.OperationalConstants.METHOD, method == null ? null : method.name());
            addValue.accept(SCIMConstants.OperationalConstants.PATH, path);
            addValue.accept(SCIMConstants.OperationalConstants.VERSION, version);
            addValue.accept(SCIMConstants.OperationalConstants.BULK_ID, bulkId);
            Optional.ofNullable(data).ifPresent(scimObject ->
                jsonObject.put(SCIMConstants.OperationalConstants.DATA,
                    LambdaExceptionUtils.rethrowSupplier(() -> new JSONEncoder().encodeSCIMObject(scimObject)).get())
            );
            return jsonObject;
        }

        /**
         * @see #method
         */
        public Method getMethod() {
            return method;
        }

        /**
         * @see #method
         */
        public BulkRequestOperation setMethod(Method method) {
            this.method = method;
            if (Method.POST.equals(method) && StringUtils.isBlank(bulkId)) {
                setBulkId(UUID.randomUUID().toString());
            }
            return this;
        }

        /**
         * @see #path
         */
        public String getPath() {
            return path;
        }

        /**
         * @see #path
         */
        public BulkRequestOperation setPath(String path) {
            this.path = path;
            return this;
        }

        /**
         * @see #bulkId
         */
        public String getBulkId() {
            return bulkId;
        }

        /**
         * @see #bulkId
         */
        public BulkRequestOperation setBulkId(String bulkId) {
            this.bulkId = bulkId;
            return this;
        }

        /**
         * @see #version
         */
        public String getVersion() {
            return version;
        }

        /**
         * @see #version
         */
        public BulkRequestOperation setVersion(String version) {
            this.version = version;
            return this;
        }

        /**
         * @see #data
         */
        public AbstractSCIMObject getData() {
            return data;
        }

        /**
         * @see #data
         */
        public BulkRequestOperation setData(AbstractSCIMObject data) {
            this.data = data;
            return this;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof BulkRequestOperation)) return false;

            BulkRequestOperation that = (BulkRequestOperation) o;

            if (bulkRequestBuilder != null ? !bulkRequestBuilder.equals(that.bulkRequestBuilder) :
                that.bulkRequestBuilder != null) return false;
            if (method != null ? !method.equals(that.method) : that.method != null) return false;
            if (path != null ? !path.equals(that.path) : that.path != null) return false;
            if (bulkId != null ? !bulkId.equals(that.bulkId) : that.bulkId != null) return false;
            if (version != null ? !version.equals(that.version) : that.version != null) return false;
            return data != null ? data.equals(that.data) : that.data == null;

        }

        @Override
        public int hashCode() {
            int result = bulkRequestBuilder != null ? bulkRequestBuilder.hashCode() : 0;
            result = 31 * result + (method != null ? method.hashCode() : 0);
            result = 31 * result + (path != null ? path.hashCode() : 0);
            result = 31 * result + (bulkId != null ? bulkId.hashCode() : 0);
            result = 31 * result + (version != null ? version.hashCode() : 0);
            result = 31 * result + (data != null ? data.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return toJsonObject().toString();
        }
    }
}
