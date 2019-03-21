package org.wso2.charon3.core.encoder;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.platform.commons.util.StringUtils;
import org.wso2.charon3.core.exceptions.AbstractCharonException;
import org.wso2.charon3.core.exceptions.BadRequestException;
import org.wso2.charon3.core.exceptions.CharonException;
import org.wso2.charon3.core.exceptions.ConflictException;
import org.wso2.charon3.core.exceptions.ForbiddenException;
import org.wso2.charon3.core.exceptions.FormatNotSupportedException;
import org.wso2.charon3.core.exceptions.InternalErrorException;
import org.wso2.charon3.core.exceptions.NotFoundException;
import org.wso2.charon3.core.exceptions.NotImplementedException;
import org.wso2.charon3.core.exceptions.PayloadTooLargeException;
import org.wso2.charon3.core.exceptions.PermanentRedirectException;
import org.wso2.charon3.core.exceptions.PreConditionFailedException;
import org.wso2.charon3.core.exceptions.TemporyRedirectException;
import org.wso2.charon3.core.exceptions.UnauthorizedException;
import org.wso2.charon3.core.objects.Group;
import org.wso2.charon3.core.objects.User;
import org.wso2.charon3.core.schema.SCIMConstants;
import org.wso2.charon3.core.schema.SCIMResourceSchemaManager;
import org.wso2.charon3.core.schema.SCIMResourceTypeSchema;
import org.wso2.charon3.core.schema.SCIMSchemaDefinitions;
import org.wso2.charon3.core.testsetup.FileReferences;
import org.wso2.charon3.core.utils.codeutils.SearchRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

/**
 * author Pascal Knueppel <br>
 * created at: 18.03.2019 - 12:31 <br>
 * <br>
 * the tests witin this class require that the tests in {@link JSONDecoderTest} are working
 */
public class JSONEncoderTest implements FileReferences {

    /**
     * builds the search request parameters for the test {@link #testEncodeSearchRequest(int, int, String,
     * String, String, String, List, List)}
     */
    public static Stream<Arguments> getEncodeSearchRequestParams() {
        return Stream.of(Arguments.of(1, 1, "userName eq \"chuck_norris\"", "domain", "userName", "ascending",
                Arrays.asList("userName", "id"), Arrays.asList("emails", "nickName")),
                Arguments.of(0, 100, null, null, null, null, null, null));
    }

    /**
     * build the exception arguments for the test-method {@link #testDecodeScimExceptions(AbstractCharonException)}
     */
    public static Stream<Arguments> getScimExceptions() {
        return Stream.of(
                Arguments.of(new AbstractCharonException()),
                Arguments.of(new CharonException()),
                Arguments.of(new BadRequestException()),
                Arguments.of(new ConflictException()),
                Arguments.of(new ForbiddenException()),
                Arguments.of(new FormatNotSupportedException()),
                Arguments.of(new InternalErrorException()),
                Arguments.of(new NotFoundException()),
                Arguments.of(new NotImplementedException()),
                Arguments.of(new PayloadTooLargeException()),
                Arguments.of(new PermanentRedirectException()),
                Arguments.of(new PreConditionFailedException()),
                Arguments.of(new TemporyRedirectException()),
                Arguments.of(new UnauthorizedException()));
    }

    /**
     * this test will show that the encoding of a group does work as expected
     */
    @Test
    public void testEncodeGroup() throws InternalErrorException, BadRequestException, CharonException {
        String groupJson = readResourceFile(CREATE_GROUP_BREMEN_FILE);
        Group group = JSON_DECODER.decodeResource(groupJson, SCIMSchemaDefinitions.SCIM_GROUP_SCHEMA, new Group());
        String encodedJson = JSON_ENCODER.encodeSCIMObject(group);
        Group onceEncodedGroup = JSON_DECODER.decodeResource(encodedJson, SCIMSchemaDefinitions.SCIM_GROUP_SCHEMA,
                new Group());
        Assertions.assertEquals(group, onceEncodedGroup);
    }

    /**
     * this test will show that the encoding of a user does work as expected
     */
    @Test
    public void testEncodeUser() throws InternalErrorException, BadRequestException, CharonException {
        String groupJson = readResourceFile(CREATE_USER_MAXILEIN_FILE);
        User user = JSON_DECODER.decodeResource(groupJson, SCIMSchemaDefinitions.SCIM_USER_SCHEMA, new User());
        String encodedJson = JSON_ENCODER.encodeSCIMObject(user);
        User onceEncodedUser = JSON_DECODER.decodeResource(encodedJson, SCIMSchemaDefinitions.SCIM_USER_SCHEMA,
                new User());
        Assertions.assertEquals(user, onceEncodedUser);
    }

    /**
     * this test will show that the encoding of an enterprise user does work as expected
     */
    @Test
    public void testEncodeEnterpriseUser() throws InternalErrorException, BadRequestException, CharonException,
            JSONException {
        String userJson = readResourceFile(CREATE_ENTERPRISE_USER_MAXILEIN_FILE);
        User user = JSON_DECODER.decodeResource(userJson, SCIMSchemaDefinitions.SCIM_USER_SCHEMA, new User());
        String encodedJson = JSON_ENCODER.encodeSCIMObject(user);
        JSONObject decodedJsonObj = new JSONObject(new JSONTokener(userJson));
        Assertions.assertTrue(decodedJsonObj.has(SCIMConstants.ENTERPRISE_USER_SCHEMA_URI));
        User onceEncodedUser = JSON_DECODER.decodeResource(encodedJson, SCIMSchemaDefinitions.SCIM_USER_SCHEMA,
                new User());
        Assertions.assertEquals(user, onceEncodedUser);
    }

    /**
     * this test will check that the encoded {@link org.wso2.charon3.core.utils.codeutils.SearchRequest} does contain
     * all values correctly
     */
    @ParameterizedTest
    @MethodSource("getEncodeSearchRequestParams")
    public void testEncodeSearchRequest(int count, int startIndex, String filter, String domain, String sortBy,
                                        String sortOrder, List<String> attributes, List<String> excludedAttributes)
            throws BadRequestException {
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.setCount(count);
        searchRequest.setStartIndex(startIndex);
        searchRequest.setFilterString(filter);
        searchRequest.setDomainName(domain);
        searchRequest.setSortBy(sortBy);
        searchRequest.setSortOder(sortOrder);
        searchRequest.setAttributes(attributes == null ? null : new ArrayList<>(attributes));
        searchRequest.setExcludedAttributes(excludedAttributes == null ? null : new ArrayList<>(excludedAttributes));

        String encodedRequest = JSON_ENCODER.encodeSearchRequest(searchRequest);
        SCIMResourceTypeSchema userSchema = SCIMResourceSchemaManager.getInstance().getUserResourceSchema();
        SearchRequest decodedRequest = JSON_DECODER.decodeSearchRequestBody(encodedRequest, userSchema);

        Assertions.assertEquals(count, decodedRequest.getCount());
        Assertions.assertEquals(startIndex, decodedRequest.getStartIndex());
        if (StringUtils.isNotBlank(decodedRequest.getFilterString())) {
            Assertions.assertNotNull(decodedRequest.getFilter());
        }
        Assertions.assertEquals(filter, decodedRequest.getFilterString());
        Assertions.assertEquals(domain, decodedRequest.getDomainName());
        Assertions.assertEquals(sortBy, decodedRequest.getSortBy());
        Assertions.assertEquals(sortOrder, decodedRequest.getSortOder());
        Assertions.assertEquals(attributes == null ? Collections.emptyList() : attributes,
                decodedRequest.getAttributes());
        Assertions.assertEquals(excludedAttributes == null ? Collections.emptyList() : excludedAttributes,
                decodedRequest.getExcludedAttributes());
    }

    /**
     * will test that decoding of the charon exceptions does work as expected
     *
     * @param exception the exception to decode
     */
    @ParameterizedTest
    @MethodSource("getScimExceptions")
    public void testDecodeScimExceptions(AbstractCharonException exception)
            throws BadRequestException, CharonException {
        String scimExceptionString = JSON_ENCODER.encodeSCIMException(exception);
        AbstractCharonException decodedAbstractException = JSON_DECODER.decodeCharonException(scimExceptionString);
        Assertions.assertNotNull(decodedAbstractException);
        AbstractCharonException ex = JSON_DECODER.decodeCharonException(scimExceptionString, exception.getClass());
        Assertions.assertEquals(exception.getClass(), ex.getClass());
        Assertions.assertEquals(exception.getDetail(), ex.getDetail());
        Assertions.assertEquals(exception.getStatus(), ex.getStatus());
        Assertions.assertEquals(exception.getSchemas(), ex.getSchemas());
        Assertions.assertEquals(exception.getScimType(), ex.getScimType());
    }
}
