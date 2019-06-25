package org.wso2.charon3.core.parser;/* -----------------------------------------------------------------------------
 * Visitor.java
 * -----------------------------------------------------------------------------
 *
 * Producer : com.parse2.aparse.Parser 2.5
 * Produced : Fri Jun 21 11:06:52 IST 2019
 *
 * -----------------------------------------------------------------------------
 */

public interface Visitor
{
  public Object visit(Rule_PATH rule);
  public Object visit(Rule_attrPath rule);
  public Object visit(Rule_valuePath rule);
  public Object visit(Rule_valFilter rule);
  public Object visit(Rule_attrExp rule);
  public Object visit(Rule_filter rule);
  public Object visit(Rule_filterDash rule);
  public Object visit(Rule_compValue rule);
  public Object visit(Rule_compareOp rule);
  public Object visit(Rule_ATTRNAME rule);
  public Object visit(Rule_nameChar rule);
  public Object visit(Rule_subAttr rule);
  public Object visit(Rule_SP rule);
  public Object visit(Rule_ALPHA rule);
  public Object visit(Rule_DIGIT rule);
  public Object visit(Rule_string rule);
  public Object visit(Rule_char rule);
  public Object visit(Rule_escape rule);
  public Object visit(Rule_quotation_mark rule);
  public Object visit(Rule_unescaped rule);
  public Object visit(Rule_HEXDIG rule);
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
  public Object visit(Rule_URI rule);
  public Object visit(Rule_hier_part rule);
  public Object visit(Rule_scheme rule);
  public Object visit(Rule_authority rule);
  public Object visit(Rule_userinfo rule);
  public Object visit(Rule_host rule);
  public Object visit(Rule_port rule);
  public Object visit(Rule_IP_literal rule);
  public Object visit(Rule_IPvFuture rule);
  public Object visit(Rule_IPv6address rule);
  public Object visit(Rule_h16 rule);
  public Object visit(Rule_ls32 rule);
  public Object visit(Rule_IPv4address rule);
  public Object visit(Rule_dec_octet rule);
  public Object visit(Rule_reg_name rule);
  public Object visit(Rule_path_abempty rule);
  public Object visit(Rule_path_absolute rule);
  public Object visit(Rule_path_rootless rule);
  public Object visit(Rule_path_empty rule);
  public Object visit(Rule_segment rule);
  public Object visit(Rule_segment_nz rule);
  public Object visit(Rule_pchar rule);
  public Object visit(Rule_query rule);
  public Object visit(Rule_fragment rule);
  public Object visit(Rule_pct_encoded rule);
  public Object visit(Rule_unreserved rule);
  public Object visit(Rule_sub_delims rule);

  public Object visit(Terminal_StringValue value);
  public Object visit(Terminal_NumericValue value);
}

/* -----------------------------------------------------------------------------
 * eof
 * -----------------------------------------------------------------------------
 */
