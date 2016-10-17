public enum TokenCode {
  IDENTIFIER, NUMBER, INCDECOP, RELOP, MULOP, ADDOP, ASSIGNOP,
  CLASS, STATIC, VOID, IF, ELSE, FOR, RETURN, BREAK, CONTINUE, 
  LBRACE, RBRACE, LBRACKET, RBRACKET, LPAREN, RPAREN,
  SEMICOLON, COMMA, NOT, INT, REAL,
  EOF, ERR_ILL_CHAR, ERR_LONG_ID;

  @Override
  public String toString() {
    switch(this) {
      case IDENTIFIER: return "^ Expected an identifier";
      case NUMBER: return "^ Expected a number";
      case INCDECOP: return "^ Expected an increment or a decrement operator";
      case RELOP: return "^ Expected a relop";
      case MULOP: return "^ Expected a mulop";
      case ADDOP: return "^ Expected an addop";
      case ASSIGNOP: return "^ Expected an assign operator";
      case CLASS: return "^ Expected 'class'";
      case STATIC: return "^ Expected 'static'";
      case VOID: return "^ Expected 'void'";
      case IF: return "^ Expected 'if'";
      case ELSE: return "^ Expected 'else'";
      case FOR: return "^ Expected 'for'";
      case RETURN: return "^ Expected 'return'";
      case BREAK: return "^ Expected 'break'";
      case CONTINUE: return "^ Expected 'continue'";
      case LBRACE: return "^ Expected '{'";
      case RBRACE: return "^ Expected '}'";
      case LBRACKET: return "^ Expected '['";
      case RBRACKET: return "^ Expected ']'";
      case LPAREN: return "^ Expected '('";
      case RPAREN: return "^ Expected ')'";
      case SEMICOLON: return "^ Expected ';'";
      case COMMA: return "^ Expected ','";
      case NOT: return "^ Expected '!'";
      case INT: return "^ Expected 'int'";
      case REAL: return "^ Expected 'real'";
      case EOF: return "End of file";
      case ERR_ILL_CHAR: return "^ Error illegal character";
      case ERR_LONG_ID: return "^ Error identifier too long";
      default: return "Expected something amazing";
    }
  }
}
