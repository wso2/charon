package org.wso2.charon3.core.aParser;/* -----------------------------------------------------------------------------
 * Rule_filter.java
 * -----------------------------------------------------------------------------
 *
 * Producer : com.parse2.aparse.Parser 2.5
 * Produced : Mon Jul 15 14:23:14 IST 2019
 *
 * -----------------------------------------------------------------------------
 */

import java.util.ArrayList;

final public class Rule_filter extends Rule {
    public Rule_filter(String spelling, ArrayList<Rule> rules) {
        super(spelling, rules);
    }

    public static Rule_filter parse(ParserContext context) {
        context.push("filter");

        boolean parsed = true;
        int s0 = context.index;
        ParserAlternative a0 = new ParserAlternative(s0);

        ArrayList<ParserAlternative> as1 = new ArrayList<ParserAlternative>();
        parsed = false;
        {
            int s1 = context.index;
            ParserAlternative a1 = new ParserAlternative(s1);
            parsed = true;
            if (parsed) {
                boolean f1 = true;
                int c1 = 0;
                for (int i1 = 0; i1 < 1 && f1; i1++) {
                    int g1 = context.index;
                    ArrayList<ParserAlternative> as2 = new ArrayList<ParserAlternative>();
                    parsed = false;
                    {
                        int s2 = context.index;
                        ParserAlternative a2 = new ParserAlternative(s2);
                        parsed = true;
                        if (parsed) {
                            boolean f2 = true;
                            int c2 = 0;
                            for (int i2 = 0; i2 < 1 && f2; i2++) {
                                Rule rule = Rule_attributeExpression.parse(context);
                                if ((f2 = rule != null)) {
                                    a2.add(rule, context.index);
                                    c2++;
                                }
                            }
                            parsed = c2 == 1;
                        }
                        if (parsed) {
                            boolean f2 = true;
                            int c2 = 0;
                            for (int i2 = 0; i2 < 1 && f2; i2++) {
                                Rule rule = Rule_filterDash.parse(context);
                                if ((f2 = rule != null)) {
                                    a2.add(rule, context.index);
                                    c2++;
                                }
                            }
                            parsed = c2 == 1;
                        }
                        if (parsed) {
                            as2.add(a2);
                        }
                        context.index = s2;
                    }

                    ParserAlternative b = ParserAlternative.getBest(as2);

                    parsed = b != null;

                    if (parsed) {
                        a1.add(b.rules, b.end);
                        context.index = b.end;
                    }

                    f1 = context.index > g1;
                    if (parsed)
                        c1++;
                }
                parsed = c1 == 1;
            }
            if (parsed) {
                as1.add(a1);
            }
            context.index = s1;
        }
        {
            int s1 = context.index;
            ParserAlternative a1 = new ParserAlternative(s1);
            parsed = true;
            if (parsed) {
                boolean f1 = true;
                int c1 = 0;
                for (int i1 = 0; i1 < 1 && f1; i1++) {
                    int g1 = context.index;
                    ArrayList<ParserAlternative> as2 = new ArrayList<ParserAlternative>();
                    parsed = false;
                    {
                        int s2 = context.index;
                        ParserAlternative a2 = new ParserAlternative(s2);
                        parsed = true;
                        if (parsed) {
                            boolean f2 = true;
                            int c2 = 0;
                            for (int i2 = 0; i2 < 1 && f2; i2++) {
                                Rule rule = Rule_valuePath.parse(context);
                                if ((f2 = rule != null)) {
                                    a2.add(rule, context.index);
                                    c2++;
                                }
                            }
                            parsed = c2 == 1;
                        }
                        if (parsed) {
                            boolean f2 = true;
                            int c2 = 0;
                            for (int i2 = 0; i2 < 1 && f2; i2++) {
                                Rule rule = Rule_filterDash.parse(context);
                                if ((f2 = rule != null)) {
                                    a2.add(rule, context.index);
                                    c2++;
                                }
                            }
                            parsed = c2 == 1;
                        }
                        if (parsed) {
                            as2.add(a2);
                        }
                        context.index = s2;
                    }

                    ParserAlternative b = ParserAlternative.getBest(as2);

                    parsed = b != null;

                    if (parsed) {
                        a1.add(b.rules, b.end);
                        context.index = b.end;
                    }

                    f1 = context.index > g1;
                    if (parsed)
                        c1++;
                }
                parsed = c1 == 1;
            }
            if (parsed) {
                as1.add(a1);
            }
            context.index = s1;
        }
        {
            int s1 = context.index;
            ParserAlternative a1 = new ParserAlternative(s1);
            parsed = true;
            if (parsed) {
                boolean f1 = true;
                int c1 = 0;
                for (int i1 = 0; i1 < 1 && f1; i1++) {
                    int g1 = context.index;
                    ArrayList<ParserAlternative> as2 = new ArrayList<ParserAlternative>();
                    parsed = false;
                    {
                        int s2 = context.index;
                        ParserAlternative a2 = new ParserAlternative(s2);
                        parsed = true;
                        if (parsed) {
                            boolean f2 = true;
                            @SuppressWarnings("unused")
                            int c2 = 0;
                            for (int i2 = 0; i2 < 1 && f2; i2++) {
                                Rule rule = Terminal_StringValue.parse(context, "not");
                                if ((f2 = rule != null)) {
                                    a2.add(rule, context.index);
                                    c2++;
                                }
                            }
                            parsed = true;
                        }
                        if (parsed) {
                            boolean f2 = true;
                            int c2 = 0;
                            for (int i2 = 0; i2 < 1 && f2; i2++) {
                                Rule rule = Terminal_StringValue.parse(context, "(");
                                if ((f2 = rule != null)) {
                                    a2.add(rule, context.index);
                                    c2++;
                                }
                            }
                            parsed = c2 == 1;
                        }
                        if (parsed) {
                            boolean f2 = true;
                            int c2 = 0;
                            for (int i2 = 0; i2 < 1 && f2; i2++) {
                                Rule rule = Rule_filter.parse(context);
                                if ((f2 = rule != null)) {
                                    a2.add(rule, context.index);
                                    c2++;
                                }
                            }
                            parsed = c2 == 1;
                        }
                        if (parsed) {
                            boolean f2 = true;
                            int c2 = 0;
                            for (int i2 = 0; i2 < 1 && f2; i2++) {
                                Rule rule = Terminal_StringValue.parse(context, ")");
                                if ((f2 = rule != null)) {
                                    a2.add(rule, context.index);
                                    c2++;
                                }
                            }
                            parsed = c2 == 1;
                        }
                        if (parsed) {
                            boolean f2 = true;
                            int c2 = 0;
                            for (int i2 = 0; i2 < 1 && f2; i2++) {
                                Rule rule = Rule_filterDash.parse(context);
                                if ((f2 = rule != null)) {
                                    a2.add(rule, context.index);
                                    c2++;
                                }
                            }
                            parsed = c2 == 1;
                        }
                        if (parsed) {
                            as2.add(a2);
                        }
                        context.index = s2;
                    }

                    ParserAlternative b = ParserAlternative.getBest(as2);

                    parsed = b != null;

                    if (parsed) {
                        a1.add(b.rules, b.end);
                        context.index = b.end;
                    }

                    f1 = context.index > g1;
                    if (parsed)
                        c1++;
                }
                parsed = c1 == 1;
            }
            if (parsed) {
                as1.add(a1);
            }
            context.index = s1;
        }

        ParserAlternative b = ParserAlternative.getBest(as1);

        parsed = b != null;

        if (parsed) {
            a0.add(b.rules, b.end);
            context.index = b.end;
        }

        Rule rule = null;
        if (parsed) {
            rule = new Rule_filter(context.text.substring(a0.start, a0.end), a0.rules);
        } else {
            context.index = s0;
        }

        context.pop("filter", parsed);

        return (Rule_filter) rule;
    }

    public Object accept(Visitor visitor) {
        return visitor.visit(this);
    }
}

/* -----------------------------------------------------------------------------
 * eof
 * -----------------------------------------------------------------------------
 */
