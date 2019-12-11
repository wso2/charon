package org.wso2.charon3.core.aParser;/* -----------------------------------------------------------------------------
 * ParserContext.java
 * -----------------------------------------------------------------------------
 *
 * Producer : com.parse2.aparse.Parser 2.5
 * Produced : Mon Jul 15 14:23:14 IST 2019
 *
 * -----------------------------------------------------------------------------
 */

import java.util.Stack;

public class ParserContext {
    public final String text;
    private final boolean traceOn;
    public int index;
    private Stack<Integer> startStack = new Stack<Integer>();
    private Stack<String> callStack = new Stack<String>();
    private Stack<String> errorStack = new Stack<String>();
    private int level = 0;
    private int errorIndex = 0;

    public ParserContext(String text, boolean traceOn) {
        this.text = text;
        this.traceOn = traceOn;
        index = 0;
    }

    public void push(String rulename) {
        push(rulename, "");
    }

    public void push(String rulename, String trace) {
        callStack.push(rulename);
        startStack.push(new Integer(index));
    }

    public void pop(String function, boolean result) {
        Integer start = startStack.pop();
        callStack.pop();

        if (!result) {
            if (index > errorIndex) {
                errorIndex = index;
                errorStack = new Stack<String>();
                errorStack.addAll(callStack);
            } else if (index == errorIndex && errorStack.isEmpty()) {
                errorStack = new Stack<String>();
                errorStack.addAll(callStack);
            }
        } else {
            if (index > errorIndex)
                errorIndex = 0;
        }
    }

    public Stack<String> getErrorStack() {
        return errorStack;
    }

    public int getErrorIndex() {
        return errorIndex;
    }
}

/* -----------------------------------------------------------------------------
 * eof
 * -----------------------------------------------------------------------------
 */
