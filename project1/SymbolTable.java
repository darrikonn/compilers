import java.util.ArrayList;

class SymbolTable {
    private ArrayList<SymbolTableEntry> _symbolTableEntries;

    public SymbolTable() {
        _symbolTableEntries = new ArrayList<SymbolTableEntry>();
    }

    public SymbolTableEntry findSymbolTableEntry(Object lexeme) {
        for (SymbolTableEntry s : _symbolTableEntries) {
            if (s.getLexeme().equals(lexeme)) {
                return s;
            }
        }

        return null;
    }

    public SymbolTableEntry addSymbolTableEntry(Object lexeme) {
        SymbolTableEntry entry = new SymbolTableEntry(lexeme);
        _symbolTableEntries.add(entry);

        return entry;
    }
    
    public ArrayList<SymbolTableEntry> getSymbolTableEntries() {
        return _symbolTableEntries;
    }
}
