public class SymbolTableEntry {
    private Object _lexeme;

    public SymbolTableEntry(Object lexeme) {
        _lexeme = lexeme;
    }

    public Object getLexeme() {
        return _lexeme;
    }
}
