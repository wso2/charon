package org.wso2.charon3.core.testsetup;

import org.apache.commons.io.IOUtils;
import org.wso2.charon3.core.encoder.JSONDecoder;
import org.wso2.charon3.core.encoder.JSONEncoder;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.function.Function;


/**
 * author Pascal Knueppel <br>
 */
public interface FileReferences {

    /**
     * the basePath where the resource files are
     */
    public static final String basePath = "/org/wso2/charon3/core/";

    /**
     * a decoder to convert json-string into SCIM DTO objects
     */
    public static final JSONDecoder JSON_DECODER = new JSONDecoder();

    /**
     * an encoder to convert SCIM DTO objects into string-json representations
     */
    public static final JSONEncoder JSON_ENCODER = new JSONEncoder();

    /**
     * the resource file path to the json structure with a creation representation of a group named "bremen"
     */
    public static final String CREATE_GROUP_BREMEN_FILE = basePath + "group/create-group-bremen.json"; // NOPMD

    /**
     * the resource file path to the json structure with a creation representation of an user named "maxilein"
     */
    public static final String CREATE_USER_MAXILEIN_FILE = basePath + "group/create-user-maxilein.json"; // NOPMD

    /**
     * reads a file from the test-resources
     *
     * @param resourcePath the path to the resource
     * @return the resource read into a string value
     */
    default String readResourceFile(String resourcePath) {
        return readResourceFile(resourcePath, null);
    }

    /**
     * reads a file from the test-resources and modifies the content
     *
     * @param resourcePath              the path to the resource
     * @param changeResourceFileContent a function on the file content to modify the return string
     * @return the resource read into a string value
     */
    default String readResourceFile(String resourcePath, Function<String, String> changeResourceFileContent) {
        try (InputStream resourceInputStream = getClass().getResourceAsStream(resourcePath)) {
            String content = IOUtils.toString(resourceInputStream, StandardCharsets.UTF_8.name());
            if (changeResourceFileContent != null) {
                content = changeResourceFileContent.apply(content);
            }
            return content;
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }
}
