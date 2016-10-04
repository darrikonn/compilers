import java.io.*;

public class MyMain {
  public static void main(String [] args) throws IOException {
    Lexer lexer = new Lexer(new FileReader(args[0]));
    boolean first = true;
    while(true) {
      if (first)
        first = false;
      else
        System.out.print(" ");
      Token t = lexer.yylex();
      System.out.print(t.getTokenCode().toString());
      if (t.getTokenCode() == TokenCode.RELOP || 
          t.getTokenCode() == TokenCode.ADDOP ||
          t.getTokenCode() == TokenCode.MULOP ||
          t.getTokenCode() == TokenCode.INCDECOP)
        System.out.print("(" + t.getOpType().toString() + ")");
      else if (t.getTokenCode() == TokenCode.IDENTIFIER || 
               t.getTokenCode() == TokenCode.NUMBER)
        System.out.print("(" + t.getSymTabEntry().getLexeme() + ")");
      if (t != null && t.getTokenCode() == TokenCode.EOF)
        break;
    }
    System.out.println();
    System.out.println();
    for(int n=0;n<SymbolTable.size();n++) {
      System.out.print(n);
      System.out.print("  ");
      System.out.println(SymbolTable.getEntry(n).getLexeme());
    }
  }
}