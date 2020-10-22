package ast;

import regex.lexer;

public class parser {
    public expectedOrder eo;
    public lexer lexer;

    public parser(lexer lex){
        eo = new expectedOrder();
        lexer = lex;
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
            return true;
        } else {
            return false;
        }
    }
}
