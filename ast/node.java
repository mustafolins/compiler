package ast;

import java.util.ArrayList;

import regex.lexeme;

public class node {
    public lexeme current;
    public ArrayList<node> children;

    public node(lexeme c) {
        current = c;
        if (children == null) {
            children = new ArrayList<node>();
        }
    }

    public node(lexeme c, node n) {
        current = c;
        if (n != null) {
            children = new ArrayList<node>();
            children.add(n);
        }
    }

    public void addChild(node child){
        children.add(child);
    }
}
