package org.wso2.charon3.core.aParser;/* -----------------------------------------------------------------------------
 * Visitor.java
 * -----------------------------------------------------------------------------
 *
 * Producer : com.parse2.aparse.Parser 2.5
 * Produced : Mon Jul 15 14:23:14 IST 2019
 *
 * -----------------------------------------------------------------------------
 */

public interface Visitor
{
  public Object visit(Rule_PATH rule);
  public Object visit(Rule_attributePath rule);
  public Object visit(Rule_valuePath rule);
  public Object visit(Rule_valueFilter rule);
  public Object visit(Rule_attributeExpression rule);
  public Object visit(Rule_filter rule);
  public Object visit(Rule_filterDash rule);
  public Object visit(Rule_compareValue rule);
  public Object visit(Rule_compareOperation rule);
  public Object visit(Rule_attributeName rule);
  public Object visit(Rule_nameChar rule);
  public Object visit(Rule_subAttribute rule);
  public Object visit(Rule_URI rule);
  public Object visit(Rule_SP rule);
  public Object visit(Rule_alpha rule);
  public Object visit(Rule_digit rule);
  public Object visit(Rule_string rule);
  public Object visit(Rule_char rule);
  public Object visit(Rule_escape rule);
  public Object visit(Rule_quotation_mark rule);
  public Object visit(Rule_unescaped rule);
  public Object visit(Rule_hexDigit rule);
  public Object visit(Rule_false rule);
  public Object visit(Rule_null rule);
  public Object visit(Rule_true rule);
  public Object visit(Rule_number rule);
  public Object visit(Rule_exp rule);
  public Object visit(Rule_frac rule);
  public Object visit(Rule_int rule);
  public Object visit(Rule_decimal_point rule);
  public Object visit(Rule_digit1_9 rule);
  public Object visit(Rule_e rule);
  public Object visit(Rule_minus rule);
  public Object visit(Rule_plus rule);
  public Object visit(Rule_zero rule);

  public Object visit(Terminal_StringValue value);
  public Object visit(Terminal_NumericValue value);
}

/* -----------------------------------------------------------------------------
 * eof
 * -----------------------------------------------------------------------------
 */
