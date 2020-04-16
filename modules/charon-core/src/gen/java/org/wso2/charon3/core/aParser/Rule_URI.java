package org.wso2.charon3.core.aParser;/* -----------------------------------------------------------------------------
 * Rule_URI.java
 * -----------------------------------------------------------------------------
 *
 * Producer : com.parse2.aparse.Parser 2.5
 * Produced : Mon Jul 15 14:23:14 IST 2019
 *
 * -----------------------------------------------------------------------------
 */

import java.util.ArrayList;
import java.util.List;

final public class Rule_URI extends Rule {

    public Rule_URI(String spelling, ArrayList<Rule> rules) {

        super(spelling, rules);
    }

    public static Rule_URI parse(ParserContext context) {

        context.push("URI");

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
                    Rule rule = Terminal_StringValue.parse(context, "urn:ietf:params:scim:schemas:core:2.0:User");
                    if ((f1 = rule != null)) {
                        a1.add(rule, context.index);
                        c1++;
                    }
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
                    Rule rule = Terminal_StringValue
                            .parse(context, "urn:ietf:params:scim:schemas:extension:enterprise:2.0:User");
                    if ((f1 = rule != null)) {
                        a1.add(rule, context.index);
                        c1++;
                    }
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
                    Rule rule = Terminal_StringValue.parse(context, "urn:ietf:params:scim:schemas:core:2.0:Group");
                    if ((f1 = rule != null)) {
                        a1.add(rule, context.index);
                        c1++;
                    }
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
            rule = new Rule_URI(context.text.substring(a0.start, a0.end), a0.rules);
        } else {
            context.index = s0;
        }

        context.pop("URI", parsed);

        return (Rule_URI) rule;
    }

    public static Rule_URI parse(ParserContext context, List<String> schemasList) {

        context.push("URI");

        boolean parsed = true;
        int s0 = context.index;
        ParserAlternative a0 = new ParserAlternative(s0);

        ArrayList<ParserAlternative> as1 = new ArrayList<ParserAlternative>();
        parsed = false;
        schemasList.forEach(schema->
                {
                    int s1 = context.index;
                    ParserAlternative a1 = new ParserAlternative(s1);
                    boolean parsedFlag = true;
                    if (parsedFlag) {
                        boolean f1 = true;
                        int c1 = 0;
                        for (int i1 = 0; i1 < 1 && f1; i1++) {
                            Rule rule = Terminal_StringValue.parse(context, schema);
                            if ((f1 = rule != null)) {
                                a1.add(rule, context.index);
                                c1++;
                            }
                        }
                        parsedFlag = c1 == 1;
                    }
                    if (parsedFlag) {
                        as1.add(a1);
                    }
                    context.index = s1;
                }
        );
        {
            int s1 = context.index;
            ParserAlternative a1 = new ParserAlternative(s1);
            parsed = true;
            if (parsed) {
                boolean f1 = true;
                int c1 = 0;
                for (int i1 = 0; i1 < 1 && f1; i1++) {
                    Rule rule = Terminal_StringValue.parse(context, "urn:ietf:params:scim:schemas:core:2.0:Group");
                    if ((f1 = rule != null)) {
                        a1.add(rule, context.index);
                        c1++;
                    }
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
            rule = new Rule_URI(context.text.substring(a0.start, a0.end), a0.rules);
        } else {
            context.index = s0;
        }

        context.pop("URI", parsed);

        return (Rule_URI) rule;
    }


    public Object accept(Visitor visitor) {

        return visitor.visit(this);
    }
}

/* -----------------------------------------------------------------------------
 * eof
 * -----------------------------------------------------------------------------
 */
