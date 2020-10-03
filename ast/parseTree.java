package ast;

import java.util.ArrayList;

import regex.lexeme;

public class parseTree {
    public ArrayList<lexeme> lexemes;

    public parseTree(){
        lexemes = new ArrayList<>();
    }

	public void add(lexeme lexeme) {
        if (lexeme.name.equals("seperator")) {
            return;
        }
        lexemes.add(lexeme);
	}
    
}
