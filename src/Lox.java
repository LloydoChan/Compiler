package CraftingInterpreters;

import java.io.BufferedReader;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import CraftingInterpreters.Scanner;
import CraftingInterpreters.Token;
import CraftingInterpreters.TokenType;
import CraftingInterpreters.AstPrinter;
import CraftingInterpreters.Parser;
import CraftingInterpreters.Expr;

public class Lox {

    static boolean hadError = false;

    public static void main(String[] args) throws IOException {
        if(args.length > 1) {
            System.out.println("Usage: jlox [script]");
        } else if (args.length == 1) {
            runFile(args[0]);
        }else{
            runPrompt();
        }
    }

    private static void runPrompt() throws IOException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        for(;;){
            System.out.print("> ");
            run(reader.readLine());
            hadError = false;
        }
    }

    private static void runFile(String path) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        run(new String(bytes, Charset.defaultCharset()));
        if (hadError) System.exit(65);
    }

    private static void run(String readLine) {
        Scanner scanner = new Scanner(readLine);
        List<Token> tokens = scanner.scanTokens();
        Parser parser = new Parser(tokens);
        Expr expression = parser.parse();

        if(hadError) return;

        System.out.println(new AstPrinter().print(expression));
    }

    private static void report(int line, String where, String message) {
        System.err.println(
                "[line " + line + "] Error" + where + ": " + message
        );
        hadError = true;
    }

    static void error(int line, String message){
        report(line, "", message);
    }

    static void error(Token token, String message){
        if(token.type == TokenType.EOF){
            report(token.line, "at end", message);
        }else{
            report(token.line, " at '" + token.lexeme + "'", message);
        }
    }
}
