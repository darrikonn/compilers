import java.util.ArrayList;
import java.util.Arrays;

public class Validation {
  public static ArrayList<TokenCode> Expression = new ArrayList<TokenCode> (Arrays.asList(
               TokenCode.IF, TokenCode.ADDOP, TokenCode.NOT, TokenCode.LPAREN, 
               TokenCode.NUMBER, TokenCode.IDENTIFIER)),
         StatementList = new ArrayList<TokenCode> (Arrays.asList(TokenCode.IF, TokenCode.FOR, 
               TokenCode.LBRACE, TokenCode.IDENTIFIER, TokenCode.RETURN, TokenCode.BREAK, 
               TokenCode.CONTINUE)),
         Types = new ArrayList<TokenCode> (Arrays.asList(TokenCode.INT, TokenCode.REAL));
}
