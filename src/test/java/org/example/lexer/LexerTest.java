package org.example.lexer;

import org.example.token.Token;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;
//import static org.junit.jupiter.api.Assertions.assertEquals;

public class LexerTest {
    @Test
    public void testNextToken() {
        String input = "=+(){},;";

        Token[] expectedTokens = {
                new Token(Token.TokenType.ASSIGN, "="),
                new Token(Token.TokenType.PLUS, "+"),
                new Token(Token.TokenType.LPAREN, "("),
                new Token(Token.TokenType.RPAREN, ")"),
                new Token(Token.TokenType.LBRACE, "{"),
                new Token(Token.TokenType.RBRACE, "}"),
                new Token(Token.TokenType.COMMA, ","),
                new Token(Token.TokenType.SEMICOLON, ";"),
                new Token(Token.TokenType.EOF, "")
        };

        Lexer lexer = new Lexer(input);

        for (int i = 0; i < expectedTokens.length; i++) {
            Token token = lexer.nextToken();

            Assertions.assertEquals(expectedTokens[i].getType(), token.getType(),
                    String.format("tests[%d] - tokentype wrong. expected=%s, got=%s",
                            i, expectedTokens[i].getType(), token.getType()));

            Assertions.assertEquals(expectedTokens[i].getLiteral(), token.getLiteral(),
                    String.format("tests[%d] - literal wrong. expected=%s, got=%s",
                            i, expectedTokens[i].getLiteral(), token.getLiteral()));
        }
    }

    @Test
    public void testNextTokenFullVersion() {
        String input = """
        let five = 5;
        let ten = 10;
        let add = fn(x, y) {
            x + y;
        };
        let result = add(five, ten);
        !-/*5;
        5 < 10 > 5;
        if (5 < 10) {
            return true;
        } else {
            return false;
        }
        10 == 10;
        10 != 9;
        "foobar"
        "foo bar"
        [1, 2];
        {"foo": "bar"}
    """;

        Token[] expectedTokens = {
                new Token(Token.TokenType.LET, "let"),
                new Token(Token.TokenType.IDENT, "five"),
                new Token(Token.TokenType.ASSIGN, "="),
                new Token(Token.TokenType.INT, "5"),
                new Token(Token.TokenType.SEMICOLON, ";"),
                new Token(Token.TokenType.LET, "let"),
                new Token(Token.TokenType.IDENT, "ten"),
                new Token(Token.TokenType.ASSIGN, "="),
                new Token(Token.TokenType.INT, "10"),
                new Token(Token.TokenType.SEMICOLON, ";"),
                new Token(Token.TokenType.LET, "let"),
                new Token(Token.TokenType.IDENT, "add"),
                new Token(Token.TokenType.ASSIGN, "="),
                new Token(Token.TokenType.FUNCTION, "fn"),
                new Token(Token.TokenType.LPAREN, "("),
                new Token(Token.TokenType.IDENT, "x"),
                new Token(Token.TokenType.COMMA, ","),
                new Token(Token.TokenType.IDENT, "y"),
                new Token(Token.TokenType.RPAREN, ")"),
                new Token(Token.TokenType.LBRACE, "{"),
                new Token(Token.TokenType.IDENT, "x"),
                new Token(Token.TokenType.PLUS, "+"),
                new Token(Token.TokenType.IDENT, "y"),
                new Token(Token.TokenType.SEMICOLON, ";"),
                new Token(Token.TokenType.RBRACE, "}"),
                new Token(Token.TokenType.SEMICOLON, ";"),
                new Token(Token.TokenType.LET, "let"),
                new Token(Token.TokenType.IDENT, "result"),
                new Token(Token.TokenType.ASSIGN, "="),
                new Token(Token.TokenType.IDENT, "add"),
                new Token(Token.TokenType.LPAREN, "("),
                new Token(Token.TokenType.IDENT, "five"),
                new Token(Token.TokenType.COMMA, ","),
                new Token(Token.TokenType.IDENT, "ten"),
                new Token(Token.TokenType.RPAREN, ")"),
                new Token(Token.TokenType.SEMICOLON, ";"),
                new Token(Token.TokenType.BANG, "!"),
                new Token(Token.TokenType.MINUS, "-"),
                new Token(Token.TokenType.SLASH, "/"),
                new Token(Token.TokenType.ASTERISK, "*"),
                new Token(Token.TokenType.INT, "5"),
                new Token(Token.TokenType.SEMICOLON, ";"),
                new Token(Token.TokenType.INT, "5"),
                new Token(Token.TokenType.LT, "<"),
                new Token(Token.TokenType.INT, "10"),
                new Token(Token.TokenType.GT, ">"),
                new Token(Token.TokenType.INT, "5"),
                new Token(Token.TokenType.SEMICOLON, ";"),
                new Token(Token.TokenType.IF, "if"),
                new Token(Token.TokenType.LPAREN, "("),
                new Token(Token.TokenType.INT, "5"),
                new Token(Token.TokenType.LT, "<"),
                new Token(Token.TokenType.INT, "10"),
                new Token(Token.TokenType.RPAREN, ")"),
                new Token(Token.TokenType.LBRACE, "{"),
                new Token(Token.TokenType.RETURN, "return"),
                new Token(Token.TokenType.TRUE, "true"),
                new Token(Token.TokenType.SEMICOLON, ";"),
                new Token(Token.TokenType.RBRACE, "}"),
                new Token(Token.TokenType.ELSE, "else"),
                new Token(Token.TokenType.LBRACE, "{"),
                new Token(Token.TokenType.RETURN, "return"),
                new Token(Token.TokenType.FALSE, "false"),
                new Token(Token.TokenType.SEMICOLON, ";"),
                new Token(Token.TokenType.RBRACE, "}"),
                new Token(Token.TokenType.INT, "10"),
                new Token(Token.TokenType.EQ, "=="),
                new Token(Token.TokenType.INT, "10"),
                new Token(Token.TokenType.SEMICOLON, ";"),
                new Token(Token.TokenType.INT, "10"),
                new Token(Token.TokenType.NOT_EQ, "!="),
                new Token(Token.TokenType.INT, "9"),
                new Token(Token.TokenType.SEMICOLON, ";"),
                new Token(Token.TokenType.STRING, "foobar"),
                new Token(Token.TokenType.STRING, "foo bar"),
                new Token(Token.TokenType.LBRACKET, "["),
                new Token(Token.TokenType.INT, "1"),
                new Token(Token.TokenType.COMMA, ","),
                new Token(Token.TokenType.INT, "2"),
                new Token(Token.TokenType.RBRACKET, "]"),
                new Token(Token.TokenType.SEMICOLON, ";"),
                new Token(Token.TokenType.LBRACE, "{"),
                new Token(Token.TokenType.STRING, "foo"),
                new Token(Token.TokenType.COLON, ":"),
                new Token(Token.TokenType.STRING, "bar"),
                new Token(Token.TokenType.RBRACE, "}"),
                new Token(Token.TokenType.EOF, "")
        };

        Lexer lexer = new Lexer(input);

        for (int i = 0; i < expectedTokens.length; i++) {
            Token token = lexer.nextToken();

            Assertions.assertEquals(expectedTokens[i].getType(), token.getType(),
                    String.format("tests[%d] - tokentype wrong. expected=%s, got=%s",
                            i, expectedTokens[i].getType(), token.getType()));

            Assertions.assertEquals(expectedTokens[i].getLiteral(), token.getLiteral(),
                    String.format("tests[%d] - literal wrong. expected=%s, got=%s",
                            i, expectedTokens[i].getLiteral(), token.getLiteral()));
        }
    }

    @ParameterizedTest
    @MethodSource("bracketTokenProvider")
    void testBracketTokens(String input, Token.TokenType expectedType, String expectedLiteral) {
        Lexer lexer = new Lexer(input);
        Token token = lexer.nextToken();
        Assertions.assertEquals(expectedType, token.getType());
        Assertions.assertEquals(expectedLiteral, token.getLiteral());
    }

    private static Stream<Arguments> bracketTokenProvider() {
        return Stream.of(
                Arguments.of("[", Token.TokenType.LBRACKET, "["),
                Arguments.of("]", Token.TokenType.RBRACKET, "]"),
                Arguments.of("[1, 2]", Token.TokenType.LBRACKET, "["),
                Arguments.of("[]", Token.TokenType.LBRACKET, "[")
        );
    }
}
