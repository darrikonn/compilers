class Token {
    private TokenCode _code;
    private SymbolTableEntry _symbolTableEntry;
    private DataType _dataType;
    private OpType _opType; 

    // key words
    public Token(TokenCode code) {
        _code = code;
    }

    // operators
    public Token(TokenCode code, OpType otype) {
        _code = code;
        _opType = otype;
    }

    // symbol table entries
    public Token(TokenCode code, DataType dtype, SymbolTableEntry entry) {
        _code = code;
        _dataType = dtype;
        _symbolTableEntry = entry;
    }

    public TokenCode getTokenCode() {
        return _code;
    }

    public SymbolTableEntry getSymbolTableEntry() {
        return _symbolTableEntry;
    }

    public OpType getOperation() {
        return _opType;
    }
}
