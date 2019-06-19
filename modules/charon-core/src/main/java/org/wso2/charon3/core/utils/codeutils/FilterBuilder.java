package org.wso2.charon3.core.utils.codeutils;

import org.apache.commons.lang3.StringUtils;
import org.wso2.charon3.core.exceptions.InternalErrorException;
import org.wso2.charon3.core.schema.AttributeSchema;
import org.wso2.charon3.core.schema.SCIMAttributeSchema;
import org.wso2.charon3.core.schema.SCIMConstants;
import org.wso2.charon3.core.schema.SCIMDefinitions;
import org.wso2.charon3.core.utils.LambdaExceptionUtils;

import java.io.Serializable;
import java.util.Locale;

import static org.wso2.charon3.core.schema.SCIMDefinitions.FilterOperation.PR;

/**
 * author Pascal Knueppel <br> created at: 22.03.2019 - 14:35 <br>
 * <br>
 * can be used to build a SCIM filter expression.
 */
public class FilterBuilder implements Serializable {

    private static final long serialVersionUID = 7231267409086617614L;

    private static final String ESCAPE_STRING_LITERAL = "\"";

    /**
     * the builder that is used to create the filter.
     */
    private StringBuilder filter = new StringBuilder();

    /**
     * counts the number of left parentheses.
     */
    private int leftParenthesisCount = 0;

    /**
     * counts the number of right parentheses.
     */
    private int rightParenthesisCount = 0;

    /**
     * if set to true a left parenthesis will be added before the next operation.
     */
    private boolean addLeftParenthesis = false;

    private FilterBuilder() {

    }

    public static FilterBuilder builder() {
        return new FilterBuilder();
    }

    /**
     * adds an and expression to the filter - if the filter has no expressions yet the "and" is ignored and a simple
     * filter expression is created.
     *
     * @param attributeSchema
     *     the attribute schema that will describe the attribute-name of the expression
     * @param operation
     *     the operation if the value is equals, contains etc.
     * @param value
     *     the value to compare
     */
    public FilterBuilder and(AttributeSchema attributeSchema, SCIMDefinitions.FilterOperation operation, String value) {
        expression(attributeSchema, operation, value, SCIMDefinitions.FilterConcatenation.AND);
        return this;
    }

    /**
     * adds an and expression to the filter - if the filter has no expressions yet the "and" is ignored and a simple
     * filter expression is created.
     *
     * @param attributeNameOrUri
     *     the attribute name of the field or the complete uri
     * @param operation
     *     the operation if the value is equals, contains etc.
     * @param value
     *     the value to compare
     */
    public FilterBuilder and(String attributeNameOrUri, SCIMDefinitions.FilterOperation operation, String value) {
        expression(attributeNameOrUri, operation, value, SCIMDefinitions.FilterConcatenation.AND);
        return this;
    }

    /**
     * adds an or expression to the filter - if the filter has no expressions yet the "or" is ignored and a simple
     * filter expression is created.
     *
     * @param attributeSchema
     *     the attribute schema that will describe the attribute-name of the expression
     * @param operation
     *     the operation if the value is equals, contains etc.
     * @param value
     *     the value to compare
     */
    public FilterBuilder or(AttributeSchema attributeSchema, SCIMDefinitions.FilterOperation operation, String value) {
        expression(attributeSchema, operation, value, SCIMDefinitions.FilterConcatenation.OR);
        return this;
    }

    /**
     * adds an or expression to the filter - if the filter has no expressions yet the "or" is ignored and a simple
     * filter expression is created.
     *
     * @param attributeNameOrUri
     *     the attribute name of the complete uri the attribute schema that will describe the attribute-name of the
     *     expression
     * @param operation
     *     the operation if the value is equals, contains etc.
     * @param value
     *     the value to compare
     */
    public FilterBuilder or(String attributeNameOrUri, SCIMDefinitions.FilterOperation operation, String value) {
        expression(attributeNameOrUri, operation, value, SCIMDefinitions.FilterConcatenation.OR);
        return this;
    }

    /**
     * adds a not expression to the filter.
     *
     * @param attributeSchema
     *     the attribute schema that will describe the attribute-name of the expression
     * @param operation
     *     the operation if the value is equals, contains etc.
     * @param value
     *     the value to compare
     */
    public FilterBuilder not(AttributeSchema attributeSchema, SCIMDefinitions.FilterOperation operation, String value) {
        expression(attributeSchema, operation, value, SCIMDefinitions.FilterConcatenation.NOT);
        return this;
    }

    /**
     * adds a not expression to the filter.
     *
     * @param attributeNameOrUri
     *     the attribute name or the complete uri
     * @param operation
     *     the operation if the value is equals, contains etc.
     * @param value
     *     the value to compare
     */
    public FilterBuilder not(String attributeNameOrUri, SCIMDefinitions.FilterOperation operation, String value) {
        expression(attributeNameOrUri, operation, value, SCIMDefinitions.FilterConcatenation.NOT);
        return this;
    }

    /**
     * prepares for adding an opening parenthesis for the next operation call.
     */
    public FilterBuilder leftParenthesis() {
        leftParenthesisCount++;
        addLeftParenthesis = true;
        return this;
    }

    /**
     * adds a right parenthesis.
     */
    public FilterBuilder rightParenthesis() {
        filter.append(')');
        rightParenthesisCount++;
        return this;
    }

    /**
     * builds the actual filter expression.
     *
     * @param attributeSchema
     *     the attribute schema that will describe the attribute-name of the expression
     * @param operation
     *     the operation if the value is equals, contains etc.
     * @param value
     *     the value to compare
     * @param concatenation
     *     the concatenation if the filter needs one yet
     */
    private void expression(AttributeSchema attributeSchema, SCIMDefinitions.FilterOperation operation, String value,
                            SCIMDefinitions.FilterConcatenation concatenation) {
        prependConcatenation(concatenation);
        if (addLeftParenthesis) {
            filter.append('(');
            addLeftParenthesis = false;
        }
        filter.append(buildAttributeName(attributeSchema))
            .append(' ')
            .append(operation.name().toLowerCase(Locale.ENGLISH));
        if (!PR.equals(operation)) {
            filter.append(' ')
                .append(ESCAPE_STRING_LITERAL)
                .append(value)
                .append(ESCAPE_STRING_LITERAL);
        }
    }

    /**
     * builds the actual filter expression.
     *
     * @param attributeNameOrUri
     *     the attribute name or the complete uri
     * @param operation
     *     the operation if the value is equals, contains etc.
     * @param value
     *     the value to compare
     * @param concatenation
     *     the concatenation if the filter needs one yet
     */
    private void expression(String attributeNameOrUri, SCIMDefinitions.FilterOperation operation, String value,
                            SCIMDefinitions.FilterConcatenation concatenation) {
        prependConcatenation(concatenation);
        if (addLeftParenthesis) {
            filter.append('(');
            addLeftParenthesis = false;
        }
        filter.append(attributeNameOrUri)
            .append(' ')
            .append(operation.name().toLowerCase(Locale.ENGLISH));
        if (!PR.equals(operation)) {
            filter.append(' ')
                .append(ESCAPE_STRING_LITERAL)
                .append(value)
                .append(ESCAPE_STRING_LITERAL);
        }
    }

    /**
     * prepends a concatenation to the current position of the filter.
     */
    private void prependConcatenation(SCIMDefinitions.FilterConcatenation concatenation) {
        if (filter.length() != 0) {
            filter.append(' ').append(concatenation.name().toLowerCase(Locale.ENGLISH)).append(' ');
        } else if (SCIMDefinitions.FilterConcatenation.NOT.equals(concatenation)) {
            filter.append(' ').append(concatenation.name().toLowerCase(Locale.ENGLISH)).append(' ');
        }
    }

    /**
     * build the attribute name as it must be entered into the filter expression.<br>
     * <br>
     * if the attribute is from the user or group core schema the simple attribute names will be used like "emails.type"
     * or "userName". But if the attribute comes from an extension - discovered by the fact that the attribute uri does
     * not start with one of the core schema uris - the full attribute uri is used as field-name
     */
    private String buildAttributeName(AttributeSchema attributeSchema) {
        if (isSCIMAttributeSchema(attributeSchema)) {
            SCIMAttributeSchema scimAttributeSchema = (SCIMAttributeSchema) attributeSchema;
            AttributeSchema parent = scimAttributeSchema.getParent();
            if (parent == null) {
                if (isAttributeFromCoreSchemas(scimAttributeSchema)) {
                    return attributeSchema.getName();
                } else {
                    return attributeSchema.getURI();
                }
            } else {
                return buildAttributeName(parent) + "." + attributeSchema.getName();
            }
        } else {
            return attributeSchema.getURI();
        }
    }

    /**
     * tells us if the given attribute is either a child of the user core schema or of the group core schema.
     */
    private boolean isAttributeFromCoreSchemas(AttributeSchema attributeSchema) {
        if (attributeSchema.getURI().startsWith(SCIMConstants.USER_CORE_SCHEMA_URI) ||
            attributeSchema.getURI().startsWith(SCIMConstants.GROUP_CORE_SCHEMA_URI)) {
            return true;
        }
        return false;
    }

    /**
     * rells us if the attribute is of type {@link SCIMAttributeSchema}.
     */
    private boolean isSCIMAttributeSchema(AttributeSchema attributeSchema) {
        return attributeSchema instanceof SCIMAttributeSchema;
    }

    /**
     * @return the desired filter string
     */
    public String build() {
        if (leftParenthesisCount != rightParenthesisCount) {
            LambdaExceptionUtils.rethrowConsumer(ex -> {
                throw (Exception) ex;
            }).accept(new InternalErrorException("invalid filter expression. You inserted " + leftParenthesisCount
                + " left parentheses and " + rightParenthesisCount + " right parentheses"));
        }
        return StringUtils.stripToNull(filter.toString());
    }


}
