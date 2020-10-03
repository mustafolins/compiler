package regex;

public class regHelper {
    
    /**
     * Determins if the character is a non-zero digit.
     * @param ch
     * @return true if is a 1-9 and false otherwise
     */
    public static boolean isNzDigit(final char ch) {
        return ch == '1' || ch == '2' || ch == '3' || ch == '4' || ch == '5' || ch == '6' || ch == '7' || ch == '8' || ch == '9';
    }

    /**
     * Determins if the character is a digit.
     * @param ch
     * @return true if is 0-9 and false otherwise.
     */
    public static boolean isDigit(final char ch) {
        return ch == '0' || isNzDigit(ch);
    }

    /**
     * Determines if the character is a dot.
     * @param ch
     * @return True if character is a dot, false otherwise.
     */
    public static boolean isDot(final char ch){
        return ch == '.';
    }

    public static boolean isUnderscore(char ch) {
        return ch == '_';
    }

    /**
     * Determins if the character is a double quote.
     * @param ch
     * @return True if character is a ", false otherwise.
     */
    public static boolean isQuote(final char ch){
        return ch == '\"';
    }

    /**
     * Determines if the character is not a double qoute.
     * @param ch
     * @return True if character isn't a ", false otherwise.
     */
    public static boolean isNotQuote(final char ch){
        return !isQuote(ch);
    }

    public static boolean isCapitolLetter(final char ch){
        return (byte)ch >= 65 && (byte)ch <= 90;
    }

    public static boolean isLowerLetter(final char ch){
        return (byte)ch >= 97 && (byte)ch <= 122;
    }

    public static boolean isLetter(final char ch){
        return isCapitolLetter(ch) | isLowerLetter(ch);
    }
    
    public static boolean isLetterOrDigit(final char ch){
        return isLetter(ch) | isDigit(ch);
    }

    public static boolean isLetterOrDigitOrUnderscore(final char ch){
        return isLetterOrDigit(ch) | isUnderscore(ch);
    }

    public static boolean isLessThanSign(final char ch){
        return ch == '<';
    }

    public static boolean isGreaterThanSign(final char ch){
        return ch == '>';
    }

    public static boolean isColon(final char ch){
        return ch == ':';
    }

    public static boolean isEqualSign(final char ch){
        return ch == '=';
    }

    public static boolean isLeftParenthesis(final char ch){
        return ch == '(';
    }

    public static boolean isRightParenthesis(final char ch){
        return ch == ')';
    }

    public static boolean isNotParenthesis(final char ch){
        return !isLeftParenthesis(ch) && !isRightParenthesis(ch);
    }

    public static boolean isEndOfStatement(final char ch){
        return ch == '\n';
    }

    public static boolean isSpace(final char ch){
        return ch == ' ';
    }
}
