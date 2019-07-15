package org.wso2.charon3.core.aParser;/* -----------------------------------------------------------------------------
 * Displayer.java
 * -----------------------------------------------------------------------------
 *
 * Producer : com.parse2.aparse.Parser 2.5
 * Produced : Mon Jul 15 14:23:14 IST 2019
 *
 * -----------------------------------------------------------------------------
 */

import java.util.ArrayList;

public class Displayer implements Visitor
{

  public Object visit(Rule_PATH rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_attributePath rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_valuePath rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_valueFilter rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_attributeExpression rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_filter rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_filterDash rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_compareValue rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_compareOperation rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_attributeName rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_nameChar rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_subAttribute rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_URI rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_SP rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_alpha rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_digit rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_string rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_char rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_escape rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_quotation_mark rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_unescaped rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_hexDigit rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_false rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_null rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_true rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_number rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_exp rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_frac rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_int rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_decimal_point rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_digit1_9 rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_e rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_minus rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_plus rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_zero rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Terminal_StringValue value)
  {
    System.out.print(value.spelling);
    return null;
  }

  public Object visit(Terminal_NumericValue value)
  {
    System.out.print(value.spelling);
    return null;
  }

  private Object visitRules(ArrayList<Rule> rules)
  {
    for (Rule rule : rules)
      rule.accept(this);
    return null;
  }
}

/* -----------------------------------------------------------------------------
 * eof
 * -----------------------------------------------------------------------------
 */
