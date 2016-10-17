import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import org.apache.commons.lang3.StringUtils;

public class Parser {
  private Lexer _lexer;
  private Token _token, _tmp;
  private String _prevText;
  private TokenCode _lookahead;
  private OpType _opType;

  private HashMap<Integer, ArrayList<ErrorObject>> _errorMap;
  private String _filename;

  public Parser(Lexer lexer, String filename) throws IOException {
    _filename = filename;
    _lexer = lexer;
    _token = lexer.yylex();
    _tmp = _token;
    _prevText = "";
    _lookahead = _token.getTokenCode();
    _opType = _token.getOpType();
    _errorMap = new HashMap<Integer, ArrayList<ErrorObject>>();
  }

  public static void main(String[] args) throws IOException {
    Parser p = new Parser(new Lexer(new FileReader(args[0])), args[0]);
    p.parse();
  }

  public void parse() {
    program();
    getErrors();
  }

  private boolean match(TokenCode code) {
    if (code == TokenCode.IDENTIFIER && _lookahead == TokenCode.ERR_LONG_ID) {
      setError(_token, "^ Identifier too long");
      readNextToken();
      return true;
    } else if (_lookahead == code) {
      readNextToken();
      return true;
    }

    String message = code == TokenCode.SEMICOLON 
      ? StringUtils.repeat(" ", _prevText.length()) + code.toString()
      : code.toString();
    setError(code == TokenCode.SEMICOLON ? _tmp : _token, message);
    return false;
  }

  private boolean match(OpType type) {
    if (_opType == type) {
      readNextToken();
      return true;
    }

    setError(_token, "^ Expected '" + type + "'");
    return false;
  }

  private void recoverError(ArrayList<TokenCode> followList) {
    while (!followList.contains(_lookahead) && _lookahead != TokenCode.EOF) {
      readNextToken();
    }
  }

  private void recoverExpression() {
    while (!Firsts.Expression.contains(_lookahead) && !Firsts.OpExpression.contains(_opType) && 
        _lookahead != TokenCode.EOF) {
      readNextToken();
    }
  }

  private void setError(Token token, String message) {
    int line = token.getLineNumber();
    if (!_errorMap.containsKey(line)) {
      _errorMap.put(line, new ArrayList<ErrorObject>());
    }
    _errorMap.get(line).add(new ErrorObject(token.getColumnNumber(), message));
  }

  private void getErrors() {
    if (_errorMap.size() == 0) {
      System.out.println("No errors");
    } else {
      try {
        int cnt = 0;
        BufferedReader br = new BufferedReader(new FileReader(_filename));
        String s = br.readLine();
        for (int i = 0; s != null; i++, s = br.readLine()) {
          if (_errorMap.containsKey(i)) {
            String code = s.trim();
            String leadString = StringUtils.leftPad(Integer.toString(i+1), 3, ' ') + " : " + code;
            ArrayList<ErrorObject> obj = _errorMap.get(i);
            for (ErrorObject err : obj) {
              cnt++;
              System.out.println(leadString);
              System.out.println(StringUtils.repeat(" ", err.Column+6-s.indexOf(code)) + err.Message);
            }
          }
        }
        System.out.println("Number of errors: " + cnt);
        br.close();
      } catch (Exception e) {
        e.printStackTrace(System.out);
        System.exit(-1);
      }
    }
  }

  private void readNextToken() {
    try {
      _tmp = _token;
      _prevText = _lexer.yytext();
      _token = _lexer.yylex();

      _lookahead = _token.getTokenCode();
      _opType = _token.getOpType();

      if (_lookahead == TokenCode.ERR_ILL_CHAR) {
        setError(_token, "^ Illegal character");
        _token = _lexer.yylex();
        _lookahead = _token.getTokenCode();
        _opType = _token.getOpType();
      }
    } catch (Exception e) {
      e.printStackTrace(System.out);
      System.exit(-1);
    }
  }

  /*
   * Grammar
   */
  private void program() {
    if (!match(TokenCode.CLASS) || !match(TokenCode.IDENTIFIER) || !match(TokenCode.LBRACE)) {
      recoverError(Firsts.VariableMethodDeclarations);
    }

    variableDeclarations(true);
    methodDeclarations();
    match(TokenCode.RBRACE);
  }

  private void variableDeclarations(boolean outSideOfScope) { // Epsilon
    if (isType()) {
      type();
      variableList();
      if (!match(TokenCode.SEMICOLON)) {
        recoverError(Firsts.VariableDeclarationsStatementList);
      }
      variableDeclarations(outSideOfScope);
    } else if (outSideOfScope && 
        (_lookahead == TokenCode.IDENTIFIER || _lookahead == TokenCode.ERR_LONG_ID)) {
      setError(_token, "^ None such type");
      recoverError(Follows.NoneSuchType);
      variableDeclarations(outSideOfScope);
    } else if (!Follows.VariableDeclaration.contains(_lookahead)) {
      setError(_token, "^ Expected a type");
      recoverError(Follows.VariableDeclaration);
    }
  }

  private void type() { // don't need error recovery because isType is always called
    if (_lookahead == TokenCode.INT) {
      match(TokenCode.INT);
    } else if (_lookahead == TokenCode.REAL) {
      match(TokenCode.REAL);
    }
  }

  private void variableList() {
    variable();
    variableList2();
  }

  private void variableList2() { // Epsilon
    if (_lookahead == TokenCode.COMMA) {
      match(TokenCode.COMMA);
      variableList();
    } else if (!Follows.VariableList.contains(_lookahead)) {
      setError(_token, "^ Expected a comma");
      recoverError(Follows.VariableList);
    }
  }

  private void variable() {
    if (_lookahead == TokenCode.IDENTIFIER || _lookahead == TokenCode.ERR_LONG_ID) {
      match(TokenCode.IDENTIFIER);
      variable2();
    } else {
      setError(_token, "^ Expected an identifier");
      recoverError(Follows.Variable);
    }
  }

  private void variable2() { // Epsilon
    if (_lookahead == TokenCode.LBRACKET) {
      if (!match(TokenCode.LBRACKET) || !match(TokenCode.NUMBER) || !match(TokenCode.RBRACKET)) {
        recoverError(Follows.Variable);
      }
    }
  }

  private void methodDeclarations() {
    if (_lookahead == TokenCode.STATIC) {
      methodDeclaration();
      moreMethodDeclarations();
    } else {
      setError(_token, "^ Expected 'static'");
      recoverError(Follows.MethodDeclarations);
    }
  }

  private void moreMethodDeclarations() { // Epsilon
    if (_lookahead == TokenCode.STATIC) {
      methodDeclarations();
    }
  }

  private void methodDeclaration() {
    match(TokenCode.STATIC); // always checked if static, no need for recovery
    methodReturnType();
    if (!match(TokenCode.IDENTIFIER) || !match(TokenCode.LPAREN)) {
      recoverError(Firsts.Parameters);
    }
    parameters();
    if (!match(TokenCode.RPAREN) || !match(TokenCode.LBRACE)) {
      recoverError(Firsts.VariableDeclarationsStatementList);
    }
    variableDeclarations(false);
    statementList();
    if (!match(TokenCode.RBRACE)) {
      recoverError(Follows.MethodDeclarations);
    }
  }

  private void methodReturnType() {
    if (isType()) {
      type();
    } else if (_lookahead == TokenCode.VOID) {
      match(TokenCode.VOID);
    } else {
      setError(_token, "^ Expected a method return type");
      recoverError(Follows.MethodReturnType);
    }
  }

  private void parameters() { // Epsilon
    if (_lookahead != TokenCode.RPAREN) {
      parameterList();
    }
  }

  private void parameterList() {
    if (isType()) {
      type();
      if (!match(TokenCode.IDENTIFIER)) {
        recoverError(Firsts.ParameterList2);
      }
    } else {
      setError(_token, "^ Expected a type");
      recoverError(Follows.ParameterList);
    }
    parameterList2();
  }

  private void parameterList2() { // Epsilon
    if (_lookahead == TokenCode.COMMA) {
      match(TokenCode.COMMA);
      parameterList();
    } else if (!Follows.Parameters.contains(_lookahead)) {
      setError(_token, "^ Expected a comma");
      recoverError(Follows.Parameters);
    }
  }

  private void statementList() { // Epsilon
    if (isStatementList()) {
      statement();
      statementList();
    }
  }

  private void statement() {
    switch (_lookahead) {
      case IF:
        match(TokenCode.IF);
        parenthesizedExpression();
        statementBlock();
        optionalElse();
        break;
      case FOR:
        if (!match(TokenCode.FOR) || !match(TokenCode.LPAREN)) {
          recoverError(Follows.MethodReturnType);
        }
        variableLoc();
        if (!match(TokenCode.ASSIGNOP)) {
          recoverExpression();
        }
        expression();
        if (!match(TokenCode.SEMICOLON)) {
          recoverExpression();
        }
        expression();
        if (!match(TokenCode.SEMICOLON)) {
          recoverError(Follows.MethodReturnType);
        }
        incrDecrVar();
        if (!match(TokenCode.RPAREN)) {
          recoverError(Firsts.StatementBlock);
        }
        statementBlock();
        break;
      case LBRACE:
        statementBlock();
        break;
      default:
        statement2();
        if (!match(TokenCode.SEMICOLON)) {
          recoverError(Follows.Statement);
        }
        break;
    }
  }

  private void statement2() {
    switch (_lookahead) {
      case IDENTIFIER:
      case ERR_LONG_ID:
        match(TokenCode.IDENTIFIER);
        if (_lookahead == TokenCode.IDENTIFIER) {
          setError(_tmp, "^ None such type");
          recoverError(Follows.VariableList);
        } else if (_lookahead == TokenCode.LPAREN) {
          parenthesizedExpressionList();
        } else {
          variableLoc2();
          if (_lookahead == TokenCode.ASSIGNOP) {
            match(TokenCode.ASSIGNOP);
            expression();
          } else if (_lookahead == TokenCode.INCDECOP) {
            match(TokenCode.INCDECOP);
          } else {
            setError(_token, "^ Invalid statement");
            recoverError(Follows.VariableList);
          }
        }
        break;
      case RETURN:
        match(TokenCode.RETURN);
        optionalExpression();
        break;
      case BREAK:
        match(TokenCode.BREAK);
        break;
      case CONTINUE:
        match(TokenCode.CONTINUE);
        break;
      default:
        setError(_token, "^ Invalid statement");
        recoverError(Follows.VariableList);
    }
  }

  private void optionalExpression() { // Epsilon
    if (isExpression()) {
      expression();
    }
  }

  private void statementBlock() {
    if (!match(TokenCode.LBRACE)) {
      recoverError(Follows.Statement);
    }
    statementList();
    if (!match(TokenCode.RBRACE)) {
      recoverError(Follows.StatementBlock);
    }
  }

  private void incrDecrVar() {
    variableLoc();
    if (!match(TokenCode.INCDECOP)) {
      recoverError(Follows.Parameters);
    }
  }

  private void optionalElse() { // Epsilon
    if (_lookahead == TokenCode.ELSE) {
      match(TokenCode.ELSE);
      statementBlock();
    }
  }

  private void expressionList() { // Epsilon
    if (isExpression()) {
      expression();
      moreExpressions();
    }
  }

  private void parenthesizedExpressionList() {
    match(TokenCode.LPAREN); // no need for error recovery or check
    expressionList();
    if (!match(TokenCode.RPAREN)) {
      recoverError(Follows.ParenthesizedExpressionList);
    }
  }

  private void moreExpressions() { // Epsilon
    if (_lookahead == TokenCode.COMMA) {
      match(TokenCode.COMMA);
      expressionList();
    }
  }

  private void expression() {
    simpleExpression();
    expression2();
  }

  private void parenthesizedExpression() {
    if (!match(TokenCode.LPAREN)) {
      recoverExpression();
    }
    expression();
    if (!match(TokenCode.RPAREN)) {
      recoverError(Follows.ParenthesizedExpression);
    }
  }

  private void expression2() { // Epsilon
    if (_lookahead == TokenCode.RELOP) {
      match(TokenCode.RELOP);
      simpleExpression();
    }
  }

  private void simpleExpression() {
    if (_opType == OpType.PLUS) {
      match(OpType.PLUS);
    } else if (_opType == OpType.MINUS) {
      match(OpType.MINUS);
    }
    term();
    simpleExpression2();
  }

  private void simpleExpression2() { // Epsilon
    if (_lookahead == TokenCode.ADDOP) {
      match(TokenCode.ADDOP);
      term();
      simpleExpression2();
    }
  }

  private void term() {
    factor();
    term2();
  }

  private void term2() { // Epsilon
    if (_lookahead == TokenCode.MULOP) {
      match(TokenCode.MULOP);
      term();
    }
  }

  private void factor() {
    switch (_lookahead) {
      case NOT:
        match(TokenCode.NOT);
        factor();
        break;
      case LPAREN:
        parenthesizedExpression();
        break;
      case NUMBER:
        match(TokenCode.NUMBER);
        break;
      case IDENTIFIER:
      case ERR_LONG_ID:
        match(TokenCode.IDENTIFIER);
        if (_lookahead == TokenCode.LPAREN) {
          parenthesizedExpressionList();
        } else {
          variableLoc2();
        }
        break;
      default:
        setError(_token, "^ Expected an expression");
        recoverError(Follows.Expression);
        break;
    }
  }

  private void variableLoc() {
    if (!match(TokenCode.IDENTIFIER)) {
      recoverError(Firsts.VariableLoc2);
    }
    variableLoc2();
  }

  private void variableLoc2() { // Epsilon
    if (_lookahead == TokenCode.LBRACKET) {
      match(TokenCode.LBRACKET);
      expression();
      if (!match(TokenCode.RBRACKET)) {
        recoverError(Follows.VariableLoc2);
      }
    }
  }

  /*
   * Lookahead checks
   */
  private boolean isType() {
    return Validations.Types.contains(_lookahead);
  }

  private boolean isExpression() {
    return Validations.Expression.contains(_lookahead);
  }

  private boolean isStatementList() {
    return Validations.StatementList.contains(_lookahead);
  }
}
// UuuuuuuuuuuNAaaaaaaaaaaaaaaaaCcEeeePTABLEeeeeeeeeeee
