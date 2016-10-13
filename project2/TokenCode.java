public enum TokenCode {
    IDENTIFIER, NUMBER, INCDECOP, RELOP, MULOP, ADDOP, ASSIGNOP,
    CLASS, STATIC, VOID, IF, ELSE, FOR, RETURN, BREAK, CONTINUE, 
    LBRACE, RBRACE, LBRACKET, RBRACKET, LPAREN, RPAREN,
    SEMICOLON, COMMA, NOT, INT, REAL,
    EOF, ERR_ILL_CHAR, ERR_LONG_ID;

    @Override
    public String toString() {
        switch(this) {
            case CLASS: return "class";
            case STATIC: return "static";
            case VOID: return "void";
            case IF: return "if";
            case ELSE: return "else";
            case FOR: return "for";
            case RETURN: return "return";
            case BREAK: return "break";
            case CONTINUE: return "continue";
            case LBRACE: return "{";
            case RBRACE: return "}";
            case LBRACKET: return "[";
            case RBRACKET: return "]";
            case LPAREN: return "(";
            case RPAREN: return ")";
            case SEMICOLON: return ";";
            case COMMA: return ",";
            case NOT: return "!";
            case INT: return "int";
            case REAL: return "real";
            default: return "";
        }
    }
}
