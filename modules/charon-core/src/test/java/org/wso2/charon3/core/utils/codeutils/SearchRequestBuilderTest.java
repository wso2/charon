package org.wso2.charon3.core.utils.codeutils;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.wso2.charon3.core.schema.SCIMConstants;

import java.util.Locale;
import java.util.Optional;

/**
 * author Pascal Knueppel <br>.
 * created at: 25.04.2019 - 09:21 <br>
 * <br>
 */
public class SearchRequestBuilderTest {

    @ParameterizedTest
    @CsvSource({",,,,,,", "0, 1, username eq \"john\", username, ascending, id username, name locale"})
    public void testSearchRequestBuilderTest(Integer count, Integer startIndex, String filter, String sortBy,
                                             String sortOrder, String attributes, String excludedAttributes) {
        String searchRequest = SearchRequestBuilder.builder()
            .setCount(count)
            .setStartIndex(startIndex)
            .setFilter(filter)
            .setSortBy(sortBy)
            .setSortOrder(
                Optional.ofNullable(sortOrder)
                    .map(s -> SearchRequestBuilder.SortOrder.valueOf(s.toUpperCase(Locale.ENGLISH))).orElse(null))
            .setAttributes(attributes)
            .setExcludedAttributes(excludedAttributes)
            .build();

        JSONObject jsonObject = new JSONObject(new JSONTokener(searchRequest));
        JSONArray schemas = jsonObject.getJSONArray(SCIMConstants.CommonSchemaConstants.SCHEMAS);
        Assertions.assertEquals(1, schemas.length());
        Assertions.assertEquals(SCIMConstants.SEARCH_SCHEMA_URI, schemas.get(0));

        Assertions.assertEquals(count, jsonObject.opt(SCIMConstants.OperationalConstants.COUNT));
        Assertions.assertEquals(startIndex, jsonObject.opt(SCIMConstants.OperationalConstants.START_INDEX));
        Assertions.assertEquals(filter, jsonObject.opt(SCIMConstants.OperationalConstants.FILTER));
        Assertions.assertEquals(sortBy, jsonObject.opt(SCIMConstants.OperationalConstants.SORT_BY));
        Assertions.assertEquals(sortOrder, jsonObject.opt(SCIMConstants.OperationalConstants.SORT_ORDER));
        Assertions.assertEquals(attributes, jsonObject.opt(SCIMConstants.OperationalConstants.ATTRIBUTES));
        Assertions
            .assertEquals(excludedAttributes, jsonObject.opt(SCIMConstants.OperationalConstants.EXCLUDED_ATTRIBUTES));
    }
}
