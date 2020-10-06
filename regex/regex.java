package regex;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class regex {
    public String lastMatch;
    public int lastMatchLength;
    private String expression;

    public regex(String regExpression){
        expression = regExpression;
    }

    public boolean isMatch(final String str){
        try {
            boolean isMatch = true;
            lastMatchLength = 0;
            // this isn't a regular expression so it matches nothing?
            if (expression == null || expression.length() == 0) {
                return false;
            }

            Matcher matcher = Pattern.compile(expression).matcher(str);
            isMatch = matcher.find();
            if (!isMatch) {
                return false;
            }
            String firstMatch = matcher.group(0);
            lastMatchLength = firstMatch.length();
            
            lastMatch = firstMatch;
            return isMatch;
        } catch (Exception e) {
            return false;
        }
    }
}
