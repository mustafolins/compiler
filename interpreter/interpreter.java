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
import regex.lexeme;
import regex.lexer;

public class interpreter {
    public parser parser;
    public String programText;

    public ArrayList<idInfo> ids;

    public interpreter(parser par) {
        // initilize list of arrays
        ids = new ArrayList<idInfo>();

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
        programText = "public class Test {\n" + "public static void run() {\n" + interpret() + "\n}\n" + "}\n";

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
        String results = "";

        String prevId = "";
        lexeme previous = new lexeme(null);
        for (int i = 0; i < parser.lexer.lexemes.size(); i++) {
            // get current lexeme
            lexeme lexeme = parser.lexer.lexemes.get(i);
            switch (lexeme.name) {
                case "end_of_statement":
                    results += ";\n";
                    break;
                case "id":
                    if (!isAlreadyAnId(lexeme.value)) {
                        results += " " + lexeme.value + ";\n";
                        ids.add(new idInfo(lexeme.value, previous.value));
                    } else if (!previous.name.equals("keyword")) {
                        results += lexeme.value;
                    }
                    prevId = lexeme.value;
                    break;
                case "assignment":
                    results += prevId + " = ";
                    break;
                case "string_literal":
                    results += lexeme.value;
                    break;
                case "integer_literal":
                    results += lexeme.value;
                    break;
                case "decimal_literal":
                    results += lexeme.value;
                    break;
                case "keyword":
                    results += processKeyword(lexeme, i);
                    if (lexeme.value.contains("print")) {
                        i++;
                    }
                    break;
                case "operator":
                    results += lexeme.value;
                    break;

                default:
                    System.out.println("Unhandled lexeme: " + lexeme.name + " Value: " + lexeme.value);
                    break;
            }

            previous = lexeme;
        }

        return results;
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
    private String processKeyword(lexeme lexeme, int i) {
        lexeme nextLexeme = null;
        switch (lexeme.value) {
            case "print":
                if (parser.lexer.lexemes.size() > i + 1) {
                    nextLexeme = parser.lexer.lexemes.get(i + 1);
                }
                if (nextLexeme != null) {
                    return "System.out.print(" + nextLexeme.value + ");\n";
                } else {
                    return "System.out.print();\n";
                }
            case "printl":
                if (parser.lexer.lexemes.size() > i + 1) {
                    nextLexeme = parser.lexer.lexemes.get(i + 1);
                }
                if (nextLexeme != null && !nextLexeme.name.equals("end_of_statement")) {
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

            default:
                break;
        }
        return null;
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
        }
    }
}
