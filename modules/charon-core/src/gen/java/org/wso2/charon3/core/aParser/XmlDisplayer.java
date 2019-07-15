package org.wso2.charon3.core.aParser;/* -----------------------------------------------------------------------------
 * XmlDisplayer.java
 * -----------------------------------------------------------------------------
 *
 * Producer : com.parse2.aparse.Parser 2.5
 * Produced : Mon Jul 15 14:23:14 IST 2019
 *
 * -----------------------------------------------------------------------------
 */

import java.util.ArrayList;

public class XmlDisplayer implements Visitor
{
  private boolean terminal = true;

  public Object visit(Rule_PATH rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<PATH>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</PATH>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_attributePath rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<attributePath>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</attributePath>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_valuePath rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<valuePath>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</valuePath>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_valueFilter rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<valueFilter>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</valueFilter>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_attributeExpression rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<attributeExpression>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</attributeExpression>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_filter rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<filter>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</filter>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_filterDash rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<filterDash>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</filterDash>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_compareValue rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<compareValue>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</compareValue>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_compareOperation rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<compareOperation>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</compareOperation>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_attributeName rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<attributeName>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</attributeName>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_nameChar rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<nameChar>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</nameChar>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_subAttribute rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<subAttribute>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</subAttribute>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_URI rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<URI>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</URI>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_SP rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<SP>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</SP>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_alpha rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<alpha>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</alpha>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_digit rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<digit>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</digit>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_string rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<string>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</string>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_char rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<char>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</char>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_escape rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<escape>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</escape>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_quotation_mark rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<quotation-mark>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</quotation-mark>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_unescaped rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<unescaped>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</unescaped>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_hexDigit rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<hexDigit>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</hexDigit>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_false rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<false>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</false>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_null rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<null>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</null>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_true rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<true>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</true>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_number rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<number>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</number>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_exp rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<exp>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</exp>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_frac rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<frac>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</frac>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_int rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<int>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</int>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_decimal_point rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<decimal-point>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</decimal-point>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_digit1_9 rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<digit1-9>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</digit1-9>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_e rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<e>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</e>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_minus rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<minus>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</minus>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_plus rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<plus>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</plus>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_zero rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<zero>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</zero>");
    terminal = false;
    return null;
  }

  public Object visit(Terminal_StringValue value)
  {
    System.out.print(value.spelling);
    terminal = true;
    return null;
  }

  public Object visit(Terminal_NumericValue value)
  {
    System.out.print(value.spelling);
    terminal = true;
    return null;
  }

  private Boolean visitRules(ArrayList<Rule> rules)
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
