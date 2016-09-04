import java.io.*;
import java.util.LinkedHashSet;

public class TokenDumper {
    public static void main(String[] args) throws IOException {
        Lexer lexer = new Lexer(new FileReader(args[0]));

        while(true) {
            // print token
            Token t = lexer.yylex();
            TokenCode code = t.getTokenCode();
            System.out.print(code.toString());

            // print symbol table entry
            Object entry;
            OpType op;
            if ((entry = t.getSymbolTableEntry()) != null) {
                System.out.print("(" + entry + ")"); 
            } else if ((op = t.getOperation()) != null) {
                System.out.print("(" + op + ")"); 
            }
            System.out.print(" ");

            if (code == TokenCode.EOF) {
                System.out.println("\n");
                break;
            }
        }

        // print out the symbol table
        LinkedHashSet<Object> mySet = lexer.sym.getSymbolTableEntries();
        int cnt = 0;
        for (Object key : mySet) {
            System.out.println(cnt++ + "  " + key);
        }
    }
}
