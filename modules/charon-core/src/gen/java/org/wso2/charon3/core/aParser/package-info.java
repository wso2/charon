/*
 * Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

/**
 * This package classes generated using aparse-2.5.jar which is a free parser tool called
 * parse2( http://www.parse2.com/).
 * This parser parses the path attribute according to the ABNF rules specified in the SCIM Specification.
 * Rules used to generate this PATH paser are follows,
 * <p>
 * PATH = attributePath / valuePath [subAttribute];
 * <p>
 * attributePath  = [URI ":"] attributeName *1subAttribute;
 * <p>
 * valuePath = attributePath "[" valueFilter "]";
 * <p>
 * valueFilter = attributeExpression / filter / *1"not" "(" valueFilter ")";
 * <p>
 * attributeExpression = (attributePath SP "pr") / (attributePath SP compareOperation SP compareValue);
 * <p>
 * filter = ( attributeExpression filterDash ) / (valuePath filterDash) / ( *1"not" "(" filter ")" filterDash );
 * <p>
 * filterDash = SP ("and" / "or") SP filter filterDash / "";
 * <p>
 * compareValue = false / null / true / number / string;
 * <p>
 * compareOperation = "eq" / "ne" / "co" / "sw" / "ew" / "gt" / "lt" / "ge" / "le";
 * <p>
 * attributeName  = alpha *(nameChar);
 * <p>
 * nameChar  = "-" / "_" / digit / alpha;
 * <p>
 * subAttribute   = "." attributeName;
 * <p>
 * URI ="urn:ietf:params:scim:schemas:core:2.0:User" / "urn:ietf:params:scim:schemas:extension:enterprise:2.0:User" / "urn:ietf:params:scim:schemas:core:2.0:Group";
 * <p>
 * SP             =  %x20;
 * <p>
 * alpha          =  (%x41-5A) / (%x61-7A);
 * <p>
 * digit          =  %x30-39;
 * <p>
 * string = quotation-mark *char quotation-mark;
 * <p>
 * char = unescaped / escape ( %x22 / %x5C / %x2F / %x62 / %x66 / %x6E / %x72 / %x74 / %x75 4hexDigit );
 * <p>
 * escape = %x5C;
 * <p>
 * quotation-mark = %x22;
 * <p>
 * unescaped = %x20-21 / %x23-5B / %x5D-10FFFF;
 * <p>
 * hexDigit         =  digit / "A" / "B" / "C" / "D" / "E" / "F";
 * <p>
 * false = %x66.61.6c.73.65;
 * <p>
 * null  = %x6e.75.6c.6c;
 * <p>
 * true  = %x74.72.75.65;
 * <p>
 * number = [ minus ] int [ frac ] [ exp ];
 * <p>
 * exp = e [ minus / plus ] 1*digit;
 * <p>
 * frac = decimal-point 1*digit;
 * <p>
 * int = zero / ( digit1-9 *digit );
 * <p>
 * decimal-point = %x2E;
 * <p>
 * digit1-9 = %x31-39;
 * <p>
 * e = %x65 / %x45;
 * <p>
 * minus = %x2D;
 * <p>
 * plus = %x2B;
 * <p>
 * zero = %x30;
 */
package org.wso2.charon3.core.aParser;