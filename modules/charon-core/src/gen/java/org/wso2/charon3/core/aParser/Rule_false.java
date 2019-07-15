package org.wso2.charon3.core.aParser;/* -----------------------------------------------------------------------------
 * Rule_false.java
 * -----------------------------------------------------------------------------
 *
 * Producer : com.parse2.aparse.Parser 2.5
 * Produced : Mon Jul 15 14:23:14 IST 2019
 *
 * -----------------------------------------------------------------------------
 */

import java.util.ArrayList;

final public class Rule_false extends Rule {
    public Rule_false(String spelling, ArrayList<Rule> rules) {
        super(spelling, rules);
    }

    public static Rule_false parse(ParserContext context) {
        context.push("false");

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
                    Rule rule = Terminal_NumericValue
                            .parse(context, "%x66.61.6c.73.65", "(\\x66\\x61\\x6c\\x73\\x65)", 5);
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
            rule = new Rule_false(context.text.substring(a0.start, a0.end), a0.rules);
        } else {
            context.index = s0;
        }

        context.pop("false", parsed);

        return (Rule_false) rule;
    }

    public Object accept(Visitor visitor) {
        return visitor.visit(this);
    }
}

/* -----------------------------------------------------------------------------
 * eof
 * -----------------------------------------------------------------------------
 */
