class Token {
    private TokenCode _code;
    private Object _symbolTableEntry;
    private DataType _dataType;
    private OpType _opType; 

    public Token(TokenCode code) {
        _code = code;
    }

    public Token(TokenCode code, OpType otype) {
        _code = code;
        _opType = otype;
    }

    public Token(TokenCode code, DataType dtype, Object entry) {
        _code = code;
        _dataType = dtype;
        _symbolTableEntry = entry;
    }

    public TokenCode getTokenCode() {
        return _code;
    }

    public Object getSymbolTableEntry() {
        return _symbolTableEntry;
    }

    public OpType getOperation() {
        return _opType;
    }
}
