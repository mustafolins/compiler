package ast;

import java.util.ArrayList;

import regex.lexeme;
import regex.lexer;

public class expectedOrder {
    private ArrayList<node> nodes;

    public expectedOrder(){
        nodes = new ArrayList<node>();

        // id expectations
        node idNode = new node(new lexeme("id"));
        nodes.add(idNode);
        idNode.addChild(idNode);

        // end of statement expextations
        node endOfStatementNode = new node(new lexeme("end_of_statement"));
        endOfStatementNode.addChild(idNode);
        nodes.add(endOfStatementNode);
        idNode.addChild(endOfStatementNode);
        endOfStatementNode.addChild(endOfStatementNode);

        // operator expectations
        node operatorNode = new node(new lexeme("operator"));
        operatorNode.addChild(idNode);
        nodes.add(operatorNode);
        idNode.addChild(operatorNode);

        // string literal expectations
        node stringNode = new node(new lexeme("string_literal"));
        stringNode.addChild(endOfStatementNode);
        nodes.add(stringNode);

        // integer literal expectations
        node intNode = new node(new lexeme("integer_literal"));
        intNode.addChild(endOfStatementNode);
        intNode.addChild(operatorNode);
        intNode.addChild(idNode);
        nodes.add(intNode);
        operatorNode.addChild(intNode);
        idNode.addChild(intNode);
        
        // decimal literal expectations
        node decNode = new node(new lexeme("decimal_literal"));
        decNode.addChild(endOfStatementNode);
        decNode.addChild(operatorNode);
        nodes.add(decNode);
        operatorNode.addChild(decNode);

        // assignment
        node assignmentNode = new node(new lexeme("assignment"));
        assignmentNode.addChild(stringNode);
        assignmentNode.addChild(decNode);
        assignmentNode.addChild(intNode);
        assignmentNode.addChild(idNode);
        nodes.add(assignmentNode);
        idNode.addChild(assignmentNode);

        // keyword expextations
        node keywordNode = new node(new lexeme("keyword"));
        keywordNode.addChild(idNode);
        keywordNode.addChild(endOfStatementNode);
        keywordNode.addChild(stringNode);
        keywordNode.addChild(intNode);
        keywordNode.addChild(decNode);
        nodes.add(keywordNode);
        endOfStatementNode.addChild(keywordNode);
        keywordNode.addChild(keywordNode);

        // conditionals
        node conditionalNode = new node(new lexeme("conditional"));
        conditionalNode.addChild(intNode);
        conditionalNode.addChild(decNode);
        conditionalNode.addChild(stringNode);
        nodes.add(conditionalNode);
        decNode.addChild(conditionalNode);
        intNode.addChild(conditionalNode);
        stringNode.addChild(conditionalNode);
        idNode.addChild(conditionalNode);

        // left code block
        node leftBlock = new node(new lexeme("start_of_block"));
        leftBlock.addChild(endOfStatementNode);
        nodes.add(leftBlock);
        endOfStatementNode.addChild(leftBlock);

        // right code block
        node rightBlock = new node(new lexeme("end_of_block"));
        rightBlock.addChild(endOfStatementNode);
        nodes.add(rightBlock);
        endOfStatementNode.addChild(rightBlock);

        // function assignment 
        node functionAssignment = new node(new lexeme("function_assignment"));
        functionAssignment.addChild(keywordNode);
        nodes.add(functionAssignment);
        idNode.addChild(functionAssignment);
    }

	public boolean conforms(lexer lex) {
        // initialize previous lexeme which should be nothing at the begginning
        lexeme curLexeme = new lexeme(null);
        // loop through parse trees lexemes
        for (int i = 0; i < lex.lexemes.size() - 1; i++) {
            curLexeme = lex.lexemes.get(i);

            // see if the previous, current and next lexemes conform to the expected order as defined in constructor
            if (!hasCorrectOrder(curLexeme, lex.lexemes.get(i + 1))) {
                return false;
            }
        }
		return true;
	}

    private boolean hasCorrectOrder(lexeme curLexeme, lexeme nextLexeme) {
        // has no next lexeme so anything should be excepted
        if (nextLexeme.name == null) {
            return true;
        }
        
        // loop through parent nodes
        for (node parent : nodes) {
            // if there is a match loop through children nodes
            if (parent.current.name.equals(curLexeme.name)) {
                // loop through all the children nodes of the current parent
                for (node child : parent.children) {
                    // if there's a match than it is in order
                    if (child.current.name.equals(nextLexeme.name)) {
                        return true;
                    }
                }
            }
        }

        // didn't find a single match so it's not in the correct order
        return false;
    }
}
