package org.example.parser;

import org.example.ast.*;
import org.example.ast.classes.*;
import org.example.lexer.Lexer;
import org.example.token.Token;

import java.util.*;


public class Parser {
    public static final int LOWEST = 1;
    public static final int EQUALS = 2;       // ==
    public static final int LESSGREATER = 3;  // > or <
    public static final int SUM = 4;          // +
    public static final int PRODUCT = 5;      // *
    public static final int PREFIX = 6;       // -X or !X
    public static final int CALL = 7;         // myFunction(X)
    public static final int INDEX = 8;
    private Lexer l;
    private Token curToken;
    private Token peekToken;
    private List<String> errors = new ArrayList<>();
    private Map<Token.TokenType, PrefixParseFn> prefixParseFns = new HashMap<>();
    private Map<Token.TokenType, InfixParseFn> infixParseFns = new HashMap<>();

    private static final Map<Token.TokenType, Integer> precedences;

    static {
        precedences = new EnumMap<>(Token.TokenType.class);
        precedences.put(Token.TokenType.EQ, EQUALS);
        precedences.put(Token.TokenType.NOT_EQ, EQUALS);
        precedences.put(Token.TokenType.LT, LESSGREATER);
        precedences.put(Token.TokenType.GT, LESSGREATER);
        precedences.put(Token.TokenType.PLUS, SUM);
        precedences.put(Token.TokenType.MINUS, SUM);
        precedences.put(Token.TokenType.SLASH, PRODUCT);
        precedences.put(Token.TokenType.ASTERISK, PRODUCT);
        precedences.put(Token.TokenType.LPAREN, CALL);
        precedences.put(Token.TokenType.LBRACKET, INDEX);
        precedences.put(Token.TokenType.ASSIGN, EQUALS);
        precedences.put(Token.TokenType.LE, LESSGREATER);
        precedences.put(Token.TokenType.GE, LESSGREATER);
        precedences.put(Token.TokenType.DOT, INDEX);
    }

    public Parser(Lexer l) {
        this.l = l;
        nextToken();
        nextToken();

        prefixParseFns = new HashMap<>();
        registerPrefix(Token.TokenType.IDENT, this::parseIdentifier);
        registerPrefix(Token.TokenType.INT, this::parseIntegerLiteral);

        registerPrefix(Token.TokenType.BANG, this::parsePrefixExpression);
        registerPrefix(Token.TokenType.MINUS, this::parsePrefixExpression);


        infixParseFns = new HashMap<>();
        registerInfix(Token.TokenType.PLUS, this::parseInfixExpression);
        registerInfix(Token.TokenType.MINUS, this::parseInfixExpression);
        registerInfix(Token.TokenType.SLASH, this::parseInfixExpression);
        registerInfix(Token.TokenType.ASTERISK, this::parseInfixExpression);
        registerInfix(Token.TokenType.EQ, this::parseInfixExpression);
        registerInfix(Token.TokenType.NOT_EQ, this::parseInfixExpression);
        registerInfix(Token.TokenType.LT, this::parseInfixExpression);
        registerInfix(Token.TokenType.GT, this::parseInfixExpression);
        registerInfix(Token.TokenType.LPAREN, this::parseCallExpression);
        registerInfix(Token.TokenType.LBRACKET, this::parseIndexExpression);
        registerInfix(Token.TokenType.ASSIGN, this::parseAssignmentExpression);
        registerInfix(Token.TokenType.LE, this::parseInfixExpression);
        registerInfix(Token.TokenType.GE, this::parseInfixExpression);
        registerInfix(Token.TokenType.DOT, this::parseMemberExpression);

        registerPrefix(Token.TokenType.IDENT, this::parseIdentifier);
        registerPrefix(Token.TokenType.TRUE, this::parseBoolean);
        registerPrefix(Token.TokenType.FALSE, this::parseBoolean);
        registerPrefix(Token.TokenType.LPAREN, this::parseGroupedExpression);
        registerPrefix(Token.TokenType.IF, this::parseIfExpression);
        registerPrefix(Token.TokenType.WHILE, this::parseWhileExpression);
        registerPrefix(Token.TokenType.FOR, this::parseForExpression);
        registerPrefix(Token.TokenType.FUNCTION, this::parseFunctionLiteral);
        registerPrefix(Token.TokenType.STRING, this::parseStringLiteral);
        registerPrefix(Token.TokenType.LBRACKET, this::parseArrayLiteral);
        registerPrefix(Token.TokenType.LBRACE, this::parseHashLiteral);
        registerPrefix(Token.TokenType.NEW, this::parseNewInstance);
        registerPrefix(Token.TokenType.THIS, this::parseThisExpression);



    }

    private void nextToken() {
        curToken = peekToken;
        peekToken = l.nextToken();
    }

    private Expression parsePrefixExpression() {
        Token token = curToken;
        String operator = token.getLiteral();

        nextToken(); // Move past the prefix operator

        Expression right = parseExpression(PREFIX);

        return new PrefixExpression(token, operator, right);
    }


    public Program parseProgram() {
        List<Statement> statements = new ArrayList<>();
        while (!curTokenIs(Token.TokenType.EOF)) {
            Statement statement = null;
            if (curToken.getType() == Token.TokenType.LET) {
                statement = parseLetStatement();
            } else if (curToken.getType() == Token.TokenType.RETURN) {
                statement = parseReturnStatement();
            }else if (curToken.getType() == Token.TokenType.CLASS) {
                statement = parseClassDeclaration();
            }

            /*else if (curToken.getType() == Token.TokenType.IF) {
                statement = parseIfStatement();
            }//*/
            else {
                statement =parseStatement();
            }
            if (statement != null) {
                statements.add(statement);
            }
            nextToken();
        }
        return new Program(statements);
    }

    private LetStatement parseLetStatement() {
        LetStatement stmt = new LetStatement(curToken, null, null);

        // Expect identifier after LET
        if (!expectPeek(Token.TokenType.IDENT)) {
            return null;
        }

        Identifier name = new Identifier(curToken, curToken.getLiteral());
        stmt.setName(name);

        // Expect ASSIGN (=) after identifier
        if (!expectPeek(Token.TokenType.ASSIGN)) {
            return null;
        }

        // Parse expression
        nextToken();
        stmt.setValue(parseExpression(LOWEST));

        // Skip until semicolon if needed
        while (!curTokenIs(Token.TokenType.SEMICOLON)) {
            nextToken();
        }

        return stmt;
    }

    public ReturnStatement parseReturnStatement() {
        ReturnStatement stmt = new ReturnStatement(curToken,null);
        nextToken();
        stmt.setReturnValue(parseExpression(LOWEST));
        if (peekTokenIs(Token.TokenType.SEMICOLON)) {
            nextToken();
        }
        return stmt;
    }

    private Expression parseIntegerLiteral() {
        try {
            int value = Integer.parseInt(curToken.getLiteral());
            return new IntegerLiteral(curToken, value);
        } catch (NumberFormatException e) {
            String msg = String.format("could not parse '%s' as integer", curToken.getLiteral());
            errors.add(msg);
            return null;
        }
    }

/*private Expression parseIfExpression() {
    IfExpression expression = new IfExpression(curToken, null, null, null);

    // Expect '(' after 'if'
    if (!expectPeek(Token.TokenType.LPAREN)) {
        return null;
    }

    nextToken();
    expression.setCondition(parseExpression(LOWEST));

    // Expect ')' after condition
    if (!expectPeek(Token.TokenType.RPAREN)) {
        return null;
    }

    // Expect '{' before consequence
    if (!expectPeek(Token.TokenType.LBRACE)) {
        return null;
    }

    expression.setConsequence(parseBlockStatement());

    // Handle optional 'else' clause
    if (peekTokenIs(Token.TokenType.ELSE)) {
        nextToken(); // Consume 'else'
        if (!expectPeek(Token.TokenType.LBRACE)) { // Fixed: expect LBRACE, error if not present
            return null;
        }
        expression.setAlternative(parseBlockStatement());
    }

    return expression;
}//*/

    private Expression parseIfExpression() {
        //IfExpression expression = new IfExpression(curToken, null, null, null);
        Expression expression = null;
        Token tokenExpression =curToken;
        // Expect '(' after 'if'
        if (!expectPeek(Token.TokenType.LPAREN)) {
            return null;
        }

        nextToken();
        Expression condition =parseExpression(LOWEST);

        // Expect ')' after condition
        if (!expectPeek(Token.TokenType.RPAREN)) {
            return null;
        }

        // Expect '{' before consequence
        if (!expectPeek(Token.TokenType.LBRACE)) {
            return null;
        }

        BlockStatement consequence =parseBlockStatement();
        //System.out.println("data: "+condition.toString()+" "+consequence);
        // Handle optional 'else' clause
        if (peekTokenIs(Token.TokenType.ELSE)) {
            nextToken(); // Consume 'else'
            if (peekTokenIs(Token.TokenType.IF)){//if(9==0){ 1; } else if(7==0) { 0;} else if(8==6){"hi"} else if (9==9){"here"} else{7} ;
                nextToken();
                Expression nestExpression = parseIfExpression();
                expression= new IfElseIfExpression(tokenExpression, condition, consequence, nestExpression);
            }
            else if (!expectPeek(Token.TokenType.LBRACE)) { // Fixed: expect LBRACE, error if not present
                return null;
            }else {
                expression = new IfExpression(curToken, condition, consequence, parseBlockStatement());

            }
        }

        return expression==null?new IfExpression(tokenExpression, condition, consequence, null):expression;
    }

    private Expression parseWhileExpression() {
        Token token = curToken;

        if (!expectPeek(Token.TokenType.LPAREN)) {
            return null;
        }

        nextToken(); // Move past '('
        Expression condition = parseExpression(LOWEST);

        if (!expectPeek(Token.TokenType.RPAREN)) {
            return null;
        }

        if (!expectPeek(Token.TokenType.LBRACE)) {
            return null;
        }

        BlockStatement body = parseBlockStatement();

        return new WhileExpression(token, condition, body);
    }

    private Expression parseForExpression() {
        Token token = curToken; // 'for' token

        if (!expectPeek(Token.TokenType.IDENT)) {
            errors.add("expected identifier after 'for'");
            return null;
        }
        Identifier loopVar = parseIdentifier();

        // Expect 'in' keyword
        if (!expectPeek(Token.TokenType.IN)) {
            errors.add("expected 'in' after loop variable");
            return null;
        }

        // Parse the iterable expression (array or range call)
        nextToken(); // Move past 'in'
        Expression iterable = parseExpression(LOWEST);

        // Parse loop body block
        if (!expectPeek(Token.TokenType.LBRACE)) {
            errors.add("expected '{' after for expression");
            return null;
        }
        BlockStatement body = parseBlockStatement();

        //System.out.println((new ForExpression(token, loopVar, iterable, body).toString()));
        return new ForExpression(token, loopVar, iterable, body);
    }

 private BlockStatement parseBlockStatement() {
     BlockStatement block = new BlockStatement(curToken, null);
     List<Statement> statements = new ArrayList<>();
     nextToken(); // Move past '{'

     while (!curTokenIs(Token.TokenType.RBRACE) && !curTokenIs(Token.TokenType.EOF)) {
         Statement stmt = parseStatement();
         if (stmt != null) {
             statements.add(stmt);
         }
         nextToken();
     }

     // Ensure we consume the closing brace
     if (!curTokenIs(Token.TokenType.RBRACE)) {
         errors.add("expected '}' to close block statement");
         return null;
     }
     block.setStatements(statements);
     return block;
    }//*/

    private FunctionLiteral parseFunctionLiteral() {
        FunctionLiteral function = new FunctionLiteral(curToken,null,null);

        // Expect '(' after 'fn'
        if (!expectPeek(Token.TokenType.LPAREN)) {
            return null;
        }

        // Parse function parameters
        List<Identifier> parameters = parseFunctionParameters();
        function.setParameters(parameters);

        // Expect '{' before body
        if (!expectPeek(Token.TokenType.LBRACE)) {
            return null;
        }

        // Parse function body
        function.setBody(parseBlockStatement());

        return function;
    }

    private List<Identifier> parseFunctionParameters() {
        List<Identifier> identifiers = new ArrayList<>();
        if (peekTokenIs(Token.TokenType.RPAREN)) {
            nextToken(); // Move past ')'
            return identifiers;
        }
        // Parse the first identifier
        nextToken(); // Move to the first identifier
        if (!curTokenIs(Token.TokenType.IDENT)) {
            peekError(Token.TokenType.IDENT);
            return null;
        }
        Identifier ident = new Identifier(curToken, curToken.getLiteral());
        identifiers.add(ident);
        // Parse subsequent identifiers
        while (peekTokenIs(Token.TokenType.COMMA)) {
            nextToken(); // Move past ','
            nextToken(); // Move to the next identifier
            if (!curTokenIs(Token.TokenType.IDENT)) {
                peekError(Token.TokenType.IDENT);
                return null;
            }
            ident = new Identifier(curToken, curToken.getLiteral());
            identifiers.add(ident);
        }
        // Check for closing parenthesis
        if (!expectPeek(Token.TokenType.RPAREN)) {
            return null;
        }
        return identifiers;
    }



    private Expression parseCallExpression(Expression function) {
        CallExpression exp = new CallExpression(curToken, null, null);
        exp.setFunction(function);
        exp.setArguments(parseCallArguments());
        return exp;
    }

    private List<Expression> parseCallArguments() {
        List<Expression> args = new ArrayList<>();

        if (peekTokenIs(Token.TokenType.RPAREN)) {
            nextToken(); // Consume ')'
            return args;
        }

        nextToken(); // Move past '('
        args.add(parseExpression(LOWEST));

        while (peekTokenIs(Token.TokenType.COMMA)) {
            nextToken(); // Move past comma
            nextToken(); // Move to next expression
            args.add(parseExpression(LOWEST));
        }

        if (!expectPeek(Token.TokenType.RPAREN)) {
            return null; // Error recovery
        }

        return args;
    }

    private Expression parseStringLiteral() {
        return new StringLiteral(curToken, curToken.getLiteral());
    }

    private Statement parseStatement() {
        switch (curToken.getType()) {
            case LET:
                return parseLetStatement();
            case RETURN:
                return parseReturnStatement();
            case CONTINUE:  // New
                return parseContinueStatement();
            case BREAK:     // New
                return parseBreakStatement();
            default:
                return parseExpressionStatement();
        }
    }
    private Expression parseArrayLiteral() {
        Token token = curToken;
        List<Expression> elements = parseExpressionList(Token.TokenType.RBRACKET);
        return new ArrayLiteral(token, elements);
    }

    private List<Expression> parseExpressionList(Token.TokenType end) {
        List<Expression> expressions = new ArrayList<>();

        if (peekTokenIs(end)) {
            nextToken();
            return expressions;
        }

        nextToken();
        expressions.add(parseExpression(LOWEST));

        while (peekTokenIs(Token.TokenType.COMMA)) {
            nextToken();
            nextToken();
            expressions.add(parseExpression(LOWEST));
        }

        if (!expectPeek(end)) {
            return Collections.emptyList();
        }

        return expressions;
    }


    /*private Expression parseIndexExpression(Expression left) {
        Token token = curToken;
        nextToken(); // Move past [

        Expression index = parseExpression(LOWEST);

        if (!expectPeek(Token.TokenType.RBRACKET)) {
            return null;
        }

        return new IndexExpression(token, left, index);
    }*/

    private Expression parseIndexExpression(Expression left) {
        IndexExpression exp = new IndexExpression(curToken, left,null);

        nextToken(); // Move past [
        exp.setIndex(parseExpression(LOWEST));

        if (!expectPeek(Token.TokenType.RBRACKET)) {
            return null;
        }

        return exp;
    }
    private Expression parseAssignmentExpression(Expression left) {
        Token token = curToken;

        // Allow member expressions (this.x) and identifiers
        if (!(left instanceof Identifier) && !(left instanceof MemberExpression)) {
            errors.add("left-hand side of assignment must be identifier or property access");
            return null;
        }

        int precedence = curPrecedence();
        nextToken(); // Move past '='
        Expression value = parseExpression(precedence);

        if (left instanceof MemberExpression) {
            MemberExpression member = (MemberExpression) left;
            return new PropertyAssignmentExpression(
                    token,
                    member.getObject(),
                    member.getProperty(),
                    value
            );
        }

        return new AssignmentExpression(token, (Identifier) left, value);
    }

    private Identifier parseIdentifier() {
        return new Identifier(curToken, curToken.getLiteral());
    }

    private Expression parseExpression() {
        if (curToken.getType() == Token.TokenType.INT) {
            nextToken(); // advance to operator or semicolon
            if (curToken.getType() == Token.TokenType.PLUS) {
                return parseOperatorExpression();
            } else if (curToken.getType() == Token.TokenType.SEMICOLON) {
                return parseIntegerLiteral();
            }
        } else if (curToken.getType() == Token.TokenType.LPAREN) {
            return parseGroupedExpression();
        }
        // handle other cases or return error
        return null;
    }

    private Expression parseExpression(int precedence) {//5+8
        PrefixParseFn prefix = prefixParseFns.get(curToken.getType());//5
        if (prefix == null) {
            noPrefixParseFnError(curToken.getType());
            return null;
        }
        Expression leftExp = prefix.parse();//converted to int
        // 3. Process infix operators with higher precedence
        while (!peekTokenIs(Token.TokenType.SEMICOLON)
                && precedence < peekPrecedence()) {//low < + < )

            // 4. Get infix parser for next token
            InfixParseFn infix = infixParseFns.get(peekToken.getType());//+
            if (infix == null) {
                return leftExp;
            }

            // 5. Advance to operator token
            nextToken();//cur = +

            // 6. Parse right-hand side with current precedence
            leftExp = infix.parse(leftExp);//5 cur->+
        }

        return leftExp;
    }

    private Expression parseInfixExpression(Expression left) {
        Token token = curToken;
        String operator = token.getLiteral();//+

        // Get current operator precedence
        int precedence = curPrecedence();

        // Advance to next token before parsing right side
        nextToken();//8

        // Parse right-hand side with current precedence
        Expression right = parseExpression(precedence);//8)

        return new InfixExpression(token, left, operator, right);//8 * 2
    }


    private Expression parseOperatorExpression() {
        Expression left = parseIntegerLiteral();
        Token operator = curToken;
        nextToken(); // advance to right operand
        Expression right = parseExpression();
        return new OperatorExpression(left, operator, right);
    }




    private Expression parseGroupedExpression() {
        nextToken(); // Move past LPAREN
        Expression exp = parseExpression(LOWEST);

        if (!expectPeek(Token.TokenType.RPAREN)) {
            return null; // Missing closing parenthesis
        }

        return exp;
    }

    private Expression parseHashLiteral() {
        HashLiteral hash = new HashLiteral(curToken, new LinkedHashMap<>());

        while (!peekTokenIs(Token.TokenType.RBRACE)) {
            nextToken(); // Move past '{' or comma
            Expression key = parseExpression(LOWEST);

            if (!expectPeek(Token.TokenType.COLON)) {
                return null;
            }

            nextToken(); // Move past colon
            Expression value = parseExpression(LOWEST);
            hash.getPairs().put(key, value);

            if (!peekTokenIs(Token.TokenType.RBRACE) && !expectPeek(Token.TokenType.COMMA)) {
                return null;
            }
        }

        if (!expectPeek(Token.TokenType.RBRACE)) {
            return null;
        }

        return hash;
    }

    private Statement parseClassDeclaration() {
        Token token = curToken; // 'class' token
        nextToken(); // Move past 'class'

        // Parse class name
        if (!curTokenIs(Token.TokenType.IDENT)) {
            errors.add("Expected class name after 'class'");
            return null;
        }
        Identifier className = parseIdentifier();

        Identifier superClass = null;
        if (peekTokenIs(Token.TokenType.LT)) {  // Handle < operator
            nextToken(); // Move past <
            if (!expectPeek(Token.TokenType.IDENT)) {
                errors.add("Expected superclass name after <");
                return null;
            }
            superClass = parseIdentifier();
        }
        // Parse class body
        if (!expectPeek(Token.TokenType.LBRACE)) {
            return null;
        }

        List<ClassMember> members = new ArrayList<>();
        while (!peekTokenIs(Token.TokenType.RBRACE) && !peekTokenIs(Token.TokenType.EOF)) {
            nextToken(); // Move to member declaration

            if (curTokenIs(Token.TokenType.PRIVATE)) {
                // Handle private field declaration
                nextToken(); // Move past 'private'
                if (!curTokenIs(Token.TokenType.LET)) {
                    errors.add("Expected 'let' after 'private'");
                    return null;
                }
                FieldDeclaration field = parseFieldDeclaration();
                field.setPrivate(true);
                members.add(field);
            } else if (curTokenIs(Token.TokenType.LET)) {
                members.add(parseFieldDeclaration());
            } else if (curTokenIs(Token.TokenType.IDENT)) {
                members.add(parseMethodDeclaration());
            }
        }

        if (!expectPeek(Token.TokenType.RBRACE)) {
            return null;
        }
        nextToken();
        if (peekTokenIs(Token.TokenType.SEMICOLON)) {
            nextToken();
        }
        return new ClassDeclaration(token, className,superClass, members);
    }

    private FieldDeclaration parseFieldDeclaration() {
        Token letToken = curToken;
        nextToken(); // Move past 'let'

        // Parse field name
        Identifier name = parseIdentifier();

        // Parse optional initialization
        Expression initializer = null;
        if (peekTokenIs(Token.TokenType.ASSIGN)) {
            nextToken(); // Move past '='
            nextToken(); // Move to initializer
            initializer = parseExpression(LOWEST);
        }

        if (!expectPeek(Token.TokenType.SEMICOLON)) {
            return null;
        }

        return new FieldDeclaration(letToken, name, initializer);
    }

    private MethodDeclaration parseMethodDeclaration() {
        Token token = curToken; // Method name token

        // Parse method name
        Identifier name = parseIdentifier();

        // Parse parameters
        if (!expectPeek(Token.TokenType.LPAREN)) {
            errors.add("expected '(' after method name");
            return null;
        }
        List<Identifier> parameters = parseFunctionParameters();

        // Parse method body
        if (!expectPeek(Token.TokenType.LBRACE)) {
            errors.add("expected '{' after method parameters");
            return null;
        }
        BlockStatement body = parseBlockStatement();

        return new MethodDeclaration(token, name, parameters, body);
    }

    private Expression parseNewInstance() {
        Token token = curToken; // 'new' token
        nextToken(); // Move past 'new'

        if (!curTokenIs(Token.TokenType.IDENT)) {
            errors.add("Expected class name after 'new'");
            return null;
        }
        Identifier className = parseIdentifier();

        // Parse arguments
        if (!expectPeek(Token.TokenType.LPAREN)) return null;
        List<Expression> args = parseCallArguments();

        return new NewInstanceExpression(token, className, args);
    }

    private Expression parseThisExpression() {
        return new ThisExpression(curToken);
    }

    private Expression parseMemberExpression(Expression left) {
        Token token = curToken; // The DOT token
        nextToken(); // Move past .

        if (!curTokenIs(Token.TokenType.IDENT)) {
            errors.add("Expected property name after '.'");
            return null;
        }

        Identifier property = parseIdentifier();
        return new MemberExpression(token, left, property);
    }

    private void parseError(String message) {
        System.out.println("Parse error: " + message);
    }


    private boolean curTokenIs(Token.TokenType t) {
        return curToken.getType() == t;
    }

    private boolean peekTokenIs(Token.TokenType t) {
        return peekToken.getType() == t;
    }

    private boolean expectPeek(Token.TokenType t) {
        if (peekTokenIs(t)) {
            nextToken();
            return true;
        } else {
            peekError(t);
            return false;
        }
    }

    public List<String> getErrors() {
        return Collections.unmodifiableList(errors);
    }

    public void peekError(Token.TokenType t) {
        String msg = String.format("expected next token to be %s, got %s instead",
                t.getLiteral(), peekToken.getType());
        errors.add(msg);
    }


    @FunctionalInterface
    public interface PrefixParseFn {
        Expression parse();
    }

    @FunctionalInterface
    public interface InfixParseFn {
        Expression parse(Expression expression);
    }

    private int peekPrecedence() {
        return precedences.getOrDefault(peekToken.getType(), LOWEST);
    }


    private int curPrecedence() {
        return precedences.getOrDefault(curToken.getType(), LOWEST);
    }


    private void registerPrefix(Token.TokenType type, PrefixParseFn fn) {
        prefixParseFns.put(type, fn);
    }

    // Example registration of infix parse function
    private void registerInfix(Token.TokenType type, InfixParseFn fn) {
        infixParseFns.put(type, fn);
    }



    private ExpressionStatement parseExpressionStatement() {
        ExpressionStatement stmt = new ExpressionStatement(curToken, null);
        stmt.setExpression(parseExpression(LOWEST));

        if (peekTokenIs(Token.TokenType.SEMICOLON)) {
            nextToken();
        }
        return stmt;
    }

    private Statement parseContinueStatement() {
        Token token = curToken;
        nextToken();
        if (peekTokenIs(Token.TokenType.SEMICOLON)) {
            nextToken();
        }
        return new ContinueStatement(token);
    }

    private Statement parseBreakStatement() {
        Token token = curToken;
        nextToken();
        if (peekTokenIs(Token.TokenType.SEMICOLON)) {
            nextToken();
        }
        return new BreakStatement(token);
    }

    private void noPrefixParseFnError(Token.TokenType type) {
        String msg = String.format("no prefix parse function for %s found", type);
        errors.add(msg);
    }
    private Expression parseBoolean() {
        return new BooleanLiteral(curToken, curTokenIs(Token.TokenType.TRUE));
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

}