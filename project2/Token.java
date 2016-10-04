class Token {
    private TokenCode _code;
    private SymbolTableEntry _symbolTableEntry;
    private DataType _dataType;
    private OpType _opType; 
    private int _lineNumber;
    private int _columnNumber;

    // non symbol table entries
    public Token(TokenCode code, OpType otype, DataType dtype, int line, int column) {
        _code = code;
        _opType = otype;
        _dataType = dtype;
        _lineNumber = line;
        _columnNumber = column;
    }

    // symbol table entries (number|id)
    public Token(TokenCode code, OpType otype, DataType dtype, 
            SymbolTableEntry entry, int line, int column) {
        _code = code;
        _opType = otype;
        _dataType = dtype;
        _symbolTableEntry = entry;
        _lineNumber = line;
        _columnNumber = column;
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

    public int getLineNumber() {
        return _lineNumber;
    }

    public int getColumnNumber() {
        return _columnNumber;
    }
}
