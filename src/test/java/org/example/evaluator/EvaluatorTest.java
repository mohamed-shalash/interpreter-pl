package org.example.evaluator;

import junit.framework.TestCase;
import org.example.ast.Identifier;
import org.example.ast.Program;
import org.example.lexer.Lexer;
import org.example.object.*;
import org.example.object.Object;
import org.example.parser.Parser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;


class EvaluatorTest {

    @ParameterizedTest
    @CsvSource({
            "'5', 5",
            "'10', 10",
            "'-5', -5",
            "'-10', -10",
            "'5 + 5 + 5 + 5 - 10', 10",
            "'2 * 2 * 2 * 2 * 2', 32",
            "'-50 + 100 + -50', 0",
            "'5 * 2 + 10', 20",
            "'5 + 2 * 10', 25",
            "'20 + 2 * -10', 0",
            "'50 / 2 * 2 + 10', 60",
            "'2 * (5 + 10)', 30",
            "'3 * 3 * 3 + 10', 37",
            "'3 * (3 * 3) + 10', 37",
            "'(5 + 10 * 2 + 15 / 3) * 2 + -10', 50"
    })
    void testEvalIntegerExpression(String input, long expected) {
        Object evaluated = testEval(input);
        assertIntegerObject(evaluated, expected);
    }

    private Object testEval(String input) {
        Lexer l = new Lexer(input);
        Parser p = new Parser(l);
        var program = p.parseProgram();
        checkParserErrors(p);
        Environment env = new Environment();
        return Evaluator.eval(program,env);
    }




    private void assertIntegerObject(Object obj, long expected) {
        assertInstanceOf(IntegerObject.class, obj, "Object is not IntegerObject");
        IntegerObject integer = (IntegerObject) obj;
        assertEquals(expected, integer.getValue(), "Integer value mismatch");
    }

    private void checkParserErrors(Parser parser) {
        var errors = parser.getErrors();
        if (errors.isEmpty()) return;

        fail("Parser had " + errors.size() + " errors:\n" + String.join("\n", errors));
    }


    /*@Test
    void testEvalInvalidExpression() {
        String input = "foobar";
        Object result = testEval(input);
        assertEquals(NullObject.NULL, result);
    }*/




    @ParameterizedTest
    @CsvSource({
            "true, true",
            "false, false",
            "'!true', false",
            "'!false', true",
            "'!5', false",
            "'!!true', true",
            "'!!false', false",
            "'!!5', true",

            // Comparisons
            "5 < 5, false",
            "5 < 10, true",
            "5 > 5, false",
            "5 > 2, true",
            "5 == 5, true",
            "5 != 5, false",
            "10 == 5, false",
            "10 != 5, true",

            // Mixed type comparisons
            "true == true, true",
            "true != false, true",
            "false == false, true",
            "(5 < 10) == true, true",
            "(5 < 10) == false, false",

            // Type mismatch cases
            "true == 10, false",
            "true != 10, true",
            "10 == false, false",
            "10 != true, true"
    })
    void testEvalBooleanExpression(String input, boolean expected) {
        Object evaluated = testEval(input);
        assertBooleanObject(evaluated, expected);
    }

    private void assertBooleanObject(Object obj, boolean expected) {
        assertInstanceOf(BooleanObject.class, obj, "Object is not BooleanObject");
        BooleanObject boolObj = (BooleanObject) obj;
        assertEquals(expected, boolObj.getValue(), "Boolean value mismatch");
    }




    @ParameterizedTest
    @CsvSource({
            "'if (true) { 10 }', '10'",
            "'if (false) { 10 }', 'null'",
            "'if (1) { 10 }', '10'",
            "'if (1 < 2) { 10 }', '10'",
            "'if (1 > 2) { 10 }', 'null'",
            "'if (1 > 2) { 10 } else { 20 }', '20'",
            "'if (1 < 2) { 10 } else { 20 }', '10'"
    })
    void testIfElseExpressions(String input, String expectedStr) {
        Object evaluated = testEval(input);

        if ("null".equals(expectedStr)) {
            assertNullObject(evaluated);
        } else {
            long expected = Long.parseLong(expectedStr);
            assertIntegerObject(evaluated, expected);
        }
    }



    private void assertNullObject(Object obj) {
        assertSame(NullObject.NULL, obj, "Object is not NullObject");
    }


    @ParameterizedTest
    @MethodSource("returnStatementTestCases")
    void testReturnStatements(String input, long expected) {
        Object evaluated = testEvalObject(input);
        testIntegerObject(evaluated, expected);
    }

    // Define test cases
    private static Stream<Arguments> returnStatementTestCases() {
        return Stream.of(
                Arguments.of("return 10;", 10),
                Arguments.of("return 10; 9;", 10),
                Arguments.of("return 2 * 5; 9;", 10),
                Arguments.of("9; return 2 * 5; 9;", 10),
                Arguments.of(
                        """
                        if (10 > 1) {
                            if (10 > 1) {
                                return 10;
                            }
                            return 1;
                        }
                        """,
                        10L
                )
        );
    }

    private static Object testEvalObject(String input) {
        Lexer lexer = new Lexer(input);
        Parser parser = new Parser(lexer);
        Program program = parser.parseProgram();

        // Check for parser errors first
        if (!parser.getErrors().isEmpty()) {
            return new ErrorObject("Parser errors: " + String.join(", ", parser.getErrors()));
        }
        Environment e =  new Environment();
        Object result = Evaluator.eval(program,e);

        // Unwrap ReturnValue if needed
        while (result instanceof ReturnValue) {
            result = ((ReturnValue) result).getValue();
        }

        return result;
    }

    // Helper to assert integer results
    private void testIntegerObject(Object obj, long expected) {
        assertTrue(obj instanceof IntegerObject,
                "Expected IntegerObject, got " + obj.getClass().getSimpleName());

        IntegerObject integer = (IntegerObject) obj;
        assertEquals(expected, integer.getValue());
    }



//////////////////////////////////////////////////////////////////////////////////////////

    @ParameterizedTest
    @MethodSource("errorHandlingTestCases")
    void testErrorHandling(String input, String expectedMessage) {
        Object evaluated = testEvalObject(input);

        assertTrue(evaluated instanceof ErrorObject,
                "Expected ErrorObject, got " + evaluated.getClass().getSimpleName());

        ErrorObject error = (ErrorObject) evaluated;
        assertEquals(expectedMessage, error.getMessage(),
                "Wrong error message. Expected: " + expectedMessage + " Got: " + error.getMessage());
    }

    private static Stream<Arguments> errorHandlingTestCases() {
        return Stream.of(
                Arguments.of("5 + true;", "type mismatch: INTEGER + BOOLEAN"),
                Arguments.of("5 + true; 5;", "type mismatch: INTEGER + BOOLEAN"),
                Arguments.of("-true", "unknown operator: -BOOLEAN"),
                Arguments.of("true + false;", "unknown operator: BOOLEAN + BOOLEAN"),
                Arguments.of("5; true + false; 5", "unknown operator: BOOLEAN + BOOLEAN"),
                Arguments.of("if (10 > 1) { true + false; }", "unknown operator: BOOLEAN + BOOLEAN"),
                Arguments.of(
                        """
                        if (10 > 1) {
                            if (10 > 1) {
                                return true + false;
                            }
                            return 1;
                        }
                        """,
                        "unknown operator: BOOLEAN + BOOLEAN"
                ),
                Arguments.of("foobar", "identifier not found: foobar")
        );
    }
/////////////////////////////////////////////////////////////////
    @Test
    void testLetStatements() {
        List<TestCase> tests = List.of(
                new TestCase("let a = 5; a;", 5L),
                new TestCase("let a = 5 * 5; a;", 25L),
                new TestCase("let a = 5; let b = a; b;", 5L),
                new TestCase("let a = 5; let b = a; let c = a + b + 5; c;", 15L)
        );

        for (TestCase test : tests) {
            Object evaluated = testEval(test.input);
            testIntegerObject(evaluated, test.expected);
        }
    }

    record TestCase(String input, long expected) {

    }


    @Test
    void testFunctionObject() {
        String input = "fn(x) { x + 2; };";
        Object evaluated = testEval(input);

        assertInstanceOf(FunctionObject.class, evaluated,
                "Object is not FunctionObject. Got: " + evaluated.getClass().getSimpleName());

        FunctionObject fn = (FunctionObject) evaluated;

        // Verify parameters
        List<Identifier> params = fn.getParameters();
        assertEquals(1, params.size(),
                "Function has wrong parameters. Parameters=" + params);
        assertEquals("x", params.get(0).toString(),
                "Parameter is not 'x'. Got=" + params.get(0));

        // Verify body
        String expectedBody = "(x + 2)";
        String actualBody = fn.getBody().toString().trim(); // Handle whitespace
        assertEquals(expectedBody, actualBody,
                "Body not equal. Expected: " + expectedBody + " Got: " + actualBody);
    }

    @Test
    void testFunctionApplication() {
        List<TestCase> tests = List.of(
                new TestCase("let identity = fn(x) { x; }; identity(5);", 5L),
                new TestCase("let identity = fn(x) { return x; }; identity(5);", 5L),
                new TestCase("let double = fn(x) { x * 2; }; double(5);", 10L),
                new TestCase("let add = fn(x, y) { x + y; }; add(5, 5);", 10L),
                new TestCase("let add = fn(x, y) { x + y; }; add(5 + 5, add(5, 5));", 20L),
                new TestCase("fn(x) { x; }(5)", 5L)
        );

        for (TestCase test : tests) {
            Object evaluated = testEval(test.input);
            testIntegerObject(evaluated, test.expected);
        }
    }

    /** private void testIntegerObject(Object obj, long expected) {
     assertTrue(obj instanceof IntegerObject,
     "Object is not IntegerObject. Got: " +
     (obj != null ? obj.getClass().getSimpleName() : "null"));

     assertEquals(expected, ((IntegerObject) obj).getValue(),
     "Value mismatch. Expected: " + expected + " Got: " +
     ((IntegerObject) obj).getValue());
     }*/


    @Test
    void testClosures() {
        String input = """
            let newAdder = fn(x) {
                fn(y) { x + y };
            };
            let addTwo = newAdder(2);
            addTwo(2);""";

        Object evaluated = testEval(input);
        testIntegerObject(evaluated, 4L);
    }

    @Test
    void testStringLiteral() {
        String input = "\"Hello World!\"";
        Object evaluated = testEval(input);

        assertInstanceOf(StringObject.class, evaluated,
                "Object is not StringObject. Got: " + evaluated.getClass().getSimpleName());

        StringObject strObj = (StringObject) evaluated;
        assertEquals("Hello World!", strObj.getValue(),
                "String has wrong value. Got: " + strObj.getValue());
    }

    @Test
    void testStringConcatenation() {
        String input = "\"Hello\" + \" \" + \"World!\"";
        Object evaluated = testEval(input);

        assertInstanceOf(StringObject.class, evaluated,
                "Object is not StringObject. Got: " + evaluated.getClass().getSimpleName());

        StringObject strObj = (StringObject) evaluated;
        assertEquals("Hello World!", strObj.getValue(),
                "String has wrong value. Got: " + strObj.getValue());
    }


    @Test
    void testErrorHandling() {
        List<ErrorTestCase> tests = List.of(
                new ErrorTestCase(
                        "\"Hello\" - \"World\"",
                        "unknown operator: STRING - STRING"
                )
                // Add more test cases here
        );

        for (ErrorTestCase test : tests) {
            Object evaluated = testEval(test.input());
            assertInstanceOf(ErrorObject.class, evaluated,
                    "Expected ErrorObject. Got: " + evaluated.getClass().getSimpleName());

            ErrorObject error = (ErrorObject) evaluated;
            assertEquals(test.expectedMessage(), error.getMessage(),
                    "Wrong error message. Expected: " + test.expectedMessage() +
                            " Got: " + error.getMessage());
        }
    }

    private record ErrorTestCase(String input, String expectedMessage) {}

/////////////////////////////////////////////////////////////////////////////////////////////////////////////
@Test
void testParserOutput() {
    String input = "len(\"hello\")";
    Lexer lexer = new Lexer(input);
    Parser parser = new Parser(lexer);
    Program program = parser.parseProgram();

    // Should print a CallExpression with identifier "len" and string argument
    System.out.println("AST: " + program.toString());
}

    @Test
    void testStringLiteralEvaluation() {
        String input = "\"hello\"";
        Object result = testEval(input);
        assertInstanceOf(StringObject.class, result);
        assertEquals("hello", ((StringObject) result).getValue());
    }

    @ParameterizedTest
    @MethodSource("provideBuiltinFunctionTestCases")
    void testBuiltinFunctions(String input, java.lang.Object expected) {
        Object evaluated = testEval(input);

        if (expected instanceof Long) {
            testIntegerObject2(evaluated, (Long) expected);
        } else if (expected instanceof ErrorObject) {
            testErrorObject(evaluated, (ErrorObject) expected);
        }
    }



    private void testIntegerObject2(Object obj, long expected) {
        assertInstanceOf(IntegerObject.class, obj);
        assertEquals(expected, ((IntegerObject) obj).getValue());
    }

    private void testErrorObject(Object obj, ErrorObject expectedMessage) {
        assertInstanceOf(Error.class, obj);
        assertEquals(expectedMessage, ((ErrorObject) obj).getMessage());
    }

    private static Stream<Arguments> provideBuiltinFunctionTestCases() {
        return Stream.of(
                Arguments.of("len(\"\")", 0L),
                Arguments.of("len(\"four\")", 4L),
                Arguments.of("len(\"hello world\")", 11L),
                Arguments.of("len(1)", "argument to `len` not supported, got INTEGER"),
                Arguments.of("len(\"one\", \"two\")", "wrong number of arguments. got=2, want=1")
        );
    }


    @Test
    void testLenFunction() {
        Builtin len = Builtins.get("len");

        // Test valid string
        List<Object> args1 = List.of(new StringObject("hello"));
        Object result1 = len.apply(args1);
        assertEquals(5L, ((IntegerObject) result1).getValue());

        // Test empty string
        List<Object> args2 = List.of(new StringObject(""));
        Object result2 = len.apply(args2);
        assertEquals(0L, ((IntegerObject) result2).getValue());

        // Test invalid argument type
        List<Object> args3 = List.of(new IntegerObject(42));
        Object result3 = len.apply(args3);
        assertTrue(result3 instanceof ErrorObject);
        assertEquals("argument to `len` not supported, got INTEGER",
                ((ErrorObject) result3).getMessage());

        // Test wrong number of arguments
        List<Object> args4 = List.of(new StringObject("one"), new StringObject("two"));
        Object result4 = len.apply(args4);
        assertTrue(result4 instanceof ErrorObject);
        assertEquals("wrong number of arguments. got=2, want=1",
                ((ErrorObject) result4).getMessage());
    }

    ////////////////////////////////////////

    @Test
    void testArrayLiterals() {
        String input = "[1, 2 * 2, 3 + 3]";
        Object evaluated = testEval(input);

        assertInstanceOf(ArrayObject.class, evaluated);
        ArrayObject result = (ArrayObject) evaluated;

        assertEquals(3, result.getElements().size());
        testIntegerObject(result.getElements().get(0), 1L);
        testIntegerObject(result.getElements().get(1), 4L);
        testIntegerObject(result.getElements().get(2), 6L);
    }

    /*private void testIntegerObject(Object obj, long expected) {
        assertInstanceOf(IntegerObject.class, obj);
        assertEquals(expected, ((IntegerObject) obj).getValue());
    }*/


    @ParameterizedTest
    @MethodSource("arrayIndexProvider")
    void testArrayIndexExpressions(String input, java.lang.Object expected) {
        Object evaluated = testEval(input);

        if (expected instanceof Long) {
            testIntegerObject(evaluated, (Long) expected);
        } else {
            testNullObject(evaluated);
        }
    }

    private static Stream<Arguments> arrayIndexProvider() {
        return Stream.of(
                Arguments.of("[1, 2, 3][0]", 1L),
                Arguments.of("[1, 2, 3][1]", 2L),
                Arguments.of("[1, 2, 3][2]", 3L),
                Arguments.of("let i = 0; [1][i];", 1L),
                Arguments.of("[1, 2, 3][1 + 1]", 3L),
                Arguments.of("let myArray = [1, 2, 3]; myArray[2];", 3L),
                Arguments.of("let myArray = [1, 2, 3]; myArray[0] + myArray[1] + myArray[2];", 6L),
                Arguments.of("let myArray = [1, 2, 3]; let i = myArray[0]; myArray[i]", 2L),
                Arguments.of("[1, 2, 3][3]", null),
                Arguments.of("[1, 2, 3][-1]", null)
        );
    }

    private void testNullObject(Object obj) {
        assertEquals(EvaluatorConstants.NULL, obj);
    }


    @Test
    void testHashLiterals() {
        String input = """
            let two = "two";
            {
                "one": 10 - 9,
                two: 1 + 1,
                "thr" + "ee": 6 / 2,
                4: 4,
                true: 5,
                false: 6
            }
            """;

        Object evaluated = testEval(input);
        assertInstanceOf(HashObject.class, evaluated);

        HashObject result = (HashObject) evaluated;
        Map<HashKey, Long> expected = new HashMap<>();

        // Generate expected hash keys
        HashKey oneKey = new StringObject("one").hashKey();
        HashKey twoKey = new StringObject("two").hashKey();
        HashKey threeKey = new StringObject("three").hashKey();
        HashKey fourKey = new IntegerObject(4).hashKey();
        HashKey trueKey = new BooleanObject(true).hashKey();
        HashKey falseKey = new BooleanObject(false).hashKey();

        expected.put(oneKey, 1L);
        expected.put(twoKey, 2L);
        expected.put(threeKey, 3L);
        expected.put(fourKey, 4L);
        expected.put(trueKey, 5L);
        expected.put(falseKey, 6L);

        assertEquals(expected.size(), result.getPairs().size(),
                "Hash has wrong number of pairs");

        expected.forEach((expectedKey, expectedValue) -> {
            HashPair pair = result.getPairs().get(expectedKey);
            assertNotNull(pair, "No pair for key: " + expectedKey);
            testIntegerObject(pair.getValue(), expectedValue);
        });
    }

    @ParameterizedTest
    @MethodSource("testCases")
    void testHashIndexExpressions(String input, java.lang.Object expected) {
        Object evaluated = testEval(input);

        if (expected instanceof Integer) {
            testIntegerObject(evaluated, (long) (Integer) expected);
        } else {
            testNullObject(evaluated);
        }
    }

    private static Stream<Arguments> testCases() {
        return Stream.of(
                Arguments.of("{\"foo\": 5}[\"foo\"]", 5),
                Arguments.of("{\"foo\": 5}[\"bar\"]", null),
                Arguments.of("let key = \"foo\"; {\"foo\": 5}[key]", 5),
                Arguments.of("{}[\"foo\"]", null),
                Arguments.of("{5: 5}[5]", 5),
                Arguments.of("{true: 5}[true]", 5),
                Arguments.of("{false: 5}[false]", 5)
        );
    }
    @ParameterizedTest
    @MethodSource("errorCases")
    void testErrorHashingHandling(String input, String expectedMessage) {
        Object evaluated = testEval(input);
        testErrorObject(evaluated, expectedMessage);
    }
    private void testErrorObject(Object obj, String expectedMessage) {
        assertInstanceOf(ErrorObject.class, obj);
        assertEquals(expectedMessage, ((ErrorObject) obj).getMessage());
    }


    private static Stream<Arguments> errorCases() {
        return Stream.of(
                // Add other error test cases here
                Arguments.of(
                        "{\"name\": \"Monkey\"}[fn(x) { x }];",
                        "unusable as hash key: FUNCTION"
                )
        );
    }
}
