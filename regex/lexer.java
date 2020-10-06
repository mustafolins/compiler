package regex;

import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * regex
 */
public class lexer { 
    public ArrayList<lexeme> lexemes;
    private regex idReg = new regex("^[a-zA-Z][a-zA-Z0-9_]*");
    private regex stringLiteralReg = new regex("^\"[^\"]*\"");
    private regex integerLiteralReg = new regex("^[1-9][0-9]*");
    private regex decimalLiteralReg = new regex("^[1-9][0-9]*\\.[0-9]*[1-9]");
    private regex endOfStatementReg = new regex("^\n");
    private regex seperatorReg = new regex("^[ ]*");
    private regex operatorReg = new regex("^[\\+\\*\\/\\-]");
    private regex assignmentReg = new regex("^[:]");

    private ArrayList<String> keywords;
    private String lastKeyword;

    public lexer(){
        lexemes = new ArrayList<lexeme>();
        keywords = new ArrayList<String>();

        initializeKeywords();
    }

    // public static void main(String[] args) {
    //     String str = "a_variable_name\"string with 3 spaces!\"\n12.541+\"test\"12+3v21a:123678";

    //     lexer lex = new lexer();
    //     lex.analyze(str);

    //     System.out.println(lex);
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

    /**
     * Print all found lexemes.
     */
    public String toString() {
        String result = "Lexer Output:\n";
        for (lexeme lexeme : lexemes) {
            result += lexeme + "\n";
        }
        return result;
    }

    public void analyze(String str){
        while (str.length() > 0) {
            // operators
            if (operatorReg.isMatch(str)) {
                // pattern.qoute here to force java to not use it's automatic regex crap since (+, *) are reserved characters
                str = str.replaceFirst(Pattern.quote(operatorReg.lastMatch), "");
                lexemes.add(new lexeme("operator", operatorReg.lastMatch));
            }
            // keywords
            else if (isKeyword(str)) {
                str = str.replaceFirst(lastKeyword, "");
                lexemes.add(new lexeme("keyword", lastKeyword));
            }
            // assignment
            else if (assignmentReg.isMatch(str)) {
                str = str.replaceFirst(assignmentReg.lastMatch, "");
                lexemes.add(new lexeme("assignment", assignmentReg.lastMatch));
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
            else {
                throw new Error("Unable to process string lexeme not found.");
            }
        }
    }
}