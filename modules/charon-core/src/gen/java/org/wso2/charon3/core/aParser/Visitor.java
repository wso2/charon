package org.wso2.charon3.core.aParser;/* -----------------------------------------------------------------------------
 * Visitor.java
 * -----------------------------------------------------------------------------
 *
 * Producer : com.parse2.aparse.Parser 2.5
 * Produced : Mon Jul 15 14:23:14 IST 2019
 *
 * -----------------------------------------------------------------------------
 */

public interface Visitor {

    Object visit(Rule_PATH rule);

    Object visit(Rule_attributePath rule);

    Object visit(Rule_valuePath rule);

    Object visit(Rule_valueFilter rule);

    Object visit(Rule_attributeExpression rule);

    Object visit(Rule_filter rule);

    Object visit(Rule_filterDash rule);

    Object visit(Rule_compareValue rule);

    Object visit(Rule_compareOperation rule);

    Object visit(Rule_attributeName rule);

    Object visit(Rule_nameChar rule);

    Object visit(Rule_subAttribute rule);

    Object visit(Rule_URI rule);

    Object visit(Rule_SP rule);

    Object visit(Rule_alpha rule);

    Object visit(Rule_digit rule);

    Object visit(Rule_string rule);

    Object visit(Rule_char rule);

    Object visit(Rule_escape rule);

    Object visit(Rule_quotation_mark rule);

    Object visit(Rule_unescaped rule);

    Object visit(Rule_hexDigit rule);

    Object visit(Rule_false rule);

    Object visit(Rule_null rule);

    Object visit(Rule_true rule);

    Object visit(Rule_number rule);

    Object visit(Rule_exp rule);

    Object visit(Rule_frac rule);

    Object visit(Rule_int rule);

    Object visit(Rule_decimal_point rule);

    Object visit(Rule_digit1_9 rule);

    Object visit(Rule_e rule);

    Object visit(Rule_minus rule);

    Object visit(Rule_plus rule);

    Object visit(Rule_zero rule);

    Object visit(Terminal_StringValue value);

    Object visit(Terminal_NumericValue value);
}

/* -----------------------------------------------------------------------------
 * eof
 * -----------------------------------------------------------------------------
 */
