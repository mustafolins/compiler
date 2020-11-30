package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.swing.*;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import java.util.List;

import ast.parser;
import regex.lexer;
import transpiler.transpiler;

public class ide {
    private static final int EXIT_ON_CLOSE = 3;
    private JFrame frame;
    public JTextPane codeTextArea;
    private JScrollPane codeScrollPane;
    public JTextPane outputTextArea;
    private JScrollPane outputScrollPane;
    private JButton runBtn;
    private JCheckBox showLexer;
    private JCheckBox showInterpretation;

    public ide() {
        frame = new JFrame("IDE");
        frame.setSize(1200, 800);
        frame.setMinimumSize(new Dimension(1200, 800));
        frame.setResizable(false);

        // show lexer output
        showLexer = new JCheckBox("Show Lexemes");
        showLexer.setSelected(true);
        showLexer.setBounds(610, 10, 140, 30);

        frame.add(showLexer);

        // show interpretation check box
        showInterpretation = new JCheckBox("Show Interpretation");
        showInterpretation.setSelected(true);
        showInterpretation.setBounds(760, 10, 140, 30);

        frame.add(showInterpretation);

        // code area
        codeTextArea = new JTextPane();
        codeTextArea.setBounds(10, 50, 580, 700);
        codeTextArea.setBackground(Color.darkGray);
        codeTextArea.setForeground(Color.cyan);
        codeTextArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        codeTextArea.setCaretColor(Color.white);
        codeTextArea.setSelectionColor(Color.blue);
        codeScrollPane = new JScrollPane(codeTextArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        codeScrollPane.setBounds(10, 50, 580, 700);

        frame.add(codeScrollPane);

        // output
        outputTextArea = new JTextPane();
        outputTextArea.setBounds(610, 50, 565, 700);
        outputTextArea.setBackground(Color.black);
        outputTextArea.setFont(new Font("Calibri", Font.PLAIN, 14));
        outputScrollPane = new JScrollPane(outputTextArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        outputScrollPane.setBounds(610, 50, 565, 700);

        frame.add(outputScrollPane);

        // run button
        runBtn = new JButton("Run");
        runBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                runClicked(e);
            }
        });
        runBtn.setBounds(10, 10, 100, 30);

        frame.add(runBtn);

        frame.setLayout(null);
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        try {
            //UIManager.LookAndFeelInfo[] lafInfo = UIManager.getInstalledLookAndFeels();
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
            // javax.swing.plaf.metal.MetalLookAndFeel
            // javax.swing.plaf.nimbus.NimbusLookAndFeel
            // javax.swing.plaf.synth.SynthLookAndFeel it's just ugly and white
            // com.sun.java.swing.plaf.motif.MotifLookAndFeel
            // com.sun.java.swing.plaf.windows.WindowsLookAndFeel
            // com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        ide ide = new ide();

        String str = "";
        if (args.length > 0) {
            str = ReadFile(args[0]);
        } else {
            str = ReadFile("code.txt");
        }

        ide.codeTextArea.setText(str);

        sysOutStream out = new sysOutStream(ide.outputTextArea);
        System.setOut(new PrintStream(out));
        System.setErr(new PrintStream(out));

        ide.Show();
    }

    private void Show() {
        frame.setVisible(true);
    }

    public void runClicked(ActionEvent e) {
        // clear output
        outputTextArea.setText(null);

        // set lexer style
        sysOutStream.attributeSet = new SimpleAttributeSet();
        StyleConstants.setForeground(sysOutStream.attributeSet, Color.yellow);

        lexer lex = new lexer();
        lex.analyze(codeTextArea.getText());

        if (showLexer.isSelected()) {
            System.out.println(lex);
        }

        parser par = new parser(lex);

        // set interpreter style
        sysOutStream.attributeSet = new SimpleAttributeSet();
        StyleConstants.setForeground(sysOutStream.attributeSet, Color.pink);

        try {
            transpiler interpreter = new transpiler(par);
            interpreter.tryCompile(showInterpretation.isSelected());
        } catch (Error error) {
            String temp = error.getMessage();
            System.err.println(temp);
        }
    }

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
}
