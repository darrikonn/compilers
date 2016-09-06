class Token {
    private TokenCode _code;
    private SymbolTableEntry _symbolTableEntry;
    private DataType _dataType;
    private OpType _opType; 

    // non symbol table entries
    public Token(TokenCode code, OpType otype, DataType dtype) {
        _code = code;
        _opType = otype;
        _dataType = dtype;
    }

    // symbol table entries (number|id)
    public Token(TokenCode code, OpType otype, DataType dtype, SymbolTableEntry entry) {
        _code = code;
        _opType = otype;
        _dataType = dtype;
        _symbolTableEntry = entry;
    }

    public TokenCode getTokenCode() {
        return _code;
    }

    public DataType getDataType() {
        return _dataType;
    }

    public OpType getOpType() {
        return _opType;
    }

    public SymbolTableEntry getSymbolTableEntry() {
        return _symbolTableEntry;
    }
}
