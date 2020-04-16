/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wso2.charon3.core.utils.codeutils;

import org.wso2.charon3.core.exceptions.BadRequestException;
import org.wso2.charon3.core.protocol.ResponseCodeConstants;
import org.wso2.charon3.core.schema.SCIMConstants;
import org.wso2.charon3.core.schema.SCIMResourceTypeSchema;
import org.wso2.charon3.core.utils.AttributeUtil;

import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * This class is basically for creating a binary tree which preserves the precedence order with simple
 * filter (eg : userName eq vindula) expressions as terminals of the tree and all the logical operators
 * (and, or, not)as the non-terminals of the tree.
 * <p>
 * All terminals are filter expressions hence denoted by ExpressionNodes and all non terminal nodes are operators hence
 * denoted by OperatorNodes.
 */

public class FilterTreeManager {

    private StreamTokenizer input;
    protected List<String> tokenList = null;
    private String symbol;
    private Node root;
    private SCIMResourceTypeSchema schema;

    public FilterTreeManager(String filterString, SCIMResourceTypeSchema schema) throws IOException {

        String encodedString = URLEncoder.encode(filterString, "UTF-8");
        String modifiedEncodedString = encodedString.replaceAll("\\+", " ");
        modifiedEncodedString = modifiedEncodedString.replaceAll("%28", "(");
        modifiedEncodedString = modifiedEncodedString.replaceAll("%29", ")");
        this.schema = schema;
        input = new StreamTokenizer(new StringReader(modifiedEncodedString));
        input.resetSyntax();
        // Default settings in StreamTokenizer syntax initializer.
        input.wordChars('a', 'z');
        input.wordChars('A', 'Z');
        // Specifies that all extended ASCII characters defined in HTML 4 standard, are word constituents.
        input.wordChars(128 + 32, 255);
        input.whitespaceChars(0, ' ');
        input.commentChar('/');
        input.quoteChar('"');
        input.quoteChar('\'');

        //Adding other string possible values
        input.wordChars('@', '@');
        input.wordChars(':', ':');
        input.wordChars('_', '_');
        input.wordChars('0', '9');
        input.wordChars('-', '-');
        input.wordChars('+', '+');
        input.wordChars('.', '.');
        input.wordChars('*', '*');
        input.wordChars('/', '/');
        input.wordChars('%', '%');

        tokenList = new ArrayList<String>();
        String concatenatedString = "";
        String decodedValue;

        while (input.nextToken() != StreamTokenizer.TT_EOF) {
            //ttype 40 is for the '('
            if (input.ttype == 40) {
                tokenList.add("(");
            } else if (input.ttype == 41) {
                //ttype 40 is for the ')'
                concatenatedString = concatenatedString.trim();
                tokenList.add(concatenatedString);
                concatenatedString = "";
                tokenList.add(")");
            } else if (input.ttype == StreamTokenizer.TT_WORD) {
                decodedValue = URLDecoder.decode(input.sval, "UTF-8");
                if (!(decodedValue.equalsIgnoreCase(SCIMConstants.OperationalConstants.AND)
                        || decodedValue.equalsIgnoreCase(SCIMConstants.OperationalConstants.OR) ||
                        decodedValue.equalsIgnoreCase(SCIMConstants.OperationalConstants.NOT))) {

                    // Remove quotes if there are starting and ending quotes.
                    decodedValue = removeStartingAndEndingQuotes(decodedValue);

                    //concatenate the string by adding spaces in between
                    concatenatedString += " " + decodedValue;

                } else {
                    concatenatedString = concatenatedString.trim();
                    if (!concatenatedString.equals("")) {
                        tokenList.add(concatenatedString);
                        concatenatedString = "";
                    }
                    tokenList.add(decodedValue);
                }
            } else if (input.ttype == '\"' || input.ttype == '\'') {
                concatenatedString += " " + input.sval;
            }
        }
        //Add to the list, if the filter is a simple filter
        if (!(concatenatedString.equals(""))) {
            tokenList.add(concatenatedString);
        }
    }

    /*
     * Builds the binary tree from the filterString
     *
     * @return
     * @throws BadRequestException
     */
    public Node buildTree() throws BadRequestException {
        expression();
        return root;
    }

    /**
     * We build the parser using the recursive descent parser technique.
     *
     * @throws BadRequestException
     */
    private void expression() throws BadRequestException {
        term();
        while (symbol.equals(String.valueOf(SCIMConstants.OperationalConstants.OR))) {
            OperationNode or = new OperationNode(SCIMConstants.OperationalConstants.OR);
            or.setLeftNode(root);
            term();
            or.setRightNode(root);
            root = or;
        }
    }

    /*
     * We build the parser using the recursive descent parser technique.
     *
     * @throws BadRequestException
     */
    private void term() throws BadRequestException {
        factor();
        while (symbol.equals(String.valueOf(SCIMConstants.OperationalConstants.AND))) {
            OperationNode and = new OperationNode(SCIMConstants.OperationalConstants.AND);
            and.setLeftNode(root);
            factor();
            and.setRightNode(root);
            root = and;
        }
    }

    /*
     * We build the parser using the recursive descent parser technique.
     *
     * @throws BadRequestException
     */
    private void factor() throws BadRequestException {
        symbol = nextSymbol();
        if (symbol.equals(String.valueOf(SCIMConstants.OperationalConstants.NOT))) {
            OperationNode not = new OperationNode(SCIMConstants.OperationalConstants.NOT);
            factor();
            not.setRightNode(root);
            root = not;
        } else if (symbol.equals(String.valueOf(SCIMConstants.OperationalConstants.LEFT))) {
            expression();
            symbol = nextSymbol(); // we don't care about ')'
        } else {
            if (!(symbol.equals(String.valueOf(SCIMConstants.OperationalConstants.RIGHT)))) {
                ExpressionNode expressionNode = new ExpressionNode();
                validateAndBuildFilterExpression(symbol, expressionNode);
                root = expressionNode;
                symbol = nextSymbol();
            } else {
            }

        }
    }

    /*
     * Validate the simple filter and build a ExpressionNode
     *
     * @param filterString
     * @param expressionNode
     * @throws BadRequestException
     */
    private void validateAndBuildFilterExpression(String filterString, ExpressionNode expressionNode)
            throws BadRequestException {
        //verify filter string. validation should be case insensitive
        if (!(Pattern.compile(Pattern.quote(SCIMConstants.OperationalConstants.EQ),
                Pattern.CASE_INSENSITIVE).matcher(filterString).find() ||
                Pattern.compile(Pattern.quote(SCIMConstants.OperationalConstants.NE),
                        Pattern.CASE_INSENSITIVE).matcher(filterString).find() ||
                Pattern.compile(Pattern.quote(SCIMConstants.OperationalConstants.CO),
                        Pattern.CASE_INSENSITIVE).matcher(filterString).find() ||
                Pattern.compile(Pattern.quote(SCIMConstants.OperationalConstants.SW),
                        Pattern.CASE_INSENSITIVE).matcher(filterString).find() ||
                Pattern.compile(Pattern.quote(SCIMConstants.OperationalConstants.EW),
                        Pattern.CASE_INSENSITIVE).matcher(filterString).find() ||
                Pattern.compile(Pattern.quote(SCIMConstants.OperationalConstants.PR),
                        Pattern.CASE_INSENSITIVE).matcher(filterString).find() ||
                Pattern.compile(Pattern.quote(SCIMConstants.OperationalConstants.GT),
                        Pattern.CASE_INSENSITIVE).matcher(filterString).find() ||
                Pattern.compile(Pattern.quote(SCIMConstants.OperationalConstants.GE),
                        Pattern.CASE_INSENSITIVE).matcher(filterString).find() ||
                Pattern.compile(Pattern.quote(SCIMConstants.OperationalConstants.LT),
                        Pattern.CASE_INSENSITIVE).matcher(filterString).find() ||
                Pattern.compile(Pattern.quote(SCIMConstants.OperationalConstants.LE),
                        Pattern.CASE_INSENSITIVE).matcher(filterString).find())) {
            String message = "Given filter operator is not supported.";
            throw new BadRequestException(message, ResponseCodeConstants.INVALID_FILTER);
        }

        String trimmedFilter = filterString.trim();
        String[] filterParts = null;

        if (Pattern.compile(Pattern.quote(SCIMConstants.OperationalConstants.EQ),
                Pattern.CASE_INSENSITIVE).matcher(filterString).find()) {
            filterParts = trimmedFilter.split(" eq | EQ | eQ | Eq ");
            setExpressionNodeValues(filterParts[0], SCIMConstants.OperationalConstants.EQ, filterParts[1],
                    expressionNode);
        } else if (Pattern.compile(Pattern.quote(SCIMConstants.OperationalConstants.NE),
                Pattern.CASE_INSENSITIVE).matcher(filterString).find()) {
            filterParts = trimmedFilter.split(" ne | NE | nE | Ne ");
            setExpressionNodeValues(filterParts[0], SCIMConstants.OperationalConstants.NE, filterParts[1],
                    expressionNode);
        } else if (Pattern.compile(Pattern.quote(SCIMConstants.OperationalConstants.CO),
                Pattern.CASE_INSENSITIVE).matcher(filterString).find()) {
            filterParts = trimmedFilter.split(" co | CO | cO | Co ");
            setExpressionNodeValues(filterParts[0], SCIMConstants.OperationalConstants.CO, filterParts[1],
                    expressionNode);
        } else if (Pattern.compile(Pattern.quote(SCIMConstants.OperationalConstants.SW),
                Pattern.CASE_INSENSITIVE).matcher(filterString).find()) {
            filterParts = trimmedFilter.split(" sw | SW | sW | Sw ");
            setExpressionNodeValues(filterParts[0], SCIMConstants.OperationalConstants.SW, filterParts[1],
                    expressionNode);
        } else if (Pattern.compile(Pattern.quote(SCIMConstants.OperationalConstants.EW),
                Pattern.CASE_INSENSITIVE).matcher(filterString).find()) {
            filterParts = trimmedFilter.split(" ew | EW | eW | Ew ");
            setExpressionNodeValues(filterParts[0], SCIMConstants.OperationalConstants.EW, filterParts[1],
                    expressionNode);
        } else if (Pattern.compile(Pattern.quote(SCIMConstants.OperationalConstants.PR),
                Pattern.CASE_INSENSITIVE).matcher(filterString).find()) {
            //with filter PR, there should not be whitespace after.
            filterParts = trimmedFilter.split(" pr| PR| pR| Pr");
            setExpressionNodeValues(filterParts[0], SCIMConstants.OperationalConstants.PR, null, expressionNode);
        } else if (Pattern.compile(Pattern.quote(SCIMConstants.OperationalConstants.GT),
                Pattern.CASE_INSENSITIVE).matcher(filterString).find()) {
            filterParts = trimmedFilter.split(" gt | GT | gT | Gt ");
            setExpressionNodeValues(filterParts[0], SCIMConstants.OperationalConstants.GT, filterParts[1],
                    expressionNode);

        } else if (Pattern.compile(Pattern.quote(SCIMConstants.OperationalConstants.GE),
                Pattern.CASE_INSENSITIVE).matcher(filterString).find()) {
            filterParts = trimmedFilter.split(" ge | GE | gE | Ge ");
            setExpressionNodeValues(filterParts[0], SCIMConstants.OperationalConstants.GE, filterParts[1],
                    expressionNode);
        } else if (Pattern.compile(Pattern.quote(SCIMConstants.OperationalConstants.LT),
                Pattern.CASE_INSENSITIVE).matcher(filterString).find()) {
            filterParts = trimmedFilter.split(" lt | LT | lT | Lt ");
            setExpressionNodeValues(filterParts[0], SCIMConstants.OperationalConstants.LT, filterParts[1],
                    expressionNode);
        } else if (Pattern.compile(Pattern.quote(SCIMConstants.OperationalConstants.LE),
                Pattern.CASE_INSENSITIVE).matcher(filterString).find()) {
            filterParts = trimmedFilter.split(" le | LE | lE | Le ");
            setExpressionNodeValues(filterParts[0], SCIMConstants.OperationalConstants.LE, filterParts[1],
                    expressionNode);
        } else {
            throw new BadRequestException(ResponseCodeConstants.INVALID_FILTER);
        }
    }

    /*
     * create a expression node from the given values
     *
     * @param attributeValue
     * @param operation
     * @param value
     * @param expressionNode
     */
    private void setExpressionNodeValues(String attributeValue, String operation,
                                         String value, ExpressionNode expressionNode) throws BadRequestException {
        expressionNode.setAttributeValue(AttributeUtil.getAttributeURI(attributeValue.trim(), schema));
        expressionNode.setOperation(operation.trim());
        if (value != null) {
            expressionNode.setValue(value.trim());
        }
    }

    /*
     * returns the first item in the list and rearrange the list
     *
     * @return
     */
    public String nextSymbol() {
        if (tokenList.size() == 0) {
            //no tokens are present in the list anymore/at all
            return String.valueOf(-1);
        } else {
            String value = tokenList.get(0);
            tokenList.remove(0);
            return value;
        }
    }

    private String removeStartingAndEndingQuotes(String decodedValue) {

        if (decodedValue.startsWith("\"") && decodedValue.endsWith("\"")) {
            decodedValue = decodedValue.replaceFirst("\"", "").replaceAll("\"$", "");
        } else if (decodedValue.startsWith("'") && decodedValue.endsWith("'")) {
            decodedValue = decodedValue.replaceFirst("'", "").replaceAll("'$", "");
        }
        return decodedValue;
    }
}
