import java.util.LinkedHashSet;

class SymbolTable {
    private LinkedHashSet<Object> _symbolTableEntry;
    public SymbolTable() {
        _symbolTableEntry = new LinkedHashSet<Object>();
    }

    public Object findSymbolTableEntry(Object key) {
        return _symbolTableEntry.contains(key) ? key : null;
    }

    public void addSymbolTableEntry(Object key) {
        _symbolTableEntry.add(key);
    }
    
    public LinkedHashSet<Object> getSymbolTableEntries() {
        return _symbolTableEntry;
    }
}
