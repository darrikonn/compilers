import java.util.ArrayList;
import java.util.Arrays;

public class Firsts {
    public static ArrayList<TokenCode> Expression = new ArrayList<TokenCode>(Arrays.asList(
                TokenCode.NOT, TokenCode.LPAREN, TokenCode.NUMBER, TokenCode.IDENTIFIER)), 
           VariableLoc2 = new ArrayList<TokenCode> (Arrays.asList(TokenCode.LBRACKET,
                       TokenCode.ASSIGNOP, TokenCode.INCDECOP)),
           ParameterList2 = new ArrayList<TokenCode> (Arrays.asList(TokenCode.RPAREN, 
                       TokenCode.COMMA)),
           VariableDeclarationsStatementList = new ArrayList<TokenCode> (Arrays.asList(TokenCode.IF,
                       TokenCode.FOR, TokenCode.LBRACE, TokenCode.IDENTIFIER, TokenCode.RETURN, 
                       TokenCode.BREAK, TokenCode.CONTINUE, TokenCode.RBRACE)),
           StatementBlock = new ArrayList<TokenCode> (Arrays.asList(TokenCode.LBRACE)),
           Parameters = new ArrayList<TokenCode> (Arrays.asList(TokenCode.RPAREN)),
           VariableMethodDeclarations = new ArrayList<TokenCode> (Arrays.asList(TokenCode.STATIC, 
                       TokenCode.IDENTIFIER, TokenCode.RBRACE));

    public static ArrayList<OpType> OpExpression = new ArrayList<OpType> (Arrays.asList(
                OpType.MINUS, OpType.PLUS));

    static {
        Parameters.addAll(Validations.Types);
        VariableMethodDeclarations.addAll(Validations.Types);
    }
}
