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
import java.util.Arrays;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;

import ast.parser;

public class interpreter {
    public parser parser;
    public String functionText;

    public interpreter(parser par) {
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

    /**
     * Try to compile the code given that it succesfully parses.
     */
    public void tryCompile(boolean printInterpretation) {
        if (parser.parse()) {
            System.out.println("Successfully parsed program:");
            compile(parser.programText, printInterpretation);
        } else {
            System.out.println("Failed to parse program!");
        }
    }

    /**
     * Write the interepreted code to a class and then use the JavaCompiler to
     * compile the class.
     */
    private void compile(String programText, boolean printInterpretation) {

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
