package org.wso2.charon3.core.aParser;/* -----------------------------------------------------------------------------
 * Terminal_NumericValue.java
 * -----------------------------------------------------------------------------
 *
 * Producer : com.parse2.aparse.Parser 2.5
 * Produced : Mon Jul 15 14:23:14 IST 2019
 *
 * -----------------------------------------------------------------------------
 */

import java.util.ArrayList;
import java.util.regex.Pattern;

public class Terminal_NumericValue extends Rule {

    private Terminal_NumericValue(String spelling, ArrayList<Rule> rules) {

        super(spelling, rules);
    }

    public static Terminal_NumericValue parse(ParserContext context, String spelling, String regex, int length) {

        context.push("NumericValue", spelling + "," + regex);

        boolean parsed = true;

        Terminal_NumericValue numericValue = null;
        try {
            String value = context.text.substring(context.index, context.index + length);

            if ((parsed = Pattern.matches(regex, value))) {
                context.index += length;
                numericValue = new Terminal_NumericValue(value, null);
            }
        } catch (IndexOutOfBoundsException e) {
            parsed = false;
        }

        context.pop("NumericValue", parsed);

        return numericValue;
    }

    public Object accept(Visitor visitor) {

        return visitor.visit(this);
    }
}
/* -----------------------------------------------------------------------------
 * eof
 * -----------------------------------------------------------------------------
 */
