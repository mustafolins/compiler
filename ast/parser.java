package ast;

import java.util.ArrayList;

import regex.lexType;
import regex.lexeme;
import regex.lexer;

public class parser {
    public expectedOrder eo;
    public lexer lexer;
    public ArrayList<parseTree> parseTrees;

    /** The interpreted Java program. */
    public String programText;

    /** Indicates inside of a conditional. */
    private boolean inConditional = false;
    /** Indicates inside of a function. */
    private boolean inFunctionCall = false;
    private boolean inClass = false;
    boolean firstLineOfClass = false;

    /** The current lexeme to process */
    // private int index = 0;
    parseTree cur = null;
    private String results = "";
    private String classResults = "";

    public ArrayList<idInfo> ids;
    public ArrayList<functionInfo> functions;
    public ArrayList<String> classes;

    public parser(lexer lex){
        eo = new expectedOrder();
        lexer = lex;
        
        // initilize list of arrays
        ids = new ArrayList<idInfo>();
        // initialize list of function names
        functions = new ArrayList<functionInfo>();
        // initialize list of class names
        classes = new ArrayList<String>();

        // initialize list of parse trees
        parseTrees = new ArrayList<parseTree>();
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
        generateParseTrees(lexer);
        if (eo.conforms(parseTrees)) {
            // interpret the program into java code since the program conforms to the expected order.
            programText = "import java.util.function.Function;\n" 
                + interpretClasses() + "\npublic class Test {\n" + "public static void run() {\n" + interpret() + "\n}\n" + "}\n";
            return true;
        } else {
            return false;
        }
    }

    private void generateParseTrees(lexer lex) {
        parseTree curTree = null, tree = curTree;
        for (lexeme lexeme : lex.lexemes) {
            if (lexeme.type == lexType.end_of_statement && tree != null) {
                curTree.addChild(new parseTree(lexeme));
                parseTrees.add(tree);
                curTree = null;
                tree = null;
            }
            else if (curTree == null) {
                curTree = new parseTree(lexeme);
                if (tree == null) {
                    tree = curTree;
                }
            }
            else {
                curTree.addChild(new parseTree(lexeme));
                curTree = curTree.child;
            }
        }
    }

    private String interpretClasses() {
        classResults = "";

        lexeme previous = new lexeme(null);
        
        // loop through parse trees 
        for (parseTree parseTree : parseTrees) {
            cur = parseTree;
            if (cur.current.type == lexType.keyword) {
                if (cur.current.value == "obj") {
                    inClass = true;
                    
                    if (cur.child != null && cur.child.current.type == lexType.id) {
                        classes.add(cur.child.current.value);
                    }

                    firstLineOfClass = true;
                }
            }
            // only process classes
            if (inClass) {
                while (cur != null) {
                    lexeme lexeme = cur.current;
                    classResults += interpretLexeme(previous, lexeme);
        
                    previous = lexeme;
                                    
                    cur = cur.child;
                }
                firstLineOfClass = false;
            }
            if (parseTree.current.type == lexType.keyword) {
                if (parseTree.current.value == "endobj") {
                    inClass = false;
                }
            }
        }

        return classResults;
    }

    /**
     * Interprets the parser's parse tree into java equivalent code.
     * 
     * @return A String of Java code.
     */
    private String interpret() {
        results = "";

        lexeme previous = new lexeme(null);
        
        // loop through parse trees 
        for (parseTree parseTree : parseTrees) {
            cur = parseTree;
            if (cur.current.type == lexType.keyword) {
                if (cur.current.value == "obj") {
                    inClass = true;
                }
            }
            if (!inClass) {
                while (cur != null) {
                    lexeme lexeme = cur.current;
                    results += interpretLexeme(previous, lexeme);
        
                    previous = lexeme;
                                    
                    cur = cur.child;
                }
            }
            if (parseTree.current.type == lexType.keyword) {
                if (parseTree.current.value == "endobj") {
                    inClass = false;
                }
            }
        }

        return results;
    }

    private String interpretLexeme(lexeme previous, lexeme lexeme) {
        String result = "";
        switch (lexeme.type) {
            case end_of_statement:
                if (firstLineOfClass) {
                    result += "{\n";
                } else {
                    result += (inConditional || inFunctionCall) ? ((inFunctionCall) ? ");\n" : ")\n") : ";\n";
                }
                if (inConditional) {
                    inConditional = false;
                }
                if (inFunctionCall) {
                    inFunctionCall = false;
                }
                break;
            case id:
                if (isFunction(lexeme.value)) {
                    functionInfo info = getFunctionInfo(lexeme.value);
                    inFunctionCall = true;
                    if (info.inClass) {
                        result += lexeme.value + "(";
                    } else {
                        result += lexeme.value + ".apply(";
                    }
                } else if (!isAlreadyAnId(lexeme.value)) {
                    result += " " + lexeme.value;
                    ids.add(new idInfo(lexeme.value, previous.value));
                } else if (previous.type != lexType.keyword) {
                    result += lexeme.value;
                } else if (previous.value.equals("while") || previous.value.equals("if") || previous.value.equals("ret") || previous.value.equals("obj")) {
                    result += lexeme.value;
                } else {
                    if (previous.value.equals("new") && classExists(lexeme.value)) {
                        result += lexeme.value + "()";
                    } else {
                        if (inFunctionCall) {
                            result += lexeme.value;
                        } else {
                            throw new Error("Variable or function name not defined.");
                        }
                    }
                }
                break;
            case assignment:
                if (!isSameType(previous, cur.child.current)) {
                    throw new Error("Type mismatch!");
                }
                result += " = ";
                break;
            case keyword:
                result += processKeyword(lexeme);
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
            case class_accessor:
            case start_of_block:
                result += lexeme.value;
                break;

            default:
                System.out.println("Unhandled lexeme: " + lexeme.type + " Value: " + lexeme.value);
                break;
        }
        return result;
    }

    private boolean classExists(String value) {
        for (String cl : classes) {
            if (value.equals(cl)) {
                return true;
            }
        }
        return false;
    }

    private functionInfo getFunctionInfo(String value) {
        for (functionInfo info : functions) {
            if (info.name.equals(value)) {
                return info;
            }
        }
        return null;
    }

    private boolean isSameType(lexeme previous, lexeme current) {
        // edge case for class initialization
        if (current.value.equals("new")) {
            return true;
        }
        // get id info and make sure the are of the same literal
        idInfo info = getIdInfo(previous);
        if (info.type == "string" && isCorrectLiteral(lexType.string_literal)) {
            return true;
        } else if (info.type == "decimal" && isCorrectLiteral(lexType.decimal_literal)) {
            return true;
        } else if (info.type == "integer" && isCorrectLiteral(lexType.integer_literal)) {
            return true;
        }
        return false;
    }

    private boolean isCorrectLiteral(lexType literal) {
        parseTree assignmentExpression = cur.child;
        boolean isCorrect = true;
        while (assignmentExpression != null) {
            switch (literal) {
                case string_literal:
                    switch (assignmentExpression.current.type) {
                        case id:
                            idInfo tempInfo = getIdInfo(assignmentExpression.current);
                            // is a function?
                            if (tempInfo == null) {
                                if (!functionExists(assignmentExpression.current))
                                {
                                    isCorrect &= false;
                                }
                                break;
                            }
                            if (classExists(tempInfo.type)) {
                                isCorrect &= true;
                            }
                            if (!tempInfo.type.equals("string")) {
                                isCorrect &= false;
                            } else {
                                // only need one variable of type string because of string concatenation
                                return true;
                            }
                            break;
                        case operator:
                            if (!assignmentExpression.current.value.equals("+")) {
                                isCorrect &= false;
                            }
                            break;
                        case end_of_statement:
                        case string_literal:
                            // only need one variable of type string because of string concatenation
                            return true;
                        case integer_literal:
                        case decimal_literal:
                            break;
                    
                        default:
                            isCorrect &= false;
                    }
                    break;
                case integer_literal:
                    switch (assignmentExpression.current.type) {
                        case id:
                            idInfo tempInfo = getIdInfo(assignmentExpression.current);
                            // is a function?
                            if (tempInfo == null) {
                                if (!functionExists(assignmentExpression.current))
                                {
                                    isCorrect &= false;
                                }
                                break;
                            }
                            if (classExists(tempInfo.type)) {
                                isCorrect &= true;
                            }
                            if (!tempInfo.type.equals("integer")) {
                                isCorrect &= false;
                            }
                            break;
                        case operator:
                        case end_of_statement:
                        case integer_literal:
                            break;
                    
                        default:
                            isCorrect &= false;
                    }
                    break;
                case decimal_literal:
                    switch (assignmentExpression.current.type) {
                        case id:
                            idInfo tempInfo = getIdInfo(assignmentExpression.current);
                            // is a function?
                            if (tempInfo == null) {
                                if (!functionExists(assignmentExpression.current))
                                {
                                    isCorrect &= false;
                                }
                                break;
                            }
                            if (classExists(tempInfo.type)) {
                                isCorrect &= true;
                            }
                            if (!(tempInfo.type.equals("decimal") || tempInfo.type.equals("integer"))) {
                                isCorrect &= false;
                            }
                            break;
                        case operator:
                        case end_of_statement:
                        case decimal_literal:
                        case integer_literal:
                            break;
                    
                        default:
                            isCorrect &= false;
                    }
                    break;
            
                default:
                    break;
            }

            assignmentExpression = assignmentExpression.child;
        }
        return isCorrect;
    }

    private boolean functionExists(lexeme current) {
        for (functionInfo function : functions) {
            if (current.value.equals(function.name)) {
                return true;
            }
        }
        return false;
    }

    private idInfo getIdInfo(lexeme previous) {
        for (idInfo info : ids) {
            if (info.id.equals(previous.value)) {
                return info;
            }
        }
        return null;
    }

    private boolean isFunction(String value) {
        for (functionInfo function : functions) {
            if (function.name.equals(value)) {
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
                if (cur.child != null) {
                    nextLexeme = cur.child.current;
                }
                if (nextLexeme != null) {
                    inFunctionCall = true;
                    return "System.out.print(";
                } else {
                    return "System.out.print();\n";
                }
            case "printl":
                if (cur.child != null) {
                    nextLexeme = cur.child.current;
                }
                if (nextLexeme != null && nextLexeme.type != lexType.end_of_statement) {
                    inFunctionCall = true;
                    return "System.out.println(";
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
                lexeme prevLexeme = incrementCurrent();
                String input = interpretLexeme(lexeme, prevLexeme), output = null;
                // get function name
                nextLexeme = incrementCurrent();
                if (nextLexeme.type != lexType.id) {
                    throw new Error("No function name supplied!");
                }
                String functionName = nextLexeme.value;
                // make sure function assignment was used
                nextLexeme = incrementCurrent();
                if (nextLexeme.type != lexType.function_assignment) {
                    throw new Error("Missing function assignment '->'");
                }
                // get output type for function
                nextLexeme = incrementCurrent();
                output = interpretLexeme(prevLexeme, nextLexeme);
                // get paramater name
                nextLexeme = incrementCurrent();
                if (nextLexeme.type != lexType.id) {
                    throw new Error("No function paramater name supplied!");
                }
                String param = interpretLexeme(prevLexeme, nextLexeme);
                // add param to ids
                ids.add(new idInfo(param, output));
                // get end of statement
                nextLexeme = incrementCurrent();
                if (nextLexeme.type != lexType.end_of_statement) {
                    throw new Error("Expected end of statement!");
                }
                // add function to list
                if (isFunction(functionName)) {
                    throw new Error("Function name already in use!");
                }
                functions.add(new functionInfo(functionName, inClass));
                if (inClass) {
                    return "public " + getFunctionType(input) + " " + functionName + "(" + getFunctionType(output) + " " + param + ")";
                } else {
                    return "Function<" + getFunctionType(input) + "," + getFunctionType(output) + "> " + functionName 
                                    + "=" + param + "->";
                }
                case "obj":
                    if (inClass) {
                        return "class ";
                    }
                    return "";
                case "endobj":
                    if (inClass) {
                        return "}";
                    }
                    return "";
                case "new":
                    return "new ";

            default:
                break;
        }
        return null;
    }

    private lexeme incrementCurrent() {
        cur = cur.child;
        return cur.current;
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
