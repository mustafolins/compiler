package regex;

import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * regex
 */
public class lexer {
    public ArrayList<lexeme> lexemes;
    private regex commentReg = new regex("^[~].*\n");
    private regex idReg = new regex("^[a-zA-Z][a-zA-Z0-9_]*");
    private regex stringLiteralReg = new regex("^\"[^\"]*\"");
    private regex integerLiteralReg = new regex("^[\\-]*[1-9][0-9]*");
    private regex decimalLiteralReg = new regex("^[\\-]*[1-9][0-9]*\\.[0-9]*[1-9]");
    private regex endOfStatementReg = new regex("^\n");
    private regex startOfBlockReg = new regex("^\\{");
    private regex endOfBlockReg = new regex("^\\}");
    private regex seperatorReg = new regex("^[ \t]*");
    private regex operatorReg = new regex("^[\\+\\*\\/\\-]");
    private regex conditionalReg = new regex("^[<>=][=]*");
    private regex assignmentReg = new regex("^[:]");
    private regex functionAssignmentReg = new regex("^\\->");

    private ArrayList<String> keywords;
    private ArrayList<String> keywordsDescription;
    private String lastKeyword;

    public lexer() {
        lexemes = new ArrayList<lexeme>();
        keywords = new ArrayList<String>();
        keywordsDescription = new ArrayList<String>();

        initializeKeywords();
    }

    // public static void main(String[] args) {
    //     String str = "";
    //     if (args.length > 0) {
    //         str = ReadFile(args[0]);
    //     } else {
    //         str = ReadFile("code.txt");
    //     }

    //     lexer lex = new lexer();
    //     lex.analyze(str);

    //     System.out.println(lex);
    // }

    private void initializeKeywords() {
        keywords.add("ret");
        keywordsDescription.add("Signals that the preceding statement should be returned.");
        keywords.add("func");
        keywordsDescription.add("The start of the declaration of a function.");
        keywords.add("while");
        keywordsDescription.add("The start of a while loop.");
        keywords.add("if");
        keywordsDescription.add("The start of an if statement.");
        keywords.add("printl");
        keywordsDescription.add("Prints a line of text.");
        keywords.add("print");
        keywordsDescription.add("Prints the given string/id without new line.");
        keywords.add("integer");
        keywordsDescription.add("Initializes an integer type.");
        keywords.add("decimal");
        keywordsDescription.add("Initializes a decimal type.");
        keywords.add("string");
        keywordsDescription.add("Initializes a string literal.");
    }

    public boolean isKeyword(String str) {
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
        for (int i = 0; i < lexemes.size(); i++) {
            lexeme lexeme = lexemes.get(i);
            if (lexeme.type == lexType.keyword) {
                result += lexeme + "\t" + keywordsDescription.get(keywords.indexOf(lexeme.value)) + "\n";
            } else {
                result += lexeme + "\n";
            }
        }
        return result;
    }

    public void analyze(String str) {
        String lastMatch = "";
        while (str.length() > 0) {
            // comments
            if (commentReg.isMatch(str)) {
                // just remove the matched comment don't add it to the lexemes
                str = str.replaceFirst(commentReg.lastMatch, "");
                lastMatch = commentReg.lastMatch;
            }
            // start of block
            else if (startOfBlockReg.isMatch(str)) {
                str = str.replaceFirst(Pattern.quote(startOfBlockReg.lastMatch), "");
                lexemes.add(new lexeme(lexType.start_of_block, startOfBlockReg.lastMatch));
                lastMatch = startOfBlockReg.lastMatch;
            }
            // end of block
            else if (endOfBlockReg.isMatch(str)) {
                str = str.replaceFirst(Pattern.quote(endOfBlockReg.lastMatch), "");
                lexemes.add(new lexeme(lexType.end_of_block, endOfBlockReg.lastMatch));
                lastMatch = endOfBlockReg.lastMatch;
            }
            // fucntion assignment
            else if (functionAssignmentReg.isMatch(str)) {
                str = str.replaceFirst(Pattern.quote(functionAssignmentReg.lastMatch), "");
                lexemes.add(new lexeme(lexType.function_assignment, functionAssignmentReg.lastMatch));
                lastMatch = functionAssignmentReg.lastMatch;
            }
            // conditionals
            else if (conditionalReg.isMatch(str)) {
                str = str.replaceFirst(Pattern.quote(conditionalReg.lastMatch), "");
                lexemes.add(new lexeme(lexType.conditional, conditionalReg.lastMatch));
                lastMatch = conditionalReg.lastMatch;
            }
            // keywords
            else if (isKeyword(str)) {
                str = str.replaceFirst(lastKeyword, "");
                lexemes.add(new lexeme(lexType.keyword, lastKeyword));
                lastMatch = lastKeyword;
            }
            // assignment
            else if (assignmentReg.isMatch(str)) {
                str = str.replaceFirst(assignmentReg.lastMatch, "");
                lexemes.add(new lexeme(lexType.assignment, assignmentReg.lastMatch));
                lastMatch = assignmentReg.lastMatch;
            }
            // ids for variable names
            else if (idReg.isMatch(str)) {
                str = str.replaceFirst(idReg.lastMatch, "");
                lexemes.add(new lexeme(lexType.id, idReg.lastMatch));
                lastMatch = idReg.lastMatch;
            }
            // strings
            else if (stringLiteralReg.isMatch(str)) {
                str = str.replaceFirst(stringLiteralReg.lastMatch, "");
                lexemes.add(new lexeme(lexType.string_literal, stringLiteralReg.lastMatch));
                lastMatch = stringLiteralReg.lastMatch;
            }
            // decimals
            else if (decimalLiteralReg.isMatch(str)) {
                str = str.replaceFirst(decimalLiteralReg.lastMatch, "");
                lexemes.add(new lexeme(lexType.decimal_literal, decimalLiteralReg.lastMatch));
                lastMatch = decimalLiteralReg.lastMatch;
            }
            // integers
            else if (integerLiteralReg.isMatch(str)) {
                str = str.replaceFirst(integerLiteralReg.lastMatch, "");
                lexemes.add(new lexeme(lexType.integer_literal, integerLiteralReg.lastMatch));
                lastMatch = integerLiteralReg.lastMatch;
            }
            // operators
            else if (operatorReg.isMatch(str)) {
                // pattern.qoute here to force java to not use it's automatic regex crap since
                // (+, *) are reserved characters
                str = str.replaceFirst(Pattern.quote(operatorReg.lastMatch), "");
                lexemes.add(new lexeme(lexType.operator, operatorReg.lastMatch));
                lastMatch = operatorReg.lastMatch;
            }
            // end of statement
            else if (endOfStatementReg.isMatch(str)) {
                str = str.replaceFirst(endOfStatementReg.lastMatch, "");
                lexemes.add(new lexeme(lexType.end_of_statement, endOfStatementReg.lastMatch));
                lastMatch = endOfStatementReg.lastMatch;
            }
            // seperator
            else if (seperatorReg.isMatch(str)) {
                // just ignore seperators a.k.a spaces it's not necessary
                str = str.replaceFirst(seperatorReg.lastMatch, "");
                lastMatch = seperatorReg.lastMatch;
            } else {
                throw new Error("Unable to process string lexeme not found.");
            }
            if (lastMatch.length() == 0) {
                throw new Error("Unable to process string lexeme not found.");
            }
        }
        // add an end of statement just in case the code doesn't end with one
        lexemes.add(new lexeme(lexType.end_of_statement, "\n"));
    }
}