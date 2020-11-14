package ast;

import java.util.Dictionary;
import java.util.Hashtable;

import regex.lexType;
import regex.lexeme;

public class node {
    public lexeme current;
    public Dictionary<lexType, node> children;

    public node(lexeme c) {
        current = c;
        if (children == null) {
            children = new Hashtable<lexType, node>();
        }
    }

    public void addChild(node child){
        children.put(child.current.type, child);
    }
}
