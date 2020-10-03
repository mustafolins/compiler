package ast;

import regex.lexeme;
import regex.lexer;

public class parser {
    public parseTree pTree;
    public expectedOrder eo;

    public parser(lexer lexer){
        eo = new expectedOrder();
        pTree = new parseTree();
        for (lexeme lexeme : lexer.lexemes) {
            pTree.add(lexeme);
        }

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
        if (eo.conforms(pTree)) {
            return true;
        } else {
            return false;
        }
    }
}
