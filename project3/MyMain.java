import java.io.*;

public class MyMain {
  public static void main(String[] args) throws IOException {
    Parser p = new Parser(new Lexer(new FileReader(args[0])), args[0]);
    p.parse();
  }
}
