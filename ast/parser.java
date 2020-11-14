package ast;

import java.util.ArrayList;

import regex.lexType;
import regex.lexeme;
import regex.lexer;

public class parser {
    public expectedOrder eo;
    public lexer lexer;

    /** The interpreted Java program. */
    public String programText;

    /** Indicates inside of a conditional. */
    private boolean inConditional = false;
    /** Indicates inside of a function. */
    private boolean inFunctionCall = false;

    /** The current lexeme to process */
    private int index = 0;
    private String results = "";

    public ArrayList<idInfo> ids;
    public ArrayList<String> functions;

    public parser(lexer lex){
        eo = new expectedOrder();
        lexer = lex;
        
        // initilize list of arrays
        ids = new ArrayList<idInfo>();
        // initialize list of function names
        functions = new ArrayList<String>();
    }

    // public static void main(String[] args) {
    //     String str = "a_variable_name \"string with 3 spaces!\"  \n";

    //     lexer lex = new lexer();
    //     lex.analyze(str);

    //     System.out.println(lex);

    //     parser par = new parser(lex);
    //     System.out.println("Parses: " + par.parse());
    // }

    public boolean parse() {
        if (eo.conforms(lexer)) {
            // interpret the program into java code since the program conforms to the expected order.
            programText = "import java.util.function.Function;\npublic class Test {\n" + "public static void run() {\n" + interpret() + "\n}\n" + "}\n";
            return true;
        } else {
            return false;
        }
    }

    /**
     * Interprets the parser's parse tree into java equivalent code.
     * 
     * @return A String of Java code.
     */
    private String interpret() {
        results = "";

        lexeme previous = new lexeme(null);
        for (index = 0; index < lexer.lexemes.size(); index++) {
            // get current lexeme
            lexeme lexeme = lexer.lexemes.get(index);
            results += interpretLexeme(previous, lexeme);

            previous = lexeme;
        }

        return results;
    }

    private String interpretLexeme(lexeme previous, lexeme lexeme) {
        String result = "";
        switch (lexeme.name) {
            case end_of_statement:
                result += (inConditional || inFunctionCall) ? ((inFunctionCall) ? ");\n" : ")\n") : ";\n";
                if (inConditional) {
                    inConditional = false;
                }
                if (inFunctionCall) {
                    inFunctionCall = false;
                }
                break;
            case id:
                if (isFunction(lexeme.value)) {
                    inFunctionCall = true;
                    result += lexeme.value + ".apply(";
                } else if (!isAlreadyAnId(lexeme.value)) {
                    result += " " + lexeme.value;
                    ids.add(new idInfo(lexeme.value, previous.value));
                } else if (previous.name != lexType.keyword) {
                    result += lexeme.value;
                } else if (previous.value.equals("while") || previous.value.equals("if") || previous.value.equals("ret")) {
                    result += lexeme.value;
                }
                break;
            case assignment:
                result += " = ";
                break;
            case keyword:
                result += processKeyword(lexeme);
                if (lexeme.value.contains("print")) {
                    index++;
                }
                break;
            case conditional:
                if (lexeme.value.equals("=")) {
                    result += "==";
                } else {
                    result += lexeme.value;
                }
                break;
            case end_of_block:
            result += lexeme.value;
                break;
            case string_literal:
            case integer_literal:
            case decimal_literal:
            case operator:
            case start_of_block:
                result += lexeme.value;
                break;

            default:
                System.out.println("Unhandled lexeme: " + lexeme.name + " Value: " + lexeme.value);
                break;
        }
        return result;
    }

    private boolean isFunction(String value) {
        for (String funcName : functions) {
            if (funcName.equals(value)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determines if the id has already been initialized.
     * 
     * @param value The String of the id.
     * @return True if the id has been initialized, false otherwise.
     */
    private boolean isAlreadyAnId(String value) {
        for (idInfo id : ids) {
            if (id.id.equals(value)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Process the given lexeme as a keyword.
     * 
     * @param lexeme the lexeme to process.
     * @param i      the current position of the interpretation of the parser's
     *               parse tree.
     * @return the interpreted String for the given lexeme.
     */
    private String processKeyword(lexeme lexeme) {
        lexeme nextLexeme = null;
        switch (lexeme.value) {
            case "print":
                if (lexer.lexemes.size() > index + 1) {
                    nextLexeme = lexer.lexemes.get(index + 1);
                }
                if (nextLexeme != null) {
                    return "System.out.print(" + nextLexeme.value + ");\n";
                } else {
                    return "System.out.print();\n";
                }
            case "printl":
                if (lexer.lexemes.size() > index + 1) {
                    nextLexeme = lexer.lexemes.get(index + 1);
                }
                if (nextLexeme != null && nextLexeme.name != lexType.end_of_statement) {
                    return "System.out.println(" + nextLexeme.value + ");\n";
                } else {
                    return "System.out.println();\n";
                }
            case "string":
                return "String";
            case "decimal":
                return "double";
            case "integer":
                return "int";
            case "while":
                inConditional = true;
                return "while(";
            case "if":
                inConditional = true;
                return "if(";
            case "ret":
                return "return ";
            case "func":
                // Function<String, Integer> func = x -> { return x.length(); };
                index++;
                lexeme prevLexeme = lexer.lexemes.get(index);
                String input = interpretLexeme(lexeme, prevLexeme), output = null;
                // get function name
                index++;
                nextLexeme = lexer.lexemes.get(index);
                if (nextLexeme.name != lexType.id) {
                    throw new Error("No function name supplied!");
                }
                String functionName = nextLexeme.value;
                // make sure function assignment was used
                index++;
                nextLexeme = lexer.lexemes.get(index);
                if (nextLexeme.name != lexType.function_assignment) {
                    throw new Error("Missing function assignment '->'");
                }
                // get output type for function
                index++;
                nextLexeme = lexer.lexemes.get(index);
                output = interpretLexeme(prevLexeme, nextLexeme);
                // get paramater name
                index++;
                nextLexeme = lexer.lexemes.get(index);
                if (nextLexeme.name != lexType.id) {
                    throw new Error("No function paramater name supplied!");
                }
                String param = interpretLexeme(prevLexeme, nextLexeme);
                // add param to ids
                ids.add(new idInfo(param, output));
                // get end of statement
                index++;
                nextLexeme = lexer.lexemes.get(index);
                if (nextLexeme.name != lexType.end_of_statement) {
                    throw new Error("Expected end of statement!");
                }
                // add function to list
                functions.add(functionName);
                return "Function<" + getFunctionType(input) + "," + getFunctionType(output) + "> " + functionName 
                                + "=" + param + "->";

            default:
                break;
        }
        return null;
    }

    /**
     * Get's the Java function type, since the Java anonymous functions require an Object type this function 
     * basically just returns the Object version of the given type.
     * @param type The Java primitive type or Class to be interpreted.
     * @return The type to use for functions.
     */
    private String getFunctionType(String type) {
        switch (type) {
            case "int":
                return "Integer";
            case "String":
                return "String";
            case "double":
                return "Double";
        
            default:
                return type;
        }
    }
}
