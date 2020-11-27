package ast;

import regex.lexeme;

public class parseTree {
    
    public lexeme current;
    public parseTree child;

    public parseTree(lexeme c) {
        current = c;
    }

    public void addChild(parseTree child){
        this.child = child;
    }
}
