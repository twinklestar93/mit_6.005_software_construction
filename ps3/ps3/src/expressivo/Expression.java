/* Copyright (c) 2015-2016 MIT 6.005 course staff, all rights reserved.
 * Redistribution of original or derived work requires permission of course staff.
 */
package expressivo;

import expressivo.parser.ExpressionLexer;
import expressivo.parser.ExpressionParser;
import org.antlr.v4.gui.Trees;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.math.BigDecimal;
import java.util.Map;

/**
 * An immutable data type representing a polynomial expression of:
 *   + and *
 *   nonnegative integers and floating-point numbers
 *   variables (case-sensitive nonempty strings of letters)
 *   parentheses (for grouping)
 * 
 * <p>PS3 instructions: this is a required ADT interface.
 * You MUST NOT change its name or package or the names or type signatures of existing methods.
 * You may, however, add additional methods, or strengthen the specs of existing methods.
 * Declare concrete variants of Expression in their own Java source files.
 */
public interface Expression {
    
    // Datatype definition (rep, recursive datatype definition)
    //   recursive datatype definition:
    //   Expression = Variable(name: String) +
    //                   Number(value: String) +
    //                   Plus(left: Expression, right: Expression) +
    //                   Multiply(left: Expression, right: Expression)

    /** @return the computed value of this expression */
    public BigDecimal value();

    /**
     * Parse an expression.
     * @param input expression to parse, as defined in the PS3 handout.
     * @return expression AST for the input
     * @throws IllegalArgumentException if the expression is invalid
     */
    public static Expression parse(String input) throws IllegalArgumentException{

        try {

            // Create a stream of characters from the string
            CharStream stream = new ANTLRInputStream(input);

            // Make a parser
            // Make a lexer.  This converts the stream of characters into a
            // stream of tokens.  A token is a character group, like "<i>"
            // or "</i>".  Note that this doesn't start reading the character stream yet,
            // it just sets up the lexer to read it.
            ExpressionLexer lexer = new ExpressionLexer(stream);
            lexer.reportErrorsAsExceptions();
            TokenStream tokens = new CommonTokenStream(lexer);

            // Make a parser whose input comes from the token stream produced by the lexer.
            ExpressionParser parser = new ExpressionParser(tokens);

            // Generate the parse tree using the starter rule.
            // root is the starter rule for this grammar.
            // Other grammars may have different names for the starter rule.
            ParseTree tree = parser.root();

            // *** Debugging option #1: print the tree to the console
//            System.err.println(tree.toStringTree(parser));

            // *** Debugging option #2: show the tree in a window
//            Trees.inspect(tree, parser);

            MakeExpression exprMaker = new MakeExpression();
            new ParseTreeWalker().walk(exprMaker, tree);
            return exprMaker.getExpression();
        } catch (Exception e) {
            throw new IllegalArgumentException("illegal expression");
        }
    }
    
    /**
     * @return a parsable representation of this expression, such that
     * for all e:Expression, e.equals(Expression.parse(e.toString())).
     * The implementation must be recursive, and must not use instanceof.
     */
    @Override 
    public String toString();

    /**
     * @param thatObject any object
     * @return true if and only if this and thatObject are structurally-equal
     * Expressions, as defined in the PS3 handout.
     */
    @Override
    public boolean equals(Object thatObject);
    
    /**
     * @return hash code value consistent with the equals() definition of structural
     * equality, such that for all e1,e2:Expression,
     *     e1.equals(e2) implies e1.hashCode() == e2.hashCode()
     *     the expressions contain the same variables, numbers, and operators;
     *     those variables, numbers, and operators are in the same order, read left-to-right;
     *     and they are grouped in the same way.
     */
    @Override
    public int hashCode();

    /**
     * The simplification operation takes an expression and an environment (a mapping of variables to values). It substitutes the
     * values for those variables into the expression, and attempts to simplify the substituted polynomial as much as it can.
     * @param env
     * @return the simplified expression
     */
    public Expression simplify(Map<String, BigDecimal> env);

    /**
     * The symbolic differentiation operation takes an expression and a variable, and produces an expression with the derivative of the
     * input with respect to the variable.
     * @param val
     * @return Expression
     */
    public Expression differentiate(String val);

    // whether the Expression is of a Number
    public boolean isNumber();

}

class Variable implements Expression {
    private final String name;

    // Abstraction function
    //    represents the variable e.g. x, y
    // Rep invariant
    //    name is case-sensitive non-empty string of letters
    // Safety from rep exposure
    //    all fields are private, immutable and final

    /** Make a Variable. */
    public Variable(String name) {
        this.name = name;
    }

    @Override
    public BigDecimal value() {
        throw new AssertionError("Not a number");
    }

    @Override public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name);
        return sb.toString();
    }

    public void checkRep() {
        assert !this.name.isEmpty();
        assert this.name.matches("[a-zA-Z]+");
    }

    @Override
    public boolean isNumber() {
        return false;
    }

    @Override
    public Expression simplify(Map<String, BigDecimal> env) {
        if (env.containsKey(name)) {
            return new Number(env.get(name));
        }
        return new Variable(this.name);
    }

    @Override
    public Expression differentiate(String val) {
        if (val.equals(this.name)) {
            return new Number(new BigDecimal("1"));
        } else {
            return new Number(new BigDecimal("0"));
        }
    }

    @Override
    public boolean equals(Object thatObject) {
        if (!(thatObject instanceof Variable)) {
            return false;
        }
        Variable that = (Variable) thatObject;
        checkRep();
        return this.name.equals(that.name);
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }
}

class Number implements Expression {

    private final BigDecimal val;

    public Number(BigDecimal value) {
        this.val = value;
    }

    @Override
    public BigDecimal value() {
        return this.val;
    }

    @Override
    public Expression simplify(Map<String, BigDecimal> env) {
        return new Number(this.val);
    }

    @Override
    public Expression differentiate(String val) {
        return new Number(new BigDecimal("0"));
    }

    @Override
    public boolean isNumber() {
        return true;
    }

    @Override
    public String toString() {
        return String.valueOf(val.toString());
    }

    @Override
    public boolean equals(Object thatObject) {
        if (!(thatObject instanceof Number)) {
            return false;
        }
        Number that = (Number) thatObject;
        return this.val.equals(that.val);
    }

    @Override
    public int hashCode() {
        return this.val.hashCode();
    }
}

class Plus implements Expression {
    private final Expression left, right;

    // Abstraction function
    //    represents the sum of two expressions left+right
    // Rep invariant
    //    true
    // Safety from rep exposure
    //    all fields are immutable and final

    /** Make a Plus which is the sum of left and right. */
    public Plus(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    public void checkRep() {
        assert left != null;
        assert right != null;
    }

    @Override public BigDecimal value() {
        throw new AssertionError("Not a number");
    }

    @Override public boolean isNumber() {
        return false;
    }

    @Override public Expression simplify(Map<String, BigDecimal> env) {
        Expression simplifiedLeft = left.simplify(env);
        Expression simplifiedRight = right.simplify(env);

        if (simplifiedLeft.isNumber() && simplifiedRight.isNumber()) {
            return new Number(simplifiedLeft.value().add(simplifiedRight.value()));
        } else {
            return new Plus(simplifiedLeft, simplifiedRight);
        }
    }

    @Override public Expression differentiate(String val) {
        return new Plus(left.differentiate(val), right.differentiate(val));
    }

    @Override public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("(").append(left.toString()).append(" + ")
                .append(right.toString()).append(")");
        return sb.toString();
    }

    @Override public int hashCode() {
        return (left.hashCode() * 2) + right.hashCode();
    }

    @Override public boolean equals(Object thatObject) {
        if (!(thatObject instanceof Plus)) {
            return false;
        }
        Plus that = (Plus) thatObject;
        checkRep();
        return this.left.equals(that.left) &&
                this.right.equals(that.right);
    }
}

class Times implements Expression {
    private final Expression left, right;

    // Abstraction function
    //    represents the product of two expressions left*right
    // Rep invariant
    //    True
    // Safety from rep exposure
    //    all fields are immutable and final

    public void checkRep() {
        assert this.left != null;
        assert this.right != null;
    }

    /** Make a Multiply which is the product of two expressions left and right. */
    public Times(Expression left, Expression right) {
        this.left = left;
        this.right = right;
    }

    @Override public BigDecimal value() {
        throw new AssertionError("Not a number");
    }

    @Override
    public boolean isNumber() {
        return false;
    }

    @Override public Expression simplify(Map<String, BigDecimal> env) {
        Expression simplifiedLeft = left.simplify(env);
        Expression simplifiedRight = right.simplify(env);

        if (simplifiedLeft.isNumber() && simplifiedRight.isNumber()) {
            return new Number(simplifiedLeft.value().multiply(simplifiedRight.value()));
        } else {
            return new Times(simplifiedLeft, simplifiedRight);
        }
    }

    @Override public Expression differentiate(String val) {
        return new Plus(new Times(left, right.differentiate(val)),
                new Times(right, left.differentiate(val)));
    }

    @Override public int hashCode() {
        return (left.hashCode() * 2) + right.hashCode();
    }

    @Override public boolean equals(Object thatObject) {
        if (!(thatObject instanceof Times)) {
            return false;
        }
        Times that = (Times) thatObject;
        checkRep();
        return this.left.equals(that.left) &&
                this.right.equals(that.right);
    }

    @Override public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("(").append(left.toString()).append(" * ")
                .append(right.toString()).append(")");
        return sb.toString();
    }
}
