package interpreter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;

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

    public ArrayList<String> ids;

    public interpreter(parser par) {
        // initilize list of arrays
        ids = new ArrayList<String>();

        parser = par;
        if (parser.parse()) {
            System.out.println("Successfully parsed program:");
            compile();
        } else {
            System.out.println("Failed to parse program!");
        }
    }

    public static void main(String[] args) {
        String str = "a_variable_name \"string with 3 spaces!\"  \n"
                    + "print a_variable_name \n";

        lexer lex = new lexer();
        lex.analyze(str);

        System.out.println(lex);

        parser par = new parser(lex);

        interpreter interpreter = new interpreter(par);
    }

    private void compile() {
        programText = "public class Test {\n" + "public static void run() {\n" + interpret()
                + "\n}\n" + "}\n";

        System.out.println(programText);

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

    private String interpret() {
        String results = "";

        String prevId = "";
        for (int i = 0; i < parser.pTree.lexemes.size(); i++) {
            lexeme lexeme = parser.pTree.lexemes.get(i);
            switch (lexeme.name) {
                case "end_of_statement":
                    results += ";\n";
                    break;
                case "id":
                    if (!isAlreadyAnId(lexeme.value)) {
                        results += "Object " + lexeme.value + ";\n";
                        prevId = lexeme.value;
                        ids.add(lexeme.value);
                    }
                    break;
                case "string_literal":
                    results += prevId + " = " + lexeme.value;
                    break;
                case "keyword":
                    results += processKeyword(lexeme, i);
                    break;
            
                default:
                    System.out.println("Unhandled lexeme: " + lexeme.name + " Value: " + lexeme.value);
                    break;
            }
        }

        return results;
    }

    private boolean isAlreadyAnId(String value) {
        for (String id : ids) {
            if (id.equals(value)) {
                return true;
            }
        }
        return false;
    }

    private String processKeyword(lexeme lexeme, int i) {
        switch (lexeme.value) {
            case "print":
                lexeme nextLexeme = null;
                if (parser.pTree.lexemes.size() > i + 1) {
                    nextLexeme = parser.pTree.lexemes.get(i + 1);
                }
                if (nextLexeme != null) {
                    return "System.out.println(" + nextLexeme.value + ");\n";
                } else {
                    return "System.out.println();\n";
                }
        
            default:
                break;
        }
        return null;
    }

    private void run(File parentDirectory) {
        URLClassLoader classLoader;
        try {
            classLoader = URLClassLoader.newInstance(new URL[] { parentDirectory.toURI().toURL() });
            Class<?> helloClass = classLoader.loadClass("Test");
            Method[] methods = helloClass.getMethods();

            for (Method method : methods) {
                if (method.getName().equals("run")) {
                    System.out.println("Starting program.");
                    method.invoke(helloClass);
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
