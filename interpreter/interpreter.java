package interpreter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;

import ast.parser;
import regex.lexType;
import regex.lexeme;
import regex.lexer;

public class interpreter {
    public parser parser;
    public String programText;
    public String functionText;

    public ArrayList<idInfo> ids;
    public ArrayList<String> functions;
    private boolean inConditional = false;
    private boolean inFunctionCall = false;
    private int index = 0;
    private String results = "";

    public interpreter(parser par) {
        // initilize list of arrays
        ids = new ArrayList<idInfo>();
        // initialize list of function names
        functions = new ArrayList<String>();

        parser = par;
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

    //     parser par = new parser(lex);

    //     interpreter interpreter = new interpreter(par);
    //     interpreter.tryCompile();
    // }

    private static String ReadFile(String file) {
        try {
            String str = "";
            List<String> strings = Files.readAllLines(Paths.get(file));
            for (String string : strings) {
                str += string + "\n";
            }
            return str;
        } catch (SecurityException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Try to compile the code given that it succesfully parses.
     */
    public void tryCompile(boolean printInterpretation) {
        if (parser.parse()) {
            System.out.println("Successfully parsed program:");
            compile(printInterpretation);
        } else {
            System.out.println("Failed to parse program!");
        }
    }

    /**
     * Write the interepreted code to a class and then use the JavaCompiler to
     * compile the class.
     */
    private void compile(boolean printInterpretation) {
        programText = "import java.util.function.Function;\npublic class Test {\n" + "public static void run() {\n" + interpret() + "\n}\n" + "}\n";

        if (printInterpretation) {
            System.out.println("Java Interpretation:");
            System.out.println(programText);
        }

        try {
            // File temp = File.createTempFile("Test", ".java");
            File temp = new File("Test.java");
            temp.deleteOnExit();
            File parentDirectory = new File(System.getProperty("user.dir"));
            FileWriter writer = new FileWriter(temp);
            writer.write(programText);
            writer.close();

            // compile the source file
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
            fileManager.setLocation(StandardLocation.CLASS_OUTPUT, Arrays.asList(parentDirectory));
            Iterable<? extends JavaFileObject> compilationUnits = fileManager
                    .getJavaFileObjectsFromFiles(Arrays.asList(temp));
            // compile source code
            System.out.println("Compiling...");
            boolean compiled = compiler.getTask(null, fileManager, null, null, null, compilationUnits).call();
            // close file manager
            fileManager.close();

            // run the compiled class
            if (compiled) {
                run(parentDirectory);
            } else {
                System.out.println("Failed to compile!");
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
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
        for (index = 0; index < parser.lexer.lexemes.size(); index++) {
            // get current lexeme
            lexeme lexeme = parser.lexer.lexemes.get(index);
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
                if (parser.lexer.lexemes.size() > index + 1) {
                    nextLexeme = parser.lexer.lexemes.get(index + 1);
                }
                if (nextLexeme != null) {
                    return "System.out.print(" + nextLexeme.value + ");\n";
                } else {
                    return "System.out.print();\n";
                }
            case "printl":
                if (parser.lexer.lexemes.size() > index + 1) {
                    nextLexeme = parser.lexer.lexemes.get(index + 1);
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
                lexeme prevLexeme = parser.lexer.lexemes.get(index);
                String input = interpretLexeme(lexeme, prevLexeme), output = null;
                // get function name
                index++;
                nextLexeme = parser.lexer.lexemes.get(index);
                if (nextLexeme.name != lexType.id) {
                    throw new Error("No function name supplied!");
                }
                String functionName = nextLexeme.value;
                // make sure function assignment was used
                index++;
                nextLexeme = parser.lexer.lexemes.get(index);
                if (nextLexeme.name != lexType.function_assignment) {
                    throw new Error("Missing function assignment '->'");
                }
                // get output type for function
                index++;
                nextLexeme = parser.lexer.lexemes.get(index);
                output = interpretLexeme(prevLexeme, nextLexeme);
                // get paramater name
                index++;
                nextLexeme = parser.lexer.lexemes.get(index);
                if (nextLexeme.name != lexType.id) {
                    throw new Error("No function paramater name supplied!");
                }
                String param = interpretLexeme(prevLexeme, nextLexeme);
                // add param to ids
                ids.add(new idInfo(param, output));
                // get end of statement
                index++;
                nextLexeme = parser.lexer.lexemes.get(index);
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

    private String getFunctionType(String type) {
        switch (type) {
            case "int":
                return "Integer";
            case "String":
                return "String";
            case "double":
                return "Double";
        
            default:
                return "Object";
        }
    }

    /**
     * Runs the compiled interpreted class.
     * 
     * @param parentDirectory
     */
    private void run(File parentDirectory) {
        URLClassLoader classLoader;
        try {
            classLoader = URLClassLoader.newInstance(new URL[] { parentDirectory.toURI().toURL() });
            Class<?> helloClass = classLoader.loadClass("Test");
            Method[] methods = helloClass.getMethods();

            for (Method method : methods) {
                if (method.getName().equals("run")) {
                    System.out.println("Starting program:");
                    method.invoke(null);
                    // delete class and java files
                    Files.delete(Paths.get(parentDirectory + "/" + "Test.java"));
                    Files.delete(Paths.get(parentDirectory + "/" + "Test.class"));
                    return;
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
