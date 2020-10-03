package ast;

import java.util.ArrayList;

import regex.lexeme;

public class node {
    public ArrayList<lexeme> parents;
    public lexeme current;
    public ArrayList<lexeme> children;

    public node(lexeme p, lexeme c, lexeme n) {
        parents = new ArrayList<lexeme>();
        parents.add(p);
        current = c;
        if (n != null) {
            children = new ArrayList<lexeme>();
            children.add(n);
        }
    }

    public void addParent(lexeme pNode){
        parents.add(pNode);
    }

    public void addChild(lexeme child){
        children.add(child);
    }
}
