import java.io.*;
import java.util.ArrayList;

public class TokenDumper {
  public static void main(String[] args) throws IOException {
    Lexer lexer = new Lexer(new FileReader(args[0]));

    while(true) {
      // print token
      Token t = lexer.yylex();
      TokenCode code = t.getTokenCode();
      System.out.print(code.toString());

      // print symbol table entry or operation
      SymbolTableEntry entry;
      OpType op;
      if ((entry = t.getSymbolTableEntry()) != null) {
        System.out.print("(" + entry.getLexeme() + ")"); 
      } else if ((op = t.getOpType()) != OpType.NONE && 
          code != TokenCode.ASSIGNOP) {
        System.out.print("(" + op + ")"); 
          }

      if (code == TokenCode.EOF) {
        System.out.println("\n");
        break;
      }
      System.out.print(" ");
    }

    // print out the symbol table
    ArrayList<SymbolTableEntry> entries = lexer.sym.getSymbolTableEntries();
    int cnt = 0;
    for (SymbolTableEntry entry : entries) {
      System.out.println(cnt++ + "  " + entry.getLexeme());
    }
  }
}
