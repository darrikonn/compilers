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
  private CodeGenerator _codeGen;

  private HashMap<Integer, ArrayList<ErrorObject>> _errorMap;
  private String _filename;

  private Integer _tempCnt;
  private Integer _labelCnt;
  private boolean _isMainDeclared;

  private SymbolTable _symTable;

  public Parser(Lexer lexer, String filename) throws IOException {
    _filename = filename;
    _lexer = lexer;
    _token = lexer.yylex();
    _tmp = _token;
    _prevText = "";
    _lookahead = _token.getTokenCode();
    _opType = _token.getOpType();
    _errorMap = new HashMap<Integer, ArrayList<ErrorObject>>();

    _codeGen = new CodeGenerator();
    _tempCnt = 1;
    _labelCnt = 1;

    SymbolTable.insert("0", Type.INT);
    SymbolTable.insert("1", Type.INT);
    SymbolTable.insert("main");
    SymbolTable.insert("write");
    SymbolTable.insert("writeln");
    _isMainDeclared = false;
  }

  public void parse() {
    program();
    getErrors();
  }

  public SymbolTableEntry newTemp() {
    // add new temporary variable name to the symbol table
    String tmp = "t" + _tempCnt++;
    SymbolTableEntry entry = SymbolTable.insert(tmp);

    // generate the TAC code for this temp variable
    _codeGen.generate(TacCode.VAR, null, null, entry);
    return entry;
  }

  public SymbolTableEntry newLabel() {
    // generates next label and inserts into the symbol table
    String tmp = "lab" + _labelCnt++;
    SymbolTableEntry entry = SymbolTable.insert(tmp);
    
    // generate code for the label
    //_codeGen.generate(TacCode.LABEL, null, null, entry);

    // keep track of two symbol-tables, one for global scope and one for local
    // when looking up an entry in symbol table, first check local-scope and then (if not found)
    // check the global-scope symbol table
    return entry;
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
    while (!First.Expression.contains(_lookahead) && !First.OpExpression.contains(_opType) && 
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
    if (_errorMap.size() == 0 && _isMainDeclared) {
      _codeGen.print();
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
        if (!_isMainDeclared) {
          System.out.println("Error: no `main` function found!");
          cnt++;
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
      recoverError(First.VariableMethodDeclarations);
    }

    variableDeclarations(true);
    _codeGen.generate(TacCode.GOTO, null, null, SymbolTable.lookup("main"));// TODO: put into the beginning
    methodDeclarations();
    match(TokenCode.RBRACE);
  }

  private void variableDeclarations(boolean outSideOfScope) { // Epsilon
    // TODO:


    if (isType()) {
      Type type = type();
      variableList(type);
      if (!match(TokenCode.SEMICOLON)) {
        recoverError(First.VariableDeclarationsStatementList);
      }
      variableDeclarations(outSideOfScope);
    } else if (outSideOfScope && 
        (_lookahead == TokenCode.IDENTIFIER || _lookahead == TokenCode.ERR_LONG_ID)) {
      setError(_token, "^ None such type");
      recoverError(Follow.NoneSuchType);
      variableDeclarations(outSideOfScope);
    } else if (!Follow.VariableDeclaration.contains(_lookahead)) {
      setError(_token, "^ Expected a type");
      recoverError(Follow.VariableDeclaration);
    }
  }

  private Type type() { // don't need error recovery because isType is always called
    if (_lookahead == TokenCode.INT) {
      match(TokenCode.INT);
      return Type.INT;
    } else if (_lookahead == TokenCode.REAL) {
      match(TokenCode.REAL);
      return Type.REAL;
    }
    return null;
  }

  private void variableList(Type type) {
    variable(type);
    variableList2(type);
  }

  private void variableList2(Type type) { // Epsilon
    if (_lookahead == TokenCode.COMMA) {
      match(TokenCode.COMMA);
      variableList(type);
    } else if (!Follow.VariableList.contains(_lookahead)) {
      setError(_token, "^ Expected a comma");
      recoverError(Follow.VariableList);
    }
  }

  private void variable(Type type) {
    SymbolTableEntry entry = _token.getSymbolTableEntry();
    _codeGen.generate(TacCode.VAR, null, null, entry);

    if (_lookahead == TokenCode.IDENTIFIER || _lookahead == TokenCode.ERR_LONG_ID) {
      entry.setType(type);
      match(TokenCode.IDENTIFIER);
      variable2();
    } else {
      setError(_token, "^ Expected an identifier");
      recoverError(Follow.Variable);
    }
  }

  private void variable2() { // Epsilon
    if (_lookahead == TokenCode.LBRACKET) {
      if (!match(TokenCode.LBRACKET) || !match(TokenCode.NUMBER) || !match(TokenCode.RBRACKET)) {
        recoverError(Follow.Variable);
      }
    }
  }

  private void methodDeclarations() {
    // TODO: give code generator a list of formal parameters, pass as an array (list)
    // code gen generates FPARAM entries

    if (_lookahead == TokenCode.STATIC) {
      methodDeclaration();
      moreMethodDeclarations();
    } else {
      setError(_token, "^ Expected 'static'");
      recoverError(Follow.MethodDeclarations);
    }
  }

  private void moreMethodDeclarations() { // Epsilon
    if (_lookahead == TokenCode.STATIC) {
      methodDeclarations();
    }
  }

  private void methodDeclaration() {
    // TODO: create a local-scope symbol table that is empty

    SymbolTable.resetLocalTable();

    match(TokenCode.STATIC); // always checked if static, no need for recovery
    Type returnType = methodReturnType();

    SymbolTable.endGlobal(); // end global declaration
    
    ////  For each function (main included), you need to construct a label (TacCode.LABEL) with the result
    // eld holding the name of the function.  At the end of a function you need to generate a return statement
    // (TacCode.  RETURN)

    SymbolTableEntry entry = SymbolTable.lookup(_lexer.yytext());
    entry.setType(returnType);
    _codeGen.generate(TacCode.LABEL, null, null, entry);

    if (_lexer.yytext().equals("main")) {
      if (_isMainDeclared) {
        setError(_token, "^ the main function has already been declared, you IDIOT!");
      } else {
        _isMainDeclared = true;
      }
    } else if (SymbolTable.lookup(_lexer.yytext()) != null) {
      setError(_token, "^ this function has already been declared");
    }

    if (!match(TokenCode.IDENTIFIER) || !match(TokenCode.LPAREN)) {
      recoverError(First.Parameters);
    }
    parameters();
    if (!match(TokenCode.RPAREN) || !match(TokenCode.LBRACE)) {
      recoverError(First.VariableDeclarationsStatementList);
    }
    variableDeclarations(false);
    statementList();

    _codeGen.generate(TacCode.RETURN, null, null, null);

    SymbolTable.startGlobal(); // start global declaration

    if (!match(TokenCode.RBRACE)) {
      recoverError(Follow.MethodDeclarations);
    }
  }

  private Type methodReturnType() {
    if (isType()) {
      return type();
    } else if (_lookahead == TokenCode.VOID) {
      match(TokenCode.VOID);
      return Type.VOID;
    } else {
      setError(_token, "^ Expected a method return type");
      recoverError(Follow.MethodReturnType);
      return null;
    }
  }

  private void parameters() { // Epsilon
    if (_lookahead != TokenCode.RPAREN) {
      parameterList();
    }
  }

  private void parameterList() {
    if (isType()) {
      Type type = type();

      _codeGen.generate(TacCode.FPARAM, null, null, _token.getSymbolTableEntry());

      _token.getSymbolTableEntry().setType(type);
      if (!match(TokenCode.IDENTIFIER)) {
        recoverError(First.ParameterList2);
      }
    } else {
      setError(_token, "^ Expected a type");
      recoverError(Follow.ParameterList);
    }
    parameterList2();
  }

  private void parameterList2() { // Epsilon
    if (_lookahead == TokenCode.COMMA) {
      match(TokenCode.COMMA);
      parameterList();
    } else if (!Follow.Parameters.contains(_lookahead)) {
      setError(_token, "^ Expected a comma");
      recoverError(Follow.Parameters);
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
        SymbolTableEntry ifLabel = newLabel();
        match(TokenCode.IF);
        SymbolTableEntry entry = parenthesizedExpression(ifLabel);
        statementBlock();
        SymbolTableEntry elseLabel = optionalElse(ifLabel);
        if (elseLabel != null) {
          _codeGen.generate(TacCode.LABEL, null, null, elseLabel);
        }
        break;
      case FOR:
        if (!match(TokenCode.FOR) || !match(TokenCode.LPAREN)) {
          recoverError(Follow.MethodReturnType);
        }
        variableLoc();
        if (!match(TokenCode.ASSIGNOP)) {
          recoverExpression();
        }
        expression(null);
        if (!match(TokenCode.SEMICOLON)) {
          recoverExpression();
        }
        expression(null);
        if (!match(TokenCode.SEMICOLON)) {
          recoverError(Follow.MethodReturnType);
        }
        incrDecrVar();
        if (!match(TokenCode.RPAREN)) {
          recoverError(First.StatementBlock);
        }
        statementBlock();
        break;
      case LBRACE:
        statementBlock();
        break;
      default:
        statement2();
        if (!match(TokenCode.SEMICOLON)) {
          recoverError(Follow.Statement);
        }
        break;
    }
  }

  private void statement2() {
    switch (_lookahead) {
      case IDENTIFIER:
      case ERR_LONG_ID:

        SymbolTableEntry entry = _token.getSymbolTableEntry();


        match(TokenCode.IDENTIFIER);
        if (_lookahead == TokenCode.IDENTIFIER) {
          setError(_tmp, "^ None such type");
          recoverError(Follow.VariableList);
        } else if (_lookahead == TokenCode.LPAREN) {
          parenthesizedExpressionList();
          _codeGen.generate(TacCode.CALL, entry, null, null);
        } else {
          variableLoc2(); // no array indexing
          if (_lookahead == TokenCode.ASSIGNOP) {
            Type type = entry.getType();
            if (type == null) {
              setError(_tmp, "^ `" + _prevText + "` has not been declared");
            }
            match(TokenCode.ASSIGNOP);
            SymbolTableEntry entry2 = expression(null);

            if (type != null && type != entry2.getType()) {
              setError(_tmp, type.toString());
            }

            _codeGen.generate(TacCode.ASSIGN, entry2, null, entry);
          } else if (_lookahead == TokenCode.INCDECOP) {
            match(TokenCode.INCDECOP);
          } else {
            setError(_token, "^ Invalid statement");
            recoverError(Follow.VariableList);
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
        recoverError(Follow.VariableList);
    }
  }

  private SymbolTableEntry optionalExpression() { // Epsilon
    if (isExpression()) {
      return expression(null);
    }

    return null;
  }

  private void statementBlock() {
    if (!match(TokenCode.LBRACE)) {
      recoverError(Follow.Statement);
    }
    statementList();
    if (!match(TokenCode.RBRACE)) {
      recoverError(Follow.StatementBlock);
    }
  }

  private SymbolTableEntry incrDecrVar() {
    SymbolTableEntry entry = variableLoc();
    SymbolTableEntry tmp = newTemp();
    _codeGen.generate(_token.getOpType().map(), entry, entry, tmp);
    if (!match(TokenCode.INCDECOP)) {
      recoverError(Follow.Parameters);
    }

    return tmp;
  }

  private SymbolTableEntry optionalElse(SymbolTableEntry ifLabel) { // Epsilon
    if (_lookahead == TokenCode.ELSE) {
      SymbolTableEntry lbl = newLabel();
      _codeGen.generate(TacCode.GOTO, null, null, lbl);
      _codeGen.generate(TacCode.LABEL, null, null, ifLabel);
      match(TokenCode.ELSE);
      statementBlock();

      return lbl;
    }
    _codeGen.generate(TacCode.LABEL, null, null, ifLabel);

    return null;
  }

  private void expressionList() { // Epsilon
    if (isExpression()) {
      _codeGen.generate(TacCode.APARAM, null, null, expression(null));
      moreExpressions();
    }
  }

  private void parenthesizedExpressionList() {
    match(TokenCode.LPAREN); // no need for error recovery or check
    expressionList();
    if (!match(TokenCode.RPAREN)) {
      recoverError(Follow.ParenthesizedExpressionList);
    }
  }

  private void moreExpressions() { // Epsilon
    if (_lookahead == TokenCode.COMMA) {
      match(TokenCode.COMMA);
      expressionList();
    }
  }

  private SymbolTableEntry expression(SymbolTableEntry label) {
    // TODO: return a symboltableentry that is an identifier, constant or a temporary name (id = E)
    

    SymbolTableEntry entry = simpleExpression();
    expression2(entry, label);

    return entry;
  }

  private SymbolTableEntry parenthesizedExpression(SymbolTableEntry label) {
    if (!match(TokenCode.LPAREN)) {
      recoverExpression();
    }
    SymbolTableEntry entry = expression(label);
    if (!match(TokenCode.RPAREN)) {
      recoverError(Follow.ParenthesizedExpression);
    }

    return entry;
  }

  private void expression2(SymbolTableEntry entry, SymbolTableEntry label) { // Epsilon
    if (_lookahead == TokenCode.RELOP) {
      TacCode tc = _token.getOpType().map();
      match(TokenCode.RELOP);
      SymbolTableEntry sexprEntry = simpleExpression();
      _codeGen.generate(tc, entry, sexprEntry, label);
      // check if real comparing int
    }
  }

  private SymbolTableEntry simpleExpression() {
    // TODO: return a symboltableentry that is an identifier, constant or a temporary name (id = E)
    
    SymbolTableEntry entry = null;

    if (_opType == OpType.PLUS) {
      match(OpType.PLUS);
    } else if (_opType == OpType.MINUS) {
      // uminus
      match(OpType.MINUS);
      entry = newTemp();
    }

    SymbolTableEntry termEntry = term();
    if (entry != null) {
      _codeGen.generate(TacCode.UMINUS, termEntry, null, entry);
      entry.setType(termEntry.getType());
    } else {
      entry = termEntry;
    }

    entry = simpleExpression2(entry);

    return entry;
  }

  private SymbolTableEntry simpleExpression2(SymbolTableEntry entry) { // Epsilon
    if (_lookahead == TokenCode.ADDOP) {
      TacCode tc = _token.getOpType().map();
      match(TokenCode.ADDOP);
      SymbolTableEntry tmp = newTemp();
      SymbolTableEntry termEntry = term();
      _codeGen.generate(tc, entry, termEntry, tmp);
      tmp.setType(termEntry.getType());
      return simpleExpression2(tmp);
    }

    return entry;
  }

  private SymbolTableEntry term() {
    

    SymbolTableEntry factorEntry = factor();
    
    SymbolTableEntry termEntry = term2();
    if (termEntry != null) {
      SymbolTableEntry tmp = newTemp();
      _codeGen.generate(TacCode.MULT, factorEntry, termEntry, tmp);
      tmp.setType(factorEntry.getType() == Type.REAL || termEntry.getType() == Type.REAL 
          ? Type.REAL 
          : Type.INT);
      return tmp;
    }

    return factorEntry;
  }

  private SymbolTableEntry term2() { // Epsilon
    if (_lookahead == TokenCode.MULOP) {
      match(TokenCode.MULOP);
      return term();
    }

    return null;
  }

  private SymbolTableEntry factor() {
    // TODO: return a symboltableentry that is an identifier, constant or a temporary name (id = E)
    
    SymbolTableEntry entry;

    switch (_lookahead) {
      case NOT:
        match(TokenCode.NOT);
        entry = newTemp();
        _codeGen.generate(TacCode.NOT, factor(), null, entry);
        return entry;
      case LPAREN:
        return parenthesizedExpression(null);
      case NUMBER:
        entry = _token.getSymbolTableEntry();
        match(TokenCode.NUMBER);
        return entry;
      case IDENTIFIER:
      case ERR_LONG_ID:
        entry = _token.getSymbolTableEntry();
        match(TokenCode.IDENTIFIER);
        if (_lookahead == TokenCode.LPAREN) {

          parenthesizedExpressionList();
          _codeGen.generate(TacCode.CALL, entry, null, null);
        } else {
          variableLoc2(); // no array indexing
        }
        return entry;
      default:
        setError(_token, "^ Expected an expression");
        recoverError(Follow.Expression);
        return null;
    }
  }

  private SymbolTableEntry variableLoc() {
    SymbolTableEntry entry = _token.getSymbolTableEntry();
    
    if (!match(TokenCode.IDENTIFIER)) {
      recoverError(First.VariableLoc2);
    }
    variableLoc2(); // no array indexing

    return entry;
  }

  private void variableLoc2() { // Epsilon
    if (_lookahead == TokenCode.LBRACKET) {
      match(TokenCode.LBRACKET);
      expression(null);
      if (!match(TokenCode.RBRACKET)) {
        recoverError(Follow.VariableLoc2);
      }
    }
  }

  /*
   * Lookahead checks
   */
  private boolean isType() {
    return Validation.Types.contains(_lookahead);
  }

  private boolean isExpression() {
    return Validation.Expression.contains(_lookahead);
  }

  private boolean isStatementList() {
    return Validation.StatementList.contains(_lookahead);
  }
}
// UuuuuuuuuuuNAaaaaaaaaaaaaaaaaCcEeeePTABLEeeeeeeeeeee
