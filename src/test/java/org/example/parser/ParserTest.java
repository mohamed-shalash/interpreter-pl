package org.example.parser;


import org.example.ast.*;
import org.example.lexer.Lexer;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import  org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.*;

public class ParserTest {
    @Test
    public void testLetStatements() {
        String input = "let x = 5;\nlet y = 10;\nlet foobar = 838383;";
        Lexer l = new Lexer(input);
        Parser p = new Parser(l);
        Program program = p.parseProgram();

        //checkParserErrors(p);

        assertNotEquals(null, program, "ParseProgram() returned null");
        List<Statement> statements = program.getStatements();
        assertEquals(3, statements.size(), "program.Statements does not contain 3 statements. got=" + statements.size());
        String[] expectedIdentifiers = {"x", "y", "foobar"};
        for (int i = 0; i < 3; i++) {
            Statement stmt = statements.get(i);
            assertEquals("let", stmt.tokenLiteral(), "s.TokenLiteral not 'let'. got=" + stmt.tokenLiteral());
            assertTrue(stmt instanceof LetStatement, "s not LetStatement. got=" + stmt.getClass().getName());
            LetStatement letStmt = (LetStatement) stmt;
            assertEquals(expectedIdentifiers[i], letStmt.getName().getValue(),
                    "letStmt.Name.Value not '" + expectedIdentifiers[i] + "'. got=" + letStmt.getName().getValue());
            assertEquals(expectedIdentifiers[i], letStmt.getName().tokenLiteral(),
                    "letStmt.Name.TokenLiteral not '" + expectedIdentifiers[i] + "'. got=" + letStmt.getName().tokenLiteral());
        }
    }

    private void checkParserErrors(Parser p) {
        List<String> errors = p.getErrors();
        if (errors.isEmpty()) {
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("parser has %d errors\n", errors.size()));
        for (String msg : errors) {
            sb.append(String.format("parser error: \"%s\"\n", msg));
        }
        Assertions.fail(sb.toString());
    }

    @Test
    public void testReturnStatements() {
        String input = """
            return 5;
            return 10;
            return 993322;
            """;

        Lexer l = new Lexer(input);
        Parser p = new Parser(l);
        Program program = p.parseProgram();



        List<Statement> statements = program.getStatements();
        Assertions.assertEquals(3, statements.size(),
                "program.Statements does not contain 3 statements. got=" + statements.size());

        for (Statement stmt : statements) {
            if (!(stmt instanceof ReturnStatement)) {
                Assertions.fail("stmt not *ast.ReturnStatement. got=" + stmt.getClass().getSimpleName());
                continue;
            }

            ReturnStatement returnStmt = (ReturnStatement) stmt;
            Assertions.assertEquals("return", returnStmt.tokenLiteral(),
                    "returnStmt.TokenLiteral not 'return', got " + returnStmt.tokenLiteral());
        }
    }

    @Test
    public void testIdentifierExpression() {
        String input = "foobar;";
        Lexer l = new Lexer(input);
        Parser p = new Parser(l);
        Program program = p.parseProgram();

        checkParserErrors(p);

        List<Statement> statements = program.getStatements();
        assertEquals(1, statements.size(),
                "program has not enough statements. got=" + statements.size());

        Statement stmt = statements.get(0);
        assertTrue(stmt instanceof ExpressionStatement,
                "program.Statements[0] is not ExpressionStatement. got=" + stmt.getClass().getSimpleName());

        ExpressionStatement exprStmt = (ExpressionStatement) stmt;
        Expression expression = exprStmt.getExpression();
        assertTrue(expression instanceof Identifier,
                "exp not Identifier. got=" + expression.getClass().getSimpleName());

        Identifier ident = (Identifier) expression;
        assertEquals("foobar", ident.getValue(),
                "ident.Value not 'foobar'. got=" + ident.getValue());
        assertEquals("foobar", ident.tokenLiteral(),
                "ident.TokenLiteral not 'foobar'. got=" + ident.tokenLiteral());
    }

    @Test
    public void testIntegerLiteralExpression() {
        String input = "5;";
        Lexer l = new Lexer(input);
        Parser p = new Parser(l);
        Program program = p.parseProgram();

        checkParserErrors(p);

        List<Statement> statements = program.getStatements();
        assertEquals(1, statements.size(),
                "program has not enough statements. got=" + statements.size());

        Statement stmt = statements.get(0);
        assertTrue(stmt instanceof ExpressionStatement,
                "program.Statements[0] is not ExpressionStatement. got=" + stmt.getClass().getSimpleName());

        ExpressionStatement exprStmt = (ExpressionStatement) stmt;
        Expression expression = exprStmt.getExpression();
        assertTrue(expression instanceof IntegerLiteral,
                "exp not IntegerLiteral. got=" + expression.getClass().getSimpleName());

        IntegerLiteral literal = (IntegerLiteral) expression;
        assertEquals(5, literal.getValue(),
                "literal.Value not 5. got=" + literal.getValue());
        assertEquals("5", literal.tokenLiteral(),
                "literal.TokenLiteral not '5'. got=" + literal.tokenLiteral());
    }


    @ParameterizedTest
    @MethodSource("prefixExpressionProvider")
    void testParsingPrefixExpressions(String input, String operator, Object expectedValue) {
        Lexer l = new Lexer(input);
        Parser p = new Parser(l);
        Program program = p.parseProgram();

        checkParserErrors(p);

        List<Statement> statements = program.getStatements();
        assertEquals(1, statements.size(),
                "program.Statements does not contain 1 statement. got=" + statements.size());

        Statement stmt = statements.get(0);
        assertInstanceOf(ExpressionStatement.class, stmt,
                "program.Statements[0] is not ExpressionStatement. got=" + stmt.getClass().getSimpleName());

        Expression expression = ((ExpressionStatement) stmt).getExpression();
        assertInstanceOf(PrefixExpression.class, expression,
                "expression is not PrefixExpression. got=" + expression.getClass().getSimpleName());

        PrefixExpression prefixExp = (PrefixExpression) expression;
        assertEquals(operator, prefixExp.getOperator(),
                "exp.Operator is not '%s'. got=%s".formatted(operator, prefixExp.getOperator()));

        assertTrue(testLiteralExpression(prefixExp.getRight(), expectedValue));
    }

    private static Stream<Arguments> prefixExpressionProvider() {
        return Stream.of(
                Arguments.of("!5;", "!", 5L),
                Arguments.of("-15;", "-", 15L),
                Arguments.of("!true;", "!", true),   // Added semicolon for consistency
                Arguments.of("!false;", "!", false)  // Added semicolon for consistency
        );
    }
    private boolean testIntegerLiteral(Expression expr, long expectedValue) {
        // 1. Type check
        if (!(expr instanceof IntegerLiteral)) {
            System.err.printf("expr not IntegerLiteral. got=%s%n",
                    expr.getClass().getSimpleName());
            return false;
        }

        IntegerLiteral integer = (IntegerLiteral) expr;

        // 2. Value check
        if (integer.getValue() != expectedValue) {
            System.err.printf("integer.Value not %d. got=%d%n",
                    expectedValue, integer.getValue());
            return false;
        }

        // 3. Token literal check
        String expectedLiteral = Long.toString(expectedValue);
        if (!integer.tokenLiteral().equals(expectedLiteral)) {
            System.err.printf("integer.TokenLiteral not %s. got=%s%n",
                    expectedLiteral, integer.tokenLiteral());
            return false;
        }

        return true;
    }


    @ParameterizedTest
    @MethodSource("infixExpressionProvider")
    void testParsingInfixExpressions(String input, Object leftValue, String operator, Object rightValue) {
        Lexer l = new Lexer(input);
        Parser p = new Parser(l);
        Program program = p.parseProgram();

        checkParserErrors(p);

        List<Statement> statements = program.getStatements();
        assertEquals(1, statements.size(),
                "program.Statements does not contain 1 statement. got=" + statements.size());

        Statement stmt = statements.get(0);
        assertInstanceOf(ExpressionStatement.class, stmt,
                "program.Statements[0] is not ExpressionStatement. got=" + stmt.getClass().getSimpleName());

        Expression expression = ((ExpressionStatement) stmt).getExpression();
        assertInstanceOf(InfixExpression.class, expression,
                "exp is not InfixExpression. got=" + expression.getClass().getSimpleName());

        InfixExpression infixExp = (InfixExpression) expression;

        assertTrue(testLiteralExpression(infixExp.getLeft(), leftValue),
                "Left expression value mismatch");
        assertEquals(operator, infixExp.getOperator(),
                "exp.Operator is not '%s'. got=%s".formatted(operator, infixExp.getOperator()));
        assertTrue(testLiteralExpression(infixExp.getRight(), rightValue),
                "Right expression value mismatch");
    }

    private static Stream<Arguments> infixExpressionProvider() {
        return Stream.of(
                // Numeric tests
                Arguments.of("5 + 5;", 5L, "+", 5L),
                Arguments.of("5 - 5;", 5L, "-", 5L),
                Arguments.of("5 * 5;", 5L, "*", 5L),
                Arguments.of("5 / 5;", 5L, "/", 5L),
                Arguments.of("5 > 5;", 5L, ">", 5L),
                Arguments.of("5 < 5;", 5L, "<", 5L),
                Arguments.of("5 == 5;", 5L, "==", 5L),
                Arguments.of("5 != 5;", 5L, "!=", 5L),

                // Boolean tests
                Arguments.of("true == true;", true, "==", true),
                Arguments.of("true != false;", true, "!=", false),
                Arguments.of("false == false;", false, "==", false)


        );
    }
    @ParameterizedTest
    @MethodSource("operatorPrecedenceProvider")
    void testOperatorPrecedenceParsing(String input, String expected) {
        Lexer l = new Lexer(input);
        Parser p = new Parser(l);
        Program program = p.parseProgram();

        checkParserErrors(p);

        String actual = program.toString();
        assertEquals(expected, actual,
                "Expected and actual AST strings do not match");
    }

    private static Stream<Arguments> operatorPrecedenceProvider() {
        return Stream.of(
                Arguments.of("-a * b;", "((-a) * b)"),
                Arguments.of("!-a;", "(!(-a))"),
                Arguments.of("a + b + c;", "((a + b) + c)"),
                Arguments.of("a + b - c;", "((a + b) - c)"),
                Arguments.of("a * b * c;", "((a * b) * c)"),
                Arguments.of("a * b / c;", "((a * b) / c)"),
                Arguments.of("a + b / c;", "(a + (b / c))"),
                Arguments.of("a + b * c + d / e - f;", "(((a + (b * c)) + (d / e)) - f)"),
                Arguments.of("3 + 4; -5 * 5;", "(3 + 4)((-5) * 5)"),
                Arguments.of("5 > 4 == 3 < 4;", "((5 > 4) == (3 < 4))"),
                Arguments.of("5 < 4 != 3 > 4;", "((5 < 4) != (3 > 4))"),
                Arguments.of("3 + 4 * 5 == 3 * 1 + 4 * 5;",
                        "((3 + (4 * 5)) == ((3 * 1) + (4 * 5)))"),
                Arguments.of("true", "true"),
                Arguments.of("false", "false"),
                Arguments.of("3 > 5 == false", "((3 > 5) == false)"),
                Arguments.of("3 < 5 == true", "((3 < 5) == true)"),
                // New test cases for parentheses and operator precedence
                Arguments.of("1 + (2 + 3) + 4", "((1 + (2 + 3)) + 4)"),
                Arguments.of("(5 + 5) * 2", "((5 + 5) * 2)"),
                Arguments.of("2 / (5 + 5)", "(2 / (5 + 5))"),
                Arguments.of("-(5 + 5)", "(-(5 + 5))"),
                Arguments.of("!(true == true)", "(!(true == true))")
        );
    }

    @Test
    void testInfixExpression() {
        String input = "5 + 10;";
        Parser parser = new Parser(new Lexer(input));
        var program = parser.parseProgram();

        // Check errors
        assertTrue(parser.getErrors().isEmpty(),
                "Parser had errors: " + String.join(", ", parser.getErrors()));

        // Get first statement
        var stmt = program.getStatements().get(0);
        assertInstanceOf(ExpressionStatement.class, stmt);

        // Test the expression
        var expr = ((ExpressionStatement) stmt).getExpression();
        assertTrue(testInfixExpression(expr, 5L, "+", 10L));
    }

    protected boolean testLiteralExpression(Expression exp, Object expected) {
        if (expected instanceof Integer) {
            return testIntegerLiteral(exp, ((Integer) expected).longValue());
        } else if (expected instanceof Long) {
            return testIntegerLiteral(exp, (Long) expected);
        } else if (expected instanceof String) {
            return testIdentifier(exp, (String) expected);
        }else if (expected instanceof Boolean) {
            return testBooleanLiteral(exp, (Boolean) expected);
        }
        System.err.printf("Type of exp not handled. got=%s%n", expected.getClass().getSimpleName());
        return false;
    }

    protected boolean testInfixExpression(Expression exp, Object left,
                                          String operator, Object right) {
        if (!(exp instanceof InfixExpression)) {
            System.err.printf("exp is not InfixExpression. got=%s(%s)%n",
                    exp.getClass().getSimpleName(), exp);
            return false;
        }

        InfixExpression opExp = (InfixExpression) exp;

        if (!testLiteralExpression(opExp.getLeft(), left)) {
            return false;
        }

        if (!opExp.getOperator().equals(operator)) {
            System.err.printf("exp.Operator is not '%s'. got='%s'%n",
                    operator, opExp.getOperator());
            return false;
        }

        if (!testLiteralExpression(opExp.getRight(), right)) {
            return false;
        }

        return true;
    }

    protected boolean testIdentifier(Expression exp, String value) {
        if (!(exp instanceof Identifier)) {
            System.err.printf("exp not Identifier. got=%s%n",
                    exp.getClass().getSimpleName());
            return false;
        }

        Identifier ident = (Identifier) exp;

        if (!ident.getValue().equals(value)) {
            System.err.printf("ident.Value not %s. got=%s%n",
                    value, ident.getValue());
            return false;
        }

        if (!ident.tokenLiteral().equals(value)) {
            System.err.printf("ident.TokenLiteral not %s. got=%s%n",
                    value, ident.tokenLiteral());
            return false;
        }

        return true;
    }

    @Test
    void testBooleanExpression() {
        testBoolean("true;", true);
        testBoolean("false;", false);
    }

    private void testBoolean(String input, boolean expected) {
        Lexer l = new Lexer(input);
        Parser p = new Parser(l);
        Program program = p.parseProgram();

        checkParserErrors(p);

        List<Statement> statements = program.getStatements();
        assertEquals(1, statements.size());

        Statement stmt = statements.get(0);
        assertInstanceOf(ExpressionStatement.class, stmt);

        Expression expr = ((ExpressionStatement) stmt).getExpression();
        assertInstanceOf(BooleanLiteral.class, expr);

        BooleanLiteral bool = (BooleanLiteral) expr;
        assertEquals(expected, bool.getValue());
        assertEquals(input.trim().replace(";", ""), bool.toString());
    }

    protected boolean testBooleanLiteral(Expression expr, boolean expected) {
        if (!(expr instanceof BooleanLiteral)) {
            System.err.printf("exp not Boolean. got=%s%n",
                    expr.getClass().getSimpleName());
            return false;
        }

        BooleanLiteral bool = (BooleanLiteral) expr;
        if (bool.getValue() != expected) {
            System.err.printf("bool.Value not %b. got=%b%n",
                    expected, bool.getValue());
            return false;
        }

        String expectedLiteral = expected ? "true" : "false";
        if (!bool.tokenLiteral().equals(expectedLiteral)) {
            System.err.printf("bool.TokenLiteral not %s. got=%s%n",
                    expectedLiteral, bool.tokenLiteral());
            return false;
        }
        return true;
    }


    @Test
    void testIfExpression() {
        String input = "if (x < y) { x }";
        Lexer l = new Lexer(input);
        Parser p = new Parser(l);
        Program program = p.parseProgram();

        checkParserErrorsv2(p);

        assertEquals(1, program.getStatements().size(),
                "program.Body does not contain 1 statement");

        Statement stmt = program.getStatements().get(0);
        assertInstanceOf(ExpressionStatement.class, stmt,
                "program.Statements[0] is not ExpressionStatement");

        Expression expr = ((ExpressionStatement) stmt).getExpression();
        assertInstanceOf(IfExpression.class, expr,
                "stmt.Expression is not IfExpression");

        IfExpression ifExp = (IfExpression) expr;

        // Test condition
        testInfixExpression(ifExp.getCondition(), "x", "<", "y");

        // Test consequence
        BlockStatement consequence = ifExp.getConsequence();
        assertEquals(1, consequence.getStatements().size(),
                "Consequence does not contain 1 statement");

        Statement consStmt = consequence.getStatements().get(0);
        assertInstanceOf(ExpressionStatement.class, consStmt,
                "Consequence statement is not ExpressionStatement");

        Expression consExpr = ((ExpressionStatement) consStmt).getExpression();
        testIdentifierv2(consExpr, "x");

        // Test alternative (should be null)
        assertNull(ifExp.getAlternative(),
                "Alternative block should be null");
    }

    private void checkParserErrorsv2(Parser p) {
        if (p.getErrors().isEmpty()) return;

        fail("Parser errors:\n" + String.join("\n", p.getErrors()));
    }

    private void testInfixExpression(Expression expr, String left, String operator, String right) {
        assertInstanceOf(InfixExpression.class, expr);
        InfixExpression infix = (InfixExpression) expr;

        testIdentifier(infix.getLeft(), left);
        assertEquals(operator, infix.getOperator());
        testIdentifier(infix.getRight(), right);
    }

    private void testIdentifierv2(Expression expr, String value) {
        assertInstanceOf(Identifier.class, expr);
        Identifier ident = (Identifier) expr;
        assertEquals(value, ident.getValue());
    }

/* let check = fn (x){ if(x==9){ print(99); } else if(x==8){ print(88); }  else if(x==7){ print(77); } };*/
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    @Test
    void testFunctionLiteralParsing() {
        String input = "fn(x, y) { x + y; }";
        Lexer l = new Lexer(input);
        Parser p = new Parser(l);
        Program program = p.parseProgram();

        checkParserErrors(p);

        assertEquals(1, program.getStatements().size(),
                "program.body does not contain 1 statement");

        Statement stmt = program.getStatements().get(0);
        assertInstanceOf(ExpressionStatement.class, stmt,
                "program.Statements[0] is not ExpressionStatement");

        Expression expr = ((ExpressionStatement) stmt).getExpression();
        assertInstanceOf(FunctionLiteral.class, expr,
                "stmt.Expression is not FunctionLiteral");

        FunctionLiteral function = (FunctionLiteral) expr;

        // Test parameters
        assertEquals(2, function.getParameters().size(),
                "Function literal parameters wrong. Expected 2");
        testLiteralExpression(function.getParameters().get(0), "x");
        testLiteralExpression(function.getParameters().get(1), "y");

        // Test body
        BlockStatement body = function.getBody();
        assertEquals(1, body.getStatements().size(),
                "Function body has incorrect number of statements");

        Statement bodyStmt = body.getStatements().get(0);
        assertInstanceOf(ExpressionStatement.class, bodyStmt,
                "Function body statement is not ExpressionStatement");

        Expression bodyExpr = ((ExpressionStatement) bodyStmt).getExpression();
        testInfixExpressionv2(bodyExpr, "x", "+", "y");
    }

    private void testLiteralExpression(Expression expr, String expected) {
        assertInstanceOf(Identifier.class, expr);
        Identifier ident = (Identifier) expr;
        assertEquals(expected, ident.getValue(),
                "Identifier value mismatch. Expected: " + expected);
    }

    // Helper method for infix expressions from previous tests
    private void testInfixExpressionv2(Expression expr, String left,
                                     String operator, String right) {
        assertInstanceOf(InfixExpression.class, expr);
        InfixExpression infix = (InfixExpression) expr;

        testLiteralExpression(infix.getLeft(), left);
        assertEquals(operator, infix.getOperator(),
                "Infix operator mismatch");
        testLiteralExpression(infix.getRight(), right);
    }

    @ParameterizedTest
    @MethodSource("provideParameterTestCases")
    void testFunctionParameterParsing(String input, List<String> expectedParams) {
        Lexer l = new Lexer(input);
        Parser p = new Parser(l);
        Program program = p.parseProgram();

        checkParserErrorsv2(p);

        // Verify statement structure
        assertEquals(1, program.getStatements().size(),
                "Program should contain 1 statement");

        Statement stmt = program.getStatements().get(0);
        assertInstanceOf(ExpressionStatement.class, stmt,
                "First statement should be ExpressionStatement");

        Expression expr = ((ExpressionStatement) stmt).getExpression();
        assertInstanceOf(FunctionLiteral.class, expr,
                "Expression should be FunctionLiteral");

        FunctionLiteral function = (FunctionLiteral) expr;

        // Verify parameter count
        assertEquals(expectedParams.size(), function.getParameters().size(),
                "Parameter count mismatch");

        // Verify individual parameters
        for (int i = 0; i < expectedParams.size(); i++) {
            Identifier param = function.getParameters().get(i);
            testIdentifierv3(param, expectedParams.get(i));
        }
    }

    private static Stream<Arguments> provideParameterTestCases() {
        return Stream.of(
                Arguments.of("fn() {}", List.of()),
                Arguments.of("fn(x) {}", List.of("x")),
                Arguments.of("fn(x, y, z) {}", List.of("x", "y", "z"))
        );
    }

    private void testIdentifierv3(Expression expr, String expectedValue) {
        assertInstanceOf(Identifier.class, expr,
                "Expected identifier expression");
        Identifier ident = (Identifier) expr;
        assertEquals(expectedValue, ident.getValue(),
                "Identifier value mismatch");
    }

    // -----------------------------------------------------------------------------------------------------


    @Test
    void testCallExpressionParsing() {
        String input = "add(1, 2 * 3, 4 + 5);";
        Lexer l = new Lexer(input);
        Parser p = new Parser(l);
        Program program = p.parseProgram();

        checkParserErrorsv2(p);

        assertEquals(1, program.getStatements().size(),
                "Program should contain 1 statement");

        Statement stmt = program.getStatements().get(0);
        assertInstanceOf(ExpressionStatement.class, stmt,
                "Statement is not ExpressionStatement");

        Expression expr = ((ExpressionStatement) stmt).getExpression();
        assertInstanceOf(CallExpression.class, expr,
                "Expression is not CallExpression");

        CallExpression callExp = (CallExpression) expr;

        // Test function identifier
        testIdentifierExpression(callExp.getFunction(), "add");

        // Test arguments
        List<Expression> args = callExp.getArguments();
        assertEquals(3, args.size(), "Wrong number of arguments");

        testLiteralExpression(args.get(0), 1);
        testInfixExpression(args.get(1), 2, "*", 3);
        testInfixExpression(args.get(2), 4, "+", 5);
    }

    private void testIdentifierExpression(Expression expr, String expectedValue) {
        assertInstanceOf(Identifier.class, expr);
        Identifier ident = (Identifier) expr;
        assertEquals(expectedValue, ident.getValue());
    }

    private void testLiteralExpression(Expression expr, int expected) {
        assertInstanceOf(IntegerLiteral.class, expr);
        IntegerLiteral literal = (IntegerLiteral) expr;
        assertEquals(expected, literal.getValue());
    }

    private void testInfixExpression(Expression expr, int left,
                                     String operator, int right) {
        assertInstanceOf(InfixExpression.class, expr);
        InfixExpression infix = (InfixExpression) expr;

        testLiteralExpression(infix.getLeft(), left);
        assertEquals(operator, infix.getOperator());
        testLiteralExpression(infix.getRight(), right);
    }


    @ParameterizedTest
    @MethodSource("providePrecedenceTestCases")
    void testOperatorPrecedenceParsingv2(String input, String expected) {
        Lexer l = new Lexer(input);
        Parser p = new Parser(l);
        Program program = p.parseProgram();

        checkParserErrorsv2(p);

        String actual = program.toString();
        assertEquals(expected, actual,
                "AST string representation mismatch");
    }

    private static Stream<Arguments> providePrecedenceTestCases() {
        return Stream.of(
                Arguments.of(
                        "-a * b",
                        "((-a) * b)"
                ),
                Arguments.of(
                        "!-a",
                        "(!(-a))"
                ),
                Arguments.of(
                        "a + add(b * c) + d",
                        "((a + add((b * c))) + d)"
                ),
                Arguments.of(
                        "add(a, b, 1, 2 * 3, 4 + 5, add(6, 7 * 8))",
                        "add(a, b, 1, (2 * 3), (4 + 5), add(6, (7 * 8)))"
                ),
                Arguments.of(
                        "add(a + b + c * d / f + g)",
                        "add((((a + b) + ((c * d) / f)) + g))"
                )
        );
    }


    @Test
    void testStringLiteralExpression() {
        String input = "\"hello world\";";
        Lexer lexer = new Lexer(input);
        Parser parser = new Parser(lexer);
        Program program = parser.parseProgram();

        // Check for parser errors
        assertFalse(parser.hasErrors(),
                "Parser had errors: " + parser.getErrors());

        // Verify statement structure
        assertEquals(1, program.getStatements().size(),
                "Program should have 1 statement");

        Statement stmt = program.getStatements().get(0);
        assertInstanceOf(ExpressionStatement.class, stmt,
                "Statement is not ExpressionStatement");

        Expression expression = ((ExpressionStatement) stmt).getExpression();
        assertInstanceOf(StringLiteral.class, expression,
                "Expression is not StringLiteral");

        // Validate string value
        StringLiteral literal = (StringLiteral) expression;
        assertEquals("hello world", literal.getValue(),
                "String value mismatch");
    }

    @Test
    void testParsingArrayLiterals() {
        String input = "[1, 2 * 2, 3 + 3]";
        Lexer lexer = new Lexer(input);
        Parser parser = new Parser(lexer);
        Program program = parser.parseProgram();

        assertParserErrors(parser);

        Statement stmt = program.getStatements().get(0);
        assertInstanceOf(ExpressionStatement.class, stmt);

        Expression expression = ((ExpressionStatement) stmt).getExpression();
        assertInstanceOf(ArrayLiteral.class, expression);

        ArrayLiteral array = (ArrayLiteral) expression;
        assertEquals(3, array.getElements().size());

        testIntegerLiteral(array.getElements().get(0), 1);
        testInfixExpression(array.getElements().get(1), 2, "*", 2);
        testInfixExpression(array.getElements().get(2), 3, "+", 3);
    }

    private void assertParserErrors(Parser parser) {
        assertTrue(parser.getErrors().isEmpty(),
                "Parser had errors: " + String.join("\n", parser.getErrors()));
    }


    @Test
    void testParsingIndexExpressions() {
        String input = "myArray[1 + 1]";
        Lexer lexer = new Lexer(input);
        Parser parser = new Parser(lexer);
        Program program = parser.parseProgram();

        assertParserErrors(parser);

        Statement stmt = program.getStatements().get(0);
        assertInstanceOf(ExpressionStatement.class, stmt);

        Expression expression = ((ExpressionStatement) stmt).getExpression();
        assertInstanceOf(IndexExpression.class, expression);

        IndexExpression indexExp = (IndexExpression) expression;
        testIdentifier(indexExp.getLeft(), "myArray");
        testInfixExpression(indexExp.getIndex(), 1L, "+", 1L);
    }

    @Test
    void testOperatorPrecedenceParsing() {
        String[][] tests = {
                {"a * [1, 2, 3, 4][b * c] * d", "((a * ([1, 2, 3, 4][(b * c)])) * d)"},
                {"add(a * b[2], b[1], 2 * [1, 2][1])",
                        "add((a * (b[2])), (b[1]), (2 * ([1, 2][1])))"}
        };

        for (String[] test : tests) {
            Lexer lexer = new Lexer(test[0]);
            Parser parser = new Parser(lexer);
            Program program = parser.parseProgram();

            assertParserErrors(parser);
            assertEquals(test[1], program.toString());
        }
    }
//////////////////////////////////////////////////////////////////////////////////

    @Test
    void testParsingHashLiteralsStringKeys() {
        String input = "{\"one\": 1, \"two\": 2, \"three\": 3}";
        Lexer lexer = new Lexer(input);
        Parser parser = new Parser(lexer);
        Program program = parser.parseProgram();

        assertParserErrors(parser);

        ExpressionStatement stmt = (ExpressionStatement) program.getStatements().get(0);
        HashLiteral hash = (HashLiteral) stmt.getExpression();

        assertEquals(3, hash.getPairs().size());

        Map<String, Long> expected = Map.of(
                "one", 1L,
                "two", 2L,
                "three", 3L
        );

        hash.getPairs().forEach((key, value) -> {
            StringLiteral literal = (StringLiteral) key;
            long expectedValue = expected.get(literal.getValue());
            testIntegerLiteral(value, expectedValue);
        });
    }

    @Test
    void testParsingEmptyHashLiteral() {
        String input = "{}";
        Lexer lexer = new Lexer(input);
        Parser parser = new Parser(lexer);
        Program program = parser.parseProgram();

        assertParserErrors(parser);

        ExpressionStatement stmt = (ExpressionStatement) program.getStatements().get(0);
        HashLiteral hash = (HashLiteral) stmt.getExpression();
        assertEquals(0, hash.getPairs().size());
    }

    @Test
    void testParsingHashLiteralsWithExpressions() {
        String input = "{\"one\": 0 + 1, \"two\": 10 - 8, \"three\": 15 / 5}";
        Lexer lexer = new Lexer(input);
        Parser parser = new Parser(lexer);
        Program program = parser.parseProgram();

        assertParserErrors(parser);

        HashLiteral hash = (HashLiteral) ((ExpressionStatement) program.getStatements().get(0)).getExpression();
        assertEquals(3, hash.getPairs().size());

        Map<String, Function<Expression, Void>> tests = new HashMap<>();
        tests.put("one", e -> { testInfixExpression(e, 0L, "+", 1L); return null; });
        tests.put("two", e -> { testInfixExpression(e, 10L, "-", 8L); return null; });
        tests.put("three", e -> { testInfixExpression(e, 15L, "/", 5L); return null; });

        hash.getPairs().forEach((key, value) -> {
            StringLiteral literal = (StringLiteral) key;
            tests.get(literal.getValue()).apply(value);
        });
    }
}