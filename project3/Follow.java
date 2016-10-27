import java.util.ArrayList;
import java.util.Arrays;

public class Follow {
  public static ArrayList<TokenCode> MethodReturnType = new ArrayList<TokenCode> (Arrays.asList(
               TokenCode.IDENTIFIER)),
         Variable = new ArrayList<TokenCode> (Arrays.asList(TokenCode.COMMA, TokenCode.SEMICOLON)),
         ParameterList = new ArrayList<TokenCode> (Arrays.asList(TokenCode.RPAREN,
               TokenCode.COMMA)),
         Expression = new ArrayList<TokenCode> (Arrays.asList(TokenCode.RPAREN, TokenCode.RBRACKET,
               TokenCode.SEMICOLON, TokenCode.COMMA)),
         MethodDeclarations = new ArrayList<TokenCode>(Arrays.asList(TokenCode.RBRACE,
               TokenCode.STATIC)),
         NoneSuchType = new ArrayList<TokenCode>(),
         ParenthesizedExpressionList = new ArrayList<TokenCode> (Arrays.asList(TokenCode.MULOP,
               TokenCode.ADDOP, TokenCode.RELOP, TokenCode.RPAREN, TokenCode.SEMICOLON, 
               TokenCode.COMMA, TokenCode.RBRACKET, TokenCode.IDENTIFIER, TokenCode.NUMBER,
               TokenCode.LPAREN, TokenCode.NOT)),
         VariableLoc2 = new ArrayList<TokenCode> (Arrays.asList(TokenCode.ASSIGNOP, 
               TokenCode.INCDECOP)),
         ParenthesizedExpression = new ArrayList<TokenCode> (Arrays.asList(TokenCode.LBRACE)),
         Parameters = new ArrayList<TokenCode> (Arrays.asList(TokenCode.RPAREN)),
         VariableList = new ArrayList<TokenCode> (Arrays.asList(TokenCode.SEMICOLON)),
         Statement = new ArrayList<TokenCode> (Arrays.asList(TokenCode.IF, TokenCode.FOR, 
               TokenCode.LBRACE, TokenCode.IDENTIFIER, TokenCode.RETURN, TokenCode.BREAK, 
               TokenCode.CONTINUE, TokenCode.RBRACE)),
         StatementBlock = new ArrayList<TokenCode>(Arrays.asList(TokenCode.ELSE)),
         VariableDeclaration = new ArrayList<TokenCode> (Arrays.asList(TokenCode.STATIC));

  static {
    NoneSuchType.addAll(Validation.Types);
    NoneSuchType.addAll(MethodDeclarations);
    VariableLoc2.addAll(ParenthesizedExpressionList);
    ParenthesizedExpression.addAll(ParenthesizedExpressionList);
    StatementBlock.addAll(Statement);
    VariableDeclaration.addAll(Statement);
  }
}
