package org.wso2.charon3.core.parser;/* -----------------------------------------------------------------------------
 * Displayer.java
 * -----------------------------------------------------------------------------
 *
 * Producer : com.parse2.aparse.Parser 2.5
 * Produced : Fri Jun 21 11:06:52 IST 2019
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

  public Object visit(Rule_attrPath rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_valuePath rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_valFilter rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_attrExp rule)
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

  public Object visit(Rule_compValue rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_compareOp rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_ATTRNAME rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_nameChar rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_subAttr rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_SP rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_ALPHA rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_DIGIT rule)
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

  public Object visit(Rule_HEXDIG rule)
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

  public Object visit(Rule_URI rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_hier_part rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_scheme rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_authority rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_userinfo rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_host rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_port rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_IP_literal rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_IPvFuture rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_IPv6address rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_h16 rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_ls32 rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_IPv4address rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_dec_octet rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_reg_name rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_path_abempty rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_path_absolute rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_path_rootless rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_path_empty rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_segment rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_segment_nz rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_pchar rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_query rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_fragment rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_pct_encoded rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_unreserved rule)
  {
    return visitRules(rule.rules);
  }

  public Object visit(Rule_sub_delims rule)
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
