package ast;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;

import regex.lexType;
import regex.lexeme;

public class expectedOrder {
    /**
     * The syntax tree nodes.
     */
    private Dictionary<lexType, node> nodes;

    public expectedOrder(){
        nodes = new Hashtable<lexType, node>();

        // id expectations
        node idNode = new node(new lexeme(lexType.id));
        nodes.put(lexType.id, idNode);
        idNode.addChild(idNode);

        // end of statement expextations
        node endOfStatementNode = new node(new lexeme(lexType.end_of_statement));
        endOfStatementNode.addChild(idNode);
        nodes.put(lexType.end_of_statement, endOfStatementNode);
        idNode.addChild(endOfStatementNode);
        endOfStatementNode.addChild(endOfStatementNode);

        // operator expectations
        node operatorNode = new node(new lexeme(lexType.operator));
        operatorNode.addChild(idNode);
        nodes.put(lexType.operator, operatorNode);
        idNode.addChild(operatorNode);

        // string literal expectations
        node stringNode = new node(new lexeme(lexType.string_literal));
        stringNode.addChild(endOfStatementNode);
        nodes.put(lexType.string_literal, stringNode);

        // integer literal expectations
        node intNode = new node(new lexeme(lexType.integer_literal));
        intNode.addChild(endOfStatementNode);
        intNode.addChild(operatorNode);
        intNode.addChild(idNode);
        nodes.put(lexType.integer_literal, intNode);
        operatorNode.addChild(intNode);
        idNode.addChild(intNode);
        
        // decimal literal expectations
        node decNode = new node(new lexeme(lexType.decimal_literal));
        decNode.addChild(endOfStatementNode);
        decNode.addChild(operatorNode);
        nodes.put(lexType.decimal_literal, decNode);
        operatorNode.addChild(decNode);

        // assignment
        node assignmentNode = new node(new lexeme(lexType.assignment));
        assignmentNode.addChild(stringNode);
        assignmentNode.addChild(decNode);
        assignmentNode.addChild(intNode);
        assignmentNode.addChild(idNode);
        nodes.put(lexType.assignment, assignmentNode);
        idNode.addChild(assignmentNode);

        // keyword expextations
        node keywordNode = new node(new lexeme(lexType.keyword));
        keywordNode.addChild(idNode);
        keywordNode.addChild(endOfStatementNode);
        keywordNode.addChild(stringNode);
        keywordNode.addChild(intNode);
        keywordNode.addChild(decNode);
        nodes.put(lexType.keyword, keywordNode);
        endOfStatementNode.addChild(keywordNode);
        keywordNode.addChild(keywordNode);

        // conditionals
        node conditionalNode = new node(new lexeme(lexType.conditional));
        conditionalNode.addChild(intNode);
        conditionalNode.addChild(decNode);
        conditionalNode.addChild(stringNode);
        nodes.put(lexType.conditional, conditionalNode);
        decNode.addChild(conditionalNode);
        intNode.addChild(conditionalNode);
        stringNode.addChild(conditionalNode);
        idNode.addChild(conditionalNode);

        // left code block
        node leftBlock = new node(new lexeme(lexType.start_of_block));
        leftBlock.addChild(endOfStatementNode);
        nodes.put(lexType.start_of_block, leftBlock);
        endOfStatementNode.addChild(leftBlock);

        // right code block
        node rightBlock = new node(new lexeme(lexType.end_of_block));
        rightBlock.addChild(endOfStatementNode);
        nodes.put(lexType.end_of_block, rightBlock);
        endOfStatementNode.addChild(rightBlock);

        // function assignment 
        node functionAssignment = new node(new lexeme(lexType.function_assignment));
        functionAssignment.addChild(keywordNode);
        nodes.put(lexType.function_assignment, functionAssignment);
        idNode.addChild(functionAssignment);
    }

    /**
     * Loops through the lexemes in the lexer and checks that they are all in the correct order.
     * @param parseTrees The lexer to check that the lexemes are in order.
     * @return True if all lexemes are in the correct order False otherwise.
     */
	public boolean conforms(ArrayList<parseTree> parseTrees) {
        // initialize previous lexeme which should be nothing at the begginning
        lexeme curLexeme = new lexeme(null);
        // loop through parse trees 
        for (parseTree parseTree : parseTrees) {
            parseTree cur = parseTree;
            while (cur != null) {
                curLexeme = cur.current;
                // see if the current and next lexeme conform to the expected order as defined in constructor
                if (!hasCorrectOrder(curLexeme, cur.child == null ? null : cur.child.current)) {
                    return false;
                }
                                
                cur = cur.child;
            }
        }
		return true;
	}

    /**
     * Checks that the next lexeme can come after the next lexeme.
     * @param curLexeme The current lexeme.
     * @param nextLexeme The next lexeme.
     * @return True if nextLexeme can come after curLexeme.
     */
    private boolean hasCorrectOrder(lexeme curLexeme, lexeme nextLexeme) {
        // has no next lexeme so anything should be excepted, in other words it's probably reached the end of the parse tree
        if (nextLexeme == null) {
            return true;
        }

        // get node
        node parent = nodes.get(curLexeme.type);
        if (parent != null) {
            // get child if it exists in current node
            node child = parent.children.get(nextLexeme.type);
            // return true if the child is not null
            if (child != null) {
                return true;
            }
        }

        // didn't find a single match so it's not in the correct order
        return false;
    }
}
