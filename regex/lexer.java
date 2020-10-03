package regex;

import java.util.ArrayList;

/**
 * regex
 */
public class lexer { 
    public ArrayList<lexeme> lexemes;
    private regex idReg = new regex("alpha.alphanum_*");
    private regex stringLiteralReg = new regex("\".notqoute*.\"");
    private regex integerLiteralReg = new regex("nzdigit.digit*");
    private regex decimalLiteralReg = new regex("nzdigit.digit*.dot.digit*");
    private regex endOfStatementReg = new regex("\n");
    private regex seperatorReg = new regex("space*");

    private ArrayList<String> keywords;
    private String lastKeyword;

    public lexer(){
        lexemes = new ArrayList<lexeme>();
        keywords = new ArrayList<String>();

        initializeKeywords();
    }

    // public static void main(String[] args) {
    //     String str = "a_variable_name\"string with 3 spaces!\"\n12.541\"test\"123v21a";

    //     lexer lex = new lexer();
    //     lex.analyze(str);

    //     System.out.println(toString(lex));
    // }

    private void initializeKeywords() {
        keywords.add("print");
    }

    public boolean isKeyword(String str){
        for (String keyword : keywords) {
            if (str.startsWith(keyword)) {
                lastKeyword = keyword;
                return true;
            }
        }
        return false;
    }

    public String toString() {
        String result = "Lexer Output:\n";
        for (lexeme lexeme : lexemes) {
            result += lexeme + "\n";
        }
        return result;
    }

    public void analyze(String str){
        while (str.length() > 0) {
            // keywords
            if (isKeyword(str)) {
                str = str.replaceFirst(lastKeyword, "");
                lexemes.add(new lexeme("keyword", lastKeyword));
            }
            // ids for variable names
            else if (idReg.isMatch(str)) {
                str = str.replaceFirst(idReg.lastMatch, "");
                lexemes.add(new lexeme("id", idReg.lastMatch));
            }
            // strings
            else if (stringLiteralReg.isMatch(str)) {
                str = str.replaceFirst(stringLiteralReg.lastMatch, "");
                lexemes.add(new lexeme("string_literal", stringLiteralReg.lastMatch));
            }
            // decimals
            else if (decimalLiteralReg.isMatch(str)) {
                str = str.replaceFirst(decimalLiteralReg.lastMatch, "");
                lexemes.add(new lexeme("decimal_literal", decimalLiteralReg.lastMatch));
            }
            // integers
            else if (integerLiteralReg.isMatch(str)) {
                str = str.replaceFirst(integerLiteralReg.lastMatch, "");
                lexemes.add(new lexeme("integer_literal", integerLiteralReg.lastMatch));
            }
            // end of statement
            else if (endOfStatementReg.isMatch(str)) {
                str = str.replaceFirst(endOfStatementReg.lastMatch, "");
                lexemes.add(new lexeme("end_of_statement", endOfStatementReg.lastMatch));
            }
            // seperator
            else if (seperatorReg.isMatch(str)) {
                str = str.replaceFirst(seperatorReg.lastMatch, "");
                lexemes.add(new lexeme("seperator", seperatorReg.lastMatch));
            }
        }
    }
}