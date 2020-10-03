package regex;

import java.util.ArrayList;

public class regex {
    public ArrayList<condition> conditions;
    public String lastMatch;
    public int lastMatchLength;

    public regex(condition firstCondition){
        addFirstCondition(firstCondition);
    }

    private void addFirstCondition(condition firstCondition) {
        conditions = new ArrayList<>();
        conditions.add(firstCondition);
        lastMatchLength = 0;
    }

    public regex(String regExpression){
        // the expression should be a valid regex for example "dot.dot" would signify 
        // the "\\." is needed to signal the .
        String[] concatentations = regExpression.split("\\.");
        // if there are concatenations
        if (concatentations.length > 0) {
            for (String expression : concatentations) {
                addConditionFromString(expression);
            }
        } else {
            // no concatenations
            addConditionFromString(regExpression);
        }
    }

    private condition addConditionFromString(String expression) {
        // the "\\*" is needed to signal the *
        String[] expressions = expression.split("\\*");
        String type = expressions[0];
        boolean isMany = expression.contains("*");
        switch (type) {
            case "dot":
                addCondition(new condition(ch -> regHelper.isDot(ch), isMany));
                break;
            case "\"":
                addCondition(new condition(ch -> regHelper.isQuote(ch), isMany));
                break;
            case "digit":
                addCondition(new condition(ch -> regHelper.isDigit(ch), isMany));
                break;
            case "nzdigit":
                addCondition(new condition(ch -> regHelper.isNzDigit(ch), isMany));
                break;
            case "notqoute":
                addCondition(new condition(ch -> regHelper.isNotQuote(ch), isMany));
                break;
            case "alpha":
                addCondition(new condition(ch -> regHelper.isLetter(ch), isMany));
                break;
            case "alphanum":
                addCondition(new condition(ch -> regHelper.isLetterOrDigit(ch), isMany));
                break;
            case "alphanum_":
                addCondition(new condition(ch -> regHelper.isLetterOrDigitOrUnderscore(ch), isMany));
                break;
            case "<":
                addCondition(new condition(ch -> regHelper.isLessThanSign(ch), isMany));
                break;
            case ">":
                addCondition(new condition(ch -> regHelper.isGreaterThanSign(ch), isMany));
                break;
            case ":":
                addCondition(new condition(ch -> regHelper.isColon(ch), isMany));
                break;
            case "=":
                addCondition(new condition(ch -> regHelper.isEqualSign(ch), isMany));
                break;
            case "(":
                addCondition(new condition(ch -> regHelper.isLeftParenthesis(ch), isMany));
                break;
            case ")":
                addCondition(new condition(ch -> regHelper.isRightParenthesis(ch), isMany));
                break;
            case "notParenthesis":
                addCondition(new condition(ch -> regHelper.isNotParenthesis(ch), isMany));
                break;
            case "\n":
                addCondition(new condition(ch -> regHelper.isEndOfStatement(ch), isMany));
                break;
            case "space":
                addCondition(new condition(ch -> regHelper.isSpace(ch), isMany));
                break;
        
            default:
                System.err.println("Unhandled regular expression.");
                break;
        }
        return null;
    }

    public void addCondition(condition condition) {
        if (conditions == null || conditions.isEmpty()) {
            addFirstCondition(condition);
        } else {
            conditions.add(condition);
        }
    }

    public boolean isMatch(final String str){
        try {
            boolean isMatch = true;
            lastMatchLength = 0;
            // there aren't any conditions so nothing matches?
            if (conditions == null) {
                return false;
            }
            // iterate through conditions and match them
            for (condition condition : conditions) {
                if (condition.isMultiple) {
                    // multiple (*) so it can be any number of matching tests
                    while (str.length() > lastMatchLength && condition.condition.test(str.charAt(lastMatchLength))) {
                        lastMatchLength++;
                    }
                } else if (condition.condition.test(str.charAt(lastMatchLength))) {
                    // isn't multiple so it needs to match one time
                    lastMatchLength++;
                } else {
                    // didn't match one time and wasn't multiple so it isn't a match
                    return false;
                }
            }
            lastMatch = str.substring(0, lastMatchLength);
            return isMatch;
        } catch (Exception e) {
            return false;
        }
    }
}
