package org.example.repl;

import org.example.ast.LetStatement;
import org.example.ast.Program;
import org.example.evaluator.Evaluator;
import org.example.lexer.Lexer;
import org.example.object.Environment;
import org.example.object.NullObject;
import org.example.parser.Parser;
import org.example.token.Token;

import java.io.InputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.Scanner;


public class Repl {
    private static final String MONKEY_FACE = """
            .--.  .-"     "-.  .--.
           / .. \\/  .-. .-.  \\/ .. \\
          | |  '|  /   Y   \\  |'  | |
          | \\   \\\\  \\ 0 | 0 /  //   / |
           \\ '- ,\\.-\"\"\"\"\"\"\"-./, -' /
            ''-' /_   ^ ^   _\\ '-''
                |  \\._   _./  |
                \\   \\ '~' /   /
                 '._ '-=-' _.'
                    '-----'
            """;
    private static final String PROMPT = ">> ";
    private static final String CONTINUATION_PROMPT = "... ";
    Environment env = new Environment();
    public void start(InputStream in, PrintWriter out) {
        Scanner scanner = new Scanner(in);
        StringBuilder inputBuffer = new StringBuilder(); // Accumulate lines

        while (true) {
            // Use continuation prompt if input is incomplete
            String currentPrompt = inputBuffer.isEmpty() ? PROMPT : CONTINUATION_PROMPT;
            out.print(currentPrompt);
            out.flush();

            if (!scanner.hasNextLine()) return;
            String line = scanner.nextLine();

            // Add line to buffer
            inputBuffer.append(line).append("\n"); // Preserve line breaks

            // Check if the input is complete
            if (isInputComplete(inputBuffer.toString())) {
                processInput(inputBuffer.toString(), out);
                inputBuffer.setLength(0); // Reset buffer
            }
        }
    }

    private boolean isInputComplete(String input) {
        int openBraces = 0, openBrackets = 0, openParens = 0;

        for (char c : input.toCharArray()) {
            switch (c) {
                case '{' -> openBraces++;
                case '}' -> openBraces--;
                case '[' -> openBrackets++;
                case ']' -> openBrackets--;
                case '(' -> openParens++;
                case ')' -> openParens--;
            }
        }
        // Input is complete when all brackets are balanced
        return openBraces == 0 && openBrackets == 0 && openParens == 0 && input.trim().endsWith(";");
    }

    private void processInput(String input, PrintWriter out) {
        Lexer lexer = new Lexer(input);
        Parser parser = new Parser(lexer);
        Program program = parser.parseProgram();

        // Handle errors
        if (!parser.getErrors().isEmpty()) {
            printParserErrors(out, parser.getErrors());
            return;
        }

        // Evaluate and print
        Object evaluated = Evaluator.eval(program, env);
        if (evaluated != NullObject.NULL) {
            out.println(((org.example.object.Object) evaluated).inspect());
        }
    }

    private void printParserErrors(PrintWriter out, List<String> errors) {
        out.println(MONKEY_FACE);
        out.println("Woops! We ran into some monkey business here!");
        out.println(" parser errors:");
        errors.forEach(error -> out.printf("\t%s%n", error));
        out.flush();
    }


}//let x =fn (x,y){x+y};