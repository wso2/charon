package org.wso2.charon3.core.parser;/* -----------------------------------------------------------------------------
 * XmlDisplayer.java
 * -----------------------------------------------------------------------------
 *
 * Producer : com.parse2.aparse.Parser 2.5
 * Produced : Fri Jun 21 11:06:52 IST 2019
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

  public Object visit(Rule_attrPath rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<attrPath>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</attrPath>");
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

  public Object visit(Rule_valFilter rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<valFilter>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</valFilter>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_attrExp rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<attrExp>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</attrExp>");
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

  public Object visit(Rule_compValue rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<compValue>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</compValue>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_compareOp rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<compareOp>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</compareOp>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_ATTRNAME rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<ATTRNAME>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</ATTRNAME>");
    terminal = false;
    return null;
  }

  public Object visit(Rule_nameChar rule)
  {
//    if (!terminal) System.out.println();
//    System.out.print("<nameChar>");
//    terminal = false;
    visitRules(rule.rules);
//    if (!terminal) System.out.println();
//    System.out.print("</nameChar>");
//    terminal = false;
    return null;
  }

  public Object visit(Rule_subAttr rule)
  {
    if (!terminal) System.out.println();
    System.out.print("<subAttr>");
    terminal = false;
    visitRules(rule.rules);
    if (!terminal) System.out.println();
    System.out.print("</subAttr>");
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

  public Object visit(Rule_ALPHA rule)
  {
//    if (!terminal) System.out.println();
//    System.out.print("<ALPHA>");
//    terminal = false;
    visitRules(rule.rules);
//    if (!terminal) System.out.println();
//    System.out.print("</ALPHA>");
//    terminal = false;
    return null;
  }

  public Object visit(Rule_DIGIT rule)
  {
//    if (!terminal) System.out.println();
//    System.out.print("<DIGIT>");
//    terminal = false;
    visitRules(rule.rules);
//    if (!terminal) System.out.println();
//    System.out.print("</DIGIT>");
//    terminal = false;
    return null;
  }

  public Object visit(Rule_string rule)
  {
//    if (!terminal) System.out.println();
//    System.out.print("<string>");
//    terminal = false;
    visitRules(rule.rules);
//    if (!terminal) System.out.println();
//    System.out.print("</string>");
//    terminal = false;
    return null;
  }

  public Object visit(Rule_char rule)
  {
//    if (!terminal) System.out.println();
//    System.out.print("<char>");
//    terminal = false;
    visitRules(rule.rules);
//    if (!terminal) System.out.println();
//    System.out.print("</char>");
//    terminal = false;
    return null;
  }

  public Object visit(Rule_escape rule)
  {
//    if (!terminal) System.out.println();
//    System.out.print("<escape>");
//    terminal = false;
    visitRules(rule.rules);
//    if (!terminal) System.out.println();
//    System.out.print("</escape>");
//    terminal = false;
    return null;
  }

  public Object visit(Rule_quotation_mark rule)
  {
//    if (!terminal) System.out.println();
//    System.out.print("<quotation-mark>");
//    terminal = false;
    visitRules(rule.rules);
//    if (!terminal) System.out.println();
//    System.out.print("</quotation-mark>");
//    terminal = false;
    return null;
  }

  public Object visit(Rule_unescaped rule)
  {
//    if (!terminal) System.out.println();
//    System.out.print("<unescaped>");
//    terminal = false;
    visitRules(rule.rules);
//    if (!terminal) System.out.println();
//    System.out.print("</unescaped>");
//    terminal = false;
    return null;
  }

  public Object visit(Rule_HEXDIG rule)
  {
//    if (!terminal) System.out.println();
//    System.out.print("<HEXDIG>");
//    terminal = false;
    visitRules(rule.rules);
//    if (!terminal) System.out.println();
//    System.out.print("</HEXDIG>");
//    terminal = false;
    return null;
  }

  public Object visit(Rule_false rule)
  {
//    if (!terminal) System.out.println();
//    System.out.print("<false>");
//    terminal = false;
    visitRules(rule.rules);
//    if (!terminal) System.out.println();
//    System.out.print("</false>");
//    terminal = false;
    return null;
  }

  public Object visit(Rule_null rule)
  {
//    if (!terminal) System.out.println();
//    System.out.print("<null>");
//    terminal = false;
    visitRules(rule.rules);
//    if (!terminal) System.out.println();
//    System.out.print("</null>");
//    terminal = false;
    return null;
  }

  public Object visit(Rule_true rule)
  {
//    if (!terminal) System.out.println();
//    System.out.print("<true>");
//    terminal = false;
    visitRules(rule.rules);
//    if (!terminal) System.out.println();
//    System.out.print("</true>");
//    terminal = false;
    return null;
  }

  public Object visit(Rule_number rule)
  {
//    if (!terminal) System.out.println();
//    System.out.print("<number>");
//    terminal = false;
    visitRules(rule.rules);
//    if (!terminal) System.out.println();
//    System.out.print("</number>");
//    terminal = false;
    return null;
  }

  public Object visit(Rule_exp rule)
  {
//    if (!terminal) System.out.println();
//    System.out.print("<exp>");
//    terminal = false;
    visitRules(rule.rules);
//    if (!terminal) System.out.println();
//    System.out.print("</exp>");
//    terminal = false;
    return null;
  }

  public Object visit(Rule_frac rule)
  {
//    if (!terminal) System.out.println();
//    System.out.print("<frac>");
//    terminal = false;
    visitRules(rule.rules);
//    if (!terminal) System.out.println();
//    System.out.print("</frac>");
//    terminal = false;
    return null;
  }

  public Object visit(Rule_int rule)
  {
//    if (!terminal) System.out.println();
//    System.out.print("<int>");
//    terminal = false;
    visitRules(rule.rules);
//    if (!terminal) System.out.println();
//    System.out.print("</int>");
//    terminal = false;
    return null;
  }

  public Object visit(Rule_decimal_point rule)
  {
//    if (!terminal) System.out.println();
//    System.out.print("<decimal-point>");
//    terminal = false;
    visitRules(rule.rules);
//    if (!terminal) System.out.println();
//    System.out.print("</decimal-point>");
//    terminal = false;
    return null;
  }

  public Object visit(Rule_digit1_9 rule)
  {
//    if (!terminal) System.out.println();
//    System.out.print("<digit1-9>");
//    terminal = false;
    visitRules(rule.rules);
//    if (!terminal) System.out.println();
//    System.out.print("</digit1-9>");
//    terminal = false;
    return null;
  }

  public Object visit(Rule_e rule)
  {
//    if (!terminal) System.out.println();
//    System.out.print("<e>");
//    terminal = false;
    visitRules(rule.rules);
//    if (!terminal) System.out.println();
//    System.out.print("</e>");
//    terminal = false;
    return null;
  }

  public Object visit(Rule_minus rule)
  {
//    if (!terminal) System.out.println();
//    System.out.print("<minus>");
//    terminal = false;
    visitRules(rule.rules);
//    if (!terminal) System.out.println();
//    System.out.print("</minus>");
//    terminal = false;
    return null;
  }

  public Object visit(Rule_plus rule)
  {
//    if (!terminal) System.out.println();
//    System.out.print("<plus>");
//    terminal = false;
    visitRules(rule.rules);
//    if (!terminal) System.out.println();
//    System.out.print("</plus>");
//    terminal = false;
    return null;
  }

  public Object visit(Rule_zero rule)
  {
//    if (!terminal) System.out.println();
//    System.out.print("<zero>");
//    terminal = false;
    visitRules(rule.rules);
//    if (!terminal) System.out.println();
//    System.out.print("</zero>");
//    terminal = false;
    return null;
  }

  public Object visit(Rule_URI rule)
  {
//    if (!terminal) System.out.println();
//    System.out.print("<URI>");
//    terminal = false;
    visitRules(rule.rules);
//    if (!terminal) System.out.println();
//    System.out.print("</URI>");
//    terminal = false;
    return null;
  }

  public Object visit(Rule_hier_part rule)
  {
//    if (!terminal) System.out.println();
//    System.out.print("<hier-part>");
//    terminal = false;
    visitRules(rule.rules);
//    if (!terminal) System.out.println();
//    System.out.print("</hier-part>");
//    terminal = false;
    return null;
  }

  public Object visit(Rule_scheme rule)
  {
//    if (!terminal) System.out.println();
//    System.out.print("<scheme>");
//    terminal = false;
    visitRules(rule.rules);
//    if (!terminal) System.out.println();
//    System.out.print("</scheme>");
//    terminal = false;
    return null;
  }

  public Object visit(Rule_authority rule)
  {
//    if (!terminal) System.out.println();
//    System.out.print("<authority>");
//    terminal = false;
    visitRules(rule.rules);
//    if (!terminal) System.out.println();
//    System.out.print("</authority>");
//    terminal = false;
    return null;
  }

  public Object visit(Rule_userinfo rule)
  {
//    if (!terminal) System.out.println();
//    System.out.print("<userinfo>");
//    terminal = false;
    visitRules(rule.rules);
//    if (!terminal) System.out.println();
//    System.out.print("</userinfo>");
//    terminal = false;
    return null;
  }

  public Object visit(Rule_host rule)
  {
//    if (!terminal) System.out.println();
//    System.out.print("<host>");
//    terminal = false;
    visitRules(rule.rules);
//    if (!terminal) System.out.println();
//    System.out.print("</host>");
//    terminal = false;
    return null;
  }

  public Object visit(Rule_port rule)
  {
//    if (!terminal) System.out.println();
//    System.out.print("<port>");
//    terminal = false;
    visitRules(rule.rules);
//    if (!terminal) System.out.println();
//    System.out.print("</port>");
//    terminal = false;
    return null;
  }

  public Object visit(Rule_IP_literal rule)
  {
//    if (!terminal) System.out.println();
//    System.out.print("<IP-literal>");
//    terminal = false;
    visitRules(rule.rules);
//    if (!terminal) System.out.println();
//    System.out.print("</IP-literal>");
//    terminal = false;
    return null;
  }

  public Object visit(Rule_IPvFuture rule)
  {
//    if (!terminal) System.out.println();
//    System.out.print("<IPvFuture>");
//    terminal = false;
    visitRules(rule.rules);
//    if (!terminal) System.out.println();
//    System.out.print("</IPvFuture>");
//    terminal = false;
    return null;
  }

  public Object visit(Rule_IPv6address rule)
  {
//    if (!terminal) System.out.println();
//    System.out.print("<IPv6address>");
//    terminal = false;
    visitRules(rule.rules);
//    if (!terminal) System.out.println();
//    System.out.print("</IPv6address>");
//    terminal = false;
    return null;
  }

  public Object visit(Rule_h16 rule)
  {
//    if (!terminal) System.out.println();
//    System.out.print("<h16>");
//    terminal = false;
    visitRules(rule.rules);
//    if (!terminal) System.out.println();
//    System.out.print("</h16>");
//    terminal = false;
    return null;
  }

  public Object visit(Rule_ls32 rule)
  {
//    if (!terminal) System.out.println();
//    System.out.print("<ls32>");
//    terminal = false;
    visitRules(rule.rules);
//    if (!terminal) System.out.println();
//    System.out.print("</ls32>");
//    terminal = false;
    return null;
  }

  public Object visit(Rule_IPv4address rule)
  {
//    if (!terminal) System.out.println();
//    System.out.print("<IPv4address>");
//    terminal = false;
    visitRules(rule.rules);
//    if (!terminal) System.out.println();
//    System.out.print("</IPv4address>");
//    terminal = false;
    return null;
  }

  public Object visit(Rule_dec_octet rule)
  {
//    if (!terminal) System.out.println();
//    System.out.print("<dec-octet>");
//    terminal = false;
    visitRules(rule.rules);
//    if (!terminal) System.out.println();
//    System.out.print("</dec-octet>");
//    terminal = false;
    return null;
  }

  public Object visit(Rule_reg_name rule)
  {
//    if (!terminal) System.out.println();
//    System.out.print("<reg-name>");
//    terminal = false;
    visitRules(rule.rules);
//    if (!terminal) System.out.println();
//    System.out.print("</reg-name>");
//    terminal = false;
    return null;
  }

  public Object visit(Rule_path_abempty rule)
  {
//    if (!terminal) System.out.println();
//    System.out.print("<path-abempty>");
//    terminal = false;
    visitRules(rule.rules);
//    if (!terminal) System.out.println();
//    System.out.print("</path-abempty>");
//    terminal = false;
    return null;
  }

  public Object visit(Rule_path_absolute rule)
  {
//    if (!terminal) System.out.println();
//    System.out.print("<path-absolute>");
//    terminal = false;
    visitRules(rule.rules);
//    if (!terminal) System.out.println();
//    System.out.print("</path-absolute>");
//    terminal = false;
    return null;
  }

  public Object visit(Rule_path_rootless rule)
  {
//    if (!terminal) System.out.println();
//    System.out.print("<path-rootless>");
//    terminal = false;
    visitRules(rule.rules);
//    if (!terminal) System.out.println();
//    System.out.print("</path-rootless>");
//    terminal = false;
    return null;
  }

  public Object visit(Rule_path_empty rule)
  {
//    if (!terminal) System.out.println();
//    System.out.print("<path-empty>");
//    terminal = false;
    visitRules(rule.rules);
//    if (!terminal) System.out.println();
//    System.out.print("</path-empty>");
//    terminal = false;
    return null;
  }

  public Object visit(Rule_segment rule)
  {
//    if (!terminal) System.out.println();
//    System.out.print("<segment>");
//    terminal = false;
    visitRules(rule.rules);
//    if (!terminal) System.out.println();
//    System.out.print("</segment>");
//    terminal = false;
    return null;
  }

  public Object visit(Rule_segment_nz rule)
  {
//    if (!terminal) System.out.println();
//    System.out.print("<segment-nz>");
//    terminal = false;
    visitRules(rule.rules);
//    if (!terminal) System.out.println();
//    System.out.print("</segment-nz>");
//    terminal = false;
    return null;
  }

  public Object visit(Rule_pchar rule)
  {
//    if (!terminal) System.out.println();
//    System.out.print("<pchar>");
//    terminal = false;
    visitRules(rule.rules);
//    if (!terminal) System.out.println();
//    System.out.print("</pchar>");
//    terminal = false;
    return null;
  }

  public Object visit(Rule_query rule)
  {
//    if (!terminal) System.out.println();
//    System.out.print("<query>");
//    terminal = false;
    visitRules(rule.rules);
//    if (!terminal) System.out.println();
//    System.out.print("</query>");
//    terminal = false;
    return null;
  }

  public Object visit(Rule_fragment rule)
  {
//    if (!terminal) System.out.println();
//    System.out.print("<fragment>");
//    terminal = false;
    visitRules(rule.rules);
//    if (!terminal) System.out.println();
//    System.out.print("</fragment>");
//    terminal = false;
    return null;
  }

  public Object visit(Rule_pct_encoded rule)
  {
//    if (!terminal) System.out.println();
//    System.out.print("<pct-encoded>");
//    terminal = false;
    visitRules(rule.rules);
//    if (!terminal) System.out.println();
//    System.out.print("</pct-encoded>");
//    terminal = false;
    return null;
  }

  public Object visit(Rule_unreserved rule)
  {
//    if (!terminal) System.out.println();
//    System.out.print("<unreserved>");
//    terminal = false;
    visitRules(rule.rules);
//    if (!terminal) System.out.println();
//    System.out.print("</unreserved>");
//    terminal = false;
    return null;
  }

  public Object visit(Rule_sub_delims rule)
  {
//    if (!terminal) System.out.println();
//    System.out.print("<sub-delims>");
//    terminal = false;
    visitRules(rule.rules);
//    if (!terminal) System.out.println();
//    System.out.print("</sub-delims>");
//    terminal = false;
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
