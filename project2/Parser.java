import java.io.*;
import java.util.ArrayList;

public class Parser {
    public static void main(String[] args) throws IOException {
        Lexer lexer = new Lexer(new FileReader(args[0]));

        while(true) {
            Token t = lexer.yylex();
            TokenCode code = t.getTokenCode();

            System.out.println(" " + t.getLineNumber() + " : " + t);

            if (code == TokenCode.EOF) {
                System.out.println("\n");
                break;
            }
        }

    }
}
