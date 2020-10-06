package ast;

import java.util.ArrayList;

import regex.lexeme;

public class expectedOrder {
    private ArrayList<node> nodes;

    public expectedOrder(){
        nodes = new ArrayList<node>();

        // id expectations
        node idNode = new node(new lexeme(null), new lexeme("id"), new lexeme("assignment"));
        idNode.addChild(new lexeme("end_of_statement"));
        idNode.addChild(new lexeme("operator"));
        nodes.add(idNode);

        // assignment
        node assignmentNode = new node(new lexeme(null), new lexeme("assignment"), new lexeme("string_literal"));
        assignmentNode.addChild(new lexeme("decimal_literal"));
        assignmentNode.addChild(new lexeme("integer_literal"));
        assignmentNode.addChild(new lexeme("id"));
        nodes.add(assignmentNode);

        // string literal expectations
        node stringNode = new node(new lexeme(null), new lexeme("string_literal"), new lexeme("end_of_statement"));
        nodes.add(stringNode);

        // integer literal expectations
        node intNode = new node(new lexeme(null), new lexeme("integer_literal"), new lexeme("end_of_statement"));
        intNode.addChild(new lexeme("operator"));
        nodes.add(intNode);

        // end of statement expextations
        node endOfStatementNode = new node(new lexeme(null), new lexeme("end_of_statement"), null);
        nodes.add(endOfStatementNode);

        // keyword expextations
        node keywordNode = new node(new lexeme(null), new lexeme("keyword"), new lexeme("id"));
        nodes.add(keywordNode);

        // operator expectations
        node operatorNode = new node(new lexeme("id"), new lexeme("operator"), new lexeme("id"));
        operatorNode.addParent(new lexeme("decimal_literal"));
        operatorNode.addParent(new lexeme("integer_literal"));
        operatorNode.addParent(new lexeme("string_literal"));
        operatorNode.addChild(new lexeme("decimal_literal"));
        operatorNode.addChild(new lexeme("integer_literal"));
        operatorNode.addChild(new lexeme("string_literal"));
        nodes.add(operatorNode);
    }

	public boolean conforms(parseTree pTree) {
        // initialize previous lexeme which should be nothing at the begginning
        lexeme prevLexeme = new lexeme(null);
        // loop through parse trees lexemes
        for (int i = 0; i < pTree.lexemes.size() - 1; i++) {
            lexeme curLexeme = pTree.lexemes.get(i);

            // see if the previous, current and next lexemes conform to the expected order as defined in constructor
            if (!hasCorrectOrder(prevLexeme, curLexeme, pTree.lexemes.get(i + 1))) {
                return false;
            }

            prevLexeme = curLexeme;
        }
		return true;
	}

    private boolean hasCorrectOrder(lexeme prevLexeme, lexeme curLexeme, lexeme nextLexeme) {
        // has no next lexeme so anything should be excepted
        if (nextLexeme.name == null) {
            return true;
        }
        // node parent = nodes.get(0);
        // go through expected nods and check if passed lexemes match expectations
        for (int i = 0; i < nodes.size(); i++) {
            // get current node
            node currentNode = nodes.get(i);
            // if current nodes current lexeme matches the given current then check that the next lexeme is a match
            if (currentNode.current.name.equals(curLexeme.name)) {
                // has no children expectation so except anything
                if (currentNode.children == null) {
                    return true;
                }
                // loop through children lexemes inside of current node
                for (lexeme nextNodes : currentNode.children) {
                    try {
                        // if the given next lexeme is in the rule return true
                        if (nextNodes.name.equals(nextLexeme.name)) {
                            return true;
                        }
                    } catch (Exception e) {
                        System.out.println("");
                    }
                }
            }
        }

        // didn't find a single match so it's not in the correct order
        return false;
    }
}
