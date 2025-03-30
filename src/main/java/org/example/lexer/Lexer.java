package org.example.lexer;


import org.example.token.Token;

public class Lexer {
    private String input;
    private int position;      // current position in input (points to current char)
    private int readPosition;  // current reading position in input (after current char)
    private char ch;           // current char under examination

    public Lexer(String input) {
        this.input = input;
        this.position = 0;
        this.readPosition = 0;
        this.ch = '\0'; // Initialize with null character
        this.readChar(); // Call readChar to set the initial character
    }
    public void readChar() {
        if (readPosition >= input.length()) {
            ch = '\0'; // Represents the null character to indicate EOF
        } else {
            ch = input.charAt(readPosition); // Get the character at the current read position
        }
        position = readPosition;
        readPosition += 1;
    }
    public Token nextToken() {
        Token tok;

        skipWhitespace(); // Skip over any whitespace characters

        switch (ch) {
            case '{': // Case for LBRACE
                tok = new Token(Token.TokenType.LBRACE, String.valueOf(ch));
                break;
            case '}': // Case for RBRACE (to be thorough)
                tok = new Token(Token.TokenType.RBRACE, String.valueOf(ch));
                break;
            case '=':
                if (peekChar() == '=') { // Look ahead to check for '=='
                    char currentChar = ch;
                    readChar(); // Consume the second '='
                    tok = new Token(Token.TokenType.EQ, String.valueOf(currentChar) + ch);
                } else {
                    tok = new Token(Token.TokenType.ASSIGN, String.valueOf(ch));
                }
                break;
            case '!':
                if (peekChar() == '=') { // Look ahead to check for '!='
                    char currentChar = ch;
                    readChar(); // Consume the second '='
                    tok = new Token(Token.TokenType.NOT_EQ, String.valueOf(currentChar) + ch);
                } else {
                    tok = new Token(Token.TokenType.BANG, String.valueOf(ch));
                }
                break;
            case ';':
                tok = new Token(Token.TokenType.SEMICOLON, String.valueOf(ch));
                break;
            case '(':
                tok = new Token(Token.TokenType.LPAREN, String.valueOf(ch));
                break;
            case ')':
                tok = new Token(Token.TokenType.RPAREN, String.valueOf(ch));
                break;
            case ',':
                tok = new Token(Token.TokenType.COMMA, String.valueOf(ch));
                break;
            case '+':
                tok = new Token(Token.TokenType.PLUS, String.valueOf(ch));
                break;
            case '.':
                tok = new Token(Token.TokenType.DOT, String.valueOf(ch));
                break;
            case '-':
                tok = new Token(Token.TokenType.MINUS, String.valueOf(ch));
                break;
            case '/':
                tok = new Token(Token.TokenType.SLASH, String.valueOf(ch));
                break;
            case '*':
                tok = new Token(Token.TokenType.ASTERISK, String.valueOf(ch));
                break;
            case '&':
                if (peekChar() == '&') {
                    char currentChar = ch;
                    readChar();
                    tok = new Token(Token.TokenType.AND, String.valueOf(currentChar) + ch);
                } else {
                    tok = new Token(Token.TokenType.ILLEGAL, String.valueOf(ch)); // Single & not supported
                }
                break;
            case '|':
                if (peekChar() == '|') {
                    char currentChar = ch;
                    readChar();
                    tok = new Token(Token.TokenType.OR, String.valueOf(currentChar) + ch);
                } else {
                    tok = new Token(Token.TokenType.ILLEGAL, String.valueOf(ch)); // Single | not supported
                }
                break;
            case '<':
                if (peekChar() == '=') { // New: <=
                    char currentChar = ch;
                    readChar();
                    tok = new Token(Token.TokenType.LE, String.valueOf(currentChar) + ch);
                } else {
                    tok = new Token(Token.TokenType.LT, String.valueOf(ch));
                }
                break;
            case '>':
                if (peekChar() == '=') { // New: >=
                    char currentChar = ch;
                    readChar();
                    tok = new Token(Token.TokenType.GE, String.valueOf(currentChar) + ch);
                } else {
                    tok = new Token(Token.TokenType.GT, String.valueOf(ch));
                }
                break;
            case ':':
                tok = new Token(Token.TokenType.COLON, String.valueOf(ch));
                break;
            case '[':
                tok = new Token(Token.TokenType.LBRACKET, String.valueOf(ch));
                break;
            case ']':
                tok =  new Token(Token.TokenType.RBRACKET, String.valueOf(ch));
                break;
            case '"':
                tok= new Token(Token.TokenType.STRING, readString());
                break;
            case '\0': // End of input
                tok = new Token(Token.TokenType.EOF, "");
                break;
            default:
                if (isLetter(ch)) {
                    String ident = readIdentifier();
                    return new Token(Token.lookupIdent(ident), ident);
                }
                /*if (isLetter(ch)) {
                    String literal = readIdentifier(); // Read the identifier
                    Token.TokenType type = Token.lookupIdent(literal); // Check if it's a keyword
                    tok = new Token(type, literal);
                    return tok; // Return the token immediately
                }*/ else if (isDigit(ch)) {
                    String literal = readNumber(); // Read the full number
                    tok = new Token(Token.TokenType.INT, literal); // Create INT token
                    return tok; // Return the token immediately
                } else {
                    tok = new Token(Token.TokenType.ILLEGAL, String.valueOf(ch)); // Handle illegal chars
                }
        }

        readChar(); // Move to the next character
        return tok;
    }

    private boolean isLetter(char ch) {
        return (ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || ch == '_';
    }

    private String readIdentifier() {
        int startPosition = position;
        while (isLetter(ch)) {
            readChar();
        }
        return input.substring(startPosition, position);
    }

    private void skipWhitespace() {
        while (ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r') {
            readChar(); // Keep advancing until no more whitespace
        }
    }

    private String readNumber() {
        int startPosition = position;
        while (isDigit(ch)) {
            readChar();
        }
        return input.substring(startPosition, position); // Extract the number as a string
    }
    private boolean isDigit(char ch) {
        return ch >= '0' && ch <= '9'; // Checks if the character is a digit (0-9)
    }

    private char peekChar() {
        if (readPosition >= input.length()) {
            return '\0'; // Return null character if out of bounds
        } else {
            return input.charAt(readPosition);
        }
    }

    private String readString() {
        StringBuilder sb = new StringBuilder();
        readChar(); // Skip the opening quote '"'
        while (ch != '"' && ch != 0) {
            sb.append(ch);
            readChar();
        }
        // After loop, ch is '"' (closing quote) or 0 (EOF)
        //readChar();
        return sb.toString();
    }

}

